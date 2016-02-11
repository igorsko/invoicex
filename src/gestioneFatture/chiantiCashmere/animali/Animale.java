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

 * Animale.java

 *

 * Created on 20 maggio 2003, 22.54

 */



package gestioneFatture.chiantiCashmere.animali;



import it.tnx.Db;
import java.sql.*;



import gestioneFatture.*;

/**

 *

 * @author  marco ceccarelli

 */

public class Animale {

  //dati di collegamento al documento

  public int tipoDocumento;

  public String serie;

  public int numero;

  public int anno;

  public int riga;

  //dati animale

  public String numeroAllevamento;

  public String numeroMicrochip;

  public String sesso;

  public String noteUsl;

  public String numeroBollaMattatoio;

  public String pesoCarcassa;

  public double prezzoKg;

  public double prezzoRiga = 0;

  //

  public boolean caricato = false;

  public boolean notFound = false;

  

  /** Creates a new instance of Animale */

  public Animale() {

  }

  

  public void loadAnimale(int tipoDocumento, String serie, int numero, int anno, int riga) {

    String sql = ""

    + " select"

    + "   *"

    + " from righ_animali"

    + " where"

    + "   tipo_documento = " + Db.pc(tipoDocumento, Types.INTEGER)

    + "   and serie = " + Db.pc(serie, Types.VARCHAR)

    + "   and numero = " + Db.pc(numero, Types.INTEGER)

    + "   and anno = " + Db.pc(anno, Types.INTEGER)

    + "   and riga = " + Db.pc(riga, Types.INTEGER);

    ResultSet resu = Db.openResultSet(sql);

    try {

      this.tipoDocumento = tipoDocumento;

      this.serie = serie;

      this.numero = numero;

      this.anno = anno;

      this.riga = riga;

      if (resu.next()) {

        this.notFound = false;

        this.numeroAllevamento = resu.getString("numero_allevamento");

        this.numeroMicrochip = resu.getString("numero_microchip");

        this.sesso = resu.getString("sesso");

        this.noteUsl = resu.getString("note_allegato_usl");

        this.numeroBollaMattatoio = resu.getString("numero_data_bolla_mattatoio");

        this.pesoCarcassa = resu.getString("peso_carcassa");

        this.prezzoKg = resu.getDouble("prezzo_kg");        

        this.caricato = true;

      } else {

        this.notFound = true;

      }

    } catch (Exception err) {

      this.caricato = false;

      this.notFound = true;

      err.printStackTrace();

    }

  }

  

  public boolean saveAnimale() {

    String sql = "";

    java.util.Vector sqlC = new java.util.Vector();

    java.util.Vector sqlV = new java.util.Vector();

    

    if (this.notFound == true) {

      this.riga = gestioneFatture.logic.documenti.Util.getUltimoNumeroRiga(this.tipoDocumento, this.serie, this.numero, this.anno) + 1;

    }

    sqlC.add("tipo_documento"); sqlV.add(Db.pc(this.tipoDocumento, Types.INTEGER));

    sqlC.add("serie"); sqlV.add(Db.pc(this.serie, Types.VARCHAR));

    sqlC.add("numero"); sqlV.add(Db.pc(this.numero, Types.INTEGER));

    sqlC.add("anno"); sqlV.add(Db.pc(this.anno, Types.INTEGER));

    sqlC.add("riga"); sqlV.add(Db.pc(this.riga, Types.INTEGER));

    sqlC.add("numero_allevamento"); sqlV.add(Db.pc(this.numeroAllevamento, Types.VARCHAR));

    sqlC.add("numero_microchip"); sqlV.add(Db.pc(this.numeroMicrochip, Types.VARCHAR));

    sqlC.add("sesso"); sqlV.add(Db.pc(this.sesso, Types.VARCHAR));

    sqlC.add("note_allegato_usl"); sqlV.add(Db.pc(this.noteUsl, Types.VARCHAR));

    sqlC.add("numero_data_bolla_mattatoio"); sqlV.add(Db.pc(this.numeroBollaMattatoio, Types.VARCHAR));

    sqlC.add("peso_carcassa"); sqlV.add(Db.pc(this.pesoCarcassa, Types.VARCHAR));

    sqlC.add("prezzo_kg"); sqlV.add(Db.pc(this.prezzoKg, Types.DOUBLE));

    if (this.notFound == true) {

      sql = Db.preparaSqlInsert(sqlC, sqlV, "righ_animali");

      Db.executeSql(sql);

    }

    

    //inserisco in tabella righe        

    String desc = "";

    String sesso = "MASCHIO";

    if (this.tipoDocumento == gestioneFatture.logic.documenti.Util.TIPO_DOCUMENTO_DDT) {

      if (this.sesso.equalsIgnoreCase("F")) sesso = "FEMMINA";

      desc = ""

      + "Animale da riproduzione"

      + "\n   Numero allevamento: " + this.numeroAllevamento

      + "\n   Sesso: " + sesso;

      if (this.noteUsl.length() > 0) desc += "\n   Note allegato USL: " + this.noteUsl;

      if (this.numeroMicrochip.length() > 0) desc += "\n   Numero Microchip: " + this.numeroMicrochip;

    } else if (this.tipoDocumento == gestioneFatture.logic.documenti.Util.TIPO_DOCUMENTO_FATTURA) {

      desc = ""

      + "Carcassa di Animale"

      + "\n   Numero e Data ddt al Mattatoio: " + this.numeroBollaMattatoio

      + "\n   Peso carcassa: " + this.pesoCarcassa

      + "\n   Prezzo al Kg: " + this.prezzoKg;

    }

    

    sql = "";

    sqlC = new java.util.Vector();

    sqlV = new java.util.Vector();

    sqlC.add("serie"); sqlV.add(Db.pc(this.serie, Types.VARCHAR));

    sqlC.add("numero"); sqlV.add(Db.pc(this.numero, Types.INTEGER));

    sqlC.add("anno"); sqlV.add(Db.pc(this.anno, Types.INTEGER));

    sqlC.add("riga"); sqlV.add(Db.pc(this.riga, Types.INTEGER));

    sqlC.add("descrizione"); sqlV.add(Db.pc(desc, Types.VARCHAR));

    if (this.tipoDocumento == gestioneFatture.logic.documenti.Util.TIPO_DOCUMENTO_DDT) {

      sqlC.add("codice_articolo"); sqlV.add(Db.pc("AR", Types.VARCHAR));    

    } else {

      sqlC.add("codice_articolo"); sqlV.add(Db.pc("C", Types.VARCHAR));    

    }

    sqlC.add("quantita"); sqlV.add(Db.pc("1", Types.INTEGER));

    sqlC.add("stato"); sqlV.add(Db.pc("P", Types.VARCHAR));

    sqlC.add("iva"); sqlV.add(Db.pc("10", Types.DOUBLE));

    sqlC.add("um"); sqlV.add(Db.pc("PZ", Types.VARCHAR));

    sqlC.add("prezzo"); sqlV.add(Db.pc(this.prezzoRiga, Types.DOUBLE));

    if (this.notFound == true) {

      sql = Db.preparaSqlInsert(sqlC, sqlV, gestioneFatture.logic.documenti.Util.getNomeTabellaRighe(this.tipoDocumento));

      Db.executeSql(sql);

    }

    

    return true;

  }  

}

