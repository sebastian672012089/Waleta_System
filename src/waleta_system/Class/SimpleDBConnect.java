package waleta_system.Class;

import java.sql.*;

public class SimpleDBConnect {

    private static final String SOURCE_DB_URL = "jdbc:mysql://194.163.41.76:3306/u1607207_sub?autoReconnect=true";
    private static final String DEST_DB_URL = "jdbc:mysql://194.163.41.103:3306/u1607207_sub?autoReconnect=true";
//    private static final String url = "jdbc:mysql://srv169.niagahoster.com:3306/u1607207_sub";
    private static final String user = "u1607207_admin_sub";
    private static final String password = "waletasub_2022";

    public static void migrateDataLP() throws SQLException {
        Connection sourceConn = null;
        Connection destConn = null;
        try {
            // Connect to both databases
            sourceConn = DriverManager.getConnection(SOURCE_DB_URL, user, password);
            destConn = DriverManager.getConnection(DEST_DB_URL, user, password);

            String tableName = "tb_laporan_produksi";
            System.out.println(tableName);

            // Fetch data from source table
            Statement fetchDataStmt = sourceConn.createStatement();
            ResultSet tableData = fetchDataStmt.executeQuery("SELECT * FROM " + tableName + " WHERE tanggal_lp > '2024-02-20'");

            // Get metadata for the table
            ResultSetMetaData tableMetaData = tableData.getMetaData();
            int columnCount = tableMetaData.getColumnCount();

            // Prepare INSERT statement for destination database
            StringBuilder insertQuery = new StringBuilder("INSERT INTO " + tableName + " VALUES (");
            for (int i = 0; i < columnCount; i++) {
                insertQuery.append("?");
                if (i < columnCount - 1) {
                    insertQuery.append(", ");
                }
            }
            insertQuery.append(") ");
            insertQuery.append("ON DUPLICATE KEY UPDATE ");
            for (int i = 1; i <= columnCount; i++) {
                String columnName = tableMetaData.getColumnLabel(i);
                insertQuery.append(columnName);
                insertQuery.append(" = ?");
                if (i < columnCount) {
                    insertQuery.append(", ");
                }
            }
            System.out.println(insertQuery);

            PreparedStatement insertStmt = destConn.prepareStatement(insertQuery.toString());

            // Iterate over rows of the current table
            while (tableData.next()) {
                System.out.print(":");
                for (int i = 1; i <= columnCount; i++) {
                    insertStmt.setObject(i, tableData.getObject(i));
                    insertStmt.setObject(i+26, tableData.getObject(i));
                }
                insertStmt.executeUpdate();
            }
            System.out.println("");

            fetchDataStmt.close();
            insertStmt.close();

        } finally {
            if (sourceConn != null) {
                sourceConn.close();
            }
            if (destConn != null) {
                destConn.close();
            }
        }
    }

    public static void migrateData() throws SQLException {
        Connection sourceConn = null;
        Connection destConn = null;
        try {
            // Connect to both databases
            sourceConn = DriverManager.getConnection(SOURCE_DB_URL, user, password);
            destConn = DriverManager.getConnection(DEST_DB_URL, user, password);

            String tableName = "att_log";
            System.out.println(tableName);

            // Fetch data from source table
            Statement fetchDataStmt = sourceConn.createStatement();
            ResultSet tableData = fetchDataStmt.executeQuery("SELECT * FROM " + tableName + " WHERE DATE(scan_date) > '2024-02-20'");

            // Get metadata for the table
            ResultSetMetaData tableMetaData = tableData.getMetaData();
            int columnCount = tableMetaData.getColumnCount();

            // Prepare INSERT statement for destination database
            StringBuilder insertQuery = new StringBuilder("INSERT IGNORE INTO " + tableName + " VALUES (");
            for (int i = 0; i < columnCount; i++) {
                insertQuery.append("?");
                if (i < columnCount - 1) {
                    insertQuery.append(", ");
                }
            }
            insertQuery.append(") ");
            System.out.println(insertQuery);

            PreparedStatement insertStmt = destConn.prepareStatement(insertQuery.toString());

            // Iterate over rows of the current table
            while (tableData.next()) {
                System.out.print(":");
                for (int i = 1; i <= columnCount; i++) {
                    insertStmt.setObject(i, tableData.getObject(i));
                }
                insertStmt.executeUpdate();
            }
            System.out.println("");

            fetchDataStmt.close();
            insertStmt.close();

        } finally {
            if (sourceConn != null) {
                sourceConn.close();
            }
            if (destConn != null) {
                destConn.close();
            }
        }
    }

    public static void main(String[] args) {
        try {
            migrateData();
            System.out.println("Data migration completed successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Data migration failed.");
        }
//        try (Connection conn = DriverManager.getConnection(url, user, password)) {
//            System.out.println("Connected to database.");
//
//            String query = "SELECT * FROM `att_log` WHERE DATE(`scan_date`) = CURRENT_DATE;";
//            try (PreparedStatement statement = conn.prepareStatement(query);
//                    ResultSet resultSet = statement.executeQuery()) {
//
//                int count = 0;
//                while (resultSet.next()) {
//                    count++;
////                    System.out.println(count + " = " + resultSet.getString("pin"));
//                }
//                System.out.println("Total records retrieved: " + count);
//            }
//
//        } catch (SQLException ex) {
//            System.out.println("Failed to connect to database: " + ex.getMessage());
//        }
    }
}
