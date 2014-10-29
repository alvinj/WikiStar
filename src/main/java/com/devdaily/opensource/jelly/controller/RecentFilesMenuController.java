package com.devdaily.opensource.jelly.controller;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.devdaily.opensource.jelly.MainFrame;

// TODO At some point i need to do a better job of limiting the number of files
//      in the recent file list.
public class RecentFilesMenuController
{
  private static final String RECENT_FILE_LIST_FILENAME = ".wikistarRecentFilelist";
  private static final int MAX_NUM_FILES_IN_LIST        = 15;
  
  MainFrame mainFrame;
  File recentFileListFile;
  List<String> recentFileList;
  List<JMenuItem> listOfMenuItems = new ArrayList<JMenuItem>();
  
  JMenu recentFilesMenu = new JMenu("Recent Files");
  
  public RecentFilesMenuController(MainFrame mainFrame)
  {
    this.mainFrame = mainFrame;
    try
    {
      loadRecentFilesList();
      initRecentFileListMenuItems();
    }
    catch (IOException e)
    {
      // TODO
    }
  }

  private void initRecentFileListMenuItems()
  {
    int numRecentFiles = recentFileList.size();
    for (int i=0; i<numRecentFiles; i++)
    {
      JMenuItem jmi = buildMenuItem(recentFileList.get(i));
      recentFilesMenu.add(jmi);  // TODO this is weak
      listOfMenuItems.add(jmi);
    }
  }
  
  private JMenuItem buildMenuItem(String filename)
  {
    JMenuItem mi = new JMenuItem(filename);
    mi.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        fileRecentFile_actionPerformed(e);
      }
    });
    return mi;
  }

  private void loadRecentFilesList() throws IOException
  {
    String fullFilename = getDirectoryRecentFileListFilename() + RECENT_FILE_LIST_FILENAME;
    recentFileListFile = new File(fullFilename);
    if (recentFileListFile.exists())
    {
      recentFileList = new ArrayList<String>();
      // read it and create the list; each line is a new filename
      BufferedReader br = new BufferedReader(new FileReader(recentFileListFile));
      String line = null;
      int count = 0;
      while ( (line = br.readLine()) != null )
      {
        // limit the number of files in the list
        if (++count > MAX_NUM_FILES_IN_LIST) break;
        if (!line.trim().equals(""))
        {
          // add the line to the list
          recentFileList.add(line);
        }
      }
      br.close();
    }
    else
    {
      // if it doesn't exist, create it now
      recentFileListFile.createNewFile();
    }
  }

  private String getDirectoryRecentFileListFilename()
  {
    String homeDir = System.getProperty("user.home");
    String fileSep = System.getProperty("file.separator");
    return homeDir + fileSep;
  }
  
  public void addFilenameToRecentFilelist(String currentFilename)
  {
    // don't add names that are already in the list
    if (recentFileList.contains(currentFilename))
    {
      return;
    }
    else
    {
      String recentFileFilename = getDirectoryRecentFileListFilename() + RECENT_FILE_LIST_FILENAME;
      try
      {
        // TODO i'm really tired, and not sure that i'm adding new files properly to the
        // top of the list
        
        // TODO i think i'm also maintain one more list than i really need
        recentFileList.add(0, currentFilename);
        JMenuItem jmi = buildMenuItem(currentFilename);
        
        recentFilesMenu.add(jmi, 0);
        listOfMenuItems.add(0, jmi);
        
        // TODO Need to update the menu items here
        
        // write the list to disk (just replace old file contents); limit to 10 entries
        File file = new File (recentFileFilename);
        FileWriter out = new FileWriter(file);
        int maxFiles = 10;
        if (recentFileList.size() < 10) maxFiles = recentFileList.size();
        for (int i=0; i<maxFiles; i++)
        {
          out.write((String)recentFileList.get(i) + "\n");
        }
        out.close();
      }
      catch (IOException e) {
        // TODO
      }    
    }
  }

  // handle the event of a user selecting a file from the recent file list
  public void fileRecentFile_actionPerformed(ActionEvent e)
  {
    if (e.getSource() instanceof JMenuItem)
    {
      JMenuItem jmi = (JMenuItem)e.getSource();
      String filename = jmi.getText();
      mainFrame.quickFileOpen(filename);
    }
  }
  
  public List<String> getRecentFilesList()
  {
    return this.recentFileList;
  }

  public JMenu getRecentFilesMenu()
  {
    return recentFilesMenu;
  }

}




