package com.devdaily.opensource.jelly.util;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

import java.io.IOException;

public class _HTMLStringUtils extends TestCase
{

  public void testQuotes1() throws IOException
  {
    String input = "Hello, y'all";
    String expected = "Hello, y&rsquo;all";
    String actual = HTMLStringUtils.convertQuotes(input);
    assertEquals(expected, actual);
  }
  
  public void testQuotes2() throws IOException
  {
    String input = " \"Hello, ";
    String expected = " &ldquo;Hello, ";
    String actual = HTMLStringUtils.convertQuotes(input);
    assertEquals(expected, actual);
  }
  
  public void testQuotes3() throws IOException
  {
    String input = "la la la. \"How y'all doin', y'all?\" ";
    String expected = "la la la. &ldquo;How y&rsquo;all doin&rsquo;, y&rsquo;all?&rdquo; ";
    String actual = HTMLStringUtils.convertQuotes(input);
    assertEquals(expected, actual);
  }
  
  public void testSteveJobs() throws IOException
  {
    String input = "Simple motivation: \"You can do better.\"";
    String expected = "Simple motivation: &ldquo;You can do better.&rdquo;";
    String actual = HTMLStringUtils.convertQuotes(input);
    assertEquals(expected, actual);
  }
  
  // "Why is it like that? Why is it like that, and not like this?"
  public void testJonyIve() throws IOException
  {
    String input = "\"Why is it like that? Why is it like that, and not like this?\"";
    String expected = "&ldquo;Why is it like that? Why is it like that, and not like this?&rdquo;";
    String actual = HTMLStringUtils.convertQuotes(input);
    assertEquals(expected, actual);
  }
  
  // "Later equals never."
  public void testLater() throws IOException
  {
    // 1
    String input = "\"Later equals never.\"";
    String expected = "&ldquo;Later equals never.&rdquo;";
    String actual = HTMLStringUtils.convertQuotes(input);
    assertEquals(expected, actual);

    // 2
    input = " \"Later equals never.\"";
    expected = " &ldquo;Later equals never.&rdquo;";
    actual = HTMLStringUtils.convertQuotes(input);
    assertEquals(expected, actual);

    // 3
    input = " \"Later equals never.\" ";
    expected = " &ldquo;Later equals never.&rdquo; ";
    actual = HTMLStringUtils.convertQuotes(input);
    assertEquals(expected, actual);

    // 4
    input = "\"Later equals never\" ";
    expected = "&ldquo;Later equals never&rdquo; ";
    actual = HTMLStringUtils.convertQuotes(input);
    assertEquals(expected, actual);

    // 5
    input = "\"Later equals never\", he said.";
    expected = " &ldquo;Later equals never&rdquo;, he said.";
    actual = HTMLStringUtils.convertQuotes(input);
    assertEquals(expected, actual);
  }
  
  // 'Sure'
  public void testSure() throws IOException
  {
    String input = "'Sure', no problem.";
    String expected = "&lsquo;Sure&rsquo;, no problem.";
    String actual = HTMLStringUtils.convertQuotes(input);
    assertEquals(expected, actual);
  }
  

  
  
  /**
   * this test *only* works if the right file is in your home directory.
   * that file is currently named ~/.wikistar-simple-urls
   */
  public void testSimpleAnchorTag1() throws IOException
  {
    String wikiString = "yada yada ++String foo bar";
    String desiredResult = "<p>yada yada <a href=\"http://java.sun.com/j2se/1.5.0/docs/api/java/lang/String.html\">String</a> foo bar</p>\n";
    String htmlString = HTMLStringUtils.createHTMLStringFromWikiString(wikiString);
    assertEquals(desiredResult, htmlString);
  }
  
//  public void testSimpleAnchorTagPeriodAtEnd() throws IOException
//  {
//    String wikiString = "yada ++String. foo bar";
//    String desiredResult = "<p>yada <a href=\"http://java.sun.com/j2se/1.5.0/docs/api/java/lang/String.html\">String</a>. foo bar</p>\n";
//    String htmlString = HTMLStringUtils.createHTMLStringFromWikiString(wikiString);
//    assertEquals(desiredResult, htmlString);
//  }
//  
//  public void testSimpleAnchorTagSemicolonAtEnd() throws IOException
//  {
//    String wikiString = "yada ++String; foo bar";
//    String desiredResult = "<p>yada <a href=\"http://java.sun.com/j2se/1.5.0/docs/api/java/lang/String.html\">String</a>; foo bar</p>\n";
//    String htmlString = HTMLStringUtils.createHTMLStringFromWikiString(wikiString);
//    assertEquals(desiredResult, htmlString);
//  }
//  
//  public void testSimpleAnchorTagColonAtEnd() throws IOException
//  {
//    String wikiString = "yada ++String: foo bar";
//    String desiredResult = "<p>yada <a href=\"http://java.sun.com/j2se/1.5.0/docs/api/java/lang/String.html\">String</a>: foo bar</p>\n";
//    String htmlString = HTMLStringUtils.createHTMLStringFromWikiString(wikiString);
//    assertEquals(desiredResult, htmlString);
//  }
//
//  public void testSimpleAnchorTagCommaAtEnd() throws IOException
//  {
//    String wikiString = "yada ++String, foo bar";
//    String desiredResult = "<p>yada <a href=\"http://java.sun.com/j2se/1.5.0/docs/api/java/lang/String.html\">String</a>, foo bar</p>\n";
//    String htmlString = HTMLStringUtils.createHTMLStringFromWikiString(wikiString);
//    assertEquals(desiredResult, htmlString);
//  }
//  
//  public void testSimpleAnchorTagEndOfString() throws IOException
//  {
//    String wikiString = "yada ++String";
//    String desiredResult = "<p>yada <a href=\"http://java.sun.com/j2se/1.5.0/docs/api/java/lang/String.html\">String</a></p>\n";
//    String htmlString = HTMLStringUtils.createHTMLStringFromWikiString(wikiString);
//    assertEquals(desiredResult, htmlString);
//  }

  
  public void testHrefKnownToFail1() throws IOException
  {
    String wikiString = "a link to [[http://perishablepress.com/press/2006/08/20/a-nice-collection-of-feed-icons/][A little more PNG format]] the end";
    String desiredResult = "<p>a link to <a href=\"http://perishablepress.com/press/2006/08/20/a-nice-collection-of-feed-icons/\">A little more PNG format</a> the end</p>\n";
    String htmlString = HTMLStringUtils.createHTMLStringFromWikiString(wikiString);
    assertEquals(desiredResult, htmlString);
  }

  public void testHrefKnownToFail2() throws IOException
  {
    String wikiString = "a link to [[http://mouserunner.deviantart.com/art/Alternative-RSS-Icons-72124107][A few different RSS Feed icons]] the end";
    String desiredResult = "<p>a link to <a href=\"http://mouserunner.deviantart.com/art/Alternative-RSS-Icons-72124107\">A few different RSS Feed icons</a> the end</p>\n";
    String htmlString = HTMLStringUtils.createHTMLStringFromWikiString(wikiString);
    assertEquals(desiredResult, htmlString);
  }

  public void testHref1() throws IOException
  {
    String wikiString = "a link to [[http://devdaily.com][devdaily.com]] the end";
    String desiredResult = "<p>a link to <a href=\"http://devdaily.com\">devdaily.com</a> the end</p>\n";
    String htmlString = HTMLStringUtils.createHTMLStringFromWikiString(wikiString);
    assertEquals(desiredResult, htmlString);
  }

  public void testHref2() throws IOException
  {
    String wikiString = "a link to [[http://devdaily.com/][devdaily.com]] the end";
    String desiredResult = "<p>a link to <a href=\"http://devdaily.com/\">devdaily.com</a> the end</p>\n";
    String htmlString = HTMLStringUtils.createHTMLStringFromWikiString(wikiString);
    assertEquals(desiredResult, htmlString);
  }

  public void testHref3() throws IOException
  {
    String wikiString = "a link to [[http://devdaily.com/][yada yada yada]] the end";
    String desiredResult = "<p>a link to <a href=\"http://devdaily.com/\">yada yada yada</a> the end</p>\n";
    String htmlString = HTMLStringUtils.createHTMLStringFromWikiString(wikiString);
    assertEquals(desiredResult, htmlString);
  }

  /**
   * TODO this one currently fails because the wiki stuff is in the url
   */
//  public void testHref4() throws IOException
//  {
//    String wikiString = "a link to [[http://devdaily.com/foo/bar=/baz=][yada yada yada]] the end";
//    String desiredResult = "<p>a link to <a href=\"http://devdaily.com/foo/bar=/baz=\">yada yada yada</a> the end</p>\n";
//    String htmlString = HTMLStringUtils.createHTMLStringFromWikiString(wikiString);
//    assertEquals(desiredResult, htmlString);
//  }


  //--------------------------------------------------------------------------//

  public static void main(String args[])
  {
    junit.textui.TestRunner.run(_HTMLStringUtils.class);
  }

  public _HTMLStringUtils(String name)
  {
    super(name);
  }

  /**
   * add one line here for each test in the suite
   */
  public static Test suite()
  {
    //TestSuite suite = new TestSuite();
    // run tests manually
    //suite.addTest( new MagccOrderContainerTest("testNumericAddress1") );
    //return suite;

    // or, run tests dynamically
    return new TestSuite(_HTMLStringUtils.class);
  }
}
