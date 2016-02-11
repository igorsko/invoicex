/*
 * Zip.java
 *
 * Created on 23 febbraio 2007, 15.38
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.tnx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author mceccarelli
 */
public class Zip {
    
    public Zip() {
    }
    
    public static void createZip(String filename, String outFilename) throws IOException {
        byte[] buf = new byte[1024];
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFilename));
        FileInputStream in = new FileInputStream(filename);
        File file = new File(filename);
        out.putNextEntry(new ZipEntry(file.getName()));
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.closeEntry();
        in.close();
        out.close();
    }
    public static void createZip(String[] filenames, String outFilename) throws IOException {
        //String[] filenames = new String[]{"filename1", "filename2"};
        byte[] buf = new byte[1024];
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFilename));
        for (int i=0; i<filenames.length; i++) {
            FileInputStream in = new FileInputStream(filenames[i]);
            out.putNextEntry(new ZipEntry(filenames[i]));
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.closeEntry();
            in.close();
        }
        out.close();
    }
    
}
