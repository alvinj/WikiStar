package com.devdaily.opensource.jelly.view;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class CurtainComponent extends JComponent implements ComponentListener,
    WindowFocusListener
{

  private final JFrame frame;

  public CurtainComponent(JFrame frame)
  {
    this.frame = frame;
    setLayout(new BorderLayout());
    frame.addComponentListener(this);
    frame.addWindowFocusListener(this);
  }

  public void turnOn()
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension dim = tk.getScreenSize();
        int width = dim.width;
        frame.setLocation(-width, 0);
        frame.setVisible(true);
        int x = -width;
        int xCurr = x;
        int jump = 0;
        while (xCurr < -10)
        {
          frame.setLocation(xCurr, 0);
          jump = (0 - xCurr) / 15;
          if (jump < 10)
            jump = 10;
          xCurr = xCurr + jump;
          if (Math.abs(xCurr) < 10)
            xCurr = 0;
          try
          {
            // kludge - slow it down for my fast pc
            // need a better way to do this
            Thread.sleep(5);
          }
          catch (InterruptedException e)
          {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
        frame.setLocation(0, 0);
        frame.transferFocus();
      }
    });
  }

  public void turnOffFast()
  {
    frame.setVisible(false);
  }

  public void turnOff()
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension dim = tk.getScreenSize();
        int width = dim.width;
        int xFinal = -width;
        int xCurr = 0;
        int jump = 0;
        while (xCurr > (xFinal))
        {
          // System.err.println("xCurr = " + xCurr);
          frame.setLocation(xCurr, 0);
          jump = (Math.abs(xFinal) - Math.abs(xCurr)) / 20;
          // System.err.println("jump = " + jump);
          if (jump < 10)
            jump = 10;
          xCurr = xCurr - jump;
        }
        frame.setVisible(false);
      }
    });
  }

  public void componentShown(ComponentEvent evt)
  {
    // System.err.println("componentShown");
  }

  public void componentResized(ComponentEvent evt)
  {
    // System.err.println("componentResized");
  }

  public void componentMoved(ComponentEvent evt)
  {
    // System.err.println("componentMoved");
  }

  public void componentHidden(ComponentEvent evt)
  {
    // System.err.println("componentHidden");
  }

  public void windowGainedFocus(WindowEvent evt)
  {
    // System.err.println("windowGainedFocus");
  }

  public void windowLostFocus(WindowEvent evt)
  {
    // System.err.println("windowLostFocus");
  }

  public void repaint()
  {
    super.repaint();
    // System.err.println("repaint was called");
  }

}
