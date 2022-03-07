package recruitment.system.data.model;

public class Application {
    String applicationID = null;
    String dateOfApplication = null;
    String firstName = null;
    String midName = null;
    String lastName = null;
    String birthdate = null;
    String mobNumber = null;
    String telNumber = null;
    String emailAddress = null;
    String addressLine = null;
    String city = null;
    String province = null;
    String zipcode = null;
    String edAttainment = null;
    String yearsOfExp = null;
    String jobID = null;
    String docPath = null;
    String expJobTitle = null;
    String expCompanyName = null;
    String status = null;
    String evalNote = null;
    String recruiterID = null;

    public Application(String applicationID, String dateOfApplication, String firstName, String midName,  String lastName,
                       String birthdate, String mobNumber, String telNumber, String emailAddress, String addressLine,
                       String city, String province, String zipcode, String edAttainment, String yearsOfExp,
                       String jobID, String docPath, String expJobTitle, String expCompanyName, String status) {

        this.applicationID = applicationID;
        this.dateOfApplication = dateOfApplication;
        this.firstName = firstName;
        this.midName = midName;
        this.lastName = lastName;
        this.birthdate = birthdate;
        this.mobNumber = mobNumber;
        this.telNumber = telNumber;
        this.emailAddress = emailAddress;
        this.addressLine = addressLine;
        this.city = city;
        this.province = province;
        this.zipcode = zipcode;
        this.edAttainment = edAttainment;
        this.yearsOfExp = yearsOfExp;
        this.jobID = jobID;
        this.docPath = docPath;
        this.expJobTitle = expJobTitle;
        this.expCompanyName = expCompanyName;
        this.status = status;
    }

    public Application (String applicationID, String firstName, String midName, String lastName, String birthdate, String mobNumber, String telNumber, String emailAddress, String addressLine,
                        String city, String province, String zipcode) {
        this.applicationID = applicationID;
        this.firstName = firstName;
        this.midName = midName;
        this.lastName = lastName;
        this.birthdate = birthdate;
        this.mobNumber = mobNumber;
        this.telNumber = telNumber;
        this.emailAddress = emailAddress;
        this.addressLine = addressLine;
        this.city = city;
        this.province = province;
        this.zipcode = zipcode;
    }

    public Application (String applicationID, String evalNote, String recruiterID) {
        this.applicationID = applicationID;
        this.evalNote = evalNote;
        this.recruiterID = recruiterID;
    }

    public void setApplicationID(String applicationID) {
        this.applicationID = applicationID;
    }

    public String getApplicationID() {
        return applicationID;
    }

    public void setDateOfApplication(String dateOfApplication) {
        this.dateOfApplication = dateOfApplication;
    }

    public String getDateOfApplication() {
        return dateOfApplication;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setMidName(String midName) {
        this.midName = midName;
    }

    public String getMidName() {
        return midName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setMobNumber(String mobNumber) {
        this.mobNumber = mobNumber;
    }

    public String getMobNumber() {
        return mobNumber;
    }

    public void setTelNumber(String telNumber) {
        this.telNumber = telNumber;
    }

    public String getTelNumber() {
        return telNumber;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getProvince() {
        return province;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setYearsOfExp(String yearsOfExp) {
        this.yearsOfExp = yearsOfExp;
    }

    public String getYearsOfExp() {
        return yearsOfExp;
    }

    public void setExpJobTitle(String expJobTitle) {
        this.expJobTitle = expJobTitle;
    }

    public String getExpJobTitle() {
        return expJobTitle;
    }

    public void setExpCompanyName(String expCompanyName) {
        this.expCompanyName = expCompanyName;
    }

    public String getExpCompanyName() {
        return expCompanyName;
    }

    public void setDocPath(String docPath) {
        this.docPath = docPath;
    }

    public String getDocPath() {
        return docPath;
    }

    public void setEdAttainment(String edAttainment) {
        this.edAttainment = edAttainment;
    }

    public String getEdAttainment() {
        return edAttainment;
    }

    public void setJobID(String jobID) {
        this.jobID = jobID;
    }

    public String getJobID() {
        return jobID;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setEvalNote(String evalNote) {
        this.evalNote = evalNote;
    }

    public String getEvalNote() {
        return evalNote;
    }

    public void setRecruiterID(String recruiterID) {
        this.recruiterID = recruiterID;
    }

    public String getRecruiterID() {
        return recruiterID;
    }
}
