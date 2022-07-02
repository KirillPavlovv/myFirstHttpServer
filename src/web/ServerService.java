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
        while (phone.ready()) {
            System.out.println(phone.readLine());
        }

        if (httpRequest.getMethod().equals("GET")) {
            handleGetRequest(phone, httpRequest);
        }
    }

    private static void handleGetRequest(Phone phone, HttpRequest httpRequest) {
        try {
            if (httpRequest.getPath().contains("?")) {
                handleRequestParameters(phone, httpRequest);
            } else if (httpRequest.getPath().equals("/")) {
                showDefaultPage(phone, httpRequest.getPath());
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
        checkUrlForIdGenerator(phone, httpRequest);
        checkUrlForSalaryCalculator(phone, httpRequest);
    }

    private static void getAvailableFile(Phone phone, Path path) throws IOException {
        phone.createFileOutputStream();
        String contentType = getContentType(path);
        phone.write((HTTP_200_OK));
        phone.write(("Content-Type: " + contentType + "\n"));
        phone.write(("\n"));
        phone.write(path);
        phone.flush();
        phone.getClientSocket().close();
    }

    private static void showDefaultPage(Phone phone, String path) {
            phone.writeOut(HTTP_200_OK);
            phone.writeOut(CONTENT_TYPE_TEXT_HTML_CHARSET_UTF_8);
            phone.transfer(Path.of(DEFAULT_PAGE));
            phone.writeOut("\n");

    }

    private static void checkUrlForSalaryCalculator(Phone phone, HttpRequest httpRequest) {
        if (httpRequest.getPath().contains("salarycalculator")) {
            ResultResponse calculationResponse = SalaryCalculation.calculateSalary(httpRequest);
            printSalaryCalculationResponse(phone, calculationResponse);
        }
    }

    private static void checkUrlForIdGenerator(Phone phone, HttpRequest httpRequest) {
        if (httpRequest.getPath().contains("idgenerator")) {
            String idNumber = IdService.generateId(httpRequest);
            phone.writeOut(HTTP_200_OK);
            phone.writeOut(CONTENT_TYPE_TEXT_HTML_CHARSET_UTF_8);
            phone.writeOut("\n");
            phone.writeOut(idNumber + "\n");
        }
    }

    private static Path urlNotFound(Phone phone, String pathString) {
        Path path = Paths.get(".", pathString);
        if (!Files.exists(path)) {
            phone.writeOut("HTTP/1.1 404 NOT_FOUND\n");
            phone.writeOut(CONTENT_TYPE_TEXT_HTML_CHARSET_UTF_8);
            phone.writeOut("\n");
            phone.writeOut("<h1> URL NOT FOUND!</h1>\n");
            phone.writeOut("<h1> ERROR 404</h1>\n");
            return null;
        }
        return path;
    }


    public static void badRequest(Phone phone) {
        phone.writeOut("HTTP/1.1 400 BAD_REQUEST\n");
        phone.writeOut(CONTENT_TYPE_TEXT_HTML_CHARSET_UTF_8);
        phone.writeOut("\n");
        phone.writeOut("<h1> BAD REQUEST</h1>\n");
        phone.writeOut("<h1> ERROR 400</h1>\n");
        phone.close();
    }

    private static String getContentType(Path path) throws IOException {
        return Files.probeContentType(path);
    }

    private static void printSalaryCalculationResponse(Phone phone, ResultResponse calculationResponse) {
        phone.writeOut(HTTP_200_OK);
        phone.writeOut(CONTENT_TYPE_TEXT_HTML_CHARSET_UTF_8);
        phone.writeOut("\n");
        phone.writeOut("Total costs for Employer = " + calculationResponse.getTotalCostForEmployer() + EUR_BR);
        phone.writeOut("Social Tax = " + calculationResponse.getSocialTax() + EUR_BR);
        phone.writeOut("Unemployment Insurance Tax for Employer = " + calculationResponse.getUnemploymentInsuranceEmployer() + EUR_BR);
        phone.writeOut("Gross Salary = " + calculationResponse.getGrossSalary() + EUR_BR);
        phone.writeOut("II Funded Pension = " + calculationResponse.getFundedPension() + EUR_BR);
        phone.writeOut("Unemployment Insurance Tax for Employee = " + calculationResponse.getUnEmploymentInsuranceEmployee() + EUR_BR);
        phone.writeOut("Income Tax = " + calculationResponse.getIncomeTax() + EUR_BR);
        phone.writeOut("Net Salary = " + calculationResponse.getNetSalary() + EUR_BR);
    }

}
