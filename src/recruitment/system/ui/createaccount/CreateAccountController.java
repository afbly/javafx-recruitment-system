package recruitment.system.ui.createaccount;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import recruitment.system.database.DataHelper;
import recruitment.system.util.RecruitmentSystemUtil;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CreateAccountController implements Initializable {

    DataHelper dataHelper;

    @FXML
    private AnchorPane container;
    @FXML
    private Label lblStatus;
    @FXML
    private TextField txtrecruiterID;
    @FXML
    private TextField txtFirstName;
    @FXML
    private TextField txtLastName;
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Button btnCreate;
    @FXML
    private Button btnBack;
    @FXML
    private Button btnClose;

    public void initialize(URL url, ResourceBundle resourceBundle) {
        String checkstmt = "SELECT recruiterID FROM recruiter ORDER BY recruiterID DESC LIMIT 1";
        int id = dataHelper.GenID(checkstmt);
        txtrecruiterID.setText(String.valueOf(id));
        btnClose.setOnAction(e -> {
            Stage stage = (Stage) btnClose.getScene().getWindow();
            stage.close();
        });
    }

    private void transition (String path, Button button) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(path));

        Scene scene = button.getScene();

        root.translateYProperty().set(scene.getWidth());

        StackPane parentContainer = (StackPane) scene.getRoot();
        parentContainer.getChildren().add(root);

        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(root.translateYProperty(), 0, Interpolator.EASE_IN);
        KeyFrame kf = new KeyFrame(Duration.seconds(1), kv);
        timeline.getKeyFrames().add(kf);
        timeline.setOnFinished(event1 -> parentContainer.getChildren().remove(container));
        timeline.play();
    }

    @FXML
    private void btnCreateOnAction(ActionEvent event) throws IOException {
        int recruiterID = Integer.parseInt(txtrecruiterID.getText());
        String firstName = txtFirstName.getText();
        String lastName = txtLastName.getText();
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            lblStatus.setText("Enter account details");
            lblStatus.setVisible(true);
        } else {
            if (dataHelper.isUsernameExist(username)) {
                lblStatus.setText("Username already exist.");
                lblStatus.setVisible(true);
            } else {
                if (dataHelper.insertRecruiter(recruiterID, firstName, lastName, username, password)) {
                    transition("/recruitment/system/ui/signin/SignIn.fxml", btnCreate);
                } else {
                    lblStatus.setText("Something's wrong, please try again.");
                    lblStatus.setVisible(true);
                }
            }
        }
    }

    @FXML
    private void btnBackOnAction(ActionEvent event) throws IOException {
        transition("/recruitment/system/ui/signin/SignIn.fxml", btnBack);
    }

    @FXML
    private void txtFieldOnAction(MouseEvent event) {
        lblStatus.setVisible(false);
    }

    private void loadWindow(String path) {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource(path));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("headhunterPH");
            stage.setScene(new Scene(parent));
            stage.show();
            RecruitmentSystemUtil.setStageIcon(stage);
        }
        catch (IOException ex) {
            System.err.println(ex);
        }
    }

}
