package idnumber;

import web.HttpRequest;

public class IdService {

    public static void main(String[] args) {
        IdNumber idNumber = new EstonianIdNumber("39306293735");
        idNumber.validateIdNumber();
        System.out.println("Vozrast: " + idNumber.getAge());
        System.out.println("Den Rozhdenija: " + idNumber.getBirthdayForIdNumber());

    }

    public static String generateId(HttpRequest httpRequest) {

        String[] splitBirthday = httpRequest.getParameter2().split("-");
        StringBuilder stringBuilderBirthday = new StringBuilder();
        for (String s : splitBirthday) {
            stringBuilderBirthday.insert(0, s);
            stringBuilderBirthday.insert(0, "-");
        }
        stringBuilderBirthday.delete(0, 1);
        String birthday = stringBuilderBirthday.toString();

        return new EstonianIdNumber(birthday, httpRequest.getParameter1()).generateIdNumber();
    }


}
