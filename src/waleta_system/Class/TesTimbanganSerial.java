package waleta_system.Class;

import com.fazecast.jSerialComm.SerialPort;

public class TesTimbanganSerial {

    public static void main(String[] args) {
        // Mendapatkan daftar port yang tersedia
        SerialPort[] ports = SerialPort.getCommPorts();

        // Mencetak daftar port
        System.out.println("Daftar port yang tersedia:");
        for (int i = 0; i < ports.length; i++) {
            System.out.println("[" + i + "] " + ports[i].getSystemPortName() + " - " + ports[i].getDescriptivePortName());
        }

        // Menentukan port serial yang digunakan (ubah sesuai port yang Anda gunakan)
        SerialPort comPort = SerialPort.getCommPorts()[9]; // Misal menggunakan port pertama dalam daftar

        // Mengatur parameter komunikasi serial
        comPort.setComPortParameters(9600, 8, 1, 0); // Baud rate, Data bits, Stop bits, Parity
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);

        // Membuka port
        if (comPort.openPort()) {
            System.out.println("Port " + comPort.getSystemPortName() + " terbuka.");

            // Membuat thread untuk membaca data secara asynchronous
            Thread thread = new Thread(() -> {
                try {
                    // Membaca data dari timbangan
                    java.io.InputStream in = comPort.getInputStream();
                    byte[] buffer = new byte[1024];
                    int nextChar;

                    StringBuilder sb = new StringBuilder();

//                    while ((len = in.read(buffer)) > -1) {
                    while ((nextChar = in.read()) != -1) {
//                        String data1 = new String(buffer, 0, nextChar);
//                        System.out.print(data1);
                        char c = (char) nextChar;
                        if (c == '\n' || c == '\r') {
                            if (sb.length() > 0) {
                                String data = sb.toString().trim();
                                System.out.println("Data dari " + comPort.getSystemPortName() + ": " + data);

                                // Memproses string untuk mendapatkan angka saja
                                String weightString = data.replaceAll("[^0-9.]", "").trim();

                                // Mengonversi string menjadi double
                                try {
                                    double weight = Double.parseDouble(weightString);
                                    System.out.println("Berat dalam kg: " + weight);
                                } catch (NumberFormatException e) {
                                    System.out.println("Gagal mengonversi data menjadi angka.");
                                }

                                sb.setLength(0); // Clear the StringBuilder for the next message
                            }
                        } else {
                            sb.append(c);
                        }
                    }

                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            thread.start();
        } else {
            System.out.println("Gagal membuka port.");
        }

        // Menutup port ketika aplikasi ditutup
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (comPort.isOpen()) {
                comPort.closePort();
                System.out.println("Port ditutup.");
            }
        }));
    }
}
