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
/*

 * JRDSInvoice.java

 *

 * Created on January 18, 2005, 2:32 PM

 */
package reports;

import gestioneFatture.*;
import gestioneFatture.logic.clienti.Cliente;

import gestioneFatture.logic.documenti.*;

import it.tnx.Util;
import it.tnx.commons.CastUtils;
import it.tnx.commons.FormatUtils;
import it.tnx.invoicex.InvoicexUtil;
import java.sql.*;

import java.util.*;
import java.util.prefs.Preferences;
import org.apache.commons.lang.StringUtils;

/**
 *
 *
 *
 * @author marco
 *
 */
public class JRDSDdt extends JRDSBase
        implements net.sf.jasperreports.engine.JRDataSource {

    private int conta = 0;
    String sql = "";
    ResultSet rDocu = null;
    ResultSet rCliente = null;
    private Documento doc;
    DettaglioIva diva = null;
    String serie = "";
    int numero = 1;
    int anno = 2004;
    String banca_sede;
    String banca_solo_sede;
    String banca_agenzia;
    String banca_iban;
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
    String notePiede = "";
    boolean stampaInvoicexRiga = true;
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
    boolean perEmail = false;
    public Integer codiceCliente = null;
    boolean italian = true;
    public String nomeClienteFile;
    String tipodoc = Db.TIPO_DOCUMENTO_DDT;
    boolean acquisto = false;
    String tabt = "test_ddt";
    String tabr = "righ_ddt";
    String ccliente = "cliente";
    boolean prezzi_ivati = false;

    /**
     * Creates a new instance of JRDSInvoice
     */
    public JRDSDdt(Connection conn, String serie, int numero, int anno, boolean perEmail, boolean acquisto, Integer id) {
        this.serie = serie;
        this.numero = numero;
        this.anno = anno;
        this.perEmail = perEmail;
        doc = new Documento();
        this.acquisto = acquisto;
        if (acquisto) {
            tipodoc = Db.TIPO_DOCUMENTO_DDT_ACQUISTO;
            tabt = "test_ddt_acquisto";
            tabr = "righ_ddt_acquisto";
            ccliente = "fornitore";
        }
        doc.load(Db.INSTANCE, numero, serie, anno, tipodoc, id);
        doc.calcolaTotali();
        doc.visualizzaCastellettoIva();

        int ivaSize = doc.dettagliIva.size();

        if (ivaSize > 3) {
            javax.swing.JOptionPane.showMessageDialog(null, "Ci sono piu' di 3 tipi di iva ma ne verranno stampati solo 3 !!!");
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

        sql = "" + "select t.*,r.*,clie_forn.*,clie_forn.codice as codice_cliente2, clie_forn_dest.* " + (acquisto ? ", '' as cliente, '' as cliente_destinazione " : "")
                + " , l.codice as listino \n"
                + " , l.descrizione as listino_descrizione \n"
                + " , l.ricarico_flag as listino_ricarico_flag \n"
                + " , l.ricarico_perc as listino_ricarico_perc \n"
                + " , l.ricarico_listino as listino_ricarico_listino \n"
                + " , l2.codice as listino2 \n"
                + " , l2.descrizione as listino2_descrizione \n"
                + " , l2.ricarico_flag as listino2_ricarico_flag \n"
                + " , l2.ricarico_perc as listino2_ricarico_perc \n"
                + " , l2.ricarico_listino as listino2_ricarico_listino \n"
                + " , ap.prezzo as listino_prezzo \n"
                + " , ap2.prezzo as listino2_prezzo \n"
                + " , articoli.codice_a_barre \n"
                + " , articoli.codice_fornitore \n"
                + " , articoli.immagine1 \n"
                + " from((" + tabt + " t \n"
                + " left join " + tabr + " r on t.serie = r.serie and t.numero = r.numero and r.anno = " + anno + " ) \n"
                + " left join clie_forn on t." + ccliente + " = clie_forn.codice) \n"
                + " left join clie_forn_dest on t." + ccliente + " = clie_forn_dest.codice_cliente and t." + ccliente + "_destinazione = clie_forn_dest.codice \n"
                + " left join tipi_listino l on t.listino_consigliato = l.codice \n"
                + " left join articoli_prezzi ap on r.codice_articolo = ap.articolo and ap.listino = l.codice \n"
                + " left join tipi_listino l2 on l.ricarico_listino = l2.codice \n"
                + " left join articoli_prezzi ap2 on r.codice_articolo = ap2.articolo and ap2.listino = l2.codice \n"
                + " left join articoli on r.codice_articolo = articoli.codice \n"
                + " where t.serie = " + Db.pc(serie, Types.VARCHAR) + " and t.numero = " + numero + " and t.anno = " + anno + " order by r.riga";

        //debug
        System.out.println("jasper sql:" + sql);

        try {

            Statement stat = conn.createStatement();
            rDocu = stat.executeQuery(sql);

            Statement statCliente = conn.createStatement();

            //trovo dati banca
            banca_sede = "";
            banca_agenzia = "";
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

            //seleziono i dati del cliente
            rCliente = statCliente.executeQuery("select * from clie_forn where codice = " + rDocu.getInt(ccliente));
            rCliente.next();
            nomeClienteFile = rCliente.getString("ragione_sociale");

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
                if (!main.pluginAttivi.contains("pluginToysforyou")) {
                    if ("ITALY".equals(recapito_1)) {
                        recapito_1 = "";
                    }
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
                if (!main.pluginAttivi.contains("pluginToysforyou")) {
                    if ("ITALY".equals(recapito_2)) {
                        recapito_2 = "";
                    }
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
                if (!main.pluginAttivi.contains("pluginToysforyou")) {
                    if ("ITALY".equals(recapito_2)) {
                        recapito_2 = "";
                    }
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
                if (!main.pluginAttivi.contains("pluginToysforyou")) {
                    if ("ITALY".equals(recapito_1)) {
                        recapito_1 = "";
                    }
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
            //--------------------------

            rDocu.previous();
        } catch (Exception err) {
            err.printStackTrace();
        }

        //carico dati intestazione
        try {
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
                String nomeCampoNotePiede = acquisto ? "testo_piede_docu_a" : "testo_piede_docu_v";
                notePiede = rDatiAzienda.getString(nomeCampoNotePiede);
                stampaInvoicexRiga = rDatiAzienda.getInt("stampa_riga_invoicex") == 1;
            }
            rDatiAzienda.close();
            sDatiAzienda.close();
        } catch (Exception err) {
            err.printStackTrace();
        }

    }

    public Object getFieldValue(net.sf.jasperreports.engine.JRField jRField)
            throws net.sf.jasperreports.engine.JRException {

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
            } else if (jRField.getName().equalsIgnoreCase("stampa_dest_div")) {
                return stampa_dest_div;
            } else if (jRField.getName().equalsIgnoreCase("etichetta_int1")) {
                if (main.fileIni.getValueBoolean("pref", "stampaDestDiversaSotto", false) && stampa_dest_div) {
                    if (italian) {
                        return acquisto ? "Forntiore" : etichettaCliente;
                    } else {
                        return etichettaCliente_eng;
                    }
                } else {
                    if (italian) {
                        return etichettaDestinazione;

                    } else {
                        return etichettaDestinazione_eng;
                    }
                }
            } else if (jRField.getName().equalsIgnoreCase("etichetta_int2")) {
                if (main.fileIni.getValueBoolean("pref", "stampaDestDiversaSotto", false) && stampa_dest_div) {
                    if (italian) {
                        return etichettaDestinazione;
                    } else {
                        return etichettaDestinazione_eng;
                    }
                } else {
                    if (italian) {
                        return acquisto ? "Fornitore" : etichettaCliente;
                    } else {
                        return etichettaCliente_eng;
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
                        sconti += " + " + it.tnx.Util.formatNumero2Decimali(rDocu.getDouble("r.sconto2"));
                    }
                }
                return sconti;
            } else if (jRField.getName().equalsIgnoreCase("s_quantita")) {

                String ret = "";

                if (rDocu.getDouble("quantita") != 0) {
                    ret = it.tnx.Util.formatNumero5Decimali(rDocu.getDouble("quantita"));
                }

                return ret;
            } else if (jRField.getName().equalsIgnoreCase("s_numcasse")) {

                String ret = "";

                if (rDocu.getDouble("numero_casse") != 0) {
                    ret = it.tnx.Util.int2str(rDocu.getInt("numero_casse"));
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
            } else if (jRField.getName().equalsIgnoreCase("s_prezzo_consigliato")) {
                String ret = "";
                double prezzo = 0;
                if (rDocu.getObject("listino_ricarico_flag") != null && rDocu.getString("listino_ricarico_flag").equals("S")) {
                    //ricarico
                    prezzo = rDocu.getDouble("listino2_prezzo");
                    prezzo = prezzo + (prezzo / 100d * rDocu.getDouble("listino_ricarico_perc"));
                } else {
                    prezzo = rDocu.getDouble("listino_prezzo");
                }
                if (prezzo != 0) {
                    ret = it.tnx.Util.format2Decimali(prezzo);
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
            } else if (jRField.getName().equalsIgnoreCase("s_spese_trasporto")) {

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
            } else if (jRField.getName().equalsIgnoreCase("s_totale_imponibile")) {

                return it.tnx.Util.EURO + "  " + it.tnx.Util.format2Decimali(doc.getTotaleImponibile());
            } else if (jRField.getName().equalsIgnoreCase("s_totale_iva")) {

                return it.tnx.Util.EURO + "  " + it.tnx.Util.format2Decimali(doc.getTotaleIva());
            } else if (jRField.getName().equalsIgnoreCase("s_totale")) {

                return it.tnx.Util.EURO + "  " + it.tnx.Util.format2Decimali(doc.getTotale());
            } else if (jRField.getName().equalsIgnoreCase("flag_ritenuta")) {
                if (doc.getRitenuta() == 0) {
                    return false;
                } else {
                    return true;
                }
            } else if (jRField.getName().equalsIgnoreCase("file_logo")) {
                return getImg(true, false);
            } else if (jRField.getName().equalsIgnoreCase("file_logo_input")) {
                return getImg(true, true);
            } else if (jRField.getName().equalsIgnoreCase("file_sfondo_input")) {
                return getImg(false, true);
            } else if (jRField.getName().equalsIgnoreCase("acquisto")) {
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
            } else if (jRField.getName().equalsIgnoreCase("peso_netto")) {
                String peso_netto = Db.nz(rDocu.getString(jRField.getName()), "");
                if (peso_netto.length() > 0) {
                    return peso_netto;
                }
                if (doc.totalePeso == 0) {
                    return "";
                }
                return Util.format2Decimali(doc.totalePeso) + " Kg";
            } else if (jRField.getName().equalsIgnoreCase("totale_quantita")) {
                return Double.valueOf(this.doc.getTotaleQuantita());
            } else if (jRField.getName().equalsIgnoreCase("int_dest_1")) {
                String s = main.fileIni.getValue("varie", "int_dest_1");
                s = sostituisci(s);
                return s;
            } else if (jRField.getName().equalsIgnoreCase("int_dest_2")) {
                String s = main.fileIni.getValue("varie", "int_dest_2");
                s = sostituisci(s);
                return s;
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
            err.printStackTrace();
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

    private Object getImg(boolean isLogo, boolean isInputStream) {
        return JRDSInvoice.getImg(isLogo, isInputStream, serie, numero, anno, perEmail, acquisto, acquisto ? "test_ddt_acquisto" : "test_ddt");
    }
}
