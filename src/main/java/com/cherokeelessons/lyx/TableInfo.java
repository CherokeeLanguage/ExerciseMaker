package com.cherokeelessons.lyx;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;

import org.jsoup.nodes.Element;

/**
 * This class provides support for converting HTML tables into <TEX
 * txt="\LaTeX{}">LaTeX</TEX> tables. Some of the things <b>NOT</b> implemented
 * include the following:
 * <ul>
 * <li>valign attributes are not processed, but align= is.
 * <li>rowspan attributes are not processed, but colspan= is.
 * <li>the argument to border= in the table tag is not used to control line size
 * </ul>
 * <br>
 * Here is an example table.
 * <p>
 * <table border bgcolor="#DDDDDD">
 * <tr>
 * <th>Column 1 Heading
 * <th>Column two heading
 * <th>Column three heading
 * <tr>
 * <td>data
 * <td colspan=2>Span two columns
 * <tr>
 * <td><i>more data</i>
 * <td align=right>right
 * <td align=left>left
 * <tr>
 * <td colspan=3>
 * <table border=5 bgcolor="#EEEEEE">
 * <tr>
 * <th colspan=3>A nested table example
 * <tr>
 * <th>Column one Heading</th>
 * <th>Column two heading</th>
 * <th>Column three heading</th>
 * <tr>
 * <td>data</td>
 * <td colspan=2>Span two columns</td>
 * <tr>
 * <td><i>more data</i></td>
 * <td align=right>right</td>
 * <td align=left>left</td>
 * <tr>
 * <td>
 * 
 * <pre>
 *  1
 *   2
 *  3
 *    4
 * </pre>
 * 
 * </td>
 * <td>
 * 
 * <pre>
 *  first line
 *  second line
 *  third line
 *  fourth line
 * </pre>
 * 
 * </td>
 * </table>
 * </table>
 * 
 * @version $Revision: 1.2 $
 * @author Gregg Wonderly - C2 Technologies Inc.
 */
public class TableInfo {

	private StringBuilder originalLatex;
	private StringBuilder latex;

	private int colcnt = 0;
	private int rowcnt = 0;
	private int totalcolcnt = 0;
	private boolean border = false;
	private int bordwid;
	private boolean parboxed;
	private double red = -1.0;
	private double blue = -1.0;
	private double green = -1.0;
	private static int tblcnt;
	private int tblno;
	private String tc;

	int hasNumAttr(HTML.Attribute attr, MutableAttributeSet attrSet) {
		String val = (String) attrSet.getAttribute(attr);
		if (val == null) {
			return -1;
		}
		try {
			return Integer.parseInt(val);
		} catch (Exception ex) {
			return -1;
		}
	}

	/**
	 * Constructs a new table object and starts processing of the table by
	 * scanning the <code>&lt;table&gt;</code> passed to count columns.
	 * 
	 * //@param p // properties found on the <code>&lt;table&gt;</code> tag
	 * //@param ret // the result buffer that will contain the output //@param
	 * table // the input string that has the entire table definition in it.
	 * //@param off // the offset into <code>&lt;table&gt;</code> where scanning
	 * // should start
	 */
	public StringBuilder startTable(StringBuilder org, Element tableElement) {
		originalLatex = org;
		latex = new StringBuilder();

		tblno = tblcnt++;
		tc = "" + (char) ('a' + (tblno / (26 * 26)))
				+ (char) ((tblno / 26) + 'a') + (char) ((tblno % 26) + 'a');

		String val = tableElement.attr("border");// (String) attrSet.getAttribute(HTML.Attribute.BORDER);
		border = false;
		if (val != null) {
			border = true;
			bordwid = 2;
			if (val.equals("") == false) {
				try {
					bordwid = Integer.parseInt(val);
				} catch (Exception ex) {
				}
				if (bordwid == 0) {
					border = false;
				}
			}
		}
		String bgcolor = tableElement.attr("bgcolor");//(String) attrSet.getAttribute(HTML.Attribute.BGCOLOR);
		if (bgcolor != null) {
			try {
				if (bgcolor.length() != 7 && bgcolor.charAt(0) == '#') {
					throw new NumberFormatException();
				}
				red = Integer.decode("#" + bgcolor.substring(1, 3))
						.doubleValue();
				blue = Integer.decode("#" + bgcolor.substring(3, 5))
						.doubleValue();
				green = Integer.decode("#" + bgcolor.substring(5, 7))
						.doubleValue();
				red /= 255.0;
				blue /= 255.0;
				green /= 255.0;
			} catch (NumberFormatException e) {
				red = 1.0;
				blue = 1.0;
				green = 1.0;
			}
		}

		return latex;
	}

	/**
	 * Ends the table, closing the last row as needed
	 * 
	 */
	public StringBuilder endTable() {
		originalLatex.append("\n% Table #" + tblno + "\n\\begin{center}\n");
		int col = totalcolcnt;
		if (col == 0) {
			col = 1;
		}
		for (int i = 0; i < col; ++i) {
			String cc = "" + (char) ('a' + (i / (26 * 26)))
					+ (char) ((i / 26) + 'a') + (char) ((i % 26) + 'a');
			originalLatex.append("\\newlength{\\tbl" + tc + "c" + cc + "w}\n");
			// originalBuffer.append("\\setlength{\\tbl"+tc+"c"+cc+"w}{"+(1.0/col)+"\\hsize}\n");
			originalLatex.append("\\setlength{\\tbl" + tc + "c" + cc + "w}{"
					+ (Constants.tableWidthScale / col) + "\\linewidth}\n");
		}
		if (red != -1.0 && green != -1.0 && blue != -1.0) {
			originalLatex.append("\\colorbox[rgb]{" + Double.toString(red)
					+ "," + Double.toString(blue) + ","
					+ Double.toString(green) + "}{");
		}
		originalLatex.append("\\begin{tabular}{");
		if (border) {
			originalLatex.append("|");
		}
		for (int i = 0; i < col; ++i) {
			String cc = "" + (char) ('a' + (i / (26 * 26)))
					+ (char) ((i / 26) + 'a') + (char) ((i % 26) + 'a');
			originalLatex.append("p{\\tbl" + tc + "c" + cc + "w}");
			if (border) {
				originalLatex.append("|");
			}
		}
		originalLatex.append("}\n");

		// Append the cached table
		originalLatex.append(latex);

		originalLatex.append("\\end{tabular}\n");
		if (red != -1.0 && green != -1.0 && blue != -1.0) {
			originalLatex.append("}\n");
		}

		originalLatex.append("\\end{center}\n");
		return originalLatex;
	}

	/**
	 * Starts a new column, possibly closing the current column if needed
	 * 
	 * //@param ret The output buffer to put <TEX txt="\LaTeXe{}">LaTeX2e</TEX>
	 * into. //@param p the properties from the <code>&lt;td&gt;</code> tag
	 */
	public void startCol(Element enode) {
		int span;
		try {
			span = Integer.valueOf(enode.attr("colspan"));
		} catch (NumberFormatException e) {
			span = 0;
		}// hasNumAttr(HTML.Attribute.COLSPAN, enode);
		if (colcnt > 0) {
			latex.append(" & ");
		}
		String align = enode.attr("align");//(String) enode.getAttribute(HTML.Attribute.ALIGN);
		if (align != null && span < 0) {
			span = 1;
		}
		if (span > 0) {
			latex.append("\\multicolumn{" + span + "}{");
			if (border && colcnt == 0) {
				latex.append("|");
			}
			String cc = "" + (char) ('a' + (colcnt / (26 * 26)))
					+ (char) ((colcnt / 26) + 'a')
					+ (char) ((colcnt % 26) + 'a');
			if (align != null) {
				String h = align.substring(0, 1);
				if ("rR".indexOf(h) >= 0) {
					latex.append("r");
				} else if ("lL".indexOf(h) >= 0) {
					latex.append("p{\\tbl" + tc + "c" + cc + "w}");
				} else if ("cC".indexOf(h) >= 0) {
					latex.append("p{\\tbl" + tc + "c" + cc + "w}");
				}
			} else {
				latex.append("p{\\tbl" + tc + "c" + cc + "w}");
			}
			if (border) {
				latex.append("|");
			}
			latex.append("}");
		}
		String wid = enode.attr("texwidth");//(String) enode.getAttribute("texwidth");
		latex.append("{");
		if (wid != null) {
			latex.append("\\parbox{" + wid + "}{\\vskip 1ex ");
			parboxed = true;
		}
		colcnt++;
		totalcolcnt = totalcolcnt > colcnt ? totalcolcnt : colcnt;
	}

	/**
	 * Starts a new Heading column, possibly closing the current column if
	 * needed. A Heading column has a Bold Face font directive around it.
	 * 
	 * //@param ret The output buffer to put <TEX txt="\LaTeXe{}">LaTeX2e</TEX>
	 * into. //@param p The properties from the <code>&lt;th&gt;</code> tag
	 */
	public void startHeadCol(Element enode) {
		startCol(enode);
		latex.append("\\bf ");
	}

	/**
	 * Ends the current column.
	 * 
	 * //@param ret The output buffer to put <TEX txt="\LaTeXe{}">LaTeX2e</TEX>
	 * into.
	 */
	public void endCol() {
		if (parboxed) {
			latex.append("\\vskip 1ex}");
		}
		parboxed = false;
		latex.append("}");
	}

	/**
	 * Starts a new row, possibly closing the current row if needed
	 * 
	 * //@param ret The output buffer to put <TEX txt="\LaTeX{}">LaTeX</TEX>
	 * into. //@param p The properties from the <code>&lt;tr&gt;</code> tag
	 */
	public void startRow(Element enode) {
		if (rowcnt == 0) {
			if (border) {
				latex.append(" \\hline ");
			}
		}
		colcnt = 0;
		++rowcnt;
	}

	/**
	 * Ends the current row.
	 * 
	 * // @param ret The output buffer to put <TEX txt="\LaTeXe{}">LaTeX2e</TEX>
	 * into.
	 */
	public void endRow() {
		latex.append(" \\\\");
		if (border) {
			latex.append(" \\hline");
		}
		latex.append("\n");
	}

}
