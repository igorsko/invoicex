/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JDialogLogoResize.java
 *
 * Created on 1-mar-2010, 15.44.08
 */

package it.tnx.invoicex.gui.logoresize;

import it.tnx.Db;
import gestioneFatture.main;
import it.tnx.commons.CastUtils;
import it.tnx.invoicex.InvoicexUtil;
import java.awt.Rectangle;

/**
 *
 * @author mceccarelli
 */
public class JDialogLogoResize extends javax.swing.JDialog {

    /** Creates new form JDialogLogoResize */
    public JDialogLogoResize(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        try {
//            jPanelLogoResize1.setLogo(main.fileIni.getValue("varie", "percorso_logo_stampe"));
            jPanelLogoResize1.setLogo(InvoicexUtil.caricaLogoDaDb(Db.getConn()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            double x = CastUtils.toDouble0Eng(main.fileIni.getValue("varie", "logo_x", "0"));
            double y = CastUtils.toDouble0Eng(main.fileIni.getValue("varie", "logo_y", "0"));
            double w = CastUtils.toDouble0Eng(main.fileIni.getValue("varie", "logo_w", "100"));
            double h = CastUtils.toDouble0Eng(main.fileIni.getValue("varie", "logo_h", "100"));
            System.out.println("load pos 1 logo x " + x + " y " + y + " sw " + w + " sh " + h);
            x = check(x, 0);
            y = check(y, 0);
            w = checks(w, 100);
            h = checks(h, 100);
            System.out.println("load pos 2 logo x " + x + " y " + y + " sw " + w + " sh " + h);

            double w2 = jPanelLogoResize1.dim_x;
            double h2 = jPanelLogoResize1.dim_y;
            x = x * w2 / 100d;
            y = y * h2 / 100d;
            w = w * w2 / 100d;
            h = h * h2 / 100d;

            System.out.println("load pos 3 logo x " + x + " y " + y + " sw " + w + " sh " + h);
            jPanelLogoResize1.i.setBounds((int)Math.round(x + jPanelLogoResize1.off_x2), (int)Math.round(y + jPanelLogoResize1.off_y2), (int)Math.round(w), (int)Math.round(h));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelLogoResize1 = new it.tnx.invoicex.gui.logoresize.JPanelLogoResize();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Posizione e Dimensioni logo");

        jLabel1.setText("Qui sopra puoi spostare e dimensionare il logo all'interno dello spazio disponibile nelle stampe dei documenti.");

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/devices/media-floppy.png"))); // NOI18N
        jButton1.setText("Conferma");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/edit-undo.png"))); // NOI18N
        jButton2.setText("Annulla");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jCheckBox1.setText("Disabilita e gestisci solo dal report");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelLogoResize1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 529, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jCheckBox1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 130, Short.MAX_VALUE)
                .add(jButton2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jPanelLogoResize1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 296, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton1)
                    .add(jButton2)
                    .add(jCheckBox1))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Rectangle.Double r = jPanelLogoResize1.getLogoBounds();
        main.fileIni.setValue("varie", "logo_x", r.getX());
        main.fileIni.setValue("varie", "logo_y", r.getY());
        main.fileIni.setValue("varie", "logo_w", r.getWidth());
        main.fileIni.setValue("varie", "logo_h", r.getHeight());
        main.fileIni.setValue("varie", "logo_disabilita", (jCheckBox1.isSelected() ? "S" : "N"));
        main.fileIni.saveFile();
        dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JDialogLogoResize dialog = new JDialogLogoResize(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton jButton1;
    public javax.swing.JButton jButton2;
    public javax.swing.JCheckBox jCheckBox1;
    public javax.swing.JLabel jLabel1;
    public it.tnx.invoicex.gui.logoresize.JPanelLogoResize jPanelLogoResize1;
    // End of variables declaration//GEN-END:variables

    private double check(double x, double def) {
        if (x < 0 || x > 100) {
            return def;
        }
        return x;
    }

    private double checks(double x, double def) {
        if (x <= 0 || x > 100) {
            return def;
        }
        return x;
    }

}