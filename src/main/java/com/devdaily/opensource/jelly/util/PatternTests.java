package com.devdaily.opensource.jelly.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternTests
{
  private static Pattern aPattern = null;
  private static Matcher aMatcher = null;

  public PatternTests(String[] args)
  {
    System.out.println(convertBeforeAfterPattern(args[0], "\\underline{", "}", "<u>", "</u>"));
  }

  private String convertBeforeAfterPattern(String s, String before, String after, String replaceBefore, String replaceAfter)
  {
    int beforeLoc = s.indexOf(before);
    int afterLoc = s.indexOf(after);
    
    int beforeLength = before.length();

    if (beforeLoc >= 0 && afterLoc >= 0)
    {
      String s1 = s.substring(0,beforeLoc);
      String s2 = s.substring(beforeLoc+beforeLength,afterLoc);
      String s3 = s.substring(afterLoc+1);
      return s1 + replaceBefore + s2 + replaceAfter + s3;
    }
    else
    {
      return s;
    }
  }

  public static void main(String[] args)
  {
    new PatternTests(args);
  }

}
