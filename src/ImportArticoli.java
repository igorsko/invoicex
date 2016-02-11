
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
import it.tnx.Db;
import gestioneFatture.*;
import java.lang.System;

public class ImportArticoli {

    public ImportArticoli() {
        try {
            Db.dbServ = "localhost";
            Db.dbNameDB = "invoicex_default";
            Db.dbName = "root";
            Db.dbPass = "";

            Db db = new Db();
            System.out.println("conn:" + db.dbConnect());
            Db.executeSql("delete from articoli");

            String sql;
            for (int i = 0; i < 10000; i++) {
                sql = "insert into articoli (codice, descrizione, prezzo1, prezzo2, iva) values ('" + i + "','" + 
                      i + "'," + i + "," + i + ",20)";
                Db.executeSql(sql);
            }
//                sql = "insert into stati values ('" + nome + "','" + 
//                      codice1 + "','" + codice2 + "'," + codice3 + ",0)";
//                Db.executeSql(sql);

        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ImportArticoli temp = new ImportArticoli();
    }
}
