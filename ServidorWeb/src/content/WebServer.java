/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package content;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Vector;
import javax.swing.JOptionPane;
import maintance.MainClass;

public class WebServer {
    private ServerSocket socket; // Atributo que armazena o Socket do Servidor.
    private String host; // Atributo que armazena Nome do host a ser conectado.
    private int port;    // Atributo que armazena Porta do servidor.
    private Vector<Socket> clients = new Vector<Socket>();    
    
    /*
    *   Thread principal do servidor, responsável pela espera de conexões e
    *   processamento das requisições e dos arquivos solicitados.
    */
    private Thread serverThread;         
    
    
    public WebServer(String host, int port) {
        this.host = host;
        this.port = port;                      
        
        try{socket = new ServerSocket(port); JOptionPane.showMessageDialog(null, "Successfully connected on "+host+" at port "+port+".");}
        catch(IOException e){JOptionPane.showMessageDialog(null, "Connection Error! - "+e.getMessage());}
        
        //System.out.println("Server started at "+host+" listening on Port "+port);        
        
        
        serverThread = new Thread(new Processador());
        serverThread.start();        
        
    }
    
    // ------------------------------------------------------------------------//
    
    class Processador implements Runnable
    {
        @Override
        public void run() 
        {   
            while(true)
            {     
                try
                {   
                    Socket client = socket.accept(); // Espera até que um Cliente se conecte.
                    BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));                                                  
                            
                    String http_message = ""; // Resposta HTTP a ser criada.                           
                    String request = br.readLine(); // Lê a requisição enviada para o Client.
                    File f = null;
                            
                    if(request != null) 
                    {   
                        /*
                            Requisição HTTP comum: "GET nome_do_arquivo HTTP/1.1".
                            
                            -> Aqui, pegamos da quarta posição da String (logo após o GET) até 
                               o caractere 'H' do HTTP. Logo, pegamos o que tem entre esses dois elementos, 
                               ou seja, o nome do arquivo requisitado.                            
                        */
                        String file = request.substring(4, request.indexOf("H"));
                        
                        
                        /*
                            O Java não detecta os 3 últimos caracteres da String de requisição explicitamente.
                            Logo, para obter o formato do arquivo, precisamos ler da última posição da String
                            até a última posição acrescentado de três.
                            
                            Exemplo: "File.txt" (O java só detecta "File.", então precisamos ler da última posição até o fim!)
                        */
                        String type = request.substring(file.length(), file.length()+3);
                        
                        /* String responsável por obter o Tipo do arquivo solicitado */
                        String content_type = "Content-type: ";
                        
                        /* String responsável pelo nome do Servidor operante (pode ser qualquer um). */
                        String server = "Server: kPHP WEB SERVER";                        

                        /* Objeto responsável pela transmissão dos dados do Servidor ao Client. 
                        * // Recebe como parâmetro o Objeto que irá enviar os dados ao Client.
                        */
                        PrintStream ps = new PrintStream(client.getOutputStream()); 
                        
                        /*
                            Na String do arquivo obtida da requisição HTTP GET padrão, 
                            o nome do arquivo permanece de um "/" antes deste. O que é feito aqui é a retirada
                            desta "/", deixando apenas o nome do arquivo solicitado.
                        */
                        file = file.replaceFirst("/", "");

                        try
                        {
                            // Caso nenhum arquivo seja solicitado, mostrar os arquivos disponíveis no Servidor.
                            if(file.equals(" ")) {                        
                                File arquivos[] = new File("src/files/").listFiles();
                                http_message += "HTTP/1.1 200 OK \n";                                                
                                http_message += server+"\n";
                                http_message += "Date: "+new Date()+" \n\n";
                                
                                ps.print(http_message);
                                ps.print("<body bgcolor='aqua'>");
                                ps.print("<title>Server File Index</title>");
                                ps.print("<center><h1><u>Server File Directory</u></h1>");
                                ps.print("<ul>");
                                for(File fx : arquivos) {
                                    String URL = host+":"+port+"/"+fx;
                                    String path = URL.substring(URL.lastIndexOf("\\")+1, URL.length());
                                    System.err.println(path);
                                    ps.print("<li><a href='"+path+"'>"+path.substring(path.indexOf("\\")+1, path.length())+"</a></li>");
                                }                                    
                                ps.print("</ul>");
                                ps.print("</center>");
                                ps.print("</body>");
                                ps.close();
                                client.close();
                                continue;
                            }
                            /*
                            * Procura o Arquivo no diretório dentro de um Try/Catch, que detectará
                            * se o arquivo existe ou não através da Exception a ser lançada: 'FileNotFoundException'.
                            */                                                        
                            f = new File("src/files/"+file);
                            
                            /*
                                Esta classe que será responsável por detectar se o arquivo existe ou não no Servidor.
                            */
                            BufferedReader file_reader = new BufferedReader(new FileReader(f));                            
                                
                            switch(type) // Selecionando o tipo do arquivo para a mensagem HTTP.
                            {
                                case "txt": case "dat": case "html": case "php": case "java": case "htm": case "phtml":
                                case "py": case "js":
                                {
                                    content_type += "text/html";
                                    break;
                                } 
                                case "jpg": case "png": case "gif": case "jpeg": case "bmp": case "ico": 
                                {                                            
                                    content_type += "image/"+((type.equals("jpg") || type.equals("jpeg")) ? "jpeg" : type);                                                
                                    break; 
                                } 
                            }                           
                            
                            // Criando a mensagem de Resposta HTTP.
                            /* Indica que o Arquivo foi encontrado. */           
                            http_message += "HTTP/1.1 200 OK \n";

                            /* Indica o tipo do arquivo solicitado. */
                            http_message += content_type+"\n";

                            /* Indica o tamanho do arquivo solicitado. */                            
                            http_message += "Content-Length: "+new File("src/files/"+file).length()+"\n";

                            /* Indica o nome do servidor utilizado. */
                            http_message += server+"\n";

                            /* Indica a hora da requisição. */
                            http_message += "Date: "+new Date()+" \n\n";                             
                            // ------------------------------------------------------------- //

                            byte[] buffer = new byte[600]; // Cria um Array de Bytes                                    
                                
                            /* Cria um Objeto para o recebimento do arquivo */
                            FileInputStream fs = new FileInputStream(f);      


                            int read_bytes; // Conta até onde o buffer foi lido.                               
                                
                            /*
                                Antes de processar o arquivo, ele escreve no objeto responsável pela transmissão
                                de dados ao cliente, a requisição HTTP, pois é depois que a mensagem de requisição é
                                enviada que os dados do arquivo solicitados são enviados.
                            */
                            System.out.println("Host solicitante: "+client.getLocalAddress());
                            ps.print(http_message);                             
                            /*
                                Lê até X bytes por vez, onde X é o tamanho do Array de Bytes, enquanto
                                ainda haver Bytes a serem lidos.
                                Por exemplo: um Array de Byte de tamanho 1000, e o arquivo de tamanho 2500.
                                       
                                1) Ele irá ler os primeiros 1000 bytes, sobrando 1500;
                                2) Ele irá ler os próximos 1000 bytes, sobrando 500;
                                3) Ele irá ler os últimos 500 bytes.
                            */
                            
                            while((read_bytes = fs.read(buffer)) > 0) 
                            {
                                ps.write(buffer, 0, read_bytes); // Grava a sequência lida no PrintStreamer do Client                                                                           
                            }                     
                            
                            MainClass.request_panel.addLog("Requested File: "+file+" - STATUS: 200");
                            System.out.println("Arquivo localizado: "+file);   // Printa no Console o arquivo localizado.             
                            ps.close(); // Fecha o Objeto de transmissão ao Client.                                         
                                
                            }
                            catch(FileNotFoundException ex) // Exception p/ caso o arquivo não seja encontrado.
                            {
                                /* Adiciona à mensagem HTTP o erro 404 de arquivo não localizado no Servidor. */                                
                                http_message += "HTTP/1.1 404 Not Found \n";

                                /* Adiciona o tipo do arquivo solicitado. */
                                http_message += content_type+"\n";

                                /* Adiciona o nome do servidor. */
                                http_message += server+"\n";

                                /* Adiciona a data da requisição. */
                                http_message += "Date: "+new Date()+"\n\n";                                        

                                /* Aqui adiciona-se o texto à tela informando o Erro e que o arquivo não foi localizado! */
                                http_message += "<body><title>Error de Servidor - 404</title><center><h1>HTTP ERROR 404</h1><br><br>Arquivo nao localizado no Sistema</center></body>";                                                                               
                                        
                                ps.print(http_message); // Envia a mensagem de resposta HTTP ao Client.
                                ps.close();             // Fecha o Transmissor de dados do Client.                           
                                
                                System.out.println("Arquivo não encontrado: "+file);  // Informa no Console o arquivo não encontrado.
                                MainClass.request_panel.addLog("Requested File: "+file+" - STATUS: 404"); 
                                client.close(); // Fecha a conexão com o Client em ambos os casos...                                      
                            }
                        }
                }                
                catch(IOException ex) // Exception responsável por tratar erros de Input e Output (Leitura/gravação de arquivos).
                {
                    ex.printStackTrace();
                }               
            }   
        }
    }
    
    /**
     * @return ************************************************************************/
    
    public void shutdown() throws IOException {
        socket.close();
        serverThread.stop();
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