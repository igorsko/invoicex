/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gestioneFatture;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

/**
 *
 * @author mceccarelli
 */
public class SortedProperties extends Properties {

    @Override
    public synchronized Enumeration keys() {

        Enumeration keysEnum = super.keys();
        Vector keyList = new Vector();

        while (keysEnum.hasMoreElements()) {
            keyList.add(keysEnum.nextElement());
        }

        Collections.sort(keyList);

        return keyList.elements();
    }

}
