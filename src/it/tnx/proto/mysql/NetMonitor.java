/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.tnx.proto.mysql;

import gestioneFatture.main;
import it.tnx.commons.FormatUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author mceccarelli
 */
public class NetMonitor extends Thread {

    int outb = 0;
    int inb = 0;
    float totalbs = 0f;
    long totaloutb = 0;
    long totalinb = 0;
    long totalb = 0;
    long old_totalb = 0;
    int addq = 0;

    public static boolean debug = false;
    
    public NetMonitor() {
        super("NetMonitor");
        start();
    }

    @Override
    public void run() {
        super.run();
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(NetMonitor.class.getName()).log(Level.SEVERE, null, ex);
            }
            totalbs = outb + inb;
            totalb = totaloutb + totalinb;
//            System.err.println("totalkb/s: " + FormatUtils.formatPerc((double)totalbs / 1024d) + " | total Mb: " + FormatUtils.formatPerc((double)totalb / 1024d / 1024d));
//            try {
//                main.getPadre().setTitle("Kb/s " + FormatUtils.formatPerc((double)totalbs / 1024d) + " | Total Mb " + FormatUtils.formatPerc((double)totalb / 1024d / 1024d));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            try {
                if (totalb != old_totalb) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
//                            System.out.println("monitor on");
                            if (main.getPadre() != null) {
                                main.getPadre().lblInfoLoading2.setBusy(true);
                                main.getPadre().lblInfoLoading2.setVisible(true);
                                main.getPadre().lblInfoLoading2.setText("<html>Monitor rete <b>" + FormatUtils.formatPerc((double) totalbs / 1024d) + "</b> Kb/s  / Total <b>" + FormatUtils.formatPerc((double) totalb / 1024d / 1024d) + "</b> Mb" + (debug ? "/ Queries <b>" + addq + "</b>" : "") + "</html>");
                            }
                        }
                    });
                } else {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
//                            System.out.println("monitor off");
                            if (main.getPadre() != null) {
                                main.getPadre().lblInfoLoading2.setBusy(false);
                                main.getPadre().lblInfoLoading2.setVisible(false);
                                main.getPadre().lblInfoLoading2.setText("");
                            }
                        }
                    });
                }
            } catch (Exception e) {
            }
            old_totalb = totalb;

            outb = 0;
            inb = 0;
        }
    }

    public void addoutb(int b) {
        outb += b;
        totaloutb += b;
    }

    public void addinb(int b) {
        inb += b;
        totalinb += b;
    }
}
