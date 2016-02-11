/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.tnx.dbeans.ResultSet;

import it.tnx.DbI;
import it.tnx.commons.MicroBench;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author mceccarelli
 */
public class LazyResultSetModel extends DefaultTableModel {

    String sql;
    DbI db;
    TreeMap<Integer, Object[]> cache = new TreeMap<Integer, Object[]>();
    String[] col_names;
    String[] col_classes;
    int index = 0;
    int rows = 0;
    int cols = 0;

    public LazyResultSetModel(String sql, DbI db) throws SQLException {
        this.sql = sql;
        this.db = db;

        MicroBench mb = new MicroBench();
        mb.start();

        String sql2 = StringUtils.replace(sql.toLowerCase(), "select ", "select SQL_CALC_FOUND_ROWS ");
        sql2 = sql2 + " limit 1000";
        System.out.println("LazyResultSetModel sql2 = " + sql2);
        ResultSet r = db.getDbConn().createStatement().executeQuery(sql2);
        ResultSetMetaData m = r.getMetaData();
        cols = m.getColumnCount();
        col_names = new String[cols];
        col_classes = new String[cols];
        for (int i = 0; i < cols; i++) {
            col_names[i] = m.getColumnName(i + 1);
            col_classes[i] = m.getColumnClassName(i + 1);
        }
        ResultSet r2 = db.getDbConn().createStatement().executeQuery("SELECT FOUND_ROWS()");
        if (r2.next()) {
            rows = r2.getInt(1);
        }

        System.out.println(mb.getDiff("open 1"));

        prendi(r, 0);
        r2.getStatement().close();
        r2.close();
        r.getStatement().close();
        r.close();

        System.out.println(mb.getDiff("open 2"));
    }

    public int getRowCount() {
        return rows;
    }

    public int getColumnCount() {
        return cols;
    }

    synchronized public Object getValueAt(int rowIndex, int columnIndex) {
//        System.out.println("get rowIndex:" + rowIndex + " index:" + index + " cond:" + rowIndex + " <= " + (index - 500) + " || " + rowIndex + " >= " + (index + 500));
        if (rowIndex <= (index - 500) || rowIndex >= (index + 500)) {
            String sql2 = sql + " limit " + ((rowIndex < 500 ? 500 : rowIndex) - 500) + ", 1000";
//            System.out.println("getValueAt: " + rowIndex + " index: " + index + " sql2 = " + sql2);
            try {
                ResultSet r = db.getDbConn().createStatement().executeQuery(sql2);
                cache.clear();
                prendi(r, rowIndex);
                index = rowIndex;
//                System.out.println("nuovo index:" + index);
                r.getStatement().close();
                r.close();
            } catch (Exception e) {
                return null;
            }
        }
        try {
            return cache.get(rowIndex)[columnIndex];
        } catch (Exception e0) {
            e0.printStackTrace();
            System.out.println("get rowIndex:" + rowIndex + " index:" + index + " cond:" + rowIndex + " <= " + (index - 500) + " || " + rowIndex + " >= " + (index + 500));
            try {System.out.println("rows:" + rows);} catch (Exception e) {}
            try {System.out.println("cache:" + cache);} catch (Exception e) {}
            try {System.out.println("cache.get(rowIndex):" + cache.get(rowIndex));} catch (Exception e) {}
            try {System.out.println("cache.get(rowIndex)[columnIndex]:" + cache.get(rowIndex)[columnIndex]);} catch (Exception e) {}
            return "?";
        }
        
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        try {
            return Class.forName(col_classes[columnIndex]);
        } catch (ClassNotFoundException ex) {
            return Object.class;
        }
    }

    @Override
    public String getColumnName(int column) {
        return col_names[column];
    }

    private void prendi(ResultSet r, int i) throws SQLException {
        int li = i - 500;
        if (li < 0) {
            li = 0;
        }
        int ls = i + 500;
        if (ls > rows) {
            ls = rows;
        }
        System.out.println("aggiungo: da " + li + " a " + ls);
        for (int conta = 0; conta < ls - li; conta++) {
            r.next();
            Object[] row = new Object[cols];
            for (int icol = 0; icol < cols; icol++) {
                row[icol] = r.getObject(icol + 1);
            }
            cache.put(li + conta, row);
        }
    }

    public void removeRow(int row) {
        rows--;
        cache.remove(row);
        fireTableRowsDeleted(row, row);
    }

    @Override
    public void addRow(Object[] rowData) {
        rows++;
        Integer max = Collections.max(cache.keySet());
        cache.put(max + 1, rowData);
        fireTableRowsInserted(max + 1, max + 1);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

//    @Override
//    public void insertRow(int row, Object[] rowData) {
//        rows++;
//        cache.put(row, rowData)
//        fireTableRowsInserted(row, row);
//    }



}
