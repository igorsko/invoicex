/*
 * Debug.java
 *
 * Created on April 3, 2005, 9:26 PM
 */

package it.tnx;

import java.sql.*;
/**
 *
 * @author marcokde
 */
public class Debug {
    
    /** Creates a new instance of Debug */
    public Debug() {
    }
    
    static public void debugResultSet(ResultSet r, boolean showData) {
        try{
            ResultSetMetaData m = r.getMetaData();
            for (int i = 1; i <= m.getColumnCount(); i++) {
                System.out.println("debugResultSet " + " Field " + i + " : name=" + m.getColumnName(i) + " type=" + m.getColumnTypeName(i) + " tableName=" + m.getTableName(i));
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }
    
}
