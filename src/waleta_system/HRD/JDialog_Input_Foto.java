package waleta_system.HRD;

import java.io.File;
import waleta_system.Class.Utility;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
//import static waleta_system.Class.Utility.ROTATE_RIGHT;
//import static waleta_system.Class.Utility.rotate90;

public class JDialog_Input_Foto extends javax.swing.JDialog {

    PreparedStatement pst;
    String imgPath = null;
    String id = null;
    ResultSet rs;

    public JDialog_Input_Foto(java.awt.Frame parent, boolean modal, String id, String nama) {
        super(parent, modal);
        initComponents();
        button_rotate_left.setIcon(Utility.ResizeImageIcon(new ImageIcon(getClass().getResource("/waleta_system/Images/rotate_page_counterclockwise_50px.png")), button_rotate_left.getWidth(), button_rotate_left.getHeight()));
        button_rotate_right.setIcon(Utility.ResizeImageIcon(new ImageIcon(getClass().getResource("/waleta_system/Images/rotate_page_clockwise_50px.png")), button_rotate_right.getWidth(), button_rotate_right.getHeight()));
        label_id.setText(id);
        label_nama.setText(nama);
        lbl_Image.setIcon(Utility.ResizeImage("\\\\192.168.10.2\\Shared Folder\\DOKUMEN SISTEM\\FOTO_ID_CARD\\" + id + ".JPG", null, lbl_Image.getWidth(), lbl_Image.getHeight()));
        this.id = id;
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        Jlabel1 = new javax.swing.JLabel();
        Jlabel2 = new javax.swing.JLabel();
        Jlabel3 = new javax.swing.JLabel();
        label_nama = new javax.swing.JLabel();
        label_id = new javax.swing.JLabel();
        button_rotate_left = new javax.swing.JButton();
        button_rotate_right = new javax.swing.JButton();
        lbl_Image = new javax.swing.JLabel();
        button_browse_ktp = new javax.swing.JButton();
        button_proceed_insert = new javax.swing.JButton();
        label_ktp_location = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        Jlabel1.setBackground(new java.awt.Color(255, 255, 255));
        Jlabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        Jlabel1.setText("Input Foto");
        Jlabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        Jlabel1.setMaximumSize(new java.awt.Dimension(280, 15));

        Jlabel2.setBackground(new java.awt.Color(255, 255, 255));
        Jlabel2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Jlabel2.setText("Nama : ");
        Jlabel2.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        Jlabel2.setMaximumSize(new java.awt.Dimension(280, 15));

        Jlabel3.setBackground(new java.awt.Color(255, 255, 255));
        Jlabel3.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Jlabel3.setText("ID Pegawai :");
        Jlabel3.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        Jlabel3.setMaximumSize(new java.awt.Dimension(280, 15));

        label_nama.setBackground(new java.awt.Color(255, 255, 255));
        label_nama.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_nama.setText("Nama");
        label_nama.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        label_nama.setMaximumSize(new java.awt.Dimension(280, 15));

        label_id.setBackground(new java.awt.Color(255, 255, 255));
        label_id.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_id.setText("ID");
        label_id.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        label_id.setMaximumSize(new java.awt.Dimension(280, 15));

        button_rotate_left.setBackground(new java.awt.Color(255, 255, 255));
        button_rotate_left.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_rotate_left.setToolTipText("Rotate Left");
        button_rotate_left.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_rotate_leftActionPerformed(evt);
            }
        });

        button_rotate_right.setBackground(new java.awt.Color(255, 255, 255));
        button_rotate_right.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_rotate_right.setToolTipText("Rotate Right");
        button_rotate_right.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_rotate_rightActionPerformed(evt);
            }
        });

        lbl_Image.setBackground(new java.awt.Color(255, 255, 255));
        lbl_Image.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_Image.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        button_browse_ktp.setBackground(new java.awt.Color(255, 255, 255));
        button_browse_ktp.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        button_browse_ktp.setText("Browse Scan KTP");
        button_browse_ktp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_browse_ktpActionPerformed(evt);
            }
        });

        button_proceed_insert.setBackground(new java.awt.Color(255, 255, 255));
        button_proceed_insert.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        button_proceed_insert.setText("Save");
        button_proceed_insert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_proceed_insertActionPerformed(evt);
            }
        });

        label_ktp_location.setBackground(new java.awt.Color(255, 255, 255));
        label_ktp_location.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        label_ktp_location.setText("Image Location");
        label_ktp_location.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        label_ktp_location.setMaximumSize(new java.awt.Dimension(280, 15));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_Image, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(Jlabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_rotate_left, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_rotate_right, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Jlabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(Jlabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_nama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(label_ktp_location, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_browse_ktp)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_proceed_insert)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Jlabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Jlabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_nama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(button_rotate_right, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button_rotate_left, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(Jlabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_Image, javax.swing.GroupLayout.DEFAULT_SIZE, 406, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_ktp_location, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_browse_ktp)
                    .addComponent(button_proceed_insert))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button_browse_ktpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_browse_ktpActionPerformed
        // TODO add your handling code here:
        JOptionPane.showMessageDialog(this, "Silahkan copy foto ke dalam folder '192.168.10.2/Shared Folder/DOKUMEN SISTEM/FOTO_ID_CARD' !\nDengan nama file ID PEGAWAI masing2");
//        JFileChooser file = new JFileChooser();
//        file.setCurrentDirectory(new File(System.getProperty("user.home")));
//        //filter files extension
//        FileNameExtensionFilter filter = new FileNameExtensionFilter("JPEG file", "jpg", "jpeg", "png");
//        file.addChoosableFileFilter(filter);
//        int result = file.showSaveDialog(null);
//        if (result == JFileChooser.APPROVE_OPTION) {
//            File selectedFile = file.getSelectedFile();
//            String path = selectedFile.getAbsolutePath();
//            lbl_Image.setIcon(Utility.ResizeImage(path, null, lbl_Image.getWidth(), lbl_Image.getHeight()));
//            label_ktp_location.setText(path);
//            imgPath = path;
//        } else if (result == JFileChooser.CANCEL_OPTION) {
//            System.out.println("NO FILE SELECTED !");
//        }
    }//GEN-LAST:event_button_browse_ktpActionPerformed

    private void button_proceed_insertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_proceed_insertActionPerformed
        JOptionPane.showMessageDialog(this, "Silahkan copy foto ke dalam folder '192.168.10.2/Shared Folder/FOTO_ID_CARD' !\nDengan nama file ID PEGAWAI masing2");
//        if (imgPath != null) {
//            try {
//                InputStream img = new FileInputStream(new File(imgPath));
//                String query = "UPDATE `tb_karyawan` SET `foto`=? WHERE `id_pegawai` = '" + id + "'";
//                pst = Utility.db.getConnection().prepareStatement(query);
//                pst.setBlob(1, img);
//                pst.executeUpdate();
//                img.close();
//                JOptionPane.showMessageDialog(this, "Sukses");
//                this.dispose();
//            } catch (SQLException | FileNotFoundException ex) {
//                Logger.getLogger(JDialog_Input_Foto.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (IOException ex) {
//                Logger.getLogger(JDialog_Input_Foto.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        } else {
//            JOptionPane.showMessageDialog(this, "Anda belum memilih Foto yang akan dimasukkan !");
//        }
    }//GEN-LAST:event_button_proceed_insertActionPerformed

    private void button_rotate_rightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_rotate_rightActionPerformed
        // TODO add your handling code here:
        File input = new File("\\\\192.168.10.2\\Shared Folder\\DOKUMEN SISTEM\\FOTO_ID_CARD\\" + id + ".JPG");
        File output = new File("\\\\192.168.10.2\\Shared Folder\\DOKUMEN SISTEM\\FOTO_ID_CARD\\" + id + ".JPG");
        Utility.rotate90(input, output, Utility.ROTATE_RIGHT);
        lbl_Image.setIcon(Utility.ResizeImage("\\\\192.168.10.2\\Shared Folder\\DOKUMEN SISTEM\\FOTO_ID_CARD\\" + id + ".JPG", null, lbl_Image.getWidth(), lbl_Image.getHeight()));
    }//GEN-LAST:event_button_rotate_rightActionPerformed

    private void button_rotate_leftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_rotate_leftActionPerformed
        // TODO add your handling code here:
        File input = new File("\\\\192.168.10.2\\Shared Folder\\DOKUMEN SISTEM\\FOTO_ID_CARD\\" + id + ".JPG");
        File output = new File("\\\\192.168.10.2\\Shared Folder\\DOKUMEN SISTEM\\FOTO_ID_CARD\\" + id + ".JPG");
        Utility.rotate90(input, output, Utility.ROTATE_LEFT);
        lbl_Image.setIcon(Utility.ResizeImage("\\\\192.168.10.2\\Shared Folder\\DOKUMEN SISTEM\\FOTO_ID_CARD\\" + id + ".JPG", null, lbl_Image.getWidth(), lbl_Image.getHeight()));
    }//GEN-LAST:event_button_rotate_leftActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Jlabel1;
    private javax.swing.JLabel Jlabel2;
    private javax.swing.JLabel Jlabel3;
    private javax.swing.JButton button_browse_ktp;
    private javax.swing.JButton button_proceed_insert;
    private javax.swing.JButton button_rotate_left;
    private javax.swing.JButton button_rotate_right;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel label_id;
    private javax.swing.JLabel label_ktp_location;
    private javax.swing.JLabel label_nama;
    private javax.swing.JLabel lbl_Image;
    // End of variables declaration//GEN-END:variables
}
