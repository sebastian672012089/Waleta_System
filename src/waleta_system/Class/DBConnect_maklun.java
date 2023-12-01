package waleta_system.Class;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class DBConnect_maklun {
    
    MysqlDataSource ds = new MysqlDataSource();
    final String DRIVER = "com.mysql.jdbc.Driver";
    Connection conn = null;
    Statement state = null;
    private Statement stmt;
    
    public DBConnect_maklun()
    {
        System.out.println("connecting .. ");
        ds.setServerName("194.163.41.76");
        ds.setPort(3306);
        ds.setDatabaseName("u1607207_maklun");
        ds.setUser("u1607207_maklun");
        ds.setPassword("waleta12345678");
        
//        ds.setServerName("217.21.72.7");
//        ds.setPort(3306);
//        ds.setDatabaseName("u1602201_toko");
//        ds.setUser("u1602201_admin");
//        ds.setPassword("bastian123");
        
        ds.setAutoReconnect(true);
        try {
            ds.setLoginTimeout(60000);//10detik
            ds.setConnectTimeout(60000);//10detik
            ds.setSocketTimeout(60000);
        } catch (SQLException ex) {
            Logger.getLogger(DBConnect_maklun.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void connect()
    {
        try {
            conn = ds.getConnection();
            stmt = conn.createStatement();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "SERVER ONLINE BERMASALAH!", "Access Denied !", JOptionPane.ERROR_MESSAGE);
//            JOptionPane.showMessageDialog(null, ex, "Access Denied !", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(DBConnect_maklun.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void closeConnection() throws SQLException, Exception
    {
        stmt.close();
        conn.close();
    }
    
    public Statement getStatement(){
        return stmt;
    }
    
    public Connection getConnection(){
        return conn;
    }
    
    public static void main(String[] args) {
        try {
            new DBConnect_maklun().connect();
            System.out.println("berhasil");
        } catch (Exception ex) {
            Logger.getLogger(DBConnect_maklun.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
