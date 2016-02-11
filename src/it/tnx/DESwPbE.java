/* ==========================================================================
 * This code is licensed under:
 * SecureDataManager Software License, Version 1.0
 * - for license details see http://sdm.sourceforge.net/LICENSE.TXT
 *
 * Copyright (c) 2001 by the SecureDataManager Team. All rights reserved.
 * (SecureDataManager Team - see http://sdm.sourceforge.net/team.html)
 */
package it.tnx;



import java.io.*;

/**
 * Implements the SecureFileAccess interface using DES with MD5 and
 * Password based Encryption.
 * It uses the SealedObject class to securely wrap a data object of any type
 * (must extend java.io.Serializable). Provides an API to write and retrieve the
 * object provided with a passphrase and a filename.
 * <br>
 * @author <a href="mailto:marktoml@hotmail.com">Mark Tomlinson</a>
 * @version %I%, %G%
 * <a href="http://www.tomlinson-web.net/">Technical Homepage</a><BR>
 *
 * TODO: allow storage and retrieval of multiple objects per file<BR>
 */
import java.lang.Exception.*;

import java.security.*;
import java.security.spec.*;


import javax.crypto.*;
import javax.crypto.spec.*;


public class DESwPbE {
    private String algorithm = "PBEWithMD5AndDES";
    private byte[] salt = new byte[8];
    private int iterations = 20;
    private SecretKey key;
    private long fileLength;
    private File dataFile;
    private AlgorithmParameterSpec aps;
    private Cipher cipher;
    private Provider sunJce;
    private String passPhrase;

    public DESwPbE() {
//        sunJce = new com.sun.crypto.provider.SunJCE();
//        Security.addProvider(sunJce);
    }

    public void setPassPhrase(String Password) {
        passPhrase = Password;
    }
    
    public String getStringCrypt() {
        try {
            KeySpec ks = new PBEKeySpec(passPhrase.toCharArray());
            SecretKeyFactory skf = SecretKeyFactory.getInstance(algorithm);
            key = skf.generateSecret(ks);
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            md.update(passPhrase.getBytes());
            byte[] digest = md.digest();
            System.arraycopy(digest, 0, salt, 0, 8);
            aps = new PBEParameterSpec(salt, iterations);
        } catch (Exception e) {                     
            System.err.println("Security_exception");
            return null;
        }
        try {
            cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, key, aps);
        } catch (Exception e) {
            return null;
        }
        return "";
    }

    public boolean WriteFile(java.io.Serializable inObj, String fileName) {
        FileOutputStream out;

        try {
            out = new FileOutputStream(fileName);

            KeySpec ks = new PBEKeySpec(passPhrase.toCharArray());

            SecretKeyFactory skf = SecretKeyFactory.getInstance(algorithm);

            key = skf.generateSecret(ks);

            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");

            md.update(passPhrase.getBytes());

            byte[] digest = md.digest();

            System.arraycopy(digest, 0, salt, 0, 8);

            aps = new PBEParameterSpec(salt, iterations);
        } catch (Exception e) {            
          
            System.err.println("Security_exception");

            return false;
        }

        try {
            out.write(salt);

            ObjectOutputStream s = new ObjectOutputStream(out);

            cipher = Cipher.getInstance(algorithm);

            cipher.init(Cipher.ENCRYPT_MODE, key, aps);

            SealedObject so = new SealedObject(inObj, cipher);

            s.writeObject(so);

            s.flush();

            out.close();
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public java.io.Serializable ReadFile(String fileName)
        throws Exception {
        FileInputStream in;

        java.io.Serializable retObj;

        try {
            in = new FileInputStream(fileName);

            in.read(salt);

            KeySpec ks = new PBEKeySpec(passPhrase.toCharArray());

            SecretKeyFactory skf = SecretKeyFactory.getInstance(algorithm);

            key = skf.generateSecret(ks);

            aps = new PBEParameterSpec(salt, iterations);

            cipher = Cipher.getInstance(algorithm);

            cipher.init(Cipher.DECRYPT_MODE, key, aps);

            ObjectInputStream s = new ObjectInputStream(in);

            SealedObject so = (SealedObject)s.readObject();

            retObj = (java.io.Serializable)so.getObject(cipher);

            in.close();
        } catch (Exception e) {
            throw e;
        }

        return retObj;
    }
}
