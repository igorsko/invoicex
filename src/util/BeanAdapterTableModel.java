/**
 * Invoicex
 * Copyright (c) 2005,2006,2007 Marco Ceccarelli, Tnx snc
 *
 * Questo software è soggetto, e deve essere distribuito con la licenza  
 * GNU General Public License, Version 2. La licenza accompagna il software
 * o potete trovarne una copia alla Free Software Foundation http://www.fsf.org .
 *
 * This software is subject to, and may be distributed under, the
 * GNU General Public License, Version 2. The license should have
 * accompanied the software or you may obtain a copy of the license
 * from the Free Software Foundation at http://www.fsf.org .
 * 
 * --
 * Marco Ceccarelli (m.ceccarelli@tnx.it)
 * Tnx snc (http://www.tnx.it)
 *
 */



/*
 * BeanAdapterTableModel.java
 *
 * Created on 24 maggio 2007, 14.36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author mceccarelli
 */
public class BeanAdapterTableModel extends AbstractTableModel {
    ArrayList beans;
    String[][] columns;
    
    public ArrayList getBeans() {
        return beans;
    }
    
    /** Creates a new instance of BeanAdapterTableModel */
    public BeanAdapterTableModel(ArrayList beans, String[][] columns) {
        this.beans = beans;
        this.columns = columns;
    }
    
    public int getColumnCount() {
        return columns.length;
    }
    
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object bean = beans.get(rowIndex);
        Object ret = null;
        try {
            String prop = columns[columnIndex][0];
            prop = prop.substring(0, 1).toUpperCase() + prop.substring(1);
            ret = bean.getClass().getMethod("get" + prop).invoke(bean);
        } catch (IllegalAccessException err1) {
            System.out.println(err1);
        } catch (NoSuchMethodException err2) {
            System.out.println(err2);
        } catch (InvocationTargetException err3) {
            System.out.println(err3);
        }
        return ret;
    }
    
    public int getRowCount() {
        return beans.size();
    }

    public String getColumnName(int column) {
        return columns[column][1];
    }
    
}
