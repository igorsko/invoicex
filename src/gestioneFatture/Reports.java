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
 * Reports.java
 *
 * Created on 4 gennaio 2007, 16.50
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package gestioneFatture;

import java.io.File;
import javax.swing.JOptionPane;


import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
//import net.sf.jasperreports.engine.JasperManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.design.JRCompiler;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.JRLoader;

/**
 *
 * @author mceccarelli
 */
public class Reports {
    
    public static String DIR_REPORTS = "reports_/";
    public static String DIR_FATTURE = "fatture/";
    public static String DIR_MAGAZZINO = "magazzino/";
    
    /** Creates a new instance of Reports */
    public Reports() {
    }
    
    public static JasperReport getReport(File frep)
    throws JRException {
        
        String newFile = frep.getAbsolutePath() + ".jasper";
        File newFileFile = new File(newFile);

        boolean testload = true;

        JasperReport ret = null;
        try {
            //ret = JasperManager.loadReport(newFile);
            ret = (JasperReport) JRLoader.loadObject(newFile);
        } catch (Throwable t) {
            t.printStackTrace();
            testload = false;
            //elimino il file jasper per ricompilarlo
            boolean esitodelete = newFileFile.delete();
            System.out.println("delete jasper " + newFileFile + " esito:" + esitodelete);
        }

        if (!newFileFile.exists() || newFileFile.lastModified() < frep.lastModified() || !testload) {
            System.out.println("ricompilo report " + frep);
            try {
//                JasperDesign jasperDesign = JasperManager.loadXmlDesign(frep.getAbsolutePath());
//                JasperManager.compileReportToFile(jasperDesign, frep.getAbsolutePath() + ".jasper");
//                JasperDesign jasperDesign = (JasperDesign) JRLoader.loadObject(frep);                
              
                
                JasperCompileManager.compileReportToFile(frep.getAbsolutePath(), frep.getAbsolutePath() + ".jasper");                
            } catch (Exception err) {
                err.printStackTrace();
                JOptionPane.showMessageDialog(gestioneFatture.main.getPadre(), "Errore durante la compilazione del report\n" + err.toString(), "Errore", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        System.out.println("newFile:" + newFile);
        
//        return JasperManager.loadReport(newFile);
        return (JasperReport) JRLoader.loadObject(newFile);        
    }
    
    public static JasperReport getReportWS(File frep) throws JRException {
        String newFile = frep.getAbsolutePath() + ".jasper";
        File newFileFile = new File(newFile);
        
        if (!newFileFile.exists() || newFileFile.lastModified() < frep.lastModified()) {
            System.out.println("ricompilo report " + frep);
            try {
                //JasperDesign jasperDesign = JasperManager.loadXmlDesign(frep.getAbsolutePath());
                //JasperManager.compileReportToFile(jasperDesign, frep.getAbsolutePath() + ".jasper");
                JasperDesign jasperDesign = (JasperDesign) JRLoader.loadObject(frep);
                JasperCompileManager.compileReportToFile(jasperDesign, frep.getAbsolutePath() + ".jasper");
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
        System.out.println("newFile:" + newFile);
        //return JasperManager.loadReport(newFile);
        return (JasperReport) JRLoader.loadObject(newFile);
    }
}