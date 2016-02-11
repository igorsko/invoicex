/*
 * DefaultListCellEditor.java
 *
 * Created on 10 gennaio 2007, 14.07
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.tnx.gui.list;

import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTextField;

//test cvs
// @author Santhosh Kumar T - santhosh@in.fiorano.com 
public class DefaultListCellEditor extends DefaultCellEditor implements ListCellEditor{ 
    public DefaultListCellEditor(final JCheckBox checkBox){ 
        super(checkBox);
    } 
 
    public DefaultListCellEditor(final JComboBox comboBox){ 
        super(comboBox); 
    } 
 
    public DefaultListCellEditor(final JTextField textField){ 
        super(textField); 
    } 
 
    public Component getListCellEditorComponent(JList list, Object value, boolean isSelected, int index){ 
        delegate.setValue(value); 
        return editorComponent; 
    } 
}