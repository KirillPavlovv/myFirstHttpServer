package web;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Phone extends Writer {

    private Socket clientSocket;
    private BufferedReader input;
    private BufferedWriter output;
    private OutputStream fileOutput;

    public Phone(ServerSocket serverSocket) {
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

    public void createFileOutputStream() {
        try {
            fileOutput = clientSocket.getOutputStream();
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

    public int read() {
        try {
            return input.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void transfer(Path path) {
        try {
            Files.newBufferedReader(path, StandardCharsets.UTF_8).transferTo(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(char[] cbuf, int off, int len){
    }

    public void write(String message) {
        try {
            fileOutput.write((message).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(Path path) {
        try {
            fileOutput.write(Files.readAllBytes(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeOut(String message) {
        try {
            output.write(message);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void flush() {

    }

    public void close() {
        try {
            input.close();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void closeSocket() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
