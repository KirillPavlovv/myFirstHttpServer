package web;

import org.json.JSONObject;
import salary.ResultResponse;

import java.io.IOException;

public class JsonHandler {

    static JSONObject salaryCalculationResponseToJson(ResultResponse calculationResponse) throws IOException {
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("Total costs for Employer", calculationResponse.getTotalCostForEmployer());
        jsonResponse.put("Social Tax", calculationResponse.getSocialTax());
        jsonResponse.put("Unemployment Insurance Tax for Employer", calculationResponse.getUnemploymentInsuranceEmployer());
        jsonResponse.put("Gross Salary", calculationResponse.getGrossSalary());
        jsonResponse.put("II Funded Pension", calculationResponse.getFundedPension());
        jsonResponse.put("Unemployment Insurance Tax for Employee", calculationResponse.getUnEmploymentInsuranceEmployee());
        jsonResponse.put("Income Tax", calculationResponse.getIncomeTax());
        jsonResponse.put("Net Salary", calculationResponse.getNetSalary());

        return jsonResponse;
    }
}
