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

import gestioneFatture.main;
import it.tnx.DbI;
import it.tnx.DbUtilsTnxBeans;
import it.tnx.accessoUtenti.Permesso;
import it.tnx.commons.DbUtils;
import it.tnx.commons.DebugUtils;
import it.tnx.commons.FxUtils;
import it.tnx.commons.SwingUtils;
import it.tnx.dbeans.JTableDb;
import it.tnx.invoicex.InvoicexUtil;
import java.io.Serializable;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.JButton;
import java.text.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import org.apache.commons.lang.StringUtils;

public class tnxDbPanel extends javax.swing.JPanel implements Serializable {

    public String dbStato = "L";
    public String dbNomeTabella = "";
    public boolean dbEditabile = true;
    public Vector dbChiave;
    public Hashtable dbChiaveValori;
    public boolean dbChiaveAutoInc = false;
    public JButton butSave;
    public JButton butSaveClose;
    public JButton butUndo;
    public JButton butFind;
    public JButton butDele;
    public JButton butNew;
    public JButton butDuplica;
    public String ultimoCampo;
    public String ultimoValore;
    public static String DB_INSERIMENTO = "I";
    public static String DB_MODIFICA = "M";
    public static String DB_LETTURA = "L";
    public Integer tipo_permesso;
    boolean permesso_lettura = false;
    boolean permesso_scrittura = false;
    boolean permesso_cancella = false;
    public Connection connection;
    private java.sql.Statement stat;
    private ResultSet resu;
    private ResultSetMetaDataCached meta;
    private boolean noRecords = false;
    public boolean isOnSomeRecord = false;
    public boolean isRefreshing = false;
    public boolean showErrorSelectSingle = true;  //in alcuni casi, ad esempio quando si inserisce un record nel pannello dati ma che poi non andr??? nella griglia associata per via di un filtro visualizza un errore
    private String oldSql;
    private String oldSqldbopen;
    private java.sql.Connection oldConn;
    public tnxIntePanel riempiCampiSecondari;
    //events
    private List _listeners = new ArrayList();
    public static int STATUS_ADDING = 1;
    public static int STATUS_EDITING = 2;
    public static int STATUS_SAVING = 3;
    public static int STATUS_REFRESHING = 4;
    public static int STATUS_PRE_SAVING = 5;
//    static public boolean debug = true;
    static public boolean debug = false;
    public tnxDbGrid griglia = null;
    public JTableDb grigliaJTableDb = null;
    private Hashtable campiAggiuntivi = null;
    private boolean init_limits = false;
    private DbI db;
    private ArrayList<Component> altriCampi = null;
    public boolean saltaSincronizzaGriglia = false;
    public Integer last_inserted_id = null;
    private tnxDbPanel parentPanel;
    private ArrayList<tnxDbPanel> dbPanelsCollegati;
    private List<String> lang = null;
    public boolean nonSpostareSuTab0 = false;
    public boolean messaggio_nuovo_manuale = false;

    public tnxDbPanel() {
        super();
    }

    //events
    public synchronized void addDbListener(DbListener l) {
        _listeners.add(l);
    }

    public synchronized void removeDbListener(DbListener l) {
        _listeners.remove(l);
    }

    void sincronizzaSelezioneGriglia(tnxDbGrid griglia) {
        //provo a trovarlo nella griglia
        this.griglia = griglia;
        if (griglia != null) {
            HashMap key_current_panel = getKeyFromPanel();
            HashMap key_current_grid = getKeyFromGrid();
            if (key_current_grid.equals(key_current_panel)) {
                if (debug) {
                    System.out.println("*** key uguali");
                }
            } else {
                if (debug) {
                    System.out.println("*** key diverse");
                }
                if (saltaSincronizzaGriglia) {
                    if (debug) {
                        System.out.println("salto sincronizza");
                    }
                    return;
                }
                //lo trovo in grid
                boolean found = false;
//                for (int r = 0; r <= griglia.getRowCount(); r++) {
                for (int r = 0; r < griglia.getRowCount(); r++) {
                    HashMap key = getKeyFromGrid(r);
                    Object v1 = key.values().iterator().next();
                    Object v2 = key_current_panel.values().iterator().next();
//                    System.out.println("v1:" + v1 + " v2:" + v2 + " / " + v1.equals(v2));
                    if (key.equals(key_current_panel)) {
                        if (debug) {
                            System.out.println("*** imposto row:" + r);
                        }
                        griglia.getSelectionModel().setSelectionInterval(r, r);
                        if (!griglia.getVisibleRect().contains(griglia.getCellRect(griglia.getSelectedRow(), 1, true))) {
                            griglia.scrollToRow(griglia.getSelectedRow());
                        }
                        found = true;
                        break;
                    }
                }
                if (debug) {
                    System.out.println("*** key trovata:" + found);
                }
            }

        }
    }

//    private void deactivateComponent(Component comp) {
//        if (comp instanceof JScrollPane) {
//            JScrollPane pane = (JScrollPane) comp;
//            for (Component compPane : pane.getComponents()) {
//                deactivateComponent(compPane);
//            }
//        } else if (comp instanceof JViewport) {
//            JViewport viewPort = (JViewport) comp;
//            for (Component compPane : viewPort.getComponents()) {
//                deactivateComponent(compPane);
//            }
//        } else if (comp instanceof JTabbedPane) {
//            JTabbedPane tpane = (JTabbedPane) comp;
//            for (Component compPane : tpane.getComponents()) {
//                deactivateComponent(compPane);
//            }
//        } else if (comp instanceof JPanel) {
//            JPanel panel = (JPanel) comp;
//            for (Component compPane : panel.getComponents()) {
//                deactivateComponent(compPane);
//            }
//        } else {
//            comp.setEnabled(false);
//        }
//    }
    private void checkPermesso() throws Exception {
        if (this.tipo_permesso != null) {
            if (!main.utente.getPermesso(tipo_permesso, Permesso.PERMESSO_TIPO_CANCELLA)) {
                if (butDele != null) {
                    butDele.setEnabled(false);
                }
            }
            if (!main.utente.getPermesso(tipo_permesso, Permesso.PERMESSO_TIPO_SCRITTURA)) {
                if (butNew != null) {
                    butNew.setEnabled(false);
                }
                if (butDuplica != null) {
                    butDuplica.setEnabled(false);
                }
                if (butSave != null) {
                    butSave.setEnabled(false);
                }
                if (butSaveClose != null) {
                    butSaveClose.setEnabled(false);
                }
                if (butUndo != null) {
                    butUndo.setEnabled(false);
                }
                if (butFind != null) {
                    butFind.setEnabled(false);
                }

                for (Component comp : getComponents()) {
                    InvoicexUtil.deactivateComponent(comp);
                }
            }
        }
    }

    public void sincronizzaSelezioneGrigliaJTableDb(JTableDb griglia) {
        //provo a trovarlo nella griglia
        this.grigliaJTableDb = griglia;
        if (griglia != null) {
            HashMap key_current_panel = getKeyFromPanel();
            HashMap key_current_grid = getKeyFromGridJTableDb();
            if (key_current_grid.equals(key_current_panel)) {
                if (debug) {
                    System.out.println("*** key uguali");
                }
            } else {
                if (debug) {
                    System.out.println("*** key diverse");
                }
                if (saltaSincronizzaGriglia) {
                    if (debug) {
                        System.out.println("salto sincronizza");
                    }
                    return;
                }
                //lo trovo in grid
                boolean found = false;
                for (int r = 0; r <= griglia.getRowCount(); r++) {
                    HashMap key = getKeyFromGridJTableDb(r);
                    if (key.equals(key_current_panel)) {
                        if (debug) {
                            System.out.println("*** imposto row:" + r);
                        }
                        griglia.getSelectionModel().setSelectionInterval(r, r);
                        if (!griglia.getVisibleRect().contains(griglia.getCellRect(griglia.getSelectedRow(), 1, true))) {
                            griglia.scrollToRow(griglia.getSelectedRow());
                        }
                        found = true;
                        break;
                    }
                }
                if (debug) {
                    System.out.println("*** key trovata:" + found);
                }
            }

        }
    }

    private synchronized void _fireDbEvent(int status) {
        DbEvent db = new DbEvent(this, status);
        Iterator listeners = _listeners.iterator();
        while (listeners.hasNext()) {
            ((DbListener) listeners.next()).statusFired(db);
        }
    }

    private HashMap getKeyFromGrid() {
        return getKeyFromGrid(griglia.getSelectedRow());
    }

    private HashMap getKeyFromGrid(int row) {
        HashMap key = new HashMap();
        for (int i = 0; i < dbChiave.size(); i++) {
            String campok = (String) dbChiave.get(i);
            key.put(campok, getKeyValueFromGrid(campok, row));
        }
        return key;
    }

    private HashMap getKeyFromGridJTableDb() {
        return getKeyFromGridJTableDb(grigliaJTableDb.getSelectedRow());
    }

    private HashMap getKeyFromGridJTableDb(int row) {
        HashMap key = new HashMap();
        for (int i = 0; i < dbChiave.size(); i++) {
            String campok = (String) dbChiave.get(i);
            key.put(campok, getKeyValueFromGridJTableDb(campok, row));
        }
        return key;
    }

    private HashMap getKeyFromPanel() {
        HashMap key = new HashMap();
        for (int i = 0; i < getComponentCount2(); i++) {
            Component comp = getComponent2(i);
            try {
//                Object nomeCampo = comp.getClass().getMethod("getDbNomeCampo", null).invoke(comp, null);
                Object nomeCampo = comp.getClass().getMethod("getDbNomeCampo").invoke(comp);
                if (dbChiave.contains(nomeCampo)) {
//                    Object valore = comp.getClass().getMethod("getText", null).invoke(comp, null);
                    Object valore = comp.getClass().getMethod("getText").invoke(comp);
                    key.put(nomeCampo, String.valueOf(valore));
                }
            } catch (Exception ex) {
            }
        }
        return key;
    }

    private Object getKeyValueFromGrid(String campok, int row) {
        return String.valueOf(griglia.getValueAt(row, griglia.getColumnByName(campok)));
    }

    private Object getKeyValueFromGridJTableDb(String campok, int row) {
        return String.valueOf(grigliaJTableDb.getValueAt(row, grigliaJTableDb.getColumnByName(campok)));
    }

    //end events
    private void setStato(String stato) {
        /*
        System.out.println("dbPanel:dbStato:precednete=" + this.dbStato + ":ora=" + stato);
        if (this.dbStato.equals("L") && stato.equals("M")) {
        System.out.println("debug");
        try {
        int i = Integer.parseInt("aaa");
        } catch (Exception err) {
        err.printStackTrace();
        }
        }
         */
        if (debug && !dbStato.equals(stato)) {
            System.out.println("cambio stato da: " + dbStato + " a:" + stato);
        }
        this.dbStato = stato;
    }



    public boolean dbOpen(Connection connection, String sql, DbI db) {
        this.db = db;
        return dbOpen(connection, sql);
    }

    public boolean dbOpen(Connection connection, String sql) {
        //assegno i permessi
        if (main.utente != null && tipo_permesso != null) {
            if (main.utente.getPermesso(tipo_permesso, Permesso.PERMESSO_TIPO_LETTURA)) {
                permesso_lettura = true;
            }
            if (main.utente.getPermesso(tipo_permesso, Permesso.PERMESSO_TIPO_SCRITTURA)) {
                permesso_scrittura = true;
            }
            if (main.utente.getPermesso(tipo_permesso, Permesso.PERMESSO_TIPO_CANCELLA)) {
                permesso_cancella = true;
            }
        } else {
            permesso_cancella = true;
            permesso_lettura = true;
            permesso_scrittura = true;
        }
        
        //debug
        //javax.swing.JOptionPane.showMessageDialog(null,this.dbChiave.get(1));
        this.oldSql = sql;
        this.oldSqldbopen = sql;
        this.oldConn = connection;

        //controlli campi in lingua da creare in automatico
        try {
            for (int i = 0; i < this.getComponentCount2(); i++) {
                Component cfield = getComponent2(i);
                if (cfield.getClass().getName().equals("tnxbeans.tnxTextFieldLang")
                        || cfield.getClass().getName().equals("tnxbeans.tnxMemoFieldLang")) {
                    BasicField basic = (BasicField) cfield;
                    String nome_campo = basic.getDbNomeCampo();
                    for (String l : getLang()) {
                        if (l.equalsIgnoreCase("it")) {
                            continue;
                        }
                        String nome_campo_lang = nome_campo + "_" + l;
                        DbUtils.duplicateColumn(connection, dbNomeTabella, nome_campo, nome_campo_lang);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //apre il resultset da abbinare
        try {
            if (db != null) {
                stat = db.getDbConn().createStatement();
            } else {
                stat = connection.createStatement();
            }
            resu = stat.executeQuery(sql);
            meta = new ResultSetMetaDataCached(resu.getMetaData());
            if (resu.next() == true) {
                this.noRecords = false;
                isOnSomeRecord = true;
                if (this.butDele != null) {
                    butDele.setEnabled(true);
                }
                if (butNew != null) {
                    butNew.setEnabled(true);
                }
                dbRefresh(false);
                caricaValoriChiave();
            } else {
                this.noRecords = true;
                isOnSomeRecord = false;
                if (this.butDele != null) {
                    butDele.setEnabled(false);
                }
                if (permesso_scrittura) {
                    this.dbNew();
                    if (!messaggio_nuovo_manuale) {
                        InvoicexUtil.msgNew(SwingUtils.getParentJInternalFrame(this), this, null, null, "Nessun record presente, prosegui con l'inserimento");
                    }
                }                
            }
            return (true);
        } catch (Exception err) {
            err.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this, err.toString());
            return (false);
        } finally {
            try {
                stat.close();
            } catch (Exception e) {
            }
            try {
                resu.close();
            } catch (Exception e) {
            }
        }
    }
    
    /* DAVID */
    
    

    public boolean dbOpenD2(Connection connection, String sql) {
        //assegno i permessi
        if (main.utente != null && tipo_permesso != null) {
            if (main.utente.getPermesso(tipo_permesso, Permesso.PERMESSO_TIPO_LETTURA)) {
                permesso_lettura = true;
            }
            if (main.utente.getPermesso(tipo_permesso, Permesso.PERMESSO_TIPO_SCRITTURA)) {
                permesso_scrittura = true;
            }
            if (main.utente.getPermesso(tipo_permesso, Permesso.PERMESSO_TIPO_CANCELLA)) {
                permesso_cancella = true;
            }
        } else {
            permesso_cancella = true;
            permesso_lettura = true;
            permesso_scrittura = true;
        }
        
        //debug
        //javax.swing.JOptionPane.showMessageDialog(null,this.dbChiave.get(1));
        this.oldSql = sql;
        this.oldSqldbopen = sql;
        this.oldConn = connection;

        //controlli campi in lingua da creare in automatico
        try {
            for (int i = 0; i < this.getComponentCount2(); i++) {
                Component cfield = getComponent2(i);
                if (cfield.getClass().getName().equals("tnxbeans.tnxTextFieldLang")
                        || cfield.getClass().getName().equals("tnxbeans.tnxMemoFieldLang")) {
                    BasicField basic = (BasicField) cfield;
                    String nome_campo = basic.getDbNomeCampo();
                    for (String l : getLang()) {
                        if (l.equalsIgnoreCase("it")) {
                            continue;
                        }
                        String nome_campo_lang = nome_campo + "_" + l;
                        DbUtils.duplicateColumn(connection, dbNomeTabella, nome_campo, nome_campo_lang);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //apre il resultset da abbinare
        try {
            if (db != null) {
                stat = db.getDbConn().createStatement();
            } else {
                stat = connection.createStatement();
            }
            resu = stat.executeQuery(sql);
            meta = new ResultSetMetaDataCached(resu.getMetaData());
            if (resu.next() == true) {
                this.noRecords = false;
                isOnSomeRecord = true;
                if (this.butDele != null) {
                    butDele.setEnabled(true);
                }
                if (butNew != null) {
                    butNew.setEnabled(true);
                }
                dbRefresh(false);
                caricaValoriChiave();
            }
            return (true);
        } catch (Exception err) {
            err.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this, err.toString());
            return (false);
        } finally {
            try {
                stat.close();
            } catch (Exception e) {
            }
            try {
                resu.close();
            } catch (Exception e) {
            }
        }
    }
    
    /* DAVID */

    public boolean dbSelectSingle(Vector valoriChiave) {
        //debug
        //javax.swing.JOptionPane.showMessageDialog(null,this.dbChiave.get(0));

        Statement stat = null;

        //apre il resultset da abbinare
        try {
            String sql = "select * from " + dbNomeTabella + " where ";
            for (int i = 0; i < valoriChiave.size(); i++) {
                //cerco id del camp chiave
                boolean trovato = false;
                int idCampo = 0;
                for (int j = 0; j < meta.getColumnCount(); j++) {
                    //debug
                    //javax.swing.JOptionPane.showMessageDialog(null,"meta:="+meta.getColumnName(j+1)+" chiave:"+this.dbChiave.get(i));
                    if (meta.getColumnName(j + 1).equalsIgnoreCase((String) this.dbChiave.get(i))) {
                        trovato = true;
                        idCampo = j + 1;
                        j = meta.getColumnCount();
                    }
                }
                if (trovato == true) {
                    sql += this.dbChiave.get(i) + " = ";
                    sql += pc(valoriChiave.get(i).toString(), meta.getColumnTypeName(idCampo));
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this, "errore, non trovato id campo chiave");
                }
                if (i != valoriChiave.size() - 1) {
                    sql += " and ";
                }
            }
            //debug
            //javax.swing.JOptionPane.showMessageDialog(null,sql);
            if (db != null) {
                stat = db.getDbConn().createStatement();
            } else {
                stat = oldConn.createStatement();
            }
            resu = stat.executeQuery(sql);
            oldSql = sql;
            if (resu.next() == true) {
                isOnSomeRecord = true;
                if (this.butDele != null) {
                    butDele.setEnabled(true);
                }
                if (butNew != null) {
                    butNew.setEnabled(true);
                }
            } else {
            }

            caricaValoriChiave();

            if (this.butSave != null) {
                butSave.setEnabled(false);
            }
            if (this.butSaveClose != null) {
                butSaveClose.setEnabled(false);
            }
            if (this.butUndo != null) {
                butUndo.setEnabled(false);
            }
            //if (this.butFind!=null) butFind.setEnabled(false);
            return (true);
        } catch (Exception err) {
            err.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this, err.toString());
            return (false);
        } finally {
            try {
                stat.close();
            } catch (Exception e) {
            }
            try {
                resu.close();
            } catch (Exception e) {
            }
        }
    }

    public boolean dbSelectSingle(Hashtable valoriChiave) {
        String sql = "select * from " + dbNomeTabella + " where ";

        Statement stat = null;

        //apre il resultset da abbinare
        try {
            //debug
      /*javax.swing.JOptionPane.showMessageDialog(null,"valoriChiave.size="+String.valueOf(valoriChiave.size()));
            javax.swing.JOptionPane.showMessageDialog(null,"valoriChiave[0]="+String.valueOf(valoriChiave.get(0)));
            javax.swing.JOptionPane.showMessageDialog(null,"meta.getColumnCount="+String.valueOf(meta.getColumnCount()));
            javax.swing.JOptionPane.showMessageDialog(null,"meta.getColumnName(0)="+String.valueOf(meta.getColumnName(0)));*/
            for (int i = 0; i < valoriChiave.size(); i++) {
                //cerco id del camp chiave
                boolean trovato = false;
                int idCampo = 0;
                for (int j = 0; j < meta.getColumnCount(); j++) {
                    //debug
                    //javax.swing.JOptionPane.showMessageDialog(null,"meta:="+meta.getColumnName(j+1)+" chiave:"+this.dbChiave.get(i));
                    if (meta.getColumnName(j + 1).equalsIgnoreCase((String) this.dbChiave.get(i))) {
                        trovato = true;
                        idCampo = j + 1;
                        j = meta.getColumnCount();
                    }
                }
                if (trovato == true) {
                    sql += this.dbChiave.get(i) + " = ";
                    if (valoriChiave.get(dbChiave.get(i)) instanceof Integer) {
                        sql += String.valueOf(valoriChiave.get(dbChiave.get(i)));
                    } else {
                        sql += pc((String) valoriChiave.get(dbChiave.get(i)), meta.getColumnTypeName(idCampo));
                    }
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this, "errore, non trovato id campo chiave");
                }
                if (i != valoriChiave.size() - 1) {
                    sql += " and ";
                }
            }
            if (sql.endsWith("where ")) {
                sql = "select * from " + dbNomeTabella;
                if (debug) {
                    System.out.println("dbSelectSingle no where");
                }
            }
            if (db != null) {
                stat = db.getDbConn().createStatement();
            } else {
                stat = oldConn.createStatement();
            }
            resu = stat.executeQuery(sql);
            oldSql = sql;

            if (debug) {
                DebugUtils.dump(valoriChiave);
                System.err.println("sql select single:" + sql);
            }

            if (resu.next() == true) {
                isOnSomeRecord = true;
                if (this.butDele != null) {
                    butDele.setEnabled(true);
                }
                if (butNew != null) {
                    butNew.setEnabled(true);
                }
            } else {
                //debug
                if (showErrorSelectSingle == true) {
                    javax.swing.JOptionPane.showMessageDialog(this, "select single sql: nessun record");
                } else {
                    if (debug) {
                        System.out.println("select single sql: nessun record (showErrorSelectSingle = false)");
                    }
                }
            }

            caricaValoriChiave();

            if (this.butSave != null) {
                butSave.setEnabled(false);
            }
            if (this.butSaveClose != null) {
                butSaveClose.setEnabled(false);
            }
            if (this.butUndo != null) {
                butUndo.setEnabled(false);
            }
            //if (this.butFind!=null) butFind.setEnabled(false);
            return (true);
        } catch (Exception err) {
            err.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this, err.toString() + "\n" + "sql:" + sql);
            return (false);
        } finally {
            try {
                stat.close();
            } catch (Exception e) {
            }
            try {
                resu.close();
            } catch (Exception e) {
            }
        }
    }

    public void dbNew() {
        //29/06/11 se non va in inserimento da errori quando si salva perch� pensa che sia in modifica (vedi destinazioni diverse clienti)
//        if (this.noRecords == false) {
        //dbStato = this.DB_INSERIMENTO;
        setStato(this.DB_INSERIMENTO);
        isOnSomeRecord = false;
        if (this.butDele != null) {
            butDele.setEnabled(false);
        }
        if (butNew != null) {
            butNew.setEnabled(false);
        }
//        }

        //se click su nuovo vado su pannello dati
        //va a ricercare a ritroso se ??? dentro un tabbed pane, se c'??? mette l'index = 0 che di solito c'??? il panel coi dati
        if (!nonSpostareSuTab0) {
            Component tempParent;
            tempParent = this.getParent();
            if (!tempParent.getClass().getName().equalsIgnoreCase("JTabbedPane")) {
                for (int i = 0; i < 5; i++) {
                    tempParent = tempParent.getParent();
                    if (tempParent == null) {
                        i = 10;
                    } else if (tempParent.getClass().getName().equalsIgnoreCase("javax.swing.JTabbedPane")) {
                        i = 10;
                    }
                }
            }
            if (tempParent != null) {
                if (tempParent.getClass().getName().equalsIgnoreCase("javax.swing.JTabbedPane")) {
                    javax.swing.JTabbedPane tempTab = (javax.swing.JTabbedPane) tempParent;
                    tempTab.setSelectedIndex(0);
                }
            }
        }

        //debug
        //javax.swing.JOptionPane.showMessageDialog(null,"inserimento");

        //azzera tutti i campi collegati
        Component tempField = null;
        for (int i = 0; i < this.getComponentCount2(); i++) {
            tempField = this.getComponent2(i);
            //debug
            //javax.swing.JOptionPane.showMessageDialog(null,tempField.getClass().getName());
            if (tempField.getClass().getName().equals("tnxbeans.tnxTextField")) {
                tnxTextField tempTnxTextField = (tnxTextField) tempField;

                //if (tempTnxTextField.getDbRiempire()==true && tempTnxTextField.dbComboAbbinata == null) {
                if (tempTnxTextField.getDbRiempire() == true) {
                    try {
                        if (nz(tempTnxTextField.dbTipoCampo, "").equalsIgnoreCase("valuta")) {
                            tempTnxTextField.setText("0,00");
                        } else if (nz(tempTnxTextField.dbTipoCampo, "").equalsIgnoreCase("numerico")) {
                            if (tempTnxTextField.getDbDefault().equalsIgnoreCase("vuoto")) {
                                tempTnxTextField.setText("");
                            } else {
                                tempTnxTextField.setText("0");
                            }
                        } else if (nz(tempTnxTextField.dbTipoCampo, "").equalsIgnoreCase("data")) {
                            if (tempTnxTextField.getDbDefault().equals(tempTnxTextField.DEFAULT_CURRENT)) {
                                DateFormat myFormat = new SimpleDateFormat("dd/MM/yyyy");
                                Calendar myCalendar = GregorianCalendar.getInstance();
                                tempTnxTextField.setText(myFormat.format(myCalendar.getTime()));
                            } else {
                                tempTnxTextField.setText("");
                            }
                        } else {
                            tempTnxTextField.setText("");
                        }
                    } catch (Exception err) {
                        err.printStackTrace();
                        javax.swing.JOptionPane.showMessageDialog(null, "dbNew:" + err.toString());
                    }

                    if (tempTnxTextField.getDbComboAbbinata() != null) {
                        //tempTnxTextField.setText(tempTnxTextField.getDbComboAbbinata().getSelectedKey().toString());
                        //tempTnxTextField.getDbComboAbbinata().setText("");
                        try {
                            tempTnxTextField.getDbComboAbbinata().setSelectedIndex(-1);
                        } catch (Exception err) {
                        }
                    }
                }
            }
            if (tempField.getClass().getName().equals("tnxbeans.tnxTextFieldFormatted")) {
                tnxTextFieldFormatted tempTnxTextField = (tnxTextFieldFormatted) tempField;

                //if (tempTnxTextField.getDbRiempire()==true && tempTnxTextField.dbComboAbbinata == null) {
                if (tempTnxTextField.getDbRiempire() == true) {
                    try {
                        if (nz(tempTnxTextField.dbTipoCampo, "").equalsIgnoreCase("valuta")) {
                            tempTnxTextField.setText("0,00");
                        } else if (nz(tempTnxTextField.dbTipoCampo, "").equalsIgnoreCase("numerico")) {
                            if (tempTnxTextField.getDbDefault().equalsIgnoreCase("vuoto")) {
                                tempTnxTextField.setText("");
                            } else {
                                tempTnxTextField.setText("0");
                            }
                        } else if (nz(tempTnxTextField.dbTipoCampo, "").equalsIgnoreCase("data")) {
                            if (tempTnxTextField.getDbDefault().equals(tempTnxTextField.DEFAULT_CURRENT)) {
                                DateFormat myFormat = new SimpleDateFormat("dd/MM/yyyy");
                                Calendar myCalendar = GregorianCalendar.getInstance();
                                tempTnxTextField.setText(myFormat.format(myCalendar.getTime()));
                            } else {
                                tempTnxTextField.setText("");
                            }
                        } else {
                            tempTnxTextField.setText("");
                        }
                    } catch (Exception err) {
                        err.printStackTrace();
                        javax.swing.JOptionPane.showMessageDialog(null, "dbNew:" + err.toString());
                    }
                }
            }
            if (tempField.getClass().getName().equals("tnxbeans.tnxComboField")) {
                tnxComboField tempTnxComboField = (tnxComboField) tempField;
                if (tempTnxComboField.getDbRiempire() == true) {
                    try {
                        tempTnxComboField.setSelectedIndex(-1);
                        tempTnxComboField.setText("");
                    } catch (Exception err) {
                        javax.swing.JOptionPane.showMessageDialog(null, "dbNew:" + err.toString());
                    }
                }
            }
            if (tempField.getClass().getName().equals("tnxbeans.tnxMemoField")) {
                tnxMemoField tempTnxMemoField = (tnxMemoField) tempField;
                if (tempTnxMemoField.getDbRiempire() == true) {
                    try {
                        tempTnxMemoField.setText("");
                    } catch (Exception err) {
                        javax.swing.JOptionPane.showMessageDialog(null, "dbNew:" + err.toString());
                    }
                }
            }
            if (tempField.getClass().getName().equals("tnxbeans.tnxMemoFieldLang")) {
                tnxMemoFieldLang tempTnxMemoField = (tnxMemoFieldLang) tempField;
                if (tempTnxMemoField.isDbRiempire() == true) {
                    try {
                        tempTnxMemoField.setText("");
                    } catch (Exception err) {
                        javax.swing.JOptionPane.showMessageDialog(null, "dbNew:" + err.toString());
                    }
                }
            }
            if (tempField.getClass().getName().equals("tnxbeans.tnxTextFieldLang")) {
                tnxTextFieldLang tempTnxMemoField = (tnxTextFieldLang) tempField;
                if (tempTnxMemoField.isDbRiempire() == true) {
                    try {
                        tempTnxMemoField.setText("");
                    } catch (Exception err) {
                        javax.swing.JOptionPane.showMessageDialog(null, "dbNew:" + err.toString());
                    }
                }
            }
            if (tempField.getClass().getName().equals("tnxbeans.tnxCheckBox")) {
                tnxCheckBox tempTnxCheckBox = (tnxCheckBox) tempField;
                if (tempTnxCheckBox.getDbRiempire() == true) {
                    try {
                        tempTnxCheckBox.setSelected(false);
                    } catch (Exception err) {
                        javax.swing.JOptionPane.showMessageDialog(null, "dbNew:" + err.toString());
                    }
                }
            }
        }
//        if (this.noRecords == false) {
        //dbStato = this.DB_INSERIMENTO;
        setStato(this.DB_INSERIMENTO);
        if (this.butSave != null) {
            butSave.setEnabled(true);
        }
        if (this.butSaveClose != null) {
            butSaveClose.setEnabled(true);
        }

        if (this.butUndo != null) {
            butUndo.setEnabled(true);
        }
        if (this.butFind != null) {
            butFind.setEnabled(true);
        }
//        } else {
//            //lo rimetto a false almeno le prossime volte ??? ok
//            this.noRecords = false;
//            if (this.butSave != null) {
//                butSave.setEnabled(false);
//            }
//            if (this.butUndo != null) {
//                butUndo.setEnabled(false);
//            }
//            if (this.butFind != null) {
//                butFind.setEnabled(false);
//            }
//        }
        this._fireDbEvent(this.STATUS_ADDING);
    }

    public boolean dbDelete() {
        //elimina il record e si posiziona sul primo
        if (isOnSomeRecord == true) {
            //se dbEditabile = false non faccio salvare
            if (dbEditabile == false) {
                javax.swing.JOptionPane.showMessageDialog(this, "I dati non possono essere modificati", "Attenzione", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                return false;
            }

            String sql = "";
            Vector valoriChiave = new Vector();

            try {
                /*
                for (int i = 1 ; i <= meta.getColumnCount() ; i++) {
                String tipoCampo=meta.getColumnTypeName(i);
                //sql += pc(fieldText,tipoCampo);
                //salvo i valori se chiave
                for (int h=0 ; h < dbChiave.size() ; h++) {
                if (meta.getColumnName(i).equalsIgnoreCase((String)this.dbChiave.get(h))) {
                //??? una chiave e memorizzo il valore
                valoriChiave.add(h,pc(resu.getString(i),tipoCampo));
                }
                }
                }
                sql = "delete from " + this.dbNomeTabella + " where ";
                for (int l = 0 ; l < dbChiave.size() ; l++) {
                if (l==0) {
                sql += (String)dbChiave.get(l) + " = " + (String)valoriChiave.get(l);
                } else {
                sql += " and " + (String)dbChiave.get(l) + " = " + (String)valoriChiave.get(l);
                }
                }
                 */

                checkResu();
                //nuovo modo tramite dbChiaveValori che viene memorizzato ogni volta si cambia il record
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    String tipoCampo = meta.getColumnTypeName(i);
                    //sql += pc(fieldText,tipoCampo);
                    //salvo i valori se chiave
                    if (dbChiave.contains(meta.getColumnName(i))) {
                        valoriChiave.add(pc(resu.getString(i), tipoCampo));
                    }
                }
                //debug
                //System.out.println(valoriChiave);

                sql = "delete from " + this.dbNomeTabella + " where ";
                for (int l = 0; l < dbChiave.size(); l++) {
                    if (l == 0) {
                        sql += (String) dbChiave.get(l) + " = " + (String) valoriChiave.get(l);
                    } else {
                        sql += " and " + (String) dbChiave.get(l) + " = " + (String) valoriChiave.get(l);
                    }
                }

            } catch (Exception err) {
                //debug
                err.printStackTrace();
                javax.swing.JOptionPane.showMessageDialog(null, spezza(err.toString(), 40));
            } finally {
                try {
                    stat.close();
                } catch (Exception e) {
                }
                try {
                    resu.close();
                } catch (Exception e) {
                }
            }
            //eseguo la query
            Statement statTmp = null;
            try {
                if (db != null) {
                    statTmp = db.getDbConn().createStatement();
                } else {
                    statTmp = oldConn.createStatement();
                }

                //debug
                //javax.swing.JOptionPane.showMessageDialog(null,spezza(sql,40));
                statTmp.execute(sql);
                if (this.butSave != null) {
                    butSave.setEnabled(false);
                }
                if (this.butSaveClose != null) {
                    butSaveClose.setEnabled(false);
                }
                if (this.butUndo != null) {
                    butUndo.setEnabled(false);
                }
                if (this.butFind != null) {
                    butFind.setEnabled(false);
                }
                isOnSomeRecord = false;
                if (this.butDele != null) {
                    butDele.setEnabled(false);
                }
                if (butNew != null) {
                    butNew.setEnabled(true);
                }

                //riseleziono il primo record della griglia
                //this.dbRefresh();
                //---
                dbOpen(this.oldConn, this.oldSqldbopen);
                //
            } catch (Exception err) {
                err.printStackTrace();
                javax.swing.JOptionPane.showMessageDialog(null, err.toString());
            } finally {
                try {
                    statTmp.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return (true);
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "Non e' selezionato alcun record, non si puo' eliminare");
            return (false);
        }
    }

    public void dbDeletePost() {
        if (this.butSave != null) {
            butSave.setEnabled(false);
        }
        if (this.butSaveClose != null) {
            butSaveClose.setEnabled(false);
        }
        if (this.butUndo != null) {
            butUndo.setEnabled(false);
        }
        if (this.butFind != null) {
            butFind.setEnabled(false);
        }
        isOnSomeRecord = false;
        if (this.butDele != null) {
            butDele.setEnabled(false);
        }
        if (butNew != null) {
            butNew.setEnabled(true);
        }
        
        dbOpen(this.oldConn, this.oldSqldbopen);
    }

    public boolean dbRefresh() {
        return dbRefresh(true);
    }

    public boolean dbRefresh(boolean chiudi) {
        if (this.isRefreshing == true) {
            if (debug) {
                System.out.println("!!! double refreshing !!!");
            }
            return false;
        }

        this.isRefreshing = true;

        //porta i valori dal resultset ai campi
        //dbStato = this.DB_LETTURA;
        if (!dbStato.equals(DB_INSERIMENTO)) {
            setStato(this.DB_LETTURA);
        }

        //controllo resu
        boolean closed = false;
        if (resu != null) {
            try {
                resu.getRow();
            } catch (SQLException sqle) {
                closed = true;
            }
        } else {
            closed = true;
        }
        try {
            if (closed) {
//                System.out.println("resu closed");
                if (db != null) {
                    stat = db.getDbConn().createStatement();
                } else {
                    stat = oldConn.createStatement();
                }
                resu = stat.executeQuery(oldSql);
                resu.next();
            }

            //ciclo i campi
            Component tempField = null;
            //System.out.println("---+---:"+this.getComponentCount());
            for (int i = 0; i < this.getComponentCount2(); i++) {
                boolean nullSeVuoto = false;
                if (i == 12) {
                    //System.out.println("***");
                }

                tempField = this.getComponent2(i);
                
                if (debug) {
                    try {
//                        String ret = (String) tempField.getClass().getMethod("getDbNomeCampo", null).invoke(tempField, null);
                        String ret = (String) tempField.getClass().getMethod("getDbNomeCampo").invoke(tempField);
                        if (debug) {
                            System.out.println("refresh nomeCampo:" + ret + " valore:" + resu.getObject(ret) + " class:" + tempField.getClass());
                        }
                        if (ret.equalsIgnoreCase("provvigione_predefinita_cliente")) {
                            if (debug) {
                                System.out.println("stop");
                            }
                        }
                    } catch (java.lang.Exception ex0) {
                    }
                }

                //debug
                //javax.swing.JOptionPane.showMessageDialog(null,tempField.getClass().getName());
                //TEXTFIELD
                if (tempField.getClass().getName().equals("tnxbeans.tnxTextField") || tempField.getClass().getSuperclass().getName().equals("tnxbeans.tnxTextField")) {
                    tnxTextField tempTnxTextField = (tnxTextField) tempField;
                    nullSeVuoto = tempTnxTextField.isDbNullSeVuoto();

                    //test su limit text size
                    if (!init_limits) {
                        try {
                            if (tempTnxTextField.dbNomeCampo != null && tempTnxTextField.dbNomeCampo.length() > 0) {
                                int col = DbUtilsTnxBeans.getColumnIndex(meta, tempTnxTextField.dbNomeCampo);
                                int size = meta.getColumnDisplaySize(col);
                                if (tempTnxTextField.dbNomeCampo.equals("fornitore")) {
                                    if (debug) {
                                        System.out.println("stop");
                                    }
                                }
                                tempTnxTextField.setmaxChars(size);
                            }
                        } catch (SQLException sqlex) {
                            if (tempTnxTextField.dbSalvare || tempTnxTextField.dbRiempire) {
                                if (debug) {
                                    System.out.println(sqlex);
                                }
                            }
                        }
                    }

                    if (tempTnxTextField.getDbRiempire() == true) {
                        //debug

                        //NOTA DI LORENZO: anche questo andrebbe messo dentro al try, quindi ti ce l'ho messo sotto, alla riga 544
                        //                 scusa se non ti ho aspettato, ma avevo bisogno di fare un paio di prove
                        //System.out.println(i + ":" + tempTnxTextField.getDbNomeCampo() + ":" + tempTnxTextField.getName() + ":" + tempField.getClass().getName() + "->:" + resu.getString(tempTnxTextField.getDbNomeCampo()));

                        try {

                            //debug

                            //NOTA DI LORENZO: eccolo qui
                            //System.out.println(i + ":" + tempTnxTextField.getDbNomeCampo() + ":" + tempTnxTextField.getName() + ":" + tempField.getClass().getName() + "->:" + resu.getString(tempTnxTextField.getDbNomeCampo()));

                            //javax.swing.JOptionPane.showMessageDialog(null,"resu:" + resu + " resu.getRow():" + String.valueOf(resu.getRow()));
                            if (resu != null) {
                                if (resu.getRow() == 0) {
                                    if (tempTnxTextField.dbTipoCampo != null && (tempTnxTextField.dbTipoCampo.equalsIgnoreCase("numerico") || tempTnxTextField.dbTipoCampo.equalsIgnoreCase("valuta"))) {
                                        if (tempTnxTextField.getDbDefault().equalsIgnoreCase("vuoto") || nullSeVuoto) {
                                            tempTnxTextField.setText("");
                                        } else {
                                            tempTnxTextField.setText("0");
                                        }
                                    } else {
                                        tempTnxTextField.setText("");
                                    }
                                } else {
                                    if (resu.getObject(tempTnxTextField.getDbNomeCampo()) == null && (tempTnxTextField.getDbDefault().equalsIgnoreCase("vuoto") || nullSeVuoto)) {
                                        tempTnxTextField.setText("");
                                    } else {
                                        if (nullSeVuoto && resu.getObject(tempTnxTextField.getDbNomeCampo()) == null) {
                                            tempTnxTextField.setText("");
                                        } else {
                                            if (nz(tempTnxTextField.dbTipoCampo, "").equalsIgnoreCase("valuta")) {
                                                DecimalFormat form = new DecimalFormat("#,##0.00###");
                                                if (tempTnxTextField.getDbDecimaliMax() != null) {
                                                    form.setMaximumFractionDigits(tempTnxTextField.getDbDecimaliMax());
                                                }
                                                if (tempTnxTextField.getDbDecimaliMin() != null) {
                                                    form.setMinimumFractionDigits(tempTnxTextField.getDbDecimaliMin());
                                                }                                                                                        
                                                tempTnxTextField.setText(form.format(resu.getDouble(tempTnxTextField.getDbNomeCampo())));
                                            } else if (nz(tempTnxTextField.dbTipoCampo, "").equalsIgnoreCase("numerico")) {
                                                NumberFormat form = NumberFormat.getInstance();
                                                if (tempTnxTextField.getDbDecimaliMax() != null) {
                                                    form.setMaximumFractionDigits(tempTnxTextField.getDbDecimaliMax());
                                                }
                                                if (tempTnxTextField.getDbDecimaliMin() != null) {
                                                    form.setMinimumFractionDigits(tempTnxTextField.getDbDecimaliMin());
                                                }                                            
                                                tempTnxTextField.setText(form.format(resu.getDouble(tempTnxTextField.getDbNomeCampo())));
                                            } else if (nz(tempTnxTextField.dbTipoCampo, "").equalsIgnoreCase("data")) {
                                                String tempData = resu.getString(tempTnxTextField.getDbNomeCampo());
                                                boolean ok = false;
                                                if (tempData != null) {
                                                    if (!tempData.equalsIgnoreCase("0000-00-00")) {
                                                        ok = true;
                                                    }
                                                }
                                                if (ok == true) {
                                                    String anno = tempData.substring(2, 4);
                                                    String mese = tempData.substring(5, 7);
                                                    String giorno = tempData.substring(8, 10);
                                                    tempTnxTextField.setText(giorno + "/" + mese + "/" + anno);
                                                } else {
                                                    tempTnxTextField.setText("");
                                                }
                                            } else {
                                                if (tempTnxTextField.maxChars > 0) {
                                                    if (resu.getString(tempTnxTextField.getDbNomeCampo()) != null) {
                                                        if (resu.getString(tempTnxTextField.getDbNomeCampo()).length() > tempTnxTextField.maxChars) {
                                                            tempTnxTextField.setText(resu.getString(tempTnxTextField.getDbNomeCampo()).substring(0, tempTnxTextField.maxChars));
                                                        } else {
                                                            tempTnxTextField.setText(resu.getString(tempTnxTextField.getDbNomeCampo()));
                                                        }
                                                    } else {
                                                        tempTnxTextField.setText("");
                                                    }
                                                } else {
                                                    tempTnxTextField.setText(resu.getString(tempTnxTextField.getDbNomeCampo()));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            //se c'??? riempo combo associata
                            if (tempTnxTextField.getDbComboAbbinata() != null) {
                                tempTnxTextField.getDbComboAbbinata().dbTrovaKey(tempTnxTextField.getText());
                                //System.out.println("dbRefresh:0:comboAssociata:found=" + tempTnxTextField.getDbComboAbbinata().isFound);
                            }
                            //se c'??? riempo combo secondria
                            if (tempTnxTextField.getDbComboSecondaria() != null) {
                                tnxComboField tempTnxComboField = tempTnxTextField.getDbComboSecondaria();
                                //System.out.println("dbRefresh:2:comboSecondaria:" + tempTnxTextField.getDbComboSecondaria().getDbNomeCampo());
                                //if (tempTnxComboField.getDbRiempire()==true) {
                                try {
                                    //tempTnxComboField.setText(resu.getString(tempTnxComboField.getDbNomeCampo()));
                                    //controllo se c?? da richiamare qlcs
                                    if (riempiCampiSecondari != null) {
                                        riempiCampiSecondari.riempiComboPrimaDiRefresh();
                                        //System.out.println("dbRefresh:1:riempiComboPrimaDiRefresh");
                                    }

                                    //debug
                                    //javax.swing.JOptionPane.showMessageDialog(null,"debug combo");

                                    //System.out.println("dbRefresh:3:trova chiave:" + nz(resu.getString(tempTnxComboField.getDbNomeCampo()), ""));

                                    if (resu.getRow() == 0) {
                                        tempTnxComboField.setSelectedIndex(-1);
                                    } else if (tempTnxComboField.dbRiempireForceText == true) {
                                        //prima trova se c'e' la voce memorizzata
                                        tempTnxComboField.dbTrovaKey(nz(resu.getString(tempTnxComboField.getDbNomeCampo()), ""));
                                        tempTnxComboField.setText(resu.getString(tempTnxComboField.getDbNomeCampo()));
                                    } else {
                                        //tempTnxComboField.setSelectedItem((Object)resu.getString(tempTnxComboField.getDbNomeCampo()));
                                        //tempTnxComboField.dbTrovaRiga(resu.getString(tempTnxComboField.getDbNomeCampo()));
                                        tempTnxComboField.dbTrovaKey(nz(resu.getString(tempTnxComboField.getDbNomeCampo()), ""));
                                        //System.out.println("dbRefresh:4:found=" + tempTnxComboField.isFound);
                                        if (debug) {
                                            System.out.println("Combo secondaria selected key=" + tempTnxComboField.getSelectedKey().toString());
                                        }
                                        if (debug) {
                                            System.out.println("Combo secondaria selected index=" + tempTnxComboField.getSelectedIndex());
                                        }
                                    }
                                } catch (Exception err) {
                                    err.printStackTrace();
                                }
                                //}
                            }
                        } catch (Exception err) {
                            if (debug) {
                                System.out.println(">>>");
                            }
                            err.printStackTrace();
                        }
                    }
                    //debug
                    //System.out.println(tempTnxTextField.getDbNomeCampo()+":"+tempTnxTextField.getText());
                }

                //TEXTFIELD FORMATTED
                if (tempField.getClass().getName().equals("tnxbeans.tnxTextFieldFormatted")) {
                    tnxTextFieldFormatted tempTnxTextField = (tnxTextFieldFormatted) tempField;

                    if (tempTnxTextField.getDbRiempire() == true) {
                        //debug
                        //System.out.println(i + ":" + tempTnxTextField.getDbNomeCampo() + ":" + tempTnxTextField.getName() + ":" + tempField.getClass().getName());

                        try {

                            //debug
                            //javax.swing.JOptionPane.showMessageDialog(null,"resu:" + resu + " resu.getRow():" + String.valueOf(resu.getRow()));
                            if (resu != null) {
                                if (resu.getRow() == 0) {
                                    if (tempTnxTextField.dbTipoCampo == "numerico" || tempTnxTextField.dbTipoCampo == "valuta") {
                                        if (tempTnxTextField.getDbDefault().equalsIgnoreCase("vuoto")) {
                                            tempTnxTextField.setText("");
                                        } else {
                                            tempTnxTextField.setText("0");
                                        }
                                    } else {
                                        tempTnxTextField.setText("");
                                    }
                                } else {
                                    if (nz(tempTnxTextField.dbTipoCampo, "").equalsIgnoreCase("valuta")) {
                                        DecimalFormat form = new DecimalFormat("#,##0.00");
                                        tempTnxTextField.setText(form.format(resu.getDouble(tempTnxTextField.getDbNomeCampo())));
                                    } else if (nz(tempTnxTextField.dbTipoCampo, "").equalsIgnoreCase("numerico")) {
                                        NumberFormat form = NumberFormat.getInstance();
                                        tempTnxTextField.setText(form.format(resu.getDouble(tempTnxTextField.getDbNomeCampo())));
                                    } else if (nz(tempTnxTextField.dbTipoCampo, "").equalsIgnoreCase("data")) {
                                        String tempData = resu.getString(tempTnxTextField.getDbNomeCampo());
                                        boolean ok = false;
                                        if (tempData != null) {
                                            if (!tempData.equalsIgnoreCase("0000-00-00")) {
                                                ok = true;
                                            }
                                        }
                                        if (ok == true) {
                                            String anno = tempData.substring(2, 4);
                                            String mese = tempData.substring(5, 7);
                                            String giorno = tempData.substring(8, 10);
                                            tempTnxTextField.setText(giorno + "/" + mese + "/" + anno);
                                        } else {
                                            tempTnxTextField.setText("");
                                        }
                                    } else {
                                        if (tempTnxTextField.maxChars > 0) {
                                            if (resu.getString(tempTnxTextField.getDbNomeCampo()) != null) {
                                                if (resu.getString(tempTnxTextField.getDbNomeCampo()).length() > tempTnxTextField.maxChars) {
                                                    tempTnxTextField.setText(resu.getString(tempTnxTextField.getDbNomeCampo()).substring(0, tempTnxTextField.maxChars));
                                                } else {
                                                    tempTnxTextField.setText(resu.getString(tempTnxTextField.getDbNomeCampo()));
                                                }
                                            } else {
                                                tempTnxTextField.setText("");
                                            }
                                        } else {
                                            tempTnxTextField.setText(resu.getString(tempTnxTextField.getDbNomeCampo()));
                                        }
                                    }
                                }
                            }
                        } catch (Exception err) {
                            if (debug) {
                                System.out.println(">>>");
                            }
                            err.printStackTrace();
                        }
                    }
                    //debug
                    //System.out.println(tempTnxTextField.getDbNomeCampo()+":"+tempTnxTextField.getText());

                    if (tempTnxTextField.getDbComboAbbinata() != null) {
                        tempTnxTextField.getDbComboAbbinata().dbTrovaKey(tempTnxTextField.getText());
                    }
                }

                //COMBOFIELD
                if (tempField.getClass().getName().equals("tnxbeans.tnxComboField")) {
                    tnxComboField tempTnxComboField = (tnxComboField) tempField;
                    if (tempTnxComboField.getDbRiempire() == true) {
                        //debug
                        //System.out.println(i + ":" + tempTnxComboField.getDbNomeCampo() + ":" + tempField.getClass().getName());
                        if (tempTnxComboField.getDbNomeCampo().equals("rivalsaInpsTesto")) {
                            if (debug) {
                                System.out.println("stop");
                            }
                        }
                        try {
                            //tempTnxComboField.setText(resu.getString(tempTnxComboField.getDbNomeCampo()));
                            if (resu.getRow() == 0) {
                                tempTnxComboField.setSelectedIndex(-1);
                            } else if (tempTnxComboField.dbRiempireForceText == true) {
                                String string = convTo(nz(resu.getString(tempTnxComboField.getDbNomeCampo()), ""));

                                //prima trova se c'e' la voce memorizzata
                                tempTnxComboField.dbTrovaKey(string);

                                if (tempTnxComboField.dbTrovaMentreScrive) {
                                    try {
                                        ((AutoCompletion) ((JTextComponent) tempTnxComboField.getEditor().getEditorComponent()).getDocument()).tnxbeansrefresh = true;
                                    } catch (Exception e) {
                                    }
                                }
                                tempTnxComboField.setText(string);
                                if (tempTnxComboField.dbTrovaMentreScrive) {
                                    try {
                                        ((AutoCompletion) ((JTextComponent) tempTnxComboField.getEditor().getEditorComponent()).getDocument()).tnxbeansrefresh = false;
                                    } catch (Exception e) {
                                    }
                                }
                            } else {
                                String string = convTo(nz(resu.getString(tempTnxComboField.getDbNomeCampo()), ""));
                                //tempTnxComboField.setSelectedItem((Object)resu.getString(tempTnxComboField.getDbNomeCampo()));
                                //tempTnxComboField.dbTrovaRiga(nz(resu.getString(tempTnxComboField.getDbNomeCampo()), ""));
                                tempTnxComboField.dbTrovaKey(string);
                            }
                        } catch (Exception err) {
                            err.printStackTrace();
                        }
                    }
                }
                //MEMOFIELD
                if (tempField.getClass().getName().equals("tnxbeans.tnxMemoField")) {
                    tnxMemoField tempTnxField = (tnxMemoField) tempField;

                    //debug
                    //System.out.println(i + ":" + tempTnxField.getDbNomeCampo() + ":" + tempField.getClass().getName());

                    if (tempTnxField.getDbRiempire() == true) {
                        try {
                            if (resu.getRow() == 0) {
                                tempTnxField.setText("");
                            } else {
                                tempTnxField.setText(resu.getString(tempTnxField.getDbNomeCampo()));
                            }
                        } catch (Exception err) {
                            err.printStackTrace();
                        }
                    }
                }
                if (tempField.getClass().getName().equals("tnxbeans.tnxMemoFieldLang")) {
                    tnxMemoFieldLang fieldTextLang = (tnxMemoFieldLang) tempField;
                    if (fieldTextLang.isDbRiempire() == true) {
                        try {
                            if (resu.getRow() == 0) {
                                fieldTextLang.setText("");
                            } else {
                                fieldTextLang.setText(resu.getString(fieldTextLang.getDbNomeCampo()));
                                String nomeCampo = fieldTextLang.getDbNomeCampo();
                                Set<String> langs = fieldTextLang.getLang_area().keySet();
                                for (String lang : langs) {
                                    String nomeCampoLang = nomeCampo;
                                    if (!lang.equalsIgnoreCase("it")) {
                                        nomeCampoLang += "_" + lang;
                                    }
                                    fieldTextLang.setText(lang, resu.getString(nomeCampoLang));
                                }
                            }
                        } catch (Exception err) {
                            err.printStackTrace();
                        }
                    }
                }
                if (tempField.getClass().getName().equals("tnxbeans.tnxTextFieldLang")) {
                    tnxTextFieldLang fieldTextLang = (tnxTextFieldLang) tempField;
                    if (fieldTextLang.isDbRiempire() == true) {
                        try {
                            if (resu.getRow() == 0) {
                                fieldTextLang.setText("");
                            } else {
                                fieldTextLang.setText(resu.getString(fieldTextLang.getDbNomeCampo()));
                                String nomeCampo = fieldTextLang.getDbNomeCampo();
                                Set<String> langs = fieldTextLang.getLang_text().keySet();
                                for (String lang : langs) {
                                    String nomeCampoLang = nomeCampo;
                                    if (!lang.equalsIgnoreCase("it")) {
                                        nomeCampoLang += "_" + lang;
                                    }
                                    fieldTextLang.setText(lang, resu.getString(nomeCampoLang));
                                }
                            }
                        } catch (Exception err) {
                            err.printStackTrace();
                        }
                    }
                }
                //CHECK BOX
                if (tempField.getClass().getName().equals("tnxbeans.tnxCheckBox")) {
                    tnxCheckBox tempTnxField = (tnxCheckBox) tempField;

                    //debug
                    //System.out.println(i + ":" + tempTnxField.getDbNomeCampo() + ":" + tempField.getClass().getName());

                    if (tempTnxField.getDbRiempire() == true) {
                        try {
                            if (resu.getRow() == 0) {
                                tempTnxField.setSelected(false);
                            } else {
                                String valoreTemp = resu.getString(tempTnxField.getDbNomeCampo());
                                if (valoreTemp == null) {
                                    valoreTemp = "n";
                                }
                                //debug
                                //System.out.println("dbPanel:" + valoreTemp);
                                if (valoreTemp.equalsIgnoreCase("s")) {
                                    tempTnxField.setSelected(true);
                                } else {
                                    tempTnxField.setSelected(false);
                                }
                            }
                        } catch (Exception err) {
                            err.printStackTrace();
                        }
                    }
                }

            }

            //controllo campi secondari
            if (riempiCampiSecondari != null) {
                riempiCampiSecondari.riempiCampiSecondari();
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            if (chiudi) {
                try {
                    stat.close();
                } catch (Exception e) {
                }
                try {
                    resu.close();
                } catch (Exception e) {
                }
            }
        }

        if (!dbStato.equals(DB_INSERIMENTO)) {
            if (this.butSave != null) {
                butSave.setEnabled(false);
            }
            if (this.butSaveClose != null) {
                butSaveClose.setEnabled(false);
            }
            if (this.butUndo != null) {
                butUndo.setEnabled(false);
            }
            //if (this.butFind!=null) butFind.setEnabled(false);
            //dbStato = this.DB_LETTURA;
            setStato(this.DB_LETTURA);
        }

        //debug
        //System.out.println("dbRefresh:-------------------------------------------------");

        //events
        this._fireDbEvent(tnxDbPanel.STATUS_REFRESHING);

        this.isRefreshing = false;

        init_limits = true;

        if (!dbStato.equals(DB_INSERIMENTO)) {
            isOnSomeRecord = true;
        }

        try {
            checkPermesso();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (true);
    }

    public boolean dbSave() {
        return dbSave(null, true);
    }

    public boolean dbSave(Connection conn) {
        return dbSave(conn, true);
    }
    
    public Integer id = null;
    public boolean dbSave(Connection conn, boolean esegui_sql) {
        //java.util.Locale.setDefault(java.util.Locale.ITALY);
        _fireDbEvent(this.STATUS_PRE_SAVING);

        boolean flagOk = true;

        //debug
        //javax.swing.JOptionPane.showMessageDialog(null,"dbSave");

        //salva i valori sul resultset
        //se dbEditabile = false non faccio salvare
        if (!dbStato.equals(DB_INSERIMENTO)) {
            if (dbEditabile == false) {
                JOptionPane.showMessageDialog(this, "I dati non possono essere modificati", "Attenzione", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
            if (!isOnSomeRecord) {
                JOptionPane.showMessageDialog(this, "Prima di salvare cliccare su Nuovo o andare su un record per modificarlo", "Attenzione", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
        }

        String sql = "";
        String sqlC = "";
        String sqlV = "";
        Vector valoriChiave = new Vector();
        Vector valoriChiaveCampi = new Vector();
        Hashtable valoriChiaveH = new Hashtable() {

            @Override
            public synchronized Object put(Object key, Object value) {
                System.out.println("debug put " + key + " -> " + value);
                return super.put(key, value);
            }
        };

        //controllo resu
        boolean closed = false;
        if (resu != null) {
            try {
                resu.getRow();
            } catch (SQLException sqle) {
                closed = true;
            }
        } else {
            closed = true;
        }
        try {
            if (closed) {
//                System.out.println("resu closed");
                if (conn != null) {
                    stat = conn.createStatement();
                } else {
                    if (db != null) {
                        stat = db.getDbConn().createStatement();
                    } else {
                        stat = oldConn.createStatement();
                    }
                }
                resu = stat.executeQuery(oldSql);
                resu.next();
                meta = new ResultSetMetaDataCached(resu.getMetaData());
            }

            //ciclo i campi
            Component tempField = null;

            int count = getComponentCount2();
            ArrayList listaComps = getComponents2();

            for (int i = 0; i < count; i++) {
                boolean nullSeVuoto = false;

//                tempField=this.getComponent(i);
                tempField = (Component) listaComps.get(i);
                try {
                    //debug
                    if (debug) {
                        String dbNomeCampo = (String) tempField.getClass().getField("dbNomeCampo").get(tempField);
                        if (tempField.getClass().getName().equals("tnxbeans.tnxTextField")) {
                            tnxTextField text = (tnxTextField) tempField;
                            System.out.println("dbNomeCampo:" + text.getDbNomeCampo() + " \t\t\t text:" + text.getText());
                        } else if (tempField.getClass().getName().equals("tnxbeans.tnxComboField")) {
                            tnxComboField combo = (tnxComboField) tempField;
                            System.out.println("dbNomeCampo:" + combo.getDbNomeCampo() + " \t\t\t combo, selitem:" + combo.getSelectedItem() + " selkey:" + combo.getSelectedKey() + " text:" + combo.getText());
                        } else if (tempField.getClass().getName().equals("tnxbeans.tnxCheckBox")) {
                            tnxCheckBox check = (tnxCheckBox) tempField;
                            System.out.println("dbNomeCampo:" + check.getDbNomeCampo() + " \t\t\t check:" + check.isSelected());
                        } else {
                            System.out.println("dbNomeCampo:" + dbNomeCampo + " \t\t\t class:" + tempField.getClass());
                        }
                    }
                } catch (Exception ex) {
                }

                boolean fieldRiempire = false;
                String fieldText = "";
                HashMap<String, String> fieldTextLang = null;
                String fieldNomeCampo = "";
                String fieldDescCampo = "";
                if (tempField.getClass().getName().equals("tnxbeans.tnxTextField")
                        || tempField.getClass().getName().equals("tnxbeans.tnxTextFieldLang")
                        || tempField.getClass().getName().equals("tnxbeans.tnxTextFieldFormatted")
                        || tempField.getClass().getName().equals("tnxbeans.tnxComboField")
                        || tempField.getClass().getName().equals("tnxbeans.tnxMemoField")
                        || tempField.getClass().getName().equals("tnxbeans.tnxMemoFieldLang")
                        || tempField.getClass().getName().equals("tnxbeans.tnxCheckBox")) {
                    //debug
                    //System.out.println("panel:"+tempField.getName()+":"+tempField.getClass().getName());
                    fieldDescCampo = "*";
                    if (tempField.getClass().getName().equals("tnxbeans.tnxTextField")) {
                        tnxTextField tempTnxTextField = (tnxTextField) tempField;
                        if (tempTnxTextField.dbNomeCampo.equals("provvigione_predefinita_cliente")) {
                            System.out.println("debug");
                        }
                        if (tempTnxTextField.isDbNullSeVuoto()) {
                            nullSeVuoto = true;
                        }
                        fieldRiempire = tempTnxTextField.dbSalvare;
                        fieldDescCampo = tempTnxTextField.dbNomeCampo;
                        if (StringUtils.isNotEmpty(tempTnxTextField.getDbDescCampo()) && !"null".equalsIgnoreCase(tempTnxTextField.getDbDescCampo())) {
                            fieldDescCampo = tempTnxTextField.getDbDescCampo();
                        }
                        fieldDescCampo = StringUtils.upperCase(fieldDescCampo);

                        //debug
                        //System.out.println(tempTnxTextField.getDbNomeCampo() + ":" + tempTnxTextField.getText()+"\n");

                        //in baso al tipo di campo formatto
                        if (nz(tempTnxTextField.dbTipoCampo, "").equalsIgnoreCase("valuta")
                                || nz(tempTnxTextField.dbTipoCampo, "").equalsIgnoreCase("numerico")) {
                            //NUMERO O VALUTA
                            NumberFormat form = NumberFormat.getInstance();
                            //se ??? vuoto prendo 0
                            if (tempTnxTextField.getText().length() == 0) {
                                if (!tempTnxTextField.getDbDefault().equalsIgnoreCase("vuoto") && !nullSeVuoto) {
                                    tempTnxTextField.setText("0");
                                }
                            }
                            try {
                                //System.out.println(form.parse(tempTnxTextField.getText()));
                                fieldText = String.valueOf(form.parse(tempTnxTextField.getText()));
                            } catch (Exception err) {
                                if (fieldText.trim().length() == 0 && nullSeVuoto) {
                                    fieldText = "";
                                } else {
                                    if (debug) {
                                        System.out.println("errore in campo");
                                    }
                                    err.printStackTrace();
                                    flagOk = false;
                                    i = this.getComponentCount2();
                                    FxUtils.fadeBackground(tempTnxTextField, Color.RED);
                                    javax.swing.JOptionPane.showMessageDialog(this, "Il valore di '" + fieldDescCampo + "' non e' corretto, impossibile salvare");
                                    tempTnxTextField.requestFocus();
                                    return (false);
                                }
                            }
                        } else if (nz(tempTnxTextField.dbTipoCampo, "").equalsIgnoreCase("data")) {
                            //DATA
                            if (tempTnxTextField.getText().length() != 0) {
                                DateFormat myFormat = new SimpleDateFormat("dd/MM/yy");
                                DateFormat myFormatSql = new SimpleDateFormat("yyyy-MM-dd");
                                myFormat.setLenient(false);
                                try {
                                    java.util.Date myDate = myFormat.parse(tempTnxTextField.getText());
                                    fieldText = myFormatSql.format(myDate);
                                } catch (Exception err) {
                                    if (debug) {
                                        System.out.println("errore in campo:" + tempTnxTextField.dbNomeCampo);
                                    }
                                    err.printStackTrace();
                                    FxUtils.fadeBackground(tempTnxTextField, Color.RED);
                                    javax.swing.JOptionPane.showMessageDialog(this, "Il valore di '" + fieldDescCampo + "' non e' corretto, impossibile salvare");
                                    tempTnxTextField.requestFocus();
                                    return (false);
                                }
                            } else {
                                fieldText = "null";
                            }
                        } else {
                            //ALTRO, TESTO
                            fieldText = tempTnxTextField.getText();
                            //debug
                            if (debug) {
                                System.out.println(tempTnxTextField.getDbNomeCampo() + " -> value:" + fieldText + " hexdump:" + DebugUtils.stringToHex(fieldText));
                            }
                        }
                        fieldNomeCampo = tempTnxTextField.getDbNomeCampo();
                    }
                    //TEXTFIELD FORMATTED
                    if (tempField.getClass().getName().equals("tnxbeans.tnxTextFieldFormatted")) {
                        tnxTextFieldFormatted tempTnxTextField = (tnxTextFieldFormatted) tempField;
                        fieldRiempire = tempTnxTextField.dbSalvare;
                        if (tempTnxTextField.getDbDescCampo() == null) {
                            fieldDescCampo = tempTnxTextField.getDbNomeCampo();
                        } else if (tempTnxTextField.getDbDescCampo().length() == 0) {
                            fieldDescCampo = tempTnxTextField.getDbNomeCampo();
                        } else if (tempTnxTextField.getDbDescCampo().equalsIgnoreCase("null")) {
                            fieldDescCampo = tempTnxTextField.getDbNomeCampo();
                        } else {
                            fieldDescCampo = tempTnxTextField.getDbDescCampo();
                        }
                        fieldDescCampo = fieldDescCampo.toUpperCase();

                        //debug
                        //System.out.println(tempTnxTextField.getDbNomeCampo() + ":" + tempTnxTextField.getText()+"\n");

                        //in baso al tipo di campo formatto
                        if (nz(tempTnxTextField.dbTipoCampo, "").equalsIgnoreCase("valuta")
                                || nz(tempTnxTextField.dbTipoCampo, "").equalsIgnoreCase("numerico")) {
                            //NUMERO O VALUTA
                            NumberFormat form = NumberFormat.getInstance();
                            //se ??? vuoto prendo 0
                            if (tempTnxTextField.getText().length() == 0) {
                                tempTnxTextField.setText("0");
                            }
                            try {
                                //System.out.println(form.parse(tempTnxTextField.getText()));
                                fieldText = String.valueOf(form.parse(tempTnxTextField.getText()));
                            } catch (Exception err) {
                                if (debug) {
                                    System.out.println("errore in campo");
                                }
                                err.printStackTrace();
                                flagOk = false;
                                i = this.getComponentCount2();
                                FxUtils.fadeBackground(tempTnxTextField, Color.RED);
                                javax.swing.JOptionPane.showMessageDialog(this, "Il valore di '" + fieldDescCampo + "' non e' corretto, impossibile salvare");
                                tempTnxTextField.requestFocus();
                                return (false);
                            }
                        } else if (nz(tempTnxTextField.dbTipoCampo, "").equalsIgnoreCase("data")) {
                            //DATA
                            if (tempTnxTextField.getText().length() != 0) {
                                DateFormat myFormat = new SimpleDateFormat("dd/MM/yy");
                                DateFormat myFormatSql = new SimpleDateFormat("yyyy-MM-dd");
                                myFormat.setLenient(false);
                                try {
                                    java.util.Date myDate = myFormat.parse(tempTnxTextField.getText());
                                    fieldText = myFormatSql.format(myDate);
                                } catch (Exception err) {
                                    if (debug) {
                                        System.out.println("errore in campo");
                                    }
                                    err.printStackTrace();
                                    FxUtils.fadeBackground(tempTnxTextField, Color.RED);
                                    javax.swing.JOptionPane.showMessageDialog(this, "Il valore di '" + fieldDescCampo + "' non e' corretto, impossibile salvare");
                                    tempTnxTextField.requestFocus();
                                    return (false);
                                }
                            } else {
                                fieldText = "null";
                            }
                        } else {
                            //ALTRO, TESTO
                            fieldText = tempTnxTextField.getText();
                        }
                        fieldNomeCampo = tempTnxTextField.getDbNomeCampo();
                    }
                    if (tempField.getClass().getName().equals("tnxbeans.tnxMemoField")) {
                        tnxMemoField tempTnxField = (tnxMemoField) tempField;
                        fieldRiempire = tempTnxField.dbSalvare;
                        fieldText = tempTnxField.getText();
                        fieldNomeCampo = tempTnxField.getDbNomeCampo();
                    }
                    if (tempField.getClass().getName().equals("tnxbeans.tnxMemoFieldLang")) {
                        tnxMemoFieldLang tempTnxField = (tnxMemoFieldLang) tempField;
                        fieldRiempire = tempTnxField.dbSalvare;
                        fieldText = null;
                        fieldTextLang = tempTnxField.getTextLang();
                        fieldNomeCampo = tempTnxField.getDbNomeCampo();
                    }
                    if (tempField.getClass().getName().equals("tnxbeans.tnxTextFieldLang")) {
                        tnxTextFieldLang tempTnxField = (tnxTextFieldLang) tempField;
                        fieldRiempire = tempTnxField.dbSalvare;
                        fieldText = null;
                        fieldTextLang = tempTnxField.getTextLang();
                        fieldNomeCampo = tempTnxField.getDbNomeCampo();
                    }
                    if (tempField.getClass().getName().equals("tnxbeans.tnxCheckBox")) {
                        tnxCheckBox tempTnxField = (tnxCheckBox) tempField;
                        fieldRiempire = tempTnxField.dbSalvare;
                        if (tempTnxField.isSelected() == true) {
                            fieldText = "S";
                        } else {
                            fieldText = "N";
                        }
                        fieldNomeCampo = tempTnxField.getDbNomeCampo();
                    }
                    if (tempField.getClass().getName().equals("tnxbeans.tnxComboField")) {
                        tnxComboField tempTnxComboField = (tnxComboField) tempField;

                        //debug
                        //System.out.println("dbSave combo Tex:" + tempTnxComboField.getDbNomeCampo() + "=" + tempTnxComboField.getText());
                        //System.out.println("dbSave combo Sel:" + tempTnxComboField.getName() + "=" + tempTnxComboField.getSelectedItem().toString());

                        fieldRiempire = tempTnxComboField.dbSalvare;
                        fieldDescCampo = "*";
                        if (tempTnxComboField.getDbDescCampo() == null) {
                            if (tempTnxComboField.getDbNomeCampo() != null) {
                                fieldDescCampo = tempTnxComboField.getDbNomeCampo();
                            }
                        } else if (tempTnxComboField.getDbDescCampo().length() == 0) {
                            fieldDescCampo = tempTnxComboField.getDbNomeCampo();
                        } else if (tempTnxComboField.getDbDescCampo().equalsIgnoreCase("null")) {
                            fieldDescCampo = tempTnxComboField.getDbNomeCampo();
                        } else {
                            fieldDescCampo = tempTnxComboField.getDbDescCampo();
                        }
                        fieldDescCampo = fieldDescCampo.toUpperCase();
                        if (tempTnxComboField.getSelectedItem() == null) {
                            //fieldText = "";
                            fieldText = tempTnxComboField.getText();
                        } else {
                            if (tempTnxComboField.getDbSalvaKey() == true) {
                                if (tempTnxComboField.getSelectedKey() != null) {
                                    fieldText = tempTnxComboField.getSelectedKey().toString();
                                } else {
                                    fieldText = null;
                                }
                            } else {
                                if (tempTnxComboField.getSelectedItem() != null) {
                                    fieldText = tempTnxComboField.getSelectedItem().toString();
                                } else {
                                    fieldText = null;
                                }
                            }
                        }

                        fieldText = convFrom(fieldText);

                        //in baso al tipo di campo formatto
                        if (fieldRiempire == true) {
                            if (nz(tempTnxComboField.dbTipoCampo, "").equalsIgnoreCase("valuta")
                                    || nz(tempTnxComboField.dbTipoCampo, "").equalsIgnoreCase("numerico")) {
                                NumberFormat form = NumberFormat.getInstance();
                                try {
                                    //System.out.println(form.parse(fieldText));
                                    fieldText = String.valueOf(form.parse(fieldText));
                                } catch (Exception err) {
                                    fieldText = "0";
                                    //                                System.out.println("errore in campo");
                                    //                                err.printStackTrace();
                                    //                                flagOk = false;
                                    //                                i = this.getComponentCount();
                                    //                                javax.swing.JOptionPane.showMessageDialog(this,"Il valore di '" + fieldDescCampo + "' non e' corretto, impossibile salvare");
                                    //                                tempTnxComboField.requestFocus();
                                }
                            }
                            fieldNomeCampo = tempTnxComboField.getDbNomeCampo();
                        }

//                        //debug
//                        String temp="";
//                        if (fieldRiempire == true) {
//                            temp += fieldRiempire + "\t" + fieldText + "\t" + fieldNomeCampo;
//                            System.out.println("dbpanel save combo:" + temp);
//                        }
//                        System.out.println(tempTnxComboField.getDbNomeCampo() + " -> value:" + fieldText + " hexdump:" + DebugUtils.stringToHex(fieldText));
                    }

                    List<String> fieldNomeCampo_list = new ArrayList<String>();
                    List<String> fieldText_list = new ArrayList<String>();
                    if (fieldText == null && fieldTextLang != null) {
                        Set<String> langs = fieldTextLang.keySet();
                        for (String lang : langs) {
                            if (lang.equalsIgnoreCase("it")) {
                                fieldNomeCampo_list.add(fieldNomeCampo);
                            } else {
                                fieldNomeCampo_list.add(fieldNomeCampo + "_" + lang);
                            }
                            fieldText_list.add(fieldTextLang.get(lang));
                        }
                    } else {
                        fieldNomeCampo_list.add(fieldNomeCampo);
                        fieldText_list.add(fieldText);
                    }

                    for (int ik = 0; ik < fieldNomeCampo_list.size(); ik++) {
                        fieldNomeCampo = fieldNomeCampo_list.get(ik);
                        fieldText = fieldText_list.get(ik);

                        boolean trovato = false;
                        int idCampo = 0;
                        if (fieldRiempire == true) {
                            try {
                                //tempTnxTextField.setText(resu.getString(tempTnxTextField.getDbNomeCampo()));
                                if (dbStato == this.DB_INSERIMENTO) {
                                    //INSERIMENTO
                                    //inserisco in base al tipo di campo
                                    //cerco id del camp chiave
                                    for (int j = 0; j < meta.getColumnCount(); j++) {
                                        //debug
                                        //javax.swing.JOptionPane.showMessageDialog(null,"meta:="+meta.getColumnName(j+1)+" chiave:"+this.dbChiave.get(i));
                                        if (meta.getColumnName(j + 1).equalsIgnoreCase(fieldNomeCampo)) {
                                            trovato = true;
                                            idCampo = j + 1;
                                            j = meta.getColumnCount();
                                        }
                                    }
                                    if (trovato != true) {
                                        //javax.swing.JOptionPane.showMessageDialog(null,"Errore, e [" + fieldNomeCampo + "]");
                                        SwingUtils.showErrorMessage(this, "<html>Errore database, il campo <b>" + fieldNomeCampo + "</b> non e' presente nella tabella <b>" + this.dbNomeTabella + "</b><br>Tutti i dati eccetto il campo segnalato verranno comunque memorizzati</html>", "Errore database");
                                        continue;
                                    } else {
                                        sqlC += "`" + fieldNomeCampo + "`";
                                        sqlC += ",";
                                    }

                                    //debug
                                      /*String temp="";
                                    temp=temp + ". " + meta.getColumnName(idCampo) + "\n";
                                    temp=temp + ". " + meta.getColumnType(idCampo) + "\n";
                                    temp=temp + ". " + meta.getColumnTypeName(idCampo) + "\n";
                                    javax.swing.JOptionPane.showMessageDialog(null,temp);*/

                                    String tipoCampo = meta.getColumnTypeName(idCampo);
                                    //faccio un controllo sul tipo di campo
                                    if (tipoCampo.equalsIgnoreCase("LONG")) {
                                        if (!it.tnx.Checks.isInteger(fieldText, true)) {
                                            javax.swing.JOptionPane.showMessageDialog(this, "Il valore di '" + fieldDescCampo + "' deve essere numerico, impossibile salvare");
                                            //tempTnxField.requestFocus();
                                            return false;
                                        }
                                    }

                                    if (fieldText.length() == 0 && nullSeVuoto) {
                                        sqlV += "null";
                                    } else {
                                        sqlV += pc(fieldText, tipoCampo);
                                    }
                                    sqlV += ",";

                                    //faccio anche per inserimento mi serve per fare la select single dopo
                                    //salvo i valori se chiave
                                    for (int h = 0; h < dbChiave.size(); h++) {
                                        if (fieldNomeCampo.equalsIgnoreCase((String) this.dbChiave.get(h))) {
                                            //controllo se ??? autoincremente
                                            if (fieldText.equals("")) {
                                                if (this.dbChiave.size() == 1) {
                                                    //cetrco ultimo e + 1
                                                    sql = "select " + fieldNomeCampo + " from " + this.dbNomeTabella + " order by " + fieldNomeCampo + " desc limit 0,1";
                                                    Statement statTmp = null;
                                                    try {
                                                        if (db != null) {
                                                            statTmp = db.getDbConn().createStatement();
                                                        } else {
                                                            statTmp = oldConn.createStatement();
                                                        }
                                                        ResultSet tempResu = statTmp.executeQuery(sql);
                                                        if (tempResu.next() == true) {
                                                            try {
                                                                fieldText = String.valueOf(tempResu.getInt(fieldNomeCampo) + 1);
                                                            } catch (Exception err1) {
                                                                err1.printStackTrace();
                                                            }
                                                        } else {
                                                            fieldText = "1";
                                                        }
                                                    } catch (Exception ex) {
                                                        ex.printStackTrace();
                                                    } finally {
                                                        if (statTmp != null) {
                                                            statTmp.close();
                                                        }
                                                    }
                                                }
                                            }
                                            valoriChiaveH.put(fieldNomeCampo, fieldText);
                                        }
                                    }
                                } else {
                                    //MODIFICA
                                    //inserisco in base al tipo di campo
                                    //cerco id del camp chiave
                                    for (int j = 0; j < meta.getColumnCount(); j++) {
                                        //debug
                                        //javax.swing.JOptionPane.showMessageDialog(null,"meta:="+meta.getColumnName(j+1)+" chiave:"+this.dbChiave.get(i));
                                        if (meta.getColumnName(j + 1).equalsIgnoreCase(fieldNomeCampo)) {
                                            trovato = true;
                                            idCampo = j + 1;
                                            j = meta.getColumnCount();
                                        }
                                    }
                                    if (trovato != true) {
                                        //javax.swing.JOptionPane.showMessageDialog(null,"errore, non trovato id campo chiave");
                                        SwingUtils.showErrorMessage(this, "<html>Errore database, il campo <b>" + fieldNomeCampo + "</b> non e' presente nella tabella <b>" + this.dbNomeTabella + "</b><br>Tutti i dati eccetto il campo segnalato verranno comunque memorizzati</html>", "Errore database");
                                    } else {
                                        //debug
                                          /*
                                        String temp="";
                                        temp=temp + ". " + meta.getColumnName(idCampo) + "\n";
                                        temp=temp + ". " + meta.getColumnType(idCampo) + "\n";
                                        temp=temp + ". " + meta.getColumnTypeName(idCampo) + "\n";
                                        javax.swing.JOptionPane.showMessageDialog(null,temp);*/

                                        sql += "`" + fieldNomeCampo + "`";
                                        sql += " = ";

                                        String tipoCampo = meta.getColumnTypeName(idCampo);
                                        if (fieldText == null || (fieldText.length() == 0 && nullSeVuoto)) {
                                            sql += "null";
                                        } else {
                                            sql += pc(fieldText, tipoCampo);
                                        }
                                        sql += ",";
                                        //salvo i valori se chiave
                                        for (int h = 0; h < dbChiave.size(); h++) {
                                            if (fieldNomeCampo.equalsIgnoreCase((String) this.dbChiave.get(h))) {
                                                valoriChiaveH.put(fieldNomeCampo, pc(String.valueOf(dbChiaveValori.get(fieldNomeCampo)), tipoCampo));
                                            }
                                        }
                                    }
                                }
                            } catch (Exception err) {
                                javax.swing.JOptionPane.showMessageDialog(null, "ciclo campi:" + err.toString());
                                err.printStackTrace();
                            }
                        } else {
                            //aggiungo il controllo che se e' un campo autoincrement metto propriet? di non salvare e quindi non mi avvalora neppure valoriChiaveH
                            if (fieldNomeCampo != null) {
                                if (fieldNomeCampo.length() > 0 && this.dbChiave.contains(fieldNomeCampo)) {
                                    try {
                                        for (int j = 0; j < meta.getColumnCount(); j++) {
                                            if (meta.getColumnName(j + 1).equalsIgnoreCase(fieldNomeCampo)) {
                                                trovato = true;
                                                idCampo = j + 1;
                                                j = meta.getColumnCount();
                                            }
                                        }
                                        if (trovato != true) {
                                            javax.swing.JOptionPane.showMessageDialog(null, "errore, non trovato id campo chiave [" + fieldNomeCampo + "]");
                                        }
                                        String tipoCampo = meta.getColumnTypeName(idCampo);
                                        //salvo i valori se chiave
                                        for (int h = 0; h < dbChiave.size(); h++) {
                                            if (fieldNomeCampo.equalsIgnoreCase((String) this.dbChiave.get(h))) {
                                                if (!valoriChiaveH.containsKey(fieldNomeCampo)) {
                                                    if (dbChiaveValori == null) {
                                                        System.err.println("tnxDbPanel dbChiaveValori nullo per fieldNomeCampo:" + fieldNomeCampo + " stato:" + dbStato + " dbchiaveautoinc:" + dbChiaveAutoInc);
                                                    } else {
                                                        valoriChiaveH.put(fieldNomeCampo, pc(String.valueOf(dbChiaveValori.get(fieldNomeCampo)), tipoCampo));
                                                    }
                                                }
                                            }
                                        }
                                    } catch (Exception err) {
                                        err.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            }

            //aggiungere campi richiesti
            Hashtable campi = getCampiAggiuntivi();
            if (campi != null) {
                for (Object k : campi.keySet()) {
                    if (dbStato.equals(DB_INSERIMENTO)) {
                        //INSERIMENTO
                        sqlC += "`" + k.toString() + "`,";
                        sqlV += campi.get(k) + ",";
                    } else {
                        sql += "`" + k.toString() + "`";
                        sql += " = " + campi.get(k) + ",";
                    }
                }
            }

            if (dbStato == this.DB_INSERIMENTO) {
                //INSERIMENTO
                sqlC = sqlC.substring(0, sqlC.length() - 1);
                sqlV = sqlV.substring(0, sqlV.length() - 1);
                sql = "insert into " + this.dbNomeTabella + " ( " + sqlC + " ) values (" + sqlV + ")";
                //debug
                //javax.swing.JOptionPane.showMessageDialog(null,sql);
            } else {
                //MODIFICA
                sql = "update " + this.dbNomeTabella + " set " + sql;
                sql = sql.substring(0, sql.length() - 1);
                //aggiungo where per modifica
                sql += " where ";

                //debug
                //javax.swing.JOptionPane.showMessageDialog(null,String.valueOf(dbChiave.size()));

                checkChiavi(dbChiave);
                checkChiaviH(valoriChiaveH);

                for (int l = 0; l < dbChiave.size(); l++) {
                    if (l == 0) {
                        //sql += (String)dbChiave.get(l) + " = " + (String)valoriChiave.get(l);
                        sql += (String) dbChiave.get(l) + " = " + (String) valoriChiaveH.get((String) dbChiave.get(l));
                    } else {
                        //sql += " and " + (String)dbChiave.get(l) + " = " + (String)valoriChiave.get(l);
                        sql += " and " + (String) dbChiave.get(l) + " = " + (String) valoriChiaveH.get((String) dbChiave.get(l));
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                stat.close();
            } catch (Exception e) {
            }
            try {
                resu.close();
            } catch (Exception e) {
            }
        }

        //eseguo la query
        Statement statTmp = null;
        try {
            if (conn != null) {
                statTmp = conn.createStatement();
            } else {
                if (db != null) {
                    statTmp = db.getDbConn().createStatement();
                } else {
                    statTmp = oldConn.createStatement();
                }
            }

            //debug
            //javax.swing.JOptionPane.showMessageDialog(null,spezza(sql,40));
            if (erroreCampiChiaveVuoti(valoriChiaveH)) {
                flagOk = false;
            }

            if (flagOk == true) {
                if (debug) {
                    System.out.println("tnxPanel:dbSave:" + sql);
                }

                //debug
//                StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
//                System.out.println("---START SQL TRACE---");
                System.out.println("sql: " + sql);
//                for (StackTraceElement e : stacks) {
//                    if (e.getClassName().startsWith("gestioneFatture") || e.getClassName().startsWith("it.tnx")) {
//                        System.out.println("TRACE: \t\t" + e.toString());
//                    }
//                }
//                System.out.println("---END SQL TRACE---");

                if (esegui_sql) {
                    statTmp.execute(sql);
                }

                if (dbStato == this.DB_INSERIMENTO) {
                    //controllo valoriChiave autoinc
                    if (dbChiaveAutoInc) {
                        if (id != null) {
                            last_inserted_id = id;
                            valoriChiaveH.put(dbChiave.get(0), id);
                        } else {
                            ResultSet r = statTmp.executeQuery("SELECT LAST_INSERT_ID()");
                            r.next();
                            last_inserted_id = r.getInt(1);
                            valoriChiaveH.put(dbChiave.get(0), r.getString(1));
                        }
                        //inserisco nel campo
                        try {
                            String campok = (String) dbChiave.get(0);
                            int count = getComponentCount2();
                            ArrayList listaComps = getComponents2();
                            for (int i = 0; i < count; i++) {
                                Component tempField = (Component) listaComps.get(i);
                                try {
                                    String dbNomeCampo = (String) tempField.getClass().getField("dbNomeCampo").get(tempField);
                                    if (dbNomeCampo.equalsIgnoreCase(campok)) {
                                        ((JTextField) tempField).setText(String.valueOf(last_inserted_id));
                                    }
                                } catch (Exception ex) {
                                }
                            }
                        } catch (Exception e) {
                        }
                    } else {
                    }
                    //events
                    _fireDbEvent(tnxDbPanel.STATUS_SAVING);
                    //select single di questo nuovo
                    dbSelectSingle(valoriChiaveH);

                    //TODO implementare una ricerca migliore per sincronizzare la griglia con i dati, in caso di molti record � lento
//                    //lo aggiungo in griglia
//                    LazyResultSetModel lazym = (LazyResultSetModel)griglia.getModel();
//                    Object[] row = new Object[griglia.getColumnCount()];
//                    row[0] = valoriChiaveH.get(dbChiave.get(0));
//                    lazym.addRow(row);

                    if (griglia != null) {
                        griglia.dbRefresh();
                    }

                    dbRefresh();
                } else {
                    //events
                    _fireDbEvent(this.STATUS_SAVING);
                }
                if (this.butSave != null) {
                    butSave.setEnabled(false);
                }
                if (this.butSaveClose != null) {
                    butSaveClose.setEnabled(false);
                }
                if (this.butUndo != null) {
                    butUndo.setEnabled(false);
                }
                if (this.butFind != null) {
                    butFind.setEnabled(false);
                }
                //this.dbStato = this.DB_LETTURA;
                setStato(this.DB_LETTURA);
                isOnSomeRecord = true;
                if (this.butDele != null) {
                    butDele.setEnabled(true);
                }
                if (butNew != null) {
                    butNew.setEnabled(true);
                }

                return (true);
            }

        } catch (Exception err) {
            if (err instanceof SQLWarning) {
                if (debug) {
                    System.out.println("warn:" + err.toString());
                }
                return true;
            }
            err.printStackTrace();
            if (err.toString().indexOf("Duplicate entry") > 0) {
                javax.swing.JOptionPane.showMessageDialog(this, "Impossibile inserire, record gia' presente", "Errore", javax.swing.JOptionPane.ERROR_MESSAGE);
            } else {
                if (!erroreCampiChiaveVuoti(valoriChiaveH)) {
                    showExc("Errore durante il salvataggio dei dati", err, sql);
//                    javax.swing.JOptionPane.showMessageDialog(null, err.toString());
//                    javax.swing.JOptionPane.showMessageDialog(null, "tnxPanel:dbSave:" + sql);
                    if (debug) {
                        System.out.println("sqlerr:" + sql);
                    }
                    System.err.println("tnxPanel:dbSave:" + sql);
                }
            }
            return (false);
        } finally {
            try {
                if (statTmp != null) {
                    statTmp.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return (false);
    }
    
    private boolean erroreCampiChiaveVuoti(Hashtable vect) {
        return false;
        /*
        if (vect.size() == 0) {
        System.out.println("checkChiaviHVuoti:vuoto");
        return true;
        } else {

        boolean vuoti = false;
        Object temp;
        while (vect.keys().hasMoreElements()) {
        temp = vect.keys().nextElement();
        if (temp != null) {
        if (String.valueOf(temp).length() == 0) {
        vuoti = true;
        break;
        }
        } else {
        vuoti = true;
        break;
        }
        }
        if (vuoti == true) {
        System.out.println("checkChiaviHVuoti:vuoti");
        javax.swing.JOptionPane.showMessageDialog(null,"Alcuni dei valori chiave non sono riempiti, impossibile salvare");
        return true;
        }
        return false;
        }
         */
    }

    private void checkChiavi(Vector vect) {
        if (vect.size() == 0) {
            if (debug) {
                System.out.println("checkChiavi:size=0");
            }
            javax.swing.JOptionPane.showMessageDialog(null, "checkChiavi1");
        } else {
            boolean allNull = true;
            for (int i = 0; i < vect.size(); i++) {
                if (vect.get(i) != null) {
                    allNull = false;
                    break;
                }
            }
            if (allNull == true) {
                if (debug) {
                    System.out.println("checkChiavi:nulls");
                }
                javax.swing.JOptionPane.showMessageDialog(null, "checkChiavi2");
            }
        }
    }

    private void checkChiaviH(Hashtable vect) {
        if (vect.isEmpty()) {
            if (debug) {
                System.out.println("checkChiaviH:size=0");
            }
//            javax.swing.JOptionPane.showMessageDialog(null,"checkChiaviH1");
            try {
                //provo a cerca chiave su campi resultset
                for (Object k : dbChiave) {
                    String sk = String.valueOf(k);
                    if (debug) {
                        System.out.println("prendere:" + sk + " valore:" + resu.getString(sk));
                    }
                    vect.put(k, resu.getString(sk));
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
        boolean allNull = true;
        Object temp;
        while (vect.keys().hasMoreElements()) {
            temp = vect.keys().nextElement();
            if (temp != null) {
                allNull = false;
                break;
            }
        }
        if (allNull == true) {
            if (debug) {
                System.out.println("checkChiaviH:nulls");
            }
            javax.swing.JOptionPane.showMessageDialog(null, "checkChiaviH2");
        }
    }

    public boolean dbCheckModificati() {
        if (!permesso_scrittura) return false;
        
        boolean flag = false;
        //ciclo i campi
        Component tempField = null;
        for (int i = 0; i < this.getComponentCount2(); i++) {
            tempField = this.getComponent2(i);
            //debug
            //javax.swing.JOptionPane.showMessageDialog(null,tempField.getClass().getName());
            //TEXTFIELD FORMATTED
            if (tempField.getClass().getName().equals("tnxbeans.tnxTextFieldFormatted")) {
                //debug
                //System.out.println("check:"+tempField.getClass().getName());

                tnxTextFieldFormatted tempTnxTextField = (tnxTextFieldFormatted) tempField;
                if (tempTnxTextField.getDbRiempire() == true) {
                    try {
                        if (tempTnxTextField.dbModificato == true) {
                            //debug
                            if (debug) {
                                System.out.println("dbCheckModificati : " + tempTnxTextField.dbNomeCampo);
                            }

                            flag = true;
                            i = this.getComponentCount2();
                        }
                    } catch (Exception err) {
                        err.printStackTrace();
                    }
                }
            }
            //COMBOFIELD
            if (tempField.getClass().getName().equals("tnxbeans.tnxComboField")) {
                //debug
                //System.out.println("check:"+tempField.getClass().getName());

                tnxComboField tempTnxComboField = (tnxComboField) tempField;
                if (tempTnxComboField.getDbRiempire() == true || tempTnxComboField.getDbTextAbbinato() != null) {
                    try {
                        if (tempTnxComboField.dbModificato == true) {
                            //debug
                            if (debug) {
                                System.out.println("dbCheckModificati : " + tempTnxComboField.dbNomeCampo);
                            }

                            flag = true;
                            i = this.getComponentCount2();
                        }
                    } catch (Exception err) {
                        err.printStackTrace();
                    }
                }
            }
            //TEXTFIELD e MEMOFIELD
            if (tempField.getClass().getName().equals("tnxbeans.tnxMemoField")
                    || tempField.getClass().getName().equals("tnxbeans.tnxMemoFieldLang")
                    || tempField.getClass().getName().equals("tnxbeans.tnxTextField")
                    || tempField.getClass().getName().equals("tnxbeans.tnxTextFieldLang")) {
                BasicField field = (BasicField) tempField;

                if (field.isDbRiempire() == true) {
                    try {
                        if (field.isDbModificato() == true) {
                            flag = true;
                            if (debug) {
                                System.out.println("dbCheckModificati : " + field.getDbNomeCampo());
                            }
                            i = this.getComponentCount2();
                        }
                    } catch (Exception err) {
                        err.printStackTrace();
                    }
                }
            }
            //CHECK BOX
            if (tempField.getClass().getName().equals("tnxbeans.tnxCheckBox")) {
                tnxCheckBox tempTnxField = (tnxCheckBox) tempField;
                if (tempTnxField.getDbRiempire() == true) {
                    try {
                        if (tempTnxField.dbModificato == true) {
                            flag = true;
                            if (debug) {
                                System.out.println("dbCheckModificati : " + tempTnxField.dbNomeCampo);
                            }
                            i = this.getComponentCount2();
                        }
                    } catch (Exception err) {
                        err.printStackTrace();
                    }
                }
            }

        }
        if (flag == true) {
            //if (dbStato != this.DB_INSERIMENTO) {
            if (dbStato != this.DB_INSERIMENTO && this.dbEditabile == true) {
                //dbStato=this.DB_MODIFICA;
                setStato(this.DB_MODIFICA);
                //debug
                //javax.swing.JOptionPane.showMessageDialog(null,"modifica");
                if (this.butSave != null) {
                    butSave.setEnabled(true);
                }
                if (this.butSaveClose != null) {
                    butSaveClose.setEnabled(true);
                }
                if (this.butUndo != null) {
                    butUndo.setEnabled(true);
                }
                if (this.butFind != null) {
                    butFind.setEnabled(true);
                }
            }
            if (dbStato != this.DB_INSERIMENTO && this.dbEditabile == false) {
                if (this.butUndo != null) {
                    butUndo.setEnabled(true);
                }
                if (this.butFind != null) {
                    butFind.setEnabled(true);
                }
            }
            return (true);
        } else {
            return (false);
        }
    }

    public void dbForzaModificati() {
        if (dbStato != this.DB_INSERIMENTO && this.dbEditabile == true) {
            setStato(this.DB_MODIFICA);
            if (this.butSave != null) {
                butSave.setEnabled(true);
            }
            if (this.butSaveClose != null) {
                butSaveClose.setEnabled(true);
            }
            if (this.butUndo != null) {
                butUndo.setEnabled(true);
            }
            if (this.butFind != null) {
                butFind.setEnabled(true);
            }
        }
        if (dbStato != this.DB_INSERIMENTO && this.dbEditabile == false) {
            if (this.butUndo != null) {
                butUndo.setEnabled(true);
            }
            if (this.butFind != null) {
                butFind.setEnabled(true);
            }
        }
    }

    public void dbCheckModificatiReset() {
        //ciclo i campi
        Component tempField = null;
        for (int i = 0; i < this.getComponentCount2(); i++) {
            tempField = this.getComponent2(i);
            if (tempField.getClass().getName().equals("tnxbeans.tnxTextField")) {
                tnxTextField tempTnxTextField = (tnxTextField) tempField;
                if (tempTnxTextField.getDbRiempire() == true) {
                    try {
                        if (tempTnxTextField.dbModificato == true) {
                            tempTnxTextField.dbModificato = false;
                        }
                    } catch (Exception err) {
                        err.printStackTrace();
                    }
                }
            }
            //TEXTFIELD FORMATTED
            if (tempField.getClass().getName().equals("tnxbeans.tnxTextFieldFormatted")) {
                tnxTextFieldFormatted tempTnxTextField = (tnxTextFieldFormatted) tempField;
                if (tempTnxTextField.getDbRiempire() == true) {
                    try {
                        if (tempTnxTextField.dbModificato == true) {
                            tempTnxTextField.dbModificato = false;
                        }
                    } catch (Exception err) {
                        err.printStackTrace();
                    }
                }
            }
            //COMBOFIELD
            if (tempField.getClass().getName().equals("tnxbeans.tnxComboField")) {
                tnxComboField tempTnxComboField = (tnxComboField) tempField;
                if (tempTnxComboField.getDbRiempire() == true || tempTnxComboField.getDbTextAbbinato() != null) {
                    try {
                        if (tempTnxComboField.dbModificato == true) {
                            tempTnxComboField.dbModificato = false;
                        }
                    } catch (Exception err) {
                        err.printStackTrace();
                    }
                }
            }
            //MEMOFIELD
            if (tempField.getClass().getName().equals("tnxbeans.tnxMemoField")) {
                tnxMemoField tempTnxField = (tnxMemoField) tempField;
                if (tempTnxField.getDbRiempire() == true) {
                    try {
                        if (tempTnxField.dbModificato == true) {
                            tempTnxField.dbModificato = false;
                        }
                    } catch (Exception err) {
                        err.printStackTrace();
                    }
                }
            }
            //CHECK BOX
            if (tempField.getClass().getName().equals("tnxbeans.tnxCheckBox")) {
                tnxCheckBox tempTnxField = (tnxCheckBox) tempField;
                if (tempTnxField.getDbRiempire() == true) {
                    try {
                        if (tempTnxField.dbModificato == true) {
                            tempTnxField.dbModificato = false;
                        }
                    } catch (Exception err) {
                        err.printStackTrace();
                    }
                }
            }
        }
    }

    public void dbCambiaStato(String stato) {
        if (stato.equals(this.DB_LETTURA)) {
            //this.dbStato = this.DB_LETTURA;
            setStato(this.DB_LETTURA);
            if (this.butSave != null) {
                butSave.setEnabled(false);
            }
            if (this.butSaveClose != null) {
                butSaveClose.setEnabled(false);
            }
            if (this.butUndo != null) {
                butUndo.setEnabled(false);
            }
            if (this.butFind != null) {
                butFind.setEnabled(false);
            }
            isOnSomeRecord = true;
            if (this.butDele != null) {
                butDele.setEnabled(true);
            }
            if (butNew != null) {
                butNew.setEnabled(true);
            }
        } else if (stato.equals(this.DB_MODIFICA) || stato.equals(this.DB_INSERIMENTO)) {
            //this.dbStato = stato;
            setStato(stato);
            if (this.butSave != null) {
                butSave.setEnabled(true);
            }
            if (this.butSaveClose != null) {
                butSaveClose.setEnabled(true);
            }
            if (this.butUndo != null) {
                butUndo.setEnabled(true);
            }
            if (this.butFind != null) {
                butFind.setEnabled(true);
            }
            isOnSomeRecord = true;
            if (this.butDele != null) {
                butDele.setEnabled(false);
            }
            if (butNew != null) {
                butNew.setEnabled(false);
            }
        }
    }

    public Object dbGetField(String fieldName) {
        ResultSet myresu = null;
        Statement mystat = null;
        boolean closed = false;
        try {
            //controllo resu
            if (resu != null) {
                try {
                    resu.getRow();
                } catch (SQLException sqle) {
                    closed = true;
                }
            } else {
                closed = true;
            }
            if (closed) {
//                System.out.println("resu closed");
                if (db != null) {
                    mystat = db.getDbConn().createStatement();
                } else {
                    mystat = oldConn.createStatement();
                }
                myresu = mystat.executeQuery(oldSql);
                myresu.next();
            } else {
                myresu = resu;
            }
            return myresu.getObject(fieldName);
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        } finally {
            if (closed) {
                try {
                    mystat.close();
                } catch (Exception e) {
                }
                try {
                    myresu.close();
                } catch (Exception e) {
                }
            }
        }
    }

    public Object dbGetFieldOnlyNotClosed(String fieldName) {
        ResultSet myresu = null;
        Statement mystat = null;
        boolean closed = false;
        try {
            //controllo resu
            if (resu != null) {
                try {
                    resu.getRow();
                } catch (SQLException sqle) {
                    closed = true;
                }
            } else {
                closed = true;
            }
            if (!closed) {
                myresu = resu;
            } else {
                String sql = "select " + fieldName + " from " + dbNomeTabella + " where " + getCondChiave(dbChiaveValori);
                System.out.println("sql = " + sql);
                return DbUtils.getObject(oldConn, sql);
            }
            return myresu.getObject(fieldName);
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
    }

    String replaceChars(String stri, char daTrov, String daMett) {
        if (stri == null) {
            stri = "";
        }
        int leng = stri.length();
        String prim = "";
        String dopo = "";
        String risu = "";
        int i = 0;
        int oldI = 0;
        while (i < leng) {
            if (stri.charAt(i) == daTrov) {
                prim = stri.substring(oldI, i);
                risu = risu + prim + daMett;
                oldI = i + 1;
            }
            i++;
        }
        risu = risu + stri.substring(oldI, leng);

        return risu;
    }

    String aa(String stringa) {
        //aggiunge apice al singolo
        //String temp = replaceChars(stringa, '\'', "''");
        //return replaceChars(temp, '\\', "\\\\");
        stringa = StringUtils.replace(stringa, "\\", "\\\\");
        stringa = StringUtils.replace(stringa, "'", "\\'");
        return stringa;
    }

    String pc(String campo, String tipoCampo) {
        //prepara il campo per sql
        //debug
        //javax.swing.JOptionPane.showMessageDialog(null,"campo:"+campo+" tipo:"+tipoCampo);
        if (campo == null) {
            campo = "";
        }
        if (tipoCampo.equalsIgnoreCase("LONG")) {
            if (campo.length() == 0) {
                return ("0");
            } else {
                return (campo);
            }
        } else if (tipoCampo.equalsIgnoreCase("DECIMAL")) {
            if (campo.length() == 0) {
                return ("0");
            } else {
                return ("(" + campo + ")");
            }
        } else if (tipoCampo.equalsIgnoreCase("VARCHAR")) {
            return ("'" + aa(campo) + "'");
        } else if (tipoCampo.equalsIgnoreCase("CHAR")) {
            return ("'" + aa(campo) + "'");
        } else if (tipoCampo.equalsIgnoreCase("DATE")) {
            if (campo.equalsIgnoreCase("null")) {
                return "null";
            } else {
                return ("'" + campo + "'");
            }
        } else {
            return ("'" + aa(campo) + "'");
        }
    }

    String spezza(String stringa, int ogni) {
        String temp = "";
        for (int i = 0; i < stringa.length(); i = i + ogni) {
            if ((i + ogni) > stringa.length()) {
                temp += stringa.substring(i, stringa.length()) + "\n";
            } else {
                temp += stringa.substring(i, i + ogni) + "\n";
            }
        }
        return (temp);
    }

    private String nz(String valore, String seNullo) {
        if (valore == null) {
            return (seNullo);
        }
        return (valore);
    }

    private String nz(Object valore, String seNullo) {
        if (valore == null) {
            return (seNullo);
        }
        return (valore.toString());
    }

    private void caricaValoriChiave() {
        //controllo che se non sono caricati alcuni valori allora iniziliazzo la hash
        try {
            if (this.dbChiave != null) {
                if (this.dbChiaveValori == null) {
                    dbChiaveValori = new Hashtable();
                }
                if (!resu.isBeforeFirst()) {
                    for (int i = 0; i < dbChiave.size(); i++) {
                        try {
                            dbChiaveValori.put(dbChiave.get(i), resu.getString(String.valueOf(dbChiave.get(i))));
                        } catch (Exception err) {
                            if (debug) {
                                System.out.println("caricaValoriChiave:non trovato campo");
                            }
                            err.printStackTrace();
                        }
                    }
                }
                //debug
                //System.out.println(dbChiaveValori.toString());
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public Hashtable getCampiAggiuntivi() {
        return campiAggiuntivi;
    }

    public void setCampiAggiuntivi(Hashtable campiAggiuntivi) {
        this.campiAggiuntivi = campiAggiuntivi;
    }

    void checkResu() throws SQLException {
        //controllo resu
        boolean closed = false;
        if (resu != null) {
            try {
                resu.getRow();
            } catch (SQLException sqle) {
                closed = true;
            }
        } else {
            closed = true;
        }
        if (closed) {
            if (db != null) {
                stat = db.getDbConn().createStatement();
            } else {
                stat = oldConn.createStatement();
            }
            resu = stat.executeQuery(oldSql);
            resu.next();
        }
    }

    private int getComponentCount2() {
        int count = getComponentCount();
        if (altriCampi != null) {
            count += altriCampi.size();
        }
        if (dbPanelsCollegati != null) {
            for (tnxDbPanel altro : dbPanelsCollegati) {
                count += altro.getComponentCount();
            }
        }
        return count;
    }

    private ArrayList getComponents2() {
        ArrayList temp = new ArrayList();
        for (Component c : getComponents()) {
            temp.add(c);
        }
        if (altriCampi != null) {
            temp.addAll(altriCampi);
        }

        if (dbPanelsCollegati != null) {
            for (tnxDbPanel altro : dbPanelsCollegati) {
                for (Component c : altro.getComponents()) {
                    temp.add(c);
                }
            }
        }

        return temp;
    }

    private Component getComponent2(int index) {
        ArrayList temp = getComponents2();
        return (Component) temp.get(index);
    }

    public void addCampoAggiuntivo(Component comp) {
        if (altriCampi == null) {
            altriCampi = new ArrayList();
        }
        altriCampi.add(comp);
    }

    private String convTo(String nz) {
        return StringUtils.replace(nz, "\n", "[invio]");
    }

    private String convFrom(String nz) {
        return StringUtils.replace(nz, "[invio]", "\n");
    }

    public ArrayList<tnxDbPanel> getDbPanelsCollegati() {
        return dbPanelsCollegati;
    }

    public void setDbPanelsCollegati(ArrayList<tnxDbPanel> dbPanelsCollegati) {
        this.dbPanelsCollegati = dbPanelsCollegati;
    }

    public tnxDbPanel getParentPanel() {
        return parentPanel;
    }

    public void setParentPanel(tnxDbPanel parentPanel) {
        this.parentPanel = parentPanel;
    }

    public void aggiungiDbPanelCollegato(tnxDbPanel panel) {
        if (dbPanelsCollegati == null) {
            dbPanelsCollegati = new ArrayList<tnxDbPanel>();
        }
        dbPanelsCollegati.add(panel);
        panel.setParentPanel(this);
    }

    private void showExc(String intestazione, Exception err, String sql) {
        try {
            try {
                getToolkit().getSystemClipboard().setContents(new StringSelection(sql), null);
            } catch (Exception e) {
            }
            Class cl = Class.forName("it.tnx.invoicex.gui.JDialogExc");
            Method m = cl.getMethod("showExc", Frame.class, boolean.class, Exception.class, String.class);
            m.invoke(null, getTopLevelAncestor(), true, err, intestazione);
        } catch (Exception e) {
            e.printStackTrace();
            SwingUtils.showErrorMessage(getTopLevelAncestor(), intestazione);
        }
    }

    public void setLang(List<String> lang) {
        this.lang = lang;
    }

    public List<String> getLang() {
        return lang;
    }

    private String getCondChiave(Hashtable valori) {
        Set s = valori.keySet();
        String sql = "";
        sql += DbUtils.prepareSqlFromMap((Map) valori, " and ");
        return sql;
    }

    public void dbUndo() {
        dbCambiaStato(DB_LETTURA);
        dbRefresh();
    }
    
    public int getColumnId(String campo) {
        boolean trovato = false;
        int idCampo = 0;
        try {
            for (int j = 0; j < meta.getColumnCount(); j++) {
                if (meta.getColumnName(j + 1).equalsIgnoreCase(campo)) {
                    trovato = true;
                    idCampo = j + 1;
                    break;
                }
            }
            if (trovato) {
                return idCampo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }
}
