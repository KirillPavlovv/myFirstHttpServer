package web;

import idnumber.IdService;
import salary.ResultResponse;
import salary.SalaryCalculation;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Server {

    public static final String CONTENT_TYPE_TEXT_HTML_CHARSET_UTF_8 = "Content-Type: text/html, charset=utf-8\n";
    public static final String HTTP_200_OK = "HTTP/1.1 200 OK\n";
    public static final String EUR_BR = " EUR<br>";
    public static final String DEFAULT_PAGE = "main.html";

    public static void main(String[] args) throws InvalidPathException {
        runServer();
    }

    private static void runServer() {
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
             BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        ) {
            while (true) {
                if (input.ready()) break;
            }
            String firstLine = input.readLine();
            HttpRequest httpRequest = new HttpRequest(firstLine);

            if (httpRequest.getMethod().equals("GET")) {
                handleGetRequest(output, httpRequest);
            }

            System.out.println(firstLine);

            while (input.ready()) {
                System.out.println(input.readLine());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleGetRequest(BufferedWriter output, HttpRequest httpRequest) {
        try {
            showDefaultPage(output, httpRequest.getPath());

            if (httpRequest.getPath().contains("?")) {
                checkUrlForIdGenerator(output, httpRequest);
                checkUrlForSalaryCalculator(output, httpRequest);
            } else {
                Path path = urlNotFound(output, httpRequest.getPath());
                if (path == null) return;
                String contentType = getContentType(path);
                output.write(HTTP_200_OK);
                output.write("Content-Type: " + contentType + "\n");
                output.write("\n");
                Files.newBufferedReader(path, StandardCharsets.UTF_8).transferTo(output);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void showDefaultPage(BufferedWriter output, String path) throws IOException {
        if (path.equals("/")) {
            output.write(HTTP_200_OK);
            output.write(CONTENT_TYPE_TEXT_HTML_CHARSET_UTF_8);
            Files.newBufferedReader(Path.of(DEFAULT_PAGE), StandardCharsets.UTF_8).transferTo(output);
            output.write("\n");
            output.close();
        }
    }

    private static void checkUrlForSalaryCalculator(BufferedWriter output, HttpRequest httpRequest) throws IOException {
        if (httpRequest.getPath().contains("salarycalculator")) {
            ResultResponse calculationResponse = SalaryCalculation.calculateSalary(httpRequest);
            printSalaryCalculationResponse(output, calculationResponse);
        }
    }

    private static void checkUrlForIdGenerator(BufferedWriter output, HttpRequest httpRequest) throws IOException {
        if (httpRequest.getPath().contains("idgenerator")) {
            String idNumber = IdService.generateId(httpRequest);
            output.write(HTTP_200_OK);
            output.write(CONTENT_TYPE_TEXT_HTML_CHARSET_UTF_8);
            output.write("\n");
            output.write(idNumber + "\n");
        }
    }

    private static Path urlNotFound(BufferedWriter output, String pathString) throws IOException {
        Path path = Paths.get(".", pathString);
        if (!Files.exists(path)) {
            output.write("HTTP/1.1 404 NOT_FOUND\n");
            output.write(CONTENT_TYPE_TEXT_HTML_CHARSET_UTF_8);
            output.write("\n");
            output.write("<h1> URL NOT FOUND!</h1>\n");
            output.write("<h1> ERROR 404</h1>\n");
            return null;
        }
        return path;
    }

    private static String getContentType(Path path) throws IOException {
        return Files.probeContentType(path);
    }

    private static void printSalaryCalculationResponse(BufferedWriter output, ResultResponse calculationResponse) throws IOException {
        output.write(HTTP_200_OK);
        output.write(CONTENT_TYPE_TEXT_HTML_CHARSET_UTF_8);
        output.write("\n");
        output.write("Total costs for Employer = " + calculationResponse.getTotalCostForEmployer() + EUR_BR);
        output.write("Social Tax = " + calculationResponse.getSocialTax() + EUR_BR);
        output.write("Unemployment Insurance Tax for Employer = " + calculationResponse.getUnemploymentInsuranceEmployer() + EUR_BR);
        output.write("Gross Salary = " + calculationResponse.getGrossSalary() + EUR_BR);
        output.write("II Funded Pension = " + calculationResponse.getFundedPension() + EUR_BR);
        output.write("Unemployment Insurance Tax for Employee = " + calculationResponse.getUnEmploymentInsuranceEmployee() + EUR_BR);
        output.write("Income Tax = " + calculationResponse.getIncomeTax() + EUR_BR);
        output.write("Net Salary = " + calculationResponse.getNetSalary() + EUR_BR);
    }

}
