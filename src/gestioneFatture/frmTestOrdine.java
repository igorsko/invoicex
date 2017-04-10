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
package gestioneFatture;

import gestioneFatture.chiantiCashmere.animali.*;
import gestioneFatture.logic.clienti.Cliente;

import gestioneFatture.logic.documenti.*;
import it.tnx.commons.AutoCompletionEditable;
import it.tnx.commons.CastUtils;
import it.tnx.commons.DateUtils;


import it.tnx.commons.DbUtils;
import it.tnx.commons.FormatUtils;
import it.tnx.commons.FxUtils;
import it.tnx.commons.MicroBench;
import it.tnx.commons.SwingUtils;
import it.tnx.commons.cu;
import it.tnx.commons.table.EditorUtils;
import it.tnx.commons.table.RendererUtils;
import it.tnx.gui.DateDocument;
import it.tnx.gui.JTableSs;
import it.tnx.gui.StyledComboBoxUI;
import it.tnx.invoicex.InvoicexUtil;
import it.tnx.invoicex.MyAbstractListIntelliHints;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


import java.beans.PropertyVetoException;
import java.io.File;
import java.sql.*;

import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.JTextComponent;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import tnxbeans.tnxComboField;
import tnxbeans.tnxDbGrid;
import tnxbeans.tnxDbPanel;
import tnxbeans.tnxTextField;



public class frmTestOrdine
        extends javax.swing.JInternalFrame
        implements InterfaceAnimale, GenericFrmTest {

    public dbOrdine ordine = new dbOrdine();
    private Documento doc = new Documento();
    public frmElenOrdini from;
    private Db db = Db.INSTANCE;
    int pWidth;
    int pHeight;
    public String dbStato = "L";
    public static String DB_INSERIMENTO = "I";
    public static String DB_MODIFICA = "M";
    public static String DB_LETTURA = "L";
    private String sql = "";
    private double totaleIniziale;
    private String pagamentoIniziale;
    //private int tempTipoFatt = 0;
    //per controllare le provvigioni
    private double provvigioniIniziale;
    private int codiceAgenteIniziale;
    private String pagamentoInizialeGiorno;
    private double totaleDaPagareIniziale;
    java.util.Timer tim;
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
    
    javax.swing.JInternalFrame zoomA;
    javax.swing.JInternalFrame zoomB;
    FoglioSelectionListener foglioSelList;
    public boolean acquisto = false;
    public String suff = "";
    private String ccliente = "cliente";
    private boolean loading = true;
    public String tipoSNJ = "";
    public boolean loadingFoglio = false;
    private DataModelFoglioA foglioDataA;
    private DataModelFoglioB foglioDataB;
    private String sqlGrigliaA;
    private String sqlGrigliaB;
    public Integer id = null;
    private boolean block_aggiornareProvvigioni;
    
    MyAbstractListIntelliHints al_clifor = null;
    AtomicReference<ClienteHint> clifor_selezionato_ref = new AtomicReference(null);

    /** Creates new form frmElenPrev */
    public frmTestOrdine(String dbStato, String dbSerie, int dbNumero, String prevStato, int dbAnno, int tipoOrdine) {
        this(dbStato, dbSerie, dbNumero, prevStato, dbAnno, tipoOrdine, -1);
    }

    public frmTestOrdine(String dbStato, String dbSerie, int dbNumero, String prevStato, int dbAnno, int tipoOrdine, int dbIdOrdine) {
        this(dbStato, dbSerie, dbNumero, prevStato, dbAnno, tipoOrdine, dbIdOrdine, false);
    }

    public frmTestOrdine(String dbStato, String dbSerie, int dbNumero, String prevStato, int dbAnno, int tipoOrdine, int dbIdOrdine, boolean acquisto) {
        this(dbStato, dbSerie, dbNumero, prevStato, dbAnno, tipoOrdine, dbIdOrdine, acquisto, "");
    }

    public frmTestOrdine(String dbStato, String dbSerie, int dbNumero, String prevStato, int dbAnno, int tipoOrdine, int dbIdOrdine, boolean acquisto, String tipoSNJ) {
                
        loading = true;
        this.id = dbIdOrdine;
        this.acquisto = acquisto;
        this.tipoSNJ = tipoSNJ;

//        int permesso = Permesso.PERMESSO_ORDINI_VENDITA;
        if (acquisto) {
            suff = "_acquisto";
            ccliente = "fornitore";
            ordine.acquisto = true;
//            permesso = Permesso.PERMESSO_ORDINI_ACQUISTO;
        } else {
        }
        
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        this.dbStato = dbStato;

        ToolTipManager ttm = ToolTipManager.sharedInstance();
        ttm.setDismissDelay(30000);
        
        //this.tempTipoFatt = tipoOrdine;
        initComponents();
//        if(!main.utente.getPermesso(permesso, Permesso.PERMESSO_TIPO_SCRITTURA)){
//            SwingUtils.showErrorMessage(main.getPadrePanel(), "Non hai i permessi per accedere a questa funzionalità", "Impossibile accedere", true);
//            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//            this.setVisible(false);
//            this.dispose();
//            return;
//        }
        
        if (main.getPersonalContain("consegna_e_scarico")) {
            split.setDividerLocation(330);
        } else {
            split.setDividerLocation(305);
            labModConsegna.setVisible(false);
            labModScarico.setVisible(false);
            comConsegna.setVisible(false);
            comScarico.setVisible(false);
        }
        
        InvoicexUtil.macButtonSmall(butPrezziPrec);
        
        AutoCompletionEditable.enable(comCausaleTrasporto);
        AutoCompletionEditable.enable(comAspettoEsterioreBeni);
        AutoCompletionEditable.enable(comVettori);
        AutoCompletionEditable.enable(comMezzoTrasporto);
        AutoCompletionEditable.enable(comPorto);
        
        DateDocument.installDateDocument(consegna_prevista.getEditor());

        prezzi_ivati.setVisible(false);

        comClie.putClientProperty("JComponent.sizeVariant", "small");
        comClieDest.putClientProperty("JComponent.sizeVariant", "small");
        comAgente.putClientProperty("JComponent.sizeVariant", "small");
        comPaga.putClientProperty("JComponent.sizeVariant", "small");
        comStatoOrdine.putClientProperty("JComponent.sizeVariant", "small");
        stato_evasione.putClientProperty("JComponent.sizeVariant", "small");

        comCausaleTrasporto.putClientProperty("JComponent.sizeVariant", "mini");
        comAspettoEsterioreBeni.putClientProperty("JComponent.sizeVariant", "mini");
        comVettori.putClientProperty("JComponent.sizeVariant", "mini");
        comMezzoTrasporto.putClientProperty("JComponent.sizeVariant", "mini");
        comMezzoTrasporto.putClientProperty("JComponent.sizeVariant", "mini");
        comPorto.putClientProperty("JComponent.sizeVariant", "mini");
        comForni.putClientProperty("JComponent.sizeVariant", "mini");
        comPaese.putClientProperty("JComponent.sizeVariant", "mini");
        
        try {
            InvoicexEvent event = new InvoicexEvent(this);
            event.type = InvoicexEvent.TYPE_FRMTESTORDI_CONSTR_POST_INIT_COMPS;
            main.events.fireInvoicexEvent(event);
        } catch (Exception err) {
            err.printStackTrace();
        }

        if (StringUtils.isEmpty(tipoSNJ) || acquisto) {
            this.panTab.remove(this.panFoglioRigheSNJA);
            this.panTab.remove(this.panFoglioRigheSNJB);
            dati.remove(this.textTipoSnj);
        } else {
            butNuovArti.setVisible(false);
            this.popGrig.remove(0);
            if (tipoSNJ.equals("A")) {
                this.panTab.remove(this.panFoglioRigheSNJB);
                foglioDataA = new DataModelFoglioA(1000, 11, this);
                foglioTipoA.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
                foglioTipoA.setModel(foglioDataA);

                javax.swing.table.TableColumnModel columns = foglioTipoA.getColumnModel();
                javax.swing.table.TableColumn col = columns.getColumn(0);
                col.setHeaderValue("riga");
                col.setPreferredWidth(0);
                col.setMinWidth(0);
                col.setMaxWidth(0);
                col.setWidth(0);

                col = columns.getColumn(1);
                col.setHeaderValue("Art.");
                col.setPreferredWidth(80);

                col = columns.getColumn(2);
                col.setHeaderValue("Descrizione");
                col.setPreferredWidth(150);

                col = columns.getColumn(3);
                col.setHeaderValue("qta");
                col.setPreferredWidth(40);                
                JTextField textfieldeditor = new JTextField();              
                EditorUtils.NumberEditor editor1 = new EditorUtils.NumberEditor(textfieldeditor);
                editor1.returnNull = true;
                col.setCellEditor(editor1);
                col.setCellRenderer(new RendererUtils.NumberRenderer(0,5));

                col = columns.getColumn(4);
                col.setHeaderValue("um");
                col.setPreferredWidth(40);
                tnxbeans.tnxComboField comboUm = new tnxbeans.tnxComboField();
                comboUm.dbAddElement("", "");
                comboUm.dbAddElement("pz", "pz");
                comboUm.dbAddElement("gg", "gg");
                comboUm.dbAddElement("mesi", "mesi");
                col.setCellEditor(new CellEditorFoglioOrdine(comboUm));
                col.setCellRenderer(new ComboBoxRenderer2());

                col = columns.getColumn(5);
                col.setHeaderValue("Importo Unitario");
                col.setPreferredWidth(50);
                
                EditorUtils.CurrencyEditor editor2 = new EditorUtils.CurrencyEditor(textfieldeditor);
                editor2.returnNull = true;
                col.setCellEditor(editor2);
                col.setCellRenderer(new RendererUtils.CurrencyRenderer(2,5));

                col = columns.getColumn(6);
                col.setHeaderValue("%");
                col.setPreferredWidth(20);
                tnxbeans.tnxComboField comboPercentuale = new tnxbeans.tnxComboField();
                comboPercentuale.dbAddElement("0", 0);
                comboPercentuale.dbAddElement("10", 10);
                comboPercentuale.dbAddElement("20", 20);
                comboPercentuale.dbAddElement("30", 30);
                comboPercentuale.dbAddElement("40", 40);
                comboPercentuale.dbAddElement("50", 50);
                comboPercentuale.dbAddElement("60", 60);
                comboPercentuale.dbAddElement("70", 70);
                comboPercentuale.dbAddElement("80", 80);
                comboPercentuale.dbAddElement("90", 90);
                comboPercentuale.dbAddElement("100", 100);
                col.setCellEditor(new CellEditorFoglioOrdine(comboPercentuale));
                col.setCellRenderer(new ComboBoxRenderer2());

                col = columns.getColumn(7);
                col.setHeaderValue("Emissione Fattura");
                col.setPreferredWidth(80);
                tnxbeans.tnxComboField comboEmissioneFattura = new tnxbeans.tnxComboField() {
                    {
                        setUI(new StyledComboBoxUI());
                    }
                };
                comboEmissioneFattura.dbOpenList(Db.getConn(), "SELECT descrizione, id FROM tipi_emissione_fattura");
                comboEmissioneFattura.dbAddElement("", "", 0);
                col.setCellEditor(new CellEditorFoglioOrdine(comboEmissioneFattura));
                col.setCellRenderer(new ComboBoxRenderer2());

                col = columns.getColumn(8);
                col.setHeaderValue("Termini di Pagamento");
                col.setPreferredWidth(80);
                tnxbeans.tnxComboField comboPagamenti = new tnxbeans.tnxComboField() {
                    {
                        setUI(new StyledComboBoxUI());
                    }
                };
                comboPagamenti.dbOpenList(Db.getConn(), "SELECT codice, codice FROM pagamenti");
                comboPagamenti.dbAddElement("", "", 0);


                col.setCellEditor(new CellEditorFoglioOrdine(comboPagamenti));
                col.setCellRenderer(new ComboBoxRenderer2());

                col = columns.getColumn(9);
                col.setHeaderValue("Imponibile");
                col.setCellRenderer(new RendererUtils.CurrencyRenderer());

                JTextField textEdit = new javax.swing.JTextField() {
                };

                CellEditorFoglioOrdine edit = new CellEditorFoglioOrdine(textEdit);

                //it.tnx.gui.KeyableCellEditor edit = new it.tnx.gui.KeyableCellEditor();
                //edit.setClickCountToStart(0);
                edit.setClickCountToStart(2);
                foglioTipoA.setDefaultEditor(Object.class, edit);

                for (int i = 0; i < foglioDataA.getRowCount(); i++) {
                    foglioDataA.setValueAt(new Integer((i + 1) * 10), i, 0);
                }

                FoglioSelectionListener foglioSelList = new FoglioSelectionListener(foglioTipoA);
                foglioTipoA.getSelectionModel().addListSelectionListener(foglioSelList);

                foglioTipoA.setColumnModel(columns);
                
                //rimuovo colonna id riga
                columns.removeColumn(columns.getColumn(10));

                //--- fine foglio ---------
            } else {
                this.panTab.remove(this.panFoglioRigheSNJA);
                foglioDataB = new DataModelFoglioB(1000, 11, this);
                foglioTipoB.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
                foglioTipoB.setModel(foglioDataB);

                javax.swing.table.TableColumnModel columns = foglioTipoB.getColumnModel();
                javax.swing.table.TableColumn col = columns.getColumn(0);
                
                JTextField textfieldeditor = new JTextField();              
                EditorUtils.NumberEditor editorNum = new EditorUtils.NumberEditor(textfieldeditor);
                editorNum.returnNull = true;
                EditorUtils.CurrencyEditor editorCur = new EditorUtils.CurrencyEditor();
                editorCur.returnNull = true;
                RendererUtils.CurrencyRenderer rendCur25 = new RendererUtils.CurrencyRenderer(2, 5);
                RendererUtils.CurrencyRenderer rendCur = new RendererUtils.CurrencyRenderer();
                RendererUtils.NumberRenderer rendNum05 = new RendererUtils.NumberRenderer(0, 5);
                
                col.setHeaderValue("riga");
                col.setPreferredWidth(0);
                col.setMinWidth(0);
                col.setMaxWidth(0);
                col.setWidth(0);

                col = columns.getColumn(1);
                col.setHeaderValue("Art.");
                col.setPreferredWidth(80);

                col = columns.getColumn(2);
                col.setHeaderValue("Descrizione");
                col.setPreferredWidth(150);

                col = columns.getColumn(3);
                col.setHeaderValue("Costo Giornaliero");
                col.setPreferredWidth(50);
                col.setCellRenderer(rendCur25);
                col.setCellEditor(editorCur);
                
                col = columns.getColumn(4);
                col.setHeaderValue("Costo Mensile");
                col.setPreferredWidth(50);
                col.setCellRenderer(rendCur25);
                col.setCellEditor(editorCur);

                col = columns.getColumn(5);
                col.setHeaderValue("Durata Consulenza");
                col.setPreferredWidth(80);
                tnxbeans.tnxComboField comboDurataConsulenza = new tnxbeans.tnxComboField();
                comboDurataConsulenza.dbOpenList(Db.getConn(), "SELECT descrizione, id FROM tipi_durata_consulenza");
                col.setCellEditor(new CellEditorFoglioOrdine(comboDurataConsulenza));
                col.setCellRenderer(new ComboBoxRenderer2());

                col = columns.getColumn(6);
                col.setHeaderValue("Durata Contratto");
                col.setPreferredWidth(80);
                tnxbeans.tnxComboField comboDurataContratto = new tnxbeans.tnxComboField();
                comboDurataContratto.dbOpenList(Db.getConn(), "SELECT descrizione, id FROM tipi_durata_contratto");
                col.setCellEditor(new CellEditorFoglioOrdine(comboDurataContratto));
                col.setCellRenderer(new ComboBoxRenderer2());

                col = columns.getColumn(7);
                col.setHeaderValue("Emissione Fattura");
                col.setPreferredWidth(80);
                tnxbeans.tnxComboField comboEmissioneFattura = new tnxbeans.tnxComboField();
                comboEmissioneFattura.dbOpenList(Db.getConn(), "SELECT descrizione, id FROM tipi_emissione_fattura");
                col.setCellEditor(new CellEditorFoglioOrdine(comboEmissioneFattura));
                col.setCellRenderer(new ComboBoxRenderer2());

                col = columns.getColumn(8);
                col.setHeaderValue("Termini di Pagamento");
                col.setPreferredWidth(80);
                tnxbeans.tnxComboField comboPagamenti = new tnxbeans.tnxComboField();
                comboPagamenti.dbOpenList(Db.getConn(), "SELECT codice, codice FROM pagamenti");
                col.setCellEditor(new CellEditorFoglioOrdine(comboPagamenti));
                col.setCellRenderer(new ComboBoxRenderer2());

                col = columns.getColumn(9);
                col.setHeaderValue("Imponibile");
                col.setPreferredWidth(50);
                col.setCellRenderer(rendCur);

                //rimuovo colonna id riga
                columns.removeColumn(columns.getColumn(10));
                
                JTextField textEdit = new javax.swing.JTextField() {
                };

                CellEditorFoglioOrdine edit = new CellEditorFoglioOrdine(textEdit);

                edit.setClickCountToStart(2);
                foglioTipoB.setDefaultEditor(Object.class, edit);

                for (int i = 0; i < foglioDataB.getRowCount(); i++) {
                    foglioDataB.setValueAt(new Integer((i + 1) * 10), i, 0);
                }

                FoglioSelectionListener foglioSelList = new FoglioSelectionListener(foglioTipoB);
                foglioTipoB.getSelectionModel().addListSelectionListener(foglioSelList);

                foglioTipoB.setColumnModel(columns);
            }
        }

        if (main.getPersonalContain("litri")) {
            butNuovArti1.setText("Inserisci Tot. Litri");
        }
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

        if (main.getPersonalContain("proskin")) {
            butImportRigheProskin.setVisible(true);
        } else {
            butImportRigheProskin.setVisible(false);
        }

        if(main.getPersonalContain("intertelecom") && !this.acquisto){
            this.labRiferimento.setText("Giorni Validità");
        } else {
            this.labRiferimento.setText("Consegna");
        }
        
        griglia.setNoTnxResize(true);
        griglia.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        if (acquisto) {
            stato_evasione.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Ricevuto", "Ricevuto Parziale", "Non Ricevuto"}));
        } else {
            stato_evasione.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Evaso", "Evaso Parziale", "Non Evaso"}));
        }
        stato_evasione.setSelectedIndex(2);

        dati.aggiungiDbPanelCollegato(dati_altri1);
        dati.aggiungiDbPanelCollegato(dati_altri2);

        if (acquisto) {
            setTitle("Preventivo/Ordine di Acquisto");
            jLabel151.setText("fornitore");
            texClie.setDbNomeCampo(ccliente);
            texClieDest.setDbNomeCampo(ccliente + "_destinazione");
            dati_altri2.remove(texForni);
            dati_altri2.remove(comForni);

            dati_altri1.setVisible(false);
            dati_altri2.setVisible(false);

            jLabel17.setText("Rif. Forn.");
            jLabel17.setToolTipText("Inserire il numero dell'ordine assegnato dal fornitore");

            jLabel151.setPreferredSize(new Dimension((int) jLabel151.getPreferredSize().getWidth() + 150, (int) jLabel151.getPreferredSize().getHeight()));

            split.setDividerLocation(170);
        } else {
            setTitle("Preventivo/Ordine di Vendita");
        }

        //imposto campi piccolini
        texDestRagioneSociale.setMargin(new Insets(0, 0, 0, 0));
        texDestIndirizzo.setMargin(new Insets(0, 0, 0, 0));
        texDestCap.setMargin(new Insets(0, 0, 0, 0));
        texDestLocalita.setMargin(new Insets(0, 0, 0, 0));
        texDestProvincia.setMargin(new Insets(0, 0, 0, 0));
        texDestTelefono.setMargin(new Insets(0, 0, 0, 0));
        texDestCellulare.setMargin(new Insets(0, 0, 0, 0));
        texForni.setMargin(new Insets(0, 0, 0, 0));
        texNumeroColli.setMargin(new Insets(0, 0, 0, 0));
        ((JTextComponent) comPaese.getEditor().getEditorComponent()).setMargin(new Insets(0, 0, 0, 0));
        ((JTextComponent) comCausaleTrasporto.getEditor().getEditorComponent()).setMargin(new Insets(0, 0, 0, 0));
        ((JTextComponent) comAspettoEsterioreBeni.getEditor().getEditorComponent()).setMargin(new Insets(0, 0, 0, 0));
        ((JTextComponent) comMezzoTrasporto.getEditor().getEditorComponent()).setMargin(new Insets(0, 0, 0, 0));
        ((JTextComponent) comPorto.getEditor().getEditorComponent()).setMargin(new Insets(0, 0, 0, 0));
        ((JTextComponent) comForni.getEditor().getEditorComponent()).setMargin(new Insets(0, 0, 0, 0));
        ((JTextComponent) comVettori.getEditor().getEditorComponent()).setMargin(new Insets(0, 0, 0, 0));

        //init campi particolari
        this.texData.setDbDefault(texData.DEFAULT_CURRENT);

        //oggetto preventivo
        this.ordine.dbStato = dbStato;
        this.ordine.serie = dbSerie;
        this.ordine.numero = dbNumero;
        this.ordine.stato = prevStato;
        this.ordine.anno = dbAnno;

        //105
        this.ordine.tipoOrdine = tipoOrdine;
        this.ordine.texTota = this.texTota;
        this.ordine.texTotaImpo = this.texTotaImpo;
        this.ordine.texTotaIva = this.texTotaIva;
//        this.setClosable(false);


        comPorto.dbAddElement("", "");
        comPorto.dbOpenList(Db.getConn(), "select porto, id from tipi_porto group by porto");
        comMezzoTrasporto.dbAddElement("");
        comMezzoTrasporto.dbAddElement("DESTINATARIO");
        comMezzoTrasporto.dbAddElement("MITTENTE");
        comMezzoTrasporto.dbAddElement("VETTORE");
        comCausaleTrasporto.dbAddElement("");
        comCausaleTrasporto.dbOpenList(Db.getConn(), "select nome, id from tipi_causali_trasporto group by nome");
        
//        comAspettoEsterioreBeni.dbAddElement("");
//        comAspettoEsterioreBeni.dbAddElement("SCATOLA");
//        comAspettoEsterioreBeni.dbAddElement("A VISTA");
//        comAspettoEsterioreBeni.dbAddElement("SCATOLA IN PANCALE");
        comAspettoEsterioreBeni.dbAddElement("");
        comAspettoEsterioreBeni.dbOpenList(Db.getConn(), "select nome, id from tipi_aspetto_esteriore_beni group by nome", null, false);
        
        comVettori.dbAddElement("");
        comVettori.dbOpenList(Db.getConn(), "select nome,nome from vettori order by nome", null, false);

        //faccio copia in caso di annulla deve rimettere le righe giuste
        if (dbStato == this.DB_MODIFICA) {
            //controllo tabella temp
            String sql = "check table righ_ordi" + suff + "_temp";
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
            sql = "delete from righ_ordi" + suff + "_temp";
            sql += " where serie " + Db.pcW(this.ordine.serie, "VARCHAR");
            sql += " and numero " + Db.pcW(String.valueOf(this.ordine.numero), "NUMBER");
            sql += " and anno " + Db.pcW(String.valueOf(this.ordine.anno), "VARCHAR");
            sql += " and username = '" + main.login + "'";
            Db.executeSqlDialogExc(sql, true);

            sql = "delete te.* from righ_ordi" + suff + "_temp te join righ_ordi" + suff + " ri on te.id = ri.id";
            sql += " and ri.serie " + Db.pcW(this.ordine.serie, "VARCHAR");
            sql += " and ri.numero " + Db.pcW(String.valueOf(this.ordine.numero), "NUMBER");
            sql += " and ri.anno " + Db.pcW(String.valueOf(this.ordine.anno), "VARCHAR");
            sql += " and te.username = '" + main.login + "'";
            Db.executeSqlDialogExc(sql, true);

            //e inserisco
            sql = "insert into righ_ordi" + suff + "_temp";
            sql += " select *, '" + main.login + "' as username";
            sql += " from righ_ordi" + suff;
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
        this.dati.dbNomeTabella = "test_ordi" + suff;
        
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
        

        //this.dati.butSave = this.butSave;
        //this.dati.butUndo = this.butUndo;
        //controllo se inserimento o modifica
        if (dbStato.equalsIgnoreCase(this.DB_INSERIMENTO)) {
            this.dati.dbOpen(db.getConn(), "select * from test_ordi" + suff + " limit 0");
        } else {
            sql = "select * from test_ordi" + suff;
//            sql += " where serie = " + db.pc(dbSerie, "VARCHAR");
//            sql += " and numero = " + dbNumero;
//            //sql += " and stato = " + db.pc(prevStato, "VARCHAR");
//            sql += " and anno = " + dbAnno;
            sql += " where id = " + id;
            this.dati.dbOpen(db.getConn(), sql);
            
            consegna_prevista.setDate(cu.toDate(dati.dbGetField("data_consegna_prevista")));
        }

        //apro la combo pagamenti
        this.comPaga.dbOpenList(db.getConn(), "select codice, codice from pagamenti order by codice", null, false);
        
        comConsegna.dbOpenList(db.getConn(), "select nome, id from tipi_consegna", null, false);
        comScarico.dbOpenList(db.getConn(), "select nome, id from tipi_scarico", null, false);

        comPaese.dbAddElement("", "");
        comPaese.dbOpenList(Db.getConn(), "select nome, codice1 from stati", null, false);

        //apro combo agenti
        this.comAgente.dbOpenList(Db.getConn(), "select nome, id from agenti where id != 0 and IFNULL(nome,'') != '' order by nome", null, false);
        this.dati.dbRefresh();
        this.ordine.dbRefresh();

        //apro la combo clienti
        this.comClie.setDbTextAbbinato(this.texClie);
        this.texClie.setDbComboAbbinata(this.comClie);
        this.comClie.dbOpenList(db.getConn(), "select ragione_sociale,codice from clie_forn where ragione_sociale != '' order by ragione_sociale", this.texClie.getText());

        //apro combo destinazione cliente
        comClieDest.dbTrovaMentreScrive = false;
        sql = "select ragione_sociale,codice from clie_forn_dest";
        sql += " where codice_cliente = " + Db.pc(this.texClie.getText(), "NUMERIC");
        sql += " order by ragione_sociale";
        riempiDestDiversa(sql);

        comForni.dbOpenList(Db.getConn(), "select ragione_sociale,codice from clie_forn order by ragione_sociale", this.texForni.getText());
        comForni.setSelectedIndex(-1);

        //righe
        //apro la griglia
        this.griglia.dbNomeTabella = "righ_ordi" + suff;

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
        if (dbStato.equalsIgnoreCase(this.DB_INSERIMENTO)) {
            inserimento();
            texData.setEditable(true);
        } else {
            comForni.dbTrovaKey(this.texForni.getText());

            //disabilito la data perch??? non tornerebbe pi??? la chiave per numero e anno
            this.texData.setEditable(false);
            this.ordine.sconto1 = Db.getDouble(this.texScon1.getText());
            this.ordine.sconto2 = Db.getDouble(this.texScon2.getText());
            this.ordine.sconto3 = Db.getDouble(this.texScon3.getText());

            //this.ordine.speseVarie = Db.getDouble(this.texSpesVari.getText());
            this.ordine.speseTrasportoIva = Db.getDouble(this.texSpeseTrasporto.getText());
            this.ordine.speseIncassoIva = Db.getDouble(this.texSpeseIncasso.getText());
            dopoInserimento();
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    comClie.setLocked(false);
                    comClie.setEditable(true);
                    comClie.setEnabled(true);
                    comClie.grabFocus();
                }
            });
        }

        this.textTipoSnj.setText(tipoSNJ);
        this.textTipoSnj.setVisible(false);
//        java.util.prefs.Preferences preferences = java.util.prefs.Preferences.userNodeForPackage(main.class);
//        boolean azioniPericolose = preferences.getBoolean("azioniPericolose", false);
        boolean azioniPericolose = main.fileIni.getValueBoolean("pref", "azioniPericolose", true);

        if (azioniPericolose) {
            texNumeOrdine.setEditable(true);
            texData.setEditable(true);
        }

        zoomA = new frmZoomDesc();
        frmZoomDesc frmZoomA = (frmZoomDesc) zoomA;
        frmZoomA.selectList = frmTestOrdine.this.foglioSelList;
        frmZoomA.setGriglia(foglioTipoA);
        zoomA.putClientProperty("JInternalFrame.isPalette", Boolean.TRUE);
        zoomA.setResizable(true);
        zoomA.setIconifiable(true);
        zoomA.setClosable(true);
        zoomA.setBounds((int) frmTestOrdine.this.getLocation().getX() + 430, (int) frmTestOrdine.this.getLocation().getY() + 350, 300, 150);

        zoomB = new frmZoomDesc();
        frmZoomDesc frmZoomB = (frmZoomDesc) zoomB;
        frmZoomB.selectList = frmTestOrdine.this.foglioSelList;
        frmZoomB.setGriglia(foglioTipoB);
        zoomB.putClientProperty("JInternalFrame.isPalette", Boolean.TRUE);
        zoomB.setResizable(true);
        zoomB.setIconifiable(true);
        zoomB.setClosable(true);
        zoomB.setBounds((int) frmTestOrdine.this.getLocation().getX() + 430, (int) frmTestOrdine.this.getLocation().getY() + 350, 300, 150);

        comStatoOrdine.dbOpenList(Db.getConn(), "SELECT descrizione, id FROM stati_preventivo_ordine");
        System.out.println("!!! texStatoOrdine.getText(): " + texStatoOrdine.getText());
        comStatoOrdine.setSelectedItem(texStatoOrdine.getText());
        if (main.getPersonalContain("canicom")) {
            comStatoOrdine.setSelectedItem("Ordine");
        }

        if (!dbStato.equalsIgnoreCase(DB_INSERIMENTO)) {
            System.out.println("!!! evasione: " + dati.dbGetField("evaso"));
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
        
        prezzi_ivati_virtual.setSelected(prezzi_ivati.isSelected());

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
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    comClie.grabFocus();
                }
            });
        } else {

            stato_evasione.setSelectedIndex(2);

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
            cs = dati_altri1.getComponents();
            for (int i = 0; i < cs.length; i++) {
                cs[i].setEnabled(false);
                if (cs[i] instanceof tnxbeans.tnxComboField) {
                    tnxbeans.tnxComboField combo = (tnxbeans.tnxComboField) cs[i];
                    combo.setEditable(false);
                    combo.setLocked(true);
                }
            }
            cs = dati_altri2.getComponents();
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
        cs = this.dati_altri1.getComponents();
        for (int i = 0; i < cs.length; i++) {
            cs[i].setEnabled(true);
            if (cs[i] instanceof tnxbeans.tnxComboField) {
                tnxbeans.tnxComboField combo = (tnxbeans.tnxComboField) cs[i];
                combo.setEditable(true);
                combo.setLocked(false);
            }
        }
        cs = this.dati_altri2.getComponents();
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

            String sql = "select numero from test_ordi" + suff;
            sql += " where anno = " + java.util.Calendar.getInstance().get(Calendar.YEAR);
            sql += " and serie = " + Db.pc(texSeri.getText(), Types.VARCHAR);
            sql += " order by numero desc limit 1";
            resu = stat.executeQuery(sql);

            if (resu.next() == true) {
                this.texNumeOrdine.setText(String.valueOf(resu.getInt(1) + 1));
            } else {
                this.texNumeOrdine.setText("1");
            }

            //inserisco data consegna standard
            //this.texDataCons.setText("");
            //inserisco spese incasso standard
            if (main.getPersonal().equals(main.PERSONAL_GIANNI)) {
                this.texSpeseIncasso.setText("1,50");
            }

            System.out.println("!!! texStatoOrdine.setText(String.valueOf(this.ordine.tipoOrdine)): " + this.ordine.tipoOrdine);
            this.texStatoOrdine.setText(String.valueOf(this.ordine.tipoOrdine));

            dati.setCampiAggiuntivi(new Hashtable());
            dati.getCampiAggiuntivi().put("evaso", Db.pc(getStatoEvaso(), Types.VARCHAR));
            dati.getCampiAggiuntivi().put("sconto", Db.pc(doc.getSconto(), Types.DOUBLE));
            dati.getCampiAggiuntivi().put("totale_imponibile_pre_sconto", Db.pc(doc.totaleImponibilePreSconto, Types.DOUBLE));
            dati.getCampiAggiuntivi().put("totale_ivato_pre_sconto", Db.pc(doc.totaleIvatoPreSconto, Types.DOUBLE));

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
                    Integer numero = Integer.parseInt(texNumeOrdine.getText());
                    Integer anno = Integer.parseInt(texAnno.getText());

                    String tmpSql = "select * from test_ordi" + suff + " where serie = '" + tmpSerie + "' and anno = " + anno + " and numero = " + numero;
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
                    frmElenOrdini temp = (frmElenOrdini) from;
                    temp.dbRefresh();
                }
            }
//            texClie.setText("");

            this.ordine.serie = this.texSeri.getText();
            this.ordine.stato = "P";
            this.ordine.numero = new Integer(this.texNumeOrdine.getText()).intValue();
            this.ordine.anno = java.util.Calendar.getInstance().get(Calendar.YEAR);

            this.id = (Integer) dati.last_inserted_id;
            System.out.println("*** id new : " + this.id);
            this.ordine.id = id;
            
            this.dati.dbCambiaStato(this.dati.DB_LETTURA);

            //-----------------------------------------------------------------
            //aggiunto da Lorenzo per tlz
            //cambio mettendo opzioni su opzioni
            //if (main.getPersonal().equalsIgnoreCase(main.PERSONAL_TLZ)) {
            //  this.texNote.setText("FATTURA ESENTE IVA.\n OPERAZIONE INTRACOMUNITARIA CON NUMERO DI IDENTIFICAZIONE ATU 54471806");
            //}
            try {

                //java.util.prefs.Preferences preferences = java.util.prefs.Preferences.userNodeForPackage(main.class);
                //this.texNote.setText(preferences.get("noteStandard", ""));
                texNote.setText(main.fileIni.getValue("pref", "noteStandard"));

                //dbRefresh();
            } catch (Exception err) {
                err.printStackTrace();
            }

            this.texSeri.setEditable(false);
            this.texSeri.setBackground(this.texNumeOrdine.getBackground());

            //Fine
        } catch (Exception err) {
            err.printStackTrace();
        }
        dati.dbCheckModificatiReset();
    }

    private void dopoInserimento() {
        dbAssociaGrigliaRighe();
        doc.load(Db.INSTANCE, this.ordine.numero, this.ordine.serie, this.ordine.anno, acquisto ? Db.TIPO_DOCUMENTO_ORDINE_ACQUISTO : Db.TIPO_DOCUMENTO_ORDINE, id);
        
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

        ricalcolaTotali();

        //rinfresco il discorso extra cee
        try {
            if (this.texClie.getText().length() > 0) {
                this.ordine.forceCliente(Long.parseLong(this.texClie.getText()));
            }
        } catch (Exception err) {
            err.printStackTrace();
        }

        //memorizzo totale iniziale, se cambia rigenreo le scadenze
        ordine.dbRefresh();
        this.totaleIniziale = this.ordine.totale;
        this.totaleDaPagareIniziale = this.ordine.totaleDaPagare;
        this.pagamentoIniziale = this.comPaga.getText();
        this.pagamentoInizialeGiorno = this.texGiornoPagamento.getText();
        this.provvigioniIniziale = Db.getDouble(this.texProvvigione.getText());
        this.codiceAgenteIniziale = it.tnx.Util.getInt(this.comAgente.getSelectedKey().toString());

        //debug
        System.out.println("provvigioni iniziale = " + provvigioniIniziale);
        System.out.println("codice agente iniziale = " + codiceAgenteIniziale);

        //nascondo le i dati agente se tlz
        if (main.getPersonalContain(main.PERSONAL_TLZ)) {
            this.labAgente.setVisible(false);
            this.comAgente.setVisible(false);
            this.labProvvigione.setVisible(false);
            this.texProvvigione.setVisible(false);
            this.labPercentoProvvigione.setVisible(false);
        }
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
            doc.setPrezziIvati(prezzi_ivati.isSelected());
            doc.setSconto(Db.getDouble(texSconto.getText()));
            doc.calcolaTotali();
        } catch (Exception err) {
            err.printStackTrace();
        }

        //storico
        Storico.scrivi("Salva Documento", "Documento = " + (acquisto ? Db.TIPO_DOCUMENTO_ORDINE_ACQUISTO : Db.TIPO_DOCUMENTO_ORDINE) + "/" + this.texSeri.getText() + "/" + this.ordine.numero + "/" + this.ordine.anno + ", Pagamento = " + this.comPaga.getText() + ", Importo documento = " + this.texTota1.getText());

        //stato ordine
        texStatoOrdine.setText(ObjectUtils.toString(comStatoOrdine.getSelectedItem()));

        dati.setCampiAggiuntivi(new Hashtable());
        dati.getCampiAggiuntivi().put("evaso", Db.pc(getStatoEvaso(), Types.VARCHAR));
        dati.getCampiAggiuntivi().put("sconto", Db.pc(doc.getSconto(), Types.DOUBLE));
        dati.getCampiAggiuntivi().put("totale_imponibile_pre_sconto", Db.pc(doc.totaleImponibilePreSconto, Types.DOUBLE));
        dati.getCampiAggiuntivi().put("totale_ivato_pre_sconto", Db.pc(doc.totaleIvatoPreSconto, Types.DOUBLE));
        dati.getCampiAggiuntivi().put("data_consegna_prevista", Db.pc(consegna_prevista.getDate(), Types.DATE));

        //salvo altrimenti genera le scadenze sull'importo vuoto
        this.dati.dbSave();

        //genero le scadenze
        if (!acquisto) {
            if (main.fileIni.getValueBoolean("pref", "scadenzeOrdini", false)) {
                Scadenze tempScad = new Scadenze(acquisto ? Db.TIPO_DOCUMENTO_ORDINE_ACQUISTO : Db.TIPO_DOCUMENTO_ORDINE, this.texSeri.getText(), ordine.numero, ordine.anno, this.comPaga.getText());
                boolean scadenzeRigenerate = false;
                if (doc.getTotale() != this.totaleIniziale || !this.pagamentoInizialeGiorno.equals(this.texGiornoPagamento.getText()) || !this.pagamentoIniziale.equals(this.comPaga.getText()) || doc.getTotale_da_pagare() != this.totaleDaPagareIniziale) {
                    tempScad.generaScadenze();
                    scadenzeRigenerate = true;
                    if (!dbStato.equals(this.DB_INSERIMENTO)) {
                        javax.swing.JOptionPane.showMessageDialog(this, "Sono state rigenerate le scadenze perche' il totale od il pagamento e' stato variato", "Attenzione", javax.swing.JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        }

        if (from != null) {
            this.from.dbRefresh();
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


     * initialize the form.


     * WARNING: Do NOT modify this code. The content of this method is


     * always regenerated by the Form Editor.


     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popGrig = new javax.swing.JPopupMenu();
        popGrigModi = new javax.swing.JMenuItem();
        popGrigElim = new javax.swing.JMenuItem();
        popGridAdd = new javax.swing.JMenuItem();
        popGridAddSub = new javax.swing.JMenuItem();
        popDuplicaRighe = new javax.swing.JMenuItem();
        popFoglio = new javax.swing.JPopupMenu();
        popFoglioElimina = new javax.swing.JMenuItem();
        panTab = new javax.swing.JTabbedPane();
        panDati = new javax.swing.JPanel();
        split = new javax.swing.JSplitPane();
        dati = new tnxbeans.tnxDbPanel();
        texNumeOrdine = new tnxbeans.tnxTextField();
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
        jLabel16 = new javax.swing.JLabel();
        labScon1 = new javax.swing.JLabel();
        labScon2 = new javax.swing.JLabel();
        jLabel113 = new javax.swing.JLabel();
        texData = new tnxbeans.tnxTextField();
        jLabel11 = new javax.swing.JLabel();
        texStat = new tnxbeans.tnxTextField();
        texStat.setVisible(false);
        texScon3 = new tnxbeans.tnxTextField();
        labScon21 = new javax.swing.JLabel();
        jLabel151 = new javax.swing.JLabel();
        texSeri = new tnxbeans.tnxTextField();
        texAnno = new tnxbeans.tnxTextField();
        texAnno.setVisible(false);
        texClieDest = new tnxbeans.tnxTextField();
        texClieDest.setVisible(false);
        jLabel17 = new javax.swing.JLabel();
        texPaga2 = new tnxbeans.tnxTextField();
        texSpeseTrasporto = new tnxbeans.tnxTextField();
        jLabel114 = new javax.swing.JLabel();
        butPrezziPrec = new javax.swing.JButton();
        comPaga = new tnxbeans.tnxComboField();
        jLabel19 = new javax.swing.JLabel();
        butAddClie = new javax.swing.JButton();
        labGiornoPagamento = new javax.swing.JLabel();
        texGiornoPagamento = new tnxbeans.tnxTextField();
        texStatoOrdine = new tnxbeans.tnxTextField();
        texStatoOrdine.setVisible(false);
        texTipoOrdine = new tnxbeans.tnxTextField();
        texTipoOrdine.setVisible(false);
        dati_altri2 = new tnxbeans.tnxDbPanel();
        jLabel15 = new javax.swing.JLabel();
        comClieDest = new tnxbeans.tnxComboField();
        labScon10 = new javax.swing.JLabel();
        labScon11 = new javax.swing.JLabel();
        texDestIndirizzo = new tnxbeans.tnxTextField();
        labScon12 = new javax.swing.JLabel();
        texDestCap = new tnxbeans.tnxTextField();
        labScon13 = new javax.swing.JLabel();
        texDestLocalita = new tnxbeans.tnxTextField();
        labScon14 = new javax.swing.JLabel();
        texDestProvincia = new tnxbeans.tnxTextField();
        labScon16 = new javax.swing.JLabel();
        texDestTelefono = new tnxbeans.tnxTextField();
        labScon15 = new javax.swing.JLabel();
        texDestCellulare = new tnxbeans.tnxTextField();
        labScon17 = new javax.swing.JLabel();
        comPaese = new tnxbeans.tnxComboField();
        labFaTitolo = new javax.swing.JLabel();
        labFa1 = new javax.swing.JLabel();
        comCausaleTrasporto = new tnxbeans.tnxComboField();
        labFa2 = new javax.swing.JLabel();
        comAspettoEsterioreBeni = new tnxbeans.tnxComboField();
        labFa3 = new javax.swing.JLabel();
        texNumeroColli = new tnxbeans.tnxTextField();
        labFa4 = new javax.swing.JLabel();
        comVettori = new tnxbeans.tnxComboField();
        labFa5 = new javax.swing.JLabel();
        comMezzoTrasporto = new tnxbeans.tnxComboField();
        labFa6 = new javax.swing.JLabel();
        comPorto = new tnxbeans.tnxComboField();
        jLabel4 = new javax.swing.JLabel();
        texForni = new tnxbeans.tnxTextField();
        comForni = new tnxbeans.tnxComboField();
        texDestRagioneSociale = new tnxbeans.tnxTextField();
        jPanel6 = new javax.swing.JPanel();
        dati_altri1 = new tnxbeans.tnxDbPanel();
        jLabel24 = new javax.swing.JLabel();
        texNotePagamento = new tnxbeans.tnxTextField();
        cheOpzioneRibaDestDiversa = new tnxbeans.tnxCheckBox();
        jLabel18 = new javax.swing.JLabel();
        texBancAbi = new tnxbeans.tnxTextField();
        butCoor = new javax.swing.JButton();
        labBancAbi = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        texBancCab = new tnxbeans.tnxTextField();
        labBancCab = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        texBancIban = new tnxbeans.tnxTextField();
        labAgente = new javax.swing.JLabel();
        comAgente = new tnxbeans.tnxComboField();
        labProvvigione = new javax.swing.JLabel();
        texProvvigione = new tnxbeans.tnxTextField();
        labPercentoProvvigione = new javax.swing.JLabel();
        texSwift = new tnxbeans.tnxTextField();
        jLabel26 = new javax.swing.JLabel();
        labRiferimento = new javax.swing.JLabel();
        texConsegna = new tnxbeans.tnxTextField();
        textTipoSnj = new tnxbeans.tnxTextField();
        texStatoOrdine.setVisible(false);
        prezzi_ivati = new tnxbeans.tnxCheckBox();
        consegna_prevista = new org.jdesktop.swingx.JXDatePicker();
        jLabel115 = new javax.swing.JLabel();
        labModConsegna = new javax.swing.JLabel();
        comConsegna = new tnxbeans.tnxComboField();
        comScarico = new tnxbeans.tnxComboField();
        labModScarico = new javax.swing.JLabel();
        texCliente = new javax.swing.JTextField();
        apriclienti = new BasicArrowButton(BasicArrowButton.SOUTH, UIManager.getColor("ComboBox.buttonBackground"), UIManager.getColor("ComboBox.buttonShadow"), UIManager.getColor("ComboBox.buttonDarkShadow"), UIManager.getColor("ComboBox.buttonHighlight"));
        datiRighe = new tnxbeans.tnxDbPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        griglia = getGrigliaInitComp();
        jPanel1 = new javax.swing.JPanel();
        prezzi_ivati_virtual = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        butNuovArti = new javax.swing.JButton();
        butNuovArti2 = new javax.swing.JButton();
        butNuovArti1 = new javax.swing.JButton();
        butImportRighe = new javax.swing.JButton();
        butImportRigheProskin = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        butPdf = new javax.swing.JButton();
        butStampa = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        butUndo = new javax.swing.JButton();
        butSave = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        comStatoOrdine = new tnxbeans.tnxComboField();
        jPanel9 = new javax.swing.JPanel();
        stato_evasione = new javax.swing.JComboBox();
        jPanel4 = new javax.swing.JPanel();
        texTotaImpo = new tnxbeans.tnxTextField();
        texTotaIva = new tnxbeans.tnxTextField();
        texTota = new tnxbeans.tnxTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        texSconto = new tnxbeans.tnxTextField();
        panFoglioRigheSNJA = new javax.swing.JPanel();
        panGriglia = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        foglioTipoA = new JTableSs(){
            @Override
            public void setValueAt(Object value,int row,int column){
                super.setValueAt(value, row, column);
                ricalcolaTotali();
            }
        };
        panTotale = new javax.swing.JPanel();
        labStatus = new javax.swing.JLabel();
        panFoglioRigheSNJB = new javax.swing.JPanel();
        panGriglia1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        foglioTipoB = new JTableSs(){
            @Override
            public void setValueAt(Object value,int row,int column){
                super.setValueAt(value, row, column);
                ricalcolaTotali();
            }
        };
        panTotale1 = new javax.swing.JPanel();
        labStatus1 = new javax.swing.JLabel();

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
        popGridAdd.setLabel("Aggiungi Riga");
        popGridAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popGridAddActionPerformed(evt);
            }
        });
        popGrig.add(popGridAdd);

        popGridAddSub.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/list-add.png"))); // NOI18N
        popGridAddSub.setLabel("Aggiungi Riga");
        popGridAddSub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popGridAddSubActionPerformed(evt);
            }
        });
        popGrig.add(popGridAddSub);

        popDuplicaRighe.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/edit-copy.png"))); // NOI18N
        popDuplicaRighe.setText("Duplica");
        popDuplicaRighe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popDuplicaRigheActionPerformed(evt);
            }
        });
        popGrig.add(popDuplicaRighe);

        popFoglioElimina.setText("elimina");
        popFoglioElimina.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popFoglioEliminaActionPerformed(evt);
            }
        });
        popFoglio.add(popFoglioElimina);

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Preventivo/Ordine");
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
        addVetoableChangeListener(new java.beans.VetoableChangeListener() {
            public void vetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {
                formVetoableChange(evt);
            }
        });

        panTab.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                panTabStateChanged(evt);
            }
        });

        panDati.setLayout(new java.awt.BorderLayout());

        split.setBorder(null);
        split.setDividerLocation(330);
        split.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        split.setMinimumSize(new java.awt.Dimension(508, 200));

        dati.setMinimumSize(new java.awt.Dimension(0, 50));
        dati.setPreferredSize(new java.awt.Dimension(50, 122));
        dati.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        texNumeOrdine.setEditable(false);
        texNumeOrdine.setText("numero");
        texNumeOrdine.setDbDescCampo("");
        texNumeOrdine.setDbNomeCampo("numero");
        texNumeOrdine.setDbTipoCampo("testo");
        texNumeOrdine.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                texNumeOrdineFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                texNumeOrdineFocusLost(evt);
            }
        });
        dati.add(texNumeOrdine, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 20, 45, 20));

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
        dati.add(texClie, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 220, 45, 20));

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
        dati.add(texSpeseIncasso, new org.netbeans.lib.awtextra.AbsoluteConstraints(225, 55, 75, 20));

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
        dati.add(texScon2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 55, 35, 20));

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
        dati.add(texScon1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 55, 35, 20));

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
        dati.add(comClie, new org.netbeans.lib.awtextra.AbsoluteConstraints(785, 220, 30, 20));

        texTotaImpo1.setBackground(new java.awt.Color(255, 200, 200));
        texTotaImpo1.setText("0");
        texTotaImpo1.setDbDescCampo("");
        texTotaImpo1.setDbNomeCampo("totale_imponibile");
        texTotaImpo1.setDbTipoCampo("valuta");
        dati.add(texTotaImpo1, new org.netbeans.lib.awtextra.AbsoluteConstraints(735, 70, 60, -1));

        texTotaIva1.setBackground(new java.awt.Color(255, 200, 200));
        texTotaIva1.setText("0");
        texTotaIva1.setDbDescCampo("");
        texTotaIva1.setDbNomeCampo("totale_iva");
        texTotaIva1.setDbTipoCampo("valuta");
        dati.add(texTotaIva1, new org.netbeans.lib.awtextra.AbsoluteConstraints(735, 90, 60, -1));

        texTota1.setBackground(new java.awt.Color(255, 200, 200));
        texTota1.setText("0");
        texTota1.setDbDescCampo("");
        texTota1.setDbNomeCampo("totale");
        texTota1.setDbTipoCampo("valuta");
        dati.add(texTota1, new org.netbeans.lib.awtextra.AbsoluteConstraints(735, 110, 60, -1));

        texNote.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        texNote.setDbNomeCampo("note");
        texNote.setFont(texNote.getFont());
        texNote.setText("note");
        dati.add(texNote, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 80, 320, 35));

        jLabel13.setText("numero");
        dati.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 5, 45, 15));

        jLabel14.setText("serie");
        dati.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 5, 35, 15));

        jLabel16.setText("data");
        dati.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(95, 5, 75, 15));

        labScon1.setText("sc. 1");
        labScon1.setToolTipText("primo sconto");
        dati.add(labScon1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 43, 35, 11));

        labScon2.setText("sc. 3");
        labScon2.setToolTipText("sconto3");
        dati.add(labScon2, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 43, 35, 11));

        jLabel113.setText("consegna prevista");
        dati.add(jLabel113, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 40, 100, 15));

        texData.setText("data");
        texData.setDbDescCampo("");
        texData.setDbNomeCampo("data");
        texData.setDbTipoCampo("data");
        texData.setFont(texData.getFont().deriveFont(texData.getFont().getSize()-1f));
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

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Annotazioni");
        dati.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 75, -1));

        texStat.setBackground(new java.awt.Color(255, 200, 200));
        texStat.setText("P");
        texStat.setDbDescCampo("");
        texStat.setDbNomeCampo("stato");
        texStat.setDbRiempire(false);
        texStat.setDbTipoCampo("");
        dati.add(texStat, new org.netbeans.lib.awtextra.AbsoluteConstraints(735, 135, 30, -1));

        texScon3.setText("sconto3");
        texScon3.setToolTipText("terzo sconto");
        texScon3.setDbDescCampo("");
        texScon3.setDbNomeCampo("sconto3");
        texScon3.setDbTipoCampo("numerico");
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
        dati.add(texScon3, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 55, 35, 20));

        labScon21.setText("sc. 2");
        labScon21.setToolTipText("secondo sconto");
        dati.add(labScon21, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 43, 35, 11));

        jLabel151.setText("cliente");
        dati.add(jLabel151, new org.netbeans.lib.awtextra.AbsoluteConstraints(175, 5, 40, 15));

        texSeri.setBackground(new java.awt.Color(204, 204, 204));
        texSeri.setEditable(false);
        texSeri.setText("serie");
        texSeri.setDbDescCampo("");
        texSeri.setDbNomeCampo("serie");
        texSeri.setDbTipoCampo("");
        texSeri.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                texSeriFocusLost(evt);
            }
        });
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
        dati.add(texAnno, new org.netbeans.lib.awtextra.AbsoluteConstraints(735, 45, 50, -1));

        texClieDest.setBackground(new java.awt.Color(255, 200, 200));
        texClieDest.setText("cliente_destinazione");
        texClieDest.setDbComboAbbinata(comClieDest);
        texClieDest.setDbDescCampo("");
        texClieDest.setDbNomeCampo("cliente_destinazione");
        texClieDest.setDbTipoCampo("numerico");
        dati.add(texClieDest, new org.netbeans.lib.awtextra.AbsoluteConstraints(735, 20, 45, -1));

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel17.setText("Riferimento");
        jLabel17.setToolTipText("Riferimento vostro ordine o altro documento");
        dati.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 75, 20));

        texPaga2.setText("riferimento");
        texPaga2.setDbDescCampo("");
        texPaga2.setDbNomeCampo("riferimento");
        texPaga2.setDbTipoCampo("");
        texPaga2.setmaxChars(255);
        dati.add(texPaga2, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 120, 140, -1));

        texSpeseTrasporto.setText("spese_trasporto");
        texSpeseTrasporto.setDbDescCampo("");
        texSpeseTrasporto.setDbNomeCampo("spese_trasporto");
        texSpeseTrasporto.setDbTipoCampo("valuta");
        texSpeseTrasporto.setPreferredSize(new java.awt.Dimension(65, 20));
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
            public void keyReleased(java.awt.event.KeyEvent evt) {
                texSpeseTrasportoKeyReleased(evt);
            }
        });
        dati.add(texSpeseTrasporto, new org.netbeans.lib.awtextra.AbsoluteConstraints(135, 55, 75, 20));

        jLabel114.setText("sp. trasporto");
        dati.add(jLabel114, new org.netbeans.lib.awtextra.AbsoluteConstraints(135, 40, 80, 15));

        butPrezziPrec.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        butPrezziPrec.setText("prezzi precedenti");
        butPrezziPrec.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butPrezziPrecActionPerformed(evt);
            }
        });
        dati.add(butPrezziPrec, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 5, 165, 15));

        comPaga.setDbDescCampo("");
        comPaga.setDbNomeCampo("pagamento");
        comPaga.setDbTextAbbinato(null);
        comPaga.setDbTipoCampo("VARCHAR");
        comPaga.setDbTrovaMentreScrive(true);
        comPaga.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comPagaItemStateChanged(evt);
            }
        });
        comPaga.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                comPagaFocusLost(evt);
            }
        });
        dati.add(comPaga, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 145, 195, 20));

        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText("Pagamento");
        dati.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 145, 75, 20));

        butAddClie.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/Actions-contact-new-icon-16.png"))); // NOI18N
        butAddClie.setToolTipText("Crea un nuovo cliente");
        butAddClie.setMargin(new java.awt.Insets(1, 1, 1, 1));
        butAddClie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butAddClieActionPerformed(evt);
            }
        });
        dati.add(butAddClie, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 20, 25, 20));

        labGiornoPagamento.setText("giorno");
        dati.add(labGiornoPagamento, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 145, 50, 20));

        texGiornoPagamento.setToolTipText("Giorno del mese per le scadenze");
        texGiornoPagamento.setDbDescCampo("");
        texGiornoPagamento.setDbNomeCampo("giorno_pagamento");
        texGiornoPagamento.setDbTipoCampo("numerico");
        dati.add(texGiornoPagamento, new org.netbeans.lib.awtextra.AbsoluteConstraints(345, 145, 65, 20));

        texStatoOrdine.setBackground(new java.awt.Color(255, 200, 200));
        texStatoOrdine.setText("stato_ordine");
        texStatoOrdine.setDbDescCampo("");
        texStatoOrdine.setDbNomeCampo("stato_ordine");
        texStatoOrdine.setDbTipoCampo("");
        texStatoOrdine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                texStatoOrdineActionPerformed(evt);
            }
        });
        dati.add(texStatoOrdine, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 70, 75, -1));

        texTipoOrdine.setBackground(new java.awt.Color(255, 200, 200));
        texTipoOrdine.setText("tipoOrdine");
        texTipoOrdine.setDbDescCampo("");
        texTipoOrdine.setDbNomeCampo("tipo_fattura");
        texTipoOrdine.setDbTipoCampo("numerico");
        texTipoOrdine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                texTipoOrdineActionPerformed(evt);
            }
        });
        dati.add(texTipoOrdine, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 20, 75, -1));

        dati_altri2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel15.setText("destinazione merce");
        dati_altri2.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 5, 210, 15));

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
        dati_altri2.add(comClieDest, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 270, 20));

        labScon10.setFont(labScon10.getFont().deriveFont(labScon10.getFont().getSize()-1f));
        labScon10.setText("ragione sociale");
        labScon10.setToolTipText("");
        dati_altri2.add(labScon10, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 55, 80, 15));

        labScon11.setFont(labScon11.getFont().deriveFont(labScon11.getFont().getSize()-1f));
        labScon11.setText("indirizzo");
        labScon11.setToolTipText("");
        dati_altri2.add(labScon11, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 70, 45, 15));

        texDestIndirizzo.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        texDestIndirizzo.setToolTipText("");
        texDestIndirizzo.setDbDescCampo("");
        texDestIndirizzo.setDbNomeCampo("dest_indirizzo");
        texDestIndirizzo.setDbTipoCampo("");
        texDestIndirizzo.setFont(texDestIndirizzo.getFont().deriveFont((float)9));
        dati_altri2.add(texDestIndirizzo, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 70, 200, 15));

        labScon12.setFont(labScon12.getFont().deriveFont(labScon12.getFont().getSize()-1f));
        labScon12.setText("cap");
        labScon12.setToolTipText("");
        dati_altri2.add(labScon12, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 85, 25, 15));

        texDestCap.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        texDestCap.setToolTipText("");
        texDestCap.setDbDescCampo("");
        texDestCap.setDbNomeCampo("dest_cap");
        texDestCap.setDbTipoCampo("");
        texDestCap.setFont(texDestCap.getFont().deriveFont((float)9));
        dati_altri2.add(texDestCap, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 85, 35, 15));

        labScon13.setFont(labScon13.getFont().deriveFont(labScon13.getFont().getSize()-1f));
        labScon13.setText("loc.");
        labScon13.setToolTipText("");
        dati_altri2.add(labScon13, new org.netbeans.lib.awtextra.AbsoluteConstraints(85, 85, 25, 15));

        texDestLocalita.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        texDestLocalita.setToolTipText("");
        texDestLocalita.setDbDescCampo("");
        texDestLocalita.setDbNomeCampo("dest_localita");
        texDestLocalita.setDbTipoCampo("");
        texDestLocalita.setFont(texDestLocalita.getFont().deriveFont((float)9));
        dati_altri2.add(texDestLocalita, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 85, 95, 15));

        labScon14.setFont(labScon14.getFont().deriveFont(labScon14.getFont().getSize()-1f));
        labScon14.setText("prov.");
        labScon14.setToolTipText("");
        dati_altri2.add(labScon14, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 85, 25, 15));

        texDestProvincia.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        texDestProvincia.setToolTipText("");
        texDestProvincia.setDbDescCampo("");
        texDestProvincia.setDbNomeCampo("dest_provincia");
        texDestProvincia.setDbTipoCampo("");
        texDestProvincia.setFont(texDestProvincia.getFont().deriveFont((float)9));
        dati_altri2.add(texDestProvincia, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 85, 30, 15));

        labScon16.setFont(labScon16.getFont().deriveFont(labScon16.getFont().getSize()-1f));
        labScon16.setText("telefono");
        labScon16.setToolTipText("");
        dati_altri2.add(labScon16, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 100, 50, 15));

        texDestTelefono.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        texDestTelefono.setToolTipText("");
        texDestTelefono.setDbDescCampo("");
        texDestTelefono.setDbNomeCampo("dest_telefono");
        texDestTelefono.setDbTipoCampo("");
        texDestTelefono.setFont(texDestTelefono.getFont().deriveFont((float)9));
        dati_altri2.add(texDestTelefono, new org.netbeans.lib.awtextra.AbsoluteConstraints(65, 100, 70, 15));

        labScon15.setFont(labScon15.getFont().deriveFont(labScon15.getFont().getSize()-1f));
        labScon15.setText("cellulare");
        labScon15.setToolTipText("");
        dati_altri2.add(labScon15, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 100, 50, 15));

        texDestCellulare.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        texDestCellulare.setToolTipText("");
        texDestCellulare.setDbDescCampo("");
        texDestCellulare.setDbNomeCampo("dest_cellulare");
        texDestCellulare.setDbTipoCampo("");
        texDestCellulare.setFont(texDestCellulare.getFont().deriveFont((float)9));
        dati_altri2.add(texDestCellulare, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 100, 80, 15));

        labScon17.setFont(labScon17.getFont().deriveFont(labScon17.getFont().getSize()-1f));
        labScon17.setText("paese");
        labScon17.setToolTipText("");
        dati_altri2.add(labScon17, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 115, 50, 15));

        comPaese.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        comPaese.setDbNomeCampo("dest_paese");
        comPaese.setDbTipoCampo("");
        comPaese.setDbTrovaMentreScrive(true);
        comPaese.setFont(comPaese.getFont().deriveFont((float)9));
        dati_altri2.add(comPaese, new org.netbeans.lib.awtextra.AbsoluteConstraints(65, 115, 205, 15));

        labFaTitolo.setFont(labFaTitolo.getFont().deriveFont(labFaTitolo.getFont().getSize()-1f));
        labFaTitolo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labFaTitolo.setText("dati fattura accompagnatoria");
        dati_altri2.add(labFaTitolo, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 140, 270, -1));

        labFa1.setFont(labFa1.getFont().deriveFont(labFa1.getFont().getSize()-2f));
        labFa1.setText("Causale del trasporto");
        dati_altri2.add(labFa1, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 160, 115, 15));

        comCausaleTrasporto.setDbNomeCampo("causale_trasporto");
        comCausaleTrasporto.setDbRiempireForceText(true);
        comCausaleTrasporto.setDbSalvaKey(false);
        comCausaleTrasporto.setFont(comCausaleTrasporto.getFont().deriveFont(comCausaleTrasporto.getFont().getSize()-1f));
        dati_altri2.add(comCausaleTrasporto, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 160, 150, 17));

        labFa2.setFont(labFa2.getFont().deriveFont(labFa2.getFont().getSize()-2f));
        labFa2.setText("Aspetto esteriore beni");
        dati_altri2.add(labFa2, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 180, 110, 15));

        comAspettoEsterioreBeni.setDbNomeCampo("aspetto_esteriore_beni");
        comAspettoEsterioreBeni.setDbRiempireForceText(true);
        comAspettoEsterioreBeni.setDbSalvaKey(false);
        comAspettoEsterioreBeni.setFont(comAspettoEsterioreBeni.getFont().deriveFont(comAspettoEsterioreBeni.getFont().getSize()-1f));
        dati_altri2.add(comAspettoEsterioreBeni, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 180, 150, 17));

        labFa3.setFont(labFa3.getFont().deriveFont(labFa3.getFont().getSize()-2f));
        labFa3.setText("Num. colli");
        dati_altri2.add(labFa3, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 200, 60, 15));

        texNumeroColli.setText("numero_colli");
        texNumeroColli.setDbDescCampo("");
        texNumeroColli.setDbNomeCampo("numero_colli");
        texNumeroColli.setDbTipoCampo("");
        texNumeroColli.setFont(texNumeroColli.getFont().deriveFont(texNumeroColli.getFont().getSize()-1f));
        texNumeroColli.setmaxChars(255);
        dati_altri2.add(texNumeroColli, new org.netbeans.lib.awtextra.AbsoluteConstraints(65, 200, 95, 17));

        labFa4.setFont(labFa4.getFont().deriveFont(labFa4.getFont().getSize()-2f));
        labFa4.setText("1° Vettore");
        dati_altri2.add(labFa4, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 220, 60, 15));

        comVettori.setDbNomeCampo("vettore1");
        comVettori.setDbRiempireForceText(true);
        comVettori.setDbSalvaKey(false);
        comVettori.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        dati_altri2.add(comVettori, new org.netbeans.lib.awtextra.AbsoluteConstraints(65, 220, 205, 17));

        labFa5.setFont(labFa5.getFont().deriveFont(labFa5.getFont().getSize()-2f));
        labFa5.setText("Cons. o inizio trasp. a mezzo");
        dati_altri2.add(labFa5, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 240, 150, 15));

        comMezzoTrasporto.setDbNomeCampo("mezzo_consegna");
        comMezzoTrasporto.setDbRiempireForceText(true);
        comMezzoTrasporto.setDbSalvaKey(false);
        comMezzoTrasporto.setFont(comMezzoTrasporto.getFont().deriveFont(comMezzoTrasporto.getFont().getSize()-1f));
        dati_altri2.add(comMezzoTrasporto, new org.netbeans.lib.awtextra.AbsoluteConstraints(155, 240, 115, 17));

        labFa6.setFont(labFa6.getFont().deriveFont(labFa6.getFont().getSize()-2f));
        labFa6.setText("Porto");
        dati_altri2.add(labFa6, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 260, 60, 15));

        comPorto.setDbNomeCampo("porto");
        comPorto.setDbRiempireForceText(true);
        comPorto.setDbSalvaKey(false);
        comPorto.setFont(comPorto.getFont().deriveFont(comPorto.getFont().getSize()-1f));
        dati_altri2.add(comPorto, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 260, 210, 17));

        jLabel4.setFont(jLabel4.getFont().deriveFont(jLabel4.getFont().getSize()-2f));
        jLabel4.setText("Fornitore:");
        dati_altri2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 280, -1, -1));

        texForni.setDbComboAbbinata(comForni);
        texForni.setDbNomeCampo("fornitore");
        texForni.setFont(texForni.getFont().deriveFont(texForni.getFont().getSize()-1f));
        texForni.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                texForniFocusLost(evt);
            }
        });
        dati_altri2.add(texForni, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 280, 35, 17));

        comForni.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comForni.setDbNomeCampo("fornitore");
        comForni.setDbRiempire(false);
        comForni.setDbSalvare(false);
        comForni.setDbTextAbbinato(texForni);
        comForni.setDbTrovaMentreScrive(true);
        comForni.setFont(comForni.getFont().deriveFont(comForni.getFont().getSize()-1f));
        comForni.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comForniItemStateChanged(evt);
            }
        });
        dati_altri2.add(comForni, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 280, 170, 17));

        texDestRagioneSociale.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        texDestRagioneSociale.setToolTipText("");
        texDestRagioneSociale.setDbDescCampo("");
        texDestRagioneSociale.setDbNomeCampo("dest_ragione_sociale");
        texDestRagioneSociale.setDbTipoCampo("");
        texDestRagioneSociale.setFont(texDestRagioneSociale.getFont().deriveFont((float)9));
        dati_altri2.add(texDestRagioneSociale, new org.netbeans.lib.awtextra.AbsoluteConstraints(105, 55, 165, 15));

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "destinazione diversa (manuale)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        dati_altri2.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 275, 95));

        dati.add(dati_altri2, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 0, 275, 300));

        dati_altri1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel24.setText("note pagamento");
        dati_altri1.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 5, 75, 20));

        texNotePagamento.setText("note pagamento");
        texNotePagamento.setDbDescCampo("");
        texNotePagamento.setDbNomeCampo("note_pagamento");
        texNotePagamento.setDbTipoCampo("");
        texNotePagamento.setmaxChars(255);
        dati_altri1.add(texNotePagamento, new org.netbeans.lib.awtextra.AbsoluteConstraints(85, 5, 320, -1));

        cheOpzioneRibaDestDiversa.setText("stampa Dest. Diversa su Distinta Riba");
        cheOpzioneRibaDestDiversa.setToolTipText("Selezionando questa opzione stampa la Destinazione diversa nella Distinta delle RIBA");
        cheOpzioneRibaDestDiversa.setDbDescCampo("Opzione Dest. Diversa Riba");
        cheOpzioneRibaDestDiversa.setDbNomeCampo("opzione_riba_dest_diversa");
        cheOpzioneRibaDestDiversa.setDbTipoCampo("");
        cheOpzioneRibaDestDiversa.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        dati_altri1.add(cheOpzioneRibaDestDiversa, new org.netbeans.lib.awtextra.AbsoluteConstraints(85, 30, -1, 20));

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText("Banca ABI");
        dati_altri1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 50, 75, 20));

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
        dati_altri1.add(texBancAbi, new org.netbeans.lib.awtextra.AbsoluteConstraints(85, 50, 50, 20));

        butCoor.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        butCoor.setText("cerca");
        butCoor.setMargin(new java.awt.Insets(1, 2, 1, 2));
        butCoor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butCoorActionPerformed(evt);
            }
        });
        dati_altri1.add(butCoor, new org.netbeans.lib.awtextra.AbsoluteConstraints(135, 50, 40, 20));

        labBancAbi.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        labBancAbi.setText("...");
        dati_altri1.add(labBancAbi, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 50, 235, 20));

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel20.setText("Banca CAB");
        dati_altri1.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 70, 75, 20));

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
        dati_altri1.add(texBancCab, new org.netbeans.lib.awtextra.AbsoluteConstraints(85, 70, 50, 20));

        labBancCab.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        labBancCab.setText("...");
        dati_altri1.add(labBancCab, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 70, 235, 20));

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel23.setText("SWIFT");
        dati_altri1.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 110, 75, 20));

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
        dati_altri1.add(texBancIban, new org.netbeans.lib.awtextra.AbsoluteConstraints(85, 90, 320, 20));

        labAgente.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labAgente.setText("Agente");
        dati_altri1.add(labAgente, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 140, 75, 20));

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
        dati_altri1.add(comAgente, new org.netbeans.lib.awtextra.AbsoluteConstraints(85, 140, 175, 20));

        labProvvigione.setText("Provvigione");
        dati_altri1.add(labProvvigione, new org.netbeans.lib.awtextra.AbsoluteConstraints(275, 140, 80, 20));

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
        dati_altri1.add(texProvvigione, new org.netbeans.lib.awtextra.AbsoluteConstraints(355, 140, 35, 20));

        labPercentoProvvigione.setText("%");
        dati_altri1.add(labPercentoProvvigione, new org.netbeans.lib.awtextra.AbsoluteConstraints(395, 140, 15, 20));

        texSwift.setToolTipText("");
        texSwift.setDbDescCampo("");
        texSwift.setDbNomeCampo("banca_iban");
        texSwift.setDbTipoCampo("");
        texSwift.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                texSwiftActionPerformed(evt);
            }
        });
        texSwift.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                texSwiftFocusLost(evt);
            }
        });
        dati_altri1.add(texSwift, new org.netbeans.lib.awtextra.AbsoluteConstraints(85, 110, 80, 20));

        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel26.setText("IBAN");
        dati_altri1.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 90, 75, 20));

        dati.add(dati_altri1, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 165, 415, 160));

        labRiferimento.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labRiferimento.setText("Consegna");
        dati.add(labRiferimento, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 120, 70, 20));

        texConsegna.setText("consegna");
        texConsegna.setDbDescCampo("");
        texConsegna.setDbNomeCampo("data_consegna");
        texConsegna.setDbTipoCampo("");
        texConsegna.setmaxChars(255);
        dati.add(texConsegna, new org.netbeans.lib.awtextra.AbsoluteConstraints(305, 120, 105, 20));

        textTipoSnj.setBackground(new java.awt.Color(255, 200, 200));
        textTipoSnj.setText("tipo_snj");
        textTipoSnj.setDbDescCampo("");
        textTipoSnj.setDbNomeCampo("tipo_snj");
        textTipoSnj.setDbTipoCampo("");
        dati.add(textTipoSnj, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 45, 75, -1));

        prezzi_ivati.setBackground(new java.awt.Color(255, 204, 204));
        prezzi_ivati.setText("prezzi ivati");
        prezzi_ivati.setToolTipText("Selezionando questa opzione stampa la Destinazione diversa nella Distinta delle RIBA");
        prezzi_ivati.setDbDescCampo("Prezzi Ivati");
        prezzi_ivati.setDbNomeCampo("prezzi_ivati");
        prezzi_ivati.setDbTipoCampo("");
        prezzi_ivati.setFont(new java.awt.Font("Dialog", 0, 9)); // NOI18N
        prezzi_ivati.setIconTextGap(1);
        dati.add(prezzi_ivati, new org.netbeans.lib.awtextra.AbsoluteConstraints(735, 155, 80, 25));

        consegna_prevista.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                consegna_previstaPropertyChange(evt);
            }
        });
        dati.add(consegna_prevista, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 55, 100, 20));

        jLabel115.setText("sp. incasso");
        dati.add(jLabel115, new org.netbeans.lib.awtextra.AbsoluteConstraints(225, 40, 75, 15));

        labModConsegna.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labModConsegna.setText("modalità di consegna");
        dati.add(labModConsegna, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 330, 120, 20));

        comConsegna.setDbDescCampo("Modalità di consegna");
        comConsegna.setDbNomeCampo("modalita_consegna");
        comConsegna.setDbTipoCampo("");
        comConsegna.setDbTrovaMentreScrive(true);
        comConsegna.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comConsegnaActionPerformed(evt);
            }
        });
        dati.add(comConsegna, new org.netbeans.lib.awtextra.AbsoluteConstraints(145, 330, 120, -1));

        comScarico.setDbDescCampo("Modalità di scarico");
        comScarico.setDbNomeCampo("modalita_scarico");
        comScarico.setDbTipoCampo("");
        comScarico.setDbTrovaMentreScrive(true);
        comScarico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comScaricoActionPerformed(evt);
            }
        });
        dati.add(comScarico, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 330, -1, -1));

        labModScarico.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labModScarico.setText("scarico");
        dati.add(labModScarico, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 330, 75, 20));
        dati.add(texCliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(175, 20, 190, 20));

        apriclienti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                apriclientiActionPerformed(evt);
            }
        });
        dati.add(apriclienti, new org.netbeans.lib.awtextra.AbsoluteConstraints(365, 20, 20, 20));

        split.setLeftComponent(dati);

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

        prezzi_ivati_virtual.setFont(prezzi_ivati_virtual.getFont().deriveFont(prezzi_ivati_virtual.getFont().getSize()-1f));
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
        butNuovArti.setText("Nuova riga");
        butNuovArti.setMargin(new java.awt.Insets(2, 2, 2, 2));
        butNuovArti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butNuovArtiActionPerformed(evt);
            }
        });
        jPanel1.add(butNuovArti);

        butNuovArti2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/document-new.png"))); // NOI18N
        butNuovArti2.setText("Sub-totale");
        butNuovArti2.setMargin(new java.awt.Insets(2, 2, 2, 2));
        butNuovArti2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butNuovArti2ActionPerformed(evt);
            }
        });
        jPanel1.add(butNuovArti2);

        butNuovArti1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/document-new.png"))); // NOI18N
        butNuovArti1.setText("Peso");
        butNuovArti1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        butNuovArti1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butNuovArti1ActionPerformed(evt);
            }
        });
        jPanel1.add(butNuovArti1);

        butImportRighe.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/document-new.png"))); // NOI18N
        butImportRighe.setText("Importa Righe Da CSV");
        butImportRighe.setMargin(new java.awt.Insets(2, 2, 2, 2));
        butImportRighe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butImportRigheActionPerformed(evt);
            }
        });
        jPanel1.add(butImportRighe);

        butImportRigheProskin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/document-new.png"))); // NOI18N
        butImportRigheProskin.setText("Righe da CC Xls");
        butImportRigheProskin.setMargin(new java.awt.Insets(2, 2, 2, 2));
        butImportRigheProskin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butImportRigheProskinActionPerformed(evt);
            }
        });
        jPanel1.add(butImportRigheProskin);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel3.setPreferredSize(new java.awt.Dimension(157, 80));

        jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 3));

        butPdf.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/pdf-icon-16.png"))); // NOI18N
        butPdf.setText("Crea PDF");
        butPdf.setMargin(new java.awt.Insets(2, 4, 2, 4));
        butPdf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butPdfActionPerformed(evt);
            }
        });
        jPanel5.add(butPdf);

        butStampa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/document-print.png"))); // NOI18N
        butStampa.setText("Stampa");
        butStampa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butStampaActionPerformed(evt);
            }
        });
        jPanel5.add(butStampa);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator2.setPreferredSize(new java.awt.Dimension(4, 20));
        jPanel5.add(jSeparator2);

        butUndo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/edit-undo.png"))); // NOI18N
        butUndo.setText("Annulla");
        butUndo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butUndoActionPerformed(evt);
            }
        });
        jPanel5.add(butUndo);

        butSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/devices/media-floppy.png"))); // NOI18N
        butSave.setText("Salva");
        butSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSaveActionPerformed(evt);
            }
        });
        jPanel5.add(butSave);

        jPanel7.setLayout(new java.awt.GridLayout(0, 1));

        jPanel8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 1));

        jLabel1.setText("Stato");
        jPanel8.add(jLabel1);

        comStatoOrdine.setDbNomeCampo("vettore1");
        comStatoOrdine.setDbRiempireForceText(true);
        comStatoOrdine.setDbSalvaKey(false);
        comStatoOrdine.setDbSalvare(false);
        comStatoOrdine.setDbTextAbbinato(texStatoOrdine);
        comStatoOrdine.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jPanel8.add(comStatoOrdine);

        jPanel7.add(jPanel8);

        jPanel9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 1));

        stato_evasione.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Evaso", "Evaso Parziale", "Non Evaso" }));
        stato_evasione.setSelectedIndex(2);
        jPanel9.add(stato_evasione);

        jPanel7.add(jPanel9);

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE)
                    .add(jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(2, 2, 2)
                .add(jPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        texTotaImpo.setEditable(false);
        texTotaImpo.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        texTotaImpo.setColumns(8);
        texTotaImpo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        texTotaImpo.setText("0");
        texTotaImpo.setDbTipoCampo("valuta");

        texTotaIva.setEditable(false);
        texTotaIva.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        texTotaIva.setColumns(8);
        texTotaIva.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        texTotaIva.setText("0");
        texTotaIva.setDbTipoCampo("valuta");

        texTota.setEditable(false);
        texTota.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        texTota.setColumns(8);
        texTota.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        texTota.setText("0");
        texTota.setDbTipoCampo("valuta");
        texTota.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("TOTALE");

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel21.setText("Totale Iva");

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel22.setText("Totale Imponibile");

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel25.setText("Sconto");

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

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap(252, Short.MAX_VALUE)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                        .add(jLabel21)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(texTotaIva, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jLabel25)
                            .add(jLabel22))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(texSconto, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(texTotaImpo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(texTota, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel4Layout.linkSize(new java.awt.Component[] {texSconto, texTota, texTotaImpo, texTotaIva}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .add(3, 3, 3)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(texSconto, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel25))
                .add(3, 3, 3)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(texTotaImpo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel22))
                .add(3, 3, 3)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(texTotaIva, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel21))
                .add(3, 3, 3)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(texTota, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2))
                .addContainerGap(39, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout datiRigheLayout = new org.jdesktop.layout.GroupLayout(datiRighe);
        datiRighe.setLayout(datiRigheLayout);
        datiRigheLayout.setHorizontalGroup(
            datiRigheLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(datiRigheLayout.createSequentialGroup()
                .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 472, Short.MAX_VALUE)
                .add(0, 0, 0)
                .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 914, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 914, Short.MAX_VALUE)
        );
        datiRigheLayout.setVerticalGroup(
            datiRigheLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(datiRigheLayout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, 0)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE)
                .add(0, 0, 0)
                .add(datiRigheLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                    .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        split.setRightComponent(datiRighe);

        panDati.add(split, java.awt.BorderLayout.CENTER);

        panTab.addTab("Dati Ordine", panDati);

        panFoglioRigheSNJA.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                panFoglioRigheSNJAFocusGained(evt);
            }
        });
        panFoglioRigheSNJA.setLayout(new java.awt.BorderLayout());

        panGriglia.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                panGrigliaFocusGained(evt);
            }
        });
        panGriglia.setLayout(new java.awt.BorderLayout());

        foglioTipoA.setModel(new javax.swing.table.DefaultTableModel(
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
        foglioTipoA.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                foglioTipoAMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                foglioTipoAMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                foglioTipoAMouseReleased(evt);
            }
        });
        jScrollPane2.setViewportView(foglioTipoA);

        panGriglia.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        panFoglioRigheSNJA.add(panGriglia, java.awt.BorderLayout.CENTER);

        labStatus.setText("...");
        panTotale.add(labStatus);

        panFoglioRigheSNJA.add(panTotale, java.awt.BorderLayout.SOUTH);

        panTab.addTab("Foglio Righe Tipo A", panFoglioRigheSNJA);

        panFoglioRigheSNJB.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                panFoglioRigheSNJBFocusGained(evt);
            }
        });
        panFoglioRigheSNJB.setLayout(new java.awt.BorderLayout());

        panGriglia1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                panGriglia1FocusGained(evt);
            }
        });
        panGriglia1.setLayout(new java.awt.BorderLayout());

        foglioTipoB.setModel(new javax.swing.table.DefaultTableModel(
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
        foglioTipoB.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                foglioTipoBMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                foglioTipoBMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                foglioTipoBMouseReleased(evt);
            }
        });
        jScrollPane3.setViewportView(foglioTipoB);

        panGriglia1.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        panFoglioRigheSNJB.add(panGriglia1, java.awt.BorderLayout.CENTER);

        labStatus1.setText("...");
        panTotale1.add(labStatus1);

        panFoglioRigheSNJB.add(panTotale1, java.awt.BorderLayout.SOUTH);

        panTab.addTab("Foglio Righe Tipo B", panFoglioRigheSNJB);

        getContentPane().add(panTab, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void comPagaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comPagaItemStateChanged
        texGiornoPagamento.setVisible(false);
        labGiornoPagamento.setVisible(false);

        try {

            ResultSet r = Db.lookUp(String.valueOf(comPaga.getSelectedKey()), "codice", "pagamenti");
            if (r != null && Db.nz(r.getString("flag_richiedi_giorno"), "N").equalsIgnoreCase("S")) {
                texGiornoPagamento.setVisible(true);
                labGiornoPagamento.setVisible(true);

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

            } else {
                texGiornoPagamento.setVisible(false);
                labGiornoPagamento.setVisible(false);
                texGiornoPagamento.setText("");
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }//GEN-LAST:event_comPagaItemStateChanged

    private void texSeriFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texSeriFocusLost
    }//GEN-LAST:event_texSeriFocusLost

    private void texSeriKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texSeriKeyPressed

        if (evt.getKeyCode() == evt.VK_TAB || evt.getKeyCode() == evt.VK_ENTER) {
            assegnaSerie();
        }
    }//GEN-LAST:event_texSeriKeyPressed

    private void comAgenteFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_comAgenteFocusLost
        InvoicexUtil.controlloProvvigioniAutomatiche(comAgente, texProvvigione, texScon1, this, acquisto ? null : cu.toInteger(texClie.getText()));
    }//GEN-LAST:event_comAgenteFocusLost

    private void comAgenteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comAgenteActionPerformed
    }//GEN-LAST:event_comAgenteActionPerformed

    private void texClieActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texClieActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_texClieActionPerformed

    private void comPagaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_comPagaFocusLost
        if (!acquisto) {
            //carico note su pagamento
            try {
                ResultSet p = Db.openResultSet("select * from pagamenti where codice = " + Db.pc(this.comPaga.getSelectedKey(), Types.VARCHAR));
                if (p.next()) {
                    this.texNotePagamento.setText(p.getString("note_su_documenti"));
                }
            } catch (Exception err) {
                err.printStackTrace();
            }
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

    private void comClieActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comClieActionPerformed
        //apro combo destinazione cliente
        if (!loading) {
            sql = "select obsoleto from clie_forn";
            sql += " where codice = " + Db.pc(this.texClie.getText(), "NUMERIC");

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
        }
        
        sql = "select ragione_sociale,codice from clie_forn_dest";
        sql += " where codice_cliente = " + Db.pc(this.texClie.getText(), "NUMERIC");
        sql += " order by ragione_sociale";

        riempiDestDiversa(sql);

        if (main.getPersonal().equals(main.PERSONAL_CUCINAIN)) {
            listiniTicket();
        }
            
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

        frameAggiungiCliente.addNew(this.comClie);
    }//GEN-LAST:event_butAddClieActionPerformed

    private void butStampaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butStampaActionPerformed
        if (block_aggiornareProvvigioni) return;

        if (SwingUtils.showYesNoMessage(this, "Prima di stampare è necessario salvare il documento, proseguire ?")) {
            if (controlloCampi() == true) {
                this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

                String dbSerie = this.ordine.serie;
                int dbNumero = this.ordine.numero;
                int dbAnno = this.ordine.anno;

                saveDocumento();
                main.events.fireInvoicexEvent(new InvoicexEvent(this, InvoicexEvent.TYPE_SAVE));
                
                this.dati.dbSave();
                
                if (evt.getActionCommand().equalsIgnoreCase("pdf")) {
                    try {
                        InvoicexUtil.creaPdf(getTipoDoc(), new Integer[] {id}, true, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                        SwingUtils.showExceptionMessage(this, e);
                    }                
                } else {            
                    frmElenOrdini.stampa("", dbSerie, dbNumero, dbAnno, acquisto, id);
                }

                String nuova_serie = texSeri.getText();
                Integer nuovo_numero = cu.toInteger(texNumeOrdine.getText());
                Integer nuovo_anno = cu.toInteger(texAnno.getText());

                //aggiorno le righe temp
                sql = "update righ_ordi" + suff + "_temp";
                sql += " set serie = '" + nuova_serie + "'";
                sql += " , numero = " + nuovo_numero + "";
                sql += " , anno = " + nuovo_anno + "";
                sql += " where serie = " + db.pc(serie_originale, "VARCHAR");
                sql += " and numero = " + numero_originale;
                sql += " and anno = " + anno_originale;
                Db.executeSqlDialogExc(sql, true);

                serie_originale = texSeri.getText();
                numero_originale = cu.toInteger(texNumeOrdine.getText());
                anno_originale = cu.toInteger(texAnno.getText());
                
                totaleIniziale = doc.getTotale();
                totaleDaPagareIniziale = doc.getTotale_da_pagare();
                pagamentoIniziale = comPaga.getText();
                pagamentoInizialeGiorno = texGiornoPagamento.getText();
                provvigioniIniziale = Db.getDouble(texProvvigione.getText());
                codiceAgenteIniziale = it.tnx.Util.getInt(comAgente.getSelectedKey().toString());

                //una volta salvatao e stampato entro in modalitaà modifica se ero in inserimento
                if (dbStato.equals(frmTestOrdine.DB_INSERIMENTO)) {
                    dbStato = frmTestOrdine.DB_MODIFICA;
                    //e riporto le righe in _temp
                    sql = "insert into righ_ordi" + suff + "_temp";
                    sql += " select *, '" + main.login + "' as username";
                    sql += " from righ_ordi" + suff;
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
        }

        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_butStampaActionPerformed

    private void texSpeseTrasportoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texSpeseTrasportoActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_texSpeseTrasportoActionPerformed

    private void butPrezziPrecActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butPrezziPrecActionPerformed
        showPrezziFatture();
    }//GEN-LAST:event_butPrezziPrecActionPerformed

    private void texSpeseTrasportoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texSpeseTrasportoKeyReleased

        try {
            ordine.speseTrasportoIva = Db.getDouble(this.texSpeseTrasporto.getText());
        } catch (Exception err) {
            ordine.speseTrasportoIva = 0;
        }

        ordine.dbRefresh();

        ricalcolaTotali();
    }//GEN-LAST:event_texSpeseTrasportoKeyReleased

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
    }//GEN-LAST:event_formInternalFrameClosing

    private void texBancAbiFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texBancAbiFocusLost
        texBancAbiActionPerformed(null);
    }//GEN-LAST:event_texBancAbiFocusLost

    private void texBancCabFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texBancCabFocusLost
        trovaCab();
    }//GEN-LAST:event_texBancCabFocusLost

    private void texBancCCFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texBancCCFocusLost
    }//GEN-LAST:event_texBancCCFocusLost

    private void texBancCabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texBancCabActionPerformed
        trovaCab();
    }//GEN-LAST:event_texBancCabActionPerformed

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

//        frmListCoorBanc frm = new frmListCoorBanc(coords);

//        main.getPadre().openFrame(frm, 700, 500, 150, 50);
        coords.setField_texIban(this.texBancIban);
        coords.setField_texSwift(this.texSwift);
        
        frmDatiAzieBanc frmcca = new frmDatiAzieBanc(coords);
        main.getPadre().openFrame(frmcca, 700, 500, 150, 50);
    }//GEN-LAST:event_butCoorActionPerformed

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
            if (dbStato == DB_INSERIMENTO) {
                ordine.dbRicalcolaProgressivo(dbStato, this.texData.getText(), this.texNumeOrdine, texAnno, texSeri.getText(), id);
                ordine.numero = new Integer(this.texNumeOrdine.getText()).intValue();
                id_modificato = true;
            } else {
                //controllo che se è un numero già presente non glielo faccio fare percè altrimenti sovrascrive una altra fattura
                sql = "select numero from test_ordi" + suff;
                sql += " where serie " + Db.pcW(this.ordine.serie, "VARCHAR");
                sql += " and numero " + Db.pcW(texNumeOrdine.getText(), "NUMBER");
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
                ordine.anno = Integer.parseInt(getAnno());
                ordine.numero = Integer.parseInt(texNumeOrdine.getText());

                sql = "update righ_ordi" + suff;
                sql += " set anno = " + Db.pc(ordine.anno, "NUMBER");
                sql += " , numero = " + Db.pc(ordine.numero, "NUMBER");
                sql += " where serie " + Db.pcW(this.ordine.serie, "VARCHAR");
                sql += " and numero " + Db.pcW(old_id, "NUMBER");
                sql += " and anno " + Db.pcW(old_anno, "VARCHAR");
                Db.executeSql(sql);

                sql = "update test_ordi" + suff;
                sql += " set anno = " + Db.pc(ordine.anno, "NUMBER");
                sql += " , numero = " + Db.pc(ordine.numero, "NUMBER");
                sql += " where serie " + Db.pcW(this.ordine.serie, "VARCHAR");
                sql += " and numero " + Db.pcW(old_id, "NUMBER");
                sql += " and anno " + Db.pcW(old_anno, "VARCHAR");
                Db.executeSql(sql);

//                dati.dbChiaveValori.clear();
//                dati.dbChiaveValori.put("serie", ordine.serie);
//                dati.dbChiaveValori.put("numero", ordine.numero);
//                dati.dbChiaveValori.put("anno", ordine.anno);

                //riassocio
                dbAssociaGrigliaRighe();


                doc.load(Db.INSTANCE, ordine.numero, ordine.serie, ordine.anno, acquisto ? Db.TIPO_DOCUMENTO_ORDINE_ACQUISTO : Db.TIPO_DOCUMENTO_ORDINE, id);
                ricalcolaTotali();

                anno_modificato = true;

            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }//GEN-LAST:event_texDataFocusLost

    private void texScon3KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texScon3KeyReleased

        try {
            ordine.sconto3 = Db.getDouble(this.texScon3.getText());
        } catch (Exception err) {

            //err.printStackTrace();
            ordine.sconto3 = 0;
        }

        ordine.dbRefresh();

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
            if (comClie.getSelectedIndex() == -1 && !StringUtils.isEmpty(comClie.getText())) {
                int ret = JOptionPane.showConfirmDialog(this, "Non hai selezionato un cliente esistente, vuoi creare '" + comClie.getText() + "' come cliente provvisorio ?", "Attenzione", JOptionPane.YES_NO_OPTION);
                if (ret == JOptionPane.YES_OPTION) {
                    int codice = 1;
                    ResultSet r = null;
                    try {
                        r = DbUtils.tryOpenResultSet(Db.getConn(), "select max(codice) from clie_forn");
                        if (r.next()) {
                            codice = r.getInt(1) + 1;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        try {
                            r.getStatement().close();
                        } catch (Exception ex) {
                        }
                    }
                    String sql = "insert into clie_forn (codice, tipo, ragione_sociale) values (" + codice + ", 'P', " + Db.pc(comClie.getText(), Types.VARCHAR) + ")";
                    try {
                        DbUtils.tryExecQuery(Db.getConn(), sql);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    comClie.dbRefreshItems();
                    comClie.dbTrovaKey(String.valueOf(codice));
                    comClie.syncToText();
                }
            } else {
                this.recuperaDatiCliente();
            }
        }
    }//GEN-LAST:event_comClieFocusLost

    private void texClieFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texClieFocusLost

        try {

            if (this.texClie.getText().length() > 0) {
                this.ordine.forceCliente(Long.parseLong(this.texClie.getText()));
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }//GEN-LAST:event_texClieFocusLost

    private void texSpeseIncassoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texSpeseIncassoKeyReleased

        try {
            ordine.speseIncassoIva = Db.getDouble(this.texSpeseIncasso.getText());
        } catch (Exception err) {
            ordine.speseIncassoIva = 0;
        }

        ordine.dbRefresh();

        ricalcolaTotali();
    }//GEN-LAST:event_texSpeseIncassoKeyReleased

    private void texScon2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texScon2KeyReleased

        try {
            ordine.sconto2 = Db.getDouble(this.texScon2.getText());
        } catch (Exception err) {

            //err.printStackTrace();
            ordine.sconto2 = 0;
        }

        ordine.dbRefresh();

        ricalcolaTotali();
    }//GEN-LAST:event_texScon2KeyReleased

    private void texScon1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texScon1KeyReleased

        try {
            ordine.sconto1 = Db.getDouble(this.texScon1.getText());
        } catch (Exception err) {
            ordine.sconto1 = 0;
        }

        ordine.dbRefresh();

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
    }//GEN-LAST:event_texScon1FocusLost

    private void texScon1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texScon1ActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_texScon1ActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened

        // Add your handling code here:
        this.griglia.resizeColumnsPerc(true);
    }//GEN-LAST:event_formInternalFrameOpened

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed

        // Add your handling code here:
        try {
            tim.cancel();
        } catch (Exception e) {
        }
        main.getPadre().closeFrame(this);
    }//GEN-LAST:event_formInternalFrameClosed

    private void grigliaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_grigliaMouseClicked

        // Add your handling code here:
        try {

            if (evt.getClickCount() == 2) {

                //modifico o la riga o la finestra
                if(main.getPersonalContain("snj") && tipoSNJ != null && (tipoSNJ.equals("A")|| tipoSNJ.equals("B"))) {
                    SwingUtils.showInfoMessage(this, "La tua personalizzazione permette l'inserimento solo tramite il foglio righe", "Modifica con foglio righe");
                } else {
                    popGrigModiActionPerformed(null);
                }
            }
        } catch (Exception err) {
            javax.swing.JOptionPane.showMessageDialog(null, err.toString());
            err.printStackTrace();
        }


    }//GEN-LAST:event_grigliaMouseClicked

    private void popGrigElimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popGrigElimActionPerformed
        //elimino la riga
        this.griglia.dbDelete();

        ricalcolaSubTotaliOrdine();
        
        griglia.dbRefresh();
        ordine.dbRefresh();
        ricalcolaTotali();
        dbAssociaGrigliaRighe();
        
    }//GEN-LAST:event_popGrigElimActionPerformed
    public void ricalcolaSubTotaliOrdine() {
        try {
            String dbSerie    = this.texSeri.getText();
            String dbNumero   = this.texNumeOrdine.getText();
            String dbAnno     = this.texAnno.getText();
            
            String query = "SELECT * FROM righ_ordi WHERE serie = " + Db.pc(dbSerie, "VARCHAR");
            query += " and numero = " + Db.pc(dbNumero, Types.INTEGER);
            query += " and anno = " + Db.pc(dbAnno, Types.INTEGER);
            query += " ORDER BY riga";
            ResultSet rs = Db.openResultSet(query);

            double subTotale = 0d;
            double subTotaleIvato = 0d;
            while (rs.next()) {
                if (rs.getString("codice_articolo").equals(dbOrdine.CODICE_SUBTOTALE)) {
                    String subTotaleDesc = "";
                    if (!prezzi_ivati.isSelected()) {
                        subTotaleDesc = dbOrdine.DESCRIZIONE_SUBTOTALE + " " + FormatUtils.formatEuroIta(subTotale);
                    } else {
                        subTotaleDesc = dbOrdine.DESCRIZIONE_SUBTOTALE + " " + FormatUtils.formatEuroIta(subTotaleIvato);
                    }
                    String updateQuery = "UPDATE righ_ordi SET descrizione = " + Db.pc(subTotaleDesc, Types.VARCHAR);
                    updateQuery += " WHERE serie = " + Db.pc(dbSerie, "VARCHAR");
                    updateQuery += " AND numero = " + Db.pc(dbNumero, Types.INTEGER);
                    updateQuery += " AND anno = " + Db.pc(dbAnno, Types.INTEGER);
                    updateQuery += " AND riga = " + Db.pc(rs.getInt("riga"), Types.INTEGER);

                    Db.executeSql(updateQuery);
                    subTotale = 0d;
                    subTotaleIvato = 0d;
                } else {
                    double totaleRiga = rs.getDouble("totale_imponibile");
                    subTotale += totaleRiga;
                    double totaleRigaIvato = rs.getDouble("totale_ivato");
                    subTotaleIvato += totaleRigaIvato;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
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
//            frmNuovRiga frm = new frmNuovRiga(this, this.dati.DB_MODIFICA, this.texSeri.getText(), Integer.valueOf(this.texNumeOrdine.getText()).intValue(), "P", Integer.valueOf(this.griglia.getValueAt(griglia.getSelectedRow(), 3).toString()).intValue(), Integer.valueOf(this.texAnno.getText()).intValue(), codiceListino, codiceCliente);
//            temp.openFrame(frm, 600, 350, 100, 100);
//            frm.setStato();
//        } else {
        try {
            frmNuovRigaDescrizioneMultiRigaNew frm = new frmNuovRigaDescrizioneMultiRigaNew(this, this.dati.DB_MODIFICA, this.texSeri.getText(), Integer.valueOf(this.texNumeOrdine.getText()).intValue(), "P", Integer.valueOf(this.griglia.getValueAt(griglia.getSelectedRow(), 3).toString()).intValue(), Integer.valueOf(this.texAnno.getText()).intValue(), codiceListino, codiceCliente);
            int w = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRiga_w", "980"));
            int h = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRiga_h", "460"));
            int top = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRiga_top", "100"));
            int left = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRiga_left", "100"));
            main.getPadre().openFrame(frm, w, h, top, left);
            frm.setStato();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        }

    }//GEN-LAST:event_popGrigModiActionPerformed

    private void butNuovArtiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butNuovArtiActionPerformed

        SwingUtils.mouse_wait(this);

        MicroBench mb = new MicroBench();
        mb.start();

        String codiceListino = "1";

        if (texClie.getText().length() > 0) {
            try {
                codiceListino = Db.lookUp(texClie.getText(), "codice", "clie_forn").getString("codice_listino");
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
        
        mb.out("listino");

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

        mb.out("multirigao");

//        if (multiriga == false) {
//
//            frmNuovRiga frm = new frmNuovRiga(this, this.dati.DB_INSERIMENTO, this.texSeri.getText(), Integer.valueOf(this.texNumeOrdine.getText()).intValue(), "P", 0, Integer.valueOf(this.texAnno.getText()).intValue(), codiceListino, codiceCliente);
//            temp.openFrame(frm, 600, 350, 100, 100);
//            frm.setStato();
//        } else {
        try {
            mb.out("pre");
            frmNuovRigaDescrizioneMultiRigaNew frm = new frmNuovRigaDescrizioneMultiRigaNew(this, this.dati.DB_INSERIMENTO, this.texSeri.getText(), Integer.valueOf(this.texNumeOrdine.getText()).intValue(), "P", 0, Integer.valueOf(this.texAnno.getText()).intValue(), codiceListino, codiceCliente, null, this.id);
            mb.out("post");
            int w = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRiga_w", "980"));
            int h = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRiga_h", "460"));
            int top = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRiga_top", "100"));
            int left = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRiga_left", "100"));
            main.getPadre().openFrame(frm, w, h, top, left);
            mb.out("post open");
            frm.setStato();
            mb.out("post setstato");
            frm.texProvvigione.setText(texProvvigione.getText());
            frm.consegna_prevista.setDate(consegna_prevista.getDate());
        } catch (Exception e) {
            e.printStackTrace();
        }
//        }

        SwingUtils.mouse_def(this);

    }//GEN-LAST:event_butNuovArtiActionPerformed

    private void butUndoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butUndoActionPerformed
        if (block_aggiornareProvvigioni) return;
        
        if (evt != null) {
            if (!SwingUtils.showYesNoMessage(main.getPadreWindow(), "Sicuro di annullare le modifiche ?")) {
                return;
            }
        }

        // Add your handling code here:
        if (dbStato == this.DB_INSERIMENTO) {

            //elimino la testata inserita e poi annullata
            String sql = "delete from test_ordi" + suff;
            sql += " where serie = " + Db.pc(String.valueOf(this.ordine.serie), "VARCHAR");
            sql += " and numero = " + Db.pc(String.valueOf(this.ordine.numero), "NUMBER");
            //sql += " and stato = " + Db.pc(String.valueOf(this.ordine.stato), "VARCHAR");
            sql += " and anno = " + Db.pc(String.valueOf(this.ordine.anno), "INTEGER");
            Db.executeSql(sql);
            sql = "delete from righ_ordi" + suff;
            sql += " where serie = " + Db.pc(String.valueOf(this.ordine.serie), "VARCHAR");
            sql += " and numero = " + Db.pc(String.valueOf(this.ordine.numero), "NUMBER");
            //sql += " and stato = " + Db.pc(String.valueOf(this.ordine.stato), "VARCHAR");
            sql += " and anno = " + Db.pc(String.valueOf(this.ordine.anno), "VARCHAR");
            Db.executeSql(sql);
        } else if (dbStato == this.DB_MODIFICA) {
            System.out.println("annulla da modifica, elimino " + ordine.serie + "/" + ordine.numero + "/" + ordine.anno + " e rimetto da temp " + serie_originale + "/" + numero_originale + "/" + anno_originale);

            //rimetto numero originale
            sql = "update test_ordi" + suff;
            sql += " set numero = " + Db.pc(numero_originale, "NUMBER");
            sql += " , anno = " + Db.pc(anno_originale, "NUMBER");
            sql += " where id = " + this.id;
            Db.executeSql(sql);

            //elimino le righe inserite
            sql = "delete from righ_ordi" + suff;
            sql += " where serie " + Db.pcW(ordine.serie, "VARCHAR");
            sql += " and numero " + Db.pcW(ordine.numero, "NUMBER");
            sql += " and anno " + Db.pcW(String.valueOf(ordine.anno), "VARCHAR");
            Db.executeSql(sql);

            //e rimetto quelle da temp
            sql = "insert into righ_ordi" + suff + " (" + Db.getFieldList("righ_ordi" + suff, false) + ")";
            sql += " select " + Db.getFieldList("righ_ordi" + suff + "_temp", true);
            sql += " from righ_ordi" + suff + "_temp";
            sql += " where serie = " + Db.pc(String.valueOf(serie_originale), "VARCHAR");
            sql += " and numero = " + Db.pc(numero_originale, "NUMBER");
            sql += " and anno = " + Db.pc(String.valueOf(anno_originale), "VARCHAR");
            sql += " and username = '" + main.login + "'";
            Db.executeSqlDialogExc(sql, true);

            //aggiorno scadenze
            if (!acquisto) {
                sql = "update scadenze";
                sql += " set documento_numero = " + Db.pc(numero_originale, "NUMBER");
                sql += " , documento_anno = " + Db.pc(anno_originale, "NUMBER");
                sql += " where documento_serie " + Db.pcW(ordine.serie, "VARCHAR");
                sql += " and documento_numero " + Db.pcW(ordine.numero, "NUMBER");
                sql += " and documento_anno " + Db.pcW(String.valueOf(ordine.anno), "VARCHAR");
                sql += " and documento_tipo " + Db.pcW(Db.TIPO_DOCUMENTO_ORDINE, "VARCHAR");
                Db.executeSql(sql);
            }
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
        
        if (controlloCampi() == true) {
            saveDocumento();
            main.events.fireInvoicexEvent(new InvoicexEvent(this, InvoicexEvent.TYPE_SAVE));
            this.dispose();
        }

        if (from != null) {
            this.from.dbRefresh();
        }
    }//GEN-LAST:event_butSaveActionPerformed

private void butNuovArti1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butNuovArti1ActionPerformed
    doc.setPrezziIvati(prezzi_ivati.isSelected());
    doc.setSconto(Db.getDouble(texSconto.getText()));
    doc.calcolaTotali();
    System.out.println("peso:" + doc.totalePeso);

    String dbSerie = this.ordine.serie;
    int dbNumero = this.ordine.numero;
    int dbAnno = this.ordine.anno;
    int riga = 0;

    //apre il resultset per ultimo +1
    Statement stat = null;
    ResultSet resu = null;
    try {
        stat = Db.getConn().createStatement();
        String sql = "select riga from righ_ordi" + suff;
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
        }
    }

    sql = "insert into righ_ordi" + suff + " (serie, numero, anno, riga, codice_articolo, descrizione, id_padre) values (";
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

private void texStatoOrdineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texStatoOrdineActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_texStatoOrdineActionPerformed

private void texTipoOrdineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texTipoOrdineActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_texTipoOrdineActionPerformed

private void texNumeOrdineFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texNumeOrdineFocusGained

    old_id = texNumeOrdine.getText();
    id_modificato = false;

}//GEN-LAST:event_texNumeOrdineFocusGained

private void texNumeOrdineFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texNumeOrdineFocusLost
    texNumeOrdine.setText(texNumeOrdine.getText().replaceAll("[^\\d.]", ""));
    if (!old_id.equals(texNumeOrdine.getText())) {
        //controllo che se è un numero già presente non glielo facci ofare percè altrimenti sovrascrive una altra fattura
        sql = "select numero from test_ordi" + suff;
        sql += " where serie " + Db.pcW(this.ordine.serie, "VARCHAR");
        sql += " and numero " + Db.pcW(texNumeOrdine.getText(), "NUMBER");
        sql += " and anno " + Db.pcW(String.valueOf(this.ordine.anno), "VARCHAR");
        ResultSet r = Db.openResultSet(sql);
        try {
            if (r.next()) {
                texNumeOrdine.setText(old_id);
                JOptionPane.showMessageDialog(this, "Non puoi mettere il numero di un documento già presente !", "Attenzione", JOptionPane.INFORMATION_MESSAGE);
                return;
            } else {
                //associo al nuovo numero
                ordine.numero = new Integer(this.texNumeOrdine.getText()).intValue();

                sql = "update righ_ordi" + suff;
                sql += " set numero = " + Db.pc(ordine.numero, "NUMBER");
                sql += " where serie " + Db.pcW(this.ordine.serie, "VARCHAR");
                sql += " and numero " + Db.pcW(old_id, "NUMBER");
                sql += " and anno " + Db.pcW(String.valueOf(this.ordine.anno), "VARCHAR");
                Db.executeSql(sql);

                sql = "update test_ordi" + suff;
                sql += " set numero = " + Db.pc(ordine.numero, "NUMBER");
                sql += " where serie " + Db.pcW(this.ordine.serie, "VARCHAR");
                sql += " and numero " + Db.pcW(old_id, "NUMBER");
                sql += " and anno " + Db.pcW(String.valueOf(this.ordine.anno), "VARCHAR");
                Db.executeSql(sql);

                //aggiorno scadenze
                sql = "update scadenze";
                sql += " set documento_numero = " + Db.pc(ordine.numero, "NUMBER");
                sql += " where documento_serie " + Db.pcW(this.ordine.serie, "VARCHAR");
                sql += " and documento_numero " + Db.pcW(old_id, "NUMBER");
                sql += " and documento_anno " + Db.pcW(String.valueOf(this.ordine.anno), "VARCHAR");
                sql += " and documento_tipo " + Db.pcW(Db.TIPO_DOCUMENTO_ORDINE, "VARCHAR");
                Db.executeSql(sql);

//                dati.dbChiaveValori.clear();
//                dati.dbChiaveValori.put("serie", ordine.serie);
//                dati.dbChiaveValori.put("numero", ordine.numero);
//                dati.dbChiaveValori.put("anno", ordine.anno);

                //riassocio
                dbAssociaGrigliaRighe();
                id_modificato = true;

                ordine.numero = Integer.parseInt(texNumeOrdine.getText());
                doc.load(Db.INSTANCE, this.ordine.numero, this.ordine.serie, this.ordine.anno, acquisto ? Db.TIPO_DOCUMENTO_ORDINE_ACQUISTO : Db.TIPO_DOCUMENTO_ORDINE, id);
                ricalcolaTotali();

            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

}//GEN-LAST:event_texNumeOrdineFocusLost

private void texDataFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texDataFocusGained

    old_anno = getAnno();
    old_data = texData.getText();
    old_id = texNumeOrdine.getText();
    anno_modificato = false;

}//GEN-LAST:event_texDataFocusGained

private void texBancIbanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texBancIbanActionPerformed
}//GEN-LAST:event_texBancIbanActionPerformed

private void texBancIbanFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texBancIbanFocusLost
}//GEN-LAST:event_texBancIbanFocusLost

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

//    if (multiriga == false) {
//
//        frmNuovRiga frm = new frmNuovRiga(this, this.dati.DB_INSERIMENTO, this.texSeri.getText(), Integer.valueOf(this.texNumeOrdine.getText()).intValue(), "P", value, Integer.valueOf(this.texAnno.getText()).intValue(), codiceListino, codiceCliente);
//        temp.openFrame(frm, 600, 350, 100, 100);
//        frm.setStato();
//    } else {
    try {
        frmNuovRigaDescrizioneMultiRigaNew frm = new frmNuovRigaDescrizioneMultiRigaNew(this, this.dati.DB_INSERIMENTO, this.texSeri.getText(), Integer.valueOf(this.texNumeOrdine.getText()).intValue(), "P", value, Integer.valueOf(this.texAnno.getText()).intValue(), codiceListino, codiceCliente);
        int w = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRiga_w", "980"));
        int h = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRiga_h", "460"));
        int top = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRiga_top", "100"));
        int left = Integer.parseInt(main.fileIni.getValue("dimensioni", "frmNuovRigaDescrizioneMultiRiga_left", "100"));
        main.getPadre().openFrame(frm, w, h, top, left);
        frm.setStato();
        frm.texProvvigione.setText(texProvvigione.getText());
    } catch (Exception e) {
        e.printStackTrace();
    }
//    }
}//GEN-LAST:event_popGridAddActionPerformed

private void formVetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {//GEN-FIRST:event_formVetoableChange
    if (evt.getPropertyName().equals(IS_CLOSED_PROPERTY)) {
        boolean changed = ((Boolean) evt.getNewValue()).booleanValue();
        if (changed) {
            if (dati.dbCheckModificati() 
                    || (doc.getTotale() != this.totaleIniziale 
                    || !this.pagamentoInizialeGiorno.equals(this.texGiornoPagamento.getText()) 
                    || !this.pagamentoIniziale.equals(this.comPaga.getText()) 
                    || doc.getTotale_da_pagare() != this.totaleDaPagareIniziale)) {
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
//            if (dbStato.equalsIgnoreCase(this.DB_INSERIMENTO)) {
            butUndoActionPerformed(null);
//            }
        }

    }
}//GEN-LAST:event_formVetoableChange

private void texForniFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texForniFocusLost
    //this.texForni1.setText(texForni.getText());
}//GEN-LAST:event_texForniFocusLost

private void comForniItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comForniItemStateChanged
//    if (evt.getStateChange() == ItemEvent.SELECTED) {
//        this.texForni.setText(String.valueOf(comForni.getSelectedKey()));
//        this.texForni1.setText(String.valueOf(comForni.getSelectedKey()));
//    }
}//GEN-LAST:event_comForniItemStateChanged

private void grigliaMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_grigliaMousePressed
    if (evt.isPopupTrigger()) {
        int numRow = this.griglia.getSelectedRow();
        if (numRow != 0) {
            popGridAdd.setText("Inserisci nuova fra riga " + numRow + " e riga " + (numRow + 1));
            popGridAddSub.setEnabled(true);
            popGridAddSub.setText("Inserisci Sub-Totale fra riga " + numRow + " e riga " + (numRow + 1));
        } else {
            popGridAdd.setText("Inserisci nuova riga all'inizio");
            popGridAddSub.setEnabled(false);
            popGridAddSub.setText("Non puoi inserire Sub-Totale come prima riga");
        }
        popGrig.show(griglia, evt.getX(), evt.getY());
    }
}//GEN-LAST:event_grigliaMousePressed

private void grigliaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_grigliaMouseReleased
    if (evt.isPopupTrigger()) {
        int numRow = this.griglia.getSelectedRow();
        if (numRow != 0) {
            popGridAdd.setText("Inserisci nuova fra riga " + numRow + " e riga " + (numRow + 1));
            popGridAddSub.setEnabled(true);
            popGridAddSub.setText("Inserisci Sub-Totale fra riga " + numRow + " e riga " + (numRow + 1));
        } else {
            popGridAdd.setText("Inserisci nuova riga all'inizio");
            popGridAddSub.setEnabled(false);
            popGridAddSub.setText("Non puoi inserire Sub-Totale come prima riga");
        }
        popGrig.show(griglia, evt.getX(), evt.getY());
    }
}//GEN-LAST:event_grigliaMouseReleased

private void butImportRigheActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butImportRigheActionPerformed
    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    JFileChooser fileChoose = new JFileChooser(new File(System.getProperty("user.home") + File.separator + ".invoicex" + File.separator + "Export" + File.separator));
    FileFilter filter1 = new FileFilter() {

        @Override
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
            int numero = Integer.valueOf(this.texNumeOrdine.getText()).intValue();
            int anno = Integer.valueOf(this.texAnno.getText()).intValue();
            int idPadre = 0;
            String sql = "SELECT id FROM test_ordi" + suff + " WHERE serie = '" + serie + "' AND numero = (" + numero + ") AND anno = (" + anno + ")";
            ResultSet testa = Db.openResultSet(sql);
            if (testa.next()) {
                idPadre = testa.getInt("id");
            } else {
                idPadre = -1;
            }
            InvoicexUtil.importCSV(acquisto ? Db.TIPO_DOCUMENTO_ORDINE_ACQUISTO : Db.TIPO_DOCUMENTO_ORDINE, f, serie, numero, anno, idPadre, nomeListino);
            InvoicexUtil.aggiornaTotaliRighe(acquisto ? Db.TIPO_DOCUMENTO_ORDINE_ACQUISTO : Db.TIPO_DOCUMENTO_ORDINE, idPadre, prezzi_ivati_virtual.isSelected());
            griglia.dbRefresh();
            ricalcolaTotali();
            JOptionPane.showMessageDialog(this, "Righe caricate correttamente", "Esecuzione terminata", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_butImportRigheActionPerformed

private void texScon3FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texScon3FocusLost
    ricalcolaTotali();
}//GEN-LAST:event_texScon3FocusLost

private void texSpeseTrasportoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texSpeseTrasportoFocusLost
    ricalcolaTotali();
}//GEN-LAST:event_texSpeseTrasportoFocusLost

private void texSpeseIncassoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texSpeseIncassoFocusLost
    ricalcolaTotali();
}//GEN-LAST:event_texSpeseIncassoFocusLost

private void texProvvigioneFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texProvvigioneFocusGained
    texProvvigione.setName(texProvvigione.getText());
}//GEN-LAST:event_texProvvigioneFocusGained

private void texProvvigioneFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texProvvigioneFocusLost
    if (!StringUtils.equals(texProvvigione.getName(), texProvvigione.getText()) && griglia.getRowCount() > 0) {
        aggiornareProvvigioni();
    }
}//GEN-LAST:event_texProvvigioneFocusLost

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
                int dbIdPadre = (Integer) DbUtils.getObject(Db.getConn(), "SELECT id_padre FROM righ_ordi" + suff + " WHERE id = " + Db.pc(dbId, Types.INTEGER));
                sql = "SELECT MAX(riga) as maxnum FROM righ_ordi" + suff + " WHERE id_padre = " + Db.pc(dbIdPadre, Types.INTEGER);

                ResultSet tempUltimo = Db.openResultSet(sql);
                if (tempUltimo.next() == true) {
                    newNumero = tempUltimo.getInt("maxnum") + 1;
                }
            } catch (Exception err) {
                err.printStackTrace();
            }

            sql = "select * from righ_ordi" + suff + " where id = " + Db.pc(dbId, Types.INTEGER);
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
                    sql = "insert into righ_ordi" + suff + " ";
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

private void foglioTipoAMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_foglioTipoAMouseClicked
}//GEN-LAST:event_foglioTipoAMouseClicked

private void foglioTipoAMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_foglioTipoAMousePressed
    if (evt.isPopupTrigger()) {
        popFoglio.show(foglioTipoA, evt.getX(), evt.getY());
    }
}//GEN-LAST:event_foglioTipoAMousePressed

private void foglioTipoAMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_foglioTipoAMouseReleased
    if (evt.isPopupTrigger()) {
        popFoglio.show(foglioTipoA, evt.getX(), evt.getY());
    }
}//GEN-LAST:event_foglioTipoAMouseReleased

private void panGrigliaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_panGrigliaFocusGained
    // TODO add your handling code here:
}//GEN-LAST:event_panGrigliaFocusGained

private void panFoglioRigheSNJAFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_panFoglioRigheSNJAFocusGained
}//GEN-LAST:event_panFoglioRigheSNJAFocusGained

private void popFoglioEliminaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popFoglioEliminaActionPerformed

    DefaultTableModel tableModel = (DefaultTableModel) foglioTipoA.getModel();
    tableModel.removeRow(foglioTipoA.getSelectedRow());
}//GEN-LAST:event_popFoglioEliminaActionPerformed

private void foglioTipoBMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_foglioTipoBMouseClicked
    // TODO add your handling code here:
}//GEN-LAST:event_foglioTipoBMouseClicked

private void foglioTipoBMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_foglioTipoBMousePressed
    // TODO add your handling code here:
}//GEN-LAST:event_foglioTipoBMousePressed

private void foglioTipoBMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_foglioTipoBMouseReleased
    // TODO add your handling code here:
}//GEN-LAST:event_foglioTipoBMouseReleased

private void panGriglia1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_panGriglia1FocusGained
    // TODO add your handling code here:
}//GEN-LAST:event_panGriglia1FocusGained

private void panFoglioRigheSNJBFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_panFoglioRigheSNJBFocusGained
    // TODO add your handling code here:
}//GEN-LAST:event_panFoglioRigheSNJBFocusGained

private void panTabStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_panTabStateChanged
    if (StringUtils.isEmpty(tipoSNJ) || acquisto) {
        return;
    }
    
    if (panTab.getSelectedIndex() == 1) {

        if (tipoSNJ != null && tipoSNJ.equals("A")) {
            //visualizzo il pannellino per lengthdescrizioni multiriga
            zoomA.setVisible(true);

            //associo il nuovo foglio
            ResultSet resu = Db.openResultSet(sqlGrigliaA);
            loadingFoglio = true;

            int rowCount = 0;

            try {
                while (resu.next()) {
                    foglioDataA.setValueAt(resu.getString(1), rowCount, 0);
                    foglioDataA.setValueAt(resu.getString(2), rowCount, 1);
                    
                    foglioDataA.setValueAt(resu.getString(3), rowCount, 2); //descrizione
                    
//                    foglioDataA.setValueAt(FormatUtils.formatNum0_5Dec(resu.getDouble(4)), rowCount, 3); //qta
//                    foglioDataA.setValueAt(Db.formatValuta(resu.getDouble(6)), rowCount, 5); //prezzo
                    foglioDataA.setValueAt(resu.getDouble(4), rowCount, 3); //qta
                    foglioDataA.setValueAt(resu.getDouble(6), rowCount, 5); //prezzo
                    
                    HashMap val = new HashMap();
                    val.put("k", resu.getString(5));
                    val.put("d", resu.getString(5));
                    foglioDataA.setValueAt(val, rowCount, 4);
                    
                    val = new HashMap();
                    val.put("k", resu.getString(7));
                    val.put("d", resu.getString(7));
                    foglioDataA.setValueAt(val, rowCount, 6);
                    val = new HashMap();
                    val.put("k", resu.getString(8));
                    val.put("d", resu.getString(9));
                    foglioDataA.setValueAt(val, rowCount, 7);
                    val = new HashMap();
                    val.put("k", resu.getString(10));
                    val.put("d", resu.getString(11));
                    foglioDataA.setValueAt(val, rowCount, 8);
                    foglioDataA.setValueAt(Db.formatValuta(resu.getDouble(12)), rowCount, 9);
                    //id riga
                    foglioDataA.setValueAt(resu.getInt("id"), rowCount, 10);
                    rowCount++;
                }
            } catch (Exception err) {
                err.printStackTrace();
            }

            loadingFoglio = false;
            ricalcolaTotali();
        } else {
            //visualizzo il pannellino per lengthdescrizioni multiriga
            zoomB.setVisible(true);
            //associo il nuovo foglio
            ResultSet resu = Db.openResultSet(sqlGrigliaB);
            loadingFoglio = true;
            int rowCount = 0;

            try {
                while (resu.next()) {
                    foglioDataB.setValueAt(resu.getString(1), rowCount, 0);
                    foglioDataB.setValueAt(resu.getString(2), rowCount, 1);
                    foglioDataB.setValueAt(resu.getString(3), rowCount, 2);
                    
                    foglioDataB.setValueAt(resu.getObject(4), rowCount, 3);
                    foglioDataB.setValueAt(resu.getObject(5), rowCount, 4);
                    
                    HashMap val = new HashMap();
                    val.put("k", resu.getString(6));
                    val.put("d", resu.getString(7));
                    foglioDataB.setValueAt(val, rowCount, 5);
                    val = new HashMap();
                    val.put("k", resu.getString(8));
                    val.put("d", resu.getString(9));
                    foglioDataB.setValueAt(val, rowCount, 6);
                    val = new HashMap();
                    val.put("k", resu.getString(10));
                    val.put("d", resu.getString(11));
                    foglioDataB.setValueAt(val, rowCount, 7);
                    val = new HashMap();
                    val.put("k", resu.getString(12));
                    val.put("d", resu.getString(13));
                    foglioDataB.setValueAt(val, rowCount, 8);
                    foglioDataB.setValueAt(Db.formatValuta(resu.getDouble(14)), rowCount, 9);
                    
                    //id riga
                    foglioDataB.setValueAt(resu.getInt("id"), rowCount, 10);
                    
                    rowCount++;
                }
            } catch (Exception err) {
                err.printStackTrace();
            }

            loadingFoglio = false;
            ricalcolaTotali();
        }
    }
}//GEN-LAST:event_panTabStateChanged

private void prezzi_ivati_virtualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prezzi_ivati_virtualActionPerformed
    prezzi_ivati.setSelected(prezzi_ivati_virtual.isSelected());
    InvoicexUtil.aggiornaTotaliRighe(Db.TIPO_DOCUMENTO_FATTURA, this.id, prezzi_ivati_virtual.isSelected());    
    ricalcolaTotali();
    ricalcolaSubTotaliOrdine();
    dbAssociaGrigliaRighe();
}//GEN-LAST:event_prezzi_ivati_virtualActionPerformed

private void butNuovArti2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butNuovArti2ActionPerformed
    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    int id_padre = 0;
    try {
        id_padre = (Integer) griglia.getValueAt(griglia.getSelectedRow(), griglia.getColumnByName("id_padre"));
    } catch (Exception e) {
        return;
    }

    int riga = 1;
    double subTotale = 0d;
    try {
        riga = CastUtils.toInteger(DbUtils.getObject(Db.getConn(), "SELECT MAX(riga) + 1 FROM righ_ordi WHERE id_padre = " + id_padre));
    } catch (Exception ex) {
        ex.printStackTrace();
    }

    try {
        String query = "INSERT INTO righ_ordi SET ";
        query += "serie = " + Db.pc(texSeri.getText(), Types.VARCHAR) + ", ";
        query += "numero = " + Db.pc(texNumeOrdine.getText(), Types.INTEGER) + ", ";
        query += "anno = " + Db.pc(texAnno.getText(), Types.INTEGER) + ", ";
        query += "riga = " + Db.pc(riga, Types.INTEGER) + ", ";
        query += "codice_articolo = " + Db.pc(dbOrdine.CODICE_SUBTOTALE, Types.VARCHAR) + ", ";
        query += "descrizione = " + Db.pc(dbOrdine.DESCRIZIONE_SUBTOTALE + " " + FormatUtils.formatEuroIta(subTotale), Types.VARCHAR) + ", ";
        query += "quantita = " + Db.pc(1, Types.DECIMAL) + ", ";
        query += "prezzo = " + Db.pc(0, Types.DECIMAL) + ", ";
        query += "iva = " + Db.pc("", Types.VARCHAR) + ", ";
        query += "sconto1 = " + Db.pc(0, Types.DECIMAL) + ", ";
        query += "sconto2 = " + Db.pc(0, Types.DECIMAL) + ", ";
        query += "is_descrizione = " + Db.pc("S", Types.VARCHAR) + ", ";
        query += "id_padre = " + Db.pc(id_padre, Types.INTEGER) + ", ";
        query += "totale_ivato = " + Db.pc(0, Types.DECIMAL) + ", ";
        query += "totale_imponibile = " + Db.pc(0, Types.DECIMAL);
        Db.executeSql(query);
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    ricalcolaTotali();
    ricalcolaSubTotaliOrdine();
    dbAssociaGrigliaRighe();
    this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

}//GEN-LAST:event_butNuovArti2ActionPerformed

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

private void popGridAddSubActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popGridAddSubActionPerformed
    int numCol = griglia.getColumnByName("riga");
    int numRiga = griglia.getSelectedRow();    
    int riga = (Integer) griglia.getValueAt(numRiga, numCol);
    int id_padre = (Integer) griglia.getValueAt(numRiga, griglia.getColumnByName("id_padre"));

    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    //sposto le righe sotto
    try {
        String sql = "update righ_ordi" + suff + " set riga = riga+1 where riga >= " + riga;
        sql += " and id_padre = " + id_padre;
        sql += " order by riga DESC";
        Db.executeSql(sql);

        //inserisco la riga
        String query = "INSERT INTO righ_ordi" + suff + " SET ";
        query += "serie = " + Db.pc(texSeri.getText(), Types.VARCHAR) + ", ";
        query += "numero = " + Db.pc(texNumeOrdine.getText(), Types.INTEGER) + ", ";
        query += "anno = " + Db.pc(texAnno.getText(), Types.INTEGER) + ", ";
        query += "riga = " + Db.pc(riga, Types.INTEGER) + ", ";
        query += "codice_articolo = " + Db.pc(dbOrdine.CODICE_SUBTOTALE, Types.VARCHAR) + ", ";
        query += "descrizione = '...',";
        query += "quantita = " + Db.pc(1, Types.DECIMAL) + ", ";
        query += "prezzo = " + Db.pc(0, Types.DECIMAL) + ", ";
        query += "iva = " + Db.pc("", Types.VARCHAR) + ", ";
        query += "sconto1 = " + Db.pc(0, Types.DECIMAL) + ", ";
        query += "sconto2 = " + Db.pc(0, Types.DECIMAL) + ", ";
        query += "is_descrizione = " + Db.pc("S", Types.VARCHAR) + ", ";
        query += "id_padre = " + Db.pc(id_padre, Types.INTEGER) + ", ";
        query += "totale_ivato = " + Db.pc(0, Types.DECIMAL) + ", ";
        query += "totale_imponibile = " + Db.pc(0, Types.DECIMAL);

        Db.executeSql(query);
    } catch (Exception e) {
        e.printStackTrace();
    }

    ricalcolaTotali();
    ricalcolaSubTotaliOrdine();
    dbAssociaGrigliaRighe();
    
    this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    
}//GEN-LAST:event_popGridAddSubActionPerformed

private void butImportRigheProskinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butImportRigheProskinActionPerformed
    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    //new File(System.getProperty("user.home") + File.separator + ".invoicex" + File.separator + "Export" + File.separator)
    String pathprec = main.fileIni.getValue("proskin", "path_prec", null);
    JFileChooser fileChoose = new JFileChooser(pathprec);
    FileFilter filter1 = new FileFilter() {

        @Override
        public boolean accept(File pathname) {
            if (pathname.getAbsolutePath().endsWith(".xls")) {
                return true;
            } else if (pathname.isDirectory()) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public String getDescription() {
            return "File Excel (*.xls)";
        }
    };

    fileChoose.addChoosableFileFilter(filter1);
    fileChoose.setFileSelectionMode(JFileChooser.FILES_ONLY);

    int ret = fileChoose.showOpenDialog(this);

    if (ret == javax.swing.JFileChooser.APPROVE_OPTION) {
        try {
            //apro il file
            File f = fileChoose.getSelectedFile();
            try {
                main.fileIni.setValue("proskin", "path_prec", f.getParent());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            String serie = texSeri.getText();
            int numero = Integer.valueOf(this.texNumeOrdine.getText()).intValue();
            int anno = Integer.valueOf(this.texAnno.getText()).intValue();
            int idPadre = 0;
            String sql = "SELECT id FROM test_ordi" + suff + " WHERE serie = '" + serie + "' AND numero = (" + numero + ") AND anno = (" + anno + ")";
            ResultSet testa = Db.openResultSet(sql);
            if (testa.next()) {
                idPadre = testa.getInt("id");
            } else {
                idPadre = 1;
            }
            InvoicexUtil.importXls(acquisto ? Db.TIPO_DOCUMENTO_ORDINE_ACQUISTO : Db.TIPO_DOCUMENTO_ORDINE, f, serie, numero, anno, idPadre);
            griglia.dbRefresh();
            ricalcolaTotali();
            JOptionPane.showMessageDialog(this, "Righe caricate correttamente", "Esecuzione terminata", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_butImportRigheProskinActionPerformed

    private void consegna_previstaPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_consegna_previstaPropertyChange
        if (!loading && evt.getPropertyName().equals("date")) {
            if (griglia.getRowCount() > 0) {
                int ret = JOptionPane.showInternalConfirmDialog(this, "Vuoi impostare la nuova scadenza del '" + DateUtils.formatDateIta(cu.toDate(evt.getNewValue())) + "' in tutte le righe del documento ?", "Attenzione", JOptionPane.YES_NO_OPTION);
                if (ret == JOptionPane.YES_OPTION) {
                    System.out.println("imposto scadenza");
                    int id = acquisto ? InvoicexUtil.getIdOrdineAcquisto(ordine.serie, ordine.numero, ordine.anno) : InvoicexUtil.getIdOrdine(ordine.serie, ordine.numero, ordine.anno);
                    String sql = "update righ_ordi" + suff + " set data_consegna_prevista = " + Db.pc(cu.toDate(evt.getNewValue()), Types.DATE) + " where id_padre = " + id;
                    try {
                        DbUtils.tryExecQuery(Db.getConn(), sql);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }                
                }
            }
            dbAssociaGrigliaRighe();
        }
    }//GEN-LAST:event_consegna_previstaPropertyChange

    private void comConsegnaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comConsegnaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comConsegnaActionPerformed

    private void comScaricoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comScaricoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comScaricoActionPerformed

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

    private void butPdfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butPdfActionPerformed
        butStampaActionPerformed(new ActionEvent(this, 0, "pdf"));
    }//GEN-LAST:event_butPdfActionPerformed

    private void texSwiftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texSwiftActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_texSwiftActionPerformed

    private void texSwiftFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texSwiftFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_texSwiftFocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton apriclienti;
    private javax.swing.JButton butAddClie;
    private javax.swing.JButton butCoor;
    private javax.swing.JButton butImportRighe;
    private javax.swing.JButton butImportRigheProskin;
    private javax.swing.JButton butNuovArti;
    private javax.swing.JButton butNuovArti1;
    private javax.swing.JButton butNuovArti2;
    private javax.swing.JButton butPdf;
    private javax.swing.JButton butPrezziPrec;
    private javax.swing.JButton butSave;
    private javax.swing.JButton butStampa;
    public javax.swing.JButton butUndo;
    private tnxbeans.tnxCheckBox cheOpzioneRibaDestDiversa;
    private tnxbeans.tnxComboField comAgente;
    private tnxbeans.tnxComboField comAspettoEsterioreBeni;
    private tnxbeans.tnxComboField comCausaleTrasporto;
    private tnxbeans.tnxComboField comClie;
    private tnxbeans.tnxComboField comClieDest;
    private tnxbeans.tnxComboField comConsegna;
    private tnxbeans.tnxComboField comForni;
    private tnxbeans.tnxComboField comMezzoTrasporto;
    private tnxbeans.tnxComboField comPaese;
    private tnxbeans.tnxComboField comPaga;
    private tnxbeans.tnxComboField comPorto;
    private tnxbeans.tnxComboField comScarico;
    public tnxbeans.tnxComboField comStatoOrdine;
    private tnxbeans.tnxComboField comVettori;
    private org.jdesktop.swingx.JXDatePicker consegna_prevista;
    private tnxbeans.tnxDbPanel dati;
    private tnxbeans.tnxDbPanel datiRighe;
    private tnxbeans.tnxDbPanel dati_altri1;
    private tnxbeans.tnxDbPanel dati_altri2;
    public it.tnx.gui.JTableSs foglioTipoA;
    private it.tnx.gui.JTableSs foglioTipoB;
    public tnxbeans.tnxDbGrid griglia;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel113;
    private javax.swing.JLabel jLabel114;
    private javax.swing.JLabel jLabel115;
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
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel labAgente;
    private javax.swing.JLabel labBancAbi;
    private javax.swing.JLabel labBancCab;
    private javax.swing.JLabel labFa1;
    private javax.swing.JLabel labFa2;
    private javax.swing.JLabel labFa3;
    private javax.swing.JLabel labFa4;
    private javax.swing.JLabel labFa5;
    private javax.swing.JLabel labFa6;
    private javax.swing.JLabel labFaTitolo;
    private javax.swing.JLabel labGiornoPagamento;
    private javax.swing.JLabel labModConsegna;
    private javax.swing.JLabel labModScarico;
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
    public javax.swing.JLabel labStatus;
    public javax.swing.JLabel labStatus1;
    private javax.swing.JPanel panDati;
    private javax.swing.JPanel panFoglioRigheSNJA;
    private javax.swing.JPanel panFoglioRigheSNJB;
    private javax.swing.JPanel panGriglia;
    private javax.swing.JPanel panGriglia1;
    private javax.swing.JTabbedPane panTab;
    private javax.swing.JPanel panTotale;
    private javax.swing.JPanel panTotale1;
    private javax.swing.JMenuItem popDuplicaRighe;
    private javax.swing.JPopupMenu popFoglio;
    private javax.swing.JMenuItem popFoglioElimina;
    private javax.swing.JMenuItem popGridAdd;
    private javax.swing.JMenuItem popGridAddSub;
    private javax.swing.JPopupMenu popGrig;
    private javax.swing.JMenuItem popGrigElim;
    private javax.swing.JMenuItem popGrigModi;
    public tnxbeans.tnxCheckBox prezzi_ivati;
    private javax.swing.JCheckBox prezzi_ivati_virtual;
    private javax.swing.JSplitPane split;
    private javax.swing.JComboBox stato_evasione;
    public tnxbeans.tnxTextField texAnno;
    private tnxbeans.tnxTextField texBancAbi;
    private tnxbeans.tnxTextField texBancCab;
    private tnxbeans.tnxTextField texBancIban;
    public tnxbeans.tnxTextField texClie;
    private tnxbeans.tnxTextField texClieDest;
    public javax.swing.JTextField texCliente;
    private tnxbeans.tnxTextField texConsegna;
    private tnxbeans.tnxTextField texData;
    private tnxbeans.tnxTextField texDestCap;
    private tnxbeans.tnxTextField texDestCellulare;
    private tnxbeans.tnxTextField texDestIndirizzo;
    private tnxbeans.tnxTextField texDestLocalita;
    private tnxbeans.tnxTextField texDestProvincia;
    private tnxbeans.tnxTextField texDestRagioneSociale;
    private tnxbeans.tnxTextField texDestTelefono;
    private tnxbeans.tnxTextField texForni;
    public tnxbeans.tnxTextField texGiornoPagamento;
    private tnxbeans.tnxMemoField texNote;
    private tnxbeans.tnxTextField texNotePagamento;
    public tnxbeans.tnxTextField texNumeOrdine;
    private tnxbeans.tnxTextField texNumeroColli;
    private tnxbeans.tnxTextField texPaga2;
    private tnxbeans.tnxTextField texProvvigione;
    public tnxbeans.tnxTextField texScon1;
    public tnxbeans.tnxTextField texScon2;
    public tnxbeans.tnxTextField texScon3;
    public tnxbeans.tnxTextField texSconto;
    public tnxbeans.tnxTextField texSeri;
    public tnxbeans.tnxTextField texSpeseIncasso;
    public tnxbeans.tnxTextField texSpeseTrasporto;
    private tnxbeans.tnxTextField texStat;
    private tnxbeans.tnxTextField texStatoOrdine;
    private tnxbeans.tnxTextField texSwift;
    private tnxbeans.tnxTextField texTipoOrdine;
    public tnxbeans.tnxTextField texTota;
    private tnxbeans.tnxTextField texTota1;
    public tnxbeans.tnxTextField texTotaImpo;
    private tnxbeans.tnxTextField texTotaImpo1;
    public tnxbeans.tnxTextField texTotaIva;
    private tnxbeans.tnxTextField texTotaIva1;
    private tnxbeans.tnxTextField textTipoSnj;
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
        campi += "REPLACE(REPLACE(REPLACE(FORMAT(quantita_evasa,2),'.','X'),',','.'),'X',',') AS '" + getCampoQtaEvasa() + "',";
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
        if (visConsegnaPrevista()) {
            campi += ", data_consegna_prevista as consegna";
        }
        if (main.isPluginContabilitaAttivo()) {
            campi += ", conto";
        }

        String sql =
                //" and anno = " + java.util.Calendar.getInstance().get(Calendar.YEAR) +
                //"select " + campi + " from righ_ordi" + " where serie = " + db.pc(this.ordine.serie, "VARCHAR") + " and numero = " + this.ordine.numero + " and stato = " + db.pc(this.ordine.stato, "VARCHAR") + " and anno = " + db.pc(this.ordine.anno, "INTEGER") + " order by riga";
                //"select " + campi + " from righ_ordi" + suff + " where serie = " + db.pc(this.ordine.serie, "VARCHAR") + " and numero = " + this.ordine.numero + " and anno = " + db.pc(this.ordine.anno, "INTEGER") + " order by riga";
                "select " + campi + " from righ_ordi" + suff + " where id_padre = " + id + " order by riga";

        //debug
        //javax.swing.JOptionPane.showMessageDialog(null,sql);
        System.out.println("sql associa griglia:" + sql);

        this.sqlGrigliaA = "SELECT riga, codice_articolo, rig.descrizione, quantita, um, prezzo, percentuale, tef.id as tef_id, tef.descrizione as emissione_fattura, pag.codice as id_pag, pag.descrizione as termini_pagamento, totale_imponibile, rig.id ";
        this.sqlGrigliaA += "FROM righ_ordi" + suff + " rig LEFT JOIN tipi_emissione_fattura tef ON rig.emissione_fattura = tef.id LEFT JOIN pagamenti pag ON rig.termini_pagamento = pag.codice";
        this.sqlGrigliaA += " where serie = " + db.pc(this.ordine.serie, "VARCHAR") + " and numero = " + this.ordine.numero + " and anno = " + db.pc(this.ordine.anno, "INTEGER") + " order by riga";

        this.sqlGrigliaB = "SELECT riga, codice_articolo, rig.descrizione, costo_giornaliero, costo_mensile, con.id as con_id, con.descrizione as durata_consulenza, cnt.id as cnt_id, cnt.descrizione as durata_contratto, tef.id as tef_id, tef.descrizione as emissione_fattura, pag.codice as id_pag, pag.descrizione as termini_pagamento, totale_imponibile, rig.id ";
        this.sqlGrigliaB += "FROM righ_ordi" + suff + " rig LEFT JOIN tipi_emissione_fattura tef ON rig.emissione_fattura = tef.id LEFT JOIN pagamenti pag ON rig.termini_pagamento = pag.codice LEFT JOIN tipi_durata_consulenza con ON rig.durata_consulenza = con.id LEFT JOIN tipi_durata_contratto cnt ON rig.durata_contratto = cnt.id";
        this.sqlGrigliaB += " where serie = " + db.pc(this.ordine.serie, "VARCHAR") + " and numero = " + this.ordine.numero + " and anno = " + db.pc(this.ordine.anno, "INTEGER") + " order by riga";
        
        griglia.colonneEditabiliByName = new String[]{getCampoQtaEvasa()};
        griglia.dbEditabile = true;

        
        //dimensioni colonne
        java.util.Hashtable colsWidthPerc = new java.util.Hashtable();
        colsWidthPerc.put("serie", new Double(0));
        colsWidthPerc.put("numero", new Double(0));
        colsWidthPerc.put("anno", new Double(0));
        colsWidthPerc.put("stato", new Double(0));
        colsWidthPerc.put("riga", new Double(5));
        colsWidthPerc.put("articolo", new Double(15));
        colsWidthPerc.put("descrizione", new Double(40));
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
        if (visConsegnaPrevista()) {
            colsWidthPerc.put("consegna", new Double(10));
        }
        if (main.isPluginContabilitaAttivo()) {
            colsWidthPerc.put("conto", new Double(10));
        }
        
        griglia.columnsSizePerc = colsWidthPerc;
        
        griglia.dbOpen(db.getConn(), sql);
        griglia.getColumn("quantita").setCellRenderer(InvoicexUtil.getNumber0_5Renderer());
        griglia.getColumn(getCampoQtaEvasa()).setCellRenderer(InvoicexUtil.getNumber0_5Renderer());
        
        if (visConsegnaPrevista()) {
            griglia.getColumn("consegna").setCellRenderer(new RendererUtils.DateRenderer(new SimpleDateFormat("dd/MM/yy")));
        }
        
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

        try {
            if (this.texClie.getText().length() > 0) {
                this.ordine.forceCliente(Long.parseLong(this.texClie.getText()));
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

                if (Db.nz(tempClie.getString("banca_abi"), "").length() > 0) {
                    this.texBancAbi.setText(Db.nz(tempClie.getString("banca_abi"), ""));
                }

                if (Db.nz(tempClie.getString("banca_cab"), "").length() > 0) {
                    this.texBancCab.setText(Db.nz(tempClie.getString("banca_cab"), ""));
                }

                if (Db.nz(tempClie.getString("banca_cc_iban"), "").length() > 0) {
                    this.texBancIban.setText(Db.nz(tempClie.getString("banca_cc_iban"), ""));
                }

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

                //note automatiche e altro
                String sqlTmp = "SELECT note, note_automatiche as auto, modalita_consegna, modalita_scarico FROM clie_forn where codice = " + Db.pc(String.valueOf(comClie.getSelectedKey()), "NUMERIC");
                ResultSet res = Db.openResultSet(sqlTmp);
                try {
                    if (res.next()) {
                        String auto = res.getString("auto");
                        String nota = res.getString("note");
                        if (auto != null && auto.equals("S")) {
                            if (!texNote.getText().equals("") && !texNote.getText().equals(nota)) {
                                this.texNote.setText(nota);
                            } else {
                                this.texNote.setText(res.getString("note"));
                            }
                        }
                        
                        //consegna e scarico
                        try {
                            comConsegna.dbTrovaKey(res.getObject("modalita_consegna"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            comScarico.dbTrovaKey(res.getObject("modalita_scarico"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                InvoicexUtil.fireEvent(this, InvoicexEvent.TYPE_FRMTESTORDI_CARICA_DATI_CLIENTE, tempClie);
            } else {
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
        ricalcolaTotali();
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

        try {
            if (tempClie.next() != true) {
                texCliente.requestFocus();
                javax.swing.JOptionPane.showMessageDialog(this, "Il codice " + ccliente + " specificato non esiste in anagrafica !");
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
        }

        //controllo tipo pagamento
        try {
            ResultSet r = Db.lookUp(String.valueOf(comPaga.getSelectedKey()), "codice", "pagamenti");
            if (Db.nz(r.getString("flag_richiedi_giorno"), "N").equalsIgnoreCase("S")) {
                //deve essere specificato il giorno e deve essere fra 1 e 28
                int i = Integer.parseInt(texGiornoPagamento.getText());

                if (i < 1 || i > 28) {
                    javax.swing.JOptionPane.showMessageDialog(this, "E' obbligatorio specificare il giorno del mese per questo tipo di pagamento" + "\nDeve essere compreso fra 1 e 28", "Attenzione", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    return false;
                }
            }
        } catch (Exception err) {
            javax.swing.JOptionPane.showMessageDialog(this, "E' obbligatorio specificare il giorno del mese per questo tipo di pagamento" + "\nDeve essere compreso fra 1 e 28" + "\n" + err.toString(), "Attenzione", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            err.printStackTrace();

            return false;
        }

        return true;
    }

    private void showPrezziFatture() {

        frmPrezziFatturePrecedenti form = new frmPrezziFatturePrecedenti(Integer.parseInt(this.texClie.getText().toString()), null, Db.TIPO_DOCUMENTO_DDT);
        main.getPadre().openFrame(form, 450, 500, this.getY() + 50, this.getX() + this.getWidth() - 200);
    }

    public void confermaAnimale(Animale animale) {
        System.out.println("aggiugnere:" + animale);
        animale.saveAnimale();
        this.griglia.dbRefresh();
    }

    public void ricalcolaTotali() {
        try {

            //this.parent.ordine.dbRefresh();
            //provo con nuova classe Documento
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
//            presconto.setText(it.tnx.Util.formatValutaEuro(doc.totaleImponibilePreSconto));
//            prescontoivato.setText(it.tnx.Util.formatValutaEuro(doc.totaleIvatoPreSconto));
//            debugiva.setText(doc.dumpCastellettoIva());
        } catch (Exception err) {
            err.printStackTrace();
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

    synchronized private void riempiDestDiversa(String sql) {
        boolean oldrefresh = dati.isRefreshing;
        dati.isRefreshing = true;
        comClieDest.setRinominaDuplicati(true);
        comClieDest.dbClearList();
        comClieDest.dbAddElement("", "");
        comClieDest.dbOpenList(Db.getConn(), sql, this.texClieDest.getText(), false);
        dati.isRefreshing = oldrefresh;
    }

    public void aggiornareProvvigioni() {
        block_aggiornareProvvigioni = true;
        if (SwingUtils.showYesNoMessage(this, "Vuoi aggiornare le provvigioni delle righe già inserite alla nuova provvigione ?")) {
            int id = InvoicexUtil.getIdOrdine(ordine.serie, ordine.numero, ordine.anno);
            String sql = "update righ_ordi" + suff + " set provvigione = " + Db.pc2(texProvvigione.getText(), Types.DOUBLE) + " where id_padre = " + id;
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

    private String getStatoEvaso() {
        System.out.println("!!! getStatoEvaso: " + stato_evasione.getSelectedIndex());
        if (stato_evasione.getSelectedIndex() == 0) {
            return "S";
        }
        if (stato_evasione.getSelectedIndex() == 1) {
            return "P";
        } else {
            return "";
        }
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
                String tabr = acquisto ? "righ_ordi_acquisto" : "righ_ordi";
                try {
                    Integer idriga = CastUtils.toInteger(getValueAt(row, getColumnByName("id")));
                    String sql = "update " + tabr + " set quantita_evasa = " + Db.pc(CastUtils.toDouble(aValue), Types.DOUBLE) + " where id = " + idriga;
                    System.err.println("sql = " + sql);
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

    private String getCampoQtaEvasa() {
        return acquisto ? "qta arrivata" : "qta evasa";
    }
    
    private String getTipoDoc() {
        return acquisto ? Db.TIPO_DOCUMENTO_ORDINE_ACQUISTO : Db.TIPO_DOCUMENTO_ORDINE;
    }

    private boolean visConsegnaPrevista() {
        if (consegna_prevista.getDate() != null) {
            return true;
        }
        
        String sql = "SELECT data_consegna_prevista ";
        sql += " FROM righ_ordi" + suff + " rig";
        sql += " where data_consegna_prevista is not null and serie = " + db.pc(ordine.serie, "VARCHAR") + " and numero = " + ordine.numero + " and anno = " + db.pc(ordine.anno, "INTEGER");
        sql += " limit 1";
        try {
            return DbUtils.containRows(Db.getConn(), sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }    

    public tnxDbPanel getDatiPanel() {
        return dati;
    }
    
    public JTabbedPane getTab() {
        return panTab;
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
}
















class DataModelFoglioOrdine extends javax.swing.table.DefaultTableModel {

    frmTestOrdine form;
    int currentRow = -1;

    public DataModelFoglioOrdine(int rowCount, int columnCount, frmTestOrdine form) {
        super(rowCount, columnCount);
        this.form = form;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return super.isCellEditable(row, column);
    }

    @Override
    public void setValueAt(Object obj, int row, int col) {
        super.setValueAt(obj, row, col);
    }

    @Override
    public void removeRow(int param) {
        String sql;

        //cancello la riga
        if (form.texNumeOrdine.getText().length() > 0 && form.texAnno.getText().length() > 0 && getValueAt(param, 0).toString().length() > 0) {
            sql = "delete from righ_ordi where ";
            sql += "serie = " + Db.pc(form.texSeri.getText(), Types.VARCHAR);
            sql += " and numero = " + Db.pc(form.texNumeOrdine.getText(), Types.INTEGER);
            sql += " and anno = " + form.texAnno.getText();
            sql += " and riga = " + Db.pc(getValueAt(param, 0), Types.INTEGER);

            if (Db.executeSql(sql) == true) {
                System.out.println("row count:" + getRowCount() + " row to del:" + param);
                super.removeRow(param);
            }

            form.dbAssociaGrigliaRighe();
        }
    }

    public void recuperaDatiArticolo(String codArt, int row) {
        String codicelistino = "0";

        if (codArt.length() > 0) {
            ResultSet temp;
            String sql = "select * from articoli where codice = " + Db.pc(codArt, "VARCHAR");
            temp = Db.openResultSet(sql);

            try {
                if (temp.next() == true) {
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

                    if (eng) {
                        setValueAt(Db.nz(temp.getString("descrizione_en"), ""), row, 2);
                    } else {
                        setValueAt(Db.nz(temp.getString("descrizione"), ""), row, 2);
                    }

                    if (form.tipoSNJ != null && form.tipoSNJ.equals("A")) {
                        sql = "select prezzo, sconto1, sconto2 from articoli_prezzi";
                        sql += " where articolo = " + Db.pc(codArt, "VARCHAR");
                        sql += " and listino = " + Db.pc(codicelistino, java.sql.Types.VARCHAR);

                        ResultSet prezzi = Db.openResultSet(sql);

                        if (prezzi.next() == true) {
                            setValueAt(Db.formatDecimal5(prezzi.getDouble(1)), row, 5);
                        } else {
                            sql = "select prezzo, sconto1, sconto2 from articoli_prezzi";
                            sql += " where articolo = " + Db.pc(codArt, "VARCHAR");
                            sql += " and listino = " + Db.pc(main.getListinoBase(), java.sql.Types.VARCHAR);
                            prezzi = Db.openResultSet(sql);

                            if (prezzi.next() == true) {
                                setValueAt(Db.formatDecimal5(prezzi.getDouble(1)), row, 5);
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

    public String getDouble(Object valore) {
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

    public String formatDouble(double number) {
        return FormatUtils.formatEuroIta(number);
    }
}

class DataModelFoglioA extends DataModelFoglioOrdine {

    public DataModelFoglioA(int rowCount, int columnCount, frmTestOrdine form) {
        super(rowCount, columnCount, form);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if (column == 0 || column == 9) {
            return false;
        }
        return super.isCellEditable(row, column);
    }

    @Override
    public void setValueAt(Object obj, int row, int col) {
        if (col == 1) {
            super.setValueAt(obj, row, col);
            String codice = String.valueOf(Db.nz(obj, ""));
            if (codice.trim().length() > 0) {
                recuperaDatiArticolo(String.valueOf(obj), row);
            }
        } else if (col == 3 || col == 5) {
//            try {                
//                double val = CastUtils.toDouble0(String.valueOf(obj));
//                super.setValueAt(formatDouble(val), row, col);
//            } catch (Exception e) {
//            } finally {
//                if (!form.loadingFoglio) {
//                    setValueAt(formatDouble(calcolaTotaleRiga(row)), row, 9);
//                }
//            }
            super.setValueAt(obj, row, col);
            if (!form.loadingFoglio) {
                setValueAt(formatDouble(calcolaTotaleRiga(row)), row, 9);
            }
        } else {
            super.setValueAt(obj, row, col);
        }

        if (!form.loadingFoglio && col >= 1 && col <= 9) {
            String sql = "";
            String sqlv = "";
            String sqlc = "";

            String codice = "";
            String desc = "";
            codice = String.valueOf(Db.nz(getValueAt(row, 1), ""));
            desc = String.valueOf(Db.nz(getValueAt(row, 2), ""));

            sql = "SELECT id FROM test_ordi WHERE ";
            sql += "serie = " + Db.pc(form.texSeri.getText(), Types.VARCHAR);
            sql += " and numero = " + form.texNumeOrdine.getText();
            sql += " and anno = " + form.texAnno.getText();
            Integer id = 0;
            try {
                id = (Integer) DbUtils.getObject(Db.getConn(), sql);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (codice.trim().length() > 0 || desc.trim().length() > 0) {
                
                /*
                sql = "delete from righ_ordi where ";
                sql += "serie = " + Db.pc(form.texSeri.getText(), Types.VARCHAR);
                sql += " and numero = " + form.texNumeOrdine.getText();
                sql += " and anno = " + form.texAnno.getText();
                sql += " and riga = " + Db.pc(getValueAt(row, 0), Types.INTEGER);
                Db.executeSql(sql);

                double totale_imponibile = calcolaTotaleRiga(row);
                double totale_iva = (totale_imponibile * 21d) / 100d;
                String val = "";
                HashMap map = null;
                sql = "insert into righ_ordi (";
                sqlc = "serie";
                sqlv = Db.pc(form.texSeri.getText(), Types.VARCHAR);
                sqlc += ", numero";
                sqlv += ", " + form.texNumeOrdine.getText();
                sqlc += ", id_padre";
                sqlv += ", " + Db.pc(id, Types.INTEGER);
                sqlc += ", anno";
                sqlv += ", " + form.texAnno.getText();
                sqlc += ", riga";
                sqlv += ", " + Db.pc(getValueAt(row, 0), Types.INTEGER);
                sqlc += ", codice_articolo";
                sqlv += ", " + Db.pc(getValueAt(row, 1), Types.VARCHAR);
                sqlc += ", descrizione";
                sqlv += ", " + Db.pc(getValueAt(row, 2), Types.VARCHAR);
                sqlc += ", um";
                try {
                    map = (HashMap) getValueAt(row, 4);
                    val = String.valueOf(Db.nz(map.get("d"), "0"));
                } catch (Exception e) {
                    val = "";
                }
                sqlv += ", " + Db.pc(val, Types.VARCHAR);
                sqlc += ", quantita";
                sqlv += ", " + getDouble(Db.nz(getValueAt(row, 3), "").replace('.', ','));
                sqlc += ", prezzo";
                sqlv += ", " + getDouble(getValueAt(row, 5));
                sqlc += ", iva";
                sqlv += ", '21'";
                sqlc += ", sconto1";
                sqlv += ", " + getDouble("0");
                sqlc += ", sconto2";
                sqlv += ", " + getDouble("0");
                sqlc += ", stato";
                sqlv += ", 'P'";
                sqlc += ", percentuale";
                try {
                    map = (HashMap) getValueAt(row, 6);
                    val = String.valueOf(Db.nz(map.get("k"), "0"));
                    val = String.valueOf(val.replace('.', ','));
                    val = val.equals("") ? "0" : val;
                } catch (Exception e) {
                    val = "0";
                }
                sqlv += ", " + Db.pc(Integer.parseInt(val), Types.INTEGER);
                sqlc += ", emissione_fattura";
                try {
                    map = (HashMap) getValueAt(row, 7);
                    val = String.valueOf(Db.nz(map.get("k"), "0"));
                    val = String.valueOf(val.replace('.', ','));
                    val = val.equals("") ? "0" : val;
                } catch (Exception e) {
                    val = "0";
                }

                sqlv += ", " + Db.pc(Integer.parseInt(val), Types.INTEGER);
                sqlc += ", termini_pagamento";
                try {
                    map = (HashMap) getValueAt(row, 8);
                    val = String.valueOf(Db.nz(map.get("k"), "0"));
                } catch (Exception e) {
                    val = "";
                }
                sqlv += ", " + Db.pc(val, Types.VARCHAR);
                sqlc += ", totale_ivato";
                sqlv += ", " + getDouble(Db.nz(totale_imponibile + totale_iva, "0").replace('.', ','));
                sqlc += ", totale_imponibile";
                sqlv += ", " + getDouble(Db.nz(totale_imponibile, "0").replace('.', ','));
                sql = sql + sqlc + ") values (" + sqlv + ")";
                System.out.println("sql update values: " + sql);
                Db.executeSql(sql);
                * */ 
                //invece di eliminare e reinserire faccio update
                int col_id = 10;
                Integer id_riga = CastUtils.toInteger(getValueAt(row, col_id));

                if (id_riga == null) {
                    sql = "insert into righ_ordi set ";
                } else {
                    sql = "update righ_ordi set ";
                }                    
                HashMap c = new HashMap();
                
                double totale_imponibile = calcolaTotaleRiga(row);
                double totale_iva = (totale_imponibile * 21d) / 100d;
                String val = "";
                HashMap map = null;
                if (id_riga == null) {
                    c.put("id_padre", id);
                    c.put("serie", form.texSeri.getText());
                    c.put("numero", form.texNumeOrdine.getText());
                    c.put("anno", form.texAnno.getText());
                    c.put("riga", getValueAt(row, 0));
                    c.put("iva", InvoicexUtil.getIvaDefaultPassaggio());
                    c.put("sconto1", "0");
                    c.put("sconto2", "0");
                }
                c.put("totale_ivato", totale_imponibile + totale_iva);
                c.put("totale_imponibile", totale_imponibile);
                if (col == 1) {
                    c.put("codice_articolo", getValueAt(row, 1));
                } else if (col == 2) {
                    c.put("descrizione", getValueAt(row, 2));
                } else if (col == 3) {
                    c.put("quantita", CastUtils.toDouble(getValueAt(row, 3)));
                } else if (col == 4) {
                    try {
                        map = (HashMap) getValueAt(row, 4);
                        val = String.valueOf(Db.nz(map.get("d"), "0"));
                    } catch (Exception e) {
                        val = "";
                    }
                    c.put("um", val);
                } else if (col == 5) {
                    c.put("prezzo", CastUtils.toDouble(getValueAt(row, 5)));
                } else if (col == 6) {
                    try {
                        map = (HashMap) getValueAt(row, 6);
                        val = String.valueOf(Db.nz(map.get("k"), "0"));
                        val = String.valueOf(val.replace('.', ','));
                        val = val.equals("") ? "0" : val;
                    } catch (Exception e) {
                        val = "0";
                    }
                    c.put("percentuale", val);
                } else if (col == 7) {
                    try {
                        map = (HashMap) getValueAt(row, 7);
                        val = String.valueOf(Db.nz(map.get("k"), "0"));
                        val = String.valueOf(val.replace('.', ','));
                        val = val.equals("") ? "0" : val;
                    } catch (Exception e) {
                        val = "0";
                    }
                    c.put("emissione_fattura", val);
                } else if (col == 8) {
                    try {
                        map = (HashMap) getValueAt(row, 8);
                        val = String.valueOf(Db.nz(map.get("k"), "0"));
                    } catch (Exception e) {
                        val = "";
                    }
                    c.put("termini_pagamento", val);
                }
                
                sql = sql + DbUtils.prepareSqlFromMap(c);
                if (id_riga != null) {
                    sql += " where id = " + id_riga;
                }
                System.out.println("sql foglio a: " + sql);
                try {
                    DbUtils.tryExecQuery(Db.getConn(), sql);
                    //se id_riga nullo prendo il nuovo id e lo metto altrimenti facci oaltre insert
                    if (id_riga == null) {
                        id_riga = CastUtils.toInteger(DbUtils.getObject(Db.getConn(), "select LAST_INSERT_ID()"));
                        if (id_riga == null) {
                            SwingUtils.showErrorMessage(form, "Errore nel recupero di LAST_INSERT_ID");
                        } else {
                            System.out.println("riga inserita: " + id_riga);
                            setValueAt(id_riga, row, col_id);
                        }
                    }                    
                } catch (Exception e) {
                    SwingUtils.showExceptionMessage(form, e);
                }
            } else {
                System.out.println("elimino riga");

                if (form.texNumeOrdine.getText().length() > 0 && form.texAnno.getText().length() > 0 && getValueAt(row, 0).toString().length() > 0) {
                    System.out.println("elimino riga 2");
                    sql = "delete from righ_ordi where ";
                    sql += "serie = " + Db.pc(form.texSeri.getText(), Types.VARCHAR);
                    sql += " and numero = " + form.texNumeOrdine.getText();
                    sql += " and anno = " + form.texAnno.getText();
                    sql += " and riga = " + Db.pc(getValueAt(row, 0), Types.INTEGER);
                    Db.executeSql(sql);
                }
            }

            form.dbAssociaGrigliaRighe();
        }
    }

    private double calcolaTotaleRiga(int row) {
        double totale = 0d;
        double qta = 0d;
        double importo = 0d;

        try {
//            String val = Db.nz(getValueAt(row, 3), "0");
//            qta = CastUtils.toDouble0(val);
            qta = CastUtils.toDouble0(getValueAt(row, 3));
        } catch (Exception e) {
            qta = 0d;
        }
        try {
//            String val = Db.nz(getValueAt(row, 5), "0");
//            importo = CastUtils.toDouble0(val);
            importo = CastUtils.toDouble0(getValueAt(row, 5));
        } catch (Exception e) {
            importo = 0d;
        }

        totale = qta * importo;
        return totale;
    }
}

class DataModelFoglioB extends DataModelFoglioOrdine {

    public DataModelFoglioB(int rowCount, int columnCount, frmTestOrdine form) {
        super(rowCount, columnCount, form);
    }

    private double calcolaTotaleRiga(int row) {
        double totale = 0d;
        double qta = 1d;
        double importo = 0d;

        try {
            importo = CastUtils.toDouble0(getValueAt(row, 3));
            if (importo == 0) {
                importo = CastUtils.toDouble0(getValueAt(row, 4));
            }
        } catch (Exception e) {
            importo = 0d;
        }

        totale = qta * importo;
        return totale;
    }

    @Override
    public void setValueAt(Object obj, int row, int col) {
//        if (obj == null || obj.equals("")) {
//            return;
//        }
        if (col == 1) {
            super.setValueAt(obj, row, col);
            String codice = String.valueOf(Db.nz(obj, ""));
            if (codice.trim().length() > 0) {
                recuperaDatiArticolo(String.valueOf(obj), row);
            }
        } else if (col == 3 || col == 4) {
//            try {
//                double val = CastUtils.toDouble0(String.valueOf(obj));
//                super.setValueAt(formatDouble(val), row, col);
//                if (!form.loadingFoglio) {
//                    if (col == 3) {
//                        super.setValueAt(formatDouble(0d), row, 4);
//                    } else {
//                        super.setValueAt(formatDouble(0d), row, 3);
//                    }
//                }
//            } catch (Exception e) {
//            } finally {
                super.setValueAt(obj, row, col);
                if (!form.loadingFoglio) {
                    setValueAt(formatDouble(calcolaTotaleRiga(row)), row, 9);
                }
//            }
        } else {
            super.setValueAt(obj, row, col);
        }

        if (!form.loadingFoglio && col >= 1 && col <= 9) {
            String sql = "";
            String sqlv = "";
            String sqlc = "";

            String codice = "";
            String desc = "";
            codice = String.valueOf(Db.nz(getValueAt(row, 1), ""));
            desc = String.valueOf(Db.nz(getValueAt(row, 2), ""));

            sql = "SELECT id FROM test_ordi WHERE ";
            sql += "serie = " + Db.pc(form.texSeri.getText(), Types.VARCHAR);
            sql += " and numero = " + form.texNumeOrdine.getText();
            sql += " and anno = " + form.texAnno.getText();
            Integer id = 0;
            try {
                id = (Integer) DbUtils.getObject(Db.getConn(), sql);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (codice.trim().length() > 0 || desc.trim().length() > 0) {
                /*
                sql = "delete from righ_ordi where ";
                sql += "serie = " + Db.pc(form.texSeri.getText(), Types.VARCHAR);
                sql += " and numero = " + form.texNumeOrdine.getText();
                sql += " and anno = " + form.texAnno.getText();
                sql += " and riga = " + Db.pc(getValueAt(row, 0), Types.INTEGER);
                Db.executeSql(sql);

                double totale_imponibile = calcolaTotaleRiga(row);
                double totale_iva = (totale_imponibile * 21d) / 100d;
                String val = "";
                HashMap map = null;
                sql = "insert into righ_ordi (";
                sqlc = "serie";
                sqlv = Db.pc(form.texSeri.getText(), Types.VARCHAR);
                sqlc += ", numero";
                sqlv += ", " + form.texNumeOrdine.getText();
                sqlc += ", id_padre";
                sqlv += ", " + Db.pc(id, Types.INTEGER);
                sqlc += ", anno";
                sqlv += ", " + form.texAnno.getText();
                sqlc += ", riga";
                sqlv += ", " + Db.pc(getValueAt(row, 0), Types.INTEGER);
                sqlc += ", codice_articolo";
                sqlv += ", " + Db.pc(getValueAt(row, 1), Types.VARCHAR);
                sqlc += ", descrizione";
                sqlv += ", " + Db.pc(getValueAt(row, 2), Types.VARCHAR);
                sqlc += ", costo_giornaliero";
                sqlv += ", " + getDouble(getValueAt(row, 3));
                sqlc += ", costo_mensile";
                sqlv += ", " + getDouble(getValueAt(row, 4));
                sqlc += ", durata_consulenza";
                try {
                    map = (HashMap) getValueAt(row, 5);
                    val = String.valueOf(Db.nz(map.get("k"), "0"));
                    val = String.valueOf(val.replace('.', ','));
                    val = val.equals("") ? "0" : val;
                } catch (Exception e) {
                    val = "0";
                }
                sqlv += ", " + Db.pc(Integer.parseInt(val), Types.INTEGER);
                sqlc += ", durata_contratto";
                try {
                    map = (HashMap) getValueAt(row, 6);
                    val = String.valueOf(Db.nz(map.get("k"), "0"));
                    val = String.valueOf(val.replace('.', ','));
                    val = val.equals("") ? "0" : val;
                } catch (Exception e) {
                    val = "0";
                }
                sqlv += ", " + Db.pc(Integer.parseInt(val), Types.INTEGER);
                sqlc += ", quantita";
                sqlv += ", " + getDouble(1);
                sqlc += ", prezzo";
                sqlv += ", " + getDouble(Db.nz(totale_imponibile, "0").replace('.', ','));
                sqlc += ", iva";
                sqlv += ", '21'";
                sqlc += ", sconto1";
                sqlv += ", " + getDouble("0");
                sqlc += ", sconto2";
                sqlv += ", " + getDouble("0");
                sqlc += ", stato";
                sqlv += ", 'P'";
                sqlc += ", emissione_fattura";
                try {
                    map = (HashMap) getValueAt(row, 7);
                    val = String.valueOf(Db.nz(map.get("k"), "0"));
                    val = String.valueOf(val.replace('.', ','));
                    val = val.equals("") ? "0" : val;
                } catch (Exception e) {
                    val = "0";
                }

                sqlv += ", " + Db.pc(Integer.parseInt(val), Types.INTEGER);
                sqlc += ", termini_pagamento";
                try {
                    map = (HashMap) getValueAt(row, 8);
                    val = String.valueOf(Db.nz(map.get("k"), "0"));
                } catch (Exception e) {
                    val = "";
                }
                sqlv += ", " + Db.pc(val, Types.VARCHAR);
                sqlc += ", totale_ivato";
                sqlv += ", " + getDouble(Db.nz(totale_imponibile + totale_iva, "0").replace('.', ','));
                sqlc += ", totale_imponibile";
                sqlv += ", " + getDouble(Db.nz(totale_imponibile, "0").replace('.', ','));
                sql = sql + sqlc + ") values (" + sqlv + ")";
                System.out.println("sql update values: " + sql);
                Db.executeSql(sql);
                */
                
                double totale_imponibile = calcolaTotaleRiga(row);
                double totale_iva = (totale_imponibile * 21d) / 100d;

                int col_id = 10;
                Integer id_riga = CastUtils.toInteger(getValueAt(row, col_id));

                if (id_riga == null) {
                    sql = "insert into righ_ordi set ";
                } else {
                    sql = "update righ_ordi set ";
                }                    
                HashMap c = new HashMap();
                
                String val = "";
                HashMap map = null;
                if (id_riga == null) {
                    c.put("id_padre", id);
                    c.put("serie", form.texSeri.getText());
                    c.put("numero", form.texNumeOrdine.getText());
                    c.put("anno", form.texAnno.getText());
                    c.put("riga", getValueAt(row, 0));
                    c.put("iva", InvoicexUtil.getIvaDefaultPassaggio());
                    c.put("sconto1", "0");
                    c.put("sconto2", "0");
                }
                c.put("prezzo", totale_imponibile);
                c.put("totale_ivato", totale_imponibile + totale_iva);
                c.put("totale_imponibile", totale_imponibile);
                c.put("quantita", 1d);
                if (col == 1) {
                    c.put("codice_articolo", getValueAt(row, 1));
                } else if (col == 2) {
                    c.put("descrizione", getValueAt(row, 2));
                } else if (col == 3) {
                    c.put("costo_giornaliero", CastUtils.toDouble(getValueAt(row, 3)));
                } else if (col == 4) {
                    c.put("costo_mensile", CastUtils.toDouble(getValueAt(row, 4)));
                } else if (col == 5) {
                    try {
                        map = (HashMap) getValueAt(row, 5);
                        val = String.valueOf(Db.nz(map.get("k"), "0"));
                        val = String.valueOf(val.replace('.', ','));
                        val = val.equals("") ? "0" : val;
                    } catch (Exception e) {
                        val = "";
                    }
                    c.put("durata_consulenza", Integer.parseInt(val));
                } else if (col == 6) {
                    try {
                        map = (HashMap) getValueAt(row, 6);
                        val = String.valueOf(Db.nz(map.get("k"), "0"));
                        val = String.valueOf(val.replace('.', ','));
                        val = val.equals("") ? "0" : val;
                    } catch (Exception e) {
                        val = "0";
                    }
                    c.put("durata_contratto", Integer.parseInt(val));
                } else if (col == 7) {
                    try {
                        map = (HashMap) getValueAt(row, 7);
                        val = String.valueOf(Db.nz(map.get("k"), "0"));
                        val = String.valueOf(val.replace('.', ','));
                        val = val.equals("") ? "0" : val;
                    } catch (Exception e) {
                        val = "0";
                    }
                    c.put("emissione_fattura", val);
                } else if (col == 8) {
                    try {
                        map = (HashMap) getValueAt(row, 8);
                        val = String.valueOf(Db.nz(map.get("k"), "0"));
                    } catch (Exception e) {
                        val = "";
                    }
                    c.put("termini_pagamento", val);
                }
                sql = sql + DbUtils.prepareSqlFromMap(c);
                if (id_riga != null) {
                    sql += " where id = " + id_riga;
                }
                System.out.println("sql foglio b: " + sql);
                try {
                    DbUtils.tryExecQuery(Db.getConn(), sql);
                    //se id_riga nullo prendo il nuovo id e lo metto altrimenti facci oaltre insert
                    if (id_riga == null) {
                        id_riga = CastUtils.toInteger(DbUtils.getObject(Db.getConn(), "select LAST_INSERT_ID()"));
                        if (id_riga == null) {
                            SwingUtils.showErrorMessage(form, "Errore nel recupero di LAST_INSERT_ID");
                        } else {
                            System.out.println("riga inserita: " + id_riga);
                            setValueAt(id_riga, row, col_id);
                        }
                    }                    
                } catch (Exception e) {
                    SwingUtils.showExceptionMessage(form, e);
                }                
            } else {
                System.out.println("elimino riga");

                if (form.texNumeOrdine.getText().length() > 0 && form.texAnno.getText().length() > 0 && getValueAt(row, 0).toString().length() > 0) {
                    System.out.println("elimino riga 2");
                    sql = "delete from righ_ordi where ";
                    sql += "serie = " + Db.pc(form.texSeri.getText(), Types.VARCHAR);
                    sql += " and numero = " + form.texNumeOrdine.getText();
                    sql += " and anno = " + form.texAnno.getText();
                    sql += " and riga = " + Db.pc(getValueAt(row, 0), Types.INTEGER);
                    Db.executeSql(sql);
                }
            }

            form.dbAssociaGrigliaRighe();
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if (column == 0 || column == 9) {
            return false;
        }
        return super.isCellEditable(row, column);
    }
}

class ComboBoxRenderer extends tnxbeans.tnxComboField implements TableCellRenderer {

    public ComboBoxRenderer(String query) {
        super();
        this.dbOpenList(Db.getConn(), query);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            super.setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }

        // Select the current value
        setSelectedItem(value);
        return this;
    }
}

class ComboBoxRenderer2 extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        try {
            if (value instanceof Map) {
                label.setText((String) ((Map) value).get("d"));
            } else {
                label.setText((String) value);
            }
        } catch (Exception e) {
            e.printStackTrace();
            label.setText("");
        }
        return label;
    }
}

class CellEditorFoglioOrdine extends javax.swing.DefaultCellEditor {

    javax.swing.JTable table;
    java.awt.Component editComp;

    public CellEditorFoglioOrdine(javax.swing.JTextField textField) {
        super(textField);
        textField.setMargin(new Insets(1, 1, 1, 1));
        textField.setBorder(BorderFactory.createEmptyBorder());
        setClickCountToStart(1);
    }

    public CellEditorFoglioOrdine(tnxbeans.tnxComboField combo) {
        super(combo);
        setClickCountToStart(1);
    }

    @Override
    public Object getCellEditorValue() {
        if (getComponent() instanceof tnxbeans.tnxComboField) {
            tnxbeans.tnxComboField combo = (tnxComboField) getComponent();
            if (combo.getSelectedKey() instanceof Integer && ((Integer)combo.getSelectedKey()) == -1) return null;
            if (combo.getSelectedKey() instanceof String && ((String)combo.getSelectedKey()).length() == 0) return null;
            Map m = new HashMap();
            m.put("k", combo.getSelectedKey());
            m.put("d", combo.getSelectedItem());
            return m;
        } else {
            return super.getCellEditorValue();
        }
    }

    public java.awt.Component getTableCellEditorComponent(javax.swing.JTable jTable, Object obj, boolean param, int param3, int param4) {

        final java.awt.Component edit;
        final java.awt.Component areaEdit;
        table = jTable;
        edit = super.getTableCellEditorComponent(jTable, obj, param, param3, param4);
        if (obj instanceof Map) {
            if (edit instanceof tnxComboField) {
                ((tnxComboField)edit).dbTrovaKey( ((Map)obj).get("k") );
            }
        }
        editComp = edit;
        edit.addFocusListener(new FocusListener() {

            public void focusGained(java.awt.event.FocusEvent evt) {
                if (edit instanceof javax.swing.JTextField) {
                    javax.swing.JTextField textEdit = (javax.swing.JTextField) edit;
//                    textEdit.selectAll();
                }
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
