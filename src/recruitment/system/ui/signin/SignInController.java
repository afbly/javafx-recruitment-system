package recruitment.system.ui.signin;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.event.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import recruitment.system.database.DataHelper;
import recruitment.system.ui.createaccount.CreateAccountController;
import recruitment.system.ui.listjob.detail.EditDetailController;
import recruitment.system.ui.main.MainController;
import recruitment.system.util.RecruitmentSystemUtil;

public class SignInController implements Initializable {

    DataHelper dataHelper;;

    @FXML
    private StackPane parentContainer;
    @FXML
    private AnchorPane container;
    @FXML
    private Button btnCreate;
    @FXML
    private Button btnSignIn;
    @FXML
    private Label lblStatus;
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Button btnClose;

    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblStatus.setVisible(false);
        btnClose.setOnAction(e -> {
            closeStage();
        });
    }

    public void transition(String path, Button button) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(path));

        Scene scene = button.getScene();

        root.translateYProperty().set(scene.getHeight());
        parentContainer.getChildren().add(root);

        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(root.translateYProperty(), 0, Interpolator.EASE_IN);
        KeyFrame kf = new KeyFrame(Duration.seconds(1), kv);
        timeline.getKeyFrames().add(kf);
        timeline.setOnFinished(event1 -> parentContainer.getChildren().remove(container));
        timeline.play();
    }

    @FXML
    public void btnCreateOnAction(ActionEvent event) throws IOException {
        transition("/recruitment/system/ui/createaccount/CreateAccount.fxml", btnCreate);
    }

    @FXML
    public void btnSignInOnAction(ActionEvent event) throws IOException {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setText("Enter sign in details.");
            lblStatus.setVisible(true);
        } else {
            if (dataHelper.signIn(username, password)) {
                Recruiter recruiter = new Recruiter(dataHelper.getRecruiterID(username));
                loadWindow("recruitment/system/ui/main/Main.fxml", recruiter.getRecruiterID());
                closeStage();
            } else {
                lblStatus.setText("Invalid sign in credentials.");
                lblStatus.setVisible(true);
            }
        }
    }

    @FXML
    private void txtFieldOnAction(MouseEvent event) {
        lblStatus.setVisible(false);
    }

    private Stage getStage() {
        return (Stage) btnSignIn.getScene().getWindow();
    }

    private void closeStage() {
        getStage().close();
    }

    private void loadWindow(String path, String recruiterID) {
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
