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

 * DettaglioIva.java

 *

 * Created on October 7, 2003, 12:22 PM

 */
package gestioneFatture.logic.documenti;


/**

 *

 * @author  marco

 */
public class DettaglioIva {

    /** Holds value of property codice. */
    private String codice;

    /** Holds value of property percentuale. */
    private double percentuale;

    /** Holds value of property descrizione. */
    private String descrizione;

    /** Holds value of property descrizioneBreve. */
    private String descrizioneBreve;

    /** Holds value of property imponibile. */
    private double imponibile;
    public double imponibile_noarr;

    /** Holds value of property imposta. */
    private double imposta;
    public double imposta_noarr;

    private double ivato;
    public double ivato_noarr;

    /** Creates a new instance of DettaglioIva */
    public DettaglioIva() {
    }

    public DettaglioIva(String codice, double percentuale, String descrizione, String descrizioneBreve, double imponibile, double imposta) {
        this.codice = codice;
        this.percentuale = percentuale;
        this.descrizione = descrizione;
        this.descrizioneBreve = descrizioneBreve;
        this.imponibile = imponibile;
        this.imposta = imposta;
    }

    public DettaglioIva(String codice, double percentuale, String descrizione, String descrizioneBreve, double imponibile, double imposta, double ivato) {
        this.codice = codice;
        this.percentuale = percentuale;
        this.descrizione = descrizione;
        this.descrizioneBreve = descrizioneBreve;
        this.imponibile = imponibile;
        this.imposta = imposta;
        this.ivato = ivato;
    }

    /** Getter for property codice.

     * @return Value of property codice.

     *

     */
    public String getCodice() {

        return this.codice;
    }

    public int getCodiceInt() {

        return Integer.parseInt(codice);
    }

    /** Setter for property codice.

     * @param codice New value of property codice.

     *

     */
    public void setCodice(String codice) {
        this.codice = codice;
    }

    /** Getter for property percentuale.

     * @return Value of property percentuale.

     *

     */
    public double getPercentuale() {

        return this.percentuale;
    }

    /** Setter for property percentuale.

     * @param percentuale New value of property percentuale.

     *

     */
    public void setPercentuale(double percentuale) {
        this.percentuale = percentuale;
    }

    /** Getter for property descrizione.

     * @return Value of property descrizione.

     *

     */
    public String getDescrizione() {

        return this.descrizione;
    }

    /** Setter for property descrizione.

     * @param descrizione New value of property descrizione.

     *

     */
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    /** Getter for property descrizioneBreve.

     * @return Value of property descrizioneBreve.

     *

     */
    public String getDescrizioneBreve() {

        return this.descrizioneBreve;
    }

    /** Setter for property descrizioneBreve.

     * @param descrizioneBreve New value of property descrizioneBreve.

     *

     */
    public void setDescrizioneBreve(String descrizioneBreve) {
        this.descrizioneBreve = descrizioneBreve;
    }

    /** Getter for property imponibile.

     * @return Value of property imponibile.

     *

     */
    public double getImponibile() {

        return this.imponibile;
    }

    /** Setter for property imponibile.

     * @param imponibile New value of property imponibile.

     *

     */
    public void setImponibile(double imponibile) {
        this.imponibile = imponibile;
    }

    /** Getter for property imposta.

     * @return Value of property imposta.

     *

     */
    public double getImposta() {

        return this.imposta;
    }

    /** Setter for property imposta.

     * @param imposta New value of property imposta.

     *

     */
    public void setImposta(double imposta) {
        this.imposta = imposta;
    }

    @Override
    public String toString() {
        return codice + " %:" + percentuale + " imponib:" + imponibile;
    }

    public double getIvato() {
        return ivato;
    }

    public void setIvato(double ivato) {
        this.ivato = ivato;
    }


}