package web;

import salary.ResultResponse;

import java.nio.file.Path;

public class HttpResponseService {


    public static final String CONTENT_TYPE_TEXT_HTML_CHARSET_UTF_8 = "Content-Type: text/html, charset=utf-8\n";
    public static final String HTTP_200_OK = "HTTP/1.1 200 OK\n";
    public static final String EUR_BR = " EUR<br>";
    public static final String DEFAULT_PAGE = "main.html";

    static void printSalaryCalculationResponse(Phone phone, ResultResponse calculationResponse) {
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
        phone.close();
    }

    static void badRequest(Phone phone) {
        phone.writeOut("HTTP/1.1 400 BAD_REQUEST\n");
        phone.writeOut(CONTENT_TYPE_TEXT_HTML_CHARSET_UTF_8);
        phone.writeOut("\n");
        phone.writeOut("<h1> BAD REQUEST</h1>\n");
        phone.writeOut("<h1> ERROR 400</h1>\n");
        phone.close();
    }

    static Path urlNorFoundError(Phone phone) {
        phone.writeOut("HTTP/1.1 404 NOT_FOUND\n");
        phone.writeOut(CONTENT_TYPE_TEXT_HTML_CHARSET_UTF_8);
        phone.writeOut("\n");
        phone.writeOut("<h1> URL NOT FOUND!</h1>\n");
        phone.writeOut("<h1> ERROR 404</h1>\n");
        phone.close();
        return null;
    }

    static void fileResponse(Phone phone, Path path, String contentType) {
        phone.write((HTTP_200_OK));
        phone.write(("Content-Type: " + contentType + "\n"));
        phone.write(("\n"));
        phone.write(path);
        phone.flush();
    }


    static void showDefaultPage(Phone phone) {
        phone.writeOut(HTTP_200_OK);
        phone.writeOut(CONTENT_TYPE_TEXT_HTML_CHARSET_UTF_8);
        phone.transfer(Path.of(DEFAULT_PAGE));
        phone.writeOut("\n");
        phone.close();

    }

    static void idGeneratorResponse(Phone phone, String idNumber) {
        phone.writeOut(HTTP_200_OK);
        phone.writeOut(CONTENT_TYPE_TEXT_HTML_CHARSET_UTF_8);
        phone.writeOut("\n");
        phone.writeOut(idNumber + "\n");
        phone.close();
    }


}
