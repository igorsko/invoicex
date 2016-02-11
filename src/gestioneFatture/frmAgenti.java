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
import java.sql.ResultSet;

import java.util.Vector;

import javax.swing.JOptionPane;

public class frmAgenti
    extends javax.swing.JInternalFrame {

    tnxbeans.tnxComboField comboToRefresh;

    /** Creates new form frmDati_blank */
    public frmAgenti() {
        initComponents();

        //associo il panel ai dati
        this.dati.dbNomeTabella = "agenti";

        Vector chiave = new Vector();
        chiave.add("id");
        this.dati.dbChiave = chiave;
        this.dati.butSave = this.butSave;
        this.dati.butUndo = this.butUndo;
        this.dati.butFind = this.butFind;
        this.dati.butNew  = this.butNew;
        this.dati.butDele = this.butDele;
        this.dati.tipo_permesso = it.tnx.accessoUtenti.Permesso.PERMESSO_AGENTI;

        this.dati.dbOpen(Db.getConn(), "select * from agenti order by nome");
        this.dati.dbRefresh();

        //apro la griglia
        //this.griglia.dbEditabile = true;
        this.griglia.dbChiave = chiave;
        this.griglia.flagUsaThread = false;

        java.util.Hashtable colsWidthPerc = new java.util.Hashtable();
        colsWidthPerc.put("id", new Double(15));
        colsWidthPerc.put("nome", new Double(35));
        colsWidthPerc.put("telefono", new Double(35));
        colsWidthPerc.put("percentuale", new Double(15));
        this.griglia.columnsSizePerc = colsWidthPerc;
        this.griglia.dbOpen(Db.getConn(), "select id, nome, telefono, percentuale from agenti order by nome");
        this.griglia.dbPanel = this.dati;

        iniFileProp fileIni = main.fileIni;

        if(fileIni.getValueBoolean("pref", "provvigioniAutomatiche", false)){
            this.jLabel21.setVisible(false);
            this.texPercentuale.setVisible(false);
            
            if(fileIni.getValueBoolean("pref", "provvigioniPercentualeAuto", false)){
                lblDescPerc.setVisible(true);

                this.lblSoglia1.setVisible(false);
                this.lblSoglia2.setVisible(false);
                this.lblSoglia3.setVisible(false);
                this.lblSoglia4.setVisible(false);
                this.lblSoglia5.setVisible(false);
                this.texPercentualeSoglia1.setVisible(false);
                this.texPercentualeSoglia2.setVisible(false);
                this.texPercentualeSoglia3.setVisible(false);
                this.texPercentualeSoglia4.setVisible(false);
                this.texPercentualeSoglia5.setVisible(false);
            } else {
                this.lblDescPerc.setText("Inserisci le commissioni");
                
                this.lblSoglia1.setVisible(true);
                this.lblSoglia2.setVisible(true);
                this.lblSoglia3.setVisible(true);
                this.lblSoglia4.setVisible(true);
                this.lblSoglia5.setVisible(true);
                this.texPercentualeSoglia1.setVisible(true);
                this.texPercentualeSoglia2.setVisible(true);
                this.texPercentualeSoglia3.setVisible(true);
                this.texPercentualeSoglia4.setVisible(true);
                this.texPercentualeSoglia5.setVisible(true);
            }
        } else {
            lblDescPerc.setVisible(false);

            this.lblSoglia1.setVisible(false);
            this.lblSoglia2.setVisible(false);
            this.lblSoglia3.setVisible(false);
            this.lblSoglia4.setVisible(false);
            this.lblSoglia5.setVisible(false);
            this.texPercentualeSoglia1.setVisible(false);
            this.texPercentualeSoglia2.setVisible(false);
            this.texPercentualeSoglia3.setVisible(false);
            this.texPercentualeSoglia4.setVisible(false);
            this.texPercentualeSoglia5.setVisible(false);
        }

        //sistemo alcune cose
        //this.comTipo.addItem("CL");
        //this.comTipo.addItem("FO");
        this.dati.dbRefresh();
    }

    public void addNew() {
        butNewActionPerformed(null);
        this.tabCent.setSelectedIndex(0);

        //this.show();
    }

    public void addNew(tnxbeans.tnxComboField combo) {
        addNew();
        this.comboToRefresh = combo;
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
        butStampaElenco = new javax.swing.JButton();
        tabCent = new javax.swing.JTabbedPane();
        panDati = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        dati = new tnxbeans.tnxDbPanel();
        texRagiSoci = new tnxbeans.tnxTextField();
        texIndi = new tnxbeans.tnxTextField();
        texProv = new tnxbeans.tnxTextField();
        texLoca = new tnxbeans.tnxTextField();
        texTele = new tnxbeans.tnxTextField();
        texCap = new tnxbeans.tnxTextField();
        texNote = new tnxbeans.tnxTextField();
        jLabel231 = new javax.swing.JLabel();
        jLabel212 = new javax.swing.JLabel();
        jLabel222 = new javax.swing.JLabel();
        jLabel232 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel241 = new javax.swing.JLabel();
        jLabel2111 = new javax.swing.JLabel();
        jLabel2211 = new javax.swing.JLabel();
        texCodi = new tnxbeans.tnxTextField();
        jLabel2 = new javax.swing.JLabel();
        texEmail = new tnxbeans.tnxTextField();
        lblDescPerc = new javax.swing.JLabel();
        texPercentuale = new tnxbeans.tnxTextField();
        jLabel21 = new javax.swing.JLabel();
        texPercentualeSoglia1 = new tnxbeans.tnxTextField();
        lblSoglia1 = new javax.swing.JLabel();
        texPercentualeSoglia2 = new tnxbeans.tnxTextField();
        texPercentualeSoglia3 = new tnxbeans.tnxTextField();
        texPercentualeSoglia4 = new tnxbeans.tnxTextField();
        texPercentualeSoglia5 = new tnxbeans.tnxTextField();
        lblSoglia5 = new javax.swing.JLabel();
        lblSoglia4 = new javax.swing.JLabel();
        lblSoglia3 = new javax.swing.JLabel();
        lblSoglia2 = new javax.swing.JLabel();
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
        setTitle("Gestione Agenti");
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        panAlto.setLayout(new java.awt.BorderLayout());

        butNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/document-new.png"))); // NOI18N
        butNew.setText("Nuovo");
        butNew.setBorderPainted(false);
        butNew.setMargin(new java.awt.Insets(2, 2, 2, 2));
        butNew.setRolloverEnabled(true);
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
        butDele.setRolloverEnabled(true);
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
        butFind.setRolloverEnabled(true);
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
        butFirs.setRolloverEnabled(true);
        butFirs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butFirsActionPerformed(evt);
            }
        });
        jToolBar1.add(butFirs);

        butPrev.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/go-previous.png"))); // NOI18N
        butPrev.setBorderPainted(false);
        butPrev.setMargin(new java.awt.Insets(2, 2, 2, 2));
        butPrev.setRolloverEnabled(true);
        butPrev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butPrevActionPerformed(evt);
            }
        });
        jToolBar1.add(butPrev);

        butNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/go-next.png"))); // NOI18N
        butNext.setBorderPainted(false);
        butNext.setMargin(new java.awt.Insets(2, 2, 2, 2));
        butNext.setRolloverEnabled(true);
        butNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butNextActionPerformed(evt);
            }
        });
        jToolBar1.add(butNext);

        butLast.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/go-last.png"))); // NOI18N
        butLast.setBorderPainted(false);
        butLast.setMargin(new java.awt.Insets(2, 2, 2, 2));
        butLast.setRolloverEnabled(true);
        butLast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butLastActionPerformed(evt);
            }
        });
        jToolBar1.add(butLast);

        butStampaElenco.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/document-print.png"))); // NOI18N
        butStampaElenco.setText("Stampa elenco");
        butStampaElenco.setBorderPainted(false);
        butStampaElenco.setMargin(new java.awt.Insets(2, 2, 2, 2));
        butStampaElenco.setRolloverEnabled(true);
        butStampaElenco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butStampaElencoActionPerformed(evt);
            }
        });
        jToolBar1.add(butStampaElenco);

        panAlto.add(jToolBar1, java.awt.BorderLayout.CENTER);

        getContentPane().add(panAlto, java.awt.BorderLayout.NORTH);

        tabCent.setName("dati"); // NOI18N

        panDati.setName("dati"); // NOI18N
        panDati.setLayout(new java.awt.BorderLayout());

        dati.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        texRagiSoci.setText("nome");
        texRagiSoci.setDbNomeCampo("nome");
        texRagiSoci.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                texRagiSociActionPerformed(evt);
            }
        });
        dati.add(texRagiSoci, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 10, 240, 20));

        texIndi.setText("indirizzo");
        texIndi.setDbNomeCampo("indirizzo");
        dati.add(texIndi, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 35, 340, -1));

        texProv.setText("provincia");
        texProv.setDbNomeCampo("provincia");
        texProv.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                texProvFocusLost(evt);
            }
        });
        dati.add(texProv, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 60, 20, -1));

        texLoca.setText("localita");
        texLoca.setDbNomeCampo("localita");
        dati.add(texLoca, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 60, 218, -1));

        texTele.setText("telefono");
        texTele.setDbNomeCampo("telefono");
        dati.add(texTele, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 85, 145, -1));

        texCap.setText("cap");
        texCap.setDbNomeCampo("cap");
        texCap.setDbTipoCampo("");
        dati.add(texCap, new org.netbeans.lib.awtextra.AbsoluteConstraints(305, 60, 40, -1));

        texNote.setText("note");
        texNote.setDbNomeCampo("note");
        dati.add(texNote, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 110, 340, 40));

        jLabel231.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel231.setText("telefono");
        dati.add(jLabel231, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 85, 40, 20));

        jLabel212.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel212.setText("cap:");
        dati.add(jLabel212, new org.netbeans.lib.awtextra.AbsoluteConstraints(282, 60, 22, 20));

        jLabel222.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel222.setText("località");
        dati.add(jLabel222, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 40, 20));

        jLabel232.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel232.setText("prov.:");
        dati.add(jLabel232, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 60, 30, 20));

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel25.setText("indirizzo");
        dati.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 35, 50, 20));

        jLabel241.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel241.setText("note");
        dati.add(jLabel241, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 40, 20));

        jLabel2111.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2111.setText("codice");
        dati.add(jLabel2111, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 40, 20));

        jLabel2211.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2211.setText("nome");
        dati.add(jLabel2211, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 10, 30, 20));

        texCodi.setText("id");
        texCodi.setDbNomeCampo("id");
        dati.add(texCodi, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, 50, 20));

        jLabel2.setText("Email");
        dati.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 85, -1, 20));

        texEmail.setDbNomeCampo("email");
        dati.add(texEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 85, 160, -1));

        lblDescPerc.setText("Le commissioni sono automatiche");
        dati.add(lblDescPerc, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 155, 180, 20));

        texPercentuale.setDbNomeCampo("percentuale");
        texPercentuale.setDbTipoCampo("numerico");
        texPercentuale.setmaxChars(5);
        dati.add(texPercentuale, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 155, 130, -1));

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel21.setText("commissione %");
        dati.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 155, 80, 20));

        texPercentualeSoglia1.setDbNomeCampo("percentuale_soglia_1");
        texPercentualeSoglia1.setDbTipoCampo("numerico");
        dati.add(texPercentualeSoglia1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 180, 130, -1));

        lblSoglia1.setText("1° Soglia:");
        dati.add(lblSoglia1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 180, 50, 20));

        texPercentualeSoglia2.setDbNomeCampo("percentuale_soglia_2");
        texPercentualeSoglia2.setDbTipoCampo("numerico");
        dati.add(texPercentualeSoglia2, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 180, 130, -1));

        texPercentualeSoglia3.setDbNomeCampo("percentuale_soglia_3");
        texPercentualeSoglia3.setDbTipoCampo("numerico");
        dati.add(texPercentualeSoglia3, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 205, 130, -1));

        texPercentualeSoglia4.setDbNomeCampo("percentuale_soglia_4");
        texPercentualeSoglia4.setDbTipoCampo("numerico");
        dati.add(texPercentualeSoglia4, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 205, 130, -1));

        texPercentualeSoglia5.setDbNomeCampo("percentuale_soglia_5");
        texPercentualeSoglia5.setDbTipoCampo("numerico");
        dati.add(texPercentualeSoglia5, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 230, 130, -1));

        lblSoglia5.setText("5° Soglia:");
        dati.add(lblSoglia5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 230, 50, 20));

        lblSoglia4.setText("4° Soglia:");
        dati.add(lblSoglia4, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 205, 50, 20));

        lblSoglia3.setText("3° Soglia:");
        dati.add(lblSoglia3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 205, 50, 20));

        lblSoglia2.setText("2° Soglia:");
        dati.add(lblSoglia2, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 180, 50, 20));

        jScrollPane2.setViewportView(dati);

        panDati.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        tabCent.addTab("dati", panDati);

        panElen.setName("elenco"); // NOI18N
        panElen.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setViewportView(griglia);

        panElen.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        tabCent.addTab("elenco", panElen);

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

    private void butStampaElencoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butStampaElencoActionPerformed
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

        int[] headerWidth = { 5, 20, 10, 10 };
        String nomeFilePdf = this.griglia.stampaTabella("Elenco AGENTI", headerWidth);
        Util.start(nomeFilePdf);
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_butStampaElencoActionPerformed

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

    private void texRagiSociActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texRagiSociActionPerformed

        // Add your handling code here:
    }//GEN-LAST:event_texRagiSociActionPerformed

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        main.getPadre().closeFrame(this);
    }//GEN-LAST:event_formInternalFrameClosing

    private void butDeleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butDeleActionPerformed

        int ret = JOptionPane.showConfirmDialog(this, "Sicuro di eliminare ?", "Attenzione", JOptionPane.YES_NO_OPTION);

        if (ret == JOptionPane.YES_OPTION) {
            this.dati.dbDelete();
            this.griglia.dbRefresh();
            this.griglia.dbSelezionaRiga();
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

        //controlli vari

        /*
        
        
        if (main.getPersonal().equals(main.PERSONAL_TNX) || main.getPersonal().equals(main.PERSONAL_GIANNI)) {
        
        
          String sql = "select * from tipi_listino";
        
        
          sql += " where codice = " + this.comList.getSelectedKey().toString();
        
        
          ResultSet resu = Db.openResultSet(sql);      
        
        
          try {
        
        
            if (resu.next() == false) {
        
        
              int ret = javax.swing.JOptionPane.showConfirmDialog(this, "Il listino deve essere selezionato, continuare comunque?", "Attenzione", javax.swing.JOptionPane.YES_NO_OPTION);
        
        
              if (ret == javax.swing.JOptionPane.NO_OPTION) return;
        
        
            }
        
        
          } catch (Exception err) {err.printStackTrace();}
        
        
        }
        
        
            
        
        
         */
        this.dati.dbSave();
        this.griglia.dbRefresh();

        if (this.comboToRefresh != null) {
            this.comboToRefresh.dbRefreshItems();
            this.dispose();
        }
    }//GEN-LAST:event_butSaveActionPerformed

    private void butNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butNewActionPerformed
        this.dati.dbNew();

        java.sql.Statement stat;
        ResultSet resu;

        //apre il resultset per ultimo +1
        try {
            stat = Db.getConn().createStatement();

            String sql = "select id from agenti order by id desc limit 1";
            resu = stat.executeQuery(sql);

            if (resu.next() == true) {
                this.texCodi.setText(String.valueOf(resu.getInt(1) + 1));
            } else {
                this.texCodi.setText("1");
            }

            this.texPercentuale.setText("10");
        } catch (Exception err) {
            javax.swing.JOptionPane.showMessageDialog(null, err.toString());
        }
    }//GEN-LAST:event_butNewActionPerformed

    private void texProvFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texProvFocusLost
        texProv.setText(texProv.getText().toUpperCase());
    }//GEN-LAST:event_texProvFocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butDele;
    private javax.swing.JButton butFind;
    private javax.swing.JButton butFirs;
    private javax.swing.JButton butLast;
    private javax.swing.JButton butNew;
    private javax.swing.JButton butNext;
    private javax.swing.JButton butPrev;
    private javax.swing.JButton butSave;
    private javax.swing.JButton butStampaElenco;
    private javax.swing.JButton butUndo;
    private tnxbeans.tnxDbPanel dati;
    private tnxbeans.tnxDbGrid griglia;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel131;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel2111;
    private javax.swing.JLabel jLabel212;
    private javax.swing.JLabel jLabel2211;
    private javax.swing.JLabel jLabel222;
    private javax.swing.JLabel jLabel231;
    private javax.swing.JLabel jLabel232;
    private javax.swing.JLabel jLabel241;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblDescPerc;
    private javax.swing.JLabel lblSoglia1;
    private javax.swing.JLabel lblSoglia2;
    private javax.swing.JLabel lblSoglia3;
    private javax.swing.JLabel lblSoglia4;
    private javax.swing.JLabel lblSoglia5;
    private javax.swing.JPanel panAlto;
    private javax.swing.JPanel panDati;
    private javax.swing.JPanel panElen;
    private javax.swing.JTabbedPane tabCent;
    private tnxbeans.tnxTextField texCap;
    private tnxbeans.tnxTextField texCodi;
    private tnxbeans.tnxTextField texEmail;
    private tnxbeans.tnxTextField texIndi;
    private tnxbeans.tnxTextField texLoca;
    private tnxbeans.tnxTextField texNote;
    private tnxbeans.tnxTextField texPercentuale;
    private tnxbeans.tnxTextField texPercentualeSoglia1;
    private tnxbeans.tnxTextField texPercentualeSoglia2;
    private tnxbeans.tnxTextField texPercentualeSoglia3;
    private tnxbeans.tnxTextField texPercentualeSoglia4;
    private tnxbeans.tnxTextField texPercentualeSoglia5;
    private tnxbeans.tnxTextField texProv;
    private tnxbeans.tnxTextField texRagiSoci;
    private tnxbeans.tnxTextField texTele;
    // End of variables declaration//GEN-END:variables

    /*
    
    
    private void trovaAbi() {
    
    
      try {
    
    
        this.labBancAbi.setText(Db.lookUp(this.texBancAbi.getText(), "abi", "banche_abi").getString(2));
    
    
      } catch(Exception err) {
    
    
        this.labBancAbi.setText("");      
    
    
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
    
    
        sql += " where banche_cab.abi = " + Db.pc(this.texBancAbi.getText(),"VARCHAR");
    
    
        sql += " and banche_cab.cab = " + Db.pc(this.texBancCab.getText(),"VARCHAR");
    
    
        ResultSet temp = Db.openResultSet(sql);
    
    
        temp.next();
    
    
        this.labBancCab.setText(Db.nz(temp.getString(3),"") + ", " + Db.nz(temp.getString(2),""));
    
    
      } catch(Exception err) {
    
    
        this.labBancCab.setText("");
    
    
      }
    
    
    }
    
    
     */
}

/*


class frmClie_tnxIntePanel implements tnxbeans.tnxIntePanel {


  public tnxbeans.tnxTextField texBancAbi;


  public tnxbeans.tnxTextField texBancCab;


  public JLabel labBancAbi;


  public JLabel labBancCab;


  public tnxbeans.tnxDbPanel panel;


  


  public void riempiComboPrimaDiRefresh() {


  } 


  


  public void riempiCampiSecondari() {


    CoordinateBancarie coords = new CoordinateBancarie();


    coords.setField_texBancAbi(this.texBancAbi);


    coords.setField_labBancAbi(this.labBancAbi);


    coords.setField_texBancCab(this.texBancCab);


    coords.setField_labBancCab(this.labBancCab);


    coords.findDescriptionLab();


  }


  


}


 */
