/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JDialogStampaArticoli.java
 *
 * Created on 21-nov-2008, 9.06.21
 */
package gestioneFatture;

import it.tnx.Db;
import it.tnx.commons.KeyValuePair;
import it.tnx.commons.SwingUtils;
import it.tnx.dbeans.pdfPrint.PrintSimpleTable;
import it.tnx.invoicex.InvoicexUtil;
import java.io.File;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Vector;
import tnxbeans.tnxDbGrid;

/**
 *
 * @author mceccarelli
 */
public class JDialogStampaArticoli extends javax.swing.JDialog {

    private tnxDbGrid griglia;

    /** Creates new form JDialogStampaArticoli */
    public JDialogStampaArticoli(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        KeyValuePair kv = new KeyValuePair(null, "<non stampare prezzi>");
        KeyValuePair kv1 = new KeyValuePair("EL1", "<Ultimi prezzi di Vendita>");
        KeyValuePair kv2 = new KeyValuePair("EL2", "<Ultimi prezzi di Acquisto>");
        SwingUtils.initJComboFromDb(listini, Db.getConn(), "select codice, descrizione from tipi_listino order by codice", "codice", "descrizione", kv, kv1, kv2);
        Vector v = new Vector();
        v.add(new KeyValuePair("a.codice", "codice"));
        v.add(new KeyValuePair("a.descrizione", "descrizione"));
        SwingUtils.initJComboFromKVList(ordine, v);

        v = new Vector();
        v.add(new KeyValuePair("pdf", "PDF"));
        v.add(new KeyValuePair("html", "HTML"));
        v.add(new KeyValuePair("xls", "EXCEL"));
        SwingUtils.initJComboFromKVList(formato, v);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btgSel = new javax.swing.ButtonGroup();
        stampa = new javax.swing.JButton();
        annulla = new javax.swing.JButton();
        listini = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        ordine = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        noteTesta = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        notePiede = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        formato = new javax.swing.JComboBox();
        rbtEnt = new javax.swing.JRadioButton();
        rbtVen = new javax.swing.JRadioButton();
        rbtAcq = new javax.swing.JRadioButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lingua = new javax.swing.JComboBox();

        FormListener formListener = new FormListener();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Stampa Elenco Articoli");

        stampa.setText("Stampa");
        stampa.addActionListener(formListener);

        annulla.setText("Annulla");
        annulla.addActionListener(formListener);

        listini.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Includi i prezzi del listino");
        jLabel1.setPreferredSize(new java.awt.Dimension(80, 14));

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Ordina per");
        jLabel2.setPreferredSize(new java.awt.Dimension(80, 14));

        ordine.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        noteTesta.setColumns(20);
        noteTesta.setRows(5);
        jScrollPane1.setViewportView(noteTesta);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Note Testata");
        jLabel3.setPreferredSize(new java.awt.Dimension(80, 14));

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Note Piede");
        jLabel4.setPreferredSize(new java.awt.Dimension(80, 14));

        notePiede.setColumns(20);
        notePiede.setRows(5);
        jScrollPane2.setViewportView(notePiede);

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Formato Stampa");
        jLabel5.setPreferredSize(new java.awt.Dimension(80, 14));

        formato.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btgSel.add(rbtEnt);
        rbtEnt.setSelected(true);
        rbtEnt.setText("Entrambi");
        rbtEnt.addActionListener(formListener);

        btgSel.add(rbtVen);
        rbtVen.setText("Vendita");
        rbtVen.addActionListener(formListener);

        btgSel.add(rbtAcq);
        rbtAcq.setText("Acquisto");
        rbtAcq.addActionListener(formListener);

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Tipo Articolo");

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Lingua");

        lingua.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Italiano", "Inglese" }));
        lingua.addActionListener(formListener);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                .add(jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE))
                            .add(jLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 175, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jScrollPane2)
                            .add(jScrollPane1)
                            .add(listini, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(ordine, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .add(layout.createSequentialGroup()
                        .add(jLabel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 130, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(0, 0, Short.MAX_VALUE)
                                .add(annulla)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(stampa))
                            .add(layout.createSequentialGroup()
                                .add(4, 4, 4)
                                .add(formato, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .add(layout.createSequentialGroup()
                        .add(jLabel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 135, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(rbtVen)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(rbtAcq)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(rbtEnt)
                        .add(0, 0, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(jLabel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 135, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lingua, 0, 306, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6, jLabel7}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(listini, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(ordine, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 83, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 83, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(formato, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel6)
                            .add(rbtVen)
                            .add(rbtAcq)
                            .add(rbtEnt))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel7)
                            .add(lingua, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(71, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(stampa)
                            .add(annulla))
                        .addContainerGap())))
        );

        pack();
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener {
        FormListener() {}
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == stampa) {
                JDialogStampaArticoli.this.stampaActionPerformed(evt);
            }
            else if (evt.getSource() == annulla) {
                JDialogStampaArticoli.this.annullaActionPerformed(evt);
            }
            else if (evt.getSource() == rbtEnt) {
                JDialogStampaArticoli.this.rbtEntActionPerformed(evt);
            }
            else if (evt.getSource() == rbtVen) {
                JDialogStampaArticoli.this.rbtVenActionPerformed(evt);
            }
            else if (evt.getSource() == rbtAcq) {
                JDialogStampaArticoli.this.rbtAcqActionPerformed(evt);
            }
            else if (evt.getSource() == lingua) {
                JDialogStampaArticoli.this.linguaActionPerformed(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents

    private void stampaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stampaActionPerformed
        //stampa
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

        //check prezzi a ricarico
        InvoicexUtil.aggiornaListini();

        String tipo = "";
        if (btgSel.isSelected(this.rbtAcq.getModel())) {
            tipo = "A";
        } else if (btgSel.isSelected(this.rbtVen.getModel())) {
            tipo = "V";
        } else {
            tipo = "E";
        }
        KeyValuePair item = (KeyValuePair) listini.getSelectedItem();
        String oldSql = griglia.oldSql;
        int[] headerWidth = null;
        String sql;
        sql = "select ";
        if (lingua.getSelectedItem().equals("Italiano")) {
            sql += " a.codice as Codice, ";
            sql += " a.descrizione as Descrizione, ";
            sql += " a.um as 'u.m.', ";
            sql += " a.codice_fornitore as 'Codice Fornitore', ";
        } else {
            sql += " a.codice as Code, ";
            sql += " a.descrizione_en as Description, ";
            sql += " a.um_en as 'm.u.', ";
            sql += " a.codice_fornitore as 'Supplier code', ";        
        }
        sql += " a.codice_a_barre as Barcode";
        if (item.key == null) {
            headerWidth = new int[]{15, 60, 5, 15, 15};
            sql += " from articoli a";
            sql += " where 1 = 1";
        } else {
            headerWidth = new int[]{15, 60, 5, 15, 15, 10};
            if (lingua.getSelectedItem().equals("Italiano")) {
                sql += " , IF(tl.ricarico_flag = 'S', (ap2.prezzo + (ap2.prezzo / 100 * tl.ricarico_perc)), ap.prezzo) as Prezzo";
            } else {
                sql += " , IF(tl.ricarico_flag = 'S', (ap2.prezzo + (ap2.prezzo / 100 * tl.ricarico_perc)), ap.prezzo) as Price";
            }
            sql += " from articoli a left join articoli_prezzi ap on a.codice = ap.articolo";
            sql += " left join tipi_listino tl on tl.codice = ap.listino";
            sql += " left join articoli_prezzi ap2 on a.codice = ap2.articolo and ap2.listino = tl.ricarico_listino";
            sql += " where (ap.listino = " + Db.pc(item.key, Types.VARCHAR) + " or ap.listino is null) ";
        }
        if (!tipo.equalsIgnoreCase("E")) {
            sql += " and a.tipo = '" + tipo + "'";
        }

        if (String.valueOf(listini.getSelectedItem()).equals("<Ultimi prezzi di Vendita>")) {
            sql = "select a.codice, a.descrizione, a.um, a.codice_fornitore as fornitore, a.codice_a_barre as barcode, (select prezzo from righ_fatt where righ_fatt.codice_articolo = a.codice order by anno desc, numero desc limit 1) as prezzo from articoli a";
        } else if (String.valueOf(listini.getSelectedItem()).equals("<Ultimi prezzi di Acquisto>")) {
            sql = "select a.codice, a.descrizione, a.um, a.codice_fornitore as fornitore, a.codice_a_barre as barcode, (select prezzo from righ_fatt_acquisto where righ_fatt_acquisto.codice_articolo = a.codice order by anno desc, numero desc limit 1) as prezzo from articoli a";
        }

        sql += " order by " + ((KeyValuePair) ordine.getSelectedItem()).key;

        System.out.println("sql:" + sql);
        griglia.dbOpen(Db.getConn(), sql);
        PrintSimpleTable print = new PrintSimpleTable(getGriglia());
        String caption = "";
        try {
            ResultSet r = Db.openResultSet("select ragione_sociale from dati_azienda");
            if (r.next()) {
                caption += r.getString(1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (caption.length() > 0) {
            caption += " - ";
        }

        if (lingua.getSelectedItem().equals("Italiano")) {
            if (String.valueOf(listini.getSelectedItem()).equals("<Ultimi prezzi di Vendita>")) {
                caption += "Elenco ultimi prezzi di vendita articoli";
            } else if (String.valueOf(listini.getSelectedItem()).equals("<Ultimi prezzi di Acquisto>")) {
                caption += "Elenco ultimi prezzi di acquisto articoli";
            } else {
                caption += "Elenco articoli";
            }
        } else {
            if (String.valueOf(listini.getSelectedItem()).equals("<Ultimi prezzi di Vendita>")) {
                caption += "List of recent sale prices items";
            } else if (String.valueOf(listini.getSelectedItem()).equals("<Ultimi prezzi di Acquisto>")) {
                caption += "List last purchase prices items";
            } else {
                caption += "Product list";
            }
        }
        if (((KeyValuePair) formato.getSelectedItem()).key.toString().equalsIgnoreCase("pdf")) {
            print.print(caption, headerWidth, ((KeyValuePair) formato.getSelectedItem()).key.toString(), noteTesta.getText(), notePiede.getText());
//            SwingUtils.open(new File(main.wd + "tempStampa.pdf"));
            Util.start2(main.wd + "tempStampa.pdf");
        } else if (((KeyValuePair) formato.getSelectedItem()).key.toString().equalsIgnoreCase("xls")) {
            String nomeFile = print.printExcel(caption, headerWidth, noteTesta.getText(), notePiede.getText());
//            SwingUtils.open(new File(main.wd + nomeFile));
            Util.start2(main.wd + nomeFile);
        } else {
            print.print(caption, headerWidth, ((KeyValuePair) formato.getSelectedItem()).key.toString(), noteTesta.getText(), notePiede.getText());
//            SwingUtils.open(new File(main.wd + "tempStampa.html"));
            Util.start2(main.wd + "tempStampa.html");
        }
        griglia.dbOpen(Db.getConn(), oldSql);

        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_stampaActionPerformed

    private void annullaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_annullaActionPerformed
        dispose();
    }//GEN-LAST:event_annullaActionPerformed

    private void rbtAcqActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtAcqActionPerformed
        String testo = this.noteTesta.getText();
        int i = testo.indexOf("ARTICOLI VENDITA");
        int e = i + 16;
        if (i != -1) {
            testo = testo.substring(0, i) + "ARTICOLI ACQUISTO " + testo.substring(e);
        } else if (testo.equals("")) {
            testo = "ARTICOLI ACQUISTO";
        }
        this.noteTesta.setText(testo);
    }//GEN-LAST:event_rbtAcqActionPerformed

    private void rbtVenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtVenActionPerformed
        String testo = this.noteTesta.getText();
        int i = testo.indexOf("ARTICOLI ACQUISTO");
        int e = i + 17;

        if (i != -1) {
            testo = testo.substring(0, i) + "ARTICOLI VENDITA " + testo.substring(e);
        } else if (testo.equals("")) {
            testo = "ARTICOLI VENDITA";
        }
        this.noteTesta.setText(testo);
    }//GEN-LAST:event_rbtVenActionPerformed

    private void rbtEntActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtEntActionPerformed
        if (this.rbtEnt.isSelected()) {
            this.noteTesta.setText("");
        }
    }//GEN-LAST:event_rbtEntActionPerformed

    private void linguaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_linguaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_linguaActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                JDialogStampaArticoli dialog = new JDialogStampaArticoli(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton annulla;
    public javax.swing.ButtonGroup btgSel;
    public javax.swing.JComboBox formato;
    public javax.swing.JLabel jLabel1;
    public javax.swing.JLabel jLabel2;
    public javax.swing.JLabel jLabel3;
    public javax.swing.JLabel jLabel4;
    public javax.swing.JLabel jLabel5;
    public javax.swing.JLabel jLabel6;
    public javax.swing.JLabel jLabel7;
    public javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JScrollPane jScrollPane2;
    public javax.swing.JComboBox lingua;
    public javax.swing.JComboBox listini;
    public javax.swing.JTextArea notePiede;
    public javax.swing.JTextArea noteTesta;
    public javax.swing.JComboBox ordine;
    public javax.swing.JRadioButton rbtAcq;
    public javax.swing.JRadioButton rbtEnt;
    public javax.swing.JRadioButton rbtVen;
    public javax.swing.JButton stampa;
    // End of variables declaration//GEN-END:variables

    public tnxDbGrid getGriglia() {
        return griglia;
    }

    public void setGriglia(tnxDbGrid griglia) {
        this.griglia = griglia;
    }
}
