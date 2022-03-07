package recruitment.system.ui.listjob;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import recruitment.system.alert.AlertMaker;
import recruitment.system.database.DatabaseHandler;
import recruitment.system.ui.addjob.AddJobController;
import recruitment.system.ui.listclient.detail.ViewClientController;
import recruitment.system.ui.listjob.detail.EditDetailController;
import recruitment.system.ui.listjob.detail.ViewJobDetailController;
import recruitment.system.util.RecruitmentSystemUtil;
import java.io.IOException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.ResourceBundle;

public class ListJobController implements Initializable {

    private PreparedStatement stmt;
    private ResultSet rs;
    private ObservableList<Job> list = FXCollections.observableArrayList();
    private String recruiterID = null;

    @FXML
    private TableView<Job> tableView;
    @FXML
    private TableColumn<Job, String> colJobID;
    @FXML
    private TableColumn<Job, String> colJobTitle;
    @FXML
    private TableColumn<Job, String> colRecruiter;
    @FXML
    private TableColumn<Job, String> colStartDate;
    @FXML
    private TableColumn<Job, String> colLocation;
    @FXML
    private TableColumn<Job, String> colClient;
    @FXML
    private TableColumn<Job, String> colJobStatus;
    @FXML
    private TableColumn<Job, String> colApplicants;
    @FXML
    private Button btnCreate;
    @FXML
    private TextField txtSearch;

    public void initialize(URL location, ResourceBundle resources) {
        initCol();
        loadData();

        tableView.setRowFactory( tv -> {
            TableRow<Job> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                   Job selectedForView = row.getItem();
                   loadWindow(selectedForView);
                }
            });
            return row;
        });

        FilteredList<Job> filteredData = new FilteredList<>(list, b -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(job -> {
                // If filter text is empty, display all persons.

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (job.getJobID().toLowerCase().indexOf(lowerCaseFilter) != -1 ) {
                    return true; // Filter matches client id
                } else if (job.getJobTitle().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true; // Filter matches client name
                } else if (job.getRecruiter().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true; // Filter matches company name
                } else if (job.getClient().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true; // Filter matches email address
                } else if (job.getJobStatus().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true; // Filter matches email address
                } else {
                    return false; // Does not match.
                }
            });
        });

        // 3. Wrap the FilteredList in a SortedList.
        SortedList<Job> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        // 	  Otherwise, sorting the TableView would have no effect.
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        tableView.setItems(sortedData);

    }

    public void getRecruiterID(String recID) {
        Recruiter recruiter = new Recruiter(recID);
        recruiterID = recruiter.getRecruiterID();
    }

    private void initCol() {
        colJobID.setCellValueFactory(new PropertyValueFactory<>("jobID"));
        colJobTitle.setCellValueFactory(new PropertyValueFactory<>("jobTitle"));
        colRecruiter.setCellValueFactory(new PropertyValueFactory<>("recruiter"));
        colStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colClient.setCellValueFactory(new PropertyValueFactory<>("client"));
        colJobStatus.setCellValueFactory(new PropertyValueFactory<>("jobStatus"));
        colApplicants.setCellValueFactory(new PropertyValueFactory<>("noOfApplicants"));
    }

    private void loadData() {
        list.clear();

        try {
            // Display view table
            String checkstmt = "SELECT * FROM job_view";
            stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(checkstmt);
            rs = stmt.executeQuery();

            while (rs.next()) {
                String jobID = String.valueOf(rs.getInt(1));
                String jobTitle = rs.getString(2);
                String recruiter = rs.getString(3);
                String startDate = RecruitmentSystemUtil.getDateString(rs.getDate(4));
                String location = rs.getString(5);
                String client = rs.getString(6);
                String jobStatus = rs.getString(7);
                String noOfApplicants = String.valueOf(rs.getInt(8));

                list.add(new Job(jobID, jobTitle, recruiter, startDate, location, client, jobStatus, noOfApplicants));
            }
        } catch (Exception ex) {
            System.out.println("Exception at ListJobController:loadData" + ex.getLocalizedMessage());
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

        tableView.setItems(list);
    }

    @FXML
    private void btnCreateOnAction(ActionEvent event) {
        try {
            getStage().close();

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("recruitment/system/ui/addjob/AddJob.fxml"));
            Parent parent = loader.load();

            AddJobController controller = (AddJobController) loader.getController();
            controller.getRecruiterID(recruiterID);

            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.setTitle("Create Job");
            stage.setScene(new Scene(parent));
            stage.setResizable(false);
            stage.show();

            RecruitmentSystemUtil.setStageIcon(stage);
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    private void loadWindow(Job selectedForView) {
        try {
            String jobID = selectedForView.getJobID();
            getStage().close();

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("recruitment/system/ui/listjob/detail/ViewJobDetail.fxml"));
            Parent parent = null;
            parent = loader.load();

            ViewJobDetailController controller = (ViewJobDetailController) loader.getController();
            controller.inflateUI(jobID, recruiterID);

            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.setTitle("View Job");
            stage.setScene(new Scene(parent));
            stage.show();
            stage.setResizable(false);
            RecruitmentSystemUtil.setStageIcon(stage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Stage getStage() {
        return (Stage) btnCreate.getScene().getWindow();
    }

    public static class Job {
        String jobID;
        String jobTitle;
        String recruiter;
        String startDate;
        String location;
        String client;
        String jobStatus;
        String noOfApplicants;

        public Job(String jobID, String jobTitle, String recruiter, String startDate, String location,
                   String client, String jobStatus, String noOfApplicants) {
            this.jobID = jobID;
            this.jobTitle = jobTitle;
            this.recruiter = recruiter;
            this.startDate = startDate;
            this.location = location;
            this.client = client;
            this.jobStatus = jobStatus;
            this.noOfApplicants = noOfApplicants;
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

        public void setRecruiter(String recruiter) {
            this.recruiter = recruiter;
        }

        public String getRecruiter() {
            return recruiter;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getLocation() {
            return location;
        }

        public void setClient(String client) {
            this.client = client;
        }

        public String getClient() {
            return client;
        }

        public void setJobStatus(String jobStatus) {
            this.jobStatus = jobStatus;
        }

        public String getJobStatus() {
            return jobStatus;
        }

        public void setNoOfApplicants(String noOfApplicants) {
            this.noOfApplicants = noOfApplicants;
        }

        public String getNoOfApplicants() {
            return noOfApplicants;
        }
    }

    public static class Recruiter {

        String recruiterID;

        public Recruiter(String recruiterID) {
            this.recruiterID = recruiterID;
        }

        public void setRecruiterID(String recruiterID) {
            this.recruiterID = recruiterID;
        }

        public String getRecruiterID() {
            return recruiterID;
        }
    }

}
