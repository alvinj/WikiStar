package com.ibm.was.graphics;

import java.awt.*;
import javax.swing.*;


/**
 * This icon can be built with another icon and will render it with a Rollover
 * effect where the icon is darkened and has its blue intensity increased
 */
public class RolloverIcon implements Icon
{
  protected Icon icon;

  public RolloverIcon(Icon icon)
  {
    this.icon = icon;
  }

  public int getIconHeight()
  {
    return icon.getIconHeight();
  }

  public int getIconWidth()
  {
    return icon.getIconWidth();
  }

  public void paintIcon(Component c, Graphics g, int x, int y)
  {
    Graphics2D graphics2d = (Graphics2D) g;
    Composite oldComposite = graphics2d.getComposite();
    graphics2d.setComposite(RolloverComposite.DEFAULT);
    icon.paintIcon(c, g, x, y);
    graphics2d.setComposite(oldComposite);
  }
}
