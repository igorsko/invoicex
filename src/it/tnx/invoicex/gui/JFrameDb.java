/**
 * Invoicex
 * Copyright (c) 2005,2006,2007,2008,2009 Marco Ceccarelli, Tnx snc
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
package it.tnx.invoicex.gui;

import gestioneFatture.DumpThread;
import gestioneFatture.JDialogWait;
import gestioneFatture.SqlLineIterator;
import gestioneFatture.main;
import it.tnx.Db;
import it.tnx.commons.DbUtils;
import it.tnx.commons.SwingUtils;
import it.tnx.dbeans.ResultSet.ScrollingResultSetTableModel;
import it.tnx.invoicex.InvoicexUtil;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.FileInputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JWindow;
import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingworker.SwingWorker;

/**
 *
 * @author  test1
 */
public class JFrameDb extends javax.swing.JFrame {

    Statement s = null;
    ResultSet r = null;

    /** Creates new form JFrameDb */
    public JFrameDb() {
        initComponents();

        SwingWorker w = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                //leggo le tabelle da aggiungere al list
                ResultSet r = Db.openResultSet("show tables");
                return r;
            }

            @Override
            protected void done() {
                try {
                    ResultSet r = (ResultSet) get();
                    jList1.setModel(new DefaultListModel());
                    while (r.next()) {
                        ((DefaultListModel) jList1.getModel()).addElement(r.getString(1));
                    }
                } catch (Exception ex) {
                    Logger.getLogger(JFrameDb.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        w.execute();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pop = new javax.swing.JPopupMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        sqlarea = new javax.swing.JTextArea();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        jMenuItem1.setText("sql to clipboard");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        pop.add(jMenuItem1);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Database");

        jSplitPane1.setDividerLocation(180);
        jSplitPane1.setMinimumSize(new java.awt.Dimension(100, 25));

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList1ValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        jSplitPane1.setLeftComponent(jScrollPane1);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTable1MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTable1MouseReleased(evt);
            }
        });
        jScrollPane2.setViewportView(jTable1);

        jPanel1.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jButton1.setText("Elimina");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        sqlarea.setColumns(20);
        sqlarea.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        sqlarea.setRows(5);
        jScrollPane3.setViewportView(sqlarea);

        jButton2.setText("Esegui");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Esegui diretto da file");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(0, 0, Short.MAX_VALUE)
                        .add(jButton1))
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jButton3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jButton2)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jButton1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 136, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton2)
                    .add(jButton3))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel2, java.awt.BorderLayout.SOUTH);

        jSplitPane1.setRightComponent(jPanel1);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList1ValueChanged
    if (evt.getValueIsAdjusting() == false) {
        if (jList1.getSelectedValue() != null) {
            apriResultSet("select * from " + jList1.getSelectedValue());
        }
    }
}//GEN-LAST:event_jList1ValueChanged

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    if (JOptionPane.showConfirmDialog(this, "Sicuro di eliminare la riga ?", "Attenzione", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
        ((ScrollingResultSetTableModel) jTable1.getModel()).delete(jTable1.getSelectedRow());
    }
}//GEN-LAST:event_jButton1ActionPerformed

private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
    if (!sqlarea.getText().toLowerCase().startsWith("select")) {
        try {
            Statement st = Db.getConn().createStatement();
            String sql = sqlarea.getText();
            String sqls[] = StringUtils.split(sql, ";");
            int ret = 0;
            for (String sqln : sqls) {
                ret += st.executeUpdate(sqln);
            }
            st.close();
            JOptionPane.showMessageDialog(this, "ret: " + ret);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
        }
    } else {
        apriResultSet(sqlarea.getText());
    }
}//GEN-LAST:event_jButton2ActionPerformed

private void jTable1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MousePressed
    if (evt.isPopupTrigger()) {
        pop.show(jTable1, evt.getX(), evt.getY());
    }
}//GEN-LAST:event_jTable1MousePressed

private void jTable1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseReleased
    if (evt.isPopupTrigger()) {
        pop.show(jTable1, evt.getX(), evt.getY());
    }
}//GEN-LAST:event_jTable1MouseReleased

private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
    String out = "";

    try {

        String tableName = jList1.getSelectedValue().toString();

        StringBuilder sb = new StringBuilder();
        sb.append("insert into ").append(tableName).append(" values (");

        ScrollingResultSetTableModel model = (ScrollingResultSetTableModel) jTable1.getModel();
        ResultSet resu = model.getResultSet();
        ResultSetMetaData meta = resu.getMetaData();
        int columns = meta.getColumnCount();
        String valoreCampo = "";

        for (int i = 1; i <= columns; i++) {
            valoreCampo = it.tnx.Db2.pc(resu.getObject(i), meta.getColumnType(i));
            sb.append(valoreCampo);
            if (i != columns) {
                sb.append(",");
            }
        }
        sb.append(");");

        System.out.println(sb.toString());

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable transferable = new StringSelection(sb.toString());
        clipboard.setContents(transferable, null);
        
    } catch (Exception e) {
        e.printStackTrace();
    }

}//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        //carica un file e lo esegue direttamente
        
        SwingWorker work1 = new SwingWorker() {
            JDialogWait wait = new JDialogWait(JFrameDb.this, false);

            @Override
            protected void done() {

                wait.labStato.setText("finito");
                System.out.println("REC:finito");
                SwingUtils.mouse_def(JFrameDb.this);
                wait.setVisible(false);

            }

            @Override
            protected void process(List chunks) {
                for (Object chunk : chunks) {
                    if (chunk instanceof int[]) {
                        int[] vals = (int[]) chunk;
                        wait.progress.setMaximum(vals[1]);
                        wait.progress.setValue(vals[0]);
                        if (wait.progress.isIndeterminate()) {
                            wait.progress.setIndeterminate(false);
                        }
                    } else if (chunk instanceof SqlString) {
                        JFrameDb.this.sqlarea.setText(chunk.toString());
                    } else {
                        wait.labStato.setText(chunk.toString());
                    }
                }
            }

            @Override
            protected Object doInBackground() throws Exception {

                SwingUtils.mouse_wait(JFrameDb.this);

                //faccio selezionare il file
                File dirBackup = new File(main.wd + "backup");
                JFileChooser fileChooser = SwingUtils.getFileOpen(JFrameDb.this, dirBackup);

                int ret = fileChooser.showOpenDialog(JFrameDb.this);
                File f = fileChooser.getSelectedFile();

                if (ret == JFileChooser.CANCEL_OPTION) {
                    return null;
                }

                if (JOptionPane.showConfirmDialog(JFrameDb.this, "Sicuro di eseguire il file sql selezionato ?",
                        "Attenzione", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                    return null;
                }

                wait.setLocationRelativeTo(null);
                wait.setVisible(true);

                //controllo zip
                if (f.getName().endsWith(".zip")) {
                    //unzippo
                    gestioneFatture.UnZip.main(new String[]{f.getAbsolutePath()});
                    f = new File(main.wd + gestioneFatture.UnZip.firstfile);
                    publish("Backup decompresso");
                }

                //restoro su db
                try {
                    FileInputStream fis = new FileInputStream(f);
                    String sql = "";

                    publish("Esecuzione in corso");

                    Statement stat;
                    stat = Db.getConn().createStatement();
                    try {
                        stat.execute("SET foreign_key_checks = 0;");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    
                    stat.execute("SET storage_engine=MYISAM;");
                    
                    String sqlc = null;

                    int tot = (int) f.length();
                    SqlLineIterator liter = new SqlLineIterator(fis);
                    int linen = 0;

                    JWindow werr = null;
                    while (liter.hasNext()) {
                        linen++;
                        if (linen % 100 == 0) {
                            publish(new int[]{(int) liter.bytes_processed, (int) tot});
                            publish("Esecuzione in corso " + liter.bytes_processed + "/" + tot);
                        }
                        sqlc = liter.nextLine();
                        
                        if (sqlc.startsWith("--")) continue;
                        if (sqlc.startsWith("insert into v_righ_tutte ")) continue;
                        if (sqlc.startsWith("CREATE ALGORITHM=UNDEFINED")) {
                            //CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`192.168.0.%` SQL SECURITY DEFINER VIEW `v_righ_tutte` AS select 'v' AS `tabella`,`r`.`id` AS `id`,`r`.`id_padre` AS `id_padre`,`t`.`data` AS `data`,`t`.`anno` AS `anno`,`t`.`numero` AS `numero`,`t`.`serie` AS `serie`,`r`.`riga` AS `riga`,`r`.`codice_articolo` AS `codice_articolo`,`r`.`descrizione` AS `descrizione`,`r`.`quantita` AS `quantita`,`r`.`prezzo` AS `prezzo`,`r`.`prezzo_netto_unitario` AS `prezzo_netto_unitario`,`r`.`sconto1` AS `sconto1`,`r`.`sconto2` AS `sconto2`,`t`.`sconto1` AS `sconto1t`,`t`.`sconto2` AS `sconto2t`,`t`.`sconto3` AS `sconto3t`,`c`.`codice` AS `clifor`,`c`.`ragione_sociale` AS `ragione_sociale` from ((`righ_fatt` `r` join `test_fatt` `t` on((`r`.`id_padre` = `t`.`id`))) join `clie_forn` `c` on((`t`.`cliente` = `c`.`codice`))) union all select 'a' AS `tabella`,`r`.`id` AS `id`,`r`.`id_padre` AS `id_padre`,`t`.`data` AS `data`,`t`.`anno` AS `anno`,`t`.`numero` AS `numero`,`t`.`serie` AS `serie`,`r`.`riga` AS `riga`,`r`.`codice_articolo` AS `codice_articolo`,`r`.`descrizione` AS `descrizione`,`r`.`quantita` AS `quantita`,`r`.`prezzo` AS `prezzo`,`r`.`prezzo_netto_unitario` AS `prezzo_netto_unitario`,`r`.`sconto1` AS `sconto1`,`r`.`sconto2` AS `sconto2`,`t`.`sconto1` AS `sconto1t`,`t`.`sconto2` AS `sconto2t`,`t`.`sconto3` AS `sconto3t`,`c`.`codice` AS `clifor`,`c`.`ragione_sociale` AS `ragione_sociale` from ((`righ_fatt_acquisto` `r` join `test_fatt_acquisto` `t` on((`r`.`id_padre` = `t`.`id`))) join `clie_forn` `c` on((`t`.`fornitore` = `c`.`codice`)));
                            System.out.println("sqlc prima = " + sqlc);
                            sqlc = "create view " + StringUtils.substringAfter(sqlc, " VIEW ");
                            System.out.println("sqlc dopo  = " + sqlc);
                        }
                        if (StringUtils.isBlank(sqlc)) continue;
                        
                        sqlc = StringUtils.replace(sqlc, "0x,", "null,");
                        sqlc = StringUtils.replace(sqlc, "0x)", "null)");
                        if (sqlc.length() > 0) {
                            try {
                                sqlc = StringUtils.replace(sqlc, "USING BTREE", "");
                                
                                //controllo per logo in dati azienda
                                boolean fatto_da_checkblob = false;
                                try {
                                    fatto_da_checkblob = InvoicexUtil.checkSqlBlob(sqlc);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (!fatto_da_checkblob) {
                                    publish(new SqlString(sqlc));
                                    stat.execute(sqlc);
                                }
                            } catch (com.mysql.jdbc.PacketTooBigException toobig) {
                                //controllo se è dati azienda provo a maetterlo a pezzi
                                if (werr == null || !werr.isVisible()) {
                                    werr = SwingUtils.showFlashMessage2("Errore durante l'esecuzione: " + toobig.getMessage(), 5, null, Color.RED);
                                }
                                toobig.printStackTrace();
                                System.out.println("toobig sql di errore:" + sql);
                            } catch (Exception err) {
                                if (werr == null || !werr.isVisible()) {
                                    if (err.getMessage().indexOf("character_set_client") < 0) {
                                        werr = SwingUtils.showFlashMessage2("Errore durante l'esecuzione: " + err.getMessage(), 5, null, Color.RED);
                                    }
                                }
                                err.printStackTrace();
                                System.out.println("sql di errore:" + sqlc);
                            }
                        }
                    }

                    try {
                        stat.execute("SET foreign_key_checks = 1;");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    
                    stat.close();

                    publish("Esecuzione completata");
                    JOptionPane.showMessageDialog(JFrameDb.this, "Esecuzione terminata", "Attenzione", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(JFrameDb.this, "Errore:" + ex.toString());
                    ex.printStackTrace();
                }

                return null;

            }
            
            


        };
        work1.execute();        
    }//GEN-LAST:event_jButton3ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new JFrameDb().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JList jList1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JPopupMenu pop;
    public javax.swing.JTextArea sqlarea;
    // End of variables declaration//GEN-END:variables

    private void apriResultSet(final String sql) {
        SwingWorker w = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                //apro tabella
                try {
                    if (r != null) {
                        r.close();
                    }
                    if (s != null) {
                        s.close();
                    }
                } catch (Exception ex0) {
                }
                try {
                    s = Db.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    r = s.executeQuery(sql);
                    ScrollingResultSetTableModel dm = new ScrollingResultSetTableModel(r, JFrameDb.this);
                    jTable1.setModel(dm);
                } catch (Exception ex0) {
                    ex0.printStackTrace();
                    SwingUtils.showErrorMessage(JFrameDb.this, ex0.toString());
                }
                return null;

            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
            }

        };
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        w.execute();
    }
    
    public static class SqlString {
        String stringa;

        public SqlString(String stringa) {
            this.stringa = stringa;
        }

        @Override
        public String toString() {
            if (stringa == null) return "";
            return stringa.toString();
        }
        
    }
}
