/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tnxbeans;

/**
 *
 * @author mceccarelli
 */
public interface BasicField {
    
    public String getDbNomeCampo();
    public void setDbNomeCampo(String dbNomeCampo);

    public String getDbTipoCampo();
    public void setDbTipoCampo(String dbTipoCampo);

    public boolean isDbRiempire();
    public void setDbRiempire(boolean dbRiempire);

    public boolean isDbSalvare();
    public void setDbSalvare(boolean dbSalvare);

    public boolean isDbModificato();
    public void setDbModificato(boolean dbModificato);

}
