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
package gestioneFatture;

import it.tnx.Db;
import com.caucho.hessian.client.HessianProxyFactory;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;
import com.jidesoft.plaf.LookAndFeelFactory;
import com.lowagie.text.pdf.ByteBuffer;
import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import it.tnx.PrintUtilities;
import it.tnx.SwingWorker;
import it.tnx.accessoUtenti.Utente;
import it.tnx.commons.CastUtils;
import it.tnx.commons.DbUtils;
import it.tnx.commons.DebugFastUtils;
import it.tnx.commons.DebugUtils;
import it.tnx.commons.FileUtils;
import it.tnx.commons.HttpUtils;
import it.tnx.commons.MicroBench;
import it.tnx.commons.SwingUtils;
import it.tnx.commons.SystemUtils;
import it.tnx.commons.agg.UnzipWorker;
import it.tnx.commons.cu;
import it.tnx.invoicex.Attivazione;
import it.tnx.invoicex.Hservices;
import it.tnx.invoicex.InvoicexUtil;
import it.tnx.invoicex.Main;
import it.tnx.invoicex.PlatformUtils;
import it.tnx.invoicex.Plugin;
import it.tnx.invoicex.Versione;
import it.tnx.invoicex.gui.JDialogDatiAzienda;

import it.tnx.invoicex.gui.JDialogExc;
import it.tnx.invoicex.gui.JDialogInstallaPlugins;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import java.io.*;
import java.net.InetAddress;

import java.net.ServerSocket;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.*;

import org.apache.commons.lang.StringUtils;

import it.tnx.invoicex.gui.JDialogPlugins;
import it.tnx.invoicex.gui.JDialogProxyAuth;
import it.tnx.invoicex.gui.JDialogRiattivaPlugins;
import it.tnx.invoicex.gui.JDialogUpd;
import it.tnx.invoicex.gui.JFrameIntro2;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.sql.Types;

import java.text.DecimalFormat;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import mjpf.EntryDescriptor;
import mjpf.PluginEntry;
import mjpf.PluginFactory;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class main {

    public static main INSTANCE = null;
//    static public boolean debug = true;
    static public boolean debug = false;
//    static public String build = "20081201-1";
//    static public String build = "20081211";    //gestione listini con ricarico
//    static public String build = "20081212";    //migliore su generazione movimenti e filtro in statistiche per non mettere le proforma
//    static public String build = "20081215";    //corretto problema su doppio click situazione fornitori
//    static public String build = "20081218";    //corretto problema su articoli con listini a percentuale, aggiunta possibilità di articolo di tipo servizio, corretto finestra situazione clienti che non spariva..., ricerca su articoli non faceva +
//    static public String build = "20081222";    //aggiunta stampa paese se non italia
//    static public String build = "20090107";    //problema su stampa paese, svuoto errore di stampa
//    static public String build = "20090108";    //problema su stampa fatture le scadenze veniva sommate alle scadenze delle fatture ricevute
    //aggiornata release sourceforge 08/01/2009
//    static public String build = "20090113";    //problema su tnxBeans.jar
//    static public String build = "20090115";    //problema su tnxBeans.jar, su modifica righe fatture precedenti venivano eliminate le righe, aggiunto pulsante duplica
//    static public String build = "20090121";    //aggiunto prezzo con iva per scorporo
//    static public String build = "20090128";        //font su mac osx più piccolo, tolti temi substance per problema freeze su clienti, aggiunti temi toniclf e officelf
//    static public String build = "20090204";        //risolto problema decimali su aggiorna iva, problema non stampava la serie sui documenti, ritenuta d'acconto a 2 decimali
//    static public String build = "20090212";        //inserimento plugin ricerca, aggiunta impostazione per vedere solo i documenti dell'anno in corso, aggiunti controlli anagrafica clienti
//    static public String build = "20090213";        //calcolo sconto in righe, sconti portati a 3 cifre per il 100%, salvataggio filtri situazione/clienti/fornitori
//    static public String build = "20090217";        //migliore su gestione agenti con nuovo report per agente, aggiunta generazione provvigioni in automatico su conversione ddt a fattura, aggiunto codice iva 41 e scelta automatica in inserimento righe in base al cliente
//    static public String build = "20090218";        //aggiunto parametro su stampa registro iva per usare data documento esterno
//    static public String build = "20090219";        //debug click situazione clienti
//    static public String build = "20090304";        //aggiornati Db per connessioni più sicure
//    static public String build = "20090311";        //risolto problema arrotondamento in alcuni casi con cifre a più di due decimali
//    static public String build = "20090312";        //migliorie su uscita programma (problemi di bloccaggio in uscita su Mac risolti) migliorie su gestione destinazione diversa du prev. ddt e fatture, riattivato tema jgoodies su mac, riaggiunti temi substance dopo modifica per alcuni bugs, invio segnalazione errore generico, problema su articoli con stessa descrizione
//    static public String build = "20090316";        //Risolti alcuni bug su dati vuoti, ritorno da inserimento riga a testata documento, corretto problema creazione icona su ubuntu 8.x, correzione su apertura folder e link su linux
//    static public String build = "20090327";        //Risolto problema paese diverso in destinazione diversa e caricamento del pagamento su fattura su apertura per modifica
//    static public String build = "20090331";        //Aggiunta possibilità di raggruppare i DDT in conversione a Fattura
//    static public String build = "20090403";        //Aggiunta possibilità di gestire le scadenze per gli ordini e preventivi e export di vendite e acquisti su pdf, excel e html, risolti problemi su destinazione diversa
//    static public String build = "20090406";        //Aggiunta possibilità di richiedere la password (del database) all'avvio del programma
//    static public String build = "20090407";        //Portato numero righe sul foglio righe a 1000 invece che 100, tolti N in um e 20 su iva
//    static public String build = "20090414";        //Corretta stampa articoli con calcolo prezzo per listini a percentuale
//    static public String build = "20090415";        //Corretto funzionamento da proforma a fattura per spostamento scadenze su nuova fattura e generazione movimenti, Aggiunti flag visualizzazione note di credito e proforma in scadenze
//    static public String build = "20090416";        //Portati servizi di invoicex su server tnx.it, aggiunto invio log su segnalazione, anche su creazione db per risolvere problemi all'avvio
//    static public String build = "20090417";        //Test per bug mac osx con mysqld, incongruenza settaggio tema su mac osx, risolto problema reset password su aggiornamento, rivista procedura di creazione db inziale ed agganciata in impostazioni
//    static public String build = "20090514";        //Aggiunta gestione pagamenti parziali delle scadenze
//    static public String build = "20090515";        //stampa situazione clienti come a video, aggiunti controlli su dimensioni logo e sfondo per stampa (errore heap space out of ..)
//    static public String build = "20090521";        //corretta generazione scadenze per fatture di acquisto per data documento esterno invece che data registrazione
//    static public String build = "20090522";        //aggiunta funzione di inserimento righe tra altre righe
//    static public String build = "20090525";        //correzione scadenze su fatture di acquisto, articoli separati vend e acq, prova xerces per problema su mac sax parser
//    static public String build = "20090526";        //risolto bug su get mac address win vista, nel caso di ordine a ddt a fattura non riportava il pagamento selezionato
//    static public String build = "20090608";        //risolto bug su F4 in ricerca su nuova riga e su apertura pdf in linux, aggiunta gestione serie_fatture in dati azienda
//    static public String build = "20090630";        //aggiunte stampe per ritenute d'acconto, migliorie sulla gestione degli errori e bug fix per posizionamenti delle finestre
//    static public String build = "20090706";        //Prezzi di vendita e acquisto su stampa articoli e stampa giacenze, migliorie a classe Db, controlli su ordini/ddt e fatture per perdita righe ma no totali, aggiunti messaggi di controllo e facilitata la chiusura del programma
//    static public String build = "20090709";        //Prezzi di listino su giacenze
//    static public String build = "20090714";        //cambiata gestione degli statement su tnxbeans
//    static public String build = "20090720";        //cambiata gestione elenco finestre
//    static public String build = "20090721";        //ottimizzata apertura situazione clienti fornitori
//    static public String build = "20090724";        //aggiunti tooltip in situazione clienti e data in giacenze
//    static public String build = "20090730";        //aggiunto controllo totale scadenze con totale fattura per rigenerare le scadenze
//    static public String build = "20090812";        //possibilità di usare un codice iva alfanumerico
//    static public String build = "20090813";        //kill di mysqld su uscita brutale, tolta colonna sconti se non presenti, risolto bug su cambio numero e annulla, aggiunta colonna note su situazione pagamenti, migliorie su inserimento righe per usare solo tastiera, problema di stampa in linux
//    static public String build = "20090814";        //look and feel da jgoodies
//    static public String build = "20090817";        //controllo errori su ridimensionamento griglie,
//    static public String build = "20090818";        //modifiche per gestione scontrini
//    static public String build = "20090819";        //modifiche per gestione degli aggiornamenti dei plugins
//    static public String build = "20090831";        //Corretto bug test porte su mac per avvi odatabase locale
//    static public String build = "20090901";        //Corretto bug su colorForRow null
//    static public String build = "20090915";        //Implementata gestione articoli composti
//    static public String build = "20090917";        //Implementata gestione articoli composti e controllo giacenza
//    static public String build = "20090924";        //Implementato invio personalizzazione report, Possibilità di filtrare statistiche per articolo
//    static public String build = "20091008";        //problema su stampa fatture, non prendeva mai dati intestazione. Possibilità di inserire note di credito in negativo.
//    static public String build = "20091009";        //Problema email in agenti
//    static public String build = "20091012";        //Impostata data corrente su cambio da proforma a fattura
//    static public String build = "20091015";        //Aggiunto totale riga nell'inserimento articolo e in elenco righe dei ddt e ordini, aggiunta ricerca testuale nella casella cliente in elenco prev. ddt e fatture
//    static public String build = "20091021";        //Integrazione modifiche per gestione scontrini
//    static public String build = "20091022";        //Nuova funzione di controllo plugin nel caso siano danneggiati vengono riscaricati, Aggiornamento setup per windows 7
//    static public String build = "20091027";        //Gestione di matricole integrata, Migliorata ricerca articoli (per adesso solo in fattura), Migliorata anagrafica articoli
//    static public String build = "20091028";        //Corretta generazione DDT raggruppando articoli e risolto bug su converti documenti (nuovo campo id autoinc), Corretto problema su fioglio righe
//    static public String build = "20091030";        //Aggiornamenti per gestione scontrini
//    static public String build = "20091104";        //Aggiornamenti per gestione ritenute e no pro forma in iva
//    static public String build = "20091106";        //Corretto calcolo rivalsa e ritenuta
//    static public String build = "20091110";        //Corretto calcolo iva per arrotondamento (faceva arrotondamento al 3 e poi al secondo decimale, ERRATO, deve essere direttamente alla seconda cifra decimale), coretto raggruppamento DDT in fattura
//    static public String build = "20091111";        //Correzione su conversione da fattura a ddt, nuovo report ordini per destinazione diversa, errori su export statistiche preventivi
//    static public String build = "20091113";          //Correzione su conversione da ordine a fattura
    //Correzione su selezione del logo in preventivi e fatture su scelta del fornitore
    //Aggiunto codice fornitore e codice a barre in anagrafica articoli e ricerca in inserimento riga
    //Aggiunta immagine su articoli
//    static public String build = "20091119";        //Aggiunta possibilità di esportare DDT in Excel
//    static public String build = "20091120";        //Risolto problema destinazioni diverse in DDT
//    static public String build = "20091201";        //Modifiche per Luxury Company
//    static public String build = "20091211";        //Implementato Undo e Redo + menu contestuale su caselle di testo per Copia e Incolla
//    static public String build = "20091217";        //Unite modifiche vari clienti con vecchi lavori
    //velocizzato il controllo delle modifiche al database in partenza
    //Aggiunto scrolling al desktop di Invoicex per gestire meglio più finestre
    //corretto bug di creazione tabelle tmeporanee per undo
    //abilitato all'assistenza anche Mac tramite Vine Vnc
    //Aggiunto totale sconti su report preventivi
    //Aggiunti export e import righe da CSV
//    static public String build = "20091218";        //Aggiunta attivazione e export su cvs di vendite e acquisti
//    static public String build = "20091221";        //Correzioni per mac icona ricerca e foto articoli
//    static public String build = "20100106";        //Aggiunta gestione Lotti nei movimenti di magazzino
//    static public String build = "20100107";        //Corretto bug su linux e mac in presenza di altro mysql
//    static public String build = "20100109";        //Migliorati avvisi di errore su wizard db iniziale
//    static public String build = "20100119";        //Aggiunto import Clienti da excel
//    static public String build = "20100126";        //Rsiolto problema movimenti kit e lotti
//    static public String build = "20100205";        //Risolto problema con gestione scontrini
    //Risolto problema su statistiche export e date in inglese
    //Aggiunta scelta listino su reimport righe
    //Risolto problema posizionamento finestra scelta iva
//    static public String build = "20100210";        //Aggiunta esportazione di ogni tabella in Excel
    //Corretto import articoli da file Excel, non inseriva i prezzi in alcuni casi
//    static public String build = "20100211";        //Corretto bug in duplicazione Fatture
//    static public String build = "20100212";        //Corretto bug salvataggio fatture con iva null
//    static public String build = "20100310";        //
//    static public String build = "20100318";
    /*
     * Aggiunte:
     * Nuovo sistema per posizionare e ridimensionare il logo in stampa
     * Anagrafica Porti e causali
     * Aggiunta ricerca su movimenti di magazzino
     * Milgiorie su inserimento prezzi articoli
     * Eliminazione multipla su movimenti di magazzino e articoli
     * Aggiunta possibilità di ricaricare il magazzino inserendo una nota di credito
     * Nuovo sistema per selezionare il cliente in nuova Fattura e Situazione Clienti
     * Stampa delle matricole e dei lotti nei documenti
     *
     *
     * Risolti problemi in stampa su linux
     * Corretto gestione del giorno di pagamento
     * Possibilità di ricercare clienti e fornitori in Situazione Clienti tramite il codice numerico.
     * Risolto problema decimali su preventivi
     * Controllate le scadenze per la rigenerazione delle provvigioni
     * Cap su anag. agenti che non veniva salvato
     * Risolto problema codice fornitore su fatture in acquisto
     * Risolto problema su prezzi con aumento in percentuale e servizio
     * Risolti problemi con Foglio Righe in fattura
     * Risolto problema della sparizione dell'eleimina riga
     * Risolto problema sulla funzione duplica che non generava i movimenti
     *
     */
    //static public String build = "20100330";        //debug problema attivazione
//    static public String build = "20100422";        //aggiornato plugin ricerca
//    static public String build = "20100423";        //migliorato import clienti
//      static public String build = "20100504";        //aggiornamento plugin ritenute, specifica applicazione ritenuta su riga
    //migliorato controllo Piva e Cod Fiscale su anag. cliente
//      static public String build = "20100511";        //Migliorie per utilizzo via internet
    //corretti piccoli bugs su Anagrafiche Clienti
//      static public String build = "20100512";        //Migliorie per utilizzo via internet
//                                                        //Aggiunta settimana scadenza
//      static public String build = "20100517";        //Aggiunta gestione provvigioni per scaglioni
//      static public String build = "20100520";        //Aggiunta gestione provvigioni per scaglioni
//      static public String build = "20100521";        //Problema esportazioni pdf (per main.wd)
//      static public String build = "20100531";        //Parametrizzata scritta su conversione da ordine e da ddt
//      static public String build = "20100603";        //Migliorata selezione articolo con descrizione su più righe
    //Aggiunta scelta prezzo dai listini in inserimento riga
    //Correzione generazione movimenti in conversione da Preventivo a DDT
    //nuove griglie per caricamento tabelle molto grandi
    //possibilità di scegliere il prezzo da un altro listino in inserimento riga
//    static public String build = "20100611";        //Corretto bug su plugin ricerca con nuova ricerca cliente in testata fattura
//    static public String build = "20100617";        //Corretto bug modifica fattura accompagnatoria
//    static public String build = "20100621";        //aumentato a 60 da 15 timeout resultset
//    static public String build = "20100630";        //corretto lazyresultset in lista articoli
//    static public String build = "20100702";        //Aggiunta specifica della provvigione agente su ogni riga (prima era sulla testata del documento)
    //Migliorata conversione documenti da ddt e ordine a fattura per recuperare le coordinate bancarie del cliente
    //Diviso su anagrafica articoli flag 'Servizio' da 'Applicare ricarico listino a percentuale's
    //Controllati problemi su salvataggio
    //Corretti "+ Aggiungi ... " su Form Cliente
//    static public String build = "20100709";        //
//    static public String build = "20100720";        //scontrini
//    static public String build = "20100903";        //bug fixing vari assistenza su win7 , conversione dati bancari, ssl, nome file cliente con slash
//    static public String build = "20100930";        //user name fino a 250 caratteri per problemi su tabelle temporanee
    //raggruppamento riepilogativo dei ddt in conversione a fattura (1 ddt -> 1 riga di fattura)
    //ordini di acquisto e ddt di acquisto (alfa)
    //provvigoni agenti su riga oltre che su documento e report dettagliato
//    static public String build = "20101004";        //correzione conversione ddt a fattura
//    static public String build = "20101013";        correzioni:
                                                    /*
     colori griglie in base al tema saltavano fuori caselle nere
     generazione fatture di acquisto dal ddt di acquisto
     generazione movimenti su ddt di acquisto
     richiesta generazione movimenti su fatture di acquisto
     ritorno su documento selezionato negli elenchi
     */
//    static public String build = "20101014";        //problema newseuropa
//    static public String build = "20101015";        //aggiornmaento per non comprendere la rivalsa nell'imponibile iva (ponente wine)
//    static public String build = "20101022";        //stampa ordine acquisto corretta
    //stampa registro iva corretta
//    static public String build = "20101115";
    //evasione parziale degli Ordini
    //fatturazione parziale dei DDT
    //calcolo giacenza con Ordinato e Venduto
    //stampa ragione sociale su più righe
    //correzioni grafiche su tema standard
    //corretto ordine righe su esportazione da Ordine ad altro documento
    //corretto bug posizione immagine
    //corretto bug visualizzazione articoli
    //corretto note automatiche da cliente
    //corretto dump backup per problemi memoria
    //corretta stampa iva con fino a 4 percentuali
    //risolto problema su conversione + ddt e raggruppamento per articolo
    //aggiunta personalizzazione carburante per spese carburante invece che spese di trasporto
    //aggiunto campo data consegna su preve. e ordini
    //online con version 1.8.0
//    static public String build = "20101122";
//    static public String build = "20101213";
    /*
     - problema non rigenerava provvigioni su cambio scadenze
     - migliorato box rihiesta assistenza
     - migliorata rimozione plugin scaduti
     - migliorata stampa liquidiazione iva
     - migliorati prezzi per Clienti, tabella di editing in anagrafica Cliente
     */
//    static public String build = "20101216";
    /*
     - migliorato export Acquisti/Vendite con filtro per articolo, calcolo importi scontati e export di tutti i documenti
     - migliorato Statistiche con possibilità di stampare le quantità, sfondo del grafico bianco, correzione ordine mesi.
     */
//    static public String build = "20101222";    //perle gitane temp
//    static public String build = "20101223";    //piccoli bug fixing su conversione da ddt a fattura
//    static public String build = "20101229";    //perle gitane temp
//    static public String build = "20110103";    // protekno
    // inserimento quantità tramite numero scatole
    // conversione da ordine a fattura di acquisto
    // fino a sei codice fornitore in articoli
//    static public String build = "20110105";    // perle gitane
//    static public String build = "20110110";    // aggiunto agente su import clienti
//    static public String build = "20110111";    // aggiunto colonne aggiuntive su elenchi fatture, ddt ordini e situazione clienti
//    static public String build = "20110112";    // perle gitane web
//    static public String build = "20110114";    // unione sorgenti alessio toce
    /*
     filtro per causale ddt e colonne aggiuntive per riferimento
     gestione rivalsa per agenti di commercio (vedi rivalsa 6,75% da sotrrarre, nuova opzione in impostazioni)
     */
//    static public String build = "20110117";    // unione sorgenti alessio toce
    /*
     import ipsoa
     modifiche oman per filtraggi osu ddt
     */
//    static public String build = "20110118";    // unione sorgenti alessio toce
    //bug su inserimento listini a ricarico e visualizzazione tutti i listini su anag articoli
    //incluse modifiche ipsoa e oman
    //static public String build = "20110121";
    /*
     autenticazione proxy (distilleria fatebenefratelli)
     */
//    static public String build = "20110124";
    /*
     personalizzazioni
     */
    //static public String build = "20110131";
    /*
     aggiunto riconoscimento proxy per navigazione
     corretto form richiesta assistenza
     corretto export righe csv su ordini acquisto e ddt acquisto
     corretto modifica riga con matricole associate (id_padre null)
     */
//    static public String build = "20110218";
    /*
     ottimizzazione per uso via internet su inserimento movimenti di magazzino
     debug ibrain lentezza/blocco
     problema nomi file in caso di windows mail e file con tanti puntini
     risolto un thread lock su apertura preventivi (tnxComboField edt)
     risolto problema paese di destinazione non azzerabile
     risolto problema modifca cliente su preventivi in modifica

     risolto problema causali di trasporto
     risolto problema elenco iva in articoli
     risolto problema iva con descrizione lunga in stampa
     aggiunto minimo ordinabile perle gitane

     */

    /*
     static public String build = "20110315";

     risolto problema import righe da CSV in caso di tabella righe vuota
     nascosto checkbox 'applica ritenuta d'acconto' su inserimento righe se non c'è nè bisogno
     aggiunta possibilità di collegamento via tunnel ssh
     risolto problema accenti su ragione sociale in registrazione dati utente
     aggiunto personalizzazione 'open' per apertura file con altro comando (vedi Crea Pdf per email)
     implementato Manutenzione con controllo movimenti senza documenti
     aggiunta possibilità di includere un listino prezzo consigliato su stampa DDT
     corretta stampa liquidazione iva in caso di costi indeducibili
     aggiunto teamviewer come assistenza remota
     aggiunta scelta font in impostazioni
     aggiunta possibilità di recuperare righe fattura da una versione precedente
     corretta movimentazioni articoli con gestione matricole su ddt di acquisto
     corretto export e import csv in ddt di acquisto
     */

    /*static public String build = "20110330";
     cancellazione documenti con controllo su documenti di provenienza
     impostazione carattere interfaccia grafica
     impostazione listino consigliato standard
     aumentato log sql
     */

    /*static public String build = "20110407";
     impostazione listino consigliato standard, correzione
     corretto problema su recupero database
     corretto problema su raggr in conversione documenti e su ristampa distinta riba
     */

    /*static public String build = "20110418";
     aggiunte librerie per plugin Email
     aggiornato plugin riba da toce
     in stampa articoli adesso visualizza anche quelli senza il prezzo associato
     */

    /*static public String build = "20110419";
     corretto arrotondamento prezzi totali di riga, quindi totale imponibile = somma dei totali di riga arrotondati
     corretto bug rigenerazione scadenze su cambio numero documento
     */

    /*
     static public String build = "20110509";
     ipsoware, gaia servizi, conenna
     */

    /*static public String build = "20110520";
     corretta versione online per mac e linux
     vannuccini, conenna
     aggiunto controllo tabelle prima dei cambi di struttura
     corretta gestione note di credito in provvigioni
     passaggio a 1.8.1
     tolto personalizzazione conenna su nuovo articolo
     */

    /*static public String build = "20110607";
     pulitura sorgenti
     aggiornamento gestione plugin (plugin privati, compry, achievo e client manager)
     check table ignorare: Found row where the auto...
     centralizzato logo e sfondo su db per installazioni in rete o via internet
     aggiunto controllo windows vista program files
     correzioni stampa fattura mod4 per casella sconto su riga errato
     migliorie su visualizzazione giacenze (filtro e flag giacenza a zero)
     */

    /*static public String build = "20110701";
     corretto 'open' per percorsi con spazi su windows
     corretta generazione scadenze su fatture di acquisto in caso di ritenuta d'acconto
     corretta estrazione preventivi di vendita filtrando per cliente
     corretta stampa liquidazione iva dopo aggiornamento per deducibilità non conteggiava bene le spese accessorie, trasporto
     migliorati messaggi di errore su inserimento destinazioni diverse
     problema logo in db con ragione sociale, spostata pk su id
     parametrizzate etichette Cliente e Destinazione merce
     corretto Costi ineducibili in report iva
     */

    /*static public String build = "20110715";
     problema destinazione diversa invertita su stampa preventivi e ordini
     aggiunto controllo chiusura con attesa chiusura finestre documenti aperti
     aggiunto controllo apertura fatture
     problema ricerca articolo in export srtatistiche venduto
     corretto backup per campi binary, vedi nuovo campo logo in dati azienda

     integrazione tnxBeans e tnxUtil dentro Invoicex
     distribuzione pluginEmail
     */
//    static public String build = "20110721";
    /*
     problema logo e scritta fornitore modifica Bianconi
     corretto bug su username con apostrofo
     corrette note automatiche che sovrascrivevano note su DDT e Ordine
     migliorie su recupero database (bug USING BTREE e drop di tutte le tabelle)
     migliorato controllo merce in arrivo e in uscita considerando solo ordini e non preventivi
     Aggiunto flag Iva a 30 giorni sui tipi di pagamento per aggiungere scadenza fissa a 30gg o 30gg fine mese per addebitare l'importo IVA
     */

    /*static public String build = "20110912";
     modifiche cd garage, brignoli, wdr
     corretto campo convertito su conversione
     corretto pluginScontrini (movimenti, numeri...)
     corretto problema mac address in attivazione
     corretto mysqladmin su osx x86
     */

    /*static public String build = "20111109";
     iva 21
     correzzioni iva 21
     test ibrain
     correzione iva 20 su spese documenti precedente al 17/09/2011
     correzione calcolo otali dopo correzzione iva 20 di cui sopra
     modifica stampe per p.iva sotto intestazione
     corretto iva spese se impostato a altro (non 21) per documenti precedenti iva 21
     correzioni su prezzi articoli e form listini
     correzioni plugin Scontrini Lanzoni
     allargato campo data dopo aggiornamenti per partita iva (non aggiornava i listini a ricarico e non faceva vedere subito i cambiamenti nella form articoli)
     corretto problema non chiusura mysql (stop dei plugin, soprattutto pluginRicerca)
     aggiunto paramentro max allowed packet a 64M per ripristino loghi grandi
     corretto gestione errore su ripristino backup (anche su plugin)
     corretto restore dump fra win/mac (lettura in utf8)
     aggiornato plugin ricerca e backup
     nuovo splash screen
     corretto posizionamento finestra nuova riga
     scritta vostro riferimento corretta per mac su stampe preventivo
     aggiunti sconti su prezzi articoli e su anag cliente/fornitore
     aggiunti ordinamenti su export totali fatture
     corretto report agenti dettagliato
     nuovo splash screen :)
     */

    /*static public String build = "20111202";
     ottimizzate le dimensioni delle finestre per un minimo di 1024 x 600
     configurabilità comandi per apertura cartelle, file e pdf (tolta personalizzazione open)
     completati report fatture e fatture accompagnatorie in inglese
     duplicazione righe documenti
     possibilità di marcare una riga di un documento come 'descrizione' e quindi occupare tutte le colonne in stampa
     calcolo totale spese di trasporto e incasso su conversione multipla ddt a fattura
     migliorata scrittura movimenti in caso di archivio articoli molto grande
     migliorata ricerca articoli in caso di archivio articoli molto grande
     importi a 5 decimali su apertura prezzi di listino dall'inserimento righe
     corretto bug annullamento conversione ddt a fattura creava comunque la fattura
     nuova funzionalità di approssimazione con parametro in inserimento righe (da attivare nelle impostazioni)
     nuova opzione per togliere la scritta di Invoicex nelle stampe
     possibilità di portare la serie serie in conversione documenti
     nuova opzione stato preventivo in conversione documenti
     */

    /*static public String build = "20111207";
     bug fix del precedente update
     */
    /*static public String build = "20111216";
     bug fix del precedente update ...
     errori su import ipsoa per totali a zero, conversione falliva per campo riferimento nullo...
     aggiunto calcolo totali righe su ogni azione
     corretto duplica per informazioni da non duplicare (evaso, riferimenti documenti..)
     corretta ricerca articoli che duplicava i risultati...
     corretto input todouble foglio righe (cirri) prendeva il punto come migliaia
     corretto converti a fattura da ddt
     corretto converti a fattura da ddt per acquisto
     corretto aggiornamento stato prev. e ord. con <lascia invariato>
     problema invio plugin
     */

    /*static public String build = "20111230";
     migliorata stampa liquidazione iva con quarta colonna iva
     corretta stampa documenti per totale_imponibile di riga
     corretto parametro_arrotondamento a 0 se vuoto, problema totali di riga a zero
     */

    /*static public String build = "20120105"; 
     corretto bug ricerca cliente su ele fatt con tema jgoodies
     corretto bug cambio anno ricalcolo progressivo
     corretto bug stampa registro iva su fatture di acquisto con sconti di testata
     */

    /*static public String build = "20120119";
     * problema generazione movimenti su articoli con apostrofo nel codice articolo
     * problema omaggi totali uguali a riga originante
     * problema conversione ddt a fattura con gestione scontrini
     * aggiunte note parametri di stampa Statistiche Totale fatture
     * problema salvataggio in anagrafica listini che azzerava i prezzi caricati   
     */
    /*static public String build = "20120125";
     * migliorato e aggiunti la maggior parte dei campi disponibili per l'import clienti/fornitori da excel
     * corretto filtro su anagrafiche clienti/fornitori
     * aggiunto controllo derivazione da ddt su generazione movimenti in fatture di acquisto
     * corretto ricerca per cliente in fatture di vendita (ancora problema di focus)
     * corretta ricerca articoli con invio in descrizione
     * problema su elimina righe da grid per resultsemetadata null
     */
    //static public String build = "20120315";
    /*
     apertura pdf su ordini e fatture di acquisto
     personalizzazioni SNJ
     personalizzazioni NetUnion
     aggiunta conferma su annullamento documenti
     corretto problema logo fornitori su preventivi/ordini
     corretti accenti (urlencode con utf8) su invio mail di assistenza
     possibilità di inserire numero documento esterno alfanumerico di 50 caratteri per le fatture di acquisto
     aggiunta richiesta di conferma su pulsante Annulla in preventivi ddt e fatture
     aggiunto Claudio Romeo nell'about

     correzione movimenti da fatture di acquisto
     variazioni su apertura combo agenti e clie/forn

     correzioni layout testando su mac
     copia di backup nella cartella Documenti\Invoicex\backup
     corretta generazione scadenze su cambi odata fatture di acquisto
     gestione sconto a importo
     gestione prezzi ivati

     corretto bug JIDE linux
     aggiunta update totali pre sconto e prezzi ivati

     20120315 problema ritenuta in stampa
     */
    //static public String build = "20120329";
    /*
     modifiche net union / litri
     modifiche birra peroni (personal peroni)
     aggiunta scelta colonna importo su elenco fatture di acquisto (con plugin ritenute)
     layout osx e dbUndo
     correzione su eliminazione fattura proveniente da ddt non toglieva sempre i riferimenti sul ddt
     aggiunto import righe da excel su ordini per Proskin (personal: proskin)     
     */

    /*
     static public String build = "20120412";
    
     modifiche idrocclima
     personal no-colori-iva
     personal proskin, import da excel per ordini
     flag_rivalsa
     nuovo report certificazione imposta
     rimozione dei riferimenti su eliminazione fattura
     aggiunto controllo installazione in locale con Invoicex già aperto per sovrascrittura dati

     massimale su rivalse
     descrizione tipi pagamento su report
     modifiche per cambiare serie in conversione documenti
     correzioni SNJ
     */

    /*static public String build = "20120419";
    
     problema stampa registro iva (flag_rivalsa solo con plugin ritenute)
     problema generazione scadenze con scontrini
     implementata gestione lotti su conversione documenti
     implementati omaggi su import proskin
     */

    /*static public String build = "20120503";
     aggiunta conversione del punto in virgola nell'inserimento dei rpezzi in anagrafica articoli
     personal pagamento_stampa_codice per stmapare codice di pagamento invece che descrizione nei documenti
     problema assegnazione numero su duplica
     problema note automatiche su selezione cliente in ddt e ordini/preventivi
     */

    /*static public String build = "20120517";
     corretto estrazione export acquisti vendite con filtro su fornitore in caso di fatture di acquisto
     nuova opzione Provvigioni su data fattura (invece che date scadenze)
     corretto calcolo totale per problema con rivalsa
     gestione scadenze fino a 12 mesi
     */

    /*static public String build = "20120528";
     corretto bug in salvataggio dati articoli, azzerava prezzo se flag listino a ricarico era nullo
     download dei file mysql/data/mysql/proc* per problema sotred procedure e corretto dbchanges per problema aggiunta campo a dati azienda
     */

    /*static public String build = "20120718";
     new:
     gestione utenti
     l'attivazione della licenza adesso scarica i plugins della versione acquistata
     export teamsystem fatseq
     migliorata finestra plugins
     destinatario e dest. diversa personalizzabile da impostazioni
     migliorato spostamento finestre interne per andare fuori dai margini
     possibilità di aggiungere un menu' personale tramite param_prop.txt
     bugs:
     import csv con listino diverso corretti totali riga
     forzato engine a MyIsam per database con innodb come default
     download del createdb della giusta versione in caso di wizard nuovo db da versioni invoicex vecchie
     corretto bug su scelta immagine non jpg in impostazioni
     in Statistiche tolti i preventivi e lasciati gli ordini
     forzato charset ISO-8859-1 in backup e restore per problema accenti fra sistemi diversi
     corretto lettura cartella utente per aprire cartella Documenti in caso di utente con accenti
     */

    /*static public String build = "20120726";
     bugs:
     corretto problema pulsante 'Nuovo' su anagrafiche senza records
     corretto problema cambiamento impostazioni database in caso di problemi di apertura database
     apertura pagamento scadenze su posizione mouse
     aggiunto pulsante salva e chiudi su pagamento scadenze
     tasto destro su griglie seleziona la riga se non selezionata o selezionata solo una riga
     corretta eliminazione riferimenti documenti di acquisto
     migliorata gestione lotti in movimenti di magazzino e lotti omaggio
     correzione per aggiornare totali dopo import righe csv
     aggiunto controllo numerazione fatture e ddt di vendita in automatico e via Utilità->Manutenzione
     aggiunta eliminazione file temporanei e vecchie versioni di Invoicex e libs
     */

    /*static public String build = "20120917";
     bugs:
     problema import cc
     problema eliminazione righe dal fogli orighe cliccando su righe vuote se avevano stesso numero riga di altre
     migliorata conversione documenti con scelta di importare la riga o meno
     migliorata gestione scadenze/provvigioni, in caso di non cambiamento di data e/o importo della scadenza non vengono rigenerate le provvigioni
     */

    /*static public String build = "20120919";
     bugs:
     corretto problema generazione provvigioni in presenza di sconto su totale a importo
     migliorato resize pannello fatture di vendita per entrare bene su schermi 1024x600
     aggiunto campo email_2 da poter aggiungere nell' intestazione cliente
     */

    /*static public String build = "20121017"; 
     new:
     più veloce in stampa preventivi/ddt/fatture per uso via internet
     completate traduzioni in inglese delle stampe preventivi/ddt/fatture
     aggiunti limiti personalizzabili per pluginRicerca in Impostazioni
     possibilità di evasione parziale su articoli con lotti
     bugs:
     non riprende giorno pagamento da ddt a fattura se specificato nel cliente
     problema qta esportata anche se non si seleziona la riga
     colori situazione clienti
     backup prima di ripristino                
     */

    /*static public String build = "20121123"; 
     bugs:
     corretto in ripristino quando loghi vuoti (0x)
     corretto bug timer su ricerca e panbarr
     corretto bug su gestione utenti assegnazioni ruoli su nuovi utenti
     corretto bug di visualizzazione su finestra impostazioni
     corretto bug gestione lotti quando più di 20 lotti in inserimento righe
     ottimizzata form articoli per gestione prezzi quando abilitato prezzi per cliente e ci sono motli clienti (visualizza soltanto i prezzi dei listini non automaici)
     corretto problema di reimpostazione parametri daabase in caso di problemi da locale a rete
     righe selezionate in situazione clienti fornitori più scure
     corretto bug cartella documenti errata su windows xp su backup in locale
     corretto bug in Foglio Righe su importi mal formattati
     corretto campo quantita_evasa per avere stessi decimali di quantita
     migliorata gestione quantità evasa con approssimazione ai 5 decimali
     */
    /*static public String build = "20121207"; 
     bugs:
     * aggiunta quinta colonna su stampa iva e aggiornato il report di stampa
     * corretto import articoli per non sovrascrivere altre informazioni
     * corretto icona mail in situazione clienti/fornitori
     * aggiunta quantità fra parentesi nelle note aggiuntive dei lotti in inserimento riga
     * corretto controllo numerazione documenti ignorando le proforma
     * corretto pluginRicerca mostra/nascondi
     * controllo quantità inserita in riga con totale quantità inserita in lotti (se diversa si usa la somma dei lotti)
     * corretto stampa dell'importo di riga a 0 se sconto 100%, prima non stampava niente invece di 0
     * aggiunta anagrafica aspetto esteriore dei beni
     * aggiunto vostro riferimento se presente nell'ordine nella conversione a ddt o fattura
     * corretto inserimento dati da foglio righe in fattura
     */

    /*static public String build = "20121219";
     * new:
     * Nuova stampa etichette con codici a barre da DDT
     * bugs:
     * corretto download eseguibile e icona in base alla versione in caso di problemi di navigazione
     * parametrizzazione etichette clienti report in inglese,
     * formattazione caselle per visualizzazione inglese,
     * modifica export readytec (note di credito saldo in positivo),
     * corretto salvataggio prezzi automatico da salvataggio righe
     * 
     */
    /*static public String build = "20130108"; 
     * new:
     * Scelta guidata del tipo di numerazione da adottare dal 1/1/2013 per le fatture in base alla legge di stabilità 2013
     * bugs:
     * riabilitata ricerca automatica sui campi combo ddt e fattura accompagnatoria
     * aggiornamento dei riferimenti ai documenti convertiti quando si modifica un numero di documento (con funzione in Manutenzione per farlo rigenerare su tutti i documenti)
     */
    /*static public String build = "20130111"; 
     * migliorata stampa registro iva
     * corretto problema stampa numero fattura su documenti precedentei al 2013
     * possibilità di rimanere con il cambio numero di anno in anno senza stampare l'anno (risoluzione agenzia entrate n. 1/E del 10/1/2013)
     */

    /*static public String build = "20130204"; 
     * versione 1.8.4 compatibilità con pluginContabilità
     * tolto limite dell'anno precedente su visualizza prezzi precedenti    
     */

    /*static public String build = "20130218"; 
     * ottimizzati indici scadenze per velocizzare Situazione Clienti/Fornitori
     * allargato il campo numero su stampa fattura
     * corretto ricerca cliente/fornitore in ddt,ordini e fatture di acquisto
     * corretto problema tasto destro su piano dei conti con Mac
     * corretto problema arrotondamento decimali iva fatture di acquisto in stampa registro iva
     * corretto inserimento note automatiche su fatture di acquisto in scorriemnto fornitori
     * corretto bug cliente obsoleto su nuovo preventivo
     * aggiunta possibilità di specificare i decimali della quantità nella composizione del kit articolo
     */

    /* static public String build = "20130613"; 
     * creazione movimenti su proforma in conversione da ordinecon con personalizzazione (movimenti_su_proforma) 
     * aggiunta possibilità di elaborare i certificati e versamenti ritenute d'acconto per data pagamento oltre che per data documento
     * risolto problema stampa codici a barre da documenti di acquisto
     * risolto problema scadenze su fatture proforma, non si spsotava dalla scadenza numero 1
     * modifiche toys4you
     * corretti importi in negativo su export acquisti e vendite in caso di note di credito
     * corretto restore dati azienda con loghi grandi (tramite split hex)
     * corretta stampa iva con fatture e scontrini
     * nuova finestra Ultimi Prezzi in menù Magazzino
     * nuovo report Chi / Cosa in menù Statistiche
     * nuovo report Ordinato in menù Magazzino
     * nuova anagrafica Nazioni
     * nuova anagrafica modalità Consegna
     * nuova anagrafica modalità Scarico
     * aggiunto campo fornitore abituale in anagrafica articoli
     * aggiunti campi modalità Consegna e Scarico su anagrafica Clienti/Fornitori
     * 
     * corretto bug anno sbagliato su modifica, stampa, annulla salvataggio
     * ottimizzazioni prima nota
     * 
     * aggiunto controllo automatico parm_prop
     */

    /*static public String build = "20130621"; 
     * 
     * Corretto algoritmo di ricerca del prezzo in caso di articoli importati e listino a ricarico
     * Corretto backup per gestire le VIEW (vedi segnalazione errore su restore sulla VIEW v_righ_tutte
     * piccole correzioni su plugin contabilità
     * bug su nuovo report chi / cosa in caso di fatture di acquisto filtrate per fornitore 
     * bug su salvataggio destinazione diversa ordini
     * bug su visualizzazione fornitore in fatture di acquisto
     * 
     */

    /*static public String build = "20130704"; 
     * 
     * Possibilità di evidenziare tutti i documenti come per le Fatture, tasto destro Marca e scelta del colore
     * Aggiunta possibilità di scrolling verticale su anagrafica Articoli
     * tolta dipendenza font Tahoma sui report provvigioni per compatibilità con Linux  
     * corretto bug vista v_righ_tutte in ripristino database e controllo tabelle 
     * migliorata gestione interna delle righe dei documenti per id invece che numero/serie/anno
     */
    /*static public String build = "20130906"; 
     * 
     * Aggiunto pulsante Crea PDF in testata del documento e in plugin Ricerca
     * aggiunta scelta lingua in stampa elenco articoli
     * (nordas) richiesta dei lotti in fase di conversione ordine (prima venivano richiesti solo se qta conf minore di qta)
     * aggiunti campi codice a barre e codice fornitore nell'elenco degli articoli in anagrafica articoli e compresi nel filtraggio
     * riattivato snj
     * corretto inserimento movimenti, non permetteva di movimentare un articolo a lotti specificando un lotto vuoto
     * corretto problema iacovone prezzi di base invece che zero se import articolo e listino non di ricarico (richiamo aggiornaListini dopo import articoli)
     * aggiunto descrizione in inglese e um in inglese all'import articoli da excel
     * aggiunta la gestione dello sconto a importo sulle fatture di acquisto
     * corretto posizionamento etichetta descrizione in stampa fattura
     * corretto opzione stampa prezzi da cliente in conversione da prev/ordine a ddt
     * 
     */

    /*static public String build = "20130926"; 
     * 
     * risolto problema visualizzazione e salvataggio fatture di acquisto
     * aggiunta qta dei lotti in descrizione riga su conversione documenti
     * corretto calcolo importo scadenze con iva a 30gg e rivalsa in sottrazione
     * correzioni e aggiornamenti contabilita
     */
    

    /*static public String build = "20130930"; 
     * 
     * Passaggio IVA 22%
     * corretto calcolo totale documento con rivalsa + spese con iva da ripartizionare
     */    

     /*static public String build = "20131004";
     * 
     * Correzioni layout versione Osx
     * corretto calcolo totale su documenti con data precedente al 1/10, più di un codice iva e spese di trasporto o incasso
     * 
     */    
    
    static public String build = "20131010"; /*
     * 
     * Preparazione setup base 185 20131010
     * aggiornato createdb a 185
     * debug attivazione
     * correzioni grafiche su finestra giacenze e plugins per mac
     * 
     */    

    
    static public Versione version = new Versione(1, 8, 5); //aumentare quando si rialscia un aggiornamento
    static public String versione = null; //versione del programma Base, Professional, Enterprise
    //static private menu padre;
    static public Menu padre;
    static public MenuFrame padre_frame;
    static public MenuPanel padre_panel;
    static public String wd = "";   //working dir
    static public iniFileProp fileIni;
    static public Utente utente;
    static public boolean iniFinestreGrandi;
//    static public String iniPercorsoLogoStampe;
//    static public String iniPercorsoLogoStampePdf;
    static public String iniPercorsoSfondoStampe;
    static public String iniPercorsoSfondoStampePdf;
    static public String iniPercorsoSfondoProforma;
    static public String iniComandoGs;
    static public String iniDirFatture;
    static public boolean iniFlagMagazzino;
    static public boolean iniPrezziCliente;
    static public boolean iniSerie;
    static public String hdserial = null;
    static public String serial;
    static public int anno;
    static public String login = "";
    static public boolean flagWebStart = false;
    static public String PERSONAL_GIANNI = "gianni";
    static public String PERSONAL_TNX = "tnx";
    static public String PERSONAL_CUCINAIN = "cucinain";
    static public String PERSONAL_CHIANTICASHMERE = "cc";
    static public String PERSONAL_CHIANTICASHMERE_ANIMALI = "cc1_animali";
    static public String PERSONAL_TLZ = "tlz";
    static public String PERSONAL_LUXURY = "lux";
    static public String PERSONAL_CLIENT_MANAGER_1 = "cm1";
    static public String PERSONAL_IPSOA = "ipsoa";
    static public boolean luxStampaNera = false; //flag per luxury company per chiedere se stampare con sfondo nero la fattura
    static public String luxStampaValuta = "Euro"; //flag per luxury company per chiedere se stampare con sfondo nero la fattura
    static public boolean luxProforma = false;  //flag per stampare pro forma
    static public Properties applicationProps = new Properties();
    static public String homeDir = "";
    static public String inst_id = "";
    static public String inst_email = "";
    static public String inst_seriale = "";
    static public String inst_nome = "";
    static public String inst_cognome = "";
    static public String inst_nazione = "";
    public static Preferences prefs = Preferences.userNodeForPackage(main.class);
    static public String startDb = null;
    static public boolean startDbCheck = false;
    static public boolean startConDbCheck = false;
    static public int dbPortaOk = 0;
    static public InvoicexEvents events = new InvoicexEvents();
    static public ArrayList<String> pluginPresenti = new ArrayList();
    static public ArrayList<String> pluginAttivi = new ArrayList();
    static public Map<String, Map> pluginErroriAttivazioni = new HashMap();
    static public Hashtable plugins = new Hashtable();
    static public Hashtable<String, PluginEntry> pluginsAvviati = new Hashtable();
    public static boolean s1;
    static public boolean pluginInvoicex = false;
    static public boolean pluginBackupTnx = false;
    static public boolean pluginClientManager = false;
    static public boolean pluginJR = false;
    static public boolean pluginAchievo = false;
    static public boolean pluginAutoUpdate = false;
    static public boolean pluginEmail = false;
    static public boolean pluginBarCode = false;
    static public boolean pluginRitenute = false;
    static public boolean pluginRiba = false;
    static public boolean pluginRicerca = false;
    static public boolean pluginDdtIntra = false;
    static public boolean pluginScontrini = false;
    static public boolean pluginEbay = false;
    static public boolean pluginContabilita = false;
    static public PluginFactory pf = null;
    static public String plugins_path = "plugins/";
    static public Attivazione attivazione = new Attivazione();
    static Process mysqlproc = null;
    static public String paramProp = "param_prop.txt";
//    static public frmIntro splash = null;
    static public JFrameIntro2 splash = null;
    static Timer timerMem = new Timer("timerMem");
    static TimerTask timerMemTask = new TimerTask() {
        @Override
        public void run() {
            if (main.getPadrePanel() != null && main.getPadrePanel().menFunzioniManutenzione != null && main.getPadrePanel().menFunzioniManutenzione.isSelected()) {
                System.gc();
                System.runFinalization();
                System.out.println("--- Manutenzione Mem ----------------------------");
                DebugUtils.dumpMem();

//                ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
                ThreadGroup rootGroup = Thread.currentThread().getThreadGroup();
                ThreadGroup parentGroup;
                while ((parentGroup = rootGroup.getParent()) != null) {
                    rootGroup = parentGroup;
                }
                Thread[] threads = new Thread[rootGroup.activeCount()];
                while (rootGroup.enumerate(threads, true) == threads.length) {
                    threads = new Thread[threads.length * 2];
                }
                //DebugUtils.dump(threads);
                System.out.println("--- Threads ----------------------------");
                for (Thread t : threads) {
                    if (t != null) {
                        System.out.println("thread: " + t.getState() + " : " + t.toString());
                    }
                }
                System.out.println("--- Fine Manutenzione ----------------------------");
            }
        }
    };
    //no innodb
//    static public String win_startDb = ".\\mysql\\bin\\mysqld-nt.exe --no-defaults --standalone --console --basedir=mysql --skip-networking --enable-named-pipe --bind-address=localhost --skip-bdb --skip-innodb --pid-file=mysql_invoicex.pid";
//    static public String lin_startDb = "./mysql/bin/mysqld --no-defaults --basedir=mysql --user=root --port={port} --bind-address=127.0.0.1 --skip-bdb --skip-innodb --pid-file=mysql_invoicex.pid --socket=./invoicex_socket";
//    static public String mac_startDb = "./mysql/bin/mysqld --no-defaults --basedir=mysql --user=root --port={port} --bind-address=127.0.0.1 --skip-bdb --skip-innodb --pid-file=mysql_invoicex.pid --socket=./invoicex_socket";
    //con innodb
//    static public String win_startDb = ".\\mysql\\bin\\mysqld-nt.exe --no-defaults --standalone --console --basedir=mysql --skip-networking --enable-named-pipe --bind-address=localhost --pid-file=mysql_invoicex.pid --innodb_file_per_table --innodb_force_recovery=6";
    static public String win_startDb = ".\\mysql\\bin\\mysqld-nt.exe --no-defaults --standalone --console --basedir=mysql --skip-networking --enable-named-pipe --bind-address=localhost --pid-file=mysql_invoicex.pid --innodb_file_per_table --max_allowed_packet=1M";
    static public String lin_startDb = "./mysql/bin/mysqld --no-defaults --basedir=mysql --user=root --port={port} --bind-address=127.0.0.1 --pid-file=mysql_invoicex.pid --socket=./invoicex_socket --max_allowed_packet=64M";
    static public String mac_startDb = "./mysql/bin/mysqld --no-defaults --basedir=mysql --user=root --port={port} --bind-address=127.0.0.1 --pid-file=mysql_invoicex.pid --socket=./invoicex_socket --max_allowed_packet=64M";
    static public String win_stopDb = ".\\mysql\\bin\\mysqladmin.exe --no-defaults -W -u root -p{pwd} shutdown";
    static public String lin_stopDb = "./mysql/bin/mysqladmin --no-defaults --socket=./mysql/data/invoicex_socket --port={port} -u root -p{pwd} shutdown";
    static public String mac_stopDb = "./mysql/bin/mysqladmin --no-defaults --socket=./mysql/data/invoicex_socket --port={port} -u root -p{pwd} shutdown";
    static public String mac_stopDb_x86 = "./mysql/bin/mysql-5.1.58-x86-32bit-mysqladmin --no-defaults --socket=./mysql/data/invoicex_socket --port={port} -u root -p{pwd} shutdown";
    static public String mac_stopDb_x86_file = "./mysql/bin/mysql-5.1.58-x86-32bit-mysqladmin";
    static public boolean substance = false;
    static public String cache_img = System.getProperty("user.home") + File.separator + ".invoicex" + File.separator + "cache_img" + File.separator;
    static public String proxy = null;
    static public Font def_font = null;
    static public boolean primo_avvio = false;
    static public boolean db_in_rete = false;
    static public boolean fine_init_plugin = false;
    static JDialogPlugins dialogPlugins;
    static public boolean mysql_ready = false;
    static public String campiDatiAzienda = "ragione_sociale, indirizzo, localita, cap, provincia, telefono, fax, intestazione_riga1, intestazione_riga2, intestazione_riga3, intestazione_riga4, intestazione_riga5, intestazione_riga6, listino_base, targa, tipo_liquidazione_iva, id, piva,  cfiscale, flag_dati_inseriti, sito_web, email, testo_piede_fatt_v, testo_piede_docu_v, testo_piede_ordi_v, testo_piede_fatt_a, testo_piede_docu_a, testo_piede_ordi_a, stampa_riga_invoicex, label_cliente, label_destinazione, label_cliente_eng, label_destinazione_eng, provvigioni_tipo_data, stampare_timbro_firma, testo_timbro_firma, tipo_numerazione, export_fatture_codice_iva, export_fatture_conto_ricavi, export_fatture_estrai_scadenze";
    static public boolean via_internet = false;
    static public boolean wizard_in_corso = false;
    private static boolean check_connessione_fail = false;
    private static boolean check_connessione_ok = false;
    public static Map<String, String> plugins_note_attivazione = new HashMap();
    public static String note_attivazione = "";

    public static Frame getPadreWindow() {
        if (!Main.applet) {
            return main.padre_frame;
        } else {
            return main.padre_panel.getFrame();
        }
    }

    static public void splash(String string) {
        splash(string, null, null);
    }

    static public void splash(String string, Boolean indeterminate) {
        splash(string, indeterminate, null);
    }

    static public void splash(String string, Integer value) {
        splash(string, null, value);
    }

    static public void splash(final String string, final Boolean indeterminate, final Integer value) {
        if (System.getProperty("java.awt.headless") != null && System.getProperty("java.awt.headless").equalsIgnoreCase("true")) {
//            System.out.println("splash headless: " + string + " indeterminate: " + indeterminate + " value: " + value);
            return;
        } else {
            if (!Main.applet) {
                if (splash == null) {
//                    splash = new frmIntro(null);
                    splash = new JFrameIntro2();
//                    Dimension ScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
//                    splash.setBounds((ScreenSize.width - 502) / 2, (ScreenSize.height - 233) / 2, 502, 233);
                    splash.setLocationRelativeTo(null);
                    splash.setLogo();
                    splash.pack();
                    splash.setVisible(true);
                }
                splash.labMess.setText(StringUtils.capitalize(string));
                if (value != null) {
                    splash.jProgressBar1.setIndeterminate(false);
                    splash.jProgressBar1.setValue(value);
                } else if (indeterminate != null) {
                    splash.jProgressBar1.setIndeterminate(indeterminate);
                }
//                splash.toFront();
            } else {
                if (Main.appletinst == null) {
                    System.out.println("applet splash (inst null): " + string);
                    return;
                }
                try {
                    Field label1 = Main.appletinst.getClass().getField("jLabel1");
                    Field prog1 = Main.appletinst.getClass().getField("jProgressBar1");
                    JLabel l1 = (JLabel) label1.get(Main.appletinst);
                    JProgressBar p1 = (JProgressBar) prog1.get(Main.appletinst);
                    l1.setText(StringUtils.capitalize(string));
                    if (value != null) {
                        p1.setIndeterminate(false);
                        p1.setValue(value);
                    } else if (indeterminate != null) {
                        p1.setIndeterminate(indeterminate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void check_connessione() throws Exception {
        //faccio test su download t che deve contenere 1
        if (main.check_connessione_fail) {
            throw new Exception("Impossibile connettersi al server di Invoicex");
        }
        if (main.check_connessione_ok) {
            return;
        }
        String surltest = "http://www.tnx.it/pagine/invoicex_server/t";
        try {
            URL urltest = new URL(surltest);
            HttpURLConnection conntest = (HttpURLConnection) urltest.openConnection();
            String stest = IOUtils.toString(conntest.getInputStream());
            if (!stest.equals("1")) {
                main.check_connessione_fail = true;
                throw new Exception("Impossibile connettersi al server di Invoicex");
            } else {
                main.check_connessione_ok = true;
            }
        } catch (Exception e) {
            main.check_connessione_fail = true;
            throw new Exception("Impossibile connettersi al server di Invoicex (e:" + e.getMessage() + ")");
        }
    }

    private void controllaDati() {
        ResultSet r = null;
        boolean errors = false;
        Vector tablesWithError = new Vector();
        try {
            r = DbUtils.tryOpenResultSet(Db.getConn(), "show full tables");
            while (r.next()) {
                String tab = r.getString(1);
                String tipo = r.getString(2);
                if (tipo != null && !tipo.equalsIgnoreCase("VIEW")) {
                    String t = tab;
                    ResultSet rc = DbUtils.tryOpenResultSet(Db.getConn(), "check table " + t);
                    if (splash.isVisible()) {
                        splash("aggiornamenti struttura database ... check " + t);
                    }
                    try {
                        rc.next();
                        System.out.println(rc.getString(4));
                        System.out.println(r.getString(1) + " : checked");
                        if (!rc.getString(4).equals("OK")) {
                            if (rc.getString(4).indexOf("Found row where the auto_increment") < 0
                                    && !rc.getString(4).startsWith("View")) {
                                errors = true;
                                tablesWithError.add(r.getString(1));
                            }
                        }
                    } finally {
                        rc.getStatement().close();
                        rc.close();
                    }
                }
            }
        } catch (Exception err) {
            try {
                r.getStatement().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                r.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (errors == true) {
            //eseguo anche il repair
            try {
                for (int i = 0; i < tablesWithError.size(); i++) {
                    System.err.println("riparazione: " + tablesWithError.get(i).toString());
                    if (splash.isVisible()) {
                        splash("aggiornamenti struttura database ... repair " + tablesWithError.get(i).toString());
                    }
                    DbUtils.tryExecQuery(Db.getConn(), "repair table " + tablesWithError.get(i).toString());
                }
            } catch (Exception err) {
                err.printStackTrace();
            }

            //ricontrollo
            errors = false;
            tablesWithError.setSize(0);
            try {
                r = DbUtils.tryOpenResultSet(Db.getConn(), "show full tables");
                while (r.next()) {
                    String tab = r.getString(1);
                    String tipo = r.getString(2);
                    if (tipo != null && !tipo.equalsIgnoreCase("VIEW")) {
                        String t = tab;
                        splash("aggiornamenti struttura database ... check " + t);
                        ResultSet rc = DbUtils.tryOpenResultSet(Db.getConn(), "check table " + t);
                        try {
                            rc.next();
                            System.out.println(rc.getString(4));
                            System.out.println(r.getString(1) + " : checked");
                            if (!rc.getString(4).equals("OK")) {
                                if (rc.getString(4).indexOf("Found row where the auto_increment") < 0) {
                                    errors = true;
                                    tablesWithError.add(r.getString(1) + " : " + rc.getString(4));
                                }
                            }
                        } finally {
                            rc.getStatement().close();
                            rc.close();
                        }
                    }
                }
            } catch (Exception err) {
                try {
                    r.getStatement().close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    r.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (errors) {
                String msg = "Le seguenti tabelle sono danneggiate:\n\n";
                for (Object t : tablesWithError) {
                    msg += String.valueOf(t) + "\n";
                }
                msg += "\nPotrebbero verificarsi problemi nell'uso del programma finchè non vengono riparate.";
                SwingUtils.showWarningMessage(main.getPadreWindow(), msg);
            }
        }

    }

    static public class ProxyAuthenticator extends Authenticator {

        private String user;
        char[] password;
        boolean chiesto = false;
        String proxy;

        public ProxyAuthenticator(String proxy) {
            this.proxy = proxy;
        }

        protected PasswordAuthentication getPasswordAuthentication() {
            if (!chiesto) {
                JDialogProxyAuth dialog = new JDialogProxyAuth(main.getPadreFrame(), true);
                dialog.setTitle("Autenticazione Proxy: " + proxy);
                dialog.setLocationRelativeTo(main.getPadreWindow());
                dialog.setVisible(true);
                this.user = dialog.jTextField1.getText();
                this.password = dialog.jPasswordField1.getPassword();
                chiesto = true;
            }
            return new PasswordAuthentication(user, password);
        }
    }

    public main(String[] args) {
        INSTANCE = this;

        //aggiungo gestore di errori generici
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable e) {
                e.printStackTrace();

                //cerco dentro allo stacktrace se ci sono errori derivanti dalle classi di Invoicex
                boolean trovate = false;
                for (StackTraceElement trace : e.getStackTrace()) {
                    if (trace.getClassName().indexOf("gestioneFatture") >= 0 || trace.getClassName().indexOf("it.tnx") >= 0 || trace.getClassName().indexOf("reports") >= 0) {
                        if (trace.getClassName().indexOf("MyEventQueue") < 0) {
                            trovate = true;
                        }
                        break;
                    }
                }

                if (trovate && !(e instanceof NullPointerException)) {
                    Frame comp = null;
                    if (getPadreWindow() != null) {
                        comp = getPadreWindow();
                    } else {
                        comp = Frame.getFrames()[0];
                        System.out.println("uncaughtException padre windows nulla, prendo frames[0]: " + comp);
                    }
                    //                SwingUtils.showErrorMessage(comp, "Sì è verificato il seguente errore:\n" + e.toString());
                    JDialogExc de = new JDialogExc(comp, true, e);
                    de.setLocationRelativeTo(null);
                    de.pack();
                    Toolkit.getDefaultToolkit().beep();
                    de.setVisible(true);
                } else {
                    e.printStackTrace();
                }
                if (padre_frame != null) {
                    padre_frame.setCursor(Cursor.getDefaultCursor());
                }
                if (getPadreWindow() != null) {
                    getPadreWindow().setCursor(Cursor.getDefaultCursor());
                }
            }
        });

        //aggiungo controlli swing
//        EventDispatchThreadHangMonitor.initMonitoring();

        //aggiungo altro controllo swing
//        RepaintManager.setCurrentManager(new TracingRepaintManager());

        //mio gestore coda edt per attaccare tasto destro su griglie
        Toolkit.getDefaultToolkit().getSystemEventQueue().push(new MyEventQueue());

//        if (debug) {
//            timerMem.schedule(timerMemTask, 1000 * 5, 1000 * 5);
//        } else {
//            timerMem.schedule(timerMemTask, 1000 * 60, 1000 * 60);
//        }

        //aggiungo gestione proxy automatica e proxy authentication
//        System.setProperty("http.proxyHost", "proxy host");
//        System.setProperty("http.proxyPort", "port");
        System.setProperty("java.net.useSystemProxies", "true");

        //in caso in cui ci sia un proxy per http e non per socket normali come mysql sulle impostazioni del proxy mettere SOCKS senza proxy

//        MyProxySelector ps = new MyProxySelector(ProxySelector.getDefault());
//        ProxySelector.setDefault(ps);

        try {
            List l = ProxySelector.getDefault().select(new URI("http://www.tnx.it/"));
            for (Iterator iter = l.iterator(); iter.hasNext();) {
                Proxy nproxy = (Proxy) iter.next();
                System.err.println("proxy type : " + nproxy.type());
                InetSocketAddress addr = (InetSocketAddress) nproxy.address();
                if (addr == null) {
                    System.err.println("No Proxy");
                } else {
                    System.err.println("proxy hostname : " + addr.getHostName());
                    System.err.println("proxy port : " + addr.getPort());
                    proxy = addr.getHostName() + ":" + addr.getPort();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Authenticator.setDefault(new ProxyAuthenticator(proxy));

        System.out.println("properties principali");

        System.out.println("java.vendor:" + System.getProperty("java.vendor"));
        System.out.println("java.version:" + System.getProperty("java.version"));
        System.out.println("java.class.path:" + System.getProperty("java.class.path"));
        System.out.println("java.class.version:" + System.getProperty("java.class.version"));
        System.out.println("java.home:" + System.getProperty("java.home"));
        System.out.println("os.arch:" + System.getProperty("os.arch"));
        System.out.println("os.name:" + System.getProperty("os.name"));
        System.out.println("os.version:" + System.getProperty("os.version"));
        System.out.println("user.dir:" + System.getProperty("user.dir"));
        System.out.println("user.home:" + System.getProperty("user.home"));
        System.out.println("user.name:" + System.getProperty("user.name"));
        System.out.println("java.class.path:" + System.getProperty("java.class.path"));

        System.out.println("------------------------");

        if (debug) {
            System.out.println("tutte le properties");
            Properties props = System.getProperties();
            props.list(System.out);

            System.out.println("------------------------");
        }

        File fwd = new File("./");
        try {
            wd = fwd.getCanonicalPath() + File.separator;
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("wd:" + wd);

        if (Main.applet) {
            wd = System.getProperty("user.home") + "/.invoicex/applet_run/";
            fwd = new File(wd);
            fwd.mkdirs();
            System.out.println("wd cambiata per applet:" + wd);
        }

//        javax.xml.parsers.SAXParserFactory
//        org.apache.xerces.jaxp.SAXParserFactoryImpl
//        com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl;     //per mac con problemi...
//        -Djavax.xml.parsers.SAXParserFactory=com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl     //per mac con errore creating sax parser..
//        System.out.println("javax.xml.parsers.SAXParserFactory prima:" + System.getProperty("javax.xml.parsers.SAXParserFactory"));
//        System.setProperty("javax.xml.parsers.SAXParserFactory", "org.apache.xerces.jaxp.SAXParserFactoryImpl");
//        System.out.println("javax.xml.parsers.SAXParserFactory dopo:" + System.getProperty("javax.xml.parsers.SAXParserFactory"));

        //imposto anno di esercizio
        main.anno = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);

        //controllo quanti param_prop ci sono e se ce n'è più di uno faccio scegliere
        //param_prop.txt
        File filesParamDir = new File(main.wd);
        File[] filesParam = filesParamDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if (name.startsWith("param_prop") && name.endsWith(".txt")) {
                    return true;
                }
                return false;
            }
        });
        boolean scegli_config = false;
        if (filesParam.length > 1) {
            scegli_config = true;
        }
        if (args.length > 0) {
            for (String arg : args) {
                if (arg.startsWith("config=")) {
                    paramProp = arg.substring(7);
                    scegli_config = false;
                }
            }
        }
        if (scegli_config) {
            Object ret = JOptionPane.showInputDialog(null, "Seleziona la configurazione",
                    "Attenzione",
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    filesParam, filesParam[0]);
            System.out.println("ret:" + ret);
            try {
                paramProp = ((File) ret).getName();
            } catch (Exception e) {
                System.exit(50);
            }

        }

        //creo il prop vuoto
        File f = new File(main.wd + paramProp);

        if (!f.exists()) {
            primo_avvio = true;

            try {
                Properties prop = new Properties();
                FileOutputStream fos = new FileOutputStream(main.wd + paramProp);
                prop.store(fos, "tnx properties file");
                fos.close();
            } catch (Exception err) {
                err.printStackTrace();
            }

            //converto il param.ini in param_prop.txt
            iniFile fileIniOld = new iniFile();
            iniFileProp fileIniProp = new iniFileProp();
            fileIniProp.realFileName = paramProp;
            fileIniOld.fileName = "param.ini";

            if (Main.applet) {
                try {
                    HttpUtils.saveFile("http://www.tnx.it/pagine/invoicex_server/download/invoicex_dev/cl_test/run/param.ini", wd + "param.ini");
                    fileIniOld.fileName = wd + "param.ini";
                    fileIniProp.realFileName = wd + paramProp;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                try {
                    //scarico run pack
                    System.out.println("!!! applet, scarico run-pack.zip");
                    HttpUtils.saveFile("http://www.tnx.it/pagine/invoicex_server/download/invoicex_dev/cl_test/run-pack.zip", wd + "run-pack.zip");
                    unzip(new File(wd + "run-pack.zip"), wd + "/");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            try {
                fileIniOld.loadFile();
                fileIniOld.parseLines();
                fileIniProp.setValue("db", "server", fileIniOld.getValue("db", "server"));
                fileIniProp.setValue("db", "nome_database", fileIniOld.getValue("db", "nome_database"));
                fileIniProp.setValue("db", "user", fileIniOld.getValue("db", "user"));
                fileIniProp.setValueCifrato("db", "pwd", fileIniOld.getValue("db", "pwd"));
                fileIniProp.setValue("db", "startdb", fileIniOld.getValue("db", "startdb"));
                fileIniProp.setValue("db", "stopdb", fileIniOld.getValue("db", "stopdb"));
                fileIniProp.setValue("db", "checkdb", fileIniOld.getValue("db", "checkdb"));
                fileIniProp.setValue("db", "startdbcheck", fileIniOld.getValue("db", "startdbcheck"));
                fileIniProp.setValue("varie", "finestre_grandi", fileIniOld.getValue("varie", "finestre_grandi"));
                fileIniProp.setValue("varie", "percorso_logo_stampe", StringUtils.replace(fileIniOld.getValue("varie", "percorso_logo_stampe"), "\\", "/"));
                fileIniProp.setValue("varie", "percorso_logo_stampe_pdf", StringUtils.replace(fileIniOld.getValue("varie", "percorso_logo_stampe"), "\\", "/"));
                fileIniProp.setValue("varie", "percorso_sfondo_proforma", fileIniOld.getValue("varie", "percorso_sfondo_proforma"));
//                fileIniProp.setValue("varie", "look", fileIniOld.getValue("varie", "look"));
                fileIniProp.setValue("varie", "look", "System");
                fileIniProp.setValue("varie", "prezziCliente", fileIniOld.getValue("varie", "prezziCliente"));
                fileIniProp.setValue("varie", "campoSerie", fileIniOld.getValue("varie", "campoSerie"));
                fileIniProp.setValue("varie", "non_stampare_logo", fileIniOld.getValue("varie", "non_stampare_logo"));
                fileIniProp.setValue("varie", "messaggio_stampa", fileIniOld.getValue("varie", "messaggio_stampa"));
                fileIniProp.setValue("personalizzazioni", "personalizzazioni", fileIniOld.getValue("personalizzazioni", "personalizzazioni"));
                fileIniProp.setValue("iva", "codiceIvaSpese", fileIniOld.getValue("iva", "codiceIvaSpese"));
                fileIniProp.setValue("iva", "codiceIvaDefault", fileIniOld.getValue("iva", "codiceIvaDefault"));
                fileIniProp.setValue("info", "inst_id", fileIniOld.getValue("info", "inst_id"));
                fileIniProp.setValue("info", "inst_email", fileIniOld.getValue("info", "inst_email"));
                fileIniProp.setValue("info", "inst_seriale", fileIniOld.getValue("info", "inst_seriale"));
                fileIniProp.setValue("info", "inst_nome", fileIniOld.getValue("info", "inst_nome"));
                fileIniProp.setValue("info", "inst_cognome", fileIniOld.getValue("info", "inst_cognome"));

//                //param def
//                try {
//                    java.util.prefs.Preferences preferences = java.util.prefs.Preferences.userNodeForPackage(main.class);
//                    preferences.put("tipoStampa", "fattura_default.jrxml");
//                    preferences.put("tipoStampaFA", "fattura_acc_default.jrxml");
//                    preferences.put("tipoStampaDDT", "ddt_default.jrxml");
//                    preferences.put("tipoStampaOrdine", "ordine_default.jrxml");
//                    preferences.sync();
//                } catch (Exception ex1) {
//                    ex1.printStackTrace();
//                }

                //altri param di default
                fileIniProp.setValue("iva", "codiceIvaDefault", "22");
                fileIniProp.setValue("iva", "codiceIvaSpese", "");
                fileIniProp.setValue("pref", "generazione_movimenti", "1");
                fileIniProp.setValue("pref", "raggruppa_articoli", "2");
                fileIniProp.setValue("varie", "campoSerie", "N");
                fileIniProp.setValue("varie", "prezziCliente", "N");
                fileIniProp.setValue("pref", "soloItaliano", "true");
                fileIniProp.setValue("pref", "azioniPericolose", "true");
                fileIniProp.setValue("pref", "stampaPivaSotto", "true");
                
                fileIniProp.setValue("pref", "tipoStampa", "fattura_mod5_default.jrxml");
                fileIniProp.setValue("pref", "tipoStampaFA", "fattura_acc_mod5_default.jrxml");
                fileIniProp.setValue("pref", "tipoStampaOrdine", "ordine_mod5_default.jrxml");
                fileIniProp.setValue("pref", "tipoStampaDDT", "ddt_mod5_default.jrxml");                
            } catch (Exception err) {
                err.printStackTrace();
            }
        }

        //----------------------------------------
        //if (!serial.equals("1")) {
        File test = new File(main.wd + paramProp);

        if (test.exists() == false) {
            javax.swing.JOptionPane.showMessageDialog(null, "Errore, Impossibile trovare i parametri");
            this.exitMain();
        }

        //controllo il param prop per il problema "\u0000\u0000\u0000\u0000\u0000"
        MicroBench mb = new MicroBench(true);
        try {
            File fcheck0 = new File(main.wd + paramProp);
            FileReader fcheck0reader = new FileReader(fcheck0);
            Iterator fcheckiter = IOUtils.lineIterator(fcheck0reader);
            while (fcheckiter.hasNext()) {
                String line = cu.toString(fcheckiter.next());
                if (line.indexOf("\\u0000\\u0000\\u0000") >= 0) {
                    fcheck0reader.close();
                    //problema riscontrato, se trovo il backup prendo quello                
                    File fcheck0b = new File(main.wd + paramProp + ".backup");
                    if (fcheck0b.exists()) {
                        try {
                            System.err.println("!!! problema nel param_prop !!! provo copia da backup");
                            FileUtils.copyFile(fcheck0b, fcheck0);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        System.err.println("!!! problema nel param_prop !!! e non presente il backup");
                        SwingUtils.showErrorMessage(splash, "Il file di configurazione del programma (" + paramProp + ") è danneggiato\ne non è presente la copia di backup.\n\nImpossibile avviare il programma.\n\nPer avviare il programma con le impostazioni iniziali\neliminare manualmente il file di configurazione", true);
                        System.exit(1);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mb.out("test param danneggiato");

        fileIni = new iniFileProp();
        fileIni.realFileName = main.wd + paramProp;

        System.out.println("!!! applet ??? : " + Main.applet);
        if (Main.applet) {
            SwingUtils.showFlashMessage2("Impostazione dati database per Applet", 3);
            fileIni.setValue("db", "server", "due.tnx.it");
            fileIni.setValue("db", "nome_database", "inv_online");
            fileIni.setValue("db", "user", "inv_online");
            fileIni.setValueCifrato("db", "pwd", "Tsy7TCGH");
            fileIni.setValue("db", "startdbcheck", "N");
            fileIni.setValue("pref", "tipoStampa", "fattura_mod4_default.jrxml");
            fileIni.setValue("pref", "tipoStampaDDT", "ddt_mod4_default.jrxml");
            fileIni.setValue("pref", "tipoStampaFA", "fattura_acc_mod4_default.jrxml");
            fileIni.setValue("pref", "tipoStampaOrdine", "ordine_acc_mod4_default.jrxml");
        }

        if (fileIni.getValue("db", "nome_database", "").indexOf("toysforyou") >= 0 && !getPersonalContain("toysforyou")) {
            String nuovopers = fileIni.getValue("personalizzazioni", "personalizzazioni", "");
            nuovopers += ", toysforyou";
            fileIni.setValue("personalizzazioni", "personalizzazioni", nuovopers);
        }

        System.out.println("START APPLICATION");
        System.out.println("Versione " + version.toString() + " " + build);

        //setto italiano
        java.util.Locale.setDefault(java.util.Locale.ITALY);

        if (this.flagWebStart == false) {
            loadIni();
        }

        if (fileIni.getValue("db", "startdb") != null && fileIni.getValue("db", "startdb").equals("false")) {
            fileIni.setValue("db", "startdbcheck", "N");
            fileIni.saveFile();
        }

        //cambio invece che mettere startdb metto checkbox
        if (fileIni.getValue("db", "startdbcheck") == null || fileIni.getValue("db", "startdbcheck").length() == 0) {
            if (fileIni.getValue("db", "startdb") != null && fileIni.getValue("db", "startdb").length() > 0) {
                fileIni.setValue("db", "startdbcheck", "S");
            } else {
                fileIni.setValue("db", "startdbcheck", "N");
            }
            fileIni.saveFile();
        }

        if (fileIni.getValue("db", "startdbcheck") == null || fileIni.getValue("db", "startdbcheck").equalsIgnoreCase("N") || fileIni.getValue("db", "startdbcheck").equalsIgnoreCase("false")) {
            startDbCheck = false;
        } else {
            startDbCheck = true;
            startConDbCheck = true;
        }

        //per splash screen con bordo
        //System.setProperty("sun.java2d.noddraw", "true");

        //wizard db
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception err) {
        }
        WizardDb wdb = new WizardDb();
        wdb.execute();
    }

    private void controllaFlagPlugin(String plugin) {
        if (plugin.equals("pluginBackupTnx")) {
            //pluginBackupTnx = true;
        } else if (plugin.equals("pluginJR")) {
            pluginJR = true;
        } else if (plugin.equals("pluginClientManager")) {
            pluginClientManager = true;
        } else if (plugin.equals("pluginClientManager")) {
            pluginClientManager = true;
        } else if (plugin.equals("pluginAchievo")) {
            pluginAchievo = true;
        } else if (plugin.equals("pluginAutoUpdate")) {
            pluginAutoUpdate = true;
        } else if (plugin.equals("plulginEbay")) {
            pluginEbay = true;
        }
    }

    public void aggiornaStatoPlugin() {
        //init plugins
        Collection plugcol = pf.getAllEntryDescriptor();
        Iterator plugiter = plugcol.iterator();
        while (plugiter.hasNext()) {
            EntryDescriptor pd = (EntryDescriptor) plugiter.next();
            pluginPresenti.add(pd.getName());
            controllaAttivazionePlugin(pd, attivazione, pf);
            controllaFlagPlugin(pd.getName());
        }
    }

    private boolean controllaAttivazionePlugin(EntryDescriptor pd, Attivazione attivazione, PluginFactory pf) {
        return false;
    }

    static public EntryDescriptor getPluginDescriptor(String nome_breve) {
        Collection plugcol = pf.getAllEntryDescriptor();
        Iterator plugiter = plugcol.iterator();
        while (plugiter.hasNext()) {
            EntryDescriptor pd = (EntryDescriptor) plugiter.next();
            if (pd.getName().equalsIgnoreCase(nome_breve)) {
                return pd;
            }
        }
        return null;
    }

    static public PluginEntry getPluginEntry(String nome_breve) {
        Collection plugcol = pf.getAllEntryDescriptor();
        Iterator plugiter = plugcol.iterator();
        while (plugiter.hasNext()) {
            EntryDescriptor pd = (EntryDescriptor) plugiter.next();
            if (pd.getName().equalsIgnoreCase(nome_breve)) {
                return pf.getPluginEntry(pd.getId());
            }
        }
        return null;
    }

    static public void controllaupd() {
        String vl = version + " (" + build + ")";
        try {
            String url = "http://www.tnx.it/pagine/invoicex_server/v.php?v=" + URLEncoder.encode(vl, "UTF-8") + (main.attivazione.getIdRegistrazione() != null ? "&i=" + main.attivazione.getIdRegistrazione() : "") + "&so=" + URLEncoder.encode(System.getProperty("os.name") + ":" + System.getProperty("os.version"), "UTF-8") + "&javav=" + URLEncoder.encode(System.getProperty("java.version"), "UTF-8");
            System.out.println("url: " + url);
            String v = getURL(url);
//            if (pluginAutoUpdate == false && fileIni.getValueBoolean("pref", "msg_plugins_upd", true)) {
            if (pluginAutoUpdate == false && !fileIni.getValueBoolean("pref", "msg_plugins_upd_v_" + v, false)) {
                if (v != null && !vl.equalsIgnoreCase(v)) {
                    JDialogUpd d = new JDialogUpd(getPadreWindow(), true, v);
                    d.pack();
                    d.setLocationRelativeTo(null);
                    d.setVisible(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String dbserver = URLEncoder.encode(main.fileIni.getValue("db", "server"), "UTF-8");
            String dbname = URLEncoder.encode(main.fileIni.getValue("db", "nome_database"), "UTF-8");
            String plugins = "";
            for (String p : main.pluginPresenti) {
                plugins += p;
                boolean attivo = false;
                for (String pa : main.pluginAttivi) {
                    if (pa.equals(p)) {
                        attivo = true;
                        break;
                    }
                }
                if (attivo) {
                    plugins += ";A";
                } else {
                    plugins += ";";
                }
                plugins += "|";
            }
            plugins = URLEncoder.encode(plugins, "UTF-8");
            String url = (main.debug ? "http://www.demo.tnx.it" : "http://www.tnx.it/pagine") + "/invoicex_server/n.php?v=" + URLEncoder.encode(vl, "UTF-8") + (main.attivazione.getIdRegistrazione() != null ? "&i=" + main.attivazione.getIdRegistrazione() : "") + "&so=" + URLEncoder.encode(System.getProperty("os.name") + ":" + System.getProperty("os.version"), "UTF-8") + "&dbs=" + dbserver + "&dbn=" + dbname + "&ver=" + main.versione + "&p=" + plugins;
//            System.out.println("url: " + url);
            String news = getURL(url);
//            System.out.println("news: " + news);

            JSONParser p = new JSONParser();
            JSONObject jo = (JSONObject) p.parse(news);
//            System.out.println("jo: " + jo);
            JSONArray lista_news = (JSONArray) jo.get("n");
//            System.out.println(lista_news);
            for (Object nnews : lista_news) {
                try {
                    String id = (String) ((JSONObject) nnews).get("id");
                    if (main.fileIni.getValue("news", "non_visualizzare", "").indexOf(id + "|") < 0) {
                        JSONObject jn = (JSONObject) nnews;
                        String scadenza = (String) jn.get("scadenza");
                        if (scadenza != null && scadenza.length() > 0) {
                            try {
                                Date dscadenza = CastUtils.toDateIta(scadenza);
                                if (dscadenza != null) {
                                    if (dscadenza.before(new Date())) {
//                                        System.out.println("non faccio vedere news " + jn.get("id") + " perchè scaduta (scadenza: " + scadenza + ")");
                                        continue;
                                    }
                                }
                            } catch (Exception e) {
                            }
                        }

                        main.INSTANCE.getPadrePanel().showNews((JSONObject) nnews);
                        Thread.sleep(1000);
                        while (getPadrePanel().news.size() > 0) {
                            Thread.sleep(1000);
//                            System.out.println("getPadrePanel().news.size():" + getPadrePanel().news.size());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String getURL(String url) throws Exception {
        check_connessione();

        URL urlagg = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) urlagg.openConnection();
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        int retcode = conn.getResponseCode();
        long lastm = conn.getLastModified();
        int size = conn.getContentLength();
        System.out.println(conn.getContentType());
        conn.getContent();

        if (retcode != 200) {
            System.out.println("getURL: errore retcode:" + retcode + " resp:" + conn.getResponseMessage());
            return null;
        }
        InputStream is = new BufferedInputStream(conn.getInputStream());
        int readed = 0;
        int read = 0;
        byte[] buff = new byte[10000];
        String out = "";
        while ((read = is.read(buff)) > 0) {
            out += new String(buff, 0, read);
            readed += read;
        }
        is.close();
        return out;
    }

    public static void main(String[] args) {
        main main1 = new main(args);
    }

    public static void startdb() {

        try {

            //            String tempStartDb = fileIni.getValue("db","startdb");
            //            if (tempStartDb.length() > 0) {
            if (startDbCheck) {
                via_internet = true;
                System.out.println("os.name:" + System.getProperty("os.name"));
                System.out.println("os.name check:mac " + (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0));

                if (System.getProperty("os.name").toLowerCase().startsWith("mac") || System.getProperty("os.name").toLowerCase().startsWith("lin")) {

                    try {
                        System.out.println("cambio permessi per esecuzione mysqld");
                        Process p = Runtime.getRuntime().exec("chmod 777 mysql/bin/mysqld");
                        int ret = p.waitFor();
                        System.out.println("ret:" + ret);

                        System.out.println("cambio permessi per esecuzione mysqladmin");
                        p = Runtime.getRuntime().exec("chmod 777 mysql/bin/mysqladmin");
                        ret = p.waitFor();
                        System.out.println("mysqlproc ret:" + ret);

                        if (PlatformUtils.isMac()) {
                            if (!System.getProperty("os.arch").equals("ppc")) {
                                p = Runtime.getRuntime().exec("chmod 777 mysql/bin/mysql-5.1.58-x86-32bit-mysqladmin");
                                ret = p.waitFor();
                                System.out.println("mysqlproc-x86 ret:" + ret);
                            }
                        }
                    } catch (Exception err) {
                        err.printStackTrace();
                    }
                }

                System.out.println("STARTING DB");

                class StartDbThread
                        extends Thread {

                    public String tempStartDb;

                    public void run() {

                        Runtime rt = Runtime.getRuntime();

                        try {

                            Process proc = rt.exec(tempStartDb);
                            mysqlproc = proc;

                            // any error message?
                            MyStreamGobbler errorGobbler = new MyStreamGobbler(proc.getErrorStream(), "ERROR") {
                                @Override
                                public void line(String line) {
                                    if (!mysql_ready) {
                                        if (line.indexOf("ready for connections") >= 0) {
                                            mysql_ready = true;
                                        }
                                    }
                                }
                            };

                            // any output?
                            MyStreamGobbler outputGobbler = new MyStreamGobbler(proc.getInputStream(), "OUTPUT");

                            // kick them off
                            errorGobbler.start();
                            outputGobbler.start();

                            // any error???
                            int exitVal = proc.waitFor();
                            System.out.println("\t\t\t### dbt exit value: " + exitVal);
                        } catch (Exception err) {
                            err.printStackTrace();
                        }
                    }
                }

                StartDbThread dbt = new StartDbThread();
                dbt.tempStartDb = startDb;

                if (debug) {
                    System.out.println("stardb:" + startDb);
                }

                dbt.start();
                System.out.println("STARTING DB");
            } else {
                System.out.println("NO DB TO START");
                if (fileIni.getValue("db", "server").toLowerCase().endsWith("tnx.it")) {
                    via_internet = true;
                }
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    String spezza(String stringa, int ogni) {

        String temp = "";

        for (int i = 0; i < stringa.length(); i = i + ogni) {

            if ((i + ogni) > stringa.length()) {
                temp += stringa.substring(i, stringa.length()) + "\n";
            } else {
                temp += stringa.substring(i, i + ogni) + "\n";
            }
        }

        return (temp);
    }

    public static Menu getPadre() {
        return (padre);
    }

    public static MenuFrame getPadreFrame() {
        return (padre_frame);
    }

    public static MenuPanel getPadrePanel() {
        return (padre_panel);
    }

    static public void loadIni() {
        fileIni.loadFile();
        fileIni.parseLines();

        String server = null;
        Db.dbServ = fileIni.getValue("db", "server");

        //la porta la parso dal server
        if (Db.dbServ.length() > 0) {

            int ip = Db.dbServ.indexOf(":");

            if (ip > 0) {
                server = Db.dbServ.substring(0, ip);

                if (!startDbCheck) {

                    String porta = Db.dbServ.substring(ip + 1, Db.dbServ.length());

                    try {
                        Db.dbPort = Integer.parseInt(porta);
                    } catch (NumberFormatException err) {
                        System.out.println("la porta nel nome del server non e' numerica:" + porta);
                    }
                }
            }
        }

        //debug
        System.out.println("db server:" + Db.dbServ);
        Db.dbNameDB = fileIni.getValue("db", "nome_database");
        Db.dbName = fileIni.getValue("db", "user");
        //richiedo password
        if (!fileIni.getValueBoolean("pref", "richiediPassword", false)) {
            Db.dbPass = fileIni.getValueCifrato("db", "pwd");
        }

        if (fileIni.getValue("varie", "finestre_grandi").equalsIgnoreCase("si")) {
            iniFinestreGrandi = true;
        } else {
            iniFinestreGrandi = false;
        }

//        iniPercorsoLogoStampe = fileIni.getValue("varie", "percorso_logo_stampe");
//        iniPercorsoLogoStampePdf = fileIni.getValue("varie", "percorso_logo_stampe_pdf");

        if (fileIni.existKey("varie", "percorso_sfondo_stampe_" + Db.dbNameDB)) {
            iniPercorsoSfondoStampe = fileIni.getValue("varie", "percorso_sfondo_stampe_" + Db.dbNameDB);
        } else {
            iniPercorsoSfondoStampe = fileIni.getValue("varie", "percorso_sfondo_stampe");
        }
        if (fileIni.existKey("varie", "percorso_sfondo_stampe_pdf_" + Db.dbNameDB)) {
            iniPercorsoSfondoStampePdf = fileIni.getValue("varie", "percorso_sfondo_stampe_pdf_" + Db.dbNameDB);
        } else {
            iniPercorsoSfondoStampePdf = fileIni.getValue("varie", "percorso_sfondo_stampe_pdf");
        }

        System.out.println("main set iniPercorsoSfondoStampe:" + iniPercorsoSfondoStampe);
        System.out.println("main set iniPercorsoSfondoStampePdf:" + iniPercorsoSfondoStampePdf);


        iniPercorsoSfondoProforma = fileIni.getValue("varie", "percorso_sfondo_proforma");
        iniDirFatture = fileIni.getValue("varie", "percorso_fatture");
        iniComandoGs = fileIni.getValue("varie", "comando_gs");

        if (fileIni.getValue("varie", "gestione_magazzino").equalsIgnoreCase("no")) {
            iniFlagMagazzino = false;
        } else {
            iniFlagMagazzino = true;
        }

        if (fileIni.getValue("varie", "prezziCliente").equalsIgnoreCase("S")) {
            iniPrezziCliente = true;
        } else {
            iniPrezziCliente = false;
        }

        if (fileIni.getValue("varie", "campoSerie").equalsIgnoreCase("S")) {
            iniSerie = true;
        } else {
            iniSerie = false;
        }

        //debug
        //javax.swing.JOptionPane.showMessageDialog(null,"logo:" + iniPercorsoLogoStampe);
    }

    static private String apriFile(String nomeFile) {

        try {

            DataInputStream fileInput = new DataInputStream(new FileInputStream(new File(main.wd + nomeFile)));
            String righe = "";
            char in;
            int a = 1;

            try {

                while (a == 1) {
                    in = (char) fileInput.readByte();
                    righe += in;
                }
            } catch (EOFException err) {
            }

            return (righe);
        } catch (Exception err) {
            err.printStackTrace();

            return (null);
        }
    }

    public static void exitMain() {
        System.out.println("exitMain -> exitMain(true);");
        exitMain(true);
    }

    public static void exitMain(final boolean chiudiDb) {
        //premo annulla su eventuali testate di documento aperte
        try {
            Component[] arrComp = main.getPadrePanel().getDesktopPane().getComponents();
            for (int i = 0; i < arrComp.length; i++) {
                if (arrComp[i] instanceof frmTestFatt) {
                    System.out.println("chiudo frmTestFatt " + arrComp[i]);
                    ((frmTestFatt) arrComp[i]).annulla();
                } else if (arrComp[i] instanceof frmTestDocu) {
                    System.out.println("chiudo frmTestDocu " + arrComp[i]);
                    ((frmTestDocu) arrComp[i]).annulla();
                } else if (arrComp[i] instanceof frmTestOrdine) {
                    System.out.println("chiudo frmTestOrdine " + arrComp[i]);
                    ((frmTestOrdine) arrComp[i]).annulla();
                } else if (arrComp[i] instanceof frmTestFattAcquisto) {
                    System.out.println("chiudo frmTestFattAcquisto " + arrComp[i]);
                    ((frmTestFattAcquisto) arrComp[i]).annulla();
                }
            }
        } catch (Exception e) {
        }

        Thread t = new Thread("chiusura") {
            boolean attendere = true;

            @Override
            public void run() {
                //attendo che vengano effettivamente chiuse altrimenti si rischia di perdere le righe dei documenti.

                while (attendere) {
                    attendere = false;
                    try {
                        SwingUtilities.invokeAndWait(new Runnable() {
                            public void run() {
                                try {
                                    Component[] arrComp = main.getPadrePanel().getDesktopPane().getComponents();
                                    for (int i = 0; i < arrComp.length; i++) {
                                        if (arrComp[i] instanceof frmTestFatt) {
                                            attendere = true;
                                        } else if (arrComp[i] instanceof frmTestDocu) {
                                            attendere = true;
                                        } else if (arrComp[i] instanceof frmTestOrdine) {
                                            attendere = true;
                                        } else if (arrComp[i] instanceof frmTestFattAcquisto) {
                                            attendere = true;
                                        }
                                    }
                                } catch (Exception e) {
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (attendere) {
                        try {
                            System.out.println("attendo...");
                            Thread.sleep(1000);
                        } catch (Exception e) {
                        }
                    }
                }

                try {
                    if (!chiudiDb) {    //lo chiude dopo
                        Db.dbClose();
                    }
                } catch (Exception ex0) {
                    ex0.printStackTrace();
                }

                //stoppo i plugins
                try {
                    Set<Entry<String, PluginEntry>> pa = pluginsAvviati.entrySet();
                    for (Entry<String, PluginEntry> e : pa) {
                        try {
                            System.out.println("stoppo plugin: " + e.getKey());
                            MicroBench mb = new MicroBench();
                            mb.start();
                            PluginEntry pe = e.getValue();
                            pe.stopPluginEntry();
                            System.out.println("stoppo plugin: " + e.getKey() + " ... stoppato in " + mb.getDiff(""));
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (chiudiDb) {
                    stopdb(true);
                }

            }
        };
        t.start();

    }

    public static void stopdb(final boolean exit) {
        /*
         SwingUtilities.invokeLater(new Runnable() {
         public void run() {
         final JDialog dialog = new JDialogChiusura();
         if (exit) {
         dialog.setLocationRelativeTo(null);
         dialog.setVisible(true);
         }
         }
         });

         if (mysqlproc != null) {
         try {
         System.out.println("\t\t\t### mysqlproc0:" + mysqlproc);
         mysqlproc.getOutputStream().write(0x03);
         mysqlproc.getOutputStream().flush();
         mysqlproc.getOutputStream().write(0x03);
         mysqlproc.getOutputStream().flush();
         System.out.println("\t\t\t### mysqlproc1:" + mysqlproc);
         } catch (Exception err) {
         System.out.println(err + " / " + err.getStackTrace()[0]);
         }
         try {
         System.out.println("\t\t\t### mysqlproc2:" + mysqlproc);
         mysqlproc.destroy();
         System.out.println("\t\t\t### mysqlproc3:" + mysqlproc);
         } catch (Exception err) {
         System.out.println(err + " / " + err.getStackTrace()[0]);
         }
         }

         if (exit) {
         System.out.println("\t\t\t### exit");
         System.exit(0);
         }
         */

        final JDialog dialog = new JDialogChiusura(main.getPadreWindow(), true);
        if (exit) {
            try {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        dialog.setLocationRelativeTo(null);
                        dialog.setVisible(true);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        org.jdesktop.swingworker.SwingWorker wstop = new org.jdesktop.swingworker.SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                if (mysqlproc != null) {
                    Thread t = new Thread("wstop") {
                        @Override
                        public void run() {
                            try {
                                //killo le connessioni
                                Object conn_id = DbUtils.getObject(Db.conn, "select CONNECTION_ID()");
                                System.out.println("this conn id: " + conn_id);
                                List<Map> process = DbUtils.getListMap(Db.conn, "show processlist");
                                DebugFastUtils.dump(process);
                                for (Map m : process) {
                                    Long id = CastUtils.toLong(m.get("Id"));
                                    if (id.equals(conn_id)) {
                                        System.out.println("non killo conn: " + id + ", uguale a conn_id");
                                    } else {
//                                        System.out.println("kill conn: " + id);
//                                        DbUtils.tryExecQuery(Db.conn, "kill " + id);
                                    }
                                }
//                                System.out.println("kill my conn: " + conn_id);
                            } catch (Exception ex0) {
                                ex0.printStackTrace();
                            }
                            try {
                                Db.dbClose();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                Runtime rt = Runtime.getRuntime();
                                String stopdb = null;
                                if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
                                    System.out.println("stopdb per win");
                                    stopdb = win_stopDb;
                                } else if (System.getProperty("os.name").toLowerCase().indexOf("lin") >= 0) {
                                    System.out.println("stopdb per linux");
                                    stopdb = lin_stopDb;
                                } else if (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0) {
                                    System.out.println("stopdb per mac");
                                    stopdb = mac_stopDb;
                                    try {
                                        if (!System.getProperty("os.arch").equals("ppc")) {
                                            File ftest = new File(mac_stopDb_x86_file);
                                            if (ftest.exists()) {
                                                stopdb = mac_stopDb_x86;
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                stopdb = StringUtils.replace(stopdb, "{port}", String.valueOf(Db.dbPort));
                                System.out.println("stopdb = " + stopdb);
                                stopdb = StringUtils.replace(stopdb, "{pwd}", String.valueOf(Db.dbPass));

                                Process proc = rt.exec(stopdb);
                                MyStreamGobbler errorGobbler = new MyStreamGobbler(proc.getErrorStream(), "ERROR-mysqlstop");
                                MyStreamGobbler outputGobbler = new MyStreamGobbler(proc.getInputStream(), "OUTPUT-mysqlstop");
                                errorGobbler.start();
                                outputGobbler.start();

                                // any error???
                                int exitVal = proc.waitFor();
                                System.out.println("\t\t\t### mysqlstop exit value: " + exitVal);

//                                System.out.println("\t\t\t### mysqlproc0:" + mysqlproc);
//                                mysqlproc.getOutputStream().write(0x03);
//                                mysqlproc.getOutputStream().flush();
//                                mysqlproc.getOutputStream().write(0x03);
//                                mysqlproc.getOutputStream().flush();
//                                System.out.println("\t\t\t### mysqlproc1:" + mysqlproc);
                            } catch (Exception err) {
                                System.out.println(err + " / " + err.getStackTrace()[0]);
                            }
//                            try {
//                                System.out.println("\t\t\t### mysqlproc2:" + mysqlproc);
//                                mysqlproc.destroy();
//                                System.out.println("\t\t\t### mysqlproc3:" + mysqlproc);
//                            } catch (Exception err) {
//                                System.out.println(err + " / " + err.getStackTrace()[0]);
//                            }
                        }
                    };
                    t.start();
                    long t1 = System.currentTimeMillis();
                    long t2 = System.currentTimeMillis();

                    while (t.isAlive() && (t2 - t1) < 10000 * 2) {
                        System.out.println("attendo mysqlproc");
                        try {
                            Thread.sleep(500);
                        } catch (Exception e) {
                        }
                        t2 = System.currentTimeMillis();
                    }
                    System.out.println("exit while wstop");
                }

                if (exit) {
                    System.out.println("\t\t\t### exit");
                    try {
                        if (main.getPadreFrame() != null) {
                            main.getPadreFrame().setVisible(false);
                            main.getPadreFrame().dispose();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.exit(0);
                }

                return null;
            }

            @Override
            protected void done() {
                if (dialog != null) {
                    dialog.dispose();
                }
            }

            @Override
            protected void process(List chunks) {
                super.process(chunks);
            }
        };
        wstop.execute();
    }

    public static java.net.URL getImageUrl(String value) {

        try {

            if (main.flagWebStart == false) {

                return new java.net.URL(value);
            } else {

                java.net.URL tmImageURL = Class.class.getResource("/" + value);

                return tmImageURL;
            }
        } catch (java.net.MalformedURLException errUrl) {
            System.err.println("getImageUrl:malformedUrl:" + value);

            return null;
        }
    }

    public static javax.swing.ImageIcon getImageIcon(Object parent, String value) {

        java.net.URL url = null;

        try {

            //javax.swing.JOptionPane.showMessageDialog(null, "value:" + value.substring(2));
            if (main.flagWebStart == false) {
                url = new java.net.URL("file:" + value);
            } else {

                // Get current classloader
                ClassLoader cl = parent.getClass().getClassLoader();

                // Create icons
                //Icon icon = new ImageIcon(cl.getResource("img/general/New16.gif"));
                return new javax.swing.ImageIcon(cl.getResource(value.substring(2)));

                //url = Class.class.getResource(value.substring(2));
                //return new javax.swing.ImageIcon(icon);
            }

            //javax.swing.JOptionPane.showMessageDialog(null, "url:" + url.toString());
            return new javax.swing.ImageIcon(url);
        } catch (Exception err) {
            System.err.println("getImageIcon:err:" + value);
            err.printStackTrace();

            return null;
        }
    }

    public static com.lowagie.text.Image getItextImage(Object parent, String fileName) {

        String fileNameCorrected;

        if (main.flagWebStart == false) {

            //in casoo venga lanciato da installazione normale
            try {

                return com.lowagie.text.Image.getInstance(fileName);
            } catch (Exception errNows) {
                errNows.printStackTrace();
            }
        } else {

            //in caso di webstart
            try {

                if (fileName.startsWith(".")) {
                    fileNameCorrected = fileName.substring(2);
                } else if (fileName.startsWith("/")) {
                    fileNameCorrected = fileName.substring(1);
                } else {
                    fileNameCorrected = fileName;
                }
            } catch (Exception errString) {
                fileNameCorrected = fileName;
            }

            try {

                ClassLoader cl = parent.getClass().getClassLoader();

                //javax.swing.JOptionPane.showMessageDialog(null, "file:" + cl.getResource("img/logo.gif").getFile());
                //InputStream iStream = cl.getResource("img/logo.gif").openStream();
                InputStream iStream = cl.getResource(fileNameCorrected).openStream();

                // Create an input stream
                int bufferSize = 8192;
                BufferedInputStream responseStream = new BufferedInputStream(iStream, bufferSize);

                // Read some data
                byte[] inputBuffer = new byte[bufferSize];
                ByteBuffer bb = new ByteBuffer();
                int inputSize = responseStream.read(inputBuffer);
                int count = 0;

                // while there is data read some and blast it into the
                while (inputSize > 0) {
                    System.out.println(inputBuffer);
                    bb.append(inputBuffer);
                    count += inputSize;

                    // read next chunk of data
                    inputSize = responseStream.read(inputBuffer);
                }

                responseStream.close();

                return com.lowagie.text.Image.getInstance(bb.toByteArray());
            } catch (Exception errImage) {
                errImage.printStackTrace();
            }
        }

        return null;
    }

    public static String getPersonal() {

        if (Util.getIniValue("personalizzazioni", "personalizzazioni").indexOf("gianni1") >= 0) {

            return PERSONAL_GIANNI;
        } else if (Util.getIniValue("personalizzazioni", "personalizzazioni").indexOf("tnx1") >= 0) {

            return PERSONAL_TNX;
        } else if (Util.getIniValue("personalizzazioni", "personalizzazioni").indexOf("cucinain1") >= 0) {


            return PERSONAL_CUCINAIN;
        } else if (Util.getIniValue("personalizzazioni", "personalizzazioni").indexOf("tlz1") >= 0) {

            return PERSONAL_TLZ;
        } else if (Util.getIniValue("personalizzazioni", "personalizzazioni").indexOf("lux1") >= 0) {

            return PERSONAL_LUXURY;
        } else {

            return PERSONAL_TNX;
        }
    }

    public static boolean getPersonalContain(String personal) {

        if (Util.getIniValue("personalizzazioni", "personalizzazioni").indexOf(personal) >= 0) {

            return true;
        } else {

            return false;
        }
    }

    public static String getProperty(String prop) {

        if (applicationProps == null) {
            return null;
        }

        return applicationProps.getProperty(prop);
    }

    public static String getListinoBase() {

        ResultSet r = Db.openResultSet("select listino_base from dati_azienda");

        try {

            if (r.next()) {

                return r.getString(1);
            }
        } catch (java.sql.SQLException sqlErr) {
            sqlErr.printStackTrace();
        }

        return "-1";
    }

    public static String getTargaStandard() {

        ResultSet r = Db.openResultSet("select targa from dati_azienda");

        try {

            if (r.next()) {

                return r.getString(1);
            }
        } catch (java.sql.SQLException sqlErr) {
            sqlErr.printStackTrace();
        }

        return "";
    }

    public void post_wizard() throws Exception {
        try {
            //carico impostazioni personali
            // Get the Preferences object.  Note, the backing store is unspecified
            java.util.prefs.Preferences preferences = java.util.prefs.Preferences.userNodeForPackage(main.class);
            String fontName = preferences.get("fontName", "Dialog");
            int fontSize = preferences.getInt("fontSize", 12);
            int fontSizePiccolo = preferences.getInt("fontSizePiccolo", 10);

            //look
            if (main.fileIni.getValue("varie", "look").equalsIgnoreCase("Simple")) {
                System.out.println("Simple look and feel");
                MetalTheme theme = new gestioneFatture.look.TnxSandTheme();
                MetalLookAndFeel.setCurrentTheme(theme);
                try {
                    UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                } catch (Exception e) {
                    System.out.println(e);
                }
            } else if (main.fileIni.getValue("varie", "look").equalsIgnoreCase("System") || Main.applet) {

                String laf = UIManager.getSystemLookAndFeelClassName();
                UIManager.setLookAndFeel(laf);
            } else if (main.fileIni.getValue("varie", "look").equalsIgnoreCase("Substance Nebula")) {
                UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceNebulaLookAndFeel");
            } else if (main.fileIni.getValue("varie", "look").equalsIgnoreCase("Substance BusinessBlackSteel")) {
                UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceBusinessBlueSteelLookAndFeel");
            } else if (main.fileIni.getValue("varie", "look").equalsIgnoreCase("Substance Creme")) {
                UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceCremeLookAndFeel");
            } else if (main.fileIni.getValue("varie", "look").equalsIgnoreCase("Tonic")) {
                UIManager.setLookAndFeel("com.digitprop.tonic.TonicLookAndFeel");
            } else if (main.fileIni.getValue("varie", "look").equalsIgnoreCase("Office 2003")) {
                UIManager.setLookAndFeel("org.fife.plaf.Office2003.Office2003LookAndFeel");
            } else if (main.fileIni.getValue("varie", "look").equalsIgnoreCase("Office XP")) {
                UIManager.setLookAndFeel("org.fife.plaf.OfficeXP.OfficeXPLookAndFeel");
            } else if (main.fileIni.getValue("varie", "look").equalsIgnoreCase("Visual Studio 2005")) {
                UIManager.setLookAndFeel("org.fife.plaf.VisualStudio2005.VisualStudio2005LookAndFeel");
            } else if (main.fileIni.getValue("varie", "look").equalsIgnoreCase("JGoodies Plastic XP")) {
                if (PlatformUtils.isMac()) {
                    UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticLookAndFeel");
                } else {
                    UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
                }
            } else {
                if (PlatformUtils.isMac()) {
//                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceCremeLookAndFeel");
                } else {
                    UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
                }
            }
            //UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception err) {
            err.printStackTrace();
        }

        if (UIManager.getLookAndFeel() instanceof PlasticLookAndFeel) {
            PlasticXPLookAndFeel l = new PlasticXPLookAndFeel();
            l.setPlasticTheme(new ExperienceBlue());
            try {
                UIManager.setLookAndFeel(l);
            } catch (UnsupportedLookAndFeelException ex) {
                Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

//        if (PlatformUtils.isWindows()) {
//            UIDefaults uiDefaults = UIManager.getDefaults();
//            uiDefaults.put("Label.font", uiDefaults.get("TextField.font"));
//            uiDefaults.put("TextField.font", uiDefaults.get("Label.font"));
//        }


        def_font = UIManager.getDefaults().getFont("Label.font");
        System.out.println("exits font_family: " + fileIni.existKey("pref", "font_family"));
        System.out.println("font family: " + fileIni.getValue("pref", "font_family", main.def_font.getFamily()));
        System.out.println("font size: " + CastUtils.toInteger0(fileIni.getValue("pref", "font_size", CastUtils.toString(main.def_font.getSize()))));
        if (fileIni.existKey("pref", "font_family")) {
            String font_family = fileIni.getValue("pref", "font_family", main.def_font.getFamily());
            Integer font_size = CastUtils.toInteger0(fileIni.getValue("pref", "font_size", CastUtils.toString(main.def_font.getSize())));
            Font newf = new Font(font_family, Font.PLAIN, font_size);
            try {
                UIDefaults uiDefaults = UIManager.getLookAndFeelDefaults();
                Enumeration e = uiDefaults.keys();
                while (e.hasMoreElements()) {
                    try {
                        Object item = e.nextElement();
                        if (item.toString().toLowerCase().indexOf("font") >= 0) {
                            Font foc = (Font) uiDefaults.get(item);
                            uiDefaults.put(item, newf);
//                        } else {
//                            String k = item.toString().toLowerCase();
//                            String ko = item.toString();
//                            if (k.indexOf("margin") >= 0 || k.indexOf("gap") >= 0 || k.indexOf("offset") >= 0 || k.indexOf("border") >= 0) {
//                                Object o = uiDefaults.get(ko);
//                                if (o != null) {
//                                    System.out.println(ko + "| " + o + " | class:" + o.getClass());
//                                    if (o instanceof Integer) {
//                                        uiDefaults.put(ko, 1);
//                                    } else if (o instanceof InsetsUIResource)  {
//                                        InsetsUIResource inset = (InsetsUIResource)o;
//                                        inset.set(1, 1, 1, 1);
//                                        uiDefaults.put(ko, inset);
//                                    }
//                                }
//                            }
                        }
                    } catch (Exception ex) {
                    }
                }
                System.out.println("fine ui");
            } catch (Exception ex2) {
            }
        } else {
            if (PlatformUtils.isMac()) {
                try {
                    UIDefaults uiDefaults = UIManager.getLookAndFeelDefaults();
                    Enumeration e = uiDefaults.keys();
                    //Font fo = new Font("Geneva", Font.PLAIN, 10);
                    Font fo = new Font("Lucida Grande", Font.PLAIN, def_font.getSize() - 3);
                    while (e.hasMoreElements()) {
                        try {
                            Object item = e.nextElement();
                            if (item.toString().toLowerCase().indexOf("font") >= 0) {
                                Font foc = (Font) uiDefaults.get(item);
                                uiDefaults.put(item, fo);
                            }
                        } catch (Exception ex) {
                        }
                    }
                } catch (Exception e) {
                }
            } else if (PlatformUtils.isLinux()) {
                try {
                    UIDefaults uiDefaults = UIManager.getLookAndFeelDefaults();
                    Enumeration e = uiDefaults.keys();
                    Font fo = new Font("Sans", Font.PLAIN, 10);
                    while (e.hasMoreElements()) {
                        try {
                            Object item = e.nextElement();
                            if (item.toString().toLowerCase().indexOf("font") >= 0) {
                                Font foc = (Font) uiDefaults.get(item);
                                uiDefaults.put(item, fo);
                            }
                        } catch (Exception ex) {
                        }
                    }
                } catch (Exception ex2) {
                }
            }
        }

        if (UIManager.getLookAndFeel().getName().toLowerCase().indexOf("substance") >= 0) {
            substance = true;
        }

        //JIDE
        if (!PlatformUtils.isLinux()) {
            LookAndFeelFactory.installJideExtension(LookAndFeelFactory.XERTO_STYLE_WITHOUT_MENU);
        }

//        //setto font per prova
//        //Segoe UI
//        try {
//            UIDefaults uiDefaults = UIManager.getLookAndFeelDefaults();
//            Enumeration e = uiDefaults.keys();
//            Font fo = new Font("Segoe UI", Font.PLAIN, 13);
//            while (e.hasMoreElements()) {
//                try {
//                    Object item = e.nextElement();
//                    if (item.toString().toLowerCase().indexOf("font") >= 0) {
//                        Font foc = (Font) uiDefaults.get(item);
//                        uiDefaults.put(item, fo);
//                    }
//                } catch (Exception ex) {
//                }
//            }
//        } catch (Exception ex2) {
//        }

//        Splash splash1 = new Splash();
//        if (WindowUtils.isWindowAlphaSupported()) {
//            WindowUtils.setWindowAlpha(splash1, 0.9f);
//        }
//        splash1.setVisible(true);

        //--------------------------------------------------------------------
        if (Main.applet) {
            //devo trovare l'applet
            Class clazz;
            try {
                clazz = null;
                try {
                    System.out.println("!!! cl 0 = " + Thread.currentThread().getContextClassLoader());
                    ClassLoader cl = Thread.currentThread().getContextClassLoader();
                    cl = cl.getParent();
                    System.out.println("!!! cl 0.1 = " + cl);
                    clazz = Class.forName("testappletinvoicex.JAppletInvoicex", false, cl);
                    System.out.println("!!! clazz 2 = " + clazz);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Field field = clazz.getDeclaredField("INSTANCE");
                Object appletinst = field.get(null);
                System.out.println("!!! appletinst = " + appletinst);
                Main.appletinst = appletinst;
            } catch (Exception ex) {
                System.out.println("!!! clazz ex");
                ex.printStackTrace();
            }
        }

        //intro
        splash("caricamento", 25);
        //--------------------------------------------------------------------

        if (System.getProperty("os.name").toLowerCase().indexOf("win") < 0) {

            //con windows utilizzo named pipes e non ho bisogno di porte aperte
            //test porte db
            //trovo una porta libera per mysql
            Db connessioneTest = new Db();
            if (startDbCheck) {
                int portaMin = 3306;
                //la porta la parso dal server //NO, parto dalla 3306 e provo ad aumentare
                //            if (Db.dbServ.length() > 0) {
                //                int ip = Db.dbServ.indexOf(":");
                //                if (ip > 0) {
                //                    String porta = Db.dbServ.substring(ip+1, Db.dbServ.length());
                //                    try {
                //                        portaMin = Integer.parseInt(porta);
                //                    } catch (NumberFormatException err) {
                //                        System.out.println("la porta nel nome del server non e' numerica:" + porta);
                //                    }
                //                }
                //            }
                int portaMax = portaMin + 10;
                int portaProva = portaMin;
                boolean portaOk = false;
                while (portaOk == false) {
                    try {
                        System.out.println("test porta : " + portaProva);
                        System.out.println("test porta esito ok: " + portaProva);
                        System.out.println("controllo che non ci sia già un mysql defunto..");
                        //controllo processi
                        String serviceName = "mysqld";
                        String s = "";
                        try {
                            Runtime Rt = Runtime.getRuntime();
                            InputStream ip = Rt.exec("ps axw").getInputStream();
                            BufferedReader in = new BufferedReader(new InputStreamReader(ip));
                            while ((s = in.readLine()) != null) {
                                System.out.println("ps:" + s);
                                if (s.indexOf(serviceName) >= 0 && s.indexOf(String.valueOf(portaProva)) >= 0) {
                                    System.out.println("!!:" + s);
                                    String pidps = s.trim().split(" ")[0];
                                    //confronto il pid da ps con quello in mysql\data
                                    String pidfile = null;
                                    try {
                                        File fpid = new File(main.wd + "mysql/data/mysql_invoicex.pid");
                                        if (fpid.exists()) {
                                            BufferedReader fr = new BufferedReader(new InputStreamReader(new FileInputStream(fpid)));
                                            pidfile = fr.readLine();
                                        }
                                    } catch (Exception ex) {
                                        System.out.println("ex:" + ex.toString());
                                    }
                                    System.out.println("pid da ps:" + pidps + " pid da file:" + pidfile);
                                    System.out.println("kill " + pidps);
                                    InputStream ip2 = Rt.exec("kill -9 " + pidps).getInputStream();
                                    BufferedReader in2 = new BufferedReader(new InputStreamReader(ip2));
                                    while ((s = in2.readLine()) != null) {
                                        System.out.println("out kill:" + s);
                                    }
                                    System.out.println("fine kill");
                                    try {
                                        Thread.sleep(3000);
                                    } catch (Exception ex) {
                                    }
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //primo test con socket
                        try {
                            Socket client = new Socket();
                            client.connect(new InetSocketAddress("127.0.0.1", portaProva), 2000);
                            portaProva++;
                            client.close();
                        } catch (IOException ioexp0) {
                            //errore quindi dovrebbe essere libera..
                            //secondo test con server socket
                            ServerSocket socket = new ServerSocket(portaProva);
                            portaOk = true;
                            Db.dbPort = portaProva;
                            dbPortaOk = portaProva;
                            socket.close();
                        }

                    } catch (IOException ioexp) {
                        System.out.println("test porta esito KO: " + ioexp);
                        portaProva++;
                    }
                }
                if (portaOk == false) {
                    JOptionPane.showMessageDialog(null, "Impossibile attivare il database: nessuna porta libera da " + portaMin + " a " + portaMax, "Errore", JOptionPane.ERROR_MESSAGE);
                    exitMain(false);
                }
            } else {
                if (Db.dbPort == 0) {
                    Db.dbPort = 3306;
                }
            }
        } else {
            //controllo su windows se è rimasto un mysql di invoicex aperto (solitamente quando si termina invoicex brutalmente)
            try {
                List<String> processes = SystemUtils.listRunningProcesses();
                String result = "";
                Iterator<String> it = processes.iterator();
                String pid = null, args = "";
                while (it.hasNext()) {
                    result = it.next();
                    if (result.startsWith("mysqld-nt.exe")) {
                        args = StringUtils.split(result, "|")[1];
                        if (args.equalsIgnoreCase(win_startDb)) {
                            pid = StringUtils.split(result, "|")[2];
                            System.out.println("trovato mysqld e provo kill : " + pid + " args:" + result);
                            splash("chiusura precedente mysqld pid " + pid);
                            SystemUtils.killProcess(pid);
                            //attendo che venga chiuso
                            Thread.sleep(5000);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //richiedo password
        if (fileIni.getValueBoolean("pref", "richiediPassword", false)) {
            String pwd = SwingUtils.showInputPassword(splash, "Invoicex, password di accesso");
            if (pwd == null) {
                System.exit(0);
            }
            Db.dbPass = pwd;
        }

        if (splash != null) {
//            splash.toFront();
        }

        try {
            if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
                System.out.println("startdb per win");
                startDb = win_startDb;
            } else if (System.getProperty("os.name").toLowerCase().indexOf("lin") >= 0) {
                System.out.println("startdb per linux");
                startDb = StringUtils.replace(lin_startDb, "{port}", String.valueOf(Db.dbPort));
            } else if (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0) {
                System.out.println("startdb per mac");
                startDb = StringUtils.replace(mac_startDb, "{port}", String.valueOf(Db.dbPort));
            }
            fileIni.setValue("db", "startdb", startDb);
        } catch (Exception err) {
            err.printStackTrace();
        }

        if (startDbCheck) {
            db_in_rete = false;
            if (this.flagWebStart == false) {
                startdb();
                //prima controllo per 5 secondi se è arrivato il mysql_ready -> ready for connection da output
                splash("attesa dell'avvio di mysql", true);
                long t1 = System.currentTimeMillis();
                long t2 = System.currentTimeMillis();
                while (!mysql_ready && (t2 - t1) < 5000) {
                    System.out.println("attendo mysql_ready");
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                    }
                    t2 = System.currentTimeMillis();
                }
                System.out.println("mysql_ready: " + mysql_ready);

                //se mac su x86 scarico mysqladmin da http://www.tnx.it/pagine/invoicex_server/download/invoicex/utils/mysql-5.1.58-x86-32bit/
                try {
                    if (PlatformUtils.isMac()) {
                        if (!System.getProperty("os.arch").equals("ppc")) {
                            Thread t = new Thread("scarica mysqladmin") {
                                @Override
                                public void run() {
                                    try {
                                        File ren1 = new File("mysql/bin/mysql-5.1.58-x86-32bit-mysqladmin.temp");
                                        File ren2 = new File("mysql/bin/mysql-5.1.58-x86-32bit-mysqladmin");
                                        if (!ren2.exists()) {
                                            if (ren1.exists()) {
                                                ren1.delete();
                                            }
                                            HttpUtils.saveBigFile("http://www.tnx.it/pagine/invoicex_server/download/invoicex/utils/mysql-5.1.58-x86-32bit/mysqladmin", "mysql/bin/mysql-5.1.58-x86-32bit-mysqladmin.temp");
                                            ren1.renameTo(ren2);
                                            System.out.println("cambio permessi per esecuzione mysqladmin x86");
                                            Process p = Runtime.getRuntime().exec("chmod 777 mysql/bin/mysql-5.1.58-x86-32bit-mysqladmin");
                                            int ret = p.waitFor();
                                            System.out.println("mysqlproc ret:" + ret);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            t.start();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            db_in_rete = true;
        }

        try {
            login = System.getProperty("user.name") + "@" + InetAddress.getLocalHost().getHostName();
        } catch (Exception err) {
            login = System.getProperty("user.name") + "@???";
        }
        login = DbUtils.aa(login);
        System.out.println("login:" + login);

        //cambio da pref a file
        if (!fileIni.existKey("pref", "noteStandard")) {
            fileIni.setValue("pref", "noteStandard", prefs.get("noteStandard", ""));
        }

        //passo tutte le altre prefs in file
        if (!fileIni.existKey("pref", "visualizzaTotali")) {
            fileIni.setValue("pref", "visualizzaTotali", prefs.get("visualizzaTotali", ""));
            fileIni.setValue("pref", "stampaTelefono", prefs.get("stampaCellulare", "false"));
            fileIni.setValue("pref", "stampaCellulare", prefs.get("stampaCellulare", "false"));
            fileIni.setValue("pref", "stampaDestDiversaSotto", prefs.get("stampaDestDiversaSotto", "false"));
            fileIni.setValue("pref", "stampaPdf", prefs.get("stampaPdf", "false"));
            fileIni.setValue("pref", "azioniPericolose", prefs.get("azioniPericolose", ""));
            fileIni.setValue("pref", "limit", prefs.get("limit", ""));
            fileIni.setValue("pref", "soloItaliano", prefs.get("soloItaliano", ""));
            fileIni.setValue("pref", "multiriga", prefs.get("multiriga", "true"));
//            fileIni.setValue("pref", "tipoStampa", prefs.get("tipoStampa", ""));
//            fileIni.setValue("pref", "tipoStampaFA", prefs.get("tipoStampaFA", ""));
//            fileIni.setValue("pref", "tipoStampaDDT", prefs.get("tipoStampaDDT", ""));
//            fileIni.setValue("pref", "tipoStampaOrdine", prefs.get("tipoStampaOrdine", ""));
        }

        splash("tentativo di connessione al database", true);

        if (this.flagWebStart == false) {
            Db connessione = new Db();
            if (startDbCheck) {
                Db.dbServ = "127.0.0.1:" + dbPortaOk;
                Db.dbPort = dbPortaOk;
                if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
                    Db.useNamedPipes = true;
                }
            }
            //faccio test di connessione
            int proveConn = 0;
            boolean connOk = false;
            while (!connOk && proveConn < 2) {
                System.out.println("prova connessione:" + proveConn);
                if (proveConn >= 1) {
                    splash("connessione al database, tentativo " + proveConn);
                }
                if (connessione.dbConnect(true)) {
                    connOk = true;
                    System.out.println("prova connessione:ok");
                    try {
                        connessione.conn.close();
                    } catch (Exception ex) {
                    }
                } else {
                    System.out.println("prova connessione:ko aspetto...");
                    try {
                        Thread.sleep(1000);
                        if (startDbCheck) {
                            //tento stop e ritento start
                            stopdb(false);
                            Thread.sleep(1000);
                            startdb();
                            Thread.sleep(1000);
                        }
                    } catch (InterruptedException ierr) {
                    }
                }
                proveConn++;
            }

            System.out.println("fine test conn db");

//            if (!connessione.dbConnect() == true) {
            if (!connOk) {
                String msg = null;
                if (connessione.last_connection_err_msg != null) {
                    msg = connessione.last_connection_err_msg;
                }
                Throwable e = connessione.last_connection_err;
                JDialogExc de = null;
                if (msg == null) {
                    msg = "Impossibile aprire i dati\n\n";
                    msg = msg + " [Errore:" + StringUtils.abbreviate(e.toString(), 50) + "]";
                    de = new JDialogExc(new JFrame(), true, e);
                    de.labInt.setFont(de.labInt.getFont().deriveFont(Font.BOLD, 14));
                } else {
                    if (e.getCause() instanceof UnknownHostException) {
                        de = new JDialogExc(new JFrame(), true, null);
                    } else {
                        de = new JDialogExc(new JFrame(), true, e.getCause());
                    }
                    de.labInt.setFont(de.labInt.getFont().deriveFont(Font.BOLD, 16));
                }
                de.labInt.setText(msg);
                de.labe.setFont(de.labInt.getFont().deriveFont(Font.PLAIN, 14));
                de.pack();
                de.setLocationRelativeTo(null);
                de.setVisible(true);

                if (fileIni.getValueBoolean("pref", "richiediPassword", false)) {
                    exitMain(true);
                    return;
                } else {
                    if (SwingUtils.showYesNoMessage(null, "Vuoi provare a cambiare le impostazioni per risolvere il problema ?")) {
                        //provo ad impostare parametri
                        JDialogImpostazioni dialog = new JDialogImpostazioni(null, true, true);
                        if (main.wizard_in_corso) {
                            return;
                        } else {
                            //rileggo
                            fileIni.loadFile();
                            fileIni.parseLines();

                            if (fileIni.getValue("db", "startdbcheck") == null || fileIni.getValue("db", "startdbcheck").equalsIgnoreCase("N") || fileIni.getValue("db", "startdbcheck").equalsIgnoreCase("false")) {
                                startDbCheck = false;
                            } else {
                                startDbCheck = true;
                                startConDbCheck = true;
                            }

                            Db.useNamedPipes = false;
                            if (startDbCheck) {
                                Db.dbServ = "127.0.0.1:" + dbPortaOk;
                                if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
                                    Db.useNamedPipes = true;
                                }
                            } else {
                                Db.dbServ = fileIni.getValue("db", "server");
                            }

                            Db.dbNameDB = fileIni.getValue("db", "nome_database");
                            Db.dbName = fileIni.getValue("db", "user");
                            //richiedo password
                            if (fileIni.getValueBoolean("pref", "richiediPassword", false)) {
                                String pwd = SwingUtils.showInputPassword(splash, "Invoicex, password di accesso");
                                if (pwd == null) {
                                    System.exit(0);
                                }
                                Db.dbPass = pwd;
                            } else {
                                Db.dbPass = fileIni.getValueCifrato("db", "pwd");
                            }

                            if (!connessione.dbConnect() == true) {
                                exitMain();
                                return;
                            }
                        }
                    } else {
                        exitMain();
                        return;
                    }
                }
            }

            //riparazione tabelle
            try {
                String tempCheck = fileIni.getValue("db", "checkdb");
                if (tempCheck.equalsIgnoreCase("si")) {
                    connessione.dbControllo(false, splash);
                } else {
                    System.out.println("NO CHECK ON START");
                }
            } catch (Exception err) {
                err.printStackTrace();
            }
        } else {
            Db connessione = new Db();
            if (connessione.dbConnect() == false) {
                javax.swing.JOptionPane.showMessageDialog(null, "debug: conn db error:" + Db.dbServ + ":" + Db.dbNameDB + ":" + Db.dbName);
            } else {
                //javax.swing.JOptionPane.showMessageDialog(null, "debug: conn db OK:" + Db.dbServ + ":" + Db.dbNameDB + ":" + Db.dbName);
            }
        }

        //apro connessioni hibernate
        //        Configuration cfg = new Configuration()
        //        .setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver")
        //        .setProperty("hibernate.connection.url", "jdbc:mysql://linux/newoffice")
        //        .setProperty("hibernate.connection.username", "root")
        //        .setProperty("hibernate.connection.password", "***")
        //        .setProperty("hibernate.connection.pool_size", "3")
        //        .setProperty("hibernate.show_sql", "true")
        //        .setProperty("hibernate.format_sql", "true");


        //controllo windows >= vista e installazione in programmi
        if (PlatformUtils.isWindows()) {
            try {
                Integer majv = Integer.parseInt(System.getProperty("os.version").substring(0, 1));
                Integer minv = Integer.parseInt(System.getProperty("os.version").substring(2));
                System.out.println("majv: " + majv);
                System.out.println("minv: " + minv);
                if (majv >= 6) {
                    String programfiles = System.getenv("ProgramFiles").toLowerCase();
                    System.out.println("programfiles = " + programfiles);
                    String apppath = new File("").getAbsolutePath().toLowerCase();
                    System.out.println("apppath = " + apppath);
                    if (apppath.startsWith(programfiles)) {
                        String msg = "<html><font size=\"4\"><b>Invoicex</b> risulta installato in<br>";
                        msg += "<b>\"" + (new File("").getAbsolutePath()) + "\"</b><br>";
                        msg += "Consigliamo caldamente di reinstallare Invoicex nel percorso suggerito <b>\"C:\\Users\\Public\"</b><br>";
                        msg += "per evitare problemi dovuti alla virtualizzazione del file system introdotte da Windows Vista<br></font></html>";
                        SwingUtils.showErrorMessage(main.getPadreWindow(), msg, true);
                    }
                }
            } catch (Exception e) {
            }
        }
//            String msg = "Le seguenti tabelle sono danneggiate:\n\n";
//            for (Object t : tablesWithError) {
//                msg += String.valueOf(t) + "\n";
//            }
//            msg += "\nPotrebbero verificarsi problemi nell'uso del programma finchè non vengono riparate.";
//            SwingUtils.showWarningMessage(main.getPadreWindow(), msg);

        //modifiche db
        //aggiungo eventuali aggiornamenti
        //aggiornamento 5
        splash("aggiornamenti struttura database ...");

        //controllo integrità dei dati
        splash("aggiornamenti struttura database ... check");
        if (!db_in_rete) {  //lo facci osolo se su postazione singola
            controllaDati();
        }

        DbChanges dbchanges = new DbChanges();
        dbchanges.splash = splash;
        dbchanges.fileIni = fileIni;
        dbchanges.esegui_aggiornamenti();

        DbChanges2 dbchanges2 = new DbChanges2() {
            @Override
            public void post_execute_ok(int id_log, String id_plugin, String id_email, String sql) {
                super.post_execute_ok(id_log, id_plugin, id_email, sql);
                if (id_log == 143 && id_plugin.equals("") && id_email.equals("m.ceccarelli@tnx.it")) {
                    try {
                        if (!CastUtils.toString(DbUtils.getObject(Db.getConn(), "select logo_in_db from dati_azienda", false)).equalsIgnoreCase("S")) {
                            System.out.println("*** salvo logo in db ***");
                            splash("aggiornamenti struttura database ... salvataggio logo in db", 70);
                            InvoicexUtil.salvaImgInDb(main.fileIni.getValue("varie", "percorso_logo_stampe"));
                            InvoicexUtil.salvaImgInDb(main.fileIni.getValue("varie", "percorso_logo_stampe_pdf"), "logo_email");
                            InvoicexUtil.salvaImgInDb(main.fileIni.getValue("varie", "percorso_sfondo_stampe"), "sfondo");
                            InvoicexUtil.salvaImgInDb(main.fileIni.getValue("varie", "percorso_sfondo_stampe_pdf"), "sfondo_email");
                            DbUtils.tryExecQuery(Db.getConn(), "update dati_azienda set logo_in_db = 'S'");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void post_check_ok(int id_log, String id_plugin, String id_email) {
                super.post_check_ok(id_log, id_plugin, id_email);
                if (id_log == 141 && id_plugin.equals("") && id_email.equals("m.ceccarelli@tnx.it")) {
                    try {
                        if (!CastUtils.toString(DbUtils.getObject(Db.getConn(), "select logo_in_db from dati_azienda", false)).equalsIgnoreCase("S")) {
                            DbUtils.tryExecQuery(Db.getConn(), "update dati_azienda set logo_in_db = 'S'");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        dbchanges2.esegui_aggiornamenti();

        //controllo se avevano scelto tipo numerazione 2 metto a 3
        try {
            int confermata2 = CastUtils.toInteger0(DbUtils.getObject(Db.getConn(), "select tipo_numerazione_confermata2 from dati_azienda"));
            if (confermata2 == 0) {
                DbUtils.tryExecQuery(Db.getConn(), "update dati_azienda set tipo_numerazione_confermata2 = 1");
                int tiponum = CastUtils.toInteger0(DbUtils.getObject(Db.getConn(), "select tipo_numerazione from dati_azienda"));
                if (tiponum == 2) {
                    DbUtils.tryExecQuery(Db.getConn(), "update dati_azienda set tipo_numerazione = 3");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //tolgo storico più vecchio di 6 mesi
        try {
            DbUtils.tryExecQuery(Db.getConn(), "delete from storico where data < DATE_ADD(CURDATE(), INTERVAL -12 MONTH)");
        } catch (Exception e) {
            e.printStackTrace();
        }

//        try {
//            if (InvoicexUtil.getIvaDefault().equals("20") || InvoicexUtil.getIvaSpese().equals("20")) {
//                if (DbUtils.containRows(Db.getConn(), "select codice from articoli where iva = '21'")) {
//                    DbUtils.tryExecQuery(Db.getConn(), "update dati_azienda set codiceIvaDefault = '21'");
//                    DbUtils.tryExecQuery(Db.getConn(), "update dati_azienda set codiceIvaSpese = '21'");
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        try {
            if (!main.fileIni.getValueBoolean("varie", "correzione_stampa_fattura_nosconti", false)) {
                File dir = new File(it.tnx.shell.CurrentDir.getCurrentDir() + "/reports/fatture");
                File[] lista = dir.listFiles();
                for (File f : lista) {
                    if (f.getName().endsWith("_nosconto_gen_invoicex.jasper")) {
                        f.delete();
                    }
                }
                main.fileIni.setValue("varie", "correzione_stampa_fattura_nosconti", true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        splash("aggiornamenti struttura database ... ok", 75);

        //thread per keep alive connessione
        Thread tmysqlkeep = new Thread("thread-mysql-keep-alive") {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000 * 60 * 3);
                    System.out.println("mysql keep alive " + DbUtils.getObject(Db.getConn(), "select NOW()"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        tmysqlkeep.start();

        //if(fileIni.getValueBoolean("gestione_utenti", "attiva", false)){
        int utenti = 0;
        try {
            utenti = CastUtils.toInteger0(DbUtils.getObject(Db.getConn(true), "select gestione_utenti from dati_azienda limit 1"));
        } catch (Exception e) {
            SwingUtils.showErrorMessage(main.getPadreFrame(), e.getMessage(), true);
        }
        if (utenti == 1) {
            splash("Login...", 0);
            int tentativi = 0;
            boolean canLogin = false;
            boolean annullaLogin = false;
            while (tentativi < 3 && !canLogin && !annullaLogin) {
                final JDialogAccesso dialog = new JDialogAccesso(splash, true, tentativi);
                dialog.setLocationRelativeTo(null);
                System.out.println("dialog set visible true");
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        dialog.setVisible(true);
                    }
                });
                //dialog.setVisible(true);
                System.out.println("uscito da jdialogaccesso");
                canLogin = dialog.canLogin();
                annullaLogin = dialog.annullaLogin();
                dialog.dispose();
                tentativi++;
            }

            if (annullaLogin) {
//                SwingUtils.showErrorMessage(splash, "Operazione di login annullata dall'utente", "Impossibile effettuare il login", true);
                main.exitMain();
                return;
            } else {
                if (!canLogin) {
                    SwingUtils.showErrorMessage(splash, "Hai esaurito i tentativi disponibili, accesso negato", "Impossibile effettuare il login", true);
                    main.exitMain();
                    return;
                }
            }
            System.out.println("#############################  UTENTE  ############################## " + System.getProperty("line.separator") + main.utente);
            splash("Login effettuato: " + main.utente.getNomeUtente(), 75);
        } else {
            this.utente = new Utente(1);
        }

        //personalizzazioni via groovy scripts
        if (!Main.applet) {
            try {
                String[] roots = new String[]{"./personal_groovy/"};
                GroovyScriptEngine gse = new GroovyScriptEngine(roots);
                Binding binding = new Binding();
                binding.setVariable("input_param_prop", paramProp);
                File dirpersonal = new File(main.wd + "personal_groovy/");
                if (dirpersonal.exists()) {
                    for (File fgroovy : dirpersonal.listFiles()) {
                        if (fgroovy.getAbsolutePath().endsWith(".groovy")) {
                            System.out.println("INIZIO PERSONAL " + fgroovy + " :");
                            try {
                                gse.run(fgroovy.getName(), binding);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            System.out.println(binding.getVariable("output"));
                            try {
                                File fgroovyexecuted = new File(fgroovy.getAbsolutePath() + ".executed");
                                try {
                                    fgroovyexecuted.delete();
                                } catch (Exception e) {
                                }
                                System.out.println("renameto:" + fgroovyexecuted + " esito:" + fgroovy.renameTo(fgroovyexecuted));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            System.out.println("FINE PERSONAL " + fgroovy);
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        MenuFrame menu1 = null;
        if (!Main.applet) {
            splash("apertura menu");
            menu1 = new MenuFrame();
            splash("apertura menu.");
            padre_frame = (MenuFrame) menu1;
            padre_panel = menu1.getMenuPanel();
            padre_panel.postInit();
            splash("apertura menu..");
            menu1.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        } else {
//            SwingUtils.showInfoMessage(null, "Avvio fuori dal browser");

            splash("apertura menu applet");
            menu1 = new MenuFrame();
            splash("apertura menu..");
            padre_frame = (MenuFrame) menu1;
            padre_panel = menu1.getMenuPanel();
            padre_panel.postInit();
            splash("apertura menu..");
            menu1.setVisible(true);
            menu1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

            /*
             //devo trovare l'applet
             Class clazz;
             try {
             clazz = null;
             try {
             System.out.println("!!! cl 0 = " + Thread.currentThread().getContextClassLoader());
             ClassLoader cl = Thread.currentThread().getContextClassLoader();
             cl = cl.getParent();
             System.out.println("!!! cl 0.1 = " + cl);
             clazz = Class.forName("testappletinvoicex.JAppletInvoicex", false, cl);
             System.out.println("!!! clazz 2 = " + clazz);
             } catch (Exception e) {
             e.printStackTrace();
             }

             Field field = clazz.getDeclaredField("INSTANCE");
             Object appletinst = field.get(null);
             System.out.println("!!! appletinst = " + appletinst);

             System.out.println("--- !!! frames !!! ---");
             for (Frame f : Frame.getFrames()) {
             System.out.println(" f:" + f);
             Component[] comps = f.getComponents();
             for (Component comp : comps) {
             System.out.println("    comp:" + comp.getClass());
             }
             }
             System.out.println("----------------------");

             Frame frameowner = Frame.getFrames()[0];
             Component[] comps = frameowner.getComponents();
             for (Component comp : comps) {
             System.out.println("    comp:" + comp.getClass());
             }

             Main.appletinst = appletinst;
             if (appletinst instanceof JApplet) {
             try {
             ((JApplet) appletinst).getContentPane().removeAll();
             ((JApplet) appletinst).setLayout(new BorderLayout());
             MenuPanel menuPanel = new MenuPanel();
             ((JApplet) appletinst).getContentPane().add(menuPanel);
             ((JApplet) appletinst).setJMenuBar(menuPanel.menBar);
             padre_panel = menuPanel;
             padre_panel.postInit();
             ((JApplet) appletinst).validate();
             } catch (Exception ex) {
             ex.printStackTrace();
             //per sviluppo netbeans
             menu1 = new MenuFrame();
             padre_frame = (MenuFrame) menu1;
             padre_panel = menu1.getMenuPanel();
             padre_panel.postInit();

             menu1.setVisible(true);
             menu1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
             }
             } else if (appletinst instanceof JApplet) {
             try {
             AppletViewer a = ((AppletViewer) appletinst);
             a.removeAll();
             a.setLayout(new BorderLayout());
             MenuPanel menuPanel = new MenuPanel();
             a.add(menuPanel);
             //                        a.setJMenuBar(menuPanel.menBar);
             padre_panel = menuPanel;
             padre_panel.postInit();
             a.validate();
             } catch (Exception ex) {
             ex.printStackTrace();
             //per sviluppo netbeans
             menu1 = new MenuFrame();
             padre_frame = (MenuFrame) menu1;
             padre_panel = menu1.getMenuPanel();
             padre_panel.postInit();

             menu1.setVisible(true);
             menu1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
             }
             }
             System.out.println("-------------------------------");
             } catch (Exception ex) {
             System.out.println("!!! clazz ex");
             ex.printStackTrace();
             }
             */

        }

        padre = new Menu();
        splash("apertura menu...", 85);

        if (!fileIni.existKey("pref", "numerazioneNoteCredito")) {
            //controllo se già presente una nota di credito lascio a false altrimenti metto a true
            String sql = "select * from test_fatt where tipo_fattura = " + dbFattura.TIPO_FATTURA_NOTA_DI_CREDITO + " limit 1";
            ResultSet r = Db.openResultSet(sql);
            try {
                if (r.next()) {
                    fileIni.setValue("pref", "numerazioneNoteCredito", false);
                } else {
                    fileIni.setValue("pref", "numerazioneNoteCredito", true);
                }
            } catch (Exception ex) {
                fileIni.setValue("pref", "numerazioneNoteCredito", true);
                ex.printStackTrace();
            }
        }

//        if (!fileIni.existKey("varie", "percorso_logo_stampe_pdf")) {
//            fileIni.setValue("varie", "percorso_logo_stampe_pdf", fileIni.getValue("varie", "percorso_logo_stampe"));
//            iniPercorsoLogoStampePdf = fileIni.getValue("varie", "percorso_logo_stampe");
//            fileIni.saveFile();
//        }

        splash("controllo tabelle temporanee", 90);

        //---- fine modifiche -----
        //creo righ_ddt_temp se e' cambiata la struttura
        if (Db.checkTableStructure("righ_ddt", "righ_ddt_temp", true) == false) {
            Db.executeSql("DROP TABLE IF EXISTS righ_ddt_temp");
            Db.duplicateTableStructure("righ_ddt", "righ_ddt_temp", true);
        }
        if (Db.checkTableStructure("righ_ddt_acquisto", "righ_ddt_acquisto_temp", true) == false) {
            Db.executeSql("DROP TABLE IF EXISTS righ_ddt_acquisto_temp");
            Db.duplicateTableStructure("righ_ddt_acquisto", "righ_ddt_acquisto_temp", true);
        }

        //creo righ_fatt_temp
        if (Db.checkTableStructure("righ_fatt", "righ_fatt_temp", true) == false) {
            Db.executeSql("DROP TABLE IF EXISTS righ_fatt_temp");
            Db.duplicateTableStructure("righ_fatt", "righ_fatt_temp", true);
        }

        //creo righ_fatt_temp
        if (Db.checkTableStructure("righ_fatt_acquisto", "righ_fatt_acquisto_temp", true) == false) {
            Db.executeSql("DROP TABLE IF EXISTS righ_fatt_acquisto_temp");
            Db.duplicateTableStructure("righ_fatt_acquisto", "righ_fatt_acquisto_temp", true);
        }

        //creo righ_ordi_temp
        if (Db.checkTableStructure("righ_ordi", "righ_ordi_temp", true) == false) {
            Db.executeSql("DROP TABLE IF EXISTS righ_ordi_temp");
            Db.duplicateTableStructure("righ_ordi", "righ_ordi_temp", true);
        }

        //creo righ_ordi_temp
        if (Db.checkTableStructure("righ_ordi_acquisto", "righ_ordi_acquisto_temp", true) == false) {
            Db.executeSql("DROP TABLE IF EXISTS righ_ordi_acquisto_temp");
            Db.duplicateTableStructure("righ_ordi_acquisto", "righ_ordi_acquisto_temp", true);
        }

        //annoto versioni clients
        //agg(154, "", "m.ceccarelli@tnx.it", "CREATE TABLE versioni_clients (hostname VARCHAR(250) NULL,versione VARCHAR(20) NULL,pacchetto VARCHAR(100) NULL)", "tabella per versioni clients di invoicex");
        try {
            String host = SystemUtils.getHostname();
            String sql = "delete from versioni_clients where hostname = " + Db.pcs(host);
            DbUtils.tryExecQuery(Db.getConn(), sql);
            sql = "insert into versioni_clients set hostname = " + Db.pcs(host) + ", versione = " + Db.pcs(main.version.toString() + " " + main.build) + ", pacchetto = " + Db.pcs(main.versione);
            System.out.println("sql = " + sql);
            DbUtils.tryExecQuery(Db.getConn(), sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

        splash("ok", 100);

        if (!Main.applet) {
            menu1.setVisible(true);
            padre_panel.checkPanBarr2();
            menu1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            padre_panel.lblInfoLoading2.setText("caricamento ...");
        }

        if (splash != null) {
            splash.dispose();
        }
        splash = null;

        if (!Main.applet) {
            //controllo se ha inserito i dati azienda
            String sql = "select ragione_sociale, flag_dati_inseriti from dati_azienda";
            ResultSet r = Db.openResultSet(sql);
            try {
                if (r.next()) {
                    if (r.getObject("flag_dati_inseriti") == null || !r.getString("flag_dati_inseriti").equalsIgnoreCase("S")) {
                        JDialogDatiAzienda datiAzienda = new JDialogDatiAzienda(main.getPadre(), true);
                        datiAzienda.setLocationRelativeTo(null);
                        datiAzienda.setVisible(true);
                    } else {
                        attivazione.setDatiAziendaInseriti(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(menu1, "Errore nel controllo della tabella dati_azienda, nessun record", "Errore", JOptionPane.ERROR_MESSAGE);
                    DbUtils.tryExecQuery(Db.getConn(), "insert into dati_azienda set id = 1, ragione_sociale = ''");
                }
            } catch (SQLException sqlerr1) {
                JOptionPane.showMessageDialog(menu1, "Errore nel controllo della tabella dati_azienda, " + sqlerr1, "Errore", JOptionPane.ERROR_MESSAGE);
            }
        }

        //imposto nuovo report ddt mod3 se mod2 o default
        if (!Main.applet) {
            if (fileIni.getValue("pref", "tipoStampaDDT", "ddt_default.jrxml").equals("ddt_default.jrxml") || fileIni.getValue("pref", "tipoStampaDDT", "ddt_default.jrxml").equals("ddt_mod2_default.jrxml")) {
                fileIni.setValue("pref", "tipoStampaDDT", "ddt_mod3_default.jrxml");
            }
            if (new File(main.wd + "reports/fatture/fattura_mod3_default.jrxml").exists() && (fileIni.getValue("pref", "tipoStampa", "fattura_default.jrxml").equals("fattura_default.jrxml") || fileIni.getValue("pref", "tipoStampa", "fattura_mod2_default.jrxml").equals("fattura_mod2_default.jrxml"))) {
                System.out.println("file exist mod3: " + new File(main.wd + "reports/fatture/fattura_mod3_default.jrxml").exists() + " && (" + fileIni.getValue("pref", "tipoStampa", "fattura_default.jrxml").equals("fattura_default.jrxml") + " || " + fileIni.getValue("pref", "tipoStampa", "fattura_mod2_default.jrxml").equals("fattura_mod2_default.jrxml") + ")");
                fileIni.setValue("pref", "tipoStampa", "fattura_mod3_default.jrxml");
            }

            //imposto nuovo report ordine mod2 se default
            if (!fileIni.existKey("pref", "flag_impostato_nuovo_report_ordine_mod2")) {
                fileIni.setValue("pref", "flag_impostato_nuovo_report_ordine_mod2", "N");
                if (fileIni.getValue("pref", "tipoStampaOrdine", "ddt_default.jrxml").equals("ordine_default.jrxml")) {
                    if (new File(main.wd + "reports/fatture/ordine_mod2_default.jrxml").exists()) {
                        fileIni.setValue("pref", "tipoStampaOrdine", "ordine_mod2_default.jrxml");
                        fileIni.setValue("pref", "flag_impostato_nuovo_report_ordine_mod2", "S");
                    }
                }
            }

            //imposto fattura_mod4 se non già impostato ed era mod3
            if (!fileIni.existKey("pref", "flag_fattura_mod4")
                    && fileIni.getValue("pref", "tipoStampa", "fattura_mod3_default.jrxml").equals("fattura_mod3_default.jrxml")) {
                fileIni.setValue("pref", "tipoStampa", "fattura_mod4_default.jrxml");
                fileIni.setValue("pref", "flag_fattura_mod4", "s");
            }
            if (!fileIni.existKey("pref", "flag_fattura_acc_mod4")
                    && fileIni.getValue("pref", "tipoStampaFA", "fattura_acc_mod2_default.jrxml").equals("fattura_acc_mod2_default.jrxml")) {
                fileIni.setValue("pref", "tipoStampaFA", "fattura_acc_mod4_default.jrxml");
                fileIni.setValue("pref", "flag_fattura_acc_mod4", "s");
            }
            if (!fileIni.existKey("pref", "flag_ddt_mod4")
                    && fileIni.getValue("pref", "tipoStampaDDT", "ddt_mod3_default.jrxml").equals("ddt_mod3_default.jrxml")) {
                fileIni.setValue("pref", "tipoStampaDDT", "ddt_mod4_default.jrxml");
                fileIni.setValue("pref", "flag_ddt_mod4", "s");
            }

            if (!fileIni.existKey("pref", "flag_ordine_mod4")
                    && fileIni.getValue("pref", "tipoStampaOrdine", "ordine_mod2_default.jrxml").equals("ordine_mod2_default.jrxml")) {
                fileIni.setValue("pref", "tipoStampaOrdine", "ordine_mod4_default.jrxml");
                fileIni.setValue("pref", "flag_ordine_mod4", "s");
            }

            //imposto _mod5 se non già impostato ed era mod4
            if (!fileIni.existKey("pref", "flag_ddt_mod5")
                    && fileIni.getValue("pref", "tipoStampaDDT", "ddt_mod4_default.jrxml").equals("ddt_mod4_default.jrxml")) {
                fileIni.setValue("pref", "tipoStampaDDT", "ddt_mod5_default.jrxml");
                fileIni.setValue("pref", "flag_ddt_mod5", "s");
            }

            //imposto _mod5 se non già impostato ed era mod4
            if (!fileIni.existKey("pref", "flag_ddt_mod5bis")
                    && fileIni.getValue("pref", "tipoStampa", "fattura_mod4_default.jrxml").equals("fattura_mod4_default.jrxml")) {
                fileIni.setValue("pref", "tipoStampa", "fattura_mod5_default.jrxml");
                fileIni.setValue("pref", "tipoStampaFA", "fattura_acc_mod5_default.jrxml");
                fileIni.setValue("pref", "tipoStampaOrdine", "ordine_mod5_default.jrxml");
                fileIni.setValue("pref", "tipoStampaDDT", "ddt_mod5_default.jrxml");
                fileIni.setValue("pref", "flag_ddt_mod5bis", "s");
            }
        }

        if (!fileIni.existKey("pref", "flag_stato_ordine_bug1")) {
            if (fileIni.getValue("pref", "stato_ordi", "").equals("Preventivo")) {
                main.fileIni.removeKey("pref", "stato_ordi");
            }
            fileIni.setValue("pref", "flag_stato_ordine_bug1", "s");
        }

        //imposto di caricare le immagini da file e non da db (da db solo per integrazione client manager)
        if (!fileIni.existKey("pref", "flag_immagini_da_db_per_client_manager")) {
            fileIni.setValue("pref", "flag_immagini_da_db_per_client_manager", "N");
        }

        if (!fileIni.existKey("varie", "int_dest_1")) {
            fileIni.setValue("varie", "int_dest_1", "<html><b><font size='3'>$F{ragione_sociale1}</font></b><br><font size='2'>$F{indirizzo1}<br>$F{cap_loc_prov1}</font><br><font size='1'>$F{piva_cfiscale_desc1}$F{recapito1}</font></html>");
        }
        if (!fileIni.existKey("varie", "int_dest_2")) {
            //<html><b><font size='3'>$F{ragione_sociale2}</font></b><br><font size='2'>$F{indirizzo2}<br>$F{cap_loc_prov2}</font><br><font size='1'>$F{recapito_2_sotto}$F{piva_cfiscale_desc_2_sotto}$F{<br>Email|email}</font></html>
            fileIni.setValue("varie", "int_dest_2", "<html><b><font size='3'>$F{ragione_sociale2}</font></b><br><font size='2'>$F{indirizzo2}<br>$F{cap_loc_prov2}</font><br><font size='1'>$F{recapito_2_sotto}$F{piva_cfiscale_desc_2_sotto}</font></html>");
        }

        //nuovo controllo registrazione
        if ((!attivazione.isFlagDatiInviatiPrimaVolta() && attivazione.isDatiAziendaInseriti()) || (!attivazione.isFlagDatiInviatiSuModifica() && attivazione.isFlagDatiModificati())) {
            //chiedo se vuol inviare i dati a tnx
//            if (JOptionPane.showConfirmDialog(getPadreWindow(), "Acconsenti all'invio della tua Ragione Sociale, Partita IVA ed il resto dei tuoi dati anagrafici aziendali a TNX snc ?\nI dati verranno utilizzati soltanto a scopo statistico o per attivare funzionalità aggiuntive,\nse non vengono inviati puoi comunque continuare ad usare il programma", "Registrazione programma", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_NO_OPTION) {
            Boolean registraini = main.fileIni.getValueBoolean("varie", "dati_azienda_registra_invoicex", true);
            if (registraini) {
                System.out.println("attivazione: invio dati...");
                boolean ret = attivazione.registra();
                if (ret) {
                    main.attivazione.setDatiAziendaInseriti(true);
                    main.attivazione.setFlagDatiInviatiPrimaVolta(true);
                    if (attivazione.isFlagDatiModificati()) {
                        main.attivazione.setFlagDatiModificati(false);
                        main.attivazione.setFlagDatiInviatiSuModifica(true);
                    }
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
//                            JOptionPane.showMessageDialog(main.this.getPadre(), "Invio completato.\nGrazie", "Informazioni", JOptionPane.INFORMATION_MESSAGE);
                            main.getPadre().aggiornaTitle();
                        }
                    });
                }
                System.out.println("attivazione: registrazione " + ret);
                System.out.println("attvazione: invio dati...finito");
            }
        }

        main.getPadrePanel().lblInfoLoading2.setVisible(true);
        main.getPadrePanel().lblInfoLoading2.setText("Controllo personalizzazioni ...");
        Thread tpersonal = new Thread("tpersonal") {
            @Override
            public void run() {
                System.out.println("tpersonal: Controllo personalizzazioni ...");
                try {
                    String vl = version + " (" + build + ")";
                    String url = "http://www.tnx.it/pagine/invoicex_server/p.php?v=" + URLEncoder.encode(vl) + (attivazione.getIdRegistrazione() != null ? "&i=" + attivazione.getIdRegistrazione() : "");
                    String lista = getURL(url);
                    String[] files = StringUtils.split(lista, "\n");
                    DebugUtils.dump(files);
                    if (files != null) {
                        for (int i = 0; i < files.length; i++) {
                            String urlfile = "http://www.tnx.it/pagine/invoicex_server/personal/" + files[i];
                            String filelocale = files[i];
                            File filelocale_test = new File(main.wd + filelocale);
                            long last_modified_locale = filelocale_test.lastModified();
                            long last_modified_server = 0;
                            try {
                                last_modified_server = HttpUtils.getLastModified(urlfile);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            System.out.println("tpersonal: download di:" + files[i] + " da:" + urlfile + " in:" + filelocale + " last_modified_locale:" + last_modified_locale + " last_modified_server:" + last_modified_server + " test s>l:" + (last_modified_server > last_modified_locale));
                            if (filelocale_test.exists() && last_modified_server > last_modified_locale) {
                                System.out.println("tpersonal: tento di eliminare il file che esiste gia':" + filelocale);
                                filelocale_test.delete();
                                System.out.println("tpersonal: tento di eliminare il file che esiste gia':" + filelocale + " ... eliminato!");
                            }
                            if (last_modified_server > last_modified_locale) {
                                try {
                                    HttpUtils.saveFile(urlfile, filelocale);
                                    //controllo se scarica un report glielo setto
                                    if (files[i].toLowerCase().startsWith("reports/fatture/")) {
                                        String nome_report = filelocale_test.getName();
                                        String tipo = "";
                                        if (nome_report.startsWith("fattura")) {
                                            if (!nome_report.startsWith("fattura_acc") && !nome_report.startsWith("fattura_acquisto")) {
                                                tipo = "tipoStampa";
                                            }
                                        } else if (nome_report.startsWith("fattura_acc")) {
                                            tipo = "tipoStampaFA";
                                        } else if (nome_report.startsWith("ddt")) {
                                            tipo = "tipoStampaDDT";
                                        } else if (nome_report.startsWith("ordine")) {
                                            tipo = "tipoStampaOrdine";
                                        }
                                        System.out.println("tpersonal: tento di impostre come report per:" + tipo + " il report:" + nome_report);
                                        fileIni.setValue("pref", tipo, nome_report);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        tpersonal.start();

        //init plugins - check aggiornamenti
//        menu1.lblInfoLoading2.setBounds( 5, menu1.getHeight() - 150, menu1.lblInfoLoading2.getWidth(), menu1.lblInfoLoading2.getHeight());
        main.getPadrePanel().lblInfoLoading2.setVisible(true);
        main.getPadrePanel().lblInfoLoading2.setText("Controllo aggiornamenti Plugins ...");

        //controllo se rimuovere dei plugin perchè rochiesti dall'avvio precedente
        try {
            File fdirplugins = new File(main.wd + plugins_path);
            File[] files = fdirplugins.listFiles();
            for (File fplugin : files) {
                String key = "remove_" + fplugin.getName().toLowerCase();
                System.out.println("key = " + key);
                if (fileIni.existKey("plugins", key)) {
                    System.out.println("plugins: delete:" + fplugin + " esito:" + fplugin.delete());
                    fileIni.removeKey("plugins", key);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            File fdirplugins = new File(main.wd + plugins_path);
            fdirplugins.mkdir();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (attivazione.getIdRegistrazione() != null) {
            pf = new PluginFactory();
            pf.loadPlugins(plugins_path);
            Collection plugcol = pf.getAllEntryDescriptor();
            if (plugcol != null) {
                Iterator plugiter = plugcol.iterator();
                //controllo aggiornamento dei plugins..
                List<Plugin> plugins_online = null;
                try {
                    String url = it.tnx.invoicex.Main.url_server;
                    HessianProxyFactory factory = new HessianProxyFactory();
                    Hservices service = (Hservices) factory.create(Hservices.class, url);
                    //plugins_online = service.getPlugins2(false);
                    plugins_online = service.getPlugins3(false, true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (plugins_online != null) {
                    while (plugiter.hasNext()) {
                        EntryDescriptor pd = (EntryDescriptor) plugiter.next();

                        //                    pluginPresenti.add(pd.getName());
                        //                    plugins.put(pd.getName(), pd);

                        //controllo aggiornamenti
                        try {
                            for (Plugin po : plugins_online) {
                                if (po.getNome_breve().equalsIgnoreCase(pd.getName())) {
                                    System.out.println("controllo aggiornamenti per:" + pd.getName() + " o:" + po.getNome_breve() + " jar:" + pd.getNomeFileJar());

//                                    test
//                                    scaricaAggiornamentoPlugin(po, pd);

                                    if (pd.getData() == null && !po.getVersione().equals("1.0")) {
                                        //aggiornarecontrollo aggiornamenti per:
                                        System.out.println("richiedo agg: " + pd.getData() + " ver:" + po.getVersione());
                                        scaricaAggiornamentoPlugin(po, pd);
                                    } else if (pd.getData() != null && pd.getData().before(po.getData_ultima_modifica())) {
                                        //aggiornare
                                        System.out.println("richiedo agg: " + pd.getData() + " " + po.getData_ultima_modifica() + " pd.getData() before po.getData():" + pd.getData().before(po.getData_ultima_modifica()));
                                        scaricaAggiornamentoPlugin(po, pd);
                                    } else if (pd.getData() != null && pd.getData().equals(po.getData_ultima_modifica()) && CastUtils.toDouble0Eng(pd.getVer()) < CastUtils.toDouble0Eng(po.getVersione())) {
                                        //aggiornare
                                        System.out.println("richiedo agg: " + pd.getData() + " " + po.getData_ultima_modifica() + " pd.getData().equals(po.getData_ultima_modifica()):" + pd.getData().equals(po.getData_ultima_modifica()) + " pd ver:" + pd.getVer() + " po ver:" + po.getVersione());
                                        scaricaAggiornamentoPlugin(po, pd);
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }


        Thread tscaduto = new Thread("scaduto") {
            public void run() {
                try {
                    Thread.sleep(1000L);
                } catch (Exception e) {
                }
                System.out.println("controllo plugins: in attesa del caricamento dei plugins");
                long max = System.currentTimeMillis() + (1000 * 30);
                while (main.fine_init_plugin == false) {
                    try {
                        Thread.sleep(5000);
                        if (System.currentTimeMillis() > max) {
                            System.out.println("controllo plugins: timeout controllaPluginPerVersione");
                            return;
                        }
                        System.out.println("controllo plugins: in attesa del caricamento dei plugins...");
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                System.out.println("controllo plugins: caricamento de plugin completato, attendo 5 secondi");
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {
                }

                //controllo plugins disattivati
                if (fileIni.getValueBoolean("pref", "msg_plugins_riattiva2", true)) {
                    String[] pluginscheck = {"pluginRicerca", "pluginAutoUpdate", "pluginBackupTnx", "pluginRitenute", "pluginEmail", "pluginBarCode", "pluginRiba", "pluginCompry", "pluginContabilita"};
                    ArrayList pluginsdisat = new ArrayList();
                    String pluginsdisatm = "";
                    for (String s : pluginscheck) {
                        if (pluginPresenti.contains(s) && !pluginAttivi.contains(s)) {
                            pluginsdisat.add(s);
                            pluginsdisatm += s + "/";
                        }
                    }

////**************************
////test
//String s = "pluginRitenute";
//pluginsdisat.add(s);
//pluginsdisatm += s + "/";
////**************************

                    if (pluginsdisat.size() > 0) {
                        pluginsdisatm = StringUtils.chop(pluginsdisatm);
                        final String pluginsdisatm_f = pluginsdisatm;

                        String msg = "<html>";
                        String versioneu = main.fileIni.getValue("cache", "versioneu", "Base");
////**************************
////test
////versioneu = "Base";
//versioneu = "Professional Plus";
////**************************

                        boolean motivo_dati_non_corrispondenti = false;
                        boolean motivo_non_attivato = false;
                        boolean motivo_disattivato = false;

                        System.out.println("debug check attivazione");
                        System.out.println("versioneu:[" + versioneu + "]");
                        System.out.println("pluginsdisatm_f:[" + pluginsdisatm_f + "]");
                        System.out.println("pluginsdisatm_f.indexOf(\"pluginRitenute\"):[" + pluginsdisatm_f.indexOf("pluginRitenute") + "]");
                        System.out.println("pluginAttivi:[" + pluginAttivi + "]");
                        System.out.println("pluginAttivi.contains(\"pluginAutoUpdate\"):[" + pluginAttivi.contains("pluginAutoUpdate") + "]");

                        if (!versioneu.equalsIgnoreCase("Base")) {
                            for (String k : pluginErroriAttivazioni.keySet()) {
                                Map m = pluginErroriAttivazioni.get(k);
                                if (m.get("motivo_dati_non_corrispondenti") != null) {
                                    motivo_dati_non_corrispondenti = true;
                                }
                                if (m.get("motivo_non_attivato") != null) {
                                    motivo_non_attivato = true;
                                }
                                if (m.get("motivo_disattivato") != null) {
                                    motivo_disattivato = true;
                                    break;
                                }
                            }                            

                            //controllo se ha la professional ed ha il plugin autoupdate attivo e ritenuto disattivo
//                                    if (versioneu.equalsIgnoreCase("Professional") && 
//                                            (pluginsdisatm_f.indexOf("pluginRitenute") >= 0)
//                                            && pluginAttivi.contains("pluginAutoUpdate")) {

                            if (!motivo_disattivato && versioneu.equalsIgnoreCase("Professional") && (pluginsdisatm_f.indexOf("pluginRiba") >= 0
                                    || pluginsdisatm_f.indexOf("pluginContabilita") >= 0
                                    || pluginsdisatm_f.indexOf("pluginBarCode") >= 0
                                    || pluginsdisatm_f.indexOf("pluginRitenute") >= 0)) {
                                msg += "<font size='5' color='red'>Alcune funzioni disattivate:</font><br><br>";
                                if (pluginsdisatm_f.indexOf("pluginRiba") >= 0) {
                                    msg += " - Generazione file RiBa per banca (pluginRiba)<br>";
                                }
                                if (pluginsdisatm_f.indexOf("pluginContabilita") >= 0) {
                                    msg += " - Contabilità (pluginContabilita)<br>";
                                }
                                if (pluginsdisatm_f.indexOf("pluginBarCode") >= 0) {
                                    msg += " - Stampa dei codici a barre (pluginBarCode)<br>";
                                }
                                if (pluginsdisatm_f.indexOf("pluginRitenute") >= 0) {
                                    msg += " - Fatture con ritenute d'acconto e rivalsa (pluginRitenute)<br>";
                                }
                                msg += "<br>";
                                msg += "<br>La tua versione " + main.versione + " non comprende le funzionalità di cui sopra.";
                                msg += "<br>Se lo desideri puoi passare alla versione Professional Plus o Enterprise.";
                                msg += "<br>Oppure puoi cliccare su Plugins e fare doppio click sui plugins<br> non più attivi per toglierli definitivamente";
                            } else {
                                for (String k : pluginErroriAttivazioni.keySet()) {
                                    Map m = pluginErroriAttivazioni.get(k);
                                    if (m.get("motivo_dati_non_corrispondenti") != null) {
                                        motivo_dati_non_corrispondenti = true;
                                    }
                                    if (m.get("motivo_non_attivato") != null) {
                                        motivo_non_attivato = true;
                                    }
                                    if (m.get("motivo_disattivato") != null) {
                                        motivo_disattivato = true;
                                        break;
                                    }
                                }

////**************************
////test
//motivo_dati_non_corrispondenti = false;
//motivo_non_attivato = true;
//motivo_disattivato = true;
////**************************

                                msg += "<b>";
                                if (motivo_disattivato) {
                                    msg += "<font size='5' color='red'>Il tuo Invoicex " + main.versione + " è scaduto !</font><br><br>";
                                } else if (motivo_dati_non_corrispondenti) {
                                    msg += "<font size='3' color='red'>Il tuo Invoicex " + main.versione + " non è registrato correttamente !</font><br><br>";
                                } else if (motivo_non_attivato) {
                                    msg += "<font size='3' color='red'>Il tuo Invoicex " + main.versione + " non è stato attivato !</font><br><br>";
                                } else {
                                }
                                msg += "</b>";
                                msg += "Senza riattivare Invoicex:<br>";
                                if (pluginsdisatm_f.indexOf("pluginAutoUpdate") >= 0) {
                                    msg += " - non si aggiornerà in automatico<br>";
                                }
                                if (pluginsdisatm_f.indexOf("pluginBackupTnx") >= 0) {
                                    msg += " - non ti permetterà di eseguire i backup online sul nostro server<br>";
                                }
                                if (pluginsdisatm_f.indexOf("pluginRicerca") >= 0) {
                                    msg += " - non potrai utilizzare la ricerca globale che si apriva all'avvio<br>";
                                }
                                if (pluginsdisatm_f.indexOf("pluginRiba") >= 0) {
                                    msg += " - non potrai generare il file Riba da inviare tramite home banking<br>";
                                }
                                if (pluginsdisatm_f.indexOf("pluginRitenute") >= 0) {
                                    msg += " - non potrai gestire le ritenute e la rivalsa inps<br>";
                                }
                                msg += " - non sei più coperto dalla nostra Assistenza.";
                                if (motivo_disattivato) {
                                    msg += "<br><br><b>Motivo: il programma non è stato rinnovato alla sua scadenza.</b>";
                                } else if (motivo_dati_non_corrispondenti) {
                                    msg += "<br><br><b>Motivo: I dati inseriti in Anagrafiche -> Anagrafica Azienda non corrispondono con i dati di registrazione.</b>";
                                    msg += "<br>Vai in Anagrafiche -> Anagrafica Azienda, conferma i dati ed alla domanda 'Acconsenti all'invio...' rispondi<br> con 'Sì', attendi la registrazione e riavvia il programma";
                                } else if (motivo_non_attivato) {
                                    msg += "<br><br><b>Motivo: Il programma non è stato attivato.</b>";
                                    msg += "<br>Per l'attivazione clicca sul pulsante 'Attivazione' e segui le istruzioni";
                                }
                            }
                        } else {
                            msg += "<font size='4' color='red'><b>I plugins in prova sono scaduti !</b></font><br><br>";
                            msg += "Per riattivare tutte le funzionalità aggiuntive acquista Invoicex e procedi<br>";
                            msg += "all'attivazione con il codice che ti invieremo<br><br>";
                            msg += "Se hai già il codice clicca sul pulsante 'Attivazione' !<br>";
                        }

                        System.out.println("pluginErroriAttivazioni:" + pluginErroriAttivazioni);
                        
                        String dettagli = "";
                        for (String k : pluginErroriAttivazioni.keySet()) {
                            System.out.println("k = " + k);
                            Map m = pluginErroriAttivazioni.get(k);
                            if (motivo_disattivato) {
                                if (m.get("motivo_msg") != null && m.get("motivo_msg").toString().indexOf("DISATTIVATO") >= 0) {
                                    dettagli += m.get("motivo_msg") + "<br>";
                                    break;
                                }
                            } else if (motivo_dati_non_corrispondenti) {
                                dettagli += m.get("motivo_msg") + "<br>";
                                DebugFastUtils.hexDump(dettagli);
                                break;
                            } else {
                                if (m.get("motivo_msg") != null) {
                                    dettagli += k + ":" + m.get("motivo_msg") + "<br>";
                                }
                            }
                        }
                        final String dettaglif = dettagli;
                        final String msgf = msg;
                        SwingUtils.inEdt(new Runnable() {
                            public void run() {
                                padre.setIconImage(main.getLogoIcon());
                                final JDialogRiattivaPlugins rp = new JDialogRiattivaPlugins(padre, true);
                                String dettagli2 = dettaglif;
                                if (dettagli2.length() > 0) {
                                    if (dettagli2.length() > 200) {
                                        dettagli2 = dettagli2.substring(0, 200) + "...";
                                    }
                                    dettagli2 = StringUtils.replace(dettagli2, "\n", "<br>");
                                    rp.dettagli.setText("<html>Dettagli:<br>" + dettagli2 + "</html>");
                                }
                                String msg2 = msgf;
                                msg2 += "</html>";
                                rp.jLabel1.setText(msg2);

                                rp.validate();
                                rp.setLocationRelativeTo(null);
                                rp.pack();
                                rp.setVisible(true);
                            }
                        });
                    }
                }
            }
        };


        //init plugins
//        menu1.lblInfoLoading2.setBounds( 5, menu1.getHeight() - 150, menu1.lblInfoLoading2.getWidth(), menu1.lblInfoLoading2.getHeight());
        main.getPadrePanel().lblInfoLoading2.setVisible(true);
        main.getPadrePanel().lblInfoLoading2.setText("Caricamento Plugins ...");
        try {
            File fdirplugins = new File(main.wd + plugins_path);
            fdirplugins.mkdir();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //controllo il plugin generale
        pf = new PluginFactory();
        pf.loadPlugins(plugins_path);
        boolean pi = false;
        Collection plugcol = pf.getAllEntryDescriptor();
        if (plugcol != null) {
            Iterator plugiter = plugcol.iterator();
            while (plugiter.hasNext()) {
                EntryDescriptor pd = (EntryDescriptor) plugiter.next();
                if (pd.getName().equalsIgnoreCase("pluginInvoicex")) {
                    pi = true;
                }
            }
        }
        if (!pi) {
            HttpUtils.saveBigFile("http://www.tnx.it/pagine/invoicex_server/download/invoicex/plugins/InvoicexPluginInvoicex.jar", "plugins/InvoicexPluginInvoicex.jar");
            pf.loadPlugins(plugins_path);
            plugcol = pf.getAllEntryDescriptor();
        }
        if (plugcol != null) {
            Iterator plugiter = plugcol.iterator();
            while (plugiter.hasNext()) {
                EntryDescriptor pd = (EntryDescriptor) plugiter.next();
                if (pd.getName().equalsIgnoreCase("pluginInvoicex")) {
                    pi = true;
                    pluginPresenti.add(pd.getName());
                    plugins.put(pd.getName(), pd);
                    try {
                        PluginEntry pl = (PluginEntry) pf.getPluginEntry(pd.getId());
                        pl.initPluginEntry(null);
                        pl.startPluginEntry();
                        pluginsAvviati.put(pd.getName(), pl);
                    } catch (NoSuchFieldError nofield) {
                        nofield.printStackTrace();
                    } catch (NoClassDefFoundError noclass) {
                        noclass.printStackTrace();
                    } catch (Throwable tr) {
                        tr.printStackTrace();
                    }
                    break;
                }
            }
        }

        if (!s1) {
            if (attivazione.getIdRegistrazione() != null) {
                pf = new PluginFactory();
                pf.loadPlugins(plugins_path);
                plugcol = pf.getAllEntryDescriptor();
                if (plugcol != null) {
                    Iterator plugiter = plugcol.iterator();
                    while (plugiter.hasNext()) {
                        EntryDescriptor pd = (EntryDescriptor) plugiter.next();
                        if (!pd.getName().equalsIgnoreCase("pluginInvoicex")) {
                            pluginPresenti.add(pd.getName());
                            plugins.put(pd.getName(), pd);

                            if (attivazione.getIdRegistrazione() != null) {
                                //avvio il plugin
                                try {
                                    MicroBench mb = new MicroBench();
                                    mb.start();
                                    main.getPadrePanel().lblInfoLoading2.setText("Caricamento plugin " + pd.getName() + "...");
                                    PluginEntry pl = (PluginEntry) pf.getPluginEntry(pd.getId());
                                    pl.initPluginEntry(null);
                                    System.out.println(pd.getName() + " Init -> tempo: " + mb.getDiff("init"));
                                    pl.startPluginEntry();
                                    pluginsAvviati.put(pd.getName(), pl);
                                    main.getPadrePanel().lblInfoLoading2.setText(pd.getName() + " Caricato");
                                    System.out.println(pd.getName() + " Caricato -> tempo: " + mb.getDiff("caricamento"));
                                } catch (NoSuchFieldError nofield) {
                                    nofield.printStackTrace();
                                    SwingUtils.showErrorMessage(getPadreFrame(), "<html>Errore durante l'avvio del <b>" + pd.getName() + "</b><br>Manca il campo <b>" + nofield.getMessage() + "</b><br>Probabilmente devi aggiornare Invoicex Base all'ultima release per utilizzare questo plugin</html>");
                                } catch (NoClassDefFoundError noclass) {
                                    noclass.printStackTrace();
                                    SwingUtils.showErrorMessage(getPadreFrame(), "<html>Errore durante l'avvio del <b>" + pd.getName() + "</b><br>Manca la classe <b>" + noclass.getMessage() + "</b><br>Probabilmente devi aggiornare Invoicex Base all'ultima release per utilizzare questo plugin</html>");
                                } catch (Throwable tr) {
                                    tr.printStackTrace();
                                    SwingUtils.showErrorMessage(getPadreFrame(), "<html>Errore durante l'avvio del <b>" + pd.getName() + "</b><br>" + tr.toString() + "</b><br>Probabilmente devi aggiornare Invoicex Base all'ultima release per utilizzare questo plugin</html>");
                                }
                            }
                            controllaFlagPlugin(pd.getName());
                        }
                    }
                    if (!s1) {
                        tscaduto.start();
                    }
                }
            }
        }

//        //controllo se far vedere note di rilascio
//        File fnote = new File("note_" + version.toStringUnderscore() + ".html");
//        File fnote2 = new File("note_" + version.toStringUnderscore() + "_vis.html");
//        if (fnote.exists() && !fnote2.exists()) {
//            try {
//                JDialogNoteRilascio note = new JDialogNoteRilascio(getPadre(), true);
//                note.jTextPane1.setText(FileUtils.readContent(fnote));
//                note.setLocationRelativeTo(null);
//                note.setVisible(true);
//                File fren = new File("note_" + version.toStringUnderscore() + "_vis.html");
//                if (fren.exists()) {
//                    fren.delete();
//                }
//                fnote.renameTo(fren);
//            } catch (FileNotFoundException ex) {
//                Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (IOException ex) {
//                Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }

//        try {
//            Thread.sleep(3000);
//        } catch (Exception e) {
//        }

        main.fine_init_plugin = true;

        main.getPadrePanel().lblInfoLoading2.setVisible(false);

        //salvo il param_prop.txt in backup;
        File fparam1 = new File(main.wd + paramProp);
        File fparam1b = new File(main.wd + paramProp + ".backup");
        try {
            FileUtils.copyFile(fparam1, fparam1b);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (s1) {
            return;
        }

        //controllo se ultimamente ha fatto un backup altrimenti glielo suggerisco
        File dirBackup = new File(main.wd + homeDir + "backup");

        if (!dirBackup.exists()) {
            dirBackup.mkdir();

            int ret = javax.swing.JOptionPane.showConfirmDialog(menu1, "Nell'ultima versione del programma abbiamo aggiunto la possibilita'\ndi eseguire delle copie dei dati dal menu' principale.\nVuoi subito eseguire una copia adesso?", "Attenzione", javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE);

            if (ret == javax.swing.JOptionPane.YES_OPTION) {

                //eseguo un dump del database
                System.out.println("inizio backup");

                //visualizzo frame con log processo
                it.tnx.JFrameMessage mess = new it.tnx.JFrameMessage();
                try {
                    mess.setIconImage(getLogoIcon());
                } catch (Exception err) {
                    err.printStackTrace();
                }
                mess.setBounds(100, 100, 400, 200);
                mess.show();

                DumpThread dump = new DumpThread(mess);
                dump.start();
                System.out.println("backup completato");
            }
        } else {

            //cerco ultimo file di backup
            File dir = new File(main.wd + homeDir + "backup");
            File[] lista = dir.listFiles();
            Vector listav = it.tnx.Util.getVectorFromArray(lista);

            if (listav != null) {
                java.util.Collections.sort(listav, new java.util.Comparator() {
                    public int compare(Object o1, Object o2) {

                        File f1 = (File) o1;
                        File f2 = (File) o2;

                        if (f1.lastModified() > f2.lastModified()) {

                            return -1;
                        } else if (f1.lastModified() < f2.lastModified()) {

                            return 1;
                        } else {

                            return 0;
                        }
                    }
                });

                File dump = (File) listav.get(0);
                Calendar cal = Calendar.getInstance();
                Calendar calOggi = Calendar.getInstance();
                Date dateModified = new Date(dump.lastModified());
                cal.setTime(dateModified);
                calOggi.setTime(new Date());

                long ldate1 = dateModified.getTime();
                long ldate2 = new Date().getTime();

                // Use integer calculation, truncate the decimals
                int hr1 = (int) (ldate1 / 3600000); //60*60*1000
                int hr2 = (int) (ldate2 / 3600000);
                int days1 = (int) hr1 / 24;
                int days2 = (int) hr2 / 24;
                int dateDiff = days2 - days1;

                if (dateDiff > 14) {
//                if (dateDiff >= 0) {

                    int ret = javax.swing.JOptionPane.showConfirmDialog(menu1, "Sono passate piu' di 2 settimane dall'ultima copia di sicurezza.\nVuoi eseguire adesso la copia?", "Attenzione", javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE);

                    if (ret == javax.swing.JOptionPane.YES_OPTION) {

                        if (pluginBackupTnx) {
                            System.out.println("inizio backup online");
                            main.getPadrePanel().callBackupOnline();
                            System.out.println("backup online completato");
                        } else {
                            //eseguo un dump del database
                            System.out.println("inizio backup locale");
                            //visualizzo frame con log processo
                            it.tnx.JFrameMessage mess = new it.tnx.JFrameMessage();
                            try {
                                mess.setIconImage(getLogoIcon());
                            } catch (Exception err) {
                                err.printStackTrace();
                            }
                            mess.setBounds(100, 100, 400, 200);
                            mess.show();

                            DumpThread dump2 = new DumpThread(mess);
                            dump2.start();
                            System.out.println("backup completato");
                        }
                    }
                }
            }
        }

        //info plugins
        if (!Main.applet) {
            if (fileIni.getValueBoolean("pref", "msg_plugins", true)) {
                DebugUtils.dump(pluginPresenti);
                if (!pluginPresenti.contains("pluginRicerca")) {
                    JDialogInstallaPlugins d = new JDialogInstallaPlugins(padre, true);
                    d.setLocationRelativeTo(null);
                    d.setVisible(true);
                    if (d.si) {
                        if (attivazione.getIdRegistrazione() == null) {
                            if (SwingUtils.showYesNoMessage(padre, "Per utilizzare le funzionalità aggiuntive devi prima registrare il programma,\nclicca su Sì per proseguire nella registrazione")) {
                                main.getPadrePanel().apridatiazienda();
                                if (attivazione.getIdRegistrazione() != null) {
                                    JDialogPlugins plugins = padre_panel.showplugins(true);
                                } else {
                                    SwingUtils.showInfoMessage(main.padre, "Impossibile scaricare i plugin, il programma non e' registrato");
                                }
                            }
                        } else {
                            JDialogPlugins plugins = padre_panel.showplugins(true);
                        }
                    } else {
                        if (d.jCheckBox1.isSelected()) {
                            fileIni.setValue("pref", "msg_plugins", false);
                        }
                    }
                }
            }
        }

//        adesso via news
//        //visualizzo note rilascio ?
//        String knote = "note_rilascio_181";
//        if (!fileIni.existKey("varie", knote)) {
//            fileIni.setValue("varie", knote, true);
//            getPadrePanel().visualizzaNote();
//        }


        //controllo aggiornamenti
        SwingWorker w = new SwingWorker() {
            @Override
            public Object construct() {
                try {
                    Thread.sleep(3000);
                    controllaupd();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        w.start();

        //controllo numerazione documenti
        if (fileIni.getValueBoolean("pref", "controlloNumeriAvvio", true)) {
            SwingWorker check_numerazioni = new SwingWorker() {
                @Override
                public Object construct() {
                    try {
                        Thread.sleep(5000);

                        String msg = "";
                        MicroBench mb = new MicroBench();
                        mb.start();
                        String msgf = controlloNumeri("test_fatt");
                        String msgd = controlloNumeri("test_ddt");
                        System.out.println("fine controllo numerazione tempo:" + mb.getDiff(""));
                        if (StringUtils.isNotBlank(msgf) || StringUtils.isNotBlank(msgd)) {
                            JPanel panel = new JPanel();
                            panel.setLayout(new GridBagLayout());
                            final JTextArea textArea = new JTextArea(10, 50);
                            msg = "Attenzione, problemi nella numerazione dei documenti:\n";
                            if (StringUtils.isNotBlank(msgf)) {
                                msg += "\nFatture di vendita:\n" + msgf;
                            }
                            if (StringUtils.isNotBlank(msgd)) {
                                msg += "\nDDT di vendita:\n" + msgd;
                            }
                            textArea.setText(msg);
                            textArea.setEditable(false);
                            JScrollPane scrollPane = new JScrollPane(textArea);
                            panel.add(scrollPane, new GridBagConstraints());
                            JButton print = new JButton("Stampa");
                            print.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    PrintUtilities p = new PrintUtilities(textArea);
                                    p.scala = 1;
                                    p.print();
                                }
                            });
                            GridBagConstraints gridBagConstraints = new GridBagConstraints();
                            gridBagConstraints.gridx = 0;
                            gridBagConstraints.gridy = 1;
                            gridBagConstraints.anchor = GridBagConstraints.EAST;
                            gridBagConstraints.insets = new Insets(6, 0, 3, 0);
                            panel.add(print, gridBagConstraints);
                            /*  -- DAVID -- */
                            // JOptionPane.showMessageDialog(main.getPadreFrame(), panel, "Attenzione !", JOptionPane.WARNING_MESSAGE);                           
                            /*  -- DAVID -- */
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
            check_numerazioni.start();
        }

        //controllo avvisi clienti
        SwingWorker check_avvisi = new SwingWorker() {
            @Override
            public Object construct() {
                try {
                    Random r = new Random();
                    int waittime = (int) (r.nextFloat() * 3000);
                    Thread.sleep(waittime);
                    main.padre_panel.segnalaRapporti();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        check_avvisi.start();

        //controlli file temporanei
        Thread tfiletemp = new Thread("deletefiletemp") {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                    eliminaFileTemporanei();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        tfiletemp.start();
    }

    ;

    public static void eliminaFileTemporanei() {
        //_old_
        //lib/_old_
        //tempStampa_
        //tempPrn
        //temp
        MicroBench mb = new MicroBench();
        int eliminati = 0;
        mb.start();
        File dir = new File(".");
        File[] files = dir.listFiles();
        Long now = System.currentTimeMillis();
        Long nowmeno7gg = now - (1000 * 60 * 60 * 24 * 7);
        Date d = new Date(nowmeno7gg);
        System.out.println("d = " + d);
        if (files != null) {
            for (File f : files) {
                boolean del = false;
                String n = f.getName();
                if (f.isFile()) {
                    if (n.startsWith("temp")) {
                        if (f.lastModified() < nowmeno7gg) {
                            del = true;
                        }
                    }
                    if (n.indexOf("_old_") > 0) {
                        if (f.lastModified() < (now - (1000l * 60l * 60l * 24l * 30l))) {
                            del = true;
                        }
                    }
                }
                if (del) {
                    System.out.println("delete di: " + f.getAbsolutePath());
                    f.delete();
                }
            }
        }
        dir = new File("lib");
        files = dir.listFiles();
        Long nowmeno30gg = now - (1000l * 60l * 60l * 24l * 30l);
        d = new Date(nowmeno30gg);
        System.out.println("d = " + d);
        if (files != null) {
            for (File f : files) {
                boolean del = false;
                String n = f.getName();
                if (f.isFile()) {
                    if (n.indexOf("_old_") > 0) {
                        if (f.lastModified() < nowmeno30gg) {
                            del = true;
                        }
                    }
                }
                if (del) {
                    System.out.println("delete di: " + f.getAbsolutePath());
                    f.delete();
                }
            }
        }
        mb.out("fine elimina files temp eliminati:" + eliminati);
    }

    public static String controlloNumeri(String nometab) {
        String doc1 = "";
        String doc2 = "";
        String doc3 = "";
        if (nometab.equalsIgnoreCase("test_fatt")) {
            doc1 = "alla Fattura";
            doc2 = "Fatture";
            doc3 = "Fattura";
        } else if (nometab.equalsIgnoreCase("test_ddt")) {
            doc1 = "al DDT";
            doc2 = "DDT";
            doc3 = "DDT";
        }
        String msg = "";
        try {
            String sql = "select serie from " + nometab + " where year(data) = year(now()) ";
            if (nometab.equals("test_fatt")) {
                sql += " and tipo_fattura != " + dbFattura.TIPO_FATTURA_SCONTRINO + " and tipo_fattura != " + dbFattura.TIPO_FATTURA_PROFORMA;
            }
            sql += " group by serie";
            ArrayList<String> serie = DbUtils.getList(Db.getConn(), sql);
            for (String s : serie) {
                System.out.println("s = " + s);
                sql = "select numero, data, serie, id from " + nometab + " where year(data) = year(now()) and serie = " + Db.pc(s, Types.VARCHAR);
                if (nometab.equals("test_fatt")) {
                    sql += " and tipo_fattura != " + dbFattura.TIPO_FATTURA_SCONTRINO + " and tipo_fattura != " + dbFattura.TIPO_FATTURA_PROFORMA;
                }
                sql += " order by data, numero";
                List<Map> numeri = DbUtils.getListMap(Db.getConn(), sql);
                List<Long> numeri2 = DbUtils.getList(Db.getConn(), sql);
                if (numeri.size() > 0) {
                    int start = CastUtils.toInteger0(numeri.get(0).get("numero"));
                    for (int i = 0; i < numeri.size(); i++) {
                        Map m = numeri.get(i);
                        int n = CastUtils.toInteger0(m.get("numero"));
                        int n0 = n;
                        int n2 = n;
                        if (i > 0) {
                            n0 = CastUtils.toInteger0(numeri.get(i - 1).get("numero"));
                        }
                        if (i < numeri.size() - 1) {
                            n2 = CastUtils.toInteger0(numeri.get(i + 1).get("numero"));
                        }
                        if (n != start && n0 < n - 1) {
                            if ((n0 + 1) == (n - 1)) {
                                //controllo se esiste o no
                                if (!numeri2.contains(((Integer) start).longValue())) {
                                    msg += doc3 + " numero " + s + (start) + " mancante\n";
                                } else {
                                    msg += doc3 + " numero " + s + (start) + " presente ma con data errata\n";
                                }
                            } else {
                                msg += doc2 + " mancanti dalla " + s + (n0 + 1) + " alla " + s + (n - 1) + "\n";
                            }
                            start = n;
                        } else if (n != start) {
                            //msg += "errore " + doc1 + " numero " + n + " (numero precedente " + n0 + ", numero successivo " + n2 + ")\n";
                            start = n;
                        }
                        start++;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return msg;
    }

    public static Image getLogoIcon() {
        //setIconImage(new ImageIcon(getClass().getResource("/res/48x48.gif")).getImage());
        File newicon = new File("icone/48x48_8.gif");
        if (newicon.exists()) {
            return new ImageIcon(newicon.getAbsolutePath()).getImage();
        } else {
            return new ImageIcon(main.class.getResource("/res/48x48.gif")).getImage();
        }
    }

    private void publish(String string) {
        System.out.println(string);
    }

    public void scaricaAggiornamentoPlugin(Plugin po, EntryDescriptor pd) throws IOException {
//        String server = "www.tnx.it";
//        int port = 8080;
        String server = "s.invoicex.it";
//        String server = "s.invoicex.linux";
        int port = 80;
        String post_url = "/InvoicexWSServer/SendAggPlugins";

//        server = "192.168.0.115";
//        port = 8080;
//        post_url = "/InvoicexWSServer/SendAggPlugins";

        publish("Invio richiesta aggiornamento");
        Socket socket = new Socket(server, port);

        //altro test
        try {
            System.out.println("test: " + HttpUtils.getUrlToStringUTF8("http://s.invoicex.it/InvoicexWSServer/SendAggPlugins"));
        } catch (Exception e) {
        }

        OutputStream outs = socket.getOutputStream();
        InputStream ins = socket.getInputStream();

        String get = "GET " + post_url + " HTTP/1.1\n"
                + "Host: " + server + "\n"
                + "User-Agent: Invoicex/" + main.version + "\n"
                //                + "Accept: text/html,application/xhtml+xml,application/xml,application/octet-stream;q=0.9,*/*;q=0.8\n"
                //                + "Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7\n"
                //                + "x-id: " + attivazione.getIdRegistrazione() + "\n"
                //                + "x-plugin: " + pd.getNomeFileJar() + "\n"
                //                + "Connection: keep-alive\n"
                + "x-id: " + attivazione.getIdRegistrazione() + "\n"
                + "x-plugin: " + pd.getNomeFileJar() + "\n"
                + "\n";

        outs.write(get.getBytes());
        outs.flush();

        System.out.println("get:" + get);

        System.out.println("1");

        byte[] bytesIn = new byte[1024 * 8];
        int readed = 0;

        publish("Inizio download agg");

        //inizio a ricevere il file
        File fd = new File(main.wd + "agg");
        fd.mkdir();
        File f = new File(main.wd + "plugins/" + pd.getNomeFileJar() + ".tmp");
        System.out.println("scarico: file:" + f);
        if (f.exists()) {
            f.delete();
        }
        FileOutputStream fos = new FileOutputStream(f);

        System.out.println("2");

        byte[] buff = new byte[10000];
        int read = 0;
        boolean headers = true;
        DecimalFormat f1 = new DecimalFormat("0,000.#");
        int toread = Integer.MAX_VALUE;
        while ((read = ins.read(buff)) > 0) {
            publish("Download " + f.getName() + " " + (int) ((double) readed / (double) toread * 100d) + "%");
            if (headers) {
                String temp1 = new String(buff);
                int ind1 = temp1.indexOf("\r\n\r\n");
                String sheaders = temp1.substring(0, ind1);
                System.out.println("headers:" + sheaders);
                String shs[] = sheaders.split("\\r\\n");
                for (String s : shs) {
                    System.out.println("s:" + s);
                    if (s.startsWith("HTTP/1.1 500")) {
                        System.out.println("problema nell'aggiornamento");
                        fos.close();
                        f.delete();
                        return;
                    }
                    if (s.startsWith("Content-Length")) {
                        String shs2[] = s.split(":");
                        toread = Integer.parseInt(shs2[1].trim());
                        System.out.println("toread:" + toread + " / " + shs2[1]);
                    }
                }
                readed += read - (ind1 + 4);
                fos.write(buff, ind1 + 4, read - (ind1 + 4));
                headers = false;
            } else {
                readed += read;
                //System.out.print(".");
                fos.write(buff, 0, read);
            }
            if (readed >= toread) {
                break;
            }
        }

        System.out.println("4 readed:" + readed);

        fos.flush();
        fos.close();
        System.out.println("");
        System.out.println("ricevuto:" + f + " / " + f.getAbsolutePath());

        File f2 = new File(main.wd + "plugins/" + pd.getNomeFileJar());
        System.out.println("copio: f:" + f + " in f2:" + f2);
        if (f2.exists()) {
            boolean ret = f2.delete();
            System.out.println("elimino precedente:" + ret);
        }
        FileUtils.copyFile(f, f2);
        boolean ret = f.delete();
        System.out.println("elimino tmp:" + ret);

        //analizzo plugin descriptor e aggiorno plugins
//        EntryDescriptor ed = pf.loadPluginByFileName(plugins_path, f2.getName());

        publish("Agg ricevuto");
    }
    public static ZipFile zf;
    public static final int EOF = -1;

    public static void unzip(File filezip, String dst) {
        Enumeration enum1;

        try {
            zf = new ZipFile(filezip.getAbsolutePath());
            enum1 = zf.entries();
            while (enum1.hasMoreElements()) {
                ZipEntry target = (ZipEntry) enum1.nextElement();
                System.out.print(target.getName() + " .");
                saveEntry(target, null, dst);
                System.out.println(". unpacked");
            }
        } catch (FileNotFoundException e) {
            System.out.println("zipfile not found");
        } catch (ZipException e) {
            System.out.println("zip error...");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IO error...");
        }
    }

    public static void saveEntry(ZipEntry target, UnzipWorker work) throws ZipException, IOException {
        saveEntry(target, work, null);
    }

    public static void saveEntry(ZipEntry target, UnzipWorker work, String dst) throws ZipException, IOException {
        try {
            File file = new File(target.getName());
            if (dst != null) {
                file = new File(dst + target.getName());
            }
            if (target.isDirectory()) {
                file.mkdirs();
            } else {
                InputStream is = zf.getInputStream(target);
                BufferedInputStream bis = new BufferedInputStream(is);

                if (file.getParent() != null) {
                    File dir = new File(file.getParent());
                    dir.mkdirs();
                }

                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                int c;
                byte[] buff = new byte[1204 * 32];
                int writed = 0;
                //while ((c = bis.read()) != EOF) {
                int towrite = (int) target.getSize();
                while ((c = bis.read(buff)) > 0) {
                    writed += c;
                    //bos.write((byte)c);
                    bos.write(buff, 0, c);
                    if (work != null) {
                        work.publicPublish("Unzip " + target + " " + (writed * 100 / towrite) + "%");
                    }
                }

                bos.close();
                fos.close();
            }
        } catch (ZipException e) {
            if (work != null) {
                work.publicPublish(e.toString());
            }
            throw e;
        } catch (IOException e) {
            if (work != null) {
                work.publicPublish(e.toString());
            }
            throw e;
        }
    }
    
    static public boolean isPluginContabilitaAttivo() {
        if (main.pluginContabilita) {
            System.out.println("isPluginContabilitaAttivo:" + main.fileIni.getValueBoolean("plugin_contab", "attivo", false));
            return main.fileIni.getValueBoolean("plugin_contab", "attivo", false);
        } else {
            System.out.println("isPluginContabilitaAttivo:false");
            return false;
        }
    }
}

class MyStreamGobbler
        extends Thread {

    InputStream is;
    String type;

    MyStreamGobbler(InputStream is, String type) {
        this.is = is;
        this.type = type;
    }

    public void run() {

        try {

            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;

            while ((line = br.readLine()) != null) {
                System.out.println(type + ">" + line);
                line(line);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void line(String line) {
    }
}
