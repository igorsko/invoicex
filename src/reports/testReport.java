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

 * testReport.java

 *

 * Created on January 20, 2005, 11:01 AM

 */



package reports;



import java.util.Vector;

import javax.swing.JOptionPane;

import java.sql.ResultSet;

import javax.swing.*;

import java.util.*;

import java.text.*;



import gestioneFatture.*;

import it.tnx.Db.*;

import java.sql.*;



//jasper

import net.sf.jasperreports.engine.*;

import net.sf.jasperreports.engine.design.*;

import net.sf.jasperreports.view.*;



//jfreechart

import org.jfree.data.general.DefaultPieDataset;

import org.jfree.chart.ChartFactory;

import org.jfree.chart.JFreeChart;

import org.jfree.chart.ChartFrame;



import org.jfree.data.xy.XYDataset;

import org.jfree.data.xy.XYSeries;

import org.jfree.data.xy.XYSeriesCollection;

import org.jfree.chart.plot.PlotOrientation;

import org.jfree.data.category.CategoryDataset;

import org.jfree.data.category.DefaultCategoryDataset;



/**

 *

 * @author  marco

 */

public class testReport {

    

    /** Creates a new instance of testReport */

    public testReport() {

        try {

            //con compilazione

            System.out.println("load jrxml");

            JasperDesign jasperDesign = JasperManager.loadXmlDesign("reports/invoice.jrxml");

            System.out.print("compilazione...");

            JasperReport jasperReport = JasperManager.compileReport(jasperDesign);

            System.out.println("...ok");

            //senza compilazione

            

            //System.out.println("load jasper");

            //JasperReport jasperReport = JasperManager.loadReport("reports/stats_monthly.jasper");

            

            // Second, create a map of parameters to pass to the report.

            java.util.Map parameters = new java.util.HashMap();

            //parameters.put("periodo", "Dal " + this.jTextField1.getText() + " al " + this.jTextField2.getText());

            //parameters.put("stampaDettagli", new Boolean(cheDettagli.isSelected()));

            

            // Third, get a database connection

            Class.forName("org.gjt.mm.mysql.Driver").newInstance();

            String url = "jdbc:mysql://linux/GestioneFatture_tnx?user=root&password=***";

            Connection conn = DriverManager.getConnection(url,"","");

            

            // Fourth, create JasperPrint using fillReport() method

            //JasperPrint jasperPrint = JasperManager.fillReport(jasperReport, parameters, conn);

            //reports.JRDSInvoice jrInvoice = new reports.JRDSInvoice(conn);

            //JasperPrint jasperPrint = JasperManager.fillReport(jasperReport, parameters, jrInvoice);

            

            // You can use JasperPrint to create PDF

            //JasperManager.printReportToPdfFile(jasperPrint, "/home/marco/pippo/test1.pdf");

            // Or to view report in the JasperViewer

            //JasperViewer.viewReport(jasperPrint, false);

        } catch (Exception err) {

            err.printStackTrace();

        }

    }

    

    /**

     * @param args the command line arguments

     */

    public static void main(String[] args) {

        // TODO code application logic here

        testReport t = new testReport();

    }

    

}

