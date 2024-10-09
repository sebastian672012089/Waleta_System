package waleta_system.Packing;

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

public class JDialog_new_SPK_Expor extends javax.swing.JDialog {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    String kode_spk = null;
    ArrayList<String> DetailGradeAwal = new ArrayList<>();

    public JDialog_new_SPK_Expor(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        try {
            initComponents();
            button_tambah1.setIcon(Utility.ResizeImageIcon(new javax.swing.ImageIcon(getClass().getResource("/waleta_system/Images/add_green.png")), button_hapus1.getWidth(), button_hapus1.getHeight()));
            button_hapus1.setIcon(Utility.ResizeImageIcon(new javax.swing.ImageIcon(getClass().getResource("/waleta_system/Images/delete-icon.png")), button_hapus1.getWidth(), button_hapus1.getHeight()));
            button_tambah2.setIcon(Utility.ResizeImageIcon(new javax.swing.ImageIcon(getClass().getResource("/waleta_system/Images/add_green.png")), button_hapus1.getWidth(), button_hapus1.getHeight()));
            button_hapus2.setIcon(Utility.ResizeImageIcon(new javax.swing.ImageIcon(getClass().getResource("/waleta_system/Images/delete-icon.png")), button_hapus1.getWidth(), button_hapus1.getHeight()));
            button_tambah3.setIcon(Utility.ResizeImageIcon(new javax.swing.ImageIcon(getClass().getResource("/waleta_system/Images/add_green.png")), button_hapus1.getWidth(), button_hapus1.getHeight()));
            button_hapus3.setIcon(Utility.ResizeImageIcon(new javax.swing.ImageIcon(getClass().getResource("/waleta_system/Images/delete-icon.png")), button_hapus1.getWidth(), button_hapus1.getHeight()));

            this.kode_spk = generateKodeSPK();
            txt_kode_spk.setText(this.kode_spk);

//            JComboBox comboBOX = new JComboBox();
//            String query = "SELECT `kode_rb`, `no_registrasi`, `nama_rumah_burung`, `kapasitas_per_tahun` "
//                    + "FROM `tb_rumah_burung` WHERE CHAR_LENGTH(`no_registrasi`) IN (3, 4)";
//            rs = Utility.db.getStatement().executeQuery(query);
//            while (rs.next()) {
//                comboBOX.addItem(rs.getString("no_registrasi"));
//            }
//            Table_detail1.getColumnModel().getColumn(8).setCellEditor(new DefaultCellEditor(comboBOX));
        } catch (Exception ex) {
            Logger.getLogger(JDialog_new_SPK_Expor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void count() {
        float total_harga = 0, total_gram = 0, total_kemasan = 0;
        for (int i = 0; i < Table_detail1.getRowCount(); i++) {
            try {
                String grade_wlt = Table_detail1.getValueAt(i, 1).toString();
                String grade_buyer = Table_detail1.getValueAt(i, 2).toString();
                int total_berat = Integer.valueOf(Table_detail1.getValueAt(i, 5).toString());
                int berat_per_kemasan = Integer.valueOf(Table_detail1.getValueAt(i, 3).toString());
                int jumlah_kemasan = total_berat / berat_per_kemasan;
                Table_detail1.setValueAt(jumlah_kemasan, i, 4);
                total_harga = total_harga + ((Float.valueOf(Table_detail1.getValueAt(i, 6).toString()) / 1000) * Float.valueOf(Table_detail1.getValueAt(i, 5).toString()));
                total_gram = total_gram + Integer.valueOf(Table_detail1.getValueAt(i, 5).toString());
                total_kemasan = total_kemasan + jumlah_kemasan;
            } catch (NullPointerException e) {
                JOptionPane.showMessageDialog(this, "Tidak Boleh ada data yang kosong !");
                break;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Format Angka salah");
                break;
            }
        }
        label_total_grade1.setText(Integer.toString(Table_detail1.getRowCount()));
        label_total_harga1.setText(decimalFormat.format(total_harga));
        label_total_gram1.setText(decimalFormat.format(total_gram));
        label_total_kemasan1.setText(decimalFormat.format(total_kemasan));
        ColumnsAutoSizer.sizeColumnsToFit(Table_detail1);

        total_harga = 0;
        total_gram = 0;
        total_kemasan = 0;
        for (int i = 0; i < Table_detail2.getRowCount(); i++) {
            try {
                String grade_wlt = Table_detail2.getValueAt(i, 1).toString();
                String grade_buyer = Table_detail2.getValueAt(i, 2).toString();
                int total_berat = Integer.valueOf(Table_detail2.getValueAt(i, 5).toString());
                int berat_per_kemasan = Integer.valueOf(Table_detail2.getValueAt(i, 3).toString());
                int jumlah_kemasan = total_berat / berat_per_kemasan;
                Table_detail2.setValueAt(jumlah_kemasan, i, 4);
                total_harga = total_harga + ((Float.valueOf(Table_detail2.getValueAt(i, 6).toString()) / 1000) * Float.valueOf(Table_detail2.getValueAt(i, 5).toString()));
                total_gram = total_gram + Integer.valueOf(Table_detail2.getValueAt(i, 5).toString());
                total_kemasan = total_kemasan + jumlah_kemasan;
            } catch (NullPointerException e) {
                JOptionPane.showMessageDialog(this, "Tidak Boleh ada data yang kosong !");
                break;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Format Angka salah");
                break;
            }
        }
        label_total_grade2.setText(Integer.toString(Table_detail2.getRowCount()));
        label_total_harga2.setText(decimalFormat.format(total_harga));
        label_total_gram2.setText(decimalFormat.format(total_gram));
        label_total_kemasan2.setText(decimalFormat.format(total_kemasan));
        ColumnsAutoSizer.sizeColumnsToFit(Table_detail2);

        total_harga = 0;
        total_gram = 0;
        total_kemasan = 0;
        for (int i = 0; i < Table_detail3.getRowCount(); i++) {
            try {
                String grade_wlt = Table_detail3.getValueAt(i, 1).toString();
                String grade_buyer = Table_detail3.getValueAt(i, 2).toString();
                int total_berat = Integer.valueOf(Table_detail3.getValueAt(i, 5).toString());
                int berat_per_kemasan = Integer.valueOf(Table_detail3.getValueAt(i, 3).toString());
                int jumlah_kemasan = total_berat / berat_per_kemasan;
                Table_detail3.setValueAt(jumlah_kemasan, i, 4);
                total_harga = total_harga + ((Float.valueOf(Table_detail3.getValueAt(i, 6).toString()) / 1000) * Float.valueOf(Table_detail3.getValueAt(i, 5).toString()));
                total_gram = total_gram + Integer.valueOf(Table_detail3.getValueAt(i, 5).toString());
                total_kemasan = total_kemasan + jumlah_kemasan;
            } catch (NullPointerException e) {
                JOptionPane.showMessageDialog(this, "Tidak Boleh ada data yang kosong !");
                break;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Format Angka salah");
                break;
            }
        }
        label_total_grade3.setText(Integer.toString(Table_detail3.getRowCount()));
        label_total_harga3.setText(decimalFormat.format(total_harga));
        label_total_gram3.setText(decimalFormat.format(total_gram));
        label_total_kemasan3.setText(decimalFormat.format(total_kemasan));
        ColumnsAutoSizer.sizeColumnsToFit(Table_detail3);
    }

    public String generateKodeSPK() {
        try {
            int LastNumber = 0;
            Date tgl_spk = date;
            if (Date_spk.getDate() != null) {
                tgl_spk = Date_spk.getDate();
            }
            String get_lastNumber = "SELECT MAX(SUBSTRING(`kode_spk`, 9, 3)+0) AS 'last' \n"
                    + "FROM `tb_spk` "
                    + "WHERE YEAR(`tanggal_spk`) = " + new SimpleDateFormat("yyyy").format(tgl_spk) + " "
                    + "AND `kode_spk` LIKE 'SPK-%'";
            ResultSet result_lastNumber = Utility.db.getStatement().executeQuery(get_lastNumber);
            if (result_lastNumber.next()) {
                LastNumber = result_lastNumber.getInt("last") + 1;
            }

            String new_kode_spk = "SPK-" + new SimpleDateFormat("yyMM").format(tgl_spk) + String.format("%03d", LastNumber);
            return new_kode_spk;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JDialog_new_SPK_Expor.class.getName()).log(Level.SEVERE, null, ex);
            return "ERROR";
        }
    }

    public void New_spk_waleta() throws Exception {
        String tanggal_tk = "NULL";
        if (Date_tk.getDate() != null) {
            tanggal_tk = "'" + dateFormat.format(Date_tk.getDate()) + "'";
        }
        String tanggal_awb = "NULL";
        if (Date_awb.getDate() != null) {
            tanggal_awb = "'" + dateFormat.format(Date_awb.getDate()) + "'";
        }
        String INSERT_SPK_WALETA = "INSERT INTO `tb_spk`(`kode_spk`, `buyer`, `no_revisi`, `tanggal_spk`, `tanggal_tk`, `tanggal_keluar`, `tanggal_awb`, `detail`, `approved`) "
                + "VALUES ('" + kode_spk + "W','" + txt_buyer_code.getText() + "',1,'" + dateFormat.format(Date_spk.getDate()) + "'," + tanggal_tk + ",'" + dateFormat.format(Date_keluar.getDate()) + "'," + tanggal_awb + ", '" + txt_catatan.getText() + "', 'MARKETING MANAGER')";
        Utility.db.getConnection().createStatement();
        if ((Utility.db.getStatement().executeUpdate(INSERT_SPK_WALETA)) > 0) {
            for (int i = 0; i < Table_detail1.getRowCount(); i++) {
                String INSERT_DETAIL_SPK_WALETA = "INSERT INTO `tb_spk_detail`(`kode_spk`, `grade_waleta`, `grade_buyer`, `berat_kemasan`, `jumlah_kemasan`, `berat`, `harga_cny`, `keterangan`) "
                        + "VALUES ("
                        + "'" + kode_spk + "W',"
                        + "'" + Table_detail1.getValueAt(i, 1).toString() + "',"
                        + "'" + Table_detail1.getValueAt(i, 2).toString() + "',"
                        + "'" + Table_detail1.getValueAt(i, 3).toString() + "',"
                        + "'" + Table_detail1.getValueAt(i, 4).toString() + "',"
                        + "'" + Table_detail1.getValueAt(i, 5).toString() + "',"
                        + "'" + Table_detail1.getValueAt(i, 6).toString() + "',"
                        + "'" + Table_detail1.getValueAt(i, 7).toString() + "')";
                Utility.db.getConnection().createStatement();
                Utility.db.getStatement().executeUpdate(INSERT_DETAIL_SPK_WALETA);
            }
        }
    }

    public void New_spk_esta() throws Exception {
        String tanggal_tk = "NULL";
        if (Date_tk.getDate() != null) {
            tanggal_tk = "'" + dateFormat.format(Date_tk.getDate()) + "'";
        }
        String tanggal_awb = "NULL";
        if (Date_awb.getDate() != null) {
            tanggal_awb = "'" + dateFormat.format(Date_awb.getDate()) + "'";
        }
        String INSERT_SPK_ESTA = "INSERT INTO `tb_spk`(`kode_spk`, `buyer`, `no_revisi`, `tanggal_spk`, `tanggal_tk`, `tanggal_keluar`, `tanggal_awb`, `detail`, `approved`) "
                + "VALUES ('" + kode_spk + "E','" + txt_buyer_code.getText() + "',1,'" + dateFormat.format(Date_spk.getDate()) + "'," + tanggal_tk + ",'" + dateFormat.format(Date_keluar.getDate()) + "'," + tanggal_awb + ", '" + txt_catatan.getText() + "', 'MARKETING MANAGER')";
        Utility.db.getConnection().createStatement();
        if ((Utility.db.getStatement().executeUpdate(INSERT_SPK_ESTA)) > 0) {
            for (int i = 0; i < Table_detail2.getRowCount(); i++) {
                String INSERT_DETAIL_SPK_ESTA = "INSERT INTO `tb_spk_detail`(`grade_waleta`, `grade_buyer`, `berat_kemasan`, `jumlah_kemasan`, `berat`, `harga_cny`, `keterangan`, `kode_spk`) "
                        + "VALUES ("
                        + "'" + Table_detail2.getValueAt(i, 1).toString() + "',"
                        + "'" + Table_detail2.getValueAt(i, 2).toString() + "',"
                        + "'" + Table_detail2.getValueAt(i, 3).toString() + "',"
                        + "'" + Table_detail2.getValueAt(i, 4).toString() + "',"
                        + "'" + Table_detail2.getValueAt(i, 5).toString() + "',"
                        + "'" + Table_detail2.getValueAt(i, 6).toString() + "',"
                        + "'" + Table_detail2.getValueAt(i, 7).toString() + "',"
                        + "'" + kode_spk + "E')";
                Utility.db.getConnection().createStatement();
                Utility.db.getStatement().executeUpdate(INSERT_DETAIL_SPK_ESTA);
            }
        }
    }

    public void New_spk_jastip() throws Exception {
        String tanggal_tk = "NULL";
        if (Date_tk.getDate() != null) {
            tanggal_tk = "'" + dateFormat.format(Date_tk.getDate()) + "'";
        }
        String tanggal_awb = "NULL";
        if (Date_awb.getDate() != null) {
            tanggal_awb = "'" + dateFormat.format(Date_awb.getDate()) + "'";
        }
        String INSERT_SPK_JASTIP = "INSERT INTO `tb_spk`(`kode_spk`, `buyer`, `no_revisi`, `tanggal_spk`, `tanggal_tk`, `tanggal_keluar`, `tanggal_awb`, `detail`, `approved`) "
                + "VALUES ('" + kode_spk + "J','" + txt_buyer_code.getText() + "',1,'" + dateFormat.format(Date_spk.getDate()) + "'," + tanggal_tk + ",'" + dateFormat.format(Date_keluar.getDate()) + "'," + tanggal_awb + ", '" + txt_catatan.getText() + "', 'MARKETING MANAGER')";
        Utility.db.getConnection().createStatement();
        if ((Utility.db.getStatement().executeUpdate(INSERT_SPK_JASTIP)) > 0) {
            for (int i = 0; i < Table_detail3.getRowCount(); i++) {
                String INSERT_DETAIL_SPK_JASTIP = "INSERT INTO `tb_spk_detail`(`grade_waleta`, `grade_buyer`, `berat_kemasan`, `jumlah_kemasan`, `berat`, `harga_cny`, `keterangan`, `kode_spk`) "
                        + "VALUES ("
                        + "'" + Table_detail3.getValueAt(i, 1).toString() + "',"
                        + "'" + Table_detail3.getValueAt(i, 2).toString() + "',"
                        + "'" + Table_detail3.getValueAt(i, 3).toString() + "',"
                        + "'" + Table_detail3.getValueAt(i, 4).toString() + "',"
                        + "'" + Table_detail3.getValueAt(i, 5).toString() + "',"
                        + "'" + Table_detail3.getValueAt(i, 6).toString() + "',"
                        + "'" + Table_detail3.getValueAt(i, 7).toString() + "',"
                        + "'" + kode_spk + "J')";
                Utility.db.getConnection().createStatement();
                Utility.db.getStatement().executeUpdate(INSERT_DETAIL_SPK_JASTIP);
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
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txt_kode_spk = new javax.swing.JTextField();
        Date_spk = new com.toedter.calendar.JDateChooser();
        jLabel9 = new javax.swing.JLabel();
        txt_buyer_name = new javax.swing.JTextField();
        button_choose = new javax.swing.JButton();
        txt_buyer_code = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        Date_keluar = new com.toedter.calendar.JDateChooser();
        button_cancel = new javax.swing.JButton();
        button_save = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txt_catatan = new javax.swing.JTextArea();
        label_catatan_counter = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        label_no_revisi = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        Date_tk = new com.toedter.calendar.JDateChooser();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        label_total_harga1 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        label_total_gram1 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        label_total_grade1 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        Table_detail1 = new javax.swing.JTable();
        button_hapus1 = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        label_total_kemasan1 = new javax.swing.JLabel();
        button_tambah1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        label_total_harga2 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        label_total_gram2 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        label_total_grade2 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        Table_detail2 = new javax.swing.JTable();
        button_hapus2 = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        label_total_kemasan2 = new javax.swing.JLabel();
        button_tambah2 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        label_total_harga3 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        label_total_gram3 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        label_total_grade3 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        Table_detail3 = new javax.swing.JTable();
        button_hapus3 = new javax.swing.JButton();
        jLabel25 = new javax.swing.JLabel();
        label_total_kemasan3 = new javax.swing.JLabel();
        button_tambah3 = new javax.swing.JButton();
        button_count = new javax.swing.JButton();
        jLabel26 = new javax.swing.JLabel();
        Date_awb = new com.toedter.calendar.JDateChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("PENGIRIMAN");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        jLabel1.setText("New SPK Ekspor");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel2.setText("DEPARTEMEN SALES & EKSPOR");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel3.setText("Kode SPK :");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel5.setText("Tanggal SPK :");

        txt_kode_spk.setEditable(false);
        txt_kode_spk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        Date_spk.setBackground(new java.awt.Color(255, 255, 255));
        Date_spk.setDate(new Date());
        Date_spk.setDateFormatString("dd MMMM yyyy");
        Date_spk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel9.setText("Kode Pembeli :");

        txt_buyer_name.setEditable(false);
        txt_buyer_name.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        button_choose.setBackground(new java.awt.Color(255, 255, 255));
        button_choose.setFont(new java.awt.Font("SansSerif", 0, 11)); // NOI18N
        button_choose.setText("Pilih Pembeli");
        button_choose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_chooseActionPerformed(evt);
            }
        });

        txt_buyer_code.setEditable(false);
        txt_buyer_code.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel10.setText("Nama Pembeli :");

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel15.setText("Catatan :");

        Date_keluar.setBackground(new java.awt.Color(255, 255, 255));
        Date_keluar.setDateFormatString("dd MMMM yyyy");
        Date_keluar.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        button_cancel.setBackground(new java.awt.Color(255, 255, 255));
        button_cancel.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_cancel.setText("CANCEL");
        button_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_cancelActionPerformed(evt);
            }
        });

        button_save.setBackground(new java.awt.Color(255, 255, 255));
        button_save.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_save.setText("SAVE");
        button_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_saveActionPerformed(evt);
            }
        });

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel16.setText("Tanggal Keluar :");

        txt_catatan.setColumns(20);
        txt_catatan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_catatan.setRows(5);
        txt_catatan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_catatanKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(txt_catatan);

        label_catatan_counter.setBackground(new java.awt.Color(255, 255, 255));
        label_catatan_counter.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        label_catatan_counter.setText("(0/250)");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel4.setText("No Revisi :");

        label_no_revisi.setBackground(new java.awt.Color(255, 255, 255));
        label_no_revisi.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        label_no_revisi.setText("1");

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel20.setText("Tanggal TK :");

        Date_tk.setBackground(new java.awt.Color(255, 255, 255));
        Date_tk.setDateFormatString("dd MMMM yyyy");
        Date_tk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Detail SPK WALETA", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        label_total_harga1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_harga1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_harga1.setText("0");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel14.setText("Gram :");

        label_total_gram1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram1.setText("0");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel11.setText("Total Grade :");

        label_total_grade1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_grade1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_grade1.setText("0");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Harga CNY :");

        Table_detail1.setAutoCreateRowSorter(true);
        Table_detail1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_detail1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Grade WLT", "Grade Buyer", "Berat / Kemasan", "Jumlah Kemasan", "Total Berat", "Harga CNY", "Keterangan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true, false, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_detail1.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(Table_detail1);

        button_hapus1.setBackground(new java.awt.Color(255, 255, 255));
        button_hapus1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_hapus1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_hapus1ActionPerformed(evt);
            }
        });

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel17.setText("Kemasan :");

        label_total_kemasan1.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kemasan1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_kemasan1.setText("0");

        button_tambah1.setBackground(new java.awt.Color(255, 255, 255));
        button_tambah1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_tambah1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_tambah1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 743, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_grade1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_harga1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_kemasan1))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(button_tambah1, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_hapus1, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_hapus1, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_tambah1, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_total_harga1)
                    .addComponent(jLabel14)
                    .addComponent(label_total_gram1)
                    .addComponent(jLabel11)
                    .addComponent(label_total_grade1)
                    .addComponent(jLabel13)
                    .addComponent(label_total_kemasan1)
                    .addComponent(jLabel17))
                .addContainerGap())
        );

        jTabbedPane1.addTab("WALETA", jPanel2);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Detail SPK ESTA", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        label_total_harga2.setBackground(new java.awt.Color(255, 255, 255));
        label_total_harga2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_harga2.setText("0");

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel18.setText("Gram :");

        label_total_gram2.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram2.setText("0");

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("Total Grade :");

        label_total_grade2.setBackground(new java.awt.Color(255, 255, 255));
        label_total_grade2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_grade2.setText("0");

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel19.setText("Harga CNY :");

        Table_detail2.setAutoCreateRowSorter(true);
        Table_detail2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_detail2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Grade WLT", "Grade Buyer", "Berat / Kemasan", "Jumlah Kemasan", "Total Berat", "Harga CNY", "Keterangan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true, false, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_detail2.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(Table_detail2);

        button_hapus2.setBackground(new java.awt.Color(255, 255, 255));
        button_hapus2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_hapus2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_hapus2ActionPerformed(evt);
            }
        });

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel21.setText("Kemasan :");

        label_total_kemasan2.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kemasan2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_kemasan2.setText("0");

        button_tambah2.setBackground(new java.awt.Color(255, 255, 255));
        button_tambah2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_tambah2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_tambah2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 743, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_grade2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_harga2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_kemasan2))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(button_tambah2, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_hapus2, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_hapus2, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_tambah2, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_total_harga2)
                    .addComponent(jLabel18)
                    .addComponent(label_total_gram2)
                    .addComponent(jLabel12)
                    .addComponent(label_total_grade2)
                    .addComponent(jLabel19)
                    .addComponent(label_total_kemasan2)
                    .addComponent(jLabel21))
                .addContainerGap())
        );

        jTabbedPane1.addTab("ESTA", jPanel3);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Detail SPK JASTIP", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        label_total_harga3.setBackground(new java.awt.Color(255, 255, 255));
        label_total_harga3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_harga3.setText("0");

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel22.setText("Gram :");

        label_total_gram3.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_gram3.setText("0");

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel23.setText("Total Grade :");

        label_total_grade3.setBackground(new java.awt.Color(255, 255, 255));
        label_total_grade3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_grade3.setText("0");

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel24.setText("Harga CNY :");

        Table_detail3.setAutoCreateRowSorter(true);
        Table_detail3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_detail3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Grade WLT", "Grade Buyer", "Berat / Kemasan", "Jumlah Kemasan", "Total Berat", "Harga CNY", "Keterangan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true, false, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_detail3.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(Table_detail3);

        button_hapus3.setBackground(new java.awt.Color(255, 255, 255));
        button_hapus3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_hapus3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_hapus3ActionPerformed(evt);
            }
        });

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel25.setText("Kemasan :");

        label_total_kemasan3.setBackground(new java.awt.Color(255, 255, 255));
        label_total_kemasan3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_kemasan3.setText("0");

        button_tambah3.setBackground(new java.awt.Color(255, 255, 255));
        button_tambah3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_tambah3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_tambah3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 743, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_grade3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_gram3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel24)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_harga3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel25)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_kemasan3))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(button_tambah3, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_hapus3, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_hapus3, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_tambah3, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(label_total_harga3)
                    .addComponent(jLabel22)
                    .addComponent(label_total_gram3)
                    .addComponent(jLabel23)
                    .addComponent(label_total_grade3)
                    .addComponent(jLabel24)
                    .addComponent(label_total_kemasan3)
                    .addComponent(jLabel25))
                .addContainerGap())
        );

        jTabbedPane1.addTab("JASTIP", jPanel4);

        button_count.setBackground(new java.awt.Color(255, 255, 255));
        button_count.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_count.setText("COUNT");
        button_count.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_countActionPerformed(evt);
            }
        });

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel26.setText("Tanggal AWB :");

        Date_awb.setBackground(new java.awt.Color(255, 255, 255));
        Date_awb.setDateFormatString("dd MMMM yyyy");
        Date_awb.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_catatan_counter, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txt_kode_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txt_buyer_code, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(button_choose)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel4)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(label_no_revisi))))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
                                            .addComponent(txt_buyer_name))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(button_cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(button_save, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(button_count, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(Date_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(Date_tk, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(Date_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(Date_awb, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(jLabel2)
                            .addComponent(jLabel1)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_kode_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_no_revisi, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(Date_tk, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(Date_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(Date_awb, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_buyer_code, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_choose, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_buyer_name, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(label_catatan_counter, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(button_count, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_save, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void button_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_saveActionPerformed
        // TODO add your handling code here:
        boolean check_null = false;
        for (int j = 0; j < Table_detail1.getColumnCount(); j++) {
            for (int i = 0; i < Table_detail1.getRowCount(); i++) {
                check_null = (Table_detail1.getValueAt(i, j) == null);
                break;
            }
        }
        for (int j = 0; j < Table_detail2.getColumnCount(); j++) {
            for (int i = 0; i < Table_detail2.getRowCount(); i++) {
                check_null = (Table_detail2.getValueAt(i, j) == null);
                break;
            }
        }
        for (int j = 0; j < Table_detail3.getColumnCount(); j++) {
            for (int i = 0; i < Table_detail3.getRowCount(); i++) {
                check_null = (Table_detail3.getValueAt(i, j) == null);
                break;
            }
        }

        if (txt_kode_spk.getText() == null
                || txt_buyer_name.getText() == null || txt_buyer_name.getText().equals("")
                || txt_buyer_code.getText() == null || txt_buyer_code.getText().equals("")
                || Date_spk.getDate() == null
                || Date_keluar.getDate() == null || check_null) {
            JOptionPane.showMessageDialog(this, "Silahkan Lengkapi Data diatas !!");
        } else {
            count();
            this.kode_spk = generateKodeSPK();
            txt_kode_spk.setText(this.kode_spk);
            try {
                Utility.db.getConnection().setAutoCommit(false);

                if (Table_detail1.getRowCount() > 0) {
                    New_spk_waleta();
                }
                if (Table_detail2.getRowCount() > 0) {
                    New_spk_esta();
                }
                if (Table_detail3.getRowCount() > 0) {
                    New_spk_jastip();
                }
                Utility.db.getConnection().commit();
                JOptionPane.showMessageDialog(this, "Data Saved !");
                this.dispose();
            } catch (Exception ex) {
                try {
                    Utility.db.getConnection().rollback();
                } catch (SQLException e) {
                    Logger.getLogger(JDialog_new_SPK_Expor.class.getName()).log(Level.SEVERE, null, e);
                }
                JOptionPane.showMessageDialog(this, "INPUT GAGAL!!!\n" + ex);
                Logger.getLogger(JDialog_new_SPK_Expor.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    Utility.db.getConnection().setAutoCommit(true);
                } catch (SQLException ex) {
                    Logger.getLogger(JDialog_new_SPK_Expor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_button_saveActionPerformed

    private void button_chooseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_chooseActionPerformed
        // TODO add your handling code here:
        JDialog_ChooseBuyer dialog = new JDialog_ChooseBuyer(new javax.swing.JFrame(), true, "Ekspor");
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);

        int a = JDialog_ChooseBuyer.table_list_buyer.getSelectedRow();
        if (a >= 0) {
            String nama_buyer = JDialog_ChooseBuyer.table_list_buyer.getValueAt(a, 1).toString();
            String kode_buyer = JDialog_ChooseBuyer.table_list_buyer.getValueAt(a, 0).toString();
            txt_buyer_code.setText(kode_buyer);
            txt_buyer_name.setText(nama_buyer);
        }
    }//GEN-LAST:event_button_chooseActionPerformed

    private void button_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cancelActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_button_cancelActionPerformed

    private void button_hapus1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_hapus1ActionPerformed
        int i = Table_detail1.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) Table_detail1.getModel();
        if (i != -1) {
            int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin detele grade " + Table_detail1.getValueAt(i, 2) + "?", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                model.removeRow(i);
            }
        }
        ColumnsAutoSizer.sizeColumnsToFit(Table_detail1);
        count();
    }//GEN-LAST:event_button_hapus1ActionPerformed

    private void button_countActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_countActionPerformed
        // TODO add your handling code here:
        count();
    }//GEN-LAST:event_button_countActionPerformed

    private void txt_catatanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_catatanKeyPressed
        // TODO add your handling code here:
        int x = txt_catatan.getText().length();
        label_catatan_counter.setText("(" + (x + 1) + "/250)");
    }//GEN-LAST:event_txt_catatanKeyPressed

    private void button_tambah1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_tambah1ActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_detail1.getModel();
        model.addRow(new Object[]{
            0,
            null,
            null,
            null,
            null,
            null,
            0,
            "-"
        });
        ColumnsAutoSizer.sizeColumnsToFit(Table_detail1);
    }//GEN-LAST:event_button_tambah1ActionPerformed

    private void button_hapus2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_hapus2ActionPerformed
        // TODO add your handling code here:
        int i = Table_detail2.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) Table_detail2.getModel();
        if (i != -1) {
            int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin detele grade " + Table_detail2.getValueAt(i, 2) + "?", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                model.removeRow(i);
            }
        }
        ColumnsAutoSizer.sizeColumnsToFit(Table_detail2);
        count();
    }//GEN-LAST:event_button_hapus2ActionPerformed

    private void button_tambah2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_tambah2ActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_detail2.getModel();
        model.addRow(new Object[]{
            0,
            null,
            null,
            null,
            null,
            null,
            0,
            "-"
        });
        ColumnsAutoSizer.sizeColumnsToFit(Table_detail2);
    }//GEN-LAST:event_button_tambah2ActionPerformed

    private void button_hapus3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_hapus3ActionPerformed
        // TODO add your handling code here:
        int i = Table_detail3.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) Table_detail3.getModel();
        if (i != -1) {
            int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin detele grade " + Table_detail3.getValueAt(i, 2) + "?", "Warning", 0);
            if (dialogResult == JOptionPane.YES_OPTION) {
                model.removeRow(i);
            }
        }
        ColumnsAutoSizer.sizeColumnsToFit(Table_detail3);
        count();
    }//GEN-LAST:event_button_hapus3ActionPerformed

    private void button_tambah3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_tambah3ActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_detail3.getModel();
        model.addRow(new Object[]{
            0,
            null,
            null,
            null,
            null,
            null,
            0,
            "-"
        });
        ColumnsAutoSizer.sizeColumnsToFit(Table_detail3);
    }//GEN-LAST:event_button_tambah3ActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JDialog_new_SPK_Expor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JDialog_new_SPK_Expor pack = new JDialog_new_SPK_Expor(new javax.swing.JFrame(), true);
                pack.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Date_awb;
    private com.toedter.calendar.JDateChooser Date_keluar;
    private com.toedter.calendar.JDateChooser Date_spk;
    private com.toedter.calendar.JDateChooser Date_tk;
    private javax.swing.JTable Table_detail1;
    private javax.swing.JTable Table_detail2;
    private javax.swing.JTable Table_detail3;
    private javax.swing.JButton button_cancel;
    private javax.swing.JButton button_choose;
    private javax.swing.JButton button_count;
    private javax.swing.JButton button_hapus1;
    private javax.swing.JButton button_hapus2;
    private javax.swing.JButton button_hapus3;
    private javax.swing.JButton button_save;
    private javax.swing.JButton button_tambah1;
    private javax.swing.JButton button_tambah2;
    private javax.swing.JButton button_tambah3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_catatan_counter;
    private javax.swing.JLabel label_no_revisi;
    private javax.swing.JLabel label_total_grade1;
    private javax.swing.JLabel label_total_grade2;
    private javax.swing.JLabel label_total_grade3;
    private javax.swing.JLabel label_total_gram1;
    private javax.swing.JLabel label_total_gram2;
    private javax.swing.JLabel label_total_gram3;
    private javax.swing.JLabel label_total_harga1;
    private javax.swing.JLabel label_total_harga2;
    private javax.swing.JLabel label_total_harga3;
    private javax.swing.JLabel label_total_kemasan1;
    private javax.swing.JLabel label_total_kemasan2;
    private javax.swing.JLabel label_total_kemasan3;
    private javax.swing.JTextField txt_buyer_code;
    private javax.swing.JTextField txt_buyer_name;
    private javax.swing.JTextArea txt_catatan;
    private javax.swing.JTextField txt_kode_spk;
    // End of variables declaration//GEN-END:variables
}
