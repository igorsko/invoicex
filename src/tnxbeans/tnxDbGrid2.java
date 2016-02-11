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
package tnxbeans;

import java.io.Serializable;
import java.awt.*;
import java.awt.event.*;

import javax.swing.table.*;
import javax.swing.event.*;
import java.util.Vector;
import java.util.Hashtable;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;

//per la stampa
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import it.tnx.DbI;
import java.sql.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

//public class tnxDbGrid2 extends org.jdesktop.swingx.JXTable implements Serializable, javax.swing.event.TableColumnModelListener, ListSelectionListener {
public class tnxDbGrid2 extends org.jdesktop.swingx.JXTable implements Serializable {

    public Vector dbChiave;
    public tnxDbPanel dbPanel;
    public JLabel labRecords;
    public JScrollPane scrollPane;
    public String dbNomeTabella;
    public boolean dbEditabile = false;
    private boolean chiediDiSalvareModifiche = true;
    public static int SU_SPOSTAMENTO_SALVA = 1;
    public static int SU_SPOSTAMENTO_ANNULLA = 2;
    public static int SU_SPOSTAMENTO_CHIEDI = 3;
    private int cosaFareSuSpostamento = 3;
    public boolean dbConsentiAggiunte = true;
    public int[] colonneEditabili;
    public DefaultTableCellRenderer currencyRender = null;
    private Connection connection;
    private java.sql.Statement stat;
    //private DefaultTableModel tm;
    private SortableTableModel tm;
    private String oldSql;
    private Connection oldConnection;
    public java.sql.ResultSetMetaData meta;
    public boolean isFinding = false;
    public boolean flagUsaThread = false;
    public boolean flagUsaOrdinamento = true;
    public Hashtable columnsSizePerc;
    public Hashtable columnsAlign;
    public Hashtable columnsProps;
    public Hashtable columnsName = new Hashtable(); //server per avere il id colonna in base al nome colonna originale
    private boolean isAlreadyAlign = false;
    private boolean isAlreadyProps = false;
    private int oldWidth;
    private int oldSelectedRow;
    private Hashtable columnsTitle;
    private Hashtable columnsTitleLang;
    private boolean isResizing = false;
    private int countOpens = 0;
    private int countResize = 0;
    public DefaultTableCellRenderer infoRender;
    //eventi per data entry
    private int prevRow = 0;
    private int prevCol = 0;
    private boolean dirty = false;
//    public HeaderListener headerListener;
    private boolean giaAgganciato = false;
    private DbI db;

    public tnxDbGrid2() {
        super();
//        try {
//            jbInit();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        RollOverListener lst = new RollOverListener();
        addMouseMotionListener(lst);
        addMouseListener(lst);
    }
    private int rollOverRowIndex = -1;

    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);
        if(isRowSelected(row)) {
            c.setForeground(getSelectionForeground());
            c.setBackground(getSelectionBackground());
        } else if(row == rollOverRowIndex) {
//            c.setForeground(getSelectionForeground().brighter().brighter().brighter().brighter());
//            c.setBackground(getSelectionBackground().brighter().brighter().brighter().brighter());
            c.setForeground(getSelectionForeground().brighter());
            c.setBackground(getSelectionBackground().brighter());
        } else {
            c.setForeground(getForeground());
            c.setBackground(getBackground());
        }
        return c;
    }

    private class RollOverListener extends MouseInputAdapter {

        public void mouseExited(MouseEvent e) {
            rollOverRowIndex = -1;
            repaint();
        }

        public void mouseMoved(MouseEvent e) {
            int row = rowAtPoint(e.getPoint());
            if (row != rollOverRowIndex) {
                rollOverRowIndex = row;
                repaint();
            }
        }
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

    public static String getDirSeparator() {
        // Get all system properties
        return System.getProperty("file.separator");
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

    public void setCosaFareSuSpostamento(int value) {
        this.cosaFareSuSpostamento = value;
    }

    public int getCosaFareSuSpostamento() {
        return this.cosaFareSuSpostamento;
    }
    boolean refreshSuSpostamento = true;

    public void setRefreshSuSpostamento(boolean value) {
        refreshSuSpostamento = value;
    }

    public boolean getRefreshSuSpostamento() {
        return refreshSuSpostamento;
    }

    //stampa generica
    public String stampaTabella(String titolo, int[] headerWidth) {
        return stampaTabella(titolo, headerWidth, null);
    }

    public String stampaTabella(String titolo, int[] headerWidth, String piede) {
        Connection connection;
        java.sql.Statement stat;
        ResultSet resu;
        String nomeFilePdf = getTempDir(true) + "tempStampa.pdf";
        String nomeFileHtml = getTempDir(true) + "tempStampa.html";

        com.lowagie.text.Font bf;
        BaseFont bfCour;
        BaseFont bf_italic;
        BaseFont bf_bold;
        BaseFont bf_times;
        PdfContentByte cb;
        Document document;
        PdfWriter writer;

        try {
            //creazione del pdf
            document = new Document(PageSize.A4);
            writer = PdfWriter.getInstance(document, new java.io.FileOutputStream(nomeFilePdf));
            //HtmlWriter.getInstance(document, new java.io.FileOutputStream(nomeFileHtml));
            document.addTitle("stampa tabella");
            document.addSubject("stampa tabella");
            document.addKeywords("stampa tabella");
            document.addAuthor("TNX s.a.s");
            document.addHeader("Expires", "0");
            document.open();
            //------------------------------------
            bf = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 7, com.lowagie.text.Font.NORMAL);
            bfCour = BaseFont.createFont("Courier", "winansi", false);
            bf_italic = BaseFont.createFont("Helvetica-Oblique", "winansi", false);
            bf_bold = BaseFont.createFont("Helvetica-Bold", "winansi", false);
            bf_times = BaseFont.createFont("Times-Roman", "winansi", false);
            //BaseFont bf = BaseFont.createFont("Helvetia", "winansi", false);
            cb = writer.getDirectContent();

            //stampa
            //cliente
            double y;
            double iy;
            double x;
            double ix;

            int riga = 0;
            iy = 1.25;
            y = 4;
            //tabella dati testata
            Table datatable;
            datatable = new Table(meta.getColumnCount());
            datatable.setBorder(0);
//            datatable.setCellpadding(0);
            datatable.setPadding(2);
            //int headerwidths2[] = {20, 80}; // percentage
            //datatable.setWidths(headerwidths2);
            if (headerWidth != null) {
                datatable.setWidths(headerWidth);
            }
            datatable.setWidth(100); // percentage

            datatable.setCellsFitPage(true);

            Phrase tempFrase;
            Cell tempPdfCell;

            //intestazione
            Phrase intestazione = new Phrase();
            intestazione.add(new Chunk(titolo, new com.lowagie.text.Font(com.lowagie.text.Font.TIMES_ROMAN, 10, com.lowagie.text.Font.BOLD)));
            document.add(intestazione);

            //tempPdfCell = new Cell(new Phrase(this. , new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 8, com.lowagie.text.Font.NORMAL)));
            //colonne
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                tempPdfCell = new Cell(new Phrase(this.meta.getColumnLabel(i), bf));
                set1(tempPdfCell);
                datatable.addCell(tempPdfCell);
            }
            //righe
            for (int j = 0; j < tm.getRowCount(); j++) {
                //colonne
                for (int i = 0; i < meta.getColumnCount(); i++) {
                    //controllo tipo di campo
                    tempPdfCell = new Cell(new Phrase(nz(String.valueOf(this.getValueAt(j, i)), ""), bf));
                    set2(tempPdfCell);
                    datatable.addCell(tempPdfCell);
                }
            }

            document.add(datatable);

            //controllo se presente il piede
            if (piede != null) {
                Phrase pPiede = new Phrase();
                pPiede.add(new Chunk(piede, new com.lowagie.text.Font(com.lowagie.text.Font.TIMES_ROMAN, 10, com.lowagie.text.Font.BOLD)));
                document.add(pPiede);
            }

            //chiudo
            document.close();

            //Runtime.getRuntime().exec("start " + nomeFilePdf);
            //Util.start(nomeFilePdf);
            return (nomeFilePdf);

        } catch (Exception err) {
            javax.swing.JOptionPane.showMessageDialog(null, err.toString());
            err.printStackTrace();

            return (null);
        }
    }

    void set1(Cell tempPdfCell) {
        tempPdfCell.setBackgroundColor(new Color(255, 255, 240));
        tempPdfCell.setBorderColor(new Color(200, 200, 200));
        tempPdfCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
    }

    void set2(Cell tempPdfCell) {
        tempPdfCell.setBackgroundColor(new Color(255, 255, 255));
        tempPdfCell.setBorderColor(new Color(200, 200, 200));
        tempPdfCell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
    }

    //public boolean setTm(DefaultTableModel tm) {
    public boolean setTm(SortableTableModel tm) {
        this.tm = tm;
        return (true);
    }

    public TableModel getTm() {
        return (this.tm);
    }

    public boolean dbOpen(Connection connection, String sql, DbI db) {
        this.db = db;
        return dbOpen(connection, sql);
    }

    public boolean dbOpen(Connection connection, String sql) {
//        //aggancio a eventi del dbPanel se presente
//        if (dbPanel != null && !giaAgganciato) {
//            dbPanel.addDbListener(new DbListener() {
//                public void statusFired(DbEvent event) {
//                    if (event.getStatus() == dbPanel.STATUS_REFRESHING) {
//                        dbPanel.sincronizzaSelezioneGriglia(tnxDbGrid2.this);
//                    }
//                }
//            });
//            giaAgganciato = true;
//        }

        //inizializzo renderers
        //per data
        DefaultTableCellRenderer dateRender = new DefaultTableCellRenderer() {

            public void setValue(Object value) {
                //super.setValue(value);
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
                if (value != null) {
                    super.setValue(dateFormat.format((Date) value));
                } else {
                    super.setValue("");
                }
                //setBackground(Color.red);
            }
        };
        //per data time
        DefaultTableCellRenderer dateTimeRender = new DefaultTableCellRenderer() {

            public void setValue(Object value) {
                //super.setValue(value);
                //DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy hh:mm:ss");
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy hh:mm");
                if (value != null) {
                    super.setValue(dateFormat.format(value));
                } else {
                    super.setValue("");
                }
                //setBackground(Color.green);
            }
        };
        //per valuta
        currencyRender = new DefaultTableCellRenderer() {

            public void setValue(Object value) {
                if (value != null) {
                    //super.setValue(value);
                    double d = getDoubleEng(String.valueOf(value));
                    NumberFormat form = DecimalFormat.getInstance(Locale.ITALIAN);
                    form.setGroupingUsed(true);
                    form.setMaximumFractionDigits(5);
                    form.setMinimumFractionDigits(2);
                    //DecimalFormat form = new DecimalFormat("#,##0.00###");
                    super.setValue(form.format(d));
                    if (d < 0) {
                        setForeground(new Color(200, 0, 0));
                    } else {
                        //setForeground();
                    }
                    setHorizontalAlignment(JLabel.RIGHT);
                } else {
                    super.setValue(null);
                }
            }
        };
        //data access
        //per info
        infoRender = new DefaultTableCellRenderer() {

            public void setValue(Object value) {
                super.setValue(value);
                if (value.toString().equals("*")) {
                    setBackground(new Color(220, 200, 200));
                } else {
                    setBackground(new Color(200, 200, 200));
                }
                setHorizontalAlignment(JLabel.CENTER);
            }
        };

        //if (this.columnsSizePerc != null) {
        this.setAutoResizeMode(this.AUTO_RESIZE_OFF);
        //}

        countOpens++;
        //debug
        //System.out.println("dbopen:" + this.getParent().getName() + " . " + countOpens);

        //resize event of the scrollpane
//        
//        this.getParent().addComponentListener(new java.awt.event.ComponentAdapter() {
//
//            public void componentResized(ComponentEvent e) {
//                this_componentResized(e);
//            }
//        });
//        this.getParent().addMouseListener(new java.awt.event.MouseAdapter() {
//
//            public void mouseEvent(MouseEvent e) {
//                this_mouseEvent(e);
//            }
//        });
//        this.addMouseListener(new java.awt.event.MouseAdapter() {
//
//            public void mouseEvent(MouseEvent e) {
//                this_mouseEvent(e);
//            }
//        });

        this.oldSql = sql;
        this.oldConnection = connection;

        //apre il resultset da abbinare

        //if (countOpens > 1) {
        if (flagUsaThread == false) {
            //provo a fare senza thread una volta aperto l prima volta
            ResultSet resu = null;
            ResultSetMetaData meta;

            //no thread
            try {
                if (db != null) {
                    stat = db.getDbConn().createStatement();
                } else {
                    stat = connection.createStatement();
                }

                resu = stat.executeQuery(sql);
                meta = resu.getMetaData();

                //colonne
                int numeColo = meta.getColumnCount();
                String Colo[] = new String[numeColo];
                for (int i = 1; i <= numeColo; ++i) {
                    //Colo[i-1] = meta.getColumnLabel(i);
                    Colo[i - 1] = meta.getColumnLabel(i);
                }
                //tm = new DefaultTableModel(Colo,0);
                tm = new SortableTableModel(Colo, 0, meta, colonneEditabili);
                //righe
                int numRec = 0;
                while (resu.next()) {
                    numRec++;
                    Object reco[] = new Object[numeColo];
                    for (int i = 1; i <= numeColo; ++i) {

                        //debug
                        //System.out.println("colType:"+i+":"+meta.getColumnType(i) + "colName:"+meta.getColumnLabel(i));
                        //System.out.println("colType VARCHAR:"+Types.VARCHAR);
                        //System.out.println("colType CHAR:"+Types.CHAR);

                        if (meta.getColumnType(i) == Types.VARCHAR ||
                                meta.getColumnType(i) == Types.CHAR) {
                            //System.out.println("varchar row:"+numRec+" col:"+i+" value:"+resu.getObject(i));
                            reco[i - 1] = resu.getString(i);
                        } else if (meta.getColumnType(i) == Types.INTEGER ||
                                meta.getColumnType(i) == Types.SMALLINT) {
                            //System.out.println("int row:"+numRec+" col:"+i+" value:"+resu.getObject(i));
                            //reco[i-1] = new Integer(resu.getInt(i));
                            if (resu.getString(i) == null) {
                                reco[i - 1] = "";
                            } else {
                                reco[i - 1] = new Integer(resu.getInt(i));
                            }
                        } else if (meta.getColumnType(i) == Types.BIGINT) {
                            //test per non far vedere i numeri null
                            //System.out.println("bigint row:"+numRec+" col:"+i+" value:"+resu.getObject(i));
                            //reco[i-1] = new Long(resu.getLong(i));
                            reco[i - 1] = resu.getObject(i);
                        } else if (meta.getColumnType(i) == Types.DECIMAL ||
                                meta.getColumnType(i) == Types.DOUBLE) {
                            //System.out.println("double row:"+numRec+" col:"+i+" value:"+resu.getObject(i));
                            reco[i - 1] = new Double(resu.getDouble(i));
                        } else if (meta.getColumnType(i) == Types.DATE) {
                            //System.out.println("date row:"+numRec+" col:"+i+" value:"+resu.getObject(i));
                            reco[i - 1] = resu.getDate(i);
                        } else {
                            //System.out.println("default row:"+numRec+" col:"+i+" value:"+resu.getObject(i));
                            reco[i - 1] = resu.getObject(i);
                        }

                    }
                    tm.addRow(reco);
                }

                if (this == null) {
                    return (true);
                } else {
                    this.setModel(tm);

//                    if (dbEditabile == true) {
//                        //abbino event listener
//                        tnxDbGrid2_eventi lisEventi = new tnxDbGrid2_eventi(this);
//                        this.tm.addTableModelListener(lisEventi);
//                    }

                    //metto i renderers
                    for (int i = 1; i <= numeColo; ++i) {
                        if (meta.getColumnType(i) == Types.VARCHAR ||
                                meta.getColumnType(i) == Types.CHAR) {
                            //this.getColumn(Colo[i-1]).setCellRenderer(cellRenderer)
                        } else if (meta.getColumnType(i) == Types.INTEGER ||
                                meta.getColumnType(i) == Types.SMALLINT) {
                            //this.getColumn(Colo[i-1]).setCellRenderer(cellRenderer)
                        } else if (meta.getColumnType(i) == Types.BIGINT) {
                            //this.getColumn(Colo[i-1]).setCellRenderer(cellRenderer)
                        } else if (meta.getColumnType(i) == Types.DECIMAL ||
                                meta.getColumnType(i) == Types.DOUBLE) {
                            //this.getColumn(meta.getColumnLabel(i)).setCellRenderer(currencyRender);
//                            this.getColumn(meta.getColumnLabel(i)).setCellRenderer(currencyRender);
                        } else if (meta.getColumnType(i) == Types.DATE) {
                            //this.getColumn(meta.getColumnLabel(i)).setCellRenderer(dateRender);
                            this.getColumn(meta.getColumnLabel(i)).setCellRenderer(dateRender);
                        } else if (meta.getColumnType(i) == Types.TIMESTAMP) {
                            //this.getColumn(meta.getColumnLabel(i)).setCellRenderer(dateTimeRender);
                            this.getColumn(meta.getColumnLabel(i)).setCellRenderer(dateTimeRender);
                        } else {
                            //this.getColumn(Colo[i-1]).setCellRenderer(cellRenderer)
                        }
                    }
                    this.meta = meta;
                    TableColumnModel defColu = this.getColumnModel();

                    if (dbEditabile == false) {
                        this.setDefaultEditor(this.getColumnClass(1), null);
                    }

                    while (tm.getRowCount() != this.getRowCount()) {
                        Thread.yield();
                    }
                    //debug
                    //System.out.println("open:prima resize:" + tm.getRowCount() + " : " + this.getRowCount());
                    this.resizeColumnsPerc(true);
                    //System.out.println("open:dopo resize:" + tm.getRowCount() + " : " + this.getRowCount());

                    //vado al primo record
                    this.getSelectionModel().setSelectionInterval(0, 0);

                    refreshRecords();

                    if (this.flagUsaOrdinamento == true) {
                        openSort();
                    }
                    return (true);
                }
            } catch (Exception err) {
                err.printStackTrace();
                System.out.println("sql errore:" + sql);
            } finally {
                try {
                    stat.close();
                } catch (Exception e) {
                }
                try {
                    resu.close();
                } catch (Exception e) {
                }
                meta = null;
            }
        } else {
            //thread
            TRiempiGriglia2 ciclo = new TRiempiGriglia2(sql, connection, this, tm, colonneEditabili, db);
            ciclo.start();
        }

        return (true);
    }

    void openSort() {
        //sort
//        if (headerListener == null) {
//            SortButtonRenderer renderer = new SortButtonRenderer();
//            TableColumnModel model = this.getColumnModel();
//            int n = model.getColumnCount();
//            for (int i = 0; i < n; i++) {
//                model.getColumn(i).setHeaderRenderer(renderer);
//            }
//
//            JTableHeader header = this.getTableHeader();
//            headerListener = new HeaderListener(header, renderer);
//            header.addMouseListener(headerListener);
//        }
        //---
    }

    public void dbRefresh() {
//        //aggancio a eventi del dbPanel se presente
//        if (dbPanel != null && !giaAgganciato) {
//            dbPanel.addDbListener(new DbListener() {
//                public void statusFired(DbEvent event) {
//                    if (event.getStatus() == dbPanel.STATUS_REFRESHING) {
//                        dbPanel.sincronizzaSelezioneGriglia(tnxDbGrid2.this);
//                    }
//                }
//            });
//            giaAgganciato = true;
//        }

        //mi salvo la riga dov'ero prima e provo a ritornarci
        oldSelectedRow = this.getSelectedRow();
        dbOpen(this.oldConnection, this.oldSql);

//        if (headerListener != null) {
//            headerListener.resort();
//        }

        if (dbPanel != null) {
//            dbPanel.sincronizzaSelezioneGriglia(tnxDbGrid2.this);
        } else {
            //provo a ritornarci sulla riga dov'ero prima
            try {
                if (this.getRowCount() > oldSelectedRow && oldSelectedRow >= 0 && this.getRowCount() > 0) {
                    //debug
                    System.out.println("grid refresh:rowcount:" + this.getRowCount() + " oldrow:" + oldSelectedRow);
                    this.setRowSelectionInterval(oldSelectedRow, oldSelectedRow);
                    if (!getVisibleRect().contains(getCellRect(getSelectedRow(), 1, true))) {
                        scrollToRow(this.getSelectedRow());
                    }
                } else {
                    if (getRowCount() > 0) {
                        this.setRowSelectionInterval(1, 1);
                        if (!getVisibleRect().contains(getCellRect(getSelectedRow(), 1, true))) {
                            this.scrollToRow(this.getSelectedRow());
                        }
                    }
                }
            } catch (Exception err) {
                //System.out.println("grid refresh:rowcount:" + this.getRowCount() + " oldrow:" + oldSelectedRow);
                err.printStackTrace();
            }
        }

        refreshRecords();
    }

    public boolean dbDelete() {
        //elimina il record e si posiziona sul primo
        String sql = "";
        Vector valoriChiave = new Vector();
        try {
            //nuovo modo tramite dbChiaveValori che viene memorizzato ogni volta si cambia il record
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                String tipoCampo = meta.getColumnTypeName(i);
                //sql += pc(fieldText,tipoCampo);
                //salvo i valori se chiave
                //debug
                //javax.swing.JOptionPane.showMessageDialog(null,meta.getColumnLabel(i));
                //debug
                //if (dbChiave.contains(meta.getColumnLabel(i))) {
                if (dbChiave.contains(this.getColumnName(i - 1))) {
                    //debug
                    //javax.swing.JOptionPane.showMessageDialog(null,"trovato");
                    //debug
                    valoriChiave.add(pc(this.getValueAt(this.getSelectedRow(), i - 1).toString(), tipoCampo));
                }
            }
            //controlli
            if (valoriChiave.size() == 0) {
                javax.swing.JOptionPane.showMessageDialog(this, "griglia:valorichiave vuoto!!!");
            }
            if (this.dbNomeTabella == null) {
                javax.swing.JOptionPane.showMessageDialog(this, "griglia:nometabella nullo!!!");
            }
            if (this.dbNomeTabella.length() == 0) {
                javax.swing.JOptionPane.showMessageDialog(this, "griglia:nometabella vuoto!!!");
                //creo query di eliminazione
            }
            sql = "delete from " + this.dbNomeTabella + " where ";
            for (int l = 0; l < dbChiave.size(); l++) {
                if (l == 0) {
                    sql += (String) dbChiave.get(l) + " = " + (String) valoriChiave.get(l);
                } else {
                    sql += " and " + (String) dbChiave.get(l) + " = " + (String) valoriChiave.get(l);
                }
            }
        } catch (Exception err) {
            //debug
            err.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, err.toString());
        }
        //eseguo la query
        try {
            //debug
            //javax.swing.JOptionPane.showMessageDialog(null,sql);
            //System.out.println("dbGrid:sqldelete:" + sql);

            if (db != null) {
                stat = db.getDbConn().createStatement();
            } else {
                stat = oldConnection.createStatement();
            }

            stat.execute(sql);
            //1.1
            //this.dbRefresh();
            //---
            tm.removeRow(this.getSelectedRow());
            //***
        } catch (Exception err) {
            err.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, err.toString());
        } finally {
            try {
                stat.close();
            } catch (Exception e) {
            }
        }
        return (true);
    }

    public void dbGoNext() {
        if (getSelectedRow() < this.getRowCount() - 1) {
            this.getSelectionModel().setSelectionInterval(this.getSelectedRow() + 1, this.getSelectedRow() + 1);
            if (this.getSelectedRow() == 0) {
                dbGoNext();
            } else {
                this.dbSelezionaRiga();
            }
        }
    }

    public void dbGoPrevious() {
        if (getSelectedRow() > 0) {
            this.getSelectionModel().setSelectionInterval(this.getSelectedRow() - 1, this.getSelectedRow() - 1);
            this.dbSelezionaRiga();
        }
    }

    public void dbGoFirst() {
        this.getSelectionModel().setSelectionInterval(0, 0);
        this.dbSelezionaRiga();
    }

    public void dbGoLast() {
        this.getSelectionModel().setSelectionInterval(this.getRowCount() - 1, this.getRowCount() - 1);
        this.dbSelezionaRiga();
    }

    public void dbSelezionaRiga() {
        if (this.dbPanel != null && this.getSelectedRowCount() > 0) {

            if (this.getValueAt(this.getSelectedRow(), 0).toString().equals("*")) {
                return;
                //133 chiedo conferma quando si sposta
                //debug
                //System.out.println("panel stato:" + this.dbPanel.dbStato);
            }
            refreshRecords();

            if (!this.dbPanel.dbStato.equals(this.dbPanel.DB_LETTURA) && isFinding == false) {
                if (cosaFareSuSpostamento == this.SU_SPOSTAMENTO_ANNULLA) {
                    if (refreshSuSpostamento) {
                        dbRefresh();
                    }
                } else if (cosaFareSuSpostamento == this.SU_SPOSTAMENTO_SALVA) {
                    this.dbPanel.dbSave();
                    if (refreshSuSpostamento) {
                        dbRefresh();
                    }
                } else if (cosaFareSuSpostamento == this.SU_SPOSTAMENTO_CHIEDI) {
                    int ret = javax.swing.JOptionPane.showConfirmDialog(this.getRootPane(), "Salvare le modifiche apportate ?", "Attenzione", javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE);
                    if (ret == javax.swing.JOptionPane.YES_OPTION) {
                        this.dbPanel.dbSave();
                        if (refreshSuSpostamento) {
                            dbRefresh();
                        }
                    }
                }
            }

            //debug
            //javax.swing.JOptionPane.showMessageDialog(null,"click su riga2 : " + String.valueOf(this.getSelectedRow()));

            Vector temp = new Vector();
            //debug
            //javax.swing.JOptionPane.showMessageDialog(null,"cerco chiave");

            for (int i = 0; i < dbChiave.size(); i++) {
                //cerco id del camp chiave
                boolean trovato = false;
                int idCampo = 0;
                //debug
                //javax.swing.JOptionPane.showMessageDialog(null,"tm.columnCount:"+String.valueOf(tm.getColumnCount()));

                if (dbChiave.get(i) instanceof String) {
                    for (int j = 0; j < tm.getColumnCount(); j++) {
                        //debug
                        //javax.swing.JOptionPane.showMessageDialog(null,"tm:="+tm.getColumnLabel(j)+" chiave:"+this.dbChiave.get(i));
                        if (tm.getColumnName(j).equalsIgnoreCase((String) this.dbChiave.get(i))) {
                            trovato = true;
                            idCampo = j;
                            j = tm.getColumnCount();
                        }
                    }
                } else {
                    trovato = true;
                    Integer tempI = (Integer) (dbChiave.get(i));
                    idCampo = tempI.intValue();
                }
                if (trovato == true) {
                    String valore = "";
                    valore = nz(tm.getValueAt(this.getSelectedRow(), idCampo).toString(), "");
                    temp.add(valore);
                    //debug
                    //javax.swing.JOptionPane.showMessageDialog(null,"valore " +String.valueOf(i)+ " : " + valore);
                } else {
                    //javax.swing.JOptionPane.showMessageDialog(null,"errore, non trovato id campo chiave");
                }
            }

            //debug
            //javax.swing.JOptionPane.showMessageDialog(null,temp.toString());

            dbPanel.dbSelectSingle(temp);
            dbPanel.dbRefresh();
        }
    }

    public void dbSelezionaRiga(int colIndex, Object value) {
        for (int i = 0; i < getRowCount(); i++) {
            if (getValueAt(i, colIndex).toString().equals(value.toString())) {
                getSelectionModel().setSelectionInterval(i, i);
                break;
            }
        }
    }

    public void refreshRecords() {
        if (this.labRecords != null) {
            //System.out.println("debug:grid:rc=" + this.getRowCount() + ":ri=" + this.getSelectedRow());
            this.labRecords.setText("R " + (this.getSelectedRow() + 1) + "/" + this.getRowCount());
        }
    }

    public boolean dbFindFirst() {
        isFinding = true;

        this.setRowSelectionInterval(0, 0);

        //controllo il primo
        boolean trovato = false;
        int numeColo = 0;
        for (int i = 0; i < this.getColumnCount(); i++) {
            if (this.getColumnName(i).equalsIgnoreCase(this.dbPanel.ultimoCampo)) {
                trovato = true;
                numeColo = i;
                i = this.getColumnCount();
            }
            if (trovato == true) {
                String griglia = "";
                String campo = "";
                campo = this.dbPanel.ultimoValore;
                if (this.getSelectedRow() == -1) {
                    this.setRowSelectionInterval(0, 0);
                }
                int j = this.getSelectedRow();
                griglia = nz(this.getValueAt(j, numeColo), "");
                if (griglia.length() >= campo.length()) {
                    if (griglia.substring(0, campo.length()).equalsIgnoreCase(campo)) {
                        //trovato il prossimo mi ci posiziono
                        this.setRowSelectionInterval(j, j);
                        this.scrollToRow(this.getSelectedRow());
                        dbSelezionaRiga();

                        if (this.dbPanel.butSave != null) {
                            this.dbPanel.butSave.setEnabled(false);
                        }
                        if (this.dbPanel.butUndo != null) {
                            this.dbPanel.butUndo.setEnabled(false);
                            //if (this.dbPanel.butFind!=null) this.dbPanel.butFind.setEnabled(false);
                        }
                    }
                }
            }
        }
        //prosegue

        if (dbFindNext() == false) {
            javax.swing.JOptionPane.showMessageDialog(null, "Posizione inesistente");
            isFinding = false;
            return (false);
        }
        isFinding = false;
        return (true);
    }

    public boolean dbFindNext() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");

        isFinding = true;

        //trovo id colonna
        boolean trovato = false;
        int numeColo = 0;
        //debug
        //javax.swing.JOptionPane.showMessageDialog(null,this.dbPanel.ultimoCampo+"-"+this.dbPanel.ultimoValore);

        for (int i = 0; i < this.getColumnCount(); i++) {
            if (this.getColumnName(i).equalsIgnoreCase(this.dbPanel.ultimoCampo)) {
                trovato = true;
                numeColo = i;
                i = this.getColumnCount();
            }
            if (trovato == true) {
                String griglia = "";
                String campo = "";
                //debug
                //javax.swing.JOptionPane.showMessageDialog(null,"ulitmovalore:" + this.dbPanel.ultimoValore);

                campo = this.dbPanel.ultimoValore;

                if (this.getSelectedRow() == -1) {
                    this.setRowSelectionInterval(0, 0);
                }

                for (int j = this.getSelectedRow() + 1; j < this.getRowCount(); j++) {
                    griglia = nz(this.getValueAt(j, numeColo), "");
                    try {
                        if (this.getValueAt(j, numeColo) != null) {
                            //System.out.println("c:" + meta.getColumnType(numeColo + 1));
                            //System.out.println("c:" + meta.getColumnTypeName(numeColo + 1));
                            if (meta.getColumnType(numeColo + 1) == Types.DATE) {
                                griglia = dateFormat.format((Date) getValueAt(j, numeColo));
                            }
                        }
                    } catch (java.sql.SQLException err) {
                    }
                    //debug
                    //javax.swing.JOptionPane.showMessageDialog(null,"+"+String.valueOf(campo.length())+"+"+griglia.substring(0,campo.length())+"+"+campo);
                    //javax.swing.JOptionPane.showMessageDialog(null,"+"+String.valueOf(campo.length())+"+"+String.valueOf(griglia.length())+"+"+campo);

                    if (griglia.length() >= campo.length()) {
                        if (griglia.substring(0, campo.length()).equalsIgnoreCase(campo)) {
                            //debug
                            //System.out.println("trovato");

                            //trovato il prossimo mi ci posiziono
                            this.setRowSelectionInterval(j, j);
                            this.scrollToRow(this.getSelectedRow());
                            dbSelezionaRiga();

                            if (this.dbPanel.butSave != null) {
                                this.dbPanel.butSave.setEnabled(false);
                            }
                            if (this.dbPanel.butUndo != null) {
                                this.dbPanel.butUndo.setEnabled(false);
                                //if (this.dbPanel.butFind!=null) this.dbPanel.butFind.setEnabled(false);
                                //debug
                                //System.out.println("trovato2");
                            }
                            isFinding = false;
                            return (true);
                        }
                    }
                }
            }
        }
        isFinding = false;
        return (false);
    }

    public boolean dbFindFirstSub() {
        isFinding = true;

        this.setRowSelectionInterval(0, 0);

        //controllo il primo
        boolean trovato = false;
        int numeColo = 0;
        for (int i = 0; i < this.getColumnCount(); i++) {
            if (this.getColumnName(i).equalsIgnoreCase(this.dbPanel.ultimoCampo)) {
                trovato = true;
                numeColo = i;
                i = this.getColumnCount();
            }
            if (trovato == true) {
                String griglia = "";
                String campo = "";
                campo = this.dbPanel.ultimoValore;
                if (this.getSelectedRow() == -1) {
                    this.setRowSelectionInterval(0, 0);
                }
                int j = this.getSelectedRow();
                griglia = nz(this.getValueAt(j, numeColo), "");
                if (griglia.length() >= campo.length()) {
                    try {
                        if (griglia.toUpperCase().indexOf(campo.toUpperCase()) >= 0) {
                            //if (griglia.substring(0,campo.length()).equalsIgnoreCase(campo)) {
                            //trovato il prossimo mi ci posiziono
                            this.setRowSelectionInterval(j, j);
                            this.scrollToRow(this.getSelectedRow());
                            dbSelezionaRiga();

                            if (this.dbPanel.butSave != null) {
                                this.dbPanel.butSave.setEnabled(false);
                            }
                            if (this.dbPanel.butUndo != null) {
                                this.dbPanel.butUndo.setEnabled(false);
                                //if (this.dbPanel.butFind!=null) this.dbPanel.butFind.setEnabled(false);
                            }
                        }
                    } catch (Exception err) {
                        err.printStackTrace();
                    }
                }
            }
        }
        //prosegue

        if (dbFindNext() == false) {
            javax.swing.JOptionPane.showMessageDialog(null, "Posizione inesistente");
            isFinding = false;
            return (false);
        }
        isFinding = false;
        return (true);
    }

    public boolean dbFindNextSub() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");

        isFinding = true;

        //trovo id colonna
        boolean trovato = false;
        int numeColo = 0;
        //debug
        //javax.swing.JOptionPane.showMessageDialog(null,this.dbPanel.ultimoCampo+"-"+this.dbPanel.ultimoValore);

        for (int i = 0; i < this.getColumnCount(); i++) {
            if (this.getColumnName(i).equalsIgnoreCase(this.dbPanel.ultimoCampo)) {
                trovato = true;
                numeColo = i;
                i = this.getColumnCount();
            }
            if (trovato == true) {
                String griglia = "";
                String campo = "";
                //debug
                //javax.swing.JOptionPane.showMessageDialog(null,"ulitmovalore:" + this.dbPanel.ultimoValore);

                campo = this.dbPanel.ultimoValore;

                if (this.getSelectedRow() == -1) {
                    this.setRowSelectionInterval(0, 0);
                }

                for (int j = this.getSelectedRow() + 1; j < this.getRowCount(); j++) {
                    griglia = nz(this.getValueAt(j, numeColo), "");
                    try {
                        if (this.getValueAt(j, numeColo) != null) {
                            //System.out.println("c:" + meta.getColumnType(numeColo + 1));
                            //System.out.println("c:" + meta.getColumnTypeName(numeColo + 1));
                            if (meta.getColumnType(numeColo + 1) == Types.DATE) {
                                griglia = dateFormat.format((Date) getValueAt(j, numeColo));
                            }
                        }
                    } catch (java.sql.SQLException err) {
                    }
                    //debug
                    //javax.swing.JOptionPane.showMessageDialog(null,"+"+String.valueOf(campo.length())+"+"+griglia.substring(0,campo.length())+"+"+campo);
                    //javax.swing.JOptionPane.showMessageDialog(null,"+"+String.valueOf(campo.length())+"+"+String.valueOf(griglia.length())+"+"+campo);

                    if (griglia.length() >= campo.length()) {
                        try {
                            if (griglia.toUpperCase().indexOf(campo.toUpperCase()) >= 0) {
                                //if (griglia.substring(0,campo.length()).equalsIgnoreCase(campo)) {
                                //debug
                                //System.out.println("trovato");

                                //trovato il prossimo mi ci posiziono
                                this.setRowSelectionInterval(j, j);
                                this.scrollToRow(this.getSelectedRow());
                                dbSelezionaRiga();

                                if (this.dbPanel.butSave != null) {
                                    this.dbPanel.butSave.setEnabled(false);
                                }
                                if (this.dbPanel.butUndo != null) {
                                    this.dbPanel.butUndo.setEnabled(false);
                                    //if (this.dbPanel.butFind!=null) this.dbPanel.butFind.setEnabled(false);
                                    //debug
                                    //System.out.println("trovato2");
                                }
                                isFinding = false;
                                return (true);
                            }
                        } catch (Exception err) {
                            err.printStackTrace();
                        }
                    }
                }
            }
        }
        isFinding = false;
        return (false);
    }

    private void jbInit() throws Exception {
        /*
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
        public void componentResized(ComponentEvent e) {
        this_componentResized(e);
        }
        });
         */

        this.addKeyListener(new java.awt.event.KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                this_keyPressed(e);
            }

            public void keyReleased(KeyEvent e) {
                this_keyReleased(e);
            }
        });

//        this.tableHeader.addMouseListener(new java.awt.event.MouseAdapter() {
//
//            public void mousePressed(MouseEvent e) {
////                this_mousePressed(e);
//            }
//        });

        this.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                this_mousePressed(e);
            }
        });
    }

    void this_mouseClicked(MouseEvent e) {
    }

    void this_mousePressed(MouseEvent e) {
        dbSelezionaRiga();

        //1.3.3
        if (e.getClickCount() == 2 && e.getComponent().getClass().getName().equals("tnxbeans.tnxDbGrid")) {
            //javax.swing.JOptionPane.showMessageDialog(null,"doppio click");
            if (this.dbPanel != null) {
                //debug
                //System.out.println("grid doppio click1:" + this.dbPanel.getParent());
                //System.out.println("grid doppio click2:" + this.dbPanel.getRootPane());

                //va a ricercare a ritroso se ??? dentro un tabbed pane, se c'??? mette l'index = 0 che di solito c'??? il panel coi dati
                Component tempParent;
                tempParent = this.dbPanel.getParent();
                if (!tempParent.getClass().getName().equalsIgnoreCase("JTabbedPane")) {
                    for (int i = 0; i < 5; i++) {
                        tempParent = tempParent.getParent();
                        if (tempParent.getClass().getName().equalsIgnoreCase("javax.swing.JTabbedPane")) {
                            i = 10;
                        }
                    }
                }
                if (tempParent.getClass().getName().equalsIgnoreCase("javax.swing.JTabbedPane")) {
                    javax.swing.JTabbedPane tempTab = (javax.swing.JTabbedPane) tempParent;
                    tempTab.setSelectedIndex(0);
                }
            }
        }
    }

    void this_keyPressed(KeyEvent e) {
    }

    void this_keyReleased(KeyEvent e) {
        if (e.getKeyCode() == 38 || e.getKeyCode() == 40) {
            dbSelezionaRiga();
        }
    }

    private String nz(String valore, String seNullo) {
        if (valore == null) {
            return (seNullo);
        }
        return (valore);
    }

    private String nz(Object valore, String seNullo) {
        if (valore == null) {
            return (seNullo);
        }
        return (valore.toString());
    }

    public void scrollToRow(int row) {
        scrollPane = (JScrollPane) this.getParent().getParent();

        java.awt.Rectangle rect = this.getCellRect(row, 1, true);

        scrollPane.getViewport().setViewPosition(new Point(1, (int) rect.getY()));

        /*

        //debug
        //javax.swing.JOptionPane.showMessageDialog(null,this.getParent().toString());
        //javax.swing.JOptionPane.showMessageDialog(null,this.getParent().getParent().toString());
        //javax.swing.JOptionPane.showMessageDialog(null,this.getTopLevelAncestor().toString());
        scrollPane = (JScrollPane)this.getParent().getParent();

        //debug
        System.out.println("1"+scrollPane.getViewport().getViewRect().toString());

        rect.setRect(scrollPane.getViewport().getViewRect().getX(),rect.getY(),scrollPane.getViewport().getViewRect().getWidth(),scrollPane.getViewport().getViewRect().getHeight());

        scrollPane.scrollRectToVisible(new Rectangle(0,0,0,0));
        //debug
        System.out.println("1.5"+scrollPane.getViewport().getViewRect().toString());

        scrollPane.scrollRectToVisible(rect);

        //debug
        System.out.println("2"+rect.toString());
        System.out.println("3"+scrollPane.getViewport().getViewRect().toString());
         */
    }

    void this_componentResized(ComponentEvent e) {
        //javax.swing.JOptionPane.showMessageDialog(this,"resize");

        //richiamo dimensionamento in base al vettore
        //debug
        //javax.swing.JOptionPane.showMessageDialog(this,"resize");
        if (this.isResizing == false) {
            this.resizeColumnsPerc(false);
        }
    }

    void this_mouseEvent(MouseEvent e) {
        if (e.getModifiers() == e.MOUSE_PRESSED) {
            this.isResizing = true;
        }
        if (e.getModifiers() == e.MOUSE_RELEASED) {
            this.isResizing = false;
        }
    }

    public void resizeColumnsPerc(boolean forceResize) {
        String columnNameLingua = "";

        //dimensionamento in base al vettore
        //resize columns in percent
        if (this.columnsSizePerc != null) {
            //get all columns size
            int colsWidth = this.getParent().getWidth();
//            System.out.println("colsWidth:" + colsWidth);

            /*
            for (int aspetta = 0 ; aspetta < 300 ; aspetta++) {
            try {
            if (this.getColumnCount() > 0 && this.getColumnCount() == this.columnsSizePerc.size()) {
            aspetta = 300;
            }
            if (aspetta != 300) Thread.sleep(100);
            } catch (Exception err) {
            err.printStackTrace();
            }
            }*/

            if (this.getColumnCount() != this.columnsSizePerc.size()) {
                //javax.swing.JOptionPane.showMessageDialog(this, "il numero di colonne di cols size perc ? diverso dal numero di colonne nella griglia(" + String.valueOf(this.getColumnCount()) + ")");
                System.out.println("debug:griglia, numero colonne diverso:" + this.getColumnCount() + ":v=" + this.columnsSizePerc.size());
            }
            //System.out.println("resizePRE:" + countResize);
            if (this.getColumnCount() > 0 && this.getColumnCount() == this.columnsSizePerc.size() && this.isResizing == false && colsWidth > 0) {
                if (this.oldWidth != this.getParent().getWidth() || forceResize == true) {
                    //debug
                    this.isResizing = true;
                    countResize++;
                    this.setVisible(false);
                    //System.out.println("resize:" + countResize);

                    this.oldWidth = this.getParent().getWidth();

                    TableColumn col;
                    double colWidth;
                    Double colWidthPerc;
                    Object columnName;
                    int contaColonne = 0;
                    for (java.util.Enumeration e = columnsSizePerc.keys(); e.hasMoreElements();) {

                        columnName = e.nextElement();
                        try {
                            columnNameLingua = columnsTitleLang.get(columnName).toString();
                        } catch (Exception err) {
                            columnNameLingua = "";
                        }

                        if (contaColonne >= 0) {

                            //debug
                            //javax.swing.JOptionPane.showMessageDialog(this,String.valueOf(this.getColumn("Numero").getWidth()));
                            //javax.swing.JOptionPane.showMessageDialog(this,columnName);
                            //javax.swing.JOptionPane.showMessageDialog(this,String.valueOf(columnsSizePerc.get(columnName)));

                            try {
                                try {
                                    col = this.getColumn(String.valueOf(columnName));
                                } catch (Exception err) {
                                    col = this.getColumn(String.valueOf(columnNameLingua));
                                }
                                colWidthPerc = (Double) columnsSizePerc.get(columnName);

                                //col.setWidth((int)(colsWidth / 100 * colWidthPerc.doubleValue()));
                                if (colWidthPerc.doubleValue() != 0.0) {
                                    col.setPreferredWidth((int) (colsWidth / 100.0 * colWidthPerc.doubleValue()));
                                    //debug
                                    //System.out.println("resize misure1:" + colsWidth);
                                    //System.out.println("resize misure2:" + colWidthPerc.doubleValue());
                                    //System.out.println("resize misure3:" + (int)(colsWidth / 100.0 * colWidthPerc.doubleValue()));
                                } else {
                                    col.setMinWidth(0);
                                    col.setWidth(0);
                                    col.setPreferredWidth(0);
                                }
                                //debug
                                //System.out.println(columnName);
                                //System.out.println(this.getColumnLabel(6));
                                //if (String.valueOf(columnName).equalsIgnoreCase("Data")) {
                                //  System.out.println(String.valueOf((int)(colsWidth / 100.0 * colWidthPerc.doubleValue())));
                                //  System.out.println("--------------");
                                //}
                            } catch (Exception err) {
                                //debug
                                //for (java.util.Enumeration e2 = columnsSizePerc.keys() ; e2.hasMoreElements() ;) System.out.println("keys:" + e2.nextElement());

                                System.out.println("non trovata colonna:" + columnName + " err:" + err.getMessage());
                                //err.printStackTrace();
                            }
                        }
                        contaColonne++;
                    }

                    /*
                    //alignements
                    if (isAlreadyAlign == false && columnsAlign != null) {
                    isAlreadyAlign = true;
                    String align = "";
                    for (java.util.Enumeration ea = columnsAlign.keys() ; ea.hasMoreElements() ;) {
                    columnName = ea.nextElement();
                    try {
                    col = this.getColumn(String.valueOf(columnName));
                    align = (String)columnsAlign.get(columnName);
                    if (align.equalsIgnoreCase("RIGHT_CURRENCY")) {
                    //set alignement
                    // Show the values in the "Favorite Number" column in different colors.
                    DefaultTableCellRenderer colRenderer = new DefaultTableCellRenderer() {
                    public void setValue(Object value) {
                    setText((value == null) ? "" : value.toString());
                    }
                    };
                    colRenderer.setHorizontalAlignment(JLabel.RIGHT);
                    col.setCellRenderer(colRenderer);
                    }
                    } catch (Exception err) {
                    //System.out.println("non trovata colonna:" + columnName + " err:" + err.getMessage());
                    err.printStackTrace();
                    }
                    }
                    }
                    
                    //props
                    if (isAlreadyProps == false && this.columnsProps != null) {
                    isAlreadyProps = true;
                    String props = "";
                    for (java.util.Enumeration ea = columnsProps.keys() ; ea.hasMoreElements() ;) {
                    columnName = ea.nextElement();
                    try {
                    col = this.getColumn(String.valueOf(columnName));
                    props = (String)columnsProps.get(columnName);
                    if (props.equalsIgnoreCase("BOLD")) {
                    //set bold font
                    DefaultTableCellRenderer colRenderer = new DefaultTableCellRenderer() {
                    public void setValue(Object value) {
                    setText((value == null) ? "" : value.toString());
                    }
                    };
                    colRenderer.setFont(new Font("Dialog",10,Font.BOLD));
                    col.setCellRenderer(colRenderer);
                    }
                    } catch (Exception err) {
                    //System.out.println("non trovata colonna:" + columnName + " err:" + err.getMessage());
                    err.printStackTrace();
                    }
                    }
                    }
                     */

                    this.setVisible(true);

                    //rimetto i nomi
//                    if (columnsTitle != null) {
//                        for (java.util.Enumeration e = columnsTitle.keys(); e.hasMoreElements();) {
//                            Integer columnIndex = (Integer) e.nextElement();
//                            String caption = (String) columnsTitle.get((Object) columnIndex);
//                            this.setColumnTitle(columnIndex.intValue(), caption);
//                        }
//                    }
                    //--------------

                    this.isResizing = false;
                }
            }
        }
    }

    String replaceChars(String stri, char daTrov, String daMett) {
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

    public String aa(String stringa) {
        //aggiunge apice al singolo
        return (replaceChars(stringa, '\'', "''"));
    }

    public String pc(String campo, String tipoCampo) {
        //prepara il campo per sql
        //debug
        //javax.swing.JOptionPane.showMessageDialog(null,"campo:"+campo+" tipo:"+tipoCampo);
        if (tipoCampo == "LONG") {
            return (campo);
        } else if (tipoCampo == "DECIMAL") {
            return ("(" + campo + ")");
        } else if (tipoCampo == "VARCHAR") {
            return ("'" + aa(campo) + "'");
        } else {
            return ("'" + aa(campo) + "'");
        }
    }

    String spezza(String stringa, int ogni) {
        String temp = "";
        for (int i = 0; i < stringa.length(); i = i + ogni) {
            if ((i + ogni) > stringa.length()) {
                temp += stringa.substring(i, stringa.length()) + "\n";
            } else {
                temp += stringa.substring(i, i + ogni) + "\n";
            }
        }
        return (temp);
    }

    public void test1() {
        this.getModel().setValueAt("test", 1, 1);
        //DefaultTableModel temp1 = (DefaultTableModel)this.getModel();
        SortableTableModel temp1 = (SortableTableModel) this.getModel();

        temp1.setNumRows(1);
        Object[] dati = {"aaa", "bbb", "ccc"};
        temp1.addRow(dati);
    }

    public int getColumnByName(String colName) {
        for (int i = 0; i < this.getColumnCount(); i++) {
            if (this.getColumnName(i).equalsIgnoreCase(colName)) {
                return (i);
            }
        }
        return (-1);
    }

//    public void valueChanged(javax.swing.event.ListSelectionEvent listSelectionEvent) {
//        //debug
//        //System.out.println(listSelectionEvent.toString());
//
//        if (this.dbEditabile == true) {
//            //controllo cambio di riga
//            if (this.prevRow != this.getSelectedRow() && listSelectionEvent.getValueIsAdjusting() == false || this.prevCol != this.getSelectedColumn() && listSelectionEvent.getValueIsAdjusting() == false) {
//                //debug
//                //System.out.prevColintln("cambioRiga");
//
//                if (tm != null) {
//                    if (tm.dirty == true) {
//                        this.dirty = true;
//
//                        //debug
//                        //System.out.println("dirty:true");
//
//                        saveDataEntry(this.prevRow);
//
//                        this.dirty = false;
//                        this.tm.dirty = false;
//                    }
//                }
//                this.prevRow = this.getSelectedRow();
//            }
//        }
//        super.valueChanged(listSelectionEvent);
//    }
    public void saveDataEntry(int row) {
        //provo a cercare la chiave
        try {
            Vector valoriChiave = new Vector();
            //mi sposto sulla riga sopra per riprendere i dati
            //this.getSelectionModel().setSelectionInterval(this.getSelectedRow()-1,this.getSelectedRow()-1);
            for (int i = 0; i < this.dbChiave.size(); i++) {
                //System.out.println("grid_chiave_nome  :" + i + ":" + this.dbChiave.get(i));
                //System.out.println("grid_chiave_valore:" + i + ":" + this.getValueAt(row, new Integer(this.columnsName.get(this.dbChiave.get(i)).toString()).intValue()+1));
                String valore = this.getValueAt(row, new Integer(this.columnsName.get(this.dbChiave.get(i)).toString()).intValue()).toString();
                String tipo = this.meta.getColumnTypeName(new Integer(this.columnsName.get(this.dbChiave.get(i)).toString()).intValue());
                valoriChiave.add(this.dbChiave.get(i) + " = " + this.pc(valore, tipo));
                //System.out.println("grid_chiave_vector:" + i + ":" + valoriChiave.get(i));
            }
            //ok prendo tutti i valori e li reinserisco
            String sql = "";
            Vector nomiCampo = new Vector();
            Vector valoriCampo = new Vector();
            Vector tipiCampo = new Vector();
            for (int i = 0; i < this.meta.getColumnCount(); i++) {
                nomiCampo.add(this.meta.getColumnLabel(i + 1));
                valoriCampo.add(this.getValueAt(row, i + 1));
                tipiCampo.add(this.meta.getColumnTypeName(i + 1));
            }

            if (row + 1 == this.getRowCount() - 1 && dbConsentiAggiunte) {
                //se sono su ultima riga INSERT
                sql += "insert into " + this.meta.getTableName(1) + " (";
                for (int i = 0; i < nomiCampo.size(); i++) {
                    if (i == nomiCampo.size() - 1) {
                        sql += nomiCampo.get(i);
                    } else {
                        sql += nomiCampo.get(i) + ", ";
                    }
                }
                sql += ") values (";
                for (int i = 0; i < valoriCampo.size(); i++) {
                    if (i == valoriCampo.size() - 1) {
                        sql += pc(valoriCampo.get(i).toString(), tipiCampo.get(i).toString());
                    } else {
                        sql += pc(valoriCampo.get(i).toString(), tipiCampo.get(i).toString()) + ", ";
                    }
                }
                sql += ")";
                //debug
                System.out.println("grid_insert_sql:" + sql);

                Statement stat = null;
                if (db != null) {
                    stat = db.getDbConn().createStatement();
                } else {
                    stat = oldConnection.createStatement();
                }

                int ret = stat.executeUpdate(sql);

                //debug
                //System.out.println("grid_insert_ret:" + ret);
            } else {
                //se non ??? ultima riga UPDATE
                sql = "update " + this.meta.getTableName(1) + " set ";
                for (int i = 0; i < nomiCampo.size(); i++) {
                    boolean ok = false;
                    if (colonneEditabili != null) {
                        for (int j = 0; j < colonneEditabili.length; j++) {
                            if (colonneEditabili[j] == (i + 1)) {
                                ok = true;
                                break;
                            }
                        }
                    } else {
                        ok = true;
                    }
                    if (ok) {
                        if (i == nomiCampo.size() - 1) {
                            sql += nomiCampo.get(i);
                            sql += " = " + pc(valoriCampo.get(i).toString(), tipiCampo.get(i).toString());
                        } else {
                            sql += nomiCampo.get(i);
                            sql += " = " + pc(valoriCampo.get(i).toString(), tipiCampo.get(i).toString()) + ", ";
                        }
                    }
                }
                sql += " where ";
                for (int i = 0; i < valoriChiave.size(); i++) {
                    sql += valoriChiave.get(i).toString();
                    //sql += this.dbChiave.get(i) + " = " + this.pc(valoriChiave.get(i).toString(), this.meta.getColumnTypeName(new Integer(this.columnsName.get(this.dbChiave.get(i)).toString()).intValue()));
                }

                //debug
                System.out.println("grid_update_sql:" + sql);

                Statement stat = null;
                if (db != null) {
                    stat = db.getDbConn().createStatement();
                } else {
                    stat = oldConnection.createStatement();
                }

                int ret = stat.executeUpdate(sql);

                //debug
                //System.out.println("grid_update_ret:" + ret);
            }
        } catch (Exception err) {
            err.printStackTrace();
        } finally {
            try {
                stat.close();
            } catch (Exception e) {
            }
        }
    }

    //FUNZIONI DI SOMMA COLONNA
    public int getColumnSumInt(int numero_colonna) {
        int cSum = 0;
        for (int i = 0; i < this.getRowCount(); i++) {
            cSum += Integer.parseInt(this.getValueAt(i, numero_colonna).toString().replaceAll(",", ""));
        }
        return cSum;
    }

    public int getColumnSumInt(String nome_colonna) {
        int numero_colonna = this.getColumnByName(nome_colonna);
        return this.getColumnSumInt(numero_colonna);
    }

    public double getColumnSumDouble(int numero_colonna) {
        double cSum = 0;
        for (int i = 0; i < this.getRowCount(); i++) {
            cSum += Double.parseDouble((this.getValueAt(i, numero_colonna)).toString());
        }
        return cSum;
    }

    public double getColumnSumDouble(String nome_colonna) {
        int numero_colonna = this.getColumnByName(nome_colonna);
        return this.getColumnSumDouble(numero_colonna);
    }
//    public void setColumnTitle(int iCol, String title) {
//        try {
//            TableColumnModel tcm = this.getColumnModel();
//            TableColumn tc = tcm.getColumn(iCol);
//            tc.setHeaderValue(title);
//        } catch (Exception err) {
//            //nothing
//        }
//    }
//    public void setColumnTitle(int iCol, tnxDbGridColumnTitle dbTitle) {
//        String title = dbTitle.langTitle;
//
//        if (columnsTitle == null) {
//            columnsTitle = new Hashtable();
//        }
//        this.columnsTitle.put(new Integer(iCol), title);
//        if (columnsTitleLang == null) {
//            columnsTitleLang = new Hashtable();
//        }
//        this.columnsTitleLang.put(dbTitle.defaultTitle, title);
//
//        try {
//            TableColumnModel tcm = this.getColumnModel();
//            TableColumn tc = tcm.getColumn(iCol);
//            tc.setHeaderValue(title);
//        } catch (Exception err) {
//            //nothing
//        }
//    }
}

class TRiempiGriglia2 extends Thread {

    Connection connection;
    java.sql.Statement stat;
    ResultSet resu;
    ResultSetMetaData meta;
    String sql;
    int numeColo;
    //DefaultTableModel tm;
    SortableTableModel tm;
    tnxDbGrid2 tabella;
    int[] colonneEditabili;
    private DbI db;
    //public TRiempiGriglia(String sql, Connection connection, tnxDbGrid tabella, DefaultTableModel tm) {

    public TRiempiGriglia2(String sql, Connection connection, tnxDbGrid2 tabella, SortableTableModel tm, int[] colonneEditabili, DbI db) {
        this.sql = sql;
        this.connection = connection;
        this.tabella = tabella;
        this.tm = tm;
        this.colonneEditabili = colonneEditabili;
        this.db = db;
    }

    public void run() {
        //debug
        //javax.swing.JOptionPane.showMessageDialog(null,"SQL GRID:" + gestionepreventivi.Db.spezza(sql,40,""));

        //this.setPriority(Thread.NORM_PRIORITY);
        this.setPriority(Thread.MAX_PRIORITY);
        //tabella.setEnabled(false);
        //tabella.setVisible(false);

        try {
            if (db != null) {
                stat = db.getDbConn().createStatement();
            } else {
                stat = connection.createStatement();
            }

            resu = stat.executeQuery(sql);
            meta = resu.getMetaData();
            tabella.meta = meta;

            //colonne
            numeColo = meta.getColumnCount() + 1;
            String Colo[] = new String[numeColo];

            Colo[0] = "";

            for (int i = 1; i <= numeColo - 1; ++i) {
                Colo[i] = meta.getColumnLabel(i);
                //carico hash nomi colonne
                tabella.columnsName.put(meta.getColumnLabel(i), new Integer(i));
                //debug
                //javax.swing.JOptionPane.showMessageDialog(null,Colo[i-1]);
            }

            //tm = new DefaultTableModel(Colo,0);
            tm = new SortableTableModel(Colo, 0, null, colonneEditabili);

            //righe
            while (resu.next()) {
                String reco[] = new String[numeColo];
                reco[0] = ">";
                for (int i = 1; i <= numeColo - 1; ++i) {
                    reco[i] = resu.getString(i);
                }
                tm.addRow(reco);
                this.yield();
            }
        } catch (Exception err) {
            err.printStackTrace();
        } finally {
            try {
                stat.close();
            } catch (Exception e) {
            }
            try {
                resu.close();
            } catch (Exception e) {
            }
            meta = null;
        }

        if (tabella.dbEditabile == true && tabella.dbConsentiAggiunte == true) {
            java.util.Vector tempRigaVuota = new java.util.Vector();
            tempRigaVuota.add("*");
            tm.addRow(tempRigaVuota);
        }

        tabella.setModel(tm);
        //abbino event listener
        if (tabella.dbEditabile == true) {
            tnxDbGrid2_eventi lisEventi = new tnxDbGrid2_eventi(tabella);
            tabella.getModel().addTableModelListener(lisEventi);
        }

        TableColumnModel defColu = tabella.getColumnModel();
        if (tabella.dbEditabile == false) {
            tabella.setDefaultEditor(tabella.getColumnClass(0), null);
        }
        boolean ret = tabella.setTm(tm);
        tabella.resizeColumnsPerc(true);

        //vado al primo record
        tabella.getSelectionModel().setSelectionInterval(0, 0);

        //tabella.setVisible(true);
        //tabella.setEnabled(true);

        //deubg
        //javax.swing.JOptionPane.showMessageDialog(null,"t end");
        //this.destroy();

        //data access
        //if (tabella.dbEditabile == true) {
        //aggiungo colonna info
        tabella.getColumn(tabella.getColumnName(0)).setCellRenderer(tabella.infoRender);
        //tabella.repaint();
        tabella.getColumnModel().getColumn(0).setWidth(20);
        tabella.getColumnModel().getColumn(0).setPreferredWidth(20);
        tabella.getColumnModel().getColumn(0).setMaxWidth(20);
        tabella.getColumnModel().getColumn(0).setMinWidth(20);
        tabella.getColumnModel().getColumn(0).setResizable(false);
        //aggiungo row per inserimento
        //}

        tabella.refreshRecords();

        //metto i renderers
        try {
            for (int i = 1; i <= tabella.meta.getColumnCount(); i++) {
                if (tabella.meta.getColumnType(i) == Types.DECIMAL || tabella.meta.getColumnType(i) == Types.DOUBLE) {
//                    tabella.getColumn(meta.getColumnLabel(i)).setCellRenderer(tabella.currencyRender);
//                    tabella.getColumn(meta.getColumnLabel(i)).setCellEditor(new CurrencyEditor2(this.tabella));
                }
            }
        } catch (SQLException sqlErr) {
            sqlErr.printStackTrace();
        }

        //table.getColumnModel().getColumn(3).setCellEditor(
        //	new IntegerEditor(0, 100));

        this.stop();
    }

    public void setColumnsSize(double[] percent) {
    }
}
/*
// Create a combo box to show that you can use one in a table.
JComboBox comboBox = new JComboBox();
comboBox.addItem("Red");
comboBox.addItem("Orange");
comboBox.addItem("Yellow");
comboBox.addItem("Green");
comboBox.addItem("Blue");
comboBox.addItem("Indigo");
comboBox.addItem("Violet");

TableColumn colorColumn = tableView.getColumn("Favorite Color");
// Use the combo box as the editor in the "Favorite Color" column.
colorColumn.setCellEditor(new DefaultCellEditor(comboBox));


// Show the values in the "Favorite Number" column in different colors.
TableColumn numbersColumn = tableView.getColumn("Favorite Number");
DefaultTableCellRenderer numberColumnRenderer = new DefaultTableCellRenderer() {
public void setValue(Object value) {
int cellValue = (value instanceof Number) ? ((Number)value).intValue() : 0;
setForeground((cellValue > 30) ? Color.black : Color.red);
setText((value == null) ? "" : value.toString());
}
};
numberColumnRenderer.setHorizontalAlignment(JLabel.RIGHT);
numbersColumn.setCellRenderer(numberColumnRenderer);
numbersColumn.setPreferredWidth(110);

 */

class tnxDbGrid2_eventi implements TableModelListener {

    private long prevRow = -1;
    tnxDbGrid2 parentGrid;

    tnxDbGrid2_eventi(tnxDbGrid2 parentGrid) {
        this.parentGrid = parentGrid;
    }

    ;

    public void tnxDbGrid2_eventi(tnxDbGrid2 parentGrid) {
        this.parentGrid = parentGrid;
    }

    public void tableChanged(javax.swing.event.TableModelEvent tableModelEvent) {
        //parentGrid.saveDataEntry();
        //controllo se devo salvare
        if (prevRow != tableModelEvent.getFirstRow() && 1 == 2) {
            //salvare inserire ... fare qualcosa
            try {
                //System.out.println("grid_fare:" + meta.toString());
                //SortableTableModel tempSource = (SortableTableModel)tableModelEvent.getSource();
                //System.out.println("grid_fare:" + this.parentGrid.getModel().getColumnCount());

                //griglia colonna e colonna modificata
                if (tableModelEvent.getType() == tableModelEvent.UPDATE) {
                    //aggiorno
                    //System.out.println("grid_fare:colName=" + this.parentGrid.columnsName.get(new Integer(tableModelEvent.getColumn())));

                    //debug
                    java.util.Hashtable tempHash = this.parentGrid.columnsName;
                    java.util.Enumeration tempElem = tempHash.elements();
                    java.util.Enumeration tempKeys = tempHash.keys();
                    while (tempElem.hasMoreElements()) {
                        //System.out.println("grid_fre_keys:" + tempElem.nextElement() + " : " + tempKeys.nextElement());
                    }
                    //System.out.println("grid_fare_1:colName=" + this.parentGrid.columnsName.get("1"));
                    //System.out.println("grid_fare_1:colName=" + this.parentGrid.columnsName.get("codice"));
                    //provo con meta almeno ho anche il tippo di campo
                    //System.out.println("grid_meta:" + this.parentGrid.meta.getColumnLabel(1));
                    //System.out.println("grid_meta:" + this.parentGrid.meta.getColumnType(1));
                    //System.out.println("grid_meta:" + this.parentGrid.meta.getColumnLabel(tableModelEvent.getColumn()));
                    //ok
                    //provo a cercare la chiave
                    Vector valoriChiave = new Vector();
                    for (int i = 0; i < this.parentGrid.dbChiave.size(); i++) {
                        //System.out.println("grid_chiave_nome  :" + i + ":" + this.parentGrid.dbChiave.get(i));
                        //System.out.println("grid_chiave_valore:" + i + ":" + this.parentGrid.getValueAt(tableModelEvent.getFirstRow(), new Integer(this.parentGrid.columnsName.get(this.parentGrid.dbChiave.get(i)).toString()).intValue()));
                        String valore = this.parentGrid.getValueAt(tableModelEvent.getFirstRow(), new Integer(this.parentGrid.columnsName.get(this.parentGrid.dbChiave.get(i)).toString()).intValue()).toString();
                        String tipo = this.parentGrid.meta.getColumnTypeName(new Integer(this.parentGrid.columnsName.get(this.parentGrid.dbChiave.get(i)).toString()).intValue());
                        valoriChiave.add(this.parentGrid.dbChiave.get(i) + " = " + this.parentGrid.pc(valore, tipo));
                        //System.out.println("grid_chiave_vector:" + i + ":" + valoriChiave.get(i));
                    }
                    //ok prendo valore nuovo del campo e nome del campo
                    String nomeCampo = "";
                    String valoreCampo = this.parentGrid.getValueAt(tableModelEvent.getFirstRow(), tableModelEvent.getColumn()).toString();
                    String tipoCampo = this.parentGrid.meta.getColumnTypeName(tableModelEvent.getColumn());
                    //System.out.println("grid_novo_campo:" + valoreCampo + ":" + tipoCampo + ":" + nomeCampo);
                }

            } catch (Exception err) {
                err.printStackTrace();
            }
        }
        prevRow = tableModelEvent.getFirstRow();

        /*
        System.out.println("tnxDbGrid:events:"+tableModelEvent);
        //System.out.println("tnxDbGrid:events1:"+tableModelEvent.getColumn());
        System.out.println("tnxDbGrid:events2:"+tableModelEvent.getFirstRow());
        System.out.println("tnxDbGrid:events3:"+tableModelEvent.getLastRow());
        //System.out.println("tnxDbGrid:events4:"+tableModelEvent.getSource());
        System.out.println("tnxDbGrid:events5:"+tableModelEvent.getType());
        //System.out.println("tnxDbGrid:events6:"+tableModelEvent.INSERT);
        //System.out.println("tnxDbGrid:events7:"+tableModelEvent.UPDATE);
         */

        SortableTableModel tempSource = (SortableTableModel) tableModelEvent.getSource();
        if (tableModelEvent.getLastRow() == tempSource.getRowCount() - 1 && tableModelEvent.getType() == tableModelEvent.UPDATE && tableModelEvent.getColumn() == tempSource.getColumnCount() - 1 && parentGrid.dbConsentiAggiunte) {
            tempSource.setValueAt(">", tempSource.getRowCount() - 1, 0);
            //javax.swing.JOptionPane.showMessageDialog(null,"inserimento");
            java.util.Vector tempRigaVuota = new java.util.Vector();
            tempRigaVuota.add("*");
            tempSource.addRow(tempRigaVuota);
        }
    }
}

class CurrencyEditor2 extends DefaultCellEditor {

    JFormattedTextField ftf;
    NumberFormat integerFormat;
    private Integer minimum, maximum;
    private boolean DEBUG = false;
    tnxDbGrid2 grid = null;

    public CurrencyEditor2(tnxDbGrid2 grid) {
        super(new JFormattedTextField());
        this.grid = grid;
        ftf = (JFormattedTextField) getComponent();

        //Set up the editor for the integer cells.
        integerFormat = NumberFormat.getInstance(Locale.ITALIAN);
        integerFormat.setMaximumFractionDigits(5);
        integerFormat.setMinimumFractionDigits(2);
        NumberFormatter intFormatter = new NumberFormatter(integerFormat);
        intFormatter.setFormat(integerFormat);

        ftf.setFormatterFactory(new DefaultFormatterFactory(intFormatter));
        ftf.setValue(new Double(0.0d));
        ftf.setHorizontalAlignment(JTextField.TRAILING);
        ftf.setFocusLostBehavior(JFormattedTextField.PERSIST);

        //React when the user presses Enter while the editor is
        //active.  (Tab is handled as specified by
        //JFormattedTextField's focusLostBehavior property.)
        ftf.getInputMap().put(KeyStroke.getKeyStroke(
                KeyEvent.VK_ENTER, 0),
                "check");
        ftf.getActionMap().put("check", new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                if (!ftf.isEditValid()) { //The text is invalid.
                    //if (userSaysRevert()) { //reverted

                    ftf.postActionEvent(); //inform the editor
                    //}

                } else {
                    try {              //The text is valid,

                        ftf.commitEdit();     //so use it.

                        ftf.postActionEvent(); //stop editing

                        CurrencyEditor2.this.grid.saveDataEntry(CurrencyEditor2.this.grid.getSelectedRow());
                    } catch (java.text.ParseException exc) {
                    }
                }
            }
        });
    }
    //Override to invoke setValue on the formatted text field.

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        JFormattedTextField ftf = (JFormattedTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);
        if (tnxDbGrid2.getDoubleEng(String.valueOf(value)) > 0) {
            Double d = new Double(tnxDbGrid2.getDoubleEng(String.valueOf(value)));
            ftf.setValue(d);
        } else {
            ftf.setValue(new Double(0));
        }
        return ftf;
    }

    //Override to ensure that the value remains an Integer.
    public Object getCellEditorValue() {
        JFormattedTextField ftf = (JFormattedTextField) getComponent();
        Object o = ftf.getValue();
        if (o instanceof Integer) {
            return o;
        } else if (o instanceof Number) {
            return o;
        } else {
            if (DEBUG) {
                System.out.println("getCellEditorValue: o isn't a Number");
            }
            try {
                return integerFormat.parseObject(o.toString());
            } catch (ParseException exc) {
                System.err.println("getCellEditorValue: can't parse o: " + o);
                return null;
            }
        }
    }
}

