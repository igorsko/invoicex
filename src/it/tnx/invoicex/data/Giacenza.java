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
 * Giacenza.java
 *
 * Created on 24 maggio 2007, 14.27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.tnx.invoicex.data;

/**
 *
 * @author mceccarelli
 */
public class Giacenza {
    private String codice_articolo;
    private String descrizione_articolo;
    private String matricola;
    private String lotto;
    private double giacenza;
    private double prezzo;
    
    /** Creates a new instance of Giacenza */
    public Giacenza() {
    }

    public String getCodice_articolo() {
        return codice_articolo;
    }

    public void setCodice_articolo(String codice_articolo) {
        this.codice_articolo = codice_articolo;
    }

    public String getDescrizione_articolo() {
        return descrizione_articolo;
    }

    public void setDescrizione_articolo(String descrizione_articolo) {
        this.descrizione_articolo = descrizione_articolo;
    }

    public String getMatricola() {
        return matricola;
    }

    public void setMatricola(String matricola) {
        this.matricola = matricola;
    }

    public double getGiacenza() {
        return giacenza;
    }

    public void setGiacenza(double giacenza) {
        this.giacenza = giacenza;
    }

    public double getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }
    public String toString() {
        if (matricola != null && lotto != null) {
            return "S/N: " + matricola + " Lotto: " + lotto;
        } else if (matricola != null) {
            return matricola;
        } else if (lotto != null) {
            return lotto;
        } else {
            return getCodice_articolo() + " " + getGiacenza();
        }
    }

    public String getLotto() {
        return lotto;
    }

    public void setLotto(String lotto) {
        this.lotto = lotto;
    }
}
