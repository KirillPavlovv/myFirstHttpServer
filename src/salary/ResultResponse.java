package salary;

import java.math.BigDecimal;
import java.util.Objects;


public class ResultResponse {

    BigDecimal totalCostForEmployer;
    BigDecimal socialTax;
    BigDecimal unemploymentInsuranceEmployer;
    BigDecimal grossSalary;
    BigDecimal fundedPension;
    BigDecimal unEmploymentInsuranceEmployee;
    BigDecimal incomeTax;
    BigDecimal netSalary;

    public ResultResponse() {

    }

    public ResultResponse(BigDecimal totalCostForEmployer, BigDecimal socialTax, BigDecimal unemploymentInsuranceEmployer, BigDecimal grossSalary, BigDecimal fundedPension, BigDecimal unEmploymentInsuranceEmployee, BigDecimal incomeTax, BigDecimal netSalary) {
        this.totalCostForEmployer = totalCostForEmployer;
        this.socialTax = socialTax;
        this.unemploymentInsuranceEmployer = unemploymentInsuranceEmployer;
        this.grossSalary = grossSalary;
        this.fundedPension = fundedPension;
        this.unEmploymentInsuranceEmployee = unEmploymentInsuranceEmployee;
        this.incomeTax = incomeTax;
        this.netSalary = netSalary;
    }

//    public ResultResponse(GrossSalary grossSalary) {
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResultResponse that = (ResultResponse) o;
        return Objects.equals(totalCostForEmployer, that.totalCostForEmployer) && Objects.equals(socialTax, that.socialTax) && Objects.equals(unemploymentInsuranceEmployer, that.unemploymentInsuranceEmployer) && Objects.equals(grossSalary, that.grossSalary) && Objects.equals(fundedPension, that.fundedPension) && Objects.equals(unEmploymentInsuranceEmployee, that.unEmploymentInsuranceEmployee) && Objects.equals(incomeTax, that.incomeTax) && Objects.equals(netSalary, that.netSalary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalCostForEmployer, socialTax, unemploymentInsuranceEmployer, grossSalary, fundedPension, unEmploymentInsuranceEmployee, incomeTax, netSalary);
    }

    @Override
    public String toString() {
        return "salaryCalculation.ResultResponse{" +
                "totalCostForEmployer=" + totalCostForEmployer +
                ", socialTax=" + socialTax +
                ", unemploymentInsuranceEmployer=" + unemploymentInsuranceEmployer +
                ", grossSalary=" + grossSalary +
                ", fundedPension=" + fundedPension +
                ", unEmploymentInsuranceEmployee=" + unEmploymentInsuranceEmployee +
                ", incomeTax=" + incomeTax +
                ", netSalary=" + netSalary +
                '}';
    }

    public BigDecimal getTotalCostForEmployer() {
        return totalCostForEmployer;
    }

    public void setTotalCostForEmployer(BigDecimal totalCostForEmployer) {
        this.totalCostForEmployer = totalCostForEmployer;
    }

    public BigDecimal getSocialTax() {
        return socialTax;
    }

    public void setSocialTax(BigDecimal socialTax) {
        this.socialTax = socialTax;
    }

    public BigDecimal getUnemploymentInsuranceEmployer() {
        return unemploymentInsuranceEmployer;
    }

    public void setUnemploymentInsuranceEmployer(BigDecimal unemploymentInsuranceEmployer) {
        this.unemploymentInsuranceEmployer = unemploymentInsuranceEmployer;
    }

    public BigDecimal getGrossSalary() {
        return grossSalary;
    }

    public void setGrossSalary(BigDecimal grossSalary) {
        this.grossSalary = grossSalary;
    }

    public BigDecimal getFundedPension() {
        return fundedPension;
    }

    public void setFundedPension(BigDecimal fundedPension) {
        this.fundedPension = fundedPension;
    }

    public BigDecimal getUnEmploymentInsuranceEmployee() {
        return unEmploymentInsuranceEmployee;
    }

    public void setUnEmploymentInsuranceEmployee(BigDecimal unEmploymentInsuranceEmployee) {
        this.unEmploymentInsuranceEmployee = unEmploymentInsuranceEmployee;
    }

    public BigDecimal getIncomeTax() {
        return incomeTax;
    }

    public void setIncomeTax(BigDecimal incomeTax) {
        this.incomeTax = incomeTax;
    }

    public BigDecimal getNetSalary() {
        return netSalary;
    }

    public void setNetSalary(BigDecimal netSalary) {
        this.netSalary = netSalary;
    }

}