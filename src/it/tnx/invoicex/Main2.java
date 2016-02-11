/**
 * Invoicex
 * Copyright (c) 2005,2006,2007 Marco Ceccarelli, Tnx snc
 *
 * Questo software è soggetto, e deve essere distribuito con la licenza  
 * GNU General Public License, Version 2. La licenza accompagna il software
 * o potete trovarne una copia alla Free Software Foundation http://www.fsf.org .
 *
 * This software is subject to, and may be distributed under, the
 * GNU General Public License, Version 2. The license should have
 * accompanied the software or you may obtain a copy of the license
 * from the Free Software Foundation at http://www.fsf.org .
 * 
 * --
 * Marco Ceccarelli (m.ceccarelli@tnx.it)
 * Tnx snc (http://www.tnx.it)
 *
 */



/*
 * Main.java
 *
 * Created on 1 luglio 2005, 7.05
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package it.tnx.invoicex;

import gestioneFatture.main2;

/**
 *
 * @author marco
 */
public class Main2 {
    
    /** Creates a new instance of Main */
    public Main2(String[] args) {
        //lancio programma vero
        try {
            main2 tempMain = new main2(args);
        } catch (Exception err) {
            System.out.println("err:" + err);
            err.printStackTrace();
        }
        
    }
    
    public static void main(String[] args) {
        new Main2(args);
    }
    
}
