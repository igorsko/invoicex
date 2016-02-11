/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package reports;

import it.tnx.Db;
import gestioneFatture.main;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

/**
 *
 * @author mceccarelli
 */
public class JRDSOrdine_lux extends JRDSOrdine {

    public JRDSOrdine_lux(Connection conn, String serie, int numero, int anno, boolean perEmail) {
        super(conn, serie, numero, anno, perEmail, false, null);
    }

    @Override
    public Object getFieldValue(JRField jRField) throws JRException {
        if (jRField.getName().equalsIgnoreCase("file_sfondo_input")) {
            return getImg(false, true);
        } else {
            return super.getFieldValue(jRField);
        }
    }

    private Object getImg(boolean isLogo, boolean isInputStream) {
        return JRDSInvoice.getImg(isLogo, isInputStream, serie, numero, anno, perEmail, true, acquisto ? "test_ordi_acquisto" : "test_ordi");
    }
}
