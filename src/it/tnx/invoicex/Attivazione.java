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
 * Attivazione.java
 *
 * Created on 21 agosto 2007, 9.36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.tnx.invoicex;

import it.tnx.Db;
import gestioneFatture.main;
import it.tnx.invoicex.data.DatiAzienda;
import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.ResultSet;
import javax.swing.JOptionPane;

import static it.tnx.Util.*;

/**
 *
 * @author mceccarelli
 */
public class Attivazione {
    private boolean datiAziendaInseriti;
    
    /** Creates a new instance of Attivazione */
    public Attivazione() {
    }
    
    public boolean isFlagDatiInviatiPrimaVolta() {
        String value = main.fileIni.getValue("attivazione", "flagDatiInviatiPrimaVolta");
        return Boolean.parseBoolean(value);
    }
    public void setFlagDatiInviatiPrimaVolta(boolean value) {
        main.fileIni.setValue("attivazione", "flagDatiInviatiPrimaVolta", String.valueOf(value));
    }
    
    public boolean isFlagDatiModificati() {
        String value = main.fileIni.getValue("attivazione", "flagDatiModificati");
        return Boolean.parseBoolean(value);
    }
    public void setFlagDatiModificati(boolean value) {
        main.fileIni.setValue("attivazione", "flagDatiModificati", String.valueOf(value));
    }

    public Integer getIdRegistrazione() {
        try {
            return Integer.parseInt(main.fileIni.getValue("attivazione", "idRegistrazione"));
        } catch (NumberFormatException  err1) {
            return null;
        }
    }
    
    public void setIdRegistrazione(Integer value) {
        main.fileIni.setValue("attivazione", "idRegistrazione", String.valueOf(value));
    }
    
    
    public boolean isFlagDatiInviatiSuModifica() {
        return Boolean.parseBoolean(main.fileIni.getValue("attivazione", "flagDatiInviatiSuModifica"));
    }
    public boolean setFlagDatiInviatiSuModifica(boolean value) {
        return main.fileIni.setValue("attivazione", "flagDatiInviatiPrimaVolta", String.valueOf(value));
    }
    
    public boolean isDatiAziendaInseriti() {
        return datiAziendaInseriti;
    }
    
    public void setDatiAziendaInseriti(boolean datiAziendaInseriti) {
        this.datiAziendaInseriti = datiAziendaInseriti;
    }
    
    public boolean registra() {
        //invia i dati a tnx (con eventuale id precedente), riceve l'id e lo registra
        Integer vecchioId;
        
        DatiAzienda dati = leggiDatiAzienda();
        if (invioReg(dati)) {
            System.out.println("dati inviato ok");
            return true;
        }
        
        return false;
    }
    
    public DatiAzienda getDatiAzienda() {
        return leggiDatiAzienda();
    }
    
    private DatiAzienda leggiDatiAzienda() {
        ResultSet r = Db.openResultSet("select ragione_sociale,piva,cfiscale,indirizzo,cap,localita,provincia,telefono,fax,sito_web,email from dati_azienda");
        try {
            r.next();
            DatiAzienda dati = new DatiAzienda();
            dati.setRagione_sociale(r.getString("ragione_sociale"));
            dati.setPartita_iva(r.getString("piva"));
            dati.setCodice_fiscale(r.getString("cfiscale"));
            dati.setIndirizzo(r.getString("indirizzo"));
            dati.setCap(r.getString("cap"));
            dati.setLocalita(r.getString("localita"));
            dati.setProvincia(r.getString("provincia"));
            dati.setTelefono(r.getString("telefono"));
            dati.setFax(r.getString("fax"));
            dati.setSito_web(r.getString("sito_web"));
            dati.setEmail(r.getString("email"));
            dati.setId_registrazione(getIdRegistrazione());
            return dati;
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
    }
    
    private boolean invioReg(DatiAzienda dati) {
        URL url;
        URLConnection conn;
        int size;
        
        try {
            String surl = "http://www.tnx.it/pagine/invoicex_server/connect2.php?";
            surl += "reg=1";
            surl += "&ver=" + URLEncoder.encode(main.version.toString()) + "";
            surl += "&ver2=" + URLEncoder.encode(main.version.toString() + " " + main.build.toString()) + "";
            surl += "&email=" + URLEncoder.encode(dati.getEmail()) + "";
            surl += "&id_registrazione=" + URLEncoder.encode(nz( main.attivazione.getIdRegistrazione() )) + "";
            surl += "&ragione_sociale=" + URLEncoder.encode(dati.getRagione_sociale(), "UTF-8") + "";
            surl += "&partita_iva=" + URLEncoder.encode(dati.getPartita_iva()) + "";
            surl += "&codice_fiscale=" + URLEncoder.encode(dati.getCodice_fiscale()) + "";
            surl += "&indirizzo=" + URLEncoder.encode(dati.getIndirizzo()) + "";
            surl += "&cap=" + URLEncoder.encode(dati.getCap()) + "";
            surl += "&localita=" + URLEncoder.encode(dati.getLocalita()) + "";
            surl += "&provincia=" + URLEncoder.encode(dati.getProvincia()) + "";
            surl += "&telefono=" + URLEncoder.encode(dati.getTelefono()) + "";
            surl += "&fax=" + URLEncoder.encode(dati.getFax()) + "";
            surl += "&sito_web=" + URLEncoder.encode(dati.getSito_web());
            surl += "&mac=" + URLEncoder.encode(MacAddressUtils.getMacAddress());
            surl += "&os=" + URLEncoder.encode(System.getProperty("os.name"));
            url = new URL(surl);
            System.err.println("url connect:" + url);
            conn = url.openConnection();

            size = conn.getContentLength();
            
            if (size < 0)
                size = 1024;
            
            BufferedInputStream bufin = new BufferedInputStream(conn.getInputStream());
            byte[] bytea = new byte[size];
            int readed = bufin.read(bytea);
            String content = new String(bytea, 0, readed);
            
            System.out.println("attivazione: response: " + content);
            
            if (content == null || content.trim().length() == 0) {
                JOptionPane.showMessageDialog(gestioneFatture.main.getPadre(), "Errore nella richiesta di registrazione");
                
                return false;
            }
            
            String[] splitted = content.split("\\n");
            
            for (int i = 0; i < splitted.length; i++) {
                
                String s = splitted[i];
                String[] splitted2 = s.split(":");
                
                if (splitted2[0].equalsIgnoreCase("error")) {
                    JOptionPane.showMessageDialog(gestioneFatture.main.getPadre(), "Errore durante la registrazione: " + splitted2[1], "Errore", JOptionPane.ERROR_MESSAGE);
                    
                    return false;
                }
                
                if (splitted2[0].equalsIgnoreCase("id")) {
                    //fileIni.setValue("info", "inst_id", splitted2[1]);
                    main.attivazione.setIdRegistrazione( Integer.parseInt(splitted2[1]) );
                    conn.getInputStream().close();
                    return true;
                }
            }
            
            conn.getInputStream().close();
            return false;
        } catch (Exception err) {
            err.printStackTrace();
            JOptionPane.showMessageDialog(gestioneFatture.main.getPadre(), "<html>Sembra che non sei collegato ad internet, la prima volta che usi il programma devi essere collegato.<br>Errore durante la registrazione: " + err.toString() + "</html>", "Errore", JOptionPane.ERROR_MESSAGE);
        }
        
        return false;
    }
    
}
