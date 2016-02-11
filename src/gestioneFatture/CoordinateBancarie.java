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

import it.tnx.Db;
import java.sql.*;

import javax.swing.*;

public class CoordinateBancarie {

    String abi;
    String cab;
    String cc;
      /*  -- DAVID -- */
    String iban;
    String swift;
      /*  -- DAVID -- */
    String descrizioneAbi;
    String descrizioneCab;
    JTextField texBancAbi;
    JTextField texBancCab;
    JTextField texIban;
    JTextField texSwift;
    JTextField texContoC;
    JLabel labBancAbi;
    JLabel labBancCab;

    public void setAbi(String abi) {

        //gestione dei dati delle coordinate bancarie
        this.abi = abi;

        if (texBancAbi != null)
            this.texBancAbi.setText(this.abi);
    }

    public String getAbi() {

        //ritorna il codice abi
        return this.abi;
    }

    public void setCab(String cab) {
        this.cab = cab;

        if (texBancCab != null)
            this.texBancCab.setText(this.cab);
    }

    public String getCab() {

        return cab;
    }

    public void setIban(String iban) {
        this.iban = iban;
        
        if (texIban != null)
            this.texIban.setText(this.iban);
    }

    public String getIban() {

        return iban;
    }

    public void setSwift(String swift) {
        this.swift = swift;

        if (texSwift != null)
            this.texSwift.setText(this.swift);
    }

    public String getSwift() {

        return swift;
    }

    public void setCc(String cc) {
        this.cc = cc;

        if (texContoC != null)
            this.texContoC.setText(this.cc);
    }

    public String getCc() {

        return cc;
    }

    public void setDescrizioneAbi(String descrizioneAbi) {
        this.descrizioneAbi = descrizioneAbi;
        this.labBancAbi.setText(descrizioneAbi);
    }

    public void setDescrizioneCab(String descrizioneCab) {
        this.descrizioneCab = descrizioneCab;
        this.labBancCab.setText(descrizioneCab);
    }

    public void setField_texBancAbi(JTextField texBancAbi) {
        this.texBancAbi = texBancAbi;
    }

    public void setField_texIban(JTextField texIban) {
        this.texIban = texIban;
    }

    public void setField_labBancAbi(JLabel labBancAbi) {
        this.labBancAbi = labBancAbi;
    }

    public void setField_texBancCab(JTextField texBancCab) {
        this.texBancCab = texBancCab;
    }

    public void setField_texSwift(JTextField texSwift) {
        this.texSwift = texSwift;
    }

    public void setField_texContoC(JTextField c) {
        this.texContoC = c;
    }

    public void setField_labBancCab(JLabel labBancCab) {
        this.labBancCab = labBancCab;
    }

    public void findDescriptionLab() {

        //controlla che ci siano abi e cab se no li prende dalle text
        if (this.abi == null)
            this.abi = texBancAbi.getText();

        if (this.cab == null)
            this.cab = texBancCab.getText();

        //riempie le labels
        try {

            String sql = "select ";
            sql += " banche_abi.abi,";
            sql += " banche_abi.nome,";
            sql += " banche_cab.cab,";
            sql += " banche_cab.cap,";
            sql += " banche_cab.indirizzo,";
            sql += " comuni.comune,";
            sql += " comuni.provincia,";
            sql += " comuni.regione";
            sql += " from (banche_abi left join banche_cab on banche_abi.abi = banche_cab.abi)";
            sql += " left join comuni on banche_cab.codice_comune = comuni.codice";
            sql += " where banche_abi.abi = " + Db.pc(this.abi, "VARCHAR");
            sql += " and banche_cab.cab = " + Db.pc(this.cab, "VARCHAR");

            ResultSet resu = Db.openResultSet(sql);

            if (resu.next() == true) {
                this.labBancAbi.setText(Db.nz(resu.getString("banche_abi.nome"), ""));
                this.labBancCab.setText(Db.nz(resu.getString("comuni.comune"), "") + ", " + Db.nz(resu.getString("banche_cab.indirizzo"), ""));
            } else {
                this.labBancAbi.setText("");
                this.labBancCab.setText("");
            }
        } catch (Exception err) {
            this.labBancAbi.setText("");
            this.labBancCab.setText("");
            err.printStackTrace();
        }
    }

    public String findDescription() {

        try {

            String sql = "select ";
            sql += " banche_abi.abi,";
            sql += " banche_abi.nome,";
            sql += " banche_cab.cab,";
            sql += " banche_cab.cap,";
            sql += " banche_cab.indirizzo,";
            sql += " comuni.comune,";
            sql += " comuni.provincia,";
            sql += " comuni.regione";
            sql += " from (banche_abi left join banche_cab on banche_abi.abi = banche_cab.abi)";
            sql += " left join comuni on banche_cab.codice_comune = comuni.codice";
            sql += " where banche_abi.abi = " + Db.pc(this.abi, "VARCHAR");
            sql += " and banche_cab.cab = " + Db.pc(this.cab, "VARCHAR");

            ResultSet resu = Db.openResultSet(sql);

            if (resu.next() == true) {

                String temp = "";
                temp += Db.nz(resu.getString("banche_abi.nome"), "") + "\n";
                temp += "Ag. " + Db.nz(resu.getString("comuni.comune"), "");
                temp += " (" + Db.nz(resu.getString("comuni.provincia"), "") + ")";
                temp += "\n" + Db.nz(resu.getString("banche_cab.cap"), "");
                temp += ", " + Db.nz(resu.getString("banche_cab.indirizzo"), "");
                temp += "\nAbi " + Db.nz(resu.getString("banche_abi.abi"), "");
                temp += "\nCab " + Db.nz(resu.getString("banche_cab.cab"), "");

                return temp;
            } else {

                return "banca non trovata";
            }
        } catch (Exception err) {
            err.printStackTrace();

            return "";
        }
    }

    public String findSmallDescription() {

        try {

            String sql = "select ";
            sql += " banche_abi.abi,";
            sql += " banche_abi.nome,";
            sql += " banche_cab.cab,";
            sql += " banche_cab.cap,";
            sql += " banche_cab.indirizzo,";
            sql += " comuni.comune,";
            sql += " comuni.provincia,";
            sql += " comuni.regione";
            sql += " from (banche_abi left join banche_cab on banche_abi.abi = banche_cab.abi)";
            sql += " left join comuni on banche_cab.codice_comune = comuni.codice";
            sql += " where banche_abi.abi = " + Db.pc(this.abi, "VARCHAR");
            sql += " and banche_cab.cab = " + Db.pc(this.cab, "VARCHAR");

            ResultSet resu = Db.openResultSet(sql);

            if (resu.next() == true) {

                String temp = "";
                temp += Db.nz(resu.getString("banche_abi.nome"), "") + " ";
                temp += "Ag. " + Db.nz(resu.getString("comuni.comune"), "");
                temp += ", " + Db.nz(resu.getString("banche_cab.indirizzo"), "");

                return temp;
            } else {

                return "banca non trovata";
            }
        } catch (Exception err) {
            err.printStackTrace();

            return "";
        }
    }
}
