/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Algorithm;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Rikysamuel
 */
public class MAC {
    public static byte[] generateHMac(String secretKey, String data) {
        String algorithm = "HmacMD5";
        SecretKeySpec Key = new SecretKeySpec(secretKey.getBytes(), algorithm);
        try {
            Mac mac = Mac.getInstance(algorithm);
            mac.init(Key);

            return mac.doFinal(data.getBytes());
        }
        catch(InvalidKeyException e) {
            throw new IllegalArgumentException("invalid secret key provided (key not printed for security reasons!)");
        }
        catch(NoSuchAlgorithmException e) {
            throw new IllegalStateException("the system doesn't support algorithm " + algorithm, e);
        }
    }
}
