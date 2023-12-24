package br.com.trabalhoredes.server;

import java.net.ServerSocket;
import java.net.Socket;

public class WebServerTest {
    public static void main(String[] args) throws Exception{

        ServerSocket server = new ServerSocket(5001);
        System.out.println("Iniciando Servidor");

        while (true) {
            Socket client = server.accept();

            HTTPRequest request = new HTTPRequest(client);

            System.out.println("Cliente Conectado");

            Thread t1 = new Thread(request);
            t1.start();
        }
    }
}
