package com.devdaily.opensource.jelly.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;

public class FileUtils
{
  /**
   * Opens and reads a file, and returns the contents as one String.
   */
  public static String readFileAsString(String filename) throws Exception
  {
    try
    {
      BufferedReader reader = new BufferedReader(new FileReader(filename));
      String line;
      StringBuilder sb = new StringBuilder();
      while ((line = reader.readLine()) != null)
      {
        sb.append(line + "\n");
      }
      reader.close();
      return sb.toString();
    }
    catch (Exception e)
    {
      System.err.format("Exception occurred trying to read '%s'.", filename);
      e.printStackTrace();
      throw e;
    }
  }

  /**
   * Open and read a file, and return the lines in the file as a list of
   * Strings.
   */
  public static List<String> readFileAsListOfStrings(String filename) throws Exception
  {
    List<String> records = new ArrayList<String>();
    try
    {
      BufferedReader reader = new BufferedReader(new FileReader(filename));
      String line;
      while ((line = reader.readLine()) != null)
      {
        records.add(line);
      }
      reader.close();
      return records;
    }
    catch (Exception e)
    {
      System.err.format("Exception occurred trying to read '%s'.", filename);
      e.printStackTrace();
      throw e;
    }
  }

  /**
   * Reads a "properties" file, and returns it as a Map (a collection of key/value pairs).
   * 
   * @param filename
   * @param delimiter
   * @return
   * @throws Exception
   */
  public static Map<String, String> readPropertiesFileAsMap(String filename, String delimiter)
  throws Exception
  {
    Map<String, String> map = new HashMap();
    try
    {
      BufferedReader reader = new BufferedReader(new FileReader(filename));
      String line;
      while ((line = reader.readLine()) != null)
      {
        if (line.trim().length()==0) continue;
        if (line.charAt(0)=='#') continue;
        // assumption here is that proper lines are like "String : http://xxx.yyy.zzz/foo/bar",
        // and the ":" is the delimiter
        int delimPosition = line.indexOf(delimiter);
        String key = line.substring(0, delimPosition-1).trim();
        String value = line.substring(delimPosition+1).trim();
        map.put(key, value);
      }
      reader.close();
      return map;
    }
    catch (Exception e)
    {
      System.err.format("Exception occurred trying to read '%s'.", filename);
      e.printStackTrace();
      throw e;
    }
  }

  public static Properties readPropertiesFile(String filename)
  throws IOException
  {
    Properties properties = new Properties();
    try
    {
      properties.load(new FileInputStream(filename));
      return properties;
    }
    catch (IOException e)
    {
      e.printStackTrace();
      throw e;
    }
  }

}












