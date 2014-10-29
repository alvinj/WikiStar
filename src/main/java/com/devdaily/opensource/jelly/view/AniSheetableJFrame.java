package com.devdaily.opensource.jelly.view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

public class AniSheetableJFrame extends JFrame implements ActionListener
{
  public static final int INCOMING = 1;
  public static final int OUTGOING = -1;
  public static final float ANIMATION_DURATION = 300f;
  public static final int ANIMATION_SLEEP = 10;

  JComponent sheet;
  JPanel glass;
  AnimatingSheet animatingSheet;
  boolean animating;
  int animationDirection;
  Timer animationTimer;
  long animationStart;
  BufferedImage offscreenImage;

  public AniSheetableJFrame()
  {
    glass = (JPanel) getGlassPane();
    glass.setLayout(new GridBagLayout());
  }

  public JComponent showJDialogAsSheet(JDialog dialog)
  {
    animatingSheet = new AnimatingSheet();
    animatingSheet.setBorder(new LineBorder(Color.black, 1));
    sheet = (JComponent) dialog.getContentPane();
    sheet.setBorder(new LineBorder(Color.black, 1));
    glass.removeAll();
    animationDirection = INCOMING;
    startAnimation();
    return sheet;
  }

  public void hideSheet()
  {
    animationDirection = OUTGOING;
    startAnimation();
    // glass.setVisible(false);
  }

  private void startAnimation()
  {
    glass.repaint();
    // clear glasspane and set up animatingSheet
    animatingSheet.setSource(sheet);
    glass.removeAll();
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.NORTH;
    glass.add(animatingSheet, gbc);
    gbc.gridy = 1;
    gbc.weighty = Integer.MAX_VALUE;
    glass.add(Box.createGlue(), gbc);
    glass.setVisible(true);

    // start animation timer
    animationStart = System.currentTimeMillis();
    if (animationTimer == null)
      animationTimer = new Timer(ANIMATION_SLEEP, this);
    animating = true;
    animationTimer.start();
  }

  private void stopAnimation()
  {
    animationTimer.stop();
    animating = false;
  }

  // used by the Timer
  public void actionPerformed(ActionEvent e)
  {
    if (animating)
    {
      // calculate height to show
      float animationPercent = (System.currentTimeMillis() - animationStart)
          / ANIMATION_DURATION;
      animationPercent = Math.min(1.0f, animationPercent);
      int animatingHeight = 0;
      if (animationDirection == INCOMING)
      {
        animatingHeight = (int) (animationPercent * sheet.getHeight());
      }
      else
      {
        animatingHeight = (int) ((1.0f - animationPercent) * sheet.getHeight());
      }
      // clip off that much from sheet and blit it
      // into animatingSheet
      animatingSheet.setAnimatingHeight(animatingHeight);
      animatingSheet.repaint();

      if (animationPercent >= 1.0f)
      {
        stopAnimation();
        if (animationDirection == INCOMING)
        {
          finishShowingSheet();
        }
        else
        {
          glass.removeAll();
          glass.setVisible(false);
        }
      }
    }
  }

  private void finishShowingSheet()
  {
    glass.removeAll();
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.NORTH;
    glass.add(sheet, gbc);
    gbc.gridy = 1;
    gbc.weighty = Integer.MAX_VALUE;
    glass.add(Box.createGlue(), gbc);
    // glass.setVisible(true);
    glass.revalidate();
    glass.repaint();
  }

  class AnimatingSheet extends JPanel
  {
    Dimension animatingSize = new Dimension(0, 1);
    JComponent source;
    BufferedImage offscreenImage;

    public AnimatingSheet()
    {
      super();
      setOpaque(true);
    }

    public void setSource(JComponent source)
    {
      this.source = source;
      animatingSize.width = source.getWidth();
      makeOffscreenImage(source);
    }

    public void setAnimatingHeight(int height)
    {
      animatingSize.height = height;
      setSize(animatingSize);
    }

    private void makeOffscreenImage(JComponent source)
    {
      GraphicsConfiguration gfxConfig = GraphicsEnvironment
          .getLocalGraphicsEnvironment().getDefaultScreenDevice()
          .getDefaultConfiguration();
      offscreenImage = gfxConfig.createCompatibleImage(source.getWidth(),
          source.getHeight());
      Graphics2D offscreenGraphics = (Graphics2D) offscreenImage.getGraphics();
      source.paint(offscreenGraphics);
    }

    public Dimension getPreferredSize()
    {
      return animatingSize;
    }

    public Dimension getMinimumSize()
    {
      return animatingSize;
    }

    public Dimension getMaximumSize()
    {
      return animatingSize;
    }

    public void paint(Graphics g)
    {
      // get the bottommost n pixels of source and
      // paint them into g, where n is height

      BufferedImage fragment = offscreenImage.getSubimage(0, offscreenImage
          .getHeight()
          - animatingSize.height, source.getWidth(), animatingSize.height);
      g.drawImage(fragment, 0, 0, this);
    }
  }

}
