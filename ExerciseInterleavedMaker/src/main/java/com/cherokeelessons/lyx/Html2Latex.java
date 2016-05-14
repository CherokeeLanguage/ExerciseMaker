package com.cherokeelessons.lyx;

import java.awt.Color;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeVisitor;

/**
 * Adapted from:
 * {@link https://github.com/doclet/texdoclet/tree/master/src/main/java/org/stfm/texdoclet}
 * 
 * @author michael
 *
 */
public class Html2Latex implements NodeVisitor {

	private final String html;
	private StringBuilder latex = new StringBuilder();

	public Html2Latex(String html) {
		this.html = html;
	}

	public String getLatex() {
		latex.setLength(0);
		Document doc = Jsoup.parseBodyFragment(html, "https://www.newsrx.com/Butter/");
		doc.getElementsByTag("body").traverse(this);
		return latex.toString();
	}

	private int colIdx = 0;
	private Map<String, String> colors = new HashMap<String, String>(10);
	private String refurl = null;

	@Override
	public void head(Node node, int depth) {
		swtch: {
			if (node instanceof TextNode) {
				TextNode tnode = (TextNode) node;
				latexEscapeAppend(tnode.text());
				break swtch;
			}
			if (node instanceof Element) {
				Element enode = (Element) node;
				String tag = enode.tag().getName().toLowerCase();
				if (tag.equals("p")) {
					latex.append("\n\n");
					break swtch;
				}
				if (tag.equals("hr")) {
					String sz = enode.attr("size");
					int size = 1;
					if (sz != null) {
						size = Integer.parseInt(sz);
					}
					latex.append("\\mbox{}\\newline\\rule[2mm]{\\hsize}{" + (1 * size * .5) + "mm}\\newline\n");
					break swtch;
				}
				if (tag.equals("br")) {
					latex.append("\\mbox{}\\newline ");
					break swtch;
				}
				if (tag.equals("pre")) {
					latex.append(Constants.TELETYPE + "\\small\n\\mbox{}\\newline ");
					verbat++;
					break swtch;
				}
				if (tag.equals("h1")) {
					latex.append("\\chapter*{");
					break swtch;
				}
				if (tag.equals("h2")) {
					latex.append("\\section*{");
					break swtch;
				}
				if (tag.equals("h3")) {
					latex.append("\\subsection*{");
					break swtch;
				}
				if (tag.equals("h4")) {
					latex.append("\\subsubsection*{");
					break swtch;
				}
				if (tag.equals("h5")) {
					latex.append("\\subsubsection*{");
					break swtch;
				}
				if (tag.equals("h6")) {
					latex.append("\\subsubsection*{");
					break swtch;
				}
				if (tag.equals("sub")) {
					latex.append("%<sub>\n\\textsubscript{\n");
					break swtch;
				}
				if (tag.equals("sup")) {
					latex.append("%<sup>\n\\textsuperscript{\n");
					break swtch;
				}
				if (tag.equals("center")) {
					latex.append("\\makebox[\\hsize]{ ");
					break swtch;
				}
				if (tag.equals("code")) {
					latex.append(Constants.TELETYPE + "\\small ");
					break swtch;
				}
				if (tag.equals("tt")) {
					latex.append(Constants.TELETYPE + " ");
					break swtch;
				}
				if (tag.equals("p")) {
					latex.append("\n\n");
					break swtch;
				}
				if (tag.equals("b")) {
					latex.append("{\\bf ");
					break swtch;
				}
				if (tag.equals("strong")) {
					latex.append("{\\bf ");
					break swtch;
				}
				if (tag.equals("a")) {
					refurl = enode.attr("href");
					if (refurl != null) {
						if (Constants.hyperref) {
							String sharp = "";
							if (refurl.indexOf("#") >= 0) {
								sharp = refurl.substring(refurl.indexOf("#") + 1, refurl.length());
								if (sharp.indexOf("%") >= 0) {
									sharp = ""; // Don't know what to do with
												// '%'
								}
								refurl = refurl.substring(0, refurl.indexOf("#"));
							}
							latex.append("\\hyperref{" + refurl + "}{" + sharp + "}{}{");
							// latex.append("\\href{" + refurl + "}{");
						} else {
							latex.append("{\\bf ");
						}
					}
					break swtch;
				}
				if (tag.equals("ol")) {
					latex.append("\n\\begin{enumerate}");
					break swtch;
				}
				if (tag.equals("dl")) {
					latex.append("\n\\begin{itemize}");
					break swtch;
				}
				if (tag.equals("li")) {
					latex.append("\n\\item{\\vskip -.8ex ");
					break swtch;
				}
				if (tag.equals("dt")) {
					latex.append("\\item[");
					return;
				}
				if (tag.equals("dd")) {
					latex.append("{");
					break swtch;
				}
				if (tag.equals("ul")) {
					latex.append("\\begin{itemize}");
					break swtch;
				}
				if (tag.equals("i")) {
					latex.append(Constants.ITALIC + " ");
					break swtch;
				}
				if (tag.equals("em")) {
					latex.append(Constants.ITALIC + " ");
					break swtch;
				}
				if (tag.equals("table")) {
					tblstk.push(tblinfo);
					tblinfo = new TableInfo();
					latex = tblinfo.startTable(latex, enode);
					break swtch;
				}
				if (tag.equals("th")) {
					tblinfo.startHeadCol(enode);
					break swtch;
				}
				if (tag.equals("td")) {
					tblinfo.startCol(enode);
					break swtch;
				}
				if (tag.equals("tr")) {
					tblinfo.startRow(enode);
					break swtch;
				}
				if (tag.equals("font")) {
					String col = enode.attr("color");
					latex.append("{");
					if (col != null) {
						if ("redgreenbluewhiteyellowblackcyanmagenta".indexOf(col) != -1) {
							latex.append("\\color{" + col + "}");
						} else {
							if ("abcdefABCDEF0123456789".indexOf(col.charAt(0)) != -1) {
								Color cc = new Color((int) Long.parseLong(col, 16));
								String name = colors.get("color" + cc.getRGB());
								if (name == null) {
									latex.append("\\definecolor{color" + colIdx + "}[rgb]{" + (cc.getRed() / 255.0)
											+ "," + (cc.getBlue() / 255.0) + "," + (cc.getGreen() / 255.0) + "}");
									name = "color" + colIdx;
									colIdx++;
									colors.put("color" + cc.getRGB(), name);
								}
								latex.append("\\color{" + name + "}");
								++colIdx;
							}
						}
					}
					break swtch;
				}
			}
		}
	}

	@Override
	public void tail(Node node, int depth) {
		swtch: {
			if (node instanceof Element) {
				Element enode = (Element) node;
				String tag = enode.tag().getName().toLowerCase();

				if (tag.equals("pre")) {
					verbat--;
					latex.append("}\n");
					break swtch;
				}
				if (tag.equals("h1")) {
					latex.append("}");
					break swtch;
				}
				if (tag.equals("h2")) {
					latex.append("}");
					break swtch;
				}
				if (tag.equals("h3")) {
					latex.append("}");
					break swtch;
				}
				if (tag.equals("h4")) {
					latex.append("}");
					break swtch;
				}
				if (tag.equals("h5")) {
					latex.append("}");
					break swtch;
				}
				if (tag.equals("h6")) {
					latex.append("}");
					break swtch;
				}
				if (tag.equals("sub")) {
					latex.append("\n}%</sub>\n");
					break swtch;
				}
				if (tag.equals("sup")) {
					latex.append("\n}%</sup>\n");
					break swtch;
				}
				if (tag.equals("center")) {
					latex.append("}");
					break swtch;
				}
				if (tag.equals("code")) {
					latex.append("}");
					break swtch;
				}
				if (tag.equals("tt")) {
					latex.append("}");
					break swtch;
				}
				if (tag.equals("b")) {
					latex.append("}");
					break swtch;
				}
				if (tag.equals("strong")) {
					latex.append("}");
					break swtch;
				}
				if (tag.equals("a")) {
					if (refurl != null) {
						latex.append("}");
					}
					break swtch;
				}
				if (tag.equals("li")) {
					latex.append("}");
					break swtch;
				}
				if (tag.equals("dt")) {
					latex.append("]");
					break swtch;
				}
				if (tag.equals("dd")) {
					latex.append("}");
					break swtch;
				}
				if (tag.equals("dl")) {// /
					latex.append("\n\\end{itemize}\n");
					break swtch;
				}
				if (tag.equals("ol")) {
					latex.append("\n\\end{enumerate}\n");
					break swtch;
				}
				if (tag.equals("ul")) {
					latex.append("\n\\end{itemize}\n");
					break swtch;
				}
				if (tag.equals("i")) {
					latex.append("}");
					break swtch;
				}
				if (tag.equals("em")) {
					latex.append("}");
					break swtch;
				}
				if (tag.equals("table")) {
					latex = tblinfo.endTable();
					tblinfo = tblstk.pop();
					break swtch;
				}
				if (tag.equals("th")) {
					tblinfo.endCol();
					break swtch;
				}
				if (tag.equals("td")) {
					tblinfo.endCol();
					break swtch;
				}
				if (tag.equals("tr")) {
					tblinfo.endRow();
					break swtch;
				}
				if (tag.equals("font")) {
					latex.append("}");
					break swtch;
				}

				break swtch;
			}
		}
	}

	Stack<TableInfo> tblstk = new Stack<TableInfo>();
	private TableInfo tblinfo;
	private int verbat = 0;

	private void latexEscapeAppend(String str) {
		for (int i = 0; i < str.length(); ++i) {
			int c = str.charAt(i);
			switch (c) {
			case 160: // &nbsp;
				latex.append("\\phantom{ }");
				break;
			case ' ':
				if (verbat > 0) {
					latex.append("\\phantom{ }");
				} else {
					latex.append(' ');
				}
				break;
			case '[':
				if (i < str.length() - 1 && str.charAt(i + 1) == ' ') {
					latex.append("\\lbrack\\ ");
					i++;
				} else {
					latex.append("\\lbrack ");
				}
				break;
			case ']':
				if (i < str.length() - 1 && str.charAt(i + 1) == ' ') {
					latex.append("\\rbrack\\ ");
					i++;
				} else {
					latex.append("\\rbrack ");
				}
				break;
			case '_':
			case '%':
			case '$':
			case '#':
			case '}':
			case '{':
			case '&':
				latex.append('\\');
				latex.append((char) c);
				if (i < str.length() - 1 && str.charAt(i + 1) == ' ') {
					latex.append("\\ ");
					i++;
				}
				break;
			// case 0xc38a:
			case 0xc3a6:
				if (Charset.defaultCharset().name().equals("UTF-8")) {
					if (i < str.length() - 1 && str.charAt(i + 1) == ' ') {
						latex.append("\\ae\\ ");
						i++;
					} else {
						latex.append("\\ae ");
					}
				} else {
					latex.append((char) c);
				}
				break;
			case 0xc386:
				if (Charset.defaultCharset().name().equals("UTF-8")) {
					if (i < str.length() - 1 && str.charAt(i + 1) == ' ') {
						latex.append("\\AE\\ ");
						i++;
					} else {
						latex.append("\\AE ");
					}
				} else {
					latex.append((char) c);
				}
				break;
			// case 0xc382:
			case 0xc3a5:
				if (Charset.defaultCharset().name().equals("UTF-8")) {
					if (i < str.length() - 1 && str.charAt(i + 1) == ' ') {
						latex.append("\\aa\\ ");
						i++;
					} else {
						latex.append("\\aa ");
					}
				} else {
					latex.append((char) c);
				}
				break;
			case 0xc385:
				if (Charset.defaultCharset().name().equals("UTF-8")) {
					if (i < str.length() - 1 && str.charAt(i + 1) == ' ') {
						latex.append("\\AA\\ ");
						i++;
					} else {
						latex.append("\\AA ");
					}
				} else {
					latex.append((char) c);
				}
				break;
			// case 0xc2af:
			case 0xc3b8:
				if (Charset.defaultCharset().name().equals("UTF-8")) {
					if (i < str.length() - 1 && str.charAt(i + 1) == ' ') {
						latex.append("\\o\\ ");
						i++;
					} else {
						latex.append("\\o ");
					}
				} else {
					latex.append((char) c);
				}
				break;
			// case 0xc3bf:
			case 0xc398:
				if (Charset.defaultCharset().name().equals("UTF-8")) {
					if (i < str.length() - 1 && str.charAt(i + 1) == ' ') {
						latex.append("\\O\\ ");
						i++;
					} else {
						latex.append("\\O ");
					}
				} else {
					latex.append((char) c);
				}
				break;
			case '^':
				latex.append("$\\wedge$");
				break;
			case '<':
				latex.append("\\textless ");
				break;
			case '\r':
			case '\n':
				if (tblstk.size() > 0) {
					// Swallow new lines while tables are in progress,
					// <tr> controls new line emission.
					if (verbat > 0) {
						latex.append("}\\mbox{}\\newline\n" + Constants.TELETYPE + "\\small ");
					} else {
						latex.append(" ");
					}
				} else {
					if (verbat > 0) {
						latex.append("}\\mbox{}\\newline\n" + Constants.TELETYPE + "\\small ");
					} else if ((i + 1) < str.length() && str.charAt(i + 1) == 10) {
						latex.append("\\bl ");
						++i;
					} else {
						latex.append((char) c);
					}
				}
				break;
			case '/':
				latex.append("/");
				break;
			case '>':
				latex.append("\\textgreater ");
				break;
			case '\\':
				latex.append("\\textbackslash ");
				break;
			default:
				latex.append((char) c);
				break;
			}
		}
	}

	public static String latexEscape(String str) {
		StringBuilder latex = new StringBuilder(str.length() * 2);
		for (int i = 0; i < str.length(); ++i) {
			int c = str.charAt(i);
			switch (c) {
			case 160: // &nbsp;
				latex.append("\\phantom{ }");
				break;
			case ' ':
				latex.append(' ');
				break;
			case '[':
				if (i < str.length() - 1 && str.charAt(i + 1) == ' ') {
					latex.append("\\lbrack\\ ");
					i++;
				} else {
					latex.append("\\lbrack ");
				}
				break;
			case ']':
				if (i < str.length() - 1 && str.charAt(i + 1) == ' ') {
					latex.append("\\rbrack\\ ");
					i++;
				} else {
					latex.append("\\rbrack ");
				}
				break;
			case '_':
			case '%':
			case '$':
			case '#':
			case '}':
			case '{':
			case '&':
				latex.append('\\');
				latex.append((char) c);
				if (i < str.length() - 1 && str.charAt(i + 1) == ' ') {
					latex.append("\\ ");
					i++;
				}
				break;
			// case 0xc38a:
			case 0xc3a6:
				if (i < str.length() - 1 && str.charAt(i + 1) == ' ') {
					latex.append("\\ae\\ ");
					i++;
				} else {
					latex.append("\\ae ");
				}
				break;
			case 0xc386:
				if (i < str.length() - 1 && str.charAt(i + 1) == ' ') {
					latex.append("\\AE\\ ");
					i++;
				} else {
					latex.append("\\AE ");
				}
				break;
			// case 0xc382:
			case 0xc3a5:
				if (i < str.length() - 1 && str.charAt(i + 1) == ' ') {
					latex.append("\\aa\\ ");
					i++;
				} else {
					latex.append("\\aa ");
				}
				break;
			case 0xc385:
				if (i < str.length() - 1 && str.charAt(i + 1) == ' ') {
					latex.append("\\AA\\ ");
					i++;
				} else {
					latex.append("\\AA ");
				}
				break;
			// case 0xc2af:
			case 0xc3b8:
				if (i < str.length() - 1 && str.charAt(i + 1) == ' ') {
					latex.append("\\o\\ ");
					i++;
				} else {
					latex.append("\\o ");
				}
				break;
			// case 0xc3bf:
			case 0xc398:
				if (i < str.length() - 1 && str.charAt(i + 1) == ' ') {
					latex.append("\\O\\ ");
					i++;
				} else {
					latex.append("\\O ");
				}
				break;
			case '^':
				latex.append("$\\wedge$");
				break;
			case '<':
				latex.append("\\textless ");
				break;
			case '\r':
			case '\n':
				if ((i + 1) < str.length() && str.charAt(i + 1) == 10) {
					latex.append("\\bl ");
					++i;
				} else {
					latex.append((char) c);
				}
				break;
			case '/':
				latex.append("/");
				break;
			case '>':
				latex.append("\\textgreater ");
				break;
			case '\\':
				latex.append("\\textbackslash ");
				break;
			default:
				latex.append((char) c);
				break;
			}
		}
		return latex.toString();
	}
}
