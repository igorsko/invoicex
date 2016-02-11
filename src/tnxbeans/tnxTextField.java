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
import java.beans.*;
import java.awt.event.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class tnxTextField extends javax.swing.JTextField implements Serializable, BasicField {

    public String dbNomeCampo;
    public String dbTipoCampo;
    public String dbDescCampo;    
    public int maxChars = 0;
    public tnxComboField dbComboAbbinata;
    public tnxComboField dbComboSecondaria;
    public boolean dbRiempire = true;
    public boolean dbSalvare = true;
    private boolean dbNullSeVuoto = false;
    public boolean dbModificato = false;
    private String dbMask = "";
    public static String MASK_DATE = "D";
    private static String MASK_DATE_sample = "__/__/__";
    private String dbDefault = "";
    public static String DEFAULT_CURRENT = "CURRENT";
    private Integer dbDecimaliMin;
    private Integer dbDecimaliMax;

    public tnxTextField() {
        super();
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public String toString() {
        return "tnxTextField " + dbNomeCampo;
    }

    //propriet?
    public void setDbDefault(String valore) {
        this.dbDefault = valore;
    }

    public String getDbDefault() {
        return this.dbDefault;
    }

    public void setDbMask(String mask) {
        this.dbMask = mask;

        if (mask.equals(this.MASK_DATE)) {
            this.setText("__/__/__");
        } else {
            this.setText("");
        }
    }

    public String getDbMask() {
        return this.dbMask;
    }

    public void setDbComboAbbinata(tnxComboField _combo) {
        try {
            if (_combo != null) {
                dbComboAbbinata = _combo;
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public tnxComboField getDbComboAbbinata() {
        try {
            return dbComboAbbinata;
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
    }

    public void setDbComboSecondaria(tnxComboField _combo) {
        dbComboSecondaria = _combo;
    }

    public tnxComboField getDbComboSecondaria() {
        return dbComboSecondaria;
    }

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

    public void setmaxChars(int chars) {
        maxChars = chars;
        LimitedTextPlainDocument limit = new LimitedTextPlainDocument(chars);
        String prima = getText();
        try {
            if (prima.length() > chars) {
                limit.insertString(0, prima.substring(0, chars), null);
            } else {
                limit.insertString(0, prima, null);
            }
        } catch (Exception ex) {
        }
        setDocument(limit);
    }

    public int getmaxChars() {
        return maxChars;
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

            public void focusGained(FocusEvent e) {
                this_focusGained(e);
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

        this.addMouseListener(new java.awt.event.MouseListener() {

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mouseClicked(MouseEvent e) {
                //this_focusGained(null);
            }
        });

    }

    void this_keyTyped(KeyEvent e) {
        //controlla se digitano il punto gli mando la virgola

        if (dbTipoCampo != null) {
            if (dbTipoCampo.equalsIgnoreCase("valuta") || dbTipoCampo.equalsIgnoreCase("numerico")) {
                if (e.getKeyChar() == '.') {
                    e.setKeyChar(',');
                }
            }
        }

        if ("numero".equals(dbNomeCampo)) {
            System.out.println("debug");
        }

        dbModificato = true;
        if (this.getParent() instanceof tnxDbPanel) {
            tnxDbPanel temp = (tnxDbPanel) this.getParent();
            if (temp.getParentPanel() != null) temp = temp.getParentPanel();
            boolean res = temp.dbCheckModificati();
        }
    }

    void this_focusLost(FocusEvent e) {
        try {
            //avvaloro ultimo campo usato in panel
            if (this.getParent() instanceof tnxDbPanel) {
                tnxDbPanel temp = (tnxDbPanel) this.getParent();
                if (temp.getParentPanel() != null) temp = temp.getParentPanel();
                temp.ultimoCampo = this.getDbNomeCampo();
                temp.ultimoValore = this.getText();
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    void this_focusGained(FocusEvent e) {
        //debug
        //System.out.println("focus gain:" + this.getSelectionStart());
        //select all text
        //18/09/07 solo se non � gi� tutto selezionato
        try {
            if (!(getSelectionStart() == 0 && getSelectionEnd() == getText().length())) {
                setSelectionStart(0);
                setSelectionEnd(this.getText().length());
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public void aggiornaComboAbbinata() {
        if (getDbComboAbbinata() != null) {
            System.err.println("aggiornaComboAbbinata getDbComboAbbinata:" + getDbComboAbbinata());
            getDbComboAbbinata().dbTrovaKey(String.valueOf(this.getText()));
        }
    }

    void this_keyReleased(KeyEvent e) {
        //controllo che non vada fuori max chars
//        if (e.getKeyCode() != 8 &&
//                e.getKeyCode() != 37 &&
//                e.getKeyCode() != 39 &&
//                e.getKeyCode() != 127) {
//            if (maxChars > 0 && this.getText().length() > maxChars) {
//                this.setText(this.getText().substring(0,maxChars));
//                //e.setKeyCode(0);
//            }
//        }

        if (this.getDbComboAbbinata() != null) {
            //debug
            //javax.swing.JOptionPane.showMessageDialog(null,String.valueOf(this.getText()));
            this.getDbComboAbbinata().dbTrovaKey(String.valueOf(this.getText()));
        }

        //se il campo e' di tipo data
        if (dbTipoCampo != null) {
            if (dbTipoCampo.equalsIgnoreCase("data")) {


                //controllo se viene inserito il carattere '/' e lo elimino
                if (e.getKeyChar() == '/') {
                    //System.out.println("key1:" + String.valueOf(e.getKeyCode()) + ":" + e.getKeyChar());
                    this.setText(this.getText().substring(0, this.getText().length() - 1));
                //System.out.println("key2:" + String.valueOf(e.getKeyCode()) + ":" + e.getKeyChar());
                }

                //e metto gli aiuti per scrivere correttamente la data
                if (((this.getText().length() == 2) || (this.getText().length() == 5)) && e.getKeyCode() != 8) {
                    //System.out.println("key1:" + String.valueOf(e.getKeyCode()) + ":" + e.getKeyChar());
                    //System.out.println("dbg keycode/char:" + String.valueOf(e.getKeyCode()) + "/" + e.getKeyChar());
                    this.setText(this.getText() + "/");
                //System.out.println("key2:" + String.valueOf(e.getKeyCode()) + ":" + e.getKeyChar());
                }
            }
        }
    }

    void this_propertyChange(PropertyChangeEvent e) {
        //debug
        //javax.swing.JOptionPane.showMessageDialog(null,e.getPropertyName());
    }

    void this_keyPressed(KeyEvent e) {
        //MC 230403 provo a commentare  dato che ?? duplicato in typed
    /*
        dbModificato=true;

        //controlla se entra nel massimo dei caratteri
        int charsTyped = 0;
        if (this.maxChars != 0) {
        charsTyped = this.getText().length();
        if (charsTyped >= this.maxChars) {
        String nuovoTesto = this.getText().substring(0, (maxChars - 1));
        this.setText(nuovoTesto);
        }
        }
        tnxDbPanel temp = (tnxDbPanel)this.getParent();
        boolean res = temp.dbCheckModificati();
         */
    }

    String getTextNumber() {
        java.text.NumberFormat form = java.text.NumberFormat.getInstance();
        return (form.format(this.getText()));
    }

    public void setText(String testo) {
//        if ("numero".equals(dbNomeCampo)) {
//            System.out.println("debug");
//        }

        if (this.getParent() != null) {
            if (this.getParent() instanceof tnxDbPanel) {
                tnxDbPanel temp = (tnxDbPanel) this.getParent();
                if (temp.getParentPanel() != null) temp = temp.getParentPanel();
                if (temp != null && !temp.isRefreshing) {
                    dbModificato = true;
                    boolean res = temp.dbCheckModificati();
                }
            }
        }

        if (this.dbMask.equals(this.MASK_DATE)) {
            if (testo.length() == 0) {
                testo = "__/__/__";
            } else if (testo.length() < 8) {
                testo = testo + this.MASK_DATE_sample.substring(testo.length(), 8);
            } else if (testo.length() == 8) {
                testo = testo + this.MASK_DATE_sample.substring(testo.length(), 8);
            } else {
                testo = "#";
            }
        }
        super.setText(testo);
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

    public boolean isDbNullSeVuoto() {
        return dbNullSeVuoto;
    }

    public void setDbNullSeVuoto(boolean dbNullSeVuoto) {
        this.dbNullSeVuoto = dbNullSeVuoto;
    }

    /**
     * @return the dbDecimaliMin
     */
    public Integer getDbDecimaliMin() {
        return dbDecimaliMin;
    }

    /**
     * @param dbDecimaliMin the dbDecimaliMin to set
     */
    public void setDbDecimaliMin(Integer dbDecimaliMin) {
        this.dbDecimaliMin = dbDecimaliMin;
    }

    /**
     * @return the dbDecimaliMax
     */
    public Integer getDbDecimaliMax() {
        return dbDecimaliMax;
    }

    /**
     * @param dbDecimaliMax the dbDecimaliMax to set
     */
    public void setDbDecimaliMax(Integer dbDecimaliMax) {
        this.dbDecimaliMax = dbDecimaliMax;
    }
}
