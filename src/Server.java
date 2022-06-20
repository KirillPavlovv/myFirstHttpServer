import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Server {

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Server is started ");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println(socket.getLocalAddress() + "  is connected");

                new Thread(() -> handleRequest(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleRequest(Socket socket) {

        try (BufferedReader input = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter output = new PrintWriter(socket.getOutputStream())

        ) {
            while (true) {
                if (input.ready()) break;
            }

            String firstLine = input.readLine();
            String[] lineParts = firstLine.split(" ");

            while (input.ready()) {
                System.out.println(input.readLine());
            }

            Path path = Paths.get(".", lineParts[1]);
            if (!Files.exists(path)) {
                output.println("HTTP/1.1 404 NOT_FOUND");
                output.println("Content-Type: text/html, charset=utf-8");
                output.println();
                output.println("<h1> URL NOT FOUND!</h1>");
                return;
            }


            output.println("HTTP/1.1 200 OK");
            output.println("Content-Type: text/html, charset=utf-8");
            output.println();

            Files.newBufferedReader(path).transferTo(output);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
