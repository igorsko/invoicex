/* mjpf - A lightweight and flexible java plugin framework
 * Copyright (C) April 2005, Andrea Sindico and AUCOM S.R.L.
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
/**
 *
 * @author Andrea Sindico and AUCOM S.R.L.
*/

package mjpf;
/**
 * This interface represents a Plugin entry point in mjpf <br>
 * 
*/
public interface PluginEntry
{
    /** 
     *this method must be used to configure a plugin entry point
     *with some pameters.
     *Tipically this parameters are objects of your application
     *that your plugin will use.
     */
    public void initPluginEntry(Object param);
    /**
     *this method must be used 
     *to define the sequence of actions that starts the plugin entry<br>
     */
    public void startPluginEntry();
    /**
     *this method must be used 
     *to define the sequence of actions that stops the plugin entry<br>
     */
    public void stopPluginEntry();
    /**
     *this method must be used 
     *to define the sequence of actions that pauses the plugin entry<br>
     */
    public void pausePluginEntry();
}
