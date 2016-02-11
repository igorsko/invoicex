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



package gestioneFatture;

import java.util.EventObject;

public class InvoicexEvent extends EventObject {
    public static final int TYPE_SALVA_NUOVA_FATTURA = 1;
    public static final int TYPE_STAMPA_FATTURA = 2;
    public static final int TYPE_FRMTESTFATT_CONSTR_POST_INIT_COMPS = 3;
    public static final int TYPE_FRMTESTFATT_DOPO_INSERIMENTO = 4;
    public static final int TYPE_FRMTESTFATT_PRIMA_DI_SAVE = 5;
    public static final int TYPE_FRMTESTFATT_RICALCOLA_TOTALI_1 = 6;
    public static final int TYPE_FRMTESTFATT_RICALCOLA_TOTALI_2 = 7;
    public static final int TYPE_FRMCLIE_CONSTR_POST_INIT_COMPS = 8;
    public static final int TYPE_FRMTESTFATT_CARICA_DATI_CLIENTE = 9;
    public static final int TYPE_SAVE = 10;     //salvataggi ogenerico - per richiamare aggioranmento indice lucene
    public static final int TYPE_FRMELENDDT_CONSTR_POST_INIT_COMPS = 11;
    public static final int TYPE_FRMTESTFATTACQUISTO_CONSTR_POST_INIT_COMPS = 12;
    public static final int TYPE_FRMTESTFATT_RICALCOLA_TOTALI_3 = 13;
    public static final int TYPE_FRMTESTFATT_RICALCOLA_TOTALI_4 = 14;
    public static final int TYPE_SITUAZIONECLIENTI_CONSTR_POST_INIT_COMPS = 15;
    public static final int TYPE_SITUAZIONECLIENTI_ACTIVATE_BTNRIBA = 16;
    public static final int TYPE_SITUAZIONECLIENTI_DEACTIVATE_BTNRIBA = 17;
    public static final int TYPE_RISTAMPA_DISTINTA_RIBA = 18;
    public static final int TYPE_FRMELENFATT_CONSTR_POST_INIT_COMPS = 19;
    public static final int TYPE_IMPOSTAZIONI_CONSTR_POST_INIT_COMPS = 20;
    public static final int TYPE_IMPOSTAZIONI_SALVA = 21;
    public static final int TYPE_FRMTESTFATT_ACQUISTO_PRIMA_DI_SAVE = 22;
    public static final int TYPE_FRMTESTFATT_ACQUISTO_DOPO_INSERIMENTO = 23;
    public static final int TYPE_FRMELENFATT_OPEN_FORM_SCONTRINO = 24;
    public static final int TYPE_MENU_OPEN = 25;
    public static final int TYPE_FRMNuovRigaDescrizioneMultiRigaNew_POST_INIT_COMPS = 26;
    public static final int TYPE_FRMNuovRigaDescrizioneMultiRigaNew_INSERIMENTO = 27;
    public static final int TYPE_PREPARA_JASPER = 28;
    public static final int TYPE_NEWRIGHPAGAMENTIRICORRENTI_INSERIMENTO = 29;
    public static final int TYPE_FRMELENORDINI_CONSTR_POST_INIT_COMPS = 30;
    public static final int TYPE_SITUAZIONECLIENTI_CONSTR_PRE_INIT_COMPS = 31;

    public static final int TYPE_FRMELENFATT_CONSTR_PRE_INIT_COMPS = 32;
    public static final int TYPE_FRMELENORDI_CONSTR_PRE_INIT_COMPS = 33;
    public static final int TYPE_FRMELENDDT_CONSTR_PRE_INIT_COMPS = 34;

    public static final int TYPE_EMAIL_STORICO = 35;

    public static final int TYPE_FRMELENORDINI_SETACQUISTO = 36;

    public static final int TYPE_FRMELENFATT_POST_DELETE = 37;
    
    public static final int TYPE_MENUFINESTRE_POST_REMOVEALL = 38;

    public static final int TYPE_FRMARTICOLI_CONSTR_POST_INIT_COMPS = 39;    
    public static final int TYPE_FRMNuovRigaDescrizioneMultiRigaNew_recuperaDatiArticoli_fine = 40;    
    
    public static final int TYPE_NUOVI_MOVIMENTI_MAGAZZINO = 41;
    
    public static final int TYPE_IMPORT_ARTICOLO = 42;
    
    public static final int TYPE_FRMTESTFATTACQUISTO_CARICA_DATI_CLIENTE = 43;
    
    public static final int TYPE_FRMTESTORDI_CONSTR_POST_INIT_COMPS = 44;
    public static final int TYPE_FRMTESTDDT_CONSTR_POST_INIT_COMPS = 45;
    public static final int TYPE_FRMTESTORDI_CARICA_DATI_CLIENTE = 46;
    public static final int TYPE_FRMTESTDDT_CARICA_DATI_CLIENTE = 47;
    
    public static final int TYPE_FRMELENFATT_ACQUISTO_POST_DELETE = 48;
    
    public static final int TYPE_EXPORTACQVEN_CONSTR_POST_INIT_COMPS = 49;
    
    public static final int TYPE_frmTipiRitenuta_CONSTR_POST_INIT_COMPS = 50;
    public static final int TYPE_frmTipiRivalsa_CONSTR_POST_INIT_COMPS = 51;
    public static final int TYPE_JPanelImpostazioniRitenute_CONSTR_POST_INIT_COMPS = 52;
    
    public static final int TYPE_PRE_RESTORE_DB = 53;
    public static final int TYPE_POST_RESTORE_DB = 54;
    
    //generici
    public static final int TYPE_GENERIC_PreInitComps = 100;
    public static final int TYPE_GENERIC_PostInitComps = 101;
    
    public int type;
    public String serie;
    public int numero;
    public int anno;
    
    public Object[] args;
    
    /** Creates a new instance of InvoicexEvent */
    public InvoicexEvent(Object source) {
        super(source);
    }

    public InvoicexEvent(Object source, int type) {
        super(source);
        this.type = type;
    }

    public String toString() {
        return "InvoicexEvent " + type + " / " + serie + " " + numero + " " + anno + " source:" + source;
    }
    
}
