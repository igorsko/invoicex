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

package tnxbeans;

import java.io.Serializable;
import java.beans.*;
import java.awt.*;
import java.awt.event.*;

import java.sql.*;
import javax.swing.*;
import java.util.Vector;
import javax.swing.table.*;

public class tnxList extends javax.swing.JList implements Serializable {
    public String dbNomeCampo;
    public String dbTipoCampo;
    public String dbDescCampo;
    public tnxTextField dbTextAbbinato;
    public boolean dbRiempire=true;
    public boolean dbSalvare=true;
    public boolean dbModificato=false;
    public boolean dbTrovaMentreScrive=false;
    public Vector dbItems = new Vector();
    public Vector dbItemsK = new Vector();
    public Vector dbItemsK2 = new Vector();
    
    private Connection connection;
    private java.sql.Statement stat;
    private String oldSql;
    private Connection oldConnection;
    
    private String selText="";
    private boolean contieneChiavi=false;
    
    private DefaultListModel lm = new DefaultListModel();
    
    public tnxList() {
        super();
        try {
            jbInit();
            
            //debug
      /*
      for (int i=0;i<this.getComponentCount();i++) {
        javax.swing.JOptionPane.showMessageDialog(null,this.getComponent(i).getName()+"-"+this.getComponent(i).getClass().getName()+"-"+String.valueOf(i));
      }*/
            
        } catch(Exception e) {
            e.printStackTrace();
        }
        
    }
    
    public void setConnection(Connection conn) {
        this.connection = conn;
    }
    public Connection getConnection() {
        return (this.connection);
    }
    
    public void setDbSalvare(boolean _flag) {
        dbSalvare = _flag;
    }
    public boolean getDbSalvare() {
        return dbSalvare;
    }
    
    public void setDbTextAbbinato(tnxTextField _text) {
        dbTextAbbinato=_text;
    }
    public tnxTextField getDbTextAbbinato() {
        return dbTextAbbinato;
    }
    
    public void setDbNomeCampo(String _nomeCampo) {
        dbNomeCampo=_nomeCampo;
    }
    public String getDbNomeCampo() {
        return dbNomeCampo;
    }
    
    public void setDbTipoCampo(String _nomeCampo) {
        dbTipoCampo=_nomeCampo;
    }
    public String getDbTipoCampo() {
        return dbTipoCampo;
    }
    
    public void setDbDescCampo(String _DescCampo) {
        dbDescCampo=_DescCampo;
    }
    public String getDbDescCampo() {
        return dbDescCampo;
    }
    
    public void setDbRiempire(boolean _flag) {
        dbRiempire = _flag;
    }
    public boolean getDbRiempire() {
        return dbRiempire;
    }
    
    
    public void setDbTrovaMentreScrive(boolean _trova) {
        dbTrovaMentreScrive = _trova;
    }
    public boolean getDbTrovaMentreScrive() {
        return dbTrovaMentreScrive;
    }
    
    public void setText(String testo) {
        JTextField temp = (JTextField)this.getComponent(2);
        temp.setText(testo);
    }
    
    public void dbTrovaKey(Object chiave) {
        for (int i = 0 ; i < this.dbItemsK.size() ; i++) {
            if (String.valueOf(this.dbItemsK.get(i)).equalsIgnoreCase(String.valueOf(chiave))) {
                this.setSelectedIndex(i);
                i = this.dbItemsK.size();
            }
        }
    }
    
    public String getText() {
        if (this.getComponentCount()>=2) {
            if (this.getComponent(2) != null) {
                JTextField temp = (JTextField)this.getComponent(2);
                return (temp.getText());
            } else {
                return ("");
            }
        } else return ("");
    }
    
    private void jbInit() throws Exception {
    }
    
    void this_keyTyped(KeyEvent e) {
        //debug
        //javax.swing.JOptionPane.showMessageDialog(null,"combo field keyTyped");
        
        dbModificato=true;
        tnxDbPanel temp = (tnxDbPanel)this.getParent();
        boolean res = temp.dbCheckModificati();
        
    }
    
    void this_itemStateChanged(ItemEvent e) {
        //quando cambiano item
        //debug
        //javax.swing.JOptionPane.showMessageDialog(null,"text field ite");
        
        if (dbTextAbbinato!=null) {
            if (this.getSelectedIndex() >= 0 && this.getSelectedIndex() < this.dbItemsK.size()) {
                dbTextAbbinato.setText(this.getSelectedKey().toString());
            }
        }
        
        //debug
        //javax.swing.JOptionPane.showMessageDialog(null,"combo changed");
        
        dbModificato=true;
        tnxDbPanel temp = (tnxDbPanel)this.getParent();
        boolean res = temp.dbCheckModificati();
    }
    
    void this_keyPressed(KeyEvent e) {
        //debug
        //javax.swing.JOptionPane.showMessageDialog(null,"pre");
        
        JTextField tex = (JTextField)this.getComponent(2);
        this.selText = tex.getSelectedText();
        
        //debug
        //System.out.println("selText:"+selText);
        
    }
    
    
    void this_focusLost(FocusEvent e) {
        //debug
        //javax.swing.JOptionPane.showMessageDialog(null,"focus");
        if (dbTextAbbinato!=null) {
            //dbTextAbbinato.setText(this.getSelectedKey().toString());
        }
        
    }
    
    public void dbClearList() {
        dbItems.removeAllElements();
        dbItemsK.removeAllElements();
        dbItemsK2.removeAllElements();
        lm.clear();
        this.setModel(lm);
    }
    
    public boolean dbOpenList(Connection connection,String sql) {
        dbClearList();
        this.oldSql = sql;
        this.oldConnection = connection;
        //apre il resultset da abbinare
        ResultSet resu = null;
        ResultSetMetaData meta;
        try {
            stat = connection.createStatement();
            resu = stat.executeQuery(sql);
            meta = resu.getMetaData();
            
            if (meta.getColumnCount()>1) {
                this.contieneChiavi = true;
            }
            
            //righe
            while (resu.next()) {
                for (int i=1;i<=meta.getColumnCount();++i) {
                    if (i==1) {
                        dbItems.add((Object)resu.getString(i));
                        lm.addElement((Object)resu.getString(i));
                    } else if (i==2) {
                        dbItemsK.add((Object)resu.getString(i));
                        
                        //debug
                        //System.out.println("list:" + String.valueOf(i) + ":" + resu.getString(i));
                    } else if (i==3) {
                        dbItemsK2.add((Object)resu.getString(i));
                    }
                }
            }
            this.setModel(lm);
            
            //vado al primo
            if (dbTextAbbinato!=null) {
                //debug
                //javax.swing.JOptionPane.showMessageDialog(null,this.getKey(0).toString());
                dbTextAbbinato.setText(this.getKey(0).toString());
            }
            
            return(true);
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(null,e.toString());
            e.printStackTrace();
            return(false);
        } finally {
            try {
                stat.close();
            } catch (Exception e) {
            }
            try {
                resu.close();
            } catch (Exception e) {
            }
            meta = null;
        }
    }
    
    public void dbAddElement(Object testo, Object valore) {
        lm.addElement(testo);
        dbItems.add(testo);
        dbItemsK.add(valore);
        dbItemsK2.add(null);
        this.setModel(lm);
    }
    public void dbAddElement(String testo, String valore) {
        lm.addElement(testo);
        dbItems.add((Object)testo);
        dbItemsK.add((Object)valore);
        dbItemsK2.add(null);
        this.setModel(lm);
    }
    public void dbAddElement(Object testo, Object valore, Object valore2) {
        lm.addElement(testo);
        dbItems.add(testo);
        dbItemsK.add(valore);
        dbItemsK2.add(valore2);
        this.setModel(lm);
    }
    
    public void dbReplaceTextAt(String testo, int index) {
        lm.setElementAt(testo,index);
        this.setModel(lm);
    }
    
    public void dbRemoveElement(int id) {
        lm.remove(id);
        dbItems.remove(id);
        dbItemsK.remove(id);
        dbItemsK2.remove(id);
        this.setModel(lm);
    }
    
    public Object getSelectedKey() {
        if (this.contieneChiavi == true) {
            //passo la chiave abbinata
            try {
                if (this.getSelectedIndex() == -1) {
                    return (new Integer(-1));
                } else {
                    return (this.dbItemsK.get(this.getSelectedIndex()));
                }
            } catch (Exception err) {
                err.printStackTrace();
                return (new Integer(-1));
            }
        } else {
            return (new Integer(-1));
        }
    }
    
    public Object getKey(int i) {
        if (this.contieneChiavi == true) {
            //passo la chiave abbinata
            try {
                return (this.dbItemsK.get(i));
            } catch (Exception err) {
                err.printStackTrace();
                return (new Integer(-1));
            }
        } else {
            return (new Integer(-1));
        }
    }
    
/*
  public Object getSelectedItem() {
    if (this.contieneChiavi == true) {
      //passo la chiave abbinata
      try {
        if (this.getSelectedIndex() == -1) {
          return (new Integer(-1));
        } else {
          if (this.getSe
          Integer ret = new Integer(String.valueOf(this.dbItemsK.get(this.getSelectedIndex())));
          return (ret);
        }
      } catch (Exception err) {
        err.printStackTrace();
        return (new Integer(-1));
      }
    } else {
      try {
        return (this.getItemAt(this.getSelectedIndex()));
      } catch (Exception err) {
        err.printStackTrace();
        return (new Integer(-1));
      }
    }
  }
 */
}
