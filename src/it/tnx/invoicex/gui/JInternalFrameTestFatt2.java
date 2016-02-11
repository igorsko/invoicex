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
package it.tnx.invoicex.gui;

import com.jidesoft.hints.AbstractListIntelliHints;
import gestioneFatture.ClienteHint;
import gestioneFatture.CoordinateBancarie;
import gestioneFatture.Db;

import gestioneFatture.FoglioSelectionListener;
import gestioneFatture.GenericFrmTest;
import gestioneFatture.InvoicexEvent;
import gestioneFatture.JDialogChooseListino;
import gestioneFatture.Menu;
import gestioneFatture.Scadenze;
import gestioneFatture.Storico;
import gestioneFatture.chiantiCashmere.animali.*;
import gestioneFatture.dbDocumento;
import gestioneFatture.dbFattura;
import gestioneFatture.frmClie;
import gestioneFatture.frmDbListSmall;
import gestioneFatture.frmElenFatt;
import gestioneFatture.frmListCoorBanc;
import gestioneFatture.frmNuovRigaDescrizioneMultiRigaNew;
import gestioneFatture.frmNuovRigaDescrizioneMultiRigaNewFrajor;
import gestioneFatture.frmPagaPart;
import gestioneFatture.frmPrezziFatturePrecedenti;
import gestioneFatture.frmZoomDesc;
import gestioneFatture.iniFileProp;
import gestioneFatture.logic.clienti.Cliente;
import gestioneFatture.logic.documenti.*;
import gestioneFatture.main;

import it.tnx.commons.CastUtils;
import it.tnx.commons.DbUtils;
import it.tnx.commons.DebugUtils;
import it.tnx.commons.FxUtils;
import it.tnx.commons.StringUtilsTnx;
import it.tnx.commons.SwingUtils;
import it.tnx.commons.cu;
import it.tnx.gui.JTableSs;
import it.tnx.invoicex.InvoicexUtil;
import it.tnx.proto.LockableBusyPainterUI;
import java.awt.BorderLayout;
import java.awt.Color;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.io.File;
import java.math.BigDecimal;
import java.sql.*;
import java.text.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;
import javax.swing.*;
import javax.swing.JInternalFrame;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.table.*;
import javax.swing.text.JTextComponent;
import org.apache.commons.lang.StringUtils;
import org.jdesktop.jxlayer_old.JXLayer;
import org.jdesktop.jxlayer_old.plaf.ext.LockableUI;
import tnxbeans.tnxTextField;
import tnxbeans.tnxDbPanel;


public class JInternalFrameTestFatt2 extends javax.swing.JInternalFrame implements InterfaceAnimale, GenericFrmTest {

    public dbFattura prev = new dbFattura();
    public Documento doc = new Documento();
    public frmElenFatt from;
    private Db db = Db.INSTANCE;
    int pWidth;
    int pHeight;
    public String dbStato = "L";
    public static String DB_INSERIMENTO = "I";
    public static String DB_MODIFICA = "M";
    public static String DB_LETTURA = "L";
    private String sql = "";
    private double totaleIniziale;
    private double totaleDaPagareIniziale;
    private String pagamentoIniziale;
    private String pagamentoInizialeGiorno;
    //private int tempTipoFatt = 0;
    //per controllare le provvigioni
    private double provvigioniIniziale;
    private String provvigioniInizialeScadenze;
    private int codiceAgenteIniziale;
    private double provvigioniTotaleIniziale;
    //per foglio righe
    private DataModelFoglio foglioData;
    public boolean loadingFoglio = false;
    private String sqlGriglia;
    java.util.Timer tim;
    FoglioSelectionListener foglioSelList;
    javax.swing.JInternalFrame zoom;
    private String old_id = "";
    private boolean id_modificato = false;
    private String old_anno = "";
    private String old_data = "";
    private boolean anno_modificato = false;
    private int comClieSel_old = -1;
    private int comClieDest_old = -1;
    private String serie_originale = null;
    private Integer numero_originale = null;
    private Integer anno_originale = null;
    private String data_originale = null;
    public Integer id = null;
    public boolean in_apertura = false;
    AbstractListIntelliHints alRicercaCliente = null;
    LockableUI lockableUI = new LockableBusyPainterUI();
    ArrayList<Runnable> toRun = new ArrayList<Runnable>();
    org.jdesktop.swingworker.SwingWorker worker = null;
    public Throwable trow = null;

    public Connection conn = null;

    /** Creates new form frmElenPrev */
    public JInternalFrameTestFatt2(final String dbStato, final String dbSerie, final int dbNumero, String prevStato, final int dbAnno, int tipoFattura) throws Exception {
        this(dbStato, dbSerie, dbNumero, prevStato, dbAnno, tipoFattura, -1);
    }

    public JInternalFrameTestFatt2(final String dbStato, final String dbSerie, final int dbNumero, String prevStato, final int dbAnno, int tipoFattura, int dbIdFattura) throws Exception {
                
        in_apertura = true;

        conn = Db.INSTANCE.getConnection();
        conn.setAutoCommit(false);

        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        this.dbStato = dbStato;
        this.id = dbIdFattura;

        //this.tempTipoFatt = tipoFattura;

        System.out.println("SwingUtilities.isEventDispatchThread(): = " + SwingUtilities.isEventDispatchThread());

        initComponents();
        texNote.setFont(texSeri.getFont());

        if (main.getPersonalContain("carburante")) {
            jLabel114.setText("spese carburante");
        }

        System.out.println("SwingUtilities.isEventDispatchThread(): = " + SwingUtilities.isEventDispatchThread());

        tutto.remove(tabDocumento);
        tutto.remove(jPanel5);

        JXLayer<JComponent> l = new JXLayer<JComponent>(tutto, lockableUI);
        lockableUI.setLocked(true);
        add(l);

        texCliente.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
//                alRicercaCliente.showHints();
                texCliente.selectAll();
            }
        });

        alRicercaCliente = new AbstractListIntelliHints(texCliente) {

            String current_search = "";

            @Override
            protected JList createList() {
                final JList list = super.createList();
                list.setCellRenderer(new DefaultListCellRenderer() {

                    @Override
                    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        String img, tipo;
                        tipo = ((ClienteHint) value).toString();
                        JLabel lab = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        String word = current_search;
                        String content = tipo;
                        Color c = null;
                        if (!isSelected) {
                            c = new Color(240, 240, 100);
                        } else {
                            c = new Color(100, 100, 40);
                        }
                        String rgb = Integer.toHexString(c.getRGB());
                        rgb = rgb.substring(2, rgb.length());

                        content = StringUtilsTnx.highlightWord(content, word, "<span style='background-color: " + rgb + "'>", "</span>");

                        if (((ClienteHint) value).obsoleto) {
                            content = "<span style='color: FF0000'>" + content + " (Obsoleto)</span>";
                        }
                        lab.setText("<html>" + content + "</html>");
                        System.out.println(index + ":" + content);
                        return lab;
                    }
                });
                return list;
            }

            public boolean updateHints(Object arg0) {
//                if (arg0.toString().trim().length() <= 1) return false;
                SwingUtils.mouse_wait();
                current_search = arg0.toString();
                Connection conn;
                try {
                    conn = Db.getConn();

                    String sql = ""
                            + "SELECT codice, ragione_sociale, obsoleto FROM clie_forn"
                            + " where codice like '%" + Db.aa(current_search) + "%'"
                            + " or ragione_sociale like '%" + Db.aa(current_search) + "%'"
                            + " order by ragione_sociale, codice limit 50";

                    System.out.println("sql ricerca:" + sql);
                    ResultSet rs = DbUtils.tryOpenResultSet(conn, sql);
                    Vector v = new Vector();

                    while (rs.next()) {
                        ClienteHint cliente = new ClienteHint();
                        cliente.codice = rs.getString(1);
                        cliente.ragione_sociale = rs.getString(2);
                        cliente.obsoleto = rs.getBoolean(3);
                        v.add(cliente);
                    }
                    setListData(v);
                    rs.getStatement().close();
                    rs.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                SwingUtils.mouse_def();
                return true;
            }

            @Override
            public void acceptHint(Object arg0) {
                super.acceptHint(arg0);
                try {
                    if (((ClienteHint) arg0).codice.equals("*")) {
                        texClie.setText("");
                    } else {
                        texClie.setText(((ClienteHint) arg0).codice);
                    }
                    comClie.dbTrovaKey(texClie.getText());
                    selezionaCliente();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        if (!main.getPersonalContain("bollo")) {
            labMarcaBollo.setVisible(false);
            texMarcaBollo.setVisible(false);
        }

        texDestRagioneSociale.setMargin(new Insets(0, 0, 0, 0));
        texDestIndirizzo.setMargin(new Insets(0, 0, 0, 0));
        texDestCap.setMargin(new Insets(0, 0, 0, 0));
        texDestLocalita.setMargin(new Insets(0, 0, 0, 0));
        texDestProvincia.setMargin(new Insets(0, 0, 0, 0));
        texDestTelefono.setMargin(new Insets(0, 0, 0, 0));
        texDestCellulare.setMargin(new Insets(0, 0, 0, 0));
//        texVettore1.setMargin(new Insets(0, 0, 0, 0));
        ((JTextComponent) comVettori.getEditor().getEditorComponent()).setMargin(new Insets(0, 0, 0, 0));
        texForni.setMargin(new Insets(0, 0, 0, 0));
        texNumeroColli.setMargin(new Insets(0, 0, 0, 0));
        ((JTextComponent) comPaese.getEditor().getEditorComponent()).setMargin(new Insets(0, 0, 0, 0));
        ((JTextComponent) comCausaleTrasporto.getEditor().getEditorComponent()).setMargin(new Insets(0, 0, 0, 0));
        ((JTextComponent) comAspettoEsterioreBeni.getEditor().getEditorComponent()).setMargin(new Insets(0, 0, 0, 0));
        ((JTextComponent) comMezzoTrasporto.getEditor().getEditorComponent()).setMargin(new Insets(0, 0, 0, 0));
        ((JTextComponent) comPorto.getEditor().getEditorComponent()).setMargin(new Insets(0, 0, 0, 0));
        ((JTextComponent) comForni.getEditor().getEditorComponent()).setMargin(new Insets(0, 0, 0, 0));

        try {
            InvoicexEvent event = new InvoicexEvent(this);
            event.type = InvoicexEvent.TYPE_FRMTESTFATT_CONSTR_POST_INIT_COMPS;
            main.events.fireInvoicexEvent(event);
        } catch (Exception err) {
            err.printStackTrace();
        }

        foglioData = new DataModelFoglio(1000, 10, this);
        foglio.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        foglio.setModel(foglioData);

        //foglio.getColumn(foglio.getColumnName(0)).setHeaderValue("riga");
        //foglio.getColumn(foglio.getColumnName(0)).setPreferredWidth(30);
        javax.swing.table.TableColumnModel columns = foglio.getColumnModel();
        javax.swing.table.TableColumn col = columns.getColumn(0);
        col.setHeaderValue("riga");
        col.setPreferredWidth(30);
        col = columns.getColumn(1);
        col.setHeaderValue("codice art.");
        col.setPreferredWidth(50);
        col = columns.getColumn(2);
        col.setHeaderValue("descrizione");
        col.setPreferredWidth(200);
        col = columns.getColumn(3);
        col.setHeaderValue("um");
        col.setPreferredWidth(20);
        col = columns.getColumn(4);
        col.setHeaderValue("qta");
        col.setPreferredWidth(40);
        col = columns.getColumn(5);
        col.setHeaderValue("prezzo");
        col.setPreferredWidth(80);
        col = columns.getColumn(6);
        col.setHeaderValue("sc.1");
        col.setPreferredWidth(30);
        col = columns.getColumn(7);
        col.setHeaderValue("sc.2");
        col.setMaxWidth(0);
        col.setMinWidth(0);
        col.setPreferredWidth(0);
        col.setWidth(0);
        col.setResizable(false);
        col = columns.getColumn(8);
        col.setHeaderValue("importo");
        col.setPreferredWidth(80);
        col = columns.getColumn(9);
        col.setHeaderValue("iva");
        col.setPreferredWidth(30);

        JTextField textEdit = new javax.swing.JTextField() {
        };

        CellEditorFoglio edit = new CellEditorFoglio(textEdit);

        //it.tnx.gui.KeyableCellEditor edit = new it.tnx.gui.KeyableCellEditor();
        //edit.setClickCountToStart(0);
        edit.setClickCountToStart(2);
        foglio.setDefaultEditor(Object.class, edit);

        for (int i = 0; i < foglioData.getRowCount(); i++) {
            foglioData.setValueAt(new Integer((i + 1) * 10), i, 0);
        }

        FoglioSelectionListener foglioSelList = new FoglioSelectionListener(foglio);
        foglio.getSelectionModel().addListSelectionListener(foglioSelList);

        //--- fine foglio ---------
        this.griglia.dbEditabile = false;

        if (main.getPersonalContain(main.PERSONAL_CHIANTICASHMERE_ANIMALI)) {
            this.butNuovoAnimale.setVisible(true);
        } else {
            this.butNuovoAnimale.setVisible(false);
        }

        //init campi particolari
        this.texData.setDbDefault(tnxTextField.DEFAULT_CURRENT);

        //oggetto preventivo
        this.prev.dbStato = dbStato;
        this.prev.serie = dbSerie;
        this.prev.numero = dbNumero;
        this.prev.stato = prevStato;
        this.prev.anno = dbAnno;

        //105
        this.prev.tipoFattura = tipoFattura;
        this.prev.texTota = this.texTota;
        this.prev.texTotaImpo = this.texTotaImpo;
        this.prev.texTotaIva = this.texTotaIva;
//        this.setClosable(false);

        if (dbStato.equals(JInternalFrameTestFatt2.DB_MODIFICA)) {
            //memorizzo il numero doc originale
            serie_originale = dbSerie;
            numero_originale = dbNumero;
            anno_originale = dbAnno;
        }

        //this.texSeri.setVisible(false);
        //associo il panel ai dati
        JInternalFrameTestFatt2.this.dati.dbNomeTabella = "test_fatt";
        dati.dbChiaveAutoInc = true;

        Vector chiave = new Vector();
        chiave.add("id");
        JInternalFrameTestFatt2.this.dati.dbChiave = chiave;

        worker = new org.jdesktop.swingworker.SwingWorker<Object, Object>() {

            @Override
            protected Object doInBackground() throws Exception {
                //apertura delle combo
                comPaga.dbOpenList(db.getConn(), "select codice, codice from pagamenti order by codice", null, false);
                comPaese.dbAddElement("", "");
                comPaese.dbOpenList(Db.getConn(), "select nome, codice1 from stati", null, false);
                comAgente.dbOpenList(Db.getConn(), "select nome, id from agenti order by nome", null, false);
                comClie.dbOpenList(Db.getConn(), "select ragione_sociale,codice from clie_forn order by ragione_sociale", JInternalFrameTestFatt2.this.texClie.getText(), false);
                comForni.dbOpenList(Db.getConn(), "select ragione_sociale,codice from clie_forn order by ragione_sociale", JInternalFrameTestFatt2.this.texForni.getText(), false);

                return null;
            }

            @Override
            protected void done() {
                //105 metto titolo finestra per sapere se fattura o altro
                if (prev.tipoFattura == dbFattura.TIPO_FATTURA_NON_IDENTIFICATA) {
                    //prev.tipoFattura = Integer.valueOf(frmTestFatt.this.texTipoFattura.getText()).intValue();
                    //leggo da db
                    sql = "select tipo_fattura from test_fatt";
                    sql += " where id = " + JInternalFrameTestFatt2.this.id;
                    System.err.println("dbopen tipo_fattura:" + sql);
                    try {
                        prev.tipoFattura = CastUtils.toInteger(DbUtils.getObject(Db.getConn(), sql));
                    } catch (Exception e) {
                    }
                }

                setTipoFattura(prev.tipoFattura);
                texForni1.setVisible(false);

                comClie.setDbTextAbbinato(JInternalFrameTestFatt2.this.texClie);
                texClie.setDbComboAbbinata(JInternalFrameTestFatt2.this.comClie);
                comForni.setDbTextAbbinato(JInternalFrameTestFatt2.this.texForni);
                texForni.setDbComboAbbinata(JInternalFrameTestFatt2.this.comForni);

                //this.dati.butSave = this.butSave;
                //this.dati.butUndo = this.butUndo;
                //controllo se inserimento o modifica
                if (dbStato.equalsIgnoreCase(JInternalFrameTestFatt2.DB_INSERIMENTO)) {
                    JInternalFrameTestFatt2.this.dati.dbOpen(Db.getConn(), "select * from test_fatt limit 0");
                } else {
                    sql = "select * from test_fatt";
                    //            sql += " where serie = " + db.pc(dbSerie, "VARCHAR");
                    //            sql += " and numero = " + dbNumero;
                    //            sql += " and anno = " + dbAnno;
                    sql += " where id = " + JInternalFrameTestFatt2.this.id;
                    System.err.println("dbopen");
                    JInternalFrameTestFatt2.this.dati.dbOpen(Db.getConn(), sql);
                }

                //righe
                //apro la griglia
                JInternalFrameTestFatt2.this.griglia.dbNomeTabella = "righ_fatt";

                java.util.Hashtable colsWidthPerc = new java.util.Hashtable();
                colsWidthPerc.put("serie", new Double(0));
                colsWidthPerc.put("numero", new Double(0));
                colsWidthPerc.put("anno", new Double(0));
                colsWidthPerc.put("stato", new Double(0));
                colsWidthPerc.put("riga", new Double(5));
                colsWidthPerc.put("articolo", new Double(10));
                colsWidthPerc.put("descrizione", new Double(35));
                colsWidthPerc.put("um", new Double(5));
                colsWidthPerc.put("quantita", new Double(10));
                colsWidthPerc.put("prezzo", new Double(15));
                colsWidthPerc.put("sconto1", new Double(0));
                colsWidthPerc.put("sconto2", new Double(0));
                colsWidthPerc.put("iva", new Double(0));
                colsWidthPerc.put("Totale", new Double(10));
                colsWidthPerc.put("Sconti", new Double(10));
                colsWidthPerc.put("provvigione", new Double(7));
                colsWidthPerc.put("id", new Double(0));
                JInternalFrameTestFatt2.this.griglia.columnsSizePerc = colsWidthPerc;

                java.util.Hashtable colsAlign = new java.util.Hashtable();
                colsAlign.put("quantita", "RIGHT_CURRENCY");
                colsAlign.put("prezzo", "RIGHT_CURRENCY");
                JInternalFrameTestFatt2.this.griglia.columnsAlign = colsAlign;
                JInternalFrameTestFatt2.this.griglia.flagUsaOrdinamento = false;

                //        Vector chiave2 = new Vector();
                //        chiave2.add("serie");
                //        chiave2.add("numero");
                //        chiave2.add("anno");
                //        chiave2.add("riga");
                Vector chiave2 = new Vector();
                chiave2.add("id");
                JInternalFrameTestFatt2.this.griglia.dbChiave = chiave2;
                if (dbStato.equalsIgnoreCase(JInternalFrameTestFatt2.DB_INSERIMENTO)) {
                } else {
                    //disabilito la data perch??? non tornerebbe pi??? la chiave per numero e anno
                    //siccome tutti vogliono modificarsi la data che se la modifichino...
                    //this.texData.setEditable(false);
                    JInternalFrameTestFatt2.this.prev.sconto1 = Db.getDouble(JInternalFrameTestFatt2.this.texScon1.getText());
                    JInternalFrameTestFatt2.this.prev.sconto2 = Db.getDouble(JInternalFrameTestFatt2.this.texScon2.getText());
                    JInternalFrameTestFatt2.this.prev.sconto3 = Db.getDouble(JInternalFrameTestFatt2.this.texScon3.getText());

                    //this.prev.speseVarie = Db.getDouble(this.texSpesVari.getText());
                    JInternalFrameTestFatt2.this.prev.speseTrasportoIva = Db.getDouble(JInternalFrameTestFatt2.this.texSpeseTrasporto.getText());
                    JInternalFrameTestFatt2.this.prev.speseIncassoIva = Db.getDouble(JInternalFrameTestFatt2.this.texSpeseIncasso.getText());
                }

                texForni.setText(texForni1.getText());
                comForni.setSelectedIndex(-1);
                if (!JInternalFrameTestFatt2.this.texForni1.getText().equals("")) {
                    boolean continua = true;
                    for (int i = 0; i < comForni.getItemCount() && continua; i++) {
                        Integer tempchiave = Integer.parseInt(String.valueOf(comForni.getKey(i)));
                        if (Integer.parseInt(texForni.getText()) == tempchiave) {
                            comForni.setSelectedIndex(i);
                            continua = false;
                        }
                    }
                }

                if (dbStato.equalsIgnoreCase(JInternalFrameTestFatt2.DB_INSERIMENTO)) {
                    inserimento();
                } else {
                    dopoInserimento();
                }

                //apro combo destinazione cliente
                comClieDest.dbTrovaMentreScrive = false;
                sql = "select ragione_sociale,codice from clie_forn_dest";
                sql += " where codice_cliente = " + Db.pc(JInternalFrameTestFatt2.this.texClie.getText(), "NUMERIC");
                sql += " order by ragione_sociale";
                riempiDestDiversa(sql);

                boolean azioniPericolose = main.fileIni.getValueBoolean("pref", "azioniPericolose", true);
                if (azioniPericolose) {
                    texNumePrev.setEditable(true);
                    texData.setEditable(true);
                }

                //impostazioni griglia foglio
                JInternalFrameTestFatt2.this.foglio.setRowHeight(20);
                zoom = new frmZoomDesc();

                frmZoomDesc frmZoom = (frmZoomDesc) zoom;
                frmZoom.selectList = JInternalFrameTestFatt2.this.foglioSelList;
                frmZoom.setGriglia(foglio);
                zoom.putClientProperty("JInternalFrame.isPalette", Boolean.TRUE);
                zoom.setResizable(true);
                zoom.setIconifiable(true);
                zoom.setClosable(true);
                zoom.setBounds((int) JInternalFrameTestFatt2.this.getLocation().getX() + 430, (int) JInternalFrameTestFatt2.this.getLocation().getY() + 350, 300, 150);

                Menu m = (Menu) main.getPadre();
                comPagaItemStateChanged(null);
                dati.dbCheckModificatiReset();
                data_originale = texData.getText();

                if (dbStato.equalsIgnoreCase(JInternalFrameTestFatt2.DB_INSERIMENTO)) {
                    SimpleDateFormat f1 = new SimpleDateFormat("dd/MM/yy HH:mm");
                    texDataOra.setText(f1.format(new java.util.Date()));
                    texData.setEditable(true);
                } else {
                }

                JInternalFrameTestFatt2.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

                tutto.add(tabDocumento, BorderLayout.CENTER);
                tutto.add(jPanel5, BorderLayout.SOUTH);
                lockableUI.setLocked(false);
                texCliente.requestFocus();
                in_apertura = false;

                if (toRun != null) {
                    System.err.println("eseguo toRun in worker done");
                    for (Runnable run : toRun) {
                        System.err.println("eseguo run in worker done:" + run);
                        run.run();
                    }
                }

                griglia.resizeColumnsPerc(true);
            }
        };
        worker.execute();


    }

    public void eseguiDopo(Runnable run) {
        System.err.println("eseguiDopo aggiungo run:" + run);
        toRun.add(run);
//        System.err.println("eseguo toRun subito run:" + run + " worker:" + worker);
//        run.run();
    }

    public void selezionaCliente() {
        //apro combo destinazione cliente
        sql = "select ragione_sociale,codice from clie_forn_dest";
        sql += " where codice_cliente = " + Db.pc(this.texClie.getText(), "NUMERIC");
        sql += " order by ragione_sociale";
        riempiDestDiversa(sql);

        if (main.getPersonal().equals(main.PERSONAL_CUCINAIN)) {
            listiniTicket();
        }

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

    private String getAnnoDaForm() {
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

    private void inserimento() {
        this.dati.dbNew();

        //controllo serie default
        if (Db.getSerieDefault().length() > 0) {
            texSeri.setText(Db.getSerieDefault());
        } else {
            texSeri.setText(prev.serie);
        }

        if (main.iniSerie == false || (prev.tipoFattura == dbFattura.TIPO_FATTURA_NOTA_DI_CREDITO || prev.tipoFattura == dbFattura.TIPO_FATTURA_PROFORMA)) {
            assegnaNumero();
            dopoInserimento();
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

            this.texSeri.setToolTipText("Inserisci la serie e premi Invio per confermarla ed assegnare un numero al documento");
            this.texSeri.setEnabled(true);
            this.texSeri.setEditable(true);
            this.texSeri.setBackground(java.awt.Color.RED);
            javax.swing.SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    texSeri.requestFocus();
                }
            });
        }

    }

    private void assegnaSerie() {
        this.texSeri.setText(texSeri.getText().toUpperCase());
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

    private void assegnaNumero() {

        //metto ultimo numero preventivo + 1
        //apre il resultset per ultimo +1
        java.sql.Statement stat;
        ResultSet resu;

        try {
            stat = db.getConn().createStatement();

            String sql = "select numero from test_fatt";
            sql += " where anno = " + java.util.Calendar.getInstance().get(Calendar.YEAR);
            sql += " and serie = " + Db.pc(texSeri.getText(), Types.VARCHAR);
            sql += " and tipo_fattura != 7";
            sql += " order by numero desc limit 1";
            resu = stat.executeQuery(sql);

            if (resu.next() == true) {
                this.texNumePrev.setText(String.valueOf(resu.getInt(1) + 1));
            } else {
                this.texNumePrev.setText("1");
            }

            //inserisco data consegna standard
            //this.texDataCons.setText("");
            //inserisco spese incasso standard
            if (main.getPersonal().equals(main.PERSONAL_GIANNI)) {
                this.texSpeseIncasso.setText("1,50");
            }

            //105 metto tipo fattura
            this.texTipoFattura.setText(String.valueOf(this.prev.tipoFattura));
            this.texAnno.setText(String.valueOf(java.util.Calendar.getInstance().get(Calendar.YEAR)));

            //-----------------------------------------------------------------
            //se apre in inserimento gli faccio subito salvare la testa
            //se poi la annulla vado ad eliminare
            //appoggio totali
            this.texTota1.setText(this.texTota.getText());
            this.texTotaIva1.setText(this.texTotaIva.getText());
            this.texTotaImpo1.setText(this.texTotaImpo.getText());

            texClie.setText("0");
            if (this.dati.dbStato.equals(DB_INSERIMENTO)) {
                try {
                    String tmpSerie = this.texSeri.getText();
                    Integer numero = Integer.parseInt(texNumePrev.getText());
                    Integer anno = Integer.parseInt(texAnno.getText());

                    String tmpSql = "select * from test_fatt where serie = '" + tmpSerie + "' and anno = " + anno + " and numero = " + numero + " and tipo_fattura != 7";
                    System.out.println("tmpSql:" + tmpSql);
                    ResultSet tmpControl = Db.openResultSet(tmpSql);

                    if (tmpControl.next()) {
                        JOptionPane.showMessageDialog(this, "Un' altra fattura con lo stesso gruppo numero - serie - anno è già stata inserita!", "Impossibile inserire dati", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (this.dati.dbSave() == true) {
                //non vedo percè ? 12/05/2010 cecca
//                //richiamo il refresh della maschera che lo ha lanciato
//                if (from != null) {
//                    frmElenFatt temp = (frmElenFatt) from;
//                    temp.dbRefresh();
//                }
            }
            texClie.setText("");

            this.prev.serie = this.texSeri.getText();
            this.prev.stato = "P";
            this.prev.numero = new Integer(this.texNumePrev.getText()).intValue();
            this.prev.anno = java.util.Calendar.getInstance().get(Calendar.YEAR);

//            this.id = (Integer) dati.dbGetField("id");
            this.id = (Integer) dati.last_inserted_id;
            System.out.println("*** id new : " + this.id);
            this.prev.id = id;

            this.dati.dbCambiaStato(this.dati.DB_LETTURA);

            //-----------------------------------------------------------------
            //aggiunto da Lorenzo per tlz
            //cambio mettendo opzioni su opzioni
            //if (main.getPersonal().equalsIgnoreCase(main.PERSONAL_TLZ)) {
            //  this.texNote.setText("FATTURA ESENTE IVA.\n OPERAZIONE INTRACOMUNITARIA CON NUMERO DI IDENTIFICAZIONE ATU 54471806");
            //}
            try {

//                java.util.prefs.Preferences preferences = java.util.prefs.Preferences.userNodeForPackage(main.class);
                //this.texNote.setText(preferences.get("noteStandard", ""));
                texNote.setText(main.fileIni.getValue("pref", "noteStandard"));

                //dbRefresh();
            } catch (Exception err) {
                err.printStackTrace();
            }

            this.texSeri.setEditable(false);
            this.texSeri.setBackground(this.texNumePrev.getBackground());

            //Fine
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    private void dopoInserimento() {
        dbAssociaGrigliaRighe();

        doc.load(Db.INSTANCE, this.prev.numero, this.prev.serie, this.prev.anno, Db.TIPO_DOCUMENTO_FATTURA, id);

//        SwingUtils.showInfoMessage(this, "comClie:" + comClie.getText() + " texClie:" + texClie.getText());

        if (comClie.getText().trim().length() == 0) {
            try {
                String cliente = (String) DbUtils.getObject(Db.getConn(), "select ragione_sociale from clie_forn where codice = '" + texClie.getText() + "'");
                texCliente.setText(cliente);
            } catch (Exception e) {
            }
        } else {
            texCliente.setText(comClie.getText());
        }

        //apro combo banche
        trovaAbi();
        trovaCab();

        //provo a fare timer per aggiornare prezzo totale
        tim = new java.util.Timer();

//        timerRefreshFattura timTest = new timerRefreshFattura(this, doc);
//        tim.schedule(timTest, 1000, 500);
        //rinfresco il discorso extra cee
        try {
            if (this.texClie.getText().length() > 0) {
                this.prev.forceCliente(Long.parseLong(this.texClie.getText()));
            }
        } catch (Exception err) {
            err.printStackTrace();
        }

        //memorizzo totale iniziale, se cambia rigenreo le scadenze
        prev.dbRefresh();

        //nascondo le i dati agente se tlz
        if (main.getPersonalContain(main.PERSONAL_TLZ)) {
            this.labAgente.setVisible(false);
            this.comAgente.setVisible(false);
            this.labProvvigione.setVisible(false);
            this.texProvvigione.setVisible(false);
            this.labPercentoProvvigione.setVisible(false);
        }

        try {
            InvoicexEvent event = new InvoicexEvent(this);
            event.type = InvoicexEvent.TYPE_FRMTESTFATT_DOPO_INSERIMENTO;
            main.events.fireInvoicexEvent(event);
        } catch (Exception err) {
            err.printStackTrace();
        }

        ricalcolaTotali();

        this.totaleIniziale = this.prev.totale;
        this.totaleDaPagareIniziale = this.prev.totaleDaPagare;
        this.pagamentoIniziale = this.comPaga.getText();
        this.pagamentoInizialeGiorno = this.texGiornoPagamento.getText();
        this.provvigioniIniziale = Db.getDouble(this.texProvvigione.getText());
        provvigioniInizialeScadenze = dumpScadenze();
        this.provvigioniTotaleIniziale = dumpProvvigioni();

        this.codiceAgenteIniziale = it.tnx.Util.getInt(this.comAgente.getSelectedKey().toString());

        //debug
        System.out.println("provvigioni iniziale scadenze = " + provvigioniInizialeScadenze);
        System.out.println("provvigioni iniziale = " + provvigioniIniziale);
        System.out.println("codice agente iniziale = " + codiceAgenteIniziale);

    }

    synchronized private void riempiDestDiversa(String sql) {
        boolean oldrefresh = dati.isRefreshing;
        dati.isRefreshing = true;
        comClieDest.dbClearList();
        comClieDest.dbAddElement("", "");
        comClieDest.dbOpenList(Db.getConn(), sql, this.texClieDest.getText(), false);
        dati.isRefreshing = oldrefresh;
    }

    private void setTipoFattura(int tipoFattura) {

        //imposto il titolo
        if (tipoFattura == dbFattura.TIPO_FATTURA_IMMEDIATA) {
            this.setTitle("FATTURA IMMEDIATA");
        } else if (tipoFattura == dbFattura.TIPO_FATTURA_ACCOMPAGNATORIA) {
            this.setTitle("FATTURA ACCOMPAGNATORIA");
        } else if (tipoFattura == dbFattura.TIPO_FATTURA_NOTA_DI_CREDITO) {
            this.setTitle("NOTA DI CREDITO");
        } else if (tipoFattura == dbFattura.TIPO_FATTURA_PROFORMA) {
            this.setTitle("FATTURA PRO-FORMA");
        } else {
            this.setTitle("FATTURA");
        }

        if (tipoFattura == dbFattura.TIPO_FATTURA_ACCOMPAGNATORIA) {

            //carico porti
            comPorto.dbAddElement("");
            comPorto.dbOpenList(Db.getConn(), "select porto, id from tipi_porto order by porto");

            //mezzo di trasporto
            comMezzoTrasporto.dbAddElement("");
            comMezzoTrasporto.dbAddElement("DESTINATARIO");
            comMezzoTrasporto.dbAddElement("MITTENTE");
            comMezzoTrasporto.dbAddElement("VETTORE");

            comVettori.dbAddElement("");
            comVettori.dbOpenList(db.getConn(), "select nome,nome from vettori order by nome", null, false);

            //carico causali trasporto
            comCausaleTrasporto.dbAddElement("");
            comCausaleTrasporto.dbOpenList(Db.getConn(), "select nome, id from tipi_causali_trasporto order by nome");

            //105 carico aspetti esteriori beni per gianni
            comAspettoEsterioreBeni.dbAddElement("");
            comAspettoEsterioreBeni.dbAddElement("SCATOLA");
            comAspettoEsterioreBeni.dbAddElement("A VISTA");
            comAspettoEsterioreBeni.dbAddElement("SCATOLA IN PANCALE");
        }

        //visualizzo i componenti per la fattura accompagnatoria
        boolean come = false;

        if (tipoFattura == dbFattura.TIPO_FATTURA_ACCOMPAGNATORIA) {
            come = true;
        } else {
            come = false;
        }

        sepFaSeparatore.setVisible(come);
        labFaTitolo.setVisible(come);
        labFa1.setVisible(come);
        labFa2.setVisible(come);
        labFa3.setVisible(come);
        labFa4.setVisible(come);
        labFa5.setVisible(come);
        labFa6.setVisible(come);
        labFa7.setVisible(come);
        comCausaleTrasporto.setVisible(come);
        comAspettoEsterioreBeni.setVisible(come);
        texNumeroColli.setVisible(come);

        //texVettore1.setVisible(come);
        comVettori.setVisible(come);
        comMezzoTrasporto.setVisible(come);
        comPorto.setVisible(come);
        texDataOra.setVisible(come);
        labPesoLordo.setVisible(come);
        labPesoNetto.setVisible(come);
        texPesoLordo.setVisible(come);
        texPesoNetto.setVisible(come);
    }

    private void listiniTicket() {

        //inserito da lorenzo per prontopizza
        ResultSet resListini = Db.openResultSet("select * from clie_forn left join tipi_listino on clie_forn.codice_listino = tipi_listino.codice where clie_forn.ragione_sociale = '" + this.comClie.getSelectedItem() + "'");

        try {
            resListini.next();

            if ((resListini.getInt("tipi_listino.codice") != 1) && (resListini.getInt("tipi_listino.codice") != 0)) {

                String stringaPerc = resListini.getString("tipi_listino.descrizione");
                stringaPerc = stringaPerc.substring(stringaPerc.length() - 1);
                this.texScon1.setText(stringaPerc + ",00");
                this.texScon1KeyReleased(null);
                this.texScon2.setText("9,10");
                this.texScon2KeyReleased(null);
            } else {
                this.texScon1.setText("0,00");
                this.texScon2.setText("0,00");
            }
        } catch (Exception err) {
        }

        //fine
    }

    private void saveDocumento() {
        try {
            conn.setAutoCommit(false);
        } catch (SQLException ex) {
            SwingUtils.showExceptionMessage(main.getPadreFrame(), ex);
        }

        //sposto i totali di modo che li salvi
        this.texTota1.setText(this.texTota.getText());
        this.texTotaImpo1.setText(this.texTotaImpo.getText());
        this.texTotaIva1.setText(this.texTotaIva.getText());

        //aggiorno totali
        try {

            if (texClie.getText() != null && texClie.getText().length() > 0) {
                doc.setCodiceCliente(Long.parseLong(texClie.getText()));
            }

            doc.setScontoTestata1(Db.getDouble(texScon1.getText()));
            doc.setScontoTestata2(Db.getDouble(texScon2.getText()));
            doc.setScontoTestata3(Db.getDouble(texScon3.getText()));
            doc.setSpeseIncasso(Db.getDouble(texSpeseIncasso.getText()));
            doc.setSpeseTrasporto(Db.getDouble(texSpeseTrasporto.getText()));
            doc.calcolaTotali();
        } catch (Exception err) {
            err.printStackTrace();
        }

        texTotaRitenuta.setText(Db.formatDecimal(doc.getTotale_ritenuta()));
        texRivalsa.setText(Db.formatDecimal(doc.getTotale_rivalsa()));
        texTotaDaPagare.setText(Db.formatDecimal(doc.getTotale_da_pagare()));

        //storico
        Storico.scrivi("Salva Documento", "Documento = " + this.texSeri.getText() + "/" + this.prev.numero + "/" + this.prev.anno + ", Pagamento = " + this.comPaga.getText() + ", Importo documento = " + this.texTota1.getText());

        //salvo altrimenti genera le scadenze sull'importo vuoto
        this.dati.dbSave(conn);
        prev.id = (Integer) dati.dbGetField("id");

        //genero le scadenze
        Scadenze tempScad = new Scadenze(conn, Db.TIPO_DOCUMENTO_FATTURA, this.texSeri.getText(), this.prev.numero, this.prev.anno, this.comPaga.getText());
        boolean scadenzeRigenerate = false;

        //20090730
//        if (doc.getTotale() != this.totaleIniziale || !this.pagamentoInizialeGiorno.equals(this.texGiornoPagamento.getText()) || !this.pagamentoIniziale.equals(this.comPaga.getText()) || doc.getTotale_da_pagare() != this.totaleDaPagareIniziale) {
        System.err.println("totaliDiversi = " + tempScad.totaliDiversi());
        if (tempScad.totaliDiversi() 
                || doc.getTotale() != this.totaleIniziale
                || !this.pagamentoInizialeGiorno.equals(this.texGiornoPagamento.getText())
                || !this.pagamentoIniziale.equals(this.comPaga.getText())
                || doc.getTotale_da_pagare() != this.totaleDaPagareIniziale
                || !data_originale.equalsIgnoreCase(texData.getText())
                ) {
            tempScad.generaScadenze(conn);
            try {
                Storico.scrivi("Genera scadenze", Db.TIPO_DOCUMENTO_FATTURA + " " + this.texSeri.getText() + " " + this.prev.numero + " " + this.prev.anno + " " + this.comPaga.getText());
            } catch (Exception e) {
            }
            scadenzeRigenerate = true;

            //rimetto i totali iniziali almeno in caso di inserimento e modifica delle date non vengono rigenerate.
            this.totaleIniziale = this.prev.totale;
            this.totaleDaPagareIniziale = this.prev.totaleDaPagare;
            this.pagamentoIniziale = this.comPaga.getText();
            this.pagamentoInizialeGiorno = this.texGiornoPagamento.getText();

            if (!dbStato.equals(this.DB_INSERIMENTO)) {
                javax.swing.JOptionPane.showMessageDialog(this, "Sono state rigenerate le scadenze perche' il totale od il pagamento e' stato variato", "Attenzione", javax.swing.JOptionPane.WARNING_MESSAGE);
            }
        }

        //rigenero le provvigioni se ancora non sono state pagate
        //Scadenze tempScad = new Scadenze(Db.TIPO_DOCUMENTO_FATTURA, this.texSeri.getText(), this.prev.numero, this.prev.anno, this.comPaga.getText());
        double nuovoImportoTeoricoProvvigioni = 0;
        gestioneFatture.logic.provvigioni.ProvvigioniFattura provvigioni = new gestioneFatture.logic.provvigioni.ProvvigioniFattura(Db.TIPO_DOCUMENTO_FATTURA, this.texSeri.getText(), this.prev.numero, this.prev.anno, it.tnx.Util.getInt(this.comAgente.getSelectedKey().toString()), it.tnx.Util.getDouble(this.texProvvigione.getText()));
        provvigioni.tipoFattura = prev.tipoFattura;
        nuovoImportoTeoricoProvvigioni = provvigioni.getTotaleProvvigioni();
        if (doc.getTotale() != this.totaleIniziale 
                || scadenzeRigenerate == true
                || this.provvigioniIniziale != Db.getDouble(this.texProvvigione.getText())
                || !this.provvigioniInizialeScadenze.equalsIgnoreCase(dumpScadenze())
                || this.codiceAgenteIniziale != it.tnx.Util.getInt(this.comAgente.getSelectedKey().toString())
                || !data_originale.equalsIgnoreCase(texData.getText())
                || nuovoImportoTeoricoProvvigioni != provvigioniTotaleIniziale) {
            System.out.println("rigenero provvigioni:" + doc.getTotale() + " != " + totaleIniziale + " || scadenzeRigenerate:" + scadenzeRigenerate + " || " + provvigioniIniziale + " != " + Db.getDouble(this.texProvvigione.getText()) + " || " + provvigioniInizialeScadenze + " != " + dumpScadenze() + " || " + codiceAgenteIniziale + " != "  + it.tnx.Util.getInt(this.comAgente.getSelectedKey().toString()) + " || " + data_originale + " != " + texData.getText() + " || " + nuovoImportoTeoricoProvvigioni + " != " + provvigioniTotaleIniziale);
            boolean ret = provvigioni.generaProvvigioni();
            try {
                Storico.scrivi("Genera provvigioni", Db.TIPO_DOCUMENTO_FATTURA + " " + this.texSeri.getText() + " " + this.prev.numero + " " + this.prev.anno + " " + it.tnx.Util.getInt(this.comAgente.getSelectedKey().toString()) + " " + it.tnx.Util.getDouble(this.texProvvigione.getText()));
            } catch (Exception e) {
            }
            System.out.println("esito genera provvigioni:" + ret + " : " + provvigioni.ret);
            if (!dbStato.equals(this.DB_INSERIMENTO)) {
                if (!main.getPersonalContain(main.PERSONAL_TLZ)) {
                    //tlz hanno una gestione differente delle provvigioni
                    if (ret) {
                        javax.swing.JOptionPane.showMessageDialog(this, "Sono state rigenerate le provvigioni", "Attenzione", javax.swing.JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        }

        //109 se fattura accompagnatoria devo generare i movimenti
        //!!!se la fattura non viene da una bolla devo generare i movimenti
//        if (prev.tipoFattura == dbFattura.TIPO_FATTURA_ACCOMPAGNATORIA) {
        //vado a cercare nei ddt se ce ne è almeno uno attaccato a questa fattura
        int conta = 0;
        try {
            String sql = "select count(*) from test_ddt" + " where fattura_serie = '" + prev.serie + "'" + " and fattura_numero = " + prev.numero + " and fattura_anno = " + prev.anno;
            ResultSet r = Db.openResultSet(sql);
            if (r.next()) {
                conta = r.getInt(1);
            }
        } catch (SQLException sqlerr) {
            sqlerr.printStackTrace();
        }
        if (conta == 0) {
            dbDocumento tempPrev = new dbDocumento();
            tempPrev.serie = prev.serie;
            tempPrev.numero = prev.numero;
            tempPrev.stato = prev.stato;
            tempPrev.anno = prev.anno;
            tempPrev.id = prev.id;
            tempPrev.tipoDocumento = Db.TIPO_DOCUMENTO_FATTURA;

            int generazione_movimenti = Integer.parseInt(main.fileIni.getValue("pref", "generazione_movimenti", "0"));
            boolean genera = false;
            int tipo_fattura = Integer.parseInt(texTipoFattura.getText());
            if (tipo_fattura == dbFattura.TIPO_FATTURA_NOTA_DI_CREDITO) {
                if (SwingUtils.showYesNoMessage(this, "Vuoi generare i movimenti di carico magazzino ?")) {
                    genera = true;
                }
            } else {
                if (generazione_movimenti == 0) {
                    //standard genera sempre
                    //su proforma no..
                    if (tipo_fattura == dbFattura.TIPO_FATTURA_IMMEDIATA || tipo_fattura == dbFattura.TIPO_FATTURA_ACCOMPAGNATORIA) {
                        genera = true;
                    }
                } else if (generazione_movimenti == 1) {
                    //genera solo per accompagnatoria
                    if (tipo_fattura != dbFattura.TIPO_FATTURA_IMMEDIATA) {
                        genera = true;
                    }
                } else {
                    //genera solo per accompagnatoria ma chiede per immediata
                    if (tipo_fattura == dbFattura.TIPO_FATTURA_IMMEDIATA) {
                        if (SwingUtils.showYesNoMessage(this, "Vuoi generare i movimenti di magazzino ?")) {
                            genera = true;
                        }
                    }
                }
            }

            if (genera) {
                if (tempPrev.generaMovimentiMagazzino() == false) {
                    javax.swing.JOptionPane.showMessageDialog(this, "Errore nella generazione dei movimenti di magazzino", "Errore", javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            if (gestioneFatture.main.pluginClientManager) {
                JOptionPane.showMessageDialog(this, "La fattura proviene da uno o più ddt e non verranno creati o rigenerati i movimenti");
            }
        }

        //***
        if (from != null) {
            this.from.dbRefresh();
        }
        try {
            conn.commit();
        } catch (SQLException ex) {
            SwingUtils.showExceptionMessage(main.getPadreFrame(), ex);
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
     *
     * initialize the form.
     *
     *
     * WARNING: Do NOT modify this code. The content of this method is
     *
     *
     * always regenerated by the Form Editor.
     *
     *
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popGrig = new javax.swing.JPopupMenu();
        popGrigModi = new javax.swing.JMenuItem();
        popGrigElim = new javax.swing.JMenuItem();
        popGrigAddUp = new javax.swing.JMenuItem();
        popFoglio = new javax.swing.JPopupMenu();
        popFoglioElimina = new javax.swing.JMenuItem();
        foglio3 = new javax.swing.JTable();
        tutto = new javax.swing.JPanel();
        tabDocumento = new javax.swing.JTabbedPane();
        panDati = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
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
        texScon3 = new tnxbeans.tnxTextField();
        labScon21 = new javax.swing.JLabel();
        jLabel151 = new javax.swing.JLabel();
        comClieDest = new tnxbeans.tnxComboField();
        texSeri = new tnxbeans.tnxTextField();
        texAnno = new tnxbeans.tnxTextField();
        texAnno.setVisible(false);
        texClieDest = new tnxbeans.tnxTextField();
        texClieDest.setVisible(false);
        jLabel17 = new javax.swing.JLabel();
        texPaga2 = new tnxbeans.tnxTextField();
        texSpeseTrasporto = new tnxbeans.tnxTextField();
        jLabel114 = new javax.swing.JLabel();
        labScon10 = new javax.swing.JLabel();
        texDestRagioneSociale = new tnxbeans.tnxTextField();
        labScon11 = new javax.swing.JLabel();
        texDestIndirizzo = new tnxbeans.tnxTextField();
        labScon12 = new javax.swing.JLabel();
        texDestCap = new tnxbeans.tnxTextField();
        texDestLocalita = new tnxbeans.tnxTextField();
        labScon13 = new javax.swing.JLabel();
        texDestProvincia = new tnxbeans.tnxTextField();
        labScon14 = new javax.swing.JLabel();
        texDestCellulare = new tnxbeans.tnxTextField();
        labScon15 = new javax.swing.JLabel();
        texDestTelefono = new tnxbeans.tnxTextField();
        labScon16 = new javax.swing.JLabel();
        comPaese = new tnxbeans.tnxComboField();
        labScon17 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        comPaga = new tnxbeans.tnxComboField();
        butScad = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        texBancAbi = new tnxbeans.tnxTextField();
        texBancIban = new tnxbeans.tnxTextField();
        butCoor = new javax.swing.JButton();
        labBancAbi = new javax.swing.JLabel();
        labBancCab = new javax.swing.JLabel();
        texTipoFattura = new tnxbeans.tnxTextField();
        texTipoFattura.setVisible(false);
        sepFaSeparatore = new javax.swing.JSeparator();
        labFaTitolo = new javax.swing.JLabel();
        labFa1 = new javax.swing.JLabel();
        comCausaleTrasporto = new tnxbeans.tnxComboField();
        labFa2 = new javax.swing.JLabel();
        comAspettoEsterioreBeni = new tnxbeans.tnxComboField();
        labFa3 = new javax.swing.JLabel();
        texNumeroColli = new tnxbeans.tnxTextField();
        labFa4 = new javax.swing.JLabel();
        labFa5 = new javax.swing.JLabel();
        comMezzoTrasporto = new tnxbeans.tnxComboField();
        labFa6 = new javax.swing.JLabel();
        comPorto = new tnxbeans.tnxComboField();
        butAddClie = new javax.swing.JButton();
        cheOpzioneRibaDestDiversa = new tnxbeans.tnxCheckBox();
        jLabel24 = new javax.swing.JLabel();
        texNotePagamento = new tnxbeans.tnxTextField();
        labAgente = new javax.swing.JLabel();
        comAgente = new tnxbeans.tnxComboField();
        labProvvigione = new javax.swing.JLabel();
        texProvvigione = new tnxbeans.tnxTextField();
        labPercentoProvvigione = new javax.swing.JLabel();
        texGiornoPagamento = new tnxbeans.tnxTextField();
        labGiornoPagamento = new javax.swing.JLabel();
        comVettori = new tnxbeans.tnxComboField();
        jLabel23 = new javax.swing.JLabel();
        texBancCab = new tnxbeans.tnxTextField();
        texRitenuta = new tnxbeans.tnxTextField();
        texRitenuta.setVisible(false);
        texTotaRitenuta = new tnxbeans.tnxTextField();
        texTotaRitenuta.setVisible(false);
        texTotaDaPagare = new tnxbeans.tnxTextField();
        texTotaDaPagare.setVisible(false);
        labFa7 = new javax.swing.JLabel();
        texDataOra = new tnxbeans.tnxTextField();
        texRivalsa = new tnxbeans.tnxTextField();
        texRivalsa.setVisible(false);
        texForni1 = new tnxbeans.tnxTextField();
        labMarcaBollo = new javax.swing.JLabel();
        texMarcaBollo = new tnxbeans.tnxTextField();
        texCliente = new javax.swing.JTextField();
        apriclienti = new BasicArrowButton(BasicArrowButton.SOUTH, UIManager.getColor("ComboBox.buttonBackground"), UIManager.getColor("ComboBox.buttonShadow"), UIManager.getColor("ComboBox.buttonDarkShadow"), UIManager.getColor("ComboBox.buttonHighlight"));
        labPesoLordo = new javax.swing.JLabel();
        texPesoLordo = new tnxbeans.tnxTextField();
        labPesoNetto = new javax.swing.JLabel();
        texPesoNetto = new tnxbeans.tnxTextField();
        datiRighe = new tnxbeans.tnxDbPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        griglia = new tnxbeans.tnxDbGrid();
        jPanel1 = new javax.swing.JPanel();
        butNuovArti = new javax.swing.JButton();
        butNuovoAnimale = new javax.swing.JButton();
        butNuovArti1 = new javax.swing.JButton();
        butImportRighe = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        panFoglioRighe = new javax.swing.JPanel();
        panGriglia = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        foglio = new JTableSs(){
            @Override
            public void setValueAt(Object value,int row,int column){
                super.setValueAt(value, row, column);
                ricalcolaTotali();
            }
        };
        panTotale = new javax.swing.JPanel();
        labStatus = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        comForni = new tnxbeans.tnxComboField();
        texForni = new tnxbeans.tnxTextField();
        jPanel5 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        butStampa = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        butUndo = new javax.swing.JButton();
        butSave = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        texTotaImpo = new tnxbeans.tnxTextField();
        texTotaIva = new tnxbeans.tnxTextField();
        texTota = new tnxbeans.tnxTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();

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

        popGrigAddUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/list-add.png"))); // NOI18N
        popGrigAddUp.setLabel("Aggiungi Riga");
        popGrigAddUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popGrigAddUpActionPerformed(evt);
            }
        });
        popGrig.add(popGrigAddUp);

        popFoglioElimina.setText("elimina");
        popFoglioElimina.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popFoglioEliminaActionPerformed(evt);
            }
        });
        popFoglio.add(popFoglioElimina);

        foglio3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        foglio3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                foglio3MouseClicked(evt);
            }
        });

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Fattura");
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosed(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
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
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });
        addVetoableChangeListener(new java.beans.VetoableChangeListener() {
            public void vetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {
                formVetoableChange(evt);
            }
        });

        tutto.setLayout(new java.awt.BorderLayout());

        tabDocumento.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabDocumentoStateChanged(evt);
            }
        });

        panDati.setLayout(new java.awt.BorderLayout());

        jSplitPane1.setDividerLocation(300);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setMinimumSize(new java.awt.Dimension(508, 200));

        dati.setMinimumSize(new java.awt.Dimension(0, 50));
        dati.setPreferredSize(new java.awt.Dimension(50, 100));
        dati.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        texNumePrev.setEditable(false);
        texNumePrev.setText("numero");
        texNumePrev.setDbDescCampo("");
        texNumePrev.setDbNomeCampo("numero");
        texNumePrev.setDbTipoCampo("testo");
        texNumePrev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                texNumePrevActionPerformed(evt);
            }
        });
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
        texClie.setDbTipoCampo("");
        texClie.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                texClieFocusLost(evt);
            }
        });
        texClie.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                texCliePropertyChange(evt);
            }
        });
        texClie.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                texClieKeyPressed(evt);
            }
        });
        dati.add(texClie, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 60, 45, 20));

        texSpeseIncasso.setText("spese_incasso");
        texSpeseIncasso.setDbDescCampo("");
        texSpeseIncasso.setDbNomeCampo("spese_incasso");
        texSpeseIncasso.setDbTipoCampo("valuta");
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
        dati.add(texSpeseIncasso, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 55, 85, 20));

        texScon2.setText("sconto2");
        texScon2.setToolTipText("secondo sconto");
        texScon2.setDbDescCampo("");
        texScon2.setDbNomeCampo("sconto2");
        texScon2.setDbTipoCampo("numerico");
        texScon2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                texScon2FocusLost(evt);
            }
        });
        texScon2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                texScon2KeyReleased(evt);
            }
        });
        dati.add(texScon2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 55, 30, 20));

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
        });
        dati.add(texScon1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 55, 30, 20));

        comClie.setDbNomeCampo("");
        comClie.setDbRiempire(false);
        comClie.setDbSalvare(false);
        comClie.setDbTextAbbinato(texClie);
        comClie.setDbTipoCampo("");
        comClie.setDbTrovaMentreScrive(true);
        comClie.setName("comClie"); // NOI18N
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
        dati.add(comClie, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 80, 35, 20));

        texTotaImpo1.setBackground(new java.awt.Color(255, 200, 200));
        texTotaImpo1.setText("0");
        texTotaImpo1.setDbDescCampo("");
        texTotaImpo1.setDbNomeCampo("totale_imponibile");
        texTotaImpo1.setDbTipoCampo("valuta");
        dati.add(texTotaImpo1, new org.netbeans.lib.awtextra.AbsoluteConstraints(705, 45, 60, -1));

        texTotaIva1.setBackground(new java.awt.Color(255, 200, 200));
        texTotaIva1.setText("0");
        texTotaIva1.setDbDescCampo("");
        texTotaIva1.setDbNomeCampo("totale_iva");
        texTotaIva1.setDbTipoCampo("valuta");
        dati.add(texTotaIva1, new org.netbeans.lib.awtextra.AbsoluteConstraints(705, 65, 60, -1));

        texTota1.setBackground(new java.awt.Color(255, 200, 200));
        texTota1.setText("0");
        texTota1.setDbDescCampo("");
        texTota1.setDbNomeCampo("totale");
        texTota1.setDbTipoCampo("valuta");
        dati.add(texTota1, new org.netbeans.lib.awtextra.AbsoluteConstraints(705, 85, 60, -1));

        texNote.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        texNote.setDbNomeCampo("note");
        texNote.setFont(texNote.getFont());
        texNote.setText("note");
        dati.add(texNote, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 80, 320, 35));

        jLabel13.setText("numero");
        dati.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 5, 45, 15));

        jLabel14.setText("serie");
        dati.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 5, 30, 15));

        jLabel15.setText("destinazione merce");
        dati.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 5, 155, 15));

        jLabel16.setText("data");
        dati.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(95, 5, 75, 15));

        labScon1.setText("sc 1");
        labScon1.setToolTipText("primo sconto o ricarico");
        dati.add(labScon1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 30, 15));

        labScon2.setText("sc 3");
        labScon2.setToolTipText("terzo sconto o ricarico");
        dati.add(labScon2, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 40, 45, 15));

        jLabel113.setText("spese incasso");
        dati.add(jLabel113, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 40, 85, -1));

        texData.setEditable(false);
        texData.setText("data");
        texData.setDbDescCampo("");
        texData.setDbNomeCampo("data");
        texData.setDbTipoCampo("data");
        texData.setmaxChars(10);
        texData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                texDataActionPerformed(evt);
            }
        });
        texData.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                texDataFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                texDataFocusLost(evt);
            }
        });
        dati.add(texData, new org.netbeans.lib.awtextra.AbsoluteConstraints(95, 20, 75, 20));

        jLabel11.setFont(jLabel11.getFont());
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Annotazioni");
        dati.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 75, -1));

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
        dati.add(texScon3, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 55, 30, 20));

        labScon21.setText("sc 2");
        labScon21.setToolTipText("secondo sconto o ricarico");
        dati.add(labScon21, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 40, 30, 15));

        jLabel151.setText("cliente");
        dati.add(jLabel151, new org.netbeans.lib.awtextra.AbsoluteConstraints(175, 5, 75, 15));

        comClieDest.setToolTipText("Premere invio per selezionarlo");
        comClieDest.setDbNomeCampo("");
        comClieDest.setDbRiempire(false);
        comClieDest.setDbSalvare(false);
        comClieDest.setDbTextAbbinato(texClieDest);
        comClieDest.setDbTipoCampo("");
        comClieDest.setDbTrovaMentreScrive(true);
        comClieDest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comClieDestActionPerformed(evt);
            }
        });
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
        dati.add(comClieDest, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 20, 260, 20));

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
        });
        dati.add(texSeri, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 30, 20));

        texAnno.setBackground(new java.awt.Color(255, 200, 200));
        texAnno.setText("anno");
        texAnno.setDbDescCampo("");
        texAnno.setDbNomeCampo("anno");
        texAnno.setDbTipoCampo("");
        dati.add(texAnno, new org.netbeans.lib.awtextra.AbsoluteConstraints(705, 20, 50, -1));

        texClieDest.setBackground(new java.awt.Color(255, 200, 200));
        texClieDest.setText("cliente_destinazione");
        texClieDest.setDbComboAbbinata(comClieDest);
        texClieDest.setDbDescCampo("");
        texClieDest.setDbNomeCampo("cliente_destinazione");
        texClieDest.setDbTipoCampo("numerico");
        dati.add(texClieDest, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 120, 45, -1));

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel17.setText("Vs. ordine");
        dati.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 75, 20));

        texPaga2.setText("riferimento");
        texPaga2.setDbDescCampo("");
        texPaga2.setDbNomeCampo("riferimento");
        texPaga2.setDbTipoCampo("");
        texPaga2.setmaxChars(255);
        dati.add(texPaga2, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 120, 320, -1));

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
        dati.add(texSpeseTrasporto, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 55, 90, 20));

        jLabel114.setText("spese trasporto");
        dati.add(jLabel114, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 40, 90, -1));

        labScon10.setFont(labScon10.getFont().deriveFont(labScon10.getFont().getSize()-1f));
        labScon10.setText("ragione sociale");
        labScon10.setToolTipText("");
        dati.add(labScon10, new org.netbeans.lib.awtextra.AbsoluteConstraints(435, 55, 80, 15));

        texDestRagioneSociale.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        texDestRagioneSociale.setToolTipText("");
        texDestRagioneSociale.setDbDescCampo("");
        texDestRagioneSociale.setDbNomeCampo("dest_ragione_sociale");
        texDestRagioneSociale.setDbTipoCampo("");
        texDestRagioneSociale.setFont(texDestRagioneSociale.getFont().deriveFont((float)9));
        dati.add(texDestRagioneSociale, new org.netbeans.lib.awtextra.AbsoluteConstraints(515, 55, 165, 15));

        labScon11.setFont(labScon11.getFont().deriveFont(labScon11.getFont().getSize()-1f));
        labScon11.setText("indirizzo");
        labScon11.setToolTipText("");
        dati.add(labScon11, new org.netbeans.lib.awtextra.AbsoluteConstraints(435, 70, 45, 15));

        texDestIndirizzo.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        texDestIndirizzo.setToolTipText("");
        texDestIndirizzo.setDbDescCampo("");
        texDestIndirizzo.setDbNomeCampo("dest_indirizzo");
        texDestIndirizzo.setDbTipoCampo("");
        texDestIndirizzo.setFont(texDestIndirizzo.getFont().deriveFont((float)9));
        dati.add(texDestIndirizzo, new org.netbeans.lib.awtextra.AbsoluteConstraints(485, 70, 195, 15));

        labScon12.setFont(labScon12.getFont().deriveFont(labScon12.getFont().getSize()-1f));
        labScon12.setText("cap");
        labScon12.setToolTipText("");
        dati.add(labScon12, new org.netbeans.lib.awtextra.AbsoluteConstraints(435, 85, 25, 15));

        texDestCap.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        texDestCap.setToolTipText("");
        texDestCap.setDbDescCampo("");
        texDestCap.setDbNomeCampo("dest_cap");
        texDestCap.setDbTipoCampo("");
        texDestCap.setFont(texDestCap.getFont().deriveFont((float)9));
        dati.add(texDestCap, new org.netbeans.lib.awtextra.AbsoluteConstraints(465, 85, 35, 15));

        texDestLocalita.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        texDestLocalita.setToolTipText("");
        texDestLocalita.setDbDescCampo("");
        texDestLocalita.setDbNomeCampo("dest_localita");
        texDestLocalita.setDbTipoCampo("");
        texDestLocalita.setFont(texDestLocalita.getFont().deriveFont((float)9));
        dati.add(texDestLocalita, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 85, 95, 15));

        labScon13.setFont(labScon13.getFont().deriveFont(labScon13.getFont().getSize()-1f));
        labScon13.setText("loc.");
        labScon13.setToolTipText("");
        dati.add(labScon13, new org.netbeans.lib.awtextra.AbsoluteConstraints(505, 85, 25, 15));

        texDestProvincia.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        texDestProvincia.setToolTipText("");
        texDestProvincia.setDbDescCampo("");
        texDestProvincia.setDbNomeCampo("dest_provincia");
        texDestProvincia.setDbTipoCampo("");
        texDestProvincia.setFont(texDestProvincia.getFont().deriveFont((float)9));
        dati.add(texDestProvincia, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 85, 20, 15));

        labScon14.setFont(labScon14.getFont().deriveFont(labScon14.getFont().getSize()-1f));
        labScon14.setText("prov.");
        labScon14.setToolTipText("");
        dati.add(labScon14, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 85, 25, 15));

        texDestCellulare.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        texDestCellulare.setToolTipText("");
        texDestCellulare.setDbDescCampo("");
        texDestCellulare.setDbNomeCampo("dest_cellulare");
        texDestCellulare.setDbTipoCampo("");
        texDestCellulare.setFont(texDestCellulare.getFont().deriveFont((float)9));
        dati.add(texDestCellulare, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 100, 70, 15));

        labScon15.setFont(labScon15.getFont().deriveFont(labScon15.getFont().getSize()-1f));
        labScon15.setText("cellulare");
        labScon15.setToolTipText("");
        dati.add(labScon15, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 100, 50, 15));

        texDestTelefono.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        texDestTelefono.setToolTipText("");
        texDestTelefono.setDbDescCampo("");
        texDestTelefono.setDbNomeCampo("dest_telefono");
        texDestTelefono.setDbTipoCampo("");
        texDestTelefono.setFont(texDestTelefono.getFont().deriveFont((float)9));
        dati.add(texDestTelefono, new org.netbeans.lib.awtextra.AbsoluteConstraints(485, 100, 70, 15));

        labScon16.setFont(labScon16.getFont().deriveFont(labScon16.getFont().getSize()-1f));
        labScon16.setText("telefono");
        labScon16.setToolTipText("");
        dati.add(labScon16, new org.netbeans.lib.awtextra.AbsoluteConstraints(435, 100, 50, -1));

        comPaese.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        comPaese.setDbNomeCampo("dest_paese");
        comPaese.setDbTipoCampo("");
        comPaese.setDbTrovaMentreScrive(true);
        comPaese.setFont(comPaese.getFont().deriveFont((float)9));
        dati.add(comPaese, new org.netbeans.lib.awtextra.AbsoluteConstraints(485, 115, 195, 15));

        labScon17.setFont(labScon17.getFont().deriveFont(labScon17.getFont().getSize()-1f));
        labScon17.setText("paese");
        labScon17.setToolTipText("");
        dati.add(labScon17, new org.netbeans.lib.awtextra.AbsoluteConstraints(435, 115, 50, 15));

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "destinazione diversa (manuale)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        dati.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 40, 265, 95));

        jButton1.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jButton1.setText("visualizza prezzi fatture");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        dati.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 5, 165, 15));

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText("Banca ABI");
        dati.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, 75, 20));

        comPaga.setDbDescCampo("");
        comPaga.setDbNomeCampo("pagamento");
        comPaga.setDbTipoCampo("VARCHAR");
        comPaga.setDbTrovaMentreScrive(true);
        comPaga.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comPagaItemStateChanged(evt);
            }
        });
        comPaga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comPagaActionPerformed(evt);
            }
        });
        comPaga.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                comPagaFocusLost(evt);
            }
        });
        dati.add(comPaga, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 145, 195, 20));

        butScad.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        butScad.setText("...");
        butScad.setToolTipText("Gestione Scadenze");
        butScad.setMargin(new java.awt.Insets(1, 1, 1, 1));
        butScad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butScadActionPerformed(evt);
            }
        });
        dati.add(butScad, new org.netbeans.lib.awtextra.AbsoluteConstraints(285, 145, 20, 20));

        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText("Pagamento");
        dati.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 145, 75, 20));

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel20.setText("IBAN");
        dati.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 250, 75, 20));

        texBancAbi.setToolTipText("");
        texBancAbi.setDbDescCampo("");
        texBancAbi.setDbNomeCampo("banca_abi");
        texBancAbi.setDbTipoCampo("");
        texBancAbi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                texBancAbiActionPerformed(evt);
            }
        });
        texBancAbi.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                texBancAbiFocusLost(evt);
            }
        });
        dati.add(texBancAbi, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 210, 50, 20));

        texBancIban.setToolTipText("");
        texBancIban.setDbDescCampo("");
        texBancIban.setDbNomeCampo("banca_iban");
        texBancIban.setDbTipoCampo("");
        texBancIban.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                texBancIbanActionPerformed(evt);
            }
        });
        texBancIban.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                texBancIbanFocusLost(evt);
            }
        });
        dati.add(texBancIban, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 250, 320, 20));

        butCoor.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        butCoor.setText("cerca");
        butCoor.setMargin(new java.awt.Insets(1, 2, 1, 2));
        butCoor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butCoorActionPerformed(evt);
            }
        });
        dati.add(butCoor, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 210, 40, 20));

        labBancAbi.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        labBancAbi.setText("...");
        dati.add(labBancAbi, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 210, 230, 20));

        labBancCab.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        labBancCab.setText("...");
        dati.add(labBancCab, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 230, 230, 20));

        texTipoFattura.setBackground(new java.awt.Color(255, 200, 200));
        texTipoFattura.setText("tipoFattura");
        texTipoFattura.setDbDescCampo("");
        texTipoFattura.setDbNomeCampo("tipo_fattura");
        texTipoFattura.setDbTipoCampo("numerico");
        dati.add(texTipoFattura, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 20, 75, -1));

        sepFaSeparatore.setOrientation(javax.swing.SwingConstants.VERTICAL);
        dati.add(sepFaSeparatore, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 125, 5, 175));

        labFaTitolo.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        labFaTitolo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labFaTitolo.setText("dati fattura accompagnatoria");
        dati.add(labFaTitolo, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 135, 305, -1));

        labFa1.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        labFa1.setText("Causale del trasporto");
        dati.add(labFa1, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 155, 115, 17));

        comCausaleTrasporto.setDbNomeCampo("causale_trasporto");
        comCausaleTrasporto.setDbRiempireForceText(true);
        comCausaleTrasporto.setDbSalvaKey(false);
        comCausaleTrasporto.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        dati.add(comCausaleTrasporto, new org.netbeans.lib.awtextra.AbsoluteConstraints(545, 155, 140, 17));

        labFa2.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        labFa2.setText("Aspetto esteriore beni");
        dati.add(labFa2, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 175, 110, 17));

        comAspettoEsterioreBeni.setDbNomeCampo("aspetto_esteriore_beni");
        comAspettoEsterioreBeni.setDbRiempireForceText(true);
        comAspettoEsterioreBeni.setDbSalvaKey(false);
        comAspettoEsterioreBeni.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        dati.add(comAspettoEsterioreBeni, new org.netbeans.lib.awtextra.AbsoluteConstraints(545, 175, 140, 17));

        labFa3.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        labFa3.setText("Data / ora");
        dati.add(labFa3, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 275, 60, 17));

        texNumeroColli.setText("numero_colli");
        texNumeroColli.setDbDescCampo("");
        texNumeroColli.setDbNomeCampo("numero_colli");
        texNumeroColli.setDbTipoCampo("");
        texNumeroColli.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        texNumeroColli.setmaxChars(255);
        dati.add(texNumeroColli, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 255, 80, 17));

        labFa4.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        labFa4.setText("1° Vettore");
        dati.add(labFa4, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 195, 60, 17));

        labFa5.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        labFa5.setText("Cons. o inizio trasp. a mezzo");
        dati.add(labFa5, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 215, 145, 17));

        comMezzoTrasporto.setDbNomeCampo("mezzo_consegna");
        comMezzoTrasporto.setDbRiempireForceText(true);
        comMezzoTrasporto.setDbSalvaKey(false);
        comMezzoTrasporto.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        dati.add(comMezzoTrasporto, new org.netbeans.lib.awtextra.AbsoluteConstraints(575, 215, 110, 17));

        labFa6.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        labFa6.setText("Porto");
        dati.add(labFa6, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 235, 60, 17));

        comPorto.setDbNomeCampo("porto");
        comPorto.setDbRiempireForceText(true);
        comPorto.setDbSalvaKey(false);
        comPorto.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        dati.add(comPorto, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 235, 195, 17));

        butAddClie.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        butAddClie.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/emblems/emblem-system.png"))); // NOI18N
        butAddClie.setToolTipText("Crea un nuovo cliente");
        butAddClie.setMargin(new java.awt.Insets(1, 1, 1, 1));
        butAddClie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butAddClieActionPerformed(evt);
            }
        });
        dati.add(butAddClie, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 20, 25, 20));

        cheOpzioneRibaDestDiversa.setText("stampa Dest. Diversa su Distinta Riba");
        cheOpzioneRibaDestDiversa.setToolTipText("Selezionando questa opzione stampa la Destinazione diversa nella Distinta delle RIBA");
        cheOpzioneRibaDestDiversa.setDbDescCampo("Opzione Dest. Diversa Riba");
        cheOpzioneRibaDestDiversa.setDbNomeCampo("opzione_riba_dest_diversa");
        cheOpzioneRibaDestDiversa.setDbTipoCampo("");
        cheOpzioneRibaDestDiversa.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        cheOpzioneRibaDestDiversa.setIconTextGap(1);
        dati.add(cheOpzioneRibaDestDiversa, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 190, 320, 18));

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel24.setText("note pagamento");
        dati.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 165, 75, 20));

        texNotePagamento.setText("note pagamento");
        texNotePagamento.setDbDescCampo("");
        texNotePagamento.setDbNomeCampo("note_pagamento");
        texNotePagamento.setDbTipoCampo("");
        texNotePagamento.setmaxChars(255);
        dati.add(texNotePagamento, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 165, 320, -1));

        labAgente.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labAgente.setText("Agente");
        dati.add(labAgente, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 275, 75, 20));

        comAgente.setToolTipText("");
        comAgente.setDbDescCampo("");
        comAgente.setDbNomeCampo("agente_codice");
        comAgente.setDbTipoCampo("numerico");
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
        dati.add(comAgente, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 275, 175, 20));

        labProvvigione.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labProvvigione.setText("Provvigione");
        dati.add(labProvvigione, new org.netbeans.lib.awtextra.AbsoluteConstraints(275, 275, 80, 20));

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
        dati.add(texProvvigione, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 275, 35, 20));

        labPercentoProvvigione.setText("%");
        dati.add(labPercentoProvvigione, new org.netbeans.lib.awtextra.AbsoluteConstraints(395, 275, 15, 20));

        texGiornoPagamento.setToolTipText("Giorno del mese per le scadenze");
        texGiornoPagamento.setDbDescCampo("");
        texGiornoPagamento.setDbNomeCampo("giorno_pagamento");
        texGiornoPagamento.setDbTipoCampo("numerico");
        dati.add(texGiornoPagamento, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 145, 30, 20));

        labGiornoPagamento.setText("giorno");
        dati.add(labGiornoPagamento, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 145, 50, 20));

        comVettori.setDbNomeCampo("vettore1");
        comVettori.setDbRiempireForceText(true);
        comVettori.setDbSalvaKey(false);
        comVettori.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        dati.add(comVettori, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 195, 195, 17));

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel23.setText("Banca CAB");
        dati.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 230, 75, 20));

        texBancCab.setToolTipText("");
        texBancCab.setDbDescCampo("");
        texBancCab.setDbNomeCampo("banca_cab");
        texBancCab.setDbTipoCampo("");
        texBancCab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                texBancCabActionPerformed(evt);
            }
        });
        texBancCab.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                texBancCabFocusLost(evt);
            }
        });
        dati.add(texBancCab, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 230, 50, 20));

        texRitenuta.setBackground(new java.awt.Color(255, 200, 200));
        texRitenuta.setDbDescCampo("");
        texRitenuta.setDbNomeCampo("ritenuta");
        texRitenuta.setDbTipoCampo("numerico");
        dati.add(texRitenuta, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 210, 60, -1));

        texTotaRitenuta.setBackground(new java.awt.Color(255, 200, 200));
        texTotaRitenuta.setText("0");
        texTotaRitenuta.setDbDescCampo("");
        texTotaRitenuta.setDbNomeCampo("totale_ritenuta");
        texTotaRitenuta.setDbTipoCampo("valuta");
        dati.add(texTotaRitenuta, new org.netbeans.lib.awtextra.AbsoluteConstraints(705, 155, 60, -1));

        texTotaDaPagare.setBackground(new java.awt.Color(255, 200, 200));
        texTotaDaPagare.setText("0");
        texTotaDaPagare.setDbDescCampo("");
        texTotaDaPagare.setDbNomeCampo("totale_da_pagare");
        texTotaDaPagare.setDbTipoCampo("valuta");
        dati.add(texTotaDaPagare, new org.netbeans.lib.awtextra.AbsoluteConstraints(705, 175, 60, -1));

        labFa7.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        labFa7.setText("Num. colli");
        dati.add(labFa7, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 255, 60, 17));

        texDataOra.setText("dataoraddt");
        texDataOra.setDbDescCampo("");
        texDataOra.setDbNomeCampo("dataoraddt");
        texDataOra.setDbTipoCampo("");
        texDataOra.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        texDataOra.setmaxChars(255);
        dati.add(texDataOra, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 275, 80, 17));

        texRivalsa.setBackground(new java.awt.Color(255, 200, 200));
        texRivalsa.setDbDescCampo("");
        texRivalsa.setDbNomeCampo("totaleRivalsa");
        texRivalsa.setDbTipoCampo("numerico");
        dati.add(texRivalsa, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 240, 60, -1));

        texForni1.setDbNomeCampo("fornitore");
        texForni1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                texForni1KeyPressed(evt);
            }
        });
        dati.add(texForni1, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 275, 70, -1));

        labMarcaBollo.setText("marche da bollo");
        dati.add(labMarcaBollo, new org.netbeans.lib.awtextra.AbsoluteConstraints(325, 40, 85, -1));

        texMarcaBollo.setText("marche_da_bollo");
        texMarcaBollo.setDbNomeCampo("marca_da_bollo");
        texMarcaBollo.setDbTipoCampo("valuta");
        dati.add(texMarcaBollo, new org.netbeans.lib.awtextra.AbsoluteConstraints(325, 55, 85, -1));
        dati.add(texCliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(175, 20, 190, -1));

        apriclienti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                apriclientiActionPerformed(evt);
            }
        });
        dati.add(apriclienti, new org.netbeans.lib.awtextra.AbsoluteConstraints(365, 20, 20, 20));

        labPesoLordo.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        labPesoLordo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labPesoLordo.setText("Peso lordo");
        dati.add(labPesoLordo, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 255, 60, 17));

        texPesoLordo.setText("peso_lordo");
        texPesoLordo.setDbDescCampo("");
        texPesoLordo.setDbNomeCampo("peso_lordo");
        texPesoLordo.setDbTipoCampo("");
        texPesoLordo.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        texPesoLordo.setmaxChars(255);
        dati.add(texPesoLordo, new org.netbeans.lib.awtextra.AbsoluteConstraints(635, 255, 50, 17));

        labPesoNetto.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        labPesoNetto.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labPesoNetto.setText("Perso netto");
        dati.add(labPesoNetto, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 275, 60, 17));

        texPesoNetto.setText("peso_netto");
        texPesoNetto.setDbDescCampo("");
        texPesoNetto.setDbNomeCampo("peso_netto");
        texPesoNetto.setDbTipoCampo("");
        texPesoNetto.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        texPesoNetto.setmaxChars(255);
        dati.add(texPesoNetto, new org.netbeans.lib.awtextra.AbsoluteConstraints(635, 275, 50, 17));

        jSplitPane1.setLeftComponent(dati);

        datiRighe.setLayout(new java.awt.BorderLayout());

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

        jPanel2.setLayout(new java.awt.GridLayout(1, 0));
        datiRighe.add(jPanel2, java.awt.BorderLayout.SOUTH);

        jSplitPane1.setRightComponent(datiRighe);

        panDati.add(jSplitPane1, java.awt.BorderLayout.CENTER);

        tabDocumento.addTab("Dati Fattura", panDati);

        panFoglioRighe.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                panFoglioRigheFocusGained(evt);
            }
        });
        panFoglioRighe.setLayout(new java.awt.BorderLayout());

        panGriglia.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                panGrigliaFocusGained(evt);
            }
        });
        panGriglia.setLayout(new java.awt.BorderLayout());

        foglio.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        foglio.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                foglioMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                foglioMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                foglioMouseReleased(evt);
            }
        });
        jScrollPane2.setViewportView(foglio);

        panGriglia.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        panFoglioRighe.add(panGriglia, java.awt.BorderLayout.CENTER);

        labStatus.setText("...");
        panTotale.add(labStatus);

        panFoglioRighe.add(panTotale, java.awt.BorderLayout.SOUTH);

        tabDocumento.addTab("Foglio Righe", panFoglioRighe);

        jLabel1.setText("Fornitore:");

        comForni.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comForni.setDbRiempire(false);
        comForni.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comForniItemStateChanged(evt);
            }
        });

        texForni.setDbNomeCampo("");
        texForni.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                texForniFocusLost(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, texForni, 0, 1, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(comForni, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 275, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(563, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .add(5, 5, 5)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(texForni, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(comForni, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(466, Short.MAX_VALUE))
        );

        tabDocumento.addTab("Seleziona Fornitore", jPanel7);

        tutto.add(tabDocumento, java.awt.BorderLayout.CENTER);

        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setPreferredSize(new java.awt.Dimension(130, 80));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        butStampa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/document-print.png"))); // NOI18N
        butStampa.setText("Stampa");
        butStampa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butStampaActionPerformed(evt);
            }
        });
        jPanel3.add(butStampa, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 45, 200, 30));

        jLabel3.setForeground(javax.swing.UIManager.getDefaults().getColor("Label.background"));
        jPanel3.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(111, 17, -1, -1));

        butUndo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/edit-undo.png"))); // NOI18N
        butUndo.setText("Annulla");
        butUndo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butUndoActionPerformed(evt);
            }
        });
        jPanel3.add(butUndo, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 5, 97, 35));

        butSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/devices/media-floppy.png"))); // NOI18N
        butSave.setText("Salva");
        butSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSaveActionPerformed(evt);
            }
        });
        jPanel3.add(butSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(108, 5, 97, 35));

        jPanel5.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 210, -1));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(" Totale "));
        jPanel4.setLayout(null);

        texTotaImpo.setBackground(javax.swing.UIManager.getDefaults().getColor("FormattedTextField.inactiveBackground"));
        texTotaImpo.setBorder(null);
        texTotaImpo.setEditable(false);
        texTotaImpo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        texTotaImpo.setText("0");
        texTotaImpo.setDbTipoCampo("valuta");
        jPanel4.add(texTotaImpo);
        texTotaImpo.setBounds(410, 10, 75, 20);

        texTotaIva.setBackground(javax.swing.UIManager.getDefaults().getColor("FormattedTextField.inactiveBackground"));
        texTotaIva.setBorder(null);
        texTotaIva.setEditable(false);
        texTotaIva.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        texTotaIva.setText("0");
        texTotaIva.setDbTipoCampo("valuta");
        jPanel4.add(texTotaIva);
        texTotaIva.setBounds(410, 32, 75, 20);

        texTota.setBackground(javax.swing.UIManager.getDefaults().getColor("FormattedTextField.inactiveBackground"));
        texTota.setBorder(null);
        texTota.setEditable(false);
        texTota.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        texTota.setText("0");
        texTota.setDbTipoCampo("valuta");
        texTota.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jPanel4.add(texTota);
        texTota.setBounds(410, 54, 75, 20);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Totale");
        jPanel4.add(jLabel2);
        jLabel2.setBounds(335, 54, 70, 20);

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel21.setText("Totale Iva");
        jPanel4.add(jLabel21);
        jLabel21.setBounds(325, 32, 80, 20);

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel25.setText("Totale Imponibile");
        jPanel4.add(jLabel25);
        jLabel25.setBounds(300, 10, 105, 20);

        jPanel5.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 0, 495, 80));

        tutto.add(jPanel5, java.awt.BorderLayout.SOUTH);

        getContentPane().add(tutto, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void texNumePrevFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texNumePrevFocusLost
        final String old_id_final = old_id;
        if (!old_id.equals(texNumePrev.getText())) {

            //controllo che se è un numero già presente non glielo faccio fare percè altrimenti sovrascrive una altra fattura
            sql = "select numero from test_fatt";
            sql += " where serie " + Db.pcW(this.prev.serie, "VARCHAR");
            sql += " and numero " + Db.pcW(texNumePrev.getText(), "NUMBER");
            sql += " and anno " + Db.pcW(String.valueOf(this.prev.anno), "VARCHAR");
            ResultSet r = Db.openResultSet(sql);
            try {
                if (r.next()) {
                    texNumePrev.setText(old_id_final);
                    JOptionPane.showMessageDialog(this, "Non puoi mettere il numero di una fattura già presente !", "Attenzione", JOptionPane.INFORMATION_MESSAGE);
                    return;
                } else {
                    //controllo se presente in distinta riba
                    sql = "select id, data_scadenza, importo, distinta from scadenze";
                    sql += " where documento_tipo = " + Db.pc(Db.TIPO_DOCUMENTO_FATTURA, Types.VARCHAR);
                    sql += " and documento_serie = " + Db.pc(prev.serie, Types.VARCHAR);
                    sql += " and documento_numero = " + Db.pc(old_id_final, Types.INTEGER);
                    sql += " and documento_anno = " + Db.pc(prev.anno, Types.INTEGER);
                    sql += " and distinta is not null";
                    try {
                        ResultSet resu = DbUtils.tryOpenResultSet(Db.getConn(), sql);
                        if (resu.next() == true) {
                            String msg = "La fattura e' legata ad una o piu' scadenze gia' stampate in distinta\nProsegunedo nel cambio del numero la distinta presentata non corrisponderà con questa fattura\nProsegui ?";
                            int ret = javax.swing.JOptionPane.showConfirmDialog(null, msg, "Attenzione", javax.swing.JOptionPane.YES_NO_OPTION);
                            if (ret == javax.swing.JOptionPane.NO_OPTION) {
                                texNumePrev.setText(old_id_final);
                                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                                return;
                            }
                        }
                        resu.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    //associo al nuovo numero
                    prev.numero = new Integer(this.texNumePrev.getText()).intValue();

                    sql = "update righ_fatt";
                    sql += " set numero = " + Db.pc(prev.numero, "NUMBER");
//                    sql += " where serie " + Db.pcW(this.prev.serie, "VARCHAR");
//                    sql += " and numero " + Db.pcW(old_id, "NUMBER");
//                    sql += " and anno " + Db.pcW(String.valueOf(this.prev.anno), "VARCHAR");
                    sql += " where id_padre = " + this.id;
                    Db.executeSql(sql);

                    sql = "update test_fatt";
                    sql += " set numero = " + Db.pc(prev.numero, "NUMBER");
//                    sql += " where serie " + Db.pcW(this.prev.serie, "VARCHAR");
//                    sql += " and numero " + Db.pcW(old_id, "NUMBER");
//                    sql += " and anno " + Db.pcW(String.valueOf(this.prev.anno), "VARCHAR");
                    sql += " where id = " + this.id;
                    Db.executeSql(sql);

                    dati.dbChiaveValori.clear();
                    dati.dbChiaveValori.put("serie", prev.serie);
                    dati.dbChiaveValori.put("numero", prev.numero);
                    dati.dbChiaveValori.put("anno", prev.anno);

                    //riassocio
                    dbAssociaGrigliaRighe();
                    id_modificato = true;

                    prev.numero = Integer.parseInt(texNumePrev.getText());
                    prev.id = id;
                    doc.load(Db.INSTANCE, this.prev.numero, this.prev.serie, this.prev.anno, Db.TIPO_DOCUMENTO_FATTURA, id);
                    ricalcolaTotali();

                    //vado ad aggiornare eventuali ddt o ordini legati
                    sql = "update test_ddt";
                    sql += " set fattura_numero = " + Db.pc(prev.numero, "NUMBER");
                    sql += " where fattura_serie " + Db.pcW(this.prev.serie, "VARCHAR");
                    sql += " and fattura_numero " + Db.pcW(old_id_final, "NUMBER");
                    sql += " and fattura_anno " + Db.pcW(String.valueOf(this.prev.anno), "VARCHAR");
                    Db.executeSql(sql);

                    //vado ad aggiornare eventuali ddt o ordini legati
                    sql = "update test_ordi";
                    sql += " set doc_numero = " + Db.pc(prev.numero, "NUMBER");
                    sql += " where doc_serie " + Db.pcW(this.prev.serie, "VARCHAR");
                    sql += " and doc_numero " + Db.pcW(old_id_final, "NUMBER");
                    sql += " and doc_anno " + Db.pcW(String.valueOf(this.prev.anno), "VARCHAR");
                    sql += " and doc_tipo " + Db.pcW(String.valueOf(this.prev.tipoDocumento), "VARCHAR");
                    Db.executeSql(sql);

                    //aggiorno scadenze
                    sql = "update scadenze";
                    sql += " set documento_numero = " + Db.pc(prev.numero, "NUMBER");
                    sql += " where documento_serie " + Db.pcW(prev.serie, "VARCHAR");
                    sql += " and documento_numero " + Db.pcW(old_id_final, "NUMBER");
                    sql += " and documento_anno " + Db.pcW(String.valueOf(prev.anno), "VARCHAR");
                    sql += " and documento_tipo " + Db.pcW(Db.TIPO_DOCUMENTO_FATTURA, "VARCHAR");
                    Db.executeSql(sql);

                    //aggiorno provvigioni
                    sql = "update provvigioni";
                    sql += " set documento_numero = " + Db.pc(prev.numero, "NUMBER");
                    sql += " where documento_serie " + Db.pcW(prev.serie, "VARCHAR");
                    sql += " and documento_numero " + Db.pcW(old_id_final, "NUMBER");
                    sql += " and documento_anno " + Db.pcW(String.valueOf(prev.anno), "VARCHAR");
                    sql += " and documento_tipo " + Db.pcW(Db.TIPO_DOCUMENTO_FATTURA, "VARCHAR");
                    Db.executeSql(sql);

                }
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }//GEN-LAST:event_texNumePrevFocusLost

    private void texNumePrevFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texNumePrevFocusGained
        System.err.println("old_id = " + texNumePrev.getText() + " da texNumePrevFocusGained");
        old_id = texNumePrev.getText();
        id_modificato = false;
    }//GEN-LAST:event_texNumePrevFocusGained

    private void texSpeseTrasportoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texSpeseTrasportoKeyPressed
// TODO add your handling code here:
    }//GEN-LAST:event_texSpeseTrasportoKeyPressed

    private void texSpeseIncassoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texSpeseIncassoFocusLost
        ricalcolaTotali();
    }//GEN-LAST:event_texSpeseIncassoFocusLost

    private void texSpeseTrasportoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texSpeseTrasportoFocusLost
        ricalcolaTotali();
    }//GEN-LAST:event_texSpeseTrasportoFocusLost

    private void texScon3FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texScon3FocusLost
        ricalcolaTotali();
    }//GEN-LAST:event_texScon3FocusLost

    private void foglioMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_foglioMouseClicked
        }//GEN-LAST:event_foglioMouseClicked

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
    }//GEN-LAST:event_formKeyPressed

    private void tabDocumentoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabDocumentoStateChanged

        if (tabDocumento.getSelectedIndex() == 1) {

            //visualizzo il pannellino per lengthdescrizioni multiriga
            zoom.setVisible(true);

            //associo il nuovo foglio
            ResultSet resu = Db.openResultSet(sqlGriglia);
            loadingFoglio = true;

            int rowCount = 0;

            try {

                while (resu.next()) {
                    foglio.setValueAt(resu.getString(4), rowCount, 0);
                    foglio.setValueAt(resu.getString(6), rowCount, 1);
                    foglio.setValueAt(resu.getString(7), rowCount, 2);
                    foglio.setValueAt(resu.getString(8), rowCount, 3);
                    foglio.setValueAt(resu.getString(9), rowCount, 4);
                    foglio.setValueAt(Db.formatValuta(resu.getDouble(10)), rowCount, 5);
                    foglio.setValueAt(resu.getString(11), rowCount, 6);
                    foglio.setValueAt(resu.getString(12), rowCount, 7);
                    foglio.setValueAt(resu.getString(13), rowCount, 9);

                    //calcolo importi riga
                    String temp = "";
                    double importo = 0;
                    double sconto1 = 0;
                    double sconto2 = 0;
                    double quantita = 0;

                    try {
                        sconto1 = resu.getDouble("sconto1");
                        sconto2 = resu.getDouble("sconto2");
                        quantita = it.tnx.Util.getDouble(resu.getString("quantita"));
                        importo = resu.getDouble("prezzo");
                    } catch (java.lang.NumberFormatException err4) {
                    }

                    importo = importo - (importo / 100 * sconto1);
                    importo = importo - (importo / 100 * sconto2);
                    importo = importo * quantita;

                    if (importo != 0) {
                        foglio.setValueAt(it.tnx.Util.format2Decimali(importo), rowCount, 8);
                    }

                    rowCount++;
                }
            } catch (Exception err) {
                err.printStackTrace();
            }

            loadingFoglio = false;
            ricalcolaTotali();
        } else {

            try {
                zoom.setVisible(false);
            } catch (Exception err) {
            }
        }
    }//GEN-LAST:event_tabDocumentoStateChanged

    private void panGrigliaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_panGrigliaFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_panGrigliaFocusGained

    private void panFoglioRigheFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_panFoglioRigheFocusGained
                            }//GEN-LAST:event_panFoglioRigheFocusGained

    private void popFoglioEliminaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popFoglioEliminaActionPerformed

        DefaultTableModel tableModel = (DefaultTableModel) foglio.getModel();
        tableModel.removeRow(foglio.getSelectedRow());
    }//GEN-LAST:event_popFoglioEliminaActionPerformed

    private void foglio3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_foglio3MouseClicked

        if (evt.getModifiers() == InputEvent.BUTTON3_MASK) {
            popFoglio.show(foglio, evt.getX(), evt.getY());
        }//GEN-LAST:event_foglio3MouseClicked
    }

    private void comPagaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comPagaItemStateChanged
        if (evt != null && evt.getStateChange() == ItemEvent.DESELECTED) {
            return;
        }

        texGiornoPagamento.setVisible(false);
        labGiornoPagamento.setVisible(false);

        try {
            ResultSet r = Db.lookUp(String.valueOf(comPaga.getSelectedKey()), "codice", "pagamenti");
            if (Db.nz(r.getString("flag_richiedi_giorno"), "N").equalsIgnoreCase("S")) {
                texGiornoPagamento.setVisible(true);
                labGiornoPagamento.setVisible(true);
                if (!in_apertura) {
                    //carico il giorno dal cliente
                    texGiornoPagamento.setText("");
                    //li recupero dal cliente
                    ResultSet tempClie;
                    String sql = "select giorno_pagamento from clie_forn";
                    sql += " where codice = " + Db.pc(this.texClie.getText(), "NUMERIC");
                    tempClie = Db.openResultSet(sql);
                    try {
                        if (tempClie.next() == true) {
                            texGiornoPagamento.setText(tempClie.getString("giorno_pagamento"));
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } catch (Exception err) {
            System.out.println(err);
        }
    }//GEN-LAST:event_comPagaItemStateChanged

    private void texSeriKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texSeriKeyPressed

        if (evt.getKeyCode() == evt.VK_TAB || evt.getKeyCode() == evt.VK_ENTER) {
            String serie = texSeri.getText();
            if (serie.equals("#") || serie.equals("*")) {
                JOptionPane.showMessageDialog(this, "Non si puo' usare '#' o '*' come serie del documento", "Attenzione", JOptionPane.WARNING_MESSAGE);
                texSeri.setText("");
                return;
            }
            assegnaSerie();
        }
    }//GEN-LAST:event_texSeriKeyPressed

    private void comAgenteFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_comAgenteFocusLost
        InvoicexUtil.controlloProvvigioniAutomatiche(comAgente, texProvvigione, texScon1, this, cu.toInteger(texClie.getText()));
    }//GEN-LAST:event_comAgenteFocusLost

    private void comAgenteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comAgenteActionPerformed
                            }//GEN-LAST:event_comAgenteActionPerformed

    private void comPagaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_comPagaFocusLost

        //carico note su pagamento
        try {

            ResultSet p = Db.openResultSet("select * from pagamenti where codice = " + Db.pc(this.comPaga.getSelectedKey(), Types.VARCHAR));

            if (p.next()) {
                this.texNotePagamento.setText(p.getString("note_su_documenti"));
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }//GEN-LAST:event_comPagaFocusLost

    private void comClieDestKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_comClieDestKeyPressed

        if (evt.getKeyCode() == evt.VK_ENTER) {
            caricaDestinazioneDiversa();
        }
    }//GEN-LAST:event_comClieDestKeyPressed

    private void comClieDestFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_comClieDestFocusLost
        if (comClieDest.getSelectedIndex() != comClieDest_old) {
            caricaDestinazioneDiversa();
        }
    }//GEN-LAST:event_comClieDestFocusLost

    private void butNuovoAnimaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butNuovoAnimaleActionPerformed

        Animale animale = new Animale();
        int riga = -1;
        animale.loadAnimale(gestioneFatture.logic.documenti.Util.TIPO_DOCUMENTO_FATTURA, this.texSeri.getText(), Integer.parseInt(this.texNumePrev.getText()), Integer.parseInt(this.texAnno.getText()), riga);

        JInternalFrameAnimaleFattura frameAnimale = new JInternalFrameAnimaleFattura(this, animale);
        main.getPadre().openFrame(frameAnimale, 450, 200);
    }//GEN-LAST:event_butNuovoAnimaleActionPerformed

    private void comClieActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comClieActionPerformed
//        sql = "select obsoleto from clie_forn";
//        sql += " where codice = " + Db.pc(this.texClie.getText(), "NUMERIC");
//
//        ResultSet rs = Db.openResultSet(sql);
//        try {
//            if (rs.next()) {
//                int obsoleto = rs.getInt("obsoleto");
//                if (obsoleto == 1) {
//                    JOptionPane.showMessageDialog(this, "Attenzione, il cliente selezionato è segnato come obsoleto.", "Cliente obsoleto", JOptionPane.INFORMATION_MESSAGE);
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

        //debug
        System.out.println(">selected");

        //apro combo destinazione cliente
        sql = "select ragione_sociale,codice from clie_forn_dest";
        sql += " where codice_cliente = " + Db.pc(this.texClie.getText(), "NUMERIC");
        sql += " order by ragione_sociale";

        riempiDestDiversa(sql);

        if (main.getPersonal().equals(main.PERSONAL_CUCINAIN)) {
            listiniTicket();
        }

//            //SAMUELE INNOCENTI
//            //costringo l'utente a selezionare la destinazione diversa (se vuole)
//            this.comClieDest.setSelectedItem(null);
//            //quando cambia l'utente pulisco la destinazione diversa
//            texDestRagioneSociale.setText(null);
//            texDestIndirizzo.setText(null);
//            texDestCap.setText(null);
//            texDestLocalita.setText(null);
//            texDestProvincia.setText(null);
//            texDestTelefono.setText(null);
//            texDestCellulare.setText(null);                

    }//GEN-LAST:event_comClieActionPerformed

    private void texDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texDataActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_texDataActionPerformed

    private void butAddClieActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butAddClieActionPerformed

        frmClie frameAggiungiCliente = new frmClie();

        //frameAggiungiCliente.setBounds(100,100,300,300);
        //frameAggiungiCliente.show();

        if (main.getPersonal().equals(main.PERSONAL_CUCINAIN)) {
            main.getPadre().openFrame(frameAggiungiCliente, 450, 350);
        } else {
            main.getPadre().openFrame(frameAggiungiCliente, 750, 620);
        }

//        frameAggiungiCliente.padre = this;
//        frameAggiungiCliente.addNew(this.comClie);
    }//GEN-LAST:event_butAddClieActionPerformed

    private void butStampaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butStampaActionPerformed

        if (SwingUtils.showYesNoMessage(this, "Prima di stampare è necessario salvare il documento, proseguire ?")) {
            if (controlloCampi() == true) {
                this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                try {
                    InvoicexEvent event = new InvoicexEvent(this);
                    event.type = InvoicexEvent.TYPE_FRMTESTFATT_PRIMA_DI_SAVE;
                    main.events.fireInvoicexEvent(event);
                } catch (Exception err) {
                    err.printStackTrace();
                }
                saveDocumento();
                //scateno evento salvataggio nuova fattura
                if (dbStato.equalsIgnoreCase(this.DB_INSERIMENTO)) {
                    try {
                        InvoicexEvent event = new InvoicexEvent(this);
                        event.type = InvoicexEvent.TYPE_SALVA_NUOVA_FATTURA;
                        event.serie = texSeri.getText();
                        event.numero = Integer.parseInt(texNumePrev.getText());
                        event.anno = Integer.parseInt(texAnno.getText());
                        main.events.fireInvoicexEvent(event);
                    } catch (Exception err) {
                        err.printStackTrace();
                    }
                }
                main.events.fireInvoicexEvent(new InvoicexEvent(this, InvoicexEvent.TYPE_SAVE));
                if (from != null) {
                    this.from.dbRefresh();
                }
                //rinizializzo le cose che iniz all'inizio
                totaleIniziale = doc.getTotale();
                totaleDaPagareIniziale = doc.getTotale_da_pagare();
                pagamentoIniziale = comPaga.getText();
                pagamentoInizialeGiorno = texGiornoPagamento.getText();
                provvigioniInizialeScadenze = dumpScadenze();
                provvigioniIniziale = Db.getDouble(texProvvigione.getText());
                codiceAgenteIniziale = it.tnx.Util.getInt(comAgente.getSelectedKey().toString());
                //proseguo con la stampa
                String tf = "";

                if (prev.tipoFattura == prev.TIPO_FATTURA_ACCOMPAGNATORIA) {
                    tf = "FA";
                }
                if (prev.tipoFattura == prev.TIPO_FATTURA_NOTA_DI_CREDITO) {
                    tf = "NC";
                }
                String dbSerie = this.prev.serie;
                int dbNumero = this.prev.numero;
                int dbAnno = this.prev.anno;
                this.dati.dbSave();
                frmElenFatt.stampa(tf, dbSerie, dbNumero, dbAnno, null);
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        }

    }//GEN-LAST:event_butStampaActionPerformed

    private void texSpeseTrasportoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texSpeseTrasportoActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_texSpeseTrasportoActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        showPrezziFatture();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void texSpeseTrasportoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texSpeseTrasportoKeyReleased

        try {
            prev.speseTrasportoIva = Db.getDouble(this.texSpeseTrasporto.getText());
        } catch (Exception err) {
            prev.speseTrasportoIva = 0;
        }

        ricalcolaTotali();
    }//GEN-LAST:event_texSpeseTrasportoKeyReleased

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
                            }//GEN-LAST:event_formInternalFrameClosing

    private void texBancAbiFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texBancAbiFocusLost
        texBancAbiActionPerformed(null);
    }//GEN-LAST:event_texBancAbiFocusLost

    private void texBancIbanFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texBancIbanFocusLost
}//GEN-LAST:event_texBancIbanFocusLost

    private void texBancCCFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texBancCCFocusLost
        //MC271102 ho ridisegnato la form e non ricordo a cosa serve
        /*
        if (Util.getScreenResolution() < Util.SCREEN_RES_1024x768) {
        if (evt.getOppositeComponent().getName().equalsIgnoreCase("butNuovArti")) {
        this.jScrollPane3.getViewport().setViewPosition(new java.awt.Point(0, 150));
        } else {
        this.jScrollPane3.getViewport().setViewPosition(new java.awt.Point(0, 0));
        }
        }
         */
    }//GEN-LAST:event_texBancCCFocusLost

    private void texBancIbanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texBancIbanActionPerformed
}//GEN-LAST:event_texBancIbanActionPerformed

    private void texBancAbiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texBancAbiActionPerformed

        try {
            this.labBancAbi.setText(Db.lookUp(this.texBancAbi.getText(), "abi", "banche_abi").getString(2));
        } catch (Exception err) {
            this.labBancAbi.setText("");

            //err.printStackTrace();
        }
    }//GEN-LAST:event_texBancAbiActionPerformed

    private void butCoorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butCoorActionPerformed

        CoordinateBancarie coords = new CoordinateBancarie();
        coords.setField_texBancAbi(this.texBancAbi);
        coords.setField_labBancAbi(this.labBancAbi);
        coords.setField_texBancCab(this.texBancCab);
        coords.setField_labBancCab(this.labBancCab);

        frmListCoorBanc frm = new frmListCoorBanc(coords);
        main.getPadre().openFrame(frm, 700, 500, 150, 50);
    }//GEN-LAST:event_butCoorActionPerformed

    private void comClieItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comClieItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
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
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }//GEN-LAST:event_comClieItemStateChanged

    private void butUndo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butUndo1ActionPerformed

    }//GEN-LAST:event_butUndo1ActionPerformed

    private void butScadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butScadActionPerformed
        saveDocumento();

        Scadenze tempScad = new Scadenze(Db.TIPO_DOCUMENTO_FATTURA, this.texSeri.getText(), this.prev.numero, this.prev.anno, this.comPaga.getText());
        frmPagaPart frm = new frmPagaPart(tempScad, null);
        main.getPadre().openFrame(frm, 650, 550, 300, 100);
    }//GEN-LAST:event_butScadActionPerformed

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

        if (!old_anno.equals(getAnnoDaForm())) {
            if (dbStato == DB_INSERIMENTO) {
                prev.dbRicalcolaProgressivo(dbStato, this.texData.getText(), this.texNumePrev, texAnno, texSeri.getText(), id);
                prev.numero = new Integer(this.texNumePrev.getText()).intValue();
                id_modificato = true;
            } else {
                //controllo che se è un numero già presente non glielo faccio fare percè altrimenti sovrascrive una altra fattura
                sql = "select numero from test_fatt";
                sql += " where serie " + Db.pcW(this.prev.serie, "VARCHAR");
                sql += " and numero " + Db.pcW(texNumePrev.getText(), "NUMBER");
                sql += " and anno " + Db.pcW(getAnnoDaForm(), "VARCHAR");
                ResultSet r = Db.openResultSet(sql);
                try {
                    if (r.next()) {
                        texData.setText(old_data);
                        JOptionPane.showMessageDialog(this, "Non puoi mettere questo numero e data, si sovrappongono ad una fattura già presente !", "Attenzione", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            try {
                texAnno.setText(getAnnoDaForm());
                prev.anno = Integer.parseInt(getAnnoDaForm());
                prev.numero = Integer.parseInt(texNumePrev.getText());

                sql = "update righ_fatt";
                sql += " set anno = " + Db.pc(prev.anno, "NUMBER");
                sql += " , numero = " + Db.pc(prev.numero, "NUMBER");
//                sql += " where serie " + Db.pcW(this.prev.serie, "VARCHAR");
//                sql += " and numero " + Db.pcW(old_id, "NUMBER");
//                sql += " and anno " + Db.pcW(old_anno, "VARCHAR");
                sql += " where id_padre = " + this.id;
                Db.executeSql(sql);

                sql = "update test_fatt";
                sql += " set anno = " + Db.pc(prev.anno, "NUMBER");
                sql += " , numero = " + Db.pc(prev.numero, "NUMBER");
//                sql += " where serie " + Db.pcW(this.prev.serie, "VARCHAR");
//                sql += " and numero " + Db.pcW(old_id, "NUMBER");
//                sql += " and anno " + Db.pcW(old_anno, "VARCHAR");
                sql += " where id = " + this.id;
                Db.executeSql(sql);

                dati.dbChiaveValori.clear();
                dati.dbChiaveValori.put("serie", prev.serie);
                dati.dbChiaveValori.put("numero", prev.numero);
                dati.dbChiaveValori.put("anno", prev.anno);

                //riassocio
                dbAssociaGrigliaRighe();

                doc.load(Db.INSTANCE, prev.numero, prev.serie, prev.anno, Db.TIPO_DOCUMENTO_FATTURA, id);
                ricalcolaTotali();

                anno_modificato = true;

                //vado ad aggiornare eventuali ddt o ordini legati
                sql = "update test_ddt";
                sql += " set fattura_numero = " + Db.pc(prev.numero, "NUMBER");
                sql += " , anno = " + Db.pc(prev.anno, "NUMBER");
                sql += " where fattura_serie " + Db.pcW(this.prev.serie, "VARCHAR");
                sql += " and fattura_numero " + Db.pcW(old_id, "NUMBER");
                sql += " and fattura_anno " + Db.pcW(String.valueOf(old_anno), "VARCHAR");
                Db.executeSql(sql);

                //vado ad aggiornare eventuali ddt o ordini legati
                sql = "update test_ordi";
                sql += " set doc_numero = " + Db.pc(prev.numero, "NUMBER");
                sql += " , anno = " + Db.pc(prev.anno, "NUMBER");
                sql += " where doc_serie " + Db.pcW(this.prev.serie, "VARCHAR");
                sql += " and doc_numero " + Db.pcW(old_id, "NUMBER");
                sql += " and doc_anno " + Db.pcW(String.valueOf(old_anno), "VARCHAR");
                sql += " and doc_tipo " + Db.pcW(String.valueOf(this.prev.tipoDocumento), "VARCHAR");
                Db.executeSql(sql);

            } catch (Exception err) {
                err.printStackTrace();
            }
        }

    }//GEN-LAST:event_texDataFocusLost

    private void texScon3KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texScon3KeyReleased

        try {
            prev.sconto3 = Db.getDouble(this.texScon3.getText());
        } catch (Exception err) {

            //err.printStackTrace();
            prev.sconto3 = 0;
        }

        ricalcolaTotali();
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

            String sql = "select clie_forn.codice as id, clie_forn.ragione_sociale as ragione, clie_forn.indirizzo as indi from clie_forn " + "where clie_forn.ragione_sociale like '%" + Db.aa(this.comClie.getText()) + "%'" + " order by clie_forn.ragione_sociale";
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
        }//GEN-LAST:event_texClieKeyPressed
    }

    private void comClieFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_comClieFocusLost
        if (comClie.getSelectedIndex() != comClieSel_old) {
            this.recuperaDatiCliente();
            ricalcolaTotali();
        }
    }//GEN-LAST:event_comClieFocusLost

    private void texClieFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texClieFocusLost

        try {

            if (this.texClie.getText().length() > 0) {
                this.prev.forceCliente(Long.parseLong(this.texClie.getText()));
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
        ricalcolaTotali();
    }//GEN-LAST:event_texClieFocusLost

    private void texSpeseIncassoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texSpeseIncassoKeyReleased

        try {
            prev.speseIncassoIva = Db.getDouble(this.texSpeseIncasso.getText());
        } catch (Exception err) {
            prev.speseIncassoIva = 0;
        }

        ricalcolaTotali();
    }//GEN-LAST:event_texSpeseIncassoKeyReleased

    private void texScon2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texScon2KeyReleased

        try {
            prev.sconto2 = Db.getDouble(this.texScon2.getText());
        } catch (Exception err) {

            //err.printStackTrace();
            prev.sconto2 = 0;
        }

        ricalcolaTotali();
    }//GEN-LAST:event_texScon2KeyReleased

    private void texScon1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texScon1KeyReleased

        try {
            prev.sconto1 = Db.getDouble(this.texScon1.getText());
        } catch (Exception err) {
            prev.sconto1 = 0;
        }

        ricalcolaTotali();
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

        iniFileProp fileIni = main.fileIni;

        if (fileIni.getValueBoolean("pref", "provvigioniAutomatiche", false)) {
            this.comAgenteFocusLost(null);
        }
    }//GEN-LAST:event_texScon1FocusLost

    private void texScon1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texScon1ActionPerformed
    }//GEN-LAST:event_texScon1ActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
    }//GEN-LAST:event_formInternalFrameOpened

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed

        // Add your handling code here:
        if (tim != null) {
            tim.cancel();
        }
        if (zoom != null) {
            zoom.dispose();
        }
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
        Integer id_riga = CastUtils.toInteger(griglia.getValueAt(griglia.getSelectedRow(), griglia.getColumnByName("id")));
        Integer id_padre = this.id;

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
//            frmNuovRiga frm = new frmNuovRiga(this, this.dati.DB_MODIFICA, this.texSeri.getText(), Integer.valueOf(this.texNumePrev.getText()).intValue(), "P", Integer.valueOf(this.griglia.getValueAt(griglia.getSelectedRow(), 3).toString()).intValue(), Integer.valueOf(this.texAnno.getText()).intValue(), codiceListino, codiceCliente);
//            temp.openFrame(frm, 600, 350, 100, 100);
//            frm.setStato();
//        } else {
//            frmNuovRigaDescrizioneMultiRiga frm = new frmNuovRigaDescrizioneMultiRiga(this, this.dati.DB_MODIFICA, this.texSeri.getText(), Integer.valueOf(this.texNumePrev.getText()).intValue(), "P", Integer.valueOf(this.griglia.getValueAt(griglia.getSelectedRow(), 3).toString()).intValue(), Integer.valueOf(this.texAnno.getText()).intValue(), codiceListino, codiceCliente);
        try {
            JInternalFrame frm = null;
            int w = 650;
            int h = 400;
            int top = 100;
            int left = 100;
            if (main.getPersonalContain("frajor")) {
                frmNuovRigaDescrizioneMultiRigaNewFrajor temp_form = new frmNuovRigaDescrizioneMultiRigaNewFrajor(this, this.dati.DB_MODIFICA, this.texSeri.getText(), Integer.valueOf(this.texNumePrev.getText()).intValue(), "P", Integer.valueOf(this.griglia.getValueAt(griglia.getSelectedRow(), 3).toString()).intValue(), Integer.valueOf(this.texAnno.getText()).intValue(), codiceListino, codiceCliente);
                temp_form.setStato();
                w = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRigaFrajor_w", "770"));
                h = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRigaFrajor_h", "660"));
                top = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRigaFrajor_top", "100"));
                left = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRigaFrajor_left", "100"));
                frm = temp_form;
            } else {
                frmNuovRigaDescrizioneMultiRigaNew temp_form = new frmNuovRigaDescrizioneMultiRigaNew(this, this.dati.DB_MODIFICA, this.texSeri.getText(), Integer.valueOf(this.texNumePrev.getText()).intValue(), "P", Integer.valueOf(this.griglia.getValueAt(griglia.getSelectedRow(), 3).toString()).intValue(), Integer.valueOf(this.texAnno.getText()).intValue(), codiceListino, codiceCliente, id_riga, id_padre);
                temp_form.setStato();
                w = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRiga_w", "760"));
                h = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRiga_h", "460"));
                top = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRiga_top", "100"));
                left = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRiga_left", "100"));
                frm = temp_form;
            }
            main.getPadre().openFrame(frm, w, h, top, left);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
//        }
    }//GEN-LAST:event_popGrigModiActionPerformed

    private void butNuovArtiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butNuovArtiActionPerformed

        String codiceListino = "1";

        try {
            codiceListino = Db.lookUp(this.texClie.getText(), "codice", "clie_forn").getString("codice_listino");
        } catch (NullPointerException nerr) {
            System.err.println(nerr.toString());
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
                temp_form.setStato();
                w = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRigaFrajor_w", "700"));
                h = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRigaFrajor_h", "660"));
                top = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRigaFrajor_top", "100"));
                left = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRigaFrajor_left", "100"));
                frm = temp_form;
            } else {
                frmNuovRigaDescrizioneMultiRigaNew temp_form = new frmNuovRigaDescrizioneMultiRigaNew(this, this.dati.DB_INSERIMENTO, this.texSeri.getText(), Integer.valueOf(this.texNumePrev.getText()).intValue(), "P", 0, Integer.valueOf(this.texAnno.getText()).intValue(), codiceListino, codiceCliente, null, this.id);
                temp_form.setStato();
                w = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRiga_w", "760"));
                h = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRiga_h", "460"));
                top = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRiga_top", "100"));
                left = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRiga_left", "100"));
                frm = temp_form;
                temp_form.texProvvigione.setText(texProvvigione.getText());
            }

            main.getPadre().openFrame(frm, w, h, top, left);
        } catch (Exception e) {
            e.printStackTrace();
            SwingUtils.showErrorMessage(main.getPadre(), e.toString());
        }
//        }
    }//GEN-LAST:event_butNuovArtiActionPerformed

    private void butUndoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butUndoActionPerformed
        lockableUI.setLocked(true);
        org.jdesktop.swingworker.SwingWorker worker = new org.jdesktop.swingworker.SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                annulla2();
                return null;
            }

            @Override
            protected void done() {
                lockableUI.setLocked(false);
                dispose();
            }
        };
        worker.execute();
    }//GEN-LAST:event_butUndoActionPerformed

    public void annulla() {
        butUndoActionPerformed(null);
    }

    private void butSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveActionPerformed
        if (controlloCampi() == true) {
            try {
                InvoicexEvent event = new InvoicexEvent(this);
                event.type = InvoicexEvent.TYPE_FRMTESTFATT_PRIMA_DI_SAVE;
                main.events.fireInvoicexEvent(event);
            } catch (Exception err) {
                err.printStackTrace();
            }

            saveDocumento();

            //scateno evento salvataggio nuova fattura
            if (dbStato.equalsIgnoreCase(this.DB_INSERIMENTO)) {
                try {
                    InvoicexEvent event = new InvoicexEvent(this);
                    event.type = InvoicexEvent.TYPE_SALVA_NUOVA_FATTURA;
                    event.serie = texSeri.getText();
                    event.numero = Integer.parseInt(texNumePrev.getText());
                    event.anno = Integer.parseInt(texAnno.getText());
                    main.events.fireInvoicexEvent(event);
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

            main.events.fireInvoicexEvent(new InvoicexEvent(this, InvoicexEvent.TYPE_SAVE));

            this.dispose();
            if (from != null) {
                this.from.dbRefresh();
            }
        }
    }//GEN-LAST:event_butSaveActionPerformed

    private void texDataFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texDataFocusGained
        old_anno = getAnnoDaForm();
        old_data = texData.getText();
        System.err.println("old_id = " + texNumePrev.getText() + " da texDataFocusGained");
        old_id = texNumePrev.getText();
        anno_modificato = false;
    }//GEN-LAST:event_texDataFocusGained

    private void texBancCabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texBancCabActionPerformed
        trovaCab();
}//GEN-LAST:event_texBancCabActionPerformed

    private void texBancCabFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texBancCabFocusLost
        trovaCab();
}//GEN-LAST:event_texBancCabFocusLost

private void butNuovArti1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butNuovArti1ActionPerformed
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
        String sql = "select riga from righ_fatt";
//        sql += " where serie = " + Db.pc(dbSerie, "VARCHAR");
//        sql += " and numero = " + dbNumero;
//        sql += " and anno = " + dbAnno;
        sql += " where id_padre = " + this.id;
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
        }
    }

    sql = "insert into righ_fatt (serie, numero, anno, riga, codice_articolo, descrizione, id_padre) values (";
    sql += db.pc(dbSerie, "VARCHAR");
    sql += ", " + db.pc(dbNumero, "NUMBER");
    sql += ", " + db.pc(dbAnno, "NUMBER");
    sql += ", " + db.pc(riga, "NUMBER");
    sql += ", ''";
    sql += ", 'Peso totale Kg. " + it.tnx.Util.format2Decimali(doc.totalePeso) + "'";
    sql += ", " + Db.pc(id, Types.INTEGER);
    sql += ")";
    Db.executeSql(sql);

    griglia.dbRefresh();

}//GEN-LAST:event_butNuovArti1ActionPerformed

private void comClieDestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comClieDestActionPerformed
}//GEN-LAST:event_comClieDestActionPerformed

private void comClieFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_comClieFocusGained
    comClieSel_old = comClie.getSelectedIndex();
}//GEN-LAST:event_comClieFocusGained

private void comClieDestFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_comClieDestFocusGained
    comClieDest_old = comClieDest.getSelectedIndex();
}//GEN-LAST:event_comClieDestFocusGained

private void popGrigAddUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popGrigAddUpActionPerformed
    int numCol = this.griglia.getColumnByName("riga");
    int numRiga = this.griglia.getSelectedRow();
    int value = (Integer) this.griglia.getValueAt(numRiga, numCol);

    try {

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
//            frmNuovRiga frm = new frmNuovRiga(this, this.dati.DB_INSERIMENTO, this.texSeri.getText(), Integer.valueOf(this.texNumePrev.getText()).intValue(), "P", value, Integer.valueOf(this.texAnno.getText()).intValue(), codiceListino, codiceCliente);
//            temp.openFrame(frm, 600, 350, 100, 100);
//            frm.setStato();
//        } else {
//            frmNuovRigaDescrizioneMultiRiga frm = new frmNuovRigaDescrizioneMultiRiga(this, this.dati.DB_INSERIMENTO, this.texSeri.getText(), Integer.valueOf(this.texNumePrev.getText()).intValue(), "P", value, Integer.valueOf(this.texAnno.getText()).intValue(), codiceListino, codiceCliente);
        try {
            JInternalFrame frm = null;
            int w = 650;
            int h = 400;
            int top = 100;
            int left = 100;
            if (main.getPersonalContain("frajor")) {
                frmNuovRigaDescrizioneMultiRigaNewFrajor temp_form = new frmNuovRigaDescrizioneMultiRigaNewFrajor(this, tnxDbPanel.DB_INSERIMENTO, this.texSeri.getText(), Integer.valueOf(this.texNumePrev.getText()).intValue(), "P", value, Integer.valueOf(this.texAnno.getText()).intValue(), codiceListino, codiceCliente);
                temp_form.setStato();
                w = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRigaFrajor_w", "700"));
                h = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRigaFrajor_h", "660"));
                top = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRigaFrajor_top", "100"));
                left = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRigaFrajor_left", "100"));
                frm = temp_form;
            } else {
                frmNuovRigaDescrizioneMultiRigaNew temp_form = new frmNuovRigaDescrizioneMultiRigaNew(this, tnxDbPanel.DB_INSERIMENTO, this.texSeri.getText(), Integer.valueOf(this.texNumePrev.getText()).intValue(), "P", value, Integer.valueOf(this.texAnno.getText()).intValue(), codiceListino, codiceCliente);
                temp_form.setStato();
                w = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRiga_w", "760"));
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
//        }
    } catch (Exception e) {
        e.printStackTrace();
    }


}//GEN-LAST:event_popGrigAddUpActionPerformed

private void texScon3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texScon3ActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_texScon3ActionPerformed

private void formVetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {//GEN-FIRST:event_formVetoableChange

    boolean tr = false;
    if (evt.getPropertyName().equals(IS_CLOSED_PROPERTY)) {
        boolean changed = ((Boolean) evt.getNewValue()).booleanValue();
        if (changed) {
            try {
                if (dati.dbCheckModificati() || (doc.getTotale() != this.totaleIniziale || !this.pagamentoInizialeGiorno.equals(this.texGiornoPagamento.getText()) || !this.pagamentoIniziale.equals(this.comPaga.getText()) || doc.getTotale_da_pagare() != this.totaleDaPagareIniziale)) {
                    FxUtils.fadeBackground(butSave, Color.RED);
                    int confirm = JOptionPane.showOptionDialog(this,
                            "<html><b>Chiudi " + getTitle() + "?</b><br>Hai fatto delle modifiche e così verranno <b>perse</b> !<br>Per salvarle devi cliccare sul pulsante <b>Salva</b> in basso a sinistra<br>",
                            "Conferma chiusura",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null, null, null);
                    if (confirm == 0) {
                    } else {
                        tr = true;
                    }
                }
            } catch (Exception e) {
            }

            if (tr) {
                throw new PropertyVetoException("Cancelled", null);
            } else {
//                if (dbStato.equalsIgnoreCase(this.DB_INSERIMENTO)) {
                    butUndoActionPerformed(null);
//                }
            }
        }
    }

}//GEN-LAST:event_formVetoableChange

private void texCliePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_texCliePropertyChange
    System.out.println("texCliePropertyChange:" + evt.getPropertyName());
    if (evt.getPropertyName().equalsIgnoreCase("text")) {
        System.out.println("stop");
    }
}//GEN-LAST:event_texCliePropertyChange

private void texNumePrevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texNumePrevActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_texNumePrevActionPerformed

private void texForni1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texForni1KeyPressed
    // TODO add your handling code here:
}//GEN-LAST:event_texForni1KeyPressed

private void texForniFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texForniFocusLost
    this.texForni1.setText(texForni.getText());
}//GEN-LAST:event_texForniFocusLost

private void comForniItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comForniItemStateChanged
    if (evt.getStateChange() == ItemEvent.SELECTED) {
        this.texForni.setText(String.valueOf(comForni.getSelectedKey()));
        this.texForni1.setText(String.valueOf(comForni.getSelectedKey()));
    }
}//GEN-LAST:event_comForniItemStateChanged

private void grigliaMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_grigliaMousePressed
    if (evt.isPopupTrigger()) {
        int numRow = this.griglia.getSelectedRow();
        if (numRow != 0) {
            this.popGrigAddUp.setText("Inserisci nuova fra riga " + numRow + " e riga " + (numRow + 1));
        } else {
            this.popGrigAddUp.setText("Inserisci nuova riga all'inizio");
        }
        popGrig.show(griglia, evt.getX(), evt.getY());
    }
}//GEN-LAST:event_grigliaMousePressed

private void grigliaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_grigliaMouseReleased
    if (evt.isPopupTrigger()) {
        int numRow = this.griglia.getSelectedRow();
        if (numRow != 0) {
            this.popGrigAddUp.setText("Inserisci nuova fra riga " + numRow + " e riga " + (numRow + 1));
        } else {
            this.popGrigAddUp.setText("Inserisci nuova riga all'inizio");
        }
        popGrig.show(griglia, evt.getX(), evt.getY());
    }
}//GEN-LAST:event_grigliaMouseReleased

private void foglioMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_foglioMousePressed
    if (evt.isPopupTrigger()) {
        popFoglio.show(foglio, evt.getX(), evt.getY());
    }
}//GEN-LAST:event_foglioMousePressed

private void foglioMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_foglioMouseReleased
    if (evt.isPopupTrigger()) {
        popFoglio.show(foglio, evt.getX(), evt.getY());
    }
}//GEN-LAST:event_foglioMouseReleased

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
            int idPadre = this.id;
            InvoicexUtil.importCSV(Db.TIPO_DOCUMENTO_FATTURA, f, serie, numero, anno, idPadre, nomeListino);
            griglia.dbRefresh();
            ricalcolaTotali();
            JOptionPane.showMessageDialog(this, "Righe caricate correttamente", "Esecuzione terminata", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_butImportRigheActionPerformed

private void texProvvigioneFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texProvvigioneFocusLost
    if (!StringUtils.equals(texProvvigione.getName(), texProvvigione.getText()) && griglia.getRowCount() > 0) {
        aggiornareProvvigioni();
    }
}//GEN-LAST:event_texProvvigioneFocusLost

private void texProvvigioneFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texProvvigioneFocusGained
    texProvvigione.setName(texProvvigione.getText());
}//GEN-LAST:event_texProvvigioneFocusGained

private void apriclientiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_apriclientiActionPerformed
    alRicercaCliente.showHints();
}//GEN-LAST:event_apriclientiActionPerformed

private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown

}//GEN-LAST:event_formComponentShown

    private void comPagaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comPagaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comPagaActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton apriclienti;
    private javax.swing.JButton butAddClie;
    private javax.swing.JButton butCoor;
    private javax.swing.JButton butImportRighe;
    private javax.swing.JButton butNuovArti;
    private javax.swing.JButton butNuovArti1;
    private javax.swing.JButton butNuovoAnimale;
    private javax.swing.JButton butSave;
    private javax.swing.JButton butScad;
    private javax.swing.JButton butStampa;
    public javax.swing.JButton butUndo;
    private tnxbeans.tnxCheckBox cheOpzioneRibaDestDiversa;
    private tnxbeans.tnxComboField comAgente;
    private tnxbeans.tnxComboField comAspettoEsterioreBeni;
    private tnxbeans.tnxComboField comCausaleTrasporto;
    public tnxbeans.tnxComboField comClie;
    private tnxbeans.tnxComboField comClieDest;
    private tnxbeans.tnxComboField comForni;
    private tnxbeans.tnxComboField comMezzoTrasporto;
    private tnxbeans.tnxComboField comPaese;
    private tnxbeans.tnxComboField comPaga;
    private tnxbeans.tnxComboField comPorto;
    private tnxbeans.tnxComboField comVettori;
    public tnxbeans.tnxDbPanel dati;
    private tnxbeans.tnxDbPanel datiRighe;
    private it.tnx.gui.JTableSs foglio;
    private javax.swing.JTable foglio3;
    public tnxbeans.tnxDbGrid griglia;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel113;
    private javax.swing.JLabel jLabel114;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel151;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    public javax.swing.JPanel jPanel4;
    public javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JLabel labAgente;
    private javax.swing.JLabel labBancAbi;
    private javax.swing.JLabel labBancCab;
    private javax.swing.JLabel labFa1;
    private javax.swing.JLabel labFa2;
    private javax.swing.JLabel labFa3;
    private javax.swing.JLabel labFa4;
    private javax.swing.JLabel labFa5;
    private javax.swing.JLabel labFa6;
    private javax.swing.JLabel labFa7;
    private javax.swing.JLabel labFaTitolo;
    private javax.swing.JLabel labGiornoPagamento;
    private javax.swing.JLabel labMarcaBollo;
    private javax.swing.JLabel labPercentoProvvigione;
    private javax.swing.JLabel labPesoLordo;
    private javax.swing.JLabel labPesoNetto;
    private javax.swing.JLabel labProvvigione;
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
    public javax.swing.JLabel labStatus;
    private javax.swing.JPanel panDati;
    private javax.swing.JPanel panFoglioRighe;
    private javax.swing.JPanel panGriglia;
    private javax.swing.JPanel panTotale;
    private javax.swing.JPopupMenu popFoglio;
    private javax.swing.JMenuItem popFoglioElimina;
    private javax.swing.JPopupMenu popGrig;
    private javax.swing.JMenuItem popGrigAddUp;
    private javax.swing.JMenuItem popGrigElim;
    private javax.swing.JMenuItem popGrigModi;
    private javax.swing.JSeparator sepFaSeparatore;
    private javax.swing.JTabbedPane tabDocumento;
    private tnxbeans.tnxTextField texAnno;
    private tnxbeans.tnxTextField texBancAbi;
    private tnxbeans.tnxTextField texBancCab;
    private tnxbeans.tnxTextField texBancIban;
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
    private tnxbeans.tnxTextField texForni;
    private tnxbeans.tnxTextField texForni1;
    public tnxbeans.tnxTextField texGiornoPagamento;
    private tnxbeans.tnxTextField texMarcaBollo;
    private tnxbeans.tnxMemoField texNote;
    private tnxbeans.tnxTextField texNotePagamento;
    private tnxbeans.tnxTextField texNumePrev;
    private tnxbeans.tnxTextField texNumeroColli;
    private tnxbeans.tnxTextField texPaga2;
    private tnxbeans.tnxTextField texPesoLordo;
    private tnxbeans.tnxTextField texPesoNetto;
    private tnxbeans.tnxTextField texProvvigione;
    public tnxbeans.tnxTextField texRitenuta;
    public tnxbeans.tnxTextField texRivalsa;
    public tnxbeans.tnxTextField texScon1;
    public tnxbeans.tnxTextField texScon2;
    public tnxbeans.tnxTextField texScon3;
    private tnxbeans.tnxTextField texSeri;
    public tnxbeans.tnxTextField texSpeseIncasso;
    public tnxbeans.tnxTextField texSpeseTrasporto;
    private tnxbeans.tnxTextField texTipoFattura;
    public tnxbeans.tnxTextField texTota;
    private tnxbeans.tnxTextField texTota1;
    public tnxbeans.tnxTextField texTotaDaPagare;
    public tnxbeans.tnxTextField texTotaImpo;
    private tnxbeans.tnxTextField texTotaImpo1;
    public tnxbeans.tnxTextField texTotaIva;
    private tnxbeans.tnxTextField texTotaIva1;
    public tnxbeans.tnxTextField texTotaRitenuta;
    private javax.swing.JPanel tutto;
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

        //campi += "quantita,";
        campi += "REPLACE(REPLACE(REPLACE(FORMAT(quantita,2),'.','X'),',','.'),'X',',') AS quantita,";
        campi += "prezzo, ";
        campi += "sconto1, ";
        campi += "sconto2, ";
        campi += "iva, ";
        campi += "if(sconto1 != 0 and sconto2 != 0, CONCAT(cast(sconto1 as CHAR), '+' , cast(sconto2 as CHAR)), if(sconto1 != 0 and sconto2 = 0, cast(sconto1 as CHAR),if(sconto1 = 0 and sconto2 != 0, cast(sconto2 as CHAR), ''))) as Sconti,";
        campi += "(prezzo*quantita) - ((prezzo*quantita)*sconto1/100) - ( ((prezzo*quantita) - ((prezzo*quantita)*sconto1/100)) * sconto2 / 100) as Totale ";
        campi += ", id";
        campi += ", provvigione";

//        String sql = "select " + campi + " from righ_fatt" + " where serie = " + db.pc(this.prev.serie, "VARCHAR") + " and numero = " + this.prev.numero + " and anno = " + db.pc(this.prev.anno, "INTEGER");
        String sql = "select " + campi + " from righ_fatt";
        sql += " where id_padre = " + id;    //per non selezionare li scontrini!!
        sql += " order by riga";


        //debug
        //javax.swing.JOptionPane.showMessageDialog(null,sql);
        System.out.println("sql associa griglia:" + sql);
        this.sqlGriglia = sql;
        griglia.setNoTnxResize(true);
        System.err.println("this visible " + this.isVisible());
        this.griglia.dbOpen(db.getConn(), sql);
    }

    public void recuperaDatiFornitore() {

        //li recupero dal cliente
        ResultSet tempForni;
        String sql = "select * from clie_forn";
        sql += " where codice = " + Db.pc(this.texForni.getText(), "NUMERIC");
        tempForni = Db.openResultSet(sql);

        try {

            if (tempForni.next() == true) {
                int codice = tempForni.getInt("codice");

                boolean continua = true;
                for (int i = 0; i < comForni.getItemCount() && continua; i++) {
                    int codice_sel = (Integer) comForni.getKey(i);

                    if (codice_sel == codice) {
                        comForni.setSelectedIndex(i);
                        continua = false;
                    }
                }
                if (continua) {
                    javax.swing.JOptionPane.showMessageDialog(this, "Il codice cliente specificato non esiste in anagrafica !");
                }
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "Il codice cliente specificato non esiste in anagrafica !");
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public void recuperaDatiCliente() {

        try {
            if (this.texClie.getText().length() > 0) {
                this.prev.forceCliente(Long.parseLong(this.texClie.getText()));
            }
        } catch (Exception err) {
            err.printStackTrace();
        }

        //li recupero dal cliente
        ResultSet tempClie;
        String sql = "select * from clie_forn";
        sql += " where codice = " + Db.pc(this.texClie.getText(), "NUMERIC");
        tempClie = Db.openResultSet(sql);

        try {

            if (tempClie.next() == true) {

                //this.texPaga.setText(Db.nz(tempClie.getString("pagamento"),""));
                //if (Db.nz(tempClie.getString("pagamento"),"").length() > 0) this.comPaga.setText(Db.nz(tempClie.getString("pagamento"),""));
                if (Db.nz(tempClie.getString("pagamento"), "").length() > 0) {
                    this.comPaga.dbTrovaRiga(Db.nz(tempClie.getString("pagamento"), ""));

                }
                comPagaFocusLost(null);

//                if (Db.nz(tempClie.getString("banca_abi"), "").length() > 0) {
                this.texBancAbi.setText(Db.nz(tempClie.getString("banca_abi"), ""));
//                }
//                if (Db.nz(tempClie.getString("banca_cab"), "").length() > 0) {
                this.texBancCab.setText(Db.nz(tempClie.getString("banca_cab"), ""));
//                }
//                if (Db.nz(tempClie.getString("banca_cc_iban"), "").length() > 0) {
                this.texBancIban.setText(Db.nz(tempClie.getString("banca_cc_iban"), ""));
//                }

                //if (Db.nz(tempClie.getString("banca_cc"),"").length() > 0) this.texBancCC.setText(Db.nz(tempClie.getString("banca_cc"),""));
                //cerca lengthdescrizioni
                texBancAbiActionPerformed(null);
                trovaCab();

                //opzione dest diversa riba
                if (tempClie.getString("opzione_riba_dest_diversa") != null && tempClie.getString("opzione_riba_dest_diversa").equalsIgnoreCase("S")) {
                    this.cheOpzioneRibaDestDiversa.setSelected(true);
                } else {
                    this.cheOpzioneRibaDestDiversa.setSelected(false);
                }

                if (tempClie.getInt("agente") >= 0) {
                    this.comAgente.dbTrovaKey(tempClie.getString("agente"));
                    comAgenteFocusLost(null);
                }

                InvoicexUtil.fireEvent(this, InvoicexEvent.TYPE_FRMTESTFATT_CARICA_DATI_CLIENTE, tempClie);
            } else {
                //javax.swing.JOptionPane.showMessageDialog(this,"Il codice cliente specificato non esiste in anagrafica !");
                //spostato il controllo su controllaCampi
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    private void trovaAbi() {

        try {
            this.labBancAbi.setText(Db.lookUp(this.texBancAbi.getText(), "abi", "banche_abi").getString(2));
        } catch (Exception err) {
            this.labBancAbi.setText("");

            //err.printStackTrace();
        }
    }

    private void trovaCab() {

        try {

            String sql = "";
            sql += "select banche_cab.cap,";
            sql += " banche_cab.indirizzo,";
            sql += " comuni.comune,";
            sql += " comuni.provincia";
            sql += " from banche_cab left join comuni on banche_cab.codice_comune = comuni.codice";
            sql += " where banche_cab.abi = " + Db.pc(this.texBancAbi.getText(), "VARCHAR");
            sql += " and banche_cab.cab = " + Db.pc(this.texBancCab.getText(), "VARCHAR");

            ResultSet temp = Db.openResultSet(sql);

            if (temp.next()) {
                this.labBancCab.setText(Db.nz(temp.getString(1), "") + " " + Db.nz(temp.getString(2), "") + ", " + Db.nz(temp.getString(3), "") + " (" + Db.nz(temp.getString(4), "") + ")");
            } else {
                this.labBancCab.setText("");
            }
        } catch (Exception err) {
            this.labBancCab.setText("");
        }
    }

    private boolean controlloCampi() {

        //controllo cliente
        //li recupero dal cliente
        ResultSet tempClie;
        String sql = "select * from clie_forn";
        sql += " where codice = " + Db.pc(this.texClie.getText(), "NUMERIC");
        tempClie = Db.openResultSet(sql);

        if (prev.totale < 0) {
            int res = javax.swing.JOptionPane.showConfirmDialog(this, "Il totale risulta negativo.\nSolitamente devono essere in positivo.\nVuoi continuare comunque?", "Conferma Dati", JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.NO_OPTION) {
                return false;
            }
        }
        try {

            if (tempClie.next() != true) {
                tabDocumento.setSelectedIndex(0);
                texCliente.requestFocus();
                javax.swing.JOptionPane.showMessageDialog(this, "Il codice cliente specificato non esiste in anagrafica !");
                return false;
            }
        } catch (Exception err) {
            err.printStackTrace();
        }

        //controllo pagamento
        ResultSet temp;
        boolean ok = true;
        boolean flagCoordinate = false;
        sql = "select * from pagamenti where codice = " + Db.pc(this.comPaga.getSelectedKey().toString(), "VARCHAR");
        temp = Db.openResultSet(sql);

        try {

            if (temp.next() == true && comPaga.getSelectedKey().toString().length() > 0) {

                if (temp.getString("coordinate_necessarie").equalsIgnoreCase("S")) {

                    //servono lengthcoordinate, cotnrollare che ci siano i 3 ccampi della banca
                    if (this.texBancAbi.getText().length() == 0 || this.texBancCab.getText().length() == 0) {
                        flagCoordinate = true;
                        ok = false;
                    }
                }
            } else {
                /* DAVID */
                
                tabDocumento.setSelectedIndex(0);
                comPaga.requestFocus();
                javax.swing.JOptionPane.showMessageDialog(this, "Manca il tipo di pagamento (e' obbligatorio)", "Attenzione", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                ok = false;
                
                /* DAVID */
            }
        } catch (Exception err) {
            err.printStackTrace();
        }

        if (ok == false && flagCoordinate == true) {

            //uscire per mancanza coordinate
            int ret = javax.swing.JOptionPane.showConfirmDialog(this, "Mancano le coordinate bancarie per il tipo di pagamento scelto\nContinuare ugualmente?", "Attenzione", javax.swing.JOptionPane.YES_NO_OPTION);

            if (ret == javax.swing.JOptionPane.YES_OPTION) {

                return true;
            } else {

                return false;
            }
        } else if (ok == false) {

            return false;
        } else {

            //alri controlli
            return true;
        }
    }

    private void showPrezziFatture() {
        try {
            frmPrezziFatturePrecedenti form = new frmPrezziFatturePrecedenti(Integer.parseInt(this.texClie.getText().toString()), null, Db.TIPO_DOCUMENTO_FATTURA);
            main.getPadre().openFrame(form, 450, 500, this.getX() + this.getWidth() - 200, this.getY() + 50);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void confermaAnimale(Animale animale) {
        System.out.println("aggiugnere:" + animale);
        animale.saveAnimale();
        this.griglia.dbRefresh();
    }

    public String getSerie() {

        return this.texSeri.getText();
    }

    public String getNumero() {

        return this.texNumePrev.getText();
    }

    public String getAnno() {
        return this.texAnno.getText();
    }

    public void ricalcolaTotali() {
        if (loadingFoglio) {
            return;
        }

        try {

            //this.parent.prev.dbRefresh();
            //provo con nuova classe Documento
            if (texClie.getText() != null && texClie.getText().length() > 0) {
                try {
                    doc.setCodiceCliente(Long.parseLong(texClie.getText()));
                } catch (NumberFormatException ex0) {
                    return;
                }
            }

            doc.setScontoTestata1(Db.getDouble(texScon1.getText()));
            doc.setScontoTestata2(Db.getDouble(texScon2.getText()));
            doc.setScontoTestata3(Db.getDouble(texScon3.getText()));
            doc.setSpeseIncasso(Db.getDouble(texSpeseIncasso.getText()));
            doc.setSpeseTrasporto(Db.getDouble(texSpeseTrasporto.getText()));
            doc.setRitenuta(0);
            try {
                InvoicexEvent event = new InvoicexEvent(this);
                event.type = InvoicexEvent.TYPE_FRMTESTFATT_RICALCOLA_TOTALI_1;
                main.events.fireInvoicexEvent(event);
            } catch (Exception err) {
                err.printStackTrace();
            }
            doc.calcolaTotali();
            texTota.setText(it.tnx.Util.formatValutaEuro(doc.getTotale()));
            texTotaImpo.setText(it.tnx.Util.formatValutaEuro(doc.getTotaleImponibile()));
            texTotaIva.setText(it.tnx.Util.formatValutaEuro(doc.getTotaleIva()));

            //test rivalsa inps
            Component comp = SwingUtils.getCompByName(jPanel4, "texRivalsaPerc");
            if (comp != null) {
//                labRivInps.setText("rivvvvvv");
                System.out.println("comp:" + comp);
            }

            try {
                InvoicexEvent event = new InvoicexEvent(this);
                event.type = InvoicexEvent.TYPE_FRMTESTFATT_RICALCOLA_TOTALI_2;
                main.events.fireInvoicexEvent(event);
            } catch (Exception err) {
                err.printStackTrace();
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    private void annulla2() {
        try {
            conn.rollback();
        } catch (SQLException ex) {
            SwingUtils.showExceptionMessage(main.getPadreFrame(), ex);
        }

        if (from != null) {
            this.from.dbRefresh();
        }
    }

    private String dumpScadenze() {
        try {
            return DebugUtils.dumpAsString(DbUtils.getListMap(Db.getConn(), "select data_scadenza, importo from scadenze where documento_tipo = 'FA' and documento_serie = '" + texSeri.getText() + "' and documento_numero = " + texNumePrev.getText() + " and documento_anno = " + texAnno.getText()));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private Double dumpProvvigioni() {
        try {
            String sql = "select sum(importo_provvigione) from provvigioni where documento_tipo = 'FA' and documento_serie = '" + texSeri.getText() + "' and documento_numero = " + texNumePrev.getText() + " and documento_anno = " + texAnno.getText();
            System.out.println("sql = " + sql);
            return  it.tnx.Util.round(((BigDecimal) DbUtils.getObject(Db.getConn(), sql)).doubleValue(), 2);
        } catch (NullPointerException ne) {
            return 0d;
        } catch (Exception e) {
            e.printStackTrace();
            return 0d;
        }
    }

    public void aggiornareProvvigioni() {
        if (SwingUtils.showYesNoMessage(this, "Vuoi aggiornare le provvigioni delle righe già inserite alla nuova provvigione ?")) {
            String sql = "update righ_fatt set provvigione = " + Db.pc2(texProvvigione.getText(), Types.DOUBLE) + " where id_padre = " + id;
            try {
                DbUtils.tryExecQuery(Db.getConn(), sql);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public JTable getGrid() {
        return griglia;
    }

    public tnxDbPanel getDatiPanel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public JTabbedPane getTab() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public tnxTextField getTexClie() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean isAcquisto() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
class timerRefreshFattura extends java.util.TimerTask {

    JInternalFrameTestFatt2 parent;
    gestioneFatture.logic.documenti.Documento doc;

    public timerRefreshFattura(JInternalFrameTestFatt2 parent, gestioneFatture.logic.documenti.Documento doc) {
        this.parent = parent;
        this.doc = doc;
    }

    public void run() {
        parent.ricalcolaTotali();
    }
}

class DataModelFoglio extends javax.swing.table.DefaultTableModel {

    JInternalFrameTestFatt2 form;
    int currentRow = -1;

    public DataModelFoglio(int rowCount, int columnCount, JInternalFrameTestFatt2 form) {
        super(rowCount, columnCount);
        this.form = form;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if (column == 8) {
            return false;
        }
        return super.isCellEditable(row, column);
    }

    public void setValueAt(Object obj, int row, int col) {
        super.setValueAt(obj, row, col);

        if (form.loadingFoglio) {
            return;
        }

        String sql;
        String sqlc;
        String sqlv;
        currentRow = row;

//        SwingUtils.showFlashMessage2(String.valueOf(getValueAt(row, 3)) + "|" + String.valueOf(getValueAt(row, 4)) + "|" + String.valueOf(getValueAt(row, 5)) + "|" + String.valueOf(getValueAt(row, 6)), 5);
        if ((getValueAt(row, 9) == null || String.valueOf(getValueAt(row, 9)).equals("0")) && (getValueAt(row, 1) != null || getValueAt(row, 2) != null) && form.loadingFoglio == false) {
            if (getValueAt(row, 4) != null && !getValueAt(row, 4).toString().equals("")) {
                setValueAt("20", row, 9);
            }
        }
        if ((getValueAt(row, 3) == null || String.valueOf(getValueAt(row, 3)).equals("0")) && (getValueAt(row, 1) != null || getValueAt(row, 2) != null) && form.loadingFoglio == false) {
            if (!Db.nz(main.fileIni.getValue("varie", "umpred"), "").equals("")) {
                setValueAt(main.fileIni.getValue("varie", "umpred"), currentRow, 3);
            }
        }

        //per codice articolo vado a riprendere i dati
        if ((col == 1) && form.loadingFoglio == false) {

            String codice = "";
            String desc = "";
            codice = String.valueOf(Db.nz(getValueAt(currentRow, 1), ""));
            desc = String.valueOf(Db.nz(getValueAt(currentRow, 2), ""));

            if (codice.trim().length() > 0) {
                recuperaDatiArticolo(String.valueOf(obj));
            }

            if (codice.trim().length() > 0 || desc.trim().length() > 0) {
//                if (Db.nz(getValueAt(row, 9), "").equals("")) {
//                    setValueAt("20", currentRow, 9);
//                }
            }
        }

        //ricalcolo importo riga
        if (col != 8) {

            String temp = "";
            double importo = 0;
            double sconto1 = 0;
            double sconto2 = 0;
            double quantita = 0;

            try {
                temp = Db.nz(getValueAt(row, 6), "");
                temp = temp.replace('.', ',');
                sconto1 = it.tnx.Util.getDouble(temp);
            } catch (java.lang.NumberFormatException err1) {
            }

            try {
                temp = Db.nz(getValueAt(row, 7), "");
                temp = temp.replace('.', ',');
                sconto2 = it.tnx.Util.getDouble(temp);
            } catch (java.lang.NumberFormatException err2) {
            }

            try {
                temp = Db.nz(getValueAt(row, 4), "");
                temp = temp.replace('.', ',');
                quantita = it.tnx.Util.getDouble(temp);
            } catch (java.lang.NumberFormatException err3) {
            }

            try {
                temp = Db.nz(getValueAt(row, 5), "");
                temp = temp.replace('.', ',');
                importo = it.tnx.Util.getDouble(temp);
//                System.out.println("importo:" + importo);
            } catch (java.lang.NumberFormatException err4) {
            }

            importo = importo - (importo / 100 * sconto1);
            importo = importo - (importo / 100 * sconto2);
            importo = importo * quantita;

            if (importo != 0) {
                setValueAt(it.tnx.Util.format2Decimali(importo), row, 8);
            }
        }

        //salvo modifica
        if (col >= 1 && col <= 9 && form.loadingFoglio == false) {

            String codice = "";
            String desc = "";
            codice = String.valueOf(Db.nz(getValueAt(currentRow, 1), ""));
            desc = String.valueOf(Db.nz(getValueAt(currentRow, 2), ""));

            if (codice.trim().length() > 0 || desc.trim().length() > 0) {
                sql = "delete from righ_fatt where ";
//                sql += "serie = " + Db.pc(form.getSerie(), Types.VARCHAR);
//                sql += " and numero = " + form.getNumero();
//                sql += " and anno = " + form.getAnno();
                sql += " id_padre = " + form.id;
                sql += " and riga = " + Db.pc(getValueAt(currentRow, 0), Types.INTEGER);
                Db.executeSql(sql);

                sql = "insert into righ_fatt (";
                sqlc = "serie";
                sqlv = Db.pc(form.getSerie(), Types.VARCHAR);
                sqlc += ", numero";
                sqlv += ", " + form.getNumero();
                sqlc += ", id_padre";
                sqlv += ", " + Db.pc(form.id, Types.INTEGER);
                sqlc += ", anno";
                sqlv += ", " + form.getAnno();
                sqlc += ", riga";
                sqlv += ", " + Db.pc(getValueAt(currentRow, 0), Types.INTEGER);
                sqlc += ", codice_articolo";
                sqlv += ", " + Db.pc(getValueAt(currentRow, 1), Types.VARCHAR);
                sqlc += ", descrizione";
                sqlv += ", " + Db.pc(getValueAt(currentRow, 2), Types.VARCHAR);
                sqlc += ", um";
                sqlv += ", " + Db.pc(getValueAt(currentRow, 3), Types.VARCHAR);
                sqlc += ", quantita";
                sqlv += ", " + getDouble(Db.nz(getValueAt(currentRow, 4), "").replace('.', ','));
                sqlc += ", prezzo";
                sqlv += ", " + getDouble(Db.nz(getValueAt(currentRow, 5), "").replace('.', ','));
                sqlc += ", iva";
                if (getValueAt(currentRow, 9) == null || getValueAt(currentRow, 9).toString().equals("")) {
                    sqlv += ", ''";
                } else {
                    sqlv += ", " + Db.pc(Db.nz(getValueAt(currentRow, 9), "0"), Types.INTEGER);
                }
                sqlc += ", sconto1";
                sqlv += ", " + getDouble(Db.nz(getValueAt(currentRow, 6), "").replace('.', ','));
                sqlc += ", sconto2";
                sqlv += ", " + getDouble(Db.nz(getValueAt(currentRow, 7), "").replace('.', ','));
                sqlc += ", stato";
                sqlv += ", 'P'";
                sql = sql + sqlc + ") values (" + sqlv + ")";
                System.out.println("sql update values: " + sql);
                Db.executeSql(sql);
            } else {
                System.out.println("elimino riga");

                if (form.getNumero().length() > 0 && form.getAnno().length() > 0 && getValueAt(currentRow, 0).toString().length() > 0) {
                    System.out.println("elimino riga 2");
                    sql = "delete from righ_fatt where ";
//                    sql += "serie = " + Db.pc(form.getSerie(), Types.VARCHAR);
//                    sql += " and numero = " + Db.pc(form.getNumero(), Types.INTEGER);
//                    sql += " and anno = " + form.getAnno();
                    sql += " id_padre = " + form.id;
                    sql += " and riga = " + Db.pc(getValueAt(currentRow, 0), Types.INTEGER);
                    Db.executeSql(sql);
                }
            }

            form.dbAssociaGrigliaRighe();
        }
    }

    private String getDouble(Object valore) {

        if (valore == null) {

            return "0";
        }
        NumberFormat numFormat = NumberFormat.getInstance();

        try {

            return Db.pc(numFormat.parse(valore.toString()), Types.DOUBLE);
        } catch (Exception err) {

            return "0";
        }
    }

    private void recuperaDatiArticolo(String codArt) {

        String codicelistino = "0";

        if (codArt.length() > 0) {

            ResultSet temp;
            String sql = "select * from articoli";
            sql += " where codice = " + Db.pc(codArt, "VARCHAR");
            temp = Db.openResultSet(sql);

            try {

                if (temp.next() == true) {

                    if (main.getPersonal().equalsIgnoreCase(main.PERSONAL_GIANNI)) {
                        setValueAt(Db.nz(temp.getString("um"), ""), currentRow, 3);
                    } else {

                        boolean eng = false;

                        if (form.texClie.getText().length() > 0) {

                            Cliente cliente = new Cliente(Integer.parseInt(form.texClie.getText()));
                            codicelistino = cliente.getListinoCliente(false);

                            if (cliente.isItalian() == true) {
                                eng = false;
                            } else {
                                eng = true;
                            }
                        }

                        if (eng == true) {
                            setValueAt(Db.nz(temp.getString("um_en"), ""), currentRow, 3);
                        } else {
                            setValueAt(Db.nz(temp.getString("um"), ""), currentRow, 3);
                        }
                    }

                    try {
                        Double iva = Double.parseDouble(temp.getString("iva"));
                        setValueAt(Db.formatNumero(iva), currentRow, 9);
                    } catch (Exception e) {
                        ResultSet ivaStandard = Db.openResultSet("SELECT iva_standard as iva FROM clie_forn WHERE codice = " + Db.pc(form.texClie.getText(), Types.INTEGER));
                        try {
                            if (ivaStandard.next()) {
                                Double iva = Double.parseDouble(ivaStandard.getString("iva"));
                                if (iva != -1) {
                                    setValueAt(Db.formatNumero(iva), currentRow, 9);
                                } else {
                                    setValueAt(Db.formatNumero(20d), currentRow, 9);
                                }
                            } else {
                                setValueAt(Db.formatNumero(20d), currentRow, 9);
                            }
                        } catch (Exception e1) {
                            setValueAt(Db.formatNumero(20d), currentRow, 9);
                        }
                    }

                    setValueAt(Db.nz(temp.getString("descrizione"), ""), currentRow, 2);

                    if (main.getPersonal().equals(main.PERSONAL_GIANNI) || main.getPersonal().equals(main.PERSONAL_CUCINAIN) || main.getPersonal().equals(main.PERSONAL_TLZ)) {

                        //controllo il cliente di che tipo e'
                        if (main.getPersonal().equals(main.PERSONAL_GIANNI)) {
                            setValueAt(Db.formatDecimal5(temp.getDouble("prezzo1")), currentRow, 5);
                        } else {
                            setValueAt(Db.formatDecimal5(temp.getDouble("prezzo2")), currentRow, 5);
                        }

                        try {
                            setValueAt(Db.formatDecimal5(temp.getDouble("prezzo" + codicelistino)), currentRow, 5);
                        } catch (Exception err2) {
                        }
                    } else {
                        sql = "select prezzo from articoli_prezzi";
                        sql += " where articolo = " + Db.pc(codArt, "VARCHAR");
                        sql += " and listino = " + Db.pc(codicelistino, java.sql.Types.VARCHAR);

                        ResultSet prezzi = Db.openResultSet(sql);

                        if (prezzi.next() == true) {
                            setValueAt(Db.formatDecimal5(prezzi.getDouble(1)), currentRow, 5);
                        } else {
                            sql = "select prezzo from articoli_prezzi";
                            sql += " where articolo = " + Db.pc(codArt, "VARCHAR");
                            sql += " and listino = " + Db.pc(main.getListinoBase(), java.sql.Types.VARCHAR);
                            prezzi = Db.openResultSet(sql);

                            if (prezzi.next() == true) {
                                setValueAt(Db.formatDecimal5(prezzi.getDouble(1)), currentRow, 5);
                            }
                        }
                    }
                } else {
                    form.labStatus.setText("Non trovo l'articolo:" + codArt);
                }
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }

    public void removeRow(int param) {

        String sql;
        String sqlc;
        String sqlv;

        //cancello la riga
        if (form.getNumero().length() > 0 && form.getAnno().length() > 0 && getValueAt(param, 0).toString().length() > 0) {
            sql = "delete from righ_fatt where ";
//            sql += "serie = " + Db.pc(form.getSerie(), Types.VARCHAR);
//            sql += " and numero = " + Db.pc(form.getNumero(), Types.INTEGER);
//            sql += " and anno = " + form.getAnno();
            sql += " id_padre = " + form.id;
            sql += " and riga = " + Db.pc(getValueAt(param, 0), Types.INTEGER);

            if (Db.executeSql(sql) == true) {
                System.out.println("row count:" + getRowCount() + " row to del:" + param);
                super.removeRow(param);
            }

            form.dbAssociaGrigliaRighe();
        }
    }
}

class CellEditorFoglio extends javax.swing.DefaultCellEditor {

    javax.swing.JTable table;
    java.awt.Component editComp;

    public CellEditorFoglio(javax.swing.JTextField textField) {
        super(textField);
        textField.setMargin(new Insets(1, 1, 1, 1));
        textField.setBorder(BorderFactory.createEmptyBorder());
    }

    public java.awt.Component getTableCellEditorComponent(javax.swing.JTable jTable, Object obj, boolean param, int param3, int param4) {

        final java.awt.Component edit;
        final java.awt.Component areaEdit;
        table = jTable;
        edit = super.getTableCellEditorComponent(jTable, obj, param, param3, param4);
        editComp = edit;
        edit.addFocusListener(new FocusListener() {

            public void focusGained(java.awt.event.FocusEvent evt) {

                javax.swing.JTextField textEdit = (javax.swing.JTextField) edit;
                textEdit.selectAll();
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
            }
        });
        edit.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseClicked(java.awt.event.MouseEvent evt) {
                editMouseClicked(evt);
            }
        });
        edit.addKeyListener(new java.awt.event.KeyAdapter() {

            public void keyPressed(java.awt.event.KeyEvent evt) {
                editKeyPressed(evt);
            }
        });

        //provare con key listener
        return edit;
    }

    public boolean shouldSelectCell(java.util.EventObject eventObject) {

        return true;
    }

    public boolean isCellEditable(java.util.EventObject eventObject) {

        return true;
    }

    public void editMouseClicked(java.awt.event.MouseEvent evt) {

        if (evt.getClickCount() == 2) {
            showZoom();
        }
    }

    public void editKeyPressed(java.awt.event.KeyEvent evt) {

        if (evt.getKeyCode() == evt.VK_F4) {
            showZoom();
        }
    }

    private void showZoom() {

        Frame[] frames = Menu.getFrames();

        for (int i = 0; i < frames.length; i++) {

            Frame f = (Frame) frames[i];

            if (f.getTitle().equalsIgnoreCase("zoom")) {
                f.setVisible(true);

                break;
            }
        }
    }
}
