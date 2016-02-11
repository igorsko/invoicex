/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tnxbeans;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.TableModelListener;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

/**
 *
 * @author mceccarelli
 */
public class tnxDbGrid_eventi implements TableModelListener {

    private long prevRow = -1;
    tnxDbGrid parentGrid;

    tnxDbGrid_eventi(tnxDbGrid parentGrid) {
        this.parentGrid = parentGrid;
    }

    public void tnxDbGrid_eventi(tnxDbGrid parentGrid) {
        this.parentGrid = parentGrid;
    }

    public void tableChanged(javax.swing.event.TableModelEvent tableModelEvent) {
        //parentGrid.saveDataEntry();
        //controllo se devo salvare
        if (prevRow != tableModelEvent.getFirstRow() && 1 == 2) {
            //salvare inserire ... fare qualcosa
            try {
                //System.out.println("grid_fare:" + meta.toString());
                //SortableTableModel tempSource = (SortableTableModel)tableModelEvent.getSource();
                //System.out.println("grid_fare:" + this.parentGrid.getModel().getColumnCount());

                //griglia colonna e colonna modificata
                if (tableModelEvent.getType() == tableModelEvent.UPDATE) {
                    //aggiorno
                    //System.out.println("grid_fare:colName=" + this.parentGrid.columnsName.get(new Integer(tableModelEvent.getColumn())));

                    //debug
                    java.util.Hashtable tempHash = this.parentGrid.columnsName;
                    java.util.Enumeration tempElem = tempHash.elements();
                    java.util.Enumeration tempKeys = tempHash.keys();
                    while (tempElem.hasMoreElements()) {
                        //System.out.println("grid_fre_keys:" + tempElem.nextElement() + " : " + tempKeys.nextElement());
                    }
                    //System.out.println("grid_fare_1:colName=" + this.parentGrid.columnsName.get("1"));
                    //System.out.println("grid_fare_1:colName=" + this.parentGrid.columnsName.get("codice"));
                    //provo con meta almeno ho anche il tippo di campo
                    //System.out.println("grid_meta:" + this.parentGrid.meta.getColumnLabel(1));
                    //System.out.println("grid_meta:" + this.parentGrid.meta.getColumnType(1));
                    //System.out.println("grid_meta:" + this.parentGrid.meta.getColumnLabel(tableModelEvent.getColumn()));
                    //ok
                    //provo a cercare la chiave
                    Vector valoriChiave = new Vector();
                    for (int i = 0; i < this.parentGrid.dbChiave.size(); i++) {
                        //System.out.println("grid_chiave_nome  :" + i + ":" + this.parentGrid.dbChiave.get(i));
                        //System.out.println("grid_chiave_valore:" + i + ":" + this.parentGrid.getValueAt(tableModelEvent.getFirstRow(), new Integer(this.parentGrid.columnsName.get(this.parentGrid.dbChiave.get(i)).toString()).intValue()));
                        String valore = this.parentGrid.getValueAt(tableModelEvent.getFirstRow(), new Integer(this.parentGrid.columnsName.get(this.parentGrid.dbChiave.get(i)).toString()).intValue()).toString();
                        String tipo = this.parentGrid.meta.getColumnTypeName(new Integer(this.parentGrid.columnsName.get(this.parentGrid.dbChiave.get(i)).toString()).intValue());
                        valoriChiave.add(this.parentGrid.dbChiave.get(i) + " = " + this.parentGrid.pc(valore, tipo));
                        //System.out.println("grid_chiave_vector:" + i + ":" + valoriChiave.get(i));
                    }
                    //ok prendo valore nuovo del campo e nome del campo
                    String nomeCampo = "";
                    String valoreCampo = this.parentGrid.getValueAt(tableModelEvent.getFirstRow(), tableModelEvent.getColumn()).toString();
                    String tipoCampo = this.parentGrid.meta.getColumnTypeName(tableModelEvent.getColumn());
                    //System.out.println("grid_novo_campo:" + valoreCampo + ":" + tipoCampo + ":" + nomeCampo);
                }

            } catch (Exception err) {
                err.printStackTrace();
            }
        }
        prevRow = tableModelEvent.getFirstRow();

        /*
        System.out.println("tnxDbGrid:events:"+tableModelEvent);
        //System.out.println("tnxDbGrid:events1:"+tableModelEvent.getColumn());
        System.out.println("tnxDbGrid:events2:"+tableModelEvent.getFirstRow());
        System.out.println("tnxDbGrid:events3:"+tableModelEvent.getLastRow());
        //System.out.println("tnxDbGrid:events4:"+tableModelEvent.getSource());
        System.out.println("tnxDbGrid:events5:"+tableModelEvent.getType());
        //System.out.println("tnxDbGrid:events6:"+tableModelEvent.INSERT);
        //System.out.println("tnxDbGrid:events7:"+tableModelEvent.UPDATE);
         */

        if (tableModelEvent.getSource() instanceof SortableTableModel) {
            SortableTableModel tempSource = (SortableTableModel) tableModelEvent.getSource();
            if (tableModelEvent.getLastRow() == tempSource.getRowCount() - 1 && tableModelEvent.getType() == tableModelEvent.UPDATE && tableModelEvent.getColumn() == tempSource.getColumnCount() - 1 && parentGrid.dbConsentiAggiunte) {
                tempSource.setValueAt(">", tempSource.getRowCount() - 1, 0);
                //javax.swing.JOptionPane.showMessageDialog(null,"inserimento");
                java.util.Vector tempRigaVuota = new java.util.Vector();
                tempRigaVuota.add("*");
                tempSource.addRow(tempRigaVuota);
            }
        }
    }
}

class CurrencyEditor extends DefaultCellEditor {

    JFormattedTextField ftf;
    NumberFormat integerFormat;
    private Integer minimum, maximum;
    private boolean DEBUG = false;
    tnxDbGrid grid = null;

    public CurrencyEditor(tnxDbGrid grid) {
        super(new JFormattedTextField());
        this.grid = grid;
        ftf = (JFormattedTextField) getComponent();

        //Set up the editor for the integer cells.
        integerFormat = NumberFormat.getInstance(Locale.ITALIAN);
        integerFormat.setMaximumFractionDigits(5);
        integerFormat.setMinimumFractionDigits(2);
        NumberFormatter intFormatter = new NumberFormatter(integerFormat);
        intFormatter.setFormat(integerFormat);

        ftf.setFormatterFactory(new DefaultFormatterFactory(intFormatter));
        ftf.setValue(new Double(0.0d));
        ftf.setHorizontalAlignment(JTextField.TRAILING);
        ftf.setFocusLostBehavior(JFormattedTextField.PERSIST);

        //React when the user presses Enter while the editor is
        //active.  (Tab is handled as specified by
        //JFormattedTextField's focusLostBehavior property.)
        ftf.getInputMap().put(KeyStroke.getKeyStroke(
                KeyEvent.VK_ENTER, 0),
                "check");
        ftf.getActionMap().put("check", new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                if (!ftf.isEditValid()) { //The text is invalid.
                    //if (userSaysRevert()) { //reverted

                    ftf.postActionEvent(); //inform the editor
                    //}

                } else {
                    try {              //The text is valid,

                        ftf.commitEdit();     //so use it.

                        ftf.postActionEvent(); //stop editing

                        CurrencyEditor.this.grid.saveDataEntry(CurrencyEditor.this.grid.getSelectedRow());
                    } catch (java.text.ParseException exc) {
                    }
                }
            }
        });
    }
    //Override to invoke setValue on the formatted text field.

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        JFormattedTextField ftf = (JFormattedTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);
        if (tnxDbGrid.getDoubleEng(String.valueOf(value)) > 0) {
            Double d = new Double(tnxDbGrid.getDoubleEng(String.valueOf(value)));
            ftf.setValue(d);
        } else {
            ftf.setValue(new Double(0));
        }
        return ftf;
    }

    //Override to ensure that the value remains an Integer.
    public Object getCellEditorValue() {
        JFormattedTextField ftf = (JFormattedTextField) getComponent();
        Object o = ftf.getValue();
        if (o instanceof Integer) {
            return o;
        } else if (o instanceof Number) {
            return o;
        } else {
            if (DEBUG) {
                System.out.println("getCellEditorValue: o isn't a Number");
            }
            try {
                return integerFormat.parseObject(o.toString());
            } catch (ParseException exc) {
                System.err.println("getCellEditorValue: can't parse o: " + o);
                return null;
            }
        }
    }

}