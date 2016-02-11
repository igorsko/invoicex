/*
 * CurrentDir.java
 *
 * Created on October 13, 2003, 9:52 AM
 */
package it.tnx.shell;

import java.io.File;

/**
 *
 * @author  marco
 */
public class CurrentDir {

    /** Creates a new instance of CurrentDir */
    public CurrentDir() {
    }

    public static String getCurrentDir() {
        File dir1 = new File(".");
        try {
            System.out.println("Current dir: " + dir1.getCanonicalPath());
            return dir1.getCanonicalPath();
        } catch (Throwable t) {
            t.printStackTrace();
            return "";
        }
    }

    public static void main(String args[]) {
        File dir1 = new File(".");
        File dir2 = new File("..");
        try {
            System.out.println("Current dir: " + dir1.getCanonicalPath());
            System.out.println("Parent  dir: " + dir2.getCanonicalPath());
            System.out.println("Current dir: " + System.getProperty("user.dir"));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
