/* 
Copyright Paul James Mutton, 2001-2004, http://www.jibble.org/

This file is part of JLogTailer.

This software is dual-licensed, allowing you to choose between the GNU
General Public License (GPL) and the www.jibble.org Commercial License.
Since the GPL may be too restrictive for use in a proprietary application,
a commercial license is also provided. Full license information can be
found at http://www.jibble.org/licenses/

$Author: pjm2 $
$Id: AutoScrollTextArea.java,v 1.2 2004/02/01 13:21:17 pjm2 Exp $

*/

package org.jibble.logtailer;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

/**
 * JLogTailer - A log tailer utility written in Java.
 * Copyright Paul James Mutton, 2002.
 * 
 * @author Paul James Mutton, http://www.jibble.org/
 * @version 2.0
 */
public class AutoScrollTextArea extends JScrollPane {
    
    public AutoScrollTextArea() {
        super();
        _textPane.setEditable(false);
        _textPane.setFont(new Font("Monospaced", Font.PLAIN, 12));
        this.setViewportView(_textPane);
    }
    
    private void scrollToBottom() {
        _textPane.setCaretPosition(_textPane.getDocument().getLength());
    }
    
    public JTextPane getTextPane() {
        return _textPane;
    }
    
    public void append(String str) throws BadLocationException {
        _textPane.getDocument().insertString(_textPane.getDocument().getLength(), str, _attributeSet);
        scrollToBottom();
    }
    
    public Document getDocument() {
        return _textPane.getDocument();
    }
    
    public SimpleAttributeSet getSimpleAttributeSet() {
        return _attributeSet;
    }
    
    private JTextPane _textPane = new JTextPane(new DefaultStyledDocument());
    private SimpleAttributeSet _attributeSet = new SimpleAttributeSet();
}