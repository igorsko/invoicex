/**
 * Invoicex Copyright (c) 2005,2006,2007 Marco Ceccarelli, Tnx snc
 *
 * Questo software è soggetto, e deve essere distribuito con la licenza GNU
 * General Public License, Version 2. La licenza accompagna il software o potete
 * trovarne una copia alla Free Software Foundation http://www.fsf.org .
 *
 * This software is subject to, and may be distributed under, the GNU General
 * Public License, Version 2. The license should have accompanied the software
 * or you may obtain a copy of the license from the Free Software Foundation at
 * http://www.fsf.org .
 *
 * -- Marco Ceccarelli (m.ceccarelli@tnx.it) Tnx snc (http://www.tnx.it)
 *
 */
package tnxbeans;

import java.io.Serializable;
import java.beans.*;
import java.awt.*;
import java.awt.event.*;

public class tnxCheckBox extends javax.swing.JCheckBox implements Serializable {

    public String dbNomeCampo;
    public String dbTipoCampo;
    public String dbDescCampo;
    public boolean dbRiempire = true;
    public boolean dbSalvare = true;
    public boolean dbModificato = false;
    public tnxDbPanel padre = null;

    public tnxCheckBox() {
        super();
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //proprieta'
    public void setDbNomeCampo(String _nomeCampo) {
        dbNomeCampo = _nomeCampo;
    }

    public String getDbNomeCampo() {
        return dbNomeCampo;
    }

    public void setDbTipoCampo(String _tipoCampo) {
        dbTipoCampo = _tipoCampo;
    }

    public String getDbTipoCampo() {
        return dbTipoCampo;
    }

    public void setDbDescCampo(String _DescCampo) {
        dbDescCampo = _DescCampo;
    }

    public String getDbDescCampo() {
        return dbDescCampo;
    }

    public void setDbRiempire(boolean _flag) {
        dbRiempire = _flag;
    }

    public boolean getDbRiempire() {
        return dbRiempire;
    }

    public void setDbSalvare(boolean _flag) {
        dbSalvare = _flag;
    }

    public boolean getDbSalvare() {
        return dbSalvare;
    }

    private void jbInit() throws Exception {
        this.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                this_propertyChange(e);
            }
        });

        this.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(FocusEvent e) {
                this_focusLost(e);
            }
        });
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                this_keyTyped(e);
            }

            public void keyReleased(KeyEvent e) {
                this_keyReleased(e);
            }

            public void keyPressed(KeyEvent e) {
                this_keyPressed(e);
            }
        });

        this.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                thisActionPerformed(evt);
            }
        });
    }

    void this_keyTyped(KeyEvent e) {
        //debug
        //javax.swing.JOptionPane.showMessageDialog(null,"text field keyTyped");
        dbModificato = true;

//    tnxDbPanel temp = (tnxDbPanel)this.getParent();

//    tnxDbPanel temp = getParent2();
//    boolean res = temp.dbCheckModificati();

        if (this.getParent() instanceof tnxDbPanel) {
            tnxDbPanel temp = (tnxDbPanel) this.getParent();
            if (temp.getParentPanel() != null) {
                temp = temp.getParentPanel();
            }
            boolean res = temp.dbCheckModificati();
        }
    }

    void thisActionPerformed(ActionEvent evt) {
        dbModificato = true;
//    tnxDbPanel temp = (tnxDbPanel)this.getParent();
        tnxDbPanel temp = getParent2();
        boolean res = temp.dbCheckModificati();
    }

    void this_focusLost(FocusEvent e) {
        try {
            //avvaloro ultimo campo usato in panel
//      tnxDbPanel temp = (tnxDbPanel)this.getParent();
            tnxDbPanel temp = getParent2();
            temp.ultimoCampo = this.getDbNomeCampo();
            temp.ultimoValore = this.getText();
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    void this_keyReleased(KeyEvent e) {
    }

    void this_propertyChange(PropertyChangeEvent e) {
        //debug
        //javax.swing.JOptionPane.showMessageDialog(null,e.getPropertyName());
    }

    void this_keyPressed(KeyEvent e) {
    }

    String getTextNumber() {
        java.text.NumberFormat form = java.text.NumberFormat.getInstance();
        return (form.format(this.getText()));
    }

    private tnxDbPanel getParent2() {
        if (padre != null) {
            return padre;
        }
        return (tnxDbPanel) getParent();
    }
}
