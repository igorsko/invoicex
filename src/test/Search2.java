/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.jidesoft.hints.AbstractListIntelliHints;
import it.tnx.commons.DbUtils;
import it.tnx.commons.KeyValuePair;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import org.apache.commons.lang.StringUtils;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

/**
 *
 * @author mceccarelli
 */
public class Search2 {

    public static void main(String[] args) throws ClassNotFoundException, MalformedURLException, IOException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (InstantiationException ex) {
            Logger.getLogger(Search.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Search.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Search.class.getName()).log(Level.SEVERE, null, ex);
        }

        final String password = JOptionPane.showInputDialog("password");

        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setLayout(new AbsoluteLayout());

        final BufferedImage image = ImageIO.read(Search2.class.getResource("/res/icons/tango-icon-theme-080/16x16/actions/system-search.png"));
        Border border = UIManager.getBorder("TextField.border");
        final int x0 = border.getBorderInsets(new JTextField()).left;
        JTextField textField = new JTextField() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int y = (getHeight() - image.getHeight()) / 2;
                g.drawImage(image, x0, y, this);
            }
        };
        textField.setMargin(new Insets(0, x0 + image.getWidth(), 0, 0));

        final AbstractListIntelliHints al = new AbstractListIntelliHints(textField) {
            String current_search = "";
            @Override
            protected JList createList() {
                final JList list = super.createList();
                list.setCellRenderer(new DefaultListCellRenderer() {

                    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
//                        System.out.println(System.nanoTime() + " : cell render : " + value + " : " + index);
                        String img, tipo;
                        tipo = (String) ((KeyValuePair) value).value;
                        JLabel lab = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                        String word = current_search.toLowerCase();
                        String content = tipo.toLowerCase();
                        Color c = lab.getBackground();
                        c = c.darker();
                        String rgb = Integer.toHexString(c.getRGB());
                        rgb = rgb.substring(2, rgb.length());
                        content = StringUtils.replace(content, word, "<span style='background-color: " + rgb + "'>" + word + "</span>");
                        lab.setText("<html>" + content + "</html>");
                        
                        return lab;

//                        JTextPane tp = new JTextPane();
//                        tp.setText(tipo);
//                        if (isSelected) {
//                            tp.setBackground(Color.BLUE);
//                        } else {
//                            tp.setBackground(Color.WHITE);
//                        }
//
//                        //rimuove altri highligh
//                        Highlighter highlighter = tp.getHighlighter();
//                        Highlighter.Highlight[] highlights = highlighter.getHighlights();
//                        for (int i = 0; i < highlights.length; i++) {
//                            Highlighter.Highlight h = highlights[i];
//                            highlighter.removeHighlight(h);
//                        }
//                        // Look for the word we are given - insensitive search
//                        String content = null;
//                        content = tipo.toLowerCase();
//
//                        String word = current_search.toLowerCase();
//                        int lastIndex = 0;
//                        int wordSize = word.length();
//
//                        while ((lastIndex = content.indexOf(word, lastIndex)) != -1) {
//                            int endIndex = lastIndex + wordSize;
//                            try {
//                                highlighter.addHighlight(lastIndex, endIndex, new DefaultHighlighter.DefaultHighlightPainter(Color.red));
//                            } catch (BadLocationException e) {
//                                // Nothing to do
//                            }
//                            lastIndex = endIndex;
//                        }

//                        return tp;
                    }
                });
                return list;
            }

            public boolean updateHints(Object arg0) {
                System.out.println("arg0:" + arg0);
                current_search = arg0.toString();
                Connection conn;
                try {
                    conn = DbUtils.getMysqlJdbcConn("linux", "GestioneFatture_tnx", "root", password);
                    System.out.println("conn:" + conn);
                    ResultSet rs = DbUtils.tryOpenResultSet(conn, "Select codice, ragione_sociale from clie_forn where ragione_sociale like '%" + arg0.toString() + "%' order by ragione_sociale");
                    Vector v = new Vector();
                    while (rs.next()) {
                        KeyValuePair kvp = new KeyValuePair(rs.getString(1), rs.getString(2));
                        v.add(kvp);
                    }
                    setListData(v);
                } catch (Exception ex) {
                    Logger.getLogger(Search2.class.getName()).log(Level.SEVERE, null, ex);
                }

                return true;
            }

        };


        f.getContentPane().add(textField, new AbsoluteConstraints(10, 10, 300, 20));
        f.getContentPane().add(new JTextField("aaaa"), new AbsoluteConstraints(10, 35, 300, 20));
        f.setSize(400, 400);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}
