package web;

import org.json.JSONObject;
import salary.ResultResponse;

import java.io.IOException;
import java.nio.file.Path;

public class Response {

    private static final String CONTENT_TYPE_TEXT_HTML_CHARSET_UTF_8 = "Content-Type: text/html, charset=utf-8\n";
    private static final String HTTP_200_OK = "HTTP/1.1 200 OK\n";
    private static final String DEFAULT_PAGE = "main.html";

    static void fileResponse(SocketService socketService, Path path) throws IOException {
        socketService.write((HTTP_200_OK));
        socketService.write(ResponseHeaders.createHeaders(path));
        socketService.write(path);
    }

    static void showDefaultPage(SocketService socketService) {
        socketService.writeOut(HTTP_200_OK);
        socketService.writeOut(CONTENT_TYPE_TEXT_HTML_CHARSET_UTF_8);
        socketService.writeOut("\n");
        socketService.transfer(Path.of(DEFAULT_PAGE));
    }

    static void badRequest(SocketService socketService) {
        socketService.writeOut("HTTP/1.1 400 BAD_REQUEST\n");
        socketService.writeOut(CONTENT_TYPE_TEXT_HTML_CHARSET_UTF_8);
        socketService.writeOut("\n");
        socketService.writeOut("<h1> BAD REQUEST</h1>\n");
        socketService.writeOut("<h1> ERROR 400</h1>\n");
    }

    static Path urlNotFoundError(SocketService socketService) {
        socketService.writeOut("HTTP/1.1 404 NOT_FOUND\n");
        socketService.writeOut(CONTENT_TYPE_TEXT_HTML_CHARSET_UTF_8);
        socketService.writeOut("\n");
        socketService.writeOut("<h1> URL NOT FOUND!</h1>\n");
        socketService.writeOut("<h1> ERROR 404</h1>\n");
        return null;
    }

    static void printSalaryCalculationResponse(SocketService socketService, ResultResponse calculationResponse) {
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("Total costs for Employer", calculationResponse.getTotalCostForEmployer());
        jsonResponse.put("Social Tax", calculationResponse.getSocialTax());
        jsonResponse.put("Unemployment Insurance Tax for Employer", calculationResponse.getUnemploymentInsuranceEmployer());
        jsonResponse.put("Gross Salary", calculationResponse.getGrossSalary());
        jsonResponse.put("II Funded Pension", calculationResponse.getFundedPension());
        jsonResponse.put("Unemployment Insurance Tax for Employee", calculationResponse.getUnEmploymentInsuranceEmployee());
        jsonResponse.put("Income Tax", calculationResponse.getIncomeTax());
        jsonResponse.put("Net Salary", calculationResponse.getNetSalary());

        sendJsonResponse(socketService, jsonResponse);
    }

    static void sendJsonResponse(SocketService socketService, JSONObject jsonObject) {
        socketService.writeOut((HTTP_200_OK));
        socketService.writeOut("Content-Type: application/json\n");
        socketService.writeOut("\n");
        socketService.writeOut(jsonObject.toString());
    }


}
