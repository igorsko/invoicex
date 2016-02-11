/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gestioneFatture;

import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.client.HessianRuntimeException;
import it.tnx.Db;
import it.tnx.commons.DbUtils;
import it.tnx.commons.HttpUtils;
import it.tnx.commons.SwingUtils;
import it.tnx.invoicex.Hservices;
import it.tnx.invoicex.PlatformUtils;
import it.tnx.invoicex.Plugin;
import it.tnx.invoicex.Plugin2;
import it.tnx.invoicex.gui.JDialogPlugins;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.swing.ImageIcon;
import mjpf.EntryDescriptor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author mceccarelli
 */
public class RiscattaAcquisto {

    static String ret = null;

    public static void riscattaAcquisto() throws UnsupportedEncodingException, Exception {
        String msg = "<html><b>Inserisci il numero Ordine da attivare</b><br>Il numero si trova nell'email di conferma Ordine, ad esempio: - ID Ordine: 123456</html>";
//        String ordine = JOptionPane.showInputDialog(main.getPadre(), msg);
        
        JDialogAttivazione da = new JDialogAttivazione(main.getPadre(), true);
        da.setLocationRelativeTo(null);
        da.setVisible(true);
        final String ordine = da.ordine;
        if (ordine != null) {
            SwingUtils.mouse_wait(main.getPadreFrame());
            main.getPadrePanel().lblInfoLoading2.setVisible(true);
            main.getPadrePanel().lblInfoLoading2.setText("Attivazione in corso");
            Thread tattivazione = new Thread("attivazione") {

                @Override
                public void run() {
                    //invio id ordine
                    String vl = main.version + " (" + main.build + ")";
                    try {
                        String partiva_iva = main.attivazione.getDatiAzienda().getPartita_iva();
                        String url = "http://www.tnx.it/pagine/invoicex_server/a.php?o=" + URLEncoder.encode(ordine, "UTF-8") + "&pi=" + URLEncoder.encode(partiva_iva, "UTF-8") + "&v=" + URLEncoder.encode(vl, "UTF-8") + (main.attivazione.getIdRegistrazione() != null ? "&i=" + main.attivazione.getIdRegistrazione() : "") + "&so=" + URLEncoder.encode(System.getProperty("os.name") + ":" + System.getProperty("os.version"), "UTF-8");
                        ret = HttpUtils.getUrlToStringUTF8(url);
                        String msglog = "Attivazione ID:" + main.attivazione.getIdRegistrazione() + " ORDINE:" + ordine + " ESITO:" + ret;
                        String sql = "insert into attivazioni set msg = " + Db.pc(msglog, Types.VARCHAR);
                        DbUtils.tryExecQuery(Db.getConn(), sql);

                        //controllare se presenti i plugin, se non presenti li scarico
                        if (ret.startsWith("Attivato:")) {
                            String ver = null;
                            if (ret.indexOf("Professional Plus") >= 0) {
                                ver = "Professional Plus";
                            } else if (ret.indexOf("Professional") >= 0) {
                                ver = "Professional";
                            } else if (ret.indexOf("Enterprise") >= 0) {
                                ver = "Enterprise";
                            }
                            
                            sql = "delete from attivazione";
                            DbUtils.tryExecQuery(Db.getConn(), sql);
                            sql = "insert into attivazione "
                                    + " set codice = " + Db.pc(ordine, Types.VARCHAR) + ""
                                    + ", versione = " + Db.pc(ver, Types.VARCHAR) + ""
                                    + ", esito_log = " + Db.pc(ret, Types.VARCHAR) + ""
                                    + ", ts = " + Db.pc(new Date(), Types.TIMESTAMP);
                            DbUtils.tryExecQuery(Db.getConn(), sql);
                            
                            //controllo che versione è
                            String oldv = main.versione;
                            
                            //adesso controllo solo da acquisti
//                            url = "http://www.tnx.it/pagine/invoicex_server/ver.php?v=" + URLEncoder.encode(vl, "UTF-8") + (main.attivazione.getIdRegistrazione() != null ? "&i=" + main.attivazione.getIdRegistrazione() : "") + "&so=" + URLEncoder.encode(System.getProperty("os.name") + ":" + System.getProperty("os.version"), "UTF-8");
//                            System.out.println("url: " + url);
//                            String ver = main.getURL(url);
                            
                            System.out.println("ver = " + ver);
                            if (ver != null && ver.length() > 50) {
                                System.out.println("ignoro problema su server:\n");
                                System.out.println("ver = " + ver);
                            } else {
                                if (ver != null && ver.length() > 0) {
                                    main.versione = ver;
                                    main.fileIni.setValue("cache", "versione", ver);
                                } else {
                                    main.versione = "Base";
                                    main.fileIni.setValue("cache", "versione", "Base");
                                }
                                if (!main.versione.equalsIgnoreCase("Base")) {
                                    main.fileIni.setValue("cache", "versioneu", main.versione);
                                    //se versione a pagamento scarico gli eventuali plugin se non presenti
                                    controllaPluginPerVersione(main.versione);
                                }
                                if (!oldv.equals(main.versione)) {
                                    main.getPadreFrame().aggiornaTitle();
                                }
                            }
                        } else if (ret.startsWith("err:pivadiversa:")) {
                            //errore partita iva
                            ret = StringUtils.substringAfter(ret, "err:pivadiversa:");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    
                    SwingUtils.inEdt(new Runnable() {
                        public void run() {
                            SwingUtils.mouse_def(main.getPadreFrame());
                            main.getPadrePanel().lblInfoLoading2.setVisible(false);
                            main.getPadrePanel().lblInfoLoading2.setText("");
                            SwingUtils.showInfoMessage(main.getPadre(), "Esito attivazione: " + ret);
                            if (ret.indexOf("Attivato") >= 0) {
                                SwingUtils.showInfoMessage(main.getPadre(), "Si prega di riavviare Invoicex");
                            }
                        }
                    });

                }

            };
            tattivazione.start();            
            
        }
    }

    public static void controllaPluginPerVersione(String versione) {
        System.out.println(" controllo per versione = " + versione);

        //elenco plugin scaricati
        if (!main.fine_init_plugin) {
            System.out.println("in attesa del caricamento dei plugins");
            long max = System.currentTimeMillis() + (1000 * 30);
            while (main.fine_init_plugin == false) {
                try {
                    Thread.sleep(5000);
                    if (System.currentTimeMillis() > max) {
                        System.out.println("timeout controllaPluginPerVersione");
                        return;
                    }
                    System.out.println("in attesa del caricamento dei plugins...");
                    if (main.s1) {
                        return;
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            System.out.println("caricamento del plugin completato");
        }

        //controllo lista plugins
        List<Plugin> pluginsDaScaricare = new ArrayList();
        try {
            SimpleDateFormat f1 = new SimpleDateFormat("dd/MM/yy");
            
            String url = it.tnx.invoicex.Main.url_server;
            HessianProxyFactory factory = new HessianProxyFactory();
            Hservices service = (Hservices) factory.create(Hservices.class, url);
            List<Plugin> plugins = service.getPlugins2(false);
            //scorro tutti i plugin e controllo se presenti e l'attivazione
            for (Plugin p : plugins) {
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
                } else {
                    System.out.println(p.getNome_breve());
                    Plugin2 p2 = (Plugin2) p;
                    String pack = (String) p2.props.get("pack");
                    boolean privato = false;
                    if (p2.props != null && p2.props.get("privato") != null && (Boolean)p2.props.get("privato") == true) {
                        privato = true;
                    }
                    System.out.println("pack:" + pack);
                    if (!privato) {
                        String[] packs = StringUtils.split(pack, '|');
                        List lpacks = Arrays.asList(packs);
                        if (main.versione.equalsIgnoreCase("Professional")) {
                            if (lpacks.contains("pro")) {
                                pluginsDaScaricare.add(p);
                            }
                        } else if (main.versione.equalsIgnoreCase("Professional Plus")) {
                            if (lpacks.contains("proplus")) {
                                pluginsDaScaricare.add(p);
                            }
                        } else if (main.versione.equalsIgnoreCase("Enterprise")) {
                            if (lpacks.contains("ent")) {
                                pluginsDaScaricare.add(p);
                            }
                        }
                    }
                }
                if (main.pluginAttivi.contains(p.getNome_breve())) {
                    p.setAttivo(true);
                }
            }
            //controllo se togliere quelli privati
            Iterator iter = plugins.iterator();
            while (iter.hasNext()) {
                Plugin2 p = (Plugin2) iter.next();
                if (!p.isPresente() && p.props != null && p.props.get("privato") != null && (Boolean)p.props.get("privato") == true) {
                    System.out.println("rimuovo plugin privato " + p.getNome_breve());
                    iter.remove();
                }
            }
            System.out.println("pluginsDaScaricare = " + pluginsDaScaricare);

            //scarico i plugins
            for (Plugin p : pluginsDaScaricare) {
                System.out.println("scarico: = " + p.getNome_breve());
                JDialogPlugins.scarica(JDialogPlugins.getNomeDownload(p.getNome_breve()));
            }
        } catch (HessianRuntimeException connerr) {
            connerr.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        cambiaIcone(versione);
    }

    public static void cambiaIcone(String versione) {
        File logovertxt = new File("img/logover.txt");
        boolean controllologovertxt = false;
        String versionetxt = null;
        if (logovertxt.exists()) {
            try {
                FileReader frlogovertxt = new FileReader(logovertxt);
                versionetxt = IOUtils.toString(frlogovertxt);
                frlogovertxt.close();
                if (versionetxt != null && versione != null && versione.equals(versionetxt)) {
                    controllologovertxt = true;     //sono uguali
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //cambio icone e splash
        String estver = "";
        if (versione.equalsIgnoreCase("professional")) {
            estver = "pro";
        } else if (versione.equalsIgnoreCase("professional plus")) {
            estver = "pro-plus";
        } else if (versione.equalsIgnoreCase("enterprise")) {
            estver = "ent";
        } else {
            estver = "base";
        }
        if (!main.fileIni.getValue("varie", "ver_icone", "").equals(estver) || !controllologovertxt) {
            if (PlatformUtils.isWindows()) {
                //cambio exe
                try {
                    File f = new File("Invoicex-" + estver + ".exe");
                    if (f.exists()) {
                        f.delete();
                    }
                    f = new File("Invoicex-old.exe");
                    if (f.exists()) {
                        f.delete();
                    }
                    String url = "http://www.tnx.it/pagine/invoicex_server/download/invoicex/Invoicex-" + estver + ".exe";
                    try {
                        main.check_connessione();
                        HttpUtils.saveFile(url, "Invoicex-" + estver + ".exe");
                        f = new File("Invoicex.exe");
                        File f2 = new File("Invoicex-old.exe");                        
                        f.renameTo(f2);
                        f = new File("Invoicex-" + estver + ".exe");
                        f2 = new File("Invoicex.exe");
                        f.renameTo(f2);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    //elimino icon cache
                    String filecache = System.getProperty("user.home") + "\\AppData\\Local\\IconCache.db";
                    String os = System.getProperty("os.name").toLowerCase();
                    if (os.equals("windows 95")
                            || os.equals("windows 98")
                            || os.equals("windows me")
                            || os.equals("windows nt")
                            || os.equals("windows 2000")
                            || os.equals("windows xp")) {
                        filecache = System.getProperty("user.home") + "\\Local Settings\\Application Data\\IconCache.db";
                    }
                    f = new File(filecache);
                    System.out.println("icon cache 1 : " + f + " exist:" + f.exists());
                    f.delete();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (PlatformUtils.isMac()) {
                //mac
                File a = new File("../Resources/48x48.icns");
                File aold = new File("../Resources/48x48-old.icns");
                if (!a.exists() || a.renameTo(aold)) {
                    String url = "http://www.tnx.it/pagine/invoicex_server/download/invoicex/icone/mac/" + estver + ".icns";
                    try {
                        main.check_connessione();
                        HttpUtils.saveFile(url, a.getAbsolutePath());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    System.out.println("impopssibile rinominare " + a + " in " + aold);
                }
                //mac dock
                a = new File("icone/invoicex_ico.png");
                aold = new File("icone/invoicex_ico-old.png");
                if (!a.exists() || a.renameTo(aold)) {
                    String url = "http://www.tnx.it/pagine/invoicex_server/download/invoicex/icone/png/" + estver + "/512x512_24.png";
                    try {
                        main.check_connessione();
                        HttpUtils.saveFile(url, a.getAbsolutePath());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    System.out.println("impopssibile rinominare " + a + " in " + aold);
                }
            } else {
                //linux
            }
            //in generale
            File a = new File("icone/48x48.ico");
            File aold = new File("icone/48x48-old.ico");
            if (!a.exists() || a.renameTo(aold)) {
                String url = "http://www.tnx.it/pagine/invoicex_server/download/invoicex/icone/win/" + estver + ".ico";
                try {
                    main.check_connessione();
                    HttpUtils.saveFile(url, a.getAbsolutePath());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                System.out.println("impopssibile rinominare " + a + " in " + aold);
            }

            //icona menu frame
            try {
                a = new File("icone/48x48_8.gif");
                String url = "http://www.tnx.it/pagine/invoicex_server/download/invoicex/icone/png/" + estver + "/48x48_8.gif";
                try {
                    main.check_connessione();
                    HttpUtils.saveFile(url, a.getAbsolutePath());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                main.getPadreFrame().setIconImage(new ImageIcon("icone/48x48_8.gif").getImage());
            } catch (Exception err) {
                err.printStackTrace();
            }

            //splashscreen
            try {
                a = new File("img/logover.png");
                if (a.exists()) a.delete();
                String url = "http://www.tnx.it/pagine/invoicex_server/download/invoicex/splashscreen/" + estver + ".png";
                try {
                    main.check_connessione();
                    HttpUtils.saveFile(url, a.getAbsolutePath());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } catch (Exception err) {
                err.printStackTrace();
            }
            main.fileIni.setValue("varie", "ver_icone", estver);
            try {                
                FileWriter fwlogovertxt = new FileWriter(logovertxt);
                IOUtils.write(versione, fwlogovertxt);
                fwlogovertxt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //icone già impostate
        }
    }

}
