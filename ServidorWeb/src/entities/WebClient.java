/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import maintance.MainClass;
import static maintance.MainClass.port;

/**
 *
 * @author Felipe
 */
public class WebClient {
    
    private Socket socket;
    private String name;
    
    public WebClient(String name) { // Cria um Client com Nome e Socket específicos.
         this.name = name;
         
         try 
         {
             MainClass.server.addWebClient(this); // Adicionado client à 'Lista de Clients' do Servidor.        
             new Socket("127.0.0.1", MainClass.port);
         }
         catch (Exception ex) {
            ex.printStackTrace();
        }       
    }
    
    public WebClient(String name, Socket socket) {
        this.name = name;
        this.socket = socket;
        MainClass.server.addWebClient(this); // Adicionado client à 'Lista de Clients' do Servidor.        
    }

    public String getName() {
        return name;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    
    
    
    public Socket getSocket() {
        return socket;
    }
    
    
    
}
