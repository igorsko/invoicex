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
import java.awt.*;
import java.awt.event.*;

import java.beans.*;

import java.sql.*;

import java.util.Vector;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import tnxbeans.*;

public class frmFine
    extends JInternalFrame {

    Border border1;
    BorderLayout borderLayout1 = new BorderLayout();
    JPanel panTop = new JPanel();
    JToolBar tooTop = new JToolBar();
    BorderLayout borderLayout2 = new BorderLayout();
    JButton butNew = new JButton();
    JButton butSave = new JButton();
    JButton butUndo = new JButton();
    JSplitPane jSplitPane1 = new JSplitPane();
    JPanel jPanel1 = new JPanel();
    JScrollPane jScrollPane1 = new JScrollPane();
    BorderLayout borderLayout3 = new BorderLayout();
    tnxDbGrid griglia = new tnxDbGrid();
    tnxTextField dbCodice = new tnxTextField();
    tnxDbPanel dati = new tnxDbPanel();
    JLabel jLabel4 = new JLabel();
    JLabel jLabel1 = new JLabel();
    JButton butFind = new JButton();
    Component component1;
    Component component2;
    JButton butDele = new JButton();
    Component component3;
    tnxComboField comTipoArti = new tnxComboField();
    tnxComboField comTipoLegn = new tnxComboField();
    JLabel jLabel2 = new JLabel();
    JLabel jLabel3 = new JLabel();
    tnxTextField tnxTextField1 = new tnxTextField();
    JLabel jLabel5 = new JLabel();
    tnxTextField tnxTextField2 = new tnxTextField();
    JLabel jLabel6 = new JLabel();
    tnxComboField comAnte = new tnxComboField();
    JLabel jLabel7 = new JLabel();
    tnxTextField tnxTextField3 = new tnxTextField();
    JLabel jLabel8 = new JLabel();
    tnxTextField tnxTextField4 = new tnxTextField();
    JLabel jLabel9 = new JLabel();
    tnxTextField tnxTextField5 = new tnxTextField();
    tnxMemoField tnxMemoField1 = new tnxMemoField();
    JLabel jLabel10 = new JLabel();

    public frmFine() {

        try {
            jbInit();

            //associo il panel ai dati
            this.dati.dbNomeTabella = "finestre";

            Vector chiave = new Vector();
            chiave.add("id");
            this.dati.dbChiave = chiave;
            this.dati.butSave = this.butSave;
            this.dati.butUndo = this.butUndo;
            this.dati.dbOpen(Db.getConn(), "select * from finestre");
            this.dati.dbRefresh();

            //sistemo le combo
            this.comTipoArti.dbOpenList(Db.getConn(), "select tipo_articolo from tipi_articoli order by tipo_articolo");
            this.comTipoLegn.dbOpenList(Db.getConn(), "select tipo_legno from tipi_legno order by tipo_legno");
            this.comAnte.dbOpenList(Db.getConn(), "select ante from ante order by ante");

            //apro la griglia
            this.griglia.dbChiave = chiave;

            String sql;
            sql = "select id, ";
            sql += " tipo_articolo, ";
            sql += " tipo_legno, ";
            sql += " h , ";
            sql += " l , ";
            sql += " ante , ";
            sql += " REPLACE(REPLACE(REPLACE(FORMAT(prezzo,2),'.','X'),',','.'),'X',',') AS prezzo, ";
            sql += " REPLACE(REPLACE(FORMAT(coeff,2),',',''),'.',',') AS coeff ";
            sql += " from finestre";
            sql += " order by tipo_articolo, tipo_legno, ante, h, l";
            this.griglia.dbOpen(Db.getConn(), sql);
            this.griglia.dbPanel = this.dati;

            //this.jScrollPane1.scrollRectToVisible(new java.awt.Rectangle(100,100));
            this.dati.dbRefresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit()
                 throws Exception {
        border1 = BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.white, new Color(134, 134, 134), new Color(93, 93, 93));
        component1 = Box.createHorizontalStrut(8);
        component2 = Box.createHorizontalStrut(8);
        component3 = Box.createHorizontalStrut(8);
        this.setResizable(true);
        this.setClosable(true);
        this.setMaximizable(true);
        this.setIconifiable(true);
        this.setTitle("Anagrafiche > Finestre");

        //this.setPreferredSize(new Dimension(200, 200));
        this.setBorder(BorderFactory.createRaisedBevelBorder());
        this.setAutoscrolls(true);
        this.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
            public void internalFrameClosing(InternalFrameEvent e) {
                this_internalFrameClosing(e);
            }
        });
        this.getContentPane().setLayout(borderLayout1);
        panTop.setMaximumSize(new Dimension(32767, 30));
        panTop.setMinimumSize(new Dimension(10, 30));
        panTop.setPreferredSize(new Dimension(10, 30));
        panTop.setLayout(borderLayout2);
        butNew.setFont(new java.awt.Font("Dialog", 0, 11));
        butNew.setText("Nuovo");
        butNew.setIcon(new ImageIcon(new java.net.URL("file:img/general/new16.gif")));
        butNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                butNew_actionPerformed(e);
            }
        });
        butSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                butSave_actionPerformed(e);
            }
        });
        butSave.setEnabled(false);
        butSave.setFont(new java.awt.Font("Dialog", 0, 11));
        butSave.setText("Salva");
        butSave.setIcon(new ImageIcon(new java.net.URL("file:img/general/save16.gif")));
        butUndo.setEnabled(false);
        butUndo.setFont(new java.awt.Font("Dialog", 0, 11));
        butUndo.setText("Annula");
        butUndo.setIcon(new ImageIcon(new java.net.URL("file:img/general/undo16.gif")));
        butUndo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                butUndo_actionPerformed(e);
            }
        });
        jSplitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setOpaque(false);
        jSplitPane1.setDividerSize(8);
        jSplitPane1.setLastDividerLocation(250);
        jSplitPane1.setLeftComponent(dati);
        jSplitPane1.setOneTouchExpandable(true);
        jSplitPane1.setRightComponent(jPanel1);
        jPanel1.setLayout(borderLayout3);
        dbCodice.setDbRiempire(true);
        dbCodice.setDbSalvare(false);
        dbCodice.setBounds(new Rectangle(174, 4, 90, 21));
        dbCodice.setDbNomeCampo("id");
        dbCodice.setDbTipoCampo("");
        dbCodice.setDbDescCampo("");
        dbCodice.setFont(new java.awt.Font("Dialog", 1, 12));
        dbCodice.setMinimumSize(new Dimension(200, 21));
        dbCodice.setPreferredSize(new Dimension(200, 21));
        dbCodice.setEditable(false);
        dbCodice.setText("tnxTextField1");
        dati.setMaximumSize(new Dimension(30000, 20000));
        dati.setMinimumSize(new Dimension(0, 0));
        dati.setPreferredSize(new Dimension(0, 0));
        dati.setLayout(null);
        jLabel4.setBounds(new Rectangle(121, 7, 48, 17));
        jLabel4.setText("id");
        jLabel4.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel4.setPreferredSize(new Dimension(150, 17));
        jLabel4.setMinimumSize(new Dimension(150, 17));
        jLabel1.setMinimumSize(new Dimension(150, 17));
        jLabel1.setPreferredSize(new Dimension(150, 17));
        jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel1.setText("tipo articolo");
        jLabel1.setBounds(new Rectangle(7, 30, 81, 17));
        butFind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                butFind_actionPerformed(e);
            }
        });
        butFind.setFont(new java.awt.Font("Dialog", 0, 11));
        butFind.setText("Trova");
        butFind.setIcon(new ImageIcon(new java.net.URL("file:img/general/find16.gif")));
        tooTop.setBorder(BorderFactory.createEtchedBorder());
        tooTop.setFloatable(false);
        butDele.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                butDele_actionPerformed(e);
            }
        });
        butDele.setIcon(new ImageIcon(new java.net.URL("file:img/general/delete16.gif")));
        butDele.setText("Elimina");
        butDele.setFont(new java.awt.Font("Dialog", 0, 11));
        comTipoArti.setDbNomeCampo("tipo_articolo");
        comTipoArti.setDbTipoCampo("");
        comTipoArti.setDbDescCampo("Tipo Articolo");
        comTipoArti.setDbTrovaMentreScrive(true);
        comTipoArti.setText("tnxComboField1");
        comTipoArti.setBounds(new Rectangle(95, 31, 179, 18));
        comTipoLegn.setBounds(new Rectangle(95, 52, 179, 18));
        comTipoLegn.setText("tnxComboField1");
        comTipoLegn.setDbTrovaMentreScrive(true);
        comTipoLegn.setDbDescCampo("Legno");
        comTipoLegn.setDbTipoCampo("");
        comTipoLegn.setDbNomeCampo("tipo_legno");
        jLabel2.setBounds(new Rectangle(9, 53, 81, 17));
        jLabel2.setText("legno");
        jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel2.setPreferredSize(new Dimension(150, 17));
        jLabel2.setToolTipText("");
        jLabel2.setMinimumSize(new Dimension(150, 17));
        jLabel3.setMinimumSize(new Dimension(150, 17));
        jLabel3.setToolTipText("");
        jLabel3.setPreferredSize(new Dimension(150, 17));
        jLabel3.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel3.setText("altezza");
        jLabel3.setBounds(new Rectangle(19, 76, 73, 17));
        tnxTextField1.setText("tnxTextField1");
        tnxTextField1.setDbNomeCampo("h");
        tnxTextField1.setDbTipoCampo("numerico");
        tnxTextField1.setDbDescCampo("Altezza");
        tnxTextField1.setBounds(new Rectangle(97, 74, 63, 17));
        jLabel5.setBounds(new Rectangle(166, 75, 73, 17));
        jLabel5.setText("larghezza");
        jLabel5.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel5.setPreferredSize(new Dimension(150, 17));
        jLabel5.setToolTipText("");
        jLabel5.setMinimumSize(new Dimension(150, 17));
        tnxTextField2.setBounds(new Rectangle(242, 74, 63, 17));
        tnxTextField2.setDbDescCampo("Larghezza");
        tnxTextField2.setDbTipoCampo("numerico");
        tnxTextField2.setDbNomeCampo("l");
        tnxTextField2.setText("tnxTextField1");
        jLabel6.setBounds(new Rectangle(23, 96, 73, 17));
        jLabel6.setText("ante");
        jLabel6.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel6.setPreferredSize(new Dimension(150, 17));
        jLabel6.setToolTipText("");
        jLabel6.setMinimumSize(new Dimension(150, 17));
        comAnte.setDbNomeCampo("ante");
        comAnte.setDbTipoCampo("");
        comAnte.setDbDescCampo("Ante");
        comAnte.setDbTrovaMentreScrive(true);
        comAnte.setText("tnxComboField1");
        comAnte.setBounds(new Rectangle(97, 96, 114, 18));
        jLabel7.setBounds(new Rectangle(19, 114, 73, 17));
        jLabel7.setText("prezzo");
        jLabel7.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel7.setPreferredSize(new Dimension(150, 17));
        jLabel7.setToolTipText("");
        jLabel7.setMinimumSize(new Dimension(150, 17));
        tnxTextField3.setBounds(new Rectangle(95, 116, 63, 17));
        tnxTextField3.setDbDescCampo("Prezzo");
        tnxTextField3.setDbTipoCampo("valuta");
        tnxTextField3.setDbNomeCampo("prezzo");
        tnxTextField3.setText("tnxTextField1");
        jLabel8.setMinimumSize(new Dimension(150, 17));
        jLabel8.setToolTipText("");
        jLabel8.setPreferredSize(new Dimension(150, 17));
        jLabel8.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel8.setText("coefficiente");
        jLabel8.setBounds(new Rectangle(170, 117, 73, 17));
        tnxTextField4.setText("tnxTextField1");
        tnxTextField4.setDbNomeCampo("coeff");
        tnxTextField4.setDbTipoCampo("numerico");
        tnxTextField4.setDbDescCampo("Coefficiente");
        tnxTextField4.setBounds(new Rectangle(251, 117, 63, 17));
        jLabel9.setMinimumSize(new Dimension(150, 17));
        jLabel9.setToolTipText("");
        jLabel9.setPreferredSize(new Dimension(150, 17));
        jLabel9.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel9.setText("descrizione");
        jLabel9.setBounds(new Rectangle(9, 138, 73, 17));
        tnxTextField5.setText("tnxTextField1");
        tnxTextField5.setDbNomeCampo("descrizione");
        tnxTextField5.setDbTipoCampo("");
        tnxTextField5.setDbDescCampo("Descrizione");
        tnxTextField5.setBounds(new Rectangle(90, 137, 297, 17));
        tnxMemoField1.setDbNomeCampo("note");
        tnxMemoField1.setText("tnxMemoField1");
        tnxMemoField1.setBounds(new Rectangle(92, 157, 295, 63));
        jLabel10.setBounds(new Rectangle(14, 159, 73, 17));
        jLabel10.setText("note");
        jLabel10.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel10.setPreferredSize(new Dimension(150, 17));
        jLabel10.setToolTipText("");
        jLabel10.setMinimumSize(new Dimension(150, 17));
        this.getContentPane().add(panTop, BorderLayout.NORTH);
        panTop.add(tooTop, BorderLayout.NORTH);
        tooTop.add(butNew, null);
        tooTop.add(component1, null);
        tooTop.add(butSave, null);
        tooTop.add(butUndo, null);
        tooTop.add(component2, null);
        tooTop.add(butDele, null);
        tooTop.add(component3, null);
        tooTop.add(butFind, null);
        this.getContentPane().add(jSplitPane1, BorderLayout.CENTER);
        jSplitPane1.add(jPanel1, JSplitPane.BOTTOM);
        jPanel1.add(jScrollPane1, BorderLayout.CENTER);
        jSplitPane1.add(dati, JSplitPane.TOP);
        dati.add(dbCodice, null);
        dati.add(jLabel1, null);
        dati.add(comTipoArti, null);
        dati.add(comTipoLegn, null);
        dati.add(jLabel2, null);
        dati.add(jLabel3, null);
        dati.add(tnxTextField1, null);
        dati.add(jLabel5, null);
        dati.add(tnxTextField2, null);
        dati.add(jLabel6, null);
        dati.add(comAnte, null);
        dati.add(jLabel4, null);
        dati.add(jLabel8, null);
        dati.add(jLabel7, null);
        dati.add(tnxTextField3, null);
        dati.add(tnxTextField4, null);
        dati.add(jLabel9, null);
        dati.add(tnxTextField5, null);
        dati.add(tnxMemoField1, null);
        dati.add(jLabel10, null);
        jScrollPane1.getViewport().add(griglia, null);
        jSplitPane1.setDividerLocation(250);
    }

    void butWeb_mousePressed(MouseEvent e) {
    }

    void butWeb_mouseReleased(MouseEvent e) {
    }

    void butWeb_mouseEntered(MouseEvent e) {
    }

    void butWeb_mouseExited(MouseEvent e) {
    }

    void butFold_mousePressed(MouseEvent e) {
    }

    void butFold_mouseReleased(MouseEvent e) {
    }

    void butFold_mouseEntered(MouseEvent e) {
    }

    void butFold_mouseExited(MouseEvent e) {
    }

    void butEmai_mousePressed(MouseEvent e) {
    }

    void butEmai_mouseReleased(MouseEvent e) {
    }

    void butEmai_mouseEntered(MouseEvent e) {
    }

    void butEmai_mouseExited(MouseEvent e) {
    }

    void butNew_actionPerformed(ActionEvent e) {
        this.dati.dbNew();
    }

    void tnxTextField1_keyTyped(KeyEvent e) {
    }

    void butSave_actionPerformed(ActionEvent e) {
        this.dati.dbSave();
        this.griglia.dbRefresh();
    }

    void butUndo_actionPerformed(ActionEvent e) {
        this.dati.dbRefresh();
    }

    void cc_keyPressed(KeyEvent e) {
        javax.swing.JOptionPane.showMessageDialog(null, "att");
    }

    void cc_inputMethodTextChanged(InputMethodEvent e) {
        javax.swing.JOptionPane.showMessageDialog(null, "att");
    }

    void cc_keyReleased(KeyEvent e) {
        javax.swing.JOptionPane.showMessageDialog(null, "att");
    }

    void cc_propertyChange(PropertyChangeEvent e) {
        javax.swing.JOptionPane.showMessageDialog(null, "att");
    }

    void cc_keyTyped(KeyEvent e) {
        javax.swing.JOptionPane.showMessageDialog(null, "att");
    }

    void cc_caretPositionChanged(InputMethodEvent e) {
        javax.swing.JOptionPane.showMessageDialog(null, "att");
    }

    void butFind_actionPerformed(ActionEvent e) {

        //debug
        //javax.swing.JOptionPane.showMessageDialog(null,this.dati.ultimoCampo + " - " + this.dati.ultimoValore);
        boolean ret = this.griglia.dbFindNext();

        if (ret == false) {

            int ret2 = JOptionPane.showConfirmDialog(this, "Posizione non trovata\nVuoi riprovare dall'inizio ?", "Attenzione", JOptionPane.YES_NO_OPTION);

            //JOptionPane.showMessageDialog(this,"?-:"+String.valueOf(i));
            if (ret2 == JOptionPane.OK_OPTION) {

                boolean ret3 = this.griglia.dbFindFirst();
            }
        }
    }

    void butDele_actionPerformed(ActionEvent e) {
        this.dati.dbDelete();
        this.griglia.dbRefresh();
    }

    void this_internalFrameClosing(InternalFrameEvent e) {
        main.getPadre().closeFrame(this);
    }
}