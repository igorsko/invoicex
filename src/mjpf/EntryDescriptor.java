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

import java.util.Date;

/**
 *This class is used to manage informations about a Plugin Entry Point. 
 */
public class EntryDescriptor {

    private Integer id = null;
    private String type = null;
    private String name = null;
    private String main = null;
    private String icon = null;
    private String tips = null;
    private String ver = null;
    private String data_string = null;
    private Date data = null;
    private String nomeFileJar = null;

    
    public void setId(Integer value) {
        this.id = value;
    }

    public void setType(String value) {
        this.type = value;
    }

    public void setName(String value) {
        this.name = value;
    }

    public void setMain(String value) {
        this.main = value;
    }

    public void setIcon(String value) {
        this.icon = value;
    }

    public void setTips(String value) {
        this.tips = value;
    }

    public Integer getId() {
        return this.id;
    }

    public String getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public String getMain() {
        return this.main;
    }

    public String getIcon() {
        return this.icon;
    }

    public String getTips() {
        return this.tips;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getData_string() {
        return data_string;
    }

    public void setData_string(String data_string) {
        this.data_string = data_string;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getNomeFileJar() {
        return nomeFileJar;
    }

    public void setNomeFileJar(String nomeFileJar) {
        this.nomeFileJar = nomeFileJar;
    }
}
