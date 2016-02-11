/*
 * GenericWorker.java
 *
 * Created on 25 settembre 2007, 12.08
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.tnx.invoicex;

import org.jdesktop.swingworker.SwingWorker;

/**
 *
 * @author mceccarelli
 */
public abstract class GenericWorker extends SwingWorker<Object,String> {
    
    /** Creates a new instance of GenericWorker */
    public GenericWorker() {
    }
    
    public void publicPublish(String p) {
        publish(p);
    }

}
