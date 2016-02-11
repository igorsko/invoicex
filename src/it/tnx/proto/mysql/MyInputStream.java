/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.tnx.proto.mysql;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author mceccarelli
 */
public class MyInputStream extends InputStream {

    InputStream myis;
    NetMonitor netMonitor;

    public MyInputStream(InputStream os, NetMonitor nm) {
        myis = os;
        netMonitor = nm;
    }

    public long skip(long n) throws IOException {
        return myis.skip(n);
    }

    public synchronized void reset() throws IOException {
        myis.reset();
    }

    public int read(byte[] b, int off, int len) throws IOException {
        netMonitor.addinb(len - off);
//        System.err.println("read: " + new String(b, off, len));
        return myis.read(b, off, len);
    }

    public int read(byte[] b) throws IOException {
        netMonitor.addinb(b.length);
//        System.err.println("read: " + new String(b));
        return myis.read(b);
    }

    public int read() throws IOException {
        return myis.read();
    }

    public boolean markSupported() {
        return myis.markSupported();
    }

    public synchronized void mark(int readlimit) {
        myis.mark(readlimit);
    }

    public void close() throws IOException {
        myis.close();
    }

    public int available() throws IOException {
        return myis.available();
    }

}
