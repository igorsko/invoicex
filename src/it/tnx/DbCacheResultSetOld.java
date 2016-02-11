/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.tnx;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author mceccarelli
 */
public abstract class DbCacheResultSetOld implements ResultSet  {
    ArrayList<Map> rn = new ArrayList();    //records per nome
    ArrayList<Object[]> ri = new ArrayList();    //record per colonna
    int currentrow = -1;

    public DbCacheResultSetOld(Connection conn, String sql, ResultSet r) {
        try {
            fillList(conn, sql, r);    
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void fillList(Connection conn, String sql, ResultSet r) throws Exception {
        ResultSetMetaData m = r.getMetaData();
        while (r.next()) {
            Object[] row = new Object[m.getColumnCount()];
            HashMap map = new HashMap();
            for (int i = 0; i < m.getColumnCount(); i++) {
                Object o = null;
                if (m.getColumnTypeName(i+1).toLowerCase().indexOf("blob") >= 0) {
                    o = myblob(r.getBlob(i + 1));
                } else {
                    o = r.getObject(i + 1);
                }
                row[i] = o;
                map.put(m.getColumnLabel(i + 1), o);
            }
            ri.add(row);
            rn.add(map);
        }
    }

    private Object myblob(Object o) {
        try {
            return new MyBlob((Blob)o);    
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class MyBlob implements Blob {
        long slength = -1;
        byte[] bytes = null;

        public MyBlob(Blob blob) throws SQLException, IOException {
            if (blob == null) return;
            slength = blob.length();
            ByteArrayOutputStream byteout = new ByteArrayOutputStream();
            IOUtils.copy(blob.getBinaryStream(), byteout);
            bytes = byteout.toByteArray();
        }


        public long length() throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public byte[] getBytes(long pos, int length) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public InputStream getBinaryStream() throws SQLException {
            if (bytes == null) return null;
            return new ByteArrayInputStream(bytes);
        }

        public long position(byte[] pattern, long start) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public long position(Blob pattern, long start) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int setBytes(long pos, byte[] bytes) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public OutputStream setBinaryStream(long pos) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void truncate(long len) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void free() throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public InputStream getBinaryStream(long pos, long length) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }

    //--------------------------------------------------------------------------

    public boolean next() throws SQLException {
        if (currentrow + 1 < rn.size()) {
            currentrow++;
            return true;
        }
        return false;
    }

    public void close() throws SQLException {
    }

    public boolean wasNull() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getString(int columnIndex) throws SQLException {
        return (String)(ri.get(currentrow)[columnIndex]);
    }

    public boolean getBoolean(int columnIndex) throws SQLException {
        return (Boolean)(ri.get(currentrow)[columnIndex]);
    }

    public byte getByte(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public short getShort(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getInt(int columnIndex) throws SQLException {
        return (Integer)(ri.get(currentrow)[columnIndex]);
    }

    public long getLong(int columnIndex) throws SQLException {
        return (Long)(ri.get(currentrow)[columnIndex]);
    }

    public float getFloat(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getDouble(int columnIndex) throws SQLException {
        return (Double)(ri.get(currentrow)[columnIndex]);
    }

    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public byte[] getBytes(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public java.sql.Date getDate(int columnIndex) throws SQLException {
        return (Date)(ri.get(currentrow)[columnIndex]);
    }

    public Time getTime(int columnIndex) throws SQLException {
        return (Time)(ri.get(currentrow)[columnIndex]);
    }

    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        return (Timestamp)(ri.get(currentrow)[columnIndex]);
    }

    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getString(String columnLabel) throws SQLException {
        return (String)(rn.get(currentrow).get(columnLabel));
    }

    public boolean getBoolean(String columnLabel) throws SQLException {
        return (Boolean)(rn.get(currentrow).get(columnLabel));
    }

    public byte getByte(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public short getShort(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getInt(String columnLabel) throws SQLException {
        return (Integer)(rn.get(currentrow).get(columnLabel));
    }

    public long getLong(String columnLabel) throws SQLException {
        return (Long)(rn.get(currentrow).get(columnLabel));
    }

    public float getFloat(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getDouble(String columnLabel) throws SQLException {
        return (Double)(rn.get(currentrow).get(columnLabel));
    }

    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public byte[] getBytes(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public java.sql.Date getDate(String columnLabel) throws SQLException {
        return (Date)(rn.get(currentrow).get(columnLabel));
    }

    public Time getTime(String columnLabel) throws SQLException {
        return (Time)(rn.get(currentrow).get(columnLabel));
    }

    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        return (Timestamp)(rn.get(currentrow).get(columnLabel));
    }

    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public SQLWarning getWarnings() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void clearWarnings() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getCursorName() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object getObject(int columnIndex) throws SQLException {
        return (ri.get(currentrow)[columnIndex]);
    }

    public Object getObject(String columnLabel) throws SQLException {
        return (rn.get(currentrow).get(columnLabel));
    }

    public int findColumn(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Reader getCharacterStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Reader getCharacterStream(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isBeforeFirst() throws SQLException {
        if (currentrow == -1) return true;
        return false;
    }

    public boolean isAfterLast() throws SQLException {
        if (currentrow >= rn.size()) return true;
        return false;
    }

    public boolean isFirst() throws SQLException {
        if (currentrow == 0) return true;
        return false;
    }

    public boolean isLast() throws SQLException {
        if (currentrow == rn.size()) return true;
        return false;
    }

    public void beforeFirst() throws SQLException {
        currentrow = -1;
    }

    public void afterLast() throws SQLException {
        currentrow = rn.size();
    }

    public boolean first() throws SQLException {
        currentrow = 0;
        return true;
    }

    public boolean last() throws SQLException {
        currentrow = rn.size() - 1;
        return true;
    }

    public int getRow() throws SQLException {
        return currentrow+1;
    }

    public boolean absolute(int row) throws SQLException {
        currentrow = row-1;
        return true;
    }

    public boolean relative(int rows) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean previous() throws SQLException {
        if (currentrow > 0) {
            currentrow--;
            return true;
        }
        return false;
    }

    public void setFetchDirection(int direction) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getFetchDirection() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setFetchSize(int rows) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getFetchSize() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getType() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getConcurrency() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean rowUpdated() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean rowInserted() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean rowDeleted() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateNull(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateByte(int columnIndex, byte x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateShort(int columnIndex, short x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateInt(int columnIndex, int x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateLong(int columnIndex, long x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateFloat(int columnIndex, float x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateDouble(int columnIndex, double x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateString(int columnIndex, String x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateDate(int columnIndex, java.sql.Date x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateTime(int columnIndex, Time x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateObject(int columnIndex, Object x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateNull(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateBoolean(String columnLabel, boolean x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateByte(String columnLabel, byte x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateShort(String columnLabel, short x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateInt(String columnLabel, int x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateLong(String columnLabel, long x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateFloat(String columnLabel, float x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateDouble(String columnLabel, double x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateString(String columnLabel, String x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateBytes(String columnLabel, byte[] x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateDate(String columnLabel, java.sql.Date x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateTime(String columnLabel, Time x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateObject(String columnLabel, Object x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void insertRow() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateRow() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void deleteRow() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void refreshRow() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void cancelRowUpdates() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void moveToInsertRow() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void moveToCurrentRow() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Statement getStatement() throws SQLException {
        return null;
    }

    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Ref getRef(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Blob getBlob(int columnIndex) throws SQLException {        
        return (Blob)ri.get(currentrow)[columnIndex];
    }

    public Clob getClob(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Array getArray(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Ref getRef(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Blob getBlob(String columnLabel) throws SQLException {
        return (Blob)rn.get(currentrow).get(columnLabel);
    }

    public Clob getClob(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Array getArray(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public java.sql.Date getDate(int columnIndex, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public java.sql.Date getDate(String columnLabel, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public URL getURL(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public URL getURL(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateRef(int columnIndex, Ref x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateRef(String columnLabel, Ref x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateBlob(String columnLabel, Blob x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateClob(int columnIndex, Clob x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateClob(String columnLabel, Clob x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateArray(int columnIndex, Array x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateArray(String columnLabel, Array x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getHoldability() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isClosed() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateNString(int columnIndex, String nString) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateNString(String columnLabel, String nString) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getNString(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getNString(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateClob(int columnIndex, Reader reader) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateClob(String columnLabel, Reader reader) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateNClob(String columnLabel, Reader reader) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}