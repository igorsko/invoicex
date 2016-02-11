/*
 * DbfTable.java
 *
 * Created on 13 aprile 2003, 0.29
 */

package it.tnx.reg;

/**
 *
 * @author  marco
 */

import java.io.*;
import java.util.*;

import com.mindprod.ledatastream.*;

public class Registry {
  private static final short debug = 1;
  private LEDataInputStream ledis;
  
  /** Creates a new instance of DbfTable */
  public Registry(String fileName) {
    try {
      ledis = new LEDataInputStream(new FileInputStream(fileName));
      print(":" + hexDump(ledis.readInt()) + "ASCII-CREG");
      print(":" + hexDump(ledis.readInt()) + "Offset of first RGDB-BLOCK");
      print(":" + hexDump(ledis.readInt()) + "no. of RGDB-block");
      //..salto fino all'array della definizione dei campi
      ledis.skipBytes(4 * 5);
      print(":" + hexDump(ledis.readInt()) + "ASCII-RGKN");
      print(":" + hexDump(ledis.readInt()) + "Size of RGKN-BLOCK in bytes");
      print(":" + hexDump(ledis.readInt()) + "Rel. offset of the root record");
      print(":" + hexDump(ledis.readInt()) + ".");
      print(":" + hexDump(ledis.readInt()) + ".");
      print(":" + hexDump(ledis.readInt()) + ".");
      print(":" + hexDump(ledis.readInt()) + ".");
      print(":" + hexDump(ledis.readInt()) + ".");
      print(":" + hexDump(ledis.readInt()) + ".");
      print(":" + hexDump(ledis.readInt()) + ".");
      print(":" + hexDump(ledis.readInt()) + ".");
print(":" + hexDump(ledis.readInt()) + ".");
print(":" + hexDump(ledis.readInt()) + ".");
print(":" + hexDump(ledis.readInt()) + ".");
print(":" + hexDump(ledis.readInt()) + ".");
print(":" + hexDump(ledis.readInt()) + ".");
print(":" + hexDump(ledis.readInt()) + ".");
print(":" + hexDump(ledis.readInt()) + ".");
      
      /*
      int r = 0;
      for (int i = 0; i < 70000; i++) {        
        r = ledis.readInt();
        if (hexDump(r).equalsIgnoreCase("0x4e4b4752")) {
          print("00:" + hexDump(r) + "ASCII-CREG" + " count:" + i);
        }
      }
      */
    } catch (Exception err) {
      err.printStackTrace();
    }
  }
    
  public static void main(String[] args) {
    //DbfTable table = new DbfTable("/home/marco/cometa/Reports/adhoc/art_icol.dbf", "art_icoli");
    Registry reg = new Registry("g:/user.dat");
    System.runFinalization();
  }

  protected void finalize() {
    try {
      System.out.println("finalizing: " + this.toString());
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
      return "0x0" + t + "[" + b + "]";
      //return "0x0" + t;
    } else {
      return "0x" + t + "[" + b + "]";
      //return "0x" + t;
    }
  }
  
  private static String hexDump(int b) {
    //return String.valueOf(b);
    String t = Integer.toHexString(b);
    if (t.length() == 1) {
      return "0x0" + t + "[" + b + "]";
      //return "0x0" + t;
    } else {
      return "0x" + t + "[" + b + "]";
      //return "0x" + t;
    }
  }

  private static byte[] readBytes(DataInput dis, int howmany) {
    byte[] b = new byte[howmany];
    try {
      for (int i = 0; i < howmany; i++) {
        b[i] = dis.readByte();
      }
    } catch (IOException err) {err.printStackTrace();}
    return b;
  }  
}