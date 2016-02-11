/**
 * Invoicex
 * Copyright (c) 2005,2006,2007,2008,2009 Marco Ceccarelli, Tnx snc
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

package it.tnx.invoicex.gui;

import ca.odell.renderpack.ClassConciousEditorRenderer;
import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.client.HessianRuntimeException;
import gestioneFatture.main;
import it.tnx.SwingWorker;
import it.tnx.commons.SwingUtils;
import it.tnx.commons.cu;
import it.tnx.invoicex.Hservices;
import it.tnx.invoicex.Plugin;
import it.tnx.invoicex.Plugin2;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import mjpf.EntryDescriptor;
import mjpf.PluginEntry;
import mjpf.PluginFactory;
import org.apache.commons.lang.StringUtils;
import sun.swing.DefaultLookup;


/**
 *
 * @author  mceccarelli
 */
public class JDialogPlugins extends javax.swing.JDialog {

    public AbstractAction actionAttivaPlugin = new AbstractAction() {

        public void actionPerformed(ActionEvent e) {
            System.out.println("attivazione plugin");
            System.out.println(tabPlugins.getSelectedRow());
            TableModelPlugins model = (TableModelPlugins) tabPlugins.getModel();
            Plugin p = (Plugin) model.plugins.get(tabPlugins.getSelectedRow());
            System.out.println("model.plugins:" + model.plugins.get(tabPlugins.getSelectedRow()));
            //se è presente e attivo, chiedo se lo vuole disinstallare

//            p.setAttivo(false);
            System.out.println(p.getNome_breve() + " " + getNomeDownload(p.getNome_breve()));

            if ((p.isPresente() && p.isAttivo()) || (p.isPresente() && !p.isAttivo())) {
                //chiedo se vuol disinstallare
                if (JOptionPane.showConfirmDialog(JDialogPlugins.this, "Il plugin '" + p.getNome_breve() + "' è già installato, lo vuoi rimuovere ?", "Attenzione", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    PluginEntry entry = main.getPluginEntry(p.getNome_breve());
                    try {
                        entry.stopPluginEntry();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    File fplugin = new File(main.plugins_path + "Invoicex" + p.getNome_breve() + ".jar");
                    System.out.println("plugin " + fplugin.getAbsolutePath() + " exist:" + fplugin.exists());
                    boolean ret = fplugin.delete();
                    System.out.println("rimozione plugin " + fplugin.getAbsolutePath() + " ret:" + ret);
                    
                    fplugin.deleteOnExit();
                    System.out.println("rimozione plugin in uscita " + fplugin.getAbsolutePath());
                    aggiornaDati();

                    try {
                        main.fileIni.setValue("plugins", "remove_" + fplugin.getName().toLowerCase(), fplugin.getAbsolutePath());    
                    } catch (Exception e2) {
                    }

                    SwingUtils.showInfoMessage(JDialogPlugins.this, "Il plugin verrà tolto al prossimo avvio");
                }
//            } else if (p.isPresente() && !p.isAttivo()) {
//                JDialogPluginsSito sito = new JDialogPluginsSito(main.getPadre(), true);
//                sito.setLocationRelativeTo(null);
//                sito.setVisible(true);
            } else if (!p.isPresente()) {
                //chiedo se vuol installare
                if (p.getNome_breve().equalsIgnoreCase("pluginClientManager")) {
                    SwingUtils.showInfoMessage(main.getPadre(), "Il plugin in oggetto è utilizzabile solo acquistando il programma Client Manager, visita www.tnx.it per i dettagli");
                } else {
                    boolean ok = false;
                    if (e != null && e.getID() == 1) {
                        ok = true;
                    } else {
                        if (SwingUtils.showYesNoMessage(main.getPadre(), "Vuoi scaricare " + p.getNome_breve() + " ?")) {
                            ok = true;
                        }
                    }
                    if (ok) {
                        System.out.println("scaricare " + p.getNome_breve());
                        try {
                            d3(1);
                            SwingUtils.showFlashMessage2("Scaricamento '" + p.getNome_breve() + "' in corso !", 3, null, Color.red, new Font(null, Font.BOLD, 16), true);
                            boolean ret = scarica(getNomeDownload(p.getNome_breve()));
                            if (ret) {
                                p.setPresente(true);
                                model.fireTableDataChanged();
                                if (e != null && e.getID() == 1) {
                                    startPlugin(p);
                                } else {
                                    SwingUtils.showInfoMessage(main.getPadre(), "Plugin scaricato, per attivarlo devi riavviare il programma");
                                }                                
                            } else {
                                SwingUtils.showErrorMessage(main.getPadre(), "Errore durante il download");
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            SwingUtils.showErrorMessage(main.getPadre(), ex.toString());
                        }
                    }
                }
            }

        }

    };

    private void startPlugin(Plugin p) {
        PluginFactory pf = new PluginFactory();
        pf.loadPlugins(main.plugins_path);
        Collection plugcol = pf.getAllEntryDescriptor();
        if (plugcol != null) {
            Iterator plugiter = plugcol.iterator();
            while (plugiter.hasNext()) {
                EntryDescriptor pd = (EntryDescriptor) plugiter.next();
                //avvio il plugin
                try {
                    PluginEntry pl = (PluginEntry) pf.getPluginEntry(pd.getId());
                    if (pd.getName().equals(p.getNome_breve())) {
                        main.getPadrePanel().lblInfoLoading2.setText("Caricamento plugin " + pd.getName() + "...");
                        pl.initPluginEntry(null);
                        pl.startPluginEntry();
                        main.getPadrePanel().lblInfoLoading2.setText(pd.getName() + " Caricato");
                        dispose();
                        SwingUtils.showFlashMessage2("Plugin '" + p.getNome_lungo() + "' installato !", 3, null, Color.red, new Font(null, Font.BOLD, 16), true);
                    }
                } catch (NoSuchFieldError nofield) {
                    nofield.printStackTrace();
                    SwingUtils.showErrorMessage(main.getPadreWindow(), "<html>Errore durante l'avvio del <b>" + pd.getName() + "</b><br>Manca il campo <b>" + nofield.getMessage() + "</b><br>Probabilmente devi aggiornare Invoicex Base all'ultima release per utilizzare questo plugin</html>");
                } catch (NoClassDefFoundError noclass) {
                    noclass.printStackTrace();
                    SwingUtils.showErrorMessage(main.getPadreWindow(), "<html>Errore durante l'avvio del <b>" + pd.getName() + "</b><br>Manca la classe <b>" + noclass.getMessage() + "</b><br>Probabilmente devi aggiornare Invoicex Base all'ultima release per utilizzare questo plugin</html>");
                } catch (Throwable tr) {
                    tr.printStackTrace();
                    SwingUtils.showErrorMessage(main.getPadreWindow(), "<html>Errore durante l'avvio del <b>" + pd.getName() + "</b><br>" + tr.toString() + "</b><br>Probabilmente devi aggiornare Invoicex Base all'ultima release per utilizzare questo plugin</html>");
                }
            }
        }

    }

    public static String getNomeDownload(String nomep) {
        return "plugins/Invoicex" + StringUtils.capitalize(nomep) + ".jar";
    }

    public static boolean scarica(String p) throws Exception {
//        URL urlagg = new URL("http://www.tnx.dyndns.org/download/invoicex/" + p);
        URL urlagg = new URL("http://www.tnx.it/pagine/invoicex_server/download/invoicex/" + p);
        System.err.println("urlagg = " + urlagg);

        HttpURLConnection conn = (HttpURLConnection) urlagg.openConnection();
        int retcode = conn.getResponseCode();
        long lastm = conn.getLastModified();
        File file_locale = new File(p);
        if (file_locale.exists()) {
            System.out.println("il file " + file_locale + " esiste già");
            return false;
        }
        int size = conn.getContentLength();

        if (retcode != 200) {
            System.out.println("scaricap: errore retcode:" + retcode + " resp:" + conn.getResponseMessage());
            return false;
        }

        System.out.println("scarico " + p);

        InputStream is = new BufferedInputStream(urlagg.openStream());
        FileOutputStream outs = new FileOutputStream(file_locale);
        int readed = 0;
        int read = 0;
        byte[] buff = new byte[10000];
        String title = main.getPadre().getTitle();
        while ((read = is.read(buff)) > 0) {
            outs.write(buff, 0, read);
            readed += read;
            try {
                main.getPadre().setTitle(title + " - scaricamento in corso " + (readed / 1024) + "Kb");
            } catch (Exception ex) {
            }
        }
        main.getPadre().setTitle(title);
        is.close();
        outs.close();

        return true;
    }

    private void d3(int i) {
        URL url;
        try {
            Integer id = main.attivazione.getIdRegistrazione();
            System.err.println("id = " + id);

            url = new URL("http://www.tnx.it/pagine/invoicex_server/connect3.php?r=" + i + "&i=" + id);
            URLConnection conn = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null) {
                System.err.println("in:" + line);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void scaricatutti() {
        System.out.println("scaricatutti");
        d3(1);
        SwingWorker w = new SwingWorker() {

            @Override
            public Object construct() {
                TableModelPlugins model = (TableModelPlugins) tabPlugins.getModel();
                for (int i = 0; i < tabPlugins.getRowCount(); i++) {
                    Plugin p = (Plugin) model.plugins.get(i);
                    System.out.println(p.getNome_breve() + " " + getNomeDownload(p.getNome_breve()));
                    try {
                        boolean ret = scarica(getNomeDownload(p.getNome_breve()));
                        if (ret) {
                            p.setPresente(true);
                            model.fireTableDataChanged();
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(JDialogPlugins.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                return null;
            }

            @Override
            public void finished() {
                SwingUtils.showInfoMessage(main.getPadre(), "I plugins sono stati scaricati per attivarli devi riavviare il programma");
            }
        };
        w.start();
    }

    private static class PluginTable extends JTable {
        String tooltipText;
        boolean sameRow;
        int rigaAttuale = 0;
        
       @Override
        public String getToolTipText(MouseEvent event) {
            if (sameRow) {
                return tooltipText;
            } else {
                String tip = null;
                Point p = event.getPoint();
                int hitRowIndex = rowAtPoint(p);
                //tip = cu.toString(hitRowIndex);
                try {
                    String plugin = cu.toString(getValueAt(hitRowIndex, 0));
                    Map m = main.pluginErroriAttivazioni.get(plugin);
                    System.out.println("map del plugin " + plugin + ":" + m);
                    String scadenza = ((TableModelPlugins)getModel()).getScadenza(hitRowIndex);
                    if (scadenza != null && scadenza.length() > 0) {
                        if (main.pluginAttivi.contains(plugin)) {
                            tip = plugin + " scade il " + scadenza;
                        } else {
                            tip = plugin + " scaduto dal " + scadenza;
                            if (m != null && m.get("motivo_msg") != null) {
                                tip += ", motivo: " + cu.toString(m.get("motivo_msg"));
                            }
                        }
                        
                    }
                } catch (Exception e) {
                }
                
                tooltipText = tip;
                return tip;
                
            }
        }
       
        // makes the tooltip's location to match table cell location
        // also avoids showing empty tooltips
        public Point getToolTipLocation(MouseEvent event) {
            int row = rowAtPoint(event.getPoint());
            if (row == -1) {
                return null;
            }
            int col = columnAtPoint(event.getPoint());
            if (col == -1) {
                return null;
            }

            sameRow = (row == rigaAttuale);
            boolean hasTooltip = getToolTipText() == null ? getToolTipText(event) != null : true;
            rigaAttuale = row;
//            return hasTooltip ? getCellRect(row, 1, false).getLocation(): null;
            Point p = event.getPoint();
            p.translate(10, 10);
            return hasTooltip ? p : null;
        }       
    }

    class TableModelPlugins extends AbstractTableModel {

        public List<Plugin> plugins;

        TableModelPlugins(List<Plugin> plugins) {
            this.plugins = plugins;
        }

        public String getScadenza(int rowIndex) {
            String plugin = cu.toString(getValueAt(rowIndex, 0));
            String scadenza = main.fileIni.getValue("plugin_invoicex", plugin + "_scadenza", null);
            return scadenza;
        }
        
        public Object getValueAt(int rowIndex, int columnIndex) {
            Plugin p = plugins.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return p.getNome_breve();
                case 1:
                    return p.getNome_lungo();
                case 2:
                    try {
                        Plugin2 p2 = (Plugin2)p;
                        return p2.props.get("pack");
                    } catch (Exception e) {
                        return null;
                    }
                case 3:
                    return p.getVersioneDisp();
                case 4:
                    return p.getVersioneInst();
                case 5:
                    return p.isPresente();
                case 6:
                    return p.isAttivo();

            }
            return null;
        }

        public int getRowCount() {
            return plugins.size();
        }

        public int getColumnCount() {
            return 7;
        }

        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return "Plugin";
                case 1:
                    return "Descrizione";
                case 2:
                    return "Pack";
                case 3:
                    return "Versione Disp";
                case 4:
                    return "Versione Inst";
                case 5:
                    return "Installato";
                case 6:
                    return "Attivato";
            }
            return null;
        }

        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return String.class;
                case 1:
                    return String.class;
                case 2:
                    return String.class;
                case 3:
                    return String.class;
                case 4:
                    return String.class;
                case 5:
                    return Boolean.class;
                case 6:
                    return Boolean.class;
            }
            return Object.class;
        }
    }

    /** Creates new form JDialogPlugins */
    public JDialogPlugins(java.awt.Frame parent, boolean modal) {
        this(parent, modal, false);
    }
    public JDialogPlugins(java.awt.Frame parent, boolean modal, final boolean f_scaricatutti) {

        super(parent, modal);
        initComponents();
        
        loading.setVisible(false);
        
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        ClassConciousEditorRenderer.install(tabPlugins);

        aggiornaDati();

        if (!main.fine_init_plugin) {
            loading.setVisible(true);
            Thread t = new Thread("aggiorna plugins") {

                @Override
                public void run() {
                    while (main.fine_init_plugin == false) {
                        synchronized(aggiorna) {
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(JDialogPlugins.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            aggiornaDati();
                            try {
                                aggiorna.wait(5000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(JDialogPlugins.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                    if (f_scaricatutti) {
                         scaricatutti();
                    }
                    loading.setVisible(false);
                }

            };
            t.start();
        }

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tabPlugins = new PluginTable();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jXHyperlink1 = new org.jdesktop.swingx.JXHyperlink();
        loading = new org.jdesktop.swingx.JXBusyLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Plugins");

        tabPlugins.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tabPlugins.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabPluginsMouseClicked(evt);
            }
        });
        tabPlugins.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tabPluginsKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(tabPlugins);

        jButton1.setText("Scarica tutti i Plugins");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setText("<html>I Plugins contengono funzionalità aggiuntive di Invoicex, possono essere provati per 30gg dopodichè devi acquistare<br> la versione di Invoicex contenente i plugin scaricati altrimenti verranno disattivati.<br>Se non sono installati puoi cliccare qui accanto o fare doppio click sul plugin di interesse</html>");

        jXHyperlink1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/apps/internet-web-browser.png"))); // NOI18N
        jXHyperlink1.setText("Per maggiori dettagli e informazioni clicca qui");
        jXHyperlink1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink1ActionPerformed(evt);
            }
        });

        loading.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        loading.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        loading.setText("aggiornamento in corso");
        loading.setBusy(true);
        loading.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        loading.setMinimumSize(new java.awt.Dimension(44, 20));
        loading.setPreferredSize(new java.awt.Dimension(44, 20));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 756, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(jButton1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(jXHyperlink1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 323, Short.MAX_VALUE)
                        .add(loading, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 200, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jButton1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jXHyperlink1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(loading, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tabPluginsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tabPluginsKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            actionAttivaPlugin.actionPerformed(null);
        }
    }//GEN-LAST:event_tabPluginsKeyPressed

    private void tabPluginsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabPluginsMouseClicked
        if (evt.getClickCount() >= 2) {
            actionAttivaPlugin.actionPerformed(null);
        }
    }//GEN-LAST:event_tabPluginsMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (main.attivazione.getIdRegistrazione() == null) {
            if (SwingUtils.showYesNoMessage(this, "Per utilizzare le funzionalità aggiuntive devi prima registrare il programma,\nclicca su Sì per proseguire nella registrazione")) {
                main.getPadre().apridatiazienda();
            }
        } else {
            scaricatutti();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jXHyperlink1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink1ActionPerformed
        try {
            SwingUtils.openUrl(new URL("http://www.tnx.it/index.php?action=acquista&ref=invoicex&ref2=0&i=" + main.attivazione.getIdRegistrazione()));
        } catch (Exception ex) {
            SwingUtils.showErrorMessage(this, ex.toString());
        }
    }//GEN-LAST:event_jXHyperlink1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new JDialogPlugins(new javax.swing.JFrame(), true).setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink1;
    public org.jdesktop.swingx.JXBusyLabel loading;
    public javax.swing.JTable tabPlugins;
    // End of variables declaration//GEN-END:variables

    Object aggiorna = new Object();

    public void aggiornaDati() {
        SwingWorker w = new SwingWorker() {

            SimpleDateFormat f1 = new SimpleDateFormat("dd/MM/yy");
            TableModelPlugins model = null;

            public Object construct() {
                //controllo lista plugins
                try {
                    String url = it.tnx.invoicex.Main.url_server;
                    HessianProxyFactory factory = new HessianProxyFactory();
                    Hservices service = (Hservices) factory.create(Hservices.class, url);
                    model = new TableModelPlugins(service.getPlugins2(false));

                    //scorro tutti i plugin e controllo se presenti e l'attivazione
                    for (Plugin p : model.plugins) {
                        p.setVersioneDisp(p.getVersione() + " " + f1.format(p.getData_ultima_modifica()));
                        if (main.pluginPresenti.contains(p.getNome_breve())) {
                            p.setPresente(true);
                            //recupero versione da quello in locale..
                            EntryDescriptor ed = (EntryDescriptor) main.plugins.get(p.getNome_breve());
                            if (ed.getVer() == null) {
                                ed.setVer("1.0");
                            }
                            if (ed.getData() == null) {
                                p.setVersioneInst(ed.getVer());
                            } else {
                                p.setVersioneInst(ed.getVer() + " " + f1.format(ed.getData()));
                            }
                        }
                        if (main.pluginAttivi.contains(p.getNome_breve())) {
                            p.setAttivo(true);
                        }
                    }

                    //controllo se togliere quelli privati
                    Iterator iter = model.plugins.iterator();
                    while (iter.hasNext()) {
                        Plugin2 p = (Plugin2) iter.next();
                        if (!p.isPresente() && p.props != null && p.props.get("privato") != null && (Boolean)p.props.get("privato") == true) {
                            System.out.println("rimuovo plugin privato " + p.getNome_breve());
                            iter.remove();
                        }
                    }

                } catch (HessianRuntimeException connerr) {
                    connerr.printStackTrace();
                    JOptionPane.showMessageDialog(JDialogPlugins.this, "Impossibile collegarsi al server TNX, riprovare più tardi.", "Errore", JOptionPane.ERROR_MESSAGE);
                    dispose();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(JDialogPlugins.this, "Errore:" + ex.getLocalizedMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                    dispose();
                }

                return null;
            }

            public void finished() {
                super.finished();
                synchronized(aggiorna) {
                    if (model != null) {
                        tabPlugins.setModel(model);
                        TableColumnModel tcm = tabPlugins.getColumnModel();
                        tcm.getColumn(0).setPreferredWidth(50);
                        tcm.getColumn(1).setPreferredWidth(150);
                        tcm.getColumn(2).setPreferredWidth(100);
                        tcm.getColumn(3).setPreferredWidth(20);
                        tcm.getColumn(4).setPreferredWidth(20);
                        tcm.getColumn(5).setPreferredWidth(10);
                        tcm.getColumn(6).setPreferredWidth(10);

                        tcm.getColumn(2).setHeaderRenderer(new PluginsHeaderRenderer(tabPlugins.getTableHeader().getDefaultRenderer()));
                        tcm.getColumn(2).setCellRenderer(new PluginsRenderer());
                        
                        tabPlugins.setRowHeight(26);
                    }
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    aggiorna.notify();
                }
            }
        };
        w.start();
    }
}
class PluginsHeaderRenderer implements TableCellRenderer {

    TableCellRenderer delegate = null;
    PanelPackHeaderRenderer panel = new PanelPackHeaderRenderer();
    TableCellRenderer default_render = null;

    PluginsHeaderRenderer(TableCellRenderer defaultRenderer) {
        delegate = defaultRenderer;
    }

    @Override
    public Component getTableCellRendererComponent(JTable jtable, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (default_render == null) {
            default_render = jtable.getTableHeader().getDefaultRenderer();
        }
        JComponent comp = (JComponent) default_render.getTableCellRendererComponent(jtable, value, isSelected, hasFocus, row, column);
        //se metto bordo su mac con java 7 non funziona, glitch grafici
//        try {
//            panel.setBorder(comp.getBorder());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return panel;
    }
    
}

class PluginsRenderer extends DefaultTableCellRenderer {

    PanelPackRenderer panel = new PanelPackRenderer();

    @Override
    public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int i1) {
        JLabel l = (JLabel) super.getTableCellRendererComponent(jtable, o, bln, bln1, i, i1);
        //return c;
        panel.setBorder(l.getBorder());
        panel.setBackground(l.getBackground());
        String pack = (String)o;
        String[] packs = StringUtils.split(pack, '|');
        List lpacks = Arrays.asList(packs);
        panel.base.setIcon(new javax.swing.ImageIcon(getClass().getResource("/it/tnx/invoicex/res/no.png")));
        if (lpacks.contains("pro")) {
            panel.pro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/it/tnx/invoicex/res/yes.png")));
        } else {
            panel.pro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/it/tnx/invoicex/res/no.png")));
        }
        if (lpacks.contains("proplus")) {
            panel.proplus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/it/tnx/invoicex/res/yes.png")));
        } else {
            panel.proplus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/it/tnx/invoicex/res/no.png")));
        }
        if (lpacks.contains("ent")) {
            panel.ent.setIcon(new javax.swing.ImageIcon(getClass().getResource("/it/tnx/invoicex/res/yes.png")));
        } else {
            panel.ent.setIcon(new javax.swing.ImageIcon(getClass().getResource("/it/tnx/invoicex/res/no.png")));
        }
        
        return panel;
    }

}