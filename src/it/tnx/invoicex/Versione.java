/*
 * Versione.java
 *
 * Created on 24 settembre 2007, 15.47
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.tnx.invoicex;

import java.io.Serializable;

/**
 *
 * @author mceccarelli
 */
public class Versione implements Serializable {
    public int major;
    public int minor;
    public int revision;
    
    /** Creates a new instance of Versione */
    public Versione(int major, int minor, int revision) {
        this.major = major;
        this.minor = minor;
        this.revision = revision;
    }
    
    public boolean older(Versione to) {
        if (major < to.major) return true;
        if (minor < to.minor) return true;
        if (revision < to.revision) return true;
        return false;
        //return false;
    }
    
    public String toString() {
        return major + "." + minor + "." + revision;
    }
    
    public String toStringUnderscore() {
        return major + "_" + minor + "_" + revision;
    }
}
