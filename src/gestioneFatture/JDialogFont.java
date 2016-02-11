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

public class JDialogFont
    extends javax.swing.JDialog {

    /** Creates new form JDialogFont */
    public JDialogFont(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        //carico la lista dei font
        java.awt.GraphicsEnvironment ge = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fonts = ge.getAvailableFontFamilyNames();

        for (int i = 0; i < fonts.length; i++) {
            this.comFontName.addItem(fonts[i]);
        }

        //carico lengthprefereences impostate
        // Get the Preferences object.  Note, the backing store is unspecified
        java.util.prefs.Preferences preferences = java.util.prefs.Preferences.userNodeForPackage(main.class);

        // Retrieve the location of the window, default given in 2nd parameter
        String fontName = preferences.get("fontName", "Dialog");
        int fontSize = preferences.getInt("fontSize", 12);
        int fontSizePiccolo = preferences.getInt("fontSizePiccolo", 10);
        this.comFontName.setSelectedItem(fontName);
        this.spiFontSize.setValue(new Integer(fontSize));
    }

    private void initComponents() {//GEN-BEGIN:initComponents
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        comFontName = new javax.swing.JComboBox();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        spiFontSize = new javax.swing.JSpinner();
        jPanel2 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        setTitle("Impostazione Caratteri");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });
        jLabel1.setText("Nome Font");
        jPanel3.add(jLabel1);
        comFontName.setPreferredSize(new java.awt.Dimension(200, 24));
        jPanel3.add(comFontName);
        jPanel1.add(jPanel3);
        jLabel2.setText("Dimensione");
        jPanel4.add(jLabel2);
        spiFontSize.setPreferredSize(new java.awt.Dimension(40, 20));
        jPanel4.add(spiFontSize);
        jPanel1.add(jPanel4);
        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);
        jButton2.setText("Annulla");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton2);
        jButton1.setText("Ok");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton1);
        getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);
        pack();
    }//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {

            java.util.prefs.Preferences preferences = java.util.prefs.Preferences.userNodeForPackage(main.class);

            // Save the window location and size
            preferences.put("fontName", comFontName.getSelectedItem().toString());
            preferences.putInt("fontSize", Integer.parseInt(spiFontSize.getValue().toString()));
            preferences.putInt("fontSizePiccolo", Integer.parseInt(spiFontSize.getValue().toString()) - 2);
        } catch (Exception err) {
            err.printStackTrace();
        }

        dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog

    public static void main(String[] args) {
        new JDialogFont(new javax.swing.JFrame(), true).show();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox comFontName;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JSpinner spiFontSize;

    // End of variables declaration//GEN-END:variables
}