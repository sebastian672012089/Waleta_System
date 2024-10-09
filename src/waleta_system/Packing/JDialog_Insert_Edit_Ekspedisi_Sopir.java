package waleta_system.Packing;

import waleta_system.Class.Utility;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class JDialog_Insert_Edit_Ekspedisi_Sopir extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String imgPath = null;
    Date today = new Date();
    String id_sopir = null;

    public JDialog_Insert_Edit_Ekspedisi_Sopir(java.awt.Frame parent, boolean modal, String id_sopir) {
        super(parent, modal);
        initComponents();
        this.setResizable(false);

        try {
            ComboBox_ekspedisi.removeAllItems();
            sql = "SELECT `id_ekspedisi` FROM `tb_ekspedisi` WHERE 1";
            pst = Utility.db.getConnection().prepareStatement(sql);
            ResultSet result = pst.executeQuery();
            while (result.next()) {
                ComboBox_ekspedisi.addItem(result.getString("id_ekspedisi"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JDialog_Insert_Edit_Ekspedisi_Sopir.class.getName()).log(Level.SEVERE, null, e);
        }

        if (id_sopir != null) {
            this.id_sopir = id_sopir;
            getEditData(id_sopir);
            txt_id_sopir.setText(id_sopir);
        }
    }

    public void insert() {
        try {
            if (imgPath == null || imgPath.equals("")) {
                sql = "INSERT INTO `tb_ekspedisi_sopir`(`id_sopir`, `nama`, `no_hp`, `foto`, `id_ekspedisi`) "
                        + "VALUES ("
                        + "'" + txt_id_sopir.getText() + "',"
                        + "'" + txt_nama.getText() + "',"
                        + "'" + txt_no_telp.getText() + "',"
                        + "0,"
                        + "'" + ComboBox_ekspedisi.getSelectedItem().toString() + "'"
                        + ")";
            } else {
                sql = "INSERT INTO `tb_ekspedisi_sopir`(`id_sopir`, `nama`, `no_hp`, `foto`, `id_ekspedisi`) "
                        + "VALUES ("
                        + "'" + txt_id_sopir.getText() + "',"
                        + "'" + txt_nama.getText() + "',"
                        + "'" + txt_no_telp.getText() + "',"
                        + "1,"
                        + "'" + ComboBox_ekspedisi.getSelectedItem().toString() + "'"
                        + ")";
            }

            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                if (imgPath != null && !imgPath.equals("")) {
                    String response = Utility.uploadFileWithPHP(imgPath, "tb_ekspedisi_sopir_foto/", txt_id_sopir.getText() + ".jpg");
                    JOptionPane.showMessageDialog(this, "Data tersimpan, " + response);
                } else {
                    JOptionPane.showMessageDialog(this, "Data tersimpan, tanpa foto!");
                }
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "FAILED!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JDialog_Insert_Edit_Ekspedisi_Sopir.class.getName()).log(Level.SEVERE, null, e);
        }
        imgPath = null;
    }

    public void update() {
        try {
            String update_foto = "`foto` = 1, ";
            if (imgPath == null || imgPath.equals("")) {
                update_foto = "`foto` = 0, ";
            }
            if (!txt_id_sopir.getText().equals(id_sopir)) {
                String oldFileName = id_sopir + ".jpg";
                String newFileName = txt_id_sopir.getText() + ".jpg";
                String directory = "./tb_ekspedisi_sopir_foto/";
                Map<String, Object> rename_result = Utility.renameFileOnServer(oldFileName, newFileName, directory);
            }
            sql = "UPDATE `tb_ekspedisi_sopir` SET "
                    + "`id_sopir`='" + txt_id_sopir.getText() + "',"
                    + "`nama`='" + txt_nama.getText() + "',"
                    + "`no_hp`='" + txt_no_telp.getText() + "',"
                    + update_foto
                    + "`id_ekspedisi`='" + ComboBox_ekspedisi.getSelectedItem() + "' "
                    + "WHERE `id_sopir` = '" + id_sopir + "'";
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                if (imgPath != null && !imgPath.equals("")) {
                    String response = Utility.uploadFileWithPHP(imgPath, "tb_ekspedisi_sopir_foto/", txt_id_sopir.getText() + ".jpg");
                    JOptionPane.showMessageDialog(this, "Data tersimpan, " + response);
                } else {
                    JOptionPane.showMessageDialog(this, "Data tersimpan, tanpa perubahan foto!");
                }
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "FAILED!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JDialog_Insert_Edit_Ekspedisi_Sopir.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void getEditData(String id_sopir) {
        try {
            String query = "SELECT `id_sopir`, `nama`, `no_hp`, `foto`, `id_ekspedisi` "
                    + "FROM `tb_ekspedisi_sopir` "
                    + "WHERE `id_sopir`='" + id_sopir + "'";
            pst = Utility.db.getConnection().prepareStatement(query);
            ResultSet result = pst.executeQuery();
            if (result.next()) {
                txt_id_sopir.setText(result.getString("id_sopir"));
                txt_nama.setText(result.getString("nama"));
                txt_no_telp.setText(result.getString("no_hp"));
                ComboBox_ekspedisi.setSelectedItem(result.getString("id_ekspedisi"));
                String img_path = "http://192.168.10.2:5050/waleta/images/tb_ekspedisi_sopir_foto/" + result.getString("id_sopir") + ".jpg";
                ImageIcon image = Utility.getAndResizeImageFromURL(img_path, lbl_Image.getWidth(), lbl_Image.getHeight());
                lbl_Image.setIcon(image);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JDialog_Insert_Edit_Ekspedisi_Sopir.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jlabel1 = new javax.swing.JLabel();
        label_img_location = new javax.swing.JLabel();
        button_browse_image = new javax.swing.JButton();
        lbl_Image = new javax.swing.JLabel();
        button_cancel = new javax.swing.JButton();
        button_simpan = new javax.swing.JButton();
        txt_nama = new javax.swing.JTextField();
        txt_id_sopir = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txt_no_telp = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        ComboBox_ekspedisi = new javax.swing.JComboBox<>();
        jLabel28 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Data Karyawan");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jlabel1.setBackground(new java.awt.Color(255, 255, 255));
        jlabel1.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jlabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlabel1.setText("Data Sopir");

        label_img_location.setBackground(new java.awt.Color(255, 255, 255));
        label_img_location.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        label_img_location.setText("Image Location");
        label_img_location.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        label_img_location.setMaximumSize(new java.awt.Dimension(280, 15));

        button_browse_image.setBackground(new java.awt.Color(255, 255, 255));
        button_browse_image.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_browse_image.setText("Browse Image");
        button_browse_image.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_browse_imageActionPerformed(evt);
            }
        });

        lbl_Image.setBackground(new java.awt.Color(255, 255, 255));
        lbl_Image.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_Image.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        button_cancel.setBackground(new java.awt.Color(255, 255, 255));
        button_cancel.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_cancel.setText("Batal");
        button_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_cancelActionPerformed(evt);
            }
        });

        button_simpan.setBackground(new java.awt.Color(255, 255, 255));
        button_simpan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_simpan.setText("Simpan");
        button_simpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_simpanActionPerformed(evt);
            }
        });

        txt_nama.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N

        txt_id_sopir.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel4.setText("Ekspedisi :");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel3.setText("Nama Lengkap :");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel2.setText("ID Sopir :");

        txt_no_telp.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel27.setText("No Telp :");

        ComboBox_ekspedisi.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        jLabel28.setText("Foto :");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label_img_location, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(button_cancel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_simpan)
                .addContainerGap())
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(lbl_Image, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_browse_image)
                            .addComponent(ComboBox_ekspedisi, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_no_telp, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_id_sopir, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jlabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_id_sopir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_nama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_ekspedisi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_no_telp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_browse_image)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_Image, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_img_location, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_simpan)
                    .addComponent(button_cancel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button_simpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_simpanActionPerformed
        if (id_sopir == null) {
            insert();
        } else {
            update();
        }
    }//GEN-LAST:event_button_simpanActionPerformed

    private void button_browse_imageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_browse_imageActionPerformed
        // TODO add your handling code here:
        JFileChooser file = new JFileChooser();
        file.setCurrentDirectory(new File(System.getProperty("user.home")));
        //filter files extension
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JPEG file", "jpg", "jpeg", "png");
        file.addChoosableFileFilter(filter);
        int result = file.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = file.getSelectedFile();
            String path = selectedFile.getAbsolutePath();
            // Check the file extension
            String extension = Utility.getFileExtension(selectedFile);
            if (extension != null && (extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png"))) {
                // File has a valid extension, proceed
                lbl_Image.setIcon(Utility.ResizeImage(path, null, lbl_Image.getWidth(), lbl_Image.getHeight()));
                label_img_location.setText(path);
                imgPath = path;
                System.out.println("Selected file path: " + imgPath);
            } else {
                // Invalid file extension
                System.out.println("Invalid file selected. Please select a JPG, JPEG, or PNG file.");
            }
        } else if (result == JFileChooser.CANCEL_OPTION) {
            System.out.println("NO FILE SELECTED !");
        }
    }//GEN-LAST:event_button_browse_imageActionPerformed

    private void button_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cancelActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_button_cancelActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDialog_pengiriman_PickUp_insert_edit.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JDialog_Insert_Edit_Ekspedisi_Sopir dialog = new JDialog_Insert_Edit_Ekspedisi_Sopir(new javax.swing.JFrame(), true, null);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JComboBox<String> ComboBox_ekspedisi;
    private javax.swing.JButton button_browse_image;
    private javax.swing.JButton button_cancel;
    private javax.swing.JButton button_simpan;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel2;
    public javax.swing.JLabel jlabel1;
    private javax.swing.JLabel label_img_location;
    private javax.swing.JLabel lbl_Image;
    public static javax.swing.JTextField txt_id_sopir;
    public static javax.swing.JTextField txt_nama;
    public static javax.swing.JTextField txt_no_telp;
    // End of variables declaration//GEN-END:variables
}
