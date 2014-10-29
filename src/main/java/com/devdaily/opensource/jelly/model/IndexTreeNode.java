package com.devdaily.opensource.jelly.model;

import javax.swing.tree.*;

public class IndexTreeNode extends DefaultMutableTreeNode
{

  private int textAreaPosition;
  private String nameOfChapter;

  public String toString()
  {
    return nameOfChapter;
  }

  public int getTextAreaPosition()
  {
    return textAreaPosition;
  }

  public void setTextAreaPosition(int textAreaPosition)
  {
    this.textAreaPosition = textAreaPosition;
  }

  public String getNameOfChapter()
  {
    return nameOfChapter;
  }

  public void setNameOfChapter(String nameOfChapter)
  {
    this.nameOfChapter = nameOfChapter;
  }

  public IndexTreeNode()
  {
  }

/*
  public int getNodeType()
  {
    return 0;
  }

  protected void populatePopupMenu()
  {
  }
*/

}