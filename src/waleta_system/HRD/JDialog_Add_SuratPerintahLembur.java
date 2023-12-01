package waleta_system.HRD;

import java.awt.HeadlessException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Browse_Karyawan;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;
import waleta_system.MainForm;

public class JDialog_Add_SuratPerintahLembur extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String no_surat;

    public JDialog_Add_SuratPerintahLembur(java.awt.Frame parent, boolean modal, String no_surat, String jenis_spl) {
        super(parent, modal);
        initComponents();
        label_jenis_spl.setText(jenis_spl);
        label_diajukan.setText(MainForm.Login_NamaPegawai);
        button_add.setIcon(Utility.ResizeImageIcon(new javax.swing.ImageIcon(getClass().getResource("/waleta_system/Images/add_green.png")), button_add.getWidth(), button_add.getHeight()));
        button_delete.setIcon(Utility.ResizeImageIcon(new javax.swing.ImageIcon(getClass().getResource("/waleta_system/Images/delete-icon.png")), button_delete.getWidth(), button_delete.getHeight()));

        this.no_surat = no_surat;
        if (no_surat != null) {
            load_data_Edit(no_surat);
        }
    }

    public void set_no_urut() {
        for (int i = 0; i < Table_pegawai_lembur.getRowCount(); i++) {
            Table_pegawai_lembur.setValueAt(i + 1, i, 0);
        }
    }

    public void load_data_Edit(String no_surat) {
        try {
            //membuat nomor surat
            sql = "SELECT `nomor_surat`, `tanggal_surat`, `jenis_spl`, `kode_departemen`, `jenis_hari`, `tanggal_lembur`, `uraian_tugas`, `diajukan` "
                    + "FROM `tb_surat_lembur` "
                    + "WHERE `nomor_surat` = '" + no_surat + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                label_no_surat.setText(rs.getString("nomor_surat"));
                Date_tgl_surat.setDate(rs.getDate("tanggal_surat"));
                label_jenis_spl.setText(rs.getString("jenis_spl"));
                label_departemen.setText(rs.getString("kode_departemen"));
                Date_tgl_lembur.setDate(rs.getDate("tanggal_lembur"));
                ComboBox_jenis_hari.setSelectedItem(rs.getString("jenis_hari"));
                txt_tugas.setText(rs.getString("uraian_tugas"));
                label_diajukan.setText(rs.getString("diajukan"));
            }

            DefaultTableModel model = (DefaultTableModel) Table_pegawai_lembur.getModel();
            model.setRowCount(0);
            sql = "SELECT `nomor_lembur`, `tb_surat_lembur_detail`.`id_pegawai`, `nama_pegawai`, `kode_departemen`, "
                    + "`mulai_lembur`, `selesai_lembur`, `jumlah_jam`, `jenis_lembur`, `menit_istirahat_lembur` "
                    + "FROM `tb_surat_lembur_detail` "
                    + "LEFT JOIN `tb_karyawan` ON `tb_surat_lembur_detail`.`id_pegawai` = `tb_karyawan`.`id_pegawai` "
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian` "
                    + "WHERE `nomor_surat` = '" + no_surat + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[1] = rs.getString("nomor_lembur");
                row[2] = rs.getString("id_pegawai");
                row[3] = rs.getString("nama_pegawai");
                row[4] = rs.getString("kode_departemen");
                row[5] = rs.getString("mulai_lembur");
                row[6] = rs.getString("selesai_lembur");
                row[7] = rs.getFloat("jumlah_jam");
                row[8] = rs.getFloat("menit_istirahat_lembur");
                ComboBox_jenis_lembur.setSelectedItem(rs.getString("jenis_lembur"));
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_pegawai_lembur);

        } catch (SQLException ex) {
            Logger.getLogger(JDialog_Add_SuratPerintahLembur.class.getName()).log(Level.SEVERE, null, ex);
        }
        set_no_urut();
    }

    private void edit() {
        try {
            Utility.db.getConnection().setAutoCommit(false);
            String Query_surat = null, Query_pegawai = null;
            //variabel for input surat lembur
            String nomor_surat = label_no_surat.getText();
            String tanggal_surat = dateFormat.format(Date_tgl_surat.getDate());
            String departemen = label_departemen.getText();
            String tanggal_lembur = dateFormat.format(Date_tgl_lembur.getDate());
            String jenis_hari = ComboBox_jenis_hari.getSelectedItem().toString();
            String jenis_lembur = ComboBox_jenis_lembur.getSelectedItem().toString();
            String uraian_tugas = txt_tugas.getText();

            sql = "DELETE FROM `tb_surat_lembur_detail` WHERE `nomor_surat` = '" + nomor_surat + "'";
            Utility.db.getStatement().executeUpdate(sql);

            Query_surat = "UPDATE `tb_surat_lembur` SET "
                    + "`tanggal_surat`='" + tanggal_surat + "',"
                    + "`kode_departemen`='" + departemen + "',"
                    + "`jenis_hari`='" + jenis_hari + "',"
                    + "`tanggal_lembur`='" + tanggal_lembur + "',"
                    + "`uraian_tugas`='" + uraian_tugas + "',"
                    + "`diajukan`='" + label_diajukan.getText() + "' "
                    + "WHERE `nomor_surat`='" + nomor_surat + "'";
            Utility.db.getStatement().executeUpdate(Query_surat);

            for (int i = 0; i < Table_pegawai_lembur.getRowCount(); i++) {
                String id_pegawai = Table_pegawai_lembur.getValueAt(i, 2).toString();
                String mulai_lembur = Table_pegawai_lembur.getValueAt(i, 5).toString();
                String selesai_lembur = Table_pegawai_lembur.getValueAt(i, 6).toString();
                String jumlah_jam = Table_pegawai_lembur.getValueAt(i, 7).toString();
                String menit_istirahat_lembur = Table_pegawai_lembur.getValueAt(i, 8).toString();
                Query_pegawai = "INSERT INTO `tb_surat_lembur_detail`(`id_pegawai`, `tanggal_lembur`, `jenis_lembur`,`mulai_lembur`,`selesai_lembur`, `jumlah_jam`, `menit_istirahat_lembur`, `nomor_surat`) "
                        + "VALUES ('" + id_pegawai + "','" + tanggal_lembur + "', '" + jenis_lembur + "','" + mulai_lembur + "','" + selesai_lembur + "','" + jumlah_jam + "','" + menit_istirahat_lembur + "','" + nomor_surat + "')";
                Utility.db.getStatement().executeUpdate(Query_pegawai);
            }
            Utility.db.getConnection().commit();
            JOptionPane.showMessageDialog(this, "Data Added!");
            this.dispose();
        } catch (SQLException | HeadlessException e) {
            try {
                Utility.db.getConnection().rollback();
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Add_SuratPerintahLembur.class.getName()).log(Level.SEVERE, null, ex);
            }
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JDialog_Add_SuratPerintahLembur.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Add_SuratPerintahLembur.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void baru() {
        try {
            Utility.db.getConnection().setAutoCommit(false);
            String Query_surat = null, Query_pegawai = null;
            //membuat nomor surat
            sql = "SELECT MAX(`nomor_surat`)+1 AS 'nomor' FROM `tb_surat_lembur`";
            rs = Utility.db.getStatement().executeQuery(sql);
//            System.out.println(sql);
            if (rs.next()) {
                if (rs.getString("nomor") == null) {
                    label_no_surat.setText("1");
                } else {
                    label_no_surat.setText(rs.getString("nomor"));
                }
            }
            //variabel for input surat lembur
            String nomor_surat = label_no_surat.getText();
            String tanggal_surat = dateFormat.format(Date_tgl_surat.getDate());
            String departemen = label_departemen.getText();
            String tanggal_lembur = dateFormat.format(Date_tgl_lembur.getDate());
            String jenis_hari = ComboBox_jenis_hari.getSelectedItem().toString();
            String jenis_lembur = ComboBox_jenis_lembur.getSelectedItem().toString();
            String uraian_tugas = txt_tugas.getText();

            Query_surat = "INSERT INTO `tb_surat_lembur`(`nomor_surat`, `tanggal_surat`, `jenis_spl`, `kode_departemen`, `jenis_hari`, `tanggal_lembur`, `uraian_tugas`, `diajukan`) "
                    + "VALUES ('" + nomor_surat + "','" + tanggal_surat + "','" + label_jenis_spl.getText() + "','" + departemen + "','" + jenis_hari + "','" + tanggal_lembur + "','" + uraian_tugas + "','" + label_diajukan.getText() + "')";
            if ((Utility.db.getStatement().executeUpdate(Query_surat)) > 0) {
                int x = Table_pegawai_lembur.getRowCount();
                for (int i = 0; i < x; i++) {
                    String id_pegawai = Table_pegawai_lembur.getValueAt(i, 2).toString();
                    String mulai_lembur = Table_pegawai_lembur.getValueAt(i, 5).toString();
                    String selesai_lembur = Table_pegawai_lembur.getValueAt(i, 6).toString();
                    String jumlah_jam = Table_pegawai_lembur.getValueAt(i, 7).toString();
                    String menit_istirahat_lembur = Table_pegawai_lembur.getValueAt(i, 8).toString();
                    Query_pegawai = "INSERT INTO `tb_surat_lembur_detail`(`id_pegawai`, `tanggal_lembur`, `jenis_lembur`,`mulai_lembur`,`selesai_lembur`, `jumlah_jam`, `menit_istirahat_lembur`, `nomor_surat`) "
                            + "VALUES ('" + id_pegawai + "','" + tanggal_lembur + "', '" + jenis_lembur + "','" + mulai_lembur + "','" + selesai_lembur + "','" + jumlah_jam + "','" + menit_istirahat_lembur + "','" + nomor_surat + "')";
                    Utility.db.getStatement().executeUpdate(Query_pegawai);
                }
                JOptionPane.showMessageDialog(this, "Data Added!");
                this.dispose();
                Utility.db.getConnection().commit();
            }
        } catch (Exception e) {
            try {
                Utility.db.getConnection().rollback();
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Add_SuratPerintahLembur.class.getName()).log(Level.SEVERE, null, ex);
            }
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JDialog_Add_SuratPerintahLembur.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Add_SuratPerintahLembur.class.getName()).log(Level.SEVERE, null, ex);
            }
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

        jPanel1 = new javax.swing.JPanel();
        label_title = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        label_no_surat = new javax.swing.JLabel();
        Date_tgl_surat = new com.toedter.calendar.JDateChooser();
        ComboBox_jenis_hari = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        Date_tgl_lembur = new com.toedter.calendar.JDateChooser();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txt_tugas = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        Table_pegawai_lembur = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        label_diajukan = new javax.swing.JLabel();
        button_delete = new javax.swing.JButton();
        button_add = new javax.swing.JButton();
        button_simpan = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        ComboBox_jenis_lembur = new javax.swing.JComboBox<>();
        label_jenis_spl = new javax.swing.JLabel();
        label_departemen = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Surat Perintah Lembur");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        label_title.setBackground(new java.awt.Color(255, 255, 255));
        label_title.setFont(new java.awt.Font("Nirmala UI", 1, 14)); // NOI18N
        label_title.setText("Surat Perintah Lembur");

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Nomor Surat :");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Tanggal SPL :");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Departemen :");

        label_no_surat.setBackground(new java.awt.Color(255, 255, 255));
        label_no_surat.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_no_surat.setText("XXXX");

        Date_tgl_surat.setBackground(new java.awt.Color(255, 255, 255));
        Date_tgl_surat.setDate(date);
        Date_tgl_surat.setDateFormatString("dd MMMM yyyy");
        Date_tgl_surat.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        ComboBox_jenis_hari.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_jenis_hari.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hari Kerja", "Hari Libur" }));

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Tanggal Lembur :");

        Date_tgl_lembur.setBackground(new java.awt.Color(255, 255, 255));
        Date_tgl_lembur.setDateFormatString("dd MMMM yyyy");
        Date_tgl_lembur.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Uraian tugas :");

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setAutoscrolls(true);

        txt_tugas.setColumns(20);
        txt_tugas.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        txt_tugas.setLineWrap(true);
        txt_tugas.setRows(5);
        jScrollPane1.setViewportView(txt_tugas);

        Table_pegawai_lembur.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Kode", "ID Pegawai", "Nama", "Departemen", "Mulai Lembur", "Selesai Lembur", "Jam", "Istirahat"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_pegawai_lembur.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(Table_pegawai_lembur);

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel7.setText("Pegawai Lembur");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Diajukan Oleh :");

        label_diajukan.setBackground(new java.awt.Color(255, 255, 255));
        label_diajukan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_diajukan.setText("-");

        button_delete.setBackground(new java.awt.Color(255, 255, 255));
        button_delete.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete.setMargin(new java.awt.Insets(2, 2, 2, 2));
        button_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_deleteActionPerformed(evt);
            }
        });

        button_add.setBackground(new java.awt.Color(255, 255, 255));
        button_add.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_add.setMargin(new java.awt.Insets(2, 2, 2, 2));
        button_add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_addActionPerformed(evt);
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

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("Jenis Hari :");

        ComboBox_jenis_lembur.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_jenis_lembur.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-", "Pulang", "Masuk" }));

        label_jenis_spl.setBackground(new java.awt.Color(255, 255, 255));
        label_jenis_spl.setFont(new java.awt.Font("Nirmala UI", 1, 14)); // NOI18N
        label_jenis_spl.setForeground(new java.awt.Color(0, 0, 255));
        label_jenis_spl.setText("PEJUANG");

        label_departemen.setBackground(new java.awt.Color(255, 255, 255));
        label_departemen.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_departemen.setText("DEPARTEMEN");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel10.setText("Jenis Lembur :");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_add, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_no_surat, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_tgl_surat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_tgl_lembur, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_jenis_hari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_departemen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_jenis_lembur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(label_title)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_jenis_spl)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_diajukan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_simpan)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_title)
                    .addComponent(label_jenis_spl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_no_surat, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(Date_tgl_surat, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_jenis_lembur, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_departemen, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_tgl_lembur, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ComboBox_jenis_hari, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_add, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_diajukan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_simpan))
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
        String departemen = "";
        if (Table_pegawai_lembur.getRowCount() > 0) {
            departemen = Table_pegawai_lembur.getValueAt(0, 4).toString();
        }
        JDialog_Add_pegawai_lembur dialog = new JDialog_Add_pegawai_lembur(new javax.swing.JFrame(), true, departemen, label_jenis_spl.getText());
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        set_no_urut();

        if (Table_pegawai_lembur.getRowCount() > 0) {
            label_departemen.setText(Table_pegawai_lembur.getValueAt(0, 4).toString());
        }
    }//GEN-LAST:event_button_addActionPerformed

    private void button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_deleteActionPerformed
        // TODO add your handling code here:
        int i = Table_pegawai_lembur.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) Table_pegawai_lembur.getModel();
        if (i != -1) {
            model.removeRow(i);
            set_no_urut();
        }
    }//GEN-LAST:event_button_deleteActionPerformed

    private void button_simpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_simpanActionPerformed
        // TODO add your handling code here:
        boolean check = true;
        try {
            if (Table_pegawai_lembur.getRowCount() == 0) {
                throw new Exception("Nama2 karyawan yang akan lembur belum di masukkan!");
            }

            if (ComboBox_jenis_lembur.getSelectedItem().toString().equals("-")) {
                throw new Exception("Jenis Lembur belum dipilih");
            }

            for (int i = 0; i < Table_pegawai_lembur.getRowCount(); i++) {
                String id = Table_pegawai_lembur.getValueAt(i, 2).toString();
                String nama = Table_pegawai_lembur.getValueAt(i, 3).toString();
                String tanggal = dateFormat.format(Date_tgl_lembur.getDate());
                String jenis_lembur = ComboBox_jenis_lembur.getSelectedItem().toString();
                String filter_no_surat = "";
                if (no_surat != null) {
                    filter_no_surat = "AND `nomor_surat` <> '" + no_surat + "'";
                }
                sql = "SELECT `nomor_lembur` FROM `tb_surat_lembur_detail` "
                        + "WHERE "
                        + "`id_pegawai` = '" + id + "' "
                        + "AND `tanggal_lembur` = '" + tanggal + "' "
                        + "AND `jenis_lembur` = '" + jenis_lembur + "'"
                        + filter_no_surat;
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    throw new Exception("Sudah ada SPL " + jenis_lembur + " untuk " + nama + " pada tanggal " + tanggal);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JDialog_Add_SuratPerintahLembur.class.getName()).log(Level.SEVERE, null, e);
            check = false;
        }
        //variabel for tambah pegawai lembur

        if (check) {
            if (no_surat != null) {
                edit();
            } else {
                baru();
            }
        }
    }//GEN-LAST:event_button_simpanActionPerformed

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JDialog_Add_SuratPerintahLembur.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JDialog_Add_SuratPerintahLembur.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JDialog_Add_SuratPerintahLembur.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDialog_Add_SuratPerintahLembur.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JDialog_Add_SuratPerintahLembur dialog = new JDialog_Add_SuratPerintahLembur(new javax.swing.JFrame(), true, null, "PEJUANG");
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
    private javax.swing.JComboBox<String> ComboBox_jenis_hari;
    private javax.swing.JComboBox<String> ComboBox_jenis_lembur;
    private com.toedter.calendar.JDateChooser Date_tgl_lembur;
    private com.toedter.calendar.JDateChooser Date_tgl_surat;
    public static javax.swing.JTable Table_pegawai_lembur;
    private javax.swing.JButton button_add;
    private javax.swing.JButton button_delete;
    private javax.swing.JButton button_simpan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel label_departemen;
    private javax.swing.JLabel label_diajukan;
    public static javax.swing.JLabel label_jenis_spl;
    private javax.swing.JLabel label_no_surat;
    public static javax.swing.JLabel label_title;
    private javax.swing.JTextArea txt_tugas;
    // End of variables declaration//GEN-END:variables
}
