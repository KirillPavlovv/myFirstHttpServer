package web;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.InvalidPathException;

public class ServerService {


    public static void main(String[] args) throws InvalidPathException {
        runServer();
    }

    private static void runServer() {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Server is started ");

            while (true) {
                SocketService socketService = new SocketService(serverSocket.accept());

                new Thread(socketService::handleRequest).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
