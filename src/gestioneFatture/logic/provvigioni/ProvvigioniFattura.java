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
import gestioneFatture.*;
import it.tnx.commons.CastUtils;

import it.tnx.commons.DbUtils;
import it.tnx.commons.SwingUtils;
import it.tnx.invoicex.InvoicexUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.apache.commons.lang.StringUtils;

/**

 *

 * @author  marco

 */
public class ProvvigioniFattura implements JRDataSource {

    public String documento_tipo;
    public String documento_serie;
    public int documento_numero;
    public int documento_anno;
    private String pagamento_tipo;
    private boolean flag_pagata;
    private double provvigione;
    private int codiceAgente;
    private Agente agente;
    private int numero; //per calcolare il progressivo delle scadenze
    public String ret = "";
    Integer id_fattura = null;
    public int tipoFattura;
    Connection conn = null;
    Integer p_numero = null;
    Integer p_id = null;

    /** Creates a new instance of ProvvigioniFattura */
    public ProvvigioniFattura(String documento_tipo, String documento_serie, int documento_numero, int documento_anno, int codiceAgente, double provvigioni) {
        this(null, documento_tipo, documento_serie, documento_numero, documento_anno, codiceAgente, provvigioni);
    }

    public ProvvigioniFattura(Connection conn, String documento_tipo, String documento_serie, int documento_numero, int documento_anno, int codiceAgente, double provvigioni) {
        this.conn = conn;
        this.documento_tipo = documento_tipo;
        this.documento_serie = documento_serie;
        this.documento_numero = documento_numero;
        this.documento_anno = documento_anno;
        this.codiceAgente = codiceAgente;
        this.provvigione = provvigioni;
    }

    public ProvvigioniFattura(Integer id_fattura) {
        this.id_fattura = id_fattura;
        getTotaleProvvigioni(true);
    }

    public boolean generaProvvigioni() {
        return generaProvvigioni(null, null);
    }

    public boolean generaProvvigioni(Date oldDataScadenza, Date nuovaDataScadenza) {
        Connection lconn = null;
        if (conn != null) {
            lconn = conn;
        } else {
            lconn = Db.getConn();
        }

        String sql;
        this.numero = 0;

        double sconto = 0;  //lo sconto a importo
        double sc1t = 0;
        double sc2t = 0;
        double sc3t = 0;

        //le provvigioni vengono generate insieme alle scadenze di pagamento nel senso che le date di pagamento delle provvigioni
        //vanno insieme alle date di scadenza delle scadenze di pagamento
        //oppure se impostato nelle impostazioni si possono generare alla data del documento

        boolean per_scadenze = true;
        try {
            String provvigioni_tipo_data = CastUtils.toString(DbUtils.getObject(Db.getConn(), "select provvigioni_tipo_data from dati_azienda"));
            if (provvigioni_tipo_data.equalsIgnoreCase("data_fattura")) {
                per_scadenze = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        //seleziono numero ultimo +1;
        java.sql.Statement stat;
        ResultSet resu;
        Date data_fattura = null;

        try {

            //elimino le pecedenti provvigioni
            sql = "select * from provvigioni";
            sql += " where documento_tipo = " + Db.pc(this.documento_tipo, Types.VARCHAR);
            sql += " and documento_serie = " + Db.pc(this.documento_serie, Types.VARCHAR);
            sql += " and documento_numero = " + Db.pc(this.documento_numero, Types.INTEGER);
            sql += " and documento_anno = " + Db.pc(this.documento_anno, Types.INTEGER);
            sql += " and pagata = 'S'";
            if (DbUtils.containRows(lconn, sql)) {
                SwingUtils.showErrorMessage(main.getPadre(), "Attenzione sono state rigenerate le provvigioni ma alcune erano già pagate e sono state reinserite come da pagare !");
//                ret = "non generate perchè ci sono già pagate";
//                return false;
            }

            //elimino le pecedenti provvigioni
            sql = "delete from provvigioni";
            sql += " where documento_tipo = " + Db.pc(this.documento_tipo, Types.VARCHAR);
            sql += " and documento_serie = " + Db.pc(this.documento_serie, Types.VARCHAR);
            sql += " and documento_numero = " + Db.pc(this.documento_numero, Types.INTEGER);
            sql += " and documento_anno = " + Db.pc(this.documento_anno, Types.INTEGER);
            Db.executeSql(lconn, sql);

            //controllo che ci sia il codice agente
            stat = lconn.createStatement();
            sql = "select agente_codice from test_fatt";
            sql += " where serie = " + Db.pc(this.documento_serie, Types.VARCHAR);
            sql += " and numero = " + Db.pc(this.documento_numero, Types.INTEGER);
            sql += " and anno = " + Db.pc(this.documento_anno, Types.INTEGER);
            sql += " and IFNULL(tipo_fattura,0) != 7";

            resu = stat.executeQuery(sql);
            if (resu.next()) {
                if (StringUtils.isEmpty(resu.getString(1))) {
                    ret = "non generate codice agente vuoto";
                    stat.close();
                    return false;
                }
            }

            //apre il resultset per ultimo +1
            try {
                stat = lconn.createStatement();
                sql = "select numero from provvigioni";
                sql += " where documento_tipo = " + Db.pc(this.documento_tipo, Types.VARCHAR);
                sql += " and documento_serie = " + Db.pc(this.documento_serie, Types.VARCHAR);
                sql += " and documento_numero = " + Db.pc(this.documento_numero, Types.INTEGER);
                sql += " and documento_anno = " + Db.pc(this.documento_anno, Types.INTEGER);
                sql += " order by numero desc limit 1";
                resu = stat.executeQuery(sql);

                if (resu.next() == true) {
                    this.numero = (resu.getInt(1) + 1);
                } else {
                    this.numero = 1;
                }
            } catch (Exception err) {
                javax.swing.JOptionPane.showMessageDialog(null, err.toString());
            }

            //prima ricalcolo l'importo delle scadenze al netto delle spese accessorie
            int numeroScadenze = 0;
            double importoTotale = 0;
            double importoScadenzaNetta = 0;
            double importoProvvigione = 0;

            double importoTotale2 = 0;
            double importoProvvigione2 = 0;
            double importoProvvigione2Scadenza = 0;

            try {
                if (per_scadenze) {
                    stat = lconn.createStatement();
                    sql = "select id from scadenze";
                    sql += " where documento_tipo = " + Db.pc(this.documento_tipo, Types.VARCHAR);
                    sql += " and documento_serie = " + Db.pc(this.documento_serie, Types.VARCHAR);
                    sql += " and documento_numero = " + Db.pc(this.documento_numero, Types.INTEGER);
                    sql += " and documento_anno = " + Db.pc(this.documento_anno, Types.INTEGER);
                    resu = stat.executeQuery(sql);

                    while (resu.next()) {
                        numeroScadenze++;
                    }
                } else {
                    numeroScadenze = 1;
                }

                stat = lconn.createStatement();
                sql = "select totale_imponibile, spese_varie, spese_trasporto, spese_incasso, sconto1, sconto2, sconto3, data, sconto from test_fatt";
                sql += " where serie = " + Db.pc(this.documento_serie, Types.VARCHAR);
                sql += " and numero = " + Db.pc(this.documento_numero, Types.INTEGER);
                sql += " and anno = " + Db.pc(this.documento_anno, Types.INTEGER);
                sql += " and IFNULL(tipo_fattura,0) != 7";

                resu = stat.executeQuery(sql);

                if (resu.next()) {
                    importoTotale = resu.getDouble("totale_imponibile") - resu.getDouble("spese_varie") - resu.getDouble("spese_trasporto") - resu.getDouble("spese_incasso");
                    sc1t = resu.getDouble("sconto1");
                    sc2t = resu.getDouble("sconto2");
                    sc3t = resu.getDouble("sconto3");
                    data_fattura = resu.getDate("data");
                    sconto = resu.getDouble("sconto");
                }

                importoScadenzaNetta = importoTotale / numeroScadenze;

                //Nuovo Calcolo per RIGA
                //calcolo in base alle righe
                Integer id_fattura = InvoicexUtil.getIdFattura(lconn, documento_serie, documento_numero, documento_anno);
                sql = "select quantita, prezzo, sconto1, sconto2, provvigione from righ_fatt";
                sql += " where id_padre = " + id_fattura;
                List<Map> rows = DbUtils.getListMap(lconn, sql);
                for (Map row : rows) {
                    double tot_riga = CastUtils.toDouble0(row.get("quantita")) * CastUtils.toDouble0(row.get("prezzo"));
                    tot_riga = tot_riga - (tot_riga / 100d * CastUtils.toDouble0(row.get("sconto1")));
                    tot_riga = tot_riga - (tot_riga / 100d * CastUtils.toDouble0(row.get("sconto2")));

                    tot_riga = tot_riga - (tot_riga / 100d * CastUtils.toDouble0(sc1t));
                    tot_riga = tot_riga - (tot_riga / 100d * CastUtils.toDouble0(sc2t));
                    tot_riga = tot_riga - (tot_riga / 100d * CastUtils.toDouble0(sc3t));

                    System.out.println("tot_riga = " + tot_riga);

                    //tolgo una proporzione in base all'importo riga con il totale(al lordo sconto) dello sconto totale se presente
                    if (sconto > 0) {
                        //importo_riga : totale_lordo = x : sconto_a_importo -> x = importo_riga * sconto_a_importo / totale_lordo
                        double sconto_proporzionato = tot_riga * sconto / (importoTotale + sconto);
                        tot_riga -= sconto_proporzionato;
                    }

                    importoTotale2 += tot_riga;
                    importoProvvigione2 += tot_riga / 100d * CastUtils.toDouble0(row.get("provvigione"));
                }
                importoTotale2 = it.tnx.Util.round(importoTotale2, 2);
                importoProvvigione2 = it.tnx.Util.round(importoProvvigione2, 2);
                importoScadenzaNetta = it.tnx.Util.round(importoTotale2 / numeroScadenze, 2);
                importoProvvigione2Scadenza = it.tnx.Util.round(importoProvvigione2 / numeroScadenze, 2);
                importoProvvigione2 = it.tnx.Util.round(importoProvvigione2Scadenza * numeroScadenze, 2);
            } catch (Exception err) {
                javax.swing.JOptionPane.showMessageDialog(null, err.toString());
            }

            //seleziono le scadenze e rigenero le provvigioni in base a quelle
            if (per_scadenze) {
                try {
                    stat = lconn.createStatement();
                    sql = "select scadenze.*, test_fatt.agente_percentuale from scadenze";
                    sql += " left join test_fatt on test_fatt.serie = scadenze.documento_serie and test_fatt.numero = scadenze.documento_numero and test_fatt.anno = scadenze.documento_anno and IFNULL(test_fatt.tipo_fattura,0) != 7";
                    sql += " where documento_tipo = " + Db.pc(this.documento_tipo, Types.VARCHAR);
                    sql += " and documento_serie = " + Db.pc(this.documento_serie, Types.VARCHAR);
                    sql += " and documento_numero = " + Db.pc(this.documento_numero, Types.INTEGER);
                    sql += " and documento_anno = " + Db.pc(this.documento_anno, Types.INTEGER);
                    sql += " order by numero";
                    resu = stat.executeQuery(sql);

                    while (resu.next()) {
                        importoProvvigione += importoScadenzaNetta / 100 * resu.getDouble("agente_percentuale");
    //                    inserisciProvvigione(importoScadenzaNetta, importoScadenzaNetta / 100 * resu.getDouble("agente_percentuale"), resu.getDate("data_scadenza"));
                        inserisciProvvigione(lconn, importoScadenzaNetta, importoProvvigione2Scadenza, resu.getDate("data_scadenza"));
                    }
                } catch (Exception err) {
                    err.printStackTrace();
    //                javax.swing.JOptionPane.showMessageDialog(null, err.toString());
                }
            } else {
                try {
//                    importoProvvigione += importoScadenzaNetta / 100 * resu.getDouble("agente_percentuale");
                    inserisciProvvigione(lconn, importoScadenzaNetta, importoProvvigione2Scadenza, data_fattura);
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

            System.out.println("--------------------------------------");
            System.out.println("importoTotale  = " + importoTotale);
            System.out.println("importoTotale2 = " + importoTotale2);
            System.out.println("importoProvvigione  = " + importoProvvigione);
            System.out.println("importoProvvigione2 = " + importoProvvigione2);
            System.out.println("--------------------------------------");

            //Storico.scrivi("Genera Provvigioni Errore", "Documento = " + documento_serie + "/" + documento_numero + "/" + documento_anno + ", Pagamento = " + pagamento_tipo + ", Importo documento = " + documento_importo);
        } catch (Exception err) {
            err.printStackTrace();

            return false;
        }

        return true;
    }

    private void inserisciProvvigione(double importo, double importoProvvigione, java.util.Date scadenza) {
        inserisciProvvigione(null, importo, importoProvvigione, scadenza);
    }
    private void inserisciProvvigione(Connection conn, double importo, double importoProvvigione, java.util.Date scadenza) {
        Connection lconn = null;
        if (conn != null) {
            lconn = conn;
        } else {
            lconn = Db.getConn();
        }

        if (tipoFattura == dbFattura.TIPO_FATTURA_NOTA_DI_CREDITO) {
            importo = -importo;
            importoProvvigione = -importoProvvigione;
        }

        String sql = "";

        try {
            sql = "insert into provvigioni (";
            sql += "documento_tipo";
            sql += ",documento_serie";
            sql += ",documento_numero";
            sql += ",documento_anno";
            sql += ",data_scadenza";
            sql += ",pagata";
            sql += ",importo";
            sql += ",importo_provvigione";
            sql += ",numero) values (";

            //valori
            sql += Db.pc(this.documento_tipo, Types.VARCHAR);
            sql += "," + Db.pc(this.documento_serie, Types.VARCHAR);
            sql += "," + Db.pc(this.documento_numero, Types.INTEGER);
            sql += "," + Db.pc(this.documento_anno, Types.INTEGER);
            sql += "," + Db.pc(Db.formatDataMysql(scadenza), Types.DATE);
            sql += "," + Db.pc("N", Types.VARCHAR);
            sql += "," + Db.pc(importo, Types.DOUBLE);
            sql += "," + Db.pc(importoProvvigione, Types.DOUBLE);
            sql += "," + Db.pc(this.numero, Types.INTEGER);
            sql += ")";

            Db.executeSql(lconn, sql);
            
            //storico
            try {
                Storico.scrivi(conn, "Genera Provvigioni Inserisci", "Documento = " + documento_serie + "/" + documento_numero + "/" + documento_anno + ", Importo = " + importo + ", Numero = " + numero + ", Data = " + scadenza + ", Importo Provvigione= " + importoProvvigione);
            } catch (Exception e) {
                System.err.println("errore in storico: " + e.getMessage());
            }
            
            this.numero++;
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    ResultSet getResultSetDocumento() {

        String sql = "";

        if (documento_tipo.equals(Db.TIPO_DOCUMENTO_DDT)) {
            sql = "Select * from test_ddt";
        } else if (documento_tipo.equals(Db.TIPO_DOCUMENTO_FATTURA)) {
            sql = "Select * from test_fatt";
        }

        sql += " where serie = " + Db.pc(documento_serie, Types.VARCHAR);
        sql += " and numero = " + Db.pc(documento_numero, Types.INTEGER);
        sql += " and anno = " + Db.pc(documento_anno, Types.INTEGER);
        if (documento_tipo.equals(Db.TIPO_DOCUMENTO_FATTURA)) {
            sql += " and IFNULL(tipo_fattura,0) != 7";
        }

        ResultSet tempDocu = Db.openResultSet(sql);

        try {
            tempDocu.next();
        } catch (Exception err) {
            err.printStackTrace();
        }

        return tempDocu;
    }

    public double getTotaleProvvigioni() {
        return getTotaleProvvigioni(false);
    }

    List<Map> rows = null;
    double importoProvvigione2 = 0;
    double importoProvvigione2Scadenza = 0;
    public double getTotaleProvvigioni(boolean ds) {
        String sql;
        java.sql.Statement stat;
        ResultSet resu;
        double importoTotale2 = 0;

        double sc1t = 0;
        double sc2t = 0;
        double sc3t = 0;

        boolean per_scadenze = true;
        try {
            String provvigioni_tipo_data = CastUtils.toString(DbUtils.getObject(Db.getConn(), "select provvigioni_tipo_data from dati_azienda"));
            if (provvigioni_tipo_data.equalsIgnoreCase("data_fattura")) {
                per_scadenze = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            //prendo sconti di testata
            //calcolo in base alle righe
            if (id_fattura == null) {
                id_fattura = InvoicexUtil.getIdFattura(documento_serie, documento_numero, documento_anno);
            }
            sql = "select t.serie, t.numero, t.anno, t.cliente, t.data, t.sconto1 as sc1t, t.sconto2 as sc2t, t.sconto3 as sc3t,";
            sql += " r.id as rigaid, r.riga, r.quantita, r.prezzo, r.provvigione, r.sconto1 as sc1r, r.sconto2 as sc2r ,";
            sql += " r.codice_articolo, r.descrizione, t.sconto, t.totale_imponibile, t.spese_varie, t.spese_trasporto, t.spese_incasso";
            sql += " from test_fatt t left join righ_fatt r on t.id = r.id_padre";
            sql += " where t.id = " + id_fattura;

            //sconti di testata
            rows = DbUtils.getListMap(Db.getConn(), sql);

            int numeroScadenze = 1;
            if (per_scadenze) {
                numeroScadenze = InvoicexUtil.getNumeroScadenze((String)rows.get(0).get("serie"), ((Long)rows.get(0).get("numero")).intValue(), ((Long)rows.get(0).get("anno")).intValue());
            }
            for (Map row : rows) {
                double tot_riga = CastUtils.toDouble0(row.get("quantita")) * CastUtils.toDouble0(row.get("prezzo"));
                double sconto = CastUtils.toDouble0(row.get("sconto"));
                double importoTotale = CastUtils.toDouble0(row.get("totale_imponibile")) - CastUtils.toDouble0(row.get("spese_varie")) - CastUtils.toDouble0(row.get("spese_trasporto")) - CastUtils.toDouble0(row.get("spese_incasso"));
                tot_riga = tot_riga - (tot_riga / 100d * CastUtils.toDouble0(row.get("sc1r")));
                tot_riga = tot_riga - (tot_riga / 100d * CastUtils.toDouble0(row.get("sc2r")));
                tot_riga = tot_riga - (tot_riga / 100d * CastUtils.toDouble0(row.get("sc1t")));
                tot_riga = tot_riga - (tot_riga / 100d * CastUtils.toDouble0(row.get("sc2t")));
                tot_riga = tot_riga - (tot_riga / 100d * CastUtils.toDouble0(row.get("sc3t")));
                System.out.println("tot_riga = " + tot_riga);

                //tolgo una proporzione in base all'importo riga con il totale(al lordo sconto) dello sconto totale se presente
                if (sconto > 0) {
                    //importo_riga : totale_lordo = x : sconto_a_importo -> x = importo_riga * sconto_a_importo / totale_lordo
                    double sconto_proporzionato = tot_riga * sconto / (importoTotale + sconto);
                    tot_riga -= sconto_proporzionato;
                }

                importoTotale2 += tot_riga;
                importoProvvigione2 += tot_riga / 100d * CastUtils.toDouble0(row.get("provvigione"));
                row.put("imp_riga", tot_riga);
                row.put("imp_prov", tot_riga / 100d * CastUtils.toDouble0(row.get("provvigione")));
            }
            importoTotale2 = it.tnx.Util.round(importoTotale2, 2);

            importoProvvigione2 = it.tnx.Util.round(importoProvvigione2, 2);
            importoProvvigione2Scadenza = it.tnx.Util.round(importoProvvigione2 / numeroScadenze, 2);
            importoProvvigione2 = it.tnx.Util.round(importoProvvigione2Scadenza * numeroScadenze, 2);

            return importoProvvigione2;
        } catch (Exception err) {
            javax.swing.JOptionPane.showMessageDialog(null, err.toString());
        }
        return 0;
    }

    private int current = 0;
    public boolean next() throws JRException {
        current++;
        if (current > rows.size()) return false;
        return true;
    }

    public Object getFieldValue(JRField jrf) throws JRException {
        try {
            if (jrf.getName().equals("importoProvvigione2")) return importoProvvigione2;
            if (jrf.getName().equals("importoProvvigione2Scadenza")) return importoProvvigione2Scadenza;
            return rows.get(current-1).get(jrf.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static public ProvvigioniFattura getDs(Object id_fattura) {
        return new ProvvigioniFattura( ((Long)id_fattura).intValue() );
    }
}