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
import java.net.ServerSocket;
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
    public static String response;
    public static String filename = "key.txt";
    public static String testString = "hello world";
    
    public static String username;
    public static String password;    
    public static String secretKey;
    
    public static JSONObject sendData(String args, Object val){
        JSONObject jso = new JSONObject();
        jso.put("method", args);
        jso.put("value", (Object) val);
        
        return jso;
    }
    
    public static void parseData(String data){
        try {
            JSONParser jsp = new JSONParser();
            JSONObject jso = (JSONObject) jsp.parse(data);
            
            String method = (String) jso.get("method");
            switch (method) {
                case "login":
                    loginData(data);
                    break;
                case "key":
                    key(data);
                    break;
                default:
                    break;
            }
        } catch (ParseException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void key (String data){
        try {
            JSONParser jsp = new JSONParser();
            JSONObject jso = (JSONObject) jsp.parse(data);
            
            secretKey = (String) jso.get("key");
            
            DiffieHellman.randLong();
            secretKey = DiffieHellman.findK(secretKey);
            AES.encryptionKey = secretKey;
            AES.setPlaintext(testString);
            
            JSONArray resp = new JSONArray();
            resp.add(String.valueOf(DiffieHellman.X));
            resp.add(AES.convertToString(AES.encrypt()));
            
            response = sendData("key", resp).toString();
            System.out.println(response);
        } catch (ParseException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void loginData(String data) {
        String user = "myusername";
        String pass = "mypassword";
        try {
            JSONParser jsp = new JSONParser();
            JSONObject jso = (JSONObject) jsp.parse(data);
            
            username = (String) jso.get("username");
            password = (String) jso.get("password");
            String str = (String) jso.get("string");
            String mac = (String) jso.get("mac");
            
            if (str.equals(testString)) {
                System.out.println("String match!");
                String matchMac = AES.convertToString(MAC.generateHMac(secretKey, pass));
            
                if (mac.equals(matchMac)){
                    System.out.println("MAC match!");
                    processLogin(user, pass);
                } else{
                    response = "Login Failed";
                    System.out.println("response: " + Connection.response);
                    response = sendData("login_resp", Connection.response).toString();
                }
            } else{
                response = "Login Failed";
                System.out.println("response: " + Connection.response);
                response = sendData("login_resp", Connection.response).toString();
            }
        } catch (ParseException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void processLogin(String user, String pass) {
        try {
            String[] cipherText = password.split(",");
            byte[] cipherBlock = new byte[cipherText.length];
            for (int i = 0; i < cipherText.length; i++) {
                cipherBlock[i] = (byte) Integer.parseInt(cipherText[i]);
            }
            
            RSA.readFromFile(filename);
            password = new String(RSA.decrypt(cipherBlock));
            
            System.out.println("user: " + username);
            System.out.println("pass: " + password);
            
            if (username.equals(user)){
                if (password.equals(pass)){
                    response = "Login Success!";
                } else{
                    response = "Login Failed!";
                }
            }
            System.out.println("response: " + Connection.response);
            
            response = sendData("login_resp", Connection.response).toString();
        } catch (Exception ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args) {
        try 
        {
            int port = 1234;
            
            System.out.println("Server is listening on localhost:" + port);
            ServerSocket serverSocket = new ServerSocket(port);
            
            while(true)
            {
                Socket connectionSocket = serverSocket.accept();
                InputStream input = connectionSocket.getInputStream();
                DataOutputStream output = new DataOutputStream(connectionSocket.getOutputStream());
                
                byte[] client_bytes = new byte[2048];
                int count = input.read(client_bytes);
                String client_message = new String(client_bytes);
                client_message = client_message.substring(0, count);
                System.out.println("client_message: " + client_message);
                
                parseData(client_message);
                output.write(response.getBytes());
            }
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
}
