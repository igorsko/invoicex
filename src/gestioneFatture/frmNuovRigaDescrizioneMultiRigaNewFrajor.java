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
package gestioneFatture;

import it.tnx.Db;
import com.jidesoft.hints.AbstractListIntelliHints;
import com.jidesoft.swing.SelectAllUtils;
import gestioneFatture.logic.*;

import gestioneFatture.logic.clienti.Cliente;
import it.tnx.commons.CastUtils;
import it.tnx.commons.DbUtils;
import it.tnx.commons.DebugUtils;
import it.tnx.commons.FormatUtils;
import it.tnx.commons.StringUtilsTnx;
import it.tnx.commons.SwingUtils;
import it.tnx.invoicex.InvoicexUtil;
import it.tnx.invoicex.Magazzino;
import it.tnx.invoicex.data.Giacenza;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.DefaultFocusTraversalPolicy;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingworker.SwingWorker;

public class frmNuovRigaDescrizioneMultiRigaNewFrajor
        extends javax.swing.JInternalFrame {

    private Object from;
    String dbStato;
    String dbSerie;
    String prevStato;
    int dbNumero;
    int dbRiga;
    int dbRigaVariante;
    int dbAnno;
    String codiceListino; //indica il listino da usare (per ora 1 o 2)
    int codiceCliente; //indica il cliente a cui si sta facendo il documento
    Object comCodiArti_old = null;
    double peso_kg_collo = 0;
    double totale_ivato = 0d;
    double totale_imponibile = 0d;
    boolean f4 = false;
    AggiornaResiduaWorker aggiornaResidua = null;
    AbstractListIntelliHints alRicerca = null;
    BufferedImage imageSearch = ImageIO.read(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/system-search.png"));
    Border border = UIManager.getBorder("TextField.border");
    int x0 = border.getBorderInsets(new JTextField()).left;

    /** Creates new form frmNuovRiga */
    public frmNuovRigaDescrizioneMultiRigaNewFrajor(Object from, String dbStato, String dbSerie, int dbNumero, String stato, int dbRiga, int dbAnno, String codiceListino, int codiceCliente) throws IOException {
        //texRicerca
        initComponents();

        dati.addCampoAggiuntivo(texStat);
        dati.addCampoAggiuntivo(texAnno);
        dati.addCampoAggiuntivo(texId);
        dati.addCampoAggiuntivo(texSeri);
        dati.addCampoAggiuntivo(texNume);
        dati.addCampoAggiuntivo(texRiga);
        dati.addCampoAggiuntivo(texCodiArti);
        dati.addCampoAggiuntivo(comUm);
        dati.addCampoAggiuntivo(texPrez);
        dati.addCampoAggiuntivo(texQtaOmaggio);
        dati.addCampoAggiuntivo(texPrez3);
        dati.addCampoAggiuntivo(texQta);
        dati.addCampoAggiuntivo(texPrezNett);
        dati.addCampoAggiuntivo(texScon1);
        dati.addCampoAggiuntivo(texScon2);
        dati.addCampoAggiuntivo(texIva);
        dati.addCampoAggiuntivo(texBolla);
        dati.addCampoAggiuntivo(texDisegno);
        dati.addCampoAggiuntivo(texMisura);
        dati.addCampoAggiuntivo(texVariante);

//        main.events.fireInvoicexEvent(new InvoicexEvent(this, InvoicexEvent.TYPE_FRMNuovRigaDescrizioneMultiRigaNew_POST_INIT_COMPS));

        //texRicerca
        texRicerca.setMargin(new Insets(1, x0 + imageSearch.getWidth(), 1, 1));

        alRicerca = new AbstractListIntelliHints(texRicerca) {

            String current_search = "";

            @Override
            protected JList createList() {
                final JList list = super.createList();
                list.setCellRenderer(new DefaultListCellRenderer() {

                    @Override
                    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        String img, tipo;
                        tipo = ((ArticoloHint) value).toString();
                        JLabel lab = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        String word = current_search.toLowerCase();
                        String content = tipo.toLowerCase();
                        Color c = lab.getBackground();
                        c = c.darker();
                        String rgb = Integer.toHexString(c.getRGB());
                        rgb = rgb.substring(2, rgb.length());
                        content = StringUtils.replace(content, word, "<span style='background-color: " + rgb + "'>" + word + "</span>");
                        lab.setText("<html>" + content + "</html>");
                        return lab;
                    }
                });
                return list;
            }

            public boolean updateHints(Object arg0) {
                SwingUtils.mouse_wait();
                current_search = arg0.toString();
                Connection conn;
                try {
                    conn = Db.getConn();
                    String sql = ""
                            + "SELECT a.codice, IFNULL(m.matricola,''), a.descrizione, IFNULL(a.codice_fornitore,''), IFNULL(a.codice_a_barre,'') FROM articoli a left join movimenti_magazzino m on a.codice = m.articolo"
                            + " where codice like '%" + Db.aa(current_search) + "%'"
                            + " or descrizione like '%" + Db.aa(current_search) + "%'"
                            + " or matricola like '%" + Db.aa(current_search) + "%'"
                            + " or codice_fornitore like '%" + Db.aa(current_search) + "%'"
                            + " or codice_a_barre like '%" + Db.aa(current_search) + "%'"
                            + " group by a.codice, IFNULL(m.matricola, '')"
                            + " order by descrizione, codice limit 100";
                    ResultSet rs = DbUtils.tryOpenResultSet(conn, sql);
                    Vector v = new Vector();
                    while (rs.next()) {
                        ArticoloHint art = new ArticoloHint();
                        art.codice = rs.getString(1);
                        art.matricola = rs.getString(2);
                        art.descrizione = rs.getString(3);
                        art.codice_fornitore = rs.getString(4);
                        art.codice_a_barre = rs.getString(5);
                        v.add(art);
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
                    texCodiArti.setText(((ArticoloHint) arg0).codice);
                    recuperaDatiArticolo();
                    texQta.requestFocus();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

//        comCodiArti.setAzzeraTextAbbinato(false);

        try {
            UIDefaults uiDefaults = UIManager.getDefaults();
            texDescrizione.getJTextArea().setFont((Font) uiDefaults.get("TextField.font"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

//        JTextField textCodiArti = (JTextField) comCodiArti.getComponent(2);
//        textCodiArti.addFocusListener(new FocusListener() {
//
//            public void focusGained(FocusEvent e) {
//                comCodiArtiFocusGained(e);
//            }
//
//            public void focusLost(FocusEvent e) {
//            }
//        });

        //this.texPrezNett.setFont(new java.awt.Font(texPrezNett.getFont().getFamily(), texPrezNett.getFont().getSize(), java.awt.Font.ITALIC));
//        this.comCodiArti.setDbRiempireForceText(true);
        this.from = from;
        this.dbStato = dbStato;
        this.dbSerie = dbSerie;
        this.dbNumero = dbNumero;
        this.dbRiga = dbRiga;
        this.dbRigaVariante = dbRigaVariante;
        this.prevStato = stato;
        this.dbAnno = dbAnno;
        this.codiceListino = codiceListino;
        this.codiceCliente = codiceCliente;

        //associo il panel ai dati
        boolean acquisto = false;
        if (from.getClass().getName().equalsIgnoreCase("gestioneFatture.frmTestDocu")) {
            this.dati.dbNomeTabella = "righ_ddt";
        } else if (from.getClass().getName().equalsIgnoreCase("gestioneFatture.frmTestFatt")) {
            this.dati.dbNomeTabella = "righ_fatt";
        } else if (from.getClass().getName().equalsIgnoreCase("gestioneFatture.frmTestOrdine")) {
            this.dati.dbNomeTabella = "righ_ordi";
        } else if (from.getClass().getName().equalsIgnoreCase("gestioneFatture.frmTestFattAcquisto")) {
            this.dati.dbNomeTabella = "righ_fatt_acquisto";
            acquisto = true;
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "non trovata form partenza");
        }

        String wta = "";
        if (acquisto) {
            wta = " where tipo = 'A' or tipo = '' or tipo is null";
        } else {
            wta = " where tipo = 'V' or tipo = '' or tipo is null";
        }

        Vector chiave = new Vector();
        chiave.add("serie");
        chiave.add("numero");
        chiave.add("anno");
        chiave.add("riga");
        chiave.add("stato");
        this.dati.dbChiave = chiave;
        this.dati.butSave = this.butSave;

        //this.dati.butUndo = this.butUndo;
        //this.dati.butFind = this.butFind;
        //109 faccio per lingua
        //carico elenchi unit??? di misura e articoli
        if (main.getPersonal().equalsIgnoreCase(main.PERSONAL_GIANNI)) {
            this.comUm.dbOpenList(Db.getConn(), "select um from articoli group by um", null, false);
//            this.comCodiArti.dbOpenList(Db.getConn(), "select descrizione, codice from articoli " + wta + " order by descrizione", null, false);
        } else {

            boolean eng = false;

            if (this.codiceCliente >= 0) {

                Cliente cliente = new Cliente(this.codiceCliente);

                if (cliente.isItalian() == true) {
                    eng = false;
                } else {
//                    Preferences preferences = java.util.prefs.Preferences.userNodeForPackage(main.class);
//                    if (!preferences.getBoolean("soloItaliano", true)) {
                    if (!main.fileIni.getValueBoolean("pref", "soloItaliano", true)) {
                        eng = true;
                    }
                }
            }

            if (eng == true) {
                this.comUm.dbOpenList(Db.getConn(), "select um_en from articoli group by um_en", null, false);
            } else {
                this.comUm.dbOpenList(Db.getConn(), "select um from articoli group by um", null, false);
            }
        }

        this.texAnno.setText(String.valueOf(dbAnno));

        //mette il focus dopo tutti gli eventi awt
        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            public void run() {
//                texCodiArti.requestFocus();
                texRicerca.requestFocus();
                SelectAllUtils.install(texRicerca);
            }
        });

        setFocusTraversalPolicy(new DefaultFocusTraversalPolicy() {

            @Override
            public Component getComponentBefore(Container aContainer, Component aComponent) {
                if (aComponent == comUm) {
                    return texDescrizione.getJTextArea();
                }
                if (aComponent.getParent() == comUm) {
                    return texDescrizione.getJTextArea();
                }
                if (aComponent == texDescrizione) {
                    return texRicerca;
                }
                if (aComponent == texDescrizione.getJTextArea()) {
                    return texRicerca;
                }
                if (aComponent == texBolla) {
                    return comUm;
                }
                return super.getComponentBefore(aContainer, aComponent);
            }

            @Override
            public Component getComponentAfter(Container aContainer, Component aComponent) {
                System.out.println("getComponentAfter " + aContainer + " " + aComponent);
//                if (aComponent == texCodiArti) {
//                    return comCodiArti;
//                }
//                if (aComponent == comCodiArti) {
//                    return texDescrizione.getJTextArea();
//                }
//                if (aComponent.getParent() == comCodiArti) {
//                    return texDescrizione.getJTextArea();
//                }
                if (aComponent == texRicerca) {
                    return texDescrizione.getJTextArea();
                }
                if (aComponent == texDescrizione) {
                    return comUm;
                }
                if (aComponent == texDescrizione.getJTextArea()) {
                    return comUm;
                }
                if (aComponent == comUm) {
                    return texQta;
                }
                if (aComponent.getParent() == comUm) {
                    return texQta;
                }
                if (aComponent == texQta) {
                    return texPrez;
                }
                if (aComponent == texPrez) {
                    return texPrez3;
                }
                if (aComponent == texPrez3) {
                    return texPrezNett;
                }
                if (aComponent == texPrezNett) {
                    return texScon1;
                }
                if (aComponent == texScon1) {
                    return texScon2;
                }
                if (aComponent == texScon2) {
                    return texIva;
                }
                if (aComponent == texIva) {
                    return texBolla;
                }
                if (aComponent == texVariante) {
                    return butNew;
                }
                if (aComponent == butNew) {
                    return butSave;
                }

                return super.getComponentAfter(aContainer, aComponent);
            }
        });

        getRootPane().setDefaultButton(butSave);


    }

    private void calcolaSconto() {
        if (StringUtilsTnx.isNumber(texPrez.getText()) && StringUtilsTnx.isNumber(texPrezNett.getText())) {
            //calcola lo sconto
            try {
                java.text.NumberFormat form = java.text.NumberFormat.getInstance();
                this.texScon1.setText(Db.formatNumero(Util.getSconto(form.parse(texPrez.getText()).doubleValue(), form.parse(texPrezNett.getText()).doubleValue())));
            } catch (Exception err) {
            }
        }
    }

    private void calcolaScontoDaPercSconto() {
        if (StringUtilsTnx.isNumber(texPrez.getText()) && (StringUtilsTnx.isNumber(texScon1.getText()) || StringUtilsTnx.isNumber(texScon2.getText()))) {
            //calcola lo sconto
            try {
                double prez = StringUtilsTnx.parseDoubleOrZero(texPrez.getText());
                double sco1 = StringUtilsTnx.parseDoubleOrZero(texScon1.getText());
                double sco2 = StringUtilsTnx.parseDoubleOrZero(texScon2.getText());
                double val = prez - (prez / 100d * sco1) - ((prez - (prez / 100d * sco1)) / 100 * sco2);
                texPrezNett.setText(FormatUtils.formatEuroItaMax5(val));
            } catch (Exception err) {
            }
        }
    }

    private void showPrezziFatture() {

        String codiceArticolo = this.texCodiArti.getText();

        if (this.codiceCliente >= 0) {

            try {

                frmPrezziFatturePrecedenti form = new frmPrezziFatturePrecedenti(this.codiceCliente, codiceArticolo);
                main.getPadre().openFrame(form, this.getWidth(), 200, this.getX(), this.getY() + this.getHeight() - 50);
            } catch (Exception err) {
                Util.showErrorMsg(this, err);
            }
        }
    }

    /** This method is called from within the constructor to
     *
     * initialize the form.
     *
     * WARNING: Do NOT modify this code. The content of this method is
     *
     * always regenerated by the Form Editor.
     *
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        dati = new tnxbeans.tnxDbPanel();
        texDescrizione = new tnxbeans.tnxMemoField();
        jPanel5 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        texStat = new tnxbeans.tnxTextField();
        texStat.setVisible(false);
        texAnno = new tnxbeans.tnxTextField();
        texAnno.setVisible(false);
        texId = new tnxbeans.tnxTextField();
        texId.setVisible(false);
        texSeri = new tnxbeans.tnxTextField();
        texNume = new tnxbeans.tnxTextField();
        texRiga = new tnxbeans.tnxTextField();
        jLabel15 = new javax.swing.JLabel();
        texRicerca = new JTextField() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int y = (getHeight() - imageSearch.getHeight()) / 2;
                g.drawImage(imageSearch, x0, y, this);
            }
        };
        texCodiArti = new tnxbeans.tnxTextField();
        jLabel20 = new javax.swing.JLabel();
        labResidua = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        comUm = new tnxbeans.tnxComboField();
        jLabel110 = new javax.swing.JLabel();
        texQta = new tnxbeans.tnxTextField();
        jLabel113 = new javax.swing.JLabel();
        texPrez = new tnxbeans.tnxTextField();
        jLabel114 = new javax.swing.JLabel();
        texQtaOmaggio = new tnxbeans.tnxTextField();
        jLabel4 = new javax.swing.JLabel();
        texPrez3 = new tnxbeans.tnxTextField();
        texPrezNett = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        butCalcolaSconto = new javax.swing.JButton();
        texScon1 = new tnxbeans.tnxTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        texScon2 = new tnxbeans.tnxTextField();
        jLabel22 = new javax.swing.JLabel();
        jLabel111 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        texIva = new tnxbeans.tnxTextField();
        comIva = new javax.swing.JButton();
        labPercentualeIva = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        texBolla = new tnxbeans.tnxTextField();
        texMisura = new tnxbeans.tnxTextField();
        texDisegno = new tnxbeans.tnxTextField();
        texVariante = new tnxbeans.tnxTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        butNew = new javax.swing.JButton();
        butSave = new javax.swing.JButton();
        labTotale = new javax.swing.JLabel();

        setMaximizable(true);
        setResizable(true);
        setTitle("Dettaglio riga");
        setMinimumSize(new java.awt.Dimension(630, 34));
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
            }
        });

        jPanel1.setMinimumSize(new java.awt.Dimension(630, 35));
        jPanel1.setLayout(new java.awt.BorderLayout());

        dati.setEnabled(false);

        texDescrizione.setDbNomeCampo("descrizione");

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/edit-find.png"))); // NOI18N
        jButton1.setText("Visualizza prezzi precedenti");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/apps/accessories-text-editor.png"))); // NOI18N
        jButton2.setText("Aggiungi numero colli");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        texStat.setBackground(new java.awt.Color(255, 200, 200));
        texStat.setText("stato");
        texStat.setDbDescCampo("");
        texStat.setDbNomeCampo("stato");
        texStat.setDbTipoCampo("");

        texAnno.setBackground(new java.awt.Color(255, 200, 200));
        texAnno.setText("anno");
        texAnno.setDbDescCampo("");
        texAnno.setDbNomeCampo("anno");
        texAnno.setDbRiempire(false);
        texAnno.setDbTipoCampo("");

        texId.setBackground(new java.awt.Color(255, 200, 200));
        texId.setText("id_padre");
        texId.setDbDescCampo("");
        texId.setDbNomeCampo("id_padre");
        texId.setDbTipoCampo("");

        texSeri.setBackground(new java.awt.Color(204, 204, 204));
        texSeri.setColumns(5);
        texSeri.setEditable(false);
        texSeri.setText("serie");
        texSeri.setToolTipText("Serie");
        texSeri.setDbDescCampo("");
        texSeri.setDbNomeCampo("serie");
        texSeri.setDbTipoCampo("");

        texNume.setEditable(false);
        texNume.setText("numero");
        texNume.setToolTipText("Numero");
        texNume.setDbDescCampo("");
        texNume.setDbNomeCampo("numero");
        texNume.setDbTipoCampo("testo");
        texNume.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                texNumeActionPerformed(evt);
            }
        });

        texRiga.setText("riga");
        texRiga.setToolTipText("Riga");
        texRiga.setDbDescCampo("");
        texRiga.setDbNomeCampo("riga");
        texRiga.setDbTipoCampo("");

        jLabel15.setText("Ricerca articolo");

        texRicerca.setForeground(javax.swing.UIManager.getDefaults().getColor("TextField.inactiveForeground"));
        texRicerca.setText("... digita qui per cercare l'articolo tramite codice o descrizione");
        texRicerca.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                texRicercaFocusGained(evt);
            }
        });
        texRicerca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                texRicercaKeyPressed(evt);
            }
        });

        texCodiArti.setText("codice_articolo");
        texCodiArti.setToolTipText("Codice Articolo");
        texCodiArti.setDbDescCampo("");
        texCodiArti.setDbNomeCampo("codice_articolo");
        texCodiArti.setDbTipoCampo("");
        texCodiArti.setPreferredSize(new java.awt.Dimension(150, 20));
        texCodiArti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                texCodiArtiActionPerformed(evt);
            }
        });
        texCodiArti.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                texCodiArtiFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                texCodiArtiFocusLost(evt);
            }
        });
        texCodiArti.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                texCodiArtiKeyPressed(evt);
            }
        });

        jLabel20.setText("descrizione articolo/riga");

        labResidua.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel5Layout.createSequentialGroup()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel5Layout.createSequentialGroup()
                        .add(148, 148, 148)
                        .add(labResidua, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 490, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel5Layout.createSequentialGroup()
                            .add(jButton1)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jButton2)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 108, Short.MAX_VALUE)
                            .add(texStat, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 41, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(texAnno, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(texId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(texSeri, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(texNume, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 45, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(texRiga, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(jPanel5Layout.createSequentialGroup()
                            .add(jLabel15)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(texRicerca, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(texCodiArti, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 187, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(jPanel5Layout.createSequentialGroup()
                            .add(jLabel20, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                            .add(496, 496, 496))))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton1)
                    .add(jButton2)
                    .add(texStat, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(texAnno, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(texId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(texSeri, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(texNume, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(texRiga, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel15)
                    .add(texRicerca, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(texCodiArti, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel5Layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 20, Short.MAX_VALUE)
                        .add(jLabel20))
                    .add(jPanel5Layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(labResidua)
                        .addContainerGap())))
        );

        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel23.setText("unità di misura");
        jPanel6.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, -1));

        comUm.setDbDescCampo("unità di misura");
        comUm.setDbNomeCampo("um");
        comUm.setDbRiempireForceText(true);
        comUm.setDbSalvaKey(false);
        comUm.setDbTipoCampo("");
        jPanel6.add(comUm, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, -1, -1));

        jLabel110.setText("Capi");
        jPanel6.add(jLabel110, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 20, -1, 20));

        texQta.setText("capi");
        texQta.setDbDescCampo("");
        texQta.setDbNomeCampo("quantita");
        texQta.setDbTipoCampo("numerico");
        texQta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                texQtaActionPerformed(evt);
            }
        });
        texQta.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                texQtaFocusLost(evt);
            }
        });
        texQta.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                texQtaInputMethodTextChanged(evt);
            }
        });
        texQta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                texQtaKeyReleased(evt);
            }
        });
        jPanel6.add(texQta, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 40, 60, -1));

        jLabel113.setText("prezzo senza iva");
        jPanel6.add(jLabel113, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 20, -1, 20));

        texPrez.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        texPrez.setText("prezzo");
        texPrez.setDbDescCampo("");
        texPrez.setDbNomeCampo("prezzo");
        texPrez.setDbTipoCampo("valuta");
        texPrez.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                texPrezActionPerformed(evt);
            }
        });
        texPrez.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                texPrezFocusLost(evt);
            }
        });
        texPrez.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                texPrezInputMethodTextChanged(evt);
            }
        });
        texPrez.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                texPrezKeyReleased(evt);
            }
        });
        jPanel6.add(texPrez, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 40, 70, -1));

        jLabel114.setText("in omaggio");
        jLabel114.setToolTipText("quantita omaggio");
        jPanel6.add(jLabel114, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 60, -1, 20));

        texQtaOmaggio.setText("0");
        texQtaOmaggio.setDbDescCampo("");
        texQtaOmaggio.setDbNomeCampo("quantita_omaggio");
        texQtaOmaggio.setDbRiempire(false);
        texQtaOmaggio.setDbSalvare(false);
        texQtaOmaggio.setDbTipoCampo("numerico");
        texQtaOmaggio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                texQtaOmaggioActionPerformed(evt);
            }
        });
        texQtaOmaggio.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                texQtaOmaggioFocusLost(evt);
            }
        });
        jPanel6.add(texQtaOmaggio, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 80, 60, -1));

        jLabel4.setText("prezzo netto");
        jPanel6.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 60, -1, 20));

        texPrez3.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        texPrez3.setText("prezzo lordo");
        texPrez3.setDbDescCampo("");
        texPrez3.setDbNomeCampo("prezzo_lordo");
        texPrez3.setDbRiempire(false);
        texPrez3.setDbSalvare(false);
        texPrez3.setDbTipoCampo("valuta");
        texPrez3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                texPrez3FocusLost(evt);
            }
        });
        texPrez3.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                texPrez3InputMethodTextChanged(evt);
            }
        });
        texPrez3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                texPrez3KeyReleased(evt);
            }
        });
        jPanel6.add(texPrez3, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 80, 70, -1));

        texPrezNett.setBackground(new java.awt.Color(255, 204, 204));
        texPrezNett.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        texPrezNett.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                texPrezNettFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                texPrezNettFocusLost(evt);
            }
        });
        texPrezNett.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                texPrezNettKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                texPrezNettKeyReleased(evt);
            }
        });
        jPanel6.add(texPrezNett, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 60, 60, -1));

        jLabel18.setText("sco/ric 1");
        jLabel18.setToolTipText("Sconto o ricarico (in negativo)");
        jPanel6.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 40, -1, -1));

        butCalcolaSconto.setText("C");
        butCalcolaSconto.setToolTipText("Calcola sconto");
        butCalcolaSconto.setMargin(new java.awt.Insets(1, 1, 1, 1));
        butCalcolaSconto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butCalcolaScontoActionPerformed(evt);
            }
        });
        jPanel6.add(butCalcolaSconto, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 60, 20, -1));

        texScon1.setText("sconto1");
        texScon1.setDbDescCampo("");
        texScon1.setDbNomeCampo("sconto1");
        texScon1.setDbTipoCampo("numerico");
        texScon1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                texScon1ActionPerformed(evt);
            }
        });
        texScon1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                texScon1FocusLost(evt);
            }
        });
        texScon1.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                texScon1InputMethodTextChanged(evt);
            }
        });
        texScon1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                texScon1KeyReleased(evt);
            }
        });
        jPanel6.add(texScon1, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 60, 50, -1));

        jLabel21.setFont(jLabel21.getFont().deriveFont(jLabel21.getFont().getSize()-2f));
        jLabel21.setText("%");
        jLabel21.setToolTipText("Sconto o ricarico (in negativo)");
        jLabel21.setIconTextGap(0);
        jLabel21.setMinimumSize(new java.awt.Dimension(9, 11));
        jLabel21.setNextFocusableComponent(texBolla);
        jLabel21.setPreferredSize(new java.awt.Dimension(9, 11));
        jPanel6.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 60, 30, 20));

        jLabel19.setText("sco/ric 2");
        jLabel19.setToolTipText("Sconto o ricarico (in negativo)");
        jPanel6.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 40, 50, -1));

        texScon2.setText("sconto2");
        texScon2.setDbDescCampo("");
        texScon2.setDbNomeCampo("sconto2");
        texScon2.setDbTipoCampo("numerico");
        texScon2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                texScon2ActionPerformed(evt);
            }
        });
        texScon2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                texScon2FocusLost(evt);
            }
        });
        texScon2.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                texScon2InputMethodTextChanged(evt);
            }
        });
        texScon2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                texScon2KeyReleased(evt);
            }
        });
        jPanel6.add(texScon2, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 60, 60, -1));

        jLabel22.setFont(jLabel22.getFont().deriveFont(jLabel22.getFont().getSize()-2f));
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("%");
        jLabel22.setToolTipText("Sconto o ricarico (in negativo)");
        jLabel22.setIconTextGap(0);
        jLabel22.setMinimumSize(new java.awt.Dimension(9, 11));
        jLabel22.setPreferredSize(new java.awt.Dimension(9, 11));
        jPanel6.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 60, 20, 20));

        jLabel111.setText("prezzo con iva");
        jPanel6.add(jLabel111, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 40, -1, -1));

        jLabel1.setText("Iva");
        jPanel6.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 40, -1, -1));

        texIva.setText("iva");
        texIva.setToolTipText("premere F4 per avere la lista dei codici iva");
        texIva.setDbDescCampo("");
        texIva.setDbNomeCampo("iva");
        texIva.setDbTipoCampo("");
        texIva.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                texIvaFocusLost(evt);
            }
        });
        texIva.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                texIvaKeyPressed(evt);
            }
        });
        jPanel6.add(texIva, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 60, 50, -1));

        comIva.setText("...");
        comIva.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comIvaActionPerformed(evt);
            }
        });
        jPanel6.add(comIva, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 60, 30, 20));

        labPercentualeIva.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labPercentualeIva.setText("%");
        jPanel6.add(labPercentualeIva, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 60, 30, 20));

        jLabel2.setText("- premere F4 sul codice articolo o sulla descrizione per avere la lista articoli filtrata");
        jPanel6.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, -1, -1));

        jLabel3.setText("- inserire nella casella prezzo netto il prezzo a cui si desidera arrivare, premendo invio calcola lo sconto da applicare");
        jPanel6.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, -1, -1));

        texBolla.setDbNomeCampo("bolla_cliente");
        texBolla.setNextFocusableComponent(texMisura);

        texMisura.setDbNomeCampo("misura");
        texMisura.setNextFocusableComponent(texDisegno);
        texMisura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                texMisuraActionPerformed(evt);
            }
        });

        texDisegno.setDbNomeCampo("disegno");
        texDisegno.setNextFocusableComponent(texVariante);

        texVariante.setDbNomeCampo("var");

        jLabel5.setText("Variante:");

        jLabel6.setText("Disegno:");

        jLabel7.setText("Misura:");

        jLabel8.setText("Bolla Cliente:");

        org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 71, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel6)
                    .add(jLabel7)
                    .add(jLabel8))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(texBolla, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE)
                    .add(texMisura, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE)
                    .add(texDisegno, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE)
                    .add(texVariante, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(texBolla, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel8))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(texMisura, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel7))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(texDisegno, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel6))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(texVariante, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel5))
                .addContainerGap())
        );

        butNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/edit-undo.png"))); // NOI18N
        butNew.setText("Annulla");
        butNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butNewActionPerformed(evt);
            }
        });
        jPanel3.add(butNew);

        butSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/devices/media-floppy.png"))); // NOI18N
        butSave.setText("Salva");
        butSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSaveActionPerformed(evt);
            }
        });
        jPanel3.add(butSave);

        labTotale.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labTotale.setText("...");
        labTotale.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        labTotale.setPreferredSize(new java.awt.Dimension(200, 50));

        org.jdesktop.layout.GroupLayout datiLayout = new org.jdesktop.layout.GroupLayout(dati);
        dati.setLayout(datiLayout);
        datiLayout.setHorizontalGroup(
            datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(datiLayout.createSequentialGroup()
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(datiLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(texDescrizione, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, datiLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(jPanel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(datiLayout.createSequentialGroup()
                        .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 458, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(labTotale, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)))
                .addContainerGap())
            .add(datiLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel6, 0, 715, Short.MAX_VALUE)
                .add(9, 9, 9))
        );
        datiLayout.setVerticalGroup(
            datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(datiLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(texDescrizione, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 163, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(labTotale, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel1.add(dati, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void comCodiArtiItemStateChanged(java.awt.event.ItemEvent evt) {
    }

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        main.fileIni.setValue("dimensioni", "frmNuovRigaDescrizioneMultiRigaFrajor_w", getSize().width);
        main.fileIni.setValue("dimensioni", "frmNuovRigaDescrizioneMultiRigaFrajor_h", getSize().height);
        main.fileIni.setValue("dimensioni", "frmNuovRigaDescrizioneMultiRigaFrajor_top", getLocation().y);
        main.fileIni.setValue("dimensioni", "frmNuovRigaDescrizioneMultiRigaFrajor_left", getLocation().x);
        // Add your handling code here:
        main.getPadre().closeFrame(this);
    }//GEN-LAST:event_formInternalFrameClosed

    private void butNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butNewActionPerformed
        this.dispose();
        //riattivo form di provenienza
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                try {
                    main.getPadre().getDesktopPane().getDesktopManager().activateFrame((JInternalFrame) from);
                    ((JInternalFrame) from).setSelected(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }//GEN-LAST:event_butNewActionPerformed

    private void butSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveActionPerformed
        String nomeTab = "righ_fatt_matricole";

        //debug
//        System.out.println(this.comCodiArti.getText());

        boolean aprireMatricolePre = false;
        String tab = "";

        if (controlli()) {
            Integer numFatt = Integer.parseInt(this.texNume.getText());
            Integer anno = Integer.parseInt(this.texAnno.getText());
            if (this.dbStato.equals("I") && (this.dbRiga != 0)) {
                if (from.getClass().getName().equalsIgnoreCase("gestioneFatture.frmTestDocu")) {
                    tab = "righ_ddt";
                } else if (from.getClass().getName().equalsIgnoreCase("gestioneFatture.frmTestFatt")) {
                    tab = "righ_fatt";
                } else if (from.getClass().getName().equalsIgnoreCase("gestioneFatture.frmTestOrdine")) {
                    tab = "righ_ordi";
                } else if (from.getClass().getName().equalsIgnoreCase("gestioneFatture.frmTestFattAcquisto")) {
                    tab = "righ_fatt_acquisto";
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this, "non trovata form partenza");
                }
                try {
                    Statement stat = Db.getConn().createStatement();
                    String sql = "update " + tab + " set riga = riga+1 where riga >= " + dbRiga;
                    sql += " and serie = '" + texSeri.getText() + "'";
                    sql += " and numero = " + numFatt;
                    sql += " and anno = " + anno + " order by riga DESC";
                    Db.executeSql(sql);
                } catch (SQLException sqlerr) {
                    sqlerr.printStackTrace();
                }
            }

            if (dati.getCampiAggiuntivi() == null) {
                dati.setCampiAggiuntivi(new Hashtable());
            }

            dati.getCampiAggiuntivi().put("totale_ivato", totale_ivato);
            dati.getCampiAggiuntivi().put("totale_imponibile", totale_imponibile);
            
            if (!dati.dbSave()) {
                return;
            }

            //inserisco omaggi se presenti
            if (CastUtils.toDouble0(texQtaOmaggio.getText()) > 0) {
                try {
                    Statement stat = Db.getConn().createStatement();
                    String sql = "update " + tab + " set riga = riga+1 where riga >= " + dbRiga;
                    sql += " and serie = '" + texSeri.getText() + "'";
                    sql += " and numero = " + numFatt;
                    sql += " and anno = " + anno + " order by riga DESC";
                    Db.executeSql(sql);
                } catch (SQLException sqlerr) {
                    sqlerr.printStackTrace();
                }
                String newriga = String.valueOf(CastUtils.toInteger0(texRiga.getText()) + 1);
                String newqta = texQtaOmaggio.getText();
                String newcodart = texCodiArti.getText();
                String newdescart = "Omaggio: " + texDescrizione.getText();
                String newum = comUm.getText();
                inserimento();
                texRiga.setText(newriga);
                texCodiArti.setText(newcodart);
                texDescrizione.setText(newdescart);
                texQta.setText(newqta);
                texPrez.setText("0");
                comUm.setText(newum);
                try {
                    dati.dbSave();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            if (from.getClass().getName().equalsIgnoreCase("gestionepreventivi.frmOrdiTest")) {
                javax.swing.JOptionPane.showMessageDialog(this, "non trovato form di partenza");
            } else if (from.getClass().getName().equalsIgnoreCase("gestioneFatture.frmTestDocu")) {
                frmTestDocu tempFrom = (frmTestDocu) from;
                tempFrom.griglia.dbRefresh();
                tempFrom.prev.dbRefresh();
                tempFrom.ricalcolaTotali();
                aprireMatricolePre = true;
            } else if (from.getClass().getName().equalsIgnoreCase("gestioneFatture.frmTestFatt")) {
                frmTestFatt tempFrom = (frmTestFatt) from;
                tempFrom.griglia.dbRefresh();
                tempFrom.prev.dbRefresh();
                tempFrom.ricalcolaTotali();

                //vado a cercare nei ddt se ce ne è almeno uno attaccato a questa fattura
                int conta = 0;
                try {
                    String sql = "select count(*) from test_ddt"
                            + " where fattura_serie = '" + texSeri.getText() + "'"
                            + " and fattura_numero = " + texNume.getText()
                            + " and fattura_anno = " + texAnno.getText();
                    ResultSet r = Db.openResultSet(sql);
                    if (r.next()) {
                        conta = r.getInt(1);
                    }
                } catch (SQLException sqlerr) {
                    sqlerr.printStackTrace();
                }
                if (conta == 0) {
                    aprireMatricolePre = true;
                } else {
                    if (gestioneFatture.main.pluginClientManager) {
                        JOptionPane.showMessageDialog(this, "La fattura proviene da uno o più ddt e non verranno creati o rigenerati i movimenti");
                    }
                }
            } else if (from.getClass().getName().equalsIgnoreCase("gestioneFatture.frmTestFattAcquisto")) {
                nomeTab = "righ_fatt_acquisto_matricole";
                frmTestFattAcquisto tempFrom = (frmTestFattAcquisto) from;
                tempFrom.griglia.dbRefresh();
                tempFrom.prev.dbRefresh();
                tempFrom.ricalcolaTotali();
                aprireMatricolePre = true;
            } else if (from.getClass().getName().equalsIgnoreCase("gestioneFatture.frmTestOrdine")) {
                frmTestOrdine tempFrom = (frmTestOrdine) from;
                tempFrom.griglia.dbRefresh();
                tempFrom.ordine.dbRefresh();
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "non trovato form di partenza");
            }

            this.dispose();

            //apro gestione matricole
            boolean aprireMatricole = false;
            if (aprireMatricolePre) {
                Statement s = null;
                ResultSet r = null;
                try {
                    s = Db.getConn().createStatement();
                    String sql = "select gestione_matricola from articoli where codice = " + Db.pc(texCodiArti.getText(), Types.VARCHAR);
                    r = s.executeQuery(sql);
                    if (r.next()) {
                        if (r.getString(1).equalsIgnoreCase("S")) {
                            aprireMatricole = true;
                        }
                    }
                } catch (SQLException err) {
                    err.printStackTrace();
                } finally {
                    try {
                        r.close();
                        s.close();
                    } catch (Exception err) {
                    }
                }
            }

//            if (aprireMatricole) {
//                JDialogMatricole dialogMatricole = new JDialogMatricole(main.getPadre(), true, it.tnx.Util.getInt(texQta.getText()), Integer.parseInt(texRiga.getText()), texCodiArti.getText(), texSeri.getText(), texNume.getText(), texAnno.getText(), nomeTab);
//                if (from.getClass().getName().equalsIgnoreCase("gestioneFatture.frmTestFattAcquisto")) {
//                    dialogMatricole.setMatricoleDaInserire(true);
//                }
//                dialogMatricole.setLocationRelativeTo(null);
//                dialogMatricole.setVisible(true);
//                //salvare insieme alla riga
//                //inserisco le matricole
//                ArrayList<String> matrs = new ArrayList();
//                for (int i = 0; i < dialogMatricole.model.getRowCount(); i++) {
//                    String matr = "";
//                    if (dialogMatricole.model.getValueAt(i, 0) instanceof Giacenza) {
//                        Giacenza giac = (Giacenza) dialogMatricole.model.getValueAt(i, 0);
//                        matr = giac.getMatricola();
//                    } else {
//                        matr = (String) dialogMatricole.model.getValueAt(i, 0);
//                    }
//                    matrs.add(matr);
//                    //System.out.println("dalfare:" + matr + " " + texSeri.getText() + " " + texNume.getText() + " " + texAnno.getText());
//                    String sql = "insert into " + nomeTab + " (serie, numero, anno, riga, matricola) values (";
//                    sql += " " + Db.pc(texSeri.getText(), Types.VARCHAR);
//                    sql += " , " + Db.pc(texNume.getText(), Types.INTEGER);
//                    sql += " , " + Db.pc(texAnno.getText(), Types.INTEGER);
//                    sql += " , " + Db.pc(texRiga.getText(), Types.INTEGER);
//                    sql += " , " + Db.pc(matr, Types.VARCHAR);
//                    sql += ")";
//                    Db.executeSql(sql);
//                }
//                if (from.getClass().getName().equalsIgnoreCase("gestioneFatture.frmTestFatt")) {
//                    String toadd = "";
//                    for (String m : matrs) {
//                        toadd += "\nS/N: " + m;
//                    }
//                    String sql = "update righ_fatt set descrizione = CONCAT(descrizione,'" + StringUtils.replace(toadd, "'", "\\'") + "') where serie = '" + texSeri.getText() + "' and numero = " + texNume.getText() + " and anno = " + texAnno.getText() + " and riga = " + texRiga.getText();
//                    Db.executeSql(sql, true);
//                }
//            }

            //riattivo form di provenienza
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    try {
                        main.getPadre().getDesktopPane().getDesktopManager().activateFrame((JInternalFrame) from);
                        ((JInternalFrame) from).setSelected(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } else {
            //controllo non ok
        }


    }//GEN-LAST:event_butSaveActionPerformed

    public void aggiorna_iva() {
        try {
            double iva_prezz = 100d + ((BigDecimal) DbUtils.getObject(Db.getConn(), "select percentuale from codici_iva where codice = " + Db.pc(texIva.getText(), Types.VARCHAR))).doubleValue();

            double new_prezz_lordo = 0;
            double prezz_netto = Db.getDouble(this.texPrez.getText());

//        if (prezz_netto >= 0) {
            new_prezz_lordo = (double) (prezz_netto / 100d) * iva_prezz;
            this.texPrez3.setText(Db.formatDecimal5(new_prezz_lordo));
//        }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

private void texMisuraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texMisuraActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_texMisuraActionPerformed

private void comIvaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comIvaActionPerformed

    frmListIva frm = new frmListIva(this.texIva, this.labPercentualeIva, this);

    main.getPadre().openFrame(frm, 400, 200, texIva.getLocationOnScreen().y, texIva.getLocationOnScreen().x);
}//GEN-LAST:event_comIvaActionPerformed

private void texIvaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texIvaKeyPressed

    if (evt.getKeyCode() == evt.VK_F4) {
        comIvaActionPerformed(null);
    }
}//GEN-LAST:event_texIvaKeyPressed

private void texIvaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texIvaFocusLost

    //trova la percentuale dell'iva
    Iva iva = new Iva();
    iva.load(Db.INSTANCE, this.texIva.getText());

    java.text.DecimalFormat decformat = new java.text.DecimalFormat("##0");
    this.labPercentualeIva.setText(decformat.format(iva.getPercentuale()) + "%");

    aggiorna_iva();

    aggiornaTotale();
}//GEN-LAST:event_texIvaFocusLost

private void texScon2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texScon2KeyReleased
    aggiornaTotale();
}//GEN-LAST:event_texScon2KeyReleased

private void texScon2InputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_texScon2InputMethodTextChanged
    aggiornaTotale();
}//GEN-LAST:event_texScon2InputMethodTextChanged

private void texScon2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texScon2FocusLost
    calcolaScontoDaPercSconto();
    aggiornaTotale();
}//GEN-LAST:event_texScon2FocusLost

private void texScon2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texScon2ActionPerformed
    calcolaScontoDaPercSconto();
}//GEN-LAST:event_texScon2ActionPerformed

private void texScon1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texScon1KeyReleased
    aggiornaTotale();
}//GEN-LAST:event_texScon1KeyReleased

private void texScon1InputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_texScon1InputMethodTextChanged
    aggiornaTotale();
}//GEN-LAST:event_texScon1InputMethodTextChanged

private void texScon1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texScon1FocusLost
    calcolaScontoDaPercSconto();
    aggiornaTotale();
}//GEN-LAST:event_texScon1FocusLost

private void texScon1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texScon1ActionPerformed
    calcolaScontoDaPercSconto();
}//GEN-LAST:event_texScon1ActionPerformed

private void butCalcolaScontoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butCalcolaScontoActionPerformed
    calcolaSconto();
}//GEN-LAST:event_butCalcolaScontoActionPerformed

private void texPrezNettKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texPrezNettKeyReleased
    aggiornaTotale();
}//GEN-LAST:event_texPrezNettKeyReleased

private void texPrezNettKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texPrezNettKeyPressed

    if (evt.getKeyCode() == 10) {
        calcolaSconto();
    }
}//GEN-LAST:event_texPrezNettKeyPressed

private void texPrezNettFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texPrezNettFocusLost
    getRootPane().setDefaultButton(butSave);
}//GEN-LAST:event_texPrezNettFocusLost

private void texPrezNettFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texPrezNettFocusGained
    getRootPane().setDefaultButton(butCalcolaSconto);
}//GEN-LAST:event_texPrezNettFocusGained

private void texPrez3KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texPrez3KeyReleased
    try {
        double iva_prezz = 100d + ((BigDecimal) DbUtils.getObject(Db.getConn(), "select percentuale from codici_iva where codice = " + Db.pc(texIva.getText(), Types.VARCHAR))).doubleValue();

        double new_prezz_netto = 0;
        double prezz_lordo = Db.getDouble(this.texPrez3.getText());

        //        if (prezz_lordo > 0) {
        new_prezz_netto = (double) (prezz_lordo * 100d) / iva_prezz;
        this.texPrez.setText(Db.formatDecimal5(new_prezz_netto));
        //        }
    } catch (Exception ex) {
        ex.printStackTrace();
    }
    aggiornaTotale();
}//GEN-LAST:event_texPrez3KeyReleased

private void texPrez3InputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_texPrez3InputMethodTextChanged
    aggiornaTotale();
}//GEN-LAST:event_texPrez3InputMethodTextChanged

private void texPrez3FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texPrez3FocusLost
    try {
        double iva_prezz = 100d + ((BigDecimal) DbUtils.getObject(Db.getConn(), "select percentuale from codici_iva where codice = " + Db.pc(texIva.getText(), Types.VARCHAR))).doubleValue();

        double new_prezz_netto = 0;
        double prezz_lordo = Db.getDouble(this.texPrez3.getText());

        //        if (prezz_lordo > 0) {
        new_prezz_netto = (double) (prezz_lordo * 100d) / iva_prezz;
        this.texPrez.setText(Db.formatDecimal5(new_prezz_netto));
        //        }
    } catch (Exception ex) {
        ex.printStackTrace();
    }
    calcolaScontoDaPercSconto();
    aggiornaTotale();
}//GEN-LAST:event_texPrez3FocusLost

private void texQtaOmaggioFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texQtaOmaggioFocusLost
    // TODO add your handling code here:
}//GEN-LAST:event_texQtaOmaggioFocusLost

private void texQtaOmaggioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texQtaOmaggioActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_texQtaOmaggioActionPerformed

private void texPrezKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texPrezKeyReleased
    // TODO add your handling code here:
    aggiorna_iva();
    aggiornaTotale();
}//GEN-LAST:event_texPrezKeyReleased

private void texPrezInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_texPrezInputMethodTextChanged
    aggiornaTotale();
}//GEN-LAST:event_texPrezInputMethodTextChanged

private void texPrezFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texPrezFocusLost
    // TODO add your handling code here:
    aggiorna_iva();
    calcolaScontoDaPercSconto();
    aggiornaTotale();
}//GEN-LAST:event_texPrezFocusLost

private void texPrezActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texPrezActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_texPrezActionPerformed

private void texQtaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texQtaKeyReleased
    aggiornaTotale();
}//GEN-LAST:event_texQtaKeyReleased

private void texQtaInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_texQtaInputMethodTextChanged
    aggiornaTotale();
}//GEN-LAST:event_texQtaInputMethodTextChanged

private void texQtaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texQtaFocusLost
    aggiornaTotale();
}//GEN-LAST:event_texQtaFocusLost

private void texQtaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texQtaActionPerformed
}//GEN-LAST:event_texQtaActionPerformed

private void texCodiArtiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texCodiArtiKeyPressed

    // Add your handling code here:
    if (evt.getKeyCode() == evt.VK_ENTER) {
        recuperaDatiArticolo();
    } else if (evt.getKeyCode() == evt.VK_F4) {
        f4 = true;
        java.util.Hashtable colsWidthPerc = new java.util.Hashtable();
        colsWidthPerc.put("codice", new Double(20));
        colsWidthPerc.put("descrizione", new Double(80));

        String sql = "select codice, descrizione from articoli" + " where codice like '" + Db.aa(this.texCodiArti.getText()) + "%'" + " order by codice, descrizione";
        frmDbListSmall temp = new frmDbListSmall(main.getPadre(), true, sql, this.texCodiArti, 0, colsWidthPerc, 50, 200, 900, 500);
        this.texCodiArti.requestFocus();
        recuperaDatiArticolo();
        //            this.comCodiArti.dbTrovaKey(this.texCodiArti.getText());
        //            this.comCodiArti.requestFocus();
        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                texQta.requestFocus();
            }
        });
        f4 = false;
    }
}//GEN-LAST:event_texCodiArtiKeyPressed

private void texCodiArtiFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texCodiArtiFocusLost
    if (comCodiArti_old != null && !comCodiArti_old.equals(texCodiArti.getText())) {
        if (!f4) {
            recuperaDatiArticolo();
        }
    }
}//GEN-LAST:event_texCodiArtiFocusLost

private void texCodiArtiFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texCodiArtiFocusGained
    comCodiArti_old = texCodiArti.getText();
}//GEN-LAST:event_texCodiArtiFocusGained

private void texCodiArtiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texCodiArtiActionPerformed
    // Add your handling code here:
}//GEN-LAST:event_texCodiArtiActionPerformed

private void texRicercaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texRicercaKeyPressed
    if (texRicerca.getText().equalsIgnoreCase("... digita qui per cercare l'articolo tramite codice o descrizione")) {
        texRicerca.setText("");
        texRicerca.setForeground(javax.swing.UIManager.getDefaults().getColor("TextField.foreground"));
    }

    String key = "";
    try {
        key = ((ArticoloHint) alRicerca.getSelectedHint()).codice;
    } catch (Exception e) {
    }
    if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
        //        texCodiArti.setText(key);
        //        recuperaDatiArticolo();
    } else if (evt.getKeyCode() == KeyEvent.VK_F4) {
        java.util.Hashtable colsWidthPerc = new java.util.Hashtable();
        colsWidthPerc.put("codice", new Double(20));
        colsWidthPerc.put("descrizione", new Double(80));
        String sql = "select codice, descrizione from articoli" + " where descrizione like '" + Db.aa(key) + "%'" + " order by descrizione, codice";
        frmDbListSmall temp = new frmDbListSmall(main.getPadre(), true, sql, this.texCodiArti, 0, colsWidthPerc, 50, 200, 900, 500);
        this.texCodiArti.requestFocus();
        recuperaDatiArticolo();
        this.requestFocus();
    }
}//GEN-LAST:event_texRicercaKeyPressed

private void texRicercaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texRicercaFocusGained
    //    if (texRicerca.getText().equalsIgnoreCase("... digita qui per cercare l'articolo tramite codice o descrizione")) {
    //        texRicerca.setText("");
    //        texRicerca.setForeground(javax.swing.UIManager.getDefaults().getColor("TextField.foreground"));
    //    } else {
    //    }
}//GEN-LAST:event_texRicercaFocusGained

private void texNumeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texNumeActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_texNumeActionPerformed

private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
    if (peso_kg_collo != 0) {
        double qta = it.tnx.Util.getDouble(texQta.getText());
        double collid = qta / peso_kg_collo;
        int colli = ((Double) Math.ceil(collid)).intValue();
        texDescrizione.setText(texDescrizione.getText() + "\n" + "Numero colli: " + colli);
    } else {
        JOptionPane.showMessageDialog(this, "Non è inserito il peso per collo in anagrafica articoli", "Attenzione", JOptionPane.INFORMATION_MESSAGE);
    }
}//GEN-LAST:event_jButton2ActionPerformed

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    showPrezziFatture();
}//GEN-LAST:event_jButton1ActionPerformed

    public void aggiornaTotale() {
        try {
            double qta = CastUtils.toDouble0(texQta.getText());
            double importo_senza_iva = CastUtils.toDouble0(texPrez.getText());
//            double importo_con_iva = CastUtils.toDouble0(texPrez3.getText());
            double sconto1 = CastUtils.toDouble0(texScon1.getText());
            double sconto2 = CastUtils.toDouble0(texScon2.getText());
            double tot_senza_iva = importo_senza_iva - (importo_senza_iva / 100 * sconto1);
            tot_senza_iva = tot_senza_iva - (tot_senza_iva / 100 * sconto2);
//            if (main.fileIni.getValueBoolean("pref", "attivaArrotondamento", false)){
//                double parametro = Double.parseDouble(String.valueOf(comArrotondamento.getSelectedItem()));
//                boolean perDifetto = String.valueOf(comTipoArr.getSelectedItem()).equals("Inf.");
//                tot_senza_iva = InvoicexUtil.calcolaPrezzoArrotondato(tot_senza_iva, parametro, perDifetto);
//                this.texTotArrotondato.setText(FormatUtils.formatEuroIta(tot_senza_iva));
//            }

            tot_senza_iva = FormatUtils.round(tot_senza_iva * qta, 2);
            totale_imponibile = tot_senza_iva;

            double iva_prezz = 100d;
            try {
                iva_prezz = 100d + ((BigDecimal) DbUtils.getObject(Db.getConn(), "select percentuale from codici_iva where codice = " + Db.pc(texIva.getText(), Types.VARCHAR))).doubleValue();
            } catch (Exception ex) {
                System.out.println("iva non trovata:" + texIva.getText());
            }

//            double tot_con_iva = importo_senza_iva - (importo_senza_iva / 100 * sconto1);
//            tot_con_iva = tot_con_iva - (tot_con_iva / 100 * sconto2);
//            tot_con_iva = (tot_con_iva / 100d) * iva_prezz;
//            tot_con_iva = tot_con_iva * qta;
            double tot_con_iva = FormatUtils.round((tot_senza_iva / 100d) * iva_prezz, 2);
            totale_ivato = tot_con_iva;

            labTotale.setText("<html><table border='0' padding='0' margin='0'><tr><td align='right'>Totale senza iva: <b>" + FormatUtils.formatEuroItaMax5(tot_senza_iva) + "</b></td></tr><tr><td align='right'>Totale con iva: <b>" + FormatUtils.formatEuroItaMax5(tot_con_iva) + "</b></td></td></table></html>");
        } catch (Exception ex2) {
            labTotale.setText("");
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butCalcolaSconto;
    private javax.swing.JButton butNew;
    private javax.swing.JButton butSave;
    private javax.swing.JButton comIva;
    private tnxbeans.tnxComboField comUm;
    public tnxbeans.tnxDbPanel dati;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel110;
    private javax.swing.JLabel jLabel111;
    private javax.swing.JLabel jLabel113;
    private javax.swing.JLabel jLabel114;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JLabel labPercentualeIva;
    private javax.swing.JLabel labResidua;
    private javax.swing.JLabel labTotale;
    private tnxbeans.tnxTextField texAnno;
    private tnxbeans.tnxTextField texBolla;
    private tnxbeans.tnxTextField texCodiArti;
    private tnxbeans.tnxMemoField texDescrizione;
    private tnxbeans.tnxTextField texDisegno;
    private tnxbeans.tnxTextField texId;
    private tnxbeans.tnxTextField texIva;
    private tnxbeans.tnxTextField texMisura;
    private tnxbeans.tnxTextField texNume;
    private tnxbeans.tnxTextField texPrez;
    private tnxbeans.tnxTextField texPrez3;
    private javax.swing.JTextField texPrezNett;
    private tnxbeans.tnxTextField texQta;
    private tnxbeans.tnxTextField texQtaOmaggio;
    private javax.swing.JTextField texRicerca;
    private tnxbeans.tnxTextField texRiga;
    private tnxbeans.tnxTextField texScon1;
    private tnxbeans.tnxTextField texScon2;
    private tnxbeans.tnxTextField texSeri;
    private tnxbeans.tnxTextField texStat;
    private tnxbeans.tnxTextField texVariante;
    // End of variables declaration//GEN-END:variables

    public void setStato() {

        //controllo se inserimento o modifica
        if (dbStato == this.dati.DB_INSERIMENTO) {
            this.dati.dbOpen(Db.getConn(), "select * from " + this.dati.dbNomeTabella);
        } else {

            String sql = "select * from " + this.dati.dbNomeTabella;
            sql += " where serie = " + Db.pc(dbSerie, "VARCHAR");
            sql += " and numero = " + dbNumero;
            sql += " and anno = " + dbAnno;
            sql += " and riga = " + dbRiga;
            this.dati.dbOpen(Db.getConn(), sql);

        }

        this.dati.dbRefresh();

        //controllo come devo aprire
        if (dbStato == this.dati.DB_INSERIMENTO) {
            inserimento();
        } else {
//            comCodiArti_old = comCodiArti.getSelectedKey();
            comCodiArti_old = texCodiArti.getText();
        }

        aggiorna_iva();
        calcolaScontoDaPercSconto();
        aggiornaTotale();

        dati.dbForzaModificati();
    }

    private void inserimento() {
        this.dati.dbNew();
        this.texSeri.setText(this.dbSerie);
        this.texNume.setText(String.valueOf(dbNumero));
        this.texStat.setText(this.prevStato);
        comUm.setText(main.fileIni.getValue("varie", "umpred"));

        if (main.getPersonal().equals(main.PERSONAL_CUCINAIN)) {
            this.texIva.setText("10");
        } else {

            //carico da impostazioni
            Cliente cliente = new Cliente(this.codiceCliente);
            if (cliente.getTipoIva2().equals(Cliente.TIPO_IVA_ALTRO) && main.fileIni.getValueBoolean("pref", "controlliIva", true)) {
                this.texIva.setText("8");
            } else if (cliente.getTipoIva2().equals(Cliente.TIPO_IVA_CEE) && main.fileIni.getValueBoolean("pref", "controlliIva", true)) {
                this.texIva.setText("41");
            } else {
//                this.texIva.setText(Db.nz(main.fileIni.getValue("iva", "codiceIvaDefault"), "20"));
//                if (texIva.getText().trim().length() == 0) {
//                    this.texIva.setText("20");
//                }
                texIva.setText(InvoicexUtil.getIvaDefaultPassaggio());
            }
        }

        this.texAnno.setText(String.valueOf(dbAnno));

        //apre il resultset per ultimo +1
        java.sql.Statement stat;
        ResultSet resu;

        try {
            stat = Db.getConn().createStatement();
            if (dbRiga != 0) {
                this.texRiga.setText(String.valueOf(dbRiga));
            } else {
                String sql = "select riga from " + this.dati.dbNomeTabella;
                sql += " where serie = " + Db.pc(dbSerie, "VARCHAR");
                sql += " and numero = " + dbNumero;
                sql += " and anno = " + dbAnno;
                //sql += " and stato = " + Db.pc(prevStato, "VARCHAR");
                sql += " order by riga desc limit 1";
                resu = stat.executeQuery(sql);

                if (resu.next() == true) {
                    this.texRiga.setText(String.valueOf(resu.getInt(1) + 1));
                } else {
                    this.texRiga.setText("1");
                }
            }
        } catch (Exception err) {
            err.printStackTrace();
        }

        try {
            String tabPadre = "";

            if (this.dati.dbNomeTabella.equals("righ_fatt")) {
                tabPadre = "test_fatt";
            } else if (this.dati.dbNomeTabella.equals("righ_ordi")) {
                tabPadre = "test_ordi";
            } else if (this.dati.dbNomeTabella.equals("righ_ddt")) {
                tabPadre = "test_ddt";
            } else if (this.dati.dbNomeTabella.equals("righ_fatt_acquisto")) {
                tabPadre = "test_fatt_acquisto";
            }
            String sql = "select id from " + tabPadre + " where serie = '" + dbSerie + "' and anno = '" + dbAnno + "' and numero = '" + dbNumero + "'";

            ResultSet rs = Db.openResultSet(sql);
            if (rs.next()) {
                int idPadre = rs.getInt("id");
                texId.setText(String.valueOf(idPadre));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        main.events.fireInvoicexEvent(new InvoicexEvent(this, InvoicexEvent.TYPE_FRMNuovRigaDescrizioneMultiRigaNew_INSERIMENTO));
    }

    class AggiornaResiduaWorker extends SwingWorker {

        public String codart;

        @Override
        protected Object doInBackground() throws Exception {
            String ret = "";
            //calcolo quantità residua...
            ArrayList giacenza = Magazzino.getGiacenza(false, codart, null);
            DebugUtils.dump(giacenza);
            double giac = 0;
            try {
                giac = ((Giacenza) giacenza.get(0)).getGiacenza();
                DebugUtils.dump(giac);
                if (giac > 0) {
                    ret = "<html>Quantità residua: " + FormatUtils.formatPerc(giac) + "</html>";
                } else {
                    ret = "<html><font color='red'><b>Quantità residua: " + FormatUtils.formatPerc(giac) + "</b></font></html>";
                }
            } catch (Exception e) {
            }
            return ret;
        }

        @Override
        protected void done() {
            try {
                labResidua.setText((String) get());
            } catch (InterruptedException ex) {
                Logger.getLogger(frmNuovRigaDescrizioneMultiRigaNewFrajor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(frmNuovRigaDescrizioneMultiRigaNewFrajor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void recuperaDatiArticolo() {
        boolean servizio = false;

        aggiornaResidua = new AggiornaResiduaWorker();
        aggiornaResidua.codart = texCodiArti.getText();
        labResidua.setText("... aggiornamento quantità residua per " + aggiornaResidua.codart);
        aggiornaResidua.execute();

        if (this.texCodiArti.getText().length() > 0) {

            //li recupero dal cliente
            ResultSet temp;
            String sql = "select * from articoli";
            sql += " where codice = " + Db.pc(this.texCodiArti.getText(), "VARCHAR");
            temp = Db.openResultSet(sql);

            try {

                if (temp.next() == true) {

                    peso_kg_collo = temp.getDouble("peso_kg_collo");
                    servizio = CastUtils.toBoolean(temp.getString("servizio"));
                    //109
                    if (main.getPersonal().equalsIgnoreCase(main.PERSONAL_GIANNI)) {
                        this.comUm.setText(Db.nz(temp.getString("um"), ""));
                        this.texDescrizione.setText(Db.nz(temp.getString("descrizione"), ""));
                    } else {

                        boolean eng = false;

                        if (this.codiceCliente >= 0) {

                            Cliente cliente = new Cliente(this.codiceCliente);

                            if (cliente.isItalian() == true) {
                                eng = false;
                            } else {
//                                Preferences preferences = java.util.prefs.Preferences.userNodeForPackage(main.class);
//                                if (!preferences.getBoolean("soloItaliano", true)) {
                                if (!main.fileIni.getValueBoolean("pref", "soloItaliano", true)) {
                                    eng = true;
                                }
                            }
                        }

                        if (eng == true) {
                            this.comUm.setText(Db.nz(temp.getString("um_en"), ""));
                            this.texDescrizione.setText(Db.nz(temp.getString("descrizione_en"), ""));
                        } else {
                            this.comUm.setText(Db.nz(temp.getString("um"), ""));
                            this.texDescrizione.setText(Db.nz(temp.getString("descrizione"), ""));
                        }
                    }

                    Cliente cliente = new Cliente(this.codiceCliente);
                    if (cliente.getTipoIva2().equals(Cliente.TIPO_IVA_ALTRO) && main.fileIni.getValueBoolean("pref", "controlliIva", true)) {
                        this.texIva.setText("8");
                    } else if (cliente.getTipoIva2().equals(Cliente.TIPO_IVA_CEE) && main.fileIni.getValueBoolean("pref", "controlliIva", true)) {
                        this.texIva.setText("41");
                    } else {
                        this.texIva.setText(Db.formatNumero(temp.getDouble("iva")));
                    }

                    sql = "select prezzo, tipi_listino.* from articoli_prezzi left join tipi_listino on articoli_prezzi.listino = tipi_listino.codice";
                    sql += " where articolo = " + Db.pc(this.texCodiArti.getText(), "VARCHAR");
                    sql += " and listino = " + Db.pc(this.codiceListino, java.sql.Types.VARCHAR);

                    ResultSet prezzi = Db.openResultSet(sql);

                    if (prezzi.next() == true) {
                        this.texPrez.setText(Db.formatDecimal5(temp.getDouble("prezzo1")));
                        if (prezzi.getString("ricarico_flag") != null && prezzi.getString("ricarico_flag").equals("S") && !servizio) {
                            double perc = prezzi.getDouble("ricarico_perc");
                            double nuovo_prezzo = 0;
                            sql = "select prezzo from articoli_prezzi";
                            sql += " where articolo = " + Db.pc(this.texCodiArti.getText(), "VARCHAR");
                            sql += " and listino = " + Db.pc(prezzi.getString("ricarico_listino"), java.sql.Types.VARCHAR);
                            ResultSet prezzi2 = Db.openResultSet(sql);
                            prezzi2.next();
                            nuovo_prezzo = prezzi2.getDouble("prezzo") * ((perc + 100d) / 100d);
                            this.texPrez.setText(Db.formatDecimal5(nuovo_prezzo));
                        } else {
                            this.texPrez.setText(Db.formatDecimal5(prezzi.getDouble(1)));
                        }
                    } else {
                        sql = "select prezzo, tipi_listino.* from articoli_prezzi left join tipi_listino on articoli_prezzi.listino = tipi_listino.codice";
                        sql += " where articolo = " + Db.pc(this.texCodiArti.getText(), "VARCHAR");
                        sql += " and listino = " + Db.pc(main.getListinoBase(), java.sql.Types.VARCHAR);
                        prezzi = Db.openResultSet(sql);

                        if (prezzi.next() == true) {
                            this.texPrez.setText(Db.formatDecimal5(temp.getDouble("prezzo1")));
                            if (prezzi.getString("ricarico_flag") != null && prezzi.getString("ricarico_flag").equals("S") && !servizio) {
                                double perc = prezzi.getDouble("ricarico_perc");
                                double nuovo_prezzo = 0;
                                sql = "select prezzo from articoli_prezzi";
                                sql += " where articolo = " + Db.pc(this.texCodiArti.getText(), "VARCHAR");
                                sql += " and listino = " + Db.pc(main.getListinoBase(), java.sql.Types.VARCHAR);
                                ResultSet prezzi2 = Db.openResultSet(sql);
                                prezzi2.next();
                                nuovo_prezzo = prezzi2.getDouble("prezzo") * ((perc + 100d) / 100d);
                                this.texPrez.setText(Db.formatDecimal5(nuovo_prezzo));
                            } else {
                                this.texPrez.setText(Db.formatDecimal5(prezzi.getDouble(1)));
                            }
                        }
                    }
                    aggiorna_iva();
                    calcolaScontoDaPercSconto();

                } else {
                    javax.swing.JOptionPane.showMessageDialog(this, "Il codice articolo specificato non esiste in anagrafica !");
                }
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }

    private boolean controlli() {

        ResultSet temp = Db.lookUp(this.texIva.getText(), "codice", "codici_iva");

        try {

            if (temp == null) {

                if (this.texQta.getText().length() == 0 || this.texQta.getText().equals("0")) {

                    return true;
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this, "Attenzione, CODICE IVA non presente", "Attenzione", javax.swing.JOptionPane.ERROR_MESSAGE);

                    return false;
                }
            }
        } catch (Exception err) {
            javax.swing.JOptionPane.showMessageDialog(this, "Errore:" + err.toString());
            err.printStackTrace();

            return false;
        }

        return true;
    }
}
