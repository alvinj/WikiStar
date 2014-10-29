package com.devdaily.opensource.jelly.controller;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.ListSelectionModel;
import com.devdaily.opensource.jelly.MainFrame;
import com.devdaily.opensource.jelly.view.HyperlinkDialog;
import com.devdaily.opensource.jelly.view.OpenRecentFileDialog;

public class OpenRecentFileController
{
  OpenRecentFileDialog openRecentFileDialog;
  MainFrame mainFrame;

  JButton okButton;
  JButton cancelButton;
  List<String> recentFilesList;
 
  public OpenRecentFileController(MainFrame mainFrame)
  {
    this.mainFrame = mainFrame;
    openRecentFileDialog = new OpenRecentFileDialog(mainFrame, this);
    openRecentFileDialog.setModal(true);
    openRecentFileDialog.setLocationRelativeTo(mainFrame);
    okButton = openRecentFileDialog.getOKButton();
    cancelButton = openRecentFileDialog.getCancelButton();
    addListenersToButtons();
  }

  private void addListenersToButtons()
  {
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
    mainFrame.hideSheet();
  }

  public void doOkButtonAction()
  {
    mainFrame.hideSheet();
    String filename = openRecentFileDialog.getSelectedFilename();
    if (filename==null)
    {
      // TODO this is an error
    }
    else
    {
      mainFrame.quickFileOpen(filename);
    }
  }

  public void doDisplayDialogAction()
  {
    recentFilesList = mainFrame.getRecentFilesList();
    openRecentFileDialog.setListOfFiles(recentFilesList);
    openRecentFileDialog.pack();
    mainFrame.showJDialogAsSheet(openRecentFileDialog);
  }

}
