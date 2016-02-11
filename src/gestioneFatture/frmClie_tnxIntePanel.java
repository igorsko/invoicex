/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gestioneFatture;

import javax.swing.JLabel;

/**
 *
 * @author mceccarelli
 */
public class frmClie_tnxIntePanel implements tnxbeans.tnxIntePanel {

    public tnxbeans.tnxTextField texBancAbi;
    public tnxbeans.tnxTextField texBancCab;
    public JLabel labBancAbi;
    public JLabel labBancCab;
    public tnxbeans.tnxDbPanel panel;

    public void riempiComboPrimaDiRefresh() {
    }

    public void riempiCampiSecondari() {

        CoordinateBancarie coords = new CoordinateBancarie();
        coords.setField_texBancAbi(this.texBancAbi);
        coords.setField_labBancAbi(this.labBancAbi);
        coords.setField_texBancCab(this.texBancCab);
        coords.setField_labBancCab(this.labBancCab);
        coords.findDescriptionLab();
    }
}
