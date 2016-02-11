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
package mjpf;

/**
 *
 * @author Andrea Sindico and AUCOM S.R.L.
 */
import gestioneFatture.main;
import it.tnx.commons.DebugUtils;
import it.tnx.commons.SwingUtils;
import it.tnx.invoicex.Plugin;
import java.net.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;
import org.xml.sax.SAXException;

/**
 *The Core Class o mjpf
 */
public class PluginFactory2 {

    private Hashtable plugins = null;
    private URLClassLoader classloader = null;
    private Vector idNotAvailable = new Vector();

    public PluginFactory2(String pluginsPath) {
        this.loadPlugins(pluginsPath);
    }

    public PluginFactory2() {
    }

    /**
     *it takes as parameter an id representing a pluginEntryId
     *and returns an instance of that PluginEntry
     *
     */
    public PluginEntry getPluginEntry(Integer id) {
        if (plugins == null) {
            return null;
        }
        EntryDescriptor2 pd = (EntryDescriptor2) plugins.get(id);
        Object obj = null;
        try {
//            System.out.println(pd.getMain());
            Object o = classloader.loadClass(pd.getMain()).newInstance();
            obj = (mjpf.PluginEntry) o;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (PluginEntry) obj;
    }

    /**
     *Returns a Collection of all EntryDescriptor objects
     *one for each entry point detected
     */
    public Collection getAllEntryDescriptor() {
        if (plugins == null) {
            return null;
        }
        return plugins.values();
    }

    /**
     *Returns a Vector Object containing EntryDescriptor objects of
     *entry points that have the specified name
     */
    public Vector getEntryDescriptorsByName(String name) {
        Iterator pluginsIterator = plugins.values().iterator();
        Vector pluginsbyname = new Vector();
        while (pluginsIterator.hasNext()) {
            EntryDescriptor2 pd = (EntryDescriptor2) pluginsIterator.next();
            if (pd.getName().equals(name)) {
                pluginsbyname.add(pd);
            }
        }
        return pluginsbyname;
    }

    private EntryDescriptor2 loadPlugin(String pluginsPath, String jar_i) throws IOException, ParserConfigurationException, SAXException {
        EntryDescriptor2 pd = null;

        JarFile jarFile = new JarFile(pluginsPath + jar_i);
//        System.out.println(jar_i);
        ZipEntry xmldescriptor = this.getPluginPathFileDescriptor(pluginsPath + jar_i);
        if (xmldescriptor != null) {
            InputStream is = jarFile.getInputStream(xmldescriptor);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(is);
            try {
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                jarFile.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            NodeList plugs = null;
            try {
                plugs = doc.getElementsByTagName("entry");
            } catch (Exception e) {
                System.out.println("    Warning: There are not entry tags in descriptor.xml!");

            }
            if (plugs.getLength() == 0) {
                System.out.println("    Warning: There are not entry tags in descriptor.xml!");
            }
//            System.out.println("descriptor.xml obtained");

            for (int j = 0; j < plugs.getLength(); j++) {
//                System.out.println("    EntryPoint " + (j + 1));
                Element entrypoint = (Element) plugs.item(j);
                pd = new EntryDescriptor2();
                Integer chiave = getNewEntryID();
                pd.setNomeFileJar(jar_i);
                pd.setId(chiave);
                try {
                    pd.setType(entrypoint.getElementsByTagName("type").item(0).getFirstChild().getNodeValue());
                } catch (Exception e) {
                    System.out.println("    Warning: type tag not present");
                    System.out.println("    It is not possibile to load this Entry Point");
                    break;
                }
                try {
                    pd.setName(entrypoint.getElementsByTagName("name").item(0).getFirstChild().getNodeValue());
                } catch (Exception e) {
                    System.out.println("    Warning: name tag not present");
                    System.out.println("    It is not possibile to load this Entry Point");
                    break;
                }
                try {
                    pd.setMain(entrypoint.getElementsByTagName("main").item(0).getFirstChild().getNodeValue());
                } catch (Exception e) {
                    System.out.println("    Warning: main tag not present");
                    System.out.println("    It is not possibile to load this Entry Point");
                    break;
                }
                try {
                    pd.setIcon(entrypoint.getElementsByTagName("icon").item(0).getFirstChild().getNodeValue());
                } catch (Exception e) {
                    System.out.println("    icon tag not present...");
                }
                try {
                    pd.setTips(entrypoint.getElementsByTagName("tips").item(0).getFirstChild().getNodeValue());
                } catch (Exception e) {
                    System.out.println("    tips tag not present...");
                }
                try {
                    pd.setVer(entrypoint.getElementsByTagName("ver").item(0).getFirstChild().getNodeValue());
                } catch (Exception e) {
//                    System.out.println("    ver tag not present...");
                }
                try {
                    pd.setData_string(entrypoint.getElementsByTagName("data").item(0).getFirstChild().getNodeValue());
                    SimpleDateFormat f1 = new SimpleDateFormat("dd/MM/yy");
                    pd.setData(f1.parse(entrypoint.getElementsByTagName("data").item(0).getFirstChild().getNodeValue()));
                } catch (Exception e) {
//                    System.out.println("    data tag not present...");
                }

//                System.out.println("    Type:" + pd.getType());
//                System.out.println("    Name:" + pd.getName());
//                System.out.println("    Main:" + pd.getMain());
//                System.out.println("    Icon:" + pd.getIcon());
//                System.out.println("    Tips:" + pd.getTips());
//                System.out.println("    Ver:" + pd.getVer());
//                System.out.println("    Data:" + pd.getData_string() + " " + pd.getData());
//                System.out.println("");
                plugins.put(chiave, pd);

            }
        } else {
            System.out.println("Warning: " + jar_i + " does not contain descriptor.xml ");
            System.out.println("it's impossible to load its entry points");
        }

        return pd;
    }

    /**
     *it loads an EntryDescriptor object for each entry point detected in
     *pluginspath end puts it in plugins hashmap using a pluginID
     */
    public EntryDescriptor2 loadPluginByFileName(String pluginsPath, String fileName) {
        try {
            String[] jar = new String[]{fileName};

            if (jar.length == 0) {
                return null;
            }
            if (plugins == null) {
                plugins = new Hashtable();
            }

            URL[] url = new URL[jar.length];
            for (int i = 0; i < jar.length; i++) {
                String pi = "file:" + pluginsPath + jar[i];
                url[i] = new URL(pi);
            }
            classloader = new URLClassLoader(url);

//            System.out.println("Plugins(jar) detected: " + jar.length + " url[]:" + DebugUtils.dumpAsString(url));
            return loadPlugin(pluginsPath, jar[0]);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     *it loads an EntryDescriptor object for each entry point detected in
     *pluginspath end puts it in plugins hashmap using a pluginID
     */
    public void loadPlugins(String pluginsPath) {
        try {
            String[] jar = (new File(pluginsPath)).list(new pluginFileFilter());
            if (jar.length == 0) {
                return;
            }
            if (plugins == null) {
                plugins = new Hashtable();
            }

//            URL[] url = new URL[jar.length];
            URL[] url = new URL[jar.length + 1];
            for (int i = 0; i < jar.length; i++) {
                String pi = "file:" + pluginsPath + jar[i];
                url[i] = new URL(pi);
            }

            url[jar.length] = new URL("file:plugins/lib/InvoicexPluginUtil.jar");
            classloader = new URLClassLoader(url);
//            System.out.println("Plugins(jar) detected: " + jar.length + " url[]:" + DebugUtils.dumpAsString(url));
            for (int i = 0; i < jar.length; i++) {
                try {
                    loadPlugin(pluginsPath, jar[i]);
                } catch (Exception ex) {
                    ex.printStackTrace();

                    //errore nel caricamento, riscarico e ritento
//                    System.out.println("");
//                    System.out.println("pluginsPath:" + pluginsPath + " jar[i]:" + jar[i]);
//                    System.out.println("");
                    File fplugin = new File(pluginsPath + jar[i]);
                    if (fplugin.delete()) {
                        //riscarico
                        Plugin po = new Plugin();
                        EntryDescriptor2 pd = new EntryDescriptor2();
                        pd.setNomeFileJar(jar[i]);
                        main.INSTANCE.scaricaAggiornamentoPlugin(po, pd);
                        //riprovo a caricare
                        try {
                            loadPlugin(pluginsPath, jar[i]);
                        } catch (Exception e) {
                            System.out.println("!!! errore 2 su ricarca plugin");
                            e.printStackTrace();
                        }
                    } else {
                        SwingUtils.showErrorMessage(main.getPadre(), "Impossibile caricare il plugin " + jar[i]);
                    }
                }
            }
//            System.out.println("\nExecution Stack:");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *Returns the ZipEntry object of descriptor.xml
     *null if it does not exist.
     */
    private ZipEntry getPluginPathFileDescriptor(String fileName) {
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(fileName);

            for (Enumeration e = jarFile.entries(); e.hasMoreElements();) {
                JarEntry jarEntry = (JarEntry) e.nextElement();
                String file = jarEntry.getName();
                if (file.endsWith(("descriptor.xml"))) {
                    return jarFile.getEntry(file);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (jarFile != null) {
                try {
                    jarFile.close();
                } catch (IOException ioe) {
                }
            }
        }
        return null;
    }

    /**
     *Generates a unique PluginEntryId
     */
    private Integer getNewEntryID() {
        int id = idNotAvailable.size() + 1;
        Integer ris = new Integer(id);
        idNotAvailable.add(ris);
        return ris;
    }

    //this class implements a FileFilter that discards every not jar file
    class pluginFileFilter implements java.io.FilenameFilter {

        public boolean accept(File dir, String filename) {
            if (filename.endsWith(".jar")) {
                return true;
            }
            return false;
        }
    }
}
