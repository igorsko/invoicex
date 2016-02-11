/*
 * JTableSs.java
 *
 * Created on January 31, 2005, 2:40 PM
 */

package it.tnx.gui;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author  marco
 */
public class JTableSs extends javax.swing.JTable {
    
    /** Creates a new instance of JTableSs */
    public JTableSs() {
        JTextField tf = new JTextField();
        tf.setBorder(BorderFactory.createEmptyBorder());
        setDefaultEditor(Object.class, new DefaultCellEditor(tf));
        putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
    }
    
    //  Place cell in edit mode when it 'gains focus'
    public void changeSelection(final int row, final int column, boolean toggle, boolean extend) {
        super.changeSelection(row, column, toggle, extend);
        System.out.println("changeSel:" + row + " " + column);        
        TableCellEditor celledit = getCellEditor();
        if (celledit != null) {
            celledit.cancelCellEditing();
        }
        if (editCellAt(row, column)) {
            Component comp = getEditorComponent();
            comp.requestFocusInWindow();
            if (comp instanceof JTextField) {
                JTextField textComp = (JTextField)comp;
                textComp.selectAll();
            }
        }
    }
    
}
