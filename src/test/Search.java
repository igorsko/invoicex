/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.jidesoft.swing.AutoCompletionComboBox;
import com.jidesoft.swing.SearchableUtils;
import it.tnx.commons.table.EditorUtils;
import it.tnx.commons.table.EditorUtils.ComboEditor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

/**
 *
 * @author mceccarelli
 */
public class Search {

    public static void main(String[] args) throws ClassNotFoundException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (InstantiationException ex) {
            Logger.getLogger(Search.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Search.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Search.class.getName()).log(Level.SEVERE, null, ex);
        }

        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLayout(new AbsoluteLayout());
        
        JTable tab = new JTable();
        JScrollPane scroll = new JScrollPane(tab);
        DefaultTableModel model = new DefaultTableModel(10, 3);
        tab.setModel(model);
        tab.setSurrendersFocusOnKeystroke(true);
        tab.setRowHeight(tab.getRowHeight() + 4);

        TableColumn col = tab.getColumnModel().getColumn(0);
        TableColumn col2 = tab.getColumnModel().getColumn(1);
        Object[] items = new String[] {"aaaaa", "aaab", "aaa23232", "bbbbbbbb", "bbbbbbbbaaa"};
        JComboBox combo = new JComboBox(items);
        combo.setBorder(BorderFactory.createEmptyBorder());
        DefaultCellEditor editor = new DefaultCellEditor(combo);
        SearchableUtils.installSearchable(combo);
        col.setCellEditor(editor);
        
//        JideComboBox combo2 = new JideComboBox(items);
//        combo2.setEditable(false);
//        DefaultCellEditor editor2 = new DefaultCellEditor(combo2);
//        SearchableUtils.installSearchable(combo2);
//        col2.setCellEditor(editor2);

        AutoCompletionComboBox combo2b = new AutoCompletionComboBox(items);
        combo2b.setBorder(BorderFactory.createEmptyBorder());
        ComboEditor editor2b = new EditorUtils.ComboEditor(combo2b);
        col2.setCellEditor(editor2b);


        f.getContentPane().add(scroll, new AbsoluteConstraints(10, 10, 400, 400));
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}
