package gestioneFatture;

import it.tnx.commons.DebugUtils;
import it.tnx.commons.ImgUtils;
import it.tnx.commons.SwingUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.*;
import java.io.File;
import uk.co.jaimon.test.SimpleImageInfo;

public class ImagePreviewPanel extends JPanel
        implements PropertyChangeListener {

    private int width, height;
    private BufferedImage image;
    String name = null;
    private static final int ACCSIZE = 155;
    private Color bg;

    public ImagePreviewPanel() {
        setPreferredSize(new Dimension(ACCSIZE, -1));
        bg = getBackground();
    }

    public void propertyChange(PropertyChangeEvent e) {
        String propertyName = e.getPropertyName();

        // Make sure we are responding to the right event.
        if (propertyName.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
            File selection = (File)e.getNewValue();
            String name;

            if (selection == null)
                return;
            else
                name = selection.getAbsolutePath();

            /*
             * Make reasonably sure we have an image format that AWT can
             * handle so we don't try to draw something silly.
             */
            if ((name != null) &&
                    name.toLowerCase().endsWith(".jpg") ||
                    name.toLowerCase().endsWith(".jpeg") ||
                    name.toLowerCase().endsWith(".gif") ||
                    name.toLowerCase().endsWith(".bmp") ||
                    name.toLowerCase().endsWith(".png")) {

                this.name = name;
//                icon = new ImageIcon(name);
//                image = icon.getImage();
//                scaleImage();
                try {
                    image = null;
                    System.out.println("ImagePreviewPanel memoria 1");
                    DebugUtils.dumpMem();

                    //check image size
                    SimpleImageInfo sii = new SimpleImageInfo(new File(name));
                    System.out.println("img w: " + sii.getWidth() + " h: " + sii.getHeight() + " mime: " + sii.getMimeType());
                    if (!main.debug) {
                        if ((sii.getWidth() * sii.getHeight()) > (3000 * 2000)) {
                            SwingUtils.showWarningMessage(this, "Il file è troppo grande (il limite è 3000 * 2000 px)");
                            image = null;
                            return;
                        }
                    }
                    image = ImgUtils.getImage(name, getWidth(), 0, "Q", false, 0.85d, main.cache_img);
                    width = image.getWidth();
                    height = image.getHeight();
                    System.out.println("ImagePreviewPanel memoria 2");
                    DebugUtils.dumpMem();
                } catch (OutOfMemoryError mem) {
                    System.out.println("ImagePreviewPanel errore memoria");
                    DebugUtils.dumpMem();
                    SwingUtils.showErrorMessage(main.getPadreFrame(), "Immagine troppo grande, memoria disponibile insufficiente!");
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    //SwingUtils.showExceptionMessage(main.getPadreFrame(), ex);
                    SwingUtils.showErrorMessage(main.getPadreFrame(), ex.getMessage(), true);
                }
                repaint();

                System.out.println("ImagePreviewPanel memoria fine");
                DebugUtils.dumpMem();
            }
        }
    }

    private void scaleImage() {
        width = image.getWidth(this);
        height = image.getHeight(this);
        double ratio = 1.0;

        /*
         * Determine how to scale the image. Since the accessory can expand
         * vertically make sure we don't go larger than 150 when scaling
         * vertically.
         */
        if (width >= height) {
            ratio = (double)(ACCSIZE-5) / width;
            width = ACCSIZE-5;
            height = (int)(height * ratio);
        }
        else {
            if (getHeight() > 150) {
                ratio = (double)(ACCSIZE-5) / height;
                height = ACCSIZE-5;
                width = (int)(width * ratio);
            }
            else {
                ratio = (double)getHeight() / height;
                height = getHeight();
                width = (int)(width * ratio);
            }
        }

//        image = image.getScaledInstance(width, height, Image.SCALE_DEFAULT);
    }

    public void paintComponent(Graphics g) {
        g.setColor(bg);
        g.fillRect(0, 0, ACCSIZE, getHeight());
        if (image != null) {
            g.drawImage(image, getWidth() / 2 - width / 2 + 5, getHeight() / 2 - height / 2, this);
        }
    }

}
