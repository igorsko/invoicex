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
   
public class ImportaDbf {

    public ImportaDbf() {
    }

    public static void main(String[] args) {
        it.tnx.dbf.DbfTable table = new it.tnx.dbf.DbfTable("/tmp/clienti.dbf", "clienti");
        System.out.println(table.getSqlCreateTable());
        while (table.next()) {
            System.out.println(table.getSqlInsertRecord() + ";");
        }
    }
}
