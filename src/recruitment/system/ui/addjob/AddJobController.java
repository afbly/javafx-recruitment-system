package recruitment.system.ui.addjob;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.events.JFXDialogEvent;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import recruitment.system.alert.AlertMaker;
import recruitment.system.data.model.Job;
import recruitment.system.database.DataHelper;
import recruitment.system.database.DatabaseHandler;
import recruitment.system.ui.listjob.ListJobController;
import recruitment.system.ui.main.MainController;
import recruitment.system.util.RecruitmentSystemUtil;

import java.sql.*;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TimeZone;


public class AddJobController implements Initializable {

    private ObservableList<String> categoryList = FXCollections.observableArrayList();
    private ObservableList<String> specList = FXCollections.observableArrayList();
    private ObservableList<String> jobSpec = FXCollections.observableArrayList();
    private ObservableList<Client> clientList = FXCollections.observableArrayList();
    private PreparedStatement stmt;
    private ResultSet rs;
    private String jobID = null;
    private String recruiterID = null;

    DataHelper dataHelper;

    @FXML
    private Button btnDetails;
    @FXML
    private Button btnReq;
    @FXML
    private ScrollPane pnDetails;
    @FXML
    private ScrollPane pnRequirements;
    @FXML
    private TextField txtJobTitle;
    @FXML
    private TextField txtJobID;
    @FXML
    private DatePicker dStartDate;
    @FXML
    private DatePicker dEndDate;
    @FXML
    private TextField txtClient;
    @FXML
    private TextArea txtJobDesc;
    @FXML
    private TextField txtMinSal;
    @FXML
    private TextField txtMaxSal;
    @FXML
    private TextField txtVacancies;
    @FXML
    private TextField txtCity;
    @FXML
    private TextField txtProvince;
    @FXML
    private ComboBox<String> chCategory;
    @FXML
    private ComboBox<String> chJobType;
    @FXML
    private ComboBox<String> chSeniorityLevel;
    @FXML
    private ComboBox<String> chEdAttainment;
    @FXML
    private ComboBox<String> chYearsOfExp;
    @FXML
    private ListView<String> listDefaultSpec;
    @FXML
    private ListView<String> listJobSpec;
    @FXML
    private Button btnCancel;
    @FXML
    private StackPane rootPane;
    @FXML
    private AnchorPane mainContainer;
    @FXML
    private Pane pnClientSearch;
    @FXML
    private TableView<Client> tblClient;
    @FXML
    private TableColumn<Client, String> colClientID;
    @FXML
    private TableColumn<Client, String> colClientName;
    @FXML
    private TableColumn<Client, String> colCompanyName;
    @FXML
    private TableColumn<Client, String> colEmailAddress;
    @FXML
    private TextField txtSearchClient;
    @FXML
    private Button btnClearSearch;
    @FXML
    private Button btnSaveClient;
    @FXML
    private Button btnCancelSearch;

    public void initialize(URL location, ResourceBundle resources) {
        btnDetails.setStyle("-fx-background-color: #93E3D1; -fx-text-fill: #00160A;");
        GenerateJobID();
        txtJobID.setText(jobID);
        inflateBoxes();
        initCol();
        loadClientTable();

        FilteredList<Client> filteredData = new FilteredList<>(clientList, b -> true);

        txtSearchClient.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(client -> {
                // If filter text is empty, display all persons.

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (client.getClientID().toLowerCase().indexOf(lowerCaseFilter) != -1 ) {
                    return true; // Filter matches client id
                } else if (client.getClientName().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true; // Filter matches client name
                } else if (client.getCompanyName().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true; // Filter matches company name
                } else if (client.getEmailAddress().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true; // Filter matches email address
                } else {
                    return false; // Does not match.
                }
            });
        });

        // 3. Wrap the FilteredList in a SortedList.
        SortedList<Client> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        // 	  Otherwise, sorting the TableView would have no effect.
        sortedData.comparatorProperty().bind(tblClient.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        tblClient.setItems(sortedData);

    }

    public void getRecruiterID(String recID) {
        Recruiter recruiter = new Recruiter(recID);
        recruiterID = recruiter.getRecruiterID();
    }

    private void initCol() {
        colClientID.setCellValueFactory(new PropertyValueFactory<>("clientID"));
        colClientName.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        colCompanyName.setCellValueFactory(new PropertyValueFactory<>("companyName"));
        colEmailAddress.setCellValueFactory(new PropertyValueFactory<>("emailAddress"));
    }

    private void loadClientTable() {
        clientList.clear();

        try {
            // Display view of existing records in client_organization table
            String checkstmt = "SELECT * FROM client_search_view";
            stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(checkstmt);
            rs = stmt.executeQuery();

            while (rs.next()) {
                String clientID = rs.getString(1);
                String clientName = rs.getString(2);
                String companyName = rs.getString(3);
                String emailAddress = rs.getString(4);

                clientList.add(new Client(clientID, clientName, companyName, emailAddress));
            }
        } catch (Exception ex) {
            System.out.println("Exception at AddJobController:loadData\n" + ex.getLocalizedMessage());
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

        tblClient.setItems(clientList);
    }

    public void handleClicks(ActionEvent event) {
        if (event.getSource() == btnDetails) {
            btnDetails.setStyle("-fx-background-color: #93E3D1; -fx-text-fill: #00160A;");
            btnReq.setStyle(null);
            pnDetails.setVvalue(0.0);
            pnDetails.toFront();
        }
        if (event.getSource() == btnReq) {
            btnReq.setStyle("-fx-background-color: #93E3D1; -fx-text-fill: #00160A;");
            btnDetails.setStyle(null);
            pnRequirements.setVvalue(0.0);
            pnRequirements.toFront();
        }
    }

    @FXML
    private void btnSearchCompOnAction(ActionEvent event) {
        pnClientSearch.toFront();
        BoxBlur blur = new BoxBlur(3, 3, 3);
        mainContainer.setEffect(blur);
        mainContainer.setDisable(true);
    }

    @FXML
    private void handleSearchBtn(ActionEvent event) {
        if (event.getSource() == btnCancelSearch) {
            mainContainer.setEffect(null);
            mainContainer.setDisable(false);
            pnClientSearch.toBack();
        }
        if (event.getSource() == btnClearSearch) {
            txtSearchClient.clear();
        }
        if (event.getSource() == btnSaveClient) {
            mainContainer.setEffect(null);
            pnClientSearch.toBack();
            mainContainer.setDisable(false);

            Client selected = tblClient.getSelectionModel().getSelectedItem();
            if (selected == null) {
                AlertMaker.showErrorMessage("No client selected", "Please select a client.");
                return;
            }

            txtClient.setText(selected.getClientName());
        }
    }

    @FXML
    private void btnAddSpecOnAction(ActionEvent event) {
        String selectedItem = listDefaultSpec.getSelectionModel().getSelectedItem();
        if (listJobSpec.getItems().contains(selectedItem)) {
            AlertMaker.showErrorMessage("Error", "Cannot add duplicate specialization");
        } else {
            listJobSpec.getItems().add(selectedItem);
        }
    }

    @FXML
    private void btnDeleteSpecOnAction(ActionEvent event) {
        String selectedItem = listJobSpec.getSelectionModel().getSelectedItem();
        listJobSpec.getItems().remove(selectedItem);
    }

    @FXML
    private void btnResetSpecOnAction(ActionEvent event) {
        listJobSpec.getItems().clear();
    }

    @FXML
    private void addJob(ActionEvent event) {
        String jobTitle = txtJobTitle.getText();
        String jobID = txtJobID.getText();
        String jobDesc = txtJobDesc.getText();
        Timestamp pubDate = new java.sql.Timestamp(new java.util.Date().getTime());
        String datePublished = RecruitmentSystemUtil.formatDateTimeString(pubDate);
        String startDate = String.valueOf(dStartDate.getValue());
        String endDate = String.valueOf(dEndDate.getValue());
        String minSalary = txtMinSal.getText();
        String maxSalary = txtMaxSal.getText();
        String noOfVacancies = txtVacancies.getText();
        String city = txtCity.getText();
        String province = txtProvince.getText();
        String jobType = chJobType.getValue();
        String yearsOfExp = chYearsOfExp.getValue();
        String seniority = chSeniorityLevel.getValue();
        String reqEdu = chEdAttainment.getValue();
        String categoryID = chCategory.getValue();
        String clientID = txtClient.getText();
        String recID = recruiterID;
        String status = "In Progress";

        LocalDate sdate = dStartDate.getValue();
        LocalDate edate = dEndDate.getValue();

        LocalDate now = LocalDate.now();

        if (jobTitle.isEmpty() || sdate.isAfter(edate) || edate.isBefore(sdate) || clientID.isEmpty()
                || minSalary.isEmpty() || maxSalary.isEmpty() || noOfVacancies.isEmpty() || city.isEmpty()
                || province.isEmpty() || Integer.parseInt(noOfVacancies) == 0
                || Integer.parseInt(noOfVacancies) > 100 || sdate.equals(now) || sdate.isBefore(now)) {
            AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Insufficient/Invalid Input",
                    "Please check data in all fields.");
            return;
        } else {
            Job job = new Job(jobID, jobTitle, jobDesc, datePublished, startDate, endDate,
                    noOfVacancies, minSalary, maxSalary, city, province, jobType, yearsOfExp,
                    seniority, reqEdu, categoryID, clientID, recID, status);

            boolean result = dataHelper.insertNewJob(job);

            if (result) {
                AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "New job added",
                        jobTitle + " with job ID number: " + jobID + " has been added");

                if (listJobSpec.getItems().isEmpty()) {

                } else {
                    jobSpec.addAll(listJobSpec.getItems());

                    // Once a job is inserted, the list of specialization it desires (if any) must be saved as well
                    dataHelper.insertList("INSERT INTO job_spec (jobID, specID) VALUES (?, (SELECT specID " +
                            "FROM specialization WHERE specName = ?))", jobID, jobSpec);
                }

                clearEntries();
            } else {
                AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Failed to add new job",
                        "Check all the entries and try again");
            }
        }

    }

    private void clearEntries() {
        GenerateJobID();
        txtJobID.setText(jobID);
        txtJobTitle.clear();
        txtJobDesc.clear();
        dStartDate.getEditor().clear();
        dEndDate.getEditor().clear();
        txtClient.clear();
        txtMinSal.clear();
        txtMaxSal.clear();
        txtCity.clear();
        txtProvince.clear();
        txtVacancies.clear();
        chCategory.getSelectionModel().select(-1);
        chJobType.getSelectionModel().select(-1);
        chEdAttainment.getSelectionModel().select(-1);
        chSeniorityLevel.getSelectionModel().select(-1);
        chYearsOfExp.getSelectionModel().select(-1);
        listJobSpec.getItems().clear();
        jobSpec.clear();
        pnDetails.toFront();
        pnDetails.setVvalue(0.0);
        btnDetails.setStyle("-fx-background-color: #93E3D1; -fx-text-fill: #00160A;");
        btnReq.setStyle(null);
    }

    private void GenerateJobID() {
        // Generate new ID for the next job to be inserted
        jobID = String.valueOf(dataHelper.GenID("SELECT jobID FROM job ORDER BY jobID DESC LIMIT 1"));
    }

    private void inflateBoxes() {
        chCategory.setVisibleRowCount(5);
        chSeniorityLevel.setVisibleRowCount(5);
        chEdAttainment.setVisibleRowCount(5);
        chYearsOfExp.setVisibleRowCount(5);

        chCategory.getItems().addAll("Accounting/Auditing", "Administrative", "Advertising",
                "Agriculture, Food, and Natural Resources", "Business Analyst", "Financial Analyst",
                "Data Analyst", "Art/Creative", "Business Development", "Consulting", "Customer Service",
                "Distribution", "Design", "Education", "Engineering", "Finance", "General Business",
                "Healthcare Provider", "Human Resources", "Information Technology", "Legal", "Management",
                "Manufacturing", "Marketing", "Others", "Public Relations", "Purchasing", "Product Management",
                "Project Management", "Production", "Quality Assurance", "Research", "Sales", "Science",
                "Strategy/Planning", "Supply Chain", "Telemarketing", "Training", "Writing/Editing"
        );
        chJobType.getItems().addAll("Full Time", "Part Time", "Contract", "Temporary");
        chSeniorityLevel.getItems().addAll("Any" , "Internship", "Entry-level", "Associate", "Mid-Senior Level", "Director", "Executive");
        chEdAttainment.getItems().addAll("Unspecified" , "High School or Equivalent Certification",
                "Associate's Degree",
                "Bachelor's Degree",
                "Master's Degree",
                "Vocational – HS Diploma",
                "Vocational - Degree");
        chYearsOfExp.getItems().addAll("None", "0-1 year", "1-3 years", "4-5 years", "5+ years");
        specList = dataHelper.inflate("SELECT specName FROM specialization");
        listDefaultSpec.getItems().addAll(specList);
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

    @FXML
    private void closeStage(ActionEvent event) {
        //loadWindow("recruitment/system/ui/listjob/ListJob.fxml");
        loadWindow("recruitment/system/ui/main/Main.fxml");
        getStage().close();
    }

    private Stage getStage() {
        return (Stage) btnCancel.getScene().getWindow();
    }

    public static class Client {
        String clientID;
        String clientName;
        String companyName;
        String emailAddress;

        public Client(String clientID, String clientName, String companyName, String emailAddress) {
            this.clientID = clientID;
            this.clientName = clientName;
            this.companyName = companyName;
            this.emailAddress = emailAddress;
        }


        public void setClientID(String clientID) {
            this.clientID = clientID;
        }

        public String getClientID() {
            return clientID;
        }

        public void setClientName(String clientName) {
            this.clientName = clientName;
        }

        public String getClientName() {
            return clientName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
        }

        public String getEmailAddress() {
            return emailAddress;
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
