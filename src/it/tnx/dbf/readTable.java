/*
 * readTable.java
 *
 * Created on 12 aprile 2003, 14.30
 */

package it.tnx.dbf;

/**
 *
 * @author  marco
 */
import java.io.*;
import java.util.*;

import com.mindprod.ledatastream.*;

public class readTable {
  
  /** Creates a new instance of readTable */
  public readTable() {
  }
  
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    Vector bs = new Vector();
    boolean ok = true;
    Vector fName = new Vector();
    Vector fType = new Vector();
    Vector fLength = new Vector();
    Vector fPrecision = new Vector();
    Vector fDecimalPlaces = new Vector();
    Vector record = new Vector();
    int recSize = 0;
    
    try {
      //test
      //DataInputStream dis = new DataInputStream(new FileInputStream("/mnt/tnx/tnx/lavori/COMETA/Reports/adhoc/tabelle.dbf"));
      //LEDataInputStream ledis = new LEDataInputStream(new FileInputStream("/mnt/tnx/tnx/lavori/COMETA/Reports/adhoc/tabelle.dbf"));
      LEDataInputStream ledis = new LEDataInputStream(new FileInputStream("/mnt/tnx/tnx/lavori/COMETA/Reports/adhoc/art_icol.dbf"));
      print("00:" + hexDump(ledis.readByte()));
      //ledis.skipBytes(3);
      print("01:" + hexDump(ledis.readByte()));
      print("02:" + hexDump(ledis.readByte()));
      print("03:" + hexDump(ledis.readByte()));
      print("04-07:" + ledis.readInt());
      short tempBytesHeader = ledis.readShort();
      int fieldsCount = ((tempBytesHeader - 1) / 32) - 1;
      print("08-09:" + tempBytesHeader);
      print("num of fields:" + fieldsCount);
      short tempBytesRecord = ledis.readShort();
      print("10-11:" + tempBytesRecord);
      print("12-13:" + hexDump(ledis.readByte()) + " " + hexDump(ledis.readByte()));
      print("14:" + hexDump(ledis.readByte()));
      print("15:" + hexDump(ledis.readByte()));
      //..salto fino all'array della definizione dei campi
      ledis.skipBytes(14);
      print("30-31:" + hexDump(ledis.readByte()) + " " + hexDump(ledis.readByte()));
      for (int fc = 0; fc < fieldsCount; fc++) {
        fName.add(new String(readBytes(ledis, 11)));
        fType.add(new String(readBytes(ledis, 1)));
        ledis.skipBytes(4);
        int len = ledis.readByte();
        if (len < 0) len = 256 + len;
        fLength.add(new Integer(len));
        recSize += len;
        
        fDecimalPlaces.add(new Byte(ledis.readByte()));
        ledis.skipBytes(2);
        ledis.skipBytes(1);
        ledis.skipBytes(10);
        ledis.skipBytes(1);
        
        print("f" + fc + ":" + fName.get(fc) + " type:" + fType.get(fc) + " lenBytes:" + fLength.get(fc) + " dec:" + fDecimalPlaces.get(fc));
      }
      print("totale record size:" + recSize);
      
      print("inizio lettura records..." + "n+1:" + hexDump(ledis.readByte()));
      
      boolean cont = true;
      long rc = 1;
      while(cont) {
        System.out.println("r:" + new String(readBytes(ledis, tempBytesRecord)));
        record.clear();
        byte deleted = ledis.readByte();
        System.out.println(hexDump(deleted) + String.valueOf(deleted));
        if (deleted != 26) {
          for (int fc = 0; fc < fieldsCount; fc++) {
            //in base al tipo di campo leggo dal file
            if (fType.get(fc).equals("C")) {
              //System.out.println(fLength.get(fc).toString());
              record.add(new String(readBytes(ledis, Integer.parseInt(fLength.get(fc).toString()))));
            } else if (fType.get(fc).equals("N")) {
              //System.out.println(fLength.get(fc).toString());
              record.add(new String(readBytes(ledis, Integer.parseInt(fLength.get(fc).toString()))));
            } else {
              System.out.println("!!! da gestire:" + fType.get(fc));
            }
          }
          System.out.print("rec[" + rc + "]");
          for (int r = 0; r < record.size(); r++) {
            System.out.print(record.get(r) + "\t");
          }
          System.out.println("");
          
          cont = true;
          rc++;
        } else {
          cont = false;
          System.out.println("fine tabella");
        }
      }
      
      //dis.close();
      ledis.close();
    } catch (Exception err) {
      err.printStackTrace();
    }
  }
  
  public static String hexDump(byte b) {
    //return String.valueOf(b);
    String t = Integer.toHexString(b);
    if (t.length() == 1) {
      return "0x0" + t;
    } else {
      return "0x" + t;
    }
  }
  
  public static void print(String s) {
    System.out.println(s);
  }
  
  
  public static int readIntLittleEndian(DataInputStream dis) {
    // 4 bytes
    int accum = 0;
    try {
      for (int shiftBy = 0; shiftBy < 32; shiftBy+=8 ) {
        accum |= (dis.readByte() & 0xff) << shiftBy;
      } return accum;
    } catch (Exception err) {err.printStackTrace();return 0;}
  }
  
  public static byte[] readBytes(DataInput dis, int howmany) {
    byte[] b = new byte[howmany];
    try {
      for (int i = 0; i < howmany; i++) {
        b[i] = dis.readByte();
      }
    } catch (IOException err) {err.printStackTrace();}
    return b;
  }
}
