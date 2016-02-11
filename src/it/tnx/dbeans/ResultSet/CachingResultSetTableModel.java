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
/*
   This class caches the result set data; it can be used
   if scrolling cursors are not supported
 */
public class CachingResultSetTableModel extends ResultSetTableModel {
  public CachingResultSetTableModel(ResultSet aResultSet) {
    super(aResultSet);
    try {
      cache = new ArrayList();
//      int cols = getColumnCount();
      int cols = aResultSet.getMetaData().getColumnCount();
      ResultSet rs = getResultSet();
      
         /* place all data in an array list of Object[] arrays
            We don't use an Object[][] because we don't know
            how many rows are in the result set
          */
      
      while (rs.next()) {
        Object[] row = new Object[cols];
        for (int j = 0; j < row.length; j++)
          row[j] = rs.getObject(j + 1);
        cache.add(row);
      }
    }
    catch(SQLException e) {
      System.out.println("Error " + e);
    }
  }
  
  public CachingResultSetTableModel(ArrayList rows) {
    super(null);
    cache = rows;
  }
  
  public Object getValueAt(int r, int c) {
    if (r < cache.size())
      return ((Object[])cache.get(r))[c];
    else
      return null;
  }
  
  public void setValueAt(Object value, int r, int c) {
    try {
      Object[] row = (Object[])cache.get(r);
      row[c] = value;
      cache.set(r, row);
      super.setValueAt(value, r, c);
    }
    catch(Exception e) {
      e.printStackTrace();
      super.setValueAt(null, r, c);
    }
  }
  
  
  public int getRowCount() {
    return cache.size();
  }
  
  public ArrayList cache;
}