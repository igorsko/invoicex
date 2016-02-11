
import java.util.Enumeration;
import javax.swing.UIManager;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author test2
 */
public class TestUIDefaults {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Enumeration e = UIManager.getDefaults().keys();
        while (e.hasMoreElements()) {
            Object o = e.nextElement();
            System.out.println("uidef:" + o + " : " + UIManager.getDefaults().get(o));
        }

    }
}
