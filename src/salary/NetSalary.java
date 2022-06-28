package salary;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.math.BigDecimal.valueOf;
import static java.math.RoundingMode.HALF_UP;
import static salary.Constants.*;

public class NetSalary extends Salary {

    private BigDecimal netSalary;

    public NetSalary(BigDecimal netSalary) {
        this.netSalary = netSalary;
    }

    @Override
    public BigDecimal getGrossSalary() {
        return getGrossSalary(netSalary);
    }


    private BigDecimal getGrossSalary(BigDecimal salary) {
        BigDecimal grossSalary;
        if (salary.compareTo(valueOf(482)) <0) {
            grossSalary = salary.divide(valueOf(0.964), 2, HALF_UP);
        }
        else if (salary.compareTo(valueOf(1025.44)) < 0) {
            BigDecimal incomeTax = salary.subtract(maxTaxFree).multiply(getPercentage(incomeTaxPortion));
            grossSalary = salary.add(incomeTax).divide(valueOf(0.964), 2, HALF_UP);
        } else if (salary.compareTo(valueOf(1025.44)) >= 0 && salary.compareTo(valueOf(1619.52)) <= 0) {
            grossSalary = salary.subtract(valueOf(233.33333333)).divide(valueOf(0.660088), 2, RoundingMode.DOWN);
        } else {
            BigDecimal incomeTax = salary.multiply(getPercentage(incomeTaxPortion));
            grossSalary = salary.add(incomeTax).divide(valueOf(0.964), 2, HALF_UP);
        }
        return grossSalary;
    }
}
