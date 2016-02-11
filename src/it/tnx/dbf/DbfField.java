/*
 * DbfField.java
 *
 * Created on 13 aprile 2003, 0.34
 */

package it.tnx.dbf;

/**
 *
 * @author  marco
 */
public class DbfField {
    public String name = "";
    public String type = "";
    public short length = 0;
    public short decimalPlaces = 0;
    
    /** Creates a new instance of DbfField */
    public DbfField() {
    }
    
    public String getSqlCreate() {
        String sql = DbfTable.getFieldName(name);
        
        if (type.equals("C")) {
            sql += " VARCHAR(" + length + ")";
        } else if (type.equals("N")) {
            sql += " NUMERIC(" + length + "," + decimalPlaces + ")";
        } else if (type.equals("M")) {
            sql += " VARCHAR(" + length + ")";
        } else if (type.equals("D")) {
            sql += " DATE";
        } else {
            sql += " !!! type:" + type + " !!!";
        }
        return sql;
    }
    
}
