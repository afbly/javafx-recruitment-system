package recruitment.system.ui.recruiter;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import recruitment.system.alert.AlertMaker;
import recruitment.system.data.model.Job;
import recruitment.system.data.model.Recruiter;
import recruitment.system.database.DataHelper;
import recruitment.system.database.DatabaseHandler;
import recruitment.system.ui.listclient.ListClientController;
import recruitment.system.ui.main.MainController;
import recruitment.system.util.RecruitmentSystemUtil;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class RecruiterProfileController implements Initializable {

    private CallableStatement cstmt;
    private ResultSet rs;
    private String recruiterID = null;

    DataHelper dataHelper;

    @FXML
    private TextField txtRecruiterID;
    @FXML
    private TextField txtUsername;
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
    private Button btnSave;
    @FXML
    private StackPane container;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnSave.setVisible(false);
        System.out.println(recruiterID);
    }

    public void getRecruiterID(String recID) {
        Recruiter recruiter = new Recruiter(recID);
        recruiterID = recruiter.getRecruiterID();
        txtRecruiterID.setText(recruiterID);
        loadData(recruiterID);
        System.out.println(recID);
    }

    private void loadData(String recruiterID) {

        try {
            String checkstmt = "{CALL disp_recruiter_details (?)}";
            cstmt = DatabaseHandler.getInstance().getConnection().prepareCall(checkstmt);
            cstmt.setString(1, recruiterID);
            rs = cstmt.executeQuery();

            if (rs.next()) {
                txtFirstName.setText(rs.getString(1));
                txtMidName.setText(rs.getString(2));
                txtLastName.setText(rs.getString(3));
                dBirthDate.getEditor().setText(rs.getString(4));
                txtMobNumber.setText(rs.getString(5));
                txtTelNumber.setText(rs.getString(6));
                txtEmailAdd.setText(rs.getString(7));
                txtAddressLine.setText(rs.getString(8));
                txtCity.setText(rs.getString(9));
                txtProvince.setText(rs.getString(10));
                txtZipcode.setText(rs.getString(11));
                txtUsername.setText(rs.getString(12));
            }

        } catch (Exception ex) {
            System.out.println("Exception at RecruiterProfileController:loadData\n" + ex.getLocalizedMessage());
            ex.printStackTrace();
        } finally {
            if (cstmt != null && rs != null) {
                try {
                    rs.close();
                    cstmt.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void btnSaveOnAction(ActionEvent event) {
        String recID = txtRecruiterID.getText();
        String firstName = txtFirstName.getText();
        String midName = txtMidName.getText();
        String lastName = txtLastName.getText();
        String birthDate = String.valueOf(dBirthDate.getValue());
        String mobNumber = txtMobNumber.getText();
        String telNumber = txtTelNumber.getText();
        String emailAddress = txtEmailAdd.getText();
        String addressLine = txtAddressLine.getText();
        String city = txtCity.getText();
        String province = txtProvince.getText();
        String zipcode = txtZipcode.getText();
        String username = txtUsername.getText();

        System.out.println(dBirthDate.getValue());

        if (username.isEmpty() || lastName.isEmpty()) {
            AlertMaker.showErrorMessage("Failed to update profile", "Check all the entries and try again");
        }

        recruitment.system.data.model.Recruiter recruiter = new recruitment.system.data.model.Recruiter(recID, firstName,
                midName, lastName, birthDate, mobNumber, telNumber, emailAddress, addressLine, city, province, zipcode, username
        );
        boolean result = dataHelper.updateRecruiterDet(recruiter);

        if (result) {
            AlertMaker.successMessage(container, "Profile Updated", "All changes were saved.");
            btnSave.setVisible(false);
            loadData(recruiterID);
        } else {
            AlertMaker.showErrorMessage("Failed to update profile", "Check all the entries and try again");
        }
    }

    @FXML
    void showBtnSave(MouseEvent event) {
        btnSave.setVisible(true);
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
