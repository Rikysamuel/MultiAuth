/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Algorithm;
 
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Rikysamuel
 */
public class AES {
    
    public static String IV = "AAAAAAAAAAAAAAAA";
    public static String encryptionKey;
 
    public static String decrypt(byte[] cipherText) throws Exception{
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "SunJCE");
        SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.DECRYPT_MODE, key,new IvParameterSpec(IV.getBytes("UTF-8")));
        
        return new String(cipher.doFinal(cipherText),"UTF-8");
    }
    
    public static String convertToString(byte[] cipher){
        String ciptext = "";
        for (int i = 0; i < cipher.length; i++) {
            ciptext += (int)cipher[i]+",";
        }
        ciptext = ciptext.substring(0,ciptext.length()-1);
        
        return ciptext;
    }
}
