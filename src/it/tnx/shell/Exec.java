/*
 * Exec.java
 *
 * Created on October 10, 2003, 6:42 PM
 */

package it.tnx.shell;

// GoodWindowsExec.java
import java.util.*;
import java.io.*;

class StreamGobbler extends Thread {
  InputStream is;
  String type;
  
  StreamGobbler(InputStream is, String type) {
    this.is = is;
    this.type = type;
  }
  
  public void run() {
    try {
      InputStreamReader isr = new InputStreamReader(is);
      BufferedReader br = new BufferedReader(isr);
      String line=null;
      while ( (line = br.readLine()) != null)
        System.out.println(type + ">" + line);
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }
}

public class Exec {
  public static void execute(String command, String args) {
    try {
      String osName = System.getProperty("os.name" );
      String[] cmd = null;
      if (osName.indexOf("95") >= 0 ||osName.indexOf("98") >= 0 || osName.toLowerCase().indexOf("me") >= 0) {
        cmd = new String[4];
        cmd[0] = "command.com" ;
        cmd[1] = "/C" ;
        cmd[2] = command;
        cmd[3] = args;
      } else if (osName.toLowerCase().indexOf("linux") >= 0) {
        cmd = new String[2];
        cmd[0] = command;
        cmd[1] = args;
      } else {
        cmd = new String[4];
        cmd[0] = "cmd.exe" ;
        cmd[1] = "/C" ;
        cmd[2] = command;
        cmd[3] = args;
      }
      
      Runtime rt = Runtime.getRuntime();
      System.out.println("Execing " + command);
      Process proc = rt.exec(cmd);
      // any error message?
      StreamGobbler errorGobbler = new
      StreamGobbler(proc.getErrorStream(), "ERROR");
      
      // any output?
      StreamGobbler outputGobbler = new
      StreamGobbler(proc.getInputStream(), "OUTPUT");
      
      // kick them off
      errorGobbler.start();
      outputGobbler.start();
      
      // any error???
      int exitVal = proc.waitFor();
      System.out.println("ExitValue: " + exitVal);      
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }
}
