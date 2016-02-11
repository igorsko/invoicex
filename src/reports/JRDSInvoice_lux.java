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
package reports;

import it.tnx.Db;
import gestioneFatture.dbFattura;
import gestioneFatture.main;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class JRDSInvoice_lux extends JRDSInvoice {

    public JRDSInvoice_lux(Connection conn, String serie, int numero, int anno, boolean perEmail) {
        super(conn, serie, numero, anno, perEmail, null);
        valuta = main.luxStampaValuta;
    }

    public JRDSInvoice_lux(Connection conn, String serie, int numero, int anno) {
        super(conn, serie, numero, anno, null);
        valuta = main.luxStampaValuta;
    }

    @Override
    public Object getFieldValue(JRField jRField) throws JRException {
        if (jRField.getName().equalsIgnoreCase("file_sfondo_input")) {
            return getImg(false, true);
        } else if (jRField.getName().equalsIgnoreCase("tipo_fattura")) {
            if (main.luxProforma) {
                return dbFattura.TIPO_FATTURA_PROFORMA;
            }
            return super.getFieldValue(jRField);
        } else if (jRField.getName().equalsIgnoreCase("file_logo_input")) {
            if (main.luxStampaNera) {
                return null;
            }
            return super.getFieldValue(jRField);
        } else if (jRField.getName().equalsIgnoreCase("numero_fattura")) {
            String num = "";
            try {
                SimpleDateFormat f1 = new SimpleDateFormat("ddMMyy");
                num += f1.format(rDocu.getDate("data")) + "-";
                if (it.tnx.Db.nz(rDocu.getString("serie"), "").length() > 0) {
                    num += rDocu.getString("serie") + "/";
                }
                num += rDocu.getString("numero");
            } catch (Exception e) {
                e.printStackTrace();
            }

            return num;
        } else {
            return super.getFieldValue(jRField);
        }
    }

    private Object getImg(boolean isLogo, boolean isInputStream) {
        return JRDSInvoice.getImg(isLogo, isInputStream, serie, numero, anno, perEmail, true, "test_fatt");
    }

}
