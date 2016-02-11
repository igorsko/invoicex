/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.tnx.proto;

import it.tnx.commons.SwingUtils;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import javax.swing.JComponent;
import javax.swing.Timer;
import org.jdesktop.jxlayer_old.JXLayer;
import org.jdesktop.jxlayer_old.plaf.ext.LockableUI;
import org.jdesktop.swingx.painter.AbstractPainter.Interpolation;
import org.jdesktop.swingx.painter.BusyPainter;

/**
 *
 * @author mceccarelli
 */
public class LockableBusyPainterUI extends LockableUI implements ActionListener {

    private BusyPainter busyPainter;
    private Timer timer;
    private int frameNumber;

    public LockableBusyPainterUI() {
        busyPainter = new BusyPainter() {
            @Override
            protected void doPaint(Graphics2D g, Object t, int width, int height) {
                Rectangle r = new Rectangle(((JComponent) t).getSize().width, ((JComponent) t).getSize().height);
                g.setColor(new Color(100, 100, 100, 50));
                g.fill(r);
                super.doPaint(g, t, width, height);
//                String s = "caricamento...";
//                int ws = g.getFontMetrics().stringWidth(s);
//                g.setColor(Color.LIGHT_GRAY);
//                g.drawString(s, width / 2 - ws / 2, height / 2 + 26);
//                g.setColor(Color.DARK_GRAY);
//                g.drawString(s, width / 2 - ws / 2, height / 2 + 26 + 1);
            }
        };
        busyPainter.setPaintCentered(true);
        busyPainter.setPointShape(new Ellipse2D.Float(0, 0, 8, 8));
        busyPainter.setTrajectory(new Ellipse2D.Float(0, 0, 26, 26));
        timer = new Timer(75, this);
    }

    @Override
    protected void paintLayer(Graphics2D g2, JXLayer<? extends JComponent> l) {
        try {
            super.paintLayer(g2, l);    
        } catch (Exception e) {
        }
        if (isLocked()) {
            busyPainter.paint(g2, l, l.getWidth(), l.getHeight());
        }
    }

    @Override
    public void setLocked(boolean isLocked) {
        super.setLocked(isLocked);
        if (isLocked) {
            timer.start();
        } else {
            timer.stop();
        }
    }

    // Change the frame for the busyPainter
    // and mark BusyPainterUI as dirty
    public void actionPerformed(ActionEvent e) {
        SwingUtils.inEdt(new Runnable() {
            public void run() {
                frameNumber = (frameNumber + 1) % 8;
                busyPainter.setFrame(frameNumber);
                // this will repaint the layer
                setDirty(true);
//                getLayer().repaint();
            }
        });
    }
}
