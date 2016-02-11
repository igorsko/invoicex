package it.tnx.accessoUtenti;

import gestioneFatture.Db;
import gestioneFatture.main;
import it.tnx.commons.SwingUtils;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;

public class Permesso {
    // VARIABILI PER DESCRIZIONE PERMESSI
    public static final Integer PERMESSO_ANAGRAFICA_CLIENTI = 1;
    public static final Integer PERMESSO_ANAGRAFICA_ARTICOLI = 2;
    public static final Integer PERMESSO_ANAGRAFICA_TIPI_PAGAMENTO = 3;
    public static final Integer PERMESSO_ANAGRAFICA_CODICI_IVA = 4;
    public static final Integer PERMESSO_ANAGRAFICA_ALTRE = 5;
    public static final Integer PERMESSO_IMPOSTAZIONI = 6;
    public static final Integer PERMESSO_GESTIONE_UTENTI = 7;
    public static final Integer PERMESSO_MAGAZZINO = 8;
    public static final Integer PERMESSO_STATISTICHE = 9;
    public static final Integer PERMESSO_AGENTI = 10;
    public static final Integer PERMESSO_ORDINI_VENDITA = 11;
    public static final Integer PERMESSO_DDT_VENDITA = 12;
    public static final Integer PERMESSO_FATTURE_VENDITA = 13;
    public static final Integer PERMESSO_ORDINI_ACQUISTO = 14;
    public static final Integer PERMESSO_DDT_ACQUISTO = 15;
    public static final Integer PERMESSO_FATTURE_ACQUISTO = 16;
    public static final Integer PERMESSO_SCADENZARIO = 17;
    public static final Integer PERMESSO_GESTIONE_IVA = 18;
    public static final Integer PERMESSO_CONTABILITA = 19;

    // TIPOLOGIE DI PERMESSO
    public static final Integer PERMESSO_TIPO_LETTURA = 1;
    public static final Integer PERMESSO_TIPO_SCRITTURA = 2;
    public static final Integer PERMESSO_TIPO_CANCELLA = 3;

    private String nomePermesso;
    private HashMap<Integer, Integer> permessi = new HashMap<Integer, Integer>();

    public Permesso(int id, int permessoLettura, int permessoScrittura, int permessoCancella) {
        ResultSet rs = Db.openResultSet(Db.getConn(), "SELECT * FROM accessi_tipi_permessi WHERE id = " + Db.pc(id, Types.INTEGER));
        try {
            if (rs.next()) {
                nomePermesso = rs.getString("descrizione");
                this.permessi.put(Permesso.PERMESSO_TIPO_LETTURA, new Integer(permessoLettura));
                this.permessi.put(Permesso.PERMESSO_TIPO_SCRITTURA, new Integer(permessoScrittura));
                this.permessi.put(Permesso.PERMESSO_TIPO_CANCELLA, new Integer(permessoCancella));
            }
        } catch (SQLException ex) {
//            ex.printStackTrace();
            SwingUtils.showErrorMessage(main.getPadreFrame(), "Permesso non applicabile", "Errore gestione permessi", true);
        }
    }

    public String getNomePermesso() {
        return nomePermesso;
    }

    public HashMap<Integer, Integer> getPermessi() {
        return permessi;
    }

    public Boolean getPermesso(int tipoPermesso){
        if(permessi.get(tipoPermesso) != null){
            return permessi.get(tipoPermesso) == 1;
        } else {
            return false;
        }
    }
    @Override
    public String toString() {
        String output = this.nomePermesso;
        output += " Lettura: " + this.permessi.get(Permesso.PERMESSO_TIPO_LETTURA);
        output += " Scrittura: " + this.permessi.get(Permesso.PERMESSO_TIPO_SCRITTURA);
        output += " Cancellazione: " + this.permessi.get(Permesso.PERMESSO_TIPO_CANCELLA);

        return output;
    }
    
}
