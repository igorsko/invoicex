/*
 * Backup.java
 *
 * Created on 20 settembre 2007, 12.27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.tnx.invoicex;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 *
 * @author mceccarelli
 */
public class Backup implements Serializable {
    private String nome_file;
    private long data_creazione;
    private long size;
    private byte[] data;
    
    /** Creates a new instance of Backup */
    public Backup() {
    }

    public String getNome_file() {
        return nome_file;
    }

    public void setNome_file(String nome_file) {
        this.nome_file = nome_file;
    }

    public long getData_creazione() {
        return data_creazione;
    }

    public void setData_creazione(long data_creazione) {
        this.data_creazione = data_creazione;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String toString() {
        SimpleDateFormat f1 = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        DecimalFormat f2 = new DecimalFormat("0.#");
        return "Backup in data " + f1.format(getData_creazione()) + ", dimensione " + f2.format((double)getSize() / 1024d / 1024d) + " MB";
    }
}
