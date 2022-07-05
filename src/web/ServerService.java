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
            String [] postContent = getPostContent(phone);
            if (httpRequest.getPath().contains("addToList")) {
                handlePostRequest(phone, httpRequest, postContent[1]);
            }
            if (httpRequest.getPath().contains("addPicture")) {
                addPicturePostRequest(phone, httpRequest,postContent);
            }
        }
    }

    private static void addPicturePostRequest(Phone phone, HttpRequest httpRequest, String[] postContent) {
        String filename = "";
        String[] fileDataLines = postContent[1].split("\r\n");
        for (String fileDataLine : fileDataLines) {
            if (fileDataLine.contains("Content-Disposition")) {
                String[] lineParts = fileDataLine.split(" ");
                for (String linePart : lineParts) {
                    if (linePart.contains("filename")) {
                        String[] filenameContent = linePart.split("=");
                        filename = filenameContent[1];
                        break;
                    }
                }
            }


        }

    }

    private static String [] getPostContent(Phone phone) {
        StringBuilder content = new StringBuilder();
        int c;
        while ((c = phone.read()) != -1) {
            content.append((char) c);
        }
        return  content.toString().split("\r\n\r\n");
    }

    private static void handlePostRequest(Phone phone, HttpRequest httpRequest, String postContent) {
        if (httpRequest.getPath().contains("addToList")) {
            addToListPostRequest(phone, httpRequest, postContent);
        }
    }

    private static void addToListPostRequest(Phone phone, HttpRequest httpRequest, String postContent) {
        httpRequest.setPath(postContent);
        httpRequest.setRequestParameters(phone);
        writeToFile(httpRequest, "ListOfNames.txt");
    }

    private static void writeToFile(HttpRequest httpRequest, String fileName) {
        String fullName = httpRequest.parameter1 + " " + httpRequest.parameter2 + "\n";
        FileHandler fileHandler = new FileHandler(fileName);
        byte[] bytes = fileHandler.readFile();

        fileHandler.write(bytes);

        fileHandler.write(fullName.getBytes(StandardCharsets.UTF_8));
        fileHandler.close();
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
            handleGetPersonalCodeGenerator(phone, httpRequest);
            handleGetSalaryCalculator(phone, httpRequest);
        }
    }

    private static void getAvailableFile(Phone phone, Path path) {
        phone.createFileOutputStream();
        HttpResponseService.fileResponse(phone, path);
        phone.closeSocket();
    }

    private static void handleGetSalaryCalculator(Phone phone, HttpRequest httpRequest) {
        if (httpRequest.getPath().contains("salarycalculator")) {
            ResultResponse calculationResponse = SalaryCalculation.calculateSalary(httpRequest);
            HttpResponseService.printSalaryCalculationResponse(phone, calculationResponse);
        }
    }

    private static void handleGetPersonalCodeGenerator(Phone phone, HttpRequest httpRequest) {
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
            return HttpResponseService.urlNotFoundError(phone);
        }
        return path;
    }
}
