package recruitment.system.database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import recruitment.system.ui.listapplication.ListApplicationController;
import recruitment.system.ui.listclient.ListClientController;
import recruitment.system.ui.listjob.ListJobController;

import javax.swing.*;
import java.sql.*;

public final class DatabaseHandler {

    private static DatabaseHandler handler = null;
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/recruitment_db?" +
            "useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode" +
            "=false&serverTimezone=UTC";
    final static String USER = "admin";
    final static String PASSWORD = "07282000";
    private static Connection conn = null;
    private static Statement stmt = null;

    static {
        createConnection();
    }

    public static void main(String[] args) throws Exception {
        DatabaseHandler.getInstance();
    }

    private DatabaseHandler() {
    }

    public Connection getConnection() {
        return conn;
    }

    public static DatabaseHandler getInstance() {
        if (handler == null) {
            handler = new DatabaseHandler();
        }
        return handler;
    }

    private static void createConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Cant load database", "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(0);
        }
    }

    public ResultSet execQuery(String query) {
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
        }
        catch (SQLException ex) {
            System.out.println("Exception at DatabaseHandler:execQuery" + ex.getLocalizedMessage());
            return null;
        }
        finally {
        }
        return rs;
    }

    public boolean execAction(String qu) {
        try {
            stmt = conn.createStatement();
            stmt.execute(qu);
            return true;
        }
        catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error:" + ex.getMessage(), "Error Occured", JOptionPane.ERROR_MESSAGE);
            System.out.println("Exception at DatabaseHandler:execAction" + ex.getLocalizedMessage());
            return false;
        }
        finally {
        }
    }

    public boolean deleteSelection(String ID, String deleteStatement) {
        try {
            PreparedStatement stmt = conn.prepareStatement(deleteStatement);
            stmt.setString(1, ID);
            int res = stmt.executeUpdate();
            if (res == 1) {
                return true;
            }
        }
        catch (SQLException ex) {
            System.err.println(ex);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }

    public ObservableList<PieChart.Data> getApplicationStatistic() {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        try {
            // Count all hired applications for the month
            String qu1 = "SELECT COUNT(*) " +
                    "FROM application " +
                    "WHERE appStatus = 'Hired' AND MONTH(lastUpdate) = MONTH(NOW())";
            // Count all application for the month
            String qu2 = "SELECT COUNT(*) " +
                    "FROM application " +
                    "WHERE MONTH(dateOfApplication) = MONTH(NOW())";
            ResultSet rs = execQuery(qu1);
            if (rs.next()) {
                int count = rs.getInt(1);
                data.add(new PieChart.Data("Total Hired (" + count + ")", count));
            }
            rs = execQuery(qu2);
            if (rs.next()) {
                int count = rs.getInt(1);
                data.add(new PieChart.Data("Total Application (" + count + ")", count));
            }
        }
        catch (Exception e) {
            System.out.println("Error");
            e.printStackTrace();
        }
        return data;
    }

}
