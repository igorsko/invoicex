/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.tnx.proto.mysql;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author mceccarelli
 */
public class MyOutputStream extends OutputStream {

    OutputStream myos;
    NetMonitor netMonitor;

    public MyOutputStream(OutputStream os, NetMonitor nm) {
        myos = os;
        netMonitor = nm;
    }

    public void write(byte[] b, int off, int len) throws IOException {
        netMonitor.addoutb(len - off);
//        System.err.println("write: " + new String(b, off, len));
//        try {
//            String q = "";
//            if (b[5] != 0) {
//                q = new String(b, off, len).toLowerCase().substring(5);
//            } else {
//                q = new String(b, off, len).toLowerCase().substring(12);
//            }
//            if (q.startsWith("select ") || q.startsWith("insert ") || q.startsWith("delete ") || q.startsWith("replace ") || q.startsWith("alter ") || q.startsWith("create ")) {
//                if (NetMonitor.debug) System.err.println("netMonitor:query:" + netMonitor.addq + ":" + q + getStack(new String[] {"gestioneFatture."}));
//                netMonitor.addq++;
//            }
//        } catch (Exception e) {
//        }
        myos.write(b, off, len);
    }

    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    public void write(int b) throws IOException {
        myos.write(b);
    }

    public void flush() throws IOException {
        myos.flush();
    }

    public void close() throws IOException {
        myos.close();
    }

    private String getStack(String[] classes) {
        String out = "";
        StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
        for (StackTraceElement stack : stacks) {
            for (String claz : classes) {
                if (stack.getClassName().indexOf(claz) >= 0 && !stack.getClassName().equalsIgnoreCase("it.tnx.proto.mysql.MyOutputStream")) {
                   out += "\n at " + stack.getClassName() + "." + stack.getMethodName() + "(" + stack.getFileName() + ":" + stack.getLineNumber() + ")";
                }
            }
        }
        return out;
    }
}
