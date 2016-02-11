package tnxbeans;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import java.sql.*;

/**
 * @version 1.0 02/25/99
 */
public class SortableTableModel extends DefaultTableModel {
    int[] indexes;
    TableSorter sorter;
    ResultSetMetaData meta;
    
    int[] colonneEditabili = null;
    String[] colonneEditabiliByName = null;
    
    //per data entry
    public boolean dirty;
    
    public SortableTableModel() {
    }
    
    public SortableTableModel(String[] colonne, int righe) {
        super.setColumnIdentifiers(colonne);
    }
    
    public SortableTableModel(String[] colonne, int righe, ResultSetMetaData meta) {
        super.setColumnIdentifiers(colonne);
        this.meta = meta;
    }
    
    public SortableTableModel(String[] colonne, int righe, ResultSetMetaData meta, int[] colonneEditabili) {
        this(colonne, righe, meta, colonneEditabili, null);
    }

    public SortableTableModel(String[] colonne, int righe, ResultSetMetaData meta, int[] colonneEditabili, String[] colonneEditabiliByName) {
        super.setColumnIdentifiers(colonne);
        this.meta = meta;
        this.colonneEditabili = colonneEditabili;
        this.colonneEditabiliByName = colonneEditabiliByName;
    }

    public Class getColumnClassSql(int col) {
        try {
            //debug
            //System.out.println("getColumClass:" + meta.getColumnType(col));
            
            switch (meta.getColumnType(col+1)) {
                case Types.VARCHAR: case Types.CHAR: case Types.BLOB: return String.class;
                case Types.INTEGER: case Types.SMALLINT: return Integer.class;
                case Types.BIGINT: return Long.class;
                case Types.DOUBLE: return Double.class;
                case Types.DECIMAL: return Double.class;
                case Types.DATE: return Date.class;
                default: return Object.class;
            }
        } catch (Exception err) {
            err.printStackTrace();
            return (null);
        }
    }
    
    
    public Object getValueAt(int row, int col) {
        int rowIndex = row;
        if (row==-1) {
            row=0;
        }
        try {
            if (indexes != null) {
                rowIndex = indexes[row];
            }
            return super.getValueAt(rowIndex, col);
        } catch (Exception err) {
            System.err.println("sortable:err:row="+row+":col="+col);
            //err.printStackTrace();
            return (null);
        }
    }
    
    public void setValueAt(Object value, int row, int col) {
        //debug
        System.out.println("grid:setValue");
        this.dirty = true;
        
        int rowIndex = row;
        if (indexes != null) {
            rowIndex = indexes[row];
        }
        super.setValueAt(value, rowIndex, col);
    }
    
    
    public void sortByColumn(int column, boolean isAscent) {
        if (sorter == null) {
            sorter = new TableSorter(this);
        }
        sorter.sort(column, isAscent);
        fireTableDataChanged();
    }
    
    public int[] getIndexes() {
        int n = getRowCount();
        if (indexes != null) {
            if (indexes.length == n) {
                return indexes;
            }
        }
        indexes = new int[n];
        for (int i=0; i<n; i++) {
            indexes[i] = i;
        }
        return indexes;
    }
    
    public boolean isCellEditable(int row, int col) {
        if (colonneEditabili != null || colonneEditabiliByName != null) {
            //faccio controllo
            if (colonneEditabili != null) {
                boolean found = false;
                for (int i = 0; i < colonneEditabili.length; i++) {
                    if (colonneEditabili[i] == col) {
                        return true;
                    }
                }
            }
            if (colonneEditabiliByName != null) {
                //faccio controllo
                boolean found = false;
                for (int i = 0; i < colonneEditabiliByName.length; i++) {
                    if (colonneEditabiliByName[i].equalsIgnoreCase( getColumnName(col) )) {
                        return true;
                    }
                }
            }
        } else {
            return true;
        }
        return false;
    }
}

