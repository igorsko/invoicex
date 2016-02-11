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
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
/**
 *
 * @author  Administrator
 */
public class JLabelHeaderRenderer extends JLabel  implements TableCellRenderer {
  
  /** Creates a new instance of JLabelHeaderRenderer */
  public JLabelHeaderRenderer() {
    
  }

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    JLabel button = this;
    button.setText((value == null) ? "" : value.toString());
    button.setText("<html>Situazione<br> CLIENTI</html>");
    return button;
  }

  
}
