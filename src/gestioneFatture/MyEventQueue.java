package gestioneFatture;

import it.tnx.commons.SwingUtils;
import it.tnx.dbeans.pdfPrint.PrintSimpleTable;
import it.tnx.invoicex.PlatformUtils;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;
import org.jdesktop.swingx.JXTreeTable;

/*
 * This source file is based on example by Kumar Santhosh 
 * http://www.jroller.com/santhosh/entry/implementing_undo_redo_in_right
 */
public class MyEventQueue extends EventQueue {

    protected void dispatchEvent(AWTEvent event) {
        try {
            super.dispatchEvent(event);    
        } catch (java.lang.ArrayIndexOutOfBoundsException ei1) {
            ei1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        Object compo = event.getSource();
//        if (compo instanceof Container) {
//            System.out.println("awt event " + event + " compo:" + compo.getClass());
//        }

//        if (event instanceof ComponentEvent) {
//            ComponentEvent cevent = (ComponentEvent) event;
//            if (cevent.getID() == ComponentEvent.COMPONENT_SHOWN) {
//                Component comp = cevent.getComponent();
//                System.out.println("comp:" + comp.getClass() + " " + comp);
//                if (comp instanceof JInternalFrame) {
//                    //tutti i textfield
//                    abilitaUndo((JInternalFrame) comp);
//                }
//            }
//        }

        if (!(event instanceof MouseEvent)) {
            return;
        }

        MouseEvent me = (MouseEvent) event;
        if (!me.isPopupTrigger()) {
            return;
        }
        Component comp = SwingUtilities.getDeepestComponentAt(me.getComponent(), me.getX(), me.getY());

        JPopupMenu menu_p = null;
        if (MenuSelectionManager.defaultManager().getSelectedPath().length > 0) {
//            System.out.println("MenuSelectionManager.defaultManager().getSelectedPath().length > 0");
            try {
//                System.out.println(MenuSelectionManager.defaultManager().getSelectedPath()[0]);
                Object menu = MenuSelectionManager.defaultManager().getSelectedPath()[0];
                if (menu instanceof JPopupMenu) {
                    menu_p = (JPopupMenu) menu;
                    comp = menu_p.getInvoker();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        if ((comp instanceof JTextComponent)) {
            JTextComponent tc = (JTextComponent) comp;
            JPopupMenu menu = new JPopupMenu();
            menu.add(new CutAction(tc));
            menu.add(new CopyAction(tc));
            menu.add(new PasteAction(tc));
            menu.add(new DeleteAction(tc));
            menu.addSeparator();
            menu.add(new SelectAllAction(tc));
            menu.addSeparator();
            menu.add(new UndoAction(tc));
            menu.add(new RedoAction(tc));
            Point pt = SwingUtilities.convertPoint(me.getComponent(), me.getPoint(), tc);
            menu.show(tc, pt.x, pt.y);
        }

        boolean aggiungoEsportaTab = false;
//        if (PlatformUtils.isMac()) {
//            if (comp instanceof JTable && (!(comp instanceof JXTreeTable))) {
//                aggiungoEsportaTab = true;
//            }
//        } else {
            if (comp instanceof JTable) {
                aggiungoEsportaTab = true;
            }
//        }
        if (aggiungoEsportaTab) {
            
            JTable table = (JTable) comp;

            Point pt = SwingUtilities.convertPoint(me.getComponent(), me.getPoint(), table);
            if (table.getSelectedRowCount() <= 1) {
                int r = table.rowAtPoint(pt);
                if (r >= 0 && r < table.getRowCount()) {
                    table.setRowSelectionInterval(r, r);
                }
            }
            
            JPopupMenu menu = new JPopupMenu();
            if (menu_p == null) {
                menu_p = menu;
            } else {
//                menu_p.setVisible(false);
            }
            boolean presente = false;
            for (MenuElement elem : menu_p.getSubElements()) {
                try {
//                    System.out.println("elem:" + elem);
//                    System.out.println("(MenuItem)elem).getLabel():" + ((JMenuItem)elem).getText());
                    if (((JMenuItem)elem).getAction() instanceof EsportaAction) presente = true;
//                    if (((JMenuItem)elem).getText().equalsIgnoreCase("Esporta in Excel")) presente = true;
                } catch (Exception e) {
                }
            }
            if (!presente) {
                if (menu_p.getSubElements().length > 0) {
                    menu_p.addSeparator();
                }
                menu_p.add(new EsportaAction(table));
            }
            
            menu_p.pack();
            menu_p.show(table, pt.x, pt.y);
        }

    }

    static public void abilitaUndo(Component comp) {
        if (comp instanceof JTextComponent) {
//            System.out.println("abilito undo su:" + comp.getName() + " : " + comp.getClass() + " : " + comp);
            JTextComponent tf = (JTextComponent) comp;
            final UndoManager undoManagertf = new UndoManager();
            tf.getActionMap().put("Undo", new AbstractAction("Undo") {

                public void actionPerformed(ActionEvent e) {
                    try {
                        undoManagertf.undo();
                    } catch (Exception ex) {
                        Toolkit.getDefaultToolkit().beep();
                    }
                }
            });
            tf.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
            tf.getActionMap().put("Redo", new AbstractAction("Redo") {

                public void actionPerformed(ActionEvent e) {
                    try {
                        undoManagertf.redo();
                    } catch (Exception ex) {
                        Toolkit.getDefaultToolkit().beep();
                    }
                }
            });
            tf.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");
            MyUndoableEditListener myundo = new MyUndoableEditListener();
            myundo.comp = tf;
            myundo.undoManager = undoManagertf;
            tf.getDocument().addUndoableEditListener(myundo);
        } else if (comp instanceof Container) {
            Container cont = (Container) comp;
            for (Component comp2 : cont.getComponents()) {
                abilitaUndo(comp2);
            }
        }
    }
}

class EsportaAction extends AbstractAction {

    JTable comp;

    public EsportaAction(JTable comp) {
        super("Esporta tabella in Excel", new ImageIcon(CutAction.class.getResource("/res/icons/tango-icon-theme-080/16x16/mimetypes/x-office-spreadsheet.png")));
        this.comp = comp;
    }

    public void actionPerformed(ActionEvent e) {

        System.out.println("esporta");

        PrintSimpleTable print = new PrintSimpleTable(comp);
        String file = print.printExcel("Export", null, "", "");
//        if (main.getPersonalContain("open")) {
//            Util.start(file);
//        } else {
//            SwingUtils.open(new File(file));
//        }
        Util.start2(file);

        System.out.println("esporta finito " + file);
    }

    public boolean isEnabled() {
        return true;
    }
}

class CutAction extends AbstractAction {

    JTextComponent comp;

    public CutAction(JTextComponent comp) {
        super("Taglia", new ImageIcon(CutAction.class.getResource("/res/icons/tango-icon-theme-080/16x16/actions/edit-cut.png")));
        this.comp = comp;
    }

    public void actionPerformed(ActionEvent e) {
        comp.cut();
    }

    public boolean isEnabled() {
        return comp.isEditable()
                && comp.isEnabled()
                && comp.getSelectedText() != null;
    }
}

class PasteAction extends AbstractAction {

    JTextComponent comp;

    public PasteAction(JTextComponent comp) {
        super("Incolla", new ImageIcon(CutAction.class.getResource("/res/icons/tango-icon-theme-080/16x16/actions/edit-paste.png")));
        this.comp = comp;
    }

    public void actionPerformed(ActionEvent e) {
        comp.paste();
    }

    public boolean isEnabled() {
        if (comp.isEditable() && comp.isEnabled()) {
            Transferable contents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this);
            return contents.isDataFlavorSupported(DataFlavor.stringFlavor);
        } else {
            return false;
        }
    }
}

class DeleteAction extends AbstractAction {

    JTextComponent comp;

    public DeleteAction(JTextComponent comp) {
        super("Cancella", new ImageIcon(CutAction.class.getResource("/res/icons/tango-icon-theme-080/16x16/actions/edit-delete.png")));
        this.comp = comp;
    }

    public void actionPerformed(ActionEvent e) {
        comp.replaceSelection(null);
    }

    public boolean isEnabled() {
        return comp.isEditable()
                && comp.isEnabled()
                && comp.getSelectedText() != null;
    }
}

class CopyAction extends AbstractAction {

    JTextComponent comp;

    public CopyAction(JTextComponent comp) {
        super("Copia", new ImageIcon(CutAction.class.getResource("/res/icons/tango-icon-theme-080/16x16/actions/edit-copy.png")));
        this.comp = comp;
    }

    public void actionPerformed(ActionEvent e) {
        comp.copy();
    }

    public boolean isEnabled() {
        return comp.isEnabled()
                && comp.getSelectedText() != null;
    }
}

class SelectAllAction extends AbstractAction {

    JTextComponent comp;

    public SelectAllAction(JTextComponent comp) {
        super("Seleziona tutto", new ImageIcon(CutAction.class.getResource("/res/icons/tango-icon-theme-080/16x16/actions/edit-select-all.png")));
        this.comp = comp;
    }

    public void actionPerformed(ActionEvent e) {
        comp.selectAll();
    }

    public boolean isEnabled() {
        return comp.isEnabled()
                && comp.getText().length() > 0;
    }
}

class UndoAction extends AbstractAction {

    JTextComponent comp;

    public UndoAction(JTextComponent comp) {
        super("Annulla", new ImageIcon(CutAction.class.getResource("/res/icons/tango-icon-theme-080/16x16/actions/edit-undo.png")));
        this.comp = comp;
    }

    public void actionPerformed(ActionEvent e) {
        comp.getActionMap().get("Undo").actionPerformed(null);
    }

    public boolean isEnabled() {
        UndoableEditListener[] listeners = ((AbstractDocument) comp.getDocument()).getListeners(UndoableEditListener.class);
//        System.out.println("listeners:" + listeners.length + ":" + listeners);
        for (UndoableEditListener uel : listeners) {
            if (uel instanceof MyUndoableEditListener) {
                UndoManager um = ((MyUndoableEditListener) uel).undoManager;
                if (um.canUndo()) {
                    return true;
                }
            }
        }
        return false;
    }
}

class RedoAction extends AbstractAction {

    JTextComponent comp;

    public RedoAction(JTextComponent comp) {
        super("Ripeti", new ImageIcon(CutAction.class.getResource("/res/icons/tango-icon-theme-080/16x16/actions/edit-redo.png")));
        this.comp = comp;
    }

    public void actionPerformed(ActionEvent e) {
        comp.getActionMap().get("Redo").actionPerformed(null);
    }

    public boolean isEnabled() {
        UndoableEditListener[] listeners = ((AbstractDocument) comp.getDocument()).getListeners(UndoableEditListener.class);
//        System.out.println("listeners:" + listeners.length + ":" + listeners);
        for (UndoableEditListener uel : listeners) {
            if (uel instanceof MyUndoableEditListener) {
                UndoManager um = ((MyUndoableEditListener) uel).undoManager;
                if (um.canRedo()) {
                    return true;
                }
            }
        }
        return false;
    }
}

class MyUndoableEditListener implements UndoableEditListener {

    public JTextComponent comp;
    public UndoManager undoManager;

    public void undoableEditHappened(UndoableEditEvent e) {
        undoManager.addEdit(e.getEdit());
    }
}
