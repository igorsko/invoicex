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


/**
 *
 * Title:        GestionePreventivi
 *
 * Description:
 *
 * Copyright:    Copyright (c) 2001
 *
 * Company:      TNX di Provvedi Andrea & C. s.a.s.
 *
 * @version 1.0
 *
 */
import java.io.*;

import java.util.*;

import javax.swing.tree.*;

/**
 *
 * A class for handling Windows-style INI files. The file format is as
 *
 * follows:  <dl>
 *
 *<dd>   [subject]       - anything beginning with [ and ending with ] is a subject
 *
 *<dd>   ;comment        - anything beginning with a ; is a comment
 *
 *<dd>   variable=value  - anything of the format string=string is an assignment
 *
 *<dd>   comment         - anything that doesn't match any of the above is a comment
 *
 * </dl>
 *
 * @author Steve DeGroof
 *
 * @author <A HREF="mailto:degroof@mindspring.com"><I>degroof@mindspring.com</A></I>
 *
 * @author <A HREF="http://www.mindspring.com/~degroof"><I>http://www.mindspring.com/~degroof</A></I>
 *
 */
public class iniFile
    extends Object {

    /**Actual text lines of the file stored in a vector.*/
    protected Vector lines;

    /**A vector of all subjects*/
    protected Vector subjects;

    /**A vector of variable name vectors grouped by subject*/
    protected Vector variables;

    /**A vector of variable value vectors grouped by subject*/
    protected Vector values;

    /**Name of the file*/
    protected String fileName;

    /**If true, INI file will be saved every time a value is changed. Defaults to false*/
    protected boolean saveOnChange = true;

    /**
     *
     * Creates an INI file object using the specified name
     *
     * If the named file doesn't exist, create one
     *
     * @param name the name of the file
     *
     */
    public void IniFile(String name) {

        //this(name,false);
    }

    /**
     *
     * Creates an INI file object using the specified name
     *
     * If the named file doesn't exist, create one
     *
     * @param name the name of the file
     *
     * @param saveOnSet save file whenever a value is set
     *
     */
    public void IniFile(String name, boolean save) {
        saveOnChange = save;
        fileName = name;

        if (!((new File(name)).exists())) {

            if (!createFile())

                return;
        }

        loadFile();
        parseLines();
    }

    /**
     *
     * Loads and parses the INI file. Can be used to reload from file.
     *
     */
    public void loadFile() {

        //reset all vectors
        lines = new Vector();
        subjects = new Vector();
        variables = new Vector();
        values = new Vector();

        //prima apro criptato poi salvo in chiaro e lo faccio riaprire a ini e poi cancello subito quello in chiaro
        String file = apriFileCifrato(fileName);

        //open the file - mc in stringa
        StringTokenizer tk = new StringTokenizer(file, "\r\n");
        String line = tk.nextElement().toString();

        while (line != null) {
            lines.addElement(line.trim());

            try {
                line = tk.nextElement().toString();
            } catch (Exception err) {
                line = null;
            }
        }
    }

    /**
     *
     * Create a new INI file.
     *
     */
    protected boolean createFile() {

        try {

            DataOutputStream newFile = new DataOutputStream(new FileOutputStream(fileName));
            newFile.writeBytes(";INI File: " + fileName + System.getProperty("line.separator"));
            newFile.close();

            return true;
        } catch (IOException e) {
            System.out.println("IniFile create failed: " + e.getMessage());
            e.printStackTrace();

            return false;
        }
    }

    /**
     *
     * Reads lines, filling in subjects, variables and values.
     *
     */
    protected void parseLines() {

        String currentLine = null; //current line being parsed
        String currentSubject = null; //the last subject found

        for (int i = 0; i < lines.size(); i++) { //parse all lines
            currentLine = (String)lines.elementAt(i);

            if (isaSubject(currentLine)) { //if line is a subject, set currentSubject
                currentSubject = currentLine.substring(1, currentLine.length() - 1);
            } else if (isanAssignment(currentLine)) { //if line is an assignment, add it

                String assignment = currentLine;
                addAssignment(currentSubject, assignment);
            }
        }
    }

    /**
     *
     * Adds and assignment (i.e. "variable=value") to a subject.
     *
     */
    protected boolean addAssignment(String subject, String assignment) {

        String value;
        String variable;
        int index = assignment.indexOf("=");
        variable = assignment.substring(0, index);
        value = assignment.substring(index + 1, assignment.length());

        if ((value.length() == 0) || (variable.length() == 0))

            return false;
        else

            return addValue(subject, variable, value, false);
    }

    /**
     *
     * Sets a specific subject/variable combination the given value. If the subject
     *
     * doesn't exist, create it. If the variable doesn't exist, create it. If
     *
     * saveOnChange is true, save the file;
     *
     * @param subject the subject heading (e.g. "Widget Settings")
     *
     * @param variable the variable name (e.g. "Color")
     *
     * @param value the value of the variable (e.g. "green")
     *
     * @return true if successful
     *
     */
    public boolean setValue(String subject, String variable, String value) {

        //debug
        if (variable.indexOf("password") == -1 && variable.indexOf("pwd") == -1 && variable.indexOf("pass") == -1) {
            System.out.println("ini:setValue:" + subject + ":" + variable + ":" + value);
        } else {
            System.out.println("ini:setValue:" + subject + ":" + variable + ":*******");
        }

        boolean result = addValue(subject, variable, value, true);

        //if (saveOnChange) saveFile();
        //saveFile();
        return result;
    }

    /**
     *
     * Sets a specific subject/variable combination the given value. If the subject
     *
     * doesn't exist, create it. If the variable doesn't exist, create it.
     *
     * @param subject the subject heading (e.g. "Widget Settings")
     *
     * @param variable the variable name (e.g. "Color")
     *
     * @param value the value of the variable (e.g. "green")
     *
     * @param addToLines add the information to the lines vector
     *
     * @return true if successful
     *
     */
    protected boolean addValue(String subject, String variable, String value, boolean addToLines) {

        //if no subject, quit
        if ((subject == null) || (subject.length() == 0))

            return false;

        //if no variable, quit
        if ((variable == null) || (variable.length() == 0))

            return false;

        //if the subject doesn't exist, add it to the end
        if (!subjects.contains(subject)) {
            subjects.addElement(subject);
            variables.addElement(new Vector());
            values.addElement(new Vector());
        }

        //set the value, if the variable doesn't exist, add it to the end of the subject
        int subjectIndex = subjects.indexOf(subject);
        Vector subjectVariables = (Vector)(variables.elementAt(subjectIndex));
        Vector subjectValues = (Vector)(values.elementAt(subjectIndex));

        if (!subjectVariables.contains(variable)) {
            subjectVariables.addElement(variable);
            subjectValues.addElement(value);
        }

        int variableIndex = subjectVariables.indexOf(variable);
        subjectValues.setElementAt(value, variableIndex);

        //add it to the lines vector?
        if (addToLines)
            setLine(subject, variable, value);

        return true;
    }

    /**
     *
     * does the line represent a subject?
     *
     * @param line a string representing a line from an INI file
     *
     * @return true if line is a subject
     *
     */
    protected boolean isaSubject(String line) {

        return (line.startsWith("[") && line.endsWith("]"));
    }

    /**
     *
     * set a line in the lines vector
     *
     * @param subject the subject heading (e.g. "Widget Settings")
     *
     * @param variable the variable name (e.g. "Color")
     *
     * @param value the value of the variable (e.g. "green")
     *
     */
    protected void setLine(String subject, String variable, String value) {

        //find the line containing the subject
        int subjectLine = findSubjectLine(subject);

        if (subjectLine == -1) {
            addSubjectLine(subject);
            subjectLine = lines.size() - 1;
        }

        //find the last line of the subject
        int endOfSubject = endOfSubject(subjectLine);

        //find the assignment within the subject
        int lineNumber = findAssignmentBetween(variable, subjectLine, endOfSubject);

        //if an assignment line doesn't exist, insert one, else change the existing one
        if (lineNumber == -1)
            lines.insertElementAt(variable + "=" + value, endOfSubject);
        else
            lines.setElementAt(variable + "=" + value, lineNumber);
    }

    /**
     *
     * find the line containing a variable within a subject
     *
     * @param subject the subject heading (e.g. "Widget Settings")
     *
     * @param variable the variable name (e.g. "Color")
     *
     * @return the line number of the assignment, -1 if not found
     *
     */
    protected int findAssignmentLine(String subject, String variable) {

        int start = findSubjectLine(subject);
        int end = endOfSubject(start);

        return findAssignmentBetween(variable, start, end);
    }

    /**
     *
     * find the line containing a variable within a range of lines
     *
     * @param variable the variable name (e.g. "Color")
     *
     * @param start the start of the range (inclusive)
     *
     * @param end the end of the range (exclusive)
     *
     * @return the line number of the assignment, -1 if not found
     *
     */
    protected int findAssignmentBetween(String variable, int start, int end) {

        for (int i = start; i < end; i++) {

            if (((String)lines.elementAt(i)).startsWith(variable + "="))

                return i;
        }

        return -1;
    }

    /**
     *
     * add a subject line to the end of the lines vector
     *
     * @param subject the subject heading (e.g. "Widget Settings")
     *
     */
    protected void addSubjectLine(String subject) {
        lines.addElement("[" + subject + "]");
    }

    /**
     *
     * find a subject line within the lines vector
     *
     * @param subject the subject heading (e.g. "Widget Settings")
     *
     * @return the line number of the subject, -1 if not found
     *
     */
    protected int findSubjectLine(String subject) {

        String line;
        String formattedSubject = "[" + subject + "]";

        for (int i = 0; i < lines.size(); i++) {
            line = (String)lines.elementAt(i);

            if (formattedSubject.equals(line))

                return i;
        }

        return -1;
    }

    /**
     *
     * find the line number which is 1 past the last assignment in a subject
     *
     * starting at a given line
     *
     * @param start the line number at which to start looking
     *
     * @return the line number of the last assignment + 1
     *
     */
    protected int endOfSubject(int start) {

        int endIndex = start + 1;

        if (start >= lines.size())

            return lines.size();

        for (int i = start + 1; i < lines.size(); i++) {

            if (isanAssignment((String)lines.elementAt(i)))
                endIndex = i + 1;

            if (isaSubject((String)lines.elementAt(i)))

                return endIndex;
        }

        return endIndex;
    }

    /**
     *
     * does the line represent an assignment?
     *
     * @param line a string representing a line from an INI file
     *
     * @return true if line is an assignment
     *
     */
    protected boolean isanAssignment(String line) {

        if ((line.indexOf("=") != -1) && (!line.startsWith(";")))

            return true;
        else

            return false;
    }

    /**
     *
     * get a copy of the lines vector
     *
     */
    public Vector getLines() {

        return (Vector)lines.clone();
    }

    /**
     *
     * get a vector containing all variables in a subject
     *
     * @param subject the subject heading (e.g. "Widget Settings")
     *
     * @return a list of variables, empty vector if subject not found
     *
     */
    public String[] getVariables(String subject) {

        String[] v;
        int index = subjects.indexOf(subject);

        if (index != -1) {

            Vector vars = (Vector)(variables.elementAt(index));
            v = new String[vars.size()];
            vars.copyInto(v);

            return v;
        } else {
            v = new String[0];

            return v;
        }
    }

    /**
     *
     * get an array containing all subjects
     *
     * @return a list of subjects
     *
     */
    public String[] getSubjects() {

        String[] s = new String[subjects.size()];
        subjects.copyInto(s);

        return s;
    }

    /**
     *
     * get the value of a variable within a subject
     *
     * @param subject the subject heading (e.g. "Widget Settings")
     *
     * @param variable the variable name (e.g. "Color")
     *
     * @return the value of the variable (e.g. "green"), empty string if not found
     *
     */
    public String getValue(String subject, String variable) {

        int subjectIndex = subjects.indexOf(subject);

        if (subjectIndex == -1)

            return "";

        Vector valVector = (Vector)(values.elementAt(subjectIndex));
        Vector varVector = (Vector)(variables.elementAt(subjectIndex));
        int valueIndex = varVector.indexOf(variable);

        if (valueIndex != -1) {

            return (String)(valVector.elementAt(valueIndex));
        }

        return "";
    }

    /**
     *
     * delete variable within a subject
     *
     * @param subject the subject heading (e.g. "Widget Settings")
     *
     * @param variable the variable name (e.g. "Color")
     *
     */
    public void deleteValue(String subject, String variable) {

        int subjectIndex = subjects.indexOf(subject);

        if (subjectIndex == -1)

            return;

        Vector valVector = (Vector)(values.elementAt(subjectIndex));
        Vector varVector = (Vector)(variables.elementAt(subjectIndex));
        int valueIndex = varVector.indexOf(variable);

        if (valueIndex != -1) {

            //delete from variables and values vectors
            valVector.removeElementAt(valueIndex);
            varVector.removeElementAt(valueIndex);

            //delete from lines vector
            int assignmentLine = findAssignmentLine(subject, variable);

            if (assignmentLine != -1) {
                lines.removeElementAt(assignmentLine);
            }

            //if the subject is empty, delete it
            if (varVector.size() == 0) {
                deleteSubject(subject);
            }

            if (saveOnChange)
                saveFile();
        }
    }

    /**
     *
     * delete a subject and all its variables
     *
     * @param subject the subject heading (e.g. "Widget Settings")
     *
     */
    public void deleteSubject(String subject) {

        int subjectIndex = subjects.indexOf(subject);

        if (subjectIndex == -1)

            return;

        //delete from subjects, variables and values vectors
        values.removeElementAt(subjectIndex);
        variables.removeElementAt(subjectIndex);
        subjects.removeElementAt(subjectIndex);

        //delete from lines vector
        int start = findSubjectLine(subject);
        int end = endOfSubject(start);

        for (int i = start; i < end; i++) {
            lines.removeElementAt(start);
        }

        if (saveOnChange)
            saveFile();
    }

    /**
     *
     * save the lines vector back to the INI file
     *
     */
    public void saveFile() {

        try {

            String fileCriptato = "";

            //debug
            System.out.println("ini:start save");

            for (int i = 0; i < lines.size(); i++) {
                fileCriptato += (String)(lines.elementAt(i)) + System.getProperty("line.separator");

                //debug
                if (lines.elementAt(i).toString().indexOf("password") == -1 && lines
                     .elementAt(i).toString().indexOf("pwd") == -1) {
                    System.out.println("ini:" + i + ":" + lines.elementAt(i));
                }
            }

            //debug
            System.out.println("ini:end save -------------");
            fileCriptato = simpleCrypt(fileCriptato, false);

            DataOutputStream outFile = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
            outFile.write(fileCriptato.getBytes());
            outFile.close();

            //debug
            System.out.println("ini:saveFile");
        } catch (IOException e) {
            System.out.println("IniFile save failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     *
     * clean up
     *
     */
    protected void finalize() {
        saveFile();
    }

    //--------------------------------------------------------------------------
    //marco
    public TreeModel getTreeModel() {

        DefaultMutableTreeNode top = new DefaultMutableTreeNode(this.fileName);
        DefaultMutableTreeNode category = null;
        DefaultMutableTreeNode book = null;
        category = new DefaultMutableTreeNode("Books for Java Programmers");
        top.add(category);

        return (TreeModel)top;
    }

    //***************************************************************************+
    //***************************************************************************+
    //***************************************************************************+
    public static String simpleCrypt(String toCrypt, boolean flagCrypt) {

        char[] crypt = toCrypt.toCharArray();
        char[] passwd = {
            'b', 'e', 'r', 'l', 'u', 's', 'o', 'n', 'i', 'c', 'a', 'p', 'o', 
            'd', 'e', 'l', 'l', 'a', 'm', 'a', 'f', 'i', 'a', '2', '0', '0', 
            '2'
        };

        //char[] passwd = {'a'};
        char[] out = crypt;
        int ip = 0;

        for (int i = 0; i < toCrypt.length(); i++) {

            if (i % passwd.length == 0)
                ip = 0;

            if (flagCrypt == true) {
                out[i] = (char)(crypt[i] ^ passwd[ip]);
            } else {

                //out[i] = (char)(passwd[ip] ^ crypt[i]);
                out[i] = (char)(crypt[i] ^ passwd[ip]);
            }

            ip++;
        }

        return (String.valueOf(out));
    }

    boolean scriviFile(String nomeFile, String contenuto) {

        File f = new File(nomeFile);

        try {

            FileOutputStream fout = new FileOutputStream(f);
            DataOutputStream out = new DataOutputStream(fout);
            out.write(contenuto.getBytes());
            out.flush();
            out.close();
            fout.close();
        } catch (Exception err) {
            err.printStackTrace();
        }

        return (true);
    }

    boolean scriviFileCifrato(String nomeFile, String contenuto) {

        File f = new File(nomeFile);

        try {

            FileOutputStream fout = new FileOutputStream(f);
            DataOutputStream out = new DataOutputStream(fout);
            out.write(simpleCrypt(contenuto, true).getBytes());
            out.flush();
            out.close();
            fout.close();
        } catch (Exception err) {
            err.printStackTrace();
        }

        return (true);
    }

    String apriFileCifrato(String nomeFile) {

        try {

            DataInputStream fileInput = new DataInputStream(new FileInputStream(new File(nomeFile)));
            String righe = "";
            char in;
            int a = 1;

            try {

                while (a == 1) {
                    in = (char)fileInput.readByte();
                    righe += in;
                }
            } catch (EOFException err) {
            }

            //decrypto
            righe = simpleCrypt(righe, false);

            return (righe);
        } catch (Exception err) {
            err.printStackTrace();

            return (null);
        }
    }

    String apriFile(String nomeFile) {

        try {

            DataInputStream fileInput = new DataInputStream(new FileInputStream(new File(nomeFile)));
            String righe = "";
            char in;
            int a = 1;

            try {

                while (a == 1) {
                    in = (char)fileInput.readByte();
                    righe += in;
                }
            } catch (EOFException err) {
            }

            return (righe);
        } catch (Exception err) {
            err.printStackTrace();

            return (null);
        }
    }
}