package scot.gov.publications.metadata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EqualityInfo {

    boolean gender;

    boolean age;

    boolean disability;

    boolean religion;

    boolean ethnicity;

    boolean orientation;

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public boolean isAge() {
        return age;
    }

    public void setAge(boolean age) {
        this.age = age;
    }

    public boolean isDisability() {
        return disability;
    }

    public void setDisability(boolean disability) {
        this.disability = disability;
    }

    public boolean isReligion() {
        return religion;
    }

    public void setReligion(boolean religion) {
        this.religion = religion;
    }

    public boolean isEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(boolean ethnicity) {
        this.ethnicity = ethnicity;
    }

    public boolean isOrientation() {
        return orientation;
    }

    public void setOrientation(boolean orientation) {
        this.orientation = orientation;
    }
}
