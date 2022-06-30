package web;

import idnumber.EstonianIdNumber;
import salary.*;

import java.io.*;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Server {

    public static final String CONTENT_TYPE_TEXT_HTML_CHARSET_UTF_8 = "Content-Type: text/html, charset=utf-8\n";
    public static final String HTTP_200_OK = "HTTP/1.1 200 OK\n";
    public static final String EUR_BR = " EUR<br>";

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
             BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))

        ) {
            while (true) {
                if (input.ready()) break;
            }

            String firstLine = input.readLine();
            HttpRequest httpRequest = new HttpRequest(firstLine);

            showMainPage(output, httpRequest.getPath());

            checkUrlForIdGenerator(output, httpRequest);
            checkUrlForSalaryCalculator(output, httpRequest);

            System.out.println(firstLine);

            while (input.ready()) {
                System.out.println(input.readLine());
            }

            Path path = urlNotFound(output, httpRequest.getPath());
            if (path == null) return;
            output.write(HTTP_200_OK);
            output.write(CONTENT_TYPE_TEXT_HTML_CHARSET_UTF_8);
            output.write("\n");

            Files.newBufferedReader(path, StandardCharsets.UTF_8).transferTo(output);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void showMainPage(BufferedWriter output, String path) throws IOException {
        if (path.equals("/")) {
            output.write(HTTP_200_OK);
            output.write(CONTENT_TYPE_TEXT_HTML_CHARSET_UTF_8);
            Files.newBufferedReader(Path.of("main.html"), StandardCharsets.UTF_8).transferTo(output);
            output.write("\n");
            output.close();
        }
    }

    private static void checkUrlForSalaryCalculator(BufferedWriter output, HttpRequest httpRequest) throws IOException {
        if (httpRequest.getPath().contains("salarycalculator")) {
            ResultResponse calculationResponse = calculateSalary(httpRequest);
            printSalaryCalculationResponse(output, calculationResponse);
        }
    }

    private static void checkUrlForIdGenerator(BufferedWriter output, HttpRequest httpRequest) throws IOException {
        if (httpRequest.getPath().contains("idgenerator")) {
            String idNumber = generateId(httpRequest);
            output.write(HTTP_200_OK);
            output.write(CONTENT_TYPE_TEXT_HTML_CHARSET_UTF_8);
            output.write("\n");
            output.write(idNumber + "\n");
        }
    }

    public static String generateId(HttpRequest httpRequest) {

        String[] splitBirthday = httpRequest.getParameter2().split("-");
        StringBuilder stringBuilderBirthday = new StringBuilder();
        for (String s : splitBirthday) {
            stringBuilderBirthday.insert(0, s);
            stringBuilderBirthday.insert(0, "-");
        }
        stringBuilderBirthday.delete(0, 1);
        String birthday = stringBuilderBirthday.toString();

        return new EstonianIdNumber(birthday, httpRequest.getParameter1()).generateIdNumber();
    }


    private static ResultResponse calculateSalary(HttpRequest httpRequest) {
        BigDecimal salary = new BigDecimal(httpRequest.getParameter2());
        if (httpRequest.getParameter1().equals("netto")) {
            return SalaryCalculation.calculate(new NetSalary(salary));
        } else if (httpRequest.getParameter1().equals("brutto")) {
            return SalaryCalculation.calculate(new GrossSalary(salary));
        } else {
            return SalaryCalculation.calculate(new TotalCostsSalary(salary));
        }
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

    private static Path urlNotFound(BufferedWriter output, String pathString) throws IOException {
        Path path = Paths.get(".", pathString);
        if (pathString.contains("?")) {
            return null;
        }
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

}
