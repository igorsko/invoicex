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



package gestioneFatture.magazzino.print;

import it.tnx.Db;
import com.lowagie.text.*;
import com.lowagie.text.html.*;
import com.lowagie.text.pdf.*;

import gestioneFatture.*;

import java.awt.Color;
import java.awt.Point;

import java.sql.*;
import java.sql.ResultSet;

public class PrintMovimenti {
    //stampa ddt
    private Connection connection;
    private Statement stat;
    private ResultSet resu = null;
    private ResultSetMetaData meta;
    private String nomeFilePdf = "tempPrnMovimenti.pdf";
    private String nomeFileHtml = "tempPrnMovimenti.html";
    public String serie;
    public int numero;
    public int anno;
    private int numeroPagine;
    private int riga;
    private BaseFont bf;
    private BaseFont bfCour;
    private BaseFont bf_italic;
    private BaseFont bf_bold;
    private BaseFont bf_times;
    private PdfContentByte cb;
    private Document document;
    private PdfWriter writer;
    private Table datatable = null;
    int y;
    int iy = 1;
    Db dbUtil = Db.INSTANCE;

    public PrintMovimenti(java.sql.ResultSet resu) {
        System.out.println("print:movimentiMagazzino");
        this.resu = resu;

        try {
            this.meta = resu.getMetaData();
        } catch (Exception err) {
            err.printStackTrace();
        }

        int[] hw = { 10, 10, 30, 10, 30, 10 };
        String ret = stampaTabella("Stampa movimenti di magazzino", hw);
        Util.start(ret);
    }

    //stampa generica
    public String stampaTabella(String titolo, int[] headerWidth) {

        String nomeFilePdf = "tempStampaMovimentiMagazzino.pdf";
        String nomeFileHtml = "tempStampaMovimentiMagazzino.html";
        com.lowagie.text.Font bf;
        BaseFont bfCour;
        BaseFont bf_italic;
        BaseFont bf_bold;
        BaseFont bf_times;
        PdfContentByte cb;
        Document document;
        PdfWriter writer;

        try {

            //creazione del pdf
            document = new Document(PageSize.A4);
            writer = PdfWriter.getInstance(document, new java.io.FileOutputStream(nomeFilePdf));

            //HtmlWriter.getInstance(document, new java.io.FileOutputStream(nomeFileHtml));
            document.addTitle("stampa tabella");
            document.addSubject("stampa tabella");
            document.addKeywords("stampa tabella");
            document.addAuthor("TNX s.a.s");
            document.addHeader("Expires", "0");
            document.open();

            //------------------------------------
            bf = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 8, com.lowagie.text.Font.NORMAL);
            bfCour = BaseFont.createFont("Courier", "winansi", false);
            bf_italic = BaseFont.createFont("Helvetica-Oblique", "winansi", false);
            bf_bold = BaseFont.createFont("Helvetica-Bold", "winansi", false);
            bf_times = BaseFont.createFont("Times-Roman", "winansi", false);

            //BaseFont bf = BaseFont.createFont("Helvetia", "winansi", false);
            cb = writer.getDirectContent();

            //stampa
            //cliente
            double y;
            double iy;
            double x;
            double ix;
            int riga = 0;
            iy = 1.25;
            y = 4;

            //tabella dati testata
            Table datatable;
            datatable = new Table(meta.getColumnCount());
            datatable.setBorder(0);
//            datatable.setCellpadding(0);
            datatable.setPadding(2);

            //int headerwidths2[] = {20, 80}; // percentage
            //datatable.setWidths(headerwidths2);
            datatable.setWidths(headerWidth);
            datatable.setWidth(100); // percentage
            datatable.setCellsFitPage(true);

            Phrase tempFrase;
            Cell tempPdfCell;

            //intestazione
            Phrase intestazione = new Phrase();
            intestazione.add(new Chunk(titolo, new com.lowagie.text.Font(com.lowagie.text.Font.TIMES_ROMAN, 10, com.lowagie.text.Font.BOLD)));
            document.add(intestazione);

            //tempPdfCell = new Cell(new Phrase(this. , new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 8, com.lowagie.text.Font.NORMAL)));
            //colonne
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                tempPdfCell = new Cell(new Phrase(this.meta.getColumnName(i), bf));
                set1(tempPdfCell);
                datatable.addCell(tempPdfCell);
            }

            //righe
            while (this.resu.next()) {

                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    tempPdfCell = new Cell(new Phrase(Db.nz(String.valueOf(resu.getString(i)), ""), bf));
                    set2(tempPdfCell);
                    datatable.addCell(tempPdfCell);
                }
            }

            document.add(datatable);

            //chiudo
            document.close();

            //Runtime.getRuntime().exec("start " + nomeFilePdf);
            //Util.start(nomeFilePdf);
            return (nomeFilePdf);
        } catch (Exception err) {
            javax.swing.JOptionPane.showMessageDialog(null, err.toString());
            err.printStackTrace();

            return (null);
        }
    }

    void set1(Cell tempPdfCell) {
        tempPdfCell.setBackgroundColor(new Color(255, 255, 240));
        tempPdfCell.setBorderColor(new Color(200, 200, 200));
        tempPdfCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
    }

    void set2(Cell tempPdfCell) {
        tempPdfCell.setBackgroundColor(new Color(255, 255, 255));
        tempPdfCell.setBorderColor(new Color(200, 200, 200));
        tempPdfCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
    }
}