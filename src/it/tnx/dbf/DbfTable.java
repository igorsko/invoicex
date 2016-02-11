/*
 * DbfTable.java
 *
 * Created on 13 aprile 2003, 0.29
 */

package it.tnx.dbf;

/**
 *
 * @author  marco
 */

import java.io.*;
import java.util.*;

import com.mindprod.ledatastream.*;

public class DbfTable {
  private static final short debug = 1;
  protected long recSize = 0;
  protected long recCount = 0;
  protected long currentRecord = 0;
  public boolean isCurrentRecordDeleted = false;
  protected int fieldsCount = 0;
  private DbfField[] fields;
  private String tableName;
  private Object[] record;
  private LEDataInputStream ledis;
  private boolean eof = false;
  
  /** Creates a new instance of DbfTable */
  public DbfTable(String fileName, String tableName) {
    this.tableName = tableName;
    try {
      print("********************************************************************************");
      /*
      DataInputStream bedis;
      bedis = new DataInputStream(new FileInputStream("/mnt/tnx/tnx/lavori/COMETA/Reports/archiviAdhoc/CLI_ENTI.FPT"));
      int numberoOfBlocks = bedis.readInt();
      bedis.skipBytes(2);
      short sizeOfBlocks = bedis.readShort();
      print("00:" + numberoOfBlocks);
      print("06:" + sizeOfBlocks);
      bedis.skipBytes(512 - 4 - 2 - 2);
      byte[] b = new byte[56];
      String s = "";
      String sql = "";
      long nblock = 1;
      for (int i = 0; i < 1000; i++) {
        System.out.print("block:" + (i+1) + " " + bedis.readInt() + " " + bedis.readInt() + "_");
        bedis.readInt();
        bedis.readInt();
        bedis.readFully(b);
        s = new String(b);
        System.out.print(s);
        System.out.println("");
        if (s.length() > 0) {
          sql = "insert into ah_cli_enti_memo values (" + i + "," + it.tnx.Db.pc(s, java.sql.Types.VARCHAR) +");";
          //System.out.println(sql);
        }
      }
       */
      print("********************************************************************************");
    } catch (Exception err) {
      print("********************************************************************************");
      err.printStackTrace();
    }
    
    try {
      ledis = new LEDataInputStream(new FileInputStream(fileName));
      print("00:" + hexDump(ledis.readByte()));
      print("01:" + hexDump(ledis.readByte()));
      print("02:" + hexDump(ledis.readByte()));
      print("03:" + hexDump(ledis.readByte()));
      print("04-07:" + ledis.readInt());
      short tempBytesHeader = ledis.readShort();
      fieldsCount = ((tempBytesHeader - 1) / 32) - 1;
      print("08-09:" + tempBytesHeader);
      print("fields count:" + fieldsCount);
      fields = new DbfField[fieldsCount];
      record = new Object[fieldsCount];
      short tempBytesRecord = ledis.readShort();
      print("10-11:" + tempBytesRecord);
      print("12-13:" + hexDump(ledis.readByte()) + " " + hexDump(ledis.readByte()));
      print("14:" + hexDump(ledis.readByte()));
      print("15:" + hexDump(ledis.readByte()));
      //..salto fino all'array della definizione dei campi
      ledis.skipBytes(14);
      print("30-31:" + hexDump(ledis.readByte()) + " " + hexDump(ledis.readByte()));
      for (int fc = 0; fc < fieldsCount; fc++) {
        fields[fc] = new DbfField();
        fields[fc].name = new String(readBytes(ledis, 11));
        //controllo carattere strano
        /*
        if (fields[fc].name.startsWith("CCCODI")) {
          System.out.println("8:" + (int)fields[fc].name.charAt(8));
          System.out.println("9:" + (int)fields[fc].name.charAt(9));
          System.out.println("10:" + (int)fields[fc].name.charAt(10));
        }*/
        fields[fc].name = fields[fc].name.substring(0, 8);
        //***
        fields[fc].type = new String(readBytes(ledis, 1));
        ledis.skipBytes(4);
        int len = ledis.readByte();
        if (len < 0) len = 256 + len;
        fields[fc].length = new Integer(len).shortValue();
        recSize += len;
        fields[fc].decimalPlaces = new Byte(ledis.readByte()).shortValue();
        ledis.skipBytes(2);
        ledis.skipBytes(1);
        ledis.skipBytes(10);
        ledis.skipBytes(1);
        print("f" + fc + ":" + fields[fc].name + " type:" + fields[fc].type + " lenBytes:" + fields[fc].length + " dec:" + fields[fc].decimalPlaces);
      }
      ledis.skipBytes(1);
      print("totale record size:" + recSize);
    } catch (Exception err) {
      err.printStackTrace();
    }
  }
  
  public String getSqlCreateTable() {
    if (this.fields.length > 0) {
      String sql = "create table " + this.tableName + " (";
      for (int i = 0; i < this.fields.length - 1; i++) {
        sql += this.fields[i].getSqlCreate() + ",";
      }
      sql += this.fields[this.fields.length - 1].getSqlCreate() + "\n";
      sql += ")";
      return sql;
    } else {
      System.out.println("Warning, fields length = 0");
      return null;
    }
  }
  
  public synchronized boolean next() {
    //read into record a new dbf record
    if (eof == false) {
      try {
        byte deleted = ledis.readByte();
        if (debug >= 2) System.out.println(hexDump(deleted) + String.valueOf(deleted));
        if (deleted == 42) {
          isCurrentRecordDeleted = true;
        } else {
          isCurrentRecordDeleted = false;
        }
        if (deleted != 26) {
          for (int fc = 0; fc < fieldsCount; fc++) {
            //in base al tipo di campo leggo dal file
            if (fields[fc].type.equals("C")) {
              //Char
              record[fc] = new String(readBytes(ledis, fields[fc].length));
            } else if (fields[fc].type.equals("D")) {
              //Date YYYYMMDD
              record[fc] = new String(readBytes(ledis, fields[fc].length));
            } else if (fields[fc].type.equals("F")) {
              //Numeric floating point
              record[fc] = new String(readBytes(ledis, fields[fc].length));
            } else if (fields[fc].type.equals("N")) {
              //Numeric fix position
              record[fc] = new String(readBytes(ledis, fields[fc].length));
            } else if (fields[fc].type.equals("M")) {
              //memo
              String temp = new String(readBytes(ledis, fields[fc].length)).trim();
              record[fc] = temp;
              //debug reading memo file
              if (temp.length() > 0) {
                try {
                  File f = new File("/mnt/tnx/tnx/lavori/COMETA/Reports/archiviAdhoc/CLI_ENTI.FPT");
                  RandomAccessFile raf = new RandomAccessFile(f, "r");
                  // Read a character
                  //char ch = raf.readChar();
                  // Seek to end of file
                  int byteBlock = 512 + (Integer.parseInt(temp) * 64);
                  raf.seek(byteBlock + 8);                  
                  byte[] b = new byte[56];
                  raf.read(b);
                  System.out.println("block=" + temp + ":byteBlock=" + byteBlock + ":" + new String(b));
                  raf.close();
                } catch (IOException e) {
                }
              }
            } else if (fields[fc].type.equals("V")) {
              //Variable
              record[fc] = new String(readBytes(ledis, fields[fc].length));
            } else if (fields[fc].type.equals("P")) {
              //Picture
              record[fc] = new String(readBytes(ledis, fields[fc].length));
            } else if (fields[fc].type.equals("B")) {
              //Binary
              record[fc] = new String(readBytes(ledis, fields[fc].length));
            } else if (fields[fc].type.equals("G")) {
              //General OLE Objects
              record[fc] = new String(readBytes(ledis, fields[fc].length));
            } else {
              System.out.println("!!! !!! da gestire:" + fields[fc].type);
              record[fc] = new String(readBytes(ledis, fields[fc].length));
            }
          }
          currentRecord++;
          if (debug >= 2) System.out.println("rec[" + currentRecord + "]");
          if (debug >= 3) {
            for (int r = 0; r < record.length; r++) {
              System.out.println("   " + fields[r].name + ":" + record[r] + "\t");
            }
            System.out.println("");
          }
          return true;
        } else {
          eof = true;
          return false;
        }
      } catch (EOFException err) {
        //err.printStackTrace();
        System.out.println("DbfTable::Eof");
        eof = true;
        return false;
      } catch (Exception err) {
        err.printStackTrace();
        eof = true;
        return false;
      }
    }
    return false;
  }
  
  public String getSqlInsertRecord() {
    if (this.fields.length > 0) {
      String sql = "insert into " + this.tableName + " (";
      for (int i = 0; i < this.fields.length - 1; i++) {
        sql += getFieldName(this.fields[i].name.trim()) + ",";
      }
      sql += this.fields[this.fields.length - 1].name.trim();
      sql += ") values (";
      for (int i = 0; i < this.record.length - 1; i++) {
        sql += pc(String.valueOf(this.record[i]).trim(), this.fields[i].type) + ",";
      }
      sql += pc(String.valueOf(this.record[this.fields.length - 1]).trim(), this.fields[this.fields.length - 1].type);
      sql += ")";
      return sql;
    } else {
      System.out.println("Warning, fields length = 0");
      return null;
    }
  }
  
  protected void finalize() {
    try {
      ledis.close();
    } catch (Exception err) {err.printStackTrace();}
  }
  
  //utility methods
  private static void print(String s) {
    if (debug >= 1) System.out.println(s);
  }
  
  private static String hexDump(byte b) {
    //return String.valueOf(b);
    String t = Integer.toHexString(b);
    if (t.length() == 1) {
      return "0x0" + t;
    } else {
      return "0x" + t;
    }
  }
  
  private static byte[] readBytes(DataInput dis, int howmany) {
    byte[] b = new byte[howmany];
    try {
      for (int i = 0; i < howmany; i++) {
        b[i] = dis.readByte();
      }
    } catch (EOFException eofErr) {
      //err.printStackTrace();
      System.out.println("DbfTable:readBytes::EOF");
    } catch (IOException err) {
      err.printStackTrace();
    }
    return b;
  }
  
  //utility db methods
  public static String aa(String stringa) {
    //aggiunge apice al singolo
    if (stringa != null) {
      if (stringa.length() > 0 ) {
        return (replaceChars(stringa,'\'',"''"));
      }
    }
    return ("");
  }
  
  public static String pc(String campo, String tipoCampo) {
    //prepara il campo per sql
    if (tipoCampo.equalsIgnoreCase("N")) {
      if (campo.length() == 0) {
        return("null");
      } else {
        if (it.tnx.Checks.isNumber(campo)) {
          return("("+campo+")");
        } else {
          return("(0)");
        }
      }
    } else if (tipoCampo.equalsIgnoreCase("C")) {
      return("'"+aa(campo)+"'");
    } else if (tipoCampo.equalsIgnoreCase("M")) {
      return("'"+aa(campo)+"'");
    } else if (tipoCampo.equalsIgnoreCase("D")) {
      //from YYYYMMDD to 'YYYY-MM-DD'
      if (campo.trim().length() == 8) {
        return("'" + campo.substring(0, 4) + "-" + campo.substring(4, 6) + "-" + campo.substring(6) + "'");
      } else {
        return("0");
      }
    } else {
      return("'"+aa(campo)+"'");
    }
  }
  
  public static String nz(String valore, String seNullo) {
    if (valore==null) return (seNullo);
    return (valore);
  }
  
  public static String nz(Object valore, String seNullo) {
    if (valore==null) return (seNullo);
    return (valore.toString());
  }
  
  public static String replaceChars(String stri,char daTrov, String daMett) {
    int leng=stri.length();
    String prim="";
    String dopo="";
    String risu="";
    int i=0;
    int oldI=0;
    while (i<leng) {
      if (stri.charAt(i)==daTrov) {
        prim=stri.substring(oldI,i);
        risu=risu+prim+daMett;
        oldI=i+1;
      }
      i++;
    }
    risu=risu+stri.substring(oldI,leng);
    
    return risu;
  }
  
  public static String replaceStrings(String object,String toFind, String toSubstitute) {
    /*
    StringTokenizer tokenizer = new StringTokenizer(object,toFind);
    String temp = "";
    while (tokenizer.hasMoreTokens()) {
       temp += tokenizer.nextToken() + toSubstitute;
       System.out.println("tokenizer:" + temp);
    }
    return(temp);
     */
    return (object.replaceAll("\\"+toFind,toSubstitute));
  }

    public static String getFieldName(String fieldName) {
        return fieldName.trim().replace(' ', '_');
    }
  
}