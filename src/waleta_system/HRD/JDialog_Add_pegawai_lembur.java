package waleta_system.HRD;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import waleta_system.Browse_Karyawan;
import waleta_system.Class.ColumnsAutoSizer;

public class JDialog_Add_pegawai_lembur extends javax.swing.JDialog {

    String departemen = "";

    public JDialog_Add_pegawai_lembur(java.awt.Frame parent, boolean modal, String departemen, String jenis_spl) {
        super(parent, modal);
        initComponents();
        this.departemen = departemen;
        label_jenis_spl.setText(jenis_spl);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        button_select_karyawan = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        label_nama = new javax.swing.JLabel();
        label_id = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        label_bagian = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        label_departemen = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        Spinner_jam = new javax.swing.JSpinner();
        Spinner_menit = new javax.swing.JSpinner();
        jLabel11 = new javax.swing.JLabel();
        button_add = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        Spinner_jam1 = new javax.swing.JSpinner();
        Spinner_menit1 = new javax.swing.JSpinner();
        jLabel15 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        label_jenis_spl = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        ComboBox_istirahat = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add Pegawai Lembur");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        button_select_karyawan.setBackground(new java.awt.Color(255, 255, 255));
        button_select_karyawan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_select_karyawan.setText("Pilih Karyawan");
        button_select_karyawan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_select_karyawanActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Nama Lengkap :");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("ID Pegawai :");

        label_nama.setBackground(new java.awt.Color(255, 255, 255));
        label_nama.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_nama.setText("-");

        label_id.setBackground(new java.awt.Color(255, 255, 255));
        label_id.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_id.setText("-");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Bagian :");

        label_bagian.setBackground(new java.awt.Color(255, 255, 255));
        label_bagian.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_bagian.setText("-");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Departemen :");

        label_departemen.setBackground(new java.awt.Color(255, 255, 255));
        label_departemen.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_departemen.setText("-");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_id, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_departemen, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(label_id))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(label_nama))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(label_bagian))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(label_departemen))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("Select Pegawai :");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Jam Mulai Lembur :");

        Spinner_jam.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Spinner_jam.setModel(new javax.swing.SpinnerNumberModel(0, 0, 23, 1));
        Spinner_jam.setEditor(new javax.swing.JSpinner.NumberEditor(Spinner_jam, ""));

        Spinner_menit.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Spinner_menit.setModel(new javax.swing.SpinnerListModel(new String[] {"00", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55"}));

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText(":");

        button_add.setBackground(new java.awt.Color(255, 255, 255));
        button_add.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_add.setText("Add");
        button_add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_addActionPerformed(evt);
            }
        });

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Jam Selesai Lembur :");

        Spinner_jam1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Spinner_jam1.setModel(new javax.swing.SpinnerNumberModel(0, 0, 23, 1));
        Spinner_jam1.setEditor(new javax.swing.JSpinner.NumberEditor(Spinner_jam1, ""));

        Spinner_menit1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Spinner_menit1.setModel(new javax.swing.SpinnerListModel(new String[] {"00", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55"}));

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText(":");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 2, 10)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(102, 102, 102));
        jLabel12.setText("Hanya bisa memilih karyawan pada departemen yang sama");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Jenis SPL :");

        label_jenis_spl.setBackground(new java.awt.Color(255, 255, 255));
        label_jenis_spl.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_jenis_spl.setText("PEJUANG");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("Menit Istirahat :");

        ComboBox_istirahat.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_istirahat.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0 (Tidak ada istirahat)", "15 Menit", "30 Menit", "45 Menit", "60 Menit (1 Jam)", "75 Menit (1 Jam 15 Menit)", "90 Menit (1 Jam 30 Menit)", "105 Menit (1 Jam 45 Menit)", "120 Menit (2 Jam)", "135 Menit (2 Jam 15 Menit)", "150 Menit (2 Jam 30 Menit)", "165 Menit (2 Jam 45 Menit)", "180 Menit (3 Jam)", "195 Menit (3 Jam 15 Menit)", "210 Menit (3 Jam 30 Menit)", "225 Menit (3 Jam 45 Menit)", "240 Menit (4 Jam)" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_select_karyawan))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Spinner_jam, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Spinner_menit, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Spinner_jam1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Spinner_menit1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_jenis_spl))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_istirahat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_add)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_select_karyawan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_jenis_spl, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Spinner_menit, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Spinner_jam, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Spinner_menit1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Spinner_jam1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_add, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_istirahat, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button_addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_addActionPerformed
        // TODO add your handling code here:
        try {
            boolean check = true;
            String id_pegawai = label_id.getText();
            String jam_mulai = Spinner_jam.getValue().toString() + ":" + Spinner_menit.getValue().toString();
            String jam_selesai = Spinner_jam1.getValue().toString() + ":" + Spinner_menit1.getValue().toString();
//        double jumlah_jam = (float) Spinner_jumlah_jam.getValue();
//        int jam = (int) jumlah_jam + (int) Spinner_jam.getValue();
//        int menit = (int) ((jumlah_jam % 1) * 60)
//                + Integer.valueOf(Spinner_menit.getValue().toString());
//        jam = jam + menit / 60;
//        menit = menit % 60;
//        String jam_selesai = jam + ":" + menit;

            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            Date date1 = format.parse(jam_mulai);
            Date date2 = format.parse(jam_selesai);
            long difference = date2.getTime() - date1.getTime();
            double menit_jarak_waktu = (double) difference / (60f * 1000f);
            int menit_istirahat = ComboBox_istirahat.getSelectedIndex() * 15;
            double jumlah_menit_lembur = (double) menit_jarak_waktu - menit_istirahat;
            double jumlah_jam_lembur = (double) jumlah_menit_lembur / 60d;
            
//            System.out.println("menit_jarak_waktu = " + menit_jarak_waktu);
//            System.out.println("menit_istirahat = " + menit_istirahat);
//            System.out.println("jumlah_menit_lembur = " + jumlah_menit_lembur);
//            System.out.println("jumlah_jam_lembur = " + jumlah_jam_lembur);

            if (date1.after(date2)) {
                check = false;
                JOptionPane.showMessageDialog(this, "jam selesai tidak bisa lebih awal dari jam mulai lembur!");
            } else if (menit_jarak_waktu == 0) {
                check = false;
                JOptionPane.showMessageDialog(this, "jam mulai dan jam selesai tidak bisa sama !!");
            } else if (menit_istirahat >= menit_jarak_waktu) {
                check = false;
                JOptionPane.showMessageDialog(this, "jumlah jam lembur tidak boleh lebih sedikit / sama dengan jam istirahat !!");
            } 
//            else if (jumlah_jam_lembur < 1) {
//                check = false;
//                JOptionPane.showMessageDialog(this, "jumlah jam lembur tidak boleh kurang dari 1 jam / 60 menit !!");
//            } else if (label_jenis_spl.getText().equals("PEJUANG") && jumlah_jam_lembur % 0.5d != 0d) {
//                check = false;
//                JOptionPane.showMessageDialog(this, "jumlah jam lembur HARUS kelipatan 0.5 jam / 30 menit !!");
//            } else if (label_jenis_spl.getText().equals("STAFF") && jumlah_jam_lembur % 1d != 0d) {
//                check = false;
//                JOptionPane.showMessageDialog(this, "jumlah jam lembur STAFF HARUS kelipatan 1 jam / 60 menit !!");
//            }

            if ("-".equals(id_pegawai)) {
                check = false;
                JOptionPane.showMessageDialog(this, "Anda belum memilih Pegawai yang akan lembur");
            } else {
                for (int i = 0; i < JDialog_Add_SuratPerintahLembur.Table_pegawai_lembur.getRowCount(); i++) {
                    if (id_pegawai.equals(JDialog_Add_SuratPerintahLembur.Table_pegawai_lembur.getValueAt(i, 2).toString())) {
                        check = false;
                        JOptionPane.showMessageDialog(this, label_nama.getText() + " sudah di input");
                        break;
                    }
                }
            }

            if (check) {
                DefaultTableModel model = (DefaultTableModel) JDialog_Add_SuratPerintahLembur.Table_pegawai_lembur.getModel();
                model.addRow(new Object[]{null, null, id_pegawai, label_nama.getText(), label_departemen.getText(), jam_mulai, jam_selesai, jumlah_jam_lembur, menit_istirahat});
                ColumnsAutoSizer.sizeColumnsToFit(JDialog_Add_SuratPerintahLembur.Table_pegawai_lembur);
                this.dispose();
            }
        } catch (ParseException ex) {
            Logger.getLogger(JDialog_Add_pegawai_lembur.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_addActionPerformed

    private void button_select_karyawanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_select_karyawanActionPerformed
        // TODO add your handling code here:
        String filter = "AND `kode_departemen` LIKE '%" + departemen + "%' AND `posisi` LIKE '" + label_jenis_spl.getText() + "%' ";
        Browse_Karyawan dialog = new Browse_Karyawan(new javax.swing.JFrame(), true, filter);
        dialog.ComboBox_posisi.setSelectedItem("All");
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);

        int x = Browse_Karyawan.table_list_karyawan.getSelectedRow();
        if (x != -1) {
            label_id.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 0).toString());
            label_nama.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 1).toString());
            label_bagian.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 2).toString());
            label_departemen.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 2).toString().split("-")[1]);
        }
    }//GEN-LAST:event_button_select_karyawanActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_istirahat;
    private javax.swing.JSpinner Spinner_jam;
    private javax.swing.JSpinner Spinner_jam1;
    private javax.swing.JSpinner Spinner_menit;
    private javax.swing.JSpinner Spinner_menit1;
    private javax.swing.JButton button_add;
    private javax.swing.JButton button_select_karyawan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel label_bagian;
    private javax.swing.JLabel label_departemen;
    private javax.swing.JLabel label_id;
    private javax.swing.JLabel label_jenis_spl;
    private javax.swing.JLabel label_nama;
    // End of variables declaration//GEN-END:variables
}
