package gestioneFatture;


public class ClienteHint {

    public String codice;
    public String ragione_sociale;
    public boolean obsoleto = false;

    @Override
    public String toString() {
        String ret = "";
        ret = ragione_sociale + " [" + codice + "]";
        return ret;
    }
}