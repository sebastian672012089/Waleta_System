package waleta_system.RND;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import waleta_system.SoundJLayer;

public class JFrame_Timer extends javax.swing.JFrame {

    String sql = null, sql_rekap = null;
    ResultSet rs;
    PreparedStatement pst;
    Date today = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    static Timer t;
    private int main_timer;
    private int sound_timer;
    private SoundJLayer soundEffect;
    private Thread tSoundEffect;

    public JFrame_Timer() {
        initComponents();
        playSound("Report\\Sound\\timer-cuci-start-sound.mp3", false);
        init();
    }

    public void init() {
        try {
            main_timer = 0;
            sound_timer = 0;
            TimerTask timer;
            timer = new TimerTask() {
                @Override
                public void run() {
                    System.out.println("main_timer : " + main_timer + " - sound_timer : " + sound_timer);
                    if (main_timer >= 90) {
                        label_time.setForeground(Color.white);
                        jPanel1.setBackground(Color.red);
                        switch (sound_timer) {
                            case 0:
                                //Playing sound
                                playSound("Report\\Sound\\digital-alarm.mp3", true);
                                sound_timer++;
                                break;
                            case 8:
                                label_time.setForeground(Color.black);
                                jPanel1.setBackground(Color.white);
                                main_timer = 0;
                                sound_timer = 0;
                                playSound("Report\\Sound\\timer-cuci-start-sound.mp3", false);
                                break;
                            default:
                                sound_timer++;
                                break;
                        }
                        label_time.setText(Integer.toString(sound_timer));
                    } else {
                        label_time.setForeground(Color.black);
                        jPanel1.setBackground(Color.white);
                        label_time.setText(Integer.toString(main_timer));
                    }
                    main_timer++;
                }
            };
            t = new Timer();
            t.schedule(timer, 0, 1000);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JFrame_Timer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void playSound(String soundFile, boolean loop) {
        try {
            tSoundEffect = new Thread(new Runnable() {
                @Override
                public void run() {
                    soundEffect = new SoundJLayer(soundFile, loop);
                    soundEffect.play();
                }
            });
            tSoundEffect.start();

//            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundFile).getAbsoluteFile());
//            Clip clip = AudioSystem.getClip();
//            clip.open(audioInputStream);
//            clip.start();
            // Timer to stop the sound after sound_time seconds
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
//                    clip.stop();
//                    clip.close();
                    soundEffect.close();
                    if (tSoundEffect != null) {
                        tSoundEffect.stop();
                        tSoundEffect = null;
                    }
                }
            }, 7500);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        label_time = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
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
        t.cancel();
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
            java.util.logging.Logger.getLogger(JFrame_Timer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrame_Timer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrame_Timer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame_Timer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame_Timer frame = new JFrame_Timer();
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
