package idnumber;

import java.util.Objects;

public class IdNumberData {
    int century;
    int year;
    int month;
    int day;
    int birthCounty;
    int controlNumber;

    public IdNumberData() {
    }

    public IdNumberData(int century, int year, int month, int day, int birthCounty, int controlNumber) {
        this.century = century;
        this.year = year;
        this.month = month;
        this.day = day;
        this.birthCounty = birthCounty;
        this.controlNumber = controlNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IdNumberData that)) return false;
        return getCentury() == that.getCentury() && getYear() == that.getYear() && getMonth() == that.getMonth() && getDay() == that.getDay() && getBirthCounty() == that.getBirthCounty() && getControlNumber() == that.getControlNumber();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCentury(), getYear(), getMonth(), getDay(), getBirthCounty(), getControlNumber());
    }

    @Override
    public String toString() {
        return "IdNumber{" +
                 century +
                 year +
                 month +
                 day +
                 birthCounty +
                 controlNumber +
                '}';
    }

    public int getCentury() {
        return century;
    }

    public void setCentury(int century) {
        this.century = century;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getBirthCounty() {
        return birthCounty;
    }

    public void setBirthCounty(int birthCounty) {
        this.birthCounty = birthCounty;
    }

    public int getControlNumber() {
        return controlNumber;
    }

    public void setControlNumber(int controlNumber) {
        this.controlNumber = controlNumber;
    }

}
