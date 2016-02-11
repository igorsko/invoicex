/**
 * Invoicex Copyright (c) 2005,2006,2007 Marco Ceccarelli, Tnx snc
 *
 * Questo software è soggetto, e deve essere distribuito con la licenza GNU
 * General Public License, Version 2. La licenza accompagna il software o potete
 * trovarne una copia alla Free Software Foundation http://www.fsf.org .
 *
 * This software is subject to, and may be distributed under, the GNU General
 * Public License, Version 2. The license should have accompanied the software
 * or you may obtain a copy of the license from the Free Software Foundation at
 * http://www.fsf.org .
 *
 * -- Marco Ceccarelli (m.ceccarelli@tnx.it) Tnx snc (http://www.tnx.it)
 *
 */
// classe per registrazione prima nota!!
// frmVettori -> frmVettori1
package gestioneFatture;

import it.tnx.Db;
import it.tnx.accessoUtenti.Permesso;
import it.tnx.commons.SwingUtils;
import it.tnx.commons.swing.DelayedExecutor;
import java.sql.ResultSet;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class frmVettori
        extends javax.swing.JInternalFrame {

    tnxbeans.tnxComboField comboToRefresh;
    public String sqlWhereDaData = "";
    public String sqlWhereAData = "";

    /* DAVID */
    DelayedExecutor delay_cliente = new DelayedExecutor(new Runnable() {

        public void run() {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    SwingUtils.mouse_wait();
                    System.out.println("*** dbrefresh");
                    SwingUtils.mouse_def();
                }
            });
        }
    }, 250);
    /* DAVID */

    /**
     * Creates new form frmDati_blank
     */
    public frmVettori() {
        initComponents();

        //associo il panel ai dati
        this.dati.dbNomeTabella = "registrazione_prima_nota";

        Vector chiave = new Vector();
        chiave.add("id");
        this.dati.dbChiave = chiave;
        this.dati.butSave = this.butSave;
        this.dati.butUndo = this.butUndo;
        this.dati.butNew = this.butNew;
        this.dati.butDele = this.butDele;
        this.dati.tipo_permesso = Permesso.PERMESSO_ANAGRAFICA_ALTRE;
        this.dati.dbOpen(Db.getConn(), "select id,numero,data,descrizione,entrate,uscite from registrazione_prima_nota order by data desc");
        this.dati.dbRefresh();

        //apro la griglia
        //this.griglia.dbEditabile = true;
        this.griglia.dbChiave = chiave;
        this.griglia.flagUsaThread = false;

        java.util.Hashtable colsWidthPerc = new java.util.Hashtable();
        colsWidthPerc.put("id", new Double(0));
        /* DAVID */
        colsWidthPerc.put("numero", new Double(10));
        colsWidthPerc.put("data", new Double(15));
        /* DAVID */
        colsWidthPerc.put("descrizione", new Double(35));
        colsWidthPerc.put("entrate", new Double(15));
        colsWidthPerc.put("uscite", new Double(15));
        this.griglia.columnsSizePerc = colsWidthPerc;
        /* DAVID */
        this.griglia.dbOpen(Db.getConn(), "select id,numero,data,descrizione,entrate,uscite  from registrazione_prima_nota order by data desc");

        /* DAVID */
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

    /* DAVID */
    public void filtraPerData() {
        delay_cliente.update();
    }
    /* DAVID */

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2213 = new javax.swing.JLabel();
        panAlto = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        butNew = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        butDele = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel131 = new javax.swing.JLabel();
        butFirs = new javax.swing.JButton();
        butPrev = new javax.swing.JButton();
        butNext = new javax.swing.JButton();
        butLast = new javax.swing.JButton();
        butStampaElenco = new javax.swing.JButton();
        jToolBar2 = new javax.swing.JToolBar();
        jLabel2 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel132 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        texDal = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        texAl = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        butRefresh = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        tabCent = new javax.swing.JTabbedPane();
        panDati = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        dati = new tnxbeans.tnxDbPanel();
        texUscite = new tnxbeans.tnxTextField();
        jLabel2111 = new javax.swing.JLabel();
        jLabel2211 = new javax.swing.JLabel();
        texCodi = new tnxbeans.tnxTextField();
        texDesc = new tnxbeans.tnxTextField();
        jLabel2214 = new javax.swing.JLabel();
        jLabel2215 = new javax.swing.JLabel();
        texEntrate = new tnxbeans.tnxTextField();
        jLabel2212 = new javax.swing.JLabel();
        texData = new tnxbeans.tnxTextField();
        texNumero = new tnxbeans.tnxTextField();
        jLabel2112 = new javax.swing.JLabel();
        panElen = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        griglia = new tnxbeans.tnxDbGrid();
        jPanel2 = new javax.swing.JPanel();
        butUndo = new javax.swing.JButton();
        butSave = new javax.swing.JButton();

        jLabel2213.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2213.setText("entrate");

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Registrazione Prima Nota");
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

        jToolBar2.setRollover(true);

        jLabel2.setText(" ");
        jToolBar2.add(jLabel2);

        jLabel14.setText(" ");
        jToolBar2.add(jLabel14);

        jLabel15.setText("                                ");
        jToolBar2.add(jLabel15);

        jLabel16.setText(" ");
        jToolBar2.add(jLabel16);

        jLabel132.setText(" ");
        jToolBar2.add(jLabel132);

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel4.setText("da data ");
        jToolBar2.add(jLabel4);

        texDal.setColumns(3);
        texDal.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        texDal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                texDalActionPerformed(evt);
            }
        });
        texDal.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                texDalFocusLost(evt);
            }
        });
        texDal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                texDalKeyPressed(evt);
            }
        });
        jToolBar2.add(texDal);

        jLabel5.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel5.setText(" a data ");
        jToolBar2.add(jLabel5);

        texAl.setColumns(3);
        texAl.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        texAl.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                texAlFocusLost(evt);
            }
        });
        texAl.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                texAlKeyPressed(evt);
            }
        });
        jToolBar2.add(texAl);

        jLabel6.setText("|");
        jToolBar2.add(jLabel6);

        butRefresh.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        butRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/view-refresh.png"))); // NOI18N
        butRefresh.setToolTipText("Aggiorna l'elenco dei documenti");
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
        jToolBar2.add(butRefresh);

        jLabel17.setText("                                ");
        jToolBar2.add(jLabel17);

        panAlto.add(jToolBar2, java.awt.BorderLayout.SOUTH);

        getContentPane().add(panAlto, java.awt.BorderLayout.NORTH);

        tabCent.setName("dati"); // NOI18N

        panDati.setName("dati"); // NOI18N
        panDati.setLayout(new java.awt.BorderLayout());

        dati.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        texUscite.setText("uscite");
        texUscite.setDbNomeCampo("uscite");
        texUscite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                texUsciteActionPerformed(evt);
            }
        });
        dati.add(texUscite, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 110, 300, 20));

        jLabel2111.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        dati.add(jLabel2111, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, 70, 20));

        jLabel2211.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2211.setText("descrizione");
        dati.add(jLabel2211, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 100, 20));

        texCodi.setText("id");
        texCodi.setDbNomeCampo("id");
        texCodi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                texCodiActionPerformed(evt);
            }
        });
        dati.add(texCodi, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 115, 0));

        texDesc.setText("descrizione");
        texDesc.setDbNomeCampo("descrizione");
        texDesc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                texDescActionPerformed(evt);
            }
        });
        dati.add(texDesc, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 50, 300, 20));

        jLabel2214.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2214.setText("uscite");
        dati.add(jLabel2214, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 110, 50, 20));

        jLabel2215.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2215.setText("entrate");
        dati.add(jLabel2215, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 80, 100, 20));

        texEntrate.setText("entrate");
        texEntrate.setDbNomeCampo("entrate");
        texEntrate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                texEntrateActionPerformed(evt);
            }
        });
        dati.add(texEntrate, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 80, 300, 20));

        jLabel2212.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2212.setText("data");
        dati.add(jLabel2212, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 140, 100, 20));

        texData.setText("");
        texData.setDbNomeCampo("data");
        texData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                texDataActionPerformed(evt);
            }
        });
        dati.add(texData, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 140, 300, 20));
        texData.setDbTipoCampo("data");

        texNumero.setText("numero");
        texNumero.setDbNomeCampo("numero");
        texNumero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                texNumeroActionPerformed(evt);
            }
        });
        dati.add(texNumero, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 20, 115, 20));

        jLabel2112.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2112.setText("numero");
        dati.add(jLabel2112, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 70, 20));

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

        /* DAVID */
        int[] headerWidth = {3, 3, 3, 14, 4, 4};
        /* DAVID */
        String nomeFilePdf = this.griglia.stampaTabella("Prima nota di cassa", headerWidth);
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

    private void texUsciteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texUsciteActionPerformed

        // Add your handling code here:
    }//GEN-LAST:event_texUsciteActionPerformed

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

    private void butUndoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butUndoActionPerformed
        dati.dbUndo();
    }//GEN-LAST:event_butUndoActionPerformed

    private void butSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveActionPerformed

        java.sql.Statement stat;
        ResultSet resu;

        try {
            stat = Db.getConn().createStatement();
            String sqlId = "SELECT * FROM `registrazione_prima_nota` WHERE id = " + texCodi.getText();

            resu = stat.executeQuery(sqlId);

            String data = texData.getText(), d = "";
            if (resu.next() == true) {
                //funziona solo fino a 2099
                d = "20" + data.substring(data.length() - 2, data.length());
            } else {
                d = data.substring(data.length() - 4, data.length());
            }

            DateFormat dateFormat = new SimpleDateFormat("yyyy");
            Date date = new Date();

            if (!d.equals(dateFormat.format(date))) {
                int numeroPost = 1;
                String sqlMaxInYear = "SELECT MAX(numero) FROM registrazione_prima_nota WHERE data LIKE '%" + d + "%'";
                resu = stat.executeQuery(sqlMaxInYear);
                if (resu.next() == true) {
                    Integer res = resu.getInt(1);
                    if (res != null) {
                        numeroPost = res + 1;
                    }
                }
                texNumero.setText(numeroPost + "");
            }

            stat = Db.getConn().createStatement();
            String sqlCheck = "SELECT * FROM `registrazione_prima_nota` WHERE numero = " + texNumero.getText() + " AND data LIKE '%" + d + "%'";

            resu = stat.executeQuery(sqlCheck);

            if (resu.next() == true) {
                int ret = JOptionPane.showConfirmDialog(this, "Record già presente. Continuare?", "Attenzione", JOptionPane.YES_NO_OPTION);

                if (ret == JOptionPane.YES_OPTION) {
                    this.dati.dbSave();
                    this.griglia.dbRefresh();

                    if (this.comboToRefresh != null) {
                        this.comboToRefresh.dbRefreshItems();
                        this.dispose();
                    }
                }
            } else {
                this.dati.dbSave();
                this.griglia.dbRefresh();

                if (this.comboToRefresh != null) {
                    this.comboToRefresh.dbRefreshItems();
                    this.dispose();
                }
            } // else
        } catch (Exception err) {
            javax.swing.JOptionPane.showMessageDialog(null, err.toString());
        }
    }//GEN-LAST:event_butSaveActionPerformed

    private void butNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butNewActionPerformed
        this.dati.dbNew();

        //texCodi.setVisible(false);
        java.sql.Statement stat, statMaxInYear;
        ResultSet resu, resuMaxInYear;

        //apre il resultset per ultimo +1
        try {
            stat = Db.getConn().createStatement();

            String sql = "select id from registrazione_prima_nota order by id desc limit 1";
            resu = stat.executeQuery(sql);

            //trovo la data corrente e uso l'anno corrente per cercare il massimo numero in quest'anno            
            DateFormat dateFormat = new SimpleDateFormat("yyyy");
            Date date = new Date();
            String sqlMaxInYear = "SELECT MAX(numero) FROM registrazione_prima_nota WHERE data LIKE '%" + dateFormat.format(date) + "%'";

            statMaxInYear = Db.getConn().createStatement();
            resuMaxInYear = statMaxInYear.executeQuery(sqlMaxInYear);

            if (resu.next() == true) {
                this.texCodi.setText(String.valueOf(resu.getInt(1) + 1));
                if (resuMaxInYear.next() == true) {
                    this.texNumero.setText(String.valueOf(resuMaxInYear.getInt(1) + 1));
                } else {
                    this.texNumero.setText("1");
                }
            } else {
                this.texCodi.setText("1");
                this.texNumero.setText("1");
            }
        } catch (Exception err) {
            javax.swing.JOptionPane.showMessageDialog(null, err.toString());
        }
    }//GEN-LAST:event_butNewActionPerformed

    private void texDescActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texDescActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_texDescActionPerformed

    private void texEntrateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texEntrateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_texEntrateActionPerformed

    private void texDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texDataActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_texDataActionPerformed

    private void texDalFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texDalFocusLost
        /* DAVID */
        filtraPerData();

        if (this.texDal.getText().length() == 0) {
            sqlWhereDaData = "";
            dbRefresh();
        } else {
            if (it.tnx.Checks.isDate(this.texDal.getText())) {
                sqlWhereDaData = " data >= " + Db.pc2(this.texDal.getText(), Types.DATE);
                dbRefresh();
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "Il parametro richiesto deve essere una data in formato (gg/mm/aaaa)", "Attenzione", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_texDalFocusLost

    private void texDalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texDalKeyPressed

        /* DAVID */
        filtraPerData();
    }//GEN-LAST:event_texDalKeyPressed

    private void texAlFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texAlFocusLost
        /* DAVID */
        filtraPerData();

        if (this.texAl.getText().length() == 0) {
            sqlWhereAData = "";
            dbRefresh();
        } else {
            if (it.tnx.Checks.isDate(this.texAl.getText())) {
                sqlWhereAData = " data <= " + Db.pc2(this.texAl.getText(), Types.DATE);
                dbRefresh();
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "Il parametro richiesto deve essere una data in formato (gg/mm/aaaa)", "Attenzione", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_texAlFocusLost

    private void texAlKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texAlKeyPressed

        /* DAVID */
        filtraPerData();
    }//GEN-LAST:event_texAlKeyPressed

    private void butRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butRefreshActionPerformed
        //dbRefresh();
        /* DAVID */
        if (this.texDal.getText().length() == 0) {
            sqlWhereDaData = "";
        } else {
            if (it.tnx.Checks.isDate(this.texDal.getText())) {
                sqlWhereDaData = " data >= " + Db.pc2(this.texDal.getText(), Types.DATE);
            }
        }

        if (this.texAl.getText().length() == 0) {
            sqlWhereAData = "";
        } else {
            if (it.tnx.Checks.isDate(this.texAl.getText())) {
                sqlWhereAData = " data <= " + Db.pc2(this.texAl.getText(), Types.DATE);

            }
        }
        dbRefresh();
    }//GEN-LAST:event_butRefreshActionPerformed

    private void texDalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texDalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_texDalActionPerformed

    private void texCodiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texCodiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_texCodiActionPerformed

    private void texNumeroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texNumeroActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_texNumeroActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butDele;
    private javax.swing.JButton butFirs;
    private javax.swing.JButton butLast;
    private javax.swing.JButton butNew;
    private javax.swing.JButton butNext;
    private javax.swing.JButton butPrev;
    private javax.swing.JButton butRefresh;
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
    private javax.swing.JLabel jLabel132;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel2111;
    private javax.swing.JLabel jLabel2112;
    private javax.swing.JLabel jLabel2211;
    private javax.swing.JLabel jLabel2212;
    private javax.swing.JLabel jLabel2213;
    private javax.swing.JLabel jLabel2214;
    private javax.swing.JLabel jLabel2215;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JPanel panAlto;
    private javax.swing.JPanel panDati;
    private javax.swing.JPanel panElen;
    private javax.swing.JTabbedPane tabCent;
    private javax.swing.JTextField texAl;
    private tnxbeans.tnxTextField texCodi;
    private javax.swing.JTextField texDal;
    private tnxbeans.tnxTextField texData;
    private tnxbeans.tnxTextField texDesc;
    private tnxbeans.tnxTextField texEntrate;
    private tnxbeans.tnxTextField texNumero;
    private tnxbeans.tnxTextField texUscite;
    // End of variables declaration//GEN-END:variables

    /* DAVID */
    public void dbRefresh() {
        String sql = "select id,numero,data,descrizione,entrate,uscite from registrazione_prima_nota";
        if (sqlWhereDaData.length() > 0 || sqlWhereAData.length() > 0) {
            sql += " where";
            if (sqlWhereDaData.length() > 0) {
                sql += sqlWhereDaData;
                if (sqlWhereAData.length() > 0) {
                    sql += " and" + sqlWhereAData;
                }
            } else {
                if (sqlWhereAData.length() > 0) {
                    sql += sqlWhereAData;
                }
            }
        }
        sql += " order by data desc";
        this.dati.dbOpenD2(Db.getConn(), sql);  //-->tnxDbpanel.java
        this.dati.dbRefresh();
        this.griglia.dbOpen(Db.getConn(), sql);
        this.griglia.dbRefresh();

    }
    /* DAVID */
}
