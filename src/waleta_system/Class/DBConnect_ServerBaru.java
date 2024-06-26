package waleta_system.Class;

import com.mysql.cj.jdbc.MysqlDataSource;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class DBConnect_ServerBaru {

    MysqlDataSource ds = new MysqlDataSource();
    final String DRIVER = "com.mysql.cj.jdbc.Driver";
    Connection conn = null;
    Statement state = null;
    private Statement stmt;

    public DBConnect_ServerBaru() {
        try {
            System.out.println("connecting .. ");
            ds.setServerName("213.210.37.19");
            ds.setPort(3306);
            ds.setDatabaseName("waleta_cabuto");
            ds.setUser("waleta_main");
            ds.setPassword("7RMrPLYLA302");

            ds.setAutoReconnect(true);
            
            ds.setLoginTimeout(60000);//10detik
            ds.setConnectTimeout(60000);//10detik
            ds.setSocketTimeout(60000);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex, "Access Denied !", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(DBConnect_ServerBaru.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void connect() {
        try {
            conn = ds.getConnection();
            stmt = conn.createStatement();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "SERVER ONLINE BERMASALAH!", "Access Denied !", JOptionPane.ERROR_MESSAGE);
//            JOptionPane.showMessageDialog(null, ex, "Access Denied !", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(DBConnect_ServerBaru.class.getName()).log(Level.SEVERE, null, ex);
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
            DBConnect_ServerBaru db = new DBConnect_ServerBaru();
            db.connect();
            System.out.println("berhasil");
        } catch (Exception ex) {
            Logger.getLogger(DBConnect_ServerBaru.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
