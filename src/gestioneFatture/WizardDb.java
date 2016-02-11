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

import it.tnx.invoicex.gui.JDialogProgress;
import it.tnx.invoicex.gui.JFrameWizardDb;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author test1
 */
public class WizardDb {
    static public JDialogProgress progress = null;
    static public boolean ok = false;
    static public JFrameWizardDb wizard = null;
    
    void execute() {
        //controllo se è il caso di visualizzare lo wizard
        if (needWizard()) {
            start(true);
        } else {
            ok = true;
            try {
                main.INSTANCE.post_wizard();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    private boolean needWizard() {
        //se c'e' localhost e non esiste il db interno
        System.out.println("need wizard?");
        if (main.fileIni.existKey("wizard", "eseguito")) return false;
        System.out.println("non è presente la key");
        String server = main.fileIni.getValue("db", "server");
        File f = new File(main.wd + "mysql/data/invoicex_default");
        System.out.println("f:" + f + " f.exists:" + f.exists() + " server:" + server);
        if (!f.exists() && (server.toLowerCase().startsWith("localhost") || server.startsWith("127.0.0.1"))) {
            System.out.println("need wizard true");
            return true;
        } else {
            main.fileIni.setValue("wizard", "eseguito", "S");
        }
        System.out.println("need wizard false");
        return false;
    }

    public void start(boolean exit) {
        //chiedo come vuol lavorare se con db interno o esterno
        System.out.println("start(" + exit + ")");
        JFrame dummyframe = new JFrame();
        dummyframe.setIconImage(main.getLogoIcon());
        wizard = new JFrameWizardDb();
        wizard.exit = exit;
        //wizard.setSize(wizard.getWidth(), 380);
        wizard.pack();
        wizard.setLocationRelativeTo(null);

        progress = new JDialogProgress(wizard, false);

        wizard.setVisible(true);
    }

    //fileIni = new iniFileProp();
    //File dir = new File("mysql/data/invoicex_default")

}
