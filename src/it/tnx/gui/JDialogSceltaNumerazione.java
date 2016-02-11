/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JDialogIva21.java
 *
 * Created on 16-set-2011, 11.29.30
 */

package it.tnx.gui;

import gestioneFatture.Db;
import gestioneFatture.main;
import it.tnx.commons.CastUtils;
import it.tnx.commons.DbUtils;
import it.tnx.commons.SwingUtils;
import it.tnx.invoicex.InvoicexUtil;
import java.awt.Color;
import java.net.URL;
import java.sql.Types;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mceccarelli
 */
public class JDialogSceltaNumerazione extends javax.swing.JDialog {

    /** Creates new form JDialogIva21 */
    public JDialogSceltaNumerazione(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        procedi = new javax.swing.JButton();
        stato = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        procedi1 = new javax.swing.JButton();
        link = new org.jdesktop.swingx.JXHyperlink();
        procedi2 = new javax.swing.JButton();
        link1 = new org.jdesktop.swingx.JXHyperlink();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Scelta del tipo di numerazione");

        jLabel1.setFont(jLabel1.getFont().deriveFont(jLabel1.getFont().getStyle() | java.awt.Font.BOLD, jLabel1.getFont().getSize()+1));
        jLabel1.setText("Scelta del tipo di numerazione per le fatture dal 1° Gennaio 2013");

        jLabel2.setText("<html>\nDal 1° gennaio 2013, con l’entrata in vigore della Legge di stabilità 2013 tutte le fatture emesse dovranno <br>\ncontenere le indicazioni previste dal riformulato articolo 21 del decreto Iva il quale prevede che ogni fattura<br>\n debba evidenziare un 'numero progressivo che la identifichi in modo univoco'.<br>\n<br>\nHai tre scelte:<br>\nA - <b>Numerazione progressiva CON  interruzione di anno in anno e stampa del numero/anno</b><br>\nB - <b>Numerazione progressiva SENZA interuzzioni di anno in anno</b><br>\nC - <b>Numerazione progressiva CON  interruzione di anno in anno e stampa del solo numero</b><br>\n&nbsp;&nbsp;&nbsp;&nbsp; (quindi nessuna variazione rispetto a prima) <br>\n<br>\nNel caso A la numerazione delle fatture riparte da 1 ad ogni inizio anno ma in stampa verrà aggiunto l'anno accanto <br>\nal numero per renderlo univoco, ad esempio la fattura numero 1 del 2013 in stampa sarà '1/2013', se usate più serie <br>\ndi numerazioni sarà ad esempio 'A/1/2013'<br>\n<br>\nNel caso B la numerazione proseguirà semplicemente progressivamente di anno in anno. Ad esempio se nel 2013 l'ultima <br>\nsarà la numero 100, la prima fattura del 2014 sarà la numero 101.<br>\n<br>\nNel caso C la numerazione non subisce variazioni e viene utilizzato il metodo usato fino ad oggi<br>\nQuesto è ufficialmente stato chiarito il 10/01/2013 con la <b>Risoluzione n. 1/E</b> dell'Agenzia delle entrate,<br>\npotete cliccare su 'info risoluzione 1/E' per leggere l'articolo<br>\n<br>\n<b>Se non volete cambiare niente cliccate sulla scelta C.</b>\n</html>");

        procedi.setText("<html><b>A</b> - Clicca qui per proseguire CON l'interruzione di anno in anno</html>");
        procedi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                procediActionPerformed(evt);
            }
        });

        stato.setFont(stato.getFont().deriveFont(stato.getFont().getStyle() | java.awt.Font.BOLD, stato.getFont().getSize()+2));

        jLabel4.setText("<html> Una volta eseguita la procedura si puo' cambiare da Utilità->Impostazioni->Tipo numerazione Fatture<br> </html>");

        procedi1.setText("<html><b>B</b> - Clicca qui per proseguire SENZA l'interruzione di anno in anno</html>");
        procedi1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                procedi1ActionPerformed(evt);
            }
        });

        link.setText("(maggiori info)");
        link.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                linkActionPerformed(evt);
            }
        });

        procedi2.setText("<html><b>C</b> - Clicca qui per proseguire come prima</html>");
        procedi2.setToolTipText("<html>Con la Risoluzione n. 1/E del 10/01/2013, l'Agenzia delle Entrate ha <br>chiarito che è possibile continuare la numerazione per anno solare resa univoca<br> dalla data apposta sulla fattura, quindi come fatto fino ad oggi</html>");
        procedi2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                procedi2ActionPerformed(evt);
            }
        });

        link1.setText("(info risoluzione 1/E)");
        link1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                link1ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(procedi)
                    .add(procedi1)
                    .add(jLabel2)
                    .add(layout.createSequentialGroup()
                        .add(procedi2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(link1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(jLabel1)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(link, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(stato)
                            .add(jLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(link, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(procedi, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(procedi1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(procedi2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(link1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(stato)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void procediActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_procediActionPerformed
        try {
            String sql = "update dati_azienda set tipo_numerazione = " + InvoicexUtil.TIPO_NUMERAZIONE_ANNO + ", tipo_numerazione_confermata = 1";
            DbUtils.tryExecQuery(Db.getConn(), sql);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        main.getPadrePanel().sceltaNumerazione.setVisible(false);
        dispose();
    }//GEN-LAST:event_procediActionPerformed

    private void procedi1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_procedi1ActionPerformed
        try {
            String sql = "update dati_azienda set tipo_numerazione = " + InvoicexUtil.TIPO_NUMERAZIONE_ANNO_INFINITA + ", tipo_numerazione_confermata = 1";
            DbUtils.tryExecQuery(Db.getConn(), sql);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        main.getPadrePanel().sceltaNumerazione.setVisible(false);
        dispose();
    }//GEN-LAST:event_procedi1ActionPerformed

    private void linkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_linkActionPerformed
        try {
            URL url = new URL("http://www.fiscoetasse.com/approfondimenti/11230-fattura-cosa-cambia-dal-2013.html");
            System.err.println("url: " + url);
            SwingUtils.openUrl(url);
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }//GEN-LAST:event_linkActionPerformed

    private void procedi2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_procedi2ActionPerformed
        try {
            String sql = "update dati_azienda set tipo_numerazione = " + InvoicexUtil.TIPO_NUMERAZIONE_ANNO_SOLO_NUMERO + ", tipo_numerazione_confermata = 1";
            DbUtils.tryExecQuery(Db.getConn(), sql);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        main.getPadrePanel().sceltaNumerazione.setVisible(false);
        dispose();
    }//GEN-LAST:event_procedi2ActionPerformed

    private void link1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_link1ActionPerformed
        try {
            URL url = new URL("http://www.fiscooggi.it/normativa-e-prassi/articolo/fatture-2013-inizio-1-o-x1si-pu%C3%B2-ricominciare-o-continuare");
            System.err.println("url: " + url);
            SwingUtils.openUrl(url);
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }//GEN-LAST:event_link1ActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JDialogSceltaNumerazione dialog = new JDialogSceltaNumerazione(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private org.jdesktop.swingx.JXHyperlink link;
    private org.jdesktop.swingx.JXHyperlink link1;
    private javax.swing.JButton procedi;
    private javax.swing.JButton procedi1;
    private javax.swing.JButton procedi2;
    private javax.swing.JLabel stato;
    // End of variables declaration//GEN-END:variables

}
