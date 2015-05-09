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
    static String plainText;
    public static String encryptionKey;
    
    public static void setPlaintext(String _plainText) {
        StringBuilder concat = new StringBuilder(_plainText);
        
        while (concat.length() % 16 != 0) {
            concat.append("\0");
        }
        plainText = concat.toString();
    }
    
    public static byte[] encrypt() throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "SunJCE");
        SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key,new IvParameterSpec(IV.getBytes("UTF-8")));
        
        return cipher.doFinal(plainText.getBytes("UTF-8"));
    }
    
    public static String convertToString(byte[] cipher){
        String ciptext = "";
        for (int i = 0; i < cipher.length; i++) {
            ciptext += (int)cipher[i]+",";
        }
        ciptext = ciptext.substring(0,ciptext.length()-1);
        
        return ciptext;
    }
  
    public static void main(String [] args) {
        try {
            System.out.println("==Java==");
            System.out.println("plain:   " + plainText);

            byte[] cipher = encrypt();

            System.out.print("cipher:  ");
            for (int i=0; i<cipher.length; i++){
                System.out.print((int) cipher[i]+" ");
            }

        } catch (Exception e) {
            System.err.println(e);
        } 
    }
}
