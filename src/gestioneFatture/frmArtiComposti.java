/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * frmArtiComposti.java
 *
 * Created on 17-set-2009, 11.45.33
 */
package gestioneFatture;

import it.tnx.Db;
import it.tnx.commons.CastUtils;
import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import tnxbeans.tnxDbGrid2;

/**
 *
 * @author mceccarelli
 */
public class frmArtiComposti extends javax.swing.JInternalFrame {

    public static final int STATUS_NEW = 0;
    public static final int STATUS_MOD = 1;
    public static final int STATUS_SAV = 2;
    private int status;
    private Vector deleteSql;

    /** Creates new form frmArtiComposti */
    public frmArtiComposti(String codice) {
        initComponents();

        griglia.setSortable(false);

        griglia.getColumn(0).setPreferredWidth(50);
        griglia.getColumn(1).setPreferredWidth(150);
        griglia.getColumn(2).setPreferredWidth(15);
        griglia.getColumn(3).setPreferredWidth(15);

        deleteSql = new Vector();
        status = STATUS_NEW;
        jButton1.setEnabled(false);
        jButton2.setEnabled(false);
        texOutput.setVisible(false);
        try {
            String sql = "SELECT descrizione from articoli where codice = '" + codice + "'";
            ResultSet rs = Db.openResultSet(sql);

            if (rs.next()) {
                status = STATUS_SAV;
                String descrizione = rs.getString("descrizione");
                texDescrizionePacchetto.setText(descrizione);
                texCodicePacchetto.setText(codice);

                sql = "select p.articolo, p.quantita, a.um, a.descrizione from pacchetti_articoli p join articoli a on p.articolo = a.codice where p.pacchetto = '" + codice + "'";
                rs = Db.openResultSet(sql);

                DefaultTableModel model = (DefaultTableModel) griglia.getModel();

                while (rs.next()) {
                    Vector v = new Vector();
                    v.add(rs.getString("articolo"));
                    v.add(rs.getString("descrizione"));
                    v.add(rs.getDouble("quantita"));
                    v.add(rs.getString("um"));

                    model.addRow(v);
                }

                griglia.setModel(model);
            } else {
                JOptionPane.showMessageDialog(this, "Kit non trovato!", "Errore", JOptionPane.ERROR_MESSAGE);
                this.setVisible(false);
                this.dispose();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetKit() {
        String sql = "delete from pacchetti_articoli where pacchetto = '" + texCodicePacchetto.getText() + "'";
        Db.executeSql(sql);
    }

    private void recuperaDatiArticolo(String chiave, int row) {
        if (chiave.length() > 0) {

            status = STATUS_MOD;
            jButton1.setEnabled(true);
            jButton2.setEnabled(true);
            String sql = "select descrizione, um from articoli";
            sql += " where codice = " + Db.pc(chiave, "VARCHAR");

            //li recupero dal cliente
            ResultSet temp;
            temp = Db.openResultSet(sql);
            try {
                if (temp.next() == true) {
                    String desc = temp.getString("descrizione");
                    String um = temp.getString("um");
                    griglia.setValueAt(desc, row, 1);
                    griglia.setValueAt(um, row, 3);
                    griglia.setValueAt(1, row, 2);
                    status = STATUS_MOD;
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this, "Il codice articolo specificato non esiste in anagrafica !");
                }
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }

    private void scegliArticolo() throws SQLException {
        texOutput.setText("");

        java.util.Hashtable colsWidthPerc = new java.util.Hashtable();
        colsWidthPerc.put("codice", new Double(20));
        colsWidthPerc.put("descrizione", new Double(80));

        String sql = "select codice, descrizione, um from articoli where codice != '" + texCodicePacchetto.getText() + "' order by descrizione, codice";

        //frmDbListSmall temp = new frmDbListSmall(main.getPadre(), true, sql, this.texCodiArti, 0, colsWidthPerc, 200,200, 550, 400);
        frmDbListSmall temp = new frmDbListSmall(main.getPadre(), true, sql, texOutput, 0, colsWidthPerc, 50, 200, 900, 500);

        if (texOutput.getText().equals("")) {
            return;
        } else {
            for (int row = 0; row < griglia.getRowCount(); row++) {
                String tmpCode = (String) griglia.getValueAt(row, 0);

                if (tmpCode.equals(texOutput.getText())) {
                    JOptionPane.showMessageDialog(this, "Non puoi inserire due volte lo stesso articolo in un kit !", "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }

        sql = "select codice, descrizione, um from articoli where codice = '" + texOutput.getText() + "'";
        ResultSet rs = Db.openResultSet(sql);

        if (rs.next()) {
            if (status == STATUS_SAV) {
                status = STATUS_MOD;
            }

            jButton1.setEnabled(true);
            jButton2.setEnabled(true);

            String descrizione = rs.getString("descrizione");
            String codice = rs.getString("codice");
            String um = rs.getString("um");
            Vector v = new Vector();
            v.add(codice);
            v.add(descrizione);
            v.add(1);
            v.add(um);

            DefaultTableModel model = (DefaultTableModel) griglia.getModel();

            model.addRow(v);
            griglia.setModel(model);

            removeBlackRow();

        } else {
            JOptionPane.showMessageDialog(this, "Articolo non trovato", "Errore dati", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveKit() {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        removeBlackRow();
        String codice = texCodicePacchetto.getText();
        try {
            for (Object sql : deleteSql) {
                String temp = (String) sql;
                Db.executeSql(temp);
            }

            deleteSql.clear();

            for (int i = 0; i < griglia.getRowCount(); i++) {
                String articolo = (String) griglia.getValueAt(i, 0);
                Double quant = CastUtils.toDouble(griglia.getValueAt(i, 2));
                String um = (String) griglia.getValueAt(i, 3);

                if (articolo.equals("") || quant.equals(null)) {
                    throw new IllegalArgumentException();
                }
            }


            Vector insertSql = new Vector();

            for (int i = 0; i < griglia.getRowCount(); i++) {
                String articolo = (String) griglia.getValueAt(i, 0);
                Double quant = CastUtils.toDouble(griglia.getValueAt(i, 2));
                String um = (String) griglia.getValueAt(i, 3);

                String sql = "insert into pacchetti_articoli (pacchetto, articolo, quantita) values ('" + codice + "', '" + articolo + "', " + quant + ")";
                insertSql.add(sql);
            }

            resetKit();

            for (Object sql : insertSql) {
                String temp = (String) sql;
                Db.executeSql(temp);
            }

            JOptionPane.showMessageDialog(this, "Salvataggio avvenuto correttamente", "Salvataggio Dati", JOptionPane.INFORMATION_MESSAGE);

            status = STATUS_SAV;
            jButton1.setEnabled(false);
            jButton2.setEnabled(false);
        } catch (NullPointerException npe) {
            JOptionPane.showMessageDialog(this, "Alcune righe non sono complete.", "Salvataggio Dati", JOptionPane.ERROR_MESSAGE);
        } finally {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    private void removeBlackRow() {
        for (int row = 0; row < griglia.getRowCount(); row++) {
            boolean delete = true;
            for (int col = 0; col < griglia.getColumnCount(); col++) {
                if (!griglia.getValueAt(row, col).equals("")) {
                    delete = false;
                }
            }
            if (delete) {
                DefaultTableModel model = (DefaultTableModel) griglia.getModel();
                model.removeRow(row);
                griglia.setModel(model);
            }
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

        popupGriglia = new javax.swing.JPopupMenu();
        menDelEl = new javax.swing.JMenuItem();
        menAddEl = new javax.swing.JMenuItem();
        jLabel1 = new javax.swing.JLabel();
        texCodicePacchetto = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        texDescrizionePacchetto = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        griglia = new tnxDbGrid2() {
            public void setValueAt(Object aValue, int row, int col) {
                if (col == 2){
                    if(aValue instanceof Integer){
                        Integer value = (Integer)aValue;
                        aValue = value.doubleValue();
                    }
                }

                super.setValueAt(aValue, row, col);
                status = STATUS_MOD;
                jButton1.setEnabled(true);
                jButton2.setEnabled(true);
                if (col == 0) {
                    String codice = (String) aValue;
                    recuperaDatiArticolo(codice, griglia.getSelectedRow());
                }
            }
        };
        jButton3 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        texOutput = new javax.swing.JTextField();

        menDelEl.setText("Elimina");
        menDelEl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menDelElActionPerformed(evt);
            }
        });
        popupGriglia.add(menDelEl);

        menAddEl.setText("Aggiungi Articolo");
        menAddEl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menAddElActionPerformed(evt);
            }
        });
        popupGriglia.add(menAddEl);

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Gestione Kit");
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
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

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Codice:"); // NOI18N

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Descrizione:"); // NOI18N

        griglia.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Codice", "Descrizione", "Quantità", "Unità di Misura"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        griglia.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                grigliaMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                grigliaMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                grigliaMouseReleased(evt);
            }
        });
        griglia.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                grigliaFocusLost(evt);
            }
        });
        griglia.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                grigliaKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(griglia);

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/list-add.png"))); // NOI18N
        jButton3.setText("Aggiungi Riga"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/edit-undo.png"))); // NOI18N
        jButton2.setText("Annulla"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/devices/media-floppy.png"))); // NOI18N
        jButton1.setText("Salva"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 559, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(texDescrizionePacchetto, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)
                            .add(texCodicePacchetto, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 125, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(layout.createSequentialGroup()
                        .add(jButton2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButton1))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(jButton3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(texOutput, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 54, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(texCodicePacchetto, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(texDescrizionePacchetto, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton3)
                    .add(texOutput, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton1)
                    .add(jButton2))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void grigliaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_grigliaMouseClicked

}//GEN-LAST:event_grigliaMouseClicked

    private void grigliaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_grigliaFocusLost
}//GEN-LAST:event_grigliaFocusLost

    private void grigliaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_grigliaKeyPressed
        try {
            if (evt.getKeyCode() == KeyEvent.VK_F4) {
                scegliArticolo();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
}//GEN-LAST:event_grigliaKeyPressed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        DefaultTableModel model = (DefaultTableModel) griglia.getModel();

        Vector v = new Vector();
        v.add("");
        v.add("");
        v.add("");
        v.add("");

        model.addRow(v);

        griglia.setModel(model);
}//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        int res = JOptionPane.showConfirmDialog(this, "Voui chiudere senza salvare? In questo caso tutte le modifiche verranno perse.", "Conferma di chiusura", JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            this.setVisible(false);
            this.dispose();
        }
}//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        saveKit();
}//GEN-LAST:event_jButton1ActionPerformed

    private void menDelElActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menDelElActionPerformed
        int res = JOptionPane.showConfirmDialog(this, "Eliminare l'articolo selezionato dal KIT?", "Conferma Eliminazione", JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.NO_OPTION) {
            return;
        }
        try {
            String pacchetto = texCodicePacchetto.getText();
            String articolo = (String) griglia.getValueAt(griglia.getSelectedRow(), griglia.getColumnByName("Codice"));
            String sql = "DELETE FROM pacchetti_articoli WHERE pacchetto = '" + pacchetto + "' AND articolo = '" + articolo + "'";

            deleteSql.add(sql);
            if (status == STATUS_SAV) {
                status = STATUS_MOD;
            }

            jButton1.setEnabled(true);
            jButton2.setEnabled(true);

            int rowSelected = griglia.getSelectedRow();
            DefaultTableModel model = (DefaultTableModel) griglia.getModel();
            model.removeRow(rowSelected);
            griglia.setModel(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
}//GEN-LAST:event_menDelElActionPerformed

    private void menAddElActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menAddElActionPerformed
        try {
            scegliArticolo();
        } catch (Exception e) {
            e.printStackTrace();
        }
}//GEN-LAST:event_menAddElActionPerformed

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        if (status == STATUS_MOD) {
            int res = JOptionPane.showConfirmDialog(this, "Salvare le modifiche?", "Salvataggio", JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.YES_OPTION) {
                try {
                    saveKit();
                } catch (IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(this, "Impossibile salvare i dati: alcune righe non sono complete", "Errore Salvataggio Dati", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }
        }
        dispose();
    }//GEN-LAST:event_formInternalFrameClosing

    private void grigliaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_grigliaMouseReleased
        if (evt.isPopupTrigger()) {
            this.popupGriglia.show(griglia, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_grigliaMouseReleased

    private void grigliaMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_grigliaMousePressed
        if (evt.isPopupTrigger()) {
            this.popupGriglia.show(griglia, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_grigliaMousePressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private tnxbeans.tnxDbGrid2 griglia;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JMenuItem menAddEl;
    private javax.swing.JMenuItem menDelEl;
    private javax.swing.JPopupMenu popupGriglia;
    private javax.swing.JTextField texCodicePacchetto;
    private javax.swing.JTextField texDescrizionePacchetto;
    private javax.swing.JTextField texOutput;
    // End of variables declaration//GEN-END:variables
}
