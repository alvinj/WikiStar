package com.devdaily.opensource.jelly.model;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

public class RGAFileFilter extends FileFilter
{

  // Accept all directories and all .tex and .latex files.
  public boolean accept(File f)
  {
    if (f.isDirectory())
    {
        return true;
    }

    String extension = getExtension(f);
    if (extension != null)
    {
      //if (extension.equals("tex") || extension.equals("latex") || extension.equals("rga") )
      if ( extension.equals("rga") )
      {
          return true;
      }
      else
      {
          return false;
      }
    }

    return false;
  }

  // The description of this filter
  public String getDescription()
  {
      return "RGA Files (.rga)";
  }

  public static String getExtension(File f)
  {
    String ext = null;
    String s = f.getName();
    int i = s.lastIndexOf('.');

    if (i > 0 &&  i < s.length() - 1) {
        ext = s.substring(i+1).toLowerCase();
    }
    return ext;
  }
}
