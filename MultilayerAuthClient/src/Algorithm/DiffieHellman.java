/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Algorithm;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rikysamuel
 */
public class DiffieHellman {
    public static BigInteger g;
    public static BigInteger n;
    public static BigInteger X;
    public static BigInteger _x;
    public static BigInteger K;
    
    public static long seed(){
        try {
            String str = new SimpleDateFormat("MMM dd yyy").format(Calendar.getInstance().getTime()) + " 00:00:00 GMT";
            SimpleDateFormat df = new SimpleDateFormat("MMM dd yyyy HH:mm:ss zzz");
            Date date = df.parse(str);
            long epoch = date.getTime();
            System.out.println("Seeder: " + epoch);
            
            return epoch;
        } catch (ParseException ex) {
            Logger.getLogger(DiffieHellman.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    public static void randLong(){
        long x = 0000000000000000L;
        long y = 9999999999999999L;
        Random rand = new Random(DiffieHellman.seed());
        
        g = BigInteger.valueOf(rand.nextLong() % (y - x));
        if (g.signum()==-1){
            g = g.negate();
        }
        
        n = BigInteger.valueOf(rand.nextLong() % (y - x));
        if (n.signum()==-1){
            n = n.negate();
        }
        
        rand = new Random();
        _x = BigInteger.valueOf(rand.nextLong() % (y - x));
        if (_x.signum()==-1){
            _x = _x.negate();
        }
        
        System.out.println("g: " + g);
        System.out.println("n: " + n);
        X = g.modPow(_x, n);
        System.out.println("X: " + X);
    }
    
    public static String findK(String Y){
        K = new BigInteger(Y).modPow(_x, n);
        System.out.println("K is " + K);
        
        return K.toString();
    }
}
