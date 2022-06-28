package salary;

import java.math.BigDecimal;

import static java.math.BigDecimal.valueOf;
import static java.math.RoundingMode.HALF_UP;
import static salary.Constants.*;

public class TotalCostsSalary extends Salary{

    private BigDecimal totalExpense;

    public TotalCostsSalary(BigDecimal totalExpense) {
        this.totalExpense = totalExpense;
    }

    @Override
    public BigDecimal getGrossSalary() {
        return getGrossSalary(totalExpense);
    }


    public BigDecimal getGrossSalary(BigDecimal salary) {
        return salary.divide(valueOf(1).add(getPercentage(socialTaxRate)).add(getPercentage(unemploymentInsuranceEmployerRate)), 10, HALF_UP);
    }

}
