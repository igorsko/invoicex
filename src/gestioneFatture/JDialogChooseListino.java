/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JDialogChooseListino.java
 *
 * Created on 4-feb-2010, 9.48.01
 */

package gestioneFatture;

import it.tnx.Db;
import java.awt.Frame;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Toce Alessio
 */
public class JDialogChooseListino extends JDialog {

    public String listinoChoose;
    JFrame padre;
    /** Creates new form JDialogChooseListino */
    public JDialogChooseListino(Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        listinoChoose = "";
        String sql = "SELECT descrizione, codice FROM tipi_listino";
        comListini.dbOpenList(Db.getConn(), sql);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        butAnnulla = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        comListini = new tnxbeans.tnxComboField();

        setBackground(new java.awt.Color(224, 223, 227));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        butAnnulla.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/edit-undo.png"))); // NOI18N
        butAnnulla.setText("Annulla");
        butAnnulla.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butAnnullaActionPerformed(evt);
            }
        });
        jPanel1.add(butAnnulla);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/edit-redo.png"))); // NOI18N
        jButton1.setText("Salva");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1);

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 37, 380, -1));

        jLabel1.setText("Scegli Listino:");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 14, -1, -1));

        comListini.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        getContentPane().add(comListini, new org.netbeans.lib.awtextra.AbsoluteConstraints(78, 11, 312, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog

    private void butAnnullaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butAnnullaActionPerformed
        dispose();
    }//GEN-LAST:event_butAnnullaActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if(!String.valueOf(comListini.getSelectedKey()).equals("")){
            listinoChoose = String.valueOf(comListini.getSelectedKey());
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Seleziona almeno un listino da caricare", "Errore selezione", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_jButton1ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butAnnulla;
    private tnxbeans.tnxComboField comListini;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

}
