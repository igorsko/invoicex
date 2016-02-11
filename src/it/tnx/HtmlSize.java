package it.tnx;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author mceccarelli
 */
public class HtmlSize {

    static JFrame frame = null;
    static JFrame frameJLabel = null;
    static JTextPane textPane;
    static JLabel label;

    public HtmlSize() {
    }

    static public int getHeight(final int width, final String html) throws BadLocationException, InterruptedException, InvocationTargetException {
        if (frame == null) {
            frame = new JFrame();
            frame.setSize(100, 100);

            textPane = new JTextPane();
            textPane.setContentType("text/html");
            textPane.setEditable(false);
            frame.getContentPane().add(textPane);
            frame.setVisible(true);
            frame.setVisible(false);
        }

//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
        textPane.setPreferredSize(new Dimension(width, 1));
        try {
            textPane.getDocument().insertString(0, html, null);
        } catch (BadLocationException ex) {
            Logger.getLogger(HtmlSize.class.getName()).log(Level.SEVERE, null, ex);
        }
        Dimension d = textPane.getPreferredSize();
        Rectangle r = null;
        try {
            r = textPane.modelToView(textPane.getDocument().getLength());
        } catch (BadLocationException ex) {
            Logger.getLogger(HtmlSize.class.getName()).log(Level.SEVERE, null, ex);
        }
        d.height = r.y + r.height;
//            }
//        });

        return d.height;
    }

    static public Dimension getSizeJLabel(final String html) throws BadLocationException, InterruptedException, InvocationTargetException {
        if (frameJLabel == null) {
            frameJLabel = new JFrame();
            frameJLabel.setSize(100, 100);

            label = new JLabel();
            
            frameJLabel.getContentPane().add(label);
//            frameJLabel.setVisible(true);
//            frameJLabel.setVisible(false);
        }

        label.setText(html);
        frameJLabel.pack();
        Dimension d = label.getPreferredSize();
        return d;
    }
}
