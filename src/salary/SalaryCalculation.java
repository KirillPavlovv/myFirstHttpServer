package salary;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static java.math.RoundingMode.HALF_UP;
import static salary.Constants.*;

public class SalaryCalculation {

    private static ResultResponse calculate(BigDecimal grossSalary) {
        ResultResponse resultResponse = new ResultResponse();
        resultResponse.setGrossSalary(grossSalary.setScale(2, HALF_UP));
        resultResponse.setFundedPension(grossSalary.multiply(getPercentage(fundedPensionRate)).setScale(2, HALF_UP));
        resultResponse.setUnEmploymentInsuranceEmployee(grossSalary.multiply(getPercentage(unemploymentInsuranceEmployeeRate)).setScale(2, HALF_UP));
        getIncomeTax(grossSalary, resultResponse);
        resultResponse.setNetSalary(grossSalary.subtract(resultResponse.getFundedPension().add(resultResponse.getUnEmploymentInsuranceEmployee().add(resultResponse.getIncomeTax()))).setScale(2, HALF_UP));
        resultResponse.setSocialTax(grossSalary.multiply(getPercentage(socialTaxRate)).setScale(2, HALF_UP));
        resultResponse.setUnemploymentInsuranceEmployer(grossSalary.multiply(getPercentage(unemploymentInsuranceEmployerRate)).setScale(2, HALF_UP));
        resultResponse.setTotalCostForEmployer(grossSalary.add(resultResponse.getSocialTax().add(resultResponse.getUnemploymentInsuranceEmployer())).setScale(2, HALF_UP));
        return resultResponse;
    }

    private static void getIncomeTax(BigDecimal grossSalary, ResultResponse resultResponse) {
        if (grossSalary.compareTo(maxTaxFree) < 0) {
            resultResponse.setIncomeTax(ZERO);
        } else if (grossSalary.compareTo(maxTaxFreeGrossSalary) < 0) {
            BigDecimal forTax = grossSalary.subtract(resultResponse.getFundedPension()
                    .add(resultResponse.getUnEmploymentInsuranceEmployee())).subtract(maxTaxFree);
            resultResponse.setIncomeTax(forTax.multiply(getPercentage(incomeTaxRate)).setScale(2, HALF_UP));
        } else if (grossSalary.compareTo(maxTaxFreeGrossSalary) >= 0 && grossSalary.compareTo(valueOf(2100)) <= 0) {
            BigDecimal monthlyTaxFreeBase = maxTaxFree.subtract(maxTaxFree.divide(valueOf(900.00), 10, HALF_UP).multiply(grossSalary.subtract(maxTaxFreeGrossSalary)));
            BigDecimal taxFreeBase = grossSalary.subtract(resultResponse.getUnEmploymentInsuranceEmployee()
                    .add(resultResponse.getFundedPension().add(monthlyTaxFreeBase)));
            resultResponse.setIncomeTax(taxFreeBase.multiply(getPercentage(incomeTaxRate)).setScale(2, HALF_UP));
        } else {
            resultResponse.setIncomeTax(grossSalary.multiply(valueOf(0.1928)).setScale(2, HALF_UP));
        }
    }

    public static ResultResponse calculate(Salary salary) {
        BigDecimal grossSalary = salary.getGrossSalary();
        return calculate(grossSalary);
    }
}