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

 * FatturaAcquisto.java

 *

 * Created on December 18, 2004, 11:33 PM

 */
package gestioneFatture.logic.documenti;

import java.sql.*;

import java.text.*;

import java.util.*;

import javax.swing.*;

/**

 *

 * @author  marco

 */
public class FatturaAcquisto {

    public int id;
    public String serie;
    public int numero;
    public int anno;
    public java.util.Date data;
    public double totale_imponibile;
    public double totale_iva;
    public double totale;
    private Vector dettaglioIva = new Vector();

    /** Creates a new instance of FatturaAcquisto */
    public FatturaAcquisto() {
    }

    public void addDettaglioIva(FatturaAcquisto_iva dettaglio) {
        dettaglioIva.add(dettaglio);
    }

    public Vector getDettaglioIva() {

        return dettaglioIva;
    }

    public void calcolaTotale() {
        totale_imponibile = 0;
        totale_iva = 0;
        totale = 0;

        for (int i = 0; i < dettaglioIva.size(); i++) {

            FatturaAcquisto_iva dettaglio = (FatturaAcquisto_iva)dettaglioIva.get(i);
            totale_imponibile += dettaglio.getImponibile();
            totale_iva += dettaglio.getIva();
            totale += dettaglio.getImporto();
        }
    }

    public boolean dbRicalcolaProgressivo(String stato, String data, JTextField texNumePrev, JTextField texAnno) {

        if (stato == gestioneFatture.frmFatturaRicevuta.DB_INSERIMENTO) {

            //ricreo campo data
            DateFormat myFormat = new SimpleDateFormat("dd/MM/yy");
            java.util.GregorianCalendar myDate = new java.util.GregorianCalendar();
            myFormat.setLenient(false);

            try {
                myDate.setTime(myFormat.parse(data));

                //calcola il progressivo in base alla data e anno
                String sql = "select numero from ";
                sql += "test_fatt_acquisto";
                sql += " where year(data) = " + myDate.get(Calendar.YEAR);
                sql += " order by numero desc limit 1";

                ResultSet resu = it.tnx.Db.openResultSet(sql);

                if (resu.next() == true) {

                    if (texNumePrev.getText().length() == 0 || !texNumePrev.getText()
                               .equalsIgnoreCase(String.valueOf(resu.getInt(1) + 1))) {
                        texNumePrev.setText(String.valueOf(resu.getInt(1) + 1));
                    }
                } else {
                    texNumePrev.setText("1");
                }

                texAnno.setText(String.valueOf(myDate.get(Calendar.YEAR)));

                return (true);
            } catch (Exception err) {
                err.printStackTrace();

                return (false);
            }
        }

        return (true);
    }
}