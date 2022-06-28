package salary;

import java.math.BigDecimal;

public class GrossSalary extends Salary {

    public GrossSalary(BigDecimal grossSalary) {
        this.grossSalary = grossSalary;
    }
    private final BigDecimal grossSalary;

    @Override
    public  BigDecimal getGrossSalary() {
        return grossSalary;
    }


}
