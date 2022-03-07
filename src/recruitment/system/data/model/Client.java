package recruitment.system.data.model;

public class Client {
    String clientID;
    String firstName;
    String midName;
    String lastName;
    String companyName;
    String mobNumber;
    String telNumber;
    String emailAddress;
    String addressLine;
    String city;
    String province;
    String zipcode;
    String industry;

    public Client(String clientID, String firstName, String midName, String lastName, String companyName,
                  String mobNumber, String telNumber, String emailAddress, String addressLine,
                  String city, String province, String zipcode, String industry) {
        this.clientID = clientID;
        this.firstName = firstName;
        this.midName = midName;
        this.lastName = lastName;
        this.companyName = companyName;
        this.mobNumber = mobNumber;
        this.telNumber = telNumber;
        this.emailAddress = emailAddress;
        this.addressLine = addressLine;
        this.city = city;
        this.province = province;
        this.zipcode = zipcode;
        this.industry = industry;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getClientID() {
        return clientID;
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

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
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

    public void setIndustry(String industryID) {
        this.industry = industry;
    }

    public String getIndustry() {
        return industry;
    }
}
