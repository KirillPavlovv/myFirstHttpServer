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
            String[] lineParts = firstLine.split(" ");

            if (firstLine.contains("idgenerator")) {
                String idNumber = generateId(lineParts[1]);
                output.write(HTTP_200_OK);
                output.write(CONTENT_TYPE_TEXT_HTML_CHARSET_UTF_8);
                output.write("\n");
                output.write(idNumber + "\n");
            }
            if (firstLine.contains("salarycalculator")) {
                ResultResponse calculationResponse = calculateSalary(lineParts[1]);

                printSalaryCalculationResponse(output, calculationResponse);
            }
            System.out.println(firstLine);

            while (input.ready()) {
                System.out.println(input.readLine());
            }
            Path path = urlNotFound(output, lineParts);
            if (path == null) return;
            output.write(HTTP_200_OK);
            output.write("Content-Type: text/html, charset=utf-8\n");
            output.write("\n");

            Files.newBufferedReader(path, StandardCharsets.UTF_8).transferTo(output);


        } catch (IOException e) {
            e.printStackTrace();
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
        output.write("Total costs for Employer = " + calculationResponse.getTotalCostForEmployer() + " EUR");
        output.write("<br>");
        output.write("Social Tax = " + calculationResponse.getSocialTax() + " EUR");
        output.write("<br>");
        output.write("Unemployment Insurance Tax for Employer = " + calculationResponse.getUnemploymentInsuranceEmployer() + " EUR");
        output.write("<br>");
        output.write("Gross Salary = " + calculationResponse.getGrossSalary() + " EUR");
        output.write("<br>");
        output.write("II Funded Pension = " + calculationResponse.getFundedPension() + " EUR");
        output.write("<br>");
        output.write("Unemployment Insurance Tax for Employee = " + calculationResponse.getUnEmploymentInsuranceEmployee() + " EUR");
        output.write("<br>");
        output.write("Income Tax = " + calculationResponse.getIncomeTax() + " EUR");
        output.write("<br>");
        output.write("Net Salary = " + calculationResponse.getNetSalary() + " EUR");
        output.write("<br>");
    }

    private static Path urlNotFound(BufferedWriter output, String[] lineParts) throws IOException {
        Path path = Paths.get(".", lineParts[1]);
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
