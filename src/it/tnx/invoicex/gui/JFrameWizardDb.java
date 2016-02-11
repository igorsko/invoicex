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

import it.tnx.Db;
import gestioneFatture.WizardDb;
import gestioneFatture.main;
import it.tnx.commons.DbUtils;
import it.tnx.commons.HttpUtils;
import it.tnx.commons.SwingUtils;
import it.tnx.commons.UnZip;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingworker.SwingWorker;

/**
 *
 * @author  test1
 */
public class JFrameWizardDb extends javax.swing.JFrame {

    public static boolean exit = true;
    public SwingWorker worker;

    /** Creates new form JDialogWizardDb */
    public JFrameWizardDb() {
        super();
        initComponents();
        try {
            setIconImage(main.getLogoIcon());
        } catch (Exception err) {
            err.printStackTrace();
        }
        jToggleButton1.setSelected(true);
        setComps();
    }

    private boolean creaDbEsterno() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String url = "jdbc:mysql://" + server.getText() + "/?user=" + username.getText() + "&password=" + password.getText() + "&jdbcCompliantTruncation=false&zeroDateTimeBehavior=round&emptyStringsConvertToZero=true";
            Connection conn = DriverManager.getConnection(url, username.getText(), password.getText());

            //salvo impostazioni
            main.startDbCheck = false;
            main.startConDbCheck = false;

            Statement stat = conn.createStatement();

            //controllo se esiste già non lo creo
            boolean esiste = false;
            try {
                ResultSet r = stat.executeQuery("select * from " + nomedb.getText() + ".clie_forn");
                r.close();
                esiste = true;
            } catch (SQLException sqlerr) {
                System.out.println(sqlerr + " / " + sqlerr.getStackTrace()[0]);
            }

            if (!esiste) {
                if (JOptionPane.showConfirmDialog(this, "Il database " + nomedb.getText() + " non esiste, proseguendo verrà creato", "Attenzione", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                    salvaDatiEsterno();
                    //creo il db
                    try {
                        stat.execute("create database IF NOT EXISTS " + nomedb.getText());
                        stat.execute("use " + nomedb.getText());

                        WizardDb.progress.setLocationRelativeTo(this);
                        WizardDb.progress.setAlwaysOnTop(true);
                        WizardDb.progress.setVisible(true);
                        WizardDb.progress.labStatus.setText("creazione database iniziale");

                        boolean ret_creadb = creazioneDb(conn);
                        return ret_creadb;
                    } catch (Throwable ex) {
                        JDialogExc exc = new JDialogExc(this, true, ex);
                        exc.setVisible(true);
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                JOptionPane.showMessageDialog(this, "Il database " + nomedb.getText() + " esiste già e verrà utilizzato da questa postazione", "Informazione", JOptionPane.INFORMATION_MESSAGE);
                salvaDatiEsterno();
            }
        } catch (Exception e) {
            e.printStackTrace();
            String problema = "";
            if (e.getCause() != null) {
                String causa = e.getCause().getClass().getSimpleName();
                String dettaglio = e.getCause().getLocalizedMessage();
                problema = causa + " " + dettaglio;
                if (causa.equalsIgnoreCase("UnknownHostException")) {
                    problema += "<br><br><b>Hai un problema di rete e/o firewall per il quale non si riesce a raggiungere il server <br>Se stai provando a collegarti su un server in hosting probabilmente il fornitore blocca la porta dall'esterno</b>";
                }
            } else {
                problema = e.getLocalizedMessage();
                if (problema.toLowerCase().indexOf("using password: yes") >= 0) {
                    problema += "<br><br><b>Il server è raggiungibile ma il nome utente o la password sono errati";
                    problema += "<br>Oppure l'utente non il permesso per accedere dall'esterno";
                    problema += "</b>";
                }
            }
            JOptionPane.showMessageDialog(this, "<html>Impossibile collegarsi al server <b>" + server.getText() + "</b><br>Controlla i dati di connessione e riprova<br>Problema: " + problema + "</html>", "Attenzione", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean creaDbLocale() {

        System.out.println("creaDbLocale");
        main.startDbCheck = true;

//        SwingUtils.showFlashMessage("...avvio database locale...", 3);

        WizardDb.progress.setLocationRelativeTo(null);
//        WizardDb.progress.setAlwaysOnTop(true);
        WizardDb.progress.setVisible(true);
        WizardDb.progress.progressbar.setIndeterminate(true);
        WizardDb.progress.labStatus.setText("...avvio database locale..");

        if (startDb()) {

            System.out.println("creazione db");

            try {
                if (DbUtils.containRows(Db.getConn(true), "show databases like 'invoicex_default'")) {
                    //il database esiste già
                    try {
                        WizardDb.progress.setVisible(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        main.splash.setVisible(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    SwingUtils.showErrorMessage(WizardDb.wizard, "E' già presente un database invoicex_default, non è possibile continuare\nPer forzare l'installazione devi spostare la cartella Invoicex\\mysql\\data\\invoicex_default", true);
                    if (exit) {
                        System.exit(1);
                    }
                    dispose();
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Db.executeSql("create database IF NOT EXISTS invoicex_default");
            Db.executeSql("use invoicex_default");

            WizardDb.progress.progressbar.setIndeterminate(false);
            WizardDb.progress.labStatus.setText("Creazione database iniziale");

            boolean ret_creadb = creazioneDb(Db.getConn());
            return ret_creadb;
        } else {
            try {
                WizardDb.progress.setVisible(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                main.splash.setVisible(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                JDialogExc de = new JDialogExc(WizardDb.wizard, true, null);
                de.labInt.setFont(de.labInt.getFont().deriveFont(Font.BOLD, 14));
                de.labInt.setText("Errore nell'avvio del database locale");
                de.labe.setFont(de.labInt.getFont().deriveFont(Font.PLAIN, 12));
                de.setLocationRelativeTo(null);
                de.pack();
                de.setVisible(true);
                if (exit) {
                    System.exit(1);
                }
                dispose();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean creazioneDb(final Connection conn) {
        boolean ok = false;
        FileInputStream fis = null;
        try {
//            File fe = new File(main.wd + "mysql/createdb_" + main.version.toStringUnderscore() + ".sql");
//            File fe = new File(main.wd + "mysql/createdb_1_7_7.sql");
//            File fe = new File(main.wd + "mysql/createdb_1_8_2.sql");
            File fe = new File(main.wd + "mysql/createdb_1_8_5.sql");
            if (!fe.exists()) {
                //scarico se non presente
                SwingUtils.showFlashMessage2("Scaricamento in corso di " + fe.getName(), 3, null, Color.red, new Font(null, Font.BOLD, 16), true);
                String nomefile = fe.getName();
                String nomefilezip = StringUtils.substringBeforeLast(nomefile, ".") + ".zip";
                try {
                    File filezip = new File(nomefilezip);
                    if (filezip.exists()) {
                        filezip.delete();
                    }
                    String url = "http://www.tnx.it/pagine/invoicex_server/download/invoicex/mysql/" + nomefilezip;
                    String filelocale = "mysql/" + nomefilezip;
                    HttpUtils.saveBigFile(url, filelocale);
                    UnZip.unzip(new File(filelocale), "mysql/");
                } catch (Exception e) {
                    e.printStackTrace();
                    SwingUtils.showErrorMessage(this, e.getMessage());
                }
            }
            fis = new FileInputStream(fe);
            String sql = "";
            byte[] buff2 = new byte[(int) fe.length()];
            fis.read(buff2);
            sql = new String(buff2);

            Statement stat;
            try {
                stat = conn.createStatement();
                String[] sqls = sql.split(";\\r\\n");
                int conta = 0;
                WizardDb.progress.progressbar.setIndeterminate(false);
                WizardDb.progress.progressbar.setMinimum(0);
                WizardDb.progress.progressbar.setMaximum(100);
                for (int i = 0; i < sqls.length; i++) {
                    conta += sqls[i].length();
                    String sqlc = sqls[i];
                    stat.execute(sqlc);
                    if (conta % 100 == 0) {
                        int perc = conta * 100 / buff2.length;
                        WizardDb.progress.labStatus.setText("Creazione database iniziale " + perc + "%");
                        WizardDb.progress.progressbar.setValue(perc);
                    }
                }
                stat.close();
            } catch (Exception err) {
                err.printStackTrace();
                System.out.println("sql di errore:" + sql);
            }

            System.out.println("fine creazione db");
            main.stopdb(false);
            ok = true;
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(JFrameWizardDb.class.getName()).log(Level.SEVERE, null, ex);
            JDialogExc exc = new JDialogExc(WizardDb.wizard, true, ex);
            exc.setVisible(true);
        } catch (IOException ex) {
            Logger.getLogger(JFrameWizardDb.class.getName()).log(Level.SEVERE, null, ex);
            JDialogExc exc = new JDialogExc(WizardDb.wizard, true, ex);
            exc.setVisible(true);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(JFrameWizardDb.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ok;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jToggleButton2 = new javax.swing.JToggleButton();
        jToggleButton1 = new javax.swing.JToggleButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        server = new javax.swing.JTextField();
        nomedb = new javax.swing.JTextField();
        username = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        password = new javax.swing.JPasswordField();
        jSeparator1 = new javax.swing.JSeparator();

        FormListener formListener = new FormListener();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Invoicex - primo avvio");
        setFont(new java.awt.Font("Tahoma", 0, 11));
        addWindowListener(formListener);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("E' la prima volta che esegui Invoicex, scegli come utilizzarlo");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel1.setMaximumSize(new java.awt.Dimension(359, 25));
        jLabel1.setMinimumSize(new java.awt.Dimension(359, 25));
        jLabel1.setPreferredSize(new java.awt.Dimension(359, 25));
        getContentPane().add(jLabel1, java.awt.BorderLayout.NORTH);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/process-stop.png"))); // NOI18N
        jButton2.setText("Chiudi");
        jButton2.setPreferredSize(new java.awt.Dimension(110, 35));
        jButton2.addActionListener(formListener);
        jPanel2.add(jButton2);

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 11));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/edit-redo.png"))); // NOI18N
        jButton1.setText("Prosegui");
        jButton1.setPreferredSize(new java.awt.Dimension(130, 35));
        jButton1.addActionListener(formListener);
        jPanel2.add(jButton1);

        getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);

        jToggleButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/22x22/places/network-workgroup.png"))); // NOI18N
        jToggleButton2.setText("<html><center><font size=\"4\"><b>Usa Invoicex in rete</b></font><br><font size=\"2\">(viene utilizzato un database<br> Mysql 5.x esterno)</center></html>");
        jToggleButton2.setMinimumSize(new java.awt.Dimension(300, 51));
        jToggleButton2.addActionListener(formListener);

        jToggleButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/22x22/places/network-server.png"))); // NOI18N
        jToggleButton1.setText("<html><center><font size=4><b>Usa Invoicex su questo computer</b></font><br><font size=2>(viene utilizzato un database interno al programma)</center></html>");
        jToggleButton1.setMinimumSize(new java.awt.Dimension(300, 39));
        jToggleButton1.addActionListener(formListener);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Impostazioni Mysql esterno"));

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Server");

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Nome db");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Username");

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Password");

        jLabel6.setFont(jLabel6.getFont().deriveFont(jLabel6.getFont().getSize()-2f));
        jLabel6.setForeground(new java.awt.Color(102, 102, 102));
        jLabel6.setText("<html><center>(Attenzione, se il db sul server esiste verrà usato da questa postazione di Invoicex, se non esiste verrà creato)</center></html>");
        jLabel6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 85, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 85, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, nomedb, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
                            .add(server, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
                            .add(jLabel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                        .add(jLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 85, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(username, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jLabel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 85, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(password, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(server, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(nomedb, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(username, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(password, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 636, Short.MAX_VALUE)
            .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .add(jToggleButton1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(8, 8, 8)
                    .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jToggleButton2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE))
                    .addContainerGap()))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 376, Short.MAX_VALUE)
            .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
                        .add(jPanel3Layout.createSequentialGroup()
                            .add(jToggleButton2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 114, Short.MAX_VALUE))
                        .add(jToggleButton1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap()))
        );

        getContentPane().add(jPanel3, java.awt.BorderLayout.CENTER);

        pack();
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener, java.awt.event.WindowListener {
        FormListener() {}
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == jButton2) {
                JFrameWizardDb.this.jButton2ActionPerformed(evt);
            }
            else if (evt.getSource() == jButton1) {
                JFrameWizardDb.this.jButton1ActionPerformed(evt);
            }
            else if (evt.getSource() == jToggleButton2) {
                JFrameWizardDb.this.jToggleButton2ActionPerformed(evt);
            }
            else if (evt.getSource() == jToggleButton1) {
                JFrameWizardDb.this.jToggleButton1ActionPerformed(evt);
            }
        }

        public void windowActivated(java.awt.event.WindowEvent evt) {
        }

        public void windowClosed(java.awt.event.WindowEvent evt) {
        }

        public void windowClosing(java.awt.event.WindowEvent evt) {
            if (evt.getSource() == JFrameWizardDb.this) {
                JFrameWizardDb.this.formWindowClosing(evt);
            }
        }

        public void windowDeactivated(java.awt.event.WindowEvent evt) {
        }

        public void windowDeiconified(java.awt.event.WindowEvent evt) {
        }

        public void windowIconified(java.awt.event.WindowEvent evt) {
        }

        public void windowOpened(java.awt.event.WindowEvent evt) {
        }
    }// </editor-fold>//GEN-END:initComponents
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        setta(false);
        SwingWorker work1 = new SwingWorker() {

            @Override
            protected void done() {
                super.done();
                setta(true);
            }

            @Override
            protected Object doInBackground() throws Exception {
                //controlli
                boolean ret = false;
                if (jToggleButton1.isSelected()) {
                    //creo il db in locale
                    ret = creaDbLocale();
                } else {
                    //controllo se si collega, altrimenti creo il db esterno e riprovo a collegarsi
                    if (server.getText().trim().length() == 0 || nomedb.getText().trim().length() == 0 || username.getText().trim().length() == 0) {
                        JOptionPane.showMessageDialog(JFrameWizardDb.this, "Inserire i dati necessari per il collegamento al Mysql esterno", "Attenzione", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        ret = creaDbEsterno();
                    }
                }

                if (!ret) {
                    //faccio rimanere in attesa che si riprovi altrimenti chiuderanno via annulla
//                    JOptionPane.showMessageDialog(null, "Per avviare il programma è necessario terminare la procedura di configurazione database", "Errore", JOptionPane.INFORMATION_MESSAGE);
//                    System.exit(0);
                } else {
                    main.fileIni.setValue("wizard", "eseguito", "S");
                    main.fileIni.saveFile();
                    WizardDb.ok = true;
                    JFrameWizardDb.this.dispose();
                    main.INSTANCE.post_wizard();
                    main.wizard_in_corso = false;
                }

                return null;
            }
        };
        work1.execute();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        jToggleButton1.getModel().setSelected(true);
        jToggleButton2.getModel().setSelected(false);
        setComps();
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void jToggleButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton2ActionPerformed
        jToggleButton1.getModel().setSelected(false);
        jToggleButton2.getModel().setSelected(true);
        setComps();
    }//GEN-LAST:event_jToggleButton2ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        System.out.println("exit?" + exit);
        if (exit) {
            System.exit(0);
        }
        dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        jButton2ActionPerformed(null);
    }//GEN-LAST:event_formWindowClosing

    private void setta(boolean val) {
        jButton1.setEnabled(val);
        jButton2.setEnabled(val);
        jToggleButton1.setEnabled(val);
        jToggleButton2.setEnabled(val);
    }

    private void setComps() {
        jPanel1.setEnabled(!jToggleButton1.getModel().isSelected());
        server.setEnabled(!jToggleButton1.getModel().isSelected());
        nomedb.setEnabled(!jToggleButton1.getModel().isSelected());
        username.setEnabled(!jToggleButton1.getModel().isSelected());
        password.setEnabled(!jToggleButton1.getModel().isSelected());
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton jButton1;
    public javax.swing.JButton jButton2;
    public javax.swing.JLabel jLabel1;
    public javax.swing.JLabel jLabel2;
    public javax.swing.JLabel jLabel3;
    public javax.swing.JLabel jLabel4;
    public javax.swing.JLabel jLabel5;
    public javax.swing.JLabel jLabel6;
    public javax.swing.JPanel jPanel1;
    public javax.swing.JPanel jPanel2;
    public javax.swing.JPanel jPanel3;
    public javax.swing.JSeparator jSeparator1;
    public javax.swing.JToggleButton jToggleButton1;
    public javax.swing.JToggleButton jToggleButton2;
    public javax.swing.JTextField nomedb;
    public javax.swing.JPasswordField password;
    public javax.swing.JTextField server;
    public javax.swing.JTextField username;
    // End of variables declaration//GEN-END:variables

    private boolean startDb() {
        String oldNameDb = Db.dbNameDB;
        try {
            Db.dbNameDB = "mysql";
            if (System.getProperty("os.name").toLowerCase().indexOf("win") < 0) {
                System.out.println("creaDbLocale:startDb:non win");
                //con windows utilizzo named pipes e non ho bisogno di porte aperte
                //test porte db
                //trovo una porta libera per mysql
                Db connessioneTest = new Db();
                if (main.startDbCheck) {
                    System.out.println("creaDbLocale:startDb:non win:test porte");
                    int portaMin = 3306;
                    int portaMax = portaMin + 10;
                    int portaProva = portaMin;
                    boolean portaOk = false;
                    while (portaOk == false) {
                        //primo test con socket
                        try {
                            Socket client = new Socket();
                            client.connect(new InetSocketAddress("127.0.0.1", portaProva), 2000);
                            portaProva++;
                            client.close();
                        } catch (IOException ioexp) {
                            //errore quindi dovrebbe essere libera..
                            //secondo test con server socket
                            try {
                                ServerSocket socket = new ServerSocket(portaProva);
                                portaOk = true;
                                Db.dbPort = portaProva;
                                main.dbPortaOk = portaProva;
                                socket.close();
                            } catch (IOException ioexp2) {
                                portaProva++;
                            }
                        }
                    }
                    if (portaOk == false) {
                        JOptionPane.showMessageDialog(null, "Impossibile attivare il database: nessuna porta libera da " + portaMin + " a " + portaMax, "Errore", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                    System.out.println("creaDbLocale:startDb:non win:test porte:" + Db.dbPort);
                } else {
                    if (Db.dbPort == 0) {
                        Db.dbPort = 3306;
                    }
                }
            }

            try {
                if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
                    System.out.println("startdb per win");
                    main.startDb = main.win_startDb;
                } else if (System.getProperty("os.name").toLowerCase().indexOf("lin") >= 0) {
                    System.out.println("startdb per lin");
                    main.startDb = StringUtils.replace(main.lin_startDb, "{port}", String.valueOf(Db.dbPort));
                } else if (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0) {
                    System.out.println("startdb per mac");
                    main.startDb = StringUtils.replace(main.mac_startDb, "{port}", String.valueOf(Db.dbPort));
                }
                main.fileIni.setValue("db", "startdb", main.startDb);
            } catch (Exception err) {
                err.printStackTrace();
            }

            //controllo se non fosse già in esecuzione altro mysql
            Db connessione = new Db();
            if (main.startDbCheck) {
                Db.dbServ = "127.0.0.1:" + main.dbPortaOk;
                Db.dbPort = main.dbPortaOk;
                if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
                    Db.useNamedPipes = true;
                }
            }
            if (connessione.dbConnect(true)) {
                System.out.println("prova connessione pre:ok");
                try {
                    String datadir = (String) DbUtils.getListArray(connessione.conn, "show variables like 'datadir'").get(0)[1];
                    System.out.println("datadir  = " + datadir);
                    File datadir2f = new File("");
                    String datadir2 = datadir2f.getAbsolutePath() + File.separator + "mysql" + File.separator + "data";
                    System.out.println("datadir2 = " + datadir2);
                    if (!datadir.equalsIgnoreCase(datadir2)) {
                        SwingUtils.showErrorMessage(this, "Invoicex sembra già in esecuzione e non puoi continuare\nPer continuare chiudi Invoicex o altre istanze di mysql", true);
                        if (exit) {
                            System.exit(1);
                        }
                        dispose();
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (main.startDbCheck) {
                if (main.flagWebStart == false) {
                    main.startdb();
                }
            }

            if (main.flagWebStart == false) {
                //faccio test di connessione
                int proveConn = 0;
                boolean connOk = false;
                while (!connOk && proveConn < 2) {
                    System.out.println("prova connessione:" + proveConn);
                    if (connessione.dbConnect(true)) {
                        connOk = true;
                        System.out.println("prova connessione:ok");
                    } else {
                        System.out.println("prova connessione:ko aspetto...");
                        try {
                            Thread.sleep(3000);
                            if (main.startDbCheck) {
                                //tento stop e ritento start
                                main.stopdb(false);
                                Thread.sleep(3000);
                                main.startdb();
                                Thread.sleep(3000);
                            }
                        } catch (InterruptedException ierr) {
                        }
                    }
                    proveConn++;
                }

                if (!connessione.dbConnect(true)) {
                    return false;
                }
            }

        } finally {
            Db.dbNameDB = oldNameDb;
        }

        return true;
    }

    private void salvaDatiEsterno() {
        main.fileIni.setValue("db", "server", server.getText());
        main.fileIni.setValue("db", "nome_database", nomedb.getText());
        main.fileIni.setValue("db", "user", username.getText());
        main.fileIni.setValueCifrato("db", "pwd", password.getText());
        main.fileIni.setValue("db", "startdbcheck", "N");
        main.fileIni.saveFile();
        main.loadIni();
        Db.useNamedPipes = false;
    }
}
