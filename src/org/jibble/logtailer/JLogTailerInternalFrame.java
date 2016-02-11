/* 
Copyright Paul James Mutton, 2001-2004, http://www.jibble.org/

This file is part of JLogTailer.

This software is dual-licensed, allowing you to choose between the GNU
General Public License (GPL) and the www.jibble.org Commercial License.
Since the GPL may be too restrictive for use in a proprietary application,
a commercial license is also provided. Full license information can be
found at http://www.jibble.org/licenses/

$Author: pjm2 $
$Id: JLogTailerInternalFrame.java,v 1.2 2004/02/01 13:21:17 pjm2 Exp $

*/

package org.jibble.logtailer;

import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.regex.*;

/**
 * JLogTailer - A log tailer utility written in Java.
 * Copyright Paul James Mutton, 2002.
 * 
 * @author Paul James Mutton, http://www.jibble.org/
 * @version 2.0
 */
public class JLogTailerInternalFrame extends JInternalFrame implements Runnable, Serializable {
    
    public JLogTailerInternalFrame() {
        
    }
    
    public JLogTailerInternalFrame(JFrame owner, File file, Rectangle bounds) throws IOException {
        _owner = owner;
        _file = file;
        
        Container pane = this.getContentPane();
        pane.add(_asta, BorderLayout.CENTER);
        this.setResizable(true);
        this.setClosable(true);
        this.setMaximizable(true);
        this.setIconifiable(true);
        this.setBounds(bounds);
        this.setTitle(file.getName());
        this.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
        
        // Detects when the window is closed.
        this.addInternalFrameListener(new InternalFrameAdapter() {
            public void internalFrameClosing(InternalFrameEvent ife) {
                _running = false;
                dispose();
            }
        });
        
        // Set up the menu bar
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        JMenu highlightingMenu = new JMenu("Highlighting");
        menuBar.add(highlightingMenu);
        
        JMenuItem fileCloseItem = new JMenuItem("Close");
        fileMenu.add(fileCloseItem);
        JMenuItem highlightingOptionsItem = new JMenuItem("Highlighting options");
        highlightingMenu.add(highlightingOptionsItem);
        
        this.setJMenuBar(menuBar);
        
        fileCloseItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                _running = false;
                dispose();
            }
        });

        highlightingOptionsItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                new SettingsDialog(_owner, JLogTailerInternalFrame.this);
            }
        });
        
        // Do not allow tail logging of non-existant files. (Is this a good idea?)
        if (!file.exists() || file.isDirectory() || !file.canRead()) {
            throw new IOException("Can't read this file.");
        }
        
        _filePointer = _file.length();
        
        this.appendMessage("Log tailing started on " + _file.toString());
        this.setVisible(true);
    }
    
    // This is the method that contains all the actual log tailing stuff.
    // Note: I'm not particularly happy about the use of the readLine()
    // method call, as it may return a partial line if it reaches the
    // end of the file.  It might be worth jibbling about with this at
    // a later date so that a different approach is used.
    public void run() {
        try {
            while (_running) {
                Thread.sleep(_updateInterval);
                long len = _file.length();
                if (len < _filePointer) {
                    // Log must have been jibbled or deleted.
                    this.appendMessage("Log file was reset. Restarting logging from start of file.");
                    _filePointer = len;
                }
                else if (len > _filePointer) {
                    // File must have had something added to it!
                    RandomAccessFile raf = new RandomAccessFile(_file, "r");
                    raf.seek(_filePointer);
                    String line = null;
                    while ((line = raf.readLine()) != null) {
                        this.appendLine(line);
                    }
                    _filePointer = raf.getFilePointer();
                    raf.close();
                }
            }
        }
        catch (Exception e) {
            this.appendMessage("Fatal error reading log file, log tailing has stopped.");
        }
        // dispose();
    }
    
    public void appendLine(String line) {
        try {
            
            HighlightRule rule = _defaultRule;
            // Synchronize on the rule list so that nothing can be added to it
            // while we go through it (going through it with a for loop is
            // actually quicker than using an iterator on any other kind of List.
            synchronized (_rules) {
                for (int i = 0; i < _rules.size(); i++) {
                    HighlightRule candidate = (HighlightRule)_rules.get(i);
                    Pattern pattern = candidate.getPattern();
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        rule = candidate;
                        break;
                    }
                }
            }
            
            if (rule.getBeep()) {
                // We should beep when this line is seen.
                this.getToolkit().beep();
            }
            
            if (rule.getFiltered()) {
                // We're not actually going to show filtered lines...
                return;
            }

            JTextPane textPane = _asta.getTextPane();
            Document document = _asta.getDocument();
            SimpleAttributeSet attr = _asta.getSimpleAttributeSet();
            
            rule.alterAttributeSet(attr);            
            _asta.append(line + "\n");
            
            textPane.setDocument(document);
            if (++_linesShown > _maxLines) {
                // We must remove a line!
                int len = textPane.getText().indexOf('\n');
                document.remove(0, len);
                _linesShown--;
            }
        }
        catch (BadLocationException e) {
            // But this'll never happen, right?
            throw new RuntimeException("Tried to add a new line to a bad place.");
        }
    }
    
    public void appendMessage(String message) {
        SimpleAttributeSet attr = _asta.getSimpleAttributeSet();
        StyleConstants.setForeground(attr, Color.red);
        this.appendLine("[" + new Date().toString() + ", " + message + "]");
        StyleConstants.setForeground(attr, Color.black);
    }
    
    public String getFilename() {
        return _file.toString();
    }
    
    public ArrayList getRules() {
        return _rules;
    }
    
    public File getFile() {
        return _file;
    }
    
    // Maximum number of lines that we shall display before removing earlier ones.
    private int _maxLines = 500;
    private int _linesShown = 0;
    
    private boolean _running = true;
    private int _updateInterval = 1000;
    private File _file;
    private long _filePointer;
    private AutoScrollTextArea _asta = new AutoScrollTextArea();
    
    private ArrayList _rules = new ArrayList();
    private HighlightRule _defaultRule = new HighlightRule();
    
    private JFrame _owner;
    
}