/**
 * Invoicex Copyright (c) 2005,2006,2007,2008,2009 Marco Ceccarelli, Tnx snc
 *
 * Questo software è soggetto, e deve essere distribuito con la licenza GNU
 * General Public License, Version 2. La licenza accompagna il software o potete
 * trovarne una copia alla Free Software Foundation http://www.fsf.org .
 *
 * This software is subject to, and may be distributed under, the GNU General
 * Public License, Version 2. The license should have accompanied the software
 * or you may obtain a copy of the license from the Free Software Foundation at
 * http://www.fsf.org .
 *
 * -- Marco Ceccarelli (m.ceccarelli@tnx.it) Tnx snc (http://www.tnx.it)
 *
 */
package it.tnx.invoicex.gui;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jidesoft.hints.AbstractListIntelliHints;
import gestioneFatture.ClienteHint;
import gestioneFatture.CoordinateBancarie;
import it.tnx.Db;
import gestioneFatture.InvoicexEvent;
import gestioneFatture.Scadenze;
import gestioneFatture.Util;
import gestioneFatture.diaDistRiba;
import gestioneFatture.frmPagaPart;
import gestioneFatture.main;
import gestioneFatture.Menu;
import gestioneFatture.dbFattura;
import gestioneFatture.dbOrdine;
import gestioneFatture.frmElenFatt;
import gestioneFatture.frmTestDocu;
import gestioneFatture.frmTestFatt;
import gestioneFatture.frmTestFattAcquisto;
import gestioneFatture.frmTestOrdine;
import gestioneFatture.iniFileProp;
import gestioneFatture.prnDistRb;
import it.tnx.accessoUtenti.Utente;
import it.tnx.commons.CastUtils;
import it.tnx.commons.DateUtils;
import it.tnx.commons.DbUtils;
import it.tnx.commons.DebugUtils;
import it.tnx.commons.FormatUtils;
import it.tnx.commons.KeyValuePair;
import it.tnx.commons.MicroBench;
import it.tnx.commons.SwingUtils;
import it.tnx.commons.swing.DelayedExecutor;
import it.tnx.invoicex.InvoicexUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDesktopPane;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import org.apache.commons.lang.StringUtils;
import tnxbeans.tnxComboField;
import tnxbeans.tnxDbGrid;

public class SituazioneClienti extends JPanel {

    JPanel panTop;
    public JToolBar tooMenu;
    int order;
    JPanel panPrefParent;
    JPanel panPref;
    JPanel panPrefExtra;
    JPanel panResult;
//    tnxComboField comClie = new tnxComboField();
    JTextField texClie = new JTextField();
    tnxComboField comCategoriaClifor = new tnxComboField();
    tnxComboField comPagamento = new tnxComboField();
    JTextField texDal = new JTextField();
    JTextField texAl = new JTextField();
    public JCheckBox cheStam = new JCheckBox();
    JCheckBox chePaga = new JCheckBox();
    JCheckBox nonFatturati = new JCheckBox();
    JCheckBox cheVisNoteDiCredito = new JCheckBox();
    JCheckBox cheVisProforma = new JCheckBox();
    JCheckBox cheVisDaInviare = new JCheckBox();
    tnxComboField comBanca = new tnxComboField();
    boolean opening = true;
    JRadioButton radCresc = new JRadioButton("Crescente");
    JRadioButton radDescr = new JRadioButton("Decrescente");
    ButtonGroup grpOrdine = new ButtonGroup();
    String orderClause = "";
    int rigaAttuale = 0;
    AbstractListIntelliHints alRicercaCliente = null;
    boolean tutti_i_clienti = false;
    Integer cliente_selezionato = null;
    public tnxDbGrid griglia = new tnxDbGrid() {
        String tooltipText;
        boolean sameRow;
//        Color color_pagata = new Color(230, 255, 230);
//        Color color_non_pagata = new Color(255, 230, 230);
//        Color color_parziale = new Color(255, 255, 230);
//        Color color_hover = new Color(200, 200, 200);
//        Color color_sel = new Color(155, 155, 155);
        Color color_pagata = new Color(200, 255, 200);
        Color color_non_pagata = new Color(255, 200, 200);
        Color color_parziale = new Color(255, 255, 200);
        Color color_hover = new Color(255, 255, 255);
        Color color_sel = new Color(160, 160, 160);

        @Override
        public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
            Component c = super.prepareRenderer(renderer, row, column);

            Color back = colorForRow(row, c);

            c.setForeground(Color.BLACK);
            if (isRowSelected(row)) {
                c.setBackground(SwingUtils.mixColours(back, color_sel));
            } else if (row == rollOverRowIndex) {
                c.setBackground(SwingUtils.mixColours(back, color_hover));
            } else {
                c.setBackground(back);
            }

            return c;
        }

        protected Color colorForRow(int row, Component c) {
            int col = getColumnByName("Pagata");
            try {
                if (getValueAt(row, col).toString().equalsIgnoreCase("S")) {
                    return color_pagata;
                } else if (getValueAt(row, col).toString().equalsIgnoreCase("N")) {
                    return color_non_pagata;
                } else {
                    return color_parziale;
                }
            } catch (Exception e) {
            }
            return c.getBackground();
        }

        public String convertValueToText(Object value, int row, int column) {
            if (value != null) {
                String sValue = value.toString();
                if (sValue != null) {
                    return sValue;
                }
            }
            return "";
        }

        @Override
        public String getToolTipText(MouseEvent event) {
            if (sameRow) {
                return tooltipText;
            } else {
                String tip = null;
                Point p = event.getPoint();
                int hitRowIndex = rowAtPoint(p);
                String campoUtente = "cliente";
                int numero = 0;
                //BigInteger numEsterno = BigInteger.ZERO;
                String numEsterno = null;
                int anno = Integer.parseInt(String.valueOf(getValueAt(hitRowIndex, getColumnByName("anno"))));
                String serie = String.valueOf(getValueAt(hitRowIndex, getColumnByName("serie")));
                if (situazioneFornitori) {
                    campoUtente = "fornitore";
                    if (numEntrambi.isSelected()) {
                        numero = Integer.parseInt(String.valueOf(getValueAt(hitRowIndex, getColumnByName("interno"))));
                        numEsterno = String.valueOf(getValueAt(hitRowIndex, getColumnByName("numero")));
                    } else if (numEste.isSelected()) {
                        numEsterno = String.valueOf(getValueAt(hitRowIndex, getColumnByName("numero")));
                        String sql = "SELECT numero FROM test_fatt_acquisto where numero_doc = '" + Db.aa(numEsterno) + "' AND anno = " + anno + " AND serie_doc = '" + serie + "'";
                        ResultSet rs = Db.openResultSet(sql);
                        try {
                            rs.next();
                            numero = rs.getInt("numero");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        numero = Integer.parseInt(String.valueOf(getValueAt(hitRowIndex, getColumnByName("interno"))));
                        String sql = "SELECT numero_doc FROM test_fatt_acquisto where numero = " + numero + " AND anno = " + anno + " AND serie_doc = '" + serie + "'";
                        ResultSet rs = Db.openResultSet(sql);
                        try {
                            rs.next();
                            numEsterno = rs.getString("numero_doc");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    numero = Integer.parseInt(String.valueOf(getValueAt(hitRowIndex, getColumnByName("numero"))));
                }
                String cliente = String.valueOf(getValueAt(hitRowIndex, getColumnByName(campoUtente)));
                String tipoDocumento = String.valueOf(getValueAt(hitRowIndex, getColumnByName("documento_tipo")));
                String dataDocumento = String.valueOf(getValueAt(hitRowIndex, getColumnByName("data")));
                Date dataDocumentod = (Date) getValueAt(hitRowIndex, getColumnByName("data"));
                String pagamento = String.valueOf(getValueAt(hitRowIndex, getColumnByName("pagamento")));
                String dataScadenza = String.valueOf(getValueAt(hitRowIndex, getColumnByName("data scadenza")));
                Date dataScadenzad = (Date) getValueAt(hitRowIndex, getColumnByName("data scadenza"));
                Double totale = Double.parseDouble(String.valueOf(getValueAt(hitRowIndex, getColumnByName("totale"))));
                int id = Integer.parseInt(String.valueOf(getValueAt(hitRowIndex, getColumnByName("id"))));

                String sql = "SELECT sum(importo) as totale FROM scadenze_parziali WHERE id_scadenza = " + id;
                ResultSet rs = Db.openResultSet(sql);

                Double daPagare = 0.0;
                try {
                    if (rs.next()) {
                        daPagare = rs.getDouble("totale");
                    } else {
                        daPagare = totale;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                daPagare = totale - daPagare;

                if (tipoDocumento.equals("FA")) {
                    tipoDocumento = "Fattura";
                } else if (tipoDocumento.equals("FR")) {
                    tipoDocumento = "Fattura di Acquisto";
                } else {
                    tipoDocumento = "Ordine";
                }
                tip = "<html><b>Cliente: </b>" + cliente + "<br>";
//                tip += "<b>Documento: </b>" + tipoDocumento + " " + numero + " del " + dataDocumento;
                tip += "<b>Documento: </b>" + tipoDocumento + " " + numero + " del " + DateUtils.formatDate(dataDocumentod);
                if (situazioneFornitori) {
                    tip += " (Num. Esterno: " + numEsterno + ") <br>";
                }
                tip += "<br><b>Pagamento: </b>" + pagamento + "<br>";
                tip += "<b>Scadenza: </b>" + DateUtils.formatDate(dataScadenzad) + "<br>";
                tip += "<b>Totale: </b>" + FormatUtils.formatEuroIta(totale) + "<br>";
                tip += "<b>Da Pagare: </b>" + FormatUtils.formatEuroIta(daPagare) + "</html>";

                tooltipText = tip;
                return tip;
            }
        }

        // makes the tooltip's location to match table cell location
        // also avoids showing empty tooltips
        public Point getToolTipLocation(MouseEvent event) {
            int row = rowAtPoint(event.getPoint());
            if (row == -1) {
                return null;
            }
            int col = columnAtPoint(event.getPoint());
            if (col == -1) {
                return null;
            }

            sameRow = (row == rigaAttuale);
            boolean hasTooltip = getToolTipText() == null ? getToolTipText(event) != null : true;
            rigaAttuale = row;
//            return hasTooltip ? getCellRect(row, 1, false).getLocation(): null;
            Point p = event.getPoint();
            p.translate(10, 10);
            return hasTooltip ? p : null;
        }
    };
    JButton butSeleTutt = new JButton();
    JButton butDeseTutt = new JButton();
    JLabel labTotale = new JLabel("...");
    JButton butPrin = new JButton("Stampa");
    JButton butPrin1 = new JButton("Stampa Distinta Riba per Banca");
    JRadioButton radClienti = new JRadioButton("Situazione Clienti / Riscossioni");
    JRadioButton radFornitori = new JRadioButton("Situazione Fornitori / Pagamenti");
    JRadioButton radOrdini = new JRadioButton("Situazione Clienti / Riscossioni da Ordini o Preventivi");
    ButtonGroup groTipoSituazione = new ButtonGroup();
    JRadioButton numEste = new JRadioButton("Solo Numero Esterno");
    JRadioButton numInte = new JRadioButton("Solo Numero Interno");
    JRadioButton numEntrambi = new JRadioButton("Sia Interno che Esterno");
    ButtonGroup groNumeroInteEste = new ButtonGroup();
    boolean situazioneClienti = true;
    boolean situazioneFornitori = false;
    boolean situazioneOrdini = false;
    boolean fatturati = true;
    JComboBox ordine = new JComboBox();
    JLabel labCliente = new JLabel("Cliente");
    JLabel labCategoriaCliente = new JLabel("Categoria Cliente");
    JButton butRefresh = new JButton("Aggiorna");
    String sql;
    public JPopupMenu pop = new JPopupMenu();
    JMenu menColAgg = new JMenu();
    JCheckBoxMenuItem menColAggRiferimentoCliente = new JCheckBoxMenuItem();
    JTextField riferimento = new JTextField();

    /**
     * Default constructor
     */
    public SituazioneClienti() {
        try {
            InvoicexEvent event = new InvoicexEvent(this);
            event.type = InvoicexEvent.TYPE_SITUAZIONECLIENTI_CONSTR_PRE_INIT_COMPS;
            main.events.fireInvoicexEvent(event);
        } catch (Exception err) {
            err.printStackTrace();
        }
        texClie.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                alRicercaCliente.showHints();
                texClie.selectAll();
            }
        });

        radCresc.setBorder(new EmptyBorder(2, 4, 2, 2));
        radDescr.setBorder(new EmptyBorder(2, 4, 2, 2));

        ToolTipManager ttm = ToolTipManager.sharedInstance();
        ttm.setDismissDelay(10000);
        butPrin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/document-print.png")));
        butPrin1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/document-print.png")));
        butRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/view-refresh.png")));

        //layout
        setLayout(new BorderLayout());

        groTipoSituazione.add(radClienti);
        groTipoSituazione.add(radFornitori);
        groNumeroInteEste.add(numInte);
        groNumeroInteEste.add(numEste);
        groNumeroInteEste.add(numEntrambi);

        if (main.fileIni.getValueBoolean("pref", "scadenzeOrdini", false)) {
            groTipoSituazione.add(radOrdini);
        }
        radClienti.setSelected(true);

        radClienti.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selezionaSituazione();
            }
        });
        radFornitori.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selezionaSituazione();
            }
        });

        if (main.fileIni.getValueBoolean("pref", "scadenzeOrdini", false)) {
            radOrdini.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    selezionaSituazione();
                }
            });
        }

        numEntrambi.setSelected(true);
        numEntrambi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selezionaSituazione();
            }
        });

        numInte.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selezionaSituazione();
            }
        });

        numEste.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selezionaSituazione();
            }
        });

        butRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selezionaSituazione();
            }
        });

        tooMenu = build_toolBar();
        panPref = build_pref();
//        panPref.setBorder(new LineBorder(Color.green));
        panPrefExtra = build_pref_extra();
//        panPrefExtra.setBorder(new LineBorder(Color.yellow));
        panResult = build_result();

        add(tooMenu, BorderLayout.NORTH);
        JPanel panBody = new JPanel();
        panBody.setLayout(new BorderLayout());
        panPrefParent = new JPanel();
        panPrefParent.setLayout(new GridBagLayout());

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        panPrefParent.add(panPref, gridBagConstraints);

        panBody.add(panPrefParent, BorderLayout.NORTH);
        panBody.add(panResult, BorderLayout.CENTER);
        add(panBody, BorderLayout.CENTER);


        JMenuItem menu1 = new JMenuItem("Apri Documento", new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/apps/accessories-text-editor.png")));
        menu1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (griglia.getSelectedRowCount() <= 0) {
                    SwingUtils.showErrorMessage(getTopLevelAncestor(), "Seleziona un documento prima!");
                    return;
                }

                getTopLevelAncestor().setCursor(new Cursor(Cursor.WAIT_CURSOR));

                //controllo se sono aperti altri frmTestFatt
                //if (this.controllaAperFatt() == false) {
                String dbSerie = "";
                int dbNumero = 0;
                int dbAnno = 0;
                Integer dbId = null;
                DebugUtils.dumpJTable(griglia, griglia.getSelectedRow());
                if (radFornitori.isSelected()) {
                    dbSerie = String.valueOf(griglia.getValueAt(griglia.getSelectedRow(), griglia.getColumnByName("serie2")));
                    dbNumero = Integer.parseInt(String.valueOf(griglia.getValueAt(griglia.getSelectedRow(), griglia.getColumnByName("numero2"))));
                    dbAnno = Integer.parseInt(String.valueOf(griglia.getValueAt(griglia.getSelectedRow(), griglia.getColumnByName("anno2"))));
                } else {
                    dbSerie = String.valueOf(griglia.getValueAt(griglia.getSelectedRow(), griglia.getColumnByName("serie")));
                    dbNumero = Integer.parseInt(String.valueOf(griglia.getValueAt(griglia.getSelectedRow(), griglia.getColumnByName("numero"))));
                    dbAnno = Integer.parseInt(String.valueOf(griglia.getValueAt(griglia.getSelectedRow(), griglia.getColumnByName("anno"))));
                }

                JInternalFrame frm = null;

                if (radClienti.isSelected()) {
                    dbId = InvoicexUtil.getIdFattura(dbSerie, dbNumero, dbAnno);
                    frm = new frmTestFatt(frmTestDocu.DB_MODIFICA, dbSerie, dbNumero, "P", dbAnno, dbFattura.TIPO_FATTURA_NON_IDENTIFICATA, dbId);
                } else if (radOrdini.isSelected()) {
                    dbId = InvoicexUtil.getIdOrdine(dbSerie, dbNumero, dbAnno);
                    frm = new frmTestOrdine(frmTestOrdine.DB_MODIFICA, dbSerie, dbNumero, "P", dbAnno, dbOrdine.TIPO_ORDINE, dbId);
                } else if (radFornitori.isSelected()) {
                    dbId = InvoicexUtil.getIdFatturaAcquisto(dbSerie, dbNumero, dbAnno);
                    frm = new frmTestFattAcquisto(frmTestFatt.DB_MODIFICA, dbSerie, dbNumero, "P", dbAnno, dbFattura.TIPO_FATTURA_ACQUISTO, dbId);
                }

                Menu m = (Menu) main.getPadre();
                m.openFrame(frm, 740, InvoicexUtil.getHeightIntFrame(750));

                getTopLevelAncestor().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        menColAgg.setText("Colonne Aggiuntive");
        menColAggRiferimentoCliente.setText("Riferimento Cliente/Fornitore");
        menColAggRiferimentoCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                System.out.println("ColAgg_RiferimentoCliente = " + menColAggRiferimentoCliente.isSelected());
                main.fileIni.setValue("pref", "ColAgg_RiferimentoCliente", menColAggRiferimentoCliente.isSelected());
                controllaPanPrefExtra();
                selezionaSituazione();
            }
        });
        menColAgg.add(menColAggRiferimentoCliente);

        pop.add(menu1);
        pop.add(menColAgg);

        if (main.fileIni.getValueBoolean("pref", "ColAgg_RiferimentoCliente", false)) {
            menColAggRiferimentoCliente.setSelected(true);
        }

        riferimento.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                delay_rif.update();
            }
        });

        controllaPanPrefExtra();

        //events
        butSeleTutt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSeleTuttActionPerformed(evt);
            }
        });
        butDeseTutt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butDeseTuttActionPerformed(evt);
            }
        });
        butPrin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                butPrinActionPerformed(evt);
                butPrinActionPerformed2(evt);
            }
        });
        butPrin1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butPrin1ActionPerformed(evt);
            }
        });

        ordine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                aggiornaOrdine();
            }
        });

        radDescr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                aggiornaOrdine();
            }
        });

        radCresc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                aggiornaOrdine();
            }
        });

        griglia.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                grigliaMouseClicked(evt);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    System.out.println("popup");
                    pop.show(griglia, e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    System.out.println("popup");
                    pop.show(griglia, e.getX(), e.getY());
                }
            }
        });

        griglia.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                order = griglia.getTableHeader().columnAtPoint(new Point(x, y));
            }
        });
        griglia.setFont(griglia.getFont().deriveFont(griglia.getFont().getSize2D() + 1f));
        griglia.setRowHeight(griglia.getRowHeight() + 4);

        if (main.pluginEmail) {
            griglia.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int r = griglia.rowAtPoint(e.getPoint());
                    int c = griglia.columnAtPoint(e.getPoint());
                    try {
                        if (c == griglia.getColumn("Email Inviata").getModelIndex() && CastUtils.toInteger0(griglia.getValueAt(r, c)) >= 1) {
                            griglia.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        } else {
                            griglia.setCursor(Cursor.getDefaultCursor());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }

//        comClie.addItemListener(new java.awt.event.ItemListener() {
//            public void itemStateChanged(java.awt.event.ItemEvent evt) {
//                comClieItemStateChanged(evt);
//            }
//        });
        chePaga.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chePagaItemStateChanged(evt);
            }
        });

        nonFatturati.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                nonFatturatiItemStateChanged(evt);
            }
        });
        cheStam.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cheStamItemStateChanged(evt);
            }
        });
        cheVisNoteDiCredito.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                selezionaSituazione();
            }
        });
        cheVisProforma.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                selezionaSituazione();
            }
        });

        cheVisDaInviare.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                selezionaSituazione();
            }
        });

        comPagamento.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                if (evt.getStateChange() == ItemEvent.SELECTED) {
                    comPagamentoItemStateChanged(evt);
                }
            }
        });
        texDal.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                texDalFocusLost(evt);
            }
        });
        texDal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                texDalKeyPressed(evt);
            }

            public void keyTyped(java.awt.event.KeyEvent evt) {
                texDalKeyTyped(evt);
            }
        });
        texAl.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                texAlFocusLost(evt);
            }
        });
        texAl.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                texAlKeyTyped(evt);
            }
        });
        comBanca.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comBancaItemStateChanged(evt);
            }
        });

        comCategoriaClifor.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    selezionaSituazione();
                }
            }
        });

        try {
            InvoicexEvent event = new InvoicexEvent(this);
            event.type = InvoicexEvent.TYPE_SITUAZIONECLIENTI_CONSTR_POST_INIT_COMPS;
            main.events.fireInvoicexEvent(event);
        } catch (Exception err) {
            err.printStackTrace();
        }

        //
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                init();
            }
        });

        alRicercaCliente = new AbstractListIntelliHints(texClie) {
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
                        if (index == 0) {
                            return lab;
                        }
                        String word = current_search.toLowerCase();
                        String content = tipo.toLowerCase();
                        Color c = lab.getBackground();
                        c = c.darker();
                        String rgb = Integer.toHexString(c.getRGB());
                        rgb = rgb.substring(2, rgb.length());
                        content = StringUtils.replace(content, word, "<span style='background-color: " + rgb + "'>" + word + "</span>");
                        lab.setText("<html>" + content + "</html>");
                        System.out.println("lab " + index + " text:" + content);
                        return lab;
                    }
                });
                return list;
            }

            public boolean updateHints(Object arg0) {
//                if (arg0.toString().trim().length() <= 1) return false;
                SwingUtils.mouse_wait();
                current_search = arg0.toString();
                Connection conn;
                try {
                    conn = Db.getConn();

                    String sql = ""
                            + "SELECT codice, ragione_sociale FROM clie_forn"
                            + " where codice like '%" + Db.aa(current_search) + "%'"
                            + " or ragione_sociale like '%" + Db.aa(current_search) + "%'"
                            + " order by ragione_sociale, codice limit 50";

                    System.out.println("sql ricerca:" + sql);
                    ResultSet rs = DbUtils.tryOpenResultSet(conn, sql);
                    Vector v = new Vector();

                    ClienteHint cliente_tutti = new ClienteHint();
                    cliente_tutti.codice = "*";
                    cliente_tutti.ragione_sociale = "<tutti>";
                    v.add(cliente_tutti);

                    while (rs.next()) {
                        ClienteHint cliente = new ClienteHint();
                        cliente.codice = rs.getString(1);
                        cliente.ragione_sociale = rs.getString(2);
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
//                super.acceptHint(arg0);
                try {
                    if (((ClienteHint) arg0).codice.equals("*")) {
                        tutti_i_clienti = true;
                        cliente_selezionato = null;
                    } else {
                        tutti_i_clienti = false;
                        cliente_selezionato = CastUtils.toInteger(((ClienteHint) arg0).codice);
                    }
                    texClie.setText(((ClienteHint) arg0).toString());
                    comPagamento.requestFocus();
                    selezionaSituazione();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        aggiornaOrdine();
    }

    private void controllaPanPrefExtra() {
        if (menColAggRiferimentoCliente.isSelected()) {
            if (panPrefExtra.getParent() == null) {
                GridBagConstraints gridBagConstraints = new GridBagConstraints();
                gridBagConstraints.gridy = 2;
                gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
                gridBagConstraints.weightx = 1.0;
                panPrefParent.add(panPrefExtra, gridBagConstraints);
                revalidate();
            }
        } else {
            if (panPrefExtra.getParent() != null) {
                panPrefParent.remove(panPrefExtra);
                revalidate();
            }
        }
    }

    public boolean caricaImpo() {
        texClie.setText(main.fileIni.getValue("situazione_clienti_fornitori", "texClie", "<tutti> [*]"));
        comCategoriaClifor.dbTrovaRiga(main.fileIni.getValue("situazione_clienti_fornitori", "comCategoriaClifor", "<tutte le categorie>"));
        comPagamento.dbTrovaRiga(main.fileIni.getValue("situazione_clienti_fornitori", "comPagamento", "Tutti i tipi di pagamento"));
        tutti_i_clienti = true;
        cliente_selezionato = null;
        if (main.fileIni.existKey("situazione_clienti_fornitori", "comClie") || main.fileIni.existKey("situazione_clienti_fornitori", "texClie")) {
//            comClie.dbTrovaRiga(main.fileIni.getValue("situazione_clienti_fornitori", "comClie"));
            texClie.setText(main.fileIni.getValue("situazione_clienti_fornitori", "texClie", "<tutti> [*]"));
            if (main.fileIni.getValueBoolean("situazione_clienti_fornitori", "tutti_i_clienti", true)) {
                tutti_i_clienti = true;
                cliente_selezionato = null;
            } else {
                tutti_i_clienti = false;
                cliente_selezionato = CastUtils.toInteger(main.fileIni.getValue("situazione_clienti_fornitori", "cliente_selezionato"));
            }
            comPagamento.dbTrovaRiga(main.fileIni.getValue("situazione_clienti_fornitori", "comPagamento", "Tutti i tipi di pagamento"));
            texDal.setText(main.fileIni.getValue("situazione_clienti_fornitori", "texDal"));
            texAl.setText(main.fileIni.getValue("situazione_clienti_fornitori", "texAl"));
            cheStam.setSelected((main.fileIni.getValue("situazione_clienti_fornitori", "cheStam").equals("true") ? true : false));
            chePaga.setSelected((main.fileIni.getValue("situazione_clienti_fornitori", "chePaga").equals("true") ? true : false));
            cheVisNoteDiCredito.setSelected((main.fileIni.getValue("situazione_clienti_fornitori", "cheVisNoteDiCredito").equals("true") ? true : false));
            cheVisProforma.setSelected((main.fileIni.getValue("situazione_clienti_fornitori", "cheVisProforma").equals("true") ? true : false));

            if (main.pluginEmail) {
                cheVisDaInviare.setSelected((main.fileIni.getValue("situazione_clienti_fornitori", "cheVisDaInviare").equals("true") ? true : false));
            }

            comBanca.dbTrovaRiga(main.fileIni.getValue("situazione_clienti_fornitori", "comBanca"));
            radClienti.setSelected((main.fileIni.getValue("situazione_clienti_fornitori", "radClienti").equals("true") ? true : false));
            radFornitori.setSelected((main.fileIni.getValue("situazione_clienti_fornitori", "radFornitori").equals("true") ? true : false));

            numEntrambi.setSelected((main.fileIni.getValue("numero_interno_esterno", "numEntrambi").equals("true") ? true : false));
            numInte.setSelected((main.fileIni.getValue("numero_interno_esterno", "numInte").equals("true") ? true : false));
            numEste.setSelected((main.fileIni.getValue("numero_interno_esterno", "numEste").equals("true") ? true : false));
            return true;
        }
        return false;
    }

    public void salvaImpo() {
//        main.fileIni.setValue("situazione_clienti_fornitori", "comClie", comClie.getSelectedItem());
        main.fileIni.setValue("situazione_clienti_fornitori", "texClie", texClie.getText());
        main.fileIni.setValue("situazione_clienti_fornitori", "tutti_i_clienti", tutti_i_clienti);
        main.fileIni.setValue("situazione_clienti_fornitori", "cliente_selezionato", cliente_selezionato);
        main.fileIni.setValue("situazione_clienti_fornitori", "comCategoriaClifor", comCategoriaClifor.getSelectedItem());
        main.fileIni.setValue("situazione_clienti_fornitori", "comPagamento", comPagamento.getSelectedItem());
        main.fileIni.setValue("situazione_clienti_fornitori", "texDal", texDal.getText());
        main.fileIni.setValue("situazione_clienti_fornitori", "texAl", texAl.getText());
        main.fileIni.setValue("situazione_clienti_fornitori", "cheStam", cheStam.isSelected());
        main.fileIni.setValue("situazione_clienti_fornitori", "chePaga", chePaga.isSelected());
        main.fileIni.setValue("situazione_clienti_fornitori", "cheVisNoteDiCredito", cheVisNoteDiCredito.isSelected());
        main.fileIni.setValue("situazione_clienti_fornitori", "cheVisProforma", cheVisProforma.isSelected());
        if (main.pluginEmail) {
            main.fileIni.setValue("situazione_clienti_fornitori", "cheVisDaInviare", cheVisDaInviare.isSelected());
        }
        main.fileIni.setValue("situazione_clienti_fornitori", "comBanca", comBanca.getSelectedItem());
        main.fileIni.setValue("situazione_clienti_fornitori", "radClienti", radClienti.isSelected());
        main.fileIni.setValue("situazione_clienti_fornitori", "radFornitori", radFornitori.isSelected());

        main.fileIni.setValue("numero_interno_esterno", "numEntrambi", numEntrambi.isSelected());
        main.fileIni.setValue("numero_interno_esterno", "numInte", numInte.isSelected());
        main.fileIni.setValue("numero_interno_esterno", "numEste", numEste.isSelected());
    }

    private void texAlKeyTyped(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == 13) {
            selezionaSituazione();
        }
    }

    private void texDalKeyTyped(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == 13) {
            selezionaSituazione();
        }
    }

    private void texDalKeyPressed(java.awt.event.KeyEvent evt) {
    }

    private void texAlFocusLost(java.awt.event.FocusEvent evt) {
        selezionaSituazione();
    }

    private void texDalFocusLost(java.awt.event.FocusEvent evt) {
        selezionaSituazione();
    }

    private void comPagamentoItemStateChanged(java.awt.event.ItemEvent evt) {
        selezionaSituazione();
    }

    private void aggiornaOrdine() {
        orderClause = String.valueOf(((KeyValuePair) ordine.getSelectedItem()).key);
        if (!orderClause.equals("")) {
            radDescr.setEnabled(true);
            radCresc.setEnabled(true);
            if (radDescr.isSelected()) {
                int index = orderClause.indexOf(",");
                if (index >= 0) {
                    String[] clausole = orderClause.split(",");
                    clausole[0] += " DESC";
                    clausole[1] += " DESC";
                    orderClause = clausole[0] + "," + clausole[1];
                } else {
                    orderClause += " DESC";
                }
            }
        } else {
            radDescr.setEnabled(false);
            radCresc.setEnabled(false);
        }
        selezionaSituazione();
    }

    private void grigliaMouseClicked(java.awt.event.MouseEvent evt) {
        try {
            System.out.println("situazione clienti fornitori click");

            int r = griglia.rowAtPoint(evt.getPoint());
            int c = griglia.columnAtPoint(evt.getPoint());
            if (griglia.hasColumn("Email Inviata") && main.pluginEmail && c == griglia.getColumn("Email Inviata").getModelIndex() && CastUtils.toInteger0(griglia.getValueAt(r, c)) >= 1) {
                HashMap params = new HashMap();
                params.put("source", this);
                params.put("tipo", "Sollecito");
                params.put("id", CastUtils.toInteger(griglia.getValueAt(r, griglia.getColumnByName("id"))));
                InvoicexEvent event = new InvoicexEvent(params);
                event.type = InvoicexEvent.TYPE_EMAIL_STORICO;
                main.events.fireInvoicexEvent(event);
                return;
            }

            String serie = null;
            int numero = 0;
            int anno = 0;
            String pagamento = null;
            if (evt.getClickCount() == 2) {
                //javax.swing.JOptionPane.showMessageDialog(this, "debug1:" + this.griglia.getValueAt(griglia.getSelectedRow(), 0) + ":" +this.griglia.getValueAt(griglia.getSelectedRow(), 4) + ":" + this.griglia.getValueAt(griglia.getSelectedRow(), 5));
                Date data_doc = null;
                if (situazioneClienti || situazioneOrdini) {
                    serie = String.valueOf(this.griglia.getValueAt(griglia.getSelectedRow(), griglia.getColumnByName("serie")));
                    System.err.println("serie = " + serie);
                    numero = Integer.parseInt(String.valueOf(this.griglia.getValueAt(griglia.getSelectedRow(), griglia.getColumnByName("numero"))));
                    System.err.println("numero = " + numero);
                    anno = Integer.parseInt(String.valueOf(this.griglia.getValueAt(griglia.getSelectedRow(), griglia.getColumnByName("anno"))));
                    System.err.println("anno = " + anno);
                    data_doc = (Date) griglia.getValueAt(griglia.getSelectedRow(), griglia.getColumnByName("data"));
                } else if (situazioneFornitori) {
                    serie = String.valueOf(this.griglia.getValueAt(griglia.getSelectedRow(), griglia.getColumnByName("serie2")));
                    System.err.println("serie = " + serie);
                    numero = Integer.parseInt(String.valueOf(this.griglia.getValueAt(griglia.getSelectedRow(), griglia.getColumnByName("numero2"))));
                    System.err.println("numero = " + numero);
                    anno = Integer.parseInt(String.valueOf(this.griglia.getValueAt(griglia.getSelectedRow(), griglia.getColumnByName("anno2"))));
                    System.err.println("anno = " + anno);
                    data_doc = (Date) griglia.getValueAt(griglia.getSelectedRow(), griglia.getColumnByName("data"));
                }
                pagamento = String.valueOf(this.griglia.getValueAt(griglia.getSelectedRow(), 1));
                System.err.println("pagamento = " + pagamento);
                //javax.swing.JOptionPane.showMessageDialog(this, "debug2:" + serie + ":" + numero + ":" + anno + ":" + pagamento);
                Scadenze tempScad = null;
                if (situazioneClienti) {
                    tempScad = new Scadenze(Db.TIPO_DOCUMENTO_FATTURA, serie, numero, anno, null, data_doc);
                    System.err.println("tempScad = " + tempScad);
                } else if (situazioneFornitori) {
                    tempScad = new Scadenze(Db.TIPO_DOCUMENTO_FATTURA_RICEVUTA, serie, numero, anno, null, data_doc);
                    System.err.println("tempScad = " + tempScad);
                } else if (situazioneOrdini) {
                    tempScad = new Scadenze(Db.TIPO_DOCUMENTO_ORDINE, serie, numero, anno, null, data_doc);
                    System.err.println("tempScad = " + tempScad);
                }
                frmPagaPart frm = new frmPagaPart(tempScad, CastUtils.toInteger(griglia.getValueAt(griglia.getSelectedRow(), 0)));
                System.err.println("frm = " + frm);
                Point p = MouseInfo.getPointerInfo().getLocation();
                SwingUtilities.convertPointFromScreen(p, main.getPadrePanel().getDesktopPane());
                int x = p.x;
                int y = p.y;
                int w = 650;
                int h = 550;
                Dimension d = main.getPadrePanel().getDesktopPane().getSize();
                if (x + w > d.width) {
                    x = x - w;
                }
                if (y + h > d.height) {
                    y = y - h;
                }
                main.getPadre().openFrame(frm, w, h, y, x);
            } else if (evt.getButton() == MouseEvent.BUTTON2 || evt.getButton() == MouseEvent.BUTTON3) {
//                try {
//                    Runtime.getRuntime().exec("cmd /C start \"mailto:user@example.com?subject=Message Title&body=Message <b>Content</b>\"");
//                } catch (IOException ex) {
//                    Logger.getLogger(SituazioneClienti.class.getName()).log(Level.SEVERE, null, ex);
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void butSeleTuttActionPerformed(java.awt.event.ActionEvent evt) {
        ListSelectionModel sele = this.griglia.getSelectionModel();
        sele.setSelectionInterval(0, this.griglia.getRowCount() - 1);
        this.griglia.setSelectionModel(sele);
    }

    private void butDeseTuttActionPerformed(java.awt.event.ActionEvent evt) {
        ListSelectionModel sele = this.griglia.getSelectionModel();
        sele.clearSelection();
        this.griglia.setSelectionModel(sele);
    }

    private void butPrin1ActionPerformed(java.awt.event.ActionEvent evt) {
        stampaDistinta();
    }

    private void cheStamItemStateChanged(java.awt.event.ItemEvent evt) {
        selezionaSituazione();
    }

    private void chePagaItemStateChanged(java.awt.event.ItemEvent evt) {
        selezionaSituazione();
    }

    private void nonFatturatiItemStateChanged(java.awt.event.ItemEvent evt) {
        if (nonFatturati.isSelected()) {
            fatturati = false;
        } else {
            fatturati = true;
        }
        selezionaSituazione();
    }

//    private void comClieItemStateChanged(java.awt.event.ItemEvent evt) {
//        if (evt.getStateChange() == ItemEvent.SELECTED) {
//            selezionaSituazione();
//        }
//    }
    private void comBancaItemStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            selezionaSituazione();
        }
    }

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
        // Add your handling code here:
        this.griglia.resizeColumnsPerc(true);
    }

    private void jScrollPane1MouseClicked(java.awt.event.MouseEvent evt) {
    }

    private void butPrinActionPerformed2(java.awt.event.ActionEvent evt) {
//        griglia.columnsSizePerc.

        int[] hw = new int[griglia.getColumnCount()];
        for (int i = 0; i < griglia.getColumnCount(); i++) {
            try {
                hw[i] = ((Double) griglia.columnsSizePerc.get(griglia.getColumnName(i))).intValue();
            } catch (Exception e) {
                System.out.println("e:" + e + " i:" + i);
                hw[i] = 10;
            }
        }

        String header = "";
        if (situazioneClienti || situazioneOrdini) {
            header = "Situazione cliente : " + this.texClie.getText();
        } else {
            header = "Situazione fornitore : " + this.texClie.getText();
        }
        if (comCategoriaClifor.getSelectedIndex() > 0) {
            if (situazioneClienti || situazioneOrdini) {
                header += "\nCategoria cliente : " + comCategoriaClifor.getSelectedItem();
            } else {
                header += "\nCategoria fornitore : " + comCategoriaClifor.getSelectedItem();
            }
        }
        header += "\nPeriodo dal : " + texDal.getText() + "  al : " + texAl.getText();

        String file = griglia.stampaTabella(header, hw, this.labTotale.getText());

        System.out.println("apro:" + file);
//        SwingUtils.open(new File(file));
        Util.start(file);
    }

    private void butPrinActionPerformed(java.awt.event.ActionEvent evt) {
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

        String campoOrdine = griglia.getColumnName(order);

        String tab = "test_fatt";
        if (situazioneFornitori) {
            tab = "test_fatt_acquisto";
        }
        if (situazioneOrdini) {
            tab = "test_ordi";
        }

//        String campo_totale = "totale";
//        if (situazioneFornitori) {
//            campo_totale = "importo";
//        }

        String campo_totale = tab + ".totale";
        if (tab.equals("test_fatt")) {
            campo_totale = "if(IFNULL(" + tab + ".totale_da_pagare,0)>0, " + tab + ".totale_da_pagare, " + tab + ".totale) as totale";
        } else if (situazioneFornitori) {
            campo_totale = "if(IFNULL(" + tab + ".totale_da_pagare,0)>0, " + tab + ".totale_da_pagare, " + tab + ".importo) as totale";
        }

        String campo_cliente = "cliente";
        if (situazioneFornitori) {
            campo_cliente = "fornitore";
        }
        String campo_numero = "numero";
        String campo_numero1 = "";
        if (situazioneFornitori) {
            if (numEste.isSelected()) {
                campo_numero = "numero_doc as numero";
            } else if (numInte.isSelected()) {
                campo_numero = "numero as numero";
            } else {
                campo_numero = "numero_doc as numero";
                campo_numero1 = "numero_doc as numero";
            }
        }
        String campo_data = "data";
        if (situazioneFornitori) {
            campo_data = "data_doc as data";
        }
        String campo_serie = "serie";
        if (situazioneFornitori) {
            campo_serie = "serie_doc as serie";
        }

//        String sql = "select " + tab + ".pagamento, scadenze.distinta, " + tab + ".serie, " + tab + ".numero, " + tab + ".anno, " + tab + ".data, " + tab + "." + campo_totale + ", scadenze.data_scadenza as 'data scadenza', scadenze.pagata, scadenze.importo";
//        sql += ", clie_forn.ragione_sociale as " + campo_cliente + ", clie_forn.codice, distinte_riba.desc_sint_banca as banca";

        String sql = "select " + tab + ".pagamento, ";
        if (numEntrambi.isSelected()) {
            sql += tab + "." + campo_numero + ", " + tab + "." + campo_numero1 + ", ";
        } else {
            sql += tab + "." + campo_numero + ", ";
        }
        sql += tab + "." + campo_data + ", " + campo_totale + ", scadenze.data_scadenza as 'data scadenza', scadenze.pagata, scadenze.importo, ";
        sql += tab + ".id as ID";
        sql += ", clie_forn.ragione_sociale as " + campo_cliente + " , LEFT(IFNULL(distinte_riba.desc_sint_banca,''),35) as banca, distinte_riba.id as dist";

        sql += " from " + tab + " left join scadenze on " + tab + ".serie = scadenze.documento_serie and " + tab + ".numero = scadenze.documento_numero and " + tab + ".anno = scadenze.documento_anno";
        sql += " left join clie_forn on " + tab + "." + campo_cliente + " = clie_forn.codice";
        sql += " left join pagamenti on " + tab + ".pagamento = pagamenti.codice";
        sql += " left join distinte_riba on scadenze.distinta = distinte_riba.id";
        if (situazioneClienti) {
            sql += " where scadenze.documento_tipo = 'FA'";
            if (!cheVisNoteDiCredito.isSelected()) {
                sql += " and " + tab + ".tipo_fattura != " + dbFattura.TIPO_FATTURA_NOTA_DI_CREDITO;
            }
            if (!cheVisProforma.isSelected()) {
                sql += " and " + tab + ".tipo_fattura != " + dbFattura.TIPO_FATTURA_PROFORMA;
            }

            if (main.pluginEmail && cheVisDaInviare.isSelected()) {
                sql += " and scadenza.mail_inviata = 0";
            }
        } else if (situazioneFornitori) {
            sql += " where scadenze.documento_tipo = 'FR'";
        } else {
            sql += " where scadenze.documento_tipo = 'OR'";
        }
        //prendo cliente
//        if (!this.comClie.getSelectedKey().toString().equalsIgnoreCase("*")) {
        if (!tutti_i_clienti && cliente_selezionato != null) {
            sql += " and " + tab + "." + campo_cliente + " = " + this.cliente_selezionato;
        }
        //lo se vederle tutte o solo da pagare
        if (this.chePaga.isSelected() == true) {
            sql += " and (scadenze.pagata = 'N' or scadenze.pagata = 'P')";
        }
        //108 se lengthmette prendo le date di inizio o fine - riferite alle scadenza
        if (this.texDal.getText().length() > 0) {
            sql += " and scadenze.data_scadenza >= " + Db.pc2(this.texDal.getText(), java.sql.Types.DATE);
        }
        if (this.texAl.getText().length() > 0) {
            sql += " and scadenze.data_scadenza <= " + Db.pc2(this.texAl.getText(), java.sql.Types.DATE);
        }
        //105
        //controllo se vederle tutte o solo RIBA
        if (this.comPagamento.getSelectedIndex() == 0) {
            sql += " and pagamenti.riba = 'S'";
        } else if (this.comPagamento.getSelectedIndex() == 1) {
            sql += " and pagamenti.riba = 'N'";
        } else if (this.comPagamento.getSelectedIndex() == 2 || this.comPagamento.getSelectedIndex() == -1) {
            //non filtro prendo tutto
        } else {
            //javax.swing.JOptionPane.showMessageDialog(this, "Altro tipo di pagamento");
        }
        //controllo se vederle tutte o solo da stampare
        if (this.cheStam.isSelected() == true) {
            sql += " and scadenze.distinta is null";
        }
        if (!this.comBanca.getSelectedKey().toString().equalsIgnoreCase("*")) {
            if (comBanca.getSelectedKey().toString().equalsIgnoreCase("-1")) {
                sql += " and distinte_riba.cc is null";
            } else {
                sql += " and distinte_riba.cc = " + Db.pc(comBanca.getSelectedKey(), Types.VARCHAR);
            }
        }
        sql += " order by " + campoOrdine + ", distinte_riba.desc_sint_banca, " + tab + ".numero, scadenze.data_scadenza";
        //debug
        this.griglia.flagUsaThread = false;
        System.out.println(sql);
        this.griglia.dbOpen(Db.getConn(), sql);
        this.sql = sql;
        //stampa
        int headerWidth[] = {10, 3, 5, 7, 5, 2, 7, 13, 10, 2};
        String msg = "";
        if (situazioneClienti || situazioneOrdini) {
            msg = "Situazione cliente : " + this.texClie.getText();
        } else {
            msg = "Situazione fornitore : " + this.texClie.getText();
        }
        if (comCategoriaClifor.getSelectedIndex() > 0) {
            if (situazioneClienti || situazioneOrdini) {
                msg += "\nCategoria cliente : " + comCategoriaClifor.getSelectedItem();
            } else {
                msg += "\nCategoria fornitore : " + comCategoriaClifor.getSelectedItem();
            }
        }

        msg += "\nPeriodo dal : " + texDal.getText() + "  al : " + texAl.getText();
        Util.start(this.griglia.stampaTabella(msg, headerWidth, this.labTotale.getText()));
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    private void init() {
        //apro la griglia
        this.griglia.dbNomeTabella = "";
        this.griglia.flagUsaThread = false;

        java.util.Hashtable colsAlign = new java.util.Hashtable();
        colsAlign.put("totale", "RIGHT_CURRENCY");
        this.griglia.columnsAlign = colsAlign;
//        this.comClie.dbAddElement("<selezionare un cliente/fornitore>", "-1");
//        this.comClie.dbAddElement("<tutti>", "*");
//        this.comClie.dbOpenList(Db.getConn(), "select ragione_sociale, codice from clie_forn order by ragione_sociale", "*", false);
//        this.comClie.dbTrovaMentreScrive = true;
        //105
        comPagamento.addItem("Solo RIBA");
        comPagamento.addItem("Solo BONIFICI o RIMESSA DIRETTA");
        comPagamento.addItem("Tutti i tipi di pagamento");

        comBanca.dbAddElement("<tutte le banche>", "*");
        comBanca.dbOpenList(Db.getConn(), "select CONCAT('CC ', cc, ' - ', desc_sint_banca), cc from distinte_riba group by cc, desc_sint_banca", "*", false);
        comBanca.dbTrovaMentreScrive = true;

        comCategoriaClifor.dbAddElement("<tutte le categorie>", "*");
        comCategoriaClifor.dbOpenList(Db.getConn(), "select descrizione,id from tipi_clie_forn", "*", false);
        comCategoriaClifor.dbTrovaMentreScrive = true;

        caricaImpo();

        opening = false;

        selezionaSituazione();

//        AutoCompletion.enable(comClie);

    }

    public void selezionaSituazione() {
        selezionaSituazione(false);
    }

    public void selezionaSituazione(boolean seltutte) {
        //Aggiorna
        if (opening) {
            return;
        }
        if (radClienti.isSelected()) {
            numInte.setEnabled(false);
            numEste.setEnabled(false);
            numEntrambi.setEnabled(false);
            nonFatturati.setEnabled(false);
            situazioneClienti = true;
            situazioneFornitori = false;
            situazioneOrdini = false;
            butPrin1.setEnabled(true);
            comBanca.setEnabled(true);
            labCliente.setText("Cliente");
            labCategoriaCliente.setText("Categoria Cliente");

            java.util.Hashtable colsWidthPerc = new java.util.Hashtable();


            colsWidthPerc.put("pagamento", new Double(12));
            colsWidthPerc.put("riba", new Double(0));
            colsWidthPerc.put("abi", new Double(0));
            colsWidthPerc.put("cab", new Double(0));
            colsWidthPerc.put("id", new Double(0));
            colsWidthPerc.put("distinta", new Double(6));
            colsWidthPerc.put("serie", new Double(5));
            colsWidthPerc.put("numero", new Double(6));
            colsWidthPerc.put("anno", new Double(0));
            colsWidthPerc.put("data", new Double(9));
            colsWidthPerc.put("totale", new Double(9));
            colsWidthPerc.put("data scadenza", new Double(9));
            colsWidthPerc.put("pagata", new Double(5));
            colsWidthPerc.put("importo", new Double(10));
            colsWidthPerc.put("cliente", new Double(13));
            colsWidthPerc.put("codice", new Double(0));
            colsWidthPerc.put("banca", new Double(19));
            colsWidthPerc.put("dserie", new Double(0));
            colsWidthPerc.put("dnumero", new Double(0));
            colsWidthPerc.put("danno", new Double(0));
            colsWidthPerc.put("importo_pagato", new Double(0));
            colsWidthPerc.put("riba", new Double(0));
            colsWidthPerc.put("abi", new Double(0));
            colsWidthPerc.put("cab", new Double(0));
            colsWidthPerc.put("documento_tipo", new Double(0));
            colsWidthPerc.put("note_pagamento", new Double(12));
            colsWidthPerc.put("Riferimento Cliente/Fornitore", new Double(8));
            if (main.pluginEmail) {
                colsWidthPerc.put("Email Inviata", new Double(3));
            }
            griglia.columnsSizePerc = colsWidthPerc;

            try {
                InvoicexEvent event = new InvoicexEvent(this);
                event.type = InvoicexEvent.TYPE_SITUAZIONECLIENTI_ACTIVATE_BTNRIBA;
                main.events.fireInvoicexEvent(event);
            } catch (Exception err) {
                err.printStackTrace();
            }
        } else if (radFornitori.isSelected()) {
            numInte.setEnabled(true);
            numEste.setEnabled(true);
            numEntrambi.setEnabled(true);
            nonFatturati.setEnabled(false);
            situazioneClienti = false;
            situazioneFornitori = true;
            situazioneOrdini = false;
            butPrin1.setEnabled(false);
            comBanca.setEnabled(false);
            labCliente.setText("Fornitore");
            labCategoriaCliente.setText("Categoria Fornitore");
            java.util.Hashtable colsWidthPerc = new java.util.Hashtable();
            if (this.numEntrambi.isSelected()) {
                colsWidthPerc.put("pagamento", new Double(10));
                colsWidthPerc.put("id", new Double(0));
                colsWidthPerc.put("distinta", new Double(6));
                colsWidthPerc.put("serie", new Double(5));
                colsWidthPerc.put("interno", new Double(8));
                colsWidthPerc.put("numero", new Double(8));
                colsWidthPerc.put("anno", new Double(0));
                colsWidthPerc.put("data", new Double(8));
                colsWidthPerc.put("totale", new Double(8));
                colsWidthPerc.put("data scadenza", new Double(8));
                colsWidthPerc.put("pagata", new Double(5));
                colsWidthPerc.put("importo", new Double(9));
                colsWidthPerc.put("cliente", new Double(12));
                colsWidthPerc.put("codice", new Double(0));
                colsWidthPerc.put("banca", new Double(16));
                colsWidthPerc.put("serie2", new Double(0));
                colsWidthPerc.put("numero2", new Double(0));
                colsWidthPerc.put("anno2", new Double(0));
                colsWidthPerc.put("dserie", new Double(0));
                colsWidthPerc.put("dnumero", new Double(0));
                colsWidthPerc.put("danno", new Double(0));
                colsWidthPerc.put("importo_pagato", new Double(0));
                colsWidthPerc.put("documento_tipo", new Double(0));
                colsWidthPerc.put("note_pagamento", new Double(15));
                colsWidthPerc.put("Riferimento Cliente/Fornitore", new Double(10));
            } else if (this.numEste.isSelected()) {
                colsWidthPerc.put("pagamento", new Double(12));
                colsWidthPerc.put("id", new Double(0));
                colsWidthPerc.put("distinta", new Double(6));
                colsWidthPerc.put("serie", new Double(5));
                colsWidthPerc.put("numero", new Double(8));
                colsWidthPerc.put("anno", new Double(0));
                colsWidthPerc.put("data", new Double(9));
                colsWidthPerc.put("totale", new Double(9));
                colsWidthPerc.put("data scadenza", new Double(9));
                colsWidthPerc.put("pagata", new Double(5));
                colsWidthPerc.put("importo", new Double(10));
                colsWidthPerc.put("cliente", new Double(13));
                colsWidthPerc.put("codice", new Double(0));
                colsWidthPerc.put("banca", new Double(18));
                colsWidthPerc.put("serie2", new Double(0));
                colsWidthPerc.put("numero2", new Double(0));
                colsWidthPerc.put("anno2", new Double(0));
                colsWidthPerc.put("dserie", new Double(0));
                colsWidthPerc.put("dnumero", new Double(0));
                colsWidthPerc.put("danno", new Double(0));
                colsWidthPerc.put("importo_pagato", new Double(0));
                colsWidthPerc.put("documento_tipo", new Double(0));
                colsWidthPerc.put("note_pagamento", new Double(15));
                colsWidthPerc.put("Riferimento Cliente/Fornitore", new Double(10));
            } else {
                colsWidthPerc.put("pagamento", new Double(12));
                colsWidthPerc.put("id", new Double(0));
                colsWidthPerc.put("distinta", new Double(6));
                colsWidthPerc.put("serie", new Double(5));
                colsWidthPerc.put("numero", new Double(8));
                colsWidthPerc.put("anno", new Double(0));
                colsWidthPerc.put("data", new Double(8));
                colsWidthPerc.put("totale", new Double(9));
                colsWidthPerc.put("data scadenza", new Double(9));
                colsWidthPerc.put("pagata", new Double(5));
                colsWidthPerc.put("importo", new Double(10));
                colsWidthPerc.put("cliente", new Double(12));
                colsWidthPerc.put("codice", new Double(0));
                colsWidthPerc.put("banca", new Double(16));
                colsWidthPerc.put("serie2", new Double(0));
                colsWidthPerc.put("numero2", new Double(0));
                colsWidthPerc.put("anno2", new Double(0));
                colsWidthPerc.put("dserie", new Double(0));
                colsWidthPerc.put("dnumero", new Double(0));
                colsWidthPerc.put("danno", new Double(0));
                colsWidthPerc.put("importo_pagato", new Double(0));
                colsWidthPerc.put("documento_tipo", new Double(0));
                colsWidthPerc.put("note_pagamento", new Double(15));
                colsWidthPerc.put("Riferimento Cliente/Fornitore", new Double(10));
            }
            griglia.columnsSizePerc = colsWidthPerc;
            try {
                InvoicexEvent event = new InvoicexEvent(this);
                event.type = InvoicexEvent.TYPE_SITUAZIONECLIENTI_DEACTIVATE_BTNRIBA;
                main.events.fireInvoicexEvent(event);
            } catch (Exception err) {
                err.printStackTrace();
            }
        } else {
            numInte.setEnabled(false);
            numEste.setEnabled(false);
            numEntrambi.setEnabled(false);
            nonFatturati.setEnabled(true);
            situazioneClienti = false;
            situazioneFornitori = false;
            situazioneOrdini = true;
            butPrin1.setEnabled(true);
            comBanca.setEnabled(true);
            labCliente.setText("Cliente");
            labCategoriaCliente.setText("Categoria Cliente");
            fatturati = nonFatturati.isSelected();

            java.util.Hashtable colsWidthPerc = new java.util.Hashtable();
            colsWidthPerc.put("pagamento", new Double(12));
            colsWidthPerc.put("id", new Double(0));
            colsWidthPerc.put("distinta", new Double(6));
            colsWidthPerc.put("serie", new Double(5));
            colsWidthPerc.put("numero", new Double(6));
            colsWidthPerc.put("anno", new Double(0));
            colsWidthPerc.put("data", new Double(9));
            colsWidthPerc.put("totale", new Double(9));
            colsWidthPerc.put("data scadenza", new Double(9));
            colsWidthPerc.put("pagata", new Double(5));
            colsWidthPerc.put("importo", new Double(10));
            colsWidthPerc.put("cliente", new Double(13));
            colsWidthPerc.put("codice", new Double(0));
            colsWidthPerc.put("banca", new Double(19));
            colsWidthPerc.put("dserie", new Double(0));
            colsWidthPerc.put("dnumero", new Double(0));
            colsWidthPerc.put("danno", new Double(0));
            colsWidthPerc.put("importo_pagato", new Double(0));
            colsWidthPerc.put("documento_tipo", new Double(0));
            colsWidthPerc.put("note_pagamento", new Double(15));
            colsWidthPerc.put("Riferimento Cliente/Fornitore", new Double(10));
            griglia.columnsSizePerc = colsWidthPerc;

            try {
                InvoicexEvent event = new InvoicexEvent(this);
                event.type = InvoicexEvent.TYPE_SITUAZIONECLIENTI_ACTIVATE_BTNRIBA;
                main.events.fireInvoicexEvent(event);
            } catch (Exception err) {
                err.printStackTrace();
            }
        }

        //select test_fatt.serie, test_fatt.numero, test_fatt.anno, test_fatt.data, test_fatt.totale, scadenze.data_scadenza, scadenze.pagata, scadenze.importo from test_fatt left join scadenze on test_fatt.serie = scadenze.documento_serie and test_fatt.numero = scadenze.documento_numero and test_fatt.anno = scadenze.documento_anno
        //format per data in italiano
        //sql += " DATE_FORMAT(test_ddt.data,'%d/%m/%y') AS Data ,";
        String tab = "test_fatt";
        if (situazioneFornitori) {
            tab = "test_fatt_acquisto";
        }
        if (situazioneOrdini) {
            tab = "test_ordi";
        }

//        String campo_totale = "totale";
//        if (situazioneFornitori) {
//            campo_totale = "importo as totale";
//        }

        String campo_totale = tab + ".totale";
        if (tab.equals("test_fatt")) {
            campo_totale = "if(IFNULL(" + tab + ".totale_da_pagare,0)>0, " + tab + ".totale_da_pagare, " + tab + ".totale)";
        } else if (situazioneFornitori) {
            campo_totale = "if(IFNULL(" + tab + ".totale_da_pagare,0)>0, " + tab + ".totale_da_pagare, " + tab + ".importo) as totale";
        }

        String campo_cliente = "cliente";
        if (situazioneFornitori) {
            campo_cliente = "fornitore";
        }
        String campo_numero = "numero";
        String campo_numero1 = "";
        if (situazioneFornitori) {
            campo_numero = "numero_doc as numero";
            campo_numero1 = "numero as interno";
        }
        String campo_data = "data";
        if (situazioneFornitori) {
            campo_data = "data_doc as data";
        }
        String campo_serie = "serie";
        if (situazioneFornitori) {
            campo_serie = "serie_doc as serie";
        }
        String agg = "";
        if (situazioneFornitori) {
            agg = ", " + tab + ".serie as serie2, " + tab + ".numero as numero2, " + tab + ".anno as anno2";
        }

        String sql = "select scadenze.id, " + tab + ".pagamento, scadenze.distinta, " + tab + "." + campo_serie + ", ";
        if (situazioneFornitori) {
            if (this.numInte.isSelected()) {
                sql += tab + "." + campo_numero1 + ", ";
            } else if (this.numEntrambi.isSelected()) {
                sql += tab + "." + campo_numero + ", " + tab + "." + campo_numero1 + ", ";
            } else {
                sql += tab + "." + campo_numero + ", ";
            }
        } else {
            sql += tab + "." + campo_numero + ", ";
        }
        sql += tab + ".anno, " + tab + "." + campo_data + ",";
        if (situazioneClienti) {
            sql += campo_totale + " * (IF(" + tab + ".tipo_fattura = 3,-1,1)) as totale";
        } else {
            sql += campo_totale;
        }
        sql += ", scadenze.data_scadenza as 'data scadenza', scadenze.pagata";
        if (situazioneClienti) {
            sql += ", scadenze.importo * (IF(" + tab + ".tipo_fattura = 3,-1,1)) as importo";
        } else {
            sql += ", scadenze.importo";
        }
        sql += ", clie_forn.ragione_sociale as " + campo_cliente + ", clie_forn.codice, distinte_riba.desc_sint_banca as banca" + agg;
        sql += ", " + tab + ".serie as dserie, " + tab + ".numero as dnumero, " + tab + ".anno as danno";
        sql += ", sum(scadenze_parziali.importo) as importo_pagato";
        if (situazioneClienti) {
            sql += ", pagamenti.riba, " + tab + ".banca_abi as abi, " + tab + ".banca_cab as cab";
        }
        sql += ", scadenze.documento_tipo, scadenze.note_pagamento";

        if (main.fileIni.getValueBoolean("pref", "ColAgg_RiferimentoCliente", false)) {
            sql += " , clie_forn.persona_riferimento as 'Riferimento Cliente/Fornitore'";
        }

        if (main.pluginEmail) {
            sql += ", scadenze.mail_inviata as 'Email Inviata'";
        }

        sql += " from " + tab + " left join scadenze on " + tab + ".serie = scadenze.documento_serie and " + tab + ".numero = scadenze.documento_numero and " + tab + ".anno = scadenze.documento_anno";
        sql += " left join clie_forn on " + tab + "." + campo_cliente + " = clie_forn.codice";
        sql += " left join pagamenti on " + tab + ".pagamento = pagamenti.codice";
        sql += " left join distinte_riba on scadenze.distinta = distinte_riba.id";
        sql += " left join scadenze_parziali on scadenze.id = scadenze_parziali.id_scadenza";
        if (situazioneClienti) {
            sql += " where scadenze.documento_tipo = 'FA'";
            sql += " and " + tab + ".tipo_fattura != " + dbFattura.TIPO_FATTURA_SCONTRINO;
            if (!cheVisNoteDiCredito.isSelected()) {
                sql += " and " + tab + ".tipo_fattura != " + dbFattura.TIPO_FATTURA_NOTA_DI_CREDITO;
            }
            if (!cheVisProforma.isSelected()) {
                sql += " and " + tab + ".tipo_fattura != " + dbFattura.TIPO_FATTURA_PROFORMA;
            }
            if (main.pluginEmail && cheVisDaInviare.isSelected()) {
                sql += " and scadenze.mail_inviata = 0";
            }
            if (main.fileIni.getValueBoolean("pref", "ColAgg_RiferimentoCliente", false)) {
                sql += " and clie_forn.persona_riferimento like '%" + Db.aa(riferimento.getText()) + "%'";
            }
        } else if (situazioneFornitori) {
            sql += " where scadenze.documento_tipo = 'FR'";
            if (main.fileIni.getValueBoolean("pref", "ColAgg_RiferimentoCliente", false)) {
                sql += " and clie_forn.persona_riferimento like '%" + Db.aa(riferimento.getText()) + "%'";
            }
        } else {
            sql += " where scadenze.documento_tipo = 'OR'";
            if (main.fileIni.getValueBoolean("pref", "ColAgg_RiferimentoCliente", false)) {
                sql += " and clie_forn.persona_riferimento like '%" + Db.aa(riferimento.getText()) + "%'";
            }
        }

        if (radOrdini.isSelected()) {
            if (fatturati) {
                sql += " and isNull(test_ordi.doc_tipo)";
            }
        }
        //prendo cliente
//        if (!this.comClie.getSelectedKey().toString().equalsIgnoreCase("*")) {
        if (!tutti_i_clienti && cliente_selezionato != null) {
            sql += " and " + tab + "." + campo_cliente + " = " + cliente_selezionato;
        }

        if (comCategoriaClifor.getSelectedIndex() > 0) {
            sql += " and clie_forn.tipo2 = " + Db.pc(comCategoriaClifor.getSelectedKey(), Types.VARCHAR);
        }

        //lo se vederle tutte o solo da pagare
        if (this.chePaga.isSelected() == true) {
            sql += " and (scadenze.pagata = 'N' or scadenze.pagata = 'P')";
        }
        //108 se lengthmette prendo le date di inizio o fine - riferite alle scadenza
        if (this.texDal.getText().length() > 0) {
            sql += " and scadenze.data_scadenza >= " + Db.pc2(this.texDal.getText(), java.sql.Types.DATE);
        }
        if (this.texAl.getText().length() > 0) {
            sql += " and scadenze.data_scadenza <= " + Db.pc2(this.texAl.getText(), java.sql.Types.DATE);
        }
        //105
        //controllo se vederle tutte o solo RIBA
        //if (this.cheRiba.isSelected() == true) {
        //  sql += " and pagamenti.riba = 'S'";
        //}
        //---
        if (this.comPagamento.getSelectedIndex() == 0) {
            sql += " and pagamenti.riba = 'S'";
        } else if (this.comPagamento.getSelectedIndex() == 1) {
            sql += " and pagamenti.riba = 'N'";
        } else if (this.comPagamento.getSelectedIndex() == 2 || this.comPagamento.getSelectedIndex() == -1) {
            //non filtro prendo tutto
        } else {
            //javax.swing.JOptionPane.showMessageDialog(this, "Altro tipo di pagamento");
        }
        //***
        //controllo se vederle tutte o solo da stampare
        if (this.cheStam.isSelected() == true) {
            sql += " and scadenze.distinta is null";
        }
        if (!this.comBanca.getSelectedKey().toString().equalsIgnoreCase("*")) {
            if (comBanca.getSelectedKey().toString().equalsIgnoreCase("-1")) {
                sql += " and distinte_riba.cc is null";
            } else {
                sql += " and distinte_riba.cc = " + Db.pc(comBanca.getSelectedKey(), Types.VARCHAR);
            }
        }
        //test
        /*sql = "select scadenze.id,test_fatt.serie, test_fatt.numero, test_fatt.anno, test_fatt.data, test_fatt.totale, scadenze.data_scadenza as 'data scadenza', scadenze.pagata, scadenze.importo";
         sql += ", clie_forn.ragione_sociale as cliente, clie_forn.codice";
         sql += " from (test_fatt left join scadenze on test_fatt.serie = scadenze.documento_serie and test_fatt.numero = scadenze.documento_numero and test_fatt.anno = scadenze.documento_anno)";
         sql += " left join clie_forn on test_fatt.cliente = clie_forn.codice";
         sql += " where scadenze.documento_tipo = 'FA'";
         sql += " order by test_fatt.cliente, test_fatt.numero, scadenze.data_scadenza";
         */
        //***
        //debug

        SwingUtils.mouse_wait(this);

        sql += " group by scadenze.id";
        if (!orderClause.equals("")) {
            sql += " order by " + orderClause;
        }


        System.out.println("sql situazione clifor: " + sql);
        this.griglia.flagUsaThread = false;
        MicroBench mb = new MicroBench();
        mb.start();
        this.griglia.dbOpen(Db.getConn(), sql);

        if (main.pluginEmail) {
            try {
                griglia.getColumn("Email Inviata").setCellRenderer(new frmElenFatt.EmailCellRenderer());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println(mb.getDiff("selezione situazione"));
        this.sql = sql;
        //setto header con Jlabel
        /*JLabelHeaderRenderer renderer = new JLabelHeaderRenderer();
         TableColumnModel model = this.griglia.getColumnModel();
         int n = model.getColumnCount();
         for (int i=0;i<n;i++) {
         model.getColumn(i).setHeaderRenderer(renderer);
         }*/
        //deseleziono tutte lengthscadenze
        if (seltutte) {
            butSeleTuttActionPerformed(null);
        } else {
            butDeseTuttActionPerformed(null);
        }

        //aggiorno il totale
        double totale = 0;
        double totalePagate = 0;
        double totaleDaPagare = 0;
        for (int i = 0; i < this.griglia.getRowCount(); i++) {
            Double d = (Double) this.griglia.getValueAt(i, griglia.getColumnByName("importo"));
            Double dpagato = (Double) this.griglia.getValueAt(i, griglia.getColumnByName("importo_pagato"));
            if (String.valueOf(this.griglia.getValueAt(i, griglia.getColumnByName("pagata"))).equals("S")) {
                totalePagate += d;
            } else if (String.valueOf(this.griglia.getValueAt(i, griglia.getColumnByName("pagata"))).equals("P")) {
                totalePagate += dpagato;
                totaleDaPagare += (d - dpagato);
            } else {
                totaleDaPagare += d.doubleValue();
            }
            totale += d.doubleValue();
            //System.out.println("sommo [" + i + "]=" + d.doubleValue());
        }

        this.labTotale.setText("Totale " + it.tnx.Util.EURO + " " + it.tnx.Util.formatValutaEuro(totale)
                + " / Gia' Pagate " + it.tnx.Util.EURO + " " + it.tnx.Util.formatValutaEuro(totalePagate)
                + " / Da Pagare " + it.tnx.Util.EURO + " " + it.tnx.Util.formatValutaEuro(totaleDaPagare));

        SwingUtils.mouse_def(this);
    }

    //stampa distinta RB per banca
    public void stampaDistinta() {
        String sql;
        String sqlSele;
        int id;
        /* faccio dialo gper cc
         CoordinateBancarie coord = new CoordinateBancarie();
         coord.setAbi("06160");
         coord.setCab("71940");
         coord.setCc("12345");
         */
        if (this.griglia.getSelectedRowCount() == 0) {
            javax.swing.JOptionPane.showMessageDialog(this, "Nessuna scadenza selezionata");
            return;
        }
        java.awt.Frame parent = (java.awt.Frame) this.getTopLevelAncestor();
        diaDistRiba dialog = new diaDistRiba(parent, true);
        dialog.setBounds(100, 100, 700, 400);
        dialog.show();
        String data_stampa = dialog.data_distinta;
        //chiedo conferma
        if (dialog.prova == false) {
            int ret = javax.swing.JOptionPane.showConfirmDialog(this, "Sicuro di stampare in definitivo?", "Attenzione", javax.swing.JOptionPane.YES_NO_OPTION);
            if (ret == javax.swing.JOptionPane.NO_OPTION) {
                return;
            }
        }
        //
        if (dialog.cancel == false) {
            CoordinateBancarie coord = new CoordinateBancarie();
            coord = dialog.coord;
            //invece che prendere tutte le scadenze prendo quelle che seleziona a mano
            //---
            sql = "delete from scadenze_sel where username = " + Db.pc(main.login, "VARCHAR");
            Db.executeSql(sql);
            int[] righe = this.griglia.getSelectedRows();
            for (int i = 0; i < righe.length; i++) {
                if (righe[i] < this.griglia.getRowCount()) {
                    sql = "insert into scadenze_sel (id, username) values ";
                    sql += "(" + this.griglia.getValueAt(righe[i], 0);
                    sql += " ," + Db.pc(main.login, "VARCHAR");
                    sql += ")";
                    if (this.griglia.getValueAt(righe[i], 0) != null) {
                        Db.executeSql(sql);
                    }
                }
            }
            //prepasro sql per print
            sqlSele = "select scadenze_sel.id, ";
            sqlSele += " scadenze_sel.username, "
                    + "test_fatt.serie, "
                    + "test_fatt.numero, "
                    + "test_fatt.anno, "
                    + "test_fatt.data, "
                    + "test_fatt.totale, "
                    + "test_fatt.banca_abi as test_fatt_banca_abi, "
                    + "test_fatt.banca_cab as test_fatt_banca_cab, "
                    + "test_fatt.opzione_riba_dest_diversa, "
                    + "scadenze.data_scadenza, "
                    + "scadenze.pagata, "
                    + "scadenze.importo, "
                    + "scadenze.numero";
            sqlSele += ", dest_ragione_sociale, dest_indirizzo, dest_cap, dest_localita, dest_provincia, dest_telefono, dest_cellulare";

            sqlSele += ", clie_forn.codice as clie_forn_codice";
            sqlSele += ", clie_forn.ragione_sociale as clie_forn_ragione_sociale";
            sqlSele += ", clie_forn.indirizzo as clie_forn_indirizzo";
            sqlSele += ", clie_forn.cap as clie_forn_cap";
            sqlSele += ", clie_forn.localita as clie_forn_localita";
            sqlSele += ", clie_forn.provincia as clie_forn_provincia";
            sqlSele += ", clie_forn.piva_cfiscale as clie_forn_piva_cfiscale";
            sqlSele += ", banche_abi.nome as banche_abi_nome";
            sqlSele += ", banche_cab.indirizzo as banche_cab_indirizzo";
            sqlSele += ", comuni.comune as comuni_comune";
            sqlSele += ", test_fatt.totale_da_pagare";

            sqlSele += " from scadenze_sel inner join scadenze on scadenze_sel.id = scadenze.id";
            sqlSele += " left join test_fatt on test_fatt.serie = scadenze.documento_serie and test_fatt.numero = scadenze.documento_numero and test_fatt.anno = scadenze.documento_anno and test_fatt.tipo_fattura != " + dbFattura.TIPO_FATTURA_SCONTRINO;
            sqlSele += " left join clie_forn on test_fatt.cliente = clie_forn.codice";
            sqlSele += " left join banche_abi on test_fatt.banca_abi = banche_abi.abi";
            sqlSele += " left join banche_cab on test_fatt.banca_abi = banche_cab.abi and test_fatt.banca_cab = banche_cab.cab";
            sqlSele += " left join comuni on banche_cab.codice_comune = comuni.codice";
            sqlSele += " where scadenze_sel.username = " + Db.pc(main.login, "VARCHAR");
            //sqlSele += " order by test_fatt.cliente, test_fatt.numero, scadenze.data_scadenza";
            sqlSele += " order by clie_forn.ragione_sociale, test_fatt.numero, scadenze.data_scadenza";
            //104 aggiungo controllo totali fattura con totali scadenze
            //da finire
            //if (Scadenze.controllaTotali(sqlSele, this) == false) {
            //  return;
            //}
            //***
            if (dialog.prova == true) {
                prnDistRb print = new prnDistRb(sqlSele, coord, true, 0, Db.getCurrDateTimeMysqlIta());
            } else {
                //inserisco il record della distinta
                try {
                    String strDataDistinta = data_stampa;
                    //sql = "insert into distinte_riba (data) values ('" + strDataDistinta + "')";
                    sql = "insert into distinte_riba (";
                    sql += " data";
                    sql += " , abi";
                    sql += " , cab";
                    sql += " , cc";
                    sql += " , desc_sint_banca";
                    sql += " ) values ( ";
                    sql += "'" + strDataDistinta + "'";
                    sql += ", '" + coord.getAbi() + "'";
                    sql += ", '" + coord.getCab() + "'";
                    sql += ", '" + coord.getCc() + "'";
                    sql += ", " + Db.pc(coord.findSmallDescription(), Types.VARCHAR);
                    sql += ")";
                    Db.executeSql(sql);
                    //prendo ultimo id distinte_riba
                    sql = "select id from distinte_riba order by id desc limit 0,1";
                    ResultSet temp = Db.openResultSet(sql);
                    temp.next();
                    id = temp.getInt("id");
                    //riformatto la data
                    String nuova_data = Db.getCurrDateTimeMysqlIta();
                    try {
                        DateFormat parsaData = new SimpleDateFormat("yyyy-MM-dd");
                        Date data_inserita = parsaData.parse(strDataDistinta);
                        java.text.SimpleDateFormat riformattazione = new java.text.SimpleDateFormat("dd/MM/yy");
                        nuova_data = riformattazione.format(data_inserita);
                    } catch (Exception err) {
                        err.printStackTrace();
                    }
                    //lancio creazione pdf, dovrei controllare anche che nega creato...
                    prnDistRb print = new prnDistRb(sqlSele, coord, false, id, nuova_data);
                    //controllo esito stampa
                    if (print.rispostaConferma == print.RISPOSTA_CONTINUA) {
                        //marca lengthscadenze come gi??? stampate
                        //metto id distinta nelle scadenze ciclando id per id
                        ResultSet tempSel = Db.openResultSet("select * from scadenze_sel where scadenze_sel.username = " + Db.pc(main.login, "VARCHAR"));
                        while (tempSel.next()) {
                            sql = "update scadenze";
                            sql += " set distinta = " + id;
                            sql += " where documento_tipo = 'FA'";
                            sql += " and id = " + tempSel.getString("id");
                            Db.executeSql(sql);
                        }
                    }
                } catch (Exception err) {
                    err.printStackTrace();
                    javax.swing.JOptionPane.showMessageDialog(this, "Errore nella marcatura delle scadenze\n" + err.toString());
                }
            }
            dialog.dispose();
        }
    }

    /**
     * Main method for panel
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                File fwd = new File("./");
                try {
                    main.wd = fwd.getCanonicalPath() + File.separator;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                main.paramProp = "param_prop_test_prima_nota.txt";
                main.fileIni = new iniFileProp();
                main.fileIni.realFileName = main.wd + main.paramProp;
                main.loadIni();
                main.utente = new Utente(1);

                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                }
                

                JFrame main = new JFrame();
                JDesktopPane desk = new JDesktopPane();
                main.getContentPane().add(desk);


                JFrame frame = new JFrame();
                frame.setSize(600, 400);
                frame.setLocation(100, 100);
                frame.getContentPane().add(new SituazioneClienti());
                frame.setVisible(true);
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent evt) {
                        System.exit(0);
                    }
                });
            }
        });
    }

    public JToolBar build_toolBar() {
        tooMenu = new JToolBar();
        tooMenu.setRollover(true);
        InvoicexUtil.macButtonRegular(butPrin);
        tooMenu.add(butPrin);
        InvoicexUtil.macButtonRegular(butPrin1);
        tooMenu.add(butPrin1);
        tooMenu.add(new JSeparator(JSeparator.VERTICAL));
        JPanel panelRadio = new JPanel(new GridLayout(3, 1));
        panelRadio.add(radClienti);
        if (main.fileIni.getValueBoolean("pref", "scadenzeOrdini", false)) {
            panelRadio.add(radOrdini);
        }
        panelRadio.add(radFornitori);
        tooMenu.add(panelRadio);

        tooMenu.add(new JSeparator(JSeparator.VERTICAL));
        JPanel panelRadio1 = new JPanel(new GridLayout(3, 1));
        panelRadio1.add(numInte);
        panelRadio1.add(numEste);
        panelRadio1.add(numEntrambi);
        tooMenu.add(panelRadio1);

        return tooMenu;
    }

    public JPanel build_pref() {
        JPanel pan = new JPanel();
        FormLayout layout = new FormLayout(
                "2dlu, right:pref, 2dlu, 80dlu, 2dlu, right:pref, 2dlu, 60dlu, 2dlu, right:100dlu, 2dlu, right:100dlu, 2dlu",
                "2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu, pref, 2dlu");

        CellConstraints cc = new CellConstraints();

//        pan.setBackground(Color.RED);
        pan.setLayout(layout);

        pan.add(labCliente, cc.xy(2, 2));
        pan.add(texClie, cc.xyw(4, 2, 5));

        pan.add(labCategoriaCliente, cc.xy(2, 4));
        pan.add(comCategoriaClifor, cc.xyw(4, 4, 5));

        pan.add(new JLabel("Pagamento"), cc.xy(2, 6));
        pan.add(comPagamento, cc.xyw(4, 6, 5));

        pan.add(new JLabel("Dal"), cc.xy(2, 8));
        pan.add(texDal, cc.xy(4, 8));
        pan.add(new JLabel("Al"), cc.xy(6, 8));
        pan.add(texAl, cc.xy(8, 8));

        pan.add(new JLabel("Banca"), cc.xy(2, 10));
        pan.add(comBanca, cc.xyw(4, 10, 7));

        //---
        cheStam.setText("Solo da Stampare");
        cheStam.setHorizontalTextPosition(JCheckBox.LEFT);
        chePaga.setText("Solo da Pagare");
        chePaga.setHorizontalTextPosition(JCheckBox.LEFT);
        nonFatturati.setText("Solo non Fatturati");
        nonFatturati.setHorizontalTextPosition(JCheckBox.LEFT);
        pan.add(cheStam, cc.xy(10, 2));
        pan.add(chePaga, cc.xy(10, 4));
        pan.add(nonFatturati, cc.xy(10, 6));

        cheVisNoteDiCredito.setText("Vis. da Note di Credito");
        cheVisNoteDiCredito.setHorizontalTextPosition(JCheckBox.LEFT);
        pan.add(cheVisNoteDiCredito, cc.xy(12, 2));

        cheVisProforma.setText("Vis. da Proforma");
        cheVisProforma.setHorizontalTextPosition(JCheckBox.LEFT);
        pan.add(cheVisProforma, cc.xy(12, 4));

        if (main.pluginEmail) {
            cheVisDaInviare.setText("Email non inviata");
            cheVisDaInviare.setHorizontalTextPosition(JCheckBox.LEFT);
            pan.add(cheVisDaInviare, cc.xy(12, 6));
        }
        return pan;
    }

    public JPanel build_pref_extra() {
        JPanel pan = new JPanel();
        FormLayout layout = new FormLayout(
                "2dlu, right:pref, 2dlu, 80dlu, 2dlu",
                "2dlu, pref, 2dlu");

        CellConstraints cc = new CellConstraints();

        pan.setLayout(layout);

        if (main.fileIni.getValueBoolean("pref", "ColAgg_RiferimentoCliente", false)) {
            pan.add(new JLabel("Filtra per Riferimento Cliente/Fornitore"), cc.xy(2, 2));
            pan.add(riferimento, cc.xy(4, 2));
        }

        return pan;
    }

    public JPanel build_result() {
        JPanel pan = new JPanel();
        FormLayout layout = new FormLayout(
                "4dlu,pref,pref,pref,pref,pref:grow(1.0),pref,2dlu,pref,2dlu,pref,4dlu",
                "2dlu, pref, 2dlu, fill:100dlu:grow(1.0), 2dlu, pref, 2dlu, pref");

        CellConstraints cc = new CellConstraints();

        pan.setLayout(layout);

        //--
        Vector v = new Vector();

        KeyValuePair kv1 = new KeyValuePair("", "<Non Ordinare>");
        KeyValuePair kv2 = new KeyValuePair("clie_forn.ragione_sociale, scadenze.data_scadenza", "Cliente, Data Scadenza");
        KeyValuePair kv3 = new KeyValuePair("clie_forn.ragione_sociale, data", "Cliente, Data Documento");
        KeyValuePair kv4 = new KeyValuePair("scadenze.data_scadenza", "Data Scadenza");
        KeyValuePair kv5 = new KeyValuePair("data", "Data Documento");
        v.add(kv1);
        v.add(kv2);
        v.add(kv3);
        v.add(kv4);
        v.add(kv5);

        SwingUtils.initJComboFromKVList(ordine, v);
        ordine.setSelectedIndex(1);
        grpOrdine.add(radCresc);
        grpOrdine.add(radDescr);
        radCresc.setSelected(true);
        butSeleTutt.setText("Seleziona tutte");
        butDeseTutt.setText("Deseleziona tutte");
        JLabel lbl1 = new JLabel("Ordina per");
        pan.add(lbl1, cc.xy(2, 2));
        pan.add(ordine, cc.xy(3, 2));
        pan.add(radCresc, cc.xy(4, 2));
        pan.add(radDescr, cc.xy(5, 2));

        pan.add(butSeleTutt, cc.xy(7, 2));
        pan.add(butDeseTutt, cc.xy(9, 2));
        pan.add(butRefresh, cc.xy(11, 2));

        //--
        JScrollPane scroll = new JScrollPane();
        scroll.setViewportView(griglia);
        pan.add(scroll, cc.xyw(2, 4, 10));

        //--
        labTotale.setHorizontalAlignment(JLabel.RIGHT);
        //labTotale.setHorizontalTextPosition(JLabel.RIGHT);
        pan.add(labTotale, cc.xyw(2, 6, 10));

        return pan;
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("*** finalizza " + this);
    }
    DelayedExecutor delay_rif = new DelayedExecutor(new Runnable() {
        public void run() {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    selezionaSituazione();
                }
            });
        }
    }, 250);
}
