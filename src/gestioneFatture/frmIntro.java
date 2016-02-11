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

import it.tnx.commons.HttpUtils;
import it.tnx.invoicex.Main;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;
import javax.swing.border.LineBorder;

public class frmIntro extends JWindow {

    BorderLayout borderLayout1 = new BorderLayout();
    JPanel jPanel1 = new JPanel();
    JLabel labLogo = new JLabel();
    BorderLayout borderLayout2 = new BorderLayout();
    JPanel panMess = new JPanel();
    public JProgressBar jProgressBar1 = new JProgressBar();
    public JLabel labMess = new JLabel("...");
    public boolean stopOnTop;

    public frmIntro(java.awt.Frame parent) {
        super(parent);
        borderLayout2.setHgap(50);
        borderLayout2.setVgap(50);
        
        try {
            stopOnTop = false;
            jbInit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit()
                 throws Exception {
        jPanel1.setLayout(borderLayout2);

//        labLogo.setBorder(BorderFactory.createLineBorder(Color.black,1));
        jPanel1.add(labLogo, BorderLayout.CENTER);
        jPanel1.setBorder(new LineBorder(Color.GRAY, 1));
        
        this.getContentPane().add(jPanel1, BorderLayout.NORTH);
        panMess.setLayout(new BorderLayout());
        panMess.add(labMess, BorderLayout.NORTH);
        panMess.add(jProgressBar1, BorderLayout.CENTER);
        panMess.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        labMess.setOpaque(true);

        //labMess.setFont(new java.awt.Font("Dialog", 1, 11));
        labMess.setHorizontalAlignment(javax.swing.JLabel.CENTER);

        //jProgressBar1.setFont(new java.awt.Font("Dialog", 1, 10));
        this.getContentPane().add(panMess, BorderLayout.SOUTH);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

    @Override
    public void paintComponents(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(new Color(200,0,0,100));
        g2.fillOval(20, 20, 200, 200);
        
        //super.paintComponents(g2);
        
    }

    void this_windowActivated(WindowEvent e) {
    }

    public void setLogo() {
        try {
            if (!Main.applet) {
                this.labLogo.setIcon(new ImageIcon(new java.net.URL("file:" + main.homeDir + "img/logo.gif")));
                java.io.File f = new java.io.File(main.homeDir + "img/logo.jpg");
                if (f.exists()) this.labLogo.setIcon(new ImageIcon(new java.net.URL("file:" + main.homeDir + "img/logo.jpg")));
            } else {
                File filelogo = new File(main.wd + "img/logo.jpg");
                if (!filelogo.exists()) {
                    filelogo.getParentFile().mkdirs();
                    HttpUtils.saveFile("http://www.tnx.dyndns.org/download/cl_test/run/img/logo.jpg", filelogo.getAbsolutePath());
                }
                java.net.URL urllogo = new java.net.URL("file:" + filelogo.getAbsolutePath());
                System.out.println("urllogo = " + urllogo);
                this.labLogo.setIcon(new ImageIcon(urllogo));
            }
        } catch (Exception err) {
            javax.swing.JOptionPane.showMessageDialog(null, err);
        }
    }
}