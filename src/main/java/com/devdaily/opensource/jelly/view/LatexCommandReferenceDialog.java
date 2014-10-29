package com.devdaily.opensource.jelly.view;

import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.text.*;
import javax.swing.event.*;


public class LatexCommandReferenceDialog extends JFrame
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


  //public LatexCommandReferenceDialog(Frame frame, String title, boolean modal)
  public LatexCommandReferenceDialog(Frame frame, String title)
  {
    //super(frame, title, modal);
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

/*
  // not used any longer (rm from here after testing and cvs check in)
  public LatexCommandReferenceDialog()
  {
    this(null, "", false);
  }
*/
  void jbInit() throws Exception
  {
    panel1.setLayout(borderLayout1);
    jPanel1.setBackground(new Color(204, 204, 194));
    jPanel2.setBackground(new Color(204, 194, 194));
    jPanel2.setLayout(borderLayout2);
    closeButton.setText("Close");
    closeButton.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        closeButton_actionPerformed(e);
      }
    });
    jEditorPane1.setContentType("text/html");
    jEditorPane1.setText("");
    jEditorPane1.setEditable(false);
    jEditorPane1.addHyperlinkListener(new javax.swing.event.HyperlinkListener()
    {
      public void hyperlinkUpdate(HyperlinkEvent e)
      {
        jEditorPane1_hyperlinkUpdate(e);
      }
    });
    this.setTitle("Help On LaTeX Commands");
    getContentPane().add(panel1);
    panel1.add(jPanel1, BorderLayout.SOUTH);
    jPanel1.add(closeButton, null);
    panel1.add(jPanel2, BorderLayout.CENTER);
    jPanel2.add(jScrollPane1, BorderLayout.CENTER);
    jScrollPane1.getViewport().add(jEditorPane1, null);
    loadFileIntoEditorPane("ltx-2.html");
    this.validate();
  }

  private void loadFileIntoEditorPane(String filename)
  {
    try
    {
      /** @todo should do this as a url, not the way I'm currently doing it below. */
      //jEditorPane1.setPage("file:///F:/CvsProjects/DevDaily/DevDaily/Jelly/jar/"+HELP_FILE_DIR+"/"+filename);
      //Reader reader = new FileReader(HELP_FILE_DIR + "/" + filename);
      //String content = getFileAsString(reader);
      String content = readFromJARFile(HELP_FILE_DIR + "/" + filename);
      jEditorPane1.setText(content);
      jEditorPane1.setCaretPosition(0);
      jEditorPane1.validate();
    }
    catch (IOException ioe)
    {
      jEditorPane1.setText("Sorry, could not read help file");
    }
  }

  /** @todo: This method has been copied from HelpFileViewer.java (put it in only one place!) */
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
    System.gc();
  }

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
}