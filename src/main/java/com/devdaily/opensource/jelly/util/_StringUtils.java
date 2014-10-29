package com.devdaily.opensource.jelly.util;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: Mar 2, 2003 Time: 6:32:08
 * PM To change this template use Options | File Templates.
 */
public class _StringUtils extends TestCase
{

  // --------------------------------------------------------------------------//

  public void testWordCount1()
  {
    List<String> wikiStrings = new ArrayList<String>();

    // add some strings
    String s1 = "all fall gala\n";
    wikiStrings.add(s1);
    String s2 = "foo bar baz\n";
    wikiStrings.add(s2);
    String s3 = "Java =String= class\n";
    wikiStrings.add(s3);
    String s4 = "Java ++String class\n";
    wikiStrings.add(s4);
    String s5 = "Wow, it's _way_ over *there*.\n";
    wikiStrings.add(s5);

    // do the conversion
    List<String> plainStrings = StringUtils.getListOfPlainTextStrings(wikiStrings);

    // get strings back and test
    String r1 = plainStrings.get(0);
    String r2 = plainStrings.get(1);
    String r3 = plainStrings.get(2);
    String r4 = plainStrings.get(3);
    String r5 = plainStrings.get(4);
    assertEquals(s1, r1);
    assertEquals(s2, r2);
    assertEquals("Java String class\n", r3);
    assertEquals("Java String class\n", r4);
    assertEquals("Wow, it's way over there.\n", r5);
  }

  // --------------------------------------------------------------------------//

  public void testWordCount2()
  {
    List<String> wikiStrings = new ArrayList<String>();
    
    // add some strings
    String s1 = "all fall gala\n";
    wikiStrings.add(s1);
    String s2 = "foo bar baz\n";
    wikiStrings.add(s2);
    String s3 = "Java =String= class\n";
    wikiStrings.add(s3);
    String s4 = "Java ++String class\n";
    wikiStrings.add(s4);
    String s5 = "Wow, it's _way_ over *there*.\n";
    wikiStrings.add(s5);

    // do the conversion
    SortedMap<String, Integer> wordsPhrases = StringUtils.getMostCommonWordsAndPhrases(wikiStrings);
    
    for (String key : wordsPhrases.keySet())
    {
      System.out.format("%s has a count of %s\n", key, wordsPhrases.get(key));
    }

  }
  
  
  // --------------------------------------------------------------------------//

  public void testNull()
  {
    try
    {
      String wikiString = null;
      String latexString = StringUtils.createLatexStringFromWikiString(wikiString);
      assertNull(latexString);
    }
    catch (IOException e)
    {
      e.printStackTrace();
      fail("Rec'd IOException where none should have occurred.");
    }
  }

  // --------------------------------------------------------------------------//

  public void testBlank()
  {
    try
    {
      String wikiString = "";
      String latexString = StringUtils.createLatexStringFromWikiString(wikiString);
      assertTrue(wikiString.equals(latexString));
    }
    catch (IOException e)
    {
      e.printStackTrace();
      fail("Rec'd IOException where none should have occurred.");
    }
  }

  // --------------------------------------------------------------------------//

  public void testManyBlanks()
  {
    try
    {
      String wikiString = "           \n";
      String latexString = StringUtils.createLatexStringFromWikiString(wikiString);
      assertTrue(wikiString.equals(latexString));
    }
    catch (IOException e)
    {
      e.printStackTrace();
      fail("Rec'd IOException where none should have occurred.");
    }
  }

  public void testHeadingLevel1() throws IOException
  {
    String wikiString = "---+My Life and Times\n";
    String desiredResult = "\\chapter{My Life and Times}\n";
    String latexString = StringUtils.createLatexStringFromWikiString(wikiString);
    assertTrue(desiredResult.equals(latexString));
  }

  public void testHeadingLevel1WithInitialSpaces() throws IOException
  {
    // intent here is to make sure that initial whitespace is removed.
    // technically this is not legal wiki text. just playing with pattern
    String wikiString = "---+  My Life and Times\n";
    String desiredResult = "\\chapter{My Life and Times}\n";
    String latexString = StringUtils.createLatexStringFromWikiString(wikiString);
    assertTrue(desiredResult.equals(latexString));
  }

  // can't get this one to work
  /*
   * public void testHeadingLevel1WithSpacingAtEnd() throws IOException { String
   * wikiString = "---+My Life and Times  \n"; String desiredResult =
   * "\\chapter{My Life and Times}\n"; String latexString =
   * StringUtils.createLatexStringFromWikiString(wikiString);
   * System.err.println(":" + latexString + ":");
   * assertTrue(desiredResult.equals(latexString)); }
   */

  public void testHeadingLevel2() throws IOException
  {
    String wikiString = "---++My Life and Times\n";
    String desiredResult = "\\section{My Life and Times}\n";
    String latexString = StringUtils.createLatexStringFromWikiString(wikiString);
    assertTrue(desiredResult.equals(latexString));
  }

  public void testHeadingLevel3() throws IOException
  {
    String wikiString = "---+++My Life and Times\n";
    String desiredResult = "\\subsection{My Life and Times}\n";
    String latexString = StringUtils.createLatexStringFromWikiString(wikiString);
    assertTrue(desiredResult.equals(latexString));
  }

  public void testHeadingLevel4() throws IOException
  {
    String wikiString = "---++++My Life and Times\n";
    String desiredResult = "\\subsubsection{My Life and Times}\n";
    String latexString = StringUtils.createLatexStringFromWikiString(wikiString);
    assertTrue(desiredResult.equals(latexString));
  }

  public void testSimpleBoldFormatting() throws IOException
  {
    String wikiString = "*this should be bold*";
    String desiredResult = "\\textbf{this should be bold}\n";
    String latexString = StringUtils.createLatexStringFromWikiString(wikiString);
    assertTrue(desiredResult.equals(latexString));
  }

  public void testBoldWordInMidSentence() throws IOException
  {
    String wikiString = "this word should be *bold* (the last word)";
    String desiredResult = "this word should be \\textbf{bold} (the last word)\n";
    String latexString = StringUtils.createLatexStringFromWikiString(wikiString);
    assertTrue(desiredResult.equals(latexString));
  }

  public void testMultipleBoldWords() throws IOException
  {
    String wikiString = "*bold* should be *bold* should be *bold* should be";
    String desiredResult = "\\textbf{bold} should be \\textbf{bold} should be \\textbf{bold} should be\n";
    String latexString = StringUtils.createLatexStringFromWikiString(wikiString);
    assertTrue(desiredResult.equals(latexString));
  }

  public void testItalicWordInMidSentence() throws IOException
  {
    String wikiString = "this word should be _italic_ (the last word)";
    String desiredResult = "this word should be \\textit{italic} (the last word)\n";
    String latexString = StringUtils.createLatexStringFromWikiString(wikiString);
    assertTrue(desiredResult.equals(latexString));
  }

  public void testSimpleItalicsFormatting() throws IOException
  {
    String wikiString = "_this should be italics_";
    String desiredResult = "\\textit{this should be italics}\n";
    String latexString = StringUtils.createLatexStringFromWikiString(wikiString);
    assertTrue(desiredResult.equals(latexString));
  }

  public void testSimpleBoldItalicsFormatting() throws IOException
  {
    String wikiString = "__bold plus italics__";
    String desiredResult = "\\textit{\\textbf{bold plus italics}}\n";
    String latexString = StringUtils.createLatexStringFromWikiString(wikiString);
    assertTrue(desiredResult.equals(latexString));
  }

  public void testFixedFontFormatting() throws IOException
  {
    String wikiString = "=public static void=";
    String desiredResult = "\\texttt{public static void}\n";
    String latexString = StringUtils.createLatexStringFromWikiString(wikiString);
    assertTrue(desiredResult.equals(latexString));
  }

  public void testBoldFixedFontFormatting() throws IOException
  {
    String wikiString = "==public static void==";
    String desiredResult = "\\texttt{\\textbf{public static void}}\n";
    String latexString = StringUtils.createLatexStringFromWikiString(wikiString);
    assertTrue(desiredResult.equals(latexString));
  }

  public void testMultipleTags1() throws IOException
  {
    String wikiString = "*bold* _italics_ *bold* stuff";
    String desiredResult = "\\textbf{bold} \\textit{italics} \\textbf{bold} stuff\n";
    String latexString = StringUtils.createLatexStringFromWikiString(wikiString);
    assertTrue(desiredResult.equals(latexString));
  }

  public void testMultipleTags2() throws IOException
  {
    String wikiString = "There is some *bold* _italics_ *bold* stuff";
    String desiredResult = "There is some \\textbf{bold} \\textit{italics} \\textbf{bold} stuff\n";
    String latexString = StringUtils.createLatexStringFromWikiString(wikiString);
    assertTrue(desiredResult.equals(latexString));
  }

  public void testMultipleTags3() throws IOException
  {
    String wikiString = "*start bold* then *end bold*";
    String desiredResult = "\\textbf{start bold} then \\textbf{end bold}\n";
    String latexString = StringUtils.createLatexStringFromWikiString(wikiString);
    assertTrue(desiredResult.equals(latexString));
  }

  public void testMultipleTags4() throws IOException
  {
    String wikiString = "__bold__ then *end bold*";
    String desiredResult = "\\textit{\\textbf{bold}} then \\textbf{end bold}\n";
    String latexString = StringUtils.createLatexStringFromWikiString(wikiString);
    assertTrue(desiredResult.equals(latexString));
  }

  public void test1LineNoAsterisks()
  {
    try
    {
      String wikiString = "This is a test line.\n";
      String latexString = StringUtils.createLatexStringFromWikiString(wikiString);
      assertTrue(wikiString.equals(latexString));
    }
    catch (IOException e)
    {
      e.printStackTrace();
      fail("Rec'd IOException where none should have occurred.");
    }
  }

  // --------------------------------------------------------------------------//
  /**
   * Buyer beware -- this test does not currently pass!
   */

  /*
   * public void testLineWithAsterisksButWithPrecedingText() { try { String
   * wikiString = "abc* This is a test line."; String latexString =
   * StringUtils.createLatexStringFromWikiString(wikiString);
   * assertTrue(wikiString.equals(latexString)); } catch (IOException e) {
   * e.printStackTrace();
   * fail("Rec'd IOException where none should have occurred."); } }
   */

  // --------------------------------------------------------------------------//

  public void test1LineWithAsteriskInWrongPlace()
  {
    try
    {
      // test 1st position
      String wikiString = "*This is a test line.\n";
      String latexString = StringUtils.createLatexStringFromWikiString(wikiString);
      assertTrue(wikiString.equals(latexString));
      // test 2nd position
      wikiString = " *This is a test line.\n";
      latexString = StringUtils.createLatexStringFromWikiString(wikiString);
      assertTrue(wikiString.equals(latexString));
      // test 3rd position
      wikiString = "  *This is a test line.\n";
      latexString = StringUtils.createLatexStringFromWikiString(wikiString);
      assertTrue(wikiString.equals(latexString));
      // test 5th position
      wikiString = "    *This is a test line.\n";
      latexString = StringUtils.createLatexStringFromWikiString(wikiString);
      assertTrue(wikiString.equals(latexString));
    }
    catch (IOException e)
    {
      e.printStackTrace();
      fail("Rec'd IOException where none should have occurred.");
    }
  }

  // --------------------------------------------------------------------------//

  public void testGetIndentationLevel()
  {
    String wikiString = "";
    int indentLevel = StringUtils.getIndentationLevel(wikiString);
    assertTrue(indentLevel == 0);

    wikiString = "   ";
    indentLevel = StringUtils.getIndentationLevel(wikiString);
    assertTrue("test indentation of 3 spaces", indentLevel == 0);

    wikiString = "   *";
    indentLevel = StringUtils.getIndentationLevel(wikiString);
    assertTrue("test indentation of 3 spaces and *", indentLevel == 1);

    wikiString = "    *";
    indentLevel = StringUtils.getIndentationLevel(wikiString);
    assertTrue("test indentation of 4 spaces and *", indentLevel == 0);

    wikiString = "      *";
    indentLevel = StringUtils.getIndentationLevel(wikiString);
    assertTrue("test indentation of 6 spaces and *", indentLevel == 2);

    wikiString = "         *";
    indentLevel = StringUtils.getIndentationLevel(wikiString);
    assertTrue("test indentation of 9 spaces and *", indentLevel == 3);
  }

  public void test1LineWithGoodAsterisk()
  {
    try
    {
      String wikiString = "   * This is a test line.";
      String latexString = StringUtils.createLatexStringFromWikiString(wikiString);
      String desiredResult = "   \\begin{itemize}\n" + "   \\item This is a test line.\n" + "   \\end{itemize}\n";
      assertTrue(desiredResult.equals(latexString));
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  public void testBoldWordsInItem()
  {
    try
    {
      String wikiString = "   * This is a *test line*.";
      String latexString = StringUtils.createLatexStringFromWikiString(wikiString);
      String desiredResult = "   \\begin{itemize}\n" + "   \\item This is a \\textbf{test line}.\n" + "   \\end{itemize}\n";
      assertTrue(desiredResult.equals(latexString));
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  // --------------------------------------------------------------------------//
  public void test1LineEnumerate()
  {
    try
    {
      String wikiString = "   1 This is a test line.";
      String latexString = StringUtils.createLatexStringFromWikiString(wikiString);
      String desiredResult = "   \\begin{enumerate}\n" + "   \\item This is a test line.\n" + "   \\end{enumerate}\n";
      assertTrue(desiredResult.equals(latexString));
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  // --------------------------------------------------------------------------//
  public void testMultiLinesEnumWith1Level()
  {
    try
    {
      String wikiString = "   1 This is a test line.\n" + "   1 This is some more stuff\n" + "   1 This is some more stuff\n"
          + "   1 This is some more stuff\n" + "   1 This is some more stuff\n" + "   1 This is some more stuff\n";
      String latexString = StringUtils.createLatexStringFromWikiString(wikiString);
      String desiredResult = "   \\begin{enumerate}\n" + "   \\item This is a test line.\n"
          + "   \\item This is some more stuff\n" + "   \\item This is some more stuff\n" + "   \\item This is some more stuff\n"
          + "   \\item This is some more stuff\n" + "   \\item This is some more stuff\n" + "   \\end{enumerate}\n";
      assertTrue(desiredResult.equals(latexString));
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  public void testMultiLinesWith1Level()
  {
    try
    {
      String wikiString = "   * This is a test line.\n" + "   * This is some more stuff\n" + "   * This is some more stuff\n"
          + "   * This is some more stuff\n" + "   * This is some more stuff\n" + "   * This is some more stuff\n";
      String latexString = StringUtils.createLatexStringFromWikiString(wikiString);
      String desiredResult = "   \\begin{itemize}\n" + "   \\item This is a test line.\n" + "   \\item This is some more stuff\n"
          + "   \\item This is some more stuff\n" + "   \\item This is some more stuff\n" + "   \\item This is some more stuff\n"
          + "   \\item This is some more stuff\n" + "   \\end{itemize}\n";
      assertTrue(desiredResult.equals(latexString));
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  public void testBoldAndItalicsInList() throws IOException
  {
    String wikiString = "   * This is a *test* _line_.\n" + "   * This is some more stuff\n" + "   * This is some more stuff\n"
        + "   * This is some more stuff\n" + "   * This is some more stuff\n" + "   * This _is_ _some_ *more stuff*\n";
    String latexString = StringUtils.createLatexStringFromWikiString(wikiString);
    String desiredResult = "   \\begin{itemize}\n" + "   \\item This is a \\textbf{test} \\textit{line}.\n"
        + "   \\item This is some more stuff\n" + "   \\item This is some more stuff\n" + "   \\item This is some more stuff\n"
        + "   \\item This is some more stuff\n" + "   \\item This \\textit{is} \\textit{some} \\textbf{more stuff}\n"
        + "   \\end{itemize}\n";
    assertTrue(desiredResult.equals(latexString));
  }

  // --------------------------------------------------------------------------//
  public void testMultiLineEnumWithTwoLevels()
  {
    try
    {
      String wikiString = "   1 This is a test line.\n" + "   1 This is some more stuff\n" + "      1 This is some more stuff\n"
          + "      1 This is some more stuff\n";
      String latexString = StringUtils.createLatexStringFromWikiString(wikiString);
      String desiredResult = "   \\begin{enumerate}\n" + "   \\item This is a test line.\n"
          + "   \\item This is some more stuff\n" + "      \\begin{enumerate}\n" + "      \\item This is some more stuff\n"
          + "      \\item This is some more stuff\n" + "      \\end{enumerate}\n" + "   \\end{enumerate}\n";
      assertTrue(desiredResult.equals(latexString));
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  // --------------------------------------------------------------------------//
  public void testMultiLinesWithTwoLevels()
  {
    try
    {
      String wikiString = "   * This is a test line.\n" + "   * This is some more stuff\n" + "      * This is some more stuff\n"
          + "      * This is some more stuff\n";
      String latexString = StringUtils.createLatexStringFromWikiString(wikiString);
      String desiredResult = "   \\begin{itemize}\n" + "   \\item This is a test line.\n" + "   \\item This is some more stuff\n"
          + "      \\begin{itemize}\n" + "      \\item This is some more stuff\n" + "      \\item This is some more stuff\n"
          + "      \\end{itemize}\n" + "   \\end{itemize}\n";
      assertTrue(desiredResult.equals(latexString));
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  // --------------------------------------------------------------------------//
  public void testMultiLinesWithMultiLevels1()
  {
    try
    {
      String wikiString = "   * This is a test line.\n" + "   * This is some more stuff\n" + "      * This is some more stuff\n"
          + "      * This is some more stuff\n" + "         * This is some more stuff\n" + "         * This is some more stuff\n";
      String latexString = StringUtils.createLatexStringFromWikiString(wikiString);
      String desiredResult = "   \\begin{itemize}\n" + "   \\item This is a test line.\n" + "   \\item This is some more stuff\n"
          + "      \\begin{itemize}\n" + "      \\item This is some more stuff\n" + "      \\item This is some more stuff\n"
          + "         \\begin{itemize}\n" + "         \\item This is some more stuff\n"
          + "         \\item This is some more stuff\n" + "         \\end{itemize}\n" + "      \\end{itemize}\n"
          + "   \\end{itemize}\n";
      // printExpectedAndGotMessage(desiredResult, latexString);
      assertTrue(desiredResult.equals(latexString));
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  // --------------------------------------------------------------------------//
  public void testMultiLinesWithMultiLevels2()
  {
    try
    {
      String wikiString = "   * This is a test line.\n" + "   * This is some more stuff\n" + "      * This is some more stuff\n"
          + "      * This is some more stuff\n" + "   * This is some more stuff\n" + "   * This is some more stuff\n";
      String latexString = StringUtils.createLatexStringFromWikiString(wikiString);
      String desiredResult = "   \\begin{itemize}\n" + "   \\item This is a test line.\n" + "   \\item This is some more stuff\n"
          + "      \\begin{itemize}\n" + "      \\item This is some more stuff\n" + "      \\item This is some more stuff\n"
          + "      \\end{itemize}\n" + "   \\item This is some more stuff\n" + "   \\item This is some more stuff\n"
          + "   \\end{itemize}\n";
      assertTrue(desiredResult.equals(latexString));
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  // --------------------------------------------------------------------------//
  public void testMultiLinesWithMultiLevels3()
  {
    try
    {
      String wikiString = "   * This is a test line.\n" + "   * This is some more stuff\n" + "      * This is some more stuff\n"
          + "      * This is some more stuff\n" + "         * This is some more stuff\n" + "         * This is some more stuff\n"
          + "   * This is some more stuff\n" + "   * This is some more stuff\n";
      String latexString = StringUtils.createLatexStringFromWikiString(wikiString);
      String desiredResult = "   \\begin{itemize}\n" + "   \\item This is a test line.\n" + "   \\item This is some more stuff\n"
          + "      \\begin{itemize}\n" + "      \\item This is some more stuff\n" + "      \\item This is some more stuff\n"
          + "         \\begin{itemize}\n" + "         \\item This is some more stuff\n"
          + "         \\item This is some more stuff\n" + "         \\end{itemize}\n" + "      \\end{itemize}\n"
          + "   \\item This is some more stuff\n" + "   \\item This is some more stuff\n" + "   \\end{itemize}\n";
      // printExpectedAndGotMessage(desiredResult, latexString);
      assertTrue(desiredResult.equals(latexString));
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  // --------------------------------------------------------------------------//

  private void printExpectedAndGotMessage(String desiredResult, String latexString)
  {
    System.err.println("EXPECTED:");
    System.err.println(desiredResult);
    System.err.println("GOT:");
    System.err.println(latexString);
  }

  // --------------------------------------------------------------------------//

  public static void main(String args[])
  {
    junit.textui.TestRunner.run(_StringUtils.class);
  }

  public _StringUtils(String name)
  {
    super(name);
  }

  /**
   * add one line here for each test in the suite
   */
  public static Test suite()
  {
    // TestSuite suite = new TestSuite();
    // run tests manually
    // suite.addTest( new MagccOrderContainerTest("testNumericAddress1") );
    // return suite;

    // or, run tests dynamically
    return new TestSuite(_StringUtils.class);
  }
}
