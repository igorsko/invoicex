/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JPanelCategorie.java
 *
 * Created on 22-dic-2010, 14.53.57
 */
package it.tnx.invoicex.gui;

import it.tnx.Db;
import gestioneFatture.main;
import it.tnx.commons.DbUtils;
import it.tnx.commons.ImgUtils;
import it.tnx.commons.swing.AbstractTreeModel;
import it.tnx.invoicex.Main;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

/**
 *
 * @author mceccarelli
 */
public class JPanelCategorie extends javax.swing.JPanel {

    CategorieModel model = null;
    boolean perSelezione = false;
    public Map categoria_selezionata = null;

    /** Creates new form JPanelCategorie */
    public JPanelCategorie() {
        initComponents();

        tcat.setCellRenderer(new DefaultTreeCellRenderer() {

            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                JLabel comp = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                if (value instanceof Map) {
                    Map m = (Map) value;
//                    System.out.println("m = " + m);
                    String img = (String) m.get("immagine1");
                    try {
                        BufferedImage buffimg = ImgUtils.getImage(img, 48, 24, "Q", false, 0.85d, main.cache_img);
//                        BufferedImage buffimg = ImageIO.read(new File(img));
//                        buffimg = ImgUtils.resizeSpeed(buffimg, ImgUtils.getDimension(buffimg.getWidth(), buffimg.getHeight(), 48, 24));
                        if (buffimg != null) {
                            comp.setIcon(new ImageIcon(buffimg));
                        } else {
                            comp.setIcon(null);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    comp.setText((String) m.get("nome"));
                }
                return comp;
            }
        });
        tcat.setRowHeight(26);
    }

    public void init(boolean perSelezione) {
        this.perSelezione = perSelezione;
        model = new CategorieModel(perSelezione);
        tcat.setModel(model);
        tcat.addMouseListener(new MouseAdapter() {

            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    menu.show((JComponent) e.getSource(), e.getX(), e.getY());
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(e.getClickCount());
                System.out.println(JPanelCategorie.this.perSelezione);
                if (e.getClickCount() == 2 && JPanelCategorie.this.perSelezione) {
                    categoria_selezionata = (Map) tcat.getSelectionPath().getLastPathComponent();
                    System.out.println("categoria_selezionata = " + categoria_selezionata);
                    dispose();
                } else if (e.getClickCount() == 2 && !JPanelCategorie.this.perSelezione) {
                    modificaActionPerformed(null);
                }
            }
        });
        if (perSelezione) {
            jLabel1.setText("Seleziona la categoria con il doppio click oppure con il tasto destro e poi 'Seleziona'");
        }
    }

    public void dispose() {
    }

    public static class CategorieModel extends AbstractTreeModel {

        public Object root = "Categorie";
        private boolean cache = false;
        private Map map_cache = new HashMap();

        public CategorieModel() {
            this(false);
        }

        public CategorieModel(boolean cache) {
            this.cache = cache;
        }

        public Object getRoot() {
            return root;
        }

        public Object getChild(Object parent, int index) {
            if (parent == root) {
                //categorie di primo livello
                try {
                    List list = get("select * from categorie where id_padre is null");
                    return list.get(index);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "errore";
            } else {
                //categorie del livello passato
                try {
                    Map m = (Map) parent;
                    List list = get("select * from categorie where id_padre = " + m.get("id"));
                    return list.get(index);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "errore";
            }

        }

        public int getChildCount(Object parent) {
            if (parent == root) {
                //categorie di primo livello
                try {
                    List list = get("select id from categorie where id_padre is null");
                    return list.size();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            } else {
                //categorie del livello passato
                try {
                    Map m = (Map) parent;
                    List list = get("select id from categorie where id_padre = " + m.get("id"));
                    return list.size();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        }

        public boolean isLeaf(Object node) {
            if (node == root) {
                return false;
            } else {
                //categorie del livello passato
                try {
                    Map m = (Map) node;
                    List list = get("select id from categorie where id_padre = " + m.get("id"));
                    if (list.size() == 0) {
                        return true;
                    } else {
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        }

        public void valueForPathChanged(TreePath path, Object newValue) {
        }

        public int getIndexOfChild(Object parent, Object child) {
            System.err.println("index of child");
            return 1;
        }

        public void add(Object source, Object parent, Map m) throws Exception {
            String sql = "insert into categorie set " + DbUtils.prepareSqlFromMap(m);
            System.out.println("sql = " + sql);
            DbUtils.tryExecQuery(Db.getConn(), sql);
            TreePath path = ((JTree) source).getSelectionPath();
            //fireTreeNodesInserted(new TreeModelEvent(source, ));
            fireTreeStructureChanged(new TreeModelEvent(source, path));
        }

        private void remove(JTree source, Map m) throws Exception {
            String sql = "delete from categorie where id = " + m.get("id");
            System.out.println("sql = " + sql);
            DbUtils.tryExecQuery(Db.getConn(), sql);
            TreePath path = ((JTree) source).getSelectionPath();
            //fireTreeNodesRemoved(new TreeModelEvent(source, path));
            fireTreeStructureChanged(new TreeModelEvent(source, path));
        }

        private List get(String sql) {
            try {
                if (cache) {
                    if (map_cache.containsKey(sql)) {
                        return (List) map_cache.get(sql);
                    }
                }
                List ret = DbUtils.getListMap(Db.getConn(), sql);
                if (cache) {
                    map_cache.put(sql, ret);
                }
                return ret;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menu = new javax.swing.JPopupMenu();
        aggiungi = new javax.swing.JMenuItem();
        modifica = new javax.swing.JMenuItem();
        rimuovi = new javax.swing.JMenuItem();
        seleziona = new javax.swing.JMenuItem();
        jScrollPane1 = new javax.swing.JScrollPane();
        tcat = new javax.swing.JTree();
        jLabel1 = new javax.swing.JLabel();

        aggiungi.setText("Aggiungi");
        aggiungi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aggiungiActionPerformed(evt);
            }
        });
        menu.add(aggiungi);

        modifica.setText("Modifica");
        modifica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modificaActionPerformed(evt);
            }
        });
        menu.add(modifica);

        rimuovi.setText("Rimuovi");
        rimuovi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rimuoviActionPerformed(evt);
            }
        });
        menu.add(rimuovi);

        seleziona.setText("Seleziona");
        seleziona.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selezionaActionPerformed(evt);
            }
        });
        menu.add(seleziona);

        tcat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tcatMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tcat);

        jLabel1.setText("Categorie Articoli");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE)
                    .add(jLabel1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void aggiungiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aggiungiActionPerformed
        TreePath path = tcat.getSelectionPath();
        Object n = path.getLastPathComponent();

        Map m = new HashMap();
        if (n == model.root) {
            m.put("id_padre", null);
        } else {
            Map mn = (Map) n;
            m.put("id_padre", mn.get("id"));
        }
        String desc = JOptionPane.showInputDialog("Nome della categoria");
        if (desc.length() > 0) {
            m.put("nome", desc);
        } else {
            return;
        }
        try {
            model.add(tcat, n, m);
        } catch (Exception ex) {
            Logger.getLogger(JPanelCategorie.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("n:" + n);
    }//GEN-LAST:event_aggiungiActionPerformed

    private void rimuoviActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rimuoviActionPerformed
        TreePath path = tcat.getSelectionPath();
        Object n = path.getLastPathComponent();

        Map m = (Map) n;
        try {
            model.remove(tcat, m);
        } catch (Exception ex) {
            Logger.getLogger(JPanelCategorie.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("n:" + n);
    }//GEN-LAST:event_rimuoviActionPerformed

    private void selezionaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selezionaActionPerformed
        categoria_selezionata = (Map) tcat.getSelectionPath().getLastPathComponent();
        dispose();
    }//GEN-LAST:event_selezionaActionPerformed

    private void tcatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tcatMouseClicked
    }//GEN-LAST:event_tcatMouseClicked

    private void modificaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modificaActionPerformed
        TreePath path = tcat.getSelectionPath();
        Object n = path.getLastPathComponent();

        Map m = new HashMap();
        if (n != model.root) {
            Map mn = (Map) n;
            Integer id = (Integer) mn.get("id");

            final JDialog d_dati = new JDialog((Frame) getTopLevelAncestor());
            JPanelCategorieDati dati = new JPanelCategorieDati() {

                @Override
                public void dispose() {
                    d_dati.dispose();
                }
            };
            dati.load(id);
            d_dati.getContentPane().add(dati);
            d_dati.pack();
            d_dati.setLocationRelativeTo(null);
            d_dati.setVisible(true);

        }

    }//GEN-LAST:event_modificaActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aggiungi;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu menu;
    private javax.swing.JMenuItem modifica;
    private javax.swing.JMenuItem rimuovi;
    private javax.swing.JMenuItem seleziona;
    private javax.swing.JTree tcat;
    // End of variables declaration//GEN-END:variables

    public static void main(String[] args) {
        Main.main(new String[]{"nolog", "config=param_prop_test.txt"});
        main.getPadreFrame().dispose();

        JPanelCategorie cat = new JPanelCategorie();
        cat.init(false);

        JFrame test = new JFrame();

        test.getContentPane().add(cat);
        test.pack();
        test.setLocationRelativeTo(null);

        test.setVisible(true);

        test.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                System.out.println("!!! e1: " + e);
                main.exitMain();
            }
        });

    }
}
