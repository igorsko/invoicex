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
/*
 * Magazzino.java
 *
 * Created on 24 maggio 2007, 14.29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package it.tnx.invoicex;

import it.tnx.Db;
import it.tnx.commons.CastUtils;
import it.tnx.commons.DbUtils;
import it.tnx.commons.FormatUtils;
import it.tnx.invoicex.data.Giacenza;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author mceccarelli
 */
public class Magazzino {

    /** Creates a new instance of Magazzino */
    public Magazzino() {
    }

    static public ArrayList getGiacenza(Object listino) {
        return getGiacenza(true, listino);
    }

    static public ArrayList getGiacenzaPerLotti(Object listino) {
        return getGiacenza(false, null, listino, null, true, false);
    }

    static public ArrayList getGiacenzaPerLotti(String articolo, Object listino, Date data, boolean comprendereQtaZero) {
        return getGiacenza(false, articolo, listino, data, true, comprendereQtaZero);
    }

    static public ArrayList getGiacenza(boolean perMatricola, Object listino) {
        return getGiacenza(perMatricola, null, listino);
    }

    static public ArrayList getGiacenza(boolean perMatricola, String articolo, Object listino) {
        return getGiacenza(perMatricola, articolo, listino, null);
    }

    static public ArrayList getGiacenza(boolean perMatricola, String articolo, Object listino, Date data) {
        return getGiacenza(perMatricola, articolo, listino, data, false, false);
    }

    static public ArrayList getGiacenza(boolean perMatricola, String articolo, Object listino, Date data, boolean perLotti, boolean comprendereQtaZero) {
        return getGiacenza(perMatricola, articolo, listino, data, perLotti, comprendereQtaZero, false);
    }

    static public ArrayList getGiacenza(boolean perMatricola, String articolo, Object listino, Date data, boolean perLotti, boolean comprendereQtaZero, boolean forza_no_lotti) {
        String sql = null;
        boolean addPrezzo = true;
        ArrayList ret = new ArrayList();

        boolean flag_kit = false;
        sql = "select flag_kit from articoli where codice = " + Db.pc(articolo, Types.VARCHAR);
        try {
            if (StringUtils.equalsIgnoreCase("S", (String) DbUtils.getObject(Db.getConn(), sql))) {
                flag_kit = true;
            }
        } catch (Exception ex) {
        }

        boolean flag_lotti = false;
        if (perLotti) {
            flag_lotti = true;
        } else {
            if (!forza_no_lotti) {
                sql = "select gestione_lotti from articoli where codice = " + Db.pc(articolo, Types.VARCHAR);
                try {
                    if (StringUtils.equalsIgnoreCase("S", (String) DbUtils.getObject(Db.getConn(), sql))) {
                        flag_lotti = true;
                    }
                } catch (Exception ex) {
                }
            }
        }

        if (flag_kit) {
            //esplodo il pacchetto..
            sql = "select * from pacchetti_articoli where pacchetto = " + Db.pc(articolo, Types.VARCHAR) + " order by articolo";
            ResultSet rp;
            try {
                rp = DbUtils.tryOpenResultSet(Db.getConn(), sql);
                Double min_qta = null;
                while (rp.next()) {
                    ArrayList ret1 = getGiacenza(perMatricola, rp.getString("articolo"), listino, data);
                    double qta_richiesta = rp.getDouble("quantita");
                    double giacenza = ((Giacenza) ret1.get(0)).getGiacenza() / qta_richiesta;
                    if (min_qta == null || min_qta > giacenza) {
                        min_qta = giacenza;
                    }
                }
                System.out.println("qta min:" + min_qta);
                rp.getStatement().close();
                rp.close();

                Giacenza g = new Giacenza();
                g.setCodice_articolo(articolo);
                g.setGiacenza(min_qta);
                ret.add(g);
            } catch (Exception ex) {
                Logger.getLogger(Magazzino.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            if (perMatricola || flag_lotti) {
                String campo_group = "ltrim(rtrim(ifnull(matricola,''))) as matricola";
                String campo_group_per_by = "ltrim(rtrim(ifnull(matricola,'')))";
                if (flag_lotti) {
                    campo_group = "ltrim(rtrim(ifnull(lotto,''))) as lotto, ltrim(rtrim(ifnull(matricola,''))) as matricola";
                    campo_group_per_by = "ltrim(rtrim(ifnull(lotto,''))), ltrim(rtrim(ifnull(matricola,'')))";
                }
                sql = "SELECT "
                        + " `movimenti_magazzino`.`articolo`,"
                        + " " + campo_group + ","
                        + " `articoli`.`descrizione`,"
                        + " Sum(quantita * segno) as giac";
                if (listino != null) {
                    addPrezzo = true;
                    if (listino instanceof Integer) {
                        Integer control = (Integer) listino;
                        if (control == 1) {
                            sql += ", (select prezzo from righ_fatt where righ_fatt.codice_articolo = movimenti_magazzino.articolo order by anno desc, numero desc limit 1) as prezzo";
                        } else if (control == 2) {
                            sql += ", (select prezzo from righ_fatt_acquisto where righ_fatt_acquisto.codice_articolo = movimenti_magazzino.articolo order by anno desc, numero desc limit 1) as prezzo";
                        } else {
                            addPrezzo = false;
                        }
                    } else {
                        sql += ", IF(tl.ricarico_flag = 'S', (ap2.prezzo + (ap2.prezzo / 100 * tl.ricarico_perc)), ap.prezzo) as prezzo";
                    }
                }
                sql += " FROM"
                        + " `movimenti_magazzino`"
                        + " Left Join `articoli` ON `movimenti_magazzino`.`articolo` = `articoli`.`codice`"
                        + " Left Join `tipi_causali_magazzino` ON `movimenti_magazzino`.`causale` = `tipi_causali_magazzino`.`codice`";
                if (listino instanceof String) {
                    sql += "left Join articoli_prezzi ap on movimenti_magazzino.articolo = ap.articolo"
                            + " left join tipi_listino tl on ap.listino = tl.codice"
                            + " left join articoli_prezzi ap2 on movimenti_magazzino.articolo = ap2.articolo and ap2.listino = tl.ricarico_listino";
                }
                sql += " where 1 = 1";
                if (articolo != null) {
                    sql += " and articolo = " + Db.pc(articolo, Types.VARCHAR);
                }
                if (listino instanceof String) {
                    String val = (String) listino;
                    sql += " and ap.listino = '" + val + "'";
                }
                if (data != null) {
                    sql += " and data <= '" + FormatUtils.formatMysqlDate(data) + "'";
                }
                sql += " GROUP BY"
                        + " `movimenti_magazzino`.`articolo`, "
                        + campo_group_per_by;
                System.out.println("sql: " + sql);
            } else {
                sql = "SELECT "
                        + " `movimenti_magazzino`.`articolo`,"
                        + " `articoli`.`descrizione`,"
                        + " Sum(quantita * segno) as giac";
                if (listino != null) {
                    addPrezzo = true;
                    if (listino instanceof Integer) {
                        Integer control = (Integer) listino;
                        if (control == 1) {
                            sql += ", (select prezzo from righ_fatt where righ_fatt.codice_articolo = movimenti_magazzino.articolo order by anno desc, numero desc limit 1) as prezzo";
                        } else if (control == 2) {
                            sql += ", (select prezzo from righ_fatt_acquisto where righ_fatt_acquisto.codice_articolo = movimenti_magazzino.articolo order by anno desc, numero desc limit 1) as prezzo";
                        } else {
                            addPrezzo = false;
                        }
                    } else {
                        sql += ", IF(tl.ricarico_flag = 'S', (ap2.prezzo + (ap2.prezzo / 100 * tl.ricarico_perc)), ap.prezzo) as prezzo";
                    }
                }
                sql += " FROM"
                        + " `movimenti_magazzino`"
                        + " Left Join `articoli` ON `movimenti_magazzino`.`articolo` = `articoli`.`codice`"
                        + " Left Join `tipi_causali_magazzino` ON `movimenti_magazzino`.`causale` = `tipi_causali_magazzino`.`codice`";
                if (listino instanceof String) {
                    sql += "left Join articoli_prezzi ap on movimenti_magazzino.articolo = ap.articolo"
                            + " left join tipi_listino tl on ap.listino = tl.codice"
                            + " left join articoli_prezzi ap2 on movimenti_magazzino.articolo = ap2.articolo and ap2.listino = tl.ricarico_listino";
                }
                sql += " where 1 = 1";
                if (articolo != null) {
                    sql += " and articolo = " + Db.pc(articolo, Types.VARCHAR);
                }
                if (listino instanceof String) {
                    String val = (String) listino;
                    sql += " and ap.listino = '" + val + "'";
                }
                if (data != null) {
                    sql += " and data <= '" + FormatUtils.formatMysqlDate(data) + "'";
                }
                sql += " GROUP BY"
                        + " `movimenti_magazzino`.`articolo`";
                System.out.println("sql:" + sql);
            }
            ResultSet r = null;
//            Statement s = null;
            try {
//                s = Db.getConn().createStatement();
//                s.executeQuery(sql);
                System.out.println("giac pre");
                r = DbUtils.tryOpenResultSet(Db.getConn(), sql);
                System.out.println("giac post");
                while (r.next()) {
                    try {
                        if (!(!comprendereQtaZero && r.getDouble("giac") == 0) && StringUtils.isNotBlank(r.getString("articolo"))) {
                            Giacenza g = new Giacenza();
                            g.setCodice_articolo(r.getString("articolo"));
                            g.setDescrizione_articolo(r.getString("descrizione"));
                            g.setGiacenza(r.getDouble("giac"));
                            if (addPrezzo && listino != null) {
                                g.setPrezzo(r.getDouble("prezzo"));
                            }
                            if (perMatricola) {
                                g.setMatricola(r.getString("matricola"));
                            }
                            if (flag_lotti) {
                                g.setLotto(r.getString("lotto"));
                            }
                            ret.add(g);
                        }
                    } catch (Exception err) {
                        System.out.println(err);
                    }
                }
            } catch (SQLException sqlerr) {
                sqlerr.printStackTrace();
            } catch (Exception err) {
                err.printStackTrace();
            } finally {
                DbUtils.close(r);
            }

        }

        return ret;
    }

    public static double getInArrivo(String codart) {
        double diff_ordi = 0;
        double diff_ddt = 0;
        try {
            String sql = "select sum(IFNULL(quantita,0) - IFNULL(quantita_evasa,0)) as diff from righ_ordi_acquisto r ";
            sql += " left join test_ordi_acquisto t on r.id_padre = t.id";
            sql += " where t.data >= '2010-11-02'";
            sql += " and t.stato_ordine like '%Ordine%'";
            sql += " and codice_articolo = " + Db.pc(codart, Types.VARCHAR);
            System.out.println("sql = " + sql);
            List list = DbUtils.getListMap(Db.getConn(), sql);
            if (list.size() > 0) {
                diff_ordi = CastUtils.toDouble0(((Map)list.get(0)).get("diff"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        try {
//            String sql = "select sum(IFNULL(quantita,0) - IFNULL(quantita_evasa,0)) as diff from righ_ddt_acquisto r ";
//            sql += " left join test_ddt_acquisto t on r.id_padre = t.id";
//            sql += " where t.data >= '2010-11-02'";
//            sql += " and codice_articolo = " + Db.pc(codart, Types.VARCHAR);
//            System.out.println("sql = " + sql);
//            List list = DbUtils.getListMap(Db.getConn(), sql);
//            if (list.size() > 0) {
//                diff_ddt = CastUtils.toDouble0(((Map)list.get(0)).get("diff"));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return diff_ordi + diff_ddt;
    }

    public static double getInUscita(String codart) {
        double diff_ordi = 0;
        double diff_ddt = 0;
        try {
            String sql = "select sum(IFNULL(quantita,0) - IFNULL(quantita_evasa,0)) as diff from righ_ordi r ";
            sql += " left join test_ordi t on r.id_padre = t.id";
            sql += " where t.data >= '2010-11-02'";
            sql += " and t.stato_ordine like '%Ordine%'";
            sql += " and codice_articolo = " + Db.pc(codart, Types.VARCHAR);
            System.out.println("sql = " + sql);
            List list = DbUtils.getListMap(Db.getConn(), sql);
            if (list.size() > 0) {
                diff_ordi = CastUtils.toDouble0(((Map)list.get(0)).get("diff"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        try {
//            String sql = "select sum(IFNULL(quantita,0) - IFNULL(quantita_evasa,0)) as diff from righ_ddt r ";
//            sql += " left join test_ddt t on r.id_padre = t.id";
//            sql += " where t.data >= '2010-11-02'";
//            sql += " and codice_articolo = " + Db.pc(codart, Types.VARCHAR);
//            System.out.println("sql = " + sql);
//            List list = DbUtils.getListMap(Db.getConn(), sql);
//            if (list.size() > 0) {
//                diff_ddt = CastUtils.toDouble0(((Map)list.get(0)).get("diff"));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return diff_ordi + diff_ddt;
    }
    
    public static void preDelete(String sql) {
        String sqlpredel = "delete from movimenti_magazzino_eliminati " + sql;
        Db.executeSql(sqlpredel);
        String sqlpre = "insert into movimenti_magazzino_eliminati select * from movimenti_magazzino " + sql;
        Db.executeSql(sqlpre);
        sqlpre = "update movimenti_magazzino_eliminati set modificato_ts = CURRENT_TIMESTAMP " + sql;
        Db.executeSql(sqlpre);                
    }
    
}
