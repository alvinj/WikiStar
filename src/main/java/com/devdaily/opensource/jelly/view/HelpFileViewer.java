package com.devdaily.opensource.jelly.view;

import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.text.*;
import javax.swing.event.*;


public class HelpFileViewer extends JFrame
{
  JPanel panel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JButton closeButton = new JButton();
  BorderLayout borderLayout2 = new BorderLayout();
  JScrollPane jScrollPane1 = new JScrollPane();
  JEditorPane jEditorPane1 = new JEditorPane();

  private static final String HELP_FILE_DIR = "latexHelpFiles";

  public HelpFileViewer(Frame frame, String title, String fileToLoadIntoViewer)
  {
    try
    {
      jbInit(title,fileToLoadIntoViewer);
      pack();
//      this.addWindowListener(new WindowAdapter() {
//        public void windowGainedFocus(WindowEvent e) {
//          closeButton.requestFocusInWindow();
//        }
//      });
      // @todo -- this seems to highlight the button on the mac
      closeButton.requestFocusInWindow();
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
  }

  void jbInit(final String title, String fileToLoadIntoViewer) throws Exception
  {
    this.setTitle(title);

    panel1.setLayout(borderLayout1);
    jPanel2.setLayout(borderLayout2);
    closeButton.setText("Close");
    closeButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        closeButton_actionPerformed(e);
      }
    });
    jEditorPane1.setContentType("text/html");
    jEditorPane1.setText("");
    jEditorPane1.setEditable(false);
    getContentPane().add(panel1);
    panel1.add(jPanel1, BorderLayout.SOUTH);
    jPanel1.add(closeButton, null);
    panel1.add(jPanel2, BorderLayout.CENTER);
    jPanel2.add(jScrollPane1, BorderLayout.CENTER);
    jScrollPane1.getViewport().add(jEditorPane1, null);
    loadFileIntoEditorPane(fileToLoadIntoViewer.trim());
    this.validate();
  }
  
  private void loadFileIntoEditorPane(String filename)
  {
    //File f = new File(HELP_FILE_DIR + "/" + filename);
    try
    {
      //System.err.println(HELP_FILE_DIR + "/" + filename);
      //System.err.println("f.exists:  " + f.exists());
      //System.err.println("f.canRead: " + f.canRead());
      //Reader reader = new FileReader(f);
      //Reader reader = new FileReader(filename);
      //String content = getFileAsString(reader);
      filename = HELP_FILE_DIR + "/" + filename;
      String content = readFromJARFile(filename);
      jEditorPane1.setText(content);
      jEditorPane1.setCaretPosition(0);
      jEditorPane1.validate();
    }
    catch (IOException ioe)
    {
      jEditorPane1.setText("Sorry, could not read help file.");
    }
  }
  
  public String readFromJARFile(String filename)
  throws IOException
  {
    InputStream is = getClass().getResourceAsStream(filename);
    InputStreamReader isr = new InputStreamReader(is);
    BufferedReader br = new BufferedReader(isr);
    StringBuffer sb = new StringBuffer();
    String line;
    while ((line = br.readLine()) != null) 
    {
      sb.append(line);
    }
    br.close();
    isr.close();
    is.close();
    return sb.toString();
  }

  private String getFileAsString(Reader reader)
  throws IOException
  {
    StringBuffer sb = new StringBuffer();
    char[] b = new char[8192];
    int n;
    while ( (n=reader.read(b)) > 0)
    {
      sb.append(b,0,n);
    }
    return sb.toString();
  }

  void closeButton_actionPerformed(ActionEvent e)
  {
    this.dispose();
  }

/*
  void jEditorPane1_hyperlinkUpdate(HyperlinkEvent e)
  {
    if ( e.getEventType().toString().equals("ACTIVATED") )
    {
      try
      {
        loadFileIntoEditorPane( e.getDescription().trim() );
        //jEditorPane1.setPage(HELP_FILE_DIR + "/" + e.getDescription());
      }
      catch (Exception ex)
      {
        jEditorPane1.setText("Sorry, an error occurred accessing that hyperlink.");
      }
    }
  }
*/
}