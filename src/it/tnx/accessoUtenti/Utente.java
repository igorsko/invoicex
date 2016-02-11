package it.tnx.accessoUtenti;

import gestioneFatture.Db;
import gestioneFatture.main;
import it.tnx.commons.SwingUtils;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.HashMap;
import java.util.Set;

public class Utente {
    private String nomeUtente;
    private Integer idUtente;
    private Integer idRuolo;
    private HashMap<Integer, Permesso> permessi = new HashMap<Integer, Permesso>();
    
    public Utente(int id){
        String sql = "SELECT ute.id, ute.username, rol.descrizione as ruolo, rol.id as id_ruolo ";
        sql += "FROM accessi_utenti ute LEFT JOIN accessi_ruoli rol ON ute.id_role = rol.id ";
        sql += "WHERE ute.id = " + Db.pc(id, Types.INTEGER);

        ResultSet rs = Db.openResultSet(sql);
        try {
            if(rs.next()){
                idUtente = rs.getInt("id");
                refreshPermessi();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void refreshPermessi() {
        String sql = "SELECT ute.id, ute.username, rol.descrizione as ruolo, rol.id as id_ruolo ";
        sql += "FROM accessi_utenti ute LEFT JOIN accessi_ruoli rol ON ute.id_role = rol.id ";
        sql += "WHERE ute.id = " + Db.pc(idUtente, Types.INTEGER);

        ResultSet rs = Db.openResultSet(sql);
        try {
            if(rs.next()){
                idUtente = rs.getInt("id");
                nomeUtente = rs.getString("username");
                idRuolo = rs.getInt("id_ruolo");

                sql = "SELECT * FROM accessi_ruoli_permessi WHERE id_role = " + Db.pc(idRuolo, Types.INTEGER);
                ResultSet rsPermessi = Db.openResultSet(sql);
                permessi.clear();
                while(rsPermessi.next()){
                    int idp         = rsPermessi.getInt("id_privilegio");
                    int lettura     = rsPermessi.getInt("lettura");
                    int scrittura   = rsPermessi.getInt("scrittura");
                    int cancella    = rsPermessi.getInt("cancella");
                    
                    Permesso prm = new Permesso(idp, lettura, scrittura, cancella);
                    permessi.put(idp, prm);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }

    public String getNomeUtente(){
        return nomeUtente;
    }

    public Integer getIdUtente() {
        return idUtente;
    }

    public Integer getIdRuolo() {
        return idRuolo;
    }

    public HashMap<Integer, Permesso> getPermessi(){
        return permessi;
    }

    public Permesso getPermesso(int idPermesso) throws Exception{
        if(permessi.containsKey(idPermesso)){
            return permessi.get(idPermesso);
        } else {
            throw new Exception("Permesso Inesistente");
        }
    }

    public Boolean getPermesso(int idPermesso, int tipoPermesso){
        if(permessi.containsKey(idPermesso)){
            return permessi.get(idPermesso).getPermesso(tipoPermesso);
        } else {
            System.out.println("Errore nella lettura dei permessi: idPermesso=" + idPermesso + " tipoPemresso:" + tipoPermesso + ", provo refreshPermessi");
            Thread.dumpStack();
            refreshPermessi();
            if(permessi.containsKey(idPermesso)){
                return permessi.get(idPermesso).getPermesso(tipoPermesso);                                
            } else {
//                SwingUtils.showErrorMessage(main.getPadreFrame(), "Errore nella lettura dei permessi", "Errore Permesso");
                System.out.println("Errore nella lettura dei permessi: idPermesso=" + idPermesso + " tipoPemresso:" + tipoPermesso + ", questo era il secondo tentativo");
                Thread.dumpStack();
                return false;
            }
        }
    }
    
    @Override
    public String toString() {
        String output = this.nomeUtente + System.getProperty("line.separator");
        Set<Integer> chiavi = permessi.keySet();
        for(Integer idPermesso : chiavi){
            Permesso permesso = permessi.get(idPermesso);
            output += permesso.toString() + System.getProperty("line.separator");
        }
        return output;
    }


}
