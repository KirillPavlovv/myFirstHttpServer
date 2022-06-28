package idnumber;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Period;

public class EstonianIdNumber extends IdNumber {

    public EstonianIdNumber(String idNumber) {
        super(idNumber);
    }

    public EstonianIdNumber(String birthday, String gender) {
        super(birthday, gender);
    }

    @Override
    public boolean validateIdNumber() {

        if (getIdNumber().length() != 11) {                                                                                         // kas pikkus on 11 marki
            return false;
        }
        char[] idNumberChars = getIdNumber().toCharArray();

        for (char idNumberChar : idNumberChars) {                                                                                   //kas koik on numbrid
            int a = (byte) idNumberChar;
            if (!(a >= 48 && a <= 57)) {
                return false;
            }
        }
        if (Integer.parseInt(getIdNumber().substring(0, 1)) < 1 || Integer.parseInt(getIdNumber().substring(0, 1)) > 6) {             //kas esimene number on oige
            return false;
        }

        IdNumberData idNumberData = getIdNumberData();


        try {
            LocalDate.of(idNumberData.year, idNumberData.month, idNumberData.day);
        } catch (DateTimeException e) {
            return false;
        }

        if (idNumberData.controlNumber != findControlNumber()) {
            return false;
        }
        LocalDate birthDate = LocalDate.of(idNumberData.year, idNumberData.month, idNumberData.day);
        return birthDate.compareTo(LocalDate.now()) <= 0;
    }

    public int findControlNumber() {
        char[] idNumberChars = getIdNumber().toCharArray();
        int[] firstStepCheck = {1, 2, 3, 4, 5, 6, 7, 8, 9, 1};
        int[] secondStepCheck = {3, 4, 5, 6, 7, 8, 9, 1, 2, 3};
        int sum = 0;
        int remainderForControlNumber;
        for (int i = 0; i < 10; i++) {
            int multiply = firstStepCheck[i] * Integer.parseInt(String.valueOf(idNumberChars[i]));
            sum += multiply;
        }
        remainderForControlNumber = sum % 11;
        if (remainderForControlNumber < 10) {
            return remainderForControlNumber;
        } else {
            for (int i = 0; i < 10; i++) {
                int multiply = secondStepCheck[i] * Integer.parseInt(String.valueOf(idNumberChars[i]));
                sum += multiply;
            }
            remainderForControlNumber = sum % 11;
            if (remainderForControlNumber < 10) {
                return remainderForControlNumber;
            }
        }
        return 0;
    }

    @Override
    public String getBirthdayForIdNumber() {
        IdNumberData idNumberData = getIdNumberData();
        return idNumberData.day + "/" + idNumberData.month + "/" + idNumberData.year;
    }

    @Override
    public String getAge() {
        IdNumberData idNumberData = getIdNumberData();
        LocalDate birthday = LocalDate.of(idNumberData.getYear(), idNumberData.getMonth(), idNumberData.getDay());
        LocalDate today = LocalDate.now();
        Period agePeriod = Period.between(birthday, today);
        return "Age is: " + agePeriod.getYears() + " years, " + agePeriod.getMonths() + " months and " + agePeriod.getDays() + " days.";
    }

    @Override
    public String generateIdNumber() {
        IdNumberData idNumberData = new IdNumberData();
        idNumberData.setDay(Integer.parseInt(getBirthday().substring(0, 2)));
        idNumberData.setMonth(Integer.parseInt(getBirthday().substring(3, 5)));
        idNumberData.setYear(Integer.parseInt(getBirthday().substring(6, 10)));
        if (getGender().equals("male")) {
            if (idNumberData.getYear() >= 1900 && idNumberData.getYear() <= 1999) {
                idNumberData.setYear(idNumberData.year - 1900);
                idNumberData.setCentury(3);
            } else {
                idNumberData.setYear(idNumberData.year - 2000);
                idNumberData.setCentury(5);
            }
        } else {
            if (idNumberData.getYear() >= 1900 && idNumberData.getYear() <= 1999) {
                idNumberData.setYear(idNumberData.year - 1900);
                idNumberData.setCentury(4);
            } else {
                idNumberData.setYear(idNumberData.year - 2000);
                idNumberData.setCentury(6);
            }
        }
        idNumberData.setBirthCounty(getRandomNumber(1, 999));
        idNumberData.setControlNumber(0);
        String idNumber = String.valueOf(idNumberData.century) + String.valueOf(idNumberData.year) + getBirthday().substring(3, 5) + getBirthday().substring(0, 2) + String.valueOf(idNumberData.birthCounty) + String.valueOf(idNumberData.controlNumber);
        EstonianIdNumber estonianIdNumber = new EstonianIdNumber(idNumber);
        idNumberData.setControlNumber(estonianIdNumber.findControlNumber());
        return  String.valueOf(idNumberData.century) + String.valueOf(idNumberData.year) + getBirthday().substring(3, 5) + getBirthday().substring(0, 2) + String.valueOf(idNumberData.birthCounty) + String.valueOf(idNumberData.controlNumber);

    }

    private IdNumberData getIdNumberData() {
        int century = Integer.parseInt(getIdNumber().substring(0, 1));
        int year = Integer.parseInt(getIdNumber().substring(1, 3));
        int month = Integer.parseInt(getIdNumber().substring(3, 5));
        int day = Integer.parseInt(getIdNumber().substring(5, 7));
        int serialNumber = Integer.parseInt(getIdNumber().substring(7, 10));
        int controlNumber = Integer.parseInt(getIdNumber().substring(10, 11));
        IdNumberData idNumberData = new IdNumberData(century, year, month, day, serialNumber, controlNumber);
        fixCentury(idNumberData);
        return idNumberData;
    }

    private void fixCentury(IdNumberData idNumberData) {
        switch (idNumberData.century) {
            case 1, 2 -> idNumberData.year += 1800;
            case 3, 4 -> idNumberData.year += 1900;
            case 5, 6 -> idNumberData.year += 2000;
        }
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
