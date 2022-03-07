package recruitment.system.ui.listclient;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import recruitment.system.database.DatabaseHandler;
import recruitment.system.ui.addclient.AddClientController;
import recruitment.system.ui.listclient.detail.ViewClientController;
import recruitment.system.util.RecruitmentSystemUtil;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class ListClientController implements Initializable {

    private PreparedStatement stmt = null;
    private ResultSet rs = null;
    private ObservableList<Client> list = FXCollections.observableArrayList();
    private String recruiterID = null;

    @FXML
    private TableView<Client> tableView;
    @FXML
    private TableColumn<Client, String> colClientID;
    @FXML
    private TableColumn<Client, String> colClientName;
    @FXML
    private TableColumn<Client, String> colCompanyName;
    @FXML
    private TableColumn<Client, String> colEmail;
    @FXML
    private TableColumn<Client, String> colLocation;
    @FXML
    private TableColumn<Client, String> colIndustry;
    @FXML
    private TableColumn<Client, String> colPendingJobOrders;
    @FXML
    private Button btnCreate;
    @FXML
    private TextField txtSearch;

    public void initialize(URL location, ResourceBundle resources) {
        initCol();
        loadData();

        tableView.setRowFactory( tv -> {
            TableRow<Client> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    Client selectedForView = row.getItem();

                    loadWindow(selectedForView);
                }
            });
            return row;
        });

        // Search Client ID, Name, Company Name, Email Address, Industry, Location

        FilteredList<Client> filteredData = new FilteredList<>(list, b -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(client -> {
                // If filter text is empty, display all persons.

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (client.getClientID().toLowerCase().indexOf(lowerCaseFilter) != -1 ) {
                    return true;
                } else if (client.getClientName().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if (client.getCompanyName().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if (client.getEmail().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if (client.getIndustry().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if (client.getLocation().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else {
                    return false;
                }
            });
        });

        // 3. Wrap the FilteredList in a SortedList.
        SortedList<Client> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        // 	  Otherwise, sorting the TableView would have no effect.
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        tableView.setItems(sortedData);
    }

    public void getRecruiterID(String recID) {
        Recruiter recruiter = new Recruiter(recID);
        recruiterID = recruiter.getRecruiterID();
    }

    // For TableView
    private void initCol() {
        colClientID.setCellValueFactory(new PropertyValueFactory<>("clientID"));
        colClientName.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        colCompanyName.setCellValueFactory(new PropertyValueFactory<>("companyName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colIndustry.setCellValueFactory(new PropertyValueFactory<>("industry"));
        colPendingJobOrders.setCellValueFactory(new PropertyValueFactory<>("pendingJobOrders"));
    }

    // Set TableView display
    private void loadData() {
        list.clear();

        try {
            // Display client view table
            String checkstmt = "SELECT * FROM client_view";
            stmt = DatabaseHandler.getInstance().getConnection().prepareStatement(checkstmt);
            rs = stmt.executeQuery();

            while (rs.next()) {
                String clientID = rs.getString(1);
                String clientName = rs.getString(2);
                String companyName = rs.getString(3);
                String email = rs.getString(4);
                String location = rs.getString(5);
                String industry = rs.getString(6);
                String pendingJobOrders = rs.getString(7);


                list.add(new Client(clientID, clientName, companyName, email, location, industry, pendingJobOrders));
            }
        } catch (Exception ex) {
            System.out.println("Exception at ListClientController:loadData" + ex.getLocalizedMessage());
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

        tableView.setItems(list);
    }

    @FXML
    private void btnCreateOnAction(ActionEvent event) {
        try {
            getStage().close();

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("recruitment/system/ui/addclient/AddClient.fxml"));
            Parent parent = loader.load();

            AddClientController controller = (AddClientController) loader.getController();
            controller.getRecruiterID(recruiterID);

            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.setTitle("Create Client");
            stage.setScene(new Scene(parent));
            stage.setResizable(false);
            stage.show();

            RecruitmentSystemUtil.setStageIcon(stage);
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    private void loadWindow(Client selectedForView) {
        try {
            getStage().close();

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("recruitment/system/ui/listclient/detail/ViewClient.fxml"));
            Parent parent = null;

            parent = loader.load();

            ViewClientController controller = (ViewClientController) loader.getController();
            controller.inflateUI(selectedForView, recruiterID);

            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.setTitle("View Client");
            stage.setScene(new Scene(parent));
            stage.show();
            stage.setResizable(false);
            RecruitmentSystemUtil.setStageIcon(stage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Stage getStage() {
        return (Stage) btnCreate.getScene().getWindow();
    }

    // For TableView display
    public static class Client {

        String clientID;
        String clientName;
        String companyName;
        String email;
        String location;
        String industry;
        String pendingJobOrders;

        public Client(String clientID, String clientName, String companyName, String email,
                      String location, String industry, String pendingJobOrders) {

            this.clientID = clientID;
            this.clientName = clientName;
            this.companyName = companyName;
            this.email = email;
            this.location = location;
            this.industry = industry;
            this.pendingJobOrders = pendingJobOrders;
        }

        public void setClientID(String clientID) {
            this.clientID = clientID;
        }

        public String getClientID() {
            return clientID;
        }

        public void setClientName(String clientName) {
            this.clientName = clientName;
        }

        public String getClientName() {
            return clientName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getEmail() {
            return email;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getLocation() {
            return location;
        }

        public void setIndustry(String industry) {
            this.industry = industry;
        }

        public String getIndustry() {
            return industry;
        }

        public void setPendingJobOrders(String pendingJobOrders) {
            this.pendingJobOrders = pendingJobOrders;
        }

        public String getPendingJobOrders() {
            return pendingJobOrders;
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
