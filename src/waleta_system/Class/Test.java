package waleta_system.Class;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

public class Test {

    private static final DBConnect db = new DBConnect();

    public Test() {

    }

    private static String getStatusAkhir(String statusAwal, String no_LP) {
        db.connect();
        String StatusAkhir = null;
        switch (statusAwal) {
            case "PASSED":
                StatusAkhir = "PASSED";
                break;
            case "HOLD/NON GNS":
                StatusAkhir = "HOLD/NON GNS";
                String status_treatment_utuh = "PASSED";
                String status_treatment_flat = "PASSED";
                String status_treatment_jidun = "PASSED";
                try {
                    String c = "SELECT\n"
                            + "(SELECT `status` FROM `tb_lab_treatment_lp` WHERE `no_laporan_produksi` = '" + no_LP + "' AND `jenis_barang` = 'Utuh' ORDER BY `nitrit_akhir` LIMIT 1) AS 'status_utuh',\n"
                            + "(SELECT `status` FROM `tb_lab_treatment_lp` WHERE `no_laporan_produksi` = '" + no_LP + "' AND `jenis_barang` = 'Flat' ORDER BY `nitrit_akhir` LIMIT 1) AS 'status_flat',\n"
                            + "(SELECT `status` FROM `tb_lab_treatment_lp` WHERE `no_laporan_produksi` = '" + no_LP + "' AND `jenis_barang` = 'Jidun' ORDER BY `nitrit_akhir` LIMIT 1) AS 'status_jidun'\n"
                            + "FROM DUAL";
                    ResultSet rs = db.getStatement().executeQuery(c);
                    if (rs.next()) {
                        if (rs.getString("status_utuh") != null) {
                            status_treatment_utuh = rs.getString("status_utuh");
                            System.out.println("status_treatment_utuh : " + status_treatment_utuh);
                        }
                        if (rs.getString("status_flat") != null) {
                            status_treatment_flat = rs.getString("status_flat");
                            System.out.println("status_treatment_flat : " + status_treatment_flat);
                        }
                        if (rs.getString("status_jidun") != null) {
                            status_treatment_jidun = rs.getString("status_jidun");
                            System.out.println("status_treatment_jidun : " + status_treatment_jidun);
                        }
                        if (rs.getString("status_utuh") == null 
                                && rs.getString("status_flat") == null 
                                && rs.getString("status_jidun") == null) {
                            StatusAkhir = "HOLD/NON GNS";
                        } else if ("HOLD/NON GNS".equals(status_treatment_flat) 
                                || "HOLD/NON GNS".equals(status_treatment_utuh) 
                                || "HOLD/NON GNS".equals(status_treatment_jidun)) {
                            StatusAkhir = "HOLD/NON GNS";
                        } else {
                            StatusAkhir = "PASSED";
                        }
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "ERROR");
                    Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
                }

                break;
            case "":
                StatusAkhir = null;
                break;
            default:
                JOptionPane.showMessageDialog(null, "ERROR");
                break;
        }
        return StatusAkhir;
    }

    private void saveReportToServer(JasperPrint jasperPrint) {
        try {
            String serverURL = "https://waleta019.com/dokumen_laporan/laporan.pdf";
            URL url = new URL(serverURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST"); // You might use POST or other methods depending on server requirements

            OutputStream outputStream = connection.getOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Successfully uploaded the file to the server
                System.out.println("File uploaded successfully.");
            } else {
                // Handle other response codes or errors
                System.out.println("File upload failed. Response Code: " + responseCode);
            }

            outputStream.close();
            connection.disconnect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void UploadFileToServer() {
//        String serverUrl = "http://192.168.10.2:5050/waleta/documents/laporan"; // Replace with your server URL
        String serverUrl = "https://waleta019.com/dokumen_laporan"; // Replace with your server URL
        String filePath = "E:\\laporan\\logo_waleta.png"; // Replace with the path to your file

        try {
            File file = new File(filePath);
            URL url = new URL(serverUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Enable writing to the connection and set the request method to POST
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            // Create a FileInputStream to read the file
            FileInputStream fileInputStream = new FileInputStream(file);

            // Get an output stream from the connection to write the file content
            OutputStream outputStream = connection.getOutputStream();

            // Read the file content into a buffer and write it to the output stream
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            // Close streams
            fileInputStream.close();
            outputStream.flush();
            outputStream.close();

            // Get the response code to verify the upload
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // File uploaded successfully
                System.out.println("File uploaded successfully.");
            } else {
                // Handle upload failure
                System.out.println("File upload failed. Response code: " + responseCode);
            }
        } catch (IOException e) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void print() {
        try {
            Utility.db.connect();
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Catatan_Penerimaan_dan_Grading_Sarang_Burung_Mentah.jrxml");
            Map<String, Object> map = new HashMap<>();
            map.put("no_kartu_waleta", "23M625");//parameter name should be like it was named inside your report.
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
//            JasperExportManager.exportReportToPdfFile(JASP_PRINT, "E:\\laporan\\laporan.pdf");
//            saveReportToServer(JASP_PRINT);
        } catch (JRException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        try {
            System.out.println(getStatusAkhir("HOLD/NON GNS", "WL-240615017-"));
        } catch (Exception ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
