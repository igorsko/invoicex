/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gestioneFatture;

import javax.swing.JTabbedPane;
import javax.swing.JTable;
import tnxbeans.tnxDbPanel;
import tnxbeans.tnxTextField;

/**
 *
 * @author mceccarelli
 */
public interface GenericFrmTest {
    public void aggiornareProvvigioni();
    public JTable getGrid();
    public tnxDbPanel getDatiPanel();
    public JTabbedPane getTab();
    public tnxTextField getTexClie();
    public boolean isAcquisto();
}
