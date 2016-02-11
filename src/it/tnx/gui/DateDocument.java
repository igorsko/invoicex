package it.tnx.gui;

import com.jidesoft.swing.SelectAllUtils;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;
import org.jdesktop.swingx.JXDatePicker;

public class DateDocument extends PlainDocument {

    private final JTextField textfield;

    /**
     * Creates a new instance of DateDocument
     */
    public DateDocument(JTextField textfield) {
        this.textfield = textfield;
    }

    // don't allow an insertion to exceed the max length
    public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
        //formato data = dd/mm/aa
        if (getLength() == 8 && getText(0, 8).equals("__/__/__")) {
            remove(0, 8);
            textfield.setForeground(UIManager.getColor("Label.foreground"));
        }
        if (getLength() + str.length() > 8) {
            //System.out.println("lunghezza:" + (getLength() + str.length()));
            Toolkit.getDefaultToolkit().beep();
        } else {
            if (str != null && str.length() > 0) {
                if (str.length() == 1) {
                    try {
                        int num = Integer.parseInt(str);
                    } catch (NumberFormatException numErr) {
                        //non numerico
                        //System.out.println("non numerica:" + numErr.toString() + " value:" + str);
                        Toolkit.getDefaultToolkit().beep();
                        return;
                    }
                    //returnif ((offset == 2 || offset == 5) && !getText(offset + 1, 1).equals("/")) {
                    if (offset == 1 || offset == 4) {
                        super.insertString(offset, str, a);
                        super.insertString(offset + 1, "/", a);
                    } else {
                        super.insertString(offset, str, a);
                    }
                } else {
                    super.insertString(offset, str, a);
                }
            }
        }
    }

    @Override
    protected void insertUpdate(DefaultDocumentEvent chng, AttributeSet attr) {
        super.insertUpdate(chng, attr);
    }

    @Override
    protected void removeUpdate(DefaultDocumentEvent chng) {
        super.removeUpdate(chng);
    }

    @Override
    public void remove(int offs, int len) throws BadLocationException {
        super.remove(offs, len);
        if (offs == 2 || offs == 5) {
            try {
                super.remove(offs - 1, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void replace(int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        super.replace(offset, length, text, attrs);
    }

    static public class MyHighlighter implements Highlighter.HighlightPainter {

        public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {
            String text = null;
            Rectangle rbounds = bounds.getBounds();
            text = c.getText();
            g.setColor(UIManager.getColor("Label.disabledForeground"));
            int remove = text.length();
            FontMetrics fm = g.getFontMetrics();
            int swidth = fm.stringWidth(text);
            g.drawString("__/__/__".substring(remove, 8), rbounds.x + swidth, rbounds.height);
        }
    }
    
    public static void installDateDocument(JTextField textfield) {
        try {
            if (textfield.getParent() instanceof JXDatePicker) {
                ((JXDatePicker)textfield.getParent()).setFormats("dd/MM/yy");
            }            
        } catch (Exception e) {
            e.printStackTrace();
        }
        textfield.setDocument(new DateDocument(textfield));
        SelectAllUtils.install(textfield);
        Highlighter.HighlightPainter hp = new MyHighlighter();
        try {
            textfield.getHighlighter().addHighlight(0, 0, hp);
        } catch (Exception e) {
        }
    }
}