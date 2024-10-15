package waleta_system.Class;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import static org.openxmlformats.schemas.drawingml.x2006.main.CTRelativeRect.type;

public class TestCabutoAPI_masterStock {

    public static String postStockData(
            double weight, 
            String arrival, 
            String notes, 
            String apiUrl,
            String token) {
        
        String result = "";
        HttpURLConnection connection = null;

        try {
            // Set agar HttpURLConnection mengikuti redirect secara otomatis
            HttpURLConnection.setFollowRedirects(false);
            // URL API
            URL url = new URL("http://" + apiUrl + "/admin/masterstock");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + token);// Tambahkan token ke header Authorization
            connection.setDoOutput(true); // Enable writing output to request body

            // Create JSON object as string
            String jsonInputString = String.format(
                "{\"weight\": %f, \"arrival\": %s, \"notes\": %s}",
                weight, arrival, notes
            );

            // Write JSON data to request body
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Get the response code
            int responseCode = connection.getResponseCode();

            switch (responseCode) {
                case HttpURLConnection.HTTP_MOVED_PERM:
                case HttpURLConnection.HTTP_MOVED_TEMP:
                    // Ambil lokasi redirect dari header "Location"
                    String redirectUrl = connection.getHeaderField("Location");
                    result = "Request redirected to: " + redirectUrl;
                    break;
                case HttpURLConnection.HTTP_OK:
                    result = "Request successful!";
                    break;
                default:
                    result = "Request failed with response code: " + responseCode;
                    break;
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
        String apiUrl = "apicabuto.waleta019.com"; 
        String token = "";

        String response = postStockData(
            1500d,     // weight
            "2024-10-15T00:00:00.000Z", // arrival
            "Percobaan", // notes
            apiUrl,
            token
        );

        System.out.println(response);
    }

}
