package recruitment.system.trial;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;
import recruitment.system.database.DataHelper;
import recruitment.system.database.DatabaseHandler;

import javax.naming.spi.InitialContextFactory;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TableViewCon implements Initializable {

    PreparedStatement stmt = null;
    ResultSet rs = null;
    private ObservableList<ObservableList> data;

    @FXML
    private AnchorPane container;
    @FXML
    private HBox hbox;
    @FXML
    private ListView<String> listView;
    @FXML
    private TableView table;
    @FXML
    private ListView<String> listView1;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnReset;
    @FXML
    private Button btnCreate;
    @FXML
    private VBox vBoxNote;
    @FXML
    private ComboBox<CheckBox> chCombo;
    @FXML
    private CheckBox checkBox1;
    @FXML
    private CheckBox checkBox2;
    @FXML
    private CheckBox checkBox3;
    ObservableList<CheckBox> list = FXCollections.observableArrayList();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String checkstmt = "SELECT * FROM job";
        //fetColumnList(checkstmt);
        //fetRowList(checkstmt);
        //setListView();


        checkBox1.setText("Yes");
        checkBox2.setText("No");
        checkBox3.setText("Maybe");
        list.addAll(checkBox1, checkBox2, checkBox3);
        chCombo.getItems().add((CheckBox) list);

        container.getChildren().add(getSpec("Enter long long long long specialization"));
        hbox.getChildren().add(getStep(12, "Preparing"));
        hbox.getChildren().add(getStep(14, "Sourcing"));
        hbox.getChildren().add(getStep(5, "Offered"));
        hbox.getChildren().add(getStep(2, "Hired"));
        vBoxNote.getChildren().addAll(getvBoxNotes("January 20, 2020", "Afrahly",
                " Donec euismod aliquet odio vitae vulputate. Orci varius natoque penatibus et " +
                        "magnis dis parturient montes, nascetur ridiculus mus. Duis orci eros, scelerisque " +
                        "interdum bibendum eu, ultricies a metus. Integer non tortor lacinia, sodales libero ut, " +
                        "ultricies enim. Duis rhoncus sapien in lectus ultricies tincidunt. Nullam pulvinar dapibus " +
                        "nibh, sed commodo tellus sagittis sed. Vestibulum viverra dignissim turpis a lacinia. " +
                        "Sed bibendum lorem eget mi vestibulum fermentum. Maecenas facilisis vulputate nisl et sagittis."));
        vBoxNote.getChildren().addAll(getvBoxNotes("January 20, 2020", "Afrahly",
                " Donec euismod aliquet odio vitae vulputate. Orci varius natoque penatibus et " +
                        "magnis dis parturient montes, nascetur ridiculus mus. Duis orci eros, scelerisque " +
                        "interdum bibendum eu, ultricies a metus. Integer non tortor lacinia, sodales libero ut, " +
                        "ultricies enim. Duis rhoncus sapien in lectus ultricies tincidunt. Nullam pulvinar dapibus " +
                        "nibh, sed commodo tellus sagittis sed. Vestibulum viverra dignissim turpis a lacinia. " +
                        "Sed bibendum lorem eget mi vestibulum fermentum. Maecenas facilisis vulputate nisl et sagittis."));
        vBoxNote.getChildren().addAll(getvBoxNotes("January 20, 2020", "Afrahly",
                " Donec euismod aliquet odio vitae vulputate. Orci varius natoque penatibus et " +
                        "magnis dis parturient montes, nascetur ridiculus mus. Duis orci eros, scelerisque " +
                        "interdum bibendum eu, ultricies a metus. Integer non tortor lacinia, sodales libero ut, " +
                        "ultricies enim. Duis rhoncus sapien in lectus ultricies tincidunt. Nullam pulvinar dapibus " +
                        "nibh, sed commodo tellus sagittis sed. Vestibulum viverra dignissim turpis a lacinia. " +
                        "Sed bibendum lorem eget mi vestibulum fermentum. Maecenas facilisis vulputate nisl et sagittis."));
        vBoxNote.getChildren().addAll(getvBoxNotes("January 20, 2020", "Afrahly",
                " Donec euismod aliquet odio vitae vulputate. Orci varius natoque penatibus et " +
                        "magnis dis parturient montes, nascetur ridiculus mus. Duis orci eros, scelerisque " +
                        "interdum bibendum eu, ultricies a metus. Integer non tortor lacinia, sodales libero ut, " +
                        "ultricies enim. Duis rhoncus sapien in lectus ultricies tincidunt. Nullam pulvinar dapibus " +
                        "nibh, sed commodo tellus sagittis sed. Vestibulum viverra dignissim turpis a lacinia. " +
                        "Sed bibendum lorem eget mi vestibulum fermentum. Maecenas facilisis vulputate nisl et sagittis."));
        vBoxNote.getChildren().addAll(getvBoxNotes("January 20, 2020", "Afrahly",
                " Donec euismod aliquet odio vitae vulputate. Orci varius natoque penatibus et " +
                        "magnis dis parturient montes, nascetur ridiculus mus. Duis orci eros, scelerisque " +
                        "interdum bibendum eu, ultricies a metus. Integer non tortor lacinia, sodales libero ut, " +
                        "ultricies enim. Duis rhoncus sapien in lectus ultricies tincidunt. Nullam pulvinar dapibus " +
                        "nibh, sed commodo tellus sagittis sed. Vestibulum viverra dignissim turpis a lacinia. " +
                        "Sed bibendum lorem eget mi vestibulum fermentum. Maecenas facilisis vulputate nisl et sagittis."));
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


    private void fetColumnList(String checkstmt) {
        try {
            stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(checkstmt);
            rs = stmt.executeQuery();

            //SQL FOR SELECTING ALL OF CUSTOMER
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1).toUpperCase());
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });

                table.getColumns().removeAll(col);
                table.getColumns().addAll(col);

                System.out.println("Column [" + i + "] ");
            }
            TableColumn view = new TableColumn("View");
            table.getColumns().add(view);
        } catch (Exception ex) {
            System.out.println("Exception at TableViewCon:fetColumnList");
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
    }

    public void fetRowList(String checkstmt) {
        data = FXCollections.observableArrayList();
        Button button = new Button("View");

        try {
            stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(checkstmt);
            rs = stmt.executeQuery();

            while (rs.next()) {
                //Iterate Row
                ObservableList row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    row.addAll(rs.getString(i));
                }
                System.out.println("Row [1] added " + row);
                data.add(row);
            }

            table.setItems(data);
        } catch (Exception ex) {
            System.out.println("Exception at TableViewCon:fetRowList");
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
    }

    public void setListView() {
        try {
            List<String> steps = new ArrayList<>();
            String checkstmt = "SELECT * FROM hiring_step";
            stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(checkstmt);
            rs = stmt.executeQuery();

            while (rs.next()) {
                steps.add(rs.getString(2));
            }

            listView.getItems().setAll(steps);
        } catch (Exception ex) {
            System.out.println("Exception at TableViewCon:setListView");
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
    }

    private void initListView(ListView<String> listView) {
        listView.getItems().setAll("apples", "oranges", "peaches", "pears");
    }

    @FXML
    private void handleAction(ActionEvent event) {
        if (event.getSource() == btnAdd) {
            String selectedItem = listView.getSelectionModel().getSelectedItem();
            System.out.println(listView.getSelectionModel().getSelectedIndex());
            if (listView1.getItems().contains(selectedItem)) {
                System.out.println("Cannot add duplicate step");
            } else {
                listView1.getItems().add(selectedItem);
            }
        }
        if (event.getSource() == btnDelete) {
            String selectedItem = listView1.getSelectionModel().getSelectedItem();
            listView1.getItems().remove(selectedItem);
        }
        if (event.getSource() == btnReset) {
            listView1.getItems().clear();
        }
        if (event.getSource() == btnCreate) {
            try {
                String sql = "INSERT INTO job_step (jobID, stepID) VALUES (?, (SELECT stepID " +
                        "FROM hiring_step WHERE stepName LIKE %?%))";
                stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(sql);
                stmt.setString(1, "0");
                stmt.setString(2, listView1.getSelectionModel().getSelectedItem());
                stmt.execute();

                System.out.println("Success");
            } catch (Exception exception) {
                System.err.println(exception);
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
        }
    }

    private Label getSpec(String specName) {

        Circle circle = new Circle();
        circle.setCenterX(0.0f);
        circle.setCenterY(0.0f);
        circle.setRadius(4.0f);
        circle.setFill(javafx.scene.paint.Color.rgb(255,182,29));

        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(10);
        dropShadow.setWidth(22);
        dropShadow.setHeight(22);
        dropShadow.setBlurType(BlurType.THREE_PASS_BOX);
        dropShadow.setColor(javafx.scene.paint.Color.rgb(164, 164, 164));

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

    private VBox getStep(int num, String stage) {

        VBox vBox = new VBox();

        Label number = new Label(String.valueOf(num));
        Label step = new Label(stage);

        number.setFont(new Font("Roboto", 25));
        step.setFont(new Font("Roboto", 18));
        step.setStyle("-fx-font-weight: bold;");

        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10);
        vBox.setStyle("-fx-background-color: #ECECEC");

        HBox.setHgrow(vBox, Priority.ALWAYS);
        vBox.getChildren().addAll(number, step);

        return vBox;
    }

}
