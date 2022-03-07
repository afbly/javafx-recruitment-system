package recruitment.system.ui.main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.StageStyle;
import recruitment.system.database.DataHelper;
import recruitment.system.util.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/recruitment/system/ui/signin/SignIn.fxml"));
        Scene scene = new Scene(root);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("headhunterPH");
        stage.setResizable(false);

        RecruitmentSystemUtil.setStageIcon(stage);
    }

    public static void main(String[] args) { launch(args); }
}

