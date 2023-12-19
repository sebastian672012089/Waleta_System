package waleta_system.HRD;

import waleta_system.Class.Utility;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import waleta_system.Class.ColumnsAutoSizer;

public class JPanel_DataDepartemen extends javax.swing.JPanel {

    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date today = new Date();
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    DefaultTableModel dm = new DefaultTableModel();
    JFileChooser chooser = new JFileChooser();

    public JPanel_DataDepartemen() {
        initComponents();
    }

    public void init() {
        FillComboBox_dept();
        refreshTable_dept();
        refreshTable_bagian();
        table_bagian.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && table_bagian.getSelectedRow() != -1) {
                    int i = table_bagian.getSelectedRow();
                    if (table_bagian.getValueAt(i, 10).toString().equals("AKTIF")) {
                        button_bagian_aktifkan.setEnabled(false);
                        button_bagian_non_aktifkan.setEnabled(true);
                    } else {
                        button_bagian_aktifkan.setEnabled(true);
                        button_bagian_non_aktifkan.setEnabled(false);
                    }
                }
            }
        });
    }

    public void refreshTable_dept() {
        try {
            DefaultTableModel model = (DefaultTableModel) table_departemen.getModel();
            model.setRowCount(0);
            sql = "SELECT `kode_dep` FROM `tb_departemen`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("kode_dep")});
            }
            int rowData = table_departemen.getRowCount();
            label_jumlah_departemen.setText(Integer.toString(rowData));
            FillComboBox_dept();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_DataDepartemen.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void refreshTable_bagian() {
        try {
            DefaultTableModel model = (DefaultTableModel) table_bagian.getModel();
            model.setRowCount(0);
            String filter_status = "";
            if (ComboBox_status.getSelectedItem().toString().equals("All")) {
                filter_status = "";
            } else if (ComboBox_status.getSelectedItem().toString().equals("AKTIF")) {
                filter_status = " AND `tb_bagian`.`status_bagian` = 1";
            } else if (ComboBox_status.getSelectedItem().toString().equals("NON-AKTIF")) {
                filter_status = " AND `tb_bagian`.`status_bagian` = 0";
            }
            String filter_departemen = " AND `tb_bagian`.`kode_departemen` = '" + ComboBox_departemen.getSelectedItem().toString() + "'";
            if (ComboBox_departemen.getSelectedItem().toString().equals("All")) {
                filter_departemen = "";
            }

            sql = "SELECT `tb_bagian`.`kode_bagian`, `tb_bagian`.`nama_bagian`, `tb_bagian`.`posisi_bagian`, `tb_bagian`.`kode_departemen`, `tb_bagian`.`divisi_bagian`, `tb_bagian`.`bagian_bagian`, `tb_bagian`.`ruang_bagian`, `tb_bagian`.`grup`, `tb_bagian`.`status_bagian`, `tb_bagian`.`kepala_bagian`, ATASAN.`nama_bagian` AS 'nama_atasan' \n"
                    + "FROM `tb_bagian`\n"
                    + "LEFT JOIN `tb_bagian` ATASAN ON `tb_bagian`.`kepala_bagian` = ATASAN.`kode_bagian` "
                    + "WHERE `tb_bagian`.`nama_bagian` LIKE '%" + txt_search_bagian.getText() + "%'"
                    + filter_departemen
                    + filter_status;
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                Object[] row = new Object[15];
                row[0] = rs.getInt("kode_bagian");
                row[1] = rs.getString("nama_bagian");
                row[2] = rs.getString("posisi_bagian");
                row[3] = rs.getString("kode_departemen");
                row[4] = rs.getString("divisi_bagian");
                row[5] = rs.getString("bagian_bagian");
                row[6] = rs.getString("ruang_bagian");
                row[7] = rs.getString("grup");
                row[8] = rs.getString("kepala_bagian");
                row[9] = rs.getString("nama_atasan");
                if (rs.getInt("status_bagian") == 0) {
                    row[10] = "NON-AKTIF";
                } else if (rs.getInt("status_bagian") == 1) {
                    row[10] = "AKTIF";
                }
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_bagian);

            int rowData = table_bagian.getRowCount();
            label_jumlah_bagian.setText(Integer.toString(rowData));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_DataDepartemen.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void FillComboBox_dept() {
        try {
            ComboBox_departemen.removeAllItems();
            ComboBox_departemen.addItem("All");
            sql = "SELECT `kode_dep` FROM `tb_departemen`";
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                ComboBox_departemen.addItem(rs.getString("kode_dep"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_DataDepartemen.class.getName()).log(Level.SEVERE, null, e);
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
        jPanel2 = new javax.swing.JPanel();
        button_dept_new = new javax.swing.JButton();
        button_dept_delete = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_departemen = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        label_jumlah_departemen = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        ComboBox_departemen = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_bagian = new javax.swing.JTable();
        label_jumlah_bagian = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        button_bagian_aktifkan = new javax.swing.JButton();
        button_bagian_non_aktifkan = new javax.swing.JButton();
        button_refresh_bagian = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        txt_search_bagian = new javax.swing.JTextField();
        ComboBox_status = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        button_bagian_new = new javax.swing.JButton();
        button_bagian_edit = new javax.swing.JButton();
        button_bagian_delete = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Departemen & Bagian", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(1366, 652));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)), "Departemen", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Century Schoolbook", 1, 12), new java.awt.Color(102, 102, 102))); // NOI18N

        button_dept_new.setBackground(new java.awt.Color(255, 255, 255));
        button_dept_new.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_dept_new.setText("+Add New");
        button_dept_new.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_dept_newActionPerformed(evt);
            }
        });

        button_dept_delete.setBackground(new java.awt.Color(255, 255, 255));
        button_dept_delete.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_dept_delete.setText("Delete");
        button_dept_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_dept_deleteActionPerformed(evt);
            }
        });

        table_departemen.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Departemen"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_departemen.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(table_departemen);
        if (table_departemen.getColumnModel().getColumnCount() > 0) {
            table_departemen.getColumnModel().getColumn(0).setResizable(false);
        }

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Jumlah Departemen :");

        label_jumlah_departemen.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_departemen.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_jumlah_departemen.setText("0");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_jumlah_departemen))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(button_dept_new)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_dept_delete)))
                        .addGap(0, 139, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_jumlah_departemen, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_dept_new, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_dept_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2)
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)), "Bagian", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Century Schoolbook", 1, 12), new java.awt.Color(102, 102, 102))); // NOI18N

        ComboBox_departemen.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        ComboBox_departemen.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("Departemen :");

        table_bagian.setAutoCreateRowSorter(true);
        table_bagian.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Bagian", "Nama Bagian", "Posisi", "Departemen", "Divisi", "Bagian", "Ruang", "Grup", "Atasan", "Atasan", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_bagian.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table_bagian);
        if (table_bagian.getColumnModel().getColumnCount() > 0) {
            table_bagian.getColumnModel().getColumn(0).setResizable(false);
        }

        label_jumlah_bagian.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_bagian.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        label_jumlah_bagian.setText("0");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Jumlah Bagian :");

        button_bagian_aktifkan.setBackground(new java.awt.Color(255, 255, 255));
        button_bagian_aktifkan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_bagian_aktifkan.setText("Aktifkan");
        button_bagian_aktifkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_bagian_aktifkanActionPerformed(evt);
            }
        });

        button_bagian_non_aktifkan.setBackground(new java.awt.Color(255, 255, 255));
        button_bagian_non_aktifkan.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_bagian_non_aktifkan.setText("Non-Aktifkan");
        button_bagian_non_aktifkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_bagian_non_aktifkanActionPerformed(evt);
            }
        });

        button_refresh_bagian.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_bagian.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_refresh_bagian.setText("Refresh");
        button_refresh_bagian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_bagianActionPerformed(evt);
            }
        });

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("Bagian :");

        txt_search_bagian.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txt_search_bagian.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_bagianKeyPressed(evt);
            }
        });

        ComboBox_status.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        ComboBox_status.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "AKTIF", "NON-AKTIF" }));
        ComboBox_status.setSelectedIndex(1);

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("Status :");

        button_bagian_new.setBackground(new java.awt.Color(255, 255, 255));
        button_bagian_new.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_bagian_new.setText("+Add New");
        button_bagian_new.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_bagian_newActionPerformed(evt);
            }
        });

        button_bagian_edit.setBackground(new java.awt.Color(255, 255, 255));
        button_bagian_edit.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_bagian_edit.setText("Edit");
        button_bagian_edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_bagian_editActionPerformed(evt);
            }
        });

        button_bagian_delete.setBackground(new java.awt.Color(255, 255, 255));
        button_bagian_delete.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        button_bagian_delete.setText("Delete");
        button_bagian_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_bagian_deleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(button_bagian_new)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_bagian_edit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_bagian_delete)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_departemen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboBox_status, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_refresh_bagian)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_bagian_non_aktifkan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_bagian_aktifkan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_jumlah_bagian)
                                .addGap(0, 152, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ComboBox_departemen, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_bagian_aktifkan, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_bagian_non_aktifkan, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_refresh_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_search_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_jumlah_bagian, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_status, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_bagian_new, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_bagian_edit, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_bagian_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1367, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button_dept_newActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_dept_newActionPerformed
        try {
            // TODO add your handling code here:
            Utility.db.getConnection().createStatement();
            String dept = JOptionPane.showInputDialog(jPanel1, "Masukkan Nama Departemen yang baru :");
            if (dept != null) {
                String Query = "INSERT INTO `tb_departemen`(`kode_dep`) VALUES ('" + dept + "')";
                if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                    JOptionPane.showMessageDialog(this, "Data Added!");
                    refreshTable_dept();
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(jPanel1, ex);
            Logger.getLogger(JPanel_DataDepartemen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_dept_newActionPerformed

    private void button_dept_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_dept_deleteActionPerformed
        // TODO add your handling code here:
        try {
            Utility.db.getConnection().createStatement();
            int j = table_departemen.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang ingin di hapus !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Are Sure You Want to Delete this department?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String Query = "DELETE FROM `tb_departemen` WHERE kode_dep = '" + table_departemen.getValueAt(j, 0) + "'";
                    if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                        JOptionPane.showMessageDialog(this, "Data Deleted!");
                        refreshTable_dept();
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(jPanel1, ex);
            Logger.getLogger(JPanel_DataDepartemen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_dept_deleteActionPerformed

    private void button_bagian_newActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_bagian_newActionPerformed
        // TODO add your handling code here:
        JDialog_TambahEditBagian dialog = new JDialog_TambahEditBagian(new javax.swing.JFrame(), true, null);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.setEnabled(true);
        dialog.setResizable(false);
        refreshTable_bagian();
    }//GEN-LAST:event_button_bagian_newActionPerformed

    private void button_bagian_editActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_bagian_editActionPerformed
        // TODO add your handling code here:
        int j = table_bagian.getSelectedRow();
        if (j == -1) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih bagian yang ingin di edit pada tabel !");
        } else {
            String kode_bagian = table_bagian.getValueAt(j, 0).toString();
            JDialog_TambahEditBagian dialog = new JDialog_TambahEditBagian(new javax.swing.JFrame(), true, kode_bagian);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            dialog.setEnabled(true);
            dialog.setResizable(false);
            refreshTable_bagian();
        }
    }//GEN-LAST:event_button_bagian_editActionPerformed

    private void button_bagian_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_bagian_deleteActionPerformed
        // TODO add your handling code here:
        try {
            int j = table_bagian.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Silahkan pilih data yang ingin di hapus !");
            } else {
                int dialogResult = JOptionPane.showConfirmDialog(this, "Yakin hapus data ini?", "Warning", 0);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // delete code here
                    String Query = "DELETE FROM `tb_bagian` WHERE `tb_bagian`.`kode_bagian` = '" + table_bagian.getValueAt(j, 0).toString() + "'";
                    Utility.db.getConnection().createStatement();
                    if ((Utility.db.getStatement().executeUpdate(Query)) == 1) {
                        JOptionPane.showMessageDialog(this, "data DELETED Successfully");
                    } else {
                        JOptionPane.showMessageDialog(this, "Delete FAILED");
                    }
                    refreshTable_bagian();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_DataDepartemen.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_bagian_deleteActionPerformed

    private void button_bagian_aktifkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_bagian_aktifkanActionPerformed
        // TODO add your handling code here:
        try {
            int j = table_bagian.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Pilih data yang akan di edit !");
            } else {
                sql = "UPDATE `tb_bagian` SET "
                        + "`status_bagian`=1 "
                        + "WHERE `kode_bagian`='" + table_bagian.getValueAt(j, 0).toString() + "'";
                if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                    JOptionPane.showMessageDialog(this, "Set Aktif Berhasil !");
                    refreshTable_bagian();
                } else {
                    JOptionPane.showMessageDialog(this, "update failed !");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_DataDepartemen.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_bagian_aktifkanActionPerformed

    private void button_bagian_non_aktifkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_bagian_non_aktifkanActionPerformed
        // TODO add your handling code here:
        try {
            int j = table_bagian.getSelectedRow();
            if (j == -1) {
                JOptionPane.showMessageDialog(this, "Pilih data yang akan di edit !");
            } else {
                sql = "UPDATE `tb_bagian` SET "
                        + "`status_bagian`=0 "
                        + "WHERE `kode_bagian`='" + table_bagian.getValueAt(j, 0).toString() + "'";
                if ((Utility.db.getStatement().executeUpdate(sql)) == 1) {
                    JOptionPane.showMessageDialog(this, "Set Non-Aktif Berhasil !");
                    refreshTable_bagian();
                } else {
                    JOptionPane.showMessageDialog(this, "update failed !");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e);
            Logger.getLogger(JPanel_DataDepartemen.class.getName()).log(Level.SEVERE, null, e);
        }
    }//GEN-LAST:event_button_bagian_non_aktifkanActionPerformed

    private void button_refresh_bagianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_bagianActionPerformed
        // TODO add your handling code here:
        refreshTable_bagian();
    }//GEN-LAST:event_button_refresh_bagianActionPerformed

    private void txt_search_bagianKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_bagianKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_bagian();
        }
    }//GEN-LAST:event_txt_search_bagianKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboBox_departemen;
    private javax.swing.JComboBox<String> ComboBox_status;
    private javax.swing.JButton button_bagian_aktifkan;
    private javax.swing.JButton button_bagian_delete;
    private javax.swing.JButton button_bagian_edit;
    private javax.swing.JButton button_bagian_new;
    private javax.swing.JButton button_bagian_non_aktifkan;
    private javax.swing.JButton button_dept_delete;
    private javax.swing.JButton button_dept_new;
    private javax.swing.JButton button_refresh_bagian;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel label_jumlah_bagian;
    private javax.swing.JLabel label_jumlah_departemen;
    private javax.swing.JTable table_bagian;
    private javax.swing.JTable table_departemen;
    private javax.swing.JTextField txt_search_bagian;
    // End of variables declaration//GEN-END:variables

}
