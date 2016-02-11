/* 
Copyright Paul James Mutton, 2001-2004, http://www.jibble.org/

This file is part of JLogTailer.

This software is dual-licensed, allowing you to choose between the GNU
General Public License (GPL) and the www.jibble.org Commercial License.
Since the GPL may be too restrictive for use in a proprietary application,
a commercial license is also provided. Full license information can be
found at http://www.jibble.org/licenses/

$Author: pjm2 $
$Id: JLogTailerFrame.java,v 1.2 2004/02/01 13:21:17 pjm2 Exp $

*/

package org.jibble.logtailer;

import java.io.*;
import java.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * JLogTailer - A log tailer utility written in Java.
 * Copyright Paul James Mutton, 2002.
 * 
 * @author Paul James Mutton, http://www.jibble.org/
 * @version 2.0
 */
public class JLogTailerFrame extends JFrame implements Serializable {
    
    public static final File SETTINGS_FILE = new File(System.getProperties().getProperty("user.home"), "JLogTailer2.xml");
    
    public JLogTailerFrame(String title, int width, int height) {
        this.setTitle(title);
        this.setSize(width, height);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        Container pane = this.getContentPane();
        pane.add(_desktop, BorderLayout.CENTER);
        
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        JMenu windowMenu = new JMenu("Window");
        menuBar.add(windowMenu);
        JMenu helpMenu = new JMenu("Help");
        menuBar.add(helpMenu);
        
        JMenuItem fileOpenItem = new JMenuItem("Open log");
        fileMenu.add(fileOpenItem);
        JMenuItem fileExitItem = new JMenuItem("Exit");
        fileMenu.add(fileExitItem);
        JMenuItem windowTileVerticallyItem = new JMenuItem("Tile vertically");
        windowMenu.add(windowTileVerticallyItem);
        JMenuItem windowTileBoxedItem = new JMenuItem("Tile boxed");
        windowMenu.add(windowTileBoxedItem);
        JMenuItem helpTipsItem = new JMenuItem("Tips");
        helpMenu.add(helpTipsItem);
        JMenuItem helpAboutItem = new JMenuItem("About");
        helpMenu.add(helpAboutItem);
        
        this.setJMenuBar(menuBar);

        // Allows the window to be closed.
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                exit();
            }
        });
        
        // Adds a new log tailing internal frame to the desktop.
        fileOpenItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                JFileChooser chooser = new JFileChooser(_currentDir);
                int returnVal = chooser.showOpenDialog(JLogTailerFrame.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = new File(chooser.getCurrentDirectory(), chooser.getSelectedFile().getName());
                    try {
                        startLogging(file, null, new Rectangle(600, 400));
                        _currentDir = file.getParentFile();
                    }
                    catch (IOException e) {
                        JOptionPane.showMessageDialog(JLogTailerFrame.this, file.toString() + " cannot be tail logged.", "Logging not started", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });
        
        // Exits the application.
        fileExitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                exit();
            }
        });
        
        windowTileVerticallyItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                tileInternalFramesVertically();
            }
        });
        
        windowTileBoxedItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                tileInternalFramesBoxed();
            }
        });        
        
        // Displays "about" information.
        helpAboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                JOptionPane.showMessageDialog(JLogTailerFrame.this, "JLogTailer 2.0.0\nA Java log tailer by Paul Mutton\nhttp://www.jibble.org/", "About", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        // Displays "about" information.
        helpTipsItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                JOptionPane.showMessageDialog(JLogTailerFrame.this, "When you start JLogTailer, it will remember which files you were last looking at.\nLine highlighting rules are also remembered between sessions.\nCheck out the highlighting options to make certain lines coloured, bold, underlined, etc.\nAll configuration is saved in your home directory as JLogTailer2.xml\nRead the javadoc documentation for the Pattern class for help with regular expressions.\nDon't forget that you can order lists of multiple rules for any file.", "Tips", JOptionPane.INFORMATION_MESSAGE);
            }
        });        
        
        this.readConfig();
        
        this.setVisible(true);
        
    }
    
    public void startLogging(File file, ArrayList rules, Rectangle bounds) throws IOException {
        JLogTailerInternalFrame iFrame = new JLogTailerInternalFrame(JLogTailerFrame.this, file, bounds);
        if (rules != null) {
            ArrayList logRules = iFrame.getRules();
            synchronized (logRules) {
                logRules.addAll(rules);
            }
        }
        _desktop.add(iFrame);
        iFrame.moveToFront();
        Thread t = new Thread(iFrame);
        t.start();
    }
    
    private void readConfig() {
        try {
            System.out.println("Trying to read previous configuration...");
            java.beans.XMLDecoder decoder = new java.beans.XMLDecoder(new BufferedInputStream(new FileInputStream(SETTINGS_FILE)));
            
            this.setBounds((Rectangle)decoder.readObject());
            
            int frameCount = ((Integer)decoder.readObject()).intValue();
            for (int i = 0; i < frameCount; i++) {
                String path = (String)decoder.readObject();
                File file = new File(path);
                Rectangle bounds = (Rectangle)decoder.readObject();
                
                int ruleCount = ((Integer)decoder.readObject()).intValue();
                ArrayList rules = new ArrayList();
                for (int j = 0; j < ruleCount; j++) {
                    String name = (String)decoder.readObject();
                    String regexp = (String)decoder.readObject();
                    boolean underlined = ((Boolean)decoder.readObject()).booleanValue();
                    boolean bold = ((Boolean)decoder.readObject()).booleanValue();
                    boolean filtered = ((Boolean)decoder.readObject()).booleanValue();
                    boolean beep = ((Boolean)decoder.readObject()).booleanValue();
                    Color color = (Color)decoder.readObject();
                    HighlightRule rule = new HighlightRule(name, regexp, underlined, bold, filtered, beep, color);
                    rules.add(rule);
                }
//                startLogging(file, rules, bounds);
            }
            
            decoder.close();
        }
        catch (Exception e) {
            System.out.println("Could not find previous configuration: assuming defaults.");
        }
    }
    
    private void exit() {
        this.setVisible(false);
        
        // Save our configuration!
        try {
            java.beans.XMLEncoder encoder = new java.beans.XMLEncoder(new BufferedOutputStream(new FileOutputStream(SETTINGS_FILE)));
            encoder.writeObject(this.getBounds());
            JInternalFrame[] frames = _desktop.getAllFrames();
            int frameCount = frames.length;
            encoder.writeObject(new Integer(frameCount));
            for (int n = 0; n < frameCount; n++) {
                JLogTailerInternalFrame frame = (JLogTailerInternalFrame)frames[n];
                encoder.writeObject(frame.getFile().getPath());
                encoder.writeObject(frame.getBounds());
                
                ArrayList rules = frame.getRules();
                synchronized (rules) {
                    encoder.writeObject(new Integer(rules.size()));
                    for (int i = 0; i < rules.size(); i++) {
                        HighlightRule rule = (HighlightRule)rules.get(i);
                        encoder.writeObject(rule.getName());
                        encoder.writeObject(rule.getRegexp());
                        encoder.writeObject(new Boolean(rule.getUnderlined()));
                        encoder.writeObject(new Boolean(rule.getBold()));
                        encoder.writeObject(new Boolean(rule.getFiltered()));
                        encoder.writeObject(new Boolean(rule.getBeep()));
                        encoder.writeObject(rule.getColor());
                    }
                }
            }
            encoder.flush();
            encoder.close();
            System.out.println("Configuration saved.");
        }
        catch (Exception e) {
            System.out.println("Unable to save configuration for next use: " + e);
        }
        
        // No need to explictly tidy anything else up as we're only reading files.
//        System.exit(0);
    }
    
    private void tileInternalFramesVertically() {
        int desktopWidth = _desktop.getWidth();
        int desktopHeight = _desktop.getHeight();
        JInternalFrame[] frames = _desktop.getAllFrames();
        int frameCount = frames.length;
        for (int n = 0; n < frameCount; n++) {
            JInternalFrame frame = frames[n];
            try {
                frame.setIcon(false);
            }
            catch (java.beans.PropertyVetoException e) {
                // Carry on...
            }
            frame.reshape(0, (n*desktopHeight)/frameCount, desktopWidth, desktopHeight/frameCount);
            frame.setVisible(true);
            frame.toFront();
        }
    }
    
    private void tileInternalFramesBoxed() {
        int desktopWidth = _desktop.getWidth();
        int desktopHeight = _desktop.getHeight();
        JInternalFrame[] frames = _desktop.getAllFrames();
        int frameCount = frames.length;
        int totalRows = (int)Math.sqrt((double)frameCount);
        int totalCols = 1;
        while (totalCols * totalRows < frameCount) {
            totalCols++;
        }
        int windowsDrawn = 0;
        for (int row = 0; row < totalRows; row++) {
            for (int col = 0; col < totalCols; col++) {
                if (windowsDrawn == frameCount) {
                    break;
                }
                JInternalFrame frame = frames[row*totalCols + col];
                try {
                    frame.setIcon(false);
                }
                catch (java.beans.PropertyVetoException e) {
                    // Carry on...
                }
                frame.reshape((col*desktopWidth)/totalCols, (row*desktopHeight)/totalRows, desktopWidth/totalCols, desktopHeight/totalRows);
                frame.setVisible(true);
                frame.toFront();
                windowsDrawn++;
            }
        }
    }
    
    private transient JDesktopPane _desktop = new JDesktopPane();
    private File _currentDir = null;
    
}