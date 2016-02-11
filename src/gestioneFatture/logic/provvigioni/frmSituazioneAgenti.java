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



package gestioneFatture.logic.provvigioni;

import it.tnx.Db;
import gestioneFatture.Reports;
import gestioneFatture.main;
import it.tnx.accessoUtenti.Permesso;
import it.tnx.commons.CastUtils;
import it.tnx.commons.DbUtils;

import it.tnx.commons.SwingUtils;
import it.tnx.commons.cu;
import java.awt.Cursor;
import java.io.File;

import java.sql.Types;


import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

public class frmSituazioneAgenti
    extends javax.swing.JInternalFrame {

    String sql;
    String sqlCorrente;
    String sqlIniziale = null;
    boolean per_scadenze = true;

    /** Creates new form frmElenPrev */
    public frmSituazioneAgenti() {

        try {
            String provvigioni_tipo_data = CastUtils.toString(DbUtils.getObject(Db.getConn(), "select provvigioni_tipo_data from dati_azienda"));
            if (provvigioni_tipo_data.equalsIgnoreCase("data_fattura")) {
                per_scadenze = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (per_scadenze) {
            sqlIniziale = " select test_fatt.pagamento, test_fatt.serie, test_fatt.numero, test_fatt.anno, test_fatt.data, test_fatt.totale, scadenze.data_scadenza as 'S data scadenza', scadenze.importo as 'S importo', scadenze.pagata as 'S pagata', provvigioni.numero as 'P num', provvigioni.importo as 'P importo', test_fatt.agente_percentuale as 'P %', provvigioni.importo_provvigione as 'P provvigione', provvigioni.pagata as 'P pagata', clie_forn.ragione_sociale as cliente, provvigioni.id as p_id, agenti.nome as agente, test_fatt.id as tid " +
                " , (select count(*) from scadenze s2 where s2.documento_numero = test_fatt.numero and s2.documento_serie = test_fatt.serie and s2.documento_anno = test_fatt.anno and s2.documento_tipo = 'FA') as numero_scadenze, test_fatt.sconto" +
                " from test_fatt left join scadenze on test_fatt.serie = scadenze.documento_serie and test_fatt.numero = scadenze.documento_numero and test_fatt.anno = scadenze.documento_anno" + "   left join provvigioni on test_fatt.serie = provvigioni.documento_serie and test_fatt.numero = provvigioni.documento_numero and test_fatt.anno = provvigioni.documento_anno and scadenze.data_scadenza = provvigioni.data_scadenza" + "   left join clie_forn on test_fatt.cliente = clie_forn.codice" + "   left join pagamenti on test_fatt.pagamento = pagamenti.codice left join agenti on test_fatt.agente_codice = agenti.id where scadenze.documento_tipo = 'FA'" + " and provvigioni.importo is not null and agenti.id is not null";
        } else {
            sqlIniziale = " select test_fatt.pagamento, test_fatt.serie, test_fatt.numero, test_fatt.anno, test_fatt.data, test_fatt.totale, provvigioni.data_scadenza as 'S data scadenza', test_fatt.totale as 'S importo', '' as 'S pagata', provvigioni.numero as 'P num', provvigioni.importo as 'P importo', test_fatt.agente_percentuale as 'P %', provvigioni.importo_provvigione as 'P provvigione', provvigioni.pagata as 'P pagata', clie_forn.ragione_sociale as cliente, provvigioni.id as p_id, agenti.nome as agente, test_fatt.id as tid " +
                " , 1 as numero_scadenze, test_fatt.sconto" +
                " from test_fatt left join provvigioni on test_fatt.serie = provvigioni.documento_serie and test_fatt.numero = provvigioni.documento_numero and test_fatt.anno = provvigioni.documento_anno " +
                "   left join clie_forn on test_fatt.cliente = clie_forn.codice" +
                "   left join pagamenti on test_fatt.pagamento = pagamenti.codice" +
                "   left join agenti on test_fatt.agente_codice = agenti.id" +
                " where provvigioni.documento_tipo = 'FA' and provvigioni.importo is not null and agenti.id is not null";
        }

        initComponents();

        if(!main.utente.getPermesso(Permesso.PERMESSO_AGENTI, Permesso.PERMESSO_TIPO_SCRITTURA)){
            this.comMarca.setEnabled(false);
            this.comMarca1.setEnabled(false);
        }
        //apro la griglia
        this.griglia.dbNomeTabella = "";
        this.griglia.flagUsaThread = false;

        java.util.Hashtable colsWidthPerc = new java.util.Hashtable();
        colsWidthPerc.put("pagamento", new Double(10));
        colsWidthPerc.put("serie", new Double(0));
        colsWidthPerc.put("numero", new Double(5));
        colsWidthPerc.put("anno", new Double(0));
        colsWidthPerc.put("data", new Double(7));
        colsWidthPerc.put("totale", new Double(8));
        colsWidthPerc.put("S data scadenza", new Double(7));
        colsWidthPerc.put("S importo", new Double(7));
        colsWidthPerc.put("S pagata", new Double(6));
        colsWidthPerc.put("P num", new Double(5));
        colsWidthPerc.put("P importo", new Double(8));
        colsWidthPerc.put("P %", new Double(6));
        colsWidthPerc.put("P provvigione", new Double(7));
        colsWidthPerc.put("P pagata", new Double(6));
        colsWidthPerc.put("cliente", new Double(10));
        colsWidthPerc.put("p_id", new Double(0));
        colsWidthPerc.put("agente", new Double(10));
        colsWidthPerc.put("tid", new Double(0));
        colsWidthPerc.put("numero_scadenze", new Double(0));
        colsWidthPerc.put("sconto", new Double(0));
        this.griglia.columnsSizePerc = colsWidthPerc;

        java.util.Hashtable colsAlign = new java.util.Hashtable();
        colsAlign.put("totale", "RIGHT_CURRENCY");
        colsAlign.put("S importo", "RIGHT_CURRENCY");
        colsAlign.put("P provvigione", "RIGHT_CURRENCY");
        this.griglia.columnsAlign = colsAlign;
        this.comAgente.dbAddElement("<selezionare un agente>", "-1");
        this.comAgente.dbAddElement("<tutti gli agenti>", "*");
        this.comAgente.dbOpenList(Db.getConn(), "select nome, id from agenti order by nome", "*", false);
        this.comMese.addItem("Gennaio");
        this.comMese.addItem("Febbraio");
        this.comMese.addItem("Marzo");
        this.comMese.addItem("Aprile");
        this.comMese.addItem("Maggio");
        this.comMese.addItem("Giugno");
        this.comMese.addItem("Luglio");
        this.comMese.addItem("Agosto");
        this.comMese.addItem("Settembre");
        this.comMese.addItem("Ottobre");
        this.comMese.addItem("Novembre");
        this.comMese.addItem("Dicembre");
        this.comMese.addItem("Gennaio/Febbraio");
        this.comMese.addItem("Marzo/Aprile");
        this.comMese.addItem("Maggio/Giugno");
        this.comMese.addItem("Luglio/Agosto");
        this.comMese.addItem("Settembre/Ottobre");
        this.comMese.addItem("Novembre/Dicembre");
        this.comMese.addItem("<tutto l'anno>");
        //this.comMese.setSelectedIndex(it.tnx.Util.getCurrenteMonth() - 1);
        comMese.setSelectedIndex(comMese.getItemCount()-1);
        this.texAnno.setText(String.valueOf(it.tnx.Util.getCurrenteYear()));

        comAggiornaActionPerformed(null);
    }

    private Calendar getFine() {
        Calendar fine = Calendar.getInstance();
        if (comMese.getSelectedIndex() == 18) {
            fine.set(it.tnx.Util.getInt(this.texAnno.getText()), 11, 31);
        } else if (comMese.getSelectedIndex() > 11) {
            Calendar inizio = getInizio();
            inizio.add(Calendar.MONTH, 1);
            fine.set(it.tnx.Util.getInt(this.texAnno.getText()), ((this.comMese.getSelectedIndex() - 12) * 2) + 1,  inizio.getActualMaximum(fine.DAY_OF_MONTH));
        } else {
            fine.set(it.tnx.Util.getInt(this.texAnno.getText()), this.comMese.getSelectedIndex(), getInizio().getActualMaximum(fine.DAY_OF_MONTH));
        }
        return fine;
    }

    private Calendar getInizio() {
        Calendar inizio = Calendar.getInstance();
        if (comMese.getSelectedIndex() == 18) {
            inizio.set(it.tnx.Util.getInt(this.texAnno.getText()), 0, 1);
        } else if (comMese.getSelectedIndex() > 11) {
            inizio.set(it.tnx.Util.getInt(this.texAnno.getText()), ((this.comMese.getSelectedIndex() - 12) * 2), 1);
        } else {
            inizio.set(it.tnx.Util.getInt(this.texAnno.getText()), this.comMese.getSelectedIndex(), 1);
        }
        return inizio;
    }


    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        tooMenu = new javax.swing.JToolBar();
        comStampa = new javax.swing.JButton();
        jLabel112 = new javax.swing.JLabel();
        comStampa1 = new javax.swing.JButton();
        comStampa2 = new javax.swing.JButton();
        jLabel113 = new javax.swing.JLabel();
        comMarca = new javax.swing.JButton();
        comMarca1 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        panDati = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        griglia = new tnxbeans.tnxDbGrid();
        jPanel1 = new javax.swing.JPanel();
        panClie = new tnxbeans.tnxDbPanel();
        jLabel2 = new javax.swing.JLabel();
        comAgente = new tnxbeans.tnxComboField();
        jLabel21 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        comMese = new javax.swing.JComboBox();
        texAnno = new javax.swing.JTextField();
        comAggiorna = new javax.swing.JButton();
        comAggiornaSoloPagate = new javax.swing.JButton();
        comAggiornaSoloPagate1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        labTotale = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Situazione Agenti");
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosed(evt);
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

        tooMenu.setRollover(true);

        comStampa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/document-print.png"))); // NOI18N
        comStampa.setText("Stampa");
        comStampa.setToolTipText("");
        comStampa.setBorderPainted(false);
        comStampa.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        comStampa.setMargin(new java.awt.Insets(2, 2, 2, 2));
        comStampa.setRolloverEnabled(true);
        comStampa.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        comStampa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comStampaActionPerformed(evt);
            }
        });
        tooMenu.add(comStampa);

        jLabel112.setText("  ");
        tooMenu.add(jLabel112);

        comStampa1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/document-print.png"))); // NOI18N
        comStampa1.setText("Report per agente");
        comStampa1.setToolTipText("");
        comStampa1.setBorderPainted(false);
        comStampa1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        comStampa1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        comStampa1.setRolloverEnabled(true);
        comStampa1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        comStampa1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comStampa1ActionPerformed(evt);
            }
        });
        tooMenu.add(comStampa1);

        comStampa2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/document-print.png"))); // NOI18N
        comStampa2.setText("Report dettagliato");
        comStampa2.setToolTipText("");
        comStampa2.setBorderPainted(false);
        comStampa2.setFocusable(false);
        comStampa2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        comStampa2.setMargin(new java.awt.Insets(2, 2, 2, 2));
        comStampa2.setRolloverEnabled(true);
        comStampa2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        comStampa2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comStampa2ActionPerformed(evt);
            }
        });
        tooMenu.add(comStampa2);

        jLabel113.setText("  ");
        tooMenu.add(jLabel113);

        comMarca.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/edit-redo.png"))); // NOI18N
        comMarca.setText("Marca come pagate");
        comMarca.setToolTipText("");
        comMarca.setBorderPainted(false);
        comMarca.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        comMarca.setMargin(new java.awt.Insets(2, 2, 2, 2));
        comMarca.setRolloverEnabled(true);
        comMarca.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        comMarca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comMarcaActionPerformed(evt);
            }
        });
        tooMenu.add(comMarca);

        comMarca1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/edit-undo.png"))); // NOI18N
        comMarca1.setText("Marca come NON pagate");
        comMarca1.setToolTipText("");
        comMarca1.setBorderPainted(false);
        comMarca1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        comMarca1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        comMarca1.setRolloverEnabled(true);
        comMarca1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        comMarca1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comMarca1ActionPerformed(evt);
            }
        });
        tooMenu.add(comMarca1);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/edit-select-all.png"))); // NOI18N
        jButton1.setText("Seleziona tutte");
        jButton1.setBorderPainted(false);
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton1.setRolloverEnabled(true);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        tooMenu.add(jButton1);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/edit-clear.png"))); // NOI18N
        jButton2.setText("Deseleziona tutte");
        jButton2.setBorderPainted(false);
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton2.setRolloverEnabled(true);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        tooMenu.add(jButton2);

        getContentPane().add(tooMenu, java.awt.BorderLayout.NORTH);

        panDati.setLayout(new java.awt.BorderLayout());

        jScrollPane1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jScrollPane1MouseClicked(evt);
            }
        });

        griglia.setModel(new javax.swing.table.DefaultTableModel(
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
        griglia.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                grigliaMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(griglia);

        panDati.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.BorderLayout());

        panClie.setPreferredSize(new java.awt.Dimension(687, 80));
        panClie.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setText("Agente");
        panClie.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 11, 45, 20));

        comAgente.setPreferredSize(new java.awt.Dimension(180, 20));
        comAgente.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comAgenteItemStateChanged(evt);
            }
        });
        panClie.add(comAgente, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, 245, -1));

        jLabel21.setText("   ");
        panClie.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(237, 9, -1, -1));

        jLabel3.setText("Mese");
        jLabel3.setToolTipText("");
        jLabel3.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        panClie.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 45, 20));

        jLabel4.setText("Anno");
        panClie.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(205, 40, 45, 20));

        comMese.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comMeseItemStateChanged(evt);
            }
        });
        panClie.add(comMese, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 40, 135, 20));

        texAnno.setColumns(4);
        texAnno.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                texAnnoFocusLost(evt);
            }
        });
        panClie.add(texAnno, new org.netbeans.lib.awtextra.AbsoluteConstraints(255, 40, 50, 20));

        comAggiorna.setText("Visualizza Tutte");
        comAggiorna.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comAggiornaActionPerformed(evt);
            }
        });
        panClie.add(comAggiorna, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 10, -1, 60));

        comAggiornaSoloPagate.setFont(comAggiornaSoloPagate.getFont().deriveFont(comAggiornaSoloPagate.getFont().getSize()-2f));
        comAggiornaSoloPagate.setText("Visualizza provvigioni NON pagate");
        comAggiornaSoloPagate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comAggiornaSoloPagateActionPerformed(evt);
            }
        });
        panClie.add(comAggiornaSoloPagate, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 40, 220, -1));

        comAggiornaSoloPagate1.setFont(comAggiornaSoloPagate1.getFont().deriveFont(comAggiornaSoloPagate1.getFont().getSize()-2f));
        comAggiornaSoloPagate1.setText("Visualizza provvigioni pagate");
        comAggiornaSoloPagate1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comAggiornaSoloPagate1ActionPerformed(evt);
            }
        });
        panClie.add(comAggiornaSoloPagate1, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 10, 220, -1));

        jPanel1.add(panClie, java.awt.BorderLayout.CENTER);

        panDati.add(jPanel1, java.awt.BorderLayout.NORTH);

        getContentPane().add(panDati, java.awt.BorderLayout.CENTER);

        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        labTotale.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labTotale.setText("...");
        jPanel2.add(labTotale);

        getContentPane().add(jPanel2, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void texAnnoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texAnnoFocusLost
        selezionaSituazione();
    }//GEN-LAST:event_texAnnoFocusLost

    private void comMeseItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comMeseItemStateChanged

        if (evt.getStateChange() == evt.SELECTED) {
            selezionaSituazione();
        }
    }//GEN-LAST:event_comMeseItemStateChanged

    private void comMarcaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comMarcaActionPerformed

        String sql;
        int id;

        if (griglia.getSelectedRowCount() == 0) {SwingUtils.showInfoMessage(this, "Seleziona almeno una riga prima!"); return;}
        
        if (javax.swing.JOptionPane.showConfirmDialog(this, "Sicuro di marcare le provvigioni SELEZIONATE come Pagate ?", "Attenzione", javax.swing.JOptionPane.YES_NO_OPTION) == javax.swing.JOptionPane.YES_OPTION) {

            for (int i = 0; i < this.griglia.getSelectedRowCount(); i++) {
                id = Integer.parseInt(String.valueOf(this.griglia.getValueAt(griglia.getSelectedRows()[i], griglia.getColumnByName("p_id"))));
                sql = "update provvigioni set pagata = 'S' where id = " + id;
                Db.executeSql(sql);
            }

            this.comAggiornaActionPerformed(null);
        }
        
    }//GEN-LAST:event_comMarcaActionPerformed

    private void comAggiornaSoloPagateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comAggiornaSoloPagateActionPerformed
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

        String sql = sqlIniziale + " and provvigioni.pagata = 'N'";

        //prendo Agente
        if (!this.comAgente.getSelectedKey().toString().equalsIgnoreCase("*")) {
            sql += " and test_fatt.agente_codice = " + this.comAgente.getSelectedKey();
        }

        //calcolo il periodo
        Calendar inizio = getInizio();
        Calendar fine = getFine();

        if (per_scadenze) {
            sql += " and scadenze.data_scadenza >= " + it.tnx.Db2.pc2(inizio.getTime(), Types.DATE);
            sql += " and scadenze.data_scadenza <= " + it.tnx.Db2.pc2(fine.getTime(), Types.DATE);
        } else {
            sql += " and provvigioni.data_scadenza >= " + it.tnx.Db2.pc2(inizio.getTime(), Types.DATE);
            sql += " and provvigioni.data_scadenza <= " + it.tnx.Db2.pc2(fine.getTime(), Types.DATE);
        }
        
        System.out.println("select provvigioni:" + sql);
        sqlCorrente = sql;
        this.griglia.dbOpen(Db.getConn(), sql);
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_comAggiornaSoloPagateActionPerformed

    private void comAggiornaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comAggiornaActionPerformed
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

        String sql = sqlIniziale;

        //prendo Agente
        if (!this.comAgente.getSelectedKey().toString().equalsIgnoreCase("*")) {
            sql += " and test_fatt.agente_codice = " + this.comAgente.getSelectedKey();
        }

        //calcolo il periodo
        Calendar inizio = getInizio();
        Calendar fine = getFine();
        if (per_scadenze) {
            sql += " and scadenze.data_scadenza >= " + it.tnx.Db2.pc2(inizio.getTime(), Types.DATE);
            sql += " and scadenze.data_scadenza <= " + it.tnx.Db2.pc2(fine.getTime(), Types.DATE);
        } else {
            sql += " and provvigioni.data_scadenza >= " + it.tnx.Db2.pc2(inizio.getTime(), Types.DATE);
            sql += " and provvigioni.data_scadenza <= " + it.tnx.Db2.pc2(fine.getTime(), Types.DATE);
        }
        System.out.println("select provvigioni:" + sql);
        sqlCorrente = sql;
        this.griglia.dbOpen(Db.getConn(), sql);
        
        //aggiorno il totale
        double totale = 0;
        double totalePagate = 0;
        double totaleDaPagare = 0;
        for (int i = 0; i < this.griglia.getRowCount(); i++) {
            Double d = (Double)this.griglia.getValueAt(i, griglia.getColumnByName("P provvigione"));
            if (String.valueOf(this.griglia.getValueAt(i, griglia.getColumnByName("P pagata"))).equals("S")) {
                totalePagate += d.doubleValue();
            } else {
                totaleDaPagare += d.doubleValue();
            }
            totale += d.doubleValue();
        }
        this.labTotale.setText("Totale " + it.tnx.Util.EURO + " " + it.tnx.Util.formatValutaEuro(totale) +
                " / Gia' Pagate " + it.tnx.Util.EURO + " " + it.tnx.Util.formatValutaEuro(totalePagate) +
                " / Da Pagare " + it.tnx.Util.EURO + " " + it.tnx.Util.formatValutaEuro(totaleDaPagare));        
        
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_comAggiornaActionPerformed

    private void comAgenteItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comAgenteItemStateChanged

        if (evt.getStateChange() == evt.SELECTED) {
            selezionaSituazione();
        }
    }//GEN-LAST:event_comAgenteItemStateChanged

    private void grigliaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_grigliaMouseClicked

        // Add your handling code here:
        String serie;
        int numero;
        int anno;
        Integer p_numero = null;
        Integer p_id = null;
        String pagamento;

        if (evt.getClickCount() == 2) {
            serie = String.valueOf(this.griglia.getValueAt(griglia.getSelectedRow(), griglia.getColumnByName("serie")));
            numero = Integer.parseInt(String.valueOf(this.griglia.getValueAt(griglia.getSelectedRow(), griglia.getColumnByName("numero"))));
            anno = Integer.parseInt(String.valueOf(this.griglia.getValueAt(griglia.getSelectedRow(), griglia.getColumnByName("anno"))));
            p_numero = cu.toInteger(griglia.getValueAt(griglia.getSelectedRow(), griglia.getColumnByName("p_numero")));
            p_id = cu.toInteger(griglia.getValueAt(griglia.getSelectedRow(), griglia.getColumnByName("p_id")));

            ProvvigioniFattura tempProvvigioni = new ProvvigioniFattura(Db.TIPO_DOCUMENTO_FATTURA, serie, numero, anno, -1, -1);
            tempProvvigioni.p_numero = p_numero;
            tempProvvigioni.p_id = p_id;

            //frmPagaPart frm = new frmPagaPart(tempScad);
            frmDettaglioProvvigione frm = new frmDettaglioProvvigione(tempProvvigioni);
            
            main.getPadre().openFrame(frm, 450, 400, 300, 200);
        }
    }//GEN-LAST:event_grigliaMouseClicked

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened

        // Add your handling code here:
        this.griglia.resizeColumnsPerc(true);
    }//GEN-LAST:event_formInternalFrameOpened

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed

        // Add your handling code here:
        main.getPadre().closeFrame(this);
    }//GEN-LAST:event_formInternalFrameClosed

    private void jScrollPane1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPane1MouseClicked
    }//GEN-LAST:event_jScrollPane1MouseClicked

    private void comStampaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comStampaActionPerformed
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

        String sql = sqlCorrente;
        this.griglia.dbOpen(Db.getConn(), sqlCorrente);

        prnStampaProvvigioniMensili stampa = new prnStampaProvvigioniMensili(sql, this.comAgente.getSelectedItem().toString(), this.comMese
            .getSelectedItem().toString() + " " + this.texAnno.getText());
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_comStampaActionPerformed

    private void comStampa1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comStampa1ActionPerformed
        try {
            System.out.println("sqlCorrente = " + sqlCorrente);

//            JasperReport r = (JasperReport)JRLoader.loadObject("reports/provvigioni.jasper");
            File freport = new File(main.wd + "reports/provvigioni.jrxml");
            
            JasperReport r = Reports.getReport(freport);

            HashMap params = new HashMap();
            params.put("query", sqlCorrente );
            String note1 = "";
            note1 = main.attivazione.getDatiAzienda().getRagione_sociale();
            note1 += "\n" + "Periodo: " + comMese.getSelectedItem() + " anno:" + texAnno.getText();
            params.put("note1", note1 );
            JasperPrint p = JasperFillManager.fillReport(r, params,Db.getConn());
            JasperViewer viewer = new JasperViewer(p, false);
            viewer.setVisible(true);
        } catch (JRException ex) {
            Logger.getLogger(frmSituazioneAgenti.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_comStampa1ActionPerformed

    private void comAggiornaSoloPagate1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comAggiornaSoloPagate1ActionPerformed
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

        String sql = sqlIniziale + " and provvigioni.pagata = 'S'";

        //prendo Agente
        if (!this.comAgente.getSelectedKey().toString().equalsIgnoreCase("*")) {
            sql += " and test_fatt.agente_codice = " + this.comAgente.getSelectedKey();
        }

        //calcolo il periodo
        Calendar inizio = getInizio();
        Calendar fine = getFine();

        if (per_scadenze) {
            sql += " and scadenze.data_scadenza >= " + it.tnx.Db2.pc2(inizio.getTime(), Types.DATE);
            sql += " and scadenze.data_scadenza <= " + it.tnx.Db2.pc2(fine.getTime(), Types.DATE);
        } else {
            sql += " and provvigioni.data_scadenza >= " + it.tnx.Db2.pc2(inizio.getTime(), Types.DATE);
            sql += " and provvigioni.data_scadenza <= " + it.tnx.Db2.pc2(fine.getTime(), Types.DATE);
        }

        System.out.println("select provvigioni:" + sql);
        sqlCorrente = sql;
        this.griglia.dbOpen(Db.getConn(), sql);
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_comAggiornaSoloPagate1ActionPerformed

    private void comMarca1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comMarca1ActionPerformed

        String sql;
        int id;

        if (griglia.getSelectedRowCount() == 0) {SwingUtils.showInfoMessage(this, "Seleziona almeno una riga prima!"); return;}

        if (javax.swing.JOptionPane.showConfirmDialog(this, "Sicuro di marcare le provvigioni SELEZIONATE come NON Pagate ?", "Attenzione", javax.swing.JOptionPane.YES_NO_OPTION) == javax.swing.JOptionPane.YES_OPTION) {

            for (int i = 0; i < this.griglia.getSelectedRowCount(); i++) {
                id = Integer.parseInt(String.valueOf(this.griglia.getValueAt(griglia.getSelectedRows()[i], griglia.getColumnByName("p_id"))));
                sql = "update provvigioni set pagata = 'N' where id = " + id;
                Db.executeSql(sql);
            }

            this.comAggiornaActionPerformed(null);
        }

    }//GEN-LAST:event_comMarca1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        griglia.getSelectionModel().setSelectionInterval(0, griglia.getRowCount());
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        griglia.getSelectionModel().clearSelection();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void comStampa2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comStampa2ActionPerformed
        try {
            System.out.println("sqlCorrente = " + sqlCorrente);

//            JasperReport r = (JasperReport)JRLoader.loadObject("reports/provvigioni_det.jasper");
            File freport = new File(main.wd + "reports/provvigioni_det.jrxml");

                        /* DAVID */
                        System.out.println("9999999999999 Jasper");
            JasperReport r = Reports.getReport(freport);

            HashMap params = new HashMap();
            params.put("query", sqlCorrente );
            params.put("SUBREPORT_DIR", main.wd + "reports/" );
            String note1 = "";
            note1 = main.attivazione.getDatiAzienda().getRagione_sociale();
            note1 += "\n" + "Periodo: " + comMese.getSelectedItem() + " anno:" + texAnno.getText();
            params.put("note1", note1 );
//            JasperPrint p = JasperFillManager.fillReport(r, params,Db.getConn());
            System.out.println("params = " + params);
            JasperPrint p = JasperFillManager.fillReport(r, params, Db.getConn());
            JasperViewer viewer = new JasperViewer(p, false);
            viewer.setVisible(true);
        } catch (JRException ex) {
            Logger.getLogger(frmSituazioneAgenti.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_comStampa2ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private tnxbeans.tnxComboField comAgente;
    private javax.swing.JButton comAggiorna;
    private javax.swing.JButton comAggiornaSoloPagate;
    private javax.swing.JButton comAggiornaSoloPagate1;
    private javax.swing.JButton comMarca;
    private javax.swing.JButton comMarca1;
    private javax.swing.JComboBox comMese;
    private javax.swing.JButton comStampa;
    private javax.swing.JButton comStampa1;
    private javax.swing.JButton comStampa2;
    private tnxbeans.tnxDbGrid griglia;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel112;
    private javax.swing.JLabel jLabel113;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labTotale;
    private tnxbeans.tnxDbPanel panClie;
    private javax.swing.JPanel panDati;
    private javax.swing.JTextField texAnno;
    private javax.swing.JToolBar tooMenu;
    // End of variables declaration//GEN-END:variables
    public void dbRefresh() {
        griglia.dbRefresh();
    }

    public void selezionaSituazione() {
        this.comAggiornaActionPerformed(null);
    }

}