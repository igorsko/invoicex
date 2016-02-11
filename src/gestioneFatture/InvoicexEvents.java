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

import java.util.HashMap;
import java.util.Map;
import javax.swing.event.EventListenerList;

public class InvoicexEvents {

    protected EventListenerList listenerList = new EventListenerList();
    protected EventListenerList listenerList2 = new EventListenerList();
    protected EventListenerList listenerList3 = new EventListenerList();

    public void addInvoicexEventListener(InvoicexEventListener listener) {
        listenerList.add(InvoicexEventListener.class, listener);
    }

    public void removeMyEventListener(InvoicexEventListener listener) {
        listenerList.remove(InvoicexEventListener.class, listener);
    }

    public void addInvoicexEventListener2(InvoicexEventListener2 listener2) {
        listenerList2.add(InvoicexEventListener2.class, listener2);
    }

    public void removeMyEventListener2(InvoicexEventListener2 listener2) {
        listenerList2.remove(InvoicexEventListener2.class, listener2);
    }

    public void addInvoicexEventListener3(InvoicexEventListener3 listener3) {
        listenerList3.add(InvoicexEventListener3.class, listener3);
    }

    public void removeMyEventListener3(InvoicexEventListener3 listener3) {
        listenerList3.remove(InvoicexEventListener3.class, listener3);
    }

    public InvoicexEvents() {
    }

    public void fireInvoicexEvent(InvoicexEvent event) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == InvoicexEventListener.class) {
                ((InvoicexEventListener) listeners[i + 1]).event(event);
            }
        }
    }

    public Object fireInvoicexEventWResult(InvoicexEvent event) {
        Object[] listeners = listenerList2.getListenerList();
        Object ret = null;
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == InvoicexEventListener2.class) {
                ret = ((InvoicexEventListener2) listeners[i + 1]).eventWResult(event);
            }
        }
        return ret;
    }

    public void fireInvoicexEventExc(InvoicexEvent event) throws Exception {
        Object[] listeners = listenerList3.getListenerList();
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == InvoicexEventListener3.class) {
                ((InvoicexEventListener3) listeners[i + 1]).eventExc(event);
            }
        }
    }

    public Object fireInvoicexEventWResultExc(InvoicexEvent event) throws Exception {
        Object[] listeners = listenerList3.getListenerList();
        Object ret = null;
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == InvoicexEventListener3.class) {
                ret = ((InvoicexEventListener3) listeners[i + 1]).eventWResultExc(event);
            }
        }
        return ret;
    }    
    
    
    public void fireInvoicexEventMagazzino(Object _this, Object inizio_mysql) {
        Map map = new HashMap();
        map.put("classe", _this);
        map.put("inizio_mysql", inizio_mysql);
        main.events.fireInvoicexEvent(new InvoicexEvent(map, InvoicexEvent.TYPE_NUOVI_MOVIMENTI_MAGAZZINO));
    }
}