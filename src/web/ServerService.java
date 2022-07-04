package web;

import idnumber.IdService;
import org.json.JSONObject;
import salary.ResultResponse;
import salary.SalaryCalculation;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerService {


    public static void main(String[] args) throws InvalidPathException {
        runServer();
    }

    private static void runServer() {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Server is started ");

            while (true) {
                Phone phone = new Phone(serverSocket);

                new Thread(() -> handleRequest(phone)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleRequest(Phone phone) {
        phone.createStreams();
        while (true) {
            if (phone.ready()) break;
        }

        String firstLine = phone.readLine();
        HttpRequest httpRequest = new HttpRequest(firstLine);
        System.out.println(firstLine);

        if (httpRequest.getMethod().equals("GET")) {
            while (phone.ready()) {
                System.out.println(phone.readLine());
            }
            handleGetRequest(phone, httpRequest);
        }

        if (httpRequest.getMethod().equals("POST")) {
            String postContent = getPostContent(phone);
            handlePostRequest(phone, httpRequest, postContent);
        }
    }

    private static String getPostContent(Phone phone) {
        StringBuilder content = new StringBuilder();
        int c;
        while ((c = phone.read()) != -1) {
            content.append((char) c);
        }
        String[] contentParts = content.toString().split("\r\n");

        for (String contentPart : contentParts) {
            System.out.println(contentPart);
        }
        return contentParts[contentParts.length - 1];
    }

    private static void handlePostRequest(Phone phone, HttpRequest httpRequest, String postContent) {
        if (httpRequest.getPath().contains("addToList")) {
            httpRequest.setPath(postContent);
            httpRequest.setRequestParameters(phone);
            writeToFile(httpRequest, "ListOfNames.txt");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("OK", "Name is added!");
            HttpResponseService.sendJsonResponse(phone, jsonObject);

        }
    }

    private static void writeToFile(HttpRequest httpRequest, String fileName) {
        String fullName = httpRequest.parameter1 + " " + httpRequest.parameter2 + "\n";
        FileHandler file = new FileHandler(fileName);
        byte[] allBytes = file.readFile();
        file.write(allBytes);
        file.write(fullName.getBytes(StandardCharsets.UTF_8));
        file.close();
    }

    private static void handleGetRequest(Phone phone, HttpRequest httpRequest) {
        if (httpRequest.getPath().contains("?")) {
            handleRequestParameters(phone, httpRequest);
        } else if (httpRequest.getPath().equals("/")) {
            HttpResponseService.showDefaultPage(phone);
        } else {
            Path path = urlNotFound(phone, httpRequest.getPath());
            if (path == null) return;
            getAvailableFile(phone, path);
        }
    }

    private static void handleRequestParameters(Phone phone, HttpRequest httpRequest) {
        httpRequest.setRequestParameters(phone);
        if ((httpRequest.hasParameters())) {
            checkUrlForIdGenerator(phone, httpRequest);
            checkUrlForSalaryCalculator(phone, httpRequest);
        }
    }

    private static void getAvailableFile(Phone phone, Path path) {
        phone.createFileOutputStream();
        HttpResponseService.fileResponse(phone, path);
        phone.closeSocket();
    }

    private static void checkUrlForSalaryCalculator(Phone phone, HttpRequest httpRequest) {
        if (httpRequest.getPath().contains("salarycalculator")) {
            ResultResponse calculationResponse = SalaryCalculation.calculateSalary(httpRequest);
            HttpResponseService.printSalaryCalculationResponse(phone, calculationResponse);
        }
    }

    private static void checkUrlForIdGenerator(Phone phone, HttpRequest httpRequest) {
        if (httpRequest.getPath().contains("idgenerator")) {
            String idNumber = IdService.generateId(httpRequest);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Personal code", idNumber);
            HttpResponseService.sendJsonResponse(phone, jsonObject);
        }
    }

    private static Path urlNotFound(Phone phone, String pathString) {
        Path path = Paths.get(".", pathString);
        if (!Files.exists(path)) {
            return HttpResponseService.urlNorFoundError(phone);
        }
        return path;
    }
}
