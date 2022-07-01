package web;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Phone extends Writer {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader input;
    private BufferedWriter output;

    public Phone(String port) {
        try {
            serverSocket = new ServerSocket(Integer.parseInt(port));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Phone (String ip, String port) {
        try {
            clientSocket = new Socket(ip, Integer.parseInt(port));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void accept() {
        try {
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Somebody is connected");

    }

    public void createStreams() {
        try {
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readLine() {
        try {
            return input.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean ready() {
        try {
            return input.ready();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void transfer(Path path) {
        try {
            Files.newBufferedReader(path, StandardCharsets.UTF_8).transferTo(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeLine(String message) {
        try {
            output.write(message);
//            output.newLine();
//            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {

    }

    @Override
    public void flush() throws IOException {

    }

    public void close() {
        try {
            input.close();
            output.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
