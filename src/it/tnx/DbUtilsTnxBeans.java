/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.tnx;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 *
 * @author mceccarelli
 */
public class DbUtilsTnxBeans {
    
    public static int getColumnIndex(ResultSetMetaData meta, String column) throws SQLException {
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            if (meta.getColumnName(i).equalsIgnoreCase(column)) {
                return i;
            }
        }
        throw new SQLException("Column " + column + " not found in " + meta);
    }

}
