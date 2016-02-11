/*
 * CellEditor2DecimaliIta.java
 *
 * Created on 24 giugno 2005, 12.05
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package it.tnx.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

/**
 *
 * @author marco
 */
public class CellEditor2DecimaliIta extends DefaultCellEditor {
    JTextField tf;
    NumberFormat format;
    NumberFormatter formatter;
    private boolean DEBUG = false;
    
    /** Creates a new instance of CellEditor2DecimaliIta */
    public CellEditor2DecimaliIta() {
        super(new JTextField());
        tf = (JTextField)getComponent();
        tf.setHorizontalAlignment(tf.RIGHT);
        
        //Set up the editor for the integer cells.
        format = NumberFormat.getInstance(Locale.ITALIAN);
        format.setMaximumFractionDigits(5);
        format.setMinimumFractionDigits(2);
        formatter = new NumberFormatter(format);
        formatter.setFormat(format);
        
        tf.setHorizontalAlignment(JTextField.RIGHT);
    }
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        JTextField tf = (JTextField)super.getTableCellEditorComponent(table, value, isSelected, row, column);
        tf.setHorizontalAlignment(tf.RIGHT);
        if (it.tnx.Util.getDoubleEng( String.valueOf(value) ) > 0) {
            Double d = new Double( it.tnx.Util.getDoubleEng( String.valueOf(value) ) );
            tf.setText( format.format(d) );
        } else {
            tf.setText( format.format(0) );
        }
        return tf;
    }
    
    //Override to ensure that the value remains an Integer.
    public Object getCellEditorValue() {
        JTextField tf = (JTextField)getComponent();
        Object o = tf.getText();
        System.out.println("getCellEditorValue:" + o + " double:" + it.tnx.Util.getDouble(o));
        return new Double( it.tnx.Util.getDouble(o) );
    }
}
