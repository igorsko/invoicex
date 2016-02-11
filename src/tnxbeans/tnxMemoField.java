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
package tnxbeans;

import java.io.Serializable;
import java.awt.*;
import java.awt.event.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;

public class tnxMemoField extends javax.swing.JScrollPane implements Serializable, BasicField {

    public String dbNomeCampo;
    public String dbTipoCampo;
    public boolean dbRiempire = true;
    public boolean dbSalvare = true;
    public boolean dbModificato = false;

    JTextArea area = new JTextArea() {

        @Override
        public Component getNextFocusableComponent() {
            System.out.println("tnxMemoField.this.getNextFocusableComponent():" + tnxMemoField.this.getNextFocusableComponent());
            return tnxMemoField.this.getNextFocusableComponent();
        }

//        @Override
//        protected void processComponentKeyEvent(KeyEvent e) {
//            System.out.println("processComponentKeyEvent e: " + e);
//            super.processComponentKeyEvent(e);
//            if (e.getKeyCode() == KeyEvent.VK_TAB) {
//                transferFocus();
//                e.consume();
//            } else {
//                super.processComponentKeyEvent(e);
//            }
//        }
    };

    public tnxMemoField() {
        super();
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // bind our new forward focus traversal keys
		Set<AWTKeyStroke> newForwardKeys = new HashSet<AWTKeyStroke>(1);
		newForwardKeys.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB,0));
		area.setFocusTraversalKeys(
			KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
			Collections.unmodifiableSet(newForwardKeys)
		);
		// bind our new backward focus traversal keys
		Set<AWTKeyStroke> newBackwardKeys = new HashSet<AWTKeyStroke>(1);
		newBackwardKeys.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB,KeyEvent.SHIFT_MASK+KeyEvent.SHIFT_DOWN_MASK));
		area.setFocusTraversalKeys(
			KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
			Collections.unmodifiableSet(newBackwardKeys)
		);

        
    }

    //propriet?
    public void setDbSalvare(boolean _flag) {
        dbSalvare = _flag;
    }
    
    public boolean getDbSalvare() {
        return dbSalvare;
    }

    public void setDbNomeCampo(String _nomeCampo) {
        dbNomeCampo = _nomeCampo;
    }

    public String getDbNomeCampo() {
        return dbNomeCampo;
    }

    public void setDbRiempire(boolean _flag) {
        dbRiempire = _flag;
    }

    public boolean getDbRiempire() {
        return dbRiempire;
    }

    public void setText(String testo) {
        area.setText(testo);
    }

    public String getText() {
        return area.getText();
    }

    private void jbInit() throws Exception {
        area.setWrapStyleWord(true);
        area.setLineWrap(true);
        area.setText("");
        this.addFocusListener(new java.awt.event.FocusAdapter() {

            public void focusLost(FocusEvent e) {
                this_focusLost(e);
            }
        });
        area.addKeyListener(new java.awt.event.KeyAdapter() {

            public void keyTyped(KeyEvent e) {
                this_keyTyped(e);
            }
        });
        this.getViewport().add(area, null);
    }

    void this_keyTyped(KeyEvent e) {
        //debug
        //javax.swing.JOptionPane.showMessageDialog(null,"text field keyTyped");

        dbModificato = true;
        tnxDbPanel temp = (tnxDbPanel) this.getParent();
        if (temp.getParentPanel() != null) temp = temp.getParentPanel();
        boolean res = temp.dbCheckModificati();
    }

    void this_focusLost(FocusEvent e) {
        //avvaloro ultimo campo usato in panel
        tnxDbPanel temp = (tnxDbPanel) this.getParent();
        if (temp.getParentPanel() != null) temp = temp.getParentPanel();
        temp.ultimoCampo = this.getDbNomeCampo();
        temp.ultimoValore = this.getText();
    }

    public javax.swing.JTextArea getJTextArea() {
        return this.area;
    }

    public void setFont(Font f) {
        super.setFont(f);
        if (this.area != null) {
            area.setFont(f);
        }
    }

    public String getDbTipoCampo() {
        return dbTipoCampo;
    }

    public void setDbTipoCampo(String dbTipoCampo) {
        this.dbTipoCampo = dbTipoCampo;
    }

    public boolean isDbRiempire() {
        return dbRiempire;
    }

    public boolean isDbSalvare() {
        return dbSalvare;
    }

    public boolean isDbModificato() {
        return dbModificato;
    }

    public void setDbModificato(boolean dbModificato) {
        this.dbModificato = dbModificato;
    }

    public synchronized void addFocusListener(FocusListener l) {
        area.addFocusListener(l);
    }

}
