/*
 * DefaultMutableListModel.java
 *
 * Created on 10 gennaio 2007, 14.07
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.tnx.gui.list;

import javax.swing.DefaultListModel;

// @author Santhosh Kumar T - santhosh@in.fiorano.com
public class DefaultMutableListModel extends DefaultListModel implements MutableListModel{
    public boolean isCellEditable(int index){
        return true;
    }
    
    public void setValueAt(Object value, int index){
        super.setElementAt(value, index);
    }
}