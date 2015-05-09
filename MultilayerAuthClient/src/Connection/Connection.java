/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Connection;

import Algorithm.AES;
import Algorithm.DiffieHellman;
import Algorithm.MAC;
import Algorithm.RSA;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Rikysamuel
 */
public class Connection {
    public static Socket clientSocket;
    public static String secretKey;
    public static String testString;
    
    public static boolean connectToServer(String ip, int port){
        try {
            clientSocket = new Socket(InetAddress.getByName(ip), port);
            return true;
        } catch (IOException ex) {
            System.out.println("Can't connect to the Server!");
        }
        return false;
    }
    
    public static boolean closeConnection(){
        try {
            clientSocket.close();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public static String send(String sentence){
        try {
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            outToServer.write(sentence.getBytes());
            
            InputStream response = clientSocket.getInputStream();
            
            byte[] resp_byte = new byte[1024];
            int resp_size = response.read(resp_byte);
            String resp = new String(resp_byte).trim();
            System.out.println("response: " + resp);
            
            responseHandler(resp);
            return resp;
        } catch (IOException ex) {
            System.out.println("Disconnected From Server");
        }
        return null;
    }
    
    public static JSONObject login(String username, String password, String testString, String mac){
        JSONObject jso = new JSONObject();
        jso.put("method", "login");
        jso.put("username", username);
        jso.put("password", password);
        jso.put("string", testString);
        jso.put("mac", mac);
        return jso;
    }
    
    public static JSONObject key(String key){
        JSONObject jso = new JSONObject();
        jso.put("method", "key");
        jso.put("key", key);
        return jso;
    }
    
    public static void responseHandler(String response){
        try {
            JSONParser jsp = new JSONParser();
            JSONObject jso = (JSONObject) jsp.parse(response);
            
            String method = (String) jso.get("method");
            switch (method) {
                case "key":
                    JSONArray key = (JSONArray) jso.get("value");
                    secretKey = DiffieHellman.findK((String) key.get(0));
                    AES.encryptionKey = secretKey;
                    
                    String str = (String) key.get(1);
                    String[] cipherText = str.split(",");
                    byte[] cipher = new byte[cipherText.length];
                    for (int i = 0; i < cipherText.length; i++) {
                        cipher[i] = (byte) Integer.parseInt(cipherText[i]);
                    }
                    
                    testString = AES.decrypt(cipher).trim();
                    break;
                case "login_resp":
                    System.out.println(jso.get("value"));
                    break;
                default:
                    break;
            }
        } catch (ParseException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args) {
        String username = "myusername";
        String password = "mypassword";
        
        String eServer = "12530547628532101213784755520521260626540817453941412140841363954376475768077782794280758640921115810363180036043030987660805878529823484809629972655195579";
        String nServer = "10158813192846843016697214635454423681865169060170987547963326733618961092734558942310427192126610702535505923536529253339530994869147535177510744206050792525069634949298566903393028808885935714478669227060871342986316021848392607023671426459160411024989637344989050165716923892159976814353680707455700216526010464515114097605644517842996440956176207929557779292017459610701715608652678468493129599867526265650408024861240941458302387308956914800049896521531609370360498955328262618450964585901952254526678332300082786172601322827107974821125582390035653243468797182521814816817044580657632564424910041912125228342343";
        
        
        try {
            String ip = "localhost";
            int port = 1234;
            
            DiffieHellman.randLong();
            
            if (Connection.connectToServer(ip, port)){
                Connection.send(Connection.key((DiffieHellman.X).toString()).toString());
            }
            
            String encryptedPassword = AES.convertToString(RSA.encrypt(password.getBytes(), eServer, nServer));
            
            if (Connection.connectToServer(ip, port)){
                byte[] mac = MAC.generateHMac(secretKey, password);
                String macString = AES.convertToString(mac);
                Connection.send(Connection.login(username, encryptedPassword, testString, macString).toString());
            }
        } catch (Exception ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
