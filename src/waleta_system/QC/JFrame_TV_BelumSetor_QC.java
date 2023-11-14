package waleta_system.QC;

import java.awt.Color;
import java.awt.Component;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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

public class JFrame_TV_BelumSetor_QC extends javax.swing.JFrame {

    String sql = null, sql_rekap = null;
    ResultSet rs;
    PreparedStatement pst;
    Date today = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    static Timer t;

    public JFrame_TV_BelumSetor_QC() {
        initComponents();
        init();
    }

    public void init() {
        try {
            TimerTask timer;
            timer = new TimerTask() {
                @Override
                public void run() {
                    refreshTabel_lp_belumSetor();
                }
            };
            t = new Timer();
            t.schedule(timer, 100, 60000);
        } catch (Exception ex) {
            Logger.getLogger(JFrame_TV_QC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTabel_lp_belumSetor() {
        try {
            DefaultTableModel model_belumSampling = (DefaultTableModel) Table_LP_belumSampling.getModel();
            model_belumSampling.setRowCount(0);
            DefaultTableModel model_belumUji = (DefaultTableModel) Table_LP_belumUji.getModel();
            model_belumUji.setRowCount(0);
            DefaultTableModel model_uji_PASSED = (DefaultTableModel) Table_LP_uji_PASSED.getModel();
            model_uji_PASSED.setRowCount(0);
            DefaultTableModel model_uji_HOLD = (DefaultTableModel) Table_LP_uji_HOLD.getModel();
            model_uji_HOLD.setRowCount(0);
            sql = "SELECT `tb_lab_laporan_produksi`.`no_laporan_produksi`, `kode_grade`, `jumlah_keping`, `memo_lp`, DATEDIFF(CURRENT_DATE(), `tgl_masuk`) AS 'Result', \n"
                    + "`tgl_sampling`, `tgl_uji`, `status`, `tb_lab_laporan_produksi`.`keterangan` \n"
                    + "FROM `tb_lab_laporan_produksi` \n"
                    + "LEFT JOIN `tb_laporan_produksi` ON `tb_lab_laporan_produksi`.`no_laporan_produksi` = `tb_laporan_produksi`.`no_laporan_produksi` \n"
                    + "WHERE `tgl_selesai` IS NULL "
                    + "ORDER BY `Result`";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[10];
            int awas_merah = 0, siaga_kuning = 0, normal_biru = 0;
            int nomor1 = 0, nomor2 = 0, nomor3 = 0, nomor4 = 0;
            while (rs.next()) {
                row[1] = rs.getString("no_laporan_produksi");
                row[2] = rs.getString("kode_grade");
                row[3] = rs.getInt("jumlah_keping");
                row[4] = rs.getInt("Result");
                row[5] = rs.getString("keterangan");
                String status = "";
                if (rs.getDate("tgl_sampling") == null) {
                    status = "Belum Sampling QC";
                    nomor1++;
                    row[0] = nomor1;
                    model_belumSampling.addRow(row);
                } else if (rs.getDate("tgl_uji") == null) {
                    status = "Belum Uji";
                    nomor2++;
                    row[0] = nomor2;
                    model_belumUji.addRow(row);
                } else if (rs.getDate("tgl_uji") != null && rs.getString("status").equals("PASSED")) {
                    status = "Sudah Uji PASSED";
                    nomor3++;
                    row[0] = nomor3;
                    model_uji_PASSED.addRow(row);
                } else if (rs.getDate("tgl_uji") != null && rs.getString("status").equals("HOLD/NON GNS")) {
                    status = "Sudah Uji HOLD/NON GNS";
                    nomor4++;
                    row[0] = nomor4;
                    model_uji_HOLD.addRow(row);
                }
                if (rs.getInt("Result") > 2) {
                    awas_merah++;
                } else if (rs.getInt("Result") == 2) {
                    siaga_kuning++;
                } else {
                    normal_biru++;
                }
            }
            ColumnsAutoSizer.sizeColumnsToFit(Table_LP_belumSampling);
            ColumnsAutoSizer.sizeColumnsToFit(Table_LP_belumUji);
            ColumnsAutoSizer.sizeColumnsToFit(Table_LP_uji_PASSED);
            ColumnsAutoSizer.sizeColumnsToFit(Table_LP_uji_HOLD);

            repaint_tabel(Table_LP_belumSampling);
            repaint_tabel(Table_LP_belumUji);
            repaint_tabel(Table_LP_uji_PASSED);
            repaint_tabel(Table_LP_uji_HOLD);

            label_lp_belumSetor_merah.setText(Integer.toString(awas_merah));
            label_lp_belumSetor_kuning.setText(Integer.toString(siaga_kuning));
            label_lp_belumSetor_biru.setText(Integer.toString(normal_biru));

            label_jumlah_LP_belumSampling.setText(decimalFormat.format(Table_LP_belumSampling.getRowCount()));
            label_jumlah_LP_belumUji.setText(decimalFormat.format(Table_LP_belumUji.getRowCount()));
            label_jumlah_LP_uji_PASSED.setText(decimalFormat.format(Table_LP_uji_PASSED.getRowCount()));
            label_jumlah_LP_uji_HOLD.setText(decimalFormat.format(Table_LP_uji_HOLD.getRowCount()));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JFrame_TV_QC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void repaint_tabel(JTable table) {
        table.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if ((int) table.getValueAt(row, 4) > 2) {
                    if (isSelected) {
                        comp.setBackground(table.getSelectionBackground());
                        comp.setForeground(table.getSelectionForeground());
                    } else {
                        comp.setBackground(Color.red);
                        comp.setForeground(Color.WHITE);
                    }
                } else if ((int) table.getValueAt(row, 4) == 2) {
                    if (isSelected) {
                        comp.setBackground(table.getSelectionBackground());
                        comp.setForeground(table.getSelectionForeground());
                    } else {
                        comp.setBackground(Color.yellow);
                        comp.setForeground(Color.BLACK);
                    }
                } else {
                    if (isSelected) {
                        comp.setBackground(table.getSelectionBackground());
                        comp.setForeground(table.getSelectionForeground());
                    } else {
                        comp.setBackground(Color.cyan);
                        comp.setForeground(Color.BLACK);
                    }
                }
                return comp;
            }
        });
        table.repaint();
        table.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if ((int) table.getValueAt(row, 4) > 2) {
                    if (isSelected) {
                        comp.setBackground(table.getSelectionBackground());
                        comp.setForeground(table.getSelectionForeground());
                    } else {
                        comp.setBackground(Color.red);
                        comp.setForeground(Color.WHITE);
                    }
                } else if ((int) table.getValueAt(row, 4) == 2) {
                    if (isSelected) {
                        comp.setBackground(table.getSelectionBackground());
                        comp.setForeground(table.getSelectionForeground());
                    } else {
                        comp.setBackground(Color.yellow);
                        comp.setForeground(Color.BLACK);
                    }
                } else {
                    if (isSelected) {
                        comp.setBackground(table.getSelectionBackground());
                        comp.setForeground(table.getSelectionForeground());
                    } else {
                        comp.setBackground(Color.cyan);
                        comp.setForeground(Color.BLACK);
                    }
                }
                return comp;
            }
        });
        table.repaint();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel_Setoran_QC = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        label_lp_belumSetor_kuning = new javax.swing.JLabel();
        label_lp_belumSetor_biru = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        label_lp_belumSetor_merah = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        Table_LP_belumSampling = new javax.swing.JTable();
        label_jumlah_LP_belumSampling = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        Table_LP_belumUji = new javax.swing.JTable();
        label_jumlah_LP_belumUji = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        Table_LP_uji_HOLD = new javax.swing.JTable();
        label_jumlah_LP_uji_HOLD = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        Table_LP_uji_PASSED = new javax.swing.JTable();
        label_jumlah_LP_uji_PASSED = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel_Setoran_QC.setBackground(new java.awt.Color(255, 255, 255));

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel21.setText("/");

        label_lp_belumSetor_kuning.setBackground(new java.awt.Color(255, 255, 255));
        label_lp_belumSetor_kuning.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        label_lp_belumSetor_kuning.setForeground(new java.awt.Color(255, 153, 0));
        label_lp_belumSetor_kuning.setText("0");

        label_lp_belumSetor_biru.setBackground(new java.awt.Color(255, 255, 255));
        label_lp_belumSetor_biru.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        label_lp_belumSetor_biru.setForeground(new java.awt.Color(0, 0, 255));
        label_lp_belumSetor_biru.setText("0");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel9.setText("LP Belum Setor");

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel23.setText("/");

        label_lp_belumSetor_merah.setBackground(new java.awt.Color(255, 255, 255));
        label_lp_belumSetor_merah.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        label_lp_belumSetor_merah.setForeground(new java.awt.Color(255, 0, 0));
        label_lp_belumSetor_merah.setText("0");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Belum Sampling QC", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        Table_LP_belumSampling.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        Table_LP_belumSampling.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "No LP", "Grade", "Kpg", "Hari", "Ket."
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class
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
        Table_LP_belumSampling.setRowHeight(28);
        Table_LP_belumSampling.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(Table_LP_belumSampling);

        label_jumlah_LP_belumSampling.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_LP_belumSampling.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        label_jumlah_LP_belumSampling.setForeground(new java.awt.Color(255, 0, 0));
        label_jumlah_LP_belumSampling.setText("0");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(label_jumlah_LP_belumSampling)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(label_jumlah_LP_belumSampling)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Sudah Sampling, Belum Uji", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        Table_LP_belumUji.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        Table_LP_belumUji.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "No LP", "Grade", "Kpg", "Hari", "Ket."
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class
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
        Table_LP_belumUji.setRowHeight(28);
        Table_LP_belumUji.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(Table_LP_belumUji);

        label_jumlah_LP_belumUji.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_LP_belumUji.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        label_jumlah_LP_belumUji.setForeground(new java.awt.Color(255, 0, 0));
        label_jumlah_LP_belumUji.setText("0");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(label_jumlah_LP_belumUji)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(label_jumlah_LP_belumUji)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "QC HOLD", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        Table_LP_uji_HOLD.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        Table_LP_uji_HOLD.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "No LP", "Grade", "Kpg", "Hari", "Ket."
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class
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
        Table_LP_uji_HOLD.setRowHeight(28);
        Table_LP_uji_HOLD.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(Table_LP_uji_HOLD);

        label_jumlah_LP_uji_HOLD.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_LP_uji_HOLD.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        label_jumlah_LP_uji_HOLD.setForeground(new java.awt.Color(255, 0, 0));
        label_jumlah_LP_uji_HOLD.setText("0");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(label_jumlah_LP_uji_HOLD)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addComponent(label_jumlah_LP_uji_HOLD)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "QC PASSED", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 14))); // NOI18N

        Table_LP_uji_PASSED.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        Table_LP_uji_PASSED.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "No LP", "Grade", "Kpg", "Hari", "Keterangan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class
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
        Table_LP_uji_PASSED.setRowHeight(28);
        Table_LP_uji_PASSED.getTableHeader().setReorderingAllowed(false);
        jScrollPane7.setViewportView(Table_LP_uji_PASSED);

        label_jumlah_LP_uji_PASSED.setBackground(new java.awt.Color(255, 255, 255));
        label_jumlah_LP_uji_PASSED.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        label_jumlah_LP_uji_PASSED.setForeground(new java.awt.Color(255, 0, 0));
        label_jumlah_LP_uji_PASSED.setText("0");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane7)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(label_jumlah_LP_uji_PASSED)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addComponent(label_jumlah_LP_uji_PASSED)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel_Setoran_QCLayout = new javax.swing.GroupLayout(jPanel_Setoran_QC);
        jPanel_Setoran_QC.setLayout(jPanel_Setoran_QCLayout);
        jPanel_Setoran_QCLayout.setHorizontalGroup(
            jPanel_Setoran_QCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Setoran_QCLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 1083, Short.MAX_VALUE)
                .addComponent(label_lp_belumSetor_merah)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_lp_belumSetor_kuning)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_lp_belumSetor_biru)
                .addGap(11, 11, 11))
            .addGroup(jPanel_Setoran_QCLayout.createSequentialGroup()
                .addGroup(jPanel_Setoran_QCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Setoran_QCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel_Setoran_QCLayout.setVerticalGroup(
            jPanel_Setoran_QCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Setoran_QCLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel_Setoran_QCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(label_lp_belumSetor_merah)
                    .addComponent(jLabel21)
                    .addComponent(label_lp_belumSetor_kuning)
                    .addComponent(jLabel23)
                    .addComponent(label_lp_belumSetor_biru))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Setoran_QCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Setoran_QCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jPanel_Setoran_QC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(3, 3, 3))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel_Setoran_QC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        t.cancel();
    }//GEN-LAST:event_formWindowClosing

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JFrame_TV_QC.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrame_TV_QC.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrame_TV_QC.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame_TV_QC.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFrame_TV_BelumSetor_QC frame = new JFrame_TV_BelumSetor_QC();
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                frame.setEnabled(true);
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable Table_LP_belumSampling;
    private javax.swing.JTable Table_LP_belumUji;
    private javax.swing.JTable Table_LP_uji_HOLD;
    private javax.swing.JTable Table_LP_uji_PASSED;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel_Setoran_QC;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JLabel label_jumlah_LP_belumSampling;
    private javax.swing.JLabel label_jumlah_LP_belumUji;
    private javax.swing.JLabel label_jumlah_LP_uji_HOLD;
    private javax.swing.JLabel label_jumlah_LP_uji_PASSED;
    private javax.swing.JLabel label_lp_belumSetor_biru;
    private javax.swing.JLabel label_lp_belumSetor_kuning;
    private javax.swing.JLabel label_lp_belumSetor_merah;
    // End of variables declaration//GEN-END:variables
}
