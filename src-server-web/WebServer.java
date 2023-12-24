package br.com.trabalhoredes.server;

import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

final class HTTPRequest implements Runnable {

    Socket socket;
    final static String CRLF = "\r\n";

    public HTTPRequest(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try {
            processRequest();
        } catch (Exception e) {
            System.out.println();
        }
    }

    private void processRequest() throws Exception {

        //Trechos de Entrada e Saída
        InputStream is = socket.getInputStream();
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());


        //Trechos de Entrada

        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(reader);


        String requestLine = br.readLine();
        System.out.println();

        System.out.println(requestLine);

        // Obter e exibir as linhas de cabeçalho da requisição
        String headerLine;
        while ((headerLine = br.readLine()).length() != 0) {
            System.out.println(headerLine);
        }


        StringTokenizer tokens = new StringTokenizer(requestLine);
        tokens.nextToken();
        String fileName = tokens.nextToken();


        fileName = "." + fileName;


        FileInputStream fis = null;
        boolean fileExists = true;

        try {
            fis = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            fileExists = false;
        }

        String statusLine;
        String contentTypeLine;
        String entityBody = null;

        if (fileExists) {
            statusLine = "HTTP/1.1 GET 200 OK" + CRLF;
            contentTypeLine = "Content-Type: " + contentType(fileName) + CRLF;
        } else {


            statusLine = CRLF+ "HTTP/1.1 404 NOT FOUND" + CRLF;


            contentTypeLine = "Content-Type: " + contentType(fileName) + CRLF;
            entityBody = "<HTML><HEAD><TITLE>Not Found</TITTLE></HEAD>" +
                    "<BODY>Not Found</BODY></HTML>";

        }

        //Enviando Linha de Status
        os.writeBytes(statusLine);
        //Enviando Linha do Tipo de Conteúdo
        os.writeBytes(contentTypeLine);

        //Enviando uma Linha em Branco para indicar o fim das Linhas de Cabeçalho
        os.writeBytes(CRLF);

        if (fileExists) {

            sendBytes(fis, os);

            fis.close();
        } else {

            os.writeBytes(entityBody);


        }

        os.flush();
        os.close();
        br.close();
        socket.close();

    }



    private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {

        byte[] buffer = new byte[1024];
        int bytes;

        while ((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }

    }


    private static String contentType(String fileName) {
        if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
            return "text/html";
        }
        if (fileName.endsWith(".gif")) {
            return "image/gif";
        }
        if (fileName.endsWith(".jpeg") || fileName.endsWith(".jpg")) {
            return "image/jpeg";
        }
        return "application/octet-stream";
    }
}

