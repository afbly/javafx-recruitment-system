package recruitment.system.ui.listjob.detail;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import recruitment.system.alert.AlertMaker;
import recruitment.system.database.DataHelper;
import recruitment.system.database.DatabaseHandler;
import recruitment.system.ui.listapplication.ListApplicationController;
import recruitment.system.ui.listapplication.detail.ViewApplicationController;
import recruitment.system.ui.listclient.detail.ViewClientController;
import recruitment.system.ui.listjob.ListJobController;
import recruitment.system.ui.main.MainController;
import recruitment.system.util.RecruitmentSystemUtil;
import java.io.IOException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;


public class ViewJobDetailController implements Initializable {

    private PreparedStatement stmt;
    private CallableStatement cstmt;
    private ResultSet rs;
    private ObservableList<Application> list = FXCollections.observableArrayList();
    private String recruiterID = null;
    private String jobID = null;

    DataHelper dataHelper;

    @FXML
    private Label lblJobTitle;
    @FXML
    private Label lblRecruiter;
    @FXML
    private Label lblJobID;
    @FXML
    private Label lblClient;
    @FXML
    private Label lblPubDate;
    @FXML
    private Label lblEndDate;
    @FXML
    private Label lblStartDate;
    @FXML
    private Label lblEmpType;
    @FXML
    private Label lblCategory;
    @FXML
    private Label lblLocation;
    @FXML
    private Label lblVacancies;
    @FXML
    private Label lblSalary;
    @FXML
    private Label lblJobDesc;
    @FXML
    private Label lblEdReq;
    @FXML
    private Label lblYearsExp;
    @FXML
    private Label lblSeniority;
    @FXML
    private FlowPane specPane;
    @FXML
    private Label hJobTitle;
    @FXML
    private Label hClient;
    @FXML
    private Label hStatus;
    @FXML
    private Button btnBack;
    @FXML
    private Label lblSubmissions;
    @FXML
    private Label lblOffered;
    @FXML
    private Label lblHired;
    @FXML
    private TableView<Application> tblSubmissions;
    @FXML
    private TableColumn<Application, String> colAppID;
    @FXML
    private TableColumn<Application, String> colAppName;
    @FXML
    private TableColumn<Application, String> colAppDate;
    @FXML
    private TableColumn<Application, String> colAppMobNo;
    @FXML
    private TableColumn<Application, String> colAppEmailAdd;
    @FXML
    private TableColumn<Application, String> colAppStatus;
    @FXML
    private ComboBox<String> cbSubmissionFilter;

    public void initialize(URL location, ResourceBundle resources) {

        cbSubmissionFilter.getItems().addAll("All","Qualified", "Disqualified", "Interview-to-be-Scheduled",
                "Interview-Scheduled", "Rejected-for-Interview", "To-be-Offered", "Offer-Accepted",
                "Offer-Declined", "Offer-Made", "Hired", "Rejected");
        cbSubmissionFilter.setValue("All");

        tblSubmissions.setRowFactory( tv -> {
            TableRow<Application> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    Application selectedForView = row.getItem();

                    loadWindow(selectedForView);
                }
            });
            return row;
        });
    }

    public void inflateUI(String jobID, String recruiterID) {
        this.jobID = jobID;
        this.recruiterID = recruiterID;
        setDetails(jobID);
        setSpecPane(jobID);

        // Count how many applications a job has, as well as the number of applicants who
        // are being offered, and already hired
        lblSubmissions.setText(String.valueOf(dataHelper.setCount(jobID, "{CALL count_applications (?)}")));
        lblOffered.setText(String.valueOf(dataHelper.setCount(jobID, "{CALL count_offered (?)}")));
        lblHired.setText(String.valueOf(dataHelper.setCount(jobID, "{CALL count_hired (?)}")));

        initCol();
        loadData();
    }

    public void initCol() {
        colAppID.setCellValueFactory(new PropertyValueFactory<>("applicationID"));
        colAppName.setCellValueFactory(new PropertyValueFactory<>("applicantName"));
        colAppDate.setCellValueFactory(new PropertyValueFactory<>("dateOfApplication"));
        colAppMobNo.setCellValueFactory(new PropertyValueFactory<>("mobNumber"));
        colAppEmailAdd.setCellValueFactory(new PropertyValueFactory<>("emailAddress"));
        colAppStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadData() {
        list.clear();

        try {
            // Display submissions for the current job in view, sort by
            // application's specializations matching with ob
            String checkstmt = "{CALL view_job_submission (?)}";
            cstmt = DatabaseHandler.getInstance().getConnection().prepareCall(checkstmt);
            cstmt.setString(1, jobID);
            rs = cstmt.executeQuery();

            while (rs.next()) {
                String appID = rs.getString(1);
                String applicantName = rs.getString(2);
                String appDate = rs.getString(3);
                String appMobNo = rs.getString(4);
                String appEmailAdd = rs.getString(5);
                String appStatus = rs.getString(6);

                list.add(new Application(appID, applicantName, appDate, appMobNo, appEmailAdd, appStatus));
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

        tblSubmissions.setItems(list);
    }

    private void loadData(String status) {
        list.clear();

        try {
            String checkstmt = "{CALL view_job_submission_filtered (?, ?)}";
            cstmt = DatabaseHandler.getInstance().getConnection().prepareCall(checkstmt);
            cstmt.setString(1, jobID);
            cstmt.setString(2, status);
            rs = cstmt.executeQuery();

            while (rs.next()) {
                String appID = rs.getString(1);
                String applicantName = rs.getString(2);
                String appDate = rs.getString(3);
                String appMobNo = rs.getString(4);
                String appEmailAdd = rs.getString(5);
                String appStatus = rs.getString(6);

                list.add(new Application(appID, applicantName, appDate, appMobNo, appEmailAdd, appStatus));
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

        tblSubmissions.setItems(list);
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
        String companyName = null;
        String client = null;
        String status = null;
        String recruiter = null;

        try {

            // Display job details
            String checkstmt = "{CALL disp_job(?)}";
            cstmt = DatabaseHandler.getInstance().getConnection().prepareCall(checkstmt);
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
                companyName = rs.getString(16);
                client = rs.getString(17);
                status = rs.getString(18);
                recruiter = rs.getString(19);
            }

            hJobTitle.setText(jobTitle);
            hClient.setText(companyName);
            String location = city + ", " + province;

            if (status.equalsIgnoreCase("In Progress")) {
                hStatus.setStyle("-fx-background-radius: 15; -fx-background-color: #64FCD9; -fx-text-fill: #000000;");
                hStatus.setText(status);
            } else {
                hStatus.setStyle("-fx-background-radius:  15; -fx-background-color: #CC0000; -fx-text-fill: #FFFFFF;");
                hStatus.setText(status);
            }

            lblJobID.setText(jobID);
            lblJobTitle.setText(jobTitle);
            lblClient.setText(client);
            lblRecruiter.setText(recruiter);
            lblLocation.setText(location);
            lblVacancies.setText(noOfVacancies);
            lblStartDate.setText(startDate);
            lblPubDate.setText(pubDate);
            lblEndDate.setText(endDate);
            lblJobDesc.setText(jobDesc);
            lblSalary.setText(minSal + " - " + maxSal);
            lblEmpType.setText(jobType);
            lblSeniority.setText(seniority);
            lblYearsExp.setText(yearsOfExp);
            lblEdReq.setText(edReq);
            lblCategory.setText(category);
        } catch (Exception ex) {
            System.out.println("Exception at ViewJobDetailController: loadData" + ex.getLocalizedMessage());
            ex.printStackTrace();
        } finally {
            if(cstmt != null && rs != null) {
                try {
                    rs.close();
                    cstmt.close();
                }  catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void setSpecPane(String jobID) {
        ArrayList<Label> list = new ArrayList<>();

        try {

            // Display the specializations the job desires
            String checkstmt = "SELECT specName FROM specialization S JOIN job_spec JS " +
                    "ON S.specID = JS.specID JOIN job J ON j.jobID = JS.jobID WHERE J.jobID = ?";
            String specName = null;
            stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(checkstmt);
            stmt.setString(1, jobID);
            rs = stmt.executeQuery();

            while (rs.next()) {
                specName = rs.getString(1);
                list.add(getSpec(specName));
            }

            specPane.getChildren().addAll(list);
        } catch (Exception ex) {
            System.out.println("Exception at ViewDetailController: setSpecPane\n" + ex.getLocalizedMessage());
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
    }

    private Label getSpec(String specName) {

        Circle circle = new Circle();
        circle.setCenterX(0.0f);
        circle.setCenterY(0.0f);
        circle.setRadius(4.0f);
        circle.setFill(javafx.scene.paint.Color.rgb(255,182,29));

        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(4.0);
        dropShadow.setOffsetX(0.0);
        dropShadow.setOffsetY(0.0);
        dropShadow.setWidth(9);
        dropShadow.setHeight(9);
        dropShadow.setBlurType(BlurType.THREE_PASS_BOX);
        dropShadow.setColor(javafx.scene.paint.Color.rgb(97, 97, 97));

        Label label = new Label(specName);

        label.setEffect(dropShadow);
        label.setFont(new Font("Roboto", 13));
        label.setGraphic(circle);
        label.setGraphicTextGap(6);
        label.setPadding(new Insets(10,10,10,10));
        label.setStyle("-fx-font-weight: bold; -fx-background-radius: 25; -fx-background-color: #FFFFFF;");
        label.setTextFill(javafx.scene.paint.Color.rgb(30,30,30));

        return label;
    }

    @FXML
    private void btnEditJobOnAction(ActionEvent event) {
        try {
            Stage s = getStage();
            s.close();

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("recruitment/system/ui/listjob/detail/EditDetail.fxml"));
            Parent parent = loader.load();

            EditDetailController controller = (EditDetailController) loader.getController();
            controller.inflateUI(jobID, recruiterID);

            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.setTitle("Edit Job");
            stage.setScene(new Scene(parent));
            stage.show();
            stage.setResizable(false);
            RecruitmentSystemUtil.setStageIcon(stage);

        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    @FXML
    private void btnDeleteJobOnAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deleting job");
        alert.setContentText("Are you sure want to delete the job order " + jobID + " ?");
        Optional<ButtonType> answer = alert.showAndWait();
        if (answer.get() == ButtonType.OK) {
            // Delete job record from database
            Boolean result = DatabaseHandler.getInstance().deleteSelection(jobID,
                    "DELETE FROM job WHERE jobID = ?");
            if (result) {
                AlertMaker.showSimpleAlert("Job deleted", jobID + " was deleted successfully.");
                loadWindow();
            } else {
                AlertMaker.showSimpleAlert("Failed", jobID + " could not be deleted");
            }
        } else {
            AlertMaker.showSimpleAlert("Deletion cancelled", "Deletion process cancelled");
        }
    }

    @FXML
    private void cbSubmissionFilterOnAction(ActionEvent event) {
        if (cbSubmissionFilter.getValue().equalsIgnoreCase("All")){
            loadData();
        } else {
            String status = cbSubmissionFilter.getValue();
            loadData(status);
        }
    }

    @FXML
    private void btnBackOnAction(ActionEvent event) {
        loadWindow();
    }

    private void loadWindow() {
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

    private void loadWindow(Application selectedForView) {
        try {
            getStage().close();

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("recruitment/system/ui/listapplication/detail/ViewApplication.fxml"));
            Parent parent = loader.load();

            ViewApplicationController controller = (ViewApplicationController) loader.getController();
            controller.inflateUI(selectedForView, recruiterID);

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

    private Stage getStage() {
        return (Stage) btnBack.getScene().getWindow();
    }

    public static class Status {
        String status;
        String dateChanged;

        public Status(String status, String dateChanged) {
            this.status = status;
            this.dateChanged = dateChanged;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }

        public void setDateChanged(String dateChanged) {
            this.dateChanged = dateChanged;
        }

        public String getDateChanged() {
            return dateChanged;
        }
    }

    public static class Application {
        String applicationID;
        String applicantName;
        String dateOfApplication;
        String mobNumber;
        String emailAddress;
        String status;

        public Application(String applicationID, String applicantName, String dateOfApplication,
                           String mobNumber, String emailAddress, String status) {
            this.applicationID = applicationID;
            this.applicantName = applicantName;
            this.dateOfApplication = dateOfApplication;
            this.mobNumber = mobNumber;
            this.emailAddress = emailAddress;
            this.status = status;
        }

        public void setApplicationID(String applicationID) {
            this.applicationID = applicationID;
        }

        public String getApplicationID() {
            return applicationID;
        }

        public void setApplicantName(String applicantName) {
            this.applicantName = applicantName;
        }

        public String getApplicantName() {
            return applicantName;
        }

        public void setDateOfApplication(String dateOfApplication) {
            this.dateOfApplication = dateOfApplication;
        }

        public String getDateOfApplication() {
            return dateOfApplication;
        }

        public void setMobNumber(String mobNumber) {
            this.mobNumber = mobNumber;
        }

        public String getMobNumber() {
            return mobNumber;
        }

        public void setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
        }

        public String getEmailAddress() {
            return emailAddress;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }
    }

}
