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

import gestioneFatture.main;
import it.tnx.accessoUtenti.Permesso;
import it.tnx.commons.CastUtils;
import it.tnx.commons.DbUtils;
import java.io.Serializable;
import java.awt.*;
import java.awt.event.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.*;
import org.apache.commons.lang.StringUtils;

public class tnxComboField extends javax.swing.JComboBox implements Serializable {

    public String dbNomeCampo;
    public String dbTipoCampo;
    public String dbDescCampo;
    private boolean locked = false;
    public tnxTextField dbTextAbbinato;
    public tnxComboField dbComboSecondaria;
    public boolean dbRiempire = true;
    public boolean dbRiempireForceText = false;
    public boolean dbSalvare = true;
    public boolean dbModificato = false;
    public boolean dbTrovaMentreScrive = false;
    public Vector dbItems = new Vector();
    public Vector dbItemsK = new Vector();
    public boolean isOpeningList = false;
    public boolean isFindingKey = false;
    private boolean dbSalvaKey = true;
    private Connection connection;
    private java.sql.Statement stat;
    private String oldSql;
    private Connection oldConnection;
    private String selText = "";
    public boolean contieneChiavi = false;
    public long timeLastKey = 0;
    public boolean isFound = false;
    public DefaultComboBoxModel cm = new DefaultComboBoxModel();
    private boolean azzeraTextAbbinato = true;
//    public timerFindItems timFind;
//    public timerFindItemsContinuos timFindC;
//    public java.util.Timer tim;
    private boolean lazy = false;
    private boolean rinominaDuplicati = false;

    public tnxComboField() {
        super();
        try {
            jbInit();
//            timFindC = new timerFindItemsContinuos(this);
//            timFindC.setPriority(java.lang.Thread.MIN_PRIORITY);
//            timFindC.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void dbRefreshItems() {
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        this.dbClearList();
        this.dbOpenList(null, null, null, false);
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    //propriet???
    public void setDbSalvaKey(boolean value) {
        this.dbSalvaKey = value;
    }

    public boolean getDbSalvaKey() {
        return this.dbSalvaKey;
    }

    public void setLocked(boolean value) {
        this.locked = value;
    }

    public boolean getLock() {
        return (locked);
    }

    public void setConnection(Connection conn) {
        this.connection = conn;
    }

    public Connection getConnection() {
        return (this.connection);
    }

    public void setDbSalvare(boolean _flag) {
        dbSalvare = _flag;
    }

    public boolean getDbSalvare() {
        return dbSalvare;
    }

    public void setDbTextAbbinato(tnxTextField _text) {
        dbTextAbbinato = _text;
    }

    public tnxTextField getDbTextAbbinato() {
        return dbTextAbbinato;
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

    public void setDbTipoCampo(String _nomeCampo) {
        dbTipoCampo = _nomeCampo;
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

    public void setDbRiempireForceText(boolean _flag) {
        dbRiempireForceText = _flag;
    }

    public boolean getDbRiempireForceText() {
        return dbRiempireForceText;
    }

    public void setDbTrovaMentreScrive(boolean _trova) {
        dbTrovaMentreScrive = _trova;
    }

    public boolean getDbTrovaMentreScrive() {
        return dbTrovaMentreScrive;
    }

    public void setText(String testo) {
        try {
            ((JTextField) getEditor().getEditorComponent()).setText(testo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Component getCompByClass(JComponent container, Class clazz) {
        for (Component comp : container.getComponents()) {
            if (clazz.isInstance(comp)) {
                return comp;
            }
        }
        return null;
    }

    public void dbTrovaKey(Object chiave) {
        boolean trovato = false;
        this.isFindingKey = true;

        if (!lazy) {
            for (int i = 0; i < this.dbItemsK.size(); i++) {
                boolean found = false;

                if ("numerico".equalsIgnoreCase(dbTipoCampo)) {
                    if (CastUtils.toDouble0(this.dbItemsK.get(i)).doubleValue() == CastUtils.toDouble0(chiave).doubleValue()) {
                        found = true;
                    }
                } else {
                    if (String.valueOf(this.dbItemsK.get(i)).equalsIgnoreCase(String.valueOf(chiave))) {
                        found = true;
                    }
                }
                if (found) {
                    if (this.getItemCount() > i) {
                        final int fi = i;
                        //faceva andare in modifica la form perch� lo eseguiva dopo e il refreshing era finito...
                        //                    SwingUtilities.invokeLater(new Runnable() {
                        //                        public void run() {
                        setSelectedIndex(fi);
                        //                        }
                        //                    });
                    }
                    trovato = true;
                    this.isFound = true;
                    i = this.dbItemsK.size();
                    break;
                }
            }
            if (getName() != null && getName().equals("comClie")) {
                System.err.println("dbTrovaKey trovato:" + trovato + " chiave:" + chiave);
            }
            if (trovato == false) {
                this.setSelectedIndex(-1);
            }
        } else {
            try {
                String sql = oldSql;
                sql = sql.toLowerCase();
                sql = StringUtils.substringBefore(sql, " order by ");
                String key_field = StringUtils.substringBefore(StringUtils.substringAfter(sql, ","), " from ");
                sql += " where " + key_field + " = '" + DbUtils.aa(chiave.toString()) + "'";
                System.out.println("tnxCombo dbTrovaKey lazy chiave:" + chiave + " sql:" + sql);
                setText((String) DbUtils.getListMap(oldConnection, sql).get(0).get(dbNomeCampo));
            } catch (Exception e) {
                setText("");
            }
        }
        this.isFindingKey = false;
    }

    public String getText() {
        try {
            if (this.getComponentCount() == 3) {
                JTextField temp = (JTextField) getCompByClass(this, JTextField.class);
                return (temp.getText());
            } else {
                return (this.getSelectedItem().toString());
            }
        } catch (Exception err) {
            return "";
        }
    }

    public void setAzzeraTextAbbinato(boolean azzera) {
        this.azzeraTextAbbinato = azzera;
    }

    public boolean isAzzeraTextAbbinato() {
        return azzeraTextAbbinato;
    }

    private void jbInit() throws Exception {
        this.setEditable(true);
        JTextField temp = (JTextField) getCompByClass(this, JTextField.class);
        temp.addFocusListener(new java.awt.event.FocusAdapter() {

            public void focusLost(FocusEvent e) {
                this_focusLost(e);
            }

            public void focusGained(FocusEvent e) {
                this_focusGained(e);
            }
        });
        this.addItemListener(new java.awt.event.ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                this_itemStateChanged(e);
            }
        });
        this.addKeyListener(new java.awt.event.KeyAdapter() {

            public void keyTyped(KeyEvent e) {
                this_keyTyped(e);
            }
        });
        temp.addKeyListener(new java.awt.event.KeyAdapter() {

            public void keyTyped(KeyEvent e) {
                this_keyTyped(e);
            }

            public void keyPressed(KeyEvent e) {
                this_keyPressed(e);
            }

            public void keyReleased(KeyEvent e) {
                this_keyReleased(e);
            }
        });

        temp.addMouseListener(new java.awt.event.MouseListener() {

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mouseClicked(MouseEvent evt) {
                this_mouseClicked(evt);
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
        //debug
        //javax.swing.JOptionPane.showMessageDialog(null,"combo field keyTyped");
        //System.out.println("tnxCombo:locked="+locked);

        if (locked == true) {
            e.setKeyCode(0);
            //System.out.println("tnxCombo:locked="+locked+"then");
        } else {

            if (this.dbNomeCampo != null && this.dbNomeCampo.length() > 0) {
                tnxDbPanel temp = (tnxDbPanel) this.getParent();
                if (temp.getParentPanel() != null) {
                    temp = temp.getParentPanel();
                }
                if (temp != null && !temp.isRefreshing) {
                    dbModificato = true;
                    boolean res = temp.dbCheckModificati();
                }
            }
            //System.out.println("tnxCombo:locked="+locked+"else");
        }
    }

    public void syncToText() {
        dbTextAbbinato.setText(String.valueOf(this.dbItemsK.get(this.getSelectedIndex())));
    }

    void this_itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            if (this.isFindingKey == false) {
                if (dbTextAbbinato != null) {
                    if (this.getSelectedIndex() >= 0 && this.getSelectedIndex() < this.dbItemsK.size()) {
                        dbTextAbbinato.setText(String.valueOf(this.dbItemsK.get(this.getSelectedIndex())));
                    }
                }

                if (this.getParent() != null) {
                    if (this.getParent() instanceof tnxDbPanel) {
                        tnxDbPanel temp = (tnxDbPanel) this.getParent();
                        if (temp.getParentPanel() != null) {
                            temp = temp.getParentPanel();
                        }
                        if (temp != null && !temp.isRefreshing) {
                            dbModificato = true;
                            boolean res = temp.dbCheckModificati();
                        }
                    }
                }
            }
        }
//        if (e.getStateChange() == e.DESELECTED) {
//            if (getSelectedItem() == null) {
//                if (dbTextAbbinato != null) {
//                    dbTextAbbinato.setText("");
//                }
//            }
//        }
    }

    void this_mouseClicked(MouseEvent evt) {
        if (this.getParent() instanceof tnxDbPanel) {
            tnxDbPanel temp = (tnxDbPanel) this.getParent();
            if (temp.getParentPanel() != null) {
                temp = temp.getParentPanel();
            }
            if (!temp.permesso_scrittura) {
                return;
            }
            if (temp != null && !temp.isRefreshing) {
                dbModificato = true;
                boolean res = temp.dbCheckModificati();
            }
        }

        if (evt.getModifiers() == evt.BUTTON3_MASK) {
            //javax.swing.JOptionPane.showMessageDialog(null,"ckick destrio");
            //faccio apparire il menu per fare refresh
            MenuUtil temp = new MenuUtil();
            temp.show(this, evt.getX(), evt.getY());
        }
    }

    void this_keyPressed(KeyEvent e) {
        //debug
        //javax.swing.JOptionPane.showMessageDialog(null,"pre");

        JTextField tex = (JTextField) getCompByClass(this, JTextField.class);
        this.selText = tex.getSelectedText();

        //debug
        //System.out.println("selText:"+selText);
        //super.processKeyEvent(e);
        if (e != null) {
            if (e.getKeyCode() == e.VK_ENTER || e.getKeyCode() == e.VK_F4) {
                super.processKeyEvent(e);
            }
        }
    }

    void this_keyReleased(KeyEvent e) {
        //metto timeout fra tasto ed altro che se ??? maggiore di un secondo allora cerca senno' falsecontinuare ad inserire
        this.timeLastKey = System.currentTimeMillis();
        this.isFound = false;
    }

    void this_focusLost(FocusEvent e) {
//        if (timFind != null) {
//            timFind.cancel();
//        }

        if (isAzzeraTextAbbinato()) {
            if (dbTextAbbinato != null) {
                if (this.getText().length() == 0) {
                    dbTextAbbinato.setText("");
                }
            }
        }

        ultimoCampo();

        super.processFocusEvent(e);
    }

    void ultimoCampo() {
        //avvaloro ultimo campo usato in panel
        if (this.getParent() != null) {
            if (getParent() instanceof tnxDbPanel) {
                tnxDbPanel temp = (tnxDbPanel) this.getParent();
                if (temp.getParentPanel() != null) {
                    temp = temp.getParentPanel();
                }
                temp.ultimoCampo = this.getDbNomeCampo();
                if (this.getSelectedItem() == null) {
                    temp.ultimoValore = "";
                } else {
                    temp.ultimoValore = this.getSelectedItem().toString();
                }
            }
        }
    }

    public void dbClearList() {
        System.out.println("combo clear list: " + getName());
        dbItems.clear();
        dbItemsK.clear();
        this.removeAllItems();
    }

    public boolean dbOpenList(Connection connection, String sql, final String valoreSelezionato, boolean usa_thread) {
        usa_thread = false;

        if (tnxDbPanel.debug) {
            System.out.println("dbOpenList3:" + sql);
        }
        this.isOpeningList = true;

        if (connection == null) {
            connection = this.oldConnection;
            sql = this.oldSql;
        } else {
            this.oldSql = sql;
            this.oldConnection = connection;
        }

        if (!lazy) {
            if (usa_thread == true) {
                try {
                    TRiempiCombo temp = new TRiempiCombo(sql, connection, this, valoreSelezionato);
                    temp.start();
                    return (true);
                } catch (Exception e) {
                    javax.swing.JOptionPane.showMessageDialog(null, e.toString());
                    e.printStackTrace();
                    return (false);
                }
            } else {
                if (SwingUtilities.isEventDispatchThread()) {
                    ResultSet resu = null;
                    ResultSetMetaData meta = null;
                    try {
                        stat = connection.createStatement();
                        resu = stat.executeQuery(sql);
                        meta = resu.getMetaData();
                        if (meta.getColumnCount() > 1) {
                            this.contieneChiavi = true;
                        }
                        //righe
                        while (resu.next()) {
                            for (int i = 1; i <= meta.getColumnCount(); ++i) {
                                if (i == 1) {
                                    String s = resu.getString(i);
                                    s = pulisci(s);
                                    if (rinominaDuplicati) {
                                        if (dbItems.contains(s)) {
                                            s += " [" + resu.getString(2) + "]";
                                        }
                                    }
                                    this.dbItems.add(s);
                                    cm.addElement(s);
                                } else {
                                    this.dbItemsK.add((Object) resu.getString(i));
                                }
                            }
                        }
                    } catch (Exception err) {
                        err.printStackTrace();
                    } finally {
                        try {
                            stat.close();
                        } catch (Exception e) {
                        }
                        try {
                            resu.close();
                        } catch (Exception e) {
                        }
                        meta = null;
                    }
                    this.setModel(cm);
                    this.cm = cm;

                    if (this.dbTrovaMentreScrive == true) {
//                        AutoCompletion.enable(this);
                        it.tnx.commons.AutoCompletionEditable.enable(this);
                    }

                    if (this.dbTextAbbinato != null) {
                        if (valoreSelezionato != null && valoreSelezionato.length() > 0) {
                            this.dbTrovaKey(valoreSelezionato);
                        } else {
                            //                    this.setSelectedItem(null);
                            try {
                                this.setSelectedIndex(0);
                            } catch (IllegalArgumentException err) {
                            }
                        }
                    } else {
                        if (valoreSelezionato != null && valoreSelezionato.length() > 0) {
                            this.dbTrovaKey(valoreSelezionato);
                        } else {
                            //                    this.setSelectedItem(null);
                            try {
                                this.setSelectedIndex(0);
                            } catch (IllegalArgumentException err) {
                            }
                        }
                    }
                } else {
                    //lavoro fuori da EDT
                    //                System.err.println("tnxComboField fuori EDT");
                    ResultSet resu = null;
                    ResultSetMetaData meta = null;
                    final ArrayList cm_a = new ArrayList();
                    try {
                        stat = connection.createStatement();
                        resu = stat.executeQuery(sql);
                        meta = resu.getMetaData();
                        if (meta.getColumnCount() > 1) {
                            this.contieneChiavi = true;
                        }
                        //righe
                        while (resu.next()) {
                            for (int i = 1; i <= meta.getColumnCount(); ++i) {
                                if (i == 1) {
                                    String s = resu.getString(i);
                                    s = pulisci(s);
                                    if (rinominaDuplicati) {
                                        if (dbItems.contains(s)) {
                                            s += " [" + resu.getString(2) + "]";
                                        }
                                    }
                                    this.dbItems.add(s);
                                    cm_a.add(s);
                                } else {
                                    this.dbItemsK.add((Object) resu.getString(i));
                                }
                            }
                        }
                    } catch (Exception err) {
                        err.printStackTrace();
                    } finally {
                        try {
                            stat.close();
                        } catch (Exception e) {
                        }
                        try {
                            resu.close();
                        } catch (Exception e) {
                        }
                        meta = null;
                    }
                    //invoco aggiornamento gui in EDT
                    SwingUtilities.invokeLater(new Runnable() {

                        public void run() {
                            for (Object obj : cm_a) {
                                cm.addElement(obj);
                            }
                            tnxComboField.this.setModel(cm);
                            if (tnxComboField.this.dbTrovaMentreScrive == true) {
//                                AutoCompletion.disable(tnxComboField.this);
//                                AutoCompletion.enable(tnxComboField.this);
                            }

                            if (tnxComboField.this.dbTextAbbinato != null) {
                                if (valoreSelezionato != null && valoreSelezionato.length() > 0) {
                                    tnxComboField.this.dbTrovaKey(valoreSelezionato);
                                } else {
                                    try {
                                        tnxComboField.this.setSelectedIndex(0);
                                    } catch (IllegalArgumentException err) {
                                    }
                                }
                            } else {
                                if (valoreSelezionato != null && valoreSelezionato.length() > 0) {
                                    tnxComboField.this.dbTrovaKey(valoreSelezionato);
                                } else {
                                    try {
                                        tnxComboField.this.setSelectedIndex(0);
                                    } catch (IllegalArgumentException err) {
                                    }
                                }
                            }
                        }
                    });
                }

                return (true);
            }
        } else {
            return true;
        }

    }

    public void dbTrovaRiga(String chiave) {
        boolean trovato = false;
        this.isFindingKey = true;

        if (this.dbItems.size() > 0) {
            for (int i = 0; i < this.dbItems.size(); i++) {
                if (String.valueOf(this.dbItems.get(i)).equalsIgnoreCase(String.valueOf(chiave))) {
                    this.setText(chiave);
                    this.setSelectedIndex(i);

                    trovato = true;
                    this.isFound = true;
                    i = this.dbItems.size();
                }
            }
            if (trovato == false) {
                this.setSelectedIndex(-1);
            }
        } else {
            //se non ho caricato dbItems vado direttamente sui valori nella lista
            for (int i = 0; i < this.getItemCount(); i++) {
                if (String.valueOf(this.getItemAt(i)).equalsIgnoreCase(String.valueOf(chiave))) {
                    this.setSelectedIndex(i);
                    trovato = true;
                    this.isFound = true;
                    i = this.getItemCount();
                }
            }
            if (trovato == false) {
                this.setSelectedIndex(-1);
            }
        }
        this.isFindingKey = false;
    }

    public boolean dbOpenList(Connection connection, String sql, String valoreSelezionato) {
//        System.out.println("dbOpenList1:" + sql);
//        this.isOpeningList = true;
//
//        this.oldSql = sql;
//        this.oldConnection = connection;
//
//        //apre il resultset da abbinare
//        if (!lazy) {
//            try {
//                //thread
//                TRiempiCombo temp = new TRiempiCombo(sql,connection,this,valoreSelezionato);
//                temp.start();
//                return(true);
//            } catch (Exception e) {
//                javax.swing.JOptionPane.showMessageDialog(null,e.toString());
//                e.printStackTrace();
//                return(false);
//            }
//        } else {
//            return true;
//        }

        return dbOpenList(connection, sql, valoreSelezionato, false);
    }

    public boolean dbOpenList(Connection connection, String sql) {
//        System.out.println("dbOpenList2:" + sql);
//        //dbClearList();
//        this.isOpeningList = true;
//        this.oldSql = sql;
//        this.oldConnection = connection;
//
//        if (!lazy) {
//            try {
//                TRiempiCombo temp = new TRiempiCombo(sql,connection,this);
//                System.out.println("sql open list:" + sql);
//                temp.start();
//                while (temp.isAlive()) {
//                    Thread.yield();
//                }
//                return(true);
//            } catch (Exception e) {
//                javax.swing.JOptionPane.showMessageDialog(null,e.toString());
//                e.printStackTrace();
//                return(false);
//            }
//        } else {
//            return true;
//        }

        return dbOpenList(connection, sql, null, false);
    }

    public void dbAddElement(Object testo, Object valore) {
        this.cm.addElement(testo);
        this.setModel(cm);
        dbItems.add(testo);
        dbItemsK.add(valore);
    }

    public void dbAddElement(String testo, String valore) {
        this.cm.addElement((Object) testo);
        this.setModel(cm);
        dbItems.add((Object) testo);
        dbItemsK.add((Object) valore);
    }

    public void dbAddElement(String testo) {
        this.cm.addElement((Object) testo);
        this.setModel(cm);
        dbItems.add((Object) testo);
        dbItemsK.add((Object) testo);
    }

    public void dbAddElement(String testo, String valore, int pos) {
        this.cm.insertElementAt((Object) testo, pos);
        this.setModel(cm);
        dbItems.insertElementAt((Object) testo, pos);
        dbItemsK.insertElementAt((Object) valore, pos);
    }

    public Object getSelectedKey() {
        if (this.dbItemsK.size() > 0) {
            //passo la chiave abbinata
            try {
                if (this.getSelectedIndex() == -1) {
                    return (new Integer(-1));
                } else {
                    return (this.dbItemsK.get(this.getSelectedIndex()));
                }
            } catch (Exception err) {
                err.printStackTrace();
                return (new Integer(-1));
            }
        } else {
            return (new Integer(-1));
        }
    }

    public Object getKey(int i) {
        if (this.contieneChiavi == true) {
            //passo la chiave abbinata
            try {
                return (this.dbItemsK.get(i));
            } catch (Exception err) {
                err.printStackTrace();
                return (new Integer(-1));
            }
        } else {
            return (new Integer(-1));
        }
    }

    void this_focusGained(FocusEvent e) {
        //select all text
        JTextField temp = (JTextField) getCompByClass(this, JTextField.class);

        temp.setSelectionStart(0);
        temp.setSelectionEnd(temp.getText().length());

        super.processFocusEvent(e);
    }

    /*
     public void findItems() {
     //ricerco quello inserito
     if (dbTrovaMentreScrive == true) {
     String testo = "";
     String testoItem = "";
     boolean trovato = false;
     JTextField tex = (JTextField) getCompByClass(this, JTextField.class);
     testo = tex.getText();

     if (testo.trim().length() == 0) {
     return;
     }

     //se dbItems ??? vuoto potrebbe essere ripreso dai valori inseriti manualmente
     if (dbItems.size() == 0) {
     for (int i2 = 0; i2 < this.getItemCount(); i2++) {
     dbItems.add(this.getItemAt(i2));
     }
     }

     for (int i = 0; i < dbItems.size(); i++) {
     if (dbItems.get(i) == null) {
     testoItem = "";
     } else {
     testoItem = dbItems.get(i).toString();
     }

     if (testoItem.length() >= testo.length()) {
     if (testoItem.substring(0, testo.length()).equalsIgnoreCase(testo)) {
     trovato = true;
     int selStart = 0;
     int selEnd = 0;
     selStart = (testo.length());
     if (this.getItemCount() > 0) {
     this.setSelectedIndex(i);
     }
     tex.setText(dbItems.get(i).toString());
     selEnd = tex.getText().length();

     if (dbTextAbbinato != null) {
     dbTextAbbinato.setText(this.getSelectedKey().toString());
     System.out.println("cliente t2:" + this.getSelectedKey().toString());
     }

     tex.setSelectionStart(selStart);
     tex.setSelectionEnd(selEnd);

     i = dbItems.size();
     }
     }
     }
     }
     }
     */
    public void setBackgroundTextComponent(java.awt.Color color) {
        JTextField temp = (JTextField) getCompByClass(this, JTextField.class);
        temp.setBackground(color);
    }

    protected void finalize() throws Throwable {
        super.finalize();
//        System.out.println(this + " finalize");
    }

    public boolean isLazy() {
        return lazy;
    }

    public void setLazy(boolean lazy) {
        this.lazy = lazy;
    }

    public boolean isRinominaDuplicati() {
        return rinominaDuplicati;
    }

    public void setRinominaDuplicati(boolean rinominaDuplicati) {
        this.rinominaDuplicati = rinominaDuplicati;
    }

    private String pulisci(String s) {
        s = StringUtils.replace(s, "\n", " ");
        s = StringUtils.replace(s, "\t", " ");
        s = StringUtils.replace(s, "\r", " ");
        return s;
    }
}

class TRiempiCombo extends Thread {

    Connection connection;
    java.sql.Statement stat;
    ResultSet resu;
    ResultSetMetaData meta;
    String sql;
    int numeColo;
    tnxComboField tabella;
    String valoreSelezionato;

    public TRiempiCombo(String sql, Connection connection, tnxComboField tabella) {
        this.sql = sql;
        this.connection = connection;
        this.tabella = tabella;
        this.valoreSelezionato = null;
    }

    public TRiempiCombo(String sql, Connection connection, tnxComboField tabella, String valoreSelezionato) {
        this.sql = sql;
        this.connection = connection;
        this.tabella = tabella;
        this.valoreSelezionato = valoreSelezionato;
    }

    public void run() {
        this.setPriority(Thread.NORM_PRIORITY);
        tabella.setEnabled(false);

        final DefaultComboBoxModel cm = tabella.cm;

        ArrayList<Object[]> list = null;
        try {
            list = DbUtils.getListArray(connection, sql);
            if (list.get(0).length > 1) {
                tabella.contieneChiavi = true;
            }
        } catch (Exception err) {
            err.printStackTrace();
        }

        final ArrayList<Object[]> flist = list;

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                //righe
                for (Object[] row : flist) {
                    for (int i = 1; i <= row.length; ++i) {
                        if (i == 1) {
                            tabella.dbItems.add(row[i - 1]);
                            cm.addElement(row[i - 1]);
                        } else {
                            tabella.dbItemsK.add(row[i - 1]);
                        }
                    }
                }

                tabella.setModel(cm);
                tabella.cm = cm;

//                AutoCompletion.enable(tabella);
                it.tnx.commons.AutoCompletionEditable.enable(tabella);

                if (tabella.dbTextAbbinato != null) {
                    if (valoreSelezionato != null) {
                        tabella.dbTrovaKey(valoreSelezionato);
                    } else {
                        tabella.setSelectedItem(null);
                    }
                    if (tabella.dbTextAbbinato.getText().length() == 0) {
                        tabella.setSelectedItem(null);
                    }
                }

                tabella.setEnabled(true);

                //lancio timer per ricerca testo dopo 5 secondi che ha premuto l'utlimo tasto
                //        if (tabella.dbTrovaMentreScrive == true) {
                //            tabella.tim.schedule(tabella.timFind,500,250);
                //        }
                tabella.isOpeningList = false;
            }
        });

    }
}

//class timerFindItems extends java.util.TimerTask {
//    tnxComboField parent;
//    
//    public timerFindItems(tnxComboField parent) {
//        this.parent = parent;
//    };
//    
//    public void run() {
//        try {
//            if ((System.currentTimeMillis() - parent.timeLastKey) > 100 && parent.isFound == false) {
//                parent.findItems();
//                parent.isFound = true;
//                parent.timeLastKey = System.currentTimeMillis();
//            }
//        } catch (Exception err) {
//            err.printStackTrace();
//        }
//    }
//}
//
//class timerFindItemsContinuos extends java.lang.Thread {
//    tnxComboField parent;
//    
//    public timerFindItemsContinuos(tnxComboField parent) {
//        this.parent = parent;
//    };
//    
//    public void run() {
//        while (1 == 1) {
//            try {
//                if ((System.currentTimeMillis() - parent.timeLastKey) > 200 && parent.isFound == false) {
//                    parent.findItems();
//                    parent.isFound = true;
//                    parent.timeLastKey = System.currentTimeMillis();
//                }
//                Thread.sleep(200);
//                Thread.yield();
//            } catch (Exception err) {
//                err.printStackTrace();
//            }
//        }
//    }
//}
class MenuUtil extends JPopupMenu {

    private ActionListenerCombo act;

    public MenuUtil() {
        javax.swing.JMenuItem menItem1;
        javax.swing.JMenuItem menItem2;
        menItem1 = this.add("Ricarica");
        menItem1.setIcon(new javax.swing.ImageIcon("./img/general/refresh16.gif"));
        //menItem2 = this.add("Vai a ->");

        menItem1.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                action(evt);
            }
        });
    }

    private void action(java.awt.event.ActionEvent evt) {
        //System.out.println("refresh della combo");

        try {
            tnxComboField temp = (tnxComboField) this.getInvoker();
            //Connection connection,String sql, String valoreSelezionato, boolean usa_thread
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            temp.dbClearList();
            temp.dbOpenList(null, null, null, false);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (Exception err) {
            err.printStackTrace();
        }
    }
}

class ActionListenerCombo implements ActionListener {

    public void actionPerformed(ActionEvent e) {
        javax.swing.JOptionPane.showMessageDialog(null, "action!!!");
    }
}
