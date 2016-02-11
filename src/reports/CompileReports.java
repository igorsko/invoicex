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
 * CompileReports.java
 *
 * Created on September 7, 2004, 10:46 AM
 */
package reports;
//jasper
//import dori.jasper.engine.design.*;
//import dori.jasper.engine.*;
//import dori.jasper.view.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.*;
import net.sf.jasperreports.view.*;
/*
//jfreechart
import org.jfree.data.DefaultPieDataset;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFrame;
import org.jfree.data.XYDataset;
import org.jfree.data.XYSeries;
import org.jfree.data.XYSeriesCollection;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.CategoryDataset;
import org.jfree.data.DefaultCategoryDataset;
 */
/**
 *
 * @author  marco
 */
public class CompileReports {
    /** Creates a new instance of CompileReports */
    public CompileReports() {
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JasperDesign jasperDesign;
        String base = "C:/lavori/tnx/private/Invoicex_altro/run/reports/";
        try {
            //con compilazione
            System.out.println("load jrxml 1");
            jasperDesign = JasperManager.loadXmlDesign(base + "stats_monthly.jrxml");
            System.out.print("compilazione...");
            JasperManager.compileReportToFile(jasperDesign, base + "stats_monthly.jasper");
            System.out.println("...ok");


//            System.out.println("load jrxml 2");
//            jasperDesign = JasperManager.loadXmlDesign(base + "iva.jrxml");
//            System.out.print("compilazione...");
//            JasperManager.compileReportToFile(jasperDesign, base + "iva.jasper");
//            System.out.println("...ok");
//            System.out.println("load jrxml 3");
//            jasperDesign = JasperManager.loadXmlDesign(base + "invoice.jrxml");
//            System.out.print("compilazione...");
//            JasperManager.compileReportToFile(jasperDesign, base + "invoice.jasper");
//            System.out.println("...ok");
            
        } catch (Exception err) {
            err.printStackTrace();
        }
    }
}
