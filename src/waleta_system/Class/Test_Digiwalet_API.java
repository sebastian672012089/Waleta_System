package waleta_system.Class;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Test_Digiwalet_API {

    public static void readJson(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            
            // Read hasil_grading_bulu
            JSONArray gradingBuluArray = jsonObject.getJSONObject("data").getJSONArray("hasil_grading_bulu");
            for (int i = 0; i < gradingBuluArray.length(); i++) {
                JSONObject item = gradingBuluArray.getJSONObject(i);
                String jenisBulu = item.getString("jenis_bulu");
                String jumlahPersen = item.getString("jumlah_persen");
                System.out.println("Jenis Bulu: " + jenisBulu + ", Jumlah Persen: " + jumlahPersen);
            }
            
            // Read hasil_grading_warna
            JSONArray gradingWarnaArray = jsonObject.getJSONObject("data").getJSONArray("hasil_grading_warna");
            for (int i = 0; i < gradingWarnaArray.length(); i++) {
                JSONObject item = gradingWarnaArray.getJSONObject(i);
                String jenisWarna = item.getString("jenis_warna");
                int berat = item.getInt("berat");
                System.out.println("Jenis Warna: " + jenisWarna + ", Berat: " + berat);
            }
            
            // Read hasil_grading_bentuk
            JSONArray gradingBentukArray = jsonObject.getJSONObject("data").getJSONArray("hasil_grading_bentuk");
            for (int i = 0; i < gradingBentukArray.length(); i++) {
                JSONObject item = gradingBentukArray.getJSONObject(i);
                String gradeBahan = item.getString("grade_bahan");
                int berat = item.getInt("berat");
                System.out.println("Grade Bahan: " + gradeBahan + ", Berat: " + berat);
            }
            
            // Read other fields
            String status = jsonObject.getJSONObject("data").getString("status");
            String noIdentitas = jsonObject.getJSONObject("data").getString("no_identitas");
            int umurStok = jsonObject.getJSONObject("data").getInt("umur_stok");
            int totalHarga = jsonObject.getJSONObject("data").getInt("total_harga");
            int totalLabaRugi = jsonObject.getJSONObject("data").getInt("total_laba_rugi");
            String totalBerat = jsonObject.getJSONObject("data").getString("total_berat");
            System.out.println("Status: " + status);
            System.out.println("No Identitas: " + noIdentitas);
            System.out.println("Umur Stok: " + umurStok);
            System.out.println("Total Harga: " + totalHarga);
            System.out.println("Total Laba Rugi: " + totalLabaRugi);
            System.out.println("Total Berat: " + totalBerat);
            
            // Read detail_grade
            JSONArray detailGradeArray = jsonObject.getJSONObject("data").getJSONArray("detail_grade");
            for (int i = 0; i < detailGradeArray.length(); i++) {
                JSONObject item = detailGradeArray.getJSONObject(i);
                String grade = item.getString("grade");
                String gram = item.getString("gram");
                int hargaKg = item.getInt("harga_kg");
                String kadarAir = item.getString("kadar_air");
                String kadarPenurunanBerat = item.getString("kadar_penurunan_berat");
                System.out.println("Grade: " + grade + ", Gram: " + gram + ", Harga/Kg: " + hargaKg + ", Kadar Air: " + kadarAir + ", Kadar Penurunan Berat: " + kadarPenurunanBerat);
            }
        } catch (JSONException ex) {
            Logger.getLogger(Test_Digiwalet_API.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String fetchDataFromApi(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        } else {
            throw new IOException("Failed to fetch data from API, HTTP error code: " + responseCode);
        }
    }

    public static void main(String[] args) {
        String apiUrl = "https://login.digiwalet.id/api/internal-digiwalet/hasil-sampling_nokartu?no_kartu_waleta=23HM286";
        try {
            String responseData = fetchDataFromApi(apiUrl);
            System.out.println(responseData);
            readJson(responseData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
