/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package reports;

import gestioneFatture.main;
import it.tnx.commons.CastUtils;
import it.tnx.commons.SwingUtils;
import java.util.ArrayList;

/**
 *
 * @author atoce
 */
public class JRDSBarcode extends JRDSBase implements net.sf.jasperreports.engine.JRDataSource {

    private int conta = 0;
    ArrayList<ArrayList<String>> valori = new ArrayList<ArrayList<String>>();
    ArrayList<String> current = new ArrayList<String>();
    String testoLibero = main.fileIni.getValue("barcode", "testo_libero", "");
    boolean articoloSopra = main.fileIni.getValueBoolean("barcode", "articolo_sopra", true);
    
    public JRDSBarcode(ArrayList<ArrayList> barcode) {
        init(barcode, true);
    }

    public JRDSBarcode(ArrayList<ArrayList> barcode, boolean perQuantita) {
        init(barcode, perQuantita);
    }

    private void init(ArrayList<ArrayList> barcode, boolean perQuantita) {
        for (int i = 0; i < barcode.size(); i++) {
            ArrayList riga = barcode.get(i);

            String codiceArti = CastUtils.toString(riga.get(0));
            String codiceBarre = CastUtils.toString(riga.get(1));
            int quantita = CastUtils.toInteger(riga.get(2));

            if (perQuantita) {
                for (int j = 1; j <= quantita; j++) {
                    ArrayList<String> temp = new ArrayList<String>();
                    temp.add(0, codiceArti);
                    temp.add(1, codiceBarre);
                    valori.add(temp);
                }
            } else {
                ArrayList<String> temp = new ArrayList<String>();
                temp.add(0, codiceArti);
                temp.add(1, codiceBarre);
                valori.add(temp);
            }
        }
    }

    @Override
    public Object getFieldValue(net.sf.jasperreports.engine.JRField jRField) throws net.sf.jasperreports.engine.JRException {

        try {
            if (jRField.getName().equalsIgnoreCase("codice_a_barre")) {
                return current.get(1);
            } else if (jRField.getName().equalsIgnoreCase("codice_articolo")) {
                return current.get(0);
            } else if (jRField.getName().equalsIgnoreCase("testo_libero")) {
                return testoLibero;
            }
        } catch (Exception e) {
            SwingUtils.showErrorMessage(main.getPadre(), "Impossibile leggere alcuni dei dati", "Errore lettura dati");
        }

        return null;
    }

    public boolean next() throws net.sf.jasperreports.engine.JRException {
        if (conta < valori.size()) {
            current = valori.get(conta);
            conta++;
            return true;
        }

        return false;

    }
}
