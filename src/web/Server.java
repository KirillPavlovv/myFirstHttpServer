package web;

import idnumber.EstonianIdNumber;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Server {

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Server is started ");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Somebody is connected");

                new Thread(() -> handleRequest(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleRequest(Socket socket) {

        try (BufferedReader input = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))

        ) {
            while (true) {
                if (input.ready()) break;
            }

            String firstLine = input.readLine();
            String[] lineParts = firstLine.split(" ");
            System.out.println(firstLine);
            if (firstLine.contains("idgenerator")) {
                String idNumber = generateId(lineParts[1]);
                output.write("HTTP/1.1 200 OK\n");
                output.write("Content-Type: text/html, charset=utf-8\n");
                output.write("\n");
                output.write(idNumber + "\n");
            }
            while (input.ready()) {
                System.out.println(input.readLine());
            }

            Path path = Paths.get(".", lineParts[1]);
            if (!Files.exists(path)) {
                output.write("HTTP/1.1 404 NOT_FOUND\n");
                output.write("Content-Type: text/html, charset=utf-8\n");
                output.write("\n");
                output.write("<h1> URL NOT FOUND!</h1>\n");
                output.write("<h1> ERROR 404</h1>\n");
                return;
            }
            output.write("HTTP/1.1 200 OK\n");
            output.write("Content-Type: text/html, charset=utf-8\n");
            output.write("\n");

            Files.newBufferedReader(path, StandardCharsets.UTF_8).transferTo(output);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String generateId(String parameters) {

        String[] firstSplit = parameters.split("&");
        List<String> stringList = new ArrayList<>();
        for (String s : firstSplit) {
            String[] split1 = s.split("=");
            stringList.add(split1[1]);
        }
        String gender = stringList.get(0);
        String reversedBirthday = stringList.get(1);
        String[] splitBirthday = reversedBirthday.split("-");
        StringBuilder stringBuilderBirthday = new StringBuilder();
        for (String s : splitBirthday) {
            stringBuilderBirthday.insert(0, s);
            stringBuilderBirthday.insert(0, "-");
        }
        stringBuilderBirthday.delete(0, 1);
        String birthday = stringBuilderBirthday.toString();

        return  new EstonianIdNumber(birthday, gender).generateIdNumber();
    }


}
