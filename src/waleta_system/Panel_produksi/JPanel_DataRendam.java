package waleta_system.Panel_produksi;

import waleta_system.Class.Utility;

import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.ExportToExcel;

public class JPanel_DataRendam extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_DataRendam() {
        initComponents();
        Table_Data_Rendam.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_Data_Rendam.getSelectedRow() != -1) {
                    try {
                        int i = Table_Data_Rendam.getSelectedRow();
                        String date_rendam = Table_Data_Rendam.getValueAt(i, 0).toString();
                        Date_tgl_rendam.setDate(dateFormat.parse(date_rendam));
                        txt_no_lp_rendam.setText(Table_Data_Rendam.getValueAt(i, 1).toString());
                        txt_waktu_rendam.setText(Table_Data_Rendam.getValueAt(i, 5).toString());
                    } catch (ParseException ex) {
                        Logger.getLogger(JPanel_DataRendam.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
    }

    public void init() {
        refreshTable_Rendam();
    }

    public void refreshTable_Rendam() {
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z");
        try {
            DefaultTableModel model = (DefaultTableModel) Table_Data_Rendam.getModel();
            model.setRowCount(0);
            sql = "SELECT `tanggal_rendam`, `tb_rendam`.`no_laporan_produksi`, `tb_laporan_produksi`.`no_kartu_waleta`, `tb_laporan_produksi`.`kode_grade`, `tb_laporan_produksi`.`jumlah_keping`, `tb_laporan_produksi`.`berat_basah`, `lama_waktu_rendam`, `tb_laporan_produksi`.`memo_lp`, `waktu_steam`, `suhu_awal_steam`, `suhu_akhir_steam`, `tb_karyawan`.`nama_pegawai` AS 'pekerja_steam', `jenis_treatment`, \n"
                    + "`waktu_mulai_rendam`, `waktu_selesai_rendam` "
                    + "FROM `tb_rendam` "
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_rendam`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_karyawan` ON `tb_rendam`.`pekerja_steam` = `tb_karyawan`.`id_pegawai`\n"
                    + "WHERE `tb_rendam`.`no_laporan_produksi` LIKE '%" + txt_search_no_lp.getText() + "%' AND `tb_laporan_produksi`.`memo_lp` LIKE '%" + txt_search_memo.getText() + "%'\n"
                    + "ORDER BY `tb_rendam`.`tanggal_rendam` DESC";
            if (Date1_rendam.getDate() != null && Date2_rendam.getDate() != null) {
                sql = "SELECT `tanggal_rendam`, `tb_rendam`.`no_laporan_produksi`, `tb_laporan_produksi`.`no_kartu_waleta`, `tb_laporan_produksi`.`kode_grade`, `tb_laporan_produksi`.`jumlah_keping`, `tb_laporan_produksi`.`berat_basah`, `lama_waktu_rendam`, `tb_laporan_produksi`.`memo_lp`, `waktu_steam`, `suhu_awal_steam`, `suhu_akhir_steam`, `tb_karyawan`.`nama_pegawai` AS 'pekerja_steam', `jenis_treatment`, \n"
                        + "`waktu_mulai_rendam`, `waktu_selesai_rendam` "
                        + "FROM `tb_rendam` "
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_rendam`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "LEFT JOIN `tb_karyawan` ON `tb_rendam`.`pekerja_steam` = `tb_karyawan`.`id_pegawai`\n"
                        + "WHERE `tb_rendam`.`no_laporan_produksi` LIKE '%" + txt_search_no_lp.getText() + "%' AND `tb_laporan_produksi`.`memo_lp` LIKE '%" + txt_search_memo.getText() + "%' AND `tb_rendam`.`tanggal_rendam` BETWEEN '" + dateFormat.format(Date1_rendam.getDate()) + "' and '" + dateFormat.format(Date2_rendam.getDate()) + "'\n"
                        + "ORDER BY `tb_rendam`.`tanggal_rendam` DESC";
            }
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[15];
            while (rs.next()) {
                row[0] = rs.getDate("tanggal_rendam");
                row[1] = rs.getString("no_laporan_produksi");
                row[2] = rs.getString("kode_grade");
                row[3] = rs.getInt("jumlah_keping");
                row[4] = rs.getInt("berat_basah");
                row[5] = rs.getInt("lama_waktu_rendam");
                row[6] = rs.getString("memo_lp");
                row[7] = rs.getTime("waktu_steam");
                row[8] = rs.getFloat("suhu_awal_steam");
                row[9] = rs.getFloat("suhu_akhir_steam");
                row[10] = rs.getString("pekerja_steam");
                row[11] = rs.getString("jenis_treatment");
                row[12] = rs.getTime("waktu_mulai_rendam");
                row[13] = rs.getTime("waktu_selesai_rendam");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_Data_Rendam);
            int rowData = Table_Data_Rendam.getRowCount();
            label_total_data_rendam.setText(Integer.toString(rowData));
            TableAlignment.setHorizontalAlignment(JLabel.CENTER);
            //tabel Data Bahan Baku
            for (int i = 0; i < Table_Data_Rendam.getColumnCount(); i++) {
                Table_Data_Rendam.getColumnModel().getColumn(i).setCellRenderer(TableAlignment);
            }
        } catch (Exception ex) {
            Logger.getLogger(JPanel_DataRendam.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void executeSQLQuery(String query, String message) {
        Utility.db.getConnection();
        try {
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(query)) == 1) {
                refreshTable_Rendam();
                JOptionPane.showMessageDialog(this, "data " + message + " Successfully");
            } else {
                JOptionPane.showMessageDialog(this, "data not " + message);
            }
        } catch (SQLException | HeadlessException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JPanel_DataRendam.class.getName()).log(Level.SEVERE, null, e);
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

        jPanel_DataRendam = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txt_search_no_lp = new javax.swing.JTextField();
        Date1_rendam = new com.toedter.calendar.JDateChooser();
        Date2_rendam = new com.toedter.calendar.JDateChooser();
        button_search_rendam = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        Table_Data_Rendam = new javax.swing.JTable();
        button_export_data_rendam = new javax.swing.JButton();
        jPanel_operation_data_cuci1 = new javax.swing.JPanel();
        button_update_rendam = new javax.swing.JButton();
        button_insert_rendam = new javax.swing.JButton();
        button_delete_rendam = new javax.swing.JButton();
        button_clear_rendam = new javax.swing.JButton();
        txt_waktu_rendam = new javax.swing.JTextField();
        txt_no_lp_rendam = new javax.swing.JTextField();
        label_tgl_rendam = new javax.swing.JLabel();
        label_waktu_rendam = new javax.swing.JLabel();
        label_no_lp_rendam = new javax.swing.JLabel();
        Date_tgl_rendam = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        label_total_data_rendam = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        button_Catatan_Rendaman_Bahan_Mentah = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txt_search_memo = new javax.swing.JTextField();
        button_input_waktu_rendam = new javax.swing.JButton();
        button_set_data_rendam_otomatis = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Rendam", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        setPreferredSize(new java.awt.Dimension(1366, 700));

        jPanel_DataRendam.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_DataRendam.setPreferredSize(new java.awt.Dimension(1366, 688));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Laporan Produksi :");

        txt_search_no_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_no_lp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_no_lpKeyPressed(evt);
            }
        });

        Date1_rendam.setBackground(new java.awt.Color(255, 255, 255));
        Date1_rendam.setDate(new Date(new Date().getTime()- (7 * 24 * 60 * 60 * 1000)));
        Date1_rendam.setDateFormatString("dd MMMM yyyy");
        Date1_rendam.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date2_rendam.setBackground(new java.awt.Color(255, 255, 255));
        Date2_rendam.setDate(new Date());
        Date2_rendam.setDateFormatString("dd MMMM yyyy");
        Date2_rendam.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_search_rendam.setBackground(new java.awt.Color(255, 255, 255));
        button_search_rendam.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_rendam.setText("Search");
        button_search_rendam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_rendamActionPerformed(evt);
            }
        });

        jScrollPane4.setBackground(new java.awt.Color(255, 255, 255));

        Table_Data_Rendam.setAutoCreateRowSorter(true);
        Table_Data_Rendam.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Table_Data_Rendam.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tanggal", "No LP", "Grade", "Keping", "Berat", "Lama Waktu", "Memo", "Waktu Steam", "Suhu Awal Steam", "Suhu Akhir Steam", "Pekerja Steam", "Jenis Treatment", "Waktu Mulai Rendam", "Waktu Selesai Rendam"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.Object.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(Table_Data_Rendam);

        button_export_data_rendam.setBackground(new java.awt.Color(255, 255, 255));
        button_export_data_rendam.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export_data_rendam.setText("Export to Excel");
        button_export_data_rendam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_data_rendamActionPerformed(evt);
            }
        });

        jPanel_operation_data_cuci1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_operation_data_cuci1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Rendam", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Yu Gothic", 1, 12))); // NOI18N
        jPanel_operation_data_cuci1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jPanel_operation_data_cuci1.setName("aah"); // NOI18N

        button_update_rendam.setBackground(new java.awt.Color(255, 255, 255));
        button_update_rendam.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_update_rendam.setText("Update");
        button_update_rendam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_update_rendamActionPerformed(evt);
            }
        });

        button_insert_rendam.setBackground(new java.awt.Color(255, 255, 255));
        button_insert_rendam.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_insert_rendam.setText("insert");
        button_insert_rendam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_insert_rendamActionPerformed(evt);
            }
        });

        button_delete_rendam.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_rendam.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_delete_rendam.setText("Delete");
        button_delete_rendam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_rendamActionPerformed(evt);
            }
        });

        button_clear_rendam.setBackground(new java.awt.Color(255, 255, 255));
        button_clear_rendam.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_clear_rendam.setText("Clear Text");
        button_clear_rendam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_clear_rendamActionPerformed(evt);
            }
        });

        txt_waktu_rendam.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_waktu_rendam.setText("10");

        txt_no_lp_rendam.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_no_lp_rendam.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_no_lp_rendamKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_no_lp_rendamKeyTyped(evt);
            }
        });

        label_tgl_rendam.setBackground(new java.awt.Color(255, 255, 255));
        label_tgl_rendam.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_tgl_rendam.setText("Tanggal :");

        label_waktu_rendam.setBackground(new java.awt.Color(255, 255, 255));
        label_waktu_rendam.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_waktu_rendam.setText("Lama Waktu Rendam :");

        label_no_lp_rendam.setBackground(new java.awt.Color(255, 255, 255));
        label_no_lp_rendam.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_no_lp_rendam.setText("No Laporan Produksi :");

        Date_tgl_rendam.setBackground(new java.awt.Color(255, 255, 255));
        Date_tgl_rendam.setDate(new Date());
        Date_tgl_rendam.setDateFormatString("dd MMMM yyyy");
        Date_tgl_rendam.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        Date_tgl_rendam.setMaxSelectableDate(new Date());

        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Menit");

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 2, 10)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(102, 102, 102));
        jLabel1.setText("(*jika tidak diisi maka akan otomatis mengambil tanggal hari ini)");

        javax.swing.GroupLayout jPanel_operation_data_cuci1Layout = new javax.swing.GroupLayout(jPanel_operation_data_cuci1);
        jPanel_operation_data_cuci1.setLayout(jPanel_operation_data_cuci1Layout);
        jPanel_operation_data_cuci1Layout.setHorizontalGroup(
            jPanel_operation_data_cuci1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_operation_data_cuci1Layout.createSequentialGroup()
                .addGroup(jPanel_operation_data_cuci1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel_operation_data_cuci1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel_operation_data_cuci1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_waktu_rendam, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_no_lp_rendam, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_tgl_rendam, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_operation_data_cuci1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_operation_data_cuci1Layout.createSequentialGroup()
                                .addComponent(txt_waktu_rendam, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(Date_tgl_rendam, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txt_no_lp_rendam)))
                    .addGroup(jPanel_operation_data_cuci1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(button_update_rendam)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_insert_rendam)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete_rendam)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_clear_rendam))
                    .addGroup(jPanel_operation_data_cuci1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_operation_data_cuci1Layout.setVerticalGroup(
            jPanel_operation_data_cuci1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_operation_data_cuci1Layout.createSequentialGroup()
                .addGroup(jPanel_operation_data_cuci1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Date_tgl_rendam, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_tgl_rendam, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_data_cuci1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_no_lp_rendam, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_no_lp_rendam, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_data_cuci1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_waktu_rendam, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_waktu_rendam, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel_operation_data_cuci1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_update_rendam, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_insert_rendam, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete_rendam, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_clear_rendam, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        label_total_data_rendam.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_rendam.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label_total_data_rendam.setText("TOTAL");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel6.setText("Total Data Rendam :");

        button_Catatan_Rendaman_Bahan_Mentah.setBackground(new java.awt.Color(255, 255, 255));
        button_Catatan_Rendaman_Bahan_Mentah.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_Catatan_Rendaman_Bahan_Mentah.setText("Catatan_Rendaman_Bahan_Mentah");
        button_Catatan_Rendaman_Bahan_Mentah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_Catatan_Rendaman_Bahan_MentahActionPerformed(evt);
            }
        });

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Tanggal :");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Memo LP :");

        txt_search_memo.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_memo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_memoKeyPressed(evt);
            }
        });

        button_input_waktu_rendam.setBackground(new java.awt.Color(255, 255, 255));
        button_input_waktu_rendam.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_input_waktu_rendam.setText("Input Waktu Rendam .csv");
        button_input_waktu_rendam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_input_waktu_rendamActionPerformed(evt);
            }
        });

        button_set_data_rendam_otomatis.setBackground(new java.awt.Color(255, 255, 255));
        button_set_data_rendam_otomatis.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_set_data_rendam_otomatis.setText("Set data rendam otomatis");
        button_set_data_rendam_otomatis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_set_data_rendam_otomatisActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_DataRendamLayout = new javax.swing.GroupLayout(jPanel_DataRendam);
        jPanel_DataRendam.setLayout(jPanel_DataRendamLayout);
        jPanel_DataRendamLayout.setHorizontalGroup(
            jPanel_DataRendamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_DataRendamLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_DataRendamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_DataRendamLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_no_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_memo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date1_rendam, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date2_rendam, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_rendam)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export_data_rendam)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_Catatan_Rendaman_Bahan_Mentah)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_input_waktu_rendam)
                        .addGap(0, 76, Short.MAX_VALUE))
                    .addGroup(jPanel_DataRendamLayout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 1002, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_DataRendamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel_operation_data_cuci1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel_DataRendamLayout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_data_rendam))
                            .addComponent(button_set_data_rendam_otomatis))))
                .addContainerGap())
        );
        jPanel_DataRendamLayout.setVerticalGroup(
            jPanel_DataRendamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_DataRendamLayout.createSequentialGroup()
                .addGroup(jPanel_DataRendamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(Date2_rendam, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search_rendam, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_memo, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_export_data_rendam, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_no_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_Catatan_Rendaman_Bahan_Mentah, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Date1_rendam, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_input_waktu_rendam, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(jPanel_DataRendamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_DataRendamLayout.createSequentialGroup()
                        .addComponent(jPanel_operation_data_cuci1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_DataRendamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(label_total_data_rendam))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_set_data_rendam_otomatis, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 630, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_DataRendam, javax.swing.GroupLayout.DEFAULT_SIZE, 1356, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_DataRendam, javax.swing.GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button_update_rendamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_update_rendamActionPerformed
        // TODO add your handling code here:
        int j = Table_Data_Rendam.getSelectedRow();
        Boolean Check = true;
        try {
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih salah satu data pada tabel !");
            } else {
                if (!txt_no_lp_rendam.getText().equals(Table_Data_Rendam.getValueAt(j, 1))) {
                    JOptionPane.showMessageDialog(this, "cannot change no laporan produksi");
                }
                if (Check) {
                    String Query = "UPDATE `tb_rendam` SET `tanggal_rendam`='" + dateFormat.format(Date_tgl_rendam.getDate()) + "',`lama_waktu_rendam`='" + txt_waktu_rendam.getText() + "' WHERE `tb_rendam`.`no_laporan_produksi` = '" + Table_Data_Rendam.getValueAt(j, 1).toString() + "'";
                    executeSQLQuery(Query, "updated !");
                    button_clear_rendam.doClick();
                }
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_button_update_rendamActionPerformed

    private void button_insert_rendamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_insert_rendamActionPerformed
        // TODO add your handling code here:
        try {
            int total_baris = Table_Data_Rendam.getRowCount();
            Boolean Check = true;
            for (int i = 0; i < total_baris; i++) {
                if (txt_no_lp_rendam.getText().equals(Table_Data_Rendam.getValueAt(i, 1))) {
                    JOptionPane.showMessageDialog(this, "No Laporan Produksi (" + txt_no_lp_rendam.getText() + ") sudah direndam !");
                    Check = false;
                }
            }

            Date tgl_lp = null;
            String get_tgl_kartu = "SELECT `tanggal_lp` FROM `tb_laporan_produksi` WHERE `no_laporan_produksi` = '" + txt_no_lp_rendam.getText() + "'";
            ResultSet result = Utility.db.getStatement().executeQuery(get_tgl_kartu);
            if (result.next()) {
                tgl_lp = result.getDate("tanggal_lp");
                if (tgl_lp.after(Date_tgl_rendam.getDate())) {
                    JOptionPane.showMessageDialog(this, "Maaf Tanggal Rendam harus setelah tanggal Laporan Produksi !");
                    Check = false;
                }
            } else {
                JOptionPane.showMessageDialog(this, "Maaf Laporan Produksi tidak ditemukan !");
                Check = false;
            }

            if (Check) {
                String insert;
                //kalau date nya nggk di isi, akan terisi tanggal hari itu secara otomatis
                if (Date_tgl_rendam.getDate() == null) {
                    insert = dateFormat.format(date);
                } else {
                    insert = dateFormat.format(Date_tgl_rendam.getDate());
                }
                String Query = "INSERT INTO `tb_rendam`(`no_laporan_produksi`, `tanggal_rendam`, `lama_waktu_rendam`) VALUES ('" + txt_no_lp_rendam.getText() + "','" + insert + "','" + txt_waktu_rendam.getText() + "')";
                executeSQLQuery(Query, "inserted !");
                txt_no_lp_rendam.setText(null);
                txt_no_lp_rendam.requestFocus();
            }
        } catch (SQLException | NullPointerException | HeadlessException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            Logger.getLogger(JPanel_DataRendam.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_insert_rendamActionPerformed

    private void button_delete_rendamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_rendamActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_Data_Rendam.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang ingin di hapus !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String Query = "DELETE FROM `tb_rendam` WHERE `tb_rendam`.`no_laporan_produksi` = \'" + Table_Data_Rendam.getValueAt(j, 1) + "\'";
                    executeSQLQuery(Query, "deleted !");
                }
                button_clear_rendam.doClick();
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_button_delete_rendamActionPerformed

    private void button_clear_rendamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_clear_rendamActionPerformed
        // TODO add your handling code here:
        Date_tgl_rendam.setDate(null);
        txt_no_lp_rendam.setText(null);
        txt_waktu_rendam.setText(null);
    }//GEN-LAST:event_button_clear_rendamActionPerformed

    private void txt_search_no_lpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_no_lpKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_Rendam();
        }
    }//GEN-LAST:event_txt_search_no_lpKeyPressed

    private void button_search_rendamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_rendamActionPerformed
        // TODO add your handling code here:
        refreshTable_Rendam();
    }//GEN-LAST:event_button_search_rendamActionPerformed

    private void button_export_data_rendamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_data_rendamActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_Data_Rendam.getModel();
        ExportToExcel.writeToExcel(model, jPanel_DataRendam);
    }//GEN-LAST:event_button_export_data_rendamActionPerformed

    private void button_Catatan_Rendaman_Bahan_MentahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_Catatan_Rendaman_Bahan_MentahActionPerformed
        try {
            String no_lp = "";
            for (int i = 0; i < Table_Data_Rendam.getRowCount(); i++) {
                if (i != 0) {
                    no_lp = no_lp + ", ";
                }
                no_lp = no_lp + "'" + Table_Data_Rendam.getValueAt(i, 1).toString() + "'";
            }
            String Query = "SELECT `tanggal_rendam`, `tb_rendam`.`no_laporan_produksi`, `tb_laporan_produksi`.`no_kartu_waleta`, `tb_bahan_baku_masuk_cheat`.`no_registrasi`, `tb_laporan_produksi`.`kode_grade`, `tb_laporan_produksi`.`jumlah_keping`, `tb_laporan_produksi`.`berat_basah`, `lama_waktu_rendam`, `tb_laporan_produksi`.`memo_lp`, \n"
                    + "`cheat_no_kartu`, `cheat_rsb`, "
                    + "`waktu_mulai_rendam`, `waktu_selesai_rendam`, `pekerja_rendam`  "
                    + "FROM `tb_rendam` "
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_rendam`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                    + "LEFT JOIN `tb_bahan_baku_masuk_cheat` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk_cheat`.`no_kartu_waleta`"
                    + "WHERE "
                    + "`tb_rendam`.`no_laporan_produksi` IN (" + no_lp + ") "
                    + "AND `waktu_mulai_rendam` IS NOT NULL "
                    //                    + "AND `cheat_rsb` IS NOT NULL "
                    + "ORDER BY `tanggal_rendam` DESC, `waktu_mulai_rendam` DESC, `tb_rendam`.`no_laporan_produksi` ASC";
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(Query);
            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Catatan_Rendaman_Bahan_Mentah.jrxml");
            JASP_DESIGN.setQuery(newQuery);
            Map<String, Object> map = new HashMap<>();
            map.put("CHEAT", 0);

            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, map, Utility.db.getConnection());
            JasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)
        } catch (JRException ex) {
            Logger.getLogger(JPanel_DataRendam.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_Catatan_Rendaman_Bahan_MentahActionPerformed

    private void txt_no_lp_rendamKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_no_lp_rendamKeyTyped
        // TODO add your handling code here:
        if (Character.isAlphabetic(evt.getKeyChar())) {
            char e = evt.getKeyChar();
            String c = String.valueOf(e);
            evt.consume();
            txt_no_lp_rendam.setText(txt_no_lp_rendam.getText() + c.toUpperCase());
        }
    }//GEN-LAST:event_txt_no_lp_rendamKeyTyped

    private void txt_search_memoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_memoKeyPressed
        // TODO add your handling code here:
        refreshTable_Rendam();
    }//GEN-LAST:event_txt_search_memoKeyPressed

    private void txt_no_lp_rendamKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_no_lp_rendamKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            button_insert_rendam.doClick();
        }
    }//GEN-LAST:event_txt_no_lp_rendamKeyPressed

    private void button_input_waktu_rendamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_input_waktu_rendamActionPerformed
        // TODO add your handling code here:
        try {
            int n = 0;
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select CSV file to import!");
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                Utility.db.getConnection();
                File file = chooser.getSelectedFile();
                String filename1 = file.getAbsolutePath();
                try ( BufferedReader br = new BufferedReader(new FileReader(filename1))) {
                    String line;
                    String Query = null;
                    try {
                        Utility.db.getConnection().setAutoCommit(false);
                        while ((line = br.readLine()) != null) {
                            String[] value = line.split(";");
                            Query = "UPDATE `tb_rendam` SET \n"
                                    + "`waktu_mulai_rendam` = '" + value[1] + "',\n"
                                    + "`waktu_selesai_rendam` = '" + value[2] + "'\n"
                                    + "WHERE `no_laporan_produksi` = '" + value[0] + "'";
                            Utility.db.getStatement().executeUpdate(Query);
                            n++;
                            System.out.println(Query);
                        }
                        Utility.db.getConnection().commit();
                        JOptionPane.showMessageDialog(this, "Data Berhasil Masuk : " + n);
                    } catch (Exception ex) {
                        Utility.db.getConnection().rollback();
                        JOptionPane.showMessageDialog(this, ex + "\n" + Query);
                        Logger.getLogger(JPanel_DataRendam.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        Utility.db.getConnection().setAutoCommit(true);
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JPanel_DataRendam.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_input_waktu_rendamActionPerformed

    private void button_set_data_rendam_otomatisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_set_data_rendam_otomatisActionPerformed
        // TODO add your handling code here:
        int x = JOptionPane.showConfirmDialog(this, "Fungsi ini untuk input semua data rendam yang lama waktu rendamnya 10 menit\n"
                + "menjadi 2 menit untuk grade warna W1 dan W2, dan 3 menit untuk grade lainnya\n"
                + "dan memasukkan nama pekerja rendam menjadi HENGKI FEBRI TRI NURYANTO\n"
                + "Lanjutkan?", "Edit data rendam", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if (x == JOptionPane.YES_OPTION) {
            try {
                Utility.db.getConnection().setAutoCommit(false);
                String Query1 = "UPDATE `tb_rendam` \n"
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_rendam`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "SET \n"
                        + "`pekerja_rendam` = 'HENGKI FEBRI TRI NURYANTO', `lama_waktu_rendam` = 2\n"
                        + "WHERE (`tb_laporan_produksi`.`kode_grade` LIKE '%W1%' OR `tb_laporan_produksi`.`kode_grade` LIKE '%W2%')\n"
                        + "AND `lama_waktu_rendam` = 10";
                int a = Utility.db.getStatement().executeUpdate(Query1);
                String Query2 = "UPDATE `tb_rendam` \n"
                        + "LEFT JOIN `tb_laporan_produksi` ON `tb_rendam`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi`\n"
                        + "SET \n"
                        + "`pekerja_rendam` = 'HENGKI FEBRI TRI NURYANTO', `lama_waktu_rendam` = 3\n"
                        + "WHERE (`tb_laporan_produksi`.`kode_grade` NOT LIKE '%W1%' AND `tb_laporan_produksi`.`kode_grade` NOT LIKE '%W2%')\n"
                        + "AND `lama_waktu_rendam` = 10";
                int b = Utility.db.getStatement().executeUpdate(Query2);
                Utility.db.getConnection().commit();
                JOptionPane.showMessageDialog(this, "Data Berhasil Masuk : " + (a + b));
            } catch (Exception ex) {
                try {
                    Utility.db.getConnection().rollback();
                } catch (SQLException ex1) {
                    Logger.getLogger(JPanel_DataRendam.class.getName()).log(Level.SEVERE, null, ex1);
                }
                Logger.getLogger(JPanel_DataRendam.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    Utility.db.getConnection().setAutoCommit(true);
                } catch (SQLException ex) {
                    Logger.getLogger(JPanel_DataRendam.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_button_set_data_rendam_otomatisActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser Date1_rendam;
    private com.toedter.calendar.JDateChooser Date2_rendam;
    private com.toedter.calendar.JDateChooser Date_tgl_rendam;
    private javax.swing.JTable Table_Data_Rendam;
    private javax.swing.JButton button_Catatan_Rendaman_Bahan_Mentah;
    private javax.swing.JButton button_clear_rendam;
    public javax.swing.JButton button_delete_rendam;
    private javax.swing.JButton button_export_data_rendam;
    private javax.swing.JButton button_input_waktu_rendam;
    public javax.swing.JButton button_insert_rendam;
    private javax.swing.JButton button_search_rendam;
    private javax.swing.JButton button_set_data_rendam_otomatis;
    public javax.swing.JButton button_update_rendam;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel_DataRendam;
    private javax.swing.JPanel jPanel_operation_data_cuci1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel label_no_lp_rendam;
    private javax.swing.JLabel label_tgl_rendam;
    private javax.swing.JLabel label_total_data_rendam;
    private javax.swing.JLabel label_waktu_rendam;
    private javax.swing.JTextField txt_no_lp_rendam;
    private javax.swing.JTextField txt_search_memo;
    private javax.swing.JTextField txt_search_no_lp;
    private javax.swing.JTextField txt_waktu_rendam;
    // End of variables declaration//GEN-END:variables

}
