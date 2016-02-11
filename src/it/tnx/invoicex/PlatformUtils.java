/**
 * Invoicex
 * Copyright (c) 2005,2006,2007 Marco Ceccarelli, Tnx snc
 *
 * Questo software è soggetto, e deve essere distribuito con la licenza  
 * GNU General Public License, Version 2. La licenza accompagna il software
 * o potete trovarne una copia alla Free Software Foundation http://www.fsf.org .
 *
 * This software is subject to, and may be distributed under, the
 * GNU General Public License, Version 2. The license should have
 * accompanied the software or you may obtain a copy of the license
 * from the Free Software Foundation at http://www.fsf.org .
 * 
 * --
 * Marco Ceccarelli (m.ceccarelli@tnx.it)
 * Tnx snc (http://www.tnx.it)
 *
 */



package it.tnx.invoicex;
public class PlatformUtils {
    
    public static final int WINDOWS = 0;
    public static final int LINUX = 1;
    public static final int SOLARIS = 2;
    public static final int MAC = 3;
    public static final int OTHER = 4;
    
    public static final int NO_PLATFORM = -1;
    
    public static boolean isWindows() {
        return ( getPlatform() == WINDOWS );
    }
    
    public static boolean isLinux() {
        return ( getPlatform() == LINUX );
    }
    
    public static boolean isSolaris() {
        return ( getPlatform() == SOLARIS );
    }
    
    public static boolean isMac() {
        return ( getPlatform() == MAC );
    }
    
    public static boolean isOther() {
        return ( getPlatform() == OTHER );
    }
    
    public static int getPlatform() {
        fetchPlatform();
        return iPlatform;
    }
    
    public static String getPlatformName() {
        fetchPlatform();
        return sOsName;
    }
    
    private static void fetchPlatform() {
        if ( iPlatform == NO_PLATFORM ) {
            
            sOsName = System.getProperty( "os.name" ).toLowerCase();
            if ( sOsName.indexOf( "windows" ) != -1 ) {
                iPlatform = WINDOWS;
            } else if ( sOsName.indexOf( "linux" ) != -1 ) {
                iPlatform = LINUX;
            } else if ( ( sOsName.indexOf( "solaris" ) != -1 ) ||
                    ( sOsName.indexOf( "sunos" ) != -1 ) ) {
                iPlatform = SOLARIS;
            } else if ( sOsName.indexOf( "mac" ) != -1 ) {
                iPlatform = MAC;
            }
        }
    }
    
    private static int iPlatform = NO_PLATFORM;
    private static String sOsName;
}