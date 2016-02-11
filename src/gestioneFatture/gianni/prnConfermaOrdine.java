/**
 * Invoicex Copyright (c) 2005,2006,2007 Marco Ceccarelli, Tnx snc
 *
 * Questo software è soggetto, e deve essere distribuito con la licenza GNU
 * General Public License, Version 2. La licenza accompagna il software o potete
 * trovarne una copia alla Free Software Foundation http://www.fsf.org .
 *
 * This software is subject to, and may be distributed under, the GNU General
 * Public License, Version 2. The license should have accompanied the software
 * or you may obtain a copy of the license from the Free Software Foundation at
 * http://www.fsf.org .
 *
 * -- Marco Ceccarelli (m.ceccarelli@tnx.it) Tnx snc (http://www.tnx.it)
 *
 */
package gestioneFatture.gianni;

import java.sql.*;

import java.sql.ResultSet;

import com.lowagie.text.*;

import com.lowagie.text.pdf.*;


import java.awt.Color;



import gestioneFatture.*;
import gestioneFatture.Util;

import gestioneFatture.logic.documenti.*;

/**
 *
 * Title: GestionePreventivi
 *
 * Description:
 *
 * Copyright: Copyright (c) 2001
 *
 * Company: TNX di Provvedi Andrea & C. s.a.s.
 *
 * @author Marco Ceccarelli
 *
 * @version 1.0
 *
 * note
 *
 * esempio per euro
 *
 * text = resu.getString("righ_ddt.descrizione") + " " +
 * resu.getString("righ_ddt.prezzo") + "111 \u20ac 111";
 *
 */
public class prnConfermaOrdine {

    //stampa fattura
    private Connection connection;
    private Statement stat;
    private ResultSet resu;
    private String nomeFilePdf = "tempPrnConfermaOrdine.pdf";
    private String nomeFileHtml = "tempPrnConfermaOrdine.html";
    public String serie;
    public int numero;
    public int anno;
    private dbDocumento prev = new dbDocumento();
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
    int y;
    int iy = 1;
    Db dbUtil = Db.INSTANCE;
    private Documento doc;

    public prnConfermaOrdine(String serie, int numero, int anno) {

        this.numeroPagine = -1;

        prnConfermaOrdineNew(serie, numero, anno, numeroPagine);

    }

    public prnConfermaOrdine(String serie, int numero, int anno, int numeroPagine) {

        this.numeroPagine = numeroPagine;

        prnConfermaOrdineNew(serie, numero, anno, numeroPagine);

    }

    public void prnConfermaOrdineNew(String serie, int numero, int anno, int numeroPagine) {

        try {

            stat = Db.getConn().createStatement();

            this.serie = serie;

            this.numero = numero;

            prev.serie = serie;

            prev.numero = numero;

            prev.stato = "P";

            prev.anno = anno;

            prev.tipoDocumento = Db.TIPO_DOCUMENTO_DDT;



            prev.dbRefresh();

            //debug

            System.out.println("preventivo totale:" + prev.totale);



            String sql = "select test_ddt.*,righ_ddt.*,clie_forn.*,clie_forn_dest.*";

            sql += " from ((test_ddt left join righ_ddt on test_ddt.serie = righ_ddt.serie and test_ddt.numero = righ_ddt.numero) ";

            sql += " left join clie_forn on test_ddt.cliente = clie_forn.codice)";

            sql += " left join clie_forn_dest on test_ddt.cliente = clie_forn_dest.codice_cliente and test_ddt.cliente_destinazione = clie_forn_dest.codice";

            sql += " where test_ddt.serie = " + dbUtil.pc(serie, "VARCHAR");

            sql += " and test_ddt.numero = " + dbUtil.pc(String.valueOf(numero), "INTEGER");

            sql += " and test_ddt.anno = " + dbUtil.pc(String.valueOf(anno), "INTEGER");

            sql += " and righ_ddt.anno = " + dbUtil.pc(String.valueOf(anno), "INTEGER");

            sql += " order by righ_ddt.riga";

            //debug

            //System.out.println(sql);

            resu = stat.executeQuery(sql);



            //calcolo del totale e castelletto iva secondo nuove classi

            doc = new Documento();

            doc.load(Db.INSTANCE, numero, serie, anno, Db.TIPO_DOCUMENTO_DDT);

            doc.calcolaTotali();

            doc.visualizzaCastellettoIva();



            report();

            if (this.numeroPagine >= 0) {

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

            document.addAuthor("TNX");

            document.addHeader("Expires", "0");

            HeaderFooter footer = new HeaderFooter(new Phrase("pagina ", new Font(Font.HELVETICA, 8, Font.NORMAL)), new Phrase(" di " + this.numeroPagine, new Font(Font.HELVETICA, 8, Font.NORMAL)));

            footer.setAlignment(com.lowagie.text.Element.ALIGN_RIGHT);

            footer.setBorder(1);

            footer.setBorderWidth((float) 0.1);

            footer.setBackgroundColor(new java.awt.Color(240, 240, 240));

            //document.setFooter(footer);

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

        int paginaCorrente = writer.getPageNumber();

        document.close();

        if (this.numeroPagine == -1) {

            //rilancio con numero pagine totali

            new prnConfermaOrdine(this.serie, this.numero, prev.anno, paginaCorrente);

        }

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

        String sDestRagioneSociale = "";

        String sDestIndirizzo = "";

        String sDestCapLocalitaProvincia = "";

        String sDestTelefonoCellulare = "";



        try {

            //Image logoAzienda = Image.getInstance(main.iniPercorsoLogoStampe);

            //Image logoAzienda = Image.getInstance("img\\logoEcoplan.jpg");

            Image logoAzienda = null;

            logoAzienda = Image.getInstance("img\\logoEcoplan.jpg");

            logoAzienda.scaleToFit(200, 100);

            logoAzienda.setAbsolutePosition(35, PageSize.A4.getHeight() - 20 - (logoAzienda.getScaledHeight()));

            document.add(logoAzienda);

        } catch (Exception err) {

            err.printStackTrace();

        }



        try {

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

            int headerwidths[] = {65, 35}; // percentage

            datatable.setWidths(headerwidths);

            datatable.setBorderWidth(0);

            datatable.setWidth(100);

//      datatable.setCellpadding(0);

            datatable.setPadding(0);

//      datatable.setDefaultCellBorderColor(new Color(255,255,255));      

//      datatable.setDefaultCellBorder(0);

            //datatable.setWidthPercentage(100); // percentage



            Cell cell1;

            //intestazione spett

            //prendo intestazione da tabella dati_azienda

            Statement statInt = Db.getConn().createStatement();

            String sqlInt = "select " + main.campiDatiAzienda + " from dati_azienda";

            ResultSet resuInt = statInt.executeQuery(sqlInt);

            resuInt.next();



            cell1 = new Cell(new Phrase("", new Font(Font.HELVETICA, 7, Font.NORMAL)));

            datatable.addCell(cell1);

            cell1 = new Cell(new Phrase(dbUtil.nz(resuInt.getString("intestazione_riga1"), ""), new Font(Font.HELVETICA, 7, Font.NORMAL)));

            setLogo(cell1);

            datatable.addCell(cell1);



            cell1 = new Cell(new Phrase("", new Font(Font.HELVETICA, 7, Font.NORMAL)));

            datatable.addCell(cell1);

            cell1 = new Cell(new Phrase(dbUtil.nz(resuInt.getString("intestazione_riga2"), ""), new Font(Font.HELVETICA, 7, Font.NORMAL)));

            setLogo(cell1);

            datatable.addCell(cell1);



            cell1 = new Cell(new Phrase("", new Font(Font.HELVETICA, 7, Font.NORMAL)));

            datatable.addCell(cell1);

            cell1 = new Cell(new Phrase(dbUtil.nz(resuInt.getString("intestazione_riga3"), ""), new Font(Font.HELVETICA, 7, Font.NORMAL)));

            setLogo(cell1);

            datatable.addCell(cell1);



            cell1 = new Cell(new Phrase("", new Font(Font.HELVETICA, 7, Font.NORMAL)));

            datatable.addCell(cell1);

            cell1 = new Cell(new Phrase(dbUtil.nz(resuInt.getString("intestazione_riga4"), ""), new Font(Font.HELVETICA, 7, Font.NORMAL)));

            setLogo(cell1);

            datatable.addCell(cell1);



            cell1 = new Cell(new Phrase("", new Font(Font.HELVETICA, 7, Font.NORMAL)));

            datatable.addCell(cell1);

            cell1 = new Cell(new Phrase(dbUtil.nz(resuInt.getString("intestazione_riga5"), ""), new Font(Font.HELVETICA, 7, Font.NORMAL)));

            setLogo(cell1);

            datatable.addCell(cell1);



            cell1 = new Cell(new Phrase("", new Font(Font.HELVETICA, 7, Font.NORMAL)));

            datatable.addCell(cell1);

            cell1 = new Cell(new Phrase(dbUtil.nz(resuInt.getString("intestazione_riga6"), ""), new Font(Font.HELVETICA, 7, Font.NORMAL)));

            setLogo(cell1);

            datatable.addCell(cell1);



            document.add(datatable);



            datatable = new Table(4);

            int headerwidths2[] = {15, 15, 15, 15}; // percentage

            datatable.setWidths(headerwidths2);

            datatable.setBorderWidth(0);

            datatable.setWidth(40);

//      datatable.setCellpadding(2);

            datatable.setPadding(0);

//      datatable.setDefaultCellBorderColor(new Color(255,255,255));

            datatable.setAlignment(datatable.ALIGN_LEFT);



            //105 aggiungo tipo documento

            String tipoFattura = "CONFERMA D'ORDINE";

            cell1 = new Cell(new Phrase(tipoFattura, new Font(Font.HELVETICA, 8, Font.BOLD)));

            set1(cell1);

            cell1.setHorizontalAlignment(cell1.ALIGN_CENTER);

            cell1.setColspan(4);

            datatable.addCell(cell1);



            cell1 = new Cell(new Phrase("NUMERO", new Font(Font.HELVETICA, 8, Font.BOLD)));

            set1(cell1);

            datatable.addCell(cell1);

            cell1 = new Cell(new Phrase(resu.getString("test_ddt.numero"), new Font(Font.HELVETICA, 8, Font.BOLD)));

            set2(cell1);

            datatable.addCell(cell1);

            cell1 = new Cell(new Phrase("DATA", new Font(Font.HELVETICA, 8, Font.BOLD)));

            set1(cell1);

            datatable.addCell(cell1);

            cell1 = new Cell(new Phrase(Db.formatData(resu.getString("test_ddt.data")), new Font(Font.HELVETICA, 8, Font.BOLD)));

            set2(cell1);

            datatable.addCell(cell1);



            document.add(datatable);



            //tabella spettabile + destinazione

            datatable = new Table(2);

            datatable.setOffset(5);

//      datatable.setCellpadding(0);

            datatable.setPadding(0);

            int headerwidths4[] = {50, 50}; // percentage

            datatable.setWidths(headerwidths4);

            datatable.setWidth(100); // percentage

            datatable.setBorderWidth(0.1f);

            datatable.setBorderColor(new Color(200, 200, 200));



            cell1 = new Cell(new Phrase(" Spettabile", new Font(Font.HELVETICA, 9, Font.BOLD)));

            set3(cell1);
            datatable.addCell(cell1);

            cell1 = new Cell(new Phrase(" Destinazione merce", new Font(Font.HELVETICA, 9, Font.BOLD)));

            set3(cell1);
            datatable.addCell(cell1);



            //preparo destinazione diversa

            if (Db.nz(resu.getString("dest_ragione_sociale"), "").length() > 0) {

                sDestRagioneSociale = Db.nz(resu.getString("dest_ragione_sociale"), "");

            } else {

                sDestRagioneSociale = Db.nz(resu.getString("clie_forn_dest.ragione_sociale"), "");

            }

            if (Db.nz(resu.getString("dest_indirizzo"), "").length() > 0) {

                sDestIndirizzo = Db.nz(resu.getString("dest_indirizzo"), "");

            } else {

                sDestIndirizzo = Db.nz(resu.getString("clie_forn_dest.indirizzo"), "");

            }

            if (Db.nz(resu.getString("dest_cap"), "").length() > 0 || Db.nz(resu.getString("dest_localita"), "").length() > 0 || Db.nz(resu.getString("dest_provincia"), "").length() > 0) {

                sDestCapLocalitaProvincia = Db.nz(resu.getString("dest_cap"), "") + " " + Db.nz(resu.getString("dest_localita"), "") + " " + Db.nz(resu.getString("dest_provincia"), "");

            } else {

                sDestCapLocalitaProvincia = Db.nz(resu.getString("clie_forn_dest.cap"), "") + " " + Db.nz(resu.getString("clie_forn_dest.localita"), "") + " " + Db.nz(resu.getString("clie_forn_dest.provincia"), "");

            }

            if (Db.nz(resu.getString("dest_telefono"), "").length() > 0 || Db.nz(resu.getString("dest_cellulare"), "").length() > 0) {

                sDestTelefonoCellulare = "Tel. " + Db.nz(resu.getString("dest_telefono"), "(non presente)") + " Cell. " + Db.nz(resu.getString("dest_cellulare"), "(non presente)");

            } else {

                sDestTelefonoCellulare = "Tel. " + Db.nz(resu.getString("clie_forn_dest.telefono"), "(non presente)") + " Cell. " + Db.nz(resu.getString("clie_forn_dest.cellulare"), "(non presente)");

            }



            cell1 = new Cell(new Phrase("   " + dbUtil.nz(resu.getString("clie_forn.ragione_sociale"), ""), new Font(Font.HELVETICA, 9, Font.NORMAL)));

            set3(cell1);
            datatable.addCell(cell1);

            cell1 = new Cell(new Phrase("   " + sDestRagioneSociale, new Font(Font.HELVETICA, 9, Font.NORMAL)));

            set3(cell1);
            datatable.addCell(cell1);



            cell1 = new Cell(new Phrase("   " + dbUtil.nz(resu.getString("clie_forn.indirizzo"), ""), new Font(Font.HELVETICA, 9, Font.NORMAL)));

            set3(cell1);
            datatable.addCell(cell1);

            cell1 = new Cell(new Phrase("   " + sDestIndirizzo, new Font(Font.HELVETICA, 9, Font.NORMAL)));

            set3(cell1);
            datatable.addCell(cell1);



            cell1 = new Cell(new Phrase("   " + dbUtil.nz(resu.getString("clie_forn.cap"), "") + " " + dbUtil.nz(resu.getString("clie_forn.localita"), "") + " " + dbUtil.nz(resu.getString("clie_forn.provincia"), ""), new Font(Font.HELVETICA, 9, Font.NORMAL)));

            set3(cell1);
            datatable.addCell(cell1);

            cell1 = new Cell(new Phrase("   " + sDestCapLocalitaProvincia, new Font(Font.HELVETICA, 9, Font.NORMAL)));

            set3(cell1);
            datatable.addCell(cell1);



            cell1 = new Cell(new Phrase("   " + "P.Iva/C.Fiscale: " + dbUtil.nz(resu.getString("clie_forn.piva_cfiscale"), ""), new Font(Font.HELVETICA, 9, Font.NORMAL)));

            set3(cell1);
            datatable.addCell(cell1);

            if (dbUtil.nz(resu.getString("clie_forn_dest.piva_cfiscale"), "").length() == 0) {

                cell1 = new Cell(new Phrase("", new Font(Font.HELVETICA, 9, Font.NORMAL)));

            } else {

                cell1 = new Cell(new Phrase("   " + "P.Iva/C.Fiscale: " + dbUtil.nz(resu.getString("clie_forn_dest.piva_cfiscale"), ""), new Font(Font.HELVETICA, 9, Font.NORMAL)));

            }

            set3(cell1);
            datatable.addCell(cell1);



            //telefono

            cell1 = new Cell(new Phrase("   " + "Tel. " + dbUtil.nz(resu.getString("clie_forn.telefono"), "(non presente)") + " Cell. " + Db.nz(resu.getString("clie_forn.cellulare"), "(non presente)"), new Font(Font.HELVETICA, 9, Font.NORMAL)));

            set3(cell1);
            datatable.addCell(cell1);

            cell1 = new Cell(new Phrase("   " + sDestTelefonoCellulare, new Font(Font.HELVETICA, 9, Font.NORMAL)));

            set3(cell1);
            datatable.addCell(cell1);



            cell1 = new Cell(new Phrase("", new Font(Font.HELVETICA, 5, Font.NORMAL)));

            set3(cell1);
            datatable.addCell(cell1);

            cell1 = new Cell(new Phrase("", new Font(Font.HELVETICA, 5, Font.NORMAL)));

            set3(cell1);
            datatable.addCell(cell1);



            document.add(datatable);



            //tabella dati testata

            datatable = new Table(2);

            datatable.setBorder(0);

//      datatable.setCellpadding(0);

            datatable.setPadding(2);

            int headerwidths3[] = {20, 80}; // percentage

            datatable.setWidths(headerwidths3);

            datatable.setWidth(100); // percentage



            Phrase tempFrase;

            Cell tempPdfCell;



            //intestazione colonne

            tempPdfCell = new Cell(new Phrase("Pagamento", new Font(Font.HELVETICA, 8, Font.NORMAL)));

            set1(tempPdfCell);

            datatable.addCell(tempPdfCell);

            tempPdfCell = new Cell(new Phrase(" " + resu.getString("test_ddt.pagamento"), new Font(Font.HELVETICA, 8, Font.NORMAL)));

            set2(tempPdfCell);

            datatable.addCell(tempPdfCell);



            if (Db.formatNumero(resu.getDouble("test_ddt.sconto1")).length() > 0) {

                tempPdfCell = new Cell(new Phrase("Sconti di testata", new Font(Font.HELVETICA, 8, Font.NORMAL)));

                set1(tempPdfCell);

                datatable.addCell(tempPdfCell);



                //debug

                System.out.println("sconto2len:" + resu.getString("test_ddt.sconto2").length() + ":" + resu.getString("test_ddt.sconto2"));

                if (Db.formatNumero(resu.getDouble("test_ddt.sconto2")).length() > 0) {

                    tempPdfCell = new Cell(new Phrase(" " + Db.formatNumero(resu.getDouble("test_ddt.sconto1")) + " + " + Db.formatNumero(resu.getDouble("test_ddt.sconto2")), new Font(Font.HELVETICA, 8, Font.NORMAL)));

                } else {

                    tempPdfCell = new Cell(new Phrase(" " + Db.formatNumero(resu.getDouble("test_ddt.sconto1")) + " ", new Font(Font.HELVETICA, 8, Font.NORMAL)));

                }

                set2(tempPdfCell);

                datatable.addCell(tempPdfCell);

            }



            tempPdfCell = new Cell(new Phrase("Vostro riferimento", new Font(Font.HELVETICA, 8, Font.NORMAL)));

            set1(tempPdfCell);

            datatable.addCell(tempPdfCell);

            tempPdfCell = new Cell(new Phrase(" " + resu.getString("test_ddt.riferimento"), new Font(Font.HELVETICA, 8, Font.NORMAL)));

            set2(tempPdfCell);

            datatable.addCell(tempPdfCell);

            tempPdfCell = new Cell(new Phrase("note", new Font(Font.HELVETICA, 8, Font.NORMAL)));

            set1(tempPdfCell);

            datatable.addCell(tempPdfCell);

            tempPdfCell = new Cell(new Phrase(" " + resu.getString("test_ddt.note"), new Font(Font.HELVETICA, 8, Font.NORMAL)));

            set2(tempPdfCell);

            datatable.addCell(tempPdfCell);



            document.add(datatable);



        } catch (Exception err) {

            err.printStackTrace();

        }

    }

    void corpo() {

        String prezzoNettoStampa = "";

        String prezzoLordoStampa = "";

        String prezzoNettoTotaleStampa = "";

        String scontiStampa = "";



        try {



            /*

             y+=iy;

             cb.beginText();

             cb.setFontAndSize(bfCour, 8);



             cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, dbUtil.nz(resu.getString("righ_ddt.riga"),"") , cx(2), cy(y), 0);

             cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, dbUtil.nz(resu.getString("righ_ddt.riga_variante"),"") , cx(4.5), cy(y), 0);

             cb.showTextAligned(PdfContentByte.ALIGN_LEFT, dbUtil.nz(resu.getString("righ_ddt.codice_articolo"),"") , cx(5), cy(y), 0);

             cb.showTextAligned(PdfContentByte.ALIGN_LEFT, dbUtil.nz( resu.getString("righ_ddt.descrizione"),"") , cx(15), cy(y), 0);

             cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, dbUtil.nz(resu.getString("righ_ddt.quantita"),"") , cx(60), cy(y), 0);

             cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, dbUtil.nz(resu.getString("righ_ddt.prezzo"),"") , cx(70), cy(y), 0);

             cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, dbUtil.nz(resu.getString("righ_ddt.iva"),"") , cx(80), cy(y), 0);

             cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, dbUtil.nz(resu.getString("righ_ddt.sconto1"),"") + " " + dbUtil.nz(resu.getString("righ_ddt.sconto2"),"") , cx(85), cy(y), 0);

             cb.endText();

             */

            int numColumns = 9;

            Table datatable = new Table(numColumns);

            datatable.setCellsFitPage(true);

            datatable.setOffset(5);

            datatable.setBorder(0);

//      datatable.setCellpadding(0);

            datatable.setPadding(2);

            int headerwidths[] = {8, 44, 5, 8, 8, 6, 8, 8, 5}; // percentage

            datatable.setWidths(headerwidths);

            datatable.setWidth(100); // percentage



            Phrase tempFrase;

            Cell tempPdfCell;



            //intestazione colonne

            tempFrase = new Phrase("Codice", new Font(Font.HELVETICA, 8, Font.NORMAL));

            tempPdfCell = new Cell(tempFrase);

            tempPdfCell.setBackgroundColor(new Color(255, 255, 240));

            tempPdfCell.setBorderColor(new Color(200, 200, 200));

            tempPdfCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);

            datatable.addCell(tempPdfCell);

            tempFrase = new Phrase("Descrizione", new Font(Font.HELVETICA, 8, Font.NORMAL));

            tempPdfCell = new Cell(tempFrase);

            tempPdfCell.setBackgroundColor(new Color(255, 255, 240));

            tempPdfCell.setBorderColor(new Color(200, 200, 200));

            tempPdfCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);

            datatable.addCell(tempPdfCell);

            tempFrase = new Phrase("u.m.", new Font(Font.HELVETICA, 8, Font.NORMAL));

            tempPdfCell = new Cell(tempFrase);

            tempPdfCell.setBackgroundColor(new Color(255, 255, 240));

            tempPdfCell.setBorderColor(new Color(200, 200, 200));

            tempPdfCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);

            datatable.addCell(tempPdfCell);

            tempFrase = new Phrase("Qt\u00e0", new Font(Font.HELVETICA, 8, Font.NORMAL));

            tempPdfCell = new Cell(tempFrase);

            tempPdfCell.setBackgroundColor(new Color(255, 255, 240));

            tempPdfCell.setBorderColor(new Color(200, 200, 200));

            tempPdfCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);

            datatable.addCell(tempPdfCell);

            tempFrase = new Phrase("Prezzo lordo", new Font(Font.HELVETICA, 8, Font.NORMAL));

            tempPdfCell = new Cell(tempFrase);

            tempPdfCell.setBackgroundColor(new Color(255, 255, 240));

            tempPdfCell.setBorderColor(new Color(200, 200, 200));

            tempPdfCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);

            datatable.addCell(tempPdfCell);

            tempFrase = new Phrase("Sconti", new Font(Font.HELVETICA, 8, Font.NORMAL));

            tempPdfCell = new Cell(tempFrase);

            tempPdfCell.setBackgroundColor(new Color(255, 255, 240));

            tempPdfCell.setBorderColor(new Color(200, 200, 200));

            tempPdfCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);

            datatable.addCell(tempPdfCell);

            tempFrase = new Phrase("Prezzo netto", new Font(Font.HELVETICA, 8, Font.NORMAL));

            tempPdfCell = new Cell(tempFrase);

            tempPdfCell.setBackgroundColor(new Color(255, 255, 240));

            tempPdfCell.setBorderColor(new Color(200, 200, 200));

            tempPdfCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);

            datatable.addCell(tempPdfCell);

            tempFrase = new Phrase("Prezzo netto Totale", new Font(Font.HELVETICA, 8, Font.NORMAL));

            tempPdfCell = new Cell(tempFrase);

            tempPdfCell.setBackgroundColor(new Color(255, 255, 240));

            tempPdfCell.setBorderColor(new Color(200, 200, 200));

            tempPdfCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);

            datatable.addCell(tempPdfCell);

            tempFrase = new Phrase("Iva", new Font(Font.HELVETICA, 8, Font.NORMAL));

            tempPdfCell = new Cell(tempFrase);

            tempPdfCell.setBackgroundColor(new Color(255, 255, 240));

            tempPdfCell.setBorderColor(new Color(200, 200, 200));

            tempPdfCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);

            datatable.addCell(tempPdfCell);

            /*

             tempFrase = new Phrase("Sconto", new Font(Font.HELVETICA, 8, Font.NORMAL));

             tempPdfCell = new Cell(tempFrase);

             tempPdfCell.setBackgroundColor(new Color(255,255,240));

             tempPdfCell.setBorderColor(new Color(200,200,200));

             tempPdfCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);

             datatable.addCell(tempPdfCell);

             */



            int i = 0;

            Color colore;

            boolean continua = true;

            double prezzoLordo = 0;

            double sconto1 = 0;

            double sconto2 = 0;

            double sconto3 = 0;

            double prezzoNetto = 0;

            double prezzoNettoTotale = 0;

            while (continua == true) {

                i++;

                if (i % 2 == 1) {

                    colore = new Color(255, 255, 255);

                } else {

                    colore = new Color(255, 255, 250);

                }

                if (dbUtil.nz(resu.getString("righ_ddt.codice_articolo"), "").equalsIgnoreCase("FINESTRA") || dbUtil.nz(resu.getString("righ_ddt.codice_articolo"), "").equalsIgnoreCase("PERSIANA")) {

                    colore = new Color(250, 250, 230);

                }

                tempPdfCell = new Cell(new Phrase(dbUtil.nz(resu.getString("righ_ddt.codice_articolo"), ""), new Font(Font.HELVETICA, 6, Font.NORMAL)));

                tempPdfCell.setBackgroundColor(colore);

                tempPdfCell.setBorderColor(new Color(200, 200, 200));

                datatable.addCell(tempPdfCell);

                tempPdfCell = new Cell(new Phrase(dbUtil.nz(resu.getString("descrizione"), ""), new Font(Font.HELVETICA, 8, Font.NORMAL)));

                tempPdfCell.setBackgroundColor(colore);

                tempPdfCell.setBorderColor(new Color(200, 200, 200));

                datatable.addCell(tempPdfCell);

                tempPdfCell = new Cell(new Phrase(dbUtil.nz(resu.getString("um"), ""), new Font(Font.HELVETICA, 8, Font.NORMAL)));

                tempPdfCell.setBorderColor(new Color(200, 200, 200));

                tempPdfCell.setBackgroundColor(colore);

                datatable.addCell(tempPdfCell);

                tempPdfCell = new Cell(new Phrase(Db.formatNumero(resu.getDouble("quantita")), new Font(Font.HELVETICA, 8, Font.NORMAL)));

                tempPdfCell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);

                tempPdfCell.setBorderColor(new Color(200, 200, 200));

                tempPdfCell.setBackgroundColor(colore);

                datatable.addCell(tempPdfCell);



                prezzoLordo = resu.getDouble("prezzo");

                sconto1 = resu.getDouble("righ_ddt.sconto1");

                sconto2 = resu.getDouble("righ_ddt.sconto2");

                prezzoNetto = prezzoLordo - (prezzoLordo / 100 * sconto1);

                prezzoNetto = prezzoNetto - (prezzoNetto / 100 * sconto2);

                sconto1 = resu.getDouble("test_ddt.sconto1");

                sconto2 = resu.getDouble("test_ddt.sconto2");

                sconto3 = resu.getDouble("test_ddt.sconto3");

                prezzoNetto = prezzoNetto - (prezzoNetto / 100 * sconto1);

                prezzoNetto = prezzoNetto - (prezzoNetto / 100 * sconto2);

                prezzoNetto = prezzoNetto - (prezzoNetto / 100 * sconto3);

                if (resu.getObject("righ_ddt.quantita") != null) {

                    prezzoNettoTotale = prezzoNetto * resu.getDouble("righ_ddt.quantita");

                }

                //se da ddt non faccio vedere gli importi a 0

                if (dbUtil.nz(resu.getString("descrizione"), "").startsWith("***")
                        || dbUtil.nz(resu.getString("descrizione"), "").toUpperCase().indexOf("DA DDT") > 0
                        || dbUtil.nz(resu.getString("descrizione"), "").toUpperCase().indexOf("DA D.D.T") > 0
                        || dbUtil.nz(resu.getString("codice_articolo"), "").startsWith("***")) {

                    if (resu.getDouble("prezzo") == 0) {

                        prezzoLordoStampa = "";

                        prezzoNettoStampa = "";

                        prezzoNettoTotaleStampa = "";

                    } else {

                        prezzoLordoStampa = Db.formatDecimal5(resu.getDouble("prezzo"));

                        prezzoNettoStampa = Db.formatDecimal5(prezzoNetto);

                        prezzoNettoTotaleStampa = Db.formatDecimal(prezzoNettoTotale);

                    }

                } else {

                    prezzoLordoStampa = Db.formatDecimal5(resu.getDouble("prezzo"));

                    prezzoNettoStampa = Db.formatDecimal5(prezzoNetto);

                    prezzoNettoTotaleStampa = Db.formatDecimal(prezzoNettoTotale);

                }

                //preparo casella sconti

                if (resu.getDouble("righ_ddt.sconto1") == 0 && resu.getDouble("righ_ddt.sconto2") == 0) {

                    scontiStampa = "";

                } else if (resu.getDouble("righ_ddt.sconto2") == 0) {

                    scontiStampa = Db.formatNumero(resu.getDouble("righ_ddt.sconto1"));

                } else {

                    scontiStampa = Db.formatNumero(resu.getDouble("righ_ddt.sconto1")) + "+" + Db.formatNumero(resu.getDouble("righ_ddt.sconto2"));

                }



                tempPdfCell = new Cell(new Phrase(prezzoLordoStampa, new Font(Font.HELVETICA, 8, Font.NORMAL)));

                tempPdfCell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);

                tempPdfCell.setBorderColor(new Color(200, 200, 200));

                tempPdfCell.setBackgroundColor(colore);

                datatable.addCell(tempPdfCell);



                tempPdfCell = new Cell(new Phrase(scontiStampa, new Font(Font.HELVETICA, 7, Font.NORMAL)));

                tempPdfCell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);

                tempPdfCell.setBorderColor(new Color(200, 200, 200));

                tempPdfCell.setBackgroundColor(colore);

                datatable.addCell(tempPdfCell);



                tempPdfCell = new Cell(new Phrase(prezzoNettoStampa, new Font(Font.HELVETICA, 8, Font.NORMAL)));

                tempPdfCell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);

                tempPdfCell.setBorderColor(new Color(200, 200, 200));

                tempPdfCell.setBackgroundColor(colore);

                datatable.addCell(tempPdfCell);



                tempPdfCell = new Cell(new Phrase(prezzoNettoTotaleStampa, new Font(Font.HELVETICA, 8, Font.NORMAL)));

                tempPdfCell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);

                tempPdfCell.setBorderColor(new Color(200, 200, 200));

                tempPdfCell.setBackgroundColor(colore);

                datatable.addCell(tempPdfCell);



                tempPdfCell = new Cell(new Phrase(Db.formatNumero(resu.getDouble("iva")), new Font(Font.HELVETICA, 8, Font.NORMAL)));

                tempPdfCell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);

                tempPdfCell.setBorderColor(new Color(200, 200, 200));

                tempPdfCell.setBackgroundColor(colore);

                datatable.addCell(tempPdfCell);



                /*

                 if (Db.formatNumero(resu.getDouble("righ_ddt.sconto2")).length() > 0) {

                 tempPdfCell = new Cell(new Phrase(Db.formatNumero(resu.getDouble("righ_ddt.sconto1")) + " + " + Db.formatNumero(resu.getDouble("righ_ddt.sconto2")), new Font(Font.HELVETICA, 8, Font.NORMAL)));

                 } else {

                 tempPdfCell = new Cell(new Phrase(Db.formatNumero(resu.getDouble("righ_ddt.sconto1")) + " ", new Font(Font.HELVETICA, 8, Font.NORMAL)));

                 }

                 tempPdfCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);

                 tempPdfCell.setBorderColor(new Color(200,200,200));

                 tempPdfCell.setBackgroundColor(colore);

                 datatable.addCell(tempPdfCell);

                 */



                if (resu.next() != true) {

                    continua = false;

                    resu.previous();

                }

            }



            document.add(datatable);

        } catch (Exception err) {

            err.printStackTrace();

        }

    }

    void piede() {

        try {

            //int yTab1 = 230;

            //int yTab2 = 100;

            int yTab1 = 170;



            Phrase phrase;

            PdfPTable datatableP = new PdfPTable(6);

            int widths[] = {15, 21, 15, 21, 14, 14};

            //datatableP.setWidth(100);

            datatableP.setWidthPercentage(100);

            datatableP.setWidths(widths);

            datatableP.getDefaultCell().setPadding(2);



//      //calcola la parte finale
//
//      PdfDocument temp = (PdfDocument)(document.listeners.get(0));      
//
//      System.out.println("docY:" + temp.currentHeight);
//
//      float y = temp.currentHeight;
//
//
//
            PdfPCell cell;
            cell = new PdfPCell(new Phrase(""));
            set2(cell);

//      if (y < yTab1) {
//
//        document.newPage();
//
//        y = temp.currentHeight;
//
//        y = PageSize.A4.getHeight() - y - 40;
//
//        cell.setMinimumHeight(PageSize.A4.getHeight() - 40  - yTab1);
//
//      } else {
//
//        cell.setMinimumHeight(y - yTab1);
//
//      }

            cell.setTop(100);

            cell.setColspan(6);

            //datatableP.addCell(cell);



            //spese di trasporto

            /*

             cell = new PdfPCell(new Phrase("Spese trasporto", new Font(Font.HELVETICA, 7, Font.NORMAL)));

             cell.setColspan(4);

             set1(cell);datatableP.addCell(cell);

             cell = new PdfPCell(new Phrase(" " + Db.formatDecimal(resu.getDouble("test_ddt.spese_varie")), new Font(Font.HELVETICA, 7, Font.NORMAL)));

             cell.setColspan(2);

             set2(cell);datatableP.addCell(cell);

             */



            //---------------------

            //aggiungo riga di note per conferma d'ordine

            Phrase tempFrase = new Phrase("Si prega di ritornare controfirmata la presente per accettazione, comunque dopo 48 ore dal ricevimento la stessa si considera tacitamente accettata.\n", new Font(Font.HELVETICA, 9, Font.NORMAL));

            PdfPCell tempPdfCell = new PdfPCell(tempFrase);

            tempPdfCell.setColspan(6);

            tempPdfCell.setBackgroundColor(new Color(255, 255, 240));

            tempPdfCell.setBorderColor(new Color(200, 200, 200));

            tempPdfCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);

            datatableP.addCell(tempPdfCell);



            cell = new PdfPCell(new Phrase("Aspetto esteriore beni", new Font(Font.HELVETICA, 8, Font.NORMAL)));

            set1(cell);
            datatableP.addCell(cell);

            cell = new PdfPCell(new Phrase(" " + Db.nz(resu.getString("test_ddt.aspetto_esteriore_beni"), ""), new Font(Font.HELVETICA, 8, Font.NORMAL)));

            set2(cell);
            datatableP.addCell(cell);

            cell = new PdfPCell(new Phrase("Numero Colli", new Font(Font.HELVETICA, 8, Font.NORMAL)));

            set1(cell);
            datatableP.addCell(cell);

            cell = new PdfPCell(new Phrase(" " + Db.nz(resu.getString("test_ddt.numero_colli"), ""), new Font(Font.HELVETICA, 8, Font.NORMAL)));

            set2(cell);
            datatableP.addCell(cell);



            phrase = new Phrase("Peso lordo", new Font(Font.HELVETICA, 6, Font.NORMAL));

            //phrase.add(new Phrase("  " + Db.nz(resu.getString("test_ddt.peso_lordo"),""), new Font(Font.HELVETICA, 7, Font.NORMAL)));

            phrase.add(new Phrase("  ", new Font(Font.HELVETICA, 7, Font.NORMAL)));

            cell = new PdfPCell(phrase);

            set2(cell);
            datatableP.addCell(cell);



            phrase = new Phrase("Peso netto", new Font(Font.HELVETICA, 6, Font.NORMAL));

            //phrase.add(new Phrase(Db.nz("  " + resu.getString("test_ddt.peso_netto"),""), new Font(Font.HELVETICA, 7, Font.NORMAL)));

            phrase.add(new Phrase("  ", new Font(Font.HELVETICA, 7, Font.NORMAL)));

            cell = new PdfPCell(phrase);

            set2(cell);
            cell.setMinimumHeight(20);
            datatableP.addCell(cell);



            //---------------------------------

            cell = new PdfPCell(new Phrase("Consegna o inizio trasporto a mezzo", new Font(Font.HELVETICA, 9, Font.ITALIC)));

            cell.setColspan(2);

            set1(cell);
            datatableP.addCell(cell);

            cell = new PdfPCell(new Phrase(" " + Db.nz(resu.getString("test_ddt.mezzo_consegna"), ""), new Font(Font.HELVETICA, 9, Font.NORMAL)));

            set2(cell);
            datatableP.addCell(cell);

            cell = new PdfPCell(new Phrase(" DATA", new Font(Font.HELVETICA, 6, Font.NORMAL)));

            set2(cell);
            datatableP.addCell(cell);

            cell = new PdfPCell(new Phrase(" GENERALITA' DEL CONDUCENTE", new Font(Font.HELVETICA, 6, Font.NORMAL)));

            cell.setColspan(2);

            set2(cell);
            cell.setMinimumHeight(25);
            datatableP.addCell(cell);





            //---------------------------------

            cell = new PdfPCell(new Phrase("Vettore", new Font(Font.HELVETICA, 9, Font.NORMAL)));

            set1(cell);
            datatableP.addCell(cell);

            cell = new PdfPCell(new Phrase(" " + Db.nz(resu.getString("test_ddt.vettore1"), ""), new Font(Font.HELVETICA, 9, Font.NORMAL)));

            cell.setColspan(3);

            set2(cell);
            datatableP.addCell(cell);

            cell = new PdfPCell(new Phrase("Firma Vettore", new Font(Font.HELVETICA, 6, Font.NORMAL)));

            cell.setColspan(2);

            set2(cell);
            cell.setMinimumHeight(25);
            datatableP.addCell(cell);



            //---------------------------------

            cell = new PdfPCell(new Phrase("Porto", new Font(Font.HELVETICA, 9, Font.NORMAL)));

            set1(cell);
            datatableP.addCell(cell);

            cell = new PdfPCell(new Phrase(" " + Db.nz(resu.getString("test_ddt.porto"), ""), new Font(Font.HELVETICA, 9, Font.NORMAL)));

            cell.setColspan(2);

            set2(cell);
            datatableP.addCell(cell);

            cell = new PdfPCell(new Phrase("Firma conducente", new Font(Font.HELVETICA, 8, Font.NORMAL)));

            set1(cell);
            datatableP.addCell(cell);

            cell = new PdfPCell(new Phrase(" ", new Font(Font.HELVETICA, 8, Font.NORMAL)));

            cell.setColspan(2);

            set2(cell);
            cell.setMinimumHeight(25);
            datatableP.addCell(cell);



            phrase = new Phrase("Annotazioni", new Font(Font.HELVETICA, 5, Font.NORMAL));

            phrase.add(new Phrase("  " + Db.nz(resu.getString("test_ddt.note"), ""), new Font(Font.HELVETICA, 9, Font.BOLD, new java.awt.Color(200, 0, 0))));

            cell = new PdfPCell(phrase);

            cell.setColspan(3);

            set2(cell);
            datatableP.addCell(cell);

            cell = new PdfPCell(new Phrase("Firma destinatario", new Font(Font.HELVETICA, 8, Font.NORMAL)));

            set1(cell);
            datatableP.addCell(cell);

            cell = new PdfPCell(new Phrase(" ", new Font(Font.HELVETICA, 8, Font.NORMAL)));

            cell.setColspan(2);

            set2(cell);
            cell.setMinimumHeight(25);
            datatableP.addCell(cell);



            datatableP.setTotalWidth(523);

            PdfContentByte cb = writer.getDirectContent();

            datatableP.writeSelectedRows(0, -1, document.leftMargin(), yTab1, cb);

        } catch (Exception err) {

            err.printStackTrace();

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

        tempPdfCell.setBackgroundColor(new Color(255, 255, 240));

        tempPdfCell.setBorderColor(new Color(200, 200, 200));

        tempPdfCell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);

    }

    void set2(Cell tempPdfCell) {

        tempPdfCell.setBackgroundColor(new Color(255, 255, 255));

        tempPdfCell.setBorderColor(new Color(200, 200, 200));

        tempPdfCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);

    }

    void set3(Cell tempPdfCell) {

        //tempPdfCell.setBackgroundColor(new Color(255,255,255));

        //tempPdfCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);

        tempPdfCell.setBorderWidth(0.05f);

        tempPdfCell.setBorder(4);

        tempPdfCell.setBorderColor(new Color(200, 200, 200));

    }

    //PdfPTable
    void set1(PdfPCell tempPdfCell) {

        tempPdfCell.setBackgroundColor(new Color(255, 255, 240));

        tempPdfCell.setBorderColor(new Color(200, 200, 200));

        tempPdfCell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);

        tempPdfCell.setMinimumHeight(25);

    }

    void set2(PdfPCell tempPdfCell) {

        tempPdfCell.setBackgroundColor(new Color(255, 255, 255));

        tempPdfCell.setBorderColor(new Color(200, 200, 200));

        tempPdfCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);

        tempPdfCell.setMinimumHeight(25);

    }

    void setIva(PdfPCell tempPdfCell) {

        tempPdfCell.setBackgroundColor(new Color(255, 255, 255));

        tempPdfCell.setBorderColor(new Color(200, 200, 200));

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
