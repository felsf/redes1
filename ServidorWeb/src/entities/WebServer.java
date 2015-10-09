/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import static maintance.MainClass.port;

/**
 *
 * @author Felipe
 */
public class WebServer {
    private ServerSocket socket;
    private String host;
    private int port;
    private ArrayList<WebClient> clients = new ArrayList<WebClient>();
    
    private int connected = 0;
    private Thread serverThread;
    
    public WebServer(String host, int port) {
        this.host = host;
        this.port = port;
        
        System.out.println("Server started at "+host+" listening on Port "+port);
        
        try
        {
            socket = new ServerSocket(port);
            serverThread = new Thread(new Runnable() {
                   
                /*
                    Thread necessário para que múltiplos clientes possam se conectar.
                */
                
                @Override
                public void run() {                                       
                    while(true)
                    {
                        try
                        {
                            Socket client = socket.accept(); // Espera até que um Cliente se conecte.
                            connected++;       
                            
                            while(connected != clients.size()) {
                                new WebClient("WebClient", client);
                            }
                            
                            WebClient wc = clients.get(clients.size() - 1);
                            wc.setSocket(client);
                            System.out.println("Client '"+wc.getName()+"' connected");                            
                            //WebClient wc = new WebClient(connected.getName(), client);                    
                            
                            
                            //System.out.println("Requ: "+new DataInputStream(client.getInputStream()).readUTF());
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
    
    public void addWebClient(WebClient webClient) {        
        clients.add(webClient);
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
