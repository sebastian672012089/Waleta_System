package waleta_system.BahanBaku;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;
import waleta_system.Packing.JDialog_pengiriman;

public class JDialog_CreateKartuCampuran extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    float total_keping_keluar = 0, tot_keping_daftarBox = 0;
    float total_gram_keluar = 0, tot_gram_daftarBox = 0;
    String status = null;
    ArrayList<String> DataBoxAwal = new ArrayList<>();

    public JDialog_CreateKartuCampuran(java.awt.Frame parent, boolean modal, String status) {
        super(parent, modal);
        this.status = status;
        initComponents();
        txt_kode_kartu.setText(kode_cmp());
    }

    public void getData() {
    }

    public String kode_cmp() {
        String kode_kartu = null;
        try {
            int KodeNumber, LastNumber = 0;
            boolean check = true;
            String AppendZero = "";
            String get_last = "SELECT COUNT(`kode_kartu_cmp`) AS 'kode' FROM `tb_kartu_cmp` WHERE YEAR(`tanggal`) = " + new SimpleDateFormat("yyyy").format(new Date());
            ResultSet result_last = Utility.db.getStatement().executeQuery(get_last);
            if (result_last.next()) {
                LastNumber = result_last.getInt("kode") + 1;
            }
            //add zero digit before number
            if (LastNumber < 10) {
                AppendZero = "00";
            } else if (LastNumber < 100) {
                AppendZero = "0";
            } else if (LastNumber < 1000) {
                AppendZero = "";
            }
            kode_kartu = new SimpleDateFormat("yy").format(new Date()) + "CMP" + (AppendZero + LastNumber);
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_CreateKartuCampuran.class.getName()).log(Level.SEVERE, null, ex);
        }
        return kode_kartu;
    }

    public void count() {
        total_keping_keluar = 0;
        total_gram_keluar = 0;
        int x = table_detail.getRowCount();
        for (int i = 0; i < x; i++) {
            try {
                total_keping_keluar = total_keping_keluar + Integer.valueOf(table_detail.getValueAt(i, 3).toString());
                total_gram_keluar = total_gram_keluar + Float.valueOf(table_detail.getValueAt(i, 4).toString());
            } catch (NullPointerException e) {
                JOptionPane.showMessageDialog(this, "Tidak Boleh ada data yang kosong !");
            }
        }
        label_total_data.setText(Integer.toString(x));
        label_total_keping.setText(decimalFormat.format(total_keping_keluar));
        label_total_gram.setText(decimalFormat.format(total_gram_keluar));
        ColumnsAutoSizer.sizeColumnsToFit(table_detail);
    }

    public boolean INSERT_KARTU_CMP() {
        boolean INSERT_KARTU_CMP = true;
        try {
            Utility.db.getConnection().setAutoCommit(false);
            sql = "INSERT INTO `tb_kartu_cmp`(`kode_kartu_cmp`, `tanggal`, `catatan`) "
                    + "VALUES ('" + txt_kode_kartu.getText() + "','" + dateFormat.format(Date_kartu.getDate()) + "','" + txt_catatan.getText() + "')";
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(sql)) > 0) {
                for (int i = 0; i < table_detail.getRowCount(); i++) {
                    sql = "INSERT INTO `tb_kartu_cmp_detail`(`kode_kartu_cmp`, `no_grading`, `keping`, `gram`) "
                            + "VALUES ('" + txt_kode_kartu.getText() + "','" + table_detail.getValueAt(i, 0) + "','" + table_detail.getValueAt(i, 3) + "','" + table_detail.getValueAt(i, 4) + "')";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(sql);
                }
                Utility.db.getConnection().commit();
                this.dispose();
                JOptionPane.showMessageDialog(this, "Data Saved !");
            }
            INSERT_KARTU_CMP = true;
        } catch (SQLException | NullPointerException ex) {
            try {
                Utility.db.getConnection().rollback();
            } catch (SQLException e) {
                Logger.getLogger(JDialog_pengiriman.class.getName()).log(Level.SEVERE, null, e);
            }
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JDialog_pengiriman.class.getName()).log(Level.SEVERE, null, ex);
            INSERT_KARTU_CMP = false;
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_CreateKartuCampuran.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return INSERT_KARTU_CMP;
    }

    public void New() {
        try {
            if (INSERT_KARTU_CMP()) {
                int Keping_grading = 0, Gram_grading = 0;
                String grade = "";
                String sql3 = "SELECT "
                        + "`tb_grading_bahan_baku`.`kode_grade`, "
                        + "SUM(`keping`) AS 'total_kpg', "
                        + "SUM(`gram`) AS 'total_gram', "
                        + "COUNT(DISTINCT(`tb_grading_bahan_baku`.`kode_grade`)) AS 'jumlah_grade' "
                        + "FROM `tb_kartu_cmp_detail` "
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_kartu_cmp_detail`.`no_grading` =  `tb_grading_bahan_baku`.`no_grading` "
                        + "WHERE `kode_kartu_cmp` = '" + txt_kode_kartu.getText() + "'";
                PreparedStatement pst1 = Utility.db.getConnection().prepareStatement(sql3);
                ResultSet rs3 = pst1.executeQuery();
                if (rs3.next()) {
                    grade = rs3.getString("kode_grade");
                    Keping_grading = rs3.getInt("total_kpg");
                    Gram_grading = rs3.getInt("total_gram");

                    if (rs3.getInt("jumlah_grade") > 1) {
                        grade = "Ragam Serat";
                    }
                }

                double KA = 0;
                String sql1 = "SELECT AVG(`tb_bahan_baku_masuk`.`kadar_air_bahan_baku`) AS 'rata2_KA' "
                        + "FROM `tb_kartu_cmp_detail` \n"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_grading_bahan_baku`.`no_grading` = `tb_kartu_cmp_detail`.`no_grading`\n"
                        + "LEFT JOIN `tb_bahan_baku_masuk` ON `tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`\n"
                        + "WHERE `kode_kartu_cmp` = '" + txt_kode_kartu.getText() + "'";
                ResultSet rs1 = Utility.db.getStatement().executeQuery(sql1);
                while (rs1.next()) {
                    KA = rs1.getDouble("rata2_KA");
                }

                decimalFormat = Utility.DecimalFormatUS(decimalFormat);
                decimalFormat.setMaximumFractionDigits(2);
                decimalFormat.setGroupingUsed(false);
                String sql2 = "INSERT INTO `tb_bahan_baku_masuk`(`no_kartu_waleta`, `no_registrasi`, `kode_supplier`, `tgl_kh`, `tgl_masuk`, `tgl_panen`, `tgl_timbang`, `kadar_air_waleta`, `kadar_air_bahan_baku`, `berat_awal`, `keping_real`, `berat_real`) "
                        + "VALUES ('" + txt_kode_kartu.getText() + "','1703','ZZ','" + dateFormat.format(Date_kartu.getDate()) + "','" + dateFormat.format(Date_kartu.getDate()) + "','" + dateFormat.format(Date_kartu.getDate()) + "','" + dateFormat.format(Date_kartu.getDate()) + "', " + decimalFormat.format(KA) + ", " + decimalFormat.format(KA) + ", 0, " + Keping_grading + ", " + Gram_grading + ")";
//                JOptionPane.showMessageDialog(this, sql2);
                Utility.db.getConnection().createStatement();
                if ((Utility.db.getStatement().executeUpdate(sql2)) > 0) {
                    JOptionPane.showMessageDialog(this, "No kartu bahan baku baru sukses");
                    String insert_ct1 = "INSERT INTO `tb_bahan_baku_masuk_cheat`(`no_kartu_waleta`, `no_registrasi`, `kode_supplier`, `tgl_kh`, `tgl_masuk`, `tgl_panen`, `tgl_timbang`, `kadar_air_waleta`, `kadar_air_bahan_baku`, `berat_awal`, `keping_real`, `berat_real`) "
                            + "VALUES ('" + txt_kode_kartu.getText() + "','1703','ZZ','" + dateFormat.format(Date_kartu.getDate()) + "','" + dateFormat.format(Date_kartu.getDate()) + "','" + dateFormat.format(Date_kartu.getDate()) + "','" + dateFormat.format(Date_kartu.getDate()) + "', " + decimalFormat.format(KA) + ", " + decimalFormat.format(KA) + ", 0, " + Keping_grading + ", " + Gram_grading + ")";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(insert_ct1)) > 0) {
                        String sql4 = "INSERT INTO `tb_grading_bahan_baku`(`no_kartu_waleta`, `kode_grade`, `jumlah_keping`, `total_berat`) "
                                + "VALUES ('" + txt_kode_kartu.getText() + "','" + grade + "','" + Keping_grading + "','" + Gram_grading + "')";
                        Utility.db.getConnection().createStatement();
                        Utility.db.getStatement().executeUpdate(sql4);

                        String insert_grading_ct1 = "INSERT INTO `tb_grading_bahan_baku_cheat`(`no_kartu_waleta`, `kode_grade`, `jumlah_keping`, `total_berat`) "
                                + "VALUES ('" + txt_kode_kartu.getText() + "','" + grade + "','" + Keping_grading + "','" + Gram_grading + "')";
                        Utility.db.getConnection().createStatement();
                        Utility.db.getStatement().executeUpdate(insert_grading_ct1);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "ERROR");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JDialog_pengiriman.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Edit() {
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
        jLabel3 = new javax.swing.JLabel();
        txt_kode_kartu = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_detail = new javax.swing.JTable();
        label_total_data = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        label_total_keping = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        label_total_gram = new javax.swing.JLabel();
        button_delete = new javax.swing.JButton();
        Label_kode_grade = new javax.swing.JLabel();
        label_kode = new javax.swing.JLabel();
        button_Keluar_select_kartu = new javax.swing.JButton();
        label_keping = new javax.swing.JLabel();
        label_berat_keluar = new javax.swing.JLabel();
        txt_berat = new javax.swing.JTextField();
        txt_keping = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        label_kartu_waleta_keluar = new javax.swing.JLabel();
        label_kartu_waleta_keluar1 = new javax.swing.JLabel();
        button_insert = new javax.swing.JButton();
        label_kartu_waleta_keluar2 = new javax.swing.JLabel();
        label_no_kartu = new javax.swing.JLabel();
        button_cancel = new javax.swing.JButton();
        Date_kartu = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        button_save = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txt_catatan = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        ComboBox_jenis = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Bahan Baku Keluar");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel3.setText("Kode Kartu Campuran :");

        txt_kode_kartu.setEditable(false);
        txt_kode_kartu.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Detail Kartu Campuran", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N

        table_detail.setAutoCreateRowSorter(true);
        table_detail.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_detail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode", "No Kartu", "Grade", "Keping", "Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_detail.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table_detail);

        label_total_data.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_data.setText("0");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Total Data :");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Total Kpg :");

        label_total_keping.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_keping.setText("0");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel8.setText("Total Gram :");

        label_total_gram.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram.setText("0");

        button_delete.setBackground(new java.awt.Color(255, 255, 255));
        button_delete.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete.setText("Delete");
        button_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_deleteActionPerformed(evt);
            }
        });

        Label_kode_grade.setBackground(new java.awt.Color(255, 255, 255));
        Label_kode_grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Label_kode_grade.setText("Kode Grade");
        Label_kode_grade.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        label_kode.setBackground(new java.awt.Color(255, 255, 255));
        label_kode.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kode.setText("Kode");
        label_kode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        button_Keluar_select_kartu.setBackground(new java.awt.Color(255, 255, 255));
        button_Keluar_select_kartu.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_Keluar_select_kartu.setText("Pilih Kartu Baku");
        button_Keluar_select_kartu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_Keluar_select_kartuActionPerformed(evt);
            }
        });

        label_keping.setBackground(new java.awt.Color(255, 255, 255));
        label_keping.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_keping.setText("Jumlah Keping :");

        label_berat_keluar.setBackground(new java.awt.Color(255, 255, 255));
        label_berat_keluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_berat_keluar.setText("Berat :");

        txt_berat.setEditable(false);
        txt_berat.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        txt_keping.setEditable(false);
        txt_keping.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel22.setText("Gram");

        label_kartu_waleta_keluar.setBackground(new java.awt.Color(255, 255, 255));
        label_kartu_waleta_keluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kartu_waleta_keluar.setText("Kode :");

        label_kartu_waleta_keluar1.setBackground(new java.awt.Color(255, 255, 255));
        label_kartu_waleta_keluar1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kartu_waleta_keluar1.setText("Kode Grade :");

        button_insert.setBackground(new java.awt.Color(255, 255, 255));
        button_insert.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_insert.setText("insert");
        button_insert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_insertActionPerformed(evt);
            }
        });

        label_kartu_waleta_keluar2.setBackground(new java.awt.Color(255, 255, 255));
        label_kartu_waleta_keluar2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_kartu_waleta_keluar2.setText("No. Kartu Waleta :");

        label_no_kartu.setBackground(new java.awt.Color(255, 255, 255));
        label_no_kartu.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_no_kartu.setText("No. Kartu Waleta");
        label_no_kartu.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_keping)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_berat_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_keping, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_kartu_waleta_keluar1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_kartu_waleta_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_kartu_waleta_keluar2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(button_Keluar_select_kartu)
                            .addComponent(Label_kode_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_kode, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addComponent(button_delete)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(button_insert))
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(txt_berat)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel22))
                                    .addComponent(txt_keping, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(label_no_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_kartu_waleta_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_kode, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_kartu_waleta_keluar2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_no_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_kartu_waleta_keluar1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Label_kode_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_Keluar_select_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_keping, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_keping, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_berat, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_berat_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(button_insert, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_total_keping)
                    .addComponent(jLabel8)
                    .addComponent(label_total_gram)
                    .addComponent(label_total_data)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        button_cancel.setBackground(new java.awt.Color(255, 255, 255));
        button_cancel.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_cancel.setText("CANCEL");
        button_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_cancelActionPerformed(evt);
            }
        });

        Date_kartu.setBackground(new java.awt.Color(255, 255, 255));
        Date_kartu.setDateFormatString("dd MMMM yyyy");
        Date_kartu.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel5.setText("Tanggal :");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel15.setText("Catatan :");

        button_save.setBackground(new java.awt.Color(255, 255, 255));
        button_save.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_save.setText("SAVE");
        button_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel2.setText("DEPARTEMEN BAHAN BAKU");

        txt_catatan.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        jLabel1.setText("MEMBUAT KARTU CAMPURAN");

        ComboBox_jenis.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_jenis.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CMP", "RGM" }));
        ComboBox_jenis.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ComboBox_jenisItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(571, 571, 571)
                        .addComponent(button_cancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_save))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(txt_kode_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ComboBox_jenis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(txt_catatan, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(13, 13, 13)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_kode_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_jenis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_catatan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_save, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_deleteActionPerformed
        // TODO add your handling code here:
        int i = table_detail.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) table_detail.getModel();
        if (i != -1) {
            model.removeRow(table_detail.getRowSorter().convertRowIndexToModel(i));
            ColumnsAutoSizer.sizeColumnsToFit(table_detail);
            count();
        }
    }//GEN-LAST:event_button_deleteActionPerformed

    private void button_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_button_cancelActionPerformed

    private void button_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_saveActionPerformed
        boolean check = true;
        for (int i = 0; i < table_detail.getRowCount(); i++) {
            if (!table_detail.getValueAt(0, 2).toString().equals(table_detail.getValueAt(i, 2).toString())) {
                JOptionPane.showMessageDialog(this, "Grade HARUS sama semua !!");
                check = false;
                break;
            }
        }

        if (txt_kode_kartu.getText() == null
                || Date_kartu.getDate() == null
                || table_detail.getRowCount() < 1) {
            JOptionPane.showMessageDialog(this, "Silahkan Lengkapi Data diatas !!");
            check = false;
        }

        if (check) {
            if ("new".equals(status)) {
                New();
            } else if ("edit".equals(status)) {
                Edit();
            }
        }
    }//GEN-LAST:event_button_saveActionPerformed

    private void button_Keluar_select_kartuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_Keluar_select_kartuActionPerformed
        Stock_Bahan_Baku dialog = new Stock_Bahan_Baku(new javax.swing.JFrame(), true, "kartu campuran");
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
    }//GEN-LAST:event_button_Keluar_select_kartuActionPerformed

    private void button_insertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_insertActionPerformed
        DefaultTableModel model = (DefaultTableModel) table_detail.getModel();
        boolean check = true;
        for (int i = 0; i < table_detail.getRowCount(); i++) {
            if (label_kode.getText().equals(table_detail.getValueAt(i, 0).toString())) {
                JOptionPane.showMessageDialog(this, label_no_kartu.getText() + " dan " + Label_kode_grade.getText() + " sudah masuk");
                check = false;
                break;
            }
        }
        if (check) {
            model.addRow(new Object[]{
                label_kode.getText(),
                label_no_kartu.getText(),
                Label_kode_grade.getText(),
                txt_keping.getText(),
                txt_berat.getText()});
            count();
            ColumnsAutoSizer.sizeColumnsToFit(table_detail);
        }
    }//GEN-LAST:event_button_insertActionPerformed

    private void ComboBox_jenisItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ComboBox_jenisItemStateChanged
        // TODO add your handling code here:
        if (ComboBox_jenis.getSelectedIndex() == 0) {
            String b = txt_kode_kartu.getText().replace("RGM", "CMP");
            txt_kode_kartu.setText(b);
            System.out.println(b);
        } else if (ComboBox_jenis.getSelectedIndex() == 1) {
            String b = txt_kode_kartu.getText().replace("CMP", "RGM");
            txt_kode_kartu.setText(b);
            System.out.println(b);
        }
    }//GEN-LAST:event_ComboBox_jenisItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_jenis;
    private com.toedter.calendar.JDateChooser Date_kartu;
    public static javax.swing.JLabel Label_kode_grade;
    private javax.swing.JButton button_Keluar_select_kartu;
    private javax.swing.JButton button_cancel;
    private javax.swing.JButton button_delete;
    public static javax.swing.JButton button_insert;
    private javax.swing.JButton button_save;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_berat_keluar;
    private javax.swing.JLabel label_kartu_waleta_keluar;
    private javax.swing.JLabel label_kartu_waleta_keluar1;
    private javax.swing.JLabel label_kartu_waleta_keluar2;
    private javax.swing.JLabel label_keping;
    public static javax.swing.JLabel label_kode;
    public static javax.swing.JLabel label_no_kartu;
    private javax.swing.JLabel label_total_data;
    private javax.swing.JLabel label_total_gram;
    private javax.swing.JLabel label_total_keping;
    private javax.swing.JTable table_detail;
    public static javax.swing.JTextField txt_berat;
    private javax.swing.JTextField txt_catatan;
    public static javax.swing.JTextField txt_keping;
    private javax.swing.JTextField txt_kode_kartu;
    // End of variables declaration//GEN-END:variables
}
