/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gestioneFatture;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JTable;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author mceccarelli
 */
public class FoglioSelectionListener implements ListSelectionListener {

    JTable table;
    int prevRow = -1;
    boolean flagSalvare = false;

    // It is necessary to keep the table since it is not possible
    // to determine the table from the event's source
    public FoglioSelectionListener(JTable table) {
        this.table = table;
    }

    public void setFlagSalvare(boolean val) {
        this.flagSalvare = val;
    }

    public void valueChanged(javax.swing.event.ListSelectionEvent listSelectionEvent) {

        //caricare la desc art. sulla casella e salvare quella di prima
        Menu m = main.getPadre();
        JDesktopPane desk = m.getDesktopPane();
        JInternalFrame[] iframes = desk.getAllFrames();
        int curRow = table.getSelectedRow();

        for (int i = 0; i < iframes.length; i++) {

            if (iframes[i].getTitle().equalsIgnoreCase("zoom")) {

                frmZoomDesc zoom = (frmZoomDesc) iframes[i];
                String desc = "";

                try {

                    if (table.getValueAt(table.getSelectedRow(), 2) != null) {
                        desc = String.valueOf(table.getValueAt(table.getSelectedRow(), 2));

                        //prima memorizzo il valore precedente
                        if (prevRow >= 0 && curRow != prevRow) {

                            if (flagSalvare == true) {
                                table.setValueAt(zoom.getDesc(), prevRow, 2);
                                setFlagSalvare(false);
                            }
                        }

                        //poi visualizzo il nuovo
                        zoom.setDesc(desc);
                    } else {
                        zoom.setDesc("");
                    }
                } catch (Exception err) {
                    System.out.println("impossibile controllare il cambio di selezione");
                }

                break;
            }
        }

        prevRow = curRow;
    }
}