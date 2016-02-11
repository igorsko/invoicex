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

/**
 *
 * @author  marco
 */
public class ResultSetTableModel extends AbstractTableModel {

    /**
     * Constructs the table model.
     * @param aResultSet the result set to display.
     */
    public ResultSetTableModel(ResultSet aResultSet) {
        if (aResultSet != null) {
            rs = aResultSet;
            try {
                rsmd = rs.getMetaData();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public String getColumnName(int c) {
        try {
            return rsmd.getColumnName(c + 1);
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
    }

    public int getColumnCount() {
        try {
            return rsmd.getColumnCount();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Gets the result set that this model exposes.
     * @return the result set
     */
    public ResultSet getResultSet() {
        return rs;
    }

    public boolean isCellEditable(int r, int c) {
        return true;
    }

    public Object getValueAt(int x, int y) {
        return null;
    }

    public int getRowCount() {
        return 0;
    }
    private ResultSet rs;
    private ResultSetMetaData rsmd;

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("*** finalize " + this);

        //chiudo lo statement
        try {
            rs.getStatement().close();
        } catch (Exception e) {
        }
        try {
            rs.close();
        } catch (Exception e) {
        }
    }
}
