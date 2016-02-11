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
 * DumpThread.java
 *
 * Created on 23-giu-2007, 14.37.57
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gestioneFatture;

import it.tnx.Db;
import it.tnx.JDialogMessage;
import it.tnx.JFrameMessage;
import it.tnx.Zip;
import it.tnx.commons.SwingUtils;
import it.tnx.commons.SystemUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Statement;
import java.util.Comparator;
import java.util.Date;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author marco
 */

public class DumpThread extends java.lang.Thread {
    javax.swing.JTextArea text;
    javax.swing.JButton chiudi;
    javax.swing.JProgressBar bar;
    javax.swing.JScrollPane scroll;
    public String nomeFileDump;
    public String nomeFileDump2;
    boolean sendOnline = false;
    JFrameMessage frame = null;
    JDialogMessage dialog = null;
    
    public DumpThread(it.tnx.JFrameMessage frame) {
        this.sendOnline = false;
        this.text = frame.getTextArea();
        this.chiudi = frame.getPulsanteChiudi();
        this.bar = frame.getAvanzamento();
        this.scroll = frame.getTextAreaScrollPane();
        frame.setTitle("Creazione copia di sicurezza dei dati");
        this.frame = frame;
    }

    public DumpThread(it.tnx.JDialogMessage dialog) {
        this.sendOnline = false;
        this.text = dialog.getTextArea();
        this.chiudi = dialog.getPulsanteChiudi();
        this.bar = dialog.getAvanzamento();
        this.scroll = dialog.getTextAreaScrollPane();
        dialog.setTitle("Creazione copia di sicurezza dei dati");
        this.dialog = dialog;
    }

    synchronized public void run() {
        Exception error1 = null;

        chiudi.setEnabled(false);
        bar.setIndeterminate(true);
        text.setAutoscrolls(true);
        text.append("Inizio backup database\n");
        
        //creo se non esiste la cartella backup
        text.append("Controllo cartella 'backup'");
        
        File dirBackup = new File(main.wd + "backup");
        
        if (!dirBackup.exists()) {
            dirBackup.mkdir();
        }
        
        text.append("...ok\n");
        
        //test con mysqldump
        //it.tnx.shell.Exec exec = new it.tnx.shell.Exec();
        //exec.execute("/usr/bin/mysqldump", "");
        //exec.execute("/usr/bin/mysqldump", Db.dbNameDB + " --add-drop-table");
        //backup con funzione senza eseguire comandi esterni
        try {
            nomeFileDump = it.tnx.shell.CurrentDir.getCurrentDir() + "/backup/dump";
            
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd_HHmm");
            Date d = new Date();
            nomeFileDump += "_" + sdf.format(d) + ".txt";
            
            if (nomeFileDump2 != null) {
                nomeFileDump = it.tnx.shell.CurrentDir.getCurrentDir() + "/backup/" + nomeFileDump2;
                File test = new File(nomeFileDump);
                if (test.exists()) {
                    test.delete();
                }
            }
            
            java.io.FileOutputStream fos = new FileOutputStream(nomeFileDump, false);

            PrintStream o = new PrintStream(fos, true, "ISO-8859-1");
            o.println("SET foreign_key_checks = 0;");
            o.println("SET storage_engine = MYISAM;");
            
            Vector tables = new Vector();
            
            Statement s = Db.getConn().createStatement();
            java.sql.ResultSet r = s.executeQuery("show full tables");
            
            while (r.next()) {
                String tab = r.getString(1);
                String tipo = r.getString(2);
                text.append("backup " + tipo + " '" + tab + "'");
                it.tnx.Util.dumpTable(tab, Db.getConn(), fos, tipo);
//                it.tnx.Util.dumpTableOld(r.getString(1),Db.getConn(), fos);
                System.out.println(tab + " : dumped");
                text.append("...ok\n");
                text.setCaretPosition(text.getText().length());
            }
            
            o.println("SET foreign_key_checks = 1;");

            s.close();
            r.close();

            File fnomeFileDump = new File(nomeFileDump);

            fos.flush();
            fos.close();

            try {                
//                System.out.println("debug cartella di backup in documenti:");
                System.out.println("SystemUtils.getUserDocumentsFolder():" + SystemUtils.getUserDocumentsFolder());
//                System.out.println("File.separator + Invoicex + File.separator + backup:" + File.separator + "Invoicex" + File.separator + "backup");
                final File fnomeFileDumpDocumentiDir = new File(SystemUtils.getUserDocumentsFolder() + File.separator + "Invoicex" + File.separator + "backup");
                boolean retdir = fnomeFileDumpDocumentiDir.mkdirs();
                System.out.println("creazione cartella backup in documenti:" + retdir);
                final File fnomeFileDumpDocumenti = new File(SystemUtils.getUserDocumentsFolder() + File.separator + "Invoicex" + File.separator + "backup" + File.separator + "dump_" + sdf.format(d) + ".txt");
                FileUtils.copyFile(fnomeFileDump, fnomeFileDumpDocumenti);
                //text.append("\nIl backup si trova nel file\n'" + fnomeFileDump.getAbsolutePath() + "'\n\n");
                text.append("\nIl backup si trova nel file\n'" + fnomeFileDumpDocumenti.getAbsolutePath() + "'\n\n");

                if (frame != null) {
                    JButton bopen = new JButton("Apri cartella backup");
                    bopen.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            Util.start2(fnomeFileDumpDocumentiDir.getAbsolutePath());
                        }
                    });
                    frame.panbasso.add(bopen);
                    frame.validate();
                }
                if (dialog != null) {
                    JButton bopen = new JButton("Apri cartella backup");
                    bopen.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            Util.start2(fnomeFileDumpDocumentiDir.getAbsolutePath());
                        }
                    });
                    dialog.panbasso.add(bopen);
                    dialog.validate();
                }
            } catch (Exception e) {
                error1 = e;
                text.append("\nErrore:\n'" + e.toString() + "'\n\n");
                text.append("Debug, User Folder = '" + SystemUtils.getUserDocumentsFolder() + "'\n\n");
                SwingUtils.showErrorMessage(main.getPadreFrame(), e.getMessage());
                e.printStackTrace();
            }

        } catch (Exception err) {
            err.printStackTrace();
        }

        Comparator filecomp = new Comparator() {
            public int compare(Object o1, Object o2) {
                File f1 = (File)o1;
                File f2 = (File)o2;
                if (f1.lastModified() > f2.lastModified()) {
                    return -1;
                } else if (f1.lastModified() < f2.lastModified()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        };
        
        //pulisco da eventuali vecchi bakcup, diciamo che ne lascio gli ultimi 10
        File dir = new File(it.tnx.shell.CurrentDir.getCurrentDir() + "/backup");
        File[] lista = dir.listFiles();
        Vector listav = it.tnx.Util.getVectorFromArray(lista);
        java.util.Collections.sort(listav, filecomp);
        for (int i = 0; i < listav.size(); i++) {
            System.out.println("files[" + i + "]:" + ((File)listav.get(i)).getName() + "\t\t" + ((File)listav.get(i)).lastModified());
            if (i > 20) {
                try {
                    System.out.println("delete file:" + ((File)listav.get(i)).getCanonicalFile() + "\t deleteed:" + ((File)listav.get(i)).delete());
                } catch (IOException err) {
                    err.printStackTrace();
                }
            }
        }
        //anche nella cartella documents pulisco da eventuali vecchi bakcup, diciamo che ne lascio gli ultimi 10
        try {
            dir = new File(SystemUtils.getUserDocumentsFolder() + File.separator + "Invoicex" + File.separator + "backup");
            lista = dir.listFiles();
            listav = it.tnx.Util.getVectorFromArray(lista);
            java.util.Collections.sort(listav, filecomp);
            for (int i = 0; i < listav.size(); i++) {
                System.out.println("files[" + i + "]:" + ((File)listav.get(i)).getName() + "\t\t" + ((File)listav.get(i)).lastModified());
                if (i > 20) {
                    try {
                        System.out.println("delete file:" + ((File)listav.get(i)).getCanonicalFile() + "\t deleteed:" + ((File)listav.get(i)).delete());
                    } catch (IOException err) {
                        err.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        bar.setIndeterminate(false);
        bar.setStringPainted(true);
        bar.setValue(100);
        if (error1 == null) {
            text.append("\nBackup completato.\n\n");
        }
        text.setCaretPosition(text.getText().length());
        
        chiudi.setEnabled(true);

        notify();
    }
}
