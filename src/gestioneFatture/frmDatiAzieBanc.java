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
import it.tnx.commons.DbUtils;
import java.sql.ResultSet;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.text.*;

public class frmDatiAzieBanc
        extends javax.swing.JInternalFrame {

    String sql;
    Scadenze scadenze;
    CoordinateBancarie coords;
    private ResultSet temp;
    
    /*  -- DAVID -- */
    
    public frmDatiAzieBanc(CoordinateBancarie coords) {
        this.coords = coords;
        initComponents();

        //associo il panel ai dati
        this.dati.dbNomeTabella = "dati_azienda_banche";

        Vector chiave = new Vector();
        chiave.add("abi");
        chiave.add("cab");
        chiave.add("cc");
        this.dati.dbChiave = chiave;
        this.dati.butSave = this.butSave;
        this.dati.butUndo = this.butUndo;
        this.dati.butFind = this.butFind;
        this.dati.butNew = this.butNew;
        this.dati.butDele = this.butDele;
        this.dati.tipo_permesso = Permesso.PERMESSO_ANAGRAFICA_ALTRE;
        
        //this.dati.dbOpen(Db.conn,"select * from scadenze where id = " + codiceCliente);
        sql = "select * from dati_azienda_banche";
        this.dati.dbOpen(Db.getConn(), sql);
        this.dati.dbRefresh();

        //apro la griglia
        //this.griglia.dbEditabile = true;
        this.griglia.dbChiave = chiave;
        this.griglia.flagUsaThread = false;

        //this.griglia.dbOpen(Db.conn,"select data_scadenza,pagata,importo from scadenze where id = " + codiceCliente + " order by ragione_sociale");
        sql = "select * from dati_azienda_banche";
        this.griglia.dbOpen(Db.getConn(), sql);
        this.griglia.dbPanel = this.dati;
        
        this.griglia.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                griAgenMouseClicked(evt);
            }
        });
        //sistemo alcune cose
        //this.comTipo.addItem("CL");
        //this.comTipo.addItem("FO");
        this.dati.dbRefresh();

    }
    
    private void griAgenMouseClicked(java.awt.event.MouseEvent evt) {                                     

        if (evt.getClickCount() == 2) {
            this.coords.setAbi(this.griglia.getValueAt(griglia.getSelectedRow(), 0).toString());
            this.coords.setCab(this.griglia.getValueAt(griglia.getSelectedRow(), 1).toString());
            this.coords.setIban(this.griglia.getValueAt(griglia.getSelectedRow(), 4).toString());
            /* DAVID  */
            this.coords.setSwift(this.griglia.getValueAt(griglia.getSelectedRow(), 6).toString());
            this.coords.setCc(this.griglia.getValueAt(griglia.getSelectedRow(), 2).toString());
            /* DAVID  */
            
            String descAbi = "";
            String descCab = "";
            
            String sql_select = "SELECT banche_abi.abi, banche_abi.nome, banche_cab.cab, banche_cab.cap, banche_cab.indirizzo, comuni.comune, comuni.provincia, comuni.regione FROM (banche_abi left join banche_cab on banche_abi.abi = banche_cab.abi) left join comuni on banche_cab.codice_comune = comuni.codice WHERE banche_abi.abi = "+this.griglia.getValueAt(griglia.getSelectedRow(), 0).toString()+" and banche_cab.cab = "+this.griglia.getValueAt(griglia.getSelectedRow(), 1).toString();
            try {
                temp = Db.openResultSet(sql_select);
                
                if (temp.next() == true) {
                    descAbi = temp.getString(2);
                    descCab = temp.getString(4) + " " + temp.getString(5) + ", " + temp.getString(6) + " (" +temp.getString(7)+")";
                    }
                } catch (Exception e) {
                   JOptionPane.showConfirmDialog(this, "Errore: "+e.toString(), "Errrore D", JOptionPane.ERROR_MESSAGE);                
                }
            this.coords.setDescrizioneAbi(descAbi);
            this.coords.setDescrizioneCab(descCab);
            
            this.dispose();
        }   
    }

    /*  /-- DAVID -- */
    

    /** Creates new form frmDati_blank */
    public frmDatiAzieBanc() {
        initComponents();

        //associo il panel ai dati
        this.dati.dbNomeTabella = "dati_azienda_banche";

        Vector chiave = new Vector();
        chiave.add("abi");
        chiave.add("cab");
        chiave.add("cc");
        this.dati.dbChiave = chiave;
        this.dati.butSave = this.butSave;
        this.dati.butUndo = this.butUndo;
        this.dati.butFind = this.butFind;
        this.dati.butNew = this.butNew;
        this.dati.butDele = this.butDele;
        this.dati.tipo_permesso = Permesso.PERMESSO_ANAGRAFICA_ALTRE;
        
        //this.dati.dbOpen(Db.conn,"select * from scadenze where id = " + codiceCliente);
        sql = "select * from dati_azienda_banche";
        this.dati.dbOpen(Db.getConn(), sql);
        this.dati.dbRefresh();

        //apro la griglia
        //this.griglia.dbEditabile = true;
        this.griglia.dbChiave = chiave;
        this.griglia.flagUsaThread = false;

        //this.griglia.dbOpen(Db.conn,"select data_scadenza,pagata,importo from scadenze where id = " + codiceCliente + " order by ragione_sociale");
        sql = "select * from dati_azienda_banche";
        this.griglia.dbOpen(Db.getConn(), sql);
        this.griglia.dbPanel = this.dati;

        //sistemo alcune cose
        //this.comTipo.addItem("CL");
        //this.comTipo.addItem("FO");
        this.dati.dbRefresh();

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
        jScrollPane1 = new javax.swing.JScrollPane();
        griglia = new tnxbeans.tnxDbGrid();
        jLabel3 = new javax.swing.JLabel();
        jLabel22111 = new javax.swing.JLabel();
        texRagiSoci1 = new tnxbeans.tnxTextField();
        jLabel22112 = new javax.swing.JLabel();
        texRagiSoci2 = new tnxbeans.tnxTextField();
        jLabel22113 = new javax.swing.JLabel();
        texRagiSoci3 = new tnxbeans.tnxTextField();
        jLabel22114 = new javax.swing.JLabel();
        texRagiSoci4 = new tnxbeans.tnxTextField();
        jLabel22115 = new javax.swing.JLabel();
        texRagiSoci5 = new tnxbeans.tnxTextField();
        jLabel22116 = new javax.swing.JLabel();
        texRagiSoci6 = new tnxbeans.tnxTextField();
        jPanel2 = new javax.swing.JPanel();
        butUndo = new javax.swing.JButton();
        butSave = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Anagrafica conti correnti aziendali");
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

        jScrollPane1.setToolTipText("cliccando su una riga si ceglie la destinazione da moficare");

        griglia.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                grigliaComponentResized(evt);
            }
        });
        jScrollPane1.setViewportView(griglia);

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel3.setText("Elenco conti correnti inseriti");

        jLabel22111.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel22111.setText("Abi");

        texRagiSoci1.setColumns(10);
        texRagiSoci1.setDbNomeCampo("abi");
        texRagiSoci1.setDbTipoCampo("");

        jLabel22112.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel22112.setText("Cab");

        texRagiSoci2.setColumns(10);
        texRagiSoci2.setDbNomeCampo("cab");
        texRagiSoci2.setDbTipoCampo("");

        jLabel22113.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel22113.setText("Note");

        texRagiSoci3.setDbNomeCampo("note");
        texRagiSoci3.setDbTipoCampo("");
        texRagiSoci3.setmaxChars(255);

        jLabel22114.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel22114.setText("Numero Conto");

        texRagiSoci4.setColumns(20);
        texRagiSoci4.setDbNomeCampo("cc");
        texRagiSoci4.setDbTipoCampo("");

        jLabel22115.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel22115.setText("IBAN");

        texRagiSoci5.setDbNomeCampo("cc_iban");
        texRagiSoci5.setDbTipoCampo("");
        texRagiSoci5.setmaxChars(27);

        jLabel22116.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel22116.setText("SWIFT");

        texRagiSoci6.setDbNomeCampo("SWIFT");
        texRagiSoci6.setDbTipoCampo("");
        texRagiSoci6.setmaxChars(27);

        org.jdesktop.layout.GroupLayout datiLayout = new org.jdesktop.layout.GroupLayout(dati);
        dati.setLayout(datiLayout);
        datiLayout.setHorizontalGroup(
            datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(datiLayout.createSequentialGroup()
                .add(10, 10, 10)
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(datiLayout.createSequentialGroup()
                        .add(jLabel22115)
                        .add(10, 10, 10)
                        .add(texRagiSoci5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE)
                    .add(datiLayout.createSequentialGroup()
                        .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(datiLayout.createSequentialGroup()
                                .add(jLabel22111)
                                .add(10, 10, 10)
                                .add(texRagiSoci1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(datiLayout.createSequentialGroup()
                                .add(jLabel22112)
                                .add(10, 10, 10)
                                .add(texRagiSoci2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(datiLayout.createSequentialGroup()
                                .add(jLabel22114)
                                .add(10, 10, 10)
                                .add(texRagiSoci4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jLabel3))
                        .add(0, 0, Short.MAX_VALUE))
                    .add(datiLayout.createSequentialGroup()
                        .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel22113, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 69, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel22116, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 69, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(texRagiSoci3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(texRagiSoci6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        datiLayout.linkSize(new java.awt.Component[] {jLabel22111, jLabel22112, jLabel22114, jLabel22115}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        datiLayout.setVerticalGroup(
            datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(datiLayout.createSequentialGroup()
                .add(10, 10, 10)
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel22111)
                    .add(texRagiSoci1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(5, 5, 5)
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel22112)
                    .add(texRagiSoci2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(5, 5, 5)
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel22114)
                    .add(texRagiSoci4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(5, 5, 5)
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel22115)
                    .add(texRagiSoci5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(texRagiSoci6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel22116, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(texRagiSoci3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel22113, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                .addContainerGap())
        );

        datiLayout.linkSize(new java.awt.Component[] {jLabel22111, jLabel22112, jLabel22114, jLabel22115, texRagiSoci1, texRagiSoci2, texRagiSoci3, texRagiSoci4, texRagiSoci5}, org.jdesktop.layout.GroupLayout.VERTICAL);

        jScrollPane2.setViewportView(dati);

        panDati.add(jScrollPane2, java.awt.BorderLayout.CENTER);

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
    }//GEN-LAST:event_butSaveActionPerformed

    private void butNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butNewActionPerformed
        this.dati.dbNew();

        //setto defualt

        /*
        
        java.sql.Statement stat;
        
        ResultSet resu;
        
        //apre il resultset per ultimo +1
        
        try {
        
        stat = Db.conn.createStatement();
        
        String sql = "select codice from clie_forn_dest where codice_cliente = " + this.codiceCliente + " order by codice desc limit 1";
        
        resu = stat.executeQuery(sql);
        
        if(resu.next()==true) {
        
        //this.texCodi.setText(String.valueOf(resu.getInt(1)+1));
        
        } else {
        
        //this.texCodi.setText("1");
        
        }
        
        } catch (Exception err) {
        
        javax.swing.JOptionPane.showMessageDialog(null,err.toString());
        
        }
        
         */
    }//GEN-LAST:event_butNewActionPerformed

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
    private tnxbeans.tnxDbPanel dati;
    private tnxbeans.tnxDbGrid griglia;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel131;
    private javax.swing.JLabel jLabel22111;
    private javax.swing.JLabel jLabel22112;
    private javax.swing.JLabel jLabel22113;
    private javax.swing.JLabel jLabel22114;
    private javax.swing.JLabel jLabel22115;
    private javax.swing.JLabel jLabel22116;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel panAlto;
    private javax.swing.JPanel panDati;
    private javax.swing.JTabbedPane tabCent;
    private tnxbeans.tnxTextField texRagiSoci1;
    private tnxbeans.tnxTextField texRagiSoci2;
    private tnxbeans.tnxTextField texRagiSoci3;
    private tnxbeans.tnxTextField texRagiSoci4;
    private tnxbeans.tnxTextField texRagiSoci5;
    private tnxbeans.tnxTextField texRagiSoci6;
    // End of variables declaration//GEN-END:variables
    MaskFormatter mask;
}
