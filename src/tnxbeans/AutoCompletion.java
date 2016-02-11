package tnxbeans;

import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.text.*;

/* This work is hereby released into the Public Domain.
 * To view a copy of the public domain dedication, visit
 * http://creativecommons.org/licenses/publicdomain/
 */
public class AutoCompletion extends PlainDocument {

    JComboBox comboBox;
    ComboBoxModel model;
    JTextComponent editor;
    // flag to indicate if setSelectedItem has been called
    // subsequent calls to remove/insertString should be ignored
    public boolean selecting = false;
    public boolean tnxbeansrefresh = false;
    boolean hidePopupOnFocusLoss;
    boolean hitBackspace = false;
    boolean hitBackspaceOnSelection;
    KeyListener editorKeyListener;
    FocusListener editorFocusListener;

    public AutoCompletion(final JComboBox comboBox) {
        this.comboBox = comboBox;
        model = comboBox.getModel();
        comboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!selecting) {
                    highlightCompletedText(0);
                }
            }
        });
        comboBox.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName().equals("editor")) {
                    configureEditor((ComboBoxEditor) e.getNewValue());
                }
                if (e.getPropertyName().equals("model")) {
                    model = (ComboBoxModel) e.getNewValue();
                }
            }
        });
        editorKeyListener = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() != KeyEvent.VK_ENTER) {
                    if (comboBox.isDisplayable()) {
                        comboBox.setPopupVisible(true);
                    }
                }
                hitBackspace = false;
                switch (e.getKeyCode()) {
                    // determine if the pressed key is backspace (needed by the remove method)
                    case KeyEvent.VK_BACK_SPACE:
                        hitBackspace = true;
                        hitBackspaceOnSelection = editor.getSelectionStart() != editor.getSelectionEnd();
                        break;
                    // ignore delete key
                    case KeyEvent.VK_DELETE:
                        e.consume();
                        comboBox.getToolkit().beep();
                        break;
                }
            }
        };
        // Bug 5100422 on Java 1.5: Editable JComboBox won't hide popup when tabbing out
        hidePopupOnFocusLoss = System.getProperty("java.version").startsWith("1.5");
        // Highlight whole text when gaining focus
        editorFocusListener = new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                highlightCompletedText(0);
            }
            public void focusLost(FocusEvent e) {
                // Workaround for Bug 5100422 - Hide Popup on focus loss
                if (hidePopupOnFocusLoss) {
                    comboBox.setPopupVisible(false);
                }
            }
        };
        configureEditor(comboBox.getEditor());
        // Handle initially selected object
        Object selected = comboBox.getSelectedItem();
        if (selected != null) {
            setText(selected.toString());
        }
        highlightCompletedText(0);
    }

    public static void enable(JComboBox comboBox) {
        // has to be editable
        comboBox.setEditable(true);
        // change the editor's document
        new AutoCompletion(comboBox);
    }

    static void disable(tnxComboField comboBox) {
        for (ActionListener l : comboBox.getActionListeners()) {
            if (l.getClass().getName().equals("tnxbeans.AutoCompletion$1")) {
                comboBox.removeActionListener(l);
            }
        }
        for (PropertyChangeListener l : comboBox.getPropertyChangeListeners()) {
            if (l.getClass().getName().equals("tnxbeans.AutoCompletion$2")) {
                comboBox.removePropertyChangeListener(l);
            }
        }
        try {
            JTextComponent text = (JTextComponent)comboBox.getEditor().getEditorComponent();
            for (KeyListener l : text.getKeyListeners()) {
                if (l.getClass().getName().equals("tnxbeans.AutoCompletion$3")) {
                    text.removeKeyListener(l);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            JTextComponent text = (JTextComponent)comboBox.getEditor().getEditorComponent();
            for (FocusListener l : text.getFocusListeners()) {
                if (l.getClass().getName().equals("tnxbeans.AutoCompletion$4")) {
                    text.removeFocusListener(l);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            JTextComponent text = (JTextComponent)comboBox.getEditor().getEditorComponent();
            text.setDocument(new PlainDocument());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void configureEditor(ComboBoxEditor newEditor) {
        if (editor != null) {
            editor.removeKeyListener(editorKeyListener);
            editor.removeFocusListener(editorFocusListener);
        }

        if (newEditor != null) {
            editor = (JTextComponent) newEditor.getEditorComponent();
            editor.addKeyListener(editorKeyListener);
            editor.addFocusListener(editorFocusListener);
            editor.setDocument(this);
        }
    }

    public void remove(int offs, int len) throws BadLocationException {
        // return immediately when selecting an item
        if (selecting) {
            return;
        }

//        if (hitBackspace) {
//            // user hit backspace => move the selection backwards
//            // old item keeps being selected
//            if (offs>0) {
//                if (hitBackspaceOnSelection) offs--;
//            } else {
//                // User hit backspace with the cursor positioned on the start => beep
//                comboBox.getToolkit().beep(); // when available use: UIManager.getLookAndFeel().provideErrorFeedback(comboBox);
//            }
//            highlightCompletedText(offs);
//        } else {
//            super.remove(offs, len);
//        }

        super.remove(offs, len);
    }

    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        // return immediately when selecting an item
        if (selecting) {
            return;
        }
        // insert the string into the document
        super.insertString(offs, str, a);
        // lookup and select a matching item
        Object item = null;
        if (!tnxbeansrefresh) {
            item = lookupItem(getText(0, getLength()));
        }
        System.out.println("item:" + item);
        if (item != null) {
            if (!tnxbeansrefresh) {
                setSelectedItem(item);
            }
            setText(item.toString());
            // select the completed part
            highlightCompletedText(offs + str.length());
        } else {
            // keep old item selected if there is no match
//            item = comboBox.getSelectedItem();
            // imitate no insert (later on offs will be incremented by str.length(): selection won't move forward)
//            offs = offs-str.length();
            // provide feedback to the user that his input has been received but can not be accepted
//            comboBox.getToolkit().beep(); // when available use: UIManager.getLookAndFeel().provideErrorFeedback(comboBox);

            if (!tnxbeansrefresh) {
                setSelectedItem(null);
            }
            comboBox.hidePopup();
//            System.out.println("comboBox.getComponent(0):" + comboBox.getComponent(0));
//            System.out.println("comboBox.getComponent(1):" + comboBox.getComponent(1));
//            System.out.println("comboBox.getComponent(2):" + comboBox.getComponent(2));

            //setText(str);
            //highlightCompletedText(getLength());
        }

    }

    private void setText(String text) {
        try {
            // remove all text and insert the completed string
            super.remove(0, getLength());
            super.insertString(0, text, null);
        } catch (BadLocationException e) {
            throw new RuntimeException(e.toString());
        }
    }

    private void highlightCompletedText(int start) {
        try {
            editor.setCaretPosition(getLength());
            editor.moveCaretPosition(start);
        } catch (Exception ex) {
            System.out.println("highlightCompletedText start:" + start + " length:" + getLength() + " text:" + editor.getText());
            System.out.println(getClass() + " " + ex);
        }
    }

    private void setSelectedItem(Object item) {
        selecting = true;
        model.setSelectedItem(item);
        selecting = false;
    }

    private Object lookupItem(String pattern) {
        Object selectedItem = model.getSelectedItem();
        // only search for a different item if the currently selected does not match
        if (selectedItem != null && startsWithIgnoreCase(selectedItem.toString(), pattern)) {
            return selectedItem;
        } else {
            // iterate over all items
            for (int i = 0, n = model.getSize(); i < n; i++) {
                Object currentItem = model.getElementAt(i);
                // current item starts with the pattern?
                if (currentItem != null && startsWithIgnoreCase(currentItem.toString(), pattern)) {
                    return currentItem;
                }
            }
        }
        // no item starts with the pattern => return null
        return null;
    }

    // checks if str1 starts with str2 - ignores case
    private boolean startsWithIgnoreCase(String str1, String str2) {
        return str1.toUpperCase().startsWith(str2.toUpperCase());
    }

    private static void createAndShowGUI() {
        // the combo box (add/modify items if you like to)
        Object[] rows = new Object[100000];
        for (int i = 0; i < 10000; i++) {
            rows[i] = "abc" + i;
        }
        //final JComboBox comboBox = new JComboBox(new Object[] {"Ester", "Jordi", "Jordina", "Jorge", "Sergi"});
        final JComboBox comboBox = new JComboBox(rows);
        enable(comboBox);

        // create and show a window containing the combo box
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(3);
        frame.getContentPane().add(comboBox);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                createAndShowGUI();
            }
        });
    }
}
