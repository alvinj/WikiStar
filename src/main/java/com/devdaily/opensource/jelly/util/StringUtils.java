package com.devdaily.opensource.jelly.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.*;

public class StringUtils
{
  private static final int ITEMIZE_INDENT_TYPE   = 1;
  private static final int ENUMERATE_INDENT_TYPE = 2;
  private static final int NO_INDENT_TYPE        = 3;

  private static final int validPositions[] = {3,6,9,12,15,18,21,24,27,30,33};

  private static Pattern aPattern = null;
  private static Matcher aMatcher = null;


  public static String createLatexStringFromWikiString(String wikiString)
  throws IOException
  {
    if (wikiString == null) return null;

    List listOfWikiStrings = createListOfStringsFromWikiString(wikiString);
    String latexString = convertListOfWikiStringsToLatexString(listOfWikiStrings);
    return latexString;
  }

  public static SortedMap<String, Integer> getMostCommonWordsAndPhrases(List listOfWikiStrings)
  {
    List<String> plainTextStrings = getListOfPlainTextStrings(listOfWikiStrings);
    SortedMap<String, Integer> wordCountMap = new TreeMap();
    
    // loop through the strings, counting the most common words.
    // also, get rid of formatting characters as you go
    for (String s : plainTextStrings)
    {
      // get rid of formatting characters
      s = s.replaceAll("'", "");
      s = s.replaceAll("\"", "");
      s = s.replaceAll(";", "");
      s = s.replaceAll("\\.", "");
      s = s.replaceAll("--", "");
      s = s.replaceAll("-", " ");
      s = s.replaceAll("/", " ");
      
      // now go through each word in the current string; words should be separated by blanks now.
      String[] words = s.split(" ");
      for (int i=0; i<words.length; i++)
      {
        String word = words[i].trim();
        
        System.out.format("looking at word :%s:\n", word);
        
        // skip common words (optional)
        if (word.toLowerCase().equals("a")) continue;
        if (word.toLowerCase().equals("an")) continue;
        if (word.toLowerCase().equals("and")) continue;
        if (word.toLowerCase().equals("for")) continue;
        if (word.toLowerCase().equals("the")) continue;
        if (word.toLowerCase().equals("of")) continue;
        if (word.toLowerCase().equals("or")) continue;
        if (word.toLowerCase().equals("i")) continue;
        if (word.toLowerCase().equals("was")) continue;
        if (word.toLowerCase().equals("will")) continue;
        if (word.toLowerCase().equals("it")) continue;
        if (word.toLowerCase().equals("its")) continue;
        if (word.toLowerCase().equals("im")) continue;
        
        System.out.println("   still considering word ...");
        
        if (!wordCountMap.containsKey(word))
        {
          // word is not already in map, so add the first instance of it
          wordCountMap.put(word, 1);
        }
        else
        {
          // if the map already contains a value for this word, increment it
          int count = wordCountMap.get(word);
          wordCountMap.put(word, count+1);
        }
      }
    }
    
    return wordCountMap;

  }
  
  /**
   * Given a list of strings in a wiki markup format, return a list
   * of "plain text" strings, i.e., all wiki markup removed.
   */
  static List getListOfPlainTextStrings(List<String> listOfWikiStrings)
  {
    List<String> strings = new ArrayList<String>();
    
    boolean inLineSkipMode = false;
    
    for (String s: listOfWikiStrings)
    {
      // handle "..." blocks by skipping them
      if (s.matches("^\\.\\.\\..*"))
      {
        // when you first see this, go into line skip mode
        if (!inLineSkipMode) inLineSkipMode = true;
        
        // the second time you see it, stop skipping lines 
        if (inLineSkipMode)
        {
          inLineSkipMode = false;
          // but still need to skip the current line
          continue;
        }
      }
      if (inLineSkipMode) continue;

      if (s.matches("---+.*"))
      {
        // do nothing, skip this line
        continue;
      }
      else if (s.matches("^>>>.*"))
      {
        // do nothing, skip this line
        continue;
      }
      else if (s.matches("   \\*.*"))
      {
        // get the position of the '1', keep everything after it
        int starLoc = s.indexOf("*");
        s = s.substring(starLoc+1).trim();
      }
      else if (s.matches("   1.*"))
      {
        // get the position of the '1', keep everything after it
        int oneLoc = s.indexOf("1");
        s = s.substring(oneLoc+1).trim();
      }
      else if (s.matches("\\[\\[.*"))
      {
        // TODO handle hyperlinks better; the link text is valid; just whack it all for now
        // format: [[link][text]]
        int linkStart = s.indexOf("[[");
        int linkStop = s.indexOf("]]");
        String beforeLink = s.substring(0,linkStart);
        String afterLink = s.substring(linkStop+1, s.length()-1);
        s = beforeLink + " " + afterLink;
      }
      else if (s.matches("<img src=.*"))
      {
        // TODO handle images
        continue;
      }

      // get rid of all wiki markup characters
      s = s.replaceAll("_", "");
      s = s.replaceAll("=", "");
      s = s.replaceAll("\\*", "");
      s = s.replaceAll("\\+\\+", "");   // "++String" syntax
      
      // made it here, so add the string to our list of strings
      strings.add(s);
    }
    
    // return our list of cleaned up strings
    return strings;
  }
  
    
  private static String convertListOfWikiStringsToLatexString(List listOfStrings)
  {
    StringBuffer latexStringBuffer = new StringBuffer();
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
      currentString = convertSimpleWikiTags(currentString);

      currentLevel = getIndentationLevel(currentString);

      lastIndentationType = indentationType;
      indentationType = getIndentationType(currentString);
      if (indentationType==NO_INDENT_TYPE) indentationType = lastIndentationType;

      if ( currentLevel==0  && currentLevel==lastLevel )
      {
        currentString = convertWikiTagsToLatexTags(currentString);
        latexStringBuffer.append(currentString + "\n");
      }
      else if ( currentLevel<lastLevel )
      {
        //differenceBetweenLevels = lastLevel - currentLevel;
        int tmpLastLevel = lastLevel;
        for ( int i=currentLevel; i<tmpLastLevel; tmpLastLevel--)
        {
          latexStringBuffer.append(getPreStringSpacing(tmpLastLevel) + "\\end{"+getIndentationTag(indentationType) +"}\n");
        }
        latexStringBuffer.append(getPreStringSpacing(currentLevel) + convertCurrentLineToItemizedLine(currentString,indentationType));
      }
      else if ( currentLevel!=0  && currentLevel==lastLevel )
      {
        latexStringBuffer.append(getPreStringSpacing(currentLevel) + convertCurrentLineToItemizedLine(currentString,indentationType));
      }
      else if ( currentLevel!=0  && currentLevel>lastLevel )
      {
        latexStringBuffer.append(getPreStringSpacing(currentLevel) + "\\begin{"+getIndentationTag(indentationType) +"}\n");
        latexStringBuffer.append(getPreStringSpacing(currentLevel) + convertCurrentLineToItemizedLine(currentString,indentationType));
      }
      lastLevel = currentLevel;
    }

    if ( lastLevel>0 )
    {
      for ( int i=0; i<lastLevel; lastLevel--)
      {
        latexStringBuffer.append(getPreStringSpacing(lastLevel) + "\\end{"+getIndentationTag(indentationType) +"}\n");
      }
    }

    return latexStringBuffer.toString();

  }

  private static String getIndentationTag(int indentationType)
  {
    if (indentationType == ITEMIZE_INDENT_TYPE) return "itemize";
    if (indentationType == ENUMERATE_INDENT_TYPE) return "enumerate";
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
    return "\\item " + currentString.substring(positionOfFirstItemCharacter+2) + "\n";
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
    int characterPosition = wikiString.indexOf('*');
    //if (characterPosition < 3) return 0;
    for (int i=0; i<validPositions.length; i++)
    {
      if (characterPosition==validPositions[i])
      {
        if (noCharactersPrecedeItemCharacter(wikiString,i)) return i+1;
      }
    }
    characterPosition = wikiString.indexOf('1');
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
    if ( characterIsInIndentPosition('*',wikiString) ) return ITEMIZE_INDENT_TYPE;
    if ( characterIsInIndentPosition('1',wikiString) ) return ENUMERATE_INDENT_TYPE;
    return NO_INDENT_TYPE;
  }

  private static boolean characterIsInIndentPosition(char wikiCharacter, String wikiString)
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
      //System.err.println(":" + currentLine + ":");
    }
    return listOfStrings;
  }

/*
  private static boolean isValidAsteriskPosition(String wikiString)
  {
    int asteriskPosition = wikiString.indexOf('*');
    if (asteriskPosition < 3) return false;

    int validAsteriskPositions[] = {3,6,9,12,15,18,21,24,27,30,33};
    boolean findMatch = false;
    for (int i=0; i<validAsteriskPositions.length; i++)
    {
      if (asteriskPosition==validAsteriskPositions[i]) findMatch=true;
    }
    return findMatch;
  }
*/

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
    if (aMatcher.find()) return "\\subsubsection{" + aMatcher.group(1) + "}";

    aPattern = Pattern.compile("^---\\+\\+\\+\\s*(.*)[ \t]*$");
    aMatcher = aPattern.matcher(wikiString);
    if (aMatcher.find()) return "\\subsection{" + aMatcher.group(1) + "}";

    aPattern = Pattern.compile("^---\\+\\+\\s*(.*)[ \t]*$");
    aMatcher = aPattern.matcher(wikiString);
    if (aMatcher.find()) return "\\section{" + aMatcher.group(1) + "}";

    aPattern = Pattern.compile("^---\\+\\s*(.*)$");
    aMatcher = aPattern.matcher(wikiString);
    if (aMatcher.find()) return "\\chapter{" + aMatcher.group(1) + "}";

    return wikiString;
  }

  private static String convertSimpleWikiTags(String currentString)
  {
    currentString = convertWikiBoldFixedFontTag(currentString); // must precede convertWikiFixedFontTag
    currentString = convertWikiFixedFontTag(currentString);

    currentString = convertWikiBoldItalicsTag(currentString);  // convert bold + italics tags; must precede italics conversion
    currentString = convertWikiItalicsTag(currentString);  // convert italics tags in a line

    currentString = convertWikiBoldTag(currentString);  // convert bold tags in a line
    return currentString;
  }

  private static String convertWikiBoldTag(String wikiString)
  {
    // the ? in the next line is a reluctant qualifier
/*
    aPattern = Pattern.compile("\\*(.*?)\\*");
    aMatcher = aPattern.matcher(wikiString);
    return aMatcher.replaceAll("\\\\textbf{$1}");
*/
    aPattern = Pattern.compile("\\*(\\S{1}.*?)\\*");
    aMatcher = aPattern.matcher(wikiString);
    return aMatcher.replaceAll("\\\\textbf{$1}");
  }

  private static String convertWikiItalicsTag(String wikiString)
  {
    aPattern = Pattern.compile("_(\\S{1}.*?)_");
    aMatcher = aPattern.matcher(wikiString);
    return aMatcher.replaceAll("\\\\textit{$1}");
  }

  private static String convertWikiBoldItalicsTag(String wikiString)
  {
    aPattern = Pattern.compile("__(\\S{1}.*?)__");
    aMatcher = aPattern.matcher(wikiString);
    return aMatcher.replaceAll("\\\\textit{\\\\textbf{$1}}");
  }

  private static String convertWikiFixedFontTag(String wikiString)
  {
    aPattern = Pattern.compile("=(\\S{1}.*?)=");
    aMatcher = aPattern.matcher(wikiString);
    return aMatcher.replaceAll("\\\\texttt{$1}");
  }

  private static String convertWikiBoldFixedFontTag(String wikiString)
  {
    aPattern = Pattern.compile("==(\\S{1}.*?)==");
    aMatcher = aPattern.matcher(wikiString);
    return aMatcher.replaceAll("\\\\texttt{\\\\textbf{$1}}");
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
