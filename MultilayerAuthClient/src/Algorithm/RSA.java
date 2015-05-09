/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Algorithm;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rikysamuel
 */
public class RSA {
    public static BigInteger p;
    public static BigInteger q;
    public static BigInteger n;
    public static BigInteger phi;
    public static BigInteger e;
    public static BigInteger d;
    
    public static void generateKey(){
        Random rand = new Random();
        
        p = BigInteger.probablePrime(1024, rand);
        q = BigInteger.probablePrime(1024, rand);
        n = p.multiply(q);
        phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        e = BigInteger.probablePrime(512, rand);
        
        while(phi.gcd(e).compareTo(BigInteger.ONE)>0 && e.compareTo(phi) < 0) {
            e.add(BigInteger.ONE);
        }
        
        d = e.modInverse(phi);
    }
    
    public static void saveToFile(){
        try {
            try (PrintWriter writer = new PrintWriter("key.txt", "UTF-8")) {
                writer.println(e);
                writer.println(n);
                writer.println(d);
            }
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void readFromFile(String filename){
        List<String> in = new ArrayList<>();
        try {
            Scanner input = new Scanner(new FileReader(filename));
            while (input.hasNext()){
                in.add(input.next());
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RSA.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (!in.isEmpty()) {
            e = new BigInteger(in.get(0));
            n = new BigInteger(in.get(1));
            d = new BigInteger(in.get(2));
        }
    }
    
    public static byte[] encrypt(byte[] message, String targetE, String targetN) {
        return (new BigInteger(message)).modPow(new BigInteger(targetE), new BigInteger(targetN)).toByteArray();
    }
    
    public static byte[] decrypt(byte[] cipher) {
        return (new BigInteger(cipher)).modPow(d, n).toByteArray();
    }
}
