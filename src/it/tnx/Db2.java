//java.text.DateFormat dateFormat = new java.text.DateFormat("yyyy-mm-dd");
//sql += Db.pc(dateFormat.format(java.util.Calendar.getInstance().getTime()), Types.DATE);
package it.tnx;

import java.sql.*;
import javax.swing.JOptionPane;
import java.text.*;

import it.tnx.gui.*;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import sun.misc.HexDumpEncoder;

public class Db2 {

    public static String dbServ = "localhost";
    public static int dbPort = 3306;
    public static String dbPass = "";
    public static String dbName = "root";
    public static String dbNameDB = "GestioneFatture_tnx";
    public static Connection conn;
    public static Statement stat;
    public static String TIPO_DOCUMENTO_DDT = "DD";
    public static String TIPO_DOCUMENTO_FATTURA = "FA";

    public Db2() {
        System.err.println("new " + getClass() + ": " + this);
    }

    public boolean dbConnect() {
        try {

            Class.forName("org.gjt.mm.mysql.Driver").newInstance();
            String url = "jdbc:mysql://" + dbServ + "/" + dbNameDB + "?user=" + dbName + "&password=" + dbPass;

            //debug
            //javax.swing.JOptionPane.showMessageDialog(null,"Db URL:" + url);
            //System.out.println("url db:" + url);

            conn = DriverManager.getConnection(url, dbName, dbPass);
            stat = conn.createStatement();
            /* prova con interbase
            java.sql.Driver d = null;
            java.sql.Connection c = null;
            java.sql.Statement s = null;
            java.sql.ResultSet rs = null;
            String databaseURL = "jdbc:interbase://linux/c:/programmi/borland/interbase/gf.gdb";
            String user = "sysdba";
            String password = "masterkey";
            String driverName = "interbase.interclient.Driver";
            Class.forName ("interbase.interclient.Driver");
            java.sql.DriverManager.registerDriver (
            (java.sql.Driver) Class.forName ("interbase.interclient.Driver").newInstance ()
            );
            java.util.Properties sysProps = System.getProperties ();
            StringBuffer drivers = new StringBuffer ("interbase.interclient.Driver");
            String oldDrivers = sysProps.getProperty ("jdbc.drivers");
            if (oldDrivers != null)
            drivers.append (":" + oldDrivers);
            sysProps.put ("jdbc.drivers", drivers.toString ());
            System.setProperties (sysProps);
            d = new interbase.interclient.Driver ();
            try {
            // We pass the entire database URL, but we could just pass "jdbc:interbase:"
            d = java.sql.DriverManager.getDriver (databaseURL);
            System.out.println ("InterClient version " +
            d.getMajorVersion () +
            "." +
            d.getMinorVersion () +
            " registered with driver manager.");
            } catch (Exception err) {
            err.printStackTrace();
            }
            c = java.sql.DriverManager.getConnection (databaseURL, user, password);
            System.out.println ("Connection established.");
            conn = java.sql.DriverManager.getConnection (databaseURL, user, password);
            stat = conn.createStatement();
             */

            return (true);
        } catch (Exception e) {
            String msg = "Impossibile stabilire la connessione al server '";
            if (this.dbPort == 0) {
                msg = msg + this.dbServ + ":3306" + "'\n";
            } else {
                msg = msg + this.dbServ + ":" + this.dbPort + "'\n";
            }
            msg = msg + "Controllare il Server di database o di essere collegati alla Rete" + '\n';
            msg = msg + "[Errore:" + spezza(String.valueOf(e), 65, " _") + "]";
            javax.swing.JOptionPane.showMessageDialog(null, msg);
            return (false);
        }
    }

    public boolean dbConnectMysql5() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String url = "jdbc:mysql://" + dbServ + "/" + dbNameDB + "?user=" + dbName + "&password=" + dbPass + "&zeroDateTimeBehavior=convertToNull&jdbcCompliantTruncation=false";
            conn = DriverManager.getConnection(url, dbName, dbPass);
            stat = conn.createStatement();

            Statement statTmp = conn.createStatement();
            statTmp.execute("SET @@session.sql_mode = 'MYSQL323';");
            statTmp.close();

            Statement statTmp2 = conn.createStatement();
            ResultSet r2 = statTmp2.executeQuery("SELECT @@global.sql_mode;");
            if (r2.next()) {
                System.out.println("r2:" + r2.getString(1));
            }
            statTmp2.close();

            Statement statTmp3 = conn.createStatement();
            ResultSet r3 = statTmp3.executeQuery("SELECT @@session.sql_mode;");
            if (r3.next()) {
                System.out.println("r3:" + r3.getString(1));
            }
            statTmp3.close();


            return (true);
        } catch (Exception e) {
            String msg = "Impossibile stabilire la connessione al server '";
            if (this.dbPort == 0) {
                msg = msg + this.dbServ + ":3306" + "'\n";
            } else {
                msg = msg + this.dbServ + ":" + this.dbPort + "'\n";
            }
            msg = msg + "Controllare il Server di database o di essere collegati alla Rete" + '\n';
            msg = msg + "[Errore:" + spezza(String.valueOf(e), 65, " _") + "]";
            javax.swing.JOptionPane.showMessageDialog(null, msg);
            return (false);
        }
    }

    public static void close() {
        try {
            Db2.conn.close();
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public Connection dbGetConnection() {
        return (conn);
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
    private final static byte[] HEX_DIGITS = new byte[]{(byte) '0',
        (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5',
        (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) 'A',
        (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F'};

    public static String aa(String stringa) {
        //aggiunge apice al singolo
        if (stringa != null) {
            if (stringa.length() > 0) {
//                return (replaceChars(stringa,'\'',"''"));
                String out = stringa;
                out = out.replaceAll("\0", "\\\\0");
                out = out.replaceAll("\\\\", "\\\\\\\\");
                out = out.replaceAll("\'", "\\\\'");
                out = out.replaceAll("\"", "\\\\\"");
                out = out.replaceAll("\r", "\\\\r");
                out = out.replaceAll("\n", "\\\\n");
                return out;
            }
        }
        return ("");
    }

    public static String aax(String stringa) {
        //aggiunge apice al singolo
        if (stringa != null) {
            if (stringa.length() > 0) {

                byte[] x = stringa.getBytes();
                ByteArrayOutputStream bOut = new ByteArrayOutputStream((x.length * 2) + 3);
                bOut.write('x');
                bOut.write('\'');

                for (int i = 0; i < x.length; i++) {
                    int lowBits = (x[i] & 0xff) / 16;
                    int highBits = (x[i] & 0xff) % 16;

                    bOut.write(HEX_DIGITS[lowBits]);
                    bOut.write(HEX_DIGITS[highBits]);
                }

                bOut.write('\'');

                return bOut.toString();
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
        if (tipoCampo == java.sql.Types.BIGINT
                || tipoCampo == Types.DECIMAL
                || tipoCampo == Types.DOUBLE
                || tipoCampo == Types.FLOAT
                || tipoCampo == Types.INTEGER
                || tipoCampo == Types.REAL
                || tipoCampo == Types.SMALLINT
                || tipoCampo == Types.TINYINT) {
            if (campo == null || campo.toString().length() == 0) {
                return ("null");
            } else {
                return ("(" + campo + ")");
            }
        } else if (tipoCampo == Types.CHAR
                || tipoCampo == Types.LONGVARCHAR
                || tipoCampo == Types.VARCHAR) {
            return ("'" + aa((String) campo) + "'");
        } else if (tipoCampo == Types.DATE) {
            if (campo == null || campo.toString().length() == 0) {
                return ("null");
            } else {
                return ("'" + campo + "'");
            }
        } else if (tipoCampo == Types.LONGVARBINARY) {
            if (campo == null) {
                return ("null");
            } else {
                try {
                    byte[] bytes = (byte[]) campo;
                    if (bytes.length == 0) return "null";
                    return ("0x" + bytesToHex(bytes));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return ("null");
            }
        } else if (tipoCampo == Types.TIMESTAMP) {
            return ("'" + aa(nz(campo, "").toString()) + "'");
        } else if (tipoCampo == Types.BIT) {
            return ("'" + aa(nz(campo, "").toString()) + "'");
        } else {
//            System.out.println("else ??? " + tipoCampo);
            return ("'" + aa(nz(campo, "").toString()) + "'");
        }
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String h = Integer.toHexString(bytes[i] & 0xFF);
            if (h.length() == 1) h = "0" + h;
            buffer.append(h);
        }
        return buffer.toString().toUpperCase();
    }
    
    public static byte[] hexToByte(String hex) {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        for (int i = 2; i < hex.length(); i++) {
            String hexp = hex.substring(i, i+2);
            int b = Integer.decode("0x" + hexp);
            bout.write(b);
            i++;
        }
        try {
            return bout.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
//                err.printStackTrace();
                return ("null");
            }
        } else {
            return ("'" + aa(campo.toString()) + "'");
        }
    }

    public static String pc2(java.util.Date campo, int tipoCampo) {
        if (tipoCampo == Types.DATE) {
            DateFormat myFormat = new SimpleDateFormat("dd/MM/yy");
            DateFormat myFormatSql = new SimpleDateFormat("yyyy-MM-dd");
            myFormat.setLenient(false);
            try {
                return "'" + myFormatSql.format(campo) + "'";
            } catch (Exception err) {
                System.out.println("errore in campo: " + campo);
                err.printStackTrace();
                return ("0");
            }
        } else if (tipoCampo == Types.TIME) {
            DateFormat myFormatSql = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            try {
                return "'" + myFormatSql.format(campo) + "'";
            } catch (Exception err) {
                System.out.println("errore in campo: " + campo);
                err.printStackTrace();
                return ("0");
            }
        } else {
            return ("'" + aa(campo.toString()) + "'");
        }
    }

    public static String pc3(java.util.Date campo, int tipoCampo) {
        if (tipoCampo == Types.DATE) {
            DateFormat myFormat = new SimpleDateFormat("dd/MM/yy");
            DateFormat myFormatSql = new SimpleDateFormat("yyyy-MM-dd");
            myFormat.setLenient(false);
            try {
                return "'" + myFormatSql.format(campo) + "'";
            } catch (Exception err) {
                System.out.println("errore in campo: " + campo + " err:" + err);
                return ("null");
            }
        } else if (tipoCampo == Types.TIME) {
            DateFormat myFormatSql = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            try {
                return "'" + myFormatSql.format(campo) + "'";
            } catch (Exception err) {
                System.out.println("errore in campo: " + campo + " err:" + err);
                return ("null");
            }
        } else {
            return ("'" + aa(campo.toString()) + "'");
        }
    }

    public static String pcx(Object campo, int tipoCampo) {
        //prepara il campo per sql
        //debug
        //javax.swing.JOptionPane.showMessageDialog(null,"campo:"+campo+" tipo:"+tipoCampo);
        if (tipoCampo == java.sql.Types.BIGINT
                || tipoCampo == Types.DECIMAL
                || tipoCampo == Types.DOUBLE
                || tipoCampo == Types.FLOAT
                || tipoCampo == Types.INTEGER
                || tipoCampo == Types.REAL
                || tipoCampo == Types.SMALLINT
                || tipoCampo == Types.TINYINT) {
            if (campo == null || campo.toString().length() == 0) {
                return ("null");
            } else {
                return ("(" + campo + ")");
            }
        } else if (tipoCampo == Types.CHAR
                || tipoCampo == Types.LONGVARCHAR
                || tipoCampo == Types.VARCHAR) {
            if (campo == null || campo.toString().length() == 0) {
                return "null";
            } else {
                return (aax((String) campo));
            }
        } else if (tipoCampo == Types.DATE || tipoCampo == Types.TIMESTAMP) {
            if (campo == null || campo.toString().length() == 0) {
                return ("null");
            } else {
                return ("'" + campo + "'");
            }
        } else {
            if (campo == null || campo.toString().length() == 0) {
                return "null";
            } else {
                return (aax(nz(campo, "")));
            }
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

    public static String nz(String valore) {
        if (valore == null) {
            return ("");
        }
        return (valore);
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
        /*
        StringTokenizer tokenizer = new StringTokenizer(object,toFind);
        String temp = "";
        while (tokenizer.hasMoreTokens()) {
        temp += tokenizer.nextToken() + toSubstitute;
        System.out.println("tokenizer:" + temp);
        }
        return(temp);
         */
        return (object.replaceAll("\\" + toFind, toSubstitute));
    }

    public static ResultSet openResultSet(String sql) {
        Statement stat;
        ResultSet resu;
        //apre il resultset per ultimo +1
        try {
            stat = Db2.conn.createStatement();
            resu = stat.executeQuery(sql);
            return (resu);
        } catch (Exception err) {
            err.printStackTrace();
            //javax.swing.JOptionPane.showMessageDialog(null,err.toString());
            System.out.println("sql di errore:" + sql);
            return (null);
        }
    }

    public static ResultSet lookUp(String valoreChiave, String campoChiave, String tabella) {
        Statement stat;
        ResultSet resu;
        //apre il resultset per ultimo +1
        String sql = "";
        sql = "select * from " + tabella + " where " + campoChiave + " = " + Db2.pc(valoreChiave, "VARCHAR");
        try {
            stat = Db2.conn.createStatement();
            resu = stat.executeQuery(sql);
            resu.next();
            return (resu);
        } catch (Exception err) {
            err.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, err.toString());
            return (null);
        }
    }

    public static String lookUp(String valoreChiave, String campoChiave, String tabella, String campoDescrizione) {
        Statement stat;
        ResultSet resu;
        //apre il resultset per ultimo +1
        String sql = "";
        sql = "select * from " + tabella + " where " + campoChiave + " = " + Db2.pc(valoreChiave, "VARCHAR");
        try {
            stat = Db2.conn.createStatement();
            resu = stat.executeQuery(sql);
            if (resu.next()) {
                return (resu.getString(campoDescrizione));
            } else {
                System.out.println("lookUp:noRecord:sql=" + sql);
                return "";
            }
        } catch (Exception err) {
            err.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, err.toString());
            return ("");
        }
    }

    public static String lookUp2(String valoreChiave1, String campoChiave1, String valoreChiave2, String campoChiave2, String tabella, String campoDescrizione) {
        Statement stat;
        ResultSet resu;
        //apre il resultset per ultimo +1
        String sql = "";
        sql = "select * from " + tabella + " where " + campoChiave1 + " = " + Db2.pc(valoreChiave1, "VARCHAR") + " and " + campoChiave2 + " = " + Db2.pc(valoreChiave2, "VARCHAR");
        try {
            stat = Db2.conn.createStatement();
            resu = stat.executeQuery(sql);
            if (resu.next()) {
                return (resu.getString(campoDescrizione));
            } else {
                System.out.println("lookUp:noRecord:sql=" + sql);
                return "";
            }
        } catch (Exception err) {
            err.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, err.toString());
            return ("");
        }
    }

    public static boolean executeSql(String sql) {
        Statement stat;

        //lancia la query
        try {
            stat = Db2.conn.createStatement();
            stat.execute(sql);
            //System.out.println("sql debug:" + sql);
            return (true);
        } catch (Exception err) {
            err.printStackTrace();
            //javax.swing.JOptionPane.showMessageDialog(null,err.toString());
            System.out.println("sql di errore:" + sql);
            return (false);
        }
    }

    public static boolean executeSql(String sql, boolean printErrors) {
        Statement stat;

        //lancia la query
        try {
            stat = Db2.conn.createStatement();
            stat.execute(sql);
            System.out.println("sql debug:" + sql);
            return (true);
        } catch (Exception err) {
            if (printErrors == true) {
                err.printStackTrace();
                //javax.swing.JOptionPane.showMessageDialog(null,err.toString());
                System.out.println("sql di errore:" + sql);
            }
            return (false);
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
            numero = Db2.replaceChars(numero, '.', "");
            numero = numero.replace(',', '.');
            return (Double.valueOf(numero).doubleValue());
        } catch (Exception err) {
            System.out.println("!!! warning getDouble:" + numero);
            //err.printStackTrace();
            return (0.0);
        }
    }

    static public String formatDecimal(double valore) {
        DecimalFormat form = new DecimalFormat("#,##0.00");
        return (form.format(valore));
    }

    static public String formatDecimal5(double valore) {
        DecimalFormat form = new DecimalFormat("#,##0.00###");
        return (form.format(valore));
    }

    static public String formatNumero(double valore) {
        DecimalFormat form = new DecimalFormat("#,##0.##");
        if (form.format(valore).equalsIgnoreCase("0")) {
            return ("");
        } else {
            return (form.format(valore));
        }
    }

    static public String formatValuta(double valore) {
        DecimalFormat form = new DecimalFormat("#,##0.00");
        if (form.format(valore).equalsIgnoreCase("0")) {
            return ("");
        } else {
            return (form.format(valore));
        }
    }

    static public String formatNumero0(double valore) {
        DecimalFormat form = new DecimalFormat("#,##0");
        if (form.format(valore).equalsIgnoreCase("0")) {
            return ("0");
        } else {
            return (form.format(valore));
        }
    }

    static public String formatDataItalian(java.util.Date data) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        if (data != null) {
            return sdf.format(data);
        } else {
            return "";
        }
    }

    static public String formatData(String dataMysql) {
        return (dataMysql.substring(8, 10) + "/" + dataMysql.substring(5, 7) + "/" + dataMysql.substring(0, 4));
    }

    static public String formatDataY2(String dataMysql) {
        return (dataMysql.substring(8, 10) + "/" + dataMysql.substring(5, 7) + "/" + dataMysql.substring(2, 4));
    }

    static public String formatDataMysql(java.util.Date data) {
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
        return (dateFormat.format(data));
    }

    static public String formatDataMysqlFromItalian(String data) {
        if (data.length() == 10) {
            return data.substring(6, 10) + "-" + data.substring(3, 5) + "-" + data.substring(0, 2);
        } else {
            return "";
        }
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
            Db2 db = new Db2();
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
            stat = Db2.conn.createStatement();
            resu = stat.executeQuery(sql);
            resu.next();
            return (resu.getString(1));
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
            stat = Db2.conn.createStatement();
            resu = stat.executeQuery(sql);
            resu.next();
            String tempData = resu.getString(1);
            String anno = tempData.substring(2, 4);
            String mese = tempData.substring(5, 7);
            String giorno = tempData.substring(8, 10);
            String hh = tempData.substring(11, 13);
            String mm = tempData.substring(14, 16);
            String ss = tempData.substring(17, 19);
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
            show = Db2.openResultSet("show columns from " + tableNameSource);
            sqlCreate = "create table " + tableNameDestination + " (\n";
            sqlPrimary = "primary key (";
            while (show.next()) {
                if (show.getString("Key").equals("PRI")) {
                    sqlPrimary += show.getString("Field") + ",";
                }
                sqlDefault = "";
                if (Db2.nz(show.getString("Default"), "").length() > 0) {
                    sqlDefault = "default " + show.getString("Default");
                }
                sqlNull = "NOT NULL";
                if (Db2.nz(show.getString("Null"), "").length() > 0) {
                    sqlNull = "NULL";
                }
                sqlCreate += show.getString("Field") + " " + show.getString("Type") + " " + sqlNull + " " + sqlDefault + ",\n";
            }
            //tolgo ultima virgola
            sqlPrimary = sqlPrimary.substring(0, sqlPrimary.length() - 1) + ")";
            //se si vuole inserisco campo username
            if (addUserField == true) {
                sqlCreate += "username varchar(50) not null,\n";
            }
            //creo sql finale di create
            sqlCreate = sqlCreate + sqlPrimary + ")";

            //debug
            System.out.println("sqlCreate:" + sqlCreate);

            //eseguo
            Db2.executeSql(sqlCreate);

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
            showS = Db2.openResultSet("show columns from " + tableNameSource);
            showD = Db2.openResultSet("show columns from " + tableNameDestination);
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
                if (!Db2.nz(showS.getString("Default"), "").equals(Db2.nz(showD.getString("Default"), ""))) {
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
            show = Db2.openResultSet("show columns from " + tableName);
            while (show.next()) {
                conta++;
            }
            show.first();
            while (show.next()) {
                conta2++;
                if (noLast == true) {
                    if (conta2 < conta - 1) {
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

    public static boolean contain(String tableName, String fieldName, int fieldType, String value) {
        String sql = "select " + fieldName;
        sql += " where fieldName = " + Db2.pc(value, fieldType);
        try {
            ResultSet r = Db2.openResultSet(sql);
            if (r.next()) {
                return true;
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        System.out.println("test escape");
        String in = aa("ciao \\ ciao");
        System.out.println("in:" + in);
        System.out.println("in:" + in.getBytes()[0] + " " + in.getBytes()[1] + " " + in.getBytes()[2] + " " + in.getBytes()[3] + " " + in.getBytes()[4] + " " + in.getBytes()[5]
                + " " + in.getBytes()[6] + " " + in.getBytes()[7] + " " + in.getBytes()[8] + " " + in.getBytes()[9] + " " + in.getBytes()[10]);
    }
}
