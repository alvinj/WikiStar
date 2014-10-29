package com.devdaily.opensource.jelly.view;

import java.awt.Container;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import com.devdaily.opensource.jelly.MainFrame;
import com.devdaily.opensource.jelly.controller.OpenRecentFileController;
import com.devdaily.utils.swing.DDDialog;
import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.*;

public class OpenRecentFileDialog
extends DDDialog
{
  // our things
  DefaultListModel listModel = new DefaultListModel();
  JList jlist = new JList(listModel);
  JScrollPane scrollPane = new JScrollPane(jlist);
  JButton okButton = new JButton("OK");
  JButton cancelButton = new JButton("Cancel");
  boolean okButtonWasSelected = false;
  // other things we know about
  MainFrame mainFrame;
  OpenRecentFileController openRecentFileController;

  public OpenRecentFileDialog(MainFrame mainFrame,
      OpenRecentFileController openRecentFileController)
  {
    this.mainFrame = mainFrame;
    this.openRecentFileController = openRecentFileController;
    jlist.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    doLayoutForm();
    addShortcutListenersToJList();
    addKeyboardShortcutsToContainer();
  }

  private void doLayoutForm()
  {
    FormLayout layout = new FormLayout( 
        "fill:pref, 3dlu, fill:80dlu:grow, 3dlu, left:pref",
        "pref, 6dlu, pref, 6dlu, pref");
    JPanel panel = new JPanel();
    DefaultFormBuilder builder = new DefaultFormBuilder(layout, panel); 
    builder.setDefaultDialogBorder();
    CellConstraints cc = new CellConstraints();

    builder.addSeparator("Recent Files", cc.xyw(1,1,5));
    builder.add(scrollPane, cc.xyw(1,3,5));
    builder.add(ButtonBarFactory.buildCenteredBar(cancelButton, okButton), cc.xy(3, 5, CellConstraints.CENTER, CellConstraints.DEFAULT));
    //builder.add(okButton, cc.xy(3,5,CellConstraints.CENTER, CellConstraints.DEFAULT));
    this.setContentPane(builder.getPanel());
  }

  private void addShortcutListenersToJList()
  {
    jlist.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent me) {
        if (me.getClickCount() == 2) {
          okButton.doClick();
          me.consume();
          }
        }
       });
    jlist.addKeyListener(new KeyAdapter() {
      public void keyReleased(KeyEvent ke) {
        if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
          okButton.doClick();
        }
       }});
  }
  
  // helps to listen when i use the "sheet" ui on the mac
  private void addKeyboardShortcutsToContainer()
  {
    Container c = this.getContentPane();
    c.addKeyListener(new KeyAdapter() {
      public void keyReleased(KeyEvent ke) {
        if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
          okButton.doClick();
        }
        if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
          cancelButton.doClick();
        }
       }});
  }
  public void setVisible(boolean b)
  {
    okButtonWasSelected = false;
    super.setVisible(b);
  }

  public JButton getOKButton()
  {
    return okButton;
  }

  public JButton getCancelButton()
  {
    return cancelButton;
  }

  public JList getJList()
  {
    return jlist;
  }

  @Override
  public void escapeCommandPerformed(KeyEvent e)
  {
    okButtonWasSelected = false;
    cancelButton.doClick();
  }

  @Override
  public void okCommandPerformed(KeyEvent e)
  {
    okButtonWasSelected = true;
    okButton.doClick();
  }

  public void setListOfFiles(List<String> recentFilesList)
  {
    listModel.clear();
    for (int i=0; i<recentFilesList.size(); i++)
    {
      listModel.addElement(recentFilesList.get(i));
    }
  }

  public boolean okButtonWasSelected()
  {
    return okButtonWasSelected;
  }

  public String getSelectedFilename()
  {
    return (String)jlist.getSelectedValue();
  }

  // tester
  public static void main(String[] args)
  {
    JFrame f = new JFrame();
    DDDialog d = new OpenRecentFileDialog(null, null);
    d.pack();
    d.setVisible(true);
  }
  
}


