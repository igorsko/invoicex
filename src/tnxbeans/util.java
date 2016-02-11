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

package tnxbeans;

import java.util.*;

public abstract class util {

  public util() {
  }

  public static String getDataFromItaToMysql(String data) {
    String giorno="";
    String mese="";
    String anno="";
    Vector temp = splitString(data,"/");
    if (temp.size()<3) {
      return ("");
    } else {
      giorno = String.valueOf(temp.get(0));
      mese = String.valueOf(temp.get(1));
      anno = String.valueOf(temp.get(2));
      return (anno + "-" + mese + "-" + giorno);
    }
  }

  public static Vector splitString(String stringa , String conCosa) {
    Vector temp = new Vector();
    StringTokenizer s = new StringTokenizer(stringa,conCosa);
    while (s.hasMoreTokens())
      temp.add(s.nextToken());
    return (temp);
  }
}