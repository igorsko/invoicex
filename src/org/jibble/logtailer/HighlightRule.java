/* 
Copyright Paul James Mutton, 2001-2004, http://www.jibble.org/

This file is part of JLogTailer.

This software is dual-licensed, allowing you to choose between the GNU
General Public License (GPL) and the www.jibble.org Commercial License.
Since the GPL may be too restrictive for use in a proprietary application,
a commercial license is also provided. Full license information can be
found at http://www.jibble.org/licenses/

$Author: pjm2 $
$Id: HighlightRule.java,v 1.2 2004/02/01 13:21:17 pjm2 Exp $

*/

package org.jibble.logtailer;

import java.awt.Color;
import javax.swing.text.*;
import java.util.regex.*;

/**
 * JLogTailer - A log tailer utility written in Java.
 * Copyright Paul James Mutton, 2002.
 * 
 * @author Paul James Mutton, http://www.jibble.org/
 * @version 2.0
 */
public class HighlightRule implements java.io.Serializable {
    
    public HighlightRule() {
        this("Default", "^.*$", false, false, false, false, Color.black);
    }
    
    public HighlightRule(String name, String regexp, boolean underlined, boolean bold, boolean filtered, boolean beep, Color color) throws PatternSyntaxException {
        _name = name;
        _regexp = regexp;
        _pattern = Pattern.compile(regexp);
        _underlined = underlined;
        _bold = bold;
        _beep = beep;
        _filtered = filtered;
        _color = color;
    }
    
    public void alterAttributeSet(SimpleAttributeSet attr) {
        StyleConstants.setForeground(attr, _color);
        StyleConstants.setBold(attr, _bold);
        StyleConstants.setUnderline(attr, _underlined);
    }
    
    public String getName() {
        return _name;
    }
    
    public String toString() {
        return getName();
    }
    
    public String getRegexp() {
        return _regexp;
    }
    
    public Pattern getPattern() {
        return _pattern;
    }
    
    public boolean getFiltered() {
        return _filtered;
    }
    
    public boolean getBold() {
        return _bold;
    }
    
    public boolean getUnderlined() {
        return _underlined;
    }
    
    public boolean getBeep() {
        return _beep;
    }
    
    public Color getColor() {
        return _color;
    }

    private String _name;
    private String _regexp;
    private Pattern _pattern;
    private boolean _underlined;
    private boolean _bold;
    private boolean _filtered;
    private boolean _beep;
    private Color _color = null;
}