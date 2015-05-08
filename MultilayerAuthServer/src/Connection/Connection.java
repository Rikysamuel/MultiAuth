/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Connection;

import Algorithm.DiffieHellman;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
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
    public static String username;
    public static String password;
    
    public static String key;
    
    public static JSONObject sendData(String args, String val){
        JSONObject jso = new JSONObject();
        switch (args){
            case "key":
                jso.put("method", "key");
                jso.put("key", val);
                return jso;
            default:
                return null;
        }
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
            
            key = (String) jso.get("key");
        } catch (ParseException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void loginData(String data){
        try {
            JSONParser jsp = new JSONParser();
            JSONObject jso = (JSONObject) jsp.parse(data);
            
            username = (String) jso.get("username");
            password = (String) jso.get("password");
            
            System.out.println("user: " + username);
            System.out.println("pass: " + password);
        } catch (ParseException ex) {
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
                
                byte[] client_bytes = new byte[1024];
                int count = input.read(client_bytes);
                String client_message = new String(client_bytes);
                client_message = client_message.substring(0, count);
                System.out.println("client_message: " + client_message);
                
                parseData(client_message);
                
                DiffieHellman.randLong();
                DiffieHellman.findK(key);
                
                String response = String.valueOf(DiffieHellman.X);
                
                System.out.println("response: " + response);
                output.write(sendData("key", response).toString().getBytes());
            }
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
}
