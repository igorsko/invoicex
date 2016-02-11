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

public class diaDescZoom
    extends javax.swing.JDialog {

    javax.swing.JTable griglia;
    int cellaX;
    int cellaY;

    /** Creates new form diaDescZoom */
    public diaDescZoom(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public void setGriglia(javax.swing.JTable table, int x, int y, String value) {
        this.griglia = table;
        this.cellaX = x;
        this.cellaY = y;
        this.texDescrizione.setText(value);
    }

    private void initComponents() {//GEN-BEGIN:initComponents
        jScrollPane1 = new javax.swing.JScrollPane();
        texDescrizione = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        butAnnulla = new javax.swing.JButton();
        butConferma = new javax.swing.JButton();
        setTitle("Zoom");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        jScrollPane1.setViewportView(texDescrizione);
        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);
        butAnnulla.setText("Annulla");
        butAnnulla.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butAnnullaActionPerformed(evt);
            }
        });
        jPanel1.add(butAnnulla);
        butConferma.setText("Conferma");
        butConferma.setToolTipText("Conferma le modifiche");
        butConferma.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butConfermaActionPerformed(evt);
            }
        });
        jPanel1.add(butConferma);
        getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);
        pack();
    }//GEN-END:initComponents

    private void butAnnullaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butAnnullaActionPerformed
        dispose();
    }//GEN-LAST:event_butAnnullaActionPerformed

    private void butConfermaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butConfermaActionPerformed
        griglia.setValueAt(texDescrizione.getText(), cellaX, cellaY);
        dispose();
    }//GEN-LAST:event_butConfermaActionPerformed

    public static void main(String[] args) {
        new diaDescZoom(new javax.swing.JFrame(), true).show();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butAnnulla;
    private javax.swing.JButton butConferma;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea texDescrizione;

    // End of variables declaration//GEN-END:variables
}
