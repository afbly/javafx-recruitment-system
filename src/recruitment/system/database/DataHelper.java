package recruitment.system.database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import recruitment.system.data.model.Application;
import recruitment.system.data.model.Client;
import recruitment.system.data.model.Job;
import recruitment.system.data.model.Recruiter;
import java.sql.*;

public class DataHelper {

    public static PreparedStatement stmt = null;
    public static CallableStatement cstmt = null;
    public static ResultSet rs = null;
    public static ObservableList<String> data = FXCollections.observableArrayList();

    public static String getRecruiterID(String username) {
        try {

            // Select the recruiterID that matches the username in the recruiter_account table
            String checkstmt = "SELECT recruiterID FROM recruiter_account WHERE username = BINARY ?";
            stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(checkstmt);
            stmt.setString(1, username);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (Exception ex) {
            System.out.println("Exception at DataHelper:getRecruiterID\n" + ex.getLocalizedMessage());
            System.err.println(ex);
        } finally {
            if(stmt != null && rs != null) {
                try {
                    rs.close();
                    stmt.close();
                }  catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }

    // Takes an input parameter checkstmt which contains an SQL command to
    // fill a certain list view (ex. specialization list) in a certain module
    public static ObservableList<String> inflate(String checkstmt) {
        data.clear();

        try {

            stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(checkstmt);
            rs = stmt.executeQuery();

            while (rs.next()) {
                data.add(rs.getString(1));
            }

            return data;
        } catch (Exception ex) {
            System.out.println("Exception at DataHelper:inflate\n" + ex.getLocalizedMessage());
            System.err.println(ex);
        } finally {
            if(stmt != null && rs != null) {
                try {
                    rs.close();
                    stmt.close();
                }  catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        return null;
    }

    // Generates a new ID by taking an input parameter that contains SQL command
    // Typically, the checkstmt takes the latest ID from a certain table then the method adds 1 to it to
    // generate a new ID
    public static int GenID(String checkstmt) {
        try {

            int id = 0;
            stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(checkstmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                id = rs.getInt(1);
                id += 1;
            }

            return id;
        } catch (Exception ex) {
            System.out.println("Exception at DataHelper:insertAccount\n" + ex.getLocalizedMessage());
            System.err.println(ex);
        } finally {
                if(stmt != null && rs != null) {
                    try {
                        rs.close();
                        stmt.close();
                    }  catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
        }
        return 0;
    }
    
    public static String getRecName(String recruiterID) {
        String name = null;

        try {

            // Select the firstname of recruiter
            String checkstmt = "SELECT firstName FROM recruiter WHERE recruiterID = ?";
            stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(checkstmt);
            stmt.setString(1, recruiterID);
            rs = stmt.executeQuery();

            if (rs.next()) {
                name = rs.getString(1);
            }

            return name;
        } catch (Exception ex) {
            System.out.println("Exception at DataHelper: getRecName\n" + ex.getLocalizedMessage());
            System.err.println(ex);
        } finally {
            if(stmt != null && rs != null) {
                try {
                    rs.close();
                    stmt.close();
                }  catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return name;
    }

    // Sets the number/count of hired applications and filled jobs for the day in the main dashboard
    public static String loadCount(String checkstmt) {
        String count = null;

        try {

            stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(checkstmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getString(1);
            }

            return count;
        } catch (Exception ex) {
            System.out.println("Exception at DataHelper: loadCount\n" + ex.getLocalizedMessage());
            System.err.println(ex);
        } finally {
            if(stmt != null && rs != null) {
                try {
                    rs.close();
                    stmt.close();
                }  catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return count;
    }

    // Sets the number/count of submissions, offered, and hired when viewing job detail
    public static int setCount(String jobID, String checkstmt) {
        try {

            int count = 0;
            cstmt = DatabaseHandler.getInstance().getConnection().prepareCall(checkstmt);
            cstmt.setInt(1, Integer.parseInt(jobID));
            rs = cstmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }

            return count;
        } catch (Exception ex) {
            System.out.println("Exception at DataHelper:setCount\n" + ex.getLocalizedMessage());
            ex.printStackTrace();
        } finally {
            if(stmt != null && rs != null && cstmt != null) {
                try {
                    rs.close();
                    stmt.close();
                    cstmt.close();
                }  catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return 0;
    }

    // Gets the number of vacancy  of a job, so that if it's
    public static int getNumberOfVacancy(String jobID) {
        try {

            int number = 0;
            String checkstmt = "{CALL getNumberOfVacancy (?)}";
            cstmt = DatabaseHandler.getInstance().getConnection().prepareCall(checkstmt);
            cstmt.setString(1, jobID);
            rs = cstmt.executeQuery();

            if (rs.next()) {
                number = rs.getInt(1);
            }

            return number;
        } catch (Exception ex) {
            System.out.println("Exception at DataHelper:updateApplicationDet\n" + ex.getLocalizedMessage());
            ex.printStackTrace();
        } finally {
            if(stmt != null && rs != null && cstmt != null) {
                try {
                    rs.close();
                    stmt.close();
                    cstmt.close();
                }  catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return -1;
    }

    // A simple method for signing in
    public static boolean signIn(String username, String password) {
        try {

            // Output's true if the username and password matches a record in the recruiter_account table
            // otherwise false
            String checkstmt = "SELECT * FROM recruiter_account WHERE username = BINARY ? AND passkey = BINARY ?";
            stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(checkstmt);
            stmt.setString(1, username);
            stmt.setString(2, password);
            rs = stmt.executeQuery();

            if(rs.next()) {
                return true;
            }
        } catch (Exception ex) {
            System.out.println("Exception at DataHelper:signIn\n" + ex.getLocalizedMessage());
            ex.printStackTrace();
        } finally {
            if(stmt != null && rs != null) {
                try {
                    rs.close();
                    stmt.close();
                }  catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }

    // Checks is username is already existing, if true, an error message is shown
    public static boolean isUsernameExist(String username) {
        try {

            String checkstmt = "SELECT username FROM recruiter_account WHERE username = BINARY ?";
            stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(checkstmt);
            stmt.setString(1, username);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return true;
            }
        } catch (Exception ex) {
            System.out.println("Exception at DataHelper:isUsernameExist\n" + ex.getLocalizedMessage());
            System.err.println(ex);
        } finally {
            if(stmt != null && rs != null) {
                try {
                    rs.close();
                    stmt.close();
                }  catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }

    // Insert recruiter by calling stored procedure from the database
    public static boolean insertRecruiter(int recruiterID, String firstName, String lastName, String username, String password) {
        try {

            // Insert recruiter by calling stored procedure from the database
            String checkstmt = "{CALL insert_recruiter(?, ?, ?, ?, ?)}";

            cstmt = DatabaseHandler.getInstance().getConnection().prepareCall(checkstmt);
            cstmt.setInt(1, recruiterID);
            cstmt.setString(2, firstName);
            cstmt.setString(3, lastName);
            cstmt.setString(4, username);
            cstmt.setString(5, password);
            cstmt.execute();

            return true;
        } catch (Exception ex) {
            System.out.println("Exception at DataHelper:insertRecruiter\n" + ex.getLocalizedMessage());
            System.err.println(ex);
        } finally {
            if(stmt != null && rs != null) {
                try {
                    rs.close();
                    stmt.close();
                }  catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }

    // Insert job by calling stored procedure from the database
    public static boolean insertNewJob(Job job) {
        try {

            // Insert job by calling stored procedure from the database
            String checkstmt = "{CALL insert_job (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

            cstmt = DatabaseHandler.getInstance().getConnection().prepareCall(checkstmt);
            cstmt.setInt(1, Integer.parseInt(job.getJobID()));
            cstmt.setString(2, job.getJobTitle());
            cstmt.setString(3, job.getJobDesc());
            cstmt.setTimestamp(4, Timestamp.valueOf(job.getDatePublished()));
            cstmt.setDate(5, Date.valueOf(job.getStartDate()));
            cstmt.setDate(6, Date.valueOf(job.getEndDate()));
            cstmt.setString(7, job.getNoOfVacancies());
            cstmt.setInt(8, Integer.parseInt(job.getMinSalary()));
            cstmt.setInt(9, Integer.parseInt(job.getMaxSalary()));
            cstmt.setString(10, job.getCity());
            cstmt.setString(11, job.getProvince());
            cstmt.setString(12, job.getJobType());
            cstmt.setString(13, job.getYearsOfExp());
            cstmt.setString(14, job.getSeniority());
            cstmt.setString(15, job.getReqEdu());
            cstmt.setString(16, job.getCategory());
            cstmt.setString(17, job.getClient());
            cstmt.setInt(18, Integer.parseInt(job.getRecruiter()));
            cstmt.setString(19, job.getJobStatus());
            cstmt.execute();

            return true;
        } catch (Exception ex) {
            System.out.println("Exception at DataHelper:insertNewJob\n" + ex.getLocalizedMessage());
            ex.printStackTrace();
        } finally {
            if(stmt != null && rs != null && cstmt != null) {
                try {
                    rs.close();
                    stmt.close();
                    cstmt.close();
                }  catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }

    // Insert application by calling stored procedure from the database
    public static boolean insertNewApplication(Application application) {
        try {

            String checkstmt = "{CALL insert_application (?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?)}";
            cstmt = DatabaseHandler.getInstance().getConnection().prepareCall(checkstmt);
            cstmt.setInt(1, Integer.parseInt(application.getApplicationID()));
            cstmt.setTimestamp(2, Timestamp.valueOf(application.getDateOfApplication()));
            cstmt.setString(3, application.getFirstName());
            cstmt.setString(4, application.getMidName());
            cstmt.setString(5, application.getLastName());
            cstmt.setDate(6, Date.valueOf(application.getBirthdate()));
            cstmt.setString(7, application.getMobNumber());
            cstmt.setString(8, application.getTelNumber());
            cstmt.setString(9, application.getEmailAddress());
            cstmt.setString(10,application.getAddressLine());
            cstmt.setString(11, application.getCity());
            cstmt.setString(12, application.getProvince());
            cstmt.setString(13, application.getZipcode());
            cstmt.setString(14, application.getEdAttainment());
            cstmt.setString(15, application.getYearsOfExp());
            cstmt.setInt(16, Integer.parseInt(application.getJobID()));
            cstmt.setString(17, application.getDocPath());
            cstmt.setString(18, application.getExpJobTitle());
            cstmt.setString(19, application.getExpCompanyName());
            cstmt.setString(20, application.getStatus());

            cstmt.execute();

            return true;
        } catch (Exception ex) {
            System.out.println("Exception at DataHelper:insertNewApplication\n" + ex.getLocalizedMessage());
            ex.printStackTrace();
        } finally {
            if(stmt != null && rs != null && cstmt != null) {
                try {
                    rs.close();
                    stmt.close();
                    cstmt.close();
                }  catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }

    // Insert client by calling stored procedure from the database
    public static boolean insertNewClient(Client client) {
        try {

            String checkstmt = "{CALL insert_client (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
            cstmt = DatabaseHandler.getInstance().getConnection().prepareCall(checkstmt);
            cstmt.setInt(1, Integer.parseInt(client.getClientID()));
            cstmt.setString(2, client.getFirstName());
            cstmt.setString(3, client.getMidName());
            cstmt.setString(4, client.getLastName());
            cstmt.setString(5, client.getCompanyName());
            cstmt.setString(6, client.getMobNumber());
            cstmt.setString(7, client.getTelNumber());
            cstmt.setString(8, client.getEmailAddress());
            cstmt.setString(9, client.getAddressLine());
            cstmt.setString(10, client.getCity());
            cstmt.setString(11, client.getProvince());
            cstmt.setString(12, client.getZipcode());
            cstmt.setString(13, client.getIndustry());
            cstmt.execute();

            return true;
        } catch (Exception ex) {
            System.out.println("Exception at DataHelper:insertNewClient\n" + ex.getLocalizedMessage());
            ex.printStackTrace();
        } finally {
            if(stmt != null && rs != null && cstmt != null) {
                try {
                    rs.close();
                    stmt.close();
                    cstmt.close();
                }  catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }

    // Update recruiter by calling stored procedure from the database
    public static boolean updateRecruiterDet(Recruiter recruiter) {
        try {

            String checkstmt = "{CALL update_recruiter_detail (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
            cstmt = DatabaseHandler.getInstance().getConnection().prepareCall(checkstmt);
            cstmt.setString(1, recruiter.getRecruiterID());
            cstmt.setString(2, recruiter.getFirstName());
            cstmt.setString(3, recruiter.getMidName());
            cstmt.setString(4, recruiter.getLastName());
            cstmt.setDate(5, Date.valueOf(recruiter.getBirthdate()));
            cstmt.setString(6, recruiter.getMobNumber());
            cstmt.setString(7, recruiter.getTelNumber());
            cstmt.setString(8, recruiter.getEmailAddress());
            cstmt.setString(9, recruiter.getAddressLine());
            cstmt.setString(10, recruiter.getCity());
            cstmt.setString(11, recruiter.getProvince());
            cstmt.setString(12, recruiter.getZipcode());
            cstmt.setString(13, recruiter.getUsername());
            cstmt.execute();

            return true;
        } catch (Exception ex) {
            System.out.println("Exception at DataHelper:updateRecruiterDet\n" + ex.getLocalizedMessage());
            ex.printStackTrace();
        } finally {
            if(stmt != null && rs != null && cstmt != null) {
                try {
                    rs.close();
                    stmt.close();
                    cstmt.close();
                }  catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }

    // Update application detail by calling stored procedure from the database
    public static boolean updateApplicationDet(Application application) {
        try {
            // Update application detail by calling stored procedure from the database
            String checkstmt = "{CALL update_app_det (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
            cstmt = DatabaseHandler.getInstance().getConnection().prepareCall(checkstmt);
            cstmt.setString(1, application.getApplicationID());
            cstmt.setString(2, application.getFirstName());
            cstmt.setString(3, application.getMidName());
            cstmt.setString(4, application.getLastName());
            cstmt.setDate(5, Date.valueOf(application.getBirthdate()));
            cstmt.setString(6, application.getMobNumber());
            cstmt.setString(7, application.getTelNumber());
            cstmt.setString(8, application.getEmailAddress());
            cstmt.setString(9, application.getAddressLine());
            cstmt.setString(10, application.getCity());
            cstmt.setString(11, application.getProvince());
            cstmt.setString(12, application.getZipcode());
            cstmt.execute();

            return true;
        } catch (Exception ex) {
            System.out.println("Exception at DataHelper:updateApplicationDet\n" + ex.getLocalizedMessage());
            ex.printStackTrace();
        } finally {
            if(stmt != null && rs != null && cstmt != null) {
                try {
                    rs.close();
                    stmt.close();
                    cstmt.close();
                }  catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }

    // Update job detail by calling stored procedure from the database
    public static boolean updateJobDet(Job job) {
        try {

            String checkstmt = "{CALL update_job_det (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
            cstmt = DatabaseHandler.getInstance().getConnection().prepareCall(checkstmt);
            cstmt.setInt(1, Integer.parseInt(job.getJobID()));
            cstmt.setString(2, job.getJobTitle());
            cstmt.setString(3, job.getJobDesc());
            cstmt.setInt(4, Integer.parseInt(job.getNoOfVacancies()));
            cstmt.setInt(5, Integer.parseInt(job.getMinSalary()));
            cstmt.setInt(6, Integer.parseInt(job.getMaxSalary()));
            cstmt.setString(7, job.getCity());
            cstmt.setString(8, job.getProvince());
            cstmt.setString(9, job.getJobType());
            cstmt.setString(10, job.getYearsOfExp());
            cstmt.setString(11, job.getSeniority());
            cstmt.setString(12, job.getReqEdu());
            cstmt.setString(13, job.getCategory());
            cstmt.setString(14, job.getClient());
            cstmt.setString(15, job.getJobStatus());
            cstmt.execute();

            return true;
        } catch (Exception ex) {
            System.out.println("Exception at DataHelper:updateJobDet\n" + ex.getLocalizedMessage());
            ex.printStackTrace();
        } finally {
            if(stmt != null && rs != null && cstmt != null) {
                try {
                    rs.close();
                    stmt.close();
                    cstmt.close();
                }  catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }

    // Update client detail by calling stored procedure from the database
    public static boolean updateClientDet(Client client) {
        try {

            String checkstmt = "{CALL update_client_det (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
            cstmt = DatabaseHandler.getInstance().getConnection().prepareCall(checkstmt);
            cstmt.setString(1, client.getClientID());
            cstmt.setString(2, client.getFirstName());
            cstmt.setString(3, client.getMidName());
            cstmt.setString(4, client.getLastName());
            cstmt.setString(5, client.getCompanyName());
            cstmt.setString(6, client.getMobNumber());
            cstmt.setString(7, client.getTelNumber());
            cstmt.setString(8, client.getEmailAddress());
            cstmt.setString(9, client.getAddressLine());
            cstmt.setString(10, client.getCity());
            cstmt.setString(11, client.getProvince());
            cstmt.setString(12, client.getZipcode());
            cstmt.setString(13, client.getIndustry());
            cstmt.execute();

            return true;
        } catch (Exception ex) {
            System.out.println("Exception at DataHelper:updateClientDet\n" + ex.getLocalizedMessage());
            ex.printStackTrace();
        } finally {
            if(stmt != null && rs != null && cstmt != null) {
                try {
                    rs.close();
                    stmt.close();
                    cstmt.close();
                }  catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }

    // Inserts the list of specialization the applicant has or the job desires depending on the input parameters
    public static boolean insertList(String checkstmt, String id, ObservableList<String> list) {
        try {

            int ctr = 0;
            stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(checkstmt);

            while (list.iterator().hasNext() && ctr < list.size()) {
                stmt.setString(1, id);
                stmt.setString(2, list.get(ctr));
                stmt.executeUpdate();
                ctr++;
            }

            return true;
        } catch (Exception ex) {
            System.out.println("Exception at DataHelper:insertList");
            System.err.println(ex);
        } finally {
            if(stmt != null && rs != null) {
                try {
                    rs.close();
                    stmt.close();
                }  catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }

    // Insert evaluation note for an applicant
    public static void insertEvalNote(Application application) {
        try {

            String checkstmt = "{CALL insert_eval_note (?, ?, ?)}";
            cstmt = DatabaseHandler.getInstance().getConnection().prepareCall(checkstmt);
            cstmt.setInt(1, Integer.parseInt(application.getApplicationID()));
            cstmt.setString(2, application.getEvalNote());
            cstmt.setInt(3, Integer.parseInt(application.getRecruiterID()));

            cstmt.execute();
        } catch (Exception ex) {
            System.out.println("Exception at DataHelper:insertEvalNote\n" + ex.getLocalizedMessage());
            ex.printStackTrace();
        } finally {
            if(stmt != null && rs != null && cstmt != null) {
                try {
                    rs.close();
                    stmt.close();
                    cstmt.close();
                }  catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    // Updates job's number of vacancy and status when a new applicant is hired by calling the stored procedure in the database
    public static void updateJobVacanciesAndStatus(String jobID) {
        try {
            // Updates job status to filled when number of vacancy becomes 0 upon hiring a new applicant
            String checkstmt = "{CALL update_job_vacancy_and_status(?)}";
            cstmt = DatabaseHandler.getInstance().getConnection().prepareCall(checkstmt);
            cstmt.setString(1, jobID);
            cstmt.execute();
        } catch (Exception ex) {
            System.out.println("Exception at DataHelper:updateJobStatus\n" + ex.getLocalizedMessage());
        } finally {
            if(stmt != null && rs != null) {
                try {
                    rs.close();
                    stmt.close();
                }  catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

}
