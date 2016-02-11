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
import java.io.*;
import java.lang.System;
import java.sql.*;

public class ImportStati {
    public ImportStati() {
        try {
            Db.dbServ = "";
            Db.dbNameDB = "";
            Db.dbName = "";
            Db.dbPass = "";

            Db db = new Db();
            System.out.println("conn:" + db.dbConnect());
            Db.executeSql("delete from stati");

            String nome;
            String codice1;
            String codice2;
            String codice3;

            //legge il file
            FileReader inFile = new FileReader("/home/marco/stati.txt");
            LineNumberReader inLines = new LineNumberReader(inFile);
            String inputLine;
            String sql;

            while ((inputLine = inLines.readLine()) != null) {
                System.out.println(inLines.getLineNumber() + ". " + 
                                   inputLine);

                //import file stati
                nome = inputLine.substring(0, 48).trim();
                codice1 = inputLine.substring(48, 54).trim();
                codice2 = inputLine.substring(56, 60).trim();
                codice3 = inputLine.substring(64, 67).trim();
                System.out.println(
                        "debug:" + nome + ":" + codice1 + ":" + codice2 + 
                        ":" + codice3);

                //
                sql = "insert into stati values ('" + nome + "','" + 
                      codice1 + "','" + codice2 + "'," + codice3 + ",0)";
                Db.executeSql(sql);
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ImportStati temp = new ImportStati();
    }
}
