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
 * Plugin.java
 *
 * Created on 23 agosto 2007, 17.21
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.tnx.invoicex;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author mceccarelli
 */
public class Plugin implements Serializable {
    private String nome_breve;  // <nome_breve>_<versione>.jar è il file da scaricare
    private String versione;
    private String versioneDisp;
    private String versioneInst;
    private String nome_lungo;
    private String descrizione;
    private Date data_creazione;
    private Date data_ultima_modifica;
    private Date data_attivazione;
    private boolean presente;
    private boolean attivo;
    
    /** Creates a new instance of Plugin */
    public Plugin() {
    }

    public String getNome_breve() {
        return nome_breve;
    }

    public void setNome_breve(String nome_breve) {
        this.nome_breve = nome_breve;
    }

    public String getVersione() {
        return versione;
    }

    public void setVersione(String versione) {
        this.versione = versione;
    }

    public String getNome_lungo() {
        return nome_lungo;
    }

    public void setNome_lungo(String nome_lungo) {
        this.nome_lungo = nome_lungo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Date getData_creazione() {
        return data_creazione;
    }

    public void setData_creazione(Date data_creazione) {
        this.data_creazione = data_creazione;
    }

    public Date getData_ultima_modifica() {
        return data_ultima_modifica;
    }

    public void setData_ultima_modifica(Date data_ultima_modifica) {
        this.data_ultima_modifica = data_ultima_modifica;
    }

    public Date getData_attivazione() {
        return data_attivazione;
    }

    public void setData_attivazione(Date data_attivazione) {
        this.data_attivazione = data_attivazione;
    }

    public boolean isPresente() {
        return presente;
    }

    public void setPresente(boolean presente) {
        this.presente = presente;
    }

    public boolean isAttivo() {
        return attivo;
    }

    public void setAttivo(boolean attivo) {
        this.attivo = attivo;
    }

    public String getVersioneDisp() {
        return versioneDisp;
    }

    public void setVersioneDisp(String versioneDisp) {
        this.versioneDisp = versioneDisp;
    }

    public String getVersioneInst() {
        return versioneInst;
    }

    public void setVersioneInst(String versioneInst) {
        this.versioneInst = versioneInst;
    }
    
}
