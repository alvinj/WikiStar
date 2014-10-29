package com.devdaily.opensource.jelly;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import java.lang.reflect.InvocationTargetException;

import com.apple.eawt.AboutHandler;
import com.apple.eawt.AppEvent.AboutEvent;
import com.apple.eawt.AppEvent.QuitEvent;
import com.apple.eawt.Application;
import com.apple.eawt.ApplicationEvent;
import com.apple.eawt.QuitHandler;
import com.apple.eawt.QuitResponse;
import com.apple.mrj.MRJApplicationUtils;
import com.devdaily.opensource.jelly.controller.MacOSXController;
import com.devdaily.opensource.jelly.view.SplashScreen;

public class Main
{
  boolean packFrame = false;

  private SplashScreen screen;
  private MainFrame mainFrame;

  Application theApplication = new Application();

  /**
   * This method handles the callback from the DockBarAdapter.
   */
  public void handleOpenFileEvent(final ApplicationEvent applicationEvent)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        mainFrame.quickFileOpen(applicationEvent.getFilename());
      }
    });
  }

  /** Construct the application */
  public Main()
  {
    // sets up the mac dock event stuff
    DockBarAdapter dockBarAdapter = new DockBarAdapter(this);
    theApplication.addApplicationListener(dockBarAdapter);

    // fire up the splash screen
    splashScreenInit();
    screen.setProgress("Preparing ...", 10);

    mainFrame = new MainFrame();
    // supposed to make frame lighter; didn't work
    //frame.getRootPane().putClientProperty("Window.alpha", new Float(0.94f));

    screen.setProgress("Packing frame ...", 20);
    // not packing here any more ...

    screen.setProgress("Doing nothing, really ...", 30);
    configureForMacOSXIfNeeded(mainFrame);

    splashScreenDestruct();
    mainFrame.setVisible(true);
    mainFrame.requestFocus();
  }

  private void configureForMacOSXIfNeeded(MainFrame frame)
  {
    theApplication.setQuitHandler(new QuitHandler() {
      @Override
      public void handleQuitRequestWith(QuitEvent arg0, QuitResponse arg1) {
        mainFrame.fileExitMenuItem_actionPerformed(null);
      }
    });
//    boolean isMacOS = System.getProperty("mrj.version") != null;
//    if (isMacOS)
//    {
//      // TODO this is a kludge b/c the old way doesn't work any more
//      // AJA/2014 - this no longer works
//      //MacOSXController macController = new MacOSXController(frame);
//      //MRJApplicationUtils.registerAboutHandler(macController);
//      //MRJApplicationUtils.registerPrefsHandler(macController);
//      //MRJApplicationUtils.registerQuitHandler(macController);
//    }
  }

  private void splashScreenDestruct()
  {
    screen.setScreenVisible(false);
  }

  private void splashScreenInit()
  {
    try
    {
      SwingUtilities.invokeAndWait(new Runnable()
      {
        public void run()
        {
          ImageIcon myImage = new ImageIcon(
              com.devdaily.opensource.jelly.Main.class
                  .getResource("springtrng.gif"));
          screen = new SplashScreen(myImage);
          screen.setLocationRelativeTo(null);
          screen.setProgressMax(100);
          screen.setScreenVisible(true);
        }
      });
    }
    catch (InvocationTargetException ex)
    {
      System.err.println("Got an InvocationTargetException");
      System.err.println(ex.getMessage());
    }
    catch (InterruptedException ex)
    {
      System.err.println("Got an InterruptedException");
      System.err.println(ex.getMessage());
    }
  }

  /** Main method */
  public static void main(String[] args)
  {
    try
    {
      //System.setProperty("apple.awt.brushMetalLook", "true");
      System.setProperty("apple.laf.useScreenMenuBar", "true");
      System.setProperty("com.apple.mrj.application.apple.menu.about.name", "WikiTeX");
      System.setProperty("apple.awt.textantialiasing", "true");
      // ref: http://developer.apple.com/releasenotes/Java/Java142RNTiger/1_NewFeatures/chapter_2_section_3.html
      System.setProperty("apple.awt.graphics.EnableQ2DX","true");
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      // set "small tabs". i didn't see any difference in tab sizes though.
      // System.setProperty("com.apple.macos.smallTabs", "true");
      // @todo: look this up, it didn't do anything
      // System.setProperty("com.apple.mrj.application.live-resize", "false");
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    new Main();
  }
}