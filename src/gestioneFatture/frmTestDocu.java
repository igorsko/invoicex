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
 * frmElenPrev.java
 *
 * Created on 23 novembre 2001, 14.54
 */
package gestioneFatture;

import gestioneFatture.chiantiCashmere.animali.*;

import gestioneFatture.logic.documenti.*;
import it.tnx.commons.AutoCompletionEditable;
import it.tnx.commons.CastUtils;


import it.tnx.commons.DbUtils;
import it.tnx.commons.FormatUtils;
import it.tnx.commons.FxUtils;
import it.tnx.commons.SwingUtils;
import it.tnx.commons.cu;
import it.tnx.commons.table.EditorUtils;
import it.tnx.invoicex.InvoicexUtil;
import it.tnx.invoicex.MyAbstractListIntelliHints;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.beans.PropertyVetoException;
import java.io.File;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicArrowButton;
import org.apache.commons.lang.StringUtils;
import tnxbeans.tnxDbGrid;
import tnxbeans.tnxDbPanel;
import tnxbeans.tnxTextField;

/**
 *
 *
 *
 * @author  marco
 *
 */
public class frmTestDocu
        extends javax.swing.JInternalFrame
        implements InterfaceAnimale, GenericFrmTest {

    public dbDocumento prev = new dbDocumento();
    private Documento doc = new Documento();
    public frmElenDDT from;
    private Db db = Db.INSTANCE;
    int pWidth;
    int pHeight;
    public String dbStato = "L";
    public static String DB_INSERIMENTO = "I";
    public static String DB_MODIFICA = "M";
    public static String DB_LETTURA = "L";
    private String sql = "";
    private String old_id = "";
    private boolean id_modificato = false;
    private String old_anno = "";
    private String old_data = "";
    private boolean anno_modificato = false;
    private int comClieSel_old = -1;
    private int comClieDest_old = -1;
    private double totaleIniziale;
    private String serie_originale = null;
    private Integer numero_originale = null;
    private Integer anno_originale = null;
    boolean acquisto = false;
    public String suff = "";
    private String ccliente = "cliente";
    private boolean loading = true;
    public Integer id = null;
    private boolean block_aggiornareProvvigioni;
    
    MyAbstractListIntelliHints al_clifor = null;
    AtomicReference<ClienteHint> clifor_selezionato_ref = new AtomicReference(null);
    

    public frmTestDocu(String dbStato, String dbSerie, int dbNumero, String prevStato, int dbAnno) {
        this(dbStato, dbSerie, dbNumero, prevStato, dbAnno, -1);
    }

    public frmTestDocu(String dbStato, String dbSerie, int dbNumero, String prevStato, int dbAnno, int dbIdDocu) {
        this(dbStato, dbSerie, dbNumero, prevStato, dbAnno, dbIdDocu, false);
    }

    public frmTestDocu(String dbStato, String dbSerie, int dbNumero, String prevStato, int dbAnno, int dbIdDocu, boolean acquisto) {

        loading = true;
        this.id = dbIdDocu;
        this.acquisto = acquisto;

//        int permesso = Permesso.PERMESSO_DDT_VENDITA;
        if (acquisto) {
            suff = "_acquisto";
            ccliente = "fornitore";
            prev.acquisto = true;
//            permesso = Permesso.PERMESSO_DDT_VENDITA;
        } else {
        }


        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        this.dbStato = dbStato;

        ToolTipManager ttm = ToolTipManager.sharedInstance();
        ttm.setDismissDelay(30000);

        initComponents();
        
        AutoCompletionEditable.enable(comAspettoEsterioreBeni);
        AutoCompletionEditable.enable(comCausaleTrasporto);
        AutoCompletionEditable.enable(comVettori);
        AutoCompletionEditable.enable(comMezzoTrasporto);
        AutoCompletionEditable.enable(comPorto);

//        if(!main.utente.getPermesso(permesso, Permesso.PERMESSO_TIPO_SCRITTURA)){
//            SwingUtils.showErrorMessage(main.getPadrePanel(), "Non hai i permessi per accedere a questa funzionalità", "Impossibile accedere", true);
//            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//            this.setVisible(false);
//            this.dispose();
//            return;
//        }
        InvoicexUtil.macButtonSmall(butPrezziPrec);

        prezzi_ivati.setVisible(false);

        comClie.putClientProperty("JComponent.sizeVariant", "small");
        comClieDest.putClientProperty("JComponent.sizeVariant", "small");
        comAgente.putClientProperty("JComponent.sizeVariant", "small");
        comPaga.putClientProperty("JComponent.sizeVariant", "small");
        stato_evasione.putClientProperty("JComponent.sizeVariant", "small");

        comCausaleTrasporto.putClientProperty("JComponent.sizeVariant", "mini");
        comAspettoEsterioreBeni.putClientProperty("JComponent.sizeVariant", "mini");
        comVettori.putClientProperty("JComponent.sizeVariant", "mini");
        comMezzoTrasporto.putClientProperty("JComponent.sizeVariant", "mini");
        comMezzoTrasporto.putClientProperty("JComponent.sizeVariant", "mini");
        comPorto.putClientProperty("JComponent.sizeVariant", "mini");
        comPaese.putClientProperty("JComponent.sizeVariant", "mini");

        if (main.getPersonalContain("litri")) {
            butNuovArti1.setText("Inserisci Tot. Litri");
        }
//        texNote.getJTextArea().getDocument().addDocumentListener(new DocumentListener() {
//
//            public void insertUpdate(DocumentEvent e) {
//                Thread.dumpStack();
//                System.err.println("!!! rinsert");
//            }
//
//            public void removeUpdate(DocumentEvent e) {
//                Thread.dumpStack();
//                System.err.println("!!! remove");
//            }
//
//            public void changedUpdate(DocumentEvent e) {
//                Thread.dumpStack();
//                System.err.println("!!! change");
//            }
//        });

        texNote.setFont(texSeri.getFont());

        texCliente.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                texCliente.selectAll();
            }
        });
        
        al_clifor = InvoicexUtil.getCliforIntelliHints(texCliente, this, clifor_selezionato_ref, null, comClieDest);
        al_clifor.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("selezionato")) {
                    ClienteHint hint = (ClienteHint)clifor_selezionato_ref.get();
                    if (hint != null) {
                        texClie.setText(hint.codice);
                    } else {
                        texClie.setText("");
                    }
                    comClie.dbTrovaKey(texClie.getText());
                    selezionaCliente();
                }
            }
        });

        if (main.getPersonalContain("carburante")) {
            jLabel114.setText("spese carburante");
        }

        griglia.setNoTnxResize(true);
        griglia.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        if (acquisto) {
            stato_evasione.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Fatturato", "Fatturato Parzialmente", "Non Fatturato"}));
        } else {
            stato_evasione.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Fatturato", "Fatturato Parzialmente", "Non Fatturato"}));
        }
        stato_evasione.setSelectedIndex(2);
        
        if (acquisto) {
            setTitle("DDT di Acquisto");
            jLabel151.setText("fornitore");
            texClie.setDbNomeCampo(ccliente);
            texClieDest.setDbNomeCampo(ccliente + "_destinazione");
            labRiferimento.setText("Rif. Forn.");

            stampa_prezzi.setVisible(false);

            labAgente.setVisible(false);
            comAgente.setVisible(false);

            labProvvigione.setVisible(false);
            labPercentoProvvigione.setVisible(false);
            texProvvigione.setVisible(false);

            jLabel15.setVisible(false);
            comClieDest.setVisible(false);

            labScon10.setVisible(false);
            labScon11.setVisible(false);
            labScon12.setVisible(false);
            labScon13.setVisible(false);
            labScon14.setVisible(false);
            labScon16.setVisible(false);
            labScon15.setVisible(false);
            labScon17.setVisible(false);
            texDestRagioneSociale.setVisible(false);
            texDestIndirizzo.setVisible(false);
            texDestCap.setVisible(false);
            texDestLocalita.setVisible(false);
            texDestProvincia.setVisible(false);
            texDestTelefono.setVisible(false);
            texDestCellulare.setVisible(false);
            comPaese.setVisible(false);
            jPanel6.setVisible(false);

//            dati_altri2.remove(texForni);
//            dati_altri2.remove(comForni);
//
//            dati_altri1.setVisible(false);
//            dati_altri2.setVisible(false);
//
//            jLabel17.setText("Rif. Forn.");
//            jLabel17.setToolTipText("Inserire il numero dell'ordine assegnato dal fornitore");

//            jLabel151.setPreferredSize(new Dimension((int)jLabel151.getPreferredSize().getWidth()+150, (int)jLabel151.getPreferredSize().getHeight()));

//            jSplitPane1.setDividerLocation(170);
        } else {
            setTitle("DDT di Vendita");
        }

        if (main.getPersonalContain(main.PERSONAL_CHIANTICASHMERE_ANIMALI)) {
            this.butNuovoAnimale.setVisible(true);
        } else {
            this.butNuovoAnimale.setVisible(false);
        }

        //init campi particolari
        this.texData.setDbDefault(texData.DEFAULT_CURRENT);
        
        try {
            InvoicexEvent event = new InvoicexEvent(this);
            event.type = InvoicexEvent.TYPE_FRMTESTDDT_CONSTR_POST_INIT_COMPS;
            main.events.fireInvoicexEvent(event);
        } catch (Exception err) {
            err.printStackTrace();
        }

        //oggetto preventivo
        this.prev.dbStato = dbStato;
        this.prev.serie = dbSerie;
        this.prev.numero = dbNumero;
        this.prev.stato = prevStato;
        this.prev.anno = dbAnno;
        this.prev.texTota = this.texTota;
        this.prev.texTotaImpo = this.texTotaImpo;
        this.prev.texTotaIva = this.texTotaIva;
        this.prev.tipoDocumento = getTipoDoc();
//        this.setClosable(false);

        //faccio copia in caso di annulla deve rimettere le righe giuste
        if (dbStato.equals(this.DB_MODIFICA)) {
            //controllo tabella temp
            String sql = "check table righ_ddt" + suff + "_temp";
            try {
                DbUtils.dumpResultSet(Db.getConn(), sql);
                ResultSet r = Db.openResultSet(sql);
                if (r.next()) {
                    if (!r.getString("Msg_text").equalsIgnoreCase("OK")) {
                        SwingUtils.showErrorMessage(main.getPadre(), "Errore durante il controllo della tabella temporanea [1]");
                    }
                } else {
                    SwingUtils.showErrorMessage(main.getPadre(), "Errore durante il controllo della tabella temporanea [2]");
                }
            } catch (Exception e) {
                SwingUtils.showErrorMessage(main.getPadre(), "Errore durante il controllo della tabella temporanea\n" + e.toString());
            }

            //tolgo le righe da temp che tanto non serbono +
            sql = "delete from righ_ddt" + suff + "_temp";
            sql += " where serie " + Db.pcW(this.prev.serie, "VARCHAR");
            sql += " and numero " + Db.pcW(String.valueOf(this.prev.numero), "NUMBER");
            //sql += " and stato " + Db.pcW(this.prev.stato, "VARCHAR");
            sql += " and anno " + Db.pcW(String.valueOf(this.prev.anno), "VARCHAR");
            sql += " and username = '" + main.login + "'";
            Db.executeSqlDialogExc(sql, true);

            sql = "delete te.* from righ_ddt" + suff + "_temp te join righ_ddt" + suff + " ri on te.id = ri.id";
            sql += " and ri.serie " + Db.pcW(this.prev.serie, "VARCHAR");
            sql += " and ri.numero " + Db.pcW(String.valueOf(this.prev.numero), "NUMBER");
            sql += " and ri.anno " + Db.pcW(String.valueOf(this.prev.anno), "VARCHAR");
            sql += " and te.username = '" + main.login + "'";
            Db.executeSqlDialogExc(sql, true);

            //e inserisco
            sql = "insert into righ_ddt" + suff + "_temp";
            sql += " select *, '" + main.login + "' as username";
            sql += " from righ_ddt" + suff + "";
            sql += " where serie = " + db.pc(dbSerie, "VARCHAR");
            sql += " and numero = " + dbNumero;
            //sql += " and stato = " + db.pc(prevStato, "VARCHAR");
            sql += " and anno = " + dbAnno;
            Db.executeSqlDialogExc(sql, true);

            //memorizzo il numero doc originale
            serie_originale = dbSerie;
            numero_originale = dbNumero;
            anno_originale = dbAnno;
        }

        //this.texSeri.setVisible(false);
        //associo il panel ai dati
        this.dati.dbNomeTabella = "test_ddt" + suff;
        
        dati.dbChiaveAutoInc = true;

        dati.messaggio_nuovo_manuale = true;

//        Vector chiave = new Vector();
//        chiave.add("serie");
//        chiave.add("numero");
//        chiave.add("anno");
//        this.dati.dbChiave = chiave;
        Vector chiave = new Vector();
        chiave.add("id");
        dati.dbChiave = chiave;
        

        dati.aggiungiDbPanelCollegato(datiOpzioni);

        //this.dati.butSave = this.butSave;
        //this.dati.butUndo = this.butUndo;
        //controllo se inserimento o modifica
        if (dbStato.equals(this.DB_INSERIMENTO)) {
            this.dati.dbOpen(db.getConn(), "select * from test_ddt" + suff + " limit 0");
        } else {
            sql = "select * from test_ddt" + suff;
//            sql += " where serie = " + db.pc(dbSerie, "VARCHAR");
//            sql += " and numero = " + dbNumero;
//            //sql += " and stato = " + db.pc(prevStato, "VARCHAR");
//            sql += " and anno = " + dbAnno;
            sql += " where id = " + id;
            this.dati.dbOpen(db.getConn(), sql);
        }

        //apro la combo pagamenti
        this.comPaga.dbAddElement("", "");
        this.comPaga.dbOpenList(db.getConn(), "select codice, codice from pagamenti order by codice", null, false);

        comPaese.dbAddElement("", "");
        comPaese.dbOpenList(Db.getConn(), "select nome, codice1 from stati", null, false);

        comPorto.dbAddElement("", "");
        comPorto.dbOpenList(Db.getConn(), "select porto, id from tipi_porto group by porto");

        //mezzo di trasporto

        comMezzoTrasporto.dbAddElement("");
        comMezzoTrasporto.dbAddElement("DESTINATARIO");
        comMezzoTrasporto.dbAddElement("MITTENTE");

        //106
        comMezzoTrasporto.dbAddElement("VETTORE");

        //carico causali trasporto
        comCausaleTrasporto.dbAddElement("");
//        comCausaleTrasporto.dbOpenList(Db.getConn(), "select nome, id from tipi_causali_trasporto order by nome");
        comCausaleTrasporto.dbOpenList(Db.getConn(), "select nome, id from tipi_causali_trasporto group by nome", null, false);

        //105 carico aspetti esteriori beni per gianni
//        comAspettoEsterioreBeni.dbAddElement("SCATOLA");
//        comAspettoEsterioreBeni.dbAddElement("A VISTA");
//        comAspettoEsterioreBeni.dbAddElement("SCATOLA IN PANCALE");
//        if (main.getPersonal().equals(main.PERSONAL_CUCINAIN)) {
//            comAspettoEsterioreBeni.dbAddElement("BUSTA");
//            if (dbStato.equals(this.DB_INSERIMENTO)) {
//                comAspettoEsterioreBeni.setText("BUSTA");
//            }
//        } else {
//            comAspettoEsterioreBeni.dbAddElement("");
//        }
        comAspettoEsterioreBeni.dbAddElement("");
        comAspettoEsterioreBeni.dbOpenList(Db.getConn(), "select nome, id from tipi_aspetto_esteriore_beni group by nome", null, false);

        comVettori.dbAddElement("");
        comVettori.dbOpenList(db.getConn(), "select nome,nome from vettori order by nome", null, false);

        comAgente.dbOpenList(Db.getConn(), "select nome, id from agenti where id != 0 and IFNULL(nome,'') != '' order by nome", null, false);

        listino_consigliato.dbAddElement("", "");
        listino_consigliato.dbOpenList(Db.getConn(), "select CONCAT(descrizione, ' [', codice, ']'), codice from tipi_listino order by descrizione");
        this.dati.dbRefresh();
        this.prev.dbRefresh();

        //apro la combo clienti
        this.comClie.setDbTextAbbinato(this.texClie);
        this.texClie.setDbComboAbbinata(this.comClie);

//        if (Db.nz(this.comCausaleTrasporto.getSelectedItem(), "").toString().equalsIgnoreCase("TENTATA VENDITA")) {
//            this.comClie.dbOpenList(db.getConn(), "select ragione_sociale,codice from clie_forn order by ragione_sociale", null, false);
//            this.comClie.setEnabled(false);
//        } else {
        if (this.texClie.getText().equalsIgnoreCase("0")) {
            this.comClie.dbOpenList(db.getConn(), "select ragione_sociale,codice from clie_forn where ragione_sociale != '' order by ragione_sociale", null, true);
        } else {
            this.comClie.dbOpenList(db.getConn(), "select ragione_sociale,codice from clie_forn where ragione_sociale != '' order by ragione_sociale", this.texClie.getText(), true);
        }
//        }

        //apro combo destinazione cliente
        comClieDest.dbTrovaMentreScrive = false;
        sql = "select ragione_sociale,codice from clie_forn_dest";
        sql += " where codice_cliente = " + Db.pc(this.texClie.getText(), "NUMERIC");
        sql += " order by ragione_sociale";
        riempiDestDiversa(sql);

        //righe
        //apro la griglia
        griglia.dbNomeTabella = "righ_ddt" + suff;

        java.util.Hashtable colsWidthPerc = new java.util.Hashtable();
        colsWidthPerc.put("serie", new Double(0));
        colsWidthPerc.put("numero", new Double(0));
        colsWidthPerc.put("anno", new Double(0));
        colsWidthPerc.put("stato", new Double(0));
        colsWidthPerc.put("riga", new Double(5));
        colsWidthPerc.put("articolo", new Double(15));
        colsWidthPerc.put("descrizione", new Double(45));
        colsWidthPerc.put("um", new Double(5));
        colsWidthPerc.put("quantita", new Double(10));
        colsWidthPerc.put(getCampoQtaEvasa(), new Double(10));
        colsWidthPerc.put("prezzo", new Double(12));
        colsWidthPerc.put("sconto1", new Double(0));
        colsWidthPerc.put("sconto2", new Double(0));
        colsWidthPerc.put("iva", new Double(0));
        colsWidthPerc.put("Totale", new Double(10));
        colsWidthPerc.put("Ivato", new Double(10));
        colsWidthPerc.put("Sconti", new Double(10));
        colsWidthPerc.put("id", 0d);
        colsWidthPerc.put("id_padre", 0d);
        if (main.isPluginContabilitaAttivo()) {
            colsWidthPerc.put("conto", new Double(10));
        }
        

        this.griglia.columnsSizePerc = colsWidthPerc;

        java.util.Hashtable colsAlign = new java.util.Hashtable();
        colsAlign.put("quantita", "RIGHT_CURRENCY");
        colsAlign.put("prezzo", "RIGHT_CURRENCY");
        this.griglia.columnsAlign = colsAlign;
        this.griglia.flagUsaOrdinamento = false;

        //        Vector chiave2 = new Vector();
        //        chiave2.add("serie");
        //        chiave2.add("numero");
        //        chiave2.add("anno");
        //        chiave2.add("riga");
        Vector chiave2 = new Vector();
        chiave2.add("id");
        this.griglia.dbChiave = chiave2;

        //this.griglia.dbPanel=this.dati;
        //controllo come devo aprire
        if (dbStato.equals(frmTestDocu.DB_INSERIMENTO)) {
            inserimento();
            SimpleDateFormat f1 = new SimpleDateFormat("dd/MM/yy HH:mm");
            texDataOra.setText(f1.format(new java.util.Date()));
            texData.setEditable(true);
        } else {

            //disabilito la data perch??? non tornerebbe pi??? la chiave per numero e anno
            this.texData.setEditable(false);
            this.prev.sconto1 = Db.getDouble(this.texScon1.getText());
            this.prev.sconto2 = Db.getDouble(this.texScon2.getText());
            this.prev.sconto3 = Db.getDouble(this.texScon3.getText());

            //this.prev.speseVarie = Db.getDouble(this.texSpesVari.getText());
            this.prev.speseTrasportoIva = Db.getDouble(this.texSpeseTrasporto.getText());
            this.prev.speseIncassoIva = Db.getDouble(this.texSpeseIncasso.getText());
            dopoInserimento();
        }

//        java.util.prefs.Preferences preferences = java.util.prefs.Preferences.userNodeForPackage(main.class);
//        boolean azioniPericolose = preferences.getBoolean("azioniPericolose", false);
        boolean azioniPericolose = main.fileIni.getValueBoolean("pref", "azioniPericolose", true);

        if (azioniPericolose) {
            texNumePrev.setEditable(true);
            texData.setEditable(true);
        }

        if (!dbStato.equals(DB_INSERIMENTO)) {
            if ("S".equalsIgnoreCase(String.valueOf(dati.dbGetField("evaso")))) {
                stato_evasione.setSelectedIndex(0);
            } else if ("P".equalsIgnoreCase(String.valueOf(dati.dbGetField("evaso")))) {
                stato_evasione.setSelectedIndex(1);
            } else {
                stato_evasione.setSelectedIndex(2);
            }
        } else {
            stato_evasione.setSelectedIndex(2);
        }

        texCliente.requestFocus();
        
        prezzi_ivati_virtual.setSelected(prezzi_ivati.isSelected());

        dati.dbCheckModificatiReset();

        if (dbStato.equalsIgnoreCase(DB_INSERIMENTO)) {
            texSconto.setText("0");
        } else {
            texSconto.setText(FormatUtils.formatEuroIta(CastUtils.toDouble0(dati.dbGetField("sconto"))));
            ricalcolaTotali();
        }
        
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

        loading = false;
    }

    private void inserimento() {

        //oggetto preventivo
        this.dati.dbNew();

        //prendo base da impostazioni
        boolean prezzi_ivati_b = false;
        try {
            String prezzi_ivati_s = CastUtils.toString(DbUtils.getObject(Db.getConn(), "select l.prezzi_ivati from dati_azienda a join tipi_listino l on a.listino_base = l.codice"));
            if (prezzi_ivati_s.equalsIgnoreCase("S")) {
                prezzi_ivati_b = true;
            }
            prezzi_ivati.setSelected(prezzi_ivati_b);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (main.iniSerie == false) {
            assegnaNumero();
            dopoInserimento();
            listino_consigliato.dbTrovaKey(main.fileIni.getValue("pref", "listinoConsigliatoDdt", ""));
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    comClie.grabFocus();
                }
            });
        } else {

            //disabilitare tutto prima
            Component[] cs = this.dati.getComponents();

            for (int i = 0; i < cs.length; i++) {
                cs[i].setEnabled(false);

                if (cs[i] instanceof tnxbeans.tnxComboField) {

                    tnxbeans.tnxComboField combo = (tnxbeans.tnxComboField) cs[i];
                    combo.setEditable(false);
                    combo.setLocked(true);
                }
            }
            listino_consigliato.setSelectedItem(main.fileIni.getValue("pref", "listinoConsigliatoDdt", ""));
            this.texSeri.setToolTipText("Inserisci la serie e premi Invio per confermarla ed assegnare un numero al documento");
            this.texSeri.setEnabled(true);
            this.texSeri.setEditable(true);
            this.texSeri.setBackground(java.awt.Color.RED);
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    texSeri.requestFocus();
                }
            });
        }
    }

    private String getAnno() {
        try {
            SimpleDateFormat datef = null;
            if (texData.getText().length() == 8 || texData.getText().length() == 7) {
                datef = new SimpleDateFormat("dd/MM/yy");
            } else if (texData.getText().length() == 10 || texData.getText().length() == 9) {
                datef = new SimpleDateFormat("dd/MM/yyyy");
            } else {
                return "";
            }
            Calendar cal = Calendar.getInstance();
            datef.setLenient(true);
            cal.setTime(datef.parse(texData.getText()));
            return String.valueOf(cal.get(Calendar.YEAR));
        } catch (Exception err) {
            return "";
        }
    }

    private void dopoInserimento() {
        dbAssociaGrigliaRighe();
        doc.load(Db.INSTANCE, this.prev.numero, this.prev.serie, this.prev.anno, getTipoDoc(), id);

        if (comClie.getText().trim().length() == 0) {
            try {
                String cliente = (String) DbUtils.getObject(Db.getConn(), "select ragione_sociale from clie_forn where codice = '" + texClie.getText() + "'");
                texCliente.setText(cliente);
            } catch (Exception e) {
            }
        } else {
            texCliente.setText(comClie.getText());
        }
        
        
        //provo a fare timer per aggiornare prezzo totale
        //        tim = new java.util.Timer();
        //        timerRefreshPreventivo timTest = new timerRefreshPreventivo(this, doc);
        //        tim.schedule(timTest,1000,500);
        ricalcolaTotali();

        prev.dbRefresh();
        totaleIniziale = prev.totale;
//        totaleIniziale = doc.getTotale();

        //nascondo la targa all'avvio che serve solo per i ddt di tentata venmdita
        visualizzaTarga();

        //debu cliente
        System.out.println("cliente4:" + this.texClie.getText());

    }

    private void assegnaNumero() {

        //metto ultimo numero preventivo + 1
        //apre il resultset per ultimo +1
        java.sql.Statement stat;
        ResultSet resu;

        try {
            stat = db.getConn().createStatement();

            String sql = "select numero from test_ddt" + suff;

            sql += " where anno = " + java.util.Calendar.getInstance().get(Calendar.YEAR);
            sql += " and serie = " + Db.pc(this.texSeri.getText(), Types.VARCHAR);
            sql += " order by numero desc limit 1";
            resu = stat.executeQuery(sql);

            if (resu.next() == true) {
                this.texNumePrev.setText(String.valueOf(resu.getInt(1) + 1));
            } else {
                this.texNumePrev.setText("1");
            }

            if (main.getPersonal().equals(main.PERSONAL_CUCINAIN)) {
                this.comAspettoEsterioreBeni.setSelectedItem("BUSTA");
            }

            if (main.getPersonal().equals(main.PERSONAL_GIANNI)) {
                this.texSpeseIncasso.setText("1,50");

                //105
                //this.texVettore1.setText("RINALDI s.r.l.\nVia Calabria 7-9\nLoc. Fosci, Poggibonsi (SI)");
            }

            dati.setCampiAggiuntivi(new Hashtable());
            dati.getCampiAggiuntivi().put("sconto", Db.pc(doc.getSconto(), Types.DOUBLE));
            dati.getCampiAggiuntivi().put("totale_imponibile_pre_sconto", Db.pc(doc.totaleImponibilePreSconto, Types.DOUBLE));
            dati.getCampiAggiuntivi().put("totale_ivato_pre_sconto", Db.pc(doc.totaleIvatoPreSconto, Types.DOUBLE));

            dati.getCampiAggiuntivi().put("evaso", Db.pc(getStatoEvaso(), Types.VARCHAR));

            this.texAnno.setText(String.valueOf(java.util.Calendar.getInstance().get(Calendar.YEAR)));

            //-----------------------------------------------------------------
            //se apre in inserimento gli faccio subito salvare la testa
            //se poi la annulla vado ad eliminare
            //appoggio totali
            this.texTota1.setText(this.texTota.getText());
            this.texTotaIva1.setText(this.texTotaIva.getText());
            this.texTotaImpo1.setText(this.texTotaImpo.getText());

//            texClie.setText("0");
            if (this.dati.dbStato.equals(DB_INSERIMENTO)) {
                try {
                    String tmpSerie = this.texSeri.getText();
                    Integer numero = Integer.parseInt(texNumePrev.getText());
                    Integer anno = Integer.parseInt(texAnno.getText());

                    String tmpSql = "select * from test_ddt" + suff + " where serie = '" + tmpSerie + "' and anno = " + anno + " and numero = " + numero;
                    ResultSet tmpControl = Db.openResultSet(tmpSql);

                    if (tmpControl.next()) {
                        JOptionPane.showMessageDialog(this, "Un' altro documento con lo stesso gruppo numero - serie - anno è già stato inserito!", "Impossibile inserire dati", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (this.dati.dbSave() == true) {

                //richiamo il refresh della maschera che lo ha lanciato
                if (from != null) {

                    frmElenDDT temp = (frmElenDDT) from;
                    temp.dbRefresh();
                }
            }
//            texClie.setText("");

            this.prev.serie = this.texSeri.getText();
            this.prev.stato = "P";
            this.prev.numero = new Integer(this.texNumePrev.getText()).intValue();
            this.prev.anno = java.util.Calendar.getInstance().get(Calendar.YEAR);
            this.id = (Integer) dati.last_inserted_id;
            System.out.println("*** id new : " + this.id);
            this.prev.id = id;
            
            this.dati.dbCambiaStato(this.dati.DB_LETTURA);
            try {
                texNote.setText(main.fileIni.getValue("pref", "noteStandard"));
            } catch (Exception err) {
                err.printStackTrace();
            }
            this.texSeri.setEditable(false);
            this.texSeri.setBackground(this.texNumePrev.getBackground());

            //-----------------------------------------------------------------
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    private void caricaDestinazioneDiversa() {

        String sql = "select * from clie_forn_dest";
        sql += " where codice_cliente = " + Db.pc(this.texClie.getText(), Types.INTEGER);
        sql += " and codice = " + Db.pc(this.texClieDest.getText(), Types.INTEGER);

        ResultSet dest = Db.openResultSet(sql);

        try {
            if (dest.next()) {
                texDestRagioneSociale.setText(dest.getString("ragione_sociale"));
                texDestIndirizzo.setText(dest.getString("indirizzo"));
                texDestCap.setText(dest.getString("cap"));
                texDestLocalita.setText(dest.getString("localita"));
                texDestProvincia.setText(dest.getString("provincia"));
                texDestTelefono.setText(dest.getString("telefono"));
                texDestCellulare.setText(dest.getString("cellulare"));
                comPaese.dbTrovaKey(dest.getString("paese"));
            } else {
                texDestRagioneSociale.setText("");
                texDestIndirizzo.setText("");
                texDestCap.setText("");
                texDestLocalita.setText("");
                texDestProvincia.setText("");
                texDestTelefono.setText("");
                texDestCellulare.setText("");
                comPaese.setSelectedIndex(-1);
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    /** This method is called from within the constructor to
     *
     * initialize the form.
     *
     * WARNING: Do NOT modify this code. The content of this method is
     *
     * always regenerated by the Form Editor.
     *
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popGrig = new javax.swing.JPopupMenu();
        popGrigModi = new javax.swing.JMenuItem();
        popGrigElim = new javax.swing.JMenuItem();
        popGridAdd = new javax.swing.JMenuItem();
        popDuplicaRighe = new javax.swing.JMenuItem();
        labScon7 = new javax.swing.JLabel();
        texScon7 = new tnxbeans.tnxTextField();
        labScon6 = new javax.swing.JLabel();
        labScon4 = new javax.swing.JLabel();
        texScon9 = new tnxbeans.tnxTextField();
        labScon5 = new javax.swing.JLabel();
        texScon10 = new tnxbeans.tnxTextField();
        jPanel5 = new javax.swing.JPanel();
        texScon6 = new tnxbeans.tnxTextField();
        labScon8 = new javax.swing.JLabel();
        texScon8 = new tnxbeans.tnxTextField();
        labScon3 = new javax.swing.JLabel();
        labScon9 = new javax.swing.JLabel();
        texScon5 = new tnxbeans.tnxTextField();
        texScon4 = new tnxbeans.tnxTextField();
        panDati = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        datiRighe = new tnxbeans.tnxDbPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        griglia = getGrigliaInitComp();
        jPanel1 = new javax.swing.JPanel();
        prezzi_ivati_virtual = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        butNuovArti = new javax.swing.JButton();
        butNuovoAnimale = new javax.swing.JButton();
        butNuovArti1 = new javax.swing.JButton();
        butImportRighe = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        butPdf = new javax.swing.JButton();
        butStampa = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        butUndo = new javax.swing.JButton();
        butSave = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        stato_evasione = new javax.swing.JComboBox();
        jPanel4 = new javax.swing.JPanel();
        texTotaImpo = new tnxbeans.tnxTextField();
        texTotaIva = new tnxbeans.tnxTextField();
        texTota = new tnxbeans.tnxTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        texSconto = new tnxbeans.tnxTextField();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        dati = new tnxbeans.tnxDbPanel();
        texNumePrev = new tnxbeans.tnxTextField();
        texClie = new tnxbeans.tnxTextField();
        texClie.setVisible(false);
        texSpeseIncasso = new tnxbeans.tnxTextField();
        texScon2 = new tnxbeans.tnxTextField();
        texScon1 = new tnxbeans.tnxTextField();
        comClie = new tnxbeans.tnxComboField();
        comClie.setVisible(false);
        texTotaImpo1 = new tnxbeans.tnxTextField();
        texTotaImpo1.setVisible(false);
        texTotaIva1 = new tnxbeans.tnxTextField();
        texTotaIva1.setVisible(false);
        texTota1 = new tnxbeans.tnxTextField();
        texTota1.setVisible(false);
        texNote = new tnxbeans.tnxMemoField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        labScon1 = new javax.swing.JLabel();
        labScon2 = new javax.swing.JLabel();
        jLabel113 = new javax.swing.JLabel();
        texData = new tnxbeans.tnxTextField();
        jLabel11 = new javax.swing.JLabel();
        texStat = new tnxbeans.tnxTextField();
        texStat.setVisible(false);
        jLabel12 = new javax.swing.JLabel();
        texScon3 = new tnxbeans.tnxTextField();
        labScon21 = new javax.swing.JLabel();
        jLabel151 = new javax.swing.JLabel();
        comClieDest = new tnxbeans.tnxComboField();
        texSeri = new tnxbeans.tnxTextField();
        texAnno = new tnxbeans.tnxTextField();
        texAnno.setVisible(false);
        texClieDest = new tnxbeans.tnxTextField();
        texClieDest.setVisible(false);
        jLabel18 = new javax.swing.JLabel();
        texNumeroColli = new tnxbeans.tnxTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        comPorto = new tnxbeans.tnxComboField();
        jLabel4 = new javax.swing.JLabel();
        comCausaleTrasporto = new tnxbeans.tnxComboField();
        texSpeseTrasporto = new tnxbeans.tnxTextField();
        jLabel114 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        comMezzoTrasporto = new tnxbeans.tnxComboField();
        labScon10 = new javax.swing.JLabel();
        texDestRagioneSociale = new tnxbeans.tnxTextField();
        labScon11 = new javax.swing.JLabel();
        texDestIndirizzo = new tnxbeans.tnxTextField();
        labScon12 = new javax.swing.JLabel();
        texRiferimento = new tnxbeans.tnxTextField();
        texDestCap = new tnxbeans.tnxTextField();
        texDestLocalita = new tnxbeans.tnxTextField();
        labScon13 = new javax.swing.JLabel();
        texDestProvincia = new tnxbeans.tnxTextField();
        labScon14 = new javax.swing.JLabel();
        texDestCellulare = new tnxbeans.tnxTextField();
        labScon15 = new javax.swing.JLabel();
        texDestTelefono = new tnxbeans.tnxTextField();
        labScon16 = new javax.swing.JLabel();
        labScon17 = new javax.swing.JLabel();
        comPaese = new tnxbeans.tnxComboField();
        jPanel6 = new javax.swing.JPanel();
        butPrezziPrec = new javax.swing.JButton();
        comAspettoEsterioreBeni = new tnxbeans.tnxComboField();
        jLabel19 = new javax.swing.JLabel();
        comPaga = new tnxbeans.tnxComboField();
        comVettori = new tnxbeans.tnxComboField();
        texDataOra = new tnxbeans.tnxTextField();
        jLabel6 = new javax.swing.JLabel();
        stampa_prezzi = new tnxbeans.tnxCheckBox();
        labAgente = new javax.swing.JLabel();
        comAgente = new tnxbeans.tnxComboField();
        labProvvigione = new javax.swing.JLabel();
        texProvvigione = new tnxbeans.tnxTextField();
        labPercentoProvvigione = new javax.swing.JLabel();
        labRiferimento = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        texNumeroColli1 = new tnxbeans.tnxTextField();
        texNumeroColli2 = new tnxbeans.tnxTextField();
        jLabel24 = new javax.swing.JLabel();
        prezzi_ivati = new tnxbeans.tnxCheckBox();
        texCliente = new javax.swing.JTextField();
        apriclienti = new BasicArrowButton(BasicArrowButton.SOUTH, UIManager.getColor("ComboBox.buttonBackground"), UIManager.getColor("ComboBox.buttonShadow"), UIManager.getColor("ComboBox.buttonDarkShadow"), UIManager.getColor("ComboBox.buttonHighlight"));
        butAddClie = new javax.swing.JButton();
        datiOpzioni = new tnxbeans.tnxDbPanel();
        jLabel8 = new javax.swing.JLabel();
        listino_consigliato = new tnxbeans.tnxComboField();

        popGrigModi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/apps/accessories-text-editor.png"))); // NOI18N
        popGrigModi.setText("modifica riga");
        popGrigModi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popGrigModiActionPerformed(evt);
            }
        });
        popGrig.add(popGrigModi);

        popGrigElim.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/places/user-trash.png"))); // NOI18N
        popGrigElim.setText("elimina");
        popGrigElim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popGrigElimActionPerformed(evt);
            }
        });
        popGrig.add(popGrigElim);

        popGridAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/list-add.png"))); // NOI18N
        popGridAdd.setLabel("Inserisci nuova riga fra");
        popGridAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popGridAddActionPerformed(evt);
            }
        });
        popGrig.add(popGridAdd);

        popDuplicaRighe.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/edit-copy.png"))); // NOI18N
        popDuplicaRighe.setText("Duplica");
        popDuplicaRighe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popDuplicaRigheActionPerformed(evt);
            }
        });
        popGrig.add(popDuplicaRighe);

        labScon7.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        labScon7.setText("prov.");
        labScon7.setToolTipText("");

        texScon7.setToolTipText("");
        texScon7.setDbDescCampo("");
        texScon7.setDbNomeCampo("dest_localita");
        texScon7.setDbTipoCampo("");
        texScon7.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N

        labScon6.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        labScon6.setText("loc.");
        labScon6.setToolTipText("");

        labScon4.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        labScon4.setText("indirizzo");
        labScon4.setToolTipText("");

        texScon9.setToolTipText("");
        texScon9.setDbDescCampo("");
        texScon9.setDbNomeCampo("dest_telefono");
        texScon9.setDbTipoCampo("");
        texScon9.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N

        labScon5.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        labScon5.setText("cap");
        labScon5.setToolTipText("");

        texScon10.setToolTipText("");
        texScon10.setDbDescCampo("");
        texScon10.setDbNomeCampo("dest_cellulare");
        texScon10.setDbTipoCampo("");
        texScon10.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N

        jPanel5.setBackground(new java.awt.Color(204, 204, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "destinazione diversa (manuale)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        texScon6.setToolTipText("");
        texScon6.setDbDescCampo("");
        texScon6.setDbNomeCampo("dest_cap");
        texScon6.setDbTipoCampo("");
        texScon6.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N

        labScon8.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        labScon8.setText("telefono");
        labScon8.setToolTipText("");

        texScon8.setToolTipText("");
        texScon8.setDbDescCampo("");
        texScon8.setDbNomeCampo("dest_provincia");
        texScon8.setDbTipoCampo("");
        texScon8.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N

        labScon3.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        labScon3.setText("ragione sociale");
        labScon3.setToolTipText("");

        labScon9.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        labScon9.setText("cellulare");
        labScon9.setToolTipText("");

        texScon5.setToolTipText("");
        texScon5.setDbDescCampo("");
        texScon5.setDbNomeCampo("dest_indirizzo");
        texScon5.setDbTipoCampo("");
        texScon5.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N

        texScon4.setToolTipText("");
        texScon4.setDbDescCampo("");
        texScon4.setDbNomeCampo("dest_ragione_sociale");
        texScon4.setDbTipoCampo("");
        texScon4.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Documento");
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosed(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
        });
        addVetoableChangeListener(new java.beans.VetoableChangeListener() {
            public void vetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {
                formVetoableChange(evt);
            }
        });

        panDati.setLayout(new java.awt.BorderLayout());

        jSplitPane1.setBorder(null);
        jSplitPane1.setDividerLocation(343);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setMinimumSize(new java.awt.Dimension(508, 200));

        datiRighe.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBorder(null);

        griglia.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                grigliaMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                grigliaMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                grigliaMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(griglia);

        datiRighe.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        prezzi_ivati_virtual.setText("Prezzi IVA inclusa");
        prezzi_ivati_virtual.setToolTipText("<html>\nSelezionando questa opzione verrà effettuato lo scorporo IVA soltanto a fine documento e non riga per riga, inoltre<br>\nverranno presentati in stampa gli importi di riga già ivati invece che gli imponibili.<br>\n<br>\nL'esempio più lampante è questo:<br>\n<br>\nArticolo di prezzo <b>10,00</b> € (iva inclusa del 21%)<br>\n- Senza la scelta 'Prezzi IVA inclusa' il totale fattura verrà <b>9,99</b> € perchè:<br>\nlo scorporo di 10,00 € genera un imponibile di 8,26 il quale applicando l'iva 21% (1,73 €) genererà un totale di 9,99 €<br>\n- Con la scelta 'Prezzi IVA inclusa' il totale fattura verrà direttamente <b>10,00</b> € e verrà calcolato l'imponibile facendo la<br>\nsottrazione tra il totale e l'iva derivante dallo scorporo del totale già ivato.<br>\n</html>");
        prezzi_ivati_virtual.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        prezzi_ivati_virtual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prezzi_ivati_virtualActionPerformed(evt);
            }
        });
        jPanel1.add(prezzi_ivati_virtual);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator1.setPreferredSize(new java.awt.Dimension(6, 20));
        jPanel1.add(jSeparator1);

        butNuovArti.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/document-new.png"))); // NOI18N
        butNuovArti.setText("Inserisci nuova riga");
        butNuovArti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butNuovArtiActionPerformed(evt);
            }
        });
        jPanel1.add(butNuovArti);

        butNuovoAnimale.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/document-new.png"))); // NOI18N
        butNuovoAnimale.setText("Inserisci Animale");
        butNuovoAnimale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butNuovoAnimaleActionPerformed(evt);
            }
        });
        jPanel1.add(butNuovoAnimale);

        butNuovArti1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/document-new.png"))); // NOI18N
        butNuovArti1.setText("Inserisci Peso");
        butNuovArti1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butNuovArti1ActionPerformed(evt);
            }
        });
        jPanel1.add(butNuovArti1);

        butImportRighe.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/document-new.png"))); // NOI18N
        butImportRighe.setText("Importa Righe Da CSV");
        butImportRighe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butImportRigheActionPerformed(evt);
            }
        });
        jPanel1.add(butImportRighe);

        datiRighe.add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel2.setPreferredSize(new java.awt.Dimension(314, 105));
        jPanel2.setLayout(new java.awt.GridLayout(1, 0));

        jPanel3.setPreferredSize(new java.awt.Dimension(157, 80));
        jPanel3.setLayout(new java.awt.GridLayout(0, 1));

        butPdf.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/pdf-icon-16.png"))); // NOI18N
        butPdf.setText("Crea PDF");
        butPdf.setMargin(new java.awt.Insets(2, 4, 2, 4));
        butPdf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butPdfActionPerformed(evt);
            }
        });
        jPanel7.add(butPdf);

        butStampa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/document-print.png"))); // NOI18N
        butStampa.setText("Stampa");
        butStampa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butStampaActionPerformed(evt);
            }
        });
        jPanel7.add(butStampa);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator2.setPreferredSize(new java.awt.Dimension(4, 20));
        jPanel7.add(jSeparator2);

        butUndo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/edit-undo.png"))); // NOI18N
        butUndo.setText("Annulla");
        butUndo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butUndoActionPerformed(evt);
            }
        });
        jPanel7.add(butUndo);

        butSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/devices/media-floppy.png"))); // NOI18N
        butSave.setText("Salva");
        butSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSaveActionPerformed(evt);
            }
        });
        jPanel7.add(butSave);

        jPanel3.add(jPanel7);

        jLabel7.setText("Stato");
        jPanel8.add(jLabel7);

        stato_evasione.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Evaso", "Evaso Parziale", "Non Evaso" }));
        stato_evasione.setSelectedIndex(2);
        jPanel8.add(stato_evasione);

        jPanel3.add(jPanel8);

        jPanel2.add(jPanel3);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel4.setLayout(null);

        texTotaImpo.setBorder(null);
        texTotaImpo.setEditable(false);
        texTotaImpo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        texTotaImpo.setText("0");
        texTotaImpo.setDbTipoCampo("valuta");
        jPanel4.add(texTotaImpo);
        texTotaImpo.setBounds(235, 31, 75, 20);

        texTotaIva.setBorder(null);
        texTotaIva.setEditable(false);
        texTotaIva.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        texTotaIva.setText("0");
        texTotaIva.setDbTipoCampo("valuta");
        jPanel4.add(texTotaIva);
        texTotaIva.setBounds(235, 54, 75, 20);

        texTota.setBorder(null);
        texTota.setEditable(false);
        texTota.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        texTota.setText("0");
        texTota.setDbTipoCampo("valuta");
        texTota.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jPanel4.add(texTota);
        texTota.setBounds(235, 77, 75, 20);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Totale");
        jPanel4.add(jLabel2);
        jLabel2.setBounds(80, 77, 150, 20);

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel21.setText("Totale Iva");
        jPanel4.add(jLabel21);
        jLabel21.setBounds(80, 54, 150, 20);

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel22.setText("Totale Imponibile");
        jPanel4.add(jLabel22);
        jLabel22.setBounds(80, 31, 150, 20);

        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel26.setText("Sconto");
        jPanel4.add(jLabel26);
        jLabel26.setBounds(80, 10, 150, 20);

        texSconto.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        texSconto.setColumns(8);
        texSconto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        texSconto.setText("0");
        texSconto.setDbTipoCampo("valuta");
        texSconto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                texScontoKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                texScontoKeyReleased(evt);
            }
        });
        jPanel4.add(texSconto);
        texSconto.setBounds(235, 10, 75, 16);

        jPanel2.add(jPanel4);

        datiRighe.add(jPanel2, java.awt.BorderLayout.SOUTH);

        jSplitPane1.setBottomComponent(datiRighe);

        dati.setMinimumSize(new java.awt.Dimension(0, 50));
        dati.setPreferredSize(new java.awt.Dimension(50, 100));
        dati.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        texNumePrev.setEditable(false);
        texNumePrev.setText("numero");
        texNumePrev.setDbDescCampo("");
        texNumePrev.setDbNomeCampo("numero");
        texNumePrev.setDbTipoCampo("testo");
        texNumePrev.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                texNumePrevFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                texNumePrevFocusLost(evt);
            }
        });
        dati.add(texNumePrev, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 20, 45, 20));

        texClie.setText("cliente");
        texClie.setDbComboAbbinata(comClie);
        texClie.setDbDefault("vuoto");
        texClie.setDbDescCampo("");
        texClie.setDbNomeCampo("cliente");
        texClie.setDbNullSeVuoto(true);
        texClie.setDbTipoCampo("");
        texClie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                texClieActionPerformed(evt);
            }
        });
        texClie.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                texClieFocusLost(evt);
            }
        });
        texClie.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                texClieKeyPressed(evt);
            }
        });
        dati.add(texClie, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 110, 45, 20));

        texSpeseIncasso.setText("spese_incasso");
        texSpeseIncasso.setDbDescCampo("");
        texSpeseIncasso.setDbNomeCampo("spese_incasso");
        texSpeseIncasso.setDbTipoCampo("valuta");
        texSpeseIncasso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                texSpeseIncassoActionPerformed(evt);
            }
        });
        texSpeseIncasso.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                texSpeseIncassoFocusLost(evt);
            }
        });
        texSpeseIncasso.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                texSpeseIncassoKeyReleased(evt);
            }
        });
        dati.add(texSpeseIncasso, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 55, 85, 20));

        texScon2.setText("sconto2");
        texScon2.setToolTipText("secondo sconto");
        texScon2.setDbDescCampo("");
        texScon2.setDbNomeCampo("sconto2");
        texScon2.setDbTipoCampo("numerico");
        texScon2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                texScon2ActionPerformed(evt);
            }
        });
        texScon2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                texScon2FocusLost(evt);
            }
        });
        texScon2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                texScon2KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                texScon2KeyReleased(evt);
            }
        });
        dati.add(texScon2, new org.netbeans.lib.awtextra.AbsoluteConstraints(65, 55, 50, 20));

        texScon1.setText("sconto1");
        texScon1.setToolTipText("primo sconto");
        texScon1.setDbDescCampo("");
        texScon1.setDbNomeCampo("sconto1");
        texScon1.setDbTipoCampo("numerico");
        texScon1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                texScon1ActionPerformed(evt);
            }
        });
        texScon1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                texScon1FocusLost(evt);
            }
        });
        texScon1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                texScon1KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                texScon1KeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                texScon1KeyTyped(evt);
            }
        });
        dati.add(texScon1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 55, 50, 20));

        comClie.setDbNomeCampo("");
        comClie.setDbRiempire(false);
        comClie.setDbSalvare(false);
        comClie.setDbTextAbbinato(texClie);
        comClie.setDbTipoCampo("");
        comClie.setDbTrovaMentreScrive(true);
        comClie.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comClieItemStateChanged(evt);
            }
        });
        comClie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comClieActionPerformed(evt);
            }
        });
        comClie.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                comClieFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                comClieFocusLost(evt);
            }
        });
        comClie.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                comClieKeyPressed(evt);
            }
        });
        dati.add(comClie, new org.netbeans.lib.awtextra.AbsoluteConstraints(745, 110, 45, 20));

        texTotaImpo1.setBackground(new java.awt.Color(255, 200, 200));
        texTotaImpo1.setText("0");
        texTotaImpo1.setDbDescCampo("");
        texTotaImpo1.setDbNomeCampo("totale_imponibile");
        texTotaImpo1.setDbTipoCampo("valuta");
        dati.add(texTotaImpo1, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 160, 60, -1));

        texTotaIva1.setBackground(new java.awt.Color(255, 200, 200));
        texTotaIva1.setText("0");
        texTotaIva1.setDbDescCampo("");
        texTotaIva1.setDbNomeCampo("totale_iva");
        texTotaIva1.setDbTipoCampo("valuta");
        dati.add(texTotaIva1, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 180, 60, -1));

        texTota1.setBackground(new java.awt.Color(255, 200, 200));
        texTota1.setText("0");
        texTota1.setDbDescCampo("");
        texTota1.setDbNomeCampo("totale");
        texTota1.setDbTipoCampo("valuta");
        dati.add(texTota1, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 200, 60, -1));

        texNote.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        texNote.setDbNomeCampo("note");
        texNote.setFont(texNote.getFont());
        texNote.setText("note");
        dati.add(texNote, new org.netbeans.lib.awtextra.AbsoluteConstraints(75, 205, 600, 35));

        jLabel13.setText("numero");
        dati.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 5, 45, 15));

        jLabel14.setText("serie");
        dati.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 5, 30, 15));

        jLabel15.setText("destinazione merce");
        dati.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 5, 205, 15));

        jLabel16.setText("data");
        dati.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(95, 5, 75, 15));

        labScon1.setText("sc. 1");
        labScon1.setToolTipText("primo sconto");
        dati.add(labScon1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 45, 15));

        labScon2.setText("sc. 3");
        labScon2.setToolTipText("sconto3");
        dati.add(labScon2, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 40, 45, 15));

        jLabel113.setText("spese incasso");
        dati.add(jLabel113, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 40, 85, -1));

        texData.setEditable(false);
        texData.setText("data");
        texData.setDbDescCampo("");
        texData.setDbNomeCampo("data");
        texData.setDbTipoCampo("data");
        texData.setFont(texData.getFont().deriveFont(texData.getFont().getSize()-1f));
        texData.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                texDataFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                texDataFocusLost(evt);
            }
        });
        dati.add(texData, new org.netbeans.lib.awtextra.AbsoluteConstraints(95, 20, 75, 20));

        jLabel11.setText("Annotazioni");
        dati.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 205, 65, -1));

        texStat.setBackground(new java.awt.Color(255, 200, 200));
        texStat.setText("P");
        texStat.setDbDescCampo("");
        texStat.setDbNomeCampo("stato");
        texStat.setDbRiempire(false);
        texStat.setDbTipoCampo("");
        dati.add(texStat, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 135, 30, -1));

        jLabel12.setText("Aspetto esteriore beni");
        dati.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 110, 20));

        texScon3.setText("sconto3");
        texScon3.setToolTipText("terzo sconto");
        texScon3.setDbDescCampo("");
        texScon3.setDbNomeCampo("sconto3");
        texScon3.setDbTipoCampo("numerico");
        texScon3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                texScon3ActionPerformed(evt);
            }
        });
        texScon3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                texScon3FocusLost(evt);
            }
        });
        texScon3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                texScon3KeyReleased(evt);
            }
        });
        dati.add(texScon3, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 55, 50, 20));

        labScon21.setText("sc. 2");
        labScon21.setToolTipText("secondo sconto");
        dati.add(labScon21, new org.netbeans.lib.awtextra.AbsoluteConstraints(65, 40, 50, 15));

        jLabel151.setText("cliente");
        dati.add(jLabel151, new org.netbeans.lib.awtextra.AbsoluteConstraints(175, 5, 75, 15));

        comClieDest.setDbNomeCampo("");
        comClieDest.setDbRiempire(false);
        comClieDest.setDbSalvare(false);
        comClieDest.setDbTextAbbinato(texClieDest);
        comClieDest.setDbTipoCampo("numerico");
        comClieDest.setDbTrovaMentreScrive(true);
        comClieDest.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                comClieDestFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                comClieDestFocusLost(evt);
            }
        });
        comClieDest.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                comClieDestKeyPressed(evt);
            }
        });
        dati.add(comClieDest, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 20, 275, 20));

        texSeri.setBackground(new java.awt.Color(204, 204, 204));
        texSeri.setEditable(false);
        texSeri.setText("serie");
        texSeri.setDbDescCampo("");
        texSeri.setDbNomeCampo("serie");
        texSeri.setDbTipoCampo("");
        texSeri.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                texSeriKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                texSeriKeyTyped(evt);
            }
        });
        dati.add(texSeri, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 30, 20));

        texAnno.setBackground(new java.awt.Color(255, 200, 200));
        texAnno.setText("anno");
        texAnno.setDbDescCampo("");
        texAnno.setDbNomeCampo("anno");
        texAnno.setDbTipoCampo("");
        texAnno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                texAnnoActionPerformed(evt);
            }
        });
        dati.add(texAnno, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 80, 100, -1));

        texClieDest.setBackground(new java.awt.Color(255, 200, 200));
        texClieDest.setText("cliente_destinazione");
        texClieDest.setDbComboAbbinata(comClieDest);
        texClieDest.setDbDescCampo("");
        texClieDest.setDbNomeCampo("cliente_destinazione");
        texClieDest.setDbTipoCampo("numerico");
        dati.add(texClieDest, new org.netbeans.lib.awtextra.AbsoluteConstraints(705, 45, 45, -1));

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText("Peso netto");
        dati.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 270, 65, 20));

        texNumeroColli.setText("numero_colli");
        texNumeroColli.setDbDescCampo("");
        texNumeroColli.setDbNomeCampo("numero_colli");
        texNumeroColli.setDbTipoCampo("");
        texNumeroColli.setmaxChars(255);
        dati.add(texNumeroColli, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 245, 60, 20));

        jLabel20.setText("1° Vettore");
        dati.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 155, 60, 20));

        jLabel1.setText("Porto");
        dati.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 180, 60, 20));

        comPorto.setDbNomeCampo("porto");
        comPorto.setDbRiempireForceText(true);
        comPorto.setDbSalvaKey(false);
        dati.add(comPorto, new org.netbeans.lib.awtextra.AbsoluteConstraints(455, 180, 220, 20));

        jLabel4.setText("Causale del trasporto");
        dati.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 105, 125, 20));

        comCausaleTrasporto.setDbNomeCampo("causale_trasporto");
        comCausaleTrasporto.setDbRiempireForceText(true);
        comCausaleTrasporto.setDbSalvaKey(false);
        comCausaleTrasporto.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comCausaleTrasportoItemStateChanged(evt);
            }
        });
        comCausaleTrasporto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comCausaleTrasportoActionPerformed(evt);
            }
        });
        dati.add(comCausaleTrasporto, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 105, 275, 20));

        texSpeseTrasporto.setText("spese_trasporto");
        texSpeseTrasporto.setDbDescCampo("");
        texSpeseTrasporto.setDbNomeCampo("spese_trasporto");
        texSpeseTrasporto.setDbTipoCampo("valuta");
        texSpeseTrasporto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                texSpeseTrasportoActionPerformed(evt);
            }
        });
        texSpeseTrasporto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                texSpeseTrasportoFocusLost(evt);
            }
        });
        texSpeseTrasporto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                texSpeseTrasportoKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                texSpeseTrasportoKeyReleased(evt);
            }
        });
        dati.add(texSpeseTrasporto, new org.netbeans.lib.awtextra.AbsoluteConstraints(175, 55, 90, 20));

        jLabel114.setText("spese trasporto");
        dati.add(jLabel114, new org.netbeans.lib.awtextra.AbsoluteConstraints(175, 40, 90, -1));

        jLabel5.setText("Consegna o inizio trasporto a mezzo");
        dati.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 180, 200, 20));

        comMezzoTrasporto.setDbNomeCampo("mezzo_consegna");
        comMezzoTrasporto.setDbRiempireForceText(true);
        comMezzoTrasporto.setDbSalvaKey(false);
        dati.add(comMezzoTrasporto, new org.netbeans.lib.awtextra.AbsoluteConstraints(215, 180, 160, 20));

        labScon10.setFont(labScon10.getFont().deriveFont(labScon10.getFont().getSize()-1f));
        labScon10.setText("ragione sociale");
        labScon10.setToolTipText("");
        dati.add(labScon10, new org.netbeans.lib.awtextra.AbsoluteConstraints(435, 58, 80, 15));

        texDestRagioneSociale.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        texDestRagioneSociale.setToolTipText("");
        texDestRagioneSociale.setDbDescCampo("");
        texDestRagioneSociale.setDbNomeCampo("dest_ragione_sociale");
        texDestRagioneSociale.setDbTipoCampo("");
        texDestRagioneSociale.setFont(texDestRagioneSociale.getFont().deriveFont(texDestRagioneSociale.getFont().getSize()-1f));
        dati.add(texDestRagioneSociale, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 58, 170, 17));

        labScon11.setFont(labScon11.getFont().deriveFont(labScon11.getFont().getSize()-1f));
        labScon11.setText("indirizzo");
        labScon11.setToolTipText("");
        dati.add(labScon11, new org.netbeans.lib.awtextra.AbsoluteConstraints(435, 76, 50, 15));

        texDestIndirizzo.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        texDestIndirizzo.setToolTipText("");
        texDestIndirizzo.setDbDescCampo("");
        texDestIndirizzo.setDbNomeCampo("dest_indirizzo");
        texDestIndirizzo.setDbTipoCampo("");
        texDestIndirizzo.setFont(texDestIndirizzo.getFont().deriveFont(texDestIndirizzo.getFont().getSize()-1f));
        dati.add(texDestIndirizzo, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 76, 200, 17));

        labScon12.setFont(labScon12.getFont().deriveFont(labScon12.getFont().getSize()-1f));
        labScon12.setText("cap");
        labScon12.setToolTipText("");
        dati.add(labScon12, new org.netbeans.lib.awtextra.AbsoluteConstraints(435, 94, 25, 15));

        texRiferimento.setText("riferimento");
        texRiferimento.setDbDescCampo("");
        texRiferimento.setDbNomeCampo("riferimento");
        texRiferimento.setDbTipoCampo("");
        texRiferimento.setmaxChars(255);
        dati.add(texRiferimento, new org.netbeans.lib.awtextra.AbsoluteConstraints(75, 80, 340, 20));

        texDestCap.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        texDestCap.setToolTipText("");
        texDestCap.setDbDescCampo("");
        texDestCap.setDbNomeCampo("dest_cap");
        texDestCap.setDbTipoCampo("");
        texDestCap.setFont(texDestCap.getFont().deriveFont(texDestCap.getFont().getSize()-1f));
        dati.add(texDestCap, new org.netbeans.lib.awtextra.AbsoluteConstraints(465, 94, 35, 17));

        texDestLocalita.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        texDestLocalita.setToolTipText("");
        texDestLocalita.setDbDescCampo("");
        texDestLocalita.setDbNomeCampo("dest_localita");
        texDestLocalita.setDbTipoCampo("");
        texDestLocalita.setFont(texDestLocalita.getFont().deriveFont(texDestLocalita.getFont().getSize()-1f));
        dati.add(texDestLocalita, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 94, 95, 17));

        labScon13.setFont(labScon13.getFont().deriveFont(labScon13.getFont().getSize()-1f));
        labScon13.setText("loc.");
        labScon13.setToolTipText("");
        dati.add(labScon13, new org.netbeans.lib.awtextra.AbsoluteConstraints(505, 94, 25, 15));

        texDestProvincia.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        texDestProvincia.setToolTipText("");
        texDestProvincia.setDbDescCampo("");
        texDestProvincia.setDbNomeCampo("dest_provincia");
        texDestProvincia.setDbTipoCampo("");
        texDestProvincia.setFont(texDestProvincia.getFont().deriveFont(texDestProvincia.getFont().getSize()-1f));
        dati.add(texDestProvincia, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 94, 30, 17));

        labScon14.setFont(labScon14.getFont().deriveFont(labScon14.getFont().getSize()-1f));
        labScon14.setText("prov.");
        labScon14.setToolTipText("");
        dati.add(labScon14, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 94, 25, 15));

        texDestCellulare.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        texDestCellulare.setToolTipText("");
        texDestCellulare.setDbDescCampo("");
        texDestCellulare.setDbNomeCampo("dest_cellulare");
        texDestCellulare.setDbTipoCampo("");
        texDestCellulare.setFont(texDestCellulare.getFont().deriveFont(texDestCellulare.getFont().getSize()-1f));
        dati.add(texDestCellulare, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 112, 80, 17));

        labScon15.setFont(labScon15.getFont().deriveFont(labScon15.getFont().getSize()-1f));
        labScon15.setText("cellulare");
        labScon15.setToolTipText("");
        dati.add(labScon15, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 112, 50, 15));

        texDestTelefono.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        texDestTelefono.setToolTipText("");
        texDestTelefono.setDbDescCampo("");
        texDestTelefono.setDbNomeCampo("dest_telefono");
        texDestTelefono.setDbTipoCampo("");
        texDestTelefono.setFont(texDestTelefono.getFont().deriveFont(texDestTelefono.getFont().getSize()-1f));
        dati.add(texDestTelefono, new org.netbeans.lib.awtextra.AbsoluteConstraints(485, 112, 70, 17));

        labScon16.setFont(labScon16.getFont().deriveFont(labScon16.getFont().getSize()-1f));
        labScon16.setText("telefono");
        labScon16.setToolTipText("");
        dati.add(labScon16, new org.netbeans.lib.awtextra.AbsoluteConstraints(435, 112, 50, 15));

        labScon17.setFont(labScon17.getFont().deriveFont(labScon17.getFont().getSize()-1f));
        labScon17.setText("paese");
        labScon17.setToolTipText("");
        dati.add(labScon17, new org.netbeans.lib.awtextra.AbsoluteConstraints(435, 130, 50, 15));

        comPaese.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        comPaese.setDbNomeCampo("dest_paese");
        comPaese.setDbTipoCampo("");
        comPaese.setDbTrovaMentreScrive(true);
        comPaese.setFont(comPaese.getFont().deriveFont(comPaese.getFont().getSize()-1f));
        dati.add(comPaese, new org.netbeans.lib.awtextra.AbsoluteConstraints(485, 130, 205, 17));

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "destinazione diversa (manuale)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 10))); // NOI18N
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        dati.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 40, 275, 110));

        butPrezziPrec.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        butPrezziPrec.setText("prezzi precedenti");
        butPrezziPrec.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butPrezziPrecActionPerformed(evt);
            }
        });
        dati.add(butPrezziPrec, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 5, 165, 15));

        comAspettoEsterioreBeni.setDbNomeCampo("aspetto_esteriore_beni");
        comAspettoEsterioreBeni.setDbRiempireForceText(true);
        comAspettoEsterioreBeni.setDbSalvaKey(false);
        dati.add(comAspettoEsterioreBeni, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 130, 275, 20));

        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText("Pagamento");
        dati.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 245, 65, 20));

        comPaga.setDbDescCampo("");
        comPaga.setDbNomeCampo("pagamento");
        comPaga.setDbTextAbbinato(null);
        comPaga.setDbTipoCampo("VARCHAR");
        comPaga.setDbTrovaMentreScrive(true);
        dati.add(comPaga, new org.netbeans.lib.awtextra.AbsoluteConstraints(75, 245, 180, 20));

        comVettori.setDbNomeCampo("vettore1");
        comVettori.setDbRiempireForceText(true);
        comVettori.setDbSalvaKey(false);
        dati.add(comVettori, new org.netbeans.lib.awtextra.AbsoluteConstraints(75, 155, 600, 20));

        texDataOra.setText("dataoraddt");
        texDataOra.setDbDescCampo("");
        texDataOra.setDbNomeCampo("dataoraddt");
        texDataOra.setDbTipoCampo("");
        texDataOra.setFont(texDataOra.getFont().deriveFont(texDataOra.getFont().getSize()-2f));
        texDataOra.setmaxChars(255);
        texDataOra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                texDataOraActionPerformed(evt);
            }
        });
        dati.add(texDataOra, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 245, 90, 20));

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("data / ora");
        dati.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 245, 60, 20));

        stampa_prezzi.setText("stampa prezzi");
        stampa_prezzi.setToolTipText("Spuntare per avere i prezzi in stampa del documento");
        stampa_prezzi.setDbDescCampo("Stampa prezzi in DDT");
        stampa_prezzi.setDbNomeCampo("opzione_prezzi_ddt");
        stampa_prezzi.setDbTipoCampo("");
        stampa_prezzi.setFont(stampa_prezzi.getFont().deriveFont(stampa_prezzi.getFont().getSize()-2f));
        stampa_prezzi.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        stampa_prezzi.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        stampa_prezzi.setIconTextGap(2);
        stampa_prezzi.setMaximumSize(new java.awt.Dimension(230, 25));
        dati.add(stampa_prezzi, new org.netbeans.lib.awtextra.AbsoluteConstraints(555, 245, 120, 20));

        labAgente.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labAgente.setText("Agente");
        dati.add(labAgente, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 270, 65, 20));

        comAgente.setToolTipText("");
        comAgente.setDbDescCampo("");
        comAgente.setDbNomeCampo("agente_codice");
        comAgente.setDbTipoCampo("numerico");
        comAgente.setDbTrovaMentreScrive(true);
        comAgente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comAgenteActionPerformed(evt);
            }
        });
        comAgente.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                comAgenteFocusLost(evt);
            }
        });
        dati.add(comAgente, new org.netbeans.lib.awtextra.AbsoluteConstraints(75, 270, 180, 20));

        labProvvigione.setText("Provvigione");
        dati.add(labProvvigione, new org.netbeans.lib.awtextra.AbsoluteConstraints(265, 270, 65, 20));

        texProvvigione.setToolTipText("");
        texProvvigione.setDbDescCampo("");
        texProvvigione.setDbNomeCampo("agente_percentuale");
        texProvvigione.setDbTipoCampo("numerico");
        texProvvigione.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                texProvvigioneFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                texProvvigioneFocusLost(evt);
            }
        });
        dati.add(texProvvigione, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 270, 35, 20));

        labPercentoProvvigione.setText("%");
        dati.add(labPercentoProvvigione, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 270, 15, 20));

        labRiferimento.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labRiferimento.setText("Riferimento");
        dati.add(labRiferimento, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 65, 20));

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel23.setText("Num. colli");
        dati.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(425, 245, 60, 20));

        texNumeroColli1.setText("peso_lordo");
        texNumeroColli1.setDbDescCampo("");
        texNumeroColli1.setDbNomeCampo("peso_lordo");
        texNumeroColli1.setDbTipoCampo("");
        texNumeroColli1.setmaxChars(255);
        dati.add(texNumeroColli1, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 270, 60, 20));

        texNumeroColli2.setText("peso_netto");
        texNumeroColli2.setDbDescCampo("");
        texNumeroColli2.setDbNomeCampo("peso_netto");
        texNumeroColli2.setDbTipoCampo("");
        texNumeroColli2.setmaxChars(255);
        dati.add(texNumeroColli2, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 270, 65, 20));

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel24.setText("Peso lordo");
        dati.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 270, 65, 20));

        prezzi_ivati.setBackground(new java.awt.Color(255, 204, 204));
        prezzi_ivati.setText("prezzi ivati");
        prezzi_ivati.setToolTipText("Selezionando questa opzione stampa la Destinazione diversa nella Distinta delle RIBA");
        prezzi_ivati.setDbDescCampo("Prezzi Ivati");
        prezzi_ivati.setDbNomeCampo("prezzi_ivati");
        prezzi_ivati.setDbTipoCampo("");
        prezzi_ivati.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        prezzi_ivati.setIconTextGap(1);
        dati.add(prezzi_ivati, new org.netbeans.lib.awtextra.AbsoluteConstraints(695, 225, 80, 25));
        dati.add(texCliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(175, 20, 190, 20));

        apriclienti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                apriclientiActionPerformed(evt);
            }
        });
        dati.add(apriclienti, new org.netbeans.lib.awtextra.AbsoluteConstraints(365, 20, 20, 20));

        butAddClie.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/Actions-contact-new-icon-16.png"))); // NOI18N
        butAddClie.setToolTipText("Crea un nuovo cliente");
        butAddClie.setMargin(new java.awt.Insets(1, 1, 1, 1));
        butAddClie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butAddClieActionPerformed(evt);
            }
        });
        dati.add(butAddClie, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 20, 25, 20));

        jTabbedPane1.addTab("DDT", dati);

        jLabel8.setText("Colonna listino consigliato");

        listino_consigliato.setDbNomeCampo("listino_consigliato");
        listino_consigliato.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listino_consigliatoActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout datiOpzioniLayout = new org.jdesktop.layout.GroupLayout(datiOpzioni);
        datiOpzioni.setLayout(datiOpzioniLayout);
        datiOpzioniLayout.setHorizontalGroup(
            datiOpzioniLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(datiOpzioniLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel8)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(listino_consigliato, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(573, Short.MAX_VALUE))
        );
        datiOpzioniLayout.setVerticalGroup(
            datiOpzioniLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(datiOpzioniLayout.createSequentialGroup()
                .addContainerGap()
                .add(datiOpzioniLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel8)
                    .add(listino_consigliato, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(284, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Opzioni", datiOpzioni);

        jSplitPane1.setTopComponent(jTabbedPane1);

        panDati.add(jSplitPane1, java.awt.BorderLayout.CENTER);

        getContentPane().add(panDati, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void texDataFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texDataFocusGained

        old_anno = getAnno();
        old_data = texData.getText();
        old_id = texNumePrev.getText();
        anno_modificato = false;

    }//GEN-LAST:event_texDataFocusGained

    private void texNumePrevFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texNumePrevFocusGained
        old_id = texNumePrev.getText();
        id_modificato = false;
    }//GEN-LAST:event_texNumePrevFocusGained

    private void texNumePrevFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texNumePrevFocusLost
        texNumePrev.setText(texNumePrev.getText().replaceAll("[^\\d.]", ""));
        if (!old_id.equals(texNumePrev.getText())) {
            //controllo che se è un numero già presente non glielo facci ofare percè altrimenti sovrascrive una altra fattura
            sql = "select numero from test_ddt" + suff;
            sql += " where serie " + Db.pcW(this.prev.serie, "VARCHAR");
            sql += " and numero " + Db.pcW(texNumePrev.getText(), "NUMBER");
            sql += " and anno " + Db.pcW(String.valueOf(this.prev.anno), "VARCHAR");
            ResultSet r = Db.openResultSet(sql);
            try {
                if (r.next()) {
                    texNumePrev.setText(old_id);
                    JOptionPane.showMessageDialog(this, "Non puoi mettere il numero di un documento già presente !", "Attenzione", JOptionPane.INFORMATION_MESSAGE);
                    return;
                } else {
                    //associo al nuovo numero
                    prev.numero = new Integer(this.texNumePrev.getText()).intValue();

                    sql = "update righ_ddt" + suff + "";
                    sql += " set numero = " + Db.pc(prev.numero, "NUMBER");
                    sql += " where serie " + Db.pcW(this.prev.serie, "VARCHAR");
                    sql += " and numero " + Db.pcW(old_id, "NUMBER");
                    sql += " and anno " + Db.pcW(String.valueOf(this.prev.anno), "VARCHAR");
                    Db.executeSql(sql);

                    sql = "update test_ddt" + suff + "";
                    sql += " set numero = " + Db.pc(prev.numero, "NUMBER");
                    sql += " where serie " + Db.pcW(this.prev.serie, "VARCHAR");
                    sql += " and numero " + Db.pcW(old_id, "NUMBER");
                    sql += " and anno " + Db.pcW(String.valueOf(this.prev.anno), "VARCHAR");
                    Db.executeSql(sql);

//                    dati.dbChiaveValori.clear();
//                    dati.dbChiaveValori.put("serie", prev.serie);
//                    dati.dbChiaveValori.put("numero", prev.numero);
//                    dati.dbChiaveValori.put("anno", prev.anno);

                    //riassocio
                    dbAssociaGrigliaRighe();
                    id_modificato = true;

                    prev.numero = Integer.parseInt(texNumePrev.getText());
                    doc.load(Db.INSTANCE, this.prev.numero, this.prev.serie, this.prev.anno, getTipoDoc(), id);
                    ricalcolaTotali();

                    //vado ad aggiornare eventuali ddt o ordini legati
                    sql = "update test_ordi";
                    sql += " set doc_numero = " + Db.pc(prev.numero, "NUMBER");
                    sql += " where doc_serie " + Db.pcW(this.prev.serie, "VARCHAR");
                    sql += " and doc_numero " + Db.pcW(old_id, "NUMBER");
                    sql += " and doc_anno " + Db.pcW(String.valueOf(this.prev.anno), "VARCHAR");
                    sql += " and doc_tipo " + Db.pcW(String.valueOf(this.prev.tipoDocumento), "VARCHAR");
                    Db.executeSql(sql);

                    //vado ad aggiornare eventuali movimenti generati
                    sql = "update movimenti_magazzino";
                    sql += " set da_numero = " + Db.pc(prev.numero, "NUMBER");
                    sql += " where da_serie " + Db.pcW(this.prev.serie, "VARCHAR");
                    sql += " and da_numero " + Db.pcW(old_id, "NUMBER");
                    sql += " and da_anno " + Db.pcW(String.valueOf(this.prev.anno), "VARCHAR");
                    sql += " and da_tabella = 'test_ddt" + suff + "'";
                    Db.executeSql(sql);

                }
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }//GEN-LAST:event_texNumePrevFocusLost

    private void comClieActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comClieActionPerformed
        sql = "select obsoleto from clie_forn";
        sql += " where codice = " + Db.pc(this.texClie.getText(), "NUMERIC");
        sql += " order by ragione_sociale";

        ResultSet rs = Db.openResultSet(sql);
        try {
            if (rs.next()) {
                int obsoleto = rs.getInt("obsoleto");
                if (obsoleto == 1) {
                    JOptionPane.showMessageDialog(this, "Attenzione, il cliente selezionato è segnato come obsoleto.", "Cliente obsoleto", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //apro combo destinazione cliente
        sql = "select ragione_sociale,codice from clie_forn_dest";
        sql += " where codice_cliente = " + Db.pc(this.texClie.getText(), "NUMERIC");

        riempiDestDiversa(sql);
    }//GEN-LAST:event_comClieActionPerformed

    private void texClieActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texClieActionPerformed
        ricalcolaTotali();
    }//GEN-LAST:event_texClieActionPerformed

    private void texSpeseTrasportoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texSpeseTrasportoActionPerformed
        ricalcolaTotali();
    }//GEN-LAST:event_texSpeseTrasportoActionPerformed

    private void texScon3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texScon3ActionPerformed
        ricalcolaTotali();
    }//GEN-LAST:event_texScon3ActionPerformed

    private void texScon2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texScon2ActionPerformed
        ricalcolaTotali();
    }//GEN-LAST:event_texScon2ActionPerformed

    private void texSpeseIncassoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texSpeseIncassoFocusLost
        ricalcolaTotali();
    }//GEN-LAST:event_texSpeseIncassoFocusLost

    private void texSpeseTrasportoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texSpeseTrasportoFocusLost
        ricalcolaTotali();
    }//GEN-LAST:event_texSpeseTrasportoFocusLost

    private void texScon3FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texScon3FocusLost
        ricalcolaTotali();
    }//GEN-LAST:event_texScon3FocusLost

    private void texSeriKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texSeriKeyTyped
            }//GEN-LAST:event_texSeriKeyTyped

    private void texSeriKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texSeriKeyPressed

        if (evt.getKeyCode() == evt.VK_TAB || evt.getKeyCode() == evt.VK_ENTER) {
            texSeri.setText(texSeri.getText().toUpperCase());
            assegnaNumero();

            //riabilito
            Component[] cs = this.dati.getComponents();

            for (int i = 0; i < cs.length; i++) {
                cs[i].setEnabled(true);

                if (cs[i] instanceof tnxbeans.tnxComboField) {

                    tnxbeans.tnxComboField combo = (tnxbeans.tnxComboField) cs[i];
                    combo.setEditable(true);
                    combo.setLocked(false);
                }
            }

            dopoInserimento();
            texCliente.requestFocus();
        }
    }//GEN-LAST:event_texSeriKeyPressed

    private void comCausaleTrasportoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comCausaleTrasportoItemStateChanged
        if (evt.getStateChange() == evt.SELECTED) {
            visualizzaTarga();
        }
    }//GEN-LAST:event_comCausaleTrasportoItemStateChanged

    private void comCausaleTrasportoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comCausaleTrasportoActionPerformed
            }//GEN-LAST:event_comCausaleTrasportoActionPerformed

    private void comClieDestFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_comClieDestFocusLost
        if (comClieDest.getSelectedIndex() != comClieDest_old) {
            caricaDestinazioneDiversa();
        }
    }//GEN-LAST:event_comClieDestFocusLost

    private void comClieDestKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_comClieDestKeyPressed

        if (evt.getKeyCode() == evt.VK_ENTER) {
            caricaDestinazioneDiversa();
        }
    }//GEN-LAST:event_comClieDestKeyPressed

    private void butNuovoAnimaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butNuovoAnimaleActionPerformed

        Animale animale = new Animale();
        int riga = -1;
        animale.loadAnimale(gestioneFatture.logic.documenti.Util.TIPO_DOCUMENTO_DDT, this.texSeri.getText(), Integer.parseInt(this.texNumePrev.getText()), Integer.parseInt(this.texAnno.getText()), riga);

        JInternalFrameAnimaleDdt frameAnimale = new JInternalFrameAnimaleDdt(this, animale);
        main.getPadre().openFrame(frameAnimale, 450, 200);
    }//GEN-LAST:event_butNuovoAnimaleActionPerformed

    private void butPrezziPrecActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butPrezziPrecActionPerformed
        showPrezziFatture();
    }//GEN-LAST:event_butPrezziPrecActionPerformed

    private void texSpeseTrasportoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texSpeseTrasportoKeyReleased

        try {
            prev.speseTrasportoIva = Db.getDouble(this.texSpeseTrasporto.getText());
        } catch (Exception err) {
            prev.speseTrasportoIva = 0;
        }

        prev.dbRefresh();
    }//GEN-LAST:event_texSpeseTrasportoKeyReleased

    private void texSpeseTrasportoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texSpeseTrasportoKeyPressed
            }//GEN-LAST:event_texSpeseTrasportoKeyPressed

    private void texSpeseIncassoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texSpeseIncassoActionPerformed
        ricalcolaTotali();
    }//GEN-LAST:event_texSpeseIncassoActionPerformed

    private void butStampaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butStampaActionPerformed
        if (block_aggiornareProvvigioni) return;

        if (SwingUtils.showYesNoMessage(this, "Prima di stampare è necessario salvare il documento, proseguire ?")) {     
            if (!controlloCampi()) {
                return;
            }

            String dbSerie = this.prev.serie;
            int dbNumero = this.prev.numero;
            int dbAnno = this.prev.anno;
            
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

            //SALVATAGGIO
            //sposto i totali di modo che li salvi
            this.texTota1.setText(this.texTota.getText());
            this.texTotaImpo1.setText(this.texTotaImpo.getText());
            this.texTotaIva1.setText(this.texTotaIva.getText());

            dati.setCampiAggiuntivi(new Hashtable());
            dati.getCampiAggiuntivi().put("evaso", Db.pc(getStatoEvaso(), Types.VARCHAR));
            dati.getCampiAggiuntivi().put("sconto", Db.pc(doc.getSconto(), Types.DOUBLE));
            dati.getCampiAggiuntivi().put("totale_imponibile_pre_sconto", Db.pc(doc.totaleImponibilePreSconto, Types.DOUBLE));
            dati.getCampiAggiuntivi().put("totale_ivato_pre_sconto", Db.pc(doc.totaleIvatoPreSconto, Types.DOUBLE));

            this.dati.dbSave();

            //forzo gli id padre
            String serie = this.texSeri.getText();
            Integer numero = Integer.parseInt(texNumePrev.getText());
            Integer anno = Integer.parseInt(texAnno.getText());
            String sql = "UPDATE righ_ddt r left join test_ddt t on r.serie = t.serie and r.anno = t.anno and r.numero = t.numero set r.id_padre = t.id where t.serie = " + Db.pc(serie, Types.VARCHAR) + " and t.numero = " + numero + " and t.anno = " + anno;
            try {
                DbUtils.tryExecQuery(Db.getConn(), sql);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (this.prev.generaMovimentiMagazzino() == false) {
                javax.swing.JOptionPane.showMessageDialog(this, "Errore nella generazione dei movimenti di magazzino", "Errore", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
            //aggiorno eventuali documenti collegati (ordini, ddt)
            InvoicexUtil.aggiornaRiferimentoDocumenti(getTipoDoc(), id);
            try {
                if (from != null) {
                    this.from.dbRefresh();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            main.events.fireInvoicexEvent(new InvoicexEvent(this, InvoicexEvent.TYPE_SAVE));            
            
            //STAMPA
            if (evt.getActionCommand().equalsIgnoreCase("pdf")) {
                try {
                    InvoicexUtil.creaPdf(getTipoDoc(), new Integer[] {id}, true, false);
                } catch (Exception e) {
                    e.printStackTrace();
                    SwingUtils.showExceptionMessage(this, e);
                }                
            } else {            
                frmElenDDT.stampa("", dbSerie, dbNumero, dbAnno, acquisto, id);
            }

            //MODIFICA dopo
            String nuova_serie = texSeri.getText();
            Integer nuovo_numero = cu.toInteger(texNumePrev.getText());
            Integer nuovo_anno = cu.toInteger(texAnno.getText());

            //aggiorno le righe temp
            sql = "update righ_ddt" + suff + "_temp";
            sql += " set serie = '" + nuova_serie + "'";
            sql += " , numero = " + nuovo_numero + "";
            sql += " , anno = " + nuovo_anno + "";
            sql += " where serie = " + db.pc(serie_originale, "VARCHAR");
            sql += " and numero = " + numero_originale;
            sql += " and anno = " + anno_originale;
            Db.executeSqlDialogExc(sql, true);

            serie_originale = texSeri.getText();
            numero_originale = cu.toInteger(texNumePrev.getText());
            anno_originale = cu.toInteger(texAnno.getText());                
            
            totaleIniziale = doc.getTotale();

            //una volta salvatao e stampato entro in modalitaà modifica se ero in inserimento
            if (dbStato.equals(frmTestDocu.DB_INSERIMENTO)) {
                dbStato = frmTestDocu.DB_MODIFICA;
                //e riporto le righe in _temp
                sql = "insert into righ_ddt" + suff + "_temp";
                sql += " select *, '" + main.login + "' as username";
                sql += " from righ_ddt" + suff;
                sql += " where serie = " + db.pc(nuova_serie, "VARCHAR");
                sql += " and numero = " + nuovo_numero;
                sql += " and anno = " + nuovo_anno;
                try {
                    DbUtils.tryExecQuery(Db.getConn(), sql);
                    System.out.println("sql ok:" + sql);
                } catch (Exception e) {
                    System.err.println("sql errore:" + sql);
                    e.printStackTrace();
                }
                dati.dbCheckModificatiReset();
            }            
        }
        
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

    }//GEN-LAST:event_butStampaActionPerformed

    private void texDataFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texDataFocusLost
        try {
            SimpleDateFormat datef = null;
            if (texData.getText().length() == 8 || texData.getText().length() == 7) {
                datef = new SimpleDateFormat("dd/MM/yy");
            } else if (texData.getText().length() == 10 || texData.getText().length() == 9) {
                datef = new SimpleDateFormat("dd/MM/yyyy");
            } else {
                JOptionPane.showMessageDialog(this, "La data inserita non è valida", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Calendar cal = Calendar.getInstance();
            datef.setLenient(true);
            cal.setTime(datef.parse(texData.getText()));
        } catch (Exception err) {
            System.out.println("err:" + err);
            JOptionPane.showMessageDialog(this, "La data inserita non è valida", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!old_anno.equals(getAnno())) {
            if (dbStato.equals(DB_INSERIMENTO)) {
                prev.dbRicalcolaProgressivo(dbStato, this.texData.getText(), this.texNumePrev, texAnno, texSeri.getText(), id);
                prev.numero = new Integer(this.texNumePrev.getText()).intValue();
                id_modificato = true;
            } else {
                //controllo che se è un numero già presente non glielo faccio fare percè altrimenti sovrascrive una altra fattura
                sql = "select numero from test_ddt" + suff + "";
                sql += " where serie " + Db.pcW(this.prev.serie, "VARCHAR");
                sql += " and numero " + Db.pcW(texNumePrev.getText(), "NUMBER");
                sql += " and anno " + Db.pcW(getAnno(), "VARCHAR");
                ResultSet r = Db.openResultSet(sql);
                try {
                    if (r.next()) {
                        texData.setText(old_data);
                        JOptionPane.showMessageDialog(this, "Non puoi mettere questo numero e data, si sovrappongono ad un documento già presente !", "Attenzione", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            try {
                texAnno.setText(getAnno());
                prev.anno = Integer.parseInt(getAnno());
                prev.numero = Integer.parseInt(texNumePrev.getText());

                sql = "update righ_ddt" + suff + "";
                sql += " set anno = " + Db.pc(prev.anno, "NUMBER");
                sql += " , numero = " + Db.pc(prev.numero, "NUMBER");
                sql += " where serie " + Db.pcW(this.prev.serie, "VARCHAR");
                sql += " and numero " + Db.pcW(old_id, "NUMBER");
                sql += " and anno " + Db.pcW(old_anno, "VARCHAR");
                Db.executeSql(sql);

                sql = "update test_ddt" + suff + "";
                sql += " set anno = " + Db.pc(prev.anno, "NUMBER");
                sql += " , numero = " + Db.pc(prev.numero, "NUMBER");
                sql += " where serie " + Db.pcW(this.prev.serie, "VARCHAR");
                sql += " and numero " + Db.pcW(old_id, "NUMBER");
                sql += " and anno " + Db.pcW(old_anno, "VARCHAR");
                Db.executeSql(sql);

//                dati.dbChiaveValori.clear();
//                dati.dbChiaveValori.put("serie", prev.serie);
//                dati.dbChiaveValori.put("numero", prev.numero);
//                dati.dbChiaveValori.put("anno", prev.anno);

                //riassocio
                dbAssociaGrigliaRighe();

                doc.load(Db.INSTANCE, prev.numero, prev.serie, prev.anno, getTipoDoc(), id);
                ricalcolaTotali();

                anno_modificato = true;

                //vado ad aggiornare eventuali ddt o ordini legati
                sql = "update test_ordi";
                sql += " set doc_numero = " + Db.pc(prev.numero, "NUMBER");
                sql += " , anno = " + Db.pc(prev.anno, "NUMBER");
                sql += " where doc_serie " + Db.pcW(this.prev.serie, "VARCHAR");
                sql += " and doc_numero " + Db.pcW(old_id, "NUMBER");
                sql += " and doc_anno " + Db.pcW(String.valueOf(old_anno), "VARCHAR");
                sql += " and doc_tipo " + Db.pcW(String.valueOf(this.prev.tipoDocumento), "VARCHAR");
                Db.executeSql(sql);

                //vado ad aggiornare eventuali movimenti generati
                sql = "update movimenti_magazzino";
                sql += " set da_numero = " + Db.pc(prev.numero, "NUMBER");
                sql += ", da_anno = " + Db.pc(prev.anno, "NUMBER");
                sql += " where da_serie " + Db.pcW(this.prev.serie, "VARCHAR");
                sql += " and da_numero " + Db.pcW(old_id, "NUMBER");
                sql += " and da_anno " + Db.pcW(String.valueOf(old_anno), "VARCHAR");
                sql += " and da_tabella = 'test_ddt" + suff + "'";
                Db.executeSql(sql);

            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }//GEN-LAST:event_texDataFocusLost

    private void comClieItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comClieItemStateChanged
//        if (evt.getStateChange() == ItemEvent.SELECTED && !loading) {
//            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//            String sqlTmp = "SELECT note, note_automatiche as auto FROM clie_forn where codice = " + Db.pc(String.valueOf(comClie.getSelectedKey()), "NUMERIC");
//            ResultSet noteauto = Db.openResultSet(sqlTmp);
//            try {
//                if (noteauto.next()) {
//                    String auto = noteauto.getString("auto");
//                    String nota = noteauto.getString("note");
//                    if (auto != null && auto.equals("S")) {
//                        if (!texNote.getText().equals("") && !texNote.getText().equals(nota)) {
//                            this.texNote.setText(nota);
//                        } else {
//                            this.texNote.setText(noteauto.getString("note"));
//                        }
//                    }
//                }
//            } catch (SQLException ex) {
//                ex.printStackTrace();
//            }
//            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
//        }
    }//GEN-LAST:event_comClieItemStateChanged

    private void texScon3KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texScon3KeyReleased

        try {
            prev.sconto3 = Db.getDouble(this.texScon3.getText());
        } catch (Exception err) {

            //err.printStackTrace();
            prev.sconto3 = 0;
        }

        prev.dbRefresh();
    }//GEN-LAST:event_texScon3KeyReleased

    private void comClieKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_comClieKeyPressed

        // Add your handling code here:
        if (evt.getKeyCode() == evt.VK_ENTER) {
            this.recuperaDatiCliente();
        }

        //ricerca con F4
        if (evt.getKeyCode() == evt.VK_F4) {

            java.util.Hashtable colsWidthPerc = new java.util.Hashtable();
            colsWidthPerc.put("id", new Double(20));
            colsWidthPerc.put("ragione", new Double(40));
            colsWidthPerc.put("indi", new Double(40));

            String sql =
                    "select clie_forn.codice as id, clie_forn.ragione_sociale as ragione, clie_forn.indirizzo as indi from clie_forn " + "where clie_forn.ragione_sociale like '%" + Db.aa(this.comClie.getText()) + "%'" + " order by clie_forn.ragione_sociale";
            ResultSet resTemp = db.openResultSet(sql);

            try {

                if (resTemp.next() == true) {

                    frmDbListSmall temp = new frmDbListSmall(main.getPadre(), true, sql, this.texClie, 0, colsWidthPerc, 50, 50, 400, 300);
                    this.recuperaDatiCliente();
                    this.comClie.dbTrovaKey(texClie.getText());
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this, "Nessun cliente trovato");
                }
            } catch (Exception err) {
                err.printStackTrace();
                javax.swing.JOptionPane.showMessageDialog(this, "Errore nella ricerca cliente: " + err.toString());
            }
        }
    }//GEN-LAST:event_comClieKeyPressed

    private void texClieKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texClieKeyPressed

        // Add your handling code here:
        if (evt.getKeyCode() == evt.VK_ENTER) {
            this.recuperaDatiCliente();
        }
    }//GEN-LAST:event_texClieKeyPressed

    private void comClieFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_comClieFocusLost
        if (comClie.getSelectedIndex() != comClieSel_old) {
            this.recuperaDatiCliente();
            ricalcolaTotali();
        }
    }//GEN-LAST:event_comClieFocusLost

    private void texClieFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texClieFocusLost
        ricalcolaTotali();
    }//GEN-LAST:event_texClieFocusLost

    private void texSpeseIncassoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texSpeseIncassoKeyReleased

        try {

            //prev.speseVarie = Db.getDouble(this.texSpesVari.getText());
            prev.speseIncassoIva = Db.getDouble(this.texSpeseIncasso.getText());
        } catch (Exception err) {

            //err.printStackTrace();
            prev.speseIncassoIva = 0;
        }

        prev.dbRefresh();
    }//GEN-LAST:event_texSpeseIncassoKeyReleased

    private void texScon2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texScon2KeyReleased

        try {
            prev.sconto2 = Db.getDouble(this.texScon2.getText());
        } catch (Exception err) {

            //err.printStackTrace();
            prev.sconto2 = 0;
        }

        prev.dbRefresh();
    }//GEN-LAST:event_texScon2KeyReleased

    private void texScon1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texScon1KeyReleased

        try {
            prev.sconto1 = Db.getDouble(this.texScon1.getText());
        } catch (Exception err) {

            //err.printStackTrace();
            prev.sconto1 = 0;
        }

        //debug
        //System.out.println("sconto1:" + prev.sconto1 + " testo:" + this.texScon1.getText());
        prev.dbRefresh();
    }//GEN-LAST:event_texScon1KeyReleased

    private void texScon2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texScon2KeyPressed
            }//GEN-LAST:event_texScon2KeyPressed

    private void texScon1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texScon1KeyPressed
            }//GEN-LAST:event_texScon1KeyPressed

    private void texScon1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texScon1KeyTyped
            }//GEN-LAST:event_texScon1KeyTyped

    private void texScon2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texScon2FocusLost
        ricalcolaTotali();
    }//GEN-LAST:event_texScon2FocusLost

    private void texScon1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texScon1FocusLost
        ricalcolaTotali();
    }//GEN-LAST:event_texScon1FocusLost

    private void texScon1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texScon1ActionPerformed
        ricalcolaTotali();
    }//GEN-LAST:event_texScon1ActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened

        // Add your handling code here:
        this.griglia.resizeColumnsPerc(true);
    }//GEN-LAST:event_formInternalFrameOpened

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed

        //        tim.cancel();
        main.getPadre().closeFrame(this);
    }//GEN-LAST:event_formInternalFrameClosed

    private void grigliaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_grigliaMouseClicked

        // Add your handling code here:
        try {

            if (evt.getClickCount() == 2) {

                //modifico o la riga o la finestra
                popGrigModiActionPerformed(null);
            }
        } catch (Exception err) {
            javax.swing.JOptionPane.showMessageDialog(null, err.toString());
            err.printStackTrace();
        }

    }//GEN-LAST:event_grigliaMouseClicked

    private void popGrigElimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popGrigElimActionPerformed

        //elimino la riga
        this.griglia.dbDelete();
        griglia.dbRefresh();
        prev.dbRefresh();
        ricalcolaTotali();

    }//GEN-LAST:event_popGrigElimActionPerformed

    private void popGrigModiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popGrigModiActionPerformed

        //modifico la riga
        String codiceListino = "1";

        try {
            codiceListino = Db.lookUp(this.texClie.getText(), "codice", "clie_forn").getString("codice_listino");
        } catch (Exception err) {
            err.printStackTrace();
        }

        int codiceCliente = -1;

        if (this.texClie.getText().length() > 0) {

            try {
                codiceCliente = Integer.parseInt(texClie.getText());
            } catch (Exception err) {
                err.printStackTrace();
            }
        }

//        java.util.prefs.Preferences preferences = java.util.prefs.Preferences.userNodeForPackage(main.class);
//        boolean multiriga = preferences.getBoolean("multiriga", false);
        boolean multiriga = main.fileIni.getValueBoolean("pref", "multiriga", false);

//        if (multiriga == false) {
//
//            frmNuovRiga frm = new frmNuovRiga(this, tnxDbPanel.DB_MODIFICA, this.texSeri.getText(), Integer.valueOf(this.texNumePrev.getText()).intValue(), "P", Integer.valueOf(this.griglia.getValueAt(griglia.getSelectedRow(), 3).toString()).intValue(), Integer.valueOf(this.texAnno.getText()).intValue(), codiceListino, codiceCliente);
//            temp.openFrame(frm, 600, 350, 100, 100);
//            frm.setStato();
//        } else {
        try {
            JInternalFrame frm = null;
            int w = 650;
            int h = 400;
            int top = 100;
            int left = 100;
            if (main.getPersonalContain("frajor")) {
                frmNuovRigaDescrizioneMultiRigaNewFrajor temp_form = new frmNuovRigaDescrizioneMultiRigaNewFrajor(this, tnxDbPanel.DB_MODIFICA, this.texSeri.getText(), Integer.valueOf(this.texNumePrev.getText()).intValue(), "P", Integer.valueOf(this.griglia.getValueAt(griglia.getSelectedRow(), 3).toString()), Integer.valueOf(this.texAnno.getText()).intValue(), codiceListino, codiceCliente);
                temp_form.setStato();
                w = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRigaFrajor_w", "760"));
                h = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRigaFrajor_h", "660"));
                top = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRigaFrajor_top", "100"));
                left = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRigaFrajor_left", "100"));
                frm = temp_form;
            } else {
                frmNuovRigaDescrizioneMultiRigaNew temp_form = new frmNuovRigaDescrizioneMultiRigaNew(this, tnxDbPanel.DB_MODIFICA, this.texSeri.getText(), Integer.valueOf(this.texNumePrev.getText()).intValue(), "P", Integer.valueOf(this.griglia.getValueAt(griglia.getSelectedRow(), 3).toString()), Integer.valueOf(this.texAnno.getText()).intValue(), codiceListino, codiceCliente);
                temp_form.setStato();
                w = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRiga_w", "980"));
                h = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRiga_h", "460"));
                top = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRiga_top", "100"));
                left = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRiga_left", "100"));
                frm = temp_form;
            }

            main.getPadre().openFrame(frm, w, h, top, left);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        }
    }//GEN-LAST:event_popGrigModiActionPerformed

    private void butNuovArtiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butNuovArtiActionPerformed

        String codiceListino = "1";

        try {
            codiceListino = Db.lookUp(this.texClie.getText(), "codice", "clie_forn").getString("codice_listino");
        } catch (Exception err) {
            System.out.println("butNuovArtiActionPerformed:" + err.toString());
        }

        int codiceCliente = -1;

        if (this.texClie.getText().length() > 0) {

            try {
                codiceCliente = Integer.parseInt(texClie.getText());
            } catch (Exception err) {
                err.printStackTrace();
            }
        }

//        java.util.prefs.Preferences preferences = java.util.prefs.Preferences.userNodeForPackage(main.class);
//        boolean multiriga = preferences.getBoolean("multiriga", false);
        boolean multiriga = main.fileIni.getValueBoolean("pref", "multiriga", false);

//        if (multiriga == false) {
//            frmNuovRiga frm = new frmNuovRiga(this, this.dati.DB_INSERIMENTO, this.texSeri.getText(), Integer.valueOf(this.texNumePrev.getText()).intValue(), "P", 0, Integer.valueOf(this.texAnno.getText()).intValue(), codiceListino, codiceCliente);
//            temp.openFrame(frm, 600, 350, 100, 100);
//            frm.setStato();
//        } else {
        try {
            JInternalFrame frm = null;
            int w = 650;
            int h = 400;
            int top = 100;
            int left = 100;
            if (main.getPersonalContain("frajor")) {
                frmNuovRigaDescrizioneMultiRigaNewFrajor temp_form = new frmNuovRigaDescrizioneMultiRigaNewFrajor(this, this.dati.DB_INSERIMENTO, this.texSeri.getText(), Integer.valueOf(this.texNumePrev.getText()).intValue(), "P", 0, Integer.valueOf(this.texAnno.getText()).intValue(), codiceListino, codiceCliente);
                w = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRigaFrajor_w", "700"));
                h = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRigaFrajor_h", "660"));
                top = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRigaFrajor_top", "100"));
                left = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRigaFrajor_left", "100"));
                temp_form.setStato();
                frm = temp_form;
            } else {
                frmNuovRigaDescrizioneMultiRigaNew temp_form = new frmNuovRigaDescrizioneMultiRigaNew(this, this.dati.DB_INSERIMENTO, this.texSeri.getText(), Integer.valueOf(this.texNumePrev.getText()).intValue(), "P", 0, Integer.valueOf(this.texAnno.getText()).intValue(), codiceListino, codiceCliente);
                w = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRiga_w", "980"));
                h = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRiga_h", "460"));
                top = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRiga_top", "100"));
                left = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRiga_left", "100"));
                temp_form.setStato();
                frm = temp_form;
                temp_form.texProvvigione.setText(texProvvigione.getText());
            }

            main.getPadre().openFrame(frm, w, h, top, left);

        } catch (Exception e) {
            e.printStackTrace();
        }
//        }

        /*      //fisso serie e numero
        this.serieRigh.setText(this.prev.serie);
        this.numeroRigh.setText(String.valueOf(this.prev.numero));
        this.codice_articolo.requestFocus();
        java.sql.Statement stat;
        ResultSet resu;
        //apre il resultset per ultimo +1
        try {
        stat = db.conn.createStatement();
        String sql = "select riga from righ_ddt" +
        " where serie = " + db.pc(this.prev.serie,"VARCHAR") +
        " and numero = " + db.pc(String.valueOf(this.prev.numero),"INTEGER") +
        " order by riga desc limit 1";
        resu = stat.executeQuery(sql);
        if(resu.next()==true) {
        this.riga.setText(String.valueOf(resu.getInt(1)+1));
        } else {
        this.riga.setText("1");
        }
        } catch (Exception err) {
        err.printStackTrace();
        javax.swing.JOptionPane.showMessageDialog(null,err.toString());
        }
         */
    }//GEN-LAST:event_butNuovArtiActionPerformed

    private void butUndoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butUndoActionPerformed
        if (block_aggiornareProvvigioni) return;
        
        if (evt != null) {
            if (!SwingUtils.showYesNoMessage(main.getPadreWindow(), "Sicuro di annullare le modifiche ?")) {
                return;
            }
        }

        // Add your handling code here:
        if (dbStato.equals(this.DB_INSERIMENTO)) {

            //elimino la testata inserita e poi annullata
            String sql = "delete from test_ddt" + suff + "";
            sql += " where serie = " + Db.pc(String.valueOf(this.prev.serie), "VARCHAR");
            sql += " and numero = " + Db.pc(String.valueOf(this.prev.numero), "NUMBER");
            //sql += " and stato = " + Db.pc(String.valueOf(this.prev.stato), "VARCHAR");
            sql += " and anno = " + Db.pc(String.valueOf(this.prev.anno), "INTEGER");
            Db.executeSql(sql);
            sql = "delete from righ_ddt" + suff + "";
            sql += " where serie = " + Db.pc(String.valueOf(this.prev.serie), "VARCHAR");
            sql += " and numero = " + Db.pc(String.valueOf(this.prev.numero), "NUMBER");
            //sql += " and stato = " + Db.pc(String.valueOf(this.prev.stato), "VARCHAR");
            sql += " and anno = " + Db.pc(String.valueOf(this.prev.anno), "INTEGER");
            Db.executeSql(sql);
        } else if (dbStato.equals(this.DB_MODIFICA)) {

            System.out.println("annulla da modifica, elimino " + prev.serie + "/" + prev.numero + "/" + prev.anno + " e rimetto da temp " + serie_originale + "/" + numero_originale + "/" + anno_originale);

            //rimetto numero originale
            sql = "update test_ddt" + suff + "";
            sql += " set numero = " + Db.pc(numero_originale, "NUMBER");
            sql += " , anno = " + Db.pc(anno_originale, "NUMBER");
            sql += " where id = " + this.id;
            Db.executeSql(sql);

            //elimino le righe inserite
            sql = "delete from righ_ddt" + suff + "";
            sql += " where serie " + Db.pcW(this.prev.serie, "VARCHAR");
            sql += " and numero " + Db.pcW(prev.numero, "NUMBER");
            sql += " and anno " + Db.pcW(String.valueOf(this.prev.anno), "VARCHAR");
            Db.executeSql(sql);

            //e rimetto quelle da temp
            sql = "insert into righ_ddt" + suff + " (" + Db.getFieldList("righ_ddt" + suff + "", false) + ")";
            sql += " select " + Db.getFieldList("righ_ddt" + suff + "_temp", true);
            sql += " from righ_ddt" + suff + "_temp";
            sql += " where serie = " + Db.pc(String.valueOf(serie_originale), "VARCHAR");
            sql += " and numero = " + Db.pc(numero_originale, "NUMBER");
            sql += " and anno = " + Db.pc(String.valueOf(anno_originale), "VARCHAR");
            sql += " and username = '" + main.login + "'";
            Db.executeSqlDialogExc(sql, true);

//            //vado ad aggiornare eventuali ddt o ordini legati
//            sql = "update test_ordi";
//            sql += " set doc_numero = " + Db.pc(numero_originale, "NUMBER");
//            sql += " where doc_serie " + Db.pcW(this.prev.serie, "VARCHAR");
//            sql += " and doc_numero " + Db.pcW(prev.numero, "NUMBER");
//            sql += " and doc_anno " + Db.pcW(String.valueOf(this.prev.anno), "VARCHAR");
//            sql += " and doc_tipo " + Db.pcW(String.valueOf(this.prev.tipoDocumento), "VARCHAR");
//            Db.executeSql(sql);

            //rimetto numero originale su eventuali movimenti
            sql = "update movimenti_magazzino";
            sql += " set da_numero = " + Db.pc(numero_originale, "NUMBER");
            sql += ", da_anno = " + Db.pc(anno_originale, "NUMBER");
            sql += " where da_serie " + Db.pcW(prev.serie, "VARCHAR");
            sql += " and da_numero " + Db.pcW(prev.numero, "NUMBER");
            sql += " and da_anno " + Db.pcW(prev.anno, "VARCHAR");
            sql += " and da_tabella = 'test_ddt" + suff + "'";
            Db.executeSql(sql);

        }

        if (from != null) {
            this.from.dbRefresh();
        }
        this.dispose();
    }//GEN-LAST:event_butUndoActionPerformed

    public void annulla() {
        butUndoActionPerformed(null);
    }

    private void butSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveActionPerformed
        if (block_aggiornareProvvigioni) return;

        if (!controlloCampi()) {
            return;
        }

        //provo a non ricalcolare
        //prev.dbRicalcolaProgressivo(dbStato, this.texData.getText(), this.texNumePrev, this.texAnno);

        //sposto i totali di modo che li salvi
        this.texTota1.setText(this.texTota.getText());
        this.texTotaImpo1.setText(this.texTotaImpo.getText());
        this.texTotaIva1.setText(this.texTotaIva.getText());

        dati.setCampiAggiuntivi(new Hashtable());
        dati.getCampiAggiuntivi().put("evaso", Db.pc(getStatoEvaso(), Types.VARCHAR));
        dati.getCampiAggiuntivi().put("sconto", Db.pc(doc.getSconto(), Types.DOUBLE));
        dati.getCampiAggiuntivi().put("totale_imponibile_pre_sconto", Db.pc(doc.totaleImponibilePreSconto, Types.DOUBLE));
        dati.getCampiAggiuntivi().put("totale_ivato_pre_sconto", Db.pc(doc.totaleIvatoPreSconto, Types.DOUBLE));

        this.dati.dbSave();

        //forzo gli id padre
        String serie = this.texSeri.getText();
        Integer numero = Integer.parseInt(texNumePrev.getText());
        Integer anno = Integer.parseInt(texAnno.getText());
        String sql = "UPDATE righ_ddt r left join test_ddt t on r.serie = t.serie and r.anno = t.anno and r.numero = t.numero set r.id_padre = t.id where t.serie = " + Db.pc(serie, Types.VARCHAR) + " and t.numero = " + numero + " and t.anno = " + anno;
        try {
            DbUtils.tryExecQuery(Db.getConn(), sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (this.prev.generaMovimentiMagazzino() == false) {
            javax.swing.JOptionPane.showMessageDialog(this, "Errore nella generazione dei movimenti di magazzino", "Errore", javax.swing.JOptionPane.ERROR_MESSAGE);
        }

        //aggiorno eventuali documenti collegati (ordini, ddt)
        InvoicexUtil.aggiornaRiferimentoDocumenti(getTipoDoc(), id);
        
        try {
            if (from != null) {
                this.from.dbRefresh();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        main.events.fireInvoicexEvent(new InvoicexEvent(this, InvoicexEvent.TYPE_SAVE));
        this.dispose();
    }//GEN-LAST:event_butSaveActionPerformed

    private boolean controlloCampi() {

        //controllo cliente
        //li recupero dal cliente
        ResultSet tempClie;
        String sql = "select * from clie_forn";
        sql += " where codice = " + Db.pc(this.texClie.getText(), "NUMERIC");
        tempClie = Db.openResultSet(sql);

        try {

            if (tempClie.next() != true) {
                texCliente.requestFocus();
                javax.swing.JOptionPane.showMessageDialog(this, "Il codice cliente specificato non esiste in anagrafica !");
                return false;
            }
        } catch (Exception err) {
            err.printStackTrace();
        }

        return true;
    }

private void butNuovArti1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butNuovArti1ActionPerformed
    doc.setPrezziIvati(prezzi_ivati.isSelected());
    doc.setSconto(Db.getDouble(texSconto.getText()));
    doc.calcolaTotali();
    System.out.println("peso:" + doc.totalePeso);

    String dbSerie = this.prev.serie;
    int dbNumero = this.prev.numero;
    int dbAnno = this.prev.anno;
    int riga = 0;

    //apre il resultset per ultimo +1
    Statement stat = null;
    ResultSet resu = null;
    try {
        stat = Db.getConn().createStatement();
        String sql = "select riga from righ_ddt" + suff + "";
        sql += " where serie = " + Db.pc(dbSerie, "VARCHAR");
        sql += " and numero = " + dbNumero;
        sql += " and anno = " + dbAnno;
        sql += " order by riga desc limit 1";
        resu = stat.executeQuery(sql);
        if (resu.next() == true) {
            riga = resu.getInt(1) + 1;
        } else {
            riga = 1;
        }
    } catch (Exception err) {
        err.printStackTrace();
    } finally {
        try {
            stat.close();
        } catch (Exception ex1) {
            ex1.printStackTrace();
        }
    }
    sql = "insert into righ_ddt" + suff + " (serie, numero, anno, riga, codice_articolo, descrizione, id_padre) values (";
    sql += db.pc(dbSerie, "VARCHAR");
    sql += ", " + db.pc(dbNumero, "NUMBER");
    sql += ", " + db.pc(dbAnno, "NUMBER");
    sql += ", " + db.pc(riga, "NUMBER");
    sql += ", ''";
    if (main.getPersonalContain("litri")) {
        sql += ", '" + it.tnx.Util.format2Decimali(doc.totalePeso) + " Litri Totali'";
    } else {
        sql += ", 'Peso totale Kg. " + it.tnx.Util.format2Decimali(doc.totalePeso) + "'";
    }
    sql += ", " + Db.pc(id, Types.INTEGER);
    sql += ")";
    Db.executeSql(sql);

    griglia.dbRefresh();
}//GEN-LAST:event_butNuovArti1ActionPerformed

private void comClieFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_comClieFocusGained
    comClieSel_old = comClie.getSelectedIndex();
}//GEN-LAST:event_comClieFocusGained

private void comClieDestFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_comClieDestFocusGained
    comClieDest_old = comClieDest.getSelectedIndex();
}//GEN-LAST:event_comClieDestFocusGained

private void popGridAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popGridAddActionPerformed
    int numCol = this.griglia.getColumnByName("riga");
    int numRiga = this.griglia.getSelectedRow();
    int value = (Integer) this.griglia.getValueAt(numRiga, numCol);

    String codiceListino = "1";

    try {
        codiceListino = Db.lookUp(this.texClie.getText(), "codice", "clie_forn").getString("codice_listino");
    } catch (Exception err) {
        System.out.println("butNuovArtiActionPerformed:" + err.toString());
    }

    int codiceCliente = -1;

    if (this.texClie.getText().length() > 0) {

        try {
            codiceCliente = Integer.parseInt(texClie.getText());
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

//        java.util.prefs.Preferences preferences = java.util.prefs.Preferences.userNodeForPackage(main.class);
//        boolean multiriga = preferences.getBoolean("multiriga", false);
    boolean multiriga = main.fileIni.getValueBoolean("pref", "multiriga", false);

//    if (multiriga == false) {
//        frmNuovRiga frm = new frmNuovRiga(this, this.dati.DB_INSERIMENTO, this.texSeri.getText(), Integer.valueOf(this.texNumePrev.getText()).intValue(), "P", value, Integer.valueOf(this.texAnno.getText()).intValue(), codiceListino, codiceCliente);
//        temp.openFrame(frm, 600, 350, 100, 100);
//        frm.setStato();
//    } else {
    try {
        JInternalFrame frm = null;
        int w = 650;
        int h = 400;
        int top = 100;
        int left = 100;
        if (main.getPersonalContain("frajor")) {
            frmNuovRigaDescrizioneMultiRigaNewFrajor temp_form = new frmNuovRigaDescrizioneMultiRigaNewFrajor(this, this.dati.DB_INSERIMENTO, this.texSeri.getText(), Integer.valueOf(this.texNumePrev.getText()).intValue(), "P", value, Integer.valueOf(this.texAnno.getText()).intValue(), codiceListino, codiceCliente);
            temp_form.setStato();
            w = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRigaFrajor_w", "700"));
            h = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRigaFrajor_h", "660"));
            top = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRigaFrajor_top", "100"));
            left = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRigaFrajor_left", "100"));
            frm = temp_form;
        } else {
            frmNuovRigaDescrizioneMultiRigaNew temp_form = new frmNuovRigaDescrizioneMultiRigaNew(this, this.dati.DB_INSERIMENTO, this.texSeri.getText(), Integer.valueOf(this.texNumePrev.getText()).intValue(), "P", value, Integer.valueOf(this.texAnno.getText()).intValue(), codiceListino, codiceCliente);
            temp_form.setStato();
            w = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRiga_w", "980"));
            h = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRiga_h", "460"));
            top = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRiga_top", "100"));
            left = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRiga_left", "100"));
            frm = temp_form;
            temp_form.texProvvigione.setText(texProvvigione.getText());
        }

        main.getPadre().openFrame(frm, w, h, top, left);
    } catch (Exception e) {
        e.printStackTrace();
    }

//    }
}//GEN-LAST:event_popGridAddActionPerformed

private void formVetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {//GEN-FIRST:event_formVetoableChange
    if (evt.getPropertyName().equals(IS_CLOSED_PROPERTY)) {
        boolean changed = ((Boolean) evt.getNewValue()).booleanValue();
        if (changed) {
            if (dati.dbCheckModificati() || (doc.getTotale() != this.totaleIniziale)) {
                FxUtils.fadeBackground(butSave, Color.RED);
                int confirm = JOptionPane.showOptionDialog(this,
                        "<html><b>Chiudi " + getTitle() + "?</b><br>Hai fatto delle modifiche e così verranno <b>perse</b> !<br>Per salvarle devi cliccare sul pulsante <b>Salva</b> in basso a sinistra<br>",
                        "Conferma chiusura",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null, null, null);
                if (confirm == 0) {
                } else {
                    throw new PropertyVetoException("Cancelled", null);
                }
            }
            if (dbStato.equalsIgnoreCase(this.DB_INSERIMENTO)) {
                butUndoActionPerformed(null);
            }
        }

    }
}//GEN-LAST:event_formVetoableChange

private void texDataOraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texDataOraActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_texDataOraActionPerformed

private void butImportRigheActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butImportRigheActionPerformed
    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    JFileChooser fileChoose = new JFileChooser(new File(System.getProperty("user.home") + File.separator + ".invoicex" + File.separator + "Export" + File.separator));
    FileFilter filter1 = new FileFilter() {

        public boolean accept(File pathname) {
            if (pathname.getAbsolutePath().endsWith(".csv")) {
                return true;
            } else if (pathname.isDirectory()) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public String getDescription() {
            return "File CSV (*.csv)";
        }
    };

    fileChoose.addChoosableFileFilter(filter1);
    fileChoose.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

    int ret = fileChoose.showOpenDialog(this);

    if (ret == javax.swing.JFileChooser.APPROVE_OPTION) {
        ret = JOptionPane.showConfirmDialog(this, "Vuoi selezionare un listino prezzi esistente?", "Import CSV", JOptionPane.YES_NO_CANCEL_OPTION);
        String nomeListino = "";
        if (ret == JOptionPane.CANCEL_OPTION) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            return;
        } else if (ret == JOptionPane.YES_OPTION) {
            JDialogChooseListino dialog = new JDialogChooseListino(main.getPadre(), true);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
            nomeListino = dialog.listinoChoose;

            if (nomeListino.equals("")) {
                nomeListino = "FromFile";
                JOptionPane.showMessageDialog(this, "Non hai scelto nessun listino. Il file verrà caricato con i prezzi interni al file stesso", "Errore Selezione", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            nomeListino = "FromFile";
        }
        try {
            //apro il file
            File f = fileChoose.getSelectedFile();
            String serie = texSeri.getText();
            int numero = Integer.valueOf(this.texNumePrev.getText()).intValue();
            int anno = Integer.valueOf(this.texAnno.getText()).intValue();
            int idPadre = 0;
            String sql = "SELECT id FROM test_ordi WHERE serie = '" + serie + "' AND numero = (" + numero + ") AND anno = (" + anno + ")";
            ResultSet testa = Db.openResultSet(sql);
            if (testa.next()) {
                idPadre = testa.getInt("id");
            } else {
                idPadre = -1;
            }
            InvoicexUtil.importCSV(getTipoDoc(), f, serie, numero, anno, idPadre, nomeListino);
            InvoicexUtil.aggiornaTotaliRighe(getTipoDoc(), idPadre, prezzi_ivati_virtual.isSelected());
            griglia.dbRefresh();
            ricalcolaTotali();
            JOptionPane.showMessageDialog(this, "Righe caricate correttamente", "Esecuzione terminata", JOptionPane.INFORMATION_MESSAGE);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_butImportRigheActionPerformed

private void grigliaMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_grigliaMousePressed
    if (evt.isPopupTrigger()) {
        int numRow = this.griglia.getSelectedRow();
        if (numRow != 0) {
            this.popGridAdd.setText("Inserisci nuova fra riga " + numRow + " e riga " + (numRow + 1));
        } else {
            this.popGridAdd.setText("Inserisci nuova riga all'inizio");
        }
        popGrig.show(griglia, evt.getX(), evt.getY());
    }
}//GEN-LAST:event_grigliaMousePressed

private void grigliaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_grigliaMouseReleased
    if (evt.isPopupTrigger()) {
        int numRow = this.griglia.getSelectedRow();
        if (numRow != 0) {
            this.popGridAdd.setText("Inserisci nuova fra riga " + numRow + " e riga " + (numRow + 1));
        } else {
            this.popGridAdd.setText("Inserisci nuova riga all'inizio");
        }
        popGrig.show(griglia, evt.getX(), evt.getY());
    }
}//GEN-LAST:event_grigliaMouseReleased

private void comAgenteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comAgenteActionPerformed
}//GEN-LAST:event_comAgenteActionPerformed

private void comAgenteFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_comAgenteFocusLost
    InvoicexUtil.controlloProvvigioniAutomatiche(comAgente, texProvvigione, texScon1, this, acquisto ? null : cu.toInteger(texClie.getText()));
}//GEN-LAST:event_comAgenteFocusLost

private void texProvvigioneFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texProvvigioneFocusLost
    if (!StringUtils.equals(texProvvigione.getName(), texProvvigione.getText()) && griglia.getRowCount() > 0) {
        aggiornareProvvigioni();
    }
}//GEN-LAST:event_texProvvigioneFocusLost

private void texProvvigioneFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texProvvigioneFocusGained
    texProvvigione.setName(texProvvigione.getText());
}//GEN-LAST:event_texProvvigioneFocusGained

private void texAnnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texAnnoActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_texAnnoActionPerformed

private void listino_consigliatoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listino_consigliatoActionPerformed
}//GEN-LAST:event_listino_consigliatoActionPerformed

private void popDuplicaRigheActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popDuplicaRigheActionPerformed
    if (griglia.getRowCount() <= 0) {
        SwingUtils.showErrorMessage(this, "Seleziona una riga prima!");
        return;
    }

    String sql;
    String sqlC = "";
    String sqlV = "";

    int numDup = griglia.getSelectedRows().length;
    int res;
    //chiedo conferma per eliminare il documento
    if (numDup > 1) {
        String msg = "Sicuro di voler duplicare " + numDup + " Righe ?";
        res = JOptionPane.showConfirmDialog(this, msg);
    } else {
        res = JOptionPane.OK_OPTION;
    }

    if (res == JOptionPane.OK_OPTION) {
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        for (int sel : griglia.getSelectedRows()) {

            int dbId = Integer.parseInt(String.valueOf(griglia.getValueAt(sel, griglia.getColumnByName("id"))));

            //cerco ultimo numero ordine
            int newNumero = 1;
            sqlC = "";
            sqlV = "";
            

            try {
                int dbIdPadre = (Integer) DbUtils.getObject(Db.getConn(), "SELECT id_padre FROM righ_ddt" + suff + " WHERE id = " + Db.pc(dbId, Types.INTEGER));
                sql = "SELECT MAX(riga) as maxnum FROM righ_ddt" + suff + " WHERE id_padre = " + Db.pc(dbIdPadre, Types.INTEGER);
                ResultSet tempUltimo = Db.openResultSet(sql);
                if (tempUltimo.next() == true) {
                    newNumero = tempUltimo.getInt("maxnum") + 1;
                }
            } catch (Exception err) {
                err.printStackTrace();
            }

            sql = "select * from righ_ddt" + suff + " where id = " + Db.pc(dbId, Types.INTEGER);
            ResultSet tempPrev2 = Db.openResultSet(sql);
            try {
                ResultSetMetaData metaPrev2 = tempPrev2.getMetaData();

                while (tempPrev2.next() == true) {
                    sqlC = "";
                    sqlV = "";
                    for (int i = 1; i <= metaPrev2.getColumnCount(); i++) {
                        if (!metaPrev2.getColumnName(i).equalsIgnoreCase("id")) {
                            if (metaPrev2.getColumnName(i).equalsIgnoreCase("riga")) {
                                sqlC += "riga";
                                sqlV += Db.pc(newNumero, metaPrev2.getColumnType(i));
                            } else {
                                sqlC += metaPrev2.getColumnName(i);
                                sqlV += Db.pc(tempPrev2.getObject(i), metaPrev2.getColumnType(i));
                            }
                            if (i != metaPrev2.getColumnCount()) {
                                sqlC += ",";
                                sqlV += ",";
                            }
                        }
                    }
                    sql = "insert into righ_ddt" + suff + " ";
                    sql += "(" + sqlC + ") values (" + sqlV + ")";
                    System.out.println("duplica righe:" + sql);
                    Db.executeSql(sql);
                }
            } catch (Exception err) {
                err.printStackTrace();
            }

        }
        griglia.dbRefresh();
        this.ricalcolaTotali();
        
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
}//GEN-LAST:event_popDuplicaRigheActionPerformed

private void prezzi_ivati_virtualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prezzi_ivati_virtualActionPerformed
    prezzi_ivati.setSelected(prezzi_ivati_virtual.isSelected());
    InvoicexUtil.aggiornaTotaliRighe(Db.TIPO_DOCUMENTO_FATTURA, this.id, prezzi_ivati_virtual.isSelected());
    dbAssociaGrigliaRighe();
    ricalcolaTotali();
}//GEN-LAST:event_prezzi_ivati_virtualActionPerformed

private void texScontoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texScontoKeyPressed

}//GEN-LAST:event_texScontoKeyPressed

private void texScontoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texScontoKeyReleased
    double valore = CastUtils.toDouble0(texSconto.getText());
    if (valore < 0) {
        valore = Math.abs(valore);
        texSconto.setText(FormatUtils.formatEuroIta(valore));
    }
    ricalcolaTotali();
}//GEN-LAST:event_texScontoKeyReleased

    private void apriclientiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_apriclientiActionPerformed
        //    alRicercaCliente.showHints();
        if (texCliente.getText().trim().length() == 0) {
            al_clifor.showHints2();
            al_clifor.updateHints(null);
            al_clifor.showHints2();
        } else {
            al_clifor.showHints();
        }
        //    al_clifor.showHints();
    }//GEN-LAST:event_apriclientiActionPerformed

    private void butAddClieActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butAddClieActionPerformed

        frmClie frameAggiungiCliente = new frmClie();

        //frameAggiungiCliente.setBounds(100,100,300,300);
        //frameAggiungiCliente.show();
        if (main.getPersonal().equals(main.PERSONAL_CUCINAIN)) {
            main.getPadre().openFrame(frameAggiungiCliente, 450, 350);
        } else {
            main.getPadre().openFrame(frameAggiungiCliente, 750, 620);
        }

        frameAggiungiCliente.addNew(this.comClie);
    }//GEN-LAST:event_butAddClieActionPerformed

    private void butPdfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butPdfActionPerformed
        butStampaActionPerformed(new ActionEvent(this, 0, "pdf"));
    }//GEN-LAST:event_butPdfActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton apriclienti;
    private javax.swing.JButton butAddClie;
    private javax.swing.JButton butImportRighe;
    private javax.swing.JButton butNuovArti;
    private javax.swing.JButton butNuovArti1;
    private javax.swing.JButton butNuovoAnimale;
    private javax.swing.JButton butPdf;
    private javax.swing.JButton butPrezziPrec;
    private javax.swing.JButton butSave;
    private javax.swing.JButton butStampa;
    public javax.swing.JButton butUndo;
    private tnxbeans.tnxComboField comAgente;
    private tnxbeans.tnxComboField comAspettoEsterioreBeni;
    private tnxbeans.tnxComboField comCausaleTrasporto;
    private tnxbeans.tnxComboField comClie;
    private tnxbeans.tnxComboField comClieDest;
    private tnxbeans.tnxComboField comMezzoTrasporto;
    private tnxbeans.tnxComboField comPaese;
    private tnxbeans.tnxComboField comPaga;
    private tnxbeans.tnxComboField comPorto;
    private tnxbeans.tnxComboField comVettori;
    private tnxbeans.tnxDbPanel dati;
    private tnxbeans.tnxDbPanel datiOpzioni;
    private tnxbeans.tnxDbPanel datiRighe;
    public tnxbeans.tnxDbGrid griglia;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel113;
    private javax.swing.JLabel jLabel114;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel151;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel labAgente;
    private javax.swing.JLabel labPercentoProvvigione;
    private javax.swing.JLabel labProvvigione;
    private javax.swing.JLabel labRiferimento;
    private javax.swing.JLabel labScon1;
    private javax.swing.JLabel labScon10;
    private javax.swing.JLabel labScon11;
    private javax.swing.JLabel labScon12;
    private javax.swing.JLabel labScon13;
    private javax.swing.JLabel labScon14;
    private javax.swing.JLabel labScon15;
    private javax.swing.JLabel labScon16;
    private javax.swing.JLabel labScon17;
    private javax.swing.JLabel labScon2;
    private javax.swing.JLabel labScon21;
    private javax.swing.JLabel labScon3;
    private javax.swing.JLabel labScon4;
    private javax.swing.JLabel labScon5;
    private javax.swing.JLabel labScon6;
    private javax.swing.JLabel labScon7;
    private javax.swing.JLabel labScon8;
    private javax.swing.JLabel labScon9;
    private tnxbeans.tnxComboField listino_consigliato;
    private javax.swing.JPanel panDati;
    private javax.swing.JMenuItem popDuplicaRighe;
    private javax.swing.JMenuItem popGridAdd;
    private javax.swing.JPopupMenu popGrig;
    private javax.swing.JMenuItem popGrigElim;
    private javax.swing.JMenuItem popGrigModi;
    public tnxbeans.tnxCheckBox prezzi_ivati;
    private javax.swing.JCheckBox prezzi_ivati_virtual;
    private tnxbeans.tnxCheckBox stampa_prezzi;
    private javax.swing.JComboBox stato_evasione;
    private tnxbeans.tnxTextField texAnno;
    public tnxbeans.tnxTextField texClie;
    private tnxbeans.tnxTextField texClieDest;
    public javax.swing.JTextField texCliente;
    private tnxbeans.tnxTextField texData;
    private tnxbeans.tnxTextField texDataOra;
    private tnxbeans.tnxTextField texDestCap;
    private tnxbeans.tnxTextField texDestCellulare;
    private tnxbeans.tnxTextField texDestIndirizzo;
    private tnxbeans.tnxTextField texDestLocalita;
    private tnxbeans.tnxTextField texDestProvincia;
    private tnxbeans.tnxTextField texDestRagioneSociale;
    private tnxbeans.tnxTextField texDestTelefono;
    private tnxbeans.tnxMemoField texNote;
    private tnxbeans.tnxTextField texNumePrev;
    private tnxbeans.tnxTextField texNumeroColli;
    private tnxbeans.tnxTextField texNumeroColli1;
    private tnxbeans.tnxTextField texNumeroColli2;
    private tnxbeans.tnxTextField texProvvigione;
    private tnxbeans.tnxTextField texRiferimento;
    public tnxbeans.tnxTextField texScon1;
    private tnxbeans.tnxTextField texScon10;
    public tnxbeans.tnxTextField texScon2;
    public tnxbeans.tnxTextField texScon3;
    private tnxbeans.tnxTextField texScon4;
    private tnxbeans.tnxTextField texScon5;
    private tnxbeans.tnxTextField texScon6;
    private tnxbeans.tnxTextField texScon7;
    private tnxbeans.tnxTextField texScon8;
    private tnxbeans.tnxTextField texScon9;
    public tnxbeans.tnxTextField texSconto;
    private tnxbeans.tnxTextField texSeri;
    public tnxbeans.tnxTextField texSpeseIncasso;
    public tnxbeans.tnxTextField texSpeseTrasporto;
    private tnxbeans.tnxTextField texStat;
    public tnxbeans.tnxTextField texTota;
    private tnxbeans.tnxTextField texTota1;
    public tnxbeans.tnxTextField texTotaImpo;
    private tnxbeans.tnxTextField texTotaImpo1;
    public tnxbeans.tnxTextField texTotaIva;
    private tnxbeans.tnxTextField texTotaIva1;
    // End of variables declaration//GEN-END:variables

    void dbAssociaGrigliaRighe() {

        String campi = "serie,";
        campi += "numero,";
        campi += "anno,";
        campi += "riga,";
        campi += "stato,";
        campi += "codice_articolo as articolo,";
        campi += "descrizione,";
        campi += "um,";

//        campi += "REPLACE(REPLACE(REPLACE(FORMAT(quantita,2),'.','X'),',','.'),'X',',') AS quantita,";
//        campi += "prezzo ";

//        campi += "REPLACE(REPLACE(REPLACE(FORMAT(quantita,2),'.','X'),',','.'),'X',',') AS quantita,";
//        campi += "REPLACE(REPLACE(REPLACE(FORMAT(quantita_evasa,2),'.','X'),',','.'),'X',',') AS '" + getCampoQtaEvasa() + "',";
        
        campi += "quantita,";
        campi += "quantita_evasa AS '" + getCampoQtaEvasa() + "',";
        
        
        campi += "prezzo, ";
        campi += "sconto1, ";
        campi += "sconto2, ";
        campi += "iva, ";
        campi += "if(sconto1 != 0 and sconto2 != 0, CONCAT(cast(sconto1 as CHAR), '+' , cast(sconto2 as CHAR)), if(sconto1 != 0 and sconto2 = 0, cast(sconto1 as CHAR),if(sconto1 = 0 and sconto2 != 0, cast(sconto2 as CHAR), ''))) as Sconti,";
        campi += "(totale_imponibile) as Totale ";
        campi += ", (totale_ivato) as Ivato ";
//        campi += "(prezzo*quantita) - ((prezzo*quantita)*sconto1/100) - ( ((prezzo*quantita) - ((prezzo*quantita)*sconto1/100)) * sconto2 / 100) as Totale ";
        campi += ",id";
        campi += ",id_padre";
        
        if (main.isPluginContabilitaAttivo()) {
            campi += ", conto";
        }

//        String sql =
//                //" and anno = " + java.util.Calendar.getInstance().get(Calendar.YEAR) +
//                //"select " + campi + " from righ_ddt" + " where serie = " + db.pc(this.prev.serie, "VARCHAR") + " and numero = " + this.prev.numero + " and stato = " + db.pc(this.prev.stato, "VARCHAR") + " and anno = " + db.pc(this.prev.anno, "INTEGER") + " order by riga";
//                "select " + campi + " from righ_ddt" + suff + "" + " where serie = " + db.pc(this.prev.serie, "VARCHAR") + " and numero = " + this.prev.numero + " and anno = " + db.pc(this.prev.anno, "INTEGER") + " order by riga";
        String sql = "select " + campi + " from righ_ddt" + suff;
        sql += " where id_padre = " + id;
        sql += " order by riga";

        //debug
        //javax.swing.JOptionPane.showMessageDialog(null,sql);
        System.out.println("sql associa griglia:" + sql);

        griglia.colonneEditabiliByName = new String[]{getCampoQtaEvasa()};
        griglia.dbEditabile = true;

        this.griglia.dbOpen(db.getConn(), sql);
        griglia.getColumn("quantita").setCellRenderer(InvoicexUtil.getNumber0_5Renderer());
        griglia.getColumn(getCampoQtaEvasa()).setCellRenderer(InvoicexUtil.getNumber0_5Renderer());
        
        DecimalFormat df1 = new DecimalFormat("0.#####");
        griglia.getColumn(getCampoQtaEvasa()).setCellEditor(new EditorUtils.NumberEditor(new JTextField(), df1) {
                public Object getCellEditorValue() {
                    String text = ((JTextField)editorComponent).getText();
                    Double qta_evasa = CastUtils.toDouble0All(text);
                    System.out.println("text:" + text + " qta_evasa:" + qta_evasa);
                    return qta_evasa;
                }
        });
    }

    public void recuperaDatiCliente() {

        //li recupero dal cliente
        ResultSet tempClie;
        String sql = "select * from clie_forn";
        sql += " where codice = " + Db.pc(this.texClie.getText(), "NUMERIC");
        tempClie = Db.openResultSet(sql);

        try {
            if (tempClie.next() == true) {
                if (Db.nz(tempClie.getString("pagamento"), "").length() > 0) {
                    this.comPaga.dbTrovaRiga(Db.nz(tempClie.getString("pagamento"), ""));
                }

                if (tempClie.getInt("agente") >= 0) {
                    this.comAgente.dbTrovaKey(tempClie.getString("agente"));
                    comAgenteFocusLost(null);
                }

                if (Db.nz(tempClie.getString("opzione_prezzi_ddt"), "").equalsIgnoreCase("S")) {
                    stampa_prezzi.setSelected(true);
                } else {
                    stampa_prezzi.setSelected(false);
                }

                //carico sconti
                texScon1.setText(FormatUtils.formatPerc(tempClie.getObject("sconto1t"), true));
                texScon2.setText(FormatUtils.formatPerc(tempClie.getObject("sconto2t"), true));
                texScon3.setText(FormatUtils.formatPerc(tempClie.getObject("sconto3t"), true));

                //leggere listino del cliente per prezzi_ivati o meno
                boolean prezzi_ivati_b = false;
                try {
                    String prezzi_ivati_s = CastUtils.toString(DbUtils.getObject(Db.getConn(), "select l.prezzi_ivati from clie_forn c join tipi_listino l on c.codice_listino = l.codice where c.codice = " + Db.pc(this.texClie.getText(), "NUMERIC")));
                    if (prezzi_ivati_s.equalsIgnoreCase("S")) {
                        prezzi_ivati_b = true;
                    }
                } catch (Exception e) {
                    //prendo base da impostazioni
                    String prezzi_ivati_s = CastUtils.toString(DbUtils.getObject(Db.getConn(), "select l.prezzi_ivati from dati_azienda a join tipi_listino l on a.listino_base = l.codice"));
                    if (prezzi_ivati_s.equalsIgnoreCase("S")) {
                        prezzi_ivati_b = true;
                    }
                }
                if (prezzi_ivati_virtual.isSelected() != prezzi_ivati_b) {
                    prezzi_ivati_virtual.setSelected(prezzi_ivati_b);
                    prezzi_ivati.setSelected(prezzi_ivati_b);
                    prezzi_ivati_virtualActionPerformed(null);
                }

                //note automatiche
                String sqlTmp = "SELECT note, note_automatiche as auto FROM clie_forn where codice = " + Db.pc(String.valueOf(comClie.getSelectedKey()), "NUMERIC");
                ResultSet noteauto = Db.openResultSet(sqlTmp);
                try {
                    if (noteauto.next()) {
                        String auto = noteauto.getString("auto");
                        String nota = noteauto.getString("note");
                        if (auto != null && auto.equals("S")) {
                            if (!texNote.getText().equals("") && !texNote.getText().equals(nota)) {
                                this.texNote.setText(nota);
                            } else {
                                this.texNote.setText(noteauto.getString("note"));
                            }
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                
                InvoicexUtil.fireEvent(this, InvoicexEvent.TYPE_FRMTESTDDT_CARICA_DATI_CLIENTE, tempClie);                

            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "Il codice cliente specificato non esiste in anagrafica !");
            }
        } catch (Exception err) {
            err.printStackTrace();
        }

    }

    private void showPrezziFatture() {
        frmPrezziFatturePrecedenti form = new frmPrezziFatturePrecedenti(Integer.parseInt(this.texClie.getText().toString()), null, getTipoDoc());
        main.getPadre().openFrame(form, 450, 500, this.getY() + 50, this.getX() + this.getWidth() - 200);
    }

    public void confermaAnimale(Animale animale) {
        System.out.println("aggiugnere:" + animale);
        animale.saveAnimale();
        this.griglia.dbRefresh();
    }

    private void visualizzaTarga() {

//        if (Db.nz(this.comCausaleTrasporto.getSelectedItem(), "").toString().equalsIgnoreCase("TENTATA VENDITA")) {
//            this.texClie.setText("");
//            this.texClie.setEnabled(false);
//            this.comClie.setSelectedItem(null);
//            this.comClie.setEnabled(false);
//            this.comClie.setLocked(true);
//
//            if (this.texTarga.getText().trim().length() == 0) {
//                this.texTarga.setText(main.getTargaStandard());
//            }
//
//            this.labTarga.setVisible(true);
//            this.texTarga.setVisible(true);
//            this.labRiferimento.setVisible(false);
//            this.texRiferimento.setVisible(false);
//        } else {
        this.comClie.setEnabled(true);
        this.comClie.setLocked(false);
        this.texClie.setEnabled(true);
        //this.labTarga.setVisible(false);
        //this.texTarga.setVisible(false);
        this.labRiferimento.setVisible(true);
        this.texRiferimento.setVisible(true);
//        }

    }

    public void ricalcolaTotali() {

        try {

            if (texClie.getText() != null && texClie.getText().length() > 0) {
                doc.setCodiceCliente(Long.parseLong(texClie.getText()));
            }

            doc.setScontoTestata1(Db.getDouble(texScon1.getText()));
            doc.setScontoTestata2(Db.getDouble(texScon2.getText()));
            doc.setScontoTestata3(Db.getDouble(texScon3.getText()));
            doc.setSpeseIncasso(Db.getDouble(texSpeseIncasso.getText()));
            doc.setSpeseTrasporto(Db.getDouble(texSpeseTrasporto.getText()));
            doc.setPrezziIvati(prezzi_ivati.isSelected());
            doc.setSconto(Db.getDouble(texSconto.getText()));
            doc.calcolaTotali();
            texTota.setText(it.tnx.Util.formatValutaEuro(doc.getTotale()));
            texTotaImpo.setText(it.tnx.Util.formatValutaEuro(doc.getTotaleImponibile()));
            texTotaIva.setText(it.tnx.Util.formatValutaEuro(doc.getTotaleIva()));
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    synchronized private void riempiDestDiversa(String sql) {
        boolean oldrefresh = dati.isRefreshing;
        dati.isRefreshing = true;
        comClieDest.setRinominaDuplicati(true);
        comClieDest.dbClearList();
        comClieDest.dbAddElement("", "");
        System.out.println("*** riempiDestDiversa *** sql:" + sql);
        comClieDest.dbOpenList(Db.getConn(), sql, this.texClieDest.getText(), false);
        dati.isRefreshing = oldrefresh;
    }

    public void aggiornareProvvigioni() {
        block_aggiornareProvvigioni = true;
        if (SwingUtils.showYesNoMessage(this, "Vuoi aggiornare le provvigioni delle righe già inserite alla nuova provvigione ?")) {
            int id = InvoicexUtil.getIdDdt(prev.serie, prev.numero, prev.anno);
            String sql = "update righ_ddt" + suff + " set provvigione = " + Db.pc2(texProvvigione.getText(), Types.DOUBLE) + " where id_padre = " + id;
            try {
                DbUtils.tryExecQuery(Db.getConn(), sql);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        block_aggiornareProvvigioni = false;
    }

    public JTable getGrid() {
        return griglia;
    }

    public tnxDbGrid getGrigliaInitComp() {
        return new tnxDbGrid() {
            //ovveride del save

            @Override
            public void saveDataEntry(int row) {
                //non faccio niente e salvo solo cosa voglio io
            }

            @Override
            public void setValueAt(Object aValue, int row, int column) {
                double qta_evasa = CastUtils.toDouble(aValue);
                qta_evasa = FormatUtils.round(qta_evasa, 5);
                aValue = qta_evasa;
                super.setValueAt(aValue, row, column);
                //salvo in tabella riga ddt la qta evasa
                String tabr = acquisto ? "righ_ddt_acquisto" : "righ_ddt";
                try {
                    Integer idriga = CastUtils.toInteger(getValueAt(row, getColumnByName("id")));
                    String sql = "update " + tabr + " set quantita_evasa = " + Db.pc(CastUtils.toDouble(aValue), Types.DOUBLE) + " where id = " + idriga;
                    System.out.println("sql = " + sql);
                    DbUtils.tryExecQuery(Db.getConn(), sql);
                    aggiornaStatoEvasione();
                } catch (Exception e) {
                    SwingUtils.showExceptionMessage(this, e);
                }
            }

            public void changeSelection(final int row, final int column, boolean toggle, boolean extend) {
                super.changeSelection(row, column, toggle, extend);
                System.out.println("changeSel:" + row + " " + column);
                if (editCellAt(row, column)) {
                    Component comp = getEditorComponent();
                    comp.requestFocusInWindow();
                    if (comp instanceof JTextField) {
                        JTextField textComp = (JTextField) comp;
                        textComp.selectAll();
                    }
                }
            }
        };
    }

    private void aggiornaStatoEvasione() {
        String evaso = InvoicexUtil.getStatoEvasione(griglia, "quantita", getCampoQtaEvasa());
        if ("S".equalsIgnoreCase(evaso)) {
            stato_evasione.setSelectedIndex(0);
        } else if ("P".equalsIgnoreCase(evaso)) {
            stato_evasione.setSelectedIndex(1);
        } else {
            stato_evasione.setSelectedIndex(2);
        }

    }

    private String getTipoDoc() {
        return acquisto ? Db.TIPO_DOCUMENTO_DDT_ACQUISTO : Db.TIPO_DOCUMENTO_DDT;
    }

    private String getStatoEvaso() {
        if (stato_evasione.getSelectedIndex() == 0) {
            return "S";
        }
        if (stato_evasione.getSelectedIndex() == 1) {
            return "P";
        } else {
            return "";
        }
    }

    private String getCampoQtaEvasa() {
//        return acquisto ? "qta arrivata" : "qta evasa";
        return "qta fatturata";
    }

    public tnxDbPanel getDatiPanel() {
        return dati;
    }
    
    public JTabbedPane getTab() {
        return jTabbedPane1;
    }    
    
    public tnxTextField getTexClie() {
        return texClie;
    }
    
    public boolean isAcquisto() {
        return acquisto;
    }    
    
    public void selezionaCliente() {
        //apro combo destinazione cliente
        sql = "select ragione_sociale,codice from clie_forn_dest";
        sql += " where codice_cliente = " + Db.pc(this.texClie.getText(), "NUMERIC");
        sql += " order by ragione_sociale";
        riempiDestDiversa(sql);

        String sqlTmp = "SELECT note, note_automatiche as auto FROM clie_forn where codice = " + Db.pc(String.valueOf(comClie.getSelectedKey()), "NUMERIC");
        ResultSet noteauto = Db.openResultSet(sqlTmp);
        try {
            if (noteauto.next()) {
                String auto = noteauto.getString("auto");
                String nota = noteauto.getString("note");
                if (auto != null && auto.equals("S")) {
                    if (!texNote.getText().equals("") && !texNote.getText().equals(nota)) {
                        this.texNote.setText(nota);
                    } else {
                        this.texNote.setText(noteauto.getString("note"));
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        recuperaDatiCliente();
        ricalcolaTotali();

    }
}
