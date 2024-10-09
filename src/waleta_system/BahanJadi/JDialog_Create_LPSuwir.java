package waleta_system.BahanJadi;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JDialog_Create_LPSuwir extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    String status, no_lp_suwir_lama;
    float total_keping_lp = 0, tot_keping = 0;
    float total_gram_lp = 0, tot_gram = 0;

    public JDialog_Create_LPSuwir(java.awt.Frame parent, boolean modal, String no_lp_suwir, String tanggal_lp, String gram_akhir) {
        super(parent, modal);
        initComponents();
        button_hapus.setIcon(Utility.ResizeImageIcon(new javax.swing.ImageIcon(getClass().getResource("/waleta_system/Images/delete-icon.png")), button_hapus.getWidth(), button_hapus.getHeight()));
        this.setResizable(false);
        if (no_lp_suwir == null) {
            Date_LPSuwir.setEnabled(true);
            label_lp_suwir.setText(getNoLPSuwir());
            status = "baru";
        } else {
            try {
                Date_LPSuwir.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(tanggal_lp));
            } catch (ParseException ex) {
                Logger.getLogger(JDialog_Create_LPSuwir.class.getName()).log(Level.SEVERE, null, ex);
            }
            txt_gram_akhir.setText(gram_akhir);
            Date_LPSuwir.setEnabled(false);
            button_refreshNoLP.setVisible(false);
            ComboBox_kode_nolp.setSelectedItem(no_lp_suwir.split("-")[0]);
            label_lp_suwir.setText("-" + no_lp_suwir.split("-")[1]);
            this.no_lp_suwir_lama = no_lp_suwir;
            status = "edit";
            try {
                tot_keping = 0;
                tot_gram = 0;
                DefaultTableModel model = (DefaultTableModel) Table_LPsuwir.getModel();
                model.setRowCount(0);
                sql = "SELECT * FROM `tb_box_bahan_jadi` LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`"
                        + "WHERE `status_terakhir` = '" + no_lp_suwir + "' ";
                rs = Utility.db.getStatement().executeQuery(sql);
                while (rs.next()) {
                    model.addRow(new Object[]{rs.getString("no_box"), rs.getString("kode_grade"), new SimpleDateFormat("dd MMMM yyyy").format(rs.getDate("tanggal_box")), rs.getInt("keping"), rs.getFloat("berat"), rs.getString("status_terakhir")});
                    tot_keping = tot_keping + rs.getInt("keping");
                    tot_gram = tot_gram + rs.getFloat("berat");
                }
                label_total_boxLP.setText(Integer.toString(Table_LPsuwir.getRowCount()));
                label_total_keping_LP.setText(decimalFormat.format(tot_keping));
                label_total_gram_LP.setText(decimalFormat.format(tot_gram));
                ColumnsAutoSizer.sizeColumnsToFit(Table_LPsuwir);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Create_LPSuwir.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        refresh_TabelDaftarBox();
    }

    public String getNoLPSuwir() {
        String no_lp = null;
        try {
            int last_number = 0;
            sql = "SELECT MAX(SUBSTRING(`no_lp_suwir`, IF(LENGTH(`no_lp_suwir`)=12, 9, 8), 4)+0) AS 'jumlah_lp' "
                    + "FROM `tb_lp_suwir` "
                    + "WHERE YEAR(`tgl_lp_suwir`) = '" + new SimpleDateFormat("yyyy").format(Date_LPSuwir.getDate()) + "' ";
            rs = Utility.db.getStatement().executeQuery(sql);
            if (rs.next()) {
                last_number = rs.getInt("jumlah_lp")+1;
            }
            no_lp = "-" + new SimpleDateFormat("yyMM").format(Date_LPSuwir.getDate()) + String.format("%04d", last_number);
        } catch (SQLException | NullPointerException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Date");
            Logger.getLogger(JDialog_Create_LPSuwir.class.getName()).log(Level.SEVERE, null, ex);
        }
        return no_lp;
    }

    public void refresh_TabelDaftarBox() {
        try {
            tot_keping = 0;
            tot_gram = 0;
            DefaultTableModel model = (DefaultTableModel) Table_daftarBox.getModel();
            model.setRowCount(0);
            sql = "SELECT * FROM `tb_box_bahan_jadi` LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`"
                    + "WHERE `no_box` LIKE '%" + txt_search_box.getText() + "%' AND `lokasi_terakhir` = 'GRADING' AND `berat` > 0";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("no_box"), rs.getString("kode_grade"), new SimpleDateFormat("dd MMMM yyyy").format(rs.getDate("tanggal_box")), rs.getInt("keping"), rs.getFloat("berat"), rs.getString("status_terakhir")});
                tot_keping = tot_keping + rs.getInt("keping");
                tot_gram = tot_gram + rs.getFloat("berat");
            }
            label_total_daftarBox.setText(Integer.toString(Table_daftarBox.getRowCount()));
            label_total_keping_daftarBox.setText(decimalFormat.format(tot_keping));
            label_total_gram_daftarBox.setText(decimalFormat.format(tot_gram));
            ColumnsAutoSizer.sizeColumnsToFit(Table_daftarBox);
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_Create_LPSuwir.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void count_LPSuwir() {
        total_keping_lp = 0;
        total_gram_lp = 0;
        int x = Table_LPsuwir.getRowCount();
        for (int i = 0; i < x; i++) {
            try {
                total_keping_lp = total_keping_lp + Integer.valueOf(Table_LPsuwir.getValueAt(i, 3).toString());
                total_gram_lp = total_gram_lp + Float.valueOf(Table_LPsuwir.getValueAt(i, 4).toString());
            } catch (NullPointerException e) {
                JOptionPane.showMessageDialog(this, "Tidak Boleh ada data yang kosong !");
            }
        }
        label_total_boxLP.setText(Integer.toString(x));
        label_total_keping_LP.setText(decimalFormat.format(total_keping_lp));
        label_total_gram_LP.setText(decimalFormat.format(total_gram_lp));
        ColumnsAutoSizer.sizeColumnsToFit(Table_LPsuwir);
    }

    public boolean CheckDuplicateBoxAsal(String no_box) {
        int i = Table_LPsuwir.getRowCount();
        for (int j = 0; j < i; j++) {
            if (no_box.equals(Table_LPsuwir.getValueAt(j, 0).toString())) {
                return true;
            }
        }
        return false;
    }

    public void baru() {
        try {
            decimalFormat = Utility.DecimalFormatUS(decimalFormat);
            decimalFormat.setMaximumFractionDigits(2);
            count_LPSuwir();
            Utility.db.getConnection().setAutoCommit(false);
            float gram_akhir = 0, total_kpg = 0;
            boolean check = true;
            try {
                Date tgl_lp = Date_LPSuwir.getDate();
                gram_akhir = Float.valueOf(txt_gram_akhir.getText());
            } catch (NumberFormatException | NullPointerException e) {
                check = false;
                JOptionPane.showMessageDialog(this, "Silahkan Lengkapi data diatas!");
            }
            button_refreshNoLP.doClick();
            if (check) {
                sql = "INSERT INTO `tb_lp_suwir`(`no_lp_suwir`, `tgl_lp_suwir`, `keping`, `gram`, `gram_akhir`) "
                        + "VALUES ('" + ComboBox_kode_nolp.getSelectedItem().toString() + label_lp_suwir.getText() + "','" + dateFormat.format(Date_LPSuwir.getDate()) + "','" + total_keping_lp + "'," + total_gram_lp + ", " + txt_gram_akhir.getText() + ")";
                Utility.db.getConnection().createStatement();
                Utility.db.getStatement().executeUpdate(sql);

                for (int i = 0; i < Table_LPsuwir.getRowCount(); i++) {
                    String update = "UPDATE `tb_box_bahan_jadi` SET `status_terakhir`='" + ComboBox_kode_nolp.getSelectedItem().toString() + label_lp_suwir.getText() + "',"
                            + "`lokasi_terakhir`='LP SUWIR',`tgl_proses_terakhir`='" + dateFormat.format(Date_LPSuwir.getDate()) + "' WHERE `no_box` = '" + Table_LPsuwir.getValueAt(i, 0) + "'";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(update);

                    String insert = "INSERT INTO `tb_lp_suwir_detail`(`no_lp_suwir`, `no_box`) "
                            + "VALUES ('" + ComboBox_kode_nolp.getSelectedItem().toString() + label_lp_suwir.getText() + "','" + Table_LPsuwir.getValueAt(i, 0) + "')";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(insert);
                }
                Utility.db.getConnection().commit();
                JOptionPane.showMessageDialog(this, "LP Suwir Created !");
                this.dispose();
            }
        } catch (SQLException | NullPointerException e) {
            try {
                Utility.db.getConnection().rollback();
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Create_LPSuwir.class.getName()).log(Level.SEVERE, null, ex);
            }
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JDialog_Create_LPSuwir.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Create_LPSuwir.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void edit() {
        try {
            decimalFormat = Utility.DecimalFormatUS(decimalFormat);
            decimalFormat.setMaximumFractionDigits(2);
            count_LPSuwir();
            Utility.db.getConnection().setAutoCommit(false);
            float gram_akhir = 0, total_kpg = 0;
            boolean check = true;
            try {
                Date tgl_lp = Date_LPSuwir.getDate();
                gram_akhir = Float.valueOf(txt_gram_akhir.getText());
            } catch (NumberFormatException | NullPointerException e) {
                check = false;
                JOptionPane.showMessageDialog(this, "Silahkan Lengkapi data diatas!");
            }
            if (check) {
                //balikin no box yang terpakai di lp suwir
                String update_box = "UPDATE `tb_box_bahan_jadi` SET `status_terakhir`='edit lp suwir',"
                        + "`lokasi_terakhir`='GRADING',`tgl_proses_terakhir`=CURRENT_DATE WHERE `no_box` IN (SELECT `no_box` FROM `tb_lp_suwir_detail` WHERE `no_lp_suwir` = '" + no_lp_suwir_lama + "')";
                Utility.db.getConnection().createStatement();
                Utility.db.getStatement().executeUpdate(update_box);
                //delete detail LP suwir
                String delete_detail = "DELETE FROM `tb_lp_suwir_detail` WHERE `no_lp_suwir` =  '" + no_lp_suwir_lama + "'";
                Utility.db.getConnection().createStatement();
                Utility.db.getStatement().executeUpdate(delete_detail);
                //delete LP suwir
                String delete_lp = "DELETE FROM `tb_lp_suwir` WHERE `no_lp_suwir` = '" + no_lp_suwir_lama + "'";
                Utility.db.getConnection().createStatement();
                Utility.db.getStatement().executeUpdate(delete_lp);

                sql = "INSERT INTO `tb_lp_suwir`(`no_lp_suwir`, `tgl_lp_suwir`, `keping`, `gram`, `gram_akhir`) "
                        + "VALUES ('" + ComboBox_kode_nolp.getSelectedItem().toString() + label_lp_suwir.getText() + "','" + dateFormat.format(Date_LPSuwir.getDate()) + "','" + total_keping_lp + "'," + total_gram_lp + ", " + txt_gram_akhir.getText() + ")";
                Utility.db.getConnection().createStatement();
                Utility.db.getStatement().executeUpdate(sql);

                for (int i = 0; i < Table_LPsuwir.getRowCount(); i++) {
                    String update = "UPDATE `tb_box_bahan_jadi` SET `status_terakhir`='" + ComboBox_kode_nolp.getSelectedItem().toString() + label_lp_suwir.getText() + "',"
                            + "`lokasi_terakhir`='LP SUWIR',`tgl_proses_terakhir`='" + dateFormat.format(Date_LPSuwir.getDate()) + "' WHERE `no_box` = '" + Table_LPsuwir.getValueAt(i, 0) + "'";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(update);

                    String insert = "INSERT INTO `tb_lp_suwir_detail`(`no_lp_suwir`, `no_box`) "
                            + "VALUES ('" + ComboBox_kode_nolp.getSelectedItem().toString() + label_lp_suwir.getText() + "','" + Table_LPsuwir.getValueAt(i, 0) + "')";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(insert);
                }
                Utility.db.getConnection().commit();
                JOptionPane.showMessageDialog(this, "LP Suwir Updated !");
                this.dispose();
            }
        } catch (SQLException | NullPointerException e) {
            try {
                Utility.db.getConnection().rollback();
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Create_LPSuwir.class.getName()).log(Level.SEVERE, null, ex);
            }
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JDialog_Create_LPSuwir.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JDialog_Create_LPSuwir.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        label_keterangan = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        Table_daftarBox = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        label_total_daftarBox = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        label_total_keping_daftarBox = new javax.swing.JLabel();
        label_total_keping_LP = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        label_total_gram_daftarBox = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_LPsuwir = new javax.swing.JTable();
        label_total_gram_LP = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        Date_LPSuwir = new com.toedter.calendar.JDateChooser();
        jLabel1 = new javax.swing.JLabel();
        label_total_boxLP = new javax.swing.JLabel();
        txt_search_box = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        button_Save = new javax.swing.JButton();
        button_hapus = new javax.swing.JButton();
        label_lp_suwir = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        txt_gram_akhir = new javax.swing.JTextField();
        button_refreshNoLP = new javax.swing.JButton();
        ComboBox_kode_nolp = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Bagian Grading Bahan Jadi");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        label_keterangan.setBackground(new java.awt.Color(255, 255, 255));
        label_keterangan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_keterangan.setForeground(new java.awt.Color(102, 102, 102));
        label_keterangan.setText("*Press ENTER to insert");

        Table_daftarBox.setAutoCreateRowSorter(true);
        Table_daftarBox.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_daftarBox.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Box", "Grade", "Tgl Box", "Kpg", "Gram", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_daftarBox.setFocusable(false);
        Table_daftarBox.getTableHeader().setReorderingAllowed(false);
        Table_daftarBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Table_daftarBoxMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(Table_daftarBox);

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Total :");

        label_total_daftarBox.setBackground(new java.awt.Color(255, 255, 255));
        label_total_daftarBox.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_daftarBox.setText("0");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Keping :");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel9.setText("Keping :");

        label_total_keping_daftarBox.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_daftarBox.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_keping_daftarBox.setText("0");

        label_total_keping_LP.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keping_LP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_keping_LP.setText("0");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Gram :");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Gram :");

        label_total_gram_daftarBox.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_daftarBox.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram_daftarBox.setText("0");

        Table_LPsuwir.setAutoCreateRowSorter(true);
        Table_LPsuwir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_LPsuwir.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Box", "Grade", "Tgl Box", "Kpg", "Gram", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Float.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_LPsuwir.setFocusable(false);
        Table_LPsuwir.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Table_LPsuwir);

        label_total_gram_LP.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_LP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram_LP.setText("0");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Daftar Box LP suwir");

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel19.setText("No LP Suwir : ");

        Date_LPSuwir.setBackground(new java.awt.Color(255, 255, 255));
        Date_LPSuwir.setDate(new Date());
        Date_LPSuwir.setDateFormatString("dd MMMM yyyy");
        Date_LPSuwir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_LPSuwir.setMaxSelectableDate(new Date());

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Total :");

        label_total_boxLP.setBackground(new java.awt.Color(255, 255, 255));
        label_total_boxLP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_boxLP.setText("0");

        txt_search_box.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_box.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_boxKeyPressed(evt);
            }
        });

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel18.setText("No Box :");

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel20.setText("Tanggal LP :");

        button_Save.setBackground(new java.awt.Color(255, 255, 255));
        button_Save.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_Save.setText("Save");
        button_Save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_SaveActionPerformed(evt);
            }
        });

        button_hapus.setBackground(new java.awt.Color(255, 255, 255));
        button_hapus.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_hapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_hapusActionPerformed(evt);
            }
        });

        label_lp_suwir.setBackground(new java.awt.Color(255, 255, 255));
        label_lp_suwir.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_lp_suwir.setText("-");

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel21.setText("Gram Akhir :");

        txt_gram_akhir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_refreshNoLP.setBackground(new java.awt.Color(255, 255, 255));
        button_refreshNoLP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refreshNoLP.setText("Refresh");
        button_refreshNoLP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refreshNoLPActionPerformed(evt);
            }
        });

        ComboBox_kode_nolp.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        ComboBox_kode_nolp.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SWR", "SP" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_box, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_daftarBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keping_daftarBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_daftarBox))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 518, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(button_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_boxLP)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_keping_LP)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram_LP)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(button_Save))
                            .addComponent(jScrollPane1)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_kode_nolp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(5, 5, 5)
                                .addComponent(label_lp_suwir)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_refreshNoLP))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_LPSuwir, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_gram_akhir, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_lp_suwir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refreshNoLP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_kode_nolp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel20)
                    .addComponent(Date_LPSuwir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(txt_gram_akhir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt_search_box, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_keterangan, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(button_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_total_keping_daftarBox, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_keping_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram_daftarBox, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_Save, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_gram_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_daftarBox, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_boxLP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void Table_daftarBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Table_daftarBoxMouseClicked
        // TODO add your handling code here:
        int x = Table_daftarBox.getSelectedRow();
        if (evt.getClickCount() == 2) {
            if (CheckDuplicateBoxAsal(Table_daftarBox.getValueAt(x, 0).toString())) {
                JOptionPane.showMessageDialog(this, "Box " + Table_daftarBox.getValueAt(x, 0).toString() + " sudah masuk");
            } else {
                DefaultTableModel model = (DefaultTableModel) Table_LPsuwir.getModel();
                model.addRow(new Object[]{Table_daftarBox.getValueAt(x, 0),
                    Table_daftarBox.getValueAt(x, 1),
                    Table_daftarBox.getValueAt(x, 2),
                    Table_daftarBox.getValueAt(x, 3),
                    Table_daftarBox.getValueAt(x, 4),
                    Table_daftarBox.getValueAt(x, 5)});
                count_LPSuwir();
            }
        }
    }//GEN-LAST:event_Table_daftarBoxMouseClicked

    private void txt_search_boxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_boxKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refresh_TabelDaftarBox();
            if (Table_daftarBox.getRowCount() == 1) {
                if (CheckDuplicateBoxAsal(Table_daftarBox.getValueAt(0, 0).toString())) {
                    JOptionPane.showMessageDialog(this, "Box " + Table_daftarBox.getValueAt(0, 0).toString() + " sudah masuk");
                } else {
                    DefaultTableModel model = (DefaultTableModel) Table_LPsuwir.getModel();
                    model.addRow(new Object[]{Table_daftarBox.getValueAt(0, 0),
                        Table_daftarBox.getValueAt(0, 1),
                        Table_daftarBox.getValueAt(0, 2),
                        Table_daftarBox.getValueAt(0, 3),
                        Table_daftarBox.getValueAt(0, 4),
                        Table_daftarBox.getValueAt(0, 5)});
                    ColumnsAutoSizer.sizeColumnsToFit(Table_LPsuwir);
                    label_keterangan.setVisible(true);
                    label_keterangan.setForeground(Color.green);
                    label_keterangan.setText("Inserted !");
                    txt_search_box.setText("");
                    txt_search_box.requestFocus();
                    count_LPSuwir();
                }
            } else if (Table_daftarBox.getRowCount() > 0) {
                label_keterangan.setVisible(true);
                label_keterangan.setForeground(Color.red);
                label_keterangan.setText("Multiple data selected !");
            } else {
                label_keterangan.setVisible(true);
                label_keterangan.setForeground(Color.red);
                label_keterangan.setText("No Data !");
            }
        }
    }//GEN-LAST:event_txt_search_boxKeyPressed

    private void button_hapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_hapusActionPerformed
        // TODO add your handling code here:
        int i = Table_LPsuwir.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) Table_LPsuwir.getModel();
        if (i != -1) {
            model.removeRow(Table_LPsuwir.getRowSorter().convertRowIndexToModel(i));
        }
        ColumnsAutoSizer.sizeColumnsToFit(Table_LPsuwir);
        count_LPSuwir();
    }//GEN-LAST:event_button_hapusActionPerformed

    private void button_SaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_SaveActionPerformed
        // TODO add your handling code here:
        if (status.equals("baru")) {
            baru();
        } else if (status.equals("edit")) {
            edit();
        }
    }//GEN-LAST:event_button_SaveActionPerformed

    private void button_refreshNoLPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refreshNoLPActionPerformed
        // TODO add your handling code here:
        label_lp_suwir.setText(getNoLPSuwir());
    }//GEN-LAST:event_button_refreshNoLPActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_kode_nolp;
    private com.toedter.calendar.JDateChooser Date_LPSuwir;
    private javax.swing.JTable Table_LPsuwir;
    private javax.swing.JTable Table_daftarBox;
    private javax.swing.JButton button_Save;
    private javax.swing.JButton button_hapus;
    private javax.swing.JButton button_refreshNoLP;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel label_keterangan;
    private javax.swing.JLabel label_lp_suwir;
    private javax.swing.JLabel label_total_boxLP;
    private javax.swing.JLabel label_total_daftarBox;
    private javax.swing.JLabel label_total_gram_LP;
    private javax.swing.JLabel label_total_gram_daftarBox;
    private javax.swing.JLabel label_total_keping_LP;
    private javax.swing.JLabel label_total_keping_daftarBox;
    private javax.swing.JTextField txt_gram_akhir;
    private javax.swing.JTextField txt_search_box;
    // End of variables declaration//GEN-END:variables
}
