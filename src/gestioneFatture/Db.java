/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gestioneFatture;

/**
 *
 * @author mceccarelli
 */
public class Db extends it.tnx.Db {
    static public Db INSTANCE = null;

    public Db() {
        INSTANCE = this;
        if (debug) {
            System.out.println("new gestioneFatture.Db instance:" + this);
        }
    }

}
