/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Felipe
 */
public class WebServer {
    private ServerSocket socket;
    private String host;
    private int port;
    
    private Thread serverThread;
    
    public WebServer(String host, int port) {
        this.host = host;
        this.port = port;
        
        System.out.println("Server started at "+host+" listening on Port "+port);
        
        try
        {
            socket = new ServerSocket(port);
            serverThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    while(true)
                    {
                        try
                        {
                            Socket client = socket.accept();                        
                        }
                        catch(IOException ex)
                        {
                            ex.printStackTrace();
                        }
                    }
                }
            });
            
            serverThread.start();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }   

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }    
    
    public ServerSocket getSocket() {
        return socket;
    } 
    
}
