/* 
Copyright Paul James Mutton, 2001-2004, http://www.jibble.org/

This file is part of JLogTailer.

This software is dual-licensed, allowing you to choose between the GNU
General Public License (GPL) and the www.jibble.org Commercial License.
Since the GPL may be too restrictive for use in a proprietary application,
a commercial license is also provided. Full license information can be
found at http://www.jibble.org/licenses/

$Author: pjm2 $
$Id: JLogTailerMain.java,v 1.2 2004/02/01 13:21:17 pjm2 Exp $

*/

package org.jibble.logtailer;

/**
 * JLogTailer - A log tailer utility written in Java.
 * Copyright Paul James Mutton, 2002.
 * 
 * @author Paul James Mutton, http://www.jibble.org/
 * @version 2.0
 */
public class JLogTailerMain {
    
    public static void main(String[] args) {
        new JLogTailerFrame("JLogTailer", 640, 480);
    }
    
}