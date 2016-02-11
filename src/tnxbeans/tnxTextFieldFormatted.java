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

public class tnxTextFieldFormatted extends javax.swing.JFormattedTextField implements Serializable {
  public String dbNomeCampo;
  public String dbTipoCampo;
  public String dbDescCampo;
  public int maxChars=0;
  public tnxComboField dbComboAbbinata;
  public boolean dbRiempire=true;
  public boolean dbSalvare=true;
  public boolean dbModificato=false;
  private String dbDefault = "";
  public static String DEFAULT_CURRENT = "CURRENT";  

  public tnxTextFieldFormatted() {
    super();
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }

  }

  public tnxTextFieldFormatted(javax.swing.text.MaskFormatter mask) {
    super(mask);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }

  }

  //proprieta'
  public void setDbDefault(String valore) {
    this.dbDefault = valore;
  }
  
  public String getDbDefault() {
    return this.dbDefault;
  }

  public void setDbComboAbbinata(tnxComboField _combo) {
    try {
      if (_combo != null) {
        dbComboAbbinata=_combo;
      }
    } catch (Exception err) {
      err.printStackTrace();
    }
  }
  public tnxComboField getDbComboAbbinata() {
    try {
      return dbComboAbbinata;
     } catch (Exception err) {
      err.printStackTrace();
      return null;
    }
  }

  public void setDbNomeCampo(String _nomeCampo) {
    dbNomeCampo=_nomeCampo;
  }
  public String getDbNomeCampo() {
    return dbNomeCampo;
  }

  public void setDbTipoCampo(String _tipoCampo) {
    dbTipoCampo=_tipoCampo;
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

  public void setDbSalvare(boolean _flag) {
    dbSalvare = _flag;
  }
  public boolean getDbSalvare() {
    return dbSalvare;
  }

  public void setmaxChars(int chars) {
    maxChars=chars;
  }
  public int getmaxChars() {
    return maxChars;
  }


  private void jbInit() throws Exception {
    this.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent e) {
        this_propertyChange(e);
      }
    });
    
    this.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        this_focusLost(e);
      }
      public void focusGained(FocusEvent e) {
        this_focusGained(e);
      }
    });
    this.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyTyped(KeyEvent e) {
        this_keyTyped(e);
      }
      public void keyReleased(KeyEvent e) {
        this_keyReleased(e);
      }
      public void keyPressed(KeyEvent e) {
        this_keyPressed(e);
      }
    });
    
    this.addMouseListener(new java.awt.event.MouseListener() {
      public void mousePressed(MouseEvent e) {
      }
      public void mouseReleased(MouseEvent e) {
      }
      public void mouseEntered(MouseEvent e) {
      }      
      public void mouseExited(MouseEvent e) {
      }      
      public void mouseClicked(MouseEvent e) {
        this_focusGained(null);
      }
    });
    
  }

  void this_keyTyped(KeyEvent e) {
    //controllo che non vada fuori max chars
    if (e.getKeyCode() != 8 &&
        e.getKeyCode() != 37 &&
        e.getKeyCode() != 39 &&
        e.getKeyCode() != 127) {
      if (maxChars > 0 && this.getText().length() >= maxChars) {
        //this.setText(this.getText().substring(0,maxChars));
        e.setKeyCode(0);
      }
    }

    //debug
    //javax.swing.JOptionPane.showMessageDialog(null,"text field keyTyped");
    dbModificato=true;
    tnxDbPanel temp = (tnxDbPanel)this.getParent();
    boolean res = temp.dbCheckModificati();
  }

  void this_focusLost(FocusEvent e) {
    try {
      //avvaloro ultimo campo usato in panel
      tnxDbPanel temp = (tnxDbPanel)this.getParent();
      temp.ultimoCampo = this.getDbNomeCampo();
      temp.ultimoValore = this.getText();
    } catch (Exception err) {
      err.printStackTrace();
    }    
  }

  void this_focusGained(FocusEvent e) {
    //debug
    //System.out.println("focus gain:" + this.getSelectionStart());
    //select all text        
    try {
      //if (e != null) {
        this.setSelectionStart(0);
        this.setSelectionEnd(this.getText().length());
      //}
    } catch (Exception err) {
      err.printStackTrace();
    }    
  }

  void this_keyReleased(KeyEvent e) {
    if (this.getDbComboAbbinata() != null) {
      //debug
      //javax.swing.JOptionPane.showMessageDialog(null,String.valueOf(this.getText()));
      this.getDbComboAbbinata().dbTrovaKey(String.valueOf(this.getText()));
    }
  }

  void this_propertyChange(PropertyChangeEvent e) {
    //debug
    //javax.swing.JOptionPane.showMessageDialog(null,e.getPropertyName());
  }

  void this_keyPressed(KeyEvent e) {
    dbModificato=true;
    tnxDbPanel temp = (tnxDbPanel)this.getParent();
    boolean res = temp.dbCheckModificati();    
  }

  String getTextNumber() {
    java.text.NumberFormat form = java.text.NumberFormat.getInstance();
    return (form.format(this.getText()));
  }
}
