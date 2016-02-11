/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * tnxTextFieldLang.java
 *
 * Created on 27-dic-2010, 17.44.00
 */

package tnxbeans;

import it.tnx.commons.ImgUtils;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author mceccarelli
 */
public class tnxTextFieldLang extends javax.swing.JPanel implements BasicField {

    private List<String> lang = null;
    private List<JTextField> textfield;
    private Map<String, JTextField> lang_text = null;
    private List<JButton> buttons;
    public String dbNomeCampo;
    public String dbTipoCampo;
    public boolean dbRiempire = true;
    public boolean dbSalvare = true;
    public boolean dbModificato = false;
    int margin = 1;


    /** Creates new form tnxTextFieldLang */
    public tnxTextFieldLang() {
        initComponents();

        setLayout(new LayoutManager() {

            public void addLayoutComponent(String name, Component comp) {
            }

            public void removeLayoutComponent(Component comp) {
            }

            public Dimension preferredLayoutSize(Container parent) {
                return new Dimension(50, 20);
            }

            public Dimension minimumLayoutSize(Container parent) {
                return new Dimension(50, 20);
            }

            public void layoutContainer(Container parent) {
                layoutComps();
            }
        });
    }

    /**
     * @return the lang
     */
    public List<String> getLang() {
        return lang;
    }

    /**
     * @param lang the lang to set
     */
    public void setLang(List<String> lang) {
        this.lang = lang;

        removeAll();

        setTextfield(new ArrayList<JTextField>());
        setLang_text((Map<String, JTextField>) new HashMap());
        for (String l : lang) {
            LangJTextField a = new LangJTextField();
            a.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) {
                    change();
                }
                public void removeUpdate(DocumentEvent e) {
                    change();
                }
                public void changedUpdate(DocumentEvent e) {
                    change();
                }
                public void change() {
                    tnxTextFieldLang.this.dbModificato = true;
                    if (tnxTextFieldLang.this.getParent() instanceof tnxDbPanel) {
                        tnxDbPanel temp = (tnxDbPanel)tnxTextFieldLang.this.getParent();
                        if (temp.getParentPanel() != null) temp = temp.getParentPanel();
                        boolean res = temp.dbCheckModificati();
                    }
                }
            });
            a.lang = l;
            getTextfield().add(a);
            getLang_text().put(l, a);
            add(a);
        }

        layoutComps();
    }

    private void layoutComps() {
        if (getLang() != null) {
            int langs = getLang().size();
            int w = getWidth();
            int h = getHeight();
            int ws = (w / langs);
            int i = 0;
            for (String l : getLang()) {
                JTextField t = getTextfield().get(i);
                if (i == getLang().size() - 1) {
                    t.setBounds((ws * i) + margin, margin, ws - (margin * 2), h - (margin * 2));
                } else {
                    t.setBounds((ws * i) + margin, margin, ws - (margin), h - (margin * 2));
                }
                i++;
            }
        }
    }

    /**
     * @return the lang_text
     */
    public Map<String, JTextField> getLang_text() {
        return lang_text;
    }

    /**
     * @param lang_text the lang_text to set
     */
    public void setLang_text(Map<String, JTextField> lang_text) {
        this.lang_text = lang_text;
    }

    public static class LangJTextField extends JTextField {

        public String lang = null;
        Locale locsel = null;
        String locsel_country = null;

        public LangJTextField() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g;
            Color old_color = g2.getColor();
            Font old_font = g2.getFont();
            Composite old_comp = g2.getComposite();

            g2.setColor(getBackground());
            g2.fillRect(0, 0, getWidth(), getHeight());

            int xrif = getWidth();
            int yrif = getHeight();

            if (locsel == null) {
                String lang2 = lang;
                if (lang2.equalsIgnoreCase("en")) {
                    locsel_country = "Great Britain";
                } else {
                    locsel = new Locale(lang.toLowerCase(), lang2.toUpperCase());
                    System.out.println(locsel);
                    locsel_country = locsel.getDisplayCountry(Locale.ENGLISH);
                    System.out.println(locsel_country);
                }
            }

            g2.setColor(new Color(old_color.getRed(), old_color.getGreen(), old_color.getBlue(), 50));
            g2.setFont(old_font.deriveFont(10f));
            g2.drawString(lang.toUpperCase(), xrif - 34, yrif - 5);
            try {
                BufferedImage flag = ImageIO.read(getClass().getResource("/flags/24/" + locsel_country + ".png"));
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
                Image flagr = ImgUtils.resizeMedium(flag, (int)(flag.getWidth() * 0.75), (int)(flag.getHeight() * 0.75));
                g2.drawImage(flagr, xrif - 20, yrif - 18, null);
            } catch (Exception e) {
            }

            g2.setColor(old_color);
            g2.setFont(old_font);
            g2.setComposite(old_comp);

            super.paintComponent(g2);

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

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    public static void main(String[] args) {
        JFrame f = new JFrame("test");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLayout(new BorderLayout(2, 2));
        tnxTextFieldLang t = new tnxTextFieldLang();
//        t.setBounds(10, 10, 400, 300);
        t.setBorder(new LineBorder(Color.red));
        f.getContentPane().add(t, BorderLayout.CENTER);
        t.setLang(Arrays.asList("it", "en", "fr"));
        f.pack();

        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    public List<JTextField> getTextfield() {
        return textfield;
    }

    public void setTextfield(List<JTextField> textfield) {
        this.textfield = textfield;
    }

    public List<JButton> getButtons() {
        return buttons;
    }

    public void setButtons(List<JButton> buttons) {
        this.buttons = buttons;
    }

    public String getDbNomeCampo() {
        return dbNomeCampo;
    }

    public void setDbNomeCampo(String dbNomeCampo) {
        this.dbNomeCampo = dbNomeCampo;
    }

    public String getDbTipoCampo() {
        return dbTipoCampo;
    }

    public void setDbTipoCampo(String dbTipoCampo) {
        this.dbTipoCampo = dbTipoCampo;
    }

    public boolean isDbRiempire() {
        return dbRiempire;
    }

    public void setDbRiempire(boolean dbRiempire) {
        this.dbRiempire = dbRiempire;
    }

    public boolean isDbSalvare() {
        return dbSalvare;
    }

    public void setDbSalvare(boolean dbSalvare) {
        this.dbSalvare = dbSalvare;
    }

    public boolean isDbModificato() {
        return dbModificato;
    }

    public void setDbModificato(boolean dbModificato) {
        this.dbModificato = dbModificato;
    }

    public String getText(String lang) {
        return getLang_text().get(lang).getText();
    }

    public void setText(String text) {
        for (String l : getLang()) {
            JTextField a = getLang_text().get(l);
            a.setText(text);
        }
    }
    public void setText(String lang, String text) {
        JTextField a = getLang_text().get(lang);
        a.setText(text);
    }

    public HashMap<String, String> getTextLang() {
        //return lang_area.get(lang).getText();
        HashMap<String, String> ret = new HashMap<String, String>();
        for (String l : getLang()) {
            JTextField a = getLang_text().get(l);
            ret.put(l, a.getText());
        }
        return ret;
    }

}