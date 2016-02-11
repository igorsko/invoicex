/*
 * JTextFieldDate.java
 *
 * Created on 2 settembre 2005, 9.20
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package it.tnx.gui;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.JTextField;

/**
 *
 * @author marco
 */
public class JTextFieldDate extends JTextField {
    
    /** Creates a new instance of JTextFieldDate */
    public JTextFieldDate() {
        super();
        // We must be non-opaque since we won't fill all pixels.
        // This will also stop the UI from filling our background.
        setOpaque(false);

        // Add an empty border around us to compensate for
        // the rounded corners.
        //setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));        
    }
    
    protected void paintComponent(Graphics g) {
        int width = getWidth();
        int height = getHeight();
        
        Color oldColor = g.getColor();
        // Paint a rounded rectangle in the background.
        g.setColor(getBackground());
        //g.fillRoundRect(0, 0, width, height, height, height);
        g.setColor(Color.WHITE);
        g.fillRect(2, 2, width - 2, height - 2);
        g.setColor(Color.LIGHT_GRAY);
        g.drawString("__/__/__", 3, getSize().height - 5);
        g.setColor(oldColor);

        // Now call the superclass behavior to paint the foreground.
        super.paintComponent(g);
        
    }
    
}
