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
package reports;

import gestioneFatture.*;
import gestioneFatture.logic.clienti.Cliente;
import gestioneFatture.logic.documenti.*;
import it.tnx.Util;
import it.tnx.commons.CastUtils;
import it.tnx.commons.DbUtils;
import it.tnx.commons.FormatUtils;
import it.tnx.invoicex.InvoicexUtil;
import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.prefs.Preferences;
import org.apache.commons.lang.StringUtils;

/**
 *
 *
 *
 * @author  marco
 *
 */
public class JRDSOrdine extends JRDSBase implements net.sf.jasperreports.engine.JRDataSource {

    private int conta = 0;
    String sql = "";
    ResultSet rDocu = null;
    ResultSet rCliente = null;
    private Documento doc;
    DettaglioIva diva = null;
    String serie = "";
    int numero = 1;
    int anno = 2004;
    Integer id = null;
    String banca_sede;
    String banca_solo_sede;
    String banca_agenzia;
    String banca_iban;
    String banca_SWIFT;
    String tempiDiConsegna;
    //iva
    String iva_codice_1 = "";
    String iva_desc_1 = "";
    String iva_imp_1 = "";
    String iva_perc_1 = "";
    String iva_imposta_1 = "";
    String iva_codice_2 = "";
    String iva_desc_2 = "";
    String iva_imp_2 = "";
    String iva_perc_2 = "";
    String iva_imposta_2 = "";
    String iva_codice_3 = "";
    String iva_desc_3 = "";
    String iva_imp_3 = "";
    String iva_perc_3 = "";
    String iva_imposta_3 = "";
    String iva_codice_4 = "";
    String iva_desc_4 = "";
    String iva_imp_4 = "";
    String iva_perc_4 = "";
    String iva_imposta_4 = "";
    String scadenze = "";
    Vector scadenze_date = new Vector();
    Vector scadenze_importi = new Vector();
    String intestazione1 = "";
    String intestazione2 = "";
    String intestazione3 = "";
    String intestazione4 = "";
    String intestazione5 = "";
    String intestazione6 = "";
    String etichettaCliente = "";
    String etichettaDestinazione = "";
    String etichettaCliente_eng = "";
    String etichettaDestinazione_eng = "";    
    Preferences preferences = Preferences.userNodeForPackage(main.class);
    boolean stampa_dest_div = false;
    String ragione_sociale_1 = "";
    String indirizzo_1 = "";
    String cap_loc_prov_1 = "";
    String piva_cfiscale_desc_1 = "";
    String recapito_1 = "";
    String ragione_sociale_2 = "";
    String indirizzo_2 = "";
    String cap_loc_prov_2 = "";
    String piva_cfiscale_desc_2 = "";
    String piva_cfiscale_desc_2_sotto = "";
    String recapito_2 = "";
    String recapito_2_sotto = "";
    String email_2 = "";
    public Integer codiceCliente = null;
    boolean italian = true;
    boolean perEmail = false;
    String riferimento = "";
    String consegna = "";
    public String nomeClienteFile;
    String tipodoc = Db.TIPO_DOCUMENTO_ORDINE;
    boolean acquisto = false;
    String tabt = "test_ordi";
    String tabr = "righ_ordi";
    String ccliente = "cliente";
    String notePiede = "";
    boolean stampaInvoicexRiga = true;
    boolean prezzi_ivati = false;

    /** Creates a new instance of JRDSInvoice */
    public JRDSOrdine(Connection conn, String serie, int numero, int anno, boolean perEmail, boolean acquisto, Integer id) {
        this.serie = serie;
        this.numero = numero;
        this.anno = anno;
        this.id = id;
        this.perEmail = perEmail;
        doc = new Documento();
        this.acquisto = acquisto;
        if (acquisto) {
            tipodoc = Db.TIPO_DOCUMENTO_ORDINE_ACQUISTO;
            tabt = "test_ordi_acquisto";
            tabr = "righ_ordi_acquisto";
            ccliente = "fornitore";
        }
        doc.load(Db.INSTANCE, numero, serie, anno, tipodoc, id);
        doc.calcolaTotali();
        doc.visualizzaCastellettoIva();


        int ivaSize = doc.dettagliIva.size();

        if (ivaSize > 4) {
            javax.swing.JOptionPane.showMessageDialog(null, "Ci sono piu' di 4 tipi di iva ma ne verranno stampati solo 4 !");
        }

        if (doc.dettagliIva.size() > 0) {
            diva = (DettaglioIva) doc.dettagliIva.get(0);
            iva_codice_1 = diva.getCodice();
            iva_desc_1 = diva.getDescrizione();
            iva_imp_1 = it.tnx.Util.formatValutaEuro(diva.getImponibile());
            iva_perc_1 = it.tnx.Util.formatNumero0Decimali(diva.getPercentuale());
            iva_imposta_1 = it.tnx.Util.formatValutaEuro(diva.getImposta());
        }

        if (ivaSize >= 2) {
            diva = (DettaglioIva) doc.dettagliIva.get(1);
            iva_codice_2 = diva.getCodice();
            iva_desc_2 = diva.getDescrizione();
            iva_imp_2 = it.tnx.Util.formatValutaEuro(diva.getImponibile());
            iva_perc_2 = it.tnx.Util.formatNumero0Decimali(diva.getPercentuale());
            iva_imposta_2 = it.tnx.Util.formatValutaEuro(diva.getImposta());
        }

        if (ivaSize >= 3) {
            diva = (DettaglioIva) doc.dettagliIva.get(2);
            iva_codice_3 = diva.getCodice();
            iva_desc_3 = diva.getDescrizione();
            iva_imp_3 = it.tnx.Util.formatValutaEuro(diva.getImponibile());
            iva_perc_3 = it.tnx.Util.formatNumero0Decimali(diva.getPercentuale());
            iva_imposta_3 = it.tnx.Util.formatValutaEuro(diva.getImposta());
        }

        if (ivaSize >= 4) {
            diva = (DettaglioIva) doc.dettagliIva.get(3);
            iva_codice_4 = diva.getCodice();
            iva_desc_4 = diva.getDescrizione();
            iva_imp_4 = it.tnx.Util.formatValutaEuro(diva.getImponibile());
            iva_perc_4 = it.tnx.Util.formatNumero0Decimali(diva.getPercentuale());
            iva_imposta_4 = it.tnx.Util.formatValutaEuro(diva.getImposta());
        }

        sql = "select t.*,r.*,clie_forn.*,clie_forn.codice as codice_cliente2, clie_forn_dest.*,agenti.nome as agente_nome \n" 
                + " , articoli.codice_a_barre \n"
                + " , articoli.codice_fornitore \n"
                + " , articoli.immagine1 \n"                
                + " from((" + tabt + " t left join " + tabr + " r on t.serie = r.serie and t.numero = r.numero and r.anno = " + anno + " ) \n" 
                + " left join clie_forn on t." + ccliente + " = clie_forn.codice) \n" 
                + " left join clie_forn_dest on t." + ccliente + " = clie_forn_dest.codice_cliente and t." + ccliente + "_destinazione = clie_forn_dest.codice \n"
                + " left join agenti on t.agente_codice = agenti.id \n" 
                + " left join articoli on r.codice_articolo = articoli.codice \n"
                + " where t.serie = " + Db.pc(serie, Types.VARCHAR) + " and t.numero = " + numero + " and t.anno = " + anno + " \n"
                + " order by r.riga";

        //debug
        System.out.println("jasper sql:" + sql);

        try {
            Statement stat = conn.createStatement();
            rDocu = stat.executeQuery(sql);

            Statement statCliente = conn.createStatement();

            //trovo dati banca
            banca_sede = "";
            banca_solo_sede = "";
            banca_agenzia = "";
            banca_iban = "";
            rDocu.next();

            Preferences preferences = java.util.prefs.Preferences.userNodeForPackage(main.class);
            if (main.fileIni.getValueBoolean("pref", "soloItaliano", true)) {
                italian = true;
            } else {
                codiceCliente = rDocu.getInt(ccliente);
                Cliente cliente = new Cliente(codiceCliente);
                italian = cliente.isItalian();
            }

            String prezzi_ivati_s = CastUtils.toString(rDocu.getString("prezzi_ivati"));
            if (prezzi_ivati_s.equalsIgnoreCase("S")) {
                prezzi_ivati = true;
            }

            //seleziono i dati del cliente
            rCliente = statCliente.executeQuery("select * from clie_forn where codice = " + rDocu.getInt(ccliente));
            rCliente.next();
            nomeClienteFile = rCliente.getString("ragione_sociale");

            if (StringUtils.defaultString(rDocu.getString("t.banca_abi")).length() > 0) {
                banca_sede += "ABI " + rDocu.getString("t.banca_abi");
                banca_solo_sede = "";
                banca_agenzia += "CAB " + rDocu.getString("t.banca_cab");

                String sql = "";
                sql += "select *";
                sql += " from banche_abi";
                sql += " where abi = " + Db.pc(rDocu.getString("t.banca_abi"), "VARCHAR");

                try {

                    Statement sAbi = conn.createStatement();
                    ResultSet rAbi = sAbi.executeQuery(sql);

                    if (rAbi.next()) {
                        banca_sede += " - " + rAbi.getString(2);
                        banca_solo_sede += rAbi.getString(2);
                    }
                } catch (Exception err) {
                    err.printStackTrace();
                }

                sql = "";
                sql += "select banche_cab.cap,";
                sql += " banche_cab.indirizzo,";
                sql += " comuni.comune,";
                sql += " comuni.provincia";
                sql += " from banche_cab left join comuni on banche_cab.codice_comune = comuni.codice";
                sql += " where banche_cab.abi = " + Db.pc(rDocu.getString("t.banca_abi"), "VARCHAR");
                sql += " and banche_cab.cab = " + Db.pc(rDocu.getString("t.banca_cab"), "VARCHAR");

                try {

                    Statement sCab = conn.createStatement();
                    ResultSet rCab = sCab.executeQuery(sql);

                    if (rCab.next()) {
                        banca_agenzia += " - Fil. " + Db.nz(rCab.getString(1), "") + " " + Db.nz(rCab.getString(2), "") + ", " + Db.nz(rCab.getString(3), "") + " (" + Db.nz(rCab.getString(4), "") + ")";
                    }
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

            if (rDocu.getString("t.banca_iban") != null && rDocu.getString("t.banca_iban").length() > 0) {
                banca_iban = "IBAN " + rDocu.getString("t.banca_iban");
            }

            //dati per dest diversa
            if (StringUtils.isNotBlank(rDocu.getString("dest_ragione_sociale")) || StringUtils.isNotBlank(rDocu.getString("dest_indirizzo"))) {
                stampa_dest_div = true;
            } else {
                stampa_dest_div = false;
            }
            if (main.fileIni.getValueBoolean("pref", "stampaDestDiversaSotto", false) && stampa_dest_div) {
                ragione_sociale_1 = rCliente.getString("ragione_sociale");
                indirizzo_1 = rCliente.getString("indirizzo");
                String capLocProv = "";
                capLocProv += it.tnx.Db.nz(rCliente.getString("cap"), "");
                if (capLocProv.length() > 0) {
                    capLocProv += " ";
                }
                capLocProv += it.tnx.Db.nz(rCliente.getString("localita"), "");
                if (it.tnx.Db.nz(rCliente.getString("provincia"), "").length() > 0) {
                    capLocProv += " (" + it.tnx.Db.nz(rCliente.getString("provincia"), "") + ")";
                }
                cap_loc_prov_1 = capLocProv;
                piva_cfiscale_desc_1 = "P.IVA " + rCliente.getString("piva_cfiscale") + " Cod. Fisc. " + rCliente.getString("cfiscale");
                try {
                    recapito_1 = Db.lookUp(rCliente.getString("paese"), "codice1", "stati").getString("nome");
                } catch (Exception ex) {
                    recapito_1 = "";
                }
                if ("ITALY".equals(recapito_1)) {
                    recapito_1 = "";
                }
                recapito_1 = InvoicexUtil.aggiungi_recapiti(recapito_1, false, rCliente, rDocu);

                ragione_sociale_2 = rDocu.getString("dest_ragione_sociale");
                indirizzo_2 = rDocu.getString("dest_indirizzo");
                capLocProv = "";
                capLocProv += it.tnx.Db.nz(rDocu.getString("dest_cap"), "");
                if (capLocProv.length() > 0) {
                    capLocProv += " ";
                }
                capLocProv += it.tnx.Db.nz(rDocu.getString("dest_localita"), "");
                if (it.tnx.Db.nz(rDocu.getString("dest_provincia"), "").length() > 0) {
                    capLocProv += " (" + it.tnx.Db.nz(rDocu.getString("dest_provincia"), "") + ")";
                }
                cap_loc_prov_2 = capLocProv;
                piva_cfiscale_desc_2 = "";
                try {
                    recapito_2 = Db.lookUp(rDocu.getString("dest_paese"), "codice1", "stati").getString("nome");
                } catch (Exception ex) {
                    recapito_2 = "";
                }
                if ("ITALY".equals(recapito_2)) {
                    recapito_2 = "";
                }
                recapito_2 = InvoicexUtil.aggiungi_recapiti(recapito_2, true, rCliente, rDocu);
                recapito_2_sotto = recapito_2;
                email_2 = CastUtils.toString(rDocu.getString("email"));
                if (email_2.equals("")) {
                    email_2 = "";
                } else {
                    email_2 = "<br>Email: " + email_2;
                }
            } else {
                ragione_sociale_2 = rCliente.getString("ragione_sociale");
                indirizzo_2 = rCliente.getString("indirizzo");
                String capLocProv = "";
                capLocProv += it.tnx.Db.nz(rCliente.getString("cap"), "");
                if (capLocProv.length() > 0) {
                    capLocProv += " ";
                }
                capLocProv += it.tnx.Db.nz(rCliente.getString("localita"), "");
                if (it.tnx.Db.nz(rCliente.getString("provincia"), "").length() > 0) {
                    capLocProv += " (" + it.tnx.Db.nz(rCliente.getString("provincia"), "") + ")";
                }
                cap_loc_prov_2 = capLocProv;

                boolean italian = true;
                String piva_lbl = "";
                String cfisc_lbl = "";
                String piva_txt = rCliente.getString("piva_cfiscale");
                String cfisc_txt = rCliente.getString("cfiscale");
                if (italian) {
                    piva_lbl = "P.IVA";
                    cfisc_lbl = "Cod. Fisc.";
                } else {
                    piva_lbl = "Vat no.";
                    cfisc_lbl = "Vat Code";
                }
                piva_cfiscale_desc_2 = "";
                if (!StringUtils.isEmpty(piva_txt)) {
                    piva_cfiscale_desc_2 += piva_lbl + " " + piva_txt;
                }
                if (!StringUtils.isEmpty(cfisc_txt)) {
                    piva_cfiscale_desc_2 += (StringUtils.isBlank(piva_txt) ? "" : " ") + cfisc_lbl + " " + cfisc_txt;
                }
                piva_cfiscale_desc_2_sotto = piva_cfiscale_desc_2;

                try {
                    recapito_2 = Db.lookUp(rCliente.getString("paese"), "codice1", "stati").getString("nome");
                } catch (Exception ex) {
                    recapito_2 = "";
                }
                if ("ITALY".equals(recapito_2)) {
                    recapito_2 = "";
                }
                recapito_2 = InvoicexUtil.aggiungi_recapiti(recapito_2, false, rCliente, rDocu);
                recapito_2_sotto = recapito_2;
                email_2 = CastUtils.toString(rCliente.getString("email"));
                if (email_2.equals("")) {
                    email_2 = "";
                } else {
                    email_2 = "<br>Email: " + email_2;
                }

                ragione_sociale_1 = rDocu.getString("dest_ragione_sociale");
                indirizzo_1 = rDocu.getString("dest_indirizzo");
                capLocProv = "";
                capLocProv += it.tnx.Db.nz(rDocu.getString("dest_cap"), "");
                if (capLocProv.length() > 0) {
                    capLocProv += " ";
                }
                capLocProv += it.tnx.Db.nz(rDocu.getString("dest_localita"), "");
                if (it.tnx.Db.nz(rDocu.getString("dest_provincia"), "").length() > 0) {
                    capLocProv += " (" + it.tnx.Db.nz(rDocu.getString("dest_provincia"), "") + ")";
                }
                cap_loc_prov_1 = capLocProv;
                piva_cfiscale_desc_1 = "";
                try {
                    recapito_1 = Db.lookUp(rDocu.getString("dest_paese"), "codice1", "stati").getString("nome");
                } catch (Exception ex) {
                    recapito_1 = "";
                }
                if ("ITALY".equals(recapito_1)) {
                    recapito_1 = "";
                }
                recapito_1 = InvoicexUtil.aggiungi_recapiti(recapito_1, true, rCliente, rDocu);
            }
            if (!StringUtils.isBlank(recapito_2)) {
                String temp = piva_cfiscale_desc_2;
                piva_cfiscale_desc_2 = recapito_2;
                recapito_2 = temp;
            }
            if (!StringUtils.isBlank(recapito_1)) {
                String temp = piva_cfiscale_desc_1;
                piva_cfiscale_desc_1 = recapito_1;
                recapito_1 = temp;
            }

//            ragione_sociale_2 = rCliente.getString("ragione_sociale");
//            indirizzo_2 = rCliente.getString("indirizzo");
//            String capLocProv = "";
//            capLocProv += gestioneFatture.Db.nz(rCliente.getString("cap"), "");
//            if (capLocProv.length() > 0) {
//                capLocProv += " ";
//            }
//            capLocProv += gestioneFatture.Db.nz(rCliente.getString("localita"), "");
//            if (gestioneFatture.Db.nz(rCliente.getString("provincia"), "").length() > 0) {
//                capLocProv += " (" + gestioneFatture.Db.nz(rCliente.getString("provincia"), "") + ")";
//            }
//            cap_loc_prov_2 = capLocProv;
//            if (!StringUtils.isEmpty(rCliente.getString("piva_cfiscale")) && !StringUtils.isEmpty(rCliente.getString("cfiscale"))) {
//                piva_cfiscale_desc_2 = "P.IVA " + rCliente.getString("piva_cfiscale") + " Cod. Fiscale " + rCliente.getString("cfiscale");
//            } else if (StringUtils.isEmpty(rCliente.getString("piva_cfiscale")) && !StringUtils.isEmpty(rCliente.getString("cfiscale"))) {
//                piva_cfiscale_desc_2 = "Cod. Fiscale " + rCliente.getString("cfiscale");
//            } else if (!StringUtils.isEmpty(rCliente.getString("piva_cfiscale")) && StringUtils.isEmpty(rCliente.getString("cfiscale"))) {
//                piva_cfiscale_desc_2 = "P.IVA " + rCliente.getString("piva_cfiscale");
//            }
//
//            ragione_sociale_1 = rDocu.getString("dest_ragione_sociale");
//            indirizzo_1 = rDocu.getString("dest_indirizzo");
//            capLocProv = "";
//            capLocProv += gestioneFatture.Db.nz(rDocu.getString("dest_cap"), "");
//            if (capLocProv.length() > 0) {
//                capLocProv += " ";
//            }
//            capLocProv += gestioneFatture.Db.nz(rDocu.getString("dest_localita"), "");
//            if (gestioneFatture.Db.nz(rDocu.getString("dest_provincia"), "").length() > 0) {
//                capLocProv += " (" + gestioneFatture.Db.nz(rDocu.getString("dest_provincia"), "") + ")";
//            }
//            cap_loc_prov_1 = capLocProv;
//            piva_cfiscale_desc_1 = "";
            //--------------------------

            rDocu.previous();
        } catch (Exception err) {
            err.printStackTrace();
        }

        //seleziono le scadenze
        sql = "" + "select *" + " from scadenze" + " "
                + "where documento_serie = " + Db.pc(serie, Types.VARCHAR) + " "
                + "and documento_numero = " + numero + " "
                + "and documento_anno = " + anno + " "
                + "and documento_tipo = 'OR' "
                + "order by data_scadenza";
        try {
            Statement sScad = conn.createStatement();
            ResultSet rScad = sScad.executeQuery(sql);
            int contaScad = 0;
            while (rScad.next()) {
                contaScad++;
                scadenze_date.add(it.tnx.Util.formatDataItalian(rScad.getDate("data_scadenza")));
                scadenze_importi.add(it.tnx.Util.EURO + "  " + it.tnx.Util.format2Decimali(rScad.getDouble("importo")));
            }
//            if (contaScad > 7) {
//                javax.swing.JOptionPane.showMessageDialog(null, "Le scadenze sono " + contaScad + " mentre in stampa ne entrano al massimo 7");
//            }
        } catch (Exception err) {
            err.printStackTrace();
        }


        //carico dati intestazione

        try {
            boolean daforn = false;
            String sqlForn = "select clie_forn.*, fornitore from clie_forn join " + tabt + " t on clie_forn.codice = t.fornitore";
            sqlForn += " where t.serie = '" + this.serie + "' and t.anno = " + this.anno + " and t.numero = " + this.numero;
            ResultSet rsForn = Db.openResultSet(sqlForn);
            if (rsForn.next() && !acquisto) {
                daforn = true;
            }
            if (daforn && rsForn.getInt("fornitore") > 0) {
                Vector v = new Vector();
                String tempInt = "";
                if (!rsForn.getString("ragione_sociale").equals("")) {
                    tempInt = rsForn.getString("ragione_sociale"); // INTESTAZIONE 1
                    v.add(tempInt);
                }

                if (!rsForn.getString("indirizzo").equals("")) {
                    tempInt = rsForn.getString("indirizzo"); // INTESTAZIONE 2
                    v.add(tempInt);
                }

                if (!rsForn.getString("localita").equals("")) {
                    tempInt = rsForn.getString("localita"); // INTESTAZIONE 3 - PARTE 1
                }

                if (!rsForn.getString("cap").equals("")) {
                    tempInt += " (" + rsForn.getString("cap") + ")"; // INTESTAZIONE 3 - PARTE 2
                }

                if (!rsForn.getString("provincia").equals("")) {
                    tempInt += " " + rsForn.getString("provincia"); // INTESTAZIONE 3 - PARTE 3
                }

                if (!tempInt.equals("")) {
                    v.add(tempInt); // INTESTAZIONE 3 - INSERIMENTO!
                }
                Vector intestazioneTmp = new Vector();
                if (!rsForn.getString("piva_cfiscale").equals("")) {
                    intestazioneTmp.add("PI: " + rsForn.getString("piva_cfiscale")); // INTESTAZIONE 4
                }

                if (!rsForn.getString("piva_cfiscale").equals("")) {
                    intestazioneTmp.add("CF: " + rsForn.getString("cfiscale")); // INTESTAZIONE 4
                }

                if (!tempInt.equals("")) {
                    v.add(StringUtils.join(intestazioneTmp, " - "));
                }

                intestazioneTmp.clear();
                if (!rsForn.getString("telefono").equals("")) {
                    intestazioneTmp.add("Tel. " + rsForn.getString("telefono")); // INTESTAZIONE 5 - PARTE 1
                }

                if (!rsForn.getString("cellulare").equals("")) {
                    intestazioneTmp.add("Cell. " + rsForn.getString("cellulare")); // INTESTAZIONE 5 - PARTE 2
                }

                if (!rsForn.getString("fax").equals("")) {
                    intestazioneTmp.add("Fax. " + rsForn.getString("fax")); // INTESTAZIONE 5 - PARTE 3
                }

                if (!tempInt.equals("")) {
                    v.add(StringUtils.join(intestazioneTmp, " - "));
                }

                intestazioneTmp.clear();
                if (!rsForn.getString("email").equals("")) {
                    intestazioneTmp.add(rsForn.getString("email")); // INTESTAZIONE 6 - PARTE 1
                }

                if (!rsForn.getString("web").equals("")) {
                    intestazioneTmp.add(rsForn.getString("web")); // INTESTAZIONE 6 - PARTE 2
                }

                if (!tempInt.equals("")) {
                    v.add(StringUtils.join(intestazioneTmp, " - "));
                }

                try {
                    intestazione1 = (String) v.get(0);
                    intestazione2 = (String) v.get(1);
                    intestazione3 = (String) v.get(2);
                    intestazione4 = (String) v.get(3);
                    intestazione5 = (String) v.get(4);
                    intestazione6 = (String) v.get(5);
//                    etichettaCliente = "Fornitore";
//                    etichettaDestinazione = "Destinazione Merce";
                } catch (Exception e) {
                }

                rsForn.close();

                try {
                    Statement sDatiAzienda = conn.createStatement();
                    ResultSet rDatiAzienda = sDatiAzienda.executeQuery("select " + main.campiDatiAzienda + " from dati_azienda");
                    if (rDatiAzienda.next()) {
                        etichettaCliente = rDatiAzienda.getString("label_cliente");
                        etichettaDestinazione = rDatiAzienda.getString("label_destinazione");
                        etichettaCliente_eng = rDatiAzienda.getString("label_cliente_eng");
                        etichettaDestinazione_eng = rDatiAzienda.getString("label_destinazione_eng");
                        String nomeCampoNotePiede = acquisto ? "testo_piede_ordi_a" : "testo_piede_ordi_v";
                        notePiede = rDatiAzienda.getString(nomeCampoNotePiede);
                        stampaInvoicexRiga = rDatiAzienda.getInt("stampa_riga_invoicex") == 1;
                    }
                    rDatiAzienda.close();
                    sDatiAzienda.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Statement sDatiAzienda = conn.createStatement();
                ResultSet rDatiAzienda = sDatiAzienda.executeQuery("select " + main.campiDatiAzienda + " from dati_azienda");
                if (rDatiAzienda.next()) {
                    intestazione1 = rDatiAzienda.getString("intestazione_riga1");
                    intestazione2 = rDatiAzienda.getString("intestazione_riga2");
                    intestazione3 = rDatiAzienda.getString("intestazione_riga3");
                    intestazione4 = rDatiAzienda.getString("intestazione_riga4");
                    intestazione5 = rDatiAzienda.getString("intestazione_riga5");
                    intestazione6 = rDatiAzienda.getString("intestazione_riga6");
                    etichettaCliente = rDatiAzienda.getString("label_cliente");
                    etichettaDestinazione = rDatiAzienda.getString("label_destinazione");
                    etichettaCliente_eng = rDatiAzienda.getString("label_cliente_eng");
                    etichettaDestinazione_eng = rDatiAzienda.getString("label_destinazione_eng");
                    String nomeCampoNotePiede = acquisto ? "testo_piede_ordi_a" : "testo_piede_ordi_v";
                    notePiede = rDatiAzienda.getString(nomeCampoNotePiede);
                    stampaInvoicexRiga = rDatiAzienda.getInt("stampa_riga_invoicex") == 1;
                }
                rDatiAzienda.close();
                sDatiAzienda.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Object getFieldValue(net.sf.jasperreports.engine.JRField jRField)
            throws net.sf.jasperreports.engine.JRException {

        if (jRField.getName() != null && jRField.getName().equals("data_consegna_prevista")) {
            System.out.println("debug");
        }
        
        try {

            // dest diversa
            if (jRField.getName().equalsIgnoreCase("ragione_sociale1")) {
                return ragione_sociale_1;
            } else if (jRField.getName().equalsIgnoreCase("indirizzo1")) {
                return indirizzo_1;
            } else if (jRField.getName().equalsIgnoreCase("cap_loc_prov1")) {
                return cap_loc_prov_1;
            } else if (jRField.getName().equalsIgnoreCase("piva_cfiscale_desc1")) {
                return piva_cfiscale_desc_1;
            } else if (jRField.getName().equalsIgnoreCase("recapito1")) {
                return recapito_1;
            }

            if (jRField.getName().equalsIgnoreCase("ragione_sociale2")) {
                return ragione_sociale_2;
            } else if (jRField.getName().equalsIgnoreCase("indirizzo2")) {
                return indirizzo_2;
            } else if (jRField.getName().equalsIgnoreCase("cap_loc_prov2")) {
                return cap_loc_prov_2;
            } else if (jRField.getName().equalsIgnoreCase("piva_cfiscale_desc2")) {
                return piva_cfiscale_desc_2;
            } else if (jRField.getName().equalsIgnoreCase("piva_cfiscale_desc_2_sotto")) {
                if (recapito_2_sotto.length() > 0 && !recapito_2_sotto.endsWith("<br>") && !main.fileIni.getValueBoolean("pref", "stampaPivaSotto", false)) {
                    return "<br>" + piva_cfiscale_desc_2_sotto;
                } else {
                    return piva_cfiscale_desc_2_sotto;
                }
            } else if (jRField.getName().equalsIgnoreCase("recapito2")) {
                return recapito_2;
            } else if (jRField.getName().equalsIgnoreCase("recapito_2_sotto")) {
                return recapito_2_sotto;
            } else if(jRField.getName().equalsIgnoreCase("email_2")) {
                return email_2;
            }

            if (jRField.getName().equalsIgnoreCase("stampa_dest_div")) {
                return stampa_dest_div;
            } else if (jRField.getName().equalsIgnoreCase("etichetta_int1")) {
                if (main.fileIni.getValueBoolean("pref", "stampaDestDiversaSotto", false) && stampa_dest_div) {
                    if (italian) {
                    return acquisto ? "Forntiore" : etichettaCliente;
                } else {
                        return "Dear";
                    }
                } else {
                    if (italian) {
                    return etichettaDestinazione;

                    } else {
                        return "Destination";
                    }
                }
            } else if (jRField.getName().equalsIgnoreCase("etichetta_int2")) {
                if (main.fileIni.getValueBoolean("pref", "stampaDestDiversaSotto", false) && stampa_dest_div) {
                    if (italian) {
                    return etichettaDestinazione;
                } else {
                        return "Destination";
                    }
                } else {
                    if (italian) {
                    return acquisto ? "Fornitore" : etichettaCliente;
                    } else {
                        return "Dear";
                    }
                }
            }
            // -------------------            

            //debug
            //System.out.println("conta:" + conta + " campo:" + jRField.getName() + " valore:" + rDocu.getString(jRField.getName()));
            if (jRField.getName().equalsIgnoreCase("ragione_sociale")) {

                return rCliente.getString(jRField.getName());
            } else if (jRField.getName().equalsIgnoreCase("indirizzo")) {

                return rCliente.getString(jRField.getName());
            } else if (jRField.getName().equalsIgnoreCase("piva_cfiscale")) {

                return rCliente.getString(jRField.getName());
            } else if (jRField.getName().equalsIgnoreCase("piva_cfiscale_desc")) {
                if (StringUtils.isEmpty(rCliente.getString("piva_cfiscale")) && StringUtils.isEmpty(rCliente.getString("cfiscale"))) {
                    return null;
                }
                return "P.IVA " + rCliente.getString("piva_cfiscale") + " Cod. Fisc. " + rCliente.getString("cfiscale");
            } else if (jRField.getName().equalsIgnoreCase("cap_loc_prov")) {
                String capLocProv = "";
                capLocProv += it.tnx.Db.nz(rCliente.getString("cap"), "");
                if (capLocProv.length() > 0) {
                    capLocProv += " ";
                }
                capLocProv += it.tnx.Db.nz(rCliente.getString("localita"), "");
                if (it.tnx.Db.nz(rCliente.getString("provincia"), "").length() > 0) {
                    capLocProv += " (" + it.tnx.Db.nz(rCliente.getString("provincia"), "") + ")";
                }
                return capLocProv;
            } else if (jRField.getName().equalsIgnoreCase("dest_cap_loc_prov")) {
                String capLocProv = "";
                capLocProv += it.tnx.Db.nz(rDocu.getString("dest_cap"), "");
                if (capLocProv.length() > 0) {
                    capLocProv += " ";
                }
                capLocProv += it.tnx.Db.nz(rDocu.getString("dest_localita"), "");
                if (it.tnx.Db.nz(rDocu.getString("dest_provincia"), "").length() > 0) {
                    capLocProv += " (" + it.tnx.Db.nz(rDocu.getString("dest_provincia"), "") + ")";
                }
                return capLocProv;
            } else if (jRField.getName().equalsIgnoreCase("recapito")) {

                if (main.fileIni.getValueBoolean("pref", "stampaCellulare", true) == false) {
                    return "";
                }

                String recapito = "";

                if (it.tnx.Db.nz(rCliente.getString("telefono"), "").length() > 0) {
                    recapito += "Tel. " + rCliente.getString("telefono");
                }

                if (it.tnx.Db.nz(rCliente.getString("cellulare"), "").length() > 0) {

                    if (recapito.length() > 0) {
                        recapito += " ";
                    }

                    recapito += "Cell. " + rCliente.getString("cellulare");
                }

                return recapito;
            } else if (jRField.getName().equalsIgnoreCase("dest_recapito")) {

                if (main.fileIni.getValueBoolean("pref", "stampaCellulare", true) == false) {
                    return "";
                }

                String recapito = "";

                if (it.tnx.Db.nz(rDocu.getString("dest_telefono"), "").length() > 0) {
                    recapito += "Tel. " + rDocu.getString("dest_telefono");
                }

                if (it.tnx.Db.nz(rDocu.getString("dest_cellulare"), "").length() > 0) {

                    if (recapito.length() > 0) {
                        recapito += " ";
                    }

                    recapito += "Cell. " + rDocu.getString("dest_cellulare");
                }

                return recapito;
            } else if (jRField.getName().equalsIgnoreCase("numero_fattura")) {

                String num = "";

                if (it.tnx.Db.nz(rDocu.getString("serie"), "").length() > 0) {
                    num += rDocu.getString("serie") + "/";
                }

                num += rDocu.getString("numero");

                return num;
            } else if (jRField.getName().equalsIgnoreCase("sconti")) {
                String sconti = "";
                if (rDocu.getDouble("r.sconto1") != 0) {
                    sconti = it.tnx.Util.formatNumero2Decimali(rDocu.getDouble("r.sconto1"));
                    if (rDocu.getDouble("r.sconto2") != 0) {
                        sconti += " + " + Util.formatNumero2Decimali(rDocu.getDouble("r.sconto2"));
                    }
                }
                return sconti;
            } else if (jRField.getName().equalsIgnoreCase("riferimento")) {
                return rDocu.getString("riferimento");
            } else if (jRField.getName().equalsIgnoreCase("consegna")) {
                return rDocu.getString("data_consegna");
            } else if (jRField.getName().equalsIgnoreCase("s_quantita")) {

                String ret = "";

                if (rDocu.getDouble("quantita") != 0) {
                    ret = it.tnx.Util.formatNumero5Decimali(rDocu.getDouble("quantita"));
                }

                return ret;
            } else if (jRField.getName().equalsIgnoreCase("s_prezzo")) {

                String ret = "";

                if (!prezzi_ivati) {
                    if (rDocu.getDouble("prezzo") != 0) {
                        ret = Db.formatDecimal5(rDocu.getDouble("prezzo"));
                    }
                } else {
                    if (rDocu.getDouble("prezzo_ivato") != 0) {
                        ret = Db.formatDecimal5(rDocu.getDouble("prezzo_ivato"));
                    }
                }

                return ret;
            } else if (jRField.getName().equalsIgnoreCase("is_descrizione")) {
                return Db.nz(rDocu.getString("is_descrizione"), "N").equals("S");
            } else if (jRField.getName().equalsIgnoreCase("s_importo")) {
                String ret = "";
                if (!prezzi_ivati) {
                    if (rDocu.getDouble("r.totale_imponibile") != 0 || rDocu.getDouble("r.sconto1") == 100 || rDocu.getDouble("quantita") != 0) {
                        ret = it.tnx.Util.format2Decimali(rDocu.getDouble("r.totale_imponibile"));
                    }
                } else {
                    if (rDocu.getDouble("r.totale_ivato") != 0 || rDocu.getDouble("r.sconto1") == 100 || rDocu.getDouble("quantita") != 0) {
                        ret = it.tnx.Util.format2Decimali(rDocu.getDouble("r.totale_ivato"));
                    }
                }
                return ret;
            } else if (jRField.getName().equalsIgnoreCase("s_iva")) {

                String ret = "";

                if (rDocu.getDouble("iva") != 0) {
                    ret = it.tnx.Util.formatNumero0Decimali(rDocu.getDouble("iva"));
                }

                return ret;
            } else if (jRField.getName().equalsIgnoreCase("s_banca_sede")) {

                return banca_sede;
            } else if (jRField.getName().equalsIgnoreCase("s_banca_solo_sede")) {
                return banca_solo_sede;
            } else if (jRField.getName().equalsIgnoreCase("s_banca_agenzia")) {

                return banca_agenzia;
            } else if (jRField.getName().equalsIgnoreCase("s_banca_iban")) {
                   System.out.println("44444444444JRRRRRRRRR iban");

                return banca_iban;

            }  else if (jRField.getName().equalsIgnoreCase("s_banca_swift")) {
                   System.out.println("44444444444444JRRRRRRRRR swift");
                return "swift";

            }  else if (jRField.getName().equalsIgnoreCase("s_spese_trasporto")) {

                String ret = "";

                if (rDocu.getDouble("spese_trasporto") != 0) {
                    ret = it.tnx.Util.EURO + " " + it.tnx.Util.format2Decimali(rDocu.getDouble("spese_trasporto"));
                }

                return ret;
            } else if (jRField.getName().equalsIgnoreCase("s_spese_incasso")) {

                String ret = "";

                if (rDocu.getDouble("spese_incasso") != 0) {
                    ret = it.tnx.Util.EURO + " " + it.tnx.Util.format2Decimali(rDocu.getDouble("spese_incasso"));
                }

                return ret;
            } else if (jRField.getName().equalsIgnoreCase("s_sconti")) {

                String ret = "";

                if (rDocu.getDouble("t.sconto1") != 0) {
                    ret = it.tnx.Util.format2Decimali(rDocu.getDouble("t.sconto1"));
                    if (rDocu.getDouble("t.sconto2") != 0) {
                        ret += " + " + it.tnx.Util.format2Decimali(rDocu.getDouble("t.sconto2"));
                    }
                    if (rDocu.getDouble("t.sconto3") != 0) {
                        ret += " + " + it.tnx.Util.format2Decimali(rDocu.getDouble("t.sconto3"));
                    }
                }
                if (ret.length() > 0 && rDocu.getDouble("t.sconto") > 0) {
                    ret += ",  ";
                }
                if (rDocu.getDouble("t.sconto") > 0) {
                    ret += " - € " + FormatUtils.formatEuroIta(rDocu.getDouble("t.sconto"));
                }

                return ret;
            } else if (jRField.getName().equalsIgnoreCase("messaggio")) {
                String ret = main.fileIni.getValue("varie", "messaggioStampa");
                return ret;
            } else if (jRField.getName().equalsIgnoreCase("iva_codice_1")) {

                return iva_codice_1;
            } else if (jRField.getName().equalsIgnoreCase("iva_desc_1")) {

                return iva_desc_1;
            } else if (jRField.getName().equalsIgnoreCase("iva_imp_1")) {

                return iva_imp_1;
            } else if (jRField.getName().equalsIgnoreCase("iva_perc_1")) {

                return iva_perc_1;
            } else if (jRField.getName().equalsIgnoreCase("iva_imposta_1")) {

                return iva_imposta_1;
            } else if (jRField.getName().equalsIgnoreCase("iva_codice_2")) {

                return iva_codice_2;
            } else if (jRField.getName().equalsIgnoreCase("iva_desc_2")) {

                return iva_desc_2;
            } else if (jRField.getName().equalsIgnoreCase("iva_imp_2")) {

                return iva_imp_2;
            } else if (jRField.getName().equalsIgnoreCase("iva_perc_2")) {

                return iva_perc_2;
            } else if (jRField.getName().equalsIgnoreCase("iva_imposta_2")) {

                return iva_imposta_2;
            } else if (jRField.getName().equalsIgnoreCase("iva_codice_3")) {

                return iva_codice_3;
            } else if (jRField.getName().equalsIgnoreCase("iva_desc_3")) {

                return iva_desc_3;
            } else if (jRField.getName().equalsIgnoreCase("iva_imp_3")) {

                return iva_imp_3;
            } else if (jRField.getName().equalsIgnoreCase("iva_perc_3")) {

                return iva_perc_3;
            } else if (jRField.getName().equalsIgnoreCase("iva_imposta_3")) {

                return iva_imposta_3;
            } else if (jRField.getName().equalsIgnoreCase("iva_codice_4")) {

                return iva_codice_4;
            } else if (jRField.getName().equalsIgnoreCase("iva_desc_4")) {

                return iva_desc_4;
            } else if (jRField.getName().equalsIgnoreCase("iva_imp_4")) {

                return iva_imp_4;
            } else if (jRField.getName().equalsIgnoreCase("iva_perc_4")) {

                return iva_perc_4;
            } else if (jRField.getName().equalsIgnoreCase("iva_imposta_4")) {

                return iva_imposta_4;
            } else if (jRField.getName().startsWith("scadenze_data_")) {
                int i = CastUtils.toInteger0(StringUtils.substringAfterLast(jRField.getName(), "_"));
                if (scadenze_date.size() >= i) {
                    return scadenze_date.get(i - 1);
                } else {
                    return "";
                }
            } else if (jRField.getName().startsWith("scadenze_importo_")) {
                int i = CastUtils.toInteger0(StringUtils.substringAfterLast(jRField.getName(), "_"));
                if (scadenze_importi.size() >= i) {
                    return scadenze_importi.get(i - 1);
                } else {
                    return "";
                }
            } else if (jRField.getName().equalsIgnoreCase("s_totale_lordo")) {
                if (!prezzi_ivati) {
                    return it.tnx.Util.EURO + "  " + it.tnx.Util.format2Decimali(doc.totaleImponibilePreSconto);
                } else {
                    return it.tnx.Util.EURO + "  " + it.tnx.Util.format2Decimali(doc.totaleIvatoPreSconto);
                }
            } else if (jRField.getName().equalsIgnoreCase("s_totale_merce")) {
                if (!prezzi_ivati) {
                    return it.tnx.Util.EURO + "  " + it.tnx.Util.format2Decimali(doc.getTotaleImponibile());
                } else {
                    return it.tnx.Util.EURO + "  " + it.tnx.Util.format2Decimali(doc.getTotale());
                }
            } else if (jRField.getName().equalsIgnoreCase("s_totale_imponibile")) {

                return it.tnx.Util.EURO + "  " + it.tnx.Util.format2Decimali(doc.getTotaleImponibile());
            } else if (jRField.getName().equalsIgnoreCase("totale_sconti_complessivo")) {

                return it.tnx.Util.EURO + "  " + it.tnx.Util.format2Decimali(doc.getTotaleSconti());
            } else if (jRField.getName().equalsIgnoreCase("s_totale_iva")) {

                return it.tnx.Util.EURO + "  " + it.tnx.Util.format2Decimali(doc.getTotaleIva());
            } else if (jRField.getName().equalsIgnoreCase("s_totale")) {

                return it.tnx.Util.EURO + "  " + it.tnx.Util.format2Decimali(doc.getTotale());
            } else if (jRField.getName().equalsIgnoreCase("file_logo")) {
                return getImg(true, false);
            } else if (jRField.getName().equalsIgnoreCase("file_logo_input")) {
                return getImg(true, true);
            } else if (jRField.getName().equalsIgnoreCase("file_sfondo_input")) {
                return getImg(false, true);
            } else if (jRField.getName().equalsIgnoreCase("file_img_firma")) {
                System.out.println("######################################################### FIRMA IMMAGINE");
                return getImgFirma();
            } else if (jRField.getName().equalsIgnoreCase("acquisto")) {
                /* 18/09/2013 ceccam : non so perchè c'era questo esclamativo davanti, faceva in modo che sul report venisse al contrario.. ?
                return !acquisto;
                */
                return acquisto;
            } else if (jRField.getName().equalsIgnoreCase("intestazione1")) {
                return intestazione1;
            } else if (jRField.getName().equalsIgnoreCase("intestazione2")) {
                return intestazione2;
            } else if (jRField.getName().equalsIgnoreCase("intestazione3")) {
                return intestazione3;
            } else if (jRField.getName().equalsIgnoreCase("intestazione4")) {
                return intestazione4;
            } else if (jRField.getName().equalsIgnoreCase("intestazione5")) {
                return intestazione5;
            } else if (jRField.getName().equalsIgnoreCase("intestazione6")) {
                return intestazione6;
            } else if (jRField.getName().equalsIgnoreCase("note_da_impostazioni")) {
                return notePiede;
            } else if (jRField.getName().equalsIgnoreCase("stampa_riga_aggiuntiva")) {
                return stampaInvoicexRiga;
            } else if (jRField.getName().equalsIgnoreCase("totale_quantita")) {
                return Double.valueOf(this.doc.getTotaleQuantita());
            } else if (jRField.getName().equalsIgnoreCase("pagamento")) {
                if (main.getPersonalContain("pagamento_stampa_codice")) {
                    return Db.nz(rDocu.getString(jRField.getName()), "");
                } else {
                    try {
                        String codicePagamento = Db.nz(rDocu.getObject(jRField.getName()), "");
                        String descPagamento = String.valueOf(DbUtils.getObject(Db.getConn(), "SELECT descrizione FROM pagamenti WHERE codice = " + Db.pc(codicePagamento, Types.VARCHAR)));
                        if (StringUtils.isEmpty(descPagamento)) {
                            return codicePagamento;
                        }
                        return descPagamento;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return Db.nz(rDocu.getString(jRField.getName()), "");
                    }
                }
            } else if (jRField.getName().equalsIgnoreCase("int_dest_1")) {
                String s = main.fileIni.getValue("varie", "int_dest_1");
                s = sostituisci(s);
                return s;
            } else if (jRField.getName().equalsIgnoreCase("int_dest_2")) {
                String s = main.fileIni.getValue("varie", "int_dest_2");
                s = sostituisci(s);
                return s;
// Blocco dati snj
            } else if (jRField.getName().equalsIgnoreCase("s_percentuale")) {
                return rDocu.getInt("percentuale");
            } else if (jRField.getName().equalsIgnoreCase("s_emissione")) {
                String emissione = String.valueOf(DbUtils.getObject(Db.getConn(), "SELECT descrizione FROM tipi_emissione_fattura WHERE id = " + Db.pc(rDocu.getInt("emissione_fattura"), Types.INTEGER)));
                return emissione;
            } else if (jRField.getName().equalsIgnoreCase("s_pagamento")) {
                String pagamento = String.valueOf(DbUtils.getObject(Db.getConn(), "SELECT descrizione FROM pagamenti WHERE codice = " + Db.pc(rDocu.getString("termini_pagamento"), Types.VARCHAR)));
                return pagamento;
            } else if (jRField.getName().equalsIgnoreCase("s_consulenza_contratto")) {
                String consulenza = String.valueOf(DbUtils.getObject(Db.getConn(), "SELECT descrizione FROM tipi_durata_consulenza WHERE id = " + Db.pc(rDocu.getInt("durata_consulenza"), Types.INTEGER)));
                String contratto = String.valueOf(DbUtils.getObject(Db.getConn(), "SELECT descrizione FROM tipi_durata_contratto WHERE id = " + Db.pc(rDocu.getInt("durata_contratto"), Types.INTEGER)));
                return consulenza + " " + contratto;
            } else if (jRField.getName().equalsIgnoreCase("immagine1_esiste")) {
                try {
                    File test = new File(rDocu.getString("immagine1"));
                    return test.exists();                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
//campi direttametne dal db
            } else if (jRField.getValueClassName().equals("java.lang.String")) {

                return Db.nz(rDocu.getString(jRField.getName()), "");
            } else if (jRField.getValueClassName().equals("java.lang.Object")) {

                return Db.nz(rDocu.getObject(jRField.getName()), "");
            } else if (jRField.getValueClassName().equals("java.util.Date")) {

                return rDocu.getDate(jRField.getName());
            } else if (jRField.getValueClassName().equals("java.lang.Double")) {

                return new Double(rDocu.getDouble(jRField.getName()));
            } else if (jRField.getValueClassName().equals("java.lang.Integer")) {

                return new Integer(rDocu.getInt(jRField.getName()));
            } else if (jRField.getValueClassName().equals("java.lang.Long")) {

                return new Long(rDocu.getLong(jRField.getName()));
            } else {

                return rDocu.getObject(jRField.getName());
            }
        } catch (Exception err) {
            System.err.println(err);
        }

        return null;
    }

    public boolean next()
            throws net.sf.jasperreports.engine.JRException {
        conta++;

        try {

            boolean ret = rDocu.next();

            return ret;
        } catch (Exception err) {
            err.printStackTrace();
        }

        return false;
    }

    public static void main(String[] args) {

        testReport t = new testReport();
    }

    private Object getImgFirma() {
        ResultSet r = Db.openResultSet("SELECT immagine_firma_ordine FROM dati_azienda LIMIT 1");
        try {
            if (r.next()) {
                Blob blob = r.getBlob("immagine_firma_ordine");
                return blob.getBinaryStream();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Object getImg(boolean isLogo, boolean isInputStream) {
        return JRDSInvoice.getImg(isLogo, isInputStream, serie, numero, anno, perEmail, acquisto, acquisto ? "test_ordi_acquisto" : "test_ordi");
    }
}
