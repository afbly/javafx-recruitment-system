package recruitment.system.data.model;

public class Job {
    String jobID;
    String jobTitle;
    String jobDesc;
    String datePublished;
    String startDate;
    String endDate;
    String noOfVacancies;
    String minSalary;
    String maxSalary;
    String city;
    String province;
    String jobType;
    String yearsOfExp;
    String seniority;
    String reqEdu;
    String category;
    String client;
    String recruiter;
    String categoryName;
    String jobStatus;

    public Job(String jobID, String jobTitle, String jobDesc, String datePublished, String startDate, String endDate,
               String noOfVacancies, String minSalary, String maxSalary, String city, String province, String jobType,
               String yearsOfExp, String seniority, String reqEdu, String category, String client, String recruiter,
               String jobStatus) {
        this.jobID = jobID;
        this.jobTitle = jobTitle;
        this.jobDesc = jobDesc;
        this.datePublished = datePublished;
        this.startDate = startDate;
        this.endDate = endDate;
        this.noOfVacancies = noOfVacancies;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
        this.city = city;
        this.province = province;
        this.jobType = jobType;
        this.yearsOfExp = yearsOfExp;
        this.seniority = seniority;
        this.reqEdu = reqEdu;
        this.category = category;
        this.client = client;
        this.recruiter = recruiter;
        this.jobStatus = jobStatus;
    }


    public Job(String jobID, String jobTitle, String jobDesc, String noOfVacancies, String minSalary, String maxSalary,
               String city, String province, String jobType, String yearsOfExp, String seniority, String reqEdu,
               String category, String client, String jobStatus) {
        this.jobID = jobID;
        this.jobTitle = jobTitle;
        this.jobDesc = jobDesc;
        this.noOfVacancies = noOfVacancies;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
        this.city = city;
        this.province = province;
        this.jobType = jobType;
        this.yearsOfExp = yearsOfExp;
        this.seniority = seniority;
        this.reqEdu = reqEdu;
        this.category = category;
        this.client = client;
        this.jobStatus = jobStatus;
    }

    public void setJobID(String jobID) {
        this.jobID = jobID;
    }

    public String getJobID() {
        return jobID;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
    }

    public String getJobDesc() {
        return jobDesc;
    }

    public void setDatePublished(String datePublished) {
        this.datePublished = datePublished;
    }

    public String getDatePublished() {
        return datePublished;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setNoOfVacancies(String noOfVacancies) {
        this.noOfVacancies = noOfVacancies;
    }

    public String getNoOfVacancies() {
        return noOfVacancies;
    }

    public void setMinSalary(String minSalary) {
        this.minSalary = minSalary;
    }

    public String getMinSalary() {
        return minSalary;
    }

    public void setMaxSalary(String maxSalary) {
        this.maxSalary = maxSalary;
    }

    public String getMaxSalary() {
        return maxSalary;
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

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getJobType() {
        return jobType;
    }

    public void setYearsOfExp(String yearsOfExp) {
        this.yearsOfExp = yearsOfExp;
    }

    public String getYearsOfExp() {
        return yearsOfExp;
    }

    public void setSeniority(String seniority) {
        this.seniority = seniority;
    }

    public String getSeniority() {
        return seniority;
    }

    public void setReqEdu(String reqEdu) {
        this.reqEdu = reqEdu;
    }

    public String getReqEdu() {
        return reqEdu;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getClient() {
        return client;
    }

    public void setRecruiter(String recruiter) {
        this.recruiter = recruiter;
    }

    public String getRecruiter() {
        return recruiter;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getJobStatus() {
        return jobStatus;
    }
}
