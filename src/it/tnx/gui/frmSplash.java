package it.tnx.gui;

import javax.swing.*;
import java.awt.*;


/**
 * Title:        GestionePreventivi
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      TNX di Provvedi Andrea & C. s.a.s.
 * @author Marco Ceccarelli
 * @version 1.0
 */

public class frmSplash extends JDialog {
  public JProgressBar jProgressBar1 = new JProgressBar();
  public JLabel jLabel1 = new JLabel();

  public frmSplash() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  public static void main(String[] args) {
    //frmSplash frmSplash1 = new frmSplash();
  }
  private void jbInit() throws Exception {
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