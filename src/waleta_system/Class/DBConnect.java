package waleta_system.Class;

import com.mysql.cj.jdbc.MysqlDataSource;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class DBConnect {

    MysqlDataSource ds = new MysqlDataSource();
    final String DRIVER = "com.mysql.cj.jdbc.Driver";
    String username = null;
    String password = null;
    Connection conn = null;
    Statement state = null;
    private Statement stmt;

    public DBConnect() {
        try {
            System.out.println("connecting .. ");
            ds.setServerName("192.168.10.2");
            ds.setPort(3306);
//            ds.setDatabaseName("waleta_dev");
            ds.setDatabaseName("waleta_database");
            ds.setUser("test");
            ds.setPassword("test");
            
//            ds.setServerName("localhost");
//            ds.setPort(3306);
//            ds.setDatabaseName("waleta_database");
//            ds.setUser("root");
//            ds.setPassword("");
            
            ds.setAutoReconnect(true);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex, "Access Denied !", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void connect() {
        try {
            conn = ds.getConnection();
            stmt = conn.createStatement();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex, "Access Denied !", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void closeConnection() throws SQLException, Exception {
        stmt.close();
        conn.close();
    }

    public Statement getStatement() {
        return stmt;
    }

    public Connection getConnection() {
        return conn;
    }

    public static void main(String[] args) {
        DBConnect db_sub = new DBConnect();
        try {
            db_sub.connect();
            System.out.println("Successfully connected.");
            String query = "SELECT * FROM `att_log` WHERE 1";
            System.out.println("Executing query: " + query);

//            String query = "SELECT * FROM `tes` WHERE 1";
//            try (PreparedStatement statement = conn.prepareStatement(query);
//                    ResultSet resultSet = statement.executeQuery()) {
//
//                int count = 0;
//                while (resultSet.next()) {
//                    count++;
//                    System.out.println(count + " = " + resultSet.getString("grade"));
//                }
//                System.out.println("Total records retrieved: " + count);
//            }

            Connection con = db_sub.getConnection();
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet result = pst.executeQuery();
//            
            int i = 0;
            while (result.next()) {
                i++;
//                System.out.println(i + " = " + result.getString("pin") + " = " + result.getString("scan_date"));
            }
            System.out.println("Total records retrieved: " + i);
        } catch (Exception ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                db_sub.closeConnection();
            } catch (Exception ex) {
                Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
