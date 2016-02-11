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
 * CheckThread.java
 * 
 * Created on 23-giu-2007, 15.14.21
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gestioneFatture;

import it.tnx.Db;
import java.util.Vector;

/**
 *
 * @author marco
 */
public class CheckThread
    extends java.lang.Thread {

    javax.swing.JTextArea text;
    javax.swing.JButton chiudi;
    javax.swing.JProgressBar bar;
    javax.swing.JScrollPane scroll;

    public CheckThread(it.tnx.JFrameMessage frame) {
        this.text = frame.getTextArea();
        this.chiudi = frame.getPulsanteChiudi();
        this.bar = frame.getAvanzamento();
        this.scroll = frame.getTextAreaScrollPane();
        frame.setTitle("Controllo integrita' dati");
    }

    public void run() {

        boolean errors = false;
        chiudi.setEnabled(false);
        bar.setIndeterminate(true);
        text.setAutoscrolls(true);
        text.append("Inizio controllo database\n");

        //backup con funzione senza eseguire comandi esterni
        Vector tablesWithError = new Vector();

        try {

            Vector tables = new Vector();
            Vector tablesStatus = new Vector();
            java.sql.ResultSet r = Db.openResultSet("show full tables");

            while (r.next()) {
                String tab = r.getString(1);
                String tipo = r.getString(2);
                if (tipo != null && !tipo.equalsIgnoreCase("VIEW")) {
                    text.append("controllo " + tipo + " '" + tab + "'");                
                
                    java.sql.ResultSet rc = Db.openResultSet("check table " + r.getString(1));
                    rc.next();
                    System.out.println(rc.getString(4));
                    System.out.println(r.getString(1) + " : checked");
                    text.append(" ..." + rc.getString(4) + "\n");
                    text.setCaretPosition(text.getText().length());
                    tables.add(r.getString(1));
                    tablesStatus.add(rc.getString(4));

                    if (!rc.getString(4).equals("OK")) {
                        errors = true;
                        tablesWithError.add(r.getString(1));
                    }
                }
            }
        } catch (Exception err) {
            err.printStackTrace();
        }

        if (errors == true) {
            errors = false;

            Vector tablesRepair = new Vector();

            //eseguo anche il repair
            text.append("\n\nRiparazione tabelle danneggiate\n");

            try {

                for (int i = 0; i < tablesWithError.size(); i++) {
                    text.append("riparazione tabella '" + tablesWithError.get(i)
                                   .toString() + "'");

                    java.sql.ResultSet rp = Db.openResultSet("repair table " + tablesWithError
                                   .get(i).toString());
                    rp.next();
                    System.out.println(rp.getString(4));
                    System.out.println(tablesWithError.get(i).toString() + " : repair");
                    text.append(" ..." + rp.getString(4) + "\n");
                    text.setCaretPosition(text.getText().length());
                    tablesRepair.add(rp.getString(4));

                    if (!rp.getString(4).equals("OK")) {
                        errors = true;
                    }
                }
            } catch (Exception err) {
                err.printStackTrace();
            }

            if (errors == true) {
                javax.swing.JOptionPane.showMessageDialog(null, "Ci sono alcune tabelle che non possono essere riparate\nContattare l'assistenza tecnica", "Errore", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        } else {
            text.append("\nNessuna tabella e' danneggiata.\n");
        }

        bar.setIndeterminate(false);
        bar.setStringPainted(true);
        bar.setValue(100);
        text.append("Controllo completato.");
        text.setCaretPosition(text.getText().length());
        chiudi.setEnabled(true);
    }
}