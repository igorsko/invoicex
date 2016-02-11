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

public class main2 {

    static public iniFileProp fileIni;
    static String paramProp = "param_prop.txt";
    
    static public String PERSONAL_GIANNI = "gianni";
    static public String PERSONAL_TNX = "tnx";
    static public String PERSONAL_CUCINAIN = "cucinain";
    static public String PERSONAL_CHIANTICASHMERE = "cc";
    static public String PERSONAL_CHIANTICASHMERE_ANIMALI = "cc1_animali";
    static public String PERSONAL_TLZ = "tlz";
    static public String PERSONAL_LUXURY = "lux";
    static public String PERSONAL_CLIENT_MANAGER_1 = "cm1";
    

    public main2(String[] args) {
        fileIni = new iniFileProp();
        fileIni.realFileName = paramProp;

        Menu menu1 = new Menu();
        menu1.show();

    }

    public static String getPersonal() {

        if (Util.getIniValue("personalizzazioni", "personalizzazioni").indexOf("gianni1") >= 0) {

            return PERSONAL_GIANNI;
        } else if (Util.getIniValue("personalizzazioni", "personalizzazioni").indexOf("tnx1") >= 0) {

            return PERSONAL_TNX;
        } else if (Util.getIniValue("personalizzazioni", "personalizzazioni").indexOf("cucinain1") >= 0) {


            return PERSONAL_CUCINAIN;
        } else if (Util.getIniValue("personalizzazioni", "personalizzazioni").indexOf("tlz1") >= 0) {

            return PERSONAL_TLZ;
        } else if (Util.getIniValue("personalizzazioni", "personalizzazioni").indexOf("lux1") >= 0) {

            return PERSONAL_LUXURY;
        } else {

            return PERSONAL_TNX;
        }
    }

    public static boolean getPersonalContain(String personal) {

        if (Util.getIniValue("personalizzazioni", "personalizzazioni").indexOf(personal) >= 0) {

            return true;
        } else {

            return false;
        }
    }
}
