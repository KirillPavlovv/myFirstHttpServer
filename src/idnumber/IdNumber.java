package idnumber;


public abstract class IdNumber {

    private String personalCode;
    private String birthday;
    private String gender;

    protected IdNumber(String idNumber) {
        this.personalCode = idNumber;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getGender() {
        return gender;
    }

    protected IdNumber(String birthday, String gender) {
        this.birthday = birthday;
        this.gender = gender;
    }

    public abstract boolean validateIdNumber();

    public abstract int findControlNumber();

    public abstract String getBirthdayForIdNumber();

    public abstract String getAge();

    public abstract String generateIdNumber();


    public String getPersonalCode() {
        return personalCode;
    }

}
