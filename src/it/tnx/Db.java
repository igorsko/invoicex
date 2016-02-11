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
package it.tnx;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import gestioneFatture.frmSplash;
import gestioneFatture.main;
import it.tnx.commons.CastUtils;
import it.tnx.commons.DbCacheResultSet;
import it.tnx.commons.DbUtils;
import it.tnx.commons.RuntimeUtils;
import it.tnx.commons.SwingUtils;
import it.tnx.invoicex.gui.JDialogExc;
import java.awt.Font;
import java.io.File;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.NClob;
import java.sql.ResultSet;
import java.sql.RowId;

import java.sql.SQLException;
import java.sql.SQLXML;

import java.sql.Statement;
import java.sql.Types;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.commons.lang.StringUtils;

public class Db implements DbI {

    public static String dbServ;
    public static int dbPort;
    public static String dbPass;
    public static String dbName;
    public static String dbNameDB;
    public static Connection conn;
    public static ArrayList<Connection> conns = new ArrayList();
    public static Statement stat;
    public static String TIPO_DOCUMENTO_DDT = "DD";
    public static String TIPO_DOCUMENTO_DDT_ACQUISTO = "DA";
    public static String TIPO_DOCUMENTO_FATTURA = "FA";
    public static String TIPO_DOCUMENTO_ORDINE = "OR";
    public static String TIPO_DOCUMENTO_ORDINE_ACQUISTO = "OA";
    public static String TIPO_DOCUMENTO_FATTURA_RICEVUTA = "FR";    //di acquisto
    public static String TIPO_DOCUMENTO_SCONTRINO = "SC";
    public static String TIPO_DOCUMENTO_PAGAMENTO_RICORRENTE = "PR";
    public static boolean useNamedPipes = false;
    static public Db INSTANCE = null;
    public static Map<String, Object> cache = new HashMap();

    boolean partitoTimertask = false;
    //TODO  togliere debug
//    static public boolean debug = true;
    static public boolean debug = false;

    static {
        if (RuntimeUtils.isInDebug()) {
            debug = true;
        }
    }
    JSch jsch = null;
    Session jsch_session = null;
    Map<ResultSet, Long> resultsets = Collections.synchronizedMap(new HashMap() {

        @Override
        public Object put(Object key, Object value) {
            Object ret = super.put(key, value);
            if (!partitoTimertask) {
                partitoTimertask = true;
                timerCancelResultset.schedule(timertaskCancelResultset, 15000, 15000);
//                timerCancelResultset.schedule(timertaskCancelResultset, 2000, 2000);
                Runtime.getRuntime().addShutdownHook(new Thread("thread chiusura timer task cancel resultset " + this.toString()) {

                    @Override
                    public void run() {
                        super.run();
                        if (debug) {
                            System.out.println("chiudo " + this.toString());
                        }
                        timertaskCancelResultset.cancel();
                        timerCancelResultset.cancel();
                        timerCancelResultset.purge();
                    }
                });
            }
            return ret;
        }
    });
    Timer timerCancelResultset = new Timer("timerCancelResultset");
    final TimerTask timertaskCancelResultset = new TimerTask() {

        @Override
        public void run() {

            synchronized (resultsets) {
                int timeout = 0;
//                if (debug) {
//                    timeout = 1000 * 60 * 15; //15 minuti
//                } else {
//                    timeout = 1000 * 15; // 15 secondi
//                    timeout = 1000 * 60; // 60 secondi
                timeout = 1000 * 120; // 120 secondi
//                }
                if (debug) {
                    System.out.println("timertaskCancelResultset: resultsets.size:" + resultsets.size() + " resultsets:" + resultsets);
                }
                Iterator<Entry<ResultSet, Long>> iter1 = resultsets.entrySet().iterator();
                while (iter1.hasNext()) {
                    Entry<ResultSet, Long> e1 = iter1.next();
                    Long adesso = System.currentTimeMillis();
                    Long passato = adesso - e1.getValue();
                    if (adesso - e1.getValue() > timeout) {
                        ResultSet r = e1.getKey();
                        try {
//                            if (debug) {
//                                System.out.println("*** chiudo r:" + r + " vivo da:" + passato);
//                            }
                            r.getStatement().close();
                        } catch (Exception e) {
//                            System.err.println(this + " [r.getStatement().close()] " + e.toString());
                        }
                        try {
                            r.close();
                        } catch (Exception e) {
//                            System.err.println(this + " [r.close()]" + e.toString());
                        }
                        r = null;
                        iter1.remove();
                    }
                }
            }
        }
    };

    public Connection getDbConn() {
        return INSTANCE.getConn();
    }

    public static Connection getConn() {
        try {
            if (conn == null || conn.isClosed()) {
                if (INSTANCE == null) {
                    new Db();
                }
                INSTANCE.dbConnect();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Db.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }

    public static Connection getConn(boolean silent) {
        try {
            if (conn == null || conn.isClosed()) {
                INSTANCE.dbConnect(silent);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Db.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }

    public static void setConn(Connection aConn) {
        conn = aConn;
    }

    public Db() {
        INSTANCE = this;
        if (debug) {
            System.out.println("new Db instance:" + this);
        }
    }

    public boolean dbConnect() {
        return dbConnect(false);
    }

    static public void dbClose() {
//        try {
//            //dump processlist
//            System.out.println(DbUtils.getListMap(conn, "show processlist"));
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }

        if (INSTANCE != null) {
            INSTANCE.chiudiTuttiResultSet();
        }

        try {
            stat.close();
        } catch (Exception err) {
            System.out.println("Errore durante la chiusura della statement a mysql: " + err);
        }
        try {
            conn.close();
        } catch (Exception err) {
            System.out.println("Errore durante la chiusura della connessione a mysql: " + err);
        }

        //tento chiusura eventuali altre connessioni
        for (Connection c : conns) {
            try {
                c.close();
            } catch (Exception e) {
            }
        }

        try {
            if (main.fileIni.getValueBoolean("db", "ssh", false)) {
                System.out.println("chiusura connessione ssh");
                //chiudo connessione ssh
                if (INSTANCE.jsch != null) {
                    if (INSTANCE.jsch_session != null) {
                        INSTANCE.jsch_session.disconnect();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Throwable last_connection_err = null;
    public String last_connection_err_msg = null;
    public boolean dbConnect(boolean silent) {

        //ssh tunnel
        if (main.fileIni.getValueBoolean("db", "ssh", false)) {
            //attivare il tunnel ?
            if (jsch == null) {
                jsch = new JSch();
            }
            try {
                if (jsch_session == null || !jsch_session.isConnected()) {
                    jsch_session = jsch.getSession(main.fileIni.getValue("db", "ssh_login"), main.fileIni.getValue("db", "ssh_hostname"), 22);

                    int lport = CastUtils.toInteger0(main.fileIni.getValue("db", "ssh_porta_locale"));
                    int rport = CastUtils.toInteger0(main.fileIni.getValue("db", "ssh_porta_remota"));
                    UserInfo ui = new UserInfo() {

                        public String getPassphrase() {
                            return main.fileIni.getValueCifrato("db", "ssh_password");
                        }

                        public String getPassword() {
                            return main.fileIni.getValueCifrato("db", "ssh_password");
                        }

                        public boolean promptPassword(String message) {
                            System.err.println("ssh promptPassword message: " + message);
                            return true;
                        }

                        public boolean promptPassphrase(String message) {
                            System.err.println("ssh promptPassphrase message: " + message);
                            return false;
                        }

                        public boolean promptYesNo(String message) {
                            System.err.println("ssh promptYesNo message: " + message);
                            if (message.startsWith("The authenticity of host '")) {
                                return true;
                            }
                            return SwingUtils.showYesNoMessage(main.getPadreFrame() == null ? main.splash : main.getPadreFrame(), message, "Invoicex - SSH");
                        }

                        public void showMessage(String message) {
                            System.err.println("ssh showMessage message: " + message);
                        }
                    };
                    jsch_session.setUserInfo(ui);

                    Properties config = new Properties();
                    config.put("compression.s2c", "none");
                    config.put("compression.c2s", "none");
                    jsch_session.setConfig(config);

                    jsch_session.connect();

                    int assinged_port = jsch_session.setPortForwardingL(lport, main.fileIni.getValue("db", "ssh_hostname"), rport);
                    System.err.println("ssh port forward localhost:" + assinged_port + " -> " + main.fileIni.getValue("db", "ssh_hostname") + ":" + rport);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            Connection conn = getConnection();
            setConn(conn);
            stat = getConn().createStatement();

            ResultSet rtest = stat.executeQuery("SELECT @@sql_mode");
            if (rtest.next()) {
                System.out.println("sql_mode=" + rtest.getString(1));
            }

            return (true);
        } catch (Exception e) {
            last_connection_err = e;
            e.printStackTrace();
            
            String msg = null;
            try {
                String errore = e.getCause().toString();
                if (e.getCause() instanceof UnknownHostException) {
                    main.splash("Host sconosciuto: " + e.getCause().getMessage());
                } else {
                    main.splash(e.getCause().getMessage());
                }
                if (errore.toLowerCase().indexOf("access denied") >= 0 && errore.toLowerCase().indexOf("using password: yes") >= 0) {
                    msg = "Impossibile aprire i dati, PASSWORD errata\n\n";
                } else if (errore.toLowerCase().indexOf("access denied") >= 0 && errore.toLowerCase().indexOf("using password: no") >= 0) {
                    msg = "Impossibile aprire i dati, PASSWORD vuota\n\n";
                } else if (errore.toLowerCase().indexOf("unknown database") >= 0) {
                    msg = "Impossibile aprire i dati, nome del database errato\n\n";
                } else if (e.getCause() != null && e.getCause().getCause() != null && e.getCause().getCause() instanceof ConnectException) {
                    msg = "Impossibile collegarsi con il database\n\n";
                } else if (e.getCause() != null && e.getCause() instanceof UnknownHostException) {
                    msg = "Host sconosciuto: " + e.getCause().getMessage() + "\n\n";
                } else if (e.getCause() != null && StringUtils.isNotEmpty(e.getCause().getMessage())) {
                    msg = e.getCause().getMessage();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            last_connection_err_msg = msg;

            if (!silent) {
                JDialogExc de = null;
                if (msg == null) {
                    msg = "Impossibile aprire i dati\n\n";
                    msg = msg + " [Errore:" + StringUtils.abbreviate(e.toString(), 50) + "]";
                    de = new JDialogExc(new JFrame(), true, e);
                    de.labInt.setFont(de.labInt.getFont().deriveFont(Font.BOLD, 14));
                } else {
                    if (e.getCause() instanceof UnknownHostException) {
                        de = new JDialogExc(new JFrame(), true, null);
                    } else {
                        de = new JDialogExc(new JFrame(), true, e.getCause());
                    }
                    de.labInt.setFont(de.labInt.getFont().deriveFont(Font.BOLD, 16));
                }
                de.labInt.setText(msg);
                de.labe.setFont(de.labInt.getFont().deriveFont(Font.PLAIN, 14));
                de.pack();
                de.setLocationRelativeTo(null);
                de.setVisible(true);
            }

            return (false);
        }
    }

    static public boolean dbConnect(String driver, String url, String login, String password) {
        try {
            Class.forName(driver).newInstance();
        } catch (Exception err) {
            System.out.println("Errore nel caricamento del driver di mysql:" + err.toString());
        }
        try {
            setConn(DriverManager.getConnection(url, login, password));
            stat = getConn().createStatement();
            return (true);
        } catch (Exception e) {
            return (false);
        }
    }

    static public boolean dbConnectTest() {
        String jdbcDriver = "com.mysql.jdbc.Driver";
        String jdbcUrl = "jdbc:mysql://linux/clientmanagerdemo?autoReconnect=true&jdbcCompliantTruncation=false&zeroDateTimeBehavior=round&emptyStringsConvertToZero=true";
        String jdbcLogin = "root";
        String jdbcPassword = "***";
        try {
            Class.forName(jdbcDriver).newInstance();
            setConn(DriverManager.getConnection(jdbcUrl, jdbcLogin, jdbcPassword));
            stat = getConn().createStatement();
            return (true);
        } catch (Exception e) {
            return (false);
        }
    }

    public Connection dbGetConnection() {

        return (getConn());
    }

    public static double parteIntera(double valore) {

        //return (valore - Math.abs(valore - Math.round(valore)));
        Double temp = new Double(valore);

        //per eccesso
        if ((valore - temp.intValue()) == 0) {

            return (temp.intValue());
        } else {

            return (temp.intValue() + 1);
        }
    }

    public static boolean isPari(int valore) {

        //return (valore - Math.abs(valore - Math.round(valore)));
        Double temp = new Double((double) valore / 2);

        //per eccesso
        if (temp.doubleValue() - temp.intValue() == 0) {

            return (true);
        } else {

            return (false);
        }
    }

    public static String spezza(String stringa, int ogni, String intermezzo) {

        String temp = "";

        for (int i = 0; i < stringa.length(); i = i + ogni) {

            if ((i + ogni) > stringa.length()) {
                temp += stringa.substring(i, stringa.length());
            } else {
                temp += stringa.substring(i, i + ogni) + intermezzo + "\n";
            }
        }

        return (temp);
    }

    public static String aa(String stringa) {
        //aggiunge apice al singolo
        if (stringa != null) {
            if (stringa.length() > 0) {
                return (replaceChars(stringa, '\'', "''"));
            }
        }
        return ("");
    }

    public static String pc(int campo, String tipoCampo) {

        return (pc(String.valueOf(campo), tipoCampo));
    }

    public static String pc(double campo, String tipoCampo) {

        return (pc(String.valueOf(campo), tipoCampo));
    }

    public static String pcs(String campo) {
        return pc(campo, Types.VARCHAR);
    }

    public static String pcdate(Date campo) {
        return pc(campo, Types.DATE);
    }

    public static String pc(String campo, String tipoCampo) {

        //prepara il campo per sql
        //debug
        //javax.swing.JOptionPane.showMessageDialog(null,"campo:"+campo+" tipo:"+tipoCampo);
        if (tipoCampo.equalsIgnoreCase("LONG") || tipoCampo.equalsIgnoreCase("INTEGER")) {

            if (campo.length() == 0) {

                return ("null");
            } else {

                return (campo);
            }
        } else if (tipoCampo.equalsIgnoreCase("DECIMAL")) {

            if (campo.length() == 0) {

                return ("null");
            } else {

                return ("(" + campo + ")");
            }
        } else if (tipoCampo.equalsIgnoreCase("DOUBLE")) {

            if (campo.length() == 0) {

                return ("null");
            } else {

                return ("(" + campo + ")");
            }
        } else if (tipoCampo.equalsIgnoreCase("NUMBER")) {

            if (campo.length() == 0) {

                return ("null");
            } else {

                return ("(" + campo + ")");
            }
        } else if (tipoCampo.equalsIgnoreCase("VARCHAR")) {

            return ("'" + aa(campo) + "'");
        } else {

            return ("'" + aa(campo) + "'");
        }
    }

    public static String pc(Object campo, int tipoCampo) {

        //prepara il campo per sql
        //debug
        //javax.swing.JOptionPane.showMessageDialog(null,"campo:"+campo+" tipo:"+tipoCampo);
        if (tipoCampo == java.sql.Types.BIGINT || tipoCampo == Types.DECIMAL || tipoCampo == Types.DOUBLE || tipoCampo == Types.FLOAT || tipoCampo == Types.INTEGER || tipoCampo == Types.REAL || tipoCampo == Types.SMALLINT || tipoCampo == Types.TINYINT) {

            if (campo == null) {

                return ("null");
            } else {

                if (campo.toString().length() == 0) {

                    return ("null");
                } else {

                    return ("(" + campo + ")");
                }
            }
        } else if (tipoCampo == Types.CHAR || tipoCampo == Types.LONGVARCHAR || tipoCampo == Types.VARCHAR) {

            if (campo == null) {

                return "null";
            } else {

                return ("'" + aa(String.valueOf(campo)) + "'");
            }
        } else if (tipoCampo == Types.DATE) {

//            return ("'" + campo + "'");

            if (campo instanceof String) {
                //controllo se è nel formato mysql
                if (campo.toString().length() == 10 && campo.toString().substring(4, 5).equals("-") && campo.toString().substring(7, 8).equals("-")) {
                    return ("'" + campo + "'");
                } else {
                    DateFormat myFormat = new SimpleDateFormat("dd/MM/yy");
                    DateFormat myFormatSql = new SimpleDateFormat("yyyy-MM-dd");
                    myFormat.setLenient(false);
                    try {
                        java.util.Date myDate = myFormat.parse(campo.toString());
                        return "'" + myFormatSql.format(myDate) + "'";
                    } catch (Exception err) {
                        if (campo != null && campo.toString().length() == 0) {
                            return "null";
                        } else {
                            System.out.println("errore in campo: " + campo);
                            err.printStackTrace();
                            //return ("0");
                            return "null";
                        }
                    }
                }
            } else if (campo instanceof Date) {
                DateFormat myFormatSql = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    return "'" + myFormatSql.format((Date) campo) + "'";
                } catch (Exception err) {
                    System.out.println("errore in campo: " + campo);
                    err.printStackTrace();
                    return ("0");
                }
            } else {
                if (campo == null) {
                    return "null";
                } else {
                    return ("'" + aa(campo.toString()) + "'");
                }
            }
        } else if (tipoCampo == Types.TIMESTAMP) {
            if (campo instanceof String) {
                //controllo se è nel formato mysql
                if (campo.toString().length() == 19 && campo.toString().substring(4, 5).equals("-") && campo.toString().substring(7, 8).equals("-")) {
                    return ("'" + campo + "'");
                } else {
                    DateFormat myFormat = new SimpleDateFormat("dd/MM/yy H:m:s");
                    DateFormat myFormatSql = new SimpleDateFormat("yyyy-MM-dd H:m:s");
                    myFormat.setLenient(false);
                    try {
                        java.util.Date myDate = myFormat.parse(campo.toString());
                        return "'" + myFormatSql.format(myDate) + "'";
                    } catch (Exception err) {
                        System.out.println("errore in campo: " + campo);
                        err.printStackTrace();
                        return ("0");
                    }
                }
            } else if (campo instanceof Date) {
                DateFormat myFormatSql = new SimpleDateFormat("yyyy-MM-dd H:m:s");
                try {
                    return "'" + myFormatSql.format((Date) campo) + "'";
                } catch (Exception err) {
                    System.out.println("errore in campo: " + campo);
                    err.printStackTrace();
                    return ("0");
                }
            } else {
                if (campo == null) {
                    return "null";
                } else {
                    return ("'" + aa(campo.toString()) + "'");
                }
            }
        } else {
            if (campo == null) {
                return "null";
            } else {
                return ("'" + aa(campo.toString()) + "'");
            }
        }
    }

    //108 cambio, metto format 'yyyy-mm-dd'
    public static String pc2(String campo, int tipoCampo) {

        if (tipoCampo == Types.DATE) {

            DateFormat myFormat = new SimpleDateFormat("dd/MM/yy");
            DateFormat myFormatSql = new SimpleDateFormat("yyyy-MM-dd");
            myFormat.setLenient(false);

            try {

                java.util.Date myDate = myFormat.parse(campo.toString());

                return "'" + myFormatSql.format(myDate) + "'";
            } catch (Exception err) {
                System.out.println("errore in campo: " + campo);
                err.printStackTrace();

                return ("0");
            }
        } else {

            return ("'" + aa(campo.toString()) + "'");
        }
    }

    public static String pc(int campo, int tipoCampo) {

        return pc(String.valueOf(campo), tipoCampo);
    }

    public static String pc(double campo, int tipoCampo) {

        return pc(String.valueOf(campo), tipoCampo);
    }

    public static String pc(Object campo, int tipoCampo, String partePrima, String parteDopo) {

        return pc(partePrima + String.valueOf(campo) + parteDopo, tipoCampo);
    }

    public static String nz(String valore, String seNullo) {

        if (valore == null) {
            return (seNullo);
        }

        return (valore);
    }

    public static String nz(Object valore, String seNullo) {

        if (valore == null) {
            return (seNullo);
        }

        return (valore.toString());
    }

    public static String replaceChars(String stri, char daTrov, String daMett) {

        int leng = stri.length();
        String prim = "";
        String dopo = "";
        String risu = "";
        int i = 0;
        int oldI = 0;

        while (i < leng) {

            if (stri.charAt(i) == daTrov) {
                prim = stri.substring(oldI, i);
                risu = risu + prim + daMett;
                oldI = i + 1;
            }

            i++;
        }

        risu = risu + stri.substring(oldI, leng);

        return risu;
    }

    public static String replaceStrings(String object, String toFind, String toSubstitute) {
        return (object.replaceAll("\\" + toFind, toSubstitute));
    }

    public static ResultSet openResultSet(String sql) {
        return openResultSet(null, sql);
    }

    private static ResultSet openResultSetCache(Connection conn, String sql) {
        if (cache.containsKey(sql) && ((Long)cache.get(sql + "|time") <= System.currentTimeMillis() + 30000)) {
            System.out.println("db openresult da cache sql:" + sql);
            return (ResultSet) cache.get(sql);
        }
        return null;
    }

    private static ResultSet openResultSetInCache(Connection conn, String sql, ResultSet r) {
        if (sql.equalsIgnoreCase("select logo from dati_azienda")
                || sql.equalsIgnoreCase("select logo_email from dati_azienda")
                || sql.equalsIgnoreCase("select sfondo from dati_azienda")
                || sql.equalsIgnoreCase("select sfondo_email from dati_azienda")
                || sql.indexOf("SELECT * FROM accessi_tipi_permessi WHERE id =") >= 0
                ) {        
            DbCacheResultSet r2 = new DbCacheResultSet(conn, sql, r) {

                public RowId getRowId(int columnIndex) throws SQLException {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                public RowId getRowId(String columnLabel) throws SQLException {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                public void updateRowId(int columnIndex, RowId x) throws SQLException {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                public void updateRowId(String columnLabel, RowId x) throws SQLException {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                public NClob getNClob(int columnIndex) throws SQLException {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                public NClob getNClob(String columnLabel) throws SQLException {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                public SQLXML getSQLXML(int columnIndex) throws SQLException {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                public SQLXML getSQLXML(String columnLabel) throws SQLException {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            };
            cache.put(sql, r2);
            cache.put(sql + "|time", System.currentTimeMillis());
            return r2;
        }
        return null;
    }

    public static ResultSet openResultSet(Connection conn, String sql) {
        //cache
        ResultSet ret = openResultSetCache(conn, sql);
        if (ret != null) {
            try {
                ret.beforeFirst();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ret;
        }

        System.err.println("openResultSet: " + sql);

        if (sql == null) {
            return null;
        }

        //apre il resultset per ultimo +1
        try {

            Connection lconn = null;
            if (conn != null) {
                lconn = conn;
            } else {
                lconn = Db.getConn();
            }
            final Statement stat = lconn.createStatement();
//            stat.setQueryTimeout(30);
            stat.setQueryTimeout(60);
            final ResultSet resu = stat.executeQuery(sql);

//            Thread run = new Thread("close statement openResultSet sql:" + sql) {
//                public void run() {
//                    try {
//                        Thread.sleep(15000);
//                        //System.out.println("chiusura stat e resu dentro openresultset: " + stat + " / " + resu);
//                        stat.close();
//                        resu.close();
//                    } catch (Exception err) {
//                        err.printStackTrace();
//                    }
//                }
//            };
//            run.start();

            if (INSTANCE == null) {
                new Db();
            }

            synchronized (INSTANCE.resultsets) {
                INSTANCE.resultsets.put(resu, System.currentTimeMillis());
            }

            ResultSet r2 = openResultSetInCache(conn, sql, resu);
            if (r2 != null) {
                return r2;
            }

            return (resu);
        } catch (Exception err) {
            err.printStackTrace();
            System.out.println("sql di errore:" + sql);

            return (null);
        }
    }

    public static ResultSet lookUp(String valoreChiave, String campoChiave, String tabella) {
        System.err.println("lookup: " + valoreChiave + ":" + campoChiave + ":" + tabella);

        //apre il resultset per ultimo +1
        String sql = "";
        sql = "select * from " + tabella + " where " + campoChiave + " = " + Db.pc(valoreChiave, "VARCHAR");

        try {

            final Statement stat = Db.getConn().createStatement();
            final ResultSet resu = stat.executeQuery(sql);
//            Thread run = new Thread("close statement lookUp tabella:" + tabella + " campoChiave:" + campoChiave + " valoreChiave:" + valoreChiave) {
//
//                public void run() {
//
//                    try {
//                        Thread.sleep(5000);
//                        //System.out.println("chiusura stat e resu dentro openresultset: " + stat + " / " + resu);
//                        stat.close();
//                        resu.close();
//                    } catch (Exception err) {
//                        err.printStackTrace();
//                    }
//                }
//            };
//            run.start();

            synchronized (INSTANCE.resultsets) {
                INSTANCE.resultsets.put(resu, System.currentTimeMillis());
            }

            if (resu.next()) {
                return (resu);
            } else {
                return (null);
            }
        } catch (Exception err) {
            err.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, err.toString());

            return (null);
        }
    }

    public static boolean executeSql(String sql) {
        return executeSql(null, sql);
    }

    public static boolean executeSql(Connection conn, String sql) {
        return executeSqlDialogExc(conn, sql, false);
    }

    public static void executeSqlThrows(String sql) throws Exception {
        executeSqlDialogExc(sql, false, true);
    }

    public static boolean executeSqlDialogExc(String sql, boolean showexc) {
        return executeSqlDialogExc(null, sql, showexc);
    }

    public static boolean executeSqlDialogExc(Connection conn, String sql, boolean showexc) {
        try {
            return executeSqlDialogExc(conn, sql, showexc, false);
        } catch (Exception e) {
        }
        return false;
    }

    public static boolean executeSqlDialogExc(String sql, boolean showexc, boolean throwsexc) throws Exception {
        return executeSqlDialogExc(null, sql, showexc, throwsexc);
    }

    public static boolean executeSqlDialogExc(Connection conn, String sql, boolean showexc, boolean throwsexc) throws Exception {
        //debug
//        StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
//        System.out.println("---START SQL TRACE---");
        System.out.println("sql: " + sql);
//        for (StackTraceElement e : stacks) {
//            if (e.getClassName().startsWith("gestioneFatture") || e.getClassName().startsWith("it.tnx")) {
//                System.out.println("TRACE: \t\t" + e.toString());
//            }
//        }
//        System.out.println("---END SQL TRACE---");

        Statement stat = null;
        //lancia la query
        try {
            if (conn != null) {
                stat = conn.createStatement();
            } else {
                stat = Db.getConn().createStatement();
            }
            stat.execute(sql);
//            System.out.println("sql debug:" + sql);
            stat.close();
            return (true);
        } catch (Exception err) {
            if (showexc) {
                err.printStackTrace();
                JDialogExc de = new JDialogExc(main.getPadre(), true, err);
                de.setLocationRelativeTo(null);
                de.pack();
                de.setVisible(true);
                return false;
            } else if (throwsexc) {
                throw err;
            } else {
                if (err.toString().indexOf("Duplicate column name") >= 0 || err.toString().indexOf("Duplicate key name") >= 0) {
                    //se il campo c'è già' in caso di dbchanges
                    return true;
                } else if (err.toString().indexOf("already exists") >= 0) {
                    //se la tabella c'è già
                    return true;
                } else if (err.toString().toLowerCase().indexOf("unknown column") >= 0 && sql.toLowerCase().indexOf(" change ") >= 0) {
                    //se il campo non c'è (ignoro errori per truncate log2)
                    return true;
                } else {
                    err.printStackTrace();
                    //javax.swing.JOptionPane.showMessageDialog(null,err.toString());
                    System.out.println("sql di errore:" + sql);
                    return (false);
                }
            }
        } finally {
            try {
                stat.close();
            } catch (Exception e) {
            }
        }
    }

    public static boolean executeSqlSplitByNl(String sql) {

        Statement stat = null;

        //lancia la query
        try {
            stat = Db.getConn().createStatement();

            String[] sqls = sql.split("\\n");
            for (int i = 0; i < sqls.length; i++) {
                String sqlc = sqls[i];
                stat.execute(sqlc);
            }

//            System.out.println("sql debug:" + sql);
            stat.close();

            return (true);
        } catch (Exception err) {
            err.printStackTrace();

            //javax.swing.JOptionPane.showMessageDialog(null,err.toString());
            System.out.println("sql di errore:" + sql);

            return (false);
        } finally {
            try {
                stat.close();
            } catch (Exception e) {
            }
        }
    }

    public static boolean executeSqlSplitByPVNl(String sql) {
        //punto e virgola e nl
        Statement stat = null;
        //lancia la query
        try {
            stat = Db.getConn().createStatement();
            String[] sqls = sql.split(";\\r\\n");
            for (int i = 0; i < sqls.length; i++) {
                String sqlc = sqls[i];
                System.out.println("qyery " + i + ":" + sqlc);
                stat.execute(sqlc);
            }

            stat.close();
            return (true);
        } catch (Exception err) {
            err.printStackTrace();
            //javax.swing.JOptionPane.showMessageDialog(null,err.toString());
            System.out.println("sql di errore:" + sql);
            return (false);
        } finally {
            try {
                stat.close();
            } catch (Exception e) {
            }
        }
    }

    public static boolean executeSql(String sql, boolean printErrors) {
        //debug
        StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
        System.out.println("---START SQL TRACE---");
        System.out.println("sql: " + sql);
        for (StackTraceElement e : stacks) {
            if (e.getClassName().startsWith("gestioneFatture") || e.getClassName().startsWith("it.tnx")) {
                System.out.println("TRACE: \t\t" + e.toString());
            }
        }
        System.out.println("---END SQL TRACE---");

        Statement stat = null;

        //lancia la query
        try {
            stat = Db.getConn().createStatement();
            stat.execute(sql);
//            System.out.println("sql debug:" + sql);
            stat.close();
            return (true);
        } catch (Exception err) {
            if (printErrors == true) {
                err.printStackTrace();
                //javax.swing.JOptionPane.showMessageDialog(null,err.toString());
                System.out.println("sql di errore:" + sql);
            }
            return (false);
        } finally {
            try {
                stat.close();
            } catch (Exception e) {
            }
        }
    }

    //static public String getNum(double numero) {
    static public String getNum(double numero) {

        //da un double ritorna una stringa formattata a modino
        java.text.DecimalFormat form = new java.text.DecimalFormat("#,##0.00");

        //double d = Double.parseDouble(form.format(numero));
        //return d;
        return (form.format(numero));
    }

    //static public String getNum(double numero) {
    static public double getNumDouble(double numero) {

        java.math.BigDecimal bigDec = new java.math.BigDecimal(numero);
        bigDec = bigDec.setScale(2, java.math.BigDecimal.ROUND_HALF_EVEN);

        //debug
        //System.out.println("getNumDouble:"+numero+":"+bigDec.doubleValue());
        return (bigDec.doubleValue());

        /*

        java.text.DecimalFormat form = null;

        try {

        //da un double ritorna una stringa formattata a modino

        form = new java.text.DecimalFormat("#,##0.00");

        double d = Double.parseDouble(form.format(numero));

        return d;

        //return (form.format(numero));

        } catch (Exception err) {

        System.out.println("err:getNumDouble:" + numero + ":" + form.format(numero));

        err.printStackTrace();

        return 0;

        }*/
    }

    static public double getDouble(String numero) {

        //ritorna un double da una stringa con la virgola invece che il punto come separatore
        try {
            numero = Db.replaceChars(numero, '.', "");
            numero = numero.replace(',', '.');

            return (Double.valueOf(numero).doubleValue());
        } catch (Exception err) {
            System.out.println("!!! warning getDouble:" + numero);

            //err.printStackTrace();
            return (0.0);
        }
    }

    static public String formatDecimal(double valore) {
        NumberFormat form = DecimalFormat.getInstance(Locale.ITALIAN);
        form.setMaximumFractionDigits(2);
        form.setMinimumFractionDigits(2);

        return (form.format(valore));
    }

    static public String formatDecimal5(double valore) {
        NumberFormat form = DecimalFormat.getInstance(Locale.ITALIAN);
        form.setMaximumFractionDigits(5);
        form.setMinimumFractionDigits(2);
        return (form.format(valore));
    }

    static public String formatNumero(double valore) {
        NumberFormat form = DecimalFormat.getInstance(Locale.ITALIAN);
        form.setMaximumFractionDigits(2);
        form.setMinimumFractionDigits(0);

        if (form.format(valore).equalsIgnoreCase("0")) {

            return ("");
        } else {

            return (form.format(valore));
        }
    }

    static public String formatValuta(double valore) {
        NumberFormat form = DecimalFormat.getInstance(Locale.ITALIAN);
        form.setMaximumFractionDigits(2);
        form.setMinimumFractionDigits(2);

        if (form.format(valore).equalsIgnoreCase("0")) {

            return ("");
        } else {

            return (form.format(valore));
        }
    }

    static public String formatNumero0(double valore) {
        NumberFormat form = DecimalFormat.getInstance(Locale.ITALIAN);
        form.setMaximumFractionDigits(0);
        form.setMinimumFractionDigits(0);

        if (form.format(valore).equalsIgnoreCase("0")) {

            return ("");
        } else {

            return (form.format(valore));
        }
    }

    static public String formatData(String dataMysql) {

        return (dataMysql.substring(8, 10) + "/" + dataMysql.substring(5, 7) + "/" + dataMysql.substring(0, 4));
    }

    static public String formatDataMysql(java.util.Date data) {

        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");

        return (dateFormat.format(data));
    }

    public void dbControllo(boolean visualizzaMessaggio, Object splash) {

        frmSplash spl;

        if (splash == null) {
            spl = new frmSplash();
            spl.setBounds(100, 100, 150, 60);
            spl.show();
        } else {
            spl = (frmSplash) splash;
        }

        //this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        spl.jProgressBar1.setValue(65);
        spl.jLabel1.setText("controllo tabelle");

        String sql = "REPAIR TABLE ante,";
        sql += "articoli,";
        sql += "clie_forn,";
        sql += "finestre,";
        sql += "locks,";
        sql += "prev_righ,";
        sql += "prev_test,";
        sql += "tipi_articoli,";
        sql += "tipi_legno,";
        sql += "tipi_serie,";
        sql += "varianti_coeff,";
        sql += "varianti_prezzi,";
        sql += "varianti_tipi";

        try {

            Db db = Db.INSTANCE;
            java.sql.ResultSet tempRipara = db.openResultSet(sql);
            String esito = "";
            System.out.println("Esito riparazione:");

            while (tempRipara.next()) {
                System.out.println(tempRipara.getString(1) + " " + tempRipara.getString(2) + " " + tempRipara.getString(3) + " " + tempRipara.getString(4));
                esito += tempRipara.getString(1) + " " + tempRipara.getString(2) + " " + tempRipara.getString(3) + " " + tempRipara.getString(4) + "\n";
            }

            if (visualizzaMessaggio == true) {
                spl.jProgressBar1.setValue(100);
                JOptionPane.showMessageDialog(null, esito, "Esito controllo", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception err) {
            err.printStackTrace();
        }

        spl.jProgressBar1.setValue(100);
        spl.jLabel1.setText("ok");
        spl.dispose();

        //this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }

    public static String getCurrDateTimeMysql() {

        Statement stat;
        ResultSet resu;
        String sql = "";
        sql = "SELECT NOW() as now";

        try {
            stat = Db.getConn().createStatement();
            resu = stat.executeQuery(sql);
            resu.next();

            String s = resu.getString(1);
            resu.close();
            stat.close();

            return s;
        } catch (Exception err) {
            err.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, err.toString());

            return (null);
        }
    }

    public static String getCurrDateTimeMysqlIta() {

        Statement stat;
        ResultSet resu;
        String sql = "";
        sql = "SELECT NOW() as now";

        try {
            stat = Db.getConn().createStatement();
            resu = stat.executeQuery(sql);
            resu.next();

            String tempData = resu.getString(1);
            String anno = tempData.substring(2, 4);
            String mese = tempData.substring(5, 7);
            String giorno = tempData.substring(8, 10);
            String hh = tempData.substring(11, 13);
            String mm = tempData.substring(14, 16);
            String ss = tempData.substring(17, 19);
            resu.close();
            stat.close();

            return (giorno + "/" + mese + "/" + anno + " " + hh + ":" + mm);
        } catch (Exception err) {
            err.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, err.toString());

            return (null);
        }
    }

    static public boolean duplicateTableStructure(String tableNameSource, String tableNameDestination, boolean addUserField) {

        //SHOW COLUMNS FROM righ_fatt
        //INSERT INTO tblTemp2 (fldID) SELECT tblTemp1.fldOrder_ID
        //   FROM tblTemp1 WHERE tblTemp1.fldOrder_ID > 100;
        //PRIMARY KEY  (serie,numero,riga,anno),
        ResultSet show;
        String sqlPrimary;
        String sqlCreate;
        String sqlDefault;
        String sqlNull;

        try {
            show = Db.openResultSet("show columns from " + tableNameSource);
            sqlCreate = "create table " + tableNameDestination + " (\n";
            sqlPrimary = "primary key (";

            while (show.next()) {

                if (show.getString("Key").equals("PRI")) {
                    sqlPrimary += show.getString("Field") + ",";
                }

                sqlDefault = "";
                
                String def = show.getString("Default");
                def = Db.nz(def, "");
                if (def.length() > 0) {
                    if (def.equalsIgnoreCase("CURRENT_TIMESTAMP")) {
                        sqlDefault = "default " + def;
                    } else {
                        sqlDefault = "default '" + def + "'";
                    }
                }

                sqlNull = "NOT NULL";

                if ("YES".equalsIgnoreCase(show.getString("Null"))) {
                    sqlNull = "NULL";
                }

                sqlCreate += show.getString("Field") + " " + show.getString("Type") + " " + sqlNull + " " + sqlDefault + ",\n";
            }

            //tolgo ultima virgola
            //se si vuole inserisco campo username
            if (addUserField == true) {
                sqlCreate += "username varchar(250) not null,\n";
                sqlPrimary = sqlPrimary.substring(0, sqlPrimary.length() - 1) + ", username)";
            } else {
                sqlPrimary = sqlPrimary.substring(0, sqlPrimary.length() - 1) + ")";
            }

            //creo sql finale di create
            sqlCreate = sqlCreate + sqlPrimary + ")";

            //debug
            System.out.println("sqlCreate:" + sqlCreate);

            //eseguo
            //Db.executeSql(sqlCreate);
            try {
                DbUtils.tryExecQuery(Db.conn, sqlCreate);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return (true);
        } catch (Exception err) {
            err.printStackTrace();

            return (false);
        }
    }

    static public boolean checkTableStructure(String tableNameSource, String tableNameDestination, boolean addUserField) {

        //SHOW COLUMNS FROM righ_fatt
        //INSERT INTO tblTemp2 (fldID) SELECT tblTemp1.fldOrder_ID
        //   FROM tblTemp1 WHERE tblTemp1.fldOrder_ID > 100;
        //PRIMARY KEY  (serie,numero,riga,anno),
        ResultSet showS;
        ResultSet showD;
        int contaS = 0;
        int contaD = 0;

        try {
            showS = Db.openResultSet("show columns from " + tableNameSource);
            showD = Db.openResultSet("show columns from " + tableNameDestination);

            //scorro per calcolare conta
            while (showS.next()) {
                contaS++;
            }

            showS.first();

            while (showD.next()) {
                contaD++;
            }

            showD.first();

            //controllo per numero campi
            if (addUserField == true) {
                contaD--;
            }

            if (contaS != contaD) {

                return false;
            }

            //proseguo per controllare che siano uguali in tutto
            while (showS.next()) {
                showD.next();

                if (!showS.getString("Field").equals(showD.getString("Field"))) {

                    return false;
                }

                if (!showS.getString("Type").equals(showD.getString("Type"))) {

                    return false;
                }

                if (!showS.getString("Null").equals(showD.getString("Null"))) {

                    return false;
                }

                if (!Db.nz(showS.getString("Default"), "").equals(Db.nz(showD.getString("Default"), ""))) {

                    return false;
                }
            }

            return (true);
        } catch (Exception err) {
            err.printStackTrace();

            return (false);
        }
    }

    static public String getFieldList(String tableName, boolean noLast) {

        //SHOW COLUMNS FROM righ_fatt
        ResultSet show;
        String fields = "";
        int conta = 0;
        int conta2 = 0;

        try {
            show = Db.openResultSet("show columns from " + tableName);

            while (show.next()) {
                conta++;
            }

            show.beforeFirst();

            while (show.next()) {
                conta2++;

                if (noLast == true) {

                    if (conta2 < conta) {
                        fields += show.getString("Field") + ",";
                    }
                } else {
                    fields += show.getString("Field") + ",";
                }
            }

            fields = fields.substring(0, fields.length() - 1);

            return (fields);
        } catch (Exception err) {
            err.printStackTrace();

            return ("");
        }
    }

    public static String pcW(int numero, String string) {
        return pcW(String.valueOf(numero), string);
    }

    public static String pcW(String campo, String tipoCampo) {

        //prepara il campo per sql per WHere (= null -> is null
        //debug
        //javax.swing.JOptionPane.showMessageDialog(null,"campo:"+campo+" tipo:"+tipoCampo);
        if (tipoCampo == "LONG" || tipoCampo == "INTEGER") {

            if (campo.length() == 0) {

                return ("null");
            } else {

                return (" = " + campo);
            }
        } else if (tipoCampo == "DECIMAL") {

            if (campo.length() == 0) {

                return ("null");
            } else {

                return (" = (" + campo + ")");
            }
        } else if (tipoCampo == "DOUBLE") {

            if (campo.length() == 0) {

                return ("null");
            } else {

                return (" = (" + campo + ")");
            }
        } else if (tipoCampo == "NUMBER") {

            if (campo.length() == 0) {

                return ("null");
            } else {

                return (" = (" + campo + ")");
            }
        } else if (tipoCampo == "VARCHAR") {

            if (campo == null) {

                return (" is null");
            } else {

                return (" = '" + aa(campo) + "'");
            }
        } else {

            if (campo == null) {

                return (" is null");
            } else {

                return (" = '" + aa(campo) + "'");
            }
        }
    }

    public static String preparaSqlInsert(Vector sqlC, Vector sqlV, String tableName) {

        if (sqlC.size() != sqlV.size()) {
            return "preparaSqlInsert, numero campi diversi sqlC=" + sqlC.size() + " sqlV=" + sqlV.size();
        }

        String sql = "replace into " + tableName + " (";

        for (int c = 0; c < sqlC.size(); c++) {

            if (c == sqlC.size() - 1) {
                sql += sqlC.get(c).toString();
            } else {
                sql += sqlC.get(c).toString() + ", ";
            }
        }

        sql += ") values (";

        for (int c = 0; c < sqlV.size(); c++) {

            if (c == sqlV.size() - 1) {
                sql += sqlV.get(c).toString();
            } else {
                sql += sqlV.get(c).toString() + ", ";
            }
        }

        sql += ")";

        return sql;
    }

    public static boolean contain(String tableName, String fieldName, int fieldType, String value) {

        String sql = "select " + fieldName;
        sql += " from " + tableName;
        sql += " where " + fieldName + " = " + Db.pc(value, fieldType);

        try {

            ResultSet r = Db.openResultSet(sql);

            if (r.next()) {

                return true;
            }
        } catch (Exception err) {
            err.printStackTrace();
        }

        return false;
    }

    public static String getSerieDefault() {
        String serie = "";
        String sql = "select serie_fatture from dati_azienda";
        try {
            ResultSet r = DbUtils.tryOpenResultSet(getConn(), sql);
            if (r.next()) {
                serie = r.getString("serie_fatture");
            }
            r.getStatement().close();
            r.close();
        } catch (Exception e) {
            //e.printStackTrace();
            System.err.println("getSerieDefault() errore:" + e);
        } finally {
        }
        return serie;
    }

    public static String getDescTipoDoc(String tipoDoc) {
        HashMap descs = new HashMap();
        descs.put(TIPO_DOCUMENTO_DDT, "DDT Vendita");
        descs.put(TIPO_DOCUMENTO_DDT_ACQUISTO, "DDT Acquisto");
        descs.put(TIPO_DOCUMENTO_FATTURA, "Fattura Vendita");
        descs.put(TIPO_DOCUMENTO_FATTURA_RICEVUTA, "Fattura Acquisto");
        descs.put(TIPO_DOCUMENTO_ORDINE, "Ordine Vendita");
        descs.put(TIPO_DOCUMENTO_ORDINE_ACQUISTO, "Ordine Acquisto");
        descs.put(TIPO_DOCUMENTO_SCONTRINO, "Scontrino");
        descs.put(TIPO_DOCUMENTO_PAGAMENTO_RICORRENTE, "Pagamento ricorrente");
        try {
            return (String) descs.get(tipoDoc);
        } catch (Exception e) {
            e.printStackTrace();
            return tipoDoc;
        }
    }

    public static String getDescTipoDocBrevissima(String tipoDoc) {
        HashMap descs = new HashMap();
        descs.put(TIPO_DOCUMENTO_DDT, "DDT");
        descs.put(TIPO_DOCUMENTO_DDT_ACQUISTO, "DDT");
        descs.put(TIPO_DOCUMENTO_FATTURA, "Fatt.");
        descs.put(TIPO_DOCUMENTO_FATTURA_RICEVUTA, "Fatt.");
        descs.put(TIPO_DOCUMENTO_ORDINE, "Ord.");
        descs.put(TIPO_DOCUMENTO_ORDINE_ACQUISTO, "Ord.");
        descs.put(TIPO_DOCUMENTO_SCONTRINO, "Scontr.");
        descs.put(TIPO_DOCUMENTO_PAGAMENTO_RICORRENTE, "Pag. ricorr.");
        try {
            return (String) descs.get(tipoDoc);
        } catch (Exception e) {
            e.printStackTrace();
            return tipoDoc;
        }
    }

    public static String getDescTipoDocBreve(String tipoDoc) {
        HashMap descs = new HashMap();
        descs.put(TIPO_DOCUMENTO_DDT, "DDT Ven.");
        descs.put(TIPO_DOCUMENTO_DDT_ACQUISTO, "DDT Acq.");
        descs.put(TIPO_DOCUMENTO_FATTURA, "Fatt. Ven.");
        descs.put(TIPO_DOCUMENTO_FATTURA_RICEVUTA, "Fatt. Acq.");
        descs.put(TIPO_DOCUMENTO_ORDINE, "Ord. Ven.");
        descs.put(TIPO_DOCUMENTO_ORDINE_ACQUISTO, "Ord. Acq.");
        descs.put(TIPO_DOCUMENTO_SCONTRINO, "Scontrino");
        descs.put(TIPO_DOCUMENTO_PAGAMENTO_RICORRENTE, "Pag. ricorrente");
        try {
            return (String) descs.get(tipoDoc);
        } catch (Exception e) {
            e.printStackTrace();
            return tipoDoc;
        }
    }

    public static String getNomeTabT(String tipoDoc) {
        HashMap val = new HashMap();
        val.put(TIPO_DOCUMENTO_DDT, "test_ddt");
        val.put(TIPO_DOCUMENTO_DDT_ACQUISTO, "test_ddt_acquisto");
        val.put(TIPO_DOCUMENTO_FATTURA, "test_fatt");
        val.put(TIPO_DOCUMENTO_FATTURA_RICEVUTA, "test_fatt_acquisto");
        val.put(TIPO_DOCUMENTO_ORDINE, "test_ordi");
        val.put(TIPO_DOCUMENTO_ORDINE_ACQUISTO, "test_ordi_acquisto");
        val.put(TIPO_DOCUMENTO_SCONTRINO, "test_fatt");
        //val.put(TIPO_DOCUMENTO_PAGAMENTO_RICORRENTE, "Pagamento ricorrente");
        try {
            return (String) val.get(tipoDoc);
        } catch (Exception e) {
            e.printStackTrace();
            return tipoDoc;
        }
    }

    public static String getNomeTabR(String tipoDoc) {
        HashMap val = new HashMap();
        val.put(TIPO_DOCUMENTO_DDT, "righ_ddt");
        val.put(TIPO_DOCUMENTO_DDT_ACQUISTO, "righ_ddt_acquisto");
        val.put(TIPO_DOCUMENTO_FATTURA, "righ_fatt");
        val.put(TIPO_DOCUMENTO_FATTURA_RICEVUTA, "righ_fatt_acquisto");
        val.put(TIPO_DOCUMENTO_ORDINE, "righ_ordi");
        val.put(TIPO_DOCUMENTO_ORDINE_ACQUISTO, "righ_ordi_acquisto");
        //val.put(TIPO_DOCUMENTO_SCONTRINO, "righ_fatt");
        //val.put(TIPO_DOCUMENTO_PAGAMENTO_RICORRENTE, "Pagamento ricorrente");
        try {
            return (String) val.get(tipoDoc);
        } catch (Exception e) {
            e.printStackTrace();
            return tipoDoc;
        }
    }
    
    public static String getTipoDocDaNomeTabT(String nome_tab_testate) {
        HashMap val = new HashMap();
        val.put("test_ddt", TIPO_DOCUMENTO_DDT);
        val.put("test_ddt_acquisto", TIPO_DOCUMENTO_DDT_ACQUISTO);
        val.put("test_fatt", TIPO_DOCUMENTO_FATTURA);
        val.put("test_fatt_acquisto", TIPO_DOCUMENTO_FATTURA_RICEVUTA);
        val.put("test_ordi", TIPO_DOCUMENTO_ORDINE);
        val.put("test_ordi_acquisto", TIPO_DOCUMENTO_ORDINE_ACQUISTO);
        //val.put(TIPO_DOCUMENTO_SCONTRINO, "righ_fatt");
        //val.put(TIPO_DOCUMENTO_PAGAMENTO_RICORRENTE, "Pagamento ricorrente");
        try {
            return (String) val.get(nome_tab_testate);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getNomeTabRdaTabT(String nome_tab_t) {
        return "righ_" + StringUtils.substringAfter(nome_tab_t, "_");
    }

    static public Connection getConnection() throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        String params = "&jdbcCompliantTruncation=false&zeroDateTimeBehavior=round&emptyStringsConvertToZero=true&autoReconnect=true&sessionVariables=sql_mode=''&allowMultiQueries=true&useCompression=false";

        //test per memory leak
        //params += "&dontTrackOpenResources=true";
        params += "&dontTrackOpenResources=false";

        params += main.fileIni.getValue("debug", "mysql_params");
        if (params.indexOf("connectTimeout") < 0) {
//            params += "&connectTimeout=10000";
            params += "&connectTimeout=20000";
        }
        if (params.indexOf("socketTimeout") < 0) {
//            params += "&socketTimeout=20000";
            params += "&socketTimeout=40000";
        }

//        if (main.debug || main.fileIni.getValueBoolean("db", "ssh", false)) {
        if (main.debug) {
//            params += "&profileSQL=true&traceProtocol=false";
        }

        String url = "jdbc:mysql://" + dbServ + "/" + dbNameDB + "?user=" + dbName + "&password=" + dbPass + params;
        if (useNamedPipes) {
            url += "&socketFactory=com.mysql.jdbc.NamedPipeSocketFactory";
        } else {
            if (main.debug) {
//                url += "&socketFactory=it.tnx.proto.mysql.InvoicexMysqlSocketFactory";
            }
        }

        if (useNamedPipes) {
            System.out.println("url:" + "jdbc:mysql://" + dbServ + "/" + dbNameDB + "?user=" + dbName + "&password=..." + params + "&socketFactory=com.mysql.jdbc.NamedPipeSocketFactory");
        } else {
            if (main.debug) {
                System.out.println("url:" + "jdbc:mysql://" + dbServ + "/" + dbNameDB + "?user=" + dbName + "&password=..." + params + "&socketFactory=it.tnx.proto.mysql.InvoicexMysqlSocketFactory");
            } else {
                System.out.println("url:" + "jdbc:mysql://" + dbServ + "/" + dbNameDB + "?user=" + dbName + "&password=..." + params);
            }
        }

        if (main.fileIni.getValueBoolean("db", "ssl", false)) {
            System.err.println("db usa ssl");
            //System.setProperty("javax.net.ssl.keyStore", main.fileIni.getValue("db", "ssl_keystore"));
            if (!main.fileIni.getValue("db", "ssl_truststore", "").trim().equalsIgnoreCase("")) {
                System.setProperty("javax.net.ssl.trustStore", main.fileIni.getValue("db", "ssl_truststore"));
                //System.setProperty("javax.net.ssl.keyStorePassword","");
                //System.setProperty("javax.net.ssl.trustStorePassword","");
            } else {
                File ftest = new File(main.wd + "ssl_truststore");
                if (ftest.exists()) {
                    System.setProperty("javax.net.ssl.trustStore", ftest.getAbsolutePath());
                }
            }
            System.err.println("javax.net.ssl.trustStore: " + System.getProperty("javax.net.ssl.trustStore"));
            url += "&useSSL=true&requireSSL=true";
        } else {
            System.err.println("db no ssl");
        }

        if (main.debug) {
            System.err.println("url:" + url);
        }

        Connection lconn = DriverManager.getConnection(url, dbName, dbPass);
        conns.add(lconn);

        return lconn;
    }

    private void chiudiTuttiResultSet() {
        Iterator<Entry<ResultSet, Long>> iter1 = resultsets.entrySet().iterator();
        while (iter1.hasNext()) {
            Entry<ResultSet, Long> e1 = iter1.next();
            ResultSet r = e1.getKey();
            try {
//                if (debug) {
//                    System.out.println("*** chiudo r:" + r);
//                }
                r.getStatement().close();
            } catch (Exception e) {
                System.err.println(this + " [r.getStatement().close()] " + e.toString());
            }
            try {
                r.close();
            } catch (Exception e) {
                System.err.println(this + " [r.close()]" + e.toString());
            }
            r = null;
            iter1.remove();
        }
    }

    public static Object getCurrentTimestamp() {
        try {
            return DbUtils.getObject(Db.getConn(), "select CURRENT_TIMESTAMP");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
