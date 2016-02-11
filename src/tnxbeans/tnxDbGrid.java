/**
 * Invoicex
 * Copyright (c) 2005,2006,2007,2008,2009 Marco Ceccarelli, Tnx snc
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

import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfWriter;
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
//import com.lowagie.text.*;
//import com.lowagie.text.pdf.*;
import it.tnx.DbI;
import it.tnx.commons.SwingUtils;
import it.tnx.dbeans.ResultSet.LazyResultSetModel;
import java.sql.*;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

public class tnxDbGrid extends javax.swing.JTable implements Serializable, javax.swing.event.TableColumnModelListener, ListSelectionListener {

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
    public String[] colonneEditabiliByName;
    public DefaultTableCellRenderer currencyRender = null;
    private Connection connection;
    private java.sql.Statement stat;
    //private DefaultTableModel tm;
    //private SortableTableModel tm;
    private TableModel tm;
    public String oldSql;
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
    public DefaultTableCellRenderer dateRender;
    public DefaultTableCellRenderer dateTimeRender;
    //eventi per data entry
    private int prevRow = 0;
    private int prevCol = 0;
    private boolean dirty = false;
    public HeaderListener headerListener;
    public HeaderListener2 headerListener2;
    private boolean giaAgganciato = false;
    private DbI db;
    public int rollOverRowIndex = -1;
    private boolean noTnxResize = false;
    boolean lazy = false;
    boolean substance = false;

    public tnxDbGrid() {
        super();
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (UIManager.getLookAndFeel().getName().toLowerCase().indexOf("substance") >= 0) {
            substance = true;
        } else {
            RollOverListener lst = new RollOverListener();
            addMouseMotionListener(lst);
            addMouseListener(lst);
        }
    }

    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);
        if (!isEnabled()) {
            c.setForeground(Color.gray);
            return c;
        }
        if (!substance) {
            if (super.getCellRenderer(row, column).getClass().getName().startsWith("java")
                    || super.getCellRenderer(row, column).getClass().getName().startsWith("tnxbeans")
                    || super.getCellRenderer(row, column).getClass().getName().startsWith("it.tnx")) {
                if (isRowSelected(row)) {
                    c.setForeground(getSelectionForeground());
                    c.setBackground(getSelectionBackground());
                } else if (row == rollOverRowIndex) {
                    c.setForeground(getSelectionForeground().brighter());
                    c.setBackground(getSelectionBackground().brighter());
                } else {
                    c.setForeground(getForeground());
                    c.setBackground(getBackground());
                }
            } else {
                if (row == rollOverRowIndex) {
                    c.setBackground(SwingUtils.mixColours(getSelectionBackground(), c.getBackground()));
                }
            }
        }
        return c;
    }

    public Component prepareRendererOld(TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);
//        System.out.println(row + "|" + column + "|" + super.getCellRenderer(row, column).getClass().getName().startsWith("java"));
        if (!super.getCellRenderer(row, column).getClass().getName().startsWith("java")) {
            if (isRowSelected(row)) {
                c.setForeground(getSelectionForeground());
                c.setBackground(getSelectionBackground());
            } else if (row == rollOverRowIndex) {
                c.setForeground(getSelectionForeground().brighter());
//                if (c.getBackground() != null) {
//                    c.setBackground(ImgUtils.getMixedColor(c.getBackground(), 0.5f, getSelectionBackground().brighter(), 0.5f) );
//                } else {
//                    c.setBackground(ImgUtils.getMixedColor(getBackground(), 0.5f, getSelectionBackground().brighter(), 0.5f));
//                }
                c.setBackground(getBackground().brighter());
            } else {
                c.setForeground(getForeground());
                c.setBackground(c.getBackground());
            }
        } else {
            if (isRowSelected(row)) {
                c.setForeground(getSelectionForeground());
                c.setBackground(getSelectionBackground());
            } else if (row == rollOverRowIndex) {
                c.setForeground(getSelectionForeground().brighter());
//                c.setBackground(getSelectionBackground().brighter());
//                c.setBackground(ImgUtils.getMixedColor(getBackground(), 0.5f, getSelectionBackground().brighter(), 0.5f));
                c.setBackground(getSelectionBackground().brighter());
            } else {
                c.setForeground(getForeground());
                c.setBackground(getBackground());
            }
        }
        return c;
    }

    public boolean isNoTnxResize() {
        return noTnxResize;
    }

    public void setNoTnxResize(boolean noTnxResize) {
        this.noTnxResize = noTnxResize;
    }

    public void initRenders() {
        //per data
        if (!substance) {
            dateRender = new DefaultTableCellRenderer() {

                public void setValue(Object value) {
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
                    if (value != null) {
                        super.setValue(dateFormat.format((Date) value));
                    } else {
                        super.setValue("");
                    }
                }
            };
        } else {
            dateRender = new org.jvnet.substance.SubstanceDefaultTableCellRenderer() {

                public void setValue(Object value) {
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
                    if (value != null) {
                        super.setValue(dateFormat.format((Date) value));
                    } else {
                        super.setValue("");
                    }
                }
            };
        }

        //per data time
        if (!substance) {
            dateTimeRender = new DefaultTableCellRenderer() {

                public void setValue(Object value) {
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy hh:mm");
                    if (value != null) {
                        super.setValue(dateFormat.format(value));
                    } else {
                        super.setValue("");
                    }
                }
            };
        } else {
            dateTimeRender = new org.jvnet.substance.SubstanceDefaultTableCellRenderer() {

                public void setValue(Object value) {
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy hh:mm");
                    if (value != null) {
                        super.setValue(dateFormat.format(value));
                    } else {
                        super.setValue("");
                    }
                }
            };

        }

        //per valuta
        currencyRender = null;
        if (!substance) {
            currencyRender = new DefaultTableCellRenderer() {

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
                                double d = (Double.valueOf(value.toString())).doubleValue();
                                NumberFormat form = DecimalFormat.getInstance(Locale.ITALIAN);
                                form.setGroupingUsed(true);
                                form.setMaximumFractionDigits(5);
                                form.setMinimumFractionDigits(2);
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
            currencyRender = new org.jvnet.substance.SubstanceDefaultTableCellRenderer() {

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
                                form.setMinimumFractionDigits(2);
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


        //data access
        //per info
        infoRender = null;
        if (!substance) {
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
        } else {
            infoRender = new org.jvnet.substance.SubstanceDefaultTableCellRenderer() {

                public void setValue(Object value) {
                    super.setValue(value);
                    setHorizontalAlignment(JLabel.CENTER);
                }
            };

        }

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
        SimpleDateFormat fd1 = new SimpleDateFormat("dd/MM/yy");
        NumberFormat fn1 = NumberFormat.getInstance(Locale.ITALIAN);
        fn1.setMaximumFractionDigits(2);
        fn1.setMinimumFractionDigits(2);

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
            document = new Document(PageSize.A4, 10, 10, 20, 20);
            writer = PdfWriter.getInstance(document, new java.io.FileOutputStream(nomeFilePdf));
            //HtmlWriter.getInstance(document, new java.io.FileOutputStream(nomeFileHtml));
            document.addTitle("stampa tabella");
            document.addSubject("stampa tabella");
            document.addKeywords("stampa tabella");
            document.addAuthor("TNX s.a.s");
            document.addHeader("Expires", "0");
            document.open();
            //------------------------------------
            bf = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 6, com.lowagie.text.Font.NORMAL);
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

            int col_zero = 0;
            for (int col : headerWidth) {
                if (col == 0) {
                    col_zero++;
                }
            }
            int col_tutte = meta.getColumnCount();
            int col_vis = meta.getColumnCount() - col_zero;
            int[] headerWidth2 = new int[col_vis];
            int i = 0;
            for (int col : headerWidth) {
                if (col > 0) {
                    headerWidth2[i] = col;
                    i++;
                }
            }

            datatable = new Table(col_vis);
            datatable.setBorder(0);
//            datatable.setCellpadding(0);
            datatable.setPadding(2);
            //int headerwidths2[] = {20, 80}; // percentage
            //datatable.setWidths(headerwidths2);
            if (headerWidth != null) {
                datatable.setWidths(headerWidth2);
            }
            datatable.setWidth(100); // percentage

            datatable.setCellsFitPage(true);

            Phrase tempFrase;
            Cell tempPdfCell;

            //intestazione
            Phrase intestazione = new Phrase();
            intestazione.add(new Chunk(titolo, new com.lowagie.text.Font(com.lowagie.text.Font.TIMES_ROMAN, 9, com.lowagie.text.Font.BOLD)));
            document.add(intestazione);

            //tempPdfCell = new Cell(new Phrase(this. , new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 8, com.lowagie.text.Font.NORMAL)));
            //colonne
            for (i = 0; i <= col_tutte; i++) {
                try {
                    if (headerWidth[i] > 0) {
                        //tempPdfCell = new Cell(new Phrase(this.meta.getColumnLabel(i), bf));
                        tempPdfCell = new Cell(new Phrase(getColumnModel().getColumn(i).getHeaderValue().toString(), bf));
                        set1(tempPdfCell);
                        datatable.addCell(tempPdfCell);
                    }
                } catch (IndexOutOfBoundsException ie) {
                }
            }
            //righe
            for (int j = 0; j < tm.getRowCount(); j++) {
                //colonne
                for (i = 0; i < col_tutte; i++) {
                    try {
                        if (headerWidth[i] > 0) {
                            //controllo tipo di campo
                            Object o = getValueAt(j, i);
                            if (o == null) {
                                tempPdfCell = new Cell(new Phrase("", bf));
                            } else if (o instanceof java.sql.Date) {
                                tempPdfCell = new Cell(new Phrase(fd1.format(o), bf));
                            } else if (o instanceof Double) {
                                tempPdfCell = new Cell(new Phrase(fn1.format(o), bf));
                            } else {
                                tempPdfCell = new Cell(new Phrase(nz(String.valueOf(this.getValueAt(j, i)), ""), bf));
                            }
                            set2(tempPdfCell);
                            datatable.addCell(tempPdfCell);
                        }
                    } catch (IndexOutOfBoundsException ie) {
                    }
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
            javax.swing.JOptionPane.showMessageDialog(this, err.toString());
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

    void set2r(Cell tempPdfCell) {
        tempPdfCell.setBackgroundColor(new Color(255, 255, 255));
        tempPdfCell.setBorderColor(new Color(200, 200, 200));
        tempPdfCell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
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
        return dbOpen(connection, sql, db, false);
    }

    public boolean dbOpen(Connection connection, String sql) {
        return dbOpen(connection, sql, null, false);
    }

    public boolean dbOpen(Connection connection, String sql, DbI db, boolean lazy) {
        if (db != null) {
            this.db = db;
        }
        this.lazy = lazy;
        //riproporziono le larghezze a 100
        if (columnsSizePerc != null) {
            Set s = columnsSizePerc.keySet();
            Iterator iter = s.iterator();
            Double totale = 0d;
            while (iter.hasNext()) {
                totale += (Double) columnsSizePerc.get(iter.next());
            }
            iter = s.iterator();
            while (iter.hasNext()) {
                Object k = iter.next();
                Double current = (Double) columnsSizePerc.get(k);
                columnsSizePerc.put(k, current * 100d / totale);
            }
        }

        //aggancio a eventi del dbPanel se presente
        if (dbPanel != null && !giaAgganciato) {
            dbPanel.addDbListener(new DbListener() {

                public void statusFired(DbEvent event) {
                    if (event.getStatus() == dbPanel.STATUS_REFRESHING) {
                        dbPanel.sincronizzaSelezioneGriglia(tnxDbGrid.this);
                    }
                }
            });
            giaAgganciato = true;
        }

        //inizializzo renderers
        initRenders();

        if (!noTnxResize) {
            setAutoResizeMode(this.AUTO_RESIZE_OFF);
        }

        countOpens++;
        //debug
        //System.out.println("dbopen:" + this.getParent().getName() + " . " + countOpens);

        //resize event of the scrollpane
        this.getParent().addComponentListener(new java.awt.event.ComponentAdapter() {

            public void componentResized(ComponentEvent e) {
                this_componentResized(e);
            }
        });
        this.getParent().addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseEvent(MouseEvent e) {
                this_mouseEvent(e);
            }
        });
        this.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseEvent(MouseEvent e) {
                this_mouseEvent(e);
            }
        });

        this.oldSql = sql;
        this.oldConnection = connection;

        //apre il resultset da abbinare

        //if (countOpens > 1) {
        if (lazy == true) {
            //provo a fare senza thread una volta aperto l prima volta
            ResultSet resu = null;
            ResultSetMetaData meta;

            //no thread
            try {
                if (db != null) {
                    stat = db.getDbConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                } else {
                    stat = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                }

                resu = stat.executeQuery(sql + " limit 1");
                meta = resu.getMetaData();

                //colonne
                int numeColo = meta.getColumnCount();
                String Colo[] = new String[numeColo];
                for (int i = 1; i <= numeColo; ++i) {
                    //Colo[i-1] = meta.getColumnLabel(i);
                    Colo[i - 1] = meta.getColumnLabel(i);
                }

                //tm = new DefaultTableModel(Colo,0);
//                tm = new SortableTableModel(Colo, 0, meta, colonneEditabili);
                tm = new LazyResultSetModel(sql, db);

                if (this == null) {
                    return (true);
                } else {
                    this.setModel(tm);
                    if (dbEditabile == true) {
                        //abbino event listener
                        tnxDbGrid_eventi lisEventi = new tnxDbGrid_eventi(this);
                        this.tm.addTableModelListener(lisEventi);
                    }

                    //metto i renderers
                    for (int i = 1; i <= numeColo; ++i) {
                        if (meta.getColumnType(i) == Types.VARCHAR
                                || meta.getColumnType(i) == Types.CHAR) {
                            //this.getColumn(Colo[i-1]).setCellRenderer(cellRenderer)
                        } else if (meta.getColumnType(i) == Types.INTEGER
                                || meta.getColumnType(i) == Types.SMALLINT) {
                            //this.getColumn(Colo[i-1]).setCellRenderer(cellRenderer)
                        } else if (meta.getColumnType(i) == Types.BIGINT) {
                            //this.getColumn(Colo[i-1]).setCellRenderer(cellRenderer)
                        } else if (meta.getColumnType(i) == Types.DECIMAL
                                || meta.getColumnType(i) == Types.DOUBLE) {
                            //this.getColumn(meta.getColumnLabel(i)).setCellRenderer(currencyRender);
                            this.getColumn(meta.getColumnLabel(i)).setCellRenderer(currencyRender);
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

                    this.resizeColumnsPerc(true);

                    //vado al primo record
                    this.getSelectionModel().setSelectionInterval(0, 0);

                    refreshRecords();

//                    if (this.flagUsaOrdinamento == true) {
//                        openSort();
//                    }

                    try {
                        resu.getStatement().close();
                        resu.close();
                    } catch (Exception e) {
                    }
                    return (true);
                }

            } catch (Exception err) {
                err.printStackTrace();
                System.out.println("sql errore:" + sql);
            }
        } else if (flagUsaThread == false) {
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

                System.out.println("sql: " + sql);
                resu = stat.executeQuery(sql);
                meta = resu.getMetaData();
                this.meta = meta;

                //colonne
                final int numeColo = meta.getColumnCount();
                String Colo[] = new String[numeColo];
                for (int i = 1; i <= numeColo; ++i) {
                    //Colo[i-1] = meta.getColumnLabel(i);
                    Colo[i - 1] = meta.getColumnLabel(i);
                }
                //tm = new DefaultTableModel(Colo,0);
                tm = new SortableTableModel(Colo, 0, meta, colonneEditabili, colonneEditabiliByName);
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

                        if (meta.getColumnType(i) == Types.VARCHAR
                                || meta.getColumnType(i) == Types.CHAR) {
                            //System.out.println("varchar row:"+numRec+" col:"+i+" value:"+resu.getObject(i));
                            reco[i - 1] = resu.getString(i);
                        } else if (meta.getColumnType(i) == Types.INTEGER
                                || meta.getColumnType(i) == Types.SMALLINT) {
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
                        } else if (meta.getColumnType(i) == Types.DECIMAL
                                || meta.getColumnType(i) == Types.DOUBLE) {
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
                    ((SortableTableModel) tm).addRow(reco);
                }

                if (this == null) {
                    return (true);
                } else {
                    final ResultSetMetaData fmeta = meta;
                    SwingUtils.inEdtWait(new Runnable() {

                        public void run() {
                            setModel(tm);
                            if (dbEditabile == true) {
                                //abbino event listener
                                tnxDbGrid_eventi lisEventi = new tnxDbGrid_eventi(tnxDbGrid.this);
                                tm.addTableModelListener(lisEventi);
                            }

                            //metto i renderers
                            try {                                
                                for (int i = 1; i <= numeColo; ++i) {
                                    if (fmeta.getColumnType(i) == Types.VARCHAR
                                            || fmeta.getColumnType(i) == Types.CHAR) {
                                    } else if (fmeta.getColumnType(i) == Types.INTEGER
                                            || fmeta.getColumnType(i) == Types.SMALLINT) {
                                    } else if (fmeta.getColumnType(i) == Types.BIGINT) {
                                    } else if (fmeta.getColumnType(i) == Types.DECIMAL
                                            || fmeta.getColumnType(i) == Types.DOUBLE) {
                                        getColumn(fmeta.getColumnLabel(i)).setCellRenderer(currencyRender);
                                    } else if (fmeta.getColumnType(i) == Types.DATE) {
                                        getColumn(fmeta.getColumnLabel(i)).setCellRenderer(dateRender);
                                    } else if (fmeta.getColumnType(i) == Types.TIMESTAMP) {
                                        getColumn(fmeta.getColumnLabel(i)).setCellRenderer(dateTimeRender);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //this.meta = meta;
                            TableColumnModel defColu = getColumnModel();

                            if (dbEditabile == false) {
                                setDefaultEditor(getColumnClass(1), null);
                            }

                            resizeColumnsPerc(true);

                            //vado al primo record
                            getSelectionModel().setSelectionInterval(0, 0);

                            refreshRecords();

                            if (flagUsaOrdinamento == true) {
                                openSort();
                            }
                        }
                    });

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
            TRiempiGriglia ciclo = new TRiempiGriglia(sql, connection, this, (SortableTableModel) tm, colonneEditabili, db);
            ciclo.start();
        }
        return (true);
    }

    void openSort() {
        //sort
        if (headerListener != null) {
            JTableHeader header = this.getTableHeader();
            header.removeMouseListener(headerListener);
        }
        if (headerListener2 != null) {
            JTableHeader header = this.getTableHeader();
            header.removeMouseListener(headerListener2);
        }
//        SortButtonRenderer renderer = new SortButtonRenderer();
        SortButtonRenderer2 renderer = new SortButtonRenderer2();
        TableColumnModel model = this.getColumnModel();
        int n = model.getColumnCount();
        for (int i = 0; i < n; i++) {
            model.getColumn(i).setHeaderRenderer(renderer);
        }

        JTableHeader header = this.getTableHeader();
        headerListener2 = new HeaderListener2(header, renderer);
//        header.addMouseListener(headerListener);
        header.addMouseListener(headerListener2);
        //---
    }

    public void dbRefresh() {
        //aggancio a eventi del dbPanel se presente
        if (dbPanel != null && !giaAgganciato) {
            dbPanel.addDbListener(new DbListener() {

                public void statusFired(DbEvent event) {
                    if (event.getStatus() == dbPanel.STATUS_REFRESHING) {
                        dbPanel.sincronizzaSelezioneGriglia(tnxDbGrid.this);
                    }
                }
            });
            giaAgganciato = true;
        }

        //mi salvo la riga dov'ero prima e provo a ritornarci
        oldSelectedRow = this.getSelectedRow();
        dbOpen(this.oldConnection, this.oldSql, db, lazy);

        if (headerListener != null) {
            headerListener.resort();
        }

        if (dbPanel != null) {
            dbPanel.sincronizzaSelezioneGriglia(tnxDbGrid.this);
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
            javax.swing.JOptionPane.showMessageDialog(this, err.toString());
        }
        //eseguo la query
        try {
            //debug
            //javax.swing.JOptionPane.showMessageDialog(null,sql);
            System.out.println("dbGrid:sqldelete:" + sql);

            if (db != null) {
                stat = db.getDbConn().createStatement();
            } else {
                stat = oldConnection.createStatement();
            }
            stat.execute(sql);
            //1.1
            //this.dbRefresh();
            //---
            ((DefaultTableModel) tm).removeRow(this.getSelectedRow());
            //***
        } catch (Exception err) {
            err.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this, err.toString());
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
        if (getRowCount() <= 0) {
            return;
        }

        if (this.dbPanel != null && this.getSelectedRowCount() > 0) {

            if (this.getValueAt(this.getSelectedRow(), 0).toString().equals("*") && !dbPanel.dbStato.equals(tnxDbPanel.DB_LETTURA)) {
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
                    } else {
                        dbPanel.dbUndo();
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
            if (String.valueOf(getValueAt(i, colIndex)).equals(String.valueOf(value))) {
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

        if (getRowCount() <= 0) {
            isFinding = false;
            return false;
        }

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
                        if (this.dbPanel.butSaveClose != null) {
                            this.dbPanel.butSaveClose.setEnabled(false);
                        }
                        if (this.dbPanel.butUndo != null) {
                            this.dbPanel.butUndo.setEnabled(false);
                            //if (this.dbPanel.butFind!=null) this.dbPanel.butFind.setEnabled(false);
                        }
                    }
                }
            }
        }
        if (!trovato) {
            JOptionPane.showMessageDialog(dbPanel, "Questo campo non è ricercabile");
            isFinding = false;
            return true;
        }
        
        //prosegue
        if (dbFindNext() == false) {
            javax.swing.JOptionPane.showMessageDialog(dbPanel, "Posizione inesistente");
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
            if (this.getColumnName(i).equalsIgnoreCase(dbPanel.ultimoCampo)) {
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
                        if (this.dbPanel.butSaveClose != null) {
                            this.dbPanel.butSaveClose.setEnabled(false);
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
        if (!trovato) {
            JOptionPane.showMessageDialog(dbPanel, "Questo campo non è ricercabile");
                isFinding = false;
            return true;
        }
        isFinding = false;
        return (false);
    }

    public boolean dbFindFirstSub() {
        isFinding = true;

        if (getRowCount() <= 0) {
            isFinding = false;
            return false;
        }
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
                        if (this.dbPanel.butSaveClose != null) {
                            this.dbPanel.butSaveClose.setEnabled(false);
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
            javax.swing.JOptionPane.showMessageDialog(dbPanel, "Posizione inesistente");
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
                        if (this.dbPanel.butSaveClose != null) {
                            this.dbPanel.butSaveClose.setEnabled(false);
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

    public boolean dbFindExact() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");

        isFinding = true;

        if (getRowCount() <= 0) {
            isFinding = false;
            return false;
        }
        setRowSelectionInterval(0, 0);

        //trovo id colonna
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
                            if (griglia.equalsIgnoreCase(campo)) {
                                this.setRowSelectionInterval(j, j);
                                this.scrollToRow(this.getSelectedRow());
                                dbSelezionaRiga();

                                if (this.dbPanel.butSave != null) {
                                    this.dbPanel.butSave.setEnabled(false);
                                }
                        if (this.dbPanel.butSaveClose != null) {
                            this.dbPanel.butSaveClose.setEnabled(false);
                        }
                                if (this.dbPanel.butUndo != null) {
                                    this.dbPanel.butUndo.setEnabled(false);
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

        this.tableHeader.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mousePressed(MouseEvent e) {
//                this_mousePressed(e);
            }
        });

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
                if (!tempParent.getClass().getName().equalsIgnoreCase("javax.swing.JTabbedPane")) {
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
        if (!noTnxResize) {
            if (this.isResizing == false) {
                this.resizeColumnsPerc(false);
            }
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
//        if (noTnxResize) return;
        if (this.columnsSizePerc == null) {
            return;
        }
        String columnNameLingua = "";
        int colonnetotali = getColumnCount();
        int colonnesizeperc = 0;
        //dimensionamento in base al vettore
        //resize columns in percent
        if (this.columnsSizePerc != null) {
            colonnesizeperc = columnsSizePerc.size();
            //get all columns size

            double colsWidth = getParent().getWidth();
            if (colsWidth == 0) {
                JInternalFrame parent = SwingUtils.getParentJInternalFrame(this);
                if (parent != null) {
                    parent.validate();
                    colsWidth = getParent().getWidth();
                }
            }
            if (colsWidth == 0) return;

            if (this.getColumnCount() != this.columnsSizePerc.size()) {
                //javax.swing.JOptionPane.showMessageDialog(this, "il numero di colonne di cols size perc ? diverso dal numero di colonne nella griglia(" + String.valueOf(this.getColumnCount()) + ")");
                System.out.println("debug:griglia, numero colonne diverso:" + this.getColumnCount() + ":v=" + this.columnsSizePerc.size());
            }

            if (this.getColumnCount() > 0 && this.isResizing == false && colsWidth > 0) {
                if (this.oldWidth != this.getParent().getWidth() || forceResize == true) {
                    //debug
                    this.isResizing = true;
                    countResize++;

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
                            try {
                                try {
                                    col = this.getColumn(String.valueOf(columnName));
                                } catch (Exception err) {
                                    col = this.getColumn(String.valueOf(columnNameLingua));
                                }
                                colWidthPerc = (Double) columnsSizePerc.get(columnName);

                                //col.setWidth((int)(colsWidth / 100 * colWidthPerc.doubleValue()));
                                if (colWidthPerc.doubleValue() != 0.0) {
                                    col.setPreferredWidth((int) (colsWidth / 100d * colWidthPerc.doubleValue()));
                                    //debug
                                    //System.out.println("resize misure1:" + colsWidth);
                                    //System.out.println("resize misure2:" + colWidthPerc.doubleValue());
                                    //System.out.println("resize misure3:" + (int)(colsWidth / 100.0 * colWidthPerc.doubleValue()));
                                } else {
                                    col.setMinWidth(0);
                                    col.setWidth(0);
                                    col.setPreferredWidth(0);
                                    col.setMaxWidth(0);
                                    col.setResizable(false);
                                }
                            } catch (Exception err) {
                                System.err.println("non trovata colonna:" + columnName + " err:" + err.getMessage());
                            }
                        }
                        contaColonne++;
                    }

                    //rimetto i nomi
                    if (columnsTitle != null) {
                        for (java.util.Enumeration e = columnsTitle.keys(); e.hasMoreElements();) {
                            Integer columnIndex = (Integer) e.nextElement();
                            String caption = (String) columnsTitle.get((Object) columnIndex);
                            this.setColumnTitle(columnIndex.intValue(), caption);
                        }
                    }
                    //--------------

                    //imposto ultima colonna
                    TableColumn lastcol = null;
                    int rimanente = ((JViewport) getParent()).getWidth();
                    int totcol = 0;
                    int dimlastcol = 0;
                    for (int i = 0; i < getColumnCount(); i++) {
                        TableColumn colc = getColumnModel().getColumn(i);
                        if (colc.getPreferredWidth() > 0) {
                            totcol += colc.getPreferredWidth();
                            dimlastcol = colc.getPreferredWidth();
                            rimanente -= dimlastcol;
                            lastcol = colc;
                        }
                    }

//                    System.err.println("totcol: " + totcol);
//                    System.err.println("rimanente: " + rimanente);
//                    System.err.println("dimlastcol: " + dimlastcol);
//                    System.err.println("lastcol: " + lastcol);
//                    System.err.println("rimanente + dimlastcol:" + (rimanente + dimlastcol));

                    lastcol.setPreferredWidth(rimanente + dimlastcol);

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

    public boolean hasColumn(String col) {
        try {
            getColumn(col);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public int getColumnByName(String colName) {
        for (int i = 0; i < this.getColumnCount(); i++) {
            if (this.getColumnName(i).equalsIgnoreCase(colName)) {
                return (i);
            }
        }
        return (-1);
    }

    public void valueChanged(javax.swing.event.ListSelectionEvent listSelectionEvent) {
        //debug
        //System.out.println(listSelectionEvent.toString());

        if (this.dbEditabile == true) {
            //controllo cambio di riga
            if (this.prevRow != this.getSelectedRow() && listSelectionEvent.getValueIsAdjusting() == false || this.prevCol != this.getSelectedColumn() && listSelectionEvent.getValueIsAdjusting() == false) {
                //debug
                //System.out.prevColintln("cambioRiga");

                if (tm != null && tm instanceof SortableTableModel) {
                    if (((SortableTableModel) tm).dirty == true) {
                        this.dirty = true;

                        //debug
                        //System.out.println("dirty:true");

                        saveDataEntry(this.prevRow);

                        this.dirty = false;
                        ((SortableTableModel) tm).dirty = false;
                    }
                }
                this.prevRow = this.getSelectedRow();
            }
        }
        super.valueChanged(listSelectionEvent);
    }

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

    public void setColumnTitle(int iCol, String title) {
        try {
            TableColumnModel tcm = this.getColumnModel();
            TableColumn tc = tcm.getColumn(iCol);
            tc.setHeaderValue(title);
        } catch (Exception err) {
            //nothing
        }
    }

    public void setColumnTitle(int iCol, tnxDbGridColumnTitle dbTitle) {
        String title = dbTitle.langTitle;

        if (columnsTitle == null) {
            columnsTitle = new Hashtable();
        }
        this.columnsTitle.put(new Integer(iCol), title);
        if (columnsTitleLang == null) {
            columnsTitleLang = new Hashtable();
        }
        this.columnsTitleLang.put(dbTitle.defaultTitle, title);

        try {
            TableColumnModel tcm = this.getColumnModel();
            TableColumn tc = tcm.getColumn(iCol);
            tc.setHeaderValue(title);
        } catch (Exception err) {
            //nothing
        }
    }
}

class TRiempiGriglia extends Thread {

    Connection connection;
    java.sql.Statement stat;
    ResultSet resu;
    ResultSetMetaData meta;
    String sql;
    int numeColo;
    //DefaultTableModel tm;
    SortableTableModel tm;
    tnxDbGrid tabella;
    int[] colonneEditabili;
    private DbI db;
    //public TRiempiGriglia(String sql, Connection connection, tnxDbGrid tabella, DefaultTableModel tm) {

    public TRiempiGriglia(String sql, Connection connection, tnxDbGrid tabella, SortableTableModel tm, int[] colonneEditabili, DbI db) {
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
            tnxDbGrid_eventi lisEventi = new tnxDbGrid_eventi(tabella);
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
                    tabella.getColumn(meta.getColumnLabel(i)).setCellRenderer(tabella.currencyRender);
                    tabella.getColumn(meta.getColumnLabel(i)).setCellEditor(new CurrencyEditor(this.tabella));
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

class HeaderListener extends MouseAdapter {

    JTableHeader header;
    SortButtonRenderer renderer;
    int oldSortedColumn = -1;
    boolean oldIsAscent = false;

    HeaderListener(JTableHeader header, SortButtonRenderer renderer) {
        this.header = header;
        this.renderer = renderer;
    }

    public void resort() {
        if (oldSortedColumn >= 0) {
            System.out.println("resort: column:" + oldSortedColumn);
            if (header.getTable().isEditing()) {
                header.getTable().getCellEditor().stopCellEditing();
            }
            ((SortableTableModel) header.getTable().getModel()).sortByColumn(oldSortedColumn, oldIsAscent);
        }
    }

    public void mousePressed(MouseEvent e) {
        if (e.getClickCount() == 2) {
            int col = header.columnAtPoint(e.getPoint());
            int sortCol = header.getTable().convertColumnIndexToModel(col);
            oldSortedColumn = sortCol;
            renderer.setPressedColumn(col);
            renderer.setSelectedColumn(col);

            if (header.getTable().isEditing()) {
                header.getTable().getCellEditor().stopCellEditing();
            }

            boolean isAscent;
            if (SortButtonRenderer.DOWN == renderer.getState(col)) {
                isAscent = true;
            } else {
                isAscent = false;
            }
            oldIsAscent = isAscent;
            System.out.println("resort: column:" + sortCol);
            ((SortableTableModel) header.getTable().getModel()).sortByColumn(sortCol, isAscent);

            header.repaint();
        }
    }

    public void mouseReleased(MouseEvent e) {
//        if(e.getClickCount()==1){
//            int col = header.columnAtPoint(e.getPoint());
//            renderer.setPressedColumn(-1); // clear
//            header.repaint();
//        }
    }
}

class HeaderListener2 extends MouseAdapter {

    JTableHeader header;
    SortButtonRenderer2 renderer;
    int oldSortedColumn = -1;
    boolean oldIsAscent = false;

    HeaderListener2(JTableHeader header, SortButtonRenderer2 renderer) {
        this.header = header;
        this.renderer = renderer;
    }

    public void resort() {
        if (oldSortedColumn >= 0) {
            System.out.println("resort: column:" + oldSortedColumn);
            if (header.getTable().isEditing()) {
                header.getTable().getCellEditor().stopCellEditing();
            }
            ((SortableTableModel) header.getTable().getModel()).sortByColumn(oldSortedColumn, oldIsAscent);
        }
    }

    public void mousePressed(MouseEvent e) {
        if (e.getClickCount() == 2) {
            int col = header.columnAtPoint(e.getPoint());
            int sortCol = header.getTable().convertColumnIndexToModel(col);
            oldSortedColumn = sortCol;
            renderer.setPressedColumn(col);
            renderer.setSelectedColumn(col);

            if (header.getTable().isEditing()) {
                header.getTable().getCellEditor().stopCellEditing();
            }

            boolean isAscent;
            if (SortButtonRenderer2.DOWN == renderer.getState(col)) {
                isAscent = true;
            } else {
                isAscent = false;
            }
            oldIsAscent = isAscent;
            System.out.println("resort: column:" + sortCol);
            ((SortableTableModel) header.getTable().getModel()).sortByColumn(sortCol, isAscent);

            header.repaint();
        }
    }
}