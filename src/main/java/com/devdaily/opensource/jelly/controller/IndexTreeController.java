package com.devdaily.opensource.jelly.controller;

import com.devdaily.opensource.jelly.model.IndexTreeNode;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import java.util.LinkedList;
import java.util.List;
import java.awt.*;

public class IndexTreeController
{
  JTree rgaDocIndexTree;
  JTextArea rgaEditingArea;
  DefaultMutableTreeNode root = new DefaultMutableTreeNode("Chapters");
  TreeModel treeModel;
  List listOfIndexes = new LinkedList();

  private IndexTreeController()
  {
  }

  public IndexTreeController(JTree rgaDocIndexTree,JTextArea rgaEditingArea)
  {
    this.rgaDocIndexTree = rgaDocIndexTree;
    this.rgaEditingArea = rgaEditingArea;
    treeModel = new DefaultTreeModel(root);
    rgaDocIndexTree.setModel(treeModel);
  }

  private void findPositionOfChapters()
  {
    // @todo need to parse for both "\\CHAPTER" (latex) and "---++" (wiki)
    String textToFind = "\\CHAPTER";
    String currentText = rgaEditingArea.getText();
    String currentTextUpperCase = currentText.toUpperCase();
    listOfIndexes.clear();
    int nextIndex = 0;

    while (true)
    {
      nextIndex = currentTextUpperCase.indexOf(textToFind,nextIndex+1);
      if (nextIndex >= 0)
      {
        listOfIndexes.add(new Integer(nextIndex));
        IndexTreeNode newNode = new IndexTreeNode();
        newNode.setTextAreaPosition(nextIndex);
        int locOfNextOpeningParen = currentText.indexOf("{",nextIndex);
        int locOfNextClosingParen = currentText.indexOf("}",nextIndex);
        if ( ((locOfNextOpeningParen-nextIndex) < 10) && ((locOfNextOpeningParen-nextIndex) < 40) )
        {
          newNode.setNameOfChapter(currentText.substring(locOfNextOpeningParen+1,locOfNextClosingParen));
        }
        else
        {
          newNode.setNameOfChapter("Unknown");
        }

        root.add(newNode);
      }
      else
      {
        break;
      }
    }

  }

  public void updateTree()
  {
    root.removeAllChildren();
    findPositionOfChapters();
    //rebuildTreeNodesFromLastSearch();
    rgaDocIndexTree.updateUI();
  }

/*
  private void rebuildTreeNodesFromLastSearch()
  {
    for (int i=0; i<listOfIndexes.size(); i++)
    {
      int location = ((Integer)listOfIndexes.get(i)).intValue();
      IndexTreeNode newNode = new IndexTreeNode();
      newNode.setTextAreaPosition(location);
      root.add(newNode);
    }
  }
*/

  public void gotoTextLocation(int textLocation)
  {
    rgaEditingArea.setCaretPosition(textLocation);
    //rgaEditingArea.setSelectionColor(Color.ORANGE);
    rgaEditingArea.setSelectionStart(textLocation);
    rgaEditingArea.setSelectionEnd(textLocation+8);
    rgaEditingArea.repaint();
    rgaEditingArea.requestFocus();

  }
}
