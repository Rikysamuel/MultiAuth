/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Connection;

import Algorithm.AES;
import Algorithm.DiffieHellman;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    public static JSONObject login(String username, String password){
        JSONObject jso = new JSONObject();
        jso.put("method", "login");
        jso.put("username", username);
        jso.put("password", password);
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
                    String key = (String) jso.get("value");
                    secretKey = DiffieHellman.findK(key);
                    break;
                case "login_resp":
                    System.out.println(jso.get("value"));
                    break;
                default:
                    break;
            }
        } catch (ParseException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args) {
        try {
            String ip = "localhost";
            int port = 1234;
            
            DiffieHellman.randLong();
            
            if (Connection.connectToServer(ip, port)){
                Connection.send(Connection.key((DiffieHellman.X).toString()).toString());
            }
            
            AES.encryptionKey = secretKey;
            AES.setPlaintext("mypassword");
            String pass = AES.convertToString(AES.encrypt());
            
            if (Connection.connectToServer(ip, port)){
                Connection.send(Connection.login("myusername", pass).toString());
            }
        } catch (Exception ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
