/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.net.Socket;
import maintance.MainClass;

/**
 *
 * @author Felipe
 */
public class WebClient {
    
    private Socket socket;
    private String name;
    
    public WebClient(String name, Socket socket) { // Cria um Client com Nome e Socket específicos.
        this.socket = socket;
        this.name = name;
        
        MainClass.server.addWebClient(this); // Adicionado client à 'Lista de Clients' do Servidor.
        System.out.println("Client '"+name+"' connected to "+socket.getInetAddress()+" on Port "+socket.getPort());              
    }

    public String getName() {
        return name;
    }

    public Socket getSocket() {
        return socket;
    }
    
    
    
}
