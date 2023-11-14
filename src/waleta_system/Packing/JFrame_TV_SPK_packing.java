package waleta_system.Packing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JFrame_TV_SPK_packing extends javax.swing.JFrame {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    static Timer t;
    TimerTask timer;
    int tab = 0;

    int[] R = new int[]{102, 153, 153, 204, 255, 255, 255, 255, 255, 204, 153, 102};
    int[] G = new int[]{255, 255, 255, 255, 255, 204, 153, 153, 153, 153, 204, 204};
    int[] B = new int[]{255, 204, 153, 153, 153, 153, 153, 204, 255, 255, 255, 255};
    int spk = 0, baris = -1, check = 0;

    public JFrame_TV_SPK_packing() {
        initComponents();
    }

    public void init() {
        Thread clock = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        refresh_JAM();
                        sleep(1000);
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(JFrame_TV_SPK_packing.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        clock.start();

        refreshTable_SPK();
        timer = new TimerTask() {
            int detik = 0;
            @Override
            public void run() {
                boolean changeTab = true;
                if (detik == 0) {
                    while (changeTab) {
                        jTabbedPane1.setSelectedIndex(tab);
                        if (tab == 1 && jTextArea1.getText().equals("-")) {
                            tab = 2;
                        } else if (tab == 2 && jTextArea2.getText().equals("-")) {
                            tab = 3;
                        } else if (tab == 3 && jTextArea3.getText().equals("-")) {
                            tab = 4;
                        } else if (tab == 4 && jTextArea4.getText().equals("-")) {
                            tab = 5;
                        } else if (tab == 5 && jTextArea5.getText().equals("-")) {
                            tab = 0;
                        } else {
                            changeTab = false;
                        }
                    }
                    tab++;
                    if (tab > 5) {
                        tab = 0;
                    }
                }

                detik = detik + 1;
                if (detik > 59) {
                    detik = 0;
                }
            }
        };
        t = new Timer();
        t.schedule(timer, 1000, 1000);
    }

    public void refresh_JAM() {
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");

        String formattedDate = myDateObj.format(myFormatObj);
        label_jam.setText(formattedDate);
    }

    public void refreshTable_SPK() {
        try {
            DefaultTableModel model1 = (DefaultTableModel) Tabel_Detail_SPK.getModel();
            model1.setRowCount(0);
            DefaultTableModel model2 = (DefaultTableModel) Tabel_Detail_SPK1.getModel();
            model2.setRowCount(0);
            int row_counter1 = 0, row_counter2 = 0;
            sql = "SELECT `tb_spk`.`kode_spk`, `tb_buyer`.`nama`, `tanggal_spk`, `tanggal_tk`, `detail`, "
                    + "(SELECT SUM(`tb_spk_detail`.`berat`) FROM `tb_spk_detail` WHERE `kode_spk` = `tb_spk`.`kode_spk`) AS 'berat', "
                    + "SUM(`tb_box_bahan_jadi`.`berat`) AS 'progress'"
                    + "FROM `tb_spk` "
                    + "LEFT JOIN `tb_buyer` ON `tb_spk`.`buyer` = `tb_buyer`.`kode_buyer`"
                    + "LEFT JOIN `tb_spk_detail` ON `tb_spk`.`kode_spk` = `tb_spk_detail`.`kode_spk`"
                    + "LEFT JOIN `tb_box_packing` ON `tb_spk_detail`.`no` = `tb_box_packing`.`no_grade_spk` "
                    + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_box_packing`.`no_box` = `tb_box_bahan_jadi`.`no_box` "
                    + "WHERE `tb_spk`.`status` = 'PROSES' AND `tb_spk`.`kode_spk` NOT IN ('Sample Internal')"
                    + "GROUP BY `kode_spk` ORDER BY `tanggal_tk` DESC";
//            System.out.println(sql);
            pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[8];
            decimalFormat.setMaximumFractionDigits(1);
            while (rs.next()) {
                row[0] = rs.getString("kode_spk");
                row[1] = rs.getString("nama");
                row[2] = rs.getDate("tanggal_tk");
                row[3] = null;
                row[4] = decimalFormat.format(rs.getFloat("berat"));
                row[5] = null;
                row[6] = decimalFormat.format(rs.getFloat("progress"));
//                row[7] = Math.round((rs.getFloat("progress") / rs.getFloat("berat")) * 1000.f) / 10.f;
                row[7] = decimalFormat.format((rs.getFloat("progress") / rs.getFloat("berat")) * 100.d);
//                System.out.println(row_counter1);
                if (row_counter1 < 26) {
                    model1.addRow(row);
                    row_counter1++;
                } else {
                    model2.addRow(row);
                    row_counter2++;
                }

                String query_detail = "SELECT `no`, `grade_waleta`, `grade_buyer`, `berat_kemasan`, `jumlah_kemasan`, `tb_spk_detail`.`berat`, `harga_cny`, `keterangan`, `kode_spk`, "
                        + "SUM(IF((`tb_box_packing`.`status` <> 'PENDING' OR `tb_box_packing`.`status` IS NULL), `tb_box_bahan_jadi`.`berat`, 0)) AS 'progress'"
                        + "FROM `tb_spk_detail` "
                        + "LEFT JOIN `tb_box_packing` ON `tb_spk_detail`.`no` = `tb_box_packing`.`no_grade_spk` "
                        + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_box_bahan_jadi`.`no_box` = `tb_box_packing`.`no_box` "
                        + "WHERE `kode_spk` = '" + rs.getString("kode_spk") + "' "
                        + "GROUP BY `no`";
//                System.out.println(query_detail);
                PreparedStatement psts = Utility.db.getConnection().prepareStatement(query_detail);
                ResultSet rst = psts.executeQuery();
                while (rst.next()) {
                    row[0] = rst.getString("grade_waleta");
                    row[1] = rst.getString("grade_buyer");
                    row[2] = rst.getInt("berat_kemasan");
                    row[3] = decimalFormat.format(rst.getFloat("jumlah_kemasan"));
                    row[4] = decimalFormat.format(rst.getFloat("berat"));
                    row[5] = rst.getString("keterangan");
                    row[6] = decimalFormat.format(rst.getFloat("progress"));
//                    row[7] = Math.round((rst.getFloat("progress") / rst.getFloat("berat")) * 1000.f) / 10.f;
                    if (rst.getFloat("berat") > 0) {
                        row[7] = decimalFormat.format((rst.getFloat("progress") / rst.getFloat("berat")) * 100.d);
                    } else {
                        row[7] = "-";
                    }
                    if (row_counter1 < 26) {
                        model1.addRow(row);
                        row_counter1++;
                    } else {
                        model2.addRow(row);
                        row_counter2++;
                    }
                }
//                if (row_counter1 < 27) {
//                    model1.addRow(new Object[]{});
//                    row_counter1++;
//                } else {
//                    model2.addRow(new Object[]{});
//                    row_counter2++;
//                }
            }

            Tabel_Detail_SPK.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (row == 0) {
                        check = 1;
                    } else {
                        check = 0;
                    }
                    if (Tabel_Detail_SPK.getValueAt(row, 3) == null) {
                        if (baris != row) {
                            if (check == 1) {
                                spk = 0;
                            }
                            baris = row;
                            spk++;
//                            System.out.println("spk = " + spk);
//                            System.out.println("row = " + row);
                        }
                        comp.setFont(new Font("Arial", Font.BOLD, 16));
                    }
                    if (Tabel_Detail_SPK.getValueAt(row, 2) != null) {
                        if (isSelected) {
                            comp.setBackground(Tabel_Detail_SPK.getSelectionBackground());
                            comp.setForeground(Tabel_Detail_SPK.getSelectionForeground());
                        } else {
                            comp.setBackground(new Color(R[spk], G[spk], B[spk]));
                            comp.setForeground(Color.BLACK);
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(Tabel_Detail_SPK.getSelectionBackground());
                            comp.setForeground(Tabel_Detail_SPK.getSelectionForeground());
                        } else {
                            comp.setBackground(Tabel_Detail_SPK.getBackground());
                            comp.setForeground(Tabel_Detail_SPK.getForeground());
                        }
                    }
                    return comp;
                }
            });
            Tabel_Detail_SPK.repaint();
            Tabel_Detail_SPK1.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (row == 0) {
                        check = 1;
                    } else {
                        check = 0;
                    }
                    if (Tabel_Detail_SPK1.getValueAt(row, 3) == null) {
                        if (baris != row) {
                            if (check == 1) {
                                spk = 0;
                            }
                            baris = row;
                            spk++;
//                            System.out.println("spk = " + spk);
//                            System.out.println("row = " + row);
                        }
                        comp.setFont(new Font("Arial", Font.BOLD, 16));
                    }
                    if (Tabel_Detail_SPK1.getValueAt(row, 2) != null) {
                        if (isSelected) {
                            comp.setBackground(Tabel_Detail_SPK1.getSelectionBackground());
                            comp.setForeground(Tabel_Detail_SPK1.getSelectionForeground());
                        } else {
                            comp.setBackground(new Color(R[spk], G[spk], B[spk]));
                            comp.setForeground(Color.BLACK);
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(Tabel_Detail_SPK1.getSelectionBackground());
                            comp.setForeground(Tabel_Detail_SPK1.getSelectionForeground());
                        } else {
                            comp.setBackground(Tabel_Detail_SPK1.getBackground());
                            comp.setForeground(Tabel_Detail_SPK1.getForeground());
                        }
                    }
                    return comp;
                }
            });
            Tabel_Detail_SPK1.repaint();

            ColumnsAutoSizer.sizeColumnsToFit(Tabel_Detail_SPK);
            ColumnsAutoSizer.sizeColumnsToFit(Tabel_Detail_SPK1);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JFrame_TV_SPK_packing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void refreshIsu1() {
        try {
            int data = 0;
            sql = "SELECT * FROM `tb_isu_produksi` WHERE `dept` LIKE (`dept` LIKE '%ALL%' OR `dept` LIKE '%PACKING%') AND `tanggal_selesai` IS NULL ORDER BY `tanggal_isu` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                if (data == 0) {
                    jTextArea1.setText(rs.getString("masalah"));
                    label_kode.setText(rs.getString("kode_isu"));
                    label_tgl_isu.setText(new SimpleDateFormat("dd MMMM yyyy").format(rs.getDate("tanggal_isu")));
                    label_departemen.setText(rs.getString("dept"));
                    label_penanggungjawab.setText(rs.getString("penanggungjawab"));
                    label_image.setIcon(Utility.ResizeImage(null, rs.getBytes("gambar_isu"), label_image.getWidth(), label_image.getHeight()));
                }
                data++;
            }

        } catch (Exception ex) {
            Logger.getLogger(JFrame_TV_SPK_packing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshIsu2() {
        try {
            int data = 0;
            sql = "SELECT * FROM `tb_isu_produksi` WHERE `dept` LIKE (`dept` LIKE '%ALL%' OR `dept` LIKE '%PACKING%') AND `tanggal_selesai` IS NULL ORDER BY `tanggal_isu` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                if (data == 1) {
                    jTextArea2.setText(rs.getString("masalah"));
                    label_kode1.setText(rs.getString("kode_isu"));
                    label_tgl_isu1.setText(new SimpleDateFormat("dd MMMM yyyy").format(rs.getDate("tanggal_isu")));
                    label_departemen1.setText(rs.getString("dept"));
                    label_penanggungjawab1.setText(rs.getString("penanggungjawab"));
                    label_image1.setIcon(Utility.ResizeImage(null, rs.getBytes("gambar_isu"), label_image1.getWidth(), label_image1.getHeight()));
                }
                data++;
            }

        } catch (Exception ex) {
            Logger.getLogger(JFrame_TV_SPK_packing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshIsu3() {
        try {
            int data = 0;
            sql = "SELECT * FROM `tb_isu_produksi` WHERE `dept` LIKE (`dept` LIKE '%ALL%' OR `dept` LIKE '%PACKING%') AND `tanggal_selesai` IS NULL ORDER BY `tanggal_isu` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                if (data == 2) {
                    jTextArea3.setText(rs.getString("masalah"));
                    label_kode2.setText(rs.getString("kode_isu"));
                    label_tgl_isu2.setText(new SimpleDateFormat("dd MMMM yyyy").format(rs.getDate("tanggal_isu")));
                    label_departemen2.setText(rs.getString("dept"));
                    label_penanggungjawab2.setText(rs.getString("penanggungjawab"));
                    label_image2.setIcon(Utility.ResizeImage(null, rs.getBytes("gambar_isu"), label_image2.getWidth(), label_image2.getHeight()));
                }
                data++;
            }

        } catch (Exception ex) {
            Logger.getLogger(JFrame_TV_SPK_packing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshIsu4() {
        try {
            int data = 0;
            sql = "SELECT * FROM `tb_isu_produksi` WHERE `dept` LIKE (`dept` LIKE '%ALL%' OR `dept` LIKE '%PACKING%') AND `tanggal_selesai` IS NULL ORDER BY `tanggal_isu` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                if (data == 3) {
                    jTextArea4.setText(rs.getString("masalah"));
                    label_kode3.setText(rs.getString("kode_isu"));
                    label_tgl_isu3.setText(new SimpleDateFormat("dd MMMM yyyy").format(rs.getDate("tanggal_isu")));
                    label_departemen3.setText(rs.getString("dept"));
                    label_penanggungjawab3.setText(rs.getString("penanggungjawab"));
                    label_image3.setIcon(Utility.ResizeImage(null, rs.getBytes("gambar_isu"), label_image3.getWidth(), label_image3.getHeight()));
                }
                data++;
            }

        } catch (Exception ex) {
            Logger.getLogger(JFrame_TV_SPK_packing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshIsu5() {
        try {
            int data = 0;
            sql = "SELECT * FROM `tb_isu_produksi` WHERE `dept` LIKE (`dept` LIKE '%ALL%' OR `dept` LIKE '%PACKING%') AND `tanggal_selesai` IS NULL ORDER BY `tanggal_isu` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                if (data == 4) {
                    jTextArea5.setText(rs.getString("masalah"));
                    label_kode4.setText(rs.getString("kode_isu"));
                    label_tgl_isu4.setText(new SimpleDateFormat("dd MMMM yyyy").format(rs.getDate("tanggal_isu")));
                    label_departemen4.setText(rs.getString("dept"));
                    label_penanggungjawab4.setText(rs.getString("penanggungjawab"));
                    label_image4.setIcon(Utility.ResizeImage(null, rs.getBytes("gambar_isu"), label_image4.getWidth(), label_image4.getHeight()));
                }
                data++;
            }

        } catch (Exception ex) {
            Logger.getLogger(JFrame_TV_SPK_packing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Tabel_Detail_SPK = new javax.swing.JTable();
        label_jam = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Tabel_Detail_SPK1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jPanel_isu1 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        label_tgl_isu = new javax.swing.JLabel();
        label_departemen = new javax.swing.JLabel();
        label_image = new javax.swing.JLabel();
        label_penanggungjawab = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        label_kode = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jPanel_isu2 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        label_tgl_isu1 = new javax.swing.JLabel();
        label_departemen1 = new javax.swing.JLabel();
        label_image1 = new javax.swing.JLabel();
        label_penanggungjawab1 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        label_kode1 = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jPanel_isu3 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        label_tgl_isu2 = new javax.swing.JLabel();
        label_departemen2 = new javax.swing.JLabel();
        label_image2 = new javax.swing.JLabel();
        label_penanggungjawab2 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        label_kode2 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        jPanel_isu4 = new javax.swing.JPanel();
        label_kode3 = new javax.swing.JLabel();
        label_tgl_isu3 = new javax.swing.JLabel();
        label_departemen3 = new javax.swing.JLabel();
        label_penanggungjawab3 = new javax.swing.JLabel();
        label_image3 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jScrollPane10 = new javax.swing.JScrollPane();
        jTextArea4 = new javax.swing.JTextArea();
        jPanel_isu5 = new javax.swing.JPanel();
        jLabel40 = new javax.swing.JLabel();
        label_tgl_isu4 = new javax.swing.JLabel();
        label_departemen4 = new javax.swing.JLabel();
        label_image4 = new javax.swing.JLabel();
        label_penanggungjawab4 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        label_kode4 = new javax.swing.JLabel();
        jScrollPane11 = new javax.swing.JScrollPane();
        jTextArea5 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Surat Perintah Kerja");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        Tabel_Detail_SPK.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Tabel_Detail_SPK.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade WLT", "Grade Buyer", "Berat / Kemasan", "Pack", "Net Weight", "Ket.", "Turun", "%"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Tabel_Detail_SPK.setRowHeight(25);
        Tabel_Detail_SPK.setRowSelectionAllowed(false);
        Tabel_Detail_SPK.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Tabel_Detail_SPK);
        if (Tabel_Detail_SPK.getColumnModel().getColumnCount() > 0) {
            Tabel_Detail_SPK.getColumnModel().getColumn(1).setMaxWidth(150);
        }

        label_jam.setBackground(new java.awt.Color(255, 255, 255));
        label_jam.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        label_jam.setText("Jam :");

        Tabel_Detail_SPK1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        Tabel_Detail_SPK1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade WLT", "Grade Buyer", "Berat / Kemasan", "Pack", "Net Weight", "Ket.", "Turun", "%"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Tabel_Detail_SPK1.setRowHeight(25);
        Tabel_Detail_SPK1.setRowSelectionAllowed(false);
        Tabel_Detail_SPK1.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(Tabel_Detail_SPK1);
        if (Tabel_Detail_SPK1.getColumnModel().getColumnCount() > 0) {
            Tabel_Detail_SPK1.getColumnModel().getColumn(1).setMaxWidth(150);
        }

        jButton1.setText("Refresh");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 695, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 680, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_jam)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_jam, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 662, Short.MAX_VALUE)
                    .addComponent(jScrollPane2)))
        );

        jTabbedPane1.addTab("DATA SPK", jPanel1);

        jPanel_isu1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel10.setText("Departemen :");

        label_tgl_isu.setBackground(new java.awt.Color(255, 255, 255));
        label_tgl_isu.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_tgl_isu.setText("-");

        label_departemen.setBackground(new java.awt.Color(255, 255, 255));
        label_departemen.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_departemen.setText("-");

        label_image.setBackground(new java.awt.Color(255, 255, 255));
        label_image.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_penanggungjawab.setBackground(new java.awt.Color(255, 255, 255));
        label_penanggungjawab.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_penanggungjawab.setText("-");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel8.setText("Penanggung Jawab :");

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel18.setText("Tanggal isu :");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel3.setText("Kode : ");

        label_kode.setBackground(new java.awt.Color(255, 255, 255));
        label_kode.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_kode.setText("-");

        jScrollPane8.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane8.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(5);
        jTextArea1.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(2);
        jTextArea1.setText("-");
        jTextArea1.setWrapStyleWord(true);
        jScrollPane8.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel_isu1Layout = new javax.swing.GroupLayout(jPanel_isu1);
        jPanel_isu1.setLayout(jPanel_isu1Layout);
        jPanel_isu1Layout.setHorizontalGroup(
            jPanel_isu1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_isu1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_image, javax.swing.GroupLayout.PREFERRED_SIZE, 827, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_isu1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_isu1Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_kode, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_isu1Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_penanggungjawab, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_isu1Layout.createSequentialGroup()
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_tgl_isu, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_isu1Layout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_departemen, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 508, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel_isu1Layout.setVerticalGroup(
            jPanel_isu1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_isu1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_isu1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_image, javax.swing.GroupLayout.PREFERRED_SIZE, 645, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_isu1Layout.createSequentialGroup()
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_isu1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(label_kode))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_isu1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel18)
                            .addComponent(label_tgl_isu))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_isu1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(label_departemen))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_isu1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(label_penanggungjawab))))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Isu 1", jPanel_isu1);

        jPanel_isu2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel26.setText("Departemen :");

        label_tgl_isu1.setBackground(new java.awt.Color(255, 255, 255));
        label_tgl_isu1.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_tgl_isu1.setText("-");

        label_departemen1.setBackground(new java.awt.Color(255, 255, 255));
        label_departemen1.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_departemen1.setText("-");

        label_image1.setBackground(new java.awt.Color(255, 255, 255));
        label_image1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_penanggungjawab1.setBackground(new java.awt.Color(255, 255, 255));
        label_penanggungjawab1.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_penanggungjawab1.setText("-");

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel27.setText("Penanggung Jawab :");

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel28.setText("Tanggal isu :");

        jLabel30.setBackground(new java.awt.Color(255, 255, 255));
        jLabel30.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel30.setText("Kode : ");

        label_kode1.setBackground(new java.awt.Color(255, 255, 255));
        label_kode1.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_kode1.setText("-");

        jScrollPane9.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane9.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N

        jTextArea2.setEditable(false);
        jTextArea2.setColumns(5);
        jTextArea2.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jTextArea2.setLineWrap(true);
        jTextArea2.setRows(2);
        jTextArea2.setText("-");
        jTextArea2.setWrapStyleWord(true);
        jScrollPane9.setViewportView(jTextArea2);

        javax.swing.GroupLayout jPanel_isu2Layout = new javax.swing.GroupLayout(jPanel_isu2);
        jPanel_isu2.setLayout(jPanel_isu2Layout);
        jPanel_isu2Layout.setHorizontalGroup(
            jPanel_isu2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_isu2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_image1, javax.swing.GroupLayout.PREFERRED_SIZE, 829, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_isu2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel_isu2Layout.createSequentialGroup()
                        .addGroup(jPanel_isu2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_isu2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(label_departemen1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_penanggungjawab1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_kode1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_tgl_isu1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane9))
                .addContainerGap())
        );
        jPanel_isu2Layout.setVerticalGroup(
            jPanel_isu2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_isu2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_isu2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_image1, javax.swing.GroupLayout.PREFERRED_SIZE, 645, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_isu2Layout.createSequentialGroup()
                        .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_isu2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_isu2Layout.createSequentialGroup()
                                .addComponent(jLabel30)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel28))
                            .addGroup(jPanel_isu2Layout.createSequentialGroup()
                                .addComponent(label_kode1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_tgl_isu1)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_isu2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_isu2Layout.createSequentialGroup()
                                .addComponent(jLabel26)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel27))
                            .addGroup(jPanel_isu2Layout.createSequentialGroup()
                                .addComponent(label_departemen1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_penanggungjawab1)))))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Isu 2", jPanel_isu2);

        jPanel_isu3.setBackground(new java.awt.Color(255, 255, 255));

        jLabel32.setBackground(new java.awt.Color(255, 255, 255));
        jLabel32.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel32.setText("Departemen :");

        label_tgl_isu2.setBackground(new java.awt.Color(255, 255, 255));
        label_tgl_isu2.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_tgl_isu2.setText("-");

        label_departemen2.setBackground(new java.awt.Color(255, 255, 255));
        label_departemen2.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_departemen2.setText("-");

        label_image2.setBackground(new java.awt.Color(255, 255, 255));
        label_image2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_penanggungjawab2.setBackground(new java.awt.Color(255, 255, 255));
        label_penanggungjawab2.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_penanggungjawab2.setText("-");

        jLabel33.setBackground(new java.awt.Color(255, 255, 255));
        jLabel33.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel33.setText("Penanggung Jawab :");

        jLabel34.setBackground(new java.awt.Color(255, 255, 255));
        jLabel34.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel34.setText("Tanggal isu :");

        jLabel36.setBackground(new java.awt.Color(255, 255, 255));
        jLabel36.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel36.setText("Kode : ");

        label_kode2.setBackground(new java.awt.Color(255, 255, 255));
        label_kode2.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_kode2.setText("-");

        jScrollPane7.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane7.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N

        jTextArea3.setEditable(false);
        jTextArea3.setColumns(5);
        jTextArea3.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jTextArea3.setLineWrap(true);
        jTextArea3.setRows(2);
        jTextArea3.setText("-");
        jTextArea3.setWrapStyleWord(true);
        jScrollPane7.setViewportView(jTextArea3);

        javax.swing.GroupLayout jPanel_isu3Layout = new javax.swing.GroupLayout(jPanel_isu3);
        jPanel_isu3.setLayout(jPanel_isu3Layout);
        jPanel_isu3Layout.setHorizontalGroup(
            jPanel_isu3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_isu3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_image2, javax.swing.GroupLayout.PREFERRED_SIZE, 829, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_isu3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel_isu3Layout.createSequentialGroup()
                        .addGroup(jPanel_isu3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_isu3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(label_departemen2, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_penanggungjawab2, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_kode2, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_tgl_isu2, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane7))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_isu3Layout.setVerticalGroup(
            jPanel_isu3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_isu3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_isu3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_image2, javax.swing.GroupLayout.PREFERRED_SIZE, 645, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_isu3Layout.createSequentialGroup()
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_isu3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_isu3Layout.createSequentialGroup()
                                .addComponent(jLabel36)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel34))
                            .addGroup(jPanel_isu3Layout.createSequentialGroup()
                                .addComponent(label_kode2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_tgl_isu2)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_isu3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_isu3Layout.createSequentialGroup()
                                .addComponent(jLabel32)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel33))
                            .addGroup(jPanel_isu3Layout.createSequentialGroup()
                                .addComponent(label_departemen2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_penanggungjawab2)))))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Isu 3", jPanel_isu3);

        jPanel_isu4.setBackground(new java.awt.Color(255, 255, 255));

        label_kode3.setBackground(new java.awt.Color(255, 255, 255));
        label_kode3.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_kode3.setText("-");

        label_tgl_isu3.setBackground(new java.awt.Color(255, 255, 255));
        label_tgl_isu3.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_tgl_isu3.setText("-");

        label_departemen3.setBackground(new java.awt.Color(255, 255, 255));
        label_departemen3.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_departemen3.setText("-");

        label_penanggungjawab3.setBackground(new java.awt.Color(255, 255, 255));
        label_penanggungjawab3.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_penanggungjawab3.setText("-");

        label_image3.setBackground(new java.awt.Color(255, 255, 255));
        label_image3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel35.setBackground(new java.awt.Color(255, 255, 255));
        jLabel35.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel35.setText("Departemen :");

        jLabel37.setBackground(new java.awt.Color(255, 255, 255));
        jLabel37.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel37.setText("Penanggung Jawab :");

        jLabel38.setBackground(new java.awt.Color(255, 255, 255));
        jLabel38.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel38.setText("Tanggal isu :");

        jLabel39.setBackground(new java.awt.Color(255, 255, 255));
        jLabel39.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel39.setText("Kode : ");

        jScrollPane10.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane10.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N

        jTextArea4.setEditable(false);
        jTextArea4.setColumns(5);
        jTextArea4.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jTextArea4.setLineWrap(true);
        jTextArea4.setRows(2);
        jTextArea4.setText("-");
        jTextArea4.setWrapStyleWord(true);
        jScrollPane10.setViewportView(jTextArea4);

        javax.swing.GroupLayout jPanel_isu4Layout = new javax.swing.GroupLayout(jPanel_isu4);
        jPanel_isu4.setLayout(jPanel_isu4Layout);
        jPanel_isu4Layout.setHorizontalGroup(
            jPanel_isu4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_isu4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_image3, javax.swing.GroupLayout.PREFERRED_SIZE, 829, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_isu4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel_isu4Layout.createSequentialGroup()
                        .addGroup(jPanel_isu4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_isu4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(label_departemen3, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_penanggungjawab3, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_kode3, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_tgl_isu3, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane10))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_isu4Layout.setVerticalGroup(
            jPanel_isu4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_isu4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_isu4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_image3, javax.swing.GroupLayout.PREFERRED_SIZE, 645, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_isu4Layout.createSequentialGroup()
                        .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_isu4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_isu4Layout.createSequentialGroup()
                                .addComponent(jLabel39)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel38))
                            .addGroup(jPanel_isu4Layout.createSequentialGroup()
                                .addComponent(label_kode3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_tgl_isu3)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_isu4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_isu4Layout.createSequentialGroup()
                                .addComponent(jLabel35)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel37))
                            .addGroup(jPanel_isu4Layout.createSequentialGroup()
                                .addComponent(label_departemen3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_penanggungjawab3)))))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Isu 4", jPanel_isu4);

        jPanel_isu5.setBackground(new java.awt.Color(255, 255, 255));

        jLabel40.setBackground(new java.awt.Color(255, 255, 255));
        jLabel40.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel40.setText("Departemen :");

        label_tgl_isu4.setBackground(new java.awt.Color(255, 255, 255));
        label_tgl_isu4.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_tgl_isu4.setText("-");

        label_departemen4.setBackground(new java.awt.Color(255, 255, 255));
        label_departemen4.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_departemen4.setText("-");

        label_image4.setBackground(new java.awt.Color(255, 255, 255));
        label_image4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_penanggungjawab4.setBackground(new java.awt.Color(255, 255, 255));
        label_penanggungjawab4.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_penanggungjawab4.setText("-");

        jLabel41.setBackground(new java.awt.Color(255, 255, 255));
        jLabel41.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel41.setText("Penanggung Jawab :");

        jLabel42.setBackground(new java.awt.Color(255, 255, 255));
        jLabel42.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel42.setText("Tanggal isu :");

        jLabel43.setBackground(new java.awt.Color(255, 255, 255));
        jLabel43.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel43.setText("Kode : ");

        label_kode4.setBackground(new java.awt.Color(255, 255, 255));
        label_kode4.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        label_kode4.setText("-");

        jScrollPane11.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane11.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N

        jTextArea5.setEditable(false);
        jTextArea5.setColumns(5);
        jTextArea5.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jTextArea5.setLineWrap(true);
        jTextArea5.setRows(2);
        jTextArea5.setText("-");
        jTextArea5.setWrapStyleWord(true);
        jScrollPane11.setViewportView(jTextArea5);

        javax.swing.GroupLayout jPanel_isu5Layout = new javax.swing.GroupLayout(jPanel_isu5);
        jPanel_isu5.setLayout(jPanel_isu5Layout);
        jPanel_isu5Layout.setHorizontalGroup(
            jPanel_isu5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_isu5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_image4, javax.swing.GroupLayout.PREFERRED_SIZE, 829, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_isu5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel_isu5Layout.createSequentialGroup()
                        .addGroup(jPanel_isu5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_isu5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(label_departemen4, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_penanggungjawab4, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_kode4, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_tgl_isu4, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane11))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_isu5Layout.setVerticalGroup(
            jPanel_isu5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_isu5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_isu5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_image4, javax.swing.GroupLayout.PREFERRED_SIZE, 645, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_isu5Layout.createSequentialGroup()
                        .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_isu5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_isu5Layout.createSequentialGroup()
                                .addComponent(jLabel43)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel42))
                            .addGroup(jPanel_isu5Layout.createSequentialGroup()
                                .addComponent(label_kode4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_tgl_isu4)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_isu5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_isu5Layout.createSequentialGroup()
                                .addComponent(jLabel40)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel41))
                            .addGroup(jPanel_isu5Layout.createSequentialGroup()
                                .addComponent(label_departemen4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_penanggungjawab4)))))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Isu 5", jPanel_isu5);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
        t.cancel();
    }//GEN-LAST:event_formWindowClosed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        refreshTable_SPK();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        // TODO add your handling code here:
        switch (jTabbedPane1.getSelectedIndex()) {
            case 0:
                refreshTable_SPK();
                break;
            case 1:
                refreshIsu1();
                break;
            case 2:
                refreshIsu2();
                break;
            case 3:
                refreshIsu3();
                break;
            case 4:
                refreshIsu4();
                break;
            case 5:
                refreshIsu5();
                break;
            default:
                refreshTable_SPK();
                break;
        }
    }//GEN-LAST:event_jTabbedPane1StateChanged

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame_TV_SPK_packing.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame_TV_SPK_packing chart = new JFrame_TV_SPK_packing();
                chart.pack();
                chart.setResizable(true);
                chart.setLocationRelativeTo(null);
                chart.setVisible(true);
                chart.setEnabled(true);
                chart.setExtendedState(JFrame.MAXIMIZED_BOTH);
                chart.init();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable Tabel_Detail_SPK;
    private javax.swing.JTable Tabel_Detail_SPK1;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel_isu1;
    private javax.swing.JPanel jPanel_isu2;
    private javax.swing.JPanel jPanel_isu3;
    private javax.swing.JPanel jPanel_isu4;
    private javax.swing.JPanel jPanel_isu5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTextArea jTextArea4;
    private javax.swing.JTextArea jTextArea5;
    private javax.swing.JLabel label_departemen;
    private javax.swing.JLabel label_departemen1;
    private javax.swing.JLabel label_departemen2;
    private javax.swing.JLabel label_departemen3;
    private javax.swing.JLabel label_departemen4;
    private javax.swing.JLabel label_image;
    private javax.swing.JLabel label_image1;
    private javax.swing.JLabel label_image2;
    private javax.swing.JLabel label_image3;
    private javax.swing.JLabel label_image4;
    private javax.swing.JLabel label_jam;
    private javax.swing.JLabel label_kode;
    private javax.swing.JLabel label_kode1;
    private javax.swing.JLabel label_kode2;
    private javax.swing.JLabel label_kode3;
    private javax.swing.JLabel label_kode4;
    private javax.swing.JLabel label_penanggungjawab;
    private javax.swing.JLabel label_penanggungjawab1;
    private javax.swing.JLabel label_penanggungjawab2;
    private javax.swing.JLabel label_penanggungjawab3;
    private javax.swing.JLabel label_penanggungjawab4;
    private javax.swing.JLabel label_tgl_isu;
    private javax.swing.JLabel label_tgl_isu1;
    private javax.swing.JLabel label_tgl_isu2;
    private javax.swing.JLabel label_tgl_isu3;
    private javax.swing.JLabel label_tgl_isu4;
    // End of variables declaration//GEN-END:variables
}
