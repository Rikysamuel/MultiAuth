/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Algorithm;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Rikysamuel
 */
public class MD5 {
    public static String encryptToMD5(String str) {
        MessageDigest digester;
        StringBuffer hexString = null;
        try {
            digester = MessageDigest.getInstance("MD5");
            
            if (str == null || str.length() == 0) {
                return null;
            }
            
            digester.update(str.getBytes());
            byte[] hash = digester.digest();
            hexString = new StringBuffer();
            for (int i = 0; i < hash.length; i++) {
                if ((0xff & hash[i]) < 0x10) {
                    hexString.append("0").append(Integer.toHexString((0xFF & hash[i])));
                }
                else {
                    hexString.append(Integer.toHexString(0xFF & hash[i]));
                }
            }
            
        } catch (NoSuchAlgorithmException ex) {
            System.err.println(ex);
        }
        return hexString.toString();
    }
}
