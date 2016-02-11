/*
 * CellRenderer2DecimaliITa.java
 *
 * Created on 24 giugno 2005, 11.21
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package it.tnx.gui;
import java.text.SimpleDateFormat;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author marco
 */
public class CellRendererDataIta extends DefaultTableCellRenderer {
    /** Creates a new instance of CellRenderer2DecimaliITa */
    public CellRendererDataIta() {
    }
    
    public void setValue(Object value) {
        //super.setValue(value);
        if (value != null) {
            System.out.println("render:" + value + " class:" + value.getClass().getName());
            SimpleDateFormat form = new SimpleDateFormat("dd/MM/yy");
            try {
                super.setValue(form.format(value));
            } catch (Exception err) {
                super.setValue(value);
            }
        } else {
            super.setValue(null);
        }
        setHorizontalAlignment(JLabel.RIGHT);
    }
}
