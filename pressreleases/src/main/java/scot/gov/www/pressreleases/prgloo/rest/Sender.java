package scot.gov.www.pressreleases.prgloo.rest;

/**
 * Created by z441571 on 19/10/2016.
 *
 * Format from PRgloo API
 *
 *  {
 *     "email": String,
 *     "jobTitle": String,
 *     "landline": String,
 *     "mobile": String,
 *     "name": String,
 *     "organisation": String,
 *  }
 *
 */
public class Sender {

    private String email;
    private String jobTitle;
    private String landline;
    private String mobile;
    private String name;
    private String organisation;

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getLandline() {
        return landline;
    }

    public void setLandline(String landline) {
        this.landline = landline;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

}