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
 * Articolo.java
 *
 * Created on 2 febbraio 2007, 19.09
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.tnx.invoicex.data;

/**
 *
 * @author mceccarelli
 */
public class Articolo {
    private String codice;
    private String descrizione;
    private String descrizione_en;
    private double prezzo1;
    private double prezzo2;
    private String um;
    private String um_en;
    private byte iva;
    private int pezzi;
    
    /** Creates a new instance of Articolo */
    public Articolo() {
    }

    public String getCodice() {
        return codice;
    }

    public void setCodice(String codice) {
        this.codice = codice;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getDescrizione_en() {
        return descrizione_en;
    }

    public void setDescrizione_eng(String descrizione_en) {
        this.descrizione_en = descrizione_en;
    }

    public double getPrezzo1() {
        return prezzo1;
    }

    public void setPrezzo1(double prezzo1) {
        this.prezzo1 = prezzo1;
    }

    public double getPrezzo2() {
        return prezzo2;
    }

    public void setPrezzo2(double prezzo2) {
        this.prezzo2 = prezzo2;
    }

    public String getUm() {
        return um;
    }

    public void setUm(String um) {
        this.um = um;
    }

    public String getUm_eng() {
        return um_en;
    }

    public void setUm_eng(String um_en) {
        this.um_en = um_en;
    }

    public byte getIva() {
        return iva;
    }

    public void setIva(byte iva) {
        this.iva = iva;
    }

    public int getPezzi() {
        return pezzi;
    }

    public void setPezzi(int pezzi) {
        this.pezzi = pezzi;
    }
    
}
