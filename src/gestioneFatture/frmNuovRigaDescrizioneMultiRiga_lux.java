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
import gestioneFatture.logic.*;

import gestioneFatture.logic.clienti.Cliente;
import it.tnx.commons.CastUtils;
import it.tnx.invoicex.InvoicexUtil;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;


import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class frmNuovRigaDescrizioneMultiRiga_lux
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
    
    /** Creates new form frmNuovRiga */
    public frmNuovRigaDescrizioneMultiRiga_lux(Object from, String dbStato, String dbSerie, int dbNumero, String stato, int dbRiga, int dbAnno, String codiceListino, int codiceCliente) {
        initComponents();

        JTextField textCodiArti = (JTextField)comCodiArti.getComponent(2);
        textCodiArti.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                comCodiArtiFocusGained(e);
            }
            public void focusLost(FocusEvent e) {
            }
        });
        
        if (!main.getPersonalContain(main.PERSONAL_LUXURY)) {
            this.labAltraDescrizione.setVisible(false);
            this.texAltraDescrizione.setVisible(false);
        }
        
        //this.texPrezNett.setFont(new java.awt.Font(texPrezNett.getFont().getFamily(), texPrezNett.getFont().getSize(), java.awt.Font.ITALIC));
        this.comCodiArti.setDbRiempireForceText(true);
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
        if (from.getClass().getName().equalsIgnoreCase("gestioneFatture.frmTestDocu")) {
            this.dati.dbNomeTabella = "righ_ddt";
        } else if (from.getClass().getName().equalsIgnoreCase("gestioneFatture.frmTestFatt")) {
            this.dati.dbNomeTabella = "righ_fatt";
        } else if (from.getClass().getName().equalsIgnoreCase("gestioneFatture.frmTestOrdine")) {
            this.dati.dbNomeTabella = "righ_ordi";
        } else if (from.getClass().getName().equalsIgnoreCase("gestioneFatture.frmTestFattAcquisto")) {
            this.dati.dbNomeTabella = "righ_fatt_acquisto";
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "non trovata form partenza");
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
            this.comCodiArti.dbOpenList(Db.getConn(), "select descrizione, codice from articoli order by descrizione", null, false);
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
                this.comCodiArti.dbOpenList(Db.getConn(), "select descrizione_en, codice from articoli order by descrizione_en", null, false);
            } else {
                this.comUm.dbOpenList(Db.getConn(), "select um from articoli group by um", null, false);
                this.comCodiArti.dbOpenList(Db.getConn(), "select descrizione, codice from articoli order by descrizione", null, false);
            }
        }
        
        this.texAnno.setText(String.valueOf(dbAnno));
        
        //sistemo personali
        //if (main.getPersonal().equals(main.PERSONAL_GIANNI)) {
        if (main.getPersonal().equals(main.PERSONAL_GIANNI) || main.getPersonal()
        .equals(main.PERSONAL_TLZ)) {
            this.panPrezList.setVisible(true);
        } else {
            this.panPrezList.setVisible(false);
            
            //utilizzo la tabella articoli_listini
            this.texPezz.setVisible(false);
            this.labPezzi.setVisible(false);
        }
        
        //debbg
        //javax.swing.JOptionPane.showMessageDialog(null, Util.getIniValue("personalizzazioni","personalizzazioni"));
        //javax.swing.JOptionPane.showMessageDialog(null, String.valueOf(Util.getIniValue("personalizzazioni","personalizzazioni").indexOf("gianni1")));
        if (main.getPersonal().equals(main.PERSONAL_GIANNI) || main.getPersonal()
        .equals(main.PERSONAL_TLZ)) {
            this.labPrez1.setText("prezzo Rivenditori");
            this.labPrez2.setText("prezzo Utilizzatori");
        }
        
        //mette il focus dopo tutti gli eventi awt
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                texCodiArti.requestFocus();
            }
        });

    }
    
    private void calcolaSconto() {
        
        //calcola lo sconto
        try {
            
            java.text.NumberFormat form = java.text.NumberFormat.getInstance();
            this.texScon1.setText(Db.formatNumero(Util.getSconto(form.parse(texPrez3.getText()).doubleValue(), form.parse(texPrezNett.getText()).doubleValue())));
        } catch (Exception err) {
            Util.showErrorMsg(this, err);
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
        texScon1 = new tnxbeans.tnxTextField();
        texSeri = new tnxbeans.tnxTextField();
        texCodiArti = new tnxbeans.tnxTextField();
        comCodiArti = new tnxbeans.tnxComboField();
        texQta = new tnxbeans.tnxTextField();
        texPrez3 = new tnxbeans.tnxTextField();
        texIva = new tnxbeans.tnxTextField();
        texScon2 = new tnxbeans.tnxTextField();
        texStat = new tnxbeans.tnxTextField();
        texStat.setVisible(false);
        comUm = new tnxbeans.tnxComboField();
        texNume = new tnxbeans.tnxTextField();
        texRiga = new tnxbeans.tnxTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel110 = new javax.swing.JLabel();
        jLabel112 = new javax.swing.JLabel();
        texAnno = new tnxbeans.tnxTextField();
        texAnno.setVisible(false);
        panPrezList = new javax.swing.JPanel();
        labPrez1 = new javax.swing.JLabel();
        texPrez1 = new tnxbeans.tnxTextField();
        labPrez2 = new javax.swing.JLabel();
        texPrez2 = new tnxbeans.tnxTextField();
        texPezz = new tnxbeans.tnxTextField();
        texPezz.setEnabled(false);
        texPrezNett = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        labPezzi = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        labPercentualeIva = new javax.swing.JLabel();
        comIva = new javax.swing.JButton();
        texAltraDescrizione = new tnxbeans.tnxTextField();
        labAltraDescrizione = new javax.swing.JLabel();
        butCalcolaSconto = new javax.swing.JButton();
        texDescrizione = new tnxbeans.tnxMemoField();
        jLabel20 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        texPrez = new tnxbeans.tnxTextField();
        jLabel113 = new javax.swing.JLabel();
        jLabel111 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        butNew = new javax.swing.JButton();
        butSave = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Dettaglio riga");
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

        jPanel1.setLayout(new java.awt.BorderLayout());

        dati.setEnabled(false);

        texScon1.setText("sconto1");
        texScon1.setDbDescCampo("");
        texScon1.setDbNomeCampo("sconto1");
        texScon1.setDbTipoCampo("numerico");

        texSeri.setBackground(new java.awt.Color(204, 204, 204));
        texSeri.setColumns(5);
        texSeri.setEditable(false);
        texSeri.setText("serie");
        texSeri.setDbDescCampo("");
        texSeri.setDbNomeCampo("serie");
        texSeri.setDbTipoCampo("");

        texCodiArti.setText("codice_articolo");
        texCodiArti.setDbComboAbbinata(comCodiArti);
        texCodiArti.setDbDescCampo("");
        texCodiArti.setDbNomeCampo("codice_articolo");
        texCodiArti.setDbTipoCampo("");
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

        comCodiArti.setToolTipText("premere INVIO per selezionare l'articolo");
        comCodiArti.setDbDescCampo("");
        comCodiArti.setDbNomeCampo("descrizione");
        comCodiArti.setDbSalvaKey(false);
        comCodiArti.setDbSalvare(false);
        comCodiArti.setDbTextAbbinato(texCodiArti);
        comCodiArti.setDbTrovaMentreScrive(true);
        comCodiArti.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comCodiArtiItemStateChanged(evt);
            }
        });
        comCodiArti.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                comCodiArtiFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                comCodiArtiFocusLost(evt);
            }
        });
        comCodiArti.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                comCodiArtiKeyPressed(evt);
            }
        });

        texQta.setText("quantita");
        texQta.setDbDescCampo("");
        texQta.setDbNomeCampo("quantita");
        texQta.setDbTipoCampo("numerico");
        texQta.setNextFocusableComponent(texPrez);
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

        texPrez3.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        texPrez3.setText("prezzo lordo");
        texPrez3.setDbDescCampo("");
        texPrez3.setDbNomeCampo("prezzo_lordo");
        texPrez3.setDbRiempire(false);
        texPrez3.setDbSalvare(false);
        texPrez3.setDbTipoCampo("valuta");
        texPrez3.setNextFocusableComponent(texPrezNett);
        texPrez3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                texPrez3FocusLost(evt);
            }
        });
        texPrez3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                texPrez3KeyReleased(evt);
            }
        });

        texIva.setText("iva");
        texIva.setToolTipText("premere F4 per avere la lista dei codici iva");
        texIva.setDbDescCampo("");
        texIva.setDbNomeCampo("iva");
        texIva.setDbTipoCampo("numerico");
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

        texScon2.setText("sconto2");
        texScon2.setDbDescCampo("");
        texScon2.setDbNomeCampo("sconto2");
        texScon2.setDbTipoCampo("numerico");

        texStat.setBackground(new java.awt.Color(255, 200, 200));
        texStat.setText("stato");
        texStat.setDbDescCampo("");
        texStat.setDbNomeCampo("stato");
        texStat.setDbTipoCampo("");

        comUm.setDbDescCampo("unità di misura");
        comUm.setDbNomeCampo("um");
        comUm.setDbRiempireForceText(true);
        comUm.setDbSalvaKey(false);
        comUm.setDbTipoCampo("");

        texNume.setEditable(false);
        texNume.setText("numero");
        texNume.setDbDescCampo("");
        texNume.setDbNomeCampo("numero");
        texNume.setDbTipoCampo("testo");

        texRiga.setText("riga");
        texRiga.setDbDescCampo("");
        texRiga.setDbNomeCampo("riga");
        texRiga.setDbTipoCampo("");

        jLabel1.setText("Iva");

        jLabel11.setText("documento");

        jLabel12.setText("numero");

        jLabel13.setText("riga");

        jLabel15.setText("Ricerca articolo");

        jLabel17.setText("unità di misura");

        jLabel110.setText("quantita");

        jLabel112.setText("serie");

        texAnno.setBackground(new java.awt.Color(255, 200, 200));
        texAnno.setText("anno");
        texAnno.setDbDescCampo("");
        texAnno.setDbNomeCampo("anno");
        texAnno.setDbRiempire(false);
        texAnno.setDbTipoCampo("");

        panPrezList.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panPrezList.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labPrez1.setForeground(new java.awt.Color(102, 102, 102));
        labPrez1.setText("prezzo 1");
        panPrezList.add(labPrez1, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 5, 115, 20));

        texPrez1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        texPrez1.setText("0");
        texPrez1.setDbDescCampo("");
        texPrez1.setDbNomeCampo("prezzo");
        texPrez1.setDbRiempire(false);
        texPrez1.setDbSalvare(false);
        texPrez1.setDbTipoCampo("valuta");
        texPrez1.setEnabled(false);
        panPrezList.add(texPrez1, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 5, 85, -1));

        labPrez2.setForeground(new java.awt.Color(102, 102, 102));
        labPrez2.setText("prezzo 2");
        panPrezList.add(labPrez2, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 30, 115, 20));

        texPrez2.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        texPrez2.setText("0");
        texPrez2.setDbDescCampo("");
        texPrez2.setDbNomeCampo("prezzo");
        texPrez2.setDbRiempire(false);
        texPrez2.setDbSalvare(false);
        texPrez2.setDbTipoCampo("valuta");
        texPrez2.setEnabled(false);
        panPrezList.add(texPrez2, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 30, 85, -1));

        texPezz.setDbNomeCampo("pezzi");
        texPezz.setDbRiempire(false);
        texPezz.setDbSalvare(false);
        texPezz.setDbTipoCampo("");

        texPrezNett.setBackground(new java.awt.Color(255, 204, 204));
        texPrezNett.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        texPrezNett.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                texPrezNettKeyPressed(evt);
            }
        });

        jLabel2.setText("- premere F4 sul codice articolo o sulla descrizione per avere la lista articoli filtrata");

        jLabel3.setText("- inserire nella casella prezzo netto il prezzo a cui si desidera arrivare, premendo invio calcola lo sconto da applicare");

        jLabel4.setText("prezzo netto");

        labPezzi.setForeground(new java.awt.Color(102, 102, 102));
        labPezzi.setText("pezzi per confezione");

        jButton1.setFont(new java.awt.Font("Dialog", 0, 10));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/edit-find.png"))); // NOI18N
        jButton1.setText("visualizza prezzi precedenti");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        labPercentualeIva.setText("%");

        comIva.setText("...");
        comIva.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comIvaActionPerformed(evt);
            }
        });

        texAltraDescrizione.setText("descrizione2");
        texAltraDescrizione.setDbDescCampo("");
        texAltraDescrizione.setDbNomeCampo("descrizione2");
        texAltraDescrizione.setDbTipoCampo("");

        labAltraDescrizione.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labAltraDescrizione.setText("Altra desc.");

        butCalcolaSconto.setText("C");
        butCalcolaSconto.setToolTipText("Calcola sconto");
        butCalcolaSconto.setMargin(new java.awt.Insets(1, 1, 1, 1));
        butCalcolaSconto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butCalcolaScontoActionPerformed(evt);
            }
        });

        texDescrizione.setDbNomeCampo("descrizione");
        texDescrizione.setNextFocusableComponent(comUm);

        jLabel20.setText("descrizione articolo/riga");

        jLabel16.setText("Cod. Art.");

        jButton2.setText("aggiungi numero colli");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel18.setText("sco/ric 1");
        jLabel18.setToolTipText("Sconto o ricarico (in negativo)");

        jLabel19.setText("sco/ric 2");
        jLabel19.setToolTipText("Sconto o ricarico (in negativo)");

        texPrez.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        texPrez.setText("prezzo");
        texPrez.setDbDescCampo("");
        texPrez.setDbNomeCampo("prezzo");
        texPrez.setDbTipoCampo("valuta");
        texPrez.setNextFocusableComponent(texPrez3);
        texPrez.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                texPrezFocusLost(evt);
            }
        });
        texPrez.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                texPrezKeyReleased(evt);
            }
        });

        jLabel113.setText("prezzo senza iva");

        jLabel111.setText("prezzo con iva");

        org.jdesktop.layout.GroupLayout datiLayout = new org.jdesktop.layout.GroupLayout(dati);
        dati.setLayout(datiLayout);
        datiLayout.setHorizontalGroup(
            datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(datiLayout.createSequentialGroup()
                .add(10, 10, 10)
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(datiLayout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(datiLayout.createSequentialGroup()
                                .add(5, 5, 5)
                                .add(jLabel11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 75, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(datiLayout.createSequentialGroup()
                                .add(jLabel112, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(jLabel12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .add(15, 15, 15)
                        .add(jLabel13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(95, 95, 95)
                        .add(texStat, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 75, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(datiLayout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(texSeri, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(5, 5, 5)
                        .add(texNume, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 45, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(20, 20, 20)
                        .add(texRiga, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(100, 100, 100)
                        .add(texAnno, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 75, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(datiLayout.createSequentialGroup()
                        .add(jLabel16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 90, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(5, 5, 5)
                        .add(jLabel15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 205, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(10, 10, 10)
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jButton2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 185, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButton1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 185, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
            .add(datiLayout.createSequentialGroup()
                .add(10, 10, 10)
                .add(texCodiArti, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 95, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(comCodiArti, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 425, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(datiLayout.createSequentialGroup()
                .add(10, 10, 10)
                .add(jLabel20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 205, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(datiLayout.createSequentialGroup()
                .add(10, 10, 10)
                .add(texDescrizione, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(96, 96, 96))
            .add(datiLayout.createSequentialGroup()
                .add(180, 180, 180)
                .add(jLabel113, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 90, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(datiLayout.createSequentialGroup()
                .add(10, 10, 10)
                .add(jLabel17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 90, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(10, 10, 10)
                .add(jLabel110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(texPrez, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 85, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(5, 5, 5)
                .add(jLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 90, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(15, 15, 15)
                .add(jLabel18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 55, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jLabel19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 55, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(5, 5, 5)
                .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(datiLayout.createSequentialGroup()
                .add(10, 10, 10)
                .add(comUm, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 90, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(10, 10, 10)
                .add(texQta, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 65, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(5, 5, 5)
                .add(jLabel111, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(20, 20, 20)
                .add(texPrezNett, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(butCalcolaSconto, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(5, 5, 5)
                .add(texScon1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(5, 5, 5)
                .add(texScon2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(5, 5, 5)
                .add(texIva, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(comIva, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(5, 5, 5)
                .add(labPercentualeIva, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(datiLayout.createSequentialGroup()
                .add(180, 180, 180)
                .add(texPrez3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 85, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(datiLayout.createSequentialGroup()
                .add(10, 10, 10)
                .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 430, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(datiLayout.createSequentialGroup()
                .add(10, 10, 10)
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(datiLayout.createSequentialGroup()
                        .add(470, 470, 470)
                        .add(labPezzi, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 575, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
            .add(datiLayout.createSequentialGroup()
                .add(20, 20, 20)
                .add(labAltraDescrizione, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 90, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(texAltraDescrizione, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 425, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(datiLayout.createSequentialGroup()
                        .add(250, 250, 250)
                        .add(panPrezList, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 220, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(datiLayout.createSequentialGroup()
                        .add(370, 370, 370)
                        .add(texPezz, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
        );
        datiLayout.setVerticalGroup(
            datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(datiLayout.createSequentialGroup()
                .add(5, 5, 5)
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(datiLayout.createSequentialGroup()
                        .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(datiLayout.createSequentialGroup()
                                .add(jLabel11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jLabel112, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(jLabel12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                            .add(datiLayout.createSequentialGroup()
                                .add(15, 15, 15)
                                .add(jLabel13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(datiLayout.createSequentialGroup()
                                .add(5, 5, 5)
                                .add(texStat, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(texSeri, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(texNume, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(texRiga, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(texAnno, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(5, 5, 5)
                        .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(datiLayout.createSequentialGroup()
                        .add(15, 15, 15)
                        .add(jButton2)
                        .add(7, 7, 7)
                        .add(jButton1)))
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(texCodiArti, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(comCodiArti, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(jLabel20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(texDescrizione, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
                .add(10, 10, 10)
                .add(jLabel113, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(texPrez, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(comUm, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(texQta, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(datiLayout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(jLabel111, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(texPrezNett, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(butCalcolaSconto, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(texScon1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(texScon2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(texIva, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(comIva, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(labPercentualeIva, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(texPrez3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(5, 5, 5)
                .add(jLabel2)
                .add(1, 1, 1)
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(datiLayout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(labPezzi, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jLabel3))
                .add(5, 5, 5)
                .add(datiLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(datiLayout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(labAltraDescrizione, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(datiLayout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(texAltraDescrizione, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(datiLayout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(panPrezList, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(texPezz, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(99, 99, 99))
        );

        jPanel1.add(dati, java.awt.BorderLayout.CENTER);

        butNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/edit-undo.png"))); // NOI18N
        butNew.setText("Annulla");
        butNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butNewActionPerformed(evt);
            }
        });
        jPanel2.add(butNew);

        butSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/devices/media-floppy.png"))); // NOI18N
        butSave.setText("Salva");
        butSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSaveActionPerformed(evt);
            }
        });
        jPanel2.add(butSave);

        jPanel1.add(jPanel2, java.awt.BorderLayout.SOUTH);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void texCodiArtiFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texCodiArtiFocusGained
        comCodiArti_old = texCodiArti.getText();
    }//GEN-LAST:event_texCodiArtiFocusGained
    
    private void comCodiArtiFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_comCodiArtiFocusGained
        comCodiArti_old = comCodiArti.getSelectedKey();
    }//GEN-LAST:event_comCodiArtiFocusGained
    
    private void butCalcolaScontoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butCalcolaScontoActionPerformed
        calcolaSconto();
    }//GEN-LAST:event_butCalcolaScontoActionPerformed
    
    private void texIvaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texIvaFocusLost
        
        //trova la percentuale dell'iva
        Iva iva = new Iva();
        iva.load(Db.INSTANCE, this.texIva.getText());
        
        java.text.DecimalFormat decformat = new java.text.DecimalFormat("##0");
        this.labPercentualeIva.setText(decformat.format(iva.getPercentuale()) + "%");
    }//GEN-LAST:event_texIvaFocusLost
    
    private void comIvaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comIvaActionPerformed
        
        frmListIva frm = new frmListIva(this.texIva, this.labPercentualeIva, this);
        
        main.getPadre().openFrame(frm, 400, 200, texIva.getLocationOnScreen().y, texIva.getLocationOnScreen().x);
    }//GEN-LAST:event_comIvaActionPerformed
    
    private void texIvaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texIvaKeyPressed
        
        if (evt.getKeyCode() == evt.VK_F4) {
            comIvaActionPerformed(null);
        }
    }//GEN-LAST:event_texIvaKeyPressed
    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        showPrezziFatture();
    }//GEN-LAST:event_jButton1ActionPerformed
    
    private void texPrezNettKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texPrezNettKeyPressed
        
        if (evt.getKeyCode() == 10) {
            calcolaSconto();
        }
    }//GEN-LAST:event_texPrezNettKeyPressed
    
    private void texCodiArtiFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texCodiArtiFocusLost
        if (comCodiArti_old != null && !comCodiArti_old.equals(texCodiArti.getText())) {
            recuperaDatiArticolo();
        }
    }//GEN-LAST:event_texCodiArtiFocusLost
    
    private void texCodiArtiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texCodiArtiKeyPressed
        
        // Add your handling code here:
        if (evt.getKeyCode() == evt.VK_ENTER) {
            recuperaDatiArticolo();
        } else if (evt.getKeyCode() == evt.VK_F4) {
            
            java.util.Hashtable colsWidthPerc = new java.util.Hashtable();
            colsWidthPerc.put("codice", new Double(20));
            colsWidthPerc.put("descrizione", new Double(80));
            
            String sql = "select codice, descrizione from articoli" + " where codice like '" + Db.aa(this.texCodiArti.getText()) + "%'" + " order by codice, descrizione";
            frmDbListSmall temp = new frmDbListSmall(main.getPadre(), true, sql, this.texCodiArti, 0, colsWidthPerc, 50, 200, 900, 500);
            this.texCodiArti.requestFocus();
            recuperaDatiArticolo();
            this.comCodiArti.dbTrovaKey(this.texCodiArti.getText());
            this.comCodiArti.requestFocus();
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    texQta.requestFocus();
                }
            });
        }
    }//GEN-LAST:event_texCodiArtiKeyPressed
    
    private void comCodiArtiFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_comCodiArtiFocusLost
        if (comCodiArti_old != null && !comCodiArti_old.equals(comCodiArti.getSelectedKey())) {
            recuperaDatiArticolo();
        }
    }//GEN-LAST:event_comCodiArtiFocusLost
    
    private void comCodiArtiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_comCodiArtiKeyPressed
        
        // Add your handling code here:
        if (evt.getKeyCode() == evt.VK_ENTER) {
            
            //  this.texCodiArti.setText(comCodiArti.getSelectedKey().toString());
            recuperaDatiArticolo();
        } else if (evt.getKeyCode() == evt.VK_F4) {
            
            java.util.Hashtable colsWidthPerc = new java.util.Hashtable();
            colsWidthPerc.put("codice", new Double(20));
            colsWidthPerc.put("descrizione", new Double(80));
            
            String sql = "select codice, descrizione from articoli" + " where descrizione like '" + Db.aa(this.comCodiArti.getText()) + "%'" + " order by descrizione, codice";
            
            //frmDbListSmall temp = new frmDbListSmall(main.getPadre(), true, sql, this.texCodiArti, 0, colsWidthPerc, 200,200, 550, 400);
            frmDbListSmall temp = new frmDbListSmall(main.getPadre(), true, sql, this.texCodiArti, 0, colsWidthPerc, 50, 200, 900, 500);
            this.texCodiArti.requestFocus();
            recuperaDatiArticolo();
            this.comCodiArti.dbTrovaKey(this.texCodiArti.getText());
            this.comCodiArti.requestFocus();
            this.requestFocus();
            this.comCodiArti.requestFocus();
        }
    }//GEN-LAST:event_comCodiArtiKeyPressed
    
    private void comCodiArtiItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comCodiArtiItemStateChanged
        
    }//GEN-LAST:event_comCodiArtiItemStateChanged
    
    private void texCodiArtiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texCodiArtiActionPerformed
        
        // Add your handling code here:
    }//GEN-LAST:event_texCodiArtiActionPerformed
    
    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        
        // Add your handling code here:
        main.getPadre().closeFrame(this);
    }//GEN-LAST:event_formInternalFrameClosed
    
    private void butNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butNewActionPerformed
        this.dispose();
    }//GEN-LAST:event_butNewActionPerformed
    
    private void butSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveActionPerformed
        String nomeTab = "righ_fatt_matricole";
        
        //debug
        System.out.println(this.comCodiArti.getText());
        
        boolean aprireMatricolePre = false;
        if (controlli()) {
            this.dati.dbSave();
            
            if (from.getClass().getName().equalsIgnoreCase("gestionepreventivi.frmOrdiTest")) {
                javax.swing.JOptionPane.showMessageDialog(this, "non trovato form di partenza");
            } else if (from.getClass().getName().equalsIgnoreCase("gestioneFatture.frmTestDocu")) {
                frmTestDocu tempFrom = (frmTestDocu)from;
                tempFrom.griglia.dbRefresh();
                tempFrom.prev.dbRefresh();
                tempFrom.ricalcolaTotali();
                aprireMatricolePre = true;
            } else if (from.getClass().getName().equalsIgnoreCase("gestioneFatture.frmTestFatt")) {
                frmTestFatt tempFrom = (frmTestFatt)from;
                tempFrom.griglia.dbRefresh();
                tempFrom.prev.dbRefresh();
                tempFrom.ricalcolaTotali();
                
                //vado a cercare nei ddt se ce ne è almeno uno attaccato a questa fattura
                int conta = 0;
                try {
                    String sql = "select count(*) from test_ddt" +
                            " where fattura_serie = '" + texSeri.getText() + "'" +
                            " and fattura_numero = " + texNume.getText() +
                            " and fattura_anno = " + texAnno.getText();
                    ResultSet r = Db.openResultSet(sql);
                    if (r.next()) conta = r.getInt(1);
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
                frmTestFattAcquisto tempFrom = (frmTestFattAcquisto)from;
                tempFrom.griglia.dbRefresh();
                tempFrom.prev.dbRefresh();
                tempFrom.ricalcolaTotali();
                aprireMatricolePre = true;
            } else if (from.getClass().getName().equalsIgnoreCase("gestioneFatture.frmTestOrdine")) {
                frmTestOrdine tempFrom = (frmTestOrdine)from;
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
                        if (r.getString(1).equalsIgnoreCase("S")) aprireMatricole = true;
                    }
                } catch (SQLException err) {
                    err.printStackTrace();
                } finally {
                    try {
                        r.close();
                        s.close();
                    } catch (Exception err) {}
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
//                for (int i = 0; i < dialogMatricole.model.getRowCount(); i++) {
//                    String matr = "";
//                    if (dialogMatricole.model.getValueAt(i, 0) instanceof Giacenza) {
//                        Giacenza giac = (Giacenza)dialogMatricole.model.getValueAt(i, 0);
//                        matr = giac.getMatricola();
//                    } else {
//                        matr = (String)dialogMatricole.model.getValueAt(i, 0);
//                    }
//
//                    //System.out.println("dalfare:" + matr + " " + texSeri.getText() + " " + texNume.getText() + " " + texAnno.getText());
//                    String sql = "insert into " + nomeTab + " values (";
//                    sql += " " + Db.pc(texSeri.getText(), Types.VARCHAR);
//                    sql += " , " + Db.pc(texNume.getText(), Types.INTEGER);
//                    sql += " , " + Db.pc(texAnno.getText(), Types.INTEGER);
//                    sql += " , " + Db.pc(texRiga.getText(), Types.INTEGER);
//                    sql += " , " + Db.pc(matr, Types.VARCHAR);
//                    sql += ")";
//                    Db.executeSql(sql);
//                }
//            }
            
        }
    }//GEN-LAST:event_butSaveActionPerformed

private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
    if (peso_kg_collo != 0) {
        double qta = it.tnx.Util.getDouble(texQta.getText());
        double collid = qta / peso_kg_collo;
        int colli = ((Double)Math.ceil(collid)).intValue();
        texDescrizione.setText(texDescrizione.getText() + "\n" + "Numero colli: " + colli);
    } else {
        JOptionPane.showMessageDialog(this, "Non è inserito il peso per collo in anagrafica articoli", "Attenzione", JOptionPane.INFORMATION_MESSAGE);
    }
}//GEN-LAST:event_jButton2ActionPerformed

private void texQtaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texQtaActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_texQtaActionPerformed

public void aggiorna_iva(){
    double iva_prezz = 100 + Db.getDouble(this.texIva.getText());

    double new_prezz_lordo = 0;
    double prezz_netto = Db.getDouble(this.texPrez.getText());

    if (prezz_netto >= 0) {
        new_prezz_lordo = (double) (prezz_netto / 100d) * iva_prezz;
        this.texPrez3.setText(Db.formatValuta(new_prezz_lordo));
    }
}

private void texPrez3KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texPrez3KeyReleased
    // TODO add your handling code here:
    double iva_prezz = 100 + Db.getDouble(this.texIva.getText());

    double new_prezz_netto = 0;
    double prezz_lordo = Db.getDouble(this.texPrez3.getText());

    if (prezz_lordo > 0) {
        new_prezz_netto = (double) (prezz_lordo * 100d) / iva_prezz;
        this.texPrez.setText(Db.formatValuta(new_prezz_netto));
    }
}//GEN-LAST:event_texPrez3KeyReleased

private void texPrez3FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texPrez3FocusLost
    // TODO add your handling code here:
    double iva_prezz = 100 + Db.getDouble(this.texIva.getText());

    double new_prezz_netto = 0;
    double prezz_lordo = Db.getDouble(this.texPrez3.getText());

    if (prezz_lordo > 0) {
        new_prezz_netto = (double) (prezz_lordo * 100d) / iva_prezz;
        this.texPrez.setText(Db.formatValuta(new_prezz_netto));
    }
}//GEN-LAST:event_texPrez3FocusLost

private void texPrezFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texPrezFocusLost
    // TODO add your handling code here:
    aggiorna_iva();
}//GEN-LAST:event_texPrezFocusLost

private void texPrezKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_texPrezKeyReleased
    // TODO add your handling code here:
    aggiorna_iva();
}//GEN-LAST:event_texPrezKeyReleased

private void texQtaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texQtaFocusLost
    // TODO add your handling code here:
}//GEN-LAST:event_texQtaFocusLost
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butCalcolaSconto;
    private javax.swing.JButton butNew;
    private javax.swing.JButton butSave;
    private tnxbeans.tnxComboField comCodiArti;
    private javax.swing.JButton comIva;
    private tnxbeans.tnxComboField comUm;
    private tnxbeans.tnxDbPanel dati;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel110;
    private javax.swing.JLabel jLabel111;
    private javax.swing.JLabel jLabel112;
    private javax.swing.JLabel jLabel113;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel labAltraDescrizione;
    private javax.swing.JLabel labPercentualeIva;
    private javax.swing.JLabel labPezzi;
    private javax.swing.JLabel labPrez1;
    private javax.swing.JLabel labPrez2;
    private javax.swing.JPanel panPrezList;
    private tnxbeans.tnxTextField texAltraDescrizione;
    private tnxbeans.tnxTextField texAnno;
    private tnxbeans.tnxTextField texCodiArti;
    private tnxbeans.tnxMemoField texDescrizione;
    private tnxbeans.tnxTextField texIva;
    private tnxbeans.tnxTextField texNume;
    private tnxbeans.tnxTextField texPezz;
    private tnxbeans.tnxTextField texPrez;
    private tnxbeans.tnxTextField texPrez1;
    private tnxbeans.tnxTextField texPrez2;
    private tnxbeans.tnxTextField texPrez3;
    private javax.swing.JTextField texPrezNett;
    private tnxbeans.tnxTextField texQta;
    private tnxbeans.tnxTextField texRiga;
    private tnxbeans.tnxTextField texScon1;
    private tnxbeans.tnxTextField texScon2;
    private tnxbeans.tnxTextField texSeri;
    private tnxbeans.tnxTextField texStat;
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
            this.dati.dbNew();
            this.texSeri.setText(this.dbSerie);
            this.texNume.setText(String.valueOf(dbNumero));
            this.texStat.setText(this.prevStato);
            
            if (main.getPersonal().equals(main.PERSONAL_CUCINAIN)) {
                this.texIva.setText("10");
            } else {
                
                //carico da impostazioni
//                this.texIva.setText(Db.nz(main.fileIni.getValue("iva", "codiceIvaDefault"), "20"));
//                if (texIva.getText().trim().length() == 0) {
//                    this.texIva.setText("20");
//                }

                texIva.setText(InvoicexUtil.getIvaDefaultPassaggio());
                
            }
            
            this.texAnno.setText(String.valueOf(dbAnno));
            
            //apre il resultset per ultimo +1
            java.sql.Statement stat;
            ResultSet resu;
            
            try {
                stat = Db.getConn().createStatement();
                
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
            } catch (Exception err) {
                err.printStackTrace();
            }
        } else {
            comCodiArti_old = comCodiArti.getSelectedKey();
        }

        aggiorna_iva();
    }
    
    private void recuperaDatiArticolo() {
        boolean servizio = false;
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
                    
                    this.texIva.setText(Db.formatNumero(temp.getDouble("iva")));
                    
                    //if (main.getPersonal().equals(main.PERSONAL_GIANNI) || main.getPersonal().equals(main.PERSONAL_CUCINAIN)) {
                    if (main.getPersonal().equals(main.PERSONAL_GIANNI) || main
                            .getPersonal().equals(main.PERSONAL_CUCINAIN) || main.getPersonal()
                            .equals(main.PERSONAL_TLZ) || main.pluginClientManager) {
                        
                        //controllo il cliente di che tipo e'
                        if (main.getPersonal().equals(main.PERSONAL_GIANNI)) {
                            this.texPrez3.setText(Db.formatDecimal5(temp.getDouble("prezzo1")));
                        } else {
                            this.texPrez3.setText(Db.formatDecimal5(temp.getDouble("prezzo2")));
                        }
                        
                        this.texPrez1.setText(Db.formatDecimal5(temp.getDouble("prezzo1")));
                        this.texPrez2.setText(Db.formatDecimal5(temp.getDouble("prezzo2")));
                        
                        try {
                            this.texPrez3.setText(Db.formatDecimal5(temp.getDouble("prezzo" + this.codiceListino)));
                        } catch (Exception err2) {
                        }
                    } else {
                        sql = "select prezzo, tipi_listino.* from articoli_prezzi left join tipi_listino on articoli_prezzi.listino = tipi_listino.codice";
                        sql += " where articolo = " + Db.pc(this.texCodiArti.getText(), "VARCHAR");
                        sql += " and listino = " + Db.pc(this.codiceListino, java.sql.Types.VARCHAR);

                        ResultSet prezzi = Db.openResultSet(sql);

                        if (prezzi.next() == true) {
                            this.texPrez3.setText(Db.formatDecimal5(temp.getDouble("prezzo1")));
                            if (prezzi.getString("ricarico_flag") != null && prezzi.getString("ricarico_flag").equals("S") && !servizio) {
                                double perc = prezzi.getDouble("ricarico_perc");
                                double nuovo_prezzo = 0;
                                sql = "select prezzo from articoli_prezzi";
                                sql += " where articolo = " + Db.pc(this.texCodiArti.getText(), "VARCHAR");
                                sql += " and listino = " + Db.pc(prezzi.getString("ricarico_listino"), java.sql.Types.VARCHAR);
                                ResultSet prezzi2 = Db.openResultSet(sql);
                                prezzi2.next();
                                nuovo_prezzo = prezzi2.getDouble("prezzo") * ((perc + 100d) / 100d);
                                this.texPrez3.setText(Db.formatDecimal5(nuovo_prezzo));
                            } else {
                                this.texPrez3.setText(Db.formatDecimal5(prezzi.getDouble(1)));
                            }
                        } else {
                            sql = "select prezzo, tipi_listino.* from articoli_prezzi left join tipi_listino on articoli_prezzi.listino = tipi_listino.codice";
                            sql += " where articolo = " + Db.pc(this.texCodiArti.getText(), "VARCHAR");
                            sql += " and listino = " + Db.pc(main.getListinoBase(), java.sql.Types.VARCHAR);
                            prezzi = Db.openResultSet(sql);

                            if (prezzi.next() == true) {
                                this.texPrez3.setText(Db.formatDecimal5(temp.getDouble("prezzo1")));
                                if (prezzi.getString("ricarico_flag") != null && prezzi.getString("ricarico_flag").equals("S") && !servizio) {
                                    double perc = prezzi.getDouble("ricarico_perc");
                                    double nuovo_prezzo = 0;
                                    sql = "select prezzo from articoli_prezzi";
                                    sql += " where articolo = " + Db.pc(this.texCodiArti.getText(), "VARCHAR");
                                    sql += " and listino = " + Db.pc(main.getListinoBase(), java.sql.Types.VARCHAR);
                                    ResultSet prezzi2 = Db.openResultSet(sql);
                                    prezzi2.next();
                                    nuovo_prezzo = prezzi2.getDouble("prezzo") * ((perc + 100d) / 100d);
                                    this.texPrez3.setText(Db.formatDecimal5(nuovo_prezzo));
                                } else {
                                    this.texPrez3.setText(Db.formatDecimal5(prezzi.getDouble(1)));
                                }
                            }
                        }

                    }
                    
                    this.texPezz.setText(temp.getString("pezzi"));
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
                
                if (this.texQta.getText().length() == 0 || this.texQta.getText()
                .equals("0")) {
                    
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
