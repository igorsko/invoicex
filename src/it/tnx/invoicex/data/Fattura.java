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
 * Fattura.java
 *
 * Created on 22 maggio 2007, 9.39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.tnx.invoicex.data;

/**
 *
 * @author mceccarelli
 */
public class Fattura {
    private String serie;
    private int numero;
    private int anno;
    private Cliente cliente;
    
    
    /** Creates a new instance of Fattura */
    public Fattura() {
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public int getAnno() {
        return anno;
    }

    public void setAnno(int anno) {
        this.anno = anno;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    
}
