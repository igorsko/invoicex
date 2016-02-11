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
package gestioneFatture.primaNota;

import gestioneFatture.*;


import gestioneFatture.logic.documenti.*;
import it.tnx.Util;
import it.tnx.commons.CastUtils;
import it.tnx.commons.DbUtils;
import it.tnx.commons.SwingUtils;
import it.tnx.commons.cu;
import it.tnx.invoicex.InvoicexUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.text.html.HTMLDocument;
import org.apache.commons.lang.StringUtils;

public class PrimaNotaUtils {

    public static final int TIPO_PERIODO_TRIMESTRALE = 1;
    public static final int TIPO_PERIODO_MENSILE = 2;
    public static final int TIPO_PERIODO_ANNUALE = 3;
    public static int progressivo = 0;
    int tipoLiquidazione;
    int periodo;
    int anno;
    int meseDal = 0;
    int meseAl = 0;
    public TotaliIva totali = new TotaliIva();
    public JProgressBar progressBar = null;
    public JLabel messaggio = null;
    JDialogCompilazioneReport dialog = null;

    /** Creates a new instance of PrimaNotaUtils */
    public PrimaNotaUtils() {
    }

    public PrimaNotaUtils(JDialogCompilazioneReport dialog) {
        this.dialog = dialog;
        this.progressBar = dialog.jProgressBar1;
        this.messaggio = dialog.jLabel1;
    }

    public void generaPrimaNota(int tipoPeriodo, int periodo, int anno, boolean data_reg, boolean scontrini) {
        List anomalie = new ArrayList();

        stato(-1, -1, "inizializzazione");

        String sql = "";
        progressivo = 0;
        this.tipoLiquidazione = tipoPeriodo;
        this.periodo = periodo;
        this.anno = anno;

        if (tipoPeriodo == TIPO_PERIODO_TRIMESTRALE) {
            meseDal = periodo * 3 - 2;
            meseAl = periodo * 3;
        }

        //elimino la precedente stampa
        sql = "delete from stampa_iva_semplice";
        Db.executeSql(sql);

        //inserisco dalle fatture ricevute
        String campodata = data_reg ? "data_doc" : "data";

        //sql = "select SQL_CALC_FOUND_ROWS t." + campodata + ", t.numero, t.serie, t.anno, t.serie_doc, t.numero_doc, t.importo, c.ragione_sociale, id from test_fatt_acquisto t";
        sql = "select SQL_CALC_FOUND_ROWS t.data_doc, t.data, t.numero, t.serie, t.anno, t.serie_doc, t.numero_doc, t.importo, c.ragione_sociale, t.id, t.imponibile, t.iva from test_fatt_acquisto t";
        sql += " left join clie_forn c on t.fornitore = c.codice";

        if (data_reg) {
            sql += " where YEAR(data_doc) = " + anno;
        } else {
            sql += " where anno = " + anno;
        }

        if (tipoPeriodo == TIPO_PERIODO_TRIMESTRALE) {
            sql += " and month(" + campodata + ") >= " + meseDal + " and month(" + campodata + ") <= " + meseAl;
        } else if (tipoPeriodo == TIPO_PERIODO_MENSILE) {
            sql += " and month(" + campodata + ") = " + periodo;
        }

        if (data_reg) {
            sql += " order by data_doc";
        } else {
            sql += " order by data, serie, numero";
        }

//        ResultSet r = Db.openResultSet(sql);

        Integer rconta = 0;
        int iconta = 0;
        ResultSet r = null;
        try {
            r = DbUtils.tryOpenResultSet(Db.getConn(), sql);
            try {
                rconta = CastUtils.toInteger0(DbUtils.getObject(Db.getConn(), "select FOUND_ROWS()"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("fatture acquisto:" + sql + " conta:" + rconta);
            stato(0, rconta, "Fatture di acquisto...");
            while (r.next()) {
                iconta++;
                
                double totale_imponibile = 0;
                double totale_iva = 0;
                double totale_totale = 0;

                //preparo sql di inserimento nella stampa
                String sqli = "insert into stampa_iva_semplice (";
                String sqlic = "tipo";
                String sqliv = "'A'";
                sqlic += ", data";
                //sqliv += ", " + Db.pc(r.getDate(campodata), Types.DATE);
                sqliv += ", " + Db.pc(r.getDate("data"), Types.DATE);
                sqlic += ", data_doc";
                sqliv += ", " + Db.pc(r.getDate("data_doc"), Types.DATE);
                sqlic += ", numero_prog";
                sqliv += ", " + Db.pc(r.getInt("numero"), Types.VARCHAR);
                sqlic += ", numero_doc";
                sqliv += ", " + Db.pc(r.getString("serie_doc") + " " + r.getString("numero_doc"), Types.VARCHAR);
                sqlic += ", ragione_sociale";
                sqliv += ", " + Db.pc(r.getString("ragione_sociale"), Types.VARCHAR);

                if (r.getInt("numero") == 16) {
                    System.out.println("stop");
                }

                //calcolo del totale e castelletto iva secondo nuove classi
                Documento doc;
                doc = new Documento();
                doc.load(Db.INSTANCE, r.getInt("numero"), r.getString("serie"), r.getInt("anno"), Db.TIPO_DOCUMENTO_FATTURA_RICEVUTA, r.getInt("id"));
                doc.calcolaTotali();

                if ((Util.round(r.getDouble("importo"), 2) != Util.round(doc.getTotale(), 2)) && (Math.abs(Util.round(r.getDouble("importo"), 2) - Util.round(doc.getTotale(), 2)) >= 1.0D)) {
                    anomalie.add(new Object[]{"Acquisto", Integer.valueOf(r.getInt("id")), r.getString("serie"), Integer.valueOf(r.getInt("numero")), Integer.valueOf(r.getInt("anno")), Double.valueOf(Util.round(r.getDouble("importo"), 2)), Double.valueOf(Util.round(doc.getTotale(), 2))});
                }

                sqlic += ", totale";
//                sqliv += ", " + Db.pc(r.getDouble("importo"), Types.DOUBLE);
                sqliv += ", " + Db.pc(doc.getTotale(), Types.DOUBLE);

                //ciclo per i codici iva descendig senza quelle a zero
                sql = "select codice from codici_iva where percentuale > 0 order by percentuale desc";

                Statement stat_riva = Db.getConn().createStatement();
                ResultSet riva = stat_riva.executeQuery(sql);
                int codiceIva = 0;

                Double impIndeducibile = doc.getImpNonDeducibile();
                impIndeducibile = Util.round(impIndeducibile, 2);
                String impIndeducibileStr = Db.pc(impIndeducibile, Types.DOUBLE);
                totale_imponibile += impIndeducibile;

                Double ivaIndeducibile = doc.getImpIvaNonDeducibile();
                ivaIndeducibile = Util.round(ivaIndeducibile, 2);
                String ivaIndeducibileStr = Db.pc(ivaIndeducibile, Types.DOUBLE);
                totale_iva += ivaIndeducibile;

                while (riva.next()) {
                    codiceIva++;

                    //seleziono i dettagli iva per ogni codice
                    double imponibile = doc.getImpIvaDeducibile(riva.getString("codice"));

                    if (imponibile != 0) {
                        sqlic += ", imp" + codiceIva;
                        sqlic += ", iva" + codiceIva;

//                        if (Db.nz(r.getString("tipofattura"), "").equals("NC")) {
//                            sqliv += ", " + Db.pc(doc.getImpIva(riva.getString("codice")) * -1, Types.DOUBLE);
//                            sqliv += ", " + Db.pc(doc.getIva(riva.getString("codice")) * -1, Types.DOUBLE);
//                            totali.totaleVendite += doc.getIva(riva.getString("codice")) * -1;
//                        } else {
                        Double imp = doc.getImpIvaDeducibile(riva.getString("codice"));
                        imp = Util.round(imp, 2);
                        String impStr = Db.pc(imp, Types.DOUBLE);
                        totale_imponibile += imp;

                        Double iva = doc.getIvaDeducibile(riva.getString("codice"));
                        iva = Util.round(iva, 2);
                        String ivaStr = Db.pc(iva, Types.DOUBLE);
                        totale_iva += iva;

                        sqliv += ", " + impStr;
                        sqliv += ", " + ivaStr;
                        System.out.println("totaleAcquisti += " + CastUtils.toDouble0(doc.getIvaDeducibile(riva.getString("codice"))) + " numero:" + r.getInt("numero") + " tot: " + totali.totaleAcquisti);
//                        totali.totaleAcquisti += CastUtils.toDouble0(doc.getIvaDeducibile(riva.getString("codice")));
                        totali.totaleAcquisti += iva;
//                        }
                    }

                }

                sqlic += ", imp_deducibile";
                sqlic += ", iva_deducibile";
                sqliv += ", " + impIndeducibileStr;
                sqliv += ", " + ivaIndeducibileStr;
                stat_riva.close();

                //ciclo per i codici iva descendig senza quelle a zero
                sql = "select codice from codici_iva where percentuale = 0";

                ResultSet rivaNoIva = Db.openResultSet(sql);
                codiceIva = 0;

                double totImpNoIva = 0;

                while (rivaNoIva.next()) {
                    codiceIva++;

                    //seleziono i dettagli iva per ogni codice
                    double imponibile = 0;

//                    if (Db.nz(r.getString("tipofattura"), "").equals("NC")) {
//                        imponibile = doc.getImpIva(rivaNoIva.getString("codice")) * -1;
//                    } else {
                    imponibile = doc.getImpIva(rivaNoIva.getString("codice"));
                    totale_imponibile += imponibile;
//                    }

                    if (imponibile != 0) {
                        totImpNoIva += imponibile;
                    }

                }

                sqlic += ", altre_imp";
                sqliv += ", " + Db.pc(totImpNoIva, Types.DOUBLE);
                sqli = sqli + sqlic + ") values (" + sqliv + ")";
//                System.out.println(sqli);
                Db.executeSql(sqli);
                
                //controllo totali registrati in fattura se uguali a totali ricalcolati
                totale_totale = totale_imponibile + totale_iva;
                if (Util.round(totale_iva,2) != r.getDouble("iva") || Util.round(totale_imponibile,2) != r.getDouble("imponibile") || Util.round(totale_totale,2) != r.getDouble("importo")) {
                    System.out.println("!!! totale_totale: " + totale_totale + " : " + r.getDouble("importo"));
                    System.out.println("!!! totale_imponibile: " + totale_imponibile + " : " + r.getDouble("imponibile"));
                    System.out.println("!!! totale_iva: " + totale_iva + " : " + r.getDouble("iva"));
                }

                //insPnTesta(r.getDate("data"), "FA", r.getString("numero_doc"), r.getString("serie_doc"), r.getDate("data_doc"), r.getInt("numero"), null);
//                System.out.println(r.getString(1));

                try {
                    stato(iconta, rconta, "Fatture di acquisto... " + r.getString("serie") + " " + r.getInt("numero") + "/" + r.getInt("anno"));
                } catch (Exception e) {
                }
            }

        } catch (Exception err) {
            err.printStackTrace();
        }
        try {
            r.getStatement().close();
            r.close();
        } catch (Exception e) {
        }

        stato(-1, -1, "Fatture di vendita... ");

        //inserisco dalle fatture di vendita
        sql = "select SQL_CALC_FOUND_ROWS t.serie, t.numero, t.data, t.totale, t.anno, IF(t.tipo_fattura = 7,'* scontrino *', c.ragione_sociale) as ragione_sociale, tf.descrizione_breve as tipofattura, t.id, t.tipo_fattura from test_fatt t";
        sql += " left join clie_forn c on t.cliente = c.codice";
        sql += " left join tipi_fatture tf on t.tipo_fattura = tf.tipo";

        sql += " where anno = " + anno;

        if (tipoPeriodo == TIPO_PERIODO_TRIMESTRALE) {
            sql += " and month(data) >= " + meseDal + " and month(data) <= " + meseAl;
        } else if (tipoPeriodo == TIPO_PERIODO_MENSILE) {
            sql += " and month(data) = " + periodo;
        }
        sql += " and tf.descrizione_breve != 'FP'";
        sql += " and anno = " + anno;
        if (!scontrini) {
            sql += " and t.tipo_fattura != 7";
        }
        sql += " order by data, serie, numero";
//        r = Db.openResultSet(sql);
        System.out.println("sql = " + sql);

        try {
            r = DbUtils.tryOpenResultSet(Db.getConn(), sql);

            rconta = 0;
            try {
                rconta = CastUtils.toInteger0(DbUtils.getObject(Db.getConn(), "select FOUND_ROWS()"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("fatture vendita:" + sql + " conta:" + rconta);

            stato(0, rconta, "Fatture di vendita...");
            iconta = 0;

            while (r.next()) {
                iconta++;

                if (r.getString("numero").equals("1082")) {
                    System.out.println("sss");
                }
                
                //preparo sql di inserimento nella stampa
                String sqli = "insert into stampa_iva_semplice (";
                String sqlic = "tipo";
                String sqliv = "'V'";
                sqlic += ", data";
                sqliv += ", " + Db.pc(r.getDate("data"), Types.DATE);
                sqlic += ", numero_doc";
                String serie = r.getString("serie");
                String numerodoc = (StringUtils.isBlank(serie) ? "" : serie + "/") + r.getString("numero");
                try {
                    if (r.getInt("anno") >= 2013) {
                        if (InvoicexUtil.getTipoNumerazione() == InvoicexUtil.TIPO_NUMERAZIONE_ANNO) {
                            numerodoc += "/" + r.getString("anno");
                        } else if (InvoicexUtil.getTipoNumerazione() == InvoicexUtil.TIPO_NUMERAZIONE_ANNO_2CIFRE) {
                            numerodoc += "/" + StringUtils.right(r.getString("anno"), 2);
                        }                        
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                sqliv += ", " + Db.pc(numerodoc, Types.VARCHAR);
                sqlic += ", ragione_sociale";
                sqliv += ", " + Db.pc(r.getString("ragione_sociale"), Types.VARCHAR);
                sqlic += ", totale";


                //calcolo del totale e castelletto iva secondo nuove classi
                Documento doc;
                doc = new Documento();
                Integer tipo_fattura = cu.toInteger0(r.getObject("tipo_fattura"));
                if (tipo_fattura == dbFattura.TIPO_FATTURA_SCONTRINO) {
                    doc.load(Db.INSTANCE, r.getInt("numero"), r.getString("serie"), r.getInt("anno"), Db.TIPO_DOCUMENTO_SCONTRINO, r.getInt("id"));
                } else {
                    doc.load(Db.INSTANCE, r.getInt("numero"), r.getString("serie"), r.getInt("anno"), Db.TIPO_DOCUMENTO_FATTURA, r.getInt("id"));
                }
                doc.calcolaTotali();

                if (Db.nz(r.getString("tipofattura"), "").equals("NC")) {
                    sqliv += ", " + Db.pc(doc.getTotale() * -1, Types.DOUBLE);
                } else {
                    sqliv += ", " + Db.pc(doc.getTotale(), Types.DOUBLE);
                }

                if ((Util.round(r.getDouble("totale"), 2) != Util.round(doc.getTotale(), 2)) && (Math.abs(Util.round(r.getDouble("totale"), 2) - Util.round(doc.getTotale(), 2)) >= 1.0D)) {
                    anomalie.add(new Object[]{"Vendita", Integer.valueOf(r.getInt("id")), r.getString("serie"), Integer.valueOf(r.getInt("numero")), Integer.valueOf(r.getInt("anno")), Double.valueOf(Util.round(r.getDouble("totale"), 2)), Double.valueOf(Util.round(doc.getTotale(), 2))});
                }

                //ciclo per i codici iva descendig senza quelle a zero
                sql = "select codice from codici_iva where percentuale > 0 order by percentuale desc";

                ResultSet riva = Db.openResultSet(sql);
                int codiceIva = 0;

                while (riva.next()) {
                    codiceIva++;

                    //seleziono i dettagli iva per ogni codice
                    double imponibile = doc.getImpIva(riva.getString("codice"));

                    if (imponibile != 0) {
                        sqlic += ", imp" + codiceIva;
                        sqlic += ", iva" + codiceIva;


                        if (Db.nz(r.getString("tipofattura"), "").equals("NC")) {
                            sqliv += ", " + Db.pc(doc.getImpIva(riva.getString("codice")) * -1, Types.DOUBLE);
                            sqliv += ", " + Db.pc(doc.getIva(riva.getString("codice")) * -1, Types.DOUBLE);

                            System.out.println("totaleVendite += " + CastUtils.toDouble0(doc.getIva(riva.getString("codice")) * -1) + " numero:" + r.getInt("numero") + " totale: " + totali.totaleVendite);
                            totali.totaleVendite += CastUtils.toDouble0(doc.getIva(riva.getString("codice")) * -1);
                        } else {
                            sqliv += ", " + Db.pc(doc.getImpIva(riva.getString("codice")), Types.DOUBLE);
                            sqliv += ", " + Db.pc(doc.getIva(riva.getString("codice")), Types.DOUBLE);

                            System.out.println("totaleVendite += " + CastUtils.toDouble0(doc.getIva(riva.getString("codice"))) + " numero:" + r.getInt("numero") + " totale: " + totali.totaleVendite);
                            totali.totaleVendite += doc.getIva(riva.getString("codice"));
                        }
                    }
                }

                //ciclo per i codici iva descendig senza quelle a zero
                sql = "select codice from codici_iva where percentuale = 0";

                ResultSet rivaNoIva = Db.openResultSet(sql);
                codiceIva = 0;

                double totImpNoIva = 0;

                while (rivaNoIva.next()) {
                    codiceIva++;

                    //seleziono i dettagli iva per ogni codice
                    double imponibile = 0;

                    if (Db.nz(r.getString("tipofattura"), "").equals("NC")) {
                        imponibile = doc.getImpIva(rivaNoIva.getString("codice")) * -1;
                    } else {
                        imponibile = doc.getImpIva(rivaNoIva.getString("codice"));
                    }

                    if (imponibile != 0) {
                        totImpNoIva += imponibile;
                    }
                }

                sqlic += ", altre_imp";
                sqliv += ", " + Db.pc(totImpNoIva, Types.DOUBLE);
                sqli = sqli + sqlic + ") values (" + sqliv + ")";
//                System.out.println(sqli);
                Db.executeSql(sqli);

                //insPnTesta(r.getDate("data"), "FA", r.getString("numero_doc"), r.getString("serie_doc"), r.getDate("data_doc"), r.getInt("numero"), null);
//                System.out.println(r.getString(1));

                try {
                    stato(iconta, rconta, "Fatture di vendita... " + r.getString("serie") + " " + r.getInt("numero") + "/" + r.getInt("anno"));
                } catch (Exception e) {
                }

            }
        } catch (Exception err) {
            err.printStackTrace();
        }

        try {
            r.getStatement().close();
            r.close();
        } catch (Exception e) {
        }

        if (anomalie.size() > 0) {
            SwingUtils.showErrorMessage(main.getPadreFrame(), "Riscontrate anomalie su una o più fatture, visualizza il dettaglio dalla prossima videata", "Attenzione", true);

            JDialogAnomalie da = new JDialogAnomalie(main.getPadreFrame(), true);
            da.anomalie = anomalie;
            da.text.setContentType("text/html");
            String bodyRule = "body { font-family: Monospaced; font-size: 14pt; }";

            ((HTMLDocument) da.text.getDocument()).getStyleSheet().addRule(bodyRule);
            String testo = "<html><pre><table>";
            testo = testo + "<tr>";
            testo = testo + "<td>Tipo</td>";
            testo = testo + "<td>ID</td>";
            testo = testo + "<td>Serie</td>";
            testo = testo + "<td>Numero</td>";
            testo = testo + "<td>Anno</td>";
            testo = testo + "<td>Totale registrato</td>";
            testo = testo + "<td>Totale ricalcolato</td>";
            testo = testo + "</tr>";
            for (int i = 0; i < anomalie.size(); i++) {
                Object[] row = (Object[]) (Object[]) anomalie.get(i);
                testo = testo + "<tr>";
                testo = testo + "<td>" + row[0] + "</td>";
                testo = testo + "<td>" + row[1] + "</td>";
                testo = testo + "<td>" + row[2] + "</td>";
                testo = testo + "<td>" + row[3] + "</td>";
                testo = testo + "<td>" + row[4] + "</td>";
                testo = testo + "<td>" + row[5] + "</td>";
                testo = testo + "<td>" + row[6] + "</td>";
                testo = testo + "</tr>";
            }
            testo = testo + "</table></pre></html>";
            da.text.setText(testo);
            da.text.setEditable(false);
            da.setLocationRelativeTo(null);
            da.setVisible(true);
        }
    }

    private void insPnTesta(java.util.Date data, String causale, String numDocEst, String serieDocEst, java.util.Date dataDocEst, int protocollo, java.util.Date dataCompIva) {

        String sql = "insert into prima_nota_teste (";
        String sqlc = "";
        String sqlv = "";

        if (progressivo == 0) {
            progressivo = 1;

            String sqlPro = "select max(numero) from prima_nota_teste";

            sqlPro += " where year(data) = " + anno;

            if (tipoLiquidazione == TIPO_PERIODO_TRIMESTRALE) {
                sqlPro += " and month(data) >= " + meseDal + " and month(data) <= " + meseAl;
            } else if (tipoLiquidazione == TIPO_PERIODO_MENSILE) {
                sqlPro += " and month(data) = " + periodo;
            }

            try {

                ResultSet numero = Db.openResultSet(sqlPro);

                if (numero.next()) {
                    progressivo = numero.getInt(1) + 1;
                }
            } catch (Exception err) {
                err.printStackTrace();
            }
        }

        sqlc += "numero";
        sqlv += Db.pc(progressivo, Types.INTEGER);
        sqlc += ", data";
        sqlv += ", " + Db.pc(data, Types.DATE);
        sqlc += ", numero_doc_esterno";
        sqlv += ", " + Db.pc(numDocEst, Types.VARCHAR);
        sqlc += ", serie_doc_esterno";
        sqlv += ", " + Db.pc(serieDocEst, Types.VARCHAR);
        sqlc += ", data_doc_esterno";
        sqlv += ", " + Db.pc(dataDocEst, Types.DATE);
        sqlc += ", protocollo";
        sqlv += ", " + Db.pc(protocollo, Types.INTEGER);
        sqlc += ", data_comp_iva";
        sqlv += ", " + Db.pc(dataCompIva, Types.DATE);
        sql = sql + sqlc + ") values (" + sqlv + ")";
        Db.executeSql(sql);
    }

    private void stato(final int progress, final int maxprogress, final String msg) {
        if (progressBar != null && messaggio != null) {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    try {
                        if (progress == -1) {
                            progressBar.setIndeterminate(true);
                        } else {
                            progressBar.setIndeterminate(false);
                            progressBar.setValue(progress);
                            progressBar.setMaximum(maxprogress);
                        }
                        messaggio.setText(msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
