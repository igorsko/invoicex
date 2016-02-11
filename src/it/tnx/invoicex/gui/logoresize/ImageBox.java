/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.tnx.invoicex.gui.logoresize;

import it.tnx.commons.ImgUtils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.JComponent;

/**
 *
 * @author mceccarelli
 */
public class ImageBox extends JComponent {

    BufferedImage image = null;

    public void setLogo(String logo) throws IOException {
        image = ImageIO.read(new File(logo));
    }

    public void setLogo(InputStream logo) throws IOException {
        image = ImageIO.read(logo);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Dimension dim = null;
        if (image != null) {
            dim = ImgUtils.getDimension(image.getWidth(), image.getHeight(), getWidth(), getHeight());
            BufferedImage image2 = ImgUtils.resizeQuality(image, dim.width, dim.height);
            g2.drawImage(image2, 0, 0, null);
        }

        Color oldc = g2.getColor();
        g2.setColor(Color.RED);

        g2.setColor(new Color(1f, 0f, 0f, 1f));
        g2.drawLine(0, 0, 4, 0);
        g2.drawLine(0, 0, 0, 4);
        g2.drawLine(4, 0, 0, 4);

        g2.drawLine(getWidth()-1, 0, getWidth()-5, 0);
        g2.drawLine(getWidth()-1, 0, getWidth()-1, 4);
        g2.drawLine(getWidth()-5, 0, getWidth()-1, 4);

        g2.drawLine(getWidth()-1, getHeight()-1, getWidth()-5, getHeight() - 1);
        g2.drawLine(getWidth()-5, getHeight()-1, getWidth()-1, getHeight() - 5);
        g2.drawLine(getWidth()-1, getHeight()-1, getWidth()-1, getHeight() - 5);

        g2.drawLine(0, getHeight()-1, 4, getHeight()-1);
        g2.drawLine(0, getHeight()-1, 0, getHeight()-5);
        g2.drawLine(0, getHeight()-5, 4, getHeight()-1);

        g2.setColor(new Color(1f, 0f, 0f, 0.1f));
        g2.drawLine(0, 0, getWidth()-1, 0);
        g2.drawLine(0, 0, 0, getHeight());
        g2.drawLine(getWidth()-1, 0, getWidth()-1, getHeight()-1);
        g2.drawLine(0, getHeight()-1, getWidth()-1, getHeight()-1);
        g2.drawLine(0, 0, getWidth()-1, getHeight()-1);
        g2.drawLine(0, getHeight()-1, getWidth()-1, 0);

        g2.setColor(new Color(0.3f, 0.3f, 0.3f, 0.05f));

        if (image != null) {
            g2.fillRect(0, 0, (int)dim.getWidth(), (int)dim.getHeight());
        }

        g2.setColor(oldc);
    }

}
