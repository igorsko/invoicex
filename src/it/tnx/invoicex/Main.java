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

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;
import gestioneFatture.iniFileProp;
import gestioneFatture.main;
import it.tnx.invoicex.gui.JDialogAbout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.prefs.*;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Category;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 *
 * @author marco
 */
public class Main {
    static {
        File fmkdir = new File(System.getProperty("user.home") + File.separator + ".invoicex");
        fmkdir.mkdir();
    }
    public static Preferences sysPrefs = Preferences.userNodeForPackage(Main.class);
    public static Preferences userPrefs = Preferences.userNodeForPackage(Main.class);
    private static ResourceBundle msgRes = ResourceBundle.getBundle("it.tnx.invoicex.res.text", Locale.getDefault());
    private Logger logger = Logger.getLogger(Main.class);
    
    //debug
//    public static String url_server = "http://192.168.0.115:8084/InvoicexWSServer/HservicesImpl";

    //tnx.dyndns.org
//    public static String url_server = "http://tnx.dyndns.org:8080/InvoicexWSServer/HservicesImpl";
//    public static String url_server = "http://www.tnx.it:8080/InvoicexWSServer/HservicesImpl";

    //produzione
    public static String url_server = "http://s.invoicex.it/InvoicexWSServer/HservicesImpl";
    
    static public iniFileProp fileIni;
    static public boolean applet = false;
    static public Object appletinst = null;

    /** Creates a new instance of Main */
    public Main(String[] args) {               
        
        if (args.length > 0) {
            for (String arg : args) {
                if (arg.equalsIgnoreCase("debug")) {
                    main.debug = true;
                    break;
                }
            }
        }

        long t0 = System.currentTimeMillis();
        System.out.println(this + " - classloader - " + this.getClass().getClassLoader() + " name:" + this.getClass().getClassLoader().getClass().getName());
        if (this.getClass().getClassLoader().getClass().getName().equals("testappletinvoicex.MyClassLoader3")
                || Arrays.asList(args).contains("applet")) {
            System.out.println("Applet!!!");
            applet = true;
        }

        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        //controllo precedente su mac
        if (PlatformUtils.isMac() && !applet) {
            File f = new File("../../../Invoicex");
            if (f.isDirectory()) {
                f = new File("non_aggiornare");
                if (!f.exists()) {
                    if (JOptionPane.showConfirmDialog(null, "Vuoi aggiornare la vecchia versione di Invoicex ?", "Attenzione", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        try {
                            //copio il param_prop.txt e la mysql/data/invoicex_default
                            f = new File("../../../Invoicex/param_prop.txt");
                            File fd = new File("param_prop.txt");
                            copyFiles(f, fd);
                            f = new File("../../../Invoicex/mysql/data/invoicex_default");
                            fd = new File("./mysql/data/invoicex_default");
                            copyFiles(f, fd);
                            f = new File("../../../Invoicex");
                            fd = new File("../../../Invoicex_vecchia_versione");
                            f.renameTo(fd);
                        } catch (IOException ex) {
                            java.util.logging.Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        try {
                            f = new File("non_aggiornare");
                            f.createNewFile();
                        } catch (IOException ex) {
                            java.util.logging.Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }

        //controllo precedente istanza con file locking
        if (!applet) {
            try {
                // Get a file channel for the file
                File file = new File("lock");
    //            JOptionPane.showMessageDialog(null, file.getAbsolutePath());
                FileChannel channel = new RandomAccessFile(file, "rw").getChannel();
                // Use the file channel to create a lock on the file.
                // This method blocks until it can retrieve the lock.
                FileLock lock = channel.tryLock();
                if (lock == null) {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    JOptionPane.showMessageDialog(null, "Invoicex e' gia' in esecuzione o in aggiornamento", "Attenzione", JOptionPane.ERROR_MESSAGE);
                    System.out.println("già in esecuzione...");
                    System.exit(0);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }

        // Redirect all output to a log fil
        boolean dolog = true;
        if (args.length > 0) {
            for (String arg : args) {
                if (arg.equalsIgnoreCase("nolog")) {
                    dolog = false;
                }
            }
        }
        if (dolog) {
            System.setErr(new PrintStream(new LoggingOutputStream(Category.getRoot(), Priority.WARN), true));
            System.setOut(new PrintStream(new LoggingOutputStream(Category.getRoot(), Priority.INFO), true));
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        long t1 = System.currentTimeMillis();
        main.splash("caricamento", true, null);

        java.text.DateFormat stamp = new java.text.SimpleDateFormat("dd/MM/yy HH:mm");
        String dateString = stamp.format(new java.util.Date());
        System.out.println("");
        System.out.println("*** Logging started " + dateString + " *** Invoicex " + main.version.toString() + " " + main.build + " tempo init:" + (t1 - t0));
        System.out.println("");

        //user language
        System.out.println(System.getProperty("user.language"));
        //setto il LOCALE
        java.util.Locale.setDefault(Locale.ITALIAN);

        //lancio programma vero
//        String[] argsMain = {""};
        String[] argsMain = args;
        try {
            main tempMain = new main(argsMain);
            this.fileIni = tempMain.fileIni;
        } catch (Exception err) {
            System.out.println("err:" + err);
            err.printStackTrace();
        }

//        System.out.println("!!!seriale:" + main.fileIni.getValue("inst", "inst_seriale"));
//        main.inst_seriale = "30C087C2";
    }

    public static void main(String[] args) {
        
        if (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0) {
            System.out.println("apple.laf.useScreenMenuBar:true");
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            Application myApp = Application.getApplication();
            myApp.addApplicationListener(new ApplicationAdapter() {

                public void handleAbout(ApplicationEvent e) {
                    if (main.getPadre() != null) {
                        JDialogAbout dialog = new JDialogAbout(main.getPadre(), true);
                        dialog.setLocationRelativeTo(null);
                        dialog.setVisible(true);
                    }
                }

                public void handleQuit(ApplicationEvent e) {
                    if (main.getPadre() != null) {
                        main.getPadre().exitForm(null);
                    }
                }
            });
        }
        new Main(args);
    }

    public static String getIntString(String key) {
        return msgRes.getString(key);
    }

    public static void copyFiles(File src, File dest) throws IOException {
        System.out.println(src.getAbsolutePath() + " (exist:" + src.exists() + ") -> " + dest.getAbsolutePath() + " (exist:" + dest.exists() + ")");
        //Check to ensure that the source is valid...
        if (!src.exists()) {
            throw new IOException("copyFiles: Can not find source: " + src.getAbsolutePath() + ".");
        } else if (!src.canRead()) { //check to ensure we have rights to the source...
            throw new IOException("copyFiles: No right to source: " + src.getAbsolutePath() + ".");
        }
        //is this a directory copy?
        if (src.isDirectory()) {
            if (!dest.exists()) { //does the destination already exist?
                //if not we need to make it exist if possible (note this is mkdirs not mkdir)
                if (!dest.mkdirs()) {
                    throw new IOException("copyFiles: Could not create direcotry: " + dest.getAbsolutePath() + ".");
                }
            }
            //get a listing of files...
            String list[] = src.list();
            //copy all the files in the list.
            for (int i = 0; i < list.length; i++) {
                File dest1 = new File(dest, list[i]);
                File src1 = new File(src, list[i]);
                copyFiles(src1, dest1);
            }
        } else {
            //This was not a directory, so lets just copy the file
            FileInputStream fin = null;
            FileOutputStream fout = null;
            byte[] buffer = new byte[4096]; //Buffer 4K at a time (you can change this).
            int bytesRead;
            try {
                //open the files for input and output
                fin = new FileInputStream(src);
                fout = new FileOutputStream(dest);
                //while bytesRead indicates a successful read, lets write...
                while ((bytesRead = fin.read(buffer)) >= 0) {
                    fout.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) { //Error copying file...
                IOException wrapper = new IOException("copyFiles: Unable to copy file: " +
                        src.getAbsolutePath() + " to " + dest.getAbsolutePath() + ".");
                wrapper.initCause(e);
                wrapper.setStackTrace(e.getStackTrace());
                throw wrapper;
            } finally { //Ensure that the files are closed (if they were open).
                if (fin != null) {
                    fin.close();
                }
                if (fout != null) {
                    fin.close();
                }
            }
        }
    }
}
