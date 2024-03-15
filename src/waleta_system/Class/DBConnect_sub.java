package waleta_system.Class;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class DBConnect_sub {

    MysqlDataSource ds = new MysqlDataSource();
    final String DRIVER = "com.mysql.jdbc.Driver";
    Connection conn = null;
    Statement state = null;
    private Statement stmt;

    public DBConnect_sub() {
        System.out.println("connecting .. ");
        ds.setServerName("194.163.41.103");
        ds.setPort(3306);
        ds.setDatabaseName("u1607207_sub");
        ds.setUser("u1607207_admin_sub");
        ds.setPassword("waletasub_2022");

        ds.setAutoReconnect(true);
        try {
            ds.setLoginTimeout(6000); // 60 seconds
            ds.setConnectTimeout(6000); // 60 seconds
            ds.setSocketTimeout(6000); // 60 seconds
        } catch (SQLException ex) {
            Logger.getLogger(DBConnect_sub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void connect() {
        try {
            conn = ds.getConnection();
            stmt = conn.createStatement();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "SERVER ONLINE BERMASALAH!", "Access Denied !", JOptionPane.ERROR_MESSAGE);
//            JOptionPane.showMessageDialog(null, ex, "Access Denied !", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(DBConnect_sub.class.getName()).log(Level.SEVERE, null, ex);
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
        DBConnect_sub db_sub = new DBConnect_sub();
        try {
            db_sub.connect();
//            String Query = "UPDATE `tb_piutang_karyawan` SET `tgl_lunas`=`tanggal_piutang` WHERE `tgl_lunas` = '0000-00-00'";
//            db_sub.getConnection().createStatement();
//            db_sub.getStatement().executeUpdate(Query);

            System.out.println("Successfully connected.");
            String query = "SELECT * FROM `att_log_waleta` WHERE 1";
            System.out.println("Executing query: " + query);

            Connection con = db_sub.getConnection();
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet result = pst.executeQuery();
//            
            int i = 0;
            while (result.next()) {
                i++;
            }
            System.out.println("Total records retrieved: " + i);
        } catch (Exception ex) {
            Logger.getLogger(DBConnect_sub.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                db_sub.closeConnection();
            } catch (Exception ex) {
                Logger.getLogger(DBConnect_sub.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
