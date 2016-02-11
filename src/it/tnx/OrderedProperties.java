/*
 * OrderedProperties.java
 *
 * Created on 17 novembre 2006, 11.00
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.tnx;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

/**
 *
 * @author mceccarelli
 */
public class OrderedProperties extends Properties {
    private static final String keyValueSeparators = "=: \t\r\n\f";

    private static final String strictKeyValueSeparators = "=:";

    private static final String specialSaveChars = "=: \t\r\n\f#!";

    private static final String whiteSpaceChars = " \t\r\n\f";
    
    /**
     * Convert a nibble to a hex character
     * @param	nibble	the nibble to convert.
     */
    private static char toHex(int nibble) {
	return hexDigit[(nibble & 0xF)];
    }

    /** A table of hex digits */
    private static final char[] hexDigit = {
	'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'
    };
    
    private String saveConvert(String theString, boolean escapeSpace) {
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len*2);
        
        for(int x=0; x<len; x++) {
            char aChar = theString.charAt(x);
            switch(aChar) {
                case ' ':
                    if (x == 0 || escapeSpace)
                        outBuffer.append('\\');
                    
                    outBuffer.append(' ');
                    break;
                case '\\':outBuffer.append('\\'); outBuffer.append('\\');
                break;
                case '\t':outBuffer.append('\\'); outBuffer.append('t');
                break;
                case '\n':outBuffer.append('\\'); outBuffer.append('n');
                break;
                case '\r':outBuffer.append('\\'); outBuffer.append('r');
                break;
                case '\f':outBuffer.append('\\'); outBuffer.append('f');
                break;
                default:
                    if ((aChar < 0x0020) || (aChar > 0x007e)) {
                        outBuffer.append('\\');
                        outBuffer.append('u');
                        outBuffer.append(toHex((aChar >> 12) & 0xF));
                        outBuffer.append(toHex((aChar >>  8) & 0xF));
                        outBuffer.append(toHex((aChar >>  4) & 0xF));
                        outBuffer.append(toHex( aChar        & 0xF));
                    } else {
                        if (specialSaveChars.indexOf(aChar) != -1)
                            outBuffer.append('\\');
                        outBuffer.append(aChar);
                    }
            }
        }
        return outBuffer.toString();
    }
    private void writeln(BufferedWriter bw, String s) throws IOException {
        bw.write(s);
        bw.newLine();
    }
    public void store(OutputStream out, String header)
    throws IOException {
        BufferedWriter awriter;
        awriter = new BufferedWriter(new OutputStreamWriter(out, "8859_1"));
        if (header != null)
            writeln(awriter, "#" + header);
        writeln(awriter, "#" + new Date().toString());
        Object[] keys = keySet().toArray();
        Arrays.sort(keys);
        synchronized (this)  {
            //for (Enumeration e = keys(); e.hasMoreElements();) {
            for (int i = 0; i < keys.length; i++) {
                String key = (String)keys[i];
                String val = (String)get(key);
                key = saveConvert(key, true);
                
                            /* No need to escape embedded and trailing spaces for value, hence
                             * pass false to flag.
                             */
                val = saveConvert(val, false);
                writeln(awriter, key + "=" + val);
            }
        }
        awriter.flush();
    }
    
}
