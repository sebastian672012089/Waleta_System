package waleta_system.HRD;

import waleta_system.Class.Utility;
import java.awt.Font;
import java.awt.HeadlessException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.apache.commons.lang.time.DateUtils;
import waleta_system.Browse_Karyawan;
import waleta_system.MainForm;

public class JDialog_Add_Cuti extends javax.swing.JDialog {

    Font ori;
    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date today = new Date();

    public JDialog_Add_Cuti(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.setResizable(false);
        try {
            initComponents();
            Date_cuti.setDate(today);
        } catch (Exception ex) {
            Logger.getLogger(JDialog_Add_Cuti.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void load_tgl_cuti() {
        try {
            DefaultTableModel model = (DefaultTableModel) tabel_tgl_cuti.getModel();
            model.setRowCount(0);
            int lama_cuti = Integer.valueOf(Spinner_lama_cuti.getValue().toString());
            Date date = Date_cuti.getDate();
            int i = 0;

            String jenis_jam_kerja = null;
            sql = "SELECT `jam_kerja` FROM `tb_karyawan` WHERE `id_pegawai` = '" + label_id_pegawai.getText() + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                if (rs.getString("jam_kerja") != null) {
                    jenis_jam_kerja = rs.getString("jam_kerja");
                    while (i < lama_cuti) {
                        try {
                            boolean hari_libur = false, hari_masuk_kerja = false;
                            String tanggal = dateFormat.format(date);
                            String hari = new SimpleDateFormat("EEEEE").format(date);

                            sql = "SELECT `tanggal_libur`, `keterangan` FROM `tb_libur` WHERE `tanggal_libur` = '" + tanggal + "'";
                            rs = Utility.db.getStatement().executeQuery(sql);
                            hari_libur = rs.next();

                            Calendar cal = Calendar.getInstance();
                            cal.setTime(date);
                            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1; //1=Sunday, 2=Monday, 3=Tuesday, 4=Wednesday, 5=Thursday, 6=Friday, 7=Saturday.
                            if (dayOfWeek == 0) {
                                dayOfWeek = 7;
                            }

                            sql = "SELECT `masuk" + dayOfWeek + "` FROM `tb_jam_kerja` WHERE `jam_kerja` = '" + jenis_jam_kerja + "' AND `masuk" + dayOfWeek + "` IS NOT NULL";
                            rs = Utility.db.getStatement().executeQuery(sql);
                            hari_masuk_kerja = rs.next();

                            if (hari_masuk_kerja && !hari_libur) {
                                model.addRow(new Object[]{tanggal, hari});
                                i++;
                            }
                            date = new Date(date.getTime() + 1 * 24 * 60 * 60 * 1000);
                        } catch (Exception e) {
                            Logger.getLogger(JDialog_Add_Cuti.class.getName()).log(Level.SEVERE, null, e);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Jenis Jam kerja tidak ditemukan!");
                }
            }

        } catch (Exception e) {
            Logger.getLogger(JDialog_Add_Cuti.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void load_tgl_cuti_melahirkan() {
        DefaultTableModel model = (DefaultTableModel) tabel_tgl_cuti.getModel();
        model.setRowCount(0);
        Date tgl_mulai_cuti = Date_cuti.getDate();
        Date tgl_selesai_cuti = DateUtils.addMonths(tgl_mulai_cuti, 3);//cuti melahirkan 3 bulan
        while (tgl_mulai_cuti.before(tgl_selesai_cuti)) {
            try {
                boolean hari_libur = false, hari_masuk_kerja = false;
                String jenis_jam_kerja = null;
                String hari = new SimpleDateFormat("EEEEE").format(tgl_mulai_cuti);

                sql = "SELECT `tanggal_libur`, `keterangan` FROM `tb_libur` WHERE `tanggal_libur` = '" + dateFormat.format(tgl_mulai_cuti) + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                hari_libur = rs.next();

                sql = "SELECT `jam_kerja` FROM `tb_karyawan` WHERE `id_pegawai` = '" + label_id_pegawai.getText() + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    jenis_jam_kerja = rs.getString("jam_kerja");
                }

                Calendar cal = Calendar.getInstance();
                cal.setTime(tgl_mulai_cuti);
                int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1; //1=Sunday, 2=Monday, 3=Tuesday, 4=Wednesday, 5=Thursday, 6=Friday, 7=Saturday.
                if (dayOfWeek == 0) {
                    dayOfWeek = 7;
                }

                sql = "SELECT `masuk" + dayOfWeek + "` FROM `tb_jam_kerja` WHERE `jam_kerja` = '" + jenis_jam_kerja + "' AND `masuk" + dayOfWeek + "` IS NOT NULL";
                rs = Utility.db.getStatement().executeQuery(sql);
                hari_masuk_kerja = rs.next();

                if (hari_masuk_kerja && !hari_libur) {
                    model.addRow(new Object[]{dateFormat.format(tgl_mulai_cuti), hari});
                }
                tgl_mulai_cuti = new Date(tgl_mulai_cuti.getTime() + 1 * 24 * 60 * 60 * 1000);
            } catch (Exception e) {
                Logger.getLogger(JDialog_Add_Cuti.class.getName()).log(Level.SEVERE, null, e);
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
        jLabel1 = new javax.swing.JLabel();
        label_id_pegawai = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        Date_cuti = new com.toedter.calendar.JDateChooser();
        txt_keterangan = new javax.swing.JTextField();
        button_save = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        button_pilih_pengganti = new javax.swing.JButton();
        label_id_pengganti = new javax.swing.JLabel();
        label_nama_pengganti = new javax.swing.JLabel();
        ComboBox_jenis = new javax.swing.JComboBox<>();
        comboBox_kategori = new javax.swing.JComboBox<>();
        button_cancel = new javax.swing.JButton();
        button_pilih_pegawai = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        label_nama_pegawai = new javax.swing.JLabel();
        label_title = new javax.swing.JLabel();
        Spinner_lama_cuti = new javax.swing.JSpinner();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_tgl_cuti = new javax.swing.JTable();
        button_check = new javax.swing.JButton();
        label_total_hari = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cuti Karyawan");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel1.setText("ID Pegawai :");

        label_id_pegawai.setBackground(new java.awt.Color(255, 255, 255));
        label_id_pegawai.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_id_pegawai.setText("-");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Mulai Cuti / Absen :");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Lama Cuti  :");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Keterangan:");

        Date_cuti.setBackground(new java.awt.Color(255, 255, 255));
        Date_cuti.setDateFormatString("dd MMMM yyyy");
        Date_cuti.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        txt_keterangan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_keterangan.setText("-");

        button_save.setBackground(new java.awt.Color(255, 255, 255));
        button_save.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_save.setText("SAVE");
        button_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveActionPerformed(evt);
            }
        });

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText("Jenis :");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel11.setText("Kategori :");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel12.setText("Pengganti :");

        button_pilih_pengganti.setBackground(new java.awt.Color(255, 255, 255));
        button_pilih_pengganti.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_pilih_pengganti.setText("Select");
        button_pilih_pengganti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_pilih_penggantiActionPerformed(evt);
            }
        });

        label_id_pengganti.setBackground(new java.awt.Color(255, 255, 255));
        label_id_pengganti.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_id_pengganti.setText("-");

        label_nama_pengganti.setBackground(new java.awt.Color(255, 255, 255));
        label_nama_pengganti.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_nama_pengganti.setText("-");

        ComboBox_jenis.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_jenis.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Cuti Tahunan", "Cuti Sakit", "Cuti Khusus", "Absen" }));
        ComboBox_jenis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboBox_jenisActionPerformed(evt);
            }
        });

        comboBox_kategori.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        comboBox_kategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pekerja Menikah", "Menikahkan anak", "Mengkhitankan anak", "Membaptiskan anak", "Istri melahirkan atau keguguran", "Suami/Istri/Orangtua/Mertua/Anak/Menantu Meninggal", "Anggota keluarga dalam 1 rumah meninggal", "Pekerja melahirkan" }));
        comboBox_kategori.setEnabled(false);
        comboBox_kategori.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBox_kategoriActionPerformed(evt);
            }
        });

        button_cancel.setText("CANCEL");
        button_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_cancelActionPerformed(evt);
            }
        });

        button_pilih_pegawai.setBackground(new java.awt.Color(255, 255, 255));
        button_pilih_pegawai.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_pilih_pegawai.setText("Select Pegawai");
        button_pilih_pegawai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_pilih_pegawaiActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel2.setText("Nama Pegawai :");

        label_nama_pegawai.setBackground(new java.awt.Color(255, 255, 255));
        label_nama_pegawai.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_nama_pegawai.setText("-");

        label_title.setBackground(new java.awt.Color(255, 255, 255));
        label_title.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_title.setText("Tambah Data Cuti");

        Spinner_lama_cuti.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Spinner_lama_cuti.setModel(new javax.swing.SpinnerNumberModel(1, 1, 365, 1));

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Hari Kerja");

        tabel_tgl_cuti.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tgl cuti", "Hari"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_tgl_cuti.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tabel_tgl_cuti);

        button_check.setBackground(new java.awt.Color(255, 255, 255));
        button_check.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_check.setText("Check");
        button_check.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_checkActionPerformed(evt);
            }
        });

        label_total_hari.setBackground(new java.awt.Color(255, 255, 255));
        label_total_hari.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_hari.setText("TOTAL HARI :");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_title)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(ComboBox_jenis, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_cuti, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_nama_pegawai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(label_id_pegawai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(button_pilih_pegawai)
                            .addComponent(comboBox_kategori, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(Spinner_lama_cuti, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_check))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_pilih_pengganti, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_id_pengganti)
                            .addComponent(label_nama_pengganti)))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(button_cancel)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(button_save))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txt_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(label_total_hari)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(label_title, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_pilih_pegawai, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(label_id_pegawai, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_nama_pegawai, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_cuti, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(ComboBox_jenis, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(comboBox_kategori, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Spinner_lama_cuti, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_check, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_id_pengganti, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(button_pilih_pengganti, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_nama_pengganti, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(button_save, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_total_hari, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(button_cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void button_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_saveActionPerformed
        // TODO add your handling code here:
        try {
            button_check.doClick();
            Utility.db.getConnection().setAutoCommit(false);
            Utility.db.getConnection().createStatement();
            String pengganti = "'" + label_id_pengganti.getText() + "'";
            if (label_id_pengganti.getText().equals("-") || label_id_pengganti.getText().equals("") || label_id_pengganti.getText() == null) {
                pengganti = "NULL";
            }

            String kategori_cuti = "-";
            if (comboBox_kategori.isEnabled()) {
                kategori_cuti = comboBox_kategori.getSelectedItem().toString();
            }

            sql = "INSERT INTO `tb_cuti_pengajuan`(`id_pegawai`, `tgl_input_pengajuan_cuti`, `tgl_pengajuan`, `jenis_cuti`, `kategori_cuti`, `keterangan`, `pengganti`) "
                    + "VALUES ('" + label_id_pegawai.getText() + "',CURRENT_DATE, '" + dateFormat.format(Date_cuti.getDate()) + "','" + ComboBox_jenis.getSelectedItem().toString() + "','" + kategori_cuti + "','" + txt_keterangan.getText() + "'," + pengganti + ")";
            PreparedStatement stmt = Utility.db.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.executeUpdate();
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int kode_pengajuan = rs.getInt(1);
                for (int i = 0; i < tabel_tgl_cuti.getRowCount(); i++) {
                    sql = "SELECT `kode_cuti` FROM `tb_cuti` WHERE `id_pegawai` = '" + label_id_pegawai.getText() + "' AND `tanggal_cuti` = '" + tabel_tgl_cuti.getValueAt(i, 0).toString() + "'";
                    rs = Utility.db.getStatement().executeQuery(sql);
                    if (rs.next()) {
                        String Query = "UPDATE `tb_cuti` SET "
                                + "`jenis_cuti`='" + ComboBox_jenis.getSelectedItem().toString() + "',"
                                + "`kategori_cuti`='" + kategori_cuti + "',"
                                + "`keterangan`='" + txt_keterangan.getText() + "',"
                                + "`pengganti`=" + pengganti + " "
                                + "WHERE `kode_cuti`='" + rs.getString("kode_cuti") + "'";
                        Utility.db.getStatement().executeUpdate(Query);
                    } else {
                        String Query = "INSERT INTO `tb_cuti` (`id_pegawai`, `tanggal_cuti`, `jenis_cuti`, `kategori_cuti`, `keterangan`, `pengganti`, `kode_pengajuan`) "
                                + "VALUES ('" + label_id_pegawai.getText() + "', '" + tabel_tgl_cuti.getValueAt(i, 0).toString() + "', '" + ComboBox_jenis.getSelectedItem().toString() + "', '" + kategori_cuti + "', '" + txt_keterangan.getText() + "', " + pengganti + ", " + kode_pengajuan + ")";
                        Utility.db.getStatement().executeUpdate(Query);
                    }
                }
            }

            Utility.db.getConnection().commit();
            JOptionPane.showMessageDialog(this, "Data Added!");
            this.dispose();
        } catch (Exception e) {
            try {
                Utility.db.getConnection().rollback();
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Add_Cuti.class.getName()).log(Level.SEVERE, null, ex);
            }
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Add_Cuti.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_saveActionPerformed

    private void button_pilih_penggantiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pilih_penggantiActionPerformed
        // TODO add your handling code here:
        Browse_Karyawan dialog = new Browse_Karyawan(new javax.swing.JFrame(), true, null);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);

        int x = Browse_Karyawan.table_list_karyawan.getSelectedRow();
        if (x != -1) {
            label_id_pengganti.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 0).toString());
            label_nama_pengganti.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 1).toString());
        }
    }//GEN-LAST:event_button_pilih_penggantiActionPerformed

    private void ComboBox_jenisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboBox_jenisActionPerformed
        // TODO add your handling code here:
        switch (ComboBox_jenis.getSelectedIndex()) {
            case 0://Cuti Tahunan
                comboBox_kategori.setEnabled(false);
                Spinner_lama_cuti.setEnabled(true);
                break;
            case 1://Cuti Sakit
                comboBox_kategori.setEnabled(false);
                Spinner_lama_cuti.setEnabled(true);
                break;
            case 2://Cuti Khusus
                comboBox_kategori.setEnabled(true);
                Spinner_lama_cuti.setEnabled(false);
                switch (comboBox_kategori.getSelectedIndex()) {
                    case 0://Pekerja Menikah
                        Spinner_lama_cuti.setValue(3);
                        break;
                    case 1://Menikahkan anak
                        Spinner_lama_cuti.setValue(2);
                        break;
                    case 2://Mengkhitankan anak
                        Spinner_lama_cuti.setValue(2);
                        break;
                    case 3://Membaptiskan anak
                        Spinner_lama_cuti.setValue(2);
                        break;
                    case 4://Istri melahirkan atau keguguran
                        Spinner_lama_cuti.setValue(2);
                        break;
                    case 5://Suami/Istri/Orangtua/Mertua/Anak/Menantu Meninggal
                        Spinner_lama_cuti.setValue(2);
                        break;
                    case 6://Anggota keluarga dalam 1 rumah meninggal
                        Spinner_lama_cuti.setValue(1);
                        break;
                    default:
                        break;
                }
                break;
            case 3://Absen / Tidak masuk
                comboBox_kategori.setEnabled(false);
                Spinner_lama_cuti.setEnabled(false);
                Spinner_lama_cuti.setValue(1);
                break;
            default:
                break;
        }
    }//GEN-LAST:event_ComboBox_jenisActionPerformed

    private void button_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_button_cancelActionPerformed

    private void button_pilih_pegawaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pilih_pegawaiActionPerformed
        // TODO add your handling code here:
        Browse_Karyawan dialog = new Browse_Karyawan(new javax.swing.JFrame(), true, null);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);

        int x = Browse_Karyawan.table_list_karyawan.getSelectedRow();
        if (x != -1) {
            label_id_pegawai.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 0).toString());
            label_nama_pegawai.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 1).toString());
        }
    }//GEN-LAST:event_button_pilih_pegawaiActionPerformed

    private void comboBox_kategoriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBox_kategoriActionPerformed
        // TODO add your handling code here:
        switch (comboBox_kategori.getSelectedIndex()) {
            case 0://Pekerja Menikah
                Spinner_lama_cuti.setValue(3);
                break;
            case 1://Menikahkan anak
                Spinner_lama_cuti.setValue(2);
                break;
            case 2://Mengkhitankan anak
                Spinner_lama_cuti.setValue(2);
                break;
            case 3://Membaptiskan anak
                Spinner_lama_cuti.setValue(2);
                break;
            case 4://Istri melahirkan atau keguguran
                Spinner_lama_cuti.setValue(2);
                break;
            case 5://Suami/Istri/Orangtua/Mertua/Anak/Menantu Meninggal
                Spinner_lama_cuti.setValue(2);
                break;
            case 6://Anggota keluarga dalam 1 rumah meninggal
                Spinner_lama_cuti.setValue(1);
                break;
            default:
                break;
        }
    }//GEN-LAST:event_comboBox_kategoriActionPerformed

    private void button_checkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_checkActionPerformed
        // TODO add your handling code here:
        if (label_id_pegawai.getText().equals("-")) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih karyawan yang akan cuti terlebih dahulu !");
        } else if (Date_cuti.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih tanggal mulai cuti terlebih dahulu !");
        } else if (ComboBox_jenis.getSelectedItem().toString().equals("Cuti Tahunan") || ComboBox_jenis.getSelectedItem().toString().equals("Absen")) {
            try {
                int lama_cuti = Integer.valueOf(Spinner_lama_cuti.getValue().toString());
                int cuti_tahunan = 0, absen = 0;
                sql = "SELECT `jenis_cuti`, COUNT(`kode_cuti`) AS 'cuti' FROM `tb_cuti` "
                        + "WHERE `id_pegawai` = '" + label_id_pegawai.getText() + "' AND YEAR(`tanggal_cuti`) = YEAR(CURDATE()) GROUP BY `jenis_cuti`";
//                System.out.println(sql);
                rs = Utility.db.getStatement().executeQuery(sql);
                while (rs.next()) {
                    if (null != rs.getString("jenis_cuti")) {
                        switch (rs.getString("jenis_cuti")) {
                            case "Cuti Tahunan":
                                cuti_tahunan = rs.getInt("cuti");
                                break;
                            case "Absen":
                                absen = rs.getInt("cuti");
                                break;
                            default:
                                break;
                        }
                    }
                }
                int jatah_cuti = 12;
                sql = "SELECT MONTH(`tanggal_masuk`) AS 'month' FROM `tb_karyawan` "
                        + "WHERE `id_pegawai` = '" + label_id_pegawai.getText() + "' AND YEAR(`tanggal_masuk`) = YEAR(CURRENT_DATE)";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    jatah_cuti = 13 - rs.getInt("month");
                }
                if ((cuti_tahunan + absen + lama_cuti) > jatah_cuti) {
                    JOptionPane.showMessageDialog(this, "Total cuti tahunan yang terpakai (" + cuti_tahunan + ") dan absen (" + absen + ") !\n"
                            + "maaf sisa cuti tahunan yang tersedia hanya " + (jatah_cuti - (cuti_tahunan + absen)), "Access Denied !", JOptionPane.ERROR_MESSAGE);
                }
                load_tgl_cuti();
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Add_Cuti.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            if (comboBox_kategori.getSelectedItem().toString().equals("Pekerja melahirkan")) {
                load_tgl_cuti_melahirkan();
            } else {
                load_tgl_cuti();
            }
        }
        label_total_hari.setText("TOTAL HARI CUTI :" + Integer.toString(tabel_tgl_cuti.getRowCount()));
    }//GEN-LAST:event_button_checkActionPerformed

    /**
     * @param args the command line arguments
     */
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
            java.util.logging.Logger.getLogger(JDialog_Add_Cuti.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JDialog_Add_Cuti.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JDialog_Add_Cuti.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDialog_Add_Cuti.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JDialog_Add_Cuti dialog = new JDialog_Add_Cuti(new javax.swing.JFrame(), true);
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
    private javax.swing.JComboBox<String> ComboBox_jenis;
    private com.toedter.calendar.JDateChooser Date_cuti;
    private javax.swing.JSpinner Spinner_lama_cuti;
    private javax.swing.JButton button_cancel;
    private javax.swing.JButton button_check;
    private javax.swing.JButton button_pilih_pegawai;
    private javax.swing.JButton button_pilih_pengganti;
    private javax.swing.JButton button_save;
    private javax.swing.JComboBox<String> comboBox_kategori;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_id_pegawai;
    private javax.swing.JLabel label_id_pengganti;
    private javax.swing.JLabel label_nama_pegawai;
    private javax.swing.JLabel label_nama_pengganti;
    private javax.swing.JLabel label_title;
    private javax.swing.JLabel label_total_hari;
    private javax.swing.JTable tabel_tgl_cuti;
    private javax.swing.JTextField txt_keterangan;
    // End of variables declaration//GEN-END:variables
}
