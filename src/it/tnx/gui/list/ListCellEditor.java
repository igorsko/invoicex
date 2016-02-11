/*
 * ListCellEditor.java
 *
 * Created on 10 gennaio 2007, 14.07
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.tnx.gui.list;

import java.awt.Component;
import javax.swing.CellEditor;
import javax.swing.JList;

// @author Santhosh Kumar T - santhosh@in.fiorano.com
public interface ListCellEditor extends CellEditor {
    Component getListCellEditorComponent(JList list, Object value,
            boolean isSelected,
            int index);
}