package waleta_system.Class;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TestCabutoAPI {

    public static String postStockData(
            double weight, 
            int pieces, 
            double dryWeight, 
            String notes, 
            String type, 
            String masterGrade, 
            double masterGradePrice, 
            String companyId, 
            String apiUrl) {
        
        String result = "";
        HttpURLConnection connection = null;

        try {
            // Set agar HttpURLConnection mengikuti redirect secara otomatis
            HttpURLConnection.setFollowRedirects(true);
            // URL API
            URL url = new URL("http://" + apiUrl + "/cheat/stock/" + companyId);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true); // Enable writing output to request body

            // Create JSON object as string
            String jsonInputString = String.format(
                "{\"weight\": %f, \"pieces\": %d, \"dryWeight\": %f, \"notes\": \"%s\", " +
                "\"type\": \"%s\", \"masterGrade\": \"%s\", \"masterGradePrice\": %f}",
                weight, pieces, dryWeight, notes, type, masterGrade, masterGradePrice
            );

            // Write JSON data to request body
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Get the response code (e.g., 200 for success)
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                result = "Request successful!";
            } else {
                result = "Request failed with response code: " + responseCode;
            }

        } catch (Exception e) {
            result = "Error: " + e.getMessage();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return result;
    }

    public static void main(String[] args) {
        String apiUrl = "apicabuto.waleta019.com";  // ganti dengan API URL yang valid
        String companyId = "1";  // ganti dengan companyId yang valid

        String response = postStockData(
            15.5d,     // weight
            10,       // pieces
            14.3d,     // dryWeight
            "Percobaan", // notes
            "A",      // type
            "Grade 1", // masterGrade
            200.5d,    // masterGradePrice
            companyId,
            apiUrl
        );

        System.out.println(response);
    }

}
