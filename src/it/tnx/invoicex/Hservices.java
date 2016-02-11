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

package it.tnx.invoicex;

import it.tnx.invoicex.data.DatiAzienda;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author mceccarelli
 */
public interface Hservices {
    public List<Plugin> getPlugins();
    public List<Plugin> getPlugins2(boolean solopubblici);
    public List<Plugin> getPlugins3(boolean solopubblici, boolean pluginInvoicex);
    public boolean checkPlugin(String plugin, DatiAzienda dati);
    public boolean checkPlugin2(String plugin, DatiAzienda dati, Date data_uc, String versione);
    public Map checkPlugin3(String plugin, DatiAzienda dati, Date data_uc, String versione);
    public List<Backup> getBackups(DatiAzienda dati);
    public boolean checkAggiornamenti(Versione v);
    public Versione getLastVersion();
    //public boolean checkAggiornamentiPlugins(Versione v);
}