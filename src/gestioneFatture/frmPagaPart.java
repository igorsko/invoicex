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

import it.tnx.Db;
import it.tnx.accessoUtenti.Permesso;
import it.tnx.commons.CastUtils;
import it.tnx.commons.DateUtils;
import it.tnx.commons.DbUtils;
import it.tnx.commons.SwingUtils;
import it.tnx.commons.table.EditorUtils;
import it.tnx.commons.table.ExcelLikeJTable;
import it.tnx.commons.table.RendererUtils;
import it.tnx.commons.table.TableUtils;
import it.tnx.invoicex.gui.SituazioneClienti;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.sql.*;
import java.sql.ResultSet;


import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.Vector;

import javax.swing.*;
import javax.swing.JOptionPane;
import javax.swing.border.Border;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.*;
import org.apache.commons.lang.StringUtils;
import tnxbeans.DbEvent;
import tnxbeans.DbListener;
import tnxbeans.tnxDbPanel;


public class frmPagaPart
        extends javax.swing.JInternalFrame {

    String sql;
    Scadenze scadenze;
    java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0.00");
    NumberFormat nfe = NumberFormat.getInstance();
    SimpleDateFormat datef = new SimpleDateFormat("dd/MM/yy");
    double totaleDocumento;
    java.util.Timer timTotali;
    String oldStato = "";
    String oldStato2 = "";
    Date oldDataScad = null;
    Double oldImporto = null;
    public static int INDEX_COL_IMPORTO = 8;

    boolean salvato = false;;

    /** Creates new form frmDati_blank */
    public frmPagaPart(Scadenze scadenze, Integer id_scadenza) {
        this.scadenze = scadenze;

        try {
            mask = new MaskFormatter("##/##/##");
        } catch (Exception err) {
            err.printStackTrace();
        }

        initComponents();


        jLabel3.setBorder(new Border() {

            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                g.setColor(Color.GRAY);
                g.drawLine(x, y, width, y);
            }

            public Insets getBorderInsets(Component c) {
                return new Insets(2, 10, 1, 10);
            }

            public boolean isBorderOpaque() {
                return true;
            }
        });
        try {
            TableUtils.addAutoNewRow(tabParziali);
            tabParziali.getColumnModel().getColumn(1).setCellEditor(new EditorUtils.DateEditor(new JTextField(), datef));
            tabParziali.getColumnModel().getColumn(2).setCellEditor(new EditorUtils.CurrencyEditor(new JTextField(), nfe));
            tabParziali.getColumnModel().getColumn(1).setCellRenderer(new RendererUtils.DateRenderer(datef));
            tabParziali.getColumnModel().getColumn(2).setCellRenderer(new RendererUtils.CurrencyRenderer(df));
        } catch (Exception e) {
            e.printStackTrace();
        }
        tabParziali.getModel().addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 2) {
                    if (tabParziali.getValueAt(e.getFirstRow(), 1) == null) {
                        tabParziali.setValueAt(new Date(), e.getFirstRow(), 1);
                    }
                }
                if (!dati.isRefreshing) {
                    dati.dbCambiaStato(tnxDbPanel.DB_MODIFICA);
                    if (oldStato.equals("S") || oldStato.equals("N")) {
                        if (getMancante() > 0 && getPagato() > 0) {
                            cheParziale.setSelected(true);
                            initOldStato();
                        }
                    }
                }
            }
        });


        //associo il panel ai dati
        this.dati.dbNomeTabella = "scadenze";

        Vector chiave = new Vector();
//        chiave.add("documento_tipo");
//        chiave.add("documento_serie");
//        chiave.add("documento_numero");
//        chiave.add("documento_anno");
//        chiave.add("data_scadenza");
        chiave.add("id");

        this.dati.dbChiave = chiave;
        this.dati.butSave = this.butSave;
        this.dati.butSaveClose = this.butSave1;
        this.dati.butUndo = this.butUndo;
        this.dati.butFind = this.butFind;
        this.dati.butDele = this.butDele;
        this.dati.butNew  = this.butNew;
        this.dati.tipo_permesso = Permesso.PERMESSO_SCADENZARIO;
        
        //this.dati.dbOpen(Db.conn,"select * from scadenze where id = " + codiceCliente);
        sql = "select * from scadenze";
        sql += " where documento_tipo = " + Db.pc(scadenze.documento_tipo, Types.VARCHAR);
        sql += " and documento_serie = " + Db.pc(scadenze.documento_serie, Types.VARCHAR);
        sql += " and documento_numero = " + Db.pc(scadenze.documento_numero, Types.INTEGER);
        sql += " and documento_anno = " + Db.pc(scadenze.documento_anno, Types.INTEGER);
        sql += " order by id";
        this.dati.dbOpen(Db.conn, sql);
        dati.addDbListener(new DbListener() {

            public void statusFired(DbEvent event) {
                if (event.getStatus() == tnxDbPanel.STATUS_REFRESHING) {
                    //carico rad pagata
                    if (StringUtils.equals("S", (String) dati.dbGetField("pagata"))) {
                        chePagata.setSelected(true);
                    }
                    if (StringUtils.equals("N", (String) dati.dbGetField("pagata"))) {
                        cheNonPagata.setSelected(true);
                    }
                    if (StringUtils.equals("P", (String) dati.dbGetField("pagata"))) {
                        cheParziale.setSelected(true);
                    }
                    initOldStato();
                    initOldStatoPerProvvigioni();
                    try {
                        //carico parziali
                        ((DefaultTableModel) tabParziali.getModel()).setRowCount(0);
                        TableUtils.loadData(Db.conn, "select id, data, importo from scadenze_parziali where id_scadenza = " + texId.getText(), (DefaultTableModel) tabParziali.getModel());
                        ((DefaultTableModel) tabParziali.getModel()).addRow(new Object[tabParziali.getColumnCount()]);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                if (event.getStatus() == tnxDbPanel.STATUS_PRE_SAVING) {
                    Hashtable campiagg = new Hashtable();
                    campiagg.put("pagata", "'" + getFlagPagata() + "'");
                    dati.setCampiAggiuntivi(campiagg);
                }
            }
        });
        this.dati.dbRefresh();

        //apro la griglia
        //this.griglia.dbEditabile = true;
        java.util.Hashtable colsWidthPerc = new java.util.Hashtable();
        colsWidthPerc.put("documento_serie", new Double(0));
        colsWidthPerc.put("documento_anno", new Double(0));
        colsWidthPerc.put("documento_numero", new Double(0));
        colsWidthPerc.put("documento_tipo", new Double(0));
        colsWidthPerc.put("data_scadenza", new Double(0));
        colsWidthPerc.put("id", new Double(10));
        colsWidthPerc.put("numero", new Double(15));
        colsWidthPerc.put("Data", new Double(20));
        colsWidthPerc.put("Importo", new Double(20));
        colsWidthPerc.put("Pagato", new Double(3));
        this.griglia.columnsSizePerc = colsWidthPerc;

        java.util.Hashtable colsAlign = new java.util.Hashtable();
        colsAlign.put("Importo", "RIGHT_CURRENCY");
        this.griglia.columnsAlign = colsAlign;
        this.griglia.dbChiave = chiave;
        this.griglia.flagUsaThread = false;

        //this.griglia.dbOpen(Db.conn,"select data_scadenza,pagata,importo from scadenze where id = " + codiceCliente + " order by ragione_sociale");
        sql = "select documento_serie,documento_anno,documento_numero,documento_tipo,data_scadenza,id,numero, data_scadenza as Data, importo as Importo, pagata as Pagata from scadenze";
        sql += " where documento_tipo = " + Db.pc(scadenze.documento_tipo, Types.VARCHAR);
        sql += " and documento_serie = " + Db.pc(scadenze.documento_serie, Types.VARCHAR);
        sql += " and documento_numero = " + Db.pc(scadenze.documento_numero, Types.INTEGER);
        sql += " and documento_anno = " + Db.pc(scadenze.documento_anno, Types.INTEGER);
        sql += " order by id";
        this.griglia.dbOpen(Db.conn, sql);
        this.griglia.dbPanel = this.dati;

        //sistemo alcune cose
        //this.comTipo.addItem("CL");
        //this.comTipo.addItem("FO");
        this.dati.dbRefresh();

        if (dati.isOnSomeRecord == false) {
            javax.swing.JOptionPane.showMessageDialog(this, "Non ci sono scadenze caricate per questo documento, prosegui inserendone una", "Attenzione", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            butNewActionPerformed(null);
        }

        String tipodesc = "";
        if (scadenze.documento_tipo.equals(Db.TIPO_DOCUMENTO_DDT)) {
            tipodesc = "DDT";
        } else if (scadenze.documento_tipo.equals(Db.TIPO_DOCUMENTO_FATTURA)) {
            tipodesc = "Fattura di vendita";
        } else if (scadenze.documento_tipo.equals(Db.TIPO_DOCUMENTO_FATTURA_RICEVUTA)) {
            tipodesc = "Fattura di acquisto";
        } else if (scadenze.documento_tipo.equals(Db.TIPO_DOCUMENTO_ORDINE)) {
            tipodesc = "Ordine/Preventivo";
        }

        this.labDocu.setText("Scadenze del documento: " + tipodesc + " " + scadenze.documento_serie + scadenze.documento_numero + " del " + DateUtils.formatDate(scadenze.documento_data));

        //metto totale fattura
        if (scadenze.documento_tipo.equals(Db.TIPO_DOCUMENTO_DDT)) {
            sql = "Select totale, 0 as totale_da_pagare from test_ddt";
        } else if (scadenze.documento_tipo.equals(Db.TIPO_DOCUMENTO_FATTURA)) {
            sql = "Select totale, totale_da_pagare from test_fatt";
        } else if (scadenze.documento_tipo.equals(Db.TIPO_DOCUMENTO_FATTURA_RICEVUTA)) {
            sql = "Select importo as totale, totale_da_pagare from test_fatt_acquisto";
        } else if (scadenze.documento_tipo.equals(Db.TIPO_DOCUMENTO_ORDINE)) {
            sql = "Select totale, 0 as totale_da_pagare from test_ordi";
        }

        sql += " where serie = " + Db.pc(scadenze.documento_serie, Types.VARCHAR);
        sql += " and numero = " + Db.pc(scadenze.documento_numero, Types.INTEGER);
        sql += " and anno = " + Db.pc(scadenze.documento_anno, Types.INTEGER);

        ResultSet tempTotaleFattura = Db.openResultSet(sql);

        try {
            tempTotaleFattura.next();
            labTotaleFattura.setText("Totale Fattura " + " " + " " + df.format(tempTotaleFattura.getDouble(1)));
            totaleDocumento = tempTotaleFattura.getDouble(1);
            if (CastUtils.toDouble0(tempTotaleFattura.getDouble("totale_da_pagare")) != 0) {
                labTotaleFattura.setText("Totale Fattura " + " " + " " + df.format(tempTotaleFattura.getDouble("totale_da_pagare")));
                totaleDocumento = tempTotaleFattura.getDouble("totale_da_pagare");
            }
        } catch (Exception err) {
            err.printStackTrace();
        }

        //creo timer per aggiornare totale scadenze
        timTotali = new java.util.Timer();

        timRefreshTotali timRefresh = new timRefreshTotali(this, this.griglia, this.labTotaleScadenze, totaleDocumento);
        timTotali.schedule(timRefresh, 1000, 500);
//        timTotali.schedule(timRefresh, 5000, 500);

        //seleziono la scadenza in oggetto
        System.out.println("id_scadenza:" + id_scadenza);
        if (id_scadenza != null) {
            griglia.dbSelezionaRiga(5, id_scadenza);
            griglia.dbSelezionaRiga();
        }

    }

    /** This method is called from within the constructor to

     * initialize the form.

     * WARNING: Do NOT modify this code. The content of this method is

     * always regenerated by the Form Editor.

     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        panAlto = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        butNew = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        butDele = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        butFind = new javax.swing.JButton();
        jLabel131 = new javax.swing.JLabel();
        butFirs = new javax.swing.JButton();
        butPrev = new javax.swing.JButton();
        butNext = new javax.swing.JButton();
        butLast = new javax.swing.JButton();
        tabCent = new javax.swing.JTabbedPane();
        panDati = new javax.swing.JPanel();
        dati = new tnxbeans.tnxDbPanel();
        texId = new tnxbeans.tnxTextField();
        jLabel2211 = new javax.swing.JLabel();
        jLabel21111 = new javax.swing.JLabel();
        labDocu = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        griglia = new tnxbeans.tnxDbGrid();
        jLabel3 = new javax.swing.JLabel();
        jLabel22111 = new javax.swing.JLabel();
        texNume = new tnxbeans.tnxTextField();
        texDataScad = new tnxbeans.tnxTextFieldFormatted(mask);
        texDocuAnno = new tnxbeans.tnxTextField();
        texDocuAnno.setVisible(false);
        texDocuTipo = new tnxbeans.tnxTextField();
        texDocuTipo.setVisible(false);
        texDocuSeri = new tnxbeans.tnxTextField();
        texDocuSeri.setVisible(false);
        texDocuNume = new tnxbeans.tnxTextField();
        texDocuNume.setVisible(false);
        jLabel22112 = new javax.swing.JLabel();
        texImpo = new tnxbeans.tnxTextField();
        labTotaleScadenze = new javax.swing.JLabel();
        labTotaleFattura = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        tnxMemoField1 = new tnxbeans.tnxMemoField();
        jLabel22113 = new javax.swing.JLabel();
        cheNonPagata = new javax.swing.JRadioButton();
        cheParziale = new javax.swing.JRadioButton();
        chePagata = new javax.swing.JRadioButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabParziali = new ExcelLikeJTable();
        labTotali = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        butUndo = new javax.swing.JButton();
        butSave = new javax.swing.JButton();
        butSave1 = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Scadenze");
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
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

        panAlto.setLayout(new java.awt.BorderLayout());

        jToolBar1.setRollover(true);

        butNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/document-new.png"))); // NOI18N
        butNew.setText("Nuovo");
        butNew.setBorderPainted(false);
        butNew.setMargin(new java.awt.Insets(2, 2, 2, 2));
        butNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butNewActionPerformed(evt);
            }
        });
        jToolBar1.add(butNew);

        jLabel1.setText(" ");
        jToolBar1.add(jLabel1);

        butDele.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/places/user-trash.png"))); // NOI18N
        butDele.setText("Elimina");
        butDele.setBorderPainted(false);
        butDele.setMargin(new java.awt.Insets(2, 2, 2, 2));
        butDele.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butDeleActionPerformed(evt);
            }
        });
        jToolBar1.add(butDele);

        jLabel11.setText(" ");
        jToolBar1.add(jLabel11);

        jLabel12.setText(" ");
        jToolBar1.add(jLabel12);

        jLabel13.setText(" ");
        jToolBar1.add(jLabel13);

        butFind.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/edit-find.png"))); // NOI18N
        butFind.setText("Trova");
        butFind.setBorderPainted(false);
        butFind.setMargin(new java.awt.Insets(2, 2, 2, 2));
        butFind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butFindActionPerformed(evt);
            }
        });
        jToolBar1.add(butFind);

        jLabel131.setText(" ");
        jToolBar1.add(jLabel131);

        butFirs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/go-first.png"))); // NOI18N
        butFirs.setBorderPainted(false);
        butFirs.setMargin(new java.awt.Insets(2, 2, 2, 2));
        butFirs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butFirsActionPerformed(evt);
            }
        });
        jToolBar1.add(butFirs);

        butPrev.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/go-previous.png"))); // NOI18N
        butPrev.setBorderPainted(false);
        butPrev.setMargin(new java.awt.Insets(2, 2, 2, 2));
        butPrev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butPrevActionPerformed(evt);
            }
        });
        jToolBar1.add(butPrev);

        butNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/go-next.png"))); // NOI18N
        butNext.setBorderPainted(false);
        butNext.setMargin(new java.awt.Insets(2, 2, 2, 2));
        butNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butNextActionPerformed(evt);
            }
        });
        jToolBar1.add(butNext);

        butLast.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/go-last.png"))); // NOI18N
        butLast.setBorderPainted(false);
        butLast.setMargin(new java.awt.Insets(2, 2, 2, 2));
        butLast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butLastActionPerformed(evt);
            }
        });
        jToolBar1.add(butLast);

        panAlto.add(jToolBar1, java.awt.BorderLayout.CENTER);

        getContentPane().add(panAlto, java.awt.BorderLayout.NORTH);

        tabCent.setName("dati"); // NOI18N

        panDati.setName("dati"); // NOI18N
        panDati.setLayout(new java.awt.BorderLayout());

        texId.setColumns(5);
        texId.setEditable(false);
        texId.setText("id");
        texId.setDbNomeCampo("id");

        jLabel2211.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2211.setText("data scadenza");

        jLabel21111.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel21111.setText("id");

        labDocu.setForeground(new java.awt.Color(0, 0, 255));
        labDocu.setText("...");

        jScrollPane1.setToolTipText("cliccando su una riga si ceglie la destinazione da moficare");

        griglia.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                grigliaComponentResized(evt);
            }
        });
        jScrollPane1.setViewportView(griglia);

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 10));
        jLabel3.setText("Elenco scadenze");

        jLabel22111.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel22111.setText("numero");

        texNume.setDbNomeCampo("numero");
        texNume.setDbTipoCampo("NUMERICO");

        texDataScad.setDbNomeCampo("data_scadenza");
        texDataScad.setDbTipoCampo("data");

        texDocuAnno.setBackground(new java.awt.Color(255, 204, 204));
        texDocuAnno.setDbNomeCampo("documento_anno");
        texDocuAnno.setDbTipoCampo("LONG");

        texDocuTipo.setBackground(new java.awt.Color(255, 204, 204));
        texDocuTipo.setDbNomeCampo("documento_tipo");

        texDocuSeri.setBackground(new java.awt.Color(255, 204, 204));
        texDocuSeri.setDbNomeCampo("documento_serie");

        texDocuNume.setBackground(new java.awt.Color(255, 204, 204));
        texDocuNume.setDbNomeCampo("documento_numero");
        texDocuNume.setDbTipoCampo("LONG");

        jLabel22112.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel22112.setText("importo");

        texImpo.setDbNomeCampo("importo");
        texImpo.setDbTipoCampo("VALUTA");

        labTotaleScadenze.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labTotaleScadenze.setText("Totale scadenze");

        labTotaleFattura.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labTotaleFattura.setText("Totale fattura");

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/emblems/emblem-system.png"))); // NOI18N
        jButton1.setText("rigenera le scadenze");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        tnxMemoField1.setDbNomeCampo("note_pagamento");

        jLabel22113.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel22113.setText("note");

        buttonGroup1.add(cheNonPagata);
        cheNonPagata.setForeground(new java.awt.Color(125, 0, 0));
        cheNonPagata.setText("Non Pagata");
        cheNonPagata.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cheNonPagataActionPerformed(evt);
            }
        });

        buttonGroup1.add(cheParziale);
        cheParziale.setForeground(new java.awt.Color(123, 125, 0));
        cheParziale.setText("Pagata Parzialmente");
        cheParziale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cheParzialeActionPerformed(evt);
            }
        });

        buttonGroup1.add(chePagata);
        chePagata.setForeground(new java.awt.Color(18, 125, 0));
        chePagata.setText("Pagata");
        chePagata.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chePagataActionPerformed(evt);
            }
        });

        tabParziali.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null}
            },
            new String [] {
                "id", "data", "importo"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tabParziali);
        tabParziali.getColumnModel().getColumn(0).setMinWidth(0);
        tabParziali.getColumnModel().getColumn(0).setPreferredWidth(0);
        tabParziali.getColumnModel().getColumn(0).setMaxWidth(0);

        labTotali.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labTotali.setText("Totale scadenze");

        org.jdesktop.layout.GroupLayout datiLayout = new org.jdesktop.layout.GroupLayout(dati);
        dati.setLayout(datiLayout);
        datiLayout.setHorizontalGroup(
            datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, datiLayout.createSequentialGroup()
                .addContainerGap()
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 594, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, labTotaleFattura, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 280, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, datiLayout.createSequentialGroup()
                        .add(texDocuTipo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(texDocuSeri, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(texDocuNume, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(texDocuAnno, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 272, Short.MAX_VALUE)
                        .add(labTotaleScadenze, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 280, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, datiLayout.createSequentialGroup()
                        .add(labDocu, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 498, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel21111, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(texId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(tnxMemoField1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(datiLayout.createSequentialGroup()
                        .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(jLabel22113, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jLabel2211, 0, 0, Short.MAX_VALUE)
                            .add(jLabel22111, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                            .add(jLabel22112, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(datiLayout.createSequentialGroup()
                                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                        .add(texDataScad, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .add(texImpo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
                                    .add(texNume, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(cheNonPagata)
                                    .add(cheParziale)
                                    .add(chePagata))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE))
                            .add(labTotali, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jButton1))
                .addContainerGap())
            .add(jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 614, Short.MAX_VALUE)
        );
        datiLayout.setVerticalGroup(
            datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(datiLayout.createSequentialGroup()
                .addContainerGap()
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(labDocu)
                    .add(texId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel21111, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(datiLayout.createSequentialGroup()
                        .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel22111, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(texNume, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(cheNonPagata))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel2211, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(texDataScad, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(cheParziale))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel22112, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(texImpo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(chePagata)))
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(2, 2, 2)
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel22113, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(labTotali, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(tnxMemoField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(labTotaleScadenze, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(texDocuTipo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(texDocuSeri, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(texDocuNume, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(texDocuAnno, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(labTotaleFattura, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton1)
                .addContainerGap())
        );

        panDati.add(dati, java.awt.BorderLayout.PAGE_START);

        tabCent.addTab("dati", panDati);

        getContentPane().add(tabCent, java.awt.BorderLayout.CENTER);

        butUndo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/edit-undo.png"))); // NOI18N
        butUndo.setText("Annulla");
        butUndo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butUndoActionPerformed(evt);
            }
        });
        jPanel2.add(butUndo);

        butSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/devices/media-floppy.png"))); // NOI18N
        butSave.setText("Salva");
        butSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSaveActionPerformed(evt);
            }
        });
        jPanel2.add(butSave);

        butSave1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/devices/media-floppy.png"))); // NOI18N
        butSave1.setText("Salva e Chiudi");
        butSave1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSave1ActionPerformed(evt);
            }
        });
        jPanel2.add(butSave1);

        getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void initOldStato() {
        if (chePagata.isSelected()) {
            oldStato = "S";
        }
        if (cheParziale.isSelected()) {
            oldStato = "P";
        }
        if (cheNonPagata.isSelected()) {
            oldStato = "N";
        }
    }

    private void initOldStatoPerProvvigioni() {
        if (chePagata.isSelected()) {
            oldStato2 = "S";
        }
        if (cheParziale.isSelected()) {
            oldStato2 = "P";
        }
        if (cheNonPagata.isSelected()) {
            oldStato2 = "N";
        }
        oldImporto = CastUtils.toDouble0(texImpo.getText());
        oldDataScad = CastUtils.toDateIta(texDataScad.getText());
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        if (javax.swing.JOptionPane.showConfirmDialog(this, "Sicuro di rigenerare le scadenze?", "Attenzione", javax.swing.JOptionPane.YES_NO_OPTION) == javax.swing.JOptionPane.YES_OPTION) {
            scadenze.generaScadenze();
            try {
                Storico.scrivi("Genera scadenze", "da frmPagaPart " + scadenze.documento_tipo + " " + scadenze.documento_serie + " " + scadenze.documento_numero + " " + scadenze.documento_anno);
            } catch (Exception e) {
            }
            try {
                scadenze.generaProvvigioni();
                Storico.scrivi("Genera provvigioni", "da frmPagaPart");
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.griglia.dbRefresh();
            griglia.dbSelezionaRiga();
            aggiornaSituazioni();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void grigliaComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_grigliaComponentResized

        // Add your handling code here:
        this.griglia.resizeColumnsPerc(true);
    }//GEN-LAST:event_grigliaComponentResized

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened

        // Add your handling code here:
        this.griglia.resizeColumnsPerc(true);
    }//GEN-LAST:event_formInternalFrameOpened

    private void butLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butLastActionPerformed

        // Add your handling code here:
        this.griglia.dbGoLast();
    }//GEN-LAST:event_butLastActionPerformed

    private void butNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butNextActionPerformed

        // Add your handling code here:
        this.griglia.dbGoNext();
    }//GEN-LAST:event_butNextActionPerformed

    private void butPrevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butPrevActionPerformed

        // Add your handling code here:
        this.griglia.dbGoPrevious();
    }//GEN-LAST:event_butPrevActionPerformed

    private void butFirsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butFirsActionPerformed

        // Add your handling code here:
        this.griglia.dbGoFirst();
    }//GEN-LAST:event_butFirsActionPerformed

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        timTotali.cancel();
        main.getPadre().closeFrame(this);
        this.dispose();
    }//GEN-LAST:event_formInternalFrameClosing

    private void butDeleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butDeleActionPerformed
        int ret = JOptionPane.showConfirmDialog(this, "Sicuro di eliminare ?", "Attenzione", JOptionPane.YES_NO_OPTION);
        if (ret == JOptionPane.YES_OPTION) {
            this.dati.dbDelete();
            this.griglia.dbRefresh();
        }
        //chiedere se togliere la provvigione associata
    }//GEN-LAST:event_butDeleActionPerformed

    private void butFindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butFindActionPerformed

        boolean ret = this.griglia.dbFindNext();

        if (ret == false) {

            int ret2 = JOptionPane.showConfirmDialog(this, "Posizione non trovata\nVuoi riprovare dall'inizio ?", "Attenzione", JOptionPane.YES_NO_OPTION);

            //JOptionPane.showMessageDialog(this,"?-:"+String.valueOf(i));
            if (ret2 == JOptionPane.OK_OPTION) {

                boolean ret3 = this.griglia.dbFindFirst();
            }
        }
    }//GEN-LAST:event_butFindActionPerformed

    private void butUndoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butUndoActionPerformed
        dati.dbUndo();
    }//GEN-LAST:event_butUndoActionPerformed

    private void butSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveActionPerformed
        salvato = false;
        if (chePagata.isSelected() && getMancante() > 0) {
            if (!SwingUtils.showYesNoMessage(this, "La scadenza e' segnata come Pagata ma risultano da pagare ancora: " + getMancante() + ", confermi ?")) return;
        }
        if (cheNonPagata.isSelected() && getMancante() == 0) {
            if (!SwingUtils.showYesNoMessage(this, "La scadenza e' segnata come NON Pagata ma non risulta gia' pagata" + ", confermi ?")) return;
        }
        if (cheParziale.isSelected() && getMancante() == 0) {
            if (!SwingUtils.showYesNoMessage(this, "La scadenza e' segnata come Pagata Parzialmente ma risulta pagata completamente, confermi ?")) return;
        }
        if (cheParziale.isSelected() && getMancante().doubleValue() == getTotaleScadenza().doubleValue()) {
            if (!SwingUtils.showYesNoMessage(this, "La scadenza e' segnata come Pagata Parzialmente ma risulta da pagare interamente, confermi ?")) return;
        }

        this.dati.dbSave();
        //salvo i dati della griglia parziali
        String sql = "";
        try {
            sql = "delete from scadenze_parziali where id_scadenza = " + texId.getText();
            DbUtils.tryExecQuery(Db.conn, sql);
            for (int i = 0; i < tabParziali.getRowCount(); i++) {
                if (CastUtils.toDouble0(tabParziali.getValueAt(i, 2)) != 0) {
                    sql = "insert into scadenze_parziali (id_scadenza, data, importo) values (" + texId.getText() +
                            ", " + Db.pc(CastUtils.toDate(tabParziali.getValueAt(i, 1)), Types.DATE) +
                            ", " + Db.pc(CastUtils.toDouble0(tabParziali.getValueAt(i, 2)), Types.DOUBLE) +
                            ")";
                    DbUtils.tryExecQuery(Db.conn, sql);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("sql exception:" + sql);
        }

        //rigenero solo se cambiata data o importo
        Date dataScad = CastUtils.toDateIta(texDataScad.getText());
        Double importo = CastUtils.toDouble0(texImpo.getText());
        String newStato = null;
        if (chePagata.isSelected()) {
            newStato = "S";
        }
        if (cheParziale.isSelected()) {
            newStato = "P";
        }
        if (cheNonPagata.isSelected()) {
            newStato = "N";
        }
        try {
            //scadenze.generaProvvigioni();
            scadenze.generaProvvigioni(oldDataScad, dataScad, oldImporto, importo, true, oldStato2, newStato);
            Storico.scrivi("Genera provvigioni", "da frmPagaPart salva ");
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.griglia.dbRefresh();

        //cerco le finestre di situazione clienti per aggiornarle
        aggiornaSituazioni();

        salvato = true;
        initOldStatoPerProvvigioni();
    }//GEN-LAST:event_butSaveActionPerformed

    private String getFlagPagata() {
        if (cheNonPagata.isSelected()) {
            return "N";
        }
        if (chePagata.isSelected()) {
            return "S";
        }
        if (cheParziale.isSelected()) {
            return "P";
        }
        return "N";
    }

    private void butNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butNewActionPerformed
        this.dati.dbNew();

        //setto defualt
        java.sql.Statement stat;
        ResultSet resu;

        //apre il resultset per ultimo +1
        try {
            stat = Db.conn.createStatement();

            String sql = "select numero from scadenze";
            sql += " where documento_tipo = " + Db.pc(this.scadenze.documento_tipo, Types.VARCHAR);
            sql += " and documento_serie = " + Db.pc(this.scadenze.documento_serie, Types.VARCHAR);
            sql += " and documento_numero = " + Db.pc(this.scadenze.documento_numero, Types.INTEGER);
            sql += " and documento_anno = " + Db.pc(this.scadenze.documento_anno, Types.INTEGER);
            sql += " order by numero desc limit 1";
            resu = stat.executeQuery(sql);

            if (resu.next() == true) {
                this.texNume.setText(String.valueOf(resu.getInt(1) + 1));
            } else {
                this.texNume.setText("1");
            }
        } catch (Exception err) {
            javax.swing.JOptionPane.showMessageDialog(null, err.toString());
        }

        this.texDocuTipo.setText(scadenze.documento_tipo);
        this.texDocuSeri.setText(scadenze.documento_serie);
        this.texDocuNume.setText(String.valueOf(scadenze.documento_numero));
        this.texDocuAnno.setText(String.valueOf(scadenze.documento_anno));
    }//GEN-LAST:event_butNewActionPerformed

    private void cheNonPagataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cheNonPagataActionPerformed
        if (oldStato.equals("S") || oldStato.equals("P")) {
            if (SwingUtils.showYesNoMessage(this, "Vuoi azzerare i pagamenti di questa scadenza ?", "Attenzione")) {
                ((DefaultTableModel) tabParziali.getModel()).setRowCount(0);
                ((DefaultTableModel) tabParziali.getModel()).setRowCount(1);
            }
            dati.dbCambiaStato(tnxDbPanel.DB_MODIFICA);
            initOldStato();
        }
    }//GEN-LAST:event_cheNonPagataActionPerformed

    private void cheParzialeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cheParzialeActionPerformed
        if (oldStato.equals("S") || oldStato.equals("N")) {
            dati.dbCambiaStato(tnxDbPanel.DB_MODIFICA);
            initOldStato();
        }
    }//GEN-LAST:event_cheParzialeActionPerformed

    private void chePagataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chePagataActionPerformed
        if (oldStato.equals("N") || oldStato.equals("P")) {
            //inserire movimento nel parziale del mancante
            if (getMancante() > 0) {
                Object[] row = new Object[3];
                row[1] = new Date();
                row[2] = getMancante();
                try {
                    if (tabParziali.getValueAt(tabParziali.getRowCount() - 1, 2) == null) {
                        ((DefaultTableModel) tabParziali.getModel()).removeRow(tabParziali.getRowCount() - 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ((DefaultTableModel) tabParziali.getModel()).addRow(row);
            }

            dati.dbCambiaStato(tnxDbPanel.DB_MODIFICA);
            initOldStato();
        }

    }//GEN-LAST:event_chePagataActionPerformed

    private void butSave1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSave1ActionPerformed
        butSaveActionPerformed(evt);
        if (salvato) dispose();
    }//GEN-LAST:event_butSave1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butDele;
    private javax.swing.JButton butFind;
    private javax.swing.JButton butFirs;
    private javax.swing.JButton butLast;
    private javax.swing.JButton butNew;
    private javax.swing.JButton butNext;
    private javax.swing.JButton butPrev;
    private javax.swing.JButton butSave;
    private javax.swing.JButton butSave1;
    private javax.swing.JButton butUndo;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JRadioButton cheNonPagata;
    private javax.swing.JRadioButton chePagata;
    private javax.swing.JRadioButton cheParziale;
    private tnxbeans.tnxDbPanel dati;
    private tnxbeans.tnxDbGrid griglia;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel131;
    private javax.swing.JLabel jLabel21111;
    private javax.swing.JLabel jLabel2211;
    private javax.swing.JLabel jLabel22111;
    private javax.swing.JLabel jLabel22112;
    private javax.swing.JLabel jLabel22113;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel labDocu;
    private javax.swing.JLabel labTotaleFattura;
    private javax.swing.JLabel labTotaleScadenze;
    public javax.swing.JLabel labTotali;
    private javax.swing.JPanel panAlto;
    private javax.swing.JPanel panDati;
    private javax.swing.JTabbedPane tabCent;
    public javax.swing.JTable tabParziali;
    private tnxbeans.tnxTextFieldFormatted texDataScad;
    private tnxbeans.tnxTextField texDocuAnno;
    private tnxbeans.tnxTextField texDocuNume;
    private tnxbeans.tnxTextField texDocuSeri;
    private tnxbeans.tnxTextField texDocuTipo;
    private tnxbeans.tnxTextField texId;
    public tnxbeans.tnxTextField texImpo;
    private tnxbeans.tnxTextField texNume;
    private tnxbeans.tnxMemoField tnxMemoField1;
    // End of variables declaration//GEN-END:variables
    MaskFormatter mask;

    private Double getMancante() {
        double tempTotale = CastUtils.toDouble0(texImpo.getText());
        double tempTotalePagato = 0;
        for (int i = 0; i < tabParziali.getRowCount(); i++) {
            try {
                tempTotalePagato += CastUtils.toDouble0(tabParziali.getValueAt(i, 2));
            } catch (Exception e) {
            }
        }
        return tempTotale - tempTotalePagato;
    }

    private Double getTotaleScadenza() {
        double tempTotale = CastUtils.toDouble0(texImpo.getText());
        return tempTotale;
    }

    private double getPagato() {
        double tempTotalePagato = 0;
        for (int i = 0; i < tabParziali.getRowCount(); i++) {
            try {
                tempTotalePagato += CastUtils.toDouble0(tabParziali.getValueAt(i, 2));
            } catch (Exception e) {
            }
        }
        return tempTotalePagato;
    }

    private void aggiornaSituazioni() {
        //throw new UnsupportedOperationException("Not yet implemented");
        JInternalFrame[] frames = main.getPadre().getDesktopPane().getAllFrames();
        for (JInternalFrame frame : frames) {
            if (frame.getContentPane().getComponent(0) instanceof SituazioneClienti) {
                ((SituazioneClienti)frame.getContentPane().getComponent(0)).selezionaSituazione(false);
            }
        }
    }
}
class timRefreshTotali
        extends java.util.TimerTask {

    JInternalFrame frame;
    JLabel labTotaleScadenze;
    tnxbeans.tnxDbGrid griglia;
    double tempTotale;
    double tempTotaleScadenza;
    double tempTotalePagato;
    double tempTotaleDaPagare;
    java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0.00");
    private double totaleDocumento;

    public timRefreshTotali(JInternalFrame frame, tnxbeans.tnxDbGrid griglia, JLabel labTotaleScadenze, double totaleDocumento) {
        this.frame = frame;
        this.labTotaleScadenze = labTotaleScadenze;
        this.griglia = griglia;
        this.totaleDocumento = totaleDocumento;
    }

    public void run() {

        //debug
        //System.out.println("runTimer");
        try {
            //this.parent.prev.dbRefresh();
            //calcola totale
            tempTotale = 0;
            tempTotalePagato = 0;
            tempTotaleScadenza = CastUtils.toDouble0(((frmPagaPart) frame).texImpo.getText());
            tempTotaleDaPagare = CastUtils.toDouble0(((frmPagaPart) frame).texImpo.getText());
            for (int i = 0; i < griglia.getRowCount(); i++) {
                try {
                    tempTotale += Double.parseDouble(griglia.getValueAt(i, frmPagaPart.INDEX_COL_IMPORTO).toString());
                } catch (Exception err) {
                }
            }
            JTable tabParziali = ((frmPagaPart) frame).tabParziali;
            for (int i = 0; i < tabParziali.getRowCount(); i++) {
                try {
                    tempTotalePagato += CastUtils.toDouble0(tabParziali.getValueAt(i, 2));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //this.labTotaleScadenze.setText("Totale Scadenze " + "???" + " " + Db.formatDecimal(tempTotale));
            this.labTotaleScadenze.setText("Totale Scadenze " + " " + " " + df.format(tempTotale));
            tempTotaleDaPagare = tempTotaleDaPagare - tempTotalePagato;

            JLabel labTotali = ((frmPagaPart) frame).labTotali;
            labTotali.setFont(labTotali.getFont().deriveFont(Font.PLAIN));
            labTotali.setForeground(Color.BLACK);
            labTotali.setText("Pagato " + df.format(tempTotalePagato) + " / Da Pagare " + df.format(tempTotaleDaPagare));
            if (tempTotalePagato > tempTotaleScadenza) {
                labTotali.setFont(labTotali.getFont().deriveFont(Font.BOLD));
                labTotali.setForeground(Color.RED.darker().darker());
            }
            if (tempTotalePagato < tempTotaleScadenza && tempTotalePagato > 0) {
                labTotali.setFont(labTotali.getFont().deriveFont(Font.BOLD));
                labTotali.setForeground(Color.YELLOW.darker().darker());
            }

            if (!df.format(this.totaleDocumento).equals(df.format(tempTotale))) {
                this.labTotaleScadenze.setForeground(java.awt.Color.red);
            } else {
                this.labTotaleScadenze.setForeground(java.awt.Color.black);
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }
}