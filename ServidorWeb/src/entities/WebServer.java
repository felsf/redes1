/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.Date;

/**
 *
 * @author Felipe
 */
public class WebServer {
    private ServerSocket socket;
    private String host;
    private int port;    
    
    private Thread serverThread;  
    
    private Vector<Socket> clients = new Vector<Socket>();
    
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
                            
                            String http_message = "";                            
                            String request = br.readLine();
                            File f = null;
                            
                            if(request != null) 
                            {                          
                            
                                String file = request.substring(4, request.indexOf("H"));             
                                String type = request.substring(file.length(), file.length()+3);
                                String content_type = "Content-type: ";
                                String server = "Server: kPHP WEB SERVER";

                                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));                                             
                                file = file.replaceFirst("/", "");                                                        
                                PrintStream ps = new PrintStream(client.getOutputStream()); // Cria uma Transmissão de gravação a partir do Client conectado
                                    try
                                    {
                                        f = new File("src/files/"+file);
                                        BufferedReader file_reader = new BufferedReader(new FileReader(f));
                                        switch(type)
                                        {
                                            case "txt": case "dat": case "html": case "php": case "java": case "htm": case "phtml":
                                            case "py":
                                            {
                                                content_type += "text/html";
                                                break;
                                            } 
                                            case "jpg": case "png": case "gif": case "jpeg": case "bmp": case "ico": {                                            
                                                content_type += "image/"+((type.equals("jpg") || type.equals("jpeg")) ? "jpeg" : type);                                                
                                                break; 
                                            } 
                                        }                           
                                        
                                        http_message += "HTTP/1.1 200 OK \n";
                                        http_message += content_type+"\n";
                                        http_message += "Content-Length: "+new File("src/files/"+file).length()+"\n";
                                        http_message += server+"\n";
                                        http_message += "Date: "+new Date()+" \n\n";
                                        
                                        byte[] buffer = new byte[600]; // Cria um Array de Bytes                                    
                                        FileInputStream fs = new FileInputStream(f); // Cria uma Stream de recebimento de arquivo                                    
                                        int read_bytes; // Conta até onde o buffer foi lido.                               
                                    
                                        ps.print(http_message); // Antes de gravar o conteúdo do arquivo, ele grava a HTTP RESPONSE.
                                    
                                        while((read_bytes = fs.read(buffer)) > 0) // Ele lê uma sequência de bytes até alcançar o tamanho do buffer.
                                            ps.write(buffer, 0, read_bytes); // Grava a sequência lida no PrintStreamer do Client                                                                           
                                    
                                        ps.close(); // Fecha a PrintStream do Client                                        
                                        
                                        System.out.println("Arquivo localizado: "+file);                                                           

                                    }
                                    catch(FileNotFoundException ex)
                                    {
                                        http_message += "HTTP/1.1 404 Not Found \n";
                                        http_message += content_type+"\n";
                                        http_message += server+"\n";
                                        http_message += "Date: "+new Date()+"\n\n";                                        
                                        http_message += "<center><h1>HTTP ERROR 404</h1><br><br>Arquivo nao localizado no Sistema</center>";                                                                               
                                        
                                        ps.print(http_message);                                        
                                        ps.close();                                        
                                        System.out.println("Arquivo não encontrado: "+file);                             
                                        
                                    }
                                    
                                    client.close(); // Fecha a conexão do Client.
                                }                            
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
