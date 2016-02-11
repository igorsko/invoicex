/**
 * Copyright (c) 2006-2008, Alexander Potochkin
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials provided
 *     with the distribution.
 *   * Neither the name of the JXLayer project nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.jdesktop.jxlayer_old.demo;

import org.jdesktop.jxlayer_old.JXLayer;
import org.jdesktop.jxlayer_old.plaf.AbstractLayerUI;
import org.jdesktop.jxlayer_old.demo.util.LafMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;

/**
 * A demo which is shows the "flash animation effect".
 * Move the mouse cursor over the button to see it
 */
public class FlashButtonDemo {
    private static void createGui() {
        JFrame frame = new JFrame("FlashButtonDemo");
        frame.setJMenuBar(LafMenu.createMenuBar());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JLabel("Move mouse over the button", JLabel.CENTER),
                BorderLayout.SOUTH);
        
        JPanel panel = new JPanel(new GridBagLayout());

        JXLayer<AbstractButton> l = new JXLayer<AbstractButton>(new JButton("Hello"));
        
        panel.add(l);
        l.setSize(200, 200);

        l.setUI(new FlashButtonUI());

        frame.add(panel);
        frame.setSize(250, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createGui();
            }
        });
    }

    public static class FlashButtonUI extends AbstractLayerUI<AbstractButton> {
        private Timer timer;
        private double scale;
        private boolean rollover;

        public FlashButtonUI() {
            scale = 1;
            timer = new Timer(20, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (scale > 3) {
                        timer.stop();
                        scale = 1;
                    } else {
                        scale += .2;
                    }
                    setDirty(true);
                }
            });
        }

        @Override
        protected void paintLayer(Graphics2D g2, JXLayer<? extends AbstractButton> l) {
            // paint in once
            l.paint(g2);
            
            // start the timer if the button is in rollovered state
            boolean currRollover = l.getView().getModel().isRollover();
            if (currRollover && !rollover) {
                if (!timer.isRunning()) {
                    timer.start();
                }
            }
            rollover = currRollover;
            
            // apply transform and paint the layer again if timer is running
            if (timer.isRunning()) {
                g2.transform(getCenteredScaleTransform(l));
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .3f));
                l.paint(g2);
            }            
        }

        private AffineTransform getCenteredScaleTransform(JComponent c) {
            AffineTransform t = AffineTransform.getScaleInstance(scale, scale);
            int width = c.getWidth();
            double xt = ((width * scale) - width) / (2 * scale);
            int height = c.getHeight();
            double yt = ((height * scale) - height) / (2 * scale);
            t.translate(-xt, -yt);
            return t;
        }
    }
}