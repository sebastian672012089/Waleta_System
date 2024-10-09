package waleta_system.Manajemen;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.jfree.data.general.DefaultPieDataset;
import waleta_system.Class.Utility;

public class JFrame_StokTV extends javax.swing.JFrame {

    
    String sql = null;
    ResultSet rs;
    PreparedStatement pst;
    Date date = new Date();
    DecimalFormat decimalFormat = new DecimalFormat();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    static int loop = 0;
    static Timer t;
    TimerTask timer;
    public DefaultPieDataset dataset7;

    public JFrame_StokTV() {
        initComponents();
    }

    public void init() {
        try {
            
            
            refreshTable_StockBoxBJ();
            refreshTable_KategoriStock();
            refresh_JAM();

            timer = new TimerTask() {
                @Override
                public void run() {
                    refreshTable_StockBoxBJ();
                    refreshTable_KategoriStock();
                    refresh_JAM();
                }
            };

        } catch (Exception ex) {
            Logger.getLogger(JFrame_StokTV.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refresh_JAM() {
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");

        String formattedDate = myDateObj.format(myFormatObj);
        label_jam.setText(formattedDate);
    }

    public void refreshTable_StockBoxBJ() {
        try {
            int count = 0;
            int total_gram_NonBox = 0;
            float total_gram_StokBox = 0;
            DefaultTableModel model = (DefaultTableModel) tabel_stockBoxBJ.getModel();
            DefaultTableModel model1 = (DefaultTableModel) tabel_stockBoxBJ1.getModel();
            DefaultTableModel model2 = (DefaultTableModel) tabel_stockBoxBJ2.getModel();
            model.setRowCount(0);
            model1.setRowCount(0);
            model2.setRowCount(0);

            sql = "SELECT `tb_grade_bahan_jadi`.`kode_grade`, SUM(`tb_grading_bahan_jadi`.`gram`) AS 'gram'  "
                    + "FROM `tb_grading_bahan_jadi` "
                    + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_grading_bahan_jadi`.`kode_asal_bahan_jadi` = `tb_bahan_jadi_masuk`.`kode_asal` "
                    + "LEFT JOIN `tb_tutupan_grading` ON `tb_bahan_jadi_masuk`.`kode_tutupan` = `tb_tutupan_grading`.`kode_tutupan` "
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_grading_bahan_jadi`.`grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode` "
                    + "WHERE `tb_grade_bahan_jadi`.`kode_grade` LIKE 'GNS%' AND (`tb_tutupan_grading`.`status_box` = 'PROSES' OR `tb_tutupan_grading`.`status_box` IS NULL) AND `tanggal_grading` IS NOT NULL "
                    + "GROUP BY `tb_grade_bahan_jadi`.`kode_grade`";
//            System.out.println(sql);
            ArrayList<String> grade = new ArrayList<>();
            ArrayList<Integer> gram = new ArrayList<>();
            rs = Utility.db.getStatement().executeQuery(sql);
            while (rs.next()) {
                grade.add(rs.getString("kode_grade"));
                gram.add(rs.getInt("gram"));
//                System.out.println(rs.getString("kode_grade") + " : " + rs.getInt("gram"));
                total_gram_NonBox = total_gram_NonBox + rs.getInt("gram");
            }

            Object[] row = new Object[6];
            sql = "SELECT `tb_grade_bahan_jadi`.`kode`, `tb_grade_bahan_jadi`.`kode_grade`, SUM(`berat`) AS 'berat', `harga` "
                    + "FROM `tb_grade_bahan_jadi` "
                    + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_grade_bahan_jadi`.`kode` = `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` "
                    + "LEFT JOIN `tb_tutupan_grading` ON `tb_box_bahan_jadi`.`no_tutupan` = `tb_tutupan_grading`.`kode_tutupan`"
                    + "WHERE `lokasi_terakhir` = 'GRADING' AND `tb_grade_bahan_jadi`.`kode_grade` LIKE 'GNS%' AND (`tb_tutupan_grading`.`status_box` = 'SELESAI' OR `tb_tutupan_grading`.`status_box` IS NULL)"
                    + "GROUP BY `tb_grade_bahan_jadi`.`kode` ORDER BY `harga` DESC";
            pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            int i = 0;
            while (rs.next()) {
                i++;
                row[0] = rs.getString("kode_grade").substring(4);
                int gram_StokBox = (int) (rs.getInt("berat") / 1000);
                int index = grade.indexOf(rs.getString("kode_grade"));
                if (index > 0) {
                    row[1] = (int) (gram.get(index) / 1000);
                } else {
                    row[1] = 0;
                }
                row[2] = gram_StokBox;
                total_gram_StokBox = total_gram_StokBox + rs.getFloat("berat");

                if ((int) row[1] > 0 || (int) row[2] > 0) {
                    count++;
                    if (count > 54) {
                        model2.addRow(row);
                    } else if (count > 27) {
                        model1.addRow(row);
                    } else {
                        model.addRow(row);
                    }
                }

//                sql = "SELECT SUM(`tb_grading_bahan_jadi`.`gram`) AS 'gram' "
//                        + "FROM `tb_grading_bahan_jadi` "
//                        + "LEFT JOIN `tb_bahan_jadi_masuk` ON `tb_grading_bahan_jadi`.`kode_asal_bahan_jadi` = `tb_bahan_jadi_masuk`.`kode_asal` "
//                        + "LEFT JOIN `tb_tutupan_grading` ON `tb_bahan_jadi_masuk`.`kode_tutupan` = `tb_tutupan_grading`.`kode_tutupan` "
//                        + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_grading_bahan_jadi`.`grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode` "
//                        + "WHERE (`tb_tutupan_grading`.`status_box` = 'PROSES' OR `tb_tutupan_grading`.`status_box` IS NULL) AND `tanggal_grading` IS NOT NULL "
//                        + "AND `grade_bahan_jadi` = '" + rs.getString("kode") + "'";
//                ResultSet rs1 = Utility.db.getStatement().executeQuery(sql);
//                if (rs1.next()) {
//                    int gram_NonBox = (int) (rs1.getInt("gram") / 1000);
//                    row[1] = gram_NonBox;
//                    total_gram_NonBox = total_gram_NonBox + rs1.getInt("gram");
//                }
            }
            System.out.println(total_gram_StokBox);
            tabel_stockBoxBJ.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (row % 2 == 1) {
                        // ganjil
                        comp.setBackground(new Color(230, 230, 230));
                    } else {
                        // genap
                        comp.setBackground(tabel_KategoriStock.getBackground());
                    }
                    return comp;
                }
            });
            tabel_stockBoxBJ.repaint();
            tabel_stockBoxBJ.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (row % 2 == 1) {
                        // ganjil
                        comp.setBackground(new Color(230, 230, 230));
                    } else {
                        // genap
                        comp.setBackground(tabel_KategoriStock.getBackground());
                    }
                    return comp;
                }
            });
            tabel_stockBoxBJ.repaint();

            tabel_stockBoxBJ1.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (row % 2 == 1) {
                        // ganjil
                        comp.setBackground(new Color(230, 230, 230));
                    } else {
                        // genap
                        comp.setBackground(tabel_KategoriStock.getBackground());
                    }
                    return comp;
                }
            });
            tabel_stockBoxBJ1.repaint();
            tabel_stockBoxBJ1.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (row % 2 == 1) {
                        // ganjil
                        comp.setBackground(new Color(230, 230, 230));
                    } else {
                        // genap
                        comp.setBackground(tabel_KategoriStock.getBackground());
                    }
                    return comp;
                }
            });
            tabel_stockBoxBJ1.repaint();

            tabel_stockBoxBJ2.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (row % 2 == 1) {
                        // ganjil
                        comp.setBackground(new Color(230, 230, 230));
                    } else {
                        // genap
                        comp.setBackground(tabel_KategoriStock.getBackground());
                    }
                    return comp;
                }
            });
            tabel_stockBoxBJ2.repaint();
            tabel_stockBoxBJ2.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (row % 2 == 1) {
                        // ganjil
                        comp.setBackground(new Color(230, 230, 230));
                    } else {
                        // genap
                        comp.setBackground(tabel_KategoriStock.getBackground());
                    }
                    return comp;
                }
            });
            tabel_stockBoxBJ2.repaint();

//            ColumnsAutoSizer.sizeColumnsToFit(tabel_stockBoxBJ);
//            ColumnsAutoSizer.sizeColumnsToFit(tabel_stockBoxBJ1);
//            ColumnsAutoSizer.sizeColumnsToFit(tabel_stockBoxBJ2);
            decimalFormat.setMaximumFractionDigits(0);
            label_total_gram_NonBox.setText(decimalFormat.format(total_gram_NonBox / 1000.f) + " Kg");
            label_total_gram_StokBox.setText(decimalFormat.format(total_gram_StokBox / 1000.f) + " Kg");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JFrame_StokTV.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_KategoriStock() {
        try {
            int total_gram_StokBox = 0;
            DefaultTableModel model = (DefaultTableModel) tabel_KategoriStock.getModel();
            model.setRowCount(0);
            Object[] row = new Object[2];

            sql = "SELECT `tb_grade_bahan_jadi`.`Kategori1`, SUM(`berat`) AS 'berat' FROM `tb_grade_bahan_jadi` "
                    + "LEFT JOIN `tb_box_bahan_jadi` ON `tb_grade_bahan_jadi`.`kode` = `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` "
                    + "LEFT JOIN `tb_reproses` ON `tb_reproses`.`no_box` = `tb_box_bahan_jadi`.`no_box` "
                    + "WHERE `lokasi_terakhir` = 'GRADING'  AND (`tb_grade_bahan_jadi`.`kode_grade` LIKE 'GNS%' OR `tb_grade_bahan_jadi`.`kode_grade` = 'NON NS SSK') AND `tb_reproses`.`no_box` IS NULL "
                    + "GROUP BY `tb_grade_bahan_jadi`.`Kategori1` ORDER BY `berat` DESC";
            pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                row[0] = rs.getString("Kategori1");
                total_gram_StokBox = total_gram_StokBox + rs.getInt("berat");
                int gram_StokBox = (int) (rs.getInt("berat") / 1000);
                row[1] = gram_StokBox;
                model.addRow(row);
            }

            tabel_KategoriStock.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (row < 2) {
                        comp.setFont(new Font("Arial", Font.BOLD, 40));
                    }
                    if ((int) tabel_KategoriStock.getValueAt(row, 1) > 100) {
                        if (isSelected) {
                            comp.setBackground(tabel_KategoriStock.getSelectionBackground());
                            comp.setForeground(tabel_KategoriStock.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.red);
                            comp.setForeground(Color.WHITE);
                        }
                    } else if ((int) tabel_KategoriStock.getValueAt(row, 1) > 50) {
                        if (isSelected) {
                            comp.setBackground(tabel_KategoriStock.getSelectionBackground());
                            comp.setForeground(tabel_KategoriStock.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.YELLOW);
                            comp.setForeground(tabel_KategoriStock.getForeground());
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(tabel_KategoriStock.getSelectionBackground());
                            comp.setForeground(tabel_KategoriStock.getSelectionForeground());
                        } else {
                            comp.setBackground(tabel_KategoriStock.getBackground());
                            comp.setForeground(tabel_KategoriStock.getForeground());
                        }
                    }
                    return comp;
                }
            });
            tabel_KategoriStock.repaint();
            tabel_KategoriStock.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (row < 2) {
                        comp.setFont(new Font("Arial", Font.BOLD, 44));
                    }
                    if ((int) tabel_KategoriStock.getValueAt(row, 1) > 100) {
                        if (isSelected) {
                            comp.setBackground(tabel_KategoriStock.getSelectionBackground());
                            comp.setForeground(tabel_KategoriStock.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.red);
                            comp.setForeground(Color.WHITE);
                        }
                    } else if ((int) tabel_KategoriStock.getValueAt(row, 1) > 50) {
                        if (isSelected) {
                            comp.setBackground(tabel_KategoriStock.getSelectionBackground());
                            comp.setForeground(tabel_KategoriStock.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.YELLOW);
                            comp.setForeground(tabel_KategoriStock.getForeground());
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(tabel_KategoriStock.getSelectionBackground());
                            comp.setForeground(tabel_KategoriStock.getSelectionForeground());
                        } else {
                            comp.setBackground(tabel_KategoriStock.getBackground());
                            comp.setForeground(tabel_KategoriStock.getForeground());
                        }
                    }
                    return comp;
                }
            });
            tabel_KategoriStock.repaint();

            refreshTable_StockRepacking();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JFrame_StokTV.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_StockRepacking() {
        try {
            int total_gram_StokBox = 0;
            DefaultTableModel model = (DefaultTableModel) tabel_stockRepacking.getModel();

            model.setRowCount(0);
            Object[] row = new Object[2];
            sql = "SELECT `kategori1`, SUM(`tb_box_bahan_jadi`.`berat`) AS 'berat' "
                    + "FROM `tb_box_bahan_jadi` "
                    + "LEFT JOIN `tb_reproses` ON `tb_reproses`.`no_box` = `tb_box_bahan_jadi`.`no_box`\n"
                    + "LEFT JOIN `tb_grade_bahan_jadi` ON `tb_box_bahan_jadi`.`kode_grade_bahan_jadi` = `tb_grade_bahan_jadi`.`kode`"
                    + "WHERE `tb_reproses`.`status` = 'FINISHED' AND `lokasi_terakhir` = 'GRADING' "
                    + "GROUP BY `Kategori1` ORDER BY `berat` DESC";
            pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                row[0] = rs.getString("Kategori1");
                row[1] = rs.getInt("berat");
                total_gram_StokBox = total_gram_StokBox + rs.getInt("berat");
                model.addRow(row);
            }

            label_total_stok_repacking.setText(decimalFormat.format(total_gram_StokBox));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            Logger.getLogger(JFrame_StokTV.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_stockBoxBJ = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        label_total_gram_NonBox = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        label_total_gram_StokBox = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel_stockBoxBJ1 = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabel_stockBoxBJ2 = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tabel_KategoriStock = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        tabel_stockRepacking = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        label_total_stok_repacking = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        Button_Close = new javax.swing.JButton();
        Button_Refresh = new javax.swing.JButton();
        label_jam = new javax.swing.JLabel();
        ToggleButton_start = new javax.swing.JToggleButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        tabel_stockBoxBJ.setAutoCreateRowSorter(true);
        tabel_stockBoxBJ.setFont(new java.awt.Font("Arial", 1, 22)); // NOI18N
        tabel_stockBoxBJ.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade", "w/o Box", "Box"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_stockBoxBJ.setRequestFocusEnabled(false);
        tabel_stockBoxBJ.setRowHeight(24);
        tabel_stockBoxBJ.setRowSelectionAllowed(false);
        tabel_stockBoxBJ.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tabel_stockBoxBJ);
        if (tabel_stockBoxBJ.getColumnModel().getColumnCount() > 0) {
            tabel_stockBoxBJ.getColumnModel().getColumn(0).setMinWidth(170);
        }

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        jLabel4.setText("Total Non Box :");

        label_total_gram_NonBox.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_NonBox.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        label_total_gram_NonBox.setText("0");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        jLabel5.setText("Total Stok Box :");

        label_total_gram_StokBox.setBackground(new java.awt.Color(255, 255, 255));
        label_total_gram_StokBox.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        label_total_gram_StokBox.setText("0");

        tabel_stockBoxBJ1.setAutoCreateRowSorter(true);
        tabel_stockBoxBJ1.setFont(new java.awt.Font("Arial", 1, 22)); // NOI18N
        tabel_stockBoxBJ1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade", "w/o Box", "Box"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_stockBoxBJ1.setRequestFocusEnabled(false);
        tabel_stockBoxBJ1.setRowHeight(24);
        tabel_stockBoxBJ1.setRowSelectionAllowed(false);
        tabel_stockBoxBJ1.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tabel_stockBoxBJ1);
        if (tabel_stockBoxBJ1.getColumnModel().getColumnCount() > 0) {
            tabel_stockBoxBJ1.getColumnModel().getColumn(0).setMinWidth(170);
        }

        tabel_stockBoxBJ2.setAutoCreateRowSorter(true);
        tabel_stockBoxBJ2.setFont(new java.awt.Font("Arial", 1, 22)); // NOI18N
        tabel_stockBoxBJ2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade", "w/o Box", "Box"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_stockBoxBJ2.setRequestFocusEnabled(false);
        tabel_stockBoxBJ2.setRowHeight(24);
        tabel_stockBoxBJ2.setRowSelectionAllowed(false);
        tabel_stockBoxBJ2.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(tabel_stockBoxBJ2);
        if (tabel_stockBoxBJ2.getColumnModel().getColumnCount() > 0) {
            tabel_stockBoxBJ2.getColumnModel().getColumn(0).setMinWidth(170);
        }

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        jLabel3.setText("STOK BARANG JADI WALETA");

        tabel_KategoriStock.setAutoCreateRowSorter(true);
        tabel_KategoriStock.setFont(new java.awt.Font("Arial", 1, 30)); // NOI18N
        tabel_KategoriStock.setForeground(new java.awt.Color(165, 42, 42));
        tabel_KategoriStock.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kategori", "Stok"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_KategoriStock.setRequestFocusEnabled(false);
        tabel_KategoriStock.setRowHeight(39);
        tabel_KategoriStock.setRowSelectionAllowed(false);
        tabel_KategoriStock.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(tabel_KategoriStock);
        if (tabel_KategoriStock.getColumnModel().getColumnCount() > 0) {
            tabel_KategoriStock.getColumnModel().getColumn(0).setMinWidth(250);
        }

        tabel_stockRepacking.setAutoCreateRowSorter(true);
        tabel_stockRepacking.setFont(new java.awt.Font("Arial", 1, 22)); // NOI18N
        tabel_stockRepacking.setForeground(new java.awt.Color(153, 0, 153));
        tabel_stockRepacking.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kategori", "Berat"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_stockRepacking.setRequestFocusEnabled(false);
        tabel_stockRepacking.setRowHeight(24);
        tabel_stockRepacking.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(tabel_stockRepacking);
        if (tabel_stockRepacking.getColumnModel().getColumnCount() > 0) {
            tabel_stockRepacking.getColumnModel().getColumn(0).setMinWidth(170);
        }

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(153, 0, 153));
        jLabel6.setText("Box Selesai Reproses,");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(153, 0, 153));
        jLabel7.setText("Total :");

        label_total_stok_repacking.setBackground(new java.awt.Color(255, 255, 255));
        label_total_stok_repacking.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        label_total_stok_repacking.setForeground(new java.awt.Color(153, 0, 153));
        label_total_stok_repacking.setText("0");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(153, 0, 153));
        jLabel8.setText("Gram");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(153, 0, 153));
        jLabel9.setText("Belum Repacking / Regrading");

        Button_Close.setBackground(new java.awt.Color(255, 255, 255));
        Button_Close.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        Button_Close.setText("CLOSE");
        Button_Close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_CloseActionPerformed(evt);
            }
        });

        Button_Refresh.setBackground(new java.awt.Color(255, 255, 255));
        Button_Refresh.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        Button_Refresh.setText("REFRESH");
        Button_Refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_RefreshActionPerformed(evt);
            }
        });

        label_jam.setBackground(new java.awt.Color(255, 255, 255));
        label_jam.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_jam.setText("Jam :");

        ToggleButton_start.setBackground(new java.awt.Color(255, 255, 255));
        ToggleButton_start.setText("START");
        ToggleButton_start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ToggleButton_startActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_NonBox)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_gram_StokBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ToggleButton_start)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_jam)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Button_Refresh)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Button_Close))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_stok_repacking)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel8))
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(label_total_gram_StokBox)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_jam, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ToggleButton_start, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(Button_Refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Button_Close, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(label_total_gram_NonBox)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(label_total_stok_repacking)
                            .addComponent(jLabel8))))
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

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        t.cancel();
    }//GEN-LAST:event_formWindowClosing

    private void Button_CloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_CloseActionPerformed
        // TODO add your handling code here:
        this.dispose();
        t.cancel();
    }//GEN-LAST:event_Button_CloseActionPerformed

    private void Button_RefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_RefreshActionPerformed
        // TODO add your handling code here:
        refreshTable_StockBoxBJ();
        refreshTable_KategoriStock();
        refresh_JAM();
    }//GEN-LAST:event_Button_RefreshActionPerformed

    private void ToggleButton_startActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ToggleButton_startActionPerformed
        // TODO add your handling code here:
        if (ToggleButton_start.isSelected()) {
            ToggleButton_start.setText("STOP");
            Button_Refresh.setEnabled(false);
            t = new Timer();
            t.schedule(timer, 1000, 60000);
        } else {
            ToggleButton_start.setText("START");
            Button_Refresh.setEnabled(true);
            t.cancel();
        }
    }//GEN-LAST:event_ToggleButton_startActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrame_StokTV.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame_StokTV Stok = new JFrame_StokTV();
                Stok.setVisible(true);
                Stok.setLocationRelativeTo(null);
                Stok.setExtendedState(MAXIMIZED_BOTH);
                Stok.init();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Button_Close;
    private javax.swing.JButton Button_Refresh;
    private javax.swing.JToggleButton ToggleButton_start;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JLabel label_jam;
    private javax.swing.JLabel label_total_gram_NonBox;
    private javax.swing.JLabel label_total_gram_StokBox;
    private javax.swing.JLabel label_total_stok_repacking;
    private javax.swing.JTable tabel_KategoriStock;
    private javax.swing.JTable tabel_stockBoxBJ;
    private javax.swing.JTable tabel_stockBoxBJ1;
    private javax.swing.JTable tabel_stockBoxBJ2;
    private javax.swing.JTable tabel_stockRepacking;
    // End of variables declaration//GEN-END:variables
}
