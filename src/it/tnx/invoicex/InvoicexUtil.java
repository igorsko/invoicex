/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.tnx.invoicex;

import it.tnx.Db;
import gestioneFatture.InvoicexEvent;
import gestioneFatture.main;
import it.tnx.commons.CastUtils;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import com.Ostermiller.util.CSVParser;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import gestioneFatture.ArticoloHint;
import gestioneFatture.ClienteHint;
import gestioneFatture.GenericFrmTest;
import gestioneFatture.MenuPanel;
import gestioneFatture.SqlLineIterator;
import gestioneFatture.Util;
import gestioneFatture.dbFattura;
import gestioneFatture.frmElenDDT;
import gestioneFatture.frmElenFatt;
import gestioneFatture.frmElenOrdini;
import gestioneFatture.frmNuovRigaDescrizioneMultiRigaNew;
import gestioneFatture.iniFileProp;
import it.tnx.commons.DbUtils;
import it.tnx.commons.DebugFastUtils;
import it.tnx.commons.DebugUtils;
import it.tnx.commons.FormatUtils;
import it.tnx.commons.FxUtils;
import it.tnx.commons.MicroBench;
import it.tnx.commons.StringUtilsTnx;
import it.tnx.commons.SwingUtils;
import it.tnx.commons.cu;
import it.tnx.commons.swing.DelayedExecutor;
import it.tnx.invoicex.data.Giacenza;
import it.tnx.invoicex.gui.JDialogLotti;
import it.tnx.invoicex.gui.JFrameDb;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.lang.Double;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicReference;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignImage;
import net.sf.jasperreports.engine.design.JasperDesign;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jdesktop.swingworker.SwingWorker;
import org.jvnet.substance.SubstanceDefaultTableCellRenderer;
import org.mozilla.universalchardet.UniversalDetector;
import sas.swing.plaf.MultiLineLabelUI;
import tnxbeans.tnxComboField;
import tnxbeans.tnxDbGrid;
import tnxbeans.tnxDbPanel;
import tnxbeans.tnxTextField;

/**
 *
 * @author test1
 */
public class InvoicexUtil {

    static public Boolean substance = false;
    static public TableCellRenderer numberRenderer0_5 = null;
    static public TableCellRenderer numberRenderer0_5_0rosso = null;
    static public Integer tipoNumerazione = null;
    static public final int TIPO_NUMERAZIONE_ANNO = 0;
    static public final int TIPO_NUMERAZIONE_ANNO_2CIFRE = 1;
    static public final int TIPO_NUMERAZIONE_ANNO_SOLO_NUMERO = 2;
    static public final int TIPO_NUMERAZIONE_ANNO_INFINITA = 3;

    static {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                System.out.println("verify = " + hostname + " sessione:" + session);
                return true;
            }
        });
    }
    static String urlwsd1 = "https://secure.tnx.it/invoicex/index.php?p=wsd";
    static String urlwsd2 = "https://due.tnx.it/invoicex/wsd.inc.php?p=wsd";
    static String urlwsd_locale = "https://demo.tnx.it/invoicex/index.php?p=wsd";

    static public String getUrlWsd() {
        String dbserver = main.fileIni.getValue("db", "server");
        if (main.fileIni.getValue("db", "ssh_hostname", "").length() > 0) {
            dbserver = main.fileIni.getValue("db", "ssh_hostname", "");
        }
        if (dbserver.equals("linux")) {
            return urlwsd_locale;
        }
        if (dbserver.indexOf("due.") >= 0) {
            return urlwsd2;
        }
        return urlwsd1;
    }

    static public void fireEvent(Object source, int eventType, Object... args) {
        try {
            InvoicexEvent event = new InvoicexEvent(source);
            event.type = eventType;
            event.args = args;
            main.events.fireInvoicexEvent(event);
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    static public int generaProssimoNumeroDocumento(String tipo, int dbAnno, String dbSerie, boolean vendita) {
        try {
            String table = "test_fatt";
            if (tipo.equals("ordine")) {
                table = "test_ordi";
            }
            if (tipo.equals("ddt")) {
                table = "test_ddt";
            }
            if (vendita) {
                table += "_acquisto";
            }

            ResultSet rs = Db.openResultSet("select max(numero) as new from " + table + " where serie = '" + Db.aa(dbSerie) + "' and anno = '" + dbAnno + "'");
            BigInteger res = BigInteger.ZERO;
            if (rs.next()) {
                res = BigInteger.valueOf(rs.getLong("new"));
            }

            return res.intValue() + 1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    static public void importCSV(String tipoDoc, File f, String dbSerie, int dbNumero, int dbAnno, int dbIdPadre) throws SQLException {
        importCSV(tipoDoc, f, dbSerie, dbNumero, dbAnno, dbIdPadre, "FromFile");
    }

    static public void importCSV(String tipoDoc, File f, String dbSerie, int dbNumero, int dbAnno, int dbIdPadre, String nomeListino) throws SQLException {
        CSVParser cp;
        String nomeTabella = "";
        boolean ricarico = false;
        double percRicarico = 0.00;
        String listinoRicarico = "";

        if (tipoDoc.equals(Db.TIPO_DOCUMENTO_DDT)) {
            nomeTabella = "righ_ddt";
        } else if (tipoDoc.equals(Db.TIPO_DOCUMENTO_DDT_ACQUISTO)) {
            nomeTabella = "righ_ddt_acquisto";
        } else if (tipoDoc.equals(Db.TIPO_DOCUMENTO_FATTURA)) {
            nomeTabella = "righ_fatt";
        } else if (tipoDoc.equals(Db.TIPO_DOCUMENTO_FATTURA_RICEVUTA)) {
            nomeTabella = "righ_fatt_acquisto";
        } else if (tipoDoc.equals(Db.TIPO_DOCUMENTO_ORDINE)) {
            nomeTabella = "righ_ordi";
        } else if (tipoDoc.equals(Db.TIPO_DOCUMENTO_ORDINE_ACQUISTO)) {
            nomeTabella = "righ_ordi_acquisto";
        }

        if (!nomeListino.equals("FromFile")) {
            ResultSet rs = Db.openResultSet("SELECT ricarico_listino, ricarico_flag, ricarico_perc FROM tipi_listino WHERE codice = '" + nomeListino + "'");
            if (rs.next()) {
                String flag = rs.getString("ricarico_flag");

                if (flag != null && flag.equals("S")) {
                    ricarico = true;
                    percRicarico = rs.getDouble("ricarico_perc");
                    listinoRicarico = rs.getString("ricarico_listino");
                }
            }
        }

        int col = 0;
        String sql = "SELECT * FROM " + nomeTabella + " LIMIT 1";
        ResultSet righe = Db.openResultSet(sql);
        Vector field = new Vector();
        try {
            ResultSetMetaData rsmd = righe.getMetaData();
            col = rsmd.getColumnCount();

            for (int i = 1; i <= col; i++) {
                field.add(rsmd.getColumnName(i));
            }

            //controllo file encoding
            byte[] buf = new byte[4096];
            String fileName = f.getAbsolutePath();
            java.io.FileInputStream fis = new java.io.FileInputStream(fileName);
            UniversalDetector detector = new UniversalDetector(null);
            int nread;
            while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }
            detector.dataEnd();
            String encoding = detector.getDetectedCharset();
            if (encoding != null) {
                System.out.println("Detected encoding = " + encoding);
            } else {
                System.out.println("No encoding detected.");
            }
            detector.reset();
            //---------------------


            try {
                cp = new CSVParser(new BufferedReader(new InputStreamReader(new FileInputStream(f), encoding)));
            } catch (Exception e) {
                cp = new CSVParser(new FileInputStream(f));
            }

            cp.changeDelimiter(';');
            String[] csv = cp.getLine();

            Vector chiavi = new Vector();
            for (int i = 0; i < csv.length; i++) {
                chiavi.add(csv[i]);
            }

            Hashtable dati = new Hashtable();
            Vector<String> nonCaricati = new Vector();
            int contarighecsv = 0;
            while ((csv = cp.getLine()) != null) {
                contarighecsv++;
                for (int i = 0; i < chiavi.size(); i++) {
                    dati.put(chiavi.get(i), csv[i]);
                }

                sql = "SELECT max(riga) as last FROM " + nomeTabella + " WHERE id_padre = '" + dbIdPadre + "'";
                ResultSet contaRighe = Db.openResultSet(sql);
                int riga = 0;

                if (contaRighe.next()) {
                    riga = contaRighe.getInt("last") + 1;
                } else {
                    riga = 1;
                }

                Vector values = new Vector();
                for (Object campo : field) {
                    String chiave = String.valueOf(campo);

                    if (chiave.equals("serie")) {
                        values.add(dbSerie);
                    } else if (chiave.equals("numero")) {
                        values.add(dbNumero);
                    } else if (chiave.equals("anno")) {
                        values.add(dbAnno);
                    } else if (chiave.equals("id_padre")) {
                        values.add(dbIdPadre);
                    } else if (chiave.equals("prezzo")) {
                        if (!nomeListino.equals("FromFile")) {
                            String sqlart = "";

                            if (ricarico) {
                                sqlart = "SELECT (prezzo + (prezzo * " + percRicarico + ")/100) as prezzo FROM articoli_prezzi WHERE articolo = '" + Db.nz(dati.get("codice_articolo"), "") + "' AND listino = '" + listinoRicarico + "'";
                            } else {
                                sqlart = "SELECT prezzo FROM articoli_prezzi WHERE articolo = '" + Db.nz(dati.get("codice_articolo"), "") + "' AND listino = '" + nomeListino + "'";
                            }

                            ResultSet rs = Db.openResultSet(sqlart);
                            if (rs.next()) {
                                values.add(String.valueOf(rs.getDouble("prezzo")));
                            } else {
                                values.add(Db.nz(dati.get(chiave), ""));
                                nonCaricati.add(Db.nz(dati.get("codice_articolo"), "") + ": " + Db.nz(dati.get("descrizione"), ""));
                            }
                        } else {
                            values.add(Db.nz(dati.get(chiave), ""));
                        }
                    } else if (chiave.equals("riga")) {
                        values.add(riga);
                        riga++;
                    } else if (chiave.equals("iva")) {
                        if (dati.get(chiave) == null) {
                            values.add(getIvaDefaultPassaggio());
                        } else {
                            values.add(Db.nz(dati.get(chiave), ""));
                        }
                    } else {
                        values.add(Db.nz(dati.get(chiave), ""));
                    }
                }

                sql = "INSERT INTO " + nomeTabella + " SET ";
                for (int i = 0; i < field.size(); i++) {
                    if (!String.valueOf(field.get(i)).equals("id")) {
                        sql += field.get(i) + " = '" + Db.aa(cu.toString(values.get(i))) + "'";
                        if (i < field.size() - 1) {
                            sql += ", ";
                        }
                    }
                }
                System.out.println("sql: " + sql);
                try {
                    DbUtils.tryExecQuery(Db.getConn(), sql);
                } catch (Exception e) {
                    SwingUtils.showErrorMessage(main.getPadreFrame(), "Errore alla riga " + contarighecsv + ":\n" + e.getMessage() + "\nL'import viene interrotto");
                    break;
                }
            }
            if (!nonCaricati.isEmpty()) {
                String list = "<html>Articoli non caricati (manca prezzo nel listino prescelto):<br>";
                for (String value : nonCaricati) {
                    list += "- " + value + "<br>";
                }
                list += "<br>Questi articoli verranno ricaricati con il prezzo inserito nel csv importato</html>";
                JOptionPane.showMessageDialog(main.getPadre(), list, "ELENCO ARTICOLI NON CARICATI", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            SwingUtils.showExceptionMessage(main.getPadreFrame(), e);
        }
    }

    public static void importXls(String tipoDoc, File f, String serie, int numero, int anno, int idPadre) {
        //import da xls cc proskin
        try {
            final InputStream inp = new FileInputStream(f);
            HSSFWorkbook wb = new HSSFWorkbook(inp);
            final HSSFSheet sheet = wb.getSheetAt(1);
            int contaok = 0;
            int contako = 0;
            int contaconcodice = 0;


            String nomeTabella = "";
            if (tipoDoc.equals(Db.TIPO_DOCUMENTO_DDT)) {
                nomeTabella = "righ_ddt";
            } else if (tipoDoc.equals(Db.TIPO_DOCUMENTO_DDT_ACQUISTO)) {
                nomeTabella = "righ_ddt_acquisto";
            } else if (tipoDoc.equals(Db.TIPO_DOCUMENTO_FATTURA)) {
                nomeTabella = "righ_fatt";
            } else if (tipoDoc.equals(Db.TIPO_DOCUMENTO_FATTURA_RICEVUTA)) {
                nomeTabella = "righ_fatt_acquisto";
            } else if (tipoDoc.equals(Db.TIPO_DOCUMENTO_ORDINE)) {
                nomeTabella = "righ_ordi";
            } else if (tipoDoc.equals(Db.TIPO_DOCUMENTO_ORDINE_ACQUISTO)) {
                nomeTabella = "righ_ordi_acquisto";
            }

            int col = 0;
            String sql = "SELECT * FROM " + nomeTabella + " LIMIT 1";
            ResultSet righe = Db.openResultSet(sql);
            Vector field = new Vector();
            ResultSetMetaData rsmd = righe.getMetaData();
            col = rsmd.getColumnCount();
            for (int i = 1; i <= col; i++) {
                field.add(rsmd.getColumnName(i));
            }
            Vector<String> nonCaricati = new Vector();

            ResultSet resu = null;
            String[] colonne = new String[]{"codice", "pz", "13me", "descrizione", "linea", "ml", "totale", "prezzo_unitario", "totale_omaggio", "totale_vendita"};

            sql = "SELECT max(riga) as last FROM " + nomeTabella + " WHERE id_padre = '" + idPadre + "'";
            ResultSet contaRighe = Db.openResultSet(sql);
            int riga = 0;
            if (contaRighe.next()) {
                riga = contaRighe.getInt("last") + 1;
            } else {
                riga = 1;
            }

            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                HSSFRow row = sheet.getRow(i);
                try {
                    HashMap recxls = getMap(row, colonne, false);
                    sql = "";
                    System.out.println("riga " + (i + 1));
                    DebugFastUtils.dump(recxls);
                    String codice = CastUtils.toString(recxls.get("codice"));
                    if (codice.equalsIgnoreCase("CODICE")) {
                        continue;
                    }
                    Object pz = recxls.get("pz");
                    //Object pztot = recxls.get("totale");
                    Double dpz = CastUtils.toDouble0(pz);
                    //Double dpztot = CastUtils.toDouble0(pztot);
                    //Double omaggi = dpztot - dpz;
                    Double omaggi = CastUtils.toDouble0(recxls.get("13me"));
                    if (dpz != 0 || omaggi != 0) {
                        Vector values = new Vector();
                        for (Object campo : field) {
                            String chiave = String.valueOf(campo);

                            if (chiave.equals("serie")) {
                                values.add(serie);
                            } else if (chiave.equals("numero")) {
                                values.add(numero);
                            } else if (chiave.equals("anno")) {
                                values.add(anno);
                            } else if (chiave.equals("id_padre")) {
                                values.add(idPadre);
                            } else if (chiave.equals("codice_articolo")) {
                                values.add(Db.nz(recxls.get("codice"), ""));
                            } else if (chiave.equals("descrizione")) {
                                values.add(Db.nz(recxls.get("descrizione"), ""));
                            } else if (chiave.equals("quantita")) {
                                values.add(Db.nz(recxls.get("pz"), ""));
                            } else if (chiave.equals("prezzo")) {
                                values.add(Db.nz(recxls.get("prezzo_unitario"), ""));
                            } else if (chiave.equals("iva")) {
                                values.add("22");
                            } else if (chiave.equals("riga")) {
                                values.add(riga);
//                                riga++;
                            } else {
                                values.add("");
                            }
                        }

                        sql = "INSERT INTO " + nomeTabella + " SET ";
                        for (int i2 = 0; i2 < field.size(); i2++) {
                            if (!String.valueOf(field.get(i2)).equals("id")) {
                                if (i2 == field.size() - 1) {
                                    sql += field.get(i2) + " = '" + Db.aa(CastUtils.toString(values.get(i2))) + "'";
                                } else {
                                    sql += field.get(i2) + " = '" + Db.aa(CastUtils.toString(values.get(i2))) + "', ";
                                }
                            }
                        }
                        System.out.println("sql: " + sql);
                        Db.executeSql(sql);
                        riga++;

                        //controllo omaggi
                        if (omaggi > 0) {
                            values = new Vector();
                            for (Object campo : field) {
                                String chiave = String.valueOf(campo);
                                if (chiave.equals("serie")) {
                                    values.add(serie);
                                } else if (chiave.equals("numero")) {
                                    values.add(numero);
                                } else if (chiave.equals("anno")) {
                                    values.add(anno);
                                } else if (chiave.equals("id_padre")) {
                                    values.add(idPadre);
                                } else if (chiave.equals("codice_articolo")) {
                                    values.add(Db.nz(recxls.get("codice"), ""));
                                } else if (chiave.equals("descrizione")) {
                                    values.add("Omaggio: " + Db.nz(recxls.get("descrizione"), ""));
                                } else if (chiave.equals("quantita")) {
                                    values.add(omaggi);
                                } else if (chiave.equals("prezzo")) {
                                    values.add(0d);
                                } else if (chiave.equals("iva")) {
                                    values.add("22");
                                } else if (chiave.equals("riga")) {
                                    values.add(riga);
//                                    riga++;
                                } else {
                                    values.add("");
                                }
                            }

                            sql = "INSERT INTO " + nomeTabella + " SET ";
                            for (int i2 = 0; i2 < field.size(); i2++) {
                                if (!String.valueOf(field.get(i2)).equals("id")) {
                                    if (i2 == field.size() - 1) {
                                        sql += field.get(i2) + " = '" + Db.aa(CastUtils.toString(values.get(i2))) + "'";
                                    } else {
                                        sql += field.get(i2) + " = '" + Db.aa(CastUtils.toString(values.get(i2))) + "', ";
                                    }
                                }
                            }
                            System.out.println("sql: " + sql);
                            Db.executeSql(sql);
                            riga++;
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            InvoicexUtil.aggiornaTotaliRighe(tipoDoc, idPadre);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static HashMap getMap(HSSFRow row, String[] colonne, boolean nonazzerare) {
        int i = 0;
        HashMap m = new HashMap();
        final NumberFormat nf1 = new DecimalFormat("0");
        for (String colonna : colonne) {
            try {
                String temp = row.getCell((short) i).getStringCellValue().trim();
                if (temp != null) {
                    temp = temp.trim();
                }
                if (nonazzerare) {
                    if (temp != null && temp.length() > 0) {
                        m.put(colonne[i], temp);
                    } else {
                        //non metto
                    }
                } else {
                    m.put(colonne[i], temp);
                }
            } catch (Exception e) {
                try {
                    Double d = row.getCell((short) i).getNumericCellValue();
                    //se non ci sono decimali passo come stringa altrimenti come double
                    if (d.intValue() == d.doubleValue()) {
                        m.put(colonne[i], nf1.format(row.getCell((short) i).getNumericCellValue()));
                    } else {
                        m.put(colonne[i], row.getCell((short) i).getNumericCellValue());
                    }
                } catch (Exception e2) {
                    System.out.println("col:" + colonna + " e2:" + e2.getMessage());
                    if (!nonazzerare) {
                        m.put(colonne[i], "");
                    }
                }
            }
            i++;
        }
        return m;
    }

    static public void exportCSV(String tipoDoc, int[] ids, String nomeFile) {
        String nomeTabella = "";
        if (tipoDoc.equals(Db.TIPO_DOCUMENTO_DDT)) {
            nomeTabella = "righ_ddt";
        } else if (tipoDoc.equals(Db.TIPO_DOCUMENTO_DDT_ACQUISTO)) {
            nomeTabella = "righ_ddt_acquisto";
        } else if (tipoDoc.equals(Db.TIPO_DOCUMENTO_FATTURA)) {
            nomeTabella = "righ_fatt";
        } else if (tipoDoc.equals(Db.TIPO_DOCUMENTO_FATTURA_RICEVUTA)) {
            nomeTabella = "righ_fatt_acquisto";
        } else if (tipoDoc.equals(Db.TIPO_DOCUMENTO_ORDINE)) {
            nomeTabella = "righ_ordi";
        } else if (tipoDoc.equals(Db.TIPO_DOCUMENTO_ORDINE_ACQUISTO)) {
            nomeTabella = "righ_ordi_acquisto";
        }
        String intestazioni = "";
        String riga = "";
        FileOutputStream fcsv = null;
        String sql = "SELECT * FROM " + nomeTabella + " LIMIT 1";
        ResultSet righe = Db.openResultSet(sql);
        ArrayList field = new ArrayList();

        try {
            String dir = System.getProperty("user.home") + File.separator + ".invoicex" + File.separator + "Export" + File.separator;
            File startDir = new File(dir);
            if (!startDir.exists()) {
                startDir.mkdir();
            }

            nomeFile = dir + nomeFile + ".csv";

            fcsv = new FileOutputStream(nomeFile);
            System.out.println("Debug: cvs creato");


            int col = 0;
            if (righe.next()) {
                ResultSetMetaData rsmd = righe.getMetaData();
                col = rsmd.getColumnCount();

                for (int i = 1; i <= col; i++) {
                    field.add(rsmd.getColumnName(i));
                }
                intestazioni = StringUtils.join(field, ";");
                riga = intestazioni + "\n";
                fcsv.write(riga.getBytes());
                System.out.println("Debug: intestazione creata");
            }
            for (int id : ids) {
                sql = "SELECT * FROM " + nomeTabella + " WHERE id_padre = (" + id + ") order by riga";
                System.out.println("sql: " + sql);
                righe = Db.openResultSet(sql);
                ResultSetMetaData rsmd = righe.getMetaData();
                col = rsmd.getColumnCount();

                righe.beforeFirst();
                while (righe.next()) {
                    Hashtable dati = new Hashtable();


                    for (int i = 0; i < col; i++) {
                        String chiave = String.valueOf(field.get(i));
                        try {
                            dati.put(chiave, Db.nz(righe.getString(chiave), ""));
                        } catch (Exception e) {
                            continue;
                        }

                    }

                    riga = getRiga(dati, intestazioni);
                    fcsv.write(riga.getBytes());
                }
            }
            fcsv.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            it.tnx.commons.SwingUtils.open(new File(System.getProperty("user.home") + File.separator + ".invoicex" + File.separator + "Export" + File.separator));
            Util.start2(System.getProperty("user.home") + File.separator + ".invoicex" + File.separator + "Export" + File.separator);
        }
    }

    private static String getRiga(Hashtable<String, String> dati, String colonne) {
        String[] cols = StringUtils.split(colonne, ";");
        String riga = "";
        for (int i = 0; i < cols.length; i++) {
            riga += "\"" + StringUtils.replace(StringUtils.defaultString(dati.get(cols[i])), "\"", "") + "\"" + ";";
        }
        riga += "\r\n";
        return riga;
    }

    static public void esportaInExcel(ResultSet rs, String nomeFile, String title, String note_testa, String note_piede, Map colonne) {
        Connection connection;
        java.sql.Statement stat;
        ResultSet resu;
        String nomeFileXls = nomeFile;

        try {

            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFDataFormat format = wb.createDataFormat();

            if (title == null || title.length() == 0) {
                title = "export";
            }

            HSSFSheet sheet = wb.createSheet(title);

            short contarows = 0;
            HSSFRow row = sheet.createRow((short) contarows);
            contarows++;
            row.createCell((short) 0).setCellValue(title);

            if (note_testa != null && note_testa.length() > 0) {
                row = sheet.createRow((short) contarows);
                contarows++;
                row = sheet.createRow((short) contarows);
                contarows++;
                row.createCell((short) 0).setCellValue(note_testa);
            }

            row = sheet.createRow((short) contarows);
            contarows++;
            //colonne
            row = sheet.createRow((short) contarows);
            contarows++;
            int columns = 0;
            columns = rs.getMetaData().getColumnCount();

            Iterator iter = colonne.keySet().iterator();
            short i = 0;
            while (iter.hasNext()) {
                Object key = iter.next();
                Object value = colonne.get(key);
                String col = "";
                //col = rs.getMetaData().getColumnLabel(i+1);
                col = (String) value;
                if (col == null || col.length() == 0) {
                    col = (String) key;
                }
                row.createCell((short) i).setCellValue(col);
                i++;
                //sheet.setColumnWidth((short) i, (short) (headerWidth[i] * 300));
            }

            //stili
            HSSFCellStyle styledouble = wb.createCellStyle();
            styledouble.setDataFormat(format.getFormat("#,##0.00###"));

            HSSFCellStyle styleint = wb.createCellStyle();
            styleint.setDataFormat(format.getFormat("#,##0"));

            HSSFCellStyle styledata = wb.createCellStyle();
            styledata.setDataFormat(format.getFormat("dd/MM/yy"));

            //righe
            int rowcount = 0;
            rs.last();
            rowcount = rs.getRow();
            rs.beforeFirst();
            for (int j = 0; j < rowcount; j++) {
                row = sheet.createRow((short) contarows);
                contarows++;
                //colonne
                iter = colonne.keySet().iterator();
                i = 0;
                while (iter.hasNext()) {
                    String key = (String) iter.next();
                    String value = (String) colonne.get(key);

                    //controllo tipo di campo
                    Object o = null;
                    rs.absolute(j + 1);
                    o = rs.getObject(key);
                    if (o instanceof Double) {
                        HSSFCell cell = row.createCell((short) i);
                        cell.setCellValue((Double) o);
                        cell.setCellStyle(styledouble);
                    } else if (o instanceof BigDecimal) {
                        HSSFCell cell = row.createCell((short) i);
                        cell.setCellValue(((BigDecimal) o).doubleValue());
                        cell.setCellStyle(styledouble);
                    } else if (o instanceof Integer) {
                        HSSFCell cell = row.createCell((short) i);
                        cell.setCellValue(((Integer) o).intValue());
                        cell.setCellStyle(styleint);
                    } else if (o instanceof java.sql.Date) {
                        HSSFCell cell = row.createCell((short) i);
                        cell.setCellValue(((java.sql.Date) o));
                        cell.setCellStyle(styledata);
                    } else if (o instanceof byte[]) {
                        HSSFCell cell = row.createCell((short) i);
                        cell.setCellValue(new String((byte[]) o));
                        cell.setCellStyle(styleint);
                        row.createCell((short) i).setCellValue(new String((byte[]) o));
                    } else if (o instanceof Long) {
                        HSSFCell cell = row.createCell((short) i);
                        cell.setCellValue(((Long) o).longValue());
                        cell.setCellStyle(styleint);
                    } else {
                        if (!(o instanceof String)) {
                            if (o != null) {
                                System.out.println(o.getClass());
                            }
                        }
                        row.createCell((short) i).setCellValue(CastUtils.toString(o));
                    }
                    i++;
                }
            }

            if (note_piede != null && note_piede.length() > 0) {
                row = sheet.createRow((short) contarows);
                contarows++;
                row = sheet.createRow((short) contarows);
                contarows++;
                row.createCell((short) 0).setCellValue(note_piede);
            }

            FileOutputStream fileOut = new FileOutputStream(nomeFileXls);
            wb.write(fileOut);
            fileOut.close();

            return;
        } catch (Exception err) {
            javax.swing.JOptionPane.showMessageDialog(null, err.toString());
            err.printStackTrace();
            return;
        }
    }

    public static void esportaInExcel(List<Object[]> list, String nomeFile, String title, String note_testa, String note_piede, Map colonne) {
        String nomeFileXls = nomeFile;
        try {
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFDataFormat format = wb.createDataFormat();

            if ((title == null) || (title.length() == 0)) {
                title = "export";
            }

            HSSFSheet sheet = wb.createSheet(title);

            short contarows = 0;
            HSSFRow row = sheet.createRow(contarows);
            contarows = (short) (contarows + 1);
            row.createCell(0).setCellValue(title);

            if ((note_testa != null) && (note_testa.length() > 0)) {
                row = sheet.createRow(contarows);
                contarows = (short) (contarows + 1);
                row = sheet.createRow(contarows);
                contarows = (short) (contarows + 1);
                row.createCell(0).setCellValue(note_testa);
            }

            row = sheet.createRow(contarows);
            contarows = (short) (contarows + 1);

            row = sheet.createRow(contarows);
            contarows = (short) (contarows + 1);
            int columns = 0;
            columns = ((Object[]) list.get(0)).length;

            Iterator iter = colonne.keySet().iterator();
            short i = 0;
            while (iter.hasNext()) {
                Object key = iter.next();
                Object value = colonne.get(key);
                String col = "";

                col = (String) value;
                if ((col == null) || (col.length() == 0)) {
                    col = (String) key;
                }
                row.createCell(i).setCellValue(col);
                i = (short) (i + 1);
            }

            HSSFCellStyle styledouble = wb.createCellStyle();
            styledouble.setDataFormat(format.getFormat("#,##0.00###"));

            HSSFCellStyle styleint = wb.createCellStyle();
            styleint.setDataFormat(format.getFormat("#,##0"));

            HSSFCellStyle styledata = wb.createCellStyle();
            styledata.setDataFormat(format.getFormat("dd/MM/yy"));

            int rowcount = 0;
            rowcount = list.size();
            for (int j = 0; j < rowcount; j++) {
                row = sheet.createRow(contarows);
                contarows = (short) (contarows + 1);

                iter = colonne.keySet().iterator();
                i = 0;
                while (iter.hasNext()) {
                    String key = (String) iter.next();
                    String value = (String) colonne.get(key);

                    Object o = null;
                    o = ((Object[]) list.get(j))[i];
                    if ((o instanceof Double)) {
                        HSSFCell cell = row.createCell(i);
                        cell.setCellValue(((Double) o).doubleValue());
                        cell.setCellStyle(styledouble);
                    } else if ((o instanceof BigDecimal)) {
                        HSSFCell cell = row.createCell(i);
                        cell.setCellValue(((BigDecimal) o).doubleValue());
                        cell.setCellStyle(styledouble);
                    } else if ((o instanceof Integer)) {
                        HSSFCell cell = row.createCell(i);
                        cell.setCellValue(((Integer) o).intValue());
                        cell.setCellStyle(styleint);
                    } else if ((o instanceof Date)) {
                        HSSFCell cell = row.createCell(i);
                        cell.setCellValue((Date) o);
                        cell.setCellStyle(styledata);
                    } else if ((o instanceof byte[])) {
                        HSSFCell cell = row.createCell(i);
                        cell.setCellValue(new String((byte[]) (byte[]) o));
                        cell.setCellStyle(styleint);
                        row.createCell(i).setCellValue(new String((byte[]) (byte[]) o));
                    } else if ((o instanceof Long)) {
                        HSSFCell cell = row.createCell(i);
                        cell.setCellValue(((Long) o).longValue());
                        cell.setCellStyle(styleint);
                    } else {
                        if ((!(o instanceof String))
                                && (o != null)) {
                            System.out.println(o.getClass());
                        }

                        row.createCell(i).setCellValue(CastUtils.toString(o));
                    }
                    i = (short) (i + 1);
                }
            }

            if ((note_piede != null) && (note_piede.length() > 0)) {
                row = sheet.createRow(contarows);
                contarows = (short) (contarows + 1);
                row = sheet.createRow(contarows);
                contarows = (short) (contarows + 1);
                row.createCell(0).setCellValue(note_piede);
            }

            FileOutputStream fileOut = new FileOutputStream(nomeFileXls);
            wb.write(fileOut);
            fileOut.close();

            return;
        } catch (Exception err) {
            JOptionPane.showMessageDialog(null, err.toString());
            err.printStackTrace();
        }
    }

    //stampe..
    public static String controllaPosizioneLogoSuffisso() {
        if (main.fileIni.existKey("varie", "logo_x") && main.fileIni.getValue("varie", "logo_disabilita", "N").equalsIgnoreCase("N")) {
            int x = CastUtils.toInteger0(main.fileIni.getValue("varie", "logo_x", "0"));
            int y = CastUtils.toInteger0(main.fileIni.getValue("varie", "logo_y", "0"));
            int w = CastUtils.toInteger0(main.fileIni.getValue("varie", "logo_w", "100"));
            int h = CastUtils.toInteger0(main.fileIni.getValue("varie", "logo_h", "100"));
            return "_x" + x + "_y" + y + "_w" + w + "_h" + h;
        }
        return "";
    }

    public static JasperDesign controllaLogo(File freport, JasperReport rep, JasperDesign repdes) throws JRException {
        //controllo il logo
        JRDesignBand header = (JRDesignBand) repdes.getPageHeader();
        if (main.fileIni.existKey("varie", "logo_x") && main.fileIni.getValue("varie", "logo_disabilita", "N").equalsIgnoreCase("N")) {
            for (JRElement el : header.getElements()) {
                if (el instanceof JRDesignImage) {
                    JRDesignImage image = (JRDesignImage) el;

                    System.out.println("image x " + image.getX() + " y " + image.getY() + " w " + image.getWidth() + " h " + image.getHeight());
                    int x = CastUtils.toInteger0(main.fileIni.getValue("varie", "logo_x", "0"));
                    int y = CastUtils.toInteger0(main.fileIni.getValue("varie", "logo_y", "0"));
                    int w = CastUtils.toInteger0(main.fileIni.getValue("varie", "logo_w", "100"));
                    int h = CastUtils.toInteger0(main.fileIni.getValue("varie", "logo_h", "100"));

                    x = check(x, 0);
                    y = check(y, 0);
                    w = checks(w, 100);
                    h = checks(h, 100);

                    System.out.println("logo x " + x + " y " + y + " sw " + w + " sh " + h);

                    image.setVerticalAlignment(JRDesignImage.VERTICAL_ALIGN_TOP);
                    int w2 = image.getWidth();
                    int h2 = image.getHeight();
                    x = x * w2 / 100;
                    y = y * h2 / 100;
                    w = w * w2 / 100;
                    h = h * h2 / 100;
                    image.setX(image.getX() + x);
                    image.setY(image.getY() + y);
                    image.setWidth(w);
                    image.setHeight(h);
                }
            }
        }

        return repdes;
    }

    static private int check(int x, int def) {
        if (x < 0 || x > 100) {
            return def;
        }
        return x;
    }

    static private int checks(int x, int def) {
        if (x <= 0 || x > 100) {
            return def;
        }
        return x;
    }

    public static void storicizza(String nota, String doc, int id) {
        //Creazione dati
        MicroBench mb = new MicroBench();
        mb.start();

        HashMap dati = new HashMap();
        if (doc.equals("fattura")) {
            try {
                ArrayList testa = DbUtils.getListMap(Db.getConn(), "select * from test_fatt where id = " + id);
                dati.put("testa", testa);
                ArrayList righe = DbUtils.getListMap(Db.getConn(), "select * from righ_fatt where id_padre = " + id);
                dati.put("righe", righe);
                ArrayList cliente = DbUtils.getListMap(Db.getConn(), "select c.* from clie_forn c join test_fatt t on c.codice = t.cliente where t.id = " + id);
                dati.put("cliente", cliente);
                ArrayList movimenti = DbUtils.getListMap(Db.getConn(), "select * from movimenti_magazzino where da_tabella = 'test_fatt' and da_id = " + id);
                dati.put("movimenti", movimenti);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (doc.equals("ddt")) {
            try {
                ArrayList testa = DbUtils.getListMap(Db.getConn(), "select * from test_ddt where id = " + id);
                dati.put("testa", testa);
                ArrayList righe = DbUtils.getListMap(Db.getConn(), "select * from righ_ddt where id_padre = " + id);
                dati.put("righe", righe);
                ArrayList cliente = DbUtils.getListMap(Db.getConn(), "select c.* from clie_forn c join test_ddt t on c.codice = t.cliente where t.id = " + id);
                dati.put("cliente", cliente);
                ArrayList movimenti = DbUtils.getListMap(Db.getConn(), "select * from movimenti_magazzino where da_tabella = 'test_ddt' and da_id = " + id);
                dati.put("movimenti", movimenti);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (doc.equals("ordine")) {
            try {
                ArrayList testa = DbUtils.getListMap(Db.getConn(), "select * from test_ordi where id = " + id);
                dati.put("testa", testa);
                ArrayList righe = DbUtils.getListMap(Db.getConn(), "select * from righ_ordi where id_padre = " + id);
                dati.put("righe", righe);
                ArrayList cliente = DbUtils.getListMap(Db.getConn(), "select c.* from clie_forn c join test_ordi t on c.codice = t.cliente where t.id = " + id);
                dati.put("cliente", cliente);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (doc.equals("ordine_acquisto")) {
            try {
                ArrayList testa = DbUtils.getListMap(Db.getConn(), "select * from test_ordi_acquisto where id = " + id);
                dati.put("testa", testa);
                ArrayList righe = DbUtils.getListMap(Db.getConn(), "select * from righ_ordi_acquisto where id_padre = " + id);
                dati.put("righe", righe);
                ArrayList cliente = DbUtils.getListMap(Db.getConn(), "select c.* from clie_forn c join test_ordi_acquisto t on c.codice = t.fornitore where t.id = " + id);
                dati.put("cliente", cliente);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            System.err.println("doc:" + doc + " non roconosciuto");
            Thread.dumpStack();
            return;
        }

        try {
//            ByteArrayOutputStream bout = new ByteArrayOutputStream();
//            ObjectOutputStream oos = new ObjectOutputStream(bout);
//            oos.writeObject(dati);

            //hessian serialization
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            Hessian2Output out = new Hessian2Output(bout);
            out.startMessage();
            out.writeObject(dati);
            out.completeMessage();
            out.close();

            //codifico base64
            byte[] bytes64 = Base64.encodeBase64(bout.toByteArray());

            ResultSet r = DbUtils.tryOpenResultSetEditable(Db.getConn(), "select * from storico limit 0");
            r.moveToInsertRow();
            r.updateString("nota", nota);
            r.updateBytes("dati", bytes64);
            r.insertRow();
            int idstorico = CastUtils.toInteger0(DbUtils.getObject(Db.getConn(), "select LAST_INSERT_ID()")).intValue();
            System.out.println("id storico:" + idstorico);

//            //test di rilettura
//            ResultSet rtest = DbUtils.tryOpenResultSetEditable(Db.getConn(), "select * from storico where id = " + idstorico);
//            if (rtest.next()) {
////                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(rtest.getBytes("dati")));
////                Object oread = ois.readObject();
//
//                //hessian serialization
//                ByteArrayInputStream bin = new ByteArrayInputStream(rtest.getBytes("dati"));
//                Hessian2Input in = new Hessian2Input(bin);
//                in.startMessage();
//                Object oread = in.readObject();
//                in.completeMessage();
//                in.close();
//                bin.close();
//
//                System.out.println("test rilettura storico:");
//                DebugUtils.dump(oread);
//            }
//            try {
//                rtest.getStatement().close();
//                rtest.close();
//            } catch (Exception e) {
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Hessian2Output out = new Hessian2Output(bos);
            out.startMessage();
            out.writeObject(dati);
            out.completeMessage();
            out.close();
            byte[] data = bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(mb.getDiff("storico"));
    }

    public static void leggiDaStorico(String cosa, int id) {
        List<Map> list;
        String msg = "";
        try {
            String sql = "select id, data from storico where nota like 'modifica " + cosa + " id:" + id + "'";
            System.out.println("sql = " + sql);
            list = DbUtils.getListMap(Db.getConn(), sql);
            System.out.println("list = " + list);
            for (Map rec : list) {
                msg += leggiDaStorico(CastUtils.toInteger(rec.get("id")));
            }
        } catch (Exception ex) {
            Logger.getLogger(InvoicexUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        JFrameDb frame = new JFrameDb();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.sqlarea.setText(msg);
    }

    public static String leggiDaStorico(int idStorico) throws Exception {
        //test di rilettura
        String msg = "";
        System.out.println("lettura sotrico id:" + idStorico);
        ResultSet rtest = DbUtils.tryOpenResultSetEditable(Db.getConn(), "select * from storico where id = " + idStorico);
        if (rtest.next()) {
            try {
                //prima provo serilalizzazione java (vecchi ostorico ma problemi con class version)
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(rtest.getBytes("dati")));
                Object oread = ois.readObject();
                System.out.println("rilettura storico:");
                DebugUtils.dump(oread);
                String sqlrighe = stampaSqlRighe(oread);
                if (sqlrighe.length() > 0) {
                    msg += "da storico " + rtest.getString("id") + " data " + rtest.getString("data") + "\n" + sqlrighe + "\n\n";
                }
            } catch (Exception e) {
                System.out.println("rileggo con hessian per err:" + e.getMessage());
                //provo con nuovo sistema hessian
                try {
                    byte[] bytes64dec = Base64.decodeBase64(rtest.getBytes("dati"));
                    ByteArrayInputStream bin = new ByteArrayInputStream(bytes64dec);
                    Hessian2Input in = new Hessian2Input(bin);
                    in.startMessage();
                    Object oread = in.readObject();
                    in.completeMessage();
                    in.close();
                    bin.close();
                    DebugUtils.dump(oread);

                    String sqlrighe = stampaSqlRighe(oread);
                    if (sqlrighe.length() > 0) {
                        msg += "da storico " + rtest.getString("id") + " data " + rtest.getString("data") + "\n" + sqlrighe + "\n\n";
                    }
                } catch (Exception e2) {
                    System.out.println("impossibile leggere storico " + idStorico + " err:" + e2.getMessage());
                }
            }
        }
        try {
            rtest.getStatement().close();
            rtest.close();
        } catch (Exception e) {
        }
        return msg;
    }

    public static String stampaSqlRighe(Object o) {
        String sql = "";
        try {
            Map m = (Map) o;
            List<Map> l = (List<Map>) m.get("righe");
            for (Map ml : l) {
                sql += "insert ??? set " + DbUtils.prepareSqlFromMap(ml) + ";\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sql;
    }

    public static Integer getIdFattura(String serie, int numero, int anno) {
        return getIdFattura(Db.getConn(), serie, numero, anno);
    }

    public static Integer getIdFattura(Connection conn, String serie, int numero, int anno) {
        try {
            return (Integer) DbUtils.getObject(conn, "select id from test_fatt where tipo_fattura != 7 and serie = '" + Db.aa(serie) + "' and numero = " + numero + " and anno = " + anno);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Integer getIdFatturaAcquisto(String serie, int numero, int anno) {
        try {
            return (Integer) DbUtils.getObject(Db.getConn(), "select id from test_fatt_acquisto where serie = '" + Db.aa(serie) + "' and numero = " + numero + " and anno = " + anno);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Integer getIdDdt(String serie, int numero, int anno) {
        try {
            return (Integer) DbUtils.getObject(Db.getConn(), "select id from test_ddt where serie = '" + Db.aa(serie) + "' and numero = " + numero + " and anno = " + anno);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Integer getIdDdtAcquisto(String serie, int numero, int anno) {
        try {
            return (Integer) DbUtils.getObject(Db.getConn(), "select id from test_ddt_acquisto where serie = '" + Db.aa(serie) + "' and numero = " + numero + " and anno = " + anno);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Integer getIdOrdine(String serie, int numero, int anno) {
        try {
            return (Integer) DbUtils.getObject(Db.getConn(), "select id from test_ordi where serie = '" + Db.aa(serie) + "' and numero = " + numero + " and anno = " + anno);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Integer getIdOrdineAcquisto(String serie, int numero, int anno) {
        try {
            return (Integer) DbUtils.getObject(Db.getConn(), "select id from test_ordi_acquisto where serie = '" + Db.aa(serie) + "' and numero = " + numero + " and anno = " + anno);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Integer getNumeroScadenze(String serie, int numero, int anno) {
        try {
            return ((Long) DbUtils.getObject(Db.getConn(), "select count(*) from scadenze where documento_tipo = 'FA' and documento_serie = '" + Db.aa(serie) + "' and documento_numero = " + numero + " and documento_anno = " + anno)).intValue();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void controlloProvvigioniAutomatiche(tnxComboField comAgente, tnxTextField texProvvigione, tnxTextField texScon1, JInternalFrame from, Integer clifor) {
        //trovo la percentuale di questo agente
        try {
            ResultSet r = Db.lookUp(comAgente.getSelectedKey().toString(), "id", "agenti");
            if (r == null) {
                return;
            }
            Double provvigione_predefinita_cliente = null;
            if (clifor != null) {
                provvigione_predefinita_cliente = cu.toDouble(DbUtils.getObject(Db.getConn(), "select provvigione_predefinita_cliente from clie_forn where codice = " + clifor.toString()));
            }

            iniFileProp fileIni = main.fileIni;

            double percentualeTabella = 0d;
            double percentualeForm = 0d;

            if (fileIni.getValueBoolean("pref", "provvigioniAutomatiche", false)) {
                BigDecimal scontoMinTmp = (BigDecimal) DbUtils.getObject(Db.getConn(), "SELECT MAX(sconto_soglia) FROM soglie_provvigioni WHERE sconto_soglia <= " + Db.pc(texScon1.getText(), Types.DECIMAL));
                BigDecimal scontoMaxTmp = (BigDecimal) DbUtils.getObject(Db.getConn(), "SELECT MIN(sconto_soglia) FROM soglie_provvigioni WHERE sconto_soglia > " + Db.pc(texScon1.getText(), Types.DECIMAL));

                BigDecimal provvMinTmp = BigDecimal.valueOf(0d);
                BigDecimal provvMaxTmp = BigDecimal.valueOf(0d);

                scontoMinTmp = scontoMinTmp == null ? BigDecimal.valueOf(0d) : scontoMinTmp;
                scontoMaxTmp = scontoMaxTmp == null ? BigDecimal.valueOf(100d) : scontoMaxTmp;

                if (fileIni.getValueBoolean("pref", "provvigioniPercentualeAuto", false)) {
                    provvMinTmp = (BigDecimal) DbUtils.getObject(Db.getConn(), "SELECT MAX(percentuale) FROM soglie_provvigioni WHERE sconto_soglia <= " + Db.pc(texScon1.getText(), Types.DECIMAL));
                    provvMaxTmp = (BigDecimal) DbUtils.getObject(Db.getConn(), "SELECT MIN(percentuale) FROM soglie_provvigioni WHERE sconto_soglia > " + Db.pc(texScon1.getText(), Types.DECIMAL));

                    provvMinTmp = provvMinTmp == null ? BigDecimal.valueOf(0d) : provvMinTmp;
                    provvMaxTmp = provvMaxTmp == null ? (BigDecimal) DbUtils.getObject(Db.getConn(), "SELECT MAX(percentuale) FROM soglie_provvigioni") : provvMaxTmp;
                } else {
                    Integer sogliaMinTmp = (Integer) DbUtils.getObject(Db.getConn(), "SELECT MAX(soglia) FROM soglie_provvigioni WHERE sconto_soglia <= " + Db.pc(texScon1.getText(), Types.DECIMAL));
                    Integer sogliaMaxTmp = (Integer) DbUtils.getObject(Db.getConn(), "SELECT MIN(soglia) FROM soglie_provvigioni WHERE sconto_soglia > " + Db.pc(texScon1.getText(), Types.DECIMAL));

                    sogliaMinTmp = sogliaMinTmp == null ? 0 : sogliaMinTmp;
                    sogliaMaxTmp = sogliaMaxTmp == null ? 5 : sogliaMaxTmp;

                    if (sogliaMinTmp != 0) {
                        provvMinTmp = (BigDecimal) DbUtils.getObject(Db.getConn(), "SELECT percentuale_soglia_" + sogliaMinTmp + " FROM agenti WHERE id = " + Db.pc(comAgente.getSelectedKey(), Types.INTEGER));
                    } else {
                        provvMinTmp = BigDecimal.valueOf(0d);
                    }
                    provvMaxTmp = (BigDecimal) DbUtils.getObject(Db.getConn(), "SELECT percentuale_soglia_" + sogliaMaxTmp + " FROM agenti WHERE id = " + Db.pc(comAgente.getSelectedKey(), Types.INTEGER));

                    provvMinTmp = provvMinTmp == null ? BigDecimal.valueOf(0d) : provvMinTmp;
                    provvMaxTmp = provvMaxTmp == null ? (BigDecimal) DbUtils.getObject(Db.getConn(), "SELECT percentuale_soglia_5 FROM agenti WHERE id = " + Db.pc(comAgente.getSelectedKey(), Types.INTEGER)) : provvMaxTmp;
                }



                Double provvigione = 0d;
                Double sconto = Double.parseDouble(texScon1.getText());
                Double scontoMin = scontoMinTmp.doubleValue();
                Double scontoMax = scontoMaxTmp.doubleValue();
                Double provvMin = provvMinTmp.doubleValue();
                Double provvMax = provvMaxTmp.doubleValue();

                provvigione = ((((sconto - scontoMin) / (scontoMax - scontoMin)) * (provvMax - provvMin)) + provvMin);

                percentualeTabella = provvigione == null ? 0 : provvigione;
                percentualeForm = Db.getDouble(texProvvigione.getText());
            } else {
                percentualeTabella = r.getObject("percentuale") == null ? 0 : r.getDouble("percentuale");
                percentualeForm = Db.getDouble(texProvvigione.getText());
                if (provvigione_predefinita_cliente != null && provvigione_predefinita_cliente != 0) {
                    percentualeTabella = provvigione_predefinita_cliente;
                }
            }

            double prov_prima = Db.getDouble(texProvvigione.getText());
            if (texProvvigione.getText().length() == 0 || percentualeForm == percentualeTabella || percentualeForm == 0) {
//                if (!fileIni.getValueBoolean("pref", "provvigioniAutomatiche", false)) {
//                    texProvvigione.setText(Db.formatValuta(r.getDouble("percentuale")));
//                } else {
//                    texProvvigione.setText(Db.formatValuta(percentualeTabella));
//                }
                texProvvigione.setText(Db.formatValuta(percentualeTabella));
            } else {
                if (!fileIni.getValueBoolean("pref", "provvigioniAutomatiche", false)) {
                    int ret = javax.swing.JOptionPane.showConfirmDialog(from, "La percentuale di provvigione dell'agente selezionato differisce da quella impostata.\nPer modificarla premere 'Si'", "Attenzione", javax.swing.JOptionPane.YES_NO_OPTION);

                    if (ret == javax.swing.JOptionPane.YES_OPTION) {
//                        texProvvigione.setText(Db.formatValuta(r.getDouble("percentuale")));
                        texProvvigione.setText(Db.formatValuta(percentualeTabella));
                    }
                } else {
                    texProvvigione.setText(Db.formatValuta(percentualeTabella));
                }
            }
            double prov_dopo = Db.getDouble(texProvvigione.getText());
            if (prov_prima != prov_dopo && ((GenericFrmTest) from).getGrid().getRowCount() > 0) {
                ((GenericFrmTest) from).aggiornareProvvigioni();
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    static public String getStatoEvasione(JTable tab, String qta, String qta_evasa) {
        String evaso = "";
        try {
            double sq = 0;
            double sqn = 0;
            double sqe = 0;
            double sqen = 0;
            boolean evasa = true;
            for (int row = 0; row < tab.getRowCount(); row++) {
                sqn = CastUtils.toDouble0(tab.getValueAt(row, tab.getColumn(qta).getModelIndex()));
                sqen = CastUtils.toDouble0(tab.getValueAt(row, tab.getColumn(qta_evasa).getModelIndex()));
                sq += sqn;
                sqe += sqen;
                if (sqn > 0 && sqen < sqn) {
                    evasa = false;
                }
            }
            if (evasa) {
                evaso = "S";
            } else if (sqe > 0) {
                evaso = "P";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return evaso;
    }

    static public void aggiornaStatoEvasione(String tipoDoc, Integer id) {
        String sql = "select IFNULL(quantita, 0) as sq, IFNULL(quantita_evasa,0) as sqe from " + Db.getNomeTabR(tipoDoc);
        sql += " where id_padre = " + id;
        System.out.println("sql = " + sql);
        String evaso = "";
        try {
            List<Map> list = DbUtils.getListMap(Db.getConn(), sql);
            double sq = 0;
            double sqn = 0;
            double sqe = 0;
            double sqen = 0;
            boolean evasa = true;
            for (int row = 0; row < list.size(); row++) {
                Map m = list.get(row);
                sqn = CastUtils.toDouble0(m.get("sq"));
                sqen = CastUtils.toDouble0(m.get("sqe"));
                sq += sqn;
                sqe += sqen;
                if (sqn > 0 && sqen < sqn) {
                    evasa = false;
                }
            }
            if (evasa) {
                evaso = "S";
            } else if (sqe > 0) {
                evaso = "P";
            }
            sql = "update " + Db.getNomeTabT(tipoDoc) + " set evaso = '" + evaso + "' where id = " + id;
            System.out.println("sql = " + sql);
            DbUtils.tryExecQuery(Db.getConn(), sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Integer getIdDaNumero(String tipoDoc, String serie, Integer numero, Integer anno) {
        try {
            String sql = "select id from " + Db.getNomeTabT(tipoDoc);
            sql += " where serie = '" + Db.aa(serie) + "' and numero = " + numero + " and anno = " + anno;
            if (tipoDoc.equals(Db.TIPO_DOCUMENTO_SCONTRINO)) {
                sql += " and tipo_fattura = 7";
            } else if (tipoDoc.equals(Db.TIPO_DOCUMENTO_FATTURA)) {
                sql += " and tipo_fattura != 7";
            }
            return (Integer) DbUtils.getObject(Db.getConn(), sql);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getNumeroDaId(String tipoDocDest, Integer id) {
        return getNumeroDaId(tipoDocDest, id, true);
    }

    public static String getNumeroDaId(String tipoDocDest, Integer id, boolean includiTipoDoc) {
        try {
            String sql = "select numero, serie, anno from " + Db.getNomeTabT(tipoDocDest) + " where id = " + id;
            if (tipoDocDest.equals(Db.TIPO_DOCUMENTO_SCONTRINO)) {
                sql += " and tipo_fattura = 7";
            } else if (tipoDocDest.equals(Db.TIPO_DOCUMENTO_FATTURA)) {
                sql += " and tipo_fattura != 7";
            }
            System.out.println("sql = " + sql);
            List<Map> list = DbUtils.getListMap(Db.getConn(), sql);
            Map m = list.get(0);
            String ret = "";
            if (includiTipoDoc) {
                ret += Db.getDescTipoDocBrevissima(tipoDocDest);
            }
            if (CastUtils.toString(m.get("serie")).trim().equals("")) {
                ret += " " + m.get("numero");
            } else {
                ret += " " + m.get("serie") + "/" + m.get("numero");
            }
            return ret;
        } catch (Exception e) {
            System.out.println("errore in getNumeroDaId : " + tipoDocDest + " " + id);
            e.printStackTrace();
            return null;
        }
    }

    static public DefaultTableCellRenderer getFlagRender() {
        return new DefaultTableCellRenderer() {
            Color green = new java.awt.Color(200, 255, 200);
            Color dback = UIManager.getColor("Table.background");
            Color bmix = SwingUtils.mixColours(dback, green);
            DefaultTableCellRenderer r2 = new DefaultTableCellRenderer();
            Font font1 = UIManager.getFont("TextField.font");
            Font font2 = UIManager.getFont("TextField.font").deriveFont(UIManager.getFont("TextField.font").getSize() - 2f);

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                Component c2 = r2.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                dback = c2.getBackground();
                ((JLabel) c).setFont(font1);
                String conv = CastUtils.toString(value);
                String conv2 = CastUtils.toString(table.getValueAt(row, table.getColumn("convertito2").getModelIndex()));
                if (value.toString().length() > 0 || conv.trim().length() > 0 || conv2.trim().length() > 0) {
                    c.setBackground(SwingUtils.mixColours(dback, green));
                    if (!conv2.trim().equals("")) {
                        ((JLabel) c).setFont(font2);
                        ((JLabel) c).setText(StringUtils.replace(conv2, "\n", " - "));
                    } else {
                        ((JLabel) c).setText(conv);
                    }
                } else {
                    c.setBackground(dback);
                    ((JLabel) c).setText("");
                }
                return c;
            }
        };
    }

    static public SubstanceDefaultTableCellRenderer getFlagRenderSubstance() {
        return new org.jvnet.substance.SubstanceDefaultTableCellRenderer() {
            Font font1 = UIManager.getFont("TextField.font");
            Font font2 = UIManager.getFont("TextField.font").deriveFont(UIManager.getFont("TextField.font").getSize() - 2f);

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                ((JLabel) c).setFont(font1);
                String conv = CastUtils.toString(value);
                String conv2 = CastUtils.toString(table.getValueAt(row, table.getColumn("convertito2").getModelIndex()));
                if (value.toString().length() > 0 || conv.trim().length() > 0 || conv2.trim().length() > 0) {
                    c.setBackground(SwingUtils.mixColours(c.getBackground(), new java.awt.Color(200, 255, 200)));
                    if (conv2.trim().equals("")) {
                        ((JLabel) c).setText(conv);
                    } else {
                        ((JLabel) c).setFont(font2);
                        ((JLabel) c).setText(StringUtils.replace(conv2, "\n", " - "));
                    }
                }
                return c;
            }
        };
    }

    public static DefaultTableCellRenderer getEvasoRender() {
        return new DefaultTableCellRenderer() {
            DefaultTableCellRenderer r2 = new DefaultTableCellRenderer();

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lab = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                Component c2 = r2.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lab.setHorizontalAlignment(JLabel.CENTER);
                lab.setFont(UIManager.getFont("Label.font"));
                if (value == null) {
                    value = "";
                }
                try {
                    if (value.toString().equalsIgnoreCase("S")) {
                        lab.setText("Sì");
                        lab.setBackground(SwingUtils.mixColours(new java.awt.Color(200, 255, 200), c2.getBackground()));
                    } else if (value.toString().equalsIgnoreCase("P")) {
                        lab.setText("Parziale");
                        lab.setBackground(SwingUtils.mixColours(new java.awt.Color(255, 255, 200), c2.getBackground()));
                        lab.setFont(lab.getFont().deriveFont((float) lab.getFont().getSize() - 2f));
                    } else {
                        lab.setText("");
                        lab.setBackground(SwingUtils.mixColours(new java.awt.Color(255, 255, 255), c2.getBackground()));
                    }
                } catch (java.lang.NullPointerException errNull) {
                }
                return lab;
            }
        };
    }

    public static DefaultTableCellRenderer getEvasoRenderSubstance() {
        return new org.jvnet.substance.SubstanceDefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lab = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lab.setHorizontalAlignment(JLabel.CENTER);
                lab.setFont(UIManager.getFont("Label.font"));
                if (value == null) {
                    value = "";
                }
                try {
                    if (value.toString().equalsIgnoreCase("S")) {
                        lab.setText("Sì");
                        lab.setBackground(SwingUtils.mixColours(new java.awt.Color(200, 255, 200), lab.getBackground()));
                    } else if (value.toString().equalsIgnoreCase("P")) {
                        lab.setText("Parziale");
                        lab.setBackground(SwingUtils.mixColours(new java.awt.Color(255, 255, 200), lab.getBackground()));
                        lab.setFont(lab.getFont().deriveFont((float) lab.getFont().getSize() - 2f));
                    } else {
                        lab.setText("");
                        lab.setBackground(SwingUtils.mixColours(new java.awt.Color(255, 255, 255), lab.getBackground()));
                    }
                } catch (java.lang.NullPointerException errNull) {
                }
                return lab;
            }
        };
    }

    static public boolean isFunzioniManutenzione() {
        return main.getPadrePanel().menFunzioniManutenzione.isSelected();
    }

    public static void aggiornaElenchiFatture() {
        JInternalFrame[] iframes = main.getPadre().getDesktopPane().getAllFrames();
        for (JInternalFrame f : iframes) {
            if (f instanceof frmElenFatt) {
                ((frmElenFatt) f).dbRefresh();
            }
        }
    }

    public static void aggiornaListini() {
        System.out.println("aggiornaListini");
        try {
            List<Map> listini = DbUtils.getListMap(Db.getConn(), "select * from tipi_listino");
            for (Map rec : listini) {
                String sql = "insert ignore into articoli_prezzi (articolo, listino, prezzo) select codice, " + Db.pcs((String) rec.get("codice")) + " as listino, 0 as prezzo from articoli";
                System.out.println("aggiornaListini sql: " + sql);
                DbUtils.tryExecQuery(Db.getConn(), sql);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static InputStream caricaLogoDaDb(Connection conn) {
        return caricaLogoDaDb(conn, "logo");
    }

    public static InputStream caricaLogoDaDb(Connection conn, String campo) {
        ResultSet r = null;
        try {
            controllaAggiornamentoFileLogo(campo);
            r = DbUtils.tryOpenResultSet(conn, "select id, " + campo + " from dati_azienda");
            if (r.next()) {
                Blob blob = r.getBlob(campo);
                if (blob == null) {
                    return null;
                }
                return blob.getBinaryStream();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DbUtils.close(r);
        }
        return null;
    }

    public static byte[] caricaLogoDaDbBytes(Connection conn, String campo) {
        ResultSet r = null;
        try {
            controllaAggiornamentoFileLogo(campo);
            r = DbUtils.tryOpenResultSet(conn, "select id, " + campo + " from dati_azienda");
            if (r.next()) {
                Blob blob = r.getBlob(campo);
                if (blob == null) {
                    return null;
                }
                return IOUtils.toByteArray(blob.getBinaryStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DbUtils.close(r);
        }
        return null;
    }

    public static void salvaImgInDb(String file) {
        salvaImgInDb(file, "logo");
    }

    public static void salvaImgInDb(String file, String campo) {
        try {
            File fileLogo = new File(file);
            boolean stampareLogo = true;

            if (file != null && fileLogo.exists()) {
                System.out.println("salvo logo in db per file:" + file + " fileLogo.exist:" + fileLogo.exists());
                FileInputStream is = new FileInputStream(fileLogo);
                byte[] bb = new byte[(int) fileLogo.length()];
                is.read(bb);
                Statement s = Db.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet r = s.executeQuery("select id," + campo + ", " + campo + "_nome_file, " + campo + "_data_modifica, " + campo + "_dimensione from dati_azienda");
                if (r.next()) {
                    r.updateObject(campo, bb);
                    r.updateObject(campo + "_nome_file", file);
                    r.updateObject(campo + "_data_modifica", fileLogo.lastModified());
                    r.updateObject(campo + "_dimensione", fileLogo.length());
                    r.updateRow();
                }
                r.close();
                s.close();
            } else {
                System.out.println("azzero logo in db per file:" + file + " fileLogo.exist:" + fileLogo.exists());
                Statement s = Db.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet r = s.executeQuery("select id," + campo + ", " + campo + "_nome_file, " + campo + "_data_modifica, " + campo + "_dimensione from dati_azienda");
                if (r.next()) {
                    r.updateObject(campo, null);
                    r.updateObject(campo + "_nome_file", null);
                    r.updateObject(campo + "_data_modifica", null);
                    r.updateObject(campo + "_dimensione", null);
                    r.updateRow();
                }
                r.close();
                s.close();
            }
        } catch (Exception ex1) {
            ex1.printStackTrace();
        }
    }

    private static void controllaAggiornamentoFileLogo(String campo) {
        //logo_nome_file
        try {
            List<Map> lm = DbUtils.getListMap(Db.getConn(), "select id, " + campo + "_nome_file as nome_file, " + campo + "_data_modifica as data_modifica, " + campo + "_dimensione as dimensione from dati_azienda");
            Map m = lm.get(0);
            String nomefile = (String) m.get("nome_file");
            if (StringUtils.isNotBlank(nomefile)) {
                File test = new File(nomefile);
                if (test.exists()) {
                    //se trovo il file controllo se è cambiato
                    Long data_modifica = (Long) m.get("data_modifica");
                    Long dimensione = (Long) m.get("dimensione");
                    if (test.lastModified() > data_modifica || test.length() != dimensione) {
                        //aggiornare
                        InvoicexUtil.salvaImgInDb(test.getAbsolutePath(), campo);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String aggiungi_recapiti(String recapito_prec, boolean dest_diversa, ResultSet rCliente, ResultSet rDocu) {
        String pre = dest_diversa ? "dest_" : "";
        ResultSet r = dest_diversa ? rDocu : rCliente;
        try {
            String recapito = "";
            if (main.fileIni.getValueBoolean("pref", "stampaTelefono", false)) {
                if (it.tnx.Db.nz(r.getString(pre + "telefono"), "").length() > 0) {
                    recapito += "Tel. " + r.getString(pre + "telefono");
                }
            }

            if (main.fileIni.getValueBoolean("pref", "stampaCellulare", false)) {
                if (it.tnx.Db.nz(r.getString(pre + "cellulare"), "").length() > 0) {
                    if (recapito.length() > 0) {
                        recapito += " ";
                    }
                    recapito += "Cell. " + r.getString(pre + "cellulare");
                }
            }

            recapito = recapito_prec + (StringUtils.isBlank(recapito_prec) ? "" : "<br>") + recapito;
            return recapito;
        } catch (Exception e) {
            e.printStackTrace();
            return recapito_prec;
        }

    }

    public static void attendiCaricamentoPluginRitenute(final Runnable run) {
        if (main.fine_init_plugin) {
            run.run();
        } else {
            final JWindow flash = SwingUtils.showFlashMessage2("Attendere caricamento plugins", 5, null, Color.red, new Font(null, Font.BOLD, 16), true);
            Thread t = new Thread("attendere caricamento plugins") {
                @Override
                public void run() {
                    while (!main.fine_init_plugin) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(InvoicexUtil.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    run.run();
                    flash.dispose();
                }
            };
            t.start();
        }
    }

    public static String getIvaDefault() {
        try {
            return (String) DbUtils.getObject(Db.getConn(), "select codiceIvaDefault from dati_azienda limit 1");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getIvaSpese() {
        try {
            return (String) DbUtils.getObject(Db.getConn(), "select codiceIvaSpese from dati_azienda limit 1");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isPassaggio21eseguito() {
        try {
            String eseguito = CastUtils.toString(DbUtils.getObject(Db.getConn(), "select iva21eseguito from dati_azienda limit 1"));
            if (eseguito.equalsIgnoreCase("S")) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean isPassaggio22eseguito() {
        try {
            String eseguito = CastUtils.toString(DbUtils.getObject(Db.getConn(), "select iva22eseguito from dati_azienda limit 1"));
            if (eseguito.equalsIgnoreCase("S")) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }    

    public static String getIvaDefaultPassaggio() {
        String iva = getIvaDefault();
        if (StringUtils.isBlank(iva)) {
            if (isPassaggio22eseguito()) {
                return "22";
            } else {
                return "21";
            }
        } else {
            return iva;
        }
    }

    static public void checkSize(Window w) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension ss = toolkit.getScreenSize();
        System.out.println("checkSize ss = " + ss);
        Insets ds = toolkit.getScreenInsets(w.getGraphicsConfiguration());
        System.out.println("ds = " + ds);
        ss.setSize(ss.getWidth() - ds.left - ds.right, ss.getHeight() - ds.top - ds.bottom);
//        ss.setSize(1024, 600);
        if (w.getHeight() > ss.getHeight()) {
            w.setSize(w.getWidth(), (int) ss.getHeight());
        }
    }

    public static int getHeightIntFrame() {
        MenuPanel m = main.getPadrePanel();
        return (int) m.getDesktopPane().getVisibleRect().getHeight() - m.getNextFrameTop();
    }

    public static int getHeightIntFrame(int h) {
        MenuPanel m = main.getPadrePanel();
        if ((m.getDesktopPane().getVisibleRect().getHeight() - m.getNextFrameTop()) < h) {
            return (int) m.getDesktopPane().getVisibleRect().getHeight() - m.getNextFrameTop();
        } else {
            return h;
        }
    }

    public static Rectangle getDesktopSize() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension ss = toolkit.getScreenSize();
        Insets ds = toolkit.getScreenInsets(new Frame().getGraphicsConfiguration());
        return new Rectangle((int) ss.getWidth() - ds.left - ds.right, (int) ss.getHeight() - ds.top - ds.bottom);
    }

    public static Point getDesktopTopLeft() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Insets ds = toolkit.getScreenInsets(new Frame().getGraphicsConfiguration());
        return new Point(ds.top, ds.left);
    }

    static public double calcolaPrezzoArrotondato(double prezzo, double parametro, boolean inf) {
        double res = 0d;
        if (parametro == 0d) {
            return prezzo;
        } else if (inf) {
            res = (Math.floor(prezzo / parametro)) * parametro;
        } else {
            res = (Math.ceil(prezzo / parametro)) * parametro;
        }

        return res;
    }

    public static void aggiornaTotaliRighe(String tipoDoc, int id) {
        aggiornaTotaliRighe(tipoDoc, id, null);
    }

    public static void aggiornaTotaliRighe(String tipoDoc, int id, Boolean is_prezzi_ivati) {
        String tabt = Db.getNomeTabT(tipoDoc);
        String tabr = Db.getNomeTabR(tipoDoc);
        System.out.println("aggiornaTotaliRighe tipoDoc:" + tipoDoc + " id:" + id);

        String sql0 = "UPDATE " + tabr + " set arrotondamento_parametro = '0' where arrotondamento_parametro is null or arrotondamento_parametro = ''";
        System.out.println("sql0 = " + sql0);
        try {
            DbUtils.tryExecQuery(Db.getConn(), sql0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (is_prezzi_ivati == null) {
            try {
                String prezzi_ivati = CastUtils.toString(DbUtils.getObject(Db.getConn(), "select prezzi_ivati from " + tabt + " where id = " + id));
                is_prezzi_ivati = !prezzi_ivati.equalsIgnoreCase("N");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!is_prezzi_ivati) {
            String sql1 = "UPDATE " + tabr + " set totale_imponibile = ROUND(CAST(  IF(arrotondamento_parametro = '0',  (prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - ((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)))*(IFNULL(sconto2, 0.00)/100))) * IFNULL(quantita, 0.00), IF(arrotondamento_tipo = 'Inf.',   FLOOR(((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - ((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)))*(IFNULL(sconto2, 0.00)/100))) )/CAST(arrotondamento_parametro AS DECIMAL(5,2))) * CAST(arrotondamento_parametro AS DECIMAL(5,2)) * IFNULL(quantita, 0.00), CEIL(((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - ((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)))*(IFNULL(sconto2, 0.00)/100))) )/CAST(arrotondamento_parametro AS DECIMAL(5,2))) * CAST(arrotondamento_parametro AS DECIMAL(5,2)) * IFNULL(quantita, 0.00))) AS DECIMAL(10,2)),2)";
            sql1 += " where id_padre = " + id;
            System.out.println("sql1 = " + sql1);
            try {
                DbUtils.tryExecQuery(Db.getConn(), sql1);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String sql2 = "UPDATE " + tabr + " r LEFT JOIN codici_iva i ON r.iva = i.codice SET totale_ivato = round(totale_imponibile + (totale_imponibile * i.percentuale/100),2)";
            sql2 += " where id_padre = " + id;
            System.out.println("sql2 = " + sql2);
            try {
                DbUtils.tryExecQuery(Db.getConn(), sql2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String sql1 = "UPDATE " + tabr + " set totale_ivato = ROUND(CAST(  IF(arrotondamento_parametro = '0',  (prezzo_ivato - (prezzo_ivato*(IFNULL(sconto1, 0.00)/100)) - ((prezzo_ivato - (prezzo_ivato*(IFNULL(sconto1, 0.00)/100)))*(IFNULL(sconto2, 0.00)/100))) * IFNULL(quantita, 0.00), IF(arrotondamento_tipo = 'Inf.',   FLOOR(((prezzo_ivato - (prezzo_ivato*(IFNULL(sconto1, 0.00)/100)) - ((prezzo_ivato - (prezzo_ivato*(IFNULL(sconto1, 0.00)/100)))*(IFNULL(sconto2, 0.00)/100))) )/CAST(arrotondamento_parametro AS DECIMAL(5,2))) * CAST(arrotondamento_parametro AS DECIMAL(5,2)) * IFNULL(quantita, 0.00), CEIL(((prezzo_ivato - (prezzo_ivato*(IFNULL(sconto1, 0.00)/100)) - ((prezzo_ivato - (prezzo_ivato*(IFNULL(sconto1, 0.00)/100)))*(IFNULL(sconto2, 0.00)/100))) )/CAST(arrotondamento_parametro AS DECIMAL(5,2))) * CAST(arrotondamento_parametro AS DECIMAL(5,2)) * IFNULL(quantita, 0.00))) AS DECIMAL(10,2)),2)";
            sql1 += " where id_padre = " + id;
            System.out.println("sql1 = " + sql1);
            try {
                DbUtils.tryExecQuery(Db.getConn(), sql1);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String sql2 = "UPDATE " + tabr + " r LEFT JOIN codici_iva i ON r.iva = i.codice SET totale_imponibile = round(totale_ivato - round(totale_ivato * i.percentuale / (100 + i.percentuale),2),2)";
            sql2 += " where id_padre = " + id;
            System.out.println("sql2 = " + sql2);
            try {
                DbUtils.tryExecQuery(Db.getConn(), sql2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static List<JInternalFrame> getFrames(Class aClass) {
        JInternalFrame[] frames = main.getPadre().getDesktopPane().getAllFrames();
        List list = new ArrayList();
        for (JInternalFrame f : frames) {
            if (f.getClass().getName().equals(aClass.getName())) {
                list.add(f);
            }
        }
        return list;
    }

    public static void macButtonSmall(JButton but) {
        but.putClientProperty("JComponent.sizeVariant", "small");
        but.putClientProperty("JButton.buttonType", "textured");
    }

    public static void macButtonRegular(JButton but) {
        but.putClientProperty("JComponent.sizeVariant", "regular");
        but.putClientProperty("JButton.buttonType", "textured");
    }

    public static void macButtonGradient(JButton but) {
        but.putClientProperty("JButton.buttonType", "gradient");
    }

    public static void riportaLotti(JTable table, String tabellaDest, String tabellaProvRighe, String tipodoc_a, JFrame parent) {
        JTable tab = table;
        String sql = "";
        int col_lotti = tab.getColumn("gestione_lotti").getModelIndex();
        int col_art = tab.getColumn("articolo").getModelIndex();
        int col_qta_conf = tab.getColumn("quantità confermata").getModelIndex();
        col_lotti = tab.convertColumnIndexToView(col_lotti);
        col_art = tab.convertColumnIndexToView(col_art);
        col_qta_conf = tab.convertColumnIndexToView(col_qta_conf);
        String toadd = "";
        String tabd = "";
        String tipo_mov = "S";
        if (tipodoc_a.equalsIgnoreCase(Db.TIPO_DOCUMENTO_DDT)) {
            tabd = "righ_ddt";
        } else if (tipodoc_a.equalsIgnoreCase(Db.TIPO_DOCUMENTO_DDT_ACQUISTO)) {
            tipo_mov = "C";
            tabd = "righ_ddt_acquisto";
        } else if (tipodoc_a.equalsIgnoreCase(Db.TIPO_DOCUMENTO_FATTURA)) {
            tabd = "righ_fatt";
        } else if (tipodoc_a.equalsIgnoreCase(Db.TIPO_DOCUMENTO_FATTURA_RICEVUTA)) {
            tipo_mov = "C";
            tabd = "righ_fatt_acquisto";
        }
        for (int i = 0; i < table.getRowCount(); i++) {
            toadd = "";
            //modificare le quantita sul documento generato
            Integer tid_prov = CastUtils.toInteger(table.getValueAt(i, table.getColumn("prov_id").getModelIndex()));
            Integer rid_prov = CastUtils.toInteger(table.getValueAt(i, table.getColumn("prov_id_riga").getModelIndex()));
            Integer tid_dest = CastUtils.toInteger(table.getValueAt(i, table.getColumn("dest_id").getModelIndex()));
            Integer rid_dest = CastUtils.toInteger(table.getValueAt(i, table.getColumn("dest_id_riga").getModelIndex()));

            double qtaconf = CastUtils.toDouble0(table.getValueAt(i, table.getColumn("quantità confermata").getModelIndex()));
            double qta = CastUtils.toDouble0(table.getValueAt(i, table.getColumn("quantità").getModelIndex()));
            String codart = CastUtils.toString(table.getValueAt(i, table.getColumn("articolo").getModelIndex()));
            //azzero eventuali
            sql = "delete from righ_" + tabellaDest + "_lotti where id_padre = " + rid_dest;
            //porto controllando le quantità (per ora non possibile evasioe parziale di articoli con lotti, o completa o zero
            if (qtaconf > 0) {
                double qta_da_togliere = qtaconf;
                //nelle tabelle _lotti il campo id_padre si riferisce all' id della RIGA del documento padre
                sql = "select * from " + tabellaProvRighe + "_lotti where id_padre = " + rid_prov;
                try {
                    List<Map> lotti_prov = DbUtils.getListMap(Db.getConn(), sql);
                    DebugFastUtils.dump(lotti_prov);
                    for (Map rec : lotti_prov) {
                        Map m = new HashMap();
                        m.put("id_padre", rid_dest);
                        m.put("lotto", rec.get("lotto"));
                        m.put("codice_articolo", rec.get("codice_articolo"));
                        double qta_orig = CastUtils.toDouble0(rec.get("qta"));
                        if (qta_orig <= qta_da_togliere) {
                            m.put("qta", qta_orig);
                            qta_da_togliere -= qta_orig;
                        } else {
                            m.put("qta", qta_da_togliere);
                            qta_da_togliere = 0;
                        }
                        sql = "insert into righ_" + tabellaDest + "_lotti set " + DbUtils.prepareSqlFromMap(m);
                        try {
                            DbUtils.tryExecQuery(Db.getConn(), sql);
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                        if (qta_da_togliere <= 0) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //chiedo quantità
                //controllo lotti
                Integer id_riga = rid_dest;
                String codice = (String) tab.getValueAt(i, col_art);
                /*
                 10/07/2013
                 * per la trasformazione da prev/ord a DDT prima chiedeva i lotti solo quando la qta ceonfermata era minore dalla quantià
                 * da adesso si mette che se c'è i lotti la richiede sempre
                 */
                if (CastUtils.toString(tab.getValueAt(i, col_lotti)).equalsIgnoreCase("S")
                        && qta > 0 && qtaconf > 0 && (qtaconf != qta || tabellaProvRighe.indexOf("ordi") >= 0)) {
                    //chiedere lotti
                    SwingUtils.showInfoMessage(parent, "Definire i lotti per l'articolo '" + codice + "' qta confermata " + qtaconf + " su " + qta);

                    ArrayList<String> alotti = null;
                    ArrayList<Double> alottiqta = null;

                    JDialogLotti dialog = new JDialogLotti(main.getPadreFrame(), true, false);
                    dialog.setLocationRelativeTo(null);
                    dialog.init(tipo_mov, CastUtils.toDouble0(qta), codice, tabd + "_lotti", id_riga, null);
                    dialog.setVisible(true);
                    System.out.println("lotti ok");

                    //aggiungo lotti in descrizione riga
                    System.out.println("id_riga : " + id_riga);
                    alotti = dialog.getLotti();
                    alottiqta = dialog.getLottiQta();
                    int ilotti = 0;
                    for (String m : alotti) {
//                        toadd += "\nLotto: " + m;
                        toadd += "\nLotto: " + m + " (" + FormatUtils.formatNum0_5Dec(alottiqta.get(ilotti)) + ")";
                        ilotti++;
                    }
                    frmNuovRigaDescrizioneMultiRigaNew.toglieLotti(tabd, id_riga);
                    sql = "update " + tabd + " set descrizione = CONCAT(descrizione,'" + StringUtils.replace(toadd, "'", "\\'") + "') where id = " + id_riga;
                    System.out.println("sql = " + sql);
                    Db.executeSql(sql, true);

                }

            }
        }
    }

    public static void deactivateComponent(Component comp) {
        if (comp instanceof JScrollPane) {
            JScrollPane pane = (JScrollPane) comp;
            for (Component compPane : pane.getComponents()) {
                deactivateComponent(compPane);
            }
        } else if (comp instanceof JViewport) {
            JViewport viewPort = (JViewport) comp;
            for (Component compPane : viewPort.getComponents()) {
                deactivateComponent(compPane);
            }
        } else if (comp instanceof JTabbedPane) {
            JTabbedPane tpane = (JTabbedPane) comp;
            for (Component compPane : tpane.getComponents()) {
                deactivateComponent(compPane);
            }
        } else if (comp instanceof JPanel) {
            JPanel panel = (JPanel) comp;
            for (Component compPane : panel.getComponents()) {
                deactivateComponent(compPane);
            }
        } else {
            comp.setEnabled(false);
        }
    }

    public static String md5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5"); //or "SHA-1"
            md.update(s.getBytes());
            BigInteger hash = new BigInteger(1, md.digest());
            String md5 = hash.toString(16);
            while (md5.length() < 32) {
                md5 = "0" + md5;
            }
            return md5;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<ArrayList> checkBarcodeList(final int idDocumento, final String tipoDocumento, final boolean acquisto) {
        try {
            String table = "";
            if (tipoDocumento.equals(Db.TIPO_DOCUMENTO_DDT)) {
                table = "righ_ddt";
                table = acquisto ? table + "_acquisto" : table;
            } else if (tipoDocumento.equals(Db.TIPO_DOCUMENTO_FATTURA)) {
                table = "righ_fatt";
                table = acquisto ? table + "_acquisto" : table;
            } else if (tipoDocumento.equals(Db.TIPO_DOCUMENTO_ORDINE)) {
                table = "righ_ordi";
                table = acquisto ? table + "_acquisto" : table;
            } else if (tipoDocumento.equals(Db.TIPO_DOCUMENTO_ORDINE_ACQUISTO)) {
                table = "righ_ordi_acquisto";
            } else if (tipoDocumento.equals(Db.TIPO_DOCUMENTO_DDT_ACQUISTO)) {
                table = "righ_ddt_acquisto";
            } else if (tipoDocumento.equals(Db.TIPO_DOCUMENTO_FATTURA_RICEVUTA)) {
                table = "righ_fatt_acquisto";
            }

            String campi = "";
            if (main.fileIni.getValueBoolean("barcode", "stampa_cod_articolo", true)) {
                campi += "art.codice,";
            }
            if (main.fileIni.getValueBoolean("barcode", "stampa_prezzo_articolo", true)) {
                if (main.fileIni.getValueBoolean("barcode", "iva_inclusa", true)) {
                    campi += "rig.prezzo_ivato as prezzo,";
                } else {
                    campi += "rig.prezzo as prezzo,";
                }
            }

            String sql = "SELECT " + campi + " art.codice_a_barre as barcode, SUM(rig.quantita) as quantita FROM " + table + " rig LEFT JOIN articoli art ON rig.codice_articolo = art.codice WHERE rig.id_padre = " + Db.pc(idDocumento, Types.INTEGER) + " AND art.codice_a_barre != '' GROUP BY art.codice_a_barre";
            ResultSet codici = Db.openResultSet(Db.getConn(), sql);

            int conta = 0;
            ArrayList<ArrayList> res = new ArrayList<ArrayList>();
            while (codici.next()) {
                String barcode = codici.getString("barcode");
                Double qtaDb = codici.getDouble("quantita");

                String codiceArticolo = "";

                if (!main.fileIni.getValueBoolean("barcode", "per_quantita", false)) {
                    if (main.fileIni.getValueBoolean("barcode", "stampa_qta_articolo", true)) {
                        String qta = String.valueOf(CastUtils.toInteger(codici.getDouble("quantita")));
                        codiceArticolo = qta + " x ";
                    }
                }

                if (main.fileIni.getValueBoolean("barcode", "stampa_cod_articolo", true)) {
                    codiceArticolo += codici.getString("codice");
                }

                if (main.fileIni.getValueBoolean("barcode", "stampa_prezzo_articolo", true)) {
                    String prezzo = "€ " + String.valueOf(codici.getDouble("prezzo"));
                    codiceArticolo += codiceArticolo.equals("") ? prezzo : " - " + prezzo;
                }



                ArrayList temp = new ArrayList();

                temp.add(codiceArticolo);
                temp.add(barcode);
                temp.add(qtaDb);

                res.add(temp);
            }

            return res.size() > 0 ? res : null;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void msgNew(final JInternalFrame frame, final tnxDbPanel dati, final IFunction fun, final JComponent compfocus, final String messaggio) {
        if (dati.isOnSomeRecord == false) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    long ms = System.currentTimeMillis();
                    while (!frame.isVisible() || System.currentTimeMillis() - ms > 5000) {
                        try {
                            Thread.sleep(500);
                        } catch (Exception ex) {
                        }
                    }
                    if (frame.isVisible()) {
                        SwingUtils.inEdt(new Runnable() {
                            public void run() {
                                JComponent compfocus2 = null;
                                javax.swing.JOptionPane.showMessageDialog(frame, messaggio, "Attenzione", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                                if (fun != null) {
                                    fun.run();
                                } else {
                                    //cerco l'action del pulsante new
                                    if (dati.butNew != null) {
                                        ActionListener[] lists = dati.butNew.getActionListeners();
                                        for (ActionListener l : lists) {
                                            l.actionPerformed(null);
                                        }
                                    }
                                }
                                if (compfocus == null) {
                                    //cerco id o codice
                                    Component[] comps = dati.getComponents();
                                    for (Component c : comps) {
                                        if (c instanceof tnxTextField) {
                                            String campo = ((tnxTextField) c).getDbNomeCampo();
                                            if (campo.equalsIgnoreCase("data")
                                                    || campo.equalsIgnoreCase("codice")
                                                    || campo.equalsIgnoreCase("id")
                                                    || campo.equalsIgnoreCase("codice_articolo")
                                                    || campo.equalsIgnoreCase("abi")
                                                    || campo.equalsIgnoreCase("descrizione")) {
                                                if (((tnxTextField) c).isEditable() && ((tnxTextField) c).isEnabled()) {
                                                    compfocus2 = (JComponent) c;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    compfocus2 = compfocus;
                                }
                                if (compfocus2 != null) {
                                    compfocus2.requestFocus();
                                    FxUtils.fadeBackground(compfocus2, Color.red);
                                }
                            }
                        });
                    }
                }
            };
            t.start();
        }

    }

    static public TableCellRenderer getNumber0_5Renderer() {
        if (numberRenderer0_5 == null) {
            if (!isSubstance()) {
                numberRenderer0_5 = new DefaultTableCellRenderer() {
                    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                        Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                        if (comp instanceof JLabel) {
                            JLabel lab = (JLabel) comp;
                            lab.setForeground(UIManager.getColor("Text.foreground"));
                            Color csel = UIManager.getColor("Table.selectionForeground");
                            Color cnosel = UIManager.getColor("Table.foreground");
                            Color c = null;
                            if (isSelected) {
                                c = csel;
                            } else {
                                c = cnosel;
                            }
                            lab.setForeground(c);
                            if (value != null) {
                                try {
                                    //double d = (Double.valueOf(value.toString())).doubleValue();
                                    double d = CastUtils.toDouble0All(value);
                                    NumberFormat form = DecimalFormat.getInstance(Locale.ITALIAN);
                                    form.setGroupingUsed(true);
                                    form.setMaximumFractionDigits(5);
                                    form.setMinimumFractionDigits(0);
                                    lab.setHorizontalAlignment(SwingConstants.RIGHT);
                                    lab.setText(form.format(d));
                                    if (d < 0) {
                                        if ((c.getRed() + c.getGreen() + c.getBlue()) / 3 > 125) {
                                            lab.setForeground(new Color(255, 180, 180));
                                        } else {
                                            lab.setForeground(Color.RED);
                                        }
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                            return lab;
                        }
                        return comp;
                    }
                };
            } else {
                numberRenderer0_5 = new org.jvnet.substance.SubstanceDefaultTableCellRenderer() {
                    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                        Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                        if (comp instanceof JLabel) {
                            JLabel lab = (JLabel) comp;
                            if (value != null) {
                                try {
                                    double d = (Double.valueOf(value.toString())).doubleValue();
                                    NumberFormat form = DecimalFormat.getInstance(Locale.ITALIAN);
                                    form.setGroupingUsed(true);
                                    form.setMaximumFractionDigits(5);
                                    form.setMinimumFractionDigits(0);
                                    lab.setHorizontalAlignment(SwingConstants.RIGHT);
                                    lab.setText(form.format(d));
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                            return lab;
                        }
                        return comp;
                    }
                };

            }
        }
        return numberRenderer0_5;
    }

    static public boolean isSubstance() {
        if (substance == null) {
            if (UIManager.getLookAndFeel().getName().toLowerCase().indexOf("substance") >= 0) {
                substance = true;
            } else {
                substance = false;
            }
        }
        return substance;
    }

    public static void aggiornaRiferimentoDocumenti(String tipo_doc, Integer id) {
        if (id == null) {
            System.out.println("non aggiornaRiferimentoDocumenti perchè id:" + id);
            return;
        }
        //!!!!! riportare in invoicexUtil e farlo anche per ddt collegati, poi su ddt farlo per le fatture collegate e su ordini farlo per ddt e fatture collegate        
        String sql = null;
        String nome_tab_righ_origine = Db.getNomeTabR(tipo_doc);
        boolean acquisto = nome_tab_righ_origine.endsWith("_acquisto") ? true : false;
        String suffisso = acquisto ? "_acquisto" : "";
        if (tipo_doc.equals(Db.TIPO_DOCUMENTO_DDT) || tipo_doc.equals(Db.TIPO_DOCUMENTO_DDT_ACQUISTO)
                || tipo_doc.equals(Db.TIPO_DOCUMENTO_FATTURA) || tipo_doc.equals(Db.TIPO_DOCUMENTO_FATTURA_RICEVUTA)) {
            try {
                List<Map> list1 = DbUtils.getListMap(Db.getConn(), "select da_ordi from " + nome_tab_righ_origine + " where id_padre = " + id + " and da_ordi is not null group by da_ordi");
                for (Map m1 : list1) {
                    //"select anno, numero, serie, id, id_padre, da_ordi from righ_fatt where da_ordi = 22"
                    System.out.println("devo aggiornare l'ordine id:" + m1.get("da_ordi"));
                    String convertito = "";
                    List<Map> list2 = DbUtils.getListMap(Db.getConn(), "select anno, numero, serie, id, id_padre, da_ordi from righ_fatt" + suffisso + " where da_ordi = " + m1.get("da_ordi") + " group by id_padre");
                    for (Map m2 : list2) {
                        System.out.println("l'ordine id:" + m1.get("da_ordi") + " è in questa fattura:" + m2.get("anno") + "/" + m2.get("numero") + "/" + m2.get("serie") + " id:" + m2.get("id_padre"));
                        convertito += (convertito.length() > 0 ? "\n" : "") + InvoicexUtil.getNumeroDaId(acquisto ? Db.TIPO_DOCUMENTO_FATTURA_RICEVUTA : Db.TIPO_DOCUMENTO_FATTURA, CastUtils.toInteger(m2.get("id_padre")), true);
                    }
                    list2 = DbUtils.getListMap(Db.getConn(), "select anno, numero, serie, id, id_padre, da_ordi from righ_ddt" + suffisso + " where da_ordi = " + m1.get("da_ordi") + " group by id_padre");
                    for (Map m2 : list2) {
                        System.out.println("l'ordine id:" + m1.get("da_ordi") + " è in questo ddt:" + m2.get("anno") + "/" + m2.get("numero") + "/" + m2.get("serie") + " id:" + m2.get("id_padre"));
                        convertito += (convertito.length() > 0 ? "\n" : "") + InvoicexUtil.getNumeroDaId(acquisto ? Db.TIPO_DOCUMENTO_DDT_ACQUISTO : Db.TIPO_DOCUMENTO_DDT, CastUtils.toInteger(m2.get("id_padre")), true);
                    }
                    sql = "update test_ordi" + suffisso + " t";
                    sql += " set convertito = " + Db.pc(convertito, "VARCHAR");
                    sql += " where id = " + m1.get("da_ordi");
                    System.out.println("sql = " + sql);
                    DbUtils.tryExecQuery(Db.getConn(), sql);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (tipo_doc.equals(Db.TIPO_DOCUMENTO_FATTURA) || tipo_doc.equals(Db.TIPO_DOCUMENTO_FATTURA_RICEVUTA)) {
            try {
                List<Map> list1 = DbUtils.getListMap(Db.getConn(), "select da_ddt from " + nome_tab_righ_origine + " where id_padre = " + id + " and da_ddt is not null group by da_ddt");
                for (Map m1 : list1) {
                    System.out.println("devo aggiornare il ddt id:" + m1.get("da_ddt"));
                    String convertito = "";
                    List<Map> list2 = DbUtils.getListMap(Db.getConn(), "select anno, numero, serie, id, id_padre, da_ddt from " + nome_tab_righ_origine + " where da_ddt = " + m1.get("da_ddt") + " group by id_padre");
                    for (Map m2 : list2) {
                        System.out.println("il ddt id:" + m1.get("da_ddt") + " è in questa fattura:" + m2.get("anno") + "/" + m2.get("numero") + "/" + m2.get("serie") + " id:" + m2.get("id_padre"));
                        convertito += (convertito.length() > 0 ? "\n" : "") + InvoicexUtil.getNumeroDaId(tipo_doc, CastUtils.toInteger(m2.get("id_padre")), false);
                    }
                    sql = "update test_ddt" + suffisso + " t";
                    sql += " set convertito = " + Db.pc(convertito, "VARCHAR");
                    sql += " where id = " + m1.get("da_ddt");
                    System.out.println("sql = " + sql);
                    DbUtils.tryExecQuery(Db.getConn(), sql);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static int getTipoNumerazione() {
        if (tipoNumerazione == null) {
            try {
                tipoNumerazione = CastUtils.toInteger0(DbUtils.getObject(Db.getConn(), "select tipo_numerazione from dati_azienda"));
            } catch (Exception ex) {
                tipoNumerazione = 0;
                ex.printStackTrace();
            }
        }
        return tipoNumerazione;
    }

    public static boolean isSceltaTipoNumerazioneEseguita() {
        try {
            int eseguito = CastUtils.toInteger0(DbUtils.getObject(Db.getConn(), "select tipo_numerazione_confermata from dati_azienda limit 1"));
            if (eseguito == 1) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean generareMovimenti(int tipo_fattura, Component parent) {
        boolean genera = false;

        int generazione_movimenti = Integer.parseInt(main.fileIni.getValue("pref", "generazione_movimenti", "0"));
        if (tipo_fattura == dbFattura.TIPO_FATTURA_NOTA_DI_CREDITO) {
            if (SwingUtils.showYesNoMessage(parent, "Vuoi generare i movimenti di carico magazzino ?")) {
                genera = true;
            }
        } else {
            if (generazione_movimenti == 0) {
                //standard genera sempre
                //su proforma no..
                if (tipo_fattura == dbFattura.TIPO_FATTURA_IMMEDIATA || tipo_fattura == dbFattura.TIPO_FATTURA_ACCOMPAGNATORIA) {
                    genera = true;
                }
                if (main.getPersonalContain("movimenti_su_proforma") && tipo_fattura == dbFattura.TIPO_FATTURA_PROFORMA) {
                    genera = true;
                }
            } else if (generazione_movimenti == 1) {
                //genera solo per accompagnatoria
                if (tipo_fattura == dbFattura.TIPO_FATTURA_ACCOMPAGNATORIA) {
                    genera = true;
                }
                if (main.getPersonalContain("movimenti_su_proforma") && tipo_fattura == dbFattura.TIPO_FATTURA_PROFORMA) {
                    genera = true;
                }
            } else {
                //genera solo per accompagnatoria ma chiede per immediata
                if (tipo_fattura == dbFattura.TIPO_FATTURA_IMMEDIATA) {
                    if (SwingUtils.showYesNoMessage(parent, "Vuoi generare i movimenti di magazzino ?")) {
                        genera = true;
                    }
                } else if (tipo_fattura == dbFattura.TIPO_FATTURA_ACCOMPAGNATORIA) {
                    genera = true;
                }
                if (main.getPersonalContain("movimenti_su_proforma") && tipo_fattura == dbFattura.TIPO_FATTURA_PROFORMA) {
                    genera = true;
                }
            }
        }

        return genera;
    }

    public static void aggiornaGiacenzeArticoli() {
        ArrayList<Giacenza> giacenze = Magazzino.getGiacenza(null);

        MicroBench mb = new MicroBench(true);
        String sql = "";
        int conta = 0;
        DbUtils.debug = false;
        Map<String, Double> giac_per_codice = new HashMap();
        for (Giacenza g : giacenze) {
            if (g.getCodice_articolo().equals("Y7693")) {
                System.out.println("debug");
            }
            conta++;
            giac_per_codice.put(g.getCodice_articolo(), g.getGiacenza());
            sql += "update articoli set disponibilita_reale = " + Db.pc(g.getGiacenza(), Types.DOUBLE) + ", disponibilita_reale_ts = NOW() where codice = " + Db.pc(g.getCodice_articolo(), Types.VARCHAR) + ";\n";
            if (conta == 100) {
                try {
                    DbUtils.tryExecQuery(Db.getConn(), sql);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                conta = 0;
                sql = "";
            }
        }
        if (conta != 100) {
            try {
                DbUtils.tryExecQuery(Db.getConn(), sql);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //controllo articoli con giacenza diversa da zero
        try {
            List<Map> elenco = DbUtils.getListMap(Db.getConn(), "select codice, disponibilita_reale from articoli where IFNULL(disponibilita_reale,0) != 0");
            for (Map rec : elenco) {
                try {
                    String codice = cu.toString(rec.get("codice"));
                    double giac_db = cu.toDouble0(rec.get("disponibilita_reale"));
                    //se non trovo in giacenze calcolate prima la metto a zero
                    if (!giac_per_codice.keySet().contains(codice)) {
                        System.out.println("!!! giacenza in db: " + giac_db + " ma non trovato nelle giacenze calcolate quindi metto a zero");
                        sql = "update articoli set disponibilita_reale = 0, disponibilita_reale_ts = NOW() where codice = " + Db.pc(codice, Types.VARCHAR);
                        DbUtils.tryExecQuery(Db.getConn(), sql);
                    } else {
                        //se la trovo la confronto, se diversa la reimposto
                        Double giac_calc = giac_per_codice.get(codice);
                        if (giac_db != giac_calc) {
                            System.out.println("!!! giacenza in db: " + giac_db + " diversa da calcolata: " + giac_calc);
                            sql = "update articoli set disponibilita_reale = " + Db.pc(giac_calc, Types.DOUBLE) + ", disponibilita_reale_ts = NOW() where codice = " + Db.pc(codice, Types.VARCHAR);
                            DbUtils.tryExecQuery(Db.getConn(), sql);
                        }
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        DbUtils.debug = true;

        mb.out("locale");

        /*
         //via wsd
         TrustManager[] trustAllCerts = new TrustManager[]{
         new X509TrustManager() {
         public java.security.cert.X509Certificate[] getAcceptedIssuers() {
         return null;
         }

         public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
         }

         public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
         }

         public boolean isClientTrusted(X509Certificate[] xcs) {
         return true;
         }

         public boolean isServerTrusted(X509Certificate[] xcs) {
         return true;
         }
         }
         };
         try {
         SSLContext sc = SSLContext.getInstance("SSL");
         sc.init(null, trustAllCerts, new java.security.SecureRandom());
         HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
         } catch (Exception e) {
         System.out.println(e);
         }


         URLCodec u = new URLCodec();
         String url = getUrlWsd();
         url += "&f=aggiorna_disponibilita_reale";
         System.out.println("url = " + url);

         try {
         HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
         conn.setDoOutput(true);
         conn.setRequestMethod("POST");
         String post = "server=" + u.encode(main.fileIni.getValue("db", "server"));
         post += "&user=" + u.encode(main.fileIni.getValue("db", "user"));
         post += "&pass=" + u.encode(main.fileIni.getValueCifrato("db", "pwd"));
         post += "&database=" + u.encode(main.fileIni.getValue("db", "nome_database"));
         post += "&ssh=" + u.encode(main.fileIni.getValue("db", "ssh"));
         post += "&ssh_server=" + u.encode(main.fileIni.getValue("db", "ssh_hostname"));
         //conversione da java a php via json
         JSONArray list = new JSONArray();
         for (Giacenza g : giacenze) {
         //                JSONObject jo = new JSONObject();
         HashMap jo = new HashMap();
         jo.put("c", g.getCodice_articolo());
         jo.put("q", g.getGiacenza());
         list.add(jo);
         }
         System.out.println("list = " + list);
         System.out.println("list encode = " + u.encode(list.toString()));
         post += "&lista=" + u.encode(list.toString());

         conn.setRequestProperty("Content-Length", "" + post.length());

         OutputStreamWriter outputWriter = new OutputStreamWriter(conn.getOutputStream());
         outputWriter.write(post.toString());
         outputWriter.flush();
         outputWriter.close();

         int retcode = conn.getResponseCode();
         long lastm = conn.getLastModified();
         int size = conn.getContentLength();
         System.out.println(conn.getContentType());
         String xinvoicex = conn.getHeaderField("X-Invoicex");
         System.out.println("xinvoicex = " + xinvoicex);

         if (retcode != 200) {
         System.out.println("getURL: errore retcode:" + retcode + " resp:" + conn.getResponseMessage());
         return;
         }
         InputStream is = new BufferedInputStream(conn.getInputStream());
         int readed = 0;
         int read = 0;
         byte[] buff = new byte[10000];
         String out = "";
         boolean errors = false;
         while ((read = is.read(buff)) > 0) {
         String tmp = new String(buff, 0, read);
         System.out.println("tmp = " + tmp);
         out += tmp;
         readed += read;
         try {
         String[] tmps = StringUtils.split(tmp, '\n');
         tmp = tmps[0];
         if (tmp.startsWith("p:")) {
         final int progresso = CastUtils.toInteger0(StringUtils.substringAfter(tmp, "p:"));
         SwingUtils.inEdt(new Runnable() {
         public void run() {
         System.out.println("aggiornamento in corso " + progresso + "%");
         //                                panelnews_f.link.setText("aggiornamento in corso " + progresso + "%");
         }
         });
         } else {
         mb.out("remoto");
         if (xinvoicex == null) {
         //errore php
         System.out.println("out = " + out);
         String noHTMLString = out.replaceAll("\\<.*?\\>", "");
         System.out.println("noHTMLString = " + noHTMLString);
         SwingUtils.showErrorMessage(main.getPadreWindow(), "Errore durante l'aggiornamento:\n" + noHTMLString);
         } else {
         SwingUtils.showErrorMessage(main.getPadreWindow(), "Errore durante l'aggiornamento:\n" + tmp);
         }

         errors = true;
         }
         } catch (Exception e) {
         e.printStackTrace();
         }
         }
         is.close();
         mb.out("remoto");

         if (!errors) {
         SwingUtils.inEdt(new Runnable() {
         public void run() {
         System.out.println("aggiornamento completato");
         //                        panelnews_f.link.setText("aggiornamento completato");
         }
         });
         }
         } catch (Exception e) {
         e.printStackTrace();
         }
         */

    }

    public static void aggiornaPrezziNettiUnitari(String tabrighe, String tabtest) {
        String sql = "update " + tabrighe + " r join " + tabtest + " t on r.id_padre = t.id "
                + "set r.prezzo_netto_unitario = IFNULL(r.prezzo,0) * ((100 - IFNULL(r.sconto1,0)) / 100) * ((100 - IFNULL(r.sconto2,0)) / 100) * ((100 - IFNULL(t.sconto1,0)) / 100) * ((100 - IFNULL(t.sconto2,0)) / 100) * ((100 - IFNULL(t.sconto3,0)) / 100)";
        System.out.println("sql aggiornaPrezziNettiUnitari : " + sql);
        try {
            DbUtils.tryExecQuery(Db.getConn(), sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        sql = "update " + tabrighe + " r join " + tabtest + " t on r.id_padre = t.id "
                + "set r.prezzo_ivato_netto_unitario = IFNULL(r.prezzo_ivato,0) * ((100 - IFNULL(r.sconto1,0)) / 100) * ((100 - IFNULL(r.sconto2,0)) / 100) * ((100 - IFNULL(t.sconto1,0)) / 100) * ((100 - IFNULL(t.sconto2,0)) / 100) * ((100 - IFNULL(t.sconto3,0)) / 100)";
        System.out.println("sql aggiornaPrezziNettiUnitari : " + sql);
        try {
            DbUtils.tryExecQuery(Db.getConn(), sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        sql = "update " + tabrighe + " r join " + tabtest + " t on r.id_padre = t.id "
                + "set r.prezzo_netto_totale = r.prezzo_netto_unitario * r.quantita";
        System.out.println("sql aggiornaPrezziNettiUnitari : " + sql);
        try {
            DbUtils.tryExecQuery(Db.getConn(), sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void aggiornaPrezziNettiTotali(String tabrighe, String tabtest) {
        String sql = "update " + tabrighe + " r join " + tabtest + " t on r.id_padre = t.id "
                + "set r.totale_imponibile_netto = (prezzo_netto_unitario * quantita) * t.totale_imponibile / t.totale_imponibile_pre_sconto";
        System.out.println("sql aggiornaPrezziNettiUnitari : " + sql);
        try {
            DbUtils.tryExecQuery(Db.getConn(), sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sql = "update " + tabrighe + " r join " + tabtest + " t on r.id_padre = t.id "
                + "set r.totale_ivato_netto = (prezzo_ivato_netto_unitario * quantita) * t.totale / t.totale_ivato_pre_sconto";
        System.out.println("sql aggiornaPrezziNettiUnitari : " + sql);
        try {
            DbUtils.tryExecQuery(Db.getConn(), sql);
        } catch (Exception e) {
            e.printStackTrace();
        }        
        sql = "update " + tabrighe + " r join " + tabtest + " t on r.id_padre = t.id "
                + "set r.totale_iva_netto = ((prezzo_ivato_netto_unitario - prezzo_netto_unitario) * quantita) * (t.totale - t.totale_imponibile) / (t.totale_ivato_pre_sconto - t.totale_imponibile_pre_sconto)";
        System.out.println("sql aggiornaPrezziNettiUnitari : " + sql);
        try {
            DbUtils.tryExecQuery(Db.getConn(), sql);
        } catch (Exception e) {
            e.printStackTrace();
        }                
        
        sql = "update " + tabrighe + " r "
                + " set r.totale_iva_netto = IFNULL(totale_iva_netto, 0), r.totale_ivato_netto = IFNULL(totale_ivato_netto, 0), r.totale_imponibile_netto = IFNULL(totale_imponibile_netto, 0)";
        System.out.println("sql aggiornaPrezziNettiUnitari : " + sql);
        try {
            DbUtils.tryExecQuery(Db.getConn(), sql);
        } catch (Exception e) {
            e.printStackTrace();
        }                
        
    }

    public static boolean checkSqlBlob(String sql) {
        String key = "insert into dati_azienda values (";
        if (!sql.startsWith(key)) {
            return false;
        }
        Map<String, String> fields = new HashMap<String, String>();
        ArrayList<String> field_list = new ArrayList();
        ArrayList<String> field_list_apici = new ArrayList();
        StringBuffer sb = new StringBuffer();
        sb = new StringBuffer();
        boolean inizio_campo = true;
        boolean inizio_apice = false;
        char oldc = (char) -1;
        String nuovasql = key;        
        for (int i = key.length(); i < sql.length(); i++) {
//            if (i == 784) {
//                System.out.println("stop");
//            }
            char c = sql.charAt(i);
//            System.out.println("colonna: " + (i+1) + " carattere:" + c + " oldc:" + oldc + " sb:" + sb.toString() + " inizio_campo:" + inizio_campo + " inizio_apice:" + inizio_apice);
            if (c == '\'' && !inizio_apice && inizio_campo) {
                inizio_apice = true;
                inizio_campo = false;
            } else if ((c == '\'' && inizio_apice && oldc != '\\' && !inizio_campo)
                    || (c == ',' && !inizio_apice) || i == (sql.length() - 2)) {
                //se arrivo qui per fine apice vado a cerca la prima virgola per spostare l'offset
                if (c == '\'' && inizio_apice && oldc != '\\' && !inizio_campo) {
                    for (int i2 = i; i2 < sql.length(); i2++) {
                        char c2 = sql.charAt(i2);
                        if (c2 == ',') {
                            i = i2;
                            break;
                        }
                    }
                }
                if (inizio_apice) {
                    field_list_apici.add("'" + sb.toString() + "'");
                } else {
                    field_list_apici.add(sb.toString());
                }
                field_list.add(sb.toString());
                inizio_apice = false;
                inizio_campo = true;
//                System.out.println("AGGIUNTO FIELD: " + sb.toString());
                sb.setLength(0);
                if (i == (sql.length() - 2)) {
                    break;
                }
            } else {
                sb.append(c);
            }
            oldc = c;
//            System.out.println("colonna: " + (i+1) + " carattere:" + c + " oldc:" + oldc + " sb:" + sb.toString() + " inizio_campo:" + inizio_campo + " inizio_apice:" + inizio_apice);
        }

        //prendo i nomi dei campi per capire il field che campo è
        List<Map> listfieldtab = null;
        try {
            listfieldtab = DbUtils.getListMap(Db.getConn(), "describe dati_azienda");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //debug
        System.out.println("listfieldtab.size: " + listfieldtab.size());
        System.out.println("field_list_apici.size: " + field_list_apici.size());
        System.out.println("----------");
        int i = 0;
        for (Map m : listfieldtab) {
            System.out.println("listfieldtab " + i + " : " + m);
            i++;
        }
        System.out.println("----------");
        i = 0;
        for (String s : field_list_apici) {
            System.out.println("field_list_apici " + i + " : " + StringUtils.abbreviate(s, 50));
            i++;
        }
        System.out.println("----------");
        
        //inserisco la riga di data azienda
        for (int i2 = 0; i2 < listfieldtab.size(); i2++) {
            String field = field_list_apici.get(i2);
            String fieldname = cu.toString(listfieldtab.get(i2).get("Field"));
            System.out.println("fieldname: " + fieldname + " value:" + StringUtils.abbreviate(field, 100));
            String virgola = ",";
            if (i2 == 0) {
                virgola = "";
            }
            if (field.startsWith("0x")) {
                nuovasql += virgola + "null";
            } else {
                nuovasql += virgola + field;
            }
        }
        nuovasql += ")";
        System.out.println("nuovasql:" + nuovasql);
        try {
            DbUtils.tryExecQuery(Db.getConn(), nuovasql);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //salvo i loghi blob
        for (int i3 = 0; i3 < listfieldtab.size(); i3++) {
            String field = field_list.get(i3);
            String fieldname = cu.toString(listfieldtab.get(i3).get("Field"));
            System.out.println("fieldname: " + fieldname + " value:" + StringUtils.abbreviate(field, 100));
            if (field.startsWith("0x") && field.length() > 2) {
                int limit = 1024 * 512;
                String field2 = field.substring(2, field.length());
                int quanti = (field2.length() / limit) + 1;
                sql = "update dati_azienda set " + fieldname + " = null";
                try {
                    DbUtils.tryExecQuery(Db.getConn(), sql);
                    for (int iq = 0; iq < quanti; iq++) {
                        String parte = field2.substring(iq * limit, Math.min((iq * limit) + limit, field2.length()));
                        sql = "update dati_azienda set " + fieldname + " = CONCAT(IFNULL(" + fieldname + ",''), 0x" + parte + ")";
                        DbUtils.debug = false;
                        DbUtils.tryExecQuery(Db.getConn(), sql);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }

    static public MyAbstractListIntelliHints getArticoloIntelliHints(final JTextField articolo, final JComponent frame, final AtomicReference articolo_selezionato, final DelayedExecutor delay_aggiornamento, final JComponent next) {
        return new MyAbstractListIntelliHints(articolo) {
            String current_search = "";
            Border myborder = new LineBorder(Color.lightGray) {
                @Override
                public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                    Color oldColor = g.getColor();
                    g.setColor(Color.lightGray);
                    g.drawLine(x, height - 1, width, height - 1);
                    g.setColor(oldColor);
                }
            };

            @Override
            protected JList createList() {
                final JList list = new JList() {
                    @Override
                    public int getVisibleRowCount() {
                        int size = getModel().getSize();
                        System.out.println("createList getVisibleRowCount " + size);
                        return size < super.getVisibleRowCount() ? size : super.getVisibleRowCount();
                    }
                };

                System.err.println("getArticoloIntelliHints... createList setfixedcellwidth " + (frame.getWidth() - 50));
                list.setFixedCellWidth(frame.getWidth() - 50);

                list.setCellRenderer(new DefaultListCellRenderer() {
                    {
                        setUI(new MultiLineLabelUI());
                    }

                    @Override
                    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        String img, tipo;
                        JLabel lab = null;
                        if (value instanceof ArticoloHint) {
                            tipo = ((ArticoloHint) value).toString();
                            lab = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                            MultiLineLabelUI ui = (MultiLineLabelUI) lab.getUI();
                            ui.tohighlight = current_search;
                            lab.setBorder(myborder);
                            System.err.println("getListCellRendererComponent w:" + list.getWidth());
                        } else {
                            lab = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        }
                        return lab;
                    }

                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                    }
                });
                return list;
            }
            SwingWorker lastw = null;

            public boolean updateHints(Object arg0) {
                if (arg0.toString().trim().length() <= 0) {
                    articolo_selezionato.set(null);
                    setSelezionato(null);
                    if (delay_aggiornamento != null) {
                        delay_aggiornamento.update(this);
                    }
                    return false;
                }

                setListData(new String[]{"... in ricerca ..."});
                current_search = arg0.toString();

                if (lastw != null) {
                    if (!lastw.isDone()) {
                        lastw.cancel(true);
                    }
                }

                SwingWorker w = new SwingWorker() {
                    @Override
                    protected Object doInBackground() throws Exception {
                        Connection conn;
                        try {
                            conn = Db.getConn();

                            //Thread.sleep((long) (Math.random() * 5000));

                            //prima cerco in articoli e poi (se non trovo abbastanza) in movimenti magazzino
                            String sql1 = ""
                                    + "SELECT a.codice, a.descrizione, IFNULL(a.codice_a_barre,''), IFNULL(a.codice_fornitore,''), IFNULL(a.codice_fornitore2,''), IFNULL(a.codice_fornitore3,''), IFNULL(a.codice_fornitore4,''), IFNULL(a.codice_fornitore5,''), IFNULL(a.codice_fornitore6,'') FROM articoli a"
                                    + " where codice like '" + Db.aa(current_search) + "%'"
                                    + " or descrizione like '" + Db.aa(current_search) + "%'"
                                    + " or codice_fornitore like '" + Db.aa(current_search) + "%'"
                                    + " or codice_fornitore2 like '" + Db.aa(current_search) + "%'"
                                    + " or codice_fornitore3 like '" + Db.aa(current_search) + "%'"
                                    + " or codice_fornitore4 like '" + Db.aa(current_search) + "%'"
                                    + " or codice_fornitore5 like '" + Db.aa(current_search) + "%'"
                                    + " or codice_fornitore6 like '" + Db.aa(current_search) + "%'"
                                    + " or codice_a_barre like '" + Db.aa(current_search) + "%'"
                                    + " order by descrizione, codice limit 100";
                            System.out.println("sql ricerca1:" + sql1);
                            ResultSet rs = DbUtils.tryOpenResultSet(conn, sql1);
                            ArrayList pk = new ArrayList();
                            Vector v = new Vector();
                            while (rs.next()) {
                                ArticoloHint art = new ArticoloHint();
                                art.codice = rs.getString(1);
                                art.descrizione = rs.getString(2);
                                art.codice_a_barre = rs.getString(3);
                                art.codice_fornitore = codice_fornitore(rs, current_search);
                                v.add(art);
                                pk.add(art.codice);
                            }
                            DbUtils.close(rs);
                            //poi provo 'per contiene' sempre su articoli
                            sql1 = ""
                                    + "SELECT a.codice, a.descrizione, IFNULL(a.codice_a_barre,''), IFNULL(a.codice_fornitore,''), IFNULL(a.codice_fornitore2,''), IFNULL(a.codice_fornitore3,''), IFNULL(a.codice_fornitore4,''), IFNULL(a.codice_fornitore5,''), IFNULL(a.codice_fornitore6,'') FROM articoli a"
                                    + " where codice like '%" + Db.aa(current_search) + "%'"
                                    + " or descrizione like '%" + Db.aa(current_search) + "%'"
                                    + " or codice_fornitore like '%" + Db.aa(current_search) + "%'"
                                    + " or codice_fornitore2 like '%" + Db.aa(current_search) + "%'"
                                    + " or codice_fornitore3 like '%" + Db.aa(current_search) + "%'"
                                    + " or codice_fornitore4 like '%" + Db.aa(current_search) + "%'"
                                    + " or codice_fornitore5 like '%" + Db.aa(current_search) + "%'"
                                    + " or codice_fornitore6 like '%" + Db.aa(current_search) + "%'"
                                    + " or codice_a_barre like '%" + Db.aa(current_search) + "%'"
                                    + " order by descrizione, codice limit 100";

                            System.out.println("sql ricerca2:" + sql1);
                            rs = DbUtils.tryOpenResultSet(conn, sql1);
                            while (rs.next()) {
                                if (!pk.contains(rs.getString(1))) {
                                    ArticoloHint art = new ArticoloHint();
                                    art.codice = rs.getString(1);
                                    art.descrizione = rs.getString(2);
                                    art.codice_a_barre = rs.getString(3);
                                    art.codice_fornitore = codice_fornitore(rs, current_search);
                                    v.add(art);
                                    pk.add(art.codice);
                                }
                            }
                            DbUtils.close(rs);
                            //se non trova quasi niente...
                            if (v.size() < 3) {
                                String sql2 = ""
                                        + "SELECT m.articolo, IFNULL(m.matricola,''), IFNULL(m.lotto,'') FROM movimenti_magazzino m"
                                        + " where articolo like '%" + Db.aa(current_search) + "%'"
                                        + " or matricola like '%" + Db.aa(current_search) + "%'"
                                        + " or lotto like '%" + Db.aa(current_search) + "%'"
                                        + " group by m.articolo, m.matricola, m.lotto"
                                        + " order by articolo limit 50";
                                System.out.println("sql2:" + sql2);
                                rs = DbUtils.tryOpenResultSet(conn, sql2);
                                while (rs.next()) {
                                    try {
                                        if (!pk.contains(rs.getString(1))) {
                                            ArticoloHint art = new ArticoloHint();
                                            art.codice = rs.getString(1);
                                            art.matricola = rs.getString(2);
                                            art.lotto = rs.getString(3);
                                            try {
                                                art.descrizione = (String) DbUtils.getObject(Db.getConn(), "select descrizione from articoli where codice = " + Db.pc(art.codice, Types.VARCHAR));
                                            } catch (Exception e) {
                                            }
                                            //art.descrizione = rs.getString(3);
                                            //art.codice_fornitore = codice_fornitore(rs, current_search);
                                            //art.codice_a_barre = rs.getString(5);
                                            v.add(art);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                rs.getStatement().close();
                                rs.close();
                            }
                            //setListData(v);
                            return v;
                        } catch (InterruptedException iex) {
                            return null;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            return new String[]{ex.getMessage()};
                        }
                    }

                    @Override
                    protected void done() {
                        try {
                            Object get = get();
                            if (get != null) {
                                //System.err.println("set listdata " + DebugFastUtils.dumpAsString(get));
                                if (get instanceof String[]) {
                                    setListData((String[]) get);
                                } else {
                                    setListData((Vector) get);
                                }
                                showHints2();
                            }
                        } catch (CancellationException cex) {
                        } catch (Exception ex) {
                            System.err.println("set listdata ex " + ex.getMessage());
                            setListData(new String[]{ex.getMessage()});
                            showHints2();
                        }
                    }
                };

                w.execute();
                lastw = w;

                return true;
            }

            @Override
            public void acceptHint(Object arg0) {
                super.acceptHint(arg0);
                try {
                    articolo.setText(((ArticoloHint) arg0).descrizione + " [" + ((ArticoloHint) arg0).codice + "]");
                    articolo_selezionato.set((ArticoloHint) arg0);
                    setSelezionato((ArticoloHint) arg0);
                    next.requestFocus();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (delay_aggiornamento != null) {
                    delay_aggiornamento.update(this);
                }
            }

            private String codice_fornitore(ResultSet rs, String cosa) {
                String ret = "";
                Integer[] ids = new Integer[]{4, 5, 6, 7, 8, 9};
                for (int i : ids) {
                    try {
                        if (rs.getString(i) != null && rs.getString(i).toLowerCase().indexOf(cosa.toLowerCase()) >= 0) {
                            if (!ret.equals("")) {
                                ret += " - ";
                            }
                            ret += rs.getString(i);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return ret;
            }
        };
    }
    
    static public MyAbstractListIntelliHints getCliforIntelliHints(final JTextField clifor, final JComponent frame, final AtomicReference clifor_selezionato, final DelayedExecutor delay_aggiornamento, final JComponent next) {
        return new MyAbstractListIntelliHints(clifor) {

            String current_search = "";

            @Override
            protected JList createList() {
                final JList list = super.createList();
                list.setCellRenderer(new DefaultListCellRenderer() {

                    @Override
                    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        String img, tipo;
                        tipo = ((ClienteHint) value).toString();
                        JLabel lab = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        String word = current_search;
                        String content = tipo;
                        Color c = null;
                        if (!isSelected) {
                            c = new Color(240, 240, 100);
                        } else {
                            c = new Color(100, 100, 40);
                        }
                        String rgb = Integer.toHexString(c.getRGB());
                        rgb = rgb.substring(2, rgb.length());

                        content = StringUtilsTnx.highlightWord(content, word, "<span style='background-color: " + rgb + "'>", "</span>");

                        if (((ClienteHint) value).obsoleto) {
                            content = "<span style='color: FF0000'>" + content + " (Obsoleto)</span>";
                        }
                        lab.setText("<html>" + content + "</html>");
                        System.out.println(index + ":" + content);
                        return lab;
                    }
                });
                return list;
            }

            public boolean updateHints(Object arg0) {
                if (arg0 != null && arg0.toString().trim().length() <= 0) {
                    clifor_selezionato.set(null);
                    setSelezionato(null);
                    if (delay_aggiornamento != null) {
                        delay_aggiornamento.update(this);
                    }
                    return false;
                }
                
                SwingUtils.mouse_wait();
                current_search = arg0 != null ? arg0.toString() : "";
                Connection conn;
                try {
                    conn = gestioneFatture.Db.getConn();

                    String sql = ""
                            + "SELECT codice, ragione_sociale, obsoleto FROM clie_forn"
                            + " where (codice like '%" + gestioneFatture.Db.aa(current_search) + "%'"
                            + " or ragione_sociale like '%" + gestioneFatture.Db.aa(current_search) + "%'"
                            + " ) and ragione_sociale != ''"
                            + " order by ragione_sociale, codice limit 50";

                    System.out.println("sql ricerca:" + sql);
                    ResultSet rs = DbUtils.tryOpenResultSet(conn, sql);
                    Vector v = new Vector();

                    while (rs.next()) {
                        ClienteHint cliente = new ClienteHint();
                        cliente.codice = rs.getString(1);
                        cliente.ragione_sociale = rs.getString(2);
                        cliente.obsoleto = rs.getBoolean(3);
                        v.add(cliente);
                    }
                    setListData(v);
                    rs.getStatement().close();
                    rs.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                SwingUtils.mouse_def();
                return true;
            }

            @Override
            public void acceptHint(Object arg0) {
                super.acceptHint(arg0);
                try {
                    clifor.setText(((ClienteHint) arg0).ragione_sociale + " [" + ((ClienteHint) arg0).codice + "]");
                    clifor_selezionato.set((ClienteHint)arg0);
                    setSelezionato((ClienteHint)arg0);
                    next.requestFocus();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (delay_aggiornamento != null) {
                    delay_aggiornamento.update(this);
                }
            }
        };    
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        File f = new File("C:\\lavori\\tnx\\private\\Invoicex_altro\\run\\backup\\dump_20130522_1703.txt");
        FileInputStream fis = new FileInputStream(f);
        String sql = "";
        String sqlc = "";
        int tot = (int) f.length();
        SqlLineIterator liter = new SqlLineIterator(fis);
        int linen = 0;

        while (liter.hasNext()) {
            linen++;
            sqlc = liter.nextLine();
            checkSqlBlob(sqlc);
        }
    }

    public static Color getColorePerMarcatura(String colore) {
        if (colore.equalsIgnoreCase("rosso")) {
            return new Color(255, 100, 100);
        } else if (colore.equalsIgnoreCase("giallo")) {
            return new Color(255, 255, 100);
        } else if (colore.equalsIgnoreCase("blu")) {
            return new Color(100, 100, 255);
        }
        return new Color(150, 150, 150);
    }
    
    static public void salvaColoreRiga(String colore, String tab, tnxDbGrid griglia) {
        int[] righe = griglia.getSelectedRows();
        for (int riga : righe) {
            int id = (Integer) griglia.getValueAt(riga, griglia.getColumnByName("id"));
            String query = "UPDATE " + tab + " SET color = " + Db.pc(colore, Types.VARCHAR) + "WHERE id = " + Db.pc(id, Types.INTEGER);
            Db.executeSql(query);
        }
        try {
            JInternalFrame frame = (JInternalFrame) SwingUtilities.getAncestorOfClass(JInternalFrame.class, griglia);
            frame.getClass().getMethod("dbRefresh", null).invoke(frame, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }    
    
    static public void creaPdf(String tipo_doc, Integer[] id, boolean apriDirDopoStampa, boolean apriPdf) throws Exception {
        Object ret_stampa = null;
        //creo la cartella, se non esistesse!
        File fdDir = new File(main.wd + "tempEmail");
        try {
            fdDir.mkdir();
        } catch (Exception e) {
        }
        try {
            //elimino i precedenti files
            File d = new File(main.wd + "tempEmail");
            Util.deleteFilesFromDir(d);
        } catch (Exception e) {
        }
        if (tipo_doc.equals(Db.TIPO_DOCUMENTO_ORDINE) || tipo_doc.equals(Db.TIPO_DOCUMENTO_ORDINE_ACQUISTO)) {
            boolean acquisto = false;
            if (tipo_doc.equals((Db.TIPO_DOCUMENTO_ORDINE_ACQUISTO))) {
                acquisto = true;
            }
            main.loadIni();
            File f1 = null;
            File f2 = null;
            ArrayList<String> file_list = new ArrayList<String>();
            boolean continua = false;
            if (main.getPersonalContain("emicad") && main.fileIni.getValueBoolean("emicad", "attiva", false) && !acquisto) {
                if (main.fileIni.getValueBoolean("emicad", "richiedi", true)) {
                    JFileChooser fileChoose = new JFileChooser();
                    FileFilter filter1 = new FileFilter() {
                        public boolean accept(File pathname) {
                            if (pathname.getAbsolutePath().endsWith(".pdf") || pathname.isDirectory()) {
                                return true;
                            } else {
                                return false;
                            }
                        }

                        @Override
                        public String getDescription() {
                            return "File PDF (*.pdf)";
                        }
                    };
                    fileChoose.setFileFilter(filter1);
                    int ret = fileChoose.showDialog(main.getPadreFrame(), "Imposta");
                    if (ret == JFileChooser.APPROVE_OPTION) {
                        f1 = fileChoose.getSelectedFile();
                        file_list.add(0, f1.getAbsolutePath());
                    } else {
                        file_list.add(0, "");
                    }
                    ret = fileChoose.showDialog(main.getPadreFrame(), "Imposta");
                    if (ret == JFileChooser.APPROVE_OPTION) {
                        f2 = fileChoose.getSelectedFile();
                        file_list.add(1, f2.getAbsolutePath());
                    } else {
                        file_list.add(1, "");
                    }
                } else {
                    String path_pre = main.fileIni.getValue("emicad", "file_pre", "");
                    String path_post = main.fileIni.getValue("emicad", "file_post", "");
                    if (!path_pre.equals("")) {
                        f1 = new File(path_pre);
                        file_list.add(0, f1.getAbsolutePath());
                    } else {
                        file_list.add(0, "");
                    }
                    if (!path_post.equals("")) {
                        f2 = new File(path_post);
                        file_list.add(1, f2.getAbsolutePath());
                    } else {
                        file_list.add(1, "");
                    }
                }
            }
            SwingUtils.mouse_wait(main.getPadreFrame());
            final ArrayList<String> list = file_list;
            for (int i = 0; i < id.length; i++) {
                int idn = id[i];
                List<Map> ret = DbUtils.getListMap(Db.getConn(), "select serie, numero, anno from " + Db.getNomeTabT(tipo_doc) + " where id = " + idn);
                final String dbSerie = cu.toString(ret.get(0).get("serie"));
                final int dbNumero = cu.toInteger(ret.get(0).get("numero"));
                final int dbAnno = cu.toInteger(ret.get(0).get("anno"));

                try {
                    InvoicexUtil.aggiornaTotaliRighe(tipo_doc, idn);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (main.getPersonalContain("emicad") && !acquisto && list != null) {
                    frmElenOrdini.stampa(null, dbSerie, dbNumero, dbAnno, true, false, true, acquisto, list, idn);
                } else {
                    if (apriDirDopoStampa && !apriPdf) {    //non attende completamento
                        ret_stampa = frmElenOrdini.stampa(null, dbSerie, dbNumero, dbAnno, true, false, true, acquisto, idn);
                    } else {
                        ret_stampa = frmElenOrdini.stampa(null, dbSerie, dbNumero, dbAnno, true, true, true, acquisto, idn);
                    }
                }
            }
        } else if (tipo_doc.equals(Db.TIPO_DOCUMENTO_FATTURA)) {
            if (main.getPersonalContain(main.PERSONAL_LUXURY)) {
                //chiedo se stampare in nero
                int ret = javax.swing.JOptionPane.showConfirmDialog(main.getPadreFrame(), "Vuoi stampare con lo sfondo nero ?", "Attenzione", javax.swing.JOptionPane.YES_NO_OPTION);
                if (ret == javax.swing.JOptionPane.YES_OPTION) {
                    main.luxStampaNera = true;
                } else {
                    main.luxStampaNera = false;
                }
                //euro o dollari
                int ret2 = javax.swing.JOptionPane.showConfirmDialog(main.getPadreFrame(), "Vuoi stampare in Dollari ?", "Attenzione", javax.swing.JOptionPane.YES_NO_OPTION);
                if (ret2 == javax.swing.JOptionPane.YES_OPTION) {
                    main.luxStampaValuta = "\u0024";
                } else {
                    main.luxStampaValuta = "\u20ac";
                }
            }

            SwingUtils.mouse_wait(main.getPadreFrame());

            for (int i = 0; i < id.length; i++) {
                int idn = id[i];
                List<Map> ret = DbUtils.getListMap(Db.getConn(), "select tf.descrizione_breve AS tipo, serie, numero, anno from " + Db.getNomeTabT(tipo_doc) + " t left join tipi_fatture tf on t.tipo_fattura = tf.tipo where id = " + idn);
                final String tipoFattura = cu.toString(ret.get(0).get("tipo"));
                final String dbSerie = cu.toString(ret.get(0).get("serie"));
                final int dbNumero = cu.toInteger(ret.get(0).get("numero"));
                final int dbAnno = cu.toInteger(ret.get(0).get("anno"));

                try {
                    InvoicexUtil.aggiornaTotaliRighe(Db.TIPO_DOCUMENTO_FATTURA, idn);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (apriDirDopoStampa && !apriPdf) {    //non attende completamento
                    ret_stampa = frmElenFatt.stampa(tipoFattura, dbSerie, dbNumero, dbAnno, true, false, true, idn);
                } else {
                    ret_stampa = frmElenFatt.stampa(tipoFattura, dbSerie, dbNumero, dbAnno, true, true, true, idn);
                }
            }
        } else if (tipo_doc.equals(Db.TIPO_DOCUMENTO_DDT) || tipo_doc.equals(Db.TIPO_DOCUMENTO_DDT_ACQUISTO)) {
            SwingUtils.mouse_wait(main.getPadreFrame());
            boolean acquisto = false;
            if (tipo_doc.equals((Db.TIPO_DOCUMENTO_DDT_ACQUISTO))) {
                acquisto = true;
            }            

            for (int i = 0; i < id.length; i++) {
                int idn = id[i];

                List<Map> ret = DbUtils.getListMap(Db.getConn(), "select serie, numero, anno from " + Db.getNomeTabT(tipo_doc) + " where id = " + idn);
                final String dbSerie = cu.toString(ret.get(0).get("serie"));
                final int dbNumero = cu.toInteger(ret.get(0).get("numero"));
                final int dbAnno = cu.toInteger(ret.get(0).get("anno"));

                try {
                    InvoicexUtil.aggiornaTotaliRighe(tipo_doc, idn);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (apriDirDopoStampa && !apriPdf) {    //non attende completamento
                    ret_stampa = frmElenDDT.stampa(null, dbSerie, dbNumero, dbAnno, true, false, true, acquisto, idn);
                } else {
                    ret_stampa = frmElenDDT.stampa(null, dbSerie, dbNumero, dbAnno, true, true, true, acquisto, idn);
                }

            }

        }
        
        if (apriDirDopoStampa) {
            try {
                Util.start(fdDir.getAbsolutePath());
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
        if (apriPdf && ret_stampa != null && ret_stampa instanceof String) {
            try {
                Util.start(ret_stampa.toString());
            } catch (Exception err) {
                err.printStackTrace();
            }                
        }
        
        SwingUtils.mouse_def(main.getPadreFrame());
        
    }
    
    static public void ripristinaDump(File f, SwingWorker w) throws SQLException, IOException {
        try {
            InvoicexEvent event = new InvoicexEvent(f);
            event.type = InvoicexEvent.TYPE_PRE_RESTORE_DB;
            main.events.fireInvoicexEvent(event);
        } catch (Exception err) {
            err.printStackTrace();
        }
        
        Statement stat;
        stat = Db.getConn().createStatement();
        try {
            stat.execute("SET foreign_key_checks = 0;");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //droppo tutte le tabelle
        try {
            ArrayList<Object[]> tables = DbUtils.getListArray(Db.getConn(), "show full tables");
            for (Object[] a : tables) {
                String t = (String) a[0];
                String tipo = (String) a[1];
                if (tipo == null || !tipo.equalsIgnoreCase("VIEW")) {
                    try {
                        stat.execute("DROP TABLE IF EXISTS " + t);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        stat.execute("DROP VIEW IF EXISTS " + t);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }                            
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        stat.execute("SET storage_engine=MYISAM;");

        String sqlc = null;

        int tot = (int) f.length();
        FileInputStream fis = new FileInputStream(f);
        String sql = "";
        
        SqlLineIterator liter = new SqlLineIterator(fis);
        int linen = 0;

        JWindow werr = null;
        while (liter.hasNext()) {
            linen++;
            if (linen % 100 == 0) {
                w.publish(new int[]{(int) liter.bytes_processed, (int) tot});
                w.publish("Ripristino in corso " + liter.bytes_processed + "/" + tot);
            }
            sqlc = liter.nextLine();

            if (sqlc.startsWith("--")) continue;
            if (sqlc.startsWith("insert into v_righ_tutte ")) continue;
            if (sqlc.startsWith("CREATE ALGORITHM=UNDEFINED")) {
                //CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`192.168.0.%` SQL SECURITY DEFINER VIEW `v_righ_tutte` AS select 'v' AS `tabella`,`r`.`id` AS `id`,`r`.`id_padre` AS `id_padre`,`t`.`data` AS `data`,`t`.`anno` AS `anno`,`t`.`numero` AS `numero`,`t`.`serie` AS `serie`,`r`.`riga` AS `riga`,`r`.`codice_articolo` AS `codice_articolo`,`r`.`descrizione` AS `descrizione`,`r`.`quantita` AS `quantita`,`r`.`prezzo` AS `prezzo`,`r`.`prezzo_netto_unitario` AS `prezzo_netto_unitario`,`r`.`sconto1` AS `sconto1`,`r`.`sconto2` AS `sconto2`,`t`.`sconto1` AS `sconto1t`,`t`.`sconto2` AS `sconto2t`,`t`.`sconto3` AS `sconto3t`,`c`.`codice` AS `clifor`,`c`.`ragione_sociale` AS `ragione_sociale` from ((`righ_fatt` `r` join `test_fatt` `t` on((`r`.`id_padre` = `t`.`id`))) join `clie_forn` `c` on((`t`.`cliente` = `c`.`codice`))) union all select 'a' AS `tabella`,`r`.`id` AS `id`,`r`.`id_padre` AS `id_padre`,`t`.`data` AS `data`,`t`.`anno` AS `anno`,`t`.`numero` AS `numero`,`t`.`serie` AS `serie`,`r`.`riga` AS `riga`,`r`.`codice_articolo` AS `codice_articolo`,`r`.`descrizione` AS `descrizione`,`r`.`quantita` AS `quantita`,`r`.`prezzo` AS `prezzo`,`r`.`prezzo_netto_unitario` AS `prezzo_netto_unitario`,`r`.`sconto1` AS `sconto1`,`r`.`sconto2` AS `sconto2`,`t`.`sconto1` AS `sconto1t`,`t`.`sconto2` AS `sconto2t`,`t`.`sconto3` AS `sconto3t`,`c`.`codice` AS `clifor`,`c`.`ragione_sociale` AS `ragione_sociale` from ((`righ_fatt_acquisto` `r` join `test_fatt_acquisto` `t` on((`r`.`id_padre` = `t`.`id`))) join `clie_forn` `c` on((`t`.`fornitore` = `c`.`codice`)));
                System.out.println("sqlc prima = " + sqlc);
                sqlc = "create view " + StringUtils.substringAfter(sqlc, " VIEW ");
                System.out.println("sqlc dopo  = " + sqlc);
            }
            if (StringUtils.isBlank(sqlc)) continue;

            sqlc = StringUtils.replace(sqlc, "0x,", "null,");
            sqlc = StringUtils.replace(sqlc, "0x)", "null)");
            if (sqlc.length() > 0) {
                try {
                    sqlc = StringUtils.replace(sqlc, "USING BTREE", "");

                    //controllo per logo in dati azienda
                    boolean fatto_da_checkblob = false;
                    try {
                        fatto_da_checkblob = InvoicexUtil.checkSqlBlob(sqlc);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (!fatto_da_checkblob) {
//                        System.out.println("sqlc = " + sqlc);
                        stat.execute(sqlc);
                    }
                } catch (com.mysql.jdbc.PacketTooBigException toobig) {
                    //controllo se è dati azienda provo a maetterlo a pezzi
                    if (werr == null || !werr.isVisible()) {
                        werr = SwingUtils.showFlashMessage2("Errore durante il ripristino: " + toobig.getMessage(), 5, null, Color.RED);
                    }
                    toobig.printStackTrace();
                    System.out.println("toobig sql di errore:" + sql);
                } catch (Exception err) {
                    if (werr == null || !werr.isVisible()) {
                        if (err.getMessage().indexOf("character_set_client") < 0) {
                            werr = SwingUtils.showFlashMessage2("Errore durante il ripristino: " + err.getMessage(), 5, null, Color.RED);
                        }
                    }
                    err.printStackTrace();
                    System.out.println("sql di errore:" + sqlc);
                }
            }
        }

        try {
            stat.execute("SET foreign_key_checks = 1;");
        } catch (Exception e) {
            e.printStackTrace();
        }

        stat.close();
        
        try {
            InvoicexEvent event = new InvoicexEvent(f);
            event.type = InvoicexEvent.TYPE_POST_RESTORE_DB;
            main.events.fireInvoicexEvent(event);
        } catch (Exception err) {
            err.printStackTrace();
        }

    }
    
}