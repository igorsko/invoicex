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



/*
 * JDialogMatricole.java
 *
 * Created on 21 maggio 2007, 12.03
 */

package it.tnx.invoicex.gui;

import com.jidesoft.swing.AutoCompletionComboBox;
import com.jidesoft.swing.SearchableUtils;
import it.tnx.Db;
import it.tnx.commons.table.EditorUtils;
import it.tnx.commons.table.EditorUtils.ComboEditor;
import it.tnx.invoicex.Magazzino;
import it.tnx.invoicex.data.Giacenza;
import java.awt.Component;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 *
 * @author  mceccarelli
 */
public class JDialogMatricole extends javax.swing.JDialog {
    public Object[][] values = null;
    public TableModel model = null;
    private int riga;
    private Integer id_riga;
    private String articolo;
    public boolean ok = true;
    public boolean matricoleDaInserire  = false;
    String nomeTabMatricole;
    
    /** Creates new form JDialogMatricole */
    public JDialogMatricole(java.awt.Frame parent, boolean modal, int righe, int riga, String articolo, String serie, String numero, String anno, String nomeTabMatricole, Integer id_riga) {
        super(parent, modal);
        this.riga = riga;
        this.id_riga = id_riga;
        this.articolo = articolo;
        this.nomeTabMatricole = nomeTabMatricole;
        
        ArrayList lista_prima = new ArrayList();
        String sql = "";
        values = new Object[righe][1];
        
        initComponents();
        
        jTable1.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        
        try {
            //prima vado a togliere le matricole precedenti se presenti
            sql = "select * from " + nomeTabMatricole;
            sql += " where numero = '" + numero + "'";
            sql += " and anno = '" + anno + "'";
            sql += " and riga = '" + riga + "'";
            
            System.out.println("seleziono le eventuali matricole abbinate:" + sql);
            ResultSet r = Db.openResultSet(sql);
            int i = 0;
            while (r.next()) {
                System.out.println("memorizzo:" + r.getString("matricola"));
                lista_prima.add(r.getString("matricola"));
                try {
                    values[i][0] = r.getString("matricola");
                } catch (ArrayIndexOutOfBoundsException e1) {}
                i++;
            }
            
            sql = "delete from " + nomeTabMatricole + " where numero = '" + numero + "'";
            sql += " and anno = '" + anno + "'";
            sql += " and riga = '" + riga + "'";
            System.out.println("elimino le matricole:" + sql);
            Db.executeSql(sql);
        } catch (Exception err) {
            err.printStackTrace();
        }
        
        model = new javax.swing.table.DefaultTableModel(values, new String [] {"Matricola"}) {
            Class[] types = new Class [] {
                java.lang.String.class
            };
            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
            
        };
        jTable1.setModel(model);
        
        ArrayList<Giacenza> beans = Magazzino.getGiacenza(true, articolo, null);
        
        // Set the combobox editor on the 1st visible column
        int vColIndex = 0;
        TableColumn col = jTable1.getColumnModel().getColumn(vColIndex);

        AutoCompletionComboBox combo2b = new AutoCompletionComboBox(new Vector(beans));
        combo2b.setBorder(BorderFactory.createEmptyBorder());
        ComboEditor editor2b = new EditorUtils.ComboEditor(combo2b);
        col.setCellEditor(editor2b);

//        JComboBox combo = new JComboBox(new Vector(beans));
//        combo.setEditable(false); // combobox searchable only works when combobox is not editable.
//        SearchableUtils.installSearchable(combo);
//        //combo.setEditable(true);
//        DefaultCellEditor editor1 = new DefaultCellEditor(combo);
//        col.setCellEditor(editor1);

//        JTextField cellEditorTextField = new JTextField();
//        ArrayList beans_stringhe = new ArrayList();
//        System.out.println("########## inizio");
//        for (Giacenza g : beans) {
//            if (g.getMatricola() != null) {
//                System.out.println("g.getMatricola():" + g.getMatricola());
//                beans_stringhe.add(g.getMatricola());
//            }
//        }
//        System.out.println("########## fine");
//        ListDataIntelliHints fontIntellihints = new ListDataIntelliHints(cellEditorTextField, beans_stringhe);
//        fontIntellihints.setCaseSensitive(false);
//        col.setCellEditor(new DefaultCellEditor(cellEditorTextField) {
//        });

        jTable1.setSurrendersFocusOnKeystroke(true);

        
        // If the cell should appear like a combobox in its
        // non-editing state, also set the combobox renderer
        //col.setCellRenderer(new MyComboBoxRenderer(values));
        
        jTable1.setRowHeight(20);
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Gestione matricole");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null}
            },
            new String [] {
                "Matricola"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/22x22/devices/media-floppy.png"))); // NOI18N
        jButton1.setText("Conferma");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/22x22/actions/edit-undo.png"))); // NOI18N
        jButton2.setText("Annulla");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel1.setText("Inserire i codici matricola");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jButton2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButton1))
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 231, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton1)
                    .add(jButton2))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        model = null;
        ok = false;
        dispose();
    }//GEN-LAST:event_jButton2ActionPerformed
    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        //prima controlla che si siano scelte tutte le matricole richieste
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            Object value = jTable1.getValueAt(i, 0);
            if (value == null) {
                JOptionPane.showMessageDialog(this, "Ci sono una o più matricole da inserire", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        //prima controlla che non si sia scelto due volte la solita matricola
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            Object value = jTable1.getValueAt(i, 0);
            int conta = 0;
            for (int i2 = 0; i2 < jTable1.getRowCount(); i2++) {
                Object value2 = jTable1.getValueAt(i2, 0);
                if (value2.equals(value)) conta++;
            }
            if (conta >= 2) {
                JOptionPane.showMessageDialog(this, "Ci sono una o più matricole inserite più di una volta", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        dispose();
    }//GEN-LAST:event_jButton1ActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
    
    public void setMatricoleDaInserire(boolean come) {
        if (come) {
            matricoleDaInserire = true;
            // Set the combobox editor on the 1st visible column
            int vColIndex = 0;
            TableColumn col = jTable1.getColumnModel().getColumn(vColIndex);
            col.setCellEditor(new DefaultCellEditor(new JTextField()));
        }
    }
}

class MyComboBoxRenderer extends JComboBox implements TableCellRenderer {
    public MyComboBoxRenderer(String[] items) {
        super(items);
    }
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            super.setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }
        // Select the current value
        setSelectedItem(value);
        return this;
    }
}