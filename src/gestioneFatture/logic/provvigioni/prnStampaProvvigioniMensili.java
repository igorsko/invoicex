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



package gestioneFatture.logic.provvigioni;

import it.tnx.Db;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import gestioneFatture.*;

import it.tnx.commons.FormatUtils;
import it.tnx.invoicex.InvoicexUtil;
import java.awt.Color;

import java.sql.*;
import java.sql.ResultSet;

public class prnStampaProvvigioniMensili {

    //stampa ddt
    private Connection connection;
    private Statement stat;
    private ResultSet resu;
    private String nomeFilePdf = "";
    private String localita_azienda = "";
    public String serie;
    public int numero;
    public int anno;
    private boolean prova;
    private int numeroDistinta;
    private double totale = 0;
    private Table datatable; //la definisco qui perch\u00ef\u00bf\u00bd l\u00ef\u00bf\u00bda uso anche nel piede
    private int riga;
    private String data;
    private BaseFont bf;
    private BaseFont bfCour;
    private BaseFont bf_italic;
    private BaseFont bf_bold;
    private BaseFont bf_times;
    private PdfContentByte cb;
    private Document document;
    private PdfWriter writer;
    int y;
    int iy = 1;
    String agente = "";
    String periodo = "";

    public prnStampaProvvigioniMensili(String sql, String agente, String periodo) {
        this.agente = agente;
        this.periodo = periodo;
        nomeFilePdf = "spool" + it.tnx.Util.getDirSeparator() + "provvigioni_" + it.tnx.Util.getDateTimeFormatYYYYMMDD_HHMM() + ".pdf";

        try {
            stat = Db.getConn().createStatement();
            resu = stat.executeQuery(sql);
            report();
            reportView();
        } catch (Exception err) {
            javax.swing.JOptionPane.showMessageDialog(null, err.toString());
            err.printStackTrace();
        }
    }

    int quantePagine() {

        return (1);
    }

    int quanteRighe() {

        return (30);
    }

    void report() {
        reportInit();

        for (int i = 0; i < quantePagine(); i++) {
            pagina(i);
        }

        reportEnd();
    }

    void reportInit() {

        try {

            //creazione del pdf
            document = new Document(PageSize.A4);
            writer = PdfWriter.getInstance(document, new java.io.FileOutputStream(nomeFilePdf));
            document.addTitle("Stampa Provvigioni");
            document.addAuthor("TNX");
            document.addHeader("Expires", "0");
            document.open();
            bf = BaseFont.createFont("Helvetica", "winansi", false);
            bfCour = BaseFont.createFont("Courier", "winansi", false);
            bf_italic = BaseFont.createFont("Helvetica-Oblique", "winansi", false);
            bf_bold = BaseFont.createFont("Helvetica-Bold", "winansi", false);
            bf_times = BaseFont.createFont("Times-Roman", "winansi", false);
            cb = writer.getDirectContent();
        } catch (Exception err) {
            err.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, err.toString());
        }
    }

    void reportEnd() {
        document.close();
    }

    void reportView() {

        //lancio anteprima
        try {
            Util.start(nomeFilePdf);
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    void pagina(int pagina) {
        sfondo();
        intestazione();
        corpo();
        piede();
    }

    void sfondo() {
    }

    void intestazione() {

        try {

            Image logoAzienda = Image.getInstance(InvoicexUtil.caricaLogoDaDbBytes(Db.getConn(), "logo"));

            logoAzienda.scaleToFit(200, 100);
            logoAzienda.setAbsolutePosition(35, PageSize.A4.getHeight() - 20 - (logoAzienda.getScaledHeight()));
            document.add(logoAzienda);
            
            document.add(new Phrase("\n"));      
            
        } catch (Exception err) {
            err.printStackTrace();
        }

        try {

            double y;
            double iy;
            double x;
            double ix;
            riga = 0;
            iy = 1.25;
            y = 4;

            int numColumns = 1;
            Table datatable = new Table(numColumns);
            int[] headerwidths = { 100 }; // percentage
            datatable.setWidths(headerwidths);
            datatable.setBorderWidth(0);
            datatable.setWidth(100);
//            datatable.setCellpadding(0);
            datatable.setPadding(1);
//            datatable.setDefaultCellBorderColor(new Color(255, 255, 255));
//            datatable.setDefaultCellBorder(0);
            datatable.setAlignment(Table.ALIGN_RIGHT);
            datatable.setBorderColor(Color.RED);

            //datatable.setWidthPercentage(100); // percentage
            Cell cell1;

            //intestazione spett
            //prendo intestazione da tabella dati_azienda
            Statement statInt = Db.getConn().createStatement();
            String sqlInt = "select " + main.campiDatiAzienda + " from dati_azienda";
            ResultSet resuInt = statInt.executeQuery(sqlInt);
            resuInt.next();
            this.localita_azienda = resuInt.getString("localita");
            cell1 = new Cell(new Phrase(Db.nz(resuInt.getString("intestazione_riga1"), ""), new Font(Font.HELVETICA, 7, Font.NORMAL)));
            setLogo(cell1);
            datatable.addCell(cell1);
            cell1 = new Cell(new Phrase(Db.nz(resuInt.getString("intestazione_riga2"), ""), new Font(Font.HELVETICA, 7, Font.NORMAL)));
            setLogo(cell1);
            datatable.addCell(cell1);
            cell1 = new Cell(new Phrase(Db.nz(resuInt.getString("intestazione_riga3"), ""), new Font(Font.HELVETICA, 7, Font.NORMAL)));
            setLogo(cell1);
            datatable.addCell(cell1);
            cell1 = new Cell(new Phrase(Db.nz(resuInt.getString("intestazione_riga4"), ""), new Font(Font.HELVETICA, 7, Font.NORMAL)));
            setLogo(cell1);
            datatable.addCell(cell1);
            cell1 = new Cell(new Phrase(Db.nz(resuInt.getString("intestazione_riga5"), ""), new Font(Font.HELVETICA, 7, Font.NORMAL)));
            setLogo(cell1);
            datatable.addCell(cell1);
            cell1 = new Cell(new Phrase(Db.nz(resuInt.getString("intestazione_riga6"), ""), new Font(Font.HELVETICA, 7, Font.NORMAL)));
            setLogo(cell1);
            datatable.addCell(cell1);
            document.add(datatable);
            datatable = new Table(3);
            
            int[] headerwidths2 = { 0, 100, 0 }; // percentage
            datatable.setWidths(headerwidths2);
            datatable.setBorderWidth(1);
            datatable.setWidth(100);
//            datatable.setCellpadding(0);
            datatable.setPadding(2);
//            datatable.setOffset(20);
//            datatable.setDefaultCellBorderColor(new Color(255, 255, 255));
//            datatable.setDefaultCellBorder(0);
            datatable.addCell("");

            Cell temp;
            temp = new Cell(new Phrase("Stampa Provvigioni", new Font(Font.HELVETICA, 9, Font.BOLD)));
            temp.setHorizontalAlignment(Cell.ALIGN_CENTER);
            datatable.addCell(temp);
            datatable.addCell("");
            document.add(datatable);
            
            document.add(new Phrase("\nAgente: " + this.agente, new Font(Font.HELVETICA, 9, Font.BOLD)));
            document.add(new Phrase("\nPeriodo: " + this.periodo, new Font(Font.HELVETICA, 9, Font.BOLD)));
            
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    void corpo() {

        boolean continua = true;
        boolean continua1 = true;
        boolean continua2 = true;
        Cell c;
        java.text.SimpleDateFormat df = new java.text.SimpleDateFormat();

        //per rottura di codice CLIENTE
        //per rottura di codice FATTURA
        String oldCliente = "";
        String oldFattura = "";

        try {
            datatable = new Table(7);

            int[] headerwidths3 = { 6, 10, 10, 5, 5, 5, 5 };
            datatable.setWidths(headerwidths3);
            datatable.setBorderWidth(0.1f);
            datatable.setWidth(100);
//            datatable.setCellpadding(1);
            datatable.setPadding(2);
//            datatable.setDefaultCellBorderColor(new Color(200, 200, 200));

            //datatable.setDefaultCellBorder();
            //intestazione
            c = new Cell(new Phrase("Scadenza", new Font(Font.HELVETICA, 8, Font.NORMAL)));
            set1(c);
            datatable.addCell(c);
            c = new Cell(new Phrase("Fattura", new Font(Font.HELVETICA, 8, Font.NORMAL)));
            set1(c);
            datatable.addCell(c);
            c = new Cell(new Phrase("Cliente", new Font(Font.HELVETICA, 8, Font.NORMAL)));
            set1(c);
            datatable.addCell(c);
            c = new Cell(new Phrase("Importo scadenza", new Font(Font.HELVETICA, 8, Font.NORMAL)));
            set1(c);
            datatable.addCell(c);
            c = new Cell(new Phrase("% Provvigione", new Font(Font.HELVETICA, 8, Font.NORMAL)));
            set1(c);
            datatable.addCell(c);
            c = new Cell(new Phrase("Importo Provvigione", new Font(Font.HELVETICA, 8, Font.NORMAL)));
            set1(c);
            datatable.addCell(c);
            c = new Cell(new Phrase("Pagata", new Font(Font.HELVETICA, 8, Font.NORMAL)));
            set1(c);
            datatable.addCell(c);
            totale = 0;

            while (resu.next()) {
                c = new Cell((new Phrase(it.tnx.Util.formatDataItalian(resu.getDate("S data scadenza")), new Font(Font.HELVETICA, 8, Font.NORMAL))));
                set2(c);
                datatable.addCell(c);
                c = new Cell((new Phrase(resu.getString("test_fatt.numero") + " del " + it.tnx.Util.formatDataItalian(resu.getDate("test_fatt.data")), new Font(Font.HELVETICA, 8, Font.NORMAL))));
                set2(c);
                datatable.addCell(c);
                c = new Cell((new Phrase(resu.getString("cliente"), new Font(Font.HELVETICA, 8, Font.NORMAL))));
                set2(c);
                datatable.addCell(c);
                c = new Cell((new Phrase(FormatUtils.formatEuroIta(resu.getDouble("P importo")), new Font(Font.HELVETICA, 8, Font.NORMAL))));
                set2(c);
                c.setHorizontalAlignment(c.ALIGN_RIGHT);
                datatable.addCell(c);
                c = new Cell((new Phrase(FormatUtils.formatPerc(resu.getDouble("P %")), new Font(Font.HELVETICA, 8, Font.NORMAL))));
                set2(c);
                datatable.addCell(c);
                c = new Cell((new Phrase(FormatUtils.formatEuroIta(resu.getDouble("P provvigione")), new Font(Font.HELVETICA, 8, Font.NORMAL))));
                set2(c);
                c.setHorizontalAlignment(c.ALIGN_RIGHT);
                datatable.addCell(c);
                c = new Cell((new Phrase(it.tnx.Db2.nz(resu.getString("P pagata"), ""), new Font(Font.HELVETICA, 8, Font.NORMAL))));
                set2(c);
                datatable.addCell(c);
                totale += resu.getDouble("P provvigione");
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    void piede() {

        //aggiungo cella a dx con totale
        //fine tabella scadenze
        try {

            Cell c;
            c = new Cell();
            set2(c);
            c.setColspan(4);
            datatable.addCell(c);
            c = new Cell(new Phrase("Totale   \u20ac " + Db.formatValuta(totale), new Font(Font.HELVETICA, 8, Font.BOLD)));
            set2(c);
            c.setColspan(2);
            c.setHorizontalAlignment(c.ALIGN_RIGHT);
            datatable.addCell(c);
            document.add(datatable);
        } catch (Exception err) {
            err.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, err.toString());
        }
    }

    float cx(float x) {

        return (PageSize.A4.getWidth() / 100 * x);
    }

    float cy(float y) {

        return ((PageSize.A4.getHeight()) - (PageSize.A4.getHeight() / 100 * y));
    }

    float cx(double x) {

        return ((PageSize.A4.getWidth() / 100) * (float)x);
    }

    float cy(double y) {

        return ((PageSize.A4.getHeight()) - (PageSize.A4.getHeight() / 100 * (float)y));
    }

    void set1(Cell tempPdfCell) {
        tempPdfCell.setBackgroundColor(new Color(220, 220, 220));
        tempPdfCell.setBorderColor(new Color(200, 200, 200));
        tempPdfCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
    }

    void set2(Cell tempPdfCell) {
        tempPdfCell.setBackgroundColor(new Color(255, 255, 255));
        tempPdfCell.setBorderColor(new Color(200, 200, 200));
        tempPdfCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
    }

    void set3(Cell tempPdfCell) {

        //tempPdfCell.setBackgroundColor(new Color(255,255,255));
        //tempPdfCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        tempPdfCell.setBorderWidth(0);

        //tempPdfCell.setBorder(4);
        //tempPdfCell.setBorderColor(new Color(200,200,200));
    }

    void setLogo(Cell tempPdfCell) {
        tempPdfCell.setBackgroundColor(new Color(255, 255, 255));
        tempPdfCell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        tempPdfCell.setBorderWidth(0);
        tempPdfCell.setBorderWidthBottom(10f);
        tempPdfCell.setBorder(0);

        tempPdfCell.setBorderColor(new Color(250,0,0));
    }

    void setVuoto(Cell tempPdfCell) {
        tempPdfCell.setBackgroundColor(new Color(255, 255, 255));
        tempPdfCell.setBorderColor(new Color(255, 255, 255));
        tempPdfCell.setBorderWidth(0);
        tempPdfCell.setBorder(0);
    }
}