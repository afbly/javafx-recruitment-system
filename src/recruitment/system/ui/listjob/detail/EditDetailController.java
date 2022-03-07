package recruitment.system.ui.listjob.detail;

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
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import recruitment.system.alert.AlertMaker;
import recruitment.system.data.model.Job;
import recruitment.system.database.DataHelper;
import recruitment.system.database.DatabaseHandler;
import recruitment.system.ui.main.MainController;
import recruitment.system.util.RecruitmentSystemUtil;
import java.io.IOException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class EditDetailController implements Initializable {

    private PreparedStatement stmt;
    private CallableStatement cstmt;
    private ResultSet rs;
    private ObservableList<Client> clientList = FXCollections.observableArrayList();
    private String jobID = null;
    private String recruiterID = null;

    DataHelper dataHelper;
    DatabaseHandler databaseHandler;


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
    @FXML
    private TextField txtJobTitle;
    @FXML
    private ComboBox<String> chStatus;
    @FXML
    private TextField txtJobID;
    @FXML
    private TextField txtdatePub;
    @FXML
    private TextField txtSDate;
    @FXML
    private TextField txtEDate;
    @FXML
    private TextField txtClient;
    @FXML
    private ComboBox<String> chCategory;
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
    private Button btnSave;
    @FXML
    private Button btnCancel;
    @FXML
    private ComboBox<String> chJobType;
    @FXML
    private ComboBox<String> chSeniorityLevel;
    @FXML
    private ComboBox<String> chEdAttainment;
    @FXML
    private ComboBox<String> chYearsOfExp;
    @FXML
    private StackPane rootPane;
    @FXML
    private ScrollPane mainContainer;

    public void initialize(URL location, ResourceBundle resources) {
        databaseHandler = DatabaseHandler.getInstance();

        inflateBoxes();
        initCol();
        loadClientTable();
        System.out.println("EditDetailController: " + recruiterID);

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

    public void inflateUI(String jobID, String recruiterID) {
        this.jobID = jobID;
        setDetails(jobID);
        this.recruiterID = recruiterID;
    }

    private void setDetails(String jobID) {

        String jobTitle = null;
        String jobDesc = null;
        String pubDate = null;
        String startDate = null;
        String endDate = null;
        String noOfVacancies = null;
        String minSal = null;
        String maxSal = null;
        String city = null;
        String province = null;
        String jobType = null;
        String yearsOfExp = null;
        String seniority = null;
        String edReq = null;
        String category = null;
        String client = null;
        String jobStatus = null;

        try {
            String checkstmt = "{CALL disp_job (?)}";
            cstmt = databaseHandler.getInstance().getConnection().prepareCall(checkstmt);
            cstmt.setString(1, jobID);
            rs = cstmt.executeQuery();

            if (rs.next()) {
                jobTitle = rs.getString(1);
                jobDesc = rs.getString(2);
                pubDate = RecruitmentSystemUtil.getDateString(rs.getDate(3));
                startDate = RecruitmentSystemUtil.getDateString(rs.getDate(4));
                endDate = RecruitmentSystemUtil.getDateString(rs.getDate(5));
                noOfVacancies = String.valueOf(rs.getInt(6));
                minSal = String.valueOf(rs.getInt(7));
                maxSal = String.valueOf(rs.getInt(8));
                city = rs.getString(9);
                province = rs.getString(10);
                jobType = rs.getString(11);
                yearsOfExp = rs.getString(12);
                seniority = rs.getString(13);
                edReq = rs.getString(14);
                category = rs.getString(15);
                client = rs.getString(17);
                jobStatus = rs.getString(18);
            }

            txtJobID.setText(jobID);
            txtJobTitle.setText(jobTitle);
            txtJobDesc.setText(jobDesc);
            txtdatePub.setText(pubDate);
            txtSDate.setText(startDate);
            txtEDate.setText(endDate);
            txtVacancies.setText(String.valueOf(noOfVacancies));
            txtMinSal.setText(String.valueOf(minSal));
            txtMaxSal.setText(String.valueOf(maxSal));
            txtCity.setText(city);
            txtProvince.setText(province);
            chJobType.setValue(jobType);
            chYearsOfExp.setValue(yearsOfExp);
            chSeniorityLevel.setValue(seniority);
            chEdAttainment.setValue(edReq);
            chCategory.setValue(category);
            txtClient.setText(client);
            chStatus.setValue(jobStatus);

            if (jobStatus.equalsIgnoreCase("Filled") || jobStatus.equalsIgnoreCase("Cancelled")) {
                chStatus.setDisable(true);
                btnSave.setDisable(true);
            }

        } catch (Exception ex) {
            System.out.println("Exception at EditDetailController:setDetails" + ex.getLocalizedMessage());
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

    @FXML
    private void btnSearchClientOnAction(ActionEvent event) {
        pnClientSearch.toFront();
        BoxBlur blur = new BoxBlur(3, 3, 3);
        mainContainer.setEffect(blur);
        mainContainer.setDisable(true);
    }

    @FXML
    private void handleSearchBtn(ActionEvent event) {
        if (event.getSource() == btnCancelSearch) {
            mainContainer.setEffect(null);
            pnClientSearch.toBack();
            mainContainer.setDisable(false);
        }
        if (event.getSource() == btnClearSearch) {
            txtSearchClient.clear();
        }
        if (event.getSource() == btnSaveClient) {
            mainContainer.setDisable(false);
            mainContainer.setEffect(null);
            pnClientSearch.toBack();

            Client selected = tblClient.getSelectionModel().getSelectedItem();
            if (selected == null) {
                AlertMaker.showErrorMessage("No client selected", "");

                return;
            }

            txtClient.setText(selected.getClientName());
        }
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
            String checkstmt = "SELECT * FROM client_search_view";
            stmt = databaseHandler.getConnection().prepareStatement(checkstmt);
            rs = stmt.executeQuery();

            while (rs.next()) {
                String clientID = rs.getString(1);
                String clientName = rs.getString(2);
                String companyName = rs.getString(3);
                String emailAddress = rs.getString(4);

                clientList.add(new Client(clientID, clientName, companyName, emailAddress));
            }
        } catch (Exception ex) {
            System.out.println("Exception at EditDetailController:loadData\n" + ex.getLocalizedMessage());
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

    private void inflateBoxes() {
        chCategory.setVisibleRowCount(5);
        chStatus.setVisibleRowCount(5);
        chJobType.setVisibleRowCount(5);
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
        chStatus.getItems().addAll("In Progress", "Filled", "Cancelled");
        chEdAttainment.getItems().addAll("Unspecified" , "High School or Equivalent Certification",
                "Associate's Degree",
                "Bachelor's Degree",
                "Master's Degree",
                "Vocational – HS Diploma",
                "Vocational - Degree");
        chSeniorityLevel.getItems().addAll("Any" , "Internship", "Entry-level", "Associate", "Mid-Senior Level", "Director", "Executive");
        chJobType.getItems().addAll("Full Time", "Part Time", "Contract", "Temporary");
        chYearsOfExp.getItems().addAll("None", "0-1 year", "1-3 years", "4-5 years", "5+ years");
    }

    @FXML
    private void btnSaveOnAction(ActionEvent event) {
        try {
            String jobID = txtJobID.getText();
            String jobTitle = txtJobTitle.getText();
            String jobDesc = txtJobDesc.getText();
            String numVacancies = txtVacancies.getText();
            String minSal = txtMinSal.getText();
            String maxSal = txtMaxSal.getText();
            String city = txtCity.getText();
            String province = txtProvince.getText();
            String jobType = chJobType.getValue();
            String yearsOfExp = chYearsOfExp.getValue();
            String seniority = chSeniorityLevel.getValue();
            String reqEdu = chEdAttainment.getValue();
            String category = chCategory.getValue();
            String clientName = txtClient.getText();
            System.out.println(clientName);
            String jobStatus = chStatus.getValue();

            if (chStatus.getValue().equalsIgnoreCase("Filled")
                    || chStatus.getValue().equalsIgnoreCase("Cancelled")) {
                numVacancies = "0";
            }

            if (Integer.parseInt(minSal) > Integer.parseInt(maxSal)
                || minSal.isEmpty() || maxSal.isEmpty()
                || minSal.equalsIgnoreCase("0") || maxSal.equalsIgnoreCase("0")
                || Integer.parseInt(numVacancies) <= 0) {
                AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Failed to update job",
                        "Check all the entries and try again");
            } else {
                Job job = new Job(jobID, jobTitle, jobDesc, numVacancies, minSal, maxSal, city, province, jobType,
                        yearsOfExp, seniority, reqEdu, category, clientName, jobStatus);
                boolean result = dataHelper.updateJobDet(job);

                if (result) {
                    AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Job Updated",
                            txtJobTitle.getText() + " has been updated");
                    setDetails(jobID);
                } else {
                    AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Failed to update job",
                            "Check all the entries and try again");
                }
            }
        } catch (Exception exception) {
            AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Failed to update job",
                    "Check all the entries and try again");
        }
    }


    private Stage getStage() {
        return (Stage) btnCancel.getScene().getWindow();
    }

    @FXML
    private void closeStage(ActionEvent event) {
        try {
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
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    private void close() {
        try {
            getStage().close();

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("recruitment/system/ui/main/Main.fxml"));
            Parent parent = loader.load();

            MainController controller = (MainController) loader.getController();
            controller.getRecruiterID(recruiterID);

            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.setTitle("headhunterPH");
            stage.setScene(new Scene(parent));
            stage.setResizable(false);
            stage.show();

            RecruitmentSystemUtil.setStageIcon(stage);
        } catch (IOException ex) {
            System.err.println(ex);
        }
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

}
