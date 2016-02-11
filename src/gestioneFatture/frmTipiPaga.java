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

 * frmDati_blank.java

 *

 * Created on 31 dicembre 2001, 16.43

 */
package gestioneFatture;

import it.tnx.Db;
import it.tnx.accessoUtenti.Permesso;
import it.tnx.commons.CastUtils;
import it.tnx.commons.DateUtils;
import java.awt.Component;
import java.util.ArrayList;


import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.*;
import org.apache.commons.lang.StringUtils;
import tnxbeans.DbEvent;
import tnxbeans.DbListener;
import tnxbeans.tnxDbPanel;

/**

 *

 * @author  Administrator

 */
public class frmTipiPaga
        extends javax.swing.JInternalFrame {

    String sql;

    /** Creates new form frmDati_blank */
    public frmTipiPaga() {
        initComponents();

        richiestagiornoItemStateChanged(null);

        simdata.setText(DateUtils.formatDate(new Date()));

        //associo il panel ai dati
        this.dati.dbNomeTabella = "pagamenti";

        Vector chiave = new Vector();
        chiave.add("codice");
        this.dati.dbChiave = chiave;
        this.dati.butSave = this.butSave;
        this.dati.butUndo = this.butUndo;
        this.dati.butFind = this.butFind;
        this.dati.butNew = this.butNew;
        this.dati.butDele = this.butDele;
        this.dati.tipo_permesso = Permesso.PERMESSO_ANAGRAFICA_TIPI_PAGAMENTO;
        //this.dati.dbOpen(Db.conn,"select * from scadenze where id = " + codiceCliente);
        sql = "select * from pagamenti order by codice";
        this.dati.dbOpen(Db.getConn(), sql);
        this.dati.dbRefresh();

        //apro la griglia
        //this.griglia.dbEditabile = true;
        java.util.Hashtable colsWidthPerc = new java.util.Hashtable();
        colsWidthPerc.put("codice", new Double(30));
        colsWidthPerc.put("immediato", new Double(10));
        
        //20.4.2015
        colsWidthPerc.put("15", new Double(10));
        colsWidthPerc.put("30", new Double(10));
        colsWidthPerc.put("45", new Double(10));
        colsWidthPerc.put("60", new Double(10));
        colsWidthPerc.put("75", new Double(10));
        colsWidthPerc.put("90", new Double(10));
        colsWidthPerc.put("120", new Double(10));
        colsWidthPerc.put("150", new Double(10));
        colsWidthPerc.put("180", new Double(10));
        colsWidthPerc.put("210", new Double(10));
        colsWidthPerc.put("240", new Double(10));
        colsWidthPerc.put("270", new Double(10));
        colsWidthPerc.put("300", new Double(10));
        colsWidthPerc.put("330", new Double(10));
        colsWidthPerc.put("360", new Double(10));
        colsWidthPerc.put("iva30gg", new Double(10));
        colsWidthPerc.put("finemese", new Double(10));
        this.griglia.columnsSizePerc = colsWidthPerc;
        this.griglia.dbChiave = chiave;
        this.griglia.flagUsaThread = false;

        //this.griglia.dbOpen(Db.conn,"select data_scadenza,pagata,importo from scadenze where id = " + codiceCliente + " order by ragione_sociale");
        sql = "select ";
        sql += " codice";

        //sql += " , coordinate_necessarie as 'banca'";
        //sql += " , IMMEDIATO as immediato";
        
        //20.4.2015
        sql += " , immediato, `15`, `30`, `45`, `60`, `75`, `90`, `120`, `150`, `180`, `210`, `240`, `270`, `300`, `330`, `360`, finemese, iva30gg ";

        //sql += " , FINEMESE as 'fine mese'";
        sql += " from pagamenti";
        sql += " order by codice";
        this.griglia.dbOpen(Db.getConn(), sql);
        this.griglia.dbPanel = this.dati;

        //combo conti
        comConti.dbOpenList(Db.getConn(), "select concat(abi, ' ', cab, ' ', cc) as conto,id from dati_azienda_banche order by abi, cab, cc", null, false);

        dati.addDbListener(new DbListener() {

            public void statusFired(DbEvent event) {
                if (event.getStatus() == tnxDbPanel.STATUS_REFRESHING) {
                    simulaActionPerformed(null);
                }
            }
        });

        //sistemo alcune cose
        //this.comTipo.addItem("CL");
        //this.comTipo.addItem("FO");
        this.dati.dbRefresh();
        rimettiRender();

        /*

        if (dati.isOnSomeRecord == false) {

        javax.swing.JOptionPane.showMessageDialog(this,"Non ci sono pagamenti caricati per questo documento, prosegui inserendone una","Attenzione",javax.swing.JOptionPane.INFORMATION_MESSAGE);

        butNewActionPerformed(null);

        }*/
        this.labTipoIpsoa.setVisible(false);
        this.texIdIpsoa.setVisible(false);
        if (main.getPersonalContain("ipsoa")) {
            this.labTipoIpsoa.setVisible(true);
            this.texIdIpsoa.setVisible(true);
        }

        rimettiRender();
    }

    /** This method is called from within the constructor to

     * initialize the form.

     * WARNING: Do NOT modify this code. The content of this method is

     * always regenerated by the Form Editor.

     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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
        jScrollPane2 = new javax.swing.JScrollPane();
        dati = new tnxbeans.tnxDbPanel();
        jLabel2211 = new javax.swing.JLabel();
        jLabel22111 = new javax.swing.JLabel();
        texNume = new tnxbeans.tnxTextField();
        tnxCheckBox1 = new tnxbeans.tnxCheckBox();
        texDataScad = new tnxbeans.tnxTextFieldFormatted(mask);
        immediata = new tnxbeans.tnxCheckBox();
        jLabel4 = new javax.swing.JLabel();
        gg30 = new tnxbeans.tnxCheckBox();
        gg60 = new tnxbeans.tnxCheckBox();
        gg90 = new tnxbeans.tnxCheckBox();
        gg120 = new tnxbeans.tnxCheckBox();
        gg150 = new tnxbeans.tnxCheckBox();
        gg180 = new tnxbeans.tnxCheckBox();
        finemese = new tnxbeans.tnxCheckBox();
        tnxCheckBox10 = new tnxbeans.tnxCheckBox();
        jLabel2212 = new javax.swing.JLabel();
        texDataScad1 = new tnxbeans.tnxTextFieldFormatted(mask);
        tnxCheckBox11 = new tnxbeans.tnxCheckBox();
        richiestagiorno = new tnxbeans.tnxCheckBox();
        comConti = new tnxbeans.tnxComboField();
        jLabel2213 = new javax.swing.JLabel();
        jLabel2214 = new javax.swing.JLabel();
        gg210 = new tnxbeans.tnxCheckBox();
        texIdIpsoa = new tnxbeans.tnxTextField();
        labTipoIpsoa = new javax.swing.JLabel();
        iva30 = new tnxbeans.tnxCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        simdata = new javax.swing.JTextField();
        jSeparator3 = new javax.swing.JSeparator();
        labSimula = new javax.swing.JLabel();
        simula = new javax.swing.JButton();
        giorno = new javax.swing.JTextField();
        gg240 = new tnxbeans.tnxCheckBox();
        gg270 = new tnxbeans.tnxCheckBox();
        gg300 = new tnxbeans.tnxCheckBox();
        gg330 = new tnxbeans.tnxCheckBox();
        gg360 = new tnxbeans.tnxCheckBox();
        gg75 = new tnxbeans.tnxCheckBox();
        gg45 = new tnxbeans.tnxCheckBox();
        gg15 = new tnxbeans.tnxCheckBox();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        griglia = new tnxbeans.tnxDbGrid();
        jPanel2 = new javax.swing.JPanel();
        butUndo = new javax.swing.JButton();
        butSave = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Tipi Pagamento");
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

        butNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/document-new.png"))); // NOI18N
        butNew.setText("Nuovo");
        butNew.setBorderPainted(false);
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
        butFirs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butFirsActionPerformed(evt);
            }
        });
        jToolBar1.add(butFirs);

        butPrev.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/go-previous.png"))); // NOI18N
        butPrev.setBorderPainted(false);
        butPrev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butPrevActionPerformed(evt);
            }
        });
        jToolBar1.add(butPrev);

        butNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/go-next.png"))); // NOI18N
        butNext.setBorderPainted(false);
        butNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butNextActionPerformed(evt);
            }
        });
        jToolBar1.add(butNext);

        butLast.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/go-last.png"))); // NOI18N
        butLast.setBorderPainted(false);
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

        jScrollPane2.setBorder(null);
        jScrollPane2.setMinimumSize(new java.awt.Dimension(22, 100));

        dati.setPreferredSize(new java.awt.Dimension(421, 250));

        jLabel2211.setFont(jLabel2211.getFont());
        jLabel2211.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2211.setText("descrizione");

        jLabel22111.setFont(jLabel22111.getFont());
        jLabel22111.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel22111.setText("codice");

        texNume.setColumns(20);
        texNume.setDbNomeCampo("codice");
        texNume.setDbTipoCampo("");
        texNume.setmaxChars(35);

        tnxCheckBox1.setText("includi in distinta RIBA");
        tnxCheckBox1.setDbDescCampo("");
        tnxCheckBox1.setDbNomeCampo("riba");
        tnxCheckBox1.setDbTipoCampo("");
        tnxCheckBox1.setFont(tnxCheckBox1.getFont());
        tnxCheckBox1.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        texDataScad.setColumns(50);
        texDataScad.setDbNomeCampo("descrizione");
        texDataScad.setDbTipoCampo("");
        texDataScad.setmaxChars(50);

        immediata.setText("Immediata");
        immediata.setDbDescCampo("");
        immediata.setDbNomeCampo("IMMEDIATO");
        immediata.setDbTipoCampo("");
        immediata.setFont(immediata.getFont());
        immediata.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jLabel4.setText("scadenze da generare");

        gg30.setText("30gg");
        gg30.setToolTipText("1 mese");
        gg30.setDbDescCampo("");
        gg30.setDbNomeCampo("30");
        gg30.setDbTipoCampo("");
        gg30.setFont(gg30.getFont());
        gg30.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        gg30.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gg30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gg30ActionPerformed(evt);
            }
        });

        gg60.setText("60gg");
        gg60.setToolTipText("2 mesi");
        gg60.setDbDescCampo("");
        gg60.setDbNomeCampo("60");
        gg60.setDbTipoCampo("");
        gg60.setFont(gg60.getFont());
        gg60.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        gg60.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        gg90.setText("90gg");
        gg90.setToolTipText("3 mesi");
        gg90.setDbDescCampo("");
        gg90.setDbNomeCampo("90");
        gg90.setDbTipoCampo("");
        gg90.setFont(gg90.getFont());
        gg90.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        gg90.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        gg120.setText("120gg");
        gg120.setToolTipText("4 mesi");
        gg120.setDbDescCampo("");
        gg120.setDbNomeCampo("120");
        gg120.setDbTipoCampo("");
        gg120.setFont(gg120.getFont());
        gg120.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        gg120.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gg120.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gg120ActionPerformed(evt);
            }
        });

        gg150.setText("150gg");
        gg150.setToolTipText("5 mesi");
        gg150.setDbDescCampo("");
        gg150.setDbNomeCampo("150");
        gg150.setDbTipoCampo("");
        gg150.setFont(gg150.getFont());
        gg150.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        gg150.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        gg180.setText("180gg");
        gg180.setToolTipText("6 mesi");
        gg180.setDbDescCampo("");
        gg180.setDbNomeCampo("180");
        gg180.setDbTipoCampo("");
        gg180.setFont(gg180.getFont());
        gg180.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        gg180.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        finemese.setText("Fine Mese");
        finemese.setDbDescCampo("");
        finemese.setDbNomeCampo("FINEMESE");
        finemese.setDbTipoCampo("");
        finemese.setFont(finemese.getFont().deriveFont((finemese.getFont().getStyle() | java.awt.Font.ITALIC)));
        finemese.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        tnxCheckBox10.setText("banca necessaria");
        tnxCheckBox10.setToolTipText("Seleziona per avvertire in caso non vengano inserite le coordinate bancarie (solitamente solo per Riba)");
        tnxCheckBox10.setDbDescCampo("");
        tnxCheckBox10.setDbNomeCampo("coordinate_necessarie");
        tnxCheckBox10.setDbTipoCampo("");
        tnxCheckBox10.setFont(tnxCheckBox10.getFont());
        tnxCheckBox10.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jLabel2212.setFont(jLabel2212.getFont().deriveFont(jLabel2212.getFont().getSize()-1f));
        jLabel2212.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2212.setText("(solo per stampa cedolini bonifici)");

        texDataScad1.setColumns(50);
        texDataScad1.setDbNomeCampo("note_su_documenti");
        texDataScad1.setDbTipoCampo("");
        texDataScad1.setmaxChars(5000);

        tnxCheckBox11.setText("inserisci scadenze come gia' pagate");
        tnxCheckBox11.setToolTipText("Seleziona se vuoi che le scadenze generate da questo pagamento vengano segnate come già pagate (solitamente per le Riba)");
        tnxCheckBox11.setDbDescCampo("");
        tnxCheckBox11.setDbNomeCampo("flag_pagata");
        tnxCheckBox11.setDbTipoCampo("");
        tnxCheckBox11.setFont(tnxCheckBox11.getFont());
        tnxCheckBox11.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        richiestagiorno.setText("Richiedi giorno scadenza");
        richiestagiorno.setToolTipText("Inserisci il giorno del mese in cui deve cadere la scadenza");
        richiestagiorno.setDbDescCampo("");
        richiestagiorno.setDbNomeCampo("flag_richiedi_giorno");
        richiestagiorno.setDbTipoCampo("");
        richiestagiorno.setFont(richiestagiorno.getFont().deriveFont((richiestagiorno.getFont().getStyle() | java.awt.Font.ITALIC)));
        richiestagiorno.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        richiestagiorno.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        richiestagiorno.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                richiestagiornoItemStateChanged(evt);
            }
        });

        comConti.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comConti.setDbNomeCampo("id_conto");
        comConti.setDbTipoCampo("");

        jLabel2213.setFont(jLabel2213.getFont());
        jLabel2213.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2213.setText("note su documenti");

        jLabel2214.setFont(jLabel2214.getFont());
        jLabel2214.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2214.setText("Conto corrente aziendale di accredito");

        gg210.setText("210gg");
        gg210.setToolTipText("7 mesi");
        gg210.setDbDescCampo("");
        gg210.setDbNomeCampo("210");
        gg210.setDbTipoCampo("");
        gg210.setFont(gg210.getFont());
        gg210.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        gg210.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        texIdIpsoa.setColumns(10);
        texIdIpsoa.setDbNomeCampo("id_pagamento_ipsoa");

        labTipoIpsoa.setFont(labTipoIpsoa.getFont());
        labTipoIpsoa.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labTipoIpsoa.setText("Id Tipo Pagamento Ipsoa");

        iva30.setText("Iva a 30gg");
        iva30.setToolTipText("Seleziona se vuoi generare una scadenza fissa a 30gg o 30gg fine mese per l'importo dell'iva a prescindere dalle altre scadenze");
        iva30.setDbDescCampo("");
        iva30.setDbNomeCampo("iva30gg");
        iva30.setDbTipoCampo("");
        iva30.setFont(iva30.getFont().deriveFont((iva30.getFont().getStyle() | java.awt.Font.ITALIC)));
        iva30.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jLabel2.setFont(jLabel2.getFont());
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Simulazione scadenze");

        simdata.setColumns(10);
        simdata.setToolTipText("Data documento");

        labSimula.setFont(labSimula.getFont());
        labSimula.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labSimula.setText("...");

        simula.setText("simula");
        simula.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simulaActionPerformed(evt);
            }
        });

        giorno.setColumns(3);
        giorno.setToolTipText("Giorno scadenza");

        gg240.setText("240gg");
        gg240.setToolTipText("8 mesi");
        gg240.setDbDescCampo("");
        gg240.setDbNomeCampo("240");
        gg240.setDbTipoCampo("");
        gg240.setFont(gg240.getFont());
        gg240.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        gg240.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        gg270.setText("270gg");
        gg270.setToolTipText("9 mesi");
        gg270.setDbDescCampo("");
        gg270.setDbNomeCampo("270");
        gg270.setDbTipoCampo("");
        gg270.setFont(gg270.getFont());
        gg270.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        gg270.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        gg300.setText("300gg");
        gg300.setToolTipText("10 mesi");
        gg300.setDbDescCampo("");
        gg300.setDbNomeCampo("300");
        gg300.setDbTipoCampo("");
        gg300.setFont(gg300.getFont());
        gg300.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        gg300.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        gg330.setText("330gg");
        gg330.setToolTipText("11 mesi");
        gg330.setDbDescCampo("");
        gg330.setDbNomeCampo("330");
        gg330.setDbTipoCampo("");
        gg330.setFont(gg330.getFont());
        gg330.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        gg330.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        gg360.setText("360gg");
        gg360.setToolTipText("12 mesi");
        gg360.setDbDescCampo("");
        gg360.setDbNomeCampo("360");
        gg360.setDbTipoCampo("");
        gg360.setFont(gg360.getFont());
        gg360.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        gg360.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gg360.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gg360ActionPerformed(evt);
            }
        });

        gg75.setText("75gg");
        gg75.setToolTipText("98 mesi");
        gg75.setDbDescCampo("");
        gg75.setDbNomeCampo("75");
        gg75.setDbTipoCampo("");
        gg75.setFont(gg75.getFont());
        gg75.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        gg75.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        gg45.setText("45gg");
        gg45.setToolTipText("99 mese");
        gg45.setDbDescCampo("");
        gg45.setDbNomeCampo("45");
        gg45.setDbTipoCampo("");
        gg45.setFont(gg45.getFont());
        gg45.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        gg45.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        gg15.setText("15gg");
        gg15.setToolTipText("1/2 mese");
        gg15.setDbDescCampo("");
        gg15.setDbNomeCampo("15");
        gg15.setDbTipoCampo("");
        gg15.setFont(gg15.getFont());
        gg15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        gg15.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gg15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gg15ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout datiLayout = new org.jdesktop.layout.GroupLayout(dati);
        dati.setLayout(datiLayout);
        datiLayout.setHorizontalGroup(
            datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(datiLayout.createSequentialGroup()
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(datiLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel22111)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(texNume, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(datiLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel2211)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(texDataScad, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(datiLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel2213)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(texDataScad1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(datiLayout.createSequentialGroup()
                        .add(15, 15, 15)
                        .add(tnxCheckBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(15, 15, 15)
                        .add(tnxCheckBox10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(tnxCheckBox11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(datiLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(datiLayout.createSequentialGroup()
                                .add(jLabel2214)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(comConti, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(79, 79, 79)
                                .add(jLabel2212))
                            .add(datiLayout.createSequentialGroup()
                                .add(labTipoIpsoa)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(texIdIpsoa, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(datiLayout.createSequentialGroup()
                                .add(jLabel2)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(simdata, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(giorno, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(simula))))
                    .add(datiLayout.createSequentialGroup()
                        .add(15, 15, 15)
                        .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(datiLayout.createSequentialGroup()
                                .add(jLabel4)
                                .add(75, 75, 75)
                                .add(jSeparator2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 447, Short.MAX_VALUE))
                            .add(datiLayout.createSequentialGroup()
                                .add(finemese, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(18, 18, 18)
                                .add(richiestagiorno, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(0, 0, Short.MAX_VALUE))
                            .add(datiLayout.createSequentialGroup()
                                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(immediata, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(gg150, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .add(2, 2, 2)
                                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(gg180, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(gg15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                    .add(datiLayout.createSequentialGroup()
                                        .add(gg210, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                        .add(gg240, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                    .add(datiLayout.createSequentialGroup()
                                        .add(gg30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .add(gg45, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(datiLayout.createSequentialGroup()
                                        .add(10, 10, 10)
                                        .add(iva30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                    .add(datiLayout.createSequentialGroup()
                                        .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                            .add(gg270, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                            .add(gg60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                        .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                            .add(datiLayout.createSequentialGroup()
                                                .add(gg300, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                                .add(gg330, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                                .add(gg360, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                            .add(datiLayout.createSequentialGroup()
                                                .add(gg75, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                                .add(gg90, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                                .add(gg120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 157, Short.MAX_VALUE))))
                    .add(datiLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jSeparator3)
                            .add(jSeparator1)
                            .add(labSimula, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        datiLayout.linkSize(new java.awt.Component[] {jLabel2211, jLabel22111, jLabel2213}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        datiLayout.linkSize(new java.awt.Component[] {gg120, gg150, gg180, gg210, gg240, gg270, gg30, gg60, gg90}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        datiLayout.linkSize(new java.awt.Component[] {finemese, immediata}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        datiLayout.setVerticalGroup(
            datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(datiLayout.createSequentialGroup()
                .add(10, 10, 10)
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel22111)
                    .add(texNume, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(5, 5, 5)
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(texDataScad, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2211))
                .add(5, 5, 5)
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(texDataScad1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2213))
                .add(5, 5, 5)
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(tnxCheckBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(tnxCheckBox10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(tnxCheckBox11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(datiLayout.createSequentialGroup()
                        .add(22, 22, 22)
                        .add(jLabel4))
                    .add(datiLayout.createSequentialGroup()
                        .add(27, 27, 27)
                        .add(jSeparator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(immediata, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(gg90, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(gg120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(gg75, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(gg45, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(gg30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(gg60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(gg15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(gg210, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(gg240, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(gg270, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(gg300, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(gg330, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(gg360, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(gg180, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(gg150, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(3, 3, 3)
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(richiestagiorno, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(finemese, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(iva30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2214)
                    .add(comConti, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2212))
                .add(18, 18, 18)
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(labTipoIpsoa)
                    .add(texIdIpsoa, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(simdata, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(giorno, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(simula))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(labSimula)
                .addContainerGap(43, Short.MAX_VALUE))
        );

        jScrollPane2.setViewportView(dati);

        panDati.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        tabCent.addTab("dati", panDati);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBorder(null);
        jScrollPane1.setToolTipText("cliccando su una riga si ceglie la destinazione da moficare");

        griglia.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                grigliaComponentResized(evt);
            }
        });
        jScrollPane1.setViewportView(griglia);

        jPanel3.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        tabCent.addTab("elenco", jPanel3);

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

        getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
        main.getPadre().closeFrame(this);
    }//GEN-LAST:event_formInternalFrameClosing

    private void butDeleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butDeleActionPerformed

        int ret = JOptionPane.showConfirmDialog(this, "Sicuro di eliminare ?", "Attenzione", JOptionPane.YES_NO_OPTION);

        if (ret == JOptionPane.YES_OPTION) {
            this.dati.dbDelete();
            this.griglia.dbRefresh();
            rimettiRender();
        }
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
        this.dati.dbSave();
        this.griglia.dbRefresh();
        rimettiRender();
    }//GEN-LAST:event_butSaveActionPerformed

    private void butNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butNewActionPerformed
        this.dati.dbNew();
    }//GEN-LAST:event_butNewActionPerformed

    private void gg360ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gg360ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_gg360ActionPerformed

    private void simulaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_simulaActionPerformed
        Date d = CastUtils.toDateIta(simdata.getText());
        Byte g = CastUtils.toInteger0(giorno.getText()).byteValue();
        boolean fm = finemese.isSelected();
        if (!richiestagiorno.isSelected()) {
            g = null;
        }

        List<String> scads = new ArrayList();
        if (immediata.isSelected()) {
            scads.add(DateUtils.formatDate(Scadenze.calcolaData(d, 0, fm, g).getTime()));
        }
        
        //DAVID
        if (gg15.isSelected()) {
            scads.add(DateUtils.formatDate(Scadenze.calcolaData(d, 97, fm, g).getTime()));
        }
        if (gg30.isSelected() || iva30.isSelected()) {
            scads.add(DateUtils.formatDate(Scadenze.calcolaData(d, 1, fm, g).getTime()));
        }
        if (gg45.isSelected()) {
             scads.add(DateUtils.formatDate(Scadenze.calcolaData(d, 99, fm, g).getTime()));
        }
        if (gg60.isSelected()) {
            scads.add(DateUtils.formatDate(Scadenze.calcolaData(d, 2, fm, g).getTime()));
        }
        if (gg75.isSelected()) {
             scads.add(DateUtils.formatDate(Scadenze.calcolaData(d, 98, fm, g).getTime()));
        }
        if (gg90.isSelected()) {
            scads.add(DateUtils.formatDate(Scadenze.calcolaData(d, 3, fm, g).getTime()));
        }
        if (gg120.isSelected()) {
            scads.add(DateUtils.formatDate(Scadenze.calcolaData(d, 4, fm, g).getTime()));
        }
        if (gg150.isSelected()) {
            scads.add(DateUtils.formatDate(Scadenze.calcolaData(d, 5, fm, g).getTime()));
        }
        if (gg180.isSelected()) {
            scads.add(DateUtils.formatDate(Scadenze.calcolaData(d, 6, fm, g).getTime()));
        }
        if (gg210.isSelected()) {
            scads.add(DateUtils.formatDate(Scadenze.calcolaData(d, 7, fm, g).getTime()));
        }
        if (gg240.isSelected()) {
            scads.add(DateUtils.formatDate(Scadenze.calcolaData(d, 8, fm, g).getTime()));
        }
        if (gg270.isSelected()) {
            scads.add(DateUtils.formatDate(Scadenze.calcolaData(d, 9, fm, g).getTime()));
        }
        if (gg300.isSelected()) {
            scads.add(DateUtils.formatDate(Scadenze.calcolaData(d, 10, fm, g).getTime()));
        }
        if (gg330.isSelected()) {
            scads.add(DateUtils.formatDate(Scadenze.calcolaData(d, 11, fm, g).getTime()));
        }
        if (gg360.isSelected()) {
            scads.add(DateUtils.formatDate(Scadenze.calcolaData(d, 12, fm, g).getTime()));
        }

        String m = "";
        for (String s : scads) {
            m += s + " - ";
        }
        m = StringUtils.removeEnd(m, " - ");
        labSimula.setText(m);
    }//GEN-LAST:event_simulaActionPerformed

    private void richiestagiornoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_richiestagiornoItemStateChanged
        if (richiestagiorno.isSelected()) {
            giorno.setEnabled(true);
        } else {
            giorno.setEnabled(false);
        }
    }//GEN-LAST:event_richiestagiornoItemStateChanged

    private void gg120ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gg120ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_gg120ActionPerformed

    private void gg15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gg15ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_gg15ActionPerformed

    private void gg30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gg30ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_gg30ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butDele;
    private javax.swing.JButton butFind;
    private javax.swing.JButton butFirs;
    private javax.swing.JButton butLast;
    private javax.swing.JButton butNew;
    private javax.swing.JButton butNext;
    private javax.swing.JButton butPrev;
    private javax.swing.JButton butSave;
    private javax.swing.JButton butUndo;
    private tnxbeans.tnxComboField comConti;
    private tnxbeans.tnxDbPanel dati;
    private tnxbeans.tnxCheckBox finemese;
    private tnxbeans.tnxCheckBox gg120;
    private tnxbeans.tnxCheckBox gg15;
    private tnxbeans.tnxCheckBox gg150;
    private tnxbeans.tnxCheckBox gg180;
    private tnxbeans.tnxCheckBox gg210;
    private tnxbeans.tnxCheckBox gg240;
    private tnxbeans.tnxCheckBox gg270;
    private tnxbeans.tnxCheckBox gg30;
    private tnxbeans.tnxCheckBox gg300;
    private tnxbeans.tnxCheckBox gg330;
    private tnxbeans.tnxCheckBox gg360;
    private tnxbeans.tnxCheckBox gg45;
    private tnxbeans.tnxCheckBox gg60;
    private tnxbeans.tnxCheckBox gg75;
    private tnxbeans.tnxCheckBox gg90;
    private javax.swing.JTextField giorno;
    private tnxbeans.tnxDbGrid griglia;
    private tnxbeans.tnxCheckBox immediata;
    private tnxbeans.tnxCheckBox iva30;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel131;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel2211;
    private javax.swing.JLabel jLabel22111;
    private javax.swing.JLabel jLabel2212;
    private javax.swing.JLabel jLabel2213;
    private javax.swing.JLabel jLabel2214;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel labSimula;
    private javax.swing.JLabel labTipoIpsoa;
    private javax.swing.JPanel panAlto;
    private javax.swing.JPanel panDati;
    private tnxbeans.tnxCheckBox richiestagiorno;
    private javax.swing.JTextField simdata;
    private javax.swing.JButton simula;
    private javax.swing.JTabbedPane tabCent;
    private tnxbeans.tnxTextFieldFormatted texDataScad;
    private tnxbeans.tnxTextFieldFormatted texDataScad1;
    private tnxbeans.tnxTextField texIdIpsoa;
    private tnxbeans.tnxTextField texNume;
    private tnxbeans.tnxCheckBox tnxCheckBox1;
    private tnxbeans.tnxCheckBox tnxCheckBox10;
    private tnxbeans.tnxCheckBox tnxCheckBox11;
    // End of variables declaration//GEN-END:variables
    MaskFormatter mask;

    public static void main(String[] args) {
        Date d = new Date();
        d = DateUtils.getDate(2012, 3, 8);
//        System.out.println(DateUtils.formatDate(Scadenze.calcolaData(d, 2, true, (byte)0).getTime()));
//        System.out.println(DateUtils.formatDate(Scadenze.calcolaData(d, 2, true, (byte)1).getTime()));
        System.out.println(DateUtils.formatDate(Scadenze.calcolaData(d, 2, true, (byte) 10).getTime()));
        System.out.println(DateUtils.formatDate(Scadenze.calcolaData(d, 3, true, (byte) 10).getTime()));
//        System.out.println(DateUtils.formatDate(Scadenze.calcolaData(d, 2, true, (byte)29).getTime()));
    }

    private void rimettiRender() {
        griglia.getColumn("immediato").setCellRenderer(new MyCheckBoxRenderer());
        griglia.getColumn("15").setCellRenderer(new MyCheckBoxRenderer());
        griglia.getColumn("30").setCellRenderer(new MyCheckBoxRenderer());
        griglia.getColumn("45").setCellRenderer(new MyCheckBoxRenderer());
        griglia.getColumn("60").setCellRenderer(new MyCheckBoxRenderer());
        griglia.getColumn("75").setCellRenderer(new MyCheckBoxRenderer());
        griglia.getColumn("90").setCellRenderer(new MyCheckBoxRenderer());
        griglia.getColumn("120").setCellRenderer(new MyCheckBoxRenderer());
        griglia.getColumn("150").setCellRenderer(new MyCheckBoxRenderer());
        griglia.getColumn("180").setCellRenderer(new MyCheckBoxRenderer());
        griglia.getColumn("210").setCellRenderer(new MyCheckBoxRenderer());
        griglia.getColumn("240").setCellRenderer(new MyCheckBoxRenderer());
        griglia.getColumn("270").setCellRenderer(new MyCheckBoxRenderer());
        griglia.getColumn("300").setCellRenderer(new MyCheckBoxRenderer());
        griglia.getColumn("330").setCellRenderer(new MyCheckBoxRenderer());
        griglia.getColumn("360").setCellRenderer(new MyCheckBoxRenderer());
        griglia.getColumn("iva30gg").setCellRenderer(new MyCheckBoxRenderer());
        griglia.getColumn("finemese").setCellRenderer(new MyCheckBoxRenderer());
    }
    
    
    static public class MyCheckBoxRenderer extends JCheckBox implements TableCellRenderer {

          MyCheckBoxRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
          }

          public Component getTableCellRendererComponent(JTable table, Object value,
              boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
              setForeground(table.getSelectionForeground());
              //super.setBackground(table.getSelectionBackground());
              setBackground(table.getSelectionBackground());
            } else {
              setForeground(table.getForeground());
              setBackground(table.getBackground());
            }
            boolean sel = CastUtils.toBoolean(value);
            setSelected(sel);
            return this;
          }
    }       
}

