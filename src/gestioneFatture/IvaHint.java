package gestioneFatture;

import it.tnx.commons.FormatUtils;


public class IvaHint {

    public String codice;
    public String descrizione;
    public double percentuale = 0;

    @Override
    public String toString() {
        String ret = "";
        ret = descrizione + " [ codice: " + codice + " perc.: " + FormatUtils.formatPerc(percentuale, true) + "]";
        return ret;
    }
}