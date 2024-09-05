package waleta_system.Class;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ApiClient {

    public static void sendMessage(String number, String message) {
        try {
            // URL API
            URL url = new URL("http://192.168.28.100:4004/message/send");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            // Pengaturan request
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true); // Membuka aliran output
            
            // Membuat body JSON
            String jsonInputString = String.format("{\"number\":\"%s\",\"message\":\"%s\"}", number, message);
            System.out.println("Sending JSON: " + jsonInputString);

            // Menulis body ke request
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            // Eksekusi request dan cek respons
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Message sent successfully.");
            } else {
                System.out.println("Failed to send message. HTTP Response Code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Contoh penggunaan
        sendMessage("82223186045", "tes");
    }
}




