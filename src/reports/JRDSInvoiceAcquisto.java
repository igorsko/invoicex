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
import gestioneFatture.logic.documenti.*;
import it.tnx.commons.CastUtils;
import it.tnx.commons.DbUtils;
import it.tnx.commons.FormatUtils;
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
public class JRDSInvoiceAcquisto extends JRDSBase implements net.sf.jasperreports.engine.JRDataSource {

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
    public String nomeClienteFile;
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
    String recapito_2 = "";
    String email_2 = "";
    boolean perEmail = false;
    String notePiede = "";
    boolean stampaInvoicexRiga = true;
    boolean prezzi_ivati = false;

    /** Creates a new instance of JRDSInvoice */
    public JRDSInvoiceAcquisto(Connection conn, String serie, int numero, int anno, boolean perEmail, Integer id) {
        this.serie = serie;
        this.numero = numero;
        this.anno = anno;
        this.id = id;
        this.perEmail = perEmail;
        doc = new Documento();
        doc.load(Db.INSTANCE, numero, serie, anno, Db.TIPO_DOCUMENTO_FATTURA_RICEVUTA, id);
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

        sql = "" + "select test_fatt_acquisto.*,righ_fatt_acquisto.*,clie_forn.*, clie_forn.codice as codice_cliente2, righ_fatt_acquisto.iva as ivarighe, righ_fatt_acquisto.descrizione as descrizionerighe "
                + " , articoli.codice_a_barre \n"
                + " , articoli.codice_fornitore \n"
                + " , articoli.immagine1 \n"
                + " from ((test_fatt_acquisto left join righ_fatt_acquisto on test_fatt_acquisto.serie = righ_fatt_acquisto.serie and test_fatt_acquisto.numero = righ_fatt_acquisto.numero and righ_fatt_acquisto.anno = " + anno + ")"
                + " left join clie_forn on test_fatt_acquisto.fornitore = clie_forn.codice)"
                + " left join articoli on righ_fatt_acquisto.codice_articolo = articoli.codice \n"
                + " where test_fatt_acquisto.serie = " + Db.pc(serie, Types.VARCHAR) + " and test_fatt_acquisto.numero = " + numero + " and test_fatt_acquisto.anno = " + anno + " order by righ_fatt_acquisto.riga";

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

            String prezzi_ivati_s = CastUtils.toString(rDocu.getString("prezzi_ivati"));
            if (prezzi_ivati_s.equalsIgnoreCase("S")) {
                prezzi_ivati = true;
            }

            //seleziono i dati del cliente
            rCliente = statCliente.executeQuery("select * from clie_forn where codice = " + rDocu.getInt("fornitore"));
            rCliente.next();
            nomeClienteFile = rCliente.getString("ragione_sociale");

            stampa_dest_div = false;

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
                piva_cfiscale_desc_2 = "P.IVA " + rCliente.getString("piva_cfiscale") + " Cod. Fisc. " + rCliente.getString("cfiscale");

//                ragione_sociale_1 = rDocu.getString("dest_ragione_sociale");
//                indirizzo_1 = rDocu.getString("dest_indirizzo");
//                capLocProv = "";
//                capLocProv += gestioneFatture.Db.nz(rDocu.getString("dest_cap"), "");
//                if (capLocProv.length() > 0) {
//                    capLocProv += " ";
//                }
//                capLocProv += gestioneFatture.Db.nz(rDocu.getString("dest_localita"), "");
//                if (gestioneFatture.Db.nz(rDocu.getString("dest_provincia"), "").length() > 0) {
//                    capLocProv += " (" + gestioneFatture.Db.nz(rDocu.getString("dest_provincia"), "") + ")";
//                }
//                cap_loc_prov_1 = capLocProv;
//                piva_cfiscale_desc_1 = "";
            }
            //--------------------------

            rDocu.previous();
        } catch (Exception err) {
            err.printStackTrace();
        }

    }

    public Object getFieldValue(net.sf.jasperreports.engine.JRField jRField)
            throws net.sf.jasperreports.engine.JRException {

        try {
            if (jRField.getName().equalsIgnoreCase("tipo_fattura")) {
                return 1;
            }

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
            } else if (jRField.getName().equalsIgnoreCase("recapito2")) {
                return recapito_2;
            }

            if (jRField.getName().equalsIgnoreCase("stampa_dest_div")) {
                return stampa_dest_div;
            } else if (jRField.getName().equalsIgnoreCase("etichetta_int1")) {
                if (main.fileIni.getValueBoolean("pref", "stampaDestDiversaSotto", false) && stampa_dest_div) {
                    return "Fornitore";
                } else {
                    return "...";
                }
            } else if (jRField.getName().equalsIgnoreCase("etichetta_int2")) {
                if (main.fileIni.getValueBoolean("pref", "stampaDestDiversaSotto", false) && stampa_dest_div) {
                    return "...";
                } else {
                    return "Fornitore";
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
            } else if (jRField.getName().equalsIgnoreCase("numero_fattura_esterna")) {
                String num = "";
                if (it.tnx.Db.nz(rDocu.getString("serie_doc"), "").length() > 0) {
                    num += rDocu.getString("serie_doc") + "/";
                }
                num += rDocu.getString("numero_doc");
                return num;
            } else if (jRField.getName().equalsIgnoreCase("sconti")) {

                String sconti = "";

                if (rDocu.getDouble("righ_fatt_acquisto.sconto1") != 0) {
                    sconti = it.tnx.Util.formatNumero2Decimali(rDocu.getDouble("righ_fatt_acquisto.sconto1"));

                    if (rDocu.getDouble("righ_fatt_acquisto.sconto2") != 0) {
                        sconti += " + " + it.tnx.Util.formatNumero2Decimali(rDocu.getDouble("righ_fatt_acquisto.sconto2"));
                    }
                }

                return sconti;
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
                    if (rDocu.getDouble("righ_fatt_acquisto.totale_imponibile") != 0 || rDocu.getDouble("righ_fatt_acquisto.sconto1") == 100 || rDocu.getDouble("quantita") != 0) {
                        ret = it.tnx.Util.format2Decimali(rDocu.getDouble("righ_fatt_acquisto.totale_imponibile"));
                    }
                } else {
                    if (rDocu.getDouble("righ_fatt_acquisto.totale_ivato") != 0 || rDocu.getDouble("righ_fatt_acquisto.sconto1") == 100 || rDocu.getDouble("quantita") != 0) {
                        ret = it.tnx.Util.format2Decimali(rDocu.getDouble("righ_fatt_acquisto.totale_ivato"));
                    }
                }
                return ret;
            } else if (jRField.getName().equalsIgnoreCase("s_iva")) {

                String ret = "";

                if (rDocu.getDouble("ivarighe") != 0) {
                    ret = it.tnx.Util.formatNumero0Decimali(rDocu.getDouble("ivarighe"));
                }

                return ret;
            } else if (jRField.getName().equalsIgnoreCase("s_banca_sede")) {
                return banca_sede;
            } else if (jRField.getName().equalsIgnoreCase("s_banca_solo_sede")) {
                return banca_solo_sede;
            } else if (jRField.getName().equalsIgnoreCase("s_banca_agenzia")) {

                return banca_agenzia;
            } else if (jRField.getName().equalsIgnoreCase("s_banca_agenzia")) {

                return banca_agenzia;
            } else if (jRField.getName().equalsIgnoreCase("s_banca_iban")) {
                return banca_iban;

            }  else if (jRField.getName().equalsIgnoreCase("s_banca_swift")) {
                System.out.println("swift: "+banca_SWIFT);
                return banca_SWIFT;

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
                if (rDocu.getDouble("test_fatt_acquisto.sconto1") != 0) {
                    ret = it.tnx.Util.format2Decimali(rDocu.getDouble("test_fatt_acquisto.sconto1"));
                    if (rDocu.getDouble("test_fatt_acquisto.sconto2") != 0) {
                        ret += " + " + it.tnx.Util.format2Decimali(rDocu.getDouble("test_fatt_acquisto.sconto2"));
                    }
                    if (rDocu.getDouble("test_fatt_acquisto.sconto3") != 0) {
                        ret += " + " + it.tnx.Util.format2Decimali(rDocu.getDouble("test_fatt_acquisto.sconto3"));
                    }
                }
                if (ret.length() > 0 && rDocu.getDouble("test_fatt_acquisto.sconto") > 0) {
                    ret += ",  ";
                }
                if (rDocu.getDouble("test_fatt_acquisto.sconto") > 0) {
                    ret += " - € " + FormatUtils.formatEuroIta(rDocu.getDouble("test_fatt_acquisto.sconto"));
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
            } else if (jRField.getName().equalsIgnoreCase("s_totale_ritenuta")) {
                return it.tnx.Util.EURO + "  " + it.tnx.Util.format2Decimali(doc.getTotale_ritenuta());
            } else if (jRField.getName().equalsIgnoreCase("s_totale_pagare")) {
                return it.tnx.Util.EURO + "  " + it.tnx.Util.format2Decimali(doc.getTotale_da_pagare());
            } else if (jRField.getName().equalsIgnoreCase("flag_ritenuta")) {
                if (doc.getRitenuta() == 0) {
                    return false;
                } else {
                    return true;
                }
            } else if (jRField.getName().equalsIgnoreCase("s_ritenuta")) {
                return doc.getRitenuta_descrizione();
            } else if (jRField.getName().equalsIgnoreCase("file_logo")) {
                return getImg(true, false);
            } else if (jRField.getName().equalsIgnoreCase("file_logo_input")) {
                if (!perEmail) {
                    ResultSet r = Db.openResultSet("select logo from dati_azienda");
                    if (r.next()) {
                        Blob blob = r.getBlob("logo");
                        try {
                            return blob.getBinaryStream();
                        } catch (NullPointerException ex0) {
                        }
                    }
                    return null;
                } else {
                    return getImg(true, true);
                }
            } else if (jRField.getName().equalsIgnoreCase("file_sfondo_input")) {
                return getImg(false, true);
            } else if (jRField.getName().equalsIgnoreCase("acquisto")) {
                return true;
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
            } else if (jRField.getName().equalsIgnoreCase("int_dest_1")) {
                String s = main.fileIni.getValue("varie", "int_dest_1");
                s = sostituisci(s);
                return s;
            } else if (jRField.getName().equalsIgnoreCase("int_dest_2")) {
                String s = main.fileIni.getValue("varie", "int_dest_2");
                s = sostituisci(s);
                return s;
//campi direttametne dal db
            } else if (jRField.getName().equalsIgnoreCase("note_da_impostazioni")) {
                return DbUtils.getString(Db.getConn(), "SELECT testo_piede_fatt_a  FROM dati_azienda LIMIT 1");
            } else if (jRField.getName().equalsIgnoreCase("stampa_riga_aggiuntiva")) {
                Integer stampaRigaAgg = Integer.parseInt(String.valueOf(DbUtils.getObject(Db.getConn(), "SELECT stampa_riga_invoicex FROM dati_azienda LIMIT 1")));
                return stampaRigaAgg == 1;
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

    private Object getImg(boolean isLogo, boolean isInputStream) {
        return JRDSInvoice.getImg(isLogo, isInputStream, serie, numero, anno, perEmail, true, "test_fatt_acquisto");
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
}
