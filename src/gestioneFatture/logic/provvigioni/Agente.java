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

 * Cliente.java

 *

 * Created on 30 gennaio 2003, 19.01

 */



package gestioneFatture.logic.provvigioni;



import it.tnx.Db;
import java.sql.*;

import gestioneFatture.*;

/**

 *

 * @author  marco

 */

public class Agente {

  private ResultSet resu;

  private long codice;

  private boolean trovato = false;

  

  static public final String TIPO_IVA_ITALIA = "IT";

  static public final int TIPO_IVA_ITALIA_ALIQUOTA = 20;

  static public final String TIPO_IVA_ITALIA_DICITURA = "";

  

  static public final String TIPO_IVA_CEE = "CEE";

  static public final int TIPO_IVA_CEE_ALIQUOTA = 0;

  static public final String TIPO_IVA_CEE_DICITURA = "Non imponibile art. 41 del DL. 331/93";

  static public final String TIPO_IVA_CEE_DICITURA_ABBR = "N.i. art. 41";

  

  static public final String TIPO_IVA_ALTRO = "ALTRO";

  static public final int TIPO_IVA_ALTRO_ALIQUOTA = 0;

  static public final String TIPO_IVA_ALTRO_DICITURA = "Fattura Esente IVA.";

  static public final String TIPO_IVA_ALTRO_DICITURA_ABBR = "Es. Iva art. 8";

  

  static public final String TIPO_IVA_PAESI_CEE = "AT;BE;DK;FI;FR;DE;GB;GR;IE;LU;NL;PT;ES;SE;";

 

  public Agente(long codice) {

    String sql = "select * from clie_forn";

    sql += " where codice = " + codice;

    ResultSet resu = Db.openResultSet(sql);

    this.codice = codice;

    this.resu = resu;

    try {

      if (resu.next() == true) {

        trovato = true;

      }

    } catch (Exception err) {err.printStackTrace();}

  }

  

  public String get(String nomeCampo) {

    if (trovato == true) {

      try {

        if (resu.getString(nomeCampo) != null) {

          return resu.getString(nomeCampo);

        } else {

          return "IT";

        }

      } catch (Exception err) {

        err.printStackTrace();

        return "!ERR!";

      }

    } else {

      return "!ERR!";

    }

  }

  

  public boolean isItalian() {

    if (trovato == true) {

      try {

        if ((resu.getString("paese") == null) || resu.getString("paese").equalsIgnoreCase("IT") || resu.getString("paese").length() == 0) {

          return true;

        }

      } catch (Exception err) {

        return true;

      }

    }

    return false;

  }

  

  public String getField(String nomeCampo) {

    if (trovato == true) {

      try {

        if (resu.getString(nomeCampo) != null) {

          return resu.getString(nomeCampo);

        } else {

          return "";

        }

      } catch (Exception err) {

        err.printStackTrace();

        return "";

      }

    } else {

      return "";

    }

  }



  public String getTipoIva() {

    if (trovato == true) {

      String paese = get("paese");

      if (paese.equalsIgnoreCase("IT") || paese.trim().length() == 0) {

        return this.TIPO_IVA_ITALIA;

      } else if (this.TIPO_IVA_PAESI_CEE.indexOf(paese) >= 0) {

        //107 controllo se ha partita iva

        //return this.TIPO_IVA_CEE;

        String piva = get("piva_cfiscale");

        if (piva.length() > 0) {

          return this.TIPO_IVA_CEE;

        } else {

          return this.TIPO_IVA_ITALIA;

        }

      } else {

        return this.TIPO_IVA_ALTRO;

      }        

    } else {

      return this.TIPO_IVA_ITALIA;

    }

  }

  

  public int getAliquotaIva() {

    if (trovato == true) {

      String tipoIva = getTipoIva();

      if (tipoIva.equals(this.TIPO_IVA_ITALIA)) return this.TIPO_IVA_ITALIA_ALIQUOTA;

      if (tipoIva.equals(this.TIPO_IVA_CEE)) return this.TIPO_IVA_CEE_ALIQUOTA;

      if (tipoIva.equals(this.TIPO_IVA_ALTRO)) return this.TIPO_IVA_ALTRO_ALIQUOTA;

      return this.TIPO_IVA_ITALIA_ALIQUOTA;

    } else {

      return this.TIPO_IVA_ITALIA_ALIQUOTA;

    }

  }

  

  public String getDicituraIva() {

    if (trovato == true) {

      String tipoIva = getTipoIva();

      if (tipoIva.equals(this.TIPO_IVA_ITALIA)) return this.TIPO_IVA_ITALIA_DICITURA;

      if (tipoIva.equals(this.TIPO_IVA_CEE)) return this.TIPO_IVA_CEE_DICITURA;

      if (tipoIva.equals(this.TIPO_IVA_ALTRO)) return this.TIPO_IVA_ALTRO_DICITURA;

      return this.TIPO_IVA_ITALIA_DICITURA;

    } else {

      return this.TIPO_IVA_ITALIA_DICITURA;

    }

  }

  

  public String getDicituraAbbrIva() {

    if (trovato == true) {

      String tipoIva = getTipoIva();

      if (tipoIva.equals(this.TIPO_IVA_ITALIA)) return this.TIPO_IVA_ITALIA_DICITURA;

      if (tipoIva.equals(this.TIPO_IVA_CEE)) return this.TIPO_IVA_CEE_DICITURA_ABBR;

      if (tipoIva.equals(this.TIPO_IVA_ALTRO)) return this.TIPO_IVA_ALTRO_DICITURA_ABBR;

      return this.TIPO_IVA_ITALIA_DICITURA;

    } else {

      return this.TIPO_IVA_ITALIA_DICITURA;

    }

  }  

}

