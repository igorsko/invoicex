/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.tnx.invoicex;

import gestioneFatture.Db;
import gestioneFatture.main;
import it.tnx.commons.CastUtils;
import it.tnx.commons.DbUtils;
import it.tnx.commons.FormatUtils;
import it.tnx.commons.SystemUtils;
import it.tnx.invoicex.gui.JDialogExportVenditePeroni;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author mceccarelli
 */
public class BirraPeroni {

    JDialogExportVenditePeroni frm = null;
    String s = "|";
    String eol = "\r\n";
    public String nomefile;
    public File fcartella;
    public String cartella;
    Map azienda;
    String current_YYYYMMDD = formatYYYYMMDD();
    String current_HHMMSS = formatHHMMSS();
    String sql = "";

    public BirraPeroni(JDialogExportVenditePeroni frm) throws Exception {
        this.frm = frm;
        azienda = DbUtils.getListMap(Db.getConn(), "Select " + main.campiDatiAzienda + " from dati_azienda").get(0);
        nomefile = "WHS_" + azienda.get("piva") + "_" + current_YYYYMMDD + "_" + current_HHMMSS + ".txt";
        fcartella = new File(SystemUtils.getUserDocumentsFolder() + File.separator + "Invoicex" + File.separator + "export");
        try {
            fcartella.mkdir();    
        } catch (Exception e) {
            e.printStackTrace();
        }
        cartella = fcartella.getAbsolutePath();
    }

    public void start() throws Exception {
        FileWriter fw = new FileWriter(cartella + File.separator + nomefile);
        String line = null;
        int numrec = 0,numrec01 = 0,numrec02 = 0;

        //controlli da fare...?

        //header
        line = "";
        line += "H";
        line += s + current_YYYYMMDD;
        line += s + CastUtils.toString(frm.annoDal.getSelectedItem()) + CastUtils.toString(frm.meseDal.getSelectedItem());
        line += s + CastUtils.toString(frm.annoAl.getSelectedItem()) + CastUtils.toString(frm.meseAl.getSelectedItem());
        line += s + azienda.get("piva");
        line += s + azienda.get("ragione_sociale");
        line += s + nomefile;
        line += eol;
        fw.write(line);

        //body
        sql = "select \n";
        sql += " cast(IF(IFNULL(t.cliente_destinazione,0)=0, t.cliente, concat(t.cliente, '/',t.cliente_destinazione)) as char(20)) as cod_dest, \n";
        sql += " IF(IFNULL(t.cliente_destinazione,0)=0, c.ragione_sociale, concat(c.ragione_sociale, '/', t.dest_ragione_sociale)) as rs_dest, \n";
        sql += " c.persona_riferimento, \n";
        sql += " a.tipo_birra, \n";
        sql += " IF(IFNULL(t.cliente_destinazione,0)=0, c.indirizzo, t.dest_indirizzo) as indirizzo_dest, \n";
        sql += " IF(IFNULL(t.cliente_destinazione,0)=0, c.cap, t.dest_cap) as cap_dest, \n";
        sql += " IF(IFNULL(t.cliente_destinazione,0)=0, c.localita, t.dest_localita) as localita_dest, \n";
        sql += " IF(IFNULL(t.cliente_destinazione,0)=0, c.provincia, t.dest_provincia) as prov_dest, \n";
        sql += " IF(cat.descrizione = '', 'ND', cat.descrizione) as cat_clie, \n";
        sql += " c.piva_cfiscale as c_pi, c.cfiscale as c_cf, cd.piva_cfiscale as cd_pi, cd.cfiscale as cd_cf, \n";
        sql = sql + " c.codice, c.ragione_sociale, r.codice_articolo, a.codice_fornitore, r.descrizione as r_descrizione, a.descrizione as a_descrizione, t.data, \n";
        sql = sql + " r.um, a.um, sum(IF(r.prezzo = 0, 0, if(t.tipo_fattura = 3, -r.quantita, r.quantita))) as sq, a.peso_kg, \n";
        sql = sql + " sum(IF(r.prezzo = 0, if(t.tipo_fattura = 3, -r.quantita, r.quantita), 0)) as sqo, \n";
        sql = sql + " round(sum(IF(r.prezzo = 0, 0, if(t.tipo_fattura = 3, -r.quantita, r.quantita))) * a.peso_kg / 100, 7) as shl, \n";
        sql = sql + " round(sum(IF(r.prezzo = 0, if(t.tipo_fattura = 3, -r.quantita, r.quantita), 0)) * a.peso_kg / 100, 7) as shlo, \n";
        sql = sql + " r.prezzo as 'prezzo unitario',  \n";
        sql = sql + " r.prezzo * if(t.tipo_fattura = 3, -r.quantita, r.quantita) as importo,  \n";
        sql = sql + " r.sconto1 as 'sconto riga 1',  r.sconto2 as 'sconto riga 2',  t.sconto1 as 'sconto testata 1',  t.sconto2 as 'sconto testata 2',   \n";
        sql = sql + " t.sconto3 as 'sconto testata 3'  , \n";
        sql = sql + " if(t.tipo_fattura = 3, -r.totale_imponibile, r.totale_imponibile) as tot_imp,\n";
        sql = sql + " if(t.tipo_fattura = 3, -r.totale_ivato, r.totale_ivato) as tot_ivato,\n";
        sql = sql + " if(t.tipo_fattura = 3, -(r.totale_ivato - r.totale_imponibile), (r.totale_ivato - r.totale_imponibile)) as tot_iva \n";
        sql = sql + " from test_fatt t \n";
        sql = sql + " left join righ_fatt r on t.id = r.id_padre \n";
        sql = sql + " left join clie_forn c on t.cliente = c.codice  \n";
        sql = sql + " left join clie_forn_dest cd on t.cliente = cd.codice_cliente and t.cliente_destinazione = cd.codice \n";
        sql = sql + " left join codici_iva i on iva = i.codice \n";
        sql = sql + " left join articoli a on r.codice_articolo = a.codice \n";
        sql = sql + " left join tipi_clie_forn cat on c.tipo2 = cat.id \n";
        sql += "  where tipo_birra = 'P' or tipo_birra = 'C' \n";
        sql += "  and (year(t.data) * 100) + month(t.data) >= " + CastUtils.toString(frm.annoDal.getSelectedItem()) + CastUtils.toString(frm.meseDal.getSelectedItem()) + " \n";
        sql += "  and (year(t.data) * 100) + month(t.data) <= " + CastUtils.toString(frm.annoAl.getSelectedItem()) + CastUtils.toString(frm.meseAl.getSelectedItem()) + " \n";
        sql = sql + " group by IF(IFNULL(t.cliente_destinazione,0)=0, t.cliente, concat(t.cliente, '/',t.cliente_destinazione)), IF(tipo_birra = 'P', a.codice_fornitore, 'C'), t.data \n";
        sql += " order by tipo_birra desc, IF(IFNULL(t.cliente_destinazione,0)=0, t.cliente, concat(t.cliente, '/',t.cliente_destinazione)), a.codice_fornitore";
        System.out.println("sql = " + sql);
        List<Map> list = DbUtils.getListMap(Db.getConn(), sql);
        for (Map m : list) {
            numrec++;
            line = "";
            line += "B";
            if (CastUtils.toString(m.get("tipo_birra")).equals("P")) {
                line += s + "01";
                numrec01++;
            } else if (CastUtils.toString(m.get("tipo_birra")).equals("C")) {
                line += s + "02";
                numrec02++;
            } else {
                line += s + "??";
                System.err.println("errore in generazione file tipo_birra:" + m.get("tipo_birra") + " al numrec:" + numrec);
            }
            line += s + max(m.get("um"), 30);
            line += s + fn(m.get("sq"), 15, 2);
            line += s + fn(m.get("sqo"), 15, 2);
            line += s + fn(m.get("shl"), 15, 7);
            line += s + fn(m.get("shlo"), 15, 7);
            if (CastUtils.toString(m.get("tipo_birra")).equals("P")) {
                line += s + fn(m.get("tot_imp"), 15, 2);
            } else {
                line += s + "0";
            }
            line += s + max(m.get("cod_dest"), 15);
            line += s + max(m.get("rs_dest"), 60);
            line += s + max(m.get("persona_riferimento"), 60);
            line += s + ""; //cognome titolare
            line += s + ""; //insegna
            line += s + max(m.get("indirizzo_dest"), 60);
            line += s + ""; //civico
            line += s + max(m.get("cap_dest"), 5);
            line += s + max(m.get("loclaita_dest"), 50);
            line += s + max(m.get("prov_dest"), 2);
            line += s + ""; //regione
            line += s + max(m.get("cat_clie"), 50);
            if (CastUtils.toString(m.get("cd_pi")).length() > 0) {
                line += s + max(m.get("cd_pi"), 11);
            } else {
                if (CastUtils.toString(m.get("c_pi")).length() > 0) {
                    line += s + max(m.get("c_pi"), 11 );
                } else {
                    line += s + "ND";
                }
            }
            line += s + ""; //telefno
            line += s + ""; //fax
            line += s + ""; //email
            line += s + ""; //agente
            line += s + ""; //nome agente
            String codart = CastUtils.toString(m.get("codice_fornitore"));
            if (CastUtils.toString(m.get("tipo_birra")).equals("P")) {
                if (StringUtils.isNumeric(codart)) {
                    codart = FormatUtils.zeroFill(CastUtils.toInteger0(codart), 20);
                } else {
                    codart = max(codart, 20);
                }
            } else if (CastUtils.toString(m.get("tipo_birra")).equals("C")) {
                codart = "CONCORRENZA";
            } else {
                codart = "";
            }
            line += s + codart;
            if (CastUtils.toString(m.get("tipo_birra")).equals("P")) {
                line += s + max(m.get("a_descrizione"), 50);
            } else if (CastUtils.toString(m.get("tipo_birra")).equals("C")) {
                line += s + "CONCORRENZA";
            } else {
                line += s + "";
            }

            codart = CastUtils.toString(m.get("codice_articolo"));
            if (CastUtils.toString(m.get("tipo_birra")).equals("P")) {
                if (StringUtils.isNumeric(codart)) {
                    codart = FormatUtils.zeroFill(CastUtils.toInteger0(codart), 20);
                } else {
                    codart = max(codart, 20);
                }
            } else if (CastUtils.toString(m.get("tipo_birra")).equals("C")) {
                codart = "CONCORRENZA";
            } else {
                codart = "";
            }
            line += s + codart;
            if (CastUtils.toString(m.get("tipo_birra")).equals("P")) {
                line += s + max(m.get("r_descrizione"), 50);
            } else if (CastUtils.toString(m.get("tipo_birra")).equals("C")) {
                line += s + "CONCORRENZA";
            } else {
                line += s + "";
            }

            line += s + ""; //tipo cont
            line += s + "ND";   //tipo involucro
            line += s + "9999";   //qta involucro
            line += s + "9999";   //capacità
            line += s + formatYYYYMMDD((Date) m.get("data"));

            line += eol;
            fw.write(line);
        }

        //footer
        line = "";
        line += "F";
        line += s + fn(numrec, 15, 0);
        line += s + fn(numrec01, 15, 0);
        line += s + fn(numrec02, 15, 0);
        line += eol;
        fw.write(line);

        fw.close();
    }

    private String formatYYYYMMDD() {
        return formatYYYYMMDD(new Date());
    }

    private String formatYYYYMMDD(Date data) {
        SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd");
        return s.format(data);
    }

    private String formatYYYYMM(Date data) {
        SimpleDateFormat s = new SimpleDateFormat("yyyyMM");
        return s.format(data);
    }

    private String formatHHMMSS() {
        return formatHHMMSS(new Date());
    }

    private String formatHHMMSS(Date data) {
        SimpleDateFormat s = new SimpleDateFormat("HHmmss");
        return s.format(data);
    }

    private String max(Object v, int i) {
        String s = CastUtils.toString(v);
        if (s.length() <= i) return s;
        return s.substring(0, 30);
    }

    public static void main(String[] args) {
        System.out.println(fn(10000.12345d, 12, 5));
        System.out.println(fn(10000.12345d, 12, 2));
        System.out.println(fn(10000.12345d, 3, 5));
    }

    static private String fn(Object v, int p, int s) {
        Double d = CastUtils.toDouble0(v);
        DecimalFormat f = new DecimalFormat();
        f.setGroupingUsed(false);
        f.setMaximumIntegerDigits(p);
        f.setMaximumFractionDigits(s);
        return f.format(d);
    }
}
