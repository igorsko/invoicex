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
package gestioneFatture.logic;

import it.tnx.Db;
import gestioneFatture.*;

public class Iva {

    private String codice;
    private double percentuale;
    private String descrizione;
    private String descrizioneBreve;

    public Iva() {
    }

    public boolean load(Db db, String codice) {
        if (codice.length() == 0) {
            return false;
        }
        java.sql.ResultSet r = db.openResultSet("select * from codici_iva where codice = '" + codice + "'");
        try {
            if (r.next()) {
                setCodice(r.getString("codice"));
                setPercentuale(r.getDouble("percentuale"));
                setDescrizione(r.getString("descrizione"));
                setDescrizioneBreve(r.getString("descrizione_breve"));
                return true;
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
        return false;
    }

    public String getCodice() {
        return this.codice;
    }

    public void setCodice(String codice) {
        this.codice = codice;
    }

    public double getPercentuale() {
        return this.percentuale;
    }

    public void setPercentuale(double percentuale) {
        this.percentuale = percentuale;
    }

    public String getDescrizione() {
        return this.descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getDescrizioneBreve() {
        return this.descrizioneBreve;
    }

    public void setDescrizioneBreve(String descrizioneBreve) {
        this.descrizioneBreve = descrizioneBreve;
    }
}