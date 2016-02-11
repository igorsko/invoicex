/**
 * Invoicex Copyright (c) 2005,2006,2007 Marco Ceccarelli, Tnx snc
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
package gestioneFatture;

import gestioneFatture.logic.clienti.Cliente;
import it.tnx.Db;
import it.tnx.SwingWorker;
import it.tnx.accessoUtenti.Permesso;

import it.tnx.commons.CastUtils;
import it.tnx.commons.DateUtils;
import it.tnx.commons.DbUtils;
import it.tnx.commons.FileUtils;
import it.tnx.commons.SwingUtils;
import it.tnx.commons.cu;
import it.tnx.commons.swing.DelayedExecutor;
import it.tnx.commons.swing.MyFlowLayout;
import it.tnx.invoicex.InvoicexUtil;
import it.tnx.invoicex.ItextUtil;
import it.tnx.invoicex.Magazzino;
import it.tnx.invoicex.gui.JDialogExc;
import it.tnx.invoicex.gui.JDialogJasperViewer;
import it.tnx.invoicex.gui.JDialogRaggruppaArticoli;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import java.io.File;

import java.sql.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import net.sf.jasperreports.engine.JRAlignment;
import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRElement;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRFrame;
import net.sf.jasperreports.engine.JRLine;
import net.sf.jasperreports.engine.JRRectangle;
import net.sf.jasperreports.engine.JRTextElement;
import net.sf.jasperreports.engine.JRTextField;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.base.JRBaseElement;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignFrame;
import net.sf.jasperreports.engine.design.JRDesignLine;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.apache.commons.lang.StringUtils;
import reports.JRDSDdt;
import reports.JRDSDdt_lux;
import tnxbeans.tnxDbGrid;

public class frmElenDDT
        extends javax.swing.JInternalFrame {

    DefaultTableCellRenderer flagRender;
    DefaultTableCellRenderer evasoRender;
    public String sqlWhereLimit = "";
    public String sqlWhereDaData = "";
    public String sqlWhereAData = "";
    public String sqlWhereCliente = "";
    public String sqlWhereCausale = "";
    public String sqlWhereTipo = "";
    public String sqlOrder = null;
    private boolean visualizzaTotali = true;
    public boolean acquisto = false;
    private boolean apriDirDopoStampa = true;
    DelayedExecutor delay_cliente = new DelayedExecutor(new Runnable() {
        public void run() {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    SwingUtils.mouse_wait();
                    System.out.println("*** dbrefresh");
                    if (texCliente.getText().trim().length() == 0) {
                        sqlWhereCliente = "";
                    } else {
                        sqlWhereCliente = " and clie_forn.ragione_sociale like '%" + Db.aa(texCliente.getText()) + "%'";
                        if (main.fileIni.getValueBoolean("pref", "ColAgg_RiferimentoCliente", false)) {
                            sqlWhereCliente = " and (clie_forn.ragione_sociale like '%" + Db.aa(texCliente.getText()) + "%'";
                            sqlWhereCliente += " or clie_forn.persona_riferimento like '%" + Db.aa(texCliente.getText()) + "%'";
                            sqlWhereCliente += ")";
                        }
                    }
                    dbRefresh();
                    SwingUtils.mouse_def();
                }
            });
        }
    }, 250);

    public frmElenDDT() {
        this(false);
    }

    public frmElenDDT(boolean acquisto) {
        if (!acquisto) {
            try {
                InvoicexEvent event = new InvoicexEvent(this);
                event.type = InvoicexEvent.TYPE_FRMELENDDT_CONSTR_PRE_INIT_COMPS;
                main.events.fireInvoicexEvent(event);
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
        this.acquisto = acquisto;

        if (!main.substance) {
            flagRender = InvoicexUtil.getFlagRender();
        } else {
            flagRender = InvoicexUtil.getFlagRenderSubstance();
        }

        if (!main.substance) {
            evasoRender = InvoicexUtil.getEvasoRender();
        } else {
            evasoRender = InvoicexUtil.getEvasoRenderSubstance();
        }

        initComponents();

        if (main.fileIni.getValueBoolean("pref", "ColAgg_RiferimentoCliente", false)) {
            menColAggRiferimentoCliente.setSelected(true);
        }
        if (main.fileIni.getValueBoolean("pref", "ColAgg_Rif", false)) {
            menColAggRif.setSelected(true);
        }
        if (main.fileIni.getValueBoolean("pref", "ColAgg_Causaletrasporto", false)) {
            menColAggCausaleTrasporto.setSelected(true);
        }

        if (!InvoicexUtil.isFunzioniManutenzione()) {
            sep1.setVisible(false);
            menAzzeraConv.setVisible(false);
            menCalcEvaso.setVisible(false);
        }

        griglia.setNoTnxResize(true);
        comCliente.setVisible(false);


        comCausaleTrasporto.dbAddElement("");
        comCausaleTrasporto.dbOpenList(Db.getConn(), "select nome, id from tipi_causali_trasporto order by nome");

        setAcquisto(acquisto);

        main.events.fireInvoicexEvent(new InvoicexEvent(this, InvoicexEvent.TYPE_FRMELENDDT_CONSTR_POST_INIT_COMPS));

        if (main.pluginEmail) {
            griglia.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int r = griglia.rowAtPoint(e.getPoint());
                    int c = griglia.columnAtPoint(e.getPoint());
                    try {
                        if (c == griglia.getColumn("Mail Inviata").getModelIndex() && CastUtils.toInteger0(griglia.getValueAt(r, c)) >= 1) {
                            griglia.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        } else {
                            griglia.setCursor(Cursor.getDefaultCursor());
                        }
                    } catch (Exception ex) {
                    }
                }
            });
        }

        radAcquisto.setEnabled(main.utente.getPermesso(Permesso.PERMESSO_DDT_ACQUISTO, Permesso.PERMESSO_TIPO_LETTURA));
        radVendita.setEnabled(main.utente.getPermesso(Permesso.PERMESSO_DDT_VENDITA, Permesso.PERMESSO_TIPO_LETTURA));
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popupElenco = new javax.swing.JPopupMenu();
        menModifica = new javax.swing.JMenuItem();
        menElimina = new javax.swing.JMenuItem();
        menStampa = new javax.swing.JMenuItem();
        menDuplica = new javax.swing.JMenuItem();
        menPdfEmail = new javax.swing.JMenuItem();
        menConvFattura = new javax.swing.JMenuItem();
        menConvNotaDiCredito = new javax.swing.JMenuItem();
        menEsportaExcel = new javax.swing.JMenuItem();
        menExportCsv = new javax.swing.JMenuItem();
        sep1 = new javax.swing.JPopupMenu.Separator();
        menAzzeraConv = new javax.swing.JMenuItem();
        menCalcEvaso = new javax.swing.JMenuItem();
        menColAgg = new javax.swing.JMenu();
        menColAggRif = new javax.swing.JCheckBoxMenuItem();
        menColAggRiferimentoCliente = new javax.swing.JCheckBoxMenuItem();
        menColAggCausaleTrasporto = new javax.swing.JCheckBoxMenuItem();
        menColoraRiga = new javax.swing.JMenu();
        menColoraRosso = new javax.swing.JMenuItem();
        menColoraBlu = new javax.swing.JMenuItem();
        menColoraGiallo = new javax.swing.JMenuItem();
        menTogliColore = new javax.swing.JMenuItem();
        buttonGroup1 = new javax.swing.ButtonGroup();
        panDati = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        griglia = new tnxDbGrid() {
            Color color_hover = new Color(200,200,220);
            Color color_sel = new Color(155,155,175);
            Color color_fatt = new Color(255,255,255);
            Color color_red = InvoicexUtil.getColorePerMarcatura("rosso");
            Color color_blu = InvoicexUtil.getColorePerMarcatura("blu");
            Color color_yel = InvoicexUtil.getColorePerMarcatura("giallo");

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
                try {
                    if(getValueAt(row, getColumnByName("color")).equals("rosso")){
                        return color_red;
                    } else if(getValueAt(row, getColumnByName("color")).equals("blu")){
                        return color_blu;
                    } else if(getValueAt(row, getColumnByName("color")).equals("giallo")){
                        return color_yel;
                    } else {
                        return color_fatt;
                    }
                } catch (Exception e) {
                }
                return c.getBackground();
            }
        };
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        butNew = new javax.swing.JButton();
        butModi = new javax.swing.JButton();
        butDele = new javax.swing.JButton();
        butDuplica = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        radVendita = new javax.swing.JRadioButton();
        radAcquisto = new javax.swing.JRadioButton();
        jPanel5 = new javax.swing.JPanel();
        jPanel5.setLayout(new MyFlowLayout());
        butPrin = new javax.swing.JButton();
        butStampaConfermaOrdine = new javax.swing.JButton();
        butEmail = new javax.swing.JButton();
        butConv = new javax.swing.JButton();
        butConvNota = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel4.setLayout(new MyFlowLayout());
        jLabel2 = new javax.swing.JLabel();
        texLimit = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        texDal = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        texAl = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        comCliente = new tnxbeans.tnxComboField();
        texCliente = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        comCausaleTrasporto = new tnxbeans.tnxComboField();
        butRefresh = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        labTotale = new javax.swing.JLabel();

        menModifica.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/apps/accessories-text-editor.png")));
        menModifica.setText("Modifica");
        menModifica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menModificaActionPerformed(evt);
            }
        });
        popupElenco.add(menModifica);

        menElimina.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/places/user-trash.png"))); // NOI18N
        menElimina.setText("Elimina");
        menElimina.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menEliminaActionPerformed(evt);
            }
        });
        popupElenco.add(menElimina);

        menStampa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/document-print.png"))); // NOI18N
        menStampa.setText("Stampa");
        menStampa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menStampaActionPerformed(evt);
            }
        });
        popupElenco.add(menStampa);

        menDuplica.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/edit-copy.png"))); // NOI18N
        menDuplica.setText("Duplica");
        menDuplica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menDuplicaActionPerformed(evt);
            }
        });
        popupElenco.add(menDuplica);

        menPdfEmail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/acrobat16x16.png"))); // NOI18N
        menPdfEmail.setText("Crea Pdf per Email");
        menPdfEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menPdfEmailActionPerformed(evt);
            }
        });
        popupElenco.add(menPdfEmail);

        menConvFattura.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/emblems/emblem-system.png"))); // NOI18N
        menConvFattura.setText("Crea Fattura");
        menConvFattura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menConvFatturaActionPerformed(evt);
            }
        });
        popupElenco.add(menConvFattura);

        menConvNotaDiCredito.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/emblems/emblem-system.png"))); // NOI18N
        menConvNotaDiCredito.setText("Crea Nota di Credito");
        menConvNotaDiCredito.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menConvNotaDiCreditoActionPerformed(evt);
            }
        });
        popupElenco.add(menConvNotaDiCredito);

        menEsportaExcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/mimetypes/x-office-spreadsheet.png"))); // NOI18N
        menEsportaExcel.setText("Esporta su Excel");
        menEsportaExcel.setActionCommand("Esporta DDT in Excel");
        menEsportaExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menEsportaExcelActionPerformed(evt);
            }
        });
        popupElenco.add(menEsportaExcel);

        menExportCsv.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/media-seek-forward.png"))); // NOI18N
        menExportCsv.setText("Export righe in CSV");
        menExportCsv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menExportCsvActionPerformed(evt);
            }
        });
        popupElenco.add(menExportCsv);
        popupElenco.add(sep1);

        menAzzeraConv.setText("Azzera campo 'Fatturato'");
        menAzzeraConv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menAzzeraConvActionPerformed(evt);
            }
        });
        popupElenco.add(menAzzeraConv);

        menCalcEvaso.setText("Ricalcola stato fatturato");
        menCalcEvaso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menCalcEvasoActionPerformed(evt);
            }
        });
        popupElenco.add(menCalcEvaso);

        menColAgg.setText("Colonne Aggiuntive");

        menColAggRif.setText("Riferimento");
        menColAggRif.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menColAggRifActionPerformed(evt);
            }
        });
        menColAgg.add(menColAggRif);

        menColAggRiferimentoCliente.setText("Riferimento Cliente");
        menColAggRiferimentoCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menColAggRiferimentoClienteActionPerformed(evt);
            }
        });
        menColAgg.add(menColAggRiferimentoCliente);

        menColAggCausaleTrasporto.setText("Causale Trasporto");
        menColAggCausaleTrasporto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menColAggCausaleTrasportoActionPerformed(evt);
            }
        });
        menColAgg.add(menColAggCausaleTrasporto);

        popupElenco.add(menColAgg);

        menColoraRiga.setText("Marca");

        menColoraRosso.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/status/color-red.png"))); // NOI18N
        menColoraRosso.setText("Rosso");
        menColoraRosso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menColoraRossoActionPerformed(evt);
            }
        });
        menColoraRiga.add(menColoraRosso);

        menColoraBlu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/status/color-blu.png"))); // NOI18N
        menColoraBlu.setText("Blu");
        menColoraBlu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menColoraBluActionPerformed(evt);
            }
        });
        menColoraRiga.add(menColoraBlu);

        menColoraGiallo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/status/color-yellow.png"))); // NOI18N
        menColoraGiallo.setText("Giallo");
        menColoraGiallo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menColoraGialloActionPerformed(evt);
            }
        });
        menColoraRiga.add(menColoraGiallo);

        menTogliColore.setText("Togli Colore");
        menTogliColore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menTogliColoreActionPerformed(evt);
            }
        });
        menColoraRiga.add(menTogliColore);

        popupElenco.add(menColoraRiga);

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Gestione DDT di Vendita");
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosed(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
        });

        panDati.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        panDati.setLayout(new java.awt.BorderLayout());

        jScrollPane1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jScrollPane1MouseClicked(evt);
            }
        });

        griglia.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        griglia.setFont(griglia.getFont().deriveFont(griglia.getFont().getSize()+1f));
        griglia.setRowHeight(20);
        griglia.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                grigliaMouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                grigliaMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                grigliaMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(griglia);

        panDati.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        getContentPane().add(panDati, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));

        jPanel2.setLayout(new java.awt.BorderLayout());

        butNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/document-new.png"))); // NOI18N
        butNew.setText("Nuovo");
        butNew.setPreferredSize(null);
        butNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butNewActionPerformed(evt);
            }
        });
        jPanel6.add(butNew);

        butModi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/apps/accessories-text-editor.png"))); // NOI18N
        butModi.setText("Modifica");
        butModi.setIconTextGap(2);
        butModi.setPreferredSize(null);
        butModi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butModiActionPerformed(evt);
            }
        });
        jPanel6.add(butModi);

        butDele.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/places/user-trash.png"))); // NOI18N
        butDele.setText("Elimina");
        butDele.setToolTipText("Elimina");
        butDele.setIconTextGap(2);
        butDele.setPreferredSize(null);
        butDele.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butDeleActionPerformed(evt);
            }
        });
        jPanel6.add(butDele);

        butDuplica.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/edit-copy.png"))); // NOI18N
        butDuplica.setText("Duplica");
        butDuplica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butDuplicaActionPerformed(evt);
            }
        });
        jPanel6.add(butDuplica);

        jPanel2.add(jPanel6, java.awt.BorderLayout.CENTER);

        jLabel1.setText("Documenti di:");
        jPanel7.add(jLabel1);

        buttonGroup1.add(radVendita);
        radVendita.setText("Vendita");
        radVendita.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radVenditaActionPerformed(evt);
            }
        });
        jPanel7.add(radVendita);

        buttonGroup1.add(radAcquisto);
        radAcquisto.setText("Acquisto");
        radAcquisto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radAcquistoActionPerformed(evt);
            }
        });
        jPanel7.add(radAcquisto);

        jPanel2.add(jPanel7, java.awt.BorderLayout.EAST);

        jPanel1.add(jPanel2);

        butPrin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/document-print.png"))); // NOI18N
        butPrin.setText("Stampa");
        butPrin.setToolTipText("");
        butPrin.setIconTextGap(2);
        butPrin.setPreferredSize(null);
        butPrin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butPrinActionPerformed(evt);
            }
        });
        jPanel5.add(butPrin);

        butStampaConfermaOrdine.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/document-print.png"))); // NOI18N
        butStampaConfermaOrdine.setText("Stampa Conf. Ordine");
        butStampaConfermaOrdine.setToolTipText("");
        butStampaConfermaOrdine.setIconTextGap(2);
        butStampaConfermaOrdine.setPreferredSize(null);
        butStampaConfermaOrdine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butStampaConfermaOrdineActionPerformed(evt);
            }
        });
        jPanel5.add(butStampaConfermaOrdine);

        butEmail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/pdf-icon-16.png"))); // NOI18N
        butEmail.setText("Crea PDF");
        butEmail.setToolTipText("");
        butEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butEmailActionPerformed(evt);
            }
        });
        jPanel5.add(butEmail);

        butConv.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/emblems/emblem-system.png"))); // NOI18N
        butConv.setText("crea Fattura");
        butConv.setToolTipText("");
        butConv.setIconTextGap(2);
        butConv.setPreferredSize(null);
        butConv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butConvActionPerformed(evt);
            }
        });
        jPanel5.add(butConv);

        butConvNota.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/emblems/emblem-system.png"))); // NOI18N
        butConvNota.setText("crea Nota di Credito");
        butConvNota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butConvNotaActionPerformed(evt);
            }
        });
        jPanel5.add(butConvNota);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/emblems/emblem-system.png"))); // NOI18N
        jButton1.setText("Ordina per Fatturare");
        jButton1.setIconTextGap(2);
        jButton1.setPreferredSize(null);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel5.add(jButton1);

        jPanel1.add(jPanel5);

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel2.setText("visualizza");
        jPanel4.add(jLabel2);

        texLimit.setColumns(3);
        texLimit.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        texLimit.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                texLimitFocusLost(evt);
            }
        });
        texLimit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                texLimitKeyPressed(evt);
            }
        });
        jPanel4.add(texLimit);

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel3.setText("documenti");
        jPanel4.add(jLabel3);

        jLabel4.setText("|");
        jPanel4.add(jLabel4);

        jLabel5.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel5.setText("da data");
        jPanel4.add(jLabel5);

        texDal.setColumns(8);
        texDal.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        texDal.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                texDalFocusLost(evt);
            }
        });
        texDal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                texDalKeyPressed(evt);
            }
        });
        jPanel4.add(texDal);

        jLabel6.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel6.setText("a data");
        jPanel4.add(jLabel6);

        texAl.setColumns(8);
        texAl.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        texAl.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                texAlFocusLost(evt);
            }
        });
        texAl.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                texAlKeyPressed(evt);
            }
        });
        jPanel4.add(texAl);

        jLabel7.setText("|");
        jPanel4.add(jLabel7);

        jLabel8.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel8.setText("cliente");
        jPanel4.add(jLabel8);

        comCliente.setDbNomeCampo("");
        comCliente.setDbTrovaMentreScrive(true);
        comCliente.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        comCliente.setPreferredSize(new java.awt.Dimension(137, 18));
        comCliente.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                comClienteFocusLost(evt);
            }
        });
        comCliente.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comClienteItemStateChanged(evt);
            }
        });
        jPanel4.add(comCliente);

        texCliente.setColumns(10);
        texCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                texClienteKeyReleased(evt);
            }
        });
        jPanel4.add(texCliente);

        jLabel9.setText("|");
        jLabel9.setIconTextGap(2);
        jPanel4.add(jLabel9);

        jLabel10.setText("Caus. Trasp.");
        jPanel4.add(jLabel10);

        comCausaleTrasporto.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comCausaleTrasporto.setPreferredSize(new java.awt.Dimension(80, 20));
        comCausaleTrasporto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comCausaleTrasportoActionPerformed(evt);
            }
        });
        jPanel4.add(comCausaleTrasporto);

        butRefresh.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        butRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/view-refresh.png"))); // NOI18N
        butRefresh.setToolTipText("Aggiorna l'elenco dei documenti");
        butRefresh.setIconTextGap(2);
        butRefresh.setMargin(new java.awt.Insets(2, 2, 2, 2));
        butRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butRefreshActionPerformed(evt);
            }
        });
        jPanel4.add(butRefresh);

        jPanel1.add(jPanel4);

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel3.setLayout(new java.awt.BorderLayout(2, 2));

        labTotale.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labTotale.setText("totale documenti visualizzati ");
        labTotale.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        jPanel3.add(labTotale, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel3, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void butStampaConfermaOrdineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butStampaConfermaOrdineActionPerformed
        if (griglia.getSelectedRowCount() <= 0) {
            SwingUtils.showErrorMessage(this, "Seleziona un documento prima!");
            return;
        }

        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

        String dbSerie = String.valueOf(griglia.getValueAt(griglia.getSelectedRow(), 0));
        int dbNumero = Integer.parseInt(String.valueOf(griglia.getValueAt(griglia.getSelectedRow(), 1)));
        int dbAnno = Integer.parseInt(String.valueOf(griglia.getValueAt(griglia.getSelectedRow(), 2)));

        try {
            if (!acquisto) {
                Integer id_ddt = InvoicexUtil.getIdDdt(dbSerie, dbNumero, dbAnno);
                InvoicexUtil.aggiornaTotaliRighe(Db.TIPO_DOCUMENTO_DDT, id_ddt);
            } else {
                Integer id_ddt = InvoicexUtil.getIdDdtAcquisto(dbSerie, dbNumero, dbAnno);
                InvoicexUtil.aggiornaTotaliRighe(Db.TIPO_DOCUMENTO_DDT_ACQUISTO, id_ddt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        gestioneFatture.gianni.prnConfermaOrdine temp = new gestioneFatture.gianni.prnConfermaOrdine(dbSerie, dbNumero, dbAnno);
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_butStampaConfermaOrdineActionPerformed

    private void texAlKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texAlKeyPressed

        if (evt.getKeyCode() == evt.VK_ENTER) {
            texAlFocusLost(null);
        }
    }//GEN-LAST:event_texAlKeyPressed

    private void texDalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texDalKeyPressed

        if (evt.getKeyCode() == evt.VK_ENTER) {
            texDalFocusLost(null);
        }
    }//GEN-LAST:event_texDalKeyPressed

    private void texLimitKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texLimitKeyPressed

        if (evt.getKeyCode() == evt.VK_ENTER) {
            texLimitFocusLost(null);
        }
    }//GEN-LAST:event_texLimitKeyPressed

    private void butRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butRefreshActionPerformed
        sqlOrder = null;
        dbRefresh();
    }//GEN-LAST:event_butRefreshActionPerformed

    private void comClienteItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comClienteItemStateChanged

        if (evt.getStateChange() == evt.SELECTED) {
            comClienteFocusLost(null);
        }
    }//GEN-LAST:event_comClienteItemStateChanged

    private void comClienteFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_comClienteFocusLost

        filtraPerCliente();

    }//GEN-LAST:event_comClienteFocusLost

    public void filtraPerCliente() {
//        if (this.comCliente.getSelectedKey().toString().equals("*")) {
//            sqlWhereCliente = "";
//        } else {
//            sqlWhereCliente = " and cliente = " + Db.pc(this.comCliente.getSelectedKey(), Types.INTEGER);
//        }
//        dbRefresh();
        delay_cliente.update();
    }

    private void texAlFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texAlFocusLost

        if (this.texAl.getText().length() == 0) {
            sqlWhereAData = "";
            dbRefresh();
        } else {

            if (it.tnx.Checks.isDate(this.texAl.getText())) {
                sqlWhereAData = " and data <= " + Db.pc2(this.texAl.getText(), Types.DATE);
                dbRefresh();
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "Il parametro richiesto deve essere una data in formato (gg/mm/aaaa)", "Attenzione", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_texAlFocusLost

    private void texDalFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texDalFocusLost

        if (this.texDal.getText().length() == 0) {
            sqlWhereDaData = "";
            dbRefresh();
        } else {

            if (it.tnx.Checks.isDate(this.texDal.getText())) {
                sqlWhereDaData = " and data >= " + Db.pc2(this.texDal.getText(), Types.DATE);
                dbRefresh();
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "Il parametro richiesto deve essere una data in formato (gg/mm/aaaa)", "Attenzione", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_texDalFocusLost

    private void texLimitFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texLimitFocusLost

        if (this.texLimit.getText().length() == 0) {
            sqlWhereLimit = "";
            dbRefresh();
        } else {

            if (it.tnx.Checks.isInteger(this.texLimit.getText())) {
                sqlWhereLimit = " limit " + this.texLimit.getText();
                dbRefresh();
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "Il parametro richiesto deve essere numerico", "Attenzione", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_texLimitFocusLost

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        sqlOrder = " order by clie_forn.ragione_sociale, t.data desc";
        dbRefresh();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void butConvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butConvActionPerformed
        if (griglia.getSelectedRowCount() <= 0) {
            SwingUtils.showErrorMessage(this, "Seleziona un documento prima!");
            return;
        }

        Vector tempElencoDdt = new Vector();
        Vector tempElencoDdtR = new Vector(); //per DDT da raggruppare
        String tempWhereDdt = "";
        int[] righeSelezionate = this.griglia.getSelectedRows();
        String cliente = "";
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

        if (this.griglia.getSelectedRowCount() < 1) {
            JOptionPane.showMessageDialog(this, "Si deve selezionare almeno un D.D.T.");
        } else {

            //controllo che abbia preso lo stesso cliente
            cliente = Db.nz(griglia.getValueAt(righeSelezionate[0], griglia.getColumnByName(acquisto ? "Fornitore" : "Cliente")), "").toString();

            for (int u = 0; u < this.griglia.getSelectedRowCount(); u++) {

                if (!Db.nz(griglia.getValueAt(righeSelezionate[u], griglia.getColumnByName(acquisto ? "Fornitore" : "Cliente")), "").toString().equals(cliente)) {
                    JOptionPane.showMessageDialog(this, "Si deve selezionare i D.D.T di un solo cliente per volta");
                    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

                    return;
                }
            }

            //controllo già convertiti
            boolean showed = false;
            for (int u = 0; u < this.griglia.getSelectedRowCount(); u++) {
                if (Db.nz(griglia.getValueAt(righeSelezionate[u], 3), "").toString().length() > 0 && !showed) {
                    int ret = JOptionPane.showConfirmDialog(this, "Ci sono uno o piu' D.D.T. gia' fatturati nella selezione, continuare lo stesso ?", "Attenzione", javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE);
                    showed = true;
                    if (ret == javax.swing.JOptionPane.NO_OPTION) {
                        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        return;
                    }
                }
            }

            //come convertire ?
            boolean raggr = false;
            boolean raggr_riepilogo = false;
            if (griglia.getSelectedRowCount() > 1) {
                try {
                    int raggruppa_articoli = Integer.parseInt(main.fileIni.getValue("pref", "raggruppa_articoli", "2"));
                    if (raggruppa_articoli == 4) {
                        //raggruppa riepilogando una riga per ogni ddt
                        raggr_riepilogo = true;
                    } else {
                        String sqlcliente = "select c.opzione_raggruppa_ddt from " + getNomeTab() + " t join clie_forn c on t.cliente = c.codice "
                                + " where t.serie = '" + griglia.getValueAt(griglia.getSelectedRow(), 0) + "'"
                                + " and t.numero = " + griglia.getValueAt(griglia.getSelectedRow(), 1)
                                + " and t.anno = " + griglia.getValueAt(griglia.getSelectedRow(), 2);
                        if (raggruppa_articoli == 0) {//no
                            raggr = false;
                        } else if (raggruppa_articoli == 1) {//Raggruppa sempre
                            raggr = true;
                        } else if (raggruppa_articoli == 2) {//Chiedi sempre (suggerendo dal Cliente)
                            raggr = false;
                            String opz = "N";
                            try {
                                opz = (String) DbUtils.getObject(Db.conn, sqlcliente);
                            } catch (Exception ex2) {
                                ex2.printStackTrace();
                            }
                            JDialogRaggruppaArticoli draggr = new JDialogRaggruppaArticoli(main.getPadre(), true);
                            if ("S".equalsIgnoreCase(opz)) {
                                raggr = true;
                                draggr.labcliente.setText("Il Cliente del documento preferisce avere gli articoli raggruppati");
                                draggr.raggruppa.setSelected(true);
                            } else {
                                draggr.labcliente.setText("");
                                draggr.raggruppa.setSelected(false);
                            }
                            draggr.setLocationRelativeTo(null);
                            draggr.setVisible(true);
                            if (!draggr.prosegui) {
                                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                                return;
                            }
                            if (draggr.raggruppa.isSelected()) {
                                raggr = true;
                            } else {
                                raggr = false;
                            }
                        } else if (raggruppa_articoli == 3) {//Raggruppa in base al Cliente
                            raggr = false;
                            String opz = "N";
                            try {
                                opz = (String) DbUtils.getObject(Db.conn, sqlcliente);
                            } catch (Exception ex2) {
                                ex2.printStackTrace();
                            }
                            if ("S".equalsIgnoreCase(opz)) {
                                raggr = true;
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            //genero la nuova fatture
            dbDocumento prev = new dbDocumento();
            prev.acquisto = acquisto;

            ArrayList elenco1 = new ArrayList();
            for (int i = 0; i < this.griglia.getSelectedRowCount(); i++) {
                Object[] row = new Object[3];
                row[0] = griglia.getValueAt(righeSelezionate[i], 0);    //serie
                row[1] = Integer.parseInt(griglia.getValueAt(righeSelezionate[i], 1).toString());    //numero
                row[2] = Integer.parseInt(griglia.getValueAt(righeSelezionate[i], 2).toString());    //anno
                elenco1.add(row);

                try {
                    if (!acquisto) {
                        Integer id_ddt = InvoicexUtil.getIdDdt(CastUtils.toString(row[0]), CastUtils.toInteger(row[1]), CastUtils.toInteger(row[2]));
                        InvoicexUtil.aggiornaTotaliRighe(Db.TIPO_DOCUMENTO_DDT, id_ddt);
                    } else {
                        Integer id_ddt = InvoicexUtil.getIdDdtAcquisto(CastUtils.toString(row[0]), CastUtils.toInteger(row[1]), CastUtils.toInteger(row[2]));
                        InvoicexUtil.aggiornaTotaliRighe(Db.TIPO_DOCUMENTO_DDT_ACQUISTO, id_ddt);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            Collections.sort(elenco1, new Comparator() {
                public int compare(Object o1, Object o2) {
                    Object[] row1 = (Object[]) o1;
                    Object[] row2 = (Object[]) o2;
                    if (row1[0].toString().equals(row2[0].toString())) {
                        if (((Integer) row1[2]).equals((Integer) row2[2])) {
                            return ((Integer) row1[1]).compareTo((Integer) row2[1]);
                        } else {
                            return ((Integer) row1[2]).compareTo((Integer) row2[2]);
                        }
                    } else {
                        return row1[0].toString().compareTo(row2[0].toString());
                    }
                }
            });

            for (int i = 0; i < elenco1.size(); i++) {
                Object[] row = (Object[]) elenco1.get(i);
                tempWhereDdt = " t.serie = " + Db.pc(row[0].toString(), "VARCHAR");
                tempWhereDdt = tempWhereDdt + " and t.numero = " + Db.pc(row[1].toString(), "NUMBER");
                tempWhereDdt = tempWhereDdt + " and t.anno = " + Db.pc(row[2].toString(), Types.VARCHAR);

                //aggiungo al vettore
                tempElencoDdt.add(" where (" + tempWhereDdt + " )");
                tempElencoDdtR.add(" (" + tempWhereDdt + " ) ");
            }

            prev.serie = griglia.getValueAt(righeSelezionate[0], 0).toString();
            prev.elencoDdt = tempElencoDdt;
            prev.elencoDdtR = tempElencoDdtR;
            prev.ids = getIds();

            String optRiportaSerie = main.fileIni.getValue("pref", "riporta_serie", "0");
            int repSerie = javax.swing.JOptionPane.YES_OPTION;
            if (optRiportaSerie.equals("0")) {
                if (!prev.serie.equals("")) {
                    repSerie = JOptionPane.showConfirmDialog(this, "Vuoi riportare la serie del DDT in Fattura?", "Riporta serie", javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE);
                }
            } else if (optRiportaSerie.equals("2") || optRiportaSerie.equals("3")) {
                repSerie = javax.swing.JOptionPane.NO_OPTION;
            }

            String ret = prev.convertiInFattura(raggr, raggr_riepilogo, repSerie == javax.swing.JOptionPane.YES_OPTION);
            if (ret != null) {
                JOptionPane.showMessageDialog(this, "La nuova fattura e' la " + ret);
            } else {
                SwingUtils.showInfoMessage(this, "Conversione annullata");
            }
        }

        //aggiorno la tabella
        dbRefresh();
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_butConvActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened

        // Add your handling code here:
        this.griglia.resizeColumnsPerc(true);
    }//GEN-LAST:event_formInternalFrameOpened

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed

        // Add your handling code here:
        main.getPadre().closeFrame(this);
    }//GEN-LAST:event_formInternalFrameClosed

    private void jScrollPane1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPane1MouseClicked
    }//GEN-LAST:event_jScrollPane1MouseClicked

    private void butDeleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butDeleActionPerformed
        if (griglia.getRowCount() <= 0) {
            SwingUtils.showErrorMessage(this, "Seleziona un documento prima!");
            return;
        }

        if (griglia.getSelectedRow() < 0) {
            return;
        }

        Object inizio_mysql = Db.getCurrentTimestamp();
        
        String sql;
        int dbAnno = 0;

        //controllo se il ddt puo' essere eliminato
        //lo puo' solo se ?? l'ultimo, altrimenti cambia la numerazione!
        //Modifica di lorenzo: questa riga era spostata piu' in basso, e cosi' la riga 316 dava errore
        try {
            dbAnno = Integer.parseInt(String.valueOf(griglia.getValueAt(griglia.getSelectedRow(), 2)));

            int numeroSelezionato = Integer.parseInt(String.valueOf(griglia.getValueAt(griglia.getSelectedRow(), griglia.getColumnByName("numero"))));
            int numeroInDb = 0;
            sql = "select numero from " + getNomeTab() + "";
            sql += " where serie = '" + griglia.getValueAt(griglia.getSelectedRow(), griglia.getColumnByName("serie")) + "'";
            sql += " and anno = " + dbAnno;
            sql += " order by numero desc";

            ResultSet tempNumero = Db.openResultSet(sql);
            tempNumero.next();
            numeroInDb = tempNumero.getInt(1);

            if (numeroInDb != numeroSelezionato) {
                java.util.prefs.Preferences preferences = java.util.prefs.Preferences.userNodeForPackage(main.class);
//                boolean azioniPericolose = preferences.getBoolean("azioniPericolose", false);
                boolean azioniPericolose = main.fileIni.getValueBoolean("pref", "azioniPericolose", true);
                if (!azioniPericolose) {
                    javax.swing.JOptionPane.showMessageDialog(this, "Il documento non puo' essere eliminato", "Attenzione", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    return;
                } else {
                    int ret = JOptionPane.showConfirmDialog(this, "Il documento non andrebbe eliminato per la progressione della numerazione, sei sicuro di eliminarlo ?", "Attenzione", javax.swing.JOptionPane.YES_NO_OPTION);
                    if (ret == JOptionPane.NO_OPTION) {
                        return;
                    }
                }
            }
        } catch (Exception err) {
            err.printStackTrace();
        }

        // Controllo provenienza da Ordine
        int numSel = Integer.parseInt(String.valueOf(griglia.getValueAt(griglia.getSelectedRow(), griglia.getColumnByName("numero"))));
        String grigliaSerie = String.valueOf(griglia.getValueAt(griglia.getSelectedRow(), griglia.getColumnByName("serie")));
        String grigliaAnno = String.valueOf(griglia.getValueAt(griglia.getSelectedRow(), griglia.getColumnByName("anno")));
        String check = "";
        if (!grigliaSerie.equals("")) {
            check += grigliaSerie + "/" + numSel;
        } else {
            check += "" + numSel;
        }

        String tabOrdini = this.acquisto ? "test_ordi_acquisto" : "test_ordi";
        String sqlOrdine = "SELECT * FROM " + tabOrdini + " WHERE ";
        sqlOrdine += "convertito like '%DDT %' AND doc_anno = (" + grigliaAnno + ") AND ";
        sqlOrdine += "(convertito like '% " + check + "\\n%' OR convertito REGEXP ' " + check + "$')";
        boolean fromOtherDoc = false;
        ResultSet recOrdini = Db.openResultSet(sqlOrdine);
        try {
            if (recOrdini.next()) {
                if (!SwingUtils.showYesNoMessage(this, "Questo documento proviene da un Ordine. Continuare con la cancellazione?")) {
                    return;
                } else {
                    fromOtherDoc = true;
                    recOrdini.beforeFirst();
                }
            }
        } catch (Exception e) {
            JDialogExc dialog = new JDialogExc(main.getPadreFrame(), true, e);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
            return;
        }

        //chiedo conferma per eliminare il documento
        String msg = "Sicuro di eliminare il ddt ?";
        int res = JOptionPane.showConfirmDialog(this, msg);

        if (res == JOptionPane.OK_OPTION) {
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

            try {
                int dbIdDocu = Integer.parseInt(String.valueOf(griglia.getValueAt(griglia.getSelectedRow(), griglia.getColumnByName("ID"))));
                InvoicexUtil.storicizza("elimina ddt id:" + dbIdDocu, "ddt", dbIdDocu);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (fromOtherDoc) {
                try {
                    while (recOrdini.next()) {
                        String convertito = recOrdini.getString("convertito");
                        int index = convertito.indexOf("DDT " + check + "\n");
                        if (index != -1) {
                            convertito = convertito.replace("DDT " + check + "\n", "");
                        } else {
                            convertito = convertito.replaceAll("\nDDT " + check + "$", "").trim();
                            convertito = convertito.replaceAll("DDT " + check + "$", "").trim();
                            convertito = convertito.replace("DDT " + check, "").trim();
                        }

                        sql = "UPDATE " + tabOrdini + " SET ";
                        if (convertito.equals("")) {
                            sql += "convertito = NULL";
                            sql += ", evaso = '', doc_tipo = NULL, doc_serie = NULL, doc_numero = NULL, doc_anno = NULL";
                        } else {
                            sql += "convertito = '" + convertito + "'";
                            sql += ", evaso = 'P', doc_numero = '" + convertito.substring(convertito.lastIndexOf(" ")) + "'";
                        }
                        sql += " WHERE id = '" + recOrdini.getString("id") + "' ";
                        // TOLGO I RIFERIMENTI DALL'ORDINE
                        Db.executeSql(sql);

                        // Seleziono tabelle righe acquisto o vendita
                        String tabRigheOrdi = this.acquisto ? "righ_ordi_acquisto" : "righ_ordi";
                        String tabRigheDdt = this.acquisto ? "righ_ddt_acquisto" : "righ_ddt";

                        sql = "UPDATE " + tabRigheOrdi + " rord LEFT JOIN " + tabRigheDdt + " rddt ON rddt.da_ordi_riga = rord.id ";
                        sql += "SET rord.quantita_evasa = rord.quantita_evasa - rddt.quantita ";
                        sql += "WHERE da_ordi = " + Db.pc(recOrdini.getString("id"), Types.INTEGER);
                        sql += " AND rddt.id_padre = '" + griglia.getValueAt(griglia.getSelectedRow(), griglia.getColumnByName("id")) + "'";
                        // TOLGO I RIFERIMENTI DALLE RIGHE DELL'ORDINE
                        Db.executeSql(sql);
                    }
                } catch (SQLException ex) {
                    JDialogExc dialog = new JDialogExc(main.getPadreFrame(), true, ex);
                    dialog.setLocationRelativeTo(null);
                    dialog.setVisible(true);
                    return;
                }
            }

            //Modifica di lorenzo: nella riga sotto c'era scritto "String sql = "delete from righ_ddt"
            sql = "delete from " + getNomeTabr();
            sql += " where serie = '" + griglia.getValueAt(griglia.getSelectedRow(), griglia.getColumnByName("serie")) + "'";
            sql += " and numero = '" + griglia.getValueAt(griglia.getSelectedRow(), griglia.getColumnByName("numero")) + "'";
            sql += " and anno = " + dbAnno;
            //sql += " and stato = 'P'";
            Db.executeSql(sql);

            sql = "delete from " + getNomeTabr() + "_matricole";
            sql += " where serie = '" + griglia.getValueAt(griglia.getSelectedRow(), griglia.getColumnByName("serie")) + "'";
            sql += " and numero = '" + griglia.getValueAt(griglia.getSelectedRow(), griglia.getColumnByName("numero")) + "'";
            sql += " and anno = " + dbAnno;
            Db.executeSql(sql);

            //memorizzo gli eliminati
            sql = " where da_tabella = '" + getNomeTab() + "'";
            sql += " and da_serie = '" + griglia.getValueAt(griglia.getSelectedRow(), griglia.getColumnByName("serie")) + "'";
            sql += " and da_numero = '" + griglia.getValueAt(griglia.getSelectedRow(), griglia.getColumnByName("numero")) + "'";
            sql += " and da_anno = " + Db.pc(dbAnno, "INTEGER");
            
            Magazzino.preDelete(sql);
            
            //elimino eventuali movimenti precedenti derivanti dallo stesso documento
            String sqldel = "delete from movimenti_magazzino" + sql;
            Db.executeSql(sqldel);

            //debug
            //JOptionPane.showMessageDialog(this,sql);
            //debug
            this.griglia.dbDelete();
            
            main.events.fireInvoicexEventMagazzino(this, inizio_mysql);
            
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }//GEN-LAST:event_butDeleActionPerformed

    private void butPrinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butPrinActionPerformed

        if (griglia.getSelectedRowCount() <= 0) {
            SwingUtils.showErrorMessage(this, "Seleziona un documento prima!");
            return;
        }

        main.loadIni();
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

        if (griglia.getSelectedRowCount() > 1) {
            SwingWorker work = new SwingWorker() {
                @Override
                public Object construct() {
                    ArrayList files = new ArrayList();
                    for (int i : griglia.getSelectedRows()) {
                        System.out.println("stampa ddt:" + i);
                        //final String tipoFattura = String.valueOf(griglia.getValueAt(i, 0));
                        final String dbSerie = String.valueOf(griglia.getValueAt(i, 0));
                        final int dbNumero = Integer.parseInt(String.valueOf(griglia.getValueAt(i, 1)));
                        final int dbAnno = Integer.parseInt(String.valueOf(griglia.getValueAt(i, 2)));
                        final Integer id = cu.toInteger(griglia.getValueAt(i, griglia.getColumnByName("id")));
                        
                        try {
                            if (!acquisto) {
                                InvoicexUtil.aggiornaTotaliRighe(Db.TIPO_DOCUMENTO_DDT, id);
                            } else {
                                InvoicexUtil.aggiornaTotaliRighe(Db.TIPO_DOCUMENTO_DDT_ACQUISTO, id);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Object ret = stampa("", dbSerie, dbNumero, dbAnno, true, true, false, acquisto, id);
                        files.add(ret);
                    }
                    //concateno i pdf e li visualizzo
                    String out = System.getProperty("user.home") + "/stampa.pdf";
                    ItextUtil.concatenate(out, (String[]) files.toArray(new String[files.size()]));
                    Util.start(out);
                    return null;
                }
            };
            work.start();
        } else {
            int i = griglia.getSelectedRow();
            //final String tipoFattura = String.valueOf(griglia.getValueAt(i, 0));
            final String dbSerie = String.valueOf(griglia.getValueAt(i, 0));
            final int dbNumero = Integer.parseInt(String.valueOf(griglia.getValueAt(i, 1)));
            final int dbAnno = Integer.parseInt(String.valueOf(griglia.getValueAt(i, 2)));
            final Integer id = cu.toInteger(griglia.getValueAt(i, griglia.getColumnByName("id")));

            try {
                if (!acquisto) {
                    InvoicexUtil.aggiornaTotaliRighe(Db.TIPO_DOCUMENTO_DDT, id);
                } else {
                    InvoicexUtil.aggiornaTotaliRighe(Db.TIPO_DOCUMENTO_DDT_ACQUISTO, id);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            stampa("", dbSerie, dbNumero, dbAnno, acquisto, id);
        }

        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));


    }//GEN-LAST:event_butPrinActionPerformed

    private void butModiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butModiActionPerformed
        int permesso = acquisto ? Permesso.PERMESSO_DDT_ACQUISTO : Permesso.PERMESSO_DDT_VENDITA;
        if (main.utente.getPermesso(permesso, Permesso.PERMESSO_TIPO_SCRITTURA)) {

            if (griglia.getSelectedRowCount() <= 0) {
                SwingUtils.showErrorMessage(this, "Seleziona un documento prima!");
                return;
            }
            
            SwingUtils.mouse_wait(this);
            SwingUtils.mouse_wait(this.griglia);

            String dbSerie = String.valueOf(griglia.getValueAt(griglia.getSelectedRow(), 0));
            int dbNumero = Integer.parseInt(String.valueOf(griglia.getValueAt(griglia.getSelectedRow(), 1)));
            int dbAnno = Integer.parseInt(String.valueOf(griglia.getValueAt(griglia.getSelectedRow(), 2)));
            int dbIdDocu = Integer.parseInt(String.valueOf(griglia.getValueAt(griglia.getSelectedRow(), griglia.getColumnByName("ID"))));

            //controllo se è già aperta andare su quella
            List<JInternalFrame> frames = InvoicexUtil.getFrames(frmTestDocu.class);
            for (JInternalFrame iframe : frames) {
                frmTestDocu f = (frmTestDocu) iframe;
                if (f.id != null && f.id.equals(dbIdDocu)) {
                    System.out.println("trovata form già aperta");
                    main.getPadre().getDesktopPane().getDesktopManager().activateFrame(f);
                    SwingUtils.mouse_def(this);
                    SwingUtils.mouse_def(this.griglia);
                    return;
                }
            }

            try {
                InvoicexUtil.storicizza("modifica ddt id:" + dbIdDocu + " serie:" + dbSerie + " numero:" + dbNumero + " anno:" + dbAnno, "ddt", dbIdDocu);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (!acquisto) {
                    InvoicexUtil.aggiornaTotaliRighe(Db.TIPO_DOCUMENTO_DDT, dbIdDocu);
                } else {
                    InvoicexUtil.aggiornaTotaliRighe(Db.TIPO_DOCUMENTO_DDT_ACQUISTO, dbIdDocu);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            frmTestDocu frm = new frmTestDocu(frmTestDocu.DB_MODIFICA, dbSerie, dbNumero, "P", dbAnno, dbIdDocu, acquisto);
            frm.from = this;

            Menu m = (Menu) main.getPadre();
            m.openFrame(frm, 740, InvoicexUtil.getHeightIntFrame(750));

            SwingUtils.mouse_def(this);
            SwingUtils.mouse_def(this.griglia);
        }
    }//GEN-LAST:event_butModiActionPerformed

    private void butNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butNewActionPerformed
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

        frmTestDocu frm = new frmTestDocu(frmTestDocu.DB_INSERIMENTO, "", 0, "P", 0, -1, acquisto);
        frm.from = this;

        Menu m = (Menu) main.getPadre();
        m.openFrame(frm, 740, InvoicexUtil.getHeightIntFrame(750));



        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_butNewActionPerformed

    public void CreaPdfPerEmail() {
        this.apriDirDopoStampa = false;
        this.butEmailActionPerformed(null);
        this.apriDirDopoStampa = true;
    }

private void butEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butEmailActionPerformed
    if (griglia.getSelectedRowCount() <= 0) {
        SwingUtils.showErrorMessage(this, "Seleziona un documento prima!");
        return;
    }
    Integer[] id = new Integer[griglia.getSelectedRowCount()];
    for (int i = 0; i < griglia.getSelectedRowCount(); i++) {
        id[i] = cu.toInteger(griglia.getValueAt(griglia.getSelectedRows()[i], griglia.getColumnByName("id")));
    }
    try {
        InvoicexUtil.creaPdf(acquisto ? Db.TIPO_DOCUMENTO_DDT_ACQUISTO : Db.TIPO_DOCUMENTO_DDT, id, apriDirDopoStampa, false);
    } catch (Exception e) {
        e.printStackTrace();
        SwingUtils.showExceptionMessage(this, e);
    }
}//GEN-LAST:event_butEmailActionPerformed

private void menModificaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menModificaActionPerformed
    butModiActionPerformed(null);
}//GEN-LAST:event_menModificaActionPerformed

private void menEliminaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menEliminaActionPerformed
    butDeleActionPerformed(null);
}//GEN-LAST:event_menEliminaActionPerformed

private void menStampaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menStampaActionPerformed
    butPrinActionPerformed(null);
}//GEN-LAST:event_menStampaActionPerformed

private void menPdfEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menPdfEmailActionPerformed
    butEmailActionPerformed(null);
}//GEN-LAST:event_menPdfEmailActionPerformed

private void menConvFatturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menConvFatturaActionPerformed
    butConvActionPerformed(null);
}//GEN-LAST:event_menConvFatturaActionPerformed

private void grigliaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_grigliaMouseClicked
    try {
        int r = griglia.rowAtPoint(evt.getPoint());
        int c = griglia.columnAtPoint(evt.getPoint());
        if (griglia.hasColumn("Mail Inviata") && main.pluginEmail && c == griglia.getColumn("Mail Inviata").getModelIndex() && CastUtils.toInteger0(griglia.getValueAt(r, c)) >= 1) {
            HashMap params = new HashMap();
            params.put("source", this);
            params.put("tipo", "DDT di " + (acquisto ? "Acquisto" : "Vendita"));
            params.put("id", griglia.getValueAt(r, griglia.getColumnByName("id")));
            InvoicexEvent event = new InvoicexEvent(params);
            event.type = InvoicexEvent.TYPE_EMAIL_STORICO;
            main.events.fireInvoicexEvent(event);
        } else {
            if (evt.getClickCount() == 2) {
                butModiActionPerformed(null);
            } else {
                //tasto destro
                //if (e.getModifiers()==InputEvent.BUTTON3_MASK) popGrig.show(tabNomi,e.getX(),e.getY());
            }
        }
    } catch (Exception err) {
        err.printStackTrace();
    }
}//GEN-LAST:event_grigliaMouseClicked

private void grigliaMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_grigliaMousePressed
// TODO add your handling code here:

    if (evt.isPopupTrigger()) {
        popupElenco.show(evt.getComponent(), evt.getX(), evt.getY());
    }

}//GEN-LAST:event_grigliaMousePressed

private void grigliaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_grigliaMouseReleased
// TODO add your handling code here:

    if (evt.isPopupTrigger()) {
        popupElenco.show(evt.getComponent(), evt.getX(), evt.getY());
    }
}//GEN-LAST:event_grigliaMouseReleased

private void butDuplicaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butDuplicaActionPerformed
    int id = -1;

    if (griglia.getSelectedRowCount() <= 0) {
        SwingUtils.showErrorMessage(this, "Seleziona un documento prima!");
        return;
    }

    String sql;
    String sqlC = "";
    String sqlV = "";
    int newAnno = java.util.Calendar.getInstance().get(Calendar.YEAR);
    int newNumero;

    int numDup = griglia.getSelectedRows().length;
    int res;
    //chiedo conferma per eliminare il documento
    if (numDup > 1) {
        String msg = "Sicuro di voler duplicare " + numDup + " DDT ?";
        res = JOptionPane.showConfirmDialog(this, msg);
    } else {
        res = JOptionPane.OK_OPTION;
    }

    if (res == JOptionPane.OK_OPTION) {
        for (int sel : griglia.getSelectedRows()) {

            String dbSerie = String.valueOf(griglia.getValueAt(sel, 0));
            int dbNumero = Integer.parseInt(String.valueOf(griglia.getValueAt(sel, 1)));
            int dbAnno = Integer.parseInt(String.valueOf(griglia.getValueAt(sel, 2)));

            try {
                if (!acquisto) {
                    Integer id_ddt = InvoicexUtil.getIdDdt(dbSerie, dbNumero, dbAnno);
                    InvoicexUtil.aggiornaTotaliRighe(Db.TIPO_DOCUMENTO_DDT, id_ddt);
                } else {
                    Integer id_ddt = InvoicexUtil.getIdDdtAcquisto(dbSerie, dbNumero, dbAnno);
                    InvoicexUtil.aggiornaTotaliRighe(Db.TIPO_DOCUMENTO_DDT_ACQUISTO, id_ddt);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //cerco ultimo numero ordine
            newNumero = 1;
            sqlC = "";
            sqlV = "";
            sql = "SELECT MAX(numero) as maxnum FROM " + getNomeTab() + " WHERE anno = '" + newAnno + "'";
            sql += " and serie = '" + dbSerie + "'";
            try {
                ResultSet tempUltimo = Db.openResultSet(sql);
                if (tempUltimo.next() == true) {
                    newNumero = tempUltimo.getInt("maxnum") + 1;
                }
            } catch (Exception err) {
                err.printStackTrace();
            }

            //    SwingUtils.showInfoMessage(this, "newnumero:" + newNumero);

            //inserisco nuovo ordine salvandomi i dati su hashtable
            sql = "select * from " + getNomeTab() + "";
            sql += " where serie = '" + dbSerie + "'";
            sql += " and numero = '" + dbNumero + "'";
            sql += " and anno = '" + dbAnno + "'";
            ResultSet tempPrev = Db.openResultSet(sql);

            try {
                ResultSetMetaData metaPrev = tempPrev.getMetaData();

                List colonne_da_ignorare = new ArrayList();
                colonne_da_ignorare.add("mail_inviata");
                colonne_da_ignorare.add("stampato");
                colonne_da_ignorare.add("fattura_serie");
                colonne_da_ignorare.add("fattura_numero");
                colonne_da_ignorare.add("fattura_anno");
                colonne_da_ignorare.add("evaso");
                colonne_da_ignorare.add("convertito");
                colonne_da_ignorare.add("ts");
                colonne_da_ignorare.add("ts_gen_totali");                

                if (tempPrev.next() == true) {
                    for (int i = 1; i <= metaPrev.getColumnCount(); i++) {
                        if (!metaPrev.getColumnName(i).equalsIgnoreCase("id")) {
                            if (metaPrev.getColumnName(i).equalsIgnoreCase("numero")) {
                                sqlC += "numero";
                                sqlV += Db.pc(newNumero, metaPrev.getColumnType(i));
                            } else if (metaPrev.getColumnName(i).equalsIgnoreCase("anno")) {
                                sqlC += "anno";
                                sqlV += Db.pc(java.util.Calendar.getInstance().get(Calendar.YEAR), "LONG");
                            } else if (metaPrev.getColumnName(i).equalsIgnoreCase("data")) {
                                DateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
                                Calendar myCalendar = GregorianCalendar.getInstance();
                                sqlC += "data";
                                sqlV += Db.pc(myFormat.format(myCalendar.getTime()), metaPrev.getColumnType(i));
                            } else {
                                if (!colonne_da_ignorare.contains(metaPrev.getColumnName(i))) {
                                    sqlC += metaPrev.getColumnName(i);
                                    sqlV += Db.pc(tempPrev.getObject(i), metaPrev.getColumnType(i));
                                }
                            }
                            if (!colonne_da_ignorare.contains(metaPrev.getColumnName(i))) {
                                sqlC += ",";
                                sqlV += ",";
                            }
                        }
                    }
                    sqlC = StringUtils.chop(sqlC);
                    sqlV = StringUtils.chop(sqlV);
                    if (sqlC.endsWith(",")) {
                        sqlC = StringUtils.chop(sqlC);
                    }
                    if (sqlV.endsWith(",")) {
                        sqlV = StringUtils.chop(sqlV);
                    }                    
                    sql = "insert into " + getNomeTab() + " ";
                    sql += "(" + sqlC + ") values (" + sqlV + ")";
                    Db.executeSql(sql);
                }
            } catch (Exception err) {
                err.printStackTrace();
            }

            id = -1;
            try {
                id = CastUtils.toInteger0(DbUtils.getObject(Db.getConn(), "select LAST_INSERT_ID()")).intValue();
            } catch (Exception e) {
                SwingUtils.showErrorMessage(this, e.getLocalizedMessage());
            }

            //inserisco nuovo salvandomi i dati su hashtable
            sql = "select * from " + getNomeTabr();
            sql += " where serie = '" + dbSerie + "'";
            sql += " and numero = '" + dbNumero + "'";
            sql += " and anno = '" + dbAnno + "'";
            ResultSet tempPrev2 = Db.openResultSet(sql);
            try {
                ResultSetMetaData metaPrev2 = tempPrev2.getMetaData();

                List colonne_da_ignorare = new ArrayList();
                colonne_da_ignorare.add("id");
                colonne_da_ignorare.add("quantita_evasa");
                colonne_da_ignorare.add("in_fatt_riga");
                colonne_da_ignorare.add("da_ordi_riga");
                colonne_da_ignorare.add("in_fatt");
                colonne_da_ignorare.add("da_ordi");
                colonne_da_ignorare.add("ts");
                colonne_da_ignorare.add("ts_gen_totali");                

                while (tempPrev2.next() == true) {
                    sqlC = "";
                    sqlV = "";
                    for (int i = 1; i <= metaPrev2.getColumnCount(); i++) {
                        if (metaPrev2.getColumnName(i).equalsIgnoreCase("numero")) {
                            sqlC += "numero";
                            sqlV += Db.pc(newNumero, metaPrev2.getColumnType(i));
                        } else if (metaPrev2.getColumnName(i).equalsIgnoreCase("anno")) {
                            sqlC += "anno";
                            sqlV += Db.pc(java.util.Calendar.getInstance().get(Calendar.YEAR), "LONG");
                        } else if (metaPrev2.getColumnName(i).equalsIgnoreCase("id_padre")) {
                            sqlC = sqlC + "id_padre";
                            sqlV = sqlV + Db.pc(id, "LONG");
                        } else {
                            if (colonne_da_ignorare.contains(metaPrev2.getColumnName(i))) {
                                continue;
                            }
                            sqlC += metaPrev2.getColumnName(i);
                            sqlV += Db.pc(tempPrev2.getObject(i), metaPrev2.getColumnType(i));
                        }
                        sqlC += ",";
                        sqlV += ",";
                    }
                    sqlC = StringUtils.chop(sqlC);
                    sqlV = StringUtils.chop(sqlV);
                    if (sqlC.endsWith(",")) {
                        sqlC = StringUtils.chop(sqlC);
                    }
                    if (sqlV.endsWith(",")) {
                        sqlV = StringUtils.chop(sqlV);
                    }                    
                    sql = "insert into " + getNomeTabr() + " ";
                    sql += "(" + sqlC + ") values (" + sqlV + ")";
                    Db.executeSql(sql);
                }
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }
    dbRefresh();

    //aprire il nuovo
    //cerco il dbId
    for (int row = 0; row < griglia.getRowCount(); row++) {
        if (CastUtils.toInteger0(griglia.getValueAt(row, griglia.getColumnByName("id"))) == id) {
            griglia.getSelectionModel().setSelectionInterval(row, row);
            griglia.scrollToRow(row);
            butModiActionPerformed(null);
            break;
        }
    }

    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_butDuplicaActionPerformed

private void texClienteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texClienteKeyReleased
    filtraPerCliente();
}//GEN-LAST:event_texClienteKeyReleased

private void menEsportaExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menEsportaExcelActionPerformed

    SwingUtils.mouse_wait();

    File fdDir = new File(System.getProperty("user.home") + File.separator + "Invoicex");
    fdDir.mkdir();

    int[] selected = griglia.getSelectedRows();
    for (int isel : selected) {
        esportaDDTinExcel(fdDir, isel);
    }

//    SwingUtils.open(fdDir);
    Util.start2(fdDir.toString());

    SwingUtils.mouse_def();

}//GEN-LAST:event_menEsportaExcelActionPerformed

private void menExportCsvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menExportCsvActionPerformed
    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    if (griglia.getSelectedRowCount() < 0) {
        JOptionPane.showMessageDialog(this, "Seleziona almeno una riga da esportare", "Errore Selezione", JOptionPane.INFORMATION_MESSAGE);
        return;
    } else {
        int[] ids = new int[griglia.getSelectedRowCount()];
        int i = 0;

        int first = griglia.getSelectedRow();
        String serie = String.valueOf(griglia.getValueAt(first, griglia.getColumnByName("serie")));
        String numero = String.valueOf(griglia.getValueAt(first, griglia.getColumnByName("numero")));
        String nomeCliente = String.valueOf(griglia.getValueAt(first, griglia.getColumnByName(acquisto ? "Fornitore" : "Cliente")));
        String nomeFile = "documento_" + Db.getDescTipoDoc(acquisto ? Db.TIPO_DOCUMENTO_DDT_ACQUISTO : Db.TIPO_DOCUMENTO_DDT) + "_" + serie + numero + "_" + nomeCliente;
        nomeFile = FileUtils.normalizeFileName(nomeFile);

        String input = JOptionPane.showInputDialog(this, "Inserisci il nome con cui vuoi salvare il file: ", nomeFile);

        if (input != null) {
            if (!input.equals("")) {
                nomeFile = FileUtils.normalizeFileNameDir(input);

                for (int rigaSel : griglia.getSelectedRows()) {
                    int id = Integer.parseInt(String.valueOf(griglia.getValueAt(rigaSel, griglia.getColumnByName("id"))));
                    ids[i] = id;
                    i++;
                }
            } else {
                int res = JOptionPane.showConfirmDialog(this, "Non puoi inserire un nome vuoto per il file. Continuare con il nome standard?", "Errore inserimento", JOptionPane.YES_NO_OPTION);
                if (res == JOptionPane.NO_OPTION) {
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    return;
                }
            }
            InvoicexUtil.exportCSV(acquisto ? Db.TIPO_DOCUMENTO_DDT_ACQUISTO : Db.TIPO_DOCUMENTO_DDT, ids, nomeFile);
        }
    }
    this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_menExportCsvActionPerformed

private void menDuplicaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menDuplicaActionPerformed
    butDuplicaActionPerformed(null);
}//GEN-LAST:event_menDuplicaActionPerformed

private void radVenditaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radVenditaActionPerformed
    setAcquisto(false);
}//GEN-LAST:event_radVenditaActionPerformed

private void radAcquistoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radAcquistoActionPerformed
    setAcquisto(true);
}//GEN-LAST:event_radAcquistoActionPerformed

private void menAzzeraConvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menAzzeraConvActionPerformed
    int id = CastUtils.toInteger(griglia.getValueAt(griglia.getSelectedRow(), griglia.getColumnByName("id")));
    try {
        DbUtils.tryExecQuery(Db.getConn(), "update " + getNomeTab() + " set fattura_serie = null, fattura_numero = null, fattura_anno = null, convertito = null where id = " + id);
    } catch (Exception e) {
        e.printStackTrace();
    }

    dbRefresh();
}//GEN-LAST:event_menAzzeraConvActionPerformed

private void menCalcEvasoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menCalcEvasoActionPerformed
    int id = CastUtils.toInteger(griglia.getValueAt(griglia.getSelectedRow(), griglia.getColumnByName("id")));
    InvoicexUtil.aggiornaStatoEvasione(acquisto ? Db.TIPO_DOCUMENTO_DDT_ACQUISTO : Db.TIPO_DOCUMENTO_DDT, id);
    dbRefresh();
}//GEN-LAST:event_menCalcEvasoActionPerformed

private void menColAggRiferimentoClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menColAggRiferimentoClienteActionPerformed
    System.out.println("ColAgg_RiferimentoCliente = " + menColAggRiferimentoCliente.isSelected());
    main.fileIni.setValue("pref", "ColAgg_RiferimentoCliente", menColAggRiferimentoCliente.isSelected());
    dbRefresh();
}//GEN-LAST:event_menColAggRiferimentoClienteActionPerformed

private void menColAggCausaleTrasportoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menColAggCausaleTrasportoActionPerformed
    System.out.println("ColAgg_Causaletrasporto = " + menColAggCausaleTrasporto.isSelected());
    main.fileIni.setValue("pref", "ColAgg_Causaletrasporto", menColAggCausaleTrasporto.isSelected());
    dbRefresh();
}//GEN-LAST:event_menColAggCausaleTrasportoActionPerformed

private void comCausaleTrasportoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comCausaleTrasportoActionPerformed
    if (comCausaleTrasporto.getSelectedItem().equals("")) {
        sqlWhereCausale = "";
    } else {
        sqlWhereCausale = " and t.causale_trasporto = '" + String.valueOf(comCausaleTrasporto.getSelectedItem()) + "'";
    }

    dbRefresh();
}//GEN-LAST:event_comCausaleTrasportoActionPerformed

private void menColAggRifActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menColAggRifActionPerformed
    System.out.println("ColAgg_Rif = " + menColAggRif.isSelected());
    main.fileIni.setValue("pref", "ColAgg_Rif", menColAggRif.isSelected());
    dbRefresh();
}//GEN-LAST:event_menColAggRifActionPerformed

private void butConvNotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butConvNotaActionPerformed
    if (griglia.getSelectedRowCount() <= 0) {
        SwingUtils.showErrorMessage(this, "Seleziona un documento prima!");
        return;
    }

    Vector tempElencoDdt = new Vector();
    Vector tempElencoDdtR = new Vector(); //per DDT da raggruppare
    String tempWhereDdt = "";
    int[] righeSelezionate = this.griglia.getSelectedRows();
    String cliente = "";
    this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

    if (this.griglia.getSelectedRowCount() < 1) {
        JOptionPane.showMessageDialog(this, "Si deve selezionare almeno un D.D.T.");
    } else {

        //controllo che abbia preso lo stesso cliente
        cliente = Db.nz(griglia.getValueAt(righeSelezionate[0], griglia.getColumnByName(acquisto ? "Fornitore" : "Cliente")), "").toString();

        for (int u = 0; u < this.griglia.getSelectedRowCount(); u++) {

            if (!Db.nz(griglia.getValueAt(righeSelezionate[u], griglia.getColumnByName(acquisto ? "Fornitore" : "Cliente")), "").toString().equals(cliente)) {
                JOptionPane.showMessageDialog(this, "Si deve selezionare i D.D.T di un solo cliente per volta");
                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

                return;
            }
        }

        //controllo già convertiti
        boolean showed = false;
        for (int u = 0; u < this.griglia.getSelectedRowCount(); u++) {
            if (Db.nz(griglia.getValueAt(righeSelezionate[u], 3), "").toString().length() > 0 && !showed) {
                int ret = JOptionPane.showConfirmDialog(this, "Ci sono uno o piu' D.D.T. gia' fatturati nella selezione, continuare lo stesso ?", "Attenzione", javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE);
                showed = true;
                if (ret == javax.swing.JOptionPane.NO_OPTION) {
                    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    return;
                }
            }
        }

        //come convertire ?
        boolean raggr = false;
        boolean raggr_riepilogo = false;
        if (griglia.getSelectedRowCount() > 1) {
            try {
                int raggruppa_articoli = Integer.parseInt(main.fileIni.getValue("pref", "raggruppa_articoli", "2"));
                if (raggruppa_articoli == 4) {
                    //raggruppa riepilogando una riga per ogni ddt
                    raggr_riepilogo = true;
                } else {
                    String sqlcliente = "select c.opzione_raggruppa_ddt from " + getNomeTab() + " t join clie_forn c on t.cliente = c.codice "
                            + " where t.serie = '" + griglia.getValueAt(griglia.getSelectedRow(), 0) + "'"
                            + " and t.numero = " + griglia.getValueAt(griglia.getSelectedRow(), 1)
                            + " and t.anno = " + griglia.getValueAt(griglia.getSelectedRow(), 2);
                    if (raggruppa_articoli == 0) {//no
                        raggr = false;
                    } else if (raggruppa_articoli == 1) {//Raggruppa sempre
                        raggr = true;
                    } else if (raggruppa_articoli == 2) {//Chiedi sempre (suggerendo dal Cliente)
                        raggr = false;
                        String opz = "N";
                        try {
                            opz = (String) DbUtils.getObject(Db.conn, sqlcliente);
                        } catch (Exception ex2) {
                            ex2.printStackTrace();
                        }
                        JDialogRaggruppaArticoli draggr = new JDialogRaggruppaArticoli(main.getPadre(), true);
                        if ("S".equalsIgnoreCase(opz)) {
                            raggr = true;
                            draggr.labcliente.setText("Il Cliente del documento preferisce avere gli articoli raggruppati");
                            draggr.raggruppa.setSelected(true);
                        } else {
                            draggr.labcliente.setText("");
                            draggr.raggruppa.setSelected(false);
                        }
                        draggr.setLocationRelativeTo(null);
                        draggr.setVisible(true);
                        if (!draggr.prosegui) {
                            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                            return;
                        }
                        if (draggr.raggruppa.isSelected()) {
                            raggr = true;
                        } else {
                            raggr = false;
                        }
                    } else if (raggruppa_articoli == 3) {//Raggruppa in base al Cliente
                        raggr = false;
                        String opz = "N";
                        try {
                            opz = (String) DbUtils.getObject(Db.conn, sqlcliente);
                        } catch (Exception ex2) {
                            ex2.printStackTrace();
                        }
                        if ("S".equalsIgnoreCase(opz)) {
                            raggr = true;
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        //genero la nuova fatture
        dbDocumento prev = new dbDocumento();
        prev.acquisto = acquisto;

        ArrayList elenco1 = new ArrayList();
        for (int i = 0; i < this.griglia.getSelectedRowCount(); i++) {
            Object[] row = new Object[3];
            row[0] = griglia.getValueAt(righeSelezionate[i], 0);    //serie
            row[1] = Integer.parseInt(griglia.getValueAt(righeSelezionate[i], 1).toString());    //numero
            row[2] = Integer.parseInt(griglia.getValueAt(righeSelezionate[i], 2).toString());    //anno
            elenco1.add(row);
        }
        Collections.sort(elenco1, new Comparator() {
            public int compare(Object o1, Object o2) {
                Object[] row1 = (Object[]) o1;
                Object[] row2 = (Object[]) o2;
                if (row1[0].toString().equals(row2[0].toString())) {
                    if (((Integer) row1[2]).equals((Integer) row2[2])) {
                        return ((Integer) row1[1]).compareTo((Integer) row2[1]);
                    } else {
                        return ((Integer) row1[2]).compareTo((Integer) row2[2]);
                    }
                } else {
                    return row1[0].toString().compareTo(row2[0].toString());
                }
            }
        });

        for (int i = 0; i < elenco1.size(); i++) {
            Object[] row = (Object[]) elenco1.get(i);
            tempWhereDdt = " t.serie = " + Db.pc(row[0].toString(), "VARCHAR");
            tempWhereDdt = tempWhereDdt + " and t.numero = " + Db.pc(row[1].toString(), "NUMBER");
            tempWhereDdt = tempWhereDdt + " and t.anno = " + Db.pc(row[2].toString(), Types.VARCHAR);

            //aggiungo al vettore
            tempElencoDdt.add(" where (" + tempWhereDdt + " )");
            tempElencoDdtR.add(" (" + tempWhereDdt + " ) ");
        }

        prev.serie = griglia.getValueAt(righeSelezionate[0], 0).toString();
        prev.elencoDdt = tempElencoDdt;
        prev.elencoDdtR = tempElencoDdtR;
        prev.ids = getIds();

        String optRiportaSerie = main.fileIni.getValue("pref", "riporta_serie", "0");
        int repSerie = javax.swing.JOptionPane.YES_OPTION;
        if (optRiportaSerie.equals("0")) {
            if (!prev.serie.equals("")) {
                repSerie = JOptionPane.showConfirmDialog(this, "Vuoi riportare la serie del DDT in Fattura?", "Riporta serie", javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE);
            }
        } else if (optRiportaSerie.equals("2")) {
            repSerie = javax.swing.JOptionPane.NO_OPTION;
        }

        String ret = prev.convertiInFattura(raggr, raggr_riepilogo, false, true);
        if (ret != null) {
            JOptionPane.showMessageDialog(this, "La nuova nota di credito e' la " + ret);
        } else {
            SwingUtils.showInfoMessage(this, "Conversione annullata");
        }
    }

    //aggiorno la tabella
    dbRefresh();
    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_butConvNotaActionPerformed

private void menConvNotaDiCreditoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menConvNotaDiCreditoActionPerformed
    butConvNotaActionPerformed(null);
}//GEN-LAST:event_menConvNotaDiCreditoActionPerformed

    private void menColoraRossoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menColoraRossoActionPerformed
        InvoicexUtil.salvaColoreRiga("rosso", getNomeTab(), griglia);
    }//GEN-LAST:event_menColoraRossoActionPerformed

    private void menColoraBluActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menColoraBluActionPerformed
        InvoicexUtil.salvaColoreRiga("blu", getNomeTab(), griglia);
    }//GEN-LAST:event_menColoraBluActionPerformed

    private void menColoraGialloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menColoraGialloActionPerformed
        InvoicexUtil.salvaColoreRiga("giallo", getNomeTab(), griglia);
    }//GEN-LAST:event_menColoraGialloActionPerformed

    private void menTogliColoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menTogliColoreActionPerformed
        InvoicexUtil.salvaColoreRiga("", getNomeTab(), griglia);
    }//GEN-LAST:event_menTogliColoreActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butConv;
    private javax.swing.JButton butConvNota;
    private javax.swing.JButton butDele;
    private javax.swing.JButton butDuplica;
    private javax.swing.JButton butEmail;
    private javax.swing.JButton butModi;
    private javax.swing.JButton butNew;
    private javax.swing.JButton butPrin;
    private javax.swing.JButton butRefresh;
    private javax.swing.JButton butStampaConfermaOrdine;
    private javax.swing.ButtonGroup buttonGroup1;
    private tnxbeans.tnxComboField comCausaleTrasporto;
    public tnxbeans.tnxComboField comCliente;
    public tnxbeans.tnxDbGrid griglia;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    public javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    public javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labTotale;
    private javax.swing.JMenuItem menAzzeraConv;
    private javax.swing.JMenuItem menCalcEvaso;
    private javax.swing.JMenu menColAgg;
    private javax.swing.JCheckBoxMenuItem menColAggCausaleTrasporto;
    private javax.swing.JCheckBoxMenuItem menColAggRif;
    private javax.swing.JCheckBoxMenuItem menColAggRiferimentoCliente;
    private javax.swing.JMenuItem menColoraBlu;
    private javax.swing.JMenuItem menColoraGiallo;
    private javax.swing.JMenu menColoraRiga;
    private javax.swing.JMenuItem menColoraRosso;
    private javax.swing.JMenuItem menConvFattura;
    private javax.swing.JMenuItem menConvNotaDiCredito;
    private javax.swing.JMenuItem menDuplica;
    private javax.swing.JMenuItem menElimina;
    private javax.swing.JMenuItem menEsportaExcel;
    private javax.swing.JMenuItem menExportCsv;
    private javax.swing.JMenuItem menModifica;
    private javax.swing.JMenuItem menPdfEmail;
    private javax.swing.JMenuItem menStampa;
    private javax.swing.JMenuItem menTogliColore;
    private javax.swing.JPanel panDati;
    public javax.swing.JPopupMenu popupElenco;
    private javax.swing.JRadioButton radAcquisto;
    private javax.swing.JRadioButton radVendita;
    private javax.swing.JPopupMenu.Separator sep1;
    private javax.swing.JTextField texAl;
    public javax.swing.JTextField texCliente;
    private javax.swing.JTextField texDal;
    private javax.swing.JTextField texLimit;
    // End of variables declaration//GEN-END:variables

    public void dbRefresh() {
        java.util.Hashtable colsWidthPerc = new java.util.Hashtable();


        colsWidthPerc.put("Serie", new Double(5));
        colsWidthPerc.put("Numero", new Double(5));
        colsWidthPerc.put("Anno", new Double(0));
        colsWidthPerc.put("Fatturato", new Double(5));
        colsWidthPerc.put("convertito2", new Double(0));
        colsWidthPerc.put("Data", new Double(12));

        if (acquisto) {
            colsWidthPerc.put("Fornitore", new Double(25));
        } else {
            colsWidthPerc.put("Cliente", new Double(25));
        }
        colsWidthPerc.put("Note", new Double(20));
        colsWidthPerc.put("Totale Imponibile", new Double(15));

        colsWidthPerc.put("ID", new Double(0));
        colsWidthPerc.put("Fatturazione", new Double(5));
        if (main.fileIni.getValueBoolean("pref", "ColAgg_RiferimentoCliente", false)) {
            colsWidthPerc.put("Riferimento Cliente", new Double(15));
        }
        if (main.fileIni.getValueBoolean("pref", "ColAgg_Rif", false)) {
            colsWidthPerc.put("Riferimento", new Double(15));
        }
        if (main.fileIni.getValueBoolean("pref", "ColAgg_Causaletrasporto", false)) {
            colsWidthPerc.put("Causale Trasporto", new Double(15));
        }
        if (main.pluginEmail) {
            colsWidthPerc.put("Mail Inviata", new Double(10));
        }
        colsWidthPerc.put("color", new Double(0));

        this.griglia.columnsSizePerc = colsWidthPerc;

        int oldsel = griglia.getSelectedRow();
        int oldselid = -1;
        if (oldsel >= 0) {
            try {
                oldselid = CastUtils.toInteger(griglia.getValueAt(oldsel, griglia.getColumnByName("id")));
            } catch (Exception e) {
            }
        }

        String sql;
        sql = "select ";
        sql += " t.serie AS 'Serie', ";
        sql += " t.numero AS 'Numero', ";
        sql += " t.anno AS 'Anno', ";
        sql += " t.fattura_numero AS 'Fatturato', ";
        sql += " t.convertito AS convertito2, ";
        sql += " t.data AS 'Data' ,";

        if (acquisto) {
            sql += " clie_forn.ragione_sociale As Fornitore, ";
        } else {
            sql += " clie_forn.ragione_sociale As Cliente, ";
        }

        sql += " t.note AS Note, ";
        sql += " t.totale_imponibile AS 'Totale Imponibile',";
        sql += " t.id AS 'ID' ";
        sql += " , t.evaso as Fatturazione ";
        if (main.fileIni.getValueBoolean("pref", "ColAgg_RiferimentoCliente", false)) {
            sql += " , clie_forn.persona_riferimento as 'Riferimento Cliente'";
        }
        if (main.fileIni.getValueBoolean("pref", "ColAgg_Rif", false)) {
            sql += " , t.riferimento as 'Riferimento'";
        }
        if (main.fileIni.getValueBoolean("pref", "ColAgg_Causaletrasporto", false)) {
            sql += " , t.causale_trasporto as 'Causale Trasporto'";
        }
        if (main.pluginEmail) {
            sql += " , t.mail_inviata as 'Mail Inviata'";
        }
        sql += " , color ";

        sql += " from " + getNomeTab() + " t left join clie_forn on";
        if (acquisto) {
            sql += " t.fornitore = clie_forn.codice";
        } else {
            sql += " t.cliente = clie_forn.codice";
        }
        sql += " where 1 = 1 ";
        sql += sqlWhereDaData;
        sql += sqlWhereAData;
        sql += sqlWhereCliente;
        sql += sqlWhereCausale;
        sql += sqlWhereTipo;
        if (sqlOrder != null) {
            sql += sqlOrder;
        } else {
            sql += " order by t.data desc, t.numero desc";
        }
        sql += sqlWhereLimit;

        System.out.println("sql: " + sql);
        this.griglia.dbOpen(Db.getConn(), sql, Db.INSTANCE);

        if (main.pluginEmail) {
            try {
                griglia.getColumn("Mail Inviata").setCellRenderer(new frmElenFatt.EmailCellRenderer());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //calcolo il totale
        if (this.visualizzaTotali == true) {

            double totale = 0;
            int totaleDocumenti = 0;
            int contaDocumenti = 0;
            ResultSet somma = Db.openResultSet(sql);
            ResultSet rtota = Db.openResultSet("select count(*) from " + getNomeTab() + "");

            try {

                while (somma.next()) {
                    totale += somma.getDouble("totale imponibile");
                    contaDocumenti++;
                }

                if (rtota.next()) {
                    totaleDocumenti = rtota.getInt(1);
                }

                this.labTotale.setText("documenti visualizzati " + contaDocumenti + " di " + totaleDocumenti + " / totale documenti visualizzati \u20ac " + it.tnx.Util.formatValutaEuro(totale) + " ");
            } catch (Exception err) {
                err.printStackTrace();
                this.labTotale.setText("");
            }
        } else {
            this.labTotale.setText("");
        }

        //metto render info fatturazione
        griglia.getColumn("Fatturato").setCellRenderer(flagRender);
        griglia.getColumn("Fatturazione").setCellRenderer(evasoRender);
        griglia.getTableHeader().setReorderingAllowed(false);

        try {
            if (oldsel != -1) {
                //riseleziono
                int colid = griglia.getColumnByName("id");
                for (int i = 0; i < griglia.getRowCount(); i++) {
                    if (CastUtils.toInteger(griglia.getValueAt(i, colid)) == oldselid) {
                        griglia.getSelectionModel().setSelectionInterval(i, i);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    static public void stampa(final String tipoFattura, final String dbSerie, final int dbNumero, final int dbAnno, final boolean acquisto, final Integer id) {
        stampa(tipoFattura, dbSerie, dbNumero, dbAnno, false, false, false, acquisto, id);
    }

//    static public Object stampa(final String tipoFattura, final String dbSerie, final int dbNumero, final int dbAnno, final boolean generazionePdfDaJasper, final boolean attendi, final boolean booleanPerEmail, boolean acquisto) {
//        return stampa(tipoFattura, dbSerie, dbNumero, dbAnno, generazionePdfDaJasper, attendi, booleanPerEmail, acquisto, null);
//    }

    static public Object stampa(final String tipoFattura, final String dbSerie, final int dbNumero, final int dbAnno, final boolean generazionePdfDaJasper, final boolean attendi, final boolean booleanPerEmail, boolean acquisto, final Integer id) {
        Object ret = null;

        if (main.getPersonalContain(main.PERSONAL_LUXURY)) {
            main.luxStampaNera = false;
            main.luxStampaValuta = "?";
            main.luxProforma = false;
        }

        //tipoFattura
        String paramTipoStampa = "tipoStampaDDT";

        String tempts = "";
        if (!StringUtils.isBlank(main.fileIni.getValue("stampe", paramTipoStampa))) {
            tempts = main.fileIni.getValue("stampe", paramTipoStampa);
        } else {
            tempts = main.fileIni.getValue("pref", paramTipoStampa, "");
        }
        final String prefTipoStampa = tempts;

        //salvo img logo in db perchè le stampe la caricono da db invece che da file per integrazione con client manager
//        InvoicexUtil.salvaLogoInDb(main.fileIni.getValue("varie", "percorso_logo_stampe"));

        //nuovo tipo di stampa
        if (prefTipoStampa.endsWith(".jrxml")) {
            final boolean f_acquisto = acquisto;
            SwingWorker work = new SwingWorker() {
                public Object construct() {
                    final JDialog dialog = new JDialogCompilazioneReport();
                    Object ret = null;
                    try {
                        dialog.setLocationRelativeTo(null);
                        dialog.setVisible(true);

                        File freport = new File(main.wd + Reports.DIR_REPORTS + Reports.DIR_FATTURE + prefTipoStampa);
                        
                        JasperReport rep = Reports.getReport(freport);

                        //controllo modifiche al report

                        //prezzi
                        String suffisso = "";
                        try {
                            if (!main.getPersonalContain("noCheckPrezziStampa")) {
                                if (!controllaStamparePrezzi(dbSerie, dbNumero, dbAnno, f_acquisto)) {
                                    suffisso += "_noprezzi";
                                }
                            }
                        } catch (Exception e) {
                        }

                        //sconti
                        try {
                            if (!main.getPersonalContain("noCheckScontiStampa")) {
                                if (!controllaScontoPresente(dbSerie, dbNumero, dbAnno)) {
                                    suffisso += "_nosconto";
                                }
                            }
                        } catch (Exception e) {
                        }

                        //logo
                        if (InvoicexUtil.controllaPosizioneLogoSuffisso().length() > 0) {
                            suffisso += InvoicexUtil.controllaPosizioneLogoSuffisso();
                        }
                        //colonna prezzo consigliato
                        if (!main.getPersonalContain("noCheckPrezzoConsigliato")) {
                            if (controllaStampareColonnaPrezzoConsigliato(dbSerie, dbNumero, dbAnno)) {
                                suffisso += "_listino_consigliato";
                            }
                        }
                        //luxury
                        if ((main.getPersonalContain(main.PERSONAL_LUXURY)) && (main.luxStampaNera)) {
                            suffisso += "_nera";
                        }
                        if (suffisso.length() == 0) {
                            //no elaborazioni
                            suffisso += ".jasper";
                        } else {
                            suffisso += "_gen_invoicex.jasper";
                        }

                        //controllo se già presente
                        String newFile = freport.getAbsolutePath() + suffisso;
                        File newFileFile = new File(newFile);
                        boolean ricompilare = true;
                        if (newFileFile.exists() && newFileFile.lastModified() >= freport.lastModified()) {
                            ricompilare = false;
                            rep = JasperManager.loadReport(newFile);
                        }

                        //DEBUG
//                        ricompilare = true;

                        JasperDesign repdes = JRXmlLoader.load(freport);

                        if (ricompilare) {

                            if (!main.getPersonalContain("noCheckPrezziStampa")) {
                                try {
                                    if (suffisso.indexOf("_noprezzi") >= 0) {
                                        repdes = controllaStamparePrezzi(freport, rep, repdes);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            try {
                                if ((!(main.getPersonalContain("noCheckScontiStampa")))
                                        && (!(controllaScontoPresente(dbSerie, dbNumero, dbAnno)))) {
                                    repdes = controllaSconto(freport, rep, repdes);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                repdes = InvoicexUtil.controllaLogo(freport, rep, repdes);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (!main.getPersonalContain("noCheckPrezzoConsigliato")) {
                                try {
                                    if (suffisso.indexOf("_listino_consigliato") >= 0) {
                                        repdes = controllaStampareColonnaPrezzoConsigliato(freport, rep, repdes);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            if ((main.getPersonalContain(main.PERSONAL_LUXURY)) && (main.luxStampaNera)) {
                                try {
                                    repdes = controllaStampaLuxury(freport, rep, repdes);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            JasperCompileManager.compileReportToFile(repdes, newFile);
                            rep = JasperManager.loadReport(newFile);

                        }

                        //eventuali personalizzazioni
                        try {
                            HashMap params = new HashMap();
                            params.put("source", this);
                            params.put("freport", freport);
                            params.put("rep", rep);
                            params.put("repdes", repdes);
                            InvoicexEvent event = new InvoicexEvent(params);
                            event.type = InvoicexEvent.TYPE_PREPARA_JASPER;
                            HashMap reth = (HashMap) main.events.fireInvoicexEventWResult(event);
                            if (reth != null) {
                                rep = (JasperReport) reth.get("rep");
                            }
                        } catch (Exception err) {
                            err.printStackTrace();
                        }

                        java.util.Map params = new java.util.HashMap();
                        JRDSDdt jrInvoice = null;
                        if (main.getPersonalContain(main.PERSONAL_LUXURY)) {
                            jrInvoice = new JRDSDdt_lux(Db.getConn(), dbSerie, dbNumero, dbAnno, booleanPerEmail);
                        } else {
                            jrInvoice = new JRDSDdt(Db.getConn(), dbSerie, dbNumero, dbAnno, booleanPerEmail, f_acquisto, id);
                        }

                        boolean italian = false;
                        if (main.fileIni.getValueBoolean("pref", "soloItaliano", true)) {
                            italian = true;
                        } else {
                            if (jrInvoice.codiceCliente != null) {
                                Cliente cliente = new Cliente(jrInvoice.codiceCliente);
                                italian = cliente.isItalian();
                            }
                        }

                        ResourceBundle rb = null;
                        if (italian) {
                            rb = ResourceBundle.getBundle("gestioneFatture/print/labels");
                            params.put("lang", "it");
                        } else {
                            rb = ResourceBundle.getBundle("gestioneFatture/print/labels", java.util.Locale.UK);
                            params.put("lang", "en");
                        }
                        for (Enumeration e = rb.getKeys(); e.hasMoreElements();) {
                            String k = (String) e.nextElement();
                            params.put("e_" + k, rb.getString(k));
                        }

                        params.put("myds", jrInvoice);
                        try {
                            Object oprezzi = DbUtils.getObject(Db.conn, "select opzione_prezzi_ddt from " + getNomeTab(f_acquisto) + " "
                                    + " where serie = '" + dbSerie + "'"
                                    + " and numero = " + dbNumero
                                    + " and anno = " + dbAnno);
                            if (oprezzi.toString().equalsIgnoreCase("S")) {
                                params.put("stampa_prezzi", new Boolean(true));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        params.put("stampaPivaSotto", main.fileIni.getValueBoolean("pref", "stampaPivaSotto", false));

                        JasperPrint print = JasperManager.fillReport(rep, params, jrInvoice);

//                        java.util.prefs.Preferences preferences = java.util.prefs.Preferences.userNodeForPackage(main.class);

                        if (generazionePdfDaJasper) {
                            //File fd = new File("tempEmail/documento_" + tipoFattura + "_" + dbSerie + String.valueOf(dbNumero) + "_" + jrInvoice.nomeClienteFile + ".pdf");
                            String nomeFile = "documento_DDT_" + dbSerie + String.valueOf(dbNumero) + "_" + jrInvoice.nomeClienteFile + ".pdf";
                            nomeFile = "tempEmail/" + FileUtils.normalizeFileName(nomeFile);
                            File fd = new File(main.wd + nomeFile);
                            String nomeFilePdf = fd.getAbsolutePath();
                            JasperExportManager.exportReportToPdfFile(print, nomeFilePdf);
                            ret = nomeFilePdf;
                        } else {
//                            if (preferences.getBoolean("stampaPdf", false)) {
                            if (main.fileIni.getValueBoolean("pref", "stampaPdf", false)) {
                                String nomeFilePdf = main.wd + "tempPrnDdt.pdf";
                                JasperExportManager.exportReportToPdfFile(print, nomeFilePdf);
//                                SwingUtils.open(new File(nomeFilePdf));
                                Util.start2(nomeFilePdf);
                            } else {
                                final JasperPrint printer = print;
                                Thread t = new Thread(new Runnable() {
                                    public void run() {
                                        JDialogJasperViewer viewr = new JDialogJasperViewer(main.getPadre(), true, printer);
                                        viewr.setTitle("Anteprima di stampa");
                                        viewr.setLocationRelativeTo(null);
                                        viewr.setVisible(true);
                                    }
                                });

                                t.start();
                            }
                        }

                    } catch (JRException jrerr) {
                        JOptionPane.showMessageDialog(main.getPadre(), jrerr.toString(), "Errore", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        dialog.setVisible(false);
                    }

//                    main.getPadre().toFront();

                    return ret;
                }

                private JasperDesign controllaSconto(File freport, JasperReport rep, JasperDesign repdes) throws JRException {
                    JRDesignBand details = (JRDesignBand) repdes.getDetail();

                    int xsconti = 0;
                    int xdescrizione = 0;
                    int wsconti = 0;

                    for (JRElement el : details.getElements()) {
                        JRTextField tf;
                        if (el instanceof JRTextField) {
                            tf = (JRTextField) el;
                            if (tf.getExpression().getText().equalsIgnoreCase("$F{sconti}")) {
                                xsconti = tf.getX();
                                wsconti = tf.getWidth();
                                details.removeElement((JRDesignElement) tf);
                                break;
                            }
                        } else if (el instanceof JRDesignFrame) {
                            JRDesignFrame frame = (JRDesignFrame) el;
                            for (JRElement el2 : frame.getElements()) {
                                if (el2 instanceof JRTextField) {
                                    tf = (JRTextField) el2;
                                    if (tf.getExpression().getText().equalsIgnoreCase("$F{sconti}")) {
                                        xsconti = tf.getX();
                                        wsconti = tf.getWidth();
                                        frame.removeElement((JRDesignElement) tf);
                                        break;
                                    }
                                }
                            }
                        }
                    }

//                    for (JRElement el : details.getElements()) {
//                        if (el instanceof JRLine) {
//                            JRLine l = (JRLine) el;
//                            System.out.println("l id:" + l.getKey());
//                            if (l.getX() > xsconti) {
//                                details.removeElement((JRDesignElement) l);
//                                break;
//                            }
//                        }
//                    }
                    try {
                        JRElement tmp = details.getElementByKey("l_importo");
                        details.removeElement((JRDesignElement) tmp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        JRDesignFrame frame = (JRDesignFrame) details.getElementByKey("frameStandard");
                        JRElement tmp = frame.getElementByKey("l_importo");
                        if (tmp == null) {
                            //unifish non ha casella importo
                            try {
                                tmp = frame.getElementByKey("l_sconto");
                                frame.removeElement((JRDesignElement) tmp);
                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                        } else {
                            frame.removeElement((JRDesignElement) tmp);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        //per mod4
                        JRDesignFrame tmp = (JRDesignFrame) details.getElementByKey("rd_con_importi");
                        tmp.removeElement(tmp.getElementByKey("l_importo"));
                    } catch (Exception e) {
                    }

                    for (JRElement el : details.getElements()) {
                        JRDesignBand header;
                        JRDesignFrame frame;
                        if (el instanceof JRTextField) {
                            JRTextField tf = (JRTextField) el;
                            if (tf.getExpression().getText().equalsIgnoreCase("$F{descrizione}")) {
                                xdescrizione = tf.getX();
                                tf.setWidth(tf.getWidth() + wsconti);
                                break;
                            }
                        } else if (el instanceof JRDesignFrame) {
                            JRDesignFrame frm = (JRDesignFrame) el;
                            if (frm.getKey().equals("frameStandard")) {
                                for (JRElement el2 : frm.getElements()) {
                                    if (el2 instanceof JRTextField) {
                                        JRTextField tf = (JRTextField) el2;
                                        if (tf.getExpression().getText().equalsIgnoreCase("$F{descrizione}")) {
                                            xdescrizione = tf.getX();
                                            tf.setWidth(tf.getWidth() + wsconti);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    for (JRElement el : details.getElements()) {
                        if (el instanceof JRDesignElement) {
                            JRDesignElement de = (JRDesignElement) el;
                            if ((de.getX() > xdescrizione) && (de.getX() < xsconti)) {
                                de.setX(de.getX() + wsconti);
                            }
                        }
                        if (el instanceof JRDesignFrame) {
                            JRDesignFrame frm = (JRDesignFrame) el;
                            for (JRElement el2 : frm.getElements()) {
                                if (el2 instanceof JRDesignElement) {
                                    JRDesignElement de = (JRDesignElement) el2;
                                    if ((de.getX() > xdescrizione) && (de.getX() < xsconti)) {
                                        de.setX(de.getX() + wsconti);
                                    }
                                }
                            }
                        }
                    }

                    JRDesignBand header = (JRDesignBand) repdes.getPageHeader();

                    for (JRElement el : header.getElements()) {
                        if (el instanceof JRDesignStaticText) {
                            JRDesignStaticText st = (JRDesignStaticText) el;
                            if ((st.getText().equalsIgnoreCase("Sconti")) || (st.getText().equalsIgnoreCase("Sconto")) || st.getText().equalsIgnoreCase("Sc. %")) {
                                xsconti = st.getX();
                                header.removeElement(st);
                                break;
                            }
                        }
                        if (el instanceof JRTextField) {
                            JRTextField tf = (JRTextField) el;
                            System.out.println("campo: " + tf.getExpression().getText());
                            if (tf.getExpression().getText().equalsIgnoreCase("$P{e_Sconti}")) {
                                xsconti = tf.getX();
                                header.removeElement((JRDesignElement) tf);
                            }
                        }
                        if (el instanceof JRDesignFrame) {
                            JRDesignFrame frame = (JRDesignFrame) el;
                            for (JRElement el2 : frame.getElements()) {
                                if (el2 instanceof JRDesignStaticText) {
                                    JRDesignStaticText st = (JRDesignStaticText) el2;
                                    if ((st.getText().equalsIgnoreCase("Sconti")) || (st.getText().equalsIgnoreCase("Sconto")) || st.getText().equalsIgnoreCase("Sc. %")) {
                                        xsconti = st.getX();
                                        frame.removeElement(st);
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    for (JRElement el : header.getElements()) {
                        if (el instanceof JRTextField) {
                            JRTextField de = (JRTextField) el;
                            System.out.println(de.getExpression().getText() + " x:" + de.getX() + "> xdescrizione:" + xdescrizione + " < xsconti:" + xsconti + " y:" + de.getY() + " > " + (header.getHeight() - 30));
                            if ((de.getX() > xdescrizione) && (de.getX() < xsconti) && (de.getY() > header.getHeight() - 30)) {
                                de.setX(de.getX() + wsconti);
                            }
                        }
                        if (el instanceof JRDesignStaticText) {
                            JRDesignStaticText de = (JRDesignStaticText) el;
                            System.out.println(de.getText() + " x:" + de.getX() + "> xdescrizione:" + xdescrizione + " < xsconti:" + xsconti + " y:" + de.getY() + " > " + (header.getHeight() - 30));
                            if ((de.getX() > xdescrizione) && (de.getX() < xsconti) && (de.getY() > header.getHeight() - 30)) {
                                de.setX(de.getX() + wsconti);
                            }
                        }
                        
                        if (el instanceof JRDesignFrame) {
                            JRDesignFrame frame = (JRDesignFrame) el;
                            System.out.println("frame:" + frame.getKey());
                            for (JRElement el2 : frame.getElements()) {
                                if (el2 instanceof JRDesignStaticText) {
                                    JRDesignStaticText de = (JRDesignStaticText) el2;
                                    System.out.println(de.getText() + " x:" + de.getX() + "> xdescrizione:" + xdescrizione + " < xsconti:" + xsconti + " y:" + de.getY() + " > " + (header.getHeight() - 30));
                                    if ((de.getX() > xdescrizione) && (de.getX() < xsconti) && (frame.getY() > header.getHeight() - 30)) {
                                        de.setX(de.getX() + wsconti);
                                    }
                                }
                            }
                        }
                    }

                    return repdes;
                }

                private boolean controllaScontoPresente(String dbSerie, int dbNumero, int dbAnno) throws SQLException {
                    String sql = "";
                    sql = sql + "select count(*) from " + getNomeTabr(f_acquisto) + " where serie = " + Db.pc(dbSerie, 12);
                    sql = sql + " and numero = " + dbNumero;
                    sql = sql + " and anno = " + dbAnno;
                    sql = sql + " and ((sconto1 is not null and sconto1 != 0)";
                    sql = sql + " or (sconto2 is not null and sconto2 != 0))";
                    ResultSet r = Db.openResultSet(sql);

                    return ((r.next()) && (r.getInt(1) > 0));
                }

                private boolean controllaStampareColonnaPrezzoConsigliato(String dbSerie, int dbNumero, int dbAnno) {
                    String sql = "";
                    sql = sql + "select listino_consigliato from " + getNomeTab(f_acquisto) + " where serie = " + Db.pc(dbSerie, 12);
                    sql = sql + " and numero = " + dbNumero;
                    sql = sql + " and anno = " + dbAnno;
                    try {
                        Object listino_consigliato = DbUtils.getObject(Db.getConn(), sql);
                        if (listino_consigliato == null || StringUtils.isEmpty(CastUtils.toString(listino_consigliato))) {
                            return false;
                        } else {
                            return true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }

                private JasperDesign controllaStampareColonnaPrezzoConsigliato(File freport, JasperReport rep, JasperDesign repdes) throws JRException {
                    int offx = 50;
                    int marginx = 2;

                    JRDesignBand detail = (JRDesignBand) repdes.getDetailSection().getBands()[0];
                    int spostarex = detail.getElementByKey("prezzo").getWidth() + (marginx * 2);

//                    JRDesignStaticText s1 = new JRDesignStaticText();
//                    s1.setText("aaa bbb ccc");
//                    s1.setX(50);
//                    s1.setY(50);
//                    s1.setWidth(70);
//                    s1.setHeight(25);
//                    ((JRDesignBand)repdes.getPageHeader()).addElement(s1);

                    //header

                    //sposto colonne precedenti
                    JRElement tmp = null;
                    tmp = repdes.getPageHeader().getElementByKey("i_um");
                    if (tmp != null) {
                        tmp.setX(tmp.getX() - spostarex);
                    }
                    tmp = repdes.getPageHeader().getElementByKey("i_qta");
                    if (tmp != null) {
                        tmp.setX(tmp.getX() - spostarex);
                    }

                    //aggiungo colonna prezzo consigliato
                    JRDesignStaticText s_prezzo_cons = new JRDesignStaticText();
                    s_prezzo_cons.setText("Prezzo\nConsigliato");
                    s_prezzo_cons.setX(tmp.getX() + tmp.getWidth() + (marginx * 3));
                    System.out.println("tmp.getY(): " + tmp.getY());
                    s_prezzo_cons.setY(tmp.getY() - 3);
                    tmp = repdes.getPageHeader().getElementByKey("i_prezzo");
                    s_prezzo_cons.setWidth(tmp.getWidth());
                    s_prezzo_cons.setHeight(tmp.getHeight() + 5);
                    s_prezzo_cons.setStyleNameReference("etichette_testa_1");
                    s_prezzo_cons.setFontSize(6);
                    s_prezzo_cons.setItalic(true);
                    s_prezzo_cons.setBold(true);
                    s_prezzo_cons.setForecolor(Color.darkGray);
                    s_prezzo_cons.setHorizontalAlignment(JRAlignment.HORIZONTAL_ALIGN_CENTER);

                    ((JRDesignBand) repdes.getPageHeader()).addElement(s_prezzo_cons);





                    //dettagli
                    tmp = detail.getElementByKey("descrizione");
                    if (tmp != null) {
                        tmp.setWidth(tmp.getWidth() - spostarex);
                    }
                    tmp = detail.getElementByKey("l_um");
                    if (tmp != null) {
                        tmp.setX(tmp.getX() - spostarex);
                    }
                    tmp = detail.getElementByKey("um");
                    if (tmp != null) {
                        tmp.setX(tmp.getX() - spostarex);
                    }
                    tmp = detail.getElementByKey("l_qta");
                    if (tmp != null) {
                        tmp.setX(tmp.getX() - spostarex);
                    }
                    tmp = detail.getElementByKey("qta");
                    if (tmp != null) {
                        tmp.setX(tmp.getX() - spostarex);
                    }
                    tmp = detail.getElementByKey("l_prezzo");
                    if (tmp != null) {
                        tmp.setX(tmp.getX() - spostarex);
                    }

                    //aggiungo prezzo cons
                    tmp = detail.getElementByKey("l_prezzo");
                    JRDesignTextField tf_prezzo_cons = new JRDesignTextField();
                    JRDesignExpression exp = new JRDesignExpression();
                    exp.setValueClass(String.class);
                    exp.setText("$F{s_prezzo_consigliato}");
                    tf_prezzo_cons.setExpression(exp);
                    System.out.println("s_prezzo_consigliato tmpgetX: " + tmp.getX());
                    System.out.println("s_prezzo_consigliato tmp.getWidth(): " + tmp.getWidth());
                    System.out.println("s_prezzo_consigliato marginx: " + marginx);
                    System.out.println("s_prezzo_consigliato x: " + (tmp.getX() + tmp.getWidth() + marginx));
                    tf_prezzo_cons.setX(tmp.getX() + tmp.getWidth() + marginx);
                    tmp = detail.getElementByKey("prezzo");
                    tf_prezzo_cons.setY(tmp.getY());
                    tmp = repdes.getPageHeader().getElementByKey("i_prezzo");
                    System.out.println("s_prezzo_consigliato w: " + tmp.getWidth());
                    tf_prezzo_cons.setWidth(tmp.getWidth());
                    tf_prezzo_cons.setHeight(tmp.getHeight());
                    tf_prezzo_cons.setStyleNameReference("variabili_corpo_1");
                    tf_prezzo_cons.setForecolor(Color.darkGray);
                    tf_prezzo_cons.setFontSize(8);
                    tf_prezzo_cons.setHorizontalAlignment(JRAlignment.HORIZONTAL_ALIGN_CENTER);
                    tf_prezzo_cons.setVerticalAlignment(JRAlignment.VERTICAL_ALIGN_MIDDLE);
//                    tf_prezzo_cons.setBottomBorder((byte)1);
//                    tf_prezzo_cons.setBorderColor(Color.RED);

                    JRElement detail1 = detail.getElementByKey("frameStandard");
                    JRDesignFrame fdetail1 = null;
                    if (detail1 != null) {
                        fdetail1 = (JRDesignFrame) detail1;
                    }

                    if (fdetail1 != null) {
                        fdetail1.addElement(tf_prezzo_cons);
                    } else {
                        detail.addElement(tf_prezzo_cons);
                    }

                    JRDesignLine l_prezzo_cons = new JRDesignLine();
                    l_prezzo_cons.setX(tf_prezzo_cons.getX() + tf_prezzo_cons.getWidth() + (marginx * 3));
                    tmp = detail.getElementByKey("l_prezzo");
                    l_prezzo_cons.setY(tmp.getY());
                    l_prezzo_cons.setHeight(tmp.getHeight());
                    l_prezzo_cons.getLinePen().setLineWidth(0.5f);
                    l_prezzo_cons.setForecolor(new Color(204, 204, 204));
                    l_prezzo_cons.setStretchType(JRBaseElement.STRETCH_TYPE_RELATIVE_TO_BAND_HEIGHT);
                    if (fdetail1 != null) {
                        fdetail1.addElement(l_prezzo_cons);
                    } else {
                        detail.addElement(l_prezzo_cons);
                    }

                    //aggiungo fields
                    JRDesignField f_s_prezzo_cons = new JRDesignField();
                    f_s_prezzo_cons.setName("s_prezzo_consigliato");
                    f_s_prezzo_cons.setValueClass(String.class);
                    repdes.addField(f_s_prezzo_cons);

                    return repdes;
                }

                private JasperDesign controllaStampaLuxury(File freport, JasperReport rep, JasperDesign repdes)
                        throws JRException {

                    ArrayList<JRElement> listel = scanJR(repdes);

                    for (JRElement el : listel) {
                        if (el instanceof JRLine) {
                            JRLine ce = (JRLine) el;
                            ce.setForecolor(new Color(200, 200, 140));
                        }
                        if (el instanceof JRRectangle) {
                            JRRectangle ce = (JRRectangle) el;
                            ce.setForecolor(new Color(200, 200, 140));
                        }
                        if (el instanceof JRTextElement) {
                            JRTextElement ce = (JRTextElement) el;
                            ce.setForecolor(new Color(220, 220, 220));
                        }
                    }

                    return repdes;
                }

                private ArrayList<JRElement> scanJR(JasperDesign repdes) {
                    ArrayList l = new ArrayList();
                    l.addAll(scanJR2(repdes.getPageHeader()));
                    l.addAll(scanJR2(repdes.getDetail()));
                    l.addAll(scanJR2(repdes.getPageFooter()));
                    l.addAll(scanJR2(repdes.getLastPageFooter()));
                    l.addAll(scanJR2(repdes.getBackground()));
                    return l;
                }

                private ArrayList scanJR2(JRBand band) {
                    ArrayList l = new ArrayList();
                    for (JRElement el : band.getElements()) {
                        if (el instanceof JRFrame) {
                            JRFrame frame = (JRFrame) el;
                            for (JRElement el2 : frame.getElements()) {
                                l.add(el2);
                            }
                        } else {
                            l.add(el);
                        }
                    }
                    return l;
                }

                private boolean controllaStamparePrezzi(String dbSerie, int dbNumero, int dbAnno, boolean f_acquisto) {
                    try {
                        Object oprezzi = DbUtils.getObject(Db.conn, "select opzione_prezzi_ddt from " + getNomeTab(f_acquisto) + " "
                                + " where serie = '" + dbSerie + "'"
                                + " and numero = " + dbNumero
                                + " and anno = " + dbAnno);
                        if (oprezzi.toString().equalsIgnoreCase("S")) {
                            return true;
                        }
                    } catch (Exception e) {
                    }
                    return false;
                }

                private JasperDesign controllaStamparePrezzi(File freport, JasperReport rep, JasperDesign repdes) {
                    int offx = 50;
                    int marginx = 2;

                    JRDesignBand detail = (JRDesignBand) repdes.getDetailSection().getBands()[0];

                    int larghezza = repdes.getPageHeader().getElementByKey("i_iva").getX() + repdes.getPageHeader().getElementByKey("i_iva").getWidth();
                    int spostarex = repdes.getPageHeader().getElementByKey("i_iva").getX() - repdes.getPageHeader().getElementByKey("i_qta").getX() - repdes.getPageHeader().getElementByKey("i_qta").getWidth();

                    //rimuovo colonne
                    ((JRDesignBand) repdes.getPageHeader()).removeElement((JRDesignElement) repdes.getPageHeader().getElementByKey("i_prezzo"));
                    ((JRDesignBand) repdes.getPageHeader()).removeElement((JRDesignElement) repdes.getPageHeader().getElementByKey("i_sconto"));
//                    if((JRDesignElement) repdes.getPageHeader().getElementByKey("i_casse") != null){
//                        ((JRDesignBand) repdes.getPageHeader()).removeElement((JRDesignElement) repdes.getPageHeader().getElementByKey("i_casse"));
//                    }
                    
                    if((JRDesignElement) repdes.getPageHeader().getElementByKey("i_importo") != null){
                        ((JRDesignBand) repdes.getPageHeader()).removeElement((JRDesignElement) repdes.getPageHeader().getElementByKey("i_importo"));
                    }
                    ((JRDesignBand) repdes.getPageHeader()).removeElement((JRDesignElement) repdes.getPageHeader().getElementByKey("i_iva"));

                    detail.removeElement((JRDesignElement) detail.getElementByKey("l_prezzo"));
                    detail.removeElement((JRDesignElement) detail.getElementByKey("prezzo"));
                    
//                    if((JRDesignElement) repdes.getPageHeader().getElementByKey("l_casse") != null){
//                        detail.removeElement((JRDesignElement) detail.getElementByKey("l_casse"));
//                    }
                    
//                    if((JRDesignElement) repdes.getPageHeader().getElementByKey("casse") != null){
//                        detail.removeElement((JRDesignElement) detail.getElementByKey("casse"));
//                    }
                    
                    detail.removeElement((JRDesignElement) detail.getElementByKey("l_sconto"));
                    detail.removeElement((JRDesignElement) detail.getElementByKey("sconto"));
                    
                    if((JRDesignElement) repdes.getPageHeader().getElementByKey("l_importo") != null){
                        detail.removeElement((JRDesignElement) detail.getElementByKey("l_importo"));
                    }
                    
                    if((JRDesignElement) repdes.getPageHeader().getElementByKey("importo") != null){
                        detail.removeElement((JRDesignElement) detail.getElementByKey("importo"));
                    }
                    detail.removeElement((JRDesignElement) detail.getElementByKey("l_iva"));
                    detail.removeElement((JRDesignElement) detail.getElementByKey("iva"));

                    JRDesignFrame frm = (JRDesignFrame) detail.getElementByKey("frameStandard");

                    frm.removeElement((JRDesignElement) frm.getElementByKey("l_prezzo"));
                    frm.removeElement((JRDesignElement) frm.getElementByKey("prezzo"));
//                    frm.removeElement((JRDesignElement) frm.getElementByKey("l_casse"));
//                    frm.removeElement((JRDesignElement) frm.getElementByKey("casse"));
                    frm.removeElement((JRDesignElement) frm.getElementByKey("l_sconto"));
                    frm.removeElement((JRDesignElement) frm.getElementByKey("sconto"));
                    frm.removeElement((JRDesignElement) frm.getElementByKey("l_importo"));
                    frm.removeElement((JRDesignElement) frm.getElementByKey("importo"));
                    frm.removeElement((JRDesignElement) frm.getElementByKey("l_iva"));
                    frm.removeElement((JRDesignElement) frm.getElementByKey("iva"));

                    //sposto
                    repdes.getPageHeader().getElementByKey("i_qta").setX(repdes.getPageHeader().getElementByKey("i_qta").getX() + spostarex);
                    repdes.getPageHeader().getElementByKey("i_um").setX(repdes.getPageHeader().getElementByKey("i_um").getX() + spostarex);
                    try {
                        repdes.getPageHeader().getElementByKey("i_ean").setX(repdes.getPageHeader().getElementByKey("i_ean").getX() + spostarex);
                    } catch (Exception e) {
                    }

                    detail.getElementByKey("l_um").setX(detail.getElementByKey("l_um").getX() + spostarex);
                    detail.getElementByKey("um").setX(detail.getElementByKey("um").getX() + spostarex);
                    detail.getElementByKey("l_qta").setX(detail.getElementByKey("l_qta").getX() + spostarex);
                    detail.getElementByKey("qta").setX(detail.getElementByKey("qta").getX() + spostarex);
                    try {
                        detail.getElementByKey("l_ean").setX(detail.getElementByKey("l_ean").getX() + spostarex);
                        detail.getElementByKey("ean").setX(detail.getElementByKey("ean").getX() + spostarex);                        
                    } catch (Exception e) {
                    }
                    

//                    frm.getElementByKey("l_um").setX(frm.getElementByKey("l_um").getX() + spostarex);
//                    frm.getElementByKey("um").setX(frm.getElementByKey("um").getX() + spostarex);
//                    frm.getElementByKey("l_qta").setX(frm.getElementByKey("l_qta").getX() + spostarex);
//                    frm.getElementByKey("qta").setX(frm.getElementByKey("qta").getX() + spostarex);

                    //allargo descrizione

                    if (detail.getElementByKey("frameStandard") != null) {
                        frm.getElementByKey("descrizione").setWidth(frm.getElementByKey("descrizione").getWidth() + spostarex);
                    } else {
                        detail.getElementByKey("descrizione").setWidth(detail.getElementByKey("descrizione").getWidth() + spostarex);
                    }

                    return repdes;
                }
            };
            work.start();
            if (attendi) {
                ret = work.get();
                System.out.println("get " + work + " : " + ret);
            }
        } else {
//            prnDdt_tnx temp = new prnDdt_tnx(dbSerie, dbNumero, dbAnno);
            System.err.println("rimossi vecch imodelli di stampa");
        }

        return ret;
    }

    private void esportaDDTinExcel(File folder, int id) {
        String serie = (String) griglia.getValueAt(id, griglia.getColumnByName("serie"));
        String numero = CastUtils.toString(griglia.getValueAt(id, griglia.getColumnByName("numero")));
        String anno = CastUtils.toString(griglia.getValueAt(id, griglia.getColumnByName("anno")));

        //sistemo id_padre
        String sql = "UPDATE " + getNomeTabr() + " r left join " + getNomeTab() + " t on r.serie = t.serie and r.anno = t.anno and r.numero = t.numero set r.id_padre = t.id";
        sql += " where t.serie = " + Db.pc(Db.nz(serie, ""), "VARCHAR");
        sql += " and t.anno = " + Db.pc(anno, "INTEGER");
        sql += " and t.numero = " + Db.pc(numero, "LONG");
        Db.executeSql(sql);

        numero = (String) griglia.getValueAt(id, griglia.getColumnByName("serie")) + numero;
        String nomeFile = folder.getAbsolutePath() + File.separator + "ddt_" + numero + "_" + anno + ".xls";
        id = CastUtils.toInteger0(griglia.getValueAt(id, griglia.getColumnByName("ID")));
        sql = ""
                + "select * from " + getNomeTab() + " t "
                + " left join " + getNomeTabr() + " r on t.id = r.id_padre "
                + " left join clie_forn c on t." + (acquisto ? "fornitore" : "cliente") + " = c.codice"
                + " where t.id = " + id
                + " order by t.id, r.riga"
                + "";
        System.out.println("esportaDDTinExcel " + id + " sql:" + sql);
        ResultSet r = Db.openResultSet(sql);

        Map<String, String> colonne = Collections.synchronizedMap(new LinkedHashMap<String, String>());

        colonne.put("t.serie", "");
        colonne.put("t.numero", "");
        colonne.put("t.anno", "");
        colonne.put("t.data", "");
        colonne.put("t.totale_imponibile", "");
        colonne.put("t.totale_iva", "");
        colonne.put("t.totale", "");
        colonne.put("t.sconto1", "");
        colonne.put("t.sconto2", "");
        colonne.put("t.sconto3", "");
        colonne.put("t.aspetto_esteriore_beni", "");
        colonne.put("t.numero_colli", "");
        colonne.put("t.peso_lordo", "");
        colonne.put("t.peso_netto", "");
        colonne.put("t.vettore1", "");
        colonne.put("t.porto", "");
        colonne.put("t.causale_trasporto", "");
        colonne.put("t.spese_trasporto", "");
        colonne.put("t.spese_incasso", "");
        colonne.put("t.mezzo_consegna", "");

        colonne.put("c.codice", "");
        colonne.put("c.ragione_sociale", "");
        colonne.put("c.piva_cfiscale", "");
        colonne.put("c.cfiscale", "");
        colonne.put("c.indirizzo", "");
        colonne.put("c.cap", "");
        colonne.put("c.localita", "");
        colonne.put("c.provincia", "");

        colonne.put("t.dest_ragione_sociale", "");
        colonne.put("t.dest_indirizzo", "");
        colonne.put("t.dest_cap", "");
        colonne.put("t.dest_localita", "");
        colonne.put("t.dest_provincia", "");

        colonne.put("r.riga", "");
        colonne.put("r.codice_articolo", "");
        colonne.put("r.descrizione", "");
        colonne.put("r.um", "");
        colonne.put("r.quantita", "");
        colonne.put("r.prezzo", "");
        colonne.put("r.iva", "");
        colonne.put("r.sconto1", "");
        colonne.put("r.sconto2", "");

        InvoicexUtil.esportaInExcel(r, nomeFile, "ddt_" + id, null, null, colonne);
    }

    private void setAcquisto(boolean acquisto) {
        this.acquisto = acquisto;

        if (!acquisto) {
            try {
                InvoicexEvent event = new InvoicexEvent(this);
                event.type = InvoicexEvent.TYPE_FRMELENDDT_CONSTR_PRE_INIT_COMPS;
                main.events.fireInvoicexEvent(event);
            } catch (Exception err) {
                err.printStackTrace();
            }
        }

        if (acquisto) {
            radAcquisto.setSelected(true);
            setTitle("Gestione DDT di Acquisto");
            jLabel8.setText("fornitore");
//            butConv.setVisible(false);
            jButton1.setVisible(false);
            this.griglia.dbNomeTabella = "test_ddt_acquisto";
        } else {
            radVendita.setSelected(true);
            setTitle("Gestione DDT di Vendita");
            jLabel8.setText("cliente");
//            butConv.setVisible(true);
            jButton1.setVisible(true);
            this.griglia.dbNomeTabella = "test_ddt";
        }

        //apro la griglia
        this.griglia.flagUsaThread = false;

        java.util.Hashtable colsAlign = new java.util.Hashtable();
        colsAlign.put("Totale Imponibile", "RIGHT_CURRENCY");
        this.griglia.columnsAlign = colsAlign;

        Vector chiave = new Vector();
        chiave.add("Serie");
        chiave.add("Numero");
        chiave.add("Anno");
        this.griglia.dbChiave = chiave;

        //carico le prefereences utente
        try {
            visualizzaTotali = main.fileIni.getValueBoolean("pref", "visualizzaTotali", true);
            String limit = main.fileIni.getValue("pref", "limit", "50");

            this.texLimit.setText(limit);
            if (main.fileIni.getValueBoolean("pref", "visualizzaAnnoInCorso", false)) {
                texDal.setText(DateUtils.getDateStartYear());
                texDalFocusLost(null);
            } else {
                texLimitFocusLost(null);
            }
        } catch (Exception err) {
            err.printStackTrace();
        }

        //metto render info fatturazione
        griglia.getColumn("Fatturato").setCellRenderer(flagRender);
        griglia.getTableHeader().setReorderingAllowed(false);

        //personalizzazioni
        if (main.getPersonalContain(main.PERSONAL_GIANNI)) {
            this.butStampaConfermaOrdine.setVisible(true);
        } else {
            this.butStampaConfermaOrdine.setVisible(false);
        }

        int permessoDdt = acquisto ? Permesso.PERMESSO_DDT_ACQUISTO : Permesso.PERMESSO_DDT_VENDITA;
        int permessoFatture = acquisto ? Permesso.PERMESSO_FATTURE_ACQUISTO : Permesso.PERMESSO_FATTURE_VENDITA;

        if (!main.utente.getPermesso(permessoDdt, Permesso.PERMESSO_TIPO_SCRITTURA)) {
            this.butNew.setEnabled(false);
            this.butDuplica.setEnabled(false);
            this.menDuplica.setEnabled(false);
            this.menAzzeraConv.setEnabled(false);
            this.menCalcEvaso.setEnabled(false);
            this.butModi.setEnabled(false);
            this.menModifica.setEnabled(false);
        }
        if (!main.utente.getPermesso(permessoDdt, Permesso.PERMESSO_TIPO_CANCELLA)) {
            this.butDele.setEnabled(false);
            this.menElimina.setEnabled(false);
        }
        if (!main.utente.getPermesso(permessoFatture, Permesso.PERMESSO_TIPO_SCRITTURA)) {
            this.butConv.setEnabled(false);
            this.butConvNota.setEnabled(false);
            this.menConvFattura.setEnabled(false);
            this.menConvNotaDiCredito.setEnabled(false);
        }
    }

    private String getNomeTab() {
        return getNomeTab(acquisto);
    }

    static private String getNomeTab(boolean acquisto) {
        if (acquisto) {
            return "test_ddt_acquisto";
        } else {
            return "test_ddt";
        }
    }

    private String getNomeTabr() {
        return getNomeTabr(acquisto);
    }

    static private String getNomeTabr(boolean acquisto) {
        if (acquisto) {
            return "righ_ddt_acquisto";
        } else {
            return "righ_ddt";
        }
    }

    public Integer[] getIds() {
        Integer[] ids = new Integer[griglia.getSelectedRowCount()];
        int[] selrows = griglia.getSelectedRows();
        for (int i = 0; i < griglia.getSelectedRowCount(); i++) {
            try {
                ids[i] = (Integer) griglia.getValueAt(selrows[i], griglia.getColumnByName("id"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ids;
    }
}
