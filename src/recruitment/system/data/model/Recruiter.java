package recruitment.system.data.model;

public class Recruiter {
    String recruiterID;
    String firstName;
    String midName;
    String lastName;
    String birthdate ;
    String mobNumber;
    String telNumber;
    String emailAddress;
    String addressLine;
    String city;
    String province;
    String zipcode;
    String username;

    public Recruiter(String recruiterID, String firstName, String midName, String lastName, String birthdate,
                     String mobNumber, String telNumber, String emailAddress, String addressLine,
                     String city, String province, String zipcode, String username) {
        this.recruiterID = recruiterID;
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
        this.username = username;
    }


    public void setRecruiterID(String recruiterID) {
        this.recruiterID = recruiterID;
    }

    public String getRecruiterID() {
        return recruiterID;
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

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getAddressLine() {
        return addressLine;
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

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
