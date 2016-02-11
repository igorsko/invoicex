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

package it.tnx.dbeans.ResultSet;
import java.sql.*;
import javax.swing.table.*;
import java.util.ArrayList;
/**
 *
 * @author  marco
 */
public class CachedTableModel extends  AbstractTableModel {
  public ArrayList cache;
  public Object[] columnNames;
  
  /**
   * Constructs the table model.
   * @param aResultSet the result set to display.
   */
  public CachedTableModel(ArrayList rows, Object[] columnNames) {
    cache = rows;
    this.columnNames = columnNames;
  }
  
  public String getColumnName(int c) {
    return (String)columnNames[c];
  }
  
  public int getColumnCount() {
    return columnNames.length;
  }
  
  public boolean isCellEditable(int r, int c) {
    return true;
  }
  
  public void setValueAt(Object value, int r, int c) {
    Object[] tempRow = (Object[])cache.get(r);
    tempRow[c] = value;
    cache.set(r, tempRow);
  }
  
  public Object getValueAt(int r, int c) {
    if (r < cache.size())
      return ((Object[])cache.get(r))[c];
    else
      return null;
  }
  
  public int getRowCount() {
    return cache.size();
  }
  
  
}
