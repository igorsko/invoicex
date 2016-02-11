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
public class CellEditorFomratted2DecimaliIta extends DefaultCellEditor {
    JFormattedTextField ftf;
    NumberFormat integerFormat;
    private Integer minimum, maximum;
    private boolean DEBUG = false;
    
    /** Creates a new instance of CellEditor2DecimaliIta */
    public CellEditorFomratted2DecimaliIta() {
        super(new JFormattedTextField());
        ftf = (JFormattedTextField)getComponent();
        
        //Set up the editor for the integer cells.
        integerFormat = NumberFormat.getInstance(Locale.ITALIAN);
        integerFormat.setMaximumFractionDigits(5);
        integerFormat.setMinimumFractionDigits(2);
        NumberFormatter intFormatter = new NumberFormatter(integerFormat);
        intFormatter.setFormat(integerFormat);
        
        ftf.setFormatterFactory(new DefaultFormatterFactory(intFormatter));
        ftf.setValue(new Double(0.0d));
        ftf.setHorizontalAlignment(JTextField.RIGHT);
        ftf.setFocusLostBehavior(JFormattedTextField.PERSIST);
        
        //React when the user presses Enter while the editor is
        //active.  (Tab is handled as specified by
        //JFormattedTextField's focusLostBehavior property.)
        ftf.addFocusListener(new FocusListener(){
            public void focusGained(FocusEvent evt) {
                
            }
            public void focusLost(FocusEvent evt) {
                try {
                    ftf.commitEdit();     //so use it.
                    ftf.postActionEvent(); //stop editing
                }catch (ParseException parseErr) {}
            }
        });
        ftf.getInputMap().put(KeyStroke.getKeyStroke(
                KeyEvent.VK_ENTER, 0),
                "check");
        ftf.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!ftf.isEditValid()) { //The text is invalid.
                    //if (userSaysRevert()) { //reverted
                    ftf.postActionEvent(); //inform the editor
                    //}
                } else try {              //The text is valid,
                    ftf.commitEdit();     //so use it.
                    ftf.postActionEvent(); //stop editing
                } catch (java.text.ParseException exc) { }
            }
        });
        
    }
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        JFormattedTextField ftf = (JFormattedTextField)super.getTableCellEditorComponent(table, value, isSelected, row, column);
        if (it.tnx.Util.getDoubleEng( String.valueOf(value) ) > 0) {
            Double d = new Double( it.tnx.Util.getDoubleEng( String.valueOf(value) ) );
            ftf.setValue(d);
        } else {
            ftf.setValue(new Double(0));
        }
        return ftf;
    }
    
    //Override to ensure that the value remains an Integer.
    public Object getCellEditorValue() {
        JFormattedTextField ftf = (JFormattedTextField)getComponent();
        Object o = ftf.getValue();
        if (o instanceof Integer) {
            return o;
        } else if (o instanceof Number) {
            return o;
        } else {
            if (DEBUG) {
                System.out.println("getCellEditorValue: o isn't a Number");
            }
            try {
                return integerFormat.parseObject(o.toString());
            } catch (ParseException exc) {
                System.err.println("getCellEditorValue: can't parse o: " + o);
                return null;
            }
        }
    }
}
