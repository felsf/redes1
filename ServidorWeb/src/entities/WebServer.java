/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Felipe
 */
public class WebServer {
    private ServerSocket socket;
    private String host;
    private int port;
    private ArrayList<WebClient> clients = new ArrayList<WebClient>();
    
    private Thread serverThread;
    private Thread communicationThread;
    
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
                            BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));                       
                            
                            BufferedReader file_reader = null;
                            String http_message = "";
                            String file_content = "";
                            String request = br.readLine();
                            String file = request.substring(4, request.indexOf("H"));             
                            String type = request.substring(file.length(), file.length()+3);
                            String content_type = "Content-type: ";
                            String server = "jPHP WEB SERVER";
                            
                            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                            
                            switch(type)
                            {
                                case "txt": type += "text/html"; break;
                                case "jpg": case "png": case "gif": case "jpeg": case "bmp": case "ico": type += "image"; break;                                    
                            }                           
                            
                            file = file.replaceFirst("/", "");                        
                            
                            {
                                try
                                {
                                    file_reader = new BufferedReader(new FileReader("src/files/"+file));
                                    http_message += "HTTP/1.1 200 OK \n";
                                    http_message += type+"\n";
                                    http_message += server+"\n";
                                    http_message += "Date: "+new Date()+" \n\n";
                                    
                                    System.out.println("Arquivo localizado: "+file);
                                    String line;
                                    
                                    while((line = file_reader.readLine()) != null)
                                        file_content += (line + "\n");                                    
                                    
                                    http_message += file_content;
                                    
                                    bw.write(http_message);
                                    bw.flush();
                                                                      
                                }
                                catch(FileNotFoundException ex)
                                {                                    
                                    http_message += "HTTP/1.1 404 Not Found \n";
                                    http_message += type+"\n";
                                    http_message += server+"\n";
                                    http_message += "Date: "+new Date()+" \n\n";                                
                                    http_message += "<html><center><h1>HTTP ERROR 404</h1><br><br>Arquivo nao localizado no Sistema</center></html>";

                                    //System.out.println(http_message);
                                    bw.write(http_message);
                                    
                                    System.out.println("Arquivo não encontrado: "+file);
                                    //client.close();                                    
                                    //return;
                                }
                                
                                bw.flush();
                                client.close();
                            }
                            
                        }
                        catch(IOException ex)
                        {
                            ex.printStackTrace();
                        }
                    }
                }
            });
            
            communicationThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    while(true)
                    {
                        for(int a  = 0; a < clients.size(); a++)
                        {
                            /*try
                            {
                                new DataInputStream(clients.get(a).getSocket().getInputStream()).readUTF();
                            }
                            catch(IOException ex) {
                                ex.printStackTrace();
                            }*/
                        }
                    }
                }
            });
            
            serverThread.start();
            communicationThread.start();
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
