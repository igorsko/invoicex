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

package it.tnx;

import java.text.*;

public class Checks {
  
  /** Creates a new instance of Checks */
  public Checks() {
  }
  
  public static boolean isInteger(String integer, boolean allowNulls) {
    if (allowNulls && integer == null) return true;
    if (allowNulls && integer.length() == 0) return true;
    try {
      int i = Integer.parseInt(integer);
      return true;
    } catch (java.lang.NumberFormatException nfe) {
      return false;
    }    
  }

  public static boolean isInteger(String integer) {
    try {
      int i = Integer.parseInt(integer);
      return true;
    } catch (java.lang.NumberFormatException nfe) {
      return false;
    }    
  }
  
  public static boolean isNumber(String number) {
    try {
      double d = Double.parseDouble(number);
      return true;
    } catch (java.lang.NumberFormatException nfe) {
      return false;
    }    
  }

  public static boolean isDate(String date) {
    DateFormat myFormat = new SimpleDateFormat("dd/MM/yy");
    myFormat.setLenient(false);
    try {
      java.util.Date myDate = myFormat.parse(date);
      return true;
    } catch (java.text.ParseException pe) {
      return false;
    }      
  }

  public static boolean isBlank(String blank) {
    if (blank == null) blank = "";
    if (blank.trim().length() == 0) {
      return true;
    } else {
      return false;
    }
  }
  
}
