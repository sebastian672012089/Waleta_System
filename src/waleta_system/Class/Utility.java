package waleta_system.Class;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Utility {

    public static DBConnect db = new DBConnect();
    public static DBConnect_sub db_sub = new DBConnect_sub();
    public static DBConnect_maklun db_maklun = new DBConnect_maklun();
    public static DBConnect_cabuto db_cabuto = new DBConnect_cabuto();
    public static final int ROTATE_LEFT = 1;
    public static final int ROTATE_RIGHT = -1;
    public static int ROTATE = 1;
    
    public static void detectAnonymousCharacters(String str) {
        // Regular expression to detect non-printable characters
        String regex = "[^\\p{Print}]";

        // Find anonymous characters using regular expression
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(str);

        // Print the anonymous characters found
        while (matcher.find()) {
            System.out.println("Anonymous character detected: " + java.util.Arrays.toString(matcher.group().getBytes()));
        }
    }
    
    public static String removeAnonymousCharacters(String str) {
        // Regular expression to remove non-printable characters
        String regex = "[^\\p{Print}]";

        // Remove anonymous characters using regular expression
        return str.replaceAll(regex, "");
    }

    public static DecimalFormat DecimalFormatUS(DecimalFormat decimalFormat) {
        Locale currentLocale = Locale.getDefault();

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(currentLocale);
        otherSymbols.setDecimalSeparator('.');
//        otherSymbols.setGroupingSeparator(' ');

//        Locale currentLocale = Locale.US;
//        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(currentLocale);
        decimalFormat.setDecimalFormatSymbols(otherSymbols);
        return decimalFormat;
    }

    public static Date addDaysSkippingSundays(Date date, int days) {
        Date result_date = date;
        int addedDays = 0;
        while (addedDays < days) {
            result_date = new Date(result_date.getTime() + (1000 * 60 * 60 * 24));
            if (!new SimpleDateFormat("EEEE", Locale.ENGLISH).format(result_date).equals("Sunday")) {
                ++addedDays;
            }
        }
        return result_date;
    }

    public static Date addDaysSkippingFreeDays(Date date, int days) throws Exception {
        Date result_date = date;
        int addedDays = 0;
        while (addedDays < days) {
            result_date = new Date(result_date.getTime() + (1000 * 60 * 60 * 24));
            String query = "SELECT `tanggal_libur` FROM `tb_libur` WHERE `tanggal_libur` = '" + new SimpleDateFormat("yyyy-MM-dd").format(result_date) + "'";
            ResultSet rs = db.getStatement().executeQuery(query);
            if (!rs.next() && !new SimpleDateFormat("EEEE", Locale.ENGLISH).format(result_date).equals("Sunday")) {
                ++addedDays;
            }
        }
        return result_date;
    }

    public static Date addDays(Date date, int days) {
        Date result = date;
        int addedDays = 0;
        while (addedDays < days) {
            result = new Date(result.getTime() + (1000 * 60 * 60 * 24));
            ++addedDays;
        }
        return result;
    }

    public static LocalDate addDaysSkippingWeekends(LocalDate date, int days) {
        LocalDate result = date;
        int addedDays = 0;
        while (addedDays < days) {
            result = result.plusDays(1);
            if (result.getDayOfWeek() != DayOfWeek.SUNDAY) {
                ++addedDays;
            }
        }
        return result;
    }

    public static int countDaysWithoutFreeDays(Date date1, Date date2) {
        long diff, jumlah_hari = 0;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            int jumlah_libur = 0;
            String sql = "SELECT COUNT(`tanggal_libur`) AS 'hari_libur'"
                    + "FROM `tb_libur` "
                    + "WHERE `tanggal_libur` BETWEEN '" + dateFormat.format(date1) + "' AND '" + dateFormat.format(date2) + "'"
                    + "AND DAYNAME(`tanggal_libur`) <> 'Sunday'";
            ResultSet rst = db.getStatement().executeQuery(sql);
            if (rst.next()) {
                jumlah_libur = rst.getInt("hari_libur");
            }
            diff = date2.getTime() - date1.getTime();
            jumlah_hari = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
//            System.out.println(jumlah_hari);
            int jumlah_minggu = 0;
            for (int i = 0; i < jumlah_hari; i++) {
                if (new SimpleDateFormat("EEE").format(date1).equals("Sun")) {
                    jumlah_minggu++;
                }
                date1 = new Date(date1.getTime() + (1 * 24 * 60 * 60 * 1000));
            }
            jumlah_hari = jumlah_hari - (jumlah_libur + jumlah_minggu);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
            Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (int) jumlah_hari;
    }

    public static boolean ApakahIniRuangSub(String ruang) throws Exception {
        Utility.db_sub.connect();
        String query = "SELECT `kode_sub` FROM `tb_sub_waleta` WHERE `kode_sub` = '" + ruang + "'";
        ResultSet rs = Utility.db_sub.getStatement().executeQuery(query);
        return rs.next();
    }

    public static ImageIcon ResizeImageIcon(ImageIcon MyImage, int widthTo, int heightTo) {
        int imgHeight = MyImage.getIconHeight();
        int imgWidth = MyImage.getIconWidth();
        Image img = MyImage.getImage();
        if (imgHeight > heightTo) {
            imgHeight = heightTo;
        }
        if (imgWidth > widthTo) {
            imgWidth = widthTo;
        }

        Image newImg = img.getScaledInstance(imgWidth - 2, imgHeight - 2, Image.SCALE_SMOOTH);
        ImageIcon image = new ImageIcon(newImg);
        return image;
    }

    public static ImageIcon ResizeImage(String ImagePath, byte[] pic, int widthTo, int heightTo) {
        if (ImagePath != null || pic != null) {
            ImageIcon MyImage = null;
            if (ImagePath != null) {
                MyImage = new ImageIcon(ImagePath);
            } else if (pic != null) {
                MyImage = new ImageIcon(pic);
            }
            int imgHeight = MyImage.getIconHeight();
            int imgWidth = MyImage.getIconWidth();
            Image img = MyImage.getImage();
            if (imgWidth > widthTo) {
                imgWidth = widthTo - 1;
                imgHeight = (MyImage.getIconHeight() / MyImage.getIconWidth()) * widthTo;
                if (imgHeight > heightTo) {
                    imgHeight = heightTo - 1;
                    imgWidth = (MyImage.getIconWidth() / MyImage.getIconHeight()) * heightTo;
                }
            } else if (imgHeight > heightTo) {
                imgHeight = heightTo - 1;
                imgWidth = (MyImage.getIconWidth() / MyImage.getIconHeight()) * heightTo;
                if (imgWidth > widthTo) {
                    imgWidth = widthTo - 1;
                    imgHeight = (MyImage.getIconHeight() / MyImage.getIconWidth()) * widthTo;
                }
            }
            Image newImg = img.getScaledInstance(imgWidth - 2, imgHeight - 2, Image.SCALE_SMOOTH);
            MyImage = new ImageIcon(newImg);
            return MyImage;
        } else {
            return null;
        }
    }

    public static boolean isURLReachable(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (Exception e) {
            return false;
        }
    }

    public static ImageIcon getAndResizeImageFromURL(String imageURL, int widthTo, int heightTo) throws Exception {
        if (imageURL != null && isURLReachable(imageURL)) {
            Image img;
            URL url = new URL(imageURL);
            img = ImageIO.read(url);

            int imgWidth = img.getWidth(null);
            int imgHeight = img.getHeight(null);

            if (imgWidth > widthTo || imgHeight > heightTo) {
                // Calculate aspect ratio
                double scaleFactor = Math.min((double) widthTo / imgWidth, (double) heightTo / imgHeight);
                imgWidth = (int) (imgWidth * scaleFactor);
                imgHeight = (int) (imgHeight * scaleFactor);
            }

            Image newImg = img.getScaledInstance(imgWidth, imgHeight, Image.SCALE_SMOOTH);
            return new ImageIcon(newImg);
        }
        return null;
    }

    public static Map<String, Object> deleteFileFromURL(String fileUrl) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        if (fileUrl != null && isURLReachable(fileUrl)) {
            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                resultMap.put("resultCode", 1);
                resultMap.put("message", "File deleted successfully.");
            } else {
                resultMap.put("resultCode", 0);
                resultMap.put("message", "Failed to delete file. Response code: " + responseCode);
                System.out.println("Failed to delete file. Response code: " + responseCode);
            }
        } else {
            resultMap.put("resultCode", 0);
            resultMap.put("message", "URL is null or unreachable.");
        }
        return resultMap;
    }

    public static Map<String, Object> renameFileOnServer(String oldFileName, String newFileName, String directory) throws IOException {
        Map<String, Object> resultMap = new HashMap<>();
        String serverUrl = "http://192.168.10.2:5050/waleta/images/rename_file.php";
        try {
            URL url = new URL(serverUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Construct data to send
            String postData = "oldFileName=" + oldFileName + "&newFileName=" + newFileName + "&directory=" + directory;

            // Send data
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(postData);
            writer.flush();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                resultMap.put("resultCode", 1);
                resultMap.put("message", "File renamed successfully.");
            } else {
                resultMap.put("resultCode", 0);
                resultMap.put("message", "Failed to rename file. Response code: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
            resultMap.put("resultCode", 0);
            resultMap.put("message", "Error: " + e.getMessage());
        }
        return resultMap;
    }

    public static void rotate90(File input, File output, int direction) {
        try {
            ImageInputStream iis = ImageIO.createImageInputStream(input);
            Iterator<ImageReader> iterator = ImageIO.getImageReaders(iis);
            ImageReader reader = iterator.next();
            String format = reader.getFormatName();

            BufferedImage image = ImageIO.read(iis);
            int width = image.getWidth();
            int height = image.getHeight();

            BufferedImage rotated = new BufferedImage(height, width, image.getType());
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    switch (direction) {
                        case ROTATE_LEFT:
                            rotated.setRGB(y, (width - 1) - x, image.getRGB(x, y));
                            break;
                        case ROTATE_RIGHT:
                            rotated.setRGB((height - 1) - y, x, image.getRGB(x, y));
                            break;
                    }
                }
            }
            ImageIO.write(rotated, format, output);
        } catch (Exception e) {
            Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static void createImage(File input, File output) {
        try {
            ImageInputStream iis = ImageIO.createImageInputStream(input);
            Iterator<ImageReader> iterator = ImageIO.getImageReaders(iis);
            ImageReader reader = iterator.next();
            String format = reader.getFormatName();

            BufferedImage image = ImageIO.read(iis);
            int width = image.getWidth();
            int height = image.getHeight();

            BufferedImage rotated = new BufferedImage(width, height, image.getType());
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    rotated.setRGB(x, y, image.getRGB(x, y));
                }
            }
            ImageIO.write(rotated, format, output);
        } catch (Exception e) {
            Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static BufferedImage imageIconToBufferedImage(ImageIcon icon) {
        BufferedImage bufferedImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(),
                BufferedImage.TYPE_BYTE_GRAY);
        Graphics graphics = bufferedImage.createGraphics();
        icon.paintIcon(null, graphics, 0, 0);
        graphics.dispose();//from   w  ww.j a  va  2  s.  co m
        return bufferedImage;
    }

    public static String bytesToString(final byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (byte bt : bytes) {
            sb.append(String.format("%02x", bt));
        }
        return sb.toString();
    }

    public static String ASCIItoHEX(String text) {
        // Initialize final String
        String hex = "";

        // Make a loop to iterate through
        // every character of ascii string
        for (int i = 0; i < text.length(); i++) {

            // take a char from
            // position i of string
            char ch = text.charAt(i);

            // cast char to integer and
            // find its ascii value
            int in = (int) ch;

            // change this ascii value
            // integer to hexadecimal value
            String part = Integer.toHexString(in);

            // add this hexadecimal value
            // to final string.
            hex += part;
        }

        // return the final string hex
        return hex;
    }

    public static String ubah_ke_INDONESIA(String EnglishDayName) {
        String IndonesianDayName = "";

        switch (EnglishDayName.toLowerCase()) {
            case "sunday":
                IndonesianDayName = "Minggu";
                break;
            case "monday":
                IndonesianDayName = "Senin";
                break;
            case "tuesday":
                IndonesianDayName = "Selasa";
                break;
            case "wednesday":
                IndonesianDayName = "Rabu";
                break;
            case "thursday":
                IndonesianDayName = "Kamis";
                break;
            case "friday":
                IndonesianDayName = "Jumat";
                break;
            case "saturday":
                IndonesianDayName = "Sabtu";
                break;
            default:
                IndonesianDayName = "Hari tidak valid"; // Hari tidak dikenali
        }

        return IndonesianDayName;
    }

    public static List<Component> getAllComponents(final Container c) {
        Component[] comps = c.getComponents();
        List<Component> compList = new ArrayList<Component>();
        for (Component comp : comps) {
            compList.add(comp);
            if (comp instanceof Container) {
                compList.addAll(getAllComponents((Container) comp));
            }
        }
        return compList;
    }

    public static ArrayList<String> GetListAtasanFromKodeBagian(String kode_bagian) throws Exception {
        ArrayList<String> Atasan = new ArrayList<>();

        String sql = "SELECT `kode_bagian`, `kepala_bagian` FROM `tb_bagian` WHERE `status_bagian` = 1";
        Connection connection = Utility.db.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet rs = preparedStatement.executeQuery();
        Map<String, String> DataBagian = new HashMap<>();
        while (rs.next()) {
            DataBagian.put(rs.getString("kode_bagian"), rs.getString("kepala_bagian"));
        }
        // Loop until the value is null
        String currentKey = kode_bagian;
        while (currentKey != null) {
            String kode_atasan = DataBagian.get(currentKey);
            if (kode_atasan != null) {
                Atasan.add(kode_atasan);
            }
            currentKey = kode_atasan;
        }
        return Atasan;
    }

    public static ArrayList<String> GetBawahan1Tingkat(Map<String, String> DataBagian, String kode_bagian) {
        ArrayList<String> Bawahan = new ArrayList<>();
        for (String kode_bawahan : DataBagian.keySet()) {
            if (kode_bagian.equals(DataBagian.get(kode_bawahan))) {
                Bawahan.add(kode_bawahan);
                Bawahan.addAll(GetBawahan1Tingkat(DataBagian, kode_bawahan));
            }
        }
        return Bawahan;
    }

    public static ArrayList<String> GetListBawahanFromKodeBagian(String kode_bagian) throws Exception {
        String sql = "SELECT `kode_bagian`, `kepala_bagian` FROM `tb_bagian` WHERE `status_bagian` = 1";
        Connection connection = Utility.db.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet rs = preparedStatement.executeQuery();
        Map<String, String> DataBagian = new HashMap<>();
        while (rs.next()) {
            DataBagian.put(rs.getString("kode_bagian"), rs.getString("kepala_bagian"));
        }

        return GetBawahan1Tingkat(DataBagian, kode_bagian);
    }

    public static String getDownloadFolderPath() {
        String userProfile = System.getProperty("user.home");
        String downloadFolder = "Downloads";
        return userProfile + File.separator + downloadFolder;
    }

    public static void generateQRCodeImage(String QR_CODE_IMAGE_PATH, String text, int width, int height) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

            java.nio.file.Path path = FileSystems.getDefault().getPath(QR_CODE_IMAGE_PATH);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
            System.out.println("QR Code berhasil dibuat di: " + QR_CODE_IMAGE_PATH);
        } catch (WriterException e) {
            System.err.println("Gagal membuat QR Code: " + e.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    static class ImageSelection implements Transferable {

        private Image image;

        public ImageSelection(Image image) {
            this.image = image;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.imageFlavor};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return DataFlavor.imageFlavor.equals(flavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (DataFlavor.imageFlavor.equals(flavor)) {
                return image;
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }
    }

    public static void copyImageToClipboard(String imagePath) {
        try {
            File imageFile = new File(imagePath);
            BufferedImage image = ImageIO.read(imageFile);

            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            ImageSelection imageSelection = new ImageSelection(image);
            clipboard.setContents(imageSelection, null);
        } catch (Exception e) {
            System.err.println("Gagal menyalin gambar ke clipboard: " + e.getMessage());
        }
    }

    public static void uploadFileToServer(File sourceFile, String serverURL) throws IOException {
        // Create URL object
        URL url = new URL(serverURL);

        // Open HTTP connection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        // Create output stream to send data
        try (OutputStream outputStream = connection.getOutputStream();
                FileInputStream inputStream = new FileInputStream(sourceFile)) {

            // Read data from file and write to output stream
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        }

        // Check response code
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            System.out.println("File uploaded successfully.");
        } else {
            throw new IOException("Failed to upload file. Response code: " + responseCode);
        }
    }

    public static void uploadFileToServer(File sourceFile, String serverAddress, String directoryPath) throws IOException {
        // Construct URI for server address
        URI serverURI = URI.create("smb://" + serverAddress);

        // Convert directory path string to Path
        Path serverDirectoryPath = Paths.get(serverURI.getPath() + directoryPath);

        // Check if the directory exists, if not, create it
        if (!Files.exists(serverDirectoryPath)) {
            Files.createDirectories(serverDirectoryPath);
        }

        // Get the file name
        String fileName = sourceFile.getName();

        // Define the destination path on the server
        Path destinationPath = serverDirectoryPath.resolve(fileName);

        // Copy the file to the destination directory on the server
        Files.copy(sourceFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
    }

    public static String getFileExtension(File file) {
        String extension = null;
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            extension = fileName.substring(dotIndex + 1).toLowerCase();
        }
        return extension;
    }

    public static String uploadFileWithPHP(String filePath, String targetDirectory, String newFileName) throws IOException {
        String targetURL = "http://192.168.10.2:5050/waleta/images/upload.php";

        // Create a URL object with the target URL
        URL url = new URL(targetURL);

        // Open a connection to the URL
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set request method to POST
        connection.setRequestMethod("POST");

        // Set Content-Type header
        connection.setRequestProperty("Content-Type", "multipart/form-data");

        // Enable output stream
        connection.setDoOutput(true);

        // Create a file object
        File file = new File(filePath);

        // Get file name
        String fileName = newFileName != null ? newFileName : file.getName();

        // Create boundary
        String boundary = Long.toHexString(System.currentTimeMillis());

        // Set request headers
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        connection.setRequestProperty("Content-Disposition", "form-data; name=\"fileToUpload\"; filename=\"" + fileName + "\"");
        connection.setRequestProperty("X-Target-Dir", targetDirectory); // Set custom header for target directory

        // Open streams
        try (
                OutputStream output = connection.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true);
                FileInputStream fileInputStream = new FileInputStream(file);) {
            // Write boundary
            writer.append("--" + boundary).append("\r\n");
            // Write content disposition
            writer.append("Content-Disposition: form-data; name=\"fileToUpload\"; filename=\"" + fileName + "\"").append("\r\n");
            // Write content type
            writer.append("Content-Type: " + Files.probeContentType(Paths.get(filePath))).append("\r\n");
            // Write empty line
            writer.append("\r\n");
            // Flush writer
            writer.flush();

            // Write file content
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            // Flush output
            output.flush();

            // Write boundary end
            writer.append("\r\n").append("--" + boundary + "--").append("\r\n");
        }

        // Get the response from the server
        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        // Read the response message
        String ResponseMessage = "";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            // Print the response message
            System.out.println("Response: " + response.toString());
            ResponseMessage = response.toString();
        }

        // Disconnect the connection
        connection.disconnect();
        return ResponseMessage;
    }

    public static void main(String[] args) {
        try {
            Utility.db.connect();
            for (int i = 0; i < 10; i++) {
                System.out.println(i);
                try {
                    // Add a delay of 1 second (1000 milliseconds)
                    Thread.sleep(1000); // You can adjust the delay time as needed
                } catch (InterruptedException e) {
                    // Handle the InterruptedException if needed
                    e.printStackTrace();
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
