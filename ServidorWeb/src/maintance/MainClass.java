/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maintance;

import entities.*;
import java.io.IOException;
import java.net.UnknownHostException;

/**
 *
 * @author Felipe
 */
public class MainClass {
    
    public static final int port = 8078;
    public static WebServer server = new WebServer("127.0.0.1", port);            
    
    public static void main(String[] args) throws UnknownHostException, IOException
    {
        
    }
    
}
