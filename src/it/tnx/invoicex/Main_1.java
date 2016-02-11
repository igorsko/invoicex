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
 * Main.java
 *
 * Created on 1 luglio 2005, 7.05
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package it.tnx.invoicex;

import gestioneFatture.iniFileProp;
import gestioneFatture.main;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.prefs.*;
import javax.swing.JOptionPane;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
/**
 *
 * @author marco
 */
public class Main_1 {
    public static Preferences sysPrefs = Preferences.systemNodeForPackage(Main.class);
    public static Preferences userPrefs = Preferences.userNodeForPackage(Main.class);
    private static ResourceBundle msgRes = ResourceBundle.getBundle("it.tnx.invoicex.res.text", Locale.getDefault());
    private Logger logger = Logger.getLogger(Main.class);
    
    //debug
    //public static String url_server = "http://localhost:8084/InvoicexWSServer/HservicesImpl";
    
    //tnx.dyndns.org
    public static String url_server = "http://tnx.dyndns.org:8080/InvoicexWSServer/HservicesImpl";
    
    static public iniFileProp fileIni;
    
    /** Creates a new instance of Main */
    public Main_1(String[] args) {
        //controllo precedente istanza con file locking
        try {
            // Get a file channel for the file
            File file = new File("lock");
            FileChannel channel = new RandomAccessFile(file, "rw").getChannel();
            
            // Use the file channel to create a lock on the file.
            // This method blocks until it can retrieve the lock.
            FileLock lock = channel.tryLock();
            if (lock == null) {
                JOptionPane.showMessageDialog(null, "Invoicex e' gia' in esecuzione..", "Attenzione", JOptionPane.ERROR_MESSAGE);
                System.out.println("già in esecuzione...");
                System.exit(0);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
        
        //init logger
        BasicConfigurator.configure();
        logger.info("user.language:" + System.getProperty("user.language"));
        
        //setto il LOCALE
        java.util.Locale.setDefault(Locale.ITALIAN);
        // Redirect all output to a log fil
        if (args.length == 0) {
            try {
                String logFile = "error.log";
                File delete = new File(logFile);
                if (delete.exists() == true) {
                    if (delete.delete() == false) {
                        javax.swing.JOptionPane.showMessageDialog(null,"Impossibile eliminare il precedente file di log !");
                    }
                }
                FileOutputStream fileOutputStream = new FileOutputStream(logFile, true);
                PrintStream printStream = new PrintStream(fileOutputStream);
                System.setOut(printStream);
                System.setErr(printStream);
                java.text.DateFormat stamp = new java.text.SimpleDateFormat("dd/MM/yy HH:mm");
                String dateString =  stamp.format(new java.util.Date());
                System.out.println("*** Logging started " + dateString + " ***");
            } catch (Exception err) {
                err.printStackTrace();
                javax.swing.JOptionPane.showMessageDialog(null,err.toString());
            }
        }
        
        //lancio programma vero
        String[] argsMain = {""};
        try {
            main tempMain = new main(argsMain);
        } catch (Exception err) {
            System.out.println("err:" + err);
            err.printStackTrace();
        }
        
//        System.out.println("!!!seriale:" + main.fileIni.getValue("inst", "inst_seriale"));
//        main.inst_seriale = "30C087C2";
    }
    
    public static void main(String[] args) {
        new Main(args);
    }
    
    public static String getIntString(String key) {
        return msgRes.getString(key);
    }
}
