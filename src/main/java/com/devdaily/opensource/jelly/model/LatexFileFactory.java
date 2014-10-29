package com.devdaily.opensource.jelly.model;

public class LatexFileFactory
{
  private static String getCommon()
  {
    String common = "\\pagestyle{plain}\n";
    common += "\\author{}\n";
    common += "\\title{}\n";
    common += "\\usepackage{makeidx}\n";
    common += "\\makeindex\n\n";
    common += "\\begin{document}\n";
    common += "\\maketitle\n";
    common += "\\tableofcontents\n\n";
    common += "\\chapter{Start}\n";
    common += "\\chapter{End}\n\n";
    common += "\\printindex\n\n";
    common += "\\end{document}\n\n";
    return common;
  }

  public static String getNewArticle()
  {
    String newArticle = "\\documentclass[letterpaper,11pt,onecolumn,oneside]{article}\n";
    newArticle += getCommon();
    return newArticle;
  }

  public static String getNewReport()
  {
    String newArticle = "\\documentclass[letterpaper,11pt]{report}\n";
    newArticle += getCommon();
    return newArticle;
  }

  public static String getNewBook()
  {
    String newArticle = "\\documentclass[letterpaper,11pt,onecolumn,twoside]{book}\n";
    newArticle += getCommon();
    return newArticle;
  }

  public static String getNewSlides()
  {
    String newArticle = "\\documentclass[letterpaper]{slides}\n";
    newArticle += getCommon();
    return newArticle;
  }
}