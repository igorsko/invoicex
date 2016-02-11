/**
 * Invoicex
 * Copyright (c) 2005,2006,2007 Marco Ceccarelli, Tnx snc
 *
 * Questo software è soggetto, e deve essere distribuito con la licenza  
 * GNU General Public License, Version 2. La licenza accompagna il software
 * o potete trovarne una copia alla Free Software Foundation http://www.fsf.org .
 *
 * This software is subject to, and may be distributed under, the
 * GNU General Public License, Version 2. The license should have
 * accompanied the software or you may obtain a copy of the license
 * from the Free Software Foundation at http://www.fsf.org .
 * 
 * --
 * Marco Ceccarelli (m.ceccarelli@tnx.it)
 * Tnx snc (http://www.tnx.it)
 *
 */

package tnxbeans;
import java.awt.*;
import javax.swing.*;

public class tnxListCellRenderer1 extends javax.swing.JLabel implements javax.swing.ListCellRenderer {
  //final static ImageIcon longIcon = new ImageIcon("long.gif");
  //final static ImageIcon shortIcon = new ImageIcon("short.gif");
  
  // This is the only method defined by ListCellRenderer.
  // We just reconfigure the JLabel each time we're called.
  
  public Component getListCellRendererComponent(
  JList list,
  Object value,            // value to display
  int index,               // cell index
  boolean isSelected,      // is the cell selected
  boolean cellHasFocus)    // the list and the cell have the focus
  {
    boolean flag1 = false;
    
    String s = value.toString();
    if (s.startsWith("*")) {
      flag1 = true;
    }
    setText(s);
    //setIcon((s.length() > 10) ? longIcon : shortIcon);
    setFont(list.getFont());
    if (isSelected) {
      setBackground(list.getSelectionBackground());
      setForeground(list.getSelectionForeground());
    }
    else {
      setBackground(list.getBackground());
      setForeground(list.getForeground());
    }
    if (flag1 == true) {
      setForeground(Color.red);
      setFont(this.getFont().deriveFont(Font.BOLD));
    }    
    setEnabled(list.isEnabled());
    setOpaque(true);
    return this;
  }
}
