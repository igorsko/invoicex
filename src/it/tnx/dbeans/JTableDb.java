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
package it.tnx.dbeans;

import java.io.Serializable;
import java.beans.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.table.*;
import javax.swing.event.*;
import java.sql.*;
import java.util.Vector;
import java.util.Hashtable;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JLabel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;

//per la stampa
//import com.lowagie.text.*;
//import com.lowagie.text.pdf.*;
//import com.lowagie.text.html.*;
import java.sql.*;

import tnxbeans.*;

import it.tnx.dbeans.ResultSet.*;

/**
 *
 * @author  marco
 */
public class JTableDb extends javax.swing.JTable implements Serializable, javax.swing.event.TableColumnModelListener {

    public Vector dbChiave;
    public tnxDbPanel dbPanel;
    public JScrollPane scrollPane;
    public String dbNomeTabella;
    public boolean dbEditabile = false;
    private Connection connection;
    private java.sql.Statement stat;
    //private DefaultTableModel tm;
    private SortableTableModel tm;
    private it.tnx.dbeans.ResultSet.ScrollingResultSetTableModel tm2;
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
    private boolean isResizing = false;
    private int countOpens = 0;
    private int countResize = 0;
    public DefaultTableCellRenderer infoRender;    //eventi per data entry
    private int prevRow = 0;
    private boolean dirty = false;
    private boolean giaAgganciato = false;

    /** Creates new Grid */
    public JTableDb() {
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
            if( row != rollOverRowIndex ) {
                rollOverRowIndex = row;
                repaint();
            }
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

    public boolean dbOpen(Connection connection, String sql) {
        //aggancio a eventi del dbPanel se presente
        if (dbPanel != null && !giaAgganciato) {
            dbPanel.addDbListener(new DbListener() {
                public void statusFired(DbEvent event) {
                    if (event.getStatus() == dbPanel.STATUS_REFRESHING) {
                        dbPanel.sincronizzaSelezioneGrigliaJTableDb(JTableDb.this);
                    }
                }
            });
            giaAgganciato = true;
        }
        
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
        DefaultTableCellRenderer currencyRender = new DefaultTableCellRenderer() {

            public void setValue(Object value) {
                //super.setValue(value);
                DecimalFormat form = new DecimalFormat("#,##0.00");
                try {
                    super.setValue(form.format(value));
                    if (form.format(value).startsWith("-")) {
                        setForeground(new Color(200, 0, 0));
                    } else {
                        setForeground(Color.black);
                    }
                } catch (Exception err) {
                    System.out.println("grid2 cannot format");
                    super.setValue(null);
                }
                setHorizontalAlignment(JLabel.RIGHT);
            }
        };
        //data access
        //per info
        infoRender = new DefaultTableCellRenderer() {

            public void setValue(Object value) {
                super.setValue(value);
                if (value.toString().equals("*") && dbEditabile) {
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
        this.getParent().addComponentListener(new java.awt.event.ComponentAdapter() {

            public void componentResized(ComponentEvent e) {
                //this_componentResized(e);
            }
        });
        this.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                this_mousePressed(e);
            }
        });

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
                stat = connection.createStatement(
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                resu = stat.executeQuery(sql);
                meta = resu.getMetaData();

                tm2 = new it.tnx.dbeans.ResultSet.ScrollingResultSetTableModel(resu);

                //colonne
                int numeColo = meta.getColumnCount();
                String Colo[] = new String[numeColo];
                for (int i = 1; i <= numeColo; ++i) {
                    Colo[i - 1] = meta.getColumnName(i);
                }
                //tm = new DefaultTableModel(Colo,0);
                tm = new SortableTableModel(Colo, 0, meta);
                //righe
                int numRec = 0;
                while (resu.next()) {
                    numRec++;
                    Object reco[] = new Object[numeColo];
                    for (int i = 1; i <= numeColo; ++i) {

                        //debug
                        //System.out.println("colType:"+i+":"+meta.getColumnType(i) + "colName:"+meta.getColumnName(i));
                        //System.out.println("colType VARCHAR:"+Types.VARCHAR);
                        //System.out.println("colType CHAR:"+Types.CHAR);            

                        if (meta.getColumnType(i) == Types.VARCHAR ||
                                meta.getColumnType(i) == Types.CHAR) {
                            //System.out.println("varchar row:"+numRec+" col:"+i+" value:"+resu.getObject(i));
                            reco[i - 1] = resu.getString(i);
                        } else if (meta.getColumnType(i) == Types.INTEGER ||
                                meta.getColumnType(i) == Types.SMALLINT) {
                            //System.out.println("int row:"+numRec+" col:"+i+" value:"+resu.getObject(i));
                            reco[i - 1] = new Integer(resu.getInt(i));
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
                    //this.setModel(tm);
                    this.setModel(tm2);
                    if (dbEditabile == true) {
                        //abbino event listener
                        //tnxDbGrid_eventi lisEventi = new tnxDbGrid_eventi(this);
                        //this.tm.addTableModelListener(lisEventi);
                    }

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
                            this.getColumn(meta.getColumnName(i)).setCellRenderer(currencyRender);
                        } else if (meta.getColumnType(i) == Types.DATE) {
                            this.getColumn(meta.getColumnName(i)).setCellRenderer(dateRender);
                        } else if (meta.getColumnType(i) == Types.TIMESTAMP) {
                            this.getColumn(meta.getColumnName(i)).setCellRenderer(dateTimeRender);
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
                    //this.resizeColumnsPerc(true);
                    //System.out.println("open:dopo resize:" + tm.getRowCount() + " : " + this.getRowCount());

                    //vado al primo record
                    this.getSelectionModel().setSelectionInterval(0, 0);

                    if (this.flagUsaOrdinamento == true) {
                        //openSort();
                    }
                    return (true);
                }
            } catch (Exception err) {
                err.printStackTrace();
                System.out.println("sql errore:" + sql);
//            } finally {
//                try {
//                    stat.close();
//                } catch (Exception e) {
//                }
//                try {
//                    resu.close();
//                } catch (Exception e) {
//                }
//                meta = null;
            }
        } else {
            //thread
            //TRiempiGriglia ciclo = new TRiempiGriglia(sql, connection, this, tm);
            //ciclo.start();      
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

            try {
                if (this.getValueAt(this.getSelectedRow(), 0).toString().equals("*") && dbEditabile) {
                    return;            //133 chiedo conferma quando si sposta
                }                
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("panel stato:" + this.dbPanel.dbStato);
            if (!this.dbPanel.dbStato.equals(this.dbPanel.DB_LETTURA) && isFinding == false) {
                int ret = javax.swing.JOptionPane.showConfirmDialog(this.getRootPane(), "Salvare le modifiche apportate ?", "Attenzione", javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE);
                if (ret == javax.swing.JOptionPane.YES_OPTION) {
                    //salvare
                    this.dbPanel.dbSave();
                    this.dbRefresh();
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
                for (int j = 0; j < tm.getColumnCount(); j++) {
                    //debug
                    //javax.swing.JOptionPane.showMessageDialog(null,"tm:="+tm.getColumnName(j)+" chiave:"+this.dbChiave.get(i));
                    if (tm.getColumnName(j).equalsIgnoreCase((String) this.dbChiave.get(i))) {
                        trovato = true;
                        idCampo = j;
                        j = tm.getColumnCount();
                    }
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

    public boolean dbFindExact() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");

        isFinding = true;

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

    public void dbRefresh() {
        //aggancio a eventi del dbPanel se presente
        if (dbPanel != null && !giaAgganciato) {
            dbPanel.addDbListener(new DbListener() {

                public void statusFired(DbEvent event) {
                    if (event.getStatus() == dbPanel.STATUS_REFRESHING) {
                        dbPanel.sincronizzaSelezioneGrigliaJTableDb(JTableDb.this);
                    }
                }
            });
            giaAgganciato = true;
        }

        //mi salvo la riga dov'ero prima e provo a ritornarci
        oldSelectedRow = this.getSelectedRow();
        dbOpen(this.oldConnection, this.oldSql);
        
        if (dbPanel != null) {
            dbPanel.sincronizzaSelezioneGrigliaJTableDb(this);
        } else {
            //provo a ritornarci sulla riga dov'ero prima
            try {
                if (this.getRowCount() > oldSelectedRow && oldSelectedRow >= 0 && this.getRowCount() > 0) {
                    //debug
                    System.out.println("grid refresh:rowcount:" + this.getRowCount() + " oldrow:" + oldSelectedRow);
                    this.setRowSelectionInterval(oldSelectedRow, oldSelectedRow);
                    this.scrollToRow(this.getSelectedRow());
                } else {
                    this.setRowSelectionInterval(0, 0);
                    this.scrollToRow(this.getSelectedRow());
                }
            } catch (Exception err) {
                //System.out.println("grid refresh:rowcount:" + this.getRowCount() + " oldrow:" + oldSelectedRow);
                err.printStackTrace();
            }
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

    void this_mouseClicked(MouseEvent e) {
    }

    void this_mousePressed(MouseEvent e) {
        dbSelezionaRiga();

        //1.3.3
        if (e.getClickCount() == 2) {
            //javax.swing.JOptionPane.showMessageDialog(null,"doppio click");
            if (this.dbPanel != null) {
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

}