package gestioneFatture;


public class ArticoloHint {

    public String codice;
    public String matricola="";
    public String lotto="";
    public String descrizione;
    public String codice_fornitore="";
    public String codice_a_barre="";

    @Override
    public String toString() {
        String ret = "";
        if (descrizione != null && descrizione.length() > 0) {
            ret += descrizione + " [" + codice + "]";
        } else {
            ret += "[" + codice + "]";
        }
        if (matricola != null && matricola.length() > 0) {
            ret += " / S/N " + matricola;
        }
        if (lotto != null && lotto.length() > 0) {
            ret += " / Lotto " + lotto;
        }
        if (codice_fornitore != null && codice_fornitore.length() > 0) {
            ret += " / Cod. Forn. " + codice_fornitore;
        }
        if (codice_a_barre != null && codice_a_barre.length() > 0) {
            ret += " / Codice a Barre " + codice_a_barre;
        }
        return ret;
    }
}