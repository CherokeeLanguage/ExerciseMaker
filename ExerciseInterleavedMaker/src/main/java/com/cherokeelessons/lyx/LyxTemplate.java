package com.cherokeelessons.lyx;

public class LyxTemplate {
	public static final String subsubsection(int number) {
		return "\\begin_layout Subsubsection*\n" + "Set " + number + "\n" + "\\end_layout\n";
	}
	
	public static final String subsubsection(char letter) {
		return "\\begin_layout Subsubsection*\n" + "Set " + letter + "\n" + "\\end_layout\n";
	}

	public static final String docStart = "#LyX 2.1 created this file. For more info see http://www.lyx.org/\n"
			+ "\\lyxformat 474\n" + "\\begin_document\n" + "\\begin_header\n" + "\\textclass extbook\n"
			+ "\\begin_preamble\n" + "\\usepackage{multicol}\n" + "\\end_preamble\n" + "\\use_default_options false\n"
			+ "\\maintain_unincluded_children false\n" + "\\language american\n" + "\\language_package default\n"
			+ "\\inputencoding utf8-plain\n" + "\\fontencoding global\n" + "\\font_roman FreeSerif\n"
			+ "\\font_sans FreeSans\n" + "\\font_typewriter FreeMono\n" + "\\font_math auto\n"
			+ "\\font_default_family default\n" + "\\use_non_tex_fonts true\n" + "\\font_sc false\n"
			+ "\\font_osf true\n" + "\\font_sf_scale 100\n" + "\\font_tt_scale 100\n" + "\\graphics none\n"
			+ "\\default_output_format pdf4\n" + "\\output_sync 1\n" + "\\bibtex_command default\n"
			+ "\\index_command default\n" + "\\paperfontsize default\n" + "\\spacing single\n" + "\\use_hyperref true\n"
			+ "\\pdf_author \"Michael Joyner\"\n" + "\\pdf_subject \"Cherokee Language\"\n" + "\\pdf_bookmarks true\n"
			+ "\\pdf_bookmarksnumbered true\n" + "\\pdf_bookmarksopen true\n" + "\\pdf_bookmarksopenlevel 0\n"
			+ "\\pdf_breaklinks true\n" + "\\pdf_pdfborder true\n" + "\\pdf_colorlinks false\n"
			+ "\\pdf_backref false\n" + "\\pdf_pdfusetitle true\n"
			+ "\\pdf_quoted_options \"unicode=true,plainpages=false,pdfpagelabels,baseurl=http://www.CherokeeLessons.com/,pdfpagelayout=OneColumn\"\n"
			+ "\\papersize custom\n" + "\\use_geometry true\n" + "\\use_package amsmath 1\n"
			+ "\\use_package amssymb 1\n" + "\\use_package cancel 1\n" + "\\use_package esint 1\n"
			+ "\\use_package mathdots 1\n" + "\\use_package mathtools 1\n" + "\\use_package mhchem 1\n"
			+ "\\use_package stackrel 1\n" + "\\use_package stmaryrd 1\n" + "\\use_package undertilde 1\n"
			+ "\\cite_engine basic\n" + "\\cite_engine_type default\n" + "\\biblio_style plain\n"
			+ "\\use_bibtopic false\n" + "\\use_indices false\n" + "\\paperorientation portrait\n"
			+ "\\suppress_date true\n" + "\\justification true\n" + "\\use_refstyle 0\n" + "\\index Index\n"
			+ "\\shortcut idx\n" + "\\color #008000\n" + "\\end_index\n" + "\\paperwidth 6in\n" + "\\paperheight 9in\n"
			+ "\\leftmargin 0.6in\n" + "\\topmargin 0.5in\n" + "\\rightmargin 0.4in\n" + "\\bottommargin 0.5in\n"
			+ "\\secnumdepth 0\n" + "\\tocdepth 1\n" + "\\paragraph_separation skip\n" + "\\defskip smallskip\n"
			+ "\\quotes_language english\n" + "\\papercolumns 1\n" + "\\papersides 2\n" + "\\paperpagestyle plain\n"
			+ "\\bullet 0 0 8 5\n" + "\\bullet 1 0 0 5\n" + "\\bullet 2 0 6 5\n" + "\\bullet 3 0 10 5\n"
			+ "\\tracking_changes false\n" + "\\output_changes false\n" + "\\html_math_output 0\n"
			+ "\\html_css_as_file 0\n" + "\\html_be_strict true\n" + "\\end_header\n" + "\n" + "\\begin_body\n\n";

	public static final String docEnd = "\n" + "\\end_body\n" + "\\end_document";

	public static final String multiCol2_begin = "\\begin_layout Standard\n" + "\\begin_inset ERT\n"
			+ "status collapsed\n" + "\n" + "\\begin_layout Plain Layout\n" + "\n" + "\n" + "\\backslash\n"
			+ "begin{multicols}{2}\n" + "\\end_layout\n" + "\n" + "\\begin_layout Plain Layout\n" + "\n" + "\n"
			+ "\\backslash\n" + "raggedcolumns\n" + "\\end_layout\n" + "\n" + "\\end_inset\n" + "\n" + "\n"
			+ "\\end_layout\n" + "";
	public static final String multiCol2_end = "\\begin_layout Standard\n" + "\\begin_inset ERT\n"
			+ "status collapsed\n" + "\n" + "\\begin_layout Plain Layout\n" + "\n" + "\n" + "\\backslash\n"
			+ "end{multicols}\n" + "\\end_layout\n" + "\n" + "\\end_inset\n" + "\n" + "\n" + "\\end_layout\n" + "";
}
