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
package gestioneFatture.logic.provvigioni;

import it.tnx.Db;
import gestioneFatture.*;
import it.tnx.accessoUtenti.Permesso;
import it.tnx.commons.cu;

import java.sql.*;
import java.sql.ResultSet;


import java.util.Vector;

import javax.swing.*;
import javax.swing.JOptionPane;
import javax.swing.text.*;

/**

 *

 * @author  Administrator

 */
public class frmDettaglioProvvigione
    extends javax.swing.JInternalFrame {

    String sql;
    ProvvigioniFattura provvigioni;
    java.text.DecimalFormat df = new java.text.DecimalFormat(".00");
    double totaleDocumento;
    java.util.Timer timTotali;

    /** Creates new form frmDati_blank */
    public frmDettaglioProvvigione(ProvvigioniFattura provvigioni) {
        this.provvigioni = provvigioni;

        try {
            mask = new MaskFormatter("##/##/##");
        } catch (Exception err) {
            err.printStackTrace();
        }

        initComponents();

        //associo il panel ai dati
        this.dati.dbNomeTabella = "provvigioni";

        Vector chiave = new Vector();
        chiave.add("id");

        //chiave.add("documento_tipo");
        //chiave.add("documento_serie");
        //chiave.add("documento_numero");
        //chiave.add("documento_anno");
        //chiave.add("data_scadenza");
        this.dati.dbChiave = chiave;
        this.dati.butSave = this.butSave;
        this.dati.butUndo = this.butUndo;
        this.dati.butFind = this.butFind;
        this.dati.butNew  = this.butNew;
        this.dati.butDele = this.butDele;
        this.dati.tipo_permesso = Permesso.PERMESSO_AGENTI;
        
        sql = "select * from provvigioni";
        sql += " where documento_tipo = " + Db.pc(provvigioni.documento_tipo, Types.VARCHAR);
        sql += " and documento_serie = " + Db.pc(provvigioni.documento_serie, Types.VARCHAR);
        sql += " and documento_numero = " + Db.pc(provvigioni.documento_numero, Types.INTEGER);
        sql += " and documento_anno = " + Db.pc(provvigioni.documento_anno, Types.INTEGER);
        sql += " order by id";
        this.dati.dbOpen(Db.getConn(), sql);
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
        sql = "select documento_serie,documento_anno,documento_numero,documento_tipo,data_scadenza,id,numero, data_scadenza as Data, importo_provvigione as Importo, pagata as Pagata from provvigioni";
        sql += " where documento_tipo = " + Db.pc(provvigioni.documento_tipo, Types.VARCHAR);
        sql += " and documento_serie = " + Db.pc(provvigioni.documento_serie, Types.VARCHAR);
        sql += " and documento_numero = " + Db.pc(provvigioni.documento_numero, Types.INTEGER);
        sql += " and documento_anno = " + Db.pc(provvigioni.documento_anno, Types.INTEGER);
        sql += " order by id";
        this.griglia.dbOpen(Db.getConn(), sql);
        this.griglia.dbPanel = this.dati;
        
        //se mi arriva il p_id cerco la provvigione selezionata
        if (provvigioni != null && provvigioni.p_id != null) {
            try {
                boolean trovato = false;
                for (int i = 0; i < griglia.getRowCount(); i++) {
                    Integer p_id_griglia = cu.toInteger(griglia.getValueAt(i, griglia.getColumnByName("id")));
                    if (p_id_griglia == provvigioni.p_id) {
                        griglia.getSelectionModel().setSelectionInterval(i, i);
                        griglia.dbSelezionaRiga();
                        trovato = true;
                    }
                }
                if (!trovato) {
                    griglia.getSelectionModel().clearSelection();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        //sistemo alcune cose
        //this.comTipo.addItem("CL");
        //this.comTipo.addItem("FO");
        this.dati.dbRefresh();

        if (dati.isOnSomeRecord == false) {
            javax.swing.JOptionPane.showMessageDialog(this, "Non ci sono provvigioni caricate per questo documento, prosegui inserendone una", "Attenzione", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            butNewActionPerformed(null);
        }

        this.labDocu.setText("Provvigioni del documento: " + provvigioni.documento_serie + provvigioni.documento_numero);

        //creo timer per aggiornare totale provvigioni
        timTotali = new java.util.Timer();

        timRefreshTotali timRefresh = new timRefreshTotali(this, this.griglia, this.labTotaleProvvigioni, totaleDocumento);
        timTotali.schedule(timRefresh, 1000, 500);
        
        texDataScad.setEditable(false);
        texDataScad.setEnabled(false);

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
        texId = new tnxbeans.tnxTextField();
        jLabel2211 = new javax.swing.JLabel();
        jLabel21111 = new javax.swing.JLabel();
        labDocu = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        griglia = new tnxbeans.tnxDbGrid();
        jLabel3 = new javax.swing.JLabel();
        jLabel22111 = new javax.swing.JLabel();
        texNume = new tnxbeans.tnxTextField();
        jLabel221111 = new javax.swing.JLabel();
        tnxCheckBox1 = new tnxbeans.tnxCheckBox();
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
        labTotaleProvvigioni = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        butUndo = new javax.swing.JButton();
        butSave = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Provvigioni");
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
        butFind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butFindActionPerformed(evt);
            }
        });
        jToolBar1.add(butFind);

        jLabel131.setText(" ");
        jToolBar1.add(jLabel131);

        butFirs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/go-first.png"))); // NOI18N
        butFirs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butFirsActionPerformed(evt);
            }
        });
        jToolBar1.add(butFirs);

        butPrev.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/go-previous.png"))); // NOI18N
        butPrev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butPrevActionPerformed(evt);
            }
        });
        jToolBar1.add(butPrev);

        butNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/go-next.png"))); // NOI18N
        butNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butNextActionPerformed(evt);
            }
        });
        jToolBar1.add(butNext);

        butLast.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/go-last.png"))); // NOI18N
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

        dati.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        texId.setEditable(false);
        texId.setText("id");
        texId.setDbNomeCampo("id");
        dati.add(texId, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 25, 70, 20));

        jLabel2211.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2211.setText("data scadenza");
        dati.add(jLabel2211, new org.netbeans.lib.awtextra.AbsoluteConstraints(-5, 50, 100, 20));

        jLabel21111.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel21111.setText("id");
        dati.add(jLabel21111, new org.netbeans.lib.awtextra.AbsoluteConstraints(155, 25, 70, 20));

        labDocu.setForeground(new java.awt.Color(0, 0, 255));
        labDocu.setText("...");
        dati.add(labDocu, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 5, 375, -1));

        jScrollPane1.setToolTipText("cliccando su una riga si ceglie la destinazione da moficare");

        griglia.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                grigliaComponentResized(evt);
            }
        });
        jScrollPane1.setViewportView(griglia);

        dati.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 115, 385, 100));

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 10));
        jLabel3.setText("Elenco provvigioni");
        dati.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, -1, 15));

        jLabel22111.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel22111.setText("numero");
        dati.add(jLabel22111, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 25, 100, 20));

        texNume.setDbNomeCampo("numero");
        texNume.setDbTipoCampo("NUMERICO");
        dati.add(texNume, new org.netbeans.lib.awtextra.AbsoluteConstraints(105, 25, 40, 20));

        jLabel221111.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel221111.setText("pagata");
        dati.add(jLabel221111, new org.netbeans.lib.awtextra.AbsoluteConstraints(215, 75, 50, 20));

        tnxCheckBox1.setDbDescCampo("");
        tnxCheckBox1.setDbNomeCampo("pagata");
        tnxCheckBox1.setDbTipoCampo("");
        dati.add(tnxCheckBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 75, -1, -1));

        texDataScad.setDbNomeCampo("data_scadenza");
        texDataScad.setDbTipoCampo("data");
        dati.add(texDataScad, new org.netbeans.lib.awtextra.AbsoluteConstraints(105, 50, 70, -1));

        texDocuAnno.setBackground(new java.awt.Color(255, 204, 204));
        texDocuAnno.setDbNomeCampo("documento_anno");
        texDocuAnno.setDbTipoCampo("LONG");
        dati.add(texDocuAnno, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 90, 60, -1));

        texDocuTipo.setBackground(new java.awt.Color(255, 204, 204));
        texDocuTipo.setDbNomeCampo("documento_tipo");
        dati.add(texDocuTipo, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 30, 60, -1));

        texDocuSeri.setBackground(new java.awt.Color(255, 204, 204));
        texDocuSeri.setDbNomeCampo("documento_serie");
        dati.add(texDocuSeri, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 50, 60, -1));

        texDocuNume.setBackground(new java.awt.Color(255, 204, 204));
        texDocuNume.setDbNomeCampo("documento_numero");
        texDocuNume.setDbTipoCampo("LONG");
        dati.add(texDocuNume, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 70, 60, -1));

        jLabel22112.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel22112.setText("importo");
        dati.add(jLabel22112, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 75, 100, 20));

        texImpo.setDbNomeCampo("importo_provvigione");
        texImpo.setDbTipoCampo("VALUTA");
        dati.add(texImpo, new org.netbeans.lib.awtextra.AbsoluteConstraints(105, 75, 100, 20));

        labTotaleProvvigioni.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labTotaleProvvigioni.setText("Totale provvigioni");
        dati.add(labTotaleProvvigioni, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 215, 280, 20));

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
        java.sql.Statement stat;
        ResultSet resu;

        //apre il resultset per ultimo +1
        try {
            stat = Db.getConn().createStatement();

            String sql = "select numero from provvigioni";
            sql += " where documento_tipo = " + Db.pc(this.provvigioni.documento_tipo, Types.VARCHAR);
            sql += " and documento_serie = " + Db.pc(this.provvigioni.documento_serie, Types.VARCHAR);
            sql += " and documento_numero = " + Db.pc(this.provvigioni.documento_numero, Types.INTEGER);
            sql += " and documento_anno = " + Db.pc(this.provvigioni.documento_anno, Types.INTEGER);
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

        this.texDocuTipo.setText(provvigioni.documento_tipo);
        this.texDocuSeri.setText(provvigioni.documento_serie);
        this.texDocuNume.setText(String.valueOf(provvigioni.documento_numero));
        this.texDocuAnno.setText(String.valueOf(provvigioni.documento_anno));
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
    private javax.swing.JLabel jLabel21111;
    private javax.swing.JLabel jLabel2211;
    private javax.swing.JLabel jLabel22111;
    private javax.swing.JLabel jLabel221111;
    private javax.swing.JLabel jLabel22112;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel labDocu;
    private javax.swing.JLabel labTotaleProvvigioni;
    private javax.swing.JPanel panAlto;
    private javax.swing.JPanel panDati;
    private javax.swing.JTabbedPane tabCent;
    private tnxbeans.tnxTextFieldFormatted texDataScad;
    private tnxbeans.tnxTextField texDocuAnno;
    private tnxbeans.tnxTextField texDocuNume;
    private tnxbeans.tnxTextField texDocuSeri;
    private tnxbeans.tnxTextField texDocuTipo;
    private tnxbeans.tnxTextField texId;
    private tnxbeans.tnxTextField texImpo;
    private tnxbeans.tnxTextField texNume;
    private tnxbeans.tnxCheckBox tnxCheckBox1;
    // End of variables declaration//GEN-END:variables
    MaskFormatter mask;
}

class timRefreshTotali
    extends java.util.TimerTask {

    JInternalFrame frame;
    JLabel labTotaleProvvigioni;
    tnxbeans.tnxDbGrid griglia;
    double tempTotale;
    int INDEX_COL_IMPORTO = 8;
    private double totaleDocumento;

    public timRefreshTotali(JInternalFrame frame, tnxbeans.tnxDbGrid griglia, JLabel labTotaleProvvigioni, double totaleDocumento) {
        this.frame = frame;
        this.labTotaleProvvigioni = labTotaleProvvigioni;
        this.griglia = griglia;
        this.totaleDocumento = totaleDocumento;
    }

    public void run() {

        try {

            //calcola totale
            tempTotale = 0;

            for (int i = 0; i < griglia.getRowCount(); i++) {

                try {
                    tempTotale += Double.parseDouble(griglia.getValueAt(i, INDEX_COL_IMPORTO).toString());
                } catch (Exception err) {
                }
            }

            this.labTotaleProvvigioni.setText("Totale Provvigioni " + it.tnx.Util.EURO + " " + it.tnx.Util.formatValutaEuro(tempTotale));
            this.labTotaleProvvigioni.setForeground(java.awt.Color.black);
        } catch (Exception err) {
            err.printStackTrace();
        }
    }
}