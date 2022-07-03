package web;

import org.json.JSONObject;
import salary.ResultResponse;

import java.nio.file.Path;

public class HttpResponseService {


    public static final String CONTENT_TYPE_TEXT_HTML_CHARSET_UTF_8 = "Content-Type: text/html, charset=utf-8\n";
    public static final String HTTP_200_OK = "HTTP/1.1 200 OK\n";
    public static final String DEFAULT_PAGE = "main.html";

    static void fileResponse(Phone phone, Path path) {
        phone.write((HTTP_200_OK));
        phone.write(Headers.createHeaders(path));
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

    static void printSalaryCalculationResponse(Phone phone, ResultResponse calculationResponse) {
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("Total costs for Employer", calculationResponse.getTotalCostForEmployer());
        jsonResponse.put("Social Tax", calculationResponse.getSocialTax());
        jsonResponse.put("Unemployment Insurance Tax for Employer", calculationResponse.getUnemploymentInsuranceEmployer());
        jsonResponse.put("Gross Salary", calculationResponse.getGrossSalary());
        jsonResponse.put("II Funded Pension", calculationResponse.getFundedPension());
        jsonResponse.put("Unemployment Insurance Tax for Employee", calculationResponse.getUnEmploymentInsuranceEmployee());
        jsonResponse.put("Income Tax", calculationResponse.getIncomeTax());
        jsonResponse.put("Net Salary", calculationResponse.getNetSalary());

        phone.writeOut(HTTP_200_OK);
        phone.writeOut("Content-Type: application/json\n");
        phone.writeOut("\n");
        phone.writeOut(jsonResponse.toString());
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
