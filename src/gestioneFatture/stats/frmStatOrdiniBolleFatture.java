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
package gestioneFatture.stats;

import java.text.*;

import gestioneFatture.*;
import it.tnx.Db;
import it.tnx.Db.*;
import it.tnx.commons.DateUtils;
import java.awt.Color;
import java.sql.*;

//jasper
import java.util.Calendar;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.*;

//jfreechart
import org.jdesktop.swingworker.SwingWorker;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;


import org.jfree.data.category.DefaultCategoryDataset;

public class frmStatOrdiniBolleFatture extends javax.swing.JInternalFrame {

    String tempdata1 = "";
    String tempdata2 = "";
    String dataPartenza = "";
    String dataArrivo = "";

    public static java.awt.Image createOrdiniBolleFattureGraph(String tipoDoc, String serie) {

        String sql = "";

        java.sql.ResultSet res = null;

        DefaultCategoryDataset cd = new DefaultCategoryDataset();

        //raggruppati per mese
        sql = "select anno, mese, sum(totale_imponibile) as totaleMese";
        sql += " from temp_stampa_stat_ord_bol_fat";
        sql += " where hostname = USER()";
        if (tipoDoc != null) {
            sql += " and tipo_doc = " + Db.pc(tipoDoc, Types.VARCHAR);
        }
        if (serie != null) {
            sql += " and serie = " + Db.pc(serie, Types.VARCHAR);
        }
        sql += " group by anno, mese";
        try {
            res = it.tnx.Db.openResultSet(sql);
            while (res.next()) {
                if (tipoDoc != null) {
                    cd.addValue(res.getDouble("totaleMese"), tipoDoc, res.getString("mese"));
                }
            }
        } catch (Exception err) {
            err.printStackTrace();
        }

        //scorro per i tipi di documento
        if (tipoDoc == null) {
            try {
                sql = "select anno, mese, sum(totale_imponibile) as totaleMese";
                sql += " from temp_stampa_stat_ord_bol_fat";
                sql += " where hostname = USER()";
                sql += " and tipo_doc = " + Db.pc("Ordini", Types.VARCHAR);
                sql += " group by anno, mese";
                sql += " order by anno, mese";
                res = it.tnx.Db.openResultSet(sql);
                while (res.next()) {
                    cd.addValue(res.getDouble("totaleMese"), "Ordini", res.getString("mese"));
                }
                sql = "select anno, mese, sum(totale_imponibile) as totaleMese";
                sql += " from temp_stampa_stat_ord_bol_fat";
                sql += " where hostname = USER()";
                sql += " and tipo_doc = " + Db.pc("Bolle", Types.VARCHAR);
                sql += " group by anno, mese";
                sql += " order by anno, mese";
                res = it.tnx.Db.openResultSet(sql);
                while (res.next()) {
                    cd.addValue(res.getDouble("totaleMese"), "Bolle", res.getString("mese"));
                }
                sql = "select anno, mese, sum(totale_imponibile) as totaleMese";
                sql += " from temp_stampa_stat_ord_bol_fat";
                sql += " where hostname = USER()";
                sql += " and tipo_doc = " + Db.pc("Fatture", Types.VARCHAR);
                sql += " group by anno, mese";
                sql += " order by anno, mese";
                res = it.tnx.Db.openResultSet(sql);
                while (res.next()) {
                    cd.addValue(res.getDouble("totaleMese"), "Fatture", res.getString("mese"));
                }
            } catch (Exception err) {
                err.printStackTrace();
            }
        }

        JFreeChart chart = ChartFactory.createLineChart(null, "Mese", "Totale", cd, org.jfree.chart.plot.PlotOrientation.VERTICAL, true, true, true);
        chart.setAntiAlias(true);
        chart.setTextAntiAlias(true);
        chart.setBackgroundPaint(new Color(255, 255, 255));
        chart.getCategoryPlot().setBackgroundPaint(new Color(255, 255, 255));
        return chart.createBufferedImage(800, 400);
    }

    public static String testReturnString(String i) {
        return "ciao " + i + " ciccio";
    }

    public static String getDescMese(String mese) {
        if (mese.equals("1")) {
            return "Gennaio";
        } else if (mese.equals("2")) {
            return "Febbraio";
        } else if (mese.equals("3")) {
            return "Marzo";
        } else if (mese.equals("4")) {
            return "Aprile";
        } else if (mese.equals("5")) {
            return "Maggio";
        } else if (mese.equals("6")) {
            return "Giugno";
        } else if (mese.equals("7")) {
            return "Luglio";
        } else if (mese.equals("8")) {
            return "Agosto";
        } else if (mese.equals("9")) {
            return "Settembre";
        } else if (mese.equals("10")) {
            return "Ottobre";
        } else if (mese.equals("11")) {
            return "Novembre";
        } else if (mese.equals("12")) {
            return "Dicembre";
        }
        return "";
    }

    public static java.awt.Image testReturnImage(String i) {
        // create a dataset...
        DefaultPieDataset data = new DefaultPieDataset();

        // fill dataset with employeeData
        data.setValue(i, 15);
        data.setValue(i, 20);
        data.setValue(i, 38);

        // create a chart with the dataset
        JFreeChart chart = ChartFactory.createPieChart(i, data, true, true, true);

        // create and return the image
        return chart.createBufferedImage(500, 220);
    }

    /** Creates new form frmStatAgenti */
    public frmStatOrdiniBolleFatture() {
        initComponents();

        java.util.Date dataInizioAnno = it.tnx.Util.getDateTime("01/01/" + it.tnx.Util.getCurrenteYear());
        this.jTextField1.setText(it.tnx.Util.getDateStringITALIAN(dataInizioAnno, java.text.DateFormat.SHORT));

        this.jTextField2.setText(it.tnx.Util.getDateStringITALIAN(java.text.DateFormat.SHORT));

        comCliente.dbAddElement("<tutti i clienti>", "*");
        comCliente.dbOpenList(Db.getConn(), "select ragione_sociale, codice from clie_forn order by ragione_sociale", "*", false);

        comArticolo.dbAddElement("<tutti gli articoli>", "*");
        comArticolo.dbOpenList(Db.getConn(), "select descrizione, codice from articoli order by descrizione", "*", false);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        butConferma = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        cheDettagli = new javax.swing.JCheckBox();
        jLabel6 = new javax.swing.JLabel();
        comCliente = new tnxbeans.tnxComboField();
        jLabel7 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        comArticolo = new tnxbeans.tnxComboField();
        cheQta = new javax.swing.JCheckBox();
        cheSoloOrdini = new javax.swing.JCheckBox();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Report su Ordini / Bolle / Fatture");

        jTextField1.setColumns(10);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("al giorno");

        jTextField2.setColumns(10);

        butConferma.setText("Visualizza il Report");
        butConferma.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butConfermaActionPerformed(evt);
            }
        });

        jLabel5.setText("Seleziona il periodo");

        cheDettagli.setSelected(true);
        cheDettagli.setText("Stampa singoli documenti");
        cheDettagli.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        cheDettagli.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Dal giorno");

        comCliente.setDbDescCampo("");
        comCliente.setDbNomeCampo("");
        comCliente.setDbTipoCampo("");
        comCliente.setDbTrovaMentreScrive(true);
        comCliente.setPreferredSize(new java.awt.Dimension(137, 18));
        comCliente.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comClienteItemStateChanged(evt);
            }
        });
        comCliente.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                comClienteFocusLost(evt);
            }
        });

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Filtra per Cliente");

        jLabel1.setText("Filtra per Articolo");

        comArticolo.setDbTrovaMentreScrive(true);

        cheQta.setSelected(true);
        cheQta.setText("stampa le quantità");
        cheQta.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        cheQta.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        cheSoloOrdini.setSelected(true);
        cheSoloOrdini.setText("conteggia solo gli ordini e ignora i preventivi");
        cheSoloOrdini.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        cheSoloOrdini.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(layout.createSequentialGroup()
                            .add(jLabel6)
                            .add(7, 7, 7)
                            .add(jTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(20, 20, 20)
                            .add(jLabel2)
                            .add(5, 5, 5)
                            .add(jTextField2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(cheDettagli)
                        .add(cheQta)
                        .add(cheSoloOrdini)
                        .add(layout.createSequentialGroup()
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                    .add(jLabel7)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                                .add(layout.createSequentialGroup()
                                    .add(jLabel1)
                                    .add(1, 1, 1)))
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(comArticolo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 310, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(comCliente, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 310, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                    .add(jLabel5))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(283, Short.MAX_VALUE)
                .add(butConferma)
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {jLabel1, jLabel7}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.linkSize(new java.awt.Component[] {comArticolo, comCliente}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel5)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel6)
                    .add(jTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2)
                    .add(jTextField2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(cheDettagli)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(comCliente, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel7))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(comArticolo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(cheQta)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cheSoloOrdini)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 35, Short.MAX_VALUE)
                .add(butConferma)
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {comArticolo, comCliente}, org.jdesktop.layout.GroupLayout.VERTICAL);

        pack();
    }// </editor-fold>//GEN-END:initComponents

  private void butConfermaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butConfermaActionPerformed

      //controllo date
      if (it.tnx.Checks.isDate(this.jTextField1.getText()) == false) {
          javax.swing.JOptionPane.showInternalMessageDialog(this, "La data di partenza non e' valida !", "Attenzione", javax.swing.JOptionPane.WARNING_MESSAGE);
          return;
      }
      if (it.tnx.Checks.isDate(this.jTextField2.getText()) == false) {
          javax.swing.JOptionPane.showInternalMessageDialog(this, "La data di arrivo non e' valida !", "Attenzione", javax.swing.JOptionPane.WARNING_MESSAGE);
          return;
      }

      this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

      final frmStatOrdiniBolleFatture _this = this;
      SwingWorker worker = new SwingWorker() {

          @Override
          protected Object doInBackground() throws Exception {
              String sql = "";
              String sqli = "";

              sql = "delete from temp_stampa_stat_ord_bol_fat";
              sql += " where hostname = USER()";
              it.tnx.Db.executeSql(sql);

              java.text.SimpleDateFormat riformattazione = new java.text.SimpleDateFormat("yyyy-MM-dd");
              sql = "select sum(round(calcola_importo_netto(r.prezzo * r.quantita, r.sconto1, r.sconto2, t.sconto1, t.sconto2, t.sconto3),2)) + IFNULL(t.spese_trasporto,0) + IFNULL(t.spese_incasso,0) as totaleArticolo, c.ragione_sociale, r.codice_articolo, month(t.data) as mese, t.* , sum(r.quantita) as tot_qta from test_fatt t join righ_fatt r on t.id = r.id_padre  join clie_forn c on t.cliente = c.codice ";

              try {
                  DateFormat parsaData = new SimpleDateFormat("dd/MM/yy");
                  java.util.Date giorno1 = parsaData.parse(_this.jTextField1.getText());
                  dataPartenza = riformattazione.format(giorno1);
                  java.util.Date giorno2 = parsaData.parse(_this.jTextField2.getText());
                  dataArrivo = riformattazione.format(giorno2);
                  sql += "where t.data between '" + dataPartenza + "' and '" + dataArrivo + "'";

                  if (comCliente.getSelectedIndex() > 0) {
                      sql += " and cliente = '" + Integer.parseInt((String) _this.comCliente.getSelectedKey()) + "'";
                  }

                  if (comArticolo.getSelectedIndex() > 0) {
                      sql += " and r.codice_articolo = '" + comArticolo.getSelectedKey() + "'";
                  }
                  sql += " and t.tipo_fattura != 6";
                  sql += " and t.tipo_fattura != 7";
                  sql += " group by t.numero, t.serie, t.anno order by t.serie, t.anno, t.numero";
              } catch (Exception err) {
                  err.printStackTrace();
              }

              System.out.println("query report:" + sql);

              //preparo la tabella di appoggio
              try {
                  java.sql.ResultSet res = it.tnx.Db.openResultSet(sql);
                  while (res.next()) {
                      sqli = "insert into temp_stampa_stat_ord_bol_fat values (";
                      sqli += "USER()";
                      sqli += ", 3";
                      sqli += ", 'Fatture'";
                      sqli += ", " + Db.pc(res.getString("serie"), Types.VARCHAR);
                      sqli += ", " + res.getInt("numero");
                      sqli += ", " + res.getInt("anno");
                      sqli += ", " + res.getInt("mese");
                      sqli += ", " + Db.pc(res.getDate("data"), Types.DATE);
                      sqli += ", " + it.tnx.Db.pc(res.getString("ragione_sociale"), java.sql.Types.VARCHAR);
                      double imp = res.getDouble("totaleArticolo");
                      if (res.getInt("tipo_fattura") == dbFattura.TIPO_FATTURA_NOTA_DI_CREDITO) {
                          imp = -(Math.abs(imp));
                      }
                      sqli += ", " + it.tnx.Db.pc(imp, java.sql.Types.DOUBLE);
                      sqli += ", " + it.tnx.Db.pc(res.getDouble("tot_qta"), java.sql.Types.DOUBLE);
                      sqli += ")";
                      it.tnx.Db.executeSql(sqli);
                  }
              } catch (Exception err) {
                  err.printStackTrace();
              }

              //scontrini
              sql = "select sum(round(calcola_importo_netto(r.prezzo * r.quantita, r.sconto1, r.sconto2, t.sconto1, t.sconto2, t.sconto3),2)) + IFNULL(t.spese_trasporto,0) + IFNULL(t.spese_incasso,0) as totaleArticolo, c.ragione_sociale, r.codice_articolo, month(t.data) as mese, t.*  , sum(r.quantita) as tot_qta from test_fatt t join righ_fatt r on t.id = r.id_padre  left join clie_forn c on t.cliente = c.codice ";
              try {
                  DateFormat parsaData = new SimpleDateFormat("dd/MM/yy");
                  java.util.Date giorno1 = parsaData.parse(_this.jTextField1.getText());
                  dataPartenza = riformattazione.format(giorno1);
                  java.util.Date giorno2 = parsaData.parse(_this.jTextField2.getText());
                  dataArrivo = riformattazione.format(giorno2);
                  sql += "where t.data between '" + dataPartenza + "' and '" + dataArrivo + "'";
                  if (comCliente.getSelectedIndex() > 0) {
                      sql += " and cliente = '" + Integer.parseInt((String) _this.comCliente.getSelectedKey()) + "'";
                  }

                  if (comArticolo.getSelectedIndex() > 0) {
                      sql += " and r.codice_articolo = '" + comArticolo.getSelectedKey() + "'";
                  }
                  sql += " and t.tipo_fattura = 7";
                  sql += " group by t.id order by t.id";
              } catch (Exception err) {
                  err.printStackTrace();
              }
              System.out.println("query report:" + sql);
              //preparo la tabella di appoggio
              try {
                  java.sql.ResultSet res = it.tnx.Db.openResultSet(sql);
                  while (res.next()) {
                      //temp_stampa_stat_ord_bol_fat (hostname varchar(255), tipo_doc_ordine int, tipo_doc varchar(50), serie char(1), numero int, anno int, mese int, data date, cliente varchar(255), totale_imponibile decimal(12,2))";
                      sqli = "insert into temp_stampa_stat_ord_bol_fat values (";
                      sqli += "USER()";
                      sqli += ", 4";
                      sqli += ", 'Scontrini'";
                      sqli += ", ''"; //la serie non c'è negli scontrini
                      sqli += ", " + res.getInt("numero");
                      //              sqli += ", " + res.getInt("anno");
                      int anno = 0;
                      try {
                          res.getDate("data");
                          Calendar cal = Calendar.getInstance();
                          cal.setTime(res.getDate("data"));
                          anno = cal.get(Calendar.YEAR);
                      } catch (Exception e) {
                          e.printStackTrace();
                      }
                      sqli += ", " + anno;
                      sqli += ", " + res.getInt("mese");
                      sqli += ", " + Db.pc(res.getDate("data"), Types.DATE);
                      sqli += ", 'scontrino - anonimo'"; //ragione sociale ...
                      double imp = res.getDouble("totaleArticolo");
                      if (res.getInt("tipo_fattura") == dbFattura.TIPO_FATTURA_NOTA_DI_CREDITO) {
                          imp = -imp;
                      }
                      sqli += ", " + it.tnx.Db.pc(imp, java.sql.Types.DOUBLE);
                      sqli += ", " + it.tnx.Db.pc(res.getDouble("tot_qta"), java.sql.Types.DOUBLE);
                      sqli += ")";
                      System.out.println("sqli = " + sqli);
                      it.tnx.Db.executeSql(sqli);
                  }
              } catch (Exception err) {
                  err.printStackTrace();
              }

              //per ordini
              sql = "select sum(round(calcola_importo_netto(r.prezzo * r.quantita, r.sconto1, r.sconto2, t.sconto1, t.sconto2, t.sconto3),2)) + IFNULL(t.spese_trasporto,0) + IFNULL(t.spese_incasso,0) as totaleArticolo, c.ragione_sociale, r.codice_articolo, month(t.data) as mese, t.*  , sum(r.quantita) as tot_qta from test_ordi t join righ_ordi r on t.serie = r.serie and t.numero = r.numero and t.anno = r.anno  join clie_forn c on t.cliente = c.codice ";

              try {
                  DateFormat parsaData = new SimpleDateFormat("dd/MM/yy");
                  java.util.Date giorno1 = parsaData.parse(_this.jTextField1.getText());
                  dataPartenza = riformattazione.format(giorno1);
                  java.util.Date giorno2 = parsaData.parse(_this.jTextField2.getText());
                  dataArrivo = riformattazione.format(giorno2);
                  sql += "where t.data between '" + dataPartenza + "' and '" + dataArrivo + "'";
                  if (comCliente.getSelectedIndex() > 0) {
                      sql += " and cliente = '" + Integer.parseInt((String) _this.comCliente.getSelectedKey()) + "'";
                  }
                  if (comArticolo.getSelectedIndex() > 0) {
                      sql += " and r.codice_articolo = '" + comArticolo.getSelectedKey() + "'";
                  }
                  if (cheSoloOrdini.isSelected()) {
                    sql += " and stato_ordine like '%ordine%'";
                  }
                  sql += " group by t.numero, t.serie, t.anno order by t.serie, t.anno, t.numero";
              } catch (Exception err) {
                  err.printStackTrace();
              }

              try {
                  java.sql.ResultSet res = it.tnx.Db.openResultSet(sql);
                  while (res.next()) {
                      //temp_stampa_stat_ord_bol_fat (hostname varchar(255), tipo_doc varchar(50), serie char(1), numero int, anno int, cliente varchar(255), totale_imponibile decimal(12,2))";
                      sqli = "insert into temp_stampa_stat_ord_bol_fat values (";
                      sqli += "USER()";
                      sqli += ", 1";
                      sqli += ", 'Ordini'";
                      sqli += ", " + Db.pc(res.getString("serie"), Types.VARCHAR);
                      sqli += ", " + res.getInt("numero");
                      sqli += ", " + res.getInt("anno");
                      sqli += ", " + res.getInt("mese");
                      sqli += ", " + Db.pc(res.getDate("data"), Types.DATE);
                      sqli += ", " + it.tnx.Db.pc(res.getString("ragione_sociale"), java.sql.Types.VARCHAR);
                      sqli += ", " + it.tnx.Db.pc(res.getDouble("totale_imponibile"), java.sql.Types.DOUBLE);
                      sqli += ", " + it.tnx.Db.pc(res.getDouble("tot_qta"), java.sql.Types.DOUBLE);
                      sqli += ")";
                      it.tnx.Db.executeSql(sqli);
                  }
              } catch (Exception err) {
                  err.printStackTrace();
              }

              //per bolle
              sql = "select sum(round(calcola_importo_netto(r.prezzo * r.quantita, r.sconto1, r.sconto2, t.sconto1, t.sconto2, t.sconto3),2)) + IFNULL(t.spese_trasporto,0) + IFNULL(t.spese_incasso,0) as totaleArticolo, c.ragione_sociale, r.codice_articolo, month(t.data) as mese, t.*  , sum(r.quantita) as tot_qta from test_ddt t join righ_ddt r on t.serie = r.serie and t.numero = r.numero and t.anno = r.anno  join clie_forn c on t.cliente = c.codice ";

              try {
                  DateFormat parsaData = new SimpleDateFormat("dd/MM/yy");
                  java.util.Date giorno1 = parsaData.parse(_this.jTextField1.getText());
                  dataPartenza = riformattazione.format(giorno1);
                  java.util.Date giorno2 = parsaData.parse(_this.jTextField2.getText());
                  dataArrivo = riformattazione.format(giorno2);
                  sql += "where t.data between '" + dataPartenza + "' and '" + dataArrivo + "'";
                  if (comCliente.getSelectedIndex() > 0) {
                      sql += " and cliente = '" + Integer.parseInt((String) _this.comCliente.getSelectedKey()) + "'";
                  }
                  if (comArticolo.getSelectedIndex() > 0) {
                      sql += " and r.codice_articolo = '" + comArticolo.getSelectedKey() + "'";
                  }
                  sql += " group by t.numero, t.serie, t.anno order by t.serie, t.anno, t.numero";
              } catch (Exception err) {
                  err.printStackTrace();
              }

              try {
                  java.sql.ResultSet res = it.tnx.Db.openResultSet(sql);
                  while (res.next()) {
                      //temp_stampa_stat_ord_bol_fat (hostname varchar(255), tipo_doc varchar(50), serie char(1), numero int, anno int, cliente varchar(255), totale_imponibile decimal(12,2))";
                      sqli = "insert into temp_stampa_stat_ord_bol_fat values (";
                      sqli += "USER()";
                      sqli += ", 2";
                      sqli += ", 'Bolle'";
                      sqli += ", " + Db.pc(res.getString("serie"), Types.VARCHAR);
                      sqli += ", " + res.getInt("numero");
                      sqli += ", " + res.getInt("anno");
                      sqli += ", " + res.getInt("mese");
                      sqli += ", " + Db.pc(res.getDate("data"), Types.DATE);
                      sqli += ", " + it.tnx.Db.pc(res.getString("ragione_sociale"), java.sql.Types.VARCHAR);
                      sqli += ", " + it.tnx.Db.pc(res.getDouble("totaleArticolo"), java.sql.Types.DOUBLE);
                      sqli += ", " + it.tnx.Db.pc(res.getDouble("tot_qta"), java.sql.Types.DOUBLE);
                      sqli += ")";
                      it.tnx.Db.executeSql(sqli);
                  }
              } catch (Exception err) {
                  err.printStackTrace();
              }

              //per fatture di acquisto
              sql = "select sum(round(calcola_importo_netto(r.prezzo * r.quantita, r.sconto1, r.sconto2, t.sconto1, t.sconto2, t.sconto3),2)) + IFNULL(t.spese_trasporto,0) + IFNULL(t.spese_incasso,0) as totaleArticolo, c.ragione_sociale, r.codice_articolo, month(t.data) as mese, t.*  , sum(r.quantita) as tot_qta from test_fatt_acquisto t join righ_fatt_acquisto r on t.serie = r.serie and t.numero = r.numero and t.anno = r.anno  join clie_forn c on t.fornitore = c.codice ";

              try {
                  DateFormat parsaData = new SimpleDateFormat("dd/MM/yy");
                  java.util.Date giorno1 = parsaData.parse(_this.jTextField1.getText());
                  dataPartenza = riformattazione.format(giorno1);
                  java.util.Date giorno2 = parsaData.parse(_this.jTextField2.getText());
                  dataArrivo = riformattazione.format(giorno2);
                  sql += "where t.data between '" + dataPartenza + "' and '" + dataArrivo + "'";
                  if (comCliente.getSelectedIndex() > 0) {
                      sql += " and t.fornitore = '" + Integer.parseInt((String) _this.comCliente.getSelectedKey()) + "'";
                  }
                  if (comArticolo.getSelectedIndex() > 0) {
                      sql += " and r.codice_articolo = '" + comArticolo.getSelectedKey() + "'";
                  }
                  sql += " group by t.numero, t.serie, t.anno order by t.serie, t.anno, t.numero";
              } catch (Exception err) {
                  err.printStackTrace();
              }

              try {
                  java.sql.ResultSet res = it.tnx.Db.openResultSet(sql);
                  while (res.next()) {
                      //temp_stampa_stat_ord_bol_fat (hostname varchar(255), tipo_doc varchar(50), serie char(1), numero int, anno int, cliente varchar(255), totale_imponibile decimal(12,2))";
                      sqli = "insert into temp_stampa_stat_ord_bol_fat values (";
                      sqli += "USER()";
                      sqli += ", 10";
                      sqli += ", 'Fatture Acquisto'";
                      sqli += ", " + Db.pc(res.getString("serie"), Types.VARCHAR);
                      sqli += ", " + res.getInt("numero");
                      sqli += ", " + res.getInt("anno");
                      sqli += ", " + res.getInt("mese");
                      sqli += ", " + Db.pc(res.getDate("data_doc"), Types.DATE);
                      sqli += ", " + it.tnx.Db.pc(res.getString("ragione_sociale"), java.sql.Types.VARCHAR);
                      sqli += ", " + it.tnx.Db.pc(res.getDouble("imponibile"), java.sql.Types.DOUBLE);
                      sqli += ", " + it.tnx.Db.pc(res.getDouble("tot_qta"), java.sql.Types.DOUBLE);
                      sqli += ")";
                      it.tnx.Db.executeSql(sqli);
                  }
              } catch (Exception err) {
                  err.printStackTrace();
              }

              //this.gridAgenti.dbOpen(Db.conn, sqlAggiorna);
              try {
                  //con compilazione
                  //System.out.println("load jrxml");
                  //JasperDesign jasperDesign = JasperManager.loadXmlDesign("/mnt/tnx/tnx/lavori/gianni/GestioneFatture/reports/stats_monthly.jrxml");
                  //JasperDesign jasperDesign = JasperManager.loadXmlDesign("C:/cvs_tnx/tnx/Invoicex15/src/reports/stats_monthly.jrxml");
                  //System.out.print("compilazione...");
                  //JasperReport jasperReport = JasperManager.compileReport(jasperDesign);
                  //System.out.println("...ok");
                  //senza compilazione

                  System.out.println("load jasper");
                  JasperReport jasperReport = JasperManager.loadReport("reports/stats_monthly.jasper");

                  // Second, create a map of parameters to pass to the report.
                  java.util.Map parameters = new java.util.HashMap();
                  parameters.put("periodo", "Dal " + _this.jTextField1.getText() + " al " + _this.jTextField2.getText());
                  parameters.put("stampaDettagli", new Boolean(cheDettagli.isSelected()));
                  parameters.put("stampaQta", new Boolean(cheQta.isSelected()));

                  // Third, get a database connection
                  java.sql.Connection conn = it.tnx.Db.getConn();
                  // Fourth, create JasperPrint using fillReport() method
                  JasperPrint jasperPrint = JasperManager.fillReport(jasperReport, parameters, conn);

                  // You can use JasperPrint to create PDF
                  //JasperManager.printReportToPdfFile(jasperPrint, "/home/marco/pippo/test1.pdf");
                  // Or to view report in the JasperViewer
                  JasperViewer.viewReport(jasperPrint, false);
              } catch (Exception err) {
                  err.printStackTrace();
              }
              return null;
          }

          @Override
          protected void done() {
              _this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
          }
      };
      worker.execute();

  }//GEN-LAST:event_butConfermaActionPerformed

private void comClienteItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comClienteItemStateChanged
}//GEN-LAST:event_comClienteItemStateChanged

private void comClienteFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_comClienteFocusLost
}//GEN-LAST:event_comClienteFocusLost
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butConferma;
    private javax.swing.JCheckBox cheDettagli;
    private javax.swing.JCheckBox cheQta;
    private javax.swing.JCheckBox cheSoloOrdini;
    private tnxbeans.tnxComboField comArticolo;
    private tnxbeans.tnxComboField comCliente;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
