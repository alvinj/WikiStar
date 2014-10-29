package com.devdaily.opensource.jelly;

import java.awt.event.ActionListener;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;

/**
 *  This class currently listens for the CRTL-F11 keystroke.
 */
public class MakeFullScreenListener implements ActionListener
{
  MainFrame mainFrame;
  private boolean isFullScreen = true;

  public MakeFullScreenListener(MainFrame mainFrame)
  {
    this.mainFrame = mainFrame;
  }

  public void actionPerformed(ActionEvent ae)
  {
    if ( isFullScreen )
    {
      mainFrame.hideAllAdornments();
      mainFrame.repaint();
      isFullScreen = false;
    }
    else
    {
      mainFrame.showAllAdornments();
      mainFrame.repaint();
      isFullScreen = true;
    }
  }
}
