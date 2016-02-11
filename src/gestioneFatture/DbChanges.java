/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gestioneFatture;

import it.tnx.Db;
import it.tnx.commons.DbUtils;
import it.tnx.commons.MicroBench;
import it.tnx.invoicex.gui.JFrameIntro2;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

/**
 *
 * @author mceccarelli
 */
public class DbChanges {
//    public frmIntro splash = null;
    public JFrameIntro2 splash = null;
    public iniFileProp fileIni;
    Statement statLog = null;
    public List logs = null;
    int max = 276;
    boolean exist_ricevute = true;
    String nome_ricevute = "_ricevute";
    String testate_ricevute = "fatture_ricevute_teste";

    public DbChanges() {
        try {
            statLog = Db.getConn().createStatement();
            this.logs = DbUtils.getList(Db.getConn(), "select id from log");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean agg(int idAggiornamento, String sql, String note) {
        main.splash("aggiornamenti struttura database: [1/2] " + idAggiornamento + "/" + max, idAggiornamento * 100 / max);

        if (checkLog(idAggiornamento) == false) {
            try {
                if (Db.executeSql(sql)) {
                    writeLog(idAggiornamento, note);
                } else {
                    System.out.println("!!! Errore in agg. db !!!");
                }
            } catch (Exception err) {
                if (err.toString().indexOf("Duplicate column name") >= 0) {
                    //se il campo c'è già'
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

    boolean writeLog(int id, String desc) {

        try {
            statLog.execute("insert into log (id,descr) values (" + id + "," + Db.pc(desc, "VARCHAR") + ")");
            return (true);
        } catch (Exception err) {
            err.printStackTrace();
            return (false);
        }
    }

    boolean checkLog(int id) {
//        try {
//            ResultSet temp = statLog.executeQuery("select * from log where id = " + id);
//            if (temp.next() == true) {
//                //trovato il log desiderato
//                return (true);
//            } else {
//                return (false);
//            }
//        } catch (Exception err) {
//            err.printStackTrace();
//            //System.exit(1);
//            return (false);
//        }
        return (logs.contains(Integer.valueOf(id)));
    }

    private String aggiornamento2_1() {

        String temp = "alter table clie_forn ";
        temp += "add cfiscale varchar(16),";
        temp += "add pagamento varchar(20),";
        temp += "add banca varchar(30),";
        temp += "add cantiere_indirizzo varchar(50),";
        temp += "add cantiere_cap varchar(10),";
        temp += "add cantiere_localita varchar(30),";
        temp += "add cantiere_provincia varchar(2),";
        temp += "add finanziamento char(1),";
        temp += "add documenti_iva_agevolata char(1),";
        temp += "add aliquota varchar(5)";

        return (temp);
    }

    private String aggiornamento2_2() {

        String temp = "alter table prev_test ";
        temp += "add cantiere_indirizzo varchar(50),";
        temp += "add cantiere_cap varchar(10),";
        temp += "add cantiere_localita varchar(30),";
        temp += "add cantiere_provincia varchar(2),";
        temp += "add finanziamento char(1),";
        temp += "add documenti_iva_agevolata char(1),";
        temp += "add aliquota varchar(5),";
        temp += "add stato char(1) default 'P' not null ";

        return (temp);
    }

    private String aggiornamento4_1() {

        String temp = "create table log (";
        temp += "id int not null,";
        temp += "descr varchar(255),";
        temp += "data timestamp not null, PRIMARY KEY (id)) ENGINE=MyISAM";

        return (temp);
    }

    private String aggiornamento4_2() {

        String temp = "alter table prev_test ";
        temp += "add cantiere_nome varchar(100),";
        temp += "add fatturazione_nome varchar(100),";
        temp += "add fatturazione_cfiscale varchar(16),";
        temp += "add fatturazione_piva varchar(16),";
        temp += "add fatturazione_indirizzo varchar(50),";
        temp += "add fatturazione_cap varchar(10),";
        temp += "add fatturazione_localita varchar(30),";
        temp += "add fatturazione_provincia varchar(2)";

        return (temp);
    }

    private String aggiornamento4_3() {

        String temp = "alter table clie_forn ";
        temp += "add cantiere_nome varchar(100)";

        return (temp);
    }

    private String aggiornamento6_1() {

        String temp = "alter table prev_righ ";
        temp += "add stato char(1) default 'P' not null";

        return (temp);
    }

    private String aggiornamento6_2() {

        String temp = "alter table prev_righ ";
        temp += "drop primary key";

        return (temp);
    }

    private String aggiornamento6_3() {

        String temp = "alter table prev_righ ";
        temp += "add primary key (serie,numero,riga,riga_variante,stato)";

        return (temp);
    }

    private String aggiornamento8_1() {

        String temp = "alter table prev_test ";
        temp += "add data_ordine date";

        return (temp);
    }

    private String aggiornamento24_1() {

        String temp = "ALTER TABLE prev_righ ";
        temp += "modify f_tipo_legno varchar(100)";

        return (temp);
    }

    private String aggiornamento25_1() {

        String temp = "ALTER TABLE prev_righ ";
        temp += "add f_vetro varchar(50)";

        return (temp);
    }

    private String aggiornamento26_1() {

        String temp = "alter table portoni ";
        temp += "drop primary key";

        return (temp);
    }

    private String aggiornamento26_2() {

        String temp = "alter table portoni ";
        temp += "add primary key (tipo_articolo, tipo_legno, tipo_laccatura, tipo_portone, sp, ante)";

        return (temp);
    }

    private String aggiornamento27_1() {

        String temp = "ALTER TABLE portoni ";
        temp += "modify tipo_portone varchar(10) not null";

        return (temp);
    }

    private String aggiornamento27_2() {

        String temp = "ALTER TABLE portoni_varianti ";
        temp += "modify tipo_portone varchar(10)";

        return (temp);
    }

    private String aggiornamento28_1() {

        String temp = "ALTER TABLE prev_righ ";
        temp += "add f_vetro_pendenza varchar(1),";
        temp += "add f_vetro_centinato varchar(1)";

        return (temp);
    }

    private String aggiornamento29_1() {

        String temp = "ALTER TABLE prev_test ";
        temp += "add prn_prezzi_lordi varchar(1)";

        return (temp);
    }

    private String aggiornamento47_1() {

        String sql = "" + "CREATE TABLE `test_ordi` (" + "`serie` char(1) NOT NULL default ''," + "`numero` int(10) unsigned NOT NULL default '0'," + "`anno` int(10) unsigned NOT NULL default '0'," + "`cliente` int(10) unsigned NOT NULL default '0'," + "`cliente_destinazione` int(10) unsigned default NULL," + "`data` date NOT NULL default '0000-00-00'," + "`data_consegna` varchar(255) default NULL," + "`pagamento` varchar(35) default NULL," + "`banca_abi` varchar(5) default NULL," + "`banca_cab` varchar(5) default NULL," + "`banca_cc` varchar(35) default NULL," + "`spese_varie` decimal(12,2) default NULL," + "`note` text," + "`note_testa` text," + "`note_corpo` text," + "`note_piede` text," + "`totale_imponibile` decimal(12,2) default NULL," + "`totale_iva` decimal(12,2) default NULL," + "`totale` decimal(12,2) default NULL," + "`sconto1` decimal(4,2) default NULL," + "`sconto2` decimal(4,2) default NULL," + "`riferimento` varchar(255) default NULL," + "`sconto3` decimal(4,2) default NULL," + "`stato` char(1) NOT NULL default ''," + "`codice_listino` tinyint(3) unsigned NOT NULL default '1'," + "`stampato` datetime NOT NULL default '0000-00-00 00:00:00'," + "`spese_trasporto` decimal(10,2) default NULL," + "`spese_incasso` decimal(10,5) default NULL," + "`dest_ragione_sociale` varchar(100) default NULL," + "`dest_indirizzo` varchar(50) default NULL," + "`dest_cap` varchar(10) default NULL," + "`dest_localita` varchar(30) default NULL," + "`dest_provincia` char(2) default NULL," + "`dest_telefono` varchar(20) default NULL," + "`dest_cellulare` varchar(20) default NULL," + "`tipo_fattura` tinyint(4) default NULL," + "`aspetto_esteriore_beni` varchar(255) default NULL," + "`numero_colli` varchar(30) default NULL," + "`peso_lordo` varchar(20) default NULL," + "`peso_netto` varchar(20) default NULL," + "`vettore1` varchar(255) default NULL," + "`porto` varchar(100) default NULL," + "`causale_trasporto` varchar(100) default NULL," + "`mezzo_consegna` varchar(25) default NULL," + "`opzione_riba_dest_diversa` char(1) default 'N'," + "`note_pagamento` text," + "`agente_codice` int(11) default NULL," + "`agente_percentuale` decimal(3,2) default '0.00'," + "PRIMARY KEY  (`serie`,`numero`,`anno`)" + ") ENGINE=MyISAM";

        return sql;
    }

    private String aggiornamento47_2() {

        String sql = "" + "CREATE TABLE `righ_ordi` (" + "`serie` char(1) NOT NULL default ''," + "`numero` int(10) unsigned NOT NULL default '0'," + "`anno` int(10) unsigned NOT NULL default '0'," + "`riga` int(10) unsigned NOT NULL default '0'," + "`codice_articolo` varchar(20) default NULL," + "`descrizione` varchar(255) default NULL," + "`um` char(3) default NULL," + "`quantita` decimal(8,2) default NULL," + "`prezzo` decimal(12,5) NOT NULL default '0.00000'," + "`iva` decimal(4,2) default NULL," + "`sconto1` decimal(4,2) default NULL," + "`sconto2` decimal(4,2) default NULL," + "`stato` char(1) NOT NULL default ''," + "`ddt_serie` char(1) NOT NULL default ''," + "`ddt_numero` int(10) unsigned NOT NULL default '0'," + "`ddt_anno` int(10) unsigned NOT NULL default '0'," + "`ddt_riga` int(10) unsigned NOT NULL default '0'," + "`riga_speciale` char(1) default NULL," + "PRIMARY KEY  (`serie`,`numero`,`riga`,`anno`)," + "FULLTEXT KEY `codice_articolo` (`codice_articolo`)" + ") ENGINE=MyISAM";

        return sql;
    }

    private void cambioCampoIva(String tab) {
        String sql = "";
        sql = "alter table " + tab + " add column iva_conv varchar(100)";
        Db.executeSql(sql);

        sql = "update " + tab + " set iva_conv = cast(iva as UNSIGNED)";
        Db.executeSql(sql);

        sql = "update " + tab + " set iva = null";
        Db.executeSql(sql);

        sql = "alter table " + tab + " modify column iva char(3) null";
        Db.executeSql(sql);

        sql = "update " + tab + " set iva = iva_conv";
        Db.executeSql(sql);
    }


    public void esegui_aggiornamenti() {
        MicroBench mb = new MicroBench();
        mb.start();

        System.out.println("inizio controllo aggiornamenti db");

        if (splash != null) {
            splash.jProgressBar1.setIndeterminate(true);
        }

        int idAggiornamento = 0;

        try {
//            //1
//            idAggiornamento = 1;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "create table log (" + "id int not null," + "descr varchar(255)," + "data timestamp not null, PRIMARY KEY (id))";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) == true) {
//                        writeLog(idAggiornamento, "creo tabella log");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            //2
//            idAggiornamento = 2;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "insert into pagamenti (codice, descrizione) values ('','')";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) == true) {
//                        writeLog(idAggiornamento, "aggiungo pagamento vuoto");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            //3
//            idAggiornamento = 3;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 =
//                        "alter table test_ddt add aspetto_esteriore_beni varchar(255) NULL," + " add numero_colli varchar(30) NULL," + " add peso_lordo varchar(20) NULL," + " add peso_netto varchar(20) NULL," + " add vettore1 varchar(255) NULL," + " add porto varchar(100) NULL," + " add causale_trasporto varchar(100) NULL";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) == true) {
//                        writeLog(idAggiornamento, "aggiungo campi per ddt");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            //4
//            idAggiornamento = 4;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 =
//                        "alter table test_ddt add spese_trasporto decimal(10,2) NULL," + " add spese_incasso decimal(10,5) NULL";
//                String sql2 =
//                        "alter table test_fatt add spese_trasporto decimal(10,2) NULL," + " add spese_incasso decimal(10,5) NULL";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) == true && Db.executeSql(sql2) == true) {
//                        writeLog(idAggiornamento, "aggiungo campi spese trasporto e spese incasso");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            //5
//            idAggiornamento = 5;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "alter table test_ddt add mezzo_consegna varchar(25) NULL";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) == true) {
//                        writeLog(idAggiornamento, "aggiungo campo mezzo consegna");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            //6
//            idAggiornamento = 6;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "alter table pagamenti add riba char(1) NULL DEFAULT 'N'";
//                String sql2 = "update pagamenti set riba = 'S' where codice like 'R.B%'";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) == true && Db.executeSql(sql2) == true) {
//                        writeLog(idAggiornamento, "aggiungo campo riba in pagamenti");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            //7
//            idAggiornamento = 7;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "alter table righ_fatt modify prezzo decimal(12,5) NOT NULL default 0";
//                String sql2 = "alter table righ_fatt_temp modify prezzo decimal(12,5) NOT NULL default 0";
//                String sql3 = "alter table righ_ddt modify prezzo decimal(12,5) NOT NULL default 0";
//                String sql4 = "alter table righ_ddt_temp modify prezzo decimal(12,5) NOT NULL default 0";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) == true && Db.executeSql(sql2) == true && Db.executeSql(sql3) == true && Db.executeSql(sql4) == true) {
//                        writeLog(idAggiornamento, "allargo i decimali dei prezzi unitari in righe");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            //8
//            idAggiornamento = 8;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "alter table clie_forn_dest add cellulare varchar(20) NULL";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) == true) {
//                        writeLog(idAggiornamento, "aggiungo cellulare in clie_forn_dest");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            //9
//            idAggiornamento = 9;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "alter table test_ddt add dest_ragione_sociale varchar(100) NULL";
//                sql1 += ", add dest_indirizzo varchar(50) NULL";
//                sql1 += ", add dest_cap varchar(10) NULL";
//                sql1 += ", add dest_localita varchar(30) NULL";
//                sql1 += ", add dest_provincia varchar(2) NULL";
//                sql1 += ", add dest_telefono varchar(20) NULL";
//                sql1 += ", add dest_cellulare varchar(20) NULL";
//
//                String sql2 = "alter table test_fatt add dest_ragione_sociale varchar(100) NULL";
//                sql2 += ", add dest_indirizzo varchar(50) NULL";
//                sql2 += ", add dest_cap varchar(10) NULL";
//                sql2 += ", add dest_localita varchar(30) NULL";
//                sql2 += ", add dest_provincia varchar(2) NULL";
//                sql2 += ", add dest_telefono varchar(20) NULL";
//                sql2 += ", add dest_cellulare varchar(20) NULL";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) == true && Db.executeSql(sql2) == true) {
//                        writeLog(idAggiornamento, "aggiungo in testate documenti i dati delle destinazioni diverse");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            //10
//            idAggiornamento = 10;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "alter table clie_forn add cellulare varchar(20) NULL";
//
//                try {
//                    
//                    Db.executeSql(sql1);
//                    writeLog(idAggiornamento, "aggiungo cellulare in clie_forn");
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            //11
//            idAggiornamento = 11;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "alter table test_ddt add fattura_serie char(1) NULL" + ", add fattura_numero int unsigned NULL" + ", add fattura_anno int unsigned NULL";
//
//                try {
//                    
//                    Db.executeSql(sql1);
//                    writeLog(idAggiornamento, "aggiungo campi id fattura su bolle");
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            //12
//            idAggiornamento = 12;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "update test_ddt set fattura_numero = NULL, fattura_anno = NULL";
//
//                try {
//                    
//                    Db.executeSql(sql1);
//                    writeLog(idAggiornamento, "aggiungo campi id fattura su bolle");
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            //13
//            idAggiornamento = 13;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "CREATE TABLE articoli_prezzi (";
//                sql1 += " articolo varchar(20) NOT NULL";
//                sql1 += ", listino tinyint NOT NULL";
//                sql1 += ", prezzo decimal(12,5) NOT NULL";
//                sql1 += ", PRIMARY KEY (articolo, listino))";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) == true) {
//                        writeLog(idAggiornamento, "aggiungo tabella prezzi listini");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            //14
//            idAggiornamento = 14;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "CREATE TABLE tipi_causali_magazzino (";
//                sql1 += " codice smallint NOT NULL";
//                sql1 += ", descrizione varchar(255) NOT NULL";
//                sql1 += ", segno tinyint NOT NULL";
//                sql1 += ", PRIMARY KEY (codice))";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) == true) {
//                        writeLog(idAggiornamento, "aggiungo tabella tipi causali magazzino");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            //15
//            idAggiornamento = 15;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "CREATE TABLE movimenti_magazzino (";
//                sql1 += " id integer NOT NULL auto_increment";
//                sql1 += ", data date NOT NULL";
//                sql1 += ", causale smallint NOT NULL";
//                sql1 += ", deposito smallint NOT NULL";
//                sql1 += ", articolo varchar(20) NOT NULL";
//                sql1 += ", quantita decimal(8,2) NOT NULL";
//                sql1 += ", note varchar(255)";
//                sql1 += ", PRIMARY KEY (id))";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) == true) {
//                        writeLog(idAggiornamento, "aggiungo tabella movimenti magazzino");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            //16
//            idAggiornamento = 16;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "insert into tipi_causali_magazzino (codice, descrizione, segno) values (1, 'Esistenza iniziale', 1)";
//                String sql2 = "insert into tipi_causali_magazzino (codice, descrizione, segno) values (2, 'Carico magazzino', 1)";
//                String sql3 = "insert into tipi_causali_magazzino (codice, descrizione, segno) values (3, 'Scarico magazzino', -1)";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) == true && Db.executeSql(sql2) == true && Db.executeSql(sql3) == true) {
//                        writeLog(idAggiornamento, "aggiungo causali magazzino standard");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            //105
//            //17
//            idAggiornamento = 17;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "ALTER TABLE movimenti_magazzino ";
//                sql1 += " add da_tabella VARCHAR(20) NULL";
//                sql1 += ", add da_serie CHAR(1) NULL";
//                sql1 += ", add da_numero INTEGER NULL";
//                sql1 += ", add da_anno SMALLINT NULL";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) == true) {
//                        writeLog(idAggiornamento, "aggiungo campi in movimenti magazzino per sapere provenienza");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            //18
//            idAggiornamento = 18;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "ALTER TABLE articoli ";
//                sql1 += " add pezzi VARCHAR(20) NULL";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1, false)) {
//                        writeLog(idAggiornamento, "aggiungo campo pezzi in articoli");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            //19
//            idAggiornamento = 19;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "ALTER TABLE test_fatt ";
//                sql1 += " add tipo_fattura tinyint NULL";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) == true) {
//                        writeLog(idAggiornamento, "aggiungo campo tipo_fattura, per note di credito");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            //20
//            idAggiornamento = 20;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "ALTER TABLE clie_forn ";
//                sql1 += " add paese varchar(2) NULL";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) == true) {
//                        writeLog(idAggiornamento, "aggiungo campo paese in clienti");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            //21
//            idAggiornamento = 21;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 =
//                        "alter table test_fatt add aspetto_esteriore_beni varchar(255) NULL," + " add numero_colli varchar(30) NULL," + " add peso_lordo varchar(20) NULL," + " add peso_netto varchar(20) NULL," + " add vettore1 varchar(255) NULL," + " add porto varchar(100) NULL," + " add causale_trasporto varchar(100) NULL," + " add mezzo_consegna varchar(25) NULL";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) == true) {
//                        writeLog(idAggiornamento, "aggiungo campi per fattura");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            //22
//            idAggiornamento = 22;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "ALTER TABLE articoli ";
//                sql1 += " add pezzi VARCHAR(20) NULL";
//
//                try {
//                    
//                    Db.executeSql(sql1, false);
//                    writeLog(idAggiornamento, "aggiungo campo pezzi in articoli");
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            //23
//            idAggiornamento = 23;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "ALTER TABLE articoli ";
//                sql1 += " add pezzi VARCHAR(20) NULL";
//
//                try {
//                    
//                    Db.executeSql(sql1, false);
//                    writeLog(idAggiornamento, "aggiungo campo pezzi in articoli");
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            //109
//            //24
//            idAggiornamento = 24;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "ALTER TABLE articoli ";
//                sql1 += " add descrizione_eng VARCHAR(255) NULL";
//                sql1 += " , add um_eng VARCHAR(3) NULL";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1, false)) {
//                        writeLog(idAggiornamento, "aggiungo campi per desc. in inglese");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            //110
//            //25
//            idAggiornamento = 25;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "update tipi_causali_magazzino";
//                sql1 += " set segno = 1 where codice = 1";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1, false)) {
//                        writeLog(idAggiornamento, "imposto esistenza iniziale a segno 1");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            //122
//            //26
//            idAggiornamento = 26;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "" + "CREATE TABLE agenti (" + " id int(10) NOT NULL default '0'," + " nome varchar(255) NOT NULL default ''," + " indirizzo varchar(255) NOT NULL default ''," + " telefono varchar(255) NOT NULL default ''," + " localita varchar(255) NOT NULL default ''," + " cap int(5) NOT NULL default '0'," + " provincia char(2) NOT NULL default ''," + " note text NOT NULL," + " percentuale int(2) NOT NULL default '0'," + " PRIMARY KEY (id)" + ")";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1)) {
//                    }
//
//                    writeLog(idAggiornamento, "creo la tabella agenti");
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            //27
//            idAggiornamento = 27;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "ALTER TABLE clie_forn add agente INT NULL";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1)) {
//                    }
//
//                    writeLog(idAggiornamento, "aggiungo agente in clienti");
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            //28
//            if (main.getPersonalContain(main.PERSONAL_TLZ)) {
//                idAggiornamento = 28;
//
//                if (checkLog(idAggiornamento) == false) {
//
//                    String sql1 = "delete from tipi_listino";
//                    String sql2 = "insert into tipi_listino values (1, 'RIVENDITORI')";
//                    String sql3 = "insert into tipi_listino values (2, 'UTILIZZATORI')";
//
//                    try {
//                        
//
//                        if (Db.executeSql(sql1)) {
//                        }
//
//                        if (Db.executeSql(sql2)) {
//                        }
//
//                        if (Db.executeSql(sql3)) {
//                        }
//
//                        writeLog(idAggiornamento, "aggiusto listini per tlz");
//                        
//                    } catch (Exception err) {
//                        err.printStackTrace();
//                    }
//                }
//            }
//
//            //per nora aggiungo una tabella legata alle righe dei documenti per dati aggiuntivi sugli animali
//            //29
//            if (main.getPersonalContain(main.PERSONAL_CHIANTICASHMERE)) {
//                idAggiornamento = 29;
//
//                if (checkLog(idAggiornamento) == false) {
//
//                    String sql1 = "create table righ_animali (tipo_documento tinyint not null, serie char(1) not null, numero int not null, anno int not null, riga int not null, numero_allevamento varchar(50), numero_microchip varchar(50), sesso char(1), note_allegato_usl varchar(255), numero_data_bolla_mattatoio varchar(50), peso_carcassa varchar(30), prezzo_kg decimal(12,5), PRIMARY KEY (tipo_documento, serie, numero, anno, riga))";
//
//                    try {
//                        
//
//                        if (Db.executeSql(sql1)) {
//                            writeLog(idAggiornamento, "aggiungo dati per animali");
//                        }
//
//                        
//                    } catch (Exception err) {
//                        err.printStackTrace();
//                    }
//                }
//            }
//
//            //Aggiungo la tabella dei codici iva
//            idAggiornamento = 30;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "create table codici_iva (codice varchar(3) not null, percentuale decimal(4,3) not null,  descrizione varchar(30) not null, descrizione_breve varchar(15), PRIMARY KEY (codice))";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1)) {
//                        writeLog(idAggiornamento, "aggiungo tabella codici iva");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 31;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "insert into codici_iva values ('20', 20, 'Iva 20%', '')";
//                String sql2 = "insert into codici_iva values ('10', 10, 'Iva 10%', '')";
//                String sql3 = "insert into codici_iva values ('4', 4, 'Iva 4%', '')";
//                String sql4 = "insert into codici_iva values ('8', 0, 'Non imponibile art.8', 'Non Imp. art.8')";
//                String sql5 = "insert into codici_iva values ('2', 0, 'Non imponibile art.2', 'Non Imp. art.2')";
//                String sql6 = "insert into codici_iva values ('15', 0, 'Esente IVA art.15', 'Es.IVA art.15')";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) && Db.executeSql(sql2) && Db.executeSql(sql3) && Db.executeSql(sql4) && Db.executeSql(sql5) && Db.executeSql(sql6)) {
//                        writeLog(idAggiornamento, "aggiungo tabella codici iva - dati");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 32;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "alter table clie_forn add opzione_riba_dest_diversa char(1) default 'N'";
//                String sql2 = "alter table test_fatt add opzione_riba_dest_diversa char(1) default 'N'";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) && Db.executeSql(sql2)) {
//                        writeLog(idAggiornamento, "aggiungo campi per stampare in riba la dest diversa della fattura");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 33;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "alter table pagamenti add note_su_documenti TEXT";
//                String sql2 = "alter table test_fatt add note_pagamento TEXT";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) && Db.executeSql(sql2)) {
//                        writeLog(idAggiornamento, "aggiungo campi per stampare sui documenti lengthnote del pagamento");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 34;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "alter table pagamenti add flag_pagata char(1)";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1)) {
//                        writeLog(idAggiornamento, "aggiungo campo sul pagamento per sapere se inserire le scadenze come gia' pagate o no");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 35;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "create table storico (id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY, data timestamp, nota text)";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1)) {
//                        writeLog(idAggiornamento, "aggiungo tabella storico per vedere cosa falsenelle fatture e scadenze");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 36;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "create table tipi_fatture (tipo TINYINT NOT NULL PRIMARY KEY, descrizione_breve VARCHAR(3), descrizione VARCHAR(100))";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1)) {
//                        writeLog(idAggiornamento, "aggiungo tabella tipi fatture");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 37;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql2 = "insert into tipi_fatture values (0, 'XX','FATTURA NON IDENTIFICATA')";
//                String sql3 = "insert into tipi_fatture values (1, 'FI','FATTURA IMMEDIATA')";
//                String sql4 = "insert into tipi_fatture values (2, 'FA','FATTURA ACCOMPAGNATORIA')";
//                String sql5 = "insert into tipi_fatture values (3, 'NC','NOTA DI CREDITO')";
//                String sql6 = "insert into tipi_fatture values (4, 'CS','SEMPLICE')";
//                String sql7 = "insert into tipi_fatture values (5, 'CT','TICKET')";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql2) && Db.executeSql(sql3) && Db.executeSql(sql4) && Db.executeSql(sql5) && Db.executeSql(sql6) && Db.executeSql(sql7)) {
//                        writeLog(idAggiornamento, "aggiungo tipi fatture");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 38;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "update test_fatt set tipo_fattura = 1 where tipo_fattura = 0";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1)) {
//                        writeLog(idAggiornamento, "aggiusto fatture con tipo = 0");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            //sulla tabella agenti era stato creato il campo percentuale come intero invece deve essere un decimale
//            idAggiornamento = 39;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "alter table agenti modify column percentuale decimal(3,2) not null default 0";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1)) {
//                        writeLog(idAggiornamento, "aggiusto tabella agenti");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            //aggiungo i campi per gli agenti sulle fatture
//            idAggiornamento = 40;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "alter table test_fatt add agente_codice int";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1)) {
//                        writeLog(idAggiornamento, "aggiusto campi agente in fatture");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 41;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql2 = "alter table test_fatt add agente_percentuale decimal(3,2) default 0";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql2)) {
//                        writeLog(idAggiornamento, "aggiusto campi agente in fatture");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 42;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "" + "CREATE TABLE `provvigioni` (" + "`numero` tinyint(4) NOT NULL default '0'," + "`id` bigint(20) NOT NULL auto_increment," + "`documento_tipo` char(2) NOT NULL default ''," + "`documento_serie` char(1) NOT NULL default ''," + "`documento_numero` int(10) unsigned NOT NULL default '0'," + "`documento_anno` int(10) unsigned NOT NULL default '0'," + "`data_scadenza` date NOT NULL default '0000-00-00'," + "`pagata` char(1) NOT NULL default ''," + "`importo` decimal(12,2) NOT NULL default '0.00'," + "`importo_provvigione` decimal(12,2) NOT NULL default '0.00'," + "PRIMARY KEY  (`id`)" + ") TYPE=MyISAM;";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1)) {
//                        writeLog(idAggiornamento, "creo tabella provvigioni");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 43;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "update test_fatt set tipo_fattura = 1 where tipo_fattura = 0";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1)) {
//                        writeLog(idAggiornamento, "aggiorno tabella test_dfatt per le fattura create dalle bolle");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 44;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "alter table scadenze add note_pagamento text";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1)) {
//                        writeLog(idAggiornamento, "aggiorno tabella scadenze per memorizzare come hanno pagato");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 45;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "alter table articoli add flag_confezione char(1) default 'N'";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1)) {
//                        writeLog(idAggiornamento, "aggiorno tabella articoli per articoli tipo confezione");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 46;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "CREATE TABLE articoli_confezione (";
//                sql1 += " articolo_padre varchar(20) NOT NULL";
//                sql1 += ", articolo_figlio varchar(20) NOT NULL";
//                sql1 += ", quantita decimal(12,5) NOT NULL";
//                sql1 += ", PRIMARY KEY (articolo_padre, articolo_figlio))";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) == true) {
//                        writeLog(idAggiornamento, "aggiungo tabella articoli per confezione");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 47;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = aggiornamento47_1();
//                String sql2 = aggiornamento47_2();
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) == true && Db.executeSql(sql2) == true) {
//                        writeLog(idAggiornamento, "aggiungo tabelle ordini e sistemo le temp con primary key per username");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 48;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "alter table righ_ddt_temp drop primary key";
//                String sql2 = "alter table righ_ddt_temp add primary key (serie, numero, anno, riga, username)";
//                String sql3 = "alter table righ_fatt_temp drop primary key";
//                String sql4 = "alter table righ_fatt_temp add primary key (serie, numero, anno, riga, username)";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) == true && Db.executeSql(sql2) == true && Db.executeSql(sql3) == true && Db.executeSql(sql4) == true) {
//                        writeLog(idAggiornamento, "sistemo la chiave nelle tabelle temporanee");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 49;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "alter table test_ordi add doc_tipo char(2), add doc_serie char(1), add doc_numero int, add doc_anno int";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) == true) {
//                        writeLog(idAggiornamento, "aggiungo campi per info ordine");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 50;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "alter table righ_ordi drop ddt_serie, drop ddt_numero, drop ddt_anno, drop ddt_riga";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) == true) {
//                        writeLog(idAggiornamento, "tolgo campi ddt_ e poi aggiungo campi doc_tipo numero...");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 51;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "alter table righ_ordi drop ddt_serie, drop ddt_numero, drop ddt_anno, drop ddt_riga";
//
//                try {
//                    
//
//                    //if (Db.executeSql(sql1)==true) {
//                    writeLog(idAggiornamento, "tolgo campi ddt_ e poi aggiungo campi doc_tipo numero...");
//
//                    //}
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 52;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "alter table righ_ordi add doc_tipo char(2), add doc_serie char(1), add doc_numero int, add doc_anno int, add doc_riga int";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) == true) {
//                        writeLog(idAggiornamento, "tolgo campi ddt_ e poi aggiungo campi doc_tipo numero...");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 54;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "alter table tipi_listino modify codice varchar(10) not null";
//                String sql2 = "alter table articoli_prezzi modify listino varchar(10) not null";
//                String sql3 = "alter table clie_forn modify codice_listino varchar(10)";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) == true) {
//                        writeLog(idAggiornamento, "modifico campo listino 1");
//                    }
//
//                    if (Db.executeSql(sql2) == true) {
//                        writeLog(idAggiornamento + 1, "modifico campo listino 2");
//                    }
//
//                    if (Db.executeSql(sql3) == true) {
//                        writeLog(idAggiornamento + 2, "modifico campo listino 3");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 57;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "alter table dati_azienda add listino_base varchar(10) null";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) == true) {
//                        writeLog(idAggiornamento, "aggiungo campo listino base su opzioni azienda");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 58;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "alter table test_ddt add ddt_tv_targa varchar(30) null";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) == true) {
//                        writeLog(idAggiornamento, "aggiungo campo listino base su opzioni azienda");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 59;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "alter table dati_azienda add targa varchar(30) null";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) == true) {
//                        writeLog(idAggiornamento, "aggiungo campo targa su opzioni azienda");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 60;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "select articolo from articoli_prezzi left join articoli on articolo = codice where articoli.codice is null group by articolo";
//
//                try {
//                    
//
//                    ResultSet r = Db.openResultSet(sql1);
//                    String sql2 = "";
//
//                    while (r.next()) {
//                        sql2 = "delete from articoli_prezzi where articolo = " + Db.pc(r.getString(1), Types.VARCHAR);
//                        Db.executeSql(sql2);
//                    }
//
//                    sql2 = "delete from articoli_prezzi where articolo is null";
//                    Db.executeSql(sql2);
//                    writeLog(idAggiornamento, "pulisco tabella articoli_prezzi");
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 61;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "select listino from articoli_prezzi left join tipi_listino on listino = codice where tipi_listino.codice is null group by listino";
//
//                try {
//                    
//
//                    ResultSet r = Db.openResultSet(sql1);
//                    String sql2 = "";
//
//                    while (r.next()) {
//                        sql2 = "delete from articoli_prezzi where listino = " + Db.pc(r.getString(1), Types.VARCHAR);
//                        Db.executeSql(sql2);
//                    }
//
//                    sql2 = "delete from articoli_prezzi where listino is null";
//                    Db.executeSql(sql2);
//                    writeLog(idAggiornamento, "pulisco tabella articoli_prezzi 2 da listini");
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 62;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "create table temp_stampa_stat_ord_bol_fat (hostname varchar(255), tipo_doc_ordine int, tipo_doc varchar(50), serie char(1), numero int, anno int, mese int, data date, cliente varchar(255), totale_imponibile decimal(12,2))";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) == true) {
//                        writeLog(idAggiornamento, "aggiungo tabella temporanea stampa statistiche");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 63;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "alter table pagamenti add giorno_pagamento tinyint";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) == true) {
//                        writeLog(idAggiornamento, "aggiungo campo per pagamenti al 10");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 64;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "alter table test_ordi add giorno_pagamento tinyint";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) == true) {
//                        writeLog(idAggiornamento, "aggiungo campo per pagamenti al 10 ordi");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 65;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "alter table test_ddt add giorno_pagamento tinyint";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) == true) {
//                        writeLog(idAggiornamento, "aggiungo campo per pagamenti al 10 ddt");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 66;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "alter table test_fatt add giorno_pagamento tinyint";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) == true) {
//                        writeLog(idAggiornamento, "aggiungo campo per pagamenti al 10 fatt");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 67;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "alter table pagamenti add flag_richiedi_giorno char(1) default 'N'";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) == true) {
//                        writeLog(idAggiornamento, "aggiungo campo per pagamenti al 10 pagamenti ");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 68;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "alter table clie_forn add giorno_pagamento tinyint";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) == true) {
//                        writeLog(idAggiornamento, "aggiungo campo per pagamenti al 10 clie forn");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 69;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "alter table clie_forn modify pagamento varchar(35)";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) == true) {
//                        writeLog(idAggiornamento, "allargo campo pagamento su clie_forn");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 70;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "alter table righ_ddt add descrizione2 varchar(255)";
//                String sql2 = "alter table righ_ddt_temp add descrizione2 varchar(255)";
//                String sql3 = "alter table righ_fatt add descrizione2 varchar(255)";
//                String sql4 = "alter table righ_fatt_temp add descrizione2 varchar(255)";
//                String sql5 = "alter table righ_ordi add descrizione2 varchar(255)";
//                String sql6 = "alter table righ_ordi_temp add descrizione2 varchar(255)";
//
//                try {
//                    
//                    Db.executeSql(sql1);
//                    Db.executeSql(sql2);
//                    Db.executeSql(sql3);
//                    Db.executeSql(sql4);
//                    Db.executeSql(sql5);
//                    Db.executeSql(sql6);
//                    writeLog(idAggiornamento, "aggiungo campo descrizione2 sulle righe");
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            //per gestione iva
//            idAggiornamento = 71;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "create table prima_nota_teste (";
//                sql1 += " id int auto_increment not null primary key";
//                sql1 += " , numero int not null";
//                sql1 += " , data date not null";
//                sql1 += " , causale varchar(2) not null";
//                sql1 += " , numero_doc_esterno int";
//                sql1 += " , serie_doc_esterno char(1)";
//                sql1 += " , data_doc_esterno date";
//                sql1 += " , protocollo int"; //numerazione interna (andr? per causale ?)
//                sql1 += " , data_comp_iva date";
//                sql1 += " )";
//
//                String sql2 = "create table prima_nota_righe (";
//                sql2 += " id int auto_increment not null primary key";
//                sql2 += " , id_prima_nota int not null";
//                sql2 += " , riga smallint not null";
//                sql2 += " , conto_dare mediumint";
//                sql2 += " , conto_avere mediumint";
//                sql2 += " , cliente_fornitore int";
//                sql2 += " , causale varchar(2) not null";
//                sql2 += " , importo decimal(15,5)";
//                sql2 += " , descrizione varchar(255)";
//                sql2 += " , conto_iva mediumint";
//                sql2 += " )";
//
//                String sql3 = "create table prima_nota_iva (";
//                sql3 += " id int auto_increment not null primary key";
//                sql3 += " , id_prima_nota int not null";
//                sql3 += " , riga smallint not null";
//                sql3 += " , codice_iva char(3) not null";
//                sql3 += " , percentuale_iva decimal(3,2) not null";
//                sql3 += " , imponibile decimal(15,5) not null";
//                sql3 += " , iva decimal(15,5) not null";
//                sql3 += " )";
//
//                try {
//                    
//                    Db.executeSql(sql1);
//                    Db.executeSql(sql2);
//                    Db.executeSql(sql3);
//                    writeLog(idAggiornamento, "aggiungo tabelle prima nota");
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 72;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "create table fatture_ricevute_teste (";
//                sql1 += " id int auto_increment not null primary key";
//                sql1 += " , serie char(1)";
//                sql1 += " , numero int not null";
//                sql1 += " , anno int not null";
//                sql1 += " , data date not null";
//                sql1 += " , numero_doc int not null";
//                sql1 += " , serie_doc char(1)";
//                sql1 += " , data_doc date not null";
//                sql1 += " , fornitore int";
//                sql1 += " , importo decimal(15,5)";
//                sql1 += " , imponibile decimal(15,5)";
//                sql1 += " , iva decimal(15,5)";
//                sql1 += " , descrizione varchar(255)";
//                sql1 += " , UNIQUE (serie, numero, anno)";
//                sql1 += " )";
//
//                String sql2 = "create table fatture_ricevute_iva (";
//                sql2 += " id int auto_increment not null primary key";
//                sql2 += " , id_fattura int not null";
//                sql2 += " , codice_iva char(3) not null";
//                sql2 += " , percentuale_iva decimal(3,2) not null";
//                sql2 += " , imponibile decimal(15,5) not null";
//                sql2 += " , iva decimal(15,5) not null";
//                sql2 += " , importo decimal(15,5)";
//                sql2 += " )";
//
//                try {
//                    
//                    Db.executeSql(sql1);
//                    Db.executeSql(sql2);
//                    writeLog(idAggiornamento, "aggiungo tabelle fatture ricevute");
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 73;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "update clie_forn set tipo = 'C'";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) == true) {
//                        writeLog(idAggiornamento, "inizializzo i clienti come clienti");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 74;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "alter table dati_azienda add tipo_liquidazione_iva varchar(50) null";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1, false) == true) {
//                        writeLog(idAggiornamento, "aggiungo campo tipo liquidazione iva su opzioni azienda");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 75;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "create table stampa_iva_semplice (";
//                sql1 += "id int auto_increment not null primary key";
//                sql1 += ", tipo char(1)";
//                sql1 += ", data date";
//                sql1 += ", numero_prog varchar(20)";
//                sql1 += ", numero_doc varchar(20)";
//                sql1 += ", ragione_sociale varchar(255)";
//                sql1 += ", totale decimal(15,5)";
//                sql1 += ", imp1 decimal(15,5)";
//                sql1 += ", iva1 decimal(15,5)";
//                sql1 += ", imp2 decimal(15,5)";
//                sql1 += ", iva2 decimal(15,5)";
//                sql1 += ", imp3 decimal(15,5)";
//                sql1 += ", iva3 decimal(15,5)";
//                sql1 += ", imp4 decimal(15,5)";
//                sql1 += ", iva4 decimal(15,5)";
//                sql1 += ", imp5 decimal(15,5)";
//                sql1 += ", iva5 decimal(15,5)";
//                sql1 += ", var_imp decimal(15,5)";
//                sql1 += ", var_iva decimal(15,5)";
//                sql1 += ", imp_non_imp decimal(15,5)";
//                sql1 += ", imp_esenti decimal(15,5)";
//                sql1 += ", altre_imp decimal(15,5)";
//                sql1 += ", altre_iva varchar(20)";
//                sql1 += " )";
//
//                try {
//                    
//
//                    if (Db.executeSql(sql1) == true) {
//                        writeLog(idAggiornamento, "creata tabella per stampa iva");
//                    }
//
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 76;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "alter table distinte_riba add abi varchar(5)";
//                String sql2 = "alter table distinte_riba add cab varchar(5)";
//                String sql3 = "alter table distinte_riba add cc varchar(25)";
//                String sql4 = "alter table distinte_riba add note_cc varchar(255)";
//                String sql5 = "alter table distinte_riba add desc_sint_banca varchar(100)";
//
//                try {
//                    
//                    Db.executeSql(sql1);
//                    Db.executeSql(sql2);
//                    Db.executeSql(sql3);
//                    Db.executeSql(sql4);
//                    Db.executeSql(sql5);
//                    writeLog(idAggiornamento, "aggiungo campo desc banca in distinte riba");
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 77;
//
//            if (checkLog(idAggiornamento) == false) {
//
//                String sql1 = "alter table fatture_ricevute_teste modify data_doc date NULL";
//                String sql2 = "alter table fatture_ricevute_teste modify numero_doc int NULL";
//
//                try {
//                    
//                    Db.executeSql(sql1);
//                    Db.executeSql(sql2);
//                    writeLog(idAggiornamento, "modifico fatture ricevute");
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 78;
//            if (checkLog(idAggiornamento) == false) {
//                String sql1 = "" + "CREATE TABLE vettori (" + " id int(10) NOT NULL default '0'," + " nome varchar(255) NOT NULL default ''," + " PRIMARY KEY (id)" + ")";
//                try {
//                    
//                    if (Db.executeSql(sql1)) {
//                    }
//                    writeLog(idAggiornamento, "creo la tabella vettori");
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 79;
//            if (checkLog(idAggiornamento) == false) {
//                String sql1 = "" + "ALTER TABLE fatture_ricevute_teste add sconto1 decimal(5,2), add sconto2 decimal(5,2), add sconto3 decimal(5,2)";
//                try {
//                    
//                    if (Db.executeSql(sql1)) {
//                    }
//                    writeLog(idAggiornamento, "aggiugno campi alla fatt ricevute per gestione carichi");
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 80;
//            if (checkLog(idAggiornamento) == false) {
//                String sql1 = "" + "ALTER TABLE movimenti_magazzino add matricola varchar(255)";
//                try {
//                    
//                    Db.executeSql(sql1);
//                    writeLog(idAggiornamento, "aggiungo campo matricola ai movimenti");
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 81;
//            if (checkLog(idAggiornamento) == false) {
//                String sql1 = "ALTER TABLE dati_azienda change preventivo_int_riga1 intestazione_riga1 VARCHAR(255)";
//                String sql2 = "ALTER TABLE dati_azienda change preventivo_int_riga2 intestazione_riga2 VARCHAR(255)";
//                String sql3 = "ALTER TABLE dati_azienda change preventivo_int_riga3 intestazione_riga3 VARCHAR(255)";
//                String sql4 = "ALTER TABLE dati_azienda change preventivo_int_riga4 intestazione_riga4 VARCHAR(255)";
//                String sql5 = "ALTER TABLE dati_azienda change preventivo_int_riga5 intestazione_riga5 VARCHAR(255)";
//                String sql6 = "ALTER TABLE dati_azienda change preventivo_int_riga6 intestazione_riga6 VARCHAR(255)";
//                try {
//                    
//                    Db.executeSql(sql1);
//                    Db.executeSql(sql2);
//                    Db.executeSql(sql3);
//                    Db.executeSql(sql4);
//                    Db.executeSql(sql5);
//                    Db.executeSql(sql6);
//                    writeLog(idAggiornamento, "modifico dati azienda con intestazione...");
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 82;
//            if (checkLog(idAggiornamento) == false) {
//                String sql1 = "" +
//                        "ALTER TABLE `dati_azienda` ADD COLUMN `id` int(11) NULL;\n" +
//                        "ALTER TABLE `dati_azienda` ADD COLUMN `piva` varchar(20) NULL;\n" +
//                        "ALTER TABLE `dati_azienda` ADD COLUMN `logo` mediumblob NULL;\n" +
//                        "ALTER TABLE `fatture_ricevute_teste` ADD COLUMN `spese_varie` decimal(12,2) NULL;\n" +
//                        "ALTER TABLE `fatture_ricevute_teste` ADD COLUMN `note` text NULL;\n" +
//                        "ALTER TABLE `fatture_ricevute_teste` ADD COLUMN `riferimento` varchar(255) NULL;\n" +
//                        "ALTER TABLE `fatture_ricevute_teste` ADD COLUMN `spese_trasporto` decimal(15,5) NULL;\n" +
//                        "ALTER TABLE `fatture_ricevute_teste` ADD COLUMN `spese_incasso` decimal(15,5) NULL;\n" +
//                        "CREATE TABLE `righ_ddt_matricole` (" +
//                        "  `serie` char(1) NOT NULL," +
//                        "  `numero` int(11) NOT NULL," +
//                        "  `anno` int(11) NOT NULL," +
//                        "  `riga` int(11) NOT NULL," +
//                        "  `matricola` varchar(255) NOT NULL," +
//                        "  PRIMARY KEY  (`serie`,`numero`,`anno`,`riga`,`matricola`)" +
//                        ");\n" +
//                        "CREATE TABLE `righ_fatt_matricole` (" +
//                        "  `serie` char(1) NOT NULL," +
//                        "  `numero` int(11) NOT NULL," +
//                        "  `anno` int(11) NOT NULL," +
//                        "  `riga` int(11) NOT NULL," +
//                        "  `matricola` varchar(255) NOT NULL," +
//                        "  PRIMARY KEY  (`serie`,`numero`,`anno`,`riga`,`matricola`)" +
//                        ");\n" +
//                        "CREATE TABLE `righ_fatt_ricevute` (" +
//                        "  `serie` char(1) NOT NULL default ''," +
//                        "  `numero` int(10) unsigned NOT NULL default '0'," +
//                        "  `anno` int(10) unsigned NOT NULL default '0'," +
//                        "  `riga` int(10) unsigned NOT NULL default '0'," +
//                        "  `codice_articolo` varchar(20) default NULL," +
//                        "  `descrizione` varchar(255) default NULL," +
//                        "  `um` char(3) default NULL," +
//                        "  `quantita` decimal(8,2) default NULL," +
//                        "  `prezzo` decimal(12,5) NOT NULL default '0.00000'," +
//                        "  `iva` decimal(4,2) default NULL," +
//                        "  `sconto1` decimal(4,2) default NULL," +
//                        "  `sconto2` decimal(4,2) default NULL," +
//                        "  `stato` char(1) NOT NULL default ''," +
//                        "  `ddt_serie` char(1) NOT NULL default ''," +
//                        "  `ddt_numero` int(10) unsigned NOT NULL default '0'," +
//                        "  `ddt_anno` int(10) unsigned NOT NULL default '0'," +
//                        "  `ddt_riga` int(10) unsigned NOT NULL default '0'," +
//                        "  `riga_speciale` char(1) default NULL," +
//                        "  `descrizione2` varchar(255) default NULL," +
//                        "  `matricola` varchar(255) default NULL," +
//                        "  PRIMARY KEY  (`serie`,`numero`,`riga`,`anno`)," +
//                        "  FULLTEXT KEY `codice_articolo` (`codice_articolo`)" +
//                        ");\n" +
//                        "CREATE TABLE `righ_fatt_ricevute_matricole` (" +
//                        "  `serie` char(1) NOT NULL," +
//                        "  `numero` int(11) NOT NULL," +
//                        "  `anno` int(11) NOT NULL," +
//                        "  `riga` int(11) NOT NULL," +
//                        "  `matricola` varchar(255) NOT NULL," +
//                        "  PRIMARY KEY  (`serie`,`numero`,`anno`,`riga`,`matricola`)" +
//                        ");\n" +
//                        "CREATE TABLE `righ_fatt_ricevute_temp` (" +
//                        "  `serie` char(1) NOT NULL default ''," +
//                        "  `numero` int(10) unsigned NOT NULL default '0'," +
//                        "  `anno` int(10) unsigned NOT NULL default '0'," +
//                        "  `riga` int(10) unsigned NOT NULL default '0'," +
//                        "  `codice_articolo` varchar(20) default NULL," +
//                        "  `descrizione` varchar(255) default NULL," +
//                        "  `um` char(3) default NULL," +
//                        "  `quantita` decimal(8,2) default NULL," +
//                        "  `prezzo` decimal(12,5) default '0.00000'," +
//                        "  `iva` decimal(4,2) default NULL," +
//                        "  `sconto1` decimal(4,2) default NULL," +
//                        "  `sconto2` decimal(4,2) default NULL," +
//                        "  `stato` char(1) default NULL, +
//                        "  `ddt_serie` char(1) default NULL," +
//                        "  `ddt_numero` int(10) unsigned default '0'," +
//                        "  `ddt_anno` int(10) unsigned default '0'," +
//                        "  `ddt_riga` int(10) unsigned default '0'," +
//                        "  `riga_speciale` char(1) default NULL," +
//                        "  `descrizione2` varchar(255) default NULL," +
//                        "  `matricola` varchar(255) default NULL," +
//                        "  `username` varchar(50) NOT NULL," +
//                        "  PRIMARY KEY  (`serie`,`numero`,`anno`,`riga`,`username`)" +
//                        ");\n";
//                try {
//                    
//                    if (Db.executeSqlSplitByNl(sql1)) {
//                        writeLog(idAggiornamento, "update per cm");
//                    }
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
//            idAggiornamento = 83;
//            if (checkLog(idAggiornamento) == false) {
//                String sql1 = "ALTER TABLE `dati_azienda` ADD COLUMN `cfiscale` varchar(20) NULL";
//                try {
//                    
//                    if (Db.executeSql(sql1)) {
//                    }
//                    writeLog(idAggiornamento, "aggiugno campi alla dati_azienda");
//                    
//                } catch (Exception err) {
//                    err.printStackTrace();
//                }
//            }
//
            
            idAggiornamento = 84;
            if (checkLog(idAggiornamento) == false) {
                String sql1 = "ALTER TABLE `dati_azienda` ADD COLUMN `flag_dati_inseriti` char(1) NULL";
                try {
                    
                    Db.executeSql(sql1);
                    writeLog(idAggiornamento, "aggiugno campi alla dati_azienda");
                    
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

            idAggiornamento = 85;
            if (checkLog(idAggiornamento) == false) {
                String sql1 = "ALTER TABLE `dati_azienda` ADD COLUMN `sito_web` varchar(200) NULL";
                String sql2 = "ALTER TABLE `dati_azienda` ADD COLUMN `email` varchar(200) NULL";
                try {
                    
                    Db.executeSql(sql1);
                    Db.executeSql(sql2);
                    writeLog(idAggiornamento, "aggiugno campi alla dati_azienda");
                    
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

            idAggiornamento = 86;
            if (checkLog(idAggiornamento) == false) {
                String sql1 = "ALTER TABLE `clie_forn` ADD COLUMN `banca_cc_iban` varchar(100) NULL";
                try {
                    
                    Db.executeSql(sql1);
                    writeLog(idAggiornamento, "aggiugno iban a clie_forn");
                    
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

            idAggiornamento = 87;
            if (checkLog(idAggiornamento) == false) {
                String sql1 = "ALTER TABLE `dati_azienda_banche` ADD COLUMN `cc_iban` varchar(100) NULL";
                try {
                    
                    Db.executeSql(sql1);
                    writeLog(idAggiornamento, "aggiugno iban a dati_azienda_banche");
                    
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

            idAggiornamento = 88;
            if (checkLog(idAggiornamento) == false) {
                String sql1 = "ALTER TABLE `test_fatt` ADD COLUMN `banca_iban` varchar(100) NULL";
                String sql2 = "ALTER TABLE `test_ddt` ADD COLUMN `banca_iban` varchar(100) NULL";
                String sql3 = "ALTER TABLE `test_ordi` ADD COLUMN `banca_iban` varchar(100) NULL";
                try {
                    
                    Db.executeSql(sql1);
                    Db.executeSql(sql2);
                    Db.executeSql(sql3);
                    writeLog(idAggiornamento, "aggiugno iban a test_");
                    
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

            idAggiornamento = 89;
            if (checkLog(idAggiornamento) == false) {
                
    //            if (JOptionPane.showConfirmDialog(splash, "Nella nuova versione c'è un nuovo modo di stampa, lo vuoi attivare ?\nUna volta attivato puoi reimpostarlo dalle Impostazioni", "Attenzione", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    //param def
                    try {
                        java.util.prefs.Preferences preferences = java.util.prefs.Preferences.userNodeForPackage(main.class);
                        preferences.put("tipoStampa", "fattura_mod2_default.jrxml");
                        preferences.put("tipoStampaFA", "fattura_acc_mod2_default.jrxml");
                        preferences.put("tipoStampaDDT", "ddt_mod2_default.jrxml");
                        preferences.put("tipoStampaOrdine", "ordine_default.jrxml");
                        preferences.sync();
                        fileIni.setValue("pref", "tipoStampa", "fattura_mod2_default.jrxml");
                        fileIni.setValue("pref", "tipoStampaFA", "fattura_acc_mod2_default.jrxml");
                        fileIni.setValue("pref", "tipoStampaDDT", "ddt_mod2_default.jrxml");
                        fileIni.setValue("pref", "tipoStampaOrdine", "ordine_default.jrxml");
                    } catch (Exception ex1) {
                        ex1.printStackTrace();
                    }
    //            }
                
                String sql1 = "ALTER TABLE `test_fatt` ADD COLUMN `ritenuta` int NULL";
                String sql2 = "ALTER TABLE `clie_forn` ADD COLUMN `ritenuta` int NULL";
                String sql3 = "alter table test_fatt add column totale_ritenuta decimal(15,5)";
                String sql4 = "alter table test_fatt add column totale_da_pagare decimal(15,5)";
                try {
                    
                    Db.executeSql(sql1);
                    Db.executeSql(sql2);
                    Db.executeSql(sql3);
                    Db.executeSql(sql4);
                    writeLog(idAggiornamento, "ritenute");
                    
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

            idAggiornamento = 90;
            if (checkLog(idAggiornamento) == false) {
                String sql1 = "alter table test_fatt add column dataoraddt varchar(50)";
                String sql2 = "alter table test_ddt add column dataoraddt varchar(50)";
                try {
                    
                    Db.executeSql(sql1);
                    Db.executeSql(sql2);
                    writeLog(idAggiornamento, "data ora ddt");
                    
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

            idAggiornamento = 91;
            if (checkLog(idAggiornamento) == false) {
                String sql1 = "alter table articoli add peso_kg decimal(15,5)";
                try {
                    
                    Db.executeSql(sql1);
                    writeLog(idAggiornamento, "aggiungo il peso sugli articoli");
                    
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

            if (!DbUtils.existTable(Db.getConn(), "righ_fatt_ricevute")) {
                exist_ricevute = false;
                nome_ricevute = "_acquisto";
                testate_ricevute = "test_fatt_acquisto";
            }
            
            idAggiornamento = 92;
            if (checkLog(idAggiornamento) == false) {
                try {
                    
                    cambioCampoIva("righ_fatt");
                    cambioCampoIva("righ_fatt" + nome_ricevute);
                    cambioCampoIva("righ_ddt");
                    cambioCampoIva("righ_ordi");
                    writeLog(idAggiornamento, "codice iva a char di 3 invece che numerico con deicmali");
                    
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

            idAggiornamento = 93;
            if (checkLog(idAggiornamento) == false) {
                String sql1 = "ALTER TABLE righ_fatt MODIFY COLUMN ddt_serie CHAR(1) NOT NULL DEFAULT ''";
                String sql2 = "ALTER TABLE righ_fatt" + nome_ricevute + " MODIFY COLUMN ddt_serie CHAR(1) NOT NULL DEFAULT ''";
                try {
                    
                    Db.executeSql(sql1);
                    Db.executeSql(sql2);
                    writeLog(idAggiornamento, "problemi mysql 5.0.51a");
                    
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

            idAggiornamento = 94;
            if (checkLog(idAggiornamento) == false) {
                String sql1 = "ALTER TABLE test_ordi ADD COLUMN stato_ordine varchar(200) NULL DEFAULT ''";
                try {
                    
                    Db.executeSql(sql1);
                    writeLog(idAggiornamento, "aggiungo stato_ordine");
                    
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

            idAggiornamento = 95;
            if (checkLog(idAggiornamento) == false) {
                String sql1 = "ALTER TABLE clie_forn ADD COLUMN campo1 varchar(200) NULL DEFAULT ''";
                try {
                    
                    Db.executeSql(sql1);
                    writeLog(idAggiornamento, "aggiungo campo libero 1");
                    
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

            idAggiornamento = 96;
            if (checkLog(idAggiornamento) == false) {
                String sql1 = "ALTER TABLE distinte_riba AUTO_INCREMENT = 0";
                try {
                    
                    Db.executeSql(sql1);
                    writeLog(idAggiornamento, "resetto il numero dist riba per bug su 175");
                    
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

            idAggiornamento = 97;
            if (checkLog(idAggiornamento) == false) {
                String sql1 = "insert into tipi_fatture values (6, 'FP', 'FATTURA PRO-FORMA')";
                try {
                    
                    Db.executeSql(sql1);
                    writeLog(idAggiornamento, "aggiungo il tipo fattura pro forma");
                    
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

            idAggiornamento = 98;
            if (checkLog(idAggiornamento) == false) {
                String sql1 = "alter table righ_fatt modify column descrizione text";
                String sql2 = "alter table righ_fatt" + nome_ricevute + " modify column descrizione text";
                String sql3 = "alter table righ_ddt modify column descrizione text";
                String sql4 = "alter table righ_ordi modify column descrizione text";
                String sql5 = "alter table articoli modify column descrizione text";
                try {
                    
                    Db.executeSql(sql1);
                    Db.executeSql(sql2);
                    Db.executeSql(sql3);
                    Db.executeSql(sql4);
                    Db.executeSql(sql5);
                    writeLog(idAggiornamento, "modifico la descrizione in text");
                    
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

            idAggiornamento = 99;
            if (checkLog(idAggiornamento) == false) {
                String sql1 = "alter table articoli add peso_kg_collo decimal(15,5)";
                try {
                    
                    Db.executeSql(sql1);
                    writeLog(idAggiornamento, "aggiungo il peso sugli articoli per collo");

                    Preferences preferences = java.util.prefs.Preferences.userNodeForPackage(main.class);
                    preferences.putBoolean("soloItaliano", true);

                    fileIni.setValue("pref", "soloItaliano", "true");

                    
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

            idAggiornamento = 100;
            if (checkLog(idAggiornamento) == false) {
                String sql1 = "alter table codici_iva modify column percentuale decimal(5,2)";
                try {
                    
                    Db.executeSql(sql1);
                    writeLog(idAggiornamento, "aggiorno perc iva");

                    
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
            idAggiornamento = 101;
            if (checkLog(idAggiornamento) == false) {
                String sql1 = "update codici_iva set percentuale = 20 where codice = '20'";
                try {
                    
                    Db.executeSql(sql1);
                    writeLog(idAggiornamento, "aggiorno perc iva");

                    
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

            idAggiornamento = 102;
            if (checkLog(idAggiornamento) == false) {
                String sql1 = "alter table " + testate_ricevute + " add column giorno_pagamento tinyint(4) default NULL";
                String sql2 = "alter table " + testate_ricevute + " add column pagamento varchar(35) default NULL";
                try {
                    Db.executeSql(sql1);
                    Db.executeSql(sql2);
                    writeLog(idAggiornamento, "aggiorno fatture ricevute teste x pagamento");
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

            idAggiornamento = 103;
            if (checkLog(idAggiornamento) == false) {
                String sql1 = "alter table test_fatt modify column stato char(1) NULL";
                String sql2 = "alter table test_ddt modify column stato char(1) NULL";
                String sql3 = "alter table test_ordi modify column stato char(1) NULL";
                try {
                    
                    Db.executeSql(sql1);
                    Db.executeSql(sql2);
                    Db.executeSql(sql3);
                    writeLog(idAggiornamento, "aggiorno campo stato nullable");

                    
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

            idAggiornamento = 104;
            if (checkLog(idAggiornamento) == false) {
                String sql1 = "alter table codici_iva modify column descrizione varchar(250) NULL";
                try {
                    
                    Db.executeSql(sql1);
                    writeLog(idAggiornamento, "aggiorno campo iva descrizione piu lunga");

                    
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

            idAggiornamento = 105;
            if (checkLog(idAggiornamento) == false) {
                String sql1 = "ALTER TABLE `dati_azienda_banche` ADD COLUMN `id` INTEGER NOT NULL AUTO_INCREMENT AFTER `cc_iban`, DROP PRIMARY KEY, ADD PRIMARY KEY  (`id`);";
                try {
                    
                    Db.executeSql(sql1);
                    writeLog(idAggiornamento, "aggiorno campo id ai conti correnti aziendali");

                    
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

            idAggiornamento = 106;
            if (checkLog(idAggiornamento) == false) {
                String sql1 = "ALTER TABLE pagamenti ADD COLUMN id_conto INTEGER NULL";
                try {
                    
                    Db.executeSql(sql1);
                    writeLog(idAggiornamento, "aggiorno campo conto ai pagamenti");
                    
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

            agg(107, "ALTER TABLE tipi_listino ADD COLUMN tipo varchar(50) NULL", "agg campo tipo listino");
            agg(108, "ALTER TABLE tipi_listino ADD COLUMN ricarico_flag char(1) NULL", "agg listino");
            agg(109, "ALTER TABLE tipi_listino ADD COLUMN ricarico_perc decimal(6,2) NULL", "agg listino");
            agg(110, "ALTER TABLE tipi_listino ADD COLUMN ricarico_listino varchar(10) NULL", "agg listino");

            agg(111, "ALTER TABLE articoli ADD COLUMN servizio CHAR(1) NULL", "agg articoli");

            agg(112, "ALTER TABLE clie_forn MODIFY COLUMN persona_riferimento VARCHAR(200) NULL", "agg persona riferimento");

            agg(113, "ALTER TABLE righ_ddt MODIFY COLUMN sconto1 DECIMAL(5,2) NULL", "agg sconti 100");
            agg(114, "ALTER TABLE righ_ddt MODIFY COLUMN sconto2 DECIMAL(5,2) NULL", "agg sconti 100");
            agg(115, "ALTER TABLE righ_fatt MODIFY COLUMN sconto1 DECIMAL(5,2) NULL", "agg sconti 100");
            agg(116, "ALTER TABLE righ_fatt MODIFY COLUMN sconto2 DECIMAL(5,2) NULL", "agg sconti 100");
            agg(117, "ALTER TABLE righ_ordi MODIFY COLUMN sconto1 DECIMAL(5,2) NULL", "agg sconti 100");
            agg(118, "ALTER TABLE righ_ordi MODIFY COLUMN sconto2 DECIMAL(5,2) NULL", "agg sconti 100");
            agg(119, "ALTER TABLE righ_fatt" + nome_ricevute + " MODIFY COLUMN sconto1 DECIMAL(5,2) NULL", "agg sconti 100");
            agg(120, "ALTER TABLE righ_fatt" + nome_ricevute + " MODIFY COLUMN sconto2 DECIMAL(5,2) NULL", "agg sconti 100");
            agg(121, "ALTER TABLE agenti MODIFY COLUMN percentuale DECIMAL(5,2) default 0 NULL ", "agg perc agenti");
            agg(122, "ALTER TABLE test_fatt MODIFY COLUMN agente_percentuale DECIMAL(5,2) default 0 NULL", "agg perc agenti");
            agg(123, "ALTER TABLE test_ordi MODIFY COLUMN agente_percentuale DECIMAL(5,2) default 0 NULL", "agg perc agenti");
            agg(124, "ALTER TABLE fatture_ricevute_iva MODIFY COLUMN percentuale_iva DECIMAL(5,2) default 0 NULL", "agg iva");
            agg(125, "ALTER TABLE prima_nota_iva MODIFY COLUMN percentuale_iva DECIMAL(5,2) default 0 NULL", "agg iva");

            agg(126, "insert into codici_iva values ('41',(0.000),'Non imponibile art.41 D.L 513','Non Imp. art.41')", "agg iva");

            agg(127, "ALTER TABLE clie_forn_dest add paese varchar(2) NULL", "aggiungo paese su dest diversa");
            agg(128, "ALTER TABLE test_fatt add dest_paese varchar(2) NULL", "aggiungo paese su dest diversa");
            agg(129, "ALTER TABLE test_ddt add dest_paese varchar(2) NULL", "aggiungo paese su dest diversa");
            agg(130, "ALTER TABLE test_ordi add dest_paese varchar(2) NULL", "aggiungo paese su dest diversa");

            agg(131, "ALTER TABLE clie_forn add opzione_raggruppa_ddt char(1) NULL default 'N'", "aggiungo opzione riba raggruppate");
            agg(132, "ALTER TABLE clie_forn add opzione_prezzi_ddt char(1) NULL default 'N'", "aggiungo opzione prezzi ddt");
            agg(133, "ALTER TABLE test_ddt add opzione_prezzi_ddt char(1) NULL default 'N'", "aggiungo opzione prezzi ddt testddt");

            agg(134, "create TABLE scadenze_parziali (id bigint not null auto_increment, id_scadenza bigint not null, data date default '0000-00-00', importo decimal (15,5) default 0, PRIMARY KEY (id)) ENGINE=MyISAM", "scadenze con paga. parziale");
            agg(135, "insert into scadenze_parziali (id_scadenza, data, importo) select id, data_scadenza, importo from scadenze where pagata = 'S'", "scadenze con paga. parziale 2");

            agg(136, "alter table articoli add tipo char(1)", "aggiunto campo tipo su articoli");
            agg(137, "ALTER TABLE `test_fatt` ADD COLUMN `totaleRivalsa` decimal(5,2) DEFAULT 0","Aggiungo Rivalsa");
            agg(138, "ALTER TABLE `scadenze` ADD COLUMN `flag_file` VARCHAR(1) DEFAULT 'N'","Aggiungo Flag stampa per riba");

            agg(139, "ALTER TABLE `test_ddt` ADD COLUMN `banca_abi` varchar(5) default NULL","Aggiungo campi mancanti in ddt per conversione prev ddt");
            agg(140, "ALTER TABLE `test_ddt` ADD COLUMN `banca_cab` varchar(5) default NULL","Aggiungo campi mancanti in ddt per conversione prev ddt");
            agg(141, "ALTER TABLE `test_ddt` ADD COLUMN `banca_cc` varchar(35) default NULL","Aggiungo campi mancanti in ddt per conversione prev ddt");
            agg(142, "ALTER TABLE `test_ddt` ADD COLUMN `agente_codice` int(11) default NULL","Aggiungo campi mancanti in ddt per conversione prev ddt");
            agg(143, "ALTER TABLE `test_ddt` ADD COLUMN `agente_percentuale` decimal(5,2) default '0.00'","Aggiungo campi mancanti in ddt per conversione prev ddt");
            agg(144, "ALTER TABLE `test_ddt` ADD COLUMN `note_pagamento` text","Aggiungo campi mancanti in ddt per conversione prev ddt");
            agg(145, "ALTER TABLE `clie_forn` ADD COLUMN `obsoleto` VARCHAR(1) default 0","Aggiungo campo per cliente/fornitore obsoleto");
            agg(146, "ALTER TABLE `articoli` ADD COLUMN `flag_kit` VARCHAR(1) default 'N'","Aggiungo campo per articolo composto");
            agg(147, "CREATE TABLE pacchetti_articoli (pacchetto varchar(20) not null, articolo varchar(20) not null, quantita int not null) ENGINE=MyISAM", "Creo tabella relazione pacchetti articoli");
            agg(148, "ALTER TABLE `articoli` ADD COLUMN `flag_kit` VARCHAR(1) default 'N'","Aggiungo campo per articolo composto2..");
            agg(149, "ALTER TABLE `clie_forn` ADD COLUMN `logo` VARCHAR(100)","Aggiungo campo logo in anagrafica clienti");
            agg(150, "ALTER TABLE `test_fatt` ADD COLUMN `fornitore` int(10) default NULL","Aggiungo campo fornitore in testata fatture");
            agg(151, "ALTER TABLE `test_ordi` ADD COLUMN `fornitore` int(10) default NULL","Aggiungo campo fornitore in testata ordini preventivi");

            agg(152, "ALTER TABLE `agenti` ADD COLUMN `email` varchar(200) default NULL", "Aggiungo campo email in agenti");

            agg(153, "alter table clie_forn modify indirizzo varchar(200)", "allargo indirizzo...");
            agg(154, "alter table clie_forn modify localita varchar(200)", "allargo indirizzo...");

            //salto...
            
            agg(161, "ALTER TABLE `test_fatt` ADD COLUMN `id` INTEGER NOT NULL AUTO_INCREMENT FIRST, DROP PRIMARY KEY, ADD PRIMARY KEY (`id`), ADD INDEX `INDEX` (`serie`, `numero`, `anno`);","Aggiungo campo id a test_fatt");
            agg(162, "ALTER TABLE `righ_fatt` ADD COLUMN (`id` INTEGER NOT NULL AUTO_INCREMENT , `id_padre` INTEGER NOT NULL), DROP PRIMARY KEY, ADD PRIMARY KEY (`id`), ADD INDEX `INDEX` (`serie`, `numero`, `anno`)","Aggiungo campo id a righ_fatt per collegamento a test_fatt");
            agg(163, "ALTER TABLE `test_ddt` ADD COLUMN `id` INTEGER NOT NULL AUTO_INCREMENT FIRST, DROP PRIMARY KEY, ADD PRIMARY KEY (`id`), ADD INDEX `INDEX` (`serie`, `numero`, `anno`);","Aggiungo campo id a test_ddt");
            agg(164, "ALTER TABLE `righ_ddt` ADD COLUMN (`id` INTEGER NOT NULL AUTO_INCREMENT , `id_padre` INTEGER NOT NULL), DROP PRIMARY KEY, ADD PRIMARY KEY (`id`), ADD INDEX `INDEX` (`serie`, `numero`, `anno`)","Aggiungo campo id a righ_ddt per collegamento a test_ddt");
            agg(165, "ALTER TABLE `test_ordi` ADD COLUMN `id` INTEGER NOT NULL AUTO_INCREMENT FIRST, DROP PRIMARY KEY, ADD PRIMARY KEY (`id`), ADD INDEX `INDEX` (`serie`, `numero`, `anno`);","Aggiungo campo id a test_ordi");
            agg(166, "ALTER TABLE `righ_ordi` ADD COLUMN (`id` INTEGER NOT NULL AUTO_INCREMENT , `id_padre` INTEGER NOT NULL), DROP PRIMARY KEY, ADD PRIMARY KEY (`id`), ADD INDEX `INDEX` (`serie`, `numero`, `anno`)","Aggiungo campo id a righ_ordi per collegamento a test_ordi");
            agg(167, "ALTER TABLE `" + testate_ricevute + "` ADD COLUMN `id` INTEGER NOT NULL AUTO_INCREMENT FIRST, DROP PRIMARY KEY, ADD PRIMARY KEY (`id`), ADD INDEX `INDEX` (`serie`, `numero`, `anno`);","Aggiungo campo id a fatture_ricevute_teste");
            agg(168, "ALTER TABLE `righ_fatt" + nome_ricevute + "` ADD COLUMN (`id` INTEGER NOT NULL AUTO_INCREMENT , `id_padre` INTEGER NOT NULL), DROP PRIMARY KEY, ADD PRIMARY KEY (`id`), ADD INDEX `INDEX` (`serie`, `numero`, `anno`)","Aggiungo campo id a righ_fatt_ricevute per collegamento a fatture_ricevute_teste");
            agg(169, "UPDATE righ_fatt r left join test_fatt t on r.serie = t.serie and r.anno = t.anno and r.numero = t.numero set r.id_padre = t.id","Aggiorno campo id per righ_fatt con l'equivalente in test_fatt");
            agg(170, "UPDATE righ_ddt r left join test_ddt t on r.serie = t.serie and r.anno = t.anno and r.numero = t.numero set r.id_padre = t.id","Aggiorno campo id per righ_ddt con l'equivalente in test_fatt");
            agg(171, "UPDATE righ_ordi r left join test_ordi t on r.serie = t.serie and r.anno = t.anno and r.numero = t.numero set r.id_padre = t.id","Aggiorno campo id per righ_ordi con l'equivalente in test_fatt");
            agg(172, "UPDATE righ_fatt" + nome_ricevute + " r left join " + testate_ricevute + " t on r.serie = t.serie and r.anno = t.anno and r.numero = t.numero set r.id_padre = t.id","Aggiorno campo id per righ_fatt_ricevute con l'equivalente in fatture_ricevute_teste");
            agg(173, "INSERT INTO tipi_fatture (tipo, descrizione_breve, descrizione) values (7, 'SC', 'SCONTRINO');","Aggiorno gli scontrini ai tipi di fattura");
            agg(174, "ALTER TABLE `righ_fatt_matricole` ADD COLUMN (`id` INTEGER NOT NULL AUTO_INCREMENT , `id_padre` INTEGER NOT NULL), DROP PRIMARY KEY, ADD PRIMARY KEY (`id`), ADD INDEX `INDEX` (`serie`, `numero`, `anno`, `riga`, `matricola`)","Aggiungo campo id a righ_fatt_matricole per la generazione dei movimenti");
            agg(175, "ALTER TABLE `movimenti_magazzino` ADD COLUMN `da_id` INTEGER","Aggiungo campo da_id a movimenti magazzino per la generazione dei movimenti con gli scontrini");
            agg(176, "ALTER TABLE `test_fatt` ADD COLUMN `scontrino_importo_pagato` decimal(12,2);","Aggiungo campo importo pagato a test_fatt");

            agg(177, "alter table articoli add gestione_matricola char(1) not null default 'N'", "aggiungo campo gestione matricola in articoli");
            
            agg(178, "ALTER TABLE movimenti_magazzino ADD INDEX ind_articolo (articolo)", "indicizzazione per ricerca articoli");

            agg(179, "ALTER TABLE righ_fatt ADD COLUMN flag_ritenuta char(1) DEFAULT 'S'", "righe ritenuta");
            agg(180, "ALTER TABLE righ_fatt" + nome_ricevute + " ADD COLUMN flag_ritenuta char(1) DEFAULT 'S'", "righe ritenuta");
            agg(181, "ALTER TABLE righ_ddt ADD COLUMN flag_ritenuta char(1) DEFAULT 'S'", "righe ritenuta");
            agg(182, "ALTER TABLE righ_ordi ADD COLUMN flag_ritenuta char(1) DEFAULT 'S'", "righe ritenuta");

            agg(183, "ALTER TABLE articoli ADD COLUMN codice_fornitore varchar(100) DEFAULT ''", "articoli codice fornitore");
            agg(184, "ALTER TABLE articoli ADD COLUMN codice_a_barre varchar(100) DEFAULT ''", "articoli codice a barre");
            agg(185, "ALTER TABLE articoli ADD INDEX index_codice_fornitore(codice_fornitore)", "articoli indice codice fornitore");
            agg(186, "ALTER TABLE articoli ADD INDEX index_codice_a_barre(codice_a_barre)", "articoli indice codice a barre");

            agg(187, "ALTER TABLE articoli ADD COLUMN immagine1 varchar(250) default ''", "articoli immagine");

            agg(188, "ALTER TABLE righ_ddt ADD COLUMN bolla_cliente varchar(50) default ''", "aggiunta bolla cliente per ddt");
            agg(189, "ALTER TABLE righ_ddt ADD COLUMN misura varchar(20) default ''", "aggiunta misura pezzo sy ddt");
            agg(190, "ALTER TABLE righ_ddt ADD COLUMN disegno varchar(50) default ''", "aggiunta bolla cliente per ddt");
            agg(191, "ALTER TABLE righ_ddt ADD COLUMN var varchar(15) default ''", "aggiunta bolla cliente per ddt");

            agg(192, "ALTER TABLE righ_fatt ADD COLUMN bolla_cliente varchar(50) default ''", "aggiunta bolla cliente per fattura");
            agg(193, "ALTER TABLE righ_fatt ADD COLUMN misura varchar(20) default ''", "aggiunta misura pezzo sy fattura");
            agg(194, "ALTER TABLE righ_fatt ADD COLUMN disegno varchar(50) default ''", "aggiunta bolla cliente per fattura");
            agg(195, "ALTER TABLE righ_fatt ADD COLUMN var varchar(15) default ''", "aggiunta bolla cliente per fattura");

            agg(196, "ALTER TABLE clie_forn ADD COLUMN nota_cliente text", "aggiunto campo nota cliente");
            agg(197, "CREATE TABLE clie_forn_rapporti (id int(11) not null AUTO_INCREMENT PRIMARY KEY, cliente int(10) not null, data date, data_avviso date, testo text) ENGINE=MyISAM", "Creo tabella relazione clienti rapporti");
            agg(198, "ALTER TABLE clie_forn_rapporti ADD COLUMN segnalato varchar(1) default 'N'", "aggiunto campo notifica rapporti");

            agg(199, "ALTER TABLE articoli ADD COLUMN fornitore varchar(10)", "aggiunto campo fornitore su articoli");

            agg(200, "CREATE TABLE plugin_ricerca_add (id int(11) not null AUTO_INCREMENT PRIMARY KEY, data_ora datetime) ENGINE=MyISAM", "plugin ricerca");
            agg(201, "UPDATE righ_fatt" + nome_ricevute + " r left join " + testate_ricevute + " t on r.serie = t.serie and r.anno = t.anno and r.numero = t.numero set r.id_padre = t.id","Aggiorno campo id per righ_fatt" + nome_ricevute + " con l'equivalente in fatture_ricevute_teste");

            agg(202, "ALTER TABLE test_fatt MODIFY COLUMN cliente INT(10) UNSIGNED DEFAULT NULL", "cliente default null");
            agg(203, "ALTER TABLE test_ddt MODIFY COLUMN cliente INT(10) UNSIGNED DEFAULT NULL", "cliente default null");
            agg(204, "ALTER TABLE test_ordi MODIFY COLUMN cliente INT(10) UNSIGNED DEFAULT NULL", "cliente default null");

            agg(205, "create TABLE attivazioni (id int unsigned NOT NULL auto_increment, data TIMESTAMP DEFAULT CURRENT_TIMESTAMP, msg varchar(255) NULL, PRIMARY KEY (id)) ENGINE=MyISAM", "tabella attivazioni");

            agg(206, "alter table articoli add gestione_lotti char(1) not null default 'N'", "aggiungo campo gestione lotti in articoli");
            agg(207, "ALTER TABLE movimenti_magazzino ADD COLUMN lotto VARCHAR(200)", "Aggiungo campo lotto movimenti magazzino");
            agg(208, "CREATE TABLE righ_fatt_lotti (id_padre int, id int not null auto_increment, lotto varchar(200), codice_articolo VARCHAR(20), qta decimal(8,2), PRIMARY KEY(id)) ENGINE=MyISAM", "Aggiungo campo lotto movimenti magazzino fatt");
            agg(209, "CREATE TABLE righ_fatt" + nome_ricevute + "_lotti (id_padre int, id int not null auto_increment, lotto varchar(200), codice_articolo VARCHAR(20), qta decimal(8,2), PRIMARY KEY(id)) ENGINE=MyISAM", "Aggiungo campo lotto movimenti magazzino fatt acq");
            agg(210, "CREATE TABLE righ_ddt_lotti (id_padre int, id int not null auto_increment, lotto varchar(200), codice_articolo VARCHAR(20), qta decimal(8,2), PRIMARY KEY(id)) ENGINE=MyISAM", "Aggiungo campo lotto movimenti magazzino ddt");
            agg(211, "ALTER TABLE movimenti_magazzino ADD INDEX ind_articolo (articolo)", "indicizzazione per ricerca articoli 2");

            agg(212, "UPDATE righ_fatt r left join test_fatt t on r.serie = t.serie and r.anno = t.anno and r.numero = t.numero and t.tipo_fattura != 7 set r.id_padre = t.id", "Aggiorno campo id per righ_fatt con l'equivalente in test_fatt");
            agg(213, "ALTER TABLE test_fatt ADD COLUMN marca_da_bollo DECIMAL(8,2)", "Aggiunto importo marche da bollo su testate fatture");

            agg(214, "ALTER TABLE clie_forn ADD COLUMN note_automatiche char(1) DEFAULT 'N'", "Aggiunto campo flag note automatiche su clienti");
            agg(215, "ALTER TABLE clie_forn ADD COLUMN iva_standard char(3)", "Aggiunto campo iva standre su clienti");
            agg(216, "ALTER TABLE " + testate_ricevute + " MODIFY COLUMN numero_doc bigint(15)", "Aumentata capienza campo numero fattura esterna");
            
            agg(217, "create table tipi_porto (id int auto_increment not null primary key, porto varchar(100)) ENGINE=MyISAM", "tabella tipi_porto");
            agg(218, "insert into tipi_porto (porto) values ('PORTO ASSEGNATO')", "tabella tipi_porto");
            agg(219, "insert into tipi_porto (porto) values ('PORTO FRANCO')", "tabella tipi_porto");

            agg(220, "create table tipi_causali_trasporto (id int auto_increment not null primary key, nome varchar(100)) ENGINE=MyISAM", "tabella tipi_causali_trasporto");
            agg(221, "insert into tipi_causali_trasporto (nome) values ('VENDITA')", "tabella tipi_causali_trasporto");
            agg(222, "insert into tipi_causali_trasporto (nome) values ('TENTATA VENDITA')", "tabella tipi_causali_trasporto");
            agg(223, "insert into tipi_causali_trasporto (nome) values ('LAVORAZIONE')", "tabella tipi_causali_trasporto");
            agg(224, "insert into tipi_causali_trasporto (nome) values ('C/LAVORAZIONE')", "tabella tipi_causali_trasporto");
            agg(225, "insert into tipi_causali_trasporto (nome) values ('C/VISIONE')", "tabella tipi_causali_trasporto");
            agg(226, "insert into tipi_causali_trasporto (nome) values ('C/RIPARAZIONE')", "tabella tipi_causali_trasporto");
            agg(227, "insert into tipi_causali_trasporto (nome) values ('RESO C/LAVORAZIONE')", "tabella tipi_causali_trasporto");
            agg(228, "insert into tipi_causali_trasporto (nome) values ('RESO SCARTO INUTILIZZABILE')", "tabella tipi_causali_trasporto");
            agg(229, "insert into tipi_causali_trasporto (nome) values ('RESO C/VISIONE')", "tabella tipi_causali_trasporto");
            agg(230, "insert into tipi_causali_trasporto (nome) values ('RESO C/RIPARAZIONE')", "tabella tipi_causali_trasporto");
            agg(231, "insert into tipi_causali_trasporto (nome) values ('RIPARAZIONE')", "tabella tipi_causali_trasporto");
            agg(232, "insert into tipi_causali_trasporto (nome) values ('CONSEGNA C/TERZI')", "tabella tipi_causali_trasporto");
            agg(233, "insert into tipi_causali_trasporto (nome) values ('OMAGGIO')", "tabella tipi_causali_trasporto");
            agg(234, "insert into tipi_causali_trasporto (nome) values ('RESO')", "tabella tipi_causali_trasporto");
            agg(235, "insert into tipi_causali_trasporto (nome) values ('CESSIONE')", "tabella tipi_causali_trasporto");

            agg(236, "alter TABLE righ_fatt_lotti add matricola varchar(255)", "Aggiungo matricola nei lotti");
            agg(237, "alter TABLE righ_fatt" + nome_ricevute + "_lotti add matricola varchar(255)", "Aggiungo matricola nei lotti");
            agg(238, "alter TABLE righ_ddt_lotti add matricola varchar(255)", "Aggiungo matricola nei lotti");

            agg(239, "alter TABLE storico add dati LONGTEXT", "Aggiungo colonna dati allo storico");

            agg(242, "ALTER TABLE agenti ADD COLUMN (percentuale_soglia_1 decimal(5,2) DEFAULT '0.00', percentuale_soglia_2 decimal(5,2) DEFAULT '0.00', percentuale_soglia_3 decimal(5,2) DEFAULT '0.00', percentuale_soglia_4 decimal(5,2) DEFAULT '0.00', percentuale_soglia_5 decimal(5,2) DEFAULT '0.00')","Aggiunte percentuali personalizzate in base alle soglie sugli agenti");

            agg(243, "ALTER TABLE agenti MODIFY COLUMN cap VARCHAR(5) DEFAULT ''", "modificato campo cap su agenti");

            agg(250, "ALTER TABLE `pagamenti`  ADD COLUMN `210` CHAR(1) NOT NULL DEFAULT '' AFTER `180`", "Aggiungo colonna 210gg");

            agg(251, "alter table " + testate_ricevute + " add column totale_ritenuta decimal(15,5)", "Aggiungo totale_ritenuta");
            agg(252, "alter table " + testate_ricevute + " add column totale_da_pagare decimal(15,5)", "Aggiungo totale_ritenuta");
            agg(253, "ALTER TABLE " + testate_ricevute + " ADD COLUMN totaleRivalsa decimal(5,2) DEFAULT 0","Aggiungo Rivalsa");

            idAggiornamento = 254;
            if (DbUtils.getCreateTable("articoli", Db.getConn()).indexOf("`iva` decimal") >= 0 && checkLog(idAggiornamento) == false) {
                try {
                    cambioCampoIva("articoli");
                    writeLog(idAggiornamento, "codice iva a char di 3 invece che numerico con deicmali");
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

            agg(255, "ALTER TABLE articoli ADD COLUMN non_applicare_percentuale char(1)","aggiungo paramentro per non applicare prcentuale di ricarico");

            //spostate per problemi modifica doppia
            if (!DbUtils.existTable(Db.getConn(), "soglie_provvigioni")) {
                agg(260, "CREATE TABLE soglie_provvigioni (soglia int not null auto_increment, min_soglia decimal (5,2), max_soglia decimal (5,2), PRIMARY KEY(soglia)) ENGINE=MyISAM", "Aggiungo tabella soglie per provvigioni agenti nesocell");
                agg(261, "ALTER TABLE soglie_provvigioni ADD COLUMN percentuale decimal (5,2)", "Aggiunto a tabella soglie per provvigioni agenti percentuale della soglia");
                agg(262, "INSERT INTO soglie_provvigioni (soglia) VALUES (1)", "Preparo riga 1 per soglie");
                agg(263, "INSERT INTO soglie_provvigioni (soglia) VALUES (2)", "Preparo riga 2 per soglie");
                agg(264, "INSERT INTO soglie_provvigioni (soglia) VALUES (3)", "Preparo riga 3 per soglie");
                agg(265, "INSERT INTO soglie_provvigioni (soglia) VALUES (4)", "Preparo riga 4 per soglie");
                agg(266, "INSERT INTO soglie_provvigioni (soglia) VALUES (5)", "Preparo riga 5 per soglie");
                agg(270, "ALTER TABLE soglie_provvigioni DROP min_soglia, CHANGE max_soglia sconto_soglia  decimal (5,2)", "Modificata tabella soglie per provvigioni");
            }

            agg(271, "ALTER TABLE righ_ddt add provvigione decimal(5,2)", "provvigioni per riga");
            agg(272, "ALTER TABLE righ_ordi add provvigione decimal(5,2)", "provvigioni per riga");
            agg(273, "ALTER TABLE righ_fatt add provvigione decimal(5,2)", "provvigioni per riga");

            agg(274, "update test_fatt t join righ_fatt r on t.id = r.id_padre set r.provvigione = t.agente_percentuale", "provvigioni per riga");
            agg(275, "update test_ddt t join righ_ddt r on t.id = r.id_padre set r.provvigione = t.agente_percentuale", "provvigioni per riga");
            agg(276, "update test_ordi t join righ_ordi r on t.id = r.id_padre set r.provvigione = t.agente_percentuale", "provvigioni per riga");

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                statLog.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (splash != null) {
            splash.jProgressBar1.setIndeterminate(false);        
        }

        System.out.println(mb.getDiff("fine agg db"));
    }

}
