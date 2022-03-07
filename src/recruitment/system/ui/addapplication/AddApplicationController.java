package recruitment.system.ui.addapplication;

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
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import recruitment.system.alert.AlertMaker;
import recruitment.system.data.model.Application;
import recruitment.system.database.DataHelper;
import recruitment.system.database.DatabaseHandler;
import recruitment.system.ui.addjob.AddJobController;
import recruitment.system.ui.listjob.ListJobController;
import recruitment.system.ui.main.MainController;
import recruitment.system.util.RecruitmentSystemUtil;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AddApplicationController implements Initializable {


    private PreparedStatement stmt;
    private ResultSet rs;
    private ObservableList<String> specList = FXCollections.observableArrayList();
    private ObservableList<String> appSpec = FXCollections.observableArrayList();
    private ObservableList<Job> list = FXCollections.observableArrayList();
    private String appID = null;
    private String path = null;
    private String recruiterID = null;

    DataHelper dataHelper;

    @FXML
    private Button btnBack;
    @FXML
    private Button btnDetails;
    @FXML
    private Button btnExp;
    @FXML
    private Button btnJob;
    @FXML
    private ScrollPane pnJob;
    @FXML
    private TextField txtSearchJob;
    @FXML
    private TableView<Job> tblJobView;
    @FXML
    private TextField txtDocPath;
    @FXML
    private RadioButton rbQualified;
    @FXML
    private RadioButton rbDisqualified;
    @FXML
    private TextArea txtNote;
    @FXML
    private ScrollPane pnExp;
    @FXML
    private ComboBox<String> chEdAttainment;
    @FXML
    private ComboBox<String> chYearsOfExp;
    @FXML
    private TextField txtJobTitle;
    @FXML
    private TextField txtCompanyName;
    @FXML
    private ListView<String> listSpec;
    @FXML
    private ListView<String> listAppSpec;
    @FXML
    private ScrollPane pnBasic;
    @FXML
    private TextField txtAppID;
    @FXML
    private TextField txtFirstName;
    @FXML
    private TextField txtMidName;
    @FXML
    private TextField txtLastName;
    @FXML
    private DatePicker dBirthDate;
    @FXML
    private TextField txtMobNumber;
    @FXML
    private TextField txtTelNumber;
    @FXML
    private TextField txtCity;
    @FXML
    private TextField txtProvince;
    @FXML
    private TextField txtZipcode;
    @FXML
    private TextArea txtAddressLine;
    @FXML
    private TextField txtEmailAdd;
    @FXML
    private StackPane rootPane;
    @FXML
    private AnchorPane mainContainer;
    @FXML
    private TableColumn<Job, String> colJobID;
    @FXML
    private TableColumn<Job, String> colJobTitle;
    @FXML
    private TableColumn<Job, String> colSDate;
    @FXML
    private TableColumn<Job, String> colCity;
    @FXML
    private TableColumn<Job, String> colClient;
    @FXML
    private TableColumn<Job, String> colCompany;
    private FileChooser fileChooser;
    private File file;
    private Desktop desktop = Desktop.getDesktop();

    public void initialize(URL location, ResourceBundle resources) {
        btnDetails.setStyle("-fx-background-color: #93E3D1; -fx-text-fill: #00160A;");
        chEdAttainment.setVisibleRowCount(5);
        chYearsOfExp.setVisibleRowCount(5);
        GenerateApplicationID();
        txtAppID.setText(appID);
        inflateBoxes();
        initCol();
        loadInProgressJobs();

        FilteredList<Job> filteredData = new FilteredList<>(list, b -> true);

        txtSearchJob.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(job -> {
                // If filter text is empty, display all persons.

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (job.getJobID().toLowerCase().indexOf(lowerCaseFilter) != -1 ) {
                    return true; // Filter matches job id
                } else if (job.getClient().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true; // Filter matches client name
                } else if (job.getCompanyName().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true; // Filter matches company name
                } else if (job.getCity().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true; // Filter matches city
                } else if (job.getJobTitle().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true; // Filter matches job title
                } else {
                    return false; // Does not match.
                }
            });
        });

        // 3. Wrap the FilteredList in a SortedList.
        SortedList<Job> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        // 	  Otherwise, sorting the TableView would have no effect.
        sortedData.comparatorProperty().bind(tblJobView.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        tblJobView.setItems(sortedData);

    }

    public void getRecruiterID(String recID) {
        Recruiter recruiter = new Recruiter(recID);
        recruiterID = recruiter.getRecruiterID();
    }

    @FXML
    private void handleClicks(ActionEvent event) {
        if (event.getSource() == btnDetails) {
            btnDetails.setStyle("-fx-background-color: #93E3D1; -fx-text-fill: #00160A;");
            btnExp.setStyle(null);
            btnJob.setStyle(null);
            pnBasic.setVvalue(0.0);
            pnBasic.toFront();
        }
        if (event.getSource() == btnExp) {
            btnExp.setStyle("-fx-background-color: #93E3D1; -fx-text-fill: #00160A;");
            btnDetails.setStyle(null);
            btnJob.setStyle(null);
            pnExp.setVvalue(0.0);
            pnExp.toFront();
        }
        if (event.getSource() == btnJob) {
            btnJob.setStyle("-fx-background-color: #93E3D1; -fx-text-fill: #00160A;");
            btnDetails.setStyle(null);
            btnExp.setStyle(null);
            pnJob.setVvalue(0.0);
            pnJob.toFront();
        }
    }

    @FXML
    private void btnAddSpecOnAction(ActionEvent event) {
        String selectedItem = listSpec.getSelectionModel().getSelectedItem();
        if (listAppSpec.getItems().contains(selectedItem)) {
            AlertMaker.showErrorMessage("Error", "Cannot add duplicate specialization");
        } else {
            listAppSpec.getItems().add(selectedItem);
        }
    }

    @FXML
    private void btnDeleteSpecOnAction(ActionEvent event) {
        String selectedItem = listAppSpec.getSelectionModel().getSelectedItem();
        listAppSpec.getItems().remove(selectedItem);
    }

    @FXML
    private void btnResetSpecOnAction(ActionEvent event) {
        listAppSpec.getItems().clear();
    }

    @FXML
    private void btnClearSearchJobOnAction(ActionEvent event) {
        txtSearchJob.clear();
    }

    @FXML
    private void btnImportOnAction(ActionEvent event) {
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text files", "*.pdf", "*.docx"),
                new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg"),
                new FileChooser.ExtensionFilter("All files", "*.*")
        );

        setTxtDocPath(fileChooser);
    }

    private void setTxtDocPath(FileChooser fileChooser) {
        file = fileChooser.showOpenDialog(getStage());

        if (file != null) {
            try {
                txtDocPath.setText(file.getAbsolutePath());
                path = txtDocPath.getText();
            } catch (Exception ex) {
                Logger.getLogger(AddApplicationController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void btnAddApplicationOnAction(ActionEvent event) {
        Job selected = tblJobView.getSelectionModel().getSelectedItem();
        String status = null;

        if (rbQualified.isSelected()) {
            status = rbQualified.getText();
        } else if (rbDisqualified.isSelected()) {
            status = rbDisqualified.getText();
        }

        if (selected == null) {
            AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Failed to add new application",
                    "Check all the entries and try again");
        } else {

            String applicationID = txtAppID.getText();
            Timestamp appDate = new java.sql.Timestamp(new java.util.Date().getTime());
            String dateOfApplication = RecruitmentSystemUtil.formatDateTimeString(appDate);
            String firstName = txtFirstName.getText();
            String midName = txtMidName.getText();
            String lastName = txtLastName.getText();
            String birthdate = String.valueOf(dBirthDate.getValue());
            String mobNumber = txtMobNumber.getText();
            String telNumber = txtTelNumber.getText();
            String emailAddress = txtEmailAdd.getText();
            String addressLine = txtAddressLine.getText();
            String city = txtCity.getText();
            String province = txtProvince.getText();
            String zipcode = txtZipcode.getText();
            String edAttainment = chEdAttainment.getValue();
            String yearsOfExp = chYearsOfExp.getValue();
            String jobID = selected.getJobID();
            String docPath = path;
            String expJobTitle = txtJobTitle.getText();
            String expCompanyName = txtCompanyName.getText();
            String evalNote = txtNote.getText();
            String recID = recruiterID;

            if (lastName.isEmpty() || emailAddress.isEmpty() || status.isEmpty()
                    || mobNumber.length() < 11 || mobNumber.length() > 11
                    || !zipcode.equalsIgnoreCase(String.valueOf(Integer.parseInt(zipcode)))
                    || zipcode.length() < 4 || zipcode.length() > 4) {
                AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Failed to add new application",
                        "Check all the entries and try again");
            } else {
                Application application = new Application(applicationID, dateOfApplication, firstName, midName,lastName,
                        birthdate, mobNumber, telNumber, emailAddress ,addressLine, city, province, zipcode, edAttainment,
                        yearsOfExp, jobID, docPath, expJobTitle, expCompanyName, status);
                boolean result = dataHelper.insertNewApplication(application);

                if (result) {

                    if (listAppSpec.getItems().isEmpty()) {

                    } else {
                        appSpec.addAll(listAppSpec.getItems());
                        // Insert the list of specialization the applicant has
                        dataHelper.insertList("INSERT INTO application_spec (applicationID, specID) VALUES (?, (SELECT specID " +
                                "FROM specialization WHERE specName = ?))", applicationID, appSpec);
                    }

                    if (evalNote.isEmpty()) {

                    } else {
                        Application appEval = new Application(applicationID, evalNote, recID);
                        dataHelper.insertEvalNote(appEval);
                    }

                    AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "New application added",
                            "Application ID number: " + applicationID + " has been added");

                    clearEntries();
                } else {
                    AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Failed to add new application",
                            "Check all the entries and try again");
                }
            }
        }
    }

    private void GenerateApplicationID() {
        // Generate a new applicationID for the next application input
        appID = String.valueOf(dataHelper.GenID("SELECT applicationID FROM application " +
                "ORDER BY applicationID DESC LIMIT 1"));
    }

    private void inflateBoxes() {
        chEdAttainment.setVisibleRowCount(5);
        chYearsOfExp.setVisibleRowCount(5);
        chEdAttainment.getItems().addAll("Unspecified" , "High School or Equivalent Certification",
                "Associate's Degree",
                "Bachelor's Degree",
                "Master's Degree",
                "Vocational – HS Diploma",
                "Vocational - Degree");
        chYearsOfExp.getItems().addAll("None", "0-1 year", "1-3 years", "4-5 years", "5+ years");
        specList = dataHelper.inflate("SELECT specName FROM specialization");
        listSpec.getItems().addAll(specList);
    }

    @FXML
    private void yearsOfExpOnAction(ActionEvent event) {
        if (chYearsOfExp.getValue().equalsIgnoreCase("None")) {
            txtJobTitle.setDisable(true);
            txtCompanyName.setDisable(true);
        } else {
            txtJobTitle.setDisable(false);
            txtCompanyName.setDisable(false);
        }
    }

    private void clearEntries() {
        GenerateApplicationID();
        txtAppID.setText(String.valueOf(appID));
        txtFirstName.clear();
        txtMidName.clear();
        txtLastName.clear();
        dBirthDate.getEditor().clear();
        txtMobNumber.clear();
        txtTelNumber.clear();
        txtEmailAdd.clear();
        txtCity.clear();
        txtProvince.clear();
        txtAddressLine.clear();
        txtZipcode.clear();
        txtJobTitle.clear();
        txtCompanyName.clear();
        txtSearchJob.clear();
        txtDocPath.clear();
        txtNote.clear();
        listAppSpec.getItems().clear();
        appSpec.clear();
        tblJobView.getSelectionModel().clearSelection();
        chEdAttainment.getSelectionModel().clearSelection();
        chYearsOfExp.getSelectionModel().clearSelection();
        rbDisqualified.setSelected(false);
        rbQualified.setSelected(false);
        txtNote.clear();
        pnBasic.toFront();
        pnBasic.setVvalue(0.0);
        btnDetails.setStyle("-fx-background-color: #93E3D1; -fx-text-fill: #00160A;");
        btnExp.setStyle(null);
        btnJob.setStyle(null);
    }

    private void initCol() {
        colJobID.setCellValueFactory(new PropertyValueFactory<>("jobID"));
        colJobTitle.setCellValueFactory(new PropertyValueFactory<>("jobTitle"));
        colSDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        colClient.setCellValueFactory(new PropertyValueFactory<>("client"));
        colCompany.setCellValueFactory(new PropertyValueFactory<>("companyName"));
    }

    private void loadInProgressJobs() {
        list.clear();

        try {
            // Display in progress jobs view that can be associated to the applicant
            String checkstmt = "SELECT * FROM in_progress_jobs_view";
            stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(checkstmt);
            rs = stmt.executeQuery();

            while (rs.next()) {
                String jobID = rs.getString(1);
                String jobTitle = rs.getString(2);
                String startDate = rs.getString(3);
                String city = rs.getString(4);
                String client = rs.getString(5);
                String companyName = rs.getString(6);

                list.add(new Job(jobID, jobTitle, startDate, city, client, companyName));
            }
        } catch (Exception ex) {
            System.out.println("Exception at AddApplicationController:loadData" + ex.getLocalizedMessage());
        } finally {
            if (stmt != null && rs != null) {
                try {
                    rs.close();
                    stmt.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        tblJobView.setItems(list);
    }

    private void loadWindow(String path) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(path));
            Parent parent = loader.load();

            MainController controller = (MainController) loader.getController();
            controller.getRecruiterID(recruiterID);

            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.setTitle("headhunterPH");
            stage.setScene(new Scene(parent));
            stage.show();
            stage.setResizable(false);
            RecruitmentSystemUtil.setStageIcon(stage);
        }
        catch (IOException ex) {
            System.err.println(ex);
        }
    }

    private Stage getStage() {
        return (Stage) btnBack.getScene().getWindow();
    }

    @FXML
    private void closeStage(ActionEvent event) {
        loadWindow("recruitment/system/ui/main/Main.fxml");
        getStage().close();
    }

    public static class Job {
        String jobID;
        String jobTitle;
        String startDate;
        String city;
        String client;
        String companyName;

        public Job(String jobID, String jobTitle, String startDate, String city,
                   String client, String companyName) {
            this.jobID = jobID;
            this.jobTitle = jobTitle;
            this.startDate = startDate;
            this.city = city;
            this.client = client;
            this.companyName = companyName;
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

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCity() {
            return city;
        }

        public void setClient(String client) {
            this.client = client;
        }

        public String getClient() {
            return client;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getCompanyName() {
            return companyName;
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
