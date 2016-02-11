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



package gestioneFatture;

import it.tnx.invoicex.InvoicexUtil;
import java.sql.*;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.text.JTextComponent;

import tnxbeans.*;

public class dbFatturaRicevuta extends dbFattura {
    
    public dbFatturaRicevuta() {
    }

    public boolean dbRicalcolaProgressivo(String stato, String data, JTextComponent texNumePrev, JTextComponent texAnno, String serie, Integer id) {

        if (stato == frmTestDocu.DB_INSERIMENTO) {

            //ricreo campo data
            DateFormat myFormat = new SimpleDateFormat("dd/MM/yy");
            java.util.GregorianCalendar myDate = new java.util.GregorianCalendar();
            myFormat.setLenient(false);

            try {
                myDate.setTime(myFormat.parse(data));

                //calcola il progressivo in base alla data e anno
                String sql = "select numero from test_fatt_acquisto";
                int myanno = myDate.get(Calendar.YEAR);
                if (InvoicexUtil.getTipoNumerazione() == InvoicexUtil.TIPO_NUMERAZIONE_ANNO_INFINITA && myanno >= 2013) {
                    sql += " where anno >= 2013";
                } else {
                    sql += " where anno = " + myanno;
                }
                sql += " and serie = " + Db.pc(serie, Types.VARCHAR);
                if (id != null) {
                    sql += " and id != " + Db.pc(id, Types.INTEGER);
                }                
                sql += " order by numero desc limit 1";

                ResultSet resu = Db.openResultSet(sql);

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

    public void dbRefresh() {
        //debug
        System.out.println("!!! richiamata la routine dbFattura.dbRefresh()");

        //calcolo del totale e castelletto iva secondo nuove classi
        gestioneFatture.logic.documenti.Documento doc = new gestioneFatture.logic.documenti.Documento();
        doc.load(Db.INSTANCE, numero, serie, anno, Db.TIPO_DOCUMENTO_FATTURA_RICEVUTA, id);
        doc.calcolaTotali();
        doc.visualizzaCastellettoIva();
        this.totale = doc.getTotale();
        this.totaleImponibile = doc.getTotaleImponibile();
        this.totaleIva = doc.getTotaleIva();
        this.speseVarie = doc.getSpeseVarieImponibili();
        this.speseTrasportoIva = doc.getSpeseTrasporto();
        this.speseIncassoIva = doc.getSpeseIncasso();
    }
}
