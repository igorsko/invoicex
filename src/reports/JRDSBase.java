/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package reports;

import gestioneFatture.main;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.design.JRDesignField;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author mceccarelli3
 */
public class JRDSBase {
    public static Map filescaricati = new HashMap();

    public Object getFieldValue(net.sf.jasperreports.engine.JRField jRField) throws net.sf.jasperreports.engine.JRException {
        return null;
    }

    public String sostituisci(String s) {
        // ($P{stampaPivaSotto}.booleanValue() ? "" : $F{piva_cfiscale_desc_2_sotto}) + (($F{piva_cfiscale_desc_2_sotto}.length() > 0 && $P{stampaPivaSotto}.booleanValue()) ? "<br>" : "") + "</font></html>
        boolean ritorna_vuoto = false;
        int conta = 0;
        while (s.indexOf("$F{") >= 0) {
            conta++;
            ritorna_vuoto = false;
            if (conta > 1000) break;
            String nomecampo = StringUtils.substringBefore(StringUtils.substringAfter(s, "$F{"), "}");
            String etichetta = null;
            if (nomecampo.indexOf("|") >= 0) {
                etichetta = StringUtils.split(nomecampo, "|")[0];
                nomecampo = StringUtils.split(nomecampo, "|")[1];
            }
            if (nomecampo.equals("email")) {
                System.out.println("debug");
            }
            JRDesignField f = new JRDesignField();
            f.setName(nomecampo);
            if (nomecampo.equalsIgnoreCase("piva_cfiscale_desc_2_sotto")) {
                if (main.fileIni.getValueBoolean("pref", "stampaPivaSotto", false)) {
                    ritorna_vuoto = true;
                }
            }
            if (!ritorna_vuoto) {
                String valore = "";
                try {
                    valore = (String) getFieldValue(f);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                if (valore == null) {
                    valore = "";
                }
                if (etichetta != null && etichetta.length() > 0) {
                    nomecampo = etichetta + "|" + nomecampo;
                }
                if (etichetta != null && etichetta.length() > 0 && valore != null && valore.length() > 0) {
                    valore = etichetta + " " + valore;
                }
                if (nomecampo.equalsIgnoreCase("piva_cfiscale_desc1") && valore != null && valore.length() > 0) {
                    valore = valore + "<br>";
                }
                s = StringUtils.replace(s, "$F{" + nomecampo + "}", valore);
            } else {
                s = StringUtils.replace(s, "$F{" + nomecampo + "}", "");
            }
        }
        return s;
    }
}
