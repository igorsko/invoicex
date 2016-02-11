/*
 * MutableListModel.java
 *
 * Created on 10 gennaio 2007, 14.07
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.tnx.gui.list;

import javax.swing.ListModel;

// @author Santhosh Kumar T - santhosh@in.fiorano.com 
public interface MutableListModel extends ListModel { 
    public boolean isCellEditable(int index); 
    public void setValueAt(Object value, int index); 
} 