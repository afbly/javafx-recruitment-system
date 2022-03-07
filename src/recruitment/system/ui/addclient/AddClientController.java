package recruitment.system.ui.addclient;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import recruitment.system.alert.AlertMaker;
import recruitment.system.data.model.Application;
import recruitment.system.data.model.Client;
import recruitment.system.database.DataHelper;
import recruitment.system.ui.addapplication.AddApplicationController;
import recruitment.system.ui.main.MainController;
import recruitment.system.util.RecruitmentSystemUtil;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AddClientController implements Initializable {

    private String recruiterID = null;

    DataHelper dataHelper;

    @FXML
    private StackPane rootPane;
    @FXML
    private AnchorPane mainContainer;
    @FXML
    private Button btnBack;
    @FXML
    private TextField txtClientID;
    @FXML
    private TextField txtCompanyName;
    @FXML
    private TextField txtFirstName;
    @FXML
    private TextField txtMidName;
    @FXML
    private TextField txtLastName;
    @FXML
    private TextField txtMobNumber;
    @FXML
    private TextField txtTelNumber;
    @FXML
    private TextField txtEmailAddress;
    @FXML
    private ComboBox<String> chIndustry;
    @FXML
    private TextField txtCity;
    @FXML
    private TextField txtProvince;
    @FXML
    private TextField txtZipcode;
    @FXML
    private TextArea txtAddressLine;

    public void initialize(URL location, ResourceBundle resources) {
        txtClientID.setText(String.valueOf(dataHelper.GenID("SELECT clientID FROM client_organization " +
                "ORDER BY clientID DESC LIMIT 1")));
        inflateIndustry();
        System.out.println("AddClientController: " + recruiterID);
    }

    public void getRecruiterID(String recID) {
        Recruiter recruiter = new Recruiter(recID);
        recruiterID = recruiter.getRecruiterID();
    }

    private void inflateIndustry() {
        chIndustry.setVisibleRowCount(5);
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
    private void btnSaveOnAction(ActionEvent event) {
        String clientID = txtClientID.getText();
        String firstName = txtFirstName.getText();
        String midName = txtMidName.getText();
        String lastName = txtLastName.getText();
        String companyName = txtCompanyName.getText();
        String mobNumber = txtMobNumber.getText();
        String telNumber = txtTelNumber.getText();
        String emailAddress = txtEmailAddress.getText();
        String addressLine = txtAddressLine.getText();
        String city = txtCity.getText();
        String province = txtProvince.getText();
        String zipcode = txtZipcode.getText();
        String industry = chIndustry.getValue();

        if (lastName.isEmpty() || emailAddress.isEmpty()
                || !zipcode.equalsIgnoreCase(String.valueOf(Integer.parseInt(zipcode)))
                || zipcode.length() < 4 || zipcode.length() > 4) {
            AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Failed to add new client",
                    "Check all the entries and try again");
        } else {
            Client client = new Client(clientID, firstName, midName, lastName, companyName, mobNumber, telNumber,
                    emailAddress, addressLine, city, province, zipcode, industry);
            boolean result = dataHelper.insertNewClient(client);

            if (result) {
                AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "New client added",
                        "Client ID number: " + clientID + " has been added");

                clearEntries();
            } else {
                AlertMaker.showMaterialDialog(rootPane, mainContainer, new ArrayList<>(), "Failed to add new client",
                        "Check all the entries and try again");
            }
        }
    }

    private void clearEntries() {
        // Generates new ID for the next clien_organization to be inserted
        txtClientID.setText(String.valueOf(dataHelper.GenID("SELECT clientID FROM client_organization " +
                "ORDER BY clientID DESC LIMIT 1")));
        txtCompanyName.clear();
        txtFirstName.clear();
        txtMidName.clear();
        txtLastName.clear();
        txtMobNumber.clear();
        txtTelNumber.clear();
        txtEmailAddress.clear();
        chIndustry.getSelectionModel().clearSelection();
        txtCity.clear();
        txtProvince.clear();
        txtAddressLine.clear();
        txtZipcode.clear();
    }

    private Stage getStage() {
        return (Stage) btnBack.getScene().getWindow();
    }

    @FXML
    private void closeStage(ActionEvent event) {
        loadWindow("recruitment/system/ui/main/Main.fxml");
        getStage().close();
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
