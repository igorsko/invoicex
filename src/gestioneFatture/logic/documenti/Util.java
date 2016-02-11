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

 * DocumentiUtil.java

 *

 * Created on 7 febbraio 2003, 12.01

 */



package gestioneFatture.logic.documenti;



import it.tnx.Db;
import java.sql.*;

import gestioneFatture.*;

/**

 *

 * @author  marco

 */

public class Util {

  public static final int TIPO_DOCUMENTO_FATTURA = 1;

  public static final int TIPO_DOCUMENTO_DDT = 2;

  

  /** Creates a new instance of DocumentiUtil */

  public Util() {

  }

  

  static public long getUltimoNumero(int tipoDocumento) {

    return 0;

  }

  

  static public String getNomeTabellaRighe(int tipoDocumento) {

    if (tipoDocumento == TIPO_DOCUMENTO_DDT) return "righ_ddt";

    if (tipoDocumento == TIPO_DOCUMENTO_FATTURA) return "righ_fatt";

    return "";

  }

  

  static public int getUltimoNumeroRiga(int tipoDocumento, String serie, int numero, int anno) {

    String nomeTabella = getNomeTabellaRighe(tipoDocumento);

    String sql= ""

    + " select"

    + "   riga"

    + " from " + nomeTabella

    + " where"

    + "   serie = " + Db.pc(serie, Types.VARCHAR)

    + "   and numero = " + Db.pc(numero, Types.INTEGER)

    + "   and anno = " + Db.pc(anno, Types.INTEGER)

    + " order by riga desc";

    ResultSet resu = Db.openResultSet(sql);

    try {

      resu.next();

      return resu.getInt(1);

    } catch (Exception err) {

      err.printStackTrace();

      return 0;

    }

    

  }



}

