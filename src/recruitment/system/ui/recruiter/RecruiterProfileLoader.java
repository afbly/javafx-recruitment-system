package recruitment.system.ui.recruiter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import recruitment.system.database.DataHelper;
import recruitment.system.util.RecruitmentSystemUtil;

public class RecruiterProfileLoader extends Application {

    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/recruitment/system/ui/recruiter/RecruiterProfile.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("headhunterPH");
        stage.setResizable(true);

        RecruitmentSystemUtil.setStageIcon(stage);
    }

    public static void main(String[] args) { launch(args); }

}