package com.devdaily.opensource.rgadoc.controller;

import com.devdaily.opensource.jelly.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Purpose: Read RGA file contents from stdin, and write LaTeX file contents
 * to stdout.
 */
public class RGA2LatexConverter
{
  public static void main(String[] args) throws IOException
  {
    new RGA2LatexConverter();
  }

  public RGA2LatexConverter() throws IOException
  {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    String input;
    StringBuffer sb = new StringBuffer();
    while ( (input=br.readLine()) != null )
    {
      //System.err.println("line: " + input);
      sb.append(input + "\n");
    }

    String latexOutput = StringUtils.createLatexStringFromWikiString(sb.toString());
    System.out.println(latexOutput);
  }
}
