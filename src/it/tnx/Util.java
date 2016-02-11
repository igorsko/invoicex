/*
 * Util.java
 *
 * Created on 9 gennaio 2003, 23.43
 */
package it.tnx;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
// com.sun.image.codec.jpeg package is included in sun and ibm sdk 1.3
import com.sun.image.codec.jpeg.*;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.*;
import java.text.*;
import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author  marco
 */
public class Util {
    //special chars

    public static final String EURO = "\u20ac";

    //images
    public static final MediaTracker tracker = new MediaTracker(new Component() {
    });

    /** Creates a new instance of Util */
    public Util() {
    }

    //dates
    public static java.util.Date getCurrentDateTime() {
        Calendar cal = Calendar.getInstance(java.util.Locale.ITALY);
        return cal.getTime();
    }

    public static int getCurrenteYear() {
        Calendar cal = Calendar.getInstance(java.util.Locale.ITALY);
        return (cal.get(cal.YEAR));
    }

    public static int getCurrenteMonth() {
        Calendar cal = Calendar.getInstance(java.util.Locale.ITALY);
        return (cal.get(cal.MONTH) + 1);
    }

    public static java.util.Date getDateTime() {
        Calendar cal = Calendar.getInstance(java.util.Locale.ITALY);
        return cal.getTime();
    }

    public static java.util.Date getDateTime(String date) {
        DateFormat myFormat = new SimpleDateFormat("dd/MM/yy");
        myFormat.setLenient(false);
        try {
            java.util.Date myDate = myFormat.parse(date);
            return myDate;
        } catch (java.text.ParseException pe) {
            return null;
        }
    }

    public static java.util.Date getDateFromDDMMYY(String date) {
        DateFormat myFormat = new SimpleDateFormat("dd/MM/yy");
        myFormat.setLenient(false);
        try {
            java.util.Date myDate = myFormat.parse(date);
            return myDate;
        } catch (java.text.ParseException pe) {
            return null;
        }
    }

    public static java.util.Date getDateFromDDMMYYYY(String date) {
        DateFormat myFormat = new SimpleDateFormat("dd/MM/yyyy");
        myFormat.setLenient(false);
        try {
            java.util.Date myDate = myFormat.parse(date);
            return myDate;
        } catch (java.text.ParseException pe) {
            return null;
        }
    }

    public static String getDateTimeFormatYYYYMMDD_HHMM() {
        Calendar cal = Calendar.getInstance(java.util.Locale.ITALY);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd_hhmm");
        return sdf.format(cal.getTime());
    }

    public static String getYearString() {
        Calendar cal = Calendar.getInstance(java.util.Locale.ITALY);
        return String.valueOf(cal.get(cal.YEAR));
    }

    public static String getYearStringMenoUno() {
        Calendar cal = Calendar.getInstance(java.util.Locale.ITALY);
        return String.valueOf(cal.get(cal.YEAR) - 1);
    }

    public static String getDateStringITALIAN(int dateFormatStyle) {
        Calendar cal = Calendar.getInstance(java.util.Locale.ITALY);
        DateFormat df = DateFormat.getDateInstance(dateFormatStyle, Locale.ITALY);
        return df.format(cal.getTime());
    }

    public static String getDateStringITALIAN(java.util.Date data, int dateFormatStyle) {
        Calendar cal = Calendar.getInstance(java.util.Locale.ITALY);
        cal.setTime(data);
        DateFormat df = DateFormat.getDateInstance(dateFormatStyle, Locale.ITALY);
        return df.format(cal.getTime());
    }

    public static String getDateTimeStringITALIAN(int dateFormatStyle, int timeFormatStyle) {
        Calendar cal = Calendar.getInstance(java.util.Locale.ITALY);
        DateFormat df = DateFormat.getDateTimeInstance(dateFormatStyle, timeFormatStyle, Locale.ITALY);
        return df.format(cal.getTime());
    }

    public static String getDateTimeStringITALIAN(java.util.Date data, int dateFormatStyle, int timeFormatStyle) {
        Calendar cal = Calendar.getInstance(java.util.Locale.ITALY);
        cal.setTime(data);
        DateFormat df = DateFormat.getDateTimeInstance(dateFormatStyle, timeFormatStyle, Locale.ITALY);
        return df.format(cal.getTime());
    }
    //images

    /** Adjusts the size of the image to the given coordinates.
     * If width or height is -1, the image aspect ration is maintained.
     */
    public static Image setSize(Image image, int width, int height) {
        return setSize(image, width, height, java.awt.Image.SCALE_DEFAULT);
    } // setSize

    /** Adjusts the size of the image to the given coordinates.
     * If width or height is -1, the image aspect ration is maintained.
     * <p>
     * Hints are one of SCALE_DEFAULT, SCALE_FAST, SCALE_SMOOTH,
     * SCALE_REPLICATE, SCALE_AREA_AVERAGING as defined in java.awt.Image.
     */
    public static Image setSize(Image image, int width, int height, int hints) {
        return image.getScaledInstance(width, height, hints);
    } // setSize

    public static boolean createThumbnail(Image image, String newFileName, int newWidth) {
        //create a thumbnail with proportional scale
        File file;

        waitForImage(image);
        int larghezzaImmagine = image.getWidth(null);
        int altezzaImmagine = image.getHeight(null);

        int nuovaLarghezzaImmagine = newWidth;
        int nuovaAltezzaImmagine = nuovaLarghezzaImmagine * altezzaImmagine / larghezzaImmagine;
        Image is = image.getScaledInstance(nuovaLarghezzaImmagine, nuovaAltezzaImmagine, image.SCALE_DEFAULT);
        Util.waitForImage(is);

        // Write new size image to JPEG file.
        if (is.getWidth(null) > 0) {
            try {
                file = new File(Util.getPathFromFullFileName(newFileName, "/"));
                if (file.exists() == false) {
                    file.mkdirs();
                }
                file = new File(newFileName);
                if (file.exists()) {
                    file.delete();
                }
                FileOutputStream fos = new FileOutputStream(newFileName);
                encodeJPEG(fos, is, 65);
                fos.close();
            } catch (Exception err) {
                err.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /** Encodes the given image at the given quality to the output stream. */
    public static void encodeJPEG(OutputStream outputStream, Image outputImage, float outputQuality)
            throws java.io.IOException {
        int outputWidth = outputImage.getWidth(null);
        if (outputWidth < 1) {
            throw new IllegalArgumentException("output image width " + outputWidth + " is out of range");
        }
        int outputHeight = outputImage.getHeight(null);
        if (outputHeight < 1) {
            throw new IllegalArgumentException("output image height " + outputHeight + " is out of range");
        }

        // Get a buffered image from the image.
        BufferedImage bi = new BufferedImage(outputWidth, outputHeight,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D biContext = bi.createGraphics();
        biContext.drawImage(outputImage, 0, 0, null);
        // Note that additional drawing such as watermarks or logos can be placed here.
        //biContext.setFont(new Font("Monospaced", Font.BOLD, 10));
        //biContext.drawString(getDateTimeStringITALIAN(DateFormat.FULL, java.text.DateFormat.FULL), 10, 10);

        // com.sun.image.codec.jpeg package is included in sun and ibm sdk 1.3
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(outputStream);
        // The default quality is 0.75.
        JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(bi);
        jep.setQuality(outputQuality, true);
        encoder.encode(bi, jep);
        // encoder.encode( bi );
        outputStream.flush();
    } // encodeImage

    public static void waitForImage(Image image) {
        try {
            tracker.addImage(image, 0);
            tracker.waitForID(0);
            // loadStatus = tracker.statusID( 0, false );
            tracker.removeImage(image, 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    } // waitForImage

    //files
    public static String getFileNameFromFullFileName(String fileName, String separator) {
        int li = fileName.lastIndexOf(separator);
        return fileName.substring(li + 1);
    }

    public static String getPathFromFullFileName(String fileName, String separator) {
        int li = fileName.lastIndexOf(separator);
        return fileName.substring(0, li);
    }

    public static String getDirSeparator() {
        // Get all system properties
        return System.getProperty("file.separator");
    }

    // This method copies a given File into another one.
    // If the given File is a directory, it will recursively copy all subdirectories
    // and the files in them.
    public static boolean copyFile(File source, File dest) throws IOException {
        if (source.isDirectory()) {
            dest.mkdir();
            File[] filesInThisDir = source.listFiles();
            for (int i = 0; i < filesInThisDir.length; i++) {
                copyFile(filesInThisDir[i], new File(dest, filesInThisDir[i].getName()));
            }
        } else {
            InputStream istream = new FileInputStream(source);
            OutputStream ostream = new FileOutputStream(dest);
            dest.createNewFile();
            while (true) {
                int nextByte = istream.read();
                if (nextByte == -1) {
                    break;
                }
                ostream.write(nextByte);
            }
            istream.close();
            ostream.close();
        }
        return true;
    }

    public static String getTempDir() {
        return System.getProperty("java.io.tmpdir");
    }

    public static String getTempDir(boolean withFinalSlash) {
        System.out.println("TempDir:" + System.getProperty("java.io.tmpdir") + getDirSeparator());
        if (withFinalSlash == true) {
            return System.getProperty("java.io.tmpdir") + getDirSeparator();
        } else {
            return System.getProperty("java.io.tmpdir");
        }
    }

    public static String getUserDir() {
        return System.getProperty("user.home");
    }

    public static String getUserDir(boolean withFinalSlash) {
        System.out.println("UserDir:" + System.getProperty("user.home") + getDirSeparator());
        if (withFinalSlash == true) {
            return System.getProperty("user.home") + getDirSeparator();
        } else {
            return System.getProperty("user.home");
        }
    }

    //conversioni
    public static double toDouble(String value) {
        double d = 0;

        value = value.replaceAll(",", ".");
        try {
            d = Double.parseDouble(value);
        } catch (Exception err) {
        }

        return d;
    }

    public static String int2str(int value) {
        return String.valueOf(value);
    }

    //math //DA TESTARE
    public static double floor(double x, int decimals) {  // always rounds  down
        int factor = 1;
        for (int i = 0; i < Math.abs(decimals); i++) {
            factor *= 10;
        }
        if (decimals < 0) {
            return factor * Math.floor(x / factor);
        } else {
            return Math.floor(factor * x) / factor;
        }
    }

    public static double ceil(double x, int decimals) {  // always rounds up
        int factor = 1;
        for (int i = 0; i < Math.abs(decimals); i++) {
            factor *= 10;
        }
        if (decimals < 0) {
            return factor * Math.ceil(x / factor);
        } else {
            return Math.ceil(factor * x) / factor;
        }
    }

    public static double rint(double x, int decimals) {  // rounds to the  nearest integer
        int factor = 1;
        for (int i = 0; i < Math.abs(decimals); i++) {
            factor *= 10;
        }
        if (decimals < 0) {
            return factor * Math.rint(x / factor);
        } else {
            return Math.rint(factor * x) / factor;
        }
    }

    public static double round(double x, int decimals) {  // rounds to the  nearest integer
        int factor = 1;
        x = Double.parseDouble(String.format(Locale.ENGLISH, "%f", x));
        for (int i = 0; i < Math.abs(decimals); i++) {
            factor *= 10;
        }
        if (decimals < 0) {
            x = factor * Math.round(x / factor);
        } else {
            x = (double) Math.round(factor * x) / (double) factor;
        }
        return x;
    }

    public static double round2(double x, int decimals) {  // rounds to the  nearest integer
        //questo caso non funzionava:
        //34,62*1.04=36.0048 che si arrotonda a 36.00 e non a 36.01

        int factor = 1;
        decimals += 1;
        for (int i = 0; i < Math.abs(decimals); i++) {
            factor *= 10;
        }
        if (decimals < 0) {
            x = factor * Math.round(x / factor);
        } else {
            x = (double) Math.round(factor * x) / (double) factor;
        }

        decimals -= 1;
        factor = 1;
        for (int i = 0; i < Math.abs(decimals); i++) {
            factor *= 10;
        }
        if (decimals < 0) {
            return factor * Math.round(x / factor);
        } else {
            return (double) Math.round(factor * x) / (double) factor;
        }
    }

    public static double round3(double x, int decimals) {  // rounds to the  nearest integer
        //questo caso non funzionava:
        //34,62*1.04=36.0048 che si arrotonda a 36.00 e non a 36.01

        int factor = 1;
        decimals += 2;
        for (int i = 0; i < Math.abs(decimals); i++) {
            factor *= 10;
        }
        if (decimals < 0) {
            x = factor * Math.round(x / factor);
        } else {
            x = (double) Math.round(factor * x) / (double) factor;
        }

        decimals -= 2;
        factor = 1;
        for (int i = 0; i < Math.abs(decimals); i++) {
            factor *= 10;
        }
        if (decimals < 0) {
            return factor * Math.round(x / factor);
        } else {
            return (double) Math.round(factor * x) / (double) factor;
        }
    }

    //utils
    public static java.util.Vector getVectorFromArray(Object[] obj) {
        if (obj == null) {
            return null;
        }
        if (obj.length == 0) {
            return null;
        }
        java.util.Vector temp = new java.util.Vector();
        for (int i = 0; i < obj.length; i++) {
            temp.add(obj[i]);
        }
        return temp;
    }

    //systema

    public static void openUrlInBrowser(URL url) {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.startsWith("mac os")) {
            try {
                System.out.println("openUrlInBrowser:mac os(" + osName + "):" + url);
                Runtime.getRuntime().exec("open " + url.toString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            try {
                org.jdesktop.jdic.desktop.Desktop.browse(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //formattazione stringhe per output
    static public String format2Decimali(double valore) {
        NumberFormat form = DecimalFormat.getInstance(Locale.ITALIAN);
        form.setMaximumFractionDigits(2);
        form.setMinimumFractionDigits(2);

        System.out.println("Importo: " + form.format(round(valore, 2)));
        return (form.format(round(valore, 2)));
    }

    static public String format5Decimali(double valore) {
        NumberFormat form = DecimalFormat.getInstance(Locale.ITALIAN);
        form.setMaximumFractionDigits(5);
        form.setMinimumFractionDigits(5);
        return (form.format(round(valore, 5)));
    }

    static public String formatNumero2Decimali(double valore) {
        NumberFormat form = DecimalFormat.getInstance(Locale.ITALIAN);
        form.setMaximumFractionDigits(2);
        form.setMinimumFractionDigits(0);
        if (form.format(valore).equalsIgnoreCase("0")) {
            return ("");
        } else {
            return (form.format(round(valore, 2)));
        }
    }

    static public String formatNumero5Decimali(double valore) {
        NumberFormat form = DecimalFormat.getInstance(Locale.ITALIAN);
        form.setMaximumFractionDigits(5);
        form.setMinimumFractionDigits(0);
        if (form.format(valore).equalsIgnoreCase("0")) {
            return ("");
        } else {
            return (form.format(round(valore, 5)));
        }
    }

    static public String formatValutaEuro(double valore) {
        NumberFormat form = DecimalFormat.getInstance(Locale.ITALIAN);
        form.setMaximumFractionDigits(2);
        form.setMinimumFractionDigits(2);
        if (form.format(round(valore, 2)).equalsIgnoreCase("0")) {
            return ("");
        } else {
            //return("\u20aC " + form.format(valore));
            return (form.format(round(valore, 2)));
        }
    }

    static public String formatNumero0Decimali(double valore) {
        valore = round(valore, 0);
        NumberFormat form = DecimalFormat.getInstance(Locale.ITALIAN);
        form.setMaximumFractionDigits(0);
        form.setMinimumFractionDigits(0);
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

    static public String formatDataTempoMysql(java.util.Date data) {
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return (dateFormat.format(data));
    }

    static public String formatDataMysqlFromItalian(String data) {
        if (data.length() == 10) {
            return data.substring(6, 10) + "-" + data.substring(3, 5) + "-" + data.substring(0, 2);
        } else if (data.length() == 8) {
            return data.substring(6, 8) + "-" + data.substring(3, 5) + "-" + data.substring(0, 2);
        } else {
            return "";
        }
    }

    //string to number conversion
    static public double getDouble(String numero) {
        //ritorna un double da una stringa con la virgola invece che il punto come separatore
        if (numero == null) return 0d;
        if (numero.equals("")) {
            return 0.0;
        }
        try {
            numero = Db2.replaceChars(numero, '.', "");
            numero = numero.replace(',', '.');
            return (Double.valueOf(numero).doubleValue());
        } catch (Exception err) {
            //System.out.println("!!! warning getDouble:" + numero);
            return (0.0);
        }
    }

    static public double getDouble(Object value) {
        //ritorna un double da una stringa con la virgola invece che il punto come separatore
        if (value == null) {
            return 0;
        }
        if (value instanceof Double) {
            return ((Double) value).doubleValue();
        } else {
            String numero = String.valueOf(value);
            if (numero.equals("")) {
                return 0.0;
            }
            try {
                numero = Db2.replaceChars(numero, '.', "");
                numero = numero.replace(',', '.');
                return (Double.valueOf(numero).doubleValue());
            } catch (Exception err) {
                return (0.0);
            }
        }
    }

    static public double getDoubleEng(String numero) {
        //ritorna un double da una stringa con la virgola invece che il punto come separatore
        if (numero.equals("")) {
            return 0.0;
        }
        try {
            return (Double.valueOf(numero).doubleValue());
        } catch (Exception err) {
            //System.out.println("!!! warning getDouble:" + numero);
            return (0.0);
        }
    }

    static public int getInt(String numero) {
        //ritorna un int da una stringa
        try {
            numero = Db2.replaceChars(numero, '.', "");
            numero = numero.replace(',', '.');
            return (Integer.valueOf(numero).intValue());
        } catch (Exception err) {
            System.out.println("!!! warning getInt:" + numero);
            return (0);
        }
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

    public static String nz(Object valore) {
        if (valore == null) {
            return ("");
        }
        return (valore.toString());
    }

    public static String nz(Object valore, String seNullo) {
        if (valore == null) {
            return (seNullo);
        }
        return (valore.toString());
    }

    //database utilities
    static public boolean dumpTable(String tableName, java.sql.Connection dbConnection, java.io.OutputStream outputStream) {
        return dumpTable(tableName, dbConnection, outputStream, null);
    }

    static public boolean dumpTable(String tableName, java.sql.Connection dbConnection, java.io.OutputStream outputStream, String tipo) {
        StringBuilder sb = new StringBuilder();
        int columns;
        String valoreCampo;

        Statement stat = null;
        ResultSet resu = null;
        int limit_da = 0;
        int limit_quanti = 100;
        boolean ancora = true;
        try {
            stat = dbConnection.createStatement();

            //PrintStream o = new PrintStream(outputStream);
            PrintStream o = new PrintStream(outputStream, true, "ISO-8859-1");
            
            if (tipo != null && tipo.equalsIgnoreCase("VIEW")) {
                o.println("drop view IF EXISTS " + tableName + ";");
            } else {
                o.println("drop table IF EXISTS " + tableName + ";");
            }
            o.println(getCreateTable(tableName, dbConnection) + ";");

            if (tipo == null || !tipo.equalsIgnoreCase("VIEW")) {            
                while (ancora) {
                    resu = stat.executeQuery("select * from " + tableName + " limit " + limit_da + ", " + limit_quanti);
                    ResultSetMetaData meta = resu.getMetaData();
                    columns = meta.getColumnCount();

                    //ciclo di inserimento delle righe
                    sb.setLength(0);
                    int conta = 0;
                    while (resu.next()) {
                        conta++;
                        sb.setLength(0);
                        sb.append("insert into " + tableName + " values (");
                        for (int i = 1; i <= columns; i++) {
                            valoreCampo = it.tnx.Db2.pc(resu.getObject(i), meta.getColumnType(i));
                            sb.append(valoreCampo);
                            if (i != columns) {
                                sb.append(",");
                            }
                        }
                        sb.append(");");
                        o.println(sb.toString());
                        //o.println(new String(sb.toString().getBytes(), "ISO-8859-1"));
                        o.flush();
                    }
                    if (conta == 0) {
                        ancora = false;
                    } else {
                        limit_da += limit_quanti;
                    }
                }
            }
            
            sb = null;

            return true;
        } catch (Exception err) {
            err.printStackTrace();
            return false;
        } finally {
            try {
                resu.close();
            } catch (Exception e) {
            }
            try {
                stat.close();
            } catch (Exception e) {
            }
        }
    }

    //database utilities
    static public boolean dumpTableOld(String tableName, java.sql.Connection dbConnection, java.io.OutputStream outputStream) {
        StringBuffer sb = null;
        int columns;
        String valoreCampo;

        Statement stat = null;
        ResultSet resu = null;
        try {
            stat = dbConnection.createStatement();
            resu = stat.executeQuery("select * from " + tableName);
            ResultSetMetaData meta = resu.getMetaData();
            columns = meta.getColumnCount();

            PrintStream o = new PrintStream(outputStream);
            //drop della tabella
            o.println("drop table IF EXISTS " + tableName + ";");
            //creazione della tabella
            o.println(getCreateTable(tableName, dbConnection) + ";");
            //ciclo di inserimento delle righe
            sb = new StringBuffer();
            int conta = 0;
            while (resu.next()) {
                conta++;
                sb.setLength(0);
                sb.append("insert into " + tableName + " values (");
                for (int i = 1; i <= columns; i++) {
                    valoreCampo = it.tnx.Db2.pc(resu.getObject(i), meta.getColumnType(i));
                    sb.append(valoreCampo);
                    if (i != columns) {
                        sb.append(",");
                    }
                }
                sb.append(");");
                o.println(sb.toString());
                o.flush();
            }
            sb = null;
            return true;
        } catch (Exception err) {
            err.printStackTrace();
            return false;
        } finally {
            try {
                resu.close();
            } catch (Exception e) {
            }
            try {
                stat.close();
            } catch (Exception e) {
            }
        }
    }

    public static String getCreateTable(String tableName, java.sql.Connection dbConnection) {
        java.sql.ResultSet show = null;
        String sqlCreate = "", sqlPrimary, sqlNull, sqlDefault, sqlExtra;
        int campiPK = 0;

        try {
            java.sql.Statement stat = dbConnection.createStatement();
//            show = stat.executeQuery("show columns from " + tableName);
//            sqlCreate = "create table " + tableName + " (\n";
//            sqlPrimary = "PRIMARY KEY (";
//            while (show.next()) {
//                if (show.getString("Key").equals("PRI")) {
//                    sqlPrimary += show.getString("Field") + ",";
//                    campiPK++;
//                }
//                sqlDefault = "";
//                if (Db.nz(show.getString("Default"),"").length() > 0) {
//                    if (show.getString("Default").indexOf("CURRENT") >= 0) {
//                        sqlDefault = "DEFAULT " + show.getString("Default") + "";
//                    } else {
//                        sqlDefault = "DEFAULT '" + show.getString("Default") + "'";
//                    }
//                }
//                sqlNull = "NOT NULL";
//                if (Db.nz(show.getString("Null"),"").length() > 0) sqlNull = "NULL";
//                sqlExtra = " " + show.getString("Extra");
//                sqlCreate += "   `" + show.getString("Field") + "` " + show.getString("Type") + " " + sqlNull + sqlExtra + " " + sqlDefault + ",\n";
//            }
//            //tolgo ultima virgola
//            sqlPrimary = sqlPrimary.substring(0, sqlPrimary.length()-1) + ")";
//            //creo sql finale di create
//            if (campiPK > 0) {
//                sqlCreate = sqlCreate + sqlPrimary + ")";
//            } else {
//                sqlCreate = sqlCreate.substring(0, sqlCreate.length() - 2) + ")";
//            }

            show = stat.executeQuery("SHOW CREATE TABLE " + tableName);
            if (show.next()) {
                sqlCreate = show.getString(2);
                sqlCreate = StringUtils.replace(sqlCreate, "CREATE TABLE ", "CREATE TABLE IF NOT EXISTS ");
            }
            try {
                show.getStatement().close();
                show.close();
            } catch (Exception e) {
            }
            return sqlCreate;
        } catch (Exception err) {
            err.printStackTrace();
        }

        return sqlCreate;
    }

    //jtree
    // If expand is true, expands all nodes in the tree.
    // Otherwise, collapses all nodes in the tree.
    static public void expandAll(JTree tree, boolean expand) {
        TreeNode root = (TreeNode) tree.getModel().getRoot();
        // Traverse tree from root
        expandAll(tree, new TreePath(root), expand);
    }

    static private void expandAll(JTree tree, TreePath parent, boolean expand) {
        // Traverse children
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration e = node.children(); e.hasMoreElements();) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path, expand);
            }
        }
        // Expansion or collapse must be done bottom-up
        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }
    }

    static public void dumpMem() {
        System.out.println(getMem());
    }

    static public String getMem() {
        double f = ((double) Runtime.getRuntime().freeMemory()) / 1024d / 1024d;
        double t = ((double) Runtime.getRuntime().totalMemory()) / 1024d / 1024d;
        double m = ((double) Runtime.getRuntime().maxMemory()) / 1024d / 1024d;
        double u = t - f;
        String out = "dumpMem: max: " + m + " tot:" + t + " free:" + f + " in use:" + u;
        return out;
    }
}
