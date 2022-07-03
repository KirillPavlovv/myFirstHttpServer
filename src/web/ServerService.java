package web;

import idnumber.IdService;
import salary.ResultResponse;
import salary.SalaryCalculation;

import java.io.IOException;
import java.net.ServerSocket;
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
            int contentLength = 0;
            while (phone.ready()) {
//            String line = phone.readLine();
//            System.out.println(line);
//            int c;
//            while ((c = phone.read()) != -1) {
//                System.out.println((char)c);
//            }

                String line = phone.readLine();
                System.out.println(line);
                if (line.contains("Content-Length")) {
                    String[] contentLengthLine = line.split(" ");
                    contentLength = Integer.parseInt(contentLengthLine[1]);
                }
            }
            handleGetRequest(phone, httpRequest);
        }
        if (httpRequest.getMethod().equals("POST")) {
            handlePostRequest(phone, httpRequest);
        }
    }

    private static void handlePostRequest(Phone phone, HttpRequest httpRequest) {
        if (httpRequest.getPath().contains("addToList")) {

        }
    }

    private static void handleGetRequest(Phone phone, HttpRequest httpRequest) {
        try {
            if (httpRequest.getPath().contains("?")) {
                handleRequestParameters(phone, httpRequest);
            } else if (httpRequest.getPath().equals("/")) {
                HttpResponseService.showDefaultPage(phone);
            } else {
                Path path = urlNotFound(phone, httpRequest.getPath());
                if (path == null) return;
                getAvailableFile(phone, path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleRequestParameters(Phone phone, HttpRequest httpRequest) {
        httpRequest.setRequestParameters(phone);
        if ((httpRequest.hasParameters())) {
            checkUrlForIdGenerator(phone, httpRequest);
            checkUrlForSalaryCalculator(phone, httpRequest);
        }
    }

    private static void getAvailableFile(Phone phone, Path path) throws IOException {
        phone.createFileOutputStream();
        HttpResponseService.fileResponse(phone, path);
        phone.getClientSocket().close();
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
            HttpResponseService.idGeneratorResponse(phone, idNumber);
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
