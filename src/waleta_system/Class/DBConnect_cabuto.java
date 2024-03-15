package waleta_system.Class;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class DBConnect_cabuto {
    
    MysqlDataSource ds = new MysqlDataSource();
    final String DRIVER = "com.mysql.jdbc.Driver";
    Connection conn = null;
    Statement state = null;
    private Statement stmt;
    
    public DBConnect_cabuto()
    {
        System.out.println("connecting .. ");
        ds.setServerName("194.163.41.103");
        ds.setPort(3306);
        ds.setDatabaseName("u1607207_cabuto");
        ds.setUser("u1607207_admin_cabuto");
        ds.setPassword("c4bcabcabto");
        
        ds.setAutoReconnect(true);
        try {
            ds.setLoginTimeout(60000);//10detik
            ds.setConnectTimeout(60000);//10detik
            ds.setSocketTimeout(60000);
        } catch (SQLException ex) {
            Logger.getLogger(DBConnect_cabuto.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(DBConnect_cabuto.class.getName()).log(Level.SEVERE, null, ex);
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
            DBConnect_cabuto db_cabuto = new DBConnect_cabuto();
            db_cabuto.connect();
            System.out.println("berhasil");
            String query = "SELECT * FROM `tb_pricelist` WHERE 1";
            System.out.println(query);
            
            Connection con = db_cabuto.getConnection();
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet result = pst.executeQuery();
            
            int i = 0;
            while (result.next()) {
                i++;
//                System.out.println(i + " = " + result.getString("waktu_order"));
            }
            System.out.println("Total records retrieved: " + i);
        } catch (Exception ex) {
            Logger.getLogger(DBConnect_cabuto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
