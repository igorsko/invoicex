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
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import it.tnx.commons.CastUtils;
import it.tnx.invoicex.InvoicexUtil;

import java.awt.Color;

import java.sql.*;
import java.sql.ResultSet;

import java.util.*;

/**

 * Title:        GestionePreventivi

 * Description:

 * Copyright:    Copyright (c) 2001

 * Company:      TNX di Provvedi Andrea & C. s.a.s.

 * @author Marco Ceccarelli

 * @version 1.0

 * note

 * esempio per euro

 * text = resu.getString("righ_ddt.descrizione") + " " + resu.getString("righ_ddt.prezzo") + "111 \u20ac 111";

 */
public class prnDistRb {

    //stampa ddt
    private Connection connection;
    private Statement stat;
    private ResultSet resu;
    private String nomeFilePdf = "";

    //private String nomeFileHtml = "tempStampaDistintaRb.html";
    private String localita_azienda = "Poggibonsi";
    public String serie;
    public int numero;
    public int anno;
    private dbDocumento prev = new dbDocumento();
    private CoordinateBancarie coord;
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
    double totaleFattura;
    double totaleScadenze;
    Vector documentiAnomali = new Vector();
    public int rispostaConferma = 0;
    public static final int RISPOSTA_CONTINUA = 0;
    public static final int RISPOSTA_ANNULLA = 1;
    Db dbUtil = new Db();

    public prnDistRb(String sql, CoordinateBancarie coord, boolean prova, int numeroDistinta, String data_distinta) {
        nomeFilePdf = "spool" + it.tnx.Util.getDirSeparator() + "distintaRiba_" + String.valueOf(numeroDistinta) + ".pdf";
        this.coord = coord;
        this.prova = prova;
        this.numeroDistinta = numeroDistinta;
        this.data = data_distinta;

        try {
            stat = Db.getConn().createStatement();
            resu = stat.executeQuery(sql);
            report();

            //controllo regolarita' fatture e scadenze
            if (documentiAnomali.size() > 0) {

                String elencoFatture = "Elenco delle fatture con scadenze anomale";

                for (int i = 0; i < documentiAnomali.size(); i++) {
                    elencoFatture += "\n" + documentiAnomali.get(i).toString();
                }

                elencoFatture += "\n\nVuoi comunque continuare la stampa ?";

                int ret = javax.swing.JOptionPane.showConfirmDialog(main.getPadre(), elencoFatture, "Attenzione", javax.swing.JOptionPane.YES_NO_OPTION);

                if (ret == javax.swing.JOptionPane.YES_OPTION) {
                    this.rispostaConferma = this.RISPOSTA_CONTINUA;
                    reportView();
                } else {
                    this.rispostaConferma = this.RISPOSTA_ANNULLA;
                }
            } else {
                reportView();
            }
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

            //HtmlWriter.getInstance(document, new java.io.FileOutputStream(nomeFileHtml));
            document.addTitle("ddt");
            document.addSubject("ddt");
            document.addKeywords("ddt");
            document.addAuthor("TNX s.a.s");
            document.addHeader("Expires", "0");
            document.open();

            //------------------------------------
            bf = BaseFont.createFont("Helvetica", "winansi", false);
            bfCour = BaseFont.createFont("Courier", "winansi", false);
            bf_italic = BaseFont.createFont("Helvetica-Oblique", "winansi", false);
            bf_bold = BaseFont.createFont("Helvetica-Bold", "winansi", false);
            bf_times = BaseFont.createFont("Times-Roman", "winansi", false);

            //BaseFont bf = BaseFont.createFont("Helvetia", "winansi", false);
            cb = writer.getDirectContent();
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    void reportEnd() {
        document.close();
    }

    void reportView() {

        //lancio anteprima
        try {

            //Runtime.getRuntime().exec("start " + nomeFilePdf);
            Util.start(nomeFilePdf);
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    void pagina(int pagina) {
        sfondo();
        intestazione();

        //for (int i = 0 ; i < quanteRighe() ; i++) {
        corpo();

        //}
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
        } catch (Exception err) {
            err.printStackTrace();
        }

        try {
            document.add(new Phrase("\n"));
            //cliente
            double y;
            double iy;
            double x;
            double ix;
            riga = 0;
            iy = 1.25;
            y = 4;
            resu.next();

            int numColumns = 2;
            Table datatable = new Table(numColumns);
            int[] headerwidths = {65, 35}; // percentage

            datatable.setWidths(headerwidths);
            datatable.setBorderWidth(0);
            datatable.setWidth(100);
//            //datatable.setCellpadding(3);
            datatable.setPadding(2);
            //datatable.setDefaultCellBorderColor(new Color(255, 255, 255));
            //datatable.setDefaultCellBorder(0);

            //datatable.setWidthPercentage(100); // percentage
            Cell cell1;

            //intestazione spett
            //prendo intestazione da tabella dati_azienda
            Statement statInt = Db.getConn().createStatement();
            String sqlInt = "select " + main.campiDatiAzienda + " from dati_azienda";
            ResultSet resuInt = statInt.executeQuery(sqlInt);
            resuInt.next();
            this.localita_azienda = resuInt.getString("localita");
            cell1 = new Cell(new Phrase("", new Font(Font.HELVETICA, 7, Font.NORMAL)));
            cell1.setBorderWidth(0);
            datatable.addCell(cell1);
            cell1 = new Cell(new Phrase(dbUtil.nz(resuInt.getString("intestazione_riga1"), ""), new Font(Font.HELVETICA, 7, Font.NORMAL)));
            cell1.setBorderWidth(0);
            setLogo(cell1);
            datatable.addCell(cell1);
            cell1 = new Cell(new Phrase("", new Font(Font.HELVETICA, 7, Font.NORMAL)));
            cell1.setBorderWidth(0);
            datatable.addCell(cell1);
            cell1 = new Cell(new Phrase(dbUtil.nz(resuInt.getString("intestazione_riga2"), ""), new Font(Font.HELVETICA, 7, Font.NORMAL)));
            cell1.setBorderWidth(0);
            setLogo(cell1);
            datatable.addCell(cell1);
            cell1 = new Cell(new Phrase("", new Font(Font.HELVETICA, 7, Font.NORMAL)));
            cell1.setBorderWidth(0);
            datatable.addCell(cell1);
            cell1 = new Cell(new Phrase(dbUtil.nz(resuInt.getString("intestazione_riga3"), ""), new Font(Font.HELVETICA, 7, Font.NORMAL)));
            cell1.setBorderWidth(0);
            setLogo(cell1);
            datatable.addCell(cell1);
            cell1 = new Cell(new Phrase("", new Font(Font.HELVETICA, 7, Font.NORMAL)));
            cell1.setBorderWidth(0);
            datatable.addCell(cell1);
            cell1 = new Cell(new Phrase(dbUtil.nz(resuInt.getString("intestazione_riga4"), ""), new Font(Font.HELVETICA, 7, Font.NORMAL)));
            cell1.setBorderWidth(0);
            setLogo(cell1);
            datatable.addCell(cell1);
            cell1 = new Cell(new Phrase("", new Font(Font.HELVETICA, 7, Font.NORMAL)));
            cell1.setBorderWidth(0);
            datatable.addCell(cell1);
            cell1 = new Cell(new Phrase(dbUtil.nz(resuInt.getString("intestazione_riga5"), ""), new Font(Font.HELVETICA, 7, Font.NORMAL)));
            cell1.setBorderWidth(0);
            setLogo(cell1);
            datatable.addCell(cell1);
            cell1 = new Cell(new Phrase("", new Font(Font.HELVETICA, 7, Font.NORMAL)));
            cell1.setBorderWidth(0);
            datatable.addCell(cell1);
            cell1 = new Cell(new Phrase(dbUtil.nz(resuInt.getString("intestazione_riga6"), ""), new Font(Font.HELVETICA, 7, Font.NORMAL)));
            cell1.setBorderWidth(0);
            setLogo(cell1);
            datatable.addCell(cell1);
            document.add(datatable);
            datatable = new Table(3);

            int[] headerwidths2 = {0, 100, 0}; // percentage

            datatable.setWidths(headerwidths2);
            datatable.setBorderWidth(1);
            datatable.setWidth(100);
//            //datatable.setCellpadding(3);
            datatable.setPadding(2);
//            datatable.setOffset(20);
            //datatable.setDefaultCellBorderColor(new Color(255, 255, 255));
            //datatable.setDefaultCellBorder(0);
            datatable.addCell("");

            Cell temp;

            if (this.prova == true) {
                temp = new Cell(new Phrase("STAMPA IN PROVA - " + "Distinta Ricevute Bancarie del " + this.data + " - STAMPA IN PROVA", new Font(Font.HELVETICA, 9, Font.BOLD)));
            } else {
                temp = new Cell(new Phrase("Distinta Ricevute Bancarie numero " + this.numeroDistinta + " del " + this.data, new Font(Font.HELVETICA, 9, Font.BOLD)));
            }

            temp.setHorizontalAlignment(Cell.ALIGN_CENTER);
            datatable.addCell(temp);
            datatable.addCell("");
            document.add(datatable);
            document.add(new Phrase("\nSpettabile " + this.coord.findDescription(), new Font(Font.HELVETICA, 9, Font.NORMAL)));
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
            datatable = new Table(2);

            int[] headerwidths3 = {70, 30};
            datatable.setWidths(headerwidths3);
            datatable.setBorderWidth(0.1f);
            datatable.setWidth(100);
//            //datatable.setCellpadding(1);
            datatable.setPadding(2);
            //datatable.setDefaultCellBorderColor(new Color(200, 200, 200));

            ////datatable.setDefaultCellBorder();
            //intestazione
            c = new Cell(new Phrase("Cliente", new Font(Font.HELVETICA, 8, Font.NORMAL)));
            set1(c);
            datatable.addCell(c);
            c = new Cell(new Phrase("Scadenze", new Font(Font.HELVETICA, 8, Font.NORMAL)));
            set1(c);
            datatable.addCell(c);

            //test
            try {

                ResultSetMetaData meta = resu.getMetaData();

                for (int im = 1; im <= meta.getColumnCount(); im++) {
                    System.out.println("col:" + meta.getColumnName(im));
                }
            } catch (Exception err) {
                err.printStackTrace();
            }

            oldCliente = resu.getString("clie_forn_codice");

            while (continua == true) {

                if (Db.nz(resu.getString("test_fatt.opzione_riba_dest_diversa"), "N").equalsIgnoreCase("S")) {

                    //allora stampo la dest diversa
                    c = new Cell(new Phrase(Db.nz(resu.getString("test_fatt.dest_ragione_sociale"), ""), new Font(Font.HELVETICA, 8, Font.NORMAL)));
                    c.add(new Phrase(" (cod. " + resu.getString("clie_forn_codice") + ")", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                    c.add(new Phrase("\n" + resu.getString("test_fatt.dest_indirizzo"), new Font(Font.HELVETICA, 8, Font.NORMAL)));
                    c.add(new Phrase("\n" + resu.getString("test_fatt.dest_cap") + " " + resu.getString("test_fatt.dest_localita") + "(" + resu.getString("test_fatt.dest_provincia") + ")", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                } else {
                    c = new Cell(new Phrase(Db.nz(resu.getString("clie_forn_ragione_sociale"), ""), new Font(Font.HELVETICA, 8, Font.NORMAL)));
                    c.add(new Phrase(" (cod. " + resu.getString("clie_forn_codice") + ")", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                    c.add(new Phrase("\n" + resu.getString("clie_forn_indirizzo"), new Font(Font.HELVETICA, 8, Font.NORMAL)));
                    c.add(new Phrase("\n" + resu.getString("clie_forn_cap") + " " + resu.getString("clie_forn_localita") + "(" + resu.getString("clie_forn_provincia") + ")", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                }

                c.add(new Phrase("\nP.IVA / Codice Fiscale : " + resu.getString("clie_forn_piva_cfiscale"), new Font(Font.HELVETICA, 8, Font.NORMAL)));
                c.add(new Phrase("\nBanca (ABI " + Db.nz(resu.getString("test_fatt_banca_abi"), "") + ") " + Db.nz(resu.getString("banche_abi_nome"), ""), new Font(Font.HELVETICA, 8, Font.NORMAL)));
                c.add(new Phrase("\nAgenzia (CAB " + Db.nz(resu.getString("test_fatt_banca_cab"), "") + ") " + Db.nz(resu.getString("comuni_comune"), "") + " indirizzo " + Db.nz(resu.getString("banche_cab_indirizzo"), ""), new Font(Font.HELVETICA, 8, Font.NORMAL)));
                set2(c);
                datatable.addCell(c);
                c = new Cell();

                //ciclo interno per fatture
                while (continua1 == true) {

                    if (c.getElements().hasNext() == true) {
                        c.add(new Phrase("\n\nFattura " + Db.nz(resu.getString("test_fatt.serie"), "") + Db.nz(resu.getString("test_fatt.numero"), "") + " del " + Db.formatData(resu.getString("test_fatt.data")), new Font(Font.HELVETICA, 7, Font.NORMAL)));
                    } else {
                        c.add(new Phrase("Fattura " + Db.nz(resu.getString("test_fatt.serie"), "") + Db.nz(resu.getString("test_fatt.numero"), "") + " del " + Db.formatData(resu.getString("test_fatt.data")), new Font(Font.HELVETICA, 7, Font.NORMAL)));
                    }

                    double dapagare = resu.getDouble("test_fatt.totale");
                    if (CastUtils.toDouble0(resu.getDouble("test_fatt.totale_da_pagare")) != 0) {
                        dapagare = resu.getDouble("test_fatt.totale_da_pagare");
                    }
                    c.add(new Phrase("\nImporto Fattura  \u20ac " + Db.formatValuta(dapagare), new Font(Font.HELVETICA, 7, Font.NORMAL)));
                    totaleFattura = dapagare;
                    totaleScadenze = 0;

                    //ciclo interno per scadenze
                    while (continua2 == true) {

                        //stampo descrizione scadenza
                        c.add(new Phrase("\nR.B. " + resu.getString("scadenze.numero") + "  \u20ac " + Db.formatValuta(resu.getDouble("scadenze.importo")) + " Scad. " + Db.formatData(resu.getString("scadenze.data_scadenza")), new Font(Font.HELVETICA, 8, Font.NORMAL)));
                        totale += resu.getDouble("scadenze.importo");
                        totaleScadenze += resu.getDouble("scadenze.importo");
                        oldCliente = resu.getString("clie_forn_codice");
                        oldFattura = resu.getString("test_fatt.serie") + resu.getString("test_fatt.numero");

                        if (resu.next() != true) {
                            continua2 = false;
                            resu.previous();
                        } else {

                            if (!oldFattura.equals(resu.getString("test_fatt.serie") + resu.getString("test_fatt.numero"))) {
                                continua2 = false;
                                resu.previous();
                            }
                        }
                    }

                    //controllo scadenze con fattura
                    if (it.tnx.Util.round(totaleFattura, 2) != it.tnx.Util.round(totaleScadenze, 2)) {
                        documentiAnomali.add(Db.nz(resu.getString("test_fatt.numero"), "") + " del " + Db.formatData(resu.getString("test_fatt.data")) + " tot. Fattura:" + it.tnx.Util.formatValutaEuro(totaleFattura) + " tot. Scad.:" + it.tnx.Util.formatValutaEuro(totaleScadenze));
                    }

                    continua2 = true;

                    if (resu.next() != true) {
                        continua1 = false;
                        resu.previous();
                    } else {

                        if (!oldCliente.equals(resu.getString("clie_forn_codice"))) {
                            continua1 = false;
                            resu.previous();
                        }
                    }
                }

                continua1 = true;

                //inserisco la cella delle scadenze
                set2(c);
                datatable.addCell(c);
                oldCliente = resu.getString("clie_forn_codice");

                if (resu.next() != true) {
                    continua = false;
                }
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
            datatable.addCell(c);
            c = new Cell(new Phrase("Totale   \u20ac " + Db.formatValuta(totale), new Font(Font.HELVETICA, 8, Font.BOLD)));
            set2(c);
            datatable.addCell(c);
            document.add(datatable);
            datatable = new Table(2);

            int[] headerwidths3 = {70, 30};
            datatable.setWidths(headerwidths3);
            datatable.setBorderWidth(0);
            datatable.setWidth(100);
            //datatable.setCellpadding(1);
            datatable.setPadding(1);
            //datatable.setDefaultCellBorderColor(new Color(200, 200, 200));

            //aggiungo la data e la firma
            //Aggiunto da LORENZO: se la localit? non ? specificata nel DB, non viene messa
            String strLocalizzazione = "";

            if (localita_azienda != null && !(this.localita_azienda.equalsIgnoreCase(""))) {
                strLocalizzazione = this.localita_azienda + ", ";
            }

            c = new Cell(new Phrase(strLocalizzazione + this.data, new Font(Font.HELVETICA, 8, Font.BOLD)));
            set3(c);
            datatable.addCell(c);
            c = new Cell(new Phrase("L'Amministratore", new Font(Font.HELVETICA, 8, Font.BOLD)));
            set3(c);
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

        return ((PageSize.A4.getWidth() / 100) * (float) x);
    }

    float cy(double y) {

        return ((PageSize.A4.getHeight()) - (PageSize.A4.getHeight() / 100 * (float) y));
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
        tempPdfCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        tempPdfCell.setBorderWidth(0);

    //tempPdfCell.setBorderColor(new Color(200,200,200));
    }

    void setVuoto(Cell tempPdfCell) {
        tempPdfCell.setBackgroundColor(new Color(255, 255, 255));
        tempPdfCell.setBorderColor(new Color(255, 255, 255));
        tempPdfCell.setBorderWidth(0);
    }
}