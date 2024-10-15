package waleta_system.RND;

import com.fazecast.jSerialComm.SerialPort;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import javax.swing.JFrame;

public class JFrame_TimbanganDigital extends javax.swing.JFrame {

    String sql = null, sql_rekap = null;
    ResultSet rs;
    PreparedStatement pst;
    Date today = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    static Timer t;

    public JFrame_TimbanganDigital() {
        initComponents();
        init();
    }

    public void init() {
        // Mendapatkan daftar port yang tersedia
        SerialPort[] ports = SerialPort.getCommPorts();

        // Mencetak daftar port
        System.out.println("Daftar port yang tersedia:");
        for (int i = 0; i < ports.length; i++) {
            System.out.println("[" + i + "] " + ports[i].getSystemPortName() + " - " + ports[i].getDescriptivePortName());
        }

        // Menentukan port serial yang digunakan (ubah sesuai port yang Anda gunakan)
        SerialPort comPort = SerialPort.getCommPorts()[4]; // Misal menggunakan port pertama dalam daftar

        // Mengatur parameter komunikasi serial
        comPort.setComPortParameters(9600, 8, 1, 0); // Baud rate, Data bits, Stop bits, Parity
        comPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
//        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 5000, 0);
//        comPort.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 1000, 0);

        // Membuka port
        if (comPort.openPort()) {
            System.out.println("Port " + comPort.getSystemPortName() + " terbuka.");
            System.out.println("Menunggu data dari port: " + comPort.getSystemPortName());

            // Membuat thread untuk membaca data secara asynchronous
            Thread thread = new Thread(() -> {
                try {
                    // Membaca data dari timbangan
                    java.io.InputStream in = comPort.getInputStream();
                    byte[] buffer = new byte[1024];
                    int nextChar;

                    StringBuilder sb = new StringBuilder();

                    System.out.println("Memulai pembacaan data...");
                    while ((nextChar = in.read()) != -1) {
                        char c = (char) nextChar;
                        if (c == '\n' || c == '\r') {
                            if (sb.length() > 0) {
                                String data = sb.toString().trim();
                                System.out.println("Data dari " + comPort.getSystemPortName() + ": " + data);

//                                // Memproses string untuk mendapatkan angka saja
//                                String weightString = data.replaceAll("[^0-9.-]", "").trim();
//
//                                // Mengonversi string menjadi double
//                                try {
//                                    label_time.setText(weightString + " g");
//                                    double weight = Double.parseDouble(weightString);
//                                    System.out.println("Berat dalam kg: " + weight);
//                                } catch (NumberFormatException e) {
//                                    System.out.println("Gagal mengonversi data menjadi angka.");
//                                }
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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        label_time = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        label_time.setBackground(new java.awt.Color(255, 255, 255));
        label_time.setFont(new java.awt.Font("Arial", 1, 300)); // NOI18N
        label_time.setForeground(new java.awt.Color(255, 0, 0));
        label_time.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label_time.setText("0");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_time, javax.swing.GroupLayout.DEFAULT_SIZE, 1346, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_time, javax.swing.GroupLayout.DEFAULT_SIZE, 698, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowClosing

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JFrame_TimbanganDigital.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrame_TimbanganDigital.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrame_TimbanganDigital.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame_TimbanganDigital.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame_TimbanganDigital frame = new JFrame_TimbanganDigital();
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                frame.setEnabled(true);
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel label_time;
    // End of variables declaration//GEN-END:variables
}
