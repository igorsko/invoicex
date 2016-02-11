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
import java.sql.ResultSet;
import java.util.Vector;
import javax.swing.JOptionPane;

public class frmTipiNazioni
    extends javax.swing.JInternalFrame {

    tnxbeans.tnxComboField comboToRefresh;

    /** Creates new form frmDati_blank */
    public frmTipiNazioni() {
        initComponents();

        //associo il panel ai dati
        this.dati.dbNomeTabella = "stati";

        Vector chiave = new Vector();
        chiave.add("codice1");
        this.dati.dbChiave = chiave;
        this.dati.butSave = this.butSave;
        this.dati.butUndo = this.butUndo;
        this.dati.butFind = this.butFind;
        this.dati.butNew  = this.butNew;
        this.dati.butDele = this.butDele;
        this.dati.tipo_permesso = Permesso.PERMESSO_ANAGRAFICA_ALTRE;
        
        this.dati.dbOpen(Db.getConn(), "select * from stati order by nome");
        this.dati.dbRefresh();

        //apro la griglia
        //this.griglia.dbEditabile = true;
        this.griglia.dbChiave = chiave;
        this.griglia.flagUsaThread = false;

        java.util.Hashtable colsWidthPerc = new java.util.Hashtable();
        colsWidthPerc.put("codice1", new Double(15));
        colsWidthPerc.put("nome", new Double(35));
        this.griglia.columnsSizePerc = colsWidthPerc;
        this.griglia.dbOpen(Db.getConn(), "select codice1, nome from stati order by nome");
        this.griglia.dbPanel = this.dati;

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
        jLabel2111 = new javax.swing.JLabel();
        jLabel2211 = new javax.swing.JLabel();
        texCodi = new tnxbeans.tnxTextField();
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
        setTitle("Nazioni");
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

        butStampaElenco.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/document-print.png"))); // NOI18N
        butStampaElenco.setText("Stampa elenco");
        butStampaElenco.setBorderPainted(false);
        butStampaElenco.setMargin(new java.awt.Insets(2, 2, 2, 2));
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
        dati.add(texRagiSoci, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 35, 300, 20));

        jLabel2111.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2111.setText("codice");
        dati.add(jLabel2111, new org.netbeans.lib.awtextra.AbsoluteConstraints(35, 10, 70, 20));

        jLabel2211.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2211.setText("nome");
        dati.add(jLabel2211, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 35, 100, 20));

        texCodi.setText("codice1");
        texCodi.setDbNomeCampo("codice1");
        dati.add(texCodi, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 115, 20));

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

        int[] headerWidth = { 5, 20 };
        String nomeFilePdf = this.griglia.stampaTabella("Elenco Stati", headerWidth);
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
        this.dati.dbSave();
        this.griglia.dbRefresh();

        if (this.comboToRefresh != null) {
            this.comboToRefresh.dbRefreshItems();
            this.dispose();
        }
    }//GEN-LAST:event_butSaveActionPerformed

    private void butNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butNewActionPerformed
        this.dati.dbNew();
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
    private javax.swing.JButton butStampaElenco;
    private javax.swing.JButton butUndo;
    private tnxbeans.tnxDbPanel dati;
    private tnxbeans.tnxDbGrid griglia;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel131;
    private javax.swing.JLabel jLabel2111;
    private javax.swing.JLabel jLabel2211;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel panAlto;
    private javax.swing.JPanel panDati;
    private javax.swing.JPanel panElen;
    private javax.swing.JTabbedPane tabCent;
    private tnxbeans.tnxTextField texCodi;
    private tnxbeans.tnxTextField texRagiSoci;
    // End of variables declaration//GEN-END:variables
}