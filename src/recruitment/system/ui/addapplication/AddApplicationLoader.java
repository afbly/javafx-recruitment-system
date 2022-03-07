package recruitment.system.ui.addapplication;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import recruitment.system.util.RecruitmentSystemUtil;

public class AddApplicationLoader extends Application {

    public void start(Stage stage) {

        try {
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("recruitment/system/ui/addapplication/AddApplication.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            stage.setTitle("headhunterPH");
            stage.setResizable(true);

            RecruitmentSystemUtil.setStageIcon(stage);
        } catch (Exception ex){
            System.err.println(ex);
            ex.printStackTrace();
        }


    }

    public static void main(String[] args) { launch(args); }

}
