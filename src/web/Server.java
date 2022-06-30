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

            checkUrlForIdGenerator(output, httpRequest.getPath());
            checkUrlForSalaryCalculator(output, httpRequest.getPath());

            System.out.println(firstLine);

            while (input.ready()) {
                System.out.println(firstLine);
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

    private static void checkUrlForSalaryCalculator(BufferedWriter output, String path) throws IOException {
        if (path.contains("salarycalculator")) {
            ResultResponse calculationResponse = calculateSalary(path);
            printSalaryCalculationResponse(output, calculationResponse);
        }
    }

    private static void checkUrlForIdGenerator(BufferedWriter output, String path) throws IOException {
        if (path.contains("idgenerator")) {
            String idNumber = generateId(path);
            output.write(HTTP_200_OK);
            output.write(CONTENT_TYPE_TEXT_HTML_CHARSET_UTF_8);
            output.write("\n");
            output.write(idNumber + "\n");
        }
    }

    public static String generateId(String url) {

        List<String> stringList = getParametersFromUrl(url);
        String[] splitBirthday = stringList.get(1).split("-");
        StringBuilder stringBuilderBirthday = new StringBuilder();
        for (String s : splitBirthday) {
            stringBuilderBirthday.insert(0, s);
            stringBuilderBirthday.insert(0, "-");
        }
        stringBuilderBirthday.delete(0, 1);
        String birthday = stringBuilderBirthday.toString();

        return new EstonianIdNumber(birthday, stringList.get(0)).generateIdNumber();
    }


    private static ResultResponse calculateSalary(String url) {
        List<String> parametersFromUrl = getParametersFromUrl(url);
        BigDecimal salary = new BigDecimal(parametersFromUrl.get(1));
        if (parametersFromUrl.get(0).equals("netto")) {
            return SalaryCalculation.calculate(new NetSalary(salary));
        } else if (parametersFromUrl.get(0).equals("brutto")) {
            return SalaryCalculation.calculate(new GrossSalary(salary));
        } else {
            return SalaryCalculation.calculate(new TotalCostsSalary(salary));
        }
    }

    private static List<String> getParametersFromUrl(String parameters) {
        String[] firstSplit = parameters.split("&");
        List<String> stringList = new ArrayList<>();
        for (String s : firstSplit) {
            String[] split1 = s.split("=");
            stringList.add(split1[1]);
        }
        return stringList;
    }
    private static void printSalaryCalculationResponse(BufferedWriter output, ResultResponse calculationResponse) throws IOException {
        output.write(HTTP_200_OK);
        output.write(CONTENT_TYPE_TEXT_HTML_CHARSET_UTF_8);
        output.write("\n");
        output.write("Total costs for Employer = " + calculationResponse.getTotalCostForEmployer() + " EUR<br>");
        output.write("Social Tax = " + calculationResponse.getSocialTax() + " EUR<br>");
        output.write("Unemployment Insurance Tax for Employer = " + calculationResponse.getUnemploymentInsuranceEmployer() + " EUR<br>");
        output.write("Gross Salary = " + calculationResponse.getGrossSalary() + " EUR<br>");
        output.write("II Funded Pension = " + calculationResponse.getFundedPension() + " EUR<br>");
        output.write("Unemployment Insurance Tax for Employee = " + calculationResponse.getUnEmploymentInsuranceEmployee() + " EUR<br>");
        output.write("Income Tax = " + calculationResponse.getIncomeTax() + " EUR<br>");
        output.write("Net Salary = " + calculationResponse.getNetSalary() + " EUR<br>");
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
