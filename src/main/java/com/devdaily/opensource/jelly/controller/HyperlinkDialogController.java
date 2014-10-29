package com.devdaily.opensource.jelly.controller;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import com.devdaily.opensource.jelly.MainFrame;
import com.devdaily.opensource.jelly.view.HyperlinkDialog;

public class HyperlinkDialogController 
{
  HyperlinkDialog hyperlinkDialog;
  MainFrame mainFrame;
  String url;
  String text;
  String altText;
  String selectedText;

  JButton okButton;
  JButton cancelButton;
 
  public HyperlinkDialogController(MainFrame mainFrame)
  {
    this.mainFrame = mainFrame;
    hyperlinkDialog = new HyperlinkDialog(mainFrame, this);
    hyperlinkDialog.setModal(true);
    hyperlinkDialog.setLocationRelativeTo(mainFrame);
    hyperlinkDialog.setTitle("Hyperlink Properties");
    okButton = hyperlinkDialog.getOkButton();
    cancelButton = hyperlinkDialog.getCancelButton();

    okButton.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        doOkButtonAction();
      }
    });

    cancelButton.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        doCancelButtonAction();
      }
    });

  }
  
  public void doCancelButtonAction()
  {
    // OLD
    //mainFrame.hideSheet();
    
    hyperlinkDialog.setVisible(false);
    hyperlinkDialog.getUrlTextField().setText("");
    hyperlinkDialog.getAltTextTextField().setText("");
    hyperlinkDialog.getTextTextField().setText("");
    hyperlinkDialog.dispose();
  }

  public void doOkButtonAction()
  {
    // OLD
    //mainFrame.hideSheet();
    
    // NEW
    hyperlinkDialog.setVisible(false);

    altText = hyperlinkDialog.getAltTextTextField().getText();
    text = hyperlinkDialog.getTextTextField().getText();
    url = hyperlinkDialog.getUrlTextField().getText();
    //@TODO -- validate these
    // should add a status bar to the HyperlinkDialog to allow validation feedback,
    // or use the jgoodies stuff.
    if (!url.trim().equals("") && !text.trim().equals(""))
    {
      String pre = "[[" + url + "][";
      String post = "]]";
      mainFrame.replaceSelectedText(pre, text, post);
    }
    hyperlinkDialog.dispose();
  }
  
  public void showDialog()
  {
    // pause for effect, then show the sheet
    // OLD
    //try {Thread.sleep(200);}
    //catch (InterruptedException ie) {}
    
    String selectedText = mainFrame.getRgaEditingArea().getSelectedText();
    if (selectedText != null)
    {
      hyperlinkDialog.getTextTextField().setText(selectedText);
    }
    else
    {
      hyperlinkDialog.getTextTextField().setText("");
    }
    hyperlinkDialog.getUrlTextField().setText("");
    hyperlinkDialog.getAltTextTextField().setText("");
    
    // OLD
    //mainFrame.showJDialogAsSheet(hyperlinkDialog);

    // NEW
    hyperlinkDialog.setLocationRelativeTo(mainFrame);

    // this solves the problem where the dialog was not getting
    // focus the second time it was displayed
    SwingUtilities.invokeLater(new Runnable(){  
      public void run(){  
        hyperlinkDialog.getUrlTextField().requestFocusInWindow();
      }
    });

    hyperlinkDialog.setModal(true);
    hyperlinkDialog.setVisible(true);
  }


//  public void showDialog()
//  {
//    String selectedText = mainFrame.getRgaEditingArea().getSelectedText();
//    if (selectedText != null)
//    {
//      hyperlinkDialog.getTextTextField().setText(selectedText);
//    }
//    else
//    {
//      hyperlinkDialog.getTextTextField().setText("");
//    }
//    hyperlinkDialog.getUrlTextField().setText("");
//    hyperlinkDialog.getAltTextTextField().setText("");
//    hyperlinkDialog.setVisible(true);
//  }

}
