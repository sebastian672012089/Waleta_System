package waleta_system.Class;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class DBConnect {

    MysqlDataSource ds = new MysqlDataSource();
    final String DRIVER = "com.mysql.jdbc.Driver";
    String username = null;
    String password = null;
    Connection conn = null;
    Statement state = null;
    private Statement stmt;

    public DBConnect() {
        System.out.println("connecting .. ");
        ds.setServerName("192.168.10.2");
        ds.setPort(3306);
        ds.setDatabaseName("waleta_database");
        ds.setUser("test");
        ds.setPassword("test");
        ds.setAutoReconnect(true);
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
        try {
            new DBConnect().connect();
            System.out.println("berhasil");
        } catch (Exception ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
