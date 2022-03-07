package recruitment.system.ui.listapplication.detail;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import recruitment.system.alert.AlertMaker;
import recruitment.system.data.model.Application;
import recruitment.system.database.DataHelper;
import recruitment.system.database.DatabaseHandler;
import recruitment.system.ui.listapplication.ListApplicationController;
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

public class ViewApplicationController implements Initializable {

    private CallableStatement cstmt;
    private ResultSet rs;
    private String appID = null;
    private String recruiterID = null;
    private String docPath = null;
    private String jobID = null;
    private boolean isInEditMode;

    DataHelper dataHelper;
    DatabaseHandler databaseHandler;

    @FXML
    private StackPane rootPane;
    @FXML
    private VBox vBoxNotes;
    @FXML
    private Pane pnEvaluation;
    @FXML
    private ComboBox<String> chStatus;
    @FXML
    private TextArea txtInputNote;
    @FXML
    private AnchorPane mainContainer;
    @FXML
    private TextField txtAppID;
    @FXML
    private TextField txtDateOfApp;
    @FXML
    private TextField txtFirstName;
    @FXML
    private TextField txtMidName;
    @FXML
    private TextField txtLastName;
    @FXML
    private DatePicker dBirthDate;
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
    private TextField txtEdAttainment;
    @FXML
    private TextField txtYearOfExp;
    @FXML
    private TextField txtJobTitle;
    @FXML
    private TextField txtCompanyName;
    @FXML
    private FlowPane pnSpec;
    @FXML
    private TextField txtJobID;
    @FXML
    private TextField txtAssocJobTitle;
    @FXML
    private Label hApplicantName;
    @FXML
    private Label hStatus;
    @FXML
    private Label hJobTitle;
    @FXML
    private Label lblLastUpdate;
    @FXML
    private Button btnSaveChanges;
    @FXML
    private Button btnEditApplication;
    @FXML
    private Button btnViewDocument;
    @FXML
    private Button btnAddEval;
    @FXML
    private Button btnBack;
    @FXML
    private Button btnCancelChanges;
    @FXML
    private Button btnDeleteApp;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        databaseHandler = DatabaseHandler.getInstance();
        inViewMode();
        inflateStatus();
    }

    public void inflateUI(ListApplicationController.Application application, String recruiterID) {
        appID = application.getAppID();
        this.recruiterID = recruiterID;
        txtAppID.setText(appID);
        loadData(appID);
    }

    public void inflateUI(ViewJobDetailController.Application application, String recruiterID) {
        appID = application.getApplicationID();
        this.recruiterID = recruiterID;
        txtAppID.setText(appID);
        loadData(appID);
    }

    private void inEditMode() {
        isInEditMode = true;

        txtFirstName.setEditable(true);
        txtMidName.setEditable(true);
        txtLastName.setEditable(true);
        dBirthDate.setEditable(true);
        dBirthDate.setDisable(false);
        txtMobNum.setEditable(true);
        txtTelNum.setEditable(true);
        txtEmailAdd.setEditable(true);
        txtCity.setEditable(true);
        txtProvince.setEditable(true);
        txtZipcode.setEditable(true);
        txtAddressLine.setEditable(true);

        btnSaveChanges.setVisible(true);
        btnCancelChanges.setVisible(true);
        btnEditApplication.setDisable(true);
        btnViewDocument.setDisable(true);
        btnDeleteApp.setDisable(true);
        btnAddEval.setDisable(true);
    }

    private void inViewMode() {
        isInEditMode = false;
        txtFirstName.setEditable(false);
        txtMidName.setEditable(false);
        txtLastName.setEditable(false);
        dBirthDate.setDisable(true);
        dBirthDate.setEditable(false);
        txtMobNum.setEditable(false);
        txtTelNum.setEditable(false);
        txtEmailAdd.setEditable(false);
        txtCity.setEditable(false);
        txtProvince.setEditable(false);
        txtZipcode.setEditable(false);
        txtAddressLine.setEditable(false);

        btnEditApplication.setDisable(false);
        String status = hStatus.getText();
        if (status.equalsIgnoreCase("Hired") || status.equalsIgnoreCase("Rejected")
                || status.equalsIgnoreCase("Offer-Declined")
                || status.equalsIgnoreCase("Rejected-for-Interview")) {
            btnAddEval.setDisable(true);
        } else {
            btnAddEval.setDisable(false);
        }
        btnViewDocument.setDisable(false);
        btnDeleteApp.setDisable(false);
        btnSaveChanges.setVisible(false);
        btnCancelChanges.setVisible(false);
    }

    private void inflateStatus() {
        chStatus.getItems().addAll("Interview-to-be-Scheduled", "Interview-Scheduled",
                "Rejected-for-Interview", "To-be-Offered", "Offer-Accepted", "Offer-Declined",
                "Offer-Made", "Hired", "Rejected"
        );
    }

    @FXML
    private void btnDeleteAppOnAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deleting application");
        alert.setContentText("Are you sure want to delete application number: " + appID + " ?");
        Optional<ButtonType> answer = alert.showAndWait();

        if (answer.get() == ButtonType.OK) {
            // Delete application record from database
            Boolean result = databaseHandler.deleteSelection(appID,
                    "DELETE FROM application WHERE applicationID = ?");
            if (result) {
                AlertMaker.showSimpleAlert("Application Deleted", "Application number: " +
                        appID + " was deleted successfully.");
            } else {
                AlertMaker.showSimpleAlert("Failed", "Application number: " +
                        appID + " could not be deleted");
            }
            loadWindow("recruitment/system/ui/main/Main.fxml");
        } else {
            AlertMaker.showSimpleAlert("Deletion cancelled", "Deletion process cancelled");
        }
    }

    @FXML
    private void btnViewDocumentOnAction(ActionEvent event) {
        // TODO: btnViewDocumentOnAction
        try {
            Runtime.getRuntime().exec("rundll32 url.dll, FileProtocolHandler " + docPath);
        } catch (Exception e) {
            AlertMaker.showErrorMessage("Broken file", "Cannot open file.");
        }
    }

    /* Add new evaluation */

    @FXML
    private void btnAddEvalOnAction(ActionEvent event) {
        pnEvaluation.toFront();
        BoxBlur blur = new BoxBlur(3, 3, 3);
        mainContainer.setDisable(true);
        mainContainer.setEffect(blur);
    }

    @FXML
    private void btnUpdateStatusOnAction(ActionEvent event) {
        if (chStatus.getValue().isEmpty()) {
            AlertMaker.showMaterialDialog(rootPane, pnEvaluation, new ArrayList<>(), "Invalid status",
                    "Status cannot be empty.");
        } else if (chStatus.getValue().equalsIgnoreCase(hStatus.getText())) {
            AlertMaker.showMaterialDialog(rootPane, pnEvaluation, new ArrayList<>(), "No changes detected",
                    "Status is the same as the previous one.");
        } else if (chStatus.getValue().equalsIgnoreCase("Hired")) {
            if (isNewHireAccepted()) {
                updateApplicantStatus();
            }
        } else {
            updateApplicantStatus();
        }
    }

    private void updateApplicantStatus() {
        try {
            String evalNote = txtInputNote.getText();
            // Update the application status
            String checkstmt = "{CALL update_app_status (?, ?)}";
            cstmt = DatabaseHandler.getInstance().getConnection().prepareCall(checkstmt);
            cstmt.setString(1, appID);
            cstmt.setString(2, chStatus.getValue());
            cstmt.execute();

            pnEvaluation.toBack();
            mainContainer.setEffect(null);
            mainContainer.setDisable(false);

            if (evalNote.isEmpty()) {

            } else {
                Application appEval = new Application(appID, evalNote, recruiterID);
                dataHelper.insertEvalNote(appEval);
            }
            AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Evaluation updated",
                    "Application number: " + appID + " has been updated to " + chStatus.getValue());
            loadData(appID);
            chStatus.getSelectionModel().clearSelection();
            txtInputNote.clear();
        } catch (Exception exception) {
            System.out.println("Exception at ViewApplicationController:btnUpdateStatusOnAction\n" + exception.getLocalizedMessage());
            exception.printStackTrace();
        } finally {
            if (rs != null && cstmt != null) {
                try {
                    rs.close();
                    cstmt.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private boolean isNewHireAccepted() {
        int numOfVacancy = dataHelper.getNumberOfVacancy(jobID);

        if (numOfVacancy == 0 || numOfVacancy == -1) {
            AlertMaker.showSimpleAlert("", "The job associated to this application cannot accept new hires anymore.");
            return false;
        } else {
            dataHelper.updateJobVacanciesAndStatus(jobID);
            return true;
        }
    }

    @FXML
    private void btnCancelUpdateOnAction(ActionEvent event) {
        pnEvaluation.toBack();
        mainContainer.setEffect(null);
        mainContainer.setDisable(false);
    }

    /* See all application eval notes */

    private void setEvaluationNotes(String applicationID) {
        try {

            ArrayList<VBox> noteList = new ArrayList<>();
            vBoxNotes.getChildren().clear();

            String date = null;
            String recruiter = null;
            String note = null;
            // Display evaluation notes for the application
            String checkstmt = "{CALL disp_app_eval_notes (?)}";
            cstmt = DatabaseHandler.getInstance().getConnection().prepareCall(checkstmt);
            cstmt.setString(1, applicationID);
            rs = cstmt.executeQuery();

            while (rs.next()) {
                date = rs.getString(1);
                recruiter = rs.getString(2);
                note = rs.getString(3);

                noteList.add(getvBoxNotes(date, recruiter, note));
            }

            vBoxNotes.getChildren().addAll(noteList);
        } catch (Exception ex) {
            System.out.println("Exception at ViewApplicationController: btnSeeAllOnAction\n" + ex.getLocalizedMessage());
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

    private VBox getvBoxNotes(String date, String recruiter, String note) {
        VBox vBox = new VBox();
        HBox hBox = new HBox();

        vBox.setSpacing(5);

        Label lblDate = new Label(date);
        Label lblCon = new Label (" made an evaluation on ");
        Label lblRecruiter = new Label(recruiter);
        Label lblNote = new Label(note);

        lblDate.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: #000000");
        lblRecruiter.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: #000000");
        lblCon.setStyle("-fx-font-size: 14; -fx-text-fill: #545454");

        lblNote.setStyle("-fx-font-size: 14; -fx-text-fill: #000000");
        lblNote.setText(note);
        lblNote.setTextAlignment(TextAlignment.JUSTIFY);
        lblNote.setWrapText(true);

        hBox.getChildren().addAll(lblRecruiter, lblCon, lblDate);

        vBox.getChildren().addAll(hBox, lblNote);

        return vBox;
    }

    /* Edit application */

    @FXML
    private void btnEditApplicationOnAction(ActionEvent event) {
        inEditMode();
    }

    @FXML
    private void btnCancelChangesOnAction(ActionEvent event) {
        loadData(appID);
        inViewMode();
    }

    @FXML
    private void btnSaveChangesOnAction(ActionEvent event) {
        String applicationID = txtAppID.getText();
        String firstName = txtFirstName.getText();
        String midName = txtMidName.getText();
        String lastName = txtLastName.getText();
        String birthDate =  String.valueOf(dBirthDate.getEditor().getText());
        String mobNumber = txtMobNum.getText();
        String telNumber = txtTelNum.getText();
        String emailAddress = txtEmailAdd.getText();
        String addressLine = txtAddressLine.getText();
        String city = txtCity.getText();
        String province = txtProvince.getText();
        String zipcode = txtZipcode.getText();


        if (lastName.isEmpty() || emailAddress.isEmpty()
                || mobNumber.length() < 11 || mobNumber.length() > 11
                || !zipcode.equalsIgnoreCase(String.valueOf(Integer.parseInt(zipcode)))
                || zipcode.length() < 4 || zipcode.length() > 4) {
            AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Failed to update application",
                    "Check all the entries and try again");
        } else {
            Application application = new Application(applicationID, firstName, midName, lastName, birthDate,
                    mobNumber, telNumber, emailAddress, addressLine, city, province, zipcode);
            boolean result = dataHelper.updateApplicationDet(application);

            if (result) {
                AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Application updated",
                        "Application number: " + applicationID + " has been updated");

                loadData(applicationID);
                inViewMode();
            } else {
                AlertMaker.showSimpleAlert("Failed", "Please check all input details.");
                inEditMode();
            }
        }
    }

    /* Set application status */

    private void setApplicationStatus(String status) {
        if (status == null) {
            System.out.println("Something's wrong");
        } else if (status.equalsIgnoreCase("Hired")) {
            hStatus.setStyle("-fx-background-radius: 15; -fx-background-color: #64FCD9; -fx-text-fill: #000000;");
            hStatus.setText(status);
            btnAddEval.setDisable(true);
        } else if (status.equalsIgnoreCase("Rejected") || status.equalsIgnoreCase("Offer-Declined")
                || status.equalsIgnoreCase("Rejected-for-Interview")
                || status.equalsIgnoreCase("Disqualified")) {
            hStatus.setStyle("-fx-background-radius: 15; -fx-background-color: #CC0000; -fx-text-fill: #FFFFFF;");
            hStatus.setText(status);
            btnAddEval.setDisable(true);
            btnEditApplication.setDisable(true);
        } else {
            hStatus.setStyle("-fx-background-radius: 15; -fx-background-color: #07152B; -fx-text-fill: #FFFFFF;");
            hStatus.setText(status);
        }
    }

    /* Load application data */

    private void loadData(String applicationID) {
        try {

            String firstname = null;
            String midName = null;
            String lastName = null;
            String status = null;
            // Display application details
            String checkstmt = "{CALL disp_app (?)}";
            cstmt = DatabaseHandler.getInstance().getConnection().prepareCall(checkstmt);
            cstmt.setString(1, applicationID);
            rs = cstmt.executeQuery();

            if (rs.next()) {
                txtDateOfApp.setText(rs.getString(1));
                txtFirstName.setText(rs.getString(2));
                txtMidName.setText(rs.getString(3));
                txtLastName.setText(rs.getString(4));
                firstname = rs.getString(2);
                midName = rs.getString(3);
                lastName = rs.getString(4);
                dBirthDate.getEditor().setText(rs.getString(5));
                txtMobNum.setText(rs.getString(6));
                txtTelNum.setText(rs.getString(7));
                txtEmailAdd.setText(rs.getString(8));
                txtAddressLine.setText(rs.getString(9));
                txtCity.setText(rs.getString(10));
                txtProvince.setText(rs.getString(11));
                txtZipcode.setText(rs.getString(12));
                txtEdAttainment.setText(rs.getString(13));
                txtYearOfExp.setText(rs.getString(14));
                txtJobTitle.setText(rs.getString(15));
                txtCompanyName.setText(rs.getString(16));
                txtJobID.setText(rs.getString(17));
                jobID = rs.getString(17);
                txtAssocJobTitle.setText(rs.getString(18));
                hJobTitle.setText(rs.getString(18));
                docPath = rs.getString(19);
                status = rs.getString(20);
                lblLastUpdate.setText(rs.getString(21));

            }


            if (midName == null) {
                hApplicantName.setText(firstname + " " + lastName);
            } else {
                hApplicantName.setText(firstname + " " +
                        midName + " " + lastName);
            }

            setApplicationStatus(status);
            setSpecPane(applicationID);
            setEvaluationNotes(applicationID);
        } catch (Exception ex) {
            System.out.println("Exception at ViewApplicationController:loadData\n" + ex.getLocalizedMessage());
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

    /* Load applicaiton specialization */

    private void setSpecPane(String applicationID) {
        ArrayList<Label> list = new ArrayList<>();

        try {
            list.clear();
            pnSpec.getChildren().clear();

            String specName = null;
            // Displays the specializations the applicant has
            String checkstmt = "{CALL disp_app_spec (?)}";
            cstmt = DatabaseHandler.getInstance().getConnection().prepareCall(checkstmt);
            cstmt.setString(1, applicationID);
            rs = cstmt.executeQuery();

            while (rs.next()) {
                specName = rs.getString(1);
                list.add(getSpec(specName));
            }

            pnSpec.getChildren().addAll(list);
        } catch (Exception ex) {
            System.out.println("Exception at ViewApplicationController: setSpecPane\n" + ex.getLocalizedMessage());
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

    /* Close window */

    @FXML
    private void closeStage(ActionEvent event) {

        if (isInEditMode) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Are you sure you want to leave?");
            alert.setContentText("Changes you made might not be saved.");
            Optional<ButtonType> answer = alert.showAndWait();

            if ((answer.get() == ButtonType.OK)) {
                loadWindow("recruitment/system/ui/main/Main.fxml");
                getStage().close();
            } else {
                loadData(appID);
            }
        } else {
            loadWindow("recruitment/system/ui/main/Main.fxml");
            getStage().close();
        }
    }

    private Stage getStage() {
        return (Stage) btnBack.getScene().getWindow();
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

}
