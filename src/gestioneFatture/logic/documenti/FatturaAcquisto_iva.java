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

import it.tnx.Db;
import gestioneFatture.*;

import java.sql.*;

/**

 *

 * @author  marco

 */
public class FatturaAcquisto_iva {

    private int codiceIva;
    private float percentualeIva;
    private double imponibile;
    private double iva;
    private double importo;

    /** Creates a new instance of FatturaAcquisto */
    public FatturaAcquisto_iva(int codiceIva, double imponibile) {
        this.codiceIva = codiceIva;
        this.imponibile = imponibile;

        ResultSet resu = Db.openResultSet("select percentuale from codici_iva where codice = " + codiceIva);

        try {

            if (resu != null && resu.next()) {
                percentualeIva = resu.getFloat(1);
            }
        } catch (SQLException sqlErr) {
            sqlErr.printStackTrace();
        }

        iva = imponibile / 100 * percentualeIva;
        iva = it.tnx.Util.round(iva, 2);
        importo = imponibile + iva;
        importo = it.tnx.Util.round(importo, 2);
    }

    public float getPercentualeIva() {

        return percentualeIva;
    }

    public double getImporto() {

        return importo;
    }

    public double getImponibile() {

        return imponibile;
    }

    public double getIva() {

        return iva;
    }

    public int getCodiceIva() {

        return codiceIva;
    }
}