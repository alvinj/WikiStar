package com.devdaily.opensource.jelly.view;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import com.devdaily.opensource.jelly.MainFrame;
import com.devdaily.utils.swing.DDDialog;

import java.util.*;

public class FindDialog extends DDDialog
{
  JPanel panel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel southPanel = new JPanel();
  JButton findButton = new JButton();
  JButton closeButton = new JButton();
  JPanel centerPanel = new JPanel();
  JLabel findLabel = new JLabel();
  JCheckBox matchCaseCheckBox = new JCheckBox();
  JCheckBox wholeWordCheckBox = new JCheckBox();
  JRadioButton startAtTopRadioButton = new JRadioButton();
  JRadioButton wrapAroundRadioButton = new JRadioButton();

  MainFrame callingFrame = null;
  int lastSearchPosition;
  JComboBox findComboBox = new JComboBox();
  java.util.List stuffSearchedForBefore;
  GridBagLayout gridBagLayout1 = new GridBagLayout();

  public FindDialog(Frame frame, String title, boolean modal)
  {
    super(frame, title, modal);
    callingFrame = (MainFrame)frame;
    try
    {
      jbInit();
      pack();
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
  }

  public FindDialog()
  {
    this(null, "", false);
  }

  void jbInit() throws Exception
  {
    panel1.setLayout(borderLayout1);
    panel1.setBackground(new Color(204, 204, 214));
    findButton.setText("Find");
    findButton.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        findButton_actionPerformed(e);
      }
    });
    closeButton.setText("Close");
    closeButton.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        closeButton_actionPerformed(e);
      }
    });
    this.setResizable(false);
    this.setTitle("Find ...");
    centerPanel.setLayout(gridBagLayout1);
    findLabel.setText("Find: ");
    matchCaseCheckBox.setText("Case Sensitive");
    wholeWordCheckBox.setEnabled(false);
    wholeWordCheckBox.setText("Whole Word");
    startAtTopRadioButton.setText("Start at Top");
    wrapAroundRadioButton.setEnabled(false);
    wrapAroundRadioButton.setText("Wrap Around");
    findComboBox.setEditable(true);
    findComboBox.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        findComboBox_actionPerformed(e);
      }
    });
    getContentPane().add(panel1);
    panel1.add(southPanel, BorderLayout.SOUTH);
    southPanel.add(findButton, null);
    southPanel.add(closeButton, null);
    panel1.add(centerPanel, BorderLayout.CENTER);
    centerPanel.add(findLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(15, 21, 0, 0), 0, 0));
    centerPanel.add(matchCaseCheckBox, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(8, 15, 0, 0), 0, 0));
    centerPanel.add(wholeWordCheckBox, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 15, 20, 14), 0, 0));
    centerPanel.add(startAtTopRadioButton, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(8, 12, 0, 44), 0, 0));
    centerPanel.add(wrapAroundRadioButton, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 12, 20, 35), 0, 0));
    centerPanel.add(findComboBox, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(15, 16, 0, 35), 94, -4));

    populateComboBox();
    findComboBox.requestDefaultFocus();
  }

  private void populateComboBox()
  {
    stuffSearchedForBefore = callingFrame.getStuffSearchForPreviously();
    int listSize = stuffSearchedForBefore.size();
    int currentPos = listSize-1;
    while ( currentPos >= 0 )
    {
      findComboBox.addItem( stuffSearchedForBefore.get(currentPos) );
      currentPos--;
    }
  }

  void closeButton_actionPerformed(ActionEvent e)
  {
    lastSearchPosition = 0;
    this.dispose();
  }

  private void doSearch()
  {
    String textToSearchFor = (String)findComboBox.getSelectedItem();
    callingFrame.rememberItemSearchedFor(textToSearchFor);
    boolean caseSensitiveSearch = this.matchCaseCheckBox.isSelected();
    if ( startAtTopRadioButton.isSelected() )
    {
      lastSearchPosition = 0;
    }
    lastSearchPosition = callingFrame.findTextAndReturnLocation( textToSearchFor,
                                                                 lastSearchPosition,
                                                                 caseSensitiveSearch );
  }

  void findButton_actionPerformed(ActionEvent e)
  {
    doSearch();
  }

  void findComboBox_actionPerformed(ActionEvent e)
  {
    doSearch();
  }

  @Override
  public void escapeCommandPerformed(KeyEvent e)
  {
    closeButton_actionPerformed(null);
  }

  @Override
  public void okCommandPerformed(KeyEvent e)
  {
    findButton_actionPerformed(null);
  }
}