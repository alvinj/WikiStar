package com.devdaily.opensource.jelly.controller;

import javax.swing.JOptionPane;

import com.apple.mrj.MRJAboutHandler;
import com.apple.mrj.MRJPrefsHandler;
import com.apple.mrj.MRJQuitHandler;
import com.devdaily.opensource.jelly.MainFrame;

public class MacOSXController 
implements MRJAboutHandler, MRJQuitHandler, MRJPrefsHandler
{
  MainFrame mainFrame;
  
  public MacOSXController(MainFrame mainFrame) 
  {
    this.mainFrame = mainFrame;    
  }

  public void handleAbout()
  {
    JOptionPane.showMessageDialog(null, 
                                  "about", 
                                  "about", 
                                  JOptionPane.INFORMATION_MESSAGE);
  }

  public void handleQuit() throws IllegalStateException
  {
    mainFrame.fileExitMenuItem_actionPerformed(null);
  }

  public void handlePrefs() throws IllegalStateException
  {
    JOptionPane.showMessageDialog(null, 
                                  "prefs", 
                                  "prefs", 
                                  JOptionPane.INFORMATION_MESSAGE);
  }

}
