package salary;

import java.math.BigDecimal;

import static java.math.BigDecimal.valueOf;
import static java.math.RoundingMode.HALF_UP;

class Constants {

    private Constants() {
    }

    public static final BigDecimal maxTaxFree = valueOf(500);
    public static final BigDecimal maxTaxFreeGrossSalary = valueOf(1200);
    public static final BigDecimal incomeTaxPortion = valueOf(25);
    public static final BigDecimal incomeTaxRate = valueOf(20);
    public static final BigDecimal socialTaxRate = valueOf(33);
    public static final BigDecimal unemploymentInsuranceEmployerRate = valueOf(0.8);
    public static final BigDecimal unemploymentInsuranceEmployeeRate = valueOf(1.6);
    public static final BigDecimal fundedPensionRate = valueOf(2);


    static BigDecimal getPercentage(BigDecimal value) {
        return value.divide(valueOf(100), 3, HALF_UP);
    }
}
