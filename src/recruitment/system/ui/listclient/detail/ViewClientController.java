package recruitment.system.ui.listclient.detail;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import recruitment.system.alert.AlertMaker;
import recruitment.system.data.model.Client;
import recruitment.system.database.DataHelper;
import recruitment.system.database.DatabaseHandler;
import recruitment.system.ui.listclient.ListClientController;
import recruitment.system.ui.listjob.detail.ViewJobDetailController;
import recruitment.system.ui.main.MainController;
import recruitment.system.util.RecruitmentSystemUtil;

import java.io.IOException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class ViewClientController implements Initializable {

    private CallableStatement cstmt;
    private ResultSet rs;
    private ObservableList<Job> list = FXCollections.observableArrayList();
    private String recruiterID = null;
    private String clientID = null;
    private boolean isInEditMode;

    DataHelper dataHelper;
    DatabaseHandler databaseHandler;

    @FXML
    private AnchorPane mainContainer;
    @FXML
    private StackPane rootPane;
    @FXML
    private Label hClientName;
    @FXML
    private Label hCompName;
    @FXML
    private TextField txtClientID;
    @FXML
    private TextField txtCompIndustry;
    @FXML
    private ComboBox<String> chIndustry;
    @FXML
    private TextField txtCompName;
    @FXML
    private TextField txtFirstName;
    @FXML
    private TextField txtMidName;
    @FXML
    private TextField txtLastName;
    @FXML
    private TextField txtMobNum;
    @FXML
    private TextField txtTelNum;
    @FXML
    private TextField txtEmailAdd;
    @FXML
    private TextField txtCity;
    @FXML
    private TextField txtProvince;
    @FXML
    private TextField txtZipcode;
    @FXML
    private TextArea txtAddressLine;
    @FXML
    private Button btnCancelChanges;
    @FXML
    private Button btnSaveChanges;
    @FXML
    private Button btnBack;
    @FXML
    private Button btnEditClient;
    @FXML
    private Button btnDeleteClient;
    @FXML
    private CheckBox chbAll;
    @FXML
    private CheckBox chbInProgress;
    @FXML
    private CheckBox chbFilled;
    @FXML
    private CheckBox chbCancelled;
    @FXML
    private TableView<Job> tableView;
    @FXML
    private TableColumn<Job, String> colJobID;
    @FXML
    private TableColumn<Job, String> colJobName;
    @FXML
    private TableColumn<Job, String> colDatePub;
    @FXML
    private TableColumn<Job, String> colSDate;
    @FXML
    private TableColumn<Job, String> colJobStatus;
    @FXML
    private TableColumn<Job, String> colLastUpdate;
    @FXML
    private TableColumn<Job, String> colRecruiter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        databaseHandler = DatabaseHandler.getInstance();

        inViewMode();
        inflateIndustry();
        initCol();

        chbAll.setSelected(true);

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

    }

    public void inflateUI(ListClientController.Client client, String recruiterID) {
        clientID = client.getClientID();
        this.recruiterID = recruiterID;
        txtClientID.setText(clientID);
        loadData(clientID);
        loadJobTable(1, null);
    }

    private void loadData(String clientID) {
        try {

            String firstname = null;
            String midName = null;
            String lastName = null;
            // Display client details
            String checkstmt = "{CALL disp_client (?)}";
            cstmt = databaseHandler.getConnection().prepareCall(checkstmt);
            cstmt.setString(1, clientID);
            rs = cstmt.executeQuery();

            if (rs.next()) {
                txtFirstName.setText(rs.getString(1));
                txtMidName.setText(rs.getString(2));
                txtLastName.setText(rs.getString(3));
                firstname = rs.getString(1);
                midName = rs.getString(2);
                lastName = rs.getString(3);
                hCompName.setText(rs.getString(4));
                txtCompName.setText(rs.getString(4));
                txtMobNum.setText(rs.getString(5));
                txtTelNum.setText(rs.getString(6));
                txtEmailAdd.setText(rs.getString(7));
                txtAddressLine.setText(rs.getString(8));
                txtCity.setText(rs.getString(9));
                txtProvince.setText(rs.getString(10));
                txtZipcode.setText(rs.getString(11));
                txtCompIndustry.setText(rs.getString(12));
            }

            if (midName == null) {
                hClientName.setText(firstname + " " + lastName);
            } else {
                hClientName.setText(firstname + " " +
                        midName + " " + lastName);
            }
        } catch (Exception ex) {
            System.out.println("Exception at ViewClientController:loadData\n" + ex.getLocalizedMessage());
            ex.printStackTrace();
        } finally {
            if(rs != null && cstmt != null) {
                try {
                    rs.close();
                    cstmt.close();
                }  catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void initCol() {
        colJobID.setCellValueFactory(new PropertyValueFactory<>("jobID"));
        colJobName.setCellValueFactory(new PropertyValueFactory<>("jobName"));
        colDatePub.setCellValueFactory(new PropertyValueFactory<>("datePublished"));
        colSDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colJobStatus.setCellValueFactory(new PropertyValueFactory<>("jobStatus"));
        colLastUpdate.setCellValueFactory(new PropertyValueFactory<>("lastUpdate"));
        colRecruiter.setCellValueFactory(new PropertyValueFactory<>("recruiter"));

    }

    private void loadJobTable(int state, String status) {
        list.clear();
        try {
            // Display table base on state or filter
            String checkstmt = "{CALL filter_client_job_order (?, ?, ?)}";
            cstmt = DatabaseHandler.getInstance().getConnection().prepareCall(checkstmt);
            cstmt.setString(1, clientID);
            cstmt.setInt(2, state);
            cstmt.setString(3, status);
            rs = cstmt.executeQuery();

            while (rs.next()) {
                String jobID = rs.getString(1);
                String jobName = rs.getString(2);
                String datePublished = rs.getString(3);
                String startDate = rs.getString(4);
                String jobStatus = rs.getString(5);
                String lastUpdate = rs.getString(6);
                String recruiter = rs.getString(7);

                list.add(new Job(jobID, jobName, datePublished, startDate, jobStatus, lastUpdate, recruiter));
            }
        } catch (Exception ex) {
            System.out.println("Exception at ViewClientController:loadJobTable\n" + ex.getLocalizedMessage());
            ex.printStackTrace();
        } finally {
            if(rs != null && cstmt != null) {
                try {
                    rs.close();
                    cstmt.close();
                }  catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        tableView.setItems(list);
    }

    private void inViewMode() {
        isInEditMode = false;
        txtFirstName.setEditable(false);
        txtMidName.setEditable(false);
        txtLastName.setEditable(false);
        txtMobNum.setEditable(false);
        txtTelNum.setEditable(false);
        txtEmailAdd.setEditable(false);
        txtCity.setEditable(false);
        txtProvince.setEditable(false);
        txtZipcode.setEditable(false);
        txtAddressLine.setEditable(false);
        txtCompIndustry.setEditable(false);
        txtCompName.setEditable(false);

        btnSaveChanges.setVisible(false);
        btnCancelChanges.setVisible(false);
        btnEditClient.setDisable(false);
        btnDeleteClient.setDisable(false);
        txtCompIndustry.toFront();
        chIndustry.toBack();
    }

    private void inEditMode() {
        isInEditMode = true;
        txtFirstName.setEditable(true);
        txtMidName.setEditable(true);
        txtLastName.setEditable(true);
        txtMobNum.setEditable(true);
        txtTelNum.setEditable(true);
        txtEmailAdd.setEditable(true);
        txtCity.setEditable(true);
        txtProvince.setEditable(true);
        txtZipcode.setEditable(true);
        txtAddressLine.setEditable(true);
        txtCompIndustry.setEditable(true);
        txtCompName.setEditable(true);

        btnSaveChanges.setVisible(true);
        btnCancelChanges.setVisible(true);
        btnEditClient.setDisable(true);
        btnDeleteClient.setDisable(true);
        chIndustry.toFront();
        chIndustry.setValue(txtCompIndustry.getText());
        txtCompIndustry.toBack();
    }

    private void inflateIndustry() {
        chIndustry.getItems().addAll("Accounting and Finance",
                "Advertising",
                "BPO",
                "Call Center",
                "Consumer Products",
                "Engineering",
                "Financial Services",
                "Food & Beverages",
                "Government",
                "Healthcare",
                "Human Resources",
                "Information Technology",
                "Legal",
                "Manufacturing",
                "Non-Profit Organization",
                "Others",
                "Real Estate",
                "Retail",
                "Service & Admin",
                "Tourism",
                "Training & Education",
                "Transportation");
    }

    @FXML
    private void handleFilterCheckbox(ActionEvent event) {
        if (chbAll.isSelected() || (chbInProgress.isSelected() && chbFilled.isSelected() && chbCancelled.isSelected())) {
            int state = 1;
            chbInProgress.setSelected(false);
            chbFilled.setSelected(false);
            chbCancelled.setSelected(false);
            chbAll.setSelected(true);
            loadJobTable(state, null);
        } else if (chbInProgress.isSelected() && chbFilled.isSelected() && chbCancelled.isSelected()) {
            int state = 1;
            chbInProgress.setSelected(false);
            chbFilled.setSelected(false);
            chbCancelled.setSelected(false);
            chbAll.setSelected(true);
            loadJobTable(state, null);
        } else if (chbInProgress.isSelected() && chbCancelled.isSelected()) {
            int state = 3;
            chbAll.setSelected(false);
            loadJobTable(state, null);
        } else if (chbInProgress.isSelected() && chbFilled.isSelected()) {
            int state = 4;
            chbAll.setSelected(false);
            loadJobTable(state, null);
        } else if (chbFilled.isSelected() && chbCancelled.isSelected()) {
            int state = 5;
            chbAll.setSelected(false);
            loadJobTable(state, null);
        } else if (chbInProgress.isSelected() || chbFilled.isSelected() || chbCancelled.isSelected()){
            int state = 2;
            String status = null;
            if (chbInProgress.isSelected())
                status = chbInProgress.getText();
            else if (chbFilled.isSelected())
                status = chbFilled.getText();
            else if (chbCancelled.isSelected())
                status = chbCancelled.getText();
            else {
                chbAll.setSelected(true);
                loadJobTable(1, status);
            }
            chbAll.setSelected(false);
            loadJobTable(state, status);
        } else {
            loadJobTable(1, null);
        }

    }

    @FXML
    private void btnEditClientOnAction(ActionEvent event) {
        inEditMode();
    }

    @FXML
    private void btnCancelChangesOnAction(ActionEvent event) {
        inViewMode();
    }

    @FXML
    private void btnSaveChangesOnAction(ActionEvent event) {
        String clientID = txtClientID.getText();
        String firstName = txtFirstName.getText();
        String midName = txtMidName.getText();
        String lastName = txtLastName.getText();
        String compName = txtCompName.getText();
        String mobNumber = txtMobNum.getText();
        String telNumber = txtTelNum.getText();
        String emailAddress = txtEmailAdd.getText();
        String addressLine = txtAddressLine.getText();
        String city = txtCity.getText();
        String province = txtProvince.getText();
        String zipcode = txtZipcode.getText();
        String compIndustry = chIndustry.getValue();

        if (lastName.isEmpty() || emailAddress.isEmpty()
                || !zipcode.equalsIgnoreCase(String.valueOf(Integer.parseInt(zipcode)))
                || zipcode.length() < 4 || zipcode.length() > 4) {
            AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Failed to update client",
                    "Check all the entries and try again");
        } else {
            Client client = new Client(clientID, firstName, midName, lastName,compName, mobNumber, telNumber, emailAddress,
                    addressLine, city, province, zipcode, compIndustry);
            boolean result = dataHelper.updateClientDet(client);

            if (result) {
                AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Client updated",
                        "Client number: " + clientID + " has been updated");

                loadData(clientID);
                inViewMode();
            } else {
                AlertMaker.showSimpleAlert("Failed", "Please check all input details.");
                inEditMode();
            }
        }
    }

    @FXML
    private void btnDeleteClientOnAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deleting client");
        alert.setContentText("Are you sure want to delete client: " + clientID + " ?");
        Optional<ButtonType> answer = alert.showAndWait();

        if (answer.get() == ButtonType.OK) {
            // Delete client from the database
            Boolean result = DatabaseHandler.getInstance().deleteSelection(clientID,
                    "DELETE FROM client_organization WHERE clientID = ?");
            if (result) {
                AlertMaker.showSimpleAlert("Client Deleted", "Client number: " +
                        clientID + " was deleted successfully.");
                loadWindow("recruitment/system/ui/main/Main.fxml");
            } else {
                AlertMaker.showSimpleAlert("Failed", "Client number: " +
                        clientID + " could not be deleted");
            }
        } else {
            AlertMaker.showSimpleAlert("Deletion cancelled", "Deletion process cancelled");
        }
    }

    @FXML
    private void closeStage(ActionEvent event) {
        if (isInEditMode) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Are you sure you want to leave?");
            alert.setContentText("Changes you made might not be saved.");
            Optional<ButtonType> answer = alert.showAndWait();

            if ((answer.get() == ButtonType.OK)) {

            } else {
               loadData(clientID);
            }
        } else {
            loadWindow("recruitment/system/ui/main/Main.fxml");
        }
    }

    private Stage getStage() {
        return (Stage) btnBack.getScene().getWindow();
    }

    private void loadWindow(String path) {
        try {
            getStage().close();

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

    public static class Job {
        String jobID;
        String jobName;
        String datePublished;
        String startDate;
        String jobStatus;
        String lastUpdate;
        String recruiter;

        public Job(String jobID, String jobName, String datePublished, String startDate,
                   String jobStatus, String lastUpdate, String recruiter) {
            this.jobID = jobID;
            this.jobName = jobName;
            this.datePublished = datePublished;
            this.startDate = startDate;
            this.jobStatus = jobStatus;
            this.lastUpdate = lastUpdate;
            this.recruiter = recruiter;
        }

        public void setJobID(String jobID) {
            this.jobID = jobID;
        }

        public String getJobID() {
            return jobID;
        }

        public void setJobName(String jobName) {
            this.jobName = jobName;
        }

        public String getJobName() {
            return jobName;
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

        public void setJobStatus(String jobStatus) {
            this.jobStatus = jobStatus;
        }

        public String getJobStatus() {
            return jobStatus;
        }

        public void setLastUpdate(String lastUpdate) {
            this.lastUpdate = lastUpdate;
        }

        public String getLastUpdate() {
            return lastUpdate;
        }

        public void setRecruiter(String recruiter) {
            this.recruiter = recruiter;
        }

        public String getRecruiter() {
            return recruiter;
        }
    }
}
