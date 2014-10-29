package com.devdaily.opensource.jelly.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.*;
import javax.swing.JOptionPane;


// TODO blockquote paragraph formatting is not working right
// TODO href's also don't work in blockquotes

/**

 */

public class HTMLStringUtils
{
  private static final int ITEMIZE_INDENT_TYPE   = 1;
  private static final int ENUMERATE_INDENT_TYPE = 2;
  private static final int NO_INDENT_TYPE        = 3;

  private static final int validPositions[] = {3,6,9,12,15,18,21,24,27,30,33};

  private static Pattern aPattern = null;
  private static Matcher aMatcher = null;
  
  // support a properties file in the home directory that contains a map
  // of "simple urls", like "String : http://xxx.yyy.zzz/foo/bar"
  private static Map<String, String> simpleUrlMap = new HashMap();
  private static String SIMPLE_URL_MAP_FILENAME = ".wikistar-simple-urls";
  
  public static String createHTMLStringFromWikiString(String wikiString)
  throws IOException
  {
    if (wikiString == null) return null;
    
    // reload the map of simple urls every time we're called
    populateSimpleUrlMap();

    List listOfWikiStrings = createListOfStringsFromWikiString(wikiString);
    String htmlString = convertListOfWikiStringsToHTMLString(listOfWikiStrings);
    return htmlString;
  }
  
  private static void populateSimpleUrlMap()
  {
    String canonicalSimpleUrlFilename = System.getProperty("user.home") + "/" + SIMPLE_URL_MAP_FILENAME;
    try
    {
      simpleUrlMap.clear();
      simpleUrlMap = FileUtils.readPropertiesFileAsMap(canonicalSimpleUrlFilename, ":");
    }
    catch (Exception e)
    {
      // do nothing, finding anything in this file is optional
    }
  }

  private static String convertListOfWikiStringsToHTMLString(List listOfStrings)
  {
    StringBuffer htmlStringBuffer = new StringBuffer();
    int currentLevel = 0;
    int lastLevel = 0;

    String currentString = null;
    String preStringSpacing = null;
    //int differenceBetweenLevels = 0;
    Iterator it = listOfStrings.iterator();
    int indentationType = NO_INDENT_TYPE;
    int lastIndentationType = indentationType;
    while ( it.hasNext() )
    {
      currentString = (String)it.next();

      if (currentString.matches("^\\.\\.\\..*$"))
      {
        // ... => PRE
        htmlStringBuffer.append(processPRESection(currentString, it));
      }
      else if (currentString.matches("^>>>"))
      {
        // >>> => BLOCKQUOTE
        htmlStringBuffer.append(processBlockquoteSection(currentString, it));
      }
      else if (currentString.matches("^\\$.*:.*") || currentString.matches("^   \\$.*:.*"))
      {
        // this is the "definition list", which looks like this: "$ term : description"
        // or this: "   $ term : description" (three spaces and a dollar sign
        htmlStringBuffer.append(processDefinitionListSection(currentString, it));
      }
      else if (currentString.matches("^<img src.*"))
      {
        htmlStringBuffer.append(processImgSection(currentString, it));
      }
//      else if (currentString.matches("^<!--adsense.*"))
//      {
//        htmlStringBuffer.append(processAdsenseLine(currentString, it));
//      }
      else
      {
    	currentString = convertBadCharsToIsoLatin(currentString);

        // TODO this section needs to be refactored
        currentString = convertSimpleWikiTags(currentString);
        currentLevel = getIndentationLevel(currentString);

        lastIndentationType = indentationType;
        indentationType = getIndentationType(currentString);
        if (indentationType==NO_INDENT_TYPE) indentationType = lastIndentationType;

        if ( currentLevel==0  && currentLevel==lastLevel )
        {
          currentString = convertWikiTagsToLatexTags(currentString);
          htmlStringBuffer.append(currentString + "\n");
        }
        else if ( currentLevel<lastLevel )
        {
          //differenceBetweenLevels = lastLevel - currentLevel;
          int tmpLastLevel = lastLevel;
          for ( int i=currentLevel; i<tmpLastLevel; tmpLastLevel--)
          {
            htmlStringBuffer.append(getPreStringSpacing(tmpLastLevel) +getClosingIndentationTag(indentationType) +"\n");
          }
          htmlStringBuffer.append(getPreStringSpacing(currentLevel) + convertCurrentLineToItemizedLine(currentString,indentationType));
        }
        else if ( currentLevel!=0  && currentLevel==lastLevel )
        {
          htmlStringBuffer.append(getPreStringSpacing(currentLevel) + convertCurrentLineToItemizedLine(currentString,indentationType));
        }
        else if ( currentLevel!=0  && currentLevel>lastLevel )
        {
          htmlStringBuffer.append(getPreStringSpacing(currentLevel) +getOpeningIndentationTag(indentationType) +"\n");
          htmlStringBuffer.append(getPreStringSpacing(currentLevel) + convertCurrentLineToItemizedLine(currentString,indentationType));
        }
        lastLevel = currentLevel;
      }
    }
  
    if ( lastLevel>0 )
    {
      for ( int i=0; i<lastLevel; lastLevel--)
      {
        htmlStringBuffer.append(getPreStringSpacing(lastLevel) + getClosingIndentationTag(indentationType) +"\n");
      }
    }

    return htmlStringBuffer.toString();

  }
  
  static String convertBadCharsToIsoLatin(String currentString) {
    String tmp = currentString.replaceAll("<", "&lt;");
    tmp = tmp.replaceAll(">", "&gt;");
    return tmp;
  }
  
  private static String processAdsenseLine(String initialString, Iterator it)
  {
    return initialString + "\n";
  }

  /**
   * "Process" an IMG tag by ignoring it.
   * Assume the IMG tag is all on one line.
   */
  private static String processImgSection(String initialString, Iterator it)
  {
    return initialString + "\n";
  }

  /**
   * TODO refactor this code, merge it with the "BLOCKQUOTE" handler code.
   */
  private static String processPRESection(String initialString, Iterator it)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("<pre>\n");
    while (it.hasNext())
    {
      String currentLine = (String)it.next();

      // leave when we find the closing tag (this was </PRE>)
      if (currentLine.matches("^\\.\\.\\..*$"))
      {
        sb.append("</pre>\n");
        return sb.toString();
      }
      
      currentLine = currentLine.replaceAll("<", "&lt;");
      currentLine = currentLine.replaceAll(">", "&gt;");
      sb.append(currentLine + "\n");
    }
    
    // shouldn't actually come here
    return sb.toString();
  }

  /**
   * TODO refactor this code, merge it with the "PRE" handler code.
   */
  private static String processBlockquoteSection(String initialString, Iterator it)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("<blockquote>\n");
    while (it.hasNext())
    {
      String currentLine = (String)it.next();

      // leave when we find the closing tag (this was </PRE>)
      if (currentLine.matches("^>>>"))
      {
        //sb.append("</p>\n");
        sb.append("</blockquote>\n");
        return sb.toString();
      }
      else if (currentLine.matches("^$") || currentLine.trim().equals(""))
      {
        // do nothing, just ignore blank lines
      }
      else
      {
        currentLine = currentLine.replaceAll("<", "&lt;");
        currentLine = currentLine.replaceAll(">", "&gt;");
        sb.append("<p>" + currentLine.trim() + "</p>\n");
      }
      
    }
    
    // shouldn't actually come here
    return sb.toString();
  }

  /** 
   * get something like "$ term : definition" and convert it to
     <dl> 
       <dt>Sushi</dt>
       <dd>Japan</dd> 
     </dl>
   */
  private static String processDefinitionListSection(String initialString, Iterator it)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("<dl>\n");
    
    // need to deal with the initial line
    sb.append(getDtDdString(initialString));
    
    // now handle subsequent lines
    while (it.hasNext())
    {
      String currentLine = (String)it.next();

      if (currentLine.matches("^$"))
      {
        // leave when we find a blank line
        sb.append("</dl>\n");
        return sb.toString();
      }
      else
      {
        // otherwise keep building the definition list
        sb.append(getDtDdString(currentLine));
      }
    }
    
    // shouldn't actually come here
    return sb.toString();
  }
  
  /**
   * Build the <DT> and <DD> portion of a definition list string, based
   * on the Twiki formatting style we were given.
   */
  private static String getDtDdString(String definitionListString)
  {
    StringBuilder sb = new StringBuilder();
    
    // find location of '$' and ':'
    int dollarLoc = definitionListString.indexOf("$");
    int colonLoc = definitionListString.indexOf(":");
    
    // term is between $ and :
    String term = definitionListString.substring(dollarLoc+1, colonLoc-1);
    term = term.trim();
    
    // definition is after ':' and to the end of line
    String definition = definitionListString.substring(colonLoc+1);
    definition = definition.trim();
    
    // create the html
    String dt = "<dt>" + term + "</dt>\n";
    String dd = "<dd>" + definition + "</dd>\n";

    // append the html to our string builder
    sb.append(dt);
    sb.append(dd);
    
    return sb.toString();
  }


  private static String getOpeningIndentationTag(int indentationType)
  {
    if (indentationType == ITEMIZE_INDENT_TYPE) return "<ul>";
    if (indentationType == ENUMERATE_INDENT_TYPE) return "<ol>";
    assert false : "should never come here";
    return null;
  }

  private static String getClosingIndentationTag(int indentationType)
  {
    if (indentationType == ITEMIZE_INDENT_TYPE) return "</ul>";
    if (indentationType == ENUMERATE_INDENT_TYPE) return "</ol>";
    assert false : "should never come here";
    return null;
  }

  private static String convertCurrentLineToItemizedLine(String currentString, int indentationType)
  {
    char itemCharacter = ' ';
    if (indentationType == ITEMIZE_INDENT_TYPE) itemCharacter = '*';
    if (indentationType == ENUMERATE_INDENT_TYPE) itemCharacter = '1';

    assert itemCharacter == '*' || itemCharacter == '1' : "itemCharacter is not a valid character";

    int positionOfFirstItemCharacter = currentString.indexOf(itemCharacter);
    if ( positionOfFirstItemCharacter<0 )
    {
      return currentString + "\n";
    }
    return "<li>" + currentString.substring(positionOfFirstItemCharacter+2) + "</li>\n";
  }

  private static String getPreStringSpacing(int indentationLevel)
  {
    StringBuffer sb = new StringBuffer();
    for ( int i=0; i<indentationLevel; i++ )
    {
      sb.append("   ");  // this assumes a desired indentation of three spaces per indent level
    }
    return sb.toString();
  }

  /**
   * Determine the indentation level of a given string.
   * 0 == no asterisks in the string, or at least not in the correct places
   * 1 == 3 spaces indented
   * 2 == 7 spaces indented
   */
  public static int getIndentationLevel(String wikiString)
  {
    int characterPosition = wikiString.indexOf('1');
    //if (characterPosition < 3) return 0;
    for (int i=0; i<validPositions.length; i++)
    {
      if (characterPosition==validPositions[i])
      {
        if (noCharactersPrecedeItemCharacter(wikiString,i)) return i+1;
      }
    }
    characterPosition = wikiString.indexOf('*');
    //if (characterPosition < 3) return 0;
    for (int i=0; i<validPositions.length; i++)
    {
      if (characterPosition==validPositions[i])
      {
        if (noCharactersPrecedeItemCharacter(wikiString,i)) return i+1;
      }
    }
    return 0;
  }

  private static boolean noCharactersPrecedeItemCharacter(String wikiString, int currentPosition)
  {
    for (int i=0; i<currentPosition; i++)
    {
      if (wikiString.charAt(i)!=' ')
      {
        return false;
      }
    }
    return true;
  }

  public static int getIndentationType(String wikiString)
  {
    if ( characterIsInIndentPosition("* ",wikiString) ) return ITEMIZE_INDENT_TYPE;
    if ( characterIsInIndentPosition("1 ",wikiString) ) return ENUMERATE_INDENT_TYPE;
    return NO_INDENT_TYPE;
  }

  private static boolean characterIsInIndentPosition(String wikiCharacter, String wikiString)
  {
    int enumeratePosition = wikiString.indexOf(wikiCharacter);
    for (int i=0; i<validPositions.length; i++)
    {
      if (enumeratePosition==validPositions[i])
      {
        return true;
      }
    }
    return false;
  }

  /**
   * Converts a string (presumably) w/ embedded newline characters into
   * a list of strings.
   */
  private static List createListOfStringsFromWikiString(String wikiString)
  throws IOException
  {
    LinkedList listOfStrings;
    BufferedReader br = new BufferedReader( new StringReader(wikiString) );
    String currentLine = null;
    listOfStrings = new LinkedList();
    while ( (currentLine = br.readLine()) != null  )
    {
      listOfStrings.add(currentLine);
    }
    return listOfStrings;
  }

  private static String convertWikiTagsToLatexTags(final String wikiString)
  {
    // if a heading string can't be found just return
    if (wikiString.indexOf("---+")<0) return wikiString;

    String newString = wikiString;
    newString = convertHeadingTagsToWikiTags(newString);

    return newString;

  }

  /**
   *
   * @param wikiString
   * @return A String that has been converted from a wiki string to a latex string.
   */
  private static String convertHeadingTagsToWikiTags(String wikiString)
  {
    aPattern = Pattern.compile("^---\\+\\+\\+\\+\\s*(.*)[ \t]*$");
    aMatcher = aPattern.matcher(wikiString);
    if (aMatcher.find()) return "<h4>" + aMatcher.group(1) + "</h4>";

    aPattern = Pattern.compile("^---\\+\\+\\+\\s*(.*)[ \t]*$");
    aMatcher = aPattern.matcher(wikiString);
    if (aMatcher.find()) return "<h3>" + aMatcher.group(1) + "</h3>";

    aPattern = Pattern.compile("^---\\+\\+\\s*(.*)[ \t]*$");
    aMatcher = aPattern.matcher(wikiString);
    if (aMatcher.find()) return "<h2>" + aMatcher.group(1) + "</h2>";

    aPattern = Pattern.compile("^---\\+\\s*(.*)$");
    aMatcher = aPattern.matcher(wikiString);
    if (aMatcher.find()) return "<h1>" + aMatcher.group(1) + "</h1>";

    return wikiString;
  }

  private static String convertSimpleWikiTags(String currentString)
  {
    currentString = convertBeginVerbatimTag(currentString);
    currentString = convertEndVerbatimTag(currentString);
    
    currentString = convertLatexBoldTagToHTML(currentString);
    currentString = convertLatexEmphTagToHTML(currentString);
    currentString = convertLatexItalicsTagToHTML(currentString);
    currentString = convertLatexTEXTTTTagToHTML(currentString);
    currentString = convertLatexUnderlineTagToHTML(currentString);

    currentString = convertWikiBoldFixedFontTag(currentString); // must precede convertWikiFixedFontTag
    currentString = convertWikiFixedFontTag(currentString);

    currentString = convertWikiBoldItalicsTag(currentString);  // convert bold + italics tags; must precede italics conversion
    currentString = convertWikiItalicsTag(currentString);  // convert italics tags in a line

    currentString = convertWikiBoldTag(currentString);  // convert bold tags
    currentString = convertQuotes(currentString);       // convert single- and double-quotes

    // new
    currentString = convertSimpleAnchorTag(currentString);

    currentString = convertAnchorTag(currentString);

    // order is important; do this at the very end
    currentString = makeParagraphTags(currentString);
    return currentString;
  }

  // something like " **String " or " **JFrame."
  // leave at package scope for testing.
  static String convertSimpleAnchorTag(String wikiString)
  {
    String currentString = wikiString;
    aPattern = Pattern.compile(" (\\+\\+\\S+)([\\. ,:;])");

    boolean stillTrying = true;
    while (stillTrying)
    {
      aMatcher = aPattern.matcher(currentString);
      if (aMatcher.find())
      {
        // this should be "**String"
        String thingToReplace = aMatcher.group(1);
        String spaceOrPeriod = aMatcher.group(2);

        // this is the key looking into the map of urls
        thingToReplace = thingToReplace.trim().substring(2);
        
        // TODO kludge
        if (thingToReplace.charAt(thingToReplace.length()-1) == '.')
        {
          thingToReplace = thingToReplace.substring(0, thingToReplace.length()-1);
        }
        else if (thingToReplace.charAt(thingToReplace.length()-1) == ',')
        {
          thingToReplace = thingToReplace.substring(0, thingToReplace.length()-1);
        }
        else if (thingToReplace.charAt(thingToReplace.length()-1) == ':')
        {
          thingToReplace = thingToReplace.substring(0, thingToReplace.length()-1);
        }
        else if (thingToReplace.charAt(thingToReplace.length()-1) == ';')
        {
          thingToReplace = thingToReplace.substring(0, thingToReplace.length()-1);
        }
        
        String theUrl = simpleUrlMap.get(thingToReplace);
        if (theUrl != null)
        {
          if (theUrl.startsWith("http:")) {
        	currentString = aMatcher.replaceFirst(" <a href=\"" + theUrl + "rel=\"nofollow\" target=\"_blank\" " +"\">" + thingToReplace + "</a>" + spaceOrPeriod);
          } else {
        	currentString = aMatcher.replaceFirst(" <a href=\"" + theUrl + "\">" + thingToReplace + "</a>" + spaceOrPeriod);
          }
          
        }
        else
        {
          // must replace that string with something to avoid an infinite loop
          currentString = aMatcher.replaceFirst(" <a href=\"" + "URL-NOT-FOUND" + "\">" + thingToReplace + "</a>" + spaceOrPeriod);
        }
      }
      else
      {
        stillTrying = false;
      }
    }
    return currentString;
  }

  // [[reference][text]]
  private static String convertAnchorTag(String wikiString)
  {
    aPattern = Pattern.compile("\\[\\[(\\S{1}.*?)\\]\\[({2}.*?)\\]\\]");
    aMatcher = aPattern.matcher(wikiString);
    if (aMatcher.find()) {
        String url = aMatcher.group(1);
        String desc = aMatcher.group(2);
        if (url.startsWith("http:")) {
          return aMatcher.replaceAll("<a href=\"" + url + "\" rel=\"nofollow\" target=\"_blank\">" + desc + "</a>");
        } else {
          return aMatcher.replaceAll("<a href=\"" + url + "\">" + desc + "</a>");
        }
    } else {
    	return wikiString;
    }
    

    
  }

  /**
   * Puts paragraph tags in the right places (hopefully).
   */
  private static String makeParagraphTags(String wikiString)
  {
    // lines that don't get <p> tags
    if (wikiString.indexOf("---+") >= 0) return wikiString;
    if (wikiString.indexOf("<h1>") >= 0) return wikiString;
    if (wikiString.indexOf("<h2>") >= 0) return wikiString;
    if (wikiString.indexOf("<h3>") >= 0) return wikiString;
    if (wikiString.indexOf("<h4>") >= 0) return wikiString;
    if (wikiString.indexOf("<h5>") >= 0) return wikiString;
    if (wikiString.indexOf("<h6>") >= 0) return wikiString;
    if (wikiString.indexOf("<ul>") >= 0) return wikiString;
    if (wikiString.indexOf("<ol>") >= 0) return wikiString;
    if (wikiString.indexOf("<li>") >= 0) return wikiString;
    if (wikiString.indexOf("<pre>") >= 0) return wikiString;
    if (wikiString.indexOf("</pre>") >= 0) return wikiString;
    
    aPattern = Pattern.compile("^(\\S{1}.*?)$");
    aMatcher = aPattern.matcher(wikiString);
    return aMatcher.replaceAll("<p>$1</p>");
  }
  
  private static String convertBeginVerbatimTag(String wikiString)
  {
    if (wikiString.equals("\\begin{verbatim}"))
      return "<pre>";
    else
      return wikiString;
  }

  private static String convertEndVerbatimTag(String wikiString)
  {
    if (wikiString.equals("\\end{verbatim}"))
      return "</pre>";
    else
      return wikiString;
  }

  private static String convertBeforeAfterPattern(String s, String before, String after, String replaceBefore, String replaceAfter)
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

  private static String convertLatexUnderlineTagToHTML(String wikiString)
  {
    return convertBeforeAfterPattern(wikiString, "\\underline{", "}", "<u>", "</u>");
  }

  private static String convertLatexBoldTagToHTML(String wikiString)
  {
    return convertBeforeAfterPattern(wikiString, "\\textbf{", "}", "<b>", "</b>");
  }

  private static String convertLatexItalicsTagToHTML(String wikiString)
  {
    return convertBeforeAfterPattern(wikiString, "\\textit{", "}", "<em>", "</em>");
  }

  private static String convertLatexEmphTagToHTML(String wikiString)
  {
    return convertBeforeAfterPattern(wikiString, "\\emph{", "}", "<em>", "</em>");
  }

  private static String convertLatexTEXTTTTagToHTML(String wikiString)
  {
    return convertBeforeAfterPattern(wikiString, "\\texttt{", "}", "<code>", "</code>");
  }

  // at package scope for testing
  static String convertQuotes(String wikiString)
  {
    // leading double quote
    Pattern leadingQuote = Pattern.compile("( *)\"(\\w{1}.*?)");
    aMatcher = leadingQuote.matcher(wikiString);
    String a = aMatcher.replaceAll("$1&ldquo;$2");

    // trailing double quote (. or , before or after ", space at end)
    Pattern trailingQuote = Pattern.compile("(\\w{1}.*?)([,\\.]*)\"([,\\. ]*)");
    aMatcher = trailingQuote.matcher(a);
    String b = aMatcher.replaceAll("$1$2&rdquo;$3");
 
    // leading single quote (no space)
    Pattern leadingSingleQuote = Pattern.compile("^'(.*)$");
    aMatcher = leadingSingleQuote.matcher(b);
    String c = aMatcher.replaceAll("&lsquo;$1");

    // leading single quote (no space)
    Pattern leadingSingleQuote2 = Pattern.compile("^ '(.*)$");
    aMatcher = leadingSingleQuote2.matcher(c);
    String c2 = aMatcher.replaceAll("&lsquo;$1");

    // trailing single quote (. or , before or after ", space at end)
    Pattern trailingSingleQuote = Pattern.compile("(\\w{1}.*?)([,\\.]*)'([,\\. ]*)");
    aMatcher = trailingSingleQuote.matcher(c);
    String d = aMatcher.replaceAll("$1$2&rsquo;$3");
 
    return d;
  }

  private static String convertWikiBoldTag(String wikiString)
  {
	return replaceTagsUnlessAnchorTagFound(wikiString, "\\*(\\S{1}.*?)\\*", "<b>$1</b>");
  }

  private static String convertWikiItalicsTag(String wikiString)
  {
    return replaceTagsUnlessAnchorTagFound(wikiString, "_(\\S{1}.*?)_", "<em>$1</em>");
  }

  private static String convertWikiFixedFontTag(String wikiString)
  {
    return replaceTagsUnlessAnchorTagFound(wikiString, "=(\\S{1}.*?)=", "<code>$1</code>");
  }
  
  private static String convertWikiBoldItalicsTag(String wikiString)
  {
    return replaceTagsUnlessAnchorTagFound(wikiString, "__(\\S{1}.*?)__", "<em><b>$1</b></em>");
  }

  private static String convertWikiBoldFixedFontTag(String wikiString)
  {
    return replaceTagsUnlessAnchorTagFound(wikiString, "==(\\S{1}.*?)==", "<code><b>$1</b></code>");
  }

  private static String replaceTagsUnlessAnchorTagFound(String wikiString, String searchPattern, String replacementPattern) {
    boolean codeTagsFound = stringContainsPattern(wikiString, searchPattern);
    boolean hrefTagsFound = wikiString.contains("[[");   // [[url][desc]]
    if (codeTagsFound && hrefTagsFound) {
        return wikiString;  // don't modify the string, we'll just mess it up
    } else if (codeTagsFound) {
        aPattern = Pattern.compile(searchPattern);
        aMatcher = aPattern.matcher(wikiString);
        return aMatcher.replaceAll(replacementPattern);
    } else {
        return wikiString;
    }
  }
  
  private static boolean stringContainsPattern(String wikiString, String searchPattern) {
    Pattern p = Pattern.compile(searchPattern);
    Matcher m = p.matcher(wikiString);
    return m.find();
  }

  private static String getStringToLookFor(int depth)
  {
    String stringToLookFor;
    StringBuffer sbToLookFor = new StringBuffer("");
    for ( int i=0; i<depth; i++ )
    {
      sbToLookFor.append(" ");
    }
    sbToLookFor.append("   *");
    return sbToLookFor.toString();
  }

}






