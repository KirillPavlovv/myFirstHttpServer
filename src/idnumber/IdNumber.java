package idnumber;


public abstract class IdNumber {

    private String idNumber;
    private String birthday;
    private String gender;

    protected IdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    protected IdNumber() {
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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


    public String getIdNumber() {
        return idNumber;
    }

}
