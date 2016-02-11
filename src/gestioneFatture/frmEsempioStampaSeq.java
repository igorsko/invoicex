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
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.print.*;

import java.sql.*;

import java.text.*;

import java.util.*;

import javax.swing.*;

public class frmEsempioStampaSeq
    extends javax.swing.JFrame {

    int screenWidth = 250;
    int screenHeight = 100;
    int numeroDoc = 0;
    Db db = Db.INSTANCE;

    /** Creates new form frmEsempioStampaSeq */
    public frmEsempioStampaSeq() {
        initComponents();
        this.setBounds(100, 100, screenWidth, screenHeight);

        java.text.SimpleDateFormat formattazione_init = new java.text.SimpleDateFormat("dd/MM/yy");
        this.jTextField1.setText(formattazione_init.format(new java.util.Date()));
    }

    Book createBook(PrinterJob pj, java.util.Date giorno) {

        Book book = new Book();
        PageFormat defaultformat = new PageFormat();
        defaultformat = pj.defaultPage(defaultformat);

        //Paper paperElla = new Paper();
        //paperElla.setSize(287, 400);
        //paperElla.setImageableArea(10, 10, 277, 390);
        //defaultformat.setPaper(paperElla);
        PageFormat landscapeformat = new PageFormat();
        landscapeformat.setOrientation(PageFormat.LANDSCAPE);

        //qui va messo il controllo sul numero di record da stampare
        java.text.SimpleDateFormat formattazione = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String strTemp = formattazione.format(giorno);
        ResultSet resu = db.openResultSet("select * from test_ddt where data = '" + strTemp + "';");
        numeroDoc = 0;

        try {

            while (resu.next()) {
                numeroDoc++;
            }
        } catch (Exception err) {
            err.printStackTrace();
        }

        PagePrinter[] page = new PagePrinter[numeroDoc];

        //this.numeroDoc = page.length;
        int pageWidth = (int)defaultformat.getImageableWidth();
        int pageHeight = (int)defaultformat.getImageableHeight();
        Font font1 = new Font("Courier", Font.PLAIN, 10);
        Font font2 = new Font("Courier", Font.BOLD, 10);
        Font font3 = new Font("Courier", Font.PLAIN, 9);
        Font font4 = new Font("Courier", Font.PLAIN, 8);
        Font font5 = new Font("Courier", Font.BOLD, 8);
        Font font6 = new Font("Courier", Font.BOLD, 11);
        Font font7 = new Font("Courier", Font.BOLD, 9);
        Font font8 = new Font("Courier", Font.PLAIN, 7);
        resu = db.openResultSet("select * from test_ddt where data = '" + strTemp + "';");

        int indPagine = 0;

        try {

            while (resu.next()) {
                page[indPagine] = new PagePrinter();
                page[indPagine].addPrintElement(new PrintElement("rectangle", 320, 80, 200, 100));
                page[indPagine].addPrintElement(new PrintElement(" La Bifora S.n.c. di Merolli & C.", font6, 75, 100));
                page[indPagine].addPrintElement(new PrintElement("Via Ricasoli, 76  53100 SIENA (SI)", font6, 75, 120));
                page[indPagine].addPrintElement(new PrintElement("     P.IVA  00856590526", font6, 75, 140));
                page[indPagine].addPrintElement(new PrintElement("Data e Ora:", font8, 335, 593));
                page[indPagine].addPrintElement(new PrintElement("Consegna o inizio trasporto", font3, 100, 568));
                page[indPagine].addPrintElement(new PrintElement("a mezzo mittente", font7, 100, 583));
                page[indPagine].addPrintElement(new PrintElement("Generalita' conducente:", font8, 335, 568));
                page[indPagine].addPrintElement(new PrintElement("rectangle", 330, 585, 160, 25));
                page[indPagine].addPrintElement(new PrintElement("rectangle", 330, 560, 160, 25));
                page[indPagine].addPrintElement(new PrintElement("Firma conducente   _________________", font4, 320, 650));
                page[indPagine].addPrintElement(new PrintElement("Firma destinatario _________________", font4, 320, 680));

                java.text.DecimalFormat decf = new java.text.DecimalFormat("##0.00");
                page[indPagine].addPrintElement(new PrintElement("Totale Documento Iva compresa", font4, 100, 650));
                page[indPagine].addPrintElement(new PrintElement("Eur " + decf.format(resu.getDouble("totale")), font4, 100, 680));

                ResultSet resucliente = db.openResultSet("select * from clie_forn where codice = " + resu.getString("cliente") + ";");
                resucliente.next();

                String strCl = resucliente.getString("ragione_sociale");

                if (strCl.length() > 32) {
                    strCl = strCl.substring(0, 31);
                }

                page[indPagine].addPrintElement(new PrintElement("Spettabile", font5, 330, 90));
                page[indPagine].addPrintElement(new PrintElement(strCl, font3, 330, 110));
                strCl = resucliente.getString("indirizzo");

                if (strCl.length() > 32) {
                    strCl = strCl.substring(0, 31);
                }

                page[indPagine].addPrintElement(new PrintElement(strCl, font3, 330, 125));
                strCl = resucliente.getString("cap");
                strCl += " ";
                strCl += resucliente.getString("localita");
                strCl += " ";
                strCl += resucliente.getString("provincia");

                if (strCl.length() > 32) {
                    strCl = strCl.substring(0, 31);
                }

                page[indPagine].addPrintElement(new PrintElement(strCl, font3, 330, 140));
                strCl = "P. IVA ";
                strCl += resucliente.getString("piva_cfiscale");

                if (strCl.length() > 32) {
                    strCl = strCl.substring(0, 31);
                }

                page[indPagine].addPrintElement(new PrintElement(strCl, font3, 330, 155));
                strCl = "Tel. ";
                strCl += resucliente.getString("telefono");

                if (strCl.length() > 32) {
                    strCl = strCl.substring(0, 31);
                }

                page[indPagine].addPrintElement(new PrintElement(strCl, font3, 330, 170));
                page[indPagine].addPrintElement(new PrintElement("rectangle", 80, 270, 440, 430));
                page[indPagine].addPrintElement(new PrintElement("rectangle", 80, 270, 440, 275));
                page[indPagine].addPrintElement(new PrintElement("DOCUMENTO DI TRASPORTO", font2, 100, 215));
                page[indPagine].addPrintElement(new PrintElement("Causale del trasporto: ", font1, 320, 215));
                page[indPagine].addPrintElement(new PrintElement("                         VENDITA", font2, 320, 215));
                page[indPagine].addPrintElement(new PrintElement("Aspetto esteriore beni: ", font1, 320, 230));
                page[indPagine].addPrintElement(new PrintElement("                         BUSTA", font2, 320, 230));
                page[indPagine].addPrintElement(new PrintElement("Numero colli: ", font1, 320, 245));
                page[indPagine].addPrintElement(new PrintElement("                         " + resu.getString("numero_colli"), font2, 320, 245));
                page[indPagine].addPrintElement(new PrintElement("(D.P.R. n. 472 del 14/08/96)", font4, 100, 225));
                strCl = "Numero ";
                strCl += resu.getString("numero");
                strCl += " del ";

                java.text.SimpleDateFormat formattazione2 = new java.text.SimpleDateFormat("dd/MM/yyyy");
                strCl += formattazione2.format(resu.getDate("data"));
                page[indPagine].addPrintElement(new PrintElement(strCl, font7, 100, 245));

                //inizio ciclo delle righe
                ResultSet resRighe = db.openResultSet("select * from righ_ddt where numero = " + resu.getString("numero") + " and anno = " + resu.getString("anno") + ";");
                page[indPagine].addPrintElement(new PrintElement("rectangle", 80, 270, 440, 25));
                page[indPagine].addPrintElement(new PrintElement("rectangle", 80, 270, 60, 25));
                page[indPagine].addPrintElement(new PrintElement("rectangle", 140, 270, 290, 25));
                page[indPagine].addPrintElement(new PrintElement("rectangle", 430, 270, 30, 25));
                page[indPagine].addPrintElement(new PrintElement("rectangle", 460, 270, 60, 25));
                page[indPagine].addPrintElement(new PrintElement("Codice           Descrizione                                    um     Qta", font7, 90, 285));

                int altezzaRiga = 270;

                try {

                    while (resRighe.next()) {
                        altezzaRiga = (altezzaRiga + 25);
                        page[indPagine].addPrintElement(new PrintElement("rectangle", 80, altezzaRiga, 60, 25));
                        page[indPagine].addPrintElement(new PrintElement("rectangle", 140, altezzaRiga, 290, 25));
                        page[indPagine].addPrintElement(new PrintElement("rectangle", 430, altezzaRiga, 30, 25));
                        page[indPagine].addPrintElement(new PrintElement("rectangle", 460, altezzaRiga, 60, 25));
                        strCl = resRighe.getString("codice_articolo");
                        page[indPagine].addPrintElement(new PrintElement(strCl, font3, 90, altezzaRiga + 15));
                        strCl = resRighe.getString("descrizione");
                        page[indPagine].addPrintElement(new PrintElement(strCl, font3, 150, altezzaRiga + 15));
                        strCl = resRighe.getString("um");
                        page[indPagine].addPrintElement(new PrintElement(strCl, font3, 440, altezzaRiga + 15));

                        //strCl = db.replaceChars(resRighe.getString("quantita"), '.', ",");
                        strCl = resRighe.getString("quantita");
                        strCl = strCl.substring(0, strCl.length() - 3);
                        page[indPagine].addPrintElement(new PrintElement(strCl, font3, 470, altezzaRiga + 15));
                    }
                } catch (Exception err) {
                    err.printStackTrace();
                }

                //fine ciclo dell righe
                //page[indPagine].addPrintElement (new PrintElement("line", 0, pageHeight, pageWidth, pageHeight));
                book.append(page[indPagine], defaultformat);
                indPagine++;
            }
        } catch (Exception err) {
            err.printStackTrace();
        }

        //qui va messo il ciclo delle pagine
        //page[1] = new PagePrinter();
        //page[1].addPrintElement (new PrintElement("L'API2 di stampa del JDK 1.2 funziona davvero!", font, 100, pageHeight/2));
        //page[1].addPrintElement (new PrintElement("line", 0, pageHeight, pageWidth, pageHeight));
        //page[2] = new PagePrinter();
        //page[2].addPrintElement (new PrintElement("L'API3 di stampa del JDK 1.2 funziona davvero!", font, 100, pageHeight/2));
        //page[2].addPrintElement (new PrintElement("line", 0, pageHeight, pageWidth, pageHeight));
        //book.append(page[1], defaultformat);
        //book.append(page[2], landscapeformat);
        return book;
    }

    class PagePrinter
        implements Printable {

        Vector pageContents;

        public PagePrinter() {
            pageContents = new Vector();
        }

        public int print(Graphics g, PageFormat pageformat, int pageIndex) {

            Enumeration printElements = pageContents.elements();

            while (printElements.hasMoreElements()) {

                PrintElement pe = (PrintElement)printElements.nextElement();
                pe.print(g);
            }

            return Printable.PAGE_EXISTS;
        }

        public void addPrintElement(PrintElement pe) {
            pageContents.addElement(pe);
        }
    }

    class PrintElement {

        static final int TEXT = 1;
        static final int GRAPHICS = 2;
        int type;
        String text;
        Font font;
        String shape;
        int x;
        int y;
        int width;
        int height;

        public PrintElement(String text, Font font, int x, int y) {
            type = TEXT;
            this.text = text;
            this.font = font;
            this.x = x;
            this.y = y;
        }

        public PrintElement(String shape, int x, int y, int width, int height) {
            type = GRAPHICS;
            this.shape = shape.toUpperCase();
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public void print(Graphics g) {

            Font oldFont = g.getFont();

            if (type == TEXT) {
                g.setFont(font);
                g.drawString(text, x, y);
            } else if (type == GRAPHICS) {

                if (shape.equals("OVAL")) {
                    g.drawOval(x, y, width, height);
                } else if (shape.equals("LINE")) {
                    g.drawLine(x, y, width, height);
                } else if (shape.equals("RECTANGLE")) {
                    g.drawRect(x, y, width, height);
                }
            }

            g.setFont(oldFont);
        }
    }

    /** This method is called from within the constructor to

   * initialize the form.

   * WARNING: Do NOT modify this code. The content of this method is

   * always regenerated by the Form Editor.

   */
    private void initComponents() {//GEN-BEGIN:initComponents
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });
        jButton1.setText("Stampa");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1);
        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);
        jLabel1.setText("Bolle del giorno");
        jPanel2.add(jLabel1);
        jTextField1.setPreferredSize(new java.awt.Dimension(63, 20));
        jPanel2.add(jTextField1);
        getContentPane().add(jPanel2, java.awt.BorderLayout.NORTH);
        pack();
    }//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        PrinterJob pj = PrinterJob.getPrinterJob();
        DateFormat parsaData = new SimpleDateFormat("dd/MM/yy");

        try {

            java.util.Date giornissimo = parsaData.parse(this.jTextField1.getText());
            Book book = createBook(pj, giornissimo);
            pj.setPageable(book);

            String strMess = "";

            if (numeroDoc > 0) {
                strMess += "Sicuro di stampare ";
                strMess += String.valueOf(numeroDoc);
                strMess += " documenti ?";

                int i = javax.swing.JOptionPane.showConfirmDialog(this, strMess);

                if (i == javax.swing.JOptionPane.OK_OPTION) {
                    pj.print();
                }
            } else {
                strMess += "Nessun documento da stampare";
                javax.swing.JOptionPane.showMessageDialog(this, strMess);
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        this.dispose();
    }//GEN-LAST:event_exitForm

    /**

   * @param args the command line arguments

   */
    public static void main(String[] args) {
        new frmEsempioStampaSeq().show();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton jButton1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel jLabel1;

    // End of variables declaration//GEN-END:variables
}
