package recruitment.system.ui.main;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import recruitment.system.database.DataHelper;
import recruitment.system.database.DatabaseHandler;
import recruitment.system.ui.listapplication.ListApplicationController;
import recruitment.system.ui.listclient.ListClientController;
import recruitment.system.ui.listjob.ListJobController;
import recruitment.system.ui.listjob.detail.ViewJobDetailController;
import recruitment.system.ui.recruiter.RecruiterProfileController;
import recruitment.system.util.RecruitmentSystemUtil;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private PreparedStatement stmt;
    private ResultSet rs;
    private ObservableList<Job> jobList = FXCollections.observableArrayList();
    private ObservableList<String> sortList = FXCollections.observableArrayList();
    private PieChart applicationChart;
    private String recruiterID = null;

    DataHelper dataHelper;
    DatabaseHandler databaseHandler;

    @FXML
    public StackPane container;
    @FXML
    public AnchorPane rootPane;
    @FXML
    private Button btnDashboard;
    @FXML
    private Button btnJob;
    @FXML
    private Button btnCandidate;
    @FXML
    private Button btnClient;
    @FXML
    private Button btnRecruiter;
    @FXML
    private Button btnOut;
    @FXML
    private Label lblGreeting;
    @FXML
    private Label lblCountHire;
    @FXML
    private Label lblCountFilled;
    @FXML
    private ComboBox<String> cbSortDate;
    @FXML
    private TableView<Job> tableView;
    @FXML
    private TableColumn<Job, String> colJobID;
    @FXML
    private TableColumn<Job, String> colJobTitle;
    @FXML
    private TableColumn<Job, String> colClient;
    @FXML
    private TableColumn<Job, String> colStartDate;
    @FXML
    private StackPane appInfo;

    public void initialize(URL url, ResourceBundle resourceBundle) {
        databaseHandler = DatabaseHandler.getInstance();

        // Set nearing start date table initial data
        String checkstmt = "SELECT jobID, jobName, CONCAT_WS(\" \", firstName, lastName) as \"Client Name\", " +
                "DATE_FORMAT(startDate, \"%M %d %Y\") as 'startDate' " +
                "FROM job J " +
                "JOIN client_organization C " +
                "ON J.clientID = C.clientID " +
                "WHERE jobStatus = 'In Progress'" +
                "ORDER BY CAST(startDate AS UNSIGNED)";

        initCol();
        loadSortTable(checkstmt);

        btnDashboard.setStyle("-fx-background-color: #E6E8E8; -fx-opacity: 1;");
        cbSortDate.setValue("None");

        // on close
        btnOut.setOnAction(event -> {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Are you sure you want to leave?");
            alert.setContentText("Changes you made might not be saved.");
            Optional<ButtonType> answer = alert.showAndWait();

            if ((answer.get() == ButtonType.OK)) {
                Stage stage = (Stage) btnOut.getScene().getWindow();
                stage.close();
                loadWindow();
            } else {

            }

        });

        // Set count details in dashboard
        String countHired = dataHelper.loadCount("SELECT * from count_hired_now_view");
        String countFilled = dataHelper.loadCount("SELECT * from count_filled_now_view");
        lblCountHire.setText(countHired);
        lblCountFilled.setText(countFilled);

        // set combo box items
        sortList.addAll("A month ahead", "A week ahead", "This week", "None");
        cbSortDate.setItems(sortList);

        initGraph();
    }

    public void getRecruiterID(String recID) {
        Recruiter recruiter = new Recruiter(recID);
        recruiterID = recruiter.getRecruiterID();

        String name = dataHelper.getRecName(recruiterID);
        lblGreeting.setText("Welcome back " + name + "!");
    }

    public void load(String path) throws IOException {
        container.setVisible(true);
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource(path));

        container.getChildren().setAll(root);
        container.toFront();
    }

    @FXML
    private void btnJobOnAction(ActionEvent event) {

        try {
            container.setVisible(true);
            btnJob.setStyle("-fx-background-color: #E6E8E8; -fx-opacity: 1;");
            btnDashboard.setStyle("-fx-opacity: 0.60;");
            btnClient.setStyle("-fx-opacity: 0.60;");
            btnCandidate.setStyle("-fx-opacity: 0.60;");
            btnRecruiter.setStyle("-fx-opacity: 0.60;");

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("recruitment/system/ui/listjob/ListJob.fxml"));
            Parent parent = loader.load();

            ListJobController controller = (ListJobController) loader.getController();
            controller.getRecruiterID(recruiterID);

            container.getChildren().setAll(parent);
            container.toFront();
        } catch (IOException ex){
            System.err.println(ex);
        }

    }

    @FXML
    private void btnCandidateOnAction(ActionEvent event) {

        try {
            container.setVisible(true);
            btnCandidate.setStyle("-fx-background-color: #E6E8E8; -fx-opacity: 1;");
            btnDashboard.setStyle("-fx-opacity: 0.60;");
            btnJob.setStyle("-fx-opacity: 0.60;");
            btnClient.setStyle("-fx-opacity: 0.60;");
            btnRecruiter.setStyle("-fx-opacity: 0.60;");

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("recruitment/system/ui/listapplication/ListApplication.fxml"));
            Parent parent = loader.load();

            ListApplicationController controller = (ListApplicationController) loader.getController();
            controller.getRecruiterID(recruiterID);

            container.getChildren().setAll(parent);
            container.toFront();
        } catch (IOException ex){
            System.err.println(ex);
        }

    }

    @FXML
    private void btnClientOnAction(ActionEvent event) {

        try {
            container.setVisible(true);
            btnClient.setStyle("-fx-background-color: #E6E8E8; -fx-opacity: 1;");
            btnDashboard.setStyle("-fx-opacity: 0.60;");
            btnJob.setStyle("-fx-opacity: 0.60;");
            btnCandidate.setStyle("-fx-opacity: 0.60;");
            btnRecruiter.setStyle("-fx-opacity: 0.60;");

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("recruitment/system/ui/listclient/ListClient.fxml"));
            Parent parent = loader.load();

            ListClientController controller = (ListClientController) loader.getController();
            controller.getRecruiterID(recruiterID);

            container.getChildren().setAll(parent);
            container.toFront();
        } catch (IOException ex){
            System.err.println(ex);
        }

    }

    @FXML
    private void btnRecruiterOnAction(ActionEvent event) {

        try {
            container.setVisible(true);
            btnRecruiter.setStyle("-fx-background-color: #E6E8E8; -fx-opacity: 1;");
            btnClient.setStyle("-fx-opacity: 0.60;");
            btnDashboard.setStyle("-fx-opacity: 0.60;");
            btnJob.setStyle("-fx-opacity: 0.60;");
            btnCandidate.setStyle("-fx-opacity: 0.60;");

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("recruitment/system/ui/recruiter/RecruiterProfile.fxml"));
            Parent parent = loader.load();

            RecruiterProfileController controller = (RecruiterProfileController) loader.getController();
            controller.getRecruiterID(recruiterID);

            container.getChildren().setAll(parent);
            container.toFront();
        } catch (IOException ex){
            System.err.println(ex);
        }

    }

    @FXML
    private void btnDashboardOnAction(ActionEvent event) {

        container.setVisible(false);
        btnDashboard.setStyle("-fx-background-color: #E6E8E8; -fx-opacity: 1;");
        btnJob.setStyle("-fx-opacity: 0.60;");
        btnClient.setStyle("-fx-opacity: 0.60;");
        btnCandidate.setStyle("-fx-opacity: 0.60;");
        btnRecruiter.setStyle("-fx-opacity: 0.60;");

    }

    @FXML
    private void btnCbSortDateOnAction(ActionEvent event) {
        if (cbSortDate.getValue().equalsIgnoreCase("A month ahead")) {
            // Sort nearing start date table if the chosen sort is a month ahead
            String checkstmt = "SELECT jobID, jobName, CONCAT_WS(\" \", firstName, lastName) as \"Client Name\", " +
                    "DATE_FORMAT(startDate, \"%M %d %Y\") as 'startDate' " +
                    "FROM job J " +
                    "JOIN client_organization C " +
                    "ON J.clientID = C.clientID " +
                    "WHERE MONTH(startDate) > MONTH(NOW()) and jobStatus = 'In Progress'" +
                    "ORDER BY CAST(startDate AS UNSIGNED)";
            loadSortTable(checkstmt);
        } else if (cbSortDate.getValue().equalsIgnoreCase("A week ahead")) {
            // Sort nearing start date table if the chosen sort is a week ahead
            String checkstmt = "SELECT jobID, jobName, CONCAT_WS(\" \", firstName, lastName) as \"Client Name\", " +
                    "DATE_FORMAT(startDate, \"%M %d %Y\") as 'startDate' " +
                    "FROM job J " +
                    "JOIN client_organization C " +
                    "ON J.clientID = C.clientID " +
                    "WHERE WEEK(startDate) > WEEK(NOW()) AND jobStatus = 'In Progress' AND MONTH(startDate) = MONTH(NOW())" +
                    "ORDER BY CAST(startDate AS UNSIGNED)";
            loadSortTable(checkstmt);
        } else if (cbSortDate.getValue().equalsIgnoreCase("This week")) {
            // Sort nearing start date table if the chosen sort is the current week
            String checkstmt = "SELECT jobID, jobName, CONCAT_WS(\" \", firstName, lastName) as \"Client Name\", " +
                    "DATE_FORMAT(startDate, \"%M %d %Y\") as 'startDate' " +
                    "FROM job J " +
                    "JOIN client_organization C " +
                    "ON J.clientID = C.clientID " +
                    "WHERE  WEEK(startDate) = WEEK(NOW()) AND DAY(startDate) >= DAY(NOW()) AND jobStatus = 'In Progress'" +
                    "ORDER BY CAST(startDate AS UNSIGNED)";
            loadSortTable(checkstmt);
        } else if (cbSortDate.getValue().equalsIgnoreCase("None")) {
            // Sort nearing start date table if there's no chosen sort
            String checkstmt = "SELECT jobID, jobName, CONCAT_WS(\" \", firstName, lastName) as \"Client Name\", " +
                    "DATE_FORMAT(startDate, \"%M %d %Y\") as 'startDate' " +
                    "FROM job J " +
                    "JOIN client_organization C " +
                    "ON J.clientID = C.clientID " +
                    "WHERE jobStatus = 'In Progress'" +
                    "ORDER BY CAST(startDate AS UNSIGNED)";
            loadSortTable(checkstmt);
        }
    }

    private void initGraph() {
        applicationChart = new PieChart(databaseHandler.getApplicationStatistic());
        appInfo.getChildren().addAll(applicationChart);
    }

    private void refreshGraph() {
        applicationChart.setData(databaseHandler.getApplicationStatistic());
    }

    private void initCol() {
        colJobID.setCellValueFactory(new PropertyValueFactory<>("jobID"));
        colJobTitle.setCellValueFactory(new PropertyValueFactory<>("jobTitle"));
        colClient.setCellValueFactory(new PropertyValueFactory<>("client"));
        colStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
    }

    private void loadSortTable(String checkstmt) {
        jobList.clear();
        try {
            String jobID = null;
            String jobTitle = null;
            String client = null;;
            String startDate = null;

            stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(checkstmt);
            rs = stmt.executeQuery();

            while (rs.next()) {
                jobID = rs.getString(1);
                jobTitle = rs.getString(2);
                client = rs.getString(3);
                startDate = rs.getString(4);

                jobList.add(new Job(jobID, jobTitle, client, startDate));
            }
        } catch (Exception ex) {
            System.out.println("Exception at MainController: loadSortTable");
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
        tableView.setItems(jobList);
    }

    private void loadWindow() {
        try {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/recruitment/system/ui/signin/SignIn.fxml"));
            Scene scene = new Scene(root);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(scene);
            stage.show();
            stage.setTitle("headhunterPH");
            stage.setResizable(false);

            RecruitmentSystemUtil.setStageIcon(stage);
        } catch (Exception ex) {
            ex.printStackTrace();
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

    public static class Job {

        String jobID;
        String jobTitle;
        String client;
        String startDate;

        public Job(String jobID, String jobTitle, String client, String startDate) {
            this.jobID = jobID;
            this.jobTitle = jobTitle;
            this.client = client;
            this.startDate = startDate;
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

        public void setClient(String client) {
            this.client = client;
        }

        public String getClient() {
            return client;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getStartDate() {
            return startDate;
        }
    }

}
