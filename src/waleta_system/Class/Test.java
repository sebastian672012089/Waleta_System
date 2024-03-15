package waleta_system.Class;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    public static DBConnect db = new DBConnect();

    public Test() {

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

        } catch (Exception ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
