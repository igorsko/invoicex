/*
 * Dbg.java
 *
 * Created on 5 febbraio 2007, 18.09
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.tnx;

import java.lang.reflect.Field;

/**
 *
 * @author mceccarelli
 */
public class Dbg {
    /** Creates a new instance of Dbg */
    public Dbg() {
    }
    
    static public void dump(Object o) {
        Field[] fields = o.getClass().getDeclaredFields();
        System.out.println(o);
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            try {
                System.out.println(f.getName() + ":" + f.get(o));
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }
    static public void dump(Object[] ao) {
        for (int i = 0; i < ao.length; i++) {
            Object o = ao[i];
            System.out.println(o + "[" + i + "]");
            dump(o);
        }
    }
}
