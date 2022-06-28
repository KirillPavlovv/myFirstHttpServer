package idnumber;

public class IdService {

    public static void main(String[] args) {
        IdNumber idNumber = new EstonianIdNumber("39306293735");
        idNumber.validateIdNumber();
        System.out.println("Vozrast: " + idNumber.getAge());
        System.out.println("Den Rozhdenija: " + idNumber.getBirthdayForIdNumber());

    }

}
