/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tnxbeans;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 *
 * @author mceccarelli
 */
class ResultSetMetaDataCached implements ResultSetMetaData {
    ResultSetMetaData delegated = null;
    int columnCount = 0;
    String[] columnNames = null;
    String[] columnTypeNames = null;
    int[] columnTypes = null;

    public ResultSetMetaDataCached() {
    }

    public ResultSetMetaDataCached(ResultSetMetaData delegate) {
        this.delegated = delegate;
        //init cache
        try {
            columnCount = delegated.getColumnCount();
            columnNames = new String[columnCount];
            columnTypeNames = new String[columnCount];
            columnTypes = new int[columnCount];
            for (int i = 0; i < columnCount; i++) {
                  columnNames[i] = delegated.getColumnName(i+1);
                  columnTypeNames[i] = delegated.getColumnTypeName(i+1);
                  columnTypes[i] = delegated.getColumnType(i+1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean isWritable(int column) throws SQLException {
        return delegated.isWritable(column);
    }

    public boolean isSigned(int column) throws SQLException {
        return delegated.isSigned(column);
    }

    public boolean isSearchable(int column) throws SQLException {
        return delegated.isSearchable(column);
    }

    public boolean isReadOnly(int column) throws SQLException {
        return delegated.isReadOnly(column);
    }

    public int isNullable(int column) throws SQLException {
        return delegated.isNullable(column);
    }

    public boolean isDefinitelyWritable(int column) throws SQLException {
        return delegated.isDefinitelyWritable(column);
    }

    public boolean isCurrency(int column) throws SQLException {
        return delegated.isCurrency(column);
    }

    public boolean isCaseSensitive(int column) throws SQLException {
        return delegated.isCaseSensitive(column);
    }

    public boolean isAutoIncrement(int column) throws SQLException {
        return delegated.isAutoIncrement(column);
    }

    public String getTableName(int column) throws SQLException {
        return delegated.getTableName(column);
    }

    public String getSchemaName(int column) throws SQLException {
        return delegated.getSchemaName(column);
    }

    public int getScale(int column) throws SQLException {
        return delegated.getScale(column);
    }

    public int getPrecision(int column) throws SQLException {
        return delegated.getPrecision(column);
    }

    public String getColumnTypeName(int column) throws SQLException {
        return columnTypeNames[column-1];
    }

    public int getColumnType(int column) throws SQLException {
        return columnTypes[column-1];
    }

    public String getColumnName(int column) throws SQLException {
        return columnNames[column-1];
    }

    public String getColumnLabel(int column) throws SQLException {
        return delegated.getColumnLabel(column);
    }

    public int getColumnDisplaySize(int column) throws SQLException {
        return delegated.getColumnDisplaySize(column);
    }

    public int getColumnCount() throws SQLException {
        return columnCount;
    }

    public String getColumnClassName(int column) throws SQLException {
        return delegated.getColumnClassName(column);
    }

    public String getCatalogName(int column) throws SQLException {
        return delegated.getCatalogName(column);
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
