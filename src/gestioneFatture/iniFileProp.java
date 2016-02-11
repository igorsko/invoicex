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
package gestioneFatture;

/**
 * Title: GestionePreventivi Description: Copyright: Copyright (c) 2001 Company:
 * TNX di Provvedi Andrea & C. s.a.s.
 *
 * @version 1.0
 */
import java.io.*;
import java.security.NoSuchAlgorithmException;

import java.util.*;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import javax.swing.tree.*;

import org.apache.commons.codec.binary.Base64;

/**
 * A class for handling Windows-style INI files. The file format is as follows:
 * <dl> <dd> [subject] - anything beginning with [ and ending with ] is a
 * subject <dd> ;comment - anything beginning with a ; is a comment <dd>
 * variable=value - anything of the format string=string is an assignment <dd>
 * comment - anything that doesn't match any of the above is a comment </dl>
 *
 * @author Steve DeGroof
 * @author <A
 * HREF="mailto:degroof@mindspring.com"><I>degroof@mindspring.com</A></I>
 * @author <A
 * HREF="http://www.mindspring.com/~degroof"><I>http://www.mindspring.com/~degroof</A></I>
 */
public class iniFileProp
        extends Object {

    public String fileName = "";
    public String realFileName = "param_prop.txt";
    static Cipher c1 = null;
    static Cipher d1 = null;
    static byte[] desKeyData = {
        (byte) 0x41, (byte) 0x32, (byte) 0x13, (byte) 0x19, (byte) 0x23, (byte) 0x27,
        (byte) 0x11, (byte) 0x04
    };
    static SecretKeySpec secretKey = new SecretKeySpec(desKeyData, "DES");

    public iniFileProp() {

        try {
            c1 = Cipher.getInstance("DES");
            d1 = Cipher.getInstance("DES");
            secretKey = new SecretKeySpec(desKeyData, "DES");
            c1.init(Cipher.ENCRYPT_MODE, secretKey);
            d1.init(Cipher.DECRYPT_MODE, secretKey);
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    /**
     * Creates an INI file object using the specified name If the named file
     * doesn't exist, create one
     *
     * @param name the name of the file
     */
    public void IniFile(String name) {
    }

    /**
     * Creates an INI file object using the specified name If the named file
     * doesn't exist, create one
     *
     * @param name the name of the file
     * @param saveOnSet save file whenever a value is set
     */
    public void IniFile(String name, boolean save) {

        /*
         //-- Opening the properties file
         try {
         FileInputStream fis = new FileInputStream("param_prop.txt");
         //-- Reading it
         Properties prop = new Properties();
         prop.load(fis);
         fis.close();
         //-- Reading a value - default value is "."
         System.out.println(prop.getProperty("defDir", "."));
         //-- Changing the value
         prop.setProperty("defDir", "c:/temp");
         //-- Writing it back
         FileOutputStream fos = new FileOutputStream("param_prop.txt");
         prop.store(fos, "tnx properties file");
         fos.close();
         } catch (Exception err) {
         err.printStackTrace();
         }
         */
    }

    /**
     * Loads and parses the INI file. Can be used to reload from file.
     */
    public void loadFile() {
    }

    /**
     * Create a new INI file.
     */
    protected boolean createFile() {

        return true;
    }

    /**
     * Reads lines, filling in subjects, variables and values.
     */
    public void parseLines() {
    }

    /**
     * Adds and assignment (i.e. "variable=value") to a subject.
     */
    protected boolean addAssignment(String subject, String assignment) {

        return true;
    }

    /**
     * Sets a specific subject/variable combination the given value. If the
     * subject doesn't exist, create it. If the variable doesn't exist, create
     * it. If saveOnChange is true, save the file;
     *
     * @param subject the subject heading (e.g. "Widget Settings")
     * @param variable the variable name (e.g. "Color")
     * @param value the value of the variable (e.g. "green")
     * @return true if successful
     */
    public synchronized boolean setValueCifrato(String subject, String variable, String value) {

        try {

            byte[] temp1 = c1.doFinal(value.getBytes());
            byte[] temp2 = Base64.encodeBase64(temp1);
            String temp3 = new String(temp2);

            return setValue(subject, variable, temp3);
        } catch (Exception err) {
            err.printStackTrace();

            return setValue(subject, variable, value);
        }
    }

    public synchronized boolean setValue(String subject, String variable, String value) {

        try {

            FileInputStream fis = new FileInputStream(realFileName);

            //-- Reading it
            SortedProperties prop = new SortedProperties();
            prop.load(fis);
            fis.close();

            //-- Changing the value
            System.out.println("set:" + subject + "_" + variable + ":" + value);
            prop.setProperty(subject + "_" + variable, value);

            //-- Writing it back
            FileOutputStream fos = new FileOutputStream(realFileName);
            prop.store(fos, "tnx properties file");
            fos.close();

            return true;
        } catch (Exception err) {
            err.printStackTrace();
        }

        return false;
    }

    public synchronized boolean setValue(String subject, String variable, Object value) {
        if (value == null) {
            value = "";
        }
        return setValue(subject, variable, String.valueOf(value));
    }

    /**
     * Sets a specific subject/variable combination the given value. If the
     * subject doesn't exist, create it. If the variable doesn't exist, create
     * it.
     *
     * @param subject the subject heading (e.g. "Widget Settings")
     * @param variable the variable name (e.g. "Color")
     * @param value the value of the variable (e.g. "green")
     * @param addToLines add the information to the lines vector
     * @return true if successful
     */
    protected boolean addValue(String subject, String variable, String value, boolean addToLines) {

        return true;
    }

    /**
     * does the line represent a subject?
     *
     * @param line a string representing a line from an INI file
     * @return true if line is a subject
     */
    protected boolean isaSubject(String line) {

        return true;
    }

    /**
     * set a line in the lines vector
     *
     * @param subject the subject heading (e.g. "Widget Settings")
     * @param variable the variable name (e.g. "Color")
     * @param value the value of the variable (e.g. "green")
     */
    protected void setLine(String subject, String variable, String value) {
    }

    /**
     * find the line containing a variable within a subject
     *
     * @param subject the subject heading (e.g. "Widget Settings")
     * @param variable the variable name (e.g. "Color")
     * @return the line number of the assignment, -1 if not found
     */
    protected int findAssignmentLine(String subject, String variable) {

        return 0;
    }

    /**
     * find the line containing a variable within a range of lines
     *
     * @param variable the variable name (e.g. "Color")
     * @param start the start of the range (inclusive)
     * @param end the end of the range (exclusive)
     * @return the line number of the assignment, -1 if not found
     */
    protected int findAssignmentBetween(String variable, int start, int end) {

        return 0;
    }

    /**
     * add a subject line to the end of the lines vector
     *
     * @param subject the subject heading (e.g. "Widget Settings")
     */
    protected void addSubjectLine(String subject) {
    }

    /**
     * find a subject line within the lines vector
     *
     * @param subject the subject heading (e.g. "Widget Settings")
     * @return the line number of the subject, -1 if not found
     */
    protected int findSubjectLine(String subject) {

        return 0;
    }

    /**
     * find the line number which is 1 past the last assignment in a subject
     * starting at a given line
     *
     * @param start the line number at which to start looking
     * @return the line number of the last assignment + 1
     */
    protected int endOfSubject(int start) {

        return 0;
    }

    /**
     * does the line represent an assignment?
     *
     * @param line a string representing a line from an INI file
     * @return true if line is an assignment
     */
    protected boolean isanAssignment(String line) {

        return true;
    }

    /**
     * get a copy of the lines vector
     */
    public Vector getLines() {

        return null;
    }

    /**
     * get a vector containing all variables in a subject
     *
     * @param subject the subject heading (e.g. "Widget Settings")
     * @return a list of variables, empty vector if subject not found
     */
    public String[] getVariables(String subject) {

        return null;
    }

    /**
     * get an array containing all subjects
     *
     * @return a list of subjects
     */
    public String[] getSubjects() {

        return null;
    }

    synchronized public boolean existKey(String subject, String variable) {
        try {
            FileInputStream fis = new FileInputStream(realFileName);
            SortedProperties prop = new SortedProperties();
            prop.load(fis);
            fis.close();
            System.out.println("exist:" + subject + "_" + variable);
            return prop.keySet().contains(subject + "_" + variable);
        } catch (Exception err) {
            err.printStackTrace();
        }
        return false;
    }

    synchronized public boolean removeKey(String subject, String variable) {
        try {
            FileInputStream fis = new FileInputStream(realFileName);
            SortedProperties prop = new SortedProperties();
            prop.load(fis);
            fis.close();
            System.out.println("exist:" + subject + "_" + variable);
            if (prop.keySet().contains(subject + "_" + variable)) {
                prop.remove(subject + "_" + variable);
                //-- Writing it back
                FileOutputStream fos = new FileOutputStream(realFileName);
                prop.store(fos, "tnx properties file");
                fos.close();
                return true;
            }
            return false;
        } catch (Exception err) {
            err.printStackTrace();
        }
        return false;
    }

    /**
     * get the value of a variable within a subject
     *
     * @param subject the subject heading (e.g. "Widget Settings")
     * @param variable the variable name (e.g. "Color")
     * @return the value of the variable (e.g. "green"), empty string if not
     * found
     */
//    public synchronized String getValueCifrato(String subject, String variable) {
    public String getValueCifrato(String subject, String variable) {

        try {

            String temp1 = getValue(subject, variable);
            byte[] temp2 = Base64.decodeBase64(temp1.getBytes());
            byte[] temp3 = d1.doFinal(temp2);
            String temp4 = new String(temp3);

            return temp4;
        } catch (Exception err) {
            err.printStackTrace();

            return getValue(subject, variable);
        }
    }

//    public synchronized String getValue(String subject, String variable) {
    public String getValue(String subject, String variable) {
        //-- Opening the properties file
        try {

            FileInputStream fis = new FileInputStream(realFileName);

            //-- Reading it
            SortedProperties prop = new SortedProperties();
            prop.load(fis);
            fis.close();

            //-- Reading a value - default value is "."
            //System.out.println("get:" + subject + "_" + variable + ":" + prop.getProperty(subject + "_" + variable, ""));

            return prop.getProperty(subject + "_" + variable, "");
        } catch (Exception err) {
            err.printStackTrace();
        }

        return "";
    }

    public synchronized String getValue(String subject, String variable, String def) {
        String ret = getValue(subject, variable);
        try {
            if (ret == null || ret.length() == 0) {
                if (!existKey(subject, variable)) {
                    return def;
                }
            }
            return ret;
        } catch (Exception ex) {
            return def;
        }
    }

    public synchronized Boolean getValueBoolean(String subject, String variable, Boolean def) {
        String ret = getValue(subject, variable);
        try {
            if (ret == null || ret.length() == 0) {
                return def;
            }
            return Boolean.parseBoolean(ret);
        } catch (Exception ex) {
            return def;
        }
    }

    /**
     * delete variable within a subject
     *
     * @param subject the subject heading (e.g. "Widget Settings")
     * @param variable the variable name (e.g. "Color")
     */
    public void deleteValue(String subject, String variable) {
    }

    /**
     * delete a subject and all its variables
     *
     * @param subject the subject heading (e.g. "Widget Settings")
     */
    public void deleteSubject(String subject) {
    }

    /**
     * save the lines vector back to the INI file
     */
    public void saveFile() {
    }

    /**
     * clean up
     */
    protected void finalize() {
    }

    //--------------------------------------------------------------------------
    //marco
    public TreeModel getTreeModel() {

        return null;
    }

    //***************************************************************************+
    //***************************************************************************+
    //***************************************************************************+
    public static String simpleCrypt(String toCrypt, boolean flagCrypt) {

        return null;
    }

    boolean scriviFile(String nomeFile, String contenuto) {

        return true;
    }

    boolean scriviFileCifrato(String nomeFile, String contenuto) {

        return true;
    }

    String apriFileCifrato(String nomeFile) {

        return "";
    }

    String apriFile(String nomeFile) {

        return "";
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        try {
            c1 = Cipher.getInstance("DES");
            d1 = Cipher.getInstance("DES");
            secretKey = new SecretKeySpec(desKeyData, "DES");
            c1.init(Cipher.ENCRYPT_MODE, secretKey);
            d1.init(Cipher.DECRYPT_MODE, secretKey);

            String temp1 = "mNU9eC/CXWAdY5xZKY5Frw\\=\\=";

            byte[] temp2 = Base64.decodeBase64(temp1.getBytes());
            byte[] temp3 = d1.doFinal(temp2);
            String temp4 = new String(temp3);
            System.out.println("temp4:" + temp4);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}