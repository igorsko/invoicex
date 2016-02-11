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
package gestioneFatture.magazzino;

import it.tnx.Db;
import gestioneFatture.*;
import it.tnx.accessoUtenti.Permesso;
import it.tnx.commons.CastUtils;
import it.tnx.commons.DbUtils;
import it.tnx.commons.MicroBench;
import it.tnx.commons.SwingUtils;
import it.tnx.commons.swing.DelayedExecutor;
import it.tnx.invoicex.Magazzino;
import it.tnx.invoicex.gui.JDialogLotti;
import it.tnx.invoicex.gui.JDialogMatricoleLotti;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import tnxbeans.DbEvent;
import tnxbeans.DbListener;
import tnxbeans.tnxDbPanel;

public class frmMovimenti
        extends javax.swing.JInternalFrame {

    DelayedExecutor delay_filtro = new DelayedExecutor(new Runnable() {
        public void run() {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    SwingUtils.mouse_wait();
                    System.out.println("*** dbrefresh articolo");
                    refresh();
                    SwingUtils.mouse_def();
                }
            });
        }
    }, 250);

    /** Creates new form frmDati_blank */
    public frmMovimenti() {
        initComponents();

        MicroBench mb = new MicroBench();
        mb.start();

        try {
            long conta = (Long) DbUtils.getObject(Db.getConn(), "select count(*) from articoli");
            if (conta > 10000) {
                System.err.println("imposto combo articoli come lazy percè record count = " + conta);
                comCodiArti.setLazy(true);
            }
        } catch (Exception ex) {
            Logger.getLogger(frmMovimenti.class.getName()).log(Level.SEVERE, null, ex);
        }

        mb.out("frmMovimenti mb controllo per lazy");

        griglia.setNoTnxResize(true);

        //apro la combo pagamenti
        this.comCausale.dbOpenList(Db.getConn(), "select descrizione, codice from tipi_causali_magazzino order by codice", null, false);
        mb.out("frmMovimenti mb due openlist piccoli 1");

        this.comCodiArti.dbOpenList(Db.getConn(), "select descrizione, codice from articoli order by descrizione", null, false);
        mb.out("frmMovimenti mb due openlist piccoli 2");

        //associo il panel ai dati
        this.dati.dbNomeTabella = "movimenti_magazzino";

        Vector chiave = new Vector();
        chiave.add("id");
        this.dati.dbChiave = chiave;
        this.dati.butSave = this.butSave;
        this.dati.butUndo = this.butUndo;
        this.dati.butFind = this.butFind;
        this.dati.butNew  = this.butNew;
        this.dati.butDele = this.butDele;
        this.dati.tipo_permesso = Permesso.PERMESSO_MAGAZZINO;
        
        this.dati.dbChiaveAutoInc = true;
        this.dati.dbOpen(Db.getConn(), "select * from movimenti_magazzino order by id desc limit 1");
        this.dati.dbRefresh();

        mb.out("frmMovimenti mb apertura dati");

        //apro la griglia
        //this.griglia.dbEditabile = true;
        this.griglia.dbChiave = chiave;
        this.griglia.flagUsaThread = false;

        java.util.Hashtable colsWidthPerc = new java.util.Hashtable();

//        griglia.dbOpen(Db.getConn(), "select m.*, a.codice_fornitore, a.codice_a_barre from movimenti_magazzino m left join articoli a on m.articolo = a.codice order by id desc", Db.INSTANCE, true);
        refresh ();

        griglia.dbPanel = this.dati;

        mb.out("frmMovimenti mb apertura griglia");

        dati.addDbListener(new DbListener() {

            public void statusFired(DbEvent event) {
                if (event.getStatus() == tnxDbPanel.STATUS_REFRESHING) {
                    //carico dati articolo
                    labDatiArticolo.setText("");
                    List<Map> l;
                    try {
                        l = DbUtils.getListMap(Db.getConn(), "select * from articoli where codice = '" + Db.aa(texCodiArti.getText()) + "'");
                        String da = "Cod. fornitore " + l.get(0).get("codice_fornitore") + " / Cod. a barre " + l.get(0).get("codice_a_barre");
                        labDatiArticolo.setText(da);
                    } catch (Exception ex) {
                        Logger.getLogger(frmMovimenti.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        dati.griglia = griglia;
        dati.dbRefresh();

        mb.out("frmMovimenti mb refresh dati");

    }

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
        jButton2 = new javax.swing.JButton();
        butRefresh = new javax.swing.JButton();
        jToolBar2 = new javax.swing.JToolBar();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        filtro_articolo = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jLabel7 = new javax.swing.JLabel();
        filtro_barre = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jLabel8 = new javax.swing.JLabel();
        filtro_fornitore = new javax.swing.JTextField();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        jButton1 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        tabCent = new javax.swing.JTabbedPane();
        dati = new tnxbeans.tnxDbPanel();
        texQuantita = new tnxbeans.tnxTextField();
        texNote = new tnxbeans.tnxTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel2111 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel224 = new javax.swing.JLabel();
        texData = new tnxbeans.tnxTextField();
        comCausale = new tnxbeans.tnxComboField();
        texDeposito = new tnxbeans.tnxTextField();
        texCodiArti = new tnxbeans.tnxTextField();
        comCodiArti = new tnxbeans.tnxComboField();
        texCausale = new tnxbeans.tnxTextField();
        texId = new tnxbeans.tnxTextField();
        jLabel3 = new javax.swing.JLabel();
        texMatricola = new tnxbeans.tnxTextField();
        jLabel4 = new javax.swing.JLabel();
        texLotto = new tnxbeans.tnxTextField();
        jLabel2112 = new javax.swing.JLabel();
        tnxTextField1 = new tnxbeans.tnxTextField();
        jLabel27 = new javax.swing.JLabel();
        labDatiArticolo = new javax.swing.JLabel();
        panElen = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        griglia = new tnxbeans.tnxDbGrid();
        jPanel2 = new javax.swing.JPanel();
        butUndo = new javax.swing.JButton();
        butSave = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Movimenti magazzino");
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
            }
        });

        panAlto.setLayout(new java.awt.BorderLayout());

        jToolBar1.setRollover(true);

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

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/document-print.png"))); // NOI18N
        jButton2.setText("Stampa");
        jButton2.setBorderPainted(false);
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton2.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton2);

        butRefresh.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        butRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/view-refresh.png"))); // NOI18N
        butRefresh.setToolTipText("Aggiorna l'elenco dei documenti");
        butRefresh.setBorderPainted(false);
        butRefresh.setFocusable(false);
        butRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        butRefresh.setIconTextGap(2);
        butRefresh.setMargin(new java.awt.Insets(2, 2, 2, 2));
        butRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        butRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butRefreshActionPerformed(evt);
            }
        });
        jToolBar1.add(butRefresh);

        panAlto.add(jToolBar1, java.awt.BorderLayout.NORTH);

        jToolBar2.setRollover(true);

        jLabel5.setText(" Filtra per   ");
        jToolBar2.add(jLabel5);

        jLabel6.setText("Cod. Articolo ");
        jToolBar2.add(jLabel6);

        filtro_articolo.setColumns(10);
        filtro_articolo.setMaximumSize(new java.awt.Dimension(80, 2147483647));
        filtro_articolo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                filtro_articoloKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filtro_articoloKeyReleased(evt);
            }
        });
        jToolBar2.add(filtro_articolo);
        jToolBar2.add(jSeparator1);

        jLabel7.setText("Cod. a barre ");
        jToolBar2.add(jLabel7);

        filtro_barre.setColumns(15);
        filtro_barre.setMaximumSize(new java.awt.Dimension(150, 2147483647));
        filtro_barre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filtro_barreKeyReleased(evt);
            }
        });
        jToolBar2.add(filtro_barre);
        jToolBar2.add(jSeparator2);

        jLabel8.setText("Cod. Fornitore ");
        jToolBar2.add(jLabel8);

        filtro_fornitore.setColumns(10);
        filtro_fornitore.setMaximumSize(new java.awt.Dimension(80, 2147483647));
        filtro_fornitore.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filtro_fornitoreKeyReleased(evt);
            }
        });
        jToolBar2.add(filtro_fornitore);
        jToolBar2.add(jSeparator3);

        jButton1.setText("Azzera filtri");
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBar2.add(jButton1);

        panAlto.add(jToolBar2, java.awt.BorderLayout.SOUTH);

        jLabel9.setText("<html><font color=red>Nota Bene:</font> Per inserire l'esistenza iniziale o rettificare una giacenza, prima di tutto cliccare su <b>'Nuovo'</b><br> per inserire un nuovo movimento e non modificare il movimento visualizzato !</html>");
        jLabel9.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 6, 6, 6));
        panAlto.add(jLabel9, java.awt.BorderLayout.CENTER);

        getContentPane().add(panAlto, java.awt.BorderLayout.NORTH);

        tabCent.setName("dati"); // NOI18N

        texQuantita.setColumns(10);
        texQuantita.setText("quantita");
        texQuantita.setDbNomeCampo("quantita");
        texQuantita.setDbTipoCampo("numerico");

        texNote.setText("note");
        texNote.setDbNomeCampo("note");

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("quantita");

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel25.setText("causale");

        jLabel2111.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2111.setText("data");

        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel26.setText("articolo");

        jLabel224.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel224.setText("note");

        texData.setText("data");
        texData.setDbDefault("CURRENT");
        texData.setDbNomeCampo("data");
        texData.setDbTipoCampo("data");

        comCausale.setDbNomeCampo("causale");
        comCausale.setDbRiempire(false);
        comCausale.setDbSalvare(false);
        comCausale.setDbTextAbbinato(texCausale);
        comCausale.setDbTrovaMentreScrive(true);

        texDeposito.setBackground(new java.awt.Color(204, 102, 255));
        texDeposito.setText("0");
        texDeposito.setDbNomeCampo("deposito");
        texDeposito.setDbRiempire(false);
        texDeposito.setDbTipoCampo("INTEGER");
        texDeposito.setVisible(false);

        texCodiArti.setColumns(10);
        texCodiArti.setText("codice_articolo");
        texCodiArti.setToolTipText("");
        texCodiArti.setDbComboAbbinata(comCodiArti);
        texCodiArti.setDbDescCampo("");
        texCodiArti.setDbNomeCampo("articolo");
        texCodiArti.setDbTipoCampo("");

        comCodiArti.setToolTipText("");
        comCodiArti.setDbDescCampo("");
        comCodiArti.setDbNomeCampo("descrizione");
        comCodiArti.setDbRiempire(false);
        comCodiArti.setDbSalvare(false);
        comCodiArti.setDbTextAbbinato(texCodiArti);
        comCodiArti.setDbTrovaMentreScrive(true);

        texCausale.setBackground(new java.awt.Color(204, 102, 255));
        texCausale.setText("0");
        texCausale.setDbComboAbbinata(comCausale);
        texCausale.setDbNomeCampo("causale");
        texCausale.setDbTipoCampo("NUMERIC");
        texCausale.setVisible(false);

        texId.setBackground(new java.awt.Color(204, 102, 255));
        texId.setText("0");
        texId.setDbNomeCampo("id");
        texId.setDbSalvare(false);
        texId.setDbTipoCampo("INTEGER");
        texId.setVisible(false);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("matricola");

        texMatricola.setColumns(10);
        texMatricola.setText("matricola");
        texMatricola.setDbNomeCampo("matricola");
        texMatricola.setDbTipoCampo("");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("lotto");

        texLotto.setColumns(10);
        texLotto.setText("lotto");
        texLotto.setDbNomeCampo("lotto");
        texLotto.setDbTipoCampo("");

        jLabel2112.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2112.setText("id");

        tnxTextField1.setEditable(false);
        tnxTextField1.setColumns(5);
        tnxTextField1.setDbNomeCampo("id");

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel27.setText("dati articolo");

        labDatiArticolo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labDatiArticolo.setText("...");

        org.jdesktop.layout.GroupLayout datiLayout = new org.jdesktop.layout.GroupLayout(dati);
        dati.setLayout(datiLayout);
        datiLayout.setHorizontalGroup(
            datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(datiLayout.createSequentialGroup()
                .addContainerGap()
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(datiLayout.createSequentialGroup()
                        .add(jLabel2111, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(texData, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 95, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel2112, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(tnxTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(datiLayout.createSequentialGroup()
                        .add(jLabel26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(texCodiArti, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(comCodiArti, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 370, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(datiLayout.createSequentialGroup()
                        .add(jLabel224, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(texNote, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(datiLayout.createSequentialGroup()
                        .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(texMatricola, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(datiLayout.createSequentialGroup()
                        .add(jLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(datiLayout.createSequentialGroup()
                                .add(texId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(texCausale, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(texDeposito, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(texLotto, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(datiLayout.createSequentialGroup()
                        .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(texQuantita, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(comCausale, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(datiLayout.createSequentialGroup()
                        .add(jLabel27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(labDatiArticolo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(290, Short.MAX_VALUE))
        );
        datiLayout.setVerticalGroup(
            datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(datiLayout.createSequentialGroup()
                .addContainerGap()
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2111, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(texData, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2112, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(tnxTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(texCodiArti, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(comCodiArti, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(8, 8, 8)
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(labDatiArticolo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(texQuantita, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(comCausale, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel224, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(texNote, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(texMatricola, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(texLotto, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(texId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(texCausale, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(texDeposito, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        tabCent.addTab("dati", dati);

        panElen.setName("elenco"); // NOI18N
        panElen.setLayout(new java.awt.BorderLayout());

        griglia.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        griglia.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                grigliaMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(griglia);

        panElen.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        tabCent.addTab("elenco", panElen);

        getContentPane().add(tabCent, java.awt.BorderLayout.CENTER);
        tabCent.getAccessibleContext().setAccessibleName("Dati");

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
            final Object inizio_mysql = Db.getCurrentTimestamp();
            
            final int[] rows = griglia.getSelectedRows();
            Thread t = new Thread() {
                @Override
                public void run() {
                    for (int i = rows.length-1; i >= 0; i--) {
                        final int final_i = i;
                        griglia.getSelectionModel().setSelectionInterval(rows[final_i], rows[final_i]);
                        griglia.dbSelezionaRiga();
                        Magazzino.preDelete("where id = " + texId.getText());
                        System.out.println("sel:" + rows[final_i]);
                        dati.dbDelete();
                    }
                    griglia.dbRefresh();
                    griglia.dbSelezionaRiga();
                    main.events.fireInvoicexEventMagazzino(this, inizio_mysql);
                }
            };
            t.start();
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
        Object inizio_mysql = Db.getCurrentTimestamp();

        this.dati.dbSave();

        //chiedo lotti ?
        String codice = texCodiArti.getText();
        try {
            String lotti = (String) DbUtils.getObject(Db.getConn(), "select gestione_lotti from articoli where codice = '" + Db.aa(codice) + "'");
            String matricole = (String) DbUtils.getObject(Db.getConn(), "select gestione_matricola from articoli where codice = '" + Db.aa(codice) + "'");
            if (lotti.equalsIgnoreCase("S") && matricole.equalsIgnoreCase("S") ) {
                JDialogMatricoleLotti dialog = new JDialogMatricoleLotti(main.getPadre(), true, false);
                dialog.setLocationRelativeTo(null);
                String tipo = "C";
                if (comCausale.getSelectedItem().toString().toLowerCase().startsWith("scaric")) {
                    tipo = "S";
                }
                dialog.init(tipo, CastUtils.toDouble0(texQuantita.getText()), codice, "movimenti_magazzino", CastUtils.toInteger0(texId.getText()), texLotto.getText());
                dialog.setVisible(true);
                System.out.println("lotti ok");
            } else if (lotti.equalsIgnoreCase("S")) {
                JDialogLotti dialog = new JDialogLotti(main.getPadre(), true, false);
                dialog.setLocationRelativeTo(null);
                String tipo = "C";
                if (comCausale.getSelectedItem().toString().toLowerCase().startsWith("scaric")) {
                    tipo = "S";
                }
                dialog.init(tipo, CastUtils.toDouble0(texQuantita.getText()), codice, "movimenti_magazzino", CastUtils.toInteger0(texId.getText()), texLotto.getText());
                dialog.setVisible(true);
                griglia.dbRefresh();
                griglia.dbSelezionaRiga();
                //dati.dbRefresh();
                System.out.println("lotti ok");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        main.events.fireInvoicexEventMagazzino(this, inizio_mysql);
        

//        griglia.dbRefresh();
//        griglia.dbSelezionaRiga();
//        dati.dbRefresh();

    }//GEN-LAST:event_butSaveActionPerformed

    private void butNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butNewActionPerformed
        tabCent.setSelectedIndex(0);
        dati.dbNew();
        comCausale.setSelectedIndex(1);
        texCodiArti.requestFocus();
    }//GEN-LAST:event_butNewActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        it.tnx.PrintUtilities.printComponent(this.dati);
}//GEN-LAST:event_jButton2ActionPerformed

    private void butRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butRefreshActionPerformed
        griglia.dbRefresh();
}//GEN-LAST:event_butRefreshActionPerformed

    private void filtro_articoloKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_filtro_articoloKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_filtro_articoloKeyPressed

    private void filtro_articoloKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_filtro_articoloKeyReleased
        delay_filtro.update();
    }//GEN-LAST:event_filtro_articoloKeyReleased

    private void filtro_barreKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_filtro_barreKeyReleased
        delay_filtro.update();
    }//GEN-LAST:event_filtro_barreKeyReleased

    private void filtro_fornitoreKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_filtro_fornitoreKeyReleased
        delay_filtro.update();
    }//GEN-LAST:event_filtro_fornitoreKeyReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        filtro_articolo.setText("");
        filtro_barre.setText("");
        filtro_fornitore.setText("");
        delay_filtro.update();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void grigliaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_grigliaMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_grigliaMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butDele;
    private javax.swing.JButton butFind;
    private javax.swing.JButton butFirs;
    private javax.swing.JButton butLast;
    private javax.swing.JButton butNew;
    private javax.swing.JButton butNext;
    private javax.swing.JButton butPrev;
    private javax.swing.JButton butRefresh;
    private javax.swing.JButton butSave;
    private javax.swing.JButton butUndo;
    private tnxbeans.tnxComboField comCausale;
    private tnxbeans.tnxComboField comCodiArti;
    private tnxbeans.tnxDbPanel dati;
    private javax.swing.JTextField filtro_articolo;
    private javax.swing.JTextField filtro_barre;
    private javax.swing.JTextField filtro_fornitore;
    private tnxbeans.tnxDbGrid griglia;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel131;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel2111;
    private javax.swing.JLabel jLabel2112;
    private javax.swing.JLabel jLabel224;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JLabel labDatiArticolo;
    private javax.swing.JPanel panAlto;
    private javax.swing.JPanel panElen;
    private javax.swing.JTabbedPane tabCent;
    private tnxbeans.tnxTextField texCausale;
    private tnxbeans.tnxTextField texCodiArti;
    private tnxbeans.tnxTextField texData;
    private tnxbeans.tnxTextField texDeposito;
    private tnxbeans.tnxTextField texId;
    private tnxbeans.tnxTextField texLotto;
    private tnxbeans.tnxTextField texMatricola;
    private tnxbeans.tnxTextField texNote;
    private tnxbeans.tnxTextField texQuantita;
    private tnxbeans.tnxTextField tnxTextField1;
    // End of variables declaration//GEN-END:variables

    public void refresh() {
        String sql = "";
        sql += "select m.id"
                + ", m.data"
                + ", cau.descrizione as causale"
                + ", m.articolo"
                + ", if (cau.segno = -1 , -m.quantita, m.quantita) as quantita"
                + ", m.note"
                + ", cast(concat(IF(m.da_tipo_fattura = 7, 'Scontr.', "
                + "         IF(m.da_tabella = 'test_fatt', 'Fatt. Vend.', "
                + "         IF(m.da_tabella = 'test_ddt', 'DDT Vend.',"
                + "         IF(m.da_tabella = 'test_fatt_acquisto', 'Fatt. Acq.',"
                + "         IF(m.da_tabella = 'test_ddt_acquisto', 'DDT Acq.', m.da_tabella)))))"
                + "        , da_serie, ' ', da_numero, '/', da_anno, ' - [ID ', da_id, ']') as CHAR)as origine"
//                + ", m.da_tabella"
//                + ", m.da_anno"
//                + ", m.da_serie"
//                + ", m.da_numero"
//                + ", m.da_id"
                + ", m.matricola"
                + ", m.lotto"
                + ", a.codice_fornitore, a.codice_a_barre from movimenti_magazzino m left join articoli a on m.articolo = a.codice"
                + " left join tipi_causali_magazzino cau on m.causale = cau.codice";
        sql += " where 1 = 1";
        if (filtro_articolo.getText().length() > 0) {
            sql += " and m.articolo like '%" + Db.aa(filtro_articolo.getText()) + "%'";
        }
        if (filtro_barre.getText().length() > 0) {
            sql += " and a.codice_a_barre like '%" + Db.aa(filtro_barre.getText()) + "%'";
        }
        if (filtro_fornitore.getText().length() > 0) {
            sql += " and a.codice_fornitore like '%" + Db.aa(filtro_fornitore.getText()) + "%'";
        }
        sql += " order by id desc";
        System.out.println("sql movimenti: " + sql);
        griglia.dbOpen(Db.getConn(), sql, Db.INSTANCE, true);
        griglia.dbSelezionaRiga();
    }

}
