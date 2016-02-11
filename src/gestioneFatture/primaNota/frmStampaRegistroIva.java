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
 * frmStatAgenti.java
 *
 * Created on 16 aprile 2003, 17.33
 */
package gestioneFatture.primaNota;

import it.tnx.Db;
import gestioneFatture.*;

import it.tnx.Db.*;
import it.tnx.SwingWorker;
import it.tnx.commons.CastUtils;
import it.tnx.commons.DbUtils;
import it.tnx.commons.DebugUtils;
import java.io.File;

import java.sql.*;
import java.sql.ResultSet;


import java.util.*;



//jasper
//import dori.jasper.engine.design.*;
//import dori.jasper.engine.*;
//import dori.jasper.view.*;
import javax.swing.JDialog;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.*;

//jfreechart
/**
 *
 * @author  Lorenzo
 */
public class frmStampaRegistroIva
        extends javax.swing.JInternalFrame {

    int tipoLiquidazione = 0;

    /** Creates new form frmStatAgenti */
    public frmStampaRegistroIva() {
        initComponents();
        
        tipo_numero_paginaActionPerformed(null);

        //jLabel2.setVisible(false);
        //this.texIvaPrecedente.setVisible(false);

        this.cambiaTipoIva(true);

        texAnno.setText(String.valueOf(it.tnx.Util.getCurrenteYear()));
        texIvaPrecedente.setText("0");

        try {
            comData.setSelectedItem(main.fileIni.getValue("frm_stampa_registro_iva", "data"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (main.pluginScontrini) {
            scontrini.setSelected(true);
            scontrini.setVisible(true);
        } else {
            scontrini.setSelected(false);
            scontrini.setVisible(false);
        }
        
    }

    public void cambiaTipoIva(boolean start) {
        try {
            if (start == true) {
                ResultSet temp = Db.openResultSet("select tipo_liquidazione_iva from dati_azienda");
                temp.next();

                if (temp.getString("tipo_liquidazione_iva").equalsIgnoreCase("mensile")) {
                    this.radMensile.setSelected(true);
                    tipoLiquidazione = gestioneFatture.primaNota.PrimaNotaUtils.TIPO_PERIODO_MENSILE;
                } else if (temp.getString("tipo_liquidazione_iva").equalsIgnoreCase("trimestrale")) {
                    this.radTrimestrale.setSelected(true);
                    tipoLiquidazione = gestioneFatture.primaNota.PrimaNotaUtils.TIPO_PERIODO_TRIMESTRALE;
                } else {
                    this.radAnnuale.setSelected(true);
                    tipoLiquidazione = gestioneFatture.primaNota.PrimaNotaUtils.TIPO_PERIODO_ANNUALE;
                }
            } else {
                if (this.radMensile.isSelected()) {
                    tipoLiquidazione = gestioneFatture.primaNota.PrimaNotaUtils.TIPO_PERIODO_MENSILE;
                } else if (this.radTrimestrale.isSelected()) {
                    tipoLiquidazione = gestioneFatture.primaNota.PrimaNotaUtils.TIPO_PERIODO_TRIMESTRALE;
                } else {
                    tipoLiquidazione = gestioneFatture.primaNota.PrimaNotaUtils.TIPO_PERIODO_ANNUALE;
                }
            }
        } catch (Exception err) {
            err.printStackTrace();
        }

        comPeriodo.removeAllItems();
        comPeriodo.setEnabled(true);

        int month = Calendar.getInstance().get(Calendar.MONTH);
        if (tipoLiquidazione == gestioneFatture.primaNota.PrimaNotaUtils.TIPO_PERIODO_MENSILE) {
            comPeriodo.addItem("Gennaio");
            comPeriodo.addItem("Febbraio");
            comPeriodo.addItem("Marzo");
            comPeriodo.addItem("Aprile");
            comPeriodo.addItem("Maggio");
            comPeriodo.addItem("Giugno");
            comPeriodo.addItem("Luglio");
            comPeriodo.addItem("Agosto");
            comPeriodo.addItem("Settembre");
            comPeriodo.addItem("Ottobre");
            comPeriodo.addItem("Novembre");
            comPeriodo.addItem("Dicembre");
            try {
                comPeriodo.setSelectedIndex(month);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (tipoLiquidazione == gestioneFatture.primaNota.PrimaNotaUtils.TIPO_PERIODO_TRIMESTRALE) {
            comPeriodo.addItem("Gennaio/Febbraio/Marzo");
            comPeriodo.addItem("Aprile/Maggio/Giugno");
            comPeriodo.addItem("Luglio/Agosto/Settembre");
            comPeriodo.addItem("Ottobre/Novembre/Dicembre");
            try {
                comPeriodo.setSelectedIndex(((month) / 3));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            comPeriodo.setEnabled(false);
        }
    }

    public static String getDescrizioneRegistro(String tipo) {

        if (tipo.equalsIgnoreCase("A")) {

            return "Registro IVA Fatture di Acquisto";
        } else {

            return "Registro IVA Fatture di Vendita";
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
        jPanel1 = new javax.swing.JPanel();
        butConferma = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        comPeriodo = new javax.swing.JComboBox();
        texAnno = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        texIvaPrecedente = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        comData = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        radTrimestrale = new javax.swing.JRadioButton();
        radMensile = new javax.swing.JRadioButton();
        radAnnuale = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        tipo_numero_pagina = new javax.swing.JComboBox();
        progressivo = new javax.swing.JTextField();
        labprogressivo = new javax.swing.JLabel();
        scontrini = new javax.swing.JCheckBox();

        FormListener formListener = new FormListener();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Stampa Registro Iva");

        butConferma.setText("Visualizza il Report");
        butConferma.addActionListener(formListener);

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Seleziona il periodo");

        texAnno.setColumns(6);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Anno");

        texIvaPrecedente.setColumns(10);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Data da usare per ordinare Fatt. Acquisto");

        comData.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Data di registrazione", "Data del doc. esterno" }));

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Iva a Credito dal periodo prec.");

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Tipo Liquidazione");

        buttonGroup1.add(radTrimestrale);
        radTrimestrale.setText("Trimestrale");
        radTrimestrale.addActionListener(formListener);

        buttonGroup1.add(radMensile);
        radMensile.setText("Mensile");
        radMensile.addActionListener(formListener);

        buttonGroup1.add(radAnnuale);
        radAnnuale.setText("Annuale");
        radAnnuale.addActionListener(formListener);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Come stampare i numeri di pagina");

        tipo_numero_pagina.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Stampa numeratore pagine semplice (Pagina # di #)", "Stampa progressivo con anno" }));
        tipo_numero_pagina.addActionListener(formListener);

        progressivo.setColumns(6);
        progressivo.setText("1");

        labprogressivo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labprogressivo.setText("progressivo di partenza");

        scontrini.setText("Includi scontrini");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .add(jLabel7)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(radAnnuale)
                                    .add(radTrimestrale)
                                    .add(radMensile)))
                            .add(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .add(jLabel5)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(comPeriodo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .add(jLabel1)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(texAnno, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(10, 10, 10)
                                .add(jLabel6)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(texIvaPrecedente, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jPanel1Layout.createSequentialGroup()
                                        .add(10, 10, 10)
                                        .add(jLabel2))
                                    .add(jPanel1Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .add(jLabel3)))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(tipo_numero_pagina, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(comData, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(jPanel1Layout.createSequentialGroup()
                                        .add(labprogressivo)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(progressivo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                    .add(scontrini))))
                        .add(0, 26, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                        .add(0, 0, Short.MAX_VALUE)
                        .add(butConferma, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 190, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(new java.awt.Component[] {jLabel1, jLabel2, jLabel3, jLabel5, jLabel6, jLabel7}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(11, 11, 11)
                .add(radMensile)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(radTrimestrale)
                    .add(jLabel7))
                .add(2, 2, 2)
                .add(radAnnuale)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(comPeriodo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel5))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(texAnno, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel6)
                    .add(texIvaPrecedente, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(comData, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(tipo_numero_pagina, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(progressivo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(labprogressivo))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(scontrini)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 18, Short.MAX_VALUE)
                .add(butConferma, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener {
        FormListener() {}
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == butConferma) {
                frmStampaRegistroIva.this.butConfermaActionPerformed(evt);
            }
            else if (evt.getSource() == radTrimestrale) {
                frmStampaRegistroIva.this.radTrimestraleActionPerformed(evt);
            }
            else if (evt.getSource() == radMensile) {
                frmStampaRegistroIva.this.radMensileActionPerformed(evt);
            }
            else if (evt.getSource() == radAnnuale) {
                frmStampaRegistroIva.this.radAnnualeActionPerformed(evt);
            }
            else if (evt.getSource() == tipo_numero_pagina) {
                frmStampaRegistroIva.this.tipo_numero_paginaActionPerformed(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents

    private void butConfermaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butConfermaActionPerformed
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

        final frmStampaRegistroIva padre = this;
        SwingWorker work = new SwingWorker() {

            public Object construct() {
                final JDialogCompilazioneReport dialog = new JDialogCompilazioneReport();

                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);

                main.fileIni.setValue("frm_stampa_registro_iva", "data", comData.getSelectedItem());

                String sql = "";

                //prima rigenero la prima nota del periodo
                int anno = 0;

                try {
                    anno = Integer.parseInt(texAnno.getText());
                } catch (Exception err) {
                    err.printStackTrace();
                }

                gestioneFatture.primaNota.PrimaNotaUtils pn = new gestioneFatture.primaNota.PrimaNotaUtils(dialog);
                pn.generaPrimaNota(tipoLiquidazione, comPeriodo.getSelectedIndex() + 1, anno, (comData.getSelectedIndex() == 0 ? false : true), scontrini.isSelected());

                try {

                    //con compilazione
                    File frep = new File("reports/iva.jrxml");
                    
                    JasperReport jasperReport = Reports.getReport(frep);

                    //System.out.println("load jrxml");
                    //JasperDesign jasperDesign = JasperManager.loadXmlDesign("reports/iva.jrxml");
                    //System.out.print("compilazione...");
                    //JasperReport jasperReport = JasperManager.compileReport(jasperDesign);
                    //System.out.println("...ok");
                    //senza compilazione
                    //System.out.println("load jasper");
                    //JasperReport jasperReport = JasperManager.loadReport("reports/iva.jasper");

//            JasperReport jasperReport = JasperManager.loadReport(getClass().getResourceAsStream("/reports/iva.jasper"));

                    // Second, create a map of parameters to pass to the report.
                    java.util.Map parameters = new java.util.HashMap();
                    if (radAnnuale.isSelected()) {
                        parameters.put("periodo", "Anno " + texAnno.getText());
                    } else {
                        parameters.put("periodo", padre.comPeriodo.getSelectedItem() + " " + texAnno.getText());
                    }
                    parameters.put("anno", texAnno.getText());
                    parameters.put("tipo_numerazione_pagine", tipo_numero_pagina.getSelectedIndex());
                    int pro = CastUtils.toInteger0(progressivo.getText());
                    if (pro > 0) pro--;
                    
                    parameters.put("progressivo_partenza", pro);
                    String int1 = "";
                    try {
                        //dati azienda
                        sql = "select ragione_sociale, indirizzo, localita, cap, provincia, cfiscale, piva from dati_azienda";
                        Map m = DbUtils.getListMap(Db.getConn(), sql).get(0);                        
                        int1 += m.get("ragione_sociale") + ", " + m.get("indirizzo") + ", " + m.get("cap") + " " + m.get("localita") + " (" + m.get("provincia") + "), " + "Partita IVA " + m.get("piva") + ", Codice Fiscale " + m.get("cfiscale");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    parameters.put("intestazione1", int1);

                    //ciclo per i codici iva descendig senza quelle a zero
                    sql = "select * from codici_iva where percentuale > 0 order by percentuale desc";

                    ResultSet riva = Db.openResultSet(sql);
                    int codiceIva = 0;

                    parameters.put("iva1", "");
                    parameters.put("iva2", "");
                    parameters.put("iva3", "");
                    parameters.put("iva4", "");
                    parameters.put("iva5", "");

                    while (riva.next() && codiceIva <= 5) {
                        codiceIva++;
                        try {
                            parameters.put("iva" + codiceIva, "Aliquota " + it.tnx.Util.formatNumero0Decimali(riva.getDouble("percentuale")) + " %");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    double saldo1 = 0;
                    double saldo2 = 0;
                    double creditoPeriodoPrec = 0;



                    //calcolo totali
                    parameters.put("totaleIvaAcquisti", new Double(pn.totali.totaleAcquisti));
                    parameters.put("totaleIvaVendite", new Double(pn.totali.totaleVendite));

                    DebugUtils.dump(parameters);

                    saldo1 = pn.totali.totaleAcquisti - pn.totali.totaleVendite;

                    System.out.println("saldo1: " + saldo1);

                    parameters.put("ivaSaldo1", new Double(Math.abs(saldo1)));
                    creditoPeriodoPrec = it.tnx.Util.getDouble(padre.texIvaPrecedente.getText());
                    parameters.put("ivaACreditoPeriodoPrec", new Double(creditoPeriodoPrec));
                    saldo2 = saldo1 + creditoPeriodoPrec;
                    parameters.put("ivaSaldo2", new Double(Math.abs(saldo2)));

                    if (saldo1 < 0) {
                        parameters.put("scrittaDebitoCredito1", "Debito");
                    } else {
                        parameters.put("scrittaDebitoCredito1", "Credito");
                    }

                    if (saldo2 < 0) {
                        parameters.put("scrittaDebitoCredito2", "Debito");
                    } else {
                        parameters.put("scrittaDebitoCredito2", "Credito");
                    }

                    // Third, get a database connection
                    Connection conn = it.tnx.Db.getConn();

                    // Fourth, create JasperPrint using fillReport() method
                    //JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);
                    JasperPrint jasperPrint = JasperManager.fillReport(jasperReport, parameters, conn);

                    // You can use JasperPrint to create PDF
                    //JasperManager.printReportToPdfFile(jasperPrint, "/home/marco/pippo/test1.pdf");
                    // Or to view report in the JasperViewer
                    JasperViewer.viewReport(jasperPrint, false);
                } catch (Exception err) {
                    err.printStackTrace();
                } finally {
                    dialog.setVisible(false);
                    return null;
                }
            }
        };
        work.start();
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_butConfermaActionPerformed

    private void radMensileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radMensileActionPerformed
        cambiaTipoIva(false);
    }//GEN-LAST:event_radMensileActionPerformed

    private void radTrimestraleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radTrimestraleActionPerformed
        cambiaTipoIva(false);
    }//GEN-LAST:event_radTrimestraleActionPerformed

    private void radAnnualeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radAnnualeActionPerformed
        cambiaTipoIva(false);
    }//GEN-LAST:event_radAnnualeActionPerformed

    private void tipo_numero_paginaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tipo_numero_paginaActionPerformed
        if (tipo_numero_pagina.getSelectedIndex() == 0) {
            labprogressivo.setEnabled(false);
            progressivo.setText("---");
            progressivo.setEnabled(false);
        } else {
            labprogressivo.setEnabled(true);
            progressivo.setText("1");
            progressivo.setEnabled(true);
        }
    }//GEN-LAST:event_tipo_numero_paginaActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton butConferma;
    public javax.swing.ButtonGroup buttonGroup1;
    public javax.swing.JComboBox comData;
    public javax.swing.JComboBox comPeriodo;
    public javax.swing.JLabel jLabel1;
    public javax.swing.JLabel jLabel2;
    public javax.swing.JLabel jLabel3;
    public javax.swing.JLabel jLabel5;
    public javax.swing.JLabel jLabel6;
    public javax.swing.JLabel jLabel7;
    public javax.swing.JPanel jPanel1;
    public javax.swing.JLabel labprogressivo;
    public javax.swing.JTextField progressivo;
    public javax.swing.JRadioButton radAnnuale;
    public javax.swing.JRadioButton radMensile;
    public javax.swing.JRadioButton radTrimestrale;
    public javax.swing.JCheckBox scontrini;
    public javax.swing.JTextField texAnno;
    public javax.swing.JTextField texIvaPrecedente;
    public javax.swing.JComboBox tipo_numero_pagina;
    // End of variables declaration//GEN-END:variables
}
