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

import java.awt.*;

import javax.swing.*;
import javax.swing.border.LineBorder;

public class frmSplash
    extends JDialog {

    public JProgressBar jProgressBar1 = new JProgressBar();
    public JLabel jLabel1 = new JLabel();

    public frmSplash() {

        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        //frmSplash frmSplash1 = new frmSplash();
    }

    private void jbInit()
                 throws Exception {

        //jLabel1.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel1.setHorizontalTextPosition(SwingConstants.CENTER);
        jLabel1.setText("...caricamento...");
        

        //jProgressBar1.setFont(new java.awt.Font("Dialog", 1, 10));
        jProgressBar1.setStringPainted(true);
        this.setResizable(false);
        this.getContentPane().add(jProgressBar1, BorderLayout.SOUTH);
        this.getContentPane().add(jLabel1, BorderLayout.NORTH);
        this.pack();
    }
}