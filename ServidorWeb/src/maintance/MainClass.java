/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maintance;

import content.WebServer;
import frontend.*;
import javax.swing.*;


/**
 *
 * @author Felipe
 */
public class MainClass {
    
    public static final String host = "127.0.0.1";
    public static final int port = 8078;    
    public static WebServer server; ///= new WebServer(host, port);
    
    public static JPrincipal window ;  // Janela Principal
    public static JPrincipal confirm_dialog;
    public static JPrincipal request_history;
    public static JPrincipal client_history;
    public static JPrincipal about;
    
    public static boolean connected = false;
    
    public static Index index_panel; 
    public static RequestHistory request_panel;
    public static About about_panel;
    public static ClientHistory client_panel;
    
    /*
        Inicializando Server no através dos dados das variáveis 'host' e 'port'
    */
    
    public static void main(String[] args) throws Exception
    {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());         
        index_panel = new Index();
        request_panel = new RequestHistory();
        client_panel = new ClientHistory();
        about_panel = new About();
        
        confirm_dialog = new JPrincipal();
        confirm_dialog.setSize(300, 200);
        confirm_dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        confirm_dialog.setVisible(false);
        confirm_dialog.setLocationRelativeTo(null);
        confirm_dialog.add(new Confirm());
        confirm_dialog.setTitle("Conexão ao servidor");
        
        request_history = new JPrincipal();
        request_history.setSize(400, 300);
        request_history.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        request_history.setVisible(false);
        request_history.setLocationRelativeTo(null);
        request_history.add(request_panel);
        request_history.setTitle("Histórico de requisições.");
        
        client_history = new JPrincipal();
        client_history.setSize(400, 300);
        client_history.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        client_history.setVisible(false);
        client_history.setLocationRelativeTo(null);
        client_history.add(client_panel);
        client_history.setTitle("Histórico de conexões.");
        
        about = new JPrincipal();
        about.setSize(400, 300);
        about.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        about.setVisible(false);
        about.setLocationRelativeTo(null);
        about.add(about_panel);
        about.setTitle("About this application");
        
        window = new JPrincipal();
        window.add(index_panel);        
        
    }
    
}
