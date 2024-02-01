package waleta_system.Packing;

import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;

public class JPanel_Pengiriman_PickUp extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    DecimalFormat decimalFormat = new DecimalFormat();

    public JPanel_Pengiriman_PickUp() {
        initComponents();
    }

    public void init() {
        try {
            refreshTable_PickUp_Request();
            refreshTable_Data_Sopir();
            refreshTable_Data_Armada();
            Table_pickup_request.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && Table_pickup_request.getSelectedRow() != -1) {
                        int i = Table_pickup_request.getSelectedRow();
                        if (i >= 0) {

                        }
                    }
                }
            });
            Table_data_sopir.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && Table_data_sopir.getSelectedRow() != -1) {
                        int i = Table_data_sopir.getSelectedRow();
                        if (i >= 0) {
                            try {
                                String id_sopir = Table_data_sopir.getValueAt(i, 0).toString();
                                String img_path = "http://192.168.10.2:5050/waleta/images/tb_ekspedisi_sopir_foto/" + id_sopir + ".jpg";
                                ImageIcon image = Utility.getAndResizeImageFromURL(img_path, Label_Foto_Sopir.getWidth(), Label_Foto_Sopir.getHeight());
                                Label_Foto_Sopir.setIcon(image);
                            } catch (Exception ex) {
                                Logger.getLogger(JPanel_Pengiriman_PickUp.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
            });
            Table_data_armada.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if (!event.getValueIsAdjusting() && Table_data_armada.getSelectedRow() != -1) {
                        int i = Table_data_armada.getSelectedRow();
                        if (i >= 0) {
                            try {
                                String nopol = Table_data_armada.getValueAt(i, 0).toString();
                                String img_path = "http://192.168.10.2:5050/waleta/images/tb_ekspedisi_armada_foto/" + nopol + ".jpg";
                                ImageIcon image = Utility.getAndResizeImageFromURL(img_path, Label_Foto_Armada.getWidth(), Label_Foto_Armada.getHeight());
                                Label_Foto_Armada.setIcon(image);
                            } catch (Exception ex) {
                                Logger.getLogger(JPanel_Pengiriman_PickUp.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(JPanel_Pengiriman_PickUp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_PickUp_Request() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_pickup_request.getModel();
            model.setRowCount(0);
            String filter_tanggal = "";
            if (Date_Filter1.getDate() != null && Date_Filter2.getDate() != null) {
                filter_tanggal = " AND (DATE(`created_at`) BETWEEN '" + dateFormat.format(Date_Filter1.getDate()) + "' AND '" + dateFormat.format(Date_Filter2.getDate()) + "')";
            }
            sql = "SELECT `id_request_pick_up`, `admin`, `tb_pengiriman_request_pick_up`.`id_ekspedisi`, `tb_pengiriman_request_pick_up`.`nopol`, `tb_ekspedisi_armada`.`jenis_armada`, `tb_ekspedisi_sopir`.`id_sopir`, `tb_ekspedisi_sopir`.`nama` AS 'nama_sopir', `nomor_tiket`, "
                    + "`waktu_kedatangan`, `waktu_keberangkatan`, `foto_keberangkatan`, `kode_spk`, "
                    + "`kondisi_bau`, `kondisi_alas_basah`, `kondisi_serpihan_kayu`, `kondisi_kotoran`, `kondisi_ceceran_oli`, `kondisi_terpal`, `semprot_box`, `ozon_kendaraan`, `diperiksa_oleh`, "
                    + "`tb_pengiriman_request_pick_up`.`created_at`, `tb_pengiriman_request_pick_up`.`updated_at` "
                    + "FROM `tb_pengiriman_request_pick_up` "
                    + "LEFT JOIN `tb_ekspedisi_armada` ON `tb_pengiriman_request_pick_up`.`nopol` = `tb_ekspedisi_armada`.`nopol` "
                    + "LEFT JOIN `tb_ekspedisi_sopir` ON `tb_pengiriman_request_pick_up`.`id_sopir` = `tb_ekspedisi_sopir`.`id_sopir` "
                    + "WHERE "
                    + "`kode_spk` LIKE '%" + txt_search_kode_spk.getText() + "%'"
                    + filter_tanggal;
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[35];
            while (rs.next()) {
                row[0] = rs.getString("id_request_pick_up");
                row[1] = rs.getTimestamp("created_at");
                row[2] = rs.getString("admin");
                row[3] = rs.getString("id_ekspedisi");
                row[4] = rs.getString("nopol");
                row[5] = rs.getString("jenis_armada");
                row[6] = rs.getString("id_sopir");
                row[7] = rs.getString("nama_sopir");
                row[8] = rs.getString("nomor_tiket");
                row[9] = rs.getTimestamp("waktu_kedatangan");
                row[10] = rs.getTimestamp("waktu_keberangkatan");
                row[11] = rs.getString("kode_spk");
                row[12] = rs.getBoolean("kondisi_bau");
                row[13] = rs.getBoolean("kondisi_alas_basah");
                row[14] = rs.getBoolean("kondisi_serpihan_kayu");
                row[15] = rs.getBoolean("kondisi_kotoran");
                row[16] = rs.getBoolean("kondisi_ceceran_oli");
                row[17] = rs.getBoolean("kondisi_terpal");
                row[18] = rs.getBoolean("semprot_box");
                row[19] = rs.getBoolean("ozon_kendaraan");
                row[20] = rs.getString("diperiksa_oleh");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_pickup_request);
            label_total_data_pickup_request.setText(decimalFormat.format(Table_pickup_request.getRowCount()));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Pengiriman_PickUp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_Data_Sopir() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_data_sopir.getModel();
            model.setRowCount(0);
            sql = "SELECT `id_sopir`, `nama`, `no_hp`, `foto`, `id_ekspedisi`, `created_at`, `updated_at` "
                    + "FROM `tb_ekspedisi_sopir` "
                    + "WHERE "
                    + "`nama` LIKE '%" + txt_search_nama_sopir.getText() + "%'";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getString("id_sopir");
                row[1] = rs.getString("nama");
                row[2] = rs.getString("no_hp");
                row[3] = rs.getString("id_ekspedisi");
                row[4] = rs.getBoolean("foto");
                row[5] = rs.getTimestamp("created_at");
                row[6] = rs.getTimestamp("updated_at");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_data_sopir);
            label_total_data_sopir.setText(decimalFormat.format(Table_data_sopir.getRowCount()));
            Label_Foto_Sopir.setText(null);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Pengiriman_PickUp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_Data_Armada() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_data_armada.getModel();
            model.setRowCount(0);
            sql = "SELECT `nopol`, `jenis_armada`, `foto_armada`, `id_ekspedisi`, `created_at`, `updated_at` "
                    + "FROM `tb_ekspedisi_armada` "
                    + "WHERE "
                    + "`nopol` LIKE '%" + txt_search_jenis_kendaraan_armada.getText() + "%' "
                    + "OR `jenis_armada` LIKE '%" + txt_search_jenis_kendaraan_armada.getText() + "%'";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getString("nopol");
                row[1] = rs.getString("jenis_armada");
                row[2] = rs.getString("id_ekspedisi");
                row[3] = rs.getBoolean("foto_armada");
                row[4] = rs.getTimestamp("created_at");
                row[5] = rs.getTimestamp("updated_at");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_data_armada);
            label_total_data_armada.setText(decimalFormat.format(Table_data_armada.getRowCount()));
            Label_Foto_Armada.setText(null);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_Pengiriman_PickUp.class.getName()).log(Level.SEVERE, null, ex);
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

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txt_search_kode_spk = new javax.swing.JTextField();
        button_search = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_pickup_request = new javax.swing.JTable();
        button_export = new javax.swing.JButton();
        label_total_data_pickup_request = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        Date_Filter1 = new com.toedter.calendar.JDateChooser();
        Date_Filter2 = new com.toedter.calendar.JDateChooser();
        button_insert = new javax.swing.JButton();
        button_delete = new javax.swing.JButton();
        button_update = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        button_insert_sopir = new javax.swing.JButton();
        txt_search_nama_sopir = new javax.swing.JTextField();
        button_update_sopir = new javax.swing.JButton();
        button_search_sopir = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        Table_data_sopir = new javax.swing.JTable();
        button_export_data_sopir = new javax.swing.JButton();
        label_total_data_sopir = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        Label_Foto_Sopir = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        button_insert_armada = new javax.swing.JButton();
        txt_search_jenis_kendaraan_armada = new javax.swing.JTextField();
        button_update_armada = new javax.swing.JButton();
        button_search_armada = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        Table_data_armada = new javax.swing.JTable();
        button_export_data_armada = new javax.swing.JButton();
        label_total_data_armada = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        Label_Foto_Armada = new javax.swing.JLabel();

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("Kode SPK :");

        txt_search_kode_spk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_kode_spk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_kode_spkKeyPressed(evt);
            }
        });

        button_search.setBackground(new java.awt.Color(255, 255, 255));
        button_search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search.setText("Search");
        button_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_searchActionPerformed(evt);
            }
        });

        Table_pickup_request.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_pickup_request.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Request", "Waktu Input", "Admin", "Ekspedisi", "NoPol Armada", "Jenis Kendaraan", "ID Sopir", "Nama Sopir", "No Tiket", "Waktu Kedatangan", "Waktu Keberangkatan", "Kode SPK", "Bau", "Alas Basah", "Serpihan Kayu", "Kotoran / Debu", "Ceceran Oli", "Terpal", "Semprot Box", "Ozon Kendaraan", "Diperiksa Oleh"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(Table_pickup_request);
        if (Table_pickup_request.getColumnModel().getColumnCount() > 0) {
            Table_pickup_request.getColumnModel().getColumn(3).setHeaderValue("Foto");
            Table_pickup_request.getColumnModel().getColumn(7).setHeaderValue("Nama Sopir");
            Table_pickup_request.getColumnModel().getColumn(8).setHeaderValue("No Tiket");
            Table_pickup_request.getColumnModel().getColumn(9).setHeaderValue("Waktu Kedatangan");
            Table_pickup_request.getColumnModel().getColumn(10).setHeaderValue("Waktu Keberangkatan");
            Table_pickup_request.getColumnModel().getColumn(11).setHeaderValue("Kode SPK");
            Table_pickup_request.getColumnModel().getColumn(12).setHeaderValue("Bau");
            Table_pickup_request.getColumnModel().getColumn(13).setHeaderValue("Alas Basah");
            Table_pickup_request.getColumnModel().getColumn(14).setHeaderValue("Serpihan Kayu");
            Table_pickup_request.getColumnModel().getColumn(15).setHeaderValue("Kotoran / Debu");
            Table_pickup_request.getColumnModel().getColumn(16).setHeaderValue("Ceceran Oli");
            Table_pickup_request.getColumnModel().getColumn(17).setHeaderValue("Terpal");
            Table_pickup_request.getColumnModel().getColumn(18).setHeaderValue("Semprot Box");
            Table_pickup_request.getColumnModel().getColumn(19).setHeaderValue("Ozon Kendaraan");
            Table_pickup_request.getColumnModel().getColumn(20).setHeaderValue("Diperiksa Oleh");
        }

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export.setText("Export To Excel");

        label_total_data_pickup_request.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_pickup_request.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_data_pickup_request.setText("0");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Total Data :");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("Tanggal Input :");

        Date_Filter1.setBackground(new java.awt.Color(255, 255, 255));
        Date_Filter1.setDateFormatString("dd MMM yyyy");
        Date_Filter1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        Date_Filter2.setBackground(new java.awt.Color(255, 255, 255));
        Date_Filter2.setDateFormatString("dd MMM yyyy");
        Date_Filter2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        button_insert.setBackground(new java.awt.Color(255, 255, 255));
        button_insert.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_insert.setText("Buat Permintaan");
        button_insert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_insertActionPerformed(evt);
            }
        });

        button_delete.setBackground(new java.awt.Color(255, 255, 255));
        button_delete.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete.setText("Hapus");
        button_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_deleteActionPerformed(evt);
            }
        });

        button_update.setBackground(new java.awt.Color(255, 255, 255));
        button_update.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_update.setText("Edit");
        button_update.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_updateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1341, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_kode_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_Filter1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_Filter2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data_pickup_request))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(button_insert)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_update)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_delete)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_export)
                    .addComponent(Date_Filter2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date_Filter1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_kode_spk, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_data_pickup_request, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_insert, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_update, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 592, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Shipment Pickup Request", jPanel1);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Nama :");

        button_insert_sopir.setBackground(new java.awt.Color(255, 255, 255));
        button_insert_sopir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_insert_sopir.setText("Baru");
        button_insert_sopir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_insert_sopirActionPerformed(evt);
            }
        });

        txt_search_nama_sopir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_nama_sopir.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_nama_sopirKeyPressed(evt);
            }
        });

        button_update_sopir.setBackground(new java.awt.Color(255, 255, 255));
        button_update_sopir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_update_sopir.setText("Edit");
        button_update_sopir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_update_sopirActionPerformed(evt);
            }
        });

        button_search_sopir.setBackground(new java.awt.Color(255, 255, 255));
        button_search_sopir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_sopir.setText("Search");
        button_search_sopir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_sopirActionPerformed(evt);
            }
        });

        Table_data_sopir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_data_sopir.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Sopir", "Nama Sopir", "No HP", "Ekspedisi", "Foto", "Created at", "Last Updated"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(Table_data_sopir);

        button_export_data_sopir.setBackground(new java.awt.Color(255, 255, 255));
        button_export_data_sopir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_data_sopir.setText("Export To Excel");

        label_total_data_sopir.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_sopir.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_data_sopir.setText("0");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Total Data :");

        Label_Foto_Sopir.setBackground(new java.awt.Color(255, 255, 255));
        Label_Foto_Sopir.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Label_Foto_Sopir.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(button_insert_sopir)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_update_sopir))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_nama_sopir, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search_sopir)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_data_sopir)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data_sopir)))
                        .addGap(0, 429, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Label_Foto_Sopir, javax.swing.GroupLayout.PREFERRED_SIZE, 449, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_export_data_sopir)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_nama_sopir, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search_sopir, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_data_sopir, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_insert_sopir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_update_sopir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 592, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(Label_Foto_Sopir, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data Sopir", jPanel2);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Jenis Kendaraan / Nomor Polisi :");

        button_insert_armada.setBackground(new java.awt.Color(255, 255, 255));
        button_insert_armada.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_insert_armada.setText("Baru");
        button_insert_armada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_insert_armadaActionPerformed(evt);
            }
        });

        txt_search_jenis_kendaraan_armada.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_jenis_kendaraan_armada.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_jenis_kendaraan_armadaKeyPressed(evt);
            }
        });

        button_update_armada.setBackground(new java.awt.Color(255, 255, 255));
        button_update_armada.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_update_armada.setText("Edit");
        button_update_armada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_update_armadaActionPerformed(evt);
            }
        });

        button_search_armada.setBackground(new java.awt.Color(255, 255, 255));
        button_search_armada.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_armada.setText("Search");
        button_search_armada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_armadaActionPerformed(evt);
            }
        });

        Table_data_armada.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_data_armada.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Polisi", "Jenis Armada", "Ekspedisi", "Foto", "Created at", "Last Updated"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class
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
        jScrollPane3.setViewportView(Table_data_armada);

        button_export_data_armada.setBackground(new java.awt.Color(255, 255, 255));
        button_export_data_armada.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_data_armada.setText("Export To Excel");

        label_total_data_armada.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_armada.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_total_data_armada.setText("0");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Total Data :");

        Label_Foto_Armada.setBackground(new java.awt.Color(255, 255, 255));
        Label_Foto_Armada.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Label_Foto_Armada.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 888, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Label_Foto_Armada, javax.swing.GroupLayout.PREFERRED_SIZE, 449, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(button_insert_armada)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_update_armada))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_jenis_kendaraan_armada, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search_armada)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_export_data_armada)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data_armada)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_export_data_armada)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_jenis_kendaraan_armada, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search_armada, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_data_armada, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_insert_armada, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_update_armada, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 592, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(Label_Foto_Armada, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data Armada", jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_search_kode_spkKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_kode_spkKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_PickUp_Request();
        }
    }//GEN-LAST:event_txt_search_kode_spkKeyPressed

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        // TODO add your handling code here:
        refreshTable_PickUp_Request();
    }//GEN-LAST:event_button_searchActionPerformed

    private void button_insertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_insertActionPerformed
        // TODO add your handling code here:
        JDialog_pengiriman_PickUp_insert_edit dialog = new JDialog_pengiriman_PickUp_insert_edit(new javax.swing.JFrame(), true, null);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        refreshTable_PickUp_Request();
    }//GEN-LAST:event_button_insertActionPerformed

    private void button_updateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_updateActionPerformed
        // TODO add your handling code here:
        int j = Table_pickup_request.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih salah satu data pada tabel !");
        } else {
            Boolean Check = true;
            if (Table_pickup_request.getValueAt(j, 9) != null) { // Waktu kedatangan tidak kosong
                JOptionPane.showMessageDialog(this, "Tiket sudah discan, tidak bisa diedit !");
                Check = false;
            }
            if (Check) {
                String id_req = Table_pickup_request.getValueAt(j, 0).toString();
                JDialog_pengiriman_PickUp_insert_edit dialog = new JDialog_pengiriman_PickUp_insert_edit(new javax.swing.JFrame(), true, id_req);
                dialog.pack();
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
                dialog.setEnabled(true);
                refreshTable_PickUp_Request();
            }
        }
    }//GEN-LAST:event_button_updateActionPerformed

    private void button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_deleteActionPerformed
        // TODO add your handling code here:
        int j = Table_pickup_request.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih data yang ingin di hapus !");
        } else {
            if (Table_pickup_request.getValueAt(j, 9) != null) { // Waktu kedatangan tidak kosong
                JOptionPane.showMessageDialog(this, "Tiket sudah discan, tidak bisa dihapus !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    try {
                        String id_req = Table_pickup_request.getValueAt(j, 0).toString();
                        String Query = "DELETE FROM `tb_pengiriman_request_pick_up` WHERE `id_request_pick_up` = ?";
                        Connection connection = Utility.db.getConnection();
                        PreparedStatement preparedStatement = connection.prepareStatement(Query);
                        preparedStatement.setString(1, id_req);
                        int rowsAffected = preparedStatement.executeUpdate();
                        if (rowsAffected == 1) {
                            JOptionPane.showMessageDialog(this, "Hapus data berhasil !");
                            refreshTable_PickUp_Request();
                        } else {
                            JOptionPane.showMessageDialog(this, "Delete failed!");
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, ex);
                        Logger.getLogger(JPanel_Pengiriman_PickUp.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }//GEN-LAST:event_button_deleteActionPerformed

    private void button_insert_sopirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_insert_sopirActionPerformed
        // TODO add your handling code here:
        JDialog_Insert_Edit_Ekspedisi_Sopir dialog = new JDialog_Insert_Edit_Ekspedisi_Sopir(new javax.swing.JFrame(), true, null);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        refreshTable_Data_Sopir();
    }//GEN-LAST:event_button_insert_sopirActionPerformed

    private void txt_search_nama_sopirKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_nama_sopirKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_Data_Sopir();
        }
    }//GEN-LAST:event_txt_search_nama_sopirKeyPressed

    private void button_update_sopirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_update_sopirActionPerformed
        // TODO add your handling code here:
        int j = Table_data_sopir.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih salah satu data pada tabel !");
        } else {
            String id_sopir = Table_data_sopir.getValueAt(j, 0).toString();
            JDialog_Insert_Edit_Ekspedisi_Sopir dialog = new JDialog_Insert_Edit_Ekspedisi_Sopir(new javax.swing.JFrame(), true, id_sopir);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
            refreshTable_Data_Sopir();
        }
    }//GEN-LAST:event_button_update_sopirActionPerformed

    private void button_search_sopirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_sopirActionPerformed
        // TODO add your handling code here:
        refreshTable_Data_Sopir();
    }//GEN-LAST:event_button_search_sopirActionPerformed

    private void button_insert_armadaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_insert_armadaActionPerformed
        // TODO add your handling code here:
        JDialog_Insert_Edit_Ekspedisi_Armada dialog = new JDialog_Insert_Edit_Ekspedisi_Armada(new javax.swing.JFrame(), true, null);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        refreshTable_Data_Armada();
    }//GEN-LAST:event_button_insert_armadaActionPerformed

    private void txt_search_jenis_kendaraan_armadaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_jenis_kendaraan_armadaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_Data_Armada();
        }
    }//GEN-LAST:event_txt_search_jenis_kendaraan_armadaKeyPressed

    private void button_update_armadaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_update_armadaActionPerformed
        // TODO add your handling code here:
        int j = Table_data_armada.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih salah satu data pada tabel !");
        } else {
            String nopol = Table_data_armada.getValueAt(j, 0).toString();
            JDialog_Insert_Edit_Ekspedisi_Armada dialog = new JDialog_Insert_Edit_Ekspedisi_Armada(new javax.swing.JFrame(), true, nopol);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
            refreshTable_Data_Armada();
        }
    }//GEN-LAST:event_button_update_armadaActionPerformed

    private void button_search_armadaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_armadaActionPerformed
        // TODO add your handling code here:
        refreshTable_Data_Armada();
    }//GEN-LAST:event_button_search_armadaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Date_Filter1;
    private com.toedter.calendar.JDateChooser Date_Filter2;
    private javax.swing.JLabel Label_Foto_Armada;
    private javax.swing.JLabel Label_Foto_Sopir;
    private javax.swing.JTable Table_data_armada;
    private javax.swing.JTable Table_data_sopir;
    private javax.swing.JTable Table_pickup_request;
    public javax.swing.JButton button_delete;
    private javax.swing.JButton button_export;
    private javax.swing.JButton button_export_data_armada;
    private javax.swing.JButton button_export_data_sopir;
    public javax.swing.JButton button_insert;
    public javax.swing.JButton button_insert_armada;
    public javax.swing.JButton button_insert_sopir;
    private javax.swing.JButton button_search;
    private javax.swing.JButton button_search_armada;
    private javax.swing.JButton button_search_sopir;
    public javax.swing.JButton button_update;
    public javax.swing.JButton button_update_armada;
    public javax.swing.JButton button_update_sopir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel label_total_data_armada;
    private javax.swing.JLabel label_total_data_pickup_request;
    private javax.swing.JLabel label_total_data_sopir;
    private javax.swing.JTextField txt_search_jenis_kendaraan_armada;
    private javax.swing.JTextField txt_search_kode_spk;
    private javax.swing.JTextField txt_search_nama_sopir;
    // End of variables declaration//GEN-END:variables
}
