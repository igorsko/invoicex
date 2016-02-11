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



package gestioneFatture;

import it.tnx.commons.CastUtils;
import it.tnx.commons.SwingUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.commons.lang.StringUtils;

public class Util {
    
    private static int numFiles = 0;
    public static int SCREEN_RES_640x480 = 1;
    public static int SCREEN_RES_800x600 = 2;
    public static int SCREEN_RES_1024x768 = 3;
    
    /** Creates a new instance of Util */
    public Util() {
    }
    
    public static void startSimple(String comando) {
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(comando);
        } catch (Exception err) {
            err.printStackTrace();
        }
    }
    
    public static void start(String comando) {
/*
//        String osName = System.getProperty("os.name").toLowerCase();
//        if (osName.startsWith("mac os")) {
//            try {
//                System.out.println("start:mac os(" + osName + "):" + comando);
//                Runtime.getRuntime().exec("open " + comando);
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        } else {
//            try {
//                org.jdesktop.jdic.desktop.Desktop.open(new File(comando));
//            } catch (org.jdesktop.jdic.desktop.DesktopException e) {
//                e.printStackTrace();
//            }
//        }

//        if (main.getPersonalContain("open")) {
            //problema su alcuni computer non funziona la open con le librerie desktop
            String os = System.getProperty("os.name").toLowerCase();
            File file = new File(comando);
            if (os.startsWith("mac")) {
                try {
                    Runtime.getRuntime().exec("open " + file.toString());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (os.equals("windows 95") || os.equals("windows 98") || os.equals("windows me")) {
                try {
                    Runtime.getRuntime().exec("start \"" + file.toString() + "\"");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (os.equals("windows nt") || os.equals("windows 2000") || os.equals("windows xp") || os.equals("windows vista") || os.startsWith("windows")) {
                try {
                    String x = null;
                    if (file.isDirectory()) {
//                        x = "explorer /select,\"" + file.toString() + "\"";
                        x = "explorer \"" + file.toString() + "\"";
                    } else {
                        x = "cmd /C \"" + file.toString() + "\"";
//                        x = "rundll32 SHELL32.DLL,ShellExec_RunDLL \"" + file.toString() + "\"";
                    }
                    System.out.println("x = " + x);
                    Runtime.getRuntime().exec(x);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                //linux dividere fra kde e gnome
                //kfmclient openURL ...
                //gnome-open ...
                try {
                    Runtime.getRuntime().exec("kfmclient openURL " + file.toString());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    try {
                        Runtime.getRuntime().exec("gnome-open " + file.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
//        } else {
//            SwingUtils.open(new File(comando));
//        }

 */
        start2(comando);
    }

    public static void start2(String cosa) {
        //init per personal open        
        if (main.getPersonalContain("open")) {
            String p = main.fileIni.getValue("personalizzazioni", "personalizzazioni");
            p = StringUtils.replace(p, "open", "");
            main.fileIni.setValue("personalizzazioni", "personalizzazioni", p);
            main.fileIni.setValue("varie", "apertura_file", "1");
            String os = System.getProperty("os.name").toLowerCase();
            if (os.startsWith("windows")) {
                main.fileIni.setValue("varie", "apertura_file_comando_cartella", "explorer \"{file}\"");
                main.fileIni.setValue("varie", "apertura_file_comando_file", "cmd /C \"{file}\"");
                main.fileIni.setValue("varie", "apertura_file_comando_pdf", "cmd /C \"{file}\"");
            } else if (os.startsWith("mac")) {
                main.fileIni.setValue("varie", "apertura_file_comando_cartella", "open {file}");
                main.fileIni.setValue("varie", "apertura_file_comando_file", "open {file}");
                main.fileIni.setValue("varie", "apertura_file_comando_pdf", "open {file}");
            } else {
                main.fileIni.setValue("varie", "apertura_file_comando_cartella", "gnome-open {file}");
                main.fileIni.setValue("varie", "apertura_file_comando_file", "gnome-open {file}");
                main.fileIni.setValue("varie", "apertura_file_comando_pdf", "gnome-open {file}");
            }
        }

        if (CastUtils.toInteger0(main.fileIni.getValue("varie", "apertura_file", "0")) == 0) {
            //automatico
            SwingUtils.open(new File(cosa));
        } else {
            File ftest = new File(cosa);
            if (ftest.isDirectory()) {
                apriCartellaManuale(cosa);
            } else if (ftest.getName().toLowerCase().endsWith("pdf")) {
                apriPdfManuale(cosa);
            } else {
                apriFileManuale(cosa);
            }
        }
    }

    public static void apriCartellaManuale(String file) {
        String cmd = main.fileIni.getValue("varie", "apertura_file_comando_cartella", "");
        cmd = StringUtils.replace(cmd, "{file}", file);
        System.out.println("apriCartellaManuale cmd:[" + cmd + "]");
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void apriFileManuale(String file) {
        String cmd = main.fileIni.getValue("varie", "apertura_file_comando_file", "");
        cmd = StringUtils.replace(cmd, "{file}", file);
        System.out.println("apriFileManuale cmd:[" + cmd + "]");
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void apriPdfManuale(String file) {
        String cmd = main.fileIni.getValue("varie", "apertura_file_comando_pdf", "");
        cmd = StringUtils.replace(cmd, "{file}", file);
        System.out.println("apriPdfManuale cmd:[" + cmd + "]");
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void traverse(File f) {
        
        if (f.isDirectory()) {
            System.out.println(f.getPath() + "\\" + f.getName());
            
            String[] children = f.list();
            
            for (int i = 0; i < children.length; i++) {
                traverse(new File(f, children[i]));
            }
        } else {
            numFiles++;
            System.out.println(f.getPath() + "\\" + f.getName() + " " + f.lastModified() + " " + f.length());
        }
    }
    
    public String encrypt(String stringa) {
        
        try {
            
            KeyGenerator keygen = KeyGenerator.getInstance("DES");
            SecretKey desKey = keygen.generateKey();
            Cipher desCipher;
            
            // Create the cipher
            desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            
            // Initialize the cipher for encryption
            desCipher.init(Cipher.ENCRYPT_MODE, desKey);
            
            // Our cleartext
            byte[] cleartext = "This is just an example".getBytes();
            
            // Encrypt the cleartext
            byte[] ciphertext = desCipher.doFinal(cleartext);
            System.out.println("cifrato:" + ciphertext);
            javax.swing.JOptionPane.showMessageDialog(null, ciphertext);
            
            // Initialize the same cipher for decryption
            desCipher.init(Cipher.DECRYPT_MODE, desKey);
            
            // Decrypt the ciphertext
            byte[] cleartext1 = desCipher.doFinal(ciphertext);
            System.out.println("clear:" + cleartext1);
            javax.swing.JOptionPane.showMessageDialog(null, cleartext1);
            
            return ("");
        } catch (Exception err) {
            javax.swing.JOptionPane.showMessageDialog(null, err);
            err.printStackTrace();
            
            return (null);
        }
    }
    
    public String decrypt(String stringa) {
        
        PBEKeySpec pbeKeySpec;
        PBEParameterSpec pbeParamSpec;
        SecretKeyFactory keyFac;
        
        // Salt
        byte[] salt = {
            (byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c, (byte)0x7e,
            (byte)0xc8, (byte)0xee, (byte)0x99
        };
        
        // Iteration count
        int count = 20;
        
        // Create PBE parameter set
        pbeParamSpec = new PBEParameterSpec(salt, count);
        
        // Prompt user for encryption password.
        // Collect user password as char array (using the
        // "readPasswd" method from above), and convert
        // it into a SecretKey object, using a PBE key
        // factory.
        char[] paaaaas = { 'k', '4', 'q', 'l' };
        
        try {
            pbeKeySpec = new PBEKeySpec(paaaaas);
            keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            
            SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);
            javax.swing.JOptionPane.showMessageDialog(null, pbeKey.toString());
            
            // Create PBE Cipher
            Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
            
            // Initialize PBE Cipher with key and parameters
            pbeCipher.init(Cipher.DECRYPT_MODE, pbeKey, pbeParamSpec);
            
            // Our cleartext
            byte[] cleartext = stringa.getBytes();
            
            // Encrypt the cleartext
            byte[] ciphertext = pbeCipher.doFinal(cleartext);
            
            return (ciphertext.toString());
        } catch (Exception err) {
            
            return (null);
        }
    }
    
    public static String getHDSerialId_dir() {
        if (main.hdserial != null) {
            return main.hdserial;
        }
        return main.serial;
    }
    
    public static int getScreenResolution() {
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        if (screenSize.getWidth() == 640) {
            return (SCREEN_RES_640x480);
        } else if (screenSize.getWidth() == 800) {
            return (SCREEN_RES_800x600);
        } else if (screenSize.getWidth() >= 1024) {
            return (SCREEN_RES_1024x768);
        } else {
            return (SCREEN_RES_800x600);
        }
    }
    
    public static String getIniValue(String sezione, String chiave) {
        if (main.fileIni == null) return null;
        if (main.flagWebStart == false) {
            iniFileProp fileIni = main.fileIni;
            return fileIni.getValue(sezione, chiave);
        } else {
            //faccio scelte da qui per ora, da finire
            if (sezione.equalsIgnoreCase("personalizzazioni") && chiave.equalsIgnoreCase("personalizzazioni")) {
                return ("tnx1");
            } else {
                return ("");
            }
        }
    }
    
    public static double getSconto(double prezzoLordo, double prezzoNetto) {
        double temp = 0;
        temp = 100 - (prezzoNetto * 100 / prezzoLordo);
        return temp;
    }
    
    public static void showErrorMsg(java.awt.Component parent, Exception err) {
        javax.swing.JOptionPane.showMessageDialog(parent, err.getLocalizedMessage(), "Errore", javax.swing.JOptionPane.ERROR_MESSAGE);
        err.printStackTrace();
    }
    
    // This method copies a given File into another one.
    // If the given File is a directory, it will recursively copy all subdirectories
    // and the files in them.
    public static void copyFile(File source, File dest)
            throws IOException {
        
        if (source.isDirectory()) {
            dest.mkdir();
            File[] filesInThisDir = source.listFiles();
            for (int i = 0; i < filesInThisDir.length; i++) {
                copyFile(filesInThisDir[i], new File(dest, filesInThisDir[i].getName()));
            }
        } else {
            
            InputStream istream = new FileInputStream(source);
            OutputStream ostream = new FileOutputStream(dest);
            dest.createNewFile();
            
            while (true) {
                int nextByte = istream.read();
                if (nextByte == -1)
                    break;
                ostream.write(nextByte);
            }
            
            istream.close();
            ostream.close();
        }
    }
    
    public static void deleteFilesFromDir(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                files[i].delete();
            }
        }
    }
    
    //files
    public static String getFileNameFromFullFileName(String fileName, String separator) {
        int li = fileName.lastIndexOf(separator);
        return fileName.substring(li + 1);
    }
    
    public static String getPathFromFullFileName(String fileName, String separator) {
        int li = fileName.lastIndexOf(separator);
        return fileName.substring(0, li);
    }
}