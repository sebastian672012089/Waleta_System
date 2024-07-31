package waleta_system.BahanJadi;

import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import waleta_system.Browse_Karyawan;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JDialog_GradingBahanJadi extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    String kode_repacking;
    int tot_keping = 0, tot_gram = 0;
    int keping_awal = 0;
    String status;
    Date tgl_masuk = null;

    public JDialog_GradingBahanJadi(java.awt.Frame parent, boolean modal, String LP, int keping, String status, Date tgl_masuk) {
        super(parent, modal);
        try {
            initComponents();
            this.setResizable(false);

            this.status = status;
            this.keping_awal = keping;
            this.tgl_masuk = tgl_masuk;
            label_LP.setText(LP);
            Date_grading.setMinSelectableDate(tgl_masuk);
            //give icon for button
            button_tambah.setIcon(Utility.ResizeImageIcon(new javax.swing.ImageIcon(getClass().getResource("/waleta_system/Images/add_green.png")), button_tambah.getWidth(), button_tambah.getHeight()));
            button_kurang.setIcon(Utility.ResizeImageIcon(new javax.swing.ImageIcon(getClass().getResource("/waleta_system/Images/delete-icon.png")), button_kurang.getWidth(), button_kurang.getHeight()));

//            int i = 0, x = 0;
//            sql = "SELECT COUNT(`kode_grade`) AS 'count_grade' FROM `tb_grade_bahan_jadi`";
//            rs = Utility.db.getStatement().executeQuery(sql);
//            if (rs.next()) {
//                x = rs.getInt("count_grade");
//            }
            ComboBox_Grade();

            if ("edit".equals(status)) {
                getEditData();
            } else {
                byProduct();
            }
        } catch (Exception ex) {
            Logger.getLogger(JDialog_GradingBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void ComboBox_Grade() {
        ComboBox_kode_grading.removeAllItems();
        if (ComboBox_GNS_NS.getSelectedIndex() == 0) {
            try {
                String grade_COMBOBOX = "SELECT `kode_grade` FROM `tb_grade_bahan_jadi` WHERE `status_grade` = 'AKTIF' AND `kode_grade` LIKE 'GNS%'";
                ResultSet grade_result = Utility.db.getStatement().executeQuery(grade_COMBOBOX);
                while (grade_result.next()) {
                    ComboBox_kode_grading.addItem(grade_result.getString("kode_grade").substring(4));
                }
                AutoCompleteDecorator.decorate(ComboBox_kode_grading);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_rePacking.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (ComboBox_GNS_NS.getSelectedIndex() == 1) {
            try {
                String grade_COMBOBOX = "SELECT `kode_grade` FROM `tb_grade_bahan_jadi` WHERE `status_grade` = 'AKTIF' AND `kode_grade` LIKE 'Non NS%'";
                ResultSet grade_result = Utility.db.getStatement().executeQuery(grade_COMBOBOX);
                while (grade_result.next()) {
                    ComboBox_kode_grading.addItem(grade_result.getString("kode_grade").substring(7));
                }
                AutoCompleteDecorator.decorate(ComboBox_kode_grading);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_rePacking.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void byProduct() {
        try {
            DefaultTableModel model = (DefaultTableModel) table_hasil.getModel();
            String b = "SELECT `sesekan`, `hancuran`, `rontokan`, `bonggol`, `serabut`, `status_akhir` "
                    + "FROM `tb_finishing_2` "
                    + "LEFT JOIN `tb_lab_laporan_produksi` ON `tb_finishing_2`.`no_laporan_produksi` = `tb_lab_laporan_produksi`.`no_laporan_produksi`"
                    + "WHERE "
                    + "`tb_finishing_2`.`no_laporan_produksi` = '" + label_LP.getText() + "'";
            pst = Utility.db.getConnection().prepareStatement(b);
            rs = pst.executeQuery();
            if (rs.next()) {
                String qc = "GNS";
                if ("HOLD/NON GNS".equals(rs.getString("status_akhir"))) {
                    qc = "NON NS";
                    ComboBox_GNS_NS.setSelectedIndex(1);
                    ComboBox_Grade();
                }
                if (rs.getInt("sesekan") > 0) {
                    model.addRow(new Object[]{table_hasil.getRowCount() + 1, "GNS SSK", 0, rs.getInt("sesekan")});
                }
                if (rs.getInt("hancuran") > 0) {
                    model.addRow(new Object[]{table_hasil.getRowCount() + 1, "GNS HC", 0, rs.getInt("hancuran")});
                }
                if (rs.getInt("rontokan") > 0) {
                    model.addRow(new Object[]{table_hasil.getRowCount() + 1, qc + " RONT LP", 0, rs.getInt("rontokan")});
                }
                if (rs.getInt("bonggol") > 0) {
                    model.addRow(new Object[]{table_hasil.getRowCount() + 1, qc + " BGL", 0, rs.getInt("bonggol")});
                }
                if (rs.getInt("serabut") > 0) {
                    model.addRow(new Object[]{table_hasil.getRowCount() + 1, qc + " SRBT", 0, rs.getInt("serabut")});
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_GradingBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void getEditData() {
        try {
            String tanggal_grading = "SELECT `tanggal_grading`, `pekerja_grading` FROM `tb_bahan_jadi_masuk` WHERE `kode_asal` = '" + label_LP.getText() + "'";
            ResultSet result = Utility.db.getStatement().executeQuery(tanggal_grading);
            while (result.next()) {
                Date_grading.setDate(result.getDate("tanggal_grading"));
                txt_pekerja.setText(result.getString("pekerja_grading"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_GradingBahanJadi.class.getName()).log(Level.SEVERE, null, ex);
        }

        DefaultTableModel model = (DefaultTableModel) table_hasil.getModel();
        Object[] row = new Object[4];
        for (int i = 0; i < JPanel_BahanJadiMasuk.table_hasil_grading.getRowCount(); i++) {
            row[0] = i + 1;
            row[1] = JPanel_BahanJadiMasuk.table_hasil_grading.getValueAt(i, 0);
            row[2] = JPanel_BahanJadiMasuk.table_hasil_grading.getValueAt(i, 1);
            row[3] = JPanel_BahanJadiMasuk.table_hasil_grading.getValueAt(i, 2);
            model.addRow(row);
        }
        count();
    }

    public void count() {
        tot_keping = 0;
        tot_gram = 0;
        int x = table_hasil.getRowCount();
        for (int i = 0; i < x; i++) {
            try {
                tot_keping = tot_keping + Integer.valueOf(table_hasil.getValueAt(i, 2).toString());
                tot_gram = tot_gram + Integer.valueOf(table_hasil.getValueAt(i, 3).toString());
            } catch (NullPointerException e) {
                JOptionPane.showMessageDialog(this, "Tidak Boleh ada data yang kosong !");
            }
        }
        label_total_hasil.setText(Integer.toString(x));
        label_total_keping_hasil.setText(Integer.toString(tot_keping));
        label_total_gram_hasil.setText(Integer.toString(tot_gram));
        ColumnsAutoSizer.sizeColumnsToFit(table_hasil);
    }

    public boolean cekDuplicateGrade(String grade) {
        int i = table_hasil.getRowCount();
        for (int j = 0; j < i; j++) {
            if (ComboBox_kode_grading.getSelectedItem().toString().equals(table_hasil.getValueAt(j, 1).toString())) {
                return true;
            }
        }
        return false;
    }

    public void addData() {
        DefaultTableModel model = (DefaultTableModel) table_hasil.getModel();
        boolean add = true;
        int kpg = 0, gram = 0;
        String kode_grade = ComboBox_GNS_NS.getSelectedItem().toString() + " " + ComboBox_kode_grading.getSelectedItem().toString();
        try {
            kpg = Integer.valueOf(txt_Jumlah_keping.getText());
            gram = Integer.valueOf(txt_jumlah_berat.getText());
            if (cekDuplicateGrade(kode_grade)) {
                ComboBox_kode_grading.requestFocus();
                throw new Exception("Grade " + ComboBox_kode_grading.getSelectedItem().toString() + "sudah ada !");
            } else if (txt_Jumlah_keping.getText() == null || "".equals(txt_Jumlah_keping.getText())) {
                txt_Jumlah_keping.requestFocus();
                throw new Exception("Keping TIDAK BOLEH Kosong");
            } else if (txt_jumlah_berat.getText() == null || "".equals(txt_jumlah_berat.getText()) || "0".equals(txt_jumlah_berat.getText())) {
                txt_jumlah_berat.requestFocus();
                throw new Exception("Gram TIDAK BOLEH Kosong / 0");
            } else if (kpg > gram) {
                txt_jumlah_berat.requestFocus();
                throw new Exception("Jumlah keping tidak bisa lebih besar dari beratnya");
            } else if (kpg == 0) {
                if (!ComboBox_kode_grading.getSelectedItem().toString().contains("SRT KCL")
                        && !ComboBox_kode_grading.getSelectedItem().toString().contains("SRT PDK")
                        && !ComboBox_kode_grading.getSelectedItem().toString().contains("SWR")
                        && !ComboBox_kode_grading.getSelectedItem().toString().contains("STR KNG")
                        && !ComboBox_kode_grading.getSelectedItem().toString().contains("RONT GRD")
                        && !ComboBox_kode_grading.getSelectedItem().toString().contains("RONT LP")
                        && !ComboBox_kode_grading.getSelectedItem().toString().contains("SSK")
                        && !ComboBox_kode_grading.getSelectedItem().toString().contains("HC")
                        && !ComboBox_kode_grading.getSelectedItem().toString().contains("HCRN KTR")
                        && !ComboBox_kode_grading.getSelectedItem().toString().contains("MESS")
                        && !ComboBox_kode_grading.getSelectedItem().toString().contains("BGL")
                        && !ComboBox_kode_grading.getSelectedItem().toString().contains("SRBT")
                        && !ComboBox_kode_grading.getSelectedItem().toString().contains("F1")
                        && !ComboBox_kode_grading.getSelectedItem().toString().contains("F2")
                        && !ComboBox_kode_grading.getSelectedItem().toString().contains("F3")
                        && !ComboBox_kode_grading.getSelectedItem().toString().contains("FT")
                        && !ComboBox_kode_grading.getSelectedItem().toString().contains("K3")
                        && !ComboBox_kode_grading.getSelectedItem().toString().contains("KK1 (PC)")
                        && !ComboBox_kode_grading.getSelectedItem().toString().contains("KK2 (PC)")
                        && !ComboBox_kode_grading.getSelectedItem().toString().contains("KK3 (PC)")
                        && !ComboBox_kode_grading.getSelectedItem().toString().contains("FEET 1+2")
                        && !ComboBox_kode_grading.getSelectedItem().toString().contains("B 3")
                        && !ComboBox_kode_grading.getSelectedItem().toString().contains("B 3 R")
                        && !ComboBox_kode_grading.getSelectedItem().toString().contains("B3")
                        && !ComboBox_kode_grading.getSelectedItem().toString().contains("B2")
                        && !ComboBox_kode_grading.getSelectedItem().toString().contains("FEET 3 R")
                        && !ComboBox_kode_grading.getSelectedItem().toString().contains("ZLX")) {
//                    throw new Exception("Maaf, keping yang di perbolehkan 0 adalah :\n"
//                            + "SRT KCL, SRT PNDK, SWR, STR KNG, RONT GRD, RONT LP, SSK, HC, HCRN Ktr, MESS1, MESS2, BGL, SRBT, \n"
//                            + "F1, F2, F3, B2, B3, FT, K3, KK1 (PC), KK2 (PC), KK3 (PC), FEET 1+2, B 3, B 3 R, B3, B2, FEET 3 R, ZLX");
                }
            }
        } catch (Exception e) {
            add = false;
            JOptionPane.showMessageDialog(this, e);
        }

        if (add) {
            model.addRow(new Object[]{table_hasil.getRowCount() + 1, kode_grade, kpg, gram});
            ComboBox_kode_grading.requestFocus();
            txt_Jumlah_keping.setText("");
            txt_jumlah_berat.setText("");
            ColumnsAutoSizer.sizeColumnsToFit(table_hasil);
            count();
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
        jScrollPane2 = new javax.swing.JScrollPane();
        table_hasil = new javax.swing.JTable();
        label_total_hasil = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        label_total_keping_hasil = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        label_total_gram_hasil = new javax.swing.JLabel();
        button_tambah = new javax.swing.JButton();
        button_kurang = new javax.swing.JButton();
        button_save = new javax.swing.JButton();
        button_count = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        Date_grading = new com.toedter.calendar.JDateChooser();
        label_LP = new javax.swing.JLabel();
        ComboBox_kode_grading = new javax.swing.JComboBox<>();
        txt_Jumlah_keping = new javax.swing.JTextField();
        txt_jumlah_berat = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txt_pekerja = new javax.swing.JTextField();
        button_pekerja = new javax.swing.JButton();
        ComboBox_GNS_NS = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("GRADING BAHAN JADI");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        table_hasil.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_hasil.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Grade", "Kpg", "Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_hasil.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(table_hasil);
        if (table_hasil.getColumnModel().getColumnCount() > 0) {
            table_hasil.getColumnModel().getColumn(0).setMaxWidth(30);
        }

        label_total_hasil.setBackground(new java.awt.Color(255, 255, 255));
        label_total_hasil.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_hasil.setText("0");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Total :");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("Keping :");

        label_total_keping_hasil.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_hasil.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_keping_hasil.setText("0");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText("Gram :");

        label_total_gram_hasil.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_hasil.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_gram_hasil.setText("0");

        button_tambah.setBackground(new java.awt.Color(255, 255, 255));
        button_tambah.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_tambahActionPerformed(evt);
            }
        });

        button_kurang.setBackground(new java.awt.Color(255, 255, 255));
        button_kurang.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_kurang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_kurangActionPerformed(evt);
            }
        });

        button_save.setBackground(new java.awt.Color(255, 255, 255));
        button_save.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_save.setText("Save");
        button_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveActionPerformed(evt);
            }
        });

        button_count.setBackground(new java.awt.Color(255, 255, 255));
        button_count.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_count.setText("Count");
        button_count.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_countActionPerformed(evt);
            }
        });

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel6.setText("Input Data Grading Barang Jadi");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("No. Laporan Produksi :");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Tanggal Grading :");

        Date_grading.setBackground(new java.awt.Color(255, 255, 255));
        Date_grading.setDate(new Date());
        Date_grading.setDateFormatString("dd MMM yyyy");
        Date_grading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_grading.setMaxSelectableDate(new Date());

        label_LP.setBackground(new java.awt.Color(255, 255, 255));
        label_LP.setText("-");

        ComboBox_kode_grading.setEditable(true);
        ComboBox_kode_grading.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_kode_grading.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ComboBox_kode_gradingKeyPressed(evt);
            }
        });

        txt_Jumlah_keping.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_Jumlah_keping.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_Jumlah_kepingKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_Jumlah_kepingKeyTyped(evt);
            }
        });

        txt_jumlah_berat.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_jumlah_berat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_jumlah_beratKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_jumlah_beratKeyTyped(evt);
            }
        });

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Grade Bulu :");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("Keping :");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("Berat :");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("Pekerja :");

        txt_pekerja.setEditable(false);
        txt_pekerja.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_pekerja.setBackground(new java.awt.Color(255, 255, 255));
        button_pekerja.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_pekerja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_pekerjaActionPerformed(evt);
            }
        });

        ComboBox_GNS_NS.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_GNS_NS.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "GNS", "Non NS" }));
        ComboBox_GNS_NS.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ComboBox_GNS_NSItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_hasil)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_keping_hasil)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_hasil)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_save))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(button_count)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_kurang, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_LP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Date_grading, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txt_pekerja)
                                .addGap(0, 0, 0)
                                .addComponent(button_pekerja, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(ComboBox_kode_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel11)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ComboBox_GNS_NS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel12)
                                    .addComponent(txt_Jumlah_keping, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel15)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(txt_jumlah_berat, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(button_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_pekerja, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(button_pekerja, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_count, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_kurang, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12)
                    .addComponent(jLabel15)
                    .addComponent(ComboBox_GNS_NS, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt_Jumlah_keping, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_jumlah_berat, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(ComboBox_kode_grading)
                    .addComponent(button_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_save, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(label_total_hasil)
                    .addComponent(jLabel8)
                    .addComponent(label_total_keping_hasil)
                    .addComponent(jLabel10)
                    .addComponent(label_total_gram_hasil))
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
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button_tambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_tambahActionPerformed
        addData();
    }//GEN-LAST:event_button_tambahActionPerformed

    private void button_kurangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_kurangActionPerformed
        int i = table_hasil.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) table_hasil.getModel();
        if (i != -1) {
            model.removeRow(i);
            for (int j = 0; j < table_hasil.getRowCount(); j++) {
                model.setValueAt(j + 1, j, 0);
            }
        }
        ColumnsAutoSizer.sizeColumnsToFit(table_hasil);
        count();
    }//GEN-LAST:event_button_kurangActionPerformed

    private void button_countActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_countActionPerformed
        // TODO add your handling code here:
        count();
    }//GEN-LAST:event_button_countActionPerformed

    private void button_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_saveActionPerformed
        // TODO add your handling code here:
        boolean check = true;
        count();
        try {
            if (table_hasil.getRowCount() == 0) {
                throw new Exception("Anda Belum memasukkan data Box yang baru");
            } else if (Date_grading.getDate() == null) {
                throw new Exception("Maaf tgl grading harus dipilih");
            } else if (tgl_masuk.after(Date_grading.getDate())) {
                throw new Exception("Maaf tgl grading salah, Anda memasukkan tgl grading sebelum tgl Masuk");
            } else if (Date_grading.getDate().after(new Date(tgl_masuk.getTime() + (30 * 24 * 60 * 60 * 1000l)))) {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Apakah kamu yakin tgl grading 1 bulan setelah tgl masuk??", "Warning", 0);
                if (dialogResult == JOptionPane.NO_OPTION) {
                    check = false;
                }
            }
            int gram_hasil = Integer.valueOf(label_total_gram_hasil.getText());
            float keping_hasil = Float.valueOf(label_total_keping_hasil.getText());
            if (keping_awal != keping_hasil) {
                int dialogResult = JOptionPane.showConfirmDialog(this, "jumlah keping hasil grading berbeda, apakah ingin melanjutkan??", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    JDialog_otorisasi_gradingBJ dialog = new JDialog_otorisasi_gradingBJ(new javax.swing.JFrame(), true);
                    dialog.pack();
                    dialog.setLocationRelativeTo(this);
                    dialog.setVisible(true);
                    dialog.setEnabled(true);
                    check = dialog.akses();
                } else {
                    check = false;
                }
            }
        } catch (Exception e) {
            check = false;
            JOptionPane.showMessageDialog(this, e);
        }

        if (check) {
            try {
                boolean check_duplicate_grade = false;
                Utility.db.getConnection().setAutoCommit(false);
                if ("edit".equals(status)) {
                    String delete = "DELETE FROM `tb_grading_bahan_jadi` WHERE `kode_asal_bahan_jadi` = '" + label_LP.getText() + "'";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(delete);
                }
                for (int i = 0; i < table_hasil.getRowCount(); i++) {
                    sql = "SELECT * FROM `tb_grading_bahan_jadi` WHERE `kode_asal_bahan_jadi` = '" + label_LP.getText() + "' AND `grade_bahan_jadi` = (SELECT `kode` FROM `tb_grade_bahan_jadi` WHERE `kode_grade` = '" + table_hasil.getValueAt(i, 1) + "')";
                    rs = Utility.db.getStatement().executeQuery(sql);
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(this, "grade " + table_hasil.getValueAt(i, 1) + " sudah ada !");
                        check_duplicate_grade = true;
                        break;
                    } else {
                        String update = "UPDATE `tb_bahan_jadi_masuk` SET "
                                + "`tanggal_grading`= ? , "
                                + "`pekerja_grading` = ? "
                                + "WHERE `kode_asal` = ?";
//                        Utility.db.getConnection().createStatement();
                        PreparedStatement preparedStmt = Utility.db.getConnection().prepareStatement(update);
                        preparedStmt.setString(1, dateFormat.format(Date_grading.getDate()));
                        preparedStmt.setString(2, txt_pekerja.getText());
                        preparedStmt.setString(3, label_LP.getText());
                        preparedStmt.execute();

                        String insert = "INSERT INTO `tb_grading_bahan_jadi`(`kode_asal_bahan_jadi`, `grade_bahan_jadi`, `keping`, `gram`) "
                                + "VALUES ('" + label_LP.getText() + "',(SELECT `kode` FROM `tb_grade_bahan_jadi` WHERE `kode_grade` = '" + table_hasil.getValueAt(i, 1) + "'),'" + table_hasil.getValueAt(i, 2) + "','" + table_hasil.getValueAt(i, 3) + "')";
                        Utility.db.getConnection().createStatement();
                        Utility.db.getStatement().executeUpdate(insert);
                        check_duplicate_grade = false;
                    }
                }
                if (!check_duplicate_grade) {
                    Utility.db.getConnection().commit();
                    JOptionPane.showMessageDialog(this, "Data Grading berhasil di simpan");
                    JPanel_BoxBahanJadi.button_search_Box.doClick();
                    this.dispose();
                } else {
                    try {
                        Utility.db.getConnection().rollback();
                    } catch (SQLException ex) {
                        Logger.getLogger(JDialog_reProcess.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            } catch (SQLException e) {
                try {
                    Utility.db.getConnection().rollback();
                } catch (SQLException ex) {
                    Logger.getLogger(JDialog_reProcess.class.getName()).log(Level.SEVERE, null, ex);
                }
                JOptionPane.showMessageDialog(this, e);
                Logger.getLogger(JDialog_reProcess.class.getName()).log(Level.SEVERE, null, e);
            } finally {
                try {
                    Utility.db.getConnection().setAutoCommit(true);
                } catch (SQLException ex) {
                    Logger.getLogger(JDialog_reProcess.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_button_saveActionPerformed

    private void txt_jumlah_beratKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_jumlah_beratKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            addData();
        }
    }//GEN-LAST:event_txt_jumlah_beratKeyPressed

    private void ComboBox_kode_gradingKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ComboBox_kode_gradingKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            addData();
        }
    }//GEN-LAST:event_ComboBox_kode_gradingKeyPressed

    private void txt_Jumlah_kepingKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_Jumlah_kepingKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            addData();
        }
    }//GEN-LAST:event_txt_Jumlah_kepingKeyPressed

    private void button_pekerjaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_pekerjaActionPerformed
        // TODO add your handling code here:
        Browse_Karyawan dialog = new Browse_Karyawan(new javax.swing.JFrame(), true, null);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);

        int x = Browse_Karyawan.table_list_karyawan.getSelectedRow();
        if (x != -1) {
            txt_pekerja.setText(Browse_Karyawan.table_list_karyawan.getValueAt(x, 1).toString());
        }
    }//GEN-LAST:event_button_pekerjaActionPerformed

    private void ComboBox_GNS_NSItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ComboBox_GNS_NSItemStateChanged
        // TODO add your handling code here:
        ComboBox_Grade();
    }//GEN-LAST:event_ComboBox_GNS_NSItemStateChanged

    private void txt_Jumlah_kepingKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_Jumlah_kepingKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_Jumlah_kepingKeyTyped

    private void txt_jumlah_beratKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_jumlah_beratKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_jumlah_beratKeyTyped

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_GNS_NS;
    private javax.swing.JComboBox<String> ComboBox_kode_grading;
    private com.toedter.calendar.JDateChooser Date_grading;
    private javax.swing.JButton button_count;
    private javax.swing.JButton button_kurang;
    private javax.swing.JButton button_pekerja;
    private javax.swing.JButton button_save;
    private javax.swing.JButton button_tambah;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel label_LP;
    private javax.swing.JLabel label_total_gram_hasil;
    private javax.swing.JLabel label_total_hasil;
    private javax.swing.JLabel label_total_keping_hasil;
    private javax.swing.JTable table_hasil;
    private javax.swing.JTextField txt_Jumlah_keping;
    private javax.swing.JTextField txt_jumlah_berat;
    private javax.swing.JTextField txt_pekerja;
    // End of variables declaration//GEN-END:variables
}
