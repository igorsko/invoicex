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
 * DatiAzienda.java
 *
 * Created on 22 agosto 2007, 14.51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.tnx.invoicex.data;

import java.io.Serializable;

/**
 *
 * @author mceccarelli
 */
public class DatiAzienda implements Serializable {
    private String ragione_sociale;
    private String partita_iva;
    private String codice_fiscale;
    private String indirizzo;
    private String cap;
    private String localita;
    private String provincia;
    private String telefono;
    private String fax;
    private String sito_web;
    private String email;
    private Integer id_registrazione;
    
    /** Creates a new instance of DatiAzienda */
    public DatiAzienda() {
    }

    public String getRagione_sociale() {
        return ragione_sociale;
    }

    public void setRagione_sociale(String ragione_sociale) {
        this.ragione_sociale = ragione_sociale;
    }

    public String getPartita_iva() {
        return partita_iva;
    }

    public void setPartita_iva(String partita_iva) {
        this.partita_iva = partita_iva;
    }

    public String getCodice_fiscale() {
        return codice_fiscale;
    }

    public void setCodice_fiscale(String codice_fiscale) {
        this.codice_fiscale = codice_fiscale;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public String getCap() {
        return cap;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }

    public String getLocalita() {
        return localita;
    }

    public void setLocalita(String localita) {
        this.localita = localita;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getSito_web() {
        return sito_web;
    }

    public void setSito_web(String sito_web) {
        this.sito_web = sito_web;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getId_registrazione() {
        return id_registrazione;
    }

    public void setId_registrazione(Integer id_registrazione) {
        this.id_registrazione = id_registrazione;
    }
    
}
