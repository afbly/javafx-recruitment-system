package recruitment.system.ui.listapplication;

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
import recruitment.system.database.DatabaseHandler;
import recruitment.system.ui.addapplication.AddApplicationController;
import recruitment.system.ui.listapplication.detail.ViewApplicationController;
import recruitment.system.ui.listclient.ListClientController;
import recruitment.system.util.RecruitmentSystemUtil;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class ListApplicationController implements Initializable {

    private PreparedStatement stmt;
    private ResultSet rs;
    private ObservableList<Application> list = FXCollections.observableArrayList();
    private String recruiterID = null;

    @FXML
    private TableView<Application> tableView;
    @FXML
    private TableColumn<Application, String> colAppID;
    @FXML
    private TableColumn<Application, String> colApplicant;
    @FXML
    private TableColumn<Application, String> colAppDate;
    @FXML
    private TableColumn<Application, String> colAppStatus;
    @FXML
    private TableColumn<Application, String> colEmail;
    @FXML
    private TableColumn<Application, String> colLocation;
    @FXML
    private TableColumn<Application, String> colExperience;
    @FXML
    private Button btnCreate;
    @FXML
    private TextField txtSearch;
    @FXML
    private ComboBox<String> cbFilter;

    public void initialize(URL location, ResourceBundle resources) {
        // Display view table
        String checkstmt = "SELECT * FROM application_view";
        initCol();
        loadData(checkstmt);
        cbFilter.getItems().addAll("All", "No specialization", "With specialization");
        cbFilter.setValue("All");

        tableView.setRowFactory( tv -> {
            TableRow<Application> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    Application selectedForView = row.getItem();

                    loadWindow(selectedForView);
                }
            });
            return row;
        });

        // Search Applicant ID, Name, Status, Email Address, Location, and Years of Experience

        FilteredList<Application> filteredData = new FilteredList<>(list, b -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(application -> {
                // If filter text is empty, display all persons.

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (application.getAppID().toLowerCase().indexOf(lowerCaseFilter) != -1 ) {
                    return true;
                } else if (application.getApplicant().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if (application.getAppStatus().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if (application.getEmail().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if (application.getLocation().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if (application.getExperience().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else {
                    return false;
                }
            });
        });

        // 3. Wrap the FilteredList in a SortedList.
        SortedList<Application> sortedData = new SortedList<>(filteredData);

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
        colAppID.setCellValueFactory(new PropertyValueFactory<>("appID"));
        colApplicant.setCellValueFactory(new PropertyValueFactory<>("applicant"));
        colAppDate.setCellValueFactory(new PropertyValueFactory<>("appDate"));
        colAppStatus.setCellValueFactory(new PropertyValueFactory<>("appStatus"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colExperience.setCellValueFactory(new PropertyValueFactory<>("experience"));
    }

    private void loadData(String checkstmt) {
        list.clear();

        try {
            stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(checkstmt);
            rs = stmt.executeQuery();

            while (rs.next()) {
                String appID = rs.getString(1);
                String applicant = rs.getString(2);
                String appDate = rs.getString(3);
                String appStatus = rs.getString(4);
                String email = rs.getString(5);
                String location = rs.getString(6);
                String experience = rs.getString(7);


                list.add(new Application(appID, applicant, appDate, appStatus, email, location, experience));
            }
        } catch (Exception ex) {
            System.out.println("Exception at ListApplicationController:loadData" + ex.getLocalizedMessage());
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

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("recruitment/system/ui/addapplication/AddApplication.fxml"));
            Parent parent = loader.load();

            AddApplicationController controller = (AddApplicationController) loader.getController();
            controller.getRecruiterID(recruiterID);

            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.setTitle("Create Application");
            stage.setScene(new Scene(parent));
            stage.setResizable(false);
            stage.show();

            RecruitmentSystemUtil.setStageIcon(stage);
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    @FXML
    private void cbFilterOnAction(ActionEvent event) {
        String checkstmt = null;
        if (cbFilter.getValue().equalsIgnoreCase("All")) {
            checkstmt = "SELECT * FROM application_view";
        } else if (cbFilter.getValue().equalsIgnoreCase("With specialization")) {
            checkstmt = "SELECT * FROM filter_application_list_view_1";
        } else if (cbFilter.getValue().equalsIgnoreCase("No specialization")) {
            checkstmt = "SELECT * FROM filter_application_list_view_2";
        }
        loadData(checkstmt);
    }

    private void loadWindow(Application selectedForView) {
        try {
            getStage().close();

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("recruitment/system/ui/listapplication/detail/ViewApplication.fxml"));
            Parent parent = loader.load();

            ViewApplicationController controller = (ViewApplicationController) loader.getController();
            controller.inflateUI(selectedForView, recruiterID);

            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.setTitle("View Application");
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

    public static class Application {

        String appID;
        String applicant;
        String appDate;
        String appStatus;
        String email;
        String location;
        String experience;

        public Application(String appID, String applicant, String appDate, String appStatus,
                           String email, String location, String experience) {

            this.appID = appID;
            this.applicant = applicant;
            this.appDate = appDate;
            this.appStatus = appStatus;
            this.email = email;
            this.location = location;
            this.experience = experience;

        }

        public void setAppID(String appID) {
            this.appID = appID;
        }

        public String getAppID() {
            return appID;
        }

        public void setApplicant(String applicant) {
            this.applicant = applicant;
        }

        public String getApplicant() {
            return applicant;
        }

        public void setAppDate(String appDate) {
            this.appDate = appDate;
        }

        public String getAppDate() {
            return appDate;
        }

        public void setAppStatus(String appStatus) {
            this.appStatus = appStatus;
        }

        public String getAppStatus() {
            return appStatus;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getEmail() {
            return email;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getLocation() {
            return location;
        }

        public void setExperience(String experience) {
            this.experience = experience;
        }

        public String getExperience() {
            return experience;
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
