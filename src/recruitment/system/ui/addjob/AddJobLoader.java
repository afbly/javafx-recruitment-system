package recruitment.system.ui.addjob;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import recruitment.system.util.RecruitmentSystemUtil;

public class AddJobLoader extends Application {
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/recruitment/system/ui/addjob/AddJob.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("headhunterPH");
        stage.setResizable(true);

        RecruitmentSystemUtil.setStageIcon(stage);
    }

    public static void main(String[] args) { launch(args); }
}
