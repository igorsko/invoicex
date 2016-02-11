/**
 * Invoicex Copyright (c) 2005,2006,2007 Marco Ceccarelli, Tnx snc
 *
 * Questo software è soggetto, e deve essere distribuito con la licenza GNU
 * General Public License, Version 2. La licenza accompagna il software o potete
 * trovarne una copia alla Free Software Foundation http://www.fsf.org .
 *
 * This software is subject to, and may be distributed under, the GNU General
 * Public License, Version 2. The license should have accompanied the software
 * or you may obtain a copy of the license from the Free Software Foundation at
 * http://www.fsf.org .
 *
 * -- Marco Ceccarelli (m.ceccarelli@tnx.it) Tnx snc (http://www.tnx.it)
 *
 */
/*
 * JDialogImpostazioni.java
 *
 * Created on 4 gennaio 2007, 9.48
 */
package gestioneFatture;

import it.tnx.Db;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;

import it.tnx.SwingWorker;
import it.tnx.accessoUtenti.Permesso;
import it.tnx.commons.CastUtils;
import it.tnx.commons.DbUtils;
import it.tnx.commons.FileUtils;
import it.tnx.commons.ImgUtils;
import it.tnx.commons.SwingUtils;
import it.tnx.commons.combo.WideComboBox;
import it.tnx.commons.cu;
import it.tnx.commons.swing.JDialogFontChooser;
import it.tnx.invoicex.InvoicexUtil;
import it.tnx.invoicex.PlatformUtils;
import it.tnx.invoicex.gui.JDialogJasperViewer;
import it.tnx.invoicex.gui.logoresize.JDialogLogoResize;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.apache.commons.lang.StringUtils;
import reports.JRDSInvoice;
import uk.co.jaimon.test.SimpleImageInfo;

/**
 *
 * @author mceccarelli
 */
public class JDialogImpostazioni extends javax.swing.JDialog {

    private static final int grandezzaTooltipImage = 150;
    String oldHost;
    String oldDb;
    String oldId;
    String oldPwd;
    boolean vaisudb = false;
    private Image image;
    private int width, height;
    private static final int ACCSIZE = 155;
    private boolean opening;
    private Font font;
    private boolean riavviare = false;
    String old_file_logo = null;
    String old_file_logo_email = null;
    String old_file_sfondo = null;
    String old_file_sfondo_email = null;
    public boolean salvare_logo = false;
    public boolean salvare_logo_email = false;
    public boolean salvare_sfondo = false;
    public boolean salvare_sfondo_email = false;

    /**
     * Creates new form JDialogImpostazioni
     */
    public JDialogImpostazioni(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        init(parent, modal, false);
    }

    public JDialogImpostazioni(java.awt.Frame parent, boolean modal, boolean vaisudb) {
        super(parent, modal);
        this.vaisudb = vaisudb;
        init(parent, modal, vaisudb);
    }

    public void preparaTipoStampa(JComboBox comTipoStampa, final int tipo) {
        //scan della dir dei reports
        File freports = new File(main.wd + Reports.DIR_REPORTS + Reports.DIR_FATTURE);
        File[] dir = freports.listFiles(new java.io.FileFilter() {
            public boolean accept(File pathname) {
//                System.out.println(pathname + " | " + pathname.getName());
                String nomeFile = pathname.getName();
                if (nomeFile.indexOf("_old_") >= 0) {
                    return false;
                }
                if (nomeFile.endsWith(".jrxml")) {
                    if (tipo == 0) {
                        if (nomeFile.startsWith("fattura")) {
                            if (!nomeFile.startsWith("fattura_acc") && !nomeFile.startsWith("fattura_acquisto")) {
                                return true;
                            }
                        }
                    } else if (tipo == 1) {
                        if (nomeFile.startsWith("fattura_acc")) {
                            return true;
                        }
                    } else if (tipo == 2) {
                        if (nomeFile.startsWith("ddt")) {
                            return true;
                        }
                    } else if (tipo == 3) {
                        if (nomeFile.startsWith("ordine")) {
                            return true;
                        }
                    }
                    return false;
                }
                return false;
            }
        });

        if (dir != null) {
            Arrays.sort(dir);
            for (int i = 0; i < dir.length; i++) {
                comTipoStampa.addItem(dir[i].getName());
            }
        }
    }

    public void init(java.awt.Frame parent, boolean modal, boolean vaisudb) {
        opening = true;
        initComponents();
        
        estraiScadenzeFatseq.setVisible(false);

        try {
//            setIconImage(new ImageIcon(getClass().getResource("/res/48x48.gif")).getImage());            
        } catch (Exception err) {
            err.printStackTrace();
        }

        tnxFileLogo.setVisible(false);
        tnxFileLogo1.setVisible(false);
        tnxFileSfondo.setVisible(false);
        tnxFileSfondoPdf.setVisible(false);

        ToolTipManager ttm = ToolTipManager.sharedInstance();
        ttm.setDismissDelay(10000);
        jScrollPane3.getVerticalScrollBar().setUnitIncrement(16);
        jScrollPane4.getVerticalScrollBar().setUnitIncrement(16);

        setLocationRelativeTo(parent);
        comLook.addItem("");
        comLook.addItem("System");
        comLook.addItem("Simple");
        comLook.addItem("JGoodies Plastic XP");
        comLook.addItem("Substance Nebula");
        comLook.addItem("Substance BusinessBlackSteel");
        comLook.addItem("Substance Creme");
        comLook.addItem("Tonic");
        if (PlatformUtils.isWindows()) {
            comLook.addItem("Office 2003");
            comLook.addItem("Office XP");
            comLook.addItem("Visual Studio 2005");
        }

        preparaTipoStampa(comTipoStampa, 1);
        preparaTipoStampa(comTipoStampa1, 0);
        preparaTipoStampa(comTipoStampaDdt, 2);
        preparaTipoStampa(comTipoStampaOrdine, 3);

        //aggiungo l'elenco dei listini
        if (!vaisudb) {
            ResultSet listini = Db.openResultSet("select codice, descrizione from tipi_listino order by codice");
            try {
                while (listini.next()) {
                    comListinoBase.addItem(listini.getString("codice"));
                }
            } catch (Exception err1) {
                err1.printStackTrace();
            }
        }

        comTipoLiquidazioneIva.addItem("Mensile");
        comTipoLiquidazioneIva.addItem("Trimestrale");
        comTipoLiquidazioneIva.addItem("Annuale");

        //carico impo da ini
        iniFileProp fileIni = main.fileIni;

        try {
            //cheAttivaUtenti.setSelected(fileIni.getValueBoolean("gestione_utenti", "attiva", false));
            //main.fileIni.setValue("gestione_utenti", "attiva", cheAttivaUtenti.isSelected());
            //codice iva per le spese se vuoto vengono ripartizionate
            if (Db.conn != null && !Db.conn.isClosed()) {
                try {
                    int utenti = CastUtils.toInteger0(DbUtils.getObject(Db.getConn(true), "select gestione_utenti from dati_azienda limit 1"));
                    cheAttivaUtenti.setSelected((utenti == 1 ? true : false));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (!main.getPersonalContain("emicad")) {
                jTabbedPane1.remove(panEmiCad);
            } else {
                cheAttivaEmiCad.setSelected(fileIni.getValueBoolean("emicad", "attiva", false));
                cheEmiCadRichiedi.setSelected(fileIni.getValueBoolean("emicad", "richiedi", true));

                texEmiCadFilePre.setText(fileIni.getValue("emicad", "file_pre", ""));
                texEmiCadFilePost.setText(fileIni.getValue("emicad", "file_post", ""));

                cheAttivaEmiCadActionPerformed(null);
                cheEmiCadRichiediActionPerformed(null);
            }

            if (!main.getPersonalContain("gaia_servizi")) {
                jTabbedPane1.remove(panPrintOption);
            }
            
            if (!main.pluginBarCode) {
                jTabbedPane1.remove(panelBarCode);
            }

            this.texHost.setText(fileIni.getValue("db", "server"));
            this.texDb.setText(fileIni.getValue("db", "nome_database"));
            this.texId.setText(fileIni.getValue("db", "user"));
            ssl.setSelected(main.fileIni.getValueBoolean("db", "ssl", false));
            ssl_truststore.setText(main.fileIni.getValue("db", "ssl_truststore"));
//            ssl_keystore.setText(main.fileIni.getValue("db", "ssl_keystore"));
            this.texPwd.setText(fileIni.getValueCifrato("db", "pwd"));
            ssh.setSelected(main.fileIni.getValueBoolean("db", "ssh", false));
            ssh_hostname.setText(main.fileIni.getValue("db", "ssh_hostname"));
            ssh_login.setText(main.fileIni.getValue("db", "ssh_login"));
            ssh_password.setText(main.fileIni.getValueCifrato("db", "ssh_password"));
            ssh_porta_remota.setText(main.fileIni.getValue("db", "ssh_porta_remota"));
            ssh_porta_locale.setText(main.fileIni.getValue("db", "ssh_porta_locale"));

            String finestre_grandi = fileIni.getValue("varie", "finestre_grandi");

            if (finestre_grandi != null && finestre_grandi.equalsIgnoreCase("si")) {
                cheApriFinestreGrandi.setSelected(true);
            } else {
                cheApriFinestreGrandi.setSelected(false);
            }

            tnxFileLogo.setText(fileIni.getValue("varie", "percorso_logo_stampe"));
            tnxFileLogo.setToolTipText(updateLogoPreview(tnxFileLogo.getText()));
            old_file_logo = tnxFileLogo.getText();

            tnxFileLogo1.setText(fileIni.getValue("varie", "percorso_logo_stampe_pdf"));
            tnxFileLogo1.setToolTipText(updateLogoPreview(tnxFileLogo1.getText()));
            old_file_logo_email = tnxFileLogo1.getText();


            if (main.getPersonalContain("gaia_servizi")) {
                this.texImgFirma.setText(fileIni.getValue("gaiaservizi", "percorso_immagine_firma"));
                texImgFirma.setToolTipText(updateLogoPreview(texImgFirma.getText()));
            }

            tnxFileSfondo.setText(fileIni.getValue("varie", "percorso_sfondo_stampe"));
            tnxFileSfondo.setToolTipText(updateLogoPreview(tnxFileSfondo.getText()));
            old_file_sfondo = tnxFileSfondo.getText();
            tnxFileSfondoPdf.setText(fileIni.getValue("varie", "percorso_sfondo_stampe_pdf"));
            tnxFileSfondoPdf.setToolTipText(updateLogoPreview(tnxFileSfondoPdf.getText()));
            old_file_sfondo_email = tnxFileSfondoPdf.getText();

            if (Db.conn != null && !Db.conn.isClosed()) {
                SwingWorker w = new SwingWorker() {
                    @Override
                    public Object construct() {
                        caricaLogo(null, logo, "logo");
                        caricaLogo(null, logo_email, "logo_email");
                        caricaLogo(null, sfondo, "sfondo");
                        caricaLogo(null, sfondo_email, "sfondo_email");
                        return null;
                    }
                };
                w.start();
            }

            String nonStampareLogo = fileIni.getValue("varie", "non_stampare_logo");

            if (nonStampareLogo != null && nonStampareLogo.equalsIgnoreCase("si")) {
                cheNonStampareLogo.setSelected(true);
            } else {
                cheNonStampareLogo.setSelected(false);
            }

            String nonStampareLogoPdf = fileIni.getValue("varie", "non_stampare_logo_pdf");
            if (nonStampareLogoPdf != null && nonStampareLogoPdf.equalsIgnoreCase("si")) {
                cheNonStampareLogo1.setSelected(true);
            } else {
                cheNonStampareLogo1.setSelected(false);
            }

            String nonStampareSfondo = fileIni.getValue("varie", "non_stampare_sfondo");
            if (nonStampareSfondo != null && nonStampareSfondo.equalsIgnoreCase("si")) {
                cheNonStampareSfondo.setSelected(true);
            } else {
                cheNonStampareSfondo.setSelected(false);
            }

            String nonStampareSfondoPdf = fileIni.getValue("varie", "non_stampare_sfondo_pdf");
            if (nonStampareSfondoPdf != null && nonStampareSfondoPdf.equalsIgnoreCase("si")) {
                cheNonStampareSfondoPdf.setSelected(true);
            } else {
                cheNonStampareSfondoPdf.setSelected(false);
            }

            if (Db.nz(fileIni.getValue("varie", "prezziCliente"), "").equalsIgnoreCase("S")) {
                this.chePrezziCliente.setSelected(true);
            } else {
                this.chePrezziCliente.setSelected(false);
            }

            if (Db.nz(fileIni.getValue("varie", "campoSerie"), "").equalsIgnoreCase("S")) {
                this.cheSerie.setSelected(true);
            } else {
                this.cheSerie.setSelected(false);
            }

            aperturaFile.setSelectedIndex(CastUtils.toInteger0(main.fileIni.getValue("varie", "apertura_file", "0")));
            aperturaText1.setText(main.fileIni.getValue("varie", "apertura_file_comando_cartella", ""));
            aperturaText2.setText(main.fileIni.getValue("varie", "apertura_file_comando_file", ""));
            aperturaText3.setText(main.fileIni.getValue("varie", "apertura_file_comando_pdf", ""));

            this.texPersonalizzazioni.setText(fileIni.getValue("personalizzazioni", "personalizzazioni"));

            //carico righe intestazione stampe e altri dati memorizzati in dati_azienda
            if (!vaisudb) {
                try {

                    ResultSet temp = Db.openResultSet("select " + main.campiDatiAzienda + " from dati_azienda");
                    temp.next();
                    this.texInte1.setText(temp.getString("intestazione_riga1"));
                    this.texInte2.setText(temp.getString("intestazione_riga2"));
                    this.texInte3.setText(temp.getString("intestazione_riga3"));
                    this.texInte4.setText(temp.getString("intestazione_riga4"));
                    this.texInte5.setText(temp.getString("intestazione_riga5"));
                    this.texInte6.setText(temp.getString("intestazione_riga6"));
                    this.texLabelCliente.setText(temp.getString("label_cliente"));
                    this.texLabelMerce.setText(temp.getString("label_destinazione"));
                    this.texLabelClienteEng.setText(temp.getString("label_cliente_eng"));
                    this.texLabelMerceEng.setText(temp.getString("label_destinazione_eng"));

                    this.comListinoBase.setSelectedItem(temp.getString("listino_base"));
//                    this.texTarga.setText(temp.getString("targa"));
                    this.comTipoLiquidazioneIva.setSelectedItem(temp.getString("tipo_liquidazione_iva"));

                    this.texNoteFatt.setText(temp.getString("testo_piede_fatt_v"));
                    this.texNoteDocu.setText(temp.getString("testo_piede_docu_v"));
                    this.texNoteOrdi.setText(temp.getString("testo_piede_ordi_v"));
//                    this.texNoteFattAcquisto.setText(temp.getString("testo_piede_fatt_a"));
                    this.texNoteDocuAcquisto.setText(temp.getString("testo_piede_docu_a"));
                    this.texNoteOrdiAcquisto.setText(temp.getString("testo_piede_ordi_a"));

                    this.cheStampaScrittaInvoicex.setSelected(temp.getInt("stampa_riga_invoicex") == 1);

                    //tipo provvigioni
                    String provvigioni_tipo_data = CastUtils.toString(temp.getString("provvigioni_tipo_data"));
                    if (provvigioni_tipo_data.equalsIgnoreCase("data_fattura")) {
                        data_fattura.setSelected(true);
                    } else {
                        data_scadenza.setSelected(true);
                    }

                    //timbro firma
                    try {
                        stampare_timbro_firma.setSelectedItem(temp.getString("stampare_timbro_firma"));
                        testo_timbro_firma.setText(temp.getString("testo_timbro_firma"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    
                    //tipo numerazione
                    try {
                        comTipoNumerazione.setSelectedIndex(temp.getInt("tipo_numerazione"));
                    } catch (Exception e) {
                    }
                    
                    try {
                        texCodIvaReadytec.setText(temp.getString("export_fatture_codice_iva"));
                        texContoRicaviReadytec.setText(temp.getString("export_fatture_conto_ricavi"));
                        estraiScadenzeFatseq.setSelected(cu.toBoolean(temp.getString("export_fatture_estrai_scadenze")));
                    } catch (Exception e) {
                    }
                } catch (Exception err) {
                }
            }

            if (Db.nz(fileIni.getValue("db", "startdbcheck"), "").equalsIgnoreCase("S")) {
                cheAvvioDb.setSelected(true);
            } else {
                oldHost = fileIni.getValue("db", "server");
                oldDb = fileIni.getValue("db", "nome_database");
                oldId = fileIni.getValue("db", "user");
                oldPwd = fileIni.getValueCifrato("db", "pwd");
                cheAvvioDb.setSelected(false);
            }

            cheAvvioDbActionPerformed(null);

            //codice iva per le spese se vuoto vengono ripartizionate
//            this.texIvaSpese.setText(Db.nz(fileIni.getValue("iva", "codiceIvaSpese"), ""));
            if (Db.conn != null && !Db.conn.isClosed()) {
                try {
                    texIvaSpese.setText((String) DbUtils.getObject(Db.getConn(true), "select codiceIvaSpese from dati_azienda limit 1"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //codice iva da presentare per default
                //            this.texIvaDefault.setText(Db.nz(fileIni.getValue("iva", "codiceIvaDefault"), ""));
                try {
                    texIvaDefault.setText((String) DbUtils.getObject(Db.getConn(true), "select codiceIvaDefault from dati_azienda limit 1"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
            }

            umpred.setText(Db.nz(fileIni.getValue("varie", "umpred"), ""));
            texMessaggioStampa.setText(fileIni.getValue("varie", "messaggioStampa"));
            int_dest_1.setText(fileIni.getValue("varie", "int_dest_1"));
            int_dest_2.setText(fileIni.getValue("varie", "int_dest_2"));

            //tema di default
            String tema = fileIni.getValue("varie", "look");

            if (tema == null || tema.length() == 0) {
                if (PlatformUtils.isMac()) {
                    tema = "Substance Creme";
                } else {
                    tema = "JGoodies Plastic XP";
                }
            }
            this.comLook.setSelectedItem(tema);

//            texCodIvaReadytec.setText(fileIni.getValue("readytec", "codiceIvaDefault", ""));
//            texContoRicaviReadytec.setText(fileIni.getValue("readytec", "codiceContoDefault", ""));

            //carico le prefereences utente
            try {

                boolean visualizzaTotali = fileIni.getValueBoolean("pref", "visualizzaTotali", true);
                boolean stampaCellulare = fileIni.getValueBoolean("pref", "stampaCellulare", false);
                boolean stampaTelefono = fileIni.getValueBoolean("pref", "stampaTelefono", false);
                boolean stampaPivaSotto = fileIni.getValueBoolean("pref", "stampaPivaSotto", false);
                boolean stampaDestDiversaSotto = fileIni.getValueBoolean("pref", "stampaDestDiversaSotto", true);
                boolean inter = fileIni.getValueBoolean("pref", "soloItaliano", true);
                boolean multiriga = fileIni.getValueBoolean("pref", "multiriga", true);
                boolean stampaPdf = fileIni.getValueBoolean("pref", "stampaPdf", true);
                boolean inclNumProForma = fileIni.getValueBoolean("pref", "inclNumProForma", true);  // dm-edit-20081215
                boolean azioniPericolose = fileIni.getValueBoolean("pref", "azioniPericolose", true);
                boolean numerazioneNoteCredito = fileIni.getValueBoolean("pref", "numerazioneNoteCredito", true);
                boolean visuAnno = fileIni.getValueBoolean("pref", "visualizzaAnnoInCorso", false);
                boolean parArrotondamento = fileIni.getValueBoolean("pref", "attivaArrotondamento", false);
                String noteStandard = fileIni.getValue("pref", "noteStandard");

                cheTotali.setSelected(visualizzaTotali);
                cheStampaCellulare.setSelected(stampaCellulare);
                cheStampaTelefono.setSelected(stampaTelefono);
                cheStampaPivaSotto.setSelected(stampaPivaSotto);
                cheStampaDestDiversaSotto.setSelected(stampaDestDiversaSotto);
                cheStampaPdf.setSelected(stampaPdf);
                cheInclNumProForma.setSelected(inclNumProForma); // dm-edit-20081215
                cheAzioniPericolose.setSelected(azioniPericolose);
                cheNumerazioneNoteCredito.setSelected(numerazioneNoteCredito);
                cheVisuAnno.setSelected(visuAnno);
                cheArrotondamento.setSelected(parArrotondamento);
                texNoteStandard.setText(noteStandard);

//                String limit = preferences.get("limit", "50");
                String limit = fileIni.getValue("pref", "limit", "50");

                this.texLimit.setText(limit);
                this.cheSoloItaliano.setSelected(inter);
//                this.cheMultiriga.setSelected(multiriga);
//                comTipoStampa1.setSelectedItem(preferences.get("tipoStampa", null));
                comTipoStampa1.setSelectedItem(fileIni.getValue("pref", "tipoStampa", null));

//                comTipoStampa.setSelectedItem(preferences.get("tipoStampaFA", null));
                comTipoStampa.setSelectedItem(fileIni.getValue("pref", "tipoStampaFA", null));

//                comTipoStampaDdt.setSelectedItem(preferences.get("tipoStampaDDT", null));
                comTipoStampaDdt.setSelectedItem(fileIni.getValue("pref", "tipoStampaDDT", null));

//                comTipoStampaOrdine.setSelectedItem(preferences.get("tipoStampaOrdine", null));
                comTipoStampaOrdine.setSelectedItem(fileIni.getValue("pref", "tipoStampaOrdine", null));

                cheStampaCedoliniBonifici.setSelected(fileIni.getValueBoolean("pref", "stampaCedoliniBonifici", false));

                generazione_movimenti.setSelectedIndex(Integer.parseInt(fileIni.getValue("pref", "generazione_movimenti", "0")));

                boolean perQuantita = fileIni.getValueBoolean("barcode", "per_quantita", false);
                int posizione = perQuantita ? 1 : 0;
                comStampaBarcode.setSelectedIndex(posizione);
                texFreeBarcode.setText(main.fileIni.getValue("barcode", "testo_libero", ""));

                boolean disposizione = fileIni.getValueBoolean("barcode", "articolo_sopra", true);
                posizione = disposizione ? 0 : 1;
                comBarcodeDisposizione.setSelectedIndex(posizione);

                boolean prezzoIva = fileIni.getValueBoolean("barcode", "iva_inclusa", true);
                posizione = prezzoIva ? 0 : 1;
                comBarcodeTipoPrezzo.setSelectedIndex(posizione);

                cheBarcodeCodArticolo.setSelected(main.fileIni.getValueBoolean("barcode", "stampa_cod_articolo", true));
                cheBarcodePrezzoArticolo.setSelected(main.fileIni.getValueBoolean("barcode", "stampa_prezzo_articolo", false));
                cheBarcodeQtaArticolo.setSelected(main.fileIni.getValueBoolean("barcode", "stampa_qta_articolo", false));
                cheBarcodeDrawtext.setSelected(main.fileIni.getValueBoolean("barcode", "draw_text", true));

                if (perQuantita) {
                    cheBarcodeQtaArticolo.setSelected(false);
                    cheBarcodeQtaArticolo.setEnabled(false);
                }

                comBarcodeTipoPrezzo.setEnabled(cheBarcodePrezzoArticolo.isSelected());

                comBarcodeColonne.setSelectedItem(main.fileIni.getValue("barcode", "colonne", "4"));
                comBarcodeRighe.setSelectedItem(main.fileIni.getValue("barcode", "righe", "10"));
                comBarcodeFormato.setSelectedItem(main.fileIni.getValue("barcode", "formato", "Code39"));
                mtop.setText(main.fileIni.getValue("barcode", "mtop", "20"));
                mbottom.setText(main.fileIni.getValue("barcode", "mbottom", "20"));
                mright.setText(main.fileIni.getValue("barcode", "mright", "20"));
                mleft.setText(main.fileIni.getValue("barcode", "mleft", "20"));
                plarghezza.setText(main.fileIni.getValue("barcode", "pagina_larghezza", "595"));
                paltezza.setText(main.fileIni.getValue("barcode", "pagina_altezza", "842"));

                raggruppa_articoli.setSelectedIndex(Integer.parseInt(fileIni.getValue("pref", "raggruppa_articoli", "2")));
                riportaSerie.setSelectedIndex(Integer.parseInt(fileIni.getValue("pref", "riporta_serie", "0")));


                if (Db.conn != null && !Db.conn.isClosed()) {
                    vostro_ordine.setSelectedIndex(Integer.parseInt(fileIni.getValue("pref", "stato_vs_ordine", "0")));
                    stato_ordi_post.dbAddElement("<lascia invariato>", "<lascia invariato>");
                    stato_ordi_post.dbOpenList(Db.getConn(), "SELECT descrizione, id FROM stati_preventivo_ordine");
                    stato_ordi_post.setSelectedItem(fileIni.getValue("pref", "stato_ordi", "<lascia invariato>"));
                }

                cheScadenzeOrdini.setSelected(fileIni.getValueBoolean("pref", "scadenzeOrdini", false));
                cheRichiediPassword.setSelected(fileIni.getValueBoolean("pref", "richiediPassword", false));
                cheControlliIva.setSelected(fileIni.getValueBoolean("pref", "controlliIva", true));
                chePersona.setSelected(fileIni.getValueBoolean("pref", "ricercaPerPersona", false));
                cheUpdateListini.setSelected(fileIni.getValueBoolean("pref", "updateListini", false));
                cheControlloNumeriAvvio.setSelected(fileIni.getValueBoolean("pref", "controlloNumeriAvvio", true));

                listino_consigliato.dbAddElement("", "");
                if (Db.conn != null && !Db.conn.isClosed()) {
                    listino_consigliato.dbOpenList(Db.getConn(true), "select CONCAT(descrizione, ' [', codice, ']'), codice from tipi_listino order by descrizione");
                    listino_consigliato.dbTrovaKey(main.fileIni.getValue("pref", "listinoConsigliatoDdt", ""));
                }

                if (!fileIni.existKey("altro", "da_ddt")) {
                    da_ddt.setText("Da DDT");
                } else {
                    da_ddt.setText(fileIni.getValue("altro", "da_ddt", "Da DDT"));
                }
                if (!fileIni.existKey("altro", "da_ordine")) {
                    da_ordine.setText("Da Ordine");
                } else {
                    da_ordine.setText(fileIni.getValue("altro", "da_ordine", "Da Ordine"));
                }
            } catch (Exception err) {
                err.printStackTrace();
            }

            if (vaisudb) {
                jTabbedPane1.setSelectedIndex(1);
            }

            try {
                InvoicexEvent event = new InvoicexEvent(this);
                event.type = InvoicexEvent.TYPE_IMPOSTAZIONI_CONSTR_POST_INIT_COMPS;
                main.events.fireInvoicexEvent(event);
            } catch (Exception err) {
                err.printStackTrace();
            }

            updateTabellaSoglieProvvigioni(true);

            try {
                String font_family = fileIni.getValue("pref", "font_family", main.def_font.getFamily());
                Integer font_size = CastUtils.toInteger0(fileIni.getValue("pref", "font_size", CastUtils.toString(main.def_font.getSize())));
                font = new Font(font_family, Font.PLAIN, font_size);
                setPulsanteFont(font);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            aggiornaAperturaFile();

            if (Db.conn != null && !Db.conn.isClosed()) {
                if (!main.utente.getPermesso(Permesso.PERMESSO_IMPOSTAZIONI, Permesso.PERMESSO_TIPO_SCRITTURA)) {
                    InvoicexUtil.deactivateComponent(jTabbedPane1);
                    InvoicexUtil.deactivateComponent(this.jButton1);
                    InvoicexUtil.deactivateComponent(this.jButton2);
                }
                cheAttivaUtenti.setEnabled(main.utente.getPermesso(Permesso.PERMESSO_GESTIONE_UTENTI, Permesso.PERMESSO_TIPO_SCRITTURA));
            }

            opening = false;
            pack();
            InvoicexUtil.checkSize(this);
            setVisible(true);
        } catch (Exception err) {
            err.printStackTrace();
        }
//        pack();

    }

    public void updateTabellaSoglieProvvigioni(boolean first) {
        try {
            if (Db.conn == null || Db.conn.isClosed()) {
                //in caso di impostazini perchè non si aggancia al db
                return;
            }
        } catch (Exception e) {
        }

        iniFileProp fileIni = main.fileIni;
        if (first) {
            cheProvvigioniAuto.setSelected(fileIni.getValueBoolean("pref", "provvigioniAutomatiche", false));
            if (cheProvvigioniAuto.isSelected()) {
                chePercStandard.setSelected(fileIni.getValueBoolean("pref", "provvigioniPercentualeAuto", false));
            } else {
                chePercStandard.setEnabled(false);
            }
        }

        String sqlSoglie = "SELECT soglia as Soglia, ";
        sqlSoglie += "sconto_soglia as Sconto ";
        if (chePercStandard.isSelected()) {
            sqlSoglie += ", percentuale as Provvigione ";
        }
        sqlSoglie += "FROM soglie_provvigioni ";
        sqlSoglie += "ORDER BY soglia";

        java.util.Hashtable colsWidthPerc = new java.util.Hashtable();
        if (chePercStandard.isSelected()) {
            colsWidthPerc.put("Soglia", new Double(30));
            colsWidthPerc.put("Sconto", new Double(30));
            colsWidthPerc.put("Provvigione", new Double(40));
        } else {
            colsWidthPerc.put("Soglia", new Double(50));
            colsWidthPerc.put("Sconto", new Double(50));
        }

        griglia_soglie.columnsSizePerc = colsWidthPerc;

        try {
            if (Db.conn != null && !Db.conn.isClosed()) {
                griglia_soglie.dbOpen(Db.getConn(true), sqlSoglie, Db.INSTANCE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        griglia_soglie.setEditable(cheProvvigioniAuto.isSelected());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popuprimuovi = new javax.swing.JPopupMenu();
        rimuovi = new javax.swing.JMenuItem();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        cheStampaDestDiversaSotto = new javax.swing.JCheckBox();
        chePrezziCliente = new javax.swing.JCheckBox();
        cheSerie = new javax.swing.JCheckBox();
        cheSoloItaliano = new javax.swing.JCheckBox();
        cheTotali = new javax.swing.JCheckBox();
        cheStampaCellulare = new javax.swing.JCheckBox();
        cheApriFinestreGrandi = new javax.swing.JCheckBox();
        cheNumerazioneNoteCredito = new javax.swing.JCheckBox();
        cheStampaCedoliniBonifici = new javax.swing.JCheckBox();
        cheInclNumProForma = new javax.swing.JCheckBox();
        cheScadenzeOrdini = new javax.swing.JCheckBox();
        cheControlliIva = new javax.swing.JCheckBox();
        chePersona = new javax.swing.JCheckBox();
        cheStampaTelefono = new javax.swing.JCheckBox();
        cheUpdateListini = new javax.swing.JCheckBox();
        cheArrotondamento = new javax.swing.JCheckBox();
        cheStampaScrittaInvoicex = new javax.swing.JCheckBox();
        cheStampaPivaSotto = new javax.swing.JCheckBox();
        cheAttivaUtenti = new javax.swing.JCheckBox();
        cheControlloNumeriAvvio = new javax.swing.JCheckBox();
        jPanel6 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        texIvaDefault = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        texIvaSpese = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        texLimit = new javax.swing.JTextField();
        comTipoLiquidazioneIva = new javax.swing.JComboBox();
        comListinoBase = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        comLook = new javax.swing.JComboBox();
        jLabel21 = new javax.swing.JLabel();
        generazione_movimenti = new WideComboBox();
        cheVisuAnno = new javax.swing.JCheckBox();
        jLabel22 = new javax.swing.JLabel();
        raggruppa_articoli = new WideComboBox();
        jLabel23 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        umpred = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        listino_consigliato = new tnxbeans.tnxComboField();
        pulsanteFont = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        riportaSerie = new javax.swing.JComboBox();
        jLabel26 = new javax.swing.JLabel();
        vostro_ordine = new javax.swing.JComboBox();
        jLabel32 = new javax.swing.JLabel();
        stato_ordi_post = new tnxbeans.tnxComboField();
        jLabel58 = new javax.swing.JLabel();
        comTipoNumerazione = new javax.swing.JComboBox();
        jPanel11 = new javax.swing.JPanel();
        jLabel111 = new javax.swing.JLabel();
        logo = new javax.swing.JLabel();
        tnxFileLogo = new javax.swing.JTextField();
        comFile = new javax.swing.JButton();
        cheNonStampareLogo = new javax.swing.JCheckBox();
        jLabel115 = new javax.swing.JLabel();
        jLabel116 = new javax.swing.JLabel();
        logo_email = new javax.swing.JLabel();
        tnxFileLogo1 = new javax.swing.JTextField();
        comFile1 = new javax.swing.JButton();
        cheNonStampareLogo1 = new javax.swing.JCheckBox();
        jButton5 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        texInte1 = new javax.swing.JTextField();
        texInte2 = new javax.swing.JTextField();
        texInte3 = new javax.swing.JTextField();
        texInte4 = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        texInte5 = new javax.swing.JTextField();
        texInte6 = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        texLabelCliente = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        texLabelMerce = new javax.swing.JTextField();
        texLabelClienteEng = new javax.swing.JTextField();
        texLabelMerceEng = new javax.swing.JTextField();
        jLabel54 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        labHost = new javax.swing.JLabel();
        texHost = new javax.swing.JTextField();
        labDb = new javax.swing.JLabel();
        texDb = new javax.swing.JTextField();
        labId = new javax.swing.JLabel();
        labPwd = new javax.swing.JLabel();
        texPwd = new javax.swing.JPasswordField();
        cheAvvioDb = new javax.swing.JCheckBox();
        jLabel14 = new javax.swing.JLabel();
        texPersonalizzazioni = new javax.swing.JTextField();
        cheAzioniPericolose = new javax.swing.JCheckBox();
        cheRichiediPassword = new javax.swing.JCheckBox();
        texId = new javax.swing.JTextField();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel10 = new javax.swing.JPanel();
        ssh = new javax.swing.JCheckBox();
        jLabel34 = new javax.swing.JLabel();
        ssh_hostname = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        ssh_login = new javax.swing.JTextField();
        ssh_porta_locale = new javax.swing.JTextField();
        ssh_porta_remota = new javax.swing.JTextField();
        ssh_password = new javax.swing.JPasswordField();
        jPanel8 = new javax.swing.JPanel();
        labDb1 = new javax.swing.JLabel();
        ssl_truststore = new javax.swing.JTextField();
        ssl = new javax.swing.JCheckBox();
        select_truststore = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        comTipoStampa1 = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        comTipoStampa = new javax.swing.JComboBox();
        jLabel19 = new javax.swing.JLabel();
        comTipoStampaDdt = new javax.swing.JComboBox();
        cheStampaPdf = new javax.swing.JCheckBox();
        comTipoStampaOrdine = new javax.swing.JComboBox();
        jLabel20 = new javax.swing.JLabel();
        btnPreview = new javax.swing.JButton();
        btnPreview1 = new javax.swing.JButton();
        btnPreview2 = new javax.swing.JButton();
        btnPreview3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        texNoteStandard = new javax.swing.JTextArea();
        jLabel17 = new javax.swing.JLabel();
        texMessaggioStampa = new javax.swing.JTextField();
        jLabel113 = new javax.swing.JLabel();
        tnxFileSfondo = new javax.swing.JTextField();
        tnxFileSfondoPdf = new javax.swing.JTextField();
        cheNonStampareSfondo = new javax.swing.JCheckBox();
        cheNonStampareSfondoPdf = new javax.swing.JCheckBox();
        jLabel24 = new javax.swing.JLabel();
        da_ddt = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        da_ordine = new javax.swing.JTextField();
        jLabel117 = new javax.swing.JLabel();
        sfondo = new javax.swing.JLabel();
        comFileSfondo = new javax.swing.JButton();
        jLabel118 = new javax.swing.JLabel();
        sfondo_email = new javax.swing.JLabel();
        comFileSfondoEmail = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        aperturaFile = new javax.swing.JComboBox();
        aperturaImposta = new javax.swing.JButton();
        aperturaLabel1 = new javax.swing.JLabel();
        aperturaText1 = new javax.swing.JTextField();
        aperturaLabel2 = new javax.swing.JLabel();
        aperturaText2 = new javax.swing.JTextField();
        aperturaLabel3 = new javax.swing.JLabel();
        aperturaText3 = new javax.swing.JTextField();
        int_dest_1 = new javax.swing.JTextField();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        int_dest_2 = new javax.swing.JTextField();
        jLabel56 = new javax.swing.JLabel();
        stampare_timbro_firma = new javax.swing.JComboBox();
        jScrollPane10 = new javax.swing.JScrollPane();
        testo_timbro_firma = new javax.swing.JTextArea();
        jSeparator4 = new javax.swing.JSeparator();
        jPanel9 = new javax.swing.JPanel();
        cheProvvigioniAuto = new javax.swing.JCheckBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        griglia_soglie = new tnxbeans.tnxDbGrid2();
        chePercStandard = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jLabel31 = new javax.swing.JLabel();
        data_scadenza = new javax.swing.JRadioButton();
        data_fattura = new javax.swing.JRadioButton();
        jSeparator3 = new javax.swing.JSeparator();
        panEmiCad = new javax.swing.JPanel();
        cheAttivaEmiCad = new javax.swing.JCheckBox();
        cheEmiCadRichiedi = new javax.swing.JCheckBox();
        texEmiCadFilePre = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        texEmiCadFilePost = new javax.swing.JTextField();
        butEmiCadFilePre = new javax.swing.JButton();
        butEmiCadFilePost = new javax.swing.JButton();
        panPrintOption = new javax.swing.JPanel();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        jPanel12 = new javax.swing.JPanel();
        jLabel40 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        texNoteFatt = new javax.swing.JTextArea();
        jLabel41 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        texNoteDocu = new javax.swing.JTextArea();
        jLabel42 = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        texNoteOrdi = new javax.swing.JTextArea();
        jPanel13 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        texNoteDocuAcquisto = new javax.swing.JTextArea();
        jLabel28 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        texNoteOrdiAcquisto = new javax.swing.JTextArea();
        labStampaFirma = new javax.swing.JLabel();
        texImgFirma = new javax.swing.JTextField();
        butImgFirma = new javax.swing.JButton();
        panelBarCode = new javax.swing.JPanel();
        jLabel46 = new javax.swing.JLabel();
        comStampaBarcode = new javax.swing.JComboBox();
        jLabel47 = new javax.swing.JLabel();
        texFreeBarcode = new javax.swing.JTextField();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        cheBarcodeCodArticolo = new javax.swing.JCheckBox();
        cheBarcodePrezzoArticolo = new javax.swing.JCheckBox();
        cheBarcodeQtaArticolo = new javax.swing.JCheckBox();
        comBarcodeDisposizione = new javax.swing.JComboBox();
        jLabel50 = new javax.swing.JLabel();
        comBarcodeTipoPrezzo = new javax.swing.JComboBox();
        jLabel51 = new javax.swing.JLabel();
        comBarcodeColonne = new javax.swing.JComboBox();
        jLabel52 = new javax.swing.JLabel();
        comBarcodeRighe = new javax.swing.JComboBox();
        jLabel53 = new javax.swing.JLabel();
        comBarcodeFormato = new javax.swing.JComboBox();
        cheBarcodeDrawtext = new javax.swing.JCheckBox();
        jLabel57 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        mtop = new javax.swing.JTextField();
        mright = new javax.swing.JTextField();
        mleft = new javax.swing.JTextField();
        mbottom = new javax.swing.JTextField();
        jLabel59 = new javax.swing.JLabel();
        plarghezza = new javax.swing.JTextField();
        jLabel60 = new javax.swing.JLabel();
        paltezza = new javax.swing.JTextField();
        jPanel14 = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        texContoRicaviReadytec = new javax.swing.JTextField();
        texCodIvaReadytec = new javax.swing.JTextField();
        estraiScadenzeFatseq = new javax.swing.JCheckBox();
        jLabel61 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        rimuovi.setText("Azzera");
        rimuovi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rimuoviActionPerformed(evt);
            }
        });
        popuprimuovi.add(rimuovi);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Impostazioni");
        setMinimumSize(new java.awt.Dimension(200, 200));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        jScrollPane3.setBorder(null);
        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        cheStampaDestDiversaSotto.setText("Destinazione Diversa sotto Intest. Cliente");
        cheStampaDestDiversaSotto.setToolTipText("Inverti ordine fra box Cliente e Destinazione Merce");
        cheStampaDestDiversaSotto.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        cheStampaDestDiversaSotto.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        chePrezziCliente.setText("Gestione prezzi per Cliente");
        chePrezziCliente.setToolTipText("Se selezionata il programma genera un listino per ogni cliente.");
        chePrezziCliente.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        chePrezziCliente.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        cheSerie.setText("Gestione campo Serie");
        cheSerie.setToolTipText("Attiva l'utilizzo del campo serie");
        cheSerie.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        cheSerie.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        cheSerie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cheSerieActionPerformed(evt);
            }
        });

        cheSoloItaliano.setText("Utilizza solo Italiano");
        cheSoloItaliano.setToolTipText("<html>Permette la gestione di report di stampa in inglese per le fatture da emettere verso l'estero</html>");
        cheSoloItaliano.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        cheSoloItaliano.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        //cheSoloItaliano.setVisible(false);

        cheTotali.setText("Visualizza il totale documenti in elenco");
        cheTotali.setToolTipText("Visualizza a fondo elenco il totale dei documenti in lista");
        cheTotali.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        cheTotali.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        cheStampaCellulare.setText("Stampa Cellulare su documenti");
        cheStampaCellulare.setToolTipText("Permette di stampare il numero di cellulare dei clienti nei documenti");
        cheStampaCellulare.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        cheStampaCellulare.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        cheApriFinestreGrandi.setText("Apri finestre a tutto schermo");
        cheApriFinestreGrandi.setToolTipText("Apre automaticamente tutte le finestre con dimensione a tutto schermo");
        cheApriFinestreGrandi.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        cheApriFinestreGrandi.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        cheNumerazioneNoteCredito.setText("Numerazione diversa per Note di Credito");
        cheNumerazioneNoteCredito.setToolTipText("Selezionando questa casella vengono numerate le note di credito con una loro numerazione indipendente dalle Fatture");
        cheNumerazioneNoteCredito.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        cheNumerazioneNoteCredito.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        cheStampaCedoliniBonifici.setText("Stampa cedolini bonifici");
        cheStampaCedoliniBonifici.setToolTipText("Stampa automatica di cedolini bonifici dopo stampa delle fatture (quando richiesto dal tipo di pagamento)");
        cheStampaCedoliniBonifici.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        cheStampaCedoliniBonifici.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        cheInclNumProForma.setText("Includi numerazione in stampa pro-forma");
        cheInclNumProForma.setToolTipText("Se disattivato, verrà nascosto il numero del pro-forma stampato");
        cheInclNumProForma.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        cheInclNumProForma.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        cheInclNumProForma.setPreferredSize(new java.awt.Dimension(167, 23));

        cheScadenzeOrdini.setText("Generazione Scadenze su Ordini/Prev. di Vendita");
        cheScadenzeOrdini.setToolTipText("Attiva la generazione delle scadenze su inserimento di preventivi e ordini");
        cheScadenzeOrdini.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        cheScadenzeOrdini.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        cheScadenzeOrdini.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cheScadenzeOrdiniActionPerformed(evt);
            }
        });

        cheControlliIva.setText("Controlli IVA automatici");
        cheControlliIva.setToolTipText("Disattivando questi controlli il programma non controllerà il paese del Cliente per impostare il giusto codice IVA");
        cheControlliIva.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        cheControlliIva.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        cheControlliIva.setPreferredSize(new java.awt.Dimension(167, 23));
        cheControlliIva.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cheControlliIvaActionPerformed(evt);
            }
        });

        chePersona.setText("Ricerca per Persona di riferimento");
        chePersona.setToolTipText("Abilita la ricerca per persona di riferimento all'interno dell'elenco fatture e situazione clienti");
        chePersona.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        chePersona.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        chePersona.setPreferredSize(new java.awt.Dimension(167, 23));

        cheStampaTelefono.setText("Stampa Telefono su documenti");
        cheStampaTelefono.setToolTipText("Permette di stampare il telefono dei clienti nei documenti");
        cheStampaTelefono.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        cheStampaTelefono.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        cheUpdateListini.setText("Aggiornamento automatico prezzi listini");
        cheUpdateListini.setToolTipText("<html>Attiva la possibilità di modificare i prezzi degli articoli in base<br>al prezzo inserito nel documento</html>");
        cheUpdateListini.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        cheUpdateListini.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        cheArrotondamento.setText("Attiva Parametro Arrotondamento");
        cheArrotondamento.setToolTipText("Attiva l'arrotondamento parametrizzato in fase di inserimento righe");
        cheArrotondamento.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        cheArrotondamento.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        cheStampaScrittaInvoicex.setText("Visualizza sponsor su report");
        cheStampaScrittaInvoicex.setToolTipText("Toglie la riga di sponsorizzazione di Invoicex all'interno dei report");
        cheStampaScrittaInvoicex.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        cheStampaScrittaInvoicex.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        cheStampaPivaSotto.setText("Stampa P.Iva/Cod.Fisc sotto intestazione");
        cheStampaPivaSotto.setToolTipText("Per invio documenti in buste con finestra selezionare per stampare fuori dalla finestra");
        cheStampaPivaSotto.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        cheStampaPivaSotto.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        cheAttivaUtenti.setFont(cheAttivaUtenti.getFont().deriveFont(cheAttivaUtenti.getFont().getStyle() | java.awt.Font.BOLD));
        cheAttivaUtenti.setText("Attiva gestione Utenti");
        cheAttivaUtenti.setToolTipText("Con questa opzione viene attivata la gestione multi utente di Invoicex, ad ogni accesso sarà richiesto utente e password per accedere");
        cheAttivaUtenti.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        cheAttivaUtenti.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        cheAttivaUtenti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cheAttivaUtentiActionPerformed(evt);
            }
        });

        cheControlloNumeriAvvio.setText("Controllo numerazione all'avvio");
        cheControlloNumeriAvvio.setToolTipText("Disabilita o abilita il controllo della numerazione dei documenti all'avvi odel programma");
        cheControlloNumeriAvvio.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        cheControlloNumeriAvvio.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(cheControlloNumeriAvvio, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 238, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(cheSerie, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(cheStampaDestDiversaSotto, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(cheTotali, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(cheNumerazioneNoteCredito, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                .add(cheAttivaUtenti)
                                .add(chePersona, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(cheScadenzeOrdini, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 300, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(cheArrotondamento, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 268, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(cheUpdateListini, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, cheSoloItaliano)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, chePrezziCliente)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, cheStampaCedoliniBonifici)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, cheApriFinestreGrandi)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, cheInclNumProForma, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 250, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(cheStampaPivaSotto, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(cheStampaTelefono, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 270, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(cheStampaCellulare, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 270, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                .add(cheStampaScrittaInvoicex, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 238, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(cheControlliIva, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4Layout.linkSize(new java.awt.Component[] {cheArrotondamento, cheAttivaUtenti, cheNumerazioneNoteCredito, chePersona, cheScadenzeOrdini, cheSerie, cheStampaDestDiversaSotto, cheTotali, cheUpdateListini}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel4Layout.linkSize(new java.awt.Component[] {cheApriFinestreGrandi, cheControlliIva, cheInclNumProForma, chePrezziCliente, cheSoloItaliano, cheStampaCedoliniBonifici, cheStampaCellulare, cheStampaPivaSotto, cheStampaScrittaInvoicex, cheStampaTelefono}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(cheStampaPivaSotto)
                        .add(1, 1, 1)
                        .add(cheStampaTelefono)
                        .add(1, 1, 1)
                        .add(cheStampaCellulare)
                        .add(1, 1, 1)
                        .add(chePrezziCliente)
                        .add(1, 1, 1)
                        .add(cheSoloItaliano)
                        .add(1, 1, 1)
                        .add(cheApriFinestreGrandi)
                        .add(1, 1, 1)
                        .add(cheStampaCedoliniBonifici)
                        .add(1, 1, 1)
                        .add(cheInclNumProForma, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(1, 1, 1)
                        .add(cheControlliIva, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(cheStampaDestDiversaSotto)
                        .add(1, 1, 1)
                        .add(cheSerie)
                        .add(1, 1, 1)
                        .add(cheTotali)
                        .add(1, 1, 1)
                        .add(cheNumerazioneNoteCredito)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel4Layout.createSequentialGroup()
                                .add(96, 96, 96)
                                .add(cheAttivaUtenti))
                            .add(jPanel4Layout.createSequentialGroup()
                                .add(cheScadenzeOrdini)
                                .add(1, 1, 1)
                                .add(chePersona, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(1, 1, 1)
                                .add(cheUpdateListini)
                                .add(1, 1, 1)
                                .add(cheArrotondamento)))))
                .add(1, 1, 1)
                .add(cheStampaScrittaInvoicex)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cheControlloNumeriAvvio)
                .addContainerGap())
        );

        jPanel4Layout.linkSize(new java.awt.Component[] {cheApriFinestreGrandi, cheControlliIva, cheInclNumProForma, chePrezziCliente, cheSoloItaliano, cheStampaCedoliniBonifici, cheStampaCellulare, cheStampaPivaSotto, cheStampaScrittaInvoicex, cheStampaTelefono}, org.jdesktop.layout.GroupLayout.VERTICAL);

        jPanel4Layout.linkSize(new java.awt.Component[] {cheArrotondamento, cheNumerazioneNoteCredito, chePersona, cheScadenzeOrdini, cheSerie, cheStampaDestDiversaSotto, cheTotali, cheUpdateListini}, org.jdesktop.layout.GroupLayout.VERTICAL);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Codice IVA standard");
        jLabel8.setToolTipText("Codice IVA automatico per righe fatture");

        texIvaDefault.setColumns(5);
        texIvaDefault.setToolTipText("Codice IVA automatico per righe fatture");

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText("Tipo Liquidazione Iva");
        jLabel15.setToolTipText("<html>\nTipo di liquidazione IVA impostata in automatico nella stampa del registro iva<br>\nraggiungibile da Gestione IVA -> Stampa registro IVA\n</html>");

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Codice IVA spese");
        jLabel2.setToolTipText("<html>\nCodice iva automatico per spese di trasporto e di incasso<br><br>\n<b>Se lasciato vuoto l'IVA sulle spese verrà calcolata in base<br>alla ripartizione dei codici IVA inseriti in fattura</b>\n</html>");

        texIvaSpese.setColumns(5);

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("Listino Base");
        jLabel12.setToolTipText("Listino prezzi selezionato di default");

        texLimit.setColumns(3);
        texLimit.setFont(texLimit.getFont().deriveFont(texLimit.getFont().getSize()-1f));

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Tema");
        jLabel9.setToolTipText("<html>Temi grafici per determinare colori di sfondo,<br>stili dei pulsanti e delle barre delle maschere</html>");

        comLook.setFont(comLook.getFont().deriveFont(comLook.getFont().getSize()-1f));
        comLook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comLookActionPerformed(evt);
            }
        });

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel21.setText("Genera movimenti su Fatture Immediate");
        jLabel21.setToolTipText("<html>\nI movimenti di magazzino vengono generati automaticamente su DDT e su <br>\nfatture accompagnatorie. Con questa opzione puoi scegliere quando generare sulle<br>\nfatture immediate:<br><br>\n\n<b>Sempre</b><br>\nLe movimentazioni di magazzino vengono generate ad ogni nuova emissione di fattura.<br>\nUsare questa opzione se le vostre fatture immediate non vegono mai generate a partire da<br>\nun DDT.\n<br>\n<br>\n<b>Mai</b><br>\nLe movimentazioni di magazzino non vengono mai generate per le fatture immediate<br>\nUsare questa opzione se le vostre fatture immediate derivano sempre da DDT.\n<br>\n<br>\n<b>Chiedi</b><br>\nLe movimentazioni di magazzino vengono generate se il cliente chiede di generarle dopo<br>\nl'inserimento della fattura. Usare questa opzione se avete sia fatture immediate create<br>\na partire da DDT che altre inserite manualmente o prevenienti da ordini.\n</html>"); // NOI18N

        generazione_movimenti.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Sempre", "Mai", "Chiedi per ogni fattura" }));

        cheVisuAnno.setFont(cheVisuAnno.getFont().deriveFont(cheVisuAnno.getFont().getSize()-1f));
        cheVisuAnno.setText("solo anno in corso");
        cheVisuAnno.setToolTipText("Includi nella visualizzazione solo i documenti dell'anno in corso");
        cheVisuAnno.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        cheVisuAnno.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel22.setText("Raggruppa articoli in conversione documenti");
        jLabel22.setToolTipText("<html>\nImpostazioni per la gestione del raggruppamento delle righe in fase di conversione di<br>\ndocumenti multipli<br><br>\n<b>NO</b><br>\nLe righe non vengono raggruppate, e ogni articolo viene segnalato nel documento generato<br>\n<br>\n<br>\n<b>Raggruppa Sempre</b><br>\nGli articoli presenti nei documenti da convertire saranno raggruppati in righe per codice articolo<br>\n<br>\n<br>\n<b>Chiedi sempre (suggerendo dal Cliente)</b><br>\nScegli ad ogni conversione se raggruppare gli articoli o meno (valorizzato automaticamente con<br>\nil valore dell'opzione \"Raggruppa DDT\" su cliente<br>\n<br>\n<br>\n<b>Raggruppa in base a impostazioni Cliente</b><br>\nRaggruppamento determinato dall'opzione \"Raggruppa DDT\" in anagrafica cliente<br>\n<br>\n<br>\n<b>Genera solamente riga riepilogativa</b><br>\nViene generata una riga riepilogativa per ogni singolo DDT, senza includere gli articoli all'interno<br>\ndel documento appena generato<br>\n</html>"); // NOI18N

        raggruppa_articoli.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "No", "Raggruppa sempre", "Chiedi sempre (suggerendo dal Cliente)", "Raggruppa in base a impostazioni Cliente", "Genera solamente riga riepilogativa" }));

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel23.setText("   um predefinita");
        jLabel23.setToolTipText("Unità di Misura richiamata automaticamente");

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("n° doc. vis.");
        jLabel7.setToolTipText("Numero di documenti da visualizzare in automatico su ogni maschera di riepilogo");

        umpred.setColumns(5);
        umpred.setToolTipText("<html>se non viene specificato le spese (di trasporto e di incasso) vengono ripartite in base alle aliquote presenti in fattura</html>");

        jLabel39.setText("List. Consigliato");
        jLabel39.setToolTipText("Listino prezzi da indicare come prezzo di vendita consigliato");

        listino_consigliato.setDbNomeCampo("listino_consigliato");

        pulsanteFont.setFont(pulsanteFont.getFont().deriveFont(pulsanteFont.getFont().getSize()-1f));
        pulsanteFont.setText("font");
        pulsanteFont.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pulsanteFontActionPerformed(evt);
            }
        });

        jLabel5.setText("Riporta serie in conversione");
        jLabel5.setToolTipText("<html>\n<b>Sempre:</b> con questa opzione la serie verrà sempre riportata nei documenti convertiti.<br>\nA partire da un DDT o da un Preventivo/Ordine con serie \"A\" verrà cioè creata sempre una fattura con serie \"A\".\n<br><br>\n<b>Mai:</b> I documenti verranno convertiti senza tenere conto della serie del documento di partenza.\n<br><br>\n<b>Chiedi:</b> Ad ogni conversione verrà chiesto se riportare la serie oppure se lasciare vuoto il campo.\n</html>");

        riportaSerie.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Chiedi", "Riporta Sempre", "Non Riportare", "Richiedi Serie di Conversione" }));

        jLabel26.setText("Etichetta Vs. Ordine in conversione");
        jLabel26.setToolTipText("<html>\n<b>Riporta da documento:</b> Nel nuovo documento verrà riportata la voce \"Vs. Riferimento\" presente nel documento di partenza.\n<br /><br />\n<b>Riporta Numero:</b> Nel campo \"Vs. Riferimento\" nel documento generato verrà riportato il documento del documento di partenza.\n<br /><br />\n<b>Entrambi:</b> Nel campo \"Vs. Riferimento\" del documento generato verranno riportati sia il campo \"Vs. Riferimento\"<br>del documento di partenza che il numero del documento di partenza stesso.\n</html>");

        vostro_ordine.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Riporta Da Documento", "Riporta Numero", "Entrambi" }));

        jLabel32.setText("Stato Preventivo Dopo Conversione");
        jLabel32.setToolTipText("<html>Scegli in quale stato riportare il preventivo dopo la conversione a Fattura o DDT</html>");

        stato_ordi_post.setEditable(false);
        stato_ordi_post.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel58.setText("Tipo numerazione Fatture");
        jLabel58.setToolTipText("Seleziona il tipo di numerazione delle Fatture in base al DL 216/2012\nValido per le fatture dal 1/1/2013");

        comTipoNumerazione.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Riparti da 1 al cambio di anno (stampa numero/anno con quattro cifre)", "Riparti da 1 al cambio di anno (stampa numero/anno con due cifre)", "Riparti da 1 al cambio di anno (stampa solo numero)", "Continua la numerazione di anno in anno (stampa numero)" }));

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel6Layout.createSequentialGroup()
                        .add(jLabel9)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(comLook, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pulsanteFont)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel7)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(texLimit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cheVisuAnno))
                    .add(jPanel6Layout.createSequentialGroup()
                        .add(jLabel15)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(comTipoLiquidazioneIva, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(jLabel12)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(comListinoBase, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(jLabel39)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(listino_consigliato, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 155, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel6Layout.createSequentialGroup()
                        .add(jLabel8)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(texIvaDefault, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(texIvaSpese, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(jLabel23)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(umpred, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel6Layout.createSequentialGroup()
                        .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel22)
                            .add(jLabel21))
                        .add(18, 18, 18)
                        .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(generazione_movimenti, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(raggruppa_articoli, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jPanel6Layout.createSequentialGroup()
                        .add(jLabel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 144, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(riportaSerie, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel6Layout.createSequentialGroup()
                        .add(jLabel32)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(stato_ordi_post, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel6Layout.createSequentialGroup()
                        .add(jLabel26)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(vostro_ordine, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel6Layout.createSequentialGroup()
                        .add(jLabel58)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(comTipoNumerazione, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6Layout.linkSize(new java.awt.Component[] {jLabel26, jLabel32}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel8)
                    .add(texIvaDefault, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2)
                    .add(texIvaSpese, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel23)
                    .add(umpred, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(comTipoLiquidazioneIva, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel12)
                    .add(comListinoBase, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel39)
                    .add(listino_consigliato, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel15))
                .add(8, 8, 8)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(generazione_movimenti, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel21))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel22)
                    .add(raggruppa_articoli, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(comLook, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(pulsanteFont)
                    .add(jLabel7)
                    .add(texLimit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cheVisuAnno)
                    .add(jLabel9))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(riportaSerie, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel5))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel26)
                    .add(vostro_ordine, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel32)
                    .add(stato_ordi_post, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel58)
                    .add(comTipoNumerazione, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel111.setText("<html>Logo azienda <small>(formato JPG, PNG, BMP o GIF)</small></html>");

        logo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logo.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        logo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        logo.setName("logo"); // NOI18N
        logo.setPreferredSize(new java.awt.Dimension(100, 50));
        logo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                logoMouseReleased(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                logoMousePressed(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logoMouseClicked(evt);
            }
        });

        tnxFileLogo.setBackground(new java.awt.Color(255, 204, 204));
        tnxFileLogo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tnxFileLogoKeyReleased(evt);
            }
        });

        comFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/document-open.png"))); // NOI18N
        comFile.setToolTipText("Clicca per selezionare o cambiare logo");
        comFile.setMargin(new java.awt.Insets(2, 2, 2, 2));
        comFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comFileActionPerformed(evt);
            }
        });

        cheNonStampareLogo.setText("non stampare");
        cheNonStampareLogo.setToolTipText("selezionare per non usare il logo selezionato");
        cheNonStampareLogo.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cheNonStampareLogo.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jLabel115.setText("x Stampe");
        jLabel115.setToolTipText("Logo per stampe");

        jLabel116.setText("x PDF");
        jLabel116.setToolTipText("Logo per stampe PDF per invio email");

        logo_email.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logo_email.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        logo_email.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        logo_email.setName("logo_email"); // NOI18N
        logo_email.setPreferredSize(new java.awt.Dimension(100, 50));
        logo_email.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                logo_emailMouseReleased(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                logo_emailMousePressed(evt);
            }
        });

        tnxFileLogo1.setBackground(new java.awt.Color(255, 204, 204));
        tnxFileLogo1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tnxFileLogo1KeyReleased(evt);
            }
        });

        comFile1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/document-open.png"))); // NOI18N
        comFile1.setToolTipText("Clicca per selezionare o cambiare logo");
        comFile1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        comFile1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comFile1ActionPerformed(evt);
            }
        });

        cheNonStampareLogo1.setText("non stampare");
        cheNonStampareLogo1.setToolTipText("selezionare per non usare il logo selezionato");
        cheNonStampareLogo1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cheNonStampareLogo1.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/mimetypes/font-x-generic.png"))); // NOI18N
        jButton5.setText("Imposta posizione e dimensioni del logo");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jLabel11.setText("Righe di intestazione stampe");
        jLabel11.setToolTipText("Righe di intestazioni dei documenti in stampa");

        jLabel3.setText("Etichetta cliente Italiano:");
        jLabel3.setToolTipText("Etichetta per cliente in fattura");

        jLabel4.setText("Etichetta Dest. Merce Italiano:");
        jLabel4.setToolTipText("Etichetta per destinazione merce in fattura");

        jLabel54.setText("Etichetta cliente Inglese:");
        jLabel54.setToolTipText("Etichetta per cliente in fattura");

        jLabel55.setText("Etichetta Dest. Merce Inglese:");
        jLabel55.setToolTipText("Etichetta per destinazione merce in fattura");

        org.jdesktop.layout.GroupLayout jPanel11Layout = new org.jdesktop.layout.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel11Layout.createSequentialGroup()
                        .add(50, 50, 50)
                        .add(jLabel115)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(logo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(jPanel11Layout.createSequentialGroup()
                                .add(cheNonStampareLogo)
                                .add(108, 108, 108))
                            .add(jPanel11Layout.createSequentialGroup()
                                .add(comFile)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(tnxFileLogo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 52, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(jLabel116)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                        .add(logo_email, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel11Layout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(cheNonStampareLogo1))
                            .add(jPanel11Layout.createSequentialGroup()
                                .add(2, 2, 2)
                                .add(comFile1)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(tnxFileLogo1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 54, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel11Layout.createSequentialGroup()
                        .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel3)
                            .add(jLabel54, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 119, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(texLabelClienteEng, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 141, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(texLabelCliente, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 141, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel4)
                            .add(jLabel55))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(texLabelMerce)
                            .add(texLabelMerceEng)))
                    .add(texInte6)
                    .add(texInte5)
                    .add(texInte4)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jSeparator1)
                    .add(texInte3)
                    .add(texInte2)
                    .add(texInte1)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jSeparator2)
                    .add(jLabel11)
                    .add(jButton5)
                    .add(jLabel111, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel11Layout.linkSize(new java.awt.Component[] {jLabel3, jLabel54}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel111, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(jPanel11Layout.createSequentialGroup()
                            .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(tnxFileLogo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(jLabel116))
                                .add(comFile))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(cheNonStampareLogo))
                        .add(jPanel11Layout.createSequentialGroup()
                            .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(comFile1)
                                .add(tnxFileLogo1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(cheNonStampareLogo1))
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, logo_email, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jLabel115)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, logo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton5)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel11)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(texInte1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(2, 2, 2)
                .add(texInte2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(2, 2, 2)
                .add(texInte3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(2, 2, 2)
                .add(texInte4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(2, 2, 2)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(2, 2, 2)
                .add(texInte5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(2, 2, 2)
                .add(texInte6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(texLabelCliente, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4)
                    .add(texLabelMerce, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(texLabelClienteEng, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(texLabelMerceEng, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel54)
                    .add(jLabel55))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel11, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jScrollPane3.setViewportView(jPanel1);

        jTabbedPane1.addTab("Azienda", jScrollPane3);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Connessione Database"));

        labHost.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labHost.setText("Host name");
        labHost.setToolTipText("Server su cui è installato il database");

        labDb.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labDb.setText("Database name");
        labDb.setToolTipText("Nome del database a cui collegarsi");

        labId.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labId.setText("Login");
        labId.setToolTipText("Username di accesso al database");

        labPwd.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labPwd.setText("Password");
        labPwd.setToolTipText("Password per l'accesso al database");

        cheAvvioDb.setText("Avvio/Stop del database insieme al programma");
        cheAvvioDb.setToolTipText("Apre automaticamente il database installato con il programma");
        cheAvvioDb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cheAvvioDbActionPerformed(evt);
            }
        });

        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel14.setText("Personalizzazioni");
        jLabel14.setToolTipText("<html>\nscrivere i codici separati da virgola, ad esempio: \"tnx1, nuova_clifor\"<br><br>\nnuova_clifor - abilita la nuova form per l'anagrafica clienti fornitori<br>\nrestore-utf8 - forza l'uso di UTF8 nel restore dei database<br>\nmovimenti_su_proforma - movimenta il magazzino anche da fatture pro-forma<br>\nno_controllo_plugin_auto - evita il download automatico dei plugin in base alla versione di Invoicex attivata<br>\n</html>");

        texPersonalizzazioni.setToolTipText("<html>\nscrivere i codici separati da virgola, ad esempio: \"tnx1, nuova_clifor\"<br><br>\nnuova_clifor - abilita la nuova form per l'anagrafica clienti fornitori<br>\nvecchia_clifor - disabilita la nuova form per l'anagrafica clienti fornitori<br>\nrestore-utf8 - forza l'uso di UTF8 nel restore dei database<br>\nmovimenti_su_proforma - movimenta il magazzino anche da fatture pro-forma<br>\nno_controllo_plugin_auto - evita il download automatico dei plugin in base alla versione di Invoicex attivata<br>\n</html>");

        cheAzioniPericolose.setText("Permetti azioni pericolose");
        cheAzioniPericolose.setToolTipText("<html>\nPermette di effettuare azioni che non dovrebbero essere svolte per mantenere la<br>\ncorretta gestione del programma (cambio numeri e date di fattura, cancellazione di<br>\ndocumenti con numeri successivi già inseriti)\n</html>");
        cheAzioniPericolose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cheAzioniPericoloseActionPerformed(evt);
            }
        });

        cheRichiediPassword.setText("Richiedi all'avvio");
        cheRichiediPassword.setToolTipText("Richiede l'inserimento della password ad ogni accesso al programma");
        cheRichiediPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cheRichiediPasswordActionPerformed(evt);
            }
        });

        jPanel10.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        ssh.setText("Utilizza Tunnel SSH");
        ssh.setToolTipText("Abilita l'utilizzo della connessione con SSH");
        ssh.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel34.setText("Hostname");
        jLabel34.setToolTipText("Nome del server su cui effettuare la connessione in SSH");

        ssh_hostname.setColumns(20);

        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel35.setText("Login");
        jLabel35.setToolTipText("Username per l'accesso al server tramite connessione SSH");

        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel36.setText("Password");
        jLabel36.setToolTipText("Password di accesso per la connessione con SSH");

        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel37.setText("Porta locale");
        jLabel37.setToolTipText("Porta locale da cui inviare i dati in SSH");

        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel38.setText("Porta remota");
        jLabel38.setToolTipText("Porta remota per la ricezione dei dati con connessione SSH");

        ssh_login.setColumns(20);

        ssh_porta_locale.setColumns(20);

        ssh_porta_remota.setColumns(20);

        ssh_password.setColumns(20);

        org.jdesktop.layout.GroupLayout jPanel10Layout = new org.jdesktop.layout.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel10Layout.createSequentialGroup()
                        .add(jLabel34)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(ssh_hostname, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE))
                    .add(ssh)
                    .add(jPanel10Layout.createSequentialGroup()
                        .add(jLabel35)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(ssh_login, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE))
                    .add(jPanel10Layout.createSequentialGroup()
                        .add(jLabel36)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(ssh_password, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE))
                    .add(jPanel10Layout.createSequentialGroup()
                        .add(jLabel37)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(ssh_porta_locale, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE))
                    .add(jPanel10Layout.createSequentialGroup()
                        .add(jLabel38)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(ssh_porta_remota, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel10Layout.linkSize(new java.awt.Component[] {jLabel34, jLabel35, jLabel36, jLabel37, jLabel38}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel10Layout.createSequentialGroup()
                .add(ssh)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel34)
                    .add(ssh_hostname, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel35)
                    .add(ssh_login, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel36)
                    .add(ssh_password, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel37)
                    .add(ssh_porta_locale, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel38)
                    .add(ssh_porta_remota, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Tunnel SSH", jPanel10);

        jPanel8.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        labDb1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labDb1.setText("TrustStore");

        ssl_truststore.setColumns(20);

        ssl.setText("Utilizza SSL");
        ssl.setToolTipText("Abilita l'invio dei dati criptati tramite protocollo SSL");
        ssl.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        select_truststore.setText("...");
        select_truststore.setMargin(new java.awt.Insets(2, 2, 2, 2));
        select_truststore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                select_truststoreActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel8Layout = new org.jdesktop.layout.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(ssl)
                    .add(jPanel8Layout.createSequentialGroup()
                        .add(labDb1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(ssl_truststore, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(select_truststore)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8Layout.createSequentialGroup()
                .add(ssl)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(labDb1)
                    .add(ssl_truststore, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(select_truststore))
                .addContainerGap(112, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("SSL", jPanel8);

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/22x22/apps/preferences-system-session.png"))); // NOI18N
        jButton3.setText("Riesegui Wizard iniziale");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(labPwd, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(labId, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(labDb, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(labHost, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(jLabel14))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(texId)
                        .add(cheAzioniPericolose)
                        .add(jPanel5Layout.createSequentialGroup()
                            .add(texPwd, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 136, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(cheRichiediPassword))
                        .add(cheAvvioDb)
                        .add(texPersonalizzazioni)
                        .add(texDb)
                        .add(texHost))
                    .add(jButton3))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTabbedPane2)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(jTabbedPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .add(jPanel5Layout.createSequentialGroup()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(labHost)
                    .add(texHost, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(labDb)
                    .add(texDb, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(labId)
                    .add(texId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(labPwd)
                    .add(texPwd, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cheRichiediPassword))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cheAvvioDb)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(texPersonalizzazioni, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel14))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cheAzioniPericolose)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton3)
                .add(0, 0, Short.MAX_VALUE))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Stampe"));

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText("Tipo stampa Fatt.");
        jLabel18.setToolTipText("<html>Modello di stampa per Fatture Immediate,<br>Note di credito e Fatture Pro-Forma</html>");

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("Tipo stampa Fatt. Acc.");
        jLabel16.setToolTipText("Modello di stampa per Fatture Accompagatorie");

        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText("Tipo stampa DDT");
        jLabel19.setToolTipText("Modello di stampa per DDT");

        cheStampaPdf.setText("Stampa in formato PDF");
        cheStampaPdf.setToolTipText("Verrà creato un file PDF e aperto con il visualizzatore predefinito");
        cheStampaPdf.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        cheStampaPdf.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel20.setText("Tipo stampa Ordine");
        jLabel20.setToolTipText("Modello di stampa per Preventivi e Ordini");

        btnPreview.setFont(btnPreview.getFont().deriveFont(btnPreview.getFont().getSize()-1f));
        btnPreview.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/document-print-preview.png"))); // NOI18N
        btnPreview.setText("Anteprima");
        btnPreview.setIconTextGap(2);
        btnPreview.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnPreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreviewActionPerformed(evt);
            }
        });

        btnPreview1.setFont(btnPreview1.getFont().deriveFont(btnPreview1.getFont().getSize()-1f));
        btnPreview1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/document-print-preview.png"))); // NOI18N
        btnPreview1.setText("Anteprima");
        btnPreview1.setIconTextGap(2);
        btnPreview1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnPreview1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreview1ActionPerformed(evt);
            }
        });

        btnPreview2.setFont(btnPreview2.getFont().deriveFont(btnPreview2.getFont().getSize()-1f));
        btnPreview2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/document-print-preview.png"))); // NOI18N
        btnPreview2.setText("Anteprima");
        btnPreview2.setIconTextGap(2);
        btnPreview2.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnPreview2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreview2ActionPerformed(evt);
            }
        });

        btnPreview3.setFont(btnPreview3.getFont().deriveFont(btnPreview3.getFont().getSize()-1f));
        btnPreview3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/document-print-preview.png"))); // NOI18N
        btnPreview3.setText("Anteprima");
        btnPreview3.setIconTextGap(2);
        btnPreview3.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnPreview3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPreview3ActionPerformed(evt);
            }
        });

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/go-jump.png"))); // NOI18N
        jButton4.setText("Importa Report");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel7Layout.createSequentialGroup()
                        .add(jLabel18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 160, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(comTipoStampa1, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnPreview))
                    .add(jPanel7Layout.createSequentialGroup()
                        .add(cheStampaPdf)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jButton4))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel7Layout.createSequentialGroup()
                        .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel7Layout.createSequentialGroup()
                                .add(jLabel19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 160, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(comTipoStampaDdt, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .add(jPanel7Layout.createSequentialGroup()
                                .add(jLabel16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 160, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(comTipoStampa, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel7Layout.createSequentialGroup()
                                .add(jLabel20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 160, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(comTipoStampaOrdine, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(btnPreview1)
                            .add(btnPreview2)
                            .add(btnPreview3))))
                .addContainerGap())
        );

        jPanel7Layout.linkSize(new java.awt.Component[] {cheStampaPdf, jLabel16, jLabel18, jLabel19, jLabel20}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(comTipoStampa1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnPreview))
                .add(2, 2, 2)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(comTipoStampa, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnPreview1))
                .add(2, 2, 2)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(comTipoStampaDdt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnPreview2))
                .add(2, 2, 2)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(comTipoStampaOrdine, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnPreview3))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cheStampaPdf)
                    .add(jButton4))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(162, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Avanzate", jPanel2);

        jScrollPane4.setBorder(null);
        jScrollPane4.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jLabel10.setText("Note da inserire in automatico sui nuovi documenti");

        jScrollPane1.setViewportView(texNoteStandard);

        jLabel17.setText("Messaggio in stampa");

        jLabel113.setText("Sfondo per stampe documenti (JPG, PNG, GIF)");

        tnxFileSfondo.setBackground(new java.awt.Color(255, 153, 153));
        tnxFileSfondo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tnxFileSfondoKeyReleased(evt);
            }
        });

        tnxFileSfondoPdf.setBackground(new java.awt.Color(255, 153, 153));
        tnxFileSfondoPdf.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tnxFileSfondoPdfKeyReleased(evt);
            }
        });

        cheNonStampareSfondo.setText("non stampare");
        cheNonStampareSfondo.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cheNonStampareSfondo.setMargin(new java.awt.Insets(0, 0, 0, 0));

        cheNonStampareSfondoPdf.setText("non stampare");
        cheNonStampareSfondoPdf.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cheNonStampareSfondoPdf.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jLabel24.setText("Stringa su conversione da DDT");

        jLabel25.setText("Stringa su conversione da Ordine");

        da_ordine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                da_ordineActionPerformed(evt);
            }
        });

        jLabel117.setText("x Stampe");

        sfondo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        sfondo.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        sfondo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        sfondo.setName("sfondo"); // NOI18N
        sfondo.setPreferredSize(new java.awt.Dimension(100, 50));
        sfondo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                sfondoMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                sfondoMouseReleased(evt);
            }
        });

        comFileSfondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/document-open.png"))); // NOI18N
        comFileSfondo.setToolTipText("Clicca per selezionare o cambiare logo");
        comFileSfondo.setMargin(new java.awt.Insets(2, 2, 2, 2));
        comFileSfondo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comFileSfondoActionPerformed(evt);
            }
        });

        jLabel118.setText("x Email");

        sfondo_email.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        sfondo_email.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        sfondo_email.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        sfondo_email.setName("sfondo_email"); // NOI18N
        sfondo_email.setPreferredSize(new java.awt.Dimension(100, 50));
        sfondo_email.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                sfondo_emailMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                sfondo_emailMouseReleased(evt);
            }
        });

        comFileSfondoEmail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/document-open.png"))); // NOI18N
        comFileSfondoEmail.setToolTipText("Clicca per selezionare o cambiare logo");
        comFileSfondoEmail.setMargin(new java.awt.Insets(2, 2, 2, 2));
        comFileSfondoEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comFileSfondoEmailActionPerformed(evt);
            }
        });

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("Modalità apertura file o cartelle");

        aperturaFile.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Automatica", "Manuale" }));
        aperturaFile.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                aperturaFileItemStateChanged(evt);
            }
        });

        aperturaImposta.setText("Imposta i comandi standard");
        aperturaImposta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aperturaImpostaActionPerformed(evt);
            }
        });

        aperturaLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        aperturaLabel1.setText("comando per aprire cartelle");

        aperturaLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        aperturaLabel2.setText("comando per aprire file");

        aperturaLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        aperturaLabel3.setText("comando per aprire pdf");

        int_dest_1.setText("<html><b><font size=\\\"3\\\">\" + $F{ragione_sociale1} + \"</font></b><br><font size=\\\"2\\\">\" + $F{indirizzo1} + \"<br>\" + $F{cap_loc_prov1} + \"</font><br><font size=\\\"1\\\">\" + $F{piva_cfiscale_desc1} + ($F{piva_cfiscale_desc1}.length() == 0 ? \"\" : \"<br>\") + $F{recapito1} + \"</font></html>");

        jLabel44.setText("Intestazione in stampa destinatario 1");

        jLabel45.setText("Intestazione in stampa destinatario 2");

        int_dest_2.setText("<html><b><font size=\\\"3\\\">\" + $F{ragione_sociale2} + \"</font></b><br><font size=\\\"2\\\">\" + $F{indirizzo2} + \"<br>\" + $F{cap_loc_prov2} + \"</font><br><font size=\\\"1\\\">\" + $F{recapito_2_sotto} + ($P{stampaPivaSotto}.booleanValue() ? \"\" : $F{piva_cfiscale_desc_2_sotto}) + (($F{piva_cfiscale_desc_2_sotto}.length() > 0 && $P{stampaPivaSotto}.booleanValue()) ? \"<br>\" : \"\") + \"</font></html>");

        jLabel56.setText("Timbro e Firma su stampa Ordine/Preventivo");

        stampare_timbro_firma.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Non stampare mai", "Stampa su Preventivi/Ordini/Conferme d'ordine", "Stampa su Ordini/Conferme d'ordine" }));

        testo_timbro_firma.setFont(testo_timbro_firma.getFont().deriveFont(testo_timbro_firma.getFont().getSize()-1f));
        testo_timbro_firma.setRows(4);
        jScrollPane10.setViewportView(testo_timbro_firma);

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jSeparator4)
                    .add(jScrollPane10)
                    .add(jScrollPane1)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jLabel45)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(int_dest_2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 498, Short.MAX_VALUE))
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jLabel24)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(da_ddt))
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jLabel25)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(da_ordine))
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jLabel44)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(int_dest_1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jLabel56)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(stampare_timbro_firma, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jLabel10)
                    .add(jLabel113)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jLabel117)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(sfondo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(comFileSfondo)
                            .add(cheNonStampareSfondo)
                            .add(tnxFileSfondo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 46, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(50, 50, 50)
                        .add(jLabel118)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(sfondo_email, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(comFileSfondoEmail)
                            .add(cheNonStampareSfondoPdf)
                            .add(tnxFileSfondoPdf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 59, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jLabel13)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(aperturaFile, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(aperturaImposta))
                    .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel3Layout.createSequentialGroup()
                            .add(aperturaLabel3)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(aperturaText3))
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel3Layout.createSequentialGroup()
                            .add(aperturaLabel2)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(aperturaText2))
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel3Layout.createSequentialGroup()
                            .add(aperturaLabel1)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(aperturaText1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 291, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jLabel17)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(texMessaggioStampa)))
                .addContainerGap())
        );

        jPanel3Layout.linkSize(new java.awt.Component[] {tnxFileSfondo, tnxFileSfondoPdf}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel3Layout.linkSize(new java.awt.Component[] {aperturaLabel1, aperturaLabel2, aperturaLabel3, jLabel13, jLabel24, jLabel25}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel10)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 75, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(1, 1, 1)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel17)
                    .add(texMessaggioStampa, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(1, 1, 1)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(int_dest_1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel44))
                .add(1, 1, 1)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel45)
                    .add(int_dest_2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel113)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel117, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 103, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel118, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, sfondo_email, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(sfondo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 103, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(comFileSfondo)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cheNonStampareSfondo)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(tnxFileSfondo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(comFileSfondoEmail)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cheNonStampareSfondoPdf)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(tnxFileSfondoPdf, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel56)
                    .add(stampare_timbro_firma, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(1, 1, 1)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel24)
                    .add(da_ddt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(1, 1, 1)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel25)
                    .add(da_ordine, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(1, 1, 1)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel13)
                    .add(aperturaFile, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(aperturaImposta))
                .add(1, 1, 1)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(aperturaLabel1)
                    .add(aperturaText1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(1, 1, 1)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(aperturaLabel2)
                    .add(aperturaText2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(1, 1, 1)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(aperturaLabel3)
                    .add(aperturaText3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(1, 1, 1))
        );

        jPanel3Layout.linkSize(new java.awt.Component[] {sfondo, sfondo_email}, org.jdesktop.layout.GroupLayout.VERTICAL);

        jScrollPane4.setViewportView(jPanel3);

        jTabbedPane1.addTab("Altro", jScrollPane4);

        cheProvvigioniAuto.setText("Attiva Provvigioni Automatiche");
        cheProvvigioniAuto.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cheProvvigioniAutoItemStateChanged(evt);
            }
        });

        griglia_soglie.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(griglia_soglie);

        chePercStandard.setText("Usa percentuali standard per tutti gli agenti");
        chePercStandard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chePercStandardActionPerformed(evt);
            }
        });

        jLabel1.setText("Le percentuali minimo e di massimo sono relative allo sconto 1 della fattura.");

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/16x16/actions/edit-select-all.png"))); // NOI18N
        jButton6.setText("Modifica Soglie");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jLabel31.setText("Data per le provvigioni:");
        jLabel31.setToolTipText("Imposta a quale data generare le provvigioni per l'agente");

        buttonGroup1.add(data_scadenza);
        data_scadenza.setText("Alla scadenza di pagamento");

        buttonGroup1.add(data_fattura);
        data_fattura.setText("A data fattura");

        org.jdesktop.layout.GroupLayout jPanel9Layout = new org.jdesktop.layout.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(chePercStandard)
                    .add(cheProvvigioniAuto)
                    .add(jButton6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 144, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1)
                    .add(jPanel9Layout.createSequentialGroup()
                        .add(jLabel31)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(data_scadenza)
                        .add(18, 18, 18)
                        .add(data_fattura))
                    .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane2)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jSeparator3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel31)
                    .add(data_scadenza)
                    .add(data_fattura))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cheProvvigioniAuto)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(chePercStandard)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 144, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton6)
                .addContainerGap(292, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Provvigioni", jPanel9);

        cheAttivaEmiCad.setText("Attiva Concatenazione PDF");
        cheAttivaEmiCad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cheAttivaEmiCadActionPerformed(evt);
            }
        });

        cheEmiCadRichiedi.setText("Richiedi tutte le volte i file da concatenare");
        cheEmiCadRichiedi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cheEmiCadRichiediActionPerformed(evt);
            }
        });

        texEmiCadFilePre.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                texEmiCadFilePreFocusLost(evt);
            }
        });

        jLabel29.setText("File Precedente:");

        jLabel30.setText("File Successivo:");

        texEmiCadFilePost.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                texEmiCadFilePostFocusLost(evt);
            }
        });

        butEmiCadFilePre.setText("...");
        butEmiCadFilePre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butEmiCadFilePreActionPerformed(evt);
            }
        });

        butEmiCadFilePost.setText("...");
        butEmiCadFilePost.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butEmiCadFilePostActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout panEmiCadLayout = new org.jdesktop.layout.GroupLayout(panEmiCad);
        panEmiCad.setLayout(panEmiCadLayout);
        panEmiCadLayout.setHorizontalGroup(
            panEmiCadLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panEmiCadLayout.createSequentialGroup()
                .addContainerGap()
                .add(panEmiCadLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cheAttivaEmiCad)
                    .add(cheEmiCadRichiedi)
                    .add(panEmiCadLayout.createSequentialGroup()
                        .add(panEmiCadLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(jLabel30, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jLabel29, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(panEmiCadLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(texEmiCadFilePost)
                            .add(texEmiCadFilePre, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(panEmiCadLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(butEmiCadFilePost, 0, 1, Short.MAX_VALUE)
                            .add(butEmiCadFilePre, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 34, Short.MAX_VALUE))))
                .addContainerGap())
        );
        panEmiCadLayout.setVerticalGroup(
            panEmiCadLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panEmiCadLayout.createSequentialGroup()
                .addContainerGap()
                .add(cheAttivaEmiCad)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(cheEmiCadRichiedi)
                .add(7, 7, 7)
                .add(panEmiCadLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel29)
                    .add(texEmiCadFilePre, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(butEmiCadFilePre))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(panEmiCadLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(texEmiCadFilePost, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel30)
                    .add(butEmiCadFilePost))
                .addContainerGap())
        );

        jTabbedPane1.addTab("EmiCAD", panEmiCad);

        jTabbedPane3.setBorder(javax.swing.BorderFactory.createTitledBorder("Note a fondo Documento"));

        jLabel40.setText("Fatture");

        texNoteFatt.setColumns(20);
        texNoteFatt.setRows(5);
        jScrollPane7.setViewportView(texNoteFatt);

        jLabel41.setText("DDT");

        texNoteDocu.setColumns(20);
        texNoteDocu.setRows(5);
        jScrollPane8.setViewportView(texNoteDocu);

        jLabel42.setText("Preventivi/Ordini");

        texNoteOrdi.setColumns(20);
        texNoteOrdi.setRows(5);
        jScrollPane9.setViewportView(texNoteOrdi);

        org.jdesktop.layout.GroupLayout jPanel12Layout = new org.jdesktop.layout.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 624, Short.MAX_VALUE)
                    .add(jScrollPane8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 624, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 624, Short.MAX_VALUE)
                    .add(jLabel40)
                    .add(jLabel41)
                    .add(jLabel42))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel40)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel41)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel42)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                .add(461, 461, 461))
        );

        jTabbedPane3.addTab("Documenti di Vendita", jPanel12);

        jLabel27.setText("DDT");

        texNoteDocuAcquisto.setColumns(20);
        texNoteDocuAcquisto.setRows(5);
        jScrollPane5.setViewportView(texNoteDocuAcquisto);

        jLabel28.setText("Preventivi/Ordini");

        texNoteOrdiAcquisto.setColumns(20);
        texNoteOrdiAcquisto.setRows(5);
        jScrollPane6.setViewportView(texNoteOrdiAcquisto);

        org.jdesktop.layout.GroupLayout jPanel13Layout = new org.jdesktop.layout.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 624, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 624, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel27)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel28))
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel27)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel28)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                .add(554, 554, 554))
        );

        jTabbedPane3.addTab("Documenti di acquisto", jPanel13);

        labStampaFirma.setText("Immagine Firma:");

        butImgFirma.setText("...");
        butImgFirma.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butImgFirmaActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout panPrintOptionLayout = new org.jdesktop.layout.GroupLayout(panPrintOption);
        panPrintOption.setLayout(panPrintOptionLayout);
        panPrintOptionLayout.setHorizontalGroup(
            panPrintOptionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panPrintOptionLayout.createSequentialGroup()
                .addContainerGap()
                .add(panPrintOptionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jTabbedPane3)
                    .add(panPrintOptionLayout.createSequentialGroup()
                        .add(labStampaFirma)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(texImgFirma)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(butImgFirma, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 34, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panPrintOptionLayout.setVerticalGroup(
            panPrintOptionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panPrintOptionLayout.createSequentialGroup()
                .addContainerGap()
                .add(jTabbedPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 359, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(panPrintOptionLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(labStampaFirma)
                    .add(texImgFirma, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(butImgFirma))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Opzioni di stampa", panPrintOption);

        jLabel46.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel46.setText("Stampa codici:");

        comStampaBarcode.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Per Articolo", "Per Quantità" }));
        comStampaBarcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comStampaBarcodeActionPerformed(evt);
            }
        });

        jLabel47.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel47.setText("Testo libero:");

        jLabel48.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel48.setText("Dettagli Articolo:");

        jLabel49.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel49.setText("Disposizione Elementi:");

        cheBarcodeCodArticolo.setText("Codice Articolo");

        cheBarcodePrezzoArticolo.setText("Prezzo");
        cheBarcodePrezzoArticolo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cheBarcodePrezzoArticoloActionPerformed(evt);
            }
        });

        cheBarcodeQtaArticolo.setText("Quantità");

        comBarcodeDisposizione.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Dettaglio Articolo sopra - Testo libero sotto", "Testo libero sopra - Dettaglio Articolo sotto" }));

        jLabel50.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel50.setText("Tipo Prezzo:");

        comBarcodeTipoPrezzo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Iva Inclusa", "Iva Esclusa" }));

        jLabel51.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel51.setText("Colonne:");

        comBarcodeColonne.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6" }));

        jLabel52.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel52.setText("Righe:");

        comBarcodeRighe.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20" }));

        jLabel53.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel53.setText("Formato Barocode:");

        comBarcodeFormato.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2of7", "3of9", "Bookland", "Codabar", "Code128", "Code128A", "Code128B", "Code128C", "Code39", "Code39 (Extended)", "EAN128", "EAN13", "GlobalTradeItemNumber", "Int2of5", "Monarch", "NW7", "PDF417", "PostNet", "RandomWeightUPCA", "SCC14ShippingCode", "SSCC18", "ShipmentIdentificationNumber", "Std2of5", "UCC128", "UPCA", "USD3", "USD4", "USPS" }));

        cheBarcodeDrawtext.setText("Testo Barcode");
        cheBarcodeDrawtext.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        cheBarcodeDrawtext.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jLabel57.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel57.setText("Margini");

        jPanel15.setBackground(new java.awt.Color(255, 255, 255));
        jPanel15.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        org.jdesktop.layout.GroupLayout jPanel15Layout = new org.jdesktop.layout.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 97, Short.MAX_VALUE)
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 133, Short.MAX_VALUE)
        );

        mtop.setColumns(5);

        mright.setColumns(5);

        mleft.setColumns(5);

        mbottom.setColumns(5);
        mbottom.setToolTipText("");

        jLabel59.setText("Pagina, larghezza");

        plarghezza.setColumns(5);

        jLabel60.setText("x altezza");

        paltezza.setColumns(5);

        org.jdesktop.layout.GroupLayout panelBarCodeLayout = new org.jdesktop.layout.GroupLayout(panelBarCode);
        panelBarCode.setLayout(panelBarCodeLayout);
        panelBarCodeLayout.setHorizontalGroup(
            panelBarCodeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelBarCodeLayout.createSequentialGroup()
                .addContainerGap()
                .add(panelBarCodeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(panelBarCodeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(panelBarCodeLayout.createSequentialGroup()
                            .add(panelBarCodeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                .add(jLabel47, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(jLabel46, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(panelBarCodeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(comStampaBarcode, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(texFreeBarcode)))
                        .add(panelBarCodeLayout.createSequentialGroup()
                            .add(jLabel49)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(comBarcodeDisposizione, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(panelBarCodeLayout.createSequentialGroup()
                            .add(jLabel48, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 94, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                            .add(panelBarCodeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(cheBarcodePrezzoArticolo)
                                .add(cheBarcodeCodArticolo)
                                .add(cheBarcodeQtaArticolo)))
                        .add(panelBarCodeLayout.createSequentialGroup()
                            .add(panelBarCodeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                .add(jLabel53, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(jLabel51, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(jLabel50, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                                .add(org.jdesktop.layout.GroupLayout.TRAILING, mleft, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(jLabel57, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .add(panelBarCodeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(panelBarCodeLayout.createSequentialGroup()
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(panelBarCodeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(comBarcodeTipoPrezzo, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .add(comBarcodeFormato, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .add(panelBarCodeLayout.createSequentialGroup()
                                            .add(panelBarCodeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                .add(panelBarCodeLayout.createSequentialGroup()
                                                    .add(comBarcodeColonne, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                    .add(jLabel52, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 56, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                    .add(comBarcodeRighe, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                    .add(4, 4, 4)
                                                    .add(cheBarcodeDrawtext, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                                .add(panelBarCodeLayout.createSequentialGroup()
                                                    .add(panelBarCodeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(jPanel15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                        .add(panelBarCodeLayout.createSequentialGroup()
                                                            .add(25, 25, 25)
                                                            .add(mbottom, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                    .add(mright, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                            .add(0, 0, Short.MAX_VALUE))))
                                .add(panelBarCodeLayout.createSequentialGroup()
                                    .add(31, 31, 31)
                                    .add(mtop, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(0, 0, Short.MAX_VALUE)))))
                    .add(panelBarCodeLayout.createSequentialGroup()
                        .add(jLabel59)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(plarghezza, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel60)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(paltezza, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        panelBarCodeLayout.linkSize(new java.awt.Component[] {jLabel46, jLabel47, jLabel48, jLabel49}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        panelBarCodeLayout.setVerticalGroup(
            panelBarCodeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelBarCodeLayout.createSequentialGroup()
                .addContainerGap()
                .add(panelBarCodeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel46)
                    .add(comStampaBarcode, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(panelBarCodeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel47)
                    .add(texFreeBarcode, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(panelBarCodeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel48)
                    .add(cheBarcodeCodArticolo))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cheBarcodePrezzoArticolo)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cheBarcodeQtaArticolo)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(panelBarCodeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel49)
                    .add(comBarcodeDisposizione, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(panelBarCodeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel50)
                    .add(comBarcodeTipoPrezzo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(panelBarCodeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel51)
                    .add(comBarcodeColonne, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel52)
                    .add(comBarcodeRighe, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cheBarcodeDrawtext))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(panelBarCodeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel53)
                    .add(comBarcodeFormato, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(panelBarCodeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel57)
                    .add(mtop, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(panelBarCodeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(panelBarCodeLayout.createSequentialGroup()
                        .add(56, 56, 56)
                        .add(mright, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(panelBarCodeLayout.createSequentialGroup()
                        .add(54, 54, 54)
                        .add(mleft, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(panelBarCodeLayout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mbottom, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(panelBarCodeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel59)
                    .add(plarghezza, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel60)
                    .add(paltezza, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Codici a Barre", panelBarCode);

        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel33.setText("Conto Ricavi Default");

        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel43.setText("Codice IVA Default");

        texContoRicaviReadytec.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                texContoRicaviReadytecActionPerformed(evt);
            }
        });

        estraiScadenzeFatseq.setText("Includi le scadenze nel file");
        estraiScadenzeFatseq.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jLabel61.setText("Parametri per l'export delle fatture nel tracciato FATSEQ di TeamSystem");

        org.jdesktop.layout.GroupLayout jPanel14Layout = new org.jdesktop.layout.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(estraiScadenzeFatseq)
                    .add(jPanel14Layout.createSequentialGroup()
                        .add(jLabel43)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(texCodIvaReadytec, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 204, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel14Layout.createSequentialGroup()
                        .add(jLabel33)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(texContoRicaviReadytec, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 204, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jLabel61))
                .addContainerGap())
        );

        jPanel14Layout.linkSize(new java.awt.Component[] {jLabel33, jLabel43}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel61)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel43)
                    .add(texCodIvaReadytec, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel33)
                    .add(texContoRicaviReadytec, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(estraiScadenzeFatseq)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Export TeamSystem", jPanel14);

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/22x22/devices/media-floppy.png"))); // NOI18N
        jButton1.setText("Conferma");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/icons/tango-icon-theme-080/22x22/actions/edit-undo.png"))); // NOI18N
        jButton2.setText("Annulla");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(2, 2, 2)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(jButton2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButton1))
                    .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 686, Short.MAX_VALUE))
                .add(2, 2, 2))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(0, 0, 0)
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 612, Short.MAX_VALUE)
                .add(2, 2, 2)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton1)
                    .add(jButton2))
                .add(2, 2, 2))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        //        setVisible(false);
        //        dispose();
    }//GEN-LAST:event_formWindowClosed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (main.getPersonalContain("gaia_servizi")) {
            File ftest5 = new File(texImgFirma.getText());
            if (texImgFirma.getText().length() > 0 && !ftest5.exists()) {
                SwingUtils.showWarningMessage(this, "Il file di firma per ordine non esiste!");
            }
            //check image size
            try {
                SimpleImageInfo sii = new SimpleImageInfo(new File(texImgFirma.getText()));
                System.out.println("img w: " + sii.getWidth() + " h: " + sii.getHeight() + " mime: " + sii.getMimeType());
                if (!main.debug) {
                    if ((sii.getWidth() * sii.getHeight()) > (3000 * 2000)) {
                        SwingUtils.showWarningMessage(this, "Il file è troppo grande (il limite è 3000 * 2000 px)");
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        SwingUtils.mouse_wait(this);

        try {
            main.fileIni.setValue("db", "server", this.texHost.getText());
            main.fileIni.setValue("db", "nome_database", this.texDb.getText());
            main.fileIni.setValue("db", "user", this.texId.getText());
            main.fileIni.setValue("db", "ssl", ssl.isSelected());
            main.fileIni.setValue("db", "ssl_truststore", ssl_truststore.getText());
//            main.fileIni.setValue("db", "ssl_keystore", ssl_keystore.getText());
            main.fileIni.setValueCifrato("db", "pwd", this.texPwd.getText());

            main.fileIni.setValue("db", "ssh", ssh.isSelected());
            main.fileIni.setValue("db", "ssh_hostname", ssh_hostname.getText());
            main.fileIni.setValue("db", "ssh_login", ssh_login.getText());
            main.fileIni.setValueCifrato("db", "ssh_password", ssh_password.getText());
            main.fileIni.setValue("db", "ssh_porta_remota", ssh_porta_remota.getText());
            main.fileIni.setValue("db", "ssh_porta_locale", ssh_porta_locale.getText());


            //            main.fileIni.setValue("db", "startdb", this.texAvvioDb.getText());
            //            main.fileIni.setValue("db", "stopdb", this.texStopDb.getText());
            main.fileIni.setValue("varie", "finestre_grandi", cheApriFinestreGrandi.isSelected() ? "si" : "no");
            main.fileIni.setValue("varie", "percorso_logo_stampe", tnxFileLogo.getText());
            main.fileIni.setValue("varie", "percorso_logo_stampe_pdf", tnxFileLogo1.getText());
            if (main.getPersonalContain("gaia_servizi")) {
                main.fileIni.setValue("gaiaservizi", "percorso_immagine_firma", texImgFirma.getText());
            }
            main.fileIni.setValue("varie", "non_stampare_logo", cheNonStampareLogo.isSelected() ? "si" : "no");
            main.fileIni.setValue("varie", "non_stampare_logo_pdf", cheNonStampareLogo1.isSelected() ? "si" : "no");
//            main.iniPercorsoLogoStampe = tnxFileLogo.getText();
//            main.iniPercorsoLogoStampePdf = tnxFileLogo1.getText();

            main.fileIni.setValue("varie", "percorso_sfondo_stampe", tnxFileSfondo.getText());
            main.fileIni.setValue("varie", "percorso_sfondo_stampe_pdf", tnxFileSfondoPdf.getText());
            main.fileIni.setValue("varie", "non_stampare_sfondo", cheNonStampareSfondo.isSelected() ? "si" : "no");
            main.fileIni.setValue("varie", "non_stampare_sfondo_pdf", cheNonStampareSfondoPdf.isSelected() ? "si" : "no");

            main.fileIni.setValue("varie", "apertura_file", aperturaFile.getSelectedIndex());
            main.fileIni.setValue("varie", "apertura_file_comando_cartella", aperturaText1.getText());
            main.fileIni.setValue("varie", "apertura_file_comando_file", aperturaText2.getText());
            main.fileIni.setValue("varie", "apertura_file_comando_pdf", aperturaText3.getText());

            main.iniPercorsoSfondoStampe = tnxFileSfondo.getText();
            main.iniPercorsoSfondoStampePdf = tnxFileSfondoPdf.getText();

            System.out.println("set iniPercorsoSfondoStampe:" + main.iniPercorsoSfondoStampe);
            System.out.println("set iniPercorsoSfondoStampePdf:" + main.iniPercorsoSfondoStampePdf);

            main.fileIni.setValue("varie", "look", comLook.getSelectedItem().toString());

            //scrivo le righe di intestazione
            //carico righe intestazione stampe
            if (!vaisudb) {
                try {
                    if (Db.conn != null && !Db.conn.isClosed()) {
                        String sql = "update dati_azienda set ";
                        sql += " intestazione_riga1 = " + Db.pc(this.texInte1.getText(), "VARCHAR");
                        sql += " ,intestazione_riga2 = " + Db.pc(this.texInte2.getText(), "VARCHAR");
                        sql += " ,intestazione_riga3 = " + Db.pc(this.texInte3.getText(), "VARCHAR");
                        sql += " ,intestazione_riga4 = " + Db.pc(this.texInte4.getText(), "VARCHAR");
                        sql += " ,intestazione_riga5 = " + Db.pc(this.texInte5.getText(), "VARCHAR");
                        sql += " ,intestazione_riga6 = " + Db.pc(this.texInte6.getText(), "VARCHAR");
                        sql += " ,label_cliente = " + Db.pc(this.texLabelCliente.getText(), "VARCHAR");
                        sql += " ,label_destinazione = " + Db.pc(this.texLabelMerce.getText(), "VARCHAR");
                        sql += " ,label_cliente_eng = " + Db.pc(this.texLabelClienteEng.getText(), "VARCHAR");
                        sql += " ,label_destinazione_eng = " + Db.pc(this.texLabelMerceEng.getText(), "VARCHAR");

                        if (comListinoBase.getSelectedItem() != null) {
                            sql += " ,listino_base = " + Db.pc(this.comListinoBase.getSelectedItem().toString(), "VARCHAR");
                        }

//                        sql += " ,targa = " + Db.pc(this.texTarga.getText(), "VARCHAR");

                        if (comTipoLiquidazioneIva.getSelectedItem() != null) {
                            sql += " , tipo_liquidazione_iva = " + Db.pc(this.comTipoLiquidazioneIva.getSelectedItem().toString(), "VARCHAR");
                        }

                        sql += " ,testo_piede_fatt_v = " + Db.pc(this.texNoteFatt.getText(), "VARCHAR");
                        sql += " ,testo_piede_docu_v = " + Db.pc(this.texNoteDocu.getText(), "VARCHAR");
                        sql += " ,testo_piede_ordi_v = " + Db.pc(this.texNoteOrdi.getText(), "VARCHAR");
                        //                    sql += " ,testo_piede_fatt_a = " + Db.pc(this.texNoteFattAcquisto.getText(), "VARCHAR");
                        sql += " ,testo_piede_docu_a = " + Db.pc(this.texNoteDocuAcquisto.getText(), "VARCHAR");
                        sql += " ,testo_piede_ordi_a = " + Db.pc(this.texNoteOrdiAcquisto.getText(), "VARCHAR");

                        String stampaRigaInvoicex = this.cheStampaScrittaInvoicex.isSelected() ? "1" : "0";
                        sql += " ,stampa_riga_invoicex = " + Db.pc(stampaRigaInvoicex, Types.INTEGER);

                        //tipo provvigioni
                        String provvigioni_tipo_data = "data_scadenza";
                        if (data_fattura.isSelected()) {
                            provvigioni_tipo_data = "data_fattura";
                        }
                        sql += " , provvigioni_tipo_data = " + Db.pc(provvigioni_tipo_data, Types.VARCHAR);

                        //timbro firmo
                        try {
                            sql += " ,stampare_timbro_firma = " + Db.pc(CastUtils.toString(stampare_timbro_firma.getSelectedItem()), "VARCHAR");
                            sql += " ,testo_timbro_firma = " + Db.pc(testo_timbro_firma.getText(), "VARCHAR");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        
                        //tipo numerazione
                        try {
                            sql += " ,tipo_numerazione = " + Db.pc(comTipoNumerazione.getSelectedIndex(), Types.INTEGER);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //export fatture
                        try {
                            sql += " ,export_fatture_codice_iva = " + Db.pc(texCodIvaReadytec.getText(), Types.VARCHAR);
                            sql += " ,export_fatture_conto_ricavi = " + Db.pc(texContoRicaviReadytec.getText(), Types.VARCHAR);
                            sql += " ,export_fatture_estrai_scadenze = " + Db.pc(estraiScadenzeFatseq.isSelected() ? "S" : "", Types.VARCHAR);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        System.out.println("salvo logo in db...");
                        if (!old_file_logo.equalsIgnoreCase(tnxFileLogo.getText()) || salvare_logo) {
                            InvoicexUtil.salvaImgInDb(tnxFileLogo.getText());
                        }
                        if (!old_file_logo_email.equalsIgnoreCase(tnxFileLogo1.getText()) || salvare_logo_email) {
                            InvoicexUtil.salvaImgInDb(tnxFileLogo1.getText(), "logo_email");
                        }

                        if (!old_file_sfondo.equalsIgnoreCase(tnxFileSfondo.getText()) || salvare_sfondo) {
                            InvoicexUtil.salvaImgInDb(tnxFileSfondo.getText(), "sfondo");
                        }
                        if (!old_file_sfondo_email.equalsIgnoreCase(tnxFileSfondoPdf.getText()) || salvare_sfondo_email) {
                            InvoicexUtil.salvaImgInDb(tnxFileSfondoPdf.getText(), "sfondo_email");
                        }

                        System.out.println("salvo logo in db fine");

                        if (main.getPersonalContain("gaia_servizi")) {
                            salvaFirmaInDb(texImgFirma.getText());
                        }
                        System.out.println("!! sql impo:" + sql);
                        Db.executeSql(sql);
                    }
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

            //opzioni avanzate
            if (this.cheAvvioDb.isSelected()) {
                main.fileIni.setValue("db", "startdbcheck", "S");
            } else {
                main.fileIni.setValue("db", "startdbcheck", "N");
            }

            //codice iva per le spese se vuoto vengono ripartizionate
            if (Db.conn != null && !Db.conn.isClosed()) {
                main.fileIni.setValue("iva", "codiceIvaSpese", this.texIvaSpese.getText());
                try {
                    DbUtils.tryExecQuery(Db.getConn(true), "update dati_azienda set codiceIvaSpese = " + Db.pcs(this.texIvaSpese.getText()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //codice iva da presentare come default
                main.fileIni.setValue("iva", "codiceIvaDefault", this.texIvaDefault.getText());
                try {
                    DbUtils.tryExecQuery(Db.getConn(true), "update dati_azienda set codiceIvaDefault = " + Db.pcs(this.texIvaDefault.getText()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            main.fileIni.setValue("varie", "umpred", umpred.getText());

            if (chePrezziCliente.isSelected() == true) {
                main.fileIni.setValue("varie", "prezziCliente", "S");
            } else {
                main.fileIni.setValue("varie", "prezziCliente", "N");
            }

            if (cheSerie.isSelected() == true) {
                main.fileIni.setValue("varie", "campoSerie", "S");
            } else {
                main.fileIni.setValue("varie", "campoSerie", "N");
            }

            main.fileIni.setValue("personalizzazioni", "personalizzazioni", this.texPersonalizzazioni.getText());
            main.fileIni.setValue("varie", "messaggioStampa", texMessaggioStampa.getText());
            main.fileIni.setValue("varie", "int_dest_1", int_dest_1.getText());
            main.fileIni.setValue("varie", "int_dest_2", int_dest_2.getText());
            main.fileIni.setValue("pref", "noteStandard", texNoteStandard.getText());

            main.fileIni.setValue("pref", "generazione_movimenti", String.valueOf(generazione_movimenti.getSelectedIndex()));
            main.fileIni.setValue("pref", "raggruppa_articoli", String.valueOf(raggruppa_articoli.getSelectedIndex()));
            main.fileIni.setValue("pref", "riporta_serie", String.valueOf(riportaSerie.getSelectedIndex()));

            main.fileIni.setValue("pref", "stato_vs_ordine", String.valueOf(vostro_ordine.getSelectedIndex()));

            if (Db.conn != null && !Db.conn.isClosed()) {
                if (stato_ordi_post.getSelectedItem().toString().equalsIgnoreCase("<lascia invariato>")) {
                    main.fileIni.removeKey("pref", "stato_ordi");
                } else {
                    main.fileIni.setValue("pref", "stato_ordi", String.valueOf(stato_ordi_post.getSelectedItem()));
                }
            }

            main.fileIni.setValue("altro", "da_ddt", da_ddt.getText());
            main.fileIni.setValue("altro", "da_ordine", da_ordine.getText());

            main.fileIni.setValue("readytec", "codiceIvaDefault", this.texCodIvaReadytec.getText());
            main.fileIni.setValue("readytec", "codiceContoDefault", this.texContoRicaviReadytec.getText());

            main.fileIni.saveFile();

            //altri parametri salvati su preferenze utente
            try {

//                java.util.prefs.Preferences preferences = java.util.prefs.Preferences.userNodeForPackage(main.class);

                // Save the window location and size
//                preferences.putBoolean("visualizzaTotali", this.cheTotali.isSelected());
                main.fileIni.setValue("pref", "visualizzaTotali", this.cheTotali.isSelected());

//                preferences.putBoolean("stampaCellulare", this.cheStampaCellulare.isSelected());
                main.fileIni.setValue("pref", "stampaCellulare", this.cheStampaCellulare.isSelected());
                main.fileIni.setValue("pref", "stampaTelefono", this.cheStampaTelefono.isSelected());

                main.fileIni.setValue("pref", "stampaPivaSotto", cheStampaPivaSotto.isSelected());

//                preferences.putBoolean("stampaDestDiversaSotto", this.cheStampaDestDiversaSotto.isSelected());
                main.fileIni.setValue("pref", "stampaDestDiversaSotto", this.cheStampaDestDiversaSotto.isSelected());

//                preferences.putBoolean("stampaPdf", this.cheStampaPdf.isSelected());
                main.fileIni.setValue("pref", "stampaPdf", this.cheStampaPdf.isSelected());

                main.fileIni.setValue("pref", "inclNumProForma", this.cheInclNumProForma.isSelected());  // dm-edit-20081215

//                preferences.putBoolean("azioniPericolose", cheAzioniPericolose.isSelected());
                main.fileIni.setValue("pref", "azioniPericolose", cheAzioniPericolose.isSelected());

                main.fileIni.setValue("pref", "numerazioneNoteCredito", cheNumerazioneNoteCredito.isSelected());

                main.fileIni.setValue("pref", "visualizzaAnnoInCorso", cheVisuAnno.isSelected());

                main.fileIni.setValue("pref", "attivaArrotondamento", cheArrotondamento.isSelected());

                main.fileIni.setValue("pref", "provvigioniAutomatiche", cheProvvigioniAuto.isSelected());

                main.fileIni.setValue("pref", "provvigioniPercentualeAuto", chePercStandard.isSelected());

                main.fileIni.setValue("pref", "listinoConsigliatoDdt", String.valueOf(listino_consigliato.getSelectedKey()));

                //preferences.put("noteStandard", this.texNoteStandard.getText());

                if (texLimit.getText().length() == 0) {
//                    preferences.put("limit", "");
                    main.fileIni.setValue("pref", "limit", "");
                } else {
//                    preferences.put("limit", this.texLimit.getText());
                    main.fileIni.setValue("pref", "limit", this.texLimit.getText());
                }

//                preferences.putBoolean("soloItaliano", this.cheSoloItaliano.isSelected());
                main.fileIni.setValue("pref", "soloItaliano", this.cheSoloItaliano.isSelected());

//                preferences.putBoolean("multiriga", this.cheMultiriga.isSelected());
//                main.fileIni.setValue("pref", "multiriga", this.cheMultiriga.isSelected());

//                preferences.put("tipoStampa", String.valueOf(comTipoStampa1.getSelectedItem()));
                main.fileIni.setValue("pref", "tipoStampa", String.valueOf(comTipoStampa1.getSelectedItem()));

//                preferences.put("tipoStampaFA", String.valueOf(comTipoStampa.getSelectedItem()));
                main.fileIni.setValue("pref", "tipoStampaFA", String.valueOf(comTipoStampa.getSelectedItem()));

//                preferences.put("tipoStampaDDT", String.valueOf(comTipoStampaDdt.getSelectedItem()));
                main.fileIni.setValue("pref", "tipoStampaDDT", String.valueOf(comTipoStampaDdt.getSelectedItem()));

//                preferences.put("tipoStampaOrdine", String.valueOf(comTipoStampaOrdine.getSelectedItem()));
                main.fileIni.setValue("pref", "tipoStampaOrdine", String.valueOf(comTipoStampaOrdine.getSelectedItem()));

                main.fileIni.setValue("pref", "stampaCedoliniBonifici", cheStampaCedoliniBonifici.isSelected());

//                main.fileIni.setValue("pref", "nascondiDonazione", cheNascondiDonazione.isSelected());
                main.fileIni.setValue("pref", "scadenzeOrdini", cheScadenzeOrdini.isSelected());
                main.fileIni.setValue("pref", "richiediPassword", cheRichiediPassword.isSelected());

                main.fileIni.setValue("pref", "controlliIva", cheControlliIva.isSelected());
                main.fileIni.setValue("pref", "ricercaPerPersona", chePersona.isSelected());
                main.fileIni.setValue("pref", "updateListini", cheUpdateListini.isSelected());
                main.fileIni.setValue("pref", "controlloNumeriAvvio", cheControlloNumeriAvvio.isSelected());

                main.fileIni.setValue("barcode", "per_quantita", comStampaBarcode.getSelectedIndex() == 1);
                main.fileIni.setValue("barcode", "testo_libero", texFreeBarcode.getText());
                main.fileIni.setValue("barcode", "stampa_cod_articolo", cheBarcodeCodArticolo.isSelected());
                main.fileIni.setValue("barcode", "stampa_prezzo_articolo", cheBarcodePrezzoArticolo.isSelected());
                main.fileIni.setValue("barcode", "stampa_qta_articolo", cheBarcodeQtaArticolo.isSelected());
                main.fileIni.setValue("barcode", "draw_text", cheBarcodeDrawtext.isSelected());
                main.fileIni.setValue("barcode", "articolo_sopra", comBarcodeDisposizione.getSelectedIndex() == 0);
                main.fileIni.setValue("barcode", "iva_inclusa", comBarcodeTipoPrezzo.getSelectedIndex() == 0);
                main.fileIni.setValue("barcode", "righe", String.valueOf(comBarcodeRighe.getSelectedItem()));
                main.fileIni.setValue("barcode", "colonne", String.valueOf(comBarcodeColonne.getSelectedItem()));
                main.fileIni.setValue("barcode", "formato", String.valueOf(comBarcodeFormato.getSelectedItem()));
                main.fileIni.setValue("barcode", "mtop", String.valueOf(mtop.getText()));
                main.fileIni.setValue("barcode", "mbottom", String.valueOf(mbottom.getText()));
                main.fileIni.setValue("barcode", "mleft", String.valueOf(mleft.getText()));
                main.fileIni.setValue("barcode", "mright", String.valueOf(mright.getText()));
                main.fileIni.setValue("barcode", "pagina_larghezza", String.valueOf(plarghezza.getText()));
                main.fileIni.setValue("barcode", "pagina_altezza", String.valueOf(paltezza.getText()));

                if (main.getPersonalContain("emicad")) {
                    main.fileIni.setValue("emicad", "attiva", cheAttivaEmiCad.isSelected());
                    main.fileIni.setValue("emicad", "richiedi", cheEmiCadRichiedi.isSelected());
                    main.fileIni.setValue("emicad", "file_pre", texEmiCadFilePre.getText());
                    main.fileIni.setValue("emicad", "file_post", texEmiCadFilePost.getText());
                }

                //main.fileIni.setValue("gestione_utenti", "attiva", cheAttivaUtenti.isSelected());
                //codice iva per le spese se vuoto vengono ripartizionate
                if (Db.conn != null && !Db.conn.isClosed()) {
                    try {
                        DbUtils.tryExecQuery(Db.getConn(true), "update dati_azienda set gestione_utenti = " + (cheAttivaUtenti.isSelected() ? "1" : "0"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //font
                try {
                    if (font != null) {
                        main.fileIni.setValue("pref", "font_family", font.getFamily());
                        main.fileIni.setValue("pref", "font_size", font.getSize());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                try {
                    InvoicexEvent event = new InvoicexEvent(this);
                    event.type = InvoicexEvent.TYPE_IMPOSTAZIONI_SALVA;
                    main.events.fireInvoicexEvent(event);
                } catch (Exception err) {
                    err.printStackTrace();
                }
//                preferences.sync();
            } catch (Exception err) {
                err.printStackTrace();
            }
        } catch (Exception err) {
            err.printStackTrace();
        }

        //aggiorno impostazioni
        try {
            if (main.fileIni.getValue("varie", "prezziCliente").equalsIgnoreCase("S")) {
                main.iniPrezziCliente = true;
            } else {
                main.iniPrezziCliente = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //azzero cache loghi
        JRDSInvoice.filescaricati.clear();
        Db.cache.clear();

        SwingUtils.mouse_def(this);

        dispose();

        if (!vaisudb) {
            SwingUtils.showInfoMessage(main.getPadreFrame(), "Per rendere attive le modifiche riavviare il programma");
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void rimuoviActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rimuoviActionPerformed
        try {
            String key = ((JPopupMenu) ((JMenuItem) evt.getSource()).getParent()).getInvoker().getName();
            Map<String, JTextField> mtex = new HashMap<String, JTextField>();
            Map<String, JLabel> mlab = new HashMap<String, JLabel>();
            mtex.put("logo", tnxFileLogo);
            mtex.put("logo_email", tnxFileLogo1);
            mtex.put("sfondo", tnxFileSfondo);
            mtex.put("sfondo_email", tnxFileSfondoPdf);
            mlab.put("logo", logo);
            mlab.put("logo_email", logo_email);
            mlab.put("sfondo", sfondo);
            mlab.put("sfondo_email", sfondo_email);

            mtex.get(key).setText(null);
            mtex.get(key).setToolTipText(null);
            mlab.get(key).setIcon(null);

            Field f = getClass().getField("salvare_" + key);
            f.setBoolean(this, true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_rimuoviActionPerformed

    private void butImgFirmaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butImgFirmaActionPerformed
        JFileChooser fileChoose = new JFileChooser();
        FileFilter filter1 = new FileFilter() {
            public boolean accept(File pathname) {
                if (pathname.getAbsolutePath().endsWith(".jpg")
                        || pathname.getAbsolutePath().endsWith(".jpeg")
                        || pathname.getAbsolutePath().endsWith(".gif")
                        || pathname.getAbsolutePath().endsWith(".bmp")
                        || pathname.getAbsolutePath().endsWith(".png")
                        || pathname.isDirectory()) {
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public String getDescription() {
                return "File Immagine (*.jpg, *.jpeg, *.gif, *.png, *.bmp)";
            }
        };

        fileChoose.addChoosableFileFilter(filter1);
        fileChoose.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        ImagePreviewPanel preview = new ImagePreviewPanel();
        fileChoose.setAccessory(preview);
        fileChoose.addPropertyChangeListener(preview);

        //        JFileChooser fileChoose = SwingUtils.getFileOpen(this);
        //fileChoose.setCurrentDirectory(new java.io.File("c:\\"));

        int ret = fileChoose.showOpenDialog(this);

        if (ret == javax.swing.JFileChooser.APPROVE_OPTION) {
            //apro il file
            this.texImgFirma.setText(fileChoose.getSelectedFile().getAbsolutePath());
            texImgFirma.setToolTipText(updateLogoPreview(texImgFirma.getText()));
        }
}//GEN-LAST:event_butImgFirmaActionPerformed

    private void butEmiCadFilePostActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butEmiCadFilePostActionPerformed
        JFileChooser fileChoose = new JFileChooser();
        FileFilter filter1 = new FileFilter() {
            public boolean accept(File pathname) {
                if (pathname.getAbsolutePath().endsWith(".pdf") || pathname.isDirectory()) {
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public String getDescription() {
                return "File PDF (*.pdf)";
            }
        };

        fileChoose.setFileFilter(filter1);
        int ret = fileChoose.showDialog(main.getPadreFrame(), "Imposta Primo File");

        if (ret == JFileChooser.APPROVE_OPTION) {
            File f = fileChoose.getSelectedFile();
            texEmiCadFilePost.setText(f.getAbsolutePath());
        }
}//GEN-LAST:event_butEmiCadFilePostActionPerformed

    private void butEmiCadFilePreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butEmiCadFilePreActionPerformed
        JFileChooser fileChoose = new JFileChooser();
        FileFilter filter1 = new FileFilter() {
            public boolean accept(File pathname) {
                if (pathname.getAbsolutePath().endsWith(".pdf") || pathname.isDirectory()) {
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public String getDescription() {
                return "File PDF (*.pdf)";
            }
        };

        fileChoose.setFileFilter(filter1);
        int ret = fileChoose.showDialog(main.getPadreFrame(), "Imposta Primo File");

        if (ret == JFileChooser.APPROVE_OPTION) {
            File f = fileChoose.getSelectedFile();
            texEmiCadFilePre.setText(f.getAbsolutePath());
        }
}//GEN-LAST:event_butEmiCadFilePreActionPerformed

    private void texEmiCadFilePostFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texEmiCadFilePostFocusLost
        String path = texEmiCadFilePost.getText();
        if (!path.equals("")) {
            File f = new File(path);
            if (!f.exists()) {
                SwingUtils.showInfoMessage(this, "Il file specificato non esiste!", "File Inesistente");
                texEmiCadFilePost.setText("");
            }
        }
}//GEN-LAST:event_texEmiCadFilePostFocusLost

    private void texEmiCadFilePreFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_texEmiCadFilePreFocusLost
        String path = texEmiCadFilePre.getText();
        if (!path.equals("")) {
            File f = new File(path);
            if (!f.exists()) {
                SwingUtils.showInfoMessage(this, "Il file specificato non esiste!", "File Inesistente");
                texEmiCadFilePre.setText("");
            }
        }
}//GEN-LAST:event_texEmiCadFilePreFocusLost

    private void cheEmiCadRichiediActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cheEmiCadRichiediActionPerformed
        boolean attiva = !cheEmiCadRichiedi.isSelected();

        texEmiCadFilePre.setEnabled(attiva);
        texEmiCadFilePost.setEnabled(attiva);
        butEmiCadFilePre.setEnabled(attiva);
        butEmiCadFilePost.setEnabled(attiva);
}//GEN-LAST:event_cheEmiCadRichiediActionPerformed

    private void cheAttivaEmiCadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cheAttivaEmiCadActionPerformed
        boolean attiva = cheAttivaEmiCad.isSelected();

        cheEmiCadRichiedi.setEnabled(attiva);
        texEmiCadFilePre.setEnabled(attiva);
        texEmiCadFilePost.setEnabled(attiva);
        butEmiCadFilePre.setEnabled(attiva);
        butEmiCadFilePost.setEnabled(attiva);

        if (attiva == true) {
            cheEmiCadRichiediActionPerformed(null);
        }
}//GEN-LAST:event_cheAttivaEmiCadActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        frmModificaSoglie frm = new frmModificaSoglie(this, true);
        frm.setLocationRelativeTo(null);
        frm.setVisible(true);
}//GEN-LAST:event_jButton6ActionPerformed

    private void chePercStandardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chePercStandardActionPerformed
        updateTabellaSoglieProvvigioni(false);
}//GEN-LAST:event_chePercStandardActionPerformed

    private void cheProvvigioniAutoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cheProvvigioniAutoItemStateChanged
        griglia_soglie.setEditable(evt.getStateChange() == evt.SELECTED);
        chePercStandard.setEnabled(evt.getStateChange() == evt.SELECTED);
}//GEN-LAST:event_cheProvvigioniAutoItemStateChanged

    private void aperturaImpostaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aperturaImpostaActionPerformed
        String os = System.getProperty("os.name").toLowerCase();
        if (os.startsWith("windows")) {
            aperturaText1.setText("explorer \"{file}\"");
            aperturaText2.setText("cmd /C \"{file}\"");
            aperturaText3.setText("cmd /C \"{file}\"");
        } else if (os.startsWith("mac")) {
            aperturaText1.setText("open {file}");
            aperturaText2.setText("open {file}");
            aperturaText3.setText("open {file}");
        } else {
            //gvfs-open TODO
            aperturaText1.setText("gnome-open {file}");
            aperturaText2.setText("gnome-open {file}");
            aperturaText3.setText("gnome-open {file}");
        }
}//GEN-LAST:event_aperturaImpostaActionPerformed

    private void aperturaFileItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_aperturaFileItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            aggiornaAperturaFile();
        }
}//GEN-LAST:event_aperturaFileItemStateChanged

    private void comFileSfondoEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comFileSfondoEmailActionPerformed
//        JFileChooser fileChoose = new JFileChooser();
        FileDialog dlgChoose = new FileDialog(main.getPadreFrame());
        dlgChoose.setMode(FileDialog.LOAD);

        String path = main.prefs.get("impostazioni_path_chooser_immagine", null);
        if (path != null) {
//            fileChoose.setCurrentDirectory(new File(path));
            dlgChoose.setDirectory(path);
        }

//        FileFilter filter1 = new FileFilter() {
//            public boolean accept(File pathname) {
//                if (pathname.getAbsolutePath().endsWith(".jpg")
//                        || pathname.getAbsolutePath().endsWith(".jpeg")
//                        || pathname.getAbsolutePath().endsWith(".gif")
//                        || pathname.getAbsolutePath().endsWith(".bmp")
//                        || pathname.getAbsolutePath().endsWith(".png")
//                        || pathname.isDirectory()) {
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//
//            @Override
//            public String getDescription() {
//                return "File Immagine (*.jpg, *.jpeg, *.gif, *.png, *.bmp)";
//            }
//        };
//
//        fileChoose.addChoosableFileFilter(filter1);
//        fileChoose.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
//        ImagePreviewPanel preview = new ImagePreviewPanel();
//        fileChoose.setAccessory(preview);
//        fileChoose.addPropertyChangeListener(preview);
        //JFileChooser fileChoose = SwingUtils.getFileOpen(this);
        //fileChoose.setCurrentDirectory(new java.io.File("c:\\"));

//        int ret = fileChoose.showOpenDialog(this);
        
        dlgChoose.setTitle("Seleziona un' immagine");
        dlgChoose.setVisible(true);
        String retfile = dlgChoose.getFile();        

//        if (ret == javax.swing.JFileChooser.APPROVE_OPTION) {
        if (retfile != null) {
            String retfile2 = retfile.toLowerCase();
            if (!(retfile2.endsWith(".jpg")
                    || retfile2.endsWith(".jpeg")
                    || retfile2.endsWith(".gif")
                    || retfile2.endsWith(".png")
                    || retfile2.endsWith(".bmp"))) {
                SwingUtils.showWarningMessage(this, "Il file '" + retfile2 + "' non è un immagine supportata (jpg, gif, png, bmp)");
                return;                
            }
            
            //controllo dimensioni
//            File ftest = fileChoose.getSelectedFile();
            String file = dlgChoose.getDirectory() + retfile;
            File ftest = new File(file);
            
            try {
                String ext = FileUtils.getExt(ftest);
                if (ext != null && ext.trim().length() > 0) {
                    ImageReader ir = ImageIO.getImageReadersBySuffix(FileUtils.getExt(ftest)).next();
                    ImageInputStream iis = ImageIO.createImageInputStream(ftest);
                    ir.setInput(iis);
                    if (tnxFileSfondo.getText().length() > 0 && (ir.getWidth(0) * ir.getHeight(0)) > (1400 * 2000)) {
                        SwingUtils.showWarningMessage(this, "Il file di sfondo è troppo grande (il limite è 1400 * 2000 px)");
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            salvare_sfondo_email = true;
            tnxFileSfondoPdf.setText(ftest.getAbsolutePath());
            tnxFileSfondoPdf.setToolTipText(updateLogoPreview(tnxFileSfondoPdf.getText()));
            caricaLogo(tnxFileSfondoPdf.getText(), sfondo_email, "sfondo_email");
        }

//        main.prefs.put("impostazioni_path_chooser_immagine", fileChoose.getCurrentDirectory().getAbsolutePath());
        main.prefs.put("impostazioni_path_chooser_immagine", dlgChoose.getDirectory());
}//GEN-LAST:event_comFileSfondoEmailActionPerformed

    private void sfondo_emailMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sfondo_emailMouseReleased
        if (evt.isPopupTrigger()) {
            popuprimuovi.show(evt.getComponent(), evt.getX(), evt.getY());
        }
}//GEN-LAST:event_sfondo_emailMouseReleased

    private void sfondo_emailMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sfondo_emailMousePressed
        if (evt.isPopupTrigger()) {
            popuprimuovi.show(evt.getComponent(), evt.getX(), evt.getY());
        }
}//GEN-LAST:event_sfondo_emailMousePressed

    private void comFileSfondoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comFileSfondoActionPerformed
//        JFileChooser fileChoose = new JFileChooser();
        FileDialog dlgChoose = new FileDialog(main.getPadreFrame());
        dlgChoose.setMode(FileDialog.LOAD);

        String path = main.prefs.get("impostazioni_path_chooser_immagine", null);
        if (path != null) {
//            fileChoose.setCurrentDirectory(new File(path));
            dlgChoose.setDirectory(path);
        }

//        FileFilter filter1 = new FileFilter() {
//            public boolean accept(File pathname) {
//                if (pathname.getAbsolutePath().endsWith(".jpg")
//                        || pathname.getAbsolutePath().endsWith(".jpeg")
//                        || pathname.getAbsolutePath().endsWith(".gif")
//                        || pathname.getAbsolutePath().endsWith(".bmp")
//                        || pathname.getAbsolutePath().endsWith(".png")
//                        || pathname.isDirectory()) {
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//
//            @Override
//            public String getDescription() {
//                return "File Immagine (*.jpg, *.jpeg, *.gif, *.png, *.bmp)";
//            }
//        };
//
//        fileChoose.addChoosableFileFilter(filter1);
//        fileChoose.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
//        ImagePreviewPanel preview = new ImagePreviewPanel();
//        fileChoose.setAccessory(preview);
//        fileChoose.addPropertyChangeListener(preview);
        //JFileChooser fileChoose = SwingUtils.getFileOpen(this);
        //fileChoose.setCurrentDirectory(new java.io.File("c:\\"));

//        int ret = fileChoose.showOpenDialog(this);
        dlgChoose.setTitle("Seleziona un' immagine");
        dlgChoose.setVisible(true);
        String retfile = dlgChoose.getFile();        

//        if (ret == javax.swing.JFileChooser.APPROVE_OPTION) {
        if (retfile != null) {
            String retfile2 = retfile.toLowerCase();
            if (!(retfile2.endsWith(".jpg")
                    || retfile2.endsWith(".jpeg")
                    || retfile2.endsWith(".gif")
                    || retfile2.endsWith(".png")
                    || retfile2.endsWith(".bmp"))) {
                SwingUtils.showWarningMessage(this, "Il file '" + retfile2 + "' non è un immagine supportata (jpg, gif, png, bmp)");
                return;                
            }
        
            //controllo dimensioni
//            File ftest = fileChoose.getSelectedFile();
            String file = dlgChoose.getDirectory() + retfile;            
            File ftest = new File(file);
            try {
                String ext = FileUtils.getExt(ftest);
                if (ext != null && ext.trim().length() > 0) {
                    ImageReader ir = ImageIO.getImageReadersBySuffix(FileUtils.getExt(ftest)).next();
                    ImageInputStream iis = ImageIO.createImageInputStream(ftest);
                    ir.setInput(iis);
                    if (tnxFileSfondo.getText().length() > 0 && (ir.getWidth(0) * ir.getHeight(0)) > (1400 * 2000)) {
                        SwingUtils.showWarningMessage(this, "Il file di sfondo è troppo grande (il limite è 1400 * 2000 px)");
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            salvare_sfondo = true;

            //apro il file
            tnxFileSfondo.setText(ftest.getAbsolutePath());
            tnxFileSfondo.setToolTipText(updateLogoPreview(tnxFileSfondo.getText()));
            caricaLogo(tnxFileSfondo.getText(), sfondo, "sfondo");
            if (JOptionPane.showConfirmDialog(this, "Vuoi impostarlo anche per la creazione dei PDF ?\n(Solitamente sì)", "Attenzione", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                salvare_sfondo_email = true;
                tnxFileSfondoPdf.setText(ftest.getAbsolutePath());
                tnxFileSfondoPdf.setToolTipText(updateLogoPreview(tnxFileSfondoPdf.getText()));
                caricaLogo(tnxFileSfondoPdf.getText(), sfondo_email, "sfondo_email");
            }
        }

//        main.prefs.put("impostazioni_path_chooser_immagine", fileChoose.getCurrentDirectory().getAbsolutePath());
        main.prefs.put("impostazioni_path_chooser_immagine", dlgChoose.getDirectory());
}//GEN-LAST:event_comFileSfondoActionPerformed

    private void sfondoMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sfondoMouseReleased
        if (evt.isPopupTrigger()) {
            popuprimuovi.show(evt.getComponent(), evt.getX(), evt.getY());
        }
}//GEN-LAST:event_sfondoMouseReleased

    private void sfondoMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sfondoMousePressed
        if (evt.isPopupTrigger()) {
            popuprimuovi.show(evt.getComponent(), evt.getX(), evt.getY());
        }
}//GEN-LAST:event_sfondoMousePressed

    private void da_ordineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_da_ordineActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_da_ordineActionPerformed

    private void tnxFileSfondoPdfKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tnxFileSfondoPdfKeyReleased
        tnxFileSfondoPdf.setToolTipText(updateLogoPreview(tnxFileSfondoPdf.getText()));
}//GEN-LAST:event_tnxFileSfondoPdfKeyReleased

    private void tnxFileSfondoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tnxFileSfondoKeyReleased
        tnxFileSfondo.setToolTipText(updateLogoPreview(tnxFileSfondo.getText()));
}//GEN-LAST:event_tnxFileSfondoKeyReleased

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if (SwingUtils.showYesNoMessage(this, "Attenzione, con questa procedura cancellerai tutti i tuoi dati, sicuro di proseguire ?")) {
            main.stopdb(false);
            main.wizard_in_corso = true;
            dispose();
            try {
                main.getPadre().dispose();
            } catch (Exception e) {
            }
            WizardDb wdb = new WizardDb();
            wdb.start(true);
        }
}//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        JFileChooser fileChoose = new JFileChooser();
        FileFilter filter1 = new FileFilter() {
            public boolean accept(File pathname) {
                if (pathname.getAbsolutePath().endsWith(".jrxml") || pathname.isDirectory()) {
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public String getDescription() {
                return "File JRXML (*.jrxml)";
            }
        };

        fileChoose.addChoosableFileFilter(filter1);
        fileChoose.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        int ret = fileChoose.showOpenDialog(this);

        if (ret == javax.swing.JFileChooser.APPROVE_OPTION) {
            JDialogImportaReport dialogimp = new JDialogImportaReport(this, true, this, new File(fileChoose.getSelectedFile().getAbsolutePath()));
            dialogimp.setLocationRelativeTo(null);
            dialogimp.setVisible(true);
        }
}//GEN-LAST:event_jButton4ActionPerformed

    private void btnPreview3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreview3ActionPerformed
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        printElement(3);
}//GEN-LAST:event_btnPreview3ActionPerformed

    private void btnPreview2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreview2ActionPerformed
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        printElement(2);
}//GEN-LAST:event_btnPreview2ActionPerformed

    private void btnPreview1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreview1ActionPerformed
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        printElement(1);
}//GEN-LAST:event_btnPreview1ActionPerformed

    private void btnPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviewActionPerformed
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        printElement(0);
}//GEN-LAST:event_btnPreviewActionPerformed

    private void select_truststoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_select_truststoreActionPerformed
        final JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            ssl_truststore.setText(file.getAbsolutePath());
        }
}//GEN-LAST:event_select_truststoreActionPerformed

    private void cheRichiediPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cheRichiediPasswordActionPerformed
        if (cheAttivaUtenti.isSelected()) {
            if (SwingUtils.showYesNoMessage(this, "Attivando questa opzione verrà disattivato l'accesso multi utente. Vuoi Proseguire?", "Accesso")) {
                cheAttivaUtenti.setSelected(false);
            }
        }
}//GEN-LAST:event_cheRichiediPasswordActionPerformed

    private void cheAzioniPericoloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cheAzioniPericoloseActionPerformed
        if (cheAzioniPericolose.isSelected()) {
            String msg = "Abilitando quest' opzione potrai mettere i numeri\nsui documenti o eliminarli senza i controlli del programma\nSei sicuro ?";
            int ret = JOptionPane.showConfirmDialog(this, msg, "Attenzione", JOptionPane.YES_NO_OPTION);
            if (ret == JOptionPane.NO_OPTION) {
                cheAzioniPericolose.setSelected(false);
            }
        }
}//GEN-LAST:event_cheAzioniPericoloseActionPerformed

    private void cheAvvioDbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cheAvvioDbActionPerformed

        if (cheAvvioDb.isSelected()) {
            oldHost = texHost.getText();
            oldDb = texDb.getText();
            oldId = texId.getText();
            oldPwd = texPwd.getText();
            texHost.setText("127.0.0.1");
            texDb.setText("invoicex_default");
            texId.setText("root");
            if (main.fileIni.existKey("db", "pwd_interno")) {
                texPwd.setText(main.fileIni.getValueCifrato("db", "pwd_interno"));
            } else {
                texPwd.setText("ohfgfesmmc666");
            }
            texHost.setEnabled(false);
            texDb.setEnabled(false);
            texId.setEnabled(false);
            texPwd.setEnabled(false);
        } else {
            texHost.setText(oldHost);
            texDb.setText(oldDb);
            texId.setText(oldId);
            texPwd.setText(oldPwd);
            texHost.setEnabled(true);
            texDb.setEnabled(true);
            texId.setEnabled(true);
            texPwd.setEnabled(true);
        }
}//GEN-LAST:event_cheAvvioDbActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        JDialogLogoResize dialog = new JDialogLogoResize(main.getPadre(), true);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
}//GEN-LAST:event_jButton5ActionPerformed

    private void comFile1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comFile1ActionPerformed
//        JFileChooser fileChoose = new JFileChooser();
        FileDialog dlgChoose = new FileDialog(main.getPadreFrame());

        String path = main.prefs.get("impostazioni_path_chooser_immagine", null);
        if (path != null) {
//            fileChoose.setCurrentDirectory(new File(path));
            dlgChoose.setDirectory(path);
        }

//        FileFilter filter1 = new FileFilter() {
//            public boolean accept(File pathname) {
//                if (pathname.getAbsolutePath().endsWith(".jpg")
//                        || pathname.getAbsolutePath().endsWith(".jpeg")
//                        || pathname.getAbsolutePath().endsWith(".gif")
//                        || pathname.getAbsolutePath().endsWith(".bmp")
//                        || pathname.getAbsolutePath().endsWith(".png")
//                        || pathname.isDirectory()) {
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//
//            @Override
//            public String getDescription() {
//                return "File Immagine (*.jpg, *.jpeg, *.gif, *.png, *.bmp)";
//            }
//        };
//
//        fileChoose.addChoosableFileFilter(filter1);
//        fileChoose.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
//        ImagePreviewPanel preview = new ImagePreviewPanel();
//        fileChoose.setAccessory(preview);
//        fileChoose.addPropertyChangeListener(preview);
        //    JFileChooser fileChoose = SwingUtils.getFileOpen(this);
        //fileChoose.setCurrentDirectory(new java.io.File("c:\\"));

//        int ret = fileChoose.showOpenDialog(this);

        dlgChoose.setTitle("Seleziona un' immagine");
        dlgChoose.setVisible(true);
        String retfile = dlgChoose.getFile();

//        if (ret == javax.swing.JFileChooser.APPROVE_OPTION) {
        if (retfile != null) {
            String retfile2 = retfile.toLowerCase();
            if (!(retfile2.endsWith(".jpg")
                    || retfile2.endsWith(".jpeg")
                    || retfile2.endsWith(".gif")
                    || retfile2.endsWith(".png")
                    || retfile2.endsWith(".bmp"))) {
                SwingUtils.showWarningMessage(this, "Il file '" + retfile2 + "' non è un immagine supportata (jpg, gif, png, bmp)");
                return;                
            }
        
            //controllo
//            String file = fileChoose.getSelectedFile().getAbsolutePath();
            String file = dlgChoose.getDirectory() + retfile;            
            File ftest1 = new File(file);
            //check image size
            try {
                SimpleImageInfo sii = new SimpleImageInfo(ftest1);
                System.out.println("img w: " + sii.getWidth() + " h: " + sii.getHeight() + " mime: " + sii.getMimeType());
                if (!main.debug) {
                    if ((sii.getWidth() * sii.getHeight()) > (3000 * 2000)) {
                        SwingUtils.showWarningMessage(this, "Il file è troppo grande (il limite è 3000 * 2000 px)");
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            salvare_logo_email = true;
            //apro il file
            tnxFileLogo1.setText(ftest1.getAbsolutePath());
            tnxFileLogo1.setToolTipText(updateLogoPreview(tnxFileLogo1.getText()));
            caricaLogo(tnxFileLogo1.getText(), logo_email, "logo_email");
        }

//        main.prefs.put("impostazioni_path_chooser_immagine", fileChoose.getCurrentDirectory().getAbsolutePath());
        main.prefs.put("impostazioni_path_chooser_immagine", dlgChoose.getDirectory());        
    }//GEN-LAST:event_comFile1ActionPerformed

    private void tnxFileLogo1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tnxFileLogo1KeyReleased
        tnxFileLogo1.setToolTipText(updateLogoPreview(tnxFileLogo1.getText()));
}//GEN-LAST:event_tnxFileLogo1KeyReleased

    private void logo_emailMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logo_emailMouseReleased
        if (evt.isPopupTrigger()) {
            popuprimuovi.show(evt.getComponent(), evt.getX(), evt.getY());
        }
}//GEN-LAST:event_logo_emailMouseReleased

    private void logo_emailMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logo_emailMousePressed
        if (evt.isPopupTrigger()) {
            popuprimuovi.show(evt.getComponent(), evt.getX(), evt.getY());
        }
}//GEN-LAST:event_logo_emailMousePressed

    private void comFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comFileActionPerformed
//        JFileChooser fileChoose = new JFileChooser();
        FileDialog dlgChoose = new FileDialog(main.getPadreFrame());
        dlgChoose.setMode(FileDialog.LOAD);
                
        String path = main.prefs.get("impostazioni_path_chooser_immagine", null);
        if (path != null) {
//            fileChoose.setCurrentDirectory(new File(path));
            dlgChoose.setDirectory(path);
        }

//        FileFilter filter1 = new FileFilter() {
//            public boolean accept(File pathname) {
//                if (pathname.getAbsolutePath().endsWith(".jpg")
//                        || pathname.getAbsolutePath().endsWith(".jpeg")
//                        || pathname.getAbsolutePath().endsWith(".gif")
//                        || pathname.getAbsolutePath().endsWith(".bmp")
//                        || pathname.getAbsolutePath().endsWith(".png")
//                        || pathname.isDirectory()) {
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//
//            @Override
//            public String getDescription() {
//                return "File Immagine (*.jpg, *.jpeg, *.gif, *.png, *.bmp)";
//            }
//        };

//        fileChoose.addChoosableFileFilter(filter1);
//        fileChoose.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
//        ImagePreviewPanel preview = new ImagePreviewPanel();
//        fileChoose.setAccessory(preview);
//        fileChoose.addPropertyChangeListener(preview);

        //        JFileChooser fileChoose = SwingUtils.getFileOpen(this);
        //fileChoose.setCurrentDirectory(new java.io.File("c:\\"));

//        int ret = fileChoose.showOpenDialog(this);
        dlgChoose.setTitle("Seleziona un' immagine");
        dlgChoose.setVisible(true);
        String retfile = dlgChoose.getFile();        

//        if (ret == javax.swing.JFileChooser.APPROVE_OPTION) {
        if (retfile != null) {
            String retfile2 = retfile.toLowerCase();
            if (!(retfile2.endsWith(".jpg")
                    || retfile2.endsWith(".jpeg")
                    || retfile2.endsWith(".gif")
                    || retfile2.endsWith(".png")
                    || retfile2.endsWith(".bmp"))) {
                SwingUtils.showWarningMessage(this, "Il file '" + retfile2 + "' non è un immagine supportata (jpg, gif, png, bmp)");
                return;                
            }
            
            //controllo
//            String file = fileChoose.getSelectedFile().getAbsolutePath();
            String file = dlgChoose.getDirectory() + retfile;
            File ftest1 = new File(file);
            //check image size
            try {
                SimpleImageInfo sii = new SimpleImageInfo(ftest1);
                System.out.println("img w: " + sii.getWidth() + " h: " + sii.getHeight() + " mime: " + sii.getMimeType());
                if (!main.debug) {
                    if ((sii.getWidth() * sii.getHeight()) > (3000 * 2000)) {
                        SwingUtils.showWarningMessage(this, "Il file è troppo grande (il limite è 3000 * 2000 px)");
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            salvare_logo = true;
            //apro il file
            tnxFileLogo.setText(ftest1.getAbsolutePath());
            tnxFileLogo.setToolTipText(updateLogoPreview(tnxFileLogo.getText()));
            caricaLogo(tnxFileLogo.getText(), logo, "logo");
            if (JOptionPane.showConfirmDialog(this, "Vuoi impostarlo anche per la creazione dei PDF ?\n(Solitamente sì)", "Attenzione", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                salvare_logo_email = true;
                tnxFileLogo1.setText(ftest1.getAbsolutePath());
                tnxFileLogo1.setToolTipText(updateLogoPreview(tnxFileLogo1.getText()));
                caricaLogo(tnxFileLogo1.getText(), logo_email, "logo_email");
            }
        }

//        main.prefs.put("impostazioni_path_chooser_immagine", fileChoose.getCurrentDirectory().getAbsolutePath());
        main.prefs.put("impostazioni_path_chooser_immagine", dlgChoose.getDirectory());
    }//GEN-LAST:event_comFileActionPerformed

    private void tnxFileLogoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tnxFileLogoKeyReleased
        tnxFileLogo.setToolTipText(updateLogoPreview(tnxFileLogo.getText()));
}//GEN-LAST:event_tnxFileLogoKeyReleased

    private void logoMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoMouseReleased
        if (evt.isPopupTrigger()) {
            popuprimuovi.show(evt.getComponent(), evt.getX(), evt.getY());
        }
}//GEN-LAST:event_logoMouseReleased

    private void logoMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoMousePressed
        if (evt.isPopupTrigger()) {
            popuprimuovi.show(evt.getComponent(), evt.getX(), evt.getY());
        }
}//GEN-LAST:event_logoMousePressed

    private void logoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoMouseClicked
}//GEN-LAST:event_logoMouseClicked

    private void pulsanteFontActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pulsanteFontActionPerformed
        SwingUtils.mouse_wait(this);
        JDialogFontChooser dialog = new JDialogFontChooser(this, true);
        dialog.panel_font.setDefFont(main.def_font, main.fileIni.existKey("pref", "font_family") ? false : true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        if (dialog.ok) {
            font = new Font((String) dialog.panel_font.font_names.getSelectedItem(), Font.PLAIN, (Integer) dialog.panel_font.font_size.getValue());
            setPulsanteFont(font);
            riavviare = true;
        } else if (dialog.reimposta) {
            main.fileIni.removeKey("pref", "font_family");
            main.fileIni.removeKey("pref", "font_size");
            font = null;
            setPulsanteFont(font);
            riavviare = true;
        }
        SwingUtils.mouse_def(this);
}//GEN-LAST:event_pulsanteFontActionPerformed

    private void comLookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comLookActionPerformed
        if (!opening) {
            if (SwingUtils.showYesNoMessage(this, "Vuoi applicare subito il tema ?\nAttenzione, verranno chiuse tutte le finestre di Invoicex aperte.\nSi consiglia comunque di riavviare poi l'applicazione.", "Attenzione")) {
                //chiudo tutte
                JInternalFrame[] frames = main.getPadre().getDesktopPane().getAllFrames();
                for (JInternalFrame frame : frames) {
                    frame.dispose();
                }

                //look
                String look = CastUtils.toString(comLook.getSelectedItem());
                try {
                    if (look.equalsIgnoreCase("Simple")) {
                        System.out.println("Simple look and feel");
                        MetalTheme theme = new gestioneFatture.look.TnxSandTheme();
                        MetalLookAndFeel.setCurrentTheme(theme);
                        try {
                            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    } else if (look.equalsIgnoreCase("System")) {
                        String laf = UIManager.getSystemLookAndFeelClassName();
                        UIManager.setLookAndFeel(laf);
                    } else if (look.equalsIgnoreCase("Substance Nebula")) {
                        UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceNebulaLookAndFeel");
                    } else if (look.equalsIgnoreCase("Substance BusinessBlackSteel")) {
                        UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceBusinessBlueSteelLookAndFeel");
                    } else if (look.equalsIgnoreCase("Substance Creme")) {
                        UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceCremeLookAndFeel");
                    } else if (look.equalsIgnoreCase("Tonic")) {
                        UIManager.setLookAndFeel("com.digitprop.tonic.TonicLookAndFeel");
                    } else if (look.equalsIgnoreCase("Office 2003")) {
                        UIManager.setLookAndFeel("org.fife.plaf.Office2003.Office2003LookAndFeel");
                    } else if (look.equalsIgnoreCase("Office XP")) {
                        UIManager.setLookAndFeel("org.fife.plaf.OfficeXP.OfficeXPLookAndFeel");
                    } else if (look.equalsIgnoreCase("Visual Studio 2005")) {
                        UIManager.setLookAndFeel("org.fife.plaf.VisualStudio2005.VisualStudio2005LookAndFeel");
                    } else if (look.equalsIgnoreCase("JGoodies Plastic XP")) {
                        if (PlatformUtils.isMac()) {
                            UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticLookAndFeel");
                        } else {
                            UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
                        }
                    } else {
                        if (PlatformUtils.isMac()) {
                            UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceCremeLookAndFeel");
                        } else {
                            UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
                        }
                    }
                } catch (Exception err) {
                    err.printStackTrace();
                }

                if (UIManager.getLookAndFeel() instanceof PlasticLookAndFeel) {
                    PlasticXPLookAndFeel l = new PlasticXPLookAndFeel();
                    l.setPlasticTheme(new ExperienceBlue());
                    try {
                        UIManager.setLookAndFeel(l);
                    } catch (UnsupportedLookAndFeelException ex) {
                        ex.printStackTrace();
                    }
                }

                SwingUtilities.updateComponentTreeUI(this);
                SwingUtilities.updateComponentTreeUI(main.getPadre());
                validate();
                pack();

                if (UIManager.getLookAndFeel().getName().toLowerCase().indexOf("substance") >= 0) {
                    main.substance = true;
                } else {
                    main.substance = false;
                }
            }
        }

    }//GEN-LAST:event_comLookActionPerformed

    private void cheAttivaUtentiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cheAttivaUtentiActionPerformed
        if (cheRichiediPassword.isSelected()) {
            if (SwingUtils.showYesNoMessage(this, "Attivando questa opzione verrà disattivata la richiesta della password del database all'accesso. Vuoi Proseguire?", "Accesso")) {
                cheRichiediPassword.setSelected(false);
            }
        }
}//GEN-LAST:event_cheAttivaUtentiActionPerformed

    private void cheControlliIvaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cheControlliIvaActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_cheControlliIvaActionPerformed

    private void cheScadenzeOrdiniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cheScadenzeOrdiniActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_cheScadenzeOrdiniActionPerformed

    private void cheSerieActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cheSerieActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_cheSerieActionPerformed

    private void texContoRicaviReadytecActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_texContoRicaviReadytecActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_texContoRicaviReadytecActionPerformed

    private void comStampaBarcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comStampaBarcodeActionPerformed
        if (cheBarcodeQtaArticolo.isSelected() && comStampaBarcode.getSelectedIndex() == 1) {
            cheBarcodeQtaArticolo.setSelected(false);
        }

        cheBarcodeQtaArticolo.setEnabled(comStampaBarcode.getSelectedIndex() == 0);
    }//GEN-LAST:event_comStampaBarcodeActionPerformed

    private void cheBarcodePrezzoArticoloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cheBarcodePrezzoArticoloActionPerformed
        this.comBarcodeTipoPrezzo.setEnabled(cheBarcodePrezzoArticolo.isSelected());
    }//GEN-LAST:event_cheBarcodePrezzoArticoloActionPerformed

    public void printElement(int scelta) {
        String tipoStampa = "";

        if (scelta == 0) {
            tipoStampa = String.valueOf(comTipoStampa1.getSelectedItem());
        } else if (scelta == 1) {
            tipoStampa = String.valueOf(comTipoStampa.getSelectedItem());
        } else if (scelta == 2) {
            tipoStampa = String.valueOf(comTipoStampaDdt.getSelectedItem());
        } else if (scelta == 3) {
            tipoStampa = String.valueOf(comTipoStampaOrdine.getSelectedItem());
        }

        stampa(tipoStampa);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JDialogImpostazioni(new javax.swing.JFrame(), true).setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox aperturaFile;
    private javax.swing.JButton aperturaImposta;
    private javax.swing.JLabel aperturaLabel1;
    private javax.swing.JLabel aperturaLabel2;
    private javax.swing.JLabel aperturaLabel3;
    private javax.swing.JTextField aperturaText1;
    private javax.swing.JTextField aperturaText2;
    private javax.swing.JTextField aperturaText3;
    private javax.swing.JButton btnPreview;
    private javax.swing.JButton btnPreview1;
    private javax.swing.JButton btnPreview2;
    private javax.swing.JButton btnPreview3;
    private javax.swing.JButton butEmiCadFilePost;
    private javax.swing.JButton butEmiCadFilePre;
    private javax.swing.JButton butImgFirma;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox cheApriFinestreGrandi;
    private javax.swing.JCheckBox cheArrotondamento;
    private javax.swing.JCheckBox cheAttivaEmiCad;
    private javax.swing.JCheckBox cheAttivaUtenti;
    private javax.swing.JCheckBox cheAvvioDb;
    private javax.swing.JCheckBox cheAzioniPericolose;
    private javax.swing.JCheckBox cheBarcodeCodArticolo;
    private javax.swing.JCheckBox cheBarcodeDrawtext;
    private javax.swing.JCheckBox cheBarcodePrezzoArticolo;
    private javax.swing.JCheckBox cheBarcodeQtaArticolo;
    private javax.swing.JCheckBox cheControlliIva;
    private javax.swing.JCheckBox cheControlloNumeriAvvio;
    private javax.swing.JCheckBox cheEmiCadRichiedi;
    private javax.swing.JCheckBox cheInclNumProForma;
    private javax.swing.JCheckBox cheNonStampareLogo;
    private javax.swing.JCheckBox cheNonStampareLogo1;
    private javax.swing.JCheckBox cheNonStampareSfondo;
    private javax.swing.JCheckBox cheNonStampareSfondoPdf;
    private javax.swing.JCheckBox cheNumerazioneNoteCredito;
    private javax.swing.JCheckBox chePercStandard;
    private javax.swing.JCheckBox chePersona;
    private javax.swing.JCheckBox chePrezziCliente;
    private javax.swing.JCheckBox cheProvvigioniAuto;
    private javax.swing.JCheckBox cheRichiediPassword;
    private javax.swing.JCheckBox cheScadenzeOrdini;
    private javax.swing.JCheckBox cheSerie;
    private javax.swing.JCheckBox cheSoloItaliano;
    private javax.swing.JCheckBox cheStampaCedoliniBonifici;
    private javax.swing.JCheckBox cheStampaCellulare;
    private javax.swing.JCheckBox cheStampaDestDiversaSotto;
    private javax.swing.JCheckBox cheStampaPdf;
    private javax.swing.JCheckBox cheStampaPivaSotto;
    private javax.swing.JCheckBox cheStampaScrittaInvoicex;
    private javax.swing.JCheckBox cheStampaTelefono;
    private javax.swing.JCheckBox cheTotali;
    private javax.swing.JCheckBox cheUpdateListini;
    private javax.swing.JCheckBox cheVisuAnno;
    private javax.swing.JComboBox comBarcodeColonne;
    private javax.swing.JComboBox comBarcodeDisposizione;
    private javax.swing.JComboBox comBarcodeFormato;
    private javax.swing.JComboBox comBarcodeRighe;
    private javax.swing.JComboBox comBarcodeTipoPrezzo;
    private javax.swing.JButton comFile;
    private javax.swing.JButton comFile1;
    private javax.swing.JButton comFileSfondo;
    private javax.swing.JButton comFileSfondoEmail;
    private javax.swing.JComboBox comListinoBase;
    private javax.swing.JComboBox comLook;
    private javax.swing.JComboBox comStampaBarcode;
    private javax.swing.JComboBox comTipoLiquidazioneIva;
    private javax.swing.JComboBox comTipoNumerazione;
    public javax.swing.JComboBox comTipoStampa;
    public javax.swing.JComboBox comTipoStampa1;
    public javax.swing.JComboBox comTipoStampaDdt;
    public javax.swing.JComboBox comTipoStampaOrdine;
    private javax.swing.JTextField da_ddt;
    private javax.swing.JTextField da_ordine;
    private javax.swing.JRadioButton data_fattura;
    private javax.swing.JRadioButton data_scadenza;
    private javax.swing.JCheckBox estraiScadenzeFatseq;
    private javax.swing.JComboBox generazione_movimenti;
    public tnxbeans.tnxDbGrid2 griglia_soglie;
    private javax.swing.JTextField int_dest_1;
    private javax.swing.JTextField int_dest_2;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel111;
    private javax.swing.JLabel jLabel113;
    private javax.swing.JLabel jLabel115;
    private javax.swing.JLabel jLabel116;
    private javax.swing.JLabel jLabel117;
    private javax.swing.JLabel jLabel118;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    public javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JLabel labDb;
    private javax.swing.JLabel labDb1;
    private javax.swing.JLabel labHost;
    private javax.swing.JLabel labId;
    private javax.swing.JLabel labPwd;
    private javax.swing.JLabel labStampaFirma;
    private tnxbeans.tnxComboField listino_consigliato;
    private javax.swing.JLabel logo;
    private javax.swing.JLabel logo_email;
    private javax.swing.JTextField mbottom;
    private javax.swing.JTextField mleft;
    private javax.swing.JTextField mright;
    private javax.swing.JTextField mtop;
    private javax.swing.JTextField paltezza;
    private javax.swing.JPanel panEmiCad;
    private javax.swing.JPanel panPrintOption;
    private javax.swing.JPanel panelBarCode;
    private javax.swing.JTextField plarghezza;
    private javax.swing.JPopupMenu popuprimuovi;
    private javax.swing.JButton pulsanteFont;
    private javax.swing.JComboBox raggruppa_articoli;
    private javax.swing.JMenuItem rimuovi;
    private javax.swing.JComboBox riportaSerie;
    private javax.swing.JButton select_truststore;
    private javax.swing.JLabel sfondo;
    private javax.swing.JLabel sfondo_email;
    private javax.swing.JCheckBox ssh;
    private javax.swing.JTextField ssh_hostname;
    private javax.swing.JTextField ssh_login;
    private javax.swing.JPasswordField ssh_password;
    private javax.swing.JTextField ssh_porta_locale;
    private javax.swing.JTextField ssh_porta_remota;
    private javax.swing.JCheckBox ssl;
    private javax.swing.JTextField ssl_truststore;
    private javax.swing.JComboBox stampare_timbro_firma;
    private tnxbeans.tnxComboField stato_ordi_post;
    private javax.swing.JTextArea testo_timbro_firma;
    private javax.swing.JTextField texCodIvaReadytec;
    private javax.swing.JTextField texContoRicaviReadytec;
    private javax.swing.JTextField texDb;
    private javax.swing.JTextField texEmiCadFilePost;
    private javax.swing.JTextField texEmiCadFilePre;
    private javax.swing.JTextField texFreeBarcode;
    private javax.swing.JTextField texHost;
    private javax.swing.JTextField texId;
    private javax.swing.JTextField texImgFirma;
    private javax.swing.JTextField texInte1;
    private javax.swing.JTextField texInte2;
    private javax.swing.JTextField texInte3;
    private javax.swing.JTextField texInte4;
    private javax.swing.JTextField texInte5;
    private javax.swing.JTextField texInte6;
    private javax.swing.JTextField texIvaDefault;
    private javax.swing.JTextField texIvaSpese;
    private javax.swing.JTextField texLabelCliente;
    private javax.swing.JTextField texLabelClienteEng;
    private javax.swing.JTextField texLabelMerce;
    private javax.swing.JTextField texLabelMerceEng;
    private javax.swing.JTextField texLimit;
    private javax.swing.JTextField texMessaggioStampa;
    private javax.swing.JTextArea texNoteDocu;
    private javax.swing.JTextArea texNoteDocuAcquisto;
    private javax.swing.JTextArea texNoteFatt;
    private javax.swing.JTextArea texNoteOrdi;
    private javax.swing.JTextArea texNoteOrdiAcquisto;
    private javax.swing.JTextArea texNoteStandard;
    private javax.swing.JTextField texPersonalizzazioni;
    private javax.swing.JPasswordField texPwd;
    private javax.swing.JTextField tnxFileLogo;
    private javax.swing.JTextField tnxFileLogo1;
    private javax.swing.JTextField tnxFileSfondo;
    private javax.swing.JTextField tnxFileSfondoPdf;
    private javax.swing.JTextField umpred;
    private javax.swing.JComboBox vostro_ordine;
    // End of variables declaration//GEN-END:variables

    public String updateLogoPreview(String file) {
        System.out.println("updateLogoPreview...");
        if (file.equals("")) {
            File f = new File(main.wd + "icone/nologo.png");
            file = f.getAbsolutePath();
        }
        String text = "";
        File ftest = new File(file);
        if (ftest.exists()) {
            ImageIcon icon = new ImageIcon(file);
            int lar = icon.getIconWidth();
            int alt = icon.getIconHeight();
            int newalt = (grandezzaTooltipImage * alt) / lar;
            try {
                String ftests = ftest.getAbsolutePath();
                ftests = ftests.replace("%", "%25");
                text = "<html><img src='file:///" + ftests + "' width='" + grandezzaTooltipImage + "' height = '" + newalt + "'></html>";
                System.out.println("text: " + text);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            text += "<html><b>file inesistente</b></html>";
        }
        return text;
    }

    public static void salvaFirmaInDb(String file) {
        try {
            File fileLogo = new File(file);

            if (fileLogo.exists()) {
                FileInputStream is = new FileInputStream(fileLogo);
                byte[] bb = new byte[(int) fileLogo.length()];
                is.read(bb);
                //sql += ", logo = " + Db.pc(new String(bb), Types.VARCHAR);
                Statement s = Db.getConn(true).createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet r = s.executeQuery("select id, immagine_firma_ordine from dati_azienda");
                if (r.next()) {
                    r.updateObject("immagine_firma_ordine", bb);
                    r.updateRow();
                }
                r.close();
                s.close();
            } else {
                Statement s = Db.getConn(true).createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet r = s.executeQuery("select id, immagine_firma_ordine from dati_azienda");
                if (r.next()) {
                    r.updateObject("immagine_firma_ordine", null);
                    r.updateRow();
                }
                r.close();
                s.close();
            }
        } catch (Exception ex1) {
            ex1.printStackTrace();
        }
    }

    public void stampa(final String tipoFattura) {
        stampa(tipoFattura, "0", 0, 0, false, false, false, null);
    }

    public Object stampa(final String tipoFattura, final String dbSerie, final int dbNumero, final int dbAnno, final boolean generazionePdfDaJasper, final boolean attendi, final boolean booleanPerEmail, final Integer id) {
        Object ret = null;

        //tipoFattura
        String paramTipoStampa = "tipoStampaOrdine";

        String tempts = "";
        if (!StringUtils.isBlank(main.fileIni.getValue("stampe", paramTipoStampa))) {
            tempts = main.fileIni.getValue("stampe", paramTipoStampa);
        } else {
            tempts = main.fileIni.getValue("pref", paramTipoStampa, "");
        }
        final String prefTipoStampa = tipoFattura;

        //salvo img logo in db percè le stampe la caricono da db invece che da file per integrazione con client manager
        InvoicexUtil.salvaImgInDb(main.fileIni.getValue("varie", "percorso_logo_stampe"));

        //nuovo tipo di stampa
        if (prefTipoStampa.endsWith(".jrxml")) {
            final JDialogImpostazioni padre = this;
            SwingWorker work = new SwingWorker() {
                public Object construct() {
                    padre.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    final JDialog dialog = new JDialogCompilazioneReport();
                    Object ret = null;
                    try {
                        dialog.setLocationRelativeTo(null);
                        dialog.setVisible(true);

                        File freport = new File(main.wd + Reports.DIR_REPORTS + Reports.DIR_FATTURE + prefTipoStampa);

                        /* DAVID */
                        System.out.println("11111111111111111Cerco Jasper");
                        JasperReport rep = Reports.getReport(freport);
                        java.util.Map params = new java.util.HashMap();
                        reports.JRDSOrdine jrInvoice = new reports.JRDSOrdine(Db.getConn(true), dbSerie, dbNumero, dbAnno, booleanPerEmail, false, id);
                        params.put("myds", jrInvoice);
                        params.put("scadenze", main.fileIni.getValueBoolean("pref", "scadenzeOrdini", false));
                        JasperPrint print = JasperManager.fillReport(rep, params, new JREmptyDataSource());

                        //JasperPrint print = JasperManager.fillReport(rep, params, jrInvoice);

                        java.util.prefs.Preferences preferences = java.util.prefs.Preferences.userNodeForPackage(main.class);

                        if (generazionePdfDaJasper) {
                            //File fd = new File("tempEmail/documento_" + tipoFattura + "_" + dbSerie + String.valueOf(dbNumero) + "_" + jrInvoice.nomeClienteFile + ".pdf");
                            String nomeFile = main.wd + "documento_" + dbSerie + String.valueOf(dbNumero) + "_" + jrInvoice.nomeClienteFile + ".pdf";
                            nomeFile = "tempEmail/" + FileUtils.normalizeFileName(nomeFile);
                            File fd = new File(nomeFile);
                            String nomeFilePdf = fd.getAbsolutePath();
                            JasperExportManager.exportReportToPdfFile(print, nomeFilePdf);
                            ret = nomeFilePdf;
                        } else {
//                            if (preferences.getBoolean("stampaPdf", false)) {
                            if (main.fileIni.getValueBoolean("pref", "stampaPdf", false)) {
                                String nomeFilePdf = main.wd + "tempPrnOrdine.pdf";
                                JasperExportManager.exportReportToPdfFile(print, nomeFilePdf);
//                                SwingUtils.open(new File(nomeFilePdf));
                                Util.start2(nomeFilePdf);
                            } else {
                                final JasperPrint printer = print;
                                Thread t = new Thread(new Runnable() {
                                    public void run() {
                                        JDialogJasperViewer viewr = new JDialogJasperViewer(main.getPadre(), true, printer);
                                        viewr.setTitle("Anteprima di stampa");
                                        viewr.setLocationRelativeTo(null);
                                        viewr.setVisible(true);
                                        padre.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                                    }
                                });

                                t.start();
                            }
                        }
                    } catch (JRException jrerr) {
                        JOptionPane.showMessageDialog(main.getPadreFrame(), jrerr.toString(), "Errore", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        dialog.setVisible(false);
                    }

//                    main.getPadre().toFront();

                    return ret;
                }
            };
            work.start();
            if (attendi) {
                ret = work.get();
                System.out.println("get " + work + " : " + ret);
            }
        } else {
//            prnDdt_tnx temp = new prnDdt_tnx(dbSerie, dbNumero, dbAnno);
            System.err.println("rimosso vecchie stampe");
        }

        return ret;
    }

    private void setPulsanteFont(Font font) {
        if (font == null) {
            pulsanteFont.setFont(main.def_font);
            pulsanteFont.setText(main.def_font.getFamily() + " " + main.def_font.getSize());
        } else {
            pulsanteFont.setFont(font);
            pulsanteFont.setText(font.getFamily() + " " + font.getSize());
        }
    }

    private void caricaLogo(String file, JLabel label, String campo) {
        try {
            BufferedImage i0 = null;
            if (file == null) {
                try {
                    i0 = ImageIO.read(InvoicexUtil.caricaLogoDaDb(Db.getConn(true), campo));
                } catch (IllegalArgumentException e) {
                    setlabelicon(label, null);
                    return;
                }
            } else {
                i0 = ImageIO.read(new FileInputStream(file));
            }
            if (i0 == null) {
                setlabelicon(label, null);
                return;
            }
            i0 = ImgUtils.resizeQuality(i0, ImgUtils.getDimension(i0.getWidth(), i0.getHeight(), label.getWidth(), label.getHeight()));
            setlabelicon(label, new javax.swing.ImageIcon(i0));
        } catch (Exception e) {
            e.printStackTrace();
            setlabelicon(label, null);
        }
    }

    private void setlabelicon(final JLabel label, final ImageIcon img) {
        SwingUtils.inEdt(new Runnable() {
            public void run() {
                label.setIcon(img);
            }
        });
    }

    private void aggiornaAperturaFile() {
        boolean come = true;
        if (aperturaFile.getSelectedIndex() == 0) {
            come = false;
        }
        aperturaLabel1.setEnabled(come);
        aperturaLabel2.setEnabled(come);
        aperturaLabel3.setEnabled(come);
        aperturaText1.setEnabled(come);
        aperturaText2.setEnabled(come);
        aperturaText3.setEnabled(come);
        aperturaImposta.setEnabled(come);
    }
}
