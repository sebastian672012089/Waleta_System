package waleta_system.Class;

import com.fazecast.jSerialComm.SerialPort;

public class SerialReader {

    public static void main(String[] args) {
        // Mendapatkan daftar port yang tersedia
        SerialPort[] ports = SerialPort.getCommPorts();
        
        // Mencoba membaca data dari setiap port
        for (SerialPort port : ports) {
            System.out.println("Mencoba port: " + port.getSystemPortName());
            
            // Mengatur parameter komunikasi serial
            port.setComPortParameters(9600, 8, 1, 0); // Sesuaikan dengan spesifikasi timbangan Anda
            port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);

            // Membuka port
            if (port.openPort()) {
                System.out.println("Port " + port.getSystemPortName() + " terbuka.");

                // Membaca data dari timbangan
                try (java.io.InputStream in = port.getInputStream()) {
                    byte[] buffer = new byte[1024];
                    int len = in.read(buffer);
                    
                    if (len > 0) {
                        String data = new String(buffer, 0, len);
                        System.out.println("Data dari " + port.getSystemPortName() + ": " + data);
                    } else {
                        System.out.println("Tidak ada data dari " + port.getSystemPortName());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    port.closePort();
                }
            } else {
                System.out.println("Gagal membuka port " + port.getSystemPortName());
            }
        }
    }
}
