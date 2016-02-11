/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gestioneFatture;

import it.tnx.Db;
import it.tnx.commons.CastUtils;
import it.tnx.commons.DateUtils;
import it.tnx.commons.DbUtils;
import it.tnx.commons.FileUtils;
import it.tnx.commons.HttpUtils;
import it.tnx.commons.MicroBench;
import it.tnx.commons.SwingUtils;
import it.tnx.commons.cu;
import it.tnx.invoicex.InvoicexUtil;
import it.tnx.shell.CurrentDir;
import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author mceccarelli
 */
public class DbChanges2 {

    Statement statLog = null;
    public List logs = null;
    Integer max = null;
    List<Map> aggs = new ArrayList();
    boolean exist_ricevute = true;
    String nome_ricevute = "_ricevute";
    String testate_ricevute = "fatture_ricevute_teste";

    public DbChanges2() {
        //controllo esistenza tabelle proc

        try {
            checkLogExtra();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            statLog = Db.getConn().createStatement();
            logs = DbUtils.getList(Db.getConn(), "select cast(concat(id_log,'|',id_plugin,'|',id_email) as CHAR) as mykey from log2");
//            System.out.println("logs = " + logs);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            if (DbUtils.existTable(Db.getConn(), "righ_fatt_acquisto")) {
                exist_ricevute = false;
                nome_ricevute = "_acquisto";
                testate_ricevute = "test_fatt_acquisto";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkLogExtra() {
        //controlla la presenza della tabella log2 e se non c'è la crea
        ArrayList<Map> log2;
        try {
            log2 = DbUtils.getListMap(Db.getConn(), "select * from log2");
            //System.out.println("log2 = " + log2);
        } catch (Exception ex) {
            System.out.println("log2 non esiste");
            creaLog2();
        }
    }

    private void creaLog2() {
        String sql = "create TABLE log2 (`id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,  `id_log` INTEGER UNSIGNED NOT NULL,  `id_plugin` VARCHAR(45),  `id_email` VARCHAR(45) NOT NULL,  `data` TIMESTAMP NOT NULL default current_timestamp,  `note` VARCHAR(45) ) ENGINE=MyISAM";
        System.out.println("sql = " + sql);
        String sql2 = "alter table log2 ADD UNIQUE INDEX `Index_2`(`id_log`, `id_email`, `id_plugin`);";
        System.out.println("sql2 = " + sql2);
        try {
            DbUtils.tryExecQuery(Db.getConn(), sql);
            DbUtils.tryExecQuery(Db.getConn(), sql2);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean agg(int id_log, String id_plugin, String id_email, String sql, String note) {
        return agg(id_log, id_plugin, id_email, sql, note, false);
    }

    private boolean agg(int id_log, String id_plugin, String id_email, String sql, String note, boolean scriviOkComunque) {
        HashMap m = new HashMap();
        m.put("id_log", id_log);
        m.put("id_plugin", id_plugin);
        m.put("id_email", id_email);
        m.put("sql", sql);
        m.put("note", note);
        m.put("okcomunque", scriviOkComunque);
        aggs.add(m);
        max = aggs.size();
        return true;
    }

    private void esegui_aggs() {
        int i = 0;
        for (Map m : aggs) {
            i++;
            int id_log = (Integer) m.get("id_log");
            String id_plugin = (String) m.get("id_plugin");
            String id_email = (String) m.get("id_email");
            String sql = (String) m.get("sql");
            String note = (String) m.get("note");
            boolean okcomunque = (Boolean) m.get("okcomunque");

            main.splash("aggiornamenti struttura database: [2/2] " + i + "/" + max, i * 100 / max);
            if (id_plugin == null) {
                id_plugin = "";
            }
            pre_check(id_log, id_plugin, id_email);

            if (sql.toLowerCase().startsWith("insert ")) {
                okcomunque = true;
            }

            if (checkLog(id_log, id_plugin, id_email) == false) {
                post_check(id_log, id_plugin, id_email);
                try {
                    if (Db.executeSql(sql)) {
                        post_execute_ok(id_log, id_plugin, id_email, sql);
                        writeLog(id_log, id_plugin, id_email, note);
                    } else {
                        if (okcomunque) {
                            writeLog(id_log, id_plugin, id_email, note);
                        }
                        post_execute_ko(id_log, id_plugin, id_email, sql);
                        System.out.println("!!! Errore in agg. db !!! " + id_log + "," + id_plugin + "," + id_email + ", sql: " + sql);
                    }
                } catch (Exception err) {
                    if (okcomunque) {
                        writeLog(id_log, id_plugin, id_email, note);
                    }
                    post_execute_ko(id_log, id_plugin, id_email, sql, err);
                    if (err.toString().indexOf("Duplicate column name") >= 0) {
//                        return true;
                    } else {
                        System.out.println("!!! Errore in agg. db !!! " + id_log + "," + id_plugin + "," + id_email + ", sql: " + sql);
                        err.printStackTrace();
//                        return false;
                    }
                }
            } else {
                post_check_ok(id_log, id_plugin, id_email);
            }
        }
    }

    private boolean aggMysql(int id_log, String id_plugin, String id_email, String sql, String note) {
        if (id_plugin == null) {
            id_plugin = "";
        }
        if (checkLog(id_log, id_plugin, id_email) == false) {
            try {
                Db.executeSql("USE mysql");
                if (Db.executeSql(sql)) {
                    Db.executeSql("USE " + Db.dbNameDB);
                    writeLog(id_log, id_plugin, id_email, note);
                } else {
                    System.out.println("!!! Errore in agg. db !!!");
                }
                Db.executeSql("USE " + Db.dbNameDB);
            } catch (Exception err) {
                Db.executeSql("USE " + Db.dbNameDB);
                if (err.toString().indexOf("Duplicate column name") >= 0) {
                    return true;
                } else {
                    System.out.println("!!! Errore in agg. db !!!");
                    err.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }

    boolean writeLog(String fileName, String desc) {
        try {
            DataOutputStream outFile = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
            outFile.writeBytes(desc + "\r\n");
            outFile.writeBytes("ok");
            outFile.close();
            return (true);
        } catch (Exception err) {
            err.printStackTrace();

            return (false);
        }
    }

    boolean writeLog(int id_log, String id_plugin, String id_email, String desc) {
        try {
            statLog.execute("insert into log2 (id_log, id_plugin, id_email, note) values (" + id_log + "," + Db.pc(id_plugin, "VARCHAR") + "," + Db.pc(id_email, "VARCHAR") + "," + Db.pc(desc, "VARCHAR") + ")");
            return (true);
        } catch (Exception err) {
            err.printStackTrace();
            return (false);
        }
    }

    boolean checkLog(int id_log, String id_plugin, String id_email) {
        return (logs.contains(id_log + "|" + id_plugin + "|" + id_email));
    }

    static public String readfrom(String string) {
        InputStream is = null;
        try {
            is = DbChanges.class.getResourceAsStream("/it/tnx/invoicex/res/" + string);
            return FileUtils.readContent(is);
        } catch (Exception e) {
        } finally {
            try {
                is.close();
            } catch (Exception e) {
            }
        }
        return null;
    }

    public void esegui_aggiornamenti() {
        MicroBench mb = new MicroBench();
        mb.start();

        System.out.println("inizio controllo aggiornamenti db log2 " + this.getClass().toString());

        try {
            //agg(1, "scontrini", "cecca@tnx.it", "ALTER TABLE fatture_ricevute_teste MODIFY COLUMN numero_doc bigint(15)", "Aumentata capienza campo numero fattura esterna");
            agg(1, "", "m.ceccarelli@tnx.it", readfrom("agg_1_mc.sql"), "nuove tabelle per ordini acquisto");
            agg(2, "", "m.ceccarelli@tnx.it", readfrom("agg_2_mc.sql"), "nuove tabelle per ordini acquisto - righe");
            agg(3, "", "m.ceccarelli@tnx.it", "alter table login modify column username varchar(250)", "username piccolo soprattutto su mac");
            agg(4, "", "m.ceccarelli@tnx.it", "alter table righ_ddt_temp modify column username varchar(250)", "username piccolo soprattutto su mac");
            agg(5, "", "m.ceccarelli@tnx.it", "alter table righ_fatt" + nome_ricevute + "_temp modify column username varchar(250)", "username piccolo soprattutto su mac");
            agg(6, "", "m.ceccarelli@tnx.it", "alter table righ_fatt_temp modify column username varchar(250)", "username piccolo soprattutto su mac");
            agg(7, "", "m.ceccarelli@tnx.it", "alter table righ_ordi_acquisto_temp modify column username varchar(250)", "username piccolo soprattutto su mac");
            agg(8, "", "m.ceccarelli@tnx.it", "alter table righ_ordi_temp modify column username varchar(250)", "username piccolo soprattutto su mac");
            agg(9, "", "m.ceccarelli@tnx.it", "alter table scadenze_sel modify column username varchar(250)", "username piccolo soprattutto su mac");

            agg(10, "", "m.ceccarelli@tnx.it", "alter table righ_ordi_acquisto add column quantita_evasa DECIMAL(8,2) NULL DEFAULT NULL", "qta evasa su ordini acquisto");
            agg(11, "", "m.ceccarelli@tnx.it", "alter table righ_ordi add column quantita_evasa DECIMAL(8,2) NULL DEFAULT NULL", "qta evasa su ordini");
            agg(12, "", "m.ceccarelli@tnx.it", "alter table test_ordi_acquisto add column evaso CHAR(1) NULL DEFAULT NULL", "ordine acq evaso"); //flag per evaso completamente
            agg(13, "", "m.ceccarelli@tnx.it", "alter table test_ordi add column evaso CHAR(1) NULL DEFAULT NULL", "ordine evaso"); //flag per evaso completamente

            agg(14, "", "m.ceccarelli@tnx.it", readfrom("agg_14_mc.sql"), "nuove tabelle per ddt acquisto");
            agg(15, "", "m.ceccarelli@tnx.it", readfrom("agg_15_mc.sql"), "nuove tabelle per ddt acquisto");
            agg(16, "", "m.ceccarelli@tnx.it", readfrom("agg_16_mc.sql"), "nuove tabelle per ddt acquisto");
            agg(17, "", "m.ceccarelli@tnx.it", readfrom("agg_17_mc.sql"), "nuove tabelle per ddt acquisto");
            //correzione movimenti ddt
            agg18();
            agg(19, "", "m.ceccarelli@tnx.it", "alter table " + testate_ricevute + " add tipo_fattura TINYINT(4) NULL DEFAULT NULL;", "conf ddt acq");
            agg(20, "", "m.ceccarelli@tnx.it", "alter table " + testate_ricevute + " add codice_listino TINYINT(3) UNSIGNED NULL DEFAULT '1';", "conf ddt acq");
            agg(21, "", "m.ceccarelli@tnx.it", "alter table " + testate_ricevute + " add banca_iban VARCHAR(100) NULL DEFAULT NULL;", "conf ddt acq");
            agg(22, "", "m.ceccarelli@tnx.it", "alter table " + testate_ricevute + " add banca_abi VARCHAR(5) NULL DEFAULT NULL", "conf ddt acq");
            agg(23, "", "m.ceccarelli@tnx.it", "alter table " + testate_ricevute + " add banca_cab VARCHAR(5) NULL DEFAULT NULL", "conf ddt acq");
            agg(24, "", "m.ceccarelli@tnx.it", "alter table " + testate_ricevute + " add banca_cc VARCHAR(35) NULL DEFAULT NULL", "conf ddt acq");
            agg(25, "", "m.ceccarelli@tnx.it", "alter table " + testate_ricevute + " add note_pagamento TEXT NULL", "conf ddt acq");

            agg(26, "", "m.ceccarelli@tnx.it", "alter table righ_ordi add in_ddt INT NULL", "campi per giac. virtuale");
            agg(27, "", "m.ceccarelli@tnx.it", "alter table righ_ordi add in_fatt INT NULL", "campi per giac. virtuale");
            agg(28, "", "m.ceccarelli@tnx.it", "alter table righ_ddt add da_ordi INT NULL", "campi per giac. virtuale");
            agg(29, "", "m.ceccarelli@tnx.it", "alter table righ_ddt add in_fatt INT NULL", "campi per giac. virtuale");
            agg(30, "", "m.ceccarelli@tnx.it", "alter table righ_fatt add da_ordi INT NULL", "campi per giac. virtuale");
            agg(31, "", "m.ceccarelli@tnx.it", "alter table righ_fatt add da_ddt INT NULL", "campi per giac. virtuale");

            agg(32, "", "m.ceccarelli@tnx.it", "alter table righ_ordi_acquisto add in_ddt INT NULL", "campi per giac. virtuale");
            agg(33, "", "m.ceccarelli@tnx.it", "alter table righ_ordi_acquisto add in_fatt INT NULL", "campi per giac. virtuale");
            agg(34, "", "m.ceccarelli@tnx.it", "alter table righ_ddt_acquisto add da_ordi INT NULL", "campi per giac. virtuale");
            agg(35, "", "m.ceccarelli@tnx.it", "alter table righ_ddt_acquisto add in_fatt INT NULL", "campi per giac. virtuale");
            agg(36, "", "m.ceccarelli@tnx.it", "alter table righ_fatt" + nome_ricevute + " add da_ordi INT NULL", "campi per giac. virtuale");
            agg(37, "", "m.ceccarelli@tnx.it", "alter table righ_fatt" + nome_ricevute + " add da_ddt INT NULL", "campi per giac. virtuale");

            agg(38, "", "m.ceccarelli@tnx.it", "alter table righ_ordi add in_ddt_riga INT NULL", "campi per giac. virtuale");
            agg(39, "", "m.ceccarelli@tnx.it", "alter table righ_ordi add in_fatt_riga INT NULL", "campi per giac. virtuale");
            agg(40, "", "m.ceccarelli@tnx.it", "alter table righ_ddt add da_ordi_riga INT NULL", "campi per giac. virtuale");
            agg(41, "", "m.ceccarelli@tnx.it", "alter table righ_ddt add in_fatt_riga INT NULL", "campi per giac. virtuale");
            agg(42, "", "m.ceccarelli@tnx.it", "alter table righ_fatt add da_ordi_riga INT NULL", "campi per giac. virtuale");
            agg(43, "", "m.ceccarelli@tnx.it", "alter table righ_fatt add da_ddt_riga INT NULL", "campi per giac. virtuale");

            agg(44, "", "m.ceccarelli@tnx.it", "alter table righ_ordi_acquisto add in_ddt_riga INT NULL", "campi per giac. virtuale");
            agg(45, "", "m.ceccarelli@tnx.it", "alter table righ_ordi_acquisto add in_fatt_riga INT NULL", "campi per giac. virtuale");
            agg(46, "", "m.ceccarelli@tnx.it", "alter table righ_ddt_acquisto add da_ordi_riga INT NULL", "campi per giac. virtuale");
            agg(47, "", "m.ceccarelli@tnx.it", "alter table righ_ddt_acquisto add in_fatt_riga INT NULL", "campi per giac. virtuale");
            agg(48, "", "m.ceccarelli@tnx.it", "alter table righ_fatt" + nome_ricevute + " add da_ordi_riga INT NULL", "campi per giac. virtuale");
            agg(49, "", "m.ceccarelli@tnx.it", "alter table righ_fatt" + nome_ricevute + " add da_ddt_riga INT NULL", "campi per giac. virtuale");

            agg(50, "", "m.ceccarelli@tnx.it", "alter table test_ordi add convertito VARCHAR(250) NULL", "conversione multipla");
            agg(51, "", "m.ceccarelli@tnx.it", "alter table test_ddt add convertito VARCHAR(250) NULL", "conversione multipla");
            agg(52, "", "m.ceccarelli@tnx.it", "alter table test_ordi_acquisto add convertito VARCHAR(250) NULL", "conversione multipla");
            agg(53, "", "m.ceccarelli@tnx.it", "alter table test_ddt_acquisto add convertito VARCHAR(250) NULL", "conversione multipla");

            agg(54, "", "m.ceccarelli@tnx.it", "alter table righ_ddt_acquisto add column quantita_evasa DECIMAL(8,2) NULL DEFAULT NULL", "qta evasa su ddt acquisto");
            agg(55, "", "m.ceccarelli@tnx.it", "alter table righ_ddt add column quantita_evasa DECIMAL(8,2) NULL DEFAULT NULL", "qta evasa su ddt");
            agg(56, "", "m.ceccarelli@tnx.it", "alter table test_ddt_acquisto add column evaso CHAR(1) NULL DEFAULT NULL", "ddt acq evaso"); //flag per evaso completamente
            agg(57, "", "m.ceccarelli@tnx.it", "alter table test_ddt add column evaso CHAR(1) NULL DEFAULT NULL", "ddt evaso"); //flag per evaso completamente

            agg(58, "", "m.ceccarelli@tnx.it", "update test_ordi set evaso = 'S' where IFNULL(doc_tipo,'') != ''", "evasione");
            agg(59, "", "m.ceccarelli@tnx.it", "update test_ordi t, righ_ordi r set r.quantita_evasa = quantita where r.id_padre = t.id and IFNULL(t.doc_tipo,'') != ''", "evasione");
            agg(60, "", "m.ceccarelli@tnx.it", "update test_ddt set evaso = 'S' where IFNULL(fattura_numero,'') != ''", "evasione");
            agg(61, "", "m.ceccarelli@tnx.it", "update test_ddt t, righ_ddt r set r.quantita_evasa = quantita where r.id_padre = t.id and IFNULL(t.fattura_numero,'') != ''", "evasione");
            agg(62, "", "a.toce@tnx.it", "alter table righ_fatt" + nome_ricevute + " add column iva_deducibile DOUBLE(5,2) NOT NULL DEFAULT 100.00", "iva deducibile su riga fatture acquisto");
            agg(63, "", "a.toce@tnx.it", "alter table stampa_iva_semplice add column imp_deducibile DOUBLE(15,5) NULL DEFAULT NULL", "imponibile deducibile su stampa registro iva");
            agg(64, "", "a.toce@tnx.it", "alter table stampa_iva_semplice add column iva_deducibile DOUBLE(15,5) NULL DEFAULT NULL", "imponibile deducibile su stampa registro iva");

//            agg(61, "", "m.ceccarelli@tnx.it", "update test_ddt t, righ_ddt r set r.quantita_evasa = quantita where r.id_padre = t.id and IFNULL(t.fattura_numero,'') != ''", "evasione");

            agg(62, "", "m.ceccarelli@tnx.it", "alter table scadenze_parziali add tipo_pagamento varchar(35) NULL", "scadenze parziali client manager");
            agg(63, "", "m.ceccarelli@tnx.it", "alter table scadenze_parziali add note TEXT NULL", "scadenze parziali client manager");

            boolean mysqlproc = false;
            try {
                if (DbUtils.tryExecQuery(Db.getConn(), "select * from mysql.proc limit 0")) {
                    mysqlproc = true;
                }
            } catch (Exception e) {
            }
            if (!mysqlproc) {
                aggMysql(64, "", "m.ceccarelli@tnx.it", readfrom("mysql_fix_privilege_tables.sql"), "aggiornamento tabelle mysql");
            }
            agg(65, "", "m.ceccarelli@tnx.it", "drop function if exists calcola_importo_netto", "funzione per calcolo importo netto riga");
            agg(66, "", "m.ceccarelli@tnx.it", readfrom("agg_66_mc.sql"), "funzione per calcolo importo netto riga");

            agg(67, "", "m.ceccarelli@tnx.it", "alter table temp_stampa_stat_ord_bol_fat add qta DECIMAL(15,5) NULL", "qta in statistiche");

            agg(67, "", "a.toce@tnx.it", "CREATE TABLE IF NOT EXISTS categorie (`id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY) ENGINE=MyISAM", "Creata tabella categorie per evitare errori");

            agg(68, "", "m.ceccarelli@tnx.it", "ALTER TABLE categorie ADD COLUMN descrizione VARCHAR(250) NULL DEFAULT NULL, ADD COLUMN id_padre INT(11) NULL DEFAULT NULL, ADD COLUMN livello INT(11) NULL DEFAULT NULL", "categorie");
            agg(69, "", "m.ceccarelli@tnx.it", "alter table articoli add cat1 INT NULL, add cat2 INT NULL, add cat3 INT NULL, add cat4 INT NULL, add cat5 INT NULL", "categorie");

            agg(70, "", "m.ceccarelli@tnx.it", "alter table articoli add modalita_vendita varchar(250) NULL, add dimensioni varchar(250) NULL, add colore varchar(250) NULL", "caratteristiche articoli");

            agg(71, "", "m.ceccarelli@tnx.it", "alter table articoli CHANGE COLUMN um_eng um_en CHAR(3) NULL DEFAULT NULL, CHANGE COLUMN descrizione_eng descrizione_en VARCHAR(255) NULL DEFAULT NULL", "internaz");

            agg(72, "", "m.ceccarelli@tnx.it", "alter table categorie add descrizione_en VARCHAR(250) NULL after descrizione, add descrizione_fr VARCHAR(250) after descrizione", "categorie");
            agg(73, "", "m.ceccarelli@tnx.it", "ALTER TABLE categorie ADD COLUMN immagine1 varchar(250) default ''", "articoli immagine");
            agg(74, "", "m.ceccarelli@tnx.it", "ALTER TABLE articoli ADD COLUMN visibile_online char(1) DEFAULT 'S'", "articoli");

            agg(75, "", "m.ceccarelli@tnx.it", "ALTER TABLE articoli ADD COLUMN codice_fornitore2 varchar(100) DEFAULT ''", "articoli codice fornitore");
            agg(76, "", "m.ceccarelli@tnx.it", "ALTER TABLE articoli ADD COLUMN codice_fornitore3 varchar(100) DEFAULT ''", "articoli codice fornitore");
            agg(77, "", "m.ceccarelli@tnx.it", "ALTER TABLE articoli ADD COLUMN codice_fornitore4 varchar(100) DEFAULT ''", "articoli codice fornitore");
            agg(78, "", "m.ceccarelli@tnx.it", "ALTER TABLE articoli ADD COLUMN codice_fornitore5 varchar(100) DEFAULT ''", "articoli codice fornitore");
            agg(75, "", "a.toce@tnx.it", "ALTER TABLE test_ordi ADD COLUMN id_ordine_ipsoa INT NULL DEFAULT NULL", "ordine ipsoa");
            agg(76, "", "a.toce@tnx.it", "ALTER TABLE clie_forn ADD COLUMN perc_sconto DECIMAL(5,2) NULL DEFAULT NULL", "percentuale di sconto prefissata");
            agg(77, "", "a.toce@tnx.it", "ALTER TABLE clie_forn ADD COLUMN flag_email INT NOT NULL DEFAULT 0", "flag per ricezione email");
            agg(78, "", "a.toce@tnx.it", "ALTER TABLE clie_forn ADD COLUMN id_cliente_ipsoa INT NULL DEFAULT NULL", "id cliente per import ipsoa");
            agg(79, "", "m.ceccarelli@tnx.it", "ALTER TABLE articoli ADD COLUMN codice_fornitore6 varchar(100) DEFAULT ''", "articoli codice fornitore");
            agg(80, "", "m.ceccarelli@tnx.it", "ALTER TABLE articoli ADD INDEX index_codice_fornitore2(codice_fornitore2)", "articoli indice codice fornitore");
            agg(81, "", "m.ceccarelli@tnx.it", "ALTER TABLE articoli ADD INDEX index_codice_fornitore3(codice_fornitore3)", "articoli indice codice fornitore");
            agg(82, "", "m.ceccarelli@tnx.it", "ALTER TABLE articoli ADD INDEX index_codice_fornitore4(codice_fornitore4)", "articoli indice codice fornitore");
            agg(83, "", "m.ceccarelli@tnx.it", "ALTER TABLE articoli ADD INDEX index_codice_fornitore5(codice_fornitore5)", "articoli indice codice fornitore");
            agg(84, "", "m.ceccarelli@tnx.it", "ALTER TABLE articoli ADD INDEX index_codice_fornitore6(codice_fornitore6)", "articoli indice codice fornitore");

            agg(85, "", "m.ceccarelli@tnx.it", "ALTER TABLE articoli ADD posizione_magazzino varchar(250)", "articoli posizione magazzino");

            agg(86, "", "m.ceccarelli@tnx.it", "alter table righ_fatt add column iva_deducibile DOUBLE(5,2) NOT NULL DEFAULT 100.00", "iva deducibile su altre tabelle");
            agg(87, "", "m.ceccarelli@tnx.it", "alter table righ_ddt add column iva_deducibile DOUBLE(5,2) NOT NULL DEFAULT 100.00", "iva deducibile su altre tabelle");
            agg(88, "", "m.ceccarelli@tnx.it", "alter table righ_ddt_acquisto add column iva_deducibile DOUBLE(5,2) NOT NULL DEFAULT 100.00", "iva deducibile su altre tabelle");
            agg(89, "", "m.ceccarelli@tnx.it", "alter table righ_ordi add column iva_deducibile DOUBLE(5,2) NOT NULL DEFAULT 100.00", "iva deducibile su altre tabelle");
            agg(90, "", "m.ceccarelli@tnx.it", "alter table righ_ordi_acquisto add column iva_deducibile DOUBLE(5,2) NOT NULL DEFAULT 100.00", "iva deducibile su altre tabelle");

            if (exist_ricevute) {
                agg(91, "", "m.ceccarelli@tnx.it", "RENAME TABLE fatture_ricevute_teste TO test_fatt_acquisto", "fatture ricevute test_fatt_acquisto");
                agg(92, "", "m.ceccarelli@tnx.it", "RENAME TABLE righ_fatt_ricevute TO righ_fatt_acquisto", "fatture ricevute test_fatt_acquisto");
                agg(93, "", "m.ceccarelli@tnx.it", "RENAME TABLE righ_fatt_ricevute_temp TO righ_fatt_acquisto_temp", "fatture ricevute test_fatt_acquisto");
                agg(94, "", "m.ceccarelli@tnx.it", "RENAME TABLE righ_fatt_ricevute_lotti TO righ_fatt_acquisto_lotti", "fatture ricevute test_fatt_acquisto");
                agg(95, "", "m.ceccarelli@tnx.it", "RENAME TABLE righ_fatt_ricevute_matricole TO righ_fatt_acquisto_matricole", "fatture ricevute test_fatt_acquisto");
            }

            agg(96, "", "m.ceccarelli@tnx.it", "update movimenti_magazzino set da_tabella = 'test_fatt_acquisto' where da_tabella = 'test_fatt_ricevute'", "fatture ricevute test_fatt_acquisto");

            agg(97, "", "m.ceccarelli@tnx.it", "alter table categorie CHANGE COLUMN descrizione nome VARCHAR(250) NULL, CHANGE COLUMN descrizione_en nome_en VARCHAR(250) NULL, CHANGE COLUMN descrizione_fr nome_fr VARCHAR(250) NULL", "categorie");

            agg(98, "", "m.ceccarelli@tnx.it", "alter table categorie add descrizione TEXT NULL, add descrizione_en TEXT NULL, add descrizione_fr TEXT NULL", "categorie");
            agg(99, "", "m.ceccarelli@tnx.it", "alter table categorie add visibile_se_registrato CHAR(1) NULL", "categorie");
            agg(100, "", "m.ceccarelli@tnx.it", "alter table articoli add visibile_se_registrato CHAR(1) NULL", "articoli web");
            agg(101, "", "m.ceccarelli@tnx.it", "ALTER TABLE articoli ADD COLUMN immagine1_web varchar(250) default '' after immagine1", "articoli immagine");

            agg(102, "", "a.toce@tnx.it", "ALTER TABLE pagamenti ADD COLUMN id_pagamento_ipsoa INT NULL DEFAULT NULL", "id tipo di pagamento Ipsoa");

            agg(103, "", "m.ceccarelli@nx.it", "alter table righ_fatt_acquisto modify column iva_deducibile DOUBLE(5,2) NULL DEFAULT 100.00", "possibilita di null");
            agg(104, "", "m.ceccarelli@tnx.it", "alter table righ_fatt modify column iva_deducibile DOUBLE(5,2) NULL DEFAULT 100.00", "possibilita di null");
            agg(105, "", "m.ceccarelli@tnx.it", "alter table righ_ddt modify column iva_deducibile DOUBLE(5,2) NULL DEFAULT 100.00", "possibilita di null");
            agg(106, "", "m.ceccarelli@tnx.it", "alter table righ_ddt_acquisto modify column iva_deducibile DOUBLE(5,2) NULL DEFAULT 100.00", "possibilita di null");
            agg(107, "", "m.ceccarelli@tnx.it", "alter table righ_ordi modify column iva_deducibile DOUBLE(5,2) NULL DEFAULT 100.00", "possibilita di null");
            agg(108, "", "m.ceccarelli@tnx.it", "alter table righ_ordi_acquisto modify column iva_deducibile DOUBLE(5,2) NULL DEFAULT 100.00", "possibilita di null");
            agg(109, "", "m.ceccarelli@tnx.it", "update righ_fatt_acquisto set iva_deducibile = null where iva_deducibile = 0", "possibilita di null");

            agg(110, "", "m.ceccarelli@tnx.it", "alter table test_ddt add column listino_consigliato varchar(10) null", "listino consigliato");
            agg(111, "", "m.ceccarelli@tnx.it", "alter table test_ordi add column listino_consigliato varchar(10) null", "listino consigliato");
            agg(112, "", "m.ceccarelli@tnx.it", "alter table test_fatt add column listino_consigliato varchar(10) null", "listino consigliato");
            agg(113, "", "m.ceccarelli@tnx.it", "alter table test_ddt_acquisto add column listino_consigliato varchar(10) null", "listino consigliato");
            agg(114, "", "m.ceccarelli@tnx.it", "alter table test_ordi_acquisto add column listino_consigliato varchar(10) null", "listino consigliato");
            agg(115, "", "m.ceccarelli@tnx.it", "alter table test_fatt_acquisto add column listino_consigliato varchar(10) null", "listino consigliato");

            agg(116, "", "a.toce@tnx.it", "ALTER TABLE dati_azienda ADD COLUMN testo_piede_fatt_v TEXT", "Aggiunte note a piede pagina su report fatture vendita");
            agg(117, "", "a.toce@tnx.it", "ALTER TABLE dati_azienda ADD COLUMN testo_piede_docu_v TEXT", "Aggiunte note a piede pagina su report ddt vendita");
            agg(118, "", "a.toce@tnx.it", "ALTER TABLE dati_azienda ADD COLUMN testo_piede_ordi_v TEXT", "Aggiunte note a piede pagina su report ordini vendita");
            agg(119, "", "a.toce@tnx.it", "ALTER TABLE dati_azienda ADD COLUMN testo_piede_fatt_a TEXT", "Aggiunte note a piede pagina su report fatture acquisto");
            agg(120, "", "a.toce@tnx.it", "ALTER TABLE dati_azienda ADD COLUMN testo_piede_docu_a TEXT", "Aggiunte note a piede pagina su report ddt acquisto");
            agg(121, "", "a.toce@tnx.it", "ALTER TABLE dati_azienda ADD COLUMN testo_piede_ordi_a TEXT", "Aggiunte note a piede pagina su report ordini acquisto");
            agg(122, "", "a.toce@tnx.it", "ALTER TABLE dati_azienda ADD COLUMN stampa_riga_invoicex INTEGER NOT NULL DEFAULT 1", "Flag per togliere riga a fondo report");

            agg(123, "", "a.toce@tnx.it", "ALTER TABLE dati_azienda ADD COLUMN immagine_firma_ordine MEDIUMBLOB NULL DEFAULT NULL", "Immagine per firma immagine");

//            agg(124, "", "test@tnx.it", "ALTER TABLE agenti ADD COLUMN test1 varchar(100) NULL DEFAULT NULL", "test");
//            agg(125, "", "test@tnx.it", "ALTER TABLE agenti ADD COLUMN test2 varchar(100) NULL DEFAULT NULL", "test");

            agg(126, "", "m.ceccarelli@tnx.it", "ALTER TABLE `dati_azienda` ADD COLUMN `logo_email` mediumblob NULL", "logo email in dati azienda");
            agg(127, "", "m.ceccarelli@tnx.it", "ALTER TABLE `dati_azienda` ADD COLUMN `sfondo` mediumblob NULL", "sfondo in dati azienda");
            agg(128, "", "m.ceccarelli@tnx.it", "ALTER TABLE `dati_azienda` ADD COLUMN `sfondo_email` mediumblob NULL", "sfondo email in dati azienda");

            agg(129, "", "m.ceccarelli@tnx.it", "ALTER TABLE `dati_azienda` ADD COLUMN `logo_nome_file` varchar(250) NULL", "logo");
            agg(130, "", "m.ceccarelli@tnx.it", "ALTER TABLE `dati_azienda` ADD COLUMN `logo_data_modifica` bigint NULL", "logo");
            agg(131, "", "m.ceccarelli@tnx.it", "ALTER TABLE `dati_azienda` ADD COLUMN `logo_dimensione` bigint NULL", "logo");

            agg(132, "", "m.ceccarelli@tnx.it", "ALTER TABLE `dati_azienda` ADD COLUMN `logo_email_nome_file` varchar(250) NULL", "logo");
            agg(133, "", "m.ceccarelli@tnx.it", "ALTER TABLE `dati_azienda` ADD COLUMN `logo_email_data_modifica` bigint NULL", "logo");
            agg(134, "", "m.ceccarelli@tnx.it", "ALTER TABLE `dati_azienda` ADD COLUMN `logo_email_dimensione` bigint NULL", "logo");

            agg(135, "", "m.ceccarelli@tnx.it", "ALTER TABLE `dati_azienda` ADD COLUMN `sfondo_nome_file` varchar(250) NULL", "sfondo");
            agg(136, "", "m.ceccarelli@tnx.it", "ALTER TABLE `dati_azienda` ADD COLUMN `sfondo_data_modifica` bigint NULL", "sfondo");
            agg(137, "", "m.ceccarelli@tnx.it", "ALTER TABLE `dati_azienda` ADD COLUMN `sfondo_dimensione` bigint NULL", "sfondo");

            agg(138, "", "m.ceccarelli@tnx.it", "ALTER TABLE `dati_azienda` ADD COLUMN `sfondo_email_nome_file` varchar(250) NULL", "sfondo");
            agg(139, "", "m.ceccarelli@tnx.it", "ALTER TABLE `dati_azienda` ADD COLUMN `sfondo_email_data_modifica` bigint NULL", "sfondo");
            agg(140, "", "m.ceccarelli@tnx.it", "ALTER TABLE `dati_azienda` ADD COLUMN `sfondo_email_dimensione` bigint NULL", "sfondo");

            agg(141, "", "m.ceccarelli@tnx.it", "ALTER TABLE `dati_azienda` ADD COLUMN `logo_in_db` char(1) NULL", "sfondo");

            agg(142, "", "a.toce@tnx.it", "ALTER TABLE `dati_azienda` ADD COLUMN `label_cliente` VARCHAR(100) DEFAULT 'Cliente'", "Etichetta cliente");
            agg(143, "", "a.toce@tnx.it", "ALTER TABLE `dati_azienda` ADD COLUMN `label_destinazione` VARCHAR(100) DEFAULT 'Destinazione Merce'", "Etichetta destinazione");

            agg(142, "", "m.ceccarelli@tnx.it", "ALTER TABLE `dati_azienda` drop primary key", "dati azienda pk", true);
            agg(143, "", "m.ceccarelli@tnx.it", "ALTER TABLE `dati_azienda` CHANGE COLUMN `id` `id` INT(11) NULL AUTO_INCREMENT,  ADD PRIMARY KEY (`id`)", "dati azienda pk", true);

            agg(144, "", "a.toce@tnx.it", "ALTER TABLE `clie_forn` ADD COLUMN `flag_update_listino` CHAR NOT NULL DEFAULT 'N'", "Aggiunto flag per aggiornamento automatico listino");
            agg(144, "", "m.ceccarelli@tnx.it", "ALTER TABLE `pagamenti` add COLUMN `iva30gg` char(1) NULL default 'N'", "iva30gg su pagamenti");
            agg(145, "", "m.ceccarelli@tnx.it", "update tipi_fatture set descrizione = 'FATTURA' where descrizione_breve = 'FI'", "tolto immediata da fattura");

            agg(145, "", "a.toce@tnx.it", "ALTER TABLE `test_fatt` ADD COLUMN `color` VARCHAR(20) DEFAULT ''", "Aggiunta colonna per segnalazione colore");

            //modifiche tux
            agg(146, "", "a.toce@tnx.it", "ALTER TABLE `righ_fatt` ADD COLUMN `arrotondamento_parametro` VARCHAR(4) DEFAULT '0'", "Aggiunta colonna per salvataggio parametro di arrotondamento su righe fattura");
            agg(147, "", "a.toce@tnx.it", "ALTER TABLE `righ_fatt` ADD COLUMN `arrotondamento_tipo` VARCHAR(4) DEFAULT ''", "Aggiunta colonna per salvataggio tipo di arrotondamento su righe fattura");
            agg(148, "", "a.toce@tnx.it", "ALTER TABLE `righ_fatt` ADD COLUMN `totale_ivato` DECIMAL(15,5) NOT NULL DEFAULT '0.00000'", "Aggiunto totale iva arrotondato su righe fattura");
            agg(149, "", "a.toce@tnx.it", "ALTER TABLE `righ_fatt` ADD COLUMN `totale_imponibile` DECIMAL(15,5) NOT NULL DEFAULT '0.00000'", "Aggiunto totale iva arrotondato su righe fattura");

            agg(150, "", "a.toce@tnx.it", "ALTER TABLE `righ_ordi` ADD COLUMN `arrotondamento_parametro` VARCHAR(4) DEFAULT '0'", "Aggiunta colonna per salvataggio parametro di arrotondamento su righe ordini");
            agg(151, "", "a.toce@tnx.it", "ALTER TABLE `righ_ordi` ADD COLUMN `arrotondamento_tipo` VARCHAR(4) DEFAULT ''", "Aggiunta colonna per salvataggio tipo di arrotondamento su righe ordini");
            agg(152, "", "a.toce@tnx.it", "ALTER TABLE `righ_ordi` ADD COLUMN `totale_ivato` DECIMAL(15,5) NOT NULL DEFAULT '0.00000'", "Aggiunto totale iva arrotondato su righe ordini");
            agg(153, "", "a.toce@tnx.it", "ALTER TABLE `righ_ordi` ADD COLUMN `totale_imponibile` DECIMAL(15,5) NOT NULL DEFAULT '0.00000'", "Aggiunto totale iva arrotondato su righe ordini");

            agg(154, "", "a.toce@tnx.it", "ALTER TABLE `righ_ddt` ADD COLUMN `arrotondamento_parametro` VARCHAR(4) DEFAULT '0'", "Aggiunta colonna per salvataggio parametro di arrotondamento su righe ddt");
            agg(155, "", "a.toce@tnx.it", "ALTER TABLE `righ_ddt` ADD COLUMN `arrotondamento_tipo` VARCHAR(4) DEFAULT ''", "Aggiunta colonna per salvataggio tipo di arrotondamento su righe ddt");
            agg(156, "", "a.toce@tnx.it", "ALTER TABLE `righ_ddt` ADD COLUMN `totale_ivato` DECIMAL(15,5) NOT NULL DEFAULT '0.00000'", "Aggiunto totale iva arrotondato su righe ddt");
            agg(157, "", "a.toce@tnx.it", "ALTER TABLE `righ_ddt` ADD COLUMN `totale_imponibile` DECIMAL(15,5) NOT NULL DEFAULT '0.00000'", "Aggiunto totale iva arrotondato su righe ddt");

            agg(158, "", "a.toce@tnx.it", "ALTER TABLE `righ_fatt_acquisto` ADD COLUMN `arrotondamento_parametro` VARCHAR(4) DEFAULT '0'", "Aggiunta colonna per salvataggio parametro di arrotondamento su righe fattura_acquisto");
            agg(159, "", "a.toce@tnx.it", "ALTER TABLE `righ_fatt_acquisto` ADD COLUMN `arrotondamento_tipo` VARCHAR(4) DEFAULT ''", "Aggiunta colonna per salvataggio tipo di arrotondamento su righe fattura_acquisto");
            agg(160, "", "a.toce@tnx.it", "ALTER TABLE `righ_fatt_acquisto` ADD COLUMN `totale_ivato` DECIMAL(15,5) NOT NULL DEFAULT '0.00000'", "Aggiunto totale iva arrotondato su righe fattura_acquisto");
            agg(161, "", "a.toce@tnx.it", "ALTER TABLE `righ_fatt_acquisto` ADD COLUMN `totale_imponibile` DECIMAL(15,5) NOT NULL DEFAULT '0.00000'", "Aggiunto totale iva arrotondato su righe fattura_acquisto");

            agg(162, "", "a.toce@tnx.it", "ALTER TABLE `righ_ordi_acquisto` ADD COLUMN `arrotondamento_parametro` VARCHAR(4) DEFAULT '0'", "Aggiunta colonna per salvataggio parametro di arrotondamento su righe ordini_acquisto");
            agg(163, "", "a.toce@tnx.it", "ALTER TABLE `righ_ordi_acquisto` ADD COLUMN `arrotondamento_tipo` VARCHAR(4) DEFAULT ''", "Aggiunta colonna per salvataggio tipo di arrotondamento su righe ordini_acquisto");
            agg(164, "", "a.toce@tnx.it", "ALTER TABLE `righ_ordi_acquisto` ADD COLUMN `totale_ivato` DECIMAL(15,5) NOT NULL DEFAULT '0.00000'", "Aggiunto totale iva arrotondato su righe ordini_acquisto");
            agg(165, "", "a.toce@tnx.it", "ALTER TABLE `righ_ordi_acquisto` ADD COLUMN `totale_imponibile` DECIMAL(15,5) NOT NULL DEFAULT '0.00000'", "Aggiunto totale iva arrotondato su righe ordini_acquisto");

            agg(166, "", "a.toce@tnx.it", "ALTER TABLE `righ_ddt_acquisto` ADD COLUMN `arrotondamento_parametro` VARCHAR(4) DEFAULT '0'", "Aggiunta colonna per salvataggio parametro di arrotondamento su righe ddt_acquisto");
            agg(167, "", "a.toce@tnx.it", "ALTER TABLE `righ_ddt_acquisto` ADD COLUMN `arrotondamento_tipo` VARCHAR(4) DEFAULT ''", "Aggiunta colonna per salvataggio tipo di arrotondamento su righe ddt_acquisto");
            agg(168, "", "a.toce@tnx.it", "ALTER TABLE `righ_ddt_acquisto` ADD COLUMN `totale_ivato` DECIMAL(15,5) NOT NULL DEFAULT '0.00000'", "Aggiunto totale iva arrotondato su righe ddt_acquisto");
            agg(169, "", "a.toce@tnx.it", "ALTER TABLE `righ_ddt_acquisto` ADD COLUMN `totale_imponibile` DECIMAL(15,5) NOT NULL DEFAULT '0.00000'", "Aggiunto totale iva arrotondato su righe ddt_acquisto");

            agg(170, "", "a.toce@tnx.it", "UPDATE righ_fatt set totale_imponibile = CAST(IF(arrotondamento_parametro = '0', (prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - (prezzo*(IFNULL(sconto2, 0.00)/100))) * IFNULL(quantita, 0.00), IF(arrotondamento_tipo = 'Inf.', FLOOR(((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - (prezzo*(IFNULL(sconto2, 0.00)/100))) )/CAST(arrotondamento_parametro AS DECIMAL(5,2))) * CAST(arrotondamento_parametro AS DECIMAL(5,2)) * IFNULL(quantita, 0.00), CEIL(((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - (prezzo*(IFNULL(sconto2, 0.00)/100))) )/CAST(arrotondamento_parametro AS DECIMAL(5,2))) * CAST(arrotondamento_parametro AS DECIMAL(5,2)) * IFNULL(quantita, 0.00))) AS DECIMAL(10,2))", "Aggiornamento totali imponibili di riga fattura");

            agg(171, "", "a.toce@tnx.it", "UPDATE righ_ordi set totale_imponibile = CAST(IF(arrotondamento_parametro = '0', (prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - (prezzo*(IFNULL(sconto2, 0.00)/100))) * IFNULL(quantita, 0.00), IF(arrotondamento_tipo = 'Inf.', FLOOR(((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - (prezzo*(IFNULL(sconto2, 0.00)/100))) )/CAST(arrotondamento_parametro AS DECIMAL(5,2))) * CAST(arrotondamento_parametro AS DECIMAL(5,2)) * IFNULL(quantita, 0.00), CEIL(((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - (prezzo*(IFNULL(sconto2, 0.00)/100))) )/CAST(arrotondamento_parametro AS DECIMAL(5,2))) * CAST(arrotondamento_parametro AS DECIMAL(5,2)) * IFNULL(quantita, 0.00))) AS DECIMAL(10,2))", "Aggiornamento totali imponibili di riga ordine");
            agg(172, "", "a.toce@tnx.it", "UPDATE righ_ordi r LEFT JOIN codici_iva i ON r.iva = i.codice SET totale_ivato = totale_imponibile + (totale_imponibile * i.percentuale/100)", "Aggiornati totali iva righe ordine");

            agg(173, "", "a.toce@tnx.it", "UPDATE righ_ddt set totale_imponibile = CAST(IF(arrotondamento_parametro = '0', (prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - (prezzo*(IFNULL(sconto2, 0.00)/100))) * IFNULL(quantita, 0.00), IF(arrotondamento_tipo = 'Inf.', FLOOR(((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - (prezzo*(IFNULL(sconto2, 0.00)/100))) )/CAST(arrotondamento_parametro AS DECIMAL(5,2))) * CAST(arrotondamento_parametro AS DECIMAL(5,2)) * IFNULL(quantita, 0.00), CEIL(((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - (prezzo*(IFNULL(sconto2, 0.00)/100))) )/CAST(arrotondamento_parametro AS DECIMAL(5,2))) * CAST(arrotondamento_parametro AS DECIMAL(5,2)) * IFNULL(quantita, 0.00))) AS DECIMAL(10,2))", "Aggiornamento totali imponibili di riga ddt");
            agg(174, "", "a.toce@tnx.it", "UPDATE righ_ddt r LEFT JOIN codici_iva i ON r.iva = i.codice SET totale_ivato = totale_imponibile + (totale_imponibile * i.percentuale/100)", "Aggiornati totali iva righe ddt");

            agg(175, "", "a.toce@tnx.it", "UPDATE righ_fatt_acquisto set totale_imponibile = CAST(IF(arrotondamento_parametro = '0', (prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - (prezzo*(IFNULL(sconto2, 0.00)/100))) * IFNULL(quantita, 0.00), IF(arrotondamento_tipo = 'Inf.', FLOOR(((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - (prezzo*(IFNULL(sconto2, 0.00)/100))) )/CAST(arrotondamento_parametro AS DECIMAL(5,2))) * CAST(arrotondamento_parametro AS DECIMAL(5,2)) * IFNULL(quantita, 0.00), CEIL(((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - (prezzo*(IFNULL(sconto2, 0.00)/100))) )/CAST(arrotondamento_parametro AS DECIMAL(5,2))) * CAST(arrotondamento_parametro AS DECIMAL(5,2)) * IFNULL(quantita, 0.00))) AS DECIMAL(10,2))", "Aggiornamento totali imponibili di riga fattura acquisto");
            agg(176, "", "a.toce@tnx.it", "UPDATE righ_fatt_acquisto r LEFT JOIN codici_iva i ON r.iva = i.codice SET totale_ivato = totale_imponibile + (totale_imponibile * i.percentuale/100)", "Aggiornati totali iva righe fattura acquisto");

            agg(177, "", "a.toce@tnx.it", "UPDATE righ_ordi_acquisto set totale_imponibile = CAST(IF(arrotondamento_parametro = '0', (prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - (prezzo*(IFNULL(sconto2, 0.00)/100))) * IFNULL(quantita, 0.00), IF(arrotondamento_tipo = 'Inf.', FLOOR(((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - (prezzo*(IFNULL(sconto2, 0.00)/100))) )/CAST(arrotondamento_parametro AS DECIMAL(5,2))) * CAST(arrotondamento_parametro AS DECIMAL(5,2)) * IFNULL(quantita, 0.00), CEIL(((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - (prezzo*(IFNULL(sconto2, 0.00)/100))) )/CAST(arrotondamento_parametro AS DECIMAL(5,2))) * CAST(arrotondamento_parametro AS DECIMAL(5,2)) * IFNULL(quantita, 0.00))) AS DECIMAL(10,2))", "Aggiornamento totali imponibili di riga ordine acquisto");
            agg(178, "", "a.toce@tnx.it", "UPDATE righ_ordi_acquisto r LEFT JOIN codici_iva i ON r.iva = i.codice SET totale_ivato = totale_imponibile + (totale_imponibile * i.percentuale/100)", "Aggiornati totali iva righe ordine acquisto");

            agg(179, "", "a.toce@tnx.it", "UPDATE righ_ddt_acquisto set totale_imponibile = CAST(IF(arrotondamento_parametro = '0', (prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - (prezzo*(IFNULL(sconto2, 0.00)/100))) * IFNULL(quantita, 0.00), IF(arrotondamento_tipo = 'Inf.', FLOOR(((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - (prezzo*(IFNULL(sconto2, 0.00)/100))) )/CAST(arrotondamento_parametro AS DECIMAL(5,2))) * CAST(arrotondamento_parametro AS DECIMAL(5,2)) * IFNULL(quantita, 0.00), CEIL(((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - (prezzo*(IFNULL(sconto2, 0.00)/100))) )/CAST(arrotondamento_parametro AS DECIMAL(5,2))) * CAST(arrotondamento_parametro AS DECIMAL(5,2)) * IFNULL(quantita, 0.00))) AS DECIMAL(10,2))", "Aggiornamento totali imponibili di riga ddt acquisto");
            agg(180, "", "a.toce@tnx.it", "UPDATE righ_ddt_acquisto r LEFT JOIN codici_iva i ON r.iva = i.codice SET totale_ivato = totale_imponibile + (totale_imponibile * i.percentuale/100)", "Aggiornati totali iva righe ddt acquisto");

            agg(181, "", "a.toce@tnx.it", "ALTER TABLE articoli ADD COLUMN is_descrizione CHAR NOT NULL DEFAULT 'N'", "Aggiunta colonna per descrizioni in articoli");
            agg(182, "", "a.toce@tnx.it", "ALTER TABLE righ_fatt ADD COLUMN is_descrizione CHAR NOT NULL DEFAULT 'N'", "Aggiunta colonna per descrizioni in righe fatture");
            agg(183, "", "a.toce@tnx.it", "ALTER TABLE righ_ordi ADD COLUMN is_descrizione CHAR NOT NULL DEFAULT 'N'", "Aggiunta colonna per descrizioni in righe ordini");
            agg(184, "", "a.toce@tnx.it", "ALTER TABLE righ_ddt ADD COLUMN is_descrizione CHAR NOT NULL DEFAULT 'N'", "Aggiunta colonna per descrizioni in righe ddt");
            agg(185, "", "a.toce@tnx.it", "ALTER TABLE righ_fatt_acquisto ADD COLUMN is_descrizione CHAR NOT NULL DEFAULT 'N'", "Aggiunta colonna per descrizioni in righe fatture di acquistO");
            agg(186, "", "a.toce@tnx.it", "ALTER TABLE righ_ordi_acquisto ADD COLUMN is_descrizione CHAR NOT NULL DEFAULT 'N'", "Aggiunta colonna per descrizioni in righe ordini di acquistO");
            agg(187, "", "a.toce@tnx.it", "ALTER TABLE righ_ddt_acquisto ADD COLUMN is_descrizione CHAR NOT NULL DEFAULT 'N'", "Aggiunta colonna per descrizioni in righe ddt di acquistO");

            agg(188, "", "a.toce@tnx.it", "UPDATE righ_fatt r LEFT JOIN codici_iva i ON r.iva = i.codice SET totale_ivato = totale_imponibile + (totale_imponibile * i.percentuale/100)", "Aggiornati totali iva righe fattura");

            //modifiche cecca
            agg(146, "", "m.ceccarelli@tnx.it", "alter table movimenti_magazzino add column da_tipo_fattura tinyint", "movimenti magazzino per riconoscere scontrini");

            agg(147, "", "m.ceccarelli@tnx.it", "ALTER TABLE `dati_azienda` ADD COLUMN `codiceIvaDefault` VARCHAR(10)", "codiceIvaDefault");
            agg(148, "", "m.ceccarelli@tnx.it", "ALTER TABLE `dati_azienda` ADD COLUMN `codiceIvaSpese` VARCHAR(10)", "codiceIvaSpese");
            agg(149, "", "m.ceccarelli@tnx.it", "ALTER TABLE `dati_azienda` ADD COLUMN `iva21eseguito` CHAR(1)", "iva21eseguito");
            agg(150, "", "m.ceccarelli@tnx.it", "ALTER TABLE `dati_azienda` ADD COLUMN `iva21a20eseguito` CHAR(1)", "iva21a20eseguito");

            //porto impostazioni iva su db
            String codiceIvaDefault = null;
            String codiceIvaSpese = null;
            try {
                codiceIvaDefault = main.fileIni.getValue("iva", "codiceIvaDefault", null);
                codiceIvaSpese = main.fileIni.getValue("iva", "codiceIvaSpese", null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            agg(151, "", "m.ceccarelli@tnx.it", "update dati_azienda set codiceIvaDefault = " + Db.pcs(codiceIvaDefault), "codiceIvaDefault");
            agg(152, "", "m.ceccarelli@tnx.it", "update dati_azienda set codiceIvaSpese = " + Db.pcs(codiceIvaSpese), "codiceIvaSpese");

            if (!(DbUtils.containRows(Db.getConn(), "select * from codici_iva where codice = '21'"))) {
                agg(153, "", "m.ceccarelli@tnx.it", "insert into codici_iva set codice = '21', percentuale = 21, descrizione = 'Iva 21%', descrizione_breve = 'Iva 21%'", "iva21");
            }

            agg(154, "", "m.ceccarelli@tnx.it", "CREATE TABLE versioni_clients (hostname VARCHAR(250) not NULL primary key,versione VARCHAR(20) NULL, pacchetto VARCHAR(100) NULL, tempo TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP) ENGINE=MyISAM", "tabella per versioni clients di invoicex");
            agg(155, "", "m.ceccarelli@tnx.it", "ALTER TABLE articoli_prezzi ADD COLUMN sconto1 DECIMAL(5,2) NULL, ADD COLUMN sconto2 DECIMAL(5,2) NULL;", "sconti su articoli");
            agg(156, "", "m.ceccarelli@tnx.it", "ALTER TABLE clie_forn ADD COLUMN sconto1t DECIMAL(5,2) NULL, ADD COLUMN sconto2t DECIMAL(5,2) NULL, ADD COLUMN sconto3t DECIMAL(5,2) NULL, ADD COLUMN sconto1r DECIMAL(5,2) NULL, ADD COLUMN sconto2r DECIMAL(5,2) NULL;", "sconti su cliente");

            agg(157, "", "m.ceccarelli@tnx.it", "ALTER TABLE articoli ADD INDEX descrizione_25 (descrizione(25))", "indicie su descrizione articoli");

            //progblema aggiornamento totali righe
            agg(158, "", "m.ceccarelli@tnx.it", "UPDATE righ_fatt set totale_imponibile = ROUND(CAST(  IF(arrotondamento_parametro = '0',  (prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - ((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)))*(IFNULL(sconto2, 0.00)/100))) * IFNULL(quantita, 0.00), IF(arrotondamento_tipo = 'Inf.',   FLOOR(((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - ((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)))*(IFNULL(sconto2, 0.00)/100))) )/CAST(arrotondamento_parametro AS DECIMAL(5,2))) * CAST(arrotondamento_parametro AS DECIMAL(5,2)) * IFNULL(quantita, 0.00), CEIL(((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - ((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)))*(IFNULL(sconto2, 0.00)/100))) )/CAST(arrotondamento_parametro AS DECIMAL(5,2))) * CAST(arrotondamento_parametro AS DECIMAL(5,2)) * IFNULL(quantita, 0.00))) AS DECIMAL(10,2)),2)", "agg2 totali");
            agg(159, "", "m.ceccarelli@tnx.it", "UPDATE righ_ddt set totale_imponibile = ROUND(CAST(  IF(arrotondamento_parametro = '0',  (prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - ((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)))*(IFNULL(sconto2, 0.00)/100))) * IFNULL(quantita, 0.00), IF(arrotondamento_tipo = 'Inf.',   FLOOR(((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - ((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)))*(IFNULL(sconto2, 0.00)/100))) )/CAST(arrotondamento_parametro AS DECIMAL(5,2))) * CAST(arrotondamento_parametro AS DECIMAL(5,2)) * IFNULL(quantita, 0.00), CEIL(((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - ((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)))*(IFNULL(sconto2, 0.00)/100))) )/CAST(arrotondamento_parametro AS DECIMAL(5,2))) * CAST(arrotondamento_parametro AS DECIMAL(5,2)) * IFNULL(quantita, 0.00))) AS DECIMAL(10,2)),2)", "agg2 totali");
            agg(160, "", "m.ceccarelli@tnx.it", "UPDATE righ_ordi set totale_imponibile = ROUND(CAST(  IF(arrotondamento_parametro = '0',  (prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - ((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)))*(IFNULL(sconto2, 0.00)/100))) * IFNULL(quantita, 0.00), IF(arrotondamento_tipo = 'Inf.',   FLOOR(((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - ((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)))*(IFNULL(sconto2, 0.00)/100))) )/CAST(arrotondamento_parametro AS DECIMAL(5,2))) * CAST(arrotondamento_parametro AS DECIMAL(5,2)) * IFNULL(quantita, 0.00), CEIL(((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - ((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)))*(IFNULL(sconto2, 0.00)/100))) )/CAST(arrotondamento_parametro AS DECIMAL(5,2))) * CAST(arrotondamento_parametro AS DECIMAL(5,2)) * IFNULL(quantita, 0.00))) AS DECIMAL(10,2)),2)", "agg2 totali");
            agg(161, "", "m.ceccarelli@tnx.it", "UPDATE righ_fatt_acquisto set totale_imponibile = ROUND(CAST(  IF(arrotondamento_parametro = '0',  (prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - ((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)))*(IFNULL(sconto2, 0.00)/100))) * IFNULL(quantita, 0.00), IF(arrotondamento_tipo = 'Inf.',   FLOOR(((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - ((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)))*(IFNULL(sconto2, 0.00)/100))) )/CAST(arrotondamento_parametro AS DECIMAL(5,2))) * CAST(arrotondamento_parametro AS DECIMAL(5,2)) * IFNULL(quantita, 0.00), CEIL(((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - ((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)))*(IFNULL(sconto2, 0.00)/100))) )/CAST(arrotondamento_parametro AS DECIMAL(5,2))) * CAST(arrotondamento_parametro AS DECIMAL(5,2)) * IFNULL(quantita, 0.00))) AS DECIMAL(10,2)),2)", "agg2 totali");
            agg(162, "", "m.ceccarelli@tnx.it", "UPDATE righ_ddt_acquisto set totale_imponibile = ROUND(CAST(  IF(arrotondamento_parametro = '0',  (prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - ((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)))*(IFNULL(sconto2, 0.00)/100))) * IFNULL(quantita, 0.00), IF(arrotondamento_tipo = 'Inf.',   FLOOR(((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - ((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)))*(IFNULL(sconto2, 0.00)/100))) )/CAST(arrotondamento_parametro AS DECIMAL(5,2))) * CAST(arrotondamento_parametro AS DECIMAL(5,2)) * IFNULL(quantita, 0.00), CEIL(((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - ((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)))*(IFNULL(sconto2, 0.00)/100))) )/CAST(arrotondamento_parametro AS DECIMAL(5,2))) * CAST(arrotondamento_parametro AS DECIMAL(5,2)) * IFNULL(quantita, 0.00))) AS DECIMAL(10,2)),2)", "agg2 totali");
            agg(163, "", "m.ceccarelli@tnx.it", "UPDATE righ_ordi_acquisto set totale_imponibile = ROUND(CAST(  IF(arrotondamento_parametro = '0',  (prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - ((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)))*(IFNULL(sconto2, 0.00)/100))) * IFNULL(quantita, 0.00), IF(arrotondamento_tipo = 'Inf.',   FLOOR(((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - ((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)))*(IFNULL(sconto2, 0.00)/100))) )/CAST(arrotondamento_parametro AS DECIMAL(5,2))) * CAST(arrotondamento_parametro AS DECIMAL(5,2)) * IFNULL(quantita, 0.00), CEIL(((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - ((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)))*(IFNULL(sconto2, 0.00)/100))) )/CAST(arrotondamento_parametro AS DECIMAL(5,2))) * CAST(arrotondamento_parametro AS DECIMAL(5,2)) * IFNULL(quantita, 0.00))) AS DECIMAL(10,2)),2)", "agg2 totali");

            agg(164, "", "m.ceccarelli@tnx.it", "UPDATE righ_fatt r LEFT JOIN codici_iva i ON r.iva = i.codice SET totale_ivato = round(totale_imponibile + (totale_imponibile * i.percentuale/100),2)", "agg2 totali righe");
            agg(165, "", "m.ceccarelli@tnx.it", "UPDATE righ_ddt r LEFT JOIN codici_iva i ON r.iva = i.codice SET totale_ivato = round(totale_imponibile + (totale_imponibile * i.percentuale/100),2)", "agg2 totali righe");
            agg(166, "", "m.ceccarelli@tnx.it", "UPDATE righ_ordi r LEFT JOIN codici_iva i ON r.iva = i.codice SET totale_ivato = round(totale_imponibile + (totale_imponibile * i.percentuale/100),2)", "agg2 totali righe");
            agg(167, "", "m.ceccarelli@tnx.it", "UPDATE righ_fatt_acquisto r LEFT JOIN codici_iva i ON r.iva = i.codice SET totale_ivato = round(totale_imponibile + (totale_imponibile * i.percentuale/100),2)", "agg2 totali righe");
            agg(168, "", "m.ceccarelli@tnx.it", "UPDATE righ_ddt_acquisto r LEFT JOIN codici_iva i ON r.iva = i.codice SET totale_ivato = round(totale_imponibile + (totale_imponibile * i.percentuale/100),2)", "agg2 totali righe");
            agg(169, "", "m.ceccarelli@tnx.it", "UPDATE righ_ordi_acquisto r LEFT JOIN codici_iva i ON r.iva = i.codice SET totale_ivato = round(totale_imponibile + (totale_imponibile * i.percentuale/100),2)", "agg2 totali righe");

            // Creo tabella di dettaglio per personalizzazione SNJ
            // Campi per tipo ordine A
            agg(194, "", "a.toce@tnx.it", "ALTER TABLE righ_ordi ADD COLUMN percentuale INTEGER NULL DEFAULT NULL", "Aggiunto campo percentuale");
            agg(195, "", "a.toce@tnx.it", "ALTER TABLE righ_ordi ADD COLUMN emissione_fattura INTEGER UNSIGNED NULL DEFAULT NULL", "Aggiunto campo emissione_fattura");
            agg(196, "", "a.toce@tnx.it", "ALTER TABLE righ_ordi ADD COLUMN termini_pagamento VARCHAR(35) NULL DEFAULT NULL", "Aggiunto campo tipo di pagamento");
            agg(197, "", "a.toce@tnx.it", "ALTER TABLE righ_ordi ADD COLUMN imponibile DECIMAL(12,5) NULL DEFAULT NULL", "Aggiunto campo imponibile");
            // Campi per tipo ordine B
            agg(198, "", "a.toce@tnx.it", "ALTER TABLE righ_ordi ADD COLUMN costo_giornaliero DECIMAL(12,5) NULL DEFAULT NULL", "Aggiunto campo imponibile");
            agg(199, "", "a.toce@tnx.it", "ALTER TABLE righ_ordi ADD COLUMN costo_mensile DECIMAL(12,5) NULL DEFAULT NULL", "Aggiunto campo imponibile");
            agg(200, "", "a.toce@tnx.it", "ALTER TABLE righ_ordi ADD COLUMN durata_consulenza INTEGER UNSIGNED NULL DEFAULT NULL", "Aggiunto campo imponibile");
            agg(201, "", "a.toce@tnx.it", "ALTER TABLE righ_ordi ADD COLUMN durata_contratto INTEGER UNSIGNED NULL DEFAULT NULL", "Aggiunto campo imponibile");

            // Tabelle per le anagrafiche di SNJ
            agg(202, "", "a.toce@tnx.it", "CREATE TABLE tipi_durata_consulenza (`id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,  `descrizione` VARCHAR(255) NOT NULL) ENGINE=MyISAM", "Creazione tabella per anagrafica durata consulenza");
            agg(203, "", "a.toce@tnx.it", "CREATE TABLE tipi_durata_contratto (`id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,  `descrizione` VARCHAR(255) NOT NULL) ENGINE=MyISAM", "Creazione tabella per anagrafica durata contratto");
            agg(204, "", "a.toce@tnx.it", "CREATE TABLE tipi_emissione_fattura (`id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,  `descrizione` VARCHAR(255) NOT NULL) ENGINE=MyISAM", "Creazione tabella per anagrafica emissione fattura");
            agg(205, "", "a.toce@tnx.it", "CREATE TABLE stati_preventivo_ordine (`id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,  `descrizione` VARCHAR(255) NOT NULL) ENGINE=MyISAM", "Creazione tabella per anagrafica stati prev./ordine");

            agg(207, "", "a.toce@tnx.it", "INSERT INTO stati_preventivo_ordine SET descrizione = 'Preventivo'", "Inserimento Stati Preventivo/Ordine");
            agg(208, "", "a.toce@tnx.it", "INSERT INTO stati_preventivo_ordine SET descrizione = 'Preventivo Inviato'", "Inserimento Stati Preventivo/Ordine");
            agg(209, "", "a.toce@tnx.it", "INSERT INTO stati_preventivo_ordine SET descrizione = 'Preventivo Accettato'", "Inserimento Stati Preventivo/Ordine");
            agg(210, "", "a.toce@tnx.it", "INSERT INTO stati_preventivo_ordine SET descrizione = 'Conferma d\\'Ordine'", "Inserimento Stati Preventivo/Ordine");
            agg(211, "", "a.toce@tnx.it", "INSERT INTO stati_preventivo_ordine SET descrizione = 'Ordine'", "Inserimento Stati Preventivo/Ordine");
            agg(212, "", "a.toce@tnx.it", "INSERT INTO stati_preventivo_ordine SET descrizione = 'Ordine Inviato'", "Inserimento Stati Preventivo/Ordine");
            agg(213, "", "a.toce@tnx.it", "INSERT INTO stati_preventivo_ordine SET descrizione = 'Ordine Accettato'", "Inserimento Stati Preventivo/Ordine");
            agg(214, "", "a.toce@tnx.it", "INSERT INTO stati_preventivo_ordine SET descrizione = 'Ordine in lavorazione - 0%'", "Inserimento Stati Preventivo/Ordine");
            agg(215, "", "a.toce@tnx.it", "INSERT INTO stati_preventivo_ordine SET descrizione = 'Ordine in lavorazione - 25%'", "Inserimento Stati Preventivo/Ordine");
            agg(216, "", "a.toce@tnx.it", "INSERT INTO stati_preventivo_ordine SET descrizione = 'Ordine in lavorazione - 50%'", "Inserimento Stati Preventivo/Ordine");
            agg(217, "", "a.toce@tnx.it", "INSERT INTO stati_preventivo_ordine SET descrizione = 'Ordine in lavorazione - 75%'", "Inserimento Stati Preventivo/Ordine");
            agg(218, "", "a.toce@tnx.it", "INSERT INTO stati_preventivo_ordine SET descrizione = 'Ordine in lavorazione - Sospeso'", "Inserimento Stati Preventivo/Ordine");
            agg(219, "", "a.toce@tnx.it", "INSERT INTO stati_preventivo_ordine SET descrizione = 'Ordine Completato'", "Inserimento Stati Preventivo/Ordine");
            agg(220, "", "a.toce@tnx.it", "INSERT INTO stati_preventivo_ordine SET descrizione = 'Ordine Fatturato'", "Inserimento Stati Preventivo/Ordine");
            agg(221, "", "a.toce@tnx.it", "INSERT INTO stati_preventivo_ordine SET descrizione = 'Consuntivo in lavorazione'", "Inserimento Stati Preventivo/Ordine");

            agg(222, "", "a.toce@tnx.it", "INSERT INTO tipi_emissione_fattura SET descrizione = 'All\\'Ordine'", "Inserimento Tipi di Emissione Fattura");
            agg(223, "", "a.toce@tnx.it", "INSERT INTO tipi_emissione_fattura SET descrizione = 'Alla Consegna'", "Inserimento Tipi di Emissione Fattura");
            agg(224, "", "a.toce@tnx.it", "INSERT INTO tipi_emissione_fattura SET descrizione = 'Al Completamento'", "Inserimento Tipi di Emissione Fattura");
            agg(225, "", "a.toce@tnx.it", "INSERT INTO tipi_emissione_fattura SET descrizione = 'Consuntivo Fine Mese'", "Inserimento Tipi di Emissione Fattura");
            agg(226, "", "a.toce@tnx.it", "INSERT INTO tipi_emissione_fattura SET descrizione = 'A Fine Evento'", "Inserimento Tipi di Emissione Fattura");
            agg(227, "", "a.toce@tnx.it", "INSERT INTO tipi_emissione_fattura SET descrizione = 'Accettazione Grafica'", "Inserimento Tipi di Emissione Fattura");
            agg(228, "", "a.toce@tnx.it", "INSERT INTO tipi_emissione_fattura SET descrizione = 'Mensile anticipata'", "Inserimento Tipi di Emissione Fattura");
            agg(229, "", "a.toce@tnx.it", "INSERT INTO tipi_emissione_fattura SET descrizione = 'Bimestrale Anticpata'", "Inserimento Tipi di Emissione Fattura");
            agg(230, "", "a.toce@tnx.it", "INSERT INTO tipi_emissione_fattura SET descrizione = 'Ogni Fine Mese'", "Inserimento Tipi di Emissione Fattura");
            agg(231, "", "a.toce@tnx.it", "INSERT INTO tipi_emissione_fattura SET descrizione = 'Ogni Fine Bimestre'", "Inserimento Tipi di Emissione Fattura");

            agg(232, "", "a.toce@tnx.it", "INSERT INTO tipi_durata_consulenza SET descrizione = 'Primi 5 gg'", "Inserimento Tipi di Durata Consulenza");
            agg(233, "", "a.toce@tnx.it", "INSERT INTO tipi_durata_consulenza SET descrizione = 'Primi 10 gg'", "Inserimento Tipi di Durata Consulenza");
            agg(234, "", "a.toce@tnx.it", "INSERT INTO tipi_durata_consulenza SET descrizione = 'Dal 6 gg al 15gg'", "Inserimento Tipi di Durata Consulenza");
            agg(235, "", "a.toce@tnx.it", "INSERT INTO tipi_durata_consulenza SET descrizione = 'Dal 15 gg in poi'", "Inserimento Tipi di Durata Consulenza");
            agg(236, "", "a.toce@tnx.it", "INSERT INTO tipi_durata_consulenza SET descrizione = 'Dal 5 gg in poi'", "Inserimento Tipi di Durata Consulenza");
            agg(237, "", "a.toce@tnx.it", "INSERT INTO tipi_durata_consulenza SET descrizione = 'Dal 10 gg in poi'", "Inserimento Tipi di Durata Consulenza");
            agg(238, "", "a.toce@tnx.it", "INSERT INTO tipi_durata_consulenza SET descrizione = 'Giornaliera'", "Inserimento Tipi di Durata Consulenza");
            agg(239, "", "a.toce@tnx.it", "INSERT INTO tipi_durata_consulenza SET descrizione = '4 gg al mese x 6 mesi'", "Inserimento Tipi di Durata Consulenza");
            agg(240, "", "a.toce@tnx.it", "INSERT INTO tipi_durata_consulenza SET descrizione = '8 gg al mese x 6 mesi'", "Inserimento Tipi di Durata Consulenza");
            agg(241, "", "a.toce@tnx.it", "INSERT INTO tipi_durata_consulenza SET descrizione = '12 gg al mese x 6 mesi'", "Inserimento Tipi di Durata Consulenza");
            agg(242, "", "a.toce@tnx.it", "INSERT INTO tipi_durata_consulenza SET descrizione = '4 gg al mese x 12 mesi'", "Inserimento Tipi di Durata Consulenza");
            agg(243, "", "a.toce@tnx.it", "INSERT INTO tipi_durata_consulenza SET descrizione = '8 gg al mese x 12 mesi'", "Inserimento Tipi di Durata Consulenza");
            agg(244, "", "a.toce@tnx.it", "INSERT INTO tipi_durata_consulenza SET descrizione = '12 gg al mese x 12 mesi'", "Inserimento Tipi di Durata Consulenza");
            agg(245, "", "a.toce@tnx.it", "INSERT INTO tipi_durata_consulenza SET descrizione = 'Fino a Termine Progetto'", "Inserimento Tipi di Durata Consulenza");

            agg(246, "", "a.toce@tnx.it", "INSERT INTO tipi_durata_contratto SET descrizione = 'X 2 Mesi'", "Inserimento Tipi di Durata Contratto");
            agg(247, "", "a.toce@tnx.it", "INSERT INTO tipi_durata_contratto SET descrizione = 'X 3 Mesi'", "Inserimento Tipi di Durata Contratto");
            agg(248, "", "a.toce@tnx.it", "INSERT INTO tipi_durata_contratto SET descrizione = 'X 4 Mesi'", "Inserimento Tipi di Durata Contratto");
            agg(249, "", "a.toce@tnx.it", "INSERT INTO tipi_durata_contratto SET descrizione = 'X 6 Mesi'", "Inserimento Tipi di Durata Contratto");
            agg(250, "", "a.toce@tnx.it", "INSERT INTO tipi_durata_contratto SET descrizione = 'X 8 Mesi'", "Inserimento Tipi di Durata Contratto");
            agg(251, "", "a.toce@tnx.it", "INSERT INTO tipi_durata_contratto SET descrizione = 'X 12 Mesi'", "Inserimento Tipi di Durata Contratto");
            agg(252, "", "a.toce@tnx.it", "INSERT INTO tipi_durata_contratto SET descrizione = 'X 18 Mesi'", "Inserimento Tipi di Durata Contratto");
            agg(253, "", "a.toce@tnx.it", "INSERT INTO tipi_durata_contratto SET descrizione = 'X 24 Mesi'", "Inserimento Tipi di Durata Contratto");
            agg(254, "", "a.toce@tnx.it", "INSERT INTO tipi_durata_contratto SET descrizione = 'X 32 Mesi'", "Inserimento Tipi di Durata Contratto");
            agg(255, "", "a.toce@tnx.it", "INSERT INTO tipi_durata_contratto SET descrizione = 'X 36 Mesi'", "Inserimento Tipi di Durata Contratto");

            agg(256, "", "a.toce@tnx.it", "ALTER TABLE test_ordi ADD COLUMN tipo_snj VARCHAR(1) NULL DEFAULT NULL", "Aggiunto campo tipo ordine SNJ");
            agg(257, "", "a.toce@tnx.it", "ALTER TABLE test_ordi_acquisto ADD COLUMN tipo_snj VARCHAR(1) NULL DEFAULT NULL", "Aggiunto campo tipo ordine SNJ");

            agg(258, "", "m.ceccarelli@tnx.it", "ALTER TABLE " + testate_ricevute + " MODIFY COLUMN numero_doc varchar(50)", "cambiato campo numero doc esterno in varchar!");

            agg(258, "", "a.toce@tnx.it", "ALTER TABLE codici_iva ADD COLUMN codice_readytec VARCHAR(3) NULL DEFAULT NULL", "Aggiunto campo di collegamento codice iva readytec");

            agg(259, "", "m.ceccarelli@tnx.it", "ALTER TABLE test_fatt add column prezzi_ivati char(1) not null default 'N'", "aggiunta gestione prezzi ivati");
            agg(260, "", "m.ceccarelli@tnx.it", "ALTER TABLE righ_fatt add column prezzo_ivato DECIMAL(15,5) NOT NULL DEFAULT 0", "aggiunta gestione prezzi ivati");
            agg(261, "", "m.ceccarelli@tnx.it", "ALTER TABLE test_ddt add column prezzi_ivati char(1) not null default 'N'", "aggiunta gestione prezzi ivati");
            agg(262, "", "m.ceccarelli@tnx.it", "ALTER TABLE righ_ddt add column prezzo_ivato DECIMAL(15,5) NOT NULL DEFAULT 0", "aggiunta gestione prezzi ivati");
            agg(263, "", "m.ceccarelli@tnx.it", "ALTER TABLE test_ordi add column prezzi_ivati char(1) not null default 'N'", "aggiunta gestione prezzi ivati");
            agg(264, "", "m.ceccarelli@tnx.it", "ALTER TABLE righ_ordi add column prezzo_ivato DECIMAL(15,5) NOT NULL DEFAULT 0", "aggiunta gestione prezzi ivati");
            agg(265, "", "m.ceccarelli@tnx.it", "ALTER TABLE test_fatt_acquisto add column prezzi_ivati char(1) not null default 'N'", "aggiunta gestione prezzi ivati");
            agg(266, "", "m.ceccarelli@tnx.it", "ALTER TABLE righ_fatt_acquisto add column prezzo_ivato DECIMAL(15,5) NOT NULL DEFAULT 0", "aggiunta gestione prezzi ivati");
            agg(267, "", "m.ceccarelli@tnx.it", "ALTER TABLE test_ddt_acquisto add column prezzi_ivati char(1) not null default 'N'", "aggiunta gestione prezzi ivati");
            agg(268, "", "m.ceccarelli@tnx.it", "ALTER TABLE righ_ddt_acquisto add column prezzo_ivato DECIMAL(15,5) NOT NULL DEFAULT 0", "aggiunta gestione prezzi ivati");
            agg(269, "", "m.ceccarelli@tnx.it", "ALTER TABLE test_ordi_acquisto add column prezzi_ivati char(1) not null default 'N'", "aggiunta gestione prezzi ivati");
            agg(270, "", "m.ceccarelli@tnx.it", "ALTER TABLE righ_ordi_acquisto add column prezzo_ivato DECIMAL(15,5) NOT NULL DEFAULT 0", "aggiunta gestione prezzi ivati");
            agg(271, "", "m.ceccarelli@tnx.it", "ALTER TABLE tipi_listino add column prezzi_ivati char(1) NOT NULL DEFAULT 'N'", "aggiunta gestione prezzi ivati");

            agg(272, "", "m.ceccarelli@tnx.it", "ALTER TABLE test_fatt add column sconto  DECIMAL(15,5) not null default 0", "gestione sconto a importo");
            agg(273, "", "m.ceccarelli@tnx.it", "ALTER TABLE test_ddt add column sconto  DECIMAL(15,5) not null default 0", "gestione sconto a importo");
            agg(274, "", "m.ceccarelli@tnx.it", "ALTER TABLE test_ordi add column sconto  DECIMAL(15,5) not null default 0", "gestione sconto a importo");
            agg(275, "", "m.ceccarelli@tnx.it", "ALTER TABLE test_fatt_acquisto add column sconto  DECIMAL(15,5) not null default 0", "gestione sconto a importo");
            agg(276, "", "m.ceccarelli@tnx.it", "ALTER TABLE test_ddt_acquisto add column sconto  DECIMAL(15,5) not null default 0", "gestione sconto a importo");
            agg(277, "", "m.ceccarelli@tnx.it", "ALTER TABLE test_ordi_acquisto add column sconto  DECIMAL(15,5) not null default 0", "gestione sconto a importo");

            agg(278, "", "m.ceccarelli@tnx.it", "ALTER TABLE test_fatt add column totale_imponibile_pre_sconto  DECIMAL(15,5) not null default 0", "gestione sconto a importo");
            agg(279, "", "m.ceccarelli@tnx.it", "ALTER TABLE test_ddt add column totale_imponibile_pre_sconto  DECIMAL(15,5) not null default 0", "gestione sconto a importo");
            agg(280, "", "m.ceccarelli@tnx.it", "ALTER TABLE test_ordi add column totale_imponibile_pre_sconto  DECIMAL(15,5) not null default 0", "gestione sconto a importo");
            agg(281, "", "m.ceccarelli@tnx.it", "ALTER TABLE test_fatt_acquisto add column totale_imponibile_pre_sconto  DECIMAL(15,5) not null default 0", "gestione sconto a importo");
            agg(282, "", "m.ceccarelli@tnx.it", "ALTER TABLE test_ddt_acquisto add column totale_imponibile_pre_sconto  DECIMAL(15,5) not null default 0", "gestione sconto a importo");
            agg(283, "", "m.ceccarelli@tnx.it", "ALTER TABLE test_ordi_acquisto add column totale_imponibile_pre_sconto  DECIMAL(15,5) not null default 0", "gestione sconto a importo");

            agg(284, "", "m.ceccarelli@tnx.it", "ALTER TABLE test_fatt add column totale_ivato_pre_sconto  DECIMAL(15,5) not null default 0", "gestione sconto a importo");
            agg(285, "", "m.ceccarelli@tnx.it", "ALTER TABLE test_ddt add column totale_ivato_pre_sconto  DECIMAL(15,5) not null default 0", "gestione sconto a importo");
            agg(286, "", "m.ceccarelli@tnx.it", "ALTER TABLE test_ordi add column totale_ivato_pre_sconto  DECIMAL(15,5) not null default 0", "gestione sconto a importo");
            agg(287, "", "m.ceccarelli@tnx.it", "ALTER TABLE test_fatt_acquisto add column totale_ivato_pre_sconto  DECIMAL(15,5) not null default 0", "gestione sconto a importo");
            agg(288, "", "m.ceccarelli@tnx.it", "ALTER TABLE test_ddt_acquisto add column totale_ivato_pre_sconto  DECIMAL(15,5) not null default 0", "gestione sconto a importo");
            agg(289, "", "m.ceccarelli@tnx.it", "ALTER TABLE test_ordi_acquisto add column totale_ivato_pre_sconto  DECIMAL(15,5) not null default 0", "gestione sconto a importo");

            agg(290, "", "m.ceccarelli@tnx.it", "update righ_fatt r join codici_iva i on r.iva = i.codice set r.prezzo_ivato = r.prezzo * ((i.percentuale / 100) + 1)", "prezzi ivati");
            agg(291, "", "m.ceccarelli@tnx.it", "update righ_ddt r join codici_iva i on r.iva = i.codice set r.prezzo_ivato = r.prezzo * ((i.percentuale / 100) + 1)", "prezzi ivati");
            agg(292, "", "m.ceccarelli@tnx.it", "update righ_ordi r join codici_iva i on r.iva = i.codice set r.prezzo_ivato = r.prezzo * ((i.percentuale / 100) + 1)", "prezzi ivati");
            agg(293, "", "m.ceccarelli@tnx.it", "update righ_fatt_acquisto r join codici_iva i on r.iva = i.codice set r.prezzo_ivato = r.prezzo * ((i.percentuale / 100) + 1)", "prezzi ivati");
            agg(294, "", "m.ceccarelli@tnx.it", "update righ_ddt_acquisto r join codici_iva i on r.iva = i.codice set r.prezzo_ivato = r.prezzo * ((i.percentuale / 100) + 1)", "prezzi ivati");
            agg(295, "", "m.ceccarelli@tnx.it", "update righ_ordi_acquisto r join codici_iva i on r.iva = i.codice set r.prezzo_ivato = r.prezzo * ((i.percentuale / 100) + 1)", "prezzi ivati");

            agg(296, "", "m.ceccarelli@tnx.it", "update test_fatt set totale_imponibile_pre_sconto = totale_imponibile, totale_ivato_pre_sconto = totale", "gestione sconto a importo");
            agg(297, "", "m.ceccarelli@tnx.it", "update test_ddt set totale_imponibile_pre_sconto = totale_imponibile, totale_ivato_pre_sconto = totale", "gestione sconto a importo");
            agg(298, "", "m.ceccarelli@tnx.it", "update test_ordi set totale_imponibile_pre_sconto = totale_imponibile, totale_ivato_pre_sconto = totale", "gestione sconto a importo");
            agg(299, "", "m.ceccarelli@tnx.it", "update test_fatt_acquisto set totale_imponibile_pre_sconto = imponibile, totale_ivato_pre_sconto = importo", "gestione sconto a importo");
            agg(300, "", "m.ceccarelli@tnx.it", "update test_ddt_acquisto set totale_imponibile_pre_sconto = totale_imponibile, totale_ivato_pre_sconto = totale", "gestione sconto a importo");
            agg(301, "", "m.ceccarelli@tnx.it", "update test_ordi_acquisto set totale_imponibile_pre_sconto = totale_imponibile, totale_ivato_pre_sconto = totale", "gestione sconto a importo");

            if (main.getPersonalContain("peroni")) {
                agg(302, "", "m.ceccarelli@tnx.it", "alter table articoli add tipo_birra char(1) NULL DEFAULT ''", "peroni");
            }
            agg(303, "", "m.ceccarelli@tnx.it", "CREATE TABLE tipi_clie_forn (id varchar(10) NOT NULL PRIMARY KEY,  descrizione VARCHAR(255) NOT NULL default '') ENGINE=MyISAM", "Creazione tabella per tipi clie forn");
            if (main.getPersonalContain("peroni")) {
                agg(304, "", "m.ceccarelli@tnx.it", "INSERT INTO tipi_clie_forn SET id = 'RIS', descrizione = 'Ristorante'", "tipi clie forn");
                agg(305, "", "m.ceccarelli@tnx.it", "INSERT INTO tipi_clie_forn SET id = 'PIZ', descrizione = 'Pizzeria'", "tipi clie forn");
                agg(306, "", "m.ceccarelli@tnx.it", "INSERT INTO tipi_clie_forn SET id = 'BAR', descrizione = 'Bar tradizionale'", "tipi clie forn");
                agg(307, "", "m.ceccarelli@tnx.it", "INSERT INTO tipi_clie_forn SET id = 'BAR2', descrizione = 'Bar altro'", "tipi clie forn");
                agg(308, "", "m.ceccarelli@tnx.it", "INSERT INTO tipi_clie_forn SET id = 'PUB', descrizione = 'Pub/Birreria'", "tipi clie forn");
                agg(309, "", "m.ceccarelli@tnx.it", "INSERT INTO tipi_clie_forn SET id = 'HOT', descrizione = 'Hotel'", "tipi clie forn");
                agg(310, "", "m.ceccarelli@tnx.it", "INSERT INTO tipi_clie_forn SET id = '', descrizione = 'Altro'", "tipi clie forn");
            }
            agg(311, "", "m.ceccarelli@tnx.it", "alter table clie_forn add tipo2 varchar(10) NOT NULL DEFAULT ''", "tipo2 su clienti fornitori");

            try {
                if (!DbUtils.containRows(Db.getConn(), "show function status  where Db = '" + Db.dbNameDB + "' and Name = 'calcola_importo_netto'")) {
                    agg(312, "", "m.ceccarelli@tnx.it", "drop function if exists calcola_importo_netto", "funzione per calcolo importo netto riga");
                    agg(313, "", "m.ceccarelli@tnx.it", readfrom("agg_66_mc.sql"), "funzione per calcolo importo netto riga");
                }
            } catch (Exception e) {
                if (e.getMessage().indexOf("mysql.proc") >= 0) {
                    e.printStackTrace();
                    //SwingUtils.showErrorMessage(main.splash, "proc:" + e.getMessage(), "Errore", true);
                    //se in locale gli scarico i file necessari
                    if (!main.db_in_rete) {
                        String url = "http://www.tnx.it/pagine/invoicex_server/download/invoicex/mysql/data/mysql";
                        String file = "proc.MYI";
                        System.out.println("scarico " + file);
                        HttpUtils.saveFile(url + "/" + file, "mysql/data/mysql/" + file);
                        file = "proc.MYD";
                        System.out.println("scarico " + file);
                        HttpUtils.saveFile(url + "/" + file, "mysql/data/mysql/" + file);
                        file = "proc.frm";
                        System.out.println("scarico " + file);
                        HttpUtils.saveFile(url + "/" + file, "mysql/data/mysql/" + file);
                        file = "procs_priv.MYI";
                        System.out.println("scarico " + file);
                        HttpUtils.saveFile(url + "/" + file, "mysql/data/mysql/" + file);
                        file = "procs_priv.MYD";
                        System.out.println("scarico " + file);
                        HttpUtils.saveFile(url + "/" + file, "mysql/data/mysql/" + file);
                        file = "procs_priv.frm";
                        System.out.println("scarico " + file);
                        HttpUtils.saveFile(url + "/" + file, "mysql/data/mysql/" + file);
                        main.fileIni.setValue("varie", "eseguito_download_mysql_proc", DateUtils.formatDateTime(new Date()));
                    }
                } else {
                    e.printStackTrace();
                }
            }

            //lotti su ordini...
            agg(314, "", "m.ceccarelli@tnx.it", "CREATE TABLE righ_ordi_lotti (id_padre INT(11) NULL DEFAULT NULL, id INT(11) NOT NULL AUTO_INCREMENT, lotto VARCHAR(200) NULL DEFAULT NULL, codice_articolo VARCHAR(20) NULL DEFAULT NULL, qta DECIMAL(8,2) NULL DEFAULT NULL, matricola VARCHAR(255) NULL DEFAULT NULL, PRIMARY KEY (id)) ENGINE=MyISAM", "righ_ordi_lotti");
            agg(315, "", "m.ceccarelli@tnx.it", "CREATE TABLE righ_ordi_acquisto_lotti (id_padre INT(11) NULL DEFAULT NULL, id INT(11) NOT NULL AUTO_INCREMENT, lotto VARCHAR(200) NULL DEFAULT NULL, codice_articolo VARCHAR(20) NULL DEFAULT NULL, qta DECIMAL(8,2) NULL DEFAULT NULL, matricola VARCHAR(255) NULL DEFAULT NULL, PRIMARY KEY (id)) ENGINE=MyISAM", "righ_ordi_acquisto_lotti");

            //problema rivalsa!!!
            if (!main.pluginRitenute) {
                agg(316, "", "m.ceccarelli@tnx.it", "ALTER TABLE righ_fatt ADD COLUMN flag_rivalsa char(1) DEFAULT 'S'", "flag_rivalsa");
                agg(317, "", "m.ceccarelli@tnx.it", "ALTER TABLE righ_fatt" + nome_ricevute + " ADD COLUMN flag_rivalsa char(1) DEFAULT 'S'", "flag_rivalsa");
                agg(318, "", "m.ceccarelli@tnx.it", "ALTER TABLE righ_ddt ADD COLUMN flag_rivalsa char(1) DEFAULT 'S'", "flag_rivalsa");
                agg(319, "", "m.ceccarelli@tnx.it", "ALTER TABLE righ_ddt_acquisto ADD COLUMN flag_rivalsa char(1) DEFAULT 'S'", "flag_rivalsa");
                agg(320, "", "m.ceccarelli@tnx.it", "ALTER TABLE righ_ordi ADD COLUMN flag_rivalsa char(1) DEFAULT 'S'", "flag_rivalsa");
                agg(321, "", "m.ceccarelli@tnx.it", "ALTER TABLE righ_ordi_acquisto ADD COLUMN flag_rivalsa char(1) DEFAULT 'S'", "flag_rivalsa");
                agg(322, "", "m.ceccarelli@tnx.it", "ALTER TABLE test_fatt ADD COLUMN rivalsa int NULL", "rivalsa fatture vendita");
                agg(323, "", "m.ceccarelli@tnx.it", "ALTER TABLE " + testate_ricevute + " ADD COLUMN rivalsa int NULL", "rivalsa fatture acquisto");
            }
            agg(324, "", "m.ceccarelli@tnx.it", "ALTER TABLE `pagamenti`  ADD COLUMN `240` CHAR(1) NOT NULL DEFAULT '' AFTER `210`", "Aggiungo colonna 240gg");
            agg(325, "", "m.ceccarelli@tnx.it", "ALTER TABLE `pagamenti`  ADD COLUMN `270` CHAR(1) NOT NULL DEFAULT '' AFTER `240`", "Aggiungo colonna 270gg");
            agg(326, "", "m.ceccarelli@tnx.it", "ALTER TABLE `pagamenti`  ADD COLUMN `300` CHAR(1) NOT NULL DEFAULT '' AFTER `270`", "Aggiungo colonna 300gg");
            agg(327, "", "m.ceccarelli@tnx.it", "ALTER TABLE `pagamenti`  ADD COLUMN `330` CHAR(1) NOT NULL DEFAULT '' AFTER `300`", "Aggiungo colonna 330gg");
            agg(328, "", "m.ceccarelli@tnx.it", "ALTER TABLE `pagamenti`  ADD COLUMN `360` CHAR(1) NOT NULL DEFAULT '' AFTER `330`", "Aggiungo colonna 360gg");

            /* INIZIO MODIFICHE DB PER ACCESSO MULTIUTENTE */
            agg(324, "", "a.toce@tnx.it", "CREATE TABLE `accessi_utenti` (`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT, `username` VARCHAR(150) NOT NULL, `password` VARCHAR(32) NOT NULL, `id_role` INT UNSIGNED NOT NULL, PRIMARY KEY (`id`)) ENGINE=MyISAM", "Aggiunta tabella utenti");
            agg(325, "", "a.toce@tnx.it", "CREATE TABLE `accessi_ruoli` (`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT, `descrizione` VARCHAR(255) NOT NULL, PRIMARY KEY (`id`)) ENGINE=MyISAM", "Aggiunta tabella ruoli");
            agg(326, "", "a.toce@tnx.it", "CREATE TABLE `accessi_tipi_permessi` (`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT, `descrizione` VARCHAR(255) NOT NULL, `lettura` INT(10) UNSIGNED NOT NULL DEFAULT '1', `scrittura` INT(10) UNSIGNED NOT NULL DEFAULT '1', `cancella` INT(10) UNSIGNED NOT NULL DEFAULT '1', PRIMARY KEY (`id`)) ENGINE=MyISAM", "Aggiunta tabella privilegi");
            agg(327, "", "a.toce@tnx.it", "CREATE TABLE `accessi_ruoli_permessi` (`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT, `id_role` INT(10) UNSIGNED NOT NULL, `id_privilegio` INT(10) UNSIGNED NOT NULL, `lettura` INT(10) UNSIGNED NOT NULL DEFAULT '0', `scrittura` INT(10) UNSIGNED NOT NULL DEFAULT '0', `cancella` INT(10) UNSIGNED NOT NULL DEFAULT '0', PRIMARY KEY (`id`), UNIQUE INDEX `id_role_id_privilegio` (`id_role`, `id_privilegio`)) ENGINE=MyISAM", "Aggiunta tabella di collegamento fra ruoli e privilegi");

            agg(328, "", "a.toce@tnx.it", "INSERT INTO accessi_tipi_permessi SET id = 1, descrizione = 'Anagrafica Clienti'", "Aggiungo voci iniziali su gestione privilegi");
            agg(329, "", "a.toce@tnx.it", "INSERT INTO accessi_tipi_permessi SET id = 2, descrizione = 'Anagrafica Articoli e Listini'", "Aggiungo voci iniziali su gestione privilegi");
            agg(330, "", "a.toce@tnx.it", "INSERT INTO accessi_tipi_permessi SET id = 3, descrizione = 'Anagrafica Tipi di Pagamento'", "Aggiungo voci iniziali su gestione privilegi");
            agg(331, "", "a.toce@tnx.it", "INSERT INTO accessi_tipi_permessi SET id = 4, descrizione = 'Anagrafica Codici IVA'", "Aggiungo voci iniziali su gestione privilegi");
            agg(332, "", "a.toce@tnx.it", "INSERT INTO accessi_tipi_permessi SET id = 5, descrizione = 'Altre Anagrafiche'", "Aggiungo voci iniziali su gestione privilegi");
            agg(333, "", "a.toce@tnx.it", "INSERT INTO accessi_tipi_permessi SET id = 6, descrizione = 'Impostazioni', cancella = 0", "Aggiungo voci iniziali su gestione privilegi");
            agg(334, "", "a.toce@tnx.it", "INSERT INTO accessi_tipi_permessi SET id = 7, descrizione = 'Gestione Accesso Utenti'", "Aggiungo voci iniziali su gestione privilegi");
            agg(335, "", "a.toce@tnx.it", "INSERT INTO accessi_tipi_permessi SET id = 8, descrizione = 'Magazzino'", "Aggiungo voci iniziali su gestione privilegi");
            agg(336, "", "a.toce@tnx.it", "INSERT INTO accessi_tipi_permessi SET id = 9, descrizione = 'Statistiche', scrittura = 0, cancella = 0", "Aggiungo voci iniziali su gestione privilegi");
            agg(337, "", "a.toce@tnx.it", "INSERT INTO accessi_tipi_permessi SET id = 10, descrizione = 'Agente'", "Aggiungo voci iniziali su gestione privilegi");
            agg(338, "", "a.toce@tnx.it", "INSERT INTO accessi_tipi_permessi SET id = 11, descrizione = 'Preventivi/Ordini di Vendita'", "Aggiungo voci iniziali su gestione privilegi");
            agg(339, "", "a.toce@tnx.it", "INSERT INTO accessi_tipi_permessi SET id = 12, descrizione = 'DDT di Vendita'", "Aggiungo voci iniziali su gestione privilegi");
            agg(340, "", "a.toce@tnx.it", "INSERT INTO accessi_tipi_permessi SET id = 13, descrizione = 'Fatture di Vendita'", "Aggiungo voci iniziali su gestione privilegi");
            agg(341, "", "a.toce@tnx.it", "INSERT INTO accessi_tipi_permessi SET id = 14, descrizione = 'Preventivi/Ordini di Acquisto'", "Aggiungo voci iniziali su gestione privilegi");
            agg(342, "", "a.toce@tnx.it", "INSERT INTO accessi_tipi_permessi SET id = 15, descrizione = 'DDT di Acquisto'", "Aggiungo voci iniziali su gestione privilegi");
            agg(343, "", "a.toce@tnx.it", "INSERT INTO accessi_tipi_permessi SET id = 16, descrizione = 'Fatture di Acquisto'", "Aggiungo voci iniziali su gestione privilegi");

            agg(344, "", "a.toce@tnx.it", "INSERT INTO accessi_ruoli SET id = 1, descrizione = 'Administrator'", "Aggiungo ruolo amministratore");
            agg(345, "", "a.toce@tnx.it", "INSERT INTO accessi_utenti SET id = 1, username = 'admin', password = " + Db.pc(InvoicexUtil.md5("admin"), Types.VARCHAR) + ", id_role = '1'", "Aggiungo utente amministratore con password admin");

            agg(346, "", "a.toce@tnx.it", "INSERT INTO accessi_ruoli_permessi SET id_role = 1, id_privilegio = 1, lettura = '1', scrittura = '1', cancella = '1'", "Aggiungo privilegi completi per utente admin");
            agg(347, "", "a.toce@tnx.it", "INSERT INTO accessi_ruoli_permessi SET id_role = 1, id_privilegio = 2, lettura = '1', scrittura = '1', cancella = '1'", "Aggiungo privilegi completi per utente admin");
            agg(348, "", "a.toce@tnx.it", "INSERT INTO accessi_ruoli_permessi SET id_role = 1, id_privilegio = 3, lettura = '1', scrittura = '1', cancella = '1'", "Aggiungo privilegi completi per utente admin");
            agg(349, "", "a.toce@tnx.it", "INSERT INTO accessi_ruoli_permessi SET id_role = 1, id_privilegio = 4, lettura = '1', scrittura = '1', cancella = '1'", "Aggiungo privilegi completi per utente admin");
            agg(350, "", "a.toce@tnx.it", "INSERT INTO accessi_ruoli_permessi SET id_role = 1, id_privilegio = 5, lettura = '1', scrittura = '1', cancella = '1'", "Aggiungo privilegi completi per utente admin");
            agg(351, "", "a.toce@tnx.it", "INSERT INTO accessi_ruoli_permessi SET id_role = 1, id_privilegio = 6, lettura = '1', scrittura = '1', cancella = '0'", "Aggiungo privilegi completi per utente admin");
            agg(352, "", "a.toce@tnx.it", "INSERT INTO accessi_ruoli_permessi SET id_role = 1, id_privilegio = 7, lettura = '1', scrittura = '1', cancella = '1'", "Aggiungo privilegi completi per utente admin");
            agg(353, "", "a.toce@tnx.it", "INSERT INTO accessi_ruoli_permessi SET id_role = 1, id_privilegio = 8, lettura = '1', scrittura = '1', cancella = '1'", "Aggiungo privilegi completi per utente admin");
            agg(354, "", "a.toce@tnx.it", "INSERT INTO accessi_ruoli_permessi SET id_role = 1, id_privilegio = 9, lettura = '1', scrittura = '0', cancella = '0'", "Aggiungo privilegi completi per utente admin");
            agg(355, "", "a.toce@tnx.it", "INSERT INTO accessi_ruoli_permessi SET id_role = 1, id_privilegio = 10, lettura = '1', scrittura = '1', cancella = '1'", "Aggiungo privilegi completi per utente admin");
            agg(356, "", "a.toce@tnx.it", "INSERT INTO accessi_ruoli_permessi SET id_role = 1, id_privilegio = 11, lettura = '1', scrittura = '1', cancella = '1'", "Aggiungo privilegi completi per utente admin");
            agg(357, "", "a.toce@tnx.it", "INSERT INTO accessi_ruoli_permessi SET id_role = 1, id_privilegio = 12, lettura = '1', scrittura = '1', cancella = '1'", "Aggiungo privilegi completi per utente admin");
            agg(358, "", "a.toce@tnx.it", "INSERT INTO accessi_ruoli_permessi SET id_role = 1, id_privilegio = 13, lettura = '1', scrittura = '1', cancella = '1'", "Aggiungo privilegi completi per utente admin");
            agg(359, "", "a.toce@tnx.it", "INSERT INTO accessi_ruoli_permessi SET id_role = 1, id_privilegio = 14, lettura = '1', scrittura = '1', cancella = '1'", "Aggiungo privilegi completi per utente admin");
            agg(360, "", "a.toce@tnx.it", "INSERT INTO accessi_ruoli_permessi SET id_role = 1, id_privilegio = 15, lettura = '1', scrittura = '1', cancella = '1'", "Aggiungo privilegi completi per utente admin");
            agg(361, "", "a.toce@tnx.it", "INSERT INTO accessi_ruoli_permessi SET id_role = 1, id_privilegio = 16, lettura = '1', scrittura = '1', cancella = '1'", "Aggiungo privilegi completi per utente admin");

            agg(362, "", "a.toce@tnx.it", "INSERT INTO accessi_tipi_permessi SET id = 17, descrizione = 'Gestione Pagamenti e Scadenzario'", "Aggiungo voci iniziali su gestione privilegi");
            agg(363, "", "a.toce@tnx.it", "INSERT INTO accessi_tipi_permessi SET id = 18, descrizione = 'Gestione Iva', scrittura = 0, cancella = 0", "Aggiungo voci iniziali su gestione privilegi");
            agg(364, "", "a.toce@tnx.it", "INSERT INTO accessi_ruoli_permessi SET id_role = 1, id_privilegio = 17, lettura = '1', scrittura = '1', cancella = '1'", "Aggiungo privilegi completi per utente admin");
            agg(365, "", "a.toce@tnx.it", "INSERT INTO accessi_ruoli_permessi SET id_role = 1, id_privilegio = 18, lettura = '1', scrittura = '0', cancella = '0'", "Aggiungo privilegi completi per utente admin");

            agg(366, "", "a.toce@tnx.it", "CREATE TABLE accessi_log (id int(10) unsigned NOT NULL AUTO_INCREMENT, utente varchar(255) NOT NULL, timestamp_login timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (id)) ENGINE=MyISAM", "Creo tabella di log per accessi");

            agg(329, "", "m.ceccarelli@tnx.it", "ALTER TABLE dati_azienda ADD COLUMN provvigioni_tipo_data varchar(20) NOT NULL DEFAULT 'data_scadenza'", "tipo di generazione delle provvigioni se con data scadenza o data fattura");
            agg(330, "", "m.ceccarelli@tnx.it", "alter table stampa_iva_semplice add column data_doc DATE NULL DEFAULT NULL", "data documento su stampa reg. iva");
            agg(331, "", "m.ceccarelli@tnx.it", "ALTER TABLE dati_azienda ADD COLUMN gestione_utenti int NULL DEFAULT 0", "gestione utenti");

            agg(332, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli` (`id`, `descrizione`) VALUES (2, 'Standard (accesso completo eccetto impostazioni)')", "gestione utenti");
            agg(333, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli` (`id`, `descrizione`) VALUES (3, 'Sola lettura')", "gestione utenti");
            agg(334, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli` (`id`, `descrizione`) VALUES (4, 'DDT e Magazzino')", "gestione utenti");

            agg(335, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 2, 1, 1, 1, 1);", "gestione utenti");
            agg(336, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 2, 2, 1, 1, 1);", "gestione utenti");
            agg(337, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 2, 3, 1, 1, 1);", "gestione utenti");
            agg(338, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 2, 4, 1, 1, 1);", "gestione utenti");
            agg(339, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 2, 5, 1, 1, 1);", "gestione utenti");
            agg(340, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 2, 6, 1, 0, 0);", "gestione utenti");
            agg(341, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 2, 7, 1, 0, 0);", "gestione utenti");
            agg(342, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 2, 8, 1, 1, 1);", "gestione utenti");
            agg(343, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 2, 9, 1, 0, 0);", "gestione utenti");
            agg(344, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 2, 10, 1, 1, 1);", "gestione utenti");
            agg(345, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 2, 11, 1, 1, 1);", "gestione utenti");
            agg(346, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 2, 12, 1, 1, 1);", "gestione utenti");
            agg(347, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 2, 13, 1, 1, 1);", "gestione utenti");
            agg(348, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 2, 14, 1, 1, 1);", "gestione utenti");
            agg(349, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 2, 15, 1, 1, 1);", "gestione utenti");
            agg(350, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 2, 16, 1, 1, 1);", "gestione utenti");
            agg(351, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 2, 17, 1, 1, 1);", "gestione utenti");
            agg(352, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 2, 18, 1, 0, 0);", "gestione utenti");
            agg(353, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 3, 1, 1, 0, 0);", "gestione utenti");
            agg(354, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 3, 2, 1, 0, 0);", "gestione utenti");
            agg(355, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 3, 3, 1, 0, 0);", "gestione utenti");
            agg(356, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 3, 4, 1, 0, 0);", "gestione utenti");
            agg(357, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 3, 5, 1, 0, 0);", "gestione utenti");
            agg(358, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 3, 6, 1, 0, 0);", "gestione utenti");
            agg(359, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 3, 7, 1, 0, 0);", "gestione utenti");
            agg(360, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 3, 8, 1, 0, 0);", "gestione utenti");
            agg(361, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 3, 9, 1, 0, 0);", "gestione utenti");
            agg(362, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 3, 10, 1, 0, 0);", "gestione utenti");
            agg(363, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 3, 11, 1, 0, 0);", "gestione utenti");
            agg(364, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 3, 12, 1, 0, 0);", "gestione utenti");
            agg(365, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 3, 13, 1, 0, 0);", "gestione utenti");
            agg(366, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 3, 14, 1, 0, 0);", "gestione utenti");
            agg(367, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 3, 15, 1, 0, 0);", "gestione utenti");
            agg(368, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 3, 16, 1, 0, 0);", "gestione utenti");
            agg(369, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 3, 17, 1, 0, 0);", "gestione utenti");
            agg(370, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 3, 18, 1, 0, 0);", "gestione utenti");
            agg(371, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 4, 1, 0, 0, 0);", "gestione utenti");
            agg(372, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 4, 2, 0, 0, 0);", "gestione utenti");
            agg(373, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 4, 3, 0, 0, 0);", "gestione utenti");
            agg(374, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 4, 4, 0, 0, 0);", "gestione utenti");
            agg(375, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 4, 5, 0, 0, 0);", "gestione utenti");
            agg(376, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 4, 6, 0, 0, 0);", "gestione utenti");
            agg(377, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 4, 7, 0, 0, 0);", "gestione utenti");
            agg(378, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 4, 8, 1, 1, 1);", "gestione utenti");
            agg(379, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 4, 9, 0, 0, 0);", "gestione utenti");
            agg(380, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 4, 10, 0, 0, 0);", "gestione utenti");
            agg(381, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 4, 11, 0, 0, 0);", "gestione utenti");
            agg(382, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 4, 12, 1, 1, 1);", "gestione utenti");
            agg(383, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 4, 13, 0, 0, 0);", "gestione utenti");
            agg(384, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 4, 14, 0, 0, 0);", "gestione utenti");
            agg(385, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 4, 15, 1, 1, 1);", "gestione utenti");
            agg(386, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 4, 16, 0, 0, 0);", "gestione utenti");
            agg(387, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 4, 17, 0, 0, 0);", "gestione utenti");
            agg(388, "", "m.ceccarelli@tnx.it", "INSERT INTO `accessi_ruoli_permessi` (`id_role`, `id_privilegio`, `lettura`, `scrittura`, `cancella`) VALUES ( 4, 18, 0, 0, 0);", "gestione utenti");

            agg(389, "", "a.toce@tnx.it", "ALTER TABLE righ_ddt ADD COLUMN numero_casse INT NULL DEFAULT NULL", "Aggiunto numero casse su righe DDT");
            agg(390, "", "a.toce@tnx.it", "ALTER TABLE righ_fatt ADD COLUMN numero_casse INT NULL DEFAULT NULL", "Aggiunto numero casse su righe DDT");
            agg(391, "", "a.toce@tnx.it", "ALTER TABLE righ_ordi ADD COLUMN numero_casse INT NULL DEFAULT NULL", "Aggiunto numero casse su righe DDT");

            agg(392, "", "a.toce@tnx.it", "ALTER TABLE `dati_azienda` ADD COLUMN `label_cliente_eng` VARCHAR(100) DEFAULT 'Dear'", "Etichetta cliente");
            agg(393, "", "a.toce@tnx.it", "ALTER TABLE `dati_azienda` ADD COLUMN `label_destinazione_eng` VARCHAR(100) DEFAULT 'Destination'", "Etichetta destinazione");

            agg(389, "", "m.ceccarelli@tnx.it", "ALTER TABLE `dati_azienda` ADD COLUMN `stampare_timbro_firma` VARCHAR(100) DEFAULT 'Non stampare mai'", "timbro firma");
            agg(390, "", "m.ceccarelli@tnx.it", "ALTER TABLE `dati_azienda` ADD COLUMN `testo_timbro_firma` TEXT", "timbro firma");
            agg(391, "", "m.ceccarelli@tnx.it", "update `dati_azienda` set testo_timbro_firma = '<html>\\n<center>\\n<b>\\n      <br>\\n           Data                                         Timbro e Firma<br>\\n          _________                                ____________________<br>\\n<br>\\n</center>\\n</html>'", "timbro firma");

            agg(392, "", "m.ceccarelli@tnx.it", "ALTER TABLE righ_ddt CHANGE COLUMN quantita_evasa quantita_evasa DECIMAL(15,5) NULL DEFAULT NULL;", "qta evasa decimali");
            agg(393, "", "m.ceccarelli@tnx.it", "ALTER TABLE righ_ddt_acquisto CHANGE COLUMN quantita_evasa quantita_evasa DECIMAL(15,5) NULL DEFAULT NULL;", "qta evasa decimali");
            agg(394, "", "m.ceccarelli@tnx.it", "ALTER TABLE righ_ordi CHANGE COLUMN quantita_evasa quantita_evasa DECIMAL(15,5) NULL DEFAULT NULL;", "qta evasa decimali");
            agg(395, "", "m.ceccarelli@tnx.it", "ALTER TABLE righ_ordi_acquisto CHANGE COLUMN quantita_evasa quantita_evasa DECIMAL(15,5) NULL DEFAULT NULL;", "qta evasa decimali");

            agg(396, "", "m.ceccarelli@tnx.it", "create table tipi_aspetto_esteriore_beni (id int auto_increment not null primary key, nome varchar(100)) ENGINE=MyISAM", "tabella tipi_aspetto_esteriore_beni");
            agg(397, "", "m.ceccarelli@tnx.it", "insert into tipi_aspetto_esteriore_beni (nome) values ('SCATOLA')", "tabella tipi_aspetto_esteriore_beni");
            agg(398, "", "m.ceccarelli@tnx.it", "insert into tipi_aspetto_esteriore_beni (nome) values ('A VISTA')", "tabella tipi_aspetto_esteriore_beni");
            agg(399, "", "m.ceccarelli@tnx.it", "insert into tipi_aspetto_esteriore_beni (nome) values ('SCATOLA IN PANCALE')", "tabella tipi_aspetto_esteriore_beni");
            agg(400, "", "m.ceccarelli@tnx.it", "insert into tipi_aspetto_esteriore_beni (nome) values ('BUSTA')", "tabella tipi_aspetto_esteriore_beni");
            agg(401, "", "m.ceccarelli@tnx.it", "insert into tipi_aspetto_esteriore_beni (nome) values ('CARTONE')", "tabella tipi_aspetto_esteriore_beni");

            agg(402, "", "m.ceccarelli@tnx.it", "alter table dati_azienda add column tipo_numerazione int not null default 0", "dati azienda tipo numerazione");
            agg(403, "", "m.ceccarelli@tnx.it", "alter table dati_azienda add column tipo_numerazione_confermata int not null default 0", "dati azienda tipo numerazione");
            agg(404, "", "m.ceccarelli@tnx.it", "alter table dati_azienda add column tipo_numerazione_confermata2 int not null default 0", "dati azienda tipo numerazione");

            //test debug per errore colonna già presente
//            agg(405, "", "m.ceccarelli@tnx.it", "ALTER TABLE `dati_azienda` ADD COLUMN `testo_timbro_firma` TEXT", "timbro firma");

            agg(406, "", "m.ceccarelli@tnx.it", "ALTER TABLE test_fatt_acquisto add index serie_numero_anno (serie, numero, anno)", "indici per scadenze");
            agg(407, "", "m.ceccarelli@tnx.it", "ALTER TABLE scadenze add index serie_numero_anno (documento_serie, documento_numero, documento_anno)", "indici per scadenze");
            agg(408, "", "m.ceccarelli@tnx.it", "alter table scadenze_parziali add index id_scadenza (id_scadenza)", "indici per scadenze");
            agg(409, "", "m.ceccarelli@tnx.it", "alter table scadenze add index documento_tipo (documento_tipo)", "indici per scadenze");
            agg(410, "", "m.ceccarelli@tnx.it", "alter table scadenze add index data_scadenza (data_scadenza)", "indici per scadenze");
            agg(411, "", "m.ceccarelli@tnx.it", "alter table clie_forn add index ragione_sociale (ragione_sociale)", "indici per scadenze");

            agg(412, "", "m.ceccarelli@tnx.it", "ALTER TABLE pagamenti ADD COLUMN id_pagamento_teamsystem INT NULL DEFAULT NULL", "Aggiunto campo di collegamento com teamsystem");
            agg(413, "", "m.ceccarelli@tnx.it", "ALTER TABLE pagamenti ADD COLUMN tipo_effetto_teamsystem INT NULL DEFAULT NULL", "Aggiunto campo di collegamento com teamsystem");

            agg(414, "", "m.ceccarelli@tnx.it", "alter table pacchetti_articoli CHANGE COLUMN quantita quantita DECIMAL(15,5) NOT NULL", "qta kit");

            agg(415, "", "m.ceccarelli@tnx.it", "ALTER TABLE clie_forn CHANGE COLUMN email email TEXT NULL DEFAULT NULL", "campo email più lungo");

            agg(416, "", "m.ceccarelli@tnx.it", "ALTER TABLE clie_forn ADD COLUMN `provvigione_predefinita_cliente` DECIMAL(5,2) NULL DEFAULT NULL AFTER `agente`", "provvigioni agenti");
            agg(417, "", "m.ceccarelli@tnx.it", "ALTER TABLE clie_forn ADD COLUMN `provvigione_predefinita_fornitore` DECIMAL(5,2) NULL DEFAULT NULL AFTER `provvigione_predefinita_cliente`", "provvigioni agenti");

            agg(418, "", "m.ceccarelli@tnx.it", "ALTER TABLE `articoli` CHANGE COLUMN `fornitore` `fornitore_old` VARCHAR(10) NULL DEFAULT NULL", "provvigioni agenti per articolo fornitore");
            agg(419, "", "m.ceccarelli@tnx.it", "ALTER TABLE articoli ADD COLUMN `fornitore` INT UNSIGNED NULL DEFAULT NULL AFTER `fornitore_old`", "provvigioni agenti per articolo fornitore");
            agg(420, "", "m.ceccarelli@tnx.it", "ALTER TABLE articoli ADD COLUMN `disponibilita_reale` decimal(15,5) NULL DEFAULT NULL", "giacenza articolo");
            agg(421, "", "m.ceccarelli@tnx.it", "ALTER TABLE articoli ADD COLUMN `disponibilita_reale_ts` TIMESTAMP NULL DEFAULT NULL AFTER disponibilita_reale", "giacenza articolo");
            agg(422, "", "m.ceccarelli@tnx.it", "ALTER TABLE movimenti_magazzino ADD COLUMN `modificato_ts` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP", "giacenza articolo");

            agg(423, "", "m.ceccarelli@tnx.it", "CREATE TABLE movimenti_magazzino_eliminati LIKE movimenti_magazzino", "giacenza articolo");

            agg(424, "", "m.ceccarelli@tnx.it", "alter table righ_ordi add column prezzo_netto_unitario decimal(15,5)", "prezzo netto unitario");
            agg(425, "", "m.ceccarelli@tnx.it", "alter table righ_ordi_acquisto add column prezzo_netto_unitario decimal(15,5)", "prezzo netto unitario");
            agg(426, "", "m.ceccarelli@tnx.it", "alter table righ_ddt add column prezzo_netto_unitario decimal(15,5)", "prezzo netto unitario");
            agg(427, "", "m.ceccarelli@tnx.it", "alter table righ_ddt_acquisto add column prezzo_netto_unitario decimal(15,5)", "prezzo netto unitario");
            agg(428, "", "m.ceccarelli@tnx.it", "alter table righ_fatt add column prezzo_netto_unitario decimal(15,5)", "prezzo netto unitario");
            agg(429, "", "m.ceccarelli@tnx.it", "alter table righ_fatt_acquisto add column prezzo_netto_unitario decimal(15,5)", "prezzo netto unitario");

            agg(430, "", "m.ceccarelli@tnx.it", "alter table clie_forn add column produttore int null", "produttore");

            String sql = "select export_fatture_estrai_scadenze from dati_azienda";
            try {
                DbUtils.tryOpenResultSet(Db.getConn(), sql);
            } catch (Exception e) {
                DbUtils.tryExecQuery(Db.getConn(), "ALTER TABLE `dati_azienda` ADD COLUMN `export_fatture_codice_iva` varchar(50) NULL");
                DbUtils.tryExecQuery(Db.getConn(), "ALTER TABLE `dati_azienda` ADD COLUMN `export_fatture_conto_ricavi` varchar(50) NULL");
                DbUtils.tryExecQuery(Db.getConn(), "ALTER TABLE `dati_azienda` ADD COLUMN `export_fatture_estrai_scadenze` char(1) NULL");
                //riporto i dati dal file ini
                sql = "update dati_azienda set export_fatture_codice_iva = " + Db.pc(main.fileIni.getValue("readytec", "codiceIvaDefault", ""), Types.VARCHAR);
                DbUtils.tryExecQuery(Db.getConn(), sql);
                sql = "update dati_azienda set export_fatture_conto_ricavi = " + Db.pc(main.fileIni.getValue("readytec", "codiceContoDefault", ""), Types.VARCHAR);
                DbUtils.tryExecQuery(Db.getConn(), sql);
                sql = "update dati_azienda set export_fatture_estrai_scadenze = 'S'";
                DbUtils.tryExecQuery(Db.getConn(), sql);
            }

            agg(431, "", "m.ceccarelli@tnx.it", "update test_fatt set totale_da_pagare = totale where tipo_fattura = 7", "update per scontrini");

            String view1 = "create view v_righ_tutte as "
                    + " SELECT 'v' as tabella, r.id, r.id_padre, t.data, t.anno, t.numero, t.serie, r.riga, r.codice_articolo, r.descrizione\n"
                    + ", r.quantita, r.prezzo, r.prezzo_netto_unitario, r.sconto1, r.sconto2, t.sconto1 as sconto1t, t.sconto2 as sconto2t, t.sconto3 as sconto3t\n"
                    + ", c.codice as clifor, c.ragione_sociale\n"
                    + "from righ_fatt r \n"
                    + "join test_fatt t on r.id_padre = t.id\n"
                    + "join clie_forn c on t.cliente = c.codice\n"
                    + "union all \n"
                    + "SELECT 'a' as tabella, r.id, r.id_padre, t.data, t.anno, t.numero, t.serie, r.riga, r.codice_articolo, r.descrizione\n"
                    + ", r.quantita, r.prezzo, r.prezzo_netto_unitario, r.sconto1, r.sconto2, t.sconto1 as sconto1t, t.sconto2 as sconto2t, t.sconto3 as sconto3t\n"
                    + ", c.codice as clifor, c.ragione_sociale\n"
                    + "from righ_fatt_acquisto r \n"
                    + "join test_fatt_acquisto t on r.id_padre = t.id\n"
                    + "join clie_forn c on t.fornitore = c.codice";
            agg(432, "", "m.ceccarelli@tnx.it", view1, "vista per ultimi prezzi");

            agg(433, "", "m.ceccarelli@tnx.it", "alter table righ_ordi add column data_consegna_prevista date", "data consegna prevista");
            agg(434, "", "m.ceccarelli@tnx.it", "alter table righ_ddt add column data_consegna_prevista date", "data consegna prevista");
            agg(435, "", "m.ceccarelli@tnx.it", "alter table righ_fatt add column data_consegna_prevista date", "data consegna prevista");
            agg(436, "", "m.ceccarelli@tnx.it", "alter table righ_ordi_acquisto add column data_consegna_prevista date", "data consegna prevista");
            agg(437, "", "m.ceccarelli@tnx.it", "alter table righ_ddt_acquisto add column data_consegna_prevista date", "data consegna prevista");
            agg(438, "", "m.ceccarelli@tnx.it", "alter table righ_fatt_acquisto add column data_consegna_prevista date", "data consegna prevista");

            agg(439, "", "m.ceccarelli@tnx.it", "alter table test_ordi add column data_consegna_prevista date", "data consegna prevista");
            agg(440, "", "m.ceccarelli@tnx.it", "alter table test_ddt add column data_consegna_prevista date", "data consegna prevista");
            agg(441, "", "m.ceccarelli@tnx.it", "alter table test_fatt add column data_consegna_prevista date", "data consegna prevista");
            agg(442, "", "m.ceccarelli@tnx.it", "alter table test_ordi_acquisto add column data_consegna_prevista date", "data consegna prevista");
            agg(443, "", "m.ceccarelli@tnx.it", "alter table test_ddt_acquisto add column data_consegna_prevista date", "data consegna prevista");
            agg(444, "", "m.ceccarelli@tnx.it", "alter table test_fatt_acquisto add column data_consegna_prevista date", "data consegna prevista");

            agg(445, "", "m.ceccarelli@tnx.it", "create table tipi_consegna (id int auto_increment not null primary key, nome varchar(100)) ENGINE=MyISAM", "tabella tipi consegna");
            agg(446, "", "m.ceccarelli@tnx.it", "insert into tipi_consegna (nome) values ('MITTENTE')", "tabella tipi consegna");
            agg(447, "", "m.ceccarelli@tnx.it", "insert into tipi_consegna (nome) values ('VETTORE')", "tabella tipi consegna");
            agg(448, "", "m.ceccarelli@tnx.it", "insert into tipi_consegna (nome) values ('DESTINATARIO')", "tabella tipi consegna");

            agg(449, "", "m.ceccarelli@tnx.it", "create table tipi_scarico (id int auto_increment not null primary key, nome varchar(100)) ENGINE=MyISAM", "tabella tipi scarico");
            agg(450, "", "m.ceccarelli@tnx.it", "insert into tipi_scarico (nome) values ('CON MULETTO')", "tabella tipi scarico");
            agg(451, "", "m.ceccarelli@tnx.it", "insert into tipi_scarico (nome) values ('A MANO')", "tabella tipi scarico");

            agg(452, "", "m.ceccarelli@tnx.it", "ALTER TABLE stati ADD UNIQUE INDEX indice_codice1 (codice1)", "indice univoco per nazioni");

            agg(453, "", "m.ceccarelli@tnx.it", "ALTER TABLE clie_forn ADD modalita_consegna int", "consegna");
            agg(454, "", "m.ceccarelli@tnx.it", "ALTER TABLE clie_forn ADD modalita_scarico int", "scarico");

            agg(455, "", "m.ceccarelli@tnx.it", "alter table test_ordi add column modalita_consegna int", "consegna");
            agg(456, "", "m.ceccarelli@tnx.it", "alter table test_ddt add column modalita_consegna int", "consegna");
            agg(457, "", "m.ceccarelli@tnx.it", "alter table test_fatt add column modalita_consegna int", "consegna");
            agg(458, "", "m.ceccarelli@tnx.it", "alter table test_ordi_acquisto add column modalita_consegna int", "consegna");
            agg(459, "", "m.ceccarelli@tnx.it", "alter table test_ddt_acquisto add column modalita_consegna int", "consegna");
            agg(460, "", "m.ceccarelli@tnx.it", "alter table test_fatt_acquisto add column modalita_consegna int", "consegna");

            agg(461, "", "m.ceccarelli@tnx.it", "alter table test_ordi add column modalita_scarico int", "consegna");
            agg(462, "", "m.ceccarelli@tnx.it", "alter table test_ddt add column modalita_scarico int", "consegna");
            agg(463, "", "m.ceccarelli@tnx.it", "alter table test_fatt add column modalita_scarico int", "consegna");
            agg(464, "", "m.ceccarelli@tnx.it", "alter table test_ordi_acquisto add column modalita_scarico int", "consegna");
            agg(465, "", "m.ceccarelli@tnx.it", "alter table test_ddt_acquisto add column modalita_scarico int", "consegna");
            agg(466, "", "m.ceccarelli@tnx.it", "alter table test_fatt_acquisto add column modalita_scarico int", "consegna");

            //numerazione, se hanno scelto bene, altrimenti forzo a numerazione come prima
            if (!InvoicexUtil.isSceltaTipoNumerazioneEseguita()) {
                agg(467, "", "m.ceccarelli@tnx.it", "update dati_azienda set tipo_numerazione = " + InvoicexUtil.TIPO_NUMERAZIONE_ANNO_SOLO_NUMERO + ", tipo_numerazione_confermata = 1", "numerazione");
            }

            agg(468, "", "m.ceccarelli@tnx.it", "update test_ordi set anno = year(data) where anno is null or anno != year(data)", "anno in data");
            agg(469, "", "m.ceccarelli@tnx.it", "update righ_ordi r join test_ordi t on r.id_padre = t.id set r.anno = year(t.data) where r.anno is null or r.anno != year(t.data)", "anno in data");
            agg(470, "", "m.ceccarelli@tnx.it", "update test_ddt set anno = year(data) where anno is null or anno != year(data)", "anno in data");
            agg(471, "", "m.ceccarelli@tnx.it", "update righ_ddt r join test_ddt t on r.id_padre = t.id set r.anno = year(t.data) where r.anno is null or r.anno != year(t.data)", "anno in data");
            agg(472, "", "m.ceccarelli@tnx.it", "update test_fatt set anno = year(data) where anno is null or anno != year(data)", "anno in data");
            agg(473, "", "m.ceccarelli@tnx.it", "update righ_fatt r join test_fatt t on r.id_padre = t.id set r.anno = year(t.data) where r.anno is null or r.anno != year(t.data)", "anno in data");

            agg(474, "", "m.ceccarelli@tnx.it", "update test_ordi_acquisto set anno = year(data) where anno is null or anno != year(data)", "anno in data");
            agg(475, "", "m.ceccarelli@tnx.it", "update righ_ordi_acquisto r join test_ordi_acquisto t on r.id_padre = t.id set r.anno = year(t.data) where r.anno is null or r.anno != year(t.data)", "anno in data");
            agg(476, "", "m.ceccarelli@tnx.it", "update test_ddt_acquisto set anno = year(data) where anno is null or anno != year(data)", "anno in data");
            agg(477, "", "m.ceccarelli@tnx.it", "update righ_ddt_acquisto r join test_ddt_acquisto t on r.id_padre = t.id set r.anno = year(t.data) where r.anno is null or r.anno != year(t.data)", "anno in data");
            agg(478, "", "m.ceccarelli@tnx.it", "update test_fatt_acquisto set anno = year(data) where anno is null or anno != year(data)", "anno in data");
            agg(479, "", "m.ceccarelli@tnx.it", "update righ_fatt_acquisto r join test_fatt_acquisto t on r.id_padre = t.id set r.anno = year(t.data) where r.anno is null or r.anno != year(t.data)", "anno in data");
            agg(480, "", "m.ceccarelli@tnx.it", "ALTER TABLE `test_fatt_acquisto` ADD COLUMN `color` VARCHAR(20) DEFAULT ''", "Aggiunta colonna per segnalazione colore");
            agg(481, "", "m.ceccarelli@tnx.it", "ALTER TABLE `test_ordi` ADD COLUMN `color` VARCHAR(20) DEFAULT ''", "Aggiunta colonna per segnalazione colore");
            agg(482, "", "m.ceccarelli@tnx.it", "ALTER TABLE `test_ordi_acquisto` ADD COLUMN `color` VARCHAR(20) DEFAULT ''", "Aggiunta colonna per segnalazione colore");
            agg(483, "", "m.ceccarelli@tnx.it", "ALTER TABLE `test_ddt` ADD COLUMN `color` VARCHAR(20) DEFAULT ''", "Aggiunta colonna per segnalazione colore");
            agg(484, "", "m.ceccarelli@tnx.it", "ALTER TABLE `test_ddt_acquisto` ADD COLUMN `color` VARCHAR(20) DEFAULT ''", "Aggiunta colonna per segnalazione colore");

            //forzo passaggio iva
            if (!InvoicexUtil.isPassaggio21eseguito()) {
                try {
                    if (!(DbUtils.containRows(gestioneFatture.Db.getConn(), "select * from codici_iva where codice = '21'"))) {
                        DbUtils.tryExecQuery(gestioneFatture.Db.getConn(), "insert into codici_iva set codice = '21', percentuale = 21, descrizione = 'Iva 21%', descrizione_breve = 'Iva 21%'");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (DbUtils.containRows(gestioneFatture.Db.getConn(), "select * from codici_iva where codice = '21'")) {
                        //articoli
                        try {
                            DbUtils.tryExecQuery(gestioneFatture.Db.getConn(), "update articoli set iva = 21 where iva = 20");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //clienti e fornitori
                        try {
                            DbUtils.tryExecQuery(gestioneFatture.Db.getConn(), "update clie_forn set iva_standard = 21 where iva_standard = 20");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //impostazioni
                        try {
                            if (CastUtils.toString(InvoicexUtil.getIvaDefault()).equals("20")) {
                                DbUtils.tryExecQuery(gestioneFatture.Db.getConn(), "update dati_azienda set codiceIvaDefault = '21'");
                                main.fileIni.setValue("iva", "codiceIvaDefault", "21");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            if (CastUtils.toString(InvoicexUtil.getIvaSpese()).equals("20")) {
                                DbUtils.tryExecQuery(gestioneFatture.Db.getConn(), "update dati_azienda set codiceIvaSpese = '21'");
                                main.fileIni.setValue("iva", "codiceIvaSpese", "21");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                main.fileIni.setValue("iva21", "eseguito", true);
                try {
                    DbUtils.tryExecQuery(gestioneFatture.Db.getConn(), "update dati_azienda set iva21eseguito = 'S'");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            agg(485, "", "m.ceccarelli@tnx.it", "alter table righ_ordi add column totale_imponibile_netto decimal(15,5)", "prezzo netto unitario");
            agg(486, "", "m.ceccarelli@tnx.it", "alter table righ_ordi_acquisto add column totale_imponibile_netto decimal(15,5)", "prezzo netto unitario");
            agg(487, "", "m.ceccarelli@tnx.it", "alter table righ_ddt add column totale_imponibile_netto decimal(15,5)", "prezzo netto unitario");
            agg(488, "", "m.ceccarelli@tnx.it", "alter table righ_ddt_acquisto add column totale_imponibile_netto decimal(15,5)", "prezzo netto unitario");
            agg(489, "", "m.ceccarelli@tnx.it", "alter table righ_fatt add column totale_imponibile_netto decimal(15,5)", "prezzo netto unitario");
            agg(490, "", "m.ceccarelli@tnx.it", "alter table righ_fatt_acquisto add column totale_imponibile_netto decimal(15,5)", "prezzo netto unitario");

            agg(491, "", "m.ceccarelli@tnx.it", "alter table righ_ordi add column totale_iva_netto decimal(15,5)", "prezzo netto unitario");
            agg(492, "", "m.ceccarelli@tnx.it", "alter table righ_ordi_acquisto add column totale_iva_netto decimal(15,5)", "prezzo netto unitario");
            agg(493, "", "m.ceccarelli@tnx.it", "alter table righ_ddt add column totale_iva_netto decimal(15,5)", "prezzo netto unitario");
            agg(494, "", "m.ceccarelli@tnx.it", "alter table righ_ddt_acquisto add column totale_iva_netto decimal(15,5)", "prezzo netto unitario");
            agg(495, "", "m.ceccarelli@tnx.it", "alter table righ_fatt add column totale_iva_netto decimal(15,5)", "prezzo netto unitario");
            agg(496, "", "m.ceccarelli@tnx.it", "alter table righ_fatt_acquisto add column totale_iva_netto decimal(15,5)", "prezzo netto unitario");

            agg(497, "", "m.ceccarelli@tnx.it", "alter table righ_ordi add column totale_ivato_netto decimal(15,5)", "prezzo netto unitario");
            agg(498, "", "m.ceccarelli@tnx.it", "alter table righ_ordi_acquisto add column totale_ivato_netto decimal(15,5)", "prezzo netto unitario");
            agg(499, "", "m.ceccarelli@tnx.it", "alter table righ_ddt add column totale_ivato_netto decimal(15,5)", "prezzo netto unitario");
            agg(500, "", "m.ceccarelli@tnx.it", "alter table righ_ddt_acquisto add column totale_ivato_netto decimal(15,5)", "prezzo netto unitario");
            agg(501, "", "m.ceccarelli@tnx.it", "alter table righ_fatt add column totale_ivato_netto decimal(15,5)", "prezzo netto unitario");
            agg(502, "", "m.ceccarelli@tnx.it", "alter table righ_fatt_acquisto add column totale_ivato_netto decimal(15,5)", "prezzo netto unitario");

            agg(503, "", "m.ceccarelli@tnx.it", "alter table righ_ordi add column prezzo_ivato_netto_unitario decimal(15,5)", "prezzo netto unitario");
            agg(504, "", "m.ceccarelli@tnx.it", "alter table righ_ordi_acquisto add column prezzo_ivato_netto_unitario decimal(15,5)", "prezzo netto unitario");
            agg(505, "", "m.ceccarelli@tnx.it", "alter table righ_ddt add column prezzo_ivato_netto_unitario decimal(15,5)", "prezzo netto unitario");
            agg(506, "", "m.ceccarelli@tnx.it", "alter table righ_ddt_acquisto add column prezzo_ivato_netto_unitario decimal(15,5)", "prezzo netto unitario");
            agg(507, "", "m.ceccarelli@tnx.it", "alter table righ_fatt add column prezzo_ivato_netto_unitario decimal(15,5)", "prezzo netto unitario");
            agg(508, "", "m.ceccarelli@tnx.it", "alter table righ_fatt_acquisto add column prezzo_ivato_netto_unitario decimal(15,5)", "prezzo netto unitario");


            agg(509, "", "m.ceccarelli@tnx.it", "alter table righ_ordi add column prezzo_netto_totale decimal(15,5)", "prezzo netto totale");
            agg(510, "", "m.ceccarelli@tnx.it", "alter table righ_ordi_acquisto add column prezzo_netto_totale decimal(15,5)", "prezzo netto totale");
            agg(511, "", "m.ceccarelli@tnx.it", "alter table righ_ddt add column prezzo_netto_totale decimal(15,5)", "prezzo netto totale");
            agg(512, "", "m.ceccarelli@tnx.it", "alter table righ_ddt_acquisto add column prezzo_netto_totale decimal(15,5)", "prezzo netto totale");
            agg(513, "", "m.ceccarelli@tnx.it", "alter table righ_fatt add column prezzo_netto_totale decimal(15,5)", "prezzo netto totale");
            agg(514, "", "m.ceccarelli@tnx.it", "alter table righ_fatt_acquisto add column prezzo_netto_totale decimal(15,5)", "prezzo netto totale");

            agg(515, "", "m.ceccarelli@tnx.it", "drop view if exists v_righ_tutte", "vista per ultimi prezzi");
            agg(516, "", "m.ceccarelli@tnx.it", view1, "vista per ultimi prezzi");

            agg(517, "", "m.ceccarelli@tnx.it", "update clie_forn set provvigione_predefinita_cliente = null where provvigione_predefinita_cliente = 0", "update prov a 0");
            agg(518, "", "m.ceccarelli@tnx.it", "update clie_forn set provvigione_predefinita_fornitore = null where provvigione_predefinita_fornitore = 0", "update prov a 0");
            agg(519, "", "m.ceccarelli@tnx.it", "CREATE TABLE `attivazione` (\n"
                    + "	`codice` VARCHAR(50) NOT NULL,\n"
                    + "	`versione` VARCHAR(50) NULL DEFAULT NULL,\n"
                    + "	`esito_log` VARCHAR(250) NULL DEFAULT NULL,\n"
                    + "	`ts` TIMESTAMP NULL DEFAULT NULL,\n"
                    + "	PRIMARY KEY (`codice`)\n"
                    + ")\n", "attivazione");

            //il 520 è servito per l'attivazione, vedi dopo esegui_aggs
            agg(521, "", "m.ceccarelli@tnx.it", "ALTER TABLE `dati_azienda` ADD COLUMN `iva22eseguito` CHAR(1)", "iva22eseguito");
            agg(522, "", "m.ceccarelli@tnx.it", "ALTER TABLE `dati_azienda` ADD COLUMN `iva22a21eseguito` CHAR(1)", "iva22a21eseguito");
            
            esegui_aggs();

            //attivazione
            if (checkLog(520, "", "m.ceccarelli@tnx.it") == false) {
                List<Map> list = DbUtils.getListMap(Db.getConn(), "select * from attivazioni where msg like '%ESITO:Attivato%' "
                        + " and (msg like '%Professional%' "
                        + " or msg like '%Enterprise%'"
                        + " ) "
                        + "order by data desc limit 1");
                //Attivazione ID:XXX ORDINE:XXX ESITO:Attivato: Invoicex Enterprise
                if (list.size() > 0) {
                    try {
                        String log = cu.toString(list.get(0).get("msg"));
                        Date ts = cu.toDate(list.get(0).get("data"));
                        String codice = "";
                        String versione = "";
                        codice = StringUtils.substringBefore(StringUtils.substringAfter(log, "ORDINE:"), " ESITO:");
                        versione = StringUtils.substringAfter(log, "Attivato: ");
                        sql = "delete from attivazione";
                        DbUtils.tryExecQuery(Db.getConn(), sql);
                        sql = "insert into attivazione (codice, versione, esito_log, ts) values (" + Db.pc(codice, Types.VARCHAR) + ", " + Db.pc(versione, Types.VARCHAR) + ", "  + Db.pc(log, Types.VARCHAR) + ", " + Db.pc(ts, Types.TIMESTAMP) + ")";
                        DbUtils.tryExecQuery(Db.getConn(), sql);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }                
                writeLog(520, "", "m.ceccarelli@tnx.it", "");
            }
            
            //controllo engine delle tabelle
            checkTableEngineInno();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                statLog.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        System.out.println(mb.getDiff("fine agg db log2 " + this.getClass().toString()));
    }

    private void agg18() {
        int id_log = 18;
        String id_plugin = "";
        String id_email = "m.ceccarelli@tnx.it";
        String note = "sistemazione movimenti ddt di acquisto per bug ver 2010-10";

        if (checkLog(id_log, id_plugin, id_email) == false) {
            try {
                List<Map> list = DbUtils.getListMap(Db.getConn(), "select serie, numero, anno, id from test_ddt_acquisto order by id");
                for (Map m : list) {
                    try {
                        dbDocumento prev = new dbDocumento();
                        prev.serie = (String) m.get("serie");
                        prev.numero = CastUtils.toInteger(m.get("numero"));
                        prev.anno = CastUtils.toInteger(m.get("anno"));
                        prev.id = CastUtils.toInteger(m.get("id"));
                        prev.tipoDocumento = Db.TIPO_DOCUMENTO_DDT_ACQUISTO;
                        prev.acquisto = true;
                        if (prev.generaMovimentiMagazzino() == false) {
                            System.out.println("agg18: !!! problema: " + prev.serie + " " + prev.numero + " " + prev.anno + " " + prev.id);
                        } else {
                            System.out.println("agg18: ok: " + prev.serie + " " + prev.numero + " " + prev.anno + " " + prev.id);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception err) {
                err.printStackTrace();
            }
            writeLog(id_log, id_plugin, id_email, note);
        }
    }

    public void pre_check(int id_log, String id_plugin, String id_email) {
    }

    public void post_check(int id_log, String id_plugin, String id_email) {
    }

    public void post_execute_ok(int id_log, String id_plugin, String id_email, String sql) {
    }

    public void post_execute_ko(int id_log, String id_plugin, String id_email, String sql) {
    }

    public void post_execute_ko(int id_log, String id_plugin, String id_email, String sql, Exception err) {
    }

    public void post_check_ok(int id_log, String id_plugin, String id_email) {
    }

    private void checkTableEngineInno() {
        main.splash("aggiornamenti struttura database: controllo engine tabelle", 50);
        try {
            List<Map> status = DbUtils.getListMap(Db.getConn(true), "SHOW TABLE STATUS");
            for (Map rec : status) {
                if (cu.toString(rec.get("Name")).startsWith("pn_")) {
                    System.out.println("ignoro tabelle prima nota:" + rec.get("Name"));
                    continue;
                }
                System.out.println("tab = " + rec.get("Name") + " engine: " + rec.get("Engine"));
                if (rec != null && rec.get("Name") != null && rec.get("Engine") != null && !rec.get("Engine").toString().equalsIgnoreCase("MyISAM")) {
                    try {
                        String name = (String) rec.get("Name");
                        System.out.println("la tabella " + rec.get("Name") + " è con engine " + rec.get("Engine") + ", cambio in MyISAM");
                        //prima faccio dump
                        String nomeFileDump = CurrentDir.getCurrentDir() + "/backup/dump_per_engine_";
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmm");
                        Date d = new Date();
                        nomeFileDump += name + "_" + sdf.format(d) + ".txt";
                        FileOutputStream fos = new FileOutputStream(nomeFileDump, false);
                        it.tnx.Util.dumpTable(name, Db.getConn(), fos);
                        System.out.println(name + " : dumped");
                        fos.close();
                        //poi cambio
                        DbUtils.tryExecQuery(Db.getConn(true), "alter table `" + name + "` ENGINE=MyISAM");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        main.splash("aggiornamenti struttura database: controllo engine tabelle completato", 100);
    }
}
