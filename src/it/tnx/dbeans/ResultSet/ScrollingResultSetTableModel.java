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
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.TableModelListener;

/**
 *
 * @author  marco
 */
/**
This class uses a scrolling cursor, a JDBC 2 feature,
to locate result set elements.
 */
public class ScrollingResultSetTableModel extends ResultSetTableModel {

    JFrame frame;
//    boolean inserimento = false;

    /**
    Constructs the table model.
    @param aResultSet the result set to display.
     */
    public ScrollingResultSetTableModel(ResultSet aResultSet) {
        super(aResultSet);
    }

    public ScrollingResultSetTableModel(ResultSet aResultSet, JFrame frame_padre) {
        super(aResultSet);
        this.frame = frame_padre;
    }

    public Object getValueAt(int r, int c) {
        try {
            //System.out.println("grid2:get:" + r + ":" + c);
            ResultSet rs = getResultSet();
            rs.absolute(r + 1);
            return rs.getObject(c + 1);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setValueAt(Object value, int r, int c) {
        ResultSet rs = getResultSet();
        try {
            System.out.println("grid2_1:set:" + r + ":" + c + ":" + rs.getObject(c + 1));

            rs.absolute(r + 1);
            rs.updateObject(c + 1, value);
            rs.updateRow();
            rs.moveToCurrentRow();

            System.out.println("grid2_2:set:" + r + ":" + c + ":" + rs.getObject(c + 1));

            super.setValueAt(rs.getObject(c + 1), r, c);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, e.getLocalizedMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
            try {
                rs.cancelRowUpdates();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            super.setValueAt(null, r, c);
        }
    }

    public int getRowCount() {
        try {
            ResultSet rs = getResultSet();
            rs.last();
//            int off = 0;
//            if (inserimento) off++;
            return rs.getRow();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    public void delete(int r) {
        try {
            ResultSet rs = getResultSet();
            rs.absolute(r + 1);
            rs.deleteRow();
            fireTableRowsDeleted(r, r);
        } catch (SQLException e) {
            e.printStackTrace();
        }        
    }

    
//    public void add() {
//        try {
//            ResultSet rs = getResultSet();
//            int rc = getRowCount();
//            System.out.println("row count 1:" + rc);
//            rs.moveToInsertRow();
//            inserimento = true;
//            System.out.println("row count 2:" + getRowCount());
//            fireTableRowsInserted(rc + 1, rc + 1);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }        
//    }


}
