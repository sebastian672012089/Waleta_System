package waleta_system.BahanBaku;

import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.Utility;
import waleta_system.Class.ExportToExcel;

public class JPanel_GradeBahanBaku extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();

    public JPanel_GradeBahanBaku() {
        initComponents();
    }

    public void init() {
        refreshTable();
        Table_GradeBahanBaku.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_GradeBahanBaku.getSelectedRow() != -1) {
                    int i = Table_GradeBahanBaku.getSelectedRow();
                    txt_kode_grade.setText(Table_GradeBahanBaku.getValueAt(i, 0).toString());
                    txt_jenis_bentuk.setText(Table_GradeBahanBaku.getValueAt(i, 1).toString());
                    txt_jenis_bulu.setText(Table_GradeBahanBaku.getValueAt(i, 2).toString());
                    txt_jenis_warna.setText(Table_GradeBahanBaku.getValueAt(i, 3).toString());
                    txt_kategori.setText(Table_GradeBahanBaku.getValueAt(i, 4).toString());
                    txt_kategori_proses.setText(Table_GradeBahanBaku.getValueAt(i, 5).toString());
                    txt_memo_lp.setText(Table_GradeBahanBaku.getValueAt(i, 7).toString());

                    button_ubah_status.setEnabled(true);
                    if ((boolean) Table_GradeBahanBaku.getValueAt(i, 8)) {
                        button_ubah_status.setText("NON-AKTIFKAN");
                    } else {
                        button_ubah_status.setText("AKTIFKAN");
                    }
                    txt_harga_gram_cabuto.setText(Table_GradeBahanBaku.getValueAt(i, 9).toString());
                }
            }
        });
    }

    public void refreshTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) Table_GradeBahanBaku.getModel();
            model.setRowCount(0);
            sql = "SELECT * FROM `waleta_database`.`tb_grade_bahan_baku` "
                    + "WHERE `kode_grade` LIKE '%" + txt_search_grade.getText() + "%' ";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            while (rs.next()) {
                row[0] = rs.getString("kode_grade");
                row[1] = rs.getString("jenis_bentuk");
                row[2] = rs.getString("jenis_bulu");
                row[3] = rs.getString("jenis_warna");
                row[4] = rs.getString("kategori");
                row[5] = rs.getString("kategori_proses");
                row[6] = rs.getInt("target_ctk_mku");
                row[7] = rs.getString("memo_untuk_lp");
                row[8] = rs.getBoolean("status_grade_baku");
                row[9] = rs.getInt("harga_gram_cabuto");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_GradeBahanBaku);
//            TableAlignment.setHorizontalAlignment(JLabel.CENTER);
//            for (int i = 0; i < Table_GradeBahanBaku.getColumnCount(); i++) {
//                Table_GradeBahanBaku.getColumnModel().getColumn(i).setCellRenderer(TableAlignment);
//            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_GradeBahanBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void executeSQLQuery(String query, String message) {
        Utility.db.getConnection();
        try {
            Utility.db.getConnection().createStatement();
            if ((Utility.db.getStatement().executeUpdate(query)) == 1) {
                JOptionPane.showMessageDialog(this, "data " + message + " Successfully");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "data not " + message);
            }
        } catch (SQLException | HeadlessException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_GradeBahanBaku.class.getName()).log(Level.SEVERE, null, e);
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

        jPanel_grade_bahan_baku = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_GradeBahanBaku = new javax.swing.JTable();
        jPanel_operation_grade = new javax.swing.JPanel();
        button_update_grade = new javax.swing.JButton();
        button_insert_grade = new javax.swing.JButton();
        button_delete_grade = new javax.swing.JButton();
        button_clear_grade = new javax.swing.JButton();
        txt_kode_grade = new javax.swing.JTextField();
        txt_jenis_bentuk = new javax.swing.JTextField();
        txt_jenis_bulu = new javax.swing.JTextField();
        txt_jenis_warna = new javax.swing.JTextField();
        label_grade_kode = new javax.swing.JLabel();
        label_grade_bentuk = new javax.swing.JLabel();
        label_grade_bulu = new javax.swing.JLabel();
        label_grade_warna = new javax.swing.JLabel();
        label_grade_warna1 = new javax.swing.JLabel();
        txt_kategori = new javax.swing.JTextField();
        label_grade_warna2 = new javax.swing.JLabel();
        txt_memo_lp = new javax.swing.JTextField();
        label_grade_warna3 = new javax.swing.JLabel();
        txt_kategori_proses = new javax.swing.JTextField();
        label_grade_warna4 = new javax.swing.JLabel();
        txt_harga_gram_cabuto = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        label_total_data_gradeBaku = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        button_export_GradeBahanBaku = new javax.swing.JButton();
        button_edit_target_cetak = new javax.swing.JButton();
        txt_search_grade = new javax.swing.JTextField();
        button_refresh_grade = new javax.swing.JButton();
        button_ubah_status = new javax.swing.JButton();

        jPanel_grade_bahan_baku.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_grade_bahan_baku.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "MASTER GRADE BAHAN BAKU", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        jPanel_grade_bahan_baku.setMaximumSize(new java.awt.Dimension(1306, 736));
        jPanel_grade_bahan_baku.setPreferredSize(new java.awt.Dimension(1366, 700));

        Table_GradeBahanBaku.setAutoCreateRowSorter(true);
        Table_GradeBahanBaku.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        Table_GradeBahanBaku.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade", "Bentuk", "Jenis Bulu", "Grade Warna", "Kategori", "Kategori Proses", "Target Cetak MKU %", "Memo LP", "Status", "Rp / gr Cabuto"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Boolean.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Table_GradeBahanBaku.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Table_GradeBahanBaku);

        jPanel_operation_grade.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_operation_grade.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel_operation_grade.setName("aah"); // NOI18N

        button_update_grade.setBackground(new java.awt.Color(255, 255, 255));
        button_update_grade.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_update_grade.setText("Update");
        button_update_grade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_update_gradeActionPerformed(evt);
            }
        });

        button_insert_grade.setBackground(new java.awt.Color(255, 255, 255));
        button_insert_grade.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_insert_grade.setText("insert");
        button_insert_grade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_insert_gradeActionPerformed(evt);
            }
        });

        button_delete_grade.setBackground(new java.awt.Color(255, 255, 255));
        button_delete_grade.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_delete_grade.setText("Delete");
        button_delete_grade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_delete_gradeActionPerformed(evt);
            }
        });

        button_clear_grade.setBackground(new java.awt.Color(255, 255, 255));
        button_clear_grade.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_clear_grade.setText("Clear Text");
        button_clear_grade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_clear_gradeActionPerformed(evt);
            }
        });

        txt_kode_grade.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        txt_jenis_bentuk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        txt_jenis_bulu.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        txt_jenis_warna.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        label_grade_kode.setBackground(new java.awt.Color(255, 255, 255));
        label_grade_kode.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_grade_kode.setText("Kode Grade :");

        label_grade_bentuk.setBackground(new java.awt.Color(255, 255, 255));
        label_grade_bentuk.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_grade_bentuk.setText("Jenis Bentuk :");

        label_grade_bulu.setBackground(new java.awt.Color(255, 255, 255));
        label_grade_bulu.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_grade_bulu.setText("Jenis Bulu :");

        label_grade_warna.setBackground(new java.awt.Color(255, 255, 255));
        label_grade_warna.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_grade_warna.setText("Jenis Warna :");

        label_grade_warna1.setBackground(new java.awt.Color(255, 255, 255));
        label_grade_warna1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_grade_warna1.setText("Kategori :");

        txt_kategori.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        label_grade_warna2.setBackground(new java.awt.Color(255, 255, 255));
        label_grade_warna2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_grade_warna2.setText("Memo LP :");

        txt_memo_lp.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        label_grade_warna3.setBackground(new java.awt.Color(255, 255, 255));
        label_grade_warna3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_grade_warna3.setText("Kategori Proses :");

        txt_kategori_proses.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        label_grade_warna4.setBackground(new java.awt.Color(255, 255, 255));
        label_grade_warna4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_grade_warna4.setText("Harga / gr Cabuto :");

        txt_harga_gram_cabuto.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_harga_gram_cabuto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_harga_gram_cabutoKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel_operation_gradeLayout = new javax.swing.GroupLayout(jPanel_operation_grade);
        jPanel_operation_grade.setLayout(jPanel_operation_gradeLayout);
        jPanel_operation_gradeLayout.setHorizontalGroup(
            jPanel_operation_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_operation_gradeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_operation_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_operation_gradeLayout.createSequentialGroup()
                        .addGroup(jPanel_operation_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_grade_kode, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_grade_bulu, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_grade_bentuk, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_operation_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_jenis_bulu, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_jenis_bentuk, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_kode_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel_operation_gradeLayout.createSequentialGroup()
                        .addComponent(label_grade_warna, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_jenis_warna, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_operation_gradeLayout.createSequentialGroup()
                        .addComponent(button_update_grade)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_insert_grade)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_delete_grade)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_clear_grade))
                    .addGroup(jPanel_operation_gradeLayout.createSequentialGroup()
                        .addComponent(label_grade_warna1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_kategori, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_operation_gradeLayout.createSequentialGroup()
                        .addComponent(label_grade_warna2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_memo_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_operation_gradeLayout.createSequentialGroup()
                        .addComponent(label_grade_warna3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_kategori_proses, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_operation_gradeLayout.createSequentialGroup()
                        .addComponent(label_grade_warna4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_harga_gram_cabuto, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_operation_gradeLayout.setVerticalGroup(
            jPanel_operation_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_operation_gradeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_operation_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_kode_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_grade_kode, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_grade_bentuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_jenis_bentuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_grade_bulu, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_jenis_bulu, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_grade_warna, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_jenis_warna, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_grade_warna1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_kategori, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_grade_warna3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_kategori_proses, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_grade_warna2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_memo_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_grade_warna4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_harga_gram_cabuto, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_operation_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_update_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_insert_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_delete_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_clear_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel6.setText("Total Data :");

        label_total_data_gradeBaku.setBackground(new java.awt.Color(255, 255, 255));
        label_total_data_gradeBaku.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        label_total_data_gradeBaku.setText("TOTAL");

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("Search Grade :");

        button_export_GradeBahanBaku.setBackground(new java.awt.Color(255, 255, 255));
        button_export_GradeBahanBaku.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_export_GradeBahanBaku.setText("Export To Excel");
        button_export_GradeBahanBaku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_export_GradeBahanBakuActionPerformed(evt);
            }
        });

        button_edit_target_cetak.setBackground(new java.awt.Color(255, 255, 255));
        button_edit_target_cetak.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_edit_target_cetak.setText("Edit Target Cetak MKU");
        button_edit_target_cetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_edit_target_cetakActionPerformed(evt);
            }
        });

        txt_search_grade.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        button_refresh_grade.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_grade.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_refresh_grade.setText("Refresh");
        button_refresh_grade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_gradeActionPerformed(evt);
            }
        });

        button_ubah_status.setBackground(new java.awt.Color(255, 255, 255));
        button_ubah_status.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_ubah_status.setText("AKTIFKAN");
        button_ubah_status.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_ubah_statusActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_grade_bahan_bakuLayout = new javax.swing.GroupLayout(jPanel_grade_bahan_baku);
        jPanel_grade_bahan_baku.setLayout(jPanel_grade_bahan_bakuLayout);
        jPanel_grade_bahan_bakuLayout.setHorizontalGroup(
            jPanel_grade_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_grade_bahan_bakuLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_grade_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_grade_bahan_bakuLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 964, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel_operation_grade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanel_grade_bahan_bakuLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_refresh_grade)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_edit_target_cetak)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_ubah_status, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export_GradeBahanBaku)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_data_gradeBaku)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel_grade_bahan_bakuLayout.setVerticalGroup(
            jPanel_grade_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_grade_bahan_bakuLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_grade_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(button_export_GradeBahanBaku, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_edit_target_cetak, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_total_data_gradeBaku, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_ubah_status, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_grade_bahan_bakuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_grade_bahan_bakuLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 641, Short.MAX_VALUE)
                        .addGap(11, 11, 11))
                    .addGroup(jPanel_grade_bahan_bakuLayout.createSequentialGroup()
                        .addComponent(jPanel_operation_grade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel_grade_bahan_baku, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_grade_bahan_baku, javax.swing.GroupLayout.DEFAULT_SIZE, 716, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button_update_gradeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_update_gradeActionPerformed
        // TODO add your handling code here:
        //        TableModel Table = (DefaultTableModel)Table_GradeBahanBaku.getModel();
        int j = Table_GradeBahanBaku.getSelectedRow();
        int total_baris = Table_GradeBahanBaku.getRowCount();
        Boolean Check = true;
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih salah satu data pada tabel !");
        } else {
            if (!txt_kode_grade.getText().equals(Table_GradeBahanBaku.getValueAt(j, 0))) {
                for (int i = 0; i < total_baris; i++) {
                    if (txt_kode_grade.getText().equals(Table_GradeBahanBaku.getValueAt(i, 0))) {
                        JOptionPane.showMessageDialog(this, "Kode Grade (" + txt_kode_grade.getText() + ") sudah terpakai, tidak boleh ada KODE GRADE yang sama");
                        Check = false;
                    }
                }
            }

            if (Check) {
                int harga_gram_cabuto = 0;
                if (txt_harga_gram_cabuto.getText() != null && !txt_harga_gram_cabuto.getText().equals("")) {
                    harga_gram_cabuto = Integer.valueOf(txt_harga_gram_cabuto.getText());
                }
                String Query = "UPDATE `tb_grade_bahan_baku` SET "
                        + "`kode_grade` = TRIM('" + txt_kode_grade.getText() + "'), "
                        + "`jenis_bentuk` = '" + txt_jenis_bentuk.getText() + "', "
                        + "`jenis_bulu` = '" + txt_jenis_bulu.getText() + "', "
                        + "`jenis_warna` = '" + txt_jenis_warna.getText() + "', "
                        + "`kategori` = '" + txt_kategori.getText() + "', "
                        + "`kategori_proses` = '" + txt_kategori_proses.getText() + "', "
                        + "`memo_untuk_lp` = '" + txt_memo_lp.getText() + "', "
                        + "`harga_gram_cabuto` = '" + harga_gram_cabuto + "' "
                        + "WHERE `tb_grade_bahan_baku`.`kode_grade` = '" + Table_GradeBahanBaku.getValueAt(j, 0) + "'";
                executeSQLQuery(Query, "updated !");
                button_clear_grade.doClick();
            }
        }
    }//GEN-LAST:event_button_update_gradeActionPerformed

    private void button_insert_gradeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_insert_gradeActionPerformed
        // TODO add your handling code here:
        try {
            int total_baris = Table_GradeBahanBaku.getRowCount();
            Boolean Check = true;
            for (int i = 0; i < total_baris; i++) {
                if (txt_kode_grade.getText().equals(Table_GradeBahanBaku.getValueAt(i, 0))) {
                    JOptionPane.showMessageDialog(this, "Kode Grade (" + txt_kode_grade.getText() + ") sudah terpakai, tidak boleh ada kode Grade yang sama");
                    Check = false;
                }
            }
            if (Check) {
                int harga_gram_cabuto = 0;
                if (txt_harga_gram_cabuto.getText() != null && !txt_harga_gram_cabuto.getText().equals("")) {
                    harga_gram_cabuto = Integer.valueOf(txt_harga_gram_cabuto.getText());
                }
                String Query = "INSERT INTO `tb_grade_bahan_baku` (`kode_grade`, `jenis_bentuk`, `jenis_bulu`, `jenis_warna`, `kategori`, `kategori_proses`, `memo_untuk_lp`, `harga_gram_cabuto`) "
                        + "VALUES (TRIM('" + txt_kode_grade.getText() + "'), '" + txt_jenis_bentuk.getText() + "', '" + txt_jenis_bulu.getText() + "', '" + txt_jenis_warna.getText() + "', '" + txt_kategori.getText() + "', '" + txt_kategori_proses.getText() + "', '" + txt_memo_lp.getText() + "', '" + harga_gram_cabuto + "')";
                executeSQLQuery(Query, "inserted ");

                button_clear_grade.doClick();
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_GradeBahanBaku.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_insert_gradeActionPerformed

    private void button_delete_gradeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_delete_gradeActionPerformed
        // TODO add your handling code here:
        try {
            int j = Table_GradeBahanBaku.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang ingin di hapus !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String Query = "DELETE FROM `tb_grade_bahan_baku` WHERE `tb_grade_bahan_baku`.`kode_grade` = '" + txt_kode_grade.getText() + "'";
                    executeSQLQuery(Query, "deleted ");
                    button_clear_grade.doClick();
                }
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }//GEN-LAST:event_button_delete_gradeActionPerformed

    private void button_clear_gradeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_clear_gradeActionPerformed
        // TODO add your handling code here:
        txt_kode_grade.setText(null);
        txt_jenis_bentuk.setText(null);
        txt_jenis_bulu.setText(null);
        txt_jenis_warna.setText(null);
        txt_kategori.setText(null);
        txt_kategori_proses.setText(null);
        txt_memo_lp.setText(null);
        txt_harga_gram_cabuto.setText(null);
    }//GEN-LAST:event_button_clear_gradeActionPerformed

    private void button_export_GradeBahanBakuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_export_GradeBahanBakuActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) Table_GradeBahanBaku.getModel();
        ExportToExcel.writeToExcel(model, this);

    }//GEN-LAST:event_button_export_GradeBahanBakuActionPerformed

    private void button_edit_target_cetakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_edit_target_cetakActionPerformed
        // TODO add your handling code here:
        int j = Table_GradeBahanBaku.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan Klik data di Tabel");
        } else {
            try {
                String target_lama = Table_GradeBahanBaku.getValueAt(j, 6).toString();
                String target_baru1 = JOptionPane.showInputDialog("Masukkan target baru untuk '" + Table_GradeBahanBaku.getValueAt(j, 0).toString() + "' (0-100%) : ", target_lama);
                if (target_baru1 != null && !target_baru1.equals("")) {
                    int target_baru = (int) Table_GradeBahanBaku.getValueAt(j, 6);
                    target_baru = Integer.valueOf(target_baru1);
                    if (target_baru >= 0 && target_baru <= 100) {
                        sql = "UPDATE `tb_grade_bahan_baku` SET "
                                + "`target_ctk_mku` = '" + target_baru + "' "
                                + "WHERE `kode_grade` = '" + Table_GradeBahanBaku.getValueAt(j, 0) + "'";
                        Utility.db.getConnection().createStatement();
                        if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                            JOptionPane.showMessageDialog(this, "Data Saved !");
                            refreshTable();
                        } else {
                            JOptionPane.showMessageDialog(this, "Failed!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Target dalam persentase, range target adalah 0-100");
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex);
                Logger.getLogger(JPanel_GradeBahanBaku.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_edit_target_cetakActionPerformed

    private void button_refresh_gradeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_gradeActionPerformed
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_button_refresh_gradeActionPerformed

    private void button_ubah_statusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_ubah_statusActionPerformed
        // TODO add your handling code here:
        int j = Table_GradeBahanBaku.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih data yang mau di EDIT !");
        } else {
            try {
                String status = null;
                if (button_ubah_status.getText().equals("AKTIFKAN")) {
                    status = "1";
                } else if (button_ubah_status.getText().equals("NON-AKTIFKAN")) {
                    status = "0";
                }
                sql = "UPDATE `tb_grade_bahan_baku` SET `status_grade_baku` = " + status + " WHERE `tb_grade_bahan_baku`.`kode_grade` = '" + Table_GradeBahanBaku.getValueAt(j, 0) + "'";
                Utility.db.getConnection().createStatement();
                if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                    JOptionPane.showMessageDialog(this, Table_GradeBahanBaku.getValueAt(j, 0) + " di" + button_ubah_status.getText());
                    refreshTable();
                    button_clear_grade.doClick();
                } else {
                    JOptionPane.showMessageDialog(this, "FAILED");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex);
                Logger.getLogger(JPanel_GradeBahanBaku.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_button_ubah_statusActionPerformed

    private void txt_harga_gram_cabutoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_harga_gram_cabutoKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())
                && evt.getKeyCode() != KeyEvent.VK_ENTER
                && evt.getKeyCode() != KeyEvent.VK_BACK_SPACE
                && evt.getKeyCode() != KeyEvent.VK_DELETE) {
            evt.consume();
        }
    }//GEN-LAST:event_txt_harga_gram_cabutoKeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable Table_GradeBahanBaku;
    private javax.swing.JButton button_clear_grade;
    public javax.swing.JButton button_delete_grade;
    private javax.swing.JButton button_edit_target_cetak;
    private javax.swing.JButton button_export_GradeBahanBaku;
    public javax.swing.JButton button_insert_grade;
    private javax.swing.JButton button_refresh_grade;
    private javax.swing.JButton button_ubah_status;
    public javax.swing.JButton button_update_grade;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel_grade_bahan_baku;
    private javax.swing.JPanel jPanel_operation_grade;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_grade_bentuk;
    private javax.swing.JLabel label_grade_bulu;
    private javax.swing.JLabel label_grade_kode;
    private javax.swing.JLabel label_grade_warna;
    private javax.swing.JLabel label_grade_warna1;
    private javax.swing.JLabel label_grade_warna2;
    private javax.swing.JLabel label_grade_warna3;
    private javax.swing.JLabel label_grade_warna4;
    private javax.swing.JLabel label_total_data_gradeBaku;
    private javax.swing.JTextField txt_harga_gram_cabuto;
    private javax.swing.JTextField txt_jenis_bentuk;
    private javax.swing.JTextField txt_jenis_bulu;
    private javax.swing.JTextField txt_jenis_warna;
    private javax.swing.JTextField txt_kategori;
    private javax.swing.JTextField txt_kategori_proses;
    private javax.swing.JTextField txt_kode_grade;
    private javax.swing.JTextField txt_memo_lp;
    private javax.swing.JTextField txt_search_grade;
    // End of variables declaration//GEN-END:variables
}
