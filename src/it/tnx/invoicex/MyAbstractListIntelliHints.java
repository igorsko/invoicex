/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.tnx.invoicex;

import com.jidesoft.hints.AbstractListIntelliHints;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.text.JTextComponent;


public class MyAbstractListIntelliHints extends AbstractListIntelliHints {

    public MyAbstractListIntelliHints(JTextComponent textComponent) {
        super(textComponent);
    }
    
    public boolean updateHints(Object context) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private Object selezionato;

    public Object getSelezionato() {
        return selezionato;
    }

    public void setSelezionato(Object selezionato) {
        propertyChangeSupport.firePropertyChange("selezionato", this.selezionato, selezionato);
        this.selezionato = selezionato;
    }
    private transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
    
}
