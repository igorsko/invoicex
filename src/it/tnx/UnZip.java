/*
 * Unzip.java
 *
 * Created on 22 marzo 2002, 21.52
 */

package it.tnx;

/**
 *
 * @author  Administrator
 */
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.ZipException;

public class UnZip {

     public static ZipFile zf;

     public static final int EOF = -1;

     public static void main( String argv[] ) {

          Enumeration enum1;

          if( argv.length == 1 ) {
              try {
                   zf = new ZipFile( argv[0] );
                   enum1 = zf.entries();

                   while( enum1.hasMoreElements() ) {

                         ZipEntry target = (ZipEntry)enum1.nextElement();
                         System.out.print( target.getName() + " ." );
                         saveEntry( target );
                         System.out.println( ". unpacked" );
                   }
              }
              catch( FileNotFoundException e ){
                     System.out.println( "zipfile not found" );
              }
              catch( ZipException e ){
                     System.out.println( "zip error..." );
              }
              catch( IOException e ){
                     System.out.println( "IO error..." );
              }
         }
         else {
               System.out.println( "Usage:java UnZip zipfile" );
         }
   }

     public static void saveEntry( ZipEntry target )
                                   throws ZipException,IOException {

            try {
                 File file = new File( target.getName() );
                 if( target.isDirectory() ) {
                     file.mkdirs();
                 }
                 else {
                     InputStream is = zf.getInputStream( target );
                     BufferedInputStream bis = new BufferedInputStream( is );
                     if (file.getParent()!=null) {
                       File dir = new File( file.getParent() );
                       dir.mkdirs();
                     }
                     FileOutputStream fos = new FileOutputStream( file );
                     BufferedOutputStream bos = new BufferedOutputStream( fos );

                     int c;
                     while( ( c = bis.read() ) != EOF ) {
                          bos.write( (byte)c );
                     }
                     bos.close();
                     fos.close();
                 }
            }
            catch( ZipException e ){
                   throw e;
            }
            catch( IOException e ){
                   throw e;
            }
      }
}