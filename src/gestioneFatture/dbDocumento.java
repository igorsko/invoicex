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

import gestioneFatture.logic.documenti.DettaglioIva;
import gestioneFatture.logic.documenti.Documento;
import gestioneFatture.logic.provvigioni.ProvvigioniFattura;
import it.tnx.commons.CastUtils;
import it.tnx.commons.DateUtils;
import it.tnx.commons.DbUtils;
import it.tnx.commons.DebugUtils;
import it.tnx.commons.SwingUtils;
import it.tnx.commons.cu;
import it.tnx.invoicex.InvoicexUtil;
import it.tnx.invoicex.Magazzino;
import it.tnx.invoicex.gui.JDialogSceltaQuantita;
import java.math.BigDecimal;
import java.sql.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.text.JTextComponent;

import org.apache.commons.lang.StringUtils;
import tnxbeans.*;

public class dbDocumento {

    private Db dbUtil = Db.INSTANCE;
    public String serie;
    public int numero;
    public String stato;
    public int anno;
    public int id;
    public tnxTextField texTota;
    public tnxTextField texTotaIva;
    public tnxTextField texTotaImpo;
    public double sconto1 = 0;
    public double sconto2 = 0;
    public double sconto3 = 0;
    public double speseVarie = 0;
    public double speseTrasportoIva = 0;
    public double speseIncassoIva = 0;
    public double totale;
    public double totaleIva;
    public double totaleImponibile;
    public String dbStato = "L";
    public static String DB_INSERIMENTO = "I";
    public static String DB_MODIFICA = "M";
    public static String DB_LETTURA = "L";
    public Vector elencoDdt;
    public String tipoDocumento = Db.TIPO_DOCUMENTO_FATTURA;
    public Integer tipoFattura = dbFattura.TIPO_FATTURA_IMMEDIATA;
    private gestioneFatture.logic.clienti.Cliente cliente;
    public Vector elencoDdtR;
    int contaRighe;
    SimpleDateFormat sdf;
    boolean acquisto = false;
    public Integer[] ids;
    Map cacheArticoli = null;
    Map cacheQtaLotti = null;
    Map cacheQtaMatricole = null;
    private boolean useSerie = true;
    public static Long articoli = null;
    private static boolean fareCacheArticoli = true;

    public dbDocumento() {
    }

    public boolean dbRicalcolaProgressivo(String stato, String data, JTextComponent texNumePrev, JTextComponent texAnno, String serie, Integer id) {

        if (stato.equals(frmTestDocu.DB_INSERIMENTO)) {

            //ricreo campo data
            DateFormat myFormat = new SimpleDateFormat("dd/MM/yy");
            java.util.GregorianCalendar myDate = new java.util.GregorianCalendar();
            myFormat.setLenient(false);

            try {
                myDate.setTime(myFormat.parse(data));

                //calcola il progressivo in base alla data e anno
                String sql = "select numero from ";

                if (this.tipoDocumento.equalsIgnoreCase(Db.TIPO_DOCUMENTO_DDT)) {
                    sql += "test_ddt";
                } else if (this.tipoDocumento.equalsIgnoreCase(Db.TIPO_DOCUMENTO_DDT_ACQUISTO)) {
                    sql += "test_ddt_acquisto";
                } else if (this.tipoDocumento.equalsIgnoreCase(Db.TIPO_DOCUMENTO_FATTURA_RICEVUTA)) {
                    sql += "test_fatt_acquisto";
                } else {
                    sql += "test_fatt";
                }
                int myanno = myDate.get(Calendar.YEAR);
                if ((this.tipoDocumento.equalsIgnoreCase(Db.TIPO_DOCUMENTO_FATTURA_RICEVUTA) || this.tipoDocumento.equalsIgnoreCase(Db.TIPO_DOCUMENTO_FATTURA))
                        && (InvoicexUtil.getTipoNumerazione() == InvoicexUtil.TIPO_NUMERAZIONE_ANNO_INFINITA && myanno >= 2013)) {
                    sql += " where anno >= 2013";
                } else {
                    sql += " where anno = " + myanno;
                }
                sql += " and serie = " + Db.pc(serie, Types.VARCHAR);
                if (id != null) {
                    sql += " and id != " + Db.pc(id, Types.INTEGER);
                }
                sql += " order by numero desc limit 1";

                ResultSet resu = Db.openResultSet(sql);

                if (resu.next() == true) {

                    if (texNumePrev.getText().length() == 0 || !texNumePrev.getText().equalsIgnoreCase(String.valueOf(resu.getInt(1) + 1))) {
                        texNumePrev.setText(String.valueOf(resu.getInt(1) + 1));
                    }
                } else {
                    texNumePrev.setText("1");
                }

                texAnno.setText(String.valueOf(myDate.get(Calendar.YEAR)));

                return (true);
            } catch (Exception err) {
                err.printStackTrace();

                return (false);
            }
        }

        return (true);
    }

    public void setStampato(String tipoDocumento) {

        //memorizzo data di stampa
        String sql = "";

        if (tipoDocumento.equals(Db.TIPO_DOCUMENTO_DDT)) {
            sql = "update test_ddt set stampato = '" + Db.getCurrDateTimeMysql() + "'";
        } else if (tipoDocumento.equals(Db.TIPO_DOCUMENTO_DDT_ACQUISTO)) {
            sql = "update test_ddt_acquisto set stampato = '" + Db.getCurrDateTimeMysql() + "'";
        } else if (tipoDocumento.equals(Db.TIPO_DOCUMENTO_FATTURA)) {
            sql = "update test_fatt set stampato = '" + Db.getCurrDateTimeMysql() + "'";
        }

        sql += " where serie = " + dbUtil.pc(this.serie, "VARHCAR");
        sql += " and numero = " + dbUtil.pc(String.valueOf(this.numero), "LONG");
        //sql += " and stato = " + dbUtil.pc(String.valueOf(this.stato), "VARCHAR");
        sql += " and anno = " + Db.pc(Calendar.getInstance().get(Calendar.YEAR), "INTEGER");
        Db.executeSql(sql);
    }

    public void dbRefresh() {
        //debug
        System.out.println("!!! richiamata la routine dbPreventivo.dbRefresh()");

        //calcolo del totale e castelletto iva secondo nuove classi
        gestioneFatture.logic.documenti.Documento doc = new gestioneFatture.logic.documenti.Documento();
        
//        doc.load(Db.INSTANCE, numero, serie, anno, this.tipoDocumento);
        doc.load(Db.INSTANCE, numero, serie, anno, this.tipoDocumento, id);

        doc.calcolaTotali();
        doc.visualizzaCastellettoIva();
        this.totale = doc.getTotale();
        this.totaleImponibile = doc.getTotaleImponibile();
        this.totaleIva = doc.getTotaleIva();
        this.speseVarie = doc.getSpeseVarieImponibili();
        this.speseTrasportoIva = doc.getSpeseTrasporto();
        this.speseIncassoIva = doc.getSpeseIncasso();
    }

    public String convertiInfattura(boolean raggr, boolean raggr_riepilogo) {
        return convertiInFattura(raggr, raggr_riepilogo, true);
    }

    public String convertiInFattura(boolean raggr, boolean raggr_riepilogo, boolean use_serie) {
        return convertiInFattura(raggr, raggr_riepilogo, use_serie, false);
    }

    public String convertiInFattura(boolean raggr, boolean raggr_riepilogo, boolean use_serie, boolean notaDiCredito) {

        //converte in Fattura...
        //cerco ultimo numero ordine
        String newSerie = "";
        int newNumero;
        String newStato;
        String sql;
        String sqlC = "";
        String sqlV = "";
        double speseIncasso = 0;
        double speseTrasporto = 0;
        double scontoTotale = 0;
        String codiceCliente = "";
        String pagamento = "";
        Integer giorno_pagamento = null;
        String banca_abi = "";
        String banca_cab = "";
        String banca_cc_iban = "";
        Integer agente_codice = null;
        Double agente_percentuale = null;

        this.useSerie = use_serie;

        String campo_in = "in_fatt";

        sdf = new SimpleDateFormat("dd/MM/yyyy");

        String test_fatt = acquisto ? "test_fatt_acquisto" : "test_fatt";
        String test_ddt = acquisto ? "test_ddt_acquisto" : "test_ddt";
        String righ_ddt = acquisto ? "righ_ddt_acquisto" : "righ_ddt";
        String tipo_doc = acquisto ? Db.TIPO_DOCUMENTO_FATTURA_RICEVUTA : Db.TIPO_DOCUMENTO_FATTURA;

        //fare controllo che i ddt sia tutti dello stesdso cliente
        //***
        if (!notaDiCredito && main.fileIni.getValue("pref", "riporta_serie", "0").equals("3")) {
            JDialogSelezionaSerie diaSerie = new JDialogSelezionaSerie(main.getPadre(), true);
            diaSerie.setLocationRelativeTo(null);
            diaSerie.setVisible(true);

            newSerie = diaSerie.serie;
        }

        sql = "select max(numero) from " + test_fatt;
        if (this.useSerie) {
            sql += " where serie = " + Db.pc(Db.nz(this.serie, ""), "VARCHAR");
        } else {
            if (notaDiCredito) {
                sql += " where serie = " + Db.pc(Db.nz("#", ""), "VARCHAR");
            } else if (main.fileIni.getValue("pref", "riporta_serie", "0").equals("3")) {
                sql += " where serie = " + Db.pc(Db.nz(newSerie, ""), "VARCHAR");
            } else {
                sql += " where serie = " + Db.pc(Db.nz("", ""), "VARCHAR");
            }
        }
        //sql += " and stato = " + Db.pc("P", "VARCHAR");

        //anno corrente
        //105 quando faccio conversione ci sono + ddt e non so diuqale prendere queste info
        //sql += " and anno = " + Db.pc(this.anno, "INTEGER");
        sql += " and anno = " + Db.pc(Calendar.getInstance().get(Calendar.YEAR), "INTEGER");
        sql += " and IFNULL(tipo_fattura,0) != 7";
        sql += " order by numero desc";

        //debug
        System.out.println("sql new fattura:" + sql);
        newNumero = 1;

        try {
            ResultSet tempUltimo = Db.openResultSet(sql);
            if (tempUltimo.next() == true) {
                newNumero = tempUltimo.getInt(1) + 1;
            }
        } catch (Exception err) {
            err.printStackTrace();
            SwingUtils.showErrorMessage(main.getPadre(), "Impossibile completare l'operazione.\n" + err.getMessage(), "Errore");
            return null;
        }


        if (notaDiCredito || !main.fileIni.getValue("pref", "riporta_serie", "0").equals("3")) {
            newSerie = Db.nz(this.useSerie ? this.serie : (notaDiCredito ? "#" : ""), "");
        }
        newStato = "P";

        //inserisco nuova fattura salvandomi i dati su hashtable
        //e selzionando dai ddt
        contaRighe = 0;
        sql = "select * from " + test_ddt + " t ";

        //selezion da elenco vettore
        /*
        sql += " where serie = " + Db.pc(serie,"VARCHAR");
        sql += " and numero = " + Db.pc(String.valueOf(numero),"NUMERIC");
        sql += " and stato = 'P'";*/
        sql += elencoDdt.get(0);

        sql += " order by serie, anno, numero";

        //***
        System.out.println("sql origine conversione:\n" + sql);
        ResultSet tempPrev = Db.openResultSet(sql);
        ResultSet ddtPrev = tempPrev;
        pagamento = null;

        String campoc = "cliente";
        if (acquisto) {
            campoc = "fornitore";
        }

        try {

            ResultSetMetaData metaPrev = tempPrev.getMetaData();
            boolean flag = true;

            if (tempPrev.next() == true) {
                codiceCliente = tempPrev.getString(campoc);
                try {
                    giorno_pagamento = CastUtils.toInteger0(DbUtils.getObject(Db.getConn(), "select giorno_pagamento from clie_forn where codice = " + Db.pc(codiceCliente, Types.VARCHAR)));
                } catch (Exception e) {
                }
                speseIncasso = tempPrev.getDouble("spese_incasso");
                speseTrasporto = tempPrev.getDouble("spese_trasporto");
                //calcolo spese di trasporto per somma
                sql = "select sum(IFNULL(spese_incasso, 0)) as tot_spese_incasso, sum(IFNULL(spese_trasporto,0)) as tot_spese_trasporto, sum(IFNULL(sconto,0)) as tot_sconto from " + test_ddt + " t ";
                for (int i = 0; i < elencoDdtR.size(); i++) {
                    if (i == 0) {
                        sql += " where " + elencoDdtR.get(i);
                    } else {
                        sql += " or " + elencoDdtR.get(i);
                    }
                }
                System.err.println("sql totale spese:" + sql);
                try {
                    List<Map> list = DbUtils.getListMap(Db.getConn(), sql);
                    System.out.println("list = " + list);
                    speseTrasporto = CastUtils.toDouble0(list.get(0).get("tot_spese_trasporto"));
                    speseIncasso = CastUtils.toDouble0(list.get(0).get("tot_spese_incasso"));
                    scontoTotale = CastUtils.toDouble0(list.get(0).get("tot_sconto"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ResultSet colonne_dest = Db.openResultSet("select * from " + test_fatt + " limit 0");

                for (int i = 1; i <= metaPrev.getColumnCount(); i++) {
                    flag = true;

                    if (metaPrev.getColumnName(i).equalsIgnoreCase("conto")) {
                        System.out.println("stop");
                    }

                    if (metaPrev.getColumnName(i).equalsIgnoreCase("numero")) {
                        sqlC += "numero";
                        sqlV += Db.pc(String.valueOf(newNumero), metaPrev.getColumnType(i));
                    } else if (metaPrev.getColumnName(i).equalsIgnoreCase("serie")) {
                        sqlC += "serie";
                        sqlV += Db.pc(newSerie, metaPrev.getColumnType(i));
                    } else if (!acquisto && metaPrev.getColumnName(i).equalsIgnoreCase("stato")) {
                        sqlC += "stato";
                        sqlV += Db.pc(newStato, metaPrev.getColumnType(i));
                    } else if (metaPrev.getColumnName(i).equalsIgnoreCase("anno")) {
                        sqlC += "anno";
                        sqlV += Db.pc(Calendar.getInstance().get(Calendar.YEAR), "INTEGER");
                    } else if (metaPrev.getColumnName(i).equalsIgnoreCase("spese_trasporto")) {
                        sqlC += "spese_trasporto";
                        sqlV += Db.pc(speseTrasporto, Types.DOUBLE);
                    } else if (metaPrev.getColumnName(i).equalsIgnoreCase("spese_incasso")) {
                        sqlC += "spese_incasso";
                        sqlV += Db.pc(speseIncasso, Types.DOUBLE);
                    } else if (metaPrev.getColumnName(i).equalsIgnoreCase("sconto")) {
                        sqlC += "sconto";
                        sqlV += Db.pc(scontoTotale, Types.DOUBLE);
                    } else if (metaPrev.getColumnName(i).equalsIgnoreCase("data")) {
                        DateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar myCalendar = GregorianCalendar.getInstance();
                        sqlC += "data";
                        sqlV += Db.pc(myFormat.format(myCalendar.getTime()), metaPrev.getColumnType(i));
                    } else if (metaPrev.getColumnName(i).equalsIgnoreCase(campoc)
                            || metaPrev.getColumnName(i).equalsIgnoreCase(campoc + "_destinazione")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("data_consegna")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("spese_varie")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("note")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("note_testa")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("note_corpo")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("note_piede")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("totale_imponibile")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("totale_iva")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("totale")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("pagamento")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("sconto1")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("sconto2")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("riferimento")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("sconto3")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("codice_listino")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("dest_ragione_sociale")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("dest_indirizzo")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("dest_cap")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("dest_localita")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("dest_provincia")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("dest_telefono")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("dest_cellulare")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("dest_paese")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("banca_abi")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("banca_cab")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("banca_cc")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("banca_iban")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("agente_codice")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("agente_percentuale")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("note_pagamento")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("prezzi_ivati")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("sconto")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("giorno_pagamento")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("conto")
                            ) {
                        if (metaPrev.getColumnName(i).equalsIgnoreCase("pagamento")) {
                            pagamento = tempPrev.getString(i);
                        }
                        if (metaPrev.getColumnName(i).equalsIgnoreCase("agente_codice")) {
                            agente_codice = tempPrev.getInt(i);
                        }
                        if (metaPrev.getColumnName(i).equalsIgnoreCase("agente_percentuale")) {
                            agente_percentuale = tempPrev.getDouble(i);
                        }

                        if (metaPrev.getColumnName(i).equalsIgnoreCase("riferimento")) {
                            sqlC += metaPrev.getColumnName(i);
                            String optionVostroOrdine = main.fileIni.getValue("pref", "stato_vs_ordine", "0");
                            if (optionVostroOrdine.equals("0")) {
                                sqlV += Db.pc(tempPrev.getObject(i), metaPrev.getColumnType(i));
                            } else {
                                String concat = "DDT " + String.valueOf(tempPrev.getObject("serie")) + String.valueOf(tempPrev.getObject("numero")) + " " + String.valueOf(tempPrev.getObject("anno"));
                                if (optionVostroOrdine.equals("1")) {
                                    sqlV += Db.pc(concat, metaPrev.getColumnType(i));
                                } else if (optionVostroOrdine.equals("2")) {
                                    if (String.valueOf(tempPrev.getObject(i)).equals("")) {
                                        sqlV += Db.pc(concat, metaPrev.getColumnType(i));
                                    } else {
                                        sqlV += Db.pc(tempPrev.getObject(i) + ", " + concat, metaPrev.getColumnType(i));
                                    }
                                }
                            }
//                        } else if (tempPrev.getObject(i) != null) {
                        } else {
                            //ignoro per acquisto
                            if (!DbUtils.existColumn(colonne_dest, metaPrev.getColumnName(i))) {
                                flag = false;
                            } else if (acquisto && metaPrev.getColumnName(i).equalsIgnoreCase("totale_imponibile")) {
                                sqlC += "imponibile";
                                sqlV += Db.pc(tempPrev.getObject(i), metaPrev.getColumnType(i));
                            } else if (acquisto && metaPrev.getColumnName(i).equalsIgnoreCase("totale")) {
                                sqlC += "importo";
                                sqlV += Db.pc(tempPrev.getObject(i), metaPrev.getColumnType(i));
                            } else if (acquisto && metaPrev.getColumnName(i).equalsIgnoreCase("totale_iva")) {
                                sqlC += "iva";
                                sqlV += Db.pc(tempPrev.getObject(i), metaPrev.getColumnType(i));
                            } else if (metaPrev.getColumnName(i).equalsIgnoreCase("giorno_pagamento")) {
                                sqlC += "giorno_pagamento";
                                Integer giorno_su_doc = CastUtils.toInteger0(tempPrev.getObject(i));
                                if (giorno_su_doc > 0) {
                                    giorno_pagamento = giorno_su_doc;
                                }
                                sqlV += Db.pc(giorno_pagamento, metaPrev.getColumnType(i));
                            } else {
                                sqlC += metaPrev.getColumnName(i);
                                sqlV += Db.pc(tempPrev.getObject(i), metaPrev.getColumnType(i));
                            }
                        }
                    } else {
                        //non li prendo
                        flag = false;
                    }

                    if (flag == true) {

//non capisco...
//                        if (tempPrev.getObject(i) != null) {
                        sqlC += ",";
                        sqlV += ",";
//                        }
                    }
                }

                //aggiungo tipo fattura fattura immediata = 1
                sqlC = "tipo_fattura," + sqlC;
                if (notaDiCredito) {
                    sqlV = Db.pc("3", Types.INTEGER) + "," + sqlV;
                } else {
                    sqlV = Db.pc("1", Types.INTEGER) + "," + sqlV;
                }

                //creo la insetrt
                sql = "insert into " + test_fatt;
                sql += "(" + sqlC.substring(0, sqlC.length() - 1) + ") values (" + sqlV.substring(0, sqlV.length() - 1) + ")";
                Db.executeSqlThrows(sql);
            }
        } catch (Exception err) {
            System.out.println("sqlerr:" + sql);
            err.printStackTrace();
            SwingUtils.showErrorMessage(main.getPadre(), "Impossibile completare l'operazione.\n" + err.getMessage(), "Errore");
            return null;
        }

        Integer id = null;
        try {
            id = CastUtils.toInteger(DbUtils.getObject(Db.getConn(), "select LAST_INSERT_ID()"));
        } catch (Exception ex) {
            Logger.getLogger(dbOrdine.class.getName()).log(Level.SEVERE, null, ex);
        }


        String tabellaDest = acquisto ? "fatt_acquisto" : "fatt";
        String tipoDocDest = acquisto ? Db.TIPO_DOCUMENTO_FATTURA_RICEVUTA : Db.TIPO_DOCUMENTO_FATTURA;

        if (raggr) {
            //inserisco righe
            sql = "select t.*, t.data as dataddt from " + test_ddt + " t ";
            for (int i = 0; i < elencoDdtR.size(); i++) {
                if (i == 0) {
                    sql += " where " + elencoDdtR.get(i);
                } else {
                    sql += " or " + elencoDdtR.get(i);
                }
            }
            sql += " order by t.serie, t.anno, t.numero";
            System.err.println("sql ddt:" + sql);
            ResultSet rdaddt = Db.openResultSet(sql);
//            String daddt = "Da DDT ";
            String daddt = main.fileIni.getValue("altro", "da_ddt", "*** Da DDT");
            try {
                while (rdaddt.next()) {
                    if (rdaddt.isFirst()) {
                    } else {
                        daddt += ", ";
                    }
                    daddt += rdaddt.getString("serie") + "" + rdaddt.getString("numero") + " del " + sdf.format(rdaddt.getDate("dataddt"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(dbDocumento.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("daddt:" + daddt);
            sql = "select r.*, t.data as dataddt, sum(quantita) as sqta, sum(r.totale_imponibile) as stotale_imponibile, sum(r.totale_ivato) as stotale_ivato from " + test_ddt + " t ";
            sql += "left join " + righ_ddt + " r on t.serie = r.serie and t.numero = r.numero and t.anno = r.anno";
            for (int i = 0; i < elencoDdtR.size(); i++) {
                if (i == 0) {
                    sql += " where " + elencoDdtR.get(i);
                } else {
                    sql += " or " + elencoDdtR.get(i);
                }
            }
            sql += " group by codice_articolo, um, prezzo, iva, sconto1, sconto2";
            sql += " order by t.serie, t.anno, t.numero, r.riga";
            tempPrev = Db.openResultSet(sql);
            inserisciRighe(raggr, 0, daddt, tempPrev, newNumero, newStato, newSerie);

            //ricalcolare lo stato evasa , parziale , no sul documento di origine
            try {
                for (int i = 0; i < this.elencoDdt.size(); i++) {
                    sql = "select id from " + test_ddt + " t ";
                    sql += elencoDdt.get(i);
                    ResultSet doc_origine = Db.openResultSet(sql);
                    if (doc_origine.next()) {
                        //forzo tutte le righe evase sul ddt di orgine
                        sql = "UPDATE " + righ_ddt + " AS r JOIN " + test_ddt + " as t ON r.id_padre = t.id SET r.quantita_evasa = r.quantita";
                        sql += elencoDdt.get(i);
                        DbUtils.tryExecQuery(Db.getConn(), sql);
                        //calcolo evasione
                        //InvoicexUtil.aggiornaStatoEvasione(acquisto ? Db.TIPO_DOCUMENTO_DDT_ACQUISTO : Db.TIPO_DOCUMENTO_DDT, doc_origine.getInt("id"));
                        //forzo evase
                        sql = "update " + test_ddt + " t set evaso = 'S' " + elencoDdt.get(i);
                        DbUtils.tryExecQuery(Db.getConn(), sql);
                    }
                    //metto flag che sono gia' state fatturate
                    try {
                        String convertito = CastUtils.toString(DbUtils.getObject(Db.getConn(), "select convertito from " + test_ddt + " t " + elencoDdt.get(i)));
                        convertito += (convertito.length() > 0 ? "\n" : "") + InvoicexUtil.getNumeroDaId(tipoDocDest, id, false);
                        sql = "update " + test_ddt + " t";
                        sql += " set convertito = " + Db.pc(convertito, "VARCHAR");
                        sql += elencoDdt.get(i);
                        System.out.println("sql = " + sql);
                        DbUtils.tryExecQuery(Db.getConn(), sql);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } else {
            if (raggr_riepilogo) {
                for (int i2 = 0; i2 < this.elencoDdt.size(); i2++) {
                    //inserisco righe
                    sql = "select r.*, t.data from " + test_ddt + " t ";
                    sql += "left join " + righ_ddt + " r on t.serie = r.serie and t.numero = r.numero and t.anno = r.anno";
                    sql += elencoDdt.get(i2);
                    sql += " order by t.serie, t.anno, t.numero, r.riga";
                    tempPrev = Db.openResultSet(sql);
                    inserisciRigheRiepilogative(i2, null, tempPrev, newNumero, newStato, newSerie);
                }

                //ricalcolare lo stato evasa , parziale , no sul documento di origine
                try {
                    for (int i = 0; i < this.elencoDdt.size(); i++) {
                        sql = "select id from " + test_ddt + " t ";
                        sql += elencoDdt.get(i);
                        ResultSet doc_origine = Db.openResultSet(sql);
                        if (doc_origine.next()) {
                            //forzo tutte le righe evase sul ddt di orgine
                            sql = "UPDATE " + righ_ddt + " AS r JOIN " + test_ddt + " as t ON r.id_padre = t.id SET r.quantita_evasa = r.quantita";
                            sql += elencoDdt.get(i);
                            DbUtils.tryExecQuery(Db.getConn(), sql);
                            //calcolo evasione
                            //InvoicexUtil.aggiornaStatoEvasione(acquisto ? Db.TIPO_DOCUMENTO_DDT_ACQUISTO : Db.TIPO_DOCUMENTO_DDT, doc_origine.getInt("id"));
                            //forzo evase
                            sql = "update " + test_ddt + " t set evaso = 'S' " + elencoDdt.get(i);
                            DbUtils.tryExecQuery(Db.getConn(), sql);
                        }
                        //metto flag che sono gia' state fatturate
                        try {
                            String convertito = CastUtils.toString(DbUtils.getObject(Db.getConn(), "select convertito from " + test_ddt + " t " + elencoDdt.get(i)));
                            convertito += (convertito.length() > 0 ? "\n" : "") + InvoicexUtil.getNumeroDaId(tipoDocDest, id, false);
                            sql = "update " + test_ddt + " t";
                            sql += " set convertito = " + Db.pc(convertito, "VARCHAR");
                            sql += elencoDdt.get(i);
                            System.out.println("sql = " + sql);
                            DbUtils.tryExecQuery(Db.getConn(), sql);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            } else {
                for (int i2 = 0; i2 < this.elencoDdt.size(); i2++) {
                    //inserisco righe
                    sql = "select r.*, t.data from " + test_ddt + " t ";
                    sql += "left join " + righ_ddt + " r on t.serie = r.serie and t.numero = r.numero and t.anno = r.anno";
                    sql += elencoDdt.get(i2);
                    sql += " order by t.serie, t.anno, t.numero, r.riga";
                    tempPrev = Db.openResultSet(sql);
                    inserisciRighe(raggr, i2, null, tempPrev, newNumero, newStato, newSerie, id);
                }
            }
        }

        if (!raggr && !raggr_riepilogo) {
            //presentare la tabella dei codici art./descr/quantità di origine/quantita evasa o arrivata
            JDialogSceltaQuantita dialog = new JDialogSceltaQuantita(main.getPadreFrame(), true);
            dialog.load(acquisto ? Db.TIPO_DOCUMENTO_DDT_ACQUISTO : Db.TIPO_DOCUMENTO_DDT, acquisto ? Db.TIPO_DOCUMENTO_FATTURA_RICEVUTA : Db.TIPO_DOCUMENTO_FATTURA, ids);
            dialog.setTitle("Selezione quantità");
            dialog.pack();
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
            //controllare esito
            boolean annullato = false;
            if (dialog.ok) {
                JTable table = dialog.getTable();
                for (int i = 0; i < table.getRowCount(); i++) {
                    //modificare le quantita sul documento generato
                    double qtaconf = CastUtils.toDouble0(table.getValueAt(i, table.getColumn("quantità confermata").getModelIndex()));
                    boolean convertire = CastUtils.toBoolean(table.getValueAt(i, table.getColumn("riga confermata").getModelIndex()));
                    double qta = CastUtils.toDouble0(table.getValueAt(i, table.getColumn("quantità").getModelIndex()));
                    double prezzo = CastUtils.toDouble0(table.getValueAt(i, table.getColumn("prezzo").getModelIndex()));
                    String codart = CastUtils.toString(table.getValueAt(i, table.getColumn("articolo").getModelIndex()));
                    String descr = CastUtils.toString(table.getValueAt(i, table.getColumn("descrizione").getModelIndex()));
                    
                    sql = null;
//                    if (qtaconf > 0) {
                    if (convertire) {
                        sql = "update righ_" + tabellaDest + " r ";
                        sql += " set quantita = " + Db.pc(qtaconf, Types.DOUBLE);
                        sql += " where id = " + table.getValueAt(i, table.getColumn("dest_id_riga").getModelIndex());
                    } else {
//                        if ((prezzo != 0 || qta > 0) && !main.getPersonalContain("proskin")) {
                            sql = "delete from righ_" + tabellaDest + " where id = " + table.getValueAt(i, table.getColumn("dest_id_riga").getModelIndex());
//                        }
                    }
                    if (sql != null) {
                        System.out.println("i = " + i + " sql = " + sql);
                        Db.executeSql(sql);
                    }
                    //andare sulle righe di origine a mettere la quantita evasa o arrivata
                    sql = "update " + righ_ddt + " r ";
                    double qta_evasa = CastUtils.toDouble0(table.getValueAt(i, table.getColumn("quantità confermata").getModelIndex()));
                    double qta_gia_evasa = CastUtils.toDouble0(table.getValueAt(i, table.getColumn("quantità già confermata").getModelIndex()));
                    sql += " set quantita_evasa = " + Db.pc(qta_evasa + qta_gia_evasa, Types.DOUBLE);
                    Object rigaid = table.getValueAt(i, table.getColumn("prov_id_riga").getModelIndex());
                    sql += " where id = " + table.getValueAt(i, table.getColumn("prov_id_riga").getModelIndex());
                    System.out.println("i = " + i + " sql = " + sql);
                    //Db.executeSql(sql);
                    try {
                        DbUtils.tryExecQuery(Db.conn, sql);
                    } catch (Exception e) {
                        SwingUtils.showErrorMessage(main.getPadreFrame(), "Problema nel salvataggio della quantità evasa alla riga id:" + cu.toString(rigaid) + " (i:" + i + "\n" + e.getMessage());
                    }
                }

                //ricalcolare lo stato evasa , parziale , no sul documento di origine
                try {
                    for (int i = 0; i < this.elencoDdt.size(); i++) {
                        sql = "select id from " + test_ddt + " t ";
                        sql += elencoDdt.get(i);
                        ResultSet doc_origine = Db.openResultSet(sql);
                        if (doc_origine.next()) {
                            InvoicexUtil.aggiornaStatoEvasione(acquisto ? Db.TIPO_DOCUMENTO_DDT_ACQUISTO : Db.TIPO_DOCUMENTO_DDT, doc_origine.getInt("id"));
                        }

                        //metto flag che sono gia' state fatturate
                        try {
                            String convertito = CastUtils.toString(DbUtils.getObject(Db.getConn(), "select convertito from " + test_ddt + " t " + elencoDdt.get(i)));
                            convertito += (convertito.length() > 0 ? "\n" : "") + InvoicexUtil.getNumeroDaId(tipoDocDest, id, false);
                            sql = "update " + test_ddt + " t";
                            sql += " set convertito = " + Db.pc(convertito, "VARCHAR");
                            sql += elencoDdt.get(i);
                            System.out.println("sql = " + sql);
                            DbUtils.tryExecQuery(Db.getConn(), sql);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                //portare i dati dei lotti/matricole
                InvoicexUtil.riportaLotti(table, tabellaDest, righ_ddt, tipoDocDest, main.getPadreFrame());
            } else {
                annullato = true;
                JTable table = dialog.getTable();
                Integer old_id = null;
                for (int i = 0; i < table.getRowCount(); i++) {
                    //Integer t_id = CastUtils.toInteger(table.getValueAt(i, table.getColumn("dest_id").getModelIndex()));
                    Integer t_id = id;  //id della fattura creata
                    if (old_id == null || old_id != t_id) {
                        //annullare l'inserimento nuovo
                        sql = "delete from righ_" + tabellaDest;
                        sql += " where id_padre = " + t_id;
                        System.out.println("i = " + i + " sql = " + sql);
                        Db.executeSql(sql);
                        sql = "delete from test_" + tabellaDest;
                        sql += " where id = " + t_id;
                        System.out.println("i = " + i + " sql = " + sql);
                        Db.executeSql(sql);
                        old_id = t_id;
                    }
                    //azzerare il campo_in nella tab. di origine
                    sql = "update " + righ_ddt + " r ";
                    sql += " set " + campo_in + " = null";
                    sql += " , " + campo_in + "_riga = null";
                    sql += " where id = " + table.getValueAt(i, table.getColumn("prov_id_riga").getModelIndex());
                    System.out.println("i = " + i + " sql = " + sql);
                    Db.executeSql(sql);

                }
//                try {
//                    ddtPrev.beforeFirst();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                for (Integer idn : ids) {
//                    System.out.println("idn:" + idn);
//                    // Ripristino a valore precedente riferimenti a fattura
//                    try {
//                        sql = "update " + test_ddt + " set ";
//                        sql += "fattura_serie = " + Db.pc(ddtPrev.getString("fattura_serie"), Types.VARCHAR) + ", ";
//                        sql += "fattura_numero = " + Db.pc(ddtPrev.getString("fattura_numero"), Types.INTEGER) + ", ";
//                        sql += "fattura_anno = " + Db.pc(ddtPrev.getString("fattura_anno"), Types.INTEGER) + ", ";
//                        sql += "convertito = " + Db.pc(ddtPrev.getString("convertito"), Types.VARCHAR) + ", ";
//                        sql += "evaso = " + Db.pc(ddtPrev.getString("evaso"), Types.VARCHAR) + " ";
//                        sql += "WHERE id = " + Db.pc(ddtPrev.getString("id"), Types.INTEGER);
//                        Db.executeSql(sql);
//                        ddtPrev.next();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
            }

            if (annullato) {
                return null;
            }

        }

        //prima di ricalcolare i totali aggiorno le righe
        if (id != null) {
            InvoicexUtil.aggiornaTotaliRighe(acquisto ? Db.TIPO_DOCUMENTO_FATTURA_RICEVUTA : Db.TIPO_DOCUMENTO_FATTURA, id);
        }

        //aggiorno abi, cab e pagamento prendendoli dal cliente
//        if (pagamento.length() == 0) {
        sql = "select pagamento, banca_abi, banca_cab, banca_cc_iban from clie_forn";
        sql += " where codice = " + Db.pc(codiceCliente, "INTEGER");

        try {
            ResultSet cliente = Db.openResultSet(sql);
            cliente.next();
            if (StringUtils.isEmpty(pagamento)) {
                pagamento = cliente.getString("pagamento");
            }
            banca_abi = cliente.getString("banca_abi");
            banca_cab = cliente.getString("banca_cab");
            banca_cc_iban = cliente.getString("banca_cc_iban");
        } catch (Exception err2) {
            System.out.println("convertiInFatture: impossibile trovare il cliente per banca abi e cab");
        }
//        }

        Integer agente = null;
        BigDecimal perc = null;
        try {
            agente = (Integer) DbUtils.getObject(Db.getConn(), "select agente from clie_forn where codice = " + Db.pc(codiceCliente, Types.VARCHAR));
            if (agente != null && agente > 0) {
                perc = (BigDecimal) DbUtils.getObject(Db.getConn(), "select percentuale from agenti where id = " + Db.pc(agente, Types.INTEGER));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //ricaloclo totali
        dbFattura tempFattura = null;
        if (acquisto) {
            tempFattura = new dbFatturaRicevuta();
            tempFattura.serie = newSerie;
            tempFattura.anno = Calendar.getInstance().get(Calendar.YEAR);
            tempFattura.numero = newNumero;
            tempFattura.id = id;
            tempFattura.speseIncassoIva = speseIncasso;
            tempFattura.speseTrasportoIva = speseTrasporto;
            tempFattura.dbRefresh();
            sql = "update " + test_fatt + " set ";
            sql += " imponibile = " + Db.pc(tempFattura.totaleImponibile, "DOUBLE");
            sql += " , iva = " + Db.pc(tempFattura.totaleIva, "DOUBLE");
            sql += " , importo = " + Db.pc(tempFattura.totale, "DOUBLE");
        } else {
            tempFattura = new dbFattura();
            tempFattura.acquisto = acquisto;
            tempFattura.serie = newSerie;
            tempFattura.anno = Calendar.getInstance().get(Calendar.YEAR);
            tempFattura.numero = newNumero;
            tempFattura.id = id;
            tempFattura.speseIncassoIva = speseIncasso;
            tempFattura.speseTrasportoIva = speseTrasporto;
            tempFattura.dbRefresh();
            sql = "update " + test_fatt + " set ";
            sql += " totale_imponibile = " + Db.pc(tempFattura.totaleImponibile, "DOUBLE");
            sql += " , totale_iva = " + Db.pc(tempFattura.totaleIva, "DOUBLE");
            sql += " , totale = " + Db.pc(tempFattura.totale, "DOUBLE");
            sql += " , totale_da_pagare = " + Db.pc(tempFattura.totaleDaPagare, "DOUBLE");
        }
        sql += " , totale_imponibile_pre_sconto = " + Db.pc(tempFattura.totaleImponibilePreSconto, "DOUBLE");
        sql += " , totale_ivato_pre_sconto = " + Db.pc(tempFattura.totaleIvatoPreSconto, "DOUBLE");

        //dati cliente
        if (main.getPersonal().equalsIgnoreCase(main.PERSONAL_CUCINAIN)) {
            sql += " , pagamento = 'BONIFICO'";
        } else {
            sql += " , pagamento = " + Db.pc(pagamento, "VARCHAR");
            try {
                sql += " , note_pagamento = " + Db.pc(CastUtils.toString(DbUtils.getObject(Db.getConn(), "select note_su_documenti from pagamenti where codice = " + Db.pc(pagamento, Types.VARCHAR))), "VARCHAR");
            } catch (Exception e) {
            }
            sql += " , banca_abi = " + Db.pc(banca_abi, "VARCHAR");
            sql += " , banca_cab = " + Db.pc(banca_cab, "VARCHAR");
            sql += " , banca_iban = " + Db.pc(banca_cc_iban, "VARCHAR");
            if (agente_codice == null || agente_codice == 0) {
                if (agente != null && agente > 0) {
                    sql += " , agente_codice = " + Db.pc(agente, "VARCHAR");
                }
                if (perc != null) {
                    sql += " , agente_percentuale = " + Db.pc(perc.doubleValue(), "VARCHAR");
                }
            }
        }

        //***
        sql += " where serie = " + Db.pc(newSerie, "VARCHAR");
        sql += " and anno = " + Db.pc(tempFattura.anno, "INTEGER");
        sql += " and numero = " + Db.pc(newNumero, "LONG");
        Db.executeSql(sql);

        //debug
        System.out.println("sql totale fatt:" + sql);

        //***
        //genero le scadenze in automatico
        if (Db.contain("pagamenti", "codice", Types.VARCHAR, pagamento)) {
            Scadenze tempScad = new Scadenze(tipo_doc, serie, newNumero, Calendar.getInstance().get(Calendar.YEAR), pagamento);
            tempScad.generaScadenze();
        } else {
            Storico.scrivi("DDT a Fattura", "non trovato pagamento");
            SwingUtils.showErrorMessage(main.getPadre(), "Il pagamento '" + pagamento + "' non esiste, impossibile generare le eventuali scadenze di pagamento !");
        }

        //genero le provvigioni
        //rigenero le provvigioni se ancora non sono state pagate
        //Scadenze tempScad = new Scadenze(Db.TIPO_DOCUMENTO_FATTURA, this.texSeri.getText(), this.prev.numero, this.prev.anno, this.comPaga.getText());
        //cercare agente da cliente
        if (agente_codice != null && !acquisto) {
            ProvvigioniFattura provvigioni = new ProvvigioniFattura(tipo_doc, serie, newNumero, Calendar.getInstance().get(Calendar.YEAR), agente_codice, agente_percentuale);
            boolean ret = provvigioni.generaProvvigioni();
            System.out.println("esito genera provvigioni:" + ret + " : " + provvigioni.ret);
        } else {
            try {
                if (perc != null && perc.doubleValue() > 0d && !acquisto) {
                    ProvvigioniFattura provvigioni = new ProvvigioniFattura(tipo_doc, serie, newNumero, DateUtils.getCurrentYear(), agente, perc.doubleValue());
                    boolean ret = provvigioni.generaProvvigioni();
                    System.out.println("esito genera provvigioni:" + ret + " : " + provvigioni.ret);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return (Db.nz(newSerie, "") + " " + newNumero);
    }
    String sqls = null;     //per fare una query sola
    //105

    public boolean azzeraMovimentiMagazzino() {
        return generaMovimentiMagazzino(true);
    }

    public boolean generaMovimentiMagazzino() {
        return generaMovimentiMagazzino(false);
    }

    public boolean generaMovimentiMagazzino(boolean soloAzzeramento) {
        String sql;
        String sqlC;
        String sqlV;
        boolean inseribile;
        ResultSet righe;
        ResultSet testa;

        Object inizio_mysql = Db.getCurrentTimestamp();
                
        System.out.println(this.numero + " / " + this.anno + " " + numero + " / " + anno + " / " + tipoDocumento);

        //elimino eventuali movimenti precedenti derivanti dallo stesso documento        
        sql = "";
        if (this.tipoDocumento.equalsIgnoreCase(Db.TIPO_DOCUMENTO_FATTURA_RICEVUTA)) {
            sql += " where da_tabella = " + dbUtil.pc("test_" + this.getNomeTabellaFinale(tipoDocumento), "VARHCAR");
            sql += " and da_serie = " + dbUtil.pc(serie, "VARHCAR");
            sql += " and da_numero = " + dbUtil.pc(String.valueOf(numero), "LONG");
            sql += " and da_anno = " + Db.pc(anno, "INTEGER");
        } else if (this.tipoDocumento.equalsIgnoreCase(Db.TIPO_DOCUMENTO_FATTURA) || this.tipoDocumento.equalsIgnoreCase(Db.TIPO_DOCUMENTO_SCONTRINO)) {
            if (id == 0) {
                System.out.println("Id 0 su generazione movimenti per " + serie + "/" + numero + "/" + anno);
                SwingUtils.showErrorMessage(main.getPadre(), "Id 0 su generazione movimenti per " + serie + "/" + numero + "/" + anno);
            }
            sql += " where da_tabella = " + dbUtil.pc("test_" + this.getNomeTabellaFinale(tipoDocumento), "VARHCAR");
            sql += " and da_id = " + id;
        } else {
            sql += " where da_tabella = " + dbUtil.pc("test_" + this.getNomeTabellaFinale(tipoDocumento), "VARHCAR");
            sql += " and da_serie = " + dbUtil.pc(serie, "VARHCAR");
            sql += " and da_numero = " + dbUtil.pc(String.valueOf(numero), "LONG");
            sql += " and da_anno = " + Db.pc(anno, "INTEGER");
        }
        //memorizzo gli eliminati
        Magazzino.preDelete(sql);
        //elimino
        sql = "delete from movimenti_magazzino" + sql;
        Db.executeSql(sql);

        if (soloAzzeramento) {
            main.events.fireInvoicexEventMagazzino(this, inizio_mysql);
            return true;
        }

        //seleziono la testata del documento, per prendere la data..
        if (this.tipoDocumento.equalsIgnoreCase(Db.TIPO_DOCUMENTO_FATTURA_RICEVUTA)) {
            sql = "select * from test_fatt_acquisto";
            sql += " where serie = " + dbUtil.pc(serie, "VARHCAR");
            sql += " and numero = " + dbUtil.pc(String.valueOf(numero), "LONG");
            sql += " and anno = " + Db.pc(anno, "INTEGER");
        } else if (this.tipoDocumento.equalsIgnoreCase(Db.TIPO_DOCUMENTO_FATTURA) || this.tipoDocumento.equalsIgnoreCase(Db.TIPO_DOCUMENTO_SCONTRINO)) {
            sql = "select * from test_" + getNomeTabellaFinale(tipoDocumento);
            sql += " where id = " + id;
        } else {
            sql = "select * from test_" + getNomeTabellaFinale(tipoDocumento);
            sql += " where serie = " + dbUtil.pc(serie, "VARHCAR");
            sql += " and numero = " + dbUtil.pc(String.valueOf(numero), "LONG");
            sql += " and anno = " + Db.pc(anno, "INTEGER");
        }
        System.out.println("sql:" + sql);
        testa = Db.openResultSet(sql);

        //seleziono le righe del documento
        if (this.tipoDocumento.equalsIgnoreCase(Db.TIPO_DOCUMENTO_FATTURA) || this.tipoDocumento.equalsIgnoreCase(Db.TIPO_DOCUMENTO_SCONTRINO)) {
            sql = "select r.codice_articolo, r.quantita, art.servizio, r.id, r.riga from righ_" + getNomeTabellaFinale(this.tipoDocumento) + " r";
            sql += " left join articoli art on r.codice_articolo = art.codice";
            sql += " where r.id_padre = " + id;
            sql += " order by r.riga";
            System.out.println("sql:" + sql);
        } else {
            sql = "select r.codice_articolo, r.quantita, art.servizio, r.id, r.riga from righ_" + getNomeTabellaFinale(this.tipoDocumento) + " r";
            sql += " left join articoli art on r.codice_articolo = art.codice";
            sql += " where r.serie = " + dbUtil.pc(this.serie, "VARHCAR");
            sql += " and r.numero = " + dbUtil.pc(String.valueOf(this.numero), "LONG");
            sql += " and r.anno = " + Db.pc(this.anno, "INTEGER");
            sql += " order by r.riga";
            System.out.println("sql:" + sql);
        }

        try {
            if (testa.next()) {
                //controllo che non sia nota di credito o fattura pro-forma
                int tipo_fattura = 0;
                try {
                    if (DbUtils.existColumn(testa, "tipo_fattura")) {
                        tipo_fattura = testa.getInt("tipo_fattura");
                        if (tipo_fattura == dbFattura.TIPO_FATTURA_PROFORMA) {
                            //ignoro le fatture pro forma
                            if (!main.getPersonalContain("movimenti_su_proforma")) {
                                return true;
                            }
                        }
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(dbDocumento.class.getName()).log(Level.SEVERE, null, ex);
                }

                righe = Db.openResultSet(sql);

                //inserisco le righe nei movimenti di magazzino
                //se presente il codice articolo e la qta

                sqls = "";
                sceltaRigaKit(righe, testa, 1.0, "righ_" + getNomeTabellaFinale(tipoDocumento), "");
                if (StringUtils.isNotBlank(sqls)) {
                    Db.executeSql(sqls);
                }

                main.events.fireInvoicexEventMagazzino(this, inizio_mysql);
                
                return true;
            } else {
                return false;
            }
        } catch (Exception err) {
            err.printStackTrace();

            return false;
        }
    }

    public boolean generaMovimentiScontrino() {
        String sql;
        String sqlC;
        String sqlV;
        boolean inseribile;
        ResultSet righe;
        ResultSet testa;

        Object inizio_mysql = Db.getCurrentTimestamp();

        System.out.println(this.numero + " / " + this.anno + " " + numero + " / " + anno + " / " + tipoDocumento);

        //memorizzo gli eliminati
        sql = " where da_tabella = 'test_fatt'";
        sql += " and da_id = " + Db.pc(id, "INTEGER");        
        Magazzino.preDelete(sql);
        //elimino eventuali movimenti precedenti derivanti dallo stesso documento        
        sql = "delete from movimenti_magazzino " + sql;
        Db.executeSql(sql);

        //seleziono la testata del documento, per prendere la data..
        sql = "select * from test_fatt";
        sql += " where id = " + Db.pc(id, "INTEGER");
        System.out.println("sql:" + sql);
        testa = Db.openResultSet(sql);

        //seleziono le righe del documento
        sql = "select r.codice_articolo, r.quantita, art.servizio, r.id, r.riga from righ_" + getNomeTabellaFinale(this.tipoDocumento) + " r";
        sql += " left join articoli art on r.codice_articolo = art.codice";
        sql += " where r.id_padre = " + Db.pc(id, Types.INTEGER);
        sql += " order by r.riga";
        System.out.println("sql:" + sql);

        try {
            testa.next();
            //controllo che non sia nota di credito o fattura pro-forma
            int tipo_fattura = 0;
            try {
                if (DbUtils.existColumn(testa, "tipo_fattura")) {
                    tipo_fattura = testa.getInt("tipo_fattura");
                    if (tipo_fattura == dbFattura.TIPO_FATTURA_NOTA_DI_CREDITO || tipo_fattura == dbFattura.TIPO_FATTURA_PROFORMA) {
                        
                        main.events.fireInvoicexEventMagazzino(this, inizio_mysql);
                        
                        //ignoro le fatture pro forma e note di credito
                        return true;
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(dbDocumento.class.getName()).log(Level.SEVERE, null, ex);
            }

            righe = Db.openResultSet(sql);

            //inserisco le righe nei movimenti di magazzino
            //se presente il codice articolo e la qta
            sqls = "";
            sceltaRigaKit(righe, testa, 1.0, "righ_" + getNomeTabellaFinale(tipoDocumento), "");
            if (StringUtils.isNotBlank(sqls)) {
                Db.executeSql(sqls);
            }

            main.events.fireInvoicexEventMagazzino(this, inizio_mysql);
            
            return true;
        } catch (Exception err) {
            err.printStackTrace();

            return false;
        }
    }

    synchronized private void sceltaRigaKit(ResultSet righe, ResultSet testa, Double quant, String tabella, String pacchetto) throws SQLException {
        try {
            DebugUtils.dumpMem();
            if (articoli == null) {
                articoli = CastUtils.toLong(DbUtils.getObject(Db.getConn(), "select count(*) from articoli"));
                if (articoli >= 25000) {
                    fareCacheArticoli = false;
                }
            }
            if (fareCacheArticoli) {
                cacheArticoli = DbUtils.getListMapKV(Db.getConn(), "select codice, flag_kit from articoli");
            }
            DebugUtils.dumpMem();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String sql = "select id_padre as riga, count(*) from " + getNomeTabellaFinaleLotti(this.tipoDocumento) + " where id_padre in (select id from righ_" + getNomeTabellaFinale(tipoDocumento) + " where id_padre = " + testa.getInt("id") + ") group by id_padre";
            System.err.println("sql:" + sql);
            cacheQtaLotti = DbUtils.getListMapKV(Db.getConn(), sql);
        } catch (Exception e) {
        }

        try {
            if (tipoDocumento.equalsIgnoreCase(Db.TIPO_DOCUMENTO_FATTURA) || tipoDocumento.equalsIgnoreCase(Db.TIPO_DOCUMENTO_SCONTRINO)) {
                if (DbUtils.existColumn(righe, "riga")) {
                    String sql = "select riga, count(*) from " + getNomeTabellaFinaleMatricole(tipoDocumento) + " where id_padre = " + testa.getInt("id") + " group by riga";
                    System.err.println("sql:" + sql);
                    cacheQtaMatricole = DbUtils.getListMapKV(Db.getConn(), sql);
                }
            } else {
                if (DbUtils.existColumn(righe, "riga")) {
                    String sql = "select riga, count(*) from " + getNomeTabellaFinaleMatricole(tipoDocumento) + " where serie = '" + testa.getString("serie") + "' and numero = " + testa.getInt("numero") + " and anno = " + testa.getInt("anno") + " group by riga";
                    System.err.println("sql:" + sql);
                    cacheQtaMatricole = DbUtils.getListMapKV(Db.getConn(), sql);
                }
            }
        } catch (Exception e) {
        }

        while (righe.next()) {
            boolean inseribile = true;

            if (Db.nz(righe.getString("codice_articolo"), "").trim().length() == 0) {
                inseribile = false;
            } else {
                if (righe.getDouble("quantita") == 0) {
                    inseribile = false;
                }
            }
            //non inserisco gli articoli di tipo servizio
            if (CastUtils.toBoolean(righe.getString("servizio")) == true) {
                inseribile = false;
            }

            if (inseribile == true) {
                String codart = righe.getString("codice_articolo");
                String sql = null;
                boolean kit = false;
                if (cacheArticoli != null && cacheArticoli.containsKey(codart)) {
                    if (((String) cacheArticoli.get(codart)).equals("S")) {
                        kit = true;
                    } else {
                        kit = false;
                    }
                } else {
                    sql = "select flag_kit from articoli where codice = " + Db.pc(righe.getString("codice_articolo"), Types.VARCHAR);
                    ResultSet tmp = Db.openResultSet(sql);
                    if (tmp.next()) {
                        if (tmp.getString("flag_kit").equals("S")) {
                            kit = true;
                        }
                    }
                }

                boolean matricole = false;

                if (!kit) {
                    insertRiga(testa, righe, quant);
                } else {
                    //comunque inserisco la riga del kit
                    insertRiga(testa, righe, quant);

                    if (tabella.equals("pacchetti_articoli")) {
                        sql = "select quantita from pacchetti_articoli where ";
                        sql += "articolo = " + Db.pc(righe.getString("codice_articolo"), Types.VARCHAR) + " and pacchetto = " + Db.pc(pacchetto, Types.VARCHAR);

                        System.out.println("sql: " + sql);
                        ResultSet rs = Db.openResultSet(sql);

                        rs.next();

                        quant = quant * rs.getDouble("quantita");
                    } else {
                        quant = righe.getDouble("quantita");
                    }

                    sql = "select articolo as codice_articolo";
                    sql += ", quantita * " + quant + " as quantita";
                    sql += " , servizio";
                    sql += " ," + righe.getInt("id") + " as id";
                    sql += " from pacchetti_articoli p left join articoli a on p.articolo = a.codice";
                    sql += " where pacchetto = " + Db.pc(righe.getString("codice_articolo"), Types.VARCHAR);

                    ResultSet temp = Db.openResultSet(sql);

                    System.out.println("sql: " + sql);
                    sceltaRigaKit(temp, testa, quant, "pacchetti_articoli", Db.nz(righe.getString("codice_articolo"), ""));
                }
            }
        }
        cacheArticoli = null;
        cacheQtaLotti = null;
        cacheQtaMatricole = null;
    }

    private void insertRiga(ResultSet testa, ResultSet righe, Double quant) throws SQLException {

        if (!DbUtils.existColumn(righe, "id")) {
            insertRiga2(testa, righe, quant, null, null);
            return;
        }

        //lotti
        String sqllotti = "select * from " + getNomeTabellaFinaleLotti(this.tipoDocumento) + " where id_padre = " + righe.getInt("id");
        try {
            int contalotti = 0;
            if (cacheQtaLotti != null) {
                contalotti = CastUtils.toInteger0(cacheQtaLotti.get(righe.getInt("id")));
            }
            if (cacheQtaLotti == null || contalotti > 0) {
                ResultSet rlotti = DbUtils.tryOpenResultSet(Db.getConn(), sqllotti);
                contalotti = 0;
                while (rlotti.next()) {
                    contalotti++;
                    DebugUtils.dump(rlotti);
                    insertRiga2(testa, righe, rlotti.getDouble("qta"), rlotti.getString("lotto"), rlotti.getString("matricola"));
                }
            }
            if (contalotti == 0) {
                //testo matricole
                String sqlmatricole = null;
                int contaMatricole = 0;
                try {
                    if (tipoDocumento.equalsIgnoreCase(Db.TIPO_DOCUMENTO_FATTURA) || tipoDocumento.equalsIgnoreCase(Db.TIPO_DOCUMENTO_SCONTRINO)) {
                        if (DbUtils.existColumn(righe, "riga")) {
                            sqlmatricole = "select * from " + getNomeTabellaFinaleMatricole(this.tipoDocumento) + " where id_padre = " + testa.getInt("id") + " and riga = " + righe.getInt("riga");
                        }
                    } else {
                        if (DbUtils.existColumn(righe, "riga")) {
                            sqlmatricole = "select * from " + getNomeTabellaFinaleMatricole(this.tipoDocumento) + " where serie = '" + testa.getString("serie") + "' and numero = " + testa.getInt("numero") + " and anno = " + testa.getInt("anno") + " and riga = " + righe.getInt("riga");
                        }
                    }
                    if (sqlmatricole != null) {
                        if (cacheQtaMatricole != null) {
                            contaMatricole = CastUtils.toInteger0(cacheQtaMatricole.get(righe.getInt("riga")));
                        }
                        if (cacheQtaMatricole == null || contaMatricole > 0) {
                            contaMatricole = 0;
                            ResultSet rMatricole = DbUtils.tryOpenResultSet(Db.getConn(), sqlmatricole);
                            while (rMatricole.next()) {
                                contaMatricole++;
                                DebugUtils.dump(rMatricole);
                                insertRiga2(testa, righe, 1d, null, rMatricole.getString("matricola"));
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (contaMatricole == 0) {
                    insertRiga2(testa, righe, quant, null, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void insertRiga2(ResultSet testa, ResultSet righe, Double quant, String lotto, String matricola) throws SQLException {
        String sqltmp = "select ";

        String sql = "insert into movimenti_magazzino (";
        String sqlC = "data";
        String sqlV = "'" + testa.getString("data") + "'";
        sqlC += ", causale";

        int tipo_fattura = 0;
        try {
            if (DbUtils.existColumn(testa, "tipo_fattura")) {
                tipo_fattura = testa.getInt("tipo_fattura");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (tipoDocumento.equals(Db.TIPO_DOCUMENTO_FATTURA_RICEVUTA)
                || tipo_fattura == dbFattura.TIPO_FATTURA_NOTA_DI_CREDITO
                || tipoDocumento == Db.TIPO_DOCUMENTO_DDT_ACQUISTO) {
            sqlV += ", 2"; //causale per il carico
        } else {
            sqlV += ", 3"; //causale che inserisco io da programma per lo scarico di magazzino
        }
        sqlC += ", deposito";
        sqlV += ", 0"; //deposito standard per ora non lo gestisco
        sqlC += ", articolo";
        sqlV += ", " + Db.pc(righe.getString("codice_articolo"), java.sql.Types.VARCHAR);
        if (matricola == null) {
            sqlC += ", quantita";
            if (lotto == null) {
                Double tot = righe.getDouble("quantita");
                sqlV += ", " + tot;
            } else {
                sqlV += ", " + quant;
            }
        } else {
            sqlC += ", quantita";
            sqlV += ", " + Db.pc(1, java.sql.Types.DECIMAL);
            sqlC += ", matricola";
            sqlV += ", " + Db.pc(matricola, java.sql.Types.VARCHAR);
        }
        sqlC += ", da_tabella";
        sqlV += ", 'test_" + getNomeTabellaFinale(this.tipoDocumento) + "'";
        sqlC += ", da_serie";
        sqlV += ", '" + this.serie + "'";
        sqlC += ", da_numero";
        sqlV += ", " + this.numero;
        sqlC += ", da_anno";
        sqlV += ", " + this.anno;

        sqlC += ", da_tipo_fattura";
        sqlV += ", " + ((tipoFattura == null) ? "null" : tipoFattura);

        sqlC += ", da_id";
        try {
            sqlV += ", " + testa.getInt("id");
        } catch (Exception e) {
            sqlV += ", " + this.id;
        }

        sqlC += ", lotto";
        sqlV += ", " + (lotto == null ? "null" : Db.pc(lotto, Types.VARCHAR));

        sql = sql + sqlC + ") values (" + sqlV + ")";
        System.out.println("sql mov:" + sql);

        sqls += sql + ";\n\n";

    }

    private String getNomeTabellaFinale(String tipo) {
        if (tipo.equalsIgnoreCase(Db.TIPO_DOCUMENTO_DDT)) {
            return ("ddt");
        } else if (tipo.equalsIgnoreCase(Db.TIPO_DOCUMENTO_DDT_ACQUISTO)) {
            return ("ddt_acquisto");
        } else if (tipo.equalsIgnoreCase(Db.TIPO_DOCUMENTO_FATTURA) || tipo.equalsIgnoreCase(Db.TIPO_DOCUMENTO_SCONTRINO)) {
            return ("fatt");
        } else if (tipo.equalsIgnoreCase(Db.TIPO_DOCUMENTO_FATTURA_RICEVUTA)) {
            return ("fatt_acquisto");
        } else {
            javax.swing.JOptionPane.showMessageDialog(null, "Tipo di documento " + tipo + " inesistente", "Errore", javax.swing.JOptionPane.ERROR_MESSAGE);
            return (null);
        }
    }

    private String getNomeTabellaFinaleLotti(String tipo) {
        if (tipo.equalsIgnoreCase(Db.TIPO_DOCUMENTO_DDT)) {
            return ("righ_ddt_lotti");
        } else if (tipo.equalsIgnoreCase(Db.TIPO_DOCUMENTO_DDT_ACQUISTO)) {
//            SwingUtils.showErrorMessage(null, "Errore tabella ddt acquisto LOTTI");
            return ("righ_ddt_acquisto_lotti");
        } else if (tipo.equalsIgnoreCase(Db.TIPO_DOCUMENTO_ORDINE)) {
            return ("righ_ordi_lotti");
        } else if (tipo.equalsIgnoreCase(Db.TIPO_DOCUMENTO_ORDINE_ACQUISTO)) {
            return ("righ_ordi_acquisto_lotti");
        } else if (tipo.equalsIgnoreCase(Db.TIPO_DOCUMENTO_FATTURA) || tipo.equalsIgnoreCase(Db.TIPO_DOCUMENTO_SCONTRINO)) {
            return ("righ_fatt_lotti");
        } else if (tipo.equalsIgnoreCase(Db.TIPO_DOCUMENTO_FATTURA_RICEVUTA)) {
            return ("righ_fatt_acquisto_lotti");
        } else {
            javax.swing.JOptionPane.showMessageDialog(null, "Tipo di documento " + tipo + " inesistente", "Errore", javax.swing.JOptionPane.ERROR_MESSAGE);
            return (null);
        }
    }

    private String getNomeTabellaFinaleMatricole(String tipo) {
        if (tipo.equalsIgnoreCase(Db.TIPO_DOCUMENTO_DDT)) {
            return ("righ_ddt_matricole");
        } else if (tipo.equalsIgnoreCase(Db.TIPO_DOCUMENTO_DDT_ACQUISTO)) {
//            SwingUtils.showErrorMessage(null, "Errore tabella ddt acquisto MATRICOLE");
            return ("righ_ddt_acquisto_matricole");
        } else if (tipo.equalsIgnoreCase(Db.TIPO_DOCUMENTO_FATTURA) || tipo.equalsIgnoreCase(Db.TIPO_DOCUMENTO_SCONTRINO)) {
            return ("righ_fatt_matricole");
        } else if (tipo.equalsIgnoreCase(Db.TIPO_DOCUMENTO_FATTURA_RICEVUTA)) {
            return ("righ_fatt_acquisto_matricole");
        } else {
            javax.swing.JOptionPane.showMessageDialog(null, "Tipo di documento " + tipo + " inesistente", "Errore", javax.swing.JOptionPane.ERROR_MESSAGE);
            return (null);
        }
    }

    private void inserisciRighe(boolean raggr, int i2, String daddt, ResultSet tempPrev, int newNumero, String newStato, String newSerie) {
        inserisciRighe(raggr, i2, daddt, tempPrev, newNumero, newStato, newSerie, null);
    }

    private void inserisciRighe(boolean raggr, int i2, String daddt, ResultSet tempPrev, int newNumero, String newStato, String newSerie, Integer id_padre_originale) {
        String test_fatt = acquisto ? "test_fatt_acquisto" : "test_fatt";
        String test_ddt = acquisto ? "test_ddt_acquisto" : "test_ddt";
        String righ_ddt = acquisto ? "righ_ddt_acquisto" : "righ_ddt";
        String righ_fatt = acquisto ? "righ_fatt_acquisto" : "righ_fatt";
        String tipo_doc = acquisto ? Db.TIPO_DOCUMENTO_FATTURA_RICEVUTA : Db.TIPO_DOCUMENTO_FATTURA;

        int id_padre = -1;
        if (id_padre_originale != null) {
            id_padre = id_padre_originale;
        } else {
            try {
                String sql = "select id from " + test_fatt + " where serie = " + Db.pc(newSerie, Types.VARCHAR);
                sql += " and numero = " + Db.pc(newNumero, Types.INTEGER);
                sql += " and anno = " + Db.pc(Calendar.getInstance().get(Calendar.YEAR), "INTEGER");
                if (test_fatt.equalsIgnoreCase("test_fatt")) {
                    sql += " and IFNULL(tipo_fattura,0) != 7";
                }
                id_padre = CastUtils.toInteger(DbUtils.getObject(Db.getConn(), sql));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            ResultSetMetaData metaPrev = tempPrev.getMetaData();
            //inserisco riga di provenienza
            String sqlC = "";
            String sqlV = "";
            contaRighe++;
            //prendo dati di provenienza
            ResultSet tempDdtProv = null;
            String sql = "";
            if (!raggr) {
                sql = "select serie,numero,data,id from " + test_ddt + " t ";
                sql += elencoDdt.get(i2);
                sql += " order by serie, anno, numero";
                System.out.println("DDT_FATT_SQL>" + sql);
                tempDdtProv = Db.openResultSet(sql);
                tempDdtProv.next();
            }

            ResultSet colonne = Db.openResultSet("select * from " + righ_fatt + " limit 0");
            //inserisco riga di descrzione
            for (int i = 1; i <= metaPrev.getColumnCount(); i++) {
                if (!DbUtils.existColumn(colonne, metaPrev.getColumnName(i))) {
                    continue;
                }
                if (metaPrev.getColumnName(i).equalsIgnoreCase("id")) {
                    continue;
                }
                if (metaPrev.getColumnName(i).equalsIgnoreCase("data")) {
                    continue;
                }
                if (metaPrev.getColumnName(i).equalsIgnoreCase("dataddt")) {
                    continue;
                }
                if (metaPrev.getColumnName(i).equalsIgnoreCase("sqta")) {
                    continue;
                }
                if (metaPrev.getColumnName(i).equalsIgnoreCase("iva_deducibile")) {
                    continue;
                }
                if (metaPrev.getColumnName(i).equalsIgnoreCase("numero")) {
                    sqlC += "numero";
                    sqlV += Db.pc(String.valueOf(newNumero), metaPrev.getColumnType(i));
                } else if (metaPrev.getColumnName(i).equalsIgnoreCase("stato")) {
                    sqlC += "stato";
                    sqlV += Db.pc(newStato, metaPrev.getColumnType(i));
                } else if (metaPrev.getColumnName(i).equalsIgnoreCase("serie")) {
                    sqlC += "serie";
                    sqlV += Db.pc(Db.nz(serie, ""), metaPrev.getColumnType(i));
                } else if (metaPrev.getColumnName(i).equalsIgnoreCase("anno")) {
                    sqlC += "anno";
                    sqlV += Db.pc(Calendar.getInstance().get(Calendar.YEAR), "INTEGER");
                } else if (metaPrev.getColumnName(i).equalsIgnoreCase("id_padre")) {
                    sqlC += "id_padre";
                    sqlV += id_padre;
                } else if (metaPrev.getColumnName(i).equalsIgnoreCase("riga")) {
                    sqlC += "riga";
                    sqlV += Db.pc(String.valueOf(contaRighe), metaPrev.getColumnType(i));
                } else if (metaPrev.getColumnName(i).equalsIgnoreCase("prezzo")) {
                    sqlC += "prezzo";
                    sqlV += Db.pc("0", metaPrev.getColumnType(i));
                } else if (metaPrev.getColumnName(i).equalsIgnoreCase("prezzo_ivato")) {
                    sqlC += "prezzo_ivato";
                    sqlV += Db.pc("0", metaPrev.getColumnType(i));
                } else if (metaPrev.getColumnName(i).equalsIgnoreCase("totale_ivato")) {
                    sqlC += "totale_ivato";
                    sqlV += "0";
                } else if (metaPrev.getColumnName(i).equalsIgnoreCase("totale_imponibile")) {
                    sqlC += "totale_imponibile";
                    sqlV += "0";
                } else if (metaPrev.getColumnName(i).equalsIgnoreCase("descrizione")) {
                    sqlC += "descrizione";
                    if (raggr) {
                        sqlV += Db.pc(daddt, metaPrev.getColumnType(i));
                    } else {
                        sqlV += Db.pc(main.fileIni.getValue("altro", "da_ddt", "*** Da DDT") + " numero " + Db.nz(tempDdtProv.getString("serie"), "") + tempDdtProv.getString("numero") + " del " + sdf.format(tempDdtProv.getDate("data")), metaPrev.getColumnType(i));
                    }
                } else {
                    sqlC += metaPrev.getColumnName(i);
                    sqlV += Db.pc("", metaPrev.getColumnType(i));
                }

                if (i < metaPrev.getColumnCount()) {
                    sqlC += ",";
                    sqlV += ",";
                }
            }

            if (sqlC.endsWith(",")) {
                sqlC = sqlC.substring(0, sqlC.length() - 1);
            }
            if (sqlV.endsWith(",")) {
                sqlV = sqlV.substring(0, sqlV.length() - 1);
            }

            //inserisco ordine di provenienza
            if (!raggr) {
                sqlC += ", da_ddt";
                sqlV += "," + tempDdtProv.getString("id");
            }

            sql = "insert into " + righ_fatt + " ";
            sql += "(" + sqlC + ") values (" + sqlV + ")";

            System.out.println("sql:" + sql);
            Db.executeSql(sql);

            //inserisco le righe sorgenti
            while (tempPrev.next() == true) {
                sqlC = "";
                sqlV = "";
                contaRighe++;

                String virgola = ",";
                for (int i = 1; i <= metaPrev.getColumnCount(); i++) {
                    virgola = ",";

                    if (!DbUtils.existColumn(colonne, metaPrev.getColumnName(i))) {
                        continue;
                    }
                    if (metaPrev.getColumnName(i).equalsIgnoreCase("id")) {
                        continue;
                    }
                    if (metaPrev.getColumnName(i).equalsIgnoreCase("data")) {
                        continue;
                    }
                    if (metaPrev.getColumnName(i).equalsIgnoreCase("dataddt")) {
                        continue;
                    }
                    if (metaPrev.getColumnName(i).equalsIgnoreCase("sqta")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("stotale_imponibile")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("stotale_ivato")) {
                        continue;
                    }

                    if (metaPrev.getColumnName(i).equalsIgnoreCase("numero")) {
                        sqlC += "numero";
                        sqlV += Db.pc(String.valueOf(newNumero), metaPrev.getColumnType(i));
                    } else if (metaPrev.getColumnName(i).equalsIgnoreCase("serie")) {
                        sqlC += "serie";
                        sqlV += Db.pc(newSerie, metaPrev.getColumnType(i));
                    } else if (metaPrev.getColumnName(i).equalsIgnoreCase("stato")) {
                        sqlC += "stato";
                        sqlV += Db.pc(newStato, metaPrev.getColumnType(i));
                    } else if (metaPrev.getColumnName(i).equalsIgnoreCase("riga")) {
                        sqlC += "riga";
                        sqlV += Db.pc(String.valueOf(contaRighe), metaPrev.getColumnType(i));
                    } else if (metaPrev.getColumnName(i).equalsIgnoreCase("anno")) {
                        sqlC += "anno";
                        sqlV += Db.pc(Calendar.getInstance().get(Calendar.YEAR), "INTEGER");
                    } else if (metaPrev.getColumnName(i).equalsIgnoreCase("id_padre")) {
                        sqlC += "id_padre";
                        sqlV += id_padre;
                    } else if (metaPrev.getColumnName(i).equalsIgnoreCase("prezzo")) {
                        sqlC += "prezzo";
                        sqlV += Db.pc(Db.nz(tempPrev.getObject(i), "0"), metaPrev.getColumnType(i));
                    } else if (metaPrev.getColumnName(i).equalsIgnoreCase("prezzo_ivato")) {
                        sqlC += "prezzo_ivato";
                        sqlV += Db.pc(Db.nz(tempPrev.getObject(i), "0"), metaPrev.getColumnType(i));
                    } else if (metaPrev.getColumnName(i).equalsIgnoreCase("quantita")) {
                        sqlC += "quantita";
                        if (raggr) {
                            sqlV += Db.pc(Db.nz(tempPrev.getObject("sqta"), "0"), metaPrev.getColumnType(i));
                        } else {
                            sqlV += Db.pc(Db.nz(tempPrev.getObject("quantita"), "0"), metaPrev.getColumnType(i));
                        }
                    } else if (metaPrev.getColumnName(i).equalsIgnoreCase("totale_imponibile")) {
                        sqlC += "totale_imponibile";
                        if (raggr) {
                            sqlV += Db.pc(Db.nz(tempPrev.getObject("stotale_imponibile"), "0"), metaPrev.getColumnType(i));
                        } else {
                            sqlV += Db.pc(Db.nz(tempPrev.getObject("totale_imponibile"), "0"), metaPrev.getColumnType(i));
                        }
                    } else if (metaPrev.getColumnName(i).equalsIgnoreCase("totale_ivato")) {
                        sqlC += "totale_ivato";
                        if (raggr) {
                            sqlV += Db.pc(Db.nz(tempPrev.getObject("stotale_ivato"), "0"), metaPrev.getColumnType(i));
                        } else {
                            sqlV += Db.pc(Db.nz(tempPrev.getObject("totale_ivato"), "0"), metaPrev.getColumnType(i));
                        }
                    } else if (metaPrev.getColumnName(i).equalsIgnoreCase("quantita_evasa")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("in_ddt")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("in_fatt")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("da_ordi")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("da_ddt")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("in_ddt_riga")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("in_fatt_riga")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("da_ordi_riga")
                            || metaPrev.getColumnName(i).equalsIgnoreCase("da_ddt_riga")) {
                        virgola = "";
                    } else {
                        sqlC += metaPrev.getColumnName(i);
                        sqlV += Db.pc(tempPrev.getObject(i), metaPrev.getColumnType(i));
                    }

                    if (i != metaPrev.getColumnCount()) {
                        sqlC += virgola;
                        sqlV += virgola;
                    }
                }
                if (sqlC.endsWith(",")) {
                    sqlC = sqlC.substring(0, sqlC.length() - 1);
                }
                if (sqlV.endsWith(",")) {
                    sqlV = sqlV.substring(0, sqlV.length() - 1);
                }

                //aggiungo id di provenienza di
                if (!raggr) {
                    sqlC += ", da_ddt, da_ddt_riga";
                    sqlV += ", " + tempDdtProv.getInt("id") + ", " + tempPrev.getInt("id");
                }

                sql = "insert into " + righ_fatt + " ";
                sql += "(" + sqlC + ") values (" + sqlV + ")";

                //debug
                System.out.println("DEBUG:" + sql);
                Db.executeSql(sql);

                //riporto su riga di provenienza l'id della riga generata e l'id di testata
                String campo_in = "in_fatt";
                Integer id_riga = CastUtils.toInteger(DbUtils.getObject(Db.getConn(), "select LAST_INSERT_ID()"));
                sql = "update " + righ_ddt + " set " + campo_in + "_riga = " + id_riga;
                sql += " , " + campo_in + " = " + id_padre;
                sql += " where id = " + tempPrev.getInt("id");
                System.out.println("sql = " + sql);
                Db.executeSql(sql);
            }

        } catch (Exception err) {
            err.printStackTrace();
        }

    }

//    private void inserisciRighePostConferma(boolean raggr, int i2, String daddt, ResultSet tempPrev, int newNumero, String newStato, String newSerie) {
//        String sql;
//        String test_fatt = acquisto ? "test_fatt_acquisto" : "test_fatt";
//        String test_ddt = acquisto ? "test_ddt_acquisto" : "test_ddt";
//        String righ_ddt = acquisto ? "righ_ddt_acquisto" : "righ_ddt";
//        String righ_fatt = acquisto ? "righ_fatt_acquisto" : "righ_fatt";
//        String tipo_doc = acquisto ? Db.TIPO_DOCUMENTO_FATTURA_RICEVUTA : Db.TIPO_DOCUMENTO_FATTURA;
//
//        //metto flag che sono gia' state fatturate
//        if (raggr) {
//            sql = "update " + test_ddt + " t ";
//            sql += " set fattura_serie = " + Db.pc(Db.nz(serie, ""), "VARCHAR");
//            sql += " , fattura_anno = " + Db.pc(Calendar.getInstance().get(Calendar.YEAR), "INTEGER");
//            sql += " , fattura_numero = " + Db.pc(String.valueOf(newNumero), "INTEGER");
//            for (int i = 0; i < elencoDdtR.size(); i++) {
//                if (i == 0) {
//                    sql += " where " + elencoDdtR.get(i);
//                } else {
//                    sql += " or " + elencoDdtR.get(i);
//                }
//            }
//            Db.executeSql(sql);
//        } else {
//            sql = "update " + test_ddt + " t ";
//            sql += " set fattura_serie = " + Db.pc(Db.nz(serie, ""), "VARCHAR");
//            sql += " , fattura_anno = " + Db.pc(Calendar.getInstance().get(Calendar.YEAR), "INTEGER");
//            sql += " , fattura_numero = " + Db.pc(String.valueOf(newNumero), "INTEGER");
//            sql += elencoDdt.get(i2);
//            Db.executeSql(sql);
//        }
//    }
    private void inserisciRigheRiepilogative(int i2, String daddt, ResultSet tempPrev, int newNumero, String newStato, String newSerie) {
        String test_fatt = acquisto ? "test_fatt_acquisto" : "test_fatt";
        String test_ddt = acquisto ? "test_ddt_acquisto" : "test_ddt";
        String righ_ddt = acquisto ? "righ_ddt_acquisto" : "righ_ddt";
        String righ_fatt = acquisto ? "righ_fatt_acquisto" : "righ_fatt";
        String tipo_doc = acquisto ? Db.TIPO_DOCUMENTO_FATTURA_RICEVUTA : Db.TIPO_DOCUMENTO_FATTURA;

        System.out.println("!!! inseriscirigheriepilogative i2:" + i2 + " daddt:" + daddt + " elencoddt:" + elencoDdt.get(i2));
        int id_padre = -1;
        try {
            String sql = "select id from " + test_fatt + " where serie = " + Db.pc(newSerie, Types.VARCHAR);
            sql += " and numero = " + Db.pc(newNumero, Types.INTEGER);
            sql += " and anno = " + Db.pc(Calendar.getInstance().get(Calendar.YEAR), "INTEGER");
            id_padre = CastUtils.toInteger(DbUtils.getObject(Db.getConn(), sql));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ResultSetMetaData metaPrev = tempPrev.getMetaData();
            //inserisco riga di provenienza
            contaRighe++;
            //prendo dati di provenienza
            ResultSet tempDdtProv = null;
            String sql = "";

            sql = "select serie,numero,data,anno, id from " + test_ddt + " t ";
            sql += elencoDdt.get(i2);
            sql += " order by serie, anno, numero";
            System.out.println("DDT_FATT_SQL>" + sql);
            tempDdtProv = Db.openResultSet(sql);
            tempDdtProv.next();

            //prendo il totale del ddt
            Documento doc = new Documento();
            String tipodoc = Db.TIPO_DOCUMENTO_DDT;
            doc.load(Db.INSTANCE, tempDdtProv.getInt("numero"), tempDdtProv.getString("serie"), tempDdtProv.getInt("anno"), tipodoc, tempDdtProv.getInt("id"));
            doc.calcolaTotali();
            doc.visualizzaCastellettoIva();

            ResultSet colonne = Db.openResultSet("select * from " + righ_fatt + " limit 0");

            Vector<DettaglioIva> ive = doc.dettagliIva;
            for (DettaglioIva diva : ive) {
                String sqlC = "";
                String sqlV = "";

                for (int i = 1; i <= metaPrev.getColumnCount(); i++) {
                    if (!DbUtils.existColumn(colonne, metaPrev.getColumnName(i))) {
                        continue;
                    }
                    if (metaPrev.getColumnName(i).equalsIgnoreCase("id")) {
                        continue;
                    }
                    if (metaPrev.getColumnName(i).equalsIgnoreCase("data")) {
                        continue;
                    }
                    if (metaPrev.getColumnName(i).equalsIgnoreCase("dataddt")) {
                        continue;
                    }
                    if (metaPrev.getColumnName(i).equalsIgnoreCase("sqta")) {
                        continue;
                    }
                    if (metaPrev.getColumnName(i).equalsIgnoreCase("iva_deducibile")) {
                        continue;
                    }
                    if (metaPrev.getColumnName(i).equalsIgnoreCase("numero")) {
                        sqlC += "numero";
                        sqlV += Db.pc(String.valueOf(newNumero), metaPrev.getColumnType(i));
                    } else if (metaPrev.getColumnName(i).equalsIgnoreCase("stato")) {
                        sqlC += "stato";
                        sqlV += Db.pc(newStato, metaPrev.getColumnType(i));
                    } else if (metaPrev.getColumnName(i).equalsIgnoreCase("serie")) {
                        sqlC += "serie";
                        sqlV += Db.pc(Db.nz(serie, ""), metaPrev.getColumnType(i));
                    } else if (metaPrev.getColumnName(i).equalsIgnoreCase("anno")) {
                        sqlC += "anno";
                        sqlV += Db.pc(Calendar.getInstance().get(Calendar.YEAR), "INTEGER");
                    } else if (metaPrev.getColumnName(i).equalsIgnoreCase("id_padre")) {
                        sqlC += "id_padre";
                        sqlV += id_padre;
                    } else if (metaPrev.getColumnName(i).equalsIgnoreCase("riga")) {
                        sqlC += "riga";
                        sqlV += Db.pc(String.valueOf(contaRighe), metaPrev.getColumnType(i));
                    } else if (metaPrev.getColumnName(i).equalsIgnoreCase("prezzo")) {
                        sqlC += "prezzo";
                        sqlV += Db.pc(diva.getImponibile(), metaPrev.getColumnType(i));
                    } else if (metaPrev.getColumnName(i).equalsIgnoreCase("prezzo_ivato")) {
                        sqlC += "prezzo_ivato";
                        sqlV += Db.pc(diva.getIvato(), metaPrev.getColumnType(i));
                    } else if (metaPrev.getColumnName(i).equalsIgnoreCase("descrizione")) {
                        sqlC += "descrizione";
                        sqlV += Db.pc(main.fileIni.getValue("altro", "da_ddt", "*** Da DDT") + " numero " + Db.nz(tempDdtProv.getString("serie"), "") + tempDdtProv.getString("numero") + " del " + sdf.format(tempDdtProv.getDate("data")), metaPrev.getColumnType(i));
                    } else if (metaPrev.getColumnName(i).equalsIgnoreCase("iva")) {
                        sqlC += "iva";
                        sqlV += Db.pc(diva.getCodice(), metaPrev.getColumnType(i));
                    } else if (metaPrev.getColumnName(i).equalsIgnoreCase("quantita")) {
                        sqlC += "quantita";
                        sqlV += Db.pc(1, metaPrev.getColumnType(i));
                    } else if (metaPrev.getColumnName(i).equalsIgnoreCase("totale_ivato")) {
                        sqlC += "totale_ivato";
                        sqlV += Db.pc(diva.getImponibile() + diva.getImposta(), metaPrev.getColumnType(i));
                    } else if (metaPrev.getColumnName(i).equalsIgnoreCase("totale_imponibile")) {
                        sqlC += "totale_imponibile";
                        sqlV += Db.pc(diva.getImponibile(), metaPrev.getColumnType(i));
                    } else {
                        sqlC += metaPrev.getColumnName(i);
                        sqlV += Db.pc("", metaPrev.getColumnType(i));
                    }

                    if (i < metaPrev.getColumnCount()) {
                        sqlC += ",";
                        sqlV += ",";
                    }
                }

                if (sqlC.endsWith(",")) {
                    sqlC = sqlC.substring(0, sqlC.length() - 1);
                }
                if (sqlV.endsWith(",")) {
                    sqlV = sqlV.substring(0, sqlV.length() - 1);
                }
                sql = "insert into " + righ_fatt + " ";
                sql += "(" + sqlC + ") values (" + sqlV + ")";

                System.out.println("sql:" + sql);
                Db.executeSql(sql);

            }
        } catch (Exception err) {
            err.printStackTrace();
        }

    }
//    private void inserisciRigheRiepilogativePostConferma(int i2, String daddt, ResultSet tempPrev, int newNumero, String newStato, String newSerie) {
//        String sql = "";
//        String test_fatt = acquisto ? "test_fatt_acquisto" : "test_fatt";
//        String test_ddt = acquisto ? "test_ddt_acquisto" : "test_ddt";
//        String righ_ddt = acquisto ? "righ_ddt_acquisto" : "righ_ddt";
//        String righ_fatt = acquisto ? "righ_fatt_acquisto" : "righ_fatt";
//        String tipo_doc = acquisto ? Db.TIPO_DOCUMENTO_FATTURA_RICEVUTA : Db.TIPO_DOCUMENTO_FATTURA;
//        //metto flag che sono gia' state fatturate
//        sql = "update " + test_ddt + " t ";
//        sql += " set fattura_serie = " + Db.pc(Db.nz(serie, ""), "VARCHAR");
//        sql += " , fattura_anno = " + Db.pc(Calendar.getInstance().get(Calendar.YEAR), "INTEGER");
//        sql += " , fattura_numero = " + Db.pc(String.valueOf(newNumero), "INTEGER");
//        sql += elencoDdt.get(i2);
//        Db.executeSql(sql);
//    }
}
