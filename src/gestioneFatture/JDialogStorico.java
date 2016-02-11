/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JDialogStorico.java
 *
 * Created on 14-mar-2011, 9.32.55
 */
package gestioneFatture;

import it.tnx.Db;
import com.caucho.hessian.io.Hessian2Input;
import it.tnx.commons.CastUtils;
import it.tnx.commons.DateUtils;
import it.tnx.commons.DbUtils;
import it.tnx.commons.DebugUtils;
import it.tnx.commons.FormatUtils;
import it.tnx.commons.SwingUtils;
import it.tnx.invoicex.InvoicexUtil;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JInternalFrame;
import javax.swing.table.DefaultTableModel;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingworker.SwingWorker;

/**
 *
 * @author mceccarelli
 */
public class JDialogStorico extends javax.swing.JDialog {

    Integer id_doc = null;
    String tipo_doc = null;
    JInternalFrame elenco = null;

    /** Creates new form JDialogStorico */
    public JDialogStorico(java.awt.Frame parent, boolean modal, JInternalFrame elenco) {
        super(parent, modal);
        initComponents();
        this.elenco = elenco;
    }

    public void leggiDaStorico(final String cosa, final int id) {
        this.id_doc = id;
        this.tipo_doc = cosa;
        
        SwingUtils.mouse_wait(this);
        SwingWorker w = new SwingWorker() {

            List<Map> list;

            @Override
            protected Object doInBackground() throws Exception {

                String msg = "";
                try {
                    String sql = "select id, data from storico where nota like 'modifica " + cosa + " id:" + id + " %' order by id desc";
                    System.out.println("sql = " + sql);
                    list = DbUtils.getListMap(Db.getConn(), sql);
                    System.out.println("list = " + list);
                    for (Map rec : list) {
                        rec.put("storico", leggiDaStorico(CastUtils.toInteger(rec.get("id"))));
                    }
                } catch (Exception ex) {
                    Logger.getLogger(InvoicexUtil.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            }

            @Override
            protected void done() {
                super.done();
                try {
                    listStorico.setModel(new DefaultListModel());
                    DefaultListModel m = (DefaultListModel) listStorico.getModel();
                    for (Map mrec : list) {
                        DatiStorico dati = new DatiStorico();
                        dati.recStorico = mrec;
                        m.addElement(dati);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    SwingUtils.mouse_def(JDialogStorico.this);
                }
            }
        };
        w.execute();
    }

    private void riempiTab(DatiStorico dati) {
        DebugUtils.dump(dati.recStorico);
        Map storico = (Map) dati.recStorico.get("storico");
        Map testa = (Map) ((List) storico.get("testa")).get(0);
        Map cliente = (Map) ((List) storico.get("cliente")).get(0);
        String inte = "<html>Fattura " + (StringUtils.isEmpty(CastUtils.toString(testa.get("serie"))) ? "" : testa.get("serie") + "/") + testa.get("numero") + " del " + DateUtils.formatDate((Date)testa.get("data")) + " anno:" + testa.get("anno") + " tipo_fattura:" + testa.get("tipo_fattura") + " pagamento:" + testa.get("pagamento");
        inte += "<br>";
        inte += "Cliente: " + cliente.get("codice") + " - " + cliente.get("ragione_sociale");
        inte += "</html>";
        labInte.setText(inte);

        List<Map> righe = (List) storico.get("righe");
        Collections.sort(righe, new Comparator<Map>() {
            public int compare(Map m1, Map m2) {
                return CastUtils.toInteger0(m1.get("riga")).compareTo(CastUtils.toInteger0(m2.get("riga")));
            }
        });
        DefaultTableModel model = (DefaultTableModel) tab.getModel();
        model.setRowCount(0);
        int totr = 0;
        for (Map mr : righe) {
            model.addRow(new Object[] {mr.get("riga"), mr.get("codice_articolo"), mr.get("descrizione"), mr.get("quantita"), mr.get("prezzo")});
            totr++;
        }

        String piede = "<html><center>righe: " + totr + " Totale documento <b>" + FormatUtils.formatEuroIta(CastUtils.toDouble0(testa.get("totale"))) + "</b></center></html>";
        labPiede.setText(piede);
        return;
    }

    public static class DatiStorico {
        Map recStorico;

        @Override
        public String toString() {
            return DateUtils.formatDateTime((Date)recStorico.get("data"));
        }

    }

    public static Map leggiDaStorico(int idStorico) throws Exception {
        //test di rilettura
        Map ret = null;
        System.out.println("lettura sotrico id:" + idStorico);
        ResultSet rtest = DbUtils.tryOpenResultSetEditable(Db.getConn(), "select * from storico where id = " + idStorico);
        if (rtest.next()) {
            try {
                //prima provo serilalizzazione java (vecchi ostorico ma problemi con class version)
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(rtest.getBytes("dati")));
                Object oread = ois.readObject();
                System.out.println("rilettura storico:");
                DebugUtils.dump(oread);
                ret = (Map) oread;
//                String sqlrighe = InvoicexUtil.stampaSqlRighe(oread);
//                if (sqlrighe.length() > 0) {
//                    msg += "da storico " + rtest.getString("id") + " data " + rtest.getString("data") + "\n" + sqlrighe + "\n\n";
//                }
            } catch (Exception e) {
                System.out.println("rileggo con hessian per err:" + e.getMessage());
                //provo con nuovo sistema hessian
                try {
                    byte[] bytes64dec = Base64.decodeBase64(rtest.getBytes("dati"));
                    ByteArrayInputStream bin = new ByteArrayInputStream(bytes64dec);
                    Hessian2Input in = new Hessian2Input(bin);
                    in.startMessage();
                    Object oread = in.readObject();
                    in.completeMessage();
                    in.close();
                    bin.close();
                    DebugUtils.dump(oread);
                    ret = (Map) oread;
//                    String sqlrighe = InvoicexUtil.stampaSqlRighe(oread);
//                    if (sqlrighe.length() > 0) {
//                        msg += "da storico " + rtest.getString("id") + " data " + rtest.getString("data") + "\n" + sqlrighe + "\n\n";
//                    }
                } catch (Exception e2) {
                    System.out.println("impossibile leggere storico " + idStorico + " err:" + e2.getMessage());
                }
            }
        }
        try {
            rtest.getStatement().close();
            rtest.close();
        } catch (Exception e) {
        }
        return ret;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        listStorico = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        tab = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        labInte = new javax.swing.JLabel();
        labPiede = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Versione precedente documento");

        listStorico.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "..." };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        listStorico.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listStoricoValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(listStorico);

        tab.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "riga", "articolo", "descrizione", "quantità", "prezzo"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tab);
        tab.getColumnModel().getColumn(0).setPreferredWidth(10);
        tab.getColumnModel().getColumn(1).setPreferredWidth(20);
        tab.getColumnModel().getColumn(2).setPreferredWidth(150);
        tab.getColumnModel().getColumn(3).setPreferredWidth(20);
        tab.getColumnModel().getColumn(4).setPreferredWidth(20);

        jLabel1.setText("Seleziona la versione precedente qui a sinistra e controlla il contenuto a destra");

        labInte.setText("...");

        labPiede.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labPiede.setText("...");

        jButton1.setText("Ripristina versione selezionata");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Annulla");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(layout.createSequentialGroup()
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 255, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 521, Short.MAX_VALUE)
                            .add(labInte, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 521, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, labPiede, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 521, Short.MAX_VALUE)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(jButton2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(labInte)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(labPiede))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton1)
                    .add(jButton2))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void listStoricoValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listStoricoValueChanged
        if (!evt.getValueIsAdjusting()) {
            DatiStorico dati = (DatiStorico) listStorico.getSelectedValue();
            riempiTab(dati);
        }
    }//GEN-LAST:event_listStoricoValueChanged

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (SwingUtils.showYesNoMessage(this, "Sicuro di ripristinare questa versione ?\n(La versione sostituita verrà storicizzata)")) {
            InvoicexUtil.storicizza("ripristino fattura id:" + id_doc, tipo_doc, id_doc);
            String sql = "delete from righ_fatt where id_padre = " + id_doc;
            System.out.println("sql: " + sql);
            try {
                DbUtils.tryExecQuery(Db.getConn(), sql);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            DatiStorico dati = (DatiStorico) listStorico.getSelectedValue();
            Map storico = (Map) dati.recStorico.get("storico");
            List<Map> righe = (List) storico.get("righe");
            List<Map> l = righe;
            for (Map ml : l) {
                sql = "insert righ_fatt set " + DbUtils.prepareSqlFromMap(ml);
                System.out.println("sql: " + sql);
                try {
                    DbUtils.tryExecQuery(Db.getConn(), sql);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                DbUtils.tryExecQuery(Db.getConn(), "UPDATE righ_fatt set totale_imponibile = ROUND(CAST(  IF(arrotondamento_parametro = '0',  (prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - ((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)))*(IFNULL(sconto2, 0.00)/100))) * IFNULL(quantita, 0.00), IF(arrotondamento_tipo = 'Inf.',   FLOOR(((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - ((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)))*(IFNULL(sconto2, 0.00)/100))) )/CAST(arrotondamento_parametro AS DECIMAL(5,2))) * CAST(arrotondamento_parametro AS DECIMAL(5,2)) * IFNULL(quantita, 0.00), CEIL(((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)) - ((prezzo - (prezzo*(IFNULL(sconto1, 0.00)/100)))*(IFNULL(sconto2, 0.00)/100))) )/CAST(arrotondamento_parametro AS DECIMAL(5,2))) * CAST(arrotondamento_parametro AS DECIMAL(5,2)) * IFNULL(quantita, 0.00))) AS DECIMAL(10,2)),2) where id_padre = " + id_doc);
            } catch (Exception e) {
                e.printStackTrace();
            }

//            //aggiorno tutte le form elenco fatture
//            InvoicexUtil.aggiornaElenchiFatture();

            //apro fattura ripristinata
            ((frmElenFatt)elenco).publicbutModiActionPerformed();
            dispose();
            SwingUtils.showInfoMessage(this, "Le righe della fattura sono state ripristinate, adesso conferma la fattura per rigenerare tutti i dati collegati");
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        dispose();
    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel labInte;
    private javax.swing.JLabel labPiede;
    private javax.swing.JList listStorico;
    private javax.swing.JTable tab;
    // End of variables declaration//GEN-END:variables
}
