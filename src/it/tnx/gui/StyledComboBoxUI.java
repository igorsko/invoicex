/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.tnx.gui;

import java.awt.Rectangle;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

/**
 *
 * @author mceccarelli
 */
public class StyledComboBoxUI extends BasicComboBoxUI {

    protected ComboPopup createPopup() {
        BasicComboPopup popup = new BasicComboPopup(comboBox) {

            @Override
            protected Rectangle computePopupBounds(int px, int py, int pw, int ph) {
                return super.computePopupBounds(
                        px, py, Math.max(comboBox.getPreferredSize().width, pw), ph);
            }
        };
        popup.getAccessibleContext().setAccessibleParent(comboBox);
        return popup;
    }
}
