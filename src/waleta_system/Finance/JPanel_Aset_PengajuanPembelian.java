package waleta_system.Finance;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;
import waleta_system.Class.Utility;
import waleta_system.JFrame_TV_PengajuanPembelian;
import waleta_system.MainForm;

public class JPanel_Aset_PengajuanPembelian extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public JPanel_Aset_PengajuanPembelian() {
        initComponents();
    }

    public void init() {
        try {
            ComboBox_filter_departemen.removeAllItems();
            ComboBox_filter_departemen.addItem("All");
            sql = "SELECT `kode_dep` FROM `tb_departemen`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_filter_departemen.addItem(rs.getString("kode_dep"));
            }
            refreshTable();
            table_pengajuan.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && table_pengajuan.getSelectedRow() != -1) {
                        int i = table_pengajuan.getSelectedRow();
                        if (i > -1) {
                        }
                    }
                }
            });
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_Aset_PengajuanPembelian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) table_pengajuan.getModel();
            model.setRowCount(0);
            String filter_status = "";
            if (ComboBox_filter_status.getSelectedIndex() > 0) {
                filter_status = "AND `tb_aset_pengajuan`.`status` = '" + ComboBox_filter_status.getSelectedItem().toString() + "' \n";
            }
            String filter_departemen = "";
            if (ComboBox_filter_departemen.getSelectedIndex() > 0) {
                filter_departemen = "AND `tb_aset_pengajuan`.`departemen` = '" + ComboBox_filter_departemen.getSelectedItem().toString() + "' \n";
            }
            String filter_tgl_pengajuan = "";
            if (Date_pengajuan1.getDate() != null && Date_pengajuan2.getDate() != null) {
                filter_tgl_pengajuan = "AND `tanggal_pengajuan` BETWEEN '" + dateFormat.format(Date_pengajuan1.getDate()) + "' AND '" + dateFormat.format(Date_pengajuan2.getDate()) + "'";
            }
            sql = "SELECT "
                    + "`no`, `tanggal_pengajuan`, `tb_aset_pengajuan`.`departemen`, `keperluan`, `nama_barang`, `jumlah`, `estimasi_harga_satuan`, `link_pembelian`, `dibutuhkan_tanggal`, `nama_pegawai` AS 'diajukan', `diketahui_kadep`, `diketahui`, `disetujui`, `diproses`, `jenis_pembelian`, `tb_aset_pengajuan`.`status`, `tb_aset_pengajuan`.`keterangan`,"
                    + " `realisasi_harga_satuan`, `ppn`, `biaya_lain`, `keterangan_biaya_lain`, `nama_bank`, `no_rekening`, `nama_rekening` \n"
                    + "FROM `tb_aset_pengajuan` \n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_aset_pengajuan`.`diajukan` = `tb_karyawan`.`id_pegawai` \n"
                    + "WHERE \n"
                    + "(`no` LIKE '%" + txt_search_keyword.getText() + "%' OR "
                    + "`nama_barang` LIKE '%" + txt_search_keyword.getText() + "%' OR "
                    + "`keperluan` LIKE '%" + txt_search_keyword.getText() + "%') \n"
                    + filter_tgl_pengajuan
                    + filter_status
                    + filter_departemen;
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[30];
            while (rs.next()) {
                row[0] = rs.getInt("no");
                row[1] = rs.getDate("tanggal_pengajuan");
                row[2] = rs.getString("departemen");
                row[3] = rs.getString("keperluan");
                row[4] = rs.getString("nama_barang");
                row[5] = rs.getInt("jumlah");
                row[6] = rs.getInt("estimasi_harga_satuan");
                row[7] = rs.getInt("jumlah") * rs.getInt("estimasi_harga_satuan");
                row[8] = rs.getDate("dibutuhkan_tanggal");
                row[9] = rs.getString("diajukan");
                row[10] = rs.getString("diketahui_kadep");
                row[11] = rs.getString("diketahui");
                row[12] = rs.getString("disetujui");
                row[13] = rs.getString("diproses");
                row[14] = rs.getString("jenis_pembelian");
                row[15] = rs.getString("status");
                row[16] = rs.getString("keterangan");
                row[17] = rs.getString("link_pembelian");
                row[18] = rs.getInt("realisasi_harga_satuan");
                row[19] = rs.getInt("ppn");
                row[20] = Math.round(rs.getFloat("ppn") / 100f * (rs.getFloat("jumlah") * rs.getFloat("realisasi_harga_satuan")));
                row[21] = rs.getInt("biaya_lain");
                row[22] = rs.getString("keterangan_biaya_lain");
                row[23] = Math.round((rs.getFloat("jumlah") * rs.getFloat("realisasi_harga_satuan"))
                        + (rs.getFloat("ppn") / 100f * (rs.getFloat("jumlah") * rs.getFloat("realisasi_harga_satuan")))
                        + rs.getFloat("biaya_lain"));
                row[24] = rs.getString("nama_bank");
                row[25] = rs.getString("no_rekening");
                row[26] = rs.getString("nama_rekening");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_pengajuan);
            label_total_data_nota.setText(decimalFormat.format(table_pengajuan.getRowCount()));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Aset_PengajuanPembelian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_pengajuan = new javax.swing.JTable();
        button_new = new javax.swing.JButton();
        button_edit = new javax.swing.JButton();
        button_delete = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        txt_search_keyword = new javax.swing.JTextField();
        button_search = new javax.swing.JButton();
        button_export = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        label_total_data_nota = new javax.swing.JLabel();
        button_diketahui = new javax.swing.JButton();
        button_disetujui = new javax.swing.JButton();
        button_diproses = new javax.swing.JButton();
        button_selesai = new javax.swing.JButton();
        button_buka_link = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        ComboBox_filter_status = new javax.swing.JComboBox<>();
        button_dikirim = new javax.swing.JButton();
        button_sampai = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        button_diketahui_kadep = new javax.swing.JButton();
        button_tv_pengajuan = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        ComboBox_filter_departemen = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        Date_pengajuan1 = new com.toedter.calendar.JDateChooser();
        Date_pengajuan2 = new com.toedter.calendar.JDateChooser();
        button_realisasi = new javax.swing.JButton();
        button_form_pengajuan = new javax.swing.JButton();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        table_pengajuan.setAutoCreateRowSorter(true);
        table_pengajuan.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_pengajuan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "Tgl Pengajuan", "Departemen", "Keperluan", "Nama Barang", "Jumlah", "Est. Harga", "Est. Total", "Dibutuhkan Tgl", "Diajukan Oleh", "Diketahui Kadep", "Diketahui Oleh", "Disetujui Oleh", "Diproses Oleh", "Jenis Pembelian", "Status", "Keterangan", "Link Pembelian", "Realisasi Harga (Rp.)", "PPN %", "PPN (Rp.)", "Biaya Lain (Rp.)", "Ket Biaya", "Total (Rp.)", "Nama Bank", "No Rekening", "Nama Rekening"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_pengajuan.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table_pengajuan);

        button_new.setBackground(new java.awt.Color(255, 255, 255));
        button_new.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_new.setText("Tambah Baru");
        button_new.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_newActionPerformed(evt);
            }
        });

        button_edit.setBackground(new java.awt.Color(255, 255, 255));
        button_edit.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_edit.setText("Edit");
        button_edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_editActionPerformed(evt);
            }
        });

        button_delete.setBackground(new java.awt.Color(255, 255, 255));
        button_delete.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_delete.setText("Delete");
        button_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_deleteActionPerformed(evt);
            }
        });

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("Search Keyword :");

        txt_search_keyword.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_keyword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_keywordKeyPressed(evt);
            }
        });

        button_search.setBackground(new java.awt.Color(255, 255, 255));
        button_search.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_search.setText("Search");
        button_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_searchActionPerformed(evt);
            }
        });

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_export.setText("Export To Excel");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel16.setText("Pengajuan Pembelian Alat Kerja");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Total Data :");

        label_total_data_nota.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_nota.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        label_total_data_nota.setText("0000");

        button_diketahui.setBackground(new java.awt.Color(255, 255, 255));
        button_diketahui.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_diketahui.setText("Diketahui");
        button_diketahui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_diketahuiActionPerformed(evt);
            }
        });

        button_disetujui.setBackground(new java.awt.Color(255, 255, 255));
        button_disetujui.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_disetujui.setText("Disetujui");
        button_disetujui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_disetujuiActionPerformed(evt);
            }
        });

        button_diproses.setBackground(new java.awt.Color(255, 255, 255));
        button_diproses.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_diproses.setText("Diproses");
        button_diproses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_diprosesActionPerformed(evt);
            }
        });

        button_selesai.setBackground(new java.awt.Color(255, 255, 255));
        button_selesai.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_selesai.setText("Selesai");
        button_selesai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_selesaiActionPerformed(evt);
            }
        });

        button_buka_link.setBackground(new java.awt.Color(255, 255, 255));
        button_buka_link.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_buka_link.setText("Copy Link");
        button_buka_link.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_buka_linkActionPerformed(evt);
            }
        });

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText("Status :");

        ComboBox_filter_status.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        ComboBox_filter_status.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Diajukan", "Diketahui Kadep", "Diketahui", "Disetujui", "Proses", "Dikirim", "Sampai", "Selesai" }));

        button_dikirim.setBackground(new java.awt.Color(255, 255, 255));
        button_dikirim.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_dikirim.setText("Dikirim");
        button_dikirim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_dikirimActionPerformed(evt);
            }
        });

        button_sampai.setBackground(new java.awt.Color(255, 255, 255));
        button_sampai.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_sampai.setText("Sampai");
        button_sampai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_sampaiActionPerformed(evt);
            }
        });

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        jTextArea1.setRows(5);
        jTextArea1.setText("Rules : \n1. hanya yang melakukan pengajuan yang bisa edit dan delete pengajuan tsb.\n2. setelah pengajuan diketahui / disetujui, pengajuan tidak dapat di edit / delete.\n3. diketahui dan disetujui bisa dilakukan terpisah. untuk bisa di proses harus sudah di setujui dan diketahui.\n4. status 'sampai' masih bisa kembali ke status 'dikirim'.\n5. jika status sudah 'selesai', tidak bisa mengganti status kembali.");
        jScrollPane2.setViewportView(jTextArea1);

        button_diketahui_kadep.setBackground(new java.awt.Color(255, 255, 255));
        button_diketahui_kadep.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_diketahui_kadep.setText("Diketahui Kadep");
        button_diketahui_kadep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_diketahui_kadepActionPerformed(evt);
            }
        });

        button_tv_pengajuan.setBackground(new java.awt.Color(255, 255, 255));
        button_tv_pengajuan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_tv_pengajuan.setText("TV Pengajuan");
        button_tv_pengajuan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_tv_pengajuanActionPerformed(evt);
            }
        });

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel11.setText("Departemen :");

        ComboBox_filter_departemen.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        ComboBox_filter_departemen.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", " " }));

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel12.setText("Tanggal Pengajuan :");

        Date_pengajuan1.setBackground(new java.awt.Color(255, 255, 255));
        Date_pengajuan1.setDateFormatString("dd MMM yyyy");

        Date_pengajuan2.setBackground(new java.awt.Color(255, 255, 255));
        Date_pengajuan2.setDateFormatString("dd MMM yyyy");

        button_realisasi.setBackground(new java.awt.Color(255, 255, 255));
        button_realisasi.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_realisasi.setText("Realisasi");
        button_realisasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_realisasiActionPerformed(evt);
            }
        });

        button_form_pengajuan.setBackground(new java.awt.Color(255, 255, 255));
        button_form_pengajuan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_form_pengajuan.setText("Form Pengajuan");
        button_form_pengajuan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_form_pengajuanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1346, Short.MAX_VALUE)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data_nota)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_diketahui_kadep)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_diketahui)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_disetujui)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_diproses)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_dikirim)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_sampai)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_selesai))
                            .addComponent(jLabel16)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_keyword, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_filter_status, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_filter_departemen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_pengajuan1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_pengajuan2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(button_new)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_edit)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_delete)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_realisasi)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_buka_link)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_tv_pengajuan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_form_pengajuan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export)))
                        .addGap(0, 314, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txt_search_keyword, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_filter_status, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_filter_departemen, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_pengajuan1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_pengajuan2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_new, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_edit, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_buka_link)
                    .addComponent(button_tv_pengajuan, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_realisasi, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_form_pengajuan, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_data_nota, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_diketahui, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_disetujui, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_diproses, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_selesai, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_dikirim, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_sampai, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_diketahui_kadep, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button_newActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_newActionPerformed
        // TODO add your handling code here:
        Boolean Check = true;
        try {
            if (!MainForm.Login_Posisi.contains("STAFF") && !MainForm.Login_Posisi.contains("MANAGER")) {
                JOptionPane.showMessageDialog(this, "hanya staff 5/6 dan manager yang dapat melakukan pengajuan");
                Check = false;
            }
            if (Check) {
                JDialog_Aset_PengajuanPembelian_NewEdit dialog = new JDialog_Aset_PengajuanPembelian_NewEdit(new javax.swing.JFrame(), true, null);
                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                dialog.setEnabled(true);
                refreshTable();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Aset_PengajuanPembelian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_newActionPerformed

    private void button_editActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_editActionPerformed
        // TODO add your handling code here:
        int j = table_pengajuan.getSelectedRow();
        try {
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan Pilih data yang mau di edit !");
            } else {
                Boolean Check = true;
                String diajukan_oleh = table_pengajuan.getValueAt(j, 9).toString();
                if (table_pengajuan.getValueAt(j, 10) != null) {
                    JOptionPane.showMessageDialog(this, "Pengajuan sudah diketahui kadep, tidak bisa edit / hapus");
                    Check = false;
                } else if (table_pengajuan.getValueAt(j, 11) != null) {
                    JOptionPane.showMessageDialog(this, "Pengajuan sudah diketahui, tidak bisa edit / hapus");
                    Check = false;
                } else if (table_pengajuan.getValueAt(j, 12) != null) {
                    JOptionPane.showMessageDialog(this, "Pengajuan sudah disetujui, tidak bisa edit / hapus");
                    Check = false;
                } else if (!MainForm.Login_NamaPegawai.equals(diajukan_oleh)) {
                    JOptionPane.showMessageDialog(this, "hanya staff yang melakukan pengajuan yang dapat mengubah pengajuan tsb.\nSilahkan login menggunakan user : " + diajukan_oleh);
                    Check = false;
                }
                if (Check) {
                    String no = table_pengajuan.getValueAt(j, 0).toString();
                    JDialog_Aset_PengajuanPembelian_NewEdit dialog = new JDialog_Aset_PengajuanPembelian_NewEdit(new javax.swing.JFrame(), true, no);
                    dialog.pack();
                    dialog.setLocationRelativeTo(this);
                    dialog.setVisible(true);
                    dialog.setEnabled(true);
                    refreshTable();
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Aset_PengajuanPembelian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_editActionPerformed

    private void button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_deleteActionPerformed
        // TODO add your handling code here:
        try {
            int j = table_pengajuan.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang mau di hapus !");
            } else {
                Boolean Check = true;
                String diajukan_oleh = table_pengajuan.getValueAt(j, 9).toString();
                if (table_pengajuan.getValueAt(j, 10) != null) {
                    JOptionPane.showMessageDialog(this, "Pengajuan sudah diketahui kadep, tidak bisa edit / hapus");
                    Check = false;
                } else if (table_pengajuan.getValueAt(j, 11) != null) {
                    JOptionPane.showMessageDialog(this, "Pengajuan sudah diketahui, tidak bisa edit / hapus");
                    Check = false;
                } else if (table_pengajuan.getValueAt(j, 12) != null) {
                    JOptionPane.showMessageDialog(this, "Pengajuan sudah disetujui, tidak bisa edit / hapus");
                    Check = false;
                } else if (!MainForm.Login_NamaPegawai.equals(diajukan_oleh)) {
                    JOptionPane.showMessageDialog(this, "hanya staff yang melakukan pengajuan yang dapat menghapus pengajuan tsb.\nSilahkan login menggunakan user : " + diajukan_oleh);
                    Check = false;
                }
                if (Check) {
                    int dialogResult = JOptionPane.showConfirmDialog(this, "Apakah yakin ingin menghapus data ini?", "Warning", 0);
                    if (dialogResult == JOptionPane.YES_OPTION) {
                        // delete code here
                        String Query = "DELETE FROM `tb_aset_pengajuan` WHERE `no` = '" + table_pengajuan.getValueAt(j, 0).toString() + "'";
                        Utility.db.getConnection().createStatement();
                        if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                            JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
                            refreshTable();
                        }
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Aset_PengajuanPembelian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_deleteActionPerformed

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_searchActionPerformed

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) table_pengajuan.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_exportActionPerformed

    private void txt_search_keywordKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_keywordKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable();
        }
    }//GEN-LAST:event_txt_search_keywordKeyPressed

    private void button_diketahuiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_diketahuiActionPerformed
        // TODO add your handling code here:
        int j = table_pengajuan.getSelectedRow();
        try {
            Utility.db.getConnection().setAutoCommit(false);
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan Pilih salah satu data !");
            } else {
                Boolean Check = true;
                if (table_pengajuan.getValueAt(j, 10) == null) {
                    JOptionPane.showMessageDialog(this, "Pengajuan belum diketahui kadep");
                    Check = false;
                } else if (table_pengajuan.getValueAt(j, 11) != null) {
                    JOptionPane.showMessageDialog(this, "Pengajuan sudah diketahui");
                    Check = false;
                } else if (MainForm.Login_kodeBagian != 244) {//OPERATIONAL MANAGER----OFFICE
//                } else if (!MainForm.Login_Posisi.equals("STAFF 5")) {
                    JOptionPane.showMessageDialog(this, "hanya OPERATIONAL MANAGER yang bisa mengetahui pengajuan pembelian alat");
                    Check = false;
                }
                if (Check) {
                    String no = table_pengajuan.getValueAt(j, 0).toString();
                    String keterangan_lama = table_pengajuan.getValueAt(j, 16).toString();
                    String keterangan = JOptionPane.showInputDialog(this, "Keterangan :", keterangan_lama);
                    if (keterangan != null) {
                        String Query = "UPDATE `tb_aset_pengajuan` SET "
                                + "`status`='Diketahui', \n"
                                + "`keterangan`='" + keterangan + "', \n"
                                + "`diketahui`='" + MainForm.Login_NamaPegawai + " " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "' "
                                + "WHERE `no` = '" + no + "'";
                        Utility.db.getConnection().createStatement();
                        Utility.db.getStatement().executeUpdate(Query);

                        Utility.db.getConnection().commit();
                        JOptionPane.showMessageDialog(this, "Status pengajuan : Diketahui");
                        refreshTable();
                    }
                }
            }
        } catch (Exception ex) {
            try {
                Utility.db.getConnection().rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(JPanel_Aset_PengajuanPembelian.class.getName()).log(Level.SEVERE, null, ex1);
            }
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Aset_PengajuanPembelian.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JPanel_Aset_PengajuanPembelian.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_diketahuiActionPerformed

    private void button_disetujuiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_disetujuiActionPerformed
        // TODO add your handling code here:        
        int j = table_pengajuan.getSelectedRow();
        try {
            Utility.db.getConnection().setAutoCommit(false);
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan Pilih salah satu data !");
            } else {
                Boolean Check = true;
                if (table_pengajuan.getValueAt(j, 11) == null) {
                    JOptionPane.showMessageDialog(this, "Pengajuan belum diketahui");
                    Check = false;
                } else if (table_pengajuan.getValueAt(j, 12) != null) {
                    JOptionPane.showMessageDialog(this, "Pengajuan sudah disetujui");
                    Check = false;
                } else if (!MainForm.Login_idPegawai.equals("1") && !MainForm.Login_idPegawai.equals("2")) {
                    JOptionPane.showMessageDialog(this, "hanya bu Fifi / pak Djoko yang bisa menyetujui pengajuan pembelian alat");
                    Check = false;
                }
                if (Check) {
                    String no = table_pengajuan.getValueAt(j, 0).toString();
                    String Query = "UPDATE `tb_aset_pengajuan` SET \n"
                            + "`status`='Disetujui', \n"
                            + "`disetujui`='" + MainForm.Login_NamaPegawai + " " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "' \n"
                            + "WHERE `no` = '" + no + "'";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(Query);

                    Utility.db.getConnection().commit();
                    JOptionPane.showMessageDialog(this, "Status pengajuan : Disetujui");
                    refreshTable();
                }
            }
        } catch (Exception ex) {
            try {
                Utility.db.getConnection().rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(JPanel_Aset_PengajuanPembelian.class.getName()).log(Level.SEVERE, null, ex1);
            }
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Aset_PengajuanPembelian.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JPanel_Aset_PengajuanPembelian.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_disetujuiActionPerformed

    private void button_diprosesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_diprosesActionPerformed
        // TODO add your handling code here:
        int j = table_pengajuan.getSelectedRow();
        try {
            Utility.db.getConnection().setAutoCommit(false);
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan Pilih salah satu data !");
            } else {
                Boolean Check = true;
                if (table_pengajuan.getValueAt(j, 12) == null) {
                    JOptionPane.showMessageDialog(this, "Pengajuan belum disetujui");
                    Check = false;
                } else if (table_pengajuan.getValueAt(j, 13) != null) {
                    JOptionPane.showMessageDialog(this, "Pengajuan sudah diproses");
                    Check = false;
                } else if (MainForm.Login_Departemen == null || !MainForm.Login_Departemen.equals("KEUANGAN")) {
//                } else if (!MainForm.Login_Posisi.equals("STAFF 5")) {
                    JOptionPane.showMessageDialog(this, "hanya departemen KEUANGAN yang bisa memproses pengajuan pembelian alat");
                    Check = false;
                }
                if (Check) {
                    String no = table_pengajuan.getValueAt(j, 0).toString();

                    String[] options = {"Online", "Offline", "PO"};
                    int selectedOption = JOptionPane.showOptionDialog(
                            null,
                            "Silahkan pilih jenis pembelian:",
                            "Jenis Pembelian",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            options,
                            options[0]
                    );

                    switch (selectedOption) {
                        case 0: {
                            String Query = "UPDATE `tb_aset_pengajuan` SET "
                                    + "`status`='Proses', \n"
                                    + "`diproses`='" + MainForm.Login_NamaPegawai + " " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "', "
                                    + "`jenis_pembelian`='Online'"
                                    + "WHERE `no` = '" + no + "'";
                            Utility.db.getConnection().createStatement();
                            Utility.db.getStatement().executeUpdate(Query);
                            Utility.db.getConnection().commit();
                            JOptionPane.showMessageDialog(this, "Status pengajuan : Proses");
                            refreshTable();
                            break;
                        }
                        case 1: {
                            String Query = "UPDATE `tb_aset_pengajuan` SET "
                                    + "`status`='Proses', \n"
                                    + "`diproses`='" + MainForm.Login_NamaPegawai + " " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "', "
                                    + "`jenis_pembelian`='Offline'"
                                    + "WHERE `no` = '" + no + "'";
                            Utility.db.getConnection().createStatement();
                            Utility.db.getStatement().executeUpdate(Query);
                            Utility.db.getConnection().commit();
                            JOptionPane.showMessageDialog(this, "Status pengajuan : Proses");
                            refreshTable();
                            break;
                        }
                        case 2: {
                            String Query = "UPDATE `tb_aset_pengajuan` SET "
                                    + "`status`='Proses', \n"
                                    + "`diproses`='" + MainForm.Login_NamaPegawai + " " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "', "
                                    + "`jenis_pembelian`='PO'"
                                    + "WHERE `no` = '" + no + "'";
                            Utility.db.getConnection().createStatement();
                            Utility.db.getStatement().executeUpdate(Query);
                            Utility.db.getConnection().commit();
                            JOptionPane.showMessageDialog(this, "Status pengajuan : Proses");
                            refreshTable();
                            break;
                        }
                        default:
                            System.out.println("Dialog closed");
                            break;
                    }
                }
            }
        } catch (Exception ex) {
            try {
                Utility.db.getConnection().rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(JPanel_Aset_PengajuanPembelian.class.getName()).log(Level.SEVERE, null, ex1);
            }
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Aset_PengajuanPembelian.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JPanel_Aset_PengajuanPembelian.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_diprosesActionPerformed

    private void button_buka_linkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_buka_linkActionPerformed
        // TODO add your handling code here:
        int j = table_pengajuan.getSelectedRow();
        try {
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan Pilih salah satu data !");
            } else {
                // Specify the URL you want to open
                String url = table_pengajuan.getValueAt(j, 17).toString();

                // Get the system clipboard
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

                // Create a StringSelection object with the text to copy
                StringSelection selection = new StringSelection(url);

                // Set the contents of the clipboard to the StringSelection
                clipboard.setContents(selection, null);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Aset_PengajuanPembelian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_buka_linkActionPerformed

    private void button_dikirimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_dikirimActionPerformed
        // TODO add your handling code here:
        int j = table_pengajuan.getSelectedRow();
        try {
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan Pilih salah satu data !");
            } else {
                Boolean Check = true;
                if (table_pengajuan.getValueAt(j, 15).toString().equals("Selesai")) {
                    JOptionPane.showMessageDialog(this, "Pengajuan sudah diselesaikan oleh user, status tidak bisa berubah");
                    Check = false;
                } else if (MainForm.Login_Departemen == null || !MainForm.Login_Departemen.equals("KEUANGAN")) {
//                } else if (!MainForm.Login_Posisi.equals("STAFF 5")) {
                    JOptionPane.showMessageDialog(this, "hanya departemen KEUANGAN yang bisa mengganti status 'Dikirim'");
                    Check = false;
                }
                if (Check) {
                    String no = table_pengajuan.getValueAt(j, 0).toString();
                    String Query = "UPDATE `tb_aset_pengajuan` SET `status`='Dikirim' WHERE `no` = '" + no + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                        JOptionPane.showMessageDialog(this, "Status pengajuan : Dikirim");
                        refreshTable();
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Aset_PengajuanPembelian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_dikirimActionPerformed

    private void button_sampaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_sampaiActionPerformed
        // TODO add your handling code here:
        int j = table_pengajuan.getSelectedRow();
        try {
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan Pilih salah satu data !");
            } else {
                Boolean Check = true;
                if (table_pengajuan.getValueAt(j, 15).toString().equals("Selesai")) {
                    JOptionPane.showMessageDialog(this, "Pengajuan sudah diselesaikan oleh user, status tidak bisa berubah");
                    Check = false;
                } else if (MainForm.Login_Departemen == null || !MainForm.Login_Departemen.equals("KEUANGAN")) {
//                } else if (!MainForm.Login_Posisi.equals("STAFF 5")) {
                    JOptionPane.showMessageDialog(this, "hanya departemen KEUANGAN yang bisa mengganti status 'Sampai'");
                    Check = false;
                }
                if (Check) {
                    String no = table_pengajuan.getValueAt(j, 0).toString();
                    String Query = "UPDATE `tb_aset_pengajuan` SET `status`='Sampai' WHERE `no` = '" + no + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                        JOptionPane.showMessageDialog(this, "Status pengajuan : Sampai");
                        refreshTable();
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Aset_PengajuanPembelian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_sampaiActionPerformed

    private void button_selesaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_selesaiActionPerformed
        // TODO add your handling code here:
        int j = table_pengajuan.getSelectedRow();
        try {
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan Pilih salah satu data !");
            } else {
                Boolean Check = true;
                String diajukan_oleh = table_pengajuan.getValueAt(j, 9).toString();
                if (table_pengajuan.getValueAt(j, 15).toString().equals("Selesai")) {
                    JOptionPane.showMessageDialog(this, "Pengajuan sudah diselesaikan oleh user, status tidak bisa berubah");
                    Check = false;
                } else if (!MainForm.Login_NamaPegawai.equals(diajukan_oleh)) {
                    JOptionPane.showMessageDialog(this, "hanya staff yang melakukan pengajuan yang dapat mengubah status menjadi 'Selesai'.\nSilahkan login menggunakan user : " + diajukan_oleh);
                    Check = false;
                }
                if (Check) {
                    String no = table_pengajuan.getValueAt(j, 0).toString();
                    String Query = "UPDATE `tb_aset_pengajuan` SET `status`='Selesai' WHERE `no` = '" + no + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                        JOptionPane.showMessageDialog(this, "Status pengajuan : Selesai");
                        refreshTable();
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Aset_PengajuanPembelian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_selesaiActionPerformed

    private void button_diketahui_kadepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_diketahui_kadepActionPerformed
        // TODO add your handling code here:
        int j = table_pengajuan.getSelectedRow();
        try {
            Utility.db.getConnection().setAutoCommit(false);
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan Pilih salah satu data !");
            } else {
                Boolean Check = true;
                String no = table_pengajuan.getValueAt(j, 0).toString();
                String kode_departemen = "";
                sql = "SELECT `tb_bagian`.`kode_departemen` \n"
                        + "FROM `tb_aset_pengajuan` \n"
                        + "LEFT JOIN `tb_karyawan` ON `tb_aset_pengajuan`.`diajukan` = `tb_karyawan`.`id_pegawai`\n"
                        + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian`\n"
                        + "WHERE `no` = '" + no + "'";
                rs = Utility.db.getStatement().executeQuery(sql);
                if (rs.next()) {
                    kode_departemen = rs.getString("kode_departemen");
                }

                if (table_pengajuan.getValueAt(j, 10) != null) {
                    JOptionPane.showMessageDialog(this, "Pengajuan sudah diketahui kadep");
                    Check = false;
                } else if (!MainForm.Login_namaBagian.toUpperCase().contains("KADEP-" + kode_departemen) && MainForm.Login_kodeBagian != 244) {
                    JOptionPane.showMessageDialog(this, "Hanya KADEP " + kode_departemen + " / OPERATIONAL MANAGER, yang bisa mengetahui pengajuan pembelian alat!");
                    Check = false;
                }
                if (Check) {
                    String Query = "UPDATE `tb_aset_pengajuan` SET "
                            + "`status`='Diketahui Kadep', \n"
                            + "`diketahui_kadep`='" + MainForm.Login_NamaPegawai + " " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "' "
                            + "WHERE `no` = '" + no + "'";
                    Utility.db.getConnection().createStatement();
                    Utility.db.getStatement().executeUpdate(Query);

                    Utility.db.getConnection().commit();
                    JOptionPane.showMessageDialog(this, "Status pengajuan : Diketahui Kadep");
                    refreshTable();
                }
            }
        } catch (Exception ex) {
            try {
                Utility.db.getConnection().rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(JPanel_Aset_PengajuanPembelian.class.getName()).log(Level.SEVERE, null, ex1);
            }
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Aset_PengajuanPembelian.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JPanel_Aset_PengajuanPembelian.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_diketahui_kadepActionPerformed

    private void button_tv_pengajuanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_tv_pengajuanActionPerformed
        // TODO add your handling code here:
        JFrame_TV_PengajuanPembelian frame = new JFrame_TV_PengajuanPembelian();
        frame.pack();
        frame.setResizable(true);
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
        frame.setEnabled(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }//GEN-LAST:event_button_tv_pengajuanActionPerformed

    private void button_realisasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_realisasiActionPerformed
        // TODO add your handling code here:
        int j = table_pengajuan.getSelectedRow();
        try {
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan Pilih data yang mau di edit !");
            } else {
                Boolean Check = true;
                if (Check) {
                    String no = table_pengajuan.getValueAt(j, 0).toString();
                    JDialog_Aset_PengajuanPembelian_Realisasi dialog = new JDialog_Aset_PengajuanPembelian_Realisasi(new javax.swing.JFrame(), true, no);
                    dialog.pack();
                    dialog.setLocationRelativeTo(this);
                    dialog.setVisible(true);
                    dialog.setEnabled(true);
                    refreshTable();
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Aset_PengajuanPembelian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_realisasiActionPerformed

    private void button_form_pengajuanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_form_pengajuanActionPerformed
        // TODO add your handling code here:
        int j = table_pengajuan.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan Pilih salah satu data !");
        } else {
            try {
                // Create a DecimalFormatSymbols object and set the grouping separator
                DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                symbols.setGroupingSeparator('.');

                // Create a DecimalFormat object with custom symbols
                DecimalFormat FormatHarga = new DecimalFormat("###,###", symbols);

                String harga_satuan = FormatHarga.format(table_pengajuan.getValueAt(j, 18));
                String subtotal = FormatHarga.format((int) table_pengajuan.getValueAt(j, 5) * (int) table_pengajuan.getValueAt(j, 18));
                String ppn = FormatHarga.format(table_pengajuan.getValueAt(j, 20));
                String biaya_lain = FormatHarga.format(table_pengajuan.getValueAt(j, 21));
                String total = FormatHarga.format(table_pengajuan.getValueAt(j, 23));

                JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Form_Pengajuan_Pembelian.jrxml");
                Map<String, Object> params = new HashMap<>();
                params.put("NO_PENGAJUAN", table_pengajuan.getValueAt(j, 0).toString());
                params.put("harga_satuan", harga_satuan);
                params.put("subtotal", subtotal);
                params.put("ppn", ppn);
                params.put("biaya_lain", biaya_lain);
                params.put("total", total);
                JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
                JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, params, Utility.db.getConnection());
                JasperViewer.viewReport(JASP_PRINT, false);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex);
                Logger.getLogger(JPanel_payrol_harian.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_form_pengajuanActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_filter_departemen;
    private javax.swing.JComboBox<String> ComboBox_filter_status;
    private com.toedter.calendar.JDateChooser Date_pengajuan1;
    private com.toedter.calendar.JDateChooser Date_pengajuan2;
    private javax.swing.JButton button_buka_link;
    private javax.swing.JButton button_delete;
    private javax.swing.JButton button_diketahui;
    private javax.swing.JButton button_diketahui_kadep;
    private javax.swing.JButton button_dikirim;
    private javax.swing.JButton button_diproses;
    private javax.swing.JButton button_disetujui;
    private javax.swing.JButton button_edit;
    private javax.swing.JButton button_export;
    private javax.swing.JButton button_form_pengajuan;
    private javax.swing.JButton button_new;
    private javax.swing.JButton button_realisasi;
    private javax.swing.JButton button_sampai;
    private javax.swing.JButton button_search;
    private javax.swing.JButton button_selesai;
    private javax.swing.JButton button_tv_pengajuan;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel label_total_data_nota;
    public static javax.swing.JTable table_pengajuan;
    private javax.swing.JTextField txt_search_keyword;
    // End of variables declaration//GEN-END:variables
}
