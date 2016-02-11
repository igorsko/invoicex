/*
 * Test.java
 *
 * Created on November 25, 2003, 1:00 PM
 */

package it.tnx.importExportAscii;

/**
 *
 * @author  marco
 */
public class Test {
  
  /** Creates a new instance of Test */
  public Test() {
  }
  
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    CSVFile cf = new CSVFile("/mnt/tnx/tnx/lavori/gf/listiniK/LISTINOKDOUGLAS2.csv", ',', '"');
    while (cf.next()) {
      //...
    }
    System.out.println("end");
  }
  
}
