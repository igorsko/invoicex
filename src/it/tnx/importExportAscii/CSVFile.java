/*
 * CSVFile.java
 *
 * DA FINIRE !!!
 * Created on November 25, 2003, 12:27 PM
 */

package it.tnx.importExportAscii;

import java.io.*;
import java.util.*;

/**
 *
 * @author  marco
 */
public class CSVFile {
  private File file;
  private BufferedReader reader;
  private String line;
  Vector fields;
  
  /** Holds value of property fileName. */
  private String fileName;
  
  /** Holds value of property fieldSeparator. */
  private char fieldSeparator;
  
  /** Holds value of property textDelimiter. */
  private char textDelimiter;
  
  /** Holds value of property fieldCount. */
  private int fieldCount;
  
  /** Creates a new instance of CSVFile */
  public CSVFile(String fileName, char fieldSeparator, char textDelimiter) {
    this.fileName = fileName;
    this.fieldSeparator = fieldSeparator;
    this.textDelimiter = textDelimiter;
    
    try   {
      file = new File(fileName);
      reader = new BufferedReader( new FileReader( fileName ) );
    } catch (Exception e) {
      throw new RuntimeException("CSVFile: Error opening file named \"" + fileName + "\" for reading" );
    }
  }
  
  public boolean next() {
    if (fetchLine()) {
      System.out.println("line=" + line);
      System.out.println("---------------------------------------------");      
      return true;
    } else {
      return false;
    }
  }
  
  private boolean fetchLine() {
    Vector v = new Vector();
    
    line = "";
    try {
      line = reader.readLine();
      
      if (line != null) {
        String field = "";
        int s = 0;
        int e = 0;
        for (int i = 1; i < line.length(); i++) {
          if (line.charAt(i) == fieldSeparator) {
            if (e == 0) {
              e = i;
              //field
              field = line.substring(s, e);
              v.add(field);
              s = i + 1;
            } else {
              if (s > e) {
                e = i;
                //field
                field = line.substring(s, e);
                v.add(field);
                s = i + 1;
              } else {
                
              }
            }
          }
        }
        s = e + 1;
        e = line.length();
        field = line.substring(s, e);
        v.add(field);
      }
    } catch (IOException ioErr) {
      ioErr.printStackTrace();
      return false;
    }

    //debug
    for (int i = 0; i < v.size(); i++) {
      //System.out.println(i + ":" + v.get(i).toString());
    }
    
    fields = v;
    this.fieldCount = v.size();
    
    if (line == null) return false;
    return true;
  }
  
  public String getFieldValue(int i) {
    return fields.get(i).toString();
  }
  
  /** Getter for property fileName.
   * @return Value of property fileName.
   *
   */
  public String getFileName() {
    return this.fileName;
  }
  
  /** Setter for property fileName.
   * @param fileName New value of property fileName.
   *
   */
  public void setFileName(String fileName) {
  }
  
  /** Getter for property fieldSeparator.
   * @return Value of property fieldSeparator.
   *
   */
  public char getFieldSeparator() {
    return this.fieldSeparator;
  }
  
  /** Setter for property fieldSeparator.
   * @param fieldSeparator New value of property fieldSeparator.
   *
   */
  public void setFieldSeparator(char fieldSeparator) {
  }
  
  /** Getter for property textDelimiter.
   * @return Value of property textDelimiter.
   *
   */
  public char getTextDelimiter() {
    return this.textDelimiter;
  }
  
  /** Setter for property textDelimiter.
   * @param textDelimiter New value of property textDelimiter.
   *
   */
  public void setTextDelimiter(char textDelimiter) {
  }
  
  /** Getter for property fieldCount.
   * @return Value of property fieldCount.
   *
   */
  public int getFieldCount() {
    return this.fieldCount;
  }
  
  /** Setter for property fieldCount.
   * @param fieldCount New value of property fieldCount.
   *
   */
  public void setFieldCount(int fieldCount) {
  }
  
}
